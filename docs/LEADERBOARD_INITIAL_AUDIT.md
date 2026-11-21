# LEADERBOARD & ANIMATED PROFILE FRAMES — INITIAL SYSTEM AUDIT

**Date:** November 22, 2026  
**Auditor:** Principal Android Architect, Jetpack Compose Motion Specialist & Backend Engineer  
**Project:** Social Gaming Application (Kotlin + Jetpack Compose)

---

## 1. Executive Summary

This audit evaluates the pre-existing ranking implementation, cosmetic profile frames, navigation pathways, and backend readiness against the specifications outlined in `ANTIGRAVITY_MASTER_PROMPT.md`, `docs/FRAME_ANIMATION_SPEC.md`, and the reference video `111398.mp4`.

Prior to this upgrade:
- `RankingScreen.kt` was a 419-line rudimentary prototype with a dark violet theme (`#6A5ACD`), static mockup data, hardcoded top-3 cards, and no dynamic frame rendering.
- Profile frames were not functional; avatars used standard circular clipping with static color rings.
- No entitlement tracking, coin shop purchasing, or multi-period aggregation existed.
- `Routes.RANKING` was wired to bottom-bar and shortcut components in `GameLobbyScreen`, `VoiceRoomListScreen`, and `TaskModels`, but lacked deep navigation, category-specific CTAs, and dynamic profile synchronization.

---

## 2. Existing Codebase Audit

### 2.1 Navigation & Routing Contracts
- **Route Definition:** `Routes.RANKING` (`"tab_ranking"`) in `app/src/main/java/com/sundram/weplay/ui/nav/WePlayBottomBar.kt:78`.
- **Entry Points:**
  1. `GameLobbyScreen.kt:212` — Quick shortcut item `VyboraShortcutItem("Ranks", "Leaderboard", R.drawable.vb_shortcut_ranking, Routes.RANKING)`.
  2. `VoiceRoomListScreen.kt:172` — Voice Lounge explore header `onExploreClick = { nav.navigateSafe(Routes.RANKING) }`.
  3. `TaskModels.kt:67` — Daily quest `"Check Today's Rankings"` pointing to `Routes.RANKING`.
- **Destination Pathways:**
  - `Routes.userDetailRoute(id)` (`"profile/detail/{userId}"`)
  - `Routes.TOPUP` (`"profile/topup"`)
  - `Routes.ROOM` (`"tab_room"`)
  - `Routes.MSG` (`"tab_msg"`)
  - `Routes.FAMILY` (`"profile/family"`)
  - `Routes.HUB` (`"tab_hub"`)

### 2.2 Profile & Cosmetic Data Structures
- In `app/src/main/java/com/sundram/weplay/data/UserProfile.kt`:
  - `UserProfile.frame: String?` already existed in the data model and was persisted into SharedPreferences (`"frame"`), but had no rendering pipeline or catalog validation.
  - `UserAvatar` in `Common.kt:190` supported only a static border `ring: Color?`.
- In `FamilyScreen.kt`:
  - Roster members showed static initials without frame customization.
- In `MeScreen.kt`:
  - Profile header rendered a plain avatar without equipped frame overlays.

### 2.3 Backend & Schema State
- Initial migrations:
  - `001_init.sql`: Users, profiles, rooms, chat messages.
  - `002_economy.sql`: Wallets, coin transactions, Google Play billing receipts.
  - `003_family.sql`: Guilds, member roles, treasuries, weekly quests.
- Gap identified: No database tables existed for `frame_catalog`, `frame_entitlements`, `frame_equipment`, `ranking_categories`, `ranking_score_events`, or `ranking_snapshots`.

---

## 3. Video Reference (`111398.mp4`) Audit & Gap Analysis

1. **Category Structure:**
   - The reference demonstrates 6 ranking tabs: Popularity, VIP, Couple, Room, BFF, and Family.
   - Each category displays distinct thematic colors and sub-period filters (Today, Yesterday, Weekly, Total, Celebrity, Annual, Global).
2. **Podium Presentation:**
   - The top three champions are displayed on elevated podium pedestals with crowns, numbered crests, framed portraits, and score counts. Rank 1 is center-prominent.
3. **List Ranks:**
   - Ranks 4+ are presented in a clean vertical list with rank numbers, framed portraits, badges, level chips, and scores.
4. **Bottom Bar:**
   - A sticky current-user ranking summary provides immediate personal standing and a direct contextual call to action.
5. **Frame Effects:**
   - The video shows glowing, ornate frames. However, the exact unlock mechanisms, prices, formulas, and asset files are proprietary.
   - **Strategy:** Build our own original, server-authoritative frame catalog and high-performance native Jetpack Compose Canvas animation engine.

---

## 4. Architectural Decisions

1. **Original Visual Identity:**
   - Preserve our established **White + Soft Lavender + Cyan + Soft Gold** design direction (`#FAF9FF` canvas, `#FFFFFF` cards, `#26233C` ink).
2. **Asset Pipeline:**
   - Store all 30 source SVGs under `app/src/main/assets/leaderboard/svg/`.
   - Convert all 30 SVGs to valid Android VectorDrawables in `app/src/main/res/drawable/` (`lb_*.xml`) for reliable XML and Compose painter rendering.
3. **Native Compose Canvas Motion:**
   - Implement `AvatarWithFrame` using Canvas drawing with `rememberInfiniteTransition()`:
     - COMMON: Static dual ring.
     - RARE: 2200ms cyan alpha breathing pulse (0.65f..1.0f).
     - EPIC: 4200ms clockwise rotating particles (8 orbit dots).
     - LEGENDARY: 5600ms counter-rotating dual orbital bands with 12 sparkling stars.
     - MYTHIC: 6400ms slow floating gold crown + 3 orbiting jewels.
     - CELESTIAL: 8000ms sweep aurora gradient + orbiting celestial gems.
   - Restrain animations when `animated = false` or size < 52dp to maintain 60/120fps scrolling performance.
4. **Server Authoritative Transactions:**
   - Create migration `004_leaderboards_and_frames.sql` and REST API handlers in `server/social/leaderboard.mjs` to ensure anti-cheat score validation and verified Google Play / coin purchases.
