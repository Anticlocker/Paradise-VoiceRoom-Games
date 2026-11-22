package com.sundram.weplay.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.sundram.weplay.R
import com.sundram.weplay.data.Demo
import com.sundram.weplay.data.RoomCard
import com.sundram.weplay.data.UserPrefs
import com.sundram.weplay.ui.components.InitialsAvatar
import com.sundram.weplay.ui.components.bounceClick
import com.sundram.weplay.ui.components.navigateSafe
import com.sundram.weplay.ui.components.tagColors
import com.sundram.weplay.ui.nav.Routes
import com.sundram.weplay.ui.theme.AuroraBackground
import com.sundram.weplay.ui.theme.AuroraBorder
import com.sundram.weplay.ui.theme.AuroraCoral
import com.sundram.weplay.ui.theme.AuroraCyan
import com.sundram.weplay.ui.theme.AuroraVoiceRoomBackground
import com.sundram.weplay.ui.theme.AuroraGold
import com.sundram.weplay.ui.theme.AuroraGradients
import com.sundram.weplay.ui.theme.AuroraLavender
import com.sundram.weplay.ui.theme.AuroraOrchid
import com.sundram.weplay.ui.theme.AuroraRose
import com.sundram.weplay.ui.theme.AuroraSurface
import com.sundram.weplay.ui.theme.AuroraTextPrimary
import com.sundram.weplay.ui.theme.AuroraTextSecondary
import com.sundram.weplay.ui.theme.AuroraTurquoise
import com.sundram.weplay.ui.theme.CelestialViolet
import com.sundram.weplay.ui.theme.ChampagneGold
import com.sundram.weplay.ui.theme.DeepMidnight
import com.sundram.weplay.ui.theme.DreamLavender
import com.sundram.weplay.ui.theme.SoftPeach

private val LoungeTabs = listOf("Recent", "All", "Popular", "Related", "Following")
private val CategoryPills = listOf("All", "Friends", "PK", "Music", "Video", "Game", "Chats")

private fun formatVisitedAgo(timestamp: Long): String {
    val diff = (System.currentTimeMillis() - timestamp).coerceAtLeast(0)
    val mins = diff / (1000 * 60)
    return when {
        mins < 1 -> "Just now"
        mins < 60 -> "${mins}m ago"
        mins < 1440 -> "${mins / 60}h ago"
        else -> "${mins / 1440}d ago"
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VoiceRoomListScreen(nav: NavHostController, pad: PaddingValues) {
    val context = LocalContext.current
    val prefs = remember { UserPrefs(context) }
    var visitRefresh by remember { mutableStateOf(0) }

    var selectedLoungeTab by rememberSaveable { mutableStateOf("All") }
    var selectedCategory by rememberSaveable { mutableStateOf("All") }
    var isSearchActive by rememberSaveable { mutableStateOf(false) }
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var showGiftPackSheet by rememberSaveable { mutableStateOf(false) }

    // Multi-criteria room filtering & sorting (supporting Recent, All, Popular, Related, Following)
    val filteredRooms = remember(selectedLoungeTab, selectedCategory, searchQuery, visitRefresh) {
        when (selectedLoungeTab) {
            "Following" -> {
                emptyList<RoomCard>()
            }
            "Recent" -> {
                val visitedPairs = prefs.getRecentVisitedRooms().ifEmpty {
                    // Seed initial recent visited rooms if none recorded yet
                    listOf(
                        "r1" to (System.currentTimeMillis() - 15 * 60 * 1000L),
                        "r6" to (System.currentTimeMillis() - 90 * 60 * 1000L),
                    )
                }
                visitedPairs.mapNotNull { (roomId, timestamp) ->
                    val found = Demo.rooms.find { it.id == roomId }
                    if (found != null) {
                        found.copy(activityText = "Visited ${formatVisitedAgo(timestamp)} • ${found.activityText ?: ""}")
                    } else {
                        RoomCard(
                            id = roomId,
                            title = "Past Session ($roomId)",
                            host = "Host",
                            members = 0,
                            tag = "Casual",
                            live = false,
                            coverRes = R.drawable.vb_room_cover_moonlight,
                            activityText = "Ended • Visited ${formatVisitedAgo(timestamp)}",
                        )
                    }
                }.filter { room ->
                    if (searchQuery.isBlank()) true else {
                        room.title.contains(searchQuery, ignoreCase = true) ||
                                room.host.contains(searchQuery, ignoreCase = true)
                    }
                }
            }
            "Popular" -> {
                Demo.rooms
                    .filter { room ->
                        val matchesCategory = when (selectedCategory) {
                            "All" -> true
                            "Friends" -> room.tag == "Friends" || room.tag == "Casual"
                            "PK" -> room.tag == "PK" || room.tag == "Deduction"
                            "Music" -> room.tag == "Music" || room.tag == "KTV"
                            "Video" -> room.tag == "Video"
                            "Game" -> room.tag == "Game" || room.tag == "Games"
                            "Chats" -> room.tag == "Chats" || room.tag == "Casual"
                            else -> room.tag.equals(selectedCategory, ignoreCase = true)
                        }
                        val matchesQuery = if (searchQuery.isBlank()) true else {
                            room.title.contains(searchQuery, ignoreCase = true) ||
                                    room.host.contains(searchQuery, ignoreCase = true) ||
                                    room.tag.contains(searchQuery, ignoreCase = true)
                        }
                        matchesCategory && matchesQuery
                    }
                    .sortedByDescending { it.members }
            }
            "Related" -> {
                Demo.rooms.filter { room ->
                    room.tag in listOf("Friends", "Music", "PK", "Game")
                }.filter { room ->
                    if (searchQuery.isBlank()) true else {
                        room.title.contains(searchQuery, ignoreCase = true) ||
                                room.host.contains(searchQuery, ignoreCase = true)
                    }
                }
            }
            else -> { // "All"
                Demo.rooms.filter { room ->
                    val matchesCategory = when (selectedCategory) {
                        "All" -> true
                        "Friends" -> room.tag == "Friends" || room.tag == "Casual"
                        "PK" -> room.tag == "PK" || room.tag == "Deduction"
                        "Music" -> room.tag == "Music" || room.tag == "KTV"
                        "Video" -> room.tag == "Video"
                        "Game" -> room.tag == "Game" || room.tag == "Games"
                        "Chats" -> room.tag == "Chats" || room.tag == "Casual"
                        else -> room.tag.equals(selectedCategory, ignoreCase = true)
                    }
                    val matchesQuery = if (searchQuery.isBlank()) true else {
                        room.title.contains(searchQuery, ignoreCase = true) ||
                                room.host.contains(searchQuery, ignoreCase = true) ||
                                room.tag.contains(searchQuery, ignoreCase = true) ||
                                (room.activityText?.contains(searchQuery, ignoreCase = true) == true)
                    }
                    matchesCategory && matchesQuery
                }
            }
        }
    }

    AuroraVoiceRoomBackground(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(pad),
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // ── Section 1: Voice Lounge Header ──
            VoiceLoungeHeader(
                nav = nav,
                isSearchActive = isSearchActive,
                onToggleSearch = {
                    isSearchActive = !isSearchActive
                    if (!isSearchActive) searchQuery = ""
                },
            )

            // Search Bar (if active)
            AnimatedVisibility(visible = isSearchActive) {
                VoiceSearchBar(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    onClose = {
                        isSearchActive = false
                        searchQuery = ""
                    },
                )
            }

            LazyColumn(
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 80.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                // ── Section 2: Discovery Pill-Based Navigation (Discover | Trending | Following) ──
                item {
                    LoungeNavPills(
                        selected = selectedLoungeTab,
                        onSelect = { selectedLoungeTab = it },
                    )
                }

                // ── Section 3: Tonight's Spotlight (Royal Midnight Featured Card) ──
                item {
                    TonightsSpotlightCard(
                        onExploreClick = { nav.navigateSafe(Routes.RANKING) },
                    )
                }

                // ── Section 4: Category Filter Chips Row ──
                item {
                    CategoryChipRow(
                        selected = selectedCategory,
                        onSelect = { selectedCategory = it },
                    )
                }

                // ── Section 5: Room Cards List ──
                if (selectedLoungeTab == "Following") {
                    item {
                        FollowingEmptyState(onExploreClick = { selectedLoungeTab = "All" })
                    }
                } else if (filteredRooms.isEmpty()) {
                    item {
                        LoungeEmptyRoomsState(
                            onReset = {
                                selectedCategory = "All"
                                searchQuery = ""
                                isSearchActive = false
                            }
                        )
                    }
                } else {
                    items(filteredRooms, key = { it.id }) { room ->
                        AuroraRoomCard(
                            room = room,
                            onClick = {
                                prefs.recordRoomVisit(room.id)
                                visitRefresh++
                                nav.navigateSafe(Routes.roomRoute(room.id))
                            }
                        )
                    }
                }
            }
        }

        // ── Floating Gift Pack Widget (Bottom-Right) ──
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 16.dp),
        ) {
            CompactGiftPackWidget(onClick = { showGiftPackSheet = true })
        }

        // ── Gift Pack Offers Sheet ──
        if (showGiftPackSheet) {
            VoiceGiftPackSheet(onDismiss = { showGiftPackSheet = false })
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  1. Voice Lounge Header (Title + Subtitle + Search + Create Room Action)
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun VoiceLoungeHeader(
    nav: NavHostController,
    isSearchActive: Boolean,
    onToggleSearch: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Voice Lounge",
                color = AuroraTextPrimary,
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = (-0.5).sp,
            )
            Spacer(Modifier.height(1.dp))
            Text(
                text = "Find your people. Join the vibe.",
                color = AuroraTextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
            )
        }

        // Search Action Icon Button
        Box(
            modifier = Modifier
                .size(36.dp)
                .shadow(1.dp, CircleShape)
                .clip(CircleShape)
                .background(AuroraSurface)
                .border(1.dp, AuroraBorder, CircleShape)
                .bounceClick(onClick = onToggleSearch),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_search),
                contentDescription = "Search rooms",
                tint = if (isSearchActive) AuroraOrchid else AuroraTextPrimary,
                modifier = Modifier.size(18.dp),
            )
        }

        Spacer(Modifier.width(10.dp))

        // Create Room Button (Sunset Fantasy Action)
        Box(
            modifier = Modifier
                .size(36.dp)
                .shadow(2.dp, CircleShape)
                .clip(CircleShape)
                .background(AuroraGradients.sunsetFantasy)
                .bounceClick { nav.navigateSafe(Routes.CREATE_ROOM) },
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_plus),
                contentDescription = "Create Room",
                tint = Color.White,
                modifier = Modifier.size(18.dp),
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  2. Search Bar Component
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun VoiceSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onClose: () -> Unit,
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
                .height(40.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(AuroraSurface)
                .border(1.dp, AuroraBorder, RoundedCornerShape(20.dp))
                .padding(horizontal = 12.dp),
            contentAlignment = Alignment.CenterStart,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(R.drawable.ic_search),
                    contentDescription = null,
                    tint = AuroraTextSecondary,
                    modifier = Modifier.size(16.dp),
                )
                Spacer(Modifier.width(8.dp))
                BasicTextField(
                    value = query,
                    onValueChange = onQueryChange,
                    singleLine = true,
                    textStyle = TextStyle(color = AuroraTextPrimary, fontSize = 13.sp),
                    cursorBrush = SolidColor(AuroraOrchid),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
                    modifier = Modifier.weight(1f),
                    decorationBox = { inner ->
                        if (query.isEmpty()) {
                            Text("Search rooms, hosts or tags...", color = AuroraTextSecondary, fontSize = 13.sp)
                        }
                        inner()
                    },
                )
                if (query.isNotEmpty()) {
                    Icon(
                        painter = painterResource(R.drawable.ic_close),
                        contentDescription = "Clear search",
                        tint = AuroraTextSecondary,
                        modifier = Modifier
                            .size(16.dp)
                            .clickable { onQueryChange("") },
                    )
                }
            }
        }

        Spacer(Modifier.width(10.dp))

        Text(
            text = "Cancel",
            color = AuroraOrchid,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.clickable {
                focusManager.clearFocus()
                onClose()
            },
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  3. Discovery Navigation Pills (Discover | Trending | Following)
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun LoungeNavPills(
    selected: String,
    onSelect: (String) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        LoungeTabs.forEach { tab ->
            val active = tab == selected
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(18.dp))
                    .background(if (active) AuroraGradients.sunsetFantasy else Brush.linearGradient(listOf(AuroraSurface, AuroraSurface)))
                    .border(
                        1.dp,
                        if (active) Color.Transparent else AuroraBorder,
                        RoundedCornerShape(18.dp)
                    )
                    .bounceClick { onSelect(tab) }
                    .padding(horizontal = 16.dp, vertical = 7.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = tab,
                    color = if (active) Color.White else AuroraTextSecondary,
                    fontSize = 13.sp,
                    fontWeight = if (active) FontWeight.Bold else FontWeight.Medium,
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  4. Tonight's Spotlight Card (Royal Midnight & Cyan Sheen)
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun TonightsSpotlightCard(
    onExploreClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .shadow(4.dp, RoundedCornerShape(22.dp))
            .clip(RoundedCornerShape(22.dp))
            .background(
                Brush.linearGradient(
                    listOf(
                        Color(0xFF1B1438), // Royal Midnight
                        Color(0xFF2E1C5E), // Deep Violet
                        Color(0xFF1E284A), // Indigo Night
                    )
                )
            )
            .border(1.dp, Color.White.copy(alpha = 0.20f), RoundedCornerShape(22.dp))
            .bounceClick(scaleDown = 0.98f, onClick = onExploreClick)
            .padding(16.dp),
    ) {
        // Voice Discovery Backdrop Banner (ap3r_banner_voice_rooms)
        Image(
            painter = painterResource(R.drawable.ap3r_banner_voice_rooms),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            alpha = 0.32f,
        )

        // Decorative radial overlay
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(150.dp)
                .background(AuroraCyan.copy(alpha = 0.08f), CircleShape),
        )

        // Left Content: Tag + Headline + Subtitle + Action Button
        Column(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .fillMaxWidth(0.62f),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            // Live Spotlight Pill
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF00E676)),
                )
                Spacer(Modifier.width(5.dp))
                Text(
                    text = "TONIGHT'S SPOTLIGHT",
                    color = AuroraCyan,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 0.6.sp,
                )
            }

            Spacer(Modifier.height(4.dp))

            Text(
                text = "Rooms worth joining.",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
            )

            Text(
                text = "Top-ranked hosts, live acoustic jam sessions & party games.",
                color = Color.White.copy(alpha = 0.85f),
                fontSize = 11.sp,
                lineHeight = 14.sp,
                maxLines = 2,
            )

            Spacer(Modifier.height(8.dp))

            // Action Pill
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color.White)
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Explore Spotlight ›",
                    color = AuroraOrchid,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }

        // Right Overlapping Room Collage (Moonlight & Pop Star with glowing borders)
        Row(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 4.dp),
            horizontalArrangement = Arrangement.spacedBy((-16).dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                painter = painterResource(R.drawable.vb_room_cover_moonlight),
                contentDescription = null,
                modifier = Modifier
                    .size(68.dp)
                    .shadow(3.dp, RoundedCornerShape(16.dp))
                    .clip(RoundedCornerShape(16.dp))
                    .border(2.dp, Color.White.copy(alpha = 0.7f), RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop,
            )
            Image(
                painter = painterResource(R.drawable.vb_room_cover_pop_star),
                contentDescription = null,
                modifier = Modifier
                    .size(76.dp)
                    .shadow(5.dp, RoundedCornerShape(18.dp))
                    .clip(RoundedCornerShape(18.dp))
                    .border(2.dp, Color.White, RoundedCornerShape(18.dp)),
                contentScale = ContentScale.Crop,
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  5. Category Rounded Filter Chips Row
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun CategoryChipRow(
    selected: String,
    onSelect: (String) -> Unit,
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        items(CategoryPills, key = { it }) { label ->
            val active = label == selected
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (active) AuroraGradients.sunsetFantasy else Brush.linearGradient(listOf(AuroraSurface, AuroraSurface)))
                    .border(
                        1.dp,
                        if (active) Color.Transparent else AuroraBorder,
                        RoundedCornerShape(16.dp)
                    )
                    .bounceClick { onSelect(label) }
                    .padding(horizontal = 14.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = label,
                    color = if (active) Color.White else AuroraTextSecondary,
                    fontSize = 12.sp,
                    fontWeight = if (active) FontWeight.Bold else FontWeight.Medium,
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  6. Aurora Room Card Layout (Light Ivory Surface, Left Cover, Gradient Join Button)
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun AuroraRoomCard(
    room: RoomCard,
    onClick: () -> Unit,
) {
    val (tagBg, tagFg) = tagColors(room.tag)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(20.dp))
            .clip(RoundedCornerShape(20.dp))
            .background(AuroraSurface)
            .border(1.dp, AuroraBorder, RoundedCornerShape(20.dp))
            .bounceClick(scaleDown = 0.98f, onClick = onClick)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // LEFT: Illustrated Square Room Cover (82dp x 82dp with smooth corners)
        Box(
            modifier = Modifier
                .size(82.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(AuroraBackground)
                .border(1.dp, AuroraBorder.copy(alpha = 0.6f), RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center,
        ) {
            val coverDrawable = room.coverRes ?: R.drawable.vb_room_cover_moonlight
            Image(
                painter = painterResource(coverDrawable),
                contentDescription = "${room.title} cover",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
            )

            // Top-left: LIVE vs ENDED status indicator badge
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(5.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0xD9000000))
                    .padding(horizontal = 5.dp, vertical = 2.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(5.dp)
                            .clip(CircleShape)
                            .background(if (room.live) Color(0xFF00E676) else Color(0xFF9E9E9E)),
                    )
                    Spacer(Modifier.width(3.dp))
                    Text(
                        text = if (room.live) "LIVE" else "OFFLINE",
                        color = Color.White,
                        fontSize = 7.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }

            // Top-right: PK Active Badge (if PK active)
            if (room.isPkActive) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(5.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color(0xFFFF5252), Color(0xFFFF4081))
                            )
                        )
                        .padding(horizontal = 4.dp, vertical = 2.dp),
                ) {
                    Text(
                        text = "⚡ PK",
                        color = Color.White,
                        fontSize = 7.sp,
                        fontWeight = FontWeight.ExtraBold,
                    )
                }
            }
        }

        Spacer(Modifier.width(12.dp))

        // CENTER: Title + Host details (Level + Room Crest) + Category Pill + User Count
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(3.dp),
        ) {
            Text(
                text = room.title,
                color = DeepMidnight,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )

            // Host Identity with Host Level & Room Crest
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp),
            ) {
                InitialsAvatar(name = room.host, size = 16.dp)
                Text(
                    text = room.host,
                    color = AuroraTextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )

                // Host level pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(SoftPeach.copy(alpha = 0.35f))
                        .padding(horizontal = 4.dp, vertical = 1.dp),
                ) {
                    Text(
                        text = "Lv.${room.hostLevel}",
                        color = Color(0xFFC75D35),
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }

                // Room Crest pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(DreamLavender.copy(alpha = 0.35f))
                        .padding(horizontal = 4.dp, vertical = 1.dp),
                ) {
                    Text(
                        text = "👑 Crest Lv.${room.roomLevel}",
                        color = CelestialViolet,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }

            // Activity text / visited time
            val subtitle = room.activityText ?: "Casual conversation & music"
            Text(
                text = subtitle,
                color = AuroraTextSecondary,
                fontSize = 10.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )

            Spacer(Modifier.height(1.dp))

            // Metadata row: Category Tag + Users count + Listeners
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = room.tag,
                    color = tagFg,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(tagBg)
                        .padding(horizontal = 6.dp, vertical = 2.dp),
                )

                Spacer(Modifier.width(8.dp))

                Icon(
                    painter = painterResource(R.drawable.ic_users),
                    contentDescription = null,
                    tint = AuroraTurquoise,
                    modifier = Modifier.size(12.dp),
                )
                Spacer(Modifier.width(3.dp))
                Text(
                    text = "${room.members}",
                    color = DeepMidnight,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                )

                Spacer(Modifier.width(8.dp))

                Text(
                    text = "(${room.listeners} listening)",
                    color = AuroraTextSecondary,
                    fontSize = 10.sp,
                )
            }
        }

        Spacer(Modifier.width(8.dp))

        // RIGHT: Badges + Join Action Button
        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            if (room.badges.isNotEmpty()) {
                Image(
                    painter = painterResource(room.badges.first()),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    contentScale = ContentScale.Fit,
                )
            }

            // Compact Join Button Pill (Sunset Fantasy)
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .background(if (room.live) AuroraGradients.sunsetFantasy else Brush.linearGradient(listOf(Color(0xFFBBB6C8), Color(0xFFCCC8D6))))
                    .padding(horizontal = 14.dp, vertical = 5.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = if (room.live) "Join" else "Revisit",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  7. Compact Floating Gift Pack Widget
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun CompactGiftPackWidget(onClick: () -> Unit) {
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
            contentDescription = "Voice Gift Pack offers",
            modifier = Modifier.size(34.dp),
            contentScale = ContentScale.Fit,
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  8. Voice Gift Pack Offers Bottom Sheet (Preview / Demo State)
// ─────────────────────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun VoiceGiftPackSheet(onDismiss: () -> Unit) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = AuroraSurface,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Image(
                painter = painterResource(R.drawable.vb_voice_gift_pack),
                contentDescription = null,
                modifier = Modifier.size(68.dp),
            )

            Spacer(Modifier.height(10.dp))

            Text(
                text = "Voice Lounge Gift Pack",
                color = AuroraTextPrimary,
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold,
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = "Exclusive perks, animated badges & voice flair",
                color = AuroraTextSecondary,
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
            )

            Spacer(Modifier.height(16.dp))

            val perks = listOf(
                "👑 3D Crown Entrance Animation",
                "🎙️ High-Definition Voice Room Mic Frame",
                "💎 500 Bonus Diamond Showcase Credits",
                "🎁 3x Free Daily Rose Stage Gifts",
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(AuroraBackground)
                    .border(1.dp, AuroraBorder, RoundedCornerShape(14.dp))
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                perks.forEach { perk ->
                    Text(text = perk, color = AuroraTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                }
            }

            Spacer(Modifier.height(14.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFFFFF3E0))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
            ) {
                Text(
                    text = "⚡ PREVIEW MODE: Live billing & payment gateway are currently disconnected. No real charges will occur.",
                    color = Color(0xFFE65100),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                )
            }

            Spacer(Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
                    .clip(RoundedCornerShape(23.dp))
                    .background(AuroraGradients.sunsetFantasy)
                    .bounceClick(scaleDown = 0.98f, onClick = onDismiss),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "Got it",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                )
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  9. Empty States
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun FollowingEmptyState(onExploreClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 36.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_users),
            contentDescription = null,
            tint = AuroraTextSecondary.copy(alpha = 0.4f),
            modifier = Modifier.size(46.dp),
        )
        Spacer(Modifier.height(10.dp))
        Text(
            text = "No followed hosts are live",
            color = AuroraTextPrimary,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = "Follow active room hosts to see them live here.",
            color = AuroraTextSecondary,
            fontSize = 12.sp,
        )
        Spacer(Modifier.height(14.dp))
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(AuroraLavender)
                .bounceClick(onClick = onExploreClick)
                .padding(horizontal = 16.dp, vertical = 8.dp),
        ) {
            Text(
                text = "Explore Live Rooms",
                color = AuroraOrchid,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
private fun LoungeEmptyRoomsState(onReset: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 36.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_users),
            contentDescription = null,
            tint = AuroraTextSecondary.copy(alpha = 0.4f),
            modifier = Modifier.size(46.dp),
        )
        Spacer(Modifier.height(10.dp))
        Text(
            text = "No voice rooms found",
            color = AuroraTextPrimary,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = "Try clearing search or switching categories",
            color = AuroraTextSecondary,
            fontSize = 12.sp,
        )
        Spacer(Modifier.height(14.dp))
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(AuroraLavender)
                .bounceClick(onClick = onReset)
                .padding(horizontal = 16.dp, vertical = 8.dp),
        ) {
            Text(
                text = "Reset filters",
                color = AuroraOrchid,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}
