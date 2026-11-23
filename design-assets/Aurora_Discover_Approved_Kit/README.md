# Aurora Paradise 3.0 — Approved Discover Kit

Original Android art and source-aware implementation for the approved WePlay-inspired Discover layout. This is the **Discover screen**, not Games or Voice interior. Brand name undecided.

## Contents

- `android/app/src/main/res/drawable-nodpi/`: **23 independent Android-ready WebP resources** (2 backdrops, 20 icon/frame/effect resources, 1 media fallback).
- `source-svg/`: **20 scalable, editable SVG vector source files** (not directly compatible with Android VectorDrawable imports).
- `previews/APPROVED_DISCOVER_LAYOUT.jpg`: supplied/approved visual benchmark (reference ONLY).
- `previews/ACTUAL_ASSET_CATALOG.jpg`: accurate visual proof of exported assets.
- `docs/ASSET_MANIFEST.json`: exact names, dimensions, transparency and SHA256.
- `docs/SOURCE_MAPPING.md`: actual Kotlin class and source references from the uploaded `tempplay-main.zip` snapshot.
- `docs/COLOR_TOKENS.json`, `docs/QA_CHECKLIST.md`, `ANTIGRAVITY_DISCOVER_MASTER_PROMPT.md`.

## Integrating with Antigravity or Claude Code

1. Unzip into `design-assets/Aurora_Discover_Approved_Kit/` in the EXISTING `tempplay` project (not inside `app/res`).
2. Attach the approved layout reference and give the agent `ANTIGRAVITY_DISCOVER_MASTER_PROMPT.md`.
3. Agent must inspect the current project and import only `ap_discover_*.webp` into `app/src/main/res/drawable-nodpi/`.
4. Build the visual layout with Kotlin/Compose. **Never render the screenshot or asset catalog as a functioning UI.**
5. Test routing, state, permissions and all five bottom tabs.

**Important:** The source snapshot's Discover feed is demo/local and has no media URL in its model; the preview's people/post images are not real app media. Use real user post media for the production feed and leave honest empty states. The ZIP does not implement backend, location or image uploading on its own.
