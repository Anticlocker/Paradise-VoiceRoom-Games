-- Leaderboards & Animated Profile Frames schema, migration 004.
--
-- Server-authoritative Leaderboard Ranking and Cosmetics Entitlements.
-- Rules:
--   1. Ranking score events are immutable, deduplicated, and server-validated.
--   2. Periodic snapshots lock in historical rankings at UTC period boundaries.
--   3. Digital cosmetics entitlements require server verification and audit logging.
--   4. User equipped frames are stored server-side and broadcast.

CREATE TABLE ranking_categories (
  id                  TEXT PRIMARY KEY,
  title               TEXT NOT NULL,
  subtitle            TEXT NOT NULL,
  score_unit          TEXT NOT NULL,
  banner_color        TEXT NOT NULL,
  reset_schedule      TEXT NOT NULL,
  is_active           BOOLEAN NOT NULL DEFAULT TRUE
);

INSERT INTO ranking_categories (id, title, subtitle, score_unit, banner_color, reset_schedule) VALUES
  ('popularity', 'Popularity', 'Most admired stars', 'Charm', '#FFDBEA', 'Daily 00:00 UTC / Weekly Sunday 23:59 UTC'),
  ('vip', 'VIP', 'Prestigious members', 'VIP Pts', '#FFF0C4', 'Weekly Sunday 23:59 UTC'),
  ('couple', 'Couple', 'Closest bond partners', 'Intimacy', '#F9DAFF', 'Daily 00:00 UTC / Annual Dec 31 23:59 UTC'),
  ('room', 'Room', 'Hottest voice lounges', 'Heat', '#D9F1FF', 'Daily 00:00 UTC'),
  ('bff', 'BFF', 'Inseparable friends', 'Bond', '#E8DDFF', 'Weekly Sunday 23:59 UTC'),
  ('family', 'Family', 'Top active dynasties', 'Activeness', '#D8FFF2', 'Weekly Sunday 23:59 UTC')
ON CONFLICT (id) DO NOTHING;

CREATE TABLE ranking_score_events (
  id                  TEXT PRIMARY KEY,
  category_id         TEXT NOT NULL REFERENCES ranking_categories(id),
  entity_id           TEXT NOT NULL, -- user_id, couple_id, room_id, or family_id
  entity_name         TEXT NOT NULL,
  points              BIGINT NOT NULL CHECK (points > 0),
  source_type         TEXT NOT NULL, -- 'gift', 'topup', 'voice_time', 'chat', 'family_quest'
  source_ref_id       TEXT,
  created_at          BIGINT NOT NULL
);

CREATE INDEX idx_ranking_events_category_time ON ranking_score_events(category_id, created_at DESC);
CREATE INDEX idx_ranking_events_entity ON ranking_score_events(entity_id);

CREATE TABLE ranking_snapshots (
  id                  TEXT PRIMARY KEY,
  category_id         TEXT NOT NULL REFERENCES ranking_categories(id),
  period_type         TEXT NOT NULL CHECK (period_type IN ('today', 'yesterday', 'weekly', 'total', 'celebrity', 'annual', 'global')),
  period_key          TEXT NOT NULL, -- e.g. '2026-W47', '2026-11-22'
  rank                INTEGER NOT NULL CHECK (rank >= 1),
  entity_id           TEXT NOT NULL,
  entity_name         TEXT NOT NULL,
  avatar_path         TEXT,
  score               BIGINT NOT NULL,
  level               INTEGER NOT NULL DEFAULT 1,
  verified            BOOLEAN NOT NULL DEFAULT FALSE,
  frame_id            TEXT,
  metadata            JSONB,
  snapshot_at         BIGINT NOT NULL
);

CREATE INDEX idx_ranking_snapshots_lookup ON ranking_snapshots(category_id, period_type, period_key, rank ASC);

CREATE TABLE frame_catalog (
  id                  TEXT PRIMARY KEY,
  name                TEXT NOT NULL,
  tier                TEXT NOT NULL CHECK (tier IN ('COMMON', 'RARE', 'EPIC', 'LEGENDARY', 'MYTHIC', 'CELESTIAL')),
  description         TEXT NOT NULL,
  unlock_source       TEXT NOT NULL CHECK (unlock_source IN ('DEFAULT', 'ACCOUNT_LEVEL', 'ACHIEVEMENT', 'VIP_BENEFIT', 'SHOP_COINS', 'FAMILY_REWARDS', 'EVENT')),
  min_level           INTEGER NOT NULL DEFAULT 1,
  price_coins         BIGINT NOT NULL DEFAULT 0 CHECK (price_coins >= 0),
  duration_days       INTEGER NOT NULL DEFAULT 0 CHECK (duration_days >= 0),
  is_shop_purchasable BOOLEAN NOT NULL DEFAULT FALSE,
  is_active           BOOLEAN NOT NULL DEFAULT TRUE
);

INSERT INTO frame_catalog (id, name, tier, description, unlock_source, min_level, price_coins, duration_days, is_shop_purchasable) VALUES
  ('frame_common_aurora', 'Aurora Breeze', 'COMMON', 'Clean light cyan starter ring with soft dual-tone border.', 'DEFAULT', 1, 0, 0, FALSE),
  ('frame_rare_cyan_pulse', 'Cyan Pulse', 'RARE', '2200ms breathing cyan aura with radial ambient glow.', 'ACCOUNT_LEVEL', 5, 0, 0, FALSE),
  ('frame_rare_azure_crest', 'Azure Crest', 'RARE', 'Sky blue pulsing ring with bright starlight accents.', 'SHOP_COINS', 1, 600, 30, TRUE),
  ('frame_epic_violet_orbit', 'Violet Orbit', 'EPIC', '4200ms clockwise rotating particles (8 orbit dots) on royal violet ring.', 'ACHIEVEMENT', 10, 0, 0, FALSE),
  ('frame_epic_amethyst_ring', 'Amethyst Spark', 'EPIC', 'Rotating lilac particle stream with electric violet aura.', 'SHOP_COINS', 1, 1800, 30, TRUE),
  ('frame_legendary_golden_sun', 'Solar Monarch', 'LEGENDARY', '5600ms counter-rotating dual orbital bands with 12 sparkling stars.', 'VIP_BENEFIT', 15, 0, 0, FALSE),
  ('frame_legendary_amber_crown', 'Amber Nova', 'LEGENDARY', 'Dual counter-rotating golden starbands for prestigious leaders.', 'SHOP_COINS', 1, 4500, 30, TRUE),
  ('frame_mythic_radiant_crown', 'Radiant Sovereign', 'MYTHIC', '6400ms slow floating gold crown with 3 orbiting jewels.', 'FAMILY_REWARDS', 20, 0, 0, FALSE),
  ('frame_mythic_rose_jewel', 'Rose Empress', 'MYTHIC', 'Imperial rose gold floating tiara and ruby star orbiting jewels.', 'SHOP_COINS', 1, 9000, 30, TRUE),
  ('frame_celestial_aurora_stream', 'Celestial Aurora', 'CELESTIAL', '8000ms sweep aurora gradient, radiant rays, and celestial gems.', 'EVENT', 25, 0, 0, FALSE),
  ('frame_celestial_nebula_god', 'Nebula Paragon', 'CELESTIAL', 'Full-spectrum rainbow cosmic aurora with orbiting stardust gems.', 'SHOP_COINS', 1, 18000, 30, TRUE)
ON CONFLICT (id) DO NOTHING;

CREATE TABLE frame_entitlements (
  user_id             TEXT NOT NULL,
  frame_id            TEXT NOT NULL REFERENCES frame_catalog(id) ON DELETE CASCADE,
  source              TEXT NOT NULL,
  granted_at          BIGINT NOT NULL,
  expires_at          BIGINT,
  PRIMARY KEY (user_id, frame_id)
);

CREATE INDEX idx_frame_entitlements_user ON frame_entitlements(user_id);

CREATE TABLE frame_equipment (
  user_id             TEXT PRIMARY KEY,
  equipped_frame_id   TEXT REFERENCES frame_catalog(id) ON DELETE SET NULL,
  equipped_at         BIGINT NOT NULL
);

CREATE TABLE frame_shop_orders (
  id                  TEXT PRIMARY KEY,
  user_id             TEXT NOT NULL,
  frame_id            TEXT NOT NULL REFERENCES frame_catalog(id),
  price_coins         BIGINT NOT NULL,
  status              TEXT NOT NULL DEFAULT 'COMPLETED' CHECK (status IN ('PENDING', 'COMPLETED', 'REFUNDED', 'FAILED')),
  created_at          BIGINT NOT NULL
);

CREATE TABLE ranking_audit_logs (
  id                  TEXT PRIMARY KEY,
  event_type          TEXT NOT NULL,
  user_id             TEXT,
  target_id           TEXT,
  details             JSONB,
  created_at          BIGINT NOT NULL
);
