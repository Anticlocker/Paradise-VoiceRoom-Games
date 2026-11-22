# Remaining production art — exact export requirements

These assets are **not delivered in the current ZIP**. They should be commissioned or rendered individually; do not upscale the original 78×82 coin or 128×97 gift crop and claim it is new 3D artwork.

| Target asset | Proposed size | Format | Alpha | Integration | Art direction |
|---|---:|---|---|---|---|
| 3D gift chest closed, lid, bows, inner glow | 1024×1024 each layer | PNG/WebP | yes | `PremiumGiftPack`, `GiftPackOffersSheet.kt` | Gold ribbon, amethyst crystal body, matching lighting, no text |
| Gold coin currency | 512×512 | WebP | yes | `CelestialHeader`, Top Up | Embossed original star, thick metallic rim, centered silhouette |
| Blue crystal currency | 512×512 | WebP | yes | `CelestialHeader` only when real wallet exists | Cut diamond, cyan/ice blue, distinct facet highlights |
| Default animated avatar-frame tier overlays | 768×768 per tier | WebP + Compose motion | yes, clear center | `AnimatedAvatar.kt`, `AvatarWithFrame.kt` | Common, Rare, Epic, Legendary, Celestial, VIP |
| Game launch tiles | 640×640 | WebP | yes or deliberate full-tile artwork | `FantasyGameCard` | One per ACTUAL game, no spoofed play buttons; maintain safe title area |
| Distinct voice-room scenic backgrounds | 1080×1920 each | WebP | no | `CelestialVoiceRoomBackground` | Quiet middle for seats; dark enough for readable chat; no fake people |
| Event card illustrations | 1200×560 | WebP | optional | `EventModels.kt` | 45–60% left text safe-zone; status label is Compose, not baked |
| Rankings podium sets | 1200×600 and 768×768 frames | WebP | yes for podiums/frames | `RankingScreen.kt` | Original gold/silver/bronze and category-specific crests |
| VIP/Shop product illustrations | 640×640 each | WebP | yes | Actual owned catalog | Render only items that exist in backend or demo catalog |
| Family crest overlays | 768×768 | WebP | yes | `FamilyScreen.kt` | Hierarchical symbols, avoid copying WePlay guild emblems |

## Delivery QA

- No logo/app name (name not finalized).
- No on-image prices, dates, unread counts, levels, room occupancy, event winners or balances.
- Alpha outside silhouette should be 0 for transparent UI objects; avoid contaminated dark rectangular edge.
- Render separate frames/sprites where actual image-layer animation is expected.
- Verify nearest-neighbor and bilinear downscale to a 56dp icon and a 128dp card.
- Manifest each image with pixels, role, safe zone, transparency and license/source information.
- Compare a device screenshot (not concept collage) to the approved Games/Event/Gift reference.
