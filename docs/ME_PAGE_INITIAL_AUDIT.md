# PROJECT ME PAGE 2.0 — INITIAL AUDIT REPORT

**Date:** 2026-11-21  
**Project:** Social Gaming Android App (`com.sundram.weplay`)  
**Scope:** Screen 5 (Me / Profile Tab) and Connected Subsystems  
**Brand Status:** Neutral (`APP_NAME` / Configurable App Label)

---

## 1. Executive Summary

This audit assesses the current state of the application's fifth primary tab (**Me / Profile**) in comparison to the desired target architecture inspired by reference social gaming hubs while strictly preserving our existing richer functionality and native Kotlin + Jetpack Compose conventions.

### Current Screen State:
- **Visuals:** Uses a dark cyan gradient top banner (`CyanBrand.copy(alpha = 0.16f)` into `PageWhite`), simple letter avatar, text-heavy profile row with age and XP bar.
- **Color Palette Disconnect:** While the rest of the application (Games/Home, Game Rooms, Create Room, Voice Lounge) uses a bright, premium White/Lavender/Cyan/Gold identity, the Me page has an outdated layout with stark white card containers on basic gray, harsh dividers, and lack of visual warmth.
- **Functionality Status:** The existing page contains robust functional routing:
  - `Routes.TOPUP` (Top up coins with packages and history)
  - `Routes.PROFILE_EDIT` (Full profile editing with name, bio, gender, birthday, avatar picker)
  - `Routes.GIFTS` (My Gifts collection and history)
  - `Routes.FAMILY` (Family hub, members, quests)
  - `Routes.BACKPACK` (Frames, bubbles, badges, inventory)
  - `Routes.SETTINGS` (Account, privacy, notifications, voice, general, support)
  - `Routes.ABOUT` (Version and policies)
  - `Log out` dialog with session invalidation
- **Missing / Partial Elements compared to Reference & Nova Pack:**
  - Lacks the four high-converting quick-access tiles: **VIP Center, Shop, Avatar Studio, My Space**.
  - Lacks dedicated, accessible entry points for **Moments, Stats, Visitors, Invite Friends, Badge Center, Contributions, Language, Parental Controls, Help Center, Security Center**.
  - Badge & Gift Showcases use generic emojis instead of our vector-rendered illustrated badge assets (`me_nova_badge_*`).
  - No one-tap copyable user ID action with clipboard feedback.
  - Social stats (Friends, Following, Followers) are hardcoded text without sub-page routing or sheet inspectors.

---

## 2. Complete Inventory of Current & Required Controls

| # | Control / Action | Current Route / Callback | Current State | Target State in Me Page 2.0 | Status |
|---|---|---|---|---|---|
| 1 | **Profile Avatar** | Opens full-size or edit | Renders with `UserAvatar` | Renders avatar with frame, tap opens Avatar Studio or Photo Inspector | `REAL` |
| 2 | **User ID** | Static Text | No copy action | Tap copies ID to clipboard with toast feedback | `ENHANCE` |
| 3 | **Verified Indicator** | Static Icon | Shown unconditionally | Show verified badge ONLY when `profile.isVerified` or server-verified | `AUDIT / FIX` |
| 4 | **Level & XP Progress Bar** | `LinearProgressIndicator` | Hardcoded 650/1000 | Dynamic display tied to `profile.level` and real XP progress | `ENHANCE` |
| 5 | **Edit Profile Chevron** | N/A | None in header | Top-right header icon/chevron navigating to `Routes.PROFILE_EDIT` | `NEW` |
| 6 | **Share Profile** | N/A | Missing | Share sheet via Android Intent (`ACTION_SEND`) with user profile ID | `NEW` |
| 7 | **Friends Count** | Navigates to `Routes.MSG` | Hardcoded "24" | Opens Friends List Sheet / Navigates to Messages/Friends | `REAL / DEMO` |
| 8 | **Following Count** | Navigates to `Routes.DISCOVER` | Hardcoded "142" | Opens Following List Sheet | `REAL / DEMO` |
| 9 | **Followers Count** | Navigates to `Routes.RANKING` | Hardcoded "380" | Opens Followers List Sheet | `REAL / DEMO` |
| 10 | **Coin Balance** | Navigates to `Routes.TOPUP` | `profile.coins` | Compact wallet chip with `me_nova_ic_wallet`, real coin balance | `REAL` |
| 11 | **VIP Center Tile** | None (in `MeSettingsScreen` only) | Missing on main Me | Quick-access tile `me_nova_ic_vip`, opens VIP Center Modal/Sheet with tier privileges | `NEW` |
| 12 | **Shop Tile** | None (in `MeSettingsScreen` only) | Missing on main Me | Quick-access tile `me_nova_ic_shop`, opens Cosmetics Shop Modal/Sheet | `NEW` |
| 13 | **Avatar Studio Tile** | None | Missing on main Me | Quick-access tile `me_nova_ic_avatar_studio`, opens Avatar Studio sheet with presets & frames | `NEW` |
| 14 | **My Space / Home Tile**| None | Missing on main Me | Quick-access tile `me_nova_ic_my_space`, opens My Space sheet | `NEW` |
| 15 | **Badge Showcase** | Horizontal emoji list | Navigates to Backpack | Render with `me_nova_badge_*` assets; tap opens Badge Details | `ENHANCE` |
| 16 | **Gift Showcase** | Horizontal emoji list | Navigates to Gifts | Render with high-contrast gift badges and real counts | `ENHANCE` |
| 17 | **Moments Row** | None | Missing on main Me | Navigates to `Routes.DISCOVER` (Moments feed) | `REAL` |
| 18 | **Stats Row** | None | Missing on main Me | Opens Game & Voice Stats sheet with real/honest session metrics | `NEW` |
| 19 | **Visitors Row** | None | Missing on main Me | Opens Visitors sheet with privacy toggle and visit logs | `NEW` |
| 20 | **Invite Friends Row** | None | Missing on main Me | Triggers Android ShareSheet with app invite deep link | `NEW` |
| 21 | **Contributions Row** | None | Missing on main Me | Opens Family/Room Contributions sheet with leaderboard info | `NEW` |
| 22 | **Family Hub Row** | Navigates to `Routes.FAMILY` | Functional | Preserved with `me_nova_ic_family` icon | `REAL` |
| 23 | **Top Up Coins Row** | Navigates to `Routes.TOPUP` | Functional | Preserved with `me_nova_ic_topup` icon | `REAL` |
| 24 | **My Gifts Row** | Navigates to `Routes.GIFTS` | Functional | Preserved with `me_nova_ic_gifts` icon | `REAL` |
| 25 | **Backpack Row** | Navigates to `Routes.BACKPACK` | Functional | Preserved with `me_nova_ic_backpack` icon | `REAL` |
| 26 | **Edit Profile Row** | Navigates to `Routes.PROFILE_EDIT` | Functional | Preserved with `me_nova_ic_edit_profile` icon | `REAL` |
| 27 | **Language Row** | None | Missing on main Me | Opens Language Selector sheet, updates locale preference | `NEW` |
| 28 | **Parental Controls Row**| None | Missing on main Me | Opens Parental Control Mode sheet with PIN and restriction toggles | `NEW` |
| 29 | **Help Center Row** | Navigates to Support | Missing directly on Me | Navigates to `Routes.SUPPORT_SETTINGS` via `me_nova_ic_help` | `REAL` |
| 30 | **Security Center Row**| Navigates to Account | Missing directly on Me | Navigates to `Routes.ACCOUNT_SETTINGS` via `me_nova_ic_security` | `REAL` |
| 31 | **Settings Row** | Navigates to `Routes.SETTINGS` | Functional | Preserved with `me_nova_ic_settings` icon | `REAL` |
| 32 | **Log Out Row** | Shows confirmation dialog | Functional | Invalidate tokens, clear session, navigate to Auth | `REAL` |
| 33 | **About Row** | Navigates to `Routes.ABOUT` | Functional | Preserved with `me_nova_ic_about` icon | `REAL` |
| 34 | **Gift Pack Widget** | Floating / Action | Maintained | Respects 48h rotation, opens Gift Pack bottom sheet | `REAL` |

---

## 3. Visual & Thematic Architecture

### Color Palette:
- **Canvas / Background:** `#FAF9FF` (pearl white)
- **Surfaces / Cards:** `#FFFFFF` (card white)
- **Subtle Lavender:** `#EFE8FF` / `#F3EFFF`
- **Primary Brand Ink:** `#29263E` (deep readable text)
- **Secondary Text:** `#7F7B93` (accessible subtitle)
- **Cyan Accent:** `#35CCD6` (vibrant brand highlight)
- **Royal Violet:** `#7656D9` (primary action & active states)
- **Soft Gold:** `#F5BD6C` (VIP, coin, and star accents)
- **Border / Divider:** `#ECE8FA` (subtle hairline dividers)

### Hero Structure:
- Background: Soft aurora gradient with gentle curved lower boundary (`me_nova_bg_aurora` or `me_nova_bg_pearl`).
- Profile picture: Round 76dp avatar with decorative frame, camera/badge overlay, level badge.
- Identity: Real name, verified check (when true), copyable Account ID with toast, Age / Gender pill.
- Action: Top-right icon group: Share Profile icon + Settings/Edit shortcut.

### Bottom Navigation Contract:
- Preserved via `WePlayBottomBar` and scaffold insets (`pad`).
- 5 Tabs: Games, Voice Room, Chats, Discover, Me.
- Me is actively selected; tab switches correctly to other main screens.

---

## 4. Remediation & Implementation Steps

1. **Milestone 1 — Asset Setup & Base UI Structure:**
   - 65 WebP assets imported into `app/src/main/res/drawable-nodpi/` (Complete).
   - Rebuild `MeScreen.kt` with the new light `#FAF9FF` theme, aurora hero, copyable ID, level progress bar, and 4 quick-access tiles (VIP, Shop, Avatar Studio, My Space).
2. **Milestone 2 — Showcases & Social Stats:**
   - Implement illustrated Badge Showcase with `me_nova_badge_*` assets.
   - Implement Gift Showcase with real counts and `Routes.GIFTS` navigation.
   - Implement Social Metrics strip (Friends, Following, Followers).
3. **Milestone 3 — Action Groups & New Sheets:**
   - Group 1: Activity & Social (Moments, Stats, Visitors, Invite Friends, Contributions, Family Hub).
   - Group 2: Wallet & Collection (Top Up, My Gifts, Backpack).
   - Group 3: Account & Safety (Edit Profile, Language, Parental Controls, Help Center, Security Center, Settings, About, Log Out).
   - Implement interactive bottom sheets / dialogs for:
     - VIP Center Sheet
     - Cosmetics Shop Sheet
     - Avatar Studio Sheet (with 8 original avatar presets from `me_nova_avatar_01..08` and photo picker)
     - My Space / Home Sheet
     - Game & Voice Stats Sheet
     - Visitors Sheet
     - Language Selection Sheet
     - Parental Controls Sheet
4. **Milestone 4 — Tests & Verification:**
   - Create unit tests in `MePageUnitTest.kt` covering profile data binding, badge showcase, XP calculation, quick tiles, and setting actions.
   - Run `testDebugUnitTest` and `assembleDebug`.
   - Generate `docs/ME_PAGE_FUNCTIONALITY_MATRIX.md` and `docs/ME_PAGE_IMPLEMENTATION_REPORT.md`.
