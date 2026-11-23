package com.sundram.weplay.ui.screens

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.sundram.weplay.R
import com.sundram.weplay.data.AppNotification
import com.sundram.weplay.data.Chat
import com.sundram.weplay.data.DirectoryUser
import com.sundram.weplay.data.MessagingStore
import com.sundram.weplay.data.NotificationKind
import com.sundram.weplay.ui.nav.Routes
import com.sundram.weplay.ui.theme.AuroraChatsBackdrop
import com.sundram.weplay.ui.theme.AuroraChatsEmptyState
import com.sundram.weplay.ui.theme.AuroraChatsTokens
import com.sundram.weplay.ui.theme.AuroraFramedAvatar

private enum class MsgTab { CHATS, PEOPLE, ALERTS }
private enum class PeopleFilter { ALL, FOLLOWING, FOLLOWERS, FRIENDS }
private enum class AlertFilter { ALL, SYSTEM, SOCIAL, ACTIVITY }

private val AVATAR_FRAMES = listOf(
    R.drawable.ap_chats_hybrid_frame_royal,
    R.drawable.ap_chats_hybrid_frame_amethyst,
    R.drawable.ap_chats_hybrid_frame_rose,
    R.drawable.ap_chats_hybrid_frame_crystal,
)

fun relativeTime(ts: Long): String {
    if (ts == 0L) return ""
    val diff = System.currentTimeMillis() - ts
    val min = diff / 60_000
    if (min < 1) return "now"
    if (min < 60) return "${min}m"
    val hr = min / 60
    if (hr < 24) return "${hr}h"
    val day = hr / 24
    if (day < 7) return "${day}d"
    return "${day / 7}w"
}

@Composable
fun MessagesScreen(
    nav: NavController,
    pad: PaddingValues,
    store: MessagingStore,
    onUnreadChange: (Int) -> Unit = {},
) {
    var tab by remember { mutableStateOf(MsgTab.CHATS) }
    var peopleFilter by remember { mutableStateOf(PeopleFilter.ALL) }
    var alertFilter by remember { mutableStateOf(AlertFilter.ALL) }
    var version by remember { mutableStateOf(0) }
    var query by remember { mutableStateOf("") }
    var confirmDecline by remember { mutableStateOf<DirectoryUser?>(null) }
    var confirmUnblock by remember { mutableStateOf<DirectoryUser?>(null) }
    var peopleRefreshNonce by remember { mutableStateOf(0) }

    fun bump() { version++ }

    // Seed demo data once per screen entry
    LaunchedEffect(Unit) {
        store.seedIfEmpty()
        bump()
    }

    val snapshot = remember(version) {
        object {
            val chats = store.allChats()
            val friends = store.friends()
            val incoming = store.incomingRequests()
            val outgoing = store.outgoingRequests()
            val blockedList = store.blockedUsers()
            val notifs = store.notifications()
            val unreadNotifs = store.unreadNotifications()
            val unreadTotal = store.unreadTotal()
        }
    }
    val chats = snapshot.chats
    val friends = snapshot.friends
    val incoming = snapshot.incoming
    val outgoing = snapshot.outgoing
    val blockedList = snapshot.blockedList
    val notifs = snapshot.notifs
    val unreadNotifs = snapshot.unreadNotifs
    val onlineFriends = remember(snapshot) { friends.filter { it.online } }

    LaunchedEffect(snapshot.unreadTotal) {
        onUnreadChange(snapshot.unreadTotal)
    }

    val backgroundRes = when (tab) {
        MsgTab.CHATS -> R.drawable.ap_chats_hybrid_bg_inbox
        MsgTab.PEOPLE -> R.drawable.ap_chats_hybrid_bg_people
        MsgTab.ALERTS -> R.drawable.ap_chats_hybrid_bg_alerts
    }

    AuroraChatsBackdrop(
        backgroundRes = backgroundRes,
        modifier = Modifier
            .fillMaxSize()
            .padding(pad),
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
        ) {
            // ==================== 1. TOP PALACE HEADER ====================
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(115.dp),
            ) {
                // Palace banner artwork
                Image(
                    painter = painterResource(R.drawable.ap_chats_hybrid_header_palace),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                )
                // Gradient scrim at bottom of header for smooth blend
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color.Transparent,
                                    AuroraChatsTokens.BgBase.copy(alpha = 0.85f),
                                )
                            )
                        )
                )

                // Header Top Row Content
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .align(Alignment.BottomStart),
                ) {
                    if (tab != MsgTab.CHATS) {
                        Image(
                            painter = painterResource(R.drawable.ap_chats_hybrid_ic_back),
                            contentDescription = "Back",
                            modifier = Modifier
                                .size(34.dp)
                                .clickable { tab = MsgTab.CHATS }
                                .padding(4.dp),
                        )
                        Spacer(Modifier.width(8.dp))
                    }

                    Column(Modifier.weight(1f)) {
                        Text(
                            text = when (tab) {
                                MsgTab.CHATS -> "Chats"
                                MsgTab.PEOPLE -> "People"
                                MsgTab.ALERTS -> "Alerts"
                            },
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = AuroraChatsTokens.TextPrimary,
                        )
                        Text(
                            text = when (tab) {
                                MsgTab.CHATS -> "${chats.size} active chat" + if (chats.size != 1) "s" else ""
                                MsgTab.PEOPLE -> "${friends.size} friends"
                                MsgTab.ALERTS -> if (unreadNotifs > 0) "$unreadNotifs unread alerts" else "All caught up"
                            },
                            fontSize = 12.sp,
                            color = AuroraChatsTokens.TextSecondary,
                        )
                    }

                    Spacer(Modifier.width(8.dp))

                    if (tab == MsgTab.CHATS) {
                        // Bell with unread badge
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clickable { tab = MsgTab.ALERTS },
                            contentAlignment = Alignment.Center,
                        ) {
                            Image(
                                painter = painterResource(R.drawable.ap_chats_hybrid_ic_bell),
                                contentDescription = "Alerts",
                                modifier = Modifier.size(28.dp),
                            )
                            if (unreadNotifs > 0) {
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .size(16.dp)
                                        .background(AuroraChatsTokens.RoseAlert, CircleShape),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Text(
                                        text = if (unreadNotifs > 9) "9+" else "$unreadNotifs",
                                        color = Color.White,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                    )
                                }
                            }
                        }

                        Spacer(Modifier.width(8.dp))

                        // Compose icon
                        Image(
                            painter = painterResource(R.drawable.ap_chats_hybrid_ic_compose),
                            contentDescription = "Compose",
                            modifier = Modifier
                                .size(32.dp)
                                .clickable { tab = MsgTab.PEOPLE }
                                .padding(2.dp),
                        )
                    } else if (tab == MsgTab.ALERTS && notifs.isNotEmpty()) {
                        TextButton(
                            onClick = { store.clearNotifications(); bump() },
                        ) {
                            Text(
                                "Clear all",
                                color = AuroraChatsTokens.CrystalCyan,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                            )
                        }
                    }
                }
            }

            // ==================== 2. SEARCH BAR ====================
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .background(AuroraChatsTokens.Surface, RoundedCornerShape(16.dp))
                    .border(1.dp, AuroraChatsTokens.CardBorder, RoundedCornerShape(16.dp))
                    .padding(horizontal = 14.dp, vertical = 11.dp),
                contentAlignment = Alignment.CenterStart,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Image(
                        painter = painterResource(R.drawable.ap_chats_hybrid_ic_search),
                        contentDescription = "Search",
                        modifier = Modifier.size(18.dp),
                    )
                    Spacer(Modifier.width(10.dp))
                    BasicTextField(
                        value = query,
                        onValueChange = { query = it },
                        singleLine = true,
                        textStyle = TextStyle(
                            color = AuroraChatsTokens.TextPrimary,
                            fontSize = 14.sp,
                        ),
                        cursorBrush = SolidColor(AuroraChatsTokens.CrystalCyan),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        modifier = Modifier.weight(1f),
                        decorationBox = { field ->
                            Box {
                                if (query.isEmpty()) {
                                    Text(
                                        "Search people by name or username",
                                        color = AuroraChatsTokens.TextSecondary.copy(alpha = 0.6f),
                                        fontSize = 13.sp,
                                    )
                                }
                                field()
                            }
                        },
                    )
                    if (query.isNotEmpty()) {
                        Text(
                            text = "✕",
                            color = AuroraChatsTokens.TextSecondary,
                            fontSize = 14.sp,
                            modifier = Modifier
                                .clickable { query = "" }
                                .padding(horizontal = 4.dp),
                        )
                    }
                }
            }

            // ==================== 3. TAB PILLS ====================
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                MsgTab.entries.forEach { t ->
                    val selected = tab == t
                    val label = when (t) {
                        MsgTab.CHATS -> "Chats"
                        MsgTab.PEOPLE -> "People"
                        MsgTab.ALERTS -> "Alerts" + if (unreadNotifs > 0) " ($unreadNotifs)" else ""
                    }
                    Box(
                        modifier = Modifier
                            .background(
                                if (selected) AuroraChatsTokens.ActiveTabGradient
                                else Brush.linearGradient(
                                    listOf(
                                        AuroraChatsTokens.SurfaceElevated.copy(alpha = 0.7f),
                                        AuroraChatsTokens.Surface.copy(alpha = 0.7f),
                                    )
                                ),
                                RoundedCornerShape(20.dp),
                            )
                            .border(
                                1.dp,
                                if (selected) AuroraChatsTokens.ActiveCyan else AuroraChatsTokens.CardBorder,
                                RoundedCornerShape(20.dp),
                            )
                            .clickable { tab = t }
                            .padding(horizontal = 16.dp, vertical = 7.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            label,
                            color = if (selected) Color.White else AuroraChatsTokens.TextSecondary,
                            fontSize = 12.sp,
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                        )
                    }
                }
            }

            // ==================== 4. TAB BODY ====================
            LazyColumn(
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 6.dp, bottom = 28.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f),
            ) {
                when (tab) {
                    MsgTab.CHATS -> {
                        if (query.isNotBlank()) {
                            // Search Mode in Chats tab
                            val results = store.search(query)
                            item {
                                HybridSectionHeader("Search results", "${results.size}")
                            }
                            items(results, key = { "cs" + it.id }) { u ->
                                HybridUserRow(
                                    u = u,
                                    rel = store.relationship(u.id),
                                    frameRes = AVATAR_FRAMES[Math.abs(u.id.hashCode()) % AVATAR_FRAMES.size],
                                    onActionClick = {
                                        when (store.relationship(u.id)) {
                                            "friend" -> nav.navigate("chat/${u.id}")
                                            "incoming" -> { store.acceptRequest(u.id); bump() }
                                            "outgoing" -> { store.cancelRequest(u.id); bump() }
                                            "blocked" -> { confirmUnblock = u }
                                            else -> { store.sendRequest(u.id); bump() }
                                        }
                                    },
                                    onClick = { nav.navigate("user/${u.id}") },
                                )
                            }
                            if (results.isEmpty()) {
                                item {
                                    AuroraChatsEmptyState(
                                        illustrationRes = R.drawable.ap_chats_hybrid_empty_no_results,
                                        title = "No people found",
                                        subtitle = "No results matching \"$query\". Check the username spelling.",
                                    )
                                }
                            }
                        } else {
                            // 1. Online Now Carousel
                            if (onlineFriends.isNotEmpty()) {
                                item {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                    ) {
                                        Text(
                                            "Online now",
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = AuroraChatsTokens.TextPrimary,
                                        )
                                        Spacer(Modifier.weight(1f))
                                        Text(
                                            "${onlineFriends.size} >",
                                            fontSize = 12.sp,
                                            color = AuroraChatsTokens.TextSecondary,
                                        )
                                    }
                                    Spacer(Modifier.height(8.dp))
                                    LazyRow(
                                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                                        contentPadding = PaddingValues(horizontal = 4.dp),
                                    ) {
                                        items(onlineFriends, key = { "on" + it.id }) { u ->
                                            Column(
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                modifier = Modifier.clickable {
                                                    nav.navigate("chat/${u.id}")
                                                },
                                            ) {
                                                val frameRes = AVATAR_FRAMES[Math.abs(u.id.hashCode()) % AVATAR_FRAMES.size]
                                                AuroraFramedAvatar(
                                                    name = u.name,
                                                    size = 56.dp,
                                                    frameRes = frameRes,
                                                    online = true,
                                                )
                                                Spacer(Modifier.height(6.dp))
                                                Text(
                                                    u.name,
                                                    color = AuroraChatsTokens.TextPrimary,
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Medium,
                                                    maxLines = 1,
                                                )
                                            }
                                        }
                                    }
                                    Spacer(Modifier.height(8.dp))
                                }
                            }

                            // 2. Recent Chats Section
                            item {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp, bottom = 2.dp),
                                ) {
                                    Text(
                                        "Recent chats",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = AuroraChatsTokens.TextPrimary,
                                    )
                                    Spacer(Modifier.weight(1f))
                                    Text(
                                        "${chats.size} >",
                                        fontSize = 12.sp,
                                        color = AuroraChatsTokens.TextSecondary,
                                    )
                                }
                            }

                            if (chats.isEmpty()) {
                                item {
                                    AuroraChatsEmptyState(
                                        illustrationRes = R.drawable.ap_chats_hybrid_empty_no_chats,
                                        title = "No chats yet",
                                        subtitle = "Connect with friends or find new companions below to start chatting.",
                                    )
                                }
                            } else {
                                items(chats, key = { it.userId }) { chat ->
                                    val user = store.userById(chat.userId)
                                    val frameRes = AVATAR_FRAMES[Math.abs(chat.userId.hashCode()) % AVATAR_FRAMES.size]
                                    HybridChatRow(
                                        chat = chat,
                                        user = user,
                                        frameRes = frameRes,
                                        onClick = { nav.navigate("chat/${chat.userId}"); bump() },
                                        onMute = {
                                            store.setMuted(chat.userId, !chat.muted)
                                            bump()
                                        },
                                        onDelete = {
                                            store.deleteChat(chat.userId)
                                            bump()
                                        },
                                    )
                                }
                            }

                            // 3. Quick People Discovery Strip
                            val quick = store.allUsers()
                                .filter { !store.isMe(it.id) && !store.isBlocked(it.id) }
                                .take(6)
                            if (quick.isNotEmpty()) {
                                item {
                                    Spacer(Modifier.height(10.dp))
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.fillMaxWidth().padding(bottom = 2.dp),
                                    ) {
                                        Text(
                                            "People",
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = AuroraChatsTokens.TextPrimary,
                                        )
                                        Spacer(Modifier.weight(1f))
                                        Text(
                                            "${quick.size} >",
                                            fontSize = 12.sp,
                                            color = AuroraChatsTokens.TextSecondary,
                                        )
                                    }
                                }
                                items(quick, key = { "q" + it.id }) { u ->
                                    val frameRes = AVATAR_FRAMES[Math.abs(u.id.hashCode()) % AVATAR_FRAMES.size]
                                    HybridUserRow(
                                        u = u,
                                        rel = store.relationship(u.id),
                                        frameRes = frameRes,
                                        onActionClick = {
                                            when (store.relationship(u.id)) {
                                                "friend" -> nav.navigate("chat/${u.id}")
                                                "incoming" -> { store.acceptRequest(u.id); bump() }
                                                "outgoing" -> { store.cancelRequest(u.id); bump() }
                                                "blocked" -> { confirmUnblock = u }
                                                else -> { store.sendRequest(u.id); bump() }
                                            }
                                        },
                                        onClick = { nav.navigate("user/${u.id}") },
                                    )
                                }
                            }
                        }
                    }

                    MsgTab.PEOPLE -> {
                        // People filter chips
                        item {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.padding(bottom = 6.dp),
                            ) {
                                PeopleFilter.entries.forEach { pf ->
                                    val sel = peopleFilter == pf
                                    Box(
                                        modifier = Modifier
                                            .background(
                                                if (sel) AuroraChatsTokens.ActiveTabGradient
                                                else Brush.linearGradient(
                                                    listOf(
                                                        AuroraChatsTokens.SurfaceElevated,
                                                        AuroraChatsTokens.Surface,
                                                    )
                                                ),
                                                RoundedCornerShape(14.dp),
                                            )
                                            .border(
                                                1.dp,
                                                if (sel) AuroraChatsTokens.ActiveCyan else AuroraChatsTokens.CardBorder,
                                                RoundedCornerShape(14.dp),
                                            )
                                            .clickable { peopleFilter = pf }
                                            .padding(horizontal = 12.dp, vertical = 6.dp),
                                    ) {
                                        val label = when (pf) {
                                            PeopleFilter.ALL -> "All"
                                            PeopleFilter.FOLLOWING -> "Following"
                                            PeopleFilter.FOLLOWERS -> "Followers"
                                            PeopleFilter.FRIENDS -> "Friends"
                                        }
                                        Text(
                                            label,
                                            color = if (sel) Color.White else AuroraChatsTokens.TextSecondary,
                                            fontSize = 11.sp,
                                            fontWeight = if (sel) FontWeight.Bold else FontWeight.Normal,
                                        )
                                    }
                                }
                            }
                        }

                        if (query.isNotBlank()) {
                            val results = store.search(query)
                            item { HybridSectionHeader("Search results", "${results.size}") }
                            items(results, key = { "ps" + it.id }) { u ->
                                val frameRes = AVATAR_FRAMES[Math.abs(u.id.hashCode()) % AVATAR_FRAMES.size]
                                HybridUserRow(
                                    u = u,
                                    rel = store.relationship(u.id),
                                    frameRes = frameRes,
                                    onActionClick = {
                                        when (store.relationship(u.id)) {
                                            "friend" -> nav.navigate("chat/${u.id}")
                                            "incoming" -> { store.acceptRequest(u.id); bump() }
                                            "outgoing" -> { store.cancelRequest(u.id); bump() }
                                            "blocked" -> { confirmUnblock = u }
                                            else -> { store.sendRequest(u.id); bump() }
                                        }
                                    },
                                    onClick = { nav.navigate("user/${u.id}") },
                                )
                            }
                            if (results.isEmpty()) {
                                item {
                                    AuroraChatsEmptyState(
                                        illustrationRes = R.drawable.ap_chats_hybrid_empty_no_results,
                                        title = "No one found",
                                        subtitle = "Try searching by exact user ID or display name.",
                                    )
                                }
                            }
                        } else {
                            // "People you may know" header with refresh
                            item {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                ) {
                                    Text(
                                        "People you may know",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = AuroraChatsTokens.TextPrimary,
                                    )
                                    Spacer(Modifier.weight(1f))
                                    Text(
                                        "↻",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = AuroraChatsTokens.CrystalCyan,
                                        modifier = Modifier
                                            .clickable { peopleRefreshNonce++; bump() }
                                            .padding(4.dp),
                                    )
                                }
                            }

                            // 1. Friend requests section (if any)
                            if (incoming.isNotEmpty()) {
                                item {
                                    HybridSectionHeader("Friend requests", "${incoming.size}")
                                }
                                items(incoming, key = { "in" + it.id }) { u ->
                                    val frameRes = AVATAR_FRAMES[Math.abs(u.id.hashCode()) % AVATAR_FRAMES.size]
                                    HybridRequestRow(
                                        u = u,
                                        frameRes = frameRes,
                                        onAccept = { store.acceptRequest(u.id); bump() },
                                        onDecline = { confirmDecline = u },
                                        onClick = { nav.navigate("user/${u.id}") },
                                    )
                                }
                            }

                            // 2. Pending outgoing requests (if any)
                            if (outgoing.isNotEmpty()) {
                                item {
                                    HybridSectionHeader("Pending", "${outgoing.size}")
                                }
                                items(outgoing, key = { "out" + it.id }) { u ->
                                    val frameRes = AVATAR_FRAMES[Math.abs(u.id.hashCode()) % AVATAR_FRAMES.size]
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(AuroraChatsTokens.Surface, RoundedCornerShape(16.dp))
                                            .border(1.dp, AuroraChatsTokens.CardBorder, RoundedCornerShape(16.dp))
                                            .padding(12.dp),
                                    ) {
                                        AuroraFramedAvatar(
                                            name = u.name,
                                            size = 46.dp,
                                            frameRes = frameRes,
                                        )
                                        Spacer(Modifier.width(12.dp))
                                        Column(Modifier.weight(1f)) {
                                            Text(
                                                u.name,
                                                color = AuroraChatsTokens.TextPrimary,
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.SemiBold,
                                            )
                                            Text(
                                                "Request sent",
                                                color = AuroraChatsTokens.TextSecondary,
                                                fontSize = 11.sp,
                                            )
                                        }
                                        TextButton(onClick = { store.cancelRequest(u.id); bump() }) {
                                            Text(
                                                "Cancel",
                                                color = AuroraChatsTokens.RoseAlert,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.SemiBold,
                                            )
                                        }
                                    }
                                }
                            }

                            // 3. Recommended Users list (Discover)
                            val allDiscover = store.allUsers()
                                .filter { !store.isMe(it.id) && !store.isBlocked(it.id) }
                                .let { list ->
                                    when (peopleFilter) {
                                        PeopleFilter.FRIENDS -> list.filter { store.relationship(it.id) == "friend" }
                                        PeopleFilter.FOLLOWING -> list.take(3)
                                        PeopleFilter.FOLLOWERS -> list.drop(2).take(4)
                                        PeopleFilter.ALL -> list
                                    }
                                }

                            if (allDiscover.isEmpty()) {
                                item {
                                    AuroraChatsEmptyState(
                                        illustrationRes = R.drawable.ap_chats_hybrid_empty_no_people,
                                        title = "No people found",
                                        subtitle = "Check back soon or search using the bar above.",
                                    )
                                }
                            } else {
                                items(allDiscover, key = { "disc" + it.id }) { u ->
                                    val frameRes = AVATAR_FRAMES[Math.abs(u.id.hashCode()) % AVATAR_FRAMES.size]
                                    HybridUserRow(
                                        u = u,
                                        rel = store.relationship(u.id),
                                        frameRes = frameRes,
                                        onActionClick = {
                                            when (store.relationship(u.id)) {
                                                "friend" -> nav.navigate("chat/${u.id}")
                                                "incoming" -> { store.acceptRequest(u.id); bump() }
                                                "outgoing" -> { store.cancelRequest(u.id); bump() }
                                                "blocked" -> { confirmUnblock = u }
                                                else -> { store.sendRequest(u.id); bump() }
                                            }
                                        },
                                        onClick = { nav.navigate("user/${u.id}") },
                                    )
                                }
                            }

                            // 4. Blocked section (if any)
                            if (blockedList.isNotEmpty()) {
                                item {
                                    Spacer(Modifier.height(6.dp))
                                    HybridSectionHeader("Blocked", "${blockedList.size}")
                                }
                                items(blockedList, key = { "b" + it.id }) { u ->
                                    val frameRes = AVATAR_FRAMES[Math.abs(u.id.hashCode()) % AVATAR_FRAMES.size]
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(AuroraChatsTokens.Surface, RoundedCornerShape(16.dp))
                                            .border(1.dp, AuroraChatsTokens.CardBorder, RoundedCornerShape(16.dp))
                                            .padding(12.dp),
                                    ) {
                                        AuroraFramedAvatar(
                                            name = u.name,
                                            size = 46.dp,
                                            frameRes = frameRes,
                                        )
                                        Spacer(Modifier.width(12.dp))
                                        Column(Modifier.weight(1f)) {
                                            Text(
                                                u.name,
                                                color = AuroraChatsTokens.TextPrimary,
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.SemiBold,
                                            )
                                            Text(
                                                "Blocked",
                                                color = AuroraChatsTokens.TextSecondary,
                                                fontSize = 11.sp,
                                            )
                                        }
                                        TextButton(onClick = { confirmUnblock = u }) {
                                            Text(
                                                "Unblock",
                                                color = AuroraChatsTokens.CrystalCyan,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.SemiBold,
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    MsgTab.ALERTS -> {
                        // Alerts Filter Chips
                        item {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.padding(bottom = 6.dp),
                            ) {
                                AlertFilter.entries.forEach { af ->
                                    val sel = alertFilter == af
                                    Box(
                                        modifier = Modifier
                                            .background(
                                                if (sel) AuroraChatsTokens.ActiveTabGradient
                                                else Brush.linearGradient(
                                                    listOf(
                                                        AuroraChatsTokens.SurfaceElevated,
                                                        AuroraChatsTokens.Surface,
                                                    )
                                                ),
                                                RoundedCornerShape(14.dp),
                                            )
                                            .border(
                                                1.dp,
                                                if (sel) AuroraChatsTokens.ActiveCyan else AuroraChatsTokens.CardBorder,
                                                RoundedCornerShape(14.dp),
                                            )
                                            .clickable { alertFilter = af }
                                            .padding(horizontal = 14.dp, vertical = 6.dp),
                                    ) {
                                        val label = when (af) {
                                            AlertFilter.ALL -> "All"
                                            AlertFilter.SYSTEM -> "System"
                                            AlertFilter.SOCIAL -> "Social"
                                            AlertFilter.ACTIVITY -> "Activity"
                                        }
                                        Text(
                                            label,
                                            color = if (sel) Color.White else AuroraChatsTokens.TextSecondary,
                                            fontSize = 11.sp,
                                            fontWeight = if (sel) FontWeight.Bold else FontWeight.Normal,
                                        )
                                    }
                                }
                            }
                        }

                        val filteredNotifs = when (alertFilter) {
                            AlertFilter.ALL -> notifs
                            AlertFilter.SYSTEM -> notifs.filter {
                                it.text.contains("VIP", ignoreCase = true) || it.text.contains("system", ignoreCase = true)
                            }
                            AlertFilter.SOCIAL -> notifs.filter {
                                it.kind == NotificationKind.FRIEND_REQUEST || it.kind == NotificationKind.FOLLOW || it.kind == NotificationKind.FRIEND_ACCEPTED
                            }
                            AlertFilter.ACTIVITY -> notifs.filter {
                                it.kind == NotificationKind.ROOM_INVITE || it.kind == NotificationKind.GIFT || it.text.contains("reward", ignoreCase = true)
                            }
                        }

                        if (filteredNotifs.isEmpty()) {
                            item {
                                AuroraChatsEmptyState(
                                    illustrationRes = R.drawable.ap_chats_hybrid_empty_no_alerts,
                                    title = "No alerts yet",
                                    subtitle = "Friend requests, game invites, rewards and system updates appear here.",
                                )
                            }
                        } else {
                            item {
                                Text(
                                    "Today",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = AuroraChatsTokens.TextSecondary,
                                    modifier = Modifier.padding(vertical = 4.dp),
                                )
                            }
                            items(filteredNotifs, key = { it.id }) { n ->
                                val user = n.userId?.let { store.userById(it) }
                                HybridAlertCard(
                                    notification = n,
                                    user = user,
                                    onUserClick = { n.userId?.let { nav.navigate("user/$it") } },
                                    onJoinRoom = { roomId -> nav.navigate(Routes.roomRoute(roomId)) },
                                    onAcceptRequest = { uid -> store.acceptRequest(uid); bump() },
                                    onDeclineRequest = { uid ->
                                        user?.let { confirmDecline = it } ?: run { store.declineRequest(uid); bump() }
                                    },
                                )
                            }

                            // Mark notifications read after viewing
                            item {
                                LaunchedEffect(notifs.size) {
                                    if (unreadNotifs > 0) {
                                        kotlinx.coroutines.delay(800)
                                        store.markNotificationsRead()
                                        bump()
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // ==================== DIALOGS ====================
    confirmDecline?.let { u ->
        AlertDialog(
            onDismissRequest = { confirmDecline = null },
            title = {
                Text(
                    "Decline request?",
                    color = AuroraChatsTokens.TextPrimary,
                    fontWeight = FontWeight.Bold,
                )
            },
            text = {
                Text(
                    "This will decline ${u.name}'s friend request.",
                    color = AuroraChatsTokens.TextSecondary,
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    store.declineRequest(u.id)
                    confirmDecline = null
                    bump()
                }) {
                    Text("Decline", color = AuroraChatsTokens.RoseAlert, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { confirmDecline = null }) {
                    Text("Cancel", color = AuroraChatsTokens.TextSecondary)
                }
            },
            containerColor = AuroraChatsTokens.SurfaceElevated,
        )
    }

    confirmUnblock?.let { u ->
        AlertDialog(
            onDismissRequest = { confirmUnblock = null },
            title = {
                Text(
                    "Unblock ${u.name}?",
                    color = AuroraChatsTokens.TextPrimary,
                    fontWeight = FontWeight.Bold,
                )
            },
            text = {
                Text(
                    "You will be able to receive messages and requests from them again.",
                    color = AuroraChatsTokens.TextSecondary,
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    store.unblock(u.id)
                    confirmUnblock = null
                    bump()
                }) {
                    Text("Unblock", color = AuroraChatsTokens.CrystalCyan, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { confirmUnblock = null }) {
                    Text("Cancel", color = AuroraChatsTokens.TextSecondary)
                }
            },
            containerColor = AuroraChatsTokens.SurfaceElevated,
        )
    }
}

// ==================== HYBRID COMPONENT ROWS ====================

@Composable
private fun HybridSectionHeader(title: String, count: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
    ) {
        Text(
            title,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = AuroraChatsTokens.TextPrimary,
        )
        Spacer(Modifier.width(6.dp))
        Box(
            modifier = Modifier
                .background(AuroraChatsTokens.SurfaceElevated, RoundedCornerShape(10.dp))
                .padding(horizontal = 6.dp, vertical = 2.dp),
        ) {
            Text(
                count,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = AuroraChatsTokens.CrystalCyan,
            )
        }
    }
}

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
private fun HybridChatRow(
    chat: Chat,
    user: DirectoryUser?,
    frameRes: Int,
    onClick: () -> Unit,
    onMute: () -> Unit,
    onDelete: () -> Unit,
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { true },
    )

    SwipeToDismissBox(
        state = dismissState,
        enableDismissFromStartToEnd = true,
        enableDismissFromEndToStart = true,
        backgroundContent = {
            when (dismissState.dismissDirection) {
                SwipeToDismissBoxValue.EndToStart -> {
                    Box(
                        Modifier
                            .fillMaxSize()
                            .background(AuroraChatsTokens.RoseAlert, RoundedCornerShape(16.dp))
                            .padding(horizontal = 18.dp),
                        contentAlignment = Alignment.CenterEnd,
                    ) {
                        Text("Delete", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
                SwipeToDismissBoxValue.StartToEnd -> {
                    Box(
                        Modifier
                            .fillMaxSize()
                            .background(AuroraChatsTokens.ActiveCyan, RoundedCornerShape(16.dp))
                            .padding(horizontal = 18.dp),
                        contentAlignment = Alignment.CenterStart,
                    ) {
                        Text(
                            if (chat.muted) "Unmute" else "Mute",
                            color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold,
                        )
                    }
                }
                else -> {
                    Box(Modifier.fillMaxSize().background(AuroraChatsTokens.Surface, RoundedCornerShape(16.dp)))
                }
            }
        },
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(AuroraChatsTokens.Surface, RoundedCornerShape(16.dp))
                .border(1.dp, AuroraChatsTokens.CardBorder, RoundedCornerShape(16.dp))
                .clickable(onClick = onClick)
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AuroraFramedAvatar(
                name = user?.name ?: chat.userId,
                size = 52.dp,
                frameRes = frameRes,
                online = user?.online == true,
            )

            Spacer(Modifier.width(12.dp))

            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (chat.pinned) {
                        Image(
                            painter = painterResource(R.drawable.ap_chats_hybrid_ic_pin),
                            contentDescription = "Pinned",
                            modifier = Modifier.size(13.dp),
                        )
                        Spacer(Modifier.width(4.dp))
                    }
                    Text(
                        user?.name ?: chat.userId,
                        color = AuroraChatsTokens.TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    if (chat.muted) {
                        Spacer(Modifier.width(6.dp))
                        Image(
                            painter = painterResource(R.drawable.ap_chats_hybrid_ic_mute),
                            contentDescription = "Muted",
                            modifier = Modifier.size(13.dp),
                        )
                    }
                }
                Spacer(Modifier.height(3.dp))
                Text(
                    chat.displayText,
                    color = if (chat.unread > 0) AuroraChatsTokens.TextPrimary else AuroraChatsTokens.TextSecondary,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            Spacer(Modifier.width(8.dp))

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    relativeTime(chat.lastTs),
                    color = AuroraChatsTokens.TextSecondary,
                    fontSize = 11.sp,
                )
                if (chat.unread > 0) {
                    Spacer(Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .background(
                                if (chat.muted) AuroraChatsTokens.TextSecondary else AuroraChatsTokens.RoseAlert,
                                RoundedCornerShape(10.dp),
                            )
                            .padding(horizontal = 7.dp, vertical = 2.dp),
                    ) {
                        Text(
                            "${chat.unread}",
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }
        }

        LaunchedEffect(dismissState.currentValue) {
            when (dismissState.currentValue) {
                SwipeToDismissBoxValue.EndToStart -> {
                    onDelete()
                    dismissState.reset()
                }
                SwipeToDismissBoxValue.StartToEnd -> {
                    onMute()
                    dismissState.reset()
                }
                else -> {}
            }
        }
    }
}

@Composable
private fun HybridUserRow(
    u: DirectoryUser,
    rel: String,
    frameRes: Int,
    onActionClick: () -> Unit,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(AuroraChatsTokens.Surface, RoundedCornerShape(16.dp))
            .border(1.dp, AuroraChatsTokens.CardBorder, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AuroraFramedAvatar(
            name = u.name,
            size = 48.dp,
            frameRes = frameRes,
            online = u.online,
        )

        Spacer(Modifier.width(12.dp))

        Column(Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    u.name,
                    color = AuroraChatsTokens.TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                )
                Spacer(Modifier.width(6.dp))
                // Level Badge
                Box(
                    modifier = Modifier
                        .background(
                            Brush.horizontalGradient(
                                listOf(AuroraChatsTokens.RoyalGold.copy(alpha = 0.25f), AuroraChatsTokens.GlowPurple.copy(alpha = 0.25f))
                            ),
                            RoundedCornerShape(8.dp),
                        )
                        .border(0.5.dp, AuroraChatsTokens.RoyalGold.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 5.dp, vertical = 1.dp),
                ) {
                    Text(
                        "Lv.${u.level}",
                        color = AuroraChatsTokens.RoyalGold,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
            Spacer(Modifier.height(3.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (u.online) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .background(AuroraChatsTokens.OnlineGreen, CircleShape)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        "Online",
                        color = AuroraChatsTokens.OnlineGreen,
                        fontSize = 11.sp,
                    )
                } else {
                    Text(
                        "@${u.id}",
                        color = AuroraChatsTokens.TextSecondary,
                        fontSize = 11.sp,
                        maxLines = 1,
                    )
                }
            }
        }

        Spacer(Modifier.width(8.dp))

        // Relationship Pill / Action
        HybridRelationshipButton(rel = rel, onClick = onActionClick)
    }
}

@Composable
private fun HybridRelationshipButton(rel: String, onClick: () -> Unit) {
    val (label, bgBrush, borderCol, textCol) = when (rel) {
        "friend" -> Quadruple(
            "Friend",
            Brush.linearGradient(listOf(Color(0xFF284B63), Color(0xFF1D3557))),
            AuroraChatsTokens.CrystalCyan.copy(alpha = 0.5f),
            AuroraChatsTokens.CrystalCyan,
        )
        "incoming" -> Quadruple(
            "Respond",
            AuroraChatsTokens.RoseGradient,
            Color.Transparent,
            Color.White,
        )
        "outgoing" -> Quadruple(
            "Sent",
            Brush.linearGradient(listOf(AuroraChatsTokens.SurfaceElevated, AuroraChatsTokens.Surface)),
            AuroraChatsTokens.CardBorder,
            AuroraChatsTokens.TextSecondary,
        )
        "blocked" -> Quadruple(
            "Blocked",
            Brush.linearGradient(listOf(AuroraChatsTokens.SurfaceElevated, AuroraChatsTokens.Surface)),
            AuroraChatsTokens.CardBorder,
            AuroraChatsTokens.RoseAlert,
        )
        else -> Quadruple(
            "+ Add",
            AuroraChatsTokens.ActiveTabGradient,
            Color.Transparent,
            Color.White,
        )
    }

    Box(
        modifier = Modifier
            .background(bgBrush, RoundedCornerShape(14.dp))
            .border(1.dp, borderCol, RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            label,
            color = textCol,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}

private data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)

@Composable
private fun HybridRequestRow(
    u: DirectoryUser,
    frameRes: Int,
    onAccept: () -> Unit,
    onDecline: () -> Unit,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(AuroraChatsTokens.Surface, RoundedCornerShape(16.dp))
            .border(1.dp, AuroraChatsTokens.CardBorder, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AuroraFramedAvatar(
            name = u.name,
            size = 48.dp,
            frameRes = frameRes,
            online = u.online,
        )

        Spacer(Modifier.width(12.dp))

        Column(Modifier.weight(1f)) {
            Text(
                u.name,
                color = AuroraChatsTokens.TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(Modifier.height(2.dp))
            Text(
                "Wants to be your friend",
                color = AuroraChatsTokens.TextSecondary,
                fontSize = 11.sp,
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Box(
                Modifier
                    .background(AuroraChatsTokens.ActiveTabGradient, RoundedCornerShape(12.dp))
                    .clickable(onClick = onAccept)
                    .padding(horizontal = 12.dp, vertical = 6.dp),
            ) {
                Text("Accept", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }

            Box(
                Modifier
                    .background(AuroraChatsTokens.SurfaceElevated, RoundedCornerShape(12.dp))
                    .border(1.dp, AuroraChatsTokens.CardBorder, RoundedCornerShape(12.dp))
                    .clickable(onClick = onDecline)
                    .padding(horizontal = 10.dp, vertical = 6.dp),
            ) {
                Text("Decline", color = AuroraChatsTokens.TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
private fun HybridAlertCard(
    notification: AppNotification,
    user: DirectoryUser?,
    onUserClick: () -> Unit,
    onJoinRoom: (String) -> Unit,
    onAcceptRequest: (String) -> Unit,
    onDeclineRequest: (String) -> Unit,
) {
    val isFriendReq = notification.kind == NotificationKind.FRIEND_REQUEST
    val isRoomInvite = notification.kind == NotificationKind.ROOM_INVITE
    val isVip = notification.text.contains("VIP", ignoreCase = true)
    val isReward = notification.text.contains("reward", ignoreCase = true) || notification.kind == NotificationKind.GIFT

    val iconRes = when {
        isFriendReq -> R.drawable.ap_chats_hybrid_ic_friend_add
        isRoomInvite -> R.drawable.ap_chats_hybrid_ic_room_invite
        isVip -> R.drawable.ap_chats_hybrid_ic_vip
        isReward -> R.drawable.ap_chats_hybrid_ic_gift
        else -> R.drawable.ap_chats_hybrid_ic_alerts
    }

    val iconBgGradient = when {
        isReward -> AuroraChatsTokens.RoyalGoldGradient
        isFriendReq -> AuroraChatsTokens.RoseGradient
        isRoomInvite -> AuroraChatsTokens.ActiveTabGradient
        isVip -> AuroraChatsTokens.RoyalGoldGradient
        else -> Brush.linearGradient(listOf(AuroraChatsTokens.SurfaceElevated, AuroraChatsTokens.GlowPurple))
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(AuroraChatsTokens.Surface, RoundedCornerShape(16.dp))
            .border(
                1.dp,
                if (!notification.read) AuroraChatsTokens.CardBorderGlow else AuroraChatsTokens.CardBorder,
                RoundedCornerShape(16.dp),
            )
            .clickable(enabled = notification.userId != null, onClick = onUserClick)
            .padding(14.dp),
        verticalAlignment = Alignment.Top,
    ) {
        // Icon Badge
        Box(
            modifier = Modifier
                .size(42.dp)
                .background(iconBgGradient, CircleShape)
                .padding(8.dp),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(iconRes),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit,
            )
        }

        Spacer(Modifier.width(12.dp))

        Column(Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = when {
                        isReward -> "Daily Reward"
                        isFriendReq -> "Friend Request"
                        isRoomInvite -> "Room Invite"
                        isVip -> "VIP Update"
                        else -> "System Notice"
                    },
                    color = AuroraChatsTokens.TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(Modifier.weight(1f))
                Text(
                    text = relativeTime(notification.ts),
                    color = AuroraChatsTokens.TextSecondary,
                    fontSize = 11.sp,
                )
                if (!notification.read) {
                    Spacer(Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .background(AuroraChatsTokens.RoseAlert, CircleShape)
                    )
                }
            }

            Spacer(Modifier.height(3.dp))

            Text(
                text = notification.text,
                color = if (notification.read) AuroraChatsTokens.TextSecondary else AuroraChatsTokens.TextPrimary,
                fontSize = 12.sp,
                lineHeight = 16.sp,
            )

            // Dynamic Action CTAs on Alert Card
            if (isRoomInvite && notification.roomId != null) {
                Spacer(Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .background(AuroraChatsTokens.ActiveTabGradient, RoundedCornerShape(12.dp))
                        .clickable { onJoinRoom(notification.roomId) }
                        .padding(horizontal = 14.dp, vertical = 6.dp),
                ) {
                    Text(
                        "Join Room",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
            } else if (isFriendReq && notification.userId != null) {
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(
                        modifier = Modifier
                            .background(AuroraChatsTokens.ActiveTabGradient, RoundedCornerShape(12.dp))
                            .clickable { onAcceptRequest(notification.userId) }
                            .padding(horizontal = 14.dp, vertical = 6.dp),
                    ) {
                        Text("Accept", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    Box(
                        modifier = Modifier
                            .background(AuroraChatsTokens.SurfaceElevated, RoundedCornerShape(12.dp))
                            .border(1.dp, AuroraChatsTokens.CardBorder, RoundedCornerShape(12.dp))
                            .clickable { onDeclineRequest(notification.userId) }
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                    ) {
                        Text("Decline", color = AuroraChatsTokens.TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}
