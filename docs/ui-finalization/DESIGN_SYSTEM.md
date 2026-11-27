# Paradise — Design System (`Aurora Paradise 3.0`)

- **App Display Name:** `Paradise - Voice Room & Games`
- **Short Launcher Label:** `Paradise`
- **Application ID:** `com.paradise.play`
- **Kotlin Namespace:** `com.sundram.weplay`

## 1. Visual Identity & Core Palette
All screens share the dark celestial **Aurora Paradise 3.0** token family defined in `ui/theme/AuroraParadise.kt` and unified in `ui/theme/Theme.kt`:

| Role | Token | Hex | Usage |
| :--- | :--- | :--- | :--- |
| Primary Canvas | `CelestialNavy` / `AuroraBackground` | `#11134A` | Window background, root scaffold canvas |
| Soft Surface | `AuroraDarkPalette.bgSoft` | `#1A1242` | Nested sections, search fields |
| Card Surface | `DeepInk` / `AuroraSurface` | `#211448` | Cards, sheet panels, list groups |
| Elevated Surface | `AuroraElevated` | `#2C195E` | Modals, chips, pressed containers |
| Royal Accent | `RoyalViolet` / `MysticPurple` | `#5423B7` / `#7435D9` | Featured headers, active borders |
| Secondary Accent | `CrystalBlue` | `#54CFFF` | Interactive accents, online dots, mic waves |
| Warm Reward | `RoyalGold` / `SoftGold` | `#FFD36B` / `#FFF0B6` | Coin balances, VIP highlights, top podium |
| Rose Accent | `SunsetRose` | `#FF78C4` | Live indicators, romantic/highlight badges |
| Primary Text | `CloudWhite` | `#F8F1FF` | Headings, body copy on dark canvas |
| Body Text | `AuroraDarkPalette.textBody` | `#DCD0F5` | Descriptions, subtitles |
| Muted Text | `DreamLavender` (85% / 80%) | `#B89BFF` | Timestamps, secondary metadata |
| Borders & Dividers | `AuroraBorder` / `AuroraDivider` | `DreamLavender` 28% / 18% | Card strokes, hairline dividers |

## 2. Preserved Visual Anchors (Do Not Alter)
- **Games Home Background:** `AuroraGamesBackground` (`ap3r_bg_games_palace` + `ap3r_overlay_sparkle_particles`)
- **Me Tab Royal Night Theme:** `MeScreen.kt` header + `ap_me_*` iconography and layout
- **Voice Room Stage:** 68dp-72dp seat tiles, crest progression tiers, and 10 replaceable starter themes

## 3. Window & Launcher Chrome
- `values/themes.xml` and `values-night/themes.xml` both lock into dark `Theme.Material.NoActionBar` with `splash_brand_window`, dark bars (`#10052D`), and light system icons.
- `values-v27/themes.xml` and `values-night-v27/themes.xml` apply `android:windowLightNavigationBar="false"`.
- Adaptive icon set (`mipmap-anydpi-v26/ic_launcher*.xml`, `drawable/ic_launcher*.xml`) renders the Paradise gold crescent, aurora prism star, and harmonic voice wave emblem.
