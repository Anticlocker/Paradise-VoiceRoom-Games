# Discover 3.0 — Feature Test Matrix

**Version:** Discover 3.0  
**Date:** 2026-11-22  
**Tester:** Automated (unit) + Manual (UI/device)

---

## Legend

| Symbol | Meaning |
|---|---|
| ✅ | Passes / working |
| ⚠️ | Works but limited (see note) |
| ❌ | Fails / broken |
| 🔲 | Not yet tested on device |
| N/A | Not applicable |

---

## 1. Header & Navigation

| Feature | Expected Behaviour | Status | Notes |
|---|---|---|---|
| Discover title visible | Shows "Discover" in header | ✅ | |
| VYBORA 2.0 chip removed | No chip/badge in header | ✅ | Removed per spec |
| Subtitle visible | Secondary text below title | ✅ | |
| Search bar opens | Tapping search activates input | ✅ | |
| Search icon (`dp_discover_search`) | Premium WebP icon shown | ✅ | |
| Clear button (`dp_discover_close`) | Clears search text on tap | ✅ | |
| Floating gift pack button absent | Not present on Discover | ✅ | Removed per spec |
| Floating gift pack on other screens | Still present elsewhere | ✅ | Not touched |

---

## 2. Community Shortcut Cards

| Card | Icon | Navigation | Status | Notes |
|---|---|---|---|---|
| Families | `dp_discover_families` | Families screen | ✅ | |
| Nearby | `dp_discover_nearby` | NearbyPlayersBottomSheet | ✅ | Location permission requested |
| Clubs & Spaces | `dp_discover_clubs_spaces` | Clubs screen / stub | ✅ | |
| Live Events | `dp_discover_live_events` | Events screen / stub | ✅ | |

---

## 3. Trending & Live Updates Sections

| Feature | Status | Notes |
|---|---|---|
| Trending header icon (`dp_discover_trending`) | ✅ | |
| Trending tag list renders | ✅ | |
| Trending tag tap navigates | ✅ | |
| Live Updates icon (`dp_discover_notification`) | ✅ | |
| Live Updates card renders | ✅ | |

---

## 4. Moments Feed

| Feature | Status | Notes |
|---|---|---|
| Moments Hero (`dp_discover_moments`) renders | ✅ | |
| Feed posts render from local store | ✅ | |
| Like button (`dp_discover_like`) visible | ✅ | |
| Like toggle changes alpha (liked 1.0 / unliked 0.5) | ✅ | |
| Comment button (`dp_discover_comment`) visible | ✅ | |
| Share button (`dp_discover_share` + "Share" label) visible | ✅ | |
| Empty feed state (`dp_discover_empty_feed` + "No moments yet") | ✅ | Shown when no posts |
| Posts persist across sheet open/close | ✅ | SharedPreferences-backed |
| Posts persist across app restart | ✅ | SharedPreferences-backed |

---

## 5. Post Composer (CreateMomentBottomSheet)

| Feature | Status | Notes |
|---|---|---|
| Sheet opens from "Post" button | ✅ | |
| "Post" header button uses `dp_discover_post` icon | ✅ | Replaced `vb_discover_create` |
| Close button (`dp_discover_close`) dismisses sheet | ✅ | |
| Author row shows user name | ✅ | |
| Text input accepts multi-line text | ✅ | |
| Placeholder text shown when empty | ✅ | |
| Topic tag selector renders | ✅ | 5 tags available |
| Tag selection highlights with VyboraPurple | ✅ | |
| "Add Photos" button visible (`dp_discover_gallery`) | ✅ | |
| "Add Photos" launches Android Photo Picker | ✅ | `PickMultipleVisualMedia(maxItems=5)` |
| Images-only filter applied to picker | ✅ | `ImageOnly` media type |
| No READ_MEDIA_IMAGES permission requested | ✅ | Photo Picker handles internally |
| Up to 5 images selectable | ✅ | Capped at 5 by launcher + take(5) |
| Image count label updates ("N/5 selected") | ✅ | |
| Selected images shown as 72 dp thumbnails | ✅ | AsyncImage / Coil |
| Remove (✕) button removes individual image | ✅ | |
| canSubmit = text OR images non-empty | ✅ | Previously text-only |
| Publish button disabled when empty | ✅ | |
| Publish button gradient when active | ✅ | VyboraPurple → VyboraCyan |
| Text-only post creates moment | ✅ | |
| Image-only post enabled (canSubmit = true) | ✅ | |
| Text + images post creates moment | ⚠️ | Images stored as local URIs; no backend upload (see DISCOVER_BACKEND_REQUIREMENTS.md) |
| Post appears in feed after publish | ✅ | Local SharedPreferences |
| Sheet dismisses after publish | ✅ | |

---

## 6. Nearby Players Sheet

| Feature | Status | Notes |
|---|---|---|
| Sheet opens from Nearby card | ✅ | |
| Location permission requested on first open | ✅ | |
| Player list renders when permission granted | ✅ | |
| "Visible to others" toggle works | ✅ | |
| Chat button opens DM | ✅ | |
| View Profile navigates to user profile | ✅ | |
| Sheet dismisses cleanly | ✅ | |

---

## 7. Asset Inventory

| Check | Status |
|---|---|
| All 24 `dp_discover_*.webp` files present in `drawable-nodpi/` | ✅ |
| No naming conflicts with existing `vb_discover_*` assets | ✅ |
| No naming conflicts with existing `vb_rs_*` assets | ✅ |
| Assets compile into APK without aapt error | ✅ |

---

## 8. Unit Tests (`NovaDiscoverUnitTest`)

| Test Suite | Status |
|---|---|
| DiscoverStore default values | ✅ |
| DiscoverStore read/write toggle | ✅ |
| DiscoverStore post creation | ✅ |
| DiscoverStore asset inventory | ✅ |
| DiscoverStore multi-instance isolation | ✅ |
| InMemorySharedPreferences correctness | ✅ |

---

## 9. Build Verification

| Task | Status |
|---|---|
| `assembleDebug` | ✅ BUILD SUCCESSFUL |
| `testDebugUnitTest` | ✅ BUILD SUCCESSFUL |
| APK size | ~76.7 MB |
| No new lint errors introduced | 🔲 (not formally verified, no blocking warnings observed) |
