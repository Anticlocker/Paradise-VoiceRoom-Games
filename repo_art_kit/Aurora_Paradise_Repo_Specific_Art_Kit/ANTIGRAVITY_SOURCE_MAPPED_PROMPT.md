# ANTIGRAVITY — REPO-AWARE AURORA PARADISE ART COMPLETION

You have an EXISTING Kotlin + Jetpack Compose Android project from `Anticlocker/tempplay`. Work in the existing `app` module, `com.sundram.weplay` package. The application name is NOT finalized. Do not introduce VYBORA branding.

The user approved a rich royal violet/gold celestial reference showing Games, Event Center, and Gift Pack. That reference is the ART DIRECTION BENCHMARK, not a screenshot to display.

## MUST READ FIRST

1. `README.md` from the extracted art package
2. `docs/REPO_SOURCE_AUDIT.md`
3. `docs/EXACT_RESOURCE_MAPPING.md`
4. `docs/asset_manifest.json`
5. Actual `ui/theme/AuroraParadise.kt`, `ui/screens/GameLobbyScreen.kt`, `data/EventModels.kt`, `ui/screens/EventCenterScreen.kt`, `ui/components/GiftPackOffersSheet.kt`, `ui/screens/LiveVoiceRoomScreen.kt` and `ui/nav/WePlayBottomBar.kt`

## CRITICAL: PRESERVE WORKING LOGIC

- No package rename, new project, navigation replacement, wallet mutations, fake LiveKit, or force push.
- Work on a safe branch with a checkpoint.
- Art in the package is ORIGINAL AI palace art plus compositions using existing project art. The banners deliberately have no text; all user text and dynamic facts belong in Compose.
- Use resources actually present. Do not invent filenames. Import only `ap3r_*.webp` into the existing module's `app/src/main/res/drawable-nodpi/`.
- Do not replace `AuroraParadise.kt` wholesale; selectively update image references and components.

## VISUAL REQUIREMENTS

1. Games: luminous palace atmosphere in first third, legible content on purple/lavender surfaces, premium original existing game characters, gold/cyan readable currency pills, compact room card, clean community carousel, functional Event star and gift pack.
2. Event Center: rich illustrated banners from `ap3r_event_*`, two-column feature cards, readable labels over dark gradients, actual category tabs and verified/demonstration event states.
3. Gift Pack: keep existing offer rotation/contents and current ownership logic. Layer subtle glow/pedestal behind actual gift artwork, with Compose celebration motion. The kit does not contain a new 3D chest; don't claim it does.
4. Voice: implement `ap3r_bg_voice_midnight` as a calm backdrop. Keep microphone, eight guest seats, host/partner and PK functionality genuinely interactive. Stop animations when not on screen.
5. Profile/Discover: use palace variant sparingly in headers, preserve readable text-heavy surfaces.
6. All screens: preserve typography contrast, 48dp tap targets, bottom nav, status bar, font scaling, IME padding and reduced motion.

## SOURCE-BASED BUG WORK

- Remove or gate hardcoded `crystals = 850L` (actual balance or not shown).
- Mark sample event counts, dates, rewards and task progress as demo until sourced from backend.
- Audit `Routes.VIP = TOPUP` and `Routes.SHOP = GIFTS`; avoid claiming dedicated VIP/Shop are complete.
- Evaluate `LIVEKIT_TOKEN_KEY` injected in APK BuildConfig. Secrets must stay server-side.
- Update stale README only with verified current implementation, not speculative production claims.

## IMPLEMENT IN CONTROLLED MILESTONES

A. Audit and screenshot current Games/Event/Gift, record baseline build.
B. Import new images and theme mapping, build and show actual Android screenshots.
C. Improve Games composition and replace only weak cropped artwork; preserve already-good `vb_character_*` assets.
D. Improve Event Center cards, edge treatments, state overlays and detail navigation.
E. Upgrade Gift Pack layering/animation without changing offer semantics.
F. Voice background and animation integration; test mode-specific state/PK screens.
G. Extend approved patterns to Chats/Discover/Me/Family/Rankings/VIP/Shop. Keep settings calm/readable.
H. Run Gradle `assembleDebug`, available tests and lint; device tests for LiveKit and navigation if possible.

At each milestone generate `docs/AURORA_REPO_UI_IMPLEMENTATION_REPORT.md` with modified files, exact assets, before/after screenshots, compile/test outputs, real/demo/blocked status, and remaining production-art needs. Add no fake assets or claimed passes. No automatic push.

BEGIN WORK BY AUDITING THE REAL CODE AND THE ASSET MANIFEST. MAKE A SOURCE-MAPPED IMPLEMENTATION, NOT A GENERIC THEME REWRITE.
