# VYBORA — Aurora Lounge Joined Voice Room Initial Audit
**Project:** VYBORA (`com.sundram.weplay`)
**Target Screen:** Joined Voice Room (`LiveVoiceRoomScreen.kt`, `VoiceRoomViewModel.kt`, `VoiceRoomModels.kt`, `VoiceRoomEngine.kt`)
**Audit Date:** 2026-11-21
**Auditor:** Principal Android Architect & Jetpack Compose Engineer

---

## 1. Executive Summary

This audit assesses the state of the third main screen in VYBORA: the **Joined Voice Room** (`LiveVoiceRoomScreen.kt`). While the first two pages (Gaming / Home and Voice Lounge Discovery) have been fully upgraded with the distinctive VYBORA design language, the Joined Voice Room previously used a basic placeholder dark-themed layout with large initials-based avatars, missing interactive sheets, non-functional activity icons, and zero Aurora Lounge 2.0 assets.

With the provision of the 93-asset **Aurora Lounge Asset Pack**, our objective is to completely transform this screen into a world-class, immersive, and fully interactive social voice room experience while maintaining strict separation between real backend integrations and offline preview/demo flows.

---

## 2. Existing Functionality vs Deficiencies

### A. Working Local / Engine Capabilities
- **Dual Transport Architecture:** `VoiceRoomViewModel` cleanly switches between `LiveKitVoiceRoomEngine` (when `VoiceRoomServer.isConfigured` is true) and `DemoVoiceRoomEngine` (local simulated state).
- **Basic Seat Occupancy Model:** `takeSeat(seat)` and `leaveSeat()` exist in `VoiceRoomEngine`, allowing a user to sit and leave.
- **Microphone Runtime Permission Flow:** `toggleMicrophone` invokes the Android `RECORD_AUDIO` launcher and updates engine mic state.
- **Basic Text Chat List:** `DarkChatPanel` renders messages from `VoiceRoomState.messages`.
- **Exit Dialog:** Basic leave confirmation dialog disconnecting the engine and popping the back stack.

### B. Broken / Missing Actions & Deficiencies
1. **Asset Visuals:**
   - Room background was a simple programmatic vertical gradient (`RoomBgTop` -> `RoomBgBottom`) with basic canvas circles; none of the supplied high-resolution ambient backgrounds (`vb_vr_bg_aurora`, `vb_vr_bg_celestial`) were used.
   - Avatars were plain circles with letter initials (`InitialsAvatar`), completely lacking the supplied transparent role frames (`vb_vr_frame_host`, `vb_vr_frame_speaker`, `vb_vr_frame_moderator`, `vb_vr_frame_vip`, `vb_vr_frame_normal`).
   - Seat states used hardcoded Canvas drawings instead of the rich 512×512 WebP seat graphics (`vb_vr_seat_empty`, `vb_vr_seat_locked`, `vb_vr_seat_request`, `vb_vr_seat_speaking`, `vb_vr_seat_muted`, `vb_vr_seat_invited`).
2. **Missing Room Header Controls:**
   - Room ID copying was not wired to the Android clipboard.
   - Favorite/heart toggle was missing.
   - Participant count was not clickable to open a full Participants sheet.
   - Share/Invite button was an empty lambda (`/* share */`).
   - More menu button was completely absent.
3. **Missing Speaker Stage Interactions:**
   - Tapping an occupied seat did not open that participant's profile sheet.
   - Seat locking/unlocking by host was not supported in the UI.
   - Host seat was statically placed without host moderation options.
4. **Missing Activity & Social Sheets:**
   - **PK Battle Entry:** No PK sheet or active battle progress UI existed (`vb_vr_ic_swords`, `vb_vr_bar_pk`).
   - **In-Room Games:** No interactive games sheet existed (Truth or Dare, Dice Challenge, Quick Poll).
   - **Room Actions / More Sheet:** Social, Personal, Safety, and Host management actions were absent.
   - **Audio Settings Sheet:** No output routing (Speaker / Earpiece) or audio diagnostics modal existed.
5. **Gifting & Animations:**
   - Previous gift panel was a barebones 3-column grid that only showed a toast on tap.
   - Lacked gift category filtering (Popular, Premium, Luxury, Event), recipient selection, quantity multiplier (1x, 5x, 10x, 99x), gift preview animations, and room chat broadcast events.

---

## 3. Resource Availability

All 93 WebP assets from the `VYBORA_Aurora_Joined_Voice_Room_Asset_Pack` have been imported into `app/src/main/res/drawable-nodpi/`:
- **Ambient Backdrops (2):** `vb_vr_bg_aurora`, `vb_vr_bg_celestial`
- **Transparent Avatar Frames (6):** `vb_vr_frame_host`, `vb_vr_frame_speaker`, `vb_vr_frame_moderator`, `vb_vr_frame_vip`, `vb_vr_frame_normal`, `vb_vr_frame_favorite`
- **Seat-State Graphic Fallbacks (7):** `vb_vr_seat_empty`, `vb_vr_seat_locked`, `vb_vr_seat_request`, `vb_vr_seat_speaking`, `vb_vr_seat_muted`, `vb_vr_seat_invited`, `vb_vr_seat_occupied`
- **Demo Avatars (6):** `vb_vr_avatar_demo_01` through `vb_vr_avatar_demo_06`
- **Control Icons (52):** Navigation, audio, media, social, and moderation icons with cyan variants
- **Premium Emblems (9):** Crown, audio ring, shield, VIP diamond, studio mic, gift, trophy, dice cards, heart star
- **Role Chips & Bars (8):** Host, moderator, VIP, speaking, request chips; PK & reward progress bars
- **Activity Banners (3):** Music, games, chill

---

## 4. UI Architecture & Color Palette

To create an immersive nighttime counterpart to the main app's white/lavender aesthetic:
```kotlin
val RoomBackground = Color(0xFF17142F)
val RoomSurface    = Color(0xFF261A42)
val RoomSurfaceSubtle = Color(0xFF1C1333)
val AccentPurple   = Color(0xFF8058E9)
val AccentLavender = Color(0xFFD7C7FF)
val AccentCyan     = Color(0xFF39D5DC)
val AccentGold     = Color(0xFFFFCE76)
val PrimaryText    = Color(0xFFF8F7FF)
val SecondaryText  = Color(0xFFBBB5D1)
```

---

## 5. Implementation Roadmap (Milestones)

1. **Milestone 1:** Import assets & build responsive 6-tier Aurora Lounge screen structure (Backdrop, Header, Status, Speaker Stage with role frames, Activities Row, Chat Panel, Fixed Action Dock).
2. **Milestone 2:** Implement Core Room Interactions (Seat click actions, Mic toggle, Participants Sheet, User Profile Bottom Sheet, Audio Settings Sheet, Exit Room Dialog).
3. **Milestone 3:** Implement Premium Gifting Engine (Category tabs, recipient picker, quantity multiplier, gift chat broadcast, and animated overlay).
4. **Milestone 4:** Implement PK Battle & In-Room Activities (PK Sheet with live simulated battle score, Dice Roll challenge, Truth or Dare prompt deck, Quick Poll).
5. **Milestone 5:** Connect LiveKit Voice Transport boundaries & ensure graceful fallback.
6. **Milestone 6:** Unit tests, Gradle assembleDebug, and final QA matrix report.
