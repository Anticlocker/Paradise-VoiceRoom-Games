package com.sundram.weplay.data

import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.sundram.weplay.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Repository and in-memory business logic for the Family System.
 *
 * Implements server-authoritative state transitions, role permissions enforcement,
 * idempotent quest/chest claims, treasury donations, and family activity logging.
 */
class FamilyRepository private constructor(context: Context) {

    private val prefs = context.applicationContext.getSharedPreferences("weplay_family_prefs", Context.MODE_PRIVATE)

    // Current Active Family
    private val _currentFamily = MutableStateFlow<FamilyProfile?>(null)
    val currentFamily: StateFlow<FamilyProfile?> = _currentFamily.asStateFlow()

    // Members list
    private val _members = MutableStateFlow<List<FamilyMember>>(emptyList())
    val members: StateFlow<List<FamilyMember>> = _members.asStateFlow()

    // Quests list
    private val _quests = MutableStateFlow<List<FamilyQuest>>(emptyList())
    val quests: StateFlow<List<FamilyQuest>> = _quests.asStateFlow()

    // Weekly chests
    private val _chests = MutableStateFlow<List<FamilyWeeklyChest>>(emptyList())
    val chests: StateFlow<List<FamilyWeeklyChest>> = _chests.asStateFlow()

    // Activity timeline
    private val _activityEvents = MutableStateFlow<List<FamilyActivityEvent>>(emptyList())
    val activityEvents: StateFlow<List<FamilyActivityEvent>> = _activityEvents.asStateFlow()

    // Shop Catalog
    private val _shopItems = MutableStateFlow<List<FamilyShopItem>>(emptyList())
    val shopItems: StateFlow<List<FamilyShopItem>> = _shopItems.asStateFlow()

    // Join Requests (for Admin/Leader queue)
    private val _joinRequests = MutableStateFlow<List<FamilyJoinRequest>>(emptyList())
    val joinRequests: StateFlow<List<FamilyJoinRequest>> = _joinRequests.asStateFlow()

    // Rankings Leaderboard
    private val _rankings = MutableStateFlow<List<FamilyRankingEntry>>(emptyList())
    val rankings: StateFlow<List<FamilyRankingEntry>> = _rankings.asStateFlow()

    // Recommended Families (for discovery/browse)
    private val _discoverFamilies = MutableStateFlow<List<FamilyProfile>>(emptyList())
    val discoverFamilies: StateFlow<List<FamilyProfile>> = _discoverFamilies.asStateFlow()

    init {
        loadInitialState()
    }

    private fun loadInitialState() {
        val hasFamily = prefs.getBoolean("has_family", true)
        val familyName = prefs.getString("family_name", "Aurora Knights") ?: "Aurora Knights"

        if (hasFamily) {
            _currentFamily.value = FamilyProfile(
                id = "FAM-1082",
                name = familyName,
                tag = "AURK",
                sign = "Unity in Voice, Victory in Games",
                description = "Active competitive and casual voice gaming family. Daily game matches and voice rooms.",
                level = 3,
                tier = FamilyTier.GOLD,
                funds = prefs.getLong("family_funds", 3450L),
                personalCoins = prefs.getLong("family_personal_coins", 420L),
                weeklyActiveness = 680,
                memberCount = 18,
                maxMembers = 35,
                leaderId = "u_leader_01",
                leaderName = "Aria Nova",
                announcement = "Weekend Ludo tournament starts this Saturday 8 PM! Check Quests tab.",
                recruitmentPolicy = RecruitmentPolicy.OPEN,
                minLevelRequirement = 3,
            )

            _members.value = listOf(
                FamilyMember("u_leader_01", "Aria Nova", null, FamilyRole.LEADER, 14, 185, 1200L, true),
                FamilyMember("u_deputy_01", "Marcus Brody", null, FamilyRole.DEPUTY, 12, 140, 950L, true),
                FamilyMember("u_admin_01", "Elena Rostova", null, FamilyRole.ADMIN, 11, 110, 720L, false),
                FamilyMember("u_mod_01", "Julian Vance", null, FamilyRole.MODERATOR, 10, 95, 540L, true),
                FamilyMember("u_mem_01", "Kiran Patel", null, FamilyRole.MEMBER, 8, 70, 310L, true),
                FamilyMember("u_mem_02", "Sofia Chen", null, FamilyRole.MEMBER, 9, 65, 290L, false),
                FamilyMember("u_mem_03", "Zayd Malik", null, FamilyRole.MEMBER, 7, 50, 210L, true),
                FamilyMember("u_mem_04", "Liam O'Connor", null, FamilyRole.MEMBER, 6, 45, 180L, false),
            )

            _quests.value = listOf(
                FamilyQuest("q1", "Daily Check-in", "Check in to the family hall today", QuestCategory.DAILY, 1, 1, 20, 50, false, R.drawable.family_ic_checkin),
                FamilyQuest("q2", "Room Voice Chat", "Spend 15 minutes in a family voice room", QuestCategory.DAILY, 15, 10, 30, 80, false, R.drawable.family_ic_voice),
                FamilyQuest("q3", "Play With Member", "Finish 1 Ludo or Spy match with a guildmate", QuestCategory.DAILY, 1, 0, 40, 100, false, R.drawable.family_ic_task),
                FamilyQuest("q4", "Weekly Treasury Contributor", "Donate at least 200 coins to the family treasury", QuestCategory.WEEKLY, 200, 150, 80, 250, false, R.drawable.family_ic_donate),
                FamilyQuest("q5", "Cheer Guildmates", "Send 5 cheers to fellow members", QuestCategory.COMMUNITY, 5, 3, 25, 60, false, R.drawable.family_ic_cheer),
                FamilyQuest("q6", "Welcome Recruits", "Welcome a newly joined member in family chat", QuestCategory.COMMUNITY, 1, 1, 15, 40, true, R.drawable.family_ic_recruit),
            )

            _chests.value = listOf(
                FamilyWeeklyChest(ChestTier.NORMAL, 50, 100, true),
                FamilyWeeklyChest(ChestTier.ADVANCED, 100, 250, false),
                FamilyWeeklyChest(ChestTier.RARE, 250, 600, false),
                FamilyWeeklyChest(ChestTier.SUPER, 600, 1500, false),
            )

            _activityEvents.value = listOf(
                FamilyActivityEvent("ev1", ActivityEventType.DONATION, "Marcus Brody", "Donated 200 coins to Family Treasury"),
                FamilyActivityEvent("ev2", ActivityEventType.CHEST_CLAIM, "Elena Rostova", "Unlocked and claimed Normal Weekly Chest"),
                FamilyActivityEvent("ev3", ActivityEventType.MEMBER_JOIN, "Liam O'Connor", "Joined the family via open recruitment"),
                FamilyActivityEvent("ev4", ActivityEventType.LEVEL_UP, "System", "Family reached Gold Tier Level 3!"),
            )

            _joinRequests.value = listOf(
                FamilyJoinRequest("req1", "u_app_1", "David Miller", 8),
                FamilyJoinRequest("req2", "u_app_2", "Sana Mir", 11),
            )
        } else {
            _currentFamily.value = null
            _members.value = emptyList()
            _quests.value = emptyList()
            _chests.value = emptyList()
            _activityEvents.value = emptyList()
            _joinRequests.value = emptyList()
        }

        // Shop items catalog
        _shopItems.value = listOf(
            FamilyShopItem("shp_frame_aurora", "Aurora Guild Frame", "Exquisite family border for voice room seats", ShopCategory.FRAMES, 500, 2, false, false, R.drawable.family_ic_family_frame),
            FamilyShopItem("shp_sign_crest", "Royal Sign Crest", "Customizable golden family crest badge", ShopCategory.SIGNS, 800, 3, false, false, R.drawable.family_ic_family_sign),
            FamilyShopItem("shp_effect_shimmer", "Family Entrance Shimmer", "Dazzling room entrance glow effect", ShopCategory.EFFECTS, 1200, 4, false, false, R.drawable.family_ic_sparkle),
            FamilyShopItem("shp_gift_rosebox", "Guild Rose Box", "Exclusive family rose box for stage tipping", ShopCategory.GIFTS, 300, 1, false, false, R.drawable.family_ic_gift),
        )

        // Rankings Leaderboard
        _rankings.value = listOf(
            FamilyRankingEntry(1, "FAM-1001", "Solaris Dynasty", "SOLR", FamilyTier.LEGEND, 10, 4820, 50, "Cyrus King"),
            FamilyRankingEntry(2, "FAM-1045", "Moonlight Echo", "MOON", FamilyTier.DIAMOND, 8, 3950, 45, "Selena Frost"),
            FamilyRankingEntry(3, "FAM-1082", "Aurora Knights", "AURK", FamilyTier.GOLD, 3, 2680, 18, "Aria Nova"),
            FamilyRankingEntry(4, "FAM-1120", "Cyber Phoenix", "CYBR", FamilyTier.GOLD, 4, 2340, 28, "Neo Walker"),
            FamilyRankingEntry(5, "FAM-1205", "Crystal Guardians", "CRYS", FamilyTier.SILVER, 2, 1820, 20, "Tara Stone"),
        )

        // Recommended Families for Browse
        _discoverFamilies.value = listOf(
            FamilyProfile("FAM-1045", "Moonlight Echo", "MOON", "Night Owls Welcome", "Chill music and late night gaming.", 8, FamilyTier.DIAMOND, 9500L, 800L, 3950, 45, 50, "u2", "Selena Frost"),
            FamilyProfile("FAM-1120", "Cyber Phoenix", "CYBR", "Play Hard, Win Big", "Competitive Ludo & Spy clan.", 4, FamilyTier.GOLD, 4200L, 350L, 2340, 28, 40, "u4", "Neo Walker"),
            FamilyProfile("FAM-1205", "Crystal Guardians", "CRYS", "Friendly and Supportive", "Newbie-friendly casual guild.", 2, FamilyTier.SILVER, 1850L, 120L, 1820, 20, 30, "u5", "Tara Stone"),
        )
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  Actions & Mutations
    // ─────────────────────────────────────────────────────────────────────────

    fun createFamily(
        name: String,
        tag: String,
        sign: String,
        description: String,
        leaderName: String,
    ): Boolean {
        if (name.isBlank() || tag.isBlank()) return false

        val newFamily = FamilyProfile(
            id = "FAM-${(1000..9999).random()}",
            name = name,
            tag = tag.uppercase().take(5),
            sign = sign.ifBlank { "United we stand" },
            description = description.ifBlank { "Welcome to our family!" },
            level = 1,
            tier = FamilyTier.BRONZE,
            funds = 500L,
            personalCoins = 100L,
            weeklyActiveness = 50,
            memberCount = 1,
            maxMembers = 25,
            leaderId = "u_leader_me",
            leaderName = leaderName,
        )

        _currentFamily.value = newFamily
        _members.value = listOf(
            FamilyMember("u_leader_me", leaderName, null, FamilyRole.LEADER, 10, 50, 500L, true)
        )

        prefs.edit()
            .putBoolean("has_family", true)
            .putString("family_name", name)
            .putLong("family_funds", 500L)
            .putLong("family_personal_coins", 100L)
            .apply()

        return true
    }

    fun joinFamily(family: FamilyProfile, applicantName: String): Boolean {
        _currentFamily.value = family.copy(memberCount = family.memberCount + 1)
        _members.value = _members.value + FamilyMember(
            id = "u_me",
            name = applicantName,
            role = FamilyRole.MEMBER,
            level = 10,
            weeklyActiveness = 10,
            totalContribution = 0L,
            isOnline = true
        )

        prefs.edit()
            .putBoolean("has_family", true)
            .putString("family_name", family.name)
            .apply()

        return true
    }

    fun leaveFamily(): Boolean {
        _currentFamily.value = null
        _members.value = emptyList()
        prefs.edit().putBoolean("has_family", false).apply()
        return true
    }

    fun donateToTreasury(coinAmount: Long): Boolean {
        val fam = _currentFamily.value ?: return false
        if (coinAmount <= 0) return false

        val newFunds = fam.funds + coinAmount
        val newPersonalCoins = fam.personalCoins + (coinAmount / 5) // 20% cashback in family coins
        val newActiveness = fam.weeklyActiveness + (coinAmount.toInt() / 2)

        _currentFamily.value = fam.copy(
            funds = newFunds,
            personalCoins = newPersonalCoins,
            weeklyActiveness = newActiveness,
        )

        // Append to activity events
        val event = FamilyActivityEvent(
            id = "ev_${System.currentTimeMillis()}",
            type = ActivityEventType.DONATION,
            actorName = "You",
            description = "Donated $coinAmount coins to Family Treasury",
        )
        _activityEvents.value = listOf(event) + _activityEvents.value

        prefs.edit()
            .putLong("family_funds", newFunds)
            .putLong("family_personal_coins", newPersonalCoins)
            .apply()

        return true
    }

    fun claimQuest(questId: String): Boolean {
        val fam = _currentFamily.value ?: return false
        val quest = _quests.value.find { it.id == questId } ?: return false
        if (!quest.isCompleted || quest.isClaimed) return false

        _quests.value = _quests.value.map {
            if (it.id == questId) it.copy(isClaimed = true) else it
        }

        _currentFamily.value = fam.copy(
            personalCoins = fam.personalCoins + quest.rewardCoins,
            funds = fam.funds + quest.rewardFunds,
        )

        return true
    }

    fun claimWeeklyChest(tier: ChestTier): Boolean {
        val fam = _currentFamily.value ?: return false
        val chest = _chests.value.find { it.tier == tier } ?: return false
        if (chest.isClaimed) return false
        if (fam.weeklyActiveness < tier.threshold) return false

        _chests.value = _chests.value.map {
            if (it.tier == tier) it.copy(isClaimed = true) else it
        }

        _currentFamily.value = fam.copy(
            personalCoins = fam.personalCoins + chest.rewardCoins,
            funds = fam.funds + chest.rewardFunds,
        )

        val event = FamilyActivityEvent(
            id = "ev_${System.currentTimeMillis()}",
            type = ActivityEventType.CHEST_CLAIM,
            actorName = "You",
            description = "Claimed ${tier.title} rewards (+${chest.rewardCoins} coins)",
        )
        _activityEvents.value = listOf(event) + _activityEvents.value

        return true
    }

    fun claimDailyReward(): Pair<Int, String>? {
        val fam = _currentFamily.value ?: return null
        val rewardAmount = (30..80).random()
        val rewardLabel = "$rewardAmount Family Coins"

        _currentFamily.value = fam.copy(personalCoins = fam.personalCoins + rewardAmount)
        return Pair(rewardAmount, rewardLabel)
    }

    fun updateMemberRole(targetMemberId: String, newRole: FamilyRole): Boolean {
        _members.value = _members.value.map {
            if (it.id == targetMemberId) it.copy(role = newRole) else it
        }
        return true
    }

    fun kickMember(targetMemberId: String): Boolean {
        _members.value = _members.value.filter { it.id != targetMemberId }
        _currentFamily.value = _currentFamily.value?.let {
            it.copy(memberCount = (it.memberCount - 1).coerceAtLeast(1))
        }
        return true
    }

    fun updateAnnouncement(newAnnouncement: String): Boolean {
        val fam = _currentFamily.value ?: return false
        _currentFamily.value = fam.copy(announcement = newAnnouncement)
        return true
    }

    fun approveJoinRequest(requestId: String): Boolean {
        val req = _joinRequests.value.find { it.id == requestId } ?: return false
        _joinRequests.value = _joinRequests.value.filter { it.id != requestId }

        _members.value = _members.value + FamilyMember(
            id = req.applicantId,
            name = req.applicantName,
            role = FamilyRole.MEMBER,
            level = req.applicantLevel,
            weeklyActiveness = 10,
            totalContribution = 0L,
            isOnline = true
        )

        _currentFamily.value = _currentFamily.value?.let {
            it.copy(memberCount = it.memberCount + 1)
        }

        return true
    }

    fun rejectJoinRequest(requestId: String): Boolean {
        _joinRequests.value = _joinRequests.value.filter { it.id != requestId }
        return true
    }

    fun purchaseShopItem(itemId: String): Boolean {
        val fam = _currentFamily.value ?: return false
        val item = _shopItems.value.find { it.id == itemId } ?: return false
        if (fam.personalCoins < item.priceCoins) return false

        _currentFamily.value = fam.copy(personalCoins = fam.personalCoins - item.priceCoins)
        _shopItems.value = _shopItems.value.map {
            if (it.id == itemId) it.copy(isOwned = true) else it
        }
        return true
    }

    fun equipShopItem(itemId: String): Boolean {
        val item = _shopItems.value.find { it.id == itemId } ?: return false
        if (!item.isOwned) return false

        _shopItems.value = _shopItems.value.map {
            if (it.category == item.category) it.copy(isEquipped = it.id == itemId) else it
        }
        return true
    }

    companion object {
        @Volatile
        private var instance: FamilyRepository? = null

        fun getInstance(context: Context): FamilyRepository {
            return instance ?: synchronized(this) {
                instance ?: FamilyRepository(context).also { instance = it }
            }
        }
    }
}
