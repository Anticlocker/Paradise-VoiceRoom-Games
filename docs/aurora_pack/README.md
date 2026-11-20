# VYBORA — Aurora Lounge Joined Voice Room Asset Pack

Original visual resources for **the joined voice room**, not the Voice Lounge/discovery listing screen.

## Contents

- `android/app/src/main/res/drawable-nodpi/`: individual WebP drawable files, all valid lowercase underscore Android resource names; `vb_vr_` prefix avoids collisions.
- `docs/asset_manifest.json`: authoritative filenames, pixel sizes and intended use.
- `previews/aurora_lounge_mockup.jpg`: optional concept preview showing demo state; do not display it as your actual room UI.
- `previews/asset_catalog.jpg`: easy visual selection.
- `kotlin/AuroraRoomAssetExample.kt.txt`: short Compose resource-layer example.
- `ANTIGRAVITY_ASSET_INTEGRATION_PROMPT.md`: copy/paste prompt for your agent.

## Installation

1. Extract the ZIP anywhere Antigravity can read.
2. Locate the **existing** Android project's application module (`settings.gradle`/`.kts` tells you its name).
3. Copy only `vb_vr_*.webp` into `your-module/src/main/res/drawable-nodpi/`.
4. In the joined-room Composables, reference `R.drawable.vb_vr_bg_aurora`, `R.drawable.vb_vr_frame_host`, and the other **actual** resource names from `docs/asset_manifest.json`.
5. Put real user avatars inside frame resources. For rings use overlays; rings and seat states have transparent centers/backgrounds.
6. Build with the project's Gradle wrapper; test on an Android phone.

## Categories

- Full-screen ambient room backgrounds (1080×1920)
- Transparent host, VIP, moderator, speaker, and normal avatar-ring frames (512×512)
- Transparent microphone-seat states (512×512)
- Six clearly fictional **demo-only** avatar illustrations (512×512)
- High-quality neon emblem illustrations from original generated artwork (512×512)
- White and selected-cyan control icons (160×160)
- Role chips, particle overlay and PK/reward bars
- Extra activity section banner backgrounds

## Usage caveats

- **No actual LiveKit, backend, audio, chat, wallet or animations** are bundled; these are images and guidance, not logic.
- Pulse speaking frames with Compose animation tied to **real** local audio telemetry when connected; demo movement must be labeled preview.
- Real avatars, badges, ranks, seat assignments and counts must come from your data model/backend; never assign fictional demo avatars to real people.
- Use separate clickable Compose controls—do not render a whole room screenshot in one Image.
- All assets are newly created for this project and are not exports of WePlay artwork. Check trademarks before production distribution.
- Emblem cutouts originally came from a generated sheet with dark background; feathered transparency preserves glows. Confirm their appearance on your real device and regenerate if needed.
- Decode large backgrounds at sensible dimensions; `drawable-nodpi` ensures Android does not auto-density-scale artwork. `WebP` transparency is preserved.
- Make controls at least 48dp touch targets; support system insets and Compose IME padding.

## Recommended design

Use the **existing first two pages' white/lavender/cyan design language**, with a more atmospheric plum/violet joined-room backdrop. Keep room status, mic seats, messages and bottom actions as native Compose components.
