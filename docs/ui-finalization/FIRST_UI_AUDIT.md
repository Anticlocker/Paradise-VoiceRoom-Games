# Paradise — First Static & Architectural UI Audit

Baseline pre-finalization findings identified across `app/src/` before remediation:

## 1. Critical Blockers Fixed
1. **Six Baseline Android Lint Errors (`app:lintDebug`):**
   - `ProduceStateDoesNotAssignValue` in `AuroraParadise.kt` (line 874) and `DiscoverScreen.kt` (lines 733, 1274).
   - API-27 `android:windowLightNavigationBar` set in unversioned `values/themes.xml` and `values-night/themes.xml` without `values-v27/` resource qualification.
2. **Broken & Legacy Launcher / Branding Assets:**
   - `res/drawable/ic_launcher.xml` was malformed / non-adaptive.
   - `strings.xml` displayed legacy `"WePlay"` name instead of **Paradise - Voice Room & Games** (`app_short_name`: **Paradise**).
   - User-facing bio, achievement, privacy choices, settings footer, About copy, and support legal copy still contained `"WePlay"` / `"VYBORA"`.
3. **Raw Card Number & CVV Collection (`WalletSettingsScreen.kt`):**
   - `PaymentMethodsScreen` / `AddCardDialog` presented a form accepting 13–19 digit card numbers (`"4242424242424242"`) and CVV inputs (`"123"`). Removed completely and replaced with a Play Billing preview label selector.
4. **White / Pastel Theme Breakage & Invisible Text:**
   - `Theme.kt` mapped `LightPalette` with white surfaces against light-theme expectations while screens mix-and-matched dark Aurora cards and hardcoded `#FFFFFF` / `#FAF9FF` / `#FFF9E6` / `#FFECE5` containers, causing pale/invisible text (`SearchScreen`, `UserProfileDetailScreen`, `RankingScreen`, `TaskCenterScreen`, `TopUpScreen`, `GameRoomsScreen`, `CreateVoiceRoomScreen`, `FamilyScreen`, `GiftPackOffersSheet`).
   - `Settings` offered a half-wired Light/Dark toggle inside `GeneralSettingsScreen` despite Aurora Paradise 3.0 being an always-dark design language.
5. **Misleading & Duplicate Routes (`Routes` & `MainActivity.kt`):**
   - `Routes.VIP == Routes.TOPUP` (tapping VIP Center opened Coin Top-Up).
   - `Routes.SHOP == Routes.GIFTS` (tapping Shop opened Avatar Gifts).
   - `"All Games"` on Games Home routed to `Routes.GAME_ROOMS` instead of a full game catalog.
   - `MeScreen` had a stale alternate/duplicate `MeSettingsScreen` (`Routes.ME_SETTINGS`) with hardcoded `"Osama bin Moham..."`, `"0"`, `"4 d 4 h"`, and light `#F5F6FA` background.
   - `UserProfileDetailScreen` hardcoded `"Osama bin Mohamm"` and `"ID:225712899"` instead of resolving clicked players dynamically.
   - `Routes.AUTH`, `Routes.ONBOARDING`, `Routes.FEEDBACK`, `Routes.SPLASH`, and `Routes.FAMILY_*` subroutes lacked `NavHost` registrations.
   - Discover shortcuts (`Nearby`, `Clubs & Spaces`) opened transient dialogs instead of real destinations.
6. **Manifest & Asset Hygiene:**
   - `NearbyScreen` requested `ACCESS_COARSE_LOCATION` at runtime without `<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />` in `AndroidManifest.xml`.
   - Unused `res/drawable-nodpi/aurora3_tile_uno.png` created trademark risk (UNO®).
   - Three stray `.bak` files sat inside `ui/screens/`.

## 2. Preserved Visual Anchors (Verified Untouched)
- `GameLobbyScreen` `AuroraGamesBackground` (`ap3r_bg_games_palace`, `ap3r_overlay_sparkle_particles`).
- `MeScreen` Royal Night theme (`#10052D`..`#54CFFF`) and all 34 `ap_me_*` assets.
- `DiscoverScreen` simplified layout structure (no Moonlit Palace banner and no 4-card Community Moments strip).
- `LiveVoiceRoomScreen` multi-tier crest progression, 10 themes, and 64–66dp stage seat sizing.
