# MASTER PROMPT — LEADERBOARDS + ANIMATED PROFILE FRAMES

You are the principal Kotlin/Jetpack Compose Android engineer, motion designer, backend architect, economy designer and QA lead for our UNNAMED social gaming application. The application name is not finalized. NEVER introduce VYBORA or another brand, rename package IDs, or create a second Android project. The existing app is developed in Antigravity and tested in Android Studio.

## Sources and evidence
- Watch the provided `111398.mp4` in full if supported; otherwise inspect extracted frames. Read `docs/VIDEO_ANALYSIS.md` and `docs/FRAME_ANIMATION_SPEC.md`. The video demonstrates WePlay ranking categories and ornate decorative frames; it does NOT reveal authoritative unlock rules, price points, formulas, or frame animation source files. Do not invent these as facts.
- Treat reference as inspiration, not pixel-perfect replication. Build original frames and UI consistent with existing first two pages and our white/lavender/cyan/gold design language.
- Audit existing ranking, wallet, VIP, gift, couple, BFF, family, room, user profile, shop, inventory, authentication, backend and navigation code before changing it. Preserve all existing functionality.

## UX architecture
A. Leaderboard screen: app bar with title/back/help; horizontally scrollable category tabs: Popularity, VIP, Couple, Room, BFF, Family; category-specific period filters (Today, Yesterday, Weekly, Total, Celebrity, Annual, Global only when supported); subtle themed backgrounds, no giant empty areas. Use server-returned category/filter capabilities, not hardcoded incompatible combinations.
B. Top-three podium: winner center prominent, 2nd/3rd flanking; ornate original SVG podium/crest/frame art; avatar, name, actual rank, verified badge if verified, score, score unit, status, tap to profile. Handle ties and missing positions gracefully. Stable shimmer while loading, not fake results.
C. Rank list: efficient LazyColumn, rank number, actual user/couple/family/room avatar, equipped frame, display name, level/role/verified labels as relevant, actual score, tap destination; pagination, refresh, empty/error/offline states, stable keys.
D. Sticky current-user rank summary where eligible, with actual percentile/score if provided. Unranked state if not eligible; never show fabricated '999+' rank.
E. Category-specific CTAs: VIP -> VIP Center; Couple -> partner/bond screen; Room -> voice room or advanced room creation; BFF -> friendship/relationship details; Family -> Family Hub; Popularity -> user profile. Only expose actual routes and verified eligibility. Help sheet explains score basis and reset period from configured backend.
F. Ranking privacy: blocked users, opted-out users, moderated profiles, minors and deleted accounts must follow existing privacy/safety policy. Avoid showing precise location, phone number, sensitive profile data.

## Frame inventory and cosmetics system
- Use `assets/svg/lb_<tier>_ring.svg`, podium, crest and shop preview templates. These are ORIGINAL STATIC SVG artwork. Convert to Android VectorDrawable where possible or use supported SVG rendering; avoid blindly placing SVG files in drawable-nodpi if Android build cannot decode them. Preserve source artwork under assets.
- Build native Compose Canvas frame rendering, with optional static SVG overlays and original particles. Follow animation timing in `docs/FRAME_ANIMATION_SPEC.md`. For visual polish use layered radial gradients, soft glow, subtle orbit, twinkle, crown float and particle streaks; never obscure faces or clip effects.
- Frame tiers: common, rare, epic, legendary, mythic, celestial. These are OUR proposed names, not WePlay verified levels. Define `FrameDefinition(id, tier, assetId, animationPreset, eligibilityRule, price?, validUntil?, supportedContexts)` and `UserFrameEntitlement(userId, frameId, source, grantedAt, expiresAt, equippedAt)`.
- Entitlement sources: account level, achievement, VIP, event, shop, family rewards. Define all thresholds and catalog pricing in versioned server-side configuration. Provide clear locked/owned/equipped/expired labels and eligibility progress. Preview locked frames without granting ownership.
- Shop: category tabs, frame cards, full-screen preview on real avatar, purchase/equip/unequip actions, expiration disclosure, refund handling, inventory restoration. Purchases of digital goods use applicable Google Play Billing requirements and server verification. Never grant paid entitlements based only on a client callback.
- Display the equipped frame consistently in profile, chat, leaderboard, voice seats, friends list and family roster via shared `AvatarWithFrame` composable. Provide a static fallback for low-power/reduced-motion modes.
- Do not run many infinite animations in long lists: animate top 3 and visible/high-priority avatars only; pause on lifecycle stop and when offscreen. Avoid unnecessary overdraw, battery drain and recomposition.

## Backend and ranking integrity
- Inspect current architecture. If existing stack includes Node.js, Supabase/Postgres, Redis and LiveKit, reuse it; don't duplicate auth or introduce unrelated services. Compose UI -> ViewModel/StateFlow -> repository -> authenticated APIs.
- Define category-scoped ranking score events (e.g. verified popularity points, verified VIP points, partner bond contribution, voice room activity, friendship interactions, family contributions). Final formulas must be approved/configurable; do NOT assume WePlay's formulas. Deduplicate event IDs; prevent self-farming, fake gifts, replay and fraudulent transactions. Ensure wallet and ranking updates are server authoritative.
- Snapshot leaderboard periods in target timezone/UTC with explicit start/end timestamps. Deterministic tie breakers; cache top pages in Redis with safe invalidation; batch and paginate; never trust client-supplied scores. Support previous-day/weekly/annual archives only if stored.
- Schema proposals: ranking_categories, ranking_periods, ranking_score_events, ranking_snapshots, frame_catalog, frame_entitlements, frame_equipment, shop_orders, verified_reward_grants, audit_logs. Adapt to existing tables and RLS; avoid destructive migrations.
- Admin moderation, fraud flags, eligibility and revocation must be auditable. Do not show fake leaderboards in production. Demo seed data only in clearly labeled debug builds.

## Animation and visuals
- The reference shows shiny ornate frames and podiums. Our app should be original: pearl-white cards, soft lavender backdrop, category-specific accent colors, vivid gold/cyan details, and premium animated frames. Ensure text contrast and Android edge-to-edge/insets.
- Motion: 250–450ms tab transitions; top podium 350ms entrance; rare ring 2.2s breathing; epic 4.2s particle orbit; legendary 5.6s counter-orbit; mythic 6.4s crown float; celestial 8s aurora sweep. Motion must be restrained and disabled when accessibility settings request reduced motion.
- Create Compose previews for every tier at 48, 72 and 120dp and verify no clipping, aliasing or distortion.

## Implementation order
0. Audit current app, existing routes/data models, rank data and shop entitlement infrastructure. Write `docs/LEADERBOARD_INITIAL_AUDIT.md`.
1. Import and convert SVG artwork safely; create reusable `AvatarWithFrame` and static fallbacks. Build/test.
2. Implement leaderboard tabs, period filters, podium, paginated rank list and current-user summary with repository state. Build/test.
3. Integrate real ranking APIs and privacy, backend score ledger, periods and caching. If backend access unavailable, show honest unconfigured/empty states rather than fake values.
4. Implement frame catalog, shop previews, entitlements, equip/unequip and shared display contexts. Verify purchases server-side.
5. Add performant Compose animations, accessibility and lifecycle handling.
6. Validate all categories and routes on real Android device; fix regressions. Never push GitHub automatically.

## Acceptance tests
- All six categories switch correctly and filters show valid data/empty state.
- Podium order, tie-breaks, ranking score labels, list pagination and current-user rank match server.
- Back/navigation works from ranking -> profile -> shop -> inventory -> leaderboard -> Games/Voice Room/Chats/Discover/Me.
- Each tier's frame is aligned at small/large sizes; no clipped glow, frozen avatar or dropped frames.
- Locked preview does not equip; level unlock occurs only after verified eligibility; shop purchase only after backend verification; expired/revoked frames unequip safely.
- Frame changes propagate to profile, voice room, chat and family roster.
- Offline/reconnect/empty/error/loading and rapid tab switching work; no duplicate purchases or event points.
- Test accessibility, TalkBack labels, font scaling, low-end Android performance and lifecycle pause/resume.
- Run actual Gradle assembleDebug and available tests/lint. Report commands and results honestly.

## Deliverables
Produce `docs/LEADERBOARD_FUNCTION_MATRIX.md` with every visible control, destination, data source, status and test evidence; `docs/FRAME_CATALOG_AND_UNLOCKS.md` with proposed level thresholds and prices clearly marked PROPOSED pending product approval; and `docs/LEADERBOARD_IMPLEMENTATION_REPORT.md` with changed files, asset import method, build/test outcomes and backend blockers. Do not claim everything is working unless verified.

START BY INSPECTING THE EXISTING CODE AND VIDEO. IMPLEMENT, COMPILE, TEST AND REPORT. DO NOT STOP AT MOCKUPS.
