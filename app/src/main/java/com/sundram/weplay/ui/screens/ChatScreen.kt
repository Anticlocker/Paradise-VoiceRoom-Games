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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.sundram.weplay.R
import com.sundram.weplay.data.ChatMessage
import com.sundram.weplay.data.MessagingStore
import com.sundram.weplay.ui.components.InitialsAvatar
import com.sundram.weplay.ui.theme.WallBlue
import com.sundram.weplay.ui.theme.WallMint
import com.sundram.weplay.ui.theme.WallPurple
import com.sundram.weplay.ui.util.formatDayLabel
import com.sundram.weplay.ui.util.formatMessageTime
import kotlinx.coroutines.delay

/**
 * Unslopped Design Tokens for ChatScreen (Ink/Graphite Nocturnal Ramp)
 * Based on unslop 9-pass design framework.
 */
private object UnsloppedChatTokens {
    val BgBase          = Color(0xFF0B0D14) // Deep midnight observatory base
    val Surface         = Color(0xFF11141C) // Quiet chrome top bar & composer
    val SurfaceRaised   = Color(0xFF1A1E28) // Input field & elevated cards
    val TextPrimary     = Color(0xFFEEF0F5) // Crisp readable primary text
    val TextSecondary   = Color(0xFFA3A9B8) // Balanced secondary metadata
    val TextMuted       = Color(0xFF7A8296) // Low-prominence captions & hints
    val Hairline        = Color(0xFFEEF0F5).copy(alpha = 0.08f) // 8% hairline border
    val HairlineFocused = Color(0xFF4F46E5).copy(alpha = 0.40f) // Subtle accent focus
    val Accent          = Color(0xFF4F46E5) // Purposeful indigo accent (no gradient)
    val IncomingBubble  = Color(0xFF161B26) // Deep slate neutral incoming
    val OutgoingBubble  = Color(0xFF4338CA) // Solid rich indigo outgoing
    val OnlineBeacon    = Color(0xFF4CC38A) // Clean active green
    val AlertDanger     = Color(0xFFE5484D) // Restrained negative red
    val GoldCoin        = Color(0xFFF59E0B) // Amber coin balance
}

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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(UnsloppedChatTokens.BgBase),
    ) {
        // Optional user wallpaper tint overlay
        if (wallOverlay != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(wallOverlay)
            )
        }

        Column(modifier = Modifier.fillMaxSize()) {
            // ==================== 1. TOP APP BAR (56dp, quiet chrome) ====================
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .background(UnsloppedChatTokens.Surface)
                    .border(
                        1.dp,
                        UnsloppedChatTokens.Hairline,
                        RoundedCornerShape(bottomStart = 0.dp, bottomEnd = 0.dp),
                    )
                    .padding(horizontal = 12.dp),
            ) {
                // Back Button
                Image(
                    painter = painterResource(R.drawable.ap_chats_hybrid_ic_back),
                    contentDescription = "Back",
                    modifier = Modifier
                        .size(32.dp)
                        .clickable { nav.popBackStack() }
                        .padding(4.dp),
                )

                Spacer(Modifier.width(8.dp))

                // Avatar & Name & Status
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { nav.navigate("user/$userId") },
                ) {
                    Box(contentAlignment = Alignment.BottomEnd) {
                        InitialsAvatar(
                            name = user?.name ?: userId,
                            size = 36.dp,
                            modifier = Modifier.clip(CircleShape),
                        )
                        if (!blocked && user?.online == true) {
                            Box(
                                modifier = Modifier
                                    .size(9.dp)
                                    .background(UnsloppedChatTokens.OnlineBeacon, CircleShape)
                                    .border(1.5.dp, UnsloppedChatTokens.Surface, CircleShape)
                            )
                        }
                    }

                    Spacer(Modifier.width(10.dp))

                    Column {
                        Text(
                            text = user?.name ?: userId,
                            color = UnsloppedChatTokens.TextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium,
                        )
                        Text(
                            text = when {
                                blocked -> "Blocked"
                                user?.online == true -> "Active now"
                                else -> "Offline"
                            },
                            color = if (!blocked && user?.online == true) UnsloppedChatTokens.OnlineBeacon else UnsloppedChatTokens.TextMuted,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Normal,
                        )
                    }
                }

                Spacer(Modifier.width(8.dp))

                // Audio Call Action
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(UnsloppedChatTokens.SurfaceRaised, CircleShape)
                        .border(1.dp, UnsloppedChatTokens.Hairline, CircleShape)
                        .clickable {
                            Toast.makeText(context, "Starting voice call", Toast.LENGTH_SHORT).show()
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_phone),
                        contentDescription = "Audio call",
                        tint = UnsloppedChatTokens.TextSecondary,
                        modifier = Modifier.size(16.dp),
                    )
                }

                Spacer(Modifier.width(8.dp))

                // Video Call Action
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(UnsloppedChatTokens.SurfaceRaised, CircleShape)
                        .border(1.dp, UnsloppedChatTokens.Hairline, CircleShape)
                        .clickable {
                            Toast.makeText(context, "Starting video call", Toast.LENGTH_SHORT).show()
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_videocam),
                        contentDescription = "Video call",
                        tint = UnsloppedChatTokens.TextSecondary,
                        modifier = Modifier.size(17.dp),
                    )
                }

                Spacer(Modifier.width(8.dp))

                // More Options Menu
                Box {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(UnsloppedChatTokens.SurfaceRaised, CircleShape)
                            .border(1.dp, UnsloppedChatTokens.Hairline, CircleShape)
                            .clickable { menuOpen = true },
                        contentAlignment = Alignment.Center,
                    ) {
                        Image(
                            painter = painterResource(R.drawable.ap_chats_hybrid_ic_settings),
                            contentDescription = "Settings",
                            modifier = Modifier.size(16.dp),
                        )
                    }

                    DropdownMenu(
                        expanded = menuOpen,
                        onDismissRequest = { menuOpen = false },
                        containerColor = UnsloppedChatTokens.SurfaceRaised,
                    ) {
                        DropdownMenuItem(
                            text = {
                                Text(
                                    if (chat?.pinned == true) "Unpin chat" else "Pin chat",
                                    color = UnsloppedChatTokens.TextPrimary,
                                    fontSize = 13.sp,
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
                                    color = UnsloppedChatTokens.TextPrimary,
                                    fontSize = 13.sp,
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
                                Text("Change wallpaper", color = UnsloppedChatTokens.TextPrimary, fontSize = 13.sp)
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
                            text = { Text("View profile", color = UnsloppedChatTokens.TextPrimary, fontSize = 13.sp) },
                            onClick = {
                                menuOpen = false
                                nav.navigate("user/$userId")
                            },
                        )
                        DropdownMenuItem(
                            text = { Text("Clear chat", color = UnsloppedChatTokens.TextPrimary, fontSize = 13.sp) },
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
                            text = { Text("Block", color = UnsloppedChatTokens.AlertDanger, fontSize = 13.sp) },
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
                            text = { Text("Report", color = UnsloppedChatTokens.AlertDanger, fontSize = 13.sp) },
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

            // ==================== 2. MESSAGES STREAM ====================
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
                            text = "${user?.name ?: userId} is blocked",
                            color = UnsloppedChatTokens.TextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium,
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "Unblock to resume messaging and calls.",
                            color = UnsloppedChatTokens.TextSecondary,
                            fontSize = 13.sp,
                        )
                        Spacer(Modifier.height(16.dp))
                        Box(
                            modifier = Modifier
                                .background(UnsloppedChatTokens.Accent, RoundedCornerShape(10.dp))
                                .clickable {
                                    store.unblock(userId)
                                    bump()
                                }
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                        ) {
                            Text("Unblock", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            } else {
                LazyColumn(
                    state = listState,
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f),
                ) {
                    // Subtle conversation header meta (Pass 6 Layout - no oversized hero blob)
                    item {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp, bottom = 12.dp),
                        ) {
                            InitialsAvatar(
                                name = user?.name ?: userId,
                                size = 52.dp,
                                modifier = Modifier.clip(CircleShape),
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = user?.name ?: userId,
                                color = UnsloppedChatTokens.TextPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                            )
                            Spacer(Modifier.height(2.dp))
                            Text(
                                text = "Lv.${user?.level ?: 5} • Direct Message",
                                color = UnsloppedChatTokens.TextMuted,
                                fontSize = 12.sp,
                            )
                        }
                    }

                    // Date Separators & Messages
                    items(chatItems, key = {
                        when (it) {
                            is ChatItem.Msg -> "m" + it.m.id
                            is ChatItem.DateSep -> "d" + it.label
                        }
                    }) { item ->
                        when (item) {
                            is ChatItem.DateSep -> {
                                Box(
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .background(
                                                UnsloppedChatTokens.SurfaceRaised,
                                                RoundedCornerShape(8.dp),
                                            )
                                            .border(
                                                1.dp,
                                                UnsloppedChatTokens.Hairline,
                                                RoundedCornerShape(8.dp),
                                            )
                                            .padding(horizontal = 10.dp, vertical = 4.dp),
                                    ) {
                                        Text(
                                            text = item.label,
                                            color = UnsloppedChatTokens.TextSecondary,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Medium,
                                        )
                                    }
                                }
                            }

                            is ChatItem.Msg -> UnsloppedMessageBubble(
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
                            Text(
                                text = "${user?.name ?: userId} is typing…",
                                color = UnsloppedChatTokens.TextMuted,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(start = 4.dp, top = 2.dp),
                            )
                        }
                    }
                }

                // ==================== 3. INPUT COMPOSER (Quiet chrome, 4px grid) ====================
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(UnsloppedChatTokens.Surface)
                        .border(
                            1.dp,
                            UnsloppedChatTokens.Hairline,
                            RoundedCornerShape(topStart = 0.dp, topEnd = 0.dp),
                        )
                        .imePadding()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                ) {
                    // Gift Button
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(UnsloppedChatTokens.SurfaceRaised, CircleShape)
                            .border(1.dp, UnsloppedChatTokens.Hairline, CircleShape)
                            .clickable { showGiftPicker = true },
                        contentAlignment = Alignment.Center,
                    ) {
                        Image(
                            painter = painterResource(R.drawable.ap_chats_hybrid_ic_gift),
                            contentDescription = "Send gift",
                            modifier = Modifier.size(18.dp),
                        )
                    }

                    Spacer(Modifier.width(8.dp))

                    // Text Input Field
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(UnsloppedChatTokens.SurfaceRaised, RoundedCornerShape(20.dp))
                            .border(1.dp, UnsloppedChatTokens.Hairline, RoundedCornerShape(20.dp))
                            .padding(horizontal = 14.dp, vertical = 8.dp),
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
                                color = UnsloppedChatTokens.TextPrimary,
                                fontSize = 14.sp,
                            ),
                            cursorBrush = SolidColor(UnsloppedChatTokens.Accent),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                            keyboardActions = KeyboardActions(
                                onSend = { sendInput() },
                            ),
                            modifier = Modifier.fillMaxWidth(),
                            decorationBox = { field ->
                                Box {
                                    if (input.isEmpty()) {
                                        Text(
                                            text = "Type a message",
                                            color = UnsloppedChatTokens.TextMuted,
                                            fontSize = 14.sp,
                                        )
                                    }
                                    field()
                                }
                            },
                        )
                    }

                    Spacer(Modifier.width(8.dp))

                    // Voice Note Button
                    val isRecording = recordingState == RecorderState.RECORDING
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(
                                if (isRecording) UnsloppedChatTokens.AlertDanger else UnsloppedChatTokens.SurfaceRaised,
                                CircleShape,
                            )
                            .border(
                                1.dp,
                                if (isRecording) UnsloppedChatTokens.AlertDanger else UnsloppedChatTokens.Hairline,
                                CircleShape,
                            )
                            .clickable {
                                if (isRecording) stopRecording() else requestMicAndRecord()
                            },
                        contentAlignment = Alignment.Center,
                    ) {
                        Image(
                            painter = painterResource(R.drawable.ap_chats_hybrid_ic_mic),
                            contentDescription = if (isRecording) "Stop voice note" else "Record voice note",
                            modifier = Modifier.size(17.dp),
                        )
                    }

                    Spacer(Modifier.width(8.dp))

                    if (input.isBlank()) {
                        // Media Attachment Button
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(UnsloppedChatTokens.SurfaceRaised, CircleShape)
                                .border(1.dp, UnsloppedChatTokens.Hairline, CircleShape)
                                .clickable { showPhotoPicker = true },
                            contentAlignment = Alignment.Center,
                        ) {
                            Image(
                                painter = painterResource(R.drawable.ap_chats_hybrid_ic_attach),
                                contentDescription = "Attach media",
                                modifier = Modifier.size(18.dp),
                            )
                        }
                    } else {
                        // Send Action Button (Solid Indigo Accent)
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(UnsloppedChatTokens.Accent, CircleShape)
                                .clickable { sendInput() },
                            contentAlignment = Alignment.Center,
                        ) {
                            Image(
                                painter = painterResource(R.drawable.ap_chats_hybrid_ic_send),
                                contentDescription = "Send",
                                modifier = Modifier.size(17.dp),
                            )
                        }
                    }
                }
            }
        }
    }

    // ==================== 4. BOTTOM SHEETS & DIALOGS ====================
    if (showGiftPicker) {
        UnsloppedGiftPickerSheet(
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
        UnsloppedPhotoPickerSheet(
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
                    text = "Clear chat history?",
                    color = UnsloppedChatTokens.TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                )
            },
            text = {
                Text(
                    text = "All messages in this conversation will be deleted.",
                    color = UnsloppedChatTokens.TextSecondary,
                    fontSize = 13.sp,
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    store.clearChat(userId)
                    confirmClear = false
                    bump()
                }) {
                    Text("Clear", color = UnsloppedChatTokens.AlertDanger, fontWeight = FontWeight.Medium)
                }
            },
            dismissButton = {
                TextButton(onClick = { confirmClear = false }) {
                    Text("Cancel", color = UnsloppedChatTokens.TextSecondary)
                }
            },
            containerColor = UnsloppedChatTokens.SurfaceRaised,
        )
    }

    if (confirmBlock) {
        AlertDialog(
            onDismissRequest = { confirmBlock = false },
            title = {
                Text(
                    text = "Block ${user?.name ?: userId}?",
                    color = UnsloppedChatTokens.TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                )
            },
            text = {
                Text(
                    text = "They will not be able to message or call you. You will be removed from each other's friends list.",
                    color = UnsloppedChatTokens.TextSecondary,
                    fontSize = 13.sp,
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    store.block(userId)
                    confirmBlock = false
                    nav.popBackStack()
                }) {
                    Text("Block", color = UnsloppedChatTokens.AlertDanger, fontWeight = FontWeight.Medium)
                }
            },
            dismissButton = {
                TextButton(onClick = { confirmBlock = false }) {
                    Text("Cancel", color = UnsloppedChatTokens.TextSecondary)
                }
            },
            containerColor = UnsloppedChatTokens.SurfaceRaised,
        )
    }

    if (confirmReport) {
        AlertDialog(
            onDismissRequest = { confirmReport = false },
            title = {
                Text(
                    text = "Report ${user?.name ?: userId}",
                    color = UnsloppedChatTokens.TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                )
            },
            text = {
                Column {
                    Text(
                        text = "Select a reason. The user will also be blocked.",
                        color = UnsloppedChatTokens.TextSecondary,
                        fontSize = 13.sp,
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
                                    .size(16.dp)
                                    .border(
                                        1.5.dp,
                                        if (reportReason == r) UnsloppedChatTokens.Accent else UnsloppedChatTokens.TextMuted,
                                        CircleShape,
                                    ),
                                contentAlignment = Alignment.Center,
                            ) {
                                if (reportReason == r) {
                                    Box(
                                        Modifier
                                            .size(8.dp)
                                            .background(UnsloppedChatTokens.Accent, CircleShape)
                                    )
                                }
                            }
                            Spacer(Modifier.width(10.dp))
                            Text(r, color = UnsloppedChatTokens.TextPrimary, fontSize = 13.sp)
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
                    Text("Report and block", color = UnsloppedChatTokens.AlertDanger, fontWeight = FontWeight.Medium)
                }
            },
            dismissButton = {
                TextButton(onClick = { confirmReport = false }) {
                    Text("Cancel", color = UnsloppedChatTokens.TextSecondary)
                }
            },
            containerColor = UnsloppedChatTokens.SurfaceRaised,
        )
    }
}

// ==================== UNSLOPPED MESSAGE BUBBLE ====================

@ExperimentalFoundationApi
@Composable
private fun UnsloppedMessageBubble(
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
            InitialsAvatar(
                name = partnerName,
                size = 28.dp,
                modifier = Modifier.clip(CircleShape),
            )
            Spacer(Modifier.width(8.dp))
        }

        Box {
            Box(
                modifier = Modifier
                    .background(
                        if (fromMe) UnsloppedChatTokens.OutgoingBubble else UnsloppedChatTokens.IncomingBubble,
                        RoundedCornerShape(
                            topStart = 16.dp,
                            topEnd = 16.dp,
                            bottomStart = if (fromMe) 16.dp else 4.dp,
                            bottomEnd = if (fromMe) 4.dp else 16.dp,
                        ),
                    )
                    .border(
                        1.dp,
                        UnsloppedChatTokens.Hairline,
                        RoundedCornerShape(
                            topStart = 16.dp,
                            topEnd = 16.dp,
                            bottomStart = if (fromMe) 16.dp else 4.dp,
                            bottomEnd = if (fromMe) 4.dp else 16.dp,
                        ),
                    )
                    .combinedClickable(
                        onClick = {},
                        onLongClick = { showMenu = true },
                    )
                    .padding(horizontal = 12.dp, vertical = 8.dp),
            ) {
                // Long press dropdown
                if (showMenu) {
                    DropdownMenu(
                        expanded = true,
                        onDismissRequest = { showMenu = false },
                        containerColor = UnsloppedChatTokens.SurfaceRaised,
                    ) {
                        DropdownMenuItem(
                            text = { Text("Copy", color = UnsloppedChatTokens.TextPrimary, fontSize = 13.sp) },
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
                                text = { Text("Recall message", color = UnsloppedChatTokens.TextPrimary, fontSize = 13.sp) },
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
                                text = { Text("Delete for me", color = UnsloppedChatTokens.AlertDanger, fontSize = 13.sp) },
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
                                text = "Message recalled",
                                color = UnsloppedChatTokens.TextMuted,
                                fontSize = 13.sp,
                            )
                        }

                        m.type == com.sundram.weplay.data.MsgType.GIFT -> {
                            val gift = com.sundram.weplay.data.DemoGifts.firstOrNull { it.id == m.giftId }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Image(
                                    painter = painterResource(R.drawable.ap_chats_hybrid_ic_gift),
                                    contentDescription = null,
                                    modifier = Modifier.size(24.dp),
                                )
                                Spacer(Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = gift?.name ?: "Gift",
                                        color = if (fromMe) Color.White else UnsloppedChatTokens.TextPrimary,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium,
                                    )
                                    Text(
                                        text = "${m.giftCost} coins",
                                        color = UnsloppedChatTokens.GoldCoin,
                                        fontSize = 11.sp,
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
                                        .size(width = 180.dp, height = 120.dp)
                                        .clip(RoundedCornerShape(8.dp)),
                                    contentScale = ContentScale.Crop,
                                )
                            } else {
                                Text("Photo", color = if (fromMe) Color.White else UnsloppedChatTokens.TextPrimary, fontSize = 13.sp)
                            }
                        }

                        m.type == com.sundram.weplay.data.MsgType.VOICE && m.mediaPath != null -> {
                            UnsloppedVoiceNoteRow(m = m, fromMe = fromMe)
                        }

                        else -> {
                            Text(
                                text = m.text,
                                color = if (fromMe) Color.White else UnsloppedChatTokens.TextPrimary,
                                fontSize = 14.sp,
                                lineHeight = 20.sp,
                            )
                        }
                    }

                    Spacer(Modifier.height(2.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.End,
                        modifier = Modifier.align(Alignment.End),
                    ) {
                        Text(
                            text = formatMessageTime(m.ts),
                            color = if (fromMe) Color.White.copy(alpha = 0.7f) else UnsloppedChatTokens.TextMuted,
                            fontSize = 11.sp,
                        )
                        if (fromMe) {
                            Spacer(Modifier.width(4.dp))
                            Image(
                                painter = painterResource(
                                    if (m.read) R.drawable.ap_chats_hybrid_ic_read else R.drawable.ap_chats_hybrid_ic_check
                                ),
                                contentDescription = if (m.read) "Read" else "Sent",
                                modifier = Modifier.size(12.dp),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun UnsloppedVoiceNoteRow(m: ChatMessage, fromMe: Boolean) {
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
                .size(32.dp)
                .background(
                    if (fromMe) Color.White.copy(alpha = 0.2f) else UnsloppedChatTokens.SurfaceRaised,
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
                tint = if (fromMe) Color.White else UnsloppedChatTokens.TextPrimary,
                modifier = Modifier.size(14.dp),
            )
        }
        Spacer(Modifier.width(8.dp))
        Text(
            text = "${(m.durationMs / 1000).toInt()}s",
            color = if (fromMe) Color.White else UnsloppedChatTokens.TextPrimary,
            fontSize = 12.sp,
        )
    }
}

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
private fun UnsloppedGiftPickerSheet(
    balance: Long,
    onDismiss: () -> Unit,
    onSelect: (com.sundram.weplay.data.GiftItem) -> Unit,
) {
    androidx.compose.material3.ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = UnsloppedChatTokens.SurfaceRaised,
        scrimColor = Color.Black.copy(alpha = 0.6f),
    ) {
        Column(Modifier.padding(horizontal = 20.dp, vertical = 12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Send gift",
                    color = UnsloppedChatTokens.TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                )
                Spacer(Modifier.weight(1f))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(UnsloppedChatTokens.Surface, RoundedCornerShape(8.dp))
                        .border(1.dp, UnsloppedChatTokens.Hairline, RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_coin),
                        contentDescription = null,
                        tint = UnsloppedChatTokens.GoldCoin,
                        modifier = Modifier.size(14.dp),
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = "$balance",
                        color = UnsloppedChatTokens.GoldCoin,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            var needCoinsNotice by remember { mutableStateOf<String?>(null) }
            androidx.compose.foundation.lazy.grid.LazyVerticalGrid(
                columns = androidx.compose.foundation.lazy.grid.GridCells.Fixed(3),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(com.sundram.weplay.data.DemoGifts.size) { idx ->
                    val gift = com.sundram.weplay.data.DemoGifts[idx]
                    val afford = balance >= gift.cost
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .background(UnsloppedChatTokens.Surface, RoundedCornerShape(10.dp))
                            .border(1.dp, UnsloppedChatTokens.Hairline, RoundedCornerShape(10.dp))
                            .clickable {
                                if (afford) {
                                    needCoinsNotice = null
                                    onSelect(gift)
                                } else {
                                    needCoinsNotice = "You need ${gift.cost - balance} more coins for ${gift.name}."
                                }
                            }
                            .padding(12.dp),
                    ) {
                        Image(
                            painter = painterResource(R.drawable.ap_chats_hybrid_ic_gift),
                            contentDescription = null,
                            modifier = Modifier.size(32.dp),
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = gift.name,
                            color = if (afford) UnsloppedChatTokens.TextPrimary else UnsloppedChatTokens.TextMuted,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Normal,
                            maxLines = 1,
                        )
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = "${gift.cost} coins",
                            color = if (afford) UnsloppedChatTokens.GoldCoin else UnsloppedChatTokens.TextMuted,
                            fontSize = 11.sp,
                        )
                    }
                }
            }
            needCoinsNotice?.let { notice ->
                Spacer(Modifier.height(10.dp))
                Text(
                    text = notice,
                    color = UnsloppedChatTokens.AlertDanger,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                )
            }

            Spacer(Modifier.height(20.dp))
        }
    }
}

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
private fun UnsloppedPhotoPickerSheet(onDismiss: () -> Unit, onPick: (String) -> Unit) {
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
        containerColor = UnsloppedChatTokens.SurfaceRaised,
        scrimColor = Color.Black.copy(alpha = 0.6f),
    ) {
        LaunchedEffect(Unit) {
            launcher.launch("image/*")
        }
        Box(
            modifier = Modifier.fillMaxWidth().padding(24.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "Opening media picker",
                color = UnsloppedChatTokens.TextSecondary,
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
        1 -> Brush.verticalGradient(listOf(b.copy(alpha = 0.15f), Color.Transparent))
        2 -> Brush.verticalGradient(listOf(p.copy(alpha = 0.15f), Color.Transparent))
        3 -> Brush.verticalGradient(listOf(m.copy(alpha = 0.15f), Color.Transparent))
        else -> null
    }
}
