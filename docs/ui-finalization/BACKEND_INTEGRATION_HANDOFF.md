# Paradise — Backend Integration Handoff Guide

Maps every finalized Android frontend surface and local repository to the existing Node/PostgreSQL backend in `server/` and the LiveKit token server (`server/token-server.mjs`).

## 1. Existing Backend Inventory (Already in `server/`)
- **HTTP Server Entry:** `server/server.mjs` (default `PORT = 8080`, requires `DATABASE_URL`)
  - `/identity` (`server/identity/routes.mjs`, `PUBLIC_PATHS`: `/identity/signup`, `/identity/signin`, token resolver)
  - `/economy` (`server/economy/routes.mjs` — wallet, ledger, virtual gift sending, coin top-up catalog)
  - `/social` (`server/social/routes.mjs` — friend requests `/requests`, `/friends`, `/blocks`, `/reports`; `family.mjs` & `leaderboard.mjs` modules also reside in `server/social/`)
  - `/messaging` (`server/messaging/routes.mjs` — DM conversations & gift reference links; never mutates coin balance directly)
  - `/notifications` (`server/notifications/routes.mjs` — social/activity/system inbox)
  - `/rooms` (`server/rooms/routes.mjs` — persistent voice room metadata, room passcodes/tags)
  - `/room-themes` (`server/room_themes/routes.mjs` — 10-theme catalog, entitlements, equipped room theme & crest progression)
  - `/support` (`server/support/routes.mjs` — FAQ/legal/bug reports)
  - `/healthz` — health check
- **Database Schema:** `server/db/migrations/`
  - `001_init.sql` (identity & core)
  - `002_economy.sql` (wallet & catalog)
  - `003_family.sql` (families)
  - `004_leaderboards_and_frames.sql` (rankings & frames)
  - `005_room_themes_and_crest.sql` (room themes & crest progress)
- **LiveKit Token Minter:** `server/token-server.mjs` (`PORT = 8081`, `/voice/token`, validates `x-weplay-token-key` header)

## 2. Client Contract & Wiring Status (`app/src/main/java/com/sundram/weplay/`)
- **Already Live-Wired:**
  - `VoiceRoomEngineFactory` switches automatically from `DemoVoiceRoomEngine` to `LiveKitVoiceRoomEngine` when both `livekit.url` and `livekit.token.url` are supplied in `local.properties`.
  - `BuildConfig.API_BASE_URL` reads `api.base.url` from `local.properties` into `AppConfig` (not yet connected to HTTP transport).
- **Platform Repositories Ready to Swap (`platform/`):**
  - `platform/auth/`: `AuthContracts.kt`, `LocalAuthRepository.kt`, `SessionStore.kt` -> Wire to `/identity/*`
  - `platform/profile/`: `ProfileContracts.kt`, `LocalProfileRepository.kt` -> Wire to `/identity/*` & `/social/*`
  - `platform/content/`: `ContentContracts.kt`, `LocalContentRepository.kt` -> Wire to `/messaging`, `/social`, `/rooms`, `/notifications`

## 3. Recommended Screen-by-Screen Connection Order
1. **Auth & Session Bearer Tokens (`AuthScreen.kt`, `SessionStore.kt`):** Connect `/identity/signup` and `/identity/signin`, store bearer tokens in EncryptedSharedPreferences/Keystore, and pass `Authorization: Bearer <token>` on all non-public endpoints.
2. **Wallet, Top-Up & Billing (`TopUpScreen.kt`, `WalletSettingsScreen.kt`):** Integrate Google Play Billing Library purchase tokens and verify receipts server-side in `/economy` before crediting coins. Keep coin ledger strictly server-authoritative.
3. **Voice Rooms & Themes (`VoiceRoomListScreen.kt`, `CreateVoiceRoomScreen.kt`, `LiveVoiceRoomScreen.kt`, `RoomThemeScreens.kt`):** Sync persistent rooms via `/rooms`, equipped themes/crests via `/room-themes`, and keep ephemeral seat/mic/speaking state in LiveKit SFU.
4. **Social Graph, Messaging & Moderation (`MessagesScreen.kt`, `ChatScreen.kt`, `DiscoverScreen.kt`, `UserProfileDetailScreen.kt`, `PrivacySettingsScreen.kt`):** Connect `/social/*` (friend requests, blocks, abuse reports), `/messaging/*`, `/notifications/*`, and `/support/*`.
5. **Family, Ranking, Events, VIP, Shop, Nearby & Clubs:** Mount/expose HTTP endpoints over migrations `003`/`004`, add server-side Play Billing verification for VIP tiers, and gate `NearbyScreen` geolocation queries on explicit opt-in coarse coordinates (`ACCESS_COARSE_LOCATION`).
