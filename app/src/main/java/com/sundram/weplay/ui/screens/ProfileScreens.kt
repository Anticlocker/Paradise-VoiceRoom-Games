package com.sundram.weplay.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.sundram.weplay.R
import com.sundram.weplay.data.BackpackItem
import com.sundram.weplay.data.DemoBackpack
import com.sundram.weplay.data.DemoGifts
import com.sundram.weplay.data.GiftItem
import com.sundram.weplay.data.UserPrefs
import com.sundram.weplay.data.UserProfile
import com.sundram.weplay.data.Demo
import com.sundram.weplay.voiceroom.theme.RoomThemesGalleryScreen
import com.sundram.weplay.voiceroom.theme.RoomThemeTab
import com.sundram.weplay.data.ageFromDob
import com.sundram.weplay.data.formatDob
import com.sundram.weplay.ui.components.DobFieldRow
import com.sundram.weplay.ui.components.DobPickerDialog
import com.sundram.weplay.ui.components.FieldLabel
import com.sundram.weplay.ui.components.PlainField
import com.sundram.weplay.ui.components.PrimaryButton
import com.sundram.weplay.ui.components.TopBackBar
import com.sundram.weplay.ui.components.ProfilePhotoPicker
import com.sundram.weplay.ui.components.UserAvatar
import com.sundram.weplay.ui.nav.Routes
import com.sundram.weplay.ui.theme.BgSoft
import com.sundram.weplay.ui.theme.CardWhite
import com.sundram.weplay.ui.theme.ChipBg
import com.sundram.weplay.ui.theme.CyanBrand
import com.sundram.weplay.ui.theme.CyanInk
import com.sundram.weplay.ui.theme.CyanPool
import com.sundram.weplay.ui.theme.Gold
import com.sundram.weplay.ui.theme.LudoBlue
import com.sundram.weplay.ui.theme.MintGreen
import com.sundram.weplay.ui.theme.OnBrand
import com.sundram.weplay.ui.theme.OnBrandDim
import com.sundram.weplay.ui.theme.PinkGift
import com.sundram.weplay.ui.theme.Stroke
import com.sundram.weplay.ui.theme.TextBody
import com.sundram.weplay.ui.theme.TextMain
import com.sundram.weplay.ui.theme.TextMuted
import com.sundram.weplay.ui.theme.WarnOrange

@Composable
internal fun SubScreen(
    nav: NavHostController,
    title: String,
    sub: String,
    content: @Composable () -> Unit,
) {
    Column(
        Modifier
            .fillMaxSize()
            .background(BgSoft)
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
    ) {
        TopBackBar(nav, title, sub)
        Spacer(Modifier.height(18.dp))
        content()
        Spacer(Modifier.height(28.dp))
    }
}

@Composable
fun ProfileEditScreen(nav: NavHostController, profile: UserProfile, onSave: (UserProfile) -> Unit) {
    var nickname by remember { mutableStateOf(profile.nickname.ifBlank { profile.name }) }
    var dob by remember { mutableStateOf(profile.dobMillis) }
    var photo by remember { mutableStateOf(profile.photoPath) }
    var gender by remember { mutableStateOf(profile.gender.ifBlank { "Not specified" }) }
    var region by remember { mutableStateOf(profile.region) }
    var signature by remember { mutableStateOf(profile.signature) }
    var priceTag by remember { mutableStateOf(profile.priceTag) }
    var title by remember { mutableStateOf(profile.title) }
    var badge by remember { mutableStateOf(profile.badge) }
    var qrContactCard by remember { mutableStateOf(profile.qrContactCard) }
    var showDate by remember { mutableStateOf(false) }
    var showGender by remember { mutableStateOf(false) }
    val genderOptions = listOf("Not specified", "Male", "Female", "Other")

    val pickPhoto = ProfilePhotoPicker { photo = it }

    SubScreen(nav, "Edit profile", "Avatar, nickname and profile details") {
        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            UserAvatar(nickname.ifBlank { "?" }, photo, size = 104.dp)
        }
        Spacer(Modifier.height(10.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            Text(
                "Change avatar",
                color = CyanInk,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.clickable(onClick = pickPhoto).padding(6.dp),
            )
            if (photo != null) {
                Text(
                    "Remove",
                    color = TextBody,
                    fontSize = 13.sp,
                    modifier = Modifier.clickable { photo = null }.padding(6.dp),
                )
            }
        }

        ProfileSectionTitle("Basic info")
        FieldLabel("Nickname")
        Spacer(Modifier.height(8.dp))
        PlainField(nickname, { nickname = it.take(24) }, "Your nickname")
        Spacer(Modifier.height(14.dp))
        ReadOnlyProfileField("User ID", profile.id)
        Spacer(Modifier.height(14.dp))
        ReadOnlyProfileField("Level", profile.level.toString())
        Spacer(Modifier.height(14.dp))
        FieldLabel("Gender")
        Spacer(Modifier.height(8.dp))
        ProfileSelectRow(value = gender, placeholder = "Select gender") { showGender = true }
        Spacer(Modifier.height(14.dp))
        FieldLabel("Birthday")
        Spacer(Modifier.height(8.dp))
        DobFieldRow(
            dobMillis = dob,
            trailing = if (dob > 0L) "Age ${ageFromDob(dob)}" else null,
            onClick = { showDate = true },
        )
        Spacer(Modifier.height(14.dp))
        FieldLabel("Region")
        Spacer(Modifier.height(8.dp))
        PlainField(region, { region = it.take(40) }, "City or region")

        ProfileSectionTitle("Profile card")
        FieldLabel("QR contact card")
        Spacer(Modifier.height(8.dp))
        PlainField(qrContactCard, { qrContactCard = it.take(120) }, "Contact card link or code")
        Spacer(Modifier.height(14.dp))
        FieldLabel("Signature")
        Spacer(Modifier.height(8.dp))
        PlainField(signature, { signature = it.take(80) }, "Write a short signature")
        Spacer(Modifier.height(14.dp))
        FieldLabel("Price tag")
        Spacer(Modifier.height(8.dp))
        PlainField(priceTag, { priceTag = it.take(24) }, "Text label")
        Spacer(Modifier.height(14.dp))
        FieldLabel("Title")
        Spacer(Modifier.height(8.dp))
        PlainField(title, { title = it.take(24) }, "Profile title")
        Spacer(Modifier.height(14.dp))
        FieldLabel("Badge")
        Spacer(Modifier.height(8.dp))
        PlainField(badge, { badge = it.take(24) }, "Badge label")

        Spacer(Modifier.height(22.dp))
        PrimaryButton("Save changes", enabled = nickname.trim().length >= 2) {
            val cleanNickname = nickname.trim()
            onSave(
                profile.copy(
                    name = cleanNickname,
                    nickname = cleanNickname,
                    dobMillis = dob,
                    photoPath = photo,
                    gender = gender,
                    region = region.trim(),
                    signature = signature.trim(),
                    priceTag = priceTag.trim(),
                    title = title.trim(),
                    badge = badge.trim(),
                    qrContactCard = qrContactCard.trim(),
                )
            )
            nav.popBackStack()
        }
        Spacer(Modifier.height(12.dp))
        Text("Your profile is stored on this device.", color = TextMuted, fontSize = 11.sp)
    }

    if (showDate) {
        DobPickerDialog(initialMillis = dob, onConfirm = { dob = it }, onDismiss = { showDate = false })
    }
    if (showGender) {
        AlertDialog(
            onDismissRequest = { showGender = false },
            containerColor = CardWhite,
            title = { Text("Gender", color = TextMain, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    genderOptions.forEach { option ->
                        Text(
                            option,
                            color = if (option == gender) CyanBrand else TextMain,
                            fontSize = 15.sp,
                            fontWeight = if (option == gender) FontWeight.SemiBold else FontWeight.Normal,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { gender = option; showGender = false }
                                .padding(vertical = 10.dp),
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showGender = false }) { Text("Close", color = TextMuted) }
            },
        )
    }
}

@Composable
private fun ProfileSectionTitle(text: String) {
    Spacer(Modifier.height(20.dp))
    Text(text, color = TextMain, fontSize = 16.sp, fontWeight = FontWeight.Bold)
    Spacer(Modifier.height(12.dp))
}

@Composable
private fun ReadOnlyProfileField(label: String, value: String) {
    FieldLabel(label)
    Spacer(Modifier.height(8.dp))
    Row(
        Modifier
            .fillMaxWidth()
            .background(ChipBg, RoundedCornerShape(14.dp))
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(value, color = TextMain, fontSize = 15.sp)
    }
}

@Composable
private fun ProfileSelectRow(value: String, placeholder: String, onClick: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .background(CardWhite, RoundedCornerShape(14.dp))
            .border(1.dp, Stroke, RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            value.ifBlank { placeholder },
            color = if (value.isBlank()) TextMuted else TextMain,
            fontSize = 15.sp,
            modifier = Modifier.weight(1f),
        )
        Icon(
            painter = painterResource(R.drawable.ic_chevron_right),
            contentDescription = null,
            tint = TextMuted,
            modifier = Modifier.size(16.dp),
        )
    }
}

@Composable
fun GiftsScreen(
    nav: NavHostController,
    profile: UserProfile,
    initialShowRoomThemes: Boolean = false,
) {
    val context = LocalContext.current
    val prefs = remember { UserPrefs(context) }
    var activeShopTab by rememberSaveable { mutableIntStateOf(if (initialShowRoomThemes) 1 else 0) }
    var counts by remember {
        mutableStateOf(DemoGifts.associate { it.id to prefs.giftCount(it.id) })
    }
    var claimed by remember { mutableStateOf(prefs.claimedToday()) }
    var selected by remember { mutableStateOf<GiftItem?>(null) }

    if (activeShopTab == 1) {
        val defaultRoomId = Demo.rooms.first().id
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF151226)),
        ) {
            // Switcher bar between Gifts and Room Themes inside Shop
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFF231D3D))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                listOf("Gifts", "Room Themes").forEachIndexed { idx, label ->
                    val sel = idx == activeShopTab
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (sel) Color(0xFF9B82E9) else Color.Transparent)
                            .clickable { activeShopTab = idx }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = label,
                            color = Color(0xFFF7F4FF),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }
            RoomThemesGalleryScreen(
                roomId = defaultRoomId,
                profile = profile,
                isHostOrAuthorized = true,
                initialTab = RoomThemeTab.ALL,
                onOpenPreview = { themeId ->
                    nav.navigate(Routes.roomThemePreviewRoute(defaultRoomId, themeId))
                },
                onOpenCrestCenter = {
                    nav.navigate(Routes.roomCrestRoute(defaultRoomId))
                },
                onEquipFeedback = { },
                onClose = { nav.popBackStack() },
                modifier = Modifier.weight(1f),
            )
        }
        return
    }

    SubScreen(nav, "Shop & Gifts", "Gifts and voice room themes") {
        // Category Toggle: Gifts vs Room Themes
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(CardWhite)
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            listOf("Gifts", "Room Themes").forEachIndexed { idx, label ->
                val sel = idx == activeShopTab
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (sel) CyanBrand else Color.Transparent)
                        .clickable { activeShopTab = idx }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = label,
                        color = if (sel) OnBrand else TextBody,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
        Spacer(Modifier.height(12.dp))
        Row(
            Modifier
                .fillMaxWidth()
                .background(CardWhite, RoundedCornerShape(16.dp))
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_coin),
                contentDescription = null,
                tint = Gold,
                modifier = Modifier.size(30.dp),
            )
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text("%,d coins".format(profile.coins), color = TextMain, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text("Balance", color = TextBody, fontSize = 11.sp)
            }
            Text("${counts.values.sum()} gifts", color = TextBody, fontSize = 12.sp)
            Text(
                "Top up",
                color = OnBrand,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .background(CyanBrand, RoundedCornerShape(9.dp))
                    .clickable { nav.navigate(Routes.TOPUP) }
                    .padding(horizontal = 12.dp, vertical = 7.dp),
            )
        }

        Spacer(Modifier.height(12.dp))
        Row(
            Modifier
                .fillMaxWidth()
                .background(CyanBrand.copy(alpha = 0.12f), RoundedCornerShape(16.dp))
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(Modifier.weight(1f)) {
                Text("Daily gift", color = TextMain, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                Text(
                    if (claimed) "Come back tomorrow for another one." else "Claim one free gift today.",
                    color = TextBody,
                    fontSize = 11.sp,
                )
            }
            Text(
                if (claimed) "Claimed" else "Claim",
                color = if (claimed) TextBody else OnBrand,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .background(
                        if (claimed) CardWhite else CyanBrand,
                        RoundedCornerShape(10.dp),
                    )
                    .clickable(enabled = !claimed) {
                        val gift = DemoGifts.shuffled().first()
                        prefs.claimDailyGift(gift.id)
                        counts = counts.toMutableMap().also { it[gift.id] = prefs.giftCount(gift.id) }
                        claimed = true
                        Toast.makeText(context, "Claimed ${gift.name}", Toast.LENGTH_SHORT).show()
                    }
                    .padding(horizontal = 14.dp, vertical = 8.dp),
            )
        }

        Spacer(Modifier.height(18.dp))
        Text("Collection", color = TextMain, fontSize = 17.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(10.dp))

        DemoGifts.chunked(2).forEach { row ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                row.forEach { gift ->
                    GiftTile(
                        gift = gift,
                        count = counts[gift.id] ?: 0,
                        modifier = Modifier.weight(1f),
                    ) { selected = gift }
                }
                if (row.size == 1) Spacer(Modifier.weight(1f))
            }
            Spacer(Modifier.height(12.dp))
        }
    }

    val gift = selected
    if (gift != null) {
        val count = counts[gift.id] ?: 0
        AlertDialog(
            onDismissRequest = { selected = null },
            containerColor = CardWhite,
            title = { Text(gift.name, color = TextMain, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text(gift.desc, color = TextBody, fontSize = 13.sp, lineHeight = 19.sp)
                    Spacer(Modifier.height(10.dp))
                    Text(
                        "You own $count.",
                        color = TextMain,
                        fontSize = 12.sp,
                    )
                }
            },
            confirmButton = {
                TextButton(
                    enabled = count > 0,
                    onClick = {
                        prefs.setGiftCount(gift.id, count - 1)
                        counts = counts.toMutableMap().also { it[gift.id] = count - 1 }
                        selected = null
                        Toast.makeText(context, "Sent ${gift.name} to Tea Time Talks", Toast.LENGTH_SHORT).show()
                    },
                ) {
                    Text(
                        if (count > 0) "Send" else "None left",
                        color = if (count > 0) CyanBrand else TextMuted,
                        fontWeight = FontWeight.Bold,
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { selected = null }) { Text("Close", color = TextBody) }
            },
        )
    }
}

@Composable
private fun GiftTile(gift: GiftItem, count: Int, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Column(
        modifier
            .background(CardWhite, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(14.dp),
    ) {
        Box(
            Modifier
                .size(46.dp)
                .background(Color(gift.tint).copy(alpha = 0.16f), RoundedCornerShape(13.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(gift.icon),
                contentDescription = null,
                tint = Color(gift.tint),
                modifier = Modifier.size(24.dp),
            )
        }
        Spacer(Modifier.height(10.dp))
        Text(gift.name, color = TextMain, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(2.dp))
        Text("Owned $count", color = TextBody, fontSize = 11.sp)
    }
}



@Composable
fun BackpackScreen(nav: NavHostController, profile: UserProfile, onSave: (UserProfile) -> Unit) {
    var selected by remember { mutableStateOf<BackpackItem?>(null) }
    val equipped = DemoBackpack.firstOrNull { it.id == profile.frame }

    SubScreen(nav, "Backpack", "Frames, bubbles and badges") {
        Row(
            Modifier
                .fillMaxWidth()
                .background(CardWhite, RoundedCornerShape(16.dp))
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            UserAvatar(
                name = profile.name,
                photoPath = profile.photoPath,
                size = 56.dp,
                ring = equipped?.frame?.let { Color(it) },
            )
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text("Equipped", color = TextBody, fontSize = 11.sp)
                Text(
                    equipped?.name ?: "No frame",
                    color = TextMain,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }
            Text("Level ${profile.level}", color = CyanInk, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(Modifier.height(16.dp))
        DemoBackpack.chunked(2).forEach { row ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                row.forEach { item ->
                    BackpackTile(
                        item = item,
                        owned = profile.level >= item.level,
                        equipped = item.id == profile.frame,
                        modifier = Modifier.weight(1f),
                    ) { selected = item }
                }
                if (row.size == 1) Spacer(Modifier.weight(1f))
            }
            Spacer(Modifier.height(12.dp))
        }
    }

    val item = selected
    if (item != null) {
        val owned = profile.level >= item.level
        AlertDialog(
            onDismissRequest = { selected = null },
            containerColor = CardWhite,
            title = { Text(item.name, color = TextMain, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text(item.kind.uppercase(), color = CyanInk, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(6.dp))
                    Text(item.desc, color = TextBody, fontSize = 13.sp, lineHeight = 19.sp)
                    if (!owned) {
                        Spacer(Modifier.height(8.dp))
                        Text("Unlocks at level ${item.level}.", color = WarnOrange, fontSize = 12.sp)
                    }
                }
            },
            confirmButton = {
                val isFrame = item.frame != null
                TextButton(
                    enabled = owned,
                    onClick = {
                        if (isFrame) onSave(profile.copy(frame = item.id))
                        selected = null
                    },
                ) {
                    Text(
                        when {
                            !owned -> "Locked"
                            !isFrame -> "Owned"
                            item.id == profile.frame -> "Equipped"
                            else -> "Equip"
                        },
                        color = if (owned) CyanBrand else TextMuted,
                        fontWeight = FontWeight.Bold,
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { selected = null }) { Text("Close", color = TextBody) }
            },
        )
    }
}

@Composable
private fun BackpackTile(
    item: BackpackItem,
    owned: Boolean,
    equipped: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val tint = when (item.kind) {
        "Avatar frame" -> item.frame?.let { Color(it) } ?: CyanBrand
        "Chat bubble" -> LudoBlue
        "Badge" -> PinkGift
        else -> MintGreen
    }
    Column(
        modifier
            .background(CardWhite, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(14.dp),
    ) {
        Box(
            Modifier
                .size(46.dp)
                .background(tint.copy(alpha = 0.16f), RoundedCornerShape(13.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(
                    when (item.kind) {
                        "Avatar frame" -> R.drawable.ic_verified
                        "Chat bubble" -> R.drawable.ic_comment
                        "Badge" -> R.drawable.ic_star
                        else -> R.drawable.ic_bolt
                    }
                ),
                contentDescription = null,
                tint = if (owned) tint else TextMuted,
                modifier = Modifier.size(23.dp),
            )
        }
        Spacer(Modifier.height(10.dp))
        Text(
            item.name,
            color = if (owned) TextMain else TextBody,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
        )
        Spacer(Modifier.height(2.dp))
        Text(
            when {
                equipped -> "Equipped"
                owned -> "Owned"
                else -> "Level ${item.level}"
            },
            color = when {
                equipped -> CyanInk
                owned -> MintGreen
                else -> TextMuted
            },
            fontSize = 11.sp,
        )
    }
}

@Composable
fun AboutScreen(nav: NavHostController) {
    var dialog by remember { mutableStateOf<String?>(null) }

    SubScreen(nav, "About", "Paradise 0.1.0") {
        Column(
            Modifier
                .fillMaxWidth()
                .background(CardWhite, RoundedCornerShape(18.dp))
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                Modifier
                    .size(64.dp)
                    .background(
                        Brush.linearGradient(listOf(CyanBrand, CyanPool)),
                        RoundedCornerShape(18.dp),
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Text("P", color = OnBrand, fontSize = 32.sp, fontWeight = FontWeight.ExtraBold)
            }
            Spacer(Modifier.height(12.dp))
            Text("Paradise", color = TextMain, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text("Voice Room & Social Party Games", color = TextBody, fontSize = 12.sp)
            Spacer(Modifier.height(4.dp))
            Text("Version 0.1.0", color = TextMuted, fontSize = 11.sp)
        }

        Spacer(Modifier.height(18.dp))
        Text(
            "Paradise brings live voice rooms and multiplayer party games together in a celestial lounge. " +
                "In this local preview, rooms, players, and purchases are simulated on your device.",
            color = TextBody,
            fontSize = 13.sp,
            lineHeight = 20.sp,
        )

        Spacer(Modifier.height(18.dp))
        AboutRow("Terms of Service") { dialog = "terms" }
        AboutRow("Privacy Policy") { dialog = "privacy" }
        AboutRow("Open source licenses") { dialog = "licenses" }

        Spacer(Modifier.height(20.dp))
        Text("Built with Kotlin and Jetpack Compose.", color = TextMuted, fontSize = 11.sp)
    }

    dialog?.let { kind ->
        val (title, body) = when (kind) {
            "terms" -> "Terms of Service" to
                "Paradise is currently running in local preview mode. Rooms, players and games are simulated on this device. " +
                "Do not use this app for real-money trading or prohibited conduct."
            "privacy" -> "Privacy Policy" to
                "Nothing is uploaded. Your name, birthday, photo and settings stay in this app's " +
                "private storage and are removed when you log out."
            else -> "Open source licenses" to
                "This app uses AndroidX, Jetpack Compose and the Kotlin standard library, " +
                "all under the Apache 2.0 license."
        }
        AlertDialog(
            onDismissRequest = { dialog = null },
            containerColor = CardWhite,
            title = { Text(title, color = TextMain, fontWeight = FontWeight.Bold) },
            text = { Text(body, color = TextBody, fontSize = 13.sp, lineHeight = 20.sp) },
            confirmButton = {
                TextButton(onClick = { dialog = null }) { Text("Close", color = CyanBrand) }
            },
        )
    }
}

@Composable
private fun AboutRow(title: String, onClick: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 13.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(title, color = TextMain, fontSize = 14.sp, modifier = Modifier.weight(1f))
        Icon(
            painter = painterResource(R.drawable.ic_chevron_right),
            contentDescription = null,
            tint = TextBody,
            modifier = Modifier.size(16.dp),
        )
    }
}
