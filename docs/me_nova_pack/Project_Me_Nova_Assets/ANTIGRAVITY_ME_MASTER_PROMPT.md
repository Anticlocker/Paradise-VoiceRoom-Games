# ANTIGRAVITY MASTER PROMPT — PROJECT ME PAGE 2.0
## Native Kotlin + Jetpack Compose | Existing Social Gaming Android App | BRAND NAME NOT FINALIZED

### ROLE & OBJECTIVE
Act as principal Android architect, premium UI/UX designer, Kotlin/Compose engineer, product manager, social gaming systems designer, accessibility/security specialist, and QA lead.

I supplied TWO screenshots:
1. WePlay's Me page (inspiration): warm pastel header, illustrated profile, level progress, VIP Center / Shop / PLAY Show / My Home, and rows for Moments, Stats, Visitors, Invite Friends, Badge, Contributions, Language, Parental Control Mode, Help Center, Security Center, Settings.
2. Our CURRENT Me page: dark cyan header, letter avatar, profile ID/online/age/XP, Friends/Following/Followers, Coins/Level/Badges, Badge Showcase, Gift Showcase, and Account actions: Top Up, Edit Profile, My Gifts, Family Hub, Backpack, Settings, Log Out, About.

Our current page has MORE FUNCTIONALITY than the reference. DO NOT REMOVE IT. Create a BETTER ORIGINAL hybrid with a light white/lavender/cyan/gold theme matching our earlier Games, Voice Lounge, Chats and Discover redesigns. The brand name is NOT FINALIZED: use neutral `APP_NAME`/existing configurable app label. Do NOT introduce VYBORA branding, rename the package/application ID, or replace existing source repositories.

### BEFORE CHANGING CODE
- Inspect actual source tree, navigation, authentication, Me/Profile composables, user/profile ViewModels, wallet and purchases, friend/follow models, gifts, badge system, family, settings, and persistence.
- Inventory every existing clickable action and route. Record what is real, local demo, broken, or missing.
- Read supplied `README.md`, `docs/asset_manifest.json`, and this prompt.
- Create `docs/ME_PAGE_INITIAL_AUDIT.md` and a reversible checkpoint.
- Preserve existing auth, user data, saved profiles, existing screens, and backend integrations.
- No fake balances, visitors, friends, badges, purchases, security scores, or account verification.
- No empty `onClick` handlers, no silent no-ops, no fabricated success messages.
- Implement in milestones; run build/tests after each milestone; do not push to GitHub automatically.

### ASSET PACK
Find extracted `Project_Me_Nova_Assets` folder in workspace.
Copy its `android/app/src/main/res/drawable-nodpi/*.webp` into the EXISTING app module's `src/main/res/drawable-nodpi/`. Do NOT create a second app module.
Use manifest for exact names. 65 static original WebP resources: 48 functional feature icons, 8 fictional demo portraits, 3 header backgrounds, 6 badge illustrations. Prefix `me_nova_`. These are static vector-rendered images, not animated files. Use subtle Compose animations. Fictional demo avatars must never replace actual users' photos or be passed off as real profiles. Badge images are visual concepts; display them as earned only with verified badge data. Check resource collisions, transparency, memory and decoding.

### VISUAL DESIGN
Create a DISTINCT original composition (not WePlay copy):
- Background #FAF9FF / white; surface #FFFFFF; soft lavender #EFE8FF; cyan #35CCD6; royal violet #7656D9; soft gold #F5BD6C; ink #29263E; secondary #7F7B93.
- Premium light hero with gentle aurora gradient and softly rounded lower edge, not giant dark/cyan/black empty area.
- Hero includes real avatar with tasteful frame, name, verified indicator ONLY if truly verified, copyable ID, level/XP, and edit-profile chevron.
- Under hero, compact social stat strip Friends, Following, Followers. Preserve actual values; tap opens corresponding lists.
- Wallet/level/badges compact chips or small cards; maintain actual balances and routes. Avoid redundant level display if hero already shows it.
- Four original quick-access tiles: VIP, Shop, Avatar Studio (renamed from PLAY Show concept), My Space/Home. These are functional entry points or explicitly unavailable if backend not present.
- Horizontally scrollable earned Badge Showcase and Gift Showcase with "See all" and real collection counts.
- Organized grouped settings with elegant thin dividers and proper touch targets, not an endless wall of giant dark blocks.
- Floating Gift Pack offer control should respect current 48h offer logic, not obscure Settings, Logout or bottom navigation.
- Compact sticky bottom nav: Games, Voice Room, Chats, Discover, Me. Me selected state, safe insets, 48dp touch areas, correct back behavior.
- Respect small phones, font scaling, contrast, dark-mode system preferences where app supports them, and reduced motion. Avoid emoji-as-production-icons and hardcoded screenshot coordinates.

### PAGE STRUCTURE
A. PROFILE HERO: photo, nickname, account ID, verified status (server only), level, XP bar, profile-edit entry, share profile.
B. SOCIAL METRICS: friends, following, followers with paged lists, empty/error/loading states and privacy controls.
C. QUICK ACTIONS: VIP Center, Shop, Avatar Studio, My Space/Home. Keep existing Top Up access easy.
D. SHOWCASES: earned badges, received gifts, levels, achievements; preserve all existing current showcases and counts.
E. ACTIVITY & SOCIAL: Moments, Stats, Visitors, Invite Friends, Contributions, Family Hub.
F. WALLET & COLLECTION: Top Up, My Gifts, Backpack, Shop, purchase history, inventory.
G. ACCOUNT & SAFETY: Edit Profile, Language, Parental Controls, Help Center, Security Center, Settings, Logout, About.
Use expandable sections if needed, but never hide essential controls behind dead surfaces.

### COMPLETE BUTTON/FLOW CONTRACTS
1. PROFILE HEADER -> opens editable profile with real current name, avatar, bio, birthday/privacy fields supported by backend, save/cancel/error/validation, photo picker and upload. Do not infer gender or overwrite user-selected avatar.
2. FRIENDS/FOLLOWING/FOLLOWERS -> real paginated lists, profile detail, add/remove/follow/unfollow with confirmation and backend consistency, blocked users excluded as policy requires.
3. VIP CENTER -> actual tier benefits, membership state, eligibility, available subscription plans and legal terms. No invented VIP privileges or fake activation. Real purchases only through applicable Play Billing and server verification.
4. SHOP -> original cosmetics/catalog categories (frames, avatar items, gifts, themes), product details, preview, inventory, ownership, real price/eligibility, purchase verification.
5. AVATAR STUDIO -> current avatar preview, available original presets, profile-photo upload, frames, cosmetics, save/cancel. 3D character customization is a separate advanced milestone requiring a real renderer/assets; don't pretend static WebP portraits are 3D.
6. MY SPACE/HOME -> personal space/profile presentation, visitors and decoration preview only where supported; privacy settings, save/restore.
7. MOMENTS -> existing user's posts, create/edit/delete where authorized, privacy, comments/reactions, moderation; preserve existing Discover Moments integration.
8. STATS -> actual game stats, wins, play sessions, voice-room time, streaks/XP with dates and empty states; don't manufacture statistics.
9. VISITORS -> real opt-in visit events, privacy controls, deduplication and retention, no fake "new visitors" numbers; obey user privacy and safety settings.
10. INVITE FRIENDS -> Android Sharesheet/deep link, invitation attribution only if supported, referral rewards only after verified backend event; never promise gifts before eligibility.
11. BADGES -> earned/locked badges, requirements, progress, details and equip actions; avoid displaying demo badges as earned.
12. CONTRIBUTIONS -> verified family/room/community contributions, time range, leaderboard or personal history, privacy; no invented points.
13. FAMILY HUB -> existing family, roles, join/create, invitations, membership requests, family activities, permissions, leave confirmation. Preserve current functionality; use WePlay family roles/quests only as inspiration.
14. MY GIFTS -> received/sent gift collection, counts, inventory, previews and supported gifting flows, server-authoritative records.
15. BACKPACK -> owned frames, effects, cosmetics, gifts, equip/unequip, expiration, inventory persistence.
16. TOP UP -> existing premium coin store and transaction history; Google Play Billing for eligible digital purchases; server-side verification, idempotent fulfillment, no fake client-side coin credit.
17. LANGUAGE -> list supported languages, selection, persist locale, update supported resources without losing navigation state.
18. PARENTAL CONTROLS -> age-appropriate safety settings, restricted interactions and purchases, privacy, report/block tools; no claim of effective parental enforcement unless server policy supports it. If guardian verification is required, do not bypass it.
19. HELP CENTER -> FAQs, report a problem, support contact if configured, error/network troubleshooting; no invented support address.
20. SECURITY CENTER -> account email/phone verification where supported, sessions/devices, password changes, linked login providers, block list, privacy and 2FA only if implemented. Do not show fabricated "low security" scoring.
21. SETTINGS -> notifications, audio, appearance, privacy, language, account, permissions, app version, legal docs, data controls. Preserve existing settings routes.
22. LOG OUT -> confirmation, invalidate tokens, clear sensitive local session state, disconnect active voice room, navigate to login/signup without allowing protected pages via back stack.
23. ABOUT -> real version/build info, credits, policies, terms and licenses; use real configured URLs.
24. GIFT PACK -> tap opens the existing 48-hour offer bottom sheet, not Top Up; offer is stable per account/cycle, prices from real catalog, no fabricated purchases.

### RESEARCH-BASED PRODUCT GUIDANCE
The publicly documented WePlay app includes avatar/dress-up, VIP gifts, daily missions and family/social experiences. Treat these as inspiration only. Screenshot explicitly shows other entry points, but it does NOT establish exact underlying behavior for Visitors, Security Center, My Home or Contributions. Implement those using our original product logic and real existing services; never claim WePlay's private implementations were verified.
A family can involve roles, quests and shared rewards, but don't copy exact mechanics or use gambling-like randomized paid rewards without an explicit compliance review.
Avoid copyrighted brand artwork, UI pixel copying, trademark use and false claims.

### ARCHITECTURE
Follow current Kotlin/Compose conventions: ViewModel + StateFlow, repositories, lifecycle-aware state, Navigation Compose, immutable UI models. Reuse existing API contracts and navigation routes. Suggested domain boundaries: ProfileRepository, SocialGraphRepository, WalletRepository, BadgeRepository, InventoryRepository, FamilyRepository, SettingsRepository, SecurityRepository. Don't duplicate repositories already present.
Data state: Loading / Content / Empty / Error. Use actual server/user data. Demo mode must be explicitly labeled. Prefer event-driven updates; don't hardcode account ID, coin balance, visitor count or badges from screenshots.
Persist profile avatar selection. Respect privacy preferences. Use scoped photo picker rather than broad storage permission.

### ANIMATIONS
Subtle header gradient, avatar-frame shimmer, stat counter transition only for real changed values, badge reveal, quick-action press and bottom-sheet motion. Stop offscreen animations and respect reduce-motion. Avoid heavy blur, infinite animation on all list items and giant bitmaps.

### NAVIGATION & REGRESSION
- All five bottom tabs work after opening any Me submenu; no invisible overlay intercepts taps.
- Back from Profile Edit, Shop, VIP, Stats, Visitors, Family, Backpack, Settings returns to Me with scroll preserved.
- Back closes dialogs/sheets before navigating.
- Logout clears protected back stack.
- Repeated tapping does not create duplicate destinations.
- Existing Games, Voice Lounge, Joined Voice Room, Chats, Discover and Tasks remain unchanged except essential integration fixes.
- Keep active voice-room connection lifecycle correct when navigating to profile from room.

### IMPLEMENTATION PHASES
0. Audit real source, routes, repositories, current functionality, assets and test baseline.
1. Import 65 WebP assets and implement original light Me layout, header, stats, shortcuts, showcases, grouped menu; compile.
2. Wire existing profile, wallet, gifts, family, inventory, settings, logout, Moments and social routes; compile and test.
3. Implement missing Visitors, Contributions, VIP, Avatar Studio, My Space, Security, Help and Parental Control entry points with truthful backend blockers; add real flows where services exist.
4. Polish navigation, permission handling, error states, accessibility, animations and performance.
5. Run Gradle debug build, available unit/UI tests and lint; verify on Android Studio emulator/device when available.

### ACCEPTANCE MATRIX
Test every button:
- Profile and Edit -> save/cancel/persistence/photo upload.
- Friends/Following/Followers -> correct lists and return.
- VIP/Shop/Avatar Studio/My Space -> proper destinations and data states.
- Moments/Stats/Visitors/Invite/Badge/Contributions -> real data or clear unavailable states.
- Language/Parental/Help/Security/Settings -> correct routes and persistence.
- Top Up/My Gifts/Family/Backpack -> preserved existing functionality.
- Logout -> confirmation, session invalidation, no back to private screens.
- Gift Pack -> modal, 48h timer and dismiss.
- Bottom nav and Android back -> no blocked Games tab or stuck screen.
- Offline/slow network/empty/error states; small phone/font scaling/keyboard/system insets.
Do not claim tests passed without execution evidence.

### REQUIRED OUTPUT
Create:
- `docs/ME_PAGE_INITIAL_AUDIT.md`
- `docs/ME_PAGE_FUNCTIONALITY_MATRIX.md` (Control | Existing behavior | Implemented behavior | Real/Demo/Blocked | Test evidence)
- `docs/ME_PAGE_IMPLEMENTATION_REPORT.md` (files changed, assets imported, actual build commands/results, tests, remaining backend blockers, manual test steps)
Run existing Gradle wrapper, e.g. `gradlew :app:assembleDebug` with actual module/OS. Fix introduced compile issues. Do not push automatically.

### FINAL COMMAND
BEGIN BY READING THE EXISTING PROJECT AND BOTH SCREENSHOTS. Import the supplied assets, redesign Me as a premium light original profile hub, preserve EVERY currently working feature, implement real routes and secure state, validate build/tests and report honestly. Do not stop at a plan.
