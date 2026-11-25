-- Room Themes, Crest Progression, Entitlements, and Purchases, migration 005.
--
-- Non-destructive additive migration appended after 004_leaderboards_and_frames.sql.
-- Keeps ephemeral live voice/seat presence out of PostgreSQL (owned by LiveKit);
-- stores persistent room theme equipment, crest progression, and audited store purchases.

CREATE TABLE room_theme_catalog (
  id                   TEXT PRIMARY KEY,
  name                 TEXT NOT NULL,
  required_crest_level INTEGER CHECK (required_crest_level IS NULL OR required_crest_level >= 1),
  unlock_type          TEXT NOT NULL CHECK (unlock_type IN ('free', 'crest', 'store')),
  default_equipped     BOOLEAN NOT NULL DEFAULT FALSE,
  price_coins          INTEGER CHECK (price_coins IS NULL OR price_coins > 0),
  background_asset     TEXT NOT NULL,
  thumbnail_asset      TEXT NOT NULL,
  seat_frame_asset     TEXT NOT NULL,
  host_frame_asset     TEXT NOT NULL,
  overlay_asset        TEXT NOT NULL,
  accent               TEXT NOT NULL,
  artwork_status       TEXT NOT NULL DEFAULT 'procedural_starter',
  active               BOOLEAN NOT NULL DEFAULT TRUE,
  sort                 INTEGER NOT NULL DEFAULT 0,
  updated_at           BIGINT NOT NULL
);

CREATE TABLE room_crest_progress (
  room_id            TEXT PRIMARY KEY REFERENCES rooms(id) ON DELETE CASCADE,
  crest_level        INTEGER NOT NULL DEFAULT 1 CHECK (crest_level >= 1 AND crest_level <= 25),
  crest_xp           BIGINT NOT NULL DEFAULT 0 CHECK (crest_xp >= 0),
  daily_xp_date      TEXT NOT NULL DEFAULT '',
  daily_xp_earned    INTEGER NOT NULL DEFAULT 0 CHECK (daily_xp_earned >= 0),
  total_host_minutes INTEGER NOT NULL DEFAULT 0 CHECK (total_host_minutes >= 0),
  total_gifts_value  BIGINT NOT NULL DEFAULT 0 CHECK (total_gifts_value >= 0),
  updated_at         BIGINT NOT NULL
);

CREATE TABLE room_crest_events (
  id               TEXT PRIMARY KEY,
  room_id          TEXT NOT NULL REFERENCES rooms(id) ON DELETE CASCADE,
  actor_id         TEXT NOT NULL,
  event_type       TEXT NOT NULL CHECK (event_type IN ('host_session_minute', 'stage_speaker_activity', 'gift_value_received', 'moderator_activity', 'milestone_bonus')),
  xp_delta         INTEGER NOT NULL CHECK (xp_delta >= 0),
  accepted         BOOLEAN NOT NULL,
  rejection_reason TEXT,
  idempotency_key  TEXT UNIQUE NOT NULL,
  created_at       BIGINT NOT NULL
);

CREATE INDEX idx_room_crest_events_room ON room_crest_events(room_id, created_at DESC);

CREATE TABLE room_theme_entitlements (
  room_id                TEXT NOT NULL REFERENCES rooms(id) ON DELETE CASCADE,
  theme_id               TEXT NOT NULL REFERENCES room_theme_catalog(id),
  granted_by_crest_level INTEGER,
  granted_at             BIGINT NOT NULL,
  PRIMARY KEY (room_id, theme_id)
);

CREATE TABLE user_theme_entitlements (
  user_id                TEXT NOT NULL,
  theme_id               TEXT NOT NULL REFERENCES room_theme_catalog(id),
  source                 TEXT NOT NULL CHECK (source IN ('free', 'store', 'grant')),
  purchase_order_id      TEXT,
  granted_at             BIGINT NOT NULL,
  PRIMARY KEY (user_id, theme_id)
);

CREATE TABLE room_theme_equipment (
  room_id    TEXT PRIMARY KEY REFERENCES rooms(id) ON DELETE CASCADE,
  theme_id   TEXT NOT NULL REFERENCES room_theme_catalog(id) DEFAULT 'classic_lounge',
  updated_by TEXT NOT NULL,
  updated_at BIGINT NOT NULL
);

-- Extend economy transactions/ledger kind constraints safely so theme purchases reconcile in wallet+ledger.
ALTER TABLE transactions DROP CONSTRAINT IF EXISTS transactions_kind_check;
ALTER TABLE transactions ADD CONSTRAINT transactions_kind_check CHECK (kind IN ('gift', 'topup', 'theme_purchase'));

ALTER TABLE ledger DROP CONSTRAINT IF EXISTS ledger_kind_check;
ALTER TABLE ledger ADD CONSTRAINT ledger_kind_check CHECK (kind IN ('topup', 'gift_sent', 'gift_received', 'theme_purchase'));

CREATE TABLE theme_purchase_orders (
  id                    TEXT PRIMARY KEY,
  user_id               TEXT NOT NULL,
  theme_id              TEXT NOT NULL REFERENCES room_theme_catalog(id),
  coins_charged         INTEGER NOT NULL CHECK (coins_charged > 0),
  status                TEXT NOT NULL CHECK (status IN ('completed', 'failed', 'refunded')),
  idempotency_key       TEXT NOT NULL UNIQUE,
  wallet_transaction_id TEXT REFERENCES transactions(id),
  created_at            BIGINT NOT NULL
);

CREATE INDEX idx_theme_purchase_user ON theme_purchase_orders(user_id, created_at DESC);
