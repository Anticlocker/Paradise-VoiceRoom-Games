import { randomUUID } from "node:crypto";
import { ROOM_THEME_CATALOG } from "./catalog.mjs";

export const DAILY_CREST_XP_CAP = 1200;
export const MAX_CREST_LEVEL = 25;
export const CLASSIC_LOUNGE_ID = "classic_lounge";

const ALLOWED_EVENTS = new Set([
  "host_session_minute",
  "stage_speaker_activity",
  "gift_value_received",
  "moderator_activity",
  "milestone_bonus",
]);

export class RoomThemesError extends Error {
  constructor(code, message, status = 400) {
    super(message);
    this.code = code;
    this.status = status;
  }
}

export function xpForCrestLevel(level) {
  const clamped = Math.max(1, Math.min(MAX_CREST_LEVEL, Math.trunc(level)));
  if (clamped <= 1) return 0;
  const steps = clamped - 1;
  return Math.trunc((50 * steps * (steps + 1)) / 2);
}

export function crestLevelForXp(xp) {
  const safeXp = Math.max(0, Number(xp) || 0);
  let lvl = 1;
  while (lvl < MAX_CREST_LEVEL && safeXp >= xpForCrestLevel(lvl + 1)) {
    lvl += 1;
  }
  return lvl;
}

export function validateThemePurchaseEligibility(theme, alreadyOwned) {
  if (!theme || theme.active === false) {
    throw new RoomThemesError("theme_not_found", "Unknown room theme.", 404);
  }
  if (theme.unlock_type !== "store" && theme.unlockType !== "store") {
    throw new RoomThemesError("not_purchasable", "Only store themes can be purchased.", 409);
  }
  if (alreadyOwned) {
    return { alreadyOwned: true, price: null };
  }
  const price = theme.price_coins ?? theme.priceCoins ?? null;
  if (price === null || !Number.isInteger(price) || price <= 0) {
    throw new RoomThemesError(
      "price_not_configured",
      "Purchase is currently unavailable because pricing is not configured.",
      409,
    );
  }
  return { alreadyOwned: false, price };
}

export function evaluateCrestEventAntiFarming({
  eventType,
  actorId,
  hostId,
  requestedXp,
  dailyXpEarned,
}) {
  if (!ALLOWED_EVENTS.has(eventType)) {
    throw new RoomThemesError("invalid_event_type", "Unsupported crest event type.");
  }
  if (!Number.isInteger(requestedXp) || requestedXp <= 0) {
    throw new RoomThemesError("invalid_xp", "requestedXp must be a positive integer.");
  }
  if (eventType === "gift_value_received" && actorId === hostId) {
    return { accepted: false, awardedXp: 0, reason: "Self-gifting cannot earn Room Crest XP." };
  }
  const remaining = Math.max(0, DAILY_CREST_XP_CAP - Math.max(0, dailyXpEarned));
  if (remaining === 0) {
    return { accepted: false, awardedXp: 0, reason: `Daily Room Crest XP cap (${DAILY_CREST_XP_CAP}) reached.` };
  }
  return { accepted: true, awardedXp: Math.min(requestedXp, remaining), reason: null };
}

export async function seedRoomThemeCatalog(db) {
  const now = Date.now();
  await db.transaction(async (tx) => {
    for (const [index, theme] of ROOM_THEME_CATALOG.entries()) {
      await tx.run(
        `INSERT INTO room_theme_catalog
           (id, name, required_crest_level, unlock_type, default_equipped, price_coins,
            background_asset, thumbnail_asset, seat_frame_asset, host_frame_asset, overlay_asset,
            accent, artwork_status, active, sort, updated_at)
         VALUES ($1, $2, $3, $4, $5, $6, $7, $8, $9, $10, $11, $12, $13, TRUE, $14, $15)
         ON CONFLICT (id) DO UPDATE SET
           name = EXCLUDED.name,
           required_crest_level = EXCLUDED.required_crest_level,
           unlock_type = EXCLUDED.unlock_type,
           default_equipped = EXCLUDED.default_equipped,
           price_coins = EXCLUDED.price_coins,
           background_asset = EXCLUDED.background_asset,
           thumbnail_asset = EXCLUDED.thumbnail_asset,
           seat_frame_asset = EXCLUDED.seat_frame_asset,
           host_frame_asset = EXCLUDED.host_frame_asset,
           overlay_asset = EXCLUDED.overlay_asset,
           accent = EXCLUDED.accent,
           artwork_status = EXCLUDED.artwork_status,
           sort = EXCLUDED.sort,
           updated_at = EXCLUDED.updated_at`,
        [
          theme.id,
          theme.name,
          theme.requiredCrestLevel,
          theme.unlockType,
          theme.defaultEquipped,
          theme.priceCoins,
          theme.background,
          theme.thumbnail,
          theme.seatFrame,
          theme.hostFrame,
          theme.overlay,
          theme.accent,
          theme.artworkStatus,
          index,
          now,
        ],
      );
    }
  });
}

export function createRoomThemesService(db) {
  async function listCatalogForRoom({ roomId, userId }) {
    const catalog = await db.all(
      "SELECT * FROM room_theme_catalog WHERE active = TRUE ORDER BY sort, id",
    );
    const crest = await getRoomCrest(roomId);
    const roomEnts = new Set(
      (await db.all("SELECT theme_id FROM room_theme_entitlements WHERE room_id = $1", [roomId])).map(
        (r) => r.theme_id,
      ),
    );
    const userEnts = new Set(
      (await db.all("SELECT theme_id FROM user_theme_entitlements WHERE user_id = $1", [userId])).map(
        (r) => r.theme_id,
      ),
    );
    const equipment = await db.get("SELECT theme_id FROM room_theme_equipment WHERE room_id = $1", [roomId]);
    const equippedId = equipment?.theme_id ?? CLASSIC_LOUNGE_ID;

    return {
      roomId,
      equippedThemeId: equippedId,
      crest,
      items: catalog.map((row) => {
        const unlockedByCrest =
          row.unlock_type === "crest" &&
          row.required_crest_level != null &&
          crest.level >= row.required_crest_level;
        const owned =
          row.unlock_type === "free" ||
          roomEnts.has(row.id) ||
          userEnts.has(row.id) ||
          unlockedByCrest;
        return {
          id: row.id,
          name: row.name,
          requiredCrestLevel: row.required_crest_level,
          unlockType: row.unlock_type,
          defaultEquipped: !!row.default_equipped,
          priceCoins: row.price_coins,
          accent: row.accent,
          artworkStatus: row.artwork_status,
          owned,
          equipped: row.id === equippedId,
        };
      }),
    };
  }

  async function getRoomCrest(roomId) {
    const row = await db.get("SELECT * FROM room_crest_progress WHERE room_id = $1", [roomId]);
    const xp = Number(row?.crest_xp ?? 0);
    const level = row?.crest_level ?? 1;
    return {
      roomId,
      level,
      xp,
      nextLevelXp: xpForCrestLevel(Math.min(MAX_CREST_LEVEL, level + 1)),
      dailyXpEarned: row?.daily_xp_earned ?? 0,
      totalHostMinutes: row?.total_host_minutes ?? 0,
      totalGiftsValue: Number(row?.total_gifts_value ?? 0),
    };
  }

  async function recordCrestEvent({ roomId, actorId, eventType, requestedXp, idempotencyKey }) {
    if (!idempotencyKey || typeof idempotencyKey !== "string") {
      throw new RoomThemesError("invalid_idempotency_key", "idempotencyKey is required.");
    }
    const existing = await db.get("SELECT * FROM room_crest_events WHERE idempotency_key = $1", [idempotencyKey]);
    if (existing) {
      return { replay: true, accepted: !!existing.accepted, crest: await getRoomCrest(roomId), unlockedThemeIds: [] };
    }

    const room = await db.get("SELECT id, host_id FROM rooms WHERE id = $1", [roomId]);
    if (!room) throw new RoomThemesError("room_not_found", "Unknown room.", 404);

    const today = new Date().toISOString().slice(0, 10);
    const now = Date.now();
    let unlockedThemeIds = [];
    let accepted = false;
    let rejectionReason = null;

    await db.transaction(async (tx) => {
      await tx.run(
        `INSERT INTO room_crest_progress (room_id, crest_level, crest_xp, daily_xp_date, daily_xp_earned, updated_at)
         VALUES ($1, 1, 0, $2, 0, $3) ON CONFLICT (room_id) DO NOTHING`,
        [roomId, today, now],
      );
      const prog = await tx.get("SELECT * FROM room_crest_progress WHERE room_id = $1 FOR UPDATE", [roomId]);
      const currentDaily = prog.daily_xp_date === today ? prog.daily_xp_earned : 0;

      const decision = evaluateCrestEventAntiFarming({
        eventType,
        actorId,
        hostId: room.host_id,
        requestedXp,
        dailyXpEarned: currentDaily,
      });
      accepted = decision.accepted;
      rejectionReason = decision.reason;

      await tx.run(
        `INSERT INTO room_crest_events (id, room_id, actor_id, event_type, xp_delta, accepted, rejection_reason, idempotency_key, created_at)
         VALUES ($1, $2, $3, $4, $5, $6, $7, $8, $9)`,
        [`ce_${randomUUID()}`, roomId, actorId, eventType, decision.awardedXp, accepted, rejectionReason, idempotencyKey, now],
      );

      if (accepted) {
        const nextXp = Number(prog.crest_xp) + decision.awardedXp;
        const nextLevel = crestLevelForXp(nextXp);
        await tx.run(
          `UPDATE room_crest_progress
           SET crest_xp = $1, crest_level = $2, daily_xp_date = $3, daily_xp_earned = $4,
               total_host_minutes = total_host_minutes + $5,
               total_gifts_value = total_gifts_value + $6,
               updated_at = $7
           WHERE room_id = $8`,
          [
            nextXp,
            nextLevel,
            today,
            currentDaily + decision.awardedXp,
            eventType === "host_session_minute" ? 1 : 0,
            eventType === "gift_value_received" ? requestedXp : 0,
            now,
            roomId,
          ],
        );

        const newlyEligible = await tx.all(
          `SELECT id, required_crest_level FROM room_theme_catalog
           WHERE unlock_type = 'crest' AND required_crest_level <= $1`,
          [nextLevel],
        );
        for (const t of newlyEligible) {
          const res = await tx.run(
            `INSERT INTO room_theme_entitlements (room_id, theme_id, granted_by_crest_level, granted_at)
             VALUES ($1, $2, $3, $4) ON CONFLICT (room_id, theme_id) DO NOTHING`,
            [roomId, t.id, t.required_crest_level, now],
          );
          if (res.changes > 0) unlockedThemeIds.push(t.id);
        }
      }
    });

    return { replay: false, accepted, rejectionReason, crest: await getRoomCrest(roomId), unlockedThemeIds };
  }

  async function equipRoomTheme({ roomId, userId, themeId }) {
    const room = await db.get("SELECT id, host_id FROM rooms WHERE id = $1", [roomId]);
    if (!room) throw new RoomThemesError("room_not_found", "Unknown room.", 404);
    if (room.host_id !== userId) {
      throw new RoomThemesError("not_allowed", "Only the room host can change the room theme.", 403);
    }

    const theme = await db.get("SELECT * FROM room_theme_catalog WHERE id = $1 AND active = TRUE", [themeId]);
    if (!theme) throw new RoomThemesError("theme_not_found", "Unknown room theme.", 404);

    const crest = await getRoomCrest(roomId);
    const roomEnt = await db.get("SELECT 1 FROM room_theme_entitlements WHERE room_id = $1 AND theme_id = $2", [roomId, themeId]);
    const userEnt = await db.get("SELECT 1 FROM user_theme_entitlements WHERE user_id = $1 AND theme_id = $2", [userId, themeId]);

    const unlocked =
      theme.unlock_type === "free" ||
      !!roomEnt ||
      !!userEnt ||
      (theme.unlock_type === "crest" && theme.required_crest_level != null && crest.level >= theme.required_crest_level);

    if (!unlocked) {
      throw new RoomThemesError("theme_locked", `${theme.name} is locked.`, 403);
    }

    const now = Date.now();
    await db.run(
      `INSERT INTO room_theme_equipment (room_id, theme_id, updated_by, updated_at)
       VALUES ($1, $2, $3, $4)
       ON CONFLICT (room_id) DO UPDATE SET
         theme_id = EXCLUDED.theme_id, updated_by = EXCLUDED.updated_by, updated_at = EXCLUDED.updated_at`,
      [roomId, themeId, userId, now],
    );
    return { roomId, equippedThemeId: themeId, updatedAt: now };
  }

  async function purchaseStoreTheme({ userId, themeId, idempotencyKey }) {
    if (!idempotencyKey || typeof idempotencyKey !== "string") {
      throw new RoomThemesError("invalid_idempotency_key", "idempotencyKey is required.");
    }
    const existingOrder = await db.get("SELECT * FROM theme_purchase_orders WHERE idempotency_key = $1", [idempotencyKey]);
    if (existingOrder) {
      return { order: existingOrder, replay: true };
    }

    const theme = await db.get("SELECT * FROM room_theme_catalog WHERE id = $1 AND active = TRUE", [themeId]);
    const alreadyOwned = !!(await db.get(
      "SELECT 1 FROM user_theme_entitlements WHERE user_id = $1 AND theme_id = $2",
      [userId, themeId],
    ));
    const { alreadyOwned: owned, price } = validateThemePurchaseEligibility(theme, alreadyOwned);
    if (owned) {
      return { alreadyOwned: true, themeId, replay: false };
    }

    const orderId = `tpo_${randomUUID()}`;
    const txId = `tx_${randomUUID()}`;
    const now = Date.now();

    await db.transaction(async (tx) => {
      await tx.run(
        "INSERT INTO wallets (user_id, balance, updated_at) VALUES ($1, 0, $2) ON CONFLICT (user_id) DO NOTHING",
        [userId, now],
      );
      const wallet = await tx.get("SELECT balance FROM wallets WHERE user_id = $1 FOR UPDATE", [userId]);
      if (wallet.balance < price) {
        throw new RoomThemesError("insufficient_funds", "Not enough coins for this theme.", 409);
      }
      await tx.run(
        `INSERT INTO transactions
           (id, kind, status, from_user_id, to_user_id, gift_id, pack_id, coins,
            price_minor, currency, provider, provider_ref, idempotency_key, failure_reason, created_at, updated_at)
         VALUES ($1, 'theme_purchase', 'completed', $2, NULL, NULL, NULL, $3, NULL, NULL, NULL, NULL, $4, NULL, $5, $5)`,
        [txId, userId, price, `theme:${idempotencyKey}`, now],
      );
      await tx.run("UPDATE wallets SET balance = balance - $1, updated_at = $2 WHERE user_id = $3", [price, now, userId]);
      await tx.run(
        "INSERT INTO ledger (user_id, delta, kind, transaction_id, created_at) VALUES ($1, $2, 'theme_purchase', $3, $4)",
        [userId, -price, txId, now],
      );
      await tx.run(
        `INSERT INTO theme_purchase_orders (id, user_id, theme_id, coins_charged, status, idempotency_key, wallet_transaction_id, created_at)
         VALUES ($1, $2, $3, $4, 'completed', $5, $6, $7)`,
        [orderId, userId, themeId, price, idempotencyKey, txId, now],
      );
      await tx.run(
        `INSERT INTO user_theme_entitlements (user_id, theme_id, source, purchase_order_id, granted_at)
         VALUES ($1, $2, 'store', $3, $4) ON CONFLICT (user_id, theme_id) DO NOTHING`,
        [userId, themeId, orderId, now],
      );
    });

    return { order: await db.get("SELECT * FROM theme_purchase_orders WHERE id = $1", [orderId]), replay: false };
  }

  return {
    listCatalogForRoom,
    getRoomCrest,
    recordCrestEvent,
    equipRoomTheme,
    purchaseStoreTheme,
  };
}
