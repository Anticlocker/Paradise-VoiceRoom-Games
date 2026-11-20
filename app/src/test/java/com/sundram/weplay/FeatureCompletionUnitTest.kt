package com.sundram.weplay

import com.sundram.weplay.data.ALL_DEFAULT_AVATARS
import com.sundram.weplay.data.ALL_GIFT_PACK_OFFERS
import com.sundram.weplay.data.DemoCoinPacks
import com.sundram.weplay.data.STARTER_ACHIEVEMENTS
import com.sundram.weplay.data.STARTER_DAILY_MISSIONS
import com.sundram.weplay.data.STARTER_NEW_PLAYER_MISSIONS
import com.sundram.weplay.data.STARTER_WEEKLY_CHALLENGES
import com.sundram.weplay.data.getAvatarById
import com.sundram.weplay.ui.nav.Routes
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Functional unit tests for Feature Completion:
 * Navigation, Gift Pack 10 offers & 48h rotation, Top Up 8 packages,
 * 12 Default Avatars, and Task Center missions.
 */
class FeatureCompletionUnitTest {

    @Test
    fun testNavigationRoutesIntegrity() {
        // Verify primary tabs
        assertTrue(Routes.tabs.contains(Routes.HUB))
        assertTrue(Routes.tabs.contains(Routes.ROOM))
        assertTrue(Routes.tabs.contains(Routes.MSG))
        assertTrue(Routes.tabs.contains(Routes.DISCOVER))
        assertTrue(Routes.tabs.contains(Routes.ME))
        assertEquals(5, Routes.tabs.size)

        // Verify Task Center route exists
        assertEquals("hub/tasks", Routes.TASKS)
        assertEquals("hub/tasks", Routes.tasksRoute())
    }

    @Test
    fun testGiftPackTenOffersCatalog() {
        assertEquals("Should contain exactly 10 starter offer definitions", 10, ALL_GIFT_PACK_OFFERS.size)

        val ids = ALL_GIFT_PACK_OFFERS.map { it.id }.toSet()
        assertEquals("All 10 offer IDs must be unique", 10, ids.size)

        // Verify ID range GP01 to GP10
        for (i in 1..10) {
            val expectedId = "GP%02d".format(i)
            assertTrue("Offer $expectedId must exist", ids.contains(expectedId))
        }

        // Verify each offer has title, valid price, and guaranteed items
        ALL_GIFT_PACK_OFFERS.forEach { offer ->
            assertTrue("Offer title must not be blank", offer.title.isNotBlank())
            assertTrue("Demo price must be positive", offer.demoPriceRupees > 0)
            assertTrue("Items list must not be empty", offer.items.isNotEmpty())
            offer.items.forEach { item ->
                assertTrue("Item name must not be blank", item.name.isNotBlank())
                assertTrue("Item count must be positive", item.count > 0)
                assertTrue("Item icon must resolve", item.iconRes != 0)
            }
        }
    }

    @Test
    fun testDefaultAvatarsTwelveCatalog() {
        assertEquals("Should contain exactly 12 default avatars", 12, ALL_DEFAULT_AVATARS.size)

        val ids = ALL_DEFAULT_AVATARS.map { it.id }.toSet()
        assertEquals("All 12 avatar IDs must be unique", 12, ids.size)

        // Check avatar resolution
        ALL_DEFAULT_AVATARS.forEach { avatar ->
            assertTrue("Avatar name must not be blank", avatar.name.isNotBlank())
            assertTrue("Avatar title must not be blank", avatar.title.isNotBlank())
            assertTrue("Avatar drawable must resolve", avatar.drawableRes != 0)
        }

        // Check fallback lookup
        val fallback = getAvatarById("non_existent_id")
        assertNotNull(fallback)
        assertEquals(ALL_DEFAULT_AVATARS[0].id, fallback.id)
    }

    @Test
    fun testTopUpEightCoinPacks() {
        assertEquals("Should contain exactly 8 coin packages", 8, DemoCoinPacks.size)

        val expectedTitles = listOf("Starter", "Mini", "Popular", "Value", "Premium", "Super", "Royal", "Mega")
        val actualTitles = DemoCoinPacks.map { it.title }
        assertEquals(expectedTitles, actualTitles)

        DemoCoinPacks.forEach { pack ->
            assertTrue("Coins must be positive", pack.coins > 0)
            assertTrue("Price must be positive", pack.price > 0)
            assertTrue("Total must be >= base coins", pack.total >= pack.coins)
        }
    }

    @Test
    fun testTaskDefinitionsIntegrity() {
        // Daily Missions
        assertEquals("Should have 8 starter daily missions", 8, STARTER_DAILY_MISSIONS.size)
        STARTER_DAILY_MISSIONS.forEach { task ->
            assertTrue(task.title.isNotBlank())
            assertTrue(task.xpReward > 0)
            assertTrue(task.coinReward > 0)
            assertTrue(task.targetCount > 0)
            assertTrue(task.actionRoute.isNotBlank())
        }

        // Weekly Challenges
        assertEquals("Should have 5 starter weekly challenges", 5, STARTER_WEEKLY_CHALLENGES.size)
        STARTER_WEEKLY_CHALLENGES.forEach { task ->
            assertTrue(task.title.isNotBlank())
            assertTrue(task.xpReward > 0)
            assertTrue(task.targetCount > 0)
        }

        // New Player Missions
        assertEquals("Should have 6 starter new player missions", 6, STARTER_NEW_PLAYER_MISSIONS.size)

        // Achievements
        assertEquals("Should have 7 starter achievements", 7, STARTER_ACHIEVEMENTS.size)
    }
}
