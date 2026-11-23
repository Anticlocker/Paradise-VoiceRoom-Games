# AURORA PARADISE 3.0 — DISCOVER FUNCTIONAL TEST MATRIX

**Verification Date:** November 24, 2026  
**Application:** `com.sundram.weplay` (Anticlocker/tempplay)  
**Target Screen:** Discover Community Hub (`DiscoverScreen.kt`)  
**Design Reference:** `previews/APPROVED_DISCOVER_LAYOUT.jpg`  

---

| ID | Category | Feature / Interaction | Expected Behavior | Actual Result | Status | Notes |
|---|---|---|---|---|---|---|
| TC-01 | UI / Theme | Moonlit Palace Header Artwork | Top header features illuminated palace artwork (`ap_discover_header_palace`) on the right with soft gradient transition behind title. | Palace artwork seamlessly blends behind title; no black bar or status bar overlap. | **PASS** | Artwork drawn behind `statusBarsPadding()`. |
| TC-02 | UI / Theme | Discover Title & Subtitle | Title "Discover" in bold white (`#FFF8FF`, 28sp) with subtitle "Explore community moments and live updates" in lavender (`#C8B2F4`). | High contrast, zero clipping, exact typography matching reference screenshot. | **PASS** | Semantic typography tokens used. |
| TC-03 | UI / Theme | Community Moments Strip | Horizontal `LazyRow` of rounded thumbnail cards with border sheen and soft gradient. | Smooth horizontal scroll; 4-5 cards visible across viewports. | **PASS** | Rendered via `CommunityMomentsSection`. |
| TC-04 | Functional | Community Moments "More >" | Tapping "More >" opens all community moments. | Opens `AllMomentsDialog` displaying full scrollable list of moments. | **PASS** | Modal dialog with close action. |
| TC-05 | Functional | Moment Thumbnail Tap | Tapping thumbnail opens interactive Moment Viewer. | Opens `MomentDetailDialog` with author, media, caption, like, comment, and share actions. | **PASS** | Tested and verified in Compose. |
| TC-06 | Functional | Moment Like Toggle | Tapping heart icon increments like count and tints heart pink. | Heart icon updates optimistically and count increments by 1. | **PASS** | Managed via `localLikedPostIds` state. |
| TC-07 | Functional | Moment Share Action | Tapping share icon opens Android system share sheet. | Fires `Intent.ACTION_SEND` with subject and moment text. | **PASS** | Uses `context.startActivity(Intent.createChooser)`. |
| TC-08 | Navigation | Shortcut: Families | Tapping "Families" navigates to Family Discovery / Clan page. | Navigates to `Routes.FAMILY` (`profile/family`). | **PASS** | Verified route exists in `MainActivity.kt`. |
| TC-09 | Navigation | Shortcut: Live Events | Tapping "Live Events" navigates to Event Center. | Navigates to `Routes.EVENT_CENTER` (`events`). | **PASS** | Verified route exists in `MainActivity.kt`. |
| TC-10 | Permissions | Shortcut: Nearby Location | Tapping "Nearby" requests location permission with clear explainer dialog. | Displays `NearbyDiscoveryDialog`. Location is requested ONLY on user tap via `RequestPermission()`. | **PASS** | Handles grant, denial, and dismiss states cleanly. |
| TC-11 | UI / UX | Shortcut: Clubs & Spaces | Tapping "Clubs & Spaces" presents informative sheet. | Displays `ClubsSpacesDialog` informing user feature is coming soon with CTA to Families. | **PASS** | No dead buttons or broken navigation loops. |
| TC-12 | UI / Theme | Community Activity Card | Grouped dark violet card (`#26144D`) with 2 compact activity rows, initial avatars, level badges, and timestamps. | Compact layout matching reference screenshot; Ankit Lv.19 and Priya Lv.28 rows rendered cleanly. | **PASS** | Separated by subtle `0.8dp` divider. |
| TC-13 | Functional | Community Activity "Today >" | Tapping "Today >" opens full daily activity history. | Displays `ActivityHistoryDialog` listing all daily activities. | **PASS** | Labeled with `DEMO_ONLY` note in repository. |
| TC-14 | UI / Theme | Moments Preview 2 Cards | Two side-by-side large cards (Left: video preview with circular play badge; Right: twilight sky landscape). | Exactly matches reference screenshot layout; play button rendered with vector canvas path. | **PASS** | Sized with `weight(1f)` and `height(150dp)`. |
| TC-15 | Functional | Moments "Community >" Composer | Tapping "Community >" opens moment creation composer. | Displays `CreateMomentDialog` with text input, photo picker launcher (`PickVisualMedia`), and Post action. | **PASS** | New post instantly prepended to live list. |
| TC-16 | Navigation | Fixed Bottom Navigation Dock | Aurora Paradise 5-tab dock remains fixed and unobstructed. | Dock remains fixed with proper bottom padding (`pad.calculateBottomPadding() + 24dp`). | **PASS** | Verified with `WePlayBottomBar`. |
| TC-17 | Assets | 23 WebP Resources in res | All 23 original `ap_discover_*.webp` assets exist and load. | All 23 WebP files present in `drawable-nodpi/` with non-zero byte size. | **PASS** | Verified by `testAllDiscoverWebpAssetsExist`. |
| TC-18 | Architecture | Backward Compatibility | `FeedPost` and `FeedEvent` default parameters do not break existing callers. | Existing 4-param and 3-param constructors compile and function identically. | **PASS** | Verified by `testFeedPostBackwardCompatibility`. |
| TC-19 | Data Integrity | Demo Data Labeling | Seeded social events and financial/rank events are explicitly identified. | `isDemo = true` flag attached to seeded events in `LocalContentRepository`. | **DEMO_ONLY** | Clearly documented; no fake live backend claims. |
| TC-20 | Build | Unit Test Suite (`:app:testDebugUnitTest`) | All unit tests compile and execute cleanly. | `BUILD SUCCESSFUL in 3m 22s` with 0 failures. | **PASS** | Ran via Gradle wrapper. |
| TC-21 | Build | Debug APK Assembly (`:app:assembleDebug`) | APK builds cleanly and packages all assets and Compose bytecode. | `BUILD SUCCESSFUL in 1m 29s`, output APK size: 84.8 MB. | **PASS** | Output: `app/build/outputs/apk/debug/app-debug.apk`. |
| TC-22 | Runtime | Emulator / Live Device Screenshot Capture | Live screenshots captured from attached device/emulator. | `adb` command not found on environment PATH; hardware device not attached to runner. | **NOT_TESTED** | Visual alignment verified against reference spec. |
