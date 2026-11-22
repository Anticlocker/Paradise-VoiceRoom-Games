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
import com.sundram.weplay.ui.theme.CelestialNavy
import com.sundram.weplay.ui.theme.RoyalViolet
import com.sundram.weplay.ui.theme.MysticPurple
import com.sundram.weplay.ui.theme.CrystalBlue
import com.sundram.weplay.ui.theme.SunsetRose
import com.sundram.weplay.ui.theme.RoyalGold
import com.sundram.weplay.ui.theme.SoftGold
import com.sundram.weplay.ui.theme.CloudWhite
import com.sundram.weplay.ui.theme.DeepInk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AuroraParadise2UnitTest {

    @Test
    fun testAuroraParadise3ColorPaletteValues() {
        assertEquals("Celestial Navy must be #11134A", Color(0xFF11134A), CelestialNavy)
        assertEquals("Royal Violet must be #5423B7", Color(0xFF5423B7), RoyalViolet)
        assertEquals("Mystic Purple must be #7435D9", Color(0xFF7435D9), MysticPurple)
        assertEquals("Dream Lavender must be #B89BFF", Color(0xFFB89BFF), DreamLavender)
        assertEquals("Crystal Blue must be #54CFFF", Color(0xFF54CFFF), CrystalBlue)
        assertEquals("Sunset Rose must be #FF78C4", Color(0xFFFF78C4), SunsetRose)
        assertEquals("Royal Gold must be #FFD36B", Color(0xFFFFD36B), RoyalGold)
        assertEquals("Soft Gold must be #FFF0B6", Color(0xFFFFF0B6), SoftGold)
        assertEquals("Cloud White must be #F8F1FF", Color(0xFFF8F1FF), CloudWhite)
        assertEquals("Deep Ink must be #211448", Color(0xFF211448), DeepInk)
    }

    @Test
    fun testAuroraParadise2ColorPaletteValues() {
        assertEquals("Deep Midnight must be #11134A (mapped to CelestialNavy)", CelestialNavy, DeepMidnight)
        assertEquals("Aurora Turquoise must be #54CFFF (mapped to CrystalBlue)", CrystalBlue, AuroraTurquoise)
        assertEquals("Pearl White must be #F8F1FF (mapped to CloudWhite)", CloudWhite, PearlWhite)
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

    @Test
    fun testEventModelRepoSpecificArtworkMapped() {
        val events = com.sundram.weplay.data.EventRepository.ALL_EVENTS
        val welcome = events.find { it.id == "evt_welcome" }
        val topup = events.find { it.id == "evt_first_topup" }
        val starlight = events.find { it.id == "evt_starlight" }
        val family = events.find { it.id == "evt_family_fest" }
        val pk = events.find { it.id == "evt_royal_arena" }

        org.junit.Assert.assertNotNull(welcome)
        assertEquals(R.drawable.ap3r_event_welcome, welcome?.coverRes)
        assertEquals(R.drawable.ap3r_event_first_topup, topup?.coverRes)
        assertEquals(R.drawable.ap3r_event_starlight, starlight?.coverRes)
        assertEquals(R.drawable.ap3r_event_family, family?.coverRes)
        assertEquals(R.drawable.ap3r_event_pk, pk?.coverRes)
    }
}
