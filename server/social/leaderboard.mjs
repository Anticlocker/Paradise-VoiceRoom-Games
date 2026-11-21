/**
 * Leaderboard & Animated Profile Frames Server Routes
 *
 * Implements server-authoritative leaderboard queries, snapshot aggregation,
 * frame catalog retrieval, and user frame equipment & purchasing.
 */

export function registerLeaderboardAndFrameRoutes(app, db) {
  // 1. Get Leaderboard Rankings
  app.get('/api/leaderboard', async (req, res) => {
    try {
      const { category = 'popularity', period = 'today', limit = 20 } = req.query;
      const safeLimit = Math.min(parseInt(limit, 10) || 20, 100);

      // Check for snapshots first
      const snapshotQuery = `
        SELECT rank, entity_id, entity_name, avatar_path, score, level, verified, frame_id, metadata
        FROM ranking_snapshots
        WHERE category_id = $1 AND period_type = $2
        ORDER BY rank ASC
        LIMIT $3
      `;
      const { rows } = await db.query(snapshotQuery, [category.toLowerCase(), period.toLowerCase(), safeLimit]);

      if (rows.length > 0) {
        return res.json({ ok: true, source: 'snapshot', entries: rows });
      }

      // If no snapshot exists, calculate live ranking from score events
      const liveQuery = `
        SELECT 
          DENSE_RANK() OVER (ORDER BY SUM(points) DESC) AS rank,
          entity_id,
          entity_name,
          SUM(points) AS score,
          COALESCE(fe.equipped_frame_id, 'frame_common_aurora') AS frame_id
        FROM ranking_score_events rse
        LEFT JOIN frame_equipment fe ON fe.user_id = rse.entity_id
        WHERE category_id = $1
        GROUP BY entity_id, entity_name, fe.equipped_frame_id
        ORDER BY score DESC
        LIMIT $2
      `;
      const liveResult = await db.query(liveQuery, [category.toLowerCase(), safeLimit]);
      res.json({ ok: true, source: 'live', entries: liveResult.rows });
    } catch (err) {
      res.status(500).json({ ok: false, error: err.message });
    }
  });

  // 2. Get Frame Catalog
  app.get('/api/frames/catalog', async (_req, res) => {
    try {
      const { rows } = await db.query(
        'SELECT * FROM frame_catalog WHERE is_active = TRUE ORDER BY min_level ASC, price_coins ASC'
      );
      res.json({ ok: true, catalog: rows });
    } catch (err) {
      res.status(500).json({ ok: false, error: err.message });
    }
  });

  // 3. Get User Entitlements and Equipped Frame
  app.get('/api/frames/my-entitlements', async (req, res) => {
    try {
      const userId = req.headers['x-user-id'] || req.query.userId;
      if (!userId) return res.status(401).json({ ok: false, error: 'Unauthorized: missing user id' });

      const entitlementsResult = await db.query(
        'SELECT frame_id, source, granted_at, expires_at FROM frame_entitlements WHERE user_id = $1',
        [userId]
      );

      const equippedResult = await db.query(
        'SELECT equipped_frame_id, equipped_at FROM frame_equipment WHERE user_id = $1',
        [userId]
      );

      res.json({
        ok: true,
        entitlements: entitlementsResult.rows,
        equippedFrameId: equippedResult.rows[0]?.equipped_frame_id || 'frame_common_aurora',
      });
    } catch (err) {
      res.status(500).json({ ok: false, error: err.message });
    }
  });

  // 4. Equip Frame
  app.post('/api/frames/equip', async (req, res) => {
    try {
      const userId = req.headers['x-user-id'] || req.body.userId;
      const { frameId } = req.body;
      if (!userId) return res.status(401).json({ ok: false, error: 'Unauthorized' });

      if (frameId) {
        // Validate user entitlement or level requirement
        const ent = await db.query(
          'SELECT * FROM frame_entitlements WHERE user_id = $1 AND frame_id = $2',
          [userId, frameId]
        );
        if (ent.rows.length === 0 && frameId !== 'frame_common_aurora') {
          return res.status(403).json({ ok: false, error: 'User does not own this frame' });
        }
      }

      await db.query(
        `INSERT INTO frame_equipment (user_id, equipped_frame_id, equipped_at)
         VALUES ($1, $2, $3)
         ON CONFLICT (user_id) DO UPDATE SET equipped_frame_id = $2, equipped_at = $3`,
        [userId, frameId || null, Date.now()]
      );

      res.json({ ok: true, equippedFrameId: frameId || null });
    } catch (err) {
      res.status(500).json({ ok: false, error: err.message });
    }
  });

  // 5. Purchase Frame
  app.post('/api/frames/purchase', async (req, res) => {
    const client = await db.connect();
    try {
      const userId = req.headers['x-user-id'] || req.body.userId;
      const { frameId } = req.body;
      if (!userId || !frameId) return res.status(400).json({ ok: false, error: 'Missing parameters' });

      await client.query('BEGIN');

      // Check frame catalog
      const frameRes = await client.query('SELECT * FROM frame_catalog WHERE id = $1', [frameId]);
      if (frameRes.rows.length === 0) {
        await client.query('ROLLBACK');
        return res.status(404).json({ ok: false, error: 'Frame not found' });
      }
      const frame = frameRes.rows[0];
      if (!frame.is_shop_purchasable) {
        await client.query('ROLLBACK');
        return res.status(400).json({ ok: false, error: 'Frame is not purchasable' });
      }

      // Check existing entitlement
      const entRes = await client.query(
        'SELECT * FROM frame_entitlements WHERE user_id = $1 AND frame_id = $2',
        [userId, frameId]
      );
      if (entRes.rows.length > 0) {
        await client.query('ROLLBACK');
        return res.status(400).json({ ok: false, error: 'Frame already owned' });
      }

      // Grant entitlement
      const expiresAt = frame.duration_days > 0 ? Date.now() + frame.duration_days * 86400000 : null;
      await client.query(
        `INSERT INTO frame_entitlements (user_id, frame_id, source, granted_at, expires_at)
         VALUES ($1, $2, 'SHOP_COINS', $3, $4)`,
        [userId, frameId, Date.now(), expiresAt]
      );

      // Auto-equip
      await client.query(
        `INSERT INTO frame_equipment (user_id, equipped_frame_id, equipped_at)
         VALUES ($1, $2, $3)
         ON CONFLICT (user_id) DO UPDATE SET equipped_frame_id = $2, equipped_at = $3`,
        [userId, frameId, Date.now()]
      );

      // Log order
      const orderId = `ord_frm_${Date.now()}_${Math.random().toString(36).substring(2, 7)}`;
      await client.query(
        `INSERT INTO frame_shop_orders (id, user_id, frame_id, price_coins, status, created_at)
         VALUES ($1, $2, $3, $4, 'COMPLETED', $5)`,
        [orderId, userId, frameId, frame.price_coins, Date.now()]
      );

      await client.query('COMMIT');
      res.json({ ok: true, orderId, frameId, equipped: true });
    } catch (err) {
      await client.query('ROLLBACK');
      res.status(500).json({ ok: false, error: err.message });
    } finally {
      client.release();
    }
  });

  // 6. Get Ranking Rules
  app.get('/api/leaderboard/rules', async (_req, res) => {
    try {
      const { rows } = await db.query('SELECT * FROM ranking_categories WHERE is_active = TRUE');
      res.json({ ok: true, categories: rows });
    } catch (err) {
      res.status(500).json({ ok: false, error: err.message });
    }
  });
}
