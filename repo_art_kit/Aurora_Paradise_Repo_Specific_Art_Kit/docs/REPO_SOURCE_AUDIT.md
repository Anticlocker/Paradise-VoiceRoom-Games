# Source-grounded audit — uploaded tempplay-main.zip

This file reflects the uploaded archive, not a verified live APK. It is distinct from the repository's self-reported implementation notes.

## Exact architecture

- App module: `app/`
- Package/namespace/applicationId: `com.sundram.weplay`
- Compose screens: `app/src/main/java/com/sundram/weplay/ui/screens/`
- Theme: `ui/theme/AuroraParadise.kt`, `ui/theme/Theme.kt`
- Main navigation: `MainActivity.kt`, `ui/nav/WePlayBottomBar.kt`
- Game lobby: `ui/screens/GameLobbyScreen.kt`
- Event models/catalog: `data/EventModels.kt`
- Event UI: `ui/screens/EventCenterScreen.kt` and `EventDetailScreen.kt`
- Gift offer definitions and 48-hour local rotation: `data/GiftPackModels.kt`
- Gift sheet: `ui/components/GiftPackOffersSheet.kt`
- LiveKit and demo engines: `voiceroom/LiveKitVoiceRoomEngine.kt`, `voiceroom/DemoVoiceRoomEngine.kt`, `VoiceRoomViewModel.kt`
- Backend Node modules: `server/` (identity, rooms, social, economy, messaging, notifications, support, DB)

## Verified visual gaps

1. `aurora3_bg_*.webp` are 720×1280 simple gradients/shapes and don't provide palace detail.
2. `aurora3_ic_coin.png` is 78×82 RGB, not transparent. `aurora3_gift_*.png` are ~125×97 RGB; `aurora3_tile_*.png` ~107×104 RGB. They appear to be crops with baked square backgrounds, unsuitable as transparent decorative icons.
3. Existing higher-resolution illustration resources `vb_character_*.webp` often exceed 400 pixels with usable alpha; prefer them over low-resolution cropped icons.
4. `AuroraParadise.kt` already contains shared background, wallet, gift and navigation components; reuse them, don't clone them.
5. `GameLobbyScreen.kt` passes `crystals = 850L` into `CelestialHeader`. Do not present this demo number as a real user wallet.
6. `data/EventModels.kt` uses static sample `participantCount`, task progress and time-based event definitions. These need a demo marker or verified feed before production.
7. `ui/nav/WePlayBottomBar.kt` has `Routes.VIP = TOPUP` and `Routes.SHOP = GIFTS`. A visual VIP/Shop redesign does not create dedicated feature navigation.
8. The app includes actual local/demo and LiveKit engine classes. Live service success is not proven by the archive; check runtime config and two-device connectivity.
9. `app/build.gradle.kts` injects `LIVEKIT_TOKEN_KEY` into BuildConfig from `local.properties`; if this is a secret, embedding it in the APK is not safe. Have backend handle authentication instead.
10. `README.md` still says offline/no backend, while the archive includes `server/` and LiveKit integration. Documentation is stale or service enablement is incomplete.
11. Old name strings remain in comments/internal identifiers (including VYBORA). Final app name is undecided, avoid adding any more brand-specific assets or user-facing labels.

## Tests executed in review environment

- ZIP integrity/extraction: passed.
- Image format/size/alpha inspection: passed for examined files.
- Server `node --test server/**/*.test.mjs`: attempted; 0/9 passed because `pg` package is missing in this offline environment (dependencies not installed). **This is not evidence that server tests would fail after npm install.**
- Android Gradle build: not executed. This environment lacks Android SDK and system Gradle; `gradle-wrapper.jar` is absent from the archive.
- Real-device, LiveKit and billing checks: not executed.

## Priority sequence

P0: Do not expose embedded token keys; ensure wallet/event state cannot be presented as verified.
P1: Source-mapped art upgrade, Games/Event/Gift checks, confirmed screenshot comparison.
P2: Connect real data and existing routes (VIP/Shop), voice testing.
P3: Roll out design to Discover, Chats, Me, Family, Ranking and subtler settings.
