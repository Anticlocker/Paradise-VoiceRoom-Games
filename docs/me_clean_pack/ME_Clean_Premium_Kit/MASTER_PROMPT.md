# MASTER PROMPT — ME PAGE 3.0 / CLEAN PREMIUM EDITION

## YOUR ROLE
You are the principal Kotlin/Jetpack Compose Android engineer, senior product designer, QA lead, and security-minded social-app architect. Implement the actual code inside the existing Android Studio project. This is an existing social gaming/voice room app whose final name has NOT been decided. Do not use VYBORA or introduce any new permanent name, app ID, logo or package rename.

## REFERENCE AND INTENT
The user supplied a WePlay ME-page screenshot with a warm cream/peach gradient profile header, square illustrated avatar, name/level progress, four colorful shortcut icons (VIP Center, Shop, PLAY Show, My Home), and clean white rows (Moments, Stats, Visitors, Invite Friends, Badge, Contributions, Language, Parental control mode, Help Center, Security Center, Settings). The previous custom dark profile page had friends/followers, wallet, badge/gift showcases, Family Hub, Backpack, Top Up, Edit Profile, Logout, About. The user rejected the previous dark/dashboard redesign. Build a much CLEANER, visually superior original version close to the reference's information density and simplicity, but not a pixel copy. Use the established light white + lavender + cyan + peach/gold theme of the existing Games and Voice Room screens. Preserve ALL existing functionality from the older ME page.

## MANDATORY FIRST STEPS
1. Read actual project source, current ME composables, theme tokens, NavHost, ViewModels, repositories, authentication and backend, wallet/billing, user profiles, social graph, family and inventory. Read this prompt and `docs/asset_manifest.json`.
2. Make an audit table: current button -> route -> backend status -> existing functionality. Preserve working behavior. Identify dead controls, demo values, fake counters, navigation bugs.
3. Record existing app theme typography, color and spacing and reuse them instead of creating an unrelated design system. Create a git checkpoint locally; do not push to remote without permission.
4. Copy supplied `android/app/src/main/res/drawable-nodpi/me_clean_*.webp` into the EXISTING app module's drawable-nodpi. Never create a second app. Assets are 50 original static WebP illustrations: 41 icons, 6 fictional demo portraits, 3 soft backgrounds. The 6 demo portraits are placeholders ONLY for mock/demo mode or new-user defaults where explicitly permitted; never overwrite real avatars. Do not present them as 3D assets or Lottie animations. Use Compose motion.

## EXACT MAIN-PAGE COMPOSITION
A. HEADER: edge-to-edge but system-inset-safe soft peach-to-lavender gradient, compact height approximately 180-220dp excluding status bar. On left: 80-92dp rounded-square actual avatar (fallback original illustrated avatar only if user has none). On right: bold username, optional genuinely verified icon, ID copy control, level label, narrow progress bar and `currentXP/nextLevelXP`, chevron to full profile. No giant empty dark header. Avoid showing age on main page. Use soft curved bottom/gradient fade into white.
B. QUICK SHORTCUT ROW: four equally spaced premium colorful illustrations with short labels: VIP Center, Shop, Avatar Studio (our own interpretation of PLAY Show), My Space (our own version of My Home). Tap targets >=48dp. Use actual imported icons. Small badge NEW only when actual new content exists. Avoid card outlines around every icon; leave whitespace like screenshot.
C. SOCIAL MICRO-STRIP: preserve Friends, Following, Followers and wallet/level/badges from old implementation but make them optional compact horizontal pills below shortcuts, not giant dashboard panels. Real data only. If they make the page cluttered, use one slim scrollable stats strip or link to Profile Stats; do not remove access.
D. MAIN MENU: simple white list, left 24-30dp illustrated icon, 16sp readable dark label, optional concise right subtitle, subtle chevron, generous 58-68dp row height. Group with subtle spacing or hairline dividers, not thick dark cards. First group: Moments, Stats, Visitors. Second: Invite Friends, Badges, Contributions, Family Hub. Third: Wallet/Top Up, My Gifts, Backpack, Shop purchases. Fourth: Language, Parental Controls, Help Center, Security Center, Settings. Fifth (inside Settings or end of list): About, Logout. Existing showcases can live in Profile/Badges/Gifts detail screens rather than bloating main page; keep their routes and data.
E. FLOATING GIFT PACK: reuse existing 48-hour offer widget if present; anchored above bottom navigation, never covering Settings or taps; opens offer bottom sheet, not Top Up directly. Stable server/persisted expiry and no fake prices.
F. BOTTOM NAV: Games, Voice Room, Chats, Discover, Me; selected Me state correct; existing NavHost, proper back handling, scroll restore and safe navigation bar inset. Never rename tabs incorrectly.

## COLOR AND STYLE
White #FFFFFF, canvas #FAF9FF, ink #26233C, secondary #8D8AA3, lavender #EFE9FF, cyan #31CAD9, peach #FFE7D2, warm gold #F5BD62. Prefer existing app theme tokens. Gentle gradients only in hero and illustrated assets; no black sections, neon borders, oversized text, emoji-only icons, excessive shadows or copied WePlay assets. Consistent rounded corners and smooth edges. Support small Android phones, text scaling, touch targets, TalkBack, dark system mode (explicit app theme handling), reduced motion and status/navigation bar insets. Keep screen scrollable using LazyColumn or equivalent; stable keys.

## EVERY CLICK MUST WORK — REQUIRED DESTINATIONS
PROFILE HEADER -> profile details and Edit Profile: actual avatar/name/bio, optional birthday/privacy, image picker, validation, save/cancel, backend sync and rollback on failure.
VIP CENTER -> current tier, benefits, renewal/purchase history and plans only from real configured services; unsupported plans explicitly unavailable. Play Billing and server verification for eligible digital goods.
SHOP -> real catalog, categories, preview, owned inventory, server prices and purchase confirmation; do not fake transactions.
AVATAR STUDIO -> preview and equip owned frames, avatar styles and cosmetics, upload/select avatar, save/cancel; don't claim 3D character editor unless implemented.
MY SPACE -> personal room/profile decoration, visibility, visitor permissions and owned decorations where backend supports them; if missing show clear honest coming-soon preview with a working back action.
MOMENTS -> user's actual moments/posts, create/edit/delete authorized content, comments, privacy and report/block; reuse Discover implementation.
STATS -> actual games played, wins, voice room sessions, XP and achievements, real time ranges; meaningful empty state, not fabricated metrics.
VISITORS -> real privacy-respecting visitor list and new count if tracking exists and user has opted in; permission/privacy settings, no fake visitors.
INVITE FRIENDS -> Android Sharesheet and working deep link, referrals and rewards only if backend supports verification; no invented free gifts.
BADGES -> earned/locked badge grid, details, progress and equip; preserve previous showcase.
CONTRIBUTIONS -> actual family/room/community contributions, history and time range; no invented scores.
FAMILY HUB -> existing family profile, search, join/create, roles, requests, leave confirmation; preserve existing features.
WALLET / TOP UP -> authoritative coin balance, transaction history, billing flow, server verification and duplicate purchase protection; never mutate coin balance on client alone.
MY GIFTS -> received/sent gifts and counts, actual inventory, history and detail previews.
BACKPACK -> owned items, frames, effects, expiration, equip/unequip and persistence.
LANGUAGE -> supported locale selector, save and apply, no fake translations.
PARENTAL CONTROLS -> safety options appropriate to age/region; privacy, blocking, chat restrictions and purchase safeguards, real backend enforcement or explicit unavailable state; no pretend parental lock.
HELP CENTER -> FAQ, support entry points if configured, report issue with details, status and troubleshooting; never invent support addresses.
SECURITY CENTER -> actual linked accounts, sessions/devices, password management, account verification, blocked users, privacy, 2FA only if supported; never invent low-security warning.
SETTINGS -> notifications, audio, privacy, account, language, permissions, legal and about. Every toggle persists and applies or clearly says unavailable.
LOGOUT -> confirmation, disconnect voice service safely, invalidate/clear session, navigate to login/signup and clear protected back stack.
ABOUT -> actual app version/build, terms/privacy/license links only if configured.
GIFT PACK -> existing offer sheet and countdown, no direct Top Up redirect.
FRIENDS/FOLLOWERS/FOLLOWING -> paged lists and real profiles, back navigation, privacy and blocking.

## UX BEHAVIOR FOR EVERY DESTINATION
Use native Compose screens or bottom sheets appropriate to content. Every destination must have proper top bar/back, loading, empty, offline, error/retry and success feedback. Do not create dead-end pages, dummy toast-only implementations or hardcoded example names/coin balances. Where backend is absent, implement UI structure and a truthful unavailable state and list the missing API. Reuse existing repositories; don't duplicate business logic. Use ViewModel/StateFlow and lifecycle-aware collection.

## NAVIGATION REGRESSION — HIGH PRIORITY
Previously the Games tab became unclickable after opening menus. Inspect modal scrims, pointer interception, zIndex, nested NavHost, bottom-nav visibility, stale overlay, and back stack. Test Games -> Me -> Edit -> Back -> Me -> Games; Voice Room -> Me -> Family -> Back -> Me -> Chats; Me -> Gift Pack -> Close -> Games; Me -> Settings -> Logout -> login; double taps, Android system Back, tab reselection, process recreation and deep links. Preserve first four tabs exactly except essential navigation fixes.

## PERFORMANCE AND ANIMATIONS
Lightweight press scale and tint, XP progress animation on actual value change, subtle avatar border glow, bottom-sheet transitions; respect reduced motion, avoid perpetual offscreen animation and huge recompositions. Efficient image loading and caching. No heavy asset decompression on UI thread.

## DELIVERABLES AND PROOF
- Implement actual source changes, not just a mockup.
- `docs/ME3_AUDIT.md`: before/after screen and route inventory.
- `docs/ME3_INTERACTION_MATRIX.md`: each button, actual route, data source, status WORKING / DEMO_ONLY / BLOCKED / NOT_TESTED, test evidence.
- `docs/ME3_IMPLEMENTATION_REPORT.md`: files changed, 50 asset imports, build/test commands and real outcomes, backend gaps and Android Studio manual checks.
- Run project Gradle wrapper debug build (`:app:assembleDebug` or actual module), unit tests and lint if available. Fix new errors; never claim tests ran if they did not.
- No package rename, no final branding, no automatic git push.

## EXECUTE
Start by inspecting project and screenshot, then implement ME 3.0 in stages: (1) audit and assets, (2) main screen, (3) existing navigation and features, (4) missing feature screens and honest states, (5) QA and reports. Continue until working build and documented test results. The main page MUST resemble the CLEAN LIGHT REFERENCE more closely than the rejected dark dashboard while retaining the existing functionality behind the menu.
