-- Family System tables, migration 003.
--
-- Server-authoritative Guild/Family System schema.
-- Rules:
--   1. Shared Family Funds and personal Family Coins are tracked with integer values.
--   2. One active family per user at a time.
--   3. All role updates, kicks, and transfers are audited.
--   4. Idempotent quest claims and chest claims prevent double payout.

CREATE TABLE families (
  id                  TEXT PRIMARY KEY,
  name                TEXT NOT NULL UNIQUE,
  tag                 TEXT NOT NULL UNIQUE,
  sign                TEXT NOT NULL DEFAULT '',
  description         TEXT NOT NULL DEFAULT '',
  level               INTEGER NOT NULL DEFAULT 1 CHECK (level >= 1 AND level <= 10),
  tier                TEXT NOT NULL DEFAULT 'BRONZE' CHECK (tier IN ('BRONZE', 'SILVER', 'GOLD', 'PLATINUM', 'DIAMOND', 'LEGEND')),
  funds               BIGINT NOT NULL DEFAULT 0 CHECK (funds >= 0),
  weekly_activeness   INTEGER NOT NULL DEFAULT 0 CHECK (weekly_activeness >= 0),
  member_count        INTEGER NOT NULL DEFAULT 1 CHECK (member_count >= 1),
  max_members         INTEGER NOT NULL DEFAULT 25 CHECK (max_members >= 10),
  leader_id           TEXT NOT NULL,
  leader_name         TEXT NOT NULL,
  announcement        TEXT NOT NULL DEFAULT '',
  voice_room_id       TEXT,
  recruitment_policy  TEXT NOT NULL DEFAULT 'OPEN' CHECK (recruitment_policy IN ('OPEN', 'APPROVAL_REQUIRED', 'INVITE_ONLY')),
  min_level           INTEGER NOT NULL DEFAULT 1,
  created_at          BIGINT NOT NULL
);

CREATE INDEX idx_families_activeness ON families(weekly_activeness DESC);
CREATE INDEX idx_families_level ON families(level DESC);

CREATE TABLE family_members (
  family_id           TEXT NOT NULL REFERENCES families(id) ON DELETE CASCADE,
  user_id             TEXT NOT NULL,
  name                TEXT NOT NULL,
  role                TEXT NOT NULL DEFAULT 'MEMBER' CHECK (role IN ('LEADER', 'DEPUTY', 'ADMIN', 'MODERATOR', 'MEMBER')),
  level               INTEGER NOT NULL DEFAULT 1,
  weekly_activeness   INTEGER NOT NULL DEFAULT 0,
  total_contribution  BIGINT NOT NULL DEFAULT 0,
  is_muted            BOOLEAN NOT NULL DEFAULT FALSE,
  joined_at           BIGINT NOT NULL,
  PRIMARY KEY (family_id, user_id)
);

CREATE UNIQUE INDEX idx_family_members_single_family ON family_members(user_id);

CREATE TABLE family_quests (
  id                  TEXT PRIMARY KEY,
  title               TEXT NOT NULL,
  description         TEXT NOT NULL DEFAULT '',
  category            TEXT NOT NULL CHECK (category IN ('DAILY', 'WEEKLY', 'COMMUNITY', 'CONTRIBUTION')),
  target_count        INTEGER NOT NULL CHECK (target_count > 0),
  reward_coins        INTEGER NOT NULL CHECK (reward_coins >= 0),
  reward_funds        INTEGER NOT NULL CHECK (reward_funds >= 0),
  active              BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE family_quest_progress (
  family_id           TEXT NOT NULL REFERENCES families(id) ON DELETE CASCADE,
  user_id             TEXT NOT NULL,
  quest_id            TEXT NOT NULL REFERENCES family_quests(id) ON DELETE CASCADE,
  current_count       INTEGER NOT NULL DEFAULT 0,
  claimed             BOOLEAN NOT NULL DEFAULT FALSE,
  claimed_at          BIGINT,
  period_key          TEXT NOT NULL, -- e.g. "2026-W47" or "2026-11-21"
  PRIMARY KEY (family_id, user_id, quest_id, period_key)
);

CREATE TABLE family_donations (
  id                  TEXT PRIMARY KEY,
  family_id           TEXT NOT NULL REFERENCES families(id) ON DELETE CASCADE,
  user_id             TEXT NOT NULL,
  user_name           TEXT NOT NULL,
  coin_amount         INTEGER NOT NULL CHECK (coin_amount > 0),
  funds_credited      INTEGER NOT NULL CHECK (funds_credited > 0),
  coins_rewarded      INTEGER NOT NULL DEFAULT 0,
  created_at          BIGINT NOT NULL
);

CREATE INDEX idx_family_donations_family ON family_donations(family_id, created_at DESC);

CREATE TABLE family_chests (
  id                  TEXT PRIMARY KEY,
  family_id           TEXT NOT NULL REFERENCES families(id) ON DELETE CASCADE,
  user_id             TEXT NOT NULL,
  chest_tier          TEXT NOT NULL CHECK (chest_tier IN ('NORMAL', 'ADVANCED', 'RARE', 'SUPER')),
  week_key            TEXT NOT NULL,
  claimed_at          BIGINT NOT NULL,
  reward_coins        INTEGER NOT NULL,
  reward_funds        INTEGER NOT NULL,
  UNIQUE (family_id, user_id, chest_tier, week_key)
);

CREATE TABLE family_activity_events (
  id                  TEXT PRIMARY KEY,
  family_id           TEXT NOT NULL REFERENCES families(id) ON DELETE CASCADE,
  event_type          TEXT NOT NULL,
  actor_name          TEXT NOT NULL,
  description         TEXT NOT NULL,
  created_at          BIGINT NOT NULL
);

CREATE INDEX idx_family_events_timeline ON family_activity_events(family_id, created_at DESC);

CREATE TABLE family_join_requests (
  id                  TEXT PRIMARY KEY,
  family_id           TEXT NOT NULL REFERENCES families(id) ON DELETE CASCADE,
  applicant_id        TEXT NOT NULL,
  applicant_name      TEXT NOT NULL,
  applicant_level     INTEGER NOT NULL DEFAULT 1,
  requested_at        BIGINT NOT NULL,
  UNIQUE (family_id, applicant_id)
);
