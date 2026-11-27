# Paradise — Navigation & Route Matrix

Complete `Routes` (`ui/nav/WePlayBottomBar.kt`) and `NavHost` (`MainActivity.kt`) wiring.

## 1. Bottom Navigation Bar (`Routes.tabs`)
1. `Routes.HUB` (`"tab_hub"`) -> `GameLobbyScreen`
2. `Routes.ROOM` (`"tab_room"`) -> `VoiceRoomListScreen`
3. `Routes.MSG` (`"tab_msg"`) -> `MessagesScreen`
4. `Routes.DISCOVER` (`"tab_discover"`) -> `DiscoverScreen`
5. `Routes.ME` (`"tab_me"`) -> `MeScreen`

Bottom-bar transitions use `EnterTransition.None` / `ExitTransition.None` to prevent ghost overlap; pushed detail screens use subtle `180ms/140ms` fade transitions.

## 2. Resolved Route Collisions & Completed Registrations
| Previous State | Issue | Finalized Unique Route & Composable |
| :--- | :--- | :--- |
| `Routes.VIP = Routes.TOPUP` | VIP shortcut opened Coin Top-Up | `Routes.VIP = "profile/vip"` -> `VipCenterScreen` |
| `Routes.SHOP = Routes.GIFTS` | Shop shortcut opened Avatar Gifts | `Routes.SHOP = "profile/shop"` -> `ShopScreen` |
| `All Games` -> `Routes.GAME_ROOMS` | Opened live rooms instead of game catalog | `Routes.ALL_GAMES = "game/all"` -> `AllGamesScreen` (`Rooms ›` stays -> `Routes.GAME_ROOMS`) |
| `My Space` modal sheet | No dedicated deep route | `Routes.MY_SPACE = "profile/myspace"` -> `MySpaceScreen` |
| `Nearby` / `Clubs & Spaces` dialogs | No full screens | `Routes.NEARBY = "discover/nearby"` -> `NearbyScreen`, `Routes.CLUBS = "discover/clubs"` -> `ClubsSpacesScreen` |
| `Routes.ME_SETTINGS` -> `MeSettingsScreen` | Stale white layout & hardcoded user name | `Routes.ME_SETTINGS` -> `SettingsHomeScreen(nav, profile)` |
| `Routes.FEEDBACK`, `Routes.ONBOARDING`, `Routes.AUTH`, `Routes.SPLASH` | Declared in `Routes` without `NavHost` entries | Registered in `MainActivity.kt` (`SupportSettingsScreen`, `OnboardingScreen`, `AuthScreen`) |
| `Routes.FAMILY_*` (10 subroutes) | Unregistered in `NavHost` | Registered in `MainActivity.kt` -> `FamilyScreen(nav, profile, onSaveProfile)` |
