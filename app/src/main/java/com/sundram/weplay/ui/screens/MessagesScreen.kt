package com.sundram.weplay.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.sundram.weplay.R
import com.sundram.weplay.data.AppNotification
import com.sundram.weplay.data.Chat
import com.sundram.weplay.data.DirectoryUser
import com.sundram.weplay.data.GroupInfo
import com.sundram.weplay.data.MessagingStore
import com.sundram.weplay.data.NotificationKind
import com.sundram.weplay.data.RoomCard
import com.sundram.weplay.ui.components.GiftPackOffersSheet
import com.sundram.weplay.ui.components.bounceClick
import com.sundram.weplay.ui.components.navigateSafe
import com.sundram.weplay.ui.nav.Routes
import com.sundram.weplay.ui.theme.AuroraBackground
import com.sundram.weplay.ui.theme.AuroraBorder
import com.sundram.weplay.ui.theme.AuroraChatBackground
import com.sundram.weplay.ui.theme.AuroraCoral
import com.sundram.weplay.ui.theme.AuroraCyan
import com.sundram.weplay.ui.theme.AuroraElevated
import com.sundram.weplay.ui.theme.AuroraGold
import com.sundram.weplay.ui.theme.AuroraGradients
import com.sundram.weplay.ui.theme.AuroraLavender
import com.sundram.weplay.ui.theme.AuroraOrchid
import com.sundram.weplay.ui.theme.AuroraRose
import com.sundram.weplay.ui.theme.AuroraSurface
import com.sundram.weplay.ui.theme.AuroraTextPrimary
import com.sundram.weplay.ui.theme.AuroraTextSecondary
import com.sundram.weplay.ui.theme.CelestialNavy
import com.sundram.weplay.ui.theme.CelestialViolet
import com.sundram.weplay.ui.theme.ChampagneGold
import com.sundram.weplay.ui.theme.CloudWhite
import com.sundram.weplay.ui.theme.CrystalBlue
import com.sundram.weplay.ui.theme.DeepInk
import com.sundram.weplay.ui.theme.DeepMidnight
import com.sundram.weplay.ui.theme.DreamLavender
import com.sundram.weplay.ui.theme.KtvRed
import com.sundram.weplay.ui.theme.LiveRed
import com.sundram.weplay.ui.theme.MintGreen
import com.sundram.weplay.ui.theme.MysticPurple
import com.sundram.weplay.ui.theme.RoyalGold
import com.sundram.weplay.ui.theme.RoyalViolet
import com.sundram.weplay.ui.theme.SoftGold
import com.sundram.weplay.ui.theme.SoftPeach
import com.sundram.weplay.ui.theme.SunsetRose

// ─── Chat filter type ────────────────────────────────────────────────────────

private enum class ChatFilter { ALL, UNREAD, GROUPS, FRIENDS }

// ─── Presence filter type ─────────────────────────────────────────────────────

private enum class PresenceFilter { ONLINE, IN_ROOMS, RECENT, FAVORITES }

// ─── Mascot avatar mapping ────────────────────────────────────────────────────

private val NOVA_AVATARS = listOf(
    R.drawable.vb_chat_avatar_01,
    R.drawable.vb_chat_avatar_02,
    R.drawable.vb_chat_avatar_03,
    R.drawable.vb_chat_avatar_04,
    R.drawable.vb_chat_avatar_05,
    R.drawable.vb_chat_avatar_06,
    R.drawable.vb_chat_avatar_07,
    R.drawable.vb_chat_avatar_08,
    R.drawable.vb_chat_avatar_09,
    R.drawable.vb_chat_avatar_10,
    R.drawable.vb_chat_avatar_11,
    R.drawable.vb_chat_avatar_12,
)

/** Returns one of the 12 original Nova mascot drawables for the given stable [id]. */
fun novaAvatarRes(id: String): Int {
    if (NOVA_AVATARS.isEmpty()) return R.drawable.vb_chat_avatar_01
    val hash = id.hashCode() and 0x7FFFFFFF
    val index = hash % NOVA_AVATARS.size
    return NOVA_AVATARS[index]
}

// ─────────────────────────────────────────────────────────────────────────────
// MAIN SCREEN: Full Celestial Messages & Social Lounge
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun MessagesScreen(
    nav: NavHostController,
    pad: PaddingValues,
    store: MessagingStore,
    onUnreadChange: (Int) -> Unit = {},
) {
    // ── State ─────────────────────────────────────────────────────────────────
    var version by remember { mutableStateOf(0) }
    var query by remember { mutableStateOf("") }
    var filter by remember { mutableStateOf(ChatFilter.ALL) }
    var presenceFilter by remember { mutableStateOf(PresenceFilter.ONLINE) }

    var confirmDecline by remember { mutableStateOf<DirectoryUser?>(null) }
    var previewRoom by remember { mutableStateOf<RoomCard?>(null) }

    var showGiftPackSheet by remember { mutableStateOf(false) }
    var showContactsSheet by remember { mutableStateOf(false) }
    var showNewChatSheet by remember { mutableStateOf(false) }
    var showNewGroupSheet by remember { mutableStateOf(false) }
    var showInboxSheet by remember { mutableStateOf(false) }
    var showArchivedSheet by remember { mutableStateOf(false) }

    val listState = rememberLazyListState()

    fun bump() { version++ }

    // Seed demo data once
    LaunchedEffect(Unit) {
        store.seedIfEmpty()
        bump()
    }

    // ── Data snapshot ────────────────────────────────────────────────────────
    val snapshot = remember(version) {
        object {
            val chats        = store.allChats()
            val friends      = store.friends()
            val incoming     = store.incomingRequests()
            val outgoing     = store.outgoingRequests()
            val blockedList  = store.blockedUsers()
            val notifs       = store.notifications()
            val unreadNotifs = store.unreadNotifications()
            val unreadTotal  = store.unreadTotal()
            val inRoomUsers  = store.usersInVoiceRooms()
            val groups       = store.allGroups()
        }
    }

    val allChats      = snapshot.chats
    val friends       = snapshot.friends
    val onlineFriends = remember(snapshot) { friends.filter { it.online } }
    val inRoomFriends = remember(snapshot) { snapshot.inRoomUsers }

    LaunchedEffect(snapshot.unreadTotal) {
        onUnreadChange(snapshot.unreadTotal)
    }

    // ── Filtered chat list ───────────────────────────────────────────────────
    val filteredChats = remember(allChats, query, filter) {
        val base = if (query.isBlank()) allChats
        else allChats.filter { chat ->
            val user = store.userById(chat.userId)
            val gInfo = if (chat.isGroup) store.groupInfo(chat.userId) else null
            val name = gInfo?.name ?: (user?.name ?: chat.userId)
            val alias = store.getAlias(chat.userId) ?: ""
            name.contains(query, ignoreCase = true) ||
                alias.contains(query, ignoreCase = true) ||
                chat.displayText.contains(query, ignoreCase = true)
        }
        when (filter) {
            ChatFilter.ALL     -> base
            ChatFilter.UNREAD  -> base.filter { it.unread > 0 }
            ChatFilter.GROUPS  -> base.filter { it.isGroup }
            ChatFilter.FRIENDS -> base.filter { !it.isGroup && store.relationship(it.userId) == "friend" }
        }
    }

    // ── Root Celestial Canvas ────────────────────────────────────────────────
    AuroraChatBackground(
        modifier = Modifier.fillMaxSize(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(bottom = pad.calculateBottomPadding()),
        ) {
            // ── Section 1: Full Celestial Chats Header ────────────────────────
            NovaChatsHeader(
                unreadTotal = snapshot.unreadTotal,
                unreadNotifs = snapshot.unreadNotifs,
                onContacts = { showContactsSheet = true },
                onNewChat = { showNewChatSheet = true },
                onInbox = { showInboxSheet = true },
                onMarkAllRead = {
                    store.markAllChatsRead()
                    bump()
                },
                onCreateGroup = { showNewGroupSheet = true },
                onArchived = { showArchivedSheet = true },
                onSettings = { nav.navigate(Routes.SETTINGS) },
            )

            // ── Section 2: Glossy Celestial Search Bar ───────────────────────
            NovaSearchBar(
                query = query,
                onQueryChange = { query = it },
                onClear = { query = "" },
            )

            // ── Main Scrollable Feed ──────────────────────────────────────────
            LazyColumn(
                state = listState,
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize(),
            ) {
                // ── Section 3: Social Presence (Online & In Voice Rooms) ───────
                if (query.isBlank()) {
                    item {
                        SocialPresenceSection(
                            selectedFilter = presenceFilter,
                            onlineCount = onlineFriends.size,
                            inRoomCount = inRoomFriends.size,
                            recentCount = friends.size,
                            onFilterSelect = { presenceFilter = it },
                            users = when (presenceFilter) {
                                PresenceFilter.ONLINE    -> onlineFriends
                                PresenceFilter.IN_ROOMS  -> inRoomFriends
                                PresenceFilter.RECENT    -> friends
                                PresenceFilter.FAVORITES -> friends.take(4)
                            },
                            store = store,
                            onUserClick = { u -> nav.navigateSafe(Routes.userRoute(u.id)) },
                            onRoomClick = { room -> previewRoom = room },
                        )
                    }
                }

                // ── Section 5: Filter Pills (All | Unread | Groups | Friends) ──
                item {
                    NovaFilterChips(
                        selected = filter,
                        unreadCount = allChats.count { it.unread > 0 },
                        groupsCount = allChats.count { it.isGroup },
                        friendCount = allChats.count { !it.isGroup && store.relationship(it.userId) == "friend" },
                        onSelect = { filter = it },
                    )
                }

                // ── Section 6: Conversation Rows or Empty State ───────────────
                if (query.isNotBlank() && filteredChats.isEmpty()) {
                    item {
                        NovaEmptySearch(query = query)
                    }
                } else if (filteredChats.isEmpty() && query.isBlank()) {
                    item {
                        NovaEmptyState(
                            filter = filter,
                            onStartChat = { showNewChatSheet = true },
                            onFindFriends = { showContactsSheet = true },
                            onCreateGroup = { showNewGroupSheet = true },
                        )
                    }
                } else {
                    itemsIndexed(filteredChats, key = { index, chat -> "${chat.userId}_$index" }) { _, chat ->
                        val user = if (!chat.isGroup) store.userById(chat.userId) else null
                        val group = if (chat.isGroup) store.groupInfo(chat.userId) else null
                        val activeRoom = if (!chat.isGroup) store.userActiveRoom(chat.userId) else null

                        NovaChatRow(
                            chat = chat,
                            user = user,
                            group = group,
                            alias = store.getAlias(chat.userId),
                            activeRoom = activeRoom,
                            onClick = { nav.navigateSafe(Routes.chatRoute(chat.userId)) },
                            onPin = { store.setPinned(chat.userId, !chat.pinned); bump() },
                            onMute = { store.setMuted(chat.userId, !chat.muted); bump() },
                            onArchive = { store.archiveChat(chat.userId, true); bump() },
                            onDelete = { store.deleteChat(chat.userId); bump() },
                            onRoomPreview = { r -> previewRoom = r },
                        )
                    }
                }

                // ── Section 7: Discover People Strip (when browsing All) ───────
                if (query.isBlank() && filter == ChatFilter.ALL) {
                    val quick = store.allUsers()
                        .filter { !store.isMe(it.id) && !store.isBlocked(it.id) }
                        .take(5)
                    if (quick.isNotEmpty()) {
                        item {
                            Spacer(Modifier.height(8.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 4.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Image(
                                    painter = painterResource(R.drawable.aurora3_ic_nav_star),
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                )
                                Spacer(Modifier.width(6.dp))
                                Text(
                                    "Discover People",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = CloudWhite,
                                    modifier = Modifier.weight(1f),
                                )
                                Text(
                                    "See All",
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CrystalBlue,
                                    modifier = Modifier
                                        .bounceClick { showContactsSheet = true }
                                        .padding(4.dp),
                                )
                            }
                        }
                        itemsIndexed(quick, key = { index, it -> "q_${it.id}_$index" }) { _, u ->
                            NovaPeopleRow(
                                user = u,
                                rel = store.relationship(u.id),
                                onClick = { nav.navigateSafe(Routes.userRoute(u.id)) },
                                onAction = {
                                    if (store.isFriend(u.id)) {
                                        nav.navigateSafe(Routes.chatRoute(u.id))
                                    } else {
                                        store.sendRequest(u.id)
                                        bump()
                                    }
                                },
                            )
                        }
                    }
                }
            }
        }

        // ── Floating Gift Pack Widget (Matches VoiceRoomListScreen) ───────────
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = pad.calculateBottomPadding() + 16.dp),
        ) {
            CompactChatGiftPackWidget(onClick = { showGiftPackSheet = true })
        }
    }

    // ── 48-Hour Rotating Gift Pack Bottom Sheet ──────────────────────────────
    if (showGiftPackSheet) {
        GiftPackOffersSheet(
            userId = store.meId(),
            onDismiss = { showGiftPackSheet = false },
            onOpenTopUp = {
                showGiftPackSheet = false
                nav.navigateSafe(Routes.TOPUP)
            },
        )
    }

    // ── Contacts / Friends Sheet ─────────────────────────────────────────────
    if (showContactsSheet) {
        ContactsBottomSheet(
            friends = friends,
            onDismiss = { showContactsSheet = false },
            onChat = { uid ->
                showContactsSheet = false
                nav.navigateSafe(Routes.chatRoute(uid))
            },
            onViewProfile = { uid ->
                showContactsSheet = false
                nav.navigateSafe(Routes.userRoute(uid))
            },
        )
    }

    // ── New Chat / Search People Sheet ───────────────────────────────────────
    if (showNewChatSheet) {
        NewChatBottomSheet(
            users = store.allUsers().filter { !store.isMe(it.id) && !store.isBlocked(it.id) },
            onDismiss = { showNewChatSheet = false },
            onSelectUser = { uid ->
                showNewChatSheet = false
                nav.navigateSafe(Routes.chatRoute(uid))
            },
        )
    }

    // ── New Group Creation Sheet ─────────────────────────────────────────────
    if (showNewGroupSheet) {
        NewGroupBottomSheet(
            friends = friends,
            onDismiss = { showNewGroupSheet = false },
            onCreate = { name, selectedIds, desc, avatarRes ->
                val newGroup = store.createGroup(name, selectedIds, desc, avatarRes)
                showNewGroupSheet = false
                nav.navigateSafe(Routes.chatRoute(newGroup.id))
                bump()
            },
        )
    }

    // ── Inbox & Communication Notices Sheet ──────────────────────────────────
    if (showInboxSheet) {
        InboxBottomSheet(
            store = store,
            notifications = snapshot.notifs,
            incomingRequests = snapshot.incoming,
            onDismiss = { showInboxSheet = false },
            onAcceptFriend = { uid ->
                store.acceptRequest(uid)
                bump()
            },
            onDeclineFriend = { uid ->
                confirmDecline = store.userById(uid)
            },
            onJoinRoom = { roomId ->
                showInboxSheet = false
                nav.navigateSafe(Routes.roomRoute(roomId))
            },
            onOpenChat = { uid ->
                showInboxSheet = false
                nav.navigateSafe(Routes.chatRoute(uid))
            },
        )
    }

    // ── Archived Chats Sheet ─────────────────────────────────────────────────
    if (showArchivedSheet) {
        ArchivedChatsBottomSheet(
            archived = store.archivedChats(),
            store = store,
            onDismiss = { showArchivedSheet = false },
            onUnarchive = { uid ->
                store.archiveChat(uid, false)
                bump()
            },
            onOpenChat = { uid ->
                showArchivedSheet = false
                nav.navigateSafe(Routes.chatRoute(uid))
                bump()
            },
        )
    }

    // ── Room Preview Modal ───────────────────────────────────────────────────
    previewRoom?.let { room ->
        RoomPreviewModal(
            room = room,
            onDismiss = { previewRoom = null },
            onJoin = {
                val rid = room.id
                previewRoom = null
                nav.navigateSafe(Routes.roomRoute(rid))
            },
        )
    }

    // ── Confirm Decline Friend Request Dialog ────────────────────────────────
    confirmDecline?.let { u ->
        AlertDialog(
            onDismissRequest = { confirmDecline = null },
            title = { Text("Decline friend request?", color = CloudWhite, fontWeight = FontWeight.Bold) },
            text = { Text("Remove request from ${u.name}?", color = DreamLavender) },
            confirmButton = {
                TextButton(onClick = {
                    store.declineRequest(u.id)
                    confirmDecline = null
                    bump()
                }) {
                    Text("Decline", color = LiveRed, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { confirmDecline = null }) {
                    Text("Cancel", color = DreamLavender)
                }
            },
            containerColor = DeepInk,
            shape = RoundedCornerShape(20.dp),
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// 1. Full Celestial Chats Header (Emblem + Title + Badges + Actions)
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun NovaChatsHeader(
    unreadTotal: Int,
    unreadNotifs: Int,
    onContacts: () -> Unit,
    onNewChat: () -> Unit,
    onInbox: () -> Unit,
    onMarkAllRead: () -> Unit,
    onCreateGroup: () -> Unit,
    onArchived: () -> Unit,
    onSettings: () -> Unit,
) {
    var menuOpen by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Gold / Violet Chat Emblem
        Image(
            painter = painterResource(R.drawable.ap_ic_chat),
            contentDescription = null,
            modifier = Modifier.size(38.dp),
            contentScale = ContentScale.Fit,
        )

        Spacer(Modifier.width(10.dp))

        // Title + Subtitle + Glowing Unread Counter
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Chats",
                    color = CloudWhite,
                    fontSize = 25.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = (-0.3).sp,
                )
                if (unreadTotal > 0) {
                    Spacer(Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(AuroraGradients.sunsetFantasy)
                            .border(0.8.dp, CloudWhite.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 7.dp, vertical = 1.5.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = if (unreadTotal > 99) "99+" else "$unreadTotal",
                            color = CloudWhite,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                        )
                    }
                }
            }
            Spacer(Modifier.height(1.dp))
            Text(
                text = "Connect",
                color = DreamLavender,
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Medium,
            )
        }

        Spacer(Modifier.width(8.dp))

        // Right Action Group
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            // Contacts / Friends circular button
            HeaderCircularButton(
                icon = R.drawable.vb_chat_ic_contacts,
                desc = "Contacts",
                onClick = onContacts,
            )

            // Inbox / Alerts button with glowing red badge
            Box {
                HeaderCircularButton(
                    icon = R.drawable.vb_chat_ic_notification,
                    desc = "Inbox",
                    onClick = onInbox,
                )
                if (unreadNotifs > 0) {
                    Box(
                        Modifier
                            .align(Alignment.TopEnd)
                            .offset(x = 2.dp, y = (-2).dp)
                            .size(10.dp)
                            .background(LiveRed, CircleShape)
                            .border(1.5.dp, DeepInk, CircleShape),
                    )
                }
            }

            // More Options Dropdown
            Box {
                HeaderCircularButton(
                    icon = R.drawable.vb_chat_ic_more,
                    desc = "More options",
                    onClick = { menuOpen = true },
                )
                DropdownMenu(
                    expanded = menuOpen,
                    onDismissRequest = { menuOpen = false },
                    modifier = Modifier
                        .background(DeepInk)
                        .border(1.dp, DreamLavender.copy(alpha = 0.35f), RoundedCornerShape(16.dp)),
                ) {
                    DropdownMenuItem(
                        text = { Text("Mark all as read", color = CloudWhite, fontSize = 13.5.sp, fontWeight = FontWeight.SemiBold) },
                        leadingIcon = {
                            Icon(
                                painter = painterResource(R.drawable.vb_chat_ic_check),
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                                tint = CrystalBlue,
                            )
                        },
                        onClick = { menuOpen = false; onMarkAllRead() },
                    )
                    DropdownMenuItem(
                        text = { Text("New Group Chat", color = CloudWhite, fontSize = 13.5.sp, fontWeight = FontWeight.SemiBold) },
                        leadingIcon = {
                            Icon(
                                painter = painterResource(R.drawable.vb_chat_ic_groups),
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                                tint = SunsetRose,
                            )
                        },
                        onClick = { menuOpen = false; onCreateGroup() },
                    )
                    DropdownMenuItem(
                        text = { Text("Archived Chats", color = CloudWhite, fontSize = 13.5.sp, fontWeight = FontWeight.SemiBold) },
                        leadingIcon = {
                            Icon(
                                painter = painterResource(R.drawable.vb_chat_ic_archive),
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                                tint = RoyalGold,
                            )
                        },
                        onClick = { menuOpen = false; onArchived() },
                    )
                    DropdownMenuItem(
                        text = { Text("Chat Settings", color = CloudWhite, fontSize = 13.5.sp, fontWeight = FontWeight.SemiBold) },
                        leadingIcon = {
                            Icon(
                                painter = painterResource(R.drawable.vb_chat_ic_privacy),
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                                tint = DreamLavender,
                            )
                        },
                        onClick = { menuOpen = false; onSettings() },
                    )
                }
            }

            // "+ New Chat" pill button (matching "+ Create Room" in VoiceLoungeHeader)
            Row(
                modifier = Modifier
                    .height(38.dp)
                    .shadow(3.dp, RoundedCornerShape(19.dp))
                    .clip(RoundedCornerShape(19.dp))
                    .background(AuroraGradients.sunsetFantasy)
                    .border(1.dp, Color.White.copy(alpha = 0.45f), RoundedCornerShape(19.dp))
                    .bounceClick(onClick = onNewChat)
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_plus),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(14.dp),
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = "New",
                    color = Color.White,
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

@Composable
private fun HeaderCircularButton(
    icon: Int,
    desc: String,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(38.dp)
            .shadow(2.dp, CircleShape)
            .clip(CircleShape)
            .background(Brush.verticalGradient(listOf(Color(0xFF2E1B5B), Color(0xFF1F1243))))
            .border(1.2.dp, DreamLavender.copy(alpha = 0.45f), CircleShape)
            .bounceClick(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = desc,
            tint = CloudWhite,
            modifier = Modifier.size(18.dp),
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// 2. Glossy Celestial Search Bar (Matches VoiceSearchBar)
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun NovaSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onClear: () -> Unit,
) {
    val focusManager = LocalFocusManager.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .height(42.dp)
                .clip(RoundedCornerShape(21.dp))
                .background(DeepInk.copy(alpha = 0.85f))
                .border(1.dp, DreamLavender.copy(alpha = 0.35f), RoundedCornerShape(21.dp))
                .padding(horizontal = 14.dp),
            contentAlignment = Alignment.CenterStart,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(R.drawable.ic_search),
                    contentDescription = null,
                    tint = CrystalBlue,
                    modifier = Modifier.size(17.dp),
                )
                Spacer(Modifier.width(10.dp))
                BasicTextField(
                    value = query,
                    onValueChange = onQueryChange,
                    singleLine = true,
                    textStyle = TextStyle(color = CloudWhite, fontSize = 13.5.sp),
                    cursorBrush = SolidColor(SunsetRose),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
                    modifier = Modifier.weight(1f),
                    decorationBox = { inner ->
                        if (query.isEmpty()) {
                            Text(
                                "Search messages, friends or groups…",
                                color = DreamLavender.copy(alpha = 0.65f),
                                fontSize = 13.5.sp,
                            )
                        }
                        inner()
                    },
                )
                if (query.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .size(22.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF2E1B5B))
                            .clickable(onClick = onClear),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_close),
                            contentDescription = "Clear search",
                            tint = CloudWhite,
                            modifier = Modifier.size(12.dp),
                        )
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// 3. Social Presence Section (Live Rings, Voice Pulse & Activity)
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun SocialPresenceSection(
    selectedFilter: PresenceFilter,
    onlineCount: Int,
    inRoomCount: Int,
    recentCount: Int,
    onFilterSelect: (PresenceFilter) -> Unit,
    users: List<DirectoryUser>,
    store: MessagingStore,
    onUserClick: (DirectoryUser) -> Unit,
    onRoomClick: (RoomCard) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
    ) {
        // Presence Filter Pills
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 2.dp),
        ) {
            item {
                PresenceFilterPill(
                    label = "Online ($onlineCount)",
                    selected = selectedFilter == PresenceFilter.ONLINE,
                    onClick = { onFilterSelect(PresenceFilter.ONLINE) },
                )
            }
            item {
                PresenceFilterPill(
                    label = "In Rooms ($inRoomCount)",
                    selected = selectedFilter == PresenceFilter.IN_ROOMS,
                    indicatorColor = LiveRed,
                    onClick = { onFilterSelect(PresenceFilter.IN_ROOMS) },
                )
            }
            item {
                PresenceFilterPill(
                    label = "Recent ($recentCount)",
                    selected = selectedFilter == PresenceFilter.RECENT,
                    onClick = { onFilterSelect(PresenceFilter.RECENT) },
                )
            }
            item {
                PresenceFilterPill(
                    label = "Favorites",
                    selected = selectedFilter == PresenceFilter.FAVORITES,
                    onClick = { onFilterSelect(PresenceFilter.FAVORITES) },
                )
            }
        }

        Spacer(Modifier.height(10.dp))

        // Avatar Feed Row
        if (users.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.CenterStart,
            ) {
                Text(
                    text = when (selectedFilter) {
                        PresenceFilter.IN_ROOMS -> "No friends currently in voice rooms"
                        PresenceFilter.ONLINE   -> "No friends online right now"
                        else                    -> "No contacts found in this section"
                    },
                    fontSize = 12.sp,
                    color = DreamLavender,
                )
            }
        } else {
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = PaddingValues(horizontal = 2.dp),
            ) {
                items(users, key = { it.id }) { u ->
                    val activeRoom = store.userActiveRoom(u.id)
                    SocialPresenceAvatar(
                        user = u,
                        activeRoom = activeRoom,
                        onAvatarClick = { onUserClick(u) },
                        onRoomClick = { activeRoom?.let { r -> onRoomClick(r) } },
                    )
                }
            }
        }
    }
}

@Composable
private fun PresenceFilterPill(
    label: String,
    selected: Boolean,
    indicatorColor: Color? = null,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(
                if (selected) AuroraGradients.sunsetFantasy
                else Brush.linearGradient(listOf(Color(0xFF221644), Color(0xFF170E33)))
            )
            .border(
                1.dp,
                if (selected) SunsetRose.copy(alpha = 0.85f) else Color(0xFF4C3775),
                RoundedCornerShape(16.dp),
            )
            .bounceClick(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 7.dp),
        contentAlignment = Alignment.Center,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (indicatorColor != null) {
                Box(
                    Modifier
                        .size(6.dp)
                        .background(if (selected) Color.White else indicatorColor, CircleShape)
                )
                Spacer(Modifier.width(6.dp))
            }
            Text(
                text = label,
                color = if (selected) Color.White else Color(0xFFD6C7F8),
                fontSize = 12.5.sp,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.SemiBold,
            )
        }
    }
}

@Composable
private fun SocialPresenceAvatar(
    user: DirectoryUser,
    activeRoom: RoomCard?,
    onAvatarClick: () -> Unit,
    onRoomClick: () -> Unit,
) {
    val isInRoom = activeRoom != null

    // Refined premium breathing shimmer animation for live presence
    val infiniteTransition = rememberInfiniteTransition(label = "presenceAura")
    val shimmerAlpha by infiniteTransition.animateFloat(
        initialValue = 0.65f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "shimmerAlpha",
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(72.dp),
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .bounceClick(scaleDown = 0.94f, onClick = onAvatarClick),
            contentAlignment = Alignment.Center,
        ) {
            if (isInRoom) {
                // High-end dual-tone celestial ring with breathing ambient glow
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .background(
                            Brush.sweepGradient(
                                listOf(
                                    SunsetRose.copy(alpha = shimmerAlpha),
                                    MysticPurple,
                                    CrystalBlue.copy(alpha = shimmerAlpha),
                                    RoyalGold,
                                    SunsetRose.copy(alpha = shimmerAlpha),
                                )
                            )
                        )
                )
                // Crisp dark gap
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(DeepInk)
                )
            } else if (user.online) {
                // Sleek emerald/cyan metallic bezel
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .background(
                            Brush.sweepGradient(
                                listOf(
                                    MintGreen.copy(alpha = shimmerAlpha),
                                    CrystalBlue,
                                    MintGreen.copy(alpha = shimmerAlpha),
                                )
                            )
                        )
                )
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(DeepInk)
                )
            } else {
                // Subtle polished border
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .border(1.2.dp, DreamLavender.copy(alpha = 0.35f), CircleShape)
                )
            }

            // Inner avatar image
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                NovaAvatarImage(id = user.id, size = 52.dp)
            }

            // Status Indicator Badge at bottom-end
            if (isInRoom) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .offset(x = 2.dp, y = 2.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(AuroraGradients.sunsetFantasy)
                        .border(1.2.dp, DeepInk, RoundedCornerShape(8.dp))
                        .clickable(onClick = onRoomClick)
                        .padding(horizontal = 4.dp, vertical = 1.5.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.size(4.dp).background(Color.White, CircleShape))
                        Spacer(Modifier.width(2.5.dp))
                        Text(
                            text = "LIVE",
                            color = Color.White,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Black,
                        )
                    }
                }
            } else if (user.online) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(13.dp)
                        .clip(CircleShape)
                        .background(MintGreen)
                        .border(2.dp, DeepInk, CircleShape),
                )
            }
        }

        Spacer(Modifier.height(6.dp))

        // User Name
        Text(
            text = user.name,
            color = CloudWhite,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )

        // Room activity label if live, or status
        Spacer(Modifier.height(2.dp))
        if (isInRoom) {
            Text(
                text = activeRoom.title,
                color = SunsetRose,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onRoomClick),
            )
        } else {
            Text(
                text = if (user.online) "Online" else "Recent",
                color = if (user.online) MintGreen.copy(alpha = 0.9f) else DreamLavender.copy(alpha = 0.55f),
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// 5. Filter Chips (All | Unread | Groups | Friends)
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun NovaFilterChips(
    selected: ChatFilter,
    unreadCount: Int,
    groupsCount: Int,
    friendCount: Int,
    onSelect: (ChatFilter) -> Unit,
) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 2.dp),
    ) {
        item {
            NovaChip(
                label = "All",
                selected = selected == ChatFilter.ALL,
                onClick = { onSelect(ChatFilter.ALL) },
            )
        }
        item {
            NovaChip(
                label = "Unread",
                count = unreadCount,
                selected = selected == ChatFilter.UNREAD,
                onClick = { onSelect(ChatFilter.UNREAD) },
            )
        }
        item {
            NovaChip(
                label = "Groups",
                count = groupsCount,
                selected = selected == ChatFilter.GROUPS,
                onClick = { onSelect(ChatFilter.GROUPS) },
            )
        }
        item {
            NovaChip(
                label = "Friends",
                count = friendCount,
                selected = selected == ChatFilter.FRIENDS,
                onClick = { onSelect(ChatFilter.FRIENDS) },
            )
        }
    }
}

@Composable
private fun NovaChip(
    label: String,
    count: Int = 0,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(18.dp))
            .background(
                if (selected) AuroraGradients.sunsetFantasy
                else Brush.linearGradient(listOf(Color(0xFF221644), Color(0xFF170E33)))
            )
            .border(
                1.dp,
                if (selected) SunsetRose.copy(alpha = 0.85f) else Color(0xFF4C3775),
                RoundedCornerShape(18.dp),
            )
            .bounceClick(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 7.dp),
        contentAlignment = Alignment.Center,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = label,
                color = if (selected) Color.White else Color(0xFFD6C7F8),
                fontSize = 13.sp,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.SemiBold,
            )
            if (count > 0) {
                Spacer(Modifier.width(6.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            if (selected) Color.White.copy(alpha = 0.28f)
                            else Color(0xFF35225E)
                        )
                        .padding(horizontal = 6.dp, vertical = 1.dp),
                ) {
                    Text(
                        text = "$count",
                        color = if (selected) Color.White else CrystalBlue,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// 6. Conversation Row (Glossy Celestial Card + Swipe to Dismiss)
// ─────────────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NovaChatRow(
    chat: Chat,
    user: DirectoryUser?,
    group: GroupInfo?,
    alias: String?,
    activeRoom: RoomCard?,
    onClick: () -> Unit,
    onPin: () -> Unit,
    onMute: () -> Unit,
    onArchive: () -> Unit,
    onDelete: () -> Unit,
    onRoomPreview: (RoomCard) -> Unit,
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            when (value) {
                SwipeToDismissBoxValue.StartToEnd -> {
                    onMute()
                    false
                }
                SwipeToDismissBoxValue.EndToStart -> {
                    onDelete()
                    false
                }
                SwipeToDismissBoxValue.Settled -> false
            }
        }
    )

    val displayName = alias?.ifBlank { null }
        ?: group?.name
        ?: user?.name
        ?: chat.userId

    val isInRoom = activeRoom != null

    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            val isSwiping = dismissState.targetValue != SwipeToDismissBoxValue.Settled
            if (isSwiping) {
                val direction = dismissState.dismissDirection
                val gradient = when (direction) {
                    SwipeToDismissBoxValue.StartToEnd -> Brush.horizontalGradient(listOf(RoyalViolet, MysticPurple))
                    SwipeToDismissBoxValue.EndToStart -> Brush.horizontalGradient(listOf(LiveRed, Color(0xFF8B1226)))
                    else                              -> SolidColor(Color.Transparent)
                }
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(20.dp))
                        .background(gradient)
                        .padding(horizontal = 20.dp),
                    contentAlignment = if (direction == SwipeToDismissBoxValue.StartToEnd)
                        Alignment.CenterStart else Alignment.CenterEnd,
                ) {
                    if (direction == SwipeToDismissBoxValue.StartToEnd) {
                        Text(if (chat.muted) "Unmute" else "Mute", color = CloudWhite, fontWeight = FontWeight.Bold)
                    } else if (direction == SwipeToDismissBoxValue.EndToStart) {
                        Text("Delete", color = CloudWhite, fontWeight = FontWeight.Bold)
                    }
                }
            }
        },
        enableDismissFromStartToEnd = true,
        enableDismissFromEndToStart = true,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(3.dp, RoundedCornerShape(20.dp))
                .clip(RoundedCornerShape(20.dp))
                .background(
                    if (chat.pinned)
                        Brush.linearGradient(listOf(Color(0xFF2E175A), Color(0xFF1B113F)))
                    else
                        Brush.verticalGradient(listOf(Color(0xFF201344), Color(0xFF150D32)))
                )
                .border(
                    1.2.dp,
                    if (chat.pinned) RoyalGold.copy(alpha = 0.65f) else DreamLavender.copy(alpha = 0.30f),
                    RoundedCornerShape(20.dp),
                )
                .bounceClick(scaleDown = 0.98f, onClick = onClick)
                .padding(12.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Avatar (52dp)
                Box(Modifier.size(52.dp)) {
                    if (chat.isGroup) {
                        Box(
                            Modifier
                                .size(52.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF2B1B57))
                                .border(1.2.dp, DreamLavender.copy(alpha = 0.4f), CircleShape),
                            contentAlignment = Alignment.Center,
                        ) {
                            Image(
                                painter = painterResource(group?.avatarRes?.takeIf { it != 0 } ?: R.drawable.vb_chat_ic_groups),
                                contentDescription = displayName,
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop,
                            )
                        }
                        Box(
                            Modifier
                                .align(Alignment.BottomEnd)
                                .size(17.dp)
                                .clip(CircleShape)
                                .background(MysticPurple)
                                .border(1.5.dp, DeepInk, CircleShape),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.vb_chat_ic_groups),
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(10.dp),
                            )
                        }
                    } else {
                        Box(
                            Modifier
                                .size(52.dp)
                                .clip(CircleShape)
                                .border(1.dp, DreamLavender.copy(alpha = 0.4f), CircleShape),
                            contentAlignment = Alignment.Center,
                        ) {
                            NovaAvatarImage(id = chat.userId, size = 50.dp)
                        }
                        if (user?.online == true) {
                            Box(
                                Modifier
                                    .align(Alignment.BottomEnd)
                                    .size(14.dp)
                                    .clip(CircleShape)
                                    .background(MintGreen)
                                    .border(2.dp, DeepInk, CircleShape),
                            )
                        }
                    }
                }

                Spacer(Modifier.width(12.dp))

                // Name + Badges + Message
                Column(Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (chat.pinned) {
                            Icon(
                                painter = painterResource(R.drawable.vb_chat_ic_pin_chat),
                                contentDescription = "Pinned",
                                tint = RoyalGold,
                                modifier = Modifier.size(13.dp),
                            )
                            Spacer(Modifier.width(4.dp))
                        }

                        Text(
                            text = displayName,
                            color = CloudWhite,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f, fill = false),
                        )

                        // Live Voice Room Badge
                        if (isInRoom) {
                            Spacer(Modifier.width(6.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(LiveRed.copy(alpha = 0.22f))
                                    .border(0.8.dp, LiveRed.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                                    .clickable { activeRoom?.let { onRoomPreview(it) } }
                                    .padding(horizontal = 6.dp, vertical = 2.dp),
                            ) {
                                Box(Modifier.size(5.dp).background(LiveRed, CircleShape))
                                Spacer(Modifier.width(3.dp))
                                Text("LIVE", color = LiveRed, fontSize = 9.sp, fontWeight = FontWeight.Black)
                            }
                        }

                        // Verified / Official Badge
                        if (user?.isOfficial == true) {
                            Spacer(Modifier.width(5.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(CrystalBlue.copy(alpha = 0.22f))
                                    .border(0.8.dp, CrystalBlue.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                                    .padding(horizontal = 5.dp, vertical = 1.5.dp),
                            ) {
                                Image(
                                    painter = painterResource(R.drawable.vb_chat_badge_official),
                                    contentDescription = null,
                                    modifier = Modifier.size(10.dp),
                                )
                                Spacer(Modifier.width(2.dp))
                                Text("Official", color = CrystalBlue, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        // Family Badge
                        if (user?.isFamily == true) {
                            Spacer(Modifier.width(5.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(SunsetRose.copy(alpha = 0.22f))
                                    .border(0.8.dp, SunsetRose.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                                    .padding(horizontal = 5.dp, vertical = 1.5.dp),
                            ) {
                                Image(
                                    painter = painterResource(R.drawable.vb_chat_badge_family),
                                    contentDescription = null,
                                    modifier = Modifier.size(10.dp),
                                )
                                Spacer(Modifier.width(2.dp))
                                Text("Family", color = SunsetRose, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        if (chat.muted) {
                            Spacer(Modifier.width(5.dp))
                            Icon(
                                painter = painterResource(R.drawable.vb_chat_ic_mute),
                                contentDescription = "Muted",
                                tint = DreamLavender.copy(alpha = 0.65f),
                                modifier = Modifier.size(13.dp),
                            )
                        }
                    }

                    Spacer(Modifier.height(3.dp))

                    // Message preview
                    Text(
                        text = chat.displayText,
                        color = if (chat.unread > 0) CloudWhite else DreamLavender.copy(alpha = 0.85f),
                        fontSize = 12.5.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontWeight = if (chat.unread > 0) FontWeight.SemiBold else FontWeight.Normal,
                    )
                }

                Spacer(Modifier.width(8.dp))

                // Timestamp + Unread Badge
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = relativeTime(chat.lastTs),
                        color = if (chat.unread > 0) CrystalBlue else DreamLavender.copy(alpha = 0.65f),
                        fontSize = 11.sp,
                        fontWeight = if (chat.unread > 0) FontWeight.Bold else FontWeight.Normal,
                    )
                    if (chat.unread > 0) {
                        Spacer(Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    if (chat.muted)
                                        Brush.horizontalGradient(listOf(Color(0xFF4C3775), Color(0xFF4C3775)))
                                    else
                                        AuroraGradients.sunsetFantasy
                                )
                                .border(0.6.dp, CloudWhite.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                                .padding(horizontal = 7.dp, vertical = 2.dp),
                        ) {
                            Text(
                                text = if (chat.unread > 99) "99+" else "${chat.unread}",
                                color = CloudWhite,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                            )
                        }
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// 7. Discover People Row
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun NovaPeopleRow(
    user: DirectoryUser,
    rel: String,
    onClick: () -> Unit,
    onAction: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(18.dp))
            .clip(RoundedCornerShape(18.dp))
            .background(AuroraGradients.celestialCard)
            .border(1.dp, DreamLavender.copy(alpha = 0.30f), RoundedCornerShape(18.dp))
            .bounceClick(scaleDown = 0.98f, onClick = onClick)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .border(1.dp, DreamLavender.copy(alpha = 0.4f), CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            NovaAvatarImage(id = user.id, size = 44.dp)
        }
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = user.name,
                    color = CloudWhite,
                    fontSize = 14.5.sp,
                    fontWeight = FontWeight.ExtraBold,
                    maxLines = 1,
                )
                if (user.isOfficial) {
                    Spacer(Modifier.width(4.dp))
                    Text("Official", color = CrystalBlue, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                }
            }
            Text(
                text = "@${user.id} · Lv ${user.level}" + if (user.online) " · Online" else "",
                color = DreamLavender,
                fontSize = 11.5.sp,
                maxLines = 1,
            )
        }
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(14.dp))
                .background(
                    if (rel == "friend") AuroraGradients.crystalBlue
                    else AuroraGradients.sunsetFantasy
                )
                .border(0.8.dp, CloudWhite.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                .bounceClick(onClick = onAction)
                .padding(horizontal = 14.dp, vertical = 6.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = if (rel == "friend") "Message" else "+ Add",
                color = CloudWhite,
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// 8. Empty States & Empty Search
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun NovaEmptyState(
    filter: ChatFilter,
    onStartChat: () -> Unit,
    onFindFriends: () -> Unit,
    onCreateGroup: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 36.dp, bottom = 20.dp, start = 20.dp, end = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(92.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        listOf(MysticPurple.copy(alpha = 0.45f), Color.Transparent)
                    )
                )
                .border(1.2.dp, CrystalBlue.copy(alpha = 0.6f), CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(
                    if (filter == ChatFilter.GROUPS) R.drawable.vb_chat_ic_groups
                    else R.drawable.vb_chat_fx_sparkle
                ),
                contentDescription = null,
                modifier = Modifier.size(50.dp),
            )
        }
        Spacer(Modifier.height(16.dp))
        Text(
            text = when (filter) {
                ChatFilter.UNREAD -> "All Caught Up! ✨"
                ChatFilter.GROUPS -> "No Group Chats Yet"
                ChatFilter.FRIENDS -> "No Friend Messages Yet"
                else -> "No Conversations Yet"
            },
            color = CloudWhite,
            fontSize = 18.sp,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = when (filter) {
                ChatFilter.UNREAD -> "You have read all received messages."
                ChatFilter.GROUPS -> "Create a squad or group chat to hang out and play together!"
                ChatFilter.FRIENDS -> "Add friends from the lobby to start private messages."
                else -> "Connect with friends and jump into voice rooms and game chats."
            },
            color = DreamLavender,
            fontSize = 12.5.sp,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(18.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            if (filter == ChatFilter.GROUPS) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(AuroraGradients.sunsetFantasy)
                        .border(1.dp, CloudWhite.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                        .bounceClick(onClick = onCreateGroup)
                        .padding(horizontal = 20.dp, vertical = 10.dp),
                ) {
                    Text("Create Group", color = CloudWhite, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            } else {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(AuroraGradients.sunsetFantasy)
                        .border(1.dp, CloudWhite.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                        .bounceClick(onClick = onStartChat)
                        .padding(horizontal = 18.dp, vertical = 10.dp),
                ) {
                    Text("Start a Chat", color = CloudWhite, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(Brush.linearGradient(listOf(Color(0xFF26184C), Color(0xFF1B113B))))
                        .border(1.dp, DreamLavender.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                        .bounceClick(onClick = onFindFriends)
                        .padding(horizontal = 18.dp, vertical = 10.dp),
                ) {
                    Text("Find Friends", color = CrystalBlue, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun NovaEmptySearch(query: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(36.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painter = painterResource(R.drawable.vb_chat_ic_search_empty),
            contentDescription = null,
            modifier = Modifier.size(64.dp),
        )
        Spacer(Modifier.height(14.dp))
        Text(
            text = "No results for \"$query\"",
            color = CloudWhite,
            fontSize = 16.sp,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = "Try searching a different name, alias, or message text",
            color = DreamLavender,
            fontSize = 12.5.sp,
            textAlign = TextAlign.Center,
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// 9. Floating Compact Gift Pack Widget (Matches VoiceRoomListScreen)
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun CompactChatGiftPackWidget(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(50.dp)
            .shadow(4.dp, CircleShape)
            .clip(CircleShape)
            .background(AuroraSurface)
            .border(1.5.dp, AuroraGold, CircleShape)
            .bounceClick(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painterResource(R.drawable.vb_voice_gift_pack),
            contentDescription = "Gift Pack offers",
            modifier = Modifier.size(34.dp),
            contentScale = ContentScale.Fit,
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// 10. New Group Creation Bottom Sheet
// ─────────────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NewGroupBottomSheet(
    friends: List<DirectoryUser>,
    onDismiss: () -> Unit,
    onCreate: (name: String, selectedIds: List<String>, desc: String, avatarRes: Int) -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var groupName by remember { mutableStateOf("") }
    var groupDesc by remember { mutableStateOf("") }
    var selectedAvatarIndex by remember { mutableStateOf(6) }
    val selectedFriendIds = remember { mutableStateOf(setOf<String>()) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = DeepInk,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    painter = painterResource(R.drawable.vb_chat_ic_groups),
                    contentDescription = null,
                    tint = SunsetRose,
                    modifier = Modifier.size(26.dp),
                )
                Spacer(Modifier.width(10.dp))
                Column(Modifier.weight(1f)) {
                    Text("Create New Group", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = CloudWhite)
                    Text("Chat, play and party together", fontSize = 12.sp, color = DreamLavender)
                }
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF2E1B5B))
                        .clickable(onClick = onDismiss),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_close),
                        contentDescription = "Close",
                        tint = CloudWhite,
                        modifier = Modifier.size(12.dp),
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // Avatar picker row
            Text("Select Group Mascot", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = CloudWhite)
            Spacer(Modifier.height(8.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(NOVA_AVATARS.indices.toList()) { index ->
                    val isSel = selectedAvatarIndex == index
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .border(
                                2.dp,
                                if (isSel) CrystalBlue else Color.Transparent,
                                CircleShape
                            )
                            .bounceClick { selectedAvatarIndex = index },
                        contentAlignment = Alignment.Center,
                    ) {
                        Image(
                            painter = painterResource(NOVA_AVATARS[index]),
                            contentDescription = null,
                            modifier = Modifier.size(40.dp).clip(CircleShape),
                        )
                    }
                }
            }

            Spacer(Modifier.height(14.dp))

            // Group Name input
            Text("Group Name", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = CloudWhite)
            Spacer(Modifier.height(6.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF1E1344), RoundedCornerShape(14.dp))
                    .border(1.dp, DreamLavender.copy(alpha = 0.35f), RoundedCornerShape(14.dp))
                    .padding(horizontal = 14.dp, vertical = 10.dp),
            ) {
                BasicTextField(
                    value = groupName,
                    onValueChange = { groupName = it },
                    singleLine = true,
                    textStyle = TextStyle(color = CloudWhite, fontSize = 14.sp),
                    cursorBrush = SolidColor(SunsetRose),
                    modifier = Modifier.fillMaxWidth(),
                    decorationBox = { field ->
                        if (groupName.isEmpty()) {
                            Text("e.g. Werewolf Nights Squad", color = DreamLavender.copy(alpha = 0.6f), fontSize = 13.sp)
                        }
                        field()
                    },
                )
            }

            Spacer(Modifier.height(12.dp))

            // Group description
            Text("Description (Optional)", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = CloudWhite)
            Spacer(Modifier.height(6.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF1E1344), RoundedCornerShape(14.dp))
                    .border(1.dp, DreamLavender.copy(alpha = 0.35f), RoundedCornerShape(14.dp))
                    .padding(horizontal = 14.dp, vertical = 8.dp),
            ) {
                BasicTextField(
                    value = groupDesc,
                    onValueChange = { groupDesc = it },
                    singleLine = false,
                    maxLines = 2,
                    textStyle = TextStyle(color = CloudWhite, fontSize = 13.sp),
                    cursorBrush = SolidColor(SunsetRose),
                    modifier = Modifier.fillMaxWidth(),
                    decorationBox = { field ->
                        if (groupDesc.isEmpty()) {
                            Text("What is this group about?", color = DreamLavender.copy(alpha = 0.6f), fontSize = 12.sp)
                        }
                        field()
                    },
                )
            }

            Spacer(Modifier.height(14.dp))

            // Friends Selection
            Text("Add Friends (${selectedFriendIds.value.size} selected)", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = CloudWhite)
            Spacer(Modifier.height(8.dp))
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                items(friends, key = { it.id }) { f ->
                    val isChecked = selectedFriendIds.value.contains(f.id)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (isChecked) Color(0xFF2E195E)
                                else Color(0xFF1E1344)
                            )
                            .border(
                                1.dp,
                                if (isChecked) CrystalBlue.copy(alpha = 0.7f) else DreamLavender.copy(alpha = 0.2f),
                                RoundedCornerShape(12.dp),
                            )
                            .clickable {
                                val cur = selectedFriendIds.value.toMutableSet()
                                if (isChecked) cur.remove(f.id) else cur.add(f.id)
                                selectedFriendIds.value = cur
                            }
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        NovaAvatarImage(id = f.id, size = 36.dp)
                        Spacer(Modifier.width(10.dp))
                        Text(f.name, color = CloudWhite, fontSize = 14.sp, modifier = Modifier.weight(1f))
                        Box(
                            modifier = Modifier
                                .size(22.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isChecked) SunsetRose else DeepInk)
                                .border(1.dp, if (isChecked) Color.Transparent else DreamLavender.copy(alpha = 0.4f), RoundedCornerShape(6.dp)),
                            contentAlignment = Alignment.Center,
                        ) {
                            if (isChecked) {
                                Icon(
                                    painter = painterResource(R.drawable.vb_chat_ic_check),
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(14.dp),
                                )
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Submit Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        if (groupName.isNotBlank() && selectedFriendIds.value.isNotEmpty())
                            AuroraGradients.sunsetFantasy
                        else
                            Brush.linearGradient(listOf(Color(0xFF332057), Color(0xFF241642)))
                    )
                    .border(
                        1.dp,
                        if (groupName.isNotBlank() && selectedFriendIds.value.isNotEmpty())
                            CloudWhite.copy(alpha = 0.45f)
                        else
                            Color.Transparent,
                        RoundedCornerShape(24.dp),
                    )
                    .bounceClick(
                        enabled = groupName.isNotBlank() && selectedFriendIds.value.isNotEmpty(),
                        onClick = {
                            onCreate(
                                groupName.trim(),
                                selectedFriendIds.value.toList(),
                                groupDesc.trim(),
                                NOVA_AVATARS[selectedAvatarIndex],
                            )
                        }
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "Create Group",
                    color = CloudWhite,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.ExtraBold,
                )
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// 11. Inbox / Requests Bottom Sheet
// ─────────────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun InboxBottomSheet(
    store: MessagingStore,
    notifications: List<AppNotification>,
    incomingRequests: List<DirectoryUser>,
    onDismiss: () -> Unit,
    onAcceptFriend: (String) -> Unit,
    onDeclineFriend: (String) -> Unit,
    onJoinRoom: (String) -> Unit,
    onOpenChat: (String) -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var tabIndex by remember { mutableStateOf(0) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = DeepInk,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    painter = painterResource(R.drawable.vb_chat_ic_notification),
                    contentDescription = null,
                    tint = CrystalBlue,
                    modifier = Modifier.size(26.dp),
                )
                Spacer(Modifier.width(10.dp))
                Column(Modifier.weight(1f)) {
                    Text("Inbox & Requests", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = CloudWhite)
                    Text("Friend requests, room invites & gifts", fontSize = 12.sp, color = DreamLavender)
                }
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF2E1B5B))
                        .clickable(onClick = onDismiss),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_close),
                        contentDescription = "Close",
                        tint = CloudWhite,
                        modifier = Modifier.size(12.dp),
                    )
                }
            }

            Spacer(Modifier.height(14.dp))

            // Sub Tab bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF1E1344), RoundedCornerShape(14.dp))
                    .padding(4.dp),
            ) {
                val tabs = listOf("All Alerts", "Friend Requests (${incomingRequests.size})", "Room Invites")
                tabs.forEachIndexed { i, title ->
                    val isSel = tabIndex == i
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSel) AuroraGradients.sunsetFantasy else SolidColor(Color.Transparent))
                            .clickable { tabIndex = i }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = title,
                            color = if (isSel) CloudWhite else DreamLavender,
                            fontSize = 11.5.sp,
                            fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                        )
                    }
                }
            }

            Spacer(Modifier.height(14.dp))

            // Content
            when (tabIndex) {
                1 -> {
                    // Friend requests
                    if (incomingRequests.isEmpty()) {
                        Box(Modifier.fillMaxWidth().height(160.dp), contentAlignment = Alignment.Center) {
                            Text("No pending friend requests", color = DreamLavender, fontSize = 13.sp)
                        }
                    } else {
                        LazyColumn(modifier = Modifier.fillMaxWidth().height(260.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(incomingRequests, key = { it.id }) { u ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(AuroraGradients.celestialCard)
                                        .border(1.dp, DreamLavender.copy(alpha = 0.25f), RoundedCornerShape(14.dp))
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    NovaAvatarImage(id = u.id, size = 42.dp)
                                    Spacer(Modifier.width(10.dp))
                                    Column(Modifier.weight(1f)) {
                                        Text(u.name, color = CloudWhite, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                                        Text("Wants to connect", color = DreamLavender, fontSize = 12.sp)
                                    }
                                    Box(
                                        Modifier
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(AuroraGradients.sunsetFantasy)
                                            .bounceClick { onAcceptFriend(u.id) }
                                            .padding(horizontal = 12.dp, vertical = 6.dp),
                                    ) {
                                        Text("Accept", color = CloudWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Spacer(Modifier.width(6.dp))
                                    Box(
                                        Modifier
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(Color(0xFF2E1B5B))
                                            .border(1.dp, DreamLavender.copy(alpha = 0.35f), RoundedCornerShape(10.dp))
                                            .bounceClick { onDeclineFriend(u.id) }
                                            .padding(horizontal = 10.dp, vertical = 6.dp),
                                    ) {
                                        Text("Decline", color = DreamLavender, fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }
                }
                2 -> {
                    // Room Invites
                    val roomNotifs = notifications.filter { it.kind == NotificationKind.ROOM_INVITE }
                    if (roomNotifs.isEmpty()) {
                        Box(Modifier.fillMaxWidth().height(160.dp), contentAlignment = Alignment.Center) {
                            Text("No room invitations at the moment", color = DreamLavender, fontSize = 13.sp)
                        }
                    } else {
                        LazyColumn(modifier = Modifier.fillMaxWidth().height(260.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(roomNotifs, key = { it.id }) { n ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(AuroraGradients.celestialCard)
                                        .border(1.dp, DreamLavender.copy(alpha = 0.25f), RoundedCornerShape(14.dp))
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Icon(
                                        painter = painterResource(R.drawable.vb_chat_ic_voice_room),
                                        contentDescription = null,
                                        tint = LiveRed,
                                        modifier = Modifier.size(32.dp),
                                    )
                                    Spacer(Modifier.width(10.dp))
                                    Column(Modifier.weight(1f)) {
                                        Text(n.text, color = CloudWhite, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                                        Text(relativeTime(n.ts), color = DreamLavender, fontSize = 11.sp)
                                    }
                                    n.roomId?.let { rid ->
                                        Box(
                                            Modifier
                                                .clip(RoundedCornerShape(10.dp))
                                                .background(AuroraGradients.sunsetFantasy)
                                                .bounceClick { onJoinRoom(rid) }
                                                .padding(horizontal = 12.dp, vertical = 6.dp),
                                        ) {
                                            Text("Join Room", color = CloudWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                else -> {
                    // All Notifications
                    if (notifications.isEmpty()) {
                        Box(Modifier.fillMaxWidth().height(160.dp), contentAlignment = Alignment.Center) {
                            Text("No notifications yet", color = DreamLavender, fontSize = 13.sp)
                        }
                    } else {
                        LazyColumn(modifier = Modifier.fillMaxWidth().height(260.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(notifications, key = { it.id }) { n ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(AuroraGradients.celestialCard)
                                        .border(1.dp, DreamLavender.copy(alpha = 0.25f), RoundedCornerShape(14.dp))
                                        .clickable {
                                            if (n.roomId != null) onJoinRoom(n.roomId)
                                             else if (n.userId != null) onOpenChat(n.userId)
                                        }
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Icon(
                                        painter = painterResource(
                                            when (n.kind) {
                                                NotificationKind.GIFT -> R.drawable.vb_chat_ic_gift
                                                NotificationKind.ROOM_INVITE -> R.drawable.vb_chat_ic_voice_room
                                                NotificationKind.FRIEND_ACCEPTED -> R.drawable.vb_chat_ic_contacts
                                                else -> R.drawable.vb_chat_ic_chat
                                            }
                                        ),
                                        contentDescription = null,
                                        tint = CrystalBlue,
                                        modifier = Modifier.size(24.dp),
                                    )
                                    Spacer(Modifier.width(10.dp))
                                    Column(Modifier.weight(1f)) {
                                        Text(n.text, color = CloudWhite, fontSize = 13.sp)
                                        Text(relativeTime(n.ts), color = DreamLavender, fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// 12. Room Preview Modal
// ─────────────────────────────────────────────────────────────────────────────

@Composable
internal fun RoomPreviewModal(
    room: RoomCard,
    onDismiss: () -> Unit,
    onJoin: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DeepInk,
        shape = RoundedCornerShape(24.dp),
        title = null,
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                // Room Cover
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF2B165C)),
                ) {
                    if (room.coverRes != null && room.coverRes != 0) {
                        Image(
                            painter = painterResource(room.coverRes),
                            contentDescription = room.title,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop,
                        )
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    listOf(Color.Transparent, Color(0xCC11134A))
                                )
                            )
                    )
                    // Live badge
                    Box(
                        modifier = Modifier
                            .padding(8.dp)
                            .align(Alignment.TopStart)
                            .clip(RoundedCornerShape(6.dp))
                            .background(LiveRed)
                            .padding(horizontal = 6.dp, vertical = 2.dp),
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(Modifier.size(5.dp).background(Color.White, CircleShape))
                            Spacer(Modifier.width(4.dp))
                            Text("LIVE", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    // Tag
                    Box(
                        modifier = Modifier
                            .padding(8.dp)
                            .align(Alignment.TopEnd)
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color.Black.copy(alpha = 0.5f))
                            .padding(horizontal = 6.dp, vertical = 2.dp),
                    ) {
                        Text(room.tag, color = Color.White, fontSize = 10.sp)
                    }
                }

                Spacer(Modifier.height(14.dp))

                // Title & Host
                Text(
                    text = room.title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = CloudWhite,
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Hosted by ${room.host} · ${room.members} listening",
                    fontSize = 12.5.sp,
                    color = DreamLavender,
                    textAlign = TextAlign.Center,
                )
                if (room.activityText != null) {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = room.activityText,
                        fontSize = 12.sp,
                        color = CrystalBlue,
                        fontWeight = FontWeight.SemiBold,
                    )
                }

                Spacer(Modifier.height(18.dp))

                // Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFF2E1B5B))
                            .border(1.dp, DreamLavender.copy(alpha = 0.35f), RoundedCornerShape(16.dp))
                            .bounceClick(onClick = onDismiss)
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text("Close", color = CloudWhite, fontSize = 13.5.sp, fontWeight = FontWeight.SemiBold)
                    }
                    Box(
                        modifier = Modifier
                            .weight(1.5f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(AuroraGradients.sunsetFantasy)
                            .border(1.dp, CloudWhite.copy(alpha = 0.45f), RoundedCornerShape(16.dp))
                            .bounceClick(onClick = onJoin)
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text("Join Room ›", color = CloudWhite, fontSize = 13.5.sp, fontWeight = FontWeight.ExtraBold)
                    }
                }
            }
        },
        confirmButton = {},
    )
}

// ─────────────────────────────────────────────────────────────────────────────
// 13. Archived Chats Bottom Sheet
// ─────────────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ArchivedChatsBottomSheet(
    archived: List<Chat>,
    store: MessagingStore,
    onDismiss: () -> Unit,
    onUnarchive: (String) -> Unit,
    onOpenChat: (String) -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = DeepInk,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    painter = painterResource(R.drawable.vb_chat_ic_archive),
                    contentDescription = null,
                    tint = RoyalGold,
                    modifier = Modifier.size(26.dp),
                )
                Spacer(Modifier.width(10.dp))
                Column(Modifier.weight(1f)) {
                    Text("Archived Chats", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = CloudWhite)
                    Text("${archived.size} conversations hidden", fontSize = 12.sp, color = DreamLavender)
                }
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF2E1B5B))
                        .clickable(onClick = onDismiss),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_close),
                        contentDescription = "Close",
                        tint = CloudWhite,
                        modifier = Modifier.size(12.dp),
                    )
                }
            }

            Spacer(Modifier.height(14.dp))

            if (archived.isEmpty()) {
                Box(Modifier.fillMaxWidth().height(160.dp), contentAlignment = Alignment.Center) {
                    Text("No archived conversations", color = DreamLavender, fontSize = 13.sp)
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxWidth().height(260.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(archived, key = { it.userId }) { c ->
                        val u = store.userById(c.userId)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(AuroraGradients.celestialCard)
                                .border(1.dp, DreamLavender.copy(alpha = 0.25f), RoundedCornerShape(14.dp))
                                .clickable { onOpenChat(c.userId) }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            NovaAvatarImage(id = c.userId, size = 42.dp)
                            Spacer(Modifier.width(10.dp))
                            Column(Modifier.weight(1f)) {
                                Text(u?.name ?: c.userId, color = CloudWhite, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                                Text(c.displayText, color = DreamLavender, fontSize = 12.sp, maxLines = 1)
                            }
                            Box(
                                Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(AuroraGradients.sunsetFantasy)
                                    .bounceClick { onUnarchive(c.userId) }
                                    .padding(horizontal = 12.dp, vertical = 6.dp),
                            ) {
                                Text("Unarchive", color = CloudWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// 14. Contacts Bottom Sheet
// ─────────────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ContactsBottomSheet(
    friends: List<DirectoryUser>,
    onDismiss: () -> Unit,
    onChat: (String) -> Unit,
    onViewProfile: (String) -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var filterQuery by remember { mutableStateOf("") }
    val filtered = remember(friends, filterQuery) {
        if (filterQuery.isBlank()) friends
        else friends.filter { it.name.contains(filterQuery, ignoreCase = true) || it.id.contains(filterQuery, ignoreCase = true) }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = DeepInk,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    painter = painterResource(R.drawable.vb_chat_ic_contacts),
                    contentDescription = null,
                    tint = CrystalBlue,
                    modifier = Modifier.size(28.dp),
                )
                Spacer(Modifier.width(10.dp))
                Column(Modifier.weight(1f)) {
                    Text("Contacts & Friends", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = CloudWhite)
                    Text("${friends.size} friends connected", fontSize = 12.sp, color = DreamLavender)
                }
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF2E1B5B))
                        .clickable(onClick = onDismiss),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_close),
                        contentDescription = "Close",
                        tint = CloudWhite,
                        modifier = Modifier.size(12.dp),
                    )
                }
            }

            Spacer(Modifier.height(14.dp))

            // Search within contacts
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF1E1344), RoundedCornerShape(14.dp))
                    .border(1.dp, DreamLavender.copy(alpha = 0.35f), RoundedCornerShape(14.dp))
                    .padding(horizontal = 12.dp, vertical = 10.dp),
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_search),
                    contentDescription = null,
                    tint = CrystalBlue,
                    modifier = Modifier.size(16.dp),
                )
                Spacer(Modifier.width(8.dp))
                BasicTextField(
                    value = filterQuery,
                    onValueChange = { filterQuery = it },
                    singleLine = true,
                    textStyle = TextStyle(color = CloudWhite, fontSize = 14.sp),
                    cursorBrush = SolidColor(SunsetRose),
                    modifier = Modifier.weight(1f),
                    decorationBox = { inner ->
                        Box {
                            if (filterQuery.isEmpty()) {
                                Text("Filter contacts…", color = DreamLavender.copy(alpha = 0.6f), fontSize = 13.sp)
                            }
                            inner()
                        }
                    },
                )
            }

            Spacer(Modifier.height(12.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(filtered, key = { it.id }) { f ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(AuroraGradients.celestialCard)
                            .border(1.dp, DreamLavender.copy(alpha = 0.25f), RoundedCornerShape(14.dp))
                            .clickable { onChat(f.id) }
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        NovaAvatarImage(id = f.id, size = 44.dp)
                        Spacer(Modifier.width(10.dp))
                        Column(Modifier.weight(1f)) {
                            Text(f.name, color = CloudWhite, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                            Text(
                                text = "@${f.id} · Lv ${f.level}" + if (f.online) " · Online" else "",
                                color = DreamLavender,
                                fontSize = 11.sp,
                            )
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(AuroraGradients.sunsetFantasy)
                                .bounceClick { onChat(f.id) }
                                .padding(horizontal = 14.dp, vertical = 7.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text("Chat", color = CloudWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// 15. New Chat / Search Bottom Sheet
// ─────────────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NewChatBottomSheet(
    users: List<DirectoryUser>,
    onDismiss: () -> Unit,
    onSelectUser: (String) -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var query by remember { mutableStateOf("") }
    val filtered = remember(users, query) {
        if (query.isBlank()) users
        else users.filter { it.name.contains(query, ignoreCase = true) || it.id.contains(query, ignoreCase = true) }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = DeepInk,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    painter = painterResource(R.drawable.vb_chat_ic_new_chat),
                    contentDescription = null,
                    tint = SunsetRose,
                    modifier = Modifier.size(26.dp),
                )
                Spacer(Modifier.width(10.dp))
                Column(Modifier.weight(1f)) {
                    Text("New Conversation", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = CloudWhite)
                    Text("Choose someone to message", fontSize = 12.sp, color = DreamLavender)
                }
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF2E1B5B))
                        .clickable(onClick = onDismiss),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_close),
                        contentDescription = "Close",
                        tint = CloudWhite,
                        modifier = Modifier.size(12.dp),
                    )
                }
            }

            Spacer(Modifier.height(14.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF1E1344), RoundedCornerShape(14.dp))
                    .border(1.dp, DreamLavender.copy(alpha = 0.35f), RoundedCornerShape(14.dp))
                    .padding(horizontal = 12.dp, vertical = 10.dp),
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_search),
                    contentDescription = null,
                    tint = CrystalBlue,
                    modifier = Modifier.size(16.dp),
                )
                Spacer(Modifier.width(8.dp))
                BasicTextField(
                    value = query,
                    onValueChange = { query = it },
                    singleLine = true,
                    textStyle = TextStyle(color = CloudWhite, fontSize = 14.sp),
                    cursorBrush = SolidColor(SunsetRose),
                    modifier = Modifier.weight(1f),
                    decorationBox = { inner ->
                        Box {
                            if (query.isEmpty()) {
                                Text("Search people by name or ID…", color = DreamLavender.copy(alpha = 0.6f), fontSize = 13.sp)
                            }
                            inner()
                        }
                    },
                )
            }

            Spacer(Modifier.height(12.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(filtered, key = { it.id }) { u ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(AuroraGradients.celestialCard)
                            .border(1.dp, DreamLavender.copy(alpha = 0.25f), RoundedCornerShape(14.dp))
                            .clickable { onSelectUser(u.id) }
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        NovaAvatarImage(id = u.id, size = 44.dp)
                        Spacer(Modifier.width(10.dp))
                        Column(Modifier.weight(1f)) {
                            Text(u.name, color = CloudWhite, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                            Text(
                                text = "@${u.id} · Lv ${u.level}" + if (u.online) " · Online" else "",
                                color = DreamLavender,
                                fontSize = 11.sp,
                            )
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(AuroraGradients.sunsetFantasy)
                                .bounceClick { onSelectUser(u.id) }
                                .padding(horizontal = 14.dp, vertical = 7.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text("Message", color = CloudWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// 16. Shared NovaAvatarImage Helper
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun NovaAvatarImage(
    id: String,
    size: androidx.compose.ui.unit.Dp = 44.dp,
    modifier: Modifier = Modifier,
) {
    Image(
        painter = painterResource(novaAvatarRes(id)),
        contentDescription = "Avatar $id",
        modifier = modifier
            .size(size)
            .clip(CircleShape),
        contentScale = ContentScale.Crop,
    )
}

// ─────────────────────────────────────────────────────────────────────────────
// 17. Relative Time Helper
// ─────────────────────────────────────────────────────────────────────────────

fun relativeTime(ts: Long): String {
    if (ts == 0L) return ""
    val diff = System.currentTimeMillis() - ts
    val min  = diff / 60_000
    if (min < 1)  return "now"
    if (min < 60) return "${min}m"
    val hr = min / 60
    if (hr < 24)  return "${hr}h"
    val day = hr / 24
    if (day < 7)  return "${day}d"
    return "${day / 7}w"
}
