# Exact source integration map (uploaded tempplay-main.zip snapshot)

| Actual source / symbol | Change | Assets | Safety notes |
|---|---|---|---|
| `ui/screens/DiscoverScreen.kt` : `DiscoverScreen()` | Retain repository/UI-state integration, apply dark background and proper system/bottom insets | `ap_discover_body_midnight`, `ap_discover_header_palace` | Current workspace may differ |
| `ui/screens/DiscoverScreen.kt` : `DiscoverContent()` | Reorder sections to header → Community Moments media row → 4 shortcuts → activity preview → Moments preview | `ap_discover_ic_moments`, `ap_discover_ic_activity` | Use native Compose for cards and labels |
| `ui/screens/DiscoverScreen.kt` : `ExploreRow()`/`exploreCards` | Replace Games/Rooms/Nearby trio with Families/Nearby/Clubs & Spaces/Live Events | 4 `ap_discover_ic_*` icons | Inspect routes before navigate; no dead buttons |
| `ui/screens/DiscoverScreen.kt` : `MomentsCard()` | Replace empty click with real Moments navigation or clearly disabled/empty state; new horizontal real-post thumbnail row | `ap_discover_ic_community_star`, `ap_discover_media_fallback` | Existing callback empty |
| `ui/screens/DiscoverScreen.kt` : activity `items(events)` | Collapse old 5 large cards into 2–3 compact rows, see all when supported | `ap_discover_frame_violet`, `ap_discover_frame_cyan`, `ap_discover_frame_gold` | Existing data is demo |
| `ui/screens/DiscoverScreen.kt` : `items(posts)` | Move to larger media preview/actual post feed | `ap_discover_ic_gallery`, `ap_discover_ic_heart`, `ap_discover_ic_comment` | Existing FeedPost has no media fields |
| `platform/content/ContentContracts.kt` : `FeedPost`, `FeedEvent`, `DiscoverFeed` | Extend backward compatibly with IDs/media/links/pagination as required, preserve old construction | none | Do not break existing callers |
| `platform/content/LocalContentRepository.kt` | Mark seeded events/posts demo-only or replace with real repository | none | Do not represent fake social/financial claims as live |
| `ui/theme/AuroraParadise.kt` : `AuroraDiscoverBackground` | Use screen-specific restrained dark backdrop and optional header layer | both backdrops | Reuse current theme instead of replacing globally |
| `ui/nav/WePlayBottomBar.kt` : `WePlayBottomBar()` | Keep existing shared `AuroraNavigationDock` | do not import a second nav bar | Five tabs remain fixed |
| `ui/nav/Routes` | `FAMILY_DISCOVERY`, `EVENT_CENTER` appear in uploaded snapshot | none | Confirm actual NavHost graph registration |
| Navigation/feature not confirmed | Nearby, Clubs & Spaces, standalone Moments route and composer | location icon and feature icons | Implement real workflows if available; otherwise honest unavailable state |

**No source files were changed by this asset pack.**
