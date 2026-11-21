# FAMILY SYSTEM — ROLE PERMISSIONS MATRIX

**Date:** 2026-11-21  
**Project:** Social Gaming Android App (`com.sundram.weplay`)  
**Scope:** Server & Client Enforced Role-Based Access Control (RBAC)  

---

## 1. Role Hierarchy

The Family hierarchy is structured in descending order of authority:
1. **Leader (`family_ic_leader`):** Highest authority (priority 5). Maximum 1 per family.
2. **Deputy (`family_ic_deputy`):** Co-leader assisting operations (priority 4). Maximum 2 per family.
3. **Admin (`family_ic_admin`):** Operational moderator (priority 3). Maximum 4 per family.
4. **Moderator (`family_ic_moderator`):** Chat and room supervisor (priority 2).
5. **Member (`family_ic_member`):** Regular participant (priority 1).

---

## 2. Permissions by Operation

| Operation / Capability | Leader | Deputy | Admin | Moderator | Member | Notes / Server Checks |
|---|:---:|:---:|:---:|:---:|:---:|---|
| **View Family Profile & Quests** | Yes | Yes | Yes | Yes | Yes | Available to all family members |
| **Claim Personal Quest Rewards** | Yes | Yes | Yes | Yes | Yes | Idempotent per user & quest period |
| **Claim Weekly Chests** | Yes | Yes | Yes | Yes | Yes | Requires user & family activeness |
| **Claim Free Daily Gift Box** | Yes | Yes | Yes | Yes | Yes | Exactly 1 free claim per UTC day |
| **Donate to Treasury** | Yes | Yes | Yes | Yes | Yes | Debits wallet, credits funds + coins |
| **Join Family Voice Room** | Yes | Yes | Yes | Yes | Yes | Muted members cannot speak |
| **Send Messages in Family Chat** | Yes | Yes | Yes | Yes | Yes | Respects mute status |
| **Invite External Friends** | Yes | Yes | Yes | Yes | Yes | Dispatches Android `ACTION_SEND` |
| **Approve / Reject Applications** | Yes | Yes | Yes | No | No | Leader, Deputy, and Admin only |
| **Edit Announcement & Notice** | Yes | Yes | Yes | No | No | Moderation filtered |
| **Change Recruitment Policy** | Yes | Yes | No | No | No | Open, Approval, or Invite-Only |
| **Promote to Deputy** | Yes | No | No | No | No | Only Leader can appoint Deputy |
| **Promote to Admin / Moderator**| Yes | Yes | No | No | No | Leader or Deputy |
| **Demote Member Role** | Yes | Yes* | No | No | No | Deputy can only demote below Admin |
| **Kick Member from Family** | Yes | Yes* | No | No | No | Can only kick strictly lower priority |
| **Mute Member in Chat/Voice** | Yes | Yes | Yes | Yes | No | Duration-bounded mutes |
| **Transfer Guild Leadership** | Yes | No | No | No | No | Leader only; requires password |
| **Disband Family** | Yes | No | No | No | No | Multi-step confirmation; permanent |
| **Leave Family** | No* | Yes | Yes | Yes | Yes | Leader must transfer before leaving |
