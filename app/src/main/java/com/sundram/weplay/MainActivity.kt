package com.sundram.weplay

import android.app.Activity
import android.graphics.Color as AndroidColor
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.sundram.weplay.data.MessagingStore
import com.sundram.weplay.data.ProfileStore
import com.sundram.weplay.data.UserProfile
import com.sundram.weplay.ui.nav.Routes
import com.sundram.weplay.ui.nav.WePlayBottomBar
import com.sundram.weplay.ui.screens.AboutScreen
import com.sundram.weplay.ui.screens.AuthScreen
import com.sundram.weplay.ui.screens.ChatScreen
import com.sundram.weplay.ui.screens.CreateVoiceRoomScreen
import com.sundram.weplay.ui.screens.MeSettingsScreen
import com.sundram.weplay.ui.screens.RankingScreen
import com.sundram.weplay.ui.screens.UserProfileDetailScreen
import com.sundram.weplay.ui.screens.UserProfileScreen
import com.sundram.weplay.ui.screens.BackpackScreen
import com.sundram.weplay.ui.screens.DiscoverScreen
import com.sundram.weplay.ui.screens.FamilyScreen
import com.sundram.weplay.ui.screens.GiftsScreen
import com.sundram.weplay.ui.screens.LiveVoiceRoomScreen
import com.sundram.weplay.ui.screens.LudoScreen
import com.sundram.weplay.ui.screens.MeScreen
import com.sundram.weplay.ui.screens.MessagesScreen
import com.sundram.weplay.ui.screens.ProfileEditScreen
import com.sundram.weplay.ui.screens.SpyScreen
import com.sundram.weplay.ui.screens.TopUpScreen
import com.sundram.weplay.ui.screens.settings.AccountSettingsScreen
import com.sundram.weplay.ui.screens.settings.BlockedUsersScreen
import com.sundram.weplay.ui.screens.settings.DndScheduleScreen
import com.sundram.weplay.ui.screens.settings.GeneralSettingsScreen
import com.sundram.weplay.ui.screens.settings.HostEarningsScreen
import com.sundram.weplay.ui.screens.settings.NotificationSettingsScreen
import com.sundram.weplay.ui.screens.settings.PaymentMethodsScreen
import com.sundram.weplay.ui.screens.settings.PrivacySettingsScreen
import com.sundram.weplay.ui.screens.settings.ReportHistoryScreen
import com.sundram.weplay.ui.screens.settings.SettingsHomeScreen
import com.sundram.weplay.ui.screens.settings.SupportSettingsScreen
import com.sundram.weplay.ui.screens.settings.TransactionsScreen
import com.sundram.weplay.ui.screens.settings.VoiceSettingsScreen
import com.sundram.weplay.ui.screens.settings.WalletSettingsScreen
import com.sundram.weplay.ui.screens.GameLobbyScreen
import com.sundram.weplay.ui.screens.GameRoomsScreen
import com.sundram.weplay.ui.screens.NotificationsScreen
import com.sundram.weplay.ui.screens.SearchScreen
import com.sundram.weplay.ui.screens.VoiceRoomListScreen
import com.sundram.weplay.ui.theme.ThemeMode
import com.sundram.weplay.ui.theme.WePlayTheme
import com.sundram.weplay.voiceroom.VoiceRoomSessionHolder
import com.sundram.weplay.voiceroom.theme.CLASSIC_LOUNGE_ID
import com.sundram.weplay.voiceroom.theme.RoomCrestCenterScreen
import com.sundram.weplay.voiceroom.theme.RoomThemePreviewScreen
import com.sundram.weplay.voiceroom.theme.RoomThemeServiceProvider
import com.sundram.weplay.voiceroom.theme.RoomThemesGalleryScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ThemeMode.load(this)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.auto(AndroidColor.TRANSPARENT, AndroidColor.TRANSPARENT),
            navigationBarStyle = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                SystemBarStyle.auto(AndroidColor.TRANSPARENT, AndroidColor.TRANSPARENT)
            } else {
                SystemBarStyle.dark(AndroidColor.argb(0xB3, 0, 0, 0))
            },
        )
        setContent {
            val darkTheme = ThemeMode.isDark(isSystemInDarkTheme())
            WePlayTheme(darkTheme = darkTheme) {
                ApplySystemBarIcons(darkTheme)
                val context = LocalContext.current
                val store = remember { ProfileStore(context) }
                var profile by remember { mutableStateOf(store.load()) }

                val current = profile
                if (current == null) {
                    AuthScreen { created ->
                        store.save(created)
                        profile = created
                    }
                } else {
                    WePlayApp(
                        profile = current,
                        onSaveProfile = { updated ->
                            store.save(updated)
                            profile = updated
                        },
                        onLogout = {
                            store.clear()
                            ThemeMode.load(this@MainActivity)
                            profile = null
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun ApplySystemBarIcons(darkTheme: Boolean) {
    val view = LocalView.current
    if (view.isInEditMode) return
    SideEffect {
        val window = (view.context as Activity).window
        WindowCompat.getInsetsController(window, view).apply {
            isAppearanceLightStatusBars = !darkTheme
            isAppearanceLightNavigationBars = !darkTheme
        }
    }
}

@Composable
private fun WePlayApp(
    profile: UserProfile,
    onSaveProfile: (UserProfile) -> Unit,
    onLogout: () -> Unit,
) {
    val nav = rememberNavController()
    val backStack by nav.currentBackStackEntryAsState()
    val route = backStack?.destination?.route
    val showBottom = route in Routes.tabs

    val context = LocalContext.current
    val profileState = rememberUpdatedState(profile)
    val store = remember(profile.id) { MessagingStore(context) { profileState.value } }
    var msgUnread by remember(profile.id) { mutableStateOf(store.unreadTotal()) }
    val minimizedRoom by VoiceRoomSessionHolder.minimizedSummary.collectAsState()

    Scaffold(
        bottomBar = {
            Column {
                val mini = minimizedRoom
                if (mini != null && route != Routes.ROOM_DETAIL) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                            .then(if (!showBottom) Modifier.navigationBarsPadding() else Modifier)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xEE221B3A))
                            .border(1.dp, Color(0xFF4B3F75), RoundedCornerShape(16.dp))
                            .clickable { nav.navigate(Routes.roomRoute(mini.roomId)) }
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Box(
                            modifier = Modifier
                                .size(30.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF332954)),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                painter = painterResource(
                                    if (mini.selfMicOn) R.drawable.vb_vr_ic_mic else R.drawable.vb_vr_ic_mic_off
                                ),
                                contentDescription = "Active Voice Room",
                                tint = Color(0xFFF7F4FF),
                                modifier = Modifier.size(16.dp),
                            )
                        }
                        Spacer(Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = mini.title,
                                color = Color(0xFFF7F4FF),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                            Text(
                                text = "In room · ${mini.participantCount} active · Tap to return",
                                color = Color(0xFFB8B0CC),
                                fontSize = 10.5.sp,
                                maxLines = 1,
                            )
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0x33EF5578))
                                .clickable { VoiceRoomSessionHolder.leaveAndRelease() }
                                .padding(horizontal = 10.dp, vertical = 5.dp),
                        ) {
                            Text("Leave", color = Color(0xFFFF8BA3), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                if (showBottom) WePlayBottomBar(nav, route, msgBadge = msgUnread)
            }
        },
        containerColor = androidx.compose.ui.graphics.Color.Transparent,
    ) { pad ->
        NavHost(
            navController = nav,
            startDestination = Routes.HUB,
            modifier = Modifier.fillMaxSize(),
        ) {
            composable(Routes.HUB) {
                GameLobbyScreen(nav, pad, profile)
            }
            composable(Routes.ROOM) {
                VoiceRoomListScreen(nav, pad)
            }
            composable(Routes.ROOM_DETAIL) { entry ->
                LiveVoiceRoomScreen(nav, entry.arguments?.getString("roomId"), profile)
            }
            composable(Routes.DISCOVER) {
                DiscoverScreen(nav, pad)
            }
            composable(Routes.MSG) {
                MessagesScreen(nav, pad, store, onUnreadChange = { msgUnread = it })
            }
            composable(Routes.CHAT) { entry ->
                ChatScreen(
                    nav = nav,
                    userId = entry.arguments?.getString("userId") ?: "",
                    store = store,
                    profile = profile,
                    onSaveCoins = { newBal ->
                        val updated = profile.copy(coins = newBal)
                        onSaveProfile(updated)
                    },
                )
            }
            composable(Routes.USER) { entry ->
                UserProfileScreen(
                    nav,
                    entry.arguments?.getString("userId") ?: "",
                    store,
                )
            }
            composable(Routes.USER_DETAIL) { entry ->
                UserProfileDetailScreen(
                    nav = nav,
                    userId = entry.arguments?.getString("userId") ?: "",
                )
            }
            composable(Routes.RANKING) {
                RankingScreen(
                    nav = nav,
                    profile = profile,
                    onSaveProfile = onSaveProfile,
                )
            }
            composable(Routes.TASKS) {
                com.sundram.weplay.ui.screens.TaskCenterScreen(
                    nav = nav,
                    profile = profile,
                    onSaveProfile = onSaveProfile,
                )
            }
            composable(Routes.ME_SETTINGS) {
                MeSettingsScreen(nav = nav)
            }
            composable(Routes.CREATE_ROOM) {
                CreateVoiceRoomScreen(nav = nav)
            }
            composable(Routes.ME) {
                MeScreen(nav, pad, profile, onLogout, onSaveProfile)
            }
            composable(Routes.LUDO) {
                LudoScreen(nav)
            }
            composable(Routes.SPY) {
                SpyScreen(nav)
            }
            composable(Routes.PROFILE_EDIT) {
                ProfileEditScreen(nav, profile, onSaveProfile)
            }
            composable(Routes.GIFTS) {
                GiftsScreen(nav, profile, initialShowRoomThemes = false)
            }
            composable("profile/gifts/themes") {
                GiftsScreen(nav, profile, initialShowRoomThemes = true)
            }
            composable(Routes.FAMILY) {
                FamilyScreen(nav, profile, onSaveProfile)
            }
            composable(Routes.BACKPACK) {
                BackpackScreen(nav, profile, onSaveProfile)
            }
            composable(Routes.TOPUP) {
                TopUpScreen(nav, profile, onSaveProfile)
            }
            composable(Routes.SETTINGS) {
                SettingsHomeScreen(nav, profile)
            }
            composable(Routes.ACCOUNT_SETTINGS) {
                AccountSettingsScreen(nav, profile, onSaveProfile, onLogout)
            }
            composable(Routes.PRIVACY_SETTINGS) {
                PrivacySettingsScreen(nav, profile)
            }
            composable(Routes.BLOCKED_USERS) {
                BlockedUsersScreen(nav, profile)
            }
            composable(Routes.REPORT_HISTORY) {
                ReportHistoryScreen(nav, profile)
            }
            composable(Routes.NOTIFICATION_SETTINGS) {
                NotificationSettingsScreen(nav)
            }
            composable(Routes.DND_SCHEDULE) {
                DndScheduleScreen(nav)
            }
            composable(Routes.VOICE_SETTINGS) {
                VoiceSettingsScreen(nav)
            }
            composable(Routes.GENERAL_SETTINGS) {
                GeneralSettingsScreen(nav)
            }
            composable(Routes.WALLET_SETTINGS) {
                WalletSettingsScreen(nav, profile)
            }
            composable(Routes.WALLET_TRANSACTIONS) {
                TransactionsScreen(nav, profile)
            }
            composable(Routes.PAYMENT_METHODS) {
                PaymentMethodsScreen(nav, profile)
            }
            composable(Routes.HOST_EARNINGS) {
                HostEarningsScreen(nav)
            }
            composable(Routes.SUPPORT_SETTINGS) {
                SupportSettingsScreen(nav, profile)
            }
            composable(Routes.ABOUT) {
                AboutScreen(nav)
            }
            composable(Routes.NOTIFICATIONS) {
                NotificationsScreen(nav)
            }
            composable(Routes.SEARCH) {
                SearchScreen(nav)
            }
            composable(Routes.GAME_ROOMS) {
                GameRoomsScreen(nav)
            }
            composable(Routes.EVENT_CENTER) {
                com.sundram.weplay.ui.screens.EventCenterScreen(nav = nav)
            }
            composable(Routes.EVENT_DETAIL) { entry ->
                com.sundram.weplay.ui.screens.EventDetailScreen(
                    eventId = entry.arguments?.getString("eventId") ?: "",
                    nav = nav,
                )
            }
            composable(Routes.ROOM_THEMES) { entry ->
                val targetRoomId = entry.arguments?.getString("roomId") ?: "r1"
                RoomThemesGalleryScreen(
                    roomId = targetRoomId,
                    profile = profile,
                    isHostOrAuthorized = true,
                    onOpenPreview = { themeId ->
                        nav.navigate(Routes.roomThemePreviewRoute(targetRoomId, themeId))
                    },
                    onOpenCrestCenter = {
                        nav.navigate(Routes.roomCrestRoute(targetRoomId))
                    },
                    onEquipFeedback = { },
                    onClose = { nav.popBackStack() },
                )
            }
            composable(Routes.ROOM_THEME_PREVIEW) { entry ->
                val targetRoomId = entry.arguments?.getString("roomId") ?: "r1"
                val themeId = entry.arguments?.getString("themeId") ?: CLASSIC_LOUNGE_ID
                RoomThemePreviewScreen(
                    themeId = themeId,
                    roomId = targetRoomId,
                    profile = profile,
                    isHostOrAuthorized = true,
                    onClose = { nav.popBackStack() },
                    onEquippedSuccess = {
                        nav.popBackStack()
                    },
                    onOpenCrestCenter = {
                        nav.navigate(Routes.roomCrestRoute(targetRoomId))
                    },
                )
            }
            composable(Routes.ROOM_CREST) { entry ->
                val targetRoomId = entry.arguments?.getString("roomId") ?: "r1"
                val created = RoomThemeServiceProvider.repository(context).getCreatedRooms()
                    .firstOrNull { it.id == targetRoomId }
                val demoRoom = com.sundram.weplay.data.Demo.rooms.firstOrNull { it.id == targetRoomId }
                RoomCrestCenterScreen(
                    roomId = targetRoomId,
                    roomName = created?.title ?: demoRoom?.title ?: "Voice Lounge",
                    hostId = "ghost-host",
                    currentUserId = profile.id,
                    isHostOrMod = true,
                    onPreviewTheme = { themeId ->
                        nav.navigate(Routes.roomThemePreviewRoute(targetRoomId, themeId))
                    },
                    onClose = { nav.popBackStack() },
                )
            }
        }
    }
}
