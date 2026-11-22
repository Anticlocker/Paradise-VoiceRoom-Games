# VYBORA ROOMS & SEATS UPGRADE — IMPLEMENTATION REPORT

**Author:** Principal Android Architect & Jetpack Compose Engineer  
**Date:** 2026-11-21  
**Target:** VYBORA Android Social Gaming Application (`com.sundram.weplay`)  
**Package Pack:** `VYBORA_Rooms_Seats_Update_Pack`

---

## 1. Executive Summary

This engineering effort completes the **VYBORA Rooms & Seats Upgrade** across three core social gaming flows:
1. **Game Rooms Discovery Screen** (`GameRoomsScreen.kt`): Rebuilt with a light pearl `#F8F7FF` and lavender identity, readable `#211B38` titles, "Find your next squad" hero panel, category filter chips, expandable live search, and illustrated room cards.
2. **Create Voice Room Screen** (`CreateVoiceRoomScreen.kt`): Eliminated status bar collision by applying proper window insets (`statusBarsPadding()`, `navigationBarsPadding()`, `imePadding()`), added hero cover art, 3–40 char validation, mode cards (`vb_rs_mode_*`), password toggle with eye visibility, and 8-seat stage layout summary.
3. **Joined Voice Room / Aurora Lounge 2.0 Stage** (`LiveVoiceRoomScreen.kt`, `VoiceRoomModels.kt`, `VoiceRoomEngine.kt`, `DemoVoiceRoomEngine.kt`, `VoiceRoomViewModel.kt`):
   - Remodeled stage strictly to **1 Host Seat + exactly 8 Regular Numbered Mic Seats (Seats 1–8) in a 4×2 grid**.
   - Added Host-controlled **Partner Seat** (Seat 9), rendered to the right of the host when enabled without renumbering regular seats.
   - Added Host-controlled **Attach Seats** (Bonding) linking two regular occupied seats with mutual consent and drawing a responsive, touch-transparent Compose Canvas aurora link with midpoint heart emblem (`vb_rs_bond_heart`).

---

## 2. Asset Pack Integration

All 35 original WebP assets from `VYBORA_Rooms_Seats_Update_Pack` were imported into `app/src/main/res/drawable-nodpi/`:

| Category | Assets Imported |
|---|---|
| **Game Rooms** | `vb_rs_bg_game_rooms.webp`, `vb_rs_card_room_light.webp`, `vb_rs_cover_friends.webp`, `vb_rs_cover_music.webp`, `vb_rs_cover_games.webp`, `vb_rs_cover_pk.webp`, `vb_rs_bg_empty_rooms.webp` |
| **Room Creation** | `vb_rs_bg_create_room.webp`, `vb_rs_bg_advanced_settings.webp`, `vb_rs_create_room.webp`, `vb_rs_voice_mic.webp`, `vb_rs_privacy_shield.webp`, `vb_rs_password_lock.webp`, `vb_rs_mode_music.webp`, `vb_rs_mode_games.webp`, `vb_rs_mode_pk.webp`, `vb_rs_mode_dice.webp` |
| **Partner Seat** | `vb_rs_bond_heart.webp`, `vb_rs_add_seat.webp`, `vb_rs_frame_partner.webp`, `vb_rs_frame_partner_selected.webp`, `vb_rs_seat_partner_empty.webp`, `vb_rs_bg_partner_card.webp` |
| **Attach Seats** | `vb_rs_attach_pair.webp`, `vb_rs_frame_bonded.webp`, `vb_rs_seat_linked.webp`, `vb_rs_seat_link_pending.webp`, `vb_rs_link_aurora.webp`, `vb_rs_link_gold.webp`, `vb_rs_link_pending.webp`, `vb_rs_bg_attach_card.webp` |
| **Shared Emblems** | `vb_rs_host_crown.webp`, `vb_rs_frame_seat_selected.webp`, `vb_rs_card_create_light.webp`, `vb_rs_card_option_selected.webp` |

---

## 3. Detailed Architectural Implementation

### 3.1 Game Rooms Screen (`GameRoomsScreen.kt`)
* **Color Palette:** Pearl surface `#F8F7FF`, card white `#FFFFFF`, dark readable text `#211B38`, lavender border `#ECE9FE`, brand cyan `#00D4D4`, purple brand `#8058E9`.
* **Top App Bar:** Respects `statusBarsPadding()`. Features Back icon, "Game Rooms" title with dynamic count pill, Search icon toggle button, and (+) Create Room button.
* **Hero Section:** "Find your next squad" hero panel with `vb_rs_bg_game_rooms`, golden featured squad crown (`vb_rs_host_crown`), and a direct "Create Room" shortcut.
* **Filter Chips:** Horizontal row for "All", "Games", "Deduction", "Casual", "KTV", "PK". High-contrast styling for unselected state (`#F0EDFF` background with `#211B38` text) and active purple gradient for selected state.
* **Expandable Search:** Smoothly animated search input with live case-insensitive filtering on room title, host name, and category tag. Clear (X) button.
* **Room Cards:** 18dp rounded cards with subtle drop shadow, illustrated covers (`vb_rs_cover_games`, `vb_rs_cover_friends`, `vb_rs_cover_music`, `vb_rs_cover_pk`), verified host crown, capacity badge (`X/8 on stage`), and vibrant "JOIN" button.
* **Empty State:** When no rooms match category or search, displays `vb_rs_bg_empty_rooms` illustration and a "Create a Room" action.

### 3.2 Create Voice Room Screen (`CreateVoiceRoomScreen.kt`)
* **Status Bar & Navigation Insets:** Root `Box` and `Column` configured with `.statusBarsPadding().navigationBarsPadding().imePadding()`. Zero overlap with clock, battery, or keyboard.
* **Hero Cover Art:** Banner with `vb_rs_bg_create_room` and icon `vb_rs_voice_mic`.
* **Title Validation:** Required, trimmed 3–40 characters with remaining character counter (`X/40`) and inline error feedback.
* **Mode Selection:** Cards for Normal (`vb_rs_create_room`), Music/KTV (`vb_rs_mode_music`), Games (`vb_rs_mode_games`), PK Battle (`vb_rs_mode_pk`), Dice Party (`vb_rs_mode_dice`).
* **Tags:** Friends, Music, Game, Chats, PK.
* **Privacy & Password:**
  - Public vs Private switch (`vb_rs_privacy_shield`).
  - Password switch (`vb_rs_password_lock`) revealing a password text field with eye visibility toggle (minimum 4 characters).
* **Stage Layout Summary:** Card summarizing: "Stage Layout: 1 Host + 8 Regular Mic Seats (4×2 Grid) + Optional Partner Seat".
* **Sticky Create CTA:** Elevated bottom button with progress indicator, disabled if form invalid, navigating to the created room preview.

### 3.3 Stage Remodeling (1 Host + 8 Regular + 1 Partner)
* **Seat Index Constants (`VoiceRoomModels.kt`):**
  - `HOST_SEAT_INDEX = 0`
  - `REGULAR_SEAT_START = 1`
  - `REGULAR_SEAT_END = 8`
  - `REGULAR_SEAT_COUNT = 8`
  - `PARTNER_SEAT_INDEX = 9`
  - `VOICE_ROOM_STAGE_CAPACITY = 10`
* **Layout Geometry:**
  - When `partnerEnabled == false`: Host seat (0) is centered at top with royal golden frame (`vb_vr_frame_host`) and crown (`vb_vr_emblem_crown`).
  - When `partnerEnabled == true`: Host is on the LEFT, Partner (seat 9) is on the RIGHT as a balanced duo. Partner seat uses `vb_rs_frame_partner` and `vb_rs_seat_partner_empty`.
  - Regular mic seats: Exactly 8 seats (seats 1 to 8) positioned strictly below the Host row in a responsive 4×2 grid:
    - Row 1: Seats 1, 2, 3, 4
    - Row 2: Seats 5, 6, 7, 8
* **Partner Seat Lifecycle:**
  - Host toggles Partner Seat via More > Room Controls.
  - Toggling OFF unseats the occupant (if any) with a polite notification, leaving the host centered.
  - Partner seat is an independent stage role and does not renumber seats 1–8.

### 3.4 Attach Seats (Bonding) Feature
* **Host Workflow:**
  - Host taps More > Room Controls > "Attach Seats".
  - Opens `AttachSeatsBottomSheet` showing seats 1–8 in a 4×2 grid with occupant status.
  - Host selects Seat A and Seat B (must be distinct and occupied). Selection highlighted with `GlowPurple` and `GlowCyan`.
  - Preview card displays "User A (Seat X) ↔ User B (Seat Y)".
  - Host taps "Request Aurora Bond 💕".
* **Mutual Consent Protocol:**
  - Initiates `SeatBond` with status `REQUESTED`.
  - Participant receives `AlertDialog`: "The Host has requested to form an Aurora Seat Bond between your seat and Seat X. Do you accept this bond?"
  - Bond transitions to `ACTIVE` only upon acceptance.
* **Canvas Connection Rendering:**
  - Each seat tracks its center position using `Modifier.onGloballyPositioned`.
  - Behind the seats, a Compose `Canvas` connects the two seat centers with a glowing quadratic bezier curve (radial gradient cyan to pink, 6dp stroke glow + 2dp core).
  - The Canvas is 100% `pointerInput`-free and touch-transparent.
  - At the midpoint of the curve, a clickable heart emblem (`vb_rs_bond_heart`) is rendered.
* **Bond Details & Detachment:**
  - Tapping the heart emblem opens `BondDetailsBottomSheet`.
  - Host or either occupant can tap "Detach Seat Bond".
  - When a bonded participant leaves their seat or is kicked, the bond is automatically invalidated and detached.

---

## 4. Verification & Testing

* **Unit Test Suite:** `RoomsAndSeatsUnitTest.kt`
  - `testStageConstantsAndInvariants()`: Validates 0=Host, 1..8=Regular, 9=Partner, 10=Capacity.
  - `testSeatBondCanActivateValidation()`: Validates mutual consent, self-pair prevention, and distinct seat enforcement.
  - `testPartnerSeatToggleAndGracefulEviction()`: Validates partner enable/disable, seat taking, and automatic graceful unseating when disabled.
  - `testAttachSeatsRequestAndActivation()`: Validates bond request, ghost acceptance simulation, `isSeatInBond` queries, and explicit detachment.
  - `testSeatDepartureInvalidatesBond()`: Validates that when a bonded occupant leaves their seat, the bond is automatically invalidated and detached.
