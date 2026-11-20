# VYBORA — Feature Update Audit & Architectural Gap Analysis
## Navigation, Gift Pack Offers, Top Up, Community Players, Animated Avatars, and Task Center

**Date:** November 2026  
**Package:** `com.sundram.weplay`  
**Application ID:** `com.sundram.weplay`  
**Status:** Audit Completed & Implementation In Progress  

---

## 1. Executive Summary

This audit assesses the state of the VYBORA Android application prior to Milestone 1–6 execution. While the first two screens (Home/Games and Voice Lounge Discovery) and the third screen (Aurora Lounge 2.0) have undergone visual redesign, several critical architectural gaps, navigation stack discrepancies, and placeholder features require immediate remediation:

1. **Bottom Navigation Branding:** The first tab is labeled `"WePlay"` rather than `"Games"`, and clicking an active tab triggers unconditional `switch` calls that can desynchronize NavController backstack state.
2. **Broken Navigation & Unresponsive Tabs:** Tapping shortcut items (e.g., "Tasks") routes to `Routes.ME` via `navigateSafe` without start destination popping, polluting the back stack. When returning from screens, users experience unresponsive tabs or navigation traps.
3. **Gift Pack Offers:** The existing `HomeGiftPackSheet` is a hardcoded placeholder with a single static pack, no rotation system, no 48-hour countdown timer, and redirects directly to generic gifts.
4. **Top Up Page:** Currently uses basic non-reactive cards, lacks package status management (Loading, Available, Pending, Success, Failed), lacks transaction history dialog, and lacks clear distinction between verified and local demo transactions.
5. **Community Players & Avatars:** Renders bare single-letter initials in colored circles (`user.name.take(1)`), lacks proper player cards, lacks animated character avatars, and lacks profile personalization options.
6. **Task Center:** No dedicated Task Center screen exists; the Tasks shortcut routes to `Routes.ME`. There is no 7-day daily check-in system, no daily or weekly missions engine, and no achievement tracking.

---

## 2. Root Cause Analysis of Broken Navigation

| Issue | Root Cause | Architectural Remediation |
| :--- | :--- | :--- |
| **First Tab Becomes Unclickable** | In `WePlayBottomBar.kt`, `clickable { switch(nav, tab.route) }` does not check `if (current != tab.route)`. Repeated clicks on the start destination invoke `popUpTo(HUB) { saveState = true }`, saving and restoring the same route recursively. | Guard tab clicks: only navigate if `current != tab.route`. If already on `HUB`, optionally smooth-scroll to top without touching the NavController. |
| **Tasks Shortcut Goes to Wrong Page** | In `GameLobbyScreen.kt`, line 221: `VyboraShortcutItem("Tasks", ..., Routes.ME)` pointed to `Routes.ME`. | Introduce `Routes.TASKS = "hub/tasks"`, create `TaskCenterScreen`, and wire shortcut to `Routes.TASKS`. |
| **Back Stack Pollution on Tab Navigation** | `NavHostController.navigateSafe(route)` pushes all routes onto the current stack without checking if the route is a top-level tab in `Routes.tabs`. | When `route in Routes.tabs`, `navigateSafe` delegates to standard single-top tab switching with `popUpTo(startDest) { saveState = true }` and `restoreState = true`. |
| **Missing Hardware Back Interception** | Screens like `LiveVoiceRoomScreen` lacked `BackHandler`. Pressing Android hardware Back bypassed the leave confirmation dialog, leaving the background audio session dangling. | Implement `BackHandler` in `LiveVoiceRoomScreen`, `TaskCenterScreen`, and sheets. |
| **Transparent Scrim & Overlay Interception** | `GiftCelebrationOverlay` had `.fillMaxSize()` without touch consumption or dismiss handling, potentially trapping user clicks. | Add explicit touch handlers and auto-dismiss timeouts to celebration overlays. |

---

## 3. Gift Pack Offers System Specification

### 10 Configurable Starter Offers
1. **GP01 — Mini Spark (₹9 demo):** 3 roses, 1 sparkle gift
2. **GP02 — Sweet Hearts (₹15 demo):** 5 hearts, 1 love badge preview
3. **GP03 — Lucky Petals (₹19 demo):** 6 roses, 2 gift effects
4. **GP04 — Friendship Box (₹25 demo):** 4 friendship gifts, 1 profile effect
5. **GP05 — Starry Surprise (₹29 demo):** 5 stars, 2 sparkle gifts
6. **GP06 — Moonlight Treat (₹39 demo):** 3 moon gifts, 3 hearts
7. **GP07 — Royal Mini (₹49 demo):** 2 crown gifts, 4 roses
8. **GP08 — Crystal Joy (₹59 demo):** 3 crystal gifts, 2 stars
9. **GP09 — Festival Bundle (₹79 demo):** 5 celebration gifts, 2 premium effects
10. **GP10 — Aurora Special (₹99 demo):** 4 premium gifts, 1 limited avatar-frame preview

### 48-Hour Rotation Engine
- **Interval:** Fixed 48 hours ($172,800,000\text{ ms}$).
- **State Stored:** `userId`, `featuredOfferId`, `offerStartTime`, `offerEndTime`, `cycleIndex`, `history` persisted in `weplay_gift_packs` SharedPreferences.
- **Clock Safety:** Timer derived from `offerEndTime - System.currentTimeMillis()`. Formatted as `47h 59m 59s`.
- **Payment Safety:** Labeled explicitly as `DEMO_PREVIEW`. Real store purchases require configured Google Play Billing; local mode prevents fake credit.

---

## 4. Top Up Screen Redesign Plan

- **Header:** Back button, Top Up title, live wallet balance, transaction history shortcut button.
- **8 Coin Packages:** Starter (100 coins), Mini (300), Popular (550), Value (1,200), Premium (2,500), Super (5,500), Royal (12,000), Mega (30,000) with gold/cyan/lavender highlights.
- **Purchase Lifecycle States:** Loading, Available, Pending checkout, Success dialog with updated balance, Payment Failed dialog.
- **Modals:** Transaction History sheet, Purchase Help / Refund FAQ dialog.

---

## 5. Community Players & 12 Animated Default Avatars

### 12 Original Default Avatars
1. **Aurora Explorer** (`vb_vr_avatar_demo_01`)
2. **Lunar Artist** (`vb_vr_avatar_demo_02`)
3. **Cyber Gamer** (`vb_vr_avatar_demo_03`)
4. **Royal Dreamer** (`vb_vr_avatar_demo_04`)
5. **Star Captain** (`vb_vr_avatar_demo_05`)
6. **Crystal Guardian** (`vb_vr_avatar_demo_06`)
7. **Neon Musician** (`vb_character_tricksters_cafe`)
8. **Celestial Wanderer** (`vb_character_space_werewolf`)
9. **Mystic Fox Mascot** (`vb_character_hide_and_seek`)
10. **Cosmic Bunny Mascot** (`vb_character_paint_to_hide`)
11. **Diamond Crown Mascot** (`vb_character_royal_dice`)
12. **Galaxy DJ Mascot** (`vb_character_whos_the_spy`)

### Animation & Persistence
- Gentle scale breathing ($0.97\times$ to $1.03\times$) + ambient radiant shimmer frame.
- Automatically paused when scrolled offscreen.
- Selected avatar ID stored in `UserProfile.avatarId` and persisted locally in `weplay_user`.

---

## 6. Task Center ("Your Missions") Engine Architecture

- **Sections:**
  1. Header with Level, XP progress bar, Streak flame, Daily progress.
  2. 7-Day Check-In card carousel with Day 7 Royal Chest.
  3. Daily Missions (8 tasks with Go & Claim actions).
  4. Weekly Challenges (5 tasks).
  5. New Player Missions (6 onboarding tasks).
  6. Achievements (7 permanent milestone trophies).
- **Progression Logic:** Real app event listeners for game completion, avatar customization, room visits, and check-ins.
- **Anti-Fraud:** Duplicate claim prevention and timestamp validation.
