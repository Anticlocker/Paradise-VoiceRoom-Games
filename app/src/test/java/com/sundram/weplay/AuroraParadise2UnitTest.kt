package com.sundram.weplay

import androidx.compose.ui.graphics.Color
import com.sundram.weplay.data.Demo
import com.sundram.weplay.ui.nav.Routes
import com.sundram.weplay.ui.theme.AuroraTurquoise
import com.sundram.weplay.ui.theme.CelestialViolet
import com.sundram.weplay.ui.theme.ChampagneGold
import com.sundram.weplay.ui.theme.DeepMidnight
import com.sundram.weplay.ui.theme.DreamLavender
import com.sundram.weplay.ui.theme.PearlWhite
import com.sundram.weplay.ui.theme.SoftPeach
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AuroraParadise2UnitTest {

    @Test
    fun testAuroraParadise2ColorPaletteValues() {
        assertEquals("Celestial Violet must be #7042D9", Color(0xFF7042D9), CelestialViolet)
        assertEquals("Deep Midnight must be #17143D", Color(0xFF17143D), DeepMidnight)
        assertEquals("Aurora Turquoise must be #23D5D1", Color(0xFF23D5D1), AuroraTurquoise)
        assertEquals("Dream Lavender must be #BBA4FA", Color(0xFFBBA4FA), DreamLavender)
        assertEquals("Soft Peach must be #FFB9A4", Color(0xFFFFB9A4), SoftPeach)
        assertEquals("Champagne Gold must be #F4C76A", Color(0xFFF4C76A), ChampagneGold)
        assertEquals("Pearl White must be #FFF9FF", Color(0xFFFFF9FF), PearlWhite)
    }

    @Test
    fun testVoiceRoomsHaveLevelsAndCrests() {
        val rooms = Demo.rooms
        assertTrue("Must have demo rooms", rooms.isNotEmpty())
        for (room in rooms) {
            assertTrue("Host level must be positive", room.hostLevel > 0)
            assertTrue("Room level crest must be positive", room.roomLevel > 0)
        }
        val pkRooms = rooms.filter { it.isPkActive }
        assertTrue("At least one room has PK active", pkRooms.isNotEmpty())
    }

    @Test
    fun testAll40NavigationRoutesPreserved() {
        val allRoutes = listOf(
            Routes.SPLASH,
            Routes.AUTH,
            Routes.ONBOARDING,
            Routes.GAMES,
            Routes.LUDO,
            Routes.SPY,
            Routes.ROOMS,
            Routes.CHAT,
            Routes.DISCOVER,
            Routes.ME,
            Routes.SETTINGS,
            Routes.SETTINGS_EDIT,
            Routes.PROFILE_EDIT,
            Routes.TOPUP,
            Routes.GIFTS,
            Routes.BACKPACK,
            Routes.SUPPORT,
            Routes.FEEDBACK,
            Routes.ABOUT,
            Routes.GAME_ROOMS,
            Routes.ROOM_CREATE,
            Routes.VIP,
            Routes.SHOP,
            Routes.RANKING,
            Routes.FAMILY,
            Routes.FAMILY_DISCOVERY,
            Routes.FAMILY_CREATION,
            Routes.FAMILY_DETAIL,
            Routes.FAMILY_CHAT,
            Routes.FAMILY_VOICE_ROOM,
            Routes.FAMILY_MANAGEMENT,
            Routes.FAMILY_LEADERBOARD,
            Routes.FAMILY_SHOP,
            Routes.FAMILY_ROSTER,
            Routes.FAMILY_QUESTS,
            Routes.TASKS,
            Routes.FRIENDS,
            Routes.ROOM_ROUTE,
            Routes.EVENT_CENTER,
            Routes.EVENT_DETAIL,
        )
        assertEquals("Must preserve all 40 navigation routes", 40, allRoutes.size)
    }
}
