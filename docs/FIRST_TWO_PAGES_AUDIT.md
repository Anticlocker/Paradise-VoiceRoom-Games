# VYBORA — First Two Pages Code & Functional Audit
**Project:** VYBORA (`com.sundram.weplay`)
**Audited Screens:**
1. **Gaming / Home Page** (`GameLobbyScreen.kt` / `GamesHomeScreen`)
2. **Voice Lounge / Room Discovery Page** (`VoiceRoomListScreen.kt`)
**Audit Date:** 2026-11-20
**Auditor:** Principal Android Architect & Jetpack Compose QA Lead

---

## 1. Executive Architecture Review

### Architecture & Tech Stack:
- **Language & Runtime:** Kotlin 2.0.21, Jetpack Compose BOM 2024.12.01, Java 17 (Temurin).
- **Navigation:** Jetpack Navigation Compose `androidx.navigation:navigation-compose:2.8.5` with single `NavHost` hosted in `MainActivity.kt`.
- **UI Paradigm:** Declarative Compose with Material 3 + Custom VYBORA Design Tokens (`VyboraBg`, `VyboraSurface`, `VyboraPurple`, `VyboraLavender`, `VyboraCyan`, `VyboraGold`, `VyboraBorder`, `VyboraTextPrimary`, `VyboraTextSecondary`).
- **State Management:** Compose `rememberSaveable`, `derivedStateOf`, and reactive UI state passing.
- **Assets:** Fully native WebP illustrations located in `app/src/main/res/drawable-nodpi/` (72+ home assets + 26 voice room assets).

---

## 2. Interaction-by-Interaction Checklist: Gaming / Home Page

| # | Control / Interactive Element | Component / Composable | Target Action / Destination | Current State | State Preserved | Double-Tap Safe | Notes & Findings |
|---|---|---|---|---|---|---|---|
| 1 | **Profile Avatar** | `VyboraHomeHeader` | `Routes.ME` | PASS | Yes | Needs guard | Opens user profile screen. Needs `launchSingleTop` to prevent duplicate screens on rapid taps. |
| 2 | **Coin Balance Capsule** | `VyboraHomeHeader` | `Routes.TOPUP` | PASS | Yes | Needs guard | Opens wallet top-up interface. Needs `launchSingleTop`. |
| 3 | **Notification Bell** | `VyboraHomeHeader` | `Routes.NOTIFICATIONS` | PASS | Yes | Needs guard | Opens notifications screen (`NotificationsScreen.kt`). |
| 4 | **Events Shortcut** | `VyboraHomeHeader` | `Routes.DISCOVER` | PASS | Yes | Yes (tab switch) | Switches to Discover tab. |
| 5 | **Search Games Toggle** | `GamesHomeScreen` | In-screen game search | PENDING | Yes | Yes | Currently missing dedicated games search bar on Home screen; adding in Phase 3. |
| 6 | **Leaderboard Tile** | `VyboraShortcutsRow` | `Routes.RANKING` | PASS | Yes | Needs guard | Opens `RankingScreen.kt`. |
| 7 | **Tasks Tile** | `VyboraShortcutsRow` | `Routes.ME` | PASS | Yes | Needs guard | Routes to user tasks in profile. |
| 8 | **Friends Tile** | `VyboraShortcutsRow` | `Routes.MSG` | PASS | Yes | Yes (tab switch) | Opens social messages tab. |
| 9 | **VIP Lounge Tile** | `VyboraShortcutsRow` | `Routes.ROOM` | PASS | Yes | Yes (tab switch) | Switches to Voice Lounge discovery. |
| 10 | **Game Rooms Banner** | `VyboraGameRoomsBanner` | `Routes.GAME_ROOMS` | PASS | Yes | Needs guard | Opens `GameRoomsScreen.kt` for multiplayer lobbies. |
| 11 | **"All Games ›" Action** | `GamesHomeScreen` | `Routes.GAME_ROOMS` | PASS | Yes | Needs guard | Text button opening game lobbies. |
| 12 | **Hero: Petrol Pump Card** | `HeroFeaturedGameCard` | Play action (`comingSoon`) | DEMO_ONLY | N/A | Yes | Displays snackbar with honest preview note. Adding micro-interaction press scale. |
| 13 | **Popular 1: Who's the Spy** | `PopularGamesRow` | `Routes.SPY` | PASS (LOCAL) | Yes | Needs guard | Fully playable local party game pass & play. |
| 14 | **Popular 2: Paint to Hide** | `PopularGamesRow` | Play action (`comingSoon`) | DEMO_ONLY | N/A | Yes | Displays snackbar with honest preview note. Adding micro-interaction press scale. |
| 15 | **Grid: Trickster's Cafe** | `VyboraGameGridCard` | Play action (`comingSoon`) | DEMO_ONLY | N/A | Yes | Card bluffing game card. |
| 16 | **Grid: Hide And Seek** | `VyboraGameGridCard` | Play action (`comingSoon`) | DEMO_ONLY | N/A | Yes | Prop hunt game card. |
| 17 | **Grid: Space Werewolf** | `VyboraGameGridCard` | Play action (`comingSoon`) | DEMO_ONLY | N/A | Yes | Impostor game card. |
| 18 | **Grid: Oh My Card** | `VyboraGameGridCard` | Play action (`comingSoon`) | DEMO_ONLY | N/A | Yes | Card matching game card. |
| 19 | **Grid: Ludo Classic** | `VyboraGameGridCard` | `Routes.LUDO` | PASS (LOCAL) | Yes | Needs guard | Fully playable local royal board game. |
| 20 | **Grid: Billiards Lounge** | `VyboraGameGridCard` | Play action (`comingSoon`) | DEMO_ONLY | N/A | Yes | 8-Ball pool card. Image needs rounded clipping. |
| 21 | **Community Feed Items** | `ExploreFeedItem` | `Routes.ROOM` | PASS | Yes | Yes | Navigates to voice lounge. |
| 22 | **Floating Gift Pack** | Floating Box | Offers Bottom Sheet | NEEDS FIX | Yes | Yes | Was navigating to `Routes.GIFTS`; needs modal bottom sheet matching Phase 7. |
| 23 | **Back to Top Pill** | AnimatedVisibility Box | Scroll to index 0 | PASS | Yes | Yes | Smooth scroll animation with coroutine scope. |

---

## 3. Interaction-by-Interaction Checklist: Voice Lounge Screen

| # | Control / Interactive Element | Component / Composable | Target Action / Destination | Current State | State Preserved | Double-Tap Safe | Notes & Findings |
|---|---|---|---|---|---|---|---|
| 24 | **Search Toggle Button** | `VoiceLoungeHeader` | Toggle `isSearchActive` | PASS | Yes | Yes | Expands/collapses search input bar. |
| 25 | **Create Room Button (+)** | `VoiceLoungeHeader` | `Routes.CREATE_ROOM` | PASS | Yes | Needs guard | Opens room creation sheet (`CreateVoiceRoomScreen.kt`). |
| 26 | **Search Query Input** | `VoiceSearchBar` | Filter rooms live | PASS | Yes | Yes | Filters by title, host, tag, activityText. |
| 27 | **Search Clear Button (X)** | `VoiceSearchBar` | Reset query to `""` | PASS | Yes | Yes | Clears query without closing bar. |
| 28 | **Search Cancel Button** | `VoiceSearchBar` | Dismiss search & reset | PASS | Yes | Yes | Closes bar, clears focus, resets query. |
| 29 | **Pill: Discover** | `LoungeNavPills` | Filter rooms by category | PASS | Yes | Yes | Shows all category-matched rooms. |
| 30 | **Pill: Trending** | `LoungeNavPills` | Sort rooms by members | PASS | Yes | Yes | Sorts descending by live member count. |
| 31 | **Pill: Following** | `LoungeNavPills` | Show followed hosts | PASS | Yes | Yes | Honest empty state with "Explore Live Rooms". |
| 32 | **Following "Explore" CTA** | `FollowingEmptyState` | Switch to "Discover" tab | PASS | Yes | Yes | Resets tab to Discover. |
| 33 | **Empty State "Reset" CTA** | `LoungeEmptyRoomsState` | Reset category & search | PASS | Yes | Yes | Resets category to "All" and clears search. |
| 34 | **Spotlight Card (Cover)** | `TonightsSpotlightCard` | `Routes.RANKING` | PASS | Yes | Needs guard | Explores highlighted featured hosts. |
| 35 | **Category Chips (7 items)** | `CategoryChipRow` | Filter by category | PASS | Yes | Yes | All, Friends, PK, Music, Video, Game, Chats. |
| 36 | **Room Card Row** | `VyboraRoomCard` | `Routes.roomRoute(id)` | PASS | Yes | Needs guard | Navigates to live voice room. |
| 37 | **Room Card "Join" Pill** | `VyboraRoomCard` | `Routes.roomRoute(id)` | PASS | Yes | Needs guard | Secondary join CTA inside room card. |
| 38 | **Voice Gift Pack Widget** | `CompactGiftPackWidget` | Open `VoiceGiftPackSheet` | PASS | Yes | Yes | Modal bottom sheet with perks and preview tag. |
| 39 | **Voice Gift Sheet "Got it"** | `VoiceGiftPackSheet` | Dismiss sheet | PASS | Yes | Yes | Dismisses bottom sheet cleanly. |

---

## 4. Interaction-by-Interaction Checklist: Shared Bottom Navigation

| # | Tab Destination | Icon & Label | Target Route | Current State | State Preserved | Double-Tap Safe | Notes & Findings |
|---|---|---|---|---|---|---|---|
| 40 | **WePlay / Games** | `vb_nav_games_on/off` | `Routes.HUB` | PASS | Yes | Yes | `launchSingleTop = true`, `saveState = true`. |
| 41 | **Voice Room** | `vb_nav_voice_on/off` | `Routes.ROOM` | PASS | Yes | Yes | `launchSingleTop = true`, `saveState = true`. |
| 42 | **Chats** | `vb_nav_chats_on/off` | `Routes.MSG` | PASS | Yes | Yes | Unread badge dynamically bound. |
| 43 | **Discover** | `vb_nav_discover_on/off` | `Routes.DISCOVER` | PASS | Yes | Yes | Red dot notification indicator. |
| 44 | **Me** | `vb_nav_me_on/off` | `Routes.ME` | PASS | Yes | Yes | Red dot notification indicator. |

---

## 5. Identified Defects & Polish Opportunities

1. **Game Card Edge Anti-Aliasing (Phase 2):**
   - High-contrast gradient cards (`HeroFeaturedGameCard` and `PopularGamesRow`) need subtle 1dp border strokes (`Color.White.copy(alpha = 0.2f)`) along their `RoundedCornerShape` paths to prevent subpixel staircasing on high-density displays.
   - `VyboraGameGridCard` used `vb_room_cover_billiards_lounge` without rounded clipping; full image covers must be clipped with `RoundedCornerShape(14.dp)` to prevent sharp 90° corners from protruding.
2. **Interactive Touch Feedback (Phase 2 & Phase 9):**
   - Game cards, action tiles, and room cards lacked spring-physics press scale feedback. Adding `bounceClick` with `0.97f` scale on press provides instant, satisfying haptic feel.
3. **Double-Tap / Re-entrant Navigation Protection (Phase 3 & Phase 5):**
   - Direct `nav.navigate(route)` calls can push duplicate destinations if tapped in rapid succession. A `navigateSingleTop(route)` extension eliminates duplicate entries.
4. **Game Search Feature (Phase 3):**
   - Currently, search is implemented on the Voice Lounge page, but the Home page lacks a dedicated in-screen game filter bar. Adding an interactive game search bar that filters featured, popular, and grid games live with clear/cancel and empty state.
5. **Gift Pack Modal Unification (Phase 7):**
   - On the Home screen, the floating Gift Pack navigated to `Routes.GIFTS` directly. It should open a dedicated `HomeGiftPackSheet` modal bottom sheet with game perks and an honest preview disclaimer, matching the Voice Lounge behavior.
