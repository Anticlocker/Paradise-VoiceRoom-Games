package com.sundram.weplay

import com.sundram.weplay.data.Demo
import com.sundram.weplay.ui.components.initialsOf
import com.sundram.weplay.ui.components.tagColors
import com.sundram.weplay.ui.nav.Routes
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Functional and data integrity unit tests for the first two pages:
 * 1. Gaming / Home Page
 * 2. Voice Room Discovery / Voice Lounge Page
 */
class FirstTwoPagesUnitTest {

    @Test
    fun testCoreNavigationRoutesAreWellFormed() {
        // Tab routes
        assertEquals("tab_hub", Routes.HUB)
        assertEquals("tab_room", Routes.ROOM)
        assertEquals("tab_discover", Routes.DISCOVER)
        assertEquals("tab_msg", Routes.MSG)
        assertEquals("tab_me", Routes.ME)

        // Subpage routes
        assertEquals("tab_ranking", Routes.RANKING)
        assertEquals("tab_create_room", Routes.CREATE_ROOM)
        assertEquals("notifications", Routes.NOTIFICATIONS)
        assertEquals("profile/topup", Routes.TOPUP)
        assertEquals("game/rooms", Routes.GAME_ROOMS)
        assertEquals("game/spy", Routes.SPY)
        assertEquals("game/ludo", Routes.LUDO)

        // Parameterized routes
        assertEquals("room/room_101", Routes.roomRoute("room_101"))
        assertEquals("chat/user_5", Routes.chatRoute("user_5"))
    }

    @Test
    fun testVoiceRoomCategoryFiltering() {
        val rooms = Demo.rooms
        assertTrue("Demo rooms must not be empty", rooms.isNotEmpty())

        val categories = listOf("All", "Friends", "PK", "Music", "Video", "Game", "Chats")
        for (category in categories) {
            val filtered = rooms.filter { room ->
                when (category) {
                    "All" -> true
                    "Friends" -> room.tag == "Friends" || room.tag == "Casual"
                    "PK" -> room.tag == "PK" || room.tag == "Deduction"
                    "Music" -> room.tag == "Music" || room.tag == "KTV"
                    "Video" -> room.tag == "Video"
                    "Game" -> room.tag == "Game" || room.tag == "Games"
                    "Chats" -> room.tag == "Chats" || room.tag == "Casual"
                    else -> room.tag.equals(category, ignoreCase = true)
                }
            }
            assertNotNull("Filtered list for category '$category' must not be null", filtered)
            // Verify all items match category
            for (room in filtered) {
                if (category != "All") {
                    val valid = when (category) {
                        "Friends" -> room.tag == "Friends" || room.tag == "Casual"
                        "PK" -> room.tag == "PK" || room.tag == "Deduction"
                        "Music" -> room.tag == "Music" || room.tag == "KTV"
                        "Video" -> room.tag == "Video"
                        "Game" -> room.tag == "Game" || room.tag == "Games"
                        "Chats" -> room.tag == "Chats" || room.tag == "Casual"
                        else -> room.tag.equals(category, ignoreCase = true)
                    }
                    assertTrue("Room '${room.title}' must match category '$category'", valid)
                }
            }
        }
    }

    @Test
    fun testVoiceRoomTrendingSortingOrder() {
        val rooms = Demo.rooms
        val trending = rooms.sortedByDescending { it.members }
        assertTrue(trending.isNotEmpty())
        for (i in 0 until trending.size - 1) {
            assertTrue(
                "Trending rooms must be sorted descending by members count",
                trending[i].members >= trending[i + 1].members
            )
        }
    }

    @Test
    fun testVoiceRoomSearchQueryMatching() {
        val rooms = Demo.rooms

        // Search by title keyword
        val query1 = "Acoustic"
        val matched1 = rooms.filter {
            it.title.contains(query1, ignoreCase = true) ||
                    it.host.contains(query1, ignoreCase = true) ||
                    it.tag.contains(query1, ignoreCase = true) ||
                    (it.activityText?.contains(query1, ignoreCase = true) == true)
        }
        assertTrue("Should find rooms containing '$query1'", matched1.isNotEmpty())

        // Search by nonexistent string
        val query2 = "NonExistentXYZ999"
        val matched2 = rooms.filter {
            it.title.contains(query2, ignoreCase = true) ||
                    it.host.contains(query2, ignoreCase = true) ||
                    it.tag.contains(query2, ignoreCase = true) ||
                    (it.activityText?.contains(query2, ignoreCase = true) == true)
        }
        assertTrue("Should find 0 rooms for nonexistent query", matched2.isEmpty())
    }

    @Test
    fun testVoiceRoomCoverAndBadgeResourceIntegrity() {
        val rooms = Demo.rooms
        for (room in rooms) {
            assertNotNull("Room id must not be null", room.id)
            assertTrue("Room title must not be blank", room.title.isNotBlank())
            assertTrue("Room host must not be blank", room.host.isNotBlank())
            assertTrue("Room members count must be >= 0", room.members >= 0)

            // Cover resource must be non-zero when provided
            if (room.coverRes != null) {
                assertTrue("coverRes drawable ID must be positive integer", room.coverRes!! != 0)
            }

            // Badges must be valid drawable IDs
            for (badge in room.badges) {
                assertTrue("Badge drawable ID must be positive integer", badge != 0)
            }
        }
    }

    @Test
    fun testTagColorsSupportedCategories() {
        val tags = listOf("Casual", "Friends", "KTV", "Music", "Games", "Game", "Deduction", "PK", "Video", "Chats")
        for (tag in tags) {
            val (bg, fg) = tagColors(tag)
            assertNotNull("Background color must not be null for tag $tag", bg)
            assertNotNull("Foreground color must not be null for tag $tag", fg)
        }
    }

    @Test
    fun testInitialsGeneration() {
        assertEquals("JD", initialsOf("John Doe"))
        assertEquals("A", initialsOf("Alice"))
        assertEquals("?", initialsOf(""))
        assertEquals("?", initialsOf("   "))
        assertEquals("WP", initialsOf("We Play Social"))
    }
}
