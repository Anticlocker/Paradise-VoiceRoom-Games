# VYBORA ROOMS & SEATS AUDIT REPORT
**Generated:** 2026-11-21  
**Project:** VYBORA Android Social Gaming Platform  
**Component:** Game Rooms, Create Voice Room, Joined Voice Room (Aurora Lounge 2.0 Stage & Bonding)

---

## 1. Executive Summary & Status Overview

This audit document catalogs every user-interactive component, route, state machine, and service boundary across the three target screens:
1. **Game Rooms Discovery Screen** (`GameRoomsScreen.kt`)
2. **Create Voice Room Screen** (`CreateVoiceRoomScreen.kt`)
3. **Joined Voice Room / Aurora Lounge 2.0 Screen** (`LiveVoiceRoomScreen.kt`)

| Screen / Capability | Current Implementation Status | Visual Quality | Functional State | Root Cause of Identified Defects |
|---|---|---|---|---|
| **Game Rooms Listing** | `LOCAL DEMO` | Dark background, unreadable white text on white cards | Functional navigation, but poor contrast & missing hero | Background set to `#0D0B18` or unstyled, text color using `TextMain` without surface contrast; no search input; no hero banner. |
| **Create Voice Room** | `LOCAL DEMO` | Status bar collision; missing padding | Interactive form, mock creation | Missing `Modifier.statusBarsPadding()` and `WindowInsets` handling on the root container, causing header to collide with clock/battery. |
| **Joined Room Stage (Seats)** | `LOCAL DEMO` (SFU fallback available) | Aurora night theme | 1 host + 7 seats (8 total) | Only 7 regular speaker seats rendered instead of the invariant: 1 Host + 8 Regular Mic seats (4×2) + 1 Optional Partner seat. |
| **Partner Seat** | `MISSING` | N/A | Missing | No partner seat model, no host toggle in More menu, no partner graphic rendering. |
| **Attach Seats (Bonding)** | `MISSING` | N/A | Missing | No bond state machine, no mutual consent protocol, no Canvas link rendering. |
| **LiveKit SFU Voice Engine** | `BLOCKED` (Network/Server required) | N/A | Standby | Real LiveKit requires `livekit.url` in `local.properties`. Gracefully falls back to `DemoVoiceRoomEngine`. |

---

## 2. Screen-by-Screen Audit & Control Breakdown

### 2.1 Game Rooms Screen (`GameRoomsScreen.kt`)
* **Route:** `Routes.GAME_ROOMS` (`"game_rooms"`)
* **Entry Points:**
  - Games/Home Screen → Game Rooms shortcut pill or hero button.
  - Voice Lounge Screen → "All Rooms" shortcut.
* **Controls & Interactivity:**
  1. **Top Back Button:**
     - *Callback:* `nav.popBackStack()`
     - *State:* Working.
  2. **Top Create (+) Button:**
     - *Callback:* `nav.navigate(Routes.CREATE_ROOM)`
     - *State:* Working.
  3. **Filter Chips (All, Games, Deduction, Casual, KTV):**
     - *Callback:* `selectedCategory = cat`
     - *State:* Working, but unselected chips lack contrast against card surfaces.
  4. **Search Action:**
     - *Current:* Missing.
     - *Fix Required:* Add expandable native search bar with live text filtering by title & host name.
  5. **Room Cards:**
     - *Current:* Uses `CoverThumb` with simple colored background; text contrast issues.
     - *Fix Required:* Use `vb_rs_cover_*` assets, readable dark `#211B38` titles, host badges, verified status, capacity badges, and vibrant "JOIN" button.
  6. **Hero Banner:**
     - *Current:* Missing.
     - *Fix Required:* Add "Find your next squad" hero panel using `vb_rs_bg_game_rooms`.

---

### 2.2 Create Voice Room Screen (`CreateVoiceRoomScreen.kt`)
* **Route:** `Routes.CREATE_ROOM` (`"create_room"`)
* **Entry Points:**
  - Game Rooms Top Bar (+) button.
  - Voice Lounge Header "Create Room" button.
* **Defects & Root Cause Analysis:**
  - **Status Bar Collision:** In `CreateVoiceRoomScreen.kt`, the root `Column` lacks `.statusBarsPadding()`. The top bar `Row` only has `padding(top = 8.dp)`, which places the title "Create Voice Room" and the close button directly underneath the phone's clock, battery, and notification icons.
  - **Missing Inset Bottom:** The sticky Create Room button does not account for `navigationBarsPadding()` or `imePadding()`, causing keyboard collision.
* **Controls & Interactivity:**
  1. **Close / Cancel Button:**
     - *Callback:* `nav.popBackStack()`
     - *State:* Working.
  2. **Cover Picker:**
     - *Current:* Basic circular placeholder.
     - *Fix Required:* Use `vb_rs_bg_create_room` with icon overlay (`vb_rs_voice_mic` / `vb_rs_create_room`).
  3. **Title Input:**
     - *Current:* Basic text field, minimal validation.
     - *Fix Required:* 3–40 characters validation, character counter, error indicator.
  4. **Room Modes:**
     - *Current:* Hardcoded canvas drawings.
     - *Fix Required:* Mode selection cards with `vb_rs_mode_*` assets (Normal, Music, Games, PK, Dice).
  5. **Tags:**
     - *Current:* Friends, Music, Game, Chats.
     - *State:* Functional.
  6. **Password Lock:**
     - *Current:* Switch only, no actual password text input field.
     - *Fix Required:* Password toggle revealing a secure password entry field with visibility toggle and validation.
  7. **Stage Seat Configuration Preview:**
     - *Current:* Selector for 8, 10, 12 seats.
     - *Fix Required:* Explicitly clarify that standard stage consists of 1 Host + 8 Regular Mic seats, with optional Partner seat.
  8. **Create Room Button:**
     - *Callback:* Simulates creation and navigates to `Routes.roomRoute(demoRoom.id)`.
     - *State:* Functional mock (`LOCAL DEMO`).

---

### 2.3 Joined Voice Room Screen (`LiveVoiceRoomScreen.kt`)
* **Route:** `Routes.ROOM` / `room/{roomId}`
* **Theme:** Deep Nighttime Aurora Lounge 2.0 (`#17142F`, `vb_vr_bg_aurora`, `vb_vr_bg_celestial`).
* **Stage Architecture Audit:**
  - *Current Implementation:*
    - Host seat: Seat 0.
    - Speaker Row 1: Seats 1, 2, 3 (3 seats).
    - Speaker Row 2: Seats 4, 5, 6, 7 (4 seats).
    - Total seats: 8 (1 host + 7 speakers).
  - *Required Architecture:*
    - **Host Seat:** Seat 0, centered at top (or on left when partner is enabled).
    - **Partner Seat:** Seat 9, rendered to the RIGHT of the host ONLY when `partnerEnabled == true`.
    - **Regular Mic Seats:** Exactly 8 seats (`seat_1` through `seat_8`, numbered 1 to 8), arranged in a responsive 4×2 grid below the Host/Partner row:
      - Row 1: Seats 1, 2, 3, 4
      - Row 2: Seats 5, 6, 7, 8
    - Total stage capacity: up to 10 positions (1 Host + 8 Regular + 1 Partner).
* **Partner Seat Feature Requirements:**
  - Controlled by Host via More > Room Controls / Seat Management.
  - Toggling ON displays the partner seat immediately to the right of the host using `vb_rs_frame_partner` and `vb_rs_seat_partner_empty`.
  - Toggling OFF unseats the occupant (if any) with a polite notification, leaving the host centered.
  - Does NOT count toward or renumber the 8 regular numbered mic seats.
* **Attach Seats (Bonding) Feature Requirements:**
  - Controlled by Host via More > Room Controls > "Attach Seats".
  - Opens selection sheet displaying regular occupied seats (1–8). Host selects two distinct occupied seats.
  - Mutual consent protocol: Sends consent invitations to both occupants.
  - Bond becomes `ACTIVE` only upon mutual consent.
  - Renders a glowing aurora link between measured centers of the two seats using Compose `Canvas` + `vb_rs_bond_heart` and `vb_rs_frame_bonded`.
  - Must NOT intercept touch inputs (`pointerInput`-free overlay).
  - Tapping bond emblem opens Bond Details with option to Detach.

---

## 3. Service Boundaries & State Classification

| Feature | Classification | Description |
|---|---|---|
| Room Browsing | `LOCAL DEMO` | Backed by `Demo.rooms` dataset with search and category filtering. |
| Room Creation | `LOCAL DEMO` | Validates form input, creates local session state, navigates to joined room. |
| Voice Room Audio | `LOCAL DEMO / LIVE SFU` | If `VoiceRoomServer.isConfigured`, connects to self-hosted LiveKit SFU. Otherwise, falls back to `DemoVoiceRoomEngine` with simulated speaker rotation. |
| Partner Seat State | `LOCAL DEMO` | Managed via `VoiceRoomState.partnerEnabled` in ViewModel and Engine. |
| Seat Bonding State | `LOCAL DEMO` | Managed via `VoiceRoomState.activeBonds` and `SeatBond` state machine. |
| In-App Purchasing / Top Up | `LOCAL DEMO` | UI demonstration with configurable packages. |

---

## 4. Remediation Plan

1. **Phase 2:** Redesign `GameRoomsScreen.kt` with white/lavender `#F8F7FF` theme, readable dark titles, hero banner, search, and refined room cards.
2. **Phase 3:** Redesign `CreateVoiceRoomScreen.kt` with status bar padding, cover picker, 3–40 char validation, mode cards, password toggle, and clear 8-seat stage preview.
3. **Phase 4 & 5:** Upgrade `VoiceRoomModels.kt`, `VoiceRoomEngine.kt`, `DemoVoiceRoomEngine.kt`, and `VoiceRoomViewModel.kt` to support:
   - 1 Host + 8 Regular Seats (indices 1..8) + 1 Partner Seat (index 9).
   - `SeatBond` model with mutual consent and state transitions.
   - Host controls for Partner Seat and Attach Seats.
4. **Phase 6:** Redesign `LiveVoiceRoomScreen.kt` stage to 1 Host (+ Partner) + 8 Regular (4×2 grid), add Canvas non-intercepting bond link, and integrate Manage Seats bottom sheets.
5. **Phase 9:** Add unit tests in `RoomsAndSeatsUnitTest.kt` and run Gradle build validation.
