# NOVA CHATS INITIAL AUDIT
> Generated: 2026-11-21 | Auditor: Antigravity Lead Engineer

---

## 0. Package & Module

| Item | Value |
|------|-------|
| Package | `com.sundram.weplay` |
| App Module | `app/` |
| Language | Kotlin + Jetpack Compose |
| Navigation | Single `NavController` in `WePlayApp` → `NavHost` |
| Bottom Nav | `WePlayBottomBar` (5 tabs: Games, Voice Room, Chats, Discover, Me) |
| Theme | `WePlayTheme` with light/dark `WePlayPalette` + Vybora Premium tokens |

---

## 1. Bottom Navigation — WORKING ✅

- **Routes.tabs** = `{tab_hub, tab_room, tab_msg, tab_discover, tab_me}`
- Labels: **Games, Voice Room, Chats, Discover, Me** — correct per spec
- Uses dual icon WebP per tab from `drawable-nodpi/vb_nav_*_on/off.webp`
- `switchTab()` uses `popUpTo(startDestination) { saveState = true }` + `restoreState = true` — correct back-stack behaviour
- Unread badge on Chats tab driven by `msgUnread` state from `MessagingStore.unreadTotal()`

### Known Nav Risk
- `MessagesScreen` previously had a header with no New Chat / Contacts button leading to routes
- No second `NavHost` found — single NavHost in `MainActivity.kt` ✅

---

## 2. Existing Chats / Messaging Features

### MessagesScreen.kt (old — will be replaced by Nova)
| Feature | Status |
|---------|--------|
| 3-tab structure: Chats / People / Alerts | WORKING |
| Search bar (by name) | WORKING |
| Chat rows with swipe-to-mute / delete | WORKING |
| Unread count badge on row | WORKING |
| Online dot on avatar | WORKING (demo data) |
| Pin indicator on chat row | WORKING |
| Mute bell indicator | WORKING |
| Friend requests (accept/decline) | WORKING |
| Blocked users / Unblock | WORKING |
| Notifications / Clear all | WORKING |
| New Chat / Contacts header buttons | **MISSING** |
| Filter chips (All/Unread/Groups/Friends) | **MISSING** |
| Nova mascot avatar fallback | **MISSING** |
| Gift Pack floating widget | **MISSING from Chats screen** |

### ChatScreen.kt (thread — preserved as-is)
| Feature | Status |
|---------|--------|
| Text send / receive | COMPLETE_LOCAL |
| Draft persistence | COMPLETE_LOCAL |
| Voice recording (MediaRecorder, mic permission) | COMPLETE_LOCAL |
| Image sending (photo picker) | COMPLETE_LOCAL |
| Gift sending with coin deduction | COMPLETE_LOCAL |
| Long-press: Recall (120 s window) / Delete for me | COMPLETE_LOCAL |
| Mute / Pin / Clear / Block / Report from thread | COMPLETE_LOCAL |
| Wallpaper cycling (3 variants) | COMPLETE_LOCAL |
| Day-separator date labels | COMPLETE_LOCAL |
| Scroll-to-latest on new message | COMPLETE_LOCAL |
| Typing indicator (demo) | COMPLETE_LOCAL |
| Read/unread tracking | COMPLETE_LOCAL |
| imePadding on composer | COMPLETE_LOCAL |
| Block state: composer replaced by unblock prompt | COMPLETE_LOCAL |
| Backend WebSocket | BLOCKED_INTEGRATION (demo store) |

---

## 3. Data & Repository

| Component | Status |
|-----------|--------|
| `MessagingStore` | Local `SharedPreferences`-backed demo store |
| `Chat`, `ChatMessage`, `DirectoryUser` data classes | Present and stable |
| `AvatarModels.kt` — 12 `DefaultAvatar` entries | Present, using VoiceRoom character art |
| `ALL_DEFAULT_AVATARS[i]` mapped via `vb_vr_avatar_demo_01..06` etc. | Present |
| Nova chat avatar drawables (`vb_chat_avatar_01..12`) | **Just imported** |
| `ProfileStore` / `UserProfile` | Present |
| `MessagingStore.seedIfEmpty()` | Populates demo DirectoryUsers and chats |

---

## 4. Assets — Post-Import Status

| Category | Count | Status |
|----------|-------|--------|
| Mascot avatars (`vb_chat_avatar_01..12`) | 12 | ✅ Imported |
| Action/nav icons (`vb_chat_ic_*`) | 40 | ✅ Imported |
| Badge sprites (`vb_chat_badge_*`) | 5 | ✅ Imported |
| Backgrounds (`vb_chat_bg_*`) | 3 | ✅ Imported |
| Sparkle effect (`vb_chat_fx_sparkle`) | 1 | ✅ Imported |
| **Total** | **61** | **✅ All imported to `drawable-nodpi/`** |

No name collisions with existing assets. All use `vb_chat_` prefix unique to this pack.

---

## 5. Visual Token Mapping (Nova Chats spec → existing theme)

| Nova Spec | Existing Token | Value |
|-----------|---------------|-------|
| Primary ink `#241B45` | `VyboraTextPrimary` | `#201B35` ≈ match |
| Violet `#7958DF` | `VyboraPurple` | `#8258EE` ≈ match |
| Cyan `#37C9D7` | `VyboraCyan` | `#27D2DC` ≈ match |
| Lavender surface `#F4F0FF` | `VyboraLavender` | `#EDE5FF` ≈ match |
| Background `#FCFBFF` | `VyboraBg` | `#FAF9FF` ≈ match |
| Muted text `#918AA6` | `VyboraTextSecondary` | `#827D98` ≈ match |
| Accent pink `#EC78B9` | `PinkGift` | `#FF738F` ≈ match |

Using existing Vybora tokens; no new colour constants needed.

---

## 6. Known Bugs to Fix in Nova Chats

1. **No "Contacts" and "New Chat" buttons** in MessagesScreen header — adding them
2. **Filter chips** (All / Unread / Groups / Friends) — not present — adding
3. **Nova mascot avatar** not used in Chats (only VoiceRoom avatars used) — adding deterministic mapping
4. **Empty state** is a plain card with text — replacing with illustrated empty state
5. **Gift Pack widget** not present in Chats landing — adding compact floating entry
6. **initials-only avatar** still used in chat rows — replacing with Nova mascot fallback

---

## 7. Navigation Regression Risk Assessment

- No duplicate `NavController` found
- No nested `NavHost` found
- `BackHandler` only used inside game screens (Ludo, Spy)
- `DropdownMenu` in `ChatScreen` (settings) dismisses correctly
- Modal sheets (gift picker, photo picker) use state booleans — no Compose Navigation involved
- **No overlay blocking tabs** found in current implementation
- Gift Pack sheet in `GiftPackOffersSheet.kt` uses `ModalBottomSheet` — verify doesn't intercept back-stack

---

## 8. Milestones Plan

| Milestone | Scope | Status |
|-----------|-------|--------|
| M0 Audit | This document | ✅ DONE |
| M1 Assets + Nova UI shell | 61 assets imported, new MessagesScreen | 🔄 In Progress |
| M2 Functional list | Search, filters, avatar mapping, Gift Pack | 🔄 In Progress |
| M3 Thread preservation | Verify ChatScreen intact | ✅ Already working |
| M4 Navigation QA | Overlay/back-stack check | 🔄 Pending build test |
| M5 Polish + QA | Accessibility, animations, offline states | 🔄 Pending |
