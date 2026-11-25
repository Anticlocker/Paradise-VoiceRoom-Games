package com.sundram.weplay.voiceroom.theme

import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sundram.weplay.R
import com.sundram.weplay.data.UserProfile

private val SheetInk = Color(0xFFF7F4FF)
private val SheetSecondary = Color(0xFFB8B0CC)
private val SheetSurface = Color(0xFF201A36)
private val SheetCard = Color(0xFF2B2347)
private val SheetBorder = Color(0xFF433968)
private val SheetAccent = Color(0xFF9B82E9)
private val SheetMint = Color(0xFF42D6A0)
private val SheetGold = Color(0xFFFFD36B)
private val SheetRose = Color(0xFFEF5578)

object RoomThemeServiceProvider {
    @Volatile
    private var cachedRepo: RoomThemeRepository? = null

    fun repository(context: android.content.Context): RoomThemeRepository =
        cachedRepo ?: synchronized(this) {
            cachedRepo ?: RoomThemeRepository(SharedPrefsRoomThemeStorage(context.applicationContext)).also {
                cachedRepo = it
            }
        }

    fun crestStore(context: android.content.Context): RoomCrestPreferencesStore =
        RoomCrestPreferencesStore(context.applicationContext)
}

/**
 * Reusable 4-tab Room Themes gallery used inside BOTH Live Voice Room ("Room Themes")
 * and Profile Shop ("Room Themes").
 */
@Composable
fun RoomThemesGalleryScreen(
    roomId: String,
    profile: UserProfile,
    isHostOrAuthorized: Boolean,
    initialTab: RoomThemeTab = RoomThemeTab.ALL,
    onOpenPreview: (String) -> Unit,
    onOpenCrestCenter: () -> Unit,
    onEquipFeedback: (ThemeActionResult) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val repo = remember(context) { RoomThemeServiceProvider.repository(context) }
    val crestStore = remember(context) { RoomThemeServiceProvider.crestStore(context) }
    var refreshTick by remember { mutableIntStateOf(0) }
    var selectedTab by rememberSaveable { mutableStateOf(initialTab) }
    var statusBanner by remember { mutableStateOf<String?>(null) }

    val crestProgress = remember(roomId, refreshTick) { crestStore.load(roomId) }
    val items = remember(roomId, profile.id, crestProgress.level, refreshTick) {
        repo.syncUnlockedThemesForCrest(roomId, crestProgress.level)
        repo.buildCatalogForRoom(roomId, profile.id, crestProgress.level)
    }
    val filtered = remember(items, selectedTab) { repo.filterByTab(items, selectedTab) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF151226))
            .statusBarsPadding()
            .navigationBarsPadding(),
    ) {
        // Header bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(SheetCard)
                    .border(1.dp, SheetBorder, CircleShape)
                    .clickable(onClick = onClose),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_back),
                    contentDescription = "Back",
                    tint = SheetInk,
                    modifier = Modifier.size(16.dp),
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("Room Themes", color = SheetInk, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text(
                    text = "Classic Lounge is free default • Higher themes unlock via Crest",
                    color = SheetSecondary,
                    fontSize = 11.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .background(SheetCard)
                    .border(1.dp, SheetGold.copy(alpha = 0.45f), RoundedCornerShape(14.dp))
                    .clickable(onClick = onOpenCrestCenter)
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("Crest Lv.${crestProgress.level}", color = SheetGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }

        // Honest feedback strip
        statusBanner?.let { msg ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(SheetCard)
                    .border(1.dp, SheetAccent.copy(alpha = 0.6f), RoundedCornerShape(10.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
            ) {
                Text(msg, color = SheetInk, fontSize = 12.sp)
            }
        }

        // 4 Category Tabs
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(RoomThemeTab.entries) { tab ->
                val active = tab == selectedTab
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (active) SheetAccent else SheetCard)
                        .border(1.dp, if (active) SheetAccent else SheetBorder, RoundedCornerShape(16.dp))
                        .clickable { selectedTab = tab }
                        .padding(horizontal = 14.dp, vertical = 7.dp),
                ) {
                    Text(
                        text = tab.title,
                        color = SheetInk,
                        fontSize = 12.sp,
                        fontWeight = if (active) FontWeight.Bold else FontWeight.Medium,
                    )
                }
            }
        }

        // Grid of Theme Cards
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 6.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(filtered, key = { it.spec.id }) { item ->
                RoomThemeCatalogCard(
                    item = item,
                    onPreview = { onOpenPreview(item.spec.id) },
                    onAction = {
                        when {
                            item.canEquip -> {
                                val res = repo.equipTheme(
                                    roomId = roomId,
                                    themeId = item.spec.id,
                                    userId = profile.id,
                                    crestLevel = crestProgress.level,
                                    isHostOrAuthorized = isHostOrAuthorized,
                                )
                                statusBanner = when (res) {
                                    is ThemeActionResult.Success -> res.message
                                    is ThemeActionResult.Locked -> res.reason
                                    is ThemeActionResult.Unavailable -> res.reason
                                    is ThemeActionResult.InsufficientCoins -> "Need ${res.requiredCoins} coins."
                                    is ThemeActionResult.Unauthorized -> res.reason
                                }
                                refreshTick++
                                onEquipFeedback(res)
                            }
                            item.spec.unlockType == ThemeUnlockType.CREST -> {
                                statusBanner = item.lockReason ?: "Requires higher Room Crest level."
                                onOpenCrestCenter()
                            }
                            else -> {
                                val res = repo.purchaseStoreTheme(
                                    themeId = item.spec.id,
                                    userId = profile.id,
                                    currentCoinBalance = profile.coins,
                                    onDeductCoins = { false },
                                )
                                statusBanner = when (res) {
                                    is ThemeActionResult.Unavailable -> res.reason
                                    is ThemeActionResult.Locked -> res.reason
                                    is ThemeActionResult.InsufficientCoins ->
                                        "Insufficient coins (${res.requiredCoins} needed)."
                                    is ThemeActionResult.Success -> res.message
                                    is ThemeActionResult.Unauthorized -> res.reason
                                }
                            }
                        }
                    },
                )
            }
        }
    }
}

@Composable
private fun RoomThemeCatalogCard(
    item: RoomThemeCatalogItem,
    onPreview: () -> Unit,
    onAction: () -> Unit,
) {
    val badgeText = when {
        item.isEquipped -> "Equipped"
        item.spec.unlockType == ThemeUnlockType.FREE -> "Free"
        item.canEquip -> "Unlocked"
        item.spec.unlockType == ThemeUnlockType.CREST -> "Crest Lv.${item.spec.requiredCrestLevel}"
        item.spec.priceCoins == null -> "Store · Price Unset"
        else -> "${item.spec.priceCoins} Coins"
    }
    val actionLabel = when {
        item.isEquipped -> "Active"
        item.canEquip -> "Equip"
        item.spec.unlockType == ThemeUnlockType.CREST -> "View Crest"
        item.isPurchaseConfigured -> "Unlock"
        else -> "Unavailable"
    }

    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(SheetCard)
            .border(
                width = if (item.isEquipped) 1.5.dp else 1.dp,
                color = if (item.isEquipped) SheetMint else SheetBorder,
                shape = RoundedCornerShape(16.dp),
            )
            .padding(10.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(104.dp)
                .clip(RoundedCornerShape(12.dp))
                .clickable(onClick = onPreview),
        ) {
            Image(
                painter = painterResource(item.resources.thumbnailRes),
                contentDescription = item.spec.name,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
            )
            // Mini seat frame & host frame hint
            Image(
                painter = painterResource(item.resources.hostFrameRes),
                contentDescription = null,
                modifier = Modifier
                    .size(34.dp)
                    .align(Alignment.Center),
            )
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(6.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xCC161229))
                    .padding(horizontal = 7.dp, vertical = 3.dp),
            ) {
                Text(
                    text = badgeText,
                    color = if (item.isEquipped) SheetMint else SheetInk,
                    fontSize = 9.5.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }

        Spacer(Modifier.height(8.dp))
        Text(
            text = item.spec.name,
            color = SheetInk,
            fontSize = 13.5.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            text = item.lockReason ?: if (item.spec.isClassicDefault) "Calm default room theme" else "Unlocked for your room",
            color = SheetSecondary,
            fontSize = 10.5.sp,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            lineHeight = 13.sp,
            modifier = Modifier.height(28.dp),
        )

        Spacer(Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(32.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF1D1833))
                    .border(1.dp, SheetBorder, RoundedCornerShape(10.dp))
                    .clickable(onClick = onPreview),
                contentAlignment = Alignment.Center,
            ) {
                Text("Preview", color = SheetInk, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(32.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        when {
                            item.isEquipped -> SheetMint.copy(alpha = 0.22f)
                            item.canEquip -> SheetAccent
                            else -> Color(0xFF332A52)
                        }
                    )
                    .clickable(enabled = !item.isEquipped, onClick = onAction),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = actionLabel,
                    color = if (item.isEquipped) SheetMint else SheetInk,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                )
            }
        }
    }
}

/**
 * Isolated full-screen Theme Preview Mode.
 * Uses strictly local sample stage state; never mutates live room seats, LiveKit, wallet, or DB.
 */
@Composable
fun RoomThemePreviewScreen(
    themeId: String,
    roomId: String,
    profile: UserProfile,
    isHostOrAuthorized: Boolean,
    onClose: () -> Unit,
    onEquippedSuccess: (String) -> Unit,
    onOpenCrestCenter: () -> Unit,
) {
    val context = LocalContext.current
    val repo = remember(context) { RoomThemeServiceProvider.repository(context) }
    val crestStore = remember(context) { RoomThemeServiceProvider.crestStore(context) }
    val crestLevel = remember(roomId) { crestStore.load(roomId).level }

    val catalogItem = remember(roomId, profile.id, crestLevel, themeId) {
        repo.buildCatalogForRoom(roomId, profile.id, crestLevel).firstOrNull { it.spec.id == themeId }
            ?: repo.buildCatalogForRoom(roomId, profile.id, crestLevel).first()
    }
    var previewPartner by remember { mutableStateOf(false) }
    var previewNotice by remember { mutableStateOf<String?>(null) }

    RoomThemeRenderer(themeId = catalogItem.spec.id) { spec, res, palette ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding(),
        ) {
            // Preview Mode Top Banner
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 10.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(palette.headerSurface)
                    .border(1.dp, palette.seatBorder, RoundedCornerShape(14.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Preview Mode · ${spec.name}",
                        color = palette.primaryText,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = "Sample layout only — live room, mic & wallet unchanged",
                        color = palette.secondaryText,
                        fontSize = 10.5.sp,
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(palette.seatSurface)
                        .clickable { previewPartner = !previewPartner }
                        .padding(horizontal = 8.dp, vertical = 5.dp),
                ) {
                    Text(
                        text = if (previewPartner) "Partner On" else "Partner Off",
                        color = palette.primaryText,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
                Spacer(Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(palette.seatSurface)
                        .clickable(onClick = onClose),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_close),
                        contentDescription = "Close preview",
                        tint = palette.primaryText,
                        modifier = Modifier.size(14.dp),
                    )
                }
            }

            // Sample Host + Optional Partner Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                PreviewSeatTile(
                    label = "Host (Sample)",
                    frameRes = res.hostFrameRes,
                    avatarRes = R.drawable.me_clean_demo_avatar_01,
                    isHost = true,
                    palette = palette,
                )
                if (previewPartner) {
                    Spacer(Modifier.width(24.dp))
                    PreviewSeatTile(
                        label = "Partner",
                        frameRes = res.hostFrameRes,
                        avatarRes = R.drawable.me_clean_demo_avatar_02,
                        isHost = false,
                        palette = palette,
                    )
                }
            }

            // Sample 8 Regular Seats (2 rows x 4)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
            ) {
                if (!previewPartner) {
                    // Subtle attached seat link between sample seat 1 & seat 2
                    Canvas(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                    ) {
                        val y = size.height * 0.5f
                        drawLine(
                            color = palette.bondLine.copy(alpha = 0.55f),
                            start = Offset(size.width * 0.14f, y),
                            end = Offset(size.width * 0.38f, y),
                            strokeWidth = 2.5f,
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 6f)),
                        )
                    }
                }
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    for (rowIndex in 0..1) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            for (col in 1..4) {
                                val seatNo = rowIndex * 4 + col
                                val avatar = when (seatNo) {
                                    1 -> R.drawable.me_clean_demo_avatar_03
                                    2 -> R.drawable.me_clean_demo_avatar_04
                                    else -> null
                                }
                                Box(
                                    modifier = Modifier.weight(1f),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    PreviewSeatTile(
                                        label = when (seatNo) {
                                            1 -> "Arjun"
                                            2 -> "Tara"
                                            else -> "$seatNo"
                                        },
                                        frameRes = res.seatFrameRes,
                                        avatarRes = avatar,
                                        isHost = false,
                                        palette = palette,
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Sample Chat Card
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp, vertical = 10.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(palette.cardSurface)
                    .border(1.dp, palette.seatBorder.copy(alpha = 0.55f), RoundedCornerShape(16.dp))
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Text("Room Notice", color = palette.accent, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Text(
                    text = "Welcome! Previewing ${spec.name} contrast and seat framing.",
                    color = palette.primaryText,
                    fontSize = 12.sp,
                )
                Text(
                    text = "Arjun: Background artwork and seat borders look super clean!",
                    color = palette.secondaryText,
                    fontSize = 11.5.sp,
                )
                previewNotice?.let { notice ->
                    Spacer(Modifier.height(6.dp))
                    Text(notice, color = SheetGold, fontSize = 11.5.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            // Bottom Action Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xDD151226))
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .height(44.dp)
                        .clip(RoundedCornerShape(22.dp))
                        .background(SheetCard)
                        .border(1.dp, SheetBorder, RoundedCornerShape(22.dp))
                        .clickable(onClick = onClose)
                        .padding(horizontal = 18.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text("Close", color = SheetInk, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .clip(RoundedCornerShape(22.dp))
                        .background(if (catalogItem.canEquip) SheetAccent else Color(0xFF332A52))
                        .clickable {
                            when {
                                catalogItem.isEquipped -> previewNotice = "${spec.name} is already equipped."
                                catalogItem.canEquip -> {
                                    when (
                                        val resAction = repo.equipTheme(
                                            roomId = roomId,
                                            themeId = spec.id,
                                            userId = profile.id,
                                            crestLevel = crestLevel,
                                            isHostOrAuthorized = isHostOrAuthorized,
                                        )
                                    ) {
                                        is ThemeActionResult.Success -> onEquippedSuccess(spec.id)
                                        is ThemeActionResult.Unauthorized -> previewNotice = resAction.reason
                                        is ThemeActionResult.Locked -> previewNotice = resAction.reason
                                        is ThemeActionResult.Unavailable -> previewNotice = resAction.reason
                                        is ThemeActionResult.InsufficientCoins -> previewNotice = "Insufficient coins."
                                    }
                                }
                                spec.unlockType == ThemeUnlockType.CREST -> {
                                    previewNotice = "Requires Room Crest Lv.${spec.requiredCrestLevel}."
                                    onOpenCrestCenter()
                                }
                                else -> {
                                    previewNotice = catalogItem.lockReason
                                        ?: "Purchase currently unavailable (price not configured)."
                                }
                            }
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = when {
                            catalogItem.isEquipped -> "Currently Equipped"
                            catalogItem.canEquip -> "Apply Theme"
                            spec.unlockType == ThemeUnlockType.CREST -> "Locked · Requires Crest Lv.${spec.requiredCrestLevel}"
                            spec.priceCoins == null -> "Purchase Unavailable (Price Unset)"
                            else -> "Unlock · ${spec.priceCoins} Coins"
                        },
                        color = SheetInk,
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }
}

@Composable
private fun PreviewSeatTile(
    label: String,
    frameRes: Int,
    avatarRes: Int?,
    isHost: Boolean,
    palette: RoomThemePalette,
) {
    val size = if (isHost) 64.dp else 52.dp
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(modifier = Modifier.size(size), contentAlignment = Alignment.Center) {
            if (avatarRes != null) {
                Image(
                    painter = painterResource(avatarRes),
                    contentDescription = label,
                    modifier = Modifier
                        .size(if (isHost) 48.dp else 40.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop,
                )
                Image(
                    painter = painterResource(frameRes),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit,
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(palette.seatSurface)
                        .border(1.dp, palette.seatBorder, CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Text("+", color = palette.secondaryText, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                }
            }
        }
        Spacer(Modifier.height(3.dp))
        Text(
            text = label,
            color = palette.primaryText,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

/**
 * Room Crest Progression Center — tracks room crest XP, level, upcoming unlocked themes,
 * anti-farming safeguards, and recent crest activity. Separate from Personal/VIP/Family level.
 */
@Composable
fun RoomCrestCenterScreen(
    roomId: String,
    roomName: String,
    hostId: String,
    currentUserId: String,
    isHostOrMod: Boolean,
    onPreviewTheme: (String) -> Unit,
    onClose: () -> Unit,
) {
    val context = LocalContext.current
    val crestStore = remember(context) { RoomCrestPreferencesStore(context) }
    val repo = remember(context) { RoomThemeServiceProvider.repository(context) }
    var progress by remember(roomId) { mutableStateOf(crestStore.load(roomId)) }
    var feedback by remember { mutableStateOf<String?>(null) }

    val upcomingThemes = remember(progress.level) {
        RoomThemeCatalog.entries.filter { it.unlockType == ThemeUnlockType.CREST }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF151226))
            .statusBarsPadding()
            .navigationBarsPadding(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(SheetCard)
                        .border(1.dp, SheetBorder, CircleShape)
                        .clickable(onClick = onClose),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_back),
                        contentDescription = "Back",
                        tint = SheetInk,
                        modifier = Modifier.size(16.dp),
                    )
                }
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("Room Crest Center", color = SheetInk, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text(roomName, color = SheetSecondary, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            }
        }

        // Crest Level Card
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(Brush.verticalGradient(listOf(Color(0xFF2B224B), Color(0xFF1E1736))))
                    .border(1.dp, SheetGold.copy(alpha = 0.4f), RoundedCornerShape(18.dp))
                    .padding(16.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column {
                        Text("Crest Level ${progress.level}", color = SheetGold, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
                        Text(
                            text = "${progress.xp} XP · Next level at ${progress.nextLevelXp} XP",
                            color = SheetSecondary,
                            fontSize = 12.sp,
                        )
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0x33FFD36B))
                            .padding(horizontal = 10.dp, vertical = 5.dp),
                    ) {
                        Text(
                            text = "${progress.dailyXpEarned}/${CrestProgressionEngine.DAILY_XP_CAP} Today",
                            color = SheetGold,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }

                Spacer(Modifier.height(12.dp))
                LinearProgressIndicator(
                    progress = { progress.progressFraction },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = SheetGold,
                    trackColor = Color(0xFF342A57),
                )
                Spacer(Modifier.height(10.dp))
                Text(
                    text = "Room Crest belongs to this voice room and is separate from your Personal Level, VIP rank, and Family tier. Unlocking a Crest theme grants eligibility without overriding your current theme.",
                    color = SheetSecondary,
                    fontSize = 11.sp,
                    lineHeight = 15.sp,
                )
            }
        }

        // Honest Offline Progression Test Controls
        if (isHostOrMod) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(SheetCard)
                        .border(1.dp, SheetBorder, RoundedCornerShape(14.dp))
                        .padding(12.dp),
                ) {
                    Text("Local Crest Progression Simulator", color = SheetInk, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Text(
                        text = "Exercises real level thresholds & anti-farming guards locally when offline.",
                        color = SheetSecondary,
                        fontSize = 11.sp,
                    )
                    Spacer(Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        @Composable
                        fun SimBtn(label: String, onClick: () -> Unit) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(SheetAccent.copy(alpha = 0.25f))
                                    .border(1.dp, SheetAccent.copy(alpha = 0.6f), RoundedCornerShape(10.dp))
                                    .clickable(onClick = onClick)
                                    .padding(horizontal = 10.dp, vertical = 6.dp),
                            ) {
                                Text(label, color = SheetInk, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }

                        SimBtn("+120 Host XP") {
                            val eventId = "host_${System.currentTimeMillis()}"
                            val outcome = CrestProgressionEngine.applyEvent(
                                current = progress,
                                eventId = eventId,
                                type = RoomCrestEventType.HOST_SESSION_MINUTE,
                                actorId = currentUserId,
                                hostId = hostId,
                                requestedDelta = 120,
                                summary = "Active room hosting activity",
                                timestampMillis = System.currentTimeMillis(),
                                seenEventIds = crestStore.loadSeenEventIds(roomId),
                                alreadyUnlockedIds = repo.buildCatalogForRoom(roomId, currentUserId, progress.level)
                                    .filter { it.canEquip }.map { it.spec.id }.toSet(),
                            )
                            progress = outcome.progress
                            crestStore.save(progress)
                            if (outcome.accepted) {
                                crestStore.addSeenEventId(roomId, eventId)
                                repo.syncUnlockedThemesForCrest(roomId, progress.level)
                            }
                            feedback = if (outcome.accepted) {
                                val unlockedMsg = if (outcome.newlyUnlockedThemeIds.isNotEmpty()) {
                                    " Unlocked: ${outcome.newlyUnlockedThemeIds.joinToString()}."
                                } else ""
                                "Awarded +120 XP (Crest Lv.${progress.level}).$unlockedMsg"
                            } else {
                                outcome.rejectionReason
                            }
                        }

                        SimBtn("Test Self-Gift") {
                            val eventId = "selfgift_${System.currentTimeMillis()}"
                            val outcome = CrestProgressionEngine.applyEvent(
                                current = progress,
                                eventId = eventId,
                                type = RoomCrestEventType.GIFT_VALUE_RECEIVED,
                                actorId = hostId,
                                hostId = hostId,
                                requestedDelta = 200,
                                summary = "Host self-gift attempt (blocked)",
                                timestampMillis = System.currentTimeMillis(),
                                seenEventIds = crestStore.loadSeenEventIds(roomId),
                                alreadyUnlockedIds = emptySet(),
                            )
                            progress = outcome.progress
                            crestStore.save(progress)
                            feedback = outcome.rejectionReason
                        }
                    }
                    feedback?.let { msg ->
                        Spacer(Modifier.height(6.dp))
                        Text(msg, color = SheetGold, fontSize = 11.sp)
                    }
                }
            }
        }

        // Theme Unlock Roadmap
        item {
            Text("Crest Theme Roadmap", color = SheetInk, fontSize = 15.sp, fontWeight = FontWeight.Bold)
        }
        items(upcomingThemes, key = { it.id }) { spec ->
            val req = spec.requiredCrestLevel ?: 1
            val unlocked = progress.level >= req
            val res = RoomThemeResourceRegistry.resourcesFor(spec.id)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(SheetCard)
                    .border(
                        1.dp,
                        if (unlocked) SheetMint.copy(alpha = 0.6f) else SheetBorder,
                        RoundedCornerShape(14.dp),
                    )
                    .clickable { onPreviewTheme(spec.id) }
                    .padding(10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Image(
                    painter = painterResource(res.thumbnailRes),
                    contentDescription = spec.name,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(10.dp)),
                    contentScale = ContentScale.Crop,
                )
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(spec.name, color = SheetInk, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Text(
                        text = if (unlocked) "Unlocked at Crest Lv.$req" else "Requires Crest Lv.$req",
                        color = if (unlocked) SheetMint else SheetSecondary,
                        fontSize = 11.5.sp,
                    )
                }
                Text(
                    text = if (unlocked) "Preview ›" else "Lv.$req",
                    color = if (unlocked) SheetMint else SheetGold,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }

        // Recent Crest Activity
        item {
            Text("Recent Crest Events", color = SheetInk, fontSize = 15.sp, fontWeight = FontWeight.Bold)
        }
        if (progress.recentEvents.isEmpty()) {
            item {
                Text(
                    text = "No room crest events recorded yet for this room.",
                    color = SheetSecondary,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Start,
                )
            }
        } else {
            items(progress.recentEvents, key = { it.id }) { ev ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(SheetSurface)
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(ev.summary, color = SheetInk, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        ev.rejectionReason?.let {
                            Text(it, color = SheetRose, fontSize = 10.5.sp)
                        }
                    }
                    Text(
                        text = if (ev.accepted) "+${ev.xpDelta} XP" else "Blocked",
                        color = if (ev.accepted) SheetMint else SheetRose,
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    }
}
