# ANTIGRAVITY MASTER PROMPT — AURORA PARADISE

You are a senior Android architect, Kotlin/Jetpack Compose engineer, game UI artist, and QA engineer. Implement Aurora Paradise as a unified, original design system in the EXISTING Android project. App name is not finalized. Do not use Vybora or rename package/application IDs.

## First audit
Inspect all modules, existing Compose themes, navigation graphs, screen components, asset directories, view models, backend integrations, animations, Gradle configuration, and tests. Report exact file paths and dependencies. Establish a baseline build before editing. Preserve all existing functionality, screen routes, authentication, LiveKit voice integration, family system, leaderboard, gifts, wallet, and shop. Do not replace real data with hardcoded samples.

## Theme foundation
Use tokens/colors.json as initial palette, not direct hardcoded colors throughout screens. Create semantic tokens: background, surface, elevatedSurface, primaryAction, secondaryAction, textPrimary, textSecondary, border, success, warning, error, vipGold, darkRoomBackground, glowCyan. Support light app surfaces and deliberately dark immersive voice/VIP sections. Ensure accessible contrast and dark system bar icons only on light surfaces. Typography: strong readable headings, medium-weight labels, consistent baseline; use existing font licenses. Spacing 4/8/12/16/20/24/32dp, corners 12/16/20/24dp, minimum tap target 48dp.

## Five main tabs
Games: bright ivory base, original vivid coral/lavender game banners, smooth corners, correct game room navigation, working search and tasks, gift offer popup, preserved existing gameplay.
Voice Room discovery: light background, rich ranking hero, functional categories/search/room creation, real room counts.
Voice Room interior: immersive midnight violet with restrained aurora effects; host + 8 seats, optional partner seat, paired seat bonds, LiveKit microphone/permissions, chat, gifts, room controls, reconnect/error states; never pretend audio is connected.
Chats: bright, accessible chat list and threads, real unread states, input, attachments and error handling.
Discover: original illustrated shortcuts for Moments, Families, Nearby, Clubs/Spaces, Events; posts with images, real location permission and privacy.
ME: peach-lavender header, real profile data, four illustrated shortcuts, compact social metrics, clean grouped rows; preserve every existing submenu and route.

## Connected features
VIP and Shop: warm gold and orchid premium accents, real membership entitlements, product previews, inventory, purchases validated server-side, Google Play Billing where required.
Family: elegant lavender/cream, role permissions, member roster, family levels, gifts, tasks, rankings, contributions, all existing flows intact.
Leaderboards: category-specific backgrounds, top-three podiums, accessible ranked lists, shared animated profile frames and real scores.
Avatar Studio/Backpack: owned frames and cosmetics, preview/equip/unequip, consistent display across screens.
Wallet and gifts: server-authoritative balances, no client-side minting, idempotent transactions and safe error states.

## Motion and assets
Create original cohesive glossy illustrative icons (not generic repeated symbols), original game banners, room decorations, VIP crowns and badges; never copy WePlay proprietary art. Prefer vector assets for simple shapes, optimized WebP for rich illustrations, and Compose Canvas/graphicsLayer for avatar effects. Animations: 120–220ms press feedback, 220–350ms navigation, restrained looping effects with frame rate/battery optimization; respect animator duration scale and reduced motion. Avoid unnecessary perpetual recomposition.

## Navigation and regression
Preserve bottom tabs and all nested routes. Back press must work from every inner screen; restore scroll state and handle deep links. No floating overlays blocking navigation. Handle loading/empty/error/offline states and responsive layouts with font scaling.

## Implementation sequence
1. Inventory current files and capture before screenshots; baseline compile.
2. Build semantic theme tokens and reusable Compose components (buttons, icon tiles, cards, section rows, badges, avatar frames).
3. Apply to Games and Voice Room discovery; screenshot and test.
4. Apply to Chats, Discover and ME; screenshot and test.
5. Apply immersive Voice Room interior, VIP, Shop, Family, Leaderboards and subpages; verify functionality.
6. Import original optimized art, refine animations and accessibility.
7. Run `./gradlew :app:assembleDebug` (Windows: `gradlew.bat :app:assembleDebug`), unit tests, and connected UI tests if a device/emulator is available.
8. Report exactly which tests passed, which could not run, file changes, screenshots, regressions, unresolved backend dependencies. Do not claim production-ready unless verified.

## Safety
Do not delete functionality, do not automatically commit/push, do not change signing or billing identifiers, do not expose secrets. Work incrementally; when an operation is destructive, request confirmation. The output must be implemented Compose code, not only mockups.
