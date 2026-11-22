package com.sundram.weplay.data

import androidx.annotation.DrawableRes
import com.sundram.weplay.R

/**
 * Family System Data Models for Kotlin + Jetpack Compose.
 *
 * Implements an original Family/Guild system with:
 * - Distinct personal Family Coins vs. shared Family Funds
 * - Role hierarchy (Leader > Deputy > Admin > Moderator > Member)
 * - 4 Weekly Chest tiers (Normal, Advanced, Rare, Super)
 * - Daily, Weekly, Community, and Contribution Quests
 * - Treasury donation ledger & activity timeline
 * - Family shop catalog & cosmetics (frames, signs)
 */

enum class FamilyRole(val priority: Int, val title: String, @DrawableRes val badgeRes: Int) {
    LEADER(5, "Leader", R.drawable.family_ic_leader),
    DEPUTY(4, "Deputy", R.drawable.family_ic_deputy),
    ADMIN(3, "Admin", R.drawable.family_ic_admin),
    MODERATOR(2, "Moderator", R.drawable.family_ic_moderator),
    MEMBER(1, "Member", R.drawable.family_ic_member);

    fun canManageSettings(): Boolean = this == LEADER || this == DEPUTY
    fun canApproveApplications(): Boolean = this == LEADER || this == DEPUTY || this == ADMIN
    fun canManageMembers(): Boolean = this == LEADER || this == DEPUTY
    fun canPromote(targetRole: FamilyRole): Boolean = this == LEADER && targetRole != LEADER
    fun canDemote(targetRole: FamilyRole): Boolean = this == LEADER || (this == DEPUTY && targetRole.priority < 4)
    fun canKick(targetRole: FamilyRole): Boolean = this.priority > targetRole.priority
    fun canDisband(): Boolean = this == LEADER
    fun canTransferLeadership(): Boolean = this == LEADER
}

enum class RecruitmentPolicy(val title: String) {
    OPEN("Open to All"),
    APPROVAL_REQUIRED("Approval Required"),
    INVITE_ONLY("Invite Only")
}

enum class FamilyTier(val displayName: String, @DrawableRes val badgeRes: Int) {
    BRONZE("Bronze Guild", R.drawable.family_tier_bronze),
    SILVER("Silver Guild", R.drawable.family_tier_silver),
    GOLD("Gold Guild", R.drawable.family_tier_gold),
    PLATINUM("Platinum Guild", R.drawable.family_tier_platinum),
    DIAMOND("Diamond Guild", R.drawable.family_tier_diamond),
    LEGEND("Legend Guild", R.drawable.family_tier_legend)
}

data class FamilyProfile(
    val id: String,
    val name: String,
    val tag: String,
    val sign: String,
    val description: String,
    val level: Int = 1,
    val tier: FamilyTier = FamilyTier.BRONZE,
    val funds: Long = 1200L,              // Shared progression currency
    val personalCoins: Long = 150L,        // Personal reward currency
    val weeklyActiveness: Int = 420,
    val memberCount: Int = 18,
    val maxMembers: Int = 30,
    val leaderId: String,
    val leaderName: String,
    val announcement: String = "Welcome to the family! Join daily rooms and check in.",
    val voiceRoomId: String? = "family_room_01",
    val recruitmentPolicy: RecruitmentPolicy = RecruitmentPolicy.OPEN,
    val minLevelRequirement: Int = 1,
    val createdAt: Long = System.currentTimeMillis() - (14L * 24 * 60 * 60 * 1000),
    @DrawableRes val emblemRes: Int = R.drawable.family_ic_family,
)

data class FamilyMember(
    val id: String,
    val name: String,
    val photoPath: String? = null,
    val role: FamilyRole = FamilyRole.MEMBER,
    val level: Int = 10,
    val weeklyActiveness: Int = 85,
    val totalContribution: Long = 620L,
    val isOnline: Boolean = true,
    val isMuted: Boolean = false,
    val joinedAt: Long = System.currentTimeMillis() - (5L * 24 * 60 * 60 * 1000),
    val frame: String? = null,
)

enum class QuestCategory(val title: String) {
    DAILY("Daily"),
    WEEKLY("Weekly"),
    COMMUNITY("Community"),
    CONTRIBUTION("Contribution")
}

data class FamilyQuest(
    val id: String,
    val title: String,
    val description: String,
    val category: QuestCategory,
    val targetCount: Int,
    val currentCount: Int,
    val rewardCoins: Int,
    val rewardFunds: Int,
    val isClaimed: Boolean = false,
    @DrawableRes val iconRes: Int = R.drawable.family_ic_quest,
) {
    val isCompleted: Boolean get() = currentCount >= targetCount
    val progress: Float get() = (currentCount.toFloat() / targetCount.coerceAtLeast(1)).coerceIn(0f, 1f)
}

enum class ChestTier(val title: String, val threshold: Int, @DrawableRes val iconRes: Int) {
    NORMAL("Normal Chest", 200, R.drawable.family_ic_chest_normal),
    ADVANCED("Advanced Chest", 500, R.drawable.family_ic_chest_advanced),
    RARE("Rare Chest", 1000, R.drawable.family_ic_chest_rare),
    SUPER("Super Chest", 2000, R.drawable.family_ic_chest_super)
}

data class FamilyWeeklyChest(
    val tier: ChestTier,
    val rewardCoins: Int,
    val rewardFunds: Int,
    val isClaimed: Boolean = false,
)

enum class ActivityEventType(val title: String, @DrawableRes val iconRes: Int) {
    MEMBER_JOIN("Member Joined", R.drawable.family_ic_join),
    MEMBER_LEAVE("Member Left", R.drawable.family_ic_leave),
    LEVEL_UP("Guild Leveled Up", R.drawable.family_ic_level),
    DONATION("Treasury Donation", R.drawable.family_ic_donate),
    CHEST_CLAIM("Weekly Chest Claimed", R.drawable.family_ic_reward),
    QUEST_COMPLETE("Quest Finished", R.drawable.family_ic_quest),
    ANNOUNCEMENT("Notice Updated", R.drawable.family_ic_announcement)
}

data class FamilyActivityEvent(
    val id: String,
    val type: ActivityEventType,
    val actorName: String,
    val description: String,
    val timestamp: Long = System.currentTimeMillis(),
)

enum class ShopCategory(val title: String) {
    FRAMES("Frames"),
    SIGNS("Family Signs"),
    EFFECTS("Room Effects"),
    GIFTS("Family Gifts")
}

data class FamilyShopItem(
    val id: String,
    val title: String,
    val description: String,
    val category: ShopCategory,
    val priceCoins: Int,
    val requiredLevel: Int,
    val isOwned: Boolean = false,
    val isEquipped: Boolean = false,
    @DrawableRes val iconRes: Int,
)

data class FamilyJoinRequest(
    val id: String,
    val applicantId: String,
    val applicantName: String,
    val applicantLevel: Int,
    val requestedAt: Long = System.currentTimeMillis(),
)

data class FamilyRankingEntry(
    val rank: Int,
    val familyId: String,
    val name: String,
    val tag: String,
    val tier: FamilyTier,
    val level: Int,
    val weeklyActiveness: Int,
    val memberCount: Int,
    val leaderName: String,
    @DrawableRes val emblemRes: Int = R.drawable.family_ic_family,
)
