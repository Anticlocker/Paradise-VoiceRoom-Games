# Vybora — Functional Implementation Roadmap
**Phase 0 Baseline · Generated: 2026-11-20**

This document establishes the phased engineering roadmap to transition VYBORA from a prototype Compose UI to a feature-complete, interactive, and resilient social gaming platform.

---

## MILESTONE 0 — Existing Project Verification & Audit (COMPLETED)
- [x] Scan and audit all Gradle files, Manifest, and source files.
- [x] Map the complete route hierarchy and bottom navigation tabs.
- [x] Document route inventory and orphan components in `docs/FUNCTIONAL_AUDIT.md`.
- [x] Create comprehensive `docs/FUNCTIONALITY_MATRIX.md` with action-level granularity.
- [x] Document screen transitions in `docs/SCREEN_NAVIGATION_MAP.md`.
- [x] Catalogue defects in `docs/BUG_TRACKER.md`.

---

## MILESTONE 1 — Critical UI Navigation & Interaction Repairs (CURRENT)
- [ ] Connect gift trigger in `LiveVoiceRoomScreen` to gift panel bottom sheet.
- [ ] Add exit-room confirmation dialog in `LiveVoiceRoomScreen`.
- [ ] Add interactive search in `VoiceRoomListScreen` and create dedicated `SearchScreen`.
- [ ] Fix hardcoded badge in `WePlayBottomBar`.
- [ ] Implement `NotificationsScreen` with dynamic category filters and route deep-linking.
- [ ] Add domain models (`GiftItem`, `NotificationItem`, `FamilyModel`, `LeaderboardEntry`) to `DemoData.kt`.
- [ ] Register new routes (`NOTIFICATIONS`, `SEARCH`, `GAME_ROOMS`) in `Routes` and `MainActivity`.

---

## MILESTONE 2 — Home, Explore & Game Discovery Engine
- [ ] Fix header actions in `GameLobbyScreen` (Profile, Wallet, Notifications, Search).
- [ ] Connect all game cards: Ludo (`Routes.LUDO`), Spy (`Routes.SPY`), and game room lobbies.
- [ ] Connect room cards in Explore feed to launch target voice rooms (`Routes.ROOM_DETAIL`).
- [ ] Implement `GameRoomsScreen` with filter tags (All, Casual, Games, KTV, Deduction).
- [ ] Provide clear [DEMO] indicators for games currently under construction.
- [ ] Add stable keys to all LazyColumn and LazyRow items in `GameLobbyScreen`.

---

## MILESTONE 3 — Voice Room Complete Experience
- [ ] Implement participant profile popup when tapping occupied speaker seats (View Profile, Message, Gift).
- [ ] Add room settings bottom sheet (mic permissions, room privacy, title).
- [ ] Implement visual request-to-speak and seat invitation transitions.
- [ ] Add interactive listener sheet when tapping listener pill.
- [ ] Add keyboard `Send` action handling in `VoiceRoomInputBar`.
- [ ] Verify LiveKit audio routing fallback to `DemoVoiceRoomEngine` in offline environments.

---

## MILESTONE 4 — Gifting System & Cinematic Overlays
- [ ] Build interactive `GiftTile` composable with multi-quantity selector (1x, 10x, 99x).
- [ ] Implement `GiftPack` offer sheet without forcing redirection to Top Up.
- [ ] Build `GiftAnimationOverlay` supporting micro (Tier 1), premium (Tier 2), and luxury (Tier 3) animations.
- [ ] Enforce lifecycle awareness and reduced-motion settings in gift animation controller.
- [ ] Keep wallet balance transactions local and strictly labeled as DEMO.

---

## MILESTONE 5 — Profiles, Badges & Social Progression
- [ ] Add animated `LevelProgressBar` and XP counters to `MeScreen`.
- [ ] Add stats bar (Followers, Following, Friends) with live counts.
- [ ] Build Badge Showcase and Gift Showcase carousels in `MeScreen`.
- [ ] Add multi-category tabs to `RankingScreen` (Wealth, Charm, Game, Family) with countdown timers.
- [ ] Wire friend relationship actions (Add Friend, Remove, Block, Report) with state updates.
- [ ] Add form validation and unsaved change confirmation to `ProfileEditScreen`.

---

## MILESTONE 6 — Family System & Community Workflows
- [ ] Expand `FamilyScreen` to support discovery, creation, and detail views.
- [ ] Build `FamilyBrowseSheet` with searchable family cards.
- [ ] Implement `FamilyCreateFlow` with name and description validation.
- [ ] Add family levels, member rosters, contribution points, and role badges (Leader, Deputy, Member).

---

## MILESTONE 7 — Local Playable Games
- [ ] Enhance "Who's the Spy" (`SpyScreen`): pass-and-play role reveal, secret word masking, voting timer, win determination.
- [ ] Enhance "Ludo" (`LudoScreen`): responsive board scaling, dice animation, token collision/capture rules, bot turns.
- [ ] Prototype "Draw & Guess": canvas drawing engine with color palette, stroke sizing, undo/clear, and guess validation.

---

## MILESTONE 8 — Supporting Infrastructure & Safety
- [ ] Fix `MediaRecorder` API level deprecation in `ChatScreen`.
- [ ] Implement location permission launcher in `DiscoverScreen` for Nearby Explore.
- [ ] Add Event Banners and event detail cards to `DiscoverScreen`.
- [ ] Audit all settings sub-screens to ensure persistence via DataStore / UserPrefs.
- [ ] Add reporting and blocking confirmation dialogs.

---

## MILESTONE 9 — Backend & Live Integration Readiness
- [ ] Abstract repository boundaries (`AuthRepository`, `RoomRepository`, `WalletRepository`).
- [ ] Prepare WebSockets / LiveKit signaling contracts for multiplayer and real-time voice.
- [ ] Prepare Google Play Billing client wrappers with server verification points.

---

## MILESTONE 10 — Production Hardening & Quality Assurance
- [ ] Enforce TalkBack accessibility (`contentDescription`) across all icon buttons.
- [ ] Profile recomposition hot paths and eliminate main-thread asset decoding.
- [ ] Test Android gesture navigation and multi-window resizing.
- [ ] Execute automated unit and UI navigation test suites.
