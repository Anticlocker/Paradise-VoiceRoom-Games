package com.sundram.weplay

import com.sundram.weplay.ui.nav.Routes
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * Verification tests for Paradise frontend finalization:
 * - Separated routes & NavHost registration
 * - Branding & launcher/manifest updates
 * - Removal of unsafe PAN/CVV collection and unused trademarked tile
 * - Preservation of approved Games Home, Royal Me, and simplified Discover layouts
 */
class ParadiseFinalizationUnitTest {

    private fun resolveProjectFile(relativePath: String): File {
        val candidates = listOf(
            File(relativePath),
            File("..", relativePath),
        )
        return candidates.firstOrNull { it.exists() }
            ?: error("Could not locate $relativePath from ${File(".").absolutePath}")
    }

    @Test
    fun testDedicatedRoutesAreDistinctAndNonBlank() {
        val dedicated = listOf(
            Routes.VIP,
            Routes.SHOP,
            Routes.ALL_GAMES,
            Routes.MY_SPACE,
            Routes.NEARBY,
            Routes.CLUBS,
            Routes.FEEDBACK,
            Routes.ONBOARDING,
            Routes.AUTH,
            Routes.SPLASH,
        )
        assertTrue(dedicated.all { it.isNotBlank() })
        assertNotEquals(Routes.TOPUP, Routes.VIP)
        assertNotEquals(Routes.GIFTS, Routes.SHOP)
        assertNotEquals(Routes.GAME_ROOMS, Routes.ALL_GAMES)
        assertNotEquals(Routes.DISCOVER, Routes.NEARBY)
        assertNotEquals(Routes.DISCOVER, Routes.CLUBS)
        assertNotEquals(Routes.PROFILE_EDIT, Routes.MY_SPACE)
    }

    @Test
    fun testMainActivityRegistersAllNewRoutes() {
        val mainActivity = resolveProjectFile("app/src/main/java/com/sundram/weplay/MainActivity.kt").readText()
        val requiredDestinations = listOf(
            "composable(Routes.VIP)",
            "composable(Routes.SHOP)",
            "composable(Routes.ALL_GAMES)",
            "composable(Routes.MY_SPACE)",
            "composable(Routes.NEARBY)",
            "composable(Routes.CLUBS)",
            "composable(Routes.FEEDBACK)",
            "composable(Routes.ONBOARDING)",
            "composable(Routes.AUTH)",
            "composable(Routes.SPLASH)",
            "composable(Routes.FAMILY_DISCOVERY)",
        )
        for (dest in requiredDestinations) {
            assertTrue("Missing NavHost destination: $dest", mainActivity.contains(dest))
        }
        assertTrue(mainActivity.contains("SettingsHomeScreen(nav, profile)"))
    }

    @Test
    fun testBrandingStringsManifestAndLauncherIcons() {
        val stringsXml = resolveProjectFile("app/src/main/res/values/strings.xml").readText()
        assertTrue(stringsXml.contains("Paradise - Voice Room &amp; Games"))
        assertTrue(stringsXml.contains("<string name=\"app_short_name\">Paradise</string>"))
        assertFalse(stringsXml.contains("WePlay", ignoreCase = true))

        val manifest = resolveProjectFile("app/src/main/AndroidManifest.xml").readText()
        assertTrue(manifest.contains("android.permission.ACCESS_COARSE_LOCATION"))
        assertTrue(manifest.contains("@mipmap/ic_launcher"))
        assertTrue(manifest.contains("@string/app_short_name"))

        val adaptiveIcon = resolveProjectFile("app/src/main/res/mipmap-anydpi-v26/ic_launcher.xml")
        assertTrue(adaptiveIcon.exists())
        val adaptiveContent = adaptiveIcon.readText()
        assertTrue(adaptiveContent.contains("ic_launcher_background"))
        assertTrue(adaptiveContent.contains("ic_launcher_foreground"))
    }

    @Test
    fun testWalletDoesNotCollectRawCardNumberOrCvv() {
        val wallet = resolveProjectFile("app/src/main/java/com/sundram/weplay/ui/screens/settings/WalletSettingsScreen.kt").readText()
        assertFalse("Raw PAN placeholder must not be present", wallet.contains("4242424242424242"))
        assertFalse("CVV field must not be present", wallet.contains("\"CVV\""))
        assertFalse("Engineering phase text must not be exposed", wallet.contains("Phase 5"))
        assertTrue(wallet.contains("AddBillingPreviewDialog"))
    }

    @Test
    fun testTrademarkedUnoTileRemovedAndApprovedScreensPreserved() {
        val unoCandidates = listOf(
            File("app/src/main/res/drawable-nodpi/aurora3_tile_uno.png"),
            File("../app/src/main/res/drawable-nodpi/aurora3_tile_uno.png"),
        )
        assertTrue("Unused trademarked UNO tile must be removed", unoCandidates.none { it.exists() })

        val gameLobby = resolveProjectFile("app/src/main/java/com/sundram/weplay/ui/screens/GameLobbyScreen.kt").readText()
        assertTrue(gameLobby.contains("AuroraGamesBackground"))
        assertTrue(gameLobby.contains("Routes.ALL_GAMES"))
        assertTrue(gameLobby.contains("Routes.GAME_ROOMS"))

        val meScreen = resolveProjectFile("app/src/main/java/com/sundram/weplay/ui/screens/MeScreen.kt").readText()
        assertTrue(meScreen.contains("Routes.VIP"))
        assertTrue(meScreen.contains("Routes.SHOP"))
        assertTrue(meScreen.contains("Routes.MY_SPACE"))

        val discoverScreen = resolveProjectFile("app/src/main/java/com/sundram/weplay/ui/screens/DiscoverScreen.kt").readText()
        assertTrue(discoverScreen.contains("Routes.NEARBY"))
        assertTrue(discoverScreen.contains("Routes.CLUBS"))
        assertFalse("Removed 4-card Community Moments heading must not return", discoverScreen.contains("Community Moments"))
    }
}
