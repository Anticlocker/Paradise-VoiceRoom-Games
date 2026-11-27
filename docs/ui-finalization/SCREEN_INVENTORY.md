# Paradise — Screen Inventory (Frontend Finalization)

Complete inventory of finalized Android Compose screens across `app/src/main/java/com/sundram/weplay`.

## 1. Bottom Navigation Destinations (5)
1. **Games Home** (`Routes.HUB`, `GameLobbyScreen.kt`) — Preserved celestial palace backdrop (`AuroraGamesBackground`), quick shortcuts (`Ranks`, `Tasks`, `Social`), `"Rooms ›"` -> `Routes.GAME_ROOMS`, `"All Games ›"` -> `Routes.ALL_GAMES`, featured carousel, 2-column game grid, Live Voice Lounge card, Community Players (`Routes.userDetailRoute`).
2. **Voice Room List** (`Routes.ROOM`, `VoiceRoomListScreen.kt`) — Room discovery carousel/list, category filters, search, room creation (`Routes.CREATE_ROOM`).
3. **Chats** (`Routes.MSG`, `MessagesScreen.kt`) — Aurora Chats Hybrid with Friends/Contacts tab, search, message preview, requests banner, unread badges, quick profile/chat actions.
4. **Discover** (`Routes.DISCOVER`, `DiscoverScreen.kt`) — Preserved simplified layout: clean header, 4 Quick Shortcuts (`Families` -> `Routes.FAMILY`, `Nearby` -> `Routes.NEARBY`, `Clubs & Spaces` -> `Routes.CLUBS`, `Live Events` -> `Routes.EVENT_CENTER`), Community Activity, Moments feed with composer, inline comments, report/hide, and delete-own-post.
5. **Me** (`Routes.ME`, `MeScreen.kt`) — Preserved Royal Night palette and 34 `ap_me_*` icons, Phoenix frame preview, Friends/Following/Followers interactive sheets, VIP Center (`Routes.VIP`), Shop (`Routes.SHOP`), My Space (`Routes.MY_SPACE`), Wallet/TopUp (`Routes.TOPUP`), Settings (`Routes.SETTINGS`), Backpack, Family, Tasks, Events, persisted Language & Parental controls.

## 2. Core Flows, Games & Feature Screens
- **All Games Catalog** (`Routes.ALL_GAMES`, `AllGamesScreen.kt`) — Search/filterable catalog marking `Ludo Classic` & `Who's the Spy` as `PLAYABLE` vs 7 clearly labeled `SOON` previews + `Live Lobbies ›` (`Routes.GAME_ROOMS`).
- **Game Rooms** (`Routes.GAME_ROOMS`, `GameRoomsScreen.kt`) — Dark Aurora game lobby filtering & joinable voice/game sessions.
- **Ludo Classic** (`Routes.LUDO`, `LudoScreen.kt`) & **Who's the Spy** (`Routes.SPY`, `SpyScreen.kt`) — Local playable party games.
- **Live Voice Room** (`Routes.ROOM_DETAIL`, `LiveVoiceRoomScreen.kt`) — Interactive 8/12-seat stage, host controls, mic permission, animated gift tray, crest HUD, bond sheets, room settings, report/block.
- **Create Voice Room** (`Routes.CREATE_ROOM`, `CreateVoiceRoomScreen.kt`) — Dark Aurora room creation, passcode toggle, LiveKit/Demo start.
- **Room Themes, Preview & Crest** (`Routes.ROOM_THEMES`, `Routes.ROOM_THEME_PREVIEW`, `Routes.ROOM_CREST`, `RoomThemeScreens.kt`).
- **Ranking** (`Routes.RANKING`, `RankingScreen.kt`) — Dark podiums, Popularity/VIP/Couple tabs, Avatar Frame shop & equip sheet.
- **Task Center** (`Routes.TASKS`, `TaskCenterScreen.kt`), **Event Center & Detail** (`Routes.EVENT_CENTER`, `Routes.EVENT_DETAIL`, `EventCenterScreen.kt`, `EventDetailScreen.kt`).
- **Direct Chat** (`Routes.CHAT`, `ChatScreen.kt`), **Notifications** (`Routes.NOTIFICATIONS`, `NotificationsScreen.kt`), **Global Search** (`Routes.SEARCH`, `SearchScreen.kt`).
- **User Profiles** (`Routes.USER` -> `UserProfileScreen.kt`, `Routes.USER_DETAIL` -> `UserProfileDetailScreen.kt`) — Real directory lookup, copy ID, friendship actions, gift send (`ProfileStore.adjustCoins`), chat, report/block.
- **Family Hub & 10 Sub-views** (`Routes.FAMILY` + `FAMILY_*`, `FamilyScreen.kt`).
- **Nearby** (`Routes.NEARBY`, `NearbyScreen.kt`) & **Clubs & Spaces** (`Routes.CLUBS`, `ClubsSpacesScreen.kt`).
- **VIP Center** (`Routes.VIP`, `VipCenterScreen.kt`), **Cosmetics Shop** (`Routes.SHOP`, `ShopScreen.kt`), **My Space** (`Routes.MY_SPACE`, `MySpaceScreen.kt`).
- **Top Up** (`Routes.TOPUP`, `TopUpScreen.kt`), **Gifts & Backpack & Profile Edit & About** (`ProfileScreens.kt`).
- **Legacy Stub Cleaned:** `MeSettingsScreen.kt` (retired from `Routes.ME_SETTINGS`; restyled cleanly & anonymized).

## 3. Auth, Onboarding & Settings Suite
- **Auth** (`Routes.AUTH`, `AuthScreen.kt`) — Sign-in methods, interactive 13+ Terms & Privacy consent, DOB age gate, local account creation.
- **Paradise Guide / Onboarding** (`Routes.ONBOARDING` / `Routes.SPLASH`, `OnboardingScreen.kt`) — Interactive dark Aurora feature & safety tour.
- **Settings Suite (`ui/screens/settings/`):** `SettingsHomeScreen`, `AccountSettingsScreen` (with `DELETE` account wipe), `PrivacySettingsScreen`, `BlockedUsersScreen`, `ReportHistoryScreen`, `NotificationSettingsScreen`, `DndScheduleScreen`, `VoiceSettingsScreen`, `GeneralSettingsScreen` (dark Aurora locked, language, cache manager), `WalletSettingsScreen` (`TransactionsScreen`, `PaymentMethodsScreen` with Play Billing preview labels, `HostEarningsScreen`), `SupportSettingsScreen` (also serving `Routes.FEEDBACK`).
