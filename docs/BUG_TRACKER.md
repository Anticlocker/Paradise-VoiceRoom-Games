# Vybora — Functional Bug Tracker
**Phase 0 & 1 Baseline · Generated: 2026-11-20**

This document tracks all identified functional, navigational, visual, and performance defects across VYBORA.

---

### BUG-001
- **Severity**: High
- **Screen**: `LiveVoiceRoomScreen.kt` (Bottom Input Bar)
- **Reproduction steps**: Open any voice room (`Routes.ROOM_DETAIL`). Tap the gift button (`🎁`) at the bottom bar.
- **Expected behavior**: Tapping the gift button should open a bottom sheet displaying the gift catalog with categories, tiers, pricing, and gift previews.
- **Actual behavior**: Nothing happens. The gift button is an unclickable `Box` without an `onClick` modifier.
- **Root cause**: Missing `clickable` modifier and absent sheet state in `VoiceRoomInputBar`.
- **Fix**: Add `showGiftPanel` state, attach `Modifier.clickable`, and display a styled `ModalBottomSheet` or gift dialog displaying available gifts.
- **Test evidence**: Code inspection of `LiveVoiceRoomScreen.kt` line 751.
- **Status**: IN_PROGRESS

---

### BUG-002
- **Severity**: Medium
- **Screen**: `LiveVoiceRoomScreen.kt` (Header)
- **Reproduction steps**: Enter a voice room. Tap the back button in the header.
- **Expected behavior**: An exit confirmation dialog appears ("Leave voice room?") to avoid accidental departure during a session. Confirming disconnects and navigates back.
- **Actual behavior**: Immediately calls `nav.popBackStack()`, leaving room connection state hanging.
- **Root cause**: No confirmation state or dialog in `RoomHeader`.
- **Fix**: Introduce `showExitDialog` state in `LiveVoiceRoomScreen`, invoke `vm.disconnect()` upon confirmation, then pop back stack.
- **Test evidence**: `LiveVoiceRoomScreen.kt` line 287.
- **Status**: IN_PROGRESS

---

### BUG-003
- **Severity**: Medium
- **Screen**: `VoiceRoomListScreen.kt` (Header)
- **Reproduction steps**: Navigate to Voice Room tab (`Routes.ROOM`). Tap the search icon in the header.
- **Expected behavior**: Search input field appears or navigates to search screen to filter rooms.
- **Actual behavior**: The search `Icon` has no `clickable` or action handler.
- **Root cause**: Pure visual icon without interaction callback.
- **Fix**: Add interactive search overlay or state toggling search bar with live room filtering.
- **Test evidence**: `VoiceRoomListScreen.kt` lines 125-130.
- **Status**: IN_PROGRESS

---

### BUG-004
- **Severity**: Low
- **Screen**: `WePlayBottomBar.kt` (Navigation Bar)
- **Reproduction steps**: Launch application with 0 unread messages. Check the "Chats" tab badge.
- **Expected behavior**: The badge displays the real unread count or remains hidden when unread is 0.
- **Actual behavior**: The badge displays `44` even when unread total is 0 (`val badgeCount = if (msgBadge > 0) msgBadge else 44`).
- **Root cause**: Hardcoded fallback `44` intended for preview mistakenly rendered in production UI.
- **Fix**: Display badge strictly when `msgBadge > 0`.
- **Test evidence**: `WePlayBottomBar.kt` line 155.
- **Status**: IN_PROGRESS

---

### BUG-005
- **Severity**: High
- **Screen**: `GameLobbyScreen.kt` (Header Top Bar)
- **Reproduction steps**: Open Home / Game Lobby tab. Tap the notification bell or coin balance pill.
- **Expected behavior**: Bell opens Notifications screen; Coin pill opens Wallet / TopUp screen.
- **Actual behavior**: Icons do not have navigation callbacks attached.
- **Root cause**: Missing click listeners in `WePlayTopBar`.
- **Fix**: Connect bell icon to `Routes.NOTIFICATIONS` and coin pill to `Routes.TOPUP`.
- **Test evidence**: `GameLobbyScreen.kt` lines 120-150.
- **Status**: IN_PROGRESS

---

### BUG-006
- **Severity**: Medium
- **Screen**: `ChatScreen.kt`
- **Reproduction steps**: Build project targeting API 31+. Inspect compile warnings for `ChatScreen.kt`.
- **Expected behavior**: Audio recorder uses modern Android `MediaRecorder(context)` API.
- **Actual behavior**: Deprecated zero-argument `MediaRecorder()` constructor is invoked.
- **Root cause**: Legacy constructor usage.
- **Fix**: Branch on `Build.VERSION.SDK_INT >= Build.VERSION_CODES.S`.
- **Test evidence**: `ChatScreen.kt` audio initialization logic.
- **Status**: IN_PROGRESS

---

### BUG-007
- **Severity**: High
- **Screen**: `GameLobbyScreen.kt` (Explore Voice Room Cards)
- **Reproduction steps**: Scroll to the Explore section in Home tab. Tap any voice room card.
- **Expected behavior**: Tapping a room card enters that specific voice room (`Routes.roomRoute(id)`).
- **Actual behavior**: Room cards in the Explore section lack click listeners.
- **Root cause**: Reusable card composable does not wire `onClick` to navigation controller.
- **Fix**: Pass `nav` and navigate to `Routes.roomRoute(card.id)`.
- **Test evidence**: `GameLobbyScreen.kt` Explore card items.
- **Status**: IN_PROGRESS

---

### BUG-008
- **Severity**: Medium
- **Screen**: `DiscoverScreen.kt` (Nearby Section)
- **Reproduction steps**: Go to Discover tab, tap "Enable GPS" or "Nearby".
- **Expected behavior**: App requests Android location permission (`ACCESS_COARSE_LOCATION`), handles grant/denial gracefully, and shows legitimate or clearly marked demo location state.
- **Actual behavior**: Button does nothing or uses dummy non-interactive UI.
- **Root cause**: Missing `rememberLauncherForActivityResult` for location permission.
- **Fix**: Implement location permission launcher, rationale dialog, and demo fallback state.
- **Test evidence**: `DiscoverScreen.kt`.
- **Status**: IN_PROGRESS

---

### BUG-009
- **Severity**: Medium
- **Screen**: `MeScreen.kt` (Progression & Stats)
- **Reproduction steps**: Go to Me tab. Inspect user profile card.
- **Expected behavior**: User level, XP progress bar, badges, and gift showcases are visible and functional.
- **Actual behavior**: Only basic name, photo, and coins are displayed; no XP bar or badge showcase.
- **Root cause**: Incomplete profile presentation composables.
- **Fix**: Add LevelProgressBar, Stats row (Friends/Followers), Badge showcase, and Gift showcase.
- **Test evidence**: `MeScreen.kt` lines 70-130.
- **Status**: IN_PROGRESS

---

### BUG-010
- **Severity**: High
- **Screen**: `LiveVoiceRoomScreen.kt` (Speaker Seats)
- **Reproduction steps**: Tap an occupied seat of another user.
- **Expected behavior**: A participant profile action popup opens with options: Send Message, View Profile, Send Gift, Mute/Kick (host only).
- **Actual behavior**: Seat click handler only toggles self seat or takes empty seat.
- **Root cause**: No participant profile dialog handler.
- **Fix**: Add `selectedMember` state and modal profile card.
- **Test evidence**: `LiveVoiceRoomScreen.kt` lines 128-135.
- **Status**: IN_PROGRESS
