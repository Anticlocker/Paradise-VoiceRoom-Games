# VYBORA ROOMS & SEATS FUNCTIONALITY MATRIX
**Generated:** 2026-11-21  
**Project:** VYBORA Android Social Gaming Platform  
**Component:** Game Rooms, Create Voice Room, Joined Voice Room (Aurora Lounge 2.0 Stage & Bonding)

---

## 1. Feature & Screen Matrix

| Feature ID | Screen / Component | Feature Description | Implementation Type | Status | Supported Actions & Invariants |
|---|---|---|---|---|---|
| **GR-01** | Game Rooms Screen | Light Pearl `#F8F7FF` Background & Typography | Native Compose | `VERIFIED` | Replaced dark navy theme with high-contrast light theme; readable dark `#211B38` titles, lavender borders (`#ECE9FE`). |
| **GR-02** | Game Rooms Screen | Safe Top App Bar | Native Compose | `VERIFIED` | Respects `statusBarsPadding()`. Back navigation, dynamic room count badge, search toggle button, and (+) Create Room button. |
| **GR-03** | Game Rooms Screen | "Find Your Next Squad" Hero Banner | Native Compose + WebP | `VERIFIED` | Uses `vb_rs_bg_game_rooms`, `vb_rs_host_crown`, with gradient overlay and direct "Create Room" action button. |
| **GR-04** | Game Rooms Screen | Filter Chips Bar | Native Compose | `VERIFIED` | High contrast pills for "All", "Games", "Deduction", "Casual", "KTV", "PK". Selected state has vibrant gradient; unselected has soft lavender background with dark text. |
| **GR-05** | Game Rooms Screen | Expandable Search Field | Native Compose | `VERIFIED` | Expands with smooth animation; live filtering on room title, host name, and category; includes clear (X) button and IME search action. |
| **GR-06** | Game Rooms Screen | Room Cards with Covers | Native Compose + WebP | `VERIFIED` | Rounded 18dp cards with subtle shadow; mapped to `vb_rs_cover_games`, `vb_rs_cover_friends`, `vb_rs_cover_music`, `vb_rs_cover_pk`; displays verified host crown, capacity (`X/8 on stage`), listeners count, and cyan "JOIN" button. |
| **GR-07** | Game Rooms Screen | Empty Search/Category State | Native Compose + WebP | `VERIFIED` | Displays `vb_rs_bg_empty_rooms` illustration, descriptive text, and a quick "Create a Room" action button. |
| **CR-01** | Create Voice Room Screen | Status & Nav Bar Insets Fix | Window Insets | `VERIFIED` | Root container has `.statusBarsPadding().navigationBarsPadding().imePadding()`. Zero overlap with Android clock/battery icons or bottom navigation bar. |
| **CR-02** | Create Voice Room Screen | Hero Cover Art Selector | Native Compose + WebP | `VERIFIED` | Uses `vb_rs_bg_create_room` with `vb_rs_voice_mic` icon overlay and informative text. |
| **CR-03** | Create Voice Room Screen | Title Validation | Native Compose Form | `VERIFIED` | Enforces 3–40 characters; displays live counter (`X/40`); inline error messaging if invalid. |
| **CR-04** | Create Voice Room Screen | Room Modes Picker | Native Compose + WebP | `VERIFIED` | Mode selection cards: Normal (`vb_rs_create_room`), Music/KTV (`vb_rs_mode_music`), Games (`vb_rs_mode_games`), PK Battle (`vb_rs_mode_pk`), Dice Party (`vb_rs_mode_dice`). |
| **CR-05** | Create Voice Room Screen | Lounge Tag Chips | Native Compose | `VERIFIED` | Chips for Friends, Music, Game, Chats, PK with purple active highlight. |
| **CR-06** | Create Voice Room Screen | Privacy Switch | Native Compose + WebP | `VERIFIED` | Uses `vb_rs_privacy_shield`; toggles between Public and Private Room with clear demo labeling. |
| **CR-07** | Create Voice Room Screen | Password Protection Toggle | Native Compose + WebP | `VERIFIED` | Uses `vb_rs_password_lock`; switch reveals secure text field with show/hide password toggle (minimum 4 characters). |
| **CR-08** | Create Voice Room Screen | Stage Layout Architecture Summary | Native Compose + WebP | `VERIFIED` | Card with `vb_rs_host_crown` highlighting: "Stage Layout: 1 Host + 8 Regular Mic Seats (4×2 Grid) + Optional Partner Seat". |
| **CR-09** | Create Voice Room Screen | Collapsible Advanced Settings | Native Compose | `VERIFIED` | Expandable section with room announcement input and partner/bonding policies. |
| **CR-10** | Create Voice Room Screen | Sticky Create Room CTA | Native Compose | `VERIFIED` | Elevated button above navigation insets; disabled when title < 3 chars; loading spinner; creates room and navigates to `Routes.roomRoute(id)`. |
| **ST-01** | Joined Voice Room | 1 Host Seat Invariant | Native Compose + WebP | `VERIFIED` | Host is always at Seat 0 (`HOST_SEAT_INDEX`). Centered when partner is disabled; positioned on the left when partner is enabled. Uses `vb_vr_frame_host` and `vb_vr_emblem_crown`. |
| **ST-02** | Joined Voice Room | Exactly 8 Regular Mic Seats Invariant | Native Compose + WebP | `VERIFIED` | Seats 1 through 8 (`REGULAR_SEAT_START..REGULAR_SEAT_END`) arranged strictly below the host in a responsive 4×2 grid: Row 1 = [1, 2, 3, 4], Row 2 = [5, 6, 7, 8]. |
| **ST-03** | Joined Voice Room | Optional Partner Seat (Seat 9) | Native Compose + WebP | `VERIFIED` | Distinct Seat 9 (`PARTNER_SEAT_INDEX`). When enabled by host, rendered to the RIGHT of the host with `vb_rs_frame_partner` and `vb_rs_seat_partner_empty`. Does NOT renumber regular seats 1–8. |
| **ST-04** | Joined Voice Room | Host Partner Seat Toggle | StateFlow / Engine | `VERIFIED` | Accessible in host's More > Room Controls. Toggling OFF gracefully unseats the occupant to listener lounge with a system notice. |
| **AT-01** | Joined Voice Room | Attach Seats Host Workflow | Native Compose Sheet | `VERIFIED` | Host opens More > Room Controls > "Attach Seats". Opens bottom sheet with 4×2 grid of regular seats (1–8). Host selects 2 distinct occupied regular seats. |
| **AT-02** | Joined Voice Room | Mutual Consent Protocol | State Machine / Dialog | `VERIFIED` | Requesting a bond sends mutual consent invitation. Pending recipient receives an `AlertDialog` with Accept / Decline options. Bond activates only upon acceptance. |
| **AT-03** | Joined Voice Room | Responsive Canvas Bond Link | Compose Canvas + WebP | `VERIFIED` | Renders a glowing gradient curved line between the measured center coordinates of the two seats using Compose `Canvas`. Non-intercepting touch (`pointerInput`-free). |
| **AT-04** | Joined Voice Room | Midpoint Heart Emblem & Bond Details | Native Compose + WebP | `VERIFIED` | `vb_rs_bond_heart` emblem positioned at midpoint of bond curve; clicking opens `BondDetailsBottomSheet` allowing host or occupants to Detach. |
| **AT-05** | Joined Voice Room | Bond Invalidation on Seat Departure | Engine Event | `VERIFIED` | When an occupant leaves or is kicked from their regular seat, any active bond on that seat is automatically invalidated and detached. |

---

## 2. Navigation & Back Handling Matrix

| User Action | Current Screen | Expected Destination / Outcome | Priority / Interception Handling | Status |
|---|---|---|---|---|
| Tap Back Button / Back Gesture | Game Rooms | Previous screen (Games/Home or Voice Lounge) | NavController pops stack | `PASS` |
| Tap Search Icon | Game Rooms | Expands/collapses search input field | Local state toggle | `PASS` |
| Tap (+ / Create Room) | Game Rooms | `Routes.CREATE_ROOM` | NavController navigate | `PASS` |
| Tap Room Card "JOIN" | Game Rooms | `Routes.roomRoute(id)` | NavController navigate | `PASS` |
| Tap Close (X) | Create Voice Room | Previous screen | NavController pops stack | `PASS` |
| Tap "Create Room Now" | Create Voice Room | Simulates creation, navigates to `Routes.roomRoute(id)` | Popping create screen from back stack | `PASS` |
| Back Gesture with Sheet Open | Joined Voice Room | Dismisses active sheet (Attach, Bond Details, More, etc.) | `BackHandler` consumes back and dismisses top sheet | `PASS` |
| Back Gesture with No Sheets | Joined Voice Room | Opens Exit Confirmation Dialog | `BackHandler` sets `showExitDialog = true` | `PASS` |
| Confirm "Leave Room" | Exit Dialog | Disconnects engine and pops back stack | `vm.disconnect()`, `nav.popBackStack()` | `PASS` |
| Tap Bonded Heart Badge | Joined Voice Room | Opens `BondDetailsBottomSheet` | Sets `selectedBondForDetails = bond` | `PASS` |
