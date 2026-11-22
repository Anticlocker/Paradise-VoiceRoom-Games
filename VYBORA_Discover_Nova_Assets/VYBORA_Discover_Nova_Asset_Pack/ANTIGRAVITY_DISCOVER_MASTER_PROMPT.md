# VYBORA DISCOVER 2.0 — ANTIGRAVITY IMPLEMENTATION MASTER PROMPT

You are a senior native Android architect, Kotlin/Jetpack Compose engineer, UX designer, real-time social product designer and QA lead. Implement a premium ORIGINAL Discover screen in my EXISTING VYBORA Android app. The first supplied screenshot is WePlay inspiration (white page with Moments, Church, Family, Nearby, floating gift). The second screenshot is our CURRENT dark Discover page (Moments, Games, Rooms, Nearby, tags, live updates, community feed). Build a better original layout consistent with VYBORA's established WHITE + LAVENDER + CYAN + soft gold theme. Do not clone WePlay or discard any current working feature.

## NON-NEGOTIABLE
- Audit the actual Android project, navigation graph, Discover composables, repositories, backend, auth, location, social feed and gift offer integrations BEFORE changing anything.
- Preserve existing Games, Voice Lounge, Chats, Me, Joined Voice Room, navigation, authentication, and current Discover functionality.
- Preserve existing Moments, Games, Rooms, Nearby, trending tags, Live Updates, and community feed if functional. Do NOT remove them merely because WePlay reference looks sparse.
- Reorganize features into a smarter Discover experience. Do not copy WePlay's exact 3-tile layout, spacing, illustrations or names.
- No static screenshot UI. All controls must have real handlers. No fake social activity, false online indicators, fabricated GPS results, or invented gift purchases.
- No unnecessary Gradle, package ID or architectural changes. Never push to GitHub automatically.

## ASSET IMPORT
I supplied extracted folder `VYBORA_Discover_Nova_Asset_Pack`.
Read `README.md` and `docs/asset_manifest.json`. Copy resources from `android/app/src/main/res/drawable-nodpi/` into the EXISTING Android application module's `src/main/res/drawable-nodpi/`. Do not create a new module. All files have prefix `vb_discover_`. Use assets where appropriate and keep vector/Compose fallbacks. Original assets include 12 feature tiles, 8 fictional demo avatars, 3 decorative backgrounds and 6 badges. They are static WebP images, NOT animated Lottie files. Use Compose for subtle motion. Never replace real profile images with demo avatars.

## DISCOVER INFORMATION ARCHITECTURE
Build a distinctive scrollable layout:

A. HEADER / PERSONALIZED WELCOME
- Safe-area aware top spacing, elegant Discover title, compact optional search/filter and a contextual subtitle ("Find your people, stories and spaces").
- A compact soft gradient decorative backdrop, not an enormous empty header. Honor system status bar and font scaling.

B. FEATURED MOMENTS HERO
- Prominent but compact horizontal feature card, original multicolor Moments artwork, brief description, dynamic preview of latest permitted content if available, chevron, tap opens existing Moments route.
- No fabricated preview activity. Proper loading/empty/error states.
- Optional small "Create Moment" action only if actual creation flow exists.

C. COMMUNITY SHORTCUTS
- Original responsive layout of 3-4 feature tiles: Families, Nearby, Communities/Clubs, Events. Show only supported destinations; unsupported destinations must show an honest preview or disabled state, not dead click targets.
- Families opens existing family discovery/membership flows. If earlier app used "Church" as a distinct group type, preserve it as a discoverable category within Communities, with culturally neutral labels and without deleting its underlying functionality.
- Nearby requires location permission request only on explicit tap, clear explanation, approximate-location support when sufficient, denied/permanently denied state, privacy controls, and real results from a backend. Do not show fake nearby users or precise coordinates. If backend is missing, show a transparent unavailable state and a useful non-location alternative.
- Events opens real event discovery if available; otherwise honest preview.

D. TRENDING DISCOVERY
- Preserve existing hashtags/categories (#Ludo, #Who's the Spy, #KTV, #Werewolf, etc.) as horizontally scrollable chips, but render them in VYBORA styling. Tap applies a real filter or opens the correct search results; empty and loading states are explicit.

E. SOCIAL ACTIVITY / LIVE UPDATES
- Preserve real activity feed and filtering; do not use fake "turned 5 coins into 500" gambling-like stories. Respect privacy, consent and block relationships. Only show server-provided, permitted events.
- Original compact feed cards with actual avatar, action, timestamp, contextual destination. Tap routes to real content. Empty feed has attractive CTA to explore communities.

F. COMMUNITY MOMENTS
- Preserve the existing moments feed beneath the hero: author, avatar, content/media, timestamp, reactions, comments, share, report and pagination as supported by backend. Do not invent counts.
- Use LazyColumn, stable keys, paged loading, content placeholders, retry, accessibility and scroll restoration.

G. FLOATING GIFT PACK
- Keep the existing floating Gift Pack if present but prevent overlap with content and bottom nav. Use the already-planned 48-hour featured offer system. Tap opens Gift Pack bottom sheet; never redirects directly to Top Up. Timer comes from persisted/server-authoritative offer expiration, never resets on recomposition. Show real product pricing only from configured billing; demo packs clearly labeled.

## VISUAL SYSTEM
Use existing VYBORA design tokens first; suggested accents: surface #FFFFFF, backdrop #F9F8FF, lavender #EDE5FF, cyan #39D5DC, violet #7957D8, gold #F5B95D, ink #25213C. White cards with subtle strokes/shadows, rounded 18-24dp corners, refined typography, elegant original icons, good contrast and ~48dp touch targets. Use balanced density: richer than WePlay's empty screen, cleaner than our existing overly long dark page. No excessive gradients, emoji-only icons, jagged edges, stretched avatars, duplicate section titles or massive top padding. Respect keyboard, gesture nav, window insets, smaller phones and accessibility settings.

## NAVIGATION / STATE
- Preserve bottom tabs Games, Voice Room, Chats, Discover, Me. Correct selected state, route restoration and no duplicate NavController.
- Ensure every feature destination returns to Discover correctly; Back dismisses sheet/dialog before navigating.
- Inspect full-screen scrims and pointerInput/zIndex issues that previously blocked Games tab.
- Persist scroll/filter state on tab switches.
- ViewModel + StateFlow + existing repository patterns; no invented server data. Separate UI, domain and network layers.
- Each button must have loading, success, error or unavailable behavior. No empty onClick lambdas.

## ANIMATION
- Use subtle Compose press response, staggered card reveal where appropriate, shimmer only while loading, gentle Moments icon motion, selected chip transitions and restrained floating Gift Pack attention effect.
- Respect reduced motion and lifecycle; avoid infinite offscreen animations, bitmap decoding on main thread and whole-page recomposition every timer tick.

## TESTING & ACCEPTANCE
1. Discover loads with correct VYBORA light theme, no empty giant top gap, and no clipped UI.
2. Moments opens existing feature and returns correctly.
3. Family, Nearby, Communities, Events have meaningful working/explicitly unavailable actions.
4. Nearby permission granted, denied, permanently denied, and location-off states tested; no fabricated users.
5. Trending chips filter/navigate correctly.
6. Live updates and moments preserve real data, pagination, and privacy controls.
7. Floating Gift Pack opens modal, closes correctly, respects 48-hour cycle and doesn't block bottom nav.
8. Games -> Discover -> Moments -> Back -> Discover -> Chats -> Back -> Discover -> Games works.
9. Rotations/recreation, font scaling, small phones, offline mode, empty states, slow network and repeated fast taps work.
10. No regressions in first three tabs or Joined Voice Room.

Run actual `gradlew :app:assembleDebug` (adapt module/OS), applicable unit tests, Compose UI tests and lint if available. Fix compilation/resource failures. Do not claim unrun tests passed.

## IMPLEMENTATION PHASES
0. Audit and document existing Discover screen/routes/data/assets.
1. Import and validate resources; implement light-theme layout and cards.
2. Wire preserved Moments, Games, Rooms, tags, live updates, community feed and existing destinations.
3. Add Families, Nearby, Communities and Events entry points with permissions and truthful unavailable states.
4. Integrate existing Gift Pack sheet and 48-hour offer logic, without duplicating store code.
5. Repair navigation, polish animations and responsiveness.
6. Build, test, fix regressions and report.

Create `docs/DISCOVER_2_IMPLEMENTATION_REPORT.md` with actual modified files, imported assets, completed features, demo-only/blocked features, build/test results and manual device checks. Create `docs/DISCOVER_2_INTERACTION_MATRIX.md` listing every clickable control, expected outcome, verified result and status PASS/FAIL/DEMO_ONLY/BLOCKED/NOT_TESTED.

BEGIN BY INSPECTING THE EXISTING CODE. THEN IMPLEMENT, BUILD AND VERIFY. DO NOT STOP AFTER PLANNING.
