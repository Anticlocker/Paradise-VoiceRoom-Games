# Aurora Paradise 3.0 — Updated Repo-Specific Art Kit

Based on user-uploaded `tempplay-main.zip`, not assumptions from a generic APK.

## Installation

1. Extract this package **outside** the Android module.
2. Keep your existing working app and git branch. This ZIP is an **asset-only overlay**, not a replacement repository.
3. Read `docs/REPO_SOURCE_AUDIT.md`, `docs/asset_manifest.json` and `ANTIGRAVITY_SOURCE_MAPPED_PROMPT.md`.
4. Copy **only** the new `ap3r_*.webp` into the actual `app/src/main/res/drawable-nodpi/` folder.
5. Have Antigravity wire the resources to the existing exact screens/components. Current app will look unchanged until Kotlin references are updated.
6. For dynamic event covers, use the `EventRepository.ALL_EVENTS` IDs listed in the mapping; keep event title, times, badges, scores and pricing as Compose text and trusted data.

## Design scope

- New original AI-created 1080×1920 fantasy palace background for Games
- Two **derivative variations** for Event/Profile (not independently illustrated backgrounds)
- Original composed dark Voice ambient background using existing `vb_voice_background.webp`
- 8 transparent-text-safe premium hero/event banners, made using the project's own illustrated character artwork
- A subtle optional sparkle overlay, gift glow, and transparent reward podium platform

**Not included:** a new 3D gift-chest model, isolated transparent gold coin, complete game collection sprites, or Lottie files. These need dedicated export. The package does not implement Kotlin logic or rebuild the app.

## Existing artwork worth reusing (already in repo; don't duplicate)

`vb_character_*.webp`, `vb_room_cover_*.webp`, `vb_vr_frame_*.webp`, `vb_vr_emblem_*.webp`, `vb_coin.webp`, `vb_gift_pack.webp`, and `vb_banner_*.webp`.

## Art + functionality cautions

- Preserve package `com.sundram.weplay`; app name not finalized, use no new branding.
- The uploaded repo has demo currency / event data and possible service credentials in BuildConfig; do not treat cosmetics as proof of production readiness.
- Keep all artwork text-free so Compose renders dynamic labels; backgrounds aren't full UI screenshots.
- Image source: AI-generated palace artwork plus composites using user-provided project assets; review publication rights of your project assets.
