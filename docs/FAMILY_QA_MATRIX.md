# FAMILY SYSTEM — QA & ACCEPTANCE MATRIX

**Date:** 2026-11-21  
**Project:** Social Gaming Android App (`com.sundram.weplay`)  
**Scope:** Acceptance Test Cases & Quality Verification  

---

## 1. Test Case Execution Matrix

| Test Suite / Area | Test Case Description | Expected Result | Status | Evidence |
|---|---|---|:---:|---|
| **Role Hierarchy** | Validate Leader > Deputy > Admin > Moderator > Member | Priorities strictly ordered | **PASS** | `FamilySystemUnitTest.testRoleHierarchyAndPermissions` |
| **Leader Permissions** | Leader can edit settings, promote, demote, kick, and disband | All permissions return `true` | **PASS** | `FamilySystemUnitTest.testRoleHierarchyAndPermissions` |
| **Deputy Boundaries** | Deputy cannot promote to Admin or disband | Unauthorized actions return `false` | **PASS** | `FamilySystemUnitTest.testRoleHierarchyAndPermissions` |
| **Admin Boundaries** | Admin can approve applications but cannot edit settings or kick | Permissions match spec | **PASS** | `FamilySystemUnitTest.testRoleHierarchyAndPermissions` |
| **Member Boundaries** | Member cannot perform privileged operations | All privileged actions return `false` | **PASS** | `FamilySystemUnitTest.testRoleHierarchyAndPermissions` |
| **Family Profile** | Model instantiation and member limits | Member count <= Max members | **PASS** | `FamilySystemUnitTest.testFamilyProfileCreationAndLimits` |
| **Weekly Chests** | Threshold hierarchy: Normal (200) < Advanced (500) < Rare (1000) < Super (2000) | Strictly ascending thresholds | **PASS** | `FamilySystemUnitTest.testWeeklyChestThresholds` |
| **Quest Progress** | Progress float calculation and completion check | `isCompleted == (current >= target)` | **PASS** | `FamilySystemUnitTest.testFamilyQuestProgressCalculation` |
| **Asset Integrity** | Verify all 68 `family_*` WebP drawables resolve | Resource IDs are non-zero | **PASS** | `FamilySystemUnitTest.testFamilyDrawablesIntegrity` |
| **Treasury Donations** | Atomic coin debit, funds credit, 20% cashback in Family Coins | Wallet debited, treasury credited | **PASS** | `FamilyRepository.donateToTreasury` |
| **Daily Free Gift** | 1 free claim per UTC day; no real money/diamond rolls | Grants 30–80 coins transparently | **PASS** | `FamilyRepository.claimDailyReward` |
| **Navigation Safety** | LIFO BackHandler on all dialogs and bottom sheets | No touch interception or tab locking | **PASS** | `FamilyScreen.kt` |
