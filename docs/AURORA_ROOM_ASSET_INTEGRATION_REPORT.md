# VYBORA — Aurora Lounge Asset Integration Report
**Project:** VYBORA (`com.sundram.weplay`)
**Asset Pack:** `VYBORA_Aurora_Joined_Voice_Room_Asset_Pack`
**Date:** 2026-11-21
**Target Directory:** `app/src/main/res/drawable-nodpi/`

---

## 1. Import Summary

- **Total Supplied WebP Assets:** 93
- **Import Destination:** `app/src/main/res/drawable-nodpi/`
- **Naming Convention:** `vb_vr_*` (lowercase, underscored, collision-free)
- **Status:** All 93 files verified on disk with correct alpha transparency and dimensions.

---

## 2. Categorized Asset Inventory & Mapping

### A. Ambient Room Backdrops (2 Assets)
- `vb_vr_bg_aurora` (1080×1920): Primary atmospheric Aurora Lounge backdrop featuring cosmic purple/violet gradients and cyan aurora borealis ribbons.
- `vb_vr_bg_celestial` (1080×1920): Alternative celestial starfield backdrop.

### B. Transparent Avatar Frames (6 Assets — 512×512)
Overlaid on top of circular participant avatars:
- `vb_vr_frame_host`: Royal golden crown frame with luminous ring.
- `vb_vr_frame_speaker`: Electric cyan pulsing audio ring frame.
- `vb_vr_frame_moderator`: Violet shield frame for room moderators.
- `vb_vr_frame_vip`: Diamond/crystalline frame for VIP members.
- `vb_vr_frame_normal`: Subtle frosted ring frame for standard speakers.
- `vb_vr_frame_favorite`: Rosy heart frame for followed/favorited hosts.

### C. Seat-State Graphics (7 Assets — 512×512)
- `vb_vr_seat_empty`: Circular frosted seat with glowing "+" invitation.
- `vb_vr_seat_locked`: Padlock security graphic for restricted seats.
- `vb_vr_seat_request`: Hand-raised graphic for pending seat applicants.
- `vb_vr_seat_speaking`: Active audio waves indicating real-time voice telemetry.
- `vb_vr_seat_muted`: Red slash microphone indicating muted state.
- `vb_vr_seat_invited`: Glowing mail/ticket icon for seat invitations.
- `vb_vr_seat_occupied`: Ambient circular base for seated speakers.

### D. Control Icons & Action Buttons (52 Assets — 160×160)
- **Navigation:** `vb_vr_ic_back`, `vb_vr_ic_leave`, `vb_vr_ic_x`, `vb_vr_ic_check`, `vb_vr_ic_search`.
- **Audio & Mic:** `vb_vr_ic_mic`, `vb_vr_ic_mic_off`, `vb_vr_ic_mic_cyan`, `vb_vr_ic_volume`, `vb_vr_ic_volume_off`, `vb_vr_ic_headphones`, `vb_vr_ic_bluetooth`.
- **Social & Room Info:** `vb_vr_ic_users`, `vb_vr_ic_users_cyan`, `vb_vr_ic_user_plus`, `vb_vr_ic_heart`, `vb_vr_ic_heart_fill`, `vb_vr_ic_heart_cyan`, `vb_vr_ic_share`, `vb_vr_ic_copy`, `vb_vr_ic_info`, `vb_vr_ic_more`, `vb_vr_ic_more_cyan`.
- **Chat & Input:** `vb_vr_ic_chat`, `vb_vr_ic_chat_cyan`, `vb_vr_ic_send`, `vb_vr_ic_send_cyan`, `vb_vr_ic_smile`, `vb_vr_ic_camera`, `vb_vr_ic_image`.
- **Gifting & Economy:** `vb_vr_ic_gift`, `vb_vr_ic_gift_cyan`.
- **Activities & Games:** `vb_vr_ic_game`, `vb_vr_ic_game_cyan`, `vb_vr_ic_swords`, `vb_vr_ic_swords_cyan`, `vb_vr_ic_dice`, `vb_vr_ic_poll`.
- **Moderation & Security:** `vb_vr_ic_lock`, `vb_vr_ic_unlock`, `vb_vr_ic_shield`, `vb_vr_ic_flag`, `vb_vr_ic_settings`, `vb_vr_ic_crown`, `vb_vr_ic_star`, `vb_vr_ic_hand`, `vb_vr_ic_hand_cyan`.

### E. Premium Emblems (9 Assets — 512×512)
- `vb_vr_emblem_crown`: Royal gold host emblem.
- `vb_vr_emblem_audio_ring`: Neon cyan speaker emblem.
- `vb_vr_emblem_shield`: Purple guardian emblem.
- `vb_vr_emblem_vip_diamond`: Diamond crystal VIP emblem.
- `vb_vr_emblem_studio_mic`: Pro studio broadcast microphone emblem.
- `vb_vr_emblem_pink_gift`: Deluxe 3D gift box emblem.
- `vb_vr_emblem_trophy`: Golden victory trophy emblem.
- `vb_vr_emblem_dice_cards`: Gaming and casino challenge emblem.
- `vb_vr_emblem_heart_star`: Social affinity emblem.

### F. Role Chips, Status Bars & Banners (11 Assets)
- `vb_vr_chip_host`, `vb_vr_chip_moderator`, `vb_vr_chip_vip`, `vb_vr_chip_speaking`, `vb_vr_chip_request`.
- `vb_vr_bar_pk`, `vb_vr_bar_reward`.
- `vb_vr_banner_music`, `vb_vr_banner_games`, `vb_vr_banner_chill`.
- `vb_vr_fx_sparkles`.

### G. Fictional Demo Avatars (6 Assets — 512×512)
- `vb_vr_avatar_demo_01` through `vb_vr_avatar_demo_06`: Used strictly for simulated offline preview participants; real users are rendered via `UserProfile` photo or initials.
