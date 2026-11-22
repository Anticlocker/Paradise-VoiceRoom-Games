# VYBORA — ANTIGRAVITY MASTER IMPLEMENTATION PROMPT
## Game Rooms + Create Voice Room + 8-Seat Stage + Optional Partner Seat + Attach Seats + Linked Page QA
### Kotlin + Jetpack Compose — Existing Android Project, No Rewrite

You are the principal Android architect, Kotlin/Jetpack Compose engineer, live-room UX designer, accessibility engineer, state-machine engineer and QA lead for my EXISTING VYBORA Android app. I edit in Antigravity and test the native APK with Android Studio. **Perform actual source changes and checks, not a plan-only reply.**

## REFERENCE AND NON-NEGOTIABLE RULES

I have supplied four current-app screenshots in this order: (1) white Games/Home, (2) dark Game Rooms with illegible white card text, (3) Create Voice Room with header colliding with the Android status bar, (4) current Aurora joined room. Match the visual QUALITY of my bright White/Lavender/Cyan Games/Home and Voice Lounge pages, but do not copy WePlay layout, graphics or proprietary content. Reuse earlier installed original VYBORA asset packs where appropriate.

Work in existing project; do not change `applicationId`, package name, authentication flow or working navigation; no new project, no WebView, no full-screen static screenshot. Preserve existing view models, repositories, tests, actual voice integrations and client/server boundaries. Do not introduce arbitrary Gradle/AGP upgrades. Do not force-push or publish. Keep each iteration buildable. Treat the backend-less features as **LOCAL DEMO**, not live. Do not fabricate real participants, purchases, coin balances or successful microphone connections.

### Outcome priority

1. Fix the demonstrably broken Create Room header/system insets and illegible Game Rooms text.
2. Rebuild Game Rooms listing with native responsive light VYBORA design and real interactions.
3. Rebuild full Create Voice Room form, validation, mode/tags, privacy/password and confirmation workflow.
4. Change Joined Room seat stage to **1 host seat + exactly 8 regular numbered mic seats (1–8)** in a responsive 4×2 grid, and **1 optional separate Partner Seat to the RIGHT of the host** when owner enables it.
5. Add **Partner Seat** toggle and invite controls accessible to host/owner, and distinct **Attach Seats** option in the host-side More/Manage Seats menu to bond two regular occupied seats with mutual consent.
6. Audit and fix all navigable buttons from Games, Game Rooms, Create Room, Voice Lounge and Joined Room; preserve other existing screens.
7. Use the supplied new asset pack automatically, implement state/tests and report remaining backend blockers honestly.

## PHASE 0 — INSPECT, BACKUP, REPORT

Read actual Gradle settings, application module, `MainActivity`, Navigation Compose graph, `Scaffold`, system bar handling, Game Rooms screen, Create Room screen, Joined Room screen, related ViewModels/models/repositories, LiveKit adapter, resources, previous `vb_` drawable packs and existing tests. Use `git status`, ensure a recoverable checkpoint or reversible changes before major edits; do not discard changes.

Create `docs/ROOMS_SEATS_AUDIT.md` with every clickable UI control, current route, actual callback, current state and root cause of failures. Take stock of which screens and services are LOCAL/DEMO/LIVE/BLOCKED. Do not ask for paths before searching the workspace.

## PHASE 1 — IMPORT THE SUPPLIED PACK

Locate folder `VYBORA_Rooms_Seats_Update_Pack` in accessible workspace. Read `README.md`, `docs/asset_manifest.json`, this prompt, `kotlin/SeatAndBondModels.kt.txt`. Copy **exactly the needed** new `vb_rs_*.webp` assets from `android/app/src/main/res/drawable-nodpi/` into the REAL application module's `src/main/res/drawable-nodpi/`. Do not import the example `android/app` as a nested project. Validate Android resource names, transparency, dimensions, conflicts, alpha edges and drawable references. Do not overwrite earlier `vb_vr_*` assets. Use proper PNG/WebP decoding, intrinsic artwork aspect ratios and `ContentScale` (never stretch isolated characters). For responsive cards use Compose `Surface`/`RoundedCornerShape`, text layered as Compose (not baked into images). Full list in manifest.

Relevant new resource names:

- Game rooms: `vb_rs_bg_game_rooms`, `vb_rs_card_room_light`, `vb_rs_cover_friends`, `vb_rs_cover_music`, `vb_rs_cover_games`, `vb_rs_cover_pk`.
- Room creation: `vb_rs_bg_create_room`, `vb_rs_bg_advanced_settings`, `vb_rs_create_room`, `vb_rs_voice_mic`, `vb_rs_privacy_shield`, `vb_rs_password_lock`, `vb_rs_mode_music`, `vb_rs_mode_games`, `vb_rs_mode_pk`, `vb_rs_mode_dice`.
- Optional partner seat: `vb_rs_bond_heart`, `vb_rs_add_seat`, `vb_rs_frame_partner`, `vb_rs_frame_partner_selected`, `vb_rs_seat_partner_empty`.
- Attach seats: `vb_rs_attach_pair`, `vb_rs_frame_bonded`, `vb_rs_seat_linked`, `vb_rs_seat_link_pending`, `vb_rs_link_aurora`, `vb_rs_link_gold`, `vb_rs_link_pending`.
- Shared: `vb_rs_host_crown`, `vb_rs_frame_seat_selected`, `vb_rs_card_create_light`, `vb_rs_card_option_selected`, `vb_rs_bg_partner_card`, `vb_rs_bg_attach_card`, `vb_rs_bg_empty_rooms`.

Use images as artwork accents, NOT to replace native buttons or as fake avatars of real people. The connector WebPs are optional decorative styles; live connected lines must be positioned based on measured seat centers using Compose `Canvas` or a custom layout behind seats, so orientation/resizing never breaks them. Avoid stretch-to-fit distorted connector images.

## PHASE 2 — GAME ROOMS SCREEN: VISUAL REDESIGN + FULL INTERACTION

Screenshot issues to fix: page has a dark navy background, bright white room cards but their titles are nearly white and unreadable; unselected filter chips have white text on almost white surfaces; default colored-letter placeholder covers; repetitive cards with oversized vertical spacing; back/plus controls should align with safe status-bar insets; no polished empty/loading/error flows.

Redesign to a distinct **VYBORA Game Rooms** look coordinated with Games/Home:

- Light pearl `#F8F7FF` or equivalent background, dark `#211B38` readable titles, white cards, lavender strokes, cyan selected state, violet accent, gold optional highlight. Keep immersive Joined Room dark.
- Top App Bar: back, "Game Rooms", optional actual count, search and bright Create Room button. Must respect status bar/cutout; do not double-apply insets inside Material3 Scaffold.
- Top original hero panel "Find your next squad" with native text, original image background `vb_rs_bg_game_rooms`, clear "Create Room" action and optional room rules shortcut. Do not recreate WePlay HOT ROOM poster.
- Horizontal filter chips All, Games, Deduction, Casual, Music/KTV and any categories actually represented in existing data. Distinct selected/unselected labels; don't relabel categories inaccurately. Tap must filter data, show accessible selection state, preserve scroll where appropriate.
- Search action expands native searchable field; IME search, clear, cancel, empty result, partial/case-insensitive matching, category+search intersection, keyboard/back flow.
- Room cards (premium 16–20dp corners, fine outline, subtle shadow): illustrated cover/legitimate room image, strong title contrast, verified host name, category, participants and capacity, privacy/lock badge, status (local demo vs live), and a primary "Join" action. Give titles at least 2 lines/ellipsis; don't show incorrect users/counts. Use provided cover fallback for **demo rooms only**, not fake real-room thumbnails.
- "Join" must pass correct roomId; disabled/full/private/locked room behaviors show clear info and permission/password entry where required. Prevent rapid duplicate join. On success navigate to joined room once, on failure remain in list with retry. Real backend confirmation is needed for LIVE. For local demo present preview state.
- Plus/Create Room opens the existing Create Voice Room screen; back returns correctly to prior tab/scroll position. Add room refresh, sorting where data supports it, loading skeleton, empty illustrations, safe error/retry.
- Use LazyColumn stable IDs; disable heavy offscreen animation; correct bottom system inset. No hard-coded fictitious online data.

## PHASE 3 — CREATE VOICE ROOM: COMPLETE REDESIGN AND FORM VALIDATION

**Fix screenshot collision immediately:** title "Create Voice Room" and X/close icon are drawn over the phone's clock/status icons. Inspect Compose Scaffold, `TopAppBar`, `WindowInsets.statusBars`, edge-to-edge setup, status bar contrast and `Modifier.statusBarsPadding()`. Apply exactly one appropriate inset policy; ensure no toolbar overlap on real phones, cutouts and font scaling. Never solve by inserting arbitrary top pixel spacers.

Build a coherent original Create Room flow:

1. Safe compact header/back plus progress/"Create Lounge" heading. Cancel/back preserves draft or prompts about unsaved changes appropriately.
2. Original hero/cover picker with `vb_rs_bg_create_room` and room icon; media picker when supported, accessible default cover, preview and crop.
3. Title text field: required, trimmed, validated, 3–40 characters (adapt to existing backend constraints), visible remaining count and clear inline errors; no keyboard overlap.
4. Room tag selector: Friends, Music, Game, Chats, PK etc mapped to actual supported modes; show selection; allow only valid combinations. Prevent unsupported modes from pretending live.
5. Room mode cards with original icons: Normal, Music/KTV, Games, PK, Private/Friends, and any *actually implemented* auction/video modes. Do not enable a mode that cannot function; show Coming Soon with a truthful explanation.
6. Public/private room control with explanation; private room requires invite/authorized access in live mode.
7. Password toggle: reveal password field only when enabled; minimum length and backend policy, confirmation if needed, hide/reveal accessibility; do not log plaintext or persist insecurely. Never broadcast password in room listing.
8. Seat count: **default and core target is eight regular mic seats plus separate host; partner is optionally additional**. If UI currently offers 8, 10, 12, do not imply 10/12 work with a fixed eight-seat stage; either adapt seat engine and mode appropriately or disable non-eight choices until supported, with explanation.
9. Add advanced settings: room announcement, entry approval, seat request permission, seat lock defaults, partner-seat enabled by default (off), attachment permission policy, max listeners, report/safety settings as supported.
10. Show a compact preview summarizing room visibility, mode, tags, host+8 seat layout, optional partner and safety policies.
11. Sticky "Create Room" action above IME/navigation insets, enabled only when form valid; prevent double submission; success/error states.
12. REAL create route must require backend response and use returned real ID. In LOCAL DEMO only create a local room ID/state labeled as demo, persist optional draft and navigate to its preview; never claim a public real room exists.

Use Material3 TextField/Switch/FilterChip style or existing design tokens, not white-on-white controls. Keep page scrollable with clean spacing and accessible 48dp touch targets. Do not place keyboard over Confirm.

## PHASE 4 — JOINED VOICE ROOM LAYOUT: HOST + EIGHT REGULAR SEATS

Do not rewrite the entire existing Aurora Lounge. Preserve background, chat area, action dock, gifts, PK, games, listeners, navigation and LiveKit adapter. Only remodel the speaker stage and relevant management state.

**Invariants:**

- Exactly ONE separate host seat (id `host` or existing canonical owner seat ID), visually featured.
- Exactly EIGHT regular numbered mic seats (`seat_1` through `seat_8`) BELOW host in two rows of four: [1,2,3,4] then [5,6,7,8]. Use IDs, not array indices, and stable keys. Current implementation appears to have fewer than eight regular seats; explicitly add all eight without destroying existing users.
- Optional PARTNER seat is extra and distinct (`partner`), not one of seats 1–8; never renumber normal seats when toggled.
- When disabled, host stays well-centered and no blank partner slot is reserved.
- When enabled, host and partner appear as a balanced duo (host LEFT, partner RIGHT) near top, with light original cyan–pink motif. The regular 4×2 grid stays below them.
- Host + 8 + optional partner = up to 10 stage positions, distinct from total listener capacity. Speaker count, participant count and seat occupancy must use actual models, not naïve sum.
- On small screens reduce seat dimensions gracefully, preserve usernames and mic badges, and let chat remain usable. No offscreen participant icons or touch overlap. Use BoxWithConstraints/custom Layout where needed rather than arbitrary absolute coordinate hacks.
- Occupied seats use actual profile avatars and role frame; empty seats use plus/empty state, locked show lock, request pending indicator, muted icon, speaking ring from transport data (clearly demo when offline). Keep seat actions functional and role-aware.

### PARTNER SEAT FEATURE (distinct from Attach Seats)

Add host/owner-only **Partner Seat** toggle under More > Room Settings / Seat Management. An optional convenient host-side toolbar chip can open it. Owner enables/disables partner seat; display the new seat on RIGHT side of host with partner frame.

State: `partnerEnabled`, `partnerSeatOccupantUserId?`, `partnerInvitation?`, `partnerLocked?` plus trustworthy persisted room configuration. Host may invite a participant or approve an eligible request, occupant may accept/decline/leave. Toggling disabled while occupied requires confirmation and graceful seat departure according to room policy. Cannot evict a real user silently. Only one occupant; apply authorization and conflict handling. Do not infer a romantic relationship from sitting in this seat. Partner seat is a *room-stage role/position* and must work for any consenting users regardless of gender.

Show mic/seat states consistent with other seats. Backend must validate live invitation, role and occupancy; local demo may simulate selection clearly labeled DEMO. Avoid storing a cosmetic partner role as a real relationship without explicit separate consent.

## PHASE 5 — ATTACH SEATS: OWNER-CONTROLLED BOND BETWEEN TWO SEATS

Add **Attach Seats** in host/owner's More > Manage Seats menu, alongside Partner Seat. These are different features:

- Partner Seat = optional extra stage position beside host.
- Attach Seats = visually connects TWO existing eligible regular mic seats, for example seat 2 and seat 6, WITHOUT moving or renumbering either seat.

### Host workflow

1. Owner taps More > Manage Seats > Attach Seats.
2. Show a clear sheet with current regular seats 1–8, occupied/empty/locked status and participant avatars, plus instructions "Select two occupied seats".
3. Owner selects first and second *distinct* eligible regular seats. Highlight selections using `vb_rs_frame_seat_selected`. Prevent self-pair, empty/locked/unavailable seats, duplicate active bonds, stale occupants and unauthorized actions.
4. Show preview: user A ↔ user B, both seat IDs, purpose, visibility, and buttons Request Bond / Cancel.
5. Send consent invitations to both occupants; either participant can Accept or Decline. Only after both accept should the bond become ACTIVE; host must not forcibly assign a romantic/couple relationship.
6. When ACTIVE, render a premium subtle animated aurora connection behind avatars between actual measured seat centers with a heart/infinity symbol centered where unobstructed, using `vb_rs_link_aurora`, `vb_rs_bond_heart`, and `vb_rs_frame_bonded` as supporting artwork. Use native Compose Canvas measured geometry to align endpoints and recalculate across resize/recomposition. Connector overlay must be `pointerInput`-nonintercepting and behind seats; it must never block clicks.
7. Tapping the bond emblem opens Bond Details with users, state, detach/cancel, privacy and report options. Host can detach by policy, and either occupant can revoke consent and detach at any time. No penalties for declining.
8. If occupant leaves, changes seat, room closes, either blocks the other, or consent is revoked, invalidate/detach or request reconfirmation. Do not silently reattach to replacement seat occupant.
9. Display no permanent romantic badge or relationship in user profiles unless users separately opt in to that feature.

### State machine

`NONE -> DRAFT -> REQUESTED -> PARTIALLY_ACCEPTED -> ACTIVE -> DETACHING -> DETACHED`; possible `DECLINED`, `EXPIRED`, `CANCELED`, `FAILED`.

Use stable bond ID, room ID, seat IDs, user IDs, initiator, per-user consent states, revision/version and timestamps. Local state must be testable, live state server-authoritative. Prevent concurrent duplicate bonds. Define whether a seat may be in only one active bond at a time (default yes); reject conflicting requests. Do not allow any user to create forced bonds outside host authority. If optional bonding of host/partner is desired later, make it a separate explicit policy; default Attach Seats only connects seats 1–8.

### Attached-seat graphics

- Bond idle/pending/accepted/active/detached states must be distinct.
- Use soft cyan→violet→pink linked line, not intrusive neon beams.
- Decorate lightly with heart/infinity indicator and minimal sparkle, honoring reduced motion.
- Connector should adapt when seats in different rows (curved path, no crossing through the chat/action dock). If lines interfere, fade and draw a small bond badge on endpoints instead.
- Manage bonding purely with correct room data; the assets are not the source of truth.

### Consent and moderation

No age/gender assumptions; no coercive romantic implications; users can dismiss notifications, block/report and revoke. Do not collect/store unnecessary sensitive relationship data. Host seat-management ability is not a substitute for user consent or server policy enforcement.

## PHASE 6 — EVERY BUTTON ON THESE FLOWS

Audit and repair routes/actions for: Games/Home > Game Rooms / Voice Lounge / Tasks / Top Up / Ranking / Friends / Community profiles; Game Rooms > filters/search/Join/Create/back/room details; Create Room > back/cover picker/title/password/privacy/seat count/mode/tag/advanced/confirm; Joined Room > all header buttons/host profile/seat actions/partner/attach/participant list/gift/PK/games/chat/mic/audio/More/report/leave. Preserve unrelated screens and existing data services. A click isn't "working" unless the correct state/route/outcome occurs. Keep nested sheets/back priority correct: IME/sheet/dialog then page; no invisible scrims blocking Games tab. Stable NavController, proper popUpTo/launchSingleTop/restoreState as fits actual graph.

For unimplemented live services, provide a meaningful local demo or honest disabled/integration-pending state. Do not fake coins, remote messages, user positions, matches or live room creation.

## PHASE 7 — VIEWMODELS, PERMISSIONS AND PERSISTENCE

Use existing ViewModels and StateFlow with a single room source of truth. Separate `RoomConfig`, `RoomSeat`, `PartnerSeatState`, `SeatBond`, `SeatBondConsent`, `CreateRoomFormState`, `RoomJoinState`, `RoomUiEvent`. Do not proliferate duplicate models. Make testable pure Kotlin state transitions. Use Room/DataStore only for appropriate local drafts and demo settings, not sensitive production passwords or authoritative real-time state. Persist proper room seat/partner/bond data on trusted backend once available. Backend validates membership, host role, seat grants, both consents, bond revision and idempotency; LiveKit token issued only by backend. Microphone permission should be requested at intentional user action. Never expose API secrets in APK. After disconnect, clear obsolete participant/bond data and release mic resources.

## PHASE 8 — UI QUALITY / PERFORMANCE

Use compact, consistent design tokens, original WebPs and native shapes. Ensure text contrast and selection on all chips/cards, 16–24dp rounded shapes, smooth edges and correct image crop. Respect Material3 Scaffold `innerPadding` and `WindowInsets.safeDrawing`/`ime` without duplicate padding. Verify top header under clock/cutout, status-bar text/icon contrast, bottom safe area on gesture and 3-button navigation, small screens, large font, landscape if supported. Animate seat changes using `AnimatedVisibility`/`animateContentSize` only when not jittery. Real speaking indicators are event-driven. Pause offscreen animations, avoid giant bitmap churn, use stable lazy keys, no accidental scroll or click interception, minimize recompositions and profile on real device when possible.

## PHASE 9 — TESTS AND ACCEPTANCE

Add Kotlin unit tests and Compose UI tests where possible. Test all of these cases:

1. Game Rooms room text is dark/readable on light cards and filter labels are readable in ALL states.
2. Create Room toolbar never overlaps the Android status bar at normal/large font and on cutout devices.
3. Game Rooms > Create > Back returns to correct list; cancel preserves or discards form intentionally.
4. Create Room title/password/privacy/mode/tag/seat validation, invalid form blocked, double-submit prevented, local demo vs live confirmed.
5. Exactly eight numbered regular seats render in a 4×2 grid regardless of partner toggle; host separate.
6. Partner toggle OFF shows no partner; ON adds one extra seat on host's RIGHT; OFF after occupied prompts correctly and handles occupant safely.
7. Guest cannot toggle partner or attach seats; host/authorized moderator rules obey actual policy.
8. Attach Seats picks two distinct occupied regular seats; invalid/self/duplicate/revoked attempts rejected.
9. Bond remains PENDING without both consents; ACTIVE only after both accept; declined/canceled/expired/revoked detach correctly.
10. Bond endpoints align with correct seats across resize, partner toggle, avatar changes and font scale; bond graphics never block seat taps.
11. Leaving either seat invalidates bond and no replacement occupant inherits it.
12. Empty/locked/occupied/speaking/muted states behave appropriately. Real voice state is not faked.
13. Room join/leave, back/sheets, Games tab switching and other existing first-page controls remain functional.
14. Scroll and animation remain smooth on midrange phone; no crashes, overlapping system bars, leaks or invisible click blockers.

Run actual existing Gradle wrapper command, e.g. `./gradlew :app:assembleDebug` or `gradlew.bat :app:assembleDebug` depending OS and real module; test and lint where available. Fix introduced compile errors. Do not claim device-specific tests occurred if not run. Explain any missing backend, auth, token, voice, persistence or server-permission blockers rather than simulating them as complete.

## PHASE 10 — MILESTONES AND REPORT

Implement in coherent milestones, building after each:

- M0 audit/project state/assets, checkpoint.
- M1 Game Rooms contrast/style, navigation/filter/search/join fixes.
- M2 Create Room safe insets, complete form, local validation and create flow.
- M3 Joined Room host + eight numbered seats, responsive stage.
- M4 Optional Partner Seat toggle/invite/state handling.
- M5 Attach Seats consent flow, link rendering, detach and role permissions.
- M6 End-to-end navigation QA, accessibility, performance, regressions, tests.

Create `docs/ROOMS_SEATS_IMPLEMENTATION_REPORT.md` and `docs/ROOMS_SEATS_FUNCTIONALITY_MATRIX.md` with every control, actual test evidence, modified files, imported assets, build output, LOCAL/DEMO/LIVE/BLOCKED status, known bugs, remaining services and Android Studio verification instructions. Do not stop after merely planning. Complete safe UI and local functionality as far as actual project and tools permit; do not claim production-ready remote behavior absent a backend. Do not automatically push GitHub.

**FINAL SUCCESS TARGET:** Professional VYBORA Game Rooms and Create Voice Room pages consistent with the Games/Home and Voice Lounge theme; accessible controls with no status-bar overlap; existing Joined Room retaining its Aurora identity with 1 host + 8 regular seats + optional right-side Partner Seat; and a separate, opt-in, host-managed Attach Seats bonding system with beautiful responsive connectors and honest live/demo boundaries. Begin by inspecting the code and implementing M0 and M1, then progress through validated milestones.
