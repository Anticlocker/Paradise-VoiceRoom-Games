# Vybora — UI Architecture Audit
**Phase 0 · Completed: 2026-11-19**
**Auditor: Antigravity (Senior Android / Compose Architect)**

---

## 1. Project Identity

| Property | Value |
|---|---|
| Package | `com.sundram.weplay` |
| Application ID | `com.sundram.weplay` |
| Kotlin version | 2.0.21 |
| Compose BOM | 2024.12.01 |
| AGP | 8.7.3 |
| compileSdk | 35 |
| minSdk | 24 |
| targetSdk | 34 |
| Architecture | Single-Activity, Jetpack Navigation Compose |
| Theme | Material 3 + custom `WePlayPalette` CompositionLocal |
| Dark mode | Supported — user-switchable via `ThemeMode` + `UserPrefs` |
| LiveKit SDK | `io.livekit:livekit-android:2.30.0` (self-hosted SFU) |
| Coil | **Not installed** |
| Lottie | **Not installed** |
| Rive | **Not installed** |

---

## 2. Existing Navigation Architecture

```
MainActivity
└── WePlayTheme
    ├── AuthScreen (gate — shown when no stored profile)
    └── WePlayApp (Scaffold + NavHost)
        ├── Bottom tabs (visible on tab routes only)
        │   ├── HUB      → GameLobbyScreen       (alias GamesHomeScreen)
        │   ├── ROOM     → VoiceRoomListScreen
        │   ├── MSG      → MessagesScreen
        │   ├── DISCOVER → DiscoverScreen
        │   └── ME       → MeScreen
        └── Full-screen destinations (no bottom bar)
            ├── ROOM_DETAIL  → LiveVoiceRoomScreen
            ├── CHAT         → ChatScreen
            ├── USER         → UserProfileScreen
            ├── USER_DETAIL  → UserProfileDetailScreen
            ├── RANKING      → RankingScreen
            ├── ME_SETTINGS  → MeSettingsScreen
            ├── CREATE_ROOM  → CreateVoiceRoomScreen
            ├── LUDO         → LudoScreen
            ├── SPY          → SpyScreen
            ├── PROFILE_EDIT → ProfileEditScreen
            ├── GIFTS        → GiftsScreen
            ├── FAMILY       → FamilyScreen
            ├── BACKPACK     → BackpackScreen
            ├── TOPUP        → TopUpScreen
            ├── SETTINGS     → SettingsHomeScreen
            ├── ACCOUNT_SETTINGS / PRIVACY_SETTINGS / BLOCKED_USERS
            ├── REPORT_HISTORY / NOTIFICATION_SETTINGS / DND_SCHEDULE
            ├── VOICE_SETTINGS / GENERAL_SETTINGS / WALLET_SETTINGS
            ├── WALLET_TRANSACTIONS / PAYMENT_METHODS / HOST_EARNINGS
            ├── SUPPORT_SETTINGS / ABOUT
            └── (no RANKING / CREATE_ROOM hooked into bottom bar)
```

**Back-stack:** Standard NavHost `popUpTo` + `saveState` + `restoreState` on tab switches. ✅  
**Deeplinks:** None configured.  
**Route typos / duplicates:** None found.

---

## 3. Complete Screen Inventory

### 3A. Implemented Screens

| Screen File | Route | Status | Quality |
|---|---|---|---|
| `AuthScreen.kt` | (gate) | ✅ Functional | Fair — no splash, no onboarding flow |
| `GameLobbyScreen.kt` | `HUB` | ✅ Compiles | ⚠️ Poor — Canvas art is primitive; cartouche shapes partially implemented; no ViewModel |
| `VoiceRoomListScreen.kt` | `ROOM` | ✅ Functional | Fair — basic card list, needs visual upgrade |
| `LiveVoiceRoomScreen.kt` | `ROOM_DETAIL` | ✅ Functional | Good — real ViewModel, LiveKit wiring, animated mic glow |
| `DiscoverScreen.kt` | `DISCOVER` | ✅ Functional | Fair — `UiState` pattern correct, needs richer feed |
| `MessagesScreen.kt` | `MSG` | ✅ Functional | Fair — inbox + 1:1 chat works via `MessagingStore` |
| `ChatScreen.kt` | `CHAT` | ✅ Functional | Fair — MediaRecorder deprecation warning; audio recording UI present |
| `MeScreen.kt` | `ME` | ✅ Functional | Fair — profile display, logout; no level/badge/achievement sections |
| `ProfileScreens.kt` | multiple | ✅ Functional | Fair — edit profile, backpack, gifts, user detail |
| `RankingScreen.kt` | `RANKING` | ✅ Functional | Fair — static demo data, purple gradient header |
| `CreateVoiceRoomScreen.kt` | `CREATE_ROOM` | ✅ Exists | Needs review |
| `LudoScreen.kt` | `LUDO` | ✅ Functional | Full Ludo game with Canvas board |
| `SpyScreen.kt` | `SPY` | ✅ Functional | Full Who's the Spy game logic |
| `TopUpScreen.kt` | `TOPUP` | ✅ Exists | Coin top-up mock UI; no real billing |
| `WePlayHubScreen.kt` | (unused) | ⚠️ Orphan | Not mounted in NavHost; dead code |
| Settings screens (×11) | various | ✅ Functional | Thin but structurally complete |
| `MeSettingsScreen.kt` | `ME_SETTINGS` | ✅ Exists | Needs review |

### 3B. Missing / Not Implemented Screens

| Required Screen | Status |
|---|---|
| Splash screen | ❌ Missing |
| Onboarding / welcome flow | ❌ Missing |
| OTP verification UI | ❌ Missing |
| Avatar selection | ❌ Missing |
| Interest selection | ❌ Missing |
| Permission explanation screens | ❌ Missing |
| Game detail page | ❌ Missing |
| Game room lobby (pre-join) | ❌ Missing |
| Matchmaking UI | ❌ Missing |
| Game result / rewards screen | ❌ Missing |
| Gift panel / catalog | ⚠️ Stub only (GiftsScreen exists, no animation system) |
| Gift animation engine | ❌ Missing |
| Room participant profile popup | ❌ Missing |
| Seat invitation UI | ❌ Missing |
| Seat request UI | ❌ Missing |
| Room settings sheet | ❌ Missing |
| Exit room confirmation | ❌ Missing |
| Family discovery | ⚠️ Partial (FamilyScreen exists, basic) |
| Family creation | ❌ Missing |
| Family ranking | ❌ Missing |
| Leaderboard (Wealth / Charm tabs) | ⚠️ Partial (RankingScreen, one type only) |
| Notification center | ❌ Missing |
| Event banners / detail | ❌ Missing |
| Search (global) | ❌ Missing |
| User block/report UI | ⚠️ Referenced but incomplete |
| Home Explore feed | ⚠️ Present in GameLobbyScreen as static list |

---

## 4. Data Models

### Existing
```kotlin
UserProfile          // name, dob, id, photoPath, coins, level, family, frame, bio, gender, etc.
GameCard             // id, title, desc, tag, playable
RoomCard             // id, title, host, members, tag, live
ChatItem             // id, name, lastMsg, time, unread
RoomMemberVo         // identity, name, seat, micOn, speaking, isSelf
VoiceRoomState       // connection, members, messages, selfMicOn, seated
RoomMessage          // id, author, body, isSelf, system
```

### Missing Data Models (needed for Phase 1+)
```kotlin
GiftItem             // id, name, tier, coinCost, animationType
GiftEvent            // giftId, senderId, recipientId, timestamp
FamilyModel          // id, name, leader, members, level, xp
LeaderboardEntry     // rank, userId, name, score, tier
AchievementBadge     // id, label, iconRes, unlockedAt
EventModel           // id, title, startMs, endMs, rewardCoins
ExplorePost          // userId, name, activity, roomId, online
NotificationItem     // id, type, body, timestamp, read
```

---

## 5. UI Component Inventory

### Existing Reusable Components (`ui/components/`)

| Component | File | Quality |
|---|---|---|
| `InitialsAvatar` | Common.kt | ✅ Good — gradient fallback avatar |
| `UserAvatar` | Common.kt | ✅ Good — photo + initials fallback |
| `avatarGradient()` | Common.kt | ✅ Good |
| `tagColors()` | Common.kt | ✅ Good |
| `LivePill` | Common.kt | ✅ Good |
| `SectionHeader` | Common.kt | ✅ Good |
| `RoundIconButton` | Common.kt | ✅ Good |
| `PrimaryButton` | Common.kt | ✅ Good |
| `PlainField` | Common.kt | ✅ Good |
| `CoverThumb` | Common.kt | ✅ Good — room cover placeholder |
| `TagPill` | Common.kt | ✅ Good |
| `UnreadBadge` | Common.kt | ✅ Good |
| `ProfilePhotoPicker` | Common.kt | ✅ Good |
| `GameArtwork` | GameArt.kt | ⚠️ Fair — 8 Canvas drawings, rudimentary |
| `TopBackBar` | TopBackBar.kt | ✅ Good |
| `ProfileFields` | ProfileFields.kt | ✅ Good |
| `WePlayBottomBar` | WePlayBottomBar.kt | ✅ Good — labels now added |

### Missing Components (needed)
- `GiftTile` — gift grid item
- `GiftAnimationOverlay` — cinematic full-screen animation host
- `SpeakerSeatCard` — voice room seat
- `RoomCoverCard` — rich room discovery card
- `LevelProgressBar` — XP/level bar
- `BadgeRow` — achievement badge display
- `LeaderboardItem` — ranking row
- `EventBannerCard` — event promotion card
- `SearchBar` — global search input
- `EmptyStateFull` — full-page empty state with illustration
- `ShimmerBox` — loading skeleton

---

## 6. Theme Assessment

### Strengths ✅
- Full dual-theme (light/dark) system via `WePlayPalette` CompositionLocal
- Material 3 color scheme correctly wired
- Good semantic color naming (`TextMain`, `CardWhite`, `BgSoft`, etc.)
- `AvatarPalette` for deterministic avatar colors
- `ThemeMode` supports system/light/dark user override
- Edge-to-edge + `WindowCompat` correctly applied in `MainActivity`

### Gaps / Problems ⚠️
1. **Typography system is absent** — No `Typography` object defined; all screens use raw `fontSize = X.sp` literals. This makes global font scaling impossible.
2. **Spacing tokens are absent** — Hardcoded `dp` values everywhere; no spacing scale (4dp/8dp grid not enforced).
3. **Shape tokens are absent** — Corner radii hardcoded per-screen (8dp, 12dp, 16dp, 20dp, 24dp used inconsistently).
4. **Motion tokens are absent** — No `Easing`, `Duration`, or animation spec objects.
5. **Dark mode inconsistency** — `GameLobbyScreen` hardcodes `Color(0xFFFFFFFF)` background regardless of theme, breaking dark mode on the HUB tab.
6. **`WePlayHubScreen.kt`** is a dead file — not wired to any route but imports many dependencies.
7. **No `ContentDescription`** provided on interactive icon buttons across any screen (accessibility gap).
8. **Lottie / Rive not installed** — Prompt describes them as requirements. Need to add before Phase 4.
9. **Coil not installed** — Needed for remote image loading on room cards and user avatars.

---

## 7. Broken / Incomplete Interactions

| Screen | Issue | Severity |
|---|---|---|
| `GameLobbyScreen` | Canvas illustrations are primitive (simple circles and rects); art quality is below standard | High |
| `GameLobbyScreen` | Hardcodes white background — dark mode breaks this tab | High |
| `GameLobbyScreen` | `WePlayHubScreen.kt` is a dead/unused duplicate approach | Medium |
| `GameLobbyScreen` | Explore feed is static; no Compose ViewModel or data layer | Medium |
| `ChatScreen` | `MediaRecorder()` default constructor deprecated (API ≥ 31) | Medium |
| `ChatScreen` | Audio recording is UI-only; no actual recording pipeline | Low |
| `GiftsScreen` | No gift animation system; tapping gifts does nothing visual | High |
| `RankingScreen` | Hardcoded static data; no tab for Wealth vs Charm | Medium |
| `VoiceRoomListScreen` | Room cards have no cover artwork or host avatar | Medium |
| `LiveVoiceRoomScreen` | Gift panel button exists but opens no sheet | High |
| `LiveVoiceRoomScreen` | No exit confirmation dialog | Medium |
| `LiveVoiceRoomScreen` | No room settings sheet | Medium |
| `LiveVoiceRoomScreen` | Seat invitation / seat request UI absent | High |
| `DiscoverScreen` | Nearby section shows no actual location permission flow | Low |
| `MeScreen` | No VIP badge, achievement section, XP bar, or level frame | High |
| All screens | No content descriptions on icon buttons | Medium |
| All screens | No reduced-motion support | Medium |
| Bottom nav | Labels now show but were previously icon-only (recent edit) | Fixed |

---

## 8. Performance Risks

| Risk | Location | Impact |
|---|---|---|
| Infinite transition animations run while off-screen | `LiveVoiceRoomScreen` mic pulse | Medium |
| `drawWhoIsTheSpyCharacters` etc. runs on every recompose | `GameLobbyScreen` Canvas | Low |
| `remember { LocalContentRepository() }` creates repository on every recompose | `DiscoverScreen` | Medium |
| No `key` on `items()` in several lazy lists | `VoiceRoomListScreen`, others | Medium |
| `BitmapFactory.decodeFile` called in composable scope | `Common.kt` UserAvatar | High — blocks main thread |
| All room covers are placeholder gradient boxes | `VoiceRoomListScreen` | UX only |

---

## 9. Asset Inventory

### Drawable assets (all XML vector icons)
```
ic_back, ic_bag, ic_bell, ic_bell_off, ic_bolt, ic_calendar,
ic_camera, ic_check, ic_check_all, ic_chevron_right, ic_close,
ic_coin, ic_comment, ic_crown, ic_edit, ic_gift, ic_heart,
ic_info, ic_launcher, ic_logout, ic_mail, ic_mic, ic_mic_off,
ic_nav_discover, ic_nav_games, ic_nav_me, ic_nav_msg, ic_nav_room,
ic_pause, ic_phone, ic_pin, ic_play, ic_plus, ic_search,
ic_send, ic_settings, ic_star, ic_theme, ic_users, ic_verified
```

### Missing Assets
- No bitmap/WebP game cover artwork
- No gift item illustrations
- No achievement badge artwork
- No event banner artwork
- No animated Lottie files
- No animated Rive files
- No room background artwork
- No VIP frame / avatar border artwork

All game artwork is currently drawn at runtime via Compose Canvas DrawScope functions — acceptable approach but quality is low and not suitable for production.

---

## 10. Recommended Component Architecture (Target State)

```
ui/
  theme/
    Color.kt             ← existing; add Vybora dark tokens
    Theme.kt             ← existing
    Typography.kt        ← NEW — type scale
    Spacing.kt           ← NEW — spacing scale (4/8/12/16/24/32dp)
    Shape.kt             ← NEW — corner radius tokens
    Motion.kt            ← NEW — easing + duration tokens
    ThemeMode.kt         ← existing
  
  components/
    avatar/
      InitialsAvatar.kt  ← extract from Common.kt
      UserAvatar.kt      ← extract from Common.kt
      LevelFrame.kt      ← NEW
    badges/
      LivePill.kt        ← extract
      TagPill.kt         ← extract
      UnreadBadge.kt     ← extract
      VipBadge.kt        ← NEW
    buttons/
      PrimaryButton.kt   ← extract
      RoundIconButton.kt ← extract
    cards/
      RoomCoverCard.kt   ← NEW
      GameBannerCard.kt  ← refactor from GameLobbyScreen
      GridGameCard.kt    ← refactor from GameLobbyScreen
    gifts/
      GiftTile.kt        ← NEW
      GiftAnimationOverlay.kt ← NEW
      GiftAnimationQueue.kt   ← NEW
    input/
      PlainField.kt      ← extract
      SearchBar.kt       ← NEW
    layout/
      SectionHeader.kt   ← extract
      EmptyStateFull.kt  ← NEW
      ShimmerBox.kt      ← NEW
    voice/
      SpeakerSeat.kt     ← NEW (extracted from LiveVoiceRoomScreen)
      SeatGrid.kt        ← NEW
    rankings/
      LeaderboardItem.kt ← NEW
      LevelProgressBar.kt ← NEW
    Common.kt            ← existing; refactor over time
    GameArt.kt           ← existing; improve art quality
    ProfileFields.kt     ← existing
    TopBackBar.kt        ← existing

  nav/
    WePlayBottomBar.kt   ← existing
    Routes.kt            ← (currently in WePlayBottomBar; extract recommended)

  screens/
    auth/        AuthScreen.kt + sub-steps
    home/        GameLobbyScreen.kt + GamesHomeScreen
    voice/       VoiceRoomListScreen, LiveVoiceRoomScreen, CreateVoiceRoomScreen
    games/       LudoScreen, SpyScreen, (future game screens)
    messages/    MessagesScreen, ChatScreen
    discover/    DiscoverScreen
    profile/     MeScreen, ProfileScreens, UserProfileScreen, etc.
    ranking/     RankingScreen
    settings/    (existing settings/ subfolder — keep as-is)
    gifts/       GiftsScreen
    family/      FamilyScreen
    wallet/      TopUpScreen, WalletSettingsScreen, TransactionsScreen
```

---

## 11. Prioritized Implementation Roadmap

### Phase 1 — Design Foundation (Pre-condition for all other phases)
**Goal:** Establish the token system; unify theme; fix dark mode.

1. Add `Typography.kt` with a coherent type scale (Display → Label).
2. Add `Spacing.kt` (spacing scale constants).
3. Add `Shape.kt` (corner radius tokens).
4. Add `Motion.kt` (easing + duration tokens).
5. Extend `Color.kt` with Vybora dark tokens (deep midnight-purple palette).
6. Fix `GameLobbyScreen.kt` dark-mode background (use `PageWhite` / `BgSoft`).
7. Fix `BitmapFactory` main-thread call in `UserAvatar`.
8. Add `ContentDescription` to all icon-only buttons.
9. Add Coil dependency for remote image loading.
10. Add Lottie Compose dependency.
11. Add stable `key` lambdas to all lazy lists.

**Risk:** Low. No navigation changes.  
**Deliverable:** Every existing screen compiles and passes dark-mode visual check.

---

### Phase 2 — Home & Games Polish
**Goal:** `GameLobbyScreen` looks like a premium product.

1. Rewrite `GameLobbyScreen` using themed colors and new token system.
2. Improve all 15 Canvas game illustrations (richer proportions, shading).
3. Use `WePlayCartoucheShape` correctly for all banner + card borders.
4. Add `VoiceRoomListScreen` room cards with Coil-ready cover thumbnails.
5. Upgrade `DiscoverScreen` Explore feed cards (rich avatars, activity tags).
6. Wire `WePlayHubScreen.kt` or delete it.

**Risk:** Medium. `GameLobbyScreen` has been edited multiple times; read before writing.

---

### Phase 3 — Voice Room Experience
**Goal:** `LiveVoiceRoomScreen` feels premium and complete.

1. Extract `SpeakerSeat` and `SeatGrid` composables.
2. Add seat invitation / seat request bottom sheet.
3. Add exit-room confirmation dialog.
4. Add room settings bottom sheet.
5. Add participant profile popup.
6. Add floating emoji reaction overlay.
7. Upgrade `VoiceRoomListScreen` room discovery cards.
8. Fix `MediaRecorder` deprecation in `ChatScreen`.

**Risk:** Medium. Must preserve LiveKit ViewModel wiring.

---

### Phase 4 — Gifting System
**Goal:** Functional, visually spectacular gift experience.

1. Design `GiftItem` model with tier (1/2/3).
2. Build `GiftTile` composable.
3. Build gift catalog bottom sheet.
4. Build `GiftAnimationOverlay` using Compose animation APIs.
5. Build `GiftAnimationQueue` with priority and lifecycle.
6. Implement 3 animation tiers (micro / premium / luxury).
7. Wire gift button in `LiveVoiceRoomScreen`.
8. Add Lottie animations for Tier 1/2 gifts.

**Risk:** High complexity. Build independently before wiring into voice room.

---

### Phase 5 — Profiles & Progression
**Goal:** `MeScreen` and `UserProfileScreen` feel like a premium social app.

1. Add `LevelProgressBar` with XP animation.
2. Add `VipBadge` and `LevelFrame` composables.
3. Add achievement showcase to `MeScreen`.
4. Add followers/following/friends counts.
5. Add gift showcase section.
6. Upgrade `RankingScreen` with Wealth/Charm/Family/Event tabs.
7. Add animated rank-upgrade reveal.

---

### Phase 6 — Families & Social
**Goal:** Family screens are usable and social features are complete.

1. Add family discovery screen.
2. Add family creation flow.
3. Upgrade `FamilyScreen` with levels, XP, rankings.
4. Add notification center screen.
5. Add global search screen.
6. Add friend request UI.

---

### Phase 7 — Supporting Screens
**Goal:** Auth onboarding, Discover events, Wallet all complete.

1. Add splash screen.
2. Add onboarding / welcome flow.
3. Add event banners to `DiscoverScreen`.
4. Add event detail page.
5. Upgrade `TopUpScreen` / `WalletSettingsScreen` with purchase UI states.
6. Clearly mark all purchase CTAs as demo.

---

### Phase 8 — Polish & Testing
**Goal:** App is production-quality on a real device.

1. Audit all screens for recomposition hot-paths.
2. Pause all `InfiniteTransition` animations when composable leaves composition.
3. Accessibility pass: content descriptions, touch targets (min 48dp), contrast check.
4. Font scaling test (200% system font size).
5. Small screen test (360dp width).
6. Dark mode visual pass on all screens.
7. Navigation stress test: rapid tab switching, deep back presses.

---

## 12. Known Risks

| Risk | Impact | Mitigation |
|---|---|---|
| LiveKit SDK (`2.30.0`) may have breaking API changes if updated | High | Do not upgrade LiveKit during UI phase |
| `local.properties` LIVEKIT_URL is empty → voice falls back to `DemoVoiceRoomEngine` | Low | Fine for UI dev; document clearly |
| `MediaRecorder()` deprecated constructor in `ChatScreen.kt` | Medium | Fix in Phase 3 |
| `BitmapFactory.decodeFile` on main thread in `UserAvatar` | High | Fix in Phase 1 |
| No build CI — only local Gradle builds tested | Medium | Keep builds clean after every phase |
| JDK 17 (Temurin) required; Android Studio JBR 25 not compatible with Kotlin 2.0.21 | High | Always use `C:\Users\Admin\.jdks\temurin-17` for Gradle |
| `C:\Users\Admin\.jdks\temurin-17` path may not persist across sessions | High | Verify at start of each session |

---

## 13. Build Status

**Last successful build:** `feature/weplay-home-screen-recreation` branch  
**Build tool:** `.\gradlew.bat packageDebug` with `JAVA_HOME=C:\Users\Admin\.jdks\temurin-17`  
**Warning:** `ChatScreen.kt:120` — `MediaRecorder()` deprecated (non-blocking)  
**Errors:** None on last compile.

---

## 14. Audit Sign-Off

This audit is complete. **No implementation changes have been made.**

The existing project is structurally sound. Navigation is correct. The core voice room, chat, auth, Ludo, and Spy screens are functional. The main gaps are visual quality, missing token system, absent animation infrastructure, missing screens (splash, onboarding, gifting system, seat management), and dark-mode breakage on the HUB tab.

**Recommended next step:** Approve Phase 1 (Design Foundation) to proceed.

> [!IMPORTANT]
> Do not begin Phase 1 until this audit has been reviewed and approved.
> Phase 1 changes are small, targeted, and reversible.
> No screen will be rewritten wholesale in Phase 1.

