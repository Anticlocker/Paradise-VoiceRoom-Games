# VYBORA — Interaction Test Matrix
## Feature Completion & Quality Fix Verification

**Date:** November 2026  
**Package:** `com.sundram.weplay`  
**Application ID:** `com.sundram.weplay`  
**Test Categories:** Navigation, Gift Pack Offers, Top Up, Community Players, Animated Avatars, Task Center  

---

## Status Definitions
- **PASS**: Feature implemented, functional, and validated locally.
- **DEMO_ONLY**: Feature operates correctly in local simulation/preview mode; clearly labeled to user.
- **BLOCKED_INTEGRATION**: Requires live backend credentials / production Google Play account.
- **FAIL**: Defect or unhandled state.
- **NOT_TESTED**: Out of scope for this milestone.

---

## 1. Bottom Navigation & Core Routes

| UI Control / Action | Expected Result | Result Status | Notes |
| :--- | :--- | :--- | :--- |
| **Tab 1: Games** | Opens Home / Games Lobby; displays label "Games"; selected icon active | **PASS** | Rebranded from "WePlay" to "Games" |
| **Tab 1 Repeated Click** | Guarded no-op; prevents duplicate back-stack instantiation or stack freeze | **PASS** | Guarded by `if (current != tab.route)` |
| **Tab 2: Voice Room** | Switches to Voice Lounge discovery screen single-top | **PASS** | State restored via `restoreState = true` |
| **Tab 3: Chats** | Switches to Messages & chat screen single-top | **PASS** | Shows unread badge |
| **Tab 4: Discover** | Switches to Discover feed screen single-top | **PASS** | Retains red dot badge |
| **Tab 5: Me** | Switches to User Profile screen single-top | **PASS** | Profile avatar & wallet displayed |
| **Rapid Tab Switching** | Flawless switching across all 5 tabs without blank screens or freezing | **PASS** | Validated via `switchTab` |
| **Android Back on Tabs** | Navigates back according to start destination graph without loop trap | **PASS** | Handled by NavHost |

---

## 2. Gift Pack Offers & 48-Hour Rotation

| UI Control / Action | Expected Result | Result Status | Notes |
| :--- | :--- | :--- | :--- |
| **Floating Gift Pack Widget** | Opens `GiftPackOffersSheet` directly from Games lobby | **PASS** | Bottom-right floating badge |
| **Sheet Header & Artwork** | Displays illustrated gift pack graphic and title | **PASS** | Uses `R.drawable.vb_gift_pack` |
| **48-Hour Countdown Timer** | Displays real-time ticking `47h 59m 59s` countdown derived from stored timestamp | **PASS** | Does not reset on launch or recomposition |
| **Featured Pack Details** | Displays currently active 48h package with title, price, and guaranteed items | **PASS** | Exactly 1 offer featured per cycle |
| **Guaranteed Items Inspection** | Displays item icons, counts, and duration pills (e.g., 3 Days, 7 Days) | **PASS** | Uses authentic original drawables |
| **"Browse 10 Packs" Tab** | Switches to catalog view listing all 10 starter offers (`GP01`–`GP10`) | **PASS** | Interactive pack cards |
| **Pack Selection in Catalog** | Shows details for tapped package | **PASS** | Tap opens pack preview |
| **Demo Claim Button** | Shows Store Preview informational notice; avoids charging money or fake credit | **DEMO_ONLY** | Clearly labeled as Demo Preview |
| **Add Coins (Top Up) Button** | Dismisses sheet cleanly and navigates to Top Up screen | **PASS** | Invokes `onOpenTopUp` callback |
| **Dismiss Sheet ('X' or Swipe)** | Dismisses sheet without modifying current screen or resetting scroll | **PASS** | State preserved on Games screen |

---

## 3. Top Up Screen

| UI Control / Action | Expected Result | Result Status | Notes |
| :--- | :--- | :--- | :--- |
| **Back Button** | Calls `nav.popBackStack()`; returns to previous screen | **PASS** | Hardware back also handled |
| **Live Wallet Balance Card** | Displays real balance from `UserProfile.coins` with gold coin icon | **PASS** | Responsive to updates |
| **8 Coin Packages Grid** | Displays Starter, Mini, Popular, Value, Premium, Super, Royal, Mega | **PASS** | 2-column responsive layout |
| **Package Card Selection** | Tapping card illuminates violet border and selects package | **PASS** | Animated selection state |
| **Promotional Badges** | Highlights `STARTER`, `POPULAR`, `BEST VALUE`, `HOT`, `SUPER`, `ROYAL`, `MEGA` | **PASS** | Distinct accent pills |
| **Checkout Confirmation Dialog** | Shows selected pack title, coin count, bonus coins, and demo price | **PASS** | Tapping Cancel closes dialog |
| **Payment Pending Spinner** | Displays circular progress indicator simulating transaction verification | **DEMO_ONLY** | 1.2s verification delay |
| **Payment Success Dialog** | Displays celebration "+X Coins Added" and updates live balance | **DEMO_ONLY** | Local demo credit simulation |
| **Transaction History Button** | Opens bottom sheet displaying past top-up records with timestamps and prices | **PASS** | Reads from `TopUpStore` |
| **Help & FAQ '?' Button** | Opens FAQ dialog explaining coin usage, credit timing, and refund policies | **PASS** | Clear customer guidance |
| **Google Play Billing Notice** | Prominent security box explaining preview mode vs real store billing | **PASS** | Prevents false billing claims |

---

## 4. Community Players & 12 Animated Default Avatars

| UI Control / Action | Expected Result | Result Status | Notes |
| :--- | :--- | :--- | :--- |
| **Section Header** | Displays "Community Players" and "Meet people who share your vibe" | **PASS** | Rebranded from old feed |
| **Animated Avatars** | Renders 2.5D character illustrations with gentle breathing scale & shimmer | **PASS** | `AnimatedAvatar` composable |
| **12 Avatar Personas** | Aurora Explorer, Lunar Artist, Cyber Gamer, Royal Dreamer, Star Captain, etc. | **PASS** | Unique drawables & palettes |
| **Player Info & Level Badge** | Displays username, gender symbol, `Lv.XX` badge, and bio | **PASS** | High-fidelity cards |
| **Online Indicator Dot** | Verified 7dp emerald dot displayed for active players | **PASS** | Only active when `isOnline = true` |
| **Follow / Following Toggle** | Tapping button toggles followed state immediately with visual feedback | **PASS** | State retained in memory |
| **Message Button** | Tapping opens 1-on-1 chat route for selected user | **PASS** | Routes to `Routes.chatRoute(userId)` |
| **Player Card Click** | Navigates to user profile or voice room | **PASS** | Seamless navigation |
| **Avatar Persistence** | Selected avatar ID persisted in `UserProfile` across sessions | **PASS** | Stored in `weplay_user` |

---

## 5. Task Center ("Your Missions")

| UI Control / Action | Expected Result | Result Status | Notes |
| :--- | :--- | :--- | :--- |
| **Tasks Shortcut (Home)** | Tapping Tasks tile opens `TaskCenterScreen` (`Routes.TASKS`) | **PASS** | Replaced old redirect to `Routes.ME` |
| **Back Button** | Calls `nav.popBackStack()`; returns cleanly to Games lobby | **PASS** | Fully reversible navigation |
| **Header Progress Banner** | Displays animated avatar, Level badge, XP progress bar, streak flame | **PASS** | Real-time level calculation |
| **7-Day Check-In Deck** | Horizontal deck showing Days 1–7; Day 7 marked with Royal Crown | **PASS** | Interactive daily cards |
| **Check In Now Button** | Claims today's daily reward; awards coins & XP; advances streak | **PASS** | Instant visual confirmation |
| **Duplicate Check-In Defense**| Rejects multiple check-ins on the same day; disables button | **PASS** | 24-hour timestamp check |
| **Filter Tabs** | Daily (8), Weekly (5), Newbie (6), Badges (7) tabs filter mission list | **PASS** | Smooth category switching |
| **Task Progress Bar** | Displays linear progress `(x/target)` and reward pill (`+XP`, `+Coins`) | **PASS** | Dynamically computed fraction |
| **Task "Go" Button** | Navigates directly to the target feature screen (Ludo, Voice, Profile, etc.) | **PASS** | Routes wired to real screens |
| **Task "Claim" Button** | Enabled when target count reached; awards coins & XP; marks `CLAIMED` | **PASS** | Triggers celebration dialog |
| **Reward Celebration Dialog**| Full celebratory modal confirming claimed rewards and updated balance | **PASS** | Dismissible with "Awesome!" |
