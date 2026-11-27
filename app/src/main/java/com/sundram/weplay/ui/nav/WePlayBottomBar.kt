package com.sundram.weplay.ui.nav

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.sundram.weplay.R
import com.sundram.weplay.ui.components.UnreadBadge
import com.sundram.weplay.ui.theme.CardWhite
import com.sundram.weplay.ui.theme.CyanBrand
import com.sundram.weplay.ui.theme.Divider
import com.sundram.weplay.ui.theme.TextMain
import com.sundram.weplay.ui.theme.TextMuted

import com.sundram.weplay.ui.theme.VyboraBorder
import com.sundram.weplay.ui.theme.VyboraPurple
import com.sundram.weplay.ui.theme.VyboraSurface
import com.sundram.weplay.ui.theme.VyboraTextPrimary
import com.sundram.weplay.ui.theme.VyboraTextSecondary

object Routes {
    const val HUB = "tab_hub"
    const val ROOM = "tab_room"
    const val DISCOVER = "tab_discover"
    const val MSG = "tab_msg"
    const val ME = "tab_me"
    const val ROOM_DETAIL = "room/{roomId}"
    const val LUDO = "game/ludo"
    const val SPY = "game/spy"
    const val PROFILE_EDIT = "profile/edit"
    const val GIFTS = "profile/gifts"
    const val FAMILY = "profile/family"
    const val BACKPACK = "profile/backpack"
    const val SETTINGS = "profile/settings"
    const val ABOUT = "profile/about"
    const val ACCOUNT_SETTINGS = "settings/account"
    const val PRIVACY_SETTINGS = "settings/privacy"
    const val BLOCKED_USERS = "settings/privacy/blocked"
    const val REPORT_HISTORY = "settings/privacy/reports"
    const val NOTIFICATION_SETTINGS = "settings/notifications"
    const val DND_SCHEDULE = "settings/notifications/dnd"
    const val VOICE_SETTINGS = "settings/voice"
    const val GENERAL_SETTINGS = "settings/general"
    const val WALLET_SETTINGS = "settings/wallet"
    const val WALLET_TRANSACTIONS = "settings/wallet/transactions"
    const val PAYMENT_METHODS = "settings/wallet/payment-methods"
    const val HOST_EARNINGS = "settings/wallet/host-earnings"
    const val SUPPORT_SETTINGS = "settings/support"
    const val TOPUP = "profile/topup"
    const val CHAT = "chat/{userId}"
    const val USER = "user/{userId}"
    const val USER_DETAIL = "profile/detail/{userId}"
    const val RANKING = "tab_ranking"
    const val ME_SETTINGS = "tab_me_settings"
    const val CREATE_ROOM = "tab_create_room"
    const val NOTIFICATIONS = "notifications"
    const val SEARCH = "search"
    const val GAME_ROOMS = "game/rooms"
    const val ALL_GAMES = "game/all"
    const val TASKS = "hub/tasks"
    const val EVENT_CENTER = "events"
    const val EVENT_DETAIL = "event/{eventId}"
    const val ROOM_THEMES = "room/{roomId}/themes"
    const val ROOM_THEME_PREVIEW = "room/{roomId}/theme-preview/{themeId}"
    const val ROOM_CREST = "room/{roomId}/crest"
    const val GAMES = HUB
    const val ROOMS = ROOM
    const val SETTINGS_EDIT = PROFILE_EDIT
    const val SUPPORT = SUPPORT_SETTINGS
    const val FEEDBACK = "settings/feedback"
    const val ROOM_CREATE = CREATE_ROOM
    const val VIP = "profile/vip"
    const val SHOP = "profile/shop"
    const val MY_SPACE = "profile/myspace"
    const val NEARBY = "discover/nearby"
    const val CLUBS = "discover/clubs"
    const val FAMILY_DISCOVERY = "family/discovery"
    const val FAMILY_CREATION = "family/creation"
    const val FAMILY_DETAIL = "family/detail"
    const val FAMILY_CHAT = "family/chat"
    const val FAMILY_VOICE_ROOM = "family/voiceroom"
    const val FAMILY_MANAGEMENT = "family/management"
    const val FAMILY_LEADERBOARD = "family/leaderboard"
    const val FAMILY_SHOP = "family/shop"
    const val FAMILY_ROSTER = "family/roster"
    const val FAMILY_QUESTS = "family/quests"
    const val FRIENDS = MSG
    const val ROOM_ROUTE = ROOM_DETAIL
    const val SPLASH = "splash"
    const val AUTH = "auth"
    const val ONBOARDING = "onboarding"

    fun chatRoute(id: String) = "chat/$id"
    fun userRoute(id: String) = "user/$id"
    fun userDetailRoute(id: String) = "profile/detail/$id"
    fun roomRoute(id: String) = "room/$id"
    fun roomDetail(roomId: String) = "room/$roomId"
    fun roomThemesRoute(roomId: String) = "room/$roomId/themes"
    fun roomThemePreviewRoute(roomId: String, themeId: String) = "room/$roomId/theme-preview/$themeId"
    fun roomCrestRoute(roomId: String) = "room/$roomId/crest"
    fun eventDetailRoute(id: String) = "event/$id"
    fun notificationsRoute() = NOTIFICATIONS
    fun searchRoute() = SEARCH
    fun tasksRoute() = TASKS
    fun eventCenterRoute() = EVENT_CENTER

    val tabs = setOf(HUB, ROOM, DISCOVER, MSG, ME)
}


private data class TabItemDual(
    val route: String,
    @DrawableRes val iconOn: Int,
    @DrawableRes val iconOff: Int,
    val label: String,
    val badge: () -> Int? = { null },
)

private val tabsDual = listOf(
    TabItemDual(Routes.HUB,      R.drawable.vb_nav_games_on,   R.drawable.vb_nav_games_off,   "Games"),
    TabItemDual(Routes.ROOM,     R.drawable.vb_nav_voice_on,   R.drawable.vb_nav_voice_off,   "Voice Room"),
    TabItemDual(Routes.MSG,      R.drawable.vb_nav_chats_on,   R.drawable.vb_nav_chats_off,   "Chats"),
    TabItemDual(Routes.DISCOVER, R.drawable.vb_nav_discover_on,R.drawable.vb_nav_discover_off,"Discover"),
    TabItemDual(Routes.ME,       R.drawable.vb_nav_me_on,      R.drawable.vb_nav_me_off,      "Me"),
)

internal fun switchTab(controller: NavHostController, route: String) {
    controller.navigate(route) {
        popUpTo(controller.graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}

@Composable
fun WePlayBottomBar(nav: NavHostController, current: String?, msgBadge: Int = 0) {
    com.sundram.weplay.ui.theme.AuroraNavigationDock(
        currentRoute = current,
        onTabSelected = { route -> switchTab(nav, route) },
        msgBadgeCount = msgBadge,
        modifier = Modifier.navigationBarsPadding(),
    )
}
