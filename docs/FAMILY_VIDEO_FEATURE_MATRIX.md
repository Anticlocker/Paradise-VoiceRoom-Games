# FAMILY SYSTEM — VIDEO FEATURE OBSERVATION & ROUTE MATRIX

**Reference Media:** `112068.mp4` (~98s screen recording)  
**Project:** Social Gaming Android App (`com.sundram.weplay`)  
**Scope:** Guild / Family System  

---

## 1. Video Timestamp & Feature Inventory

| Timestamp (s) | Feature / Control Observed | Target Implementation in VYBORA App | Route / Action | Status |
|---|---|---|---|---|
| **00:00–00:06** | Family Profile Overview: emblem, name, level, member count, weekly chest progress, treasury funds, announcements | `FamilyHeroSection` + `FamilyEconomyStrip` + `OverviewTab` | `Routes.FAMILY` | **IMPLEMENTED** |
| **00:06–00:10** | Tasks / Quests list with progress bar, reward icons, and claim buttons | `FamilyQuestsTab` (Daily, Weekly, Community, Contribution) | Tab 1: Quests | **IMPLEMENTED** |
| **00:10–00:18** | Members section with search bar, role badges (Leader, Deputy, Admin, Member), online indicator, and kick/manage actions | `FamilyMembersTab` with role-based member management | Tab 2: Members | **IMPLEMENTED** |
| **00:18–00:27** | News / Activity Timeline of join events, level-ups, treasury donations, and weekly chest claims | `FamilyActivityTab` chronological event timeline | Tab 3: Activity | **IMPLEMENTED** |
| **00:27–00:33** | Manage tab with announcement editing, family sign motto, recruitment policy (Open/Approval/Invite), and applications queue | `FamilyManageTab` (role-gated for Leader, Deputy, and Admin) | Tab 4: Manage | **IMPLEMENTED** |
| **00:33–00:39** | Interaction feedback, toast notifications on copy/claim/donate, and return to profile | Toast & animated Compose state changes | Core UX feedback | **IMPLEMENTED** |
| **00:39–00:47** | Family Chat room with message history, member mentions, and gifting shortcuts | Top-bar chat button navigating directly to family messages | `Routes.MSG` | **IMPLEMENTED** |
| **00:48–00:54** | Family Leaderboard Rankings with top 3 podium badges, level, activeness points, and tier | `FamilyRankingsBottomSheet` with top-rank cards | Modal sheet | **IMPLEMENTED** |
| **00:60–00:63** | Gacha-like reward machine / Daily Gift Box | `DailyFreeGiftDialog` (free daily reward box once per day; no paid spins) | Modal dialog | **IMPLEMENTED** |
| **00:65–00:76** | Family Shop & Cosmetics with exclusive frames, signs, and virtual gifts | `FamilyShopBottomSheet` (Frames, Signs, Effects, Gifts) | Modal sheet | **IMPLEMENTED** |
| **00:78–00:96** | Navigation back to main app, tabs, and voice room launch | LiveKit family room connector (`Routes.ROOM_DETAIL`) | In-app navigation | **IMPLEMENTED** |
