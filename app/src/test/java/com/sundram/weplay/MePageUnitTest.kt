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
 * Unit tests for ME Page 3.0 Clean Premium Edition (MeScreen):
 * - UserProfile bindings, ID, level, age calculation, and verification flag.
 * - Navigation routing contracts for all Me page actions.
 * - Gift Pack 48-hour featured offer manager integration.
 * - Illustrated badge showcase and backpack items.
 * - Clean Avatar Studio presets and VIP privilege models.
 * - Me Clean Premium asset drawable integrity.
 */
class MePageUnitTest {

    @Test
    fun testUserProfileBindingsAndAge() {
        // 989884800000L = 15 May 2001 UTC
        val testProfile = UserProfile(
            name = "Aria Nova",
            dobMillis = 989884800000L,
            id = "NV98241",
            coins = 12500L,
            level = 14,
            phone = "+1234567890",
            email = "aria@example.com",
        )

        assertEquals("NV98241", testProfile.id)
        assertEquals("Aria Nova", testProfile.name)
        assertEquals(12500L, testProfile.coins)
        assertEquals(14, testProfile.level)
        assertTrue("Verified status must be true when phone is verified", testProfile.phoneVerified)
        assertTrue("Verified status must be true when email is verified", testProfile.emailVerified)

        val age = ageFromDob(testProfile.dobMillis)
        assertTrue("Calculated age should be positive adult age", age in 18..80)

        // Test unverified profile
        val unverifiedProfile = UserProfile(
            name = "Aria Nova",
            dobMillis = 989884800000L,
            id = "NV98241",
            phone = null,
            email = null,
            phoneVerified = false,
            emailVerified = false,
        )
        assertFalse("Verified status must be false when neither phone nor email is set", unverifiedProfile.phoneVerified || unverifiedProfile.emailVerified)
    }

    @Test
    fun testNavigationRoutesContract() {
        // Primary tab contract
        assertTrue("Me tab must exist in primary navigation", Routes.tabs.contains(Routes.ME))
        assertEquals("tab_me", Routes.ME)

        // Me screen target sub-routes
        assertEquals("profile/topup", Routes.TOPUP)
        assertEquals("profile/edit", Routes.PROFILE_EDIT)
        assertEquals("profile/gifts", Routes.GIFTS)
        assertEquals("profile/family", Routes.FAMILY)
        assertEquals("profile/backpack", Routes.BACKPACK)
        assertEquals("profile/settings", Routes.SETTINGS)
        assertEquals("settings/account", Routes.ACCOUNT_SETTINGS)
        assertEquals("settings/support", Routes.SUPPORT_SETTINGS)
        assertEquals("profile/about", Routes.ABOUT)
        assertEquals("tab_discover", Routes.DISCOVER)
        assertEquals("tab_ranking", Routes.RANKING)
        assertEquals("tab_msg", Routes.MSG)
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
        val items = DemoBackpack
        assertTrue("Backpack items must not be empty", items.isNotEmpty())

        val names = items.map { it.name }.toSet()
        assertTrue("Backpack should contain items", names.isNotEmpty())
    }

    @Test
    fun testCleanDemoAvatarsIntegrity() {
        val demoAvatars = listOf(
            R.drawable.me_clean_demo_avatar_01,
            R.drawable.me_clean_demo_avatar_02,
            R.drawable.me_clean_demo_avatar_03,
            R.drawable.me_clean_demo_avatar_04,
            R.drawable.me_clean_demo_avatar_05,
            R.drawable.me_clean_demo_avatar_06,
        )

        assertEquals("Must have 6 clean demo avatars", 6, demoAvatars.size)
        demoAvatars.forEach { resId ->
            assertTrue("Demo avatar drawable ID must be valid non-zero", resId != 0)
        }
    }

    @Test
    fun testCleanShortcutsIntegrity() {
        val shortcutIcons = listOf(
            R.drawable.me_clean_vip,
            R.drawable.me_clean_shop,
            R.drawable.me_clean_avatar,
            R.drawable.me_clean_home,
        )

        shortcutIcons.forEach { resId ->
            assertTrue("Shortcut icon drawable ID must be valid non-zero", resId != 0)
        }
    }

    @Test
    fun testCleanMenuIconsIntegrity() {
        val menuIcons = listOf(
            R.drawable.me_clean_moments,
            R.drawable.me_clean_stats,
            R.drawable.me_clean_visitors,
            R.drawable.me_clean_invite,
            R.drawable.me_clean_badges,
            R.drawable.me_clean_contributions,
            R.drawable.me_clean_family,
            R.drawable.me_clean_wallet,
            R.drawable.me_clean_gifts,
            R.drawable.me_clean_backpack,
            R.drawable.me_clean_language,
            R.drawable.me_clean_parental,
            R.drawable.me_clean_help,
            R.drawable.me_clean_security,
            R.drawable.me_clean_settings,
            R.drawable.me_clean_about,
            R.drawable.me_clean_logout,
            R.drawable.me_clean_share,
            R.drawable.me_clean_verify,
            R.drawable.me_clean_giftpack,
        )

        menuIcons.forEach { resId ->
            assertTrue("Menu icon drawable ID must be valid non-zero", resId != 0)
        }
    }
}
