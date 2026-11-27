# Paradise — Google Play Store Readiness & Data Safety Assessment

Pre-submission readiness checklist for **Paradise - Voice Room & Games** (`com.paradise.play`, `versionCode = 1`, `versionName = "0.1.0"`, `minSdk = 24`, `targetSdk = 34`, `compileSdk = 35`).

## 1. Completed Readiness Controls (Frontend)
- **App Identity & Launcher:** Display name set to `"Paradise - Voice Room & Games"`, short launcher label `"Paradise"`, valid adaptive icons (`mipmap-anydpi-v26/ic_launcher*.xml`) and splash window background (`splash_brand_window.xml`).
- **Age Gate & Terms/Privacy Consent:** `AuthScreen` enforces 13+ DOB check on registration, displays interactive Terms of Service & Privacy Policy viewer + consent toggle before sign-in, and notes 18+ recommendation for public voice rooms.
- **User Safety & UGC Moderation UI:**
  - Block & Report flows in Voice Rooms, Chats, Discover Moments (report/hide & delete own post), `UserProfileScreen`, `UserProfileDetailScreen`, and `Settings > Privacy & Safety`.
  - Parental Control safety toggles persisted locally in `MeScreen` (`paradise_safety`).
- **Account Deletion Path:** `Settings > Account > Delete account` requires typing `DELETE` and wipes local account data via `clearLocalAccount(context)`.
- **No Raw Payment Card Collection:** Removed 16-digit PAN and CVV collection from `WalletSettingsScreen`; replaced with non-sensitive Play Billing preview selector.
- **Permissions Match Declaration:**
  - `INTERNET`, `ACCESS_NETWORK_STATE`, `RECORD_AUDIO`, `MODIFY_AUDIO_SETTINGS`, `VIBRATE`, `ACCESS_COARSE_LOCATION` (declared for Nearby runtime prompt).
- **Release Variant Signing Note:** Currently debug-signed in `app/build.gradle.kts` (`signingConfigs.getByName("debug")`) for local testing. **Play Upload Key / keystore + `isMinifyEnabled = true` must be configured before Play Console release upload.**

## 2. Draft Play Data Safety Summary (Local Preview Build)
- **Current Preview Mode (Offline / Local):** All profile, wallet ledger, messages, and moderation reports remain exclusively in on-device `SharedPreferences` and `filesDir`. No data leaves the device unless LiveKit voice SFU coordinates (`livekit.url`, `livekit.token.url`) are supplied in `local.properties`.
- **Production Backend Mode (When Connected):** Declare encrypted HTTPS/WSS transit, collection of account identifiers (name, username, optional email/phone), ephemeral audio streaming (LiveKit), user-generated text/photos, and Google Play Billing purchase tokens. Provide an external account-deletion web URL matching in-app deletion.

## 3. Draft Google Play Store Listing Copy
- **Title (30 chars max):** `Paradise - Voice Room & Games` (29 chars)
- **Short Description (80 chars max):** `Live voice rooms, party games, clubs & friends under a celestial night sky.` (75 chars)
- **Target Audience / Content Rating:** Teen / 13+ (UGC voice rooms, chat, simulated virtual gifts & coins — declare user-generated content, interactive audio, and in-app digital purchases).

## 4. Blocking Items Before Play Production Submission
1. Connect Play Billing Library (`com.android.billingclient:billing-ktx`) for real digital coin purchases (remove demo checkout toggle).
2. Wire live moderation backend + server-side CSAM/abuse escalation endpoints.
3. Replace procedural placeholder graphics in preview-only game tiles and finalize artist sign-off on any AI-generated assets (`ASSET_PROVENANCE.csv`).
4. Provision Play App Signing upload keystore and enable R8 (`isMinifyEnabled = true`, `isShrinkResources = true`).
5. Target Android API 35 (`targetSdk = 35`) if submitting after Google Play's August 2025/2026 annual target-API deadline.
