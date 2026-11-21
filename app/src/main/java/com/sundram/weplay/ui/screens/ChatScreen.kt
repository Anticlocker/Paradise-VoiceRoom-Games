package com.sundram.weplay.ui.screens

import android.content.ClipboardManager
import android.content.Context
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
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
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import com.sundram.weplay.R
import com.sundram.weplay.data.ChatMessage
import com.sundram.weplay.data.Demo
import com.sundram.weplay.data.DemoGifts
import com.sundram.weplay.data.GiftItem
import com.sundram.weplay.data.GroupInfo
import com.sundram.weplay.data.MessagingStore
import com.sundram.weplay.data.MsgStatus
import com.sundram.weplay.data.MsgType
import com.sundram.weplay.data.ProfileStore
import com.sundram.weplay.data.ReplyQuote
import com.sundram.weplay.data.RoomCard
import com.sundram.weplay.data.UserProfile
import com.sundram.weplay.ui.nav.Routes
import com.sundram.weplay.ui.theme.CyanBrand
import com.sundram.weplay.ui.theme.DeepPurple
import com.sundram.weplay.ui.theme.Gold
import com.sundram.weplay.ui.theme.KtvRed
import com.sundram.weplay.ui.theme.LiveRed
import com.sundram.weplay.ui.theme.Midnight
import com.sundram.weplay.ui.theme.MintGreen
import com.sundram.weplay.ui.theme.VyboraBg
import com.sundram.weplay.ui.theme.VyboraBorder
import com.sundram.weplay.ui.theme.VyboraCyan
import com.sundram.weplay.ui.theme.VyboraCyanSoft
import com.sundram.weplay.ui.theme.VyboraGold
import com.sundram.weplay.ui.theme.VyboraLavender
import com.sundram.weplay.ui.theme.VyboraPurple
import com.sundram.weplay.ui.theme.VyboraSurface
import com.sundram.weplay.ui.theme.VyboraTextPrimary
import com.sundram.weplay.ui.theme.VyboraTextSecondary
import com.sundram.weplay.ui.util.formatDayLabel
import com.sundram.weplay.ui.util.formatMessageTime
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

private val REPORT_REASONS = listOf(
    "Harassment",
    "Inappropriate content",
    "Fake profile",
    "Spam",
    "Other",
)

private val QUICK_EMOJIS = listOf("👍", "❤️", "🔥", "😂", "🎉", "👏", "🎁", "✨", "💯", "🙏")

private sealed interface ChatItem {
    data class Msg(val m: ChatMessage) : ChatItem
    data class DateSep(val label: String) : ChatItem
}

private fun buildChatItems(messages: List<ChatMessage>): List<ChatItem> {
    val sorted = messages.sortedBy { it.ts }
    val out = mutableListOf<ChatItem>()
    var lastDay: String? = null
    for (m in sorted) {
        val day = formatDayLabel(m.ts)
        if (day != lastDay) {
            out += ChatItem.DateSep(day)
            lastDay = day
        }
        out += ChatItem.Msg(m)
    }
    return out
}

private enum class RecorderState { IDLE, RECORDING }

private object VoiceRecorder {
    private var recorder: MediaRecorder? = null
    private var startTs: Long = 0L
    private var outputFile: File? = null

    fun start(context: Context) {
        cancel()
        outputFile = File(context.cacheDir, "voice_${System.currentTimeMillis()}.m4a")
            .apply { parentFile?.mkdirs() }
        val r = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(context)
        } else {
            @Suppress("DEPRECATION")
            MediaRecorder()
        }
        try {
            r.setAudioSource(MediaRecorder.AudioSource.MIC)
            r.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            r.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            r.setAudioEncodingBitRate(64_000)
            r.setAudioSamplingRate(44_100)
            r.setOutputFile(outputFile?.absolutePath)
            r.prepare()
            r.start()
        } catch (t: Throwable) {
            runCatching { r.release() }
            outputFile?.delete()
            outputFile = null
            throw t
        }
        recorder = r
        startTs = System.currentTimeMillis()
    }

    fun stop(): Pair<String?, Long> {
        val r = recorder ?: return null to 0L
        val f = outputFile
        val duration = System.currentTimeMillis() - startTs
        runCatching { r.stop() }
        runCatching { r.release() }
        recorder = null
        outputFile = null
        return (if (f?.exists() == true) f.absolutePath else null) to duration
    }

    fun cancel() {
        val r = recorder
        if (r != null) {
            runCatching { r.stop() }
            runCatching { r.release() }
            recorder = null
        }
        outputFile?.delete()
        outputFile = null
        startTs = 0L
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ChatScreen(
    nav: NavController,
    userId: String,
    store: MessagingStore,
    profile: UserProfile? = null,
    onSaveCoins: (Long) -> Unit = {},
) {
    if (userId.isBlank()) {
        LaunchedEffect(Unit) { nav.popBackStack() }
        return
    }

    val context = LocalContext.current
    val isGroup = store.isGroup(userId)
    val groupInfo = if (isGroup) store.groupInfo(userId) else null
    val user = if (!isGroup) store.userById(userId) else null
    val alias = store.getAlias(userId)

    val displayName = alias?.ifBlank { null }
        ?: groupInfo?.name
        ?: user?.name
        ?: userId

    val activeRoom = if (!isGroup) store.userActiveRoom(userId) else null

    var version by remember { mutableStateOf(0) }
    var input by remember(userId) { mutableStateOf("") }
    var replyingTo by remember { mutableStateOf<ReplyQuote?>(null) }

    var menuOpen by remember { mutableStateOf(false) }
    var showChatSettings by remember { mutableStateOf(false) }
    var showGiftDrawer by remember { mutableStateOf(false) }
    var showAttachments by remember { mutableStateOf(false) }
    var showRoomInvitePicker by remember { mutableStateOf(false) }
    var showGameInvitePicker by remember { mutableStateOf(false) }
    var showPhotoPicker by remember { mutableStateOf(false) }
    var showInsufficientBalance by remember { mutableStateOf(false) }
    var previewImageUri by remember { mutableStateOf<String?>(null) }
    var previewRoom by remember { mutableStateOf<RoomCard?>(null) }
    var showCelebration by remember { mutableStateOf(false) }

    var confirmClear by remember { mutableStateOf(false) }
    var confirmBlock by remember { mutableStateOf(false) }
    var confirmReport by remember { mutableStateOf(false) }
    var reportReason by remember { mutableStateOf(REPORT_REASONS.first()) }

    var coinBalance by remember(userId) { mutableStateOf(profile?.coins ?: 500L) }
    LaunchedEffect(profile?.coins) {
        profile?.coins?.let { coinBalance = it }
    }

    var recordingState by remember { mutableStateOf(RecorderState.IDLE) }
    var recordingDurationSec by remember { mutableStateOf(0) }

    fun bump() { version++ }

    // Live auto-replies flush loop
    LaunchedEffect(userId) {
        while (true) {
            delay(1000)
            if (store.flushDueReplies()) {
                bump()
            }
        }
    }

    // Timer while recording
    LaunchedEffect(recordingState) {
        if (recordingState == RecorderState.RECORDING) {
            recordingDurationSec = 0
            while (recordingState == RecorderState.RECORDING) {
                delay(1000)
                recordingDurationSec++
            }
        }
    }

    fun sendInput() {
        val text = input.trim()
        if (text.isEmpty()) return
        val quote = replyingTo
        val sent = if (quote != null) {
            store.sendReplyMessage(userId, text, quote)
        } else {
            store.sendMessage(userId, text)
        }
        if (sent != null) {
            input = ""
            replyingTo = null
            store.saveDraft(userId, "")
            bump()
        }
    }

    fun startRecording() {
        recordingState = RecorderState.RECORDING
        runCatching { VoiceRecorder.start(context) }.onFailure {
            recordingState = RecorderState.IDLE
        }
    }

    fun stopRecording() {
        val (path, durationMs) = VoiceRecorder.stop()
        recordingState = RecorderState.IDLE
        if (path != null && durationMs >= 1000) {
            store.sendMedia(userId, MsgType.VOICE, path, durationMs)
            bump()
        }
    }

    val micPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { granted ->
        if (granted) startRecording()
    }

    fun requestMicAndRecord() {
        val hasMic = androidx.core.content.ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.RECORD_AUDIO,
        ) == android.content.pm.PackageManager.PERMISSION_GRANTED
        if (hasMic) startRecording() else micPermissionLauncher.launch(android.Manifest.permission.RECORD_AUDIO)
    }

    val chat = remember(version, userId) {
        if (userId.isNotBlank()) store.openChat(userId) else null
    }
    val messages = chat?.messages.orEmpty()
    val chatItems = remember(messages) { buildChatItems(messages) }
    val blocked = remember(version, userId) { store.isBlocked(userId) }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    // Restore draft
    LaunchedEffect(userId) {
        val draft = chat?.draft.orEmpty()
        if (draft.isNotBlank() && input.isBlank()) {
            input = draft
        }
    }

    // Auto-scroll to bottom on new message
    LaunchedEffect(chatItems.size) {
        if (chatItems.isNotEmpty()) {
            runCatching {
                listState.scrollToItem(chatItems.lastIndex)
            }
        }
    }

    val wallpaper = chat?.wallpaper ?: 0
    val isMidnight = wallpaper == 3

    // ── Root Container ────────────────────────────────────────────────────────
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(wallBrush(wallpaper)),
    ) {
        Column(Modifier.fillMaxSize()) {

            // ── Premium Header (Section 5.1) ─────────────────────────────────
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(if (isMidnight) DeepPurple.copy(alpha = 0.95f) else VyboraSurface.copy(alpha = 0.96f))
                    .border(1.dp, if (isMidnight) Color.White.copy(alpha = 0.08f) else VyboraBorder)
                    .padding(horizontal = 8.dp, vertical = 10.dp),
            ) {
                // Back Button
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .clickable { nav.popBackStack() },
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        painter = painterResource(R.drawable.vb_chat_ic_back),
                        contentDescription = "Back",
                        tint = if (isMidnight) Color.White else VyboraTextPrimary,
                        modifier = Modifier.size(20.dp),
                    )
                }

                Spacer(Modifier.width(4.dp))

                // Avatar + Presence
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            if (!isGroup) nav.navigate("user/$userId")
                            else showChatSettings = true
                        },
                ) {
                    Box(Modifier.size(40.dp)) {
                        if (isGroup) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(VyboraLavender),
                                contentAlignment = Alignment.Center,
                            ) {
                                Image(
                                    painter = painterResource(groupInfo?.avatarRes?.takeIf { it != 0 } ?: R.drawable.vb_chat_ic_groups),
                                    contentDescription = null,
                                    modifier = Modifier.size(36.dp).clip(CircleShape),
                                )
                            }
                        } else {
                            NovaAvatarImage(id = userId, size = 40.dp)
                            if (user?.online == true) {
                                Box(
                                    Modifier
                                        .align(Alignment.BottomEnd)
                                        .size(11.dp)
                                        .background(MintGreen, CircleShape)
                                        .border(2.dp, VyboraSurface, CircleShape),
                                )
                            }
                        }
                    }

                    Spacer(Modifier.width(10.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                displayName,
                                color = if (isMidnight) Color.White else VyboraTextPrimary,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                            if (user?.isOfficial == true) {
                                Spacer(Modifier.width(4.dp))
                                Image(
                                    painter = painterResource(R.drawable.vb_chat_badge_official),
                                    contentDescription = null,
                                    modifier = Modifier.size(12.dp),
                                )
                            }
                        }

                        // Subtitle status or Voice Room Shortcut
                        Text(
                            when {
                                isGroup -> "${groupInfo?.memberIds?.size ?: 0} members"
                                blocked -> "Blocked"
                                activeRoom != null -> "Live: ${activeRoom.title}"
                                store.isTyping(userId) -> "Typing…"
                                user?.online == true -> "Online now"
                                else -> "Offline"
                            },
                            color = when {
                                activeRoom != null -> LiveRed
                                store.isTyping(userId) -> VyboraCyan
                                else -> if (isMidnight) Color.White.copy(alpha = 0.7f) else VyboraTextSecondary
                            },
                            fontSize = 11.sp,
                            fontWeight = if (activeRoom != null || store.isTyping(userId)) FontWeight.Bold else FontWeight.Normal,
                        )
                    }
                }

                // Header Live Room Shortcut Pill (Section 8.1)
                if (activeRoom != null) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Brush.horizontalGradient(listOf(LiveRed, VyboraPurple)))
                            .clickable { previewRoom = activeRoom }
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                painter = painterResource(R.drawable.vb_chat_ic_voice_room),
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(12.dp),
                            )
                            Spacer(Modifier.width(4.dp))
                            Text("Join Room", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    Spacer(Modifier.width(4.dp))
                }

                // More Menu Button
                Box {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .clickable { menuOpen = true },
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.vb_chat_ic_more),
                            contentDescription = "Menu",
                            tint = if (isMidnight) Color.White else VyboraTextPrimary,
                            modifier = Modifier.size(20.dp),
                        )
                    }

                    DropdownMenu(
                        expanded = menuOpen,
                        onDismissRequest = { menuOpen = false },
                        containerColor = VyboraSurface,
                    ) {
                        DropdownMenuItem(
                            text = { Text("Chat Settings", color = VyboraTextPrimary) },
                            leadingIcon = {
                                Icon(
                                    painter = painterResource(R.drawable.vb_chat_ic_privacy),
                                    contentDescription = null,
                                    tint = VyboraPurple,
                                    modifier = Modifier.size(18.dp),
                                )
                            },
                            onClick = { menuOpen = false; showChatSettings = true },
                        )
                        if (!isGroup) {
                            DropdownMenuItem(
                                text = { Text("View Profile", color = VyboraTextPrimary) },
                                leadingIcon = {
                                    Icon(
                                        painter = painterResource(R.drawable.vb_chat_ic_profile),
                                        contentDescription = null,
                                        tint = VyboraCyan,
                                        modifier = Modifier.size(18.dp),
                                    )
                                },
                                onClick = { menuOpen = false; nav.navigate("user/$userId") },
                            )
                        }
                        DropdownMenuItem(
                            text = {
                                Text(
                                    if (chat?.pinned == true) "Unpin Chat" else "Pin Chat",
                                    color = VyboraTextPrimary,
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    painter = painterResource(R.drawable.vb_chat_ic_pin_chat),
                                    contentDescription = null,
                                    tint = VyboraPurple,
                                    modifier = Modifier.size(18.dp),
                                )
                            },
                            onClick = {
                                store.setPinned(userId, chat?.pinned != true)
                                menuOpen = false; bump()
                            },
                        )
                        DropdownMenuItem(
                            text = {
                                Text(
                                    if (chat?.muted == true) "Unmute" else "Mute Notifications",
                                    color = VyboraTextPrimary,
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    painter = painterResource(R.drawable.vb_chat_ic_mute),
                                    contentDescription = null,
                                    tint = VyboraTextSecondary,
                                    modifier = Modifier.size(18.dp),
                                )
                            },
                            onClick = {
                                store.setMuted(userId, chat?.muted != true)
                                menuOpen = false; bump()
                            },
                        )
                        DropdownMenuItem(
                            text = { Text("Clear Chat History", color = VyboraTextPrimary) },
                            onClick = { menuOpen = false; confirmClear = true },
                        )
                        if (!isGroup) {
                            DropdownMenuItem(
                                text = { Text("Block Contact", color = KtvRed) },
                                leadingIcon = {
                                    Icon(
                                        painter = painterResource(R.drawable.vb_chat_ic_block),
                                        contentDescription = null,
                                        tint = KtvRed,
                                        modifier = Modifier.size(18.dp),
                                    )
                                },
                                onClick = { menuOpen = false; confirmBlock = true },
                            )
                            DropdownMenuItem(
                                text = { Text("Report User", color = KtvRed) },
                                leadingIcon = {
                                    Icon(
                                        painter = painterResource(R.drawable.vb_chat_ic_report),
                                        contentDescription = null,
                                         tint = KtvRed,
                                        modifier = Modifier.size(18.dp),
                                    )
                                },
                                onClick = { menuOpen = false; confirmReport = true },
                            )
                        }
                    }
                }
            }

            // ── Messages Timeline (Section 5.2) ──────────────────────────────
            if (blocked) {
                Box(
                    Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "You blocked $displayName",
                            color = if (isMidnight) Color.White else VyboraTextPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            "Unblock them to resume messaging and room invites.",
                            color = VyboraTextSecondary,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center,
                        )
                        Spacer(Modifier.height(16.dp))
                        Box(
                            Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .background(Brush.horizontalGradient(listOf(VyboraPurple, VyboraCyan)))
                                .clickable { store.unblock(userId); bump() }
                                .padding(horizontal = 24.dp, vertical = 12.dp),
                        ) {
                            Text("Unblock", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else {
                LazyColumn(
                    state = listState,
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f),
                ) {
                    if (messages.isEmpty()) {
                        item {
                            Box(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(top = 40.dp),
                                contentAlignment = Alignment.Center,
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Box(
                                        modifier = Modifier
                                            .size(72.dp)
                                            .clip(CircleShape)
                                            .background(VyboraLavender),
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        Image(
                                            painter = painterResource(R.drawable.vb_chat_fx_sparkle),
                                            contentDescription = null,
                                            modifier = Modifier.size(40.dp),
                                        )
                                    }
                                    Spacer(Modifier.height(12.dp))
                                    Text(
                                        "Say hello to $displayName!",
                                        color = if (isMidnight) Color.White else VyboraTextPrimary,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium,
                                    )
                                    Spacer(Modifier.height(4.dp))
                                    Text(
                                        "Send a wave, text, or a gift to start the conversation",
                                        color = VyboraTextSecondary,
                                        fontSize = 12.sp,
                                    )
                                }
                            }
                        }
                    }

                    itemsIndexed(chatItems, key = { index, item ->
                        when (item) {
                            is ChatItem.Msg     -> "msg_${item.m.id}_$index"
                            is ChatItem.DateSep -> "sep_${item.label}_$index"
                        }
                    }) { _, item ->
                        when (item) {
                            is ChatItem.DateSep -> {
                                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                                    Box(
                                        Modifier
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(
                                                if (isMidnight) Color.White.copy(alpha = 0.12f)
                                                else VyboraLavender
                                            )
                                            .padding(horizontal = 12.dp, vertical = 4.dp),
                                    ) {
                                        Text(
                                            item.label,
                                            color = if (isMidnight) Color.White.copy(alpha = 0.8f) else VyboraTextSecondary,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Medium,
                                        )
                                    }
                                }
                            }

                            is ChatItem.Msg -> {
                                ModernMessageBubble(
                                    m = item.m,
                                    isMidnight = isMidnight,
                                    isGroup = isGroup,
                                    onDelete = {
                                        store.deleteMessageForMe(userId, item.m.id)
                                        bump()
                                    },
                                    onRecall = {
                                        store.recallMessage(userId, item.m.id)
                                        bump()
                                    },
                                    onReply = {
                                        replyingTo = ReplyQuote(
                                            id = item.m.id,
                                            senderName = if (item.m.fromMe) "You" else (item.m.senderName ?: displayName),
                                            text = item.m.text,
                                            type = item.m.type,
                                        )
                                    },
                                    onToggleReaction = { emoji ->
                                        store.toggleReaction(userId, item.m.id, emoji)
                                        bump()
                                    },
                                    onJoinRoom = { rid ->
                                        nav.navigate(Routes.roomRoute(rid))
                                    },
                                    onPlayGame = { gid ->
                                        if (gid == "ludo") nav.navigate(Routes.LUDO)
                                        else nav.navigate(Routes.SPY)
                                    },
                                    onSendGiftBack = {
                                        showGiftDrawer = true
                                    },
                                    onViewPhoto = { path ->
                                        previewImageUri = path
                                    },
                                )
                            }
                        }
                    }

                    // Typing Indicator
                    if (store.isTyping(userId)) {
                        item {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(start = 12.dp, top = 4.dp),
                            ) {
                                Box(
                                    Modifier
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(if (isMidnight) DeepPurple else VyboraSurface)
                                        .border(1.dp, if (isMidnight) Color.White.copy(alpha = 0.1f) else VyboraBorder, RoundedCornerShape(14.dp))
                                        .padding(horizontal = 12.dp, vertical = 6.dp),
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            "$displayName is typing",
                                            color = VyboraCyan,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Medium,
                                        )
                                        TypingDots()
                                    }
                                }
                            }
                        }
                    }
                }

                // ── Reply Quote Preview Bar (if active) ──────────────────────
                replyingTo?.let { quote ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(if (isMidnight) DeepPurple else VyboraLavender)
                            .border(1.dp, VyboraBorder)
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.vb_chat_ic_reply),
                            contentDescription = "Reply",
                            tint = VyboraPurple,
                            modifier = Modifier.size(16.dp),
                        )
                        Spacer(Modifier.width(8.dp))
                        Column(Modifier.weight(1f)) {
                            Text("Replying to ${quote.senderName}", color = VyboraPurple, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text(quote.text, color = VyboraTextSecondary, fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.08f))
                                .clickable { replyingTo = null },
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.vb_chat_ic_close),
                                contentDescription = "Cancel reply",
                                tint = Color.Unspecified,
                                modifier = Modifier.size(10.dp),
                            )
                        }
                    }
                }

                // ── Message Composer (Section 5.3) ───────────────────────────
                Row(
                    verticalAlignment = Alignment.Bottom,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(if (isMidnight) DeepPurple.copy(alpha = 0.96f) else VyboraSurface.copy(alpha = 0.98f))
                        .border(1.dp, if (isMidnight) Color.White.copy(alpha = 0.08f) else VyboraBorder)
                        .imePadding()
                        .padding(horizontal = 10.dp, vertical = 10.dp),
                ) {
                    // Attachment + button
                    ComposerIconButton(
                        icon = R.drawable.vb_chat_ic_attachment,
                        desc = "Attachments",
                        onClick = { showAttachments = true },
                    )

                    Spacer(Modifier.width(6.dp))

                    // Text Input
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(
                                if (isMidnight) Midnight else VyboraBg,
                                RoundedCornerShape(22.dp),
                            )
                            .border(1.dp, if (isMidnight) Color.White.copy(alpha = 0.1f) else VyboraBorder, RoundedCornerShape(22.dp))
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        contentAlignment = Alignment.CenterStart,
                    ) {
                        BasicTextField(
                            value = input,
                            onValueChange = {
                                input = it
                                store.saveDraft(userId, it)
                            },
                            singleLine = false,
                            maxLines = 4,
                            textStyle = TextStyle(
                                color = if (isMidnight) Color.White else VyboraTextPrimary,
                                fontSize = 14.sp,
                            ),
                            cursorBrush = SolidColor(VyboraCyan),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                            keyboardActions = KeyboardActions(onSend = { sendInput() }),
                            modifier = Modifier.fillMaxWidth(),
                            decorationBox = { field ->
                                if (input.isEmpty()) {
                                    Text("Message…", color = VyboraTextSecondary, fontSize = 14.sp)
                                }
                                field()
                            },
                        )
                    }

                    Spacer(Modifier.width(6.dp))

                    // Gift shortcut button (Section 7)
                    ComposerIconButton(
                        icon = R.drawable.vb_chat_ic_gift,
                        desc = "Send gift",
                        onClick = { showGiftDrawer = true },
                    )

                    Spacer(Modifier.width(6.dp))

                    // Voice / Send button
                    val isRecording = recordingState == RecorderState.RECORDING
                    if (input.isBlank() && !isRecording) {
                        ComposerIconButton(
                            icon = R.drawable.vb_chat_ic_voice,
                            desc = "Voice note",
                            onClick = { requestMicAndRecord() },
                        )
                    } else if (isRecording) {
                        // Stop recording button with timer
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(LiveRed)
                                .clickable { stopRecording() }
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                        ) {
                            Box(Modifier.size(8.dp).background(Color.White, CircleShape))
                            Spacer(Modifier.width(6.dp))
                            Text(
                                "0:%02d".format(recordingDurationSec),
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                    } else {
                        // Send Button
                        Box(
                            Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(Brush.horizontalGradient(listOf(VyboraPurple, VyboraCyan)))
                                .clickable { sendInput() },
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.vb_chat_ic_send),
                                contentDescription = "Send",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp),
                            )
                        }
                    }
                }
            }
        }

        // ── Celebration Overlay (confetti effect on gift send) ───────────────
        if (showCelebration) {
            CelebrationOverlay(onComplete = { showCelebration = false })
        }
    }

    // ── Virtual Gift Drawer Bottom Sheet (Section 7) ─────────────────────────
    if (showGiftDrawer) {
        VirtualGiftDrawerSheet(
            balance = coinBalance,
            recipientName = displayName,
            onDismiss = { showGiftDrawer = false },
            onTopUp = {
                showGiftDrawer = false
                nav.navigate(Routes.TOPUP)
            },
            onSend = { gift, qty ->
                val totalCost = gift.cost * qty
                if (coinBalance < totalCost) {
                    showInsufficientBalance = true
                } else {
                    val ok = store.sendRichGift(userId, gift.id, qty) { newBal ->
                        coinBalance = newBal
                        onSaveCoins(newBal)
                    }
                    if (ok) {
                        showCelebration = true
                        bump()
                    }
                    showGiftDrawer = false
                }
            },
        )
    }

    // ── Insufficient Balance Alert ───────────────────────────────────────────
    if (showInsufficientBalance) {
        AlertDialog(
            onDismissRequest = { showInsufficientBalance = false },
            title = { Text("Insufficient Coins", color = VyboraTextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "You need more coins to send this gift. Would you like to top up your balance now?",
                    color = VyboraTextSecondary,
                    fontSize = 13.sp,
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    showInsufficientBalance = false
                    nav.navigate(Routes.TOPUP)
                }) {
                    Text("Top Up Now", color = VyboraCyan, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showInsufficientBalance = false }) {
                    Text("Cancel", color = VyboraTextSecondary)
                }
            },
            containerColor = VyboraSurface,
        )
    }

    // ── Attachment Menu Bottom Sheet ─────────────────────────────────────────
    if (showAttachments) {
        AttachmentPickerSheet(
            onDismiss = { showAttachments = false },
            onPickPhoto = {
                showAttachments = false
                showPhotoPicker = true
            },
            onInviteRoom = {
                showAttachments = false
                showRoomInvitePicker = true
            },
            onInviteGame = {
                showAttachments = false
                showGameInvitePicker = true
            },
        )
    }

    // ── Room Invite Picker Sheet ─────────────────────────────────────────────
    if (showRoomInvitePicker) {
        RoomInvitePickerSheet(
            rooms = Demo.rooms,
            onDismiss = { showRoomInvitePicker = false },
            onSelect = { room ->
                store.sendRoomInviteMessage(userId, room.id)
                showRoomInvitePicker = false
                bump()
            },
        )
    }

    // ── Game Invite Picker Sheet ─────────────────────────────────────────────
    if (showGameInvitePicker) {
        GameInvitePickerSheet(
            onDismiss = { showGameInvitePicker = false },
            onSelect = { gid ->
                store.sendGameInviteMessage(userId, gid)
                showGameInvitePicker = false
                bump()
            },
        )
    }

    // ── Photo Picker Launcher ────────────────────────────────────────────────
    if (showPhotoPicker) {
        val pStore = remember { ProfileStore(context) }
        val launcher = rememberLauncherForActivityResult(
            ActivityResultContracts.GetContent(),
        ) { uri: Uri? ->
            if (uri != null) {
                val path = pStore.saveChatPhoto(uri)
                if (path != null) {
                    store.sendMedia(userId, MsgType.IMAGE, path)
                    bump()
                }
            }
            showPhotoPicker = false
        }
        LaunchedEffect(Unit) {
            launcher.launch("image/*")
        }
    }

    // ── Chat Settings Sheet (Section 10) ─────────────────────────────────────
    if (showChatSettings) {
        ChatSettingsSheet(
            userId = userId,
            displayName = displayName,
            isGroup = isGroup,
            store = store,
            currentWallpaper = wallpaper,
            onDismiss = { showChatSettings = false },
            onWallpaperChange = { idx ->
                store.setWallpaper(userId, idx)
                bump()
            },
            onClearHistory = {
                showChatSettings = false
                confirmClear = true
            },
            onBlock = {
                showChatSettings = false
                confirmBlock = true
            },
            onReport = {
                showChatSettings = false
                confirmReport = true
            },
            onViewProfile = {
                showChatSettings = false
                nav.navigate("user/$userId")
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
                nav.navigate(Routes.roomRoute(rid))
            },
        )
    }

    // ── Fullscreen Photo Viewer Dialog ───────────────────────────────────────
    previewImageUri?.let { path ->
        FullscreenPhotoDialog(
            path = path,
            onDismiss = { previewImageUri = null },
        )
    }

    // ── Clear Chat Confirmation Dialog ───────────────────────────────────────
    if (confirmClear) {
        AlertDialog(
            onDismissRequest = { confirmClear = false },
            title = { Text("Clear chat history?", color = VyboraTextPrimary, fontWeight = FontWeight.Bold) },
            text = { Text("All messages in this conversation will be permanently removed.", color = VyboraTextSecondary) },
            confirmButton = {
                TextButton(onClick = {
                    store.clearChat(userId)
                    confirmClear = false
                    bump()
                }) {
                    Text("Clear", color = KtvRed, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { confirmClear = false }) { Text("Cancel", color = VyboraTextSecondary) }
            },
            containerColor = VyboraSurface,
        )
    }

    // ── Block User Confirmation Dialog ───────────────────────────────────────
    if (confirmBlock) {
        AlertDialog(
            onDismissRequest = { confirmBlock = false },
            title = { Text("Block $displayName?", color = VyboraTextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "They will not be able to message you or see your profile. You will be removed from each other's friends.",
                    color = VyboraTextSecondary,
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    store.block(userId)
                    confirmBlock = false
                    nav.popBackStack()
                }) {
                    Text("Block", color = KtvRed, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { confirmBlock = false }) { Text("Cancel", color = VyboraTextSecondary) }
            },
            containerColor = VyboraSurface,
        )
    }

    // ── Report User Confirmation Dialog ──────────────────────────────────────
    if (confirmReport) {
        AlertDialog(
            onDismissRequest = { confirmReport = false },
            title = { Text("Report $displayName", color = VyboraTextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Select a reason. The user will also be blocked for your safety.", color = VyboraTextSecondary, fontSize = 12.sp)
                    Spacer(Modifier.height(12.dp))
                    REPORT_REASONS.forEach { r ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { reportReason = r }
                                .padding(vertical = 6.dp),
                        ) {
                            Box(
                                Modifier
                                    .size(18.dp)
                                    .border(
                                        1.dp,
                                        if (reportReason == r) VyboraCyan else VyboraBorder,
                                        CircleShape,
                                    ),
                                contentAlignment = Alignment.Center,
                            ) {
                                if (reportReason == r) {
                                    Box(Modifier.size(10.dp).background(VyboraCyan, CircleShape))
                                }
                            }
                            Spacer(Modifier.width(10.dp))
                            Text(r, color = VyboraTextPrimary, fontSize = 13.sp)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    store.report(userId, reportReason)
                    confirmReport = false
                    nav.popBackStack()
                }) {
                    Text("Report & Block", color = KtvRed, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { confirmReport = false }) { Text("Cancel", color = VyboraTextSecondary) }
            },
            containerColor = VyboraSurface,
        )
    }
}

// ─── Modern Message Bubble (Section 5.2) ─────────────────────────────────────

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ModernMessageBubble(
    m: ChatMessage,
    isMidnight: Boolean,
    isGroup: Boolean,
    onDelete: () -> Unit,
    onRecall: () -> Unit,
    onReply: () -> Unit,
    onToggleReaction: (String) -> Unit,
    onJoinRoom: (String) -> Unit,
    onPlayGame: (String) -> Unit,
    onSendGiftBack: () -> Unit,
    onViewPhoto: (String) -> Unit,
) {
    var showActionSheet by remember { mutableStateOf(false) }
    val fromMe = m.fromMe
    val withinRecallWindow = System.currentTimeMillis() - m.ts <= 120_000L
    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (fromMe) Alignment.End else Alignment.Start,
    ) {
        // Group author header
        if (isGroup && !fromMe && m.senderName != null) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(start = 4.dp, bottom = 3.dp),
            ) {
                NovaAvatarImage(id = m.senderId ?: "u", size = 18.dp)
                Spacer(Modifier.width(6.dp))
                Text(m.senderName, color = VyboraPurple, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }

        Box(
            modifier = Modifier
                .clip(
                    RoundedCornerShape(
                        topStart = 18.dp,
                        topEnd = 18.dp,
                        bottomStart = if (fromMe) 18.dp else 4.dp,
                        bottomEnd = if (fromMe) 4.dp else 18.dp,
                    )
                )
                .background(
                    if (fromMe)
                        Brush.horizontalGradient(listOf(VyboraPurple, VyboraCyan))
                    else if (isMidnight)
                        SolidColor(DeepPurple)
                    else
                        SolidColor(VyboraSurface)
                )
                .border(
                    1.dp,
                    if (fromMe) Color.Transparent
                    else if (isMidnight) Color.White.copy(alpha = 0.08f)
                    else VyboraBorder,
                    RoundedCornerShape(
                        topStart = 18.dp,
                        topEnd = 18.dp,
                        bottomStart = if (fromMe) 18.dp else 4.dp,
                        bottomEnd = if (fromMe) 4.dp else 18.dp,
                    ),
                )
                .combinedClickable(
                    onClick = {
                        if (m.type == MsgType.IMAGE && m.mediaPath != null) {
                            onViewPhoto(m.mediaPath)
                        }
                    },
                    onLongClick = { showActionSheet = true },
                )
                .padding(horizontal = 14.dp, vertical = 10.dp),
        ) {
            Column {
                // Reply quote inside bubble
                m.replyTo?.let { quote ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                if (fromMe) Color.Black.copy(alpha = 0.15f)
                                else VyboraLavender.copy(alpha = 0.6f)
                            )
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                    ) {
                        Column {
                            Text(
                                quote.senderName,
                                color = if (fromMe) Color.White else VyboraPurple,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                            )
                            Text(
                                quote.text,
                                color = if (fromMe) Color.White.copy(alpha = 0.85f) else VyboraTextSecondary,
                                fontSize = 11.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                    }
                    Spacer(Modifier.height(6.dp))
                }

                // Message Type Rendering
                when {
                    m.recalled -> {
                        Text(
                            "Message recalled",
                            color = if (fromMe) Color.White.copy(alpha = 0.7f) else VyboraTextSecondary,
                            fontSize = 13.sp,
                        )
                    }

                    // Virtual Gift Card
                    m.type == MsgType.GIFT -> {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                painter = painterResource(
                                    when (m.giftId?.lowercase()) {
                                        "rose", "g1" -> R.drawable.vb_gift_rose
                                        "heart", "g4" -> R.drawable.vb_gift_heart
                                        "star", "g3" -> R.drawable.vb_gift_star
                                        "crown", "g6" -> R.drawable.vb_gift_crown
                                        "diamond", "g5" -> R.drawable.vb_gift_diamond
                                        else -> R.drawable.vb_chat_ic_gift_box
                                    }
                                ),
                                contentDescription = null,
                                modifier = Modifier.size(42.dp),
                            )
                            Spacer(Modifier.width(10.dp))
                            Column {
                                Text(
                                    m.text,
                                    color = if (fromMe) Color.White else VyboraTextPrimary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                )
                                Text(
                                    "${m.giftCost * m.giftQuantity} Coins Spent",
                                    color = if (fromMe) VyboraGold else Gold,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                )
                            }
                        }
                        if (!fromMe) {
                            Spacer(Modifier.height(8.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(VyboraCyanSoft)
                                    .clickable(onClick = onSendGiftBack)
                                    .padding(horizontal = 10.dp, vertical = 5.dp),
                            ) {
                                Text("Send Gift Back 🎁", color = VyboraCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    // Voice Room Invitation Card (Section 8.2)
                    m.type == MsgType.ROOM_INVITE -> {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    painter = painterResource(R.drawable.vb_chat_ic_voice_room),
                                    contentDescription = null,
                                    tint = if (fromMe) Color.White else LiveRed,
                                    modifier = Modifier.size(24.dp),
                                )
                                Spacer(Modifier.width(8.dp))
                                Column {
                                    Text(
                                        m.roomTitle ?: "Voice Lounge",
                                        color = if (fromMe) Color.White else VyboraTextPrimary,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                    )
                                    Text(
                                        "Live Voice Room Invitation",
                                        color = if (fromMe) Color.White.copy(alpha = 0.8f) else VyboraTextSecondary,
                                        fontSize = 11.sp,
                                    )
                                }
                            }
                            Spacer(Modifier.height(8.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(
                                        if (fromMe) SolidColor(Color.White)
                                        else Brush.horizontalGradient(listOf(LiveRed, VyboraPurple))
                                    )
                                    .clickable { m.roomId?.let { onJoinRoom(it) } }
                                    .padding(horizontal = 14.dp, vertical = 7.dp),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    "Join Room 🎙️",
                                    color = if (fromMe) VyboraPurple else Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                )
                            }
                        }
                    }

                    // Game Invitation Card
                    m.type == MsgType.GAME_INVITE -> {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    painter = painterResource(R.drawable.vb_chat_ic_games),
                                    contentDescription = null,
                                    tint = if (fromMe) Color.White else VyboraPurple,
                                    modifier = Modifier.size(24.dp),
                                )
                                Spacer(Modifier.width(8.dp))
                                Column {
                                    Text(
                                        m.gameTitle ?: "Game Match",
                                        color = if (fromMe) Color.White else VyboraTextPrimary,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                    )
                                    Text(
                                        "Social Game Match Invitation",
                                        color = if (fromMe) Color.White.copy(alpha = 0.8f) else VyboraTextSecondary,
                                        fontSize = 11.sp,
                                    )
                                }
                            }
                            Spacer(Modifier.height(8.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(
                                        if (fromMe) SolidColor(Color.White)
                                        else Brush.horizontalGradient(listOf(VyboraPurple, VyboraCyan))
                                    )
                                    .clickable { m.gameId?.let { onPlayGame(it) } }
                                    .padding(horizontal = 14.dp, vertical = 7.dp),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    "Play Now 🎮",
                                    color = if (fromMe) VyboraPurple else Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                )
                            }
                        }
                    }

                    // Photo Message Card
                    m.type == MsgType.IMAGE && m.mediaPath != null -> {
                        val path = m.mediaPath
                        val bitmap = remember(path) {
                            runCatching { BitmapFactory.decodeFile(path)?.asImageBitmap() }.getOrNull()
                        }
                        if (bitmap != null) {
                            Image(
                                bitmap = bitmap,
                                contentDescription = "Photo",
                                modifier = Modifier
                                    .size(width = 200.dp, height = 140.dp)
                                    .clip(RoundedCornerShape(12.dp)),
                                contentScale = ContentScale.Crop,
                            )
                        } else {
                            Text("Photo", color = if (fromMe) Color.White else VyboraTextPrimary, fontSize = 13.sp)
                        }
                    }

                    // Voice Note Player Card
                    m.type == MsgType.VOICE && m.mediaPath != null -> {
                        VoicePlayerCard(m = m, fromMe = fromMe)
                    }

                    // Text / Emoji Message
                    else -> {
                        Text(
                            m.text,
                            color = if (fromMe) Color.White else if (isMidnight) Color.White else VyboraTextPrimary,
                            fontSize = 14.sp,
                            lineHeight = 20.sp,
                        )
                    }
                }

                Spacer(Modifier.height(4.dp))

                // Timestamp + Delivery Status
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.align(Alignment.End),
                ) {
                    Text(
                        formatMessageTime(m.ts),
                        color = if (fromMe) Color.White.copy(alpha = 0.7f) else VyboraTextSecondary,
                        fontSize = 10.sp,
                    )
                    if (fromMe) {
                        Spacer(Modifier.width(4.dp))
                        when (m.status) {
                            MsgStatus.PENDING -> {
                                Icon(
                                    painter = painterResource(R.drawable.vb_chat_ic_clock),
                                    contentDescription = "Pending",
                                    tint = Color.White.copy(alpha = 0.8f),
                                    modifier = Modifier.size(11.dp),
                                )
                            }
                            MsgStatus.SENT -> {
                                Icon(
                                    painter = painterResource(R.drawable.vb_chat_ic_check),
                                    contentDescription = "Sent",
                                    tint = Color.White.copy(alpha = 0.8f),
                                    modifier = Modifier.size(11.dp),
                                )
                            }
                            MsgStatus.DELIVERED -> {
                                Row {
                                    Icon(
                                        painter = painterResource(R.drawable.vb_chat_ic_check),
                                        contentDescription = "Delivered",
                                        tint = Color.White.copy(alpha = 0.8f),
                                        modifier = Modifier.size(11.dp),
                                    )
                                    Icon(
                                        painter = painterResource(R.drawable.vb_chat_ic_check),
                                        contentDescription = null,
                                        tint = Color.White.copy(alpha = 0.8f),
                                        modifier = Modifier.size(11.dp),
                                    )
                                }
                            }
                            MsgStatus.READ -> {
                                Row {
                                    Icon(
                                        painter = painterResource(R.drawable.vb_chat_ic_check),
                                        contentDescription = "Read",
                                        tint = VyboraCyan,
                                        modifier = Modifier.size(11.dp),
                                    )
                                    Icon(
                                        painter = painterResource(R.drawable.vb_chat_ic_check),
                                        contentDescription = null,
                                        tint = VyboraCyan,
                                        modifier = Modifier.size(11.dp),
                                    )
                                }
                            }
                            MsgStatus.FAILED -> {
                                Text("⚠️ Failed", color = KtvRed, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // Emoji Reaction Badges Under Bubble
        if (m.reactions.isNotEmpty()) {
            Spacer(Modifier.height(3.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.padding(horizontal = 4.dp),
            ) {
                m.reactions.forEach { (emoji, userList) ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                if (isMidnight) DeepPurple else VyboraSurface
                            )
                            .border(1.dp, VyboraBorder, RoundedCornerShape(10.dp))
                            .clickable { onToggleReaction(emoji) }
                            .padding(horizontal = 6.dp, vertical = 2.dp),
                    ) {
                        Text(
                            "$emoji ${userList.size}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isMidnight) Color.White else VyboraTextPrimary,
                        )
                    }
                }
            }
        }

        // Long Press Actions Dropdown / Sheet
        if (showActionSheet) {
            DropdownMenu(
                expanded = true,
                onDismissRequest = { showActionSheet = false },
                containerColor = VyboraSurface,
            ) {
                // Quick emoji reaction bar
                DropdownMenuItem(
                    text = {
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            QUICK_EMOJIS.take(6).forEach { emo ->
                                Text(
                                    emo,
                                    fontSize = 18.sp,
                                    modifier = Modifier.clickable {
                                        onToggleReaction(emo)
                                        showActionSheet = false
                                    },
                                )
                            }
                        }
                    },
                    onClick = {},
                )
                DropdownMenuItem(
                    text = { Text("Reply", color = VyboraTextPrimary) },
                    leadingIcon = {
                        Icon(painterResource(R.drawable.vb_chat_ic_reply), contentDescription = null, modifier = Modifier.size(16.dp), tint = VyboraPurple)
                    },
                    onClick = {
                        showActionSheet = false
                        onReply()
                    },
                )
                DropdownMenuItem(
                    text = { Text("Copy Text", color = VyboraTextPrimary) },
                    leadingIcon = {
                        Icon(painterResource(R.drawable.vb_chat_ic_copy), contentDescription = null, modifier = Modifier.size(16.dp), tint = VyboraCyan)
                    },
                    onClick = {
                        showActionSheet = false
                        val clip = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        clip.setPrimaryClip(android.content.ClipData.newPlainText("chat_msg", m.text))
                    },
                )
                if (fromMe && withinRecallWindow && !m.recalled) {
                    DropdownMenuItem(
                        text = { Text("Recall message", color = VyboraTextPrimary) },
                        onClick = {
                            showActionSheet = false
                            onRecall()
                        },
                    )
                }
                if (!m.recalled) {
                    DropdownMenuItem(
                        text = { Text("Delete for me", color = KtvRed) },
                        onClick = {
                            showActionSheet = false
                            onDelete()
                        },
                    )
                }
            }
        }
    }
}

// ─── Voice Player Card ───────────────────────────────────────────────────────

@Composable
private fun VoicePlayerCard(m: ChatMessage, fromMe: Boolean) {
    var playing by remember { mutableStateOf(false) }
    var player by remember { mutableStateOf<MediaPlayer?>(null) }

    DisposableEffect(m.id) {
        onDispose {
            player?.runCatching { stop() }
            player?.release()
            player = null
            playing = false
        }
    }

    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(if (fromMe) Color.White.copy(alpha = 0.2f) else VyboraCyanSoft)
                .clickable {
                    if (playing) {
                        player?.runCatching { stop() }
                        player?.release()
                        player = null
                        playing = false
                    } else {
                        val path = m.mediaPath ?: return@clickable
                        val next = MediaPlayer()
                        val started = runCatching {
                            next.setDataSource(path)
                            next.prepare()
                            next.setOnCompletionListener {
                                playing = false
                                next.release()
                                player = null
                            }
                            next.start()
                        }.isSuccess
                        if (started) {
                            player = next
                            playing = true
                        } else {
                            next.release()
                            playing = false
                        }
                    }
                },
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(if (playing) R.drawable.ic_pause else R.drawable.ic_play),
                contentDescription = if (playing) "Pause" else "Play",
                tint = if (fromMe) Color.White else VyboraCyan,
                modifier = Modifier.size(16.dp),
            )
        }
        Spacer(Modifier.width(10.dp))
        Icon(
            painter = painterResource(R.drawable.vb_chat_ic_voice_wave),
            contentDescription = null,
            tint = if (fromMe) Color.White.copy(alpha = 0.85f) else VyboraCyan,
            modifier = Modifier.size(width = 80.dp, height = 18.dp),
        )
        Spacer(Modifier.width(8.dp))
        Text(
            "0:%02d".format((m.durationMs / 1000).coerceAtLeast(1)),
            color = if (fromMe) Color.White else VyboraTextPrimary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}

// ─── Virtual Gift Drawer Bottom Sheet (Section 7) ────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun VirtualGiftDrawerSheet(
    balance: Long,
    recipientName: String,
    onDismiss: () -> Unit,
    onTopUp: () -> Unit,
    onSend: (GiftItem, Int) -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var selectedCategoryIndex by remember { mutableStateOf(0) }
    var selectedGift by remember { mutableStateOf(DemoGifts.first()) }
    var selectedQty by remember { mutableStateOf(1) }

    val categories = listOf("Popular", "Romantic", "Luxury", "Cute", "Special", "VIP")

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
            // Header with Coin Balance and Top Up action
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    "Send Gift to $recipientName",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = VyboraTextPrimary,
                    modifier = Modifier.weight(1f),
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(VyboraBg)
                        .clickable(onClick = onTopUp)
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_coin),
                        contentDescription = "Coins",
                        tint = Gold,
                        modifier = Modifier.size(16.dp),
                    )
                    Spacer(Modifier.width(4.dp))
                    Text("$balance", color = VyboraTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.width(6.dp))
                    Text("+ Top Up", color = VyboraCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(Modifier.height(14.dp))

            // Category Bar
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(categories.indices.toList()) { i ->
                    val isSel = selectedCategoryIndex == i
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSel) VyboraPurple else VyboraBg)
                            .clickable { selectedCategoryIndex = i }
                            .padding(horizontal = 14.dp, vertical = 6.dp),
                    ) {
                        Text(
                            categories[i],
                            color = if (isSel) Color.White else VyboraTextSecondary,
                            fontSize = 12.sp,
                            fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                        )
                    }
                }
            }

            Spacer(Modifier.height(14.dp))

            // Gifts Grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.height(200.dp),
            ) {
                items(DemoGifts.size) { idx ->
                    val g = DemoGifts[idx]
                    val isSel = selectedGift.id == g.id
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (isSel) VyboraLavender else VyboraBg)
                            .border(
                                1.5.dp,
                                if (isSel) VyboraCyan else Color.Transparent,
                                RoundedCornerShape(14.dp),
                            )
                            .clickable { selectedGift = g }
                            .padding(8.dp),
                    ) {
                        Image(
                            painter = painterResource(
                                when (g.id) {
                                    "rose" -> R.drawable.vb_gift_rose
                                    "star" -> R.drawable.vb_gift_star
                                    "box"  -> R.drawable.vb_chat_ic_gift_box
                                    "crown" -> R.drawable.vb_gift_crown
                                    else   -> R.drawable.vb_gift_heart
                                }
                            ),
                            contentDescription = g.name,
                            modifier = Modifier.size(36.dp),
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            g.name,
                            color = VyboraTextPrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                        )
                        Text(
                            "${g.cost}c",
                            color = Gold,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }

            Spacer(Modifier.height(14.dp))

            // Quantity selector and Send button
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
            ) {
                // Quantity chips
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf(1, 5, 10, 99).forEach { q ->
                        val isSel = selectedQty == q
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSel) VyboraCyan else VyboraBg)
                                .clickable { selectedQty = q }
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                        ) {
                            Text(
                                "x$q",
                                color = if (isSel) Color.White else VyboraTextPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                    }
                }

                Spacer(Modifier.weight(1f))

                // Send Button
                val totalCost = selectedGift.cost * selectedQty
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(Brush.horizontalGradient(listOf(VyboraPurple, VyboraCyan)))
                        .clickable { onSend(selectedGift, selectedQty) }
                        .padding(horizontal = 22.dp, vertical = 10.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        "Send ($totalCost c)",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

// ─── Chat Settings Bottom Sheet (Section 10) ─────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChatSettingsSheet(
    userId: String,
    displayName: String,
    isGroup: Boolean,
    store: MessagingStore,
    currentWallpaper: Int,
    onDismiss: () -> Unit,
    onWallpaperChange: (Int) -> Unit,
    onClearHistory: () -> Unit,
    onBlock: () -> Unit,
    onReport: () -> Unit,
    onViewProfile: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var localAlias by remember { mutableStateOf(store.getAlias(userId) ?: "") }
    var muteNotifs by remember { mutableStateOf(store.chat(userId)?.muted == true) }
    var pinChat by remember { mutableStateOf(store.chat(userId)?.pinned == true) }
    var blockGames by remember { mutableStateOf(store.isGameInvitesBlocked(userId)) }
    var blockRooms by remember { mutableStateOf(store.isRoomInvitesBlocked(userId)) }

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
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Chat Settings", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = VyboraTextPrimary, modifier = Modifier.weight(1f))
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

            // Profile Preview Card
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(VyboraBg, RoundedCornerShape(16.dp))
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                NovaAvatarImage(id = userId, size = 48.dp)
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(displayName, color = VyboraTextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    Text("@$userId", color = VyboraTextSecondary, fontSize = 12.sp)
                }
                if (!isGroup) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(VyboraLavender)
                            .clickable(onClick = onViewProfile)
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                    ) {
                        Text("Profile", color = VyboraPurple, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Alias Input (Section 10)
            if (!isGroup) {
                Text("Custom Nickname / Alias", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = VyboraTextPrimary)
                Spacer(Modifier.height(6.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(VyboraBg, RoundedCornerShape(14.dp))
                        .border(1.dp, VyboraBorder, RoundedCornerShape(14.dp))
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                ) {
                    BasicTextField(
                        value = localAlias,
                        onValueChange = {
                            localAlias = it
                            store.setAlias(userId, it)
                        },
                        singleLine = true,
                        textStyle = TextStyle(color = VyboraTextPrimary, fontSize = 14.sp),
                        cursorBrush = SolidColor(VyboraCyan),
                        modifier = Modifier.fillMaxWidth(),
                        decorationBox = { field ->
                            if (localAlias.isEmpty()) {
                                Text("Set local alias (only visible to you)", color = VyboraTextSecondary, fontSize = 13.sp)
                            }
                            field()
                        },
                    )
                }
                Spacer(Modifier.height(16.dp))
            }

            // Wallpaper Themes Selector (Section 10)
            Text("Conversation Background", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = VyboraTextPrimary)
            Spacer(Modifier.height(8.dp))
            val themes = listOf("Pearl White", "Lavender", "Mint Lounge", "Midnight")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                themes.forEachIndexed { idx, name ->
                    val isSel = currentWallpaper == idx
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                when (idx) {
                                    1 -> VyboraLavender
                                    2 -> VyboraCyanSoft
                                    3 -> DeepPurple
                                    else -> VyboraBg
                                }
                            )
                            .border(
                                2.dp,
                                if (isSel) VyboraCyan else Color.Transparent,
                                RoundedCornerShape(12.dp),
                            )
                            .clickable { onWallpaperChange(idx) }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            name,
                            color = if (idx == 3) Color.White else VyboraTextPrimary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Toggles
            SettingToggleRow("Mute Notifications", muteNotifs) {
                muteNotifs = it
                store.setMuted(userId, it)
            }
            SettingToggleRow("Pin Conversation", pinChat) {
                pinChat = it
                store.setPinned(userId, it)
            }
            if (!isGroup) {
                SettingToggleRow("Block Game Invitations", blockGames) {
                    blockGames = it
                    store.setGameInvitesBlocked(userId, it)
                }
                SettingToggleRow("Block Room Invitations", blockRooms) {
                    blockRooms = it
                    store.setRoomInvitesBlocked(userId, it)
                }
            }

            Spacer(Modifier.height(16.dp))

            // Actions
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(VyboraBg)
                        .clickable(onClick = onClearHistory)
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text("Clear History", color = VyboraTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }
                if (!isGroup) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(KtvRed.copy(alpha = 0.12f))
                            .clickable(onClick = onBlock)
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text("Block User", color = KtvRed, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(KtvRed.copy(alpha = 0.12f))
                            .clickable(onClick = onReport)
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text("Report", color = KtvRed, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun SettingToggleRow(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
    ) {
        Text(label, color = VyboraTextPrimary, fontSize = 13.sp, modifier = Modifier.weight(1f))
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = VyboraCyan,
            ),
        )
    }
}

// ─── Attachment Picker Sheet ─────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AttachmentPickerSheet(
    onDismiss: () -> Unit,
    onPickPhoto: () -> Unit,
    onInviteRoom: () -> Unit,
    onInviteGame: () -> Unit,
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
                .padding(horizontal = 24.dp, vertical = 16.dp),
        ) {
            Text("Send Attachment", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = VyboraTextPrimary)
            Spacer(Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
            ) {
                AttachmentOptionItem(
                    icon = R.drawable.vb_chat_ic_gallery,
                    label = "Photo",
                    color = VyboraPurple,
                    onClick = onPickPhoto,
                )
                AttachmentOptionItem(
                    icon = R.drawable.vb_chat_ic_voice_room,
                    label = "Room Invite",
                    color = LiveRed,
                    onClick = onInviteRoom,
                )
                AttachmentOptionItem(
                    icon = R.drawable.vb_chat_ic_games,
                    label = "Game Match",
                    color = VyboraCyan,
                    onClick = onInviteGame,
                )
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun AttachmentOptionItem(icon: Int, label: String, color: Color, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable(onClick = onClick)) {
        Box(
            modifier = Modifier
                .size(54.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(painterResource(icon), contentDescription = label, tint = color, modifier = Modifier.size(24.dp))
        }
        Spacer(Modifier.height(8.dp))
        Text(label, color = VyboraTextPrimary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
    }
}

// ─── Room Invite Picker Sheet ─────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RoomInvitePickerSheet(
    rooms: List<RoomCard>,
    onDismiss: () -> Unit,
    onSelect: (RoomCard) -> Unit,
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
            Text("Select Voice Room to Invite", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = VyboraTextPrimary)
            Spacer(Modifier.height(12.dp))
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(rooms, key = { it.id }) { r ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(VyboraBg, RoundedCornerShape(14.dp))
                            .clickable { onSelect(r) }
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(VyboraLavender),
                        ) {
                            if (r.coverRes != null && r.coverRes != 0) {
                                Image(
                                    painter = painterResource(r.coverRes),
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop,
                                )
                            }
                        }
                        Spacer(Modifier.width(10.dp))
                        Column(Modifier.weight(1f)) {
                            Text(r.title, color = VyboraTextPrimary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                            Text("Hosted by ${r.host} · ${r.members} listening", color = VyboraTextSecondary, fontSize = 11.sp)
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(VyboraCyan)
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                        ) {
                            Text("Invite", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}

// ─── Game Invite Picker Sheet ─────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GameInvitePickerSheet(
    onDismiss: () -> Unit,
    onSelect: (String) -> Unit,
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
            Text("Invite Friend to Game", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = VyboraTextPrimary)
            Spacer(Modifier.height(14.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(VyboraBg, RoundedCornerShape(14.dp))
                    .clickable { onSelect("ludo") }
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(VyboraCyanSoft),
                    contentAlignment = Alignment.Center,
                ) {
                    Text("🎲", fontSize = 24.sp)
                }
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text("Ludo Classic", color = VyboraTextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    Text("2-4 players · Board strategy", color = VyboraTextSecondary, fontSize = 12.sp)
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(VyboraPurple)
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                ) {
                    Text("Invite", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(Modifier.height(10.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(VyboraBg, RoundedCornerShape(14.dp))
                    .clickable { onSelect("spy") }
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(VyboraLavender),
                    contentAlignment = Alignment.Center,
                ) {
                    Text("🕵️", fontSize = 24.sp)
                }
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text("Who's the Spy", color = VyboraTextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    Text("Social deduction · Word game", color = VyboraTextSecondary, fontSize = 12.sp)
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(VyboraPurple)
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                ) {
                    Text("Invite", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(Modifier.height(20.dp))
        }
    }
}

// ─── Fullscreen Photo Viewer Dialog ──────────────────────────────────────────

@Composable
private fun FullscreenPhotoDialog(path: String, onDismiss: () -> Unit) {
    val bitmap = remember(path) {
        runCatching { BitmapFactory.decodeFile(path)?.asImageBitmap() }.getOrNull()
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.95f)),
            contentAlignment = Alignment.Center,
        ) {
            if (bitmap != null) {
                Image(
                    bitmap = bitmap,
                    contentDescription = "Full photo",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentScale = ContentScale.Fit,
                )
            }
            Box(
                modifier = Modifier
                    .padding(24.dp)
                    .align(Alignment.TopEnd)
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.2f))
                    .clickable(onClick = onDismiss),
                contentAlignment = Alignment.Center,
            ) {
                Icon(painterResource(R.drawable.vb_chat_ic_close), contentDescription = "Close", tint = Color.White, modifier = Modifier.size(16.dp))
            }
        }
    }
}

// ─── Celebration Confetti Overlay (Section 7) ────────────────────────────────

@Composable
private fun CelebrationOverlay(onComplete: () -> Unit) {
    var visible by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        delay(2200)
        visible = false
        onComplete()
    }

    AnimatedVisibility(
        visible = visible,
        enter = scaleIn(tween(300)) + fadeIn(tween(200)),
        exit = fadeOut(tween(400)) + scaleOut(tween(400)),
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("🎉 ✨ 🎁 ✨ 🎉", fontSize = 36.sp)
                Spacer(Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.Black.copy(alpha = 0.75f))
                        .padding(horizontal = 20.dp, vertical = 10.dp),
                ) {
                    Text("Gift Sent Successfully!", color = Gold, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// ─── Typing Dots Animation ───────────────────────────────────────────────────

@Composable
private fun TypingDots() {
    val infiniteTransition = rememberInfiniteTransition(label = "dots")
    val alpha1 by infiniteTransition.animateFloat(0.2f, 1f, infiniteRepeatable(tween(600), RepeatMode.Reverse), label = "d1")
    val alpha2 by infiniteTransition.animateFloat(0.2f, 1f, infiniteRepeatable(tween(600, delayMillis = 200), RepeatMode.Reverse), label = "d2")
    val alpha3 by infiniteTransition.animateFloat(0.2f, 1f, infiniteRepeatable(tween(600, delayMillis = 400), RepeatMode.Reverse), label = "d3")

    Row(modifier = Modifier.padding(start = 4.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(3.dp).background(VyboraCyan.copy(alpha = alpha1), CircleShape))
        Spacer(Modifier.width(2.dp))
        Box(Modifier.size(3.dp).background(VyboraCyan.copy(alpha = alpha2), CircleShape))
        Spacer(Modifier.width(2.dp))
        Box(Modifier.size(3.dp).background(VyboraCyan.copy(alpha = alpha3), CircleShape))
    }
}

@Composable
private fun ComposerIconButton(icon: Int, desc: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(VyboraBg)
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

@Composable
private fun wallBrush(w: Int): Brush = when (w) {
    1 -> Brush.verticalGradient(listOf(VyboraLavender, VyboraBg))
    2 -> Brush.verticalGradient(listOf(VyboraCyanSoft, VyboraBg))
    3 -> Brush.verticalGradient(listOf(Midnight, DeepPurple))
    else -> Brush.verticalGradient(listOf(VyboraSurface, VyboraBg))
}
