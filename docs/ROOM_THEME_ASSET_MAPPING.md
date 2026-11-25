# Room Theme Asset Mapping

- Source bundle: `C:\Users\Admin\Downloads\Voice_Room_10_Theme_Starter_Assets` (`52` files inspected: `IMPLEMENTATION_GUIDE.md`, `theme_catalog.json`, `50` PNG/WEBP assets)
- Target Android assets JSON: [room_theme_catalog.json](file:///c:/Users/Admin/OneDrive/Desktop/final%20weplay/app/src/main/assets/room_theme_catalog.json)
- Target Registry: [RoomThemeCatalog.kt](file:///c:/Users/Admin/OneDrive/Desktop/final%20weplay/app/src/main/java/com/sundram/weplay/voiceroom/theme/RoomThemeCatalog.kt) (`RoomThemeCatalog`, `RoomThemeResourceRegistry`)
- Artwork status across all 10 themes: `procedural_starter` (replace drawable files cleanly in `drawable-nodpi/` later with illustrated/animated artwork without changing theme IDs, unlock logic, or PostgreSQL rows).

| # | Theme ID | Theme Name | Unlock Rule | Price (`priceCoins`) | Accent | Background (`drawable-nodpi`) | Thumbnail (`drawable-nodpi`) | Seat Frame (`drawable-nodpi`) | Host Frame (`drawable-nodpi`) | Overlay (`drawable-nodpi`) |
| --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- |
| 1 | `classic_lounge` | Classic Lounge | `free` (Crest Lv.1, default equipped) | `null` | `#40345E` | `room_bg_classic_lounge.webp` | `room_thumb_classic_lounge.webp` | `room_seat_classic_lounge.png` | `room_host_classic_lounge.png` | `room_overlay_classic_lounge.png` (unused in restrained default) |
| 2 | `ocean_lounge` | Ocean Lounge | `crest` (Crest Lv.3) | `null` | `#1599AC` | `room_bg_ocean_lounge.webp` | `room_thumb_ocean_lounge.webp` | `room_seat_ocean_lounge.png` | `room_host_ocean_lounge.png` | `room_overlay_ocean_lounge.png` (alpha `0.24f`) |
| 3 | `velvet_lounge` | Velvet Lounge | `crest` (Crest Lv.5) | `null` | `#B16C96` | `room_bg_velvet_lounge.webp` | `room_thumb_velvet_lounge.webp` | `room_seat_velvet_lounge.png` | `room_host_velvet_lounge.png` | `room_overlay_velvet_lounge.png` (alpha `0.24f`) |
| 4 | `royal_lounge` | Royal Lounge | `crest` (Crest Lv.7) | `null` | `#C6A259` | `room_bg_royal_lounge.webp` | `room_thumb_royal_lounge.webp` | `room_seat_royal_lounge.png` | `room_host_royal_lounge.png` | `room_overlay_royal_lounge.png` (alpha `0.24f`) |
| 5 | `aurora_palace` | Aurora Palace | `crest` (Crest Lv.9) | `null` | `#9250C8` | `room_bg_aurora_palace.webp` | `room_thumb_aurora_palace.webp` | `room_seat_aurora_palace.png` | `room_host_aurora_palace.png` | `room_overlay_aurora_palace.png` (alpha `0.24f`) |
| 6 | `celestial_kingdom` | Celestial Kingdom | `crest` (Crest Lv.11) | `null` | `#747FE7` | `room_bg_celestial_kingdom.webp` | `room_thumb_celestial_kingdom.webp` | `room_seat_celestial_kingdom.png` | `room_host_celestial_kingdom.png` | `room_overlay_celestial_kingdom.png` (alpha `0.24f`) |
| 7 | `imperial_realm` | Imperial Realm | `crest` (Crest Lv.16) | `null` | `#A98946` | `room_bg_imperial_realm.webp` | `room_thumb_imperial_realm.webp` | `room_seat_imperial_realm.png` | `room_host_imperial_realm.png` | `room_overlay_imperial_realm.png` (alpha `0.24f`) |
| 8 | `eternal_galaxy` | Eternal Galaxy | `crest` (Crest Lv.21) | `null` | `#5656D8` | `room_bg_eternal_galaxy.webp` | `room_thumb_eternal_galaxy.webp` | `room_seat_eternal_galaxy.png` | `room_host_eternal_galaxy.png` | `room_overlay_eternal_galaxy.png` (alpha `0.24f`) |
| 9 | `sakura_garden` | Sakura Garden | `store` (unpriced) | `null` | `#F09AB9` | `room_bg_sakura_garden.webp` | `room_thumb_sakura_garden.webp` | `room_seat_sakura_garden.png` | `room_host_sakura_garden.png` | `room_overlay_sakura_garden.png` (alpha `0.24f`) |
| 10 | `rose_palace` | Rose Palace | `store` (unpriced) | `null` | `#D6A287` | `room_bg_rose_palace.webp` | `room_thumb_rose_palace.webp` | `room_seat_rose_palace.png` | `room_host_rose_palace.png` | `room_overlay_rose_palace.png` (alpha `0.24f`) |

## Overlay & Rendering Compliance
- All 50 drawable files imported into `app/src/main/res/drawable-nodpi/` with exact `room_bg_`, `room_thumb_`, `room_seat_`, `room_host_`, `room_overlay_` prefixes.
- `IMPLEMENTATION_GUIDE.md` rule enforced in [RoomThemeRenderer.kt](file:///c:/Users/Admin/OneDrive/Desktop/final%20weplay/app/src/main/java/com/sundram/weplay/voiceroom/theme/RoomThemeRenderer.kt): Classic Lounge omits extra overlay layers and continuous animations; unlocked themes blend `room_overlay_<id>` at `alpha = 0.24f` above `room_bg_<id>` with a dark readability gradient so stage text and chat remain crisp.
- Backward-compatibility drawables (`vb_vr_*`, `aurora3_seat_*`) were preserved so existing unit suites continue compiling and passing cleanly.
