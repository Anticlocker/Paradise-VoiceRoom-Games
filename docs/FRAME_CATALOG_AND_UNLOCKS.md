# PROFILE FRAME CATALOG, ECONOMY & UNLOCK SPECIFICATION

**Status:** PROPOSED PENDING PRODUCT & MONETIZATION APPROVAL  
**Engine:** Jetpack Compose Canvas Dynamic Animation Engine  
**Asset Pack:** `Leaderboard_Animation_Kit` (30 Vectors + Native Canvas Layers)

---

## 1. Frame Tiers Overview

All cosmetic frames are categorized into six distinct tiers. Animation durations and motion characteristics conform strictly to `docs/FRAME_ANIMATION_SPEC.md`.

| Tier | Duration | Motion Characteristics | Layer Stack | Target Audience |
|---|---|---|---|---|
| **COMMON** | Static (0 ms) | Static clean border & dual ring | Base cyan & light cyan ring | All players (Starter) |
| **RARE** | 2200 ms | Alpha pulse (0.65f..1.0f) | Electric cyan ring, ambient diffuse glow, top diamond jewel | Level 5+ or Coin Shop |
| **EPIC** | 4200 ms | Clockwise orbital rotation | Royal violet ring, lilac inner accent, 8 rotating orbit dots | Level 10+ Achievements or Shop |
| **LEGENDARY** | 5600 ms | Dual counter-rotating orbits + star twinkle (1400ms) | Amber gold ring, dual counter-orbiting arcs, 12 sparkling stars | VIP Tier 7+ or Coin Shop |
| **MYTHIC** | 6400 ms | Slow floating gold crown (-6..-1dp) + 3 jewels orbit | Floating tiara, dual concentric rings, 3 orbiting diamond gems | Family Guild Champions or Shop |
| **CELESTIAL** | 8000 ms | Aurora sweep gradient + 6 orbiting starlight gems | Sweep gradient (teal/purple/white), outer aura, 6 celestial gems | Global Events / Elite Top Tier |

---

## 2. Proposed Catalog & Monetization Matrix

> [!IMPORTANT]
> All coin prices and account level milestones listed below are **PROPOSED** and subject to final economy balancing before production release.

| Frame ID | Display Name | Tier | Unlock Source | Min Level [PROPOSED] | Price (Coins) [PROPOSED] | Duration | Purchasable |
|---|---|---|---|---|---|---|---|
| `frame_common_aurora` | Aurora Breeze | COMMON | Starter Default | Lv. 1 | 0 (Free) | Permanent | No |
| `frame_rare_cyan_pulse` | Cyan Pulse | RARE | Account Level | Lv. 5 | 0 (Progression) | Permanent | No |
| `frame_rare_azure_crest` | Azure Crest | RARE | Coins Shop | Lv. 1 | 🪙 600 | 30 Days | Yes |
| `frame_epic_violet_orbit` | Violet Orbit | EPIC | Achievement | Lv. 10 | 0 (Reward) | Permanent | No |
| `frame_epic_amethyst_ring` | Amethyst Spark | EPIC | Coins Shop | Lv. 1 | 🪙 1,800 | 30 Days | Yes |
| `frame_legendary_golden_sun` | Solar Monarch | LEGENDARY | VIP Benefit | Lv. 15 | 0 (VIP 7+) | Permanent | No |
| `frame_legendary_amber_crown` | Amber Nova | LEGENDARY | Coins Shop | Lv. 1 | 🪙 4,500 | 30 Days | Yes |
| `frame_mythic_radiant_crown` | Radiant Sovereign | MYTHIC | Family Rewards | Lv. 20 | 0 (Guild Top 3) | Permanent | No |
| `frame_mythic_rose_jewel` | Rose Empress | MYTHIC | Coins Shop | Lv. 1 | 🪙 9,000 | 30 Days | Yes |
| `frame_celestial_aurora_stream` | Celestial Aurora | CELESTIAL | Special Event | Lv. 25 | 0 (Seasonal Event) | Permanent | No |
| `frame_celestial_nebula_god` | Nebula Paragon | CELESTIAL | Coins Shop | Lv. 1 | 🪙 18,000 | 30 Days | Yes |

---

## 3. Entitlement & Lifecycle Rules

1. **State Machine:**
   `LOCKED` $\rightarrow$ `AVAILABLE` $\rightarrow$ `OWNED` $\rightarrow$ `EQUIPPED` $\rightarrow$ `EXPIRED`
   - `LOCKED`: User does not meet minimum level requirement or event eligibility.
   - `AVAILABLE`: User can purchase with coins or claim via level milestone.
   - `OWNED`: Entitlement record present in `frame_entitlements`.
   - `EQUIPPED`: ID matches `UserProfile.frame` and `frame_equipment.equipped_frame_id`.
   - `EXPIRED`: Time-limited shop frame whose `expires_at < System.currentTimeMillis()`. Auto-unequipped on next session.

2. **Server Verification Requirements:**
   - Client is strictly a presentation and preview surface.
   - Coin purchases require server-side transaction locks (`BEGIN ... COMMIT`) to prevent race conditions or balance exploitation.
   - Level milestones verify true lifetime XP from database records, not client memory.
   - Revocation (e.g., chargebacks or moderation bans) immediately clears entitlements and notifies active clients.

3. **Performance & Accessibility Optimization:**
   - **Reduced Motion:** If accessibility options or device settings request reduced motion, all animations fall back gracefully to clean static vector renderings.
   - **Lazy List Optimization:** Avatars rendered inside `LazyColumn` rows below 52dp disable infinite transitions, preventing GPU overdraw and preserving battery.
   - **High-Impact Surfaces:** Podiums (68dp–88dp), user profile headers (80dp–84dp), and wardrobe preview sheets (88dp–120dp) display the full animated Canvas particle suite.
