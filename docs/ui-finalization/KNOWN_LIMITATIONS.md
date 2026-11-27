# Paradise — Known Frontend Limitations (Pre-Backend Integration)

Explicit disclosure of simulated or offline behaviors intentionally preserved prior to backend wiring:

1. **Offline-First Storage:** Profiles (`weplay_user`), friend/DM threads (`weplay_messaging`), coin wallet/top-up ledger (`weplay_topup`, `weplay_wallet`), tasks, family membership, VIP tier previews, shop items, and bug reports persist only in local Android `SharedPreferences` / `filesDir`. Clearing app data or deleting the local account erases them.
2. **Simulated Billing & Virtual Currency:** `TopUpScreen`, `VipCenterScreen`, `ShopScreen`, `GiftPackOffersSheet`, and `PaymentMethodsScreen` do NOT process real money or call Google Play Billing. Clear `"Preview"` / `"Simulated"` badges are displayed across all economy surfaces.
3. **Host Earnings & Payouts:** `HostEarningsScreen` displays preview UI and zero balance (`₹0.00`) with disabled withdraw CTA because creator KYC/payout APIs are not connected yet.
4. **LiveKit Voice Fallback:** Without `livekit.url` and `livekit.token.url` configured in `local.properties`, `VoiceRoomEngineFactory` selects `DemoVoiceRoomEngine` (local participant/mic simulation). Setting valid coordinates enables `LiveKitVoiceRoomEngine`.
5. **Nearby Discovery & Clubs:** `NearbyScreen` uses `ACCESS_COARSE_LOCATION` runtime permission state only to unlock local preview filters; no GPS coordinates are transmitted or matched against live users. `ClubsSpacesScreen` persists joined club IDs locally (`paradise_clubs`).
6. **Playable vs Preview Games:** `Ludo Classic` and `Who's the Spy` (`GameLobbyScreen` / `AllGamesScreen`) are locally playable (`Pass & Play` / offline bots). The remaining 7 catalog entries (`Petrol Pump Simulator`, `Paint to Hide`, `Trickster's Cards`, `Hide And Seek`, `Sci-Fi Impostor`, `Card Matching`, `Skyline Drift`) are explicitly labeled **Soon** / **Preview** with informative modal states.
7. **Auth Verification:** Phone/OTP uses demo code `123456`; Google/Email/Password flows create/authenticate local `ProfileStore` / `LocalAuthRepository` accounts.
8. **Release Signing & Minification:** `app/build.gradle.kts` signs `release` with `debug.keystore` and keeps `isMinifyEnabled = false` until Play App Signing upload credentials are provisioned.
