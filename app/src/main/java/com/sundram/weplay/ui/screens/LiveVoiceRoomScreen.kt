package com.sundram.weplay.ui.screens

import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.sundram.weplay.R
import com.sundram.weplay.data.Demo
import com.sundram.weplay.data.UserPrefs
import com.sundram.weplay.data.UserProfile
import com.sundram.weplay.data.getAvatarById
import com.sundram.weplay.ui.components.InitialsAvatar
import com.sundram.weplay.ui.components.bounceClick
import com.sundram.weplay.ui.components.navigateSafe
import com.sundram.weplay.ui.nav.Routes
import com.sundram.weplay.ui.theme.AuroraTurquoise
import com.sundram.weplay.ui.theme.CelestialViolet
import com.sundram.weplay.ui.theme.ChampagneGold
import com.sundram.weplay.ui.theme.CyanBrand
import com.sundram.weplay.ui.theme.DeepMidnight
import com.sundram.weplay.ui.theme.DreamLavender
import com.sundram.weplay.ui.theme.SoftPeach
import com.sundram.weplay.ui.theme.VyboraBorder
import com.sundram.weplay.ui.theme.VyboraCyan
import com.sundram.weplay.ui.theme.VyboraGold
import com.sundram.weplay.ui.theme.VyboraLavender
import com.sundram.weplay.ui.theme.VyboraPurple
import com.sundram.weplay.ui.theme.VyboraSurface
import com.sundram.weplay.ui.theme.VyboraTextPrimary
import com.sundram.weplay.ui.theme.VyboraTextSecondary
import com.sundram.weplay.voiceroom.HOST_SEAT_INDEX
import com.sundram.weplay.voiceroom.PARTNER_SEAT_INDEX
import com.sundram.weplay.voiceroom.REGULAR_SEAT_START
import com.sundram.weplay.voiceroom.REGULAR_SEAT_END
import com.sundram.weplay.voiceroom.RoomConnection
import com.sundram.weplay.voiceroom.RoomMemberVo
import com.sundram.weplay.voiceroom.RoomMessage
import com.sundram.weplay.voiceroom.SeatBond
import com.sundram.weplay.voiceroom.VOICE_ROOM_SEAT_COUNT
import com.sundram.weplay.voiceroom.VoiceRoomState
import com.sundram.weplay.voiceroom.VoiceRoomViewModel
import com.sundram.weplay.voiceroom.theme.CLASSIC_LOUNGE_ID
import com.sundram.weplay.voiceroom.theme.RoomThemePalette
import com.sundram.weplay.voiceroom.theme.RoomThemeRenderer
import com.sundram.weplay.voiceroom.theme.RoomThemeResources
import com.sundram.weplay.voiceroom.theme.RoomThemeServiceProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// ─────────────────────────────────────────────────────────────────────────────
//  Color Palette — AURORA PARADISE 2.0 (Celestial Lounge Atmosphere)
// ─────────────────────────────────────────────────────────────────────────────
private val RoomBgColor       = DeepMidnight
private val RoomSurfaceColor  = Color(0xFF261A42)
private val RoomSurfaceSubtle = Color(0xFF1C1333)
private val GlowCyan          = AuroraTurquoise
private val GlowPurple        = CelestialViolet
private val VyboraGold        = ChampagneGold
private val ChatPanelBg       = Color(0x66100826)
private val InputBarBg        = Color(0xDD120C24)
private val LiveRedBadge      = Color(0xFFFF334B)

/**
 * Aurora Lounge 2.0: Complete Joined Voice Room Screen with native Compose
 * layout, original WebP artwork, responsive stage, real mic permissions,
 * and comprehensive interactive modals.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiveVoiceRoomScreen(
    nav: NavHostController,
    roomId: String?,
    profile: UserProfile,
) {
    val id = roomId?.takeIf { it.isNotBlank() } ?: Demo.rooms.first().id
    val context = LocalContext.current
    val prefs = remember(context) { UserPrefs(context) }
    val themeRepo = remember(context) { RoomThemeServiceProvider.repository(context) }
    val crestStore = remember(context) { RoomThemeServiceProvider.crestStore(context) }
    val createdRoom = remember(id) { themeRepo.getCreatedRooms().firstOrNull { it.id == id } }
    val demoRoom = remember(id) { Demo.rooms.firstOrNull { it.id == id } ?: Demo.rooms.first() }
    val displayTitle = createdRoom?.title ?: demoRoom.title
    val coverRes = if (createdRoom != null) R.drawable.room_thumb_classic_lounge else (demoRoom.coverRes ?: R.drawable.room_thumb_classic_lounge)

    val vm: VoiceRoomViewModel = viewModel(
        factory = VoiceRoomViewModel.factory(
            context = context,
            roomId = id,
            identity = profile.id,
            displayName = profile.name,
            roomTitle = displayTitle,
        ),
    )
    val state by vm.state.collectAsState()
    val repoRevision by themeRepo.revision.collectAsState()
    var resumeTick by remember { mutableIntStateOf(0) }
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                resumeTick++
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    val activeThemeId = remember(id, repoRevision, resumeTick) {
        themeRepo.getEquippedThemeId(id)
    }
    val crestProgress = remember(id, repoRevision, resumeTick) {
        crestStore.load(id)
    }
    val defaultAnnouncement = createdRoom?.announcement
        ?: "Welcome to $displayTitle! Keep chat friendly and enjoy the conversation."
    val activeAnnouncement = remember(id, repoRevision, resumeTick) {
        themeRepo.getAnnouncement(id, defaultAnnouncement)
    }

    LaunchedEffect(id, profile.photoPath, profile.avatarId, profile.frame) {
        prefs.recordRoomVisit(id)
        vm.setFavorited(prefs.isRoomFollowed(id))
        val avatarRes = getAvatarById(profile.avatarId).drawableRes
        val frameRes = when (profile.frame?.lowercase()) {
            "phoenix" -> R.drawable.vb_vr_frame_vip
            else -> R.drawable.vb_vr_frame_speaker
        }
        vm.setSelfProfileVisuals(profile.photoPath, avatarRes, frameRes)
    }
    LaunchedEffect(activeThemeId, activeAnnouncement) {
        vm.setRoomThemeId(activeThemeId)
        vm.setAnnouncement(activeAnnouncement)
    }

    // ── Dialog & Bottom Sheet States ──
    var showExitDialog by rememberSaveable { mutableStateOf(false) }
    var showGiftSheet by rememberSaveable { mutableStateOf(false) }
    var showParticipantsSheet by rememberSaveable { mutableStateOf(false) }
    var showAudioSettingsSheet by rememberSaveable { mutableStateOf(false) }
    var showMoreSheet by rememberSaveable { mutableStateOf(false) }
    var showPkSheet by rememberSaveable { mutableStateOf(false) }
    var showGamesSheet by rememberSaveable { mutableStateOf(false) }
    var showAttachSeatsSheet by rememberSaveable { mutableStateOf(false) }
    var showAnnouncementDialog by rememberSaveable { mutableStateOf(false) }
    var showSeatManagerSheet by rememberSaveable { mutableStateOf(false) }
    var selectedBondForDetails by remember { mutableStateOf<SeatBond?>(null) }
    var selectedMemberForProfile by remember { mutableStateOf<RoomMemberVo?>(null) }
    var activeGiftAnimation by remember { mutableStateOf<GiftAnimationData?>(null) }
    var reportTargetLabel by remember { mutableStateOf<String?>(null) }

    // ── Microphone Runtime Permission ──
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { granted ->
        if (granted) {
            vm.onMicrophonePermissionResult(true)
        } else {
            Toast.makeText(context, "Microphone permission is required to talk.", Toast.LENGTH_SHORT).show()
        }
    }

    // ── Seat Interaction Logic ──
    val onSeatClick: (Int) -> Unit = { seatIndex ->
        val occupant = state.seatAt(seatIndex)
        val seatLabel = when (seatIndex) {
            HOST_SEAT_INDEX -> "Host seat"
            PARTNER_SEAT_INDEX -> "Partner seat"
            else -> "Seat $seatIndex"
        }
        when {
            occupant == null -> {
                if (state.isSeatLocked(seatIndex)) {
                    Toast.makeText(context, "$seatLabel is locked by host.", Toast.LENGTH_SHORT).show()
                } else {
                    vm.takeSeat(seatIndex)
                }
            }
            occupant.isSelf -> {
                vm.leaveSeat()
            }
            else -> {
                selectedMemberForProfile = occupant
            }
        }
    }

    // ── Active Gift Animation Timer ──
    LaunchedEffect(activeGiftAnimation) {
        if (activeGiftAnimation != null) {
            delay(2500)
            activeGiftAnimation = null
        }
    }

    // ── Hardware & Gesture Back Interception ──
    BackHandler {
        when {
            showExitDialog -> showExitDialog = false
            showAnnouncementDialog -> showAnnouncementDialog = false
            showSeatManagerSheet -> showSeatManagerSheet = false
            reportTargetLabel != null -> reportTargetLabel = null
            selectedMemberForProfile != null -> selectedMemberForProfile = null
            showParticipantsSheet -> showParticipantsSheet = false
            showAudioSettingsSheet -> showAudioSettingsSheet = false
            showGiftSheet -> showGiftSheet = false
            showPkSheet -> showPkSheet = false
            showGamesSheet -> showGamesSheet = false
            showMoreSheet -> showMoreSheet = false
            showAttachSeatsSheet -> showAttachSeatsSheet = false
            selectedBondForDetails != null -> selectedBondForDetails = null
            activeGiftAnimation != null -> activeGiftAnimation = null
            else -> showExitDialog = true
        }
    }

    val isHostOrMod = remember(state.self, createdRoom) {
        val self = state.self
        self?.isHost == true || self?.role == "moderator" || createdRoom != null
    }

    RoomThemeRenderer(
        themeId = activeThemeId,
        modifier = Modifier.fillMaxSize(),
    ) { _, themeRes, palette ->
        // ── Main Screen Architecture ──
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding(),
        ) {
            // A. Compact Header with Live Room Crest
            AuroraRoomHeader(
                roomTitle = displayTitle,
                roomId = id,
                coverRes = coverRes,
                crestLevel = crestProgress.level,
                palette = palette,
                state = state,
                onBack = { showExitDialog = true },
                onMinimize = {
                    vm.minimizeRoom()
                    nav.popBackStack()
                },
                onCrestClick = { nav.navigateSafe(Routes.roomCrestRoute(id)) },
                onFavoriteToggle = {
                    val nextFav = !state.isFavorited
                    prefs.followRoom(id, nextFav)
                    vm.toggleFavorite()
                    Toast.makeText(
                        context,
                        if (nextFav) "Added to Followed rooms" else "Removed from Followed rooms",
                        Toast.LENGTH_SHORT,
                    ).show()
                },
                onParticipantsClick = { showParticipantsSheet = true },
                onShareClick = {
                    val sendIntent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, "Join my voice room \"$displayTitle\" (Room ID: $id)")
                        type = "text/plain"
                    }
                    context.startActivity(Intent.createChooser(sendIntent, "Share Voice Room"))
                },
                onMoreClick = { showMoreSheet = true },
            )

            // B. Compact Connection Status Bar & Announcement Strip
            RoomConnectionStatusPill(state = state, palette = palette, onRetry = vm::reconnect)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 3.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(palette.cardSurface)
                    .border(0.8.dp, palette.seatBorder.copy(alpha = 0.55f), RoundedCornerShape(10.dp))
                    .clickable(enabled = isHostOrMod) { showAnnouncementDialog = true }
                    .padding(horizontal = 10.dp, vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("Notice:", color = palette.accent, fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.width(6.dp))
                Text(
                    text = activeAnnouncement,
                    color = palette.secondaryText,
                    fontSize = 10.5.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )
                if (isHostOrMod) {
                    Text("Edit", color = palette.accent, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            // C. Interactive Speaker Stage (1 Host + 8 Regular + Optional Partner)
            AuroraSpeakerStage(
                state = state,
                palette = palette,
                themeRes = themeRes,
                onSeatClick = onSeatClick,
                onHostProfileClick = {
                    val hostMember = state.seatAt(HOST_SEAT_INDEX)
                    if (hostMember != null) selectedMemberForProfile = hostMember
                    else onSeatClick(HOST_SEAT_INDEX)
                },
                onBondDetailsClick = { bond ->
                    selectedBondForDetails = bond
                },
            )

            Spacer(Modifier.height(6.dp))

            // D. Quick Activities Entry Bar (PK, Games, Dice)
            QuickActivitiesBar(
                palette = palette,
                onPkClick = { showPkSheet = true },
                onGamesClick = { showGamesSheet = true },
                onDiceRoll = {
                    val roll = (1..6).random()
                    vm.sendMessage("rolled a $roll on the room dice.")
                    Toast.makeText(context, "Rolled $roll", Toast.LENGTH_SHORT).show()
                },
            )

            Spacer(Modifier.height(6.dp))

            // E. Translucent Compact Chat Panel
            AuroraChatPanel(
                state = state,
                palette = palette,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
            )

            // F. Fixed Bottom Action Dock
            AuroraInputDock(
                state = state,
                palette = palette,
                onToggleMic = {
                    vm.toggleMicrophone {
                        if (vm.usesLiveServer) {
                            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                        }
                    }
                },
                onSend = { text -> vm.sendMessage(text) },
                onGiftClick = { showGiftSheet = true },
                onAudioSettingsClick = { showAudioSettingsSheet = true },
            )
        }

        // ── Celebratory Floating Gift Animation Overlay ──
        activeGiftAnimation?.let { data ->
            GiftCelebrationOverlay(data = data, onDismiss = { activeGiftAnimation = null })
        }

        // 1. Minimize / Exit Confirmation Dialog
        if (showExitDialog) {
            AlertDialog(
                onDismissRequest = { showExitDialog = false },
                title = { Text("Leave Voice Room?", fontWeight = FontWeight.Bold) },
                text = { Text("Minimize to keep listening in the background, or leave to disconnect audio cleanly.") },
                confirmButton = {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        TextButton(onClick = {
                            showExitDialog = false
                            vm.minimizeRoom()
                            nav.popBackStack()
                        }) {
                            Text("Minimize", color = Color(0xFF9B82E9), fontWeight = FontWeight.Bold)
                        }
                        TextButton(onClick = {
                            showExitDialog = false
                            vm.disconnect()
                            nav.popBackStack()
                        }) {
                            Text("Leave", color = LiveRedBadge, fontWeight = FontWeight.Bold)
                        }
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showExitDialog = false }) {
                        Text("Stay", color = Color(0xFFD0C8E8), fontWeight = FontWeight.SemiBold)
                    }
                },
                containerColor = RoomSurfaceColor,
                titleContentColor = Color.White,
                textContentColor = Color(0xFFD0C8E8),
            )
        }

        // 2. Edit Announcement Dialog
        if (showAnnouncementDialog) {
            var draftNotice by remember(activeAnnouncement) { mutableStateOf(activeAnnouncement) }
            AlertDialog(
                onDismissRequest = { showAnnouncementDialog = false },
                title = { Text("Room Announcement", fontWeight = FontWeight.Bold) },
                text = {
                    OutlinedTextField(
                        value = draftNotice,
                        onValueChange = { if (it.length <= 140) draftNotice = it },
                        placeholder = { Text("Welcome guests, rules, or schedule...") },
                        singleLine = false,
                        maxLines = 3,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFF9B82E9),
                            unfocusedBorderColor = Color(0xFF473E63),
                        ),
                    )
                },
                confirmButton = {
                    TextButton(onClick = {
                        val cleaned = draftNotice.trim()
                        if (cleaned.isNotEmpty()) {
                            themeRepo.updateAnnouncement(id, cleaned, isHostOrMod)
                            vm.setAnnouncement(cleaned)
                        }
                        showAnnouncementDialog = false
                    }) {
                        Text("Save", color = Color(0xFF42D6A0), fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAnnouncementDialog = false }) {
                        Text("Cancel", color = Color(0xFFD0C8E8))
                    }
                },
                containerColor = RoomSurfaceColor,
                titleContentColor = Color.White,
                textContentColor = Color(0xFFD0C8E8),
            )
        }

        // Report Confirmation Dialog
        reportTargetLabel?.let { target ->
            AlertDialog(
                onDismissRequest = { reportTargetLabel = null },
                title = { Text("Report $target", fontWeight = FontWeight.Bold) },
                text = { Text("Submit a moderation safety report for \"$target\"? Abuse reports are saved in local review history.") },
                confirmButton = {
                    TextButton(onClick = {
                        reportTargetLabel = null
                        Toast.makeText(context, "Report recorded for review.", Toast.LENGTH_SHORT).show()
                    }) {
                        Text("Submit Report", color = LiveRedBadge, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { reportTargetLabel = null }) {
                        Text("Cancel", color = Color(0xFFD0C8E8))
                    }
                },
                containerColor = RoomSurfaceColor,
                titleContentColor = Color.White,
                textContentColor = Color(0xFFD0C8E8),
            )
        }

        // 2b. User Profile Sheet
        selectedMemberForProfile?.let { member ->
            val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
            ModalBottomSheet(
                onDismissRequest = { selectedMemberForProfile = null },
                sheetState = sheetState,
                containerColor = RoomSurfaceColor,
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            ) {
                UserProfileSheet(
                    member = member,
                    state = state,
                    isHostUser = isHostOrMod,
                    onDismiss = { selectedMemberForProfile = null },
                    onFollowToggle = { vm.toggleFollowMember(member.identity) },
                    onSendGift = {
                        selectedMemberForProfile = null
                        showGiftSheet = true
                    },
                    onDirectMessage = {
                        selectedMemberForProfile = null
                        vm.minimizeRoom()
                        nav.navigateSafe(Routes.chatRoute(member.identity))
                    },
                    onLocalMuteToggle = {
                        val currentlyMuted = member.identity in state.locallyMutedIdentities
                        vm.setLocalMuteMember(member.identity, !currentlyMuted)
                    },
                    onModeratorMute = {
                        member.seat?.let { seatIdx -> vm.moderatorMuteSeat(seatIdx, member.micOn) }
                    },
                    onMoveSeat = { targetSeat ->
                        vm.moveMemberSeat(member.identity, targetSeat)
                        selectedMemberForProfile = null
                    },
                    onToggleModerator = {
                        vm.toggleModerator(member.identity)
                    },
                    onViewFullProfile = {
                        selectedMemberForProfile = null
                        vm.minimizeRoom()
                        nav.navigateSafe(Routes.userDetailRoute(member.identity))
                    },
                    onKickSeat = {
                        member.seat?.let { vm.kickSeat(it) }
                        selectedMemberForProfile = null
                    },
                    onReport = {
                        selectedMemberForProfile = null
                        reportTargetLabel = member.name
                    },
                    onBlock = {
                        vm.blockMember(member.identity)
                        selectedMemberForProfile = null
                    },
                )
            }
        }

        // 3. Participants List Sheet
        if (showParticipantsSheet) {
            val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
            ModalBottomSheet(
                onDismissRequest = { showParticipantsSheet = false },
                sheetState = sheetState,
                containerColor = RoomSurfaceColor,
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            ) {
                ParticipantsSheet(
                    state = state,
                    isHostOrMod = isHostOrMod,
                    onMemberClick = { member ->
                        showParticipantsSheet = false
                        selectedMemberForProfile = member
                    },
                    onInviteToSeat = { member ->
                        val freeSeat = (1..8).firstOrNull { state.seatAt(it) == null && !state.isSeatLocked(it) }
                        if (freeSeat != null) {
                            vm.inviteToSeat(member.identity, freeSeat)
                        } else {
                            Toast.makeText(context, "No open seats available.", Toast.LENGTH_SHORT).show()
                        }
                    },
                    onDismiss = { showParticipantsSheet = false },
                )
            }
        }

        // 4. Audio Settings Sheet
        if (showAudioSettingsSheet) {
            val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
            ModalBottomSheet(
                onDismissRequest = { showAudioSettingsSheet = false },
                sheetState = sheetState,
                containerColor = RoomSurfaceColor,
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            ) {
                AudioSettingsSheet(
                    state = state,
                    onToggleMic = {
                        vm.toggleMicrophone {
                            if (vm.usesLiveServer) {
                                permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                            }
                        }
                    },
                    onToggleEarpiece = { vm.toggleEarpieceMode() },
                    onToggleOutputMute = { vm.toggleOutputMute() },
                    onDismiss = { showAudioSettingsSheet = false },
                )
            }
        }

        // 5. Gifting Sheet
        if (showGiftSheet) {
            val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
            ModalBottomSheet(
                onDismissRequest = { showGiftSheet = false },
                sheetState = sheetState,
                containerColor = RoomSurfaceColor,
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            ) {
                AuroraGiftSheet(
                    state = state,
                    userCoinBalance = profile.coins,
                    onSendGift = { gift, recipient, qty ->
                        showGiftSheet = false
                        vm.sendGift(gift.name, recipient, qty, gift.iconRes)
                        activeGiftAnimation = GiftAnimationData(
                            giftName = gift.name,
                            iconRes = gift.iconRes,
                            recipientName = recipient,
                            count = qty,
                        )
                    },
                    onDismiss = { showGiftSheet = false },
                )
            }
        }

        // 6. PK Battle Sheet
        if (showPkSheet) {
            val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
            ModalBottomSheet(
                onDismissRequest = { showPkSheet = false },
                sheetState = sheetState,
                containerColor = RoomSurfaceColor,
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            ) {
                PkBattleSheet(
                    onStartBattle = { mode ->
                        showPkSheet = false
                        vm.sendMessage("Started a $mode Battle on stage.")
                    },
                    onDismiss = { showPkSheet = false },
                )
            }
        }

        // 7. In-Room Games Sheet (Truth or Dare, Poll + Compatible Mini-Games keeping Voice Live)
        if (showGamesSheet) {
            val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
            ModalBottomSheet(
                onDismissRequest = { showGamesSheet = false },
                sheetState = sheetState,
                containerColor = RoomSurfaceColor,
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            ) {
                InRoomGamesSheet(
                    onPostGameAction = { line -> vm.sendMessage(line) },
                    onLaunchLudo = {
                        showGamesSheet = false
                        vm.minimizeRoom()
                        nav.navigateSafe(Routes.LUDO)
                    },
                    onLaunchSpy = {
                        showGamesSheet = false
                        vm.minimizeRoom()
                        nav.navigateSafe(Routes.SPY)
                    },
                    onDismiss = { showGamesSheet = false },
                )
            }
        }

        // 8. Room Actions / More Sheet
        if (showMoreSheet) {
            val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
            ModalBottomSheet(
                onDismissRequest = { showMoreSheet = false },
                sheetState = sheetState,
                containerColor = RoomSurfaceColor,
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            ) {
                RoomMoreActionsSheet(
                    state = state,
                    isHostOrMod = isHostOrMod,
                    onOpenThemes = {
                        showMoreSheet = false
                        nav.navigateSafe(Routes.roomThemesRoute(id))
                    },
                    onOpenCrest = {
                        showMoreSheet = false
                        nav.navigateSafe(Routes.roomCrestRoute(id))
                    },
                    onEditAnnouncement = {
                        showMoreSheet = false
                        showAnnouncementDialog = true
                    },
                    onOpenParticipants = {
                        showMoreSheet = false
                        showParticipantsSheet = true
                    },
                    onOpenAudioSettings = {
                        showMoreSheet = false
                        showAudioSettingsSheet = true
                    },
                    onOpenSeatManager = {
                        showMoreSheet = false
                        showSeatManagerSheet = true
                    },
                    onOpenPk = {
                        showMoreSheet = false
                        showPkSheet = true
                    },
                    onOpenGames = {
                        showMoreSheet = false
                        showGamesSheet = true
                    },
                    onTogglePartnerSeat = {
                        vm.togglePartnerSeat(!state.partnerEnabled)
                    },
                    onOpenAttachSeats = {
                        showMoreSheet = false
                        showAttachSeatsSheet = true
                    },
                    onInviteFriends = {
                        showMoreSheet = false
                        val sendIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, "Join my voice room \"$displayTitle\" (ID: $id)")
                            type = "text/plain"
                        }
                        context.startActivity(Intent.createChooser(sendIntent, "Invite Friends"))
                    },
                    onReportRoom = {
                        showMoreSheet = false
                        reportTargetLabel = displayTitle
                    },
                    onCloseRoom = {
                        showMoreSheet = false
                        showExitDialog = true
                    },
                    onDismiss = { showMoreSheet = false },
                )
            }
        }

        // Seat Management Sheet (lock/unlock 1..8 & Partner seat)
        if (showSeatManagerSheet) {
            val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
            ModalBottomSheet(
                onDismissRequest = { showSeatManagerSheet = false },
                sheetState = sheetState,
                containerColor = RoomSurfaceColor,
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            ) {
                SeatManagementSheet(
                    state = state,
                    onToggleLock = { seat -> vm.toggleLockSeat(seat) },
                    onDismiss = { showSeatManagerSheet = false },
                )
            }
        }

        // 9. Mutual Consent Bond Request Dialog
        state.pendingBondConsent?.let { pending ->
            AlertDialog(
                onDismissRequest = { vm.respondSeatBondConsent(pending.bondId, false) },
                title = { Text("Attached Seat Request", fontWeight = FontWeight.Bold) },
                text = {
                    Text("Accept Attached Seat link with Seat ${pending.firstSeat}?")
                },
                confirmButton = {
                    TextButton(onClick = { vm.respondSeatBondConsent(pending.bondId, true) }) {
                        Text("Accept", color = GlowCyan, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { vm.respondSeatBondConsent(pending.bondId, false) }) {
                        Text("Decline", color = Color(0xFFD0C8E8), fontWeight = FontWeight.SemiBold)
                    }
                },
                containerColor = RoomSurfaceColor,
                titleContentColor = Color.White,
                textContentColor = Color(0xFFD0C8E8),
            )
        }

        // 10. Attach Seats Selection Bottom Sheet (Host Only)
        if (showAttachSeatsSheet) {
            val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
            ModalBottomSheet(
                onDismissRequest = { showAttachSeatsSheet = false },
                sheetState = sheetState,
                containerColor = RoomSurfaceColor,
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            ) {
                AttachSeatsBottomSheet(
                    state = state,
                    onRequestBond = { seatA, seatB ->
                        showAttachSeatsSheet = false
                        vm.requestSeatBond(seatA, seatB)
                    },
                    onDismiss = { showAttachSeatsSheet = false },
                )
            }
        }

        // 11. Bond Details Bottom Sheet
        selectedBondForDetails?.let { bond ->
            val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
            ModalBottomSheet(
                onDismissRequest = { selectedBondForDetails = null },
                sheetState = sheetState,
                containerColor = RoomSurfaceColor,
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            ) {
                BondDetailsBottomSheet(
                    bond = bond,
                    state = state,
                    onDetach = {
                        selectedBondForDetails = null
                        vm.detachSeatBond(bond.bondId)
                    },
                    onDismiss = { selectedBondForDetails = null },
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  1. Shared Real User Avatar Renderer (PhotoPath -> DrawableRes -> Initials Fallback)
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun RoomMemberAvatarImage(
    name: String,
    photoPath: String?,
    avatarRes: Int?,
    size: Dp,
) {
    val photoState = remember(photoPath) { mutableStateOf<ImageBitmap?>(null) }
    LaunchedEffect(photoPath) {
        photoState.value = if (!photoPath.isNullOrBlank()) {
            withContext(Dispatchers.IO) {
                runCatching { BitmapFactory.decodeFile(photoPath)?.asImageBitmap() }.getOrNull()
            }
        } else null
    }
    val decoded = photoState.value
    when {
        decoded != null -> Image(
            bitmap = decoded,
            contentDescription = name,
            modifier = Modifier
                .size(size)
                .clip(CircleShape),
            contentScale = ContentScale.Crop,
        )
        avatarRes != null -> Image(
            painter = painterResource(avatarRes),
            contentDescription = name,
            modifier = Modifier
                .size(size)
                .clip(CircleShape),
            contentScale = ContentScale.Crop,
        )
        else -> InitialsAvatar(name = name, size = size)
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  2. Compact Room Header
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun AuroraRoomHeader(
    roomTitle: String,
    roomId: String,
    coverRes: Int,
    crestLevel: Int,
    palette: RoomThemePalette,
    state: VoiceRoomState,
    onBack: () -> Unit,
    onMinimize: () -> Unit,
    onCrestClick: () -> Unit,
    onFavoriteToggle: () -> Unit,
    onParticipantsClick: () -> Unit,
    onShareClick: () -> Unit,
    onMoreClick: () -> Unit,
) {
    val context = LocalContext.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Minimize / Keep Listening Button
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(palette.headerSurface)
                .border(1.dp, palette.seatBorder, CircleShape)
                .bounceClick(onClick = onMinimize),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_back),
                contentDescription = "Minimize room",
                tint = palette.primaryText,
                modifier = Modifier.size(15.dp),
            )
        }

        Spacer(Modifier.width(8.dp))

        // Room Thumbnail
        Image(
            painter = painterResource(coverRes),
            contentDescription = roomTitle,
            modifier = Modifier
                .size(38.dp)
                .clip(RoundedCornerShape(10.dp))
                .border(1.dp, palette.seatBorder, RoundedCornerShape(10.dp)),
            contentScale = ContentScale.Crop,
        )

        Spacer(Modifier.width(8.dp))

        // Title + Dynamic Room Crest + ID with copy shortcut
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = roomTitle,
                color = palette.primaryText,
                fontSize = 14.5.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                // Dynamic Room Crest Badge (opens Room Crest Center)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(5.dp))
                        .background(Color(0x33FFD36B))
                        .border(0.6.dp, ChampagneGold.copy(alpha = 0.55f), RoundedCornerShape(5.dp))
                        .clickable(onClick = onCrestClick)
                        .padding(horizontal = 5.dp, vertical = 1.5.dp),
                ) {
                    Text(
                        text = "Crest Lv.$crestLevel",
                        color = ChampagneGold,
                        fontSize = 8.5.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        clipboard.setPrimaryClip(ClipData.newPlainText("Room ID", roomId))
                        Toast.makeText(context, "Room ID copied: $roomId", Toast.LENGTH_SHORT).show()
                    },
                ) {
                    Text(
                        text = "ID: ${roomId.take(8).uppercase()}",
                        color = palette.secondaryText,
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.Medium,
                    )
                    Spacer(Modifier.width(3.dp))
                    Image(
                        painter = painterResource(R.drawable.vb_vr_ic_copy),
                        contentDescription = "Copy ID",
                        modifier = Modifier.size(10.dp),
                    )
                }
            }
        }

        // Favorite / Follow Room Toggle
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(palette.headerSurface)
                .border(1.dp, palette.seatBorder, CircleShape)
                .bounceClick(onClick = onFavoriteToggle),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(
                    if (state.isFavorited) R.drawable.vb_vr_ic_heart_fill else R.drawable.vb_vr_ic_heart
                ),
                contentDescription = "Favorite room",
                modifier = Modifier.size(17.dp),
            )
        }

        Spacer(Modifier.width(5.dp))

        // Participants Pill Button
        Row(
            modifier = Modifier
                .height(34.dp)
                .clip(RoundedCornerShape(17.dp))
                .background(palette.headerSurface)
                .border(1.dp, palette.seatBorder, RoundedCornerShape(17.dp))
                .bounceClick(onClick = onParticipantsClick)
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                painter = painterResource(R.drawable.vb_vr_ic_users_cyan),
                contentDescription = null,
                modifier = Modifier.size(13.dp),
            )
            Spacer(Modifier.width(4.dp))
            Text(
                text = "${state.members.size}",
                color = palette.primaryText,
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Bold,
            )
        }

        Spacer(Modifier.width(5.dp))

        // Share Button
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(palette.headerSurface)
                .border(1.dp, palette.seatBorder, CircleShape)
                .bounceClick(onClick = onShareClick),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(R.drawable.vb_vr_ic_share),
                contentDescription = "Share",
                modifier = Modifier.size(15.dp),
            )
        }

        Spacer(Modifier.width(5.dp))

        // More Menu Button
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(palette.headerSurface)
                .border(1.dp, palette.seatBorder, CircleShape)
                .bounceClick(onClick = onMoreClick),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(R.drawable.vb_vr_ic_more),
                contentDescription = "More actions",
                modifier = Modifier.size(15.dp),
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  3. Compact Connection Status Bar
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun RoomConnectionStatusPill(
    state: VoiceRoomState,
    palette: RoomThemePalette,
    onRetry: () -> Unit,
) {
    val isConnected = state.connection == RoomConnection.CONNECTED || state.connection == RoomConnection.SIMULATED
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 2.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(palette.cardSurface)
                .border(1.dp, if (isConnected) palette.seatBorder else LiveRedBadge.copy(alpha = 0.55f), RoundedCornerShape(12.dp))
                .padding(horizontal = 10.dp, vertical = 3.dp),
            contentAlignment = Alignment.Center,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(if (isConnected) palette.activeMic else LiveRedBadge),
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = state.transportLabel,
                    color = palette.secondaryText,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                )
                if (state.error != null) {
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = "• Retry",
                        color = palette.accent,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable(onClick = onRetry),
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  4. Interactive Aurora Speaker Stage
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun AuroraSpeakerStage(
    state: VoiceRoomState,
    palette: RoomThemePalette,
    themeRes: RoomThemeResources,
    onSeatClick: (Int) -> Unit,
    onHostProfileClick: () -> Unit,
    onBondDetailsClick: (SeatBond) -> Unit,
) {
    var stageCoords by remember { mutableStateOf<LayoutCoordinates?>(null) }
    val seatCenters = remember { mutableStateMapOf<Int, Offset>() }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 4.dp)
            .onGloballyPositioned { stageCoords = it },
    ) {
        // ── Canvas for Active Seat Bonds (Behind seats, non-intercepting) ──
        Canvas(
            modifier = Modifier.matchParentSize(),
        ) {
            state.activeBonds.forEach { bond ->
                val p1 = seatCenters[bond.firstSeat]
                val p2 = seatCenters[bond.secondSeat]
                if (p1 != null && p2 != null) {
                    val path = Path().apply {
                        moveTo(p1.x, p1.y)
                        val midY = (p1.y + p2.y) / 2f - 22.dp.toPx()
                        val midX = (p1.x + p2.x) / 2f
                        quadraticTo(midX, midY, p2.x, p2.y)
                    }
                    if (!palette.usesRestrainedStyle) {
                        drawPath(
                            path = path,
                            brush = Brush.linearGradient(
                                listOf(palette.bondLine, Color(0xFFFF78C4)),
                                start = p1,
                                end = p2,
                            ),
                            style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round),
                            alpha = 0.55f,
                        )
                    }
                    drawPath(
                        path = path,
                        color = if (palette.usesRestrainedStyle) palette.bondLine else Color.White,
                        style = Stroke(width = 1.6.dp.toPx(), cap = StrokeCap.Round),
                        alpha = 0.8f,
                    )
                }
            }
        }

        // ── Active Bond Heart Emblems at Midpoint ──
        val density = LocalDensity.current
        state.activeBonds.forEach { bond ->
            val p1 = seatCenters[bond.firstSeat]
            val p2 = seatCenters[bond.secondSeat]
            if (p1 != null && p2 != null) {
                val midX = (p1.x + p2.x) / 2f
                val midY = (p1.y + p2.y) / 2f
                with(density) {
                    val offsetX = (midX - 14.dp.toPx()).toDp()
                    val offsetY = (midY - 20.dp.toPx() - 14.dp.toPx()).toDp()
                    Box(
                        modifier = Modifier
                            .offset(x = offsetX, y = offsetY)
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(palette.seatSurface)
                            .border(1.dp, palette.bondLine, CircleShape)
                            .bounceClick { onBondDetailsClick(bond) },
                        contentAlignment = Alignment.Center,
                    ) {
                        Image(
                            painter = painterResource(R.drawable.vb_rs_bond_heart),
                            contentDescription = "Bond Details",
                            modifier = Modifier.size(16.dp),
                        )
                    }
                }
            }
        }

        // ── Stage Seats Layout ──
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // A. Top Host + Optional Partner Row
            if (state.partnerEnabled) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    AuroraHostSeat(
                        member = state.seatAt(HOST_SEAT_INDEX),
                        isLocked = state.isSeatLocked(HOST_SEAT_INDEX),
                        palette = palette,
                        hostFrameRes = themeRes.hostFrameRes,
                        onClick = onHostProfileClick,
                    )
                    Spacer(Modifier.width(28.dp))
                    AuroraPartnerSeat(
                        member = state.seatAt(PARTNER_SEAT_INDEX),
                        isLocked = state.isSeatLocked(PARTNER_SEAT_INDEX),
                        palette = palette,
                        hostFrameRes = themeRes.hostFrameRes,
                        onClick = { onSeatClick(PARTNER_SEAT_INDEX) },
                    )
                }
            } else {
                AuroraHostSeat(
                    member = state.seatAt(HOST_SEAT_INDEX),
                    isLocked = state.isSeatLocked(HOST_SEAT_INDEX),
                    palette = palette,
                    hostFrameRes = themeRes.hostFrameRes,
                    onClick = onHostProfileClick,
                )
            }

            Spacer(Modifier.height(10.dp))

            // B. Row 1 of Regular Mic Seats (Seats 1, 2, 3, 4)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                for (seat in 1..4) {
                    AuroraSeatView(
                        seatIndex = seat,
                        member = state.seatAt(seat),
                        isLocked = state.isSeatLocked(seat),
                        isBonded = state.isSeatInBond(seat),
                        palette = palette,
                        seatFrameRes = themeRes.seatFrameRes,
                        size = 64.dp,
                        onClick = { onSeatClick(seat) },
                        onPositioned = { coords ->
                            val stage = stageCoords
                            if (stage != null && coords.isAttached && stage.isAttached) {
                                val center = stage.localPositionOf(coords, Offset(coords.size.width / 2f, coords.size.height / 2f))
                                seatCenters[seat] = center
                            }
                        },
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            // C. Row 2 of Regular Mic Seats (Seats 5, 6, 7, 8)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                for (seat in 5..8) {
                    AuroraSeatView(
                        seatIndex = seat,
                        member = state.seatAt(seat),
                        isLocked = state.isSeatLocked(seat),
                        isBonded = state.isSeatInBond(seat),
                        palette = palette,
                        seatFrameRes = themeRes.seatFrameRes,
                        size = 64.dp,
                        onClick = { onSeatClick(seat) },
                        onPositioned = { coords ->
                            val stage = stageCoords
                            if (stage != null && coords.isAttached && stage.isAttached) {
                                val center = stage.localPositionOf(coords, Offset(coords.size.width / 2f, coords.size.height / 2f))
                                seatCenters[seat] = center
                            }
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun AuroraHostSeat(
    member: RoomMemberVo?,
    isLocked: Boolean,
    palette: RoomThemePalette,
    hostFrameRes: Int,
    onClick: () -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.bounceClick(scaleDown = 0.98f, onClick = onClick),
    ) {
        Box(
            modifier = Modifier.size(86.dp),
            contentAlignment = Alignment.Center,
        ) {
            if (member != null) {
                Box(
                    modifier = Modifier
                        .size(66.dp)
                        .clip(CircleShape)
                        .background(palette.seatSurface)
                        .border(
                            width = if (member.speaking) 2.dp else 1.2.dp,
                            color = if (member.speaking) palette.activeMic else palette.seatBorder,
                            shape = CircleShape,
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    RoomMemberAvatarImage(
                        name = member.name,
                        photoPath = member.photoPath,
                        avatarRes = member.avatarRes,
                        size = 62.dp,
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(palette.seatSurface)
                        .border(1.dp, palette.seatBorder, CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(if (isLocked) "🔒" else "+", color = palette.secondaryText, fontSize = 18.sp, fontWeight = FontWeight.Medium)
                }
            }

            Image(
                painter = painterResource(hostFrameRes),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit,
            )

            // Mic Status Indicator (Muted or Active)
            if (member != null && !member.micOn) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(palette.mutedMic)
                        .border(1.dp, palette.primaryText, CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Image(
                        painter = painterResource(R.drawable.vb_vr_ic_mic_off),
                        contentDescription = "Muted",
                        modifier = Modifier.size(11.dp),
                    )
                }
            } else if (member?.speaking == true) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(palette.activeMic)
                        .border(1.dp, palette.primaryText, CircleShape),
                )
            }
        }

        Spacer(Modifier.height(4.dp))

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(palette.cardSurface)
                .border(0.8.dp, palette.seatBorder, RoundedCornerShape(8.dp))
                .padding(horizontal = 8.dp, vertical = 1.dp),
        ) {
            Text(
                text = "HOST",
                color = ChampagneGold,
                fontSize = 8.5.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.4.sp,
            )
        }

        Spacer(Modifier.height(2.dp))

        Text(
            text = member?.name ?: "Host Seat",
            color = if (member != null) palette.primaryText else palette.secondaryText,
            fontSize = 11.5.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun AuroraPartnerSeat(
    member: RoomMemberVo?,
    isLocked: Boolean,
    palette: RoomThemePalette,
    hostFrameRes: Int,
    onClick: () -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.bounceClick(scaleDown = 0.98f, onClick = onClick),
    ) {
        Box(
            modifier = Modifier.size(86.dp),
            contentAlignment = Alignment.Center,
        ) {
            if (member != null) {
                Box(
                    modifier = Modifier
                        .size(66.dp)
                        .clip(CircleShape)
                        .background(palette.seatSurface)
                        .border(
                            width = if (member.speaking) 2.dp else 1.2.dp,
                            color = if (member.speaking) palette.activeMic else palette.seatBorder,
                            shape = CircleShape,
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    RoomMemberAvatarImage(
                        name = member.name,
                        photoPath = member.photoPath,
                        avatarRes = member.avatarRes,
                        size = 62.dp,
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(palette.seatSurface)
                        .border(1.dp, palette.seatBorder, CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    if (isLocked) {
                        Image(
                            painter = painterResource(R.drawable.vb_vr_seat_locked),
                            contentDescription = "Locked partner seat",
                            modifier = Modifier.size(26.dp),
                        )
                    } else {
                        Text("+", color = palette.secondaryText, fontSize = 18.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }

            Image(
                painter = painterResource(hostFrameRes),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit,
            )

            if (member != null && !member.micOn) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(palette.mutedMic)
                        .border(1.dp, palette.primaryText, CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Image(
                        painter = painterResource(R.drawable.vb_vr_ic_mic_off),
                        contentDescription = "Muted",
                        modifier = Modifier.size(11.dp),
                    )
                }
            }
        }

        Spacer(Modifier.height(4.dp))

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(palette.cardSurface)
                .border(0.8.dp, palette.seatBorder, RoundedCornerShape(8.dp))
                .padding(horizontal = 8.dp, vertical = 1.dp),
        ) {
            Text(
                text = "PARTNER",
                color = palette.accent,
                fontSize = 8.5.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.4.sp,
            )
        }

        Spacer(Modifier.height(2.dp))

        Text(
            text = member?.name ?: "Partner Seat",
            color = if (member != null) palette.primaryText else palette.secondaryText,
            fontSize = 11.5.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun AuroraSeatView(
    seatIndex: Int,
    member: RoomMemberVo?,
    isLocked: Boolean,
    isBonded: Boolean = false,
    palette: RoomThemePalette,
    seatFrameRes: Int,
    size: Dp,
    onClick: () -> Unit,
    onPositioned: (LayoutCoordinates) -> Unit = {},
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .bounceClick(scaleDown = 0.97f, onClick = onClick)
            .onGloballyPositioned(onPositioned),
    ) {
        Box(
            modifier = Modifier.size(size),
            contentAlignment = Alignment.Center,
        ) {
            if (member != null) {
                val avatarSize = (size.value * 0.78f).dp
                Box(
                    modifier = Modifier
                        .size(avatarSize)
                        .clip(CircleShape)
                        .background(palette.seatSurface)
                        .border(
                            width = if (member.speaking) 2.dp else 1.dp,
                            color = if (member.speaking) palette.activeMic else palette.seatBorder,
                            shape = CircleShape,
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    RoomMemberAvatarImage(
                        name = member.name,
                        photoPath = member.photoPath,
                        avatarRes = member.avatarRes,
                        size = avatarSize - 2.dp,
                    )
                }

                // Theme-aware seat frame overlay
                val overlayFrame = if (isBonded && !palette.usesRestrainedStyle) {
                    R.drawable.vb_rs_frame_bonded
                } else {
                    seatFrameRes
                }
                Image(
                    painter = painterResource(overlayFrame),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit,
                )

                // Mic Status Badge (Muted or Speaking)
                if (!member.micOn) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size((size.value * 0.28f).dp)
                            .clip(CircleShape)
                            .background(palette.mutedMic)
                            .border(1.dp, palette.primaryText, CircleShape),
                        contentAlignment = Alignment.Center,
                    ) {
                        Image(
                            painter = painterResource(R.drawable.vb_vr_ic_mic_off),
                            contentDescription = "Muted",
                            modifier = Modifier.size((size.value * 0.16f).dp),
                        )
                    }
                } else if (member.speaking) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size((size.value * 0.24f).dp)
                            .clip(CircleShape)
                            .background(palette.activeMic)
                            .border(1.dp, palette.primaryText, CircleShape),
                    )
                }
            } else {
                // Clean empty / locked circular seat — single seat label below (no duplicate number)
                Box(
                    modifier = Modifier
                        .size((size.value * 0.80f).dp)
                        .clip(CircleShape)
                        .background(palette.seatSurface)
                        .border(
                            width = 1.dp,
                            color = if (isLocked) palette.mutedMic.copy(alpha = 0.55f) else palette.seatBorder,
                            shape = CircleShape,
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    if (isLocked) {
                        Image(
                            painter = painterResource(R.drawable.vb_vr_seat_locked),
                            contentDescription = "Locked Seat",
                            modifier = Modifier.size((size.value * 0.36f).dp),
                        )
                    } else {
                        Text(
                            text = "+",
                            color = palette.secondaryText,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(3.dp))

        val label = when {
            member == null -> if (isLocked) "Locked" else "Seat $seatIndex"
            member.isSelf -> "You"
            else -> member.name
        }
        Text(
            text = label,
            color = if (member != null) palette.primaryText else palette.secondaryText,
            fontSize = 10.sp,
            fontWeight = if (member != null) FontWeight.SemiBold else FontWeight.Normal,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  5. Quick Activities Bar (PK Battle, Games, Dice)
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun QuickActivitiesBar(
    palette: RoomThemePalette,
    onPkClick: () -> Unit,
    onGamesClick: () -> Unit,
    onDiceRoll: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        // PK Battle Entry
        Row(
            modifier = Modifier
                .weight(1f)
                .height(36.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(palette.cardSurface)
                .border(1.dp, palette.accent.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                .bounceClick(scaleDown = 0.97f, onClick = onPkClick)
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            Image(
                painter = painterResource(R.drawable.vb_vr_ic_swords),
                contentDescription = null,
                modifier = Modifier.size(16.dp),
            )
            Spacer(Modifier.width(6.dp))
            Text(
                text = "PK Battle",
                color = palette.primaryText,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }

        // In-Room Games Entry
        Row(
            modifier = Modifier
                .weight(1f)
                .height(36.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(palette.cardSurface)
                .border(1.dp, palette.seatBorder, RoundedCornerShape(12.dp))
                .bounceClick(scaleDown = 0.97f, onClick = onGamesClick)
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            Image(
                painter = painterResource(R.drawable.vb_vr_ic_game_cyan),
                contentDescription = null,
                modifier = Modifier.size(16.dp),
            )
            Spacer(Modifier.width(6.dp))
            Text(
                text = "Games",
                color = palette.primaryText,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }

        // Dice Challenge
        Row(
            modifier = Modifier
                .weight(1f)
                .height(36.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(palette.cardSurface)
                .border(1.dp, palette.seatBorder, RoundedCornerShape(12.dp))
                .bounceClick(scaleDown = 0.97f, onClick = onDiceRoll)
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            Image(
                painter = painterResource(R.drawable.vb_vr_ic_dice),
                contentDescription = null,
                modifier = Modifier.size(16.dp),
            )
            Spacer(Modifier.width(6.dp))
            Text(
                text = "Roll Dice",
                color = palette.primaryText,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  6. Translucent Compact Chat Panel (Scroll-safe + New Message Pill)
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun AuroraChatPanel(
    state: VoiceRoomState,
    palette: RoomThemePalette,
    modifier: Modifier = Modifier,
) {
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    var lastSeenCount by remember { mutableIntStateOf(0) }
    val atBottom by remember {
        derivedStateOf {
            val info = listState.layoutInfo
            val lastVisible = info.visibleItemsInfo.lastOrNull()?.index ?: -1
            info.totalItemsCount == 0 || lastVisible >= info.totalItemsCount - 1
        }
    }

    LaunchedEffect(state.messages.size) {
        val newest = state.messages.lastOrNull()
        if (newest != null && (atBottom || newest.isSelf)) {
            listState.animateScrollToItem(state.messages.lastIndex)
            lastSeenCount = state.messages.size
        }
    }
    LaunchedEffect(atBottom) {
        if (atBottom) lastSeenCount = state.messages.size
    }

    val unseenCount = (state.messages.size - lastSeenCount).coerceAtLeast(0)

    Box(
        modifier = modifier
            .padding(horizontal = 12.dp)
            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            .background(palette.cardSurface),
    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 10.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            items(state.messages, key = { it.id }) { message ->
                val authorMember = state.members.firstOrNull { it.name == message.author }
                AuroraChatMessageItem(message = message, authorMember = authorMember, palette = palette)
            }
        }

        if (!atBottom && unseenCount > 0) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(10.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(palette.accent)
                    .clickable {
                        scope.launch {
                            if (state.messages.isNotEmpty()) {
                                listState.animateScrollToItem(state.messages.lastIndex)
                            }
                            lastSeenCount = state.messages.size
                        }
                    }
                    .padding(horizontal = 10.dp, vertical = 5.dp),
            ) {
                Text(
                    text = "$unseenCount new ↓",
                    color = palette.primaryText,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

@Composable
private fun AuroraChatMessageItem(
    message: RoomMessage,
    authorMember: RoomMemberVo?,
    palette: RoomThemePalette,
) {
    if (message.system) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(palette.seatSurface)
                .padding(horizontal = 8.dp, vertical = 4.dp),
        ) {
            Text(
                text = message.body,
                color = palette.secondaryText,
                fontSize = 11.sp,
                lineHeight = 14.sp,
                fontWeight = FontWeight.Medium,
            )
        }
    } else if (message.isGift) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(palette.accent.copy(alpha = 0.25f))
                .padding(horizontal = 8.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (message.giftIconRes != null) {
                Image(
                    painter = painterResource(message.giftIconRes),
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                )
                Spacer(Modifier.width(6.dp))
            }
            Text(
                text = "${message.author} ${message.body}",
                color = palette.primaryText,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
            )
        }
    } else {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top,
        ) {
            RoomMemberAvatarImage(
                name = message.author,
                photoPath = authorMember?.photoPath,
                avatarRes = authorMember?.avatarRes,
                size = 22.dp,
            )
            Spacer(Modifier.width(6.dp))
            Column {
                Text(
                    text = message.author,
                    color = if (message.isSelf) palette.activeMic else palette.secondaryText,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = message.body,
                    color = palette.primaryText,
                    fontSize = 12.sp,
                    lineHeight = 15.sp,
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  7. Fixed Bottom Action Dock
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun AuroraInputDock(
    state: VoiceRoomState,
    palette: RoomThemePalette,
    onToggleMic: () -> Unit,
    onSend: (String) -> Unit,
    onGiftClick: () -> Unit,
    onAudioSettingsClick: () -> Unit,
) {
    var draft by remember { mutableStateOf("") }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(InputBarBg)
            .navigationBarsPadding()
            .imePadding()
            .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .height(42.dp)
                .clip(RoundedCornerShape(21.dp))
                .background(palette.seatSurface)
                .border(1.dp, palette.seatBorder, RoundedCornerShape(21.dp))
                .padding(horizontal = 12.dp),
            contentAlignment = Alignment.CenterStart,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                BasicTextField(
                    value = draft,
                    onValueChange = { draft = it },
                    singleLine = true,
                    textStyle = TextStyle(color = palette.primaryText, fontSize = 13.sp),
                    cursorBrush = SolidColor(palette.accent),
                    modifier = Modifier.weight(1f),
                    decorationBox = { inner ->
                        if (draft.isEmpty()) {
                            Text("Say something nice…", color = palette.secondaryText, fontSize = 13.sp)
                        }
                        inner()
                    },
                )
                Image(
                    painter = painterResource(R.drawable.vb_vr_ic_smile),
                    contentDescription = "Emoji",
                    modifier = Modifier
                        .size(18.dp)
                        .clickable { draft += " ✨" },
                )
            }
        }

        if (draft.isNotBlank()) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(palette.accent)
                    .bounceClick {
                        onSend(draft)
                        draft = ""
                    },
                contentAlignment = Alignment.Center,
            ) {
                Image(
                    painter = painterResource(R.drawable.vb_vr_ic_send),
                    contentDescription = "Send",
                    modifier = Modifier.size(18.dp),
                )
            }
        }

        // Mic Toggle Button
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(if (state.selfMicOn) palette.activeMic.copy(alpha = 0.25f) else palette.seatSurface)
                .border(1.dp, if (state.selfMicOn) palette.activeMic else palette.mutedMic.copy(alpha = 0.65f), CircleShape)
                .bounceClick(onClick = onToggleMic),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(
                    if (state.selfMicOn) R.drawable.vb_vr_ic_mic else R.drawable.vb_vr_ic_mic_off
                ),
                contentDescription = if (state.selfMicOn) "Mute" else "Unmute",
                modifier = Modifier.size(20.dp),
            )
        }

        // Audio Settings Button
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(palette.seatSurface)
                .border(1.dp, palette.seatBorder, CircleShape)
                .bounceClick(onClick = onAudioSettingsClick),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(R.drawable.vb_vr_ic_headphones),
                contentDescription = "Audio Settings",
                modifier = Modifier.size(20.dp),
            )
        }

        // Gift Button
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(palette.accent)
                .bounceClick(onClick = onGiftClick),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(R.drawable.vb_vr_ic_gift),
                contentDescription = "Gifts",
                modifier = Modifier.size(22.dp),
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  8. User Profile Modal Bottom Sheet
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun UserProfileSheet(
    member: RoomMemberVo,
    state: VoiceRoomState,
    isHostUser: Boolean,
    onDismiss: () -> Unit,
    onFollowToggle: () -> Unit,
    onSendGift: () -> Unit,
    onDirectMessage: () -> Unit,
    onLocalMuteToggle: () -> Unit,
    onModeratorMute: () -> Unit,
    onMoveSeat: (Int) -> Unit,
    onToggleModerator: () -> Unit,
    onViewFullProfile: () -> Unit,
    onKickSeat: () -> Unit,
    onReport: () -> Unit,
    onBlock: () -> Unit,
) {
    val isLocallyMuted = member.identity in state.locallyMutedIdentities
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(modifier = Modifier.size(82.dp), contentAlignment = Alignment.Center) {
            RoomMemberAvatarImage(
                name = member.name,
                photoPath = member.photoPath,
                avatarRes = member.avatarRes,
                size = 64.dp,
            )
            Image(
                painter = painterResource(member.frameRes ?: R.drawable.room_seat_classic_lounge),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit,
            )
        }

        Spacer(Modifier.height(8.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(member.name, color = Color.White, fontSize = 17.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.width(6.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFF8058E9))
                    .padding(horizontal = 6.dp, vertical = 2.dp),
            ) {
                Text("Lv.${member.level}", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
            }
        }

        Text(
            text = "ID: ${member.identity.take(8).uppercase()} • ${member.role.uppercase()}",
            color = Color(0xFFC0B8DC),
            fontSize = 11.5.sp,
        )

        Spacer(Modifier.height(14.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(40.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (member.isFollowed) Color(0x55301844) else Color(0xFF9B82E9))
                    .bounceClick(onClick = onFollowToggle),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = if (member.isFollowed) "Following" else "+ Follow",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(40.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0x443E3266))
                    .bounceClick(onClick = onDirectMessage),
                contentAlignment = Alignment.Center,
            ) {
                Text("Message", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(40.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFF7B4AE2))
                    .bounceClick(onClick = onSendGift),
                contentAlignment = Alignment.Center,
            ) {
                Text("Gift", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(36.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0x33261A42))
                    .bounceClick(onClick = onLocalMuteToggle),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = if (isLocallyMuted) "Unmute Locally" else "Local Mute",
                    color = Color(0xFFD2CAE6),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(36.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0x33261A42))
                    .bounceClick(onClick = onViewFullProfile),
                contentAlignment = Alignment.Center,
            ) {
                Text("View Profile", color = Color(0xFF9B82E9), fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
            }
        }

        if (isHostUser && !member.isSelf) {
            Spacer(Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                if (member.hasSeat) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(36.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0x3342D6A0))
                            .bounceClick(onClick = onModeratorMute),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = if (member.micOn) "Mod Mute" else "Unmute Seat",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(36.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0x33FF334B))
                            .bounceClick(onClick = onKickSeat),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text("Remove Seat", color = LiveRedBadge, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(36.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0x33FFD36B))
                        .bounceClick(onClick = onToggleModerator),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = if (member.role == "moderator") "Remove Mod" else "Make Mod",
                        color = ChampagneGold,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
            val openSeat = (1..8).firstOrNull { state.seatAt(it) == null && !state.isSeatLocked(it) }
            if (openSeat != null && member.seat != openSeat) {
                Spacer(Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(34.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0x33302448))
                        .bounceClick { onMoveSeat(openSeat) },
                    contentAlignment = Alignment.Center,
                ) {
                    Text("Move to Open Seat $openSeat", color = Color(0xFFD2CAE6), fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }

        if (!member.isSelf) {
            Spacer(Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                Text(
                    text = "Report User",
                    color = Color(0xFFB8B0CC),
                    fontSize = 11.5.sp,
                    modifier = Modifier
                        .clickable(onClick = onReport)
                        .padding(6.dp),
                )
                Text(
                    text = "Block User",
                    color = LiveRedBadge,
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .clickable(onClick = onBlock)
                        .padding(6.dp),
                )
            }
        }
        Spacer(Modifier.height(10.dp))
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  9. Participants Bottom Sheet (Searchable + Role Groups)
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun ParticipantsSheet(
    state: VoiceRoomState,
    isHostOrMod: Boolean,
    onMemberClick: (RoomMemberVo) -> Unit,
    onInviteToSeat: (RoomMemberVo) -> Unit,
    onDismiss: () -> Unit,
) {
    var query by remember { mutableStateOf("") }
    val filtered = remember(state.members, query) {
        if (query.isBlank()) state.members
        else state.members.filter { it.name.contains(query, ignoreCase = true) || it.role.contains(query, ignoreCase = true) }
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 14.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Participants (${state.members.size})",
                color = Color.White,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
            )
            TextButton(onClick = onDismiss) { Text("Close", color = Color.White.copy(alpha = 0.7f)) }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF1A1430))
                .border(1.dp, Color(0xFF473E63), RoundedCornerShape(12.dp))
                .padding(horizontal = 12.dp, vertical = 9.dp),
        ) {
            BasicTextField(
                value = query,
                onValueChange = { query = it },
                singleLine = true,
                textStyle = TextStyle(color = Color.White, fontSize = 13.sp),
                cursorBrush = SolidColor(Color(0xFF9B82E9)),
                modifier = Modifier.fillMaxWidth(),
                decorationBox = { inner ->
                    if (query.isEmpty()) Text("Search participants or role...", color = Color(0xFFB8B0CC), fontSize = 12.5.sp)
                    inner()
                },
            )
        }

        Spacer(Modifier.height(10.dp))

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .height(320.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(filtered, key = { it.identity }) { member ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0x33261A42))
                        .clickable { onMemberClick(member) }
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    RoomMemberAvatarImage(
                        name = member.name,
                        photoPath = member.photoPath,
                        avatarRes = member.avatarRes,
                        size = 38.dp,
                    )

                    Spacer(Modifier.width(10.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(member.name, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Text(
                            text = when {
                                member.seat == HOST_SEAT_INDEX -> "Host Seat"
                                member.seat == PARTNER_SEAT_INDEX -> "Partner Seat"
                                member.hasSeat -> "Seat ${member.seat} • ${member.role.uppercase()}"
                                else -> "Listening"
                            },
                            color = Color(0xFFC0B8DC),
                            fontSize = 11.sp,
                        )
                    }

                    if (isHostOrMod && !member.hasSeat && !member.isSelf) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF9B82E9))
                                .clickable { onInviteToSeat(member) }
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                        ) {
                            Text("Invite", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    } else if (member.isHost) {
                        Text("Host", color = ChampagneGold, fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                    } else if (member.hasSeat) {
                        Text(
                            text = if (member.role == "moderator") "Mod" else "On Mic",
                            color = Color(0xFF42D6A0),
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  10. Audio Settings Bottom Sheet
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun AudioSettingsSheet(
    state: VoiceRoomState,
    onToggleMic: () -> Unit,
    onToggleEarpiece: () -> Unit,
    onToggleOutputMute: () -> Unit,
    onDismiss: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Audio & Device Settings",
                color = Color.White,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
            )
            TextButton(onClick = onDismiss) {
                Text("Done", color = GlowCyan)
            }
        }

        Spacer(Modifier.height(14.dp))

        // 1. Microphone Toggle
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0x33261A42))
                .clickable(onClick = onToggleMic)
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column {
                Text("Microphone Input", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Text(
                    text = if (state.selfMicOn) "Active • Broadcasting audio" else "Muted • Not broadcasting",
                    color = Color(0xFFC0B8DC),
                    fontSize = 11.sp,
                )
            }
            Text(
                text = if (state.selfMicOn) "MUTE" else "UNMUTE",
                color = if (state.selfMicOn) LiveRedBadge else GlowCyan,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
            )
        }

        Spacer(Modifier.height(10.dp))

        // 2. Audio Output Routing (Speaker vs Earpiece)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0x33261A42))
                .clickable(onClick = onToggleEarpiece)
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column {
                Text("Audio Output Device", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Text(
                    text = if (state.isEarpieceMode) "Earpiece receiver" else "Loudspeaker mode",
                    color = Color(0xFFC0B8DC),
                    fontSize = 11.sp,
                )
            }
            Text(
                text = if (state.isEarpieceMode) "EARPIECE" else "SPEAKER",
                color = GlowCyan,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
            )
        }

        Spacer(Modifier.height(10.dp))

        // 3. Master Audio Output Mute
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0x33261A42))
                .clickable(onClick = onToggleOutputMute)
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column {
                Text("Mute Room Audio", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Text(
                    text = if (state.isAudioMuted) "Room audio is silenced" else "Normal listening volume",
                    color = Color(0xFFC0B8DC),
                    fontSize = 11.sp,
                )
            }
            Text(
                text = if (state.isAudioMuted) "UNMUTE" else "MUTE",
                color = if (state.isAudioMuted) LiveRedBadge else Color.White.copy(alpha = 0.7f),
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
            )
        }

        Spacer(Modifier.height(20.dp))
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  11. Aurora Gift Bottom Sheet
// ─────────────────────────────────────────────────────────────────────────────
private data class AuroraGiftItem(
    val id: String,
    val name: String,
    val coins: Int,
    val iconRes: Int,
    val category: String,
)

private val AuroraGifts = listOf(
    AuroraGiftItem("1", "Red Rose", 10, R.drawable.vb_gift_rose, "Popular"),
    AuroraGiftItem("2", "Sweet Heart", 50, R.drawable.vb_gift_heart, "Popular"),
    AuroraGiftItem("3", "Golden Star", 100, R.drawable.vb_gift_star, "Popular"),
    AuroraGiftItem("4", "Studio Mic", 200, R.drawable.vb_vr_emblem_studio_mic, "Premium"),
    AuroraGiftItem("5", "Royal Crown", 500, R.drawable.vb_gift_crown, "Premium"),
    AuroraGiftItem("6", "Pink Gift Box", 300, R.drawable.vb_vr_emblem_pink_gift, "Popular"),
    AuroraGiftItem("7", "Blue Diamond", 1000, R.drawable.vb_gift_diamond, "Luxury"),
    AuroraGiftItem("8", "VIP Diamond", 2000, R.drawable.vb_vr_emblem_vip_diamond, "Luxury"),
    AuroraGiftItem("9", "Victory Trophy", 1500, R.drawable.vb_vr_emblem_trophy, "Event"),
)

@Composable
private fun AuroraGiftSheet(
    state: VoiceRoomState,
    userCoinBalance: Long,
    onSendGift: (AuroraGiftItem, String, Int) -> Unit,
    onDismiss: () -> Unit,
) {
    var selectedCategory by rememberSaveable { mutableStateOf("Popular") }
    var selectedGift by remember { mutableStateOf(AuroraGifts.first()) }
    var selectedRecipient by rememberSaveable {
        mutableStateOf(state.seatAt(0)?.name ?: "Room Host")
    }
    var selectedMultiplier by rememberSaveable { mutableIntStateOf(1) }

    val categories = listOf("Popular", "Premium", "Luxury", "Event")
    val multipliers = listOf(1, 5, 10, 99)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 14.dp),
    ) {
        // Sheet Header: Category Tabs
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(categories) { cat ->
                    val active = cat == selectedCategory
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (active) GlowCyan else Color(0x33261A42))
                            .bounceClick { selectedCategory = cat }
                            .padding(horizontal = 12.dp, vertical = 5.dp),
                    ) {
                        Text(
                            text = cat,
                            color = if (active) Color(0xFF06202B) else Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }

            TextButton(onClick = onDismiss) {
                Text("Close", color = Color.White.copy(alpha = 0.6f))
            }
        }

        Spacer(Modifier.height(10.dp))

        // Recipient Selection Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0x33261A42))
                .padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("Send to:", color = Color(0xFFC0B8DC), fontSize = 11.sp)
            Spacer(Modifier.width(8.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                // Host option
                item {
                    val hostName = state.seatAt(0)?.name ?: "Room Host"
                    val active = selectedRecipient == hostName
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (active) VyboraPurple else Color(0x33FFFFFF))
                            .bounceClick { selectedRecipient = hostName }
                            .padding(horizontal = 8.dp, vertical = 3.dp),
                    ) {
                        Text("👑 $hostName", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
                // Other seated speakers
                items(state.seated.filter { !it.isHost && !it.isSelf }) { member ->
                    val active = selectedRecipient == member.name
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (active) VyboraPurple else Color(0x33FFFFFF))
                            .bounceClick { selectedRecipient = member.name }
                            .padding(horizontal = 8.dp, vertical = 3.dp),
                    ) {
                        Text(member.name, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                    }
                }
                // All Speakers option
                item {
                    val active = selectedRecipient == "All Speakers"
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (active) VyboraPurple else Color(0x33FFFFFF))
                            .bounceClick { selectedRecipient = "All Speakers" }
                            .padding(horizontal = 8.dp, vertical = 3.dp),
                    ) {
                        Text("All Speakers", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        // 3-Column Gift Grid
        val filteredGifts = AuroraGifts.filter { it.category == selectedCategory }
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier
                .fillMaxWidth()
                .height(190.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(filteredGifts, key = { it.id }) { gift ->
                val selected = gift.id == selectedGift.id
                Column(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (selected) Color(0x668058E9) else Color(0x33261A42))
                        .border(
                            1.dp,
                            if (selected) GlowCyan else Color.White.copy(alpha = 0.1f),
                            RoundedCornerShape(12.dp)
                        )
                        .bounceClick { selectedGift = gift }
                        .padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Image(
                        painter = painterResource(gift.iconRes),
                        contentDescription = gift.name,
                        modifier = Modifier.size(40.dp),
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(gift.name, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                    Text("🪙 ${gift.coins}", color = VyboraGold, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(Modifier.height(10.dp))

        // Quantity Multipliers + Send CTA
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            // Quantity pills
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                multipliers.forEach { qty ->
                    val active = qty == selectedMultiplier
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (active) GlowCyan else Color(0x33FFFFFF))
                            .bounceClick { selectedMultiplier = qty }
                            .padding(horizontal = 8.dp, vertical = 6.dp),
                    ) {
                        Text(
                            text = "${qty}x",
                            color = if (active) Color(0xFF06202B) else Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }

            val totalCost = selectedGift.coins * selectedMultiplier
            val hasEnough = userCoinBalance >= totalCost || state.connection == RoomConnection.SIMULATED
            Box(
                modifier = Modifier
                    .height(40.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        if (hasEnough) Brush.horizontalGradient(listOf(Color(0xFFE91E63), Color(0xFF8058E9)))
                        else SolidColor(Color(0x553B3057))
                    )
                    .bounceClick(enabled = hasEnough) {
                        onSendGift(selectedGift, selectedRecipient, selectedMultiplier)
                    }
                    .padding(horizontal = 18.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "Send · $totalCost Coins",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        Text(
            text = if (state.connection == RoomConnection.SIMULATED) {
                "Balance: $userCoinBalance Coins • Offline preview — no coins are deducted"
            } else {
                "Balance: $userCoinBalance Coins"
            },
            color = Color(0xFFB39DDB),
            fontSize = 10.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

// ── Floating Gift Celebration Overlay ──
private data class GiftAnimationData(
    val giftName: String,
    val iconRes: Int,
    val recipientName: String,
    val count: Int,
)

@Composable
private fun GiftCelebrationOverlay(data: GiftAnimationData, onDismiss: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0x55000000))
            .clickable(
                interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
                indication = null,
                onClick = onDismiss,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Image(
                painter = painterResource(data.iconRes),
                contentDescription = data.giftName,
                modifier = Modifier.size(120.dp),
            )
            Spacer(Modifier.height(10.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.horizontalGradient(listOf(Color(0xFFE91E63), Color(0xFF8058E9)))
                    )
                    .padding(horizontal = 20.dp, vertical = 8.dp),
            ) {
                Text(
                    text = "${data.giftName} x${data.count} sent to ${data.recipientName}",
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  12. PK Battle Modal Bottom Sheet
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun PkBattleSheet(
    onStartBattle: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    var selectedMode by rememberSaveable { mutableStateOf("1v1 Host PK") }
    var battleActive by rememberSaveable { mutableStateOf(false) }
    var blueScore by rememberSaveable { mutableIntStateOf(1450) }
    var pinkScore by rememberSaveable { mutableIntStateOf(1820) }
    var secondsRemaining by rememberSaveable { mutableIntStateOf(45) }

    LaunchedEffect(battleActive) {
        if (battleActive) {
            while (secondsRemaining > 0) {
                delay(1000)
                secondsRemaining--
                blueScore += (20..80).random()
                pinkScore += (15..90).random()
            }
            battleActive = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column {
                Text("PK Battle", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text("Local interactive preview — cross-room server PK pending", color = Color(0xFFB8B0CC), fontSize = 10.5.sp)
            }
            TextButton(onClick = onDismiss) { Text("Close", color = Color.White.copy(alpha = 0.6f)) }
        }

        Spacer(Modifier.height(12.dp))

        // PK Mode Selector Tabs
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            listOf("1v1 Host PK", "4v4 Team Battle", "Party PK").forEach { mode ->
                val active = mode == selectedMode
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (active) GlowCyan else Color(0x33261A42))
                        .bounceClick { selectedMode = mode }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = mode,
                        color = if (active) Color(0xFF06202B) else Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }

        Spacer(Modifier.height(18.dp))

        // Live Battle Status & Scores (Team Blue vs Team Pink)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(horizontalAlignment = Alignment.Start) {
                Text("TEAM BLUE", color = Color(0xFF29B6F6), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Text("$blueScore", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(ChampagneGold.copy(alpha = 0.2f))
                        .border(1.dp, ChampagneGold.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 2.dp),
                ) {
                    Text("⚡ VS ⚡", color = ChampagneGold, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold)
                }
                Spacer(Modifier.height(4.dp))
                Text("${secondsRemaining}s", color = ChampagneGold, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }

            Column(horizontalAlignment = Alignment.End) {
                Text("TEAM PINK", color = Color(0xFFEC407A), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Text("$pinkScore", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
            }
        }

        Spacer(Modifier.height(10.dp))

        // Progress Bar with glowing neon gradients
        val total = (blueScore + pinkScore).coerceAtLeast(1)
        val blueFraction = blueScore.toFloat() / total
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(14.dp)
                .clip(RoundedCornerShape(7.dp))
                .background(
                    Brush.horizontalGradient(
                        listOf(Color(0xFFFF4081), Color(0xFFE91E63))
                    )
                ),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(blueFraction)
                    .height(14.dp)
                    .background(
                        Brush.horizontalGradient(
                            listOf(Color(0xFF00B0FF), AuroraTurquoise)
                        )
                    ),
            )
        }

        Spacer(Modifier.height(20.dp))

        // Action CTA
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(46.dp)
                .clip(RoundedCornerShape(23.dp))
                .background(
                    Brush.horizontalGradient(listOf(Color(0xFF8058E9), Color(0xFF39D5DC)))
                )
                .bounceClick {
                    battleActive = true
                    secondsRemaining = 60
                    onStartBattle(selectedMode)
                },
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = if (battleActive) "Cheer for Team!" else "Start $selectedMode Battle",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
            )
        }

        Spacer(Modifier.height(14.dp))
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  13. In-Room Games & Activities Sheet (Truth or Dare, Dice, Poll)
// ─────────────────────────────────────────────────────────────────────────────
private val TruthOrDarePrompts = listOf(
    "Sing your favorite song's chorus for 15 seconds!",
    "What is the funniest rumor you ever heard about yourself?",
    "Do an impression of another host in the lounge!",
    "Who in this room would you take on a road trip?",
    "Tell us about your most awkward online gaming moment.",
    "Speak in a Shakespearean accent until the next round!",
    "What song do you secretly love that nobody expects?",
    "Rate your gaming skills from 1 to 10 honestly!",
)

@Composable
private fun InRoomGamesSheet(
    onPostGameAction: (String) -> Unit,
    onLaunchLudo: () -> Unit,
    onLaunchSpy: () -> Unit,
    onDismiss: () -> Unit,
) {
    var selectedTab by rememberSaveable { mutableStateOf("Truth/Dare") }
    var promptIndex by rememberSaveable { mutableIntStateOf(0) }
    var pollVotedOption by rememberSaveable { mutableStateOf<String?>(null) }
    var optionAVotes by rememberSaveable { mutableIntStateOf(14) }
    var optionBVotes by rememberSaveable { mutableIntStateOf(8) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("Room Games & Activities", color = Color.White, fontSize = 17.sp, fontWeight = FontWeight.Bold)
            TextButton(onClick = onDismiss) { Text("Close", color = Color.White.copy(alpha = 0.6f)) }
        }

        Spacer(Modifier.height(8.dp))

        // Quick launch compatible mini-games while keeping active voice room minimized
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(38.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF2B224B))
                    .border(1.dp, Color(0xFF473E63), RoundedCornerShape(10.dp))
                    .bounceClick(onClick = onLaunchLudo),
                contentAlignment = Alignment.Center,
            ) {
                Text("Play Ludo (Voice Live)", color = Color.White, fontSize = 11.5.sp, fontWeight = FontWeight.SemiBold)
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(38.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF2B224B))
                    .border(1.dp, Color(0xFF473E63), RoundedCornerShape(10.dp))
                    .bounceClick(onClick = onLaunchSpy),
                contentAlignment = Alignment.Center,
            ) {
                Text("Who's the Spy (Live)", color = Color.White, fontSize = 11.5.sp, fontWeight = FontWeight.SemiBold)
            }
        }

        Spacer(Modifier.height(10.dp))

        // Activity Tab Selector
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("Truth/Dare", "Quick Poll").forEach { tab ->
                val active = tab == selectedTab
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (active) GlowCyan else Color(0x33261A42))
                        .bounceClick { selectedTab = tab }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = tab,
                        color = if (active) Color(0xFF06202B) else Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        if (selectedTab == "Truth/Dare") {
            // Truth or Dare Card Deck
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0x448058E9))
                    .border(1.dp, GlowCyan.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                    .padding(16.dp),
                contentAlignment = Alignment.Center,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🎭 PARTY PROMPT #${promptIndex + 1}", color = VyboraGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = TruthOrDarePrompts[promptIndex % TruthOrDarePrompts.size],
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center,
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .clip(RoundedCornerShape(22.dp))
                        .background(Color(0x33FFFFFF))
                        .bounceClick {
                            promptIndex = (promptIndex + 1) % TruthOrDarePrompts.size
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    Text("Next Prompt ›", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .clip(RoundedCornerShape(22.dp))
                        .background(GlowCyan)
                        .bounceClick {
                            val prompt = TruthOrDarePrompts[promptIndex % TruthOrDarePrompts.size]
                            onPostGameAction("🎭 Truth/Dare Prompt: \"$prompt\"")
                            onDismiss()
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    Text("Challenge Stage!", color = Color(0xFF06202B), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }
        } else {
            // Quick Poll Widget
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0x33261A42))
                    .padding(16.dp),
            ) {
                Text("📊 Live Room Poll", color = VyboraGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(4.dp))
                Text("Who should host the next live party game?", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)

                Spacer(Modifier.height(12.dp))

                // Option A
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (pollVotedOption == "A") GlowCyan.copy(alpha = 0.2f) else Color(0x33FFFFFF))
                        .clickable {
                            if (pollVotedOption == null) {
                                pollVotedOption = "A"
                                optionAVotes++
                            }
                        }
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text("Option A: Current Host (Neha)", color = Color.White, fontSize = 13.sp)
                    Text("$optionAVotes votes", color = GlowCyan, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(Modifier.height(8.dp))

                // Option B
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (pollVotedOption == "B") GlowCyan.copy(alpha = 0.2f) else Color(0x33FFFFFF))
                        .clickable {
                            if (pollVotedOption == null) {
                                pollVotedOption = "B"
                                optionBVotes++
                            }
                        }
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text("Option B: Pass Mic to Stage Challenger", color = Color.White, fontSize = 13.sp)
                    Text("$optionBVotes votes", color = GlowCyan, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(Modifier.height(20.dp))
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  14. Room Actions / More Bottom Sheet
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun RoomMoreActionsSheet(
    state: VoiceRoomState,
    isHostOrMod: Boolean,
    onOpenThemes: () -> Unit,
    onOpenCrest: () -> Unit,
    onEditAnnouncement: () -> Unit,
    onOpenParticipants: () -> Unit,
    onOpenAudioSettings: () -> Unit,
    onOpenSeatManager: () -> Unit,
    onOpenPk: () -> Unit,
    onOpenGames: () -> Unit,
    onTogglePartnerSeat: () -> Unit,
    onOpenAttachSeats: () -> Unit,
    onInviteFriends: () -> Unit,
    onReportRoom: () -> Unit,
    onCloseRoom: () -> Unit,
    onDismiss: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 14.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("Room Controls", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            TextButton(onClick = onDismiss) { Text("Close", color = Color.White.copy(alpha = 0.6f)) }
        }

        Spacer(Modifier.height(10.dp))

        val actions = buildList {
            add(Triple("Room Themes", R.drawable.vb_vr_ic_sparkles, onOpenThemes))
            add(Triple("Room Crest", R.drawable.vb_vr_emblem_crown, onOpenCrest))
            add(Triple("Participants", R.drawable.vb_vr_ic_users, onOpenParticipants))
            add(Triple("Audio Setup", R.drawable.vb_vr_ic_headphones, onOpenAudioSettings))
            if (isHostOrMod) {
                add(Triple("Notice", R.drawable.vb_vr_ic_copy, onEditAnnouncement))
                add(Triple("Seat Locks", R.drawable.vb_vr_ic_lock, onOpenSeatManager))
                add(Triple(if (state.partnerEnabled) "Partner: ON" else "Partner: OFF", R.drawable.vb_rs_seat_partner_empty, onTogglePartnerSeat))
                add(Triple("Attach Seats", R.drawable.vb_rs_attach_pair, onOpenAttachSeats))
            }
            add(Triple("PK Battle", R.drawable.vb_vr_ic_swords, onOpenPk))
            add(Triple("Games", R.drawable.vb_vr_ic_game, onOpenGames))
            add(Triple("Invite", R.drawable.vb_vr_ic_share, onInviteFriends))
            add(Triple("Report", R.drawable.vb_vr_ic_mic_off, onReportRoom))
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            modifier = Modifier
                .fillMaxWidth()
                .height(if (actions.size > 8) 250.dp else 180.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(actions) { (label, iconRes, action) ->
                Column(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0x33261A42))
                        .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
                        .bounceClick(onClick = action)
                        .padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Image(
                        painter = painterResource(iconRes),
                        contentDescription = label,
                        modifier = Modifier.size(24.dp),
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        label,
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
                .clip(RoundedCornerShape(22.dp))
                .background(Color(0x33FF334B))
                .bounceClick(onClick = onCloseRoom),
            contentAlignment = Alignment.Center,
        ) {
            Text("Leave Voice Room", color = LiveRedBadge, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(Modifier.height(14.dp))
    }
}

@Composable
private fun SeatManagementSheet(
    state: VoiceRoomState,
    onToggleLock: (Int) -> Unit,
    onDismiss: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 14.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("Manage Seat Locks", color = Color.White, fontSize = 17.sp, fontWeight = FontWeight.Bold)
            TextButton(onClick = onDismiss) { Text("Done", color = Color(0xFF9B82E9)) }
        }
        Spacer(Modifier.height(8.dp))
        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items((1..8).toList()) { seat ->
                val locked = state.isSeatLocked(seat)
                Column(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (locked) Color(0x33EF5578) else Color(0x33261A42))
                        .border(1.dp, if (locked) Color(0xFFEF5578) else Color(0xFF473E63), RoundedCornerShape(12.dp))
                        .clickable { onToggleLock(seat) }
                        .padding(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text("Seat $seat", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Text(if (locked) "Locked" else "Open", color = if (locked) Color(0xFFFF8BA3) else Color(0xFF42D6A0), fontSize = 10.sp)
                }
            }
        }
        Spacer(Modifier.height(12.dp))
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  15. Attach Seats (Bonding) Selection Sheet
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun AttachSeatsBottomSheet(
    state: VoiceRoomState,
    onRequestBond: (Int, Int) -> Unit,
    onDismiss: () -> Unit,
) {
    var selectedSeatA by remember { mutableStateOf<Int?>(null) }
    var selectedSeatB by remember { mutableStateOf<Int?>(null) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(R.drawable.vb_rs_attach_pair),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                )
                Spacer(Modifier.width(8.dp))
                Text("Attach Seats", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
            TextButton(onClick = onDismiss) { Text("Close", color = Color.White.copy(alpha = 0.6f)) }
        }

        Spacer(Modifier.height(4.dp))
        Text(
            text = "Select two occupied regular mic seats (1-8) to connect inside this room only.",
            color = Color(0xFFC0B8DC),
            fontSize = 12.sp,
        )

        Spacer(Modifier.height(14.dp))

        // Grid of 8 regular seats
        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            modifier = Modifier
                .fillMaxWidth()
                .height(170.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items((1..8).toList()) { seat ->
                val occupant = state.seatAt(seat)
                val isSelected = seat == selectedSeatA || seat == selectedSeatB
                val isOccupied = occupant != null
                val isAlreadyBonded = state.isSeatInBond(seat)

                Column(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (isSelected) GlowPurple.copy(alpha = 0.45f)
                            else if (isAlreadyBonded) Color(0x33FF4081)
                            else Color(0x33261A42)
                        )
                        .border(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) GlowCyan else if (isAlreadyBonded) Color(0xFFFF4081) else Color.White.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(12.dp),
                        )
                        .clickable(enabled = isOccupied) {
                            when {
                                selectedSeatA == seat -> selectedSeatA = null
                                selectedSeatB == seat -> selectedSeatB = null
                                selectedSeatA == null -> selectedSeatA = seat
                                selectedSeatB == null -> selectedSeatB = seat
                                else -> selectedSeatB = seat
                            }
                        }
                        .padding(6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Box(
                        modifier = Modifier.size(36.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        if (occupant != null) {
                            if (occupant.avatarRes != null) {
                                Image(
                                    painter = painterResource(occupant.avatarRes),
                                    contentDescription = occupant.name,
                                    modifier = Modifier.size(32.dp).clip(CircleShape),
                                    contentScale = ContentScale.Crop,
                                )
                            } else {
                                InitialsAvatar(name = occupant.name, size = 32.dp)
                            }
                        } else {
                            Image(
                                painter = painterResource(R.drawable.vb_vr_seat_empty),
                                contentDescription = "Empty",
                                modifier = Modifier.size(28.dp),
                            )
                        }
                    }
                    Spacer(Modifier.height(3.dp))
                    Text(
                        text = "Seat $seat",
                        color = if (isSelected) GlowCyan else Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = if (occupant != null) occupant.name else "Empty",
                        color = if (occupant != null) Color(0xFFD0C8E8) else Color.White.copy(alpha = 0.4f),
                        fontSize = 9.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        // Selected summary
        val canBond = selectedSeatA != null && selectedSeatB != null && selectedSeatA != selectedSeatB
        val occupantA = selectedSeatA?.let { state.seatAt(it) }
        val occupantB = selectedSeatB?.let { state.seatAt(it) }

        if (selectedSeatA != null || selectedSeatB != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0x44261A42))
                    .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                    .padding(10.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Seat ${selectedSeatA ?: "?"} (${occupantA?.name ?: "—"})",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Image(
                        painter = painterResource(R.drawable.vb_rs_bond_heart),
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                    )
                    Text(
                        text = "Seat ${selectedSeatB ?: "?"} (${occupantB?.name ?: "—"})",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
            Spacer(Modifier.height(12.dp))
        }

        // Action Button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(46.dp)
                .clip(RoundedCornerShape(23.dp))
                .background(
                    if (canBond) Brush.horizontalGradient(listOf(GlowPurple, GlowCyan))
                    else SolidColor(Color(0x33FFFFFF))
                )
                .bounceClick(enabled = canBond) {
                    if (canBond) {
                        onRequestBond(selectedSeatA!!, selectedSeatB!!)
                    }
                },
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = if (canBond) "Attach Selected Seats" else "Select 2 Occupied Seats",
                color = if (canBond) Color.White else Color.White.copy(alpha = 0.5f),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
            )
        }

        Spacer(Modifier.height(16.dp))
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  16. Bond Details Bottom Sheet
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun BondDetailsBottomSheet(
    bond: SeatBond,
    state: VoiceRoomState,
    onDetach: () -> Unit,
    onDismiss: () -> Unit,
) {
    val occupantA = state.seatAt(bond.firstSeat)
    val occupantB = state.seatAt(bond.secondSeat)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painter = painterResource(R.drawable.vb_rs_bond_heart),
            contentDescription = null,
            modifier = Modifier.size(48.dp),
        )

        Spacer(Modifier.height(10.dp))

        Text(
            text = "Attached Seats",
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
        )

        Spacer(Modifier.height(4.dp))

        Text(
            text = "Visual stage link between two seats (does not alter friend or profile bonds)",
            color = Color(0xFFC0B8DC),
            fontSize = 12.sp,
        )

        Spacer(Modifier.height(20.dp))

        // Bonded Participants Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                RoomMemberAvatarImage(
                    name = occupantA?.name ?: "Seat ${bond.firstSeat}",
                    photoPath = occupantA?.photoPath,
                    avatarRes = occupantA?.avatarRes,
                    size = 48.dp,
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = occupantA?.name ?: "Seat ${bond.firstSeat}",
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                )
                Text(text = "Seat ${bond.firstSeat}", color = GlowCyan, fontSize = 11.sp)
            }

            Image(
                painter = painterResource(R.drawable.vb_rs_attach_pair),
                contentDescription = null,
                modifier = Modifier.size(32.dp),
            )

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                RoomMemberAvatarImage(
                    name = occupantB?.name ?: "Seat ${bond.secondSeat}",
                    photoPath = occupantB?.photoPath,
                    avatarRes = occupantB?.avatarRes,
                    size = 48.dp,
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = occupantB?.name ?: "Seat ${bond.secondSeat}",
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                )
                Text(text = "Seat ${bond.secondSeat}", color = GlowCyan, fontSize = 11.sp)
            }
        }

        Spacer(Modifier.height(24.dp))

        // Detach Button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
                .clip(RoundedCornerShape(22.dp))
                .background(Color(0x33FF334B))
                .bounceClick(onClick = onDetach),
            contentAlignment = Alignment.Center,
        ) {
            Text("Detach Seat Bond", color = LiveRedBadge, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(Modifier.height(10.dp))

        TextButton(onClick = onDismiss) {
            Text("Close", color = Color.White.copy(alpha = 0.6f))
        }
    }
}
