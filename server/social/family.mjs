/**
 * Family System Server Routes & Handlers
 *
 * Implements server-authoritative Family operations:
 * - Family creation, discovery, joining, and leaving
 * - Role permission validation (Leader, Deputy, Admin, Member)
 * - Treasury donation ledger & quest claims
 * - Weekly chests & daily free rewards
 * - Family member moderation
 */

export function registerFamilyRoutes(app, db) {
  // 1. List or search families
  app.get('/api/families', async (req, res) => {
    try {
      const { query = '', limit = 20 } = req.query;
      let sql = 'SELECT * FROM families WHERE 1=1';
      const params = [];

      if (query.trim()) {
        sql += ' AND (name ILIKE $1 OR tag ILIKE $1)';
        params.push(`%${query.trim()}%`);
      }

      sql += ' ORDER BY weekly_activeness DESC LIMIT $' + (params.length + 1);
      params.push(Math.min(parseInt(limit, 10) || 20, 50));

      const { rows } = await db.query(sql, params);
      res.json({ ok: true, families: rows });
    } catch (err) {
      res.status(500).json({ ok: false, error: err.message });
    }
  });

  // 2. Get single family details
  app.get('/api/families/:id', async (req, res) => {
    try {
      const { id } = req.params;
      const { rows } = await db.query('SELECT * FROM families WHERE id = $1', [id]);
      if (!rows.length) return res.status(404).json({ ok: false, error: 'Family not found' });
      res.json({ ok: true, family: rows[0] });
    } catch (err) {
      res.status(500).json({ ok: false, error: err.message });
    }
  });

  // 3. Create family
  app.post('/api/families', async (req, res) => {
    try {
      const { name, tag, sign, description, userId, userName } = req.body;
      if (!name || !tag || !userId) {
        return res.status(400).json({ ok: false, error: 'Missing required parameters' });
      }

      const client = await db.connect();
      try {
        await client.query('BEGIN');

        // Check if user is already in a family
        const existing = await client.query('SELECT family_id FROM family_members WHERE user_id = $1', [userId]);
        if (existing.rows.length) {
          await client.query('ROLLBACK');
          return res.status(400).json({ ok: false, error: 'User is already in a family' });
        }

        const familyId = `FAM-${Math.floor(1000 + Math.random() * 9000)}`;
        const now = Date.now();

        await client.query(
          `INSERT INTO families (id, name, tag, sign, description, leader_id, leader_name, created_at)
           VALUES ($1, $2, $3, $4, $5, $6, $7, $8)`,
          [familyId, name.trim(), tag.trim().toUpperCase(), sign || '', description || '', userId, userName, now]
        );

        await client.query(
          `INSERT INTO family_members (family_id, user_id, name, role, joined_at)
           VALUES ($1, $2, $3, 'LEADER', $4)`,
          [familyId, userId, userName, now]
        );

        await client.query('COMMIT');
        res.json({ ok: true, familyId, message: 'Family created successfully' });
      } catch (e) {
        await client.query('ROLLBACK');
        throw e;
      } finally {
        client.release();
      }
    } catch (err) {
      res.status(500).json({ ok: false, error: err.message });
    }
  });

  // 4. Donate to Family Treasury
  app.post('/api/families/:id/donate', async (req, res) => {
    try {
      const { id } = req.params;
      const { userId, userName, coinAmount } = req.body;
      const amount = parseInt(coinAmount, 10);

      if (!amount || amount <= 0) return res.status(400).json({ ok: false, error: 'Invalid donation amount' });

      const client = await db.connect();
      try {
        await client.query('BEGIN');

        // Verify member belongs to family
        const memberCheck = await client.query(
          'SELECT role FROM family_members WHERE family_id = $1 AND user_id = $2',
          [id, userId]
        );
        if (!memberCheck.rows.length) {
          await client.query('ROLLBACK');
          return res.status(403).json({ ok: false, error: 'Not a member of this family' });
        }

        // Check user wallet
        const wallet = await client.query('SELECT balance FROM wallets WHERE user_id = $1', [userId]);
        if (!wallet.rows.length || wallet.rows[0].balance < amount) {
          await client.query('ROLLBACK');
          return res.status(400).json({ ok: false, error: 'Insufficient wallet balance' });
        }

        // Deduct user wallet
        await client.query('UPDATE wallets SET balance = balance - $1 WHERE user_id = $2', [amount, userId]);

        // Credit family funds and activeness
        const activenessGain = Math.floor(amount / 2);
        await client.query(
          `UPDATE families 
           SET funds = funds + $1, weekly_activeness = weekly_activeness + $2 
           WHERE id = $3`,
          [amount, activenessGain, id]
        );

        // Update member contribution
        await client.query(
          `UPDATE family_members 
           SET total_contribution = total_contribution + $1, weekly_activeness = weekly_activeness + $2 
           WHERE family_id = $3 AND user_id = $4`,
          [amount, activenessGain, id, userId]
        );

        // Log donation
        const donationId = `don_${Date.now()}`;
        await client.query(
          `INSERT INTO family_donations (id, family_id, user_id, user_name, coin_amount, funds_credited, created_at)
           VALUES ($1, $2, $3, $4, $5, $5, $6)`,
          [donationId, id, userId, userName, amount, Date.now()]
        );

        await client.query('COMMIT');
        res.json({ ok: true, message: `Donated ${amount} coins to family treasury` });
      } catch (e) {
        await client.query('ROLLBACK');
        throw e;
      } finally {
        client.release();
      }
    } catch (err) {
      res.status(500).json({ ok: false, error: err.message });
    }
  });

  // 5. Claim Weekly Chest
  app.post('/api/families/:id/chests/claim', async (req, res) => {
    try {
      const { id } = req.params;
      const { userId, chestTier, weekKey } = req.body;

      const thresholds = { NORMAL: 200, ADVANCED: 500, RARE: 1000, SUPER: 2000 };
      const reqPoints = thresholds[chestTier];
      if (!reqPoints) return res.status(400).json({ ok: false, error: 'Invalid chest tier' });

      const fam = await db.query('SELECT weekly_activeness FROM families WHERE id = $1', [id]);
      if (!fam.rows.length || fam.rows[0].weekly_activeness < reqPoints) {
        return res.status(400).json({ ok: false, error: 'Family weekly activeness threshold not reached' });
      }

      const client = await db.connect();
      try {
        await client.query('BEGIN');
        const chestId = `c_${id}_${userId}_${chestTier}_${weekKey}`;
        await client.query(
          `INSERT INTO family_chests (id, family_id, user_id, chest_tier, week_key, claimed_at, reward_coins, reward_funds)
           VALUES ($1, $2, $3, $4, $5, $6, 100, 250)`,
          [chestId, id, userId, chestTier, weekKey, Date.now()]
        );
        await client.query('COMMIT');
        res.json({ ok: true, message: 'Chest claimed successfully' });
      } catch (e) {
        await client.query('ROLLBACK');
        if (e.code === '23505') return res.status(409).json({ ok: false, error: 'Chest already claimed for this week' });
        throw e;
      } finally {
        client.release();
      }
    } catch (err) {
      res.status(500).json({ ok: false, error: err.message });
    }
  });
}
