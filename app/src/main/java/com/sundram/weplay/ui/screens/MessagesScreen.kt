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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.sundram.weplay.R
import com.sundram.weplay.data.AppNotification
import com.sundram.weplay.data.Chat
import com.sundram.weplay.data.DirectoryUser
import com.sundram.weplay.data.GroupInfo
import com.sundram.weplay.data.MessagingStore
import com.sundram.weplay.data.NotificationKind
import com.sundram.weplay.data.RoomCard
import com.sundram.weplay.ui.components.GiftPackOffersSheet
import com.sundram.weplay.ui.nav.Routes
import com.sundram.weplay.ui.theme.KtvRed
import com.sundram.weplay.ui.theme.LiveRed
import com.sundram.weplay.ui.theme.MintGreen
import com.sundram.weplay.ui.theme.AuroraChatBackground
import com.sundram.weplay.ui.theme.VyboraBg
import com.sundram.weplay.ui.theme.VyboraBorder
import com.sundram.weplay.ui.theme.VyboraCyan
import com.sundram.weplay.ui.theme.VyboraCyanSoft
import com.sundram.weplay.ui.theme.VyboraLavender
import com.sundram.weplay.ui.theme.VyboraPurple
import com.sundram.weplay.ui.theme.VyboraSurface
import com.sundram.weplay.ui.theme.VyboraTextPrimary
import com.sundram.weplay.ui.theme.VyboraTextSecondary

// ─── Chat filter type ────────────────────────────────────────────────────────

private enum class ChatFilter { ALL, UNREAD, GROUPS, FRIENDS }

// ─── Presence filter type ─────────────────────────────────────────────────────

private enum class PresenceFilter { ONLINE, IN_ROOMS, RECENT, FAVORITES }

// ─── Nova mascot avatar mapping ───────────────────────────────────────────────

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

// ─── Main screen ─────────────────────────────────────────────────────────────

@Composable
fun MessagesScreen(
    nav: NavController,
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

    // ── Root layout ──────────────────────────────────────────────────────────
    AuroraChatBackground(
        modifier = Modifier
            .fillMaxSize()
            .padding(pad),
    ) {
        LazyColumn(
            contentPadding = PaddingValues(bottom = 100.dp),
            modifier = Modifier.fillMaxSize(),
        ) {

            // ── Premium Header (Section 3.1) ─────────────────────────────────
            item {
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
            }

            // ── Search bar ───────────────────────────────────────────────────
            item {
                NovaSearchBar(
                    query = query,
                    onQueryChange = { query = it },
                    onClear = { query = "" },
                )
            }

            // ── Social Presence Section (Section 3.2) ────────────────────────
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
                        onUserClick = { u -> nav.navigate(Routes.userRoute(u.id)) },
                        onRoomClick = { room -> previewRoom = room },
                    )
                }
            }

            // ── Main Conversation Filter Chips ───────────────────────────────
            item {
                NovaFilterChips(
                    selected = filter,
                    unreadCount = allChats.count { it.unread > 0 },
                    groupsCount = allChats.count { it.isGroup },
                    friendCount = allChats.count { !it.isGroup && store.relationship(it.userId) == "friend" },
                    onSelect = { filter = it },
                )
            }

            // ── Chats list or empty state ────────────────────────────────────
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
                        onClick = { nav.navigate(Routes.chatRoute(chat.userId)) },
                        onPin = { store.setPinned(chat.userId, !chat.pinned); bump() },
                        onMute = { store.setMuted(chat.userId, !chat.muted); bump() },
                        onArchive = { store.archiveChat(chat.userId, true); bump() },
                        onDelete = { store.deleteChat(chat.userId); bump() },
                        onRoomPreview = { r -> previewRoom = r },
                    )
                }
            }

            // ── People discovery strip (when not searching and on ALL tab) ───
            if (query.isBlank() && filter == ChatFilter.ALL) {
                val quick = store.allUsers()
                    .filter { !store.isMe(it.id) && !store.isBlocked(it.id) }
                    .take(5)
                if (quick.isNotEmpty()) {
                    item {
                        Spacer(Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                "Discover People",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = VyboraTextPrimary,
                                modifier = Modifier.weight(1f),
                            )
                        }
                        Spacer(Modifier.height(4.dp))
                    }
                    itemsIndexed(quick, key = { index, it -> "q_${it.id}_$index" }) { _, u ->
                        NovaPeopleRow(
                            user = u,
                            rel = store.relationship(u.id),
                            onClick = { nav.navigate(Routes.userRoute(u.id)) },
                            onAction = {
                                if (store.isFriend(u.id)) {
                                    nav.navigate(Routes.chatRoute(u.id))
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

        // ── Floating Gift Pack FAB ───────────────────────────────────────────
        NovaGiftPackFab(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 20.dp, bottom = 16.dp),
            onClick = { showGiftPackSheet = true },
        )
    }

    // ── 48-Hour Rotating Gift Pack Bottom Sheet ──────────────────────────────
    if (showGiftPackSheet) {
        GiftPackOffersSheet(
            userId = store.meId(),
            onDismiss = { showGiftPackSheet = false },
            onOpenTopUp = {
                showGiftPackSheet = false
                nav.navigate(Routes.TOPUP)
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
                nav.navigate(Routes.chatRoute(uid))
            },
            onViewProfile = { uid ->
                showContactsSheet = false
                nav.navigate(Routes.userRoute(uid))
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
                nav.navigate(Routes.chatRoute(uid))
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
                nav.navigate(Routes.chatRoute(newGroup.id))
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
                nav.navigate(Routes.roomRoute(roomId))
            },
            onOpenChat = { uid ->
                showInboxSheet = false
                nav.navigate(Routes.chatRoute(uid))
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
                nav.navigate(Routes.chatRoute(uid))
                bump()
            },
        )
    }

    // ── Room Preview Modal (Section 8.1) ─────────────────────────────────────
    previewRoom?.let { room ->
        RoomPreviewModal(
            room = room,
            onDismiss = { previewRoom = null },
            onJoin = {
                val rid = room.id
                previewRoom = null
                nav.navigate(Routes.roomRoute(rid))
            },
        )
    }

    // ── Confirm Decline Friend Request Dialog ────────────────────────────────
    confirmDecline?.let { u ->
        AlertDialog(
            onDismissRequest = { confirmDecline = null },
            title = { Text("Decline friend request?", color = VyboraTextPrimary) },
            text = { Text("Remove request from ${u.name}?", color = VyboraTextSecondary) },
            confirmButton = {
                TextButton(onClick = {
                    store.declineRequest(u.id)
                    confirmDecline = null
                    bump()
                }) {
                    Text("Decline", color = KtvRed, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { confirmDecline = null }) {
                    Text("Cancel", color = VyboraTextSecondary)
                }
            },
            containerColor = VyboraSurface,
        )
    }
}

// ─── Header Component (Section 3.1) ──────────────────────────────────────────

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
            .padding(start = 20.dp, end = 16.dp, top = 16.dp, bottom = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // "Chats" Title + Gradient Unread Badge
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f),
        ) {
            Text(
                "Chats",
                color = VyboraTextPrimary,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
            )
            if (unreadTotal > 0) {
                Spacer(Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            Brush.horizontalGradient(listOf(VyboraPurple, VyboraCyan))
                        )
                        .padding(horizontal = 8.dp, vertical = 2.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        if (unreadTotal > 99) "99+" else "$unreadTotal",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }

        // Right Action Buttons
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            // Contacts / Friends
            HeaderIconButton(
                icon = R.drawable.vb_chat_ic_contacts,
                desc = "Contacts",
                onClick = onContacts,
            )

            // Add / New Chat
            HeaderIconButton(
                icon = R.drawable.vb_chat_ic_new_chat,
                desc = "New Chat",
                onClick = onNewChat,
            )

            // Inbox / Message Requests (with unread badge)
            Box {
                HeaderIconButton(
                    icon = R.drawable.vb_chat_ic_notification,
                    desc = "Inbox",
                    onClick = onInbox,
                )
                if (unreadNotifs > 0) {
                    Box(
                        Modifier
                            .align(Alignment.TopEnd)
                            .size(10.dp)
                            .background(LiveRed, CircleShape)
                            .border(1.5.dp, VyboraBg, CircleShape),
                    )
                }
            }

            // More Options Dropdown
            Box {
                HeaderIconButton(
                    icon = R.drawable.vb_chat_ic_more,
                    desc = "More options",
                    onClick = { menuOpen = true },
                )
                DropdownMenu(
                    expanded = menuOpen,
                    onDismissRequest = { menuOpen = false },
                    modifier = Modifier.background(VyboraSurface),
                ) {
                    DropdownMenuItem(
                        text = { Text("Mark all as read", color = VyboraTextPrimary, fontSize = 14.sp) },
                        leadingIcon = {
                            Icon(
                                painter = painterResource(R.drawable.vb_chat_ic_check),
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                                tint = VyboraCyan,
                            )
                        },
                        onClick = { menuOpen = false; onMarkAllRead() },
                    )
                    DropdownMenuItem(
                        text = { Text("New Group Chat", color = VyboraTextPrimary, fontSize = 14.sp) },
                        leadingIcon = {
                            Icon(
                                painter = painterResource(R.drawable.vb_chat_ic_groups),
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                                tint = VyboraPurple,
                            )
                        },
                        onClick = { menuOpen = false; onCreateGroup() },
                    )
                    DropdownMenuItem(
                        text = { Text("Archived Chats", color = VyboraTextPrimary, fontSize = 14.sp) },
                        leadingIcon = {
                            Icon(
                                painter = painterResource(R.drawable.vb_chat_ic_archive),
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                                tint = VyboraTextSecondary,
                            )
                        },
                        onClick = { menuOpen = false; onArchived() },
                    )
                    DropdownMenuItem(
                        text = { Text("Chat Settings", color = VyboraTextPrimary, fontSize = 14.sp) },
                        leadingIcon = {
                            Icon(
                                painter = painterResource(R.drawable.vb_chat_ic_privacy),
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                                tint = VyboraTextSecondary,
                            )
                        },
                        onClick = { menuOpen = false; onSettings() },
                    )
                }
            }
        }
    }
}

@Composable
private fun HeaderIconButton(
    icon: Int,
    desc: String,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(VyboraSurface)
            .border(1.dp, VyboraBorder, CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = desc,
            tint = Color.Unspecified,
            modifier = Modifier.size(20.dp),
        )
    }
}

// ─── Social Presence Section (Section 3.2) ───────────────────────────────────

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
            .padding(vertical = 6.dp),
    ) {
        // Presence Filter Pills
        LazyRow(
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
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
                    label = "Recent",
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

        // Avatar Row
        if (users.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp),
            ) {
                Text(
                    when (selectedFilter) {
                        PresenceFilter.IN_ROOMS -> "No friends currently in voice rooms"
                        PresenceFilter.ONLINE   -> "No friends online right now"
                        else                    -> "No contacts found in this section"
                    },
                    fontSize = 12.sp,
                    color = VyboraTextSecondary,
                )
            }
        } else {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
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
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (selected) VyboraPurple else VyboraSurface
            )
            .border(
                1.dp,
                if (selected) Color.Transparent else VyboraBorder,
                RoundedCornerShape(12.dp),
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 6.dp),
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
                label,
                color = if (selected) Color.White else VyboraTextSecondary,
                fontSize = 12.sp,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
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

    // Breathing pulse for room status
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "pulseScale",
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(62.dp),
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clickable(onClick = onAvatarClick),
            contentAlignment = Alignment.Center,
        ) {
            // Live pulsing ring if in room
            if (isInRoom) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .scale(pulseScale)
                        .clip(CircleShape)
                        .background(
                            Brush.sweepGradient(listOf(LiveRed, VyboraPurple, VyboraCyan, LiveRed))
                        )
                )
            } else if (user.online) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .background(
                            Brush.sweepGradient(listOf(MintGreen, VyboraCyan, MintGreen))
                        )
                )
            }

            // Avatar image
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(VyboraSurface)
                    .padding(2.dp),
                contentAlignment = Alignment.Center,
            ) {
                NovaAvatarImage(id = user.id, size = 46.dp)
            }

            // Status Indicator Dot or Mic
            if (isInRoom) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(18.dp)
                        .clip(CircleShape)
                        .background(LiveRed)
                        .border(1.5.dp, VyboraSurface, CircleShape)
                        .clickable(onClick = onRoomClick),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        painter = painterResource(R.drawable.vb_chat_ic_voice_room),
                        contentDescription = "In Room",
                        tint = Color.White,
                        modifier = Modifier.size(10.dp),
                    )
                }
            } else if (user.online) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(13.dp)
                        .clip(CircleShape)
                        .background(MintGreen)
                        .border(2.dp, VyboraSurface, CircleShape),
                )
            }
        }

        Spacer(Modifier.height(4.dp))

        // Name
        Text(
            user.name,
            color = VyboraTextPrimary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
        )

        // Room activity label if live
        if (isInRoom) {
            Text(
                activeRoom.title,
                color = LiveRed,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                modifier = Modifier.clickable(onClick = onRoomClick),
            )
        }
    }
}

// ─── Search Bar ──────────────────────────────────────────────────────────────

@Composable
private fun NovaSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onClear: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp)
            .background(VyboraSurface, RoundedCornerShape(16.dp))
            .border(1.dp, VyboraBorder, RoundedCornerShape(16.dp))
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            painter = painterResource(R.drawable.vb_chat_ic_search),
            contentDescription = "Search",
            tint = Color.Unspecified,
            modifier = Modifier.size(18.dp),
        )
        Spacer(Modifier.width(10.dp))
        BasicTextField(
            value = query,
            onValueChange = onQueryChange,
            singleLine = true,
            textStyle = TextStyle(
                color = VyboraTextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
            ),
            cursorBrush = SolidColor(VyboraCyan),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            modifier = Modifier.weight(1f),
            decorationBox = { innerTextField ->
                Box {
                    if (query.isEmpty()) {
                        Text(
                            "Search messages, friends or groups…",
                            color = VyboraTextSecondary,
                            fontSize = 14.sp,
                        )
                    }
                    innerTextField()
                }
            },
        )
        if (query.isNotEmpty()) {
            Spacer(Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(VyboraLavender)
                    .clickable(onClick = onClear),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = painterResource(R.drawable.vb_chat_ic_close),
                    contentDescription = "Clear",
                    tint = Color.Unspecified,
                    modifier = Modifier.size(10.dp),
                )
            }
        }
    }
}

// ─── Filter Chips (Section 4) ────────────────────────────────────────────────

@Composable
private fun NovaFilterChips(
    selected: ChatFilter,
    unreadCount: Int,
    groupsCount: Int,
    friendCount: Int,
    onSelect: (ChatFilter) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        NovaChip(
            label = "All",
            selected = selected == ChatFilter.ALL,
            onClick = { onSelect(ChatFilter.ALL) },
        )
        NovaChip(
            label = "Unread",
            count = unreadCount,
            selected = selected == ChatFilter.UNREAD,
            onClick = { onSelect(ChatFilter.UNREAD) },
        )
        NovaChip(
            label = "Groups",
            count = groupsCount,
            selected = selected == ChatFilter.GROUPS,
            onClick = { onSelect(ChatFilter.GROUPS) },
        )
        NovaChip(
            label = "Friends",
            count = friendCount,
            selected = selected == ChatFilter.FRIENDS,
            onClick = { onSelect(ChatFilter.FRIENDS) },
        )
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
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (selected)
                    Brush.horizontalGradient(listOf(VyboraPurple, VyboraCyan))
                else
                    SolidColor(VyboraSurface)
            )
            .border(
                1.dp,
                if (selected) Color.Transparent else VyboraBorder,
                RoundedCornerShape(12.dp),
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 7.dp),
        contentAlignment = Alignment.Center,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                label,
                color = if (selected) Color.White else VyboraTextPrimary,
                fontSize = 13.sp,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            )
            if (count > 0) {
                Spacer(Modifier.width(5.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            if (selected) Color.White.copy(alpha = 0.25f)
                            else VyboraLavender
                        )
                        .padding(horizontal = 5.dp, vertical = 1.dp),
                ) {
                    Text(
                        "$count",
                        color = if (selected) Color.White else VyboraPurple,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    }
}

// ─── Conversation Row (Section 4) ────────────────────────────────────────────

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
    var contextMenuOpen by remember { mutableStateOf(false) }
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
            val direction = dismissState.dismissDirection
            val color = when (direction) {
                SwipeToDismissBoxValue.StartToEnd -> VyboraPurple
                SwipeToDismissBoxValue.EndToStart -> KtvRed
                else                              -> Color.Transparent
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp, vertical = 4.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(color)
                    .padding(horizontal = 20.dp),
                contentAlignment = if (direction == SwipeToDismissBoxValue.StartToEnd)
                    Alignment.CenterStart else Alignment.CenterEnd,
            ) {
                if (direction == SwipeToDismissBoxValue.StartToEnd) {
                    Text(if (chat.muted) "Unmute" else "Mute", color = Color.White, fontWeight = FontWeight.Bold)
                } else {
                    Text("Delete", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        },
        enableDismissFromStartToEnd = true,
        enableDismissFromEndToStart = true,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 4.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(if (chat.pinned) VyboraLavender.copy(alpha = 0.5f) else VyboraSurface)
                .border(1.dp, VyboraBorder, RoundedCornerShape(18.dp))
                .clickable(onClick = onClick)
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Avatar
            Box(Modifier.size(50.dp)) {
                if (chat.isGroup) {
                    Box(
                        Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(VyboraLavender),
                        contentAlignment = Alignment.Center,
                    ) {
                        Image(
                            painter = painterResource(group?.avatarRes?.takeIf { it != 0 } ?: R.drawable.vb_chat_ic_groups),
                            contentDescription = displayName,
                            modifier = Modifier.size(46.dp).clip(CircleShape),
                            contentScale = ContentScale.Crop,
                        )
                    }
                    Box(
                        Modifier
                            .align(Alignment.BottomEnd)
                            .size(16.dp)
                            .clip(CircleShape)
                            .background(VyboraPurple)
                            .border(1.5.dp, VyboraSurface, CircleShape),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.vb_chat_ic_groups),
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(9.dp),
                        )
                    }
                } else {
                    NovaAvatarImage(id = chat.userId, size = 50.dp)
                    if (user?.online == true) {
                        Box(
                            Modifier
                                .align(Alignment.BottomEnd)
                                .size(13.dp)
                                .clip(CircleShape)
                                .background(MintGreen)
                                .border(2.dp, VyboraSurface, CircleShape),
                        )
                    }
                }
            }

            Spacer(Modifier.width(12.dp))

            // Name + Badges + Last Message
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (chat.pinned) {
                        Icon(
                            painter = painterResource(R.drawable.vb_chat_ic_pin_chat),
                            contentDescription = "Pinned",
                            tint = Color.Unspecified,
                            modifier = Modifier.size(12.dp),
                        )
                        Spacer(Modifier.width(4.dp))
                    }

                    Text(
                        displayName,
                        color = VyboraTextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false),
                    )

                    // Live Voice Room Badge
                    if (isInRoom) {
                        Spacer(Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(LiveRed.copy(alpha = 0.12f))
                                .clickable { activeRoom?.let { onRoomPreview(it) } }
                                .padding(horizontal = 5.dp, vertical = 2.dp),
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(Modifier.size(5.dp).background(LiveRed, CircleShape))
                                Spacer(Modifier.width(3.dp))
                                Text("LIVE", color = LiveRed, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    // Verified / Official Badge
                    if (user?.isOfficial == true) {
                        Spacer(Modifier.width(5.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(VyboraCyanSoft)
                                .padding(horizontal = 4.dp, vertical = 1.dp),
                        ) {
                            Image(
                                painter = painterResource(R.drawable.vb_chat_badge_official),
                                contentDescription = null,
                                modifier = Modifier.size(10.dp),
                            )
                            Spacer(Modifier.width(2.dp))
                            Text("Official", color = VyboraCyan, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    // Family Badge
                    if (user?.isFamily == true) {
                        Spacer(Modifier.width(5.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(VyboraLavender)
                                .padding(horizontal = 4.dp, vertical = 1.dp),
                        ) {
                            Image(
                                painter = painterResource(R.drawable.vb_chat_badge_family),
                                contentDescription = null,
                                modifier = Modifier.size(10.dp),
                            )
                            Spacer(Modifier.width(2.dp))
                            Text("Family", color = VyboraPurple, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    if (chat.muted) {
                        Spacer(Modifier.width(5.dp))
                        Icon(
                            painter = painterResource(R.drawable.vb_chat_ic_mute),
                            contentDescription = "Muted",
                            tint = Color.Unspecified,
                            modifier = Modifier.size(13.dp),
                        )
                    }
                }

                Spacer(Modifier.height(3.dp))

                // Message preview with icon
                Text(
                    chat.displayText,
                    color = if (chat.unread > 0) VyboraTextPrimary else VyboraTextSecondary,
                    fontSize = 13.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = if (chat.unread > 0) FontWeight.Medium else FontWeight.Normal,
                )
            }

            Spacer(Modifier.width(8.dp))

            // Timestamp + Unread Badge
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    relativeTime(chat.lastTs),
                    color = if (chat.unread > 0) VyboraCyan else VyboraTextSecondary,
                    fontSize = 11.sp,
                    fontWeight = if (chat.unread > 0) FontWeight.SemiBold else FontWeight.Normal,
                )
                if (chat.unread > 0) {
                    Spacer(Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .background(
                                if (chat.muted)
                                    Brush.horizontalGradient(listOf(VyboraTextSecondary, VyboraTextSecondary))
                                else
                                    Brush.horizontalGradient(listOf(VyboraPurple, VyboraCyan)),
                                RoundedCornerShape(10.dp),
                            )
                            .padding(horizontal = 7.dp, vertical = 2.dp),
                    ) {
                        Text(
                            if (chat.unread > 99) "99+" else "${chat.unread}",
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }
        }

        // Swipe handler
        LaunchedEffect(dismissState.currentValue) {
            when (dismissState.currentValue) {
                SwipeToDismissBoxValue.StartToEnd -> { onMute(); dismissState.reset() }
                SwipeToDismissBoxValue.EndToStart -> { onDelete(); dismissState.reset() }
                else -> {}
            }
        }
    }
}

// ─── People Row ──────────────────────────────────────────────────────────────

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
            .padding(horizontal = 20.dp, vertical = 4.dp)
            .background(VyboraSurface, RoundedCornerShape(16.dp))
            .border(1.dp, VyboraBorder, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        NovaAvatarImage(id = user.id, size = 44.dp)
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    user.name,
                    color = VyboraTextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                )
                if (user.isOfficial) {
                    Spacer(Modifier.width(4.dp))
                    Text("Official", color = VyboraCyan, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                }
            }
            Text(
                "@${user.id} · Lv ${user.level}" + if (user.online) " · Online" else "",
                color = VyboraTextSecondary,
                fontSize = 12.sp,
                maxLines = 1,
            )
        }
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(
                    if (rel == "friend") Brush.horizontalGradient(listOf(MintGreen, VyboraCyan))
                    else Brush.horizontalGradient(listOf(VyboraPurple, VyboraCyan))
                )
                .clickable(onClick = onAction)
                .padding(horizontal = 12.dp, vertical = 6.dp),
        ) {
            Text(
                if (rel == "friend") "Message" else "Add",
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

// ─── Empty States (Section 17) ────────────────────────────────────────────────

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
            .padding(top = 40.dp, bottom = 20.dp, start = 24.dp, end = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(VyboraLavender),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(
                    if (filter == ChatFilter.GROUPS) R.drawable.vb_chat_ic_groups
                    else R.drawable.vb_chat_fx_sparkle
                ),
                contentDescription = null,
                modifier = Modifier.size(54.dp),
            )
        }
        Spacer(Modifier.height(18.dp))
        Text(
            when (filter) {
                ChatFilter.UNREAD -> "All caught up!"
                ChatFilter.GROUPS -> "No group chats yet"
                ChatFilter.FRIENDS -> "No friend messages yet"
                else -> "No conversations yet"
            },
            color = VyboraTextPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            when (filter) {
                ChatFilter.UNREAD -> "You have read all received messages."
                ChatFilter.GROUPS -> "Create a group to chat with multiple friends at once!"
                ChatFilter.FRIENDS -> "Add friends from the lobby to start personal chats."
                else -> "Connect with friends and start chatting on VYBORA"
            },
            color = VyboraTextSecondary,
            fontSize = 13.sp,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(20.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            if (filter == ChatFilter.GROUPS) {
                Box(
                    Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(Brush.horizontalGradient(listOf(VyboraPurple, VyboraCyan)))
                        .clickable(onClick = onCreateGroup)
                        .padding(horizontal = 20.dp, vertical = 10.dp),
                ) {
                    Text("Create Group", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            } else {
                Box(
                    Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(Brush.horizontalGradient(listOf(VyboraPurple, VyboraCyan)))
                        .clickable(onClick = onStartChat)
                        .padding(horizontal = 18.dp, vertical = 10.dp),
                ) {
                    Text("Start a Chat", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
                Box(
                    Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(VyboraLavender)
                        .border(1.dp, VyboraBorder, RoundedCornerShape(14.dp))
                        .clickable(onClick = onFindFriends)
                        .padding(horizontal = 18.dp, vertical = 10.dp),
                ) {
                    Text("Find Friends", color = VyboraPurple, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
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
            .padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painter = painterResource(R.drawable.vb_chat_ic_search_empty),
            contentDescription = null,
            modifier = Modifier.size(64.dp),
        )
        Spacer(Modifier.height(14.dp))
        Text(
            "No results for \"$query\"",
            color = VyboraTextPrimary,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(6.dp))
        Text(
            "Try searching a different name, alias, or message text",
            color = VyboraTextSecondary,
            fontSize = 13.sp,
            textAlign = TextAlign.Center,
        )
    }
}

// ─── Gift Pack FAB ───────────────────────────────────────────────────────────

@Composable
private fun NovaGiftPackFab(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    var dismissed by rememberSaveable { mutableStateOf(false) }

    AnimatedVisibility(
        visible = !dismissed,
        enter = scaleIn() + fadeIn(),
        exit = fadeOut(),
        modifier = modifier,
    ) {
        Box(
            Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(
                    Brush.linearGradient(
                        listOf(Color(0xFFEC78B9), Color(0xFF8258EE)),
                    )
                )
                .clickable(onClick = onClick)
                .padding(horizontal = 14.dp, vertical = 10.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(R.drawable.vb_chat_bg_gift_offer),
                    contentDescription = null,
                    modifier = Modifier.size(28.dp).clip(RoundedCornerShape(6.dp)),
                    contentScale = ContentScale.Crop,
                )
                Spacer(Modifier.width(8.dp))
                Column {
                    Text("Gift Pack", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text("48h Offer", color = Color.White.copy(alpha = 0.8f), fontSize = 10.sp)
                }
                Spacer(Modifier.width(8.dp))
                Box(
                    Modifier
                        .size(18.dp)
                        .background(Color.White.copy(alpha = 0.2f), CircleShape)
                        .clickable { dismissed = true },
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        painter = painterResource(R.drawable.vb_chat_ic_close),
                        contentDescription = "Dismiss",
                        tint = Color.Unspecified,
                        modifier = Modifier.size(10.dp),
                    )
                }
            }
        }
    }
}

// ─── New Group Bottom Sheet (Section 9) ──────────────────────────────────────

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
    var selectedAvatarIndex by remember { mutableStateOf(6) } // mascot 7
    val selectedFriendIds = remember { mutableStateOf(setOf<String>()) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = VyboraSurface,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
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
                    tint = VyboraPurple,
                    modifier = Modifier.size(26.dp),
                )
                Spacer(Modifier.width(10.dp))
                Column(Modifier.weight(1f)) {
                    Text("Create New Group", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = VyboraTextPrimary)
                    Text("Chat, play and party together", fontSize = 12.sp, color = VyboraTextSecondary)
                }
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(VyboraBg)
                        .clickable(onClick = onDismiss),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        painter = painterResource(R.drawable.vb_chat_ic_close),
                        contentDescription = "Close",
                        tint = Color.Unspecified,
                        modifier = Modifier.size(12.dp),
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // Avatar picker row
            Text("Select Group Mascot", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = VyboraTextPrimary)
            Spacer(Modifier.height(8.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(NOVA_AVATARS.indices.toList()) { index ->
                    val isSel = selectedAvatarIndex == index
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .border(
                                2.dp,
                                if (isSel) VyboraCyan else Color.Transparent,
                                CircleShape
                            )
                            .clickable { selectedAvatarIndex = index },
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
            Text("Group Name", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = VyboraTextPrimary)
            Spacer(Modifier.height(6.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(VyboraBg, RoundedCornerShape(14.dp))
                    .border(1.dp, VyboraBorder, RoundedCornerShape(14.dp))
                    .padding(horizontal = 14.dp, vertical = 10.dp),
            ) {
                BasicTextField(
                    value = groupName,
                    onValueChange = { groupName = it },
                    singleLine = true,
                    textStyle = TextStyle(color = VyboraTextPrimary, fontSize = 14.sp),
                    cursorBrush = SolidColor(VyboraCyan),
                    modifier = Modifier.fillMaxWidth(),
                    decorationBox = { field ->
                        if (groupName.isEmpty()) {
                            Text("e.g. Werewolf Nights Squad", color = VyboraTextSecondary, fontSize = 13.sp)
                        }
                        field()
                    },
                )
            }

            Spacer(Modifier.height(12.dp))

            // Group description
            Text("Description (Optional)", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = VyboraTextPrimary)
            Spacer(Modifier.height(6.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(VyboraBg, RoundedCornerShape(14.dp))
                    .border(1.dp, VyboraBorder, RoundedCornerShape(14.dp))
                    .padding(horizontal = 14.dp, vertical = 8.dp),
            ) {
                BasicTextField(
                    value = groupDesc,
                    onValueChange = { groupDesc = it },
                    singleLine = false,
                    maxLines = 2,
                    textStyle = TextStyle(color = VyboraTextPrimary, fontSize = 13.sp),
                    cursorBrush = SolidColor(VyboraCyan),
                    modifier = Modifier.fillMaxWidth(),
                    decorationBox = { field ->
                        if (groupDesc.isEmpty()) {
                            Text("What is this group about?", color = VyboraTextSecondary, fontSize = 12.sp)
                        }
                        field()
                    },
                )
            }

            Spacer(Modifier.height(14.dp))

            // Friends Selection
            Text("Add Friends (${selectedFriendIds.value.size} selected)", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = VyboraTextPrimary)
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
                            .background(if (isChecked) VyboraLavender.copy(alpha = 0.5f) else VyboraBg, RoundedCornerShape(12.dp))
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
                        Text(f.name, color = VyboraTextPrimary, fontSize = 14.sp, modifier = Modifier.weight(1f))
                        Box(
                            modifier = Modifier
                                .size(22.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isChecked) VyboraCyan else VyboraSurface)
                                .border(1.dp, if (isChecked) Color.Transparent else VyboraBorder, RoundedCornerShape(6.dp)),
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
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        if (groupName.isNotBlank() && selectedFriendIds.value.isNotEmpty())
                            Brush.horizontalGradient(listOf(VyboraPurple, VyboraCyan))
                        else
                            SolidColor(VyboraBorder)
                    )
                    .clickable(enabled = groupName.isNotBlank() && selectedFriendIds.value.isNotEmpty()) {
                        onCreate(
                            groupName.trim(),
                            selectedFriendIds.value.toList(),
                            groupDesc.trim(),
                            NOVA_AVATARS[selectedAvatarIndex],
                        )
                    }
                    .padding(vertical = 14.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text("Create Group", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}

// ─── Inbox / Requests Bottom Sheet (Section 12) ──────────────────────────────

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
    var tabIndex by remember { mutableStateOf(0) } // 0: All / Notifs, 1: Friend Requests, 2: Room Invites

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = VyboraSurface,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
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
                    tint = VyboraPurple,
                    modifier = Modifier.size(26.dp),
                )
                Spacer(Modifier.width(10.dp))
                Column(Modifier.weight(1f)) {
                    Text("Inbox & Requests", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = VyboraTextPrimary)
                    Text("Friend requests, room invites & gifts", fontSize = 12.sp, color = VyboraTextSecondary)
                }
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(VyboraBg)
                        .clickable(onClick = onDismiss),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        painter = painterResource(R.drawable.vb_chat_ic_close),
                        contentDescription = "Close",
                        tint = Color.Unspecified,
                        modifier = Modifier.size(12.dp),
                    )
                }
            }

            Spacer(Modifier.height(14.dp))

            // Tab bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(VyboraBg, RoundedCornerShape(12.dp))
                    .padding(4.dp),
            ) {
                val tabs = listOf("All Alerts", "Friend Requests (${incomingRequests.size})", "Room Invites")
                tabs.forEachIndexed { i, title ->
                    val isSel = tabIndex == i
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSel) VyboraSurface else Color.Transparent)
                            .clickable { tabIndex = i }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            title,
                            color = if (isSel) VyboraPurple else VyboraTextSecondary,
                            fontSize = 12.sp,
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
                            Text("No pending friend requests", color = VyboraTextSecondary, fontSize = 13.sp)
                        }
                    } else {
                        LazyColumn(modifier = Modifier.fillMaxWidth().height(260.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(incomingRequests, key = { it.id }) { u ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(VyboraBg, RoundedCornerShape(14.dp))
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    NovaAvatarImage(id = u.id, size = 42.dp)
                                    Spacer(Modifier.width(10.dp))
                                    Column(Modifier.weight(1f)) {
                                        Text(u.name, color = VyboraTextPrimary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                                        Text("Wants to connect", color = VyboraTextSecondary, fontSize = 12.sp)
                                    }
                                    Box(
                                        Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Brush.horizontalGradient(listOf(VyboraPurple, VyboraCyan)))
                                            .clickable { onAcceptFriend(u.id) }
                                            .padding(horizontal = 12.dp, vertical = 6.dp),
                                    ) {
                                        Text("Accept", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Spacer(Modifier.width(6.dp))
                                    Box(
                                        Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(VyboraLavender)
                                            .clickable { onDeclineFriend(u.id) }
                                            .padding(horizontal = 10.dp, vertical = 6.dp),
                                    ) {
                                        Text("Decline", color = VyboraTextPrimary, fontSize = 11.sp)
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
                            Text("No room invitations at the moment", color = VyboraTextSecondary, fontSize = 13.sp)
                        }
                    } else {
                        LazyColumn(modifier = Modifier.fillMaxWidth().height(260.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(roomNotifs, key = { it.id }) { n ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(VyboraBg, RoundedCornerShape(14.dp))
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
                                        Text(n.text, color = VyboraTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                                        Text(relativeTime(n.ts), color = VyboraTextSecondary, fontSize = 11.sp)
                                    }
                                    n.roomId?.let { rid ->
                                        Box(
                                            Modifier
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(Brush.horizontalGradient(listOf(LiveRed, VyboraPurple)))
                                                .clickable { onJoinRoom(rid) }
                                                .padding(horizontal = 12.dp, vertical = 6.dp),
                                        ) {
                                            Text("Join Room", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
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
                            Text("No notifications yet", color = VyboraTextSecondary, fontSize = 13.sp)
                        }
                    } else {
                        LazyColumn(modifier = Modifier.fillMaxWidth().height(260.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(notifications, key = { it.id }) { n ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(VyboraBg, RoundedCornerShape(14.dp))
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
                                        tint = VyboraCyan,
                                        modifier = Modifier.size(24.dp),
                                    )
                                    Spacer(Modifier.width(10.dp))
                                    Column(Modifier.weight(1f)) {
                                        Text(n.text, color = VyboraTextPrimary, fontSize = 13.sp)
                                        Text(relativeTime(n.ts), color = VyboraTextSecondary, fontSize = 11.sp)
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

// ─── Room Preview Modal (Section 8.1) ────────────────────────────────────────

@Composable
internal fun RoomPreviewModal(
    room: RoomCard,
    onDismiss: () -> Unit,
    onJoin: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = VyboraSurface,
        shape = RoundedCornerShape(22.dp),
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
                        .background(VyboraLavender),
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
                                    listOf(Color.Transparent, Color(0xCC17112F))
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
                    room.title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = VyboraTextPrimary,
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "Hosted by ${room.host} · ${room.members} listening",
                    fontSize = 13.sp,
                    color = VyboraTextSecondary,
                    textAlign = TextAlign.Center,
                )
                if (room.activityText != null) {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        room.activityText,
                        fontSize = 12.sp,
                        color = VyboraCyan,
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
                            .clip(RoundedCornerShape(14.dp))
                            .background(VyboraLavender)
                            .clickable(onClick = onDismiss)
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text("Close", color = VyboraTextPrimary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    }
                    Box(
                        modifier = Modifier
                            .weight(1.5f)
                            .clip(RoundedCornerShape(14.dp))
                            .background(Brush.horizontalGradient(listOf(LiveRed, VyboraPurple)))
                            .clickable(onClick = onJoin)
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text("Join Room", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        },
        confirmButton = {},
    )
}

// ─── Archived Chats Bottom Sheet (Section 4) ─────────────────────────────────

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
        containerColor = VyboraSurface,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
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
                    tint = VyboraPurple,
                    modifier = Modifier.size(26.dp),
                )
                Spacer(Modifier.width(10.dp))
                Column(Modifier.weight(1f)) {
                    Text("Archived Chats", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = VyboraTextPrimary)
                    Text("${archived.size} conversations hidden", fontSize = 12.sp, color = VyboraTextSecondary)
                }
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(VyboraBg)
                        .clickable(onClick = onDismiss),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        painter = painterResource(R.drawable.vb_chat_ic_close),
                        contentDescription = "Close",
                        tint = Color.Unspecified,
                        modifier = Modifier.size(12.dp),
                    )
                }
            }

            Spacer(Modifier.height(14.dp))

            if (archived.isEmpty()) {
                Box(Modifier.fillMaxWidth().height(160.dp), contentAlignment = Alignment.Center) {
                    Text("No archived conversations", color = VyboraTextSecondary, fontSize = 13.sp)
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxWidth().height(260.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(archived, key = { it.userId }) { c ->
                        val u = store.userById(c.userId)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(VyboraBg, RoundedCornerShape(14.dp))
                                .clickable { onOpenChat(c.userId) }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            NovaAvatarImage(id = c.userId, size = 42.dp)
                            Spacer(Modifier.width(10.dp))
                            Column(Modifier.weight(1f)) {
                                Text(u?.name ?: c.userId, color = VyboraTextPrimary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                                Text(c.displayText, color = VyboraTextSecondary, fontSize = 12.sp, maxLines = 1)
                            }
                            Box(
                                Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(VyboraLavender)
                                    .clickable { onUnarchive(c.userId) }
                                    .padding(horizontal = 12.dp, vertical = 6.dp),
                            ) {
                                Text("Unarchive", color = VyboraPurple, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}

// ─── Contacts Bottom Sheet ───────────────────────────────────────────────────

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
        containerColor = VyboraSurface,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
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
                    tint = Color.Unspecified,
                    modifier = Modifier.size(28.dp),
                )
                Spacer(Modifier.width(10.dp))
                Column(Modifier.weight(1f)) {
                    Text("Contacts & Friends", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = VyboraTextPrimary)
                    Text("${friends.size} friends connected", fontSize = 12.sp, color = VyboraTextSecondary)
                }
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(VyboraBg)
                        .clickable(onClick = onDismiss),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        painter = painterResource(R.drawable.vb_chat_ic_close),
                        contentDescription = "Close",
                        tint = Color.Unspecified,
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
                    .background(VyboraLavender, RoundedCornerShape(14.dp))
                    .padding(horizontal = 12.dp, vertical = 10.dp),
            ) {
                Icon(
                    painter = painterResource(R.drawable.vb_chat_ic_search),
                    contentDescription = null,
                    tint = Color.Unspecified,
                    modifier = Modifier.size(16.dp),
                )
                Spacer(Modifier.width(8.dp))
                BasicTextField(
                    value = filterQuery,
                    onValueChange = { filterQuery = it },
                    singleLine = true,
                    textStyle = TextStyle(color = VyboraTextPrimary, fontSize = 14.sp),
                    cursorBrush = SolidColor(VyboraCyan),
                    modifier = Modifier.weight(1f),
                    decorationBox = { inner ->
                        Box {
                            if (filterQuery.isEmpty()) {
                                Text("Filter contacts…", color = VyboraTextSecondary, fontSize = 13.sp)
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
                            .background(VyboraBg, RoundedCornerShape(14.dp))
                            .clickable { onChat(f.id) }
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        NovaAvatarImage(id = f.id, size = 44.dp)
                        Spacer(Modifier.width(10.dp))
                        Column(Modifier.weight(1f)) {
                            Text(f.name, color = VyboraTextPrimary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                            Text(
                                "@${f.id} · Lv ${f.level}" + if (f.online) " · Online" else "",
                                color = VyboraTextSecondary,
                                fontSize = 11.sp,
                            )
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(Brush.horizontalGradient(listOf(VyboraPurple, VyboraCyan)))
                                .clickable { onChat(f.id) }
                                .padding(horizontal = 14.dp, vertical = 7.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text("Chat", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}

// ─── New Chat / Search Bottom Sheet ──────────────────────────────────────────

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
        containerColor = VyboraSurface,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
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
                    tint = Color.Unspecified,
                    modifier = Modifier.size(26.dp),
                )
                Spacer(Modifier.width(10.dp))
                Column(Modifier.weight(1f)) {
                    Text("New Conversation", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = VyboraTextPrimary)
                    Text("Choose someone to message", fontSize = 12.sp, color = VyboraTextSecondary)
                }
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(VyboraBg)
                        .clickable(onClick = onDismiss),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        painter = painterResource(R.drawable.vb_chat_ic_close),
                        contentDescription = "Close",
                        tint = Color.Unspecified,
                        modifier = Modifier.size(12.dp),
                    )
                }
            }

            Spacer(Modifier.height(14.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(VyboraLavender, RoundedCornerShape(14.dp))
                    .padding(horizontal = 12.dp, vertical = 10.dp),
            ) {
                Icon(
                    painter = painterResource(R.drawable.vb_chat_ic_search),
                    contentDescription = null,
                    tint = Color.Unspecified,
                    modifier = Modifier.size(16.dp),
                )
                Spacer(Modifier.width(8.dp))
                BasicTextField(
                    value = query,
                    onValueChange = { query = it },
                    singleLine = true,
                    textStyle = TextStyle(color = VyboraTextPrimary, fontSize = 14.sp),
                    cursorBrush = SolidColor(VyboraCyan),
                    modifier = Modifier.weight(1f),
                    decorationBox = { inner ->
                        Box {
                            if (query.isEmpty()) {
                                Text("Search people by name or ID…", color = VyboraTextSecondary, fontSize = 13.sp)
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
                            .background(VyboraBg, RoundedCornerShape(14.dp))
                            .clickable { onSelectUser(u.id) }
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        NovaAvatarImage(id = u.id, size = 44.dp)
                        Spacer(Modifier.width(10.dp))
                        Column(Modifier.weight(1f)) {
                            Text(u.name, color = VyboraTextPrimary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                            Text(
                                "@${u.id} · Lv ${u.level}" + if (u.online) " · Online" else "",
                                color = VyboraTextSecondary,
                                fontSize = 11.sp,
                            )
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(Brush.horizontalGradient(listOf(VyboraPurple, VyboraCyan)))
                                .clickable { onSelectUser(u.id) }
                                .padding(horizontal = 14.dp, vertical = 7.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text("Message", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}

// ─── Shared NovaAvatarImage Helper ───────────────────────────────────────────

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

// ─── Relative Time Helper ────────────────────────────────────────────────────

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
