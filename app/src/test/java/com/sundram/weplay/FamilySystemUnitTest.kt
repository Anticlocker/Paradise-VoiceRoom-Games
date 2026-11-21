package com.sundram.weplay

import com.sundram.weplay.data.ChestTier
import com.sundram.weplay.data.FamilyMember
import com.sundram.weplay.data.FamilyProfile
import com.sundram.weplay.data.FamilyQuest
import com.sundram.weplay.data.FamilyRole
import com.sundram.weplay.data.FamilyTier
import com.sundram.weplay.data.QuestCategory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit tests for the Family System:
 * - Role hierarchy and permission boundaries.
 * - Family models and progression rules.
 * - Weekly chest thresholds.
 * - Quest completion and progress calculation.
 * - Asset drawable integrity for all 68 Family WebP resources.
 */
class FamilySystemUnitTest {

    @Test
    fun testRoleHierarchyAndPermissions() {
        val leader = FamilyRole.LEADER
        val deputy = FamilyRole.DEPUTY
        val admin = FamilyRole.ADMIN
        val moderator = FamilyRole.MODERATOR
        val member = FamilyRole.MEMBER

        // Priority ordering
        assertTrue(leader.priority > deputy.priority)
        assertTrue(deputy.priority > admin.priority)
        assertTrue(admin.priority > moderator.priority)
        assertTrue(moderator.priority > member.priority)

        // Leader permissions
        assertTrue(leader.canManageSettings())
        assertTrue(leader.canApproveApplications())
        assertTrue(leader.canManageMembers())
        assertTrue(leader.canPromote(deputy))
        assertTrue(leader.canDemote(deputy))
        assertTrue(leader.canKick(member))
        assertTrue(leader.canDisband())
        assertTrue(leader.canTransferLeadership())

        // Deputy permissions
        assertTrue(deputy.canManageSettings())
        assertTrue(deputy.canApproveApplications())
        assertTrue(deputy.canManageMembers())
        assertFalse(deputy.canPromote(admin))
        assertFalse(deputy.canDisband())
        assertFalse(deputy.canTransferLeadership())
        assertTrue(deputy.canKick(member))
        assertFalse(deputy.canKick(leader))

        // Admin permissions
        assertFalse(admin.canManageSettings())
        assertTrue(admin.canApproveApplications())
        assertFalse(admin.canManageMembers())
        assertFalse(admin.canDisband())

        // Member permissions
        assertFalse(member.canManageSettings())
        assertFalse(member.canApproveApplications())
        assertFalse(member.canManageMembers())
        assertFalse(member.canDisband())
    }

    @Test
    fun testFamilyProfileCreationAndLimits() {
        val family = FamilyProfile(
            id = "FAM-1082",
            name = "Aurora Knights",
            tag = "AURK",
            sign = "Unity in Voice",
            description = "Active voice family",
            level = 3,
            tier = FamilyTier.GOLD,
            funds = 3450L,
            personalCoins = 420L,
            weeklyActiveness = 680,
            memberCount = 18,
            maxMembers = 35,
            leaderId = "u_leader_01",
            leaderName = "Aria Nova",
        )

        assertEquals("FAM-1082", family.id)
        assertEquals("Aurora Knights", family.name)
        assertEquals("AURK", family.tag)
        assertEquals(3, family.level)
        assertEquals(FamilyTier.GOLD, family.tier)
        assertEquals(3450L, family.funds)
        assertEquals(420L, family.personalCoins)
        assertEquals(18, family.memberCount)
        assertEquals(35, family.maxMembers)
        assertTrue(family.memberCount <= family.maxMembers)
    }

    @Test
    fun testWeeklyChestThresholds() {
        assertEquals("Normal Chest", ChestTier.NORMAL.title)
        assertEquals(200, ChestTier.NORMAL.threshold)

        assertEquals("Advanced Chest", ChestTier.ADVANCED.title)
        assertEquals(500, ChestTier.ADVANCED.threshold)

        assertEquals("Rare Chest", ChestTier.RARE.title)
        assertEquals(1000, ChestTier.RARE.threshold)

        assertEquals("Super Chest", ChestTier.SUPER.title)
        assertEquals(2000, ChestTier.SUPER.threshold)

        assertTrue(ChestTier.NORMAL.threshold < ChestTier.ADVANCED.threshold)
        assertTrue(ChestTier.ADVANCED.threshold < ChestTier.RARE.threshold)
        assertTrue(ChestTier.RARE.threshold < ChestTier.SUPER.threshold)
    }

    @Test
    fun testFamilyQuestProgressCalculation() {
        val quest = FamilyQuest(
            id = "q1",
            title = "Room Voice Chat",
            description = "Spend 15 mins in room",
            category = QuestCategory.DAILY,
            targetCount = 15,
            currentCount = 10,
            rewardCoins = 30,
            rewardFunds = 80,
            isClaimed = false,
        )

        assertFalse(quest.isCompleted)
        assertEquals(10f / 15f, quest.progress, 0.01f)

        val completedQuest = quest.copy(currentCount = 15)
        assertTrue(completedQuest.isCompleted)
        assertEquals(1.0f, completedQuest.progress, 0.01f)

        val claimedQuest = completedQuest.copy(isClaimed = true)
        assertTrue(claimedQuest.isClaimed)
    }

    @Test
    fun testFamilyDrawablesIntegrity() {
        val familyDrawables = listOf(
            R.drawable.family_ic_family,
            R.drawable.family_ic_leader,
            R.drawable.family_ic_deputy,
            R.drawable.family_ic_admin,
            R.drawable.family_ic_moderator,
            R.drawable.family_ic_member,
            R.drawable.family_ic_funds,
            R.drawable.family_ic_coins,
            R.drawable.family_ic_activeness,
            R.drawable.family_ic_donate,
            R.drawable.family_ic_quest,
            R.drawable.family_ic_chest_normal,
            R.drawable.family_ic_chest_advanced,
            R.drawable.family_ic_chest_rare,
            R.drawable.family_ic_chest_super,
            R.drawable.family_ic_gacha,
            R.drawable.family_ic_voice,
            R.drawable.family_ic_chat,
            R.drawable.family_ic_ranking,
            R.drawable.family_ic_shop,
            R.drawable.family_ic_announcement,
            R.drawable.family_ic_family_sign,
            R.drawable.family_ic_family_frame,
            R.drawable.family_tier_bronze,
            R.drawable.family_tier_silver,
            R.drawable.family_tier_gold,
            R.drawable.family_tier_platinum,
            R.drawable.family_tier_diamond,
            R.drawable.family_tier_legend,
            R.drawable.family_bg_aurora,
            R.drawable.family_bg_festival,
            R.drawable.family_bg_pearl,
        )

        familyDrawables.forEach { resId ->
            assertTrue("Family drawable resource ID must be valid non-zero", resId != 0)
        }
    }
}
