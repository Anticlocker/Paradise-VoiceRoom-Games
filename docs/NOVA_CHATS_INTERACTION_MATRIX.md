# VYBORA NOVA CHATS 2.0 — INTERACTION MATRIX

| Interaction / Element | Trigger | Expected Outcome | State Management | Status |
|-----------------------|---------|------------------|------------------|--------|
| **Bottom Navigation — Games** | Tap Games tab (tab 1) | Navigates to `Routes.HUB` (Game Lobby), maintains back-stack state | `switchTab(nav, Routes.HUB)` with `saveState = true`, `restoreState = true` | `COMPLETE_LOCAL` |
| **Bottom Navigation — Voice Room** | Tap Voice Room tab (tab 2) | Navigates to `Routes.ROOM` (Voice Room list) | `switchTab(nav, Routes.ROOM)` | `COMPLETE_LOCAL` |
| **Bottom Navigation — Chats** | Tap Chats tab (tab 3) | Navigates to `Routes.MSG` (Nova Chats landing) | `switchTab(nav, Routes.MSG)`, shows active purple badge & unread count pill | `COMPLETE_LOCAL` |
| **Bottom Navigation — Discover** | Tap Discover tab (tab 4) | Navigates to `Routes.DISCOVER` | `switchTab(nav, Routes.DISCOVER)` | `COMPLETE_LOCAL` |
| **Bottom Navigation — Me** | Tap Me tab (tab 5) | Navigates to `Routes.ME` (User profile & account hub) | `switchTab(nav, Routes.ME)` | `COMPLETE_LOCAL` |
| **Header — Contacts Button** | Tap contacts icon in Chats header | Opens `ContactsBottomSheet` with search and list of friends | `showContactsSheet = true` (ModalBottomSheet) | `COMPLETE_LOCAL` |
| **Header — New Chat Button** | Tap new chat icon in Chats header | Opens `NewChatBottomSheet` with directory search & direct messaging | `showNewChatSheet = true` (ModalBottomSheet) | `COMPLETE_LOCAL` |
| **Search Bar — Keystroke** | Type text into search field | Filters conversation rows by participant name or last message text in real time | Reactive filter on `query` StateFlow/remember | `COMPLETE_LOCAL` |
| **Search Bar — Clear Button** | Tap 'X' clear icon | Clears search input, restores full conversation list | `query = ""` | `COMPLETE_LOCAL` |
| **Filter Chip — All** | Tap "All" chip | Shows all active direct messages and chats | `filter = ChatFilter.ALL` | `COMPLETE_LOCAL` |
| **Filter Chip — Unread** | Tap "Unread" chip | Filters list to only chats with `unread > 0` | `filter = ChatFilter.UNREAD` | `COMPLETE_LOCAL` |
| **Filter Chip — Groups** | Tap "Groups" chip | Filters to group/family channels (empty state if none joined) | `filter = ChatFilter.GROUPS` | `COMPLETE_LOCAL` |
| **Filter Chip — Friends** | Tap "Friends" chip | Filters list to confirmed friends only | `filter = ChatFilter.FRIENDS` | `COMPLETE_LOCAL` |
| **Active Friends Row** | Tap avatar in "Active Now" | Navigates to `Routes.userRoute(u.id)` (profile detail) | `nav.navigate(Routes.userRoute(u.id))` | `COMPLETE_LOCAL` |
| **Conversation Row — Tap** | Tap any chat card | Opens existing `ChatScreen` with stable `userId` | `nav.navigate(Routes.chatRoute(chat.userId))` | `COMPLETE_LOCAL` |
| **Conversation Row — Swipe Right (Start to End)** | Swipe row from left to right | Toggles mute/unmute notifications for this conversation | `store.setMuted(userId, !muted)`, animated swipe dismiss | `COMPLETE_LOCAL` |
| **Conversation Row — Swipe Left (End to Start)** | Swipe row from right to left | Deletes chat from local store without deleting friend relationship | `store.deleteChat(userId)`, animated swipe dismiss | `COMPLETE_LOCAL` |
| **Conversation Badges** | Render chat row | Displays official or family badge base (`vb_chat_badge_*`) with localized Compose text ONLY when verified by metadata | `user?.isOfficial == true` / `user?.isFamily == true` | `COMPLETE_LOCAL` |
| **Floating Gift Pack FAB** | Tap floating promotional card | Opens 48-Hour Rotating Gift Pack Bottom Sheet (`GiftPackOffersSheet`) without navigating away | `showGiftPackSheet = true` (`ModalBottomSheet`) | `COMPLETE_LOCAL` |
| **Floating Gift Pack — Dismiss** | Tap 'X' on Gift Pack card | Dismisses floating entry for session with scale/fade exit animation | `dismissed = true` (rememberSaveable) | `COMPLETE_LOCAL` |
| **Gift Pack Sheet — Top Up** | Tap "Top Up" inside sheet | Closes sheet and navigates to `Routes.TOPUP` | `nav.navigate(Routes.TOPUP)` | `COMPLETE_LOCAL` |
| **Empty State — Start a Chat** | Tap "Start a Chat" in empty list | Opens `NewChatBottomSheet` to pick user | `showNewChatSheet = true` | `COMPLETE_LOCAL` |
| **Empty State — Find Friends** | Tap "Find Friends" in empty list | Opens `ContactsBottomSheet` | `showContactsSheet = true` | `COMPLETE_LOCAL` |
| **Friend Request — Accept** | Tap "Accept" on request card | Converts request into mutual friend relationship, updates unread counter | `store.acceptRequest(u.id)` | `COMPLETE_LOCAL` |
| **Friend Request — Decline** | Tap "Decline" on request card | Shows confirmation alert dialog, declines request on confirm | `confirmDecline = u` -> `store.declineRequest(u.id)` | `COMPLETE_LOCAL` |
| **Chat Thread — Send Text** | Type and tap send icon | Appends message, plays local echo, triggers simulated response timer | `store.sendMessage(userId, text)` | `COMPLETE_LOCAL` |
| **Chat Thread — Send Photo** | Tap camera icon in composer | Opens photo picker sheet and attaches image | `store.sendMedia(userId, MsgType.IMAGE, path)` | `COMPLETE_LOCAL` |
| **Chat Thread — Voice Note** | Tap mic icon in composer | Requests audio recording permission, records `.m4a` file, attaches on stop | `VoiceRecorder.start(context)` / `VoiceRecorder.stop()` | `COMPLETE_LOCAL` |
| **Chat Thread — Send Gift** | Tap gift icon in composer | Deducts demo coin balance, logs gift transaction, sends gift card bubble | `store.sendGift(userId, giftId)` | `COMPLETE_LOCAL` |
| **Chat Thread — Message Recall** | Long press bubble within 120s | Shows context menu with "Recall message", marks message as recalled | `store.recallMessage(userId, msgId)` | `COMPLETE_LOCAL` |
| **Chat Thread — Delete for Me** | Long press bubble | Removes message locally | `store.deleteMessageForMe(userId, msgId)` | `COMPLETE_LOCAL` |
| **Chat Thread — Back Navigation** | Tap header back arrow or gesture back | Pops back stack cleanly to `MessagesScreen`, preserving scroll & filter state | `nav.popBackStack()` | `COMPLETE_LOCAL` |
| **Realtime Transport** | WebSocket connection | Live server connection for messages and presence across sessions | Simulated local engine with background delay coroutine | `BLOCKED_INTEGRATION` (Backend WS server offline) |
