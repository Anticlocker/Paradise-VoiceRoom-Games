# ANTIGRAVITY — VYBORA AURORA LOUNGE ASSET INTEGRATION TASK

You are implementing artwork into MY EXISTING native Kotlin + Jetpack Compose Android application.

**Focus only on the joined Voice Room screen.** Preserve the already improved Gaming/Home page and Voice Lounge discovery page. This asset pack complements my preceding JOINED VOICE ROOM FUNCTIONALITY MASTER PROMPT; apply both.

## SOURCE

I extracted `VYBORA_Aurora_Joined_Voice_Room_Asset_Pack` into a location you can read.

Inspect:
- `README.md`
- `docs/asset_manifest.json`
- `kotlin/AuroraRoomAssetExample.kt.txt`
- `previews/aurora_lounge_mockup.jpg`
- Real Android project, theme, navigation, ViewModel, repositories, and LiveKit state.

## INTEGRATE (not merely plan)

1. Find the actual Android application module; keep the current package/app ID and Gradle versions.
2. Import `android/app/src/main/res/drawable-nodpi/vb_vr_*.webp` into the existing module's `src/main/res/drawable-nodpi` directory. Do not add a nested Android project.
3. Use the actual manifest filename list; do not reference imaginary assets.
4. Use `vb_vr_bg_aurora` as the default ambient backdrop; optional `vb_vr_bg_celestial` as alternative.
5. Render real participant profile pictures with transparent `vb_vr_frame_host`, `_speaker`, `_moderator`, `_vip` frame overlays. Treat `vb_vr_avatar_demo_*` as fictional placeholders for LOCAL PREVIEW only; never substitute them for identified real people.
6. Bind seat visual resources `_empty`, `_locked`, `_request`, `_speaking`, `_muted`, `_occupied`, `_invited` to the REAL seat-state model; avoid letter-only circles in the final design.
7. Use `vb_vr_ic_*` control icons for mic, mute, volume, gift, chat, participants, share, leave, more, info, camera, reactions, PK (`swords`), games, dice, settings, report, lock and related actions.
8. Use premium `vb_vr_emblem_*` images only as visual decoration when relevant to **genuine** role, gift, rank or feature state; do not fabricate earned badges or purchases.
9. Keep messages as accessible, readable Compose text; do not bake live names, message text, IDs, balances or scores into images.
10. Build responsive header, visually balanced speaker stage, chat area and bottom action dock. Keep the first two pages unchanged except for essential shared-component regression fixes.
11. Make each visible control properly interactive via ViewModel/real repositories. In offline mode label preview explicitly. A LiveKit URL alone is not a connection; connecting requires a backend-issued token, appropriate room grants, microphone permissions and actual SDK connection state.
12. Use `vb_vr_fx_sparkles` as OPTIONAL overlay with gentle Compose animation; respect reduced motion and lifecycle. Do not claim animated assets are provided—the resources are static WebP files.
13. Use a consistent design with White + Lavender + Cyan from the first two pages but a richer midnight-violet ROOM interior. Do not imitate the WePlay screenshot's exact layout or graphics.
14. Compose screen must remain interactive; NEVER use `previews/aurora_lounge_mockup.jpg` as a full-screen screenshot replacement.
15. Verify resource names, Gradle compile, navigation and real-device rendering. Run the project's actual `assembleDebug` Gradle task and available tests; do not claim passes you have not achieved.
16. Create `docs/JOINED_ROOM_ASSET_INTEGRATION_REPORT.md` listing imported/used assets, Kotlin files, build status, verified controls, demo-only features, missing backend integrations and a manual Android Studio checklist.

## SAFETY

Do not delete working files, force-push Git, change package/application ID, change signing, modify production credentials, fabricate wallet transactions or copy WePlay proprietary artwork. If external services are missing, keep clearly marked preview functionality and record the blocker.

**DO THE IMPLEMENTATION** inside my current project now, progressively, then report actual results.
