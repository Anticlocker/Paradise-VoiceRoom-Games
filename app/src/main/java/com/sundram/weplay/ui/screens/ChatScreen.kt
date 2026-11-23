package com.sundram.weplay.ui.screens

import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.sundram.weplay.R
import com.sundram.weplay.data.ChatMessage
import com.sundram.weplay.data.MessagingStore
import com.sundram.weplay.ui.theme.AuroraChatsBackdrop
import com.sundram.weplay.ui.theme.AuroraChatsTokens
import com.sundram.weplay.ui.theme.AuroraFramedAvatar
import com.sundram.weplay.ui.theme.WallBlue
import com.sundram.weplay.ui.theme.WallMint
import com.sundram.weplay.ui.theme.WallPurple
import com.sundram.weplay.ui.util.formatDayLabel
import com.sundram.weplay.ui.util.formatMessageTime
import kotlinx.coroutines.delay

private val REPORT_REASONS = listOf(
    "Harassment",
    "Inappropriate content",
    "Fake profile",
    "Other",
)

private sealed interface ChatItem {
    data class Msg(val m: ChatMessage) : ChatItem
    data class DateSep(val label: String) : ChatItem
}

private fun buildChatItems(messages: List<ChatMessage>): List<ChatItem> {
    val out = mutableListOf<ChatItem>()
    var lastDay: String? = null
    for (m in messages) {
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
    private var recorder: android.media.MediaRecorder? = null
    private var startTs: Long = 0L
    private var outputFile: java.io.File? = null

    fun start(context: android.content.Context) {
        cancel()
        outputFile = java.io.File(context.cacheDir, "voice_${System.currentTimeMillis()}.m4a")
            .apply { parentFile?.mkdirs() }
        val r = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            android.media.MediaRecorder(context)
        } else {
            @Suppress("DEPRECATION")
            android.media.MediaRecorder()
        }
        try {
            r.setAudioSource(android.media.MediaRecorder.AudioSource.MIC)
            r.setOutputFormat(android.media.MediaRecorder.OutputFormat.MPEG_4)
            r.setAudioEncoder(android.media.MediaRecorder.AudioEncoder.AAC)
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

@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun ChatScreen(
    nav: NavController,
    userId: String,
    store: MessagingStore,
    profile: com.sundram.weplay.data.UserProfile? = null,
    onSaveCoins: (Long) -> Unit = {},
) {
    var version by remember { mutableStateOf(0) }
    var input by remember(userId) { mutableStateOf("") }
    var menuOpen by remember { mutableStateOf(false) }
    var confirmClear by remember { mutableStateOf(false) }
    var confirmBlock by remember { mutableStateOf(false) }
    var confirmReport by remember { mutableStateOf(false) }
    var reportReason by remember { mutableStateOf(REPORT_REASONS.first()) }
    var showGiftPicker by remember { mutableStateOf(false) }
    var showPhotoPicker by remember { mutableStateOf(false) }
    var coinBalance by remember(userId) { mutableStateOf(profile?.coins ?: 500L) }
    LaunchedEffect(profile?.coins) {
        profile?.coins?.let { coinBalance = it }
    }
    var recordingState by remember { mutableStateOf(RecorderState.IDLE) }
    val context = LocalContext.current

    fun bump() { version++ }

    fun sendInput() {
        val text = input.trim()
        if (text.isEmpty()) return
        if (store.sendMessage(userId, text) != null) {
            input = ""
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
            store.sendMedia(userId, com.sundram.weplay.data.MsgType.VOICE, path, durationMs)
            bump()
        }
    }

    val micPermissionLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
        androidx.activity.result.contract.ActivityResultContracts.RequestPermission(),
    ) { granted ->
        if (granted) startRecording()
    }

    fun requestMicAndRecord() {
        val granted = androidx.core.content.ContextCompat.checkSelfPermission(
            context, android.Manifest.permission.RECORD_AUDIO,
        ) == android.content.pm.PackageManager.PERMISSION_GRANTED
        if (granted) startRecording() else micPermissionLauncher.launch(android.Manifest.permission.RECORD_AUDIO)
    }

    val currentRecordingState by androidx.compose.runtime.rememberUpdatedState(recordingState)
    DisposableEffect(Unit) {
        onDispose {
            if (currentRecordingState == RecorderState.RECORDING) VoiceRecorder.stop()
        }
    }

    val user = store.userById(userId)
    val chat = remember(version, userId) { if (userId.isNotEmpty()) store.openChat(userId) else null }
    val messages = chat?.messages ?: emptyList()
    val chatItems = buildChatItems(messages)
    val blocked = remember(version, userId) { store.isBlocked(userId) }
    val listState = rememberLazyListState()
    val atBottom by remember {
        derivedStateOf {
            val info = listState.layoutInfo
            val lastVisible = info.visibleItemsInfo.lastOrNull()?.index ?: -1
            lastVisible >= info.totalItemsCount - 1
        }
    }

    val stickToBottom = atBottom
    LaunchedEffect(chatItems.size) {
        if (chatItems.isNotEmpty() && (stickToBottom || messages.lastOrNull()?.fromMe == true)) {
            listState.animateScrollToItem(chatItems.lastIndex)
        }
    }

    LaunchedEffect(userId) {
        while (true) {
            delay(1000)
            if (store.flushDueReplies()) bump()
        }
    }

    DisposableEffect(Unit) {
        onDispose { VoiceRecorder.cancel() }
    }

    // restore draft
    LaunchedEffect(userId) {
        input = (store.chat(userId)?.draft ?: "")
    }

    val wallOverlay = customWallpaperOverlay(chat?.wallpaper ?: 0)

    AuroraChatsBackdrop(
        backgroundRes = R.drawable.ap_chats_hybrid_bg_conversation,
        modifier = Modifier.fillMaxSize(),
    ) {
        // Optional wallpaper tint overlay if user selected wallpaper 1, 2, or 3
        if (wallOverlay != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(wallOverlay)
            )
        }

        Column(
            modifier = Modifier.fillMaxSize(),
        ) {
            // ==================== 1. TOP CONVERSATION BAR ====================
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(AuroraChatsTokens.Header.copy(alpha = 0.92f))
                    .border(
                        0.5.dp,
                        AuroraChatsTokens.CardBorder,
                        RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp),
                    )
                    .padding(horizontal = 12.dp, vertical = 10.dp),
            ) {
                // Back Button
                Image(
                    painter = painterResource(R.drawable.ap_chats_hybrid_ic_back),
                    contentDescription = "Back",
                    modifier = Modifier
                        .size(32.dp)
                        .clickable { nav.popBackStack() }
                        .padding(2.dp),
                )

                Spacer(Modifier.width(8.dp))

                // Avatar & Name & Status
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { nav.navigate("user/$userId") },
                ) {
                    AuroraFramedAvatar(
                        name = user?.name ?: userId,
                        size = 40.dp,
                        frameRes = R.drawable.ap_chats_hybrid_frame_royal,
                        online = user?.online == true && !blocked,
                    )

                    Spacer(Modifier.width(10.dp))

                    Column {
                        Text(
                            text = user?.name ?: userId,
                            color = AuroraChatsTokens.TextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (!blocked && user?.online == true) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
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
                                    when {
                                        blocked -> "Blocked"
                                        else -> "Offline"
                                    },
                                    color = AuroraChatsTokens.TextSecondary,
                                    fontSize = 11.sp,
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.width(6.dp))

                // Audio Call Icon Action
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(AuroraChatsTokens.SurfaceElevated, CircleShape)
                        .border(1.dp, AuroraChatsTokens.CardBorder, CircleShape)
                        .clickable {
                            Toast.makeText(context, "Voice call to ${user?.name ?: userId}...", Toast.LENGTH_SHORT).show()
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_phone),
                        contentDescription = "Audio call",
                        tint = AuroraChatsTokens.CrystalCyan,
                        modifier = Modifier.size(17.dp),
                    )
                }

                Spacer(Modifier.width(8.dp))

                // Video Call Icon Action
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(AuroraChatsTokens.SurfaceElevated, CircleShape)
                        .border(1.dp, AuroraChatsTokens.CardBorder, CircleShape)
                        .clickable {
                            Toast.makeText(context, "Video call to ${user?.name ?: userId}...", Toast.LENGTH_SHORT).show()
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_videocam),
                        contentDescription = "Video call",
                        tint = AuroraChatsTokens.CrystalCyan,
                        modifier = Modifier.size(18.dp),
                    )
                }

                Spacer(Modifier.width(8.dp))

                // More Menu Button
                Box {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(AuroraChatsTokens.SurfaceElevated, CircleShape)
                            .border(1.dp, AuroraChatsTokens.CardBorder, CircleShape)
                            .clickable { menuOpen = true },
                        contentAlignment = Alignment.Center,
                    ) {
                        Image(
                            painter = painterResource(R.drawable.ap_chats_hybrid_ic_settings),
                            contentDescription = "Menu",
                            modifier = Modifier.size(18.dp),
                        )
                    }

                    DropdownMenu(
                        expanded = menuOpen,
                        onDismissRequest = { menuOpen = false },
                        containerColor = AuroraChatsTokens.SurfaceElevated,
                    ) {
                        DropdownMenuItem(
                            text = {
                                Text(
                                    if (chat?.pinned == true) "Unpin chat" else "Pin chat",
                                    color = AuroraChatsTokens.TextPrimary,
                                )
                            },
                            leadingIcon = {
                                Image(
                                    painter = painterResource(R.drawable.ap_chats_hybrid_ic_pin),
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                )
                            },
                            onClick = {
                                store.setPinned(userId, chat?.pinned != true)
                                menuOpen = false
                                bump()
                            },
                        )
                        DropdownMenuItem(
                            text = {
                                Text(
                                    if (chat?.muted == true) "Unmute notifications" else "Mute notifications",
                                    color = AuroraChatsTokens.TextPrimary,
                                )
                            },
                            leadingIcon = {
                                Image(
                                    painter = painterResource(R.drawable.ap_chats_hybrid_ic_mute),
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                )
                            },
                            onClick = {
                                store.setMuted(userId, chat?.muted != true)
                                menuOpen = false
                                bump()
                            },
                        )
                        DropdownMenuItem(
                            text = {
                                Text("Change wallpaper", color = AuroraChatsTokens.TextPrimary)
                            },
                            leadingIcon = {
                                Image(
                                    painter = painterResource(R.drawable.ap_chats_hybrid_ic_wallpaper),
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                )
                            },
                            onClick = {
                                store.cycleWallpaper(userId)
                                menuOpen = false
                                bump()
                            },
                        )
                        DropdownMenuItem(
                            text = { Text("View profile", color = AuroraChatsTokens.TextPrimary) },
                            onClick = {
                                menuOpen = false
                                nav.navigate("user/$userId")
                            },
                        )
                        DropdownMenuItem(
                            text = { Text("Clear chat", color = AuroraChatsTokens.TextPrimary) },
                            leadingIcon = {
                                Image(
                                    painter = painterResource(R.drawable.ap_chats_hybrid_ic_clear),
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                )
                            },
                            onClick = {
                                menuOpen = false
                                confirmClear = true
                            },
                        )
                        DropdownMenuItem(
                            text = { Text("Block", color = AuroraChatsTokens.RoseAlert) },
                            leadingIcon = {
                                Image(
                                    painter = painterResource(R.drawable.ap_chats_hybrid_ic_block),
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                )
                            },
                            onClick = {
                                menuOpen = false
                                confirmBlock = true
                            },
                        )
                        DropdownMenuItem(
                            text = { Text("Report", color = AuroraChatsTokens.RoseAlert) },
                            leadingIcon = {
                                Image(
                                    painter = painterResource(R.drawable.ap_chats_hybrid_ic_report),
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                )
                            },
                            onClick = {
                                menuOpen = false
                                confirmReport = true
                            },
                        )
                    }
                }
            }

            // ==================== 2. MESSAGES LIST ====================
            if (blocked) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "You blocked ${user?.name ?: userId}",
                            color = AuroraChatsTokens.TextPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            "Unblock them to resume chatting.",
                            color = AuroraChatsTokens.TextSecondary,
                            fontSize = 12.sp,
                        )
                        Spacer(Modifier.height(16.dp))
                        Box(
                            modifier = Modifier
                                .background(AuroraChatsTokens.ActiveTabGradient, RoundedCornerShape(14.dp))
                                .clickable {
                                    store.unblock(userId)
                                    bump()
                                }
                                .padding(horizontal = 20.dp, vertical = 10.dp),
                        ) {
                            Text("Unblock", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else {
                LazyColumn(
                    state = listState,
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f),
                ) {
                    // HERO HEADER (Matches Screen 4 of reference image)
                    item {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp, bottom = 12.dp),
                        ) {
                            AuroraFramedAvatar(
                                name = user?.name ?: userId,
                                size = 80.dp,
                                frameRes = R.drawable.ap_chats_hybrid_frame_royal,
                                online = user?.online == true,
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = user?.name ?: userId,
                                color = AuroraChatsTokens.TextPrimary,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                            )
                            Spacer(Modifier.height(4.dp))
                            // Level & Friend Badge Pill
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .background(
                                        Brush.horizontalGradient(
                                            listOf(
                                                AuroraChatsTokens.RoyalGold.copy(alpha = 0.2f),
                                                AuroraChatsTokens.GlowPurple.copy(alpha = 0.2f),
                                            )
                                        ),
                                        RoundedCornerShape(12.dp),
                                    )
                                    .border(
                                        0.5.dp,
                                        AuroraChatsTokens.RoyalGold.copy(alpha = 0.6f),
                                        RoundedCornerShape(12.dp),
                                    )
                                    .padding(horizontal = 8.dp, vertical = 2.dp),
                            ) {
                                Text(
                                    "Lv.${user?.level ?: 5} • Friend",
                                    color = AuroraChatsTokens.RoyalGold,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                )
                            }
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = "Chatting with ${user?.name ?: userId}",
                                color = AuroraChatsTokens.TextSecondary,
                                fontSize = 12.sp,
                            )
                        }
                    }

                    // DATE SEPARATORS & MESSAGES
                    items(chatItems, key = {
                        when (it) {
                            is ChatItem.Msg -> "m" + it.m.id
                            is ChatItem.DateSep -> "d" + it.label
                        }
                    }) { item ->
                        when (item) {
                            is ChatItem.DateSep -> {
                                Box(
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .background(
                                                AuroraChatsTokens.SurfaceElevated.copy(alpha = 0.85f),
                                                RoundedCornerShape(12.dp),
                                            )
                                            .border(
                                                0.5.dp,
                                                AuroraChatsTokens.CardBorder,
                                                RoundedCornerShape(12.dp),
                                            )
                                            .padding(horizontal = 14.dp, vertical = 4.dp),
                                    ) {
                                        Text(
                                            item.label,
                                            color = AuroraChatsTokens.TextSecondary,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Medium,
                                        )
                                    }
                                }
                            }

                            is ChatItem.Msg -> HybridMessageBubble(
                                m = item.m,
                                partnerName = user?.name ?: userId,
                                onDelete = {
                                    store.deleteMessageForMe(userId, item.m.id)
                                    bump()
                                },
                                onRecall = {
                                    store.recallMessage(userId, item.m.id)
                                    bump()
                                },
                            )
                        }
                    }

                    // Typing Indicator
                    if (store.isTyping(userId)) {
                        item {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(start = 6.dp, top = 4.dp),
                            ) {
                                Text(
                                    "${user?.name ?: userId} is typing…",
                                    color = AuroraChatsTokens.CrystalCyan,
                                    fontSize = 12.sp,
                                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                )
                            }
                        }
                    }
                }

                // ==================== 3. INPUT COMPOSER BAR ====================
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(AuroraChatsTokens.Header.copy(alpha = 0.95f))
                        .border(
                            0.5.dp,
                            AuroraChatsTokens.CardBorder,
                            RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                        )
                        .imePadding()
                        .padding(horizontal = 10.dp, vertical = 10.dp),
                ) {
                    // Gift Button
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .background(
                                Brush.linearGradient(
                                    listOf(
                                        AuroraChatsTokens.GlowPurple.copy(alpha = 0.35f),
                                        AuroraChatsTokens.SurfaceElevated,
                                    )
                                ),
                                CircleShape,
                            )
                            .border(1.dp, AuroraChatsTokens.CardBorderGlow, CircleShape)
                            .clickable { showGiftPicker = true },
                        contentAlignment = Alignment.Center,
                    ) {
                        Image(
                            painter = painterResource(R.drawable.ap_chats_hybrid_ic_gift),
                            contentDescription = "Send gift",
                            modifier = Modifier.size(24.dp),
                        )
                    }

                    Spacer(Modifier.width(8.dp))

                    // Text Input Field with inside emoji button
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(AuroraChatsTokens.SurfaceElevated, RoundedCornerShape(22.dp))
                            .border(1.dp, AuroraChatsTokens.CardBorder, RoundedCornerShape(22.dp))
                            .padding(horizontal = 14.dp, vertical = 9.dp),
                        contentAlignment = Alignment.CenterStart,
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth(),
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
                                    color = AuroraChatsTokens.TextPrimary,
                                    fontSize = 14.sp,
                                ),
                                cursorBrush = SolidColor(AuroraChatsTokens.CrystalCyan),
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                                keyboardActions = KeyboardActions(
                                    onSend = { sendInput() },
                                ),
                                modifier = Modifier.weight(1f),
                                decorationBox = { field ->
                                    Box {
                                        if (input.isEmpty()) {
                                            Text(
                                                "Type a message...",
                                                color = AuroraChatsTokens.TextSecondary.copy(alpha = 0.6f),
                                                fontSize = 13.sp,
                                            )
                                        }
                                        field()
                                    }
                                },
                            )

                            Spacer(Modifier.width(6.dp))

                            // Emoji Icon inside input
                            Image(
                                painter = painterResource(R.drawable.ap_chats_hybrid_ic_emoji),
                                contentDescription = "Emoji",
                                modifier = Modifier
                                    .size(22.dp)
                                    .clickable {
                                        input += " 😊"
                                    },
                            )
                        }
                    }

                    Spacer(Modifier.width(8.dp))

                    // Voice Note / Mic Button
                    val isRecording = recordingState == RecorderState.RECORDING
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .background(
                                if (isRecording) AuroraChatsTokens.RoseGradient
                                else Brush.linearGradient(
                                    listOf(
                                        AuroraChatsTokens.SurfaceElevated,
                                        AuroraChatsTokens.Surface,
                                    )
                                ),
                                CircleShape,
                            )
                            .border(
                                1.dp,
                                if (isRecording) AuroraChatsTokens.RoseAlert else AuroraChatsTokens.CardBorder,
                                CircleShape,
                            )
                            .clickable {
                                if (isRecording) stopRecording() else requestMicAndRecord()
                            },
                        contentAlignment = Alignment.Center,
                    ) {
                        Image(
                            painter = painterResource(R.drawable.ap_chats_hybrid_ic_mic),
                            contentDescription = if (isRecording) "Stop voice note" else "Voice note",
                            modifier = Modifier.size(20.dp),
                        )
                    }

                    Spacer(Modifier.width(6.dp))

                    if (input.isBlank()) {
                        // Media / Photo Button
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .background(
                                    Brush.linearGradient(
                                        listOf(
                                            AuroraChatsTokens.SurfaceElevated,
                                            AuroraChatsTokens.Surface,
                                        )
                                    ),
                                    CircleShape,
                                )
                                .border(1.dp, AuroraChatsTokens.CardBorder, CircleShape)
                                .clickable { showPhotoPicker = true },
                            contentAlignment = Alignment.Center,
                        ) {
                            Image(
                                painter = painterResource(R.drawable.ap_chats_hybrid_ic_attach),
                                contentDescription = "Attach photo",
                                modifier = Modifier.size(20.dp),
                            )
                        }
                    } else {
                        // Send Button
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .background(AuroraChatsTokens.ActiveTabGradient, CircleShape)
                                .clickable { sendInput() },
                            contentAlignment = Alignment.Center,
                        ) {
                            Image(
                                painter = painterResource(R.drawable.ap_chats_hybrid_ic_send),
                                contentDescription = "Send",
                                modifier = Modifier.size(20.dp),
                            )
                        }
                    }
                }
            }
        }
    }

    // ==================== 4. BOTTOM SHEETS & DIALOGS ====================
    if (showGiftPicker) {
        GiftPickerSheet(
            balance = coinBalance,
            onDismiss = { showGiftPicker = false },
            onSelect = { gift ->
                val ok = store.sendGift(userId, gift.id) { newBal ->
                    coinBalance = newBal
                    onSaveCoins(newBal)
                }
                if (ok) bump()
                showGiftPicker = false
            },
        )
    }

    if (showPhotoPicker) {
        PhotoPickerSheet(
            onDismiss = { showPhotoPicker = false },
            onPick = { path ->
                store.sendMedia(userId, com.sundram.weplay.data.MsgType.IMAGE, path)
                bump()
                showPhotoPicker = false
            },
        )
    }

    if (confirmClear) {
        AlertDialog(
            onDismissRequest = { confirmClear = false },
            title = {
                Text(
                    "Clear chat?",
                    color = AuroraChatsTokens.TextPrimary,
                    fontWeight = FontWeight.Bold,
                )
            },
            text = {
                Text(
                    "All messages in this conversation will be permanently deleted.",
                    color = AuroraChatsTokens.TextSecondary,
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    store.clearChat(userId)
                    confirmClear = false
                    bump()
                }) {
                    Text("Clear", color = AuroraChatsTokens.RoseAlert, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { confirmClear = false }) {
                    Text("Cancel", color = AuroraChatsTokens.TextSecondary)
                }
            },
            containerColor = AuroraChatsTokens.SurfaceElevated,
        )
    }

    if (confirmBlock) {
        AlertDialog(
            onDismissRequest = { confirmBlock = false },
            title = {
                Text(
                    "Block ${user?.name ?: userId}?",
                    color = AuroraChatsTokens.TextPrimary,
                    fontWeight = FontWeight.Bold,
                )
            },
            text = {
                Text(
                    "They will not be able to message you or see your profile. You will be removed from each other's friends list.",
                    color = AuroraChatsTokens.TextSecondary,
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    store.block(userId)
                    confirmBlock = false
                    nav.popBackStack()
                }) {
                    Text("Block", color = AuroraChatsTokens.RoseAlert, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { confirmBlock = false }) {
                    Text("Cancel", color = AuroraChatsTokens.TextSecondary)
                }
            },
            containerColor = AuroraChatsTokens.SurfaceElevated,
        )
    }

    if (confirmReport) {
        AlertDialog(
            onDismissRequest = { confirmReport = false },
            title = {
                Text(
                    "Report ${user?.name ?: userId}",
                    color = AuroraChatsTokens.TextPrimary,
                    fontWeight = FontWeight.Bold,
                )
            },
            text = {
                Column {
                    Text(
                        "Pick a reason. The user will also be blocked.",
                        color = AuroraChatsTokens.TextSecondary,
                        fontSize = 12.sp,
                    )
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
                                modifier = Modifier
                                    .size(18.dp)
                                    .border(
                                        1.5.dp,
                                        if (reportReason == r) AuroraChatsTokens.ActiveCyan else AuroraChatsTokens.TextSecondary,
                                        CircleShape,
                                    ),
                                contentAlignment = Alignment.Center,
                            ) {
                                if (reportReason == r) {
                                    Box(
                                        Modifier
                                            .size(10.dp)
                                            .background(AuroraChatsTokens.ActiveCyan, CircleShape)
                                    )
                                }
                            }
                            Spacer(Modifier.width(10.dp))
                            Text(r, color = AuroraChatsTokens.TextPrimary, fontSize = 13.sp)
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
                    Text("Report & Block", color = AuroraChatsTokens.RoseAlert, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { confirmReport = false }) {
                    Text("Cancel", color = AuroraChatsTokens.TextSecondary)
                }
            },
            containerColor = AuroraChatsTokens.SurfaceElevated,
        )
    }
}

// ==================== HYBRID MESSAGE BUBBLE ====================

@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
private fun HybridMessageBubble(
    m: ChatMessage,
    partnerName: String,
    onDelete: () -> Unit = {},
    onRecall: () -> Unit = {},
) {
    var showMenu by remember { mutableStateOf(false) }
    val clipboard = LocalClipboardManager.current
    val fromMe = m.fromMe
    val withinRecallWindow = System.currentTimeMillis() - m.ts <= 120_000L

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (fromMe) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Bottom,
    ) {
        if (!fromMe) {
            // Incoming message avatar thumbnail
            AuroraFramedAvatar(
                name = partnerName,
                size = 32.dp,
                frameRes = R.drawable.ap_chats_hybrid_frame_royal,
            )
            Spacer(Modifier.width(8.dp))
        }

        Box {
            Box(
                modifier = Modifier
                    .background(
                        if (fromMe) AuroraChatsTokens.OutgoingBubbleGradient
                        else Brush.linearGradient(
                            listOf(
                                AuroraChatsTokens.IncomingBubble,
                                AuroraChatsTokens.IncomingBubble.copy(alpha = 0.95f),
                            )
                        ),
                        RoundedCornerShape(
                            topStart = 18.dp,
                            topEnd = 18.dp,
                            bottomStart = if (fromMe) 18.dp else 4.dp,
                            bottomEnd = if (fromMe) 4.dp else 18.dp,
                        ),
                    )
                    .border(
                        1.dp,
                        if (fromMe) AuroraChatsTokens.CardBorderGlow.copy(alpha = 0.35f) else AuroraChatsTokens.CardBorder,
                        RoundedCornerShape(
                            topStart = 18.dp,
                            topEnd = 18.dp,
                            bottomStart = if (fromMe) 18.dp else 4.dp,
                            bottomEnd = if (fromMe) 4.dp else 18.dp,
                        ),
                    )
                    .combinedClickable(
                        onClick = {},
                        onLongClick = { showMenu = true },
                    )
                    .padding(horizontal = 14.dp, vertical = 9.dp),
            ) {
                // Long press dropdown
                if (showMenu) {
                    DropdownMenu(
                        expanded = true,
                        onDismissRequest = { showMenu = false },
                        containerColor = AuroraChatsTokens.SurfaceElevated,
                    ) {
                        DropdownMenuItem(
                            text = { Text("Copy", color = AuroraChatsTokens.TextPrimary) },
                            leadingIcon = {
                                Image(
                                    painter = painterResource(R.drawable.ap_chats_hybrid_ic_copy),
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                )
                            },
                            onClick = {
                                showMenu = false
                                clipboard.setText(AnnotatedString(m.text))
                            },
                        )
                        if (fromMe && withinRecallWindow && !m.recalled) {
                            DropdownMenuItem(
                                text = { Text("Recall message", color = AuroraChatsTokens.TextPrimary) },
                                leadingIcon = {
                                    Image(
                                        painter = painterResource(R.drawable.ap_chats_hybrid_ic_reply),
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp),
                                    )
                                },
                                onClick = {
                                    showMenu = false
                                    onRecall()
                                },
                            )
                        }
                        if (!m.recalled) {
                            DropdownMenuItem(
                                text = { Text("Delete for me", color = AuroraChatsTokens.RoseAlert) },
                                leadingIcon = {
                                    Image(
                                        painter = painterResource(R.drawable.ap_chats_hybrid_ic_delete),
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp),
                                    )
                                },
                                onClick = {
                                    showMenu = false
                                    onDelete()
                                },
                            )
                        }
                    }
                }

                Column {
                    when {
                        m.recalled -> {
                            Text(
                                "Message recalled",
                                color = if (fromMe) Color.White.copy(alpha = 0.7f) else AuroraChatsTokens.TextSecondary,
                                fontSize = 12.sp,
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                            )
                        }

                        m.type == com.sundram.weplay.data.MsgType.GIFT -> {
                            val gift = com.sundram.weplay.data.DemoGifts.firstOrNull { it.id == m.giftId }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Image(
                                    painter = painterResource(R.drawable.ap_chats_hybrid_ic_gift),
                                    contentDescription = null,
                                    modifier = Modifier.size(28.dp),
                                )
                                Spacer(Modifier.width(8.dp))
                                Column {
                                    Text(
                                        gift?.name ?: "Celestial Gift",
                                        color = if (fromMe) Color.White else AuroraChatsTokens.TextPrimary,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                    )
                                    Text(
                                        "${m.giftCost} coins",
                                        color = AuroraChatsTokens.RoyalGold,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                    )
                                }
                            }
                        }

                        m.type == com.sundram.weplay.data.MsgType.IMAGE && m.mediaPath != null -> {
                            val path = m.mediaPath
                            val bitmap = remember(path) {
                                runCatching { android.graphics.BitmapFactory.decodeFile(path)?.asImageBitmap() }.getOrNull()
                            }
                            if (bitmap != null) {
                                Image(
                                    bitmap = bitmap,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(width = 190.dp, height = 130.dp)
                                        .clip(RoundedCornerShape(12.dp)),
                                    contentScale = ContentScale.Crop,
                                )
                            } else {
                                Text("Photo", color = if (fromMe) Color.White else AuroraChatsTokens.TextPrimary, fontSize = 13.sp)
                            }
                        }

                        m.type == com.sundram.weplay.data.MsgType.VOICE && m.mediaPath != null -> {
                            HybridVoiceNoteRow(m = m, fromMe = fromMe)
                        }

                        else -> {
                            Text(
                                text = m.text,
                                color = if (fromMe) Color.White else AuroraChatsTokens.TextPrimary,
                                fontSize = 14.sp,
                                lineHeight = 19.sp,
                            )
                        }
                    }

                    Spacer(Modifier.height(3.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.End,
                        modifier = Modifier.align(Alignment.End),
                    ) {
                        Text(
                            text = (if (m.type == com.sundram.weplay.data.MsgType.VOICE) "🎤 " else "") +
                                formatMessageTime(m.ts),
                            color = if (fromMe) Color.White.copy(alpha = 0.75f) else AuroraChatsTokens.TextSecondary,
                            fontSize = 10.sp,
                        )
                        if (fromMe) {
                            Spacer(Modifier.width(4.dp))
                            Image(
                                painter = painterResource(
                                    if (m.read) R.drawable.ap_chats_hybrid_ic_read else R.drawable.ap_chats_hybrid_ic_check
                                ),
                                contentDescription = if (m.read) "Read" else "Sent",
                                modifier = Modifier.size(13.dp),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HybridVoiceNoteRow(m: ChatMessage, fromMe: Boolean) {
    var playing by remember { mutableStateOf(false) }
    var player by remember { mutableStateOf<android.media.MediaPlayer?>(null) }

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
                .size(34.dp)
                .background(
                    if (fromMe) Color.White.copy(alpha = 0.2f) else AuroraChatsTokens.ActiveCyan.copy(alpha = 0.25f),
                    CircleShape,
                )
                .clickable {
                    if (playing) {
                        player?.runCatching { stop() }
                        player?.release()
                        player = null
                        playing = false
                    } else {
                        val path = m.mediaPath ?: return@clickable
                        val next = android.media.MediaPlayer()
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
                tint = if (fromMe) Color.White else AuroraChatsTokens.ActiveCyan,
                modifier = Modifier.size(15.dp),
            )
        }
        Spacer(Modifier.width(8.dp))
        Text(
            "${(m.durationMs / 1000).toInt()}s",
            color = if (fromMe) Color.White else AuroraChatsTokens.TextPrimary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
        )
    }
}

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
private fun GiftPickerSheet(
    balance: Long,
    onDismiss: () -> Unit,
    onSelect: (com.sundram.weplay.data.GiftItem) -> Unit,
) {
    androidx.compose.material3.ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = AuroraChatsTokens.SurfaceElevated,
        scrimColor = Color.Black.copy(alpha = 0.7f),
    ) {
        Column(Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "Send a celestial gift",
                    color = AuroraChatsTokens.TextPrimary,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(Modifier.weight(1f))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(AuroraChatsTokens.Surface, RoundedCornerShape(12.dp))
                        .border(1.dp, AuroraChatsTokens.RoyalGold.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_coin),
                        contentDescription = null,
                        tint = AuroraChatsTokens.RoyalGold,
                        modifier = Modifier.size(16.dp),
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        "$balance",
                        color = AuroraChatsTokens.RoyalGold,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            androidx.compose.foundation.lazy.grid.LazyVerticalGrid(
                columns = androidx.compose.foundation.lazy.grid.GridCells.Fixed(3),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                items(com.sundram.weplay.data.DemoGifts.size) { idx ->
                    val gift = com.sundram.weplay.data.DemoGifts[idx]
                    val afford = balance >= gift.cost
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .background(AuroraChatsTokens.Surface, RoundedCornerShape(14.dp))
                            .border(
                                1.dp,
                                if (afford) AuroraChatsTokens.CardBorder else AuroraChatsTokens.CardBorder.copy(alpha = 0.2f),
                                RoundedCornerShape(14.dp),
                            )
                            .clickable(enabled = afford) { onSelect(gift) }
                            .padding(12.dp),
                    ) {
                        Image(
                            painter = painterResource(R.drawable.ap_chats_hybrid_ic_gift),
                            contentDescription = null,
                            modifier = Modifier.size(36.dp),
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            gift.name,
                            color = if (afford) AuroraChatsTokens.TextPrimary else AuroraChatsTokens.TextSecondary.copy(alpha = 0.5f),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                        )
                        Spacer(Modifier.height(2.dp))
                        Text(
                            "${gift.cost}c",
                            color = if (afford) AuroraChatsTokens.RoyalGold else AuroraChatsTokens.TextSecondary.copy(alpha = 0.4f),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }

            Spacer(Modifier.height(14.dp))
            Text(
                "Gifts transfer instant charm to your friend and appear in the conversation.",
                color = AuroraChatsTokens.TextSecondary,
                fontSize = 11.sp,
            )
            Spacer(Modifier.height(16.dp))
        }
    }
}

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
private fun PhotoPickerSheet(onDismiss: () -> Unit, onPick: (String) -> Unit) {
    val context = LocalContext.current
    val store = remember { com.sundram.weplay.data.ProfileStore(context) }
    val launcher = androidx.activity.compose.rememberLauncherForActivityResult(
        androidx.activity.result.contract.ActivityResultContracts.GetContent(),
    ) { uri: android.net.Uri? ->
        if (uri != null) {
            val path = store.saveChatPhoto(uri)
            if (path != null) onPick(path)
        }
    }

    androidx.compose.material3.ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = AuroraChatsTokens.SurfaceElevated,
        scrimColor = Color.Black.copy(alpha = 0.7f),
    ) {
        LaunchedEffect(Unit) {
            launcher.launch("image/*")
        }
        Box(
            modifier = Modifier.fillMaxWidth().padding(28.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                "Opening gallery…",
                color = AuroraChatsTokens.TextPrimary,
                fontSize = 14.sp,
            )
        }
    }
}

@Composable
private fun customWallpaperOverlay(w: Int): Brush? {
    val b = WallBlue
    val p = WallPurple
    val m = WallMint
    return when (w) {
        1 -> Brush.verticalGradient(listOf(b.copy(alpha = 0.35f), Color.Transparent))
        2 -> Brush.verticalGradient(listOf(p.copy(alpha = 0.35f), Color.Transparent))
        3 -> Brush.verticalGradient(listOf(m.copy(alpha = 0.35f), Color.Transparent))
        else -> null
    }
}
