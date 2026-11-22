# Discover 3.0 — Changelog

**Release:** Discover 3.0  
**Date:** 2026-11-22  
**App:** VYBORA (`com.sundram.weplay`)

---

## Changed Files

### `app/src/main/java/com/sundram/weplay/ui/screens/DiscoverScreen.kt`

**UI / Header**
- Removed `VYBORA 2.0` chip/badge from Discover header entirely. Header now shows only the `Discover` title and subtitle.
- Removed floating `Special Offer / 48h Flash Pack` button (DiscoverFloatingGiftPack composable, GiftPackOffersSheet, showGiftPackSheet state) from Discover screen. The component is not removed from other screens.
- Updated `Post` quick-action button icon: `vb_discover_create` → `dp_discover_post` (Discover 3.0 premium icon set).

**Icon replacements — all using `dp_discover_*` WebP assets (24 dp prefix icons)**
| Location | Old icon | New icon |
|---|---|---|
| Search bar | system icon | `dp_discover_search` |
| Clear/close button | system icon | `dp_discover_close` |
| Trending section header | `vb_discover_trending` | `dp_discover_trending` |
| Live Updates section header | `vb_discover_notification` | `dp_discover_notification` |
| Community Shortcuts — Families | `vb_discover_families` | `dp_discover_families` |
| Community Shortcuts — Nearby | `vb_discover_nearby` | `dp_discover_nearby` |
| Community Shortcuts — Clubs & Spaces | `vb_discover_clubs_spaces` | `dp_discover_clubs_spaces` |
| Community Shortcuts — Live Events | `vb_discover_live_events` | `dp_discover_live_events` |
| Moments Hero artwork | previous asset | `dp_discover_moments` |
| Post Like button | system icon | `dp_discover_like` (alpha 1.0 / 0.5 for liked state) |
| Post Comment button | system icon | `dp_discover_comment` (alpha 0.65) |
| Post Share button | system icon | `dp_discover_share` + "Share" label |
| Empty feed state | generic icon | `dp_discover_empty_feed` |
| Post header quick action | `vb_discover_create` | `dp_discover_post` |
| CreateMomentBottomSheet close | system icon | `dp_discover_close` |
| Image picker gallery button | — (new) | `dp_discover_gallery` |
| Image preview remove button | — (new) | `dp_discover_close` |

**Post Composer (`CreateMomentBottomSheet`) — image picker upgrade**
- Added `selectedImages: List<Uri>` state variable (max 5 images).
- Added `imagePickerLauncher` using `ActivityResultContracts.PickMultipleVisualMedia(maxItems = 5)` — Android Photo Picker API; no broad storage permission required.
- Added **Add Photos** button row below the text input that launches `PickVisualMediaRequest(ImageOnly)`.
- Added image count label: "Up to 5 images" or "N/5 selected".
- Added horizontal image preview strip (`LazyRow`) showing 72 dp thumbnails via `AsyncImage` (Coil) when images are selected.
- Each thumbnail has a remove ✕ button (top-right, 20 dp semi-transparent circle).
- Updated `canSubmit` condition: post is enabled when **text is non-blank OR at least one image is selected** (previously required text only).

---

### `app/src/main/java/com/sundram/weplay/data/DiscoverModels.kt`

- Added `InMemorySharedPreferences` test double (fully in-memory, thread-safe implementation of `SharedPreferences`).
- Updated `DiscoverStore` constructor to accept optional `customPrefs: SharedPreferences?` for dependency injection in unit tests.

---

### `app/src/main/res/drawable-nodpi/dp_discover_*.webp` (24 files — new)

All 24 premium WebP assets from the Discover 3.0 asset kit copied with `dp_` prefix to avoid conflicts with existing `vb_discover_*` assets:

`dp_discover_camera`, `dp_discover_close`, `dp_discover_clubs_spaces`, `dp_discover_comment`,
`dp_discover_edit`, `dp_discover_empty_feed`, `dp_discover_events_calendar`, `dp_discover_families`,
`dp_discover_filter`, `dp_discover_gallery`, `dp_discover_hashtag`, `dp_discover_like`,
`dp_discover_live_events`, `dp_discover_location`, `dp_discover_moments`, `dp_discover_nearby`,
`dp_discover_notification`, `dp_discover_post`, `dp_discover_privacy`, `dp_discover_profile`,
`dp_discover_search`, `dp_discover_share`, `dp_discover_trending`, `dp_discover_upload`

---

### `app/src/test/java/com/sundram/weplay/NovaDiscoverUnitTest.kt`

- Added 6 unit test suites covering `DiscoverStore` read/write, toggle operations, default values, asset inventory verification, and post creation logic.
- All 6 test suites pass (`testDebugUnitTest` BUILD SUCCESSFUL).

---

## Build Status

| Task | Result |
|---|---|
| `assembleDebug` | ✅ BUILD SUCCESSFUL (76.7 MB APK) |
| `testDebugUnitTest` | ✅ BUILD SUCCESSFUL (all tests pass) |
