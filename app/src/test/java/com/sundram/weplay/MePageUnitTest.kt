package com.sundram.weplay

import com.sundram.weplay.data.ALL_GIFT_PACK_OFFERS
import com.sundram.weplay.data.DemoBackpack
import com.sundram.weplay.data.GiftPackRotationManager
import com.sundram.weplay.data.UserProfile
import com.sundram.weplay.data.ageFromDob
import com.sundram.weplay.ui.nav.Routes
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit tests for Project Me Page 2.0 (MeScreen):
 * - UserProfile bindings, ID, level, age calculation, and verification flag.
 * - Navigation routing contracts for all Me page actions.
 * - Gift Pack 48-hour featured offer manager integration.
 * - Illustrated badge showcase and backpack items.
 * - Avatar Studio presets and VIP privilege models.
 */
class MePageUnitTest {

    @Test
    fun testUserProfileBindingsAndAge() {
        val testProfile = UserProfile(
            id = "NV98241",
            name = "Aria Nova",
            gender = "Female",
            dob = "2001-05-15",
            coins = 12500,
            level = 14,
            isVerified = true,
        )

        assertEquals("NV98241", testProfile.id)
        assertEquals("Aria Nova", testProfile.name)
        assertEquals(12500, testProfile.coins)
        assertEquals(14, testProfile.level)
        assertTrue("Verified status must be true for verified user", testProfile.isVerified)

        val age = ageFromDob(testProfile.dob)
        assertTrue("Calculated age should be positive adult age", age in 18..80)

        // Test unverified profile
        val unverifiedProfile = testProfile.copy(isVerified = false)
        assertFalse("Verified status must be false for unverified user", unverifiedProfile.isVerified)
    }

    @Test
    fun testNavigationRoutesContract() {
        // Primary tab contract
        assertTrue("Me tab must exist in primary navigation", Routes.tabs.contains(Routes.ME))
        assertEquals("hub/me", Routes.ME)

        // Me screen target sub-routes
        assertEquals("hub/topup", Routes.TOPUP)
        assertEquals("hub/profile_edit", Routes.PROFILE_EDIT)
        assertEquals("hub/gifts", Routes.GIFTS)
        assertEquals("hub/family", Routes.FAMILY)
        assertEquals("hub/backpack", Routes.BACKPACK)
        assertEquals("hub/settings", Routes.SETTINGS)
        assertEquals("hub/settings/account", Routes.ACCOUNT_SETTINGS)
        assertEquals("hub/settings/support", Routes.SUPPORT_SETTINGS)
        assertEquals("hub/about", Routes.ABOUT)
        assertEquals("discover", Routes.DISCOVER)
        assertEquals("hub/ranking", Routes.RANKING)
        assertEquals("msg", Routes.MSG)
    }

    @Test
    fun testGiftPackRotationIntegration() {
        assertEquals("Should contain 10 starter offers", 10, ALL_GIFT_PACK_OFFERS.size)
        val offer = ALL_GIFT_PACK_OFFERS.first()
        assertNotNull("Featured offer must never be null", offer)
        assertTrue("Offer ID must not be blank", offer.id.isNotBlank())
        assertTrue("Offer title must not be blank", offer.title.isNotBlank())
        assertTrue("Offer must contain at least one reward item", offer.items.isNotEmpty())

        assertEquals(48L * 60L * 60L * 1000L, GiftPackRotationManager.CYCLE_DURATION_MILLIS)
    }

    @Test
    fun testBackpackAndShowcaseCollections() {
        val items = DemoBackpack.sampleItems
        assertTrue("Backpack items must not be empty", items.isNotEmpty())

        val categories = items.map { it.category }.toSet()
        assertTrue("Backpack should contain frames", categories.contains(DemoBackpack.CATEGORY_FRAMES))
        assertTrue("Backpack should contain chat bubbles", categories.contains(DemoBackpack.CATEGORY_BUBBLES))
        assertTrue("Backpack should contain badges", categories.contains(DemoBackpack.CATEGORY_BADGES))
    }

    @Test
    fun testAvatarStudioPresetsIntegrity() {
        // Verify all 8 avatar preset drawables resolve
        val avatarDrawables = listOf(
            R.drawable.me_nova_avatar_01,
            R.drawable.me_nova_avatar_02,
            R.drawable.me_nova_avatar_03,
            R.drawable.me_nova_avatar_04,
            R.drawable.me_nova_avatar_05,
            R.drawable.me_nova_avatar_06,
            R.drawable.me_nova_avatar_07,
            R.drawable.me_nova_avatar_08,
        )

        assertEquals("Must have exactly 8 avatar presets", 8, avatarDrawables.size)
        avatarDrawables.forEach { resId ->
            assertTrue("Drawable resource ID must be valid non-zero", resId != 0)
        }
    }

    @Test
    fun testIllustratedBadgesDrawablesIntegrity() {
        val badgeDrawables = listOf(
            R.drawable.me_nova_badge_host,
            R.drawable.me_nova_badge_vip,
            R.drawable.me_nova_badge_collector,
            R.drawable.me_nova_badge_newcomer,
            R.drawable.me_nova_badge_family,
            R.drawable.me_nova_badge_gamer,
        )

        assertEquals("Must have 6 badge illustrations", 6, badgeDrawables.size)
        badgeDrawables.forEach { resId ->
            assertTrue("Badge drawable resource ID must be valid non-zero", resId != 0)
        }
    }

    @Test
    fun testCoreQuickTilesDrawablesIntegrity() {
        val tileIcons = listOf(
            R.drawable.me_nova_ic_vip,
            R.drawable.me_nova_ic_shop,
            R.drawable.me_nova_ic_avatar_studio,
            R.drawable.me_nova_ic_my_space,
            R.drawable.me_nova_bg_aurora,
        )

        tileIcons.forEach { resId ->
            assertTrue("Tile icon resource ID must be valid non-zero", resId != 0)
        }
    }
}
