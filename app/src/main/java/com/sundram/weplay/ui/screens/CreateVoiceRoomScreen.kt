package com.sundram.weplay.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.sundram.weplay.R
import com.sundram.weplay.data.Demo
import com.sundram.weplay.ui.components.bounceClick
import com.sundram.weplay.ui.nav.Routes
import com.sundram.weplay.ui.theme.CyanBrand
import kotlinx.coroutines.delay

private val BgPearl = Color(0xFFF8F7FF)
private val TextDark = Color(0xFF211B38)
private val TextSecondary = Color(0xFF6C628A)
private val BorderLavender = Color(0xFFECE9FE)
private val CardWhite = Color(0xFFFFFFFF)
private val PurpleBrand = Color(0xFF8058E9)
private val ChipInactiveBg = Color(0xFFF0EDFF)
private val ErrorRed = Color(0xFFE53935)

private data class CreateRoomMode(
    val title: String,
    val iconRes: Int,
    val badge: String? = null,
)

private val RoomModes = listOf(
    CreateRoomMode("Normal", R.drawable.vb_rs_create_room),
    CreateRoomMode("Music/KTV", R.drawable.vb_rs_mode_music),
    CreateRoomMode("Games", R.drawable.vb_rs_mode_games, badge = "HOT"),
    CreateRoomMode("PK Battle", R.drawable.vb_rs_mode_pk),
    CreateRoomMode("Dice Party", R.drawable.vb_rs_mode_dice),
)

private val RoomTags = listOf("Friends", "Music", "Game", "Chats", "PK")

/**
 * Redesigned Create Voice Room Screen.
 * Fixes status-bar and navigation-bar overlap, enforces 3-40 char validation,
 * provides room mode and tag selection, password toggle, stage layout preview (1 Host + 8 Mic Seats),
 * and demo room creation.
 */
@Composable
fun CreateVoiceRoomScreen(nav: NavHostController) {
    var roomTitle by rememberSaveable { mutableStateOf("") }
    var passwordEnabled by rememberSaveable { mutableStateOf(false) }
    var roomPassword by rememberSaveable { mutableStateOf("") }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    var isPrivate by rememberSaveable { mutableStateOf(false) }
    var selectedModeIndex by rememberSaveable { mutableIntStateOf(0) }
    var selectedTagIndex by rememberSaveable { mutableIntStateOf(0) }
    var announcement by rememberSaveable { mutableStateOf("") }
    var showAdvanced by rememberSaveable { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var hasAttemptedSubmit by remember { mutableStateOf(false) }

    val isTitleValid = roomTitle.trim().length in 3..40
    val isFormValid = isTitleValid && (!passwordEnabled || roomPassword.length >= 4)

    // Creation simulation effect
    LaunchedEffect(isLoading) {
        if (isLoading) {
            delay(1200)
            val targetRoomId = Demo.rooms.firstOrNull()?.id ?: "room_1"
            nav.navigate(Routes.roomRoute(targetRoomId)) {
                popUpTo(Routes.CREATE_ROOM) { inclusive = true }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgPearl)
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding(),
    ) {
        // ── Main Scrollable Body ──
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 76.dp)
                .verticalScroll(rememberScrollState()),
        ) {
            // ── 1. Safe Top App Bar ──
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Close Button
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(CardWhite)
                        .border(1.dp, BorderLavender, CircleShape)
                        .bounceClick { nav.popBackStack() },
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_close),
                        contentDescription = "Close",
                        tint = TextDark,
                        modifier = Modifier.size(18.dp),
                    )
                }

                Spacer(Modifier.width(12.dp))

                Text(
                    text = "Create Lounge",
                    color = TextDark,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                )

                // Demo Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(BorderLavender)
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                ) {
                    Text(
                        text = "LOCAL DEMO",
                        color = PurpleBrand,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            // ── 2. Hero Cover Picker ──
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(130.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .shadow(3.dp, RoundedCornerShape(20.dp)),
            ) {
                Image(
                    painter = painterResource(R.drawable.vb_rs_bg_create_room),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                )

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(Color(0x221C1438), Color(0xBB1C1438))
                            )
                        ),
                )

                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier
                            .size(68.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0x55FFFFFF))
                            .border(1.dp, Color.White.copy(alpha = 0.5f), RoundedCornerShape(16.dp)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Image(
                            painter = painterResource(R.drawable.vb_rs_voice_mic),
                            contentDescription = "Room Icon",
                            modifier = Modifier.size(42.dp),
                        )
                    }

                    Spacer(Modifier.width(14.dp))

                    Column(Modifier.weight(1f)) {
                        Text(
                            text = "Lounge Cover Art",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                        )
                        Spacer(Modifier.height(3.dp))
                        Text(
                            text = "Original Aurora artwork is assigned automatically",
                            color = Color(0xFFD8D2EE),
                            fontSize = 12.sp,
                        )
                    }
                }
            }

            Spacer(Modifier.height(18.dp))

            // ── 3. Room Title Input ──
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Lounge Title",
                        color = TextDark,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = "${roomTitle.length}/40",
                        color = if (roomTitle.length > 40) ErrorRed else TextSecondary,
                        fontSize = 12.sp,
                    )
                }

                Spacer(Modifier.height(6.dp))

                val showError = hasAttemptedSubmit && !isTitleValid
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(CardWhite)
                        .border(
                            width = 1.dp,
                            color = if (showError) ErrorRed else BorderLavender,
                            shape = RoundedCornerShape(14.dp),
                        )
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                ) {
                    BasicTextField(
                        value = roomTitle,
                        onValueChange = {
                            if (it.length <= 40) roomTitle = it
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        textStyle = TextStyle(color = TextDark, fontSize = 15.sp, fontWeight = FontWeight.Medium),
                        cursorBrush = SolidColor(PurpleBrand),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        decorationBox = { innerTextField ->
                            if (roomTitle.isEmpty()) {
                                Text("e.g. Midnight Chill & Music Lounge", color = TextSecondary, fontSize = 14.sp)
                            }
                            innerTextField()
                        },
                    )
                }

                if (showError) {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "Title must be between 3 and 40 characters.",
                        color = ErrorRed,
                        fontSize = 12.sp,
                    )
                }
            }

            Spacer(Modifier.height(18.dp))

            // ── 4. Room Modes ──
            Column(
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = "Room Mode",
                    color = TextDark,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp),
                )

                Spacer(Modifier.height(8.dp))

                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    itemsIndexed(RoomModes) { index, mode ->
                        val isSelected = index == selectedModeIndex
                        Box(
                            modifier = Modifier
                                .width(96.dp)
                                .height(90.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (isSelected) ChipInactiveBg else CardWhite)
                                .border(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) PurpleBrand else BorderLavender,
                                    shape = RoundedCornerShape(16.dp),
                                )
                                .clickable { selectedModeIndex = index }
                                .padding(8.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                            ) {
                                Image(
                                    painter = painterResource(mode.iconRes),
                                    contentDescription = mode.title,
                                    modifier = Modifier.size(36.dp),
                                )
                                Spacer(Modifier.height(6.dp))
                                Text(
                                    text = mode.title,
                                    color = if (isSelected) PurpleBrand else TextDark,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    maxLines = 1,
                                )
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(18.dp))

            // ── 5. Room Tags ──
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
            ) {
                Text(
                    text = "Lounge Tag",
                    color = TextDark,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                )

                Spacer(Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    RoomTags.forEachIndexed { index, tag ->
                        val isSelected = index == selectedTagIndex
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) PurpleBrand else CardWhite)
                                .border(1.dp, if (isSelected) PurpleBrand else BorderLavender, RoundedCornerShape(12.dp))
                                .clickable { selectedTagIndex = index }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = tag,
                                color = if (isSelected) Color.White else TextDark,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(18.dp))

            // ── 6. Stage Architecture Summary Card ──
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(CardWhite)
                    .border(1.dp, BorderLavender, RoundedCornerShape(16.dp))
                    .padding(14.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(R.drawable.vb_rs_host_crown),
                        contentDescription = null,
                        modifier = Modifier.size(28.dp),
                    )
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(
                            text = "Stage Layout: 1 Host + 8 Regular Mic Seats",
                            color = TextDark,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                        )
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = "4×2 mic grid below host. Expandable with optional Partner Seat.",
                            color = TextSecondary,
                            fontSize = 11.sp,
                        )
                    }
                }
            }

            Spacer(Modifier.height(18.dp))

            // ── 7. Privacy & Password Controls ──
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(CardWhite)
                    .border(1.dp, BorderLavender, RoundedCornerShape(16.dp))
                    .padding(14.dp),
            ) {
                // Private Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Image(
                        painter = painterResource(R.drawable.vb_rs_privacy_shield),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                    )
                    Spacer(Modifier.width(10.dp))
                    Column(Modifier.weight(1f)) {
                        Text(
                            text = "Private Room",
                            color = TextDark,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                        )
                        Text(
                            text = if (isPrivate) "Invite-only [DEMO]" else "Anyone can discover & join [DEMO]",
                            color = TextSecondary,
                            fontSize = 12.sp,
                        )
                    }
                    Switch(
                        checked = isPrivate,
                        onCheckedChange = { isPrivate = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = CyanBrand,
                        ),
                    )
                }

                Spacer(Modifier.height(12.dp))

                // Password Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Image(
                        painter = painterResource(R.drawable.vb_rs_password_lock),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                    )
                    Spacer(Modifier.width(10.dp))
                    Column(Modifier.weight(1f)) {
                        Text(
                            text = "Room Password",
                            color = TextDark,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                        )
                        Text(
                            text = if (passwordEnabled) "Protected by 4-digit code" else "No password required",
                            color = TextSecondary,
                            fontSize = 12.sp,
                        )
                    }
                    Switch(
                        checked = passwordEnabled,
                        onCheckedChange = { passwordEnabled = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = PurpleBrand,
                        ),
                    )
                }

                // Password Text Field (Revealed when enabled)
                AnimatedVisibility(
                    visible = passwordEnabled,
                    enter = fadeIn(),
                    exit = fadeOut(),
                ) {
                    Column(modifier = Modifier.padding(top = 10.dp)) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(ChipInactiveBg)
                                .border(1.dp, BorderLavender, RoundedCornerShape(12.dp))
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                BasicTextField(
                                    value = roomPassword,
                                    onValueChange = {
                                        if (it.length <= 12) roomPassword = it
                                    },
                                    modifier = Modifier.weight(1f),
                                    singleLine = true,
                                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                    textStyle = TextStyle(color = TextDark, fontSize = 14.sp, fontWeight = FontWeight.Bold),
                                    cursorBrush = SolidColor(PurpleBrand),
                                    decorationBox = { innerTextField ->
                                        if (roomPassword.isEmpty()) {
                                            Text("Set room password (min 4 chars)", color = TextSecondary, fontSize = 13.sp)
                                        }
                                        innerTextField()
                                    },
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    text = if (passwordVisible) "HIDE" else "SHOW",
                                    color = PurpleBrand,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.clickable { passwordVisible = !passwordVisible },
                                )
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(18.dp))

            // ── 8. Collapsible Advanced Settings ──
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showAdvanced = !showAdvanced }
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Advanced Settings",
                        color = PurpleBrand,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = if (showAdvanced) "▲" else "▼",
                        color = PurpleBrand,
                        fontSize = 11.sp,
                    )
                }

                AnimatedVisibility(visible = showAdvanced) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(CardWhite)
                            .border(1.dp, BorderLavender, RoundedCornerShape(14.dp))
                            .padding(12.dp),
                    ) {
                        Text("Room Announcement", color = TextDark, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(ChipInactiveBg)
                                .padding(10.dp),
                        ) {
                            BasicTextField(
                                value = announcement,
                                onValueChange = { announcement = it },
                                modifier = Modifier.fillMaxWidth(),
                                textStyle = TextStyle(color = TextDark, fontSize = 13.sp),
                                decorationBox = { inner ->
                                    if (announcement.isEmpty()) {
                                        Text("Welcome message for entering guests...", color = TextSecondary, fontSize = 12.sp)
                                    }
                                    inner()
                                },
                            )
                        }

                        Spacer(Modifier.height(10.dp))
                        Text(
                            text = "• Partner Seat: Disabled by default (Enable anytime from Host Controls)",
                            color = TextSecondary,
                            fontSize = 11.sp,
                        )
                        Text(
                            text = "• Attach Seats: Host can bond any 2 regular occupied seats on stage",
                            color = TextSecondary,
                            fontSize = 11.sp,
                        )
                    }
                }
            }

            Spacer(Modifier.height(24.dp))
        }

        // ── Sticky Bottom Create Button ──
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        listOf(Color.Transparent, BgPearl.copy(alpha = 0.95f), BgPearl)
                    )
                )
                .padding(horizontal = 16.dp, vertical = 14.dp),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .clip(RoundedCornerShape(25.dp))
                    .background(
                        if (isFormValid && !isLoading) {
                            Brush.horizontalGradient(listOf(PurpleBrand, CyanBrand))
                        } else {
                            SolidColor(Color(0xFFD4CDE8))
                        }
                    )
                    .bounceClick(enabled = !isLoading) {
                        hasAttemptedSubmit = true
                        if (isFormValid) {
                            isLoading = true
                        }
                    },
                contentAlignment = Alignment.Center,
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.5.dp,
                    )
                } else {
                    Text(
                        text = "Create Room Now",
                        color = if (isFormValid) Color.White else Color(0xFF6C628A),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    }
}
