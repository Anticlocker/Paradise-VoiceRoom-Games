# Room Theme & Voice Room Backend Requirements

## 1. What Was Implemented on the Server (`server/`)
- **Migration:** [005_room_themes_and_crest.sql](file:///c:/Users/Admin/OneDrive/Desktop/final%20weplay/server/db/migrations/005_room_themes_and_crest.sql) adds additive tables cleanly after `004_leaderboards_and_frames.sql`:
  - `room_theme_catalog`
  - `room_crest_progress`
  - `room_crest_events` (unique `idempotency_key` deduplication)
  - `room_theme_entitlements` (room-level Crest unlocks)
  - `user_theme_entitlements` (account-level Store theme ownership)
  - `room_theme_equipment` (`CLASSIC_LOUNGE_ID` default `'classic_lounge'`)
  - `theme_purchase_orders` (audited Store orders linked to `transactions` + `ledger`)
- **Service & HTTP Routes:** [service.mjs](file:///c:/Users/Admin/OneDrive/Desktop/final%20weplay/server/room_themes/service.mjs), [routes.mjs](file:///c:/Users/Admin/OneDrive/Desktop/final%20weplay/server/room_themes/routes.mjs), [catalog.mjs](file:///c:/Users/Admin/OneDrive/Desktop/final%20weplay/server/room_themes/catalog.mjs), mounted at `/room-themes` in [server.mjs](file:///c:/Users/Admin/OneDrive/Desktop/final%20weplay/server/server.mjs):
  - `GET /room-themes/catalog?roomId=...`
  - `GET /room-themes/crest?roomId=...`
  - `POST /room-themes/equip` (host-only authorization + unlock validation)
  - `POST /room-themes/purchase` (locks `wallets FOR UPDATE`, rejects `price_coins == null` with `409 price_not_configured`, prevents double charges, writes `theme_purchase_orders` + `transactions` + `ledger`)
  - `POST /room-themes/crest/events` (deduped via `idempotency_key`, blocks `gift_value_received` when `actorId === host_id`, enforces `1200` daily XP cap, grants unlocked `room_theme_entitlements` without auto-equipping)

## 2. What Still Requires Live Server / Infrastructure Integration
The Android app currently uses `SharedPreferences`-backed local storage (`RoomThemeRepository`, `RoomCrestPreferencesStore`, `UserPrefs`) and `DemoVoiceRoomEngine` when `livekit.url` / `livekit.token.url` are unconfigured. The following pieces cannot execute against live remote infrastructure from the offline Android build alone:

1. **Android-to-API HTTP Client Wiring:** `AppConfig` has `api.base.url`, which is not yet wired to HTTP calls against `server/server.mjs` (`/identity`, `/rooms`, `/economy`, `/room-themes`). A Retrofit/OkHttp client carrying the bearer token is needed to sync created rooms, store theme purchases, wallet balances, and crest progress across devices.
2. **Pricing Configuration for Store Exclusives:** Both Store Exclusive themes (`sakura_garden`, `rose_palace`) ship with `priceCoins: null` in `theme_catalog.json` and `server/room_themes/catalog.mjs`. Both Android (`Purchase currently unavailable (price not configured)`) and Server (`409 price_not_configured`) block purchases safely until product pricing is configured.
3. **LiveKit & Multi-Device State Synchronization:**
   - Live audio transport requires configuring valid `livekit.url`, `livekit.token.url`, and `livekit.token.key` backed by [token-server.mjs](file:///c:/Users/Admin/OneDrive/Desktop/final%20weplay/server/token-server.mjs) and a running LiveKit SFU.
   - Multi-user real-time seat lock state, moderator promotion, server-side mute enforcement (`RoomServiceClient.mutePublishedTrack`), Attached Seat mutual-consent state, Live Dice RNG broadcasts, and live cross-room PK Battle scoring require server/LiveKit data-channel or WebSocket authority rather than local device state.
4. **PostgreSQL Instance for Integration Tests:** `server/room_themes/room_themes.pure.test.mjs` runs offline without Postgres, while full PostgreSQL integration tests (`npm --prefix server test` / `005_room_themes_and_crest.sql` application) require a running PostgreSQL 17 instance (`postgresql://weplay:weplay@127.0.0.1:54329/postgres`).
