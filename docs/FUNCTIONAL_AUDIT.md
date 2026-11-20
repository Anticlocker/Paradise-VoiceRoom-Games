# Vybora — Functional Audit
**Phase 0 · Generated: 2026-11-19**
**Auditor: Antigravity (Senior Android / Compose Architect)**

---

## 1. Route Inventory

All routes declared in `Routes` object (`WePlayBottomBar.kt`):

| Constant | Route String | Type | Registered in NavHost |
|---|---|---|---|
| `HUB` | `tab_hub` | Tab | ✅ Yes |
| `ROOM` | `tab_room` | Tab | ✅ Yes |
| `DISCOVER` | `tab_discover` | Tab | ✅ Yes |
| `MSG` | `tab_msg` | Tab | ✅ Yes |
| `ME` | `tab_me` | Tab | ✅ Yes |
| `ROOM_DETAIL` | `room/{roomId}` | Full-screen | ✅ Yes |
| `LUDO` | `game/ludo` | Full-screen | ✅ Yes |
| `SPY` | `game/spy` | Full-screen | ✅ Yes |
| `PROFILE_EDIT` | `profile/edit` | Full-screen | ✅ Yes |
| `GIFTS` | `profile/gifts` | Full-screen | ✅ Yes |
| `FAMILY` | `profile/family` | Full-screen | ✅ Yes |
| `BACKPACK` | `profile/backpack` | Full-screen | ✅ Yes |
| `SETTINGS` | `profile/settings` | Full-screen | ✅ Yes |
| `ABOUT` | `profile/about` | Full-screen | ✅ Yes |
| `ACCOUNT_SETTINGS` | `settings/account` | Full-screen | ✅ Yes |
| `PRIVACY_SETTINGS` | `settings/privacy` | Full-screen | ✅ Yes |
| `BLOCKED_USERS` | `settings/privacy/blocked` | Full-screen | ✅ Yes |
| `REPORT_HISTORY` | `settings/privacy/reports` | Full-screen | ✅ Yes |
| `NOTIFICATION_SETTINGS` | `settings/notifications` | Full-screen | ✅ Yes |
| `DND_SCHEDULE` | `settings/notifications/dnd` | Full-screen | ✅ Yes |
| `VOICE_SETTINGS` | `settings/voice` | Full-screen | ✅ Yes |
| `GENERAL_SETTINGS` | `settings/general` | Full-screen | ✅ Yes |
| `WALLET_SETTINGS` | `settings/wallet` | Full-screen | ✅ Yes |
| `WALLET_TRANSACTIONS` | `settings/wallet/transactions` | Full-screen | ✅ Yes |
| `PAYMENT_METHODS` | `settings/wallet/payment-methods` | Full-screen | ✅ Yes |
| `HOST_EARNINGS` | `settings/wallet/host-earnings` | Full-screen | ✅ Yes |
| `SUPPORT_SETTINGS` | `settings/support` | Full-screen | ✅ Yes |
| `TOPUP` | `profile/topup` | Full-screen | ✅ Yes |
| `CHAT` | `chat/{userId}` | Full-screen | ✅ Yes |
| `USER` | `user/{userId}` | Full-screen | ✅ Yes |
| `USER_DETAIL` | `profile/detail/{userId}` | Full-screen | ✅ Yes |
| `RANKING` | `tab_ranking` | Full-screen | ✅ Yes |
| `ME_SETTINGS` | `tab_me_settings` | Full-screen | ✅ Yes |
| `CREATE_ROOM` | `tab_create_room` | Full-screen | ✅ Yes |

**Total routes defined:** 34  
**Total registered in NavHost:** 34  
**Unregistered routes:** 0  
**Orphan screens (file exists, no route):** `WePlayHubScreen.kt` (deprecated)

---

## 2. Complete Screen Functional Inventory

| Page | UI Status | Clickable Controls | Existing Behavior | Expected Behavior | Missing Dependency | Priority |
|---|---|---|---|---|---|---|
| **AuthScreen** | ✅ Functional | Name field, gender picker, DOB picker, photo picker, Save button | Collects name/gender/DOB/photo, writes UserProfile to ProfileStore, navigates to main app | Same — add OTP verification, avatar selection, interest selection | OTP backend, avatar gallery, interest tag model | P0 |
| **GameLobbyScreen** | ⚠️ Compiles — visual gaps | Game banner cards (Ludo, Spy, etc.), explore section rows, create-room FAB-equivalent | Tapping Ludo → navigates `Routes.LUDO`; Spy → `Routes.SPY`; room cards → not wired | All game cards launch correct screens; explore cards show real users | ViewModel + data layer; real Explore feed API; Coil room covers | P1 |
| **VoiceRoomListScreen** | ✅ Functional (basic) | Category tabs, room card rows, Create Room button | Category tabs filter room list; tapping card → `Routes.ROOM_DETAIL`; Create → `Routes.CREATE_ROOM` | Same + host avatar/cover art, member count badge, real-time room data | Coil for covers; real API replacing mock data | P1 |
| **LiveVoiceRoomScreen** | ✅ Functional — gaps | Mic toggle, speaker seats, chat overlay, gift button, back/leave | Mic mute/unmute via LiveKit; seat tap (no action sheet); gift button (no sheet opens); back navigates out | Seat tap → seat action sheet; gift button → gift tray; leave → confirmation dialog; room settings via gear icon | GiftTray composable; SeatActionSheet; ExitDialog; RoomSettingsSheet; seat request/invitation flow | P1 |
| **DiscoverScreen** | ✅ Functional (basic) | Trending cards, event cards, Explore posts, Nearby toggle | Cards display static/demo data; Nearby section shows no location prompt | Real data feed; location permission rationale; event deep-links | Location permission flow; real API; EventModel | P2 |
| **MessagesScreen** | ✅ Functional | Conversation list rows, New Chat FAB | Loads conversations from MessagingStore; tapping row → ChatScreen | Same + search, unread filters, online indicators | Search bar; real-time messaging backend | P2 |
| **ChatScreen** | ✅ Functional — deprecation warning | Message bubbles, text input, send button, mic button, coin-gift button | Sends/receives text messages; audio record UI present but not functional; coin gift deducts coins | Full audio message send/receive; push notifications | MediaRecorder fix (API ≥ 31); audio upload backend | P2 |
| **MeScreen** | ✅ Functional — visual gaps | Edit profile, settings gear, ranking, top-up, gifts, backpack, family, logout | Navigates to profile/settings sub-screens; logout clears ProfileStore | Add VIP badge, XP/level bar, achievement showcase, followers count | LevelProgressBar; VipBadge; AchievementBadge model | P2 |
| **UserProfileScreen** | ✅ Functional | Follow button, chat button, gift button, block/report | Shows user detail from MessagingStore; follow/chat navigate correctly | Real user data from backend; gift send integration | Backend user API; gift send flow | P2 |
| **UserProfileDetailScreen** | ✅ Functional | Back button, detail rows | Shows extended profile info | Same | None critical | P3 |
| **RankingScreen** | ✅ Functional (static) | Tab selector (Wealth only), leaderboard rows | Displays hardcoded demo ranking data with purple gradient header | Wealth / Charm / Family / Event tabs; real leaderboard data | LeaderboardEntry API; tab model | P2 |
| **CreateVoiceRoomScreen** | ✅ Exists | Room name input, topic tags, privacy toggle, Create button | UI renders; create action not wired to backend | Form validates + creates real room on server; navigates to LiveVoiceRoomScreen | Room creation API; ViewModel | P1 |
| **LudoScreen** | ✅ Functional | Dice roll button, piece taps, turn control | Full 2-4 player Ludo game on Canvas board; complete game logic | Same — add multiplayer over WebSocket | Multiplayer backend | P3 |
| **SpyScreen** | ✅ Functional | Player list, word reveal, vote buttons, timer | Full "Who's the Spy" game logic; works locally | Same — add multiplayer | Multiplayer backend | P3 |
| **GiftsScreen** | ⚠️ Stub only | Gift grid tiles, quantity selector, send button | UI renders gift grid; tapping gifts has no animation; send deducts coins (mock) | Animated gift send: tier-1/2/3 Lottie overlays; broadcast to room | GiftAnimationOverlay; Lottie files; gift send API | P1 |
| **FamilyScreen** | ⚠️ Basic | Join/leave button, member list, family info | Saves family name to UserProfile | Real family discovery, creation, XP/level, rankings | FamilyModel; family API | P3 |
| **BackpackScreen** | ✅ Functional | Item grid, equip/unequip buttons | Displays backpack items from UserProfile | Real item inventory from backend | Item API | P3 |
| **TopUpScreen** | ⚠️ Mock UI | Coin package tiles, buy buttons | UI renders; buy actions show demo toast | Real Google Play Billing integration | Google Play Billing v5 | P2 |
| **ProfileEditScreen** | ✅ Functional | Name/bio/gender/DOB fields, photo picker, save | Saves updated UserProfile | Same | None | P2 |
| **SettingsHomeScreen** | ✅ Functional | Settings section rows | Navigates to sub-settings screens | Same | None | P3 |
| **AccountSettingsScreen** | ✅ Functional | Display name, email, phone, change password rows | Displays current profile; editing fields writes to ProfileStore; logout | Real account API | Backend auth | P3 |
| **PrivacySettingsScreen** | ✅ Functional | Privacy toggles, blocked users, report history | Toggle states saved locally | Real backend privacy flags | Backend | P3 |
| **BlockedUsersScreen** | ✅ Functional | Blocked user list, unblock button | Shows locally stored blocked list | Real block list from backend | Backend | P3 |
| **ReportHistoryScreen** | ✅ Functional | Report history list | Shows submitted report stubs | Real report API | Backend | P3 |
| **NotificationSettingsScreen** | ✅ Functional | Notification toggles, DND schedule link | Toggle states saved locally | FCM-integrated push toggles | FCM | P3 |
| **DndScheduleScreen** | ✅ Functional | Time pickers, repeat days | DND schedule saved locally | Backend sync | Backend | P3 |
| **VoiceSettingsScreen** | ✅ Functional | Audio quality, mic sensitivity toggles | Settings saved locally | LiveKit-aware audio profile switching | LiveKit audio config API | P3 |
| **GeneralSettingsScreen** | ✅ Functional | Language, font size, theme toggle | Theme toggle switches ThemeMode live; others saved locally | Backend language sync | Backend | P2 |
| **WalletSettingsScreen** | ✅ Functional | Balance display, top-up link, transaction history link | Navigates to TopUp and Transactions | Real wallet balance from backend | Wallet API | P2 |
| **TransactionsScreen** | ✅ Functional | Transaction list rows | Shows demo transaction list | Real transaction history from backend | Wallet API | P2 |
| **PaymentMethodsScreen** | ✅ Functional | Payment method list, add method button | Shows demo cards | Real payment method management | Payment backend | P2 |
| **HostEarningsScreen** | ✅ Functional | Earnings summary, withdrawal button | Shows demo earnings | Real host earnings API | Backend | P3 |
| **SupportSettingsScreen** | ✅ Functional | FAQ rows, chat with support | Opens support contact stubs | Real support ticket system | Support API | P3 |
| **AboutScreen** | ✅ Functional | Version info, legal links | Shows app version and links | Same | None | P4 |
| **MeSettingsScreen** | ✅ Exists | (Needs review) | Unknown — not fully audited | Should navigate to relevant settings | None known | P3 |

---

## 3. Orphan Screens

| File | Status | Action Required |
|---|---|---|
| `WePlayHubScreen.kt` | ⚠️ Deprecated — not in NavHost | Delete or formally replace with `GameLobbyScreen` |

---

## 4. Broken Navigation List

| From Screen | Expected Destination | Actual Behavior | Severity |
|---|---|---|---|
| `LiveVoiceRoomScreen` — Gift button | Gift tray / GiftsScreen | Nothing happens (no sheet opens) | 🔴 High |
| `LiveVoiceRoomScreen` — Seat tap | Seat action sheet | No action sheet appears | 🔴 High |
| `LiveVoiceRoomScreen` — Back/Leave | Exit confirmation dialog | Navigates out immediately without confirm | 🟡 Medium |
| `LiveVoiceRoomScreen` — Settings icon | Room settings sheet | No sheet opens | 🟡 Medium |
| `GameLobbyScreen` — Explore card tap | User profile / room detail | Not wired | 🟡 Medium |
| `CreateVoiceRoomScreen` — Create button | LiveVoiceRoomScreen with new room | Action not wired to backend | 🟡 Medium |
| `GiftsScreen` — Gift tile tap | Gift animation overlay | No animation; nothing visual | 🔴 High |
| `RankingScreen` — Charm/Family/Event tabs | Filtered leaderboard | Only Wealth type shown; tabs missing | 🟡 Medium |
| `ChatScreen` — Mic button | Audio recording → send | UI only; no actual recording pipeline | 🟡 Medium |
| `MeScreen` — Notification bell | Notification center | No notification center screen exists | 🔴 High |
| `DiscoverScreen` — Nearby toggle | Location permission rationale | No permission flow; toggle does nothing visible | 🟡 Low |

---

## 5. Missing Screens List

| Required Screen | Priority | Blocking |
|---|---|---|
| Splash screen / branding | P0 | None |
| Onboarding / welcome flow | P0 | Auth experience |
| OTP verification UI | P0 | Auth security |
| Avatar selection | P1 | Profile personalization |
| Interest selection | P1 | Feed personalization |
| Permission explanation screens (Mic, Camera, Location) | P1 | LiveKit voice, Discover |
| Game detail / info page | P2 | Game discoverability |
| Game room lobby (pre-join) | P2 | Multiplayer UX |
| Matchmaking UI | P3 | Multiplayer |
| Game result / rewards screen | P3 | Game loop completion |
| Gift animation engine / overlay | P1 | Gifting feature |
| Room participant profile popup | P1 | Voice room UX |
| Seat invitation / request UI | P1 | Voice room UX |
| Room settings bottom sheet | P1 | Voice room UX |
| Exit room confirmation dialog | P1 | Voice room UX |
| Family discovery screen | P2 | Social |
| Family creation flow | P2 | Social |
| Family ranking screen | P2 | Social |
| Notification center | P2 | Engagement |
| Event banners / detail page | P2 | Discovery |
| Global search screen | P2 | Discovery |
| Leaderboard (Wealth / Charm / Family / Event tabs) | P2 | Progression |
| User block / report UI (complete) | P2 | Safety |

---

## 6. Audit Sign-Off

**Total screens implemented:** 34 routes → 34+ screens  
**Total screens missing:** 23  
**Total broken navigations:** 11  
**Orphan files:** 1 (`WePlayHubScreen.kt`)  
**Build status at audit time:** ✅ BUILD SUCCESSFUL (Phase 1 complete)
