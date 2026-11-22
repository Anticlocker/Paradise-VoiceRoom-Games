# LEADERBOARD & ANIMATED PROFILE FRAMES — FUNCTION MATRIX

**Date:** November 22, 2026  
**Auditor:** Principal Android Architect & QA Lead  
**Scope:** Full Leaderboard Navigation, Multi-Category Filters, Top-3 Podiums, Paginated Rank List, Sticky Current-User Footer, Animated Profile Frame Canvas, and Cosmetics Wardrobe Sheet.

---

## 1. Functional Controls & Interaction Matrix

| Screen Area | Control / Widget | Action / Gesture | Data Source | Navigation Destination / Effect | Status | Test Evidence |
|---|---|---|---|---|---|---|
| **Top App Bar** | Back Icon (`ic_back`) | Single Tap | Navigation Stack | Pops back to originating screen (`GameLobby`, `VoiceRoom`, `Tasks`) | Verified | Unit & Nav test |
| **Top App Bar** | Leaderboard Title | Static Header | Localized Resource | Displays screen title & subtitle | Verified | Layout inspection |
| **Top App Bar** | Frame Wardrobe Button | Single Tap | `repository.catalog` | Opens ModalBottomSheet with live avatar preview & frame shop | Verified | Compose StateFlow test |
| **Top App Bar** | Rules Button (`?`) | Single Tap | `repository.getRankingRules()` | Displays Category Scoring, Reset Schedule & Anti-Cheat Dialog | Verified | `testRankingRuleIntegrity` |
| **Category Tabs** | Popularity Tab | Tap | `LeaderboardCategory.POPULARITY` | Filters by Charm points; switches palette to warm rose (`#FFDBEA`) | Verified | `testLeaderboardCategoriesAndRouting` |
| **Category Tabs** | VIP Tab | Tap | `LeaderboardCategory.VIP` | Filters by VIP points; switches palette to gold (`#FFF0C4`) | Verified | `testLeaderboardCategoriesAndRouting` |
| **Category Tabs** | Couple Tab | Tap | `LeaderboardCategory.COUPLE` | Filters by intimacy points; switches palette to magenta (`#F9DAFF`) | Verified | `testLeaderboardCategoriesAndRouting` |
| **Category Tabs** | Room Tab | Tap | `LeaderboardCategory.ROOM` | Filters by room heat; switches palette to cyan (`#D9F1FF`) | Verified | `testLeaderboardCategoriesAndRouting` |
| **Category Tabs** | BFF Tab | Tap | `LeaderboardCategory.BFF` | Filters by friendship bond; switches palette to violet (`#E8DDFF`) | Verified | `testLeaderboardCategoriesAndRouting` |
| **Category Tabs** | Family Tab | Tap | `LeaderboardCategory.FAMILY` | Filters by guild activeness; switches palette to emerald (`#D8FFF2`) | Verified | `testLeaderboardCategoriesAndRouting` |
| **Period Chips** | Today / Yesterday / Weekly / Total / etc. | Tap Chip | Category's `supportedPeriods` | Aggregates period scores with deterministic multipliers | Verified | `testLeaderboardCategoriesAndRouting` |
| **Top-3 Podium** | Rank 1 Pedestal (Center) | Tap Avatar/Card | Top entry (`rank == 1`) | Navigates to `Routes.userDetailRoute(id)`; displays gold pedestal, champion crown & celestial/mythic frame | Verified | Compose Preview & Unit test |
| **Top-3 Podium** | Rank 2 Pedestal (Left) | Tap Avatar/Card | 2nd entry (`rank == 2`) | Navigates to user profile; displays silver pedestal & legendary frame | Verified | Compose Preview & Unit test |
| **Top-3 Podium** | Rank 3 Pedestal (Right) | Tap Avatar/Card | 3rd entry (`rank == 3`) | Navigates to user profile; displays bronze pedestal & epic frame | Verified | Compose Preview & Unit test |
| **Rank 4+ List** | Rank Row Item | Tap Row | `LazyColumn` items 4..N | Navigates to user detail; displays rank badge, framed avatar, level pill, verified check & score | Verified | LazyColumn scroll test |
| **Sticky Footer** | Current User Badge | Display | `CurrentUserRank.rank` | Shows exact standing (e.g. `"#14"` or `"--"` if unranked) | Verified | `testLeaderboardCategoriesAndRouting` |
| **Sticky Footer** | User Avatar with Frame | Display | `UserProfile.frame` & repo | Renders user's currently equipped frame with real-time sync | Verified | `testFrameEquipAndUnequipWorkflow` |
| **Sticky Footer** | Category CTA Button | Single Tap | `category.ctaRoute` | Popularity $\rightarrow$ Hub; VIP $\rightarrow$ TopUp; Couple $\rightarrow$ Room; Room $\rightarrow$ Room; BFF $\rightarrow$ Chats; Family $\rightarrow$ Family Hub | Verified | `testLeaderboardCategoriesAndRouting` |
| **Wardrobe Sheet** | Live Avatar Preview | Interactive Preview | Selected catalog frame | Displays real user photo with animated frame effects | Verified | Canvas animation test |
| **Wardrobe Sheet** | Equip Frame Button | Tap | `repository.equipFrame()` | Equips unlocked frame, persists to `UserProfile.frame`, updates UI | Verified | `testFrameEquipAndUnequipWorkflow` |
| **Wardrobe Sheet** | Unequip Frame Button | Tap | `repository.unequipFrame()` | Clears equipped frame to default | Verified | `testFrameEquipAndUnequipWorkflow` |
| **Wardrobe Sheet** | Purchase Frame Button | Tap | `repository.purchaseFrame()` | Validates coin balance, deducts coins, grants entitlement, auto-equips | Verified | `testShopPurchaseFlowAndCoinBalance` |
| **Wardrobe Sheet** | Locked Indicator | Display | Level / Event requirement | Shows locked state with clear unlock instruction (e.g. `Lv. 5`) | Verified | `testCatalogCompletenessAndEligibilityRules` |
| **Rules Dialog** | Got It Button | Tap | Dialog State | Closes modal dialog | Verified | Compose State test |
