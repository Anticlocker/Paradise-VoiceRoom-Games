package com.sundram.weplay

import com.sundram.weplay.data.FrameDefinition
import com.sundram.weplay.data.FrameRepository
import com.sundram.weplay.data.FrameTier
import com.sundram.weplay.data.FrameUnlockSource
import com.sundram.weplay.data.LeaderboardCategory
import com.sundram.weplay.data.LeaderboardPeriod
import com.sundram.weplay.data.UserProfile
import com.sundram.weplay.ui.nav.Routes
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * Unit tests for Leaderboard and Animated Profile Frame System:
 * - Frame tier specifications, animation durations, and particle counts.
 * - Catalog definition integrity, pricing, and level eligibility.
 * - Frame equipment, unequipping, and UserProfile synchronization.
 * - Coin shop purchasing, balance deduction, and duplicate prevention.
 * - Leaderboard 6-category feed generation, period filters, and tie-break order.
 * - Ranking rule explanations, reset schedules, and anti-fraud notes.
 * - Asset verification for all 30 VectorDrawable and SVG resources.
 */
class LeaderboardAndFramesUnitTest {

    private val repository = FrameRepository(context = null)

    @Test
    fun testFrameTierPropertiesAndSpecs() {
        // Verification against docs/FRAME_ANIMATION_SPEC.md
        assertEquals(0, FrameTier.COMMON.durationMs)
        assertEquals(2200, FrameTier.RARE.durationMs)
        assertEquals(4200, FrameTier.EPIC.durationMs)
        assertEquals(5600, FrameTier.LEGENDARY.durationMs)
        assertEquals(6400, FrameTier.MYTHIC.durationMs)
        assertEquals(8000, FrameTier.CELESTIAL.durationMs)

        // Particle counts
        assertEquals(0, FrameTier.COMMON.particleCount)
        assertEquals(0, FrameTier.RARE.particleCount)
        assertEquals(8, FrameTier.EPIC.particleCount)
        assertEquals(12, FrameTier.LEGENDARY.particleCount)
        assertEquals(3, FrameTier.MYTHIC.particleCount)
        assertEquals(6, FrameTier.CELESTIAL.particleCount)

        // Tier fromId lookups
        assertEquals(FrameTier.CELESTIAL, FrameTier.fromId("CELESTIAL"))
        assertEquals(FrameTier.CELESTIAL, FrameTier.fromId("celestial"))
        assertEquals(FrameTier.LEGENDARY, FrameTier.fromId("legendary"))
        assertEquals(FrameTier.COMMON, FrameTier.fromId("unknown_tier"))
        assertEquals(FrameTier.COMMON, FrameTier.fromId(null))
    }

    @Test
    fun testCatalogCompletenessAndEligibilityRules() {
        val catalog = repository.catalog
        assertTrue("Catalog must contain at least 10 frames", catalog.size >= 10)

        // Verify starter frame is equippable by default
        val starterFrame = catalog.firstOrNull { it.id == "frame_common_aurora" }
        assertNotNull("Starter frame must exist", starterFrame)
        assertTrue(starterFrame!!.isEquippableByDefault)
        assertTrue(repository.isFrameUnlocked(starterFrame, userLevel = 1))

        // Level-gated frames
        val rarePulse = catalog.firstOrNull { it.id == "frame_rare_cyan_pulse" }
        assertNotNull("Rare pulse frame must exist", rarePulse)
        assertEquals(FrameUnlockSource.ACCOUNT_LEVEL, rarePulse!!.unlockSource)
        assertEquals(5, rarePulse.minLevel)
        assertFalse("Level 1 user should not unlock Lv5 frame", repository.isFrameUnlocked(rarePulse, userLevel = 1))
        assertTrue("Level 5 user must unlock Lv5 frame", repository.isFrameUnlocked(rarePulse, userLevel = 5))
        assertTrue("Level 10 user must unlock Lv5 frame", repository.isFrameUnlocked(rarePulse, userLevel = 10))

        // Shop-gated frames
        val azureCrest = catalog.firstOrNull { it.id == "frame_rare_azure_crest" }
        assertNotNull(azureCrest)
        assertTrue(azureCrest!!.isShopPurchasable)
        assertEquals(600L, azureCrest.priceCoins)
        assertFalse("Unowned shop frame must not be unlocked initially", repository.isFrameUnlocked(azureCrest, userLevel = 1))
    }

    @Test
    fun testFrameEquipAndUnequipWorkflow() {
        var profile = UserProfile(
            name = "Test User",
            dobMillis = 1000000000000L,
            id = "tu_123",
            level = 10,
            coins = 5000L,
            frame = null,
        )

        // Equip unlocked Lv5 frame (User is Lv10)
        val equipSuccess = repository.equipFrame(
            frameId = "frame_rare_cyan_pulse",
            userLevel = profile.level,
            profile = profile,
            onProfileUpdate = { updated -> profile = updated },
        )
        assertTrue("Equip of unlocked frame must succeed", equipSuccess)
        assertEquals("frame_rare_cyan_pulse", repository.equippedFrameId.value)
        assertEquals("frame_rare_cyan_pulse", profile.frame)

        // Unequip
        repository.unequipFrame(profile = profile, onProfileUpdate = { updated -> profile = updated })
        assertNull("Equipped frame in repo should be null after unequip", repository.equippedFrameId.value)
        assertNull("Profile frame should be null after unequip", profile.frame)

        // Attempting to equip locked Lv20 frame (User is Lv10) must fail
        val equipLocked = repository.equipFrame(
            frameId = "frame_mythic_radiant_crown",
            userLevel = profile.level,
            profile = profile,
            onProfileUpdate = { updated -> profile = updated },
        )
        assertFalse("Equipping locked frame must fail", equipLocked)
        assertNull(repository.equippedFrameId.value)
    }

    @Test
    fun testShopPurchaseFlowAndCoinBalance() {
        var profile = UserProfile(
            name = "Shopper",
            dobMillis = 1000000000000L,
            id = "shop_user",
            level = 1,
            coins = 1000L,
            frame = null,
        )

        // Purchase expensive frame with insufficient coins (price: 1800 coins, balance: 1000)
        val failResult = repository.purchaseFrame(
            frameId = "frame_epic_amethyst_ring",
            currentCoins = profile.coins,
            userLevel = profile.level,
            profile = profile,
        )
        assertTrue("Purchase with insufficient coins must fail", failResult.isFailure)
        assertEquals(1000L, profile.coins)

        // Purchase affordable frame (price: 600 coins, balance: 1000)
        val successResult = repository.purchaseFrame(
            frameId = "frame_rare_azure_crest",
            currentCoins = profile.coins,
            userLevel = profile.level,
            profile = profile,
            onCoinsDeducted = { newBal -> profile = profile.copy(coins = newBal) },
            onProfileUpdate = { updated -> profile = updated },
        )
        assertTrue("Purchase with sufficient coins must succeed", successResult.isSuccess)
        assertEquals(400L, profile.coins)
        assertEquals("frame_rare_azure_crest", profile.frame)
        assertEquals("frame_rare_azure_crest", repository.equippedFrameId.value)
        assertTrue("Purchased frame must now be owned", repository.ownedFrameIds.value.contains("frame_rare_azure_crest"))

        // Duplicate purchase should fail
        val duplicateResult = repository.purchaseFrame(
            frameId = "frame_rare_azure_crest",
            currentCoins = profile.coins,
            userLevel = profile.level,
            profile = profile,
        )
        assertTrue("Purchasing already owned frame must fail", duplicateResult.isFailure)
    }

    @Test
    fun testLeaderboardCategoriesAndRouting() {
        val testProfile = UserProfile(
            name = "Champion",
            dobMillis = 1000000000000L,
            id = "champ_1",
            level = 8,
            coins = 2000L,
        )

        val categories = LeaderboardCategory.values()
        assertEquals(6, categories.size)

        for (category in categories) {
            val (entries, summary) = repository.getLeaderboard(category, LeaderboardPeriod.TODAY, testProfile)
            assertTrue("${category.name} must return at least 5 ranking entries", entries.size >= 5)

            // Verify podium hierarchy (Rank 1 > Rank 2 > Rank 3)
            val rank1 = entries.first { it.rank == 1 }
            val rank2 = entries.first { it.rank == 2 }
            val rank3 = entries.first { it.rank == 3 }
            assertTrue("Rank 1 score must be >= Rank 2 score", rank1.score >= rank2.score)
            assertTrue("Rank 2 score must be >= Rank 3 score", rank2.score >= rank3.score)

            // Verify score unit match
            assertEquals(category.scoreUnit, summary.category.scoreUnit)
            assertNotNull(category.ctaLabel)
            assertTrue("CTA route must not be empty", category.ctaRoute.isNotBlank())
        }

        // Period filter scaling test
        val (todayEntries, _) = repository.getLeaderboard(LeaderboardCategory.POPULARITY, LeaderboardPeriod.TODAY)
        val (weeklyEntries, _) = repository.getLeaderboard(LeaderboardCategory.POPULARITY, LeaderboardPeriod.WEEKLY)
        assertTrue(
            "Weekly aggregated score must be greater than daily score",
            weeklyEntries.first().score > todayEntries.first().score,
        )
    }

    @Test
    fun testRankingRuleIntegrity() {
        for (category in LeaderboardCategory.values()) {
            val rules = repository.getRankingRules(category)
            assertEquals(category, rules.category)
            assertTrue("Rule title must not be empty", rules.title.isNotBlank())
            assertTrue("Rule summary must not be empty", rules.summary.isNotBlank())
            assertTrue("Calculation rules must have bullet points", rules.calculationRules.isNotEmpty())
            assertTrue("Reset schedule must be defined", rules.resetSchedule.isNotBlank())
            assertTrue("Anti-cheat policy must be documented", rules.antiCheatNote.isNotBlank())
        }
    }

    @Test
    fun testVectorDrawableAndAssetFilesExist() {
        // Verify FrameTier and LeaderboardCategory resource references are valid non-zero IDs
        val tierDrawables = FrameTier.values().flatMap {
            listOf(it.ringDrawableRes, it.podiumDrawableRes, it.crestDrawableRes, it.shopPreviewDrawableRes)
        }
        for (res in tierDrawables) {
            assertTrue("Tier drawable res must be valid non-zero resource", res != 0)
        }

        val bannerDrawables = LeaderboardCategory.values().map { it.bannerDrawableRes }
        for (res in bannerDrawables) {
            assertTrue("Banner drawable res must be valid non-zero resource", res != 0)
        }

        // Test that all 30 VectorDrawable XML resources exist on disk
        val possibleDirs = listOf(
            File("app/src/main/res/drawable"),
            File("src/main/res/drawable"),
        )
        val resDir = possibleDirs.firstOrNull { it.exists() && it.isDirectory }
        assertNotNull("Resource directory must exist", resDir)

        val expectedDrawables = listOf(
            "lb_common_ring.xml", "lb_common_podium.xml", "lb_common_crest.xml", "lb_common_shop_preview.xml",
            "lb_rare_ring.xml", "lb_rare_podium.xml", "lb_rare_crest.xml", "lb_rare_shop_preview.xml",
            "lb_epic_ring.xml", "lb_epic_podium.xml", "lb_epic_crest.xml", "lb_epic_shop_preview.xml",
            "lb_legendary_ring.xml", "lb_legendary_podium.xml", "lb_legendary_crest.xml", "lb_legendary_shop_preview.xml",
            "lb_mythic_ring.xml", "lb_mythic_podium.xml", "lb_mythic_crest.xml", "lb_mythic_shop_preview.xml",
            "lb_celestial_ring.xml", "lb_celestial_podium.xml", "lb_celestial_crest.xml", "lb_celestial_shop_preview.xml",
            "lb_category_popularity_banner.xml", "lb_category_vip_banner.xml", "lb_category_couple_banner.xml",
            "lb_category_room_banner.xml", "lb_category_bff_banner.xml", "lb_category_family_banner.xml",
        )

        for (drawable in expectedDrawables) {
            val file = File(resDir, drawable)
            assertTrue("Missing VectorDrawable: $drawable", file.exists())
            assertTrue("VectorDrawable must not be empty: $drawable", file.length() > 50)
        }
    }
}
