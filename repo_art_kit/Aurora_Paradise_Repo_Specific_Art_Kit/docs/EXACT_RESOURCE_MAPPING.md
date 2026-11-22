# Exact Source Mapping — Antigravity integration guide

This is NOT a patch; copy new files and change references as appropriate.

| Existing component / file | New artwork | Integration note |
|---|---|---|
| `ui/theme/AuroraParadise.kt` → `AuroraGamesBackground` | `ap3r_bg_games_palace` | Replace current `aurora3_bg_games`; keep `BoxScope` content, alpha/scrims, star logic |
| `ui/theme/AuroraParadise.kt` → `FestivalEventBackground` | `ap3r_bg_event_festival` | Replace current `aurora3_bg_event`; darken text areas |
| `ui/theme/AuroraParadise.kt` → `AuroraProfileBackground` | `ap3r_bg_profile_dream` | Limit art to profile header; do not put busy palace behind menu text |
| `ui/theme/AuroraParadise.kt` → `AuroraVoiceRoomBackground` / `CelestialVoiceRoomBackground` | `ap3r_bg_voice_midnight` | Replace background in the relevant helper and preserve readability |
| `ui/screens/GameLobbyScreen.kt` → `HeroFeaturedGameCard` | `ap3r_banner_home_festival` | Optional extra festival hero. Existing petrol pump character can remain for its card; avoid game title baked into art |
| `ui/screens/VoiceRoomListScreen.kt` | `ap3r_banner_voice_rooms` | Add only where existing featured-room banner data permits |
| `data/EventModels.kt` → `evt_welcome.coverRes` | `ap3r_event_welcome` | Swap drawable, keep title/status as Compose text |
| `data/EventModels.kt` → `evt_first_topup.coverRes` | `ap3r_event_first_topup` | Swap drawable, but keep sample offer unpurchasable without verified billing |
| `data/EventModels.kt` → `evt_starlight.coverRes` | `ap3r_event_starlight` | Swap drawable |
| `data/EventModels.kt` → `evt_family_fest.coverRes` | `ap3r_event_family` | Swap drawable |
| `data/EventModels.kt` → `evt_royal_arena.coverRes` | `ap3r_event_pk` | Verify ID string when using source rather than assuming it |
| `ui/components/GiftPackOffersSheet.kt` + `ui/theme/AuroraParadise.kt` → `PremiumGiftPack` | `ap3r_fx_gift_ambient_glow`, `ap3r_fx_reward_pedestal` | Decorative layers behind existing image, DO NOT use as the gift-box object |
| `ui/screens/GameLobbyScreen.kt` → `CelestialHeader` call | no asset | Replace hardcoded `crystals = 850L` with real verified balance or hide pill |
| `ui/nav/WePlayBottomBar.kt` | existing SVG/XML | Keep five tabs; confirm no overlay intercepts input |

`ap3r_overlay_sparkle_particles` is an optional effect. Avoid animated static backgrounds on every scrolling cell.

For dynamic sizing: use ContentScale.Crop for full-screen backgrounds, ContentScale.FillBounds ONLY for truly stretchable gradient surfaces, and ContentScale.Fit for illustrated characters and ornaments.
