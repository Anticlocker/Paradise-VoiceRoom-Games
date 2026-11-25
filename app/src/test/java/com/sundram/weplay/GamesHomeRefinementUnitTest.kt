package com.sundram.weplay

import com.sundram.weplay.ui.nav.Routes
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class GamesHomeRefinementUnitTest {

    @Test
    fun testApplicationIdMigratedWhileKeepingKotlinNamespace() {
        assertEquals("com.paradise.play", BuildConfig.APPLICATION_ID)
        assertEquals("com.sundram.weplay", GamesHomeRefinementUnitTest::class.java.`package`?.name)

        val gradleText = File("build.gradle.kts").readText()
        assertTrue(gradleText.contains("namespace = \"com.sundram.weplay\""))
        assertTrue(gradleText.contains("applicationId = \"com.paradise.play\""))
    }

    @Test
    fun testRequiredHomeWebpAssetsImportedAndOptionalWallpaperOmitted() {
        val resDir = File("src/main/res/drawable-nodpi")
        val requiredAssets = listOf(
            "ap_home_rank_neon.webp",
            "ap_home_tasks_neon.webp",
            "ap_home_social_neon.webp",
            "ap_home_gold_coin.webp",
            "ap_home_notification_bell.webp",
            "ap_home_plus.webp",
            "ap_home_event_star.webp",
        )

        for (asset in requiredAssets) {
            val file = File(resDir, asset)
            assertTrue("Expected imported icon $asset in $resDir", file.exists())
            assertTrue("Expected non-empty icon $asset", file.length() > 0L)
        }

        val forbiddenWallpaper = File(resDir, "ap_home_celestial_wallpaper.webp")
        assertFalse(
            "Do not import ap_home_celestial_wallpaper.webp; existing Home background must be preserved",
            forbiddenWallpaper.exists(),
        )
    }

    @Test
    fun testGamesHomeBackgroundAndHeaderStructurePreserved() {
        val auroraText = File("src/main/java/com/sundram/weplay/ui/theme/AuroraParadise.kt").readText()
        assertTrue(auroraText.contains("fun AuroraGamesBackground"))
        assertTrue(auroraText.contains("R.drawable.ap3r_bg_games_palace"))
        assertTrue(auroraText.contains("R.drawable.ap3r_overlay_sparkle_particles"))
        assertFalse(auroraText.contains("ap_home_celestial_wallpaper"))
        assertFalse(auroraText.contains("Welcome to Aurora"))
        assertTrue(auroraText.contains("text = \"Welcome\""))
        assertTrue(auroraText.contains("R.drawable.ap_home_gold_coin"))
        assertTrue(auroraText.contains("R.drawable.ap_home_plus"))
        assertTrue(auroraText.contains("R.drawable.ap_home_notification_bell"))
    }

    @Test
    fun testGamesHomeLayoutAndRoutesPreserved() {
        val gameLobbyText = File("src/main/java/com/sundram/weplay/ui/screens/GameLobbyScreen.kt").readText()
        assertFalse(gameLobbyText.contains("AuroraGameRoomsBanner"))
        assertTrue(gameLobbyText.contains("R.drawable.ap_home_rank_neon"))
        assertTrue(gameLobbyText.contains("R.drawable.ap_home_tasks_neon"))
        assertTrue(gameLobbyText.contains("R.drawable.ap_home_social_neon"))
        assertTrue(gameLobbyText.contains("R.drawable.ap_home_event_star"))
        assertTrue(gameLobbyText.contains("Routes.RANKING"))
        assertTrue(gameLobbyText.contains("Routes.TASKS"))
        assertTrue(gameLobbyText.contains("Routes.MSG"))
        assertTrue(gameLobbyText.contains("Routes.GAME_ROOMS"))
        assertTrue(gameLobbyText.contains("Routes.EVENT_CENTER"))
        assertTrue(gameLobbyText.contains("Routes.NOTIFICATIONS"))
        assertTrue(gameLobbyText.contains("Routes.WALLET_SETTINGS"))
        assertTrue(gameLobbyText.contains("Routes.TOPUP"))
        assertTrue(gameLobbyText.contains("GiftPackRotationManager"))
        assertTrue(gameLobbyText.contains("CompactCommunityPlayerCard"))
        assertTrue(Routes.GAME_ROOMS.isNotEmpty())
        assertTrue(Routes.EVENT_CENTER.isNotEmpty())
    }
}
