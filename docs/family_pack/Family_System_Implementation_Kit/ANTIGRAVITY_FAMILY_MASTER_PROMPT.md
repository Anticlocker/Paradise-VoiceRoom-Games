# FAMILY SYSTEM — ANTIGRAVITY MASTER IMPLEMENTATION PROMPT
## Existing Android Kotlin + Jetpack Compose app | Brand name NOT finalized

You are principal Android engineer, product architect, security engineer, game-economy designer, QA lead and UX designer. Implement a complete original Family system inside my EXISTING app. Reference the supplied 98-second WePlay family screen recording `112068.mp4`, but do not copy WePlay UI/artwork or assume undocumented numerical rules. Current app's theme: premium WHITE, PEARL, LAVENDER, CYAN and SOFT GOLD, rounded smooth cards, attractive original badges, restrained animations. App name is NOT finalized: no permanent brand and no Android application ID/package renaming.

## MANDATORY FIRST STEP: VIDEO & PROJECT AUDIT
- Read supplied `docs/VIDEO_OBSERVATIONS.md`, `docs/RESEARCH_AND_ASSUMPTIONS.md`, asset manifest, and this entire prompt. Reopen the actual user-supplied video if available and inspect key transitions at 0–10, 10–17, 18–27, 27–39, 39–47, 48–55, 60–63, 65–76, 78–96 seconds.
- Audit existing Family Hub, Me, Discover, voice rooms, chat, wallet, gifts, task systems, ranking, auth, Supabase/backend/Redis/LiveKit if present. Follow actual repository architecture, not assumptions. The project is Kotlin + Jetpack Compose; don't introduce React/Capacitor.
- Record existing routes and actions. Preserve all working features, data, IDs and API contracts. No new project/module. Do not push GitHub automatically.
- Family features must not show invented server data or imply fake purchases. Demo preview must be clearly labeled. No dead clicks.

## REFERENCE FEATURE INVENTORY
Visible video: Family main Profile tab (identity, activity, weekly chest, treasury, announcement), Tasks tab (activity tasks and actions), Members tab (search, status/role list), News tab (family history), Manage tab (settings, introduction, family sign, access rules), family chat with gifting, ranking, Gacha-like reward screen, Family Shop with virtual items, frames and signs. Video is a visual reference, not proof of exact thresholds, prices, chances, membership limits or permission policy.
Public WePlay FAQ: Leader/Deputy/Admin, personal Family Coins vs shared Family Funds, quests, donations, cheers, gifts, level-ups, 4 chest categories, weekly activity, sign, family-only cosmetics, family voice room, competitive stealing/barrier. Implement original and configurable values; do not clone numeric economy.

## INFORMATION ARCHITECTURE
ENTRY:
Me > Family Hub, Discover > Families, user profile > family badge, voice room > family, family invitation deep link. Entry for non-member: Browse / Search / Create / Join requests / Invitations; member: Family Home.

FAMILY HOME: light original hero with emblem, family name, unique ID, description, level badge, activity progress, member count, join policy and CTA; compact stat strip Family Funds, Family Coins (clearly personal), Weekly Activeness, next upgrade; navigation tabs `Overview | Quests | Members | Activity | Manage` (Manage visible only to authorized roles). Persistent family chat entry, shop, rewards, rankings. Avoid overlong empty header.

OVERVIEW: weekly chest progress, treasury ledger, family announcement, current events, contribution leaderboard, member spotlight, family sign, room status, invite button; accessible empty/loading/error.

QUESTS: Daily, Weekly, Community, Contribution tabs; real server-authoritative progress, eligibility, completed/claimed state, idempotent claim, reset UTC/region rules, streak if product decides. Suggested original quest types: participate in voice room, play a game with members, send a free cheer, complete check-in, contribute to a family event, welcome a new member. No paid purchase required for basic progression. Progress is server verified. Show reward and criteria before accepting.

MEMBERS: searchable paginated member roster; Leader, Deputy, Admin, Moderator (optional), Member; online state only from real presence; contribution/last activity visibility follows privacy. Member profile, invite, promote/demote, remove, mute family chat, role history, transfer leadership, leave. All privileged operations server checked and audited. No self-promotion, no removing leader, no transfer without explicit confirmation.

ACTIVITY / NEWS: event ledger of joins, departures, quest completions, promotions, level upgrades, donation receipts, chest distributions, event notices; filter, timestamp, actor, details, privacy, pagination, report and moderation. Separate system notifications from user chat.

MANAGE: family identity, avatar/banner upload, rules/description, announcement, recruitment policy (open/request/invite-only), minimum eligibility where applicable, applications queue, role permissions, blocked/banned list, family sign, treasury controls, room configuration, leadership transfer, disband with multi-step confirmation. Only show actions the user can perform.

FAMILY CHAT: actual chat service integration, messages, reactions, mentions, moderation, read status if supported, media policy, pinned notices, real gifting and gift event cards. Respect blocks, reporting, rate limits. No fake chat users. Preserve global Chats navigation and room lifecycle.

VOICE ROOM: if level unlock is configured and reached, family-owned LiveKit room (reuse existing room service). Membership-based permissions, host/cohost, seat controls, moderation, join/reconnect, background audio, Android mic permissions, state restoration. Do not claim unlocked room before server entitlement.

FAMILY ECONOMY:
- Family Coins: personal, non-cash virtual rewards earned from verified activities.
- Family Funds: shared non-cash progression resource with transaction ledger, permissions and upgrade costs.
- Separate platform wallet/purchased coins from Family Coins/Funds; never silently convert.
- Every mutation via backend transaction/ledger and idempotency key, never Compose local counters. Immutable event log, server timestamps, concurrency control, replay protection, rollback/error recovery.
- Gifts between members may grant activeness according to server policy; paid gifting must not be the only practical route to family growth. Use existing gift catalog and Play Billing verification where applicable.
- Donations require explicit confirmation, daily caps, anti-abuse, refund/chargeback handling.
- Family upgrade: threshold + cost + authorized role + atomic funds debit + level increment + reward entitlement. Never deduct twice.
- No real-money cash-out, peer-to-peer sale or trading of virtual assets.

PROGRESSION CONFIGURATION:
Use data-driven `family_level_rules` not hardcoded arbitrary WePlay values. Provide sample DESIGN-ONLY values in seed/dev environment (e.g. levels 1–10, activeness threshold, funds cost, member cap, perks), clearly marked provisional and adjustable through server config. Do not claim WePlay exact levels. UI shows current threshold from API.
- Level unlock categories: member capacity, emblem variants, family sign, custom frame, additional quests, optional family voice room, cosmetic showcase.
- Family tier names and visuals are OUR original designs: Bronze, Silver, Gold, Platinum, Diamond, Legend.
- Weekly sign reflects verified prior week activity; leader may customize text subject to moderation.
- Family-only frames unequip automatically when membership ends.

REWARDS & CHESTS:
- Four categories Normal, Advanced, Rare, Super inspired by public FAQ, with ORIGINAL appearance and our own eligibility rules.
- Chest lifecycle: issued, claimable, claimed, expired, returned to family inventory (if enabled); timestamps, ownership and server validation.
- Prefer deterministic disclosed reward bundles initially. If randomized mechanics are later enabled, disclose probability, eligibility, regional restrictions, and obtain compliance review; never make paid random-item flows part of MVP.
- Daily Gacha-like machine from reference should be an optional FREE daily reward experience, server-generated with one claim/day, visible reward odds if random and transparent history. No paid spins by default.
- Competitive stealing/barrier mechanics should NOT be enabled in initial release. Keep feature flag off pending abuse/safety review. If implemented later, use non-monetary points, opt-in participation, limits, audit, protection and appeal.

SHOP / COSMETICS:
Family-exclusive shop categories: frames, family signs, banners, room effects, profile decorations, gift items. Display actual ownership, eligibility, currency, expiry and equip states. No fake prices. Implement real purchases only when backend/catalog exists. Keep preview and inventory flows real.

RANKINGS:
Weekly activeness, contributions, family levels, friendly community activity; real aggregated data, tie-break rules, privacy controls, season reset and pagination. No fake ranks.

INVITATIONS / JOIN / LEAVE:
One active family per user by default (configurable); invite link expiration and revocation, apply/approve/reject, waitlist, join cooldowns, blocked users, family capacity, pending application management. Leader departure requires transfer or disband policy. Leave confirms loss of family-only cosmetics and access; don't erase personal earned history without policy. Member and funds changes must be atomic.

PERMISSIONS MATRIX (server-enforced):
Leader: all authorized operations including transfer/disband and upgrades.
Deputy: applications, member management except leader, upgrades if enabled, announcement, events.
Admin: approve requests, moderate chat, manage events, edit announcements as granted.
Moderator (optional): chat moderation only.
Member: quests, chat, gifts, free cheers, donations, rewards, shop, leave.
Build exact configurable permission matrix, display disabled/hidden actions accordingly, and verify every mutation on backend.

BACKEND DATA MODEL (adapt to existing schema):
families, family_members, family_roles, family_role_permissions, family_join_requests, family_invitations, family_quests, family_quest_progress, family_activity_events, family_wallets, family_ledger_entries, family_level_rules, family_level_history, family_donations, family_chests, family_chest_claims, family_daily_rewards, family_shop_catalog, family_inventory, family_cosmetic_equips, family_announcements, family_moderation_actions, family_rank_snapshots, family_voice_rooms, family_settings.
Unique constraints for membership and claims, FK and index plans, row-level security where Supabase is used, service-side transaction authority, private storage for sensitive data. PostgreSQL transaction isolation, optimistic versioning or locks on funds. Redis for cache/presence/rate limits, not authoritative money. LiveKit tokens minted server-side only. If backend absent, implement interfaces and clearly labeled local demo mode, not fake production APIs.

API CONTRACTS (adapt to existing stack):
GET /families?query=&cursor=
POST /families
GET /families/{id}
POST /families/{id}/applications
POST /families/{id}/invites
POST /families/{id}/invites/{token}/accept
GET /families/{id}/members
PATCH /families/{id}/members/{uid}/role
POST /families/{id}/members/{uid}/remove
POST /families/{id}/leave
GET /families/{id}/quests
POST /families/{id}/quests/{questId}/claim
GET /families/{id}/ledger
POST /families/{id}/donations
POST /families/{id}/upgrade
GET /families/{id}/chests
POST /families/{id}/chests/{chestId}/claim
POST /families/{id}/daily-reward/claim
GET /families/{id}/shop
POST /families/{id}/shop/{itemId}/purchase
GET /families/{id}/activity
GET /families/{id}/rankings
PATCH /families/{id}/settings
POST /families/{id}/voice-token
All authenticated; permission check; validation; pagination; rate limits; idempotency for mutations. Do not blindly implement new endpoints when existing routes cover these.

COMPOSE IMPLEMENTATION:
Use existing Navigation Compose, ViewModel, StateFlow, repository, DI, image loader, design tokens and localizations. Screen components: FamilyHero, FamilyTabRow, ActivityProgress, WeeklyChestCard, TreasuryCard, FamilyQuestList, MemberRoster, ActivityTimeline, ManagementPanel, FamilyChat, FamilyShop, RewardModal, LevelUpgradeSheet, RoleBadge, FamilyRankCard. Smooth 18–24dp corners, white cards, pastel accents, no black background. Android back and five bottom tabs remain functional; dialogs close before leaving; scroll state preserved; no pointer overlay blocking navigation. LazyColumn stable keys, paging, offline/error/retry, accessibility and 48dp targets. Animate small progress/level/chest transitions with Compose, respect reduced motion and low-end devices.

ASSETS:
Import supplied 68 original static WebP files from `android_assets/drawable-nodpi/` into existing app `src/main/res/drawable-nodpi/`. Names `family_ic_*`, `family_tier_*`, `family_bg_*`. Do not copy WePlay artwork. These are vector-rendered icons, not Lottie or 3D animations. Use actual existing user avatars.

QA / ACCEPTANCE:
- Guest/login guard; browse, create, apply, invite, approve, reject, join, leave, transfer.
- Role-based access: member cannot approve or upgrade, deputy/admin only allowed privileges, blocked member cannot rejoin.
- Simultaneous donations and upgrades cannot overdraft funds; double-click claim/donate never double-credits.
- Quest progress verified server-side, weekly resets, timezone boundaries, daily reward one per day.
- Chest expiry and cosmetics equip/unequip, no random paid rewards in MVP.
- Chat permissions, reporting, blocking, notification routing, voice join/reconnect and room lifecycle.
- Rankings accurate, no fabricated activity.
- Offline, empty, error, slow network, Android process death, rotation, screen readers and small screens.
- Navigation: Me -> Family -> Chat -> Back -> Family -> Games -> Discover -> Family -> Back; no blocked tabs.
- Run real Gradle assembleDebug, available unit/UI tests, lint. Report actual execution evidence, not claimed success.

DELIVERABLES:
`docs/FAMILY_VIDEO_FEATURE_MATRIX.md` — each observed control, timestamp, actual route, status.
`docs/FAMILY_PERMISSIONS_MATRIX.md` — role-by-action.
`docs/FAMILY_ECONOMY_SPEC.md` — personal vs shared currency, ledger, progression, quests, caps, reward eligibility, provisional values.
`docs/FAMILY_API_AND_SCHEMA.md` — migrations, indexes, RLS/security, endpoints.
`docs/FAMILY_IMPLEMENTATION_REPORT.md` — actual files changed, feature completion, blockers, build/test output.
`docs/FAMILY_QA_MATRIX.md` — pass/fail/not-tested evidence.
Implement incrementally: audit -> core Family and membership -> quests/progression -> economy/chests -> chat/voice -> shop/ranking -> polish/test. Do not stop at a plan. Do not push to GitHub automatically.
