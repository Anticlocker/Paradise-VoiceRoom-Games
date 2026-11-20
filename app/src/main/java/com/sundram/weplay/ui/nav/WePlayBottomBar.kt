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
    const val TASKS = "hub/tasks"

    fun chatRoute(id: String) = "chat/$id"
    fun userRoute(id: String) = "user/$id"
    fun userDetailRoute(id: String) = "profile/detail/$id"
    fun roomRoute(id: String) = "room/$id"
    fun notificationsRoute() = NOTIFICATIONS
    fun searchRoute() = SEARCH
    fun tasksRoute() = TASKS

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
    val navBarBg = VyboraSurface
    val inactiveColor = VyboraTextSecondary
    val activeLabelColor = VyboraPurple
    val badgeRed = Color(0xFFFF4D4F)

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = navBarBg,
        tonalElevation = 0.dp,
    ) {
        Column(Modifier.navigationBarsPadding()) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(VyboraBorder),
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .padding(horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                tabsDual.forEach { tab ->
                    val selected = current == tab.route
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .height(60.dp)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                            ) {
                                if (current != tab.route) {
                                    switchTab(nav, tab.route)
                                }
                            },
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center,
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                painter = painterResource(if (selected) tab.iconOn else tab.iconOff),
                                contentDescription = tab.label,
                                tint = Color.Unspecified,
                                modifier = Modifier.size(26.dp),
                            )
                            // Chats badge with count
                            if (tab.route == Routes.MSG) {
                                val badgeCount = msgBadge
                                if (badgeCount > 0) {
                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.TopEnd)
                                            .offset(x = 10.dp, y = (-4).dp)
                                            .size(16.dp)
                                            .background(badgeRed, CircleShape),
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        Text(
                                            text = "$badgeCount",
                                            color = Color.White,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                        )
                                    }
                                }
                            }
                            // Discover and Me red dot badges
                            if (tab.route == Routes.DISCOVER || tab.route == Routes.ME) {
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .offset(x = 4.dp, y = (-2).dp)
                                        .size(7.dp)
                                        .background(badgeRed, CircleShape),
                                )
                            }
                        }
                        Spacer(Modifier.height(3.dp))
                        Text(
                            text = tab.label,
                            color = if (selected) activeLabelColor else inactiveColor,
                            fontSize = 11.sp,
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                        )
                    }
                }
            }
        }
    }
}
