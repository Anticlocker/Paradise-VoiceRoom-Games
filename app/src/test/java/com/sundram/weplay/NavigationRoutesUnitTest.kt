package com.sundram.weplay

import com.sundram.weplay.data.ALL_TASKS
import com.sundram.weplay.ui.nav.Routes
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit tests verifying that all 38 application routes are valid,
 * properly configured, non-empty, and correctly mapped across all screens.
 */
class NavigationRoutesUnitTest {

    @Test
    fun testAllRouteConstantsNonEmptyAndUnique() {
        val allRoutes = listOf(
            Routes.HUB,
            Routes.ROOM,
            Routes.DISCOVER,
            Routes.MSG,
            Routes.ME,
            Routes.ROOM_DETAIL,
            Routes.LUDO,
            Routes.SPY,
            Routes.PROFILE_EDIT,
            Routes.GIFTS,
            Routes.FAMILY,
            Routes.BACKPACK,
            Routes.SETTINGS,
            Routes.ABOUT,
            Routes.ACCOUNT_SETTINGS,
            Routes.PRIVACY_SETTINGS,
            Routes.BLOCKED_USERS,
            Routes.REPORT_HISTORY,
            Routes.NOTIFICATION_SETTINGS,
            Routes.DND_SCHEDULE,
            Routes.VOICE_SETTINGS,
            Routes.GENERAL_SETTINGS,
            Routes.WALLET_SETTINGS,
            Routes.WALLET_TRANSACTIONS,
            Routes.PAYMENT_METHODS,
            Routes.HOST_EARNINGS,
            Routes.SUPPORT_SETTINGS,
            Routes.TOPUP,
            Routes.CHAT,
            Routes.USER,
            Routes.USER_DETAIL,
            Routes.RANKING,
            Routes.ME_SETTINGS,
            Routes.CREATE_ROOM,
            Routes.NOTIFICATIONS,
            Routes.SEARCH,
            Routes.GAME_ROOMS,
            Routes.TASKS,
            Routes.EVENT_CENTER,
            Routes.EVENT_DETAIL,
        )

        assertEquals("Must have exactly 40 declared routes", 40, allRoutes.size)

        // All routes must be non-blank
        for (route in allRoutes) {
            assertTrue("Route must not be blank", route.isNotBlank())
            assertFalse("Route must not contain unescaped spaces", route.contains(" "))
        }

        // All routes must be unique
        val uniqueRoutes = allRoutes.toSet()
        assertEquals("All 40 routes must have unique paths", 40, uniqueRoutes.size)
    }

    @Test
    fun testBottomBarTabs() {
        val expectedTabs = setOf(
            Routes.HUB,
            Routes.ROOM,
            Routes.DISCOVER,
            Routes.MSG,
            Routes.ME,
        )
        assertEquals("Routes.tabs must match the 5 main bottom-bar tabs", expectedTabs, Routes.tabs)
    }

    @Test
    fun testParameterizedRouteHelperFunctions() {
        val testUserId = "user_abc_123"
        val testRoomId = "room_xyz_789"

        assertEquals("chat/$testUserId", Routes.chatRoute(testUserId))
        assertEquals("user/$testUserId", Routes.userRoute(testUserId))
        assertEquals("profile/detail/$testUserId", Routes.userDetailRoute(testUserId))
        assertEquals("room/$testRoomId", Routes.roomRoute(testRoomId))
        assertEquals("room/$testRoomId", Routes.roomDetail(testRoomId))
        assertEquals(Routes.NOTIFICATIONS, Routes.notificationsRoute())
        assertEquals(Routes.SEARCH, Routes.searchRoute())
        assertEquals(Routes.TASKS, Routes.tasksRoute())
        assertEquals("event/evt_123", Routes.eventDetailRoute("evt_123"))
        assertEquals(Routes.EVENT_CENTER, Routes.eventCenterRoute())
    }

    @Test
    fun testTaskDefinitionsHaveValidRoutes() {
        val allValidRoutes = setOf(
            Routes.HUB, Routes.ROOM, Routes.DISCOVER, Routes.MSG, Routes.ME,
            Routes.LUDO, Routes.SPY, Routes.PROFILE_EDIT, Routes.GIFTS, Routes.FAMILY,
            Routes.BACKPACK, Routes.SETTINGS, Routes.ABOUT, Routes.ACCOUNT_SETTINGS,
            Routes.PRIVACY_SETTINGS, Routes.BLOCKED_USERS, Routes.REPORT_HISTORY,
            Routes.NOTIFICATION_SETTINGS, Routes.DND_SCHEDULE, Routes.VOICE_SETTINGS,
            Routes.GENERAL_SETTINGS, Routes.WALLET_SETTINGS, Routes.WALLET_TRANSACTIONS,
            Routes.PAYMENT_METHODS, Routes.HOST_EARNINGS, Routes.SUPPORT_SETTINGS,
            Routes.TOPUP, Routes.RANKING, Routes.ME_SETTINGS, Routes.CREATE_ROOM,
            Routes.NOTIFICATIONS, Routes.SEARCH, Routes.GAME_ROOMS, Routes.TASKS,
            Routes.EVENT_CENTER, Routes.EVENT_DETAIL,
        )

        for (task in ALL_TASKS) {
            assertTrue(
                "Task '${task.id}' has invalid action route: ${task.actionRoute}",
                allValidRoutes.contains(task.actionRoute),
            )
        }
    }
}
