# FAMILY SYSTEM — BACKEND SCHEMA & API SPECIFICATION

**Date:** 2026-11-21  
**Project:** Social Gaming Android App (`com.sundram.weplay`)  
**Scope:** PostgreSQL Database Migration & REST API Endpoints  

---

## 1. Database Schema (`server/db/migrations/003_family.sql`)

### Tables Implemented:
1. `families` — Core entity storing ID, name, tag, sign, description, level, tier, funds, activeness, capacity, leader, and settings.
2. `family_members` — Member association table with unique constraint enforcing 1 family per user, tracking role, joined date, activeness, and mute status.
3. `family_quests` — Catalog of Daily, Weekly, Community, and Contribution quests.
4. `family_quest_progress` — User progress per quest period with unique constraint preventing double claim.
5. `family_donations` — Immutable treasury donation ledger recording coin contributions and funds credited.
6. `family_chests` — Weekly chest claim records with `(family_id, user_id, chest_tier, week_key)` unique constraint.
7. `family_activity_events` — Chronological timeline of guild milestones (joins, promotions, donations, level-ups).
8. `family_join_requests` — Applicant queue for families requiring leader/admin approval.

---

## 2. API Endpoint Contracts (`server/social/family.mjs`)

| HTTP Method | Path | Description | Authorization |
|---|---|---|---|
| `GET` | `/api/families?query=&limit=` | Browse and search recommended families | Authenticated User |
| `GET` | `/api/families/:id` | Get comprehensive family details and stats | Authenticated User |
| `POST` | `/api/families` | Create a new family with name, tag, and motto | Authenticated User |
| `POST` | `/api/families/:id/donate` | Donate wallet coins to treasury (atomic) | Family Member |
| `POST` | `/api/families/:id/chests/claim` | Claim weekly chest based on activeness threshold | Family Member |
| `POST` | `/api/families/:id/quests/:qid/claim` | Claim completed quest reward (idempotent) | Family Member |
| `POST` | `/api/families/:id/daily-reward` | Claim free daily guild gift box | Family Member |
| `GET` | `/api/families/:id/members` | Get paginated member list with roles and activeness | Family Member |
| `PATCH` | `/api/families/:id/members/:uid/role` | Promote/demote member role | Leader / Deputy |
| `POST` | `/api/families/:id/members/:uid/kick` | Remove member from family | Role Hierarchy |
| `POST` | `/api/families/:id/leave` | Leave current family | Member / Deputy / Admin |
| `GET` | `/api/families/:id/activity` | Get chronological family activity timeline | Family Member |
| `GET` | `/api/families/rankings` | Get weekly guild leaderboard | Authenticated User |
| `PATCH` | `/api/families/:id/settings` | Update announcement, sign, or policy | Leader / Deputy |
