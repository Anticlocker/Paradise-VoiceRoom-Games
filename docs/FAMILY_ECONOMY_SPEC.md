# FAMILY SYSTEM — ECONOMY & PROGRESSION SPECIFICATION

**Date:** 2026-11-21  
**Project:** Social Gaming Android App (`com.sundram.weplay`)  
**Scope:** Dual-Currency Architecture, Treasury, Level Rules, and Reward Distribution  

---

## 1. Dual-Currency Architecture

The Family System cleanly separates shared guild progression from personal member rewards:

1. **Family Funds (`family_ic_funds`):**
   - **Nature:** Shared virtual treasury resource belonging to the family collective.
   - **Sources:** Member coin donations, completed family community quests, and weekly chest collective rewards.
   - **Sinks:** Guild level upgrades, unlocking higher member capacities, activating family voice rooms, and purchasing permanent guild signs/emblems.
   - **Invariants:** Funds can never be negative; all debits and credits occur within ACID database transactions.

2. **Family Coins (`family_ic_coins`):**
   - **Nature:** Personal non-cash virtual token earned by individual members.
   - **Sources:** Completing daily/weekly quests, treasury donation rebate (20% cashback in Family Coins), claiming weekly chests, and daily free gift box.
   - **Sinks:** Family shop cosmetics (avatar frames, custom family signs, entrance effects, exclusive gift items).
   - **Invariants:** Non-transferable between users; no cash-out or peer-to-peer trading.

---

## 2. Level Progression & Tier Rules

| Level | Tier | Badge Asset | Min Activeness | Funds to Upgrade | Max Members | Perks & Unlocks |
|:---:|:---:|:---:|:---:|:---:|:---:|---|
| **1** | Bronze | `family_tier_bronze` | 0 | 500 | 25 | Guild Chat, Daily Quests, Normal Chest |
| **2** | Bronze | `family_tier_bronze` | 500 | 1,200 | 30 | Advanced Chest, Shop Tier 1, Custom Signs |
| **3** | Silver | `family_tier_silver` | 1,500 | 2,500 | 35 | Silver Frame, Weekly Quests |
| **4** | Silver | `family_tier_silver` | 3,000 | 4,500 | 40 | Family Voice Room Unlock, Rare Chest |
| **5** | Gold | `family_tier_gold` | 5,000 | 7,000 | 45 | Gold Frame, Entrance Shimmer Effect |
| **6** | Gold | `family_tier_gold` | 8,000 | 10,000 | 50 | Super Chest, Shop Tier 2 |
| **7** | Platinum | `family_tier_platinum` | 12,000 | 15,000 | 55 | Platinum Halo Frame, Guild Badge Pin |
| **8** | Platinum | `family_tier_platinum` | 18,000 | 22,000 | 60 | Stage Banner Priority, VIP Room Host |
| **9** | Diamond | `family_tier_diamond` | 26,000 | 32,000 | 70 | Diamond Crown, Custom Family Room Wall |
| **10** | Legend | `family_tier_legend` | 40,000 | MAX | 80 | Legend Guild Crest, App-wide Recognition |

---

## 3. Weekly Chest Progression

Weekly chest rewards unlock collectively when the family's cumulative activeness crosses thresholds:

- **Normal Chest (`family_ic_chest_normal`):** Threshold = 200 pts $\rightarrow$ +50 Family Coins, +100 Funds.
- **Advanced Chest (`family_ic_chest_advanced`):** Threshold = 500 pts $\rightarrow$ +100 Family Coins, +250 Funds.
- **Rare Chest (`family_ic_chest_rare`):** Threshold = 1,000 pts $\rightarrow$ +250 Family Coins, +600 Funds.
- **Super Chest (`family_ic_chest_super`):** Threshold = 2,000 pts $\rightarrow$ +600 Family Coins, +1,500 Funds.

*All claims are idempotent per user per week.*

---

## 4. Policy on Paid Gacha & Fund-Stealing

- **No Paid Gacha:** The reference daily reward machine is implemented strictly as a **free daily gift box** (one claim per day per member), transparently granting 30–80 Family Coins. No real money or diamond rolls are permitted in the MVP.
- **No Competitive Fund-Stealing:** Steal/barrier rivalry mechanics observed in references are disabled by default to prevent harassment, player frustration, and account abuse.
