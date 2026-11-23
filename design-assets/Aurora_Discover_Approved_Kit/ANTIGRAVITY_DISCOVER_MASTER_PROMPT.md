# AURORA PARADISE 3.0 — DISCOVER COMMUNITY HUB
## FINAL ANTIGRAVITY / CLAUDE CODE IMPLEMENTATION MASTER PROMPT

### Context and visual source of truth

You are a Principal Android/Kotlin engineer, expert Jetpack Compose UI architect, premium social-app designer, accessibility and QA lead. Work inside the EXISTING native project **Anticlocker/tempplay**, package `com.sundram.weplay`. The application name is **not finalized**; never introduce a brand name. **Do not rebuild in Flutter, Lovable, React Native or WebView.**

I supplied the approved Discover reference `previews/APPROVED_DISCOVER_LAYOUT.jpg` and the asset folder `Aurora_Discover_Approved_Kit`. This is a **WePlay-inspired layout direction only**; retain original app artwork and style. Do not copy WePlay-specific branding, proprietary artwork or assets. The **approved arrangement** is: restrained moonlit palace header → Community Moments horizontal media strip → 4 quick-access tiles Families, Nearby, Clubs & Spaces, Live Events → compact Community Activity (2–3 rows) → larger Moments feed/media previews → fixed shared five-tab bottom navigation.

### Read before touching source

1. Read `README.md`, `docs/SOURCE_MAPPING.md`, `docs/ASSET_MANIFEST.json`, `docs/COLOR_TOKENS.json`, `docs/QA_CHECKLIST.md` and this entire prompt.
2. Inspect `app/src/main/java/com/sundram/weplay/ui/screens/DiscoverScreen.kt`, `ui/theme/AuroraParadise.kt` (especially `AuroraDiscoverBackground`), `ui/nav/WePlayBottomBar.kt`, `MainActivity.kt`, the current NavHost, and `platform/content/ContentContracts.kt` plus `LocalContentRepository.kt`.
3. Audit any newly added Moments, Nearby, Clubs/Spaces, events, post-composer, profile and family screens; source files may be newer than the ZIP snapshot. Reuse existing routes/repositories and avoid duplication.
4. Create a safe local Git checkpoint/branch or at minimum record uncommitted work. **Do not automatically commit or push**.
5. Record existing build/test baseline; preserve working functionality and all other tabs.

### Findings from the supplied repository snapshot (VERIFY CURRENT WORKSPACE)

- `DiscoverScreen.kt` displays `Games`, `Rooms`, `Nearby` in `ExploreRow` and uses very pale colors (`DeepMidnight` text and `AuroraSurface`) inconsistent with dark Aurora theme; this needs scoped replacement.
- `MomentsCard()` has an empty `bounceClick { }`: do not leave it inert.
- `Nearby` has a `route = null`, so it needs a truthful permission-aware implementation.
- `LocalContentRepository.discoverFeed()` returns hardcoded `FeedEvent` and `FeedPost` examples; `ContentContracts.kt` has no stable post IDs or media fields. Treat local feed as **DEMO_ONLY** until a real authorized backend supports it.
- Navigation has `Routes.FAMILY_DISCOVERY` and `Routes.EVENT_CENTER` available in the snapshot. Validate actual NavHost registration before navigating. `Clubs & Spaces` does not have a verified destination in the snapshot: do not invent an existing route.
- `WePlayBottomBar.kt` delegates to shared `AuroraNavigationDock`: reuse that component, preserving the five tabs and back-stack behavior. Do not insert another bottom bar inside Discover.

### Phase 1 — assets, original art, and Android integration

Import every `ap_discover_*.webp` asset from `android/app/src/main/res/drawable-nodpi/` into the EXISTING Android app module. Refer to **exact** file names in `docs/ASSET_MANIFEST.json`. Keep source SVGs in a design/source folder; SVG files are NOT Android VectorDrawables. All icons are original transparent WebP resources and need no additional image dependency to display with `painterResource`.

Primary artwork:
- `ap_discover_header_palace` — top artwork layer only. NO baked text or controls. Keep it behind header content with fade and appropriate `ContentScale.Crop`/`FillWidth` depending on viewport.
- `ap_discover_body_midnight` — full-body dark atmospheric background; can be replaced by Compose gradient for low memory if visually comparable.
- Four distinct shortcuts: `ap_discover_ic_families`, `ap_discover_ic_nearby`, `ap_discover_ic_clubs`, `ap_discover_ic_events`.
- Section icons `ap_discover_ic_moments`, `ap_discover_ic_activity`, `ap_discover_ic_community_star`, `ap_discover_ic_trending`.
- Avatar frame overlays (`ap_discover_frame_violet`, `_cyan`, `_gold`) go OVER real avatars; empty center is transparent. Do not treat frame as an entire person avatar.
- Other action icons, decorative glow, and `ap_discover_media_fallback` are only for appropriate state/empty use.

Never place `previews/APPROVED_DISCOVER_LAYOUT.jpg` or `previews/ACTUAL_ASSET_CATALOG.jpg` inside `res/drawable`. Those are review/reference images, not functional screens or components. Do not fill an actual Moments feed with generated people/stock media; show real user-uploaded media or truthful empty states.

### Phase 2 — shared, restrained theme (not an AI art poster)

Maintain Aurora Paradise 3.0's existing dark royal-violet identity, using semantic screen-specific tokens, high contrast, and restrained glow. Use colors from `COLOR_TOKENS.json`: deep indigo `#170B40`, elevated violet `#29164F`, lavender `#C8B2F4`, cyan `#54CFFF`, subtle gold `#FFD36B`, primary text `#FFF8FF`. Avoid low-contrast black labels on violet cards. Avoid dramatic full-card neon outlines everywhere. Cards should feel like premium native Android UI, not like a casino banner.

Header height should adapt to status-bar insets; visually target ~130–170dp of artwork/header rather than huge empty top spacing. Use a strong `Discover` title, one subtitle, and an unobtrusive palace vignette on the right. Do not duplicate the Android status bar inside Compose. Do not leave black bands above the background. Keep content navigation safe under the fixed bottom dock.

### Phase 3 — implement exact approved screen hierarchy

A. **Community Moments** (immediately after header)
- A small section heading with a functional `More` action.
- A horizontal `LazyRow` of rounded **real media thumbnails** from published, visible Moments, showing photos/video previews where data exists.
- Tap a thumbnail: open its actual post detail/media viewer; preserve playback/back handling. Add placeholder only for missing media, and a useful empty state when no posts exist.
- Preserve post privacy/visibility; do not render private posts publicly.
- If existing post models lack stable IDs/media, evolve them backward compatibly with defaults; avoid breaking callers.

B. **Four quick shortcuts** (below Moments)
- Exactly `Families`, `Nearby`, `Clubs & Spaces`, `Live Events`, in a responsive four-item row on wider phones; on compact phones horizontally scroll or use a 2x2 grid based on measured width (never clip the names).
- Use supplied illustrated transparent icons over native dark-violet glassy card plates. Text remains Compose text.
- Families → route via existing verified `Routes.FAMILY_DISCOVERY` (or current equivalent).
- Nearby → request appropriate location permission ONLY after user taps; explain usage, allow denial, do not access precise location by default; show consent-aware nearby discovery if backend/service implemented. If unavailable, show honest unavailable/empty state rather than fake nearby people.
- Clubs & Spaces → inspect latest repo; connect real route and membership workflow if exists. If absent, implement a minimal honest entry/state only when backed by functional data; otherwise clearly disabled/unavailable with explanation.
- Live Events → `Routes.EVENT_CENTER` if registered; display only actual event data/state when opened.

C. **Community Activity** (a compact feed)
- Show 2–3 most recent authorized updates rather than the old five very large separate cards.
- Present avatar, name, optional real level, action and relative timestamp in one quiet grouped translucent panel.
- `Today` / `See All` opens a functioning activity list, if available.
- Do not use false claims about money/gambling earnings, fake winners, invented player ranks or fake online status. The existing local examples must be labeled DEMO or replaced with real data.
- Rows should navigate to real referenced users, rooms, games, or family when target identifiers exist; otherwise no misleading action.

D. **Moments feed preview** (bottom)
- Use 2 larger media cards (or responsive horizontal carousel) with real post media where available; include author and actions when appropriate. Open post detail.
- Support existing create-post flow plus image attachment (Android Photo Picker + permissions and storage as needed). Do not claim image upload is successful until verified persisted.
- Preserve likes/comments and counts where implemented, avoiding fabricated numbers. Ensure optimistic UI changes reconcile with source-of-truth state.
- Offer a functional `Community` or `See All` action.

E. **Hashtags**
- Preserve existing `trending` data but do NOT place a large noisy hashtag row between Moments and shortcuts. Place a compact horizontal topics row after shortcuts or below Activity when data exists. Tapping a tag should filter/search when implemented; otherwise avoid inert chips.

### Phase 4 — data, routing, privacy, behavior

Respect the existing `DiscoverFeed`, `FeedPost`, `FeedEvent`, `ContentRepository`, and nav graph. Refactor `DiscoverContent` into small composables like `DiscoverHeroHeader`, `MomentsMediaRow`, `DiscoveryShortcutGrid`, `CommunityActivityPreview`, and `MomentsFeedPreview` but **keep API compatibility** with current callers. Extend `FeedPost` conservatively for `postId`, `mediaUri`, `createdAt`, visibility, and optional author metadata where needed; use defaults/mappers so local demos still compile.

Prefer actual authenticated repositories if present. The uploaded snapshot's `LocalContentRepository` is DEMO data; visually polish in demo mode but clearly mark mode in diagnostics and do not silently call it production. Use stable keys for `LazyColumn` and `LazyRow`. Handle offline, loading, empty, error, retry, location denied and content removed. Avoid duplicate NavControllers or navigation loops; restore scroll when switching tabs; keep the bottom dock fixed and unobstructed.

### Phase 5 — Compose performance, motion, accessibility

Use small, subtle animations: tab/click press 100–150ms, section reveal 180–240ms, post media fade 200–300ms. No full-screen shimmering particles or massive blur in lists. Keep continuous decoration paused offscreen, respect animator scale/reduced-motion, prevent overdraw, optimize background and bitmap decoding. Ensure sufficient text contrast and 48dp touch targets; handle small screens, dynamic fonts, landscape if supported, insets, keyboard, TalkBack descriptions, permissions and dark system bars. Avoid static fake status bars.

### Phase 6 — testing and approval gates

Run baseline and final tasks when environment supports them:

```powershell
.\gradlew.bat :app:assembleDebug
.\gradlew.bat :app:testDebugUnitTest
.\gradlew.bat :app:lintDebug
```

Test: all 4 shortcut taps and Back, Moments→detail→Back, post photos, create post image upload if supported, feed refresh, loading/error/empty states, 2-device visibility/privacy if backend available, location permission denied and granted, long text, offline mode, bottom navigation, scroll restore, no overlay touches, and no crashes. If build tools or network are unavailable, label tests `NOT_TESTED`, not passed.

Capture **actual Android emulator/device screenshots**: top (header+Moments), middle (shortcuts+Activity), bottom (Moments preview+fixed dock), plus compact 360dp viewport. Compare to `previews/APPROVED_DISCOVER_LAYOUT.jpg`: match STRUCTURE, visual hierarchy, spacing, colors, restraint, not literally every generated image in preview. Don't pass until title/text are legible and no sections overlap the dock.

### Final deliverables

Produce `docs/AURORA_DISCOVER_IMPLEMENTATION_REPORT.md` containing: source revision, modified Kotlin/XML files, asset imports, routes verified, working flows, fake/demo data identification, permissions, actual build/test command results, screenshots, remaining blockers. Produce `docs/DISCOVER_FUNCTIONAL_TEST_MATRIX.md` with `PASS/FAIL/PARTIAL/DEMO_ONLY/BLOCKED/NOT_TESTED`. Do not automatically commit or push.

**Execute the implementation now: audit → import → code → screenshot comparison → build → test → report. Do not stop after planning.**
