package com.sundram.weplay.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import com.sundram.weplay.ui.theme.AuroraGold
import com.sundram.weplay.ui.theme.AuroraGradients
import com.sundram.weplay.ui.theme.AuroraLavender
import com.sundram.weplay.ui.theme.AuroraOrchid
import com.sundram.weplay.ui.theme.AuroraRose
import com.sundram.weplay.ui.theme.AuroraSurface
import com.sundram.weplay.ui.theme.AuroraTextPrimary
import com.sundram.weplay.ui.theme.AuroraTextSecondary
import com.sundram.weplay.ui.theme.AuroraTurquoise
import com.sundram.weplay.ui.theme.AuroraVoiceRoomBackground
import com.sundram.weplay.ui.theme.CelestialNavy
import com.sundram.weplay.ui.theme.CelestialViolet
import com.sundram.weplay.ui.theme.ChampagneGold
import com.sundram.weplay.ui.theme.CloudWhite
import com.sundram.weplay.ui.theme.CrystalBlue
import com.sundram.weplay.ui.theme.DeepInk
import com.sundram.weplay.ui.theme.DeepMidnight
import com.sundram.weplay.ui.theme.DreamLavender
import com.sundram.weplay.ui.theme.RoyalGold
import com.sundram.weplay.ui.theme.SoftPeach
import com.sundram.weplay.ui.theme.SunsetRose

private data class LoungeTabItem(
    val id: String,
    val label: String,
    val iconPrefix: String? = null,
)

private val LoungeTabItems = listOf(
    LoungeTabItem("Recent", "Recent"),
    LoungeTabItem("All", "All"),
    LoungeTabItem("Popular", "Popular", "🔥 "),
    LoungeTabItem("Related", "Related", "🔗 "),
    LoungeTabItem("Following", "Following", "♡ "),
)

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
                val followedIds = prefs.getFollowedRooms()
                Demo.rooms.filter { it.id in followedIds }.filter { room ->
                    if (searchQuery.isBlank()) true else {
                        room.title.contains(searchQuery, ignoreCase = true) ||
                                room.host.contains(searchQuery, ignoreCase = true)
                    }
                }
            }
            "Recent" -> {
                val visitedPairs = prefs.getRecentVisitedRooms()
                visitedPairs.mapNotNull { (roomId, timestamp) ->
                    val found = Demo.rooms.find { it.id == roomId }
                    if (found != null) {
                        found.copy(activityText = "Visited ${formatVisitedAgo(timestamp)} • ${found.activityText ?: ""}")
                    } else null
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
        modifier = Modifier.fillMaxSize(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(bottom = pad.calculateBottomPadding()),
        ) {
            // ── Section 1: Full Celestial Voice Lounge Header ──
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
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                // ── Section 2: Main Filter Tabs (Recent | All | Popular | Related | Following) ──
                item {
                    LoungeNavPills(
                        selected = selectedLoungeTab,
                        onSelect = { selectedLoungeTab = it },
                    )
                }

                // ── Section 3: Featured Melody Night Lounge Banner ──
                item {
                    FeaturedRoomBanner(nav = nav)
                }

                // ── Section 4: Category Filter Chips Row ──
                item {
                    CategoryChipRow(
                        selected = selectedCategory,
                        onSelect = { selectedCategory = it },
                    )
                }

                // ── Section 5: Illustrated Room Cards List ──
                if (selectedLoungeTab == "Recent" && filteredRooms.isEmpty()) {
                    item {
                        RecentEmptyRoomsState(onExploreClick = { selectedLoungeTab = "All" })
                    }
                } else if (selectedLoungeTab == "Following" && filteredRooms.isEmpty()) {
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
                .padding(end = 16.dp, bottom = pad.calculateBottomPadding() + 16.dp),
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
//  1. Full Celestial Voice Lounge Header (Emblem + Title + Search + Create Room)
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
        // Gold Microphone Emblem
        Image(
            painter = painterResource(R.drawable.ap_voice_emblem_mic),
            contentDescription = null,
            modifier = Modifier.size(38.dp),
            contentScale = ContentScale.Fit,
        )

        Spacer(Modifier.width(10.dp))

        // Title + Subtitle
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Voice Lounge",
                color = CloudWhite,
                fontSize = 25.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = (-0.3).sp,
            )
            Spacer(Modifier.height(1.dp))
            Text(
                text = "Find your people · Join the vibe",
                color = DreamLavender,
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Medium,
            )
        }

        Spacer(Modifier.width(8.dp))

        // Circular Search Action Button
        Box(
            modifier = Modifier
                .size(38.dp)
                .shadow(2.dp, CircleShape)
                .clip(CircleShape)
                .background(Brush.verticalGradient(listOf(Color(0xFF2E1B5B), Color(0xFF1F1243))))
                .border(1.2.dp, DreamLavender.copy(alpha = 0.45f), CircleShape)
                .bounceClick(onClick = onToggleSearch),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_search),
                contentDescription = "Search rooms",
                tint = if (isSearchActive) SunsetRose else CloudWhite,
                modifier = Modifier.size(18.dp),
            )
        }

        Spacer(Modifier.width(8.dp))

        // "+ Create Room" Pill Button (Sunset Fantasy)
        Row(
            modifier = Modifier
                .height(38.dp)
                .shadow(3.dp, RoundedCornerShape(19.dp))
                .clip(RoundedCornerShape(19.dp))
                .background(AuroraGradients.sunsetFantasy)
                .border(1.dp, Color.White.copy(alpha = 0.45f), RoundedCornerShape(19.dp))
                .bounceClick { nav.navigateSafe(Routes.CREATE_ROOM) }
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_plus),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(14.dp),
            )
            Spacer(Modifier.width(4.dp))
            Text(
                text = "Create Room",
                color = Color.White,
                fontSize = 12.5.sp,
                fontWeight = FontWeight.Bold,
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
//  3. Main Filter Tabs (Recent | All | Popular | Related | Following)
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun LoungeNavPills(
    selected: String,
    onSelect: (String) -> Unit,
) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 2.dp),
    ) {
        items(LoungeTabItems, key = { it.id }) { tab ->
            val active = tab.id == selected
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(18.dp))
                    .background(
                        if (active) AuroraGradients.sunsetFantasy
                        else Brush.linearGradient(listOf(Color(0xFF221644), Color(0xFF170E33)))
                    )
                    .border(
                        1.dp,
                        if (active) SunsetRose.copy(alpha = 0.85f) else Color(0xFF4C3775),
                        RoundedCornerShape(18.dp),
                    )
                    .bounceClick { onSelect(tab.id) }
                    .padding(horizontal = 16.dp, vertical = 7.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "${tab.iconPrefix ?: ""}${tab.label}",
                    color = if (active) Color.White else Color(0xFFD6C7F8),
                    fontSize = 13.sp,
                    fontWeight = if (active) FontWeight.Bold else FontWeight.SemiBold,
                    maxLines = 1,
                    softWrap = false,
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  4. Featured Melody Night Lounge Banner
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun FeaturedRoomBanner(
    nav: NavHostController,
) {
    val liveRoomId = remember {
        Demo.rooms.firstOrNull { it.live }?.id ?: "r3"
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(165.dp)
            .shadow(6.dp, RoundedCornerShape(22.dp))
            .clip(RoundedCornerShape(22.dp))
            .background(
                Brush.horizontalGradient(
                    listOf(
                        Color(0xFF26124E), // Deep Violet
                        Color(0xFF1C0F3A), // Royal Midnight
                    )
                )
            )
            .border(
                1.2.dp,
                Brush.horizontalGradient(
                    listOf(
                        Color(0xFFBA55D3), // Orchid
                        RoyalGold,          // Gold
                        SunsetRose,        // Rose
                    )
                ),
                RoundedCornerShape(22.dp),
            )
            .bounceClick(scaleDown = 0.98f) {
                nav.navigateSafe(Routes.roomRoute(liveRoomId))
            }
            .padding(14.dp),
    ) {
        // Decorative aurora trails overlay
        Image(
            painter = painterResource(R.drawable.ap_voice_fx_aurora_ribbon),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(90.dp)
                .align(Alignment.BottomCenter),
            contentScale = ContentScale.Crop,
            alpha = 0.35f,
        )

        // Right side: Official featured music artwork
        Image(
            painter = painterResource(R.drawable.ap_voice_featured_music),
            contentDescription = "Melody Night Lounge",
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .size(165.dp)
                .offset(x = 6.dp, y = (-4).dp),
            contentScale = ContentScale.Fit,
        )

        // Left Content Column
        Column(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .fillMaxWidth(0.66f)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            // Crown "Featured Room" ribbon pill
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF3A240C))
                    .border(0.8.dp, RoyalGold.copy(alpha = 0.75f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 7.dp, vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("👑", fontSize = 10.sp)
                Spacer(Modifier.width(3.dp))
                Text(
                    text = "Featured Room",
                    color = RoyalGold,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                )
            }

            Column {
                Text(
                    text = "Melody Night Lounge 🎵",
                    color = CloudWhite,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = (-0.2).sp,
                )
                Spacer(Modifier.height(3.dp))
                Text(
                    text = "Live music · Chat · Make new friends",
                    color = DreamLavender,
                    fontSize = 11.5.sp,
                    lineHeight = 14.sp,
                    maxLines = 1,
                )
            }

            // "Join Live >" golden pill button
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(AuroraGradients.royalGold)
                    .border(0.8.dp, CloudWhite.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
                    .clickable { nav.navigateSafe(Routes.roomRoute(liveRoomId)) }
                    .padding(horizontal = 14.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Join Live ›",
                    color = DeepInk,
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Black,
                )
            }
        }

        // 4-Dot carousel indicator at bottom center
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(y = (-2).dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .width(14.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(RoyalGold),
            )
            repeat(3) {
                Box(
                    modifier = Modifier
                        .size(4.dp)
                        .clip(CircleShape)
                        .background(DreamLavender.copy(alpha = 0.4f)),
                )
            }
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
        contentPadding = PaddingValues(horizontal = 2.dp),
    ) {
        items(CategoryPills, key = { it }) { label ->
            val active = label == selected
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        if (active) AuroraGradients.sunsetFantasy
                        else Brush.linearGradient(listOf(Color(0xFF241848), Color(0xFF191036)))
                    )
                    .border(
                        1.dp,
                        if (active) Color.Transparent else DreamLavender.copy(alpha = 0.30f),
                        RoundedCornerShape(16.dp),
                    )
                    .bounceClick { onSelect(label) }
                    .padding(horizontal = 14.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = label,
                    color = if (active) Color.White else DreamLavender.copy(alpha = 0.85f),
                    fontSize = 12.sp,
                    fontWeight = if (active) FontWeight.Bold else FontWeight.Medium,
                    maxLines = 1,
                    softWrap = false,
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  6. Illustrated Aurora Room Card (Official Artwork, Crest & Headphone Join)
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun AuroraRoomCard(
    room: RoomCard,
    onClick: () -> Unit,
) {
    val (tagBg, tagFg) = tagColors(room.tag)

    // Crest drawable based on room category / type
    val crestDrawable = when {
        room.tag == "PK" || room.isPkActive || room.id == "r2" -> R.drawable.ap_voice_crest_pk
        room.tag == "Music" || room.id == "r3" || room.id == "r6" -> R.drawable.ap_voice_crest_music
        room.tag == "Game" || room.id == "r4" || room.id == "r7" || room.id == "r8" -> R.drawable.ap_voice_crest_game
        else -> R.drawable.ap_voice_crest_royal
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(20.dp))
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF261650),
                        Color(0xFF1A103D),
                    )
                )
            )
            .border(1.2.dp, Color(0xFF6B4BAE).copy(alpha = 0.45f), RoundedCornerShape(20.dp))
            .bounceClick(scaleDown = 0.98f, onClick = onClick)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // LEFT: Room Cover (86dp x 86dp) with LIVE & PK badges
        Box(
            modifier = Modifier
                .size(86.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF140E2C))
                .border(1.dp, DreamLavender.copy(alpha = 0.35f), RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center,
        ) {
            val coverDrawable = room.coverRes ?: when (room.tag) {
                "PK" -> R.drawable.ap_voice_cover_werewolf
                "Music" -> R.drawable.ap_voice_cover_music
                "Game" -> R.drawable.ap_voice_cover_dice
                else -> R.drawable.ap_voice_cover_moon
            }
            Image(
                painter = painterResource(coverDrawable),
                contentDescription = room.title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
            )

            // Top-left Badges Row: LIVE + PK
            Row(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(5.dp),
                horizontalArrangement = Arrangement.spacedBy(3.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // LIVE Badge (Red pill)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xD9000000))
                        .padding(horizontal = 5.dp, vertical = 2.dp),
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(5.dp)
                                .clip(CircleShape)
                                .background(if (room.live) Color(0xFFFF1744) else Color(0xFF9E9E9E)),
                        )
                        Spacer(Modifier.width(3.dp))
                        Text(
                            text = if (room.live) "LIVE" else "OFFLINE",
                            color = Color.White,
                            fontSize = 7.5.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }

                // PK Badge (if PK active)
                if (room.isPkActive || room.tag == "PK") {
                    Box(
                        modifier = Modifier
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
                            fontSize = 7.5.sp,
                            fontWeight = FontWeight.ExtraBold,
                        )
                    }
                }
            }
        }

        Spacer(Modifier.width(12.dp))

        // CENTER: Title + Host details (framed avatar, Lv, Crest) + Subtitle + Metadata
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(3.5.dp),
        ) {
            // Room Title
            Text(
                text = room.title,
                color = CloudWhite,
                fontSize = 15.5.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )

            // Host Row with Framed Avatar, Host Name, Level & Crest
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp),
            ) {
                // Host Avatar with official host frame
                Box(
                    modifier = Modifier.size(26.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    InitialsAvatar(name = room.host, size = 20.dp)
                    Image(
                        painter = painterResource(R.drawable.ap_voice_frame_host),
                        contentDescription = null,
                        modifier = Modifier.size(26.dp),
                    )
                }

                Text(
                    text = room.host,
                    color = CloudWhite.copy(alpha = 0.95f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )

                // Host level pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xFF4A2518))
                        .border(0.6.dp, Color(0xFFF4A261).copy(alpha = 0.7f), RoundedCornerShape(4.dp))
                        .padding(horizontal = 4.dp, vertical = 1.dp),
                ) {
                    Text(
                        text = "Lv.${room.hostLevel}",
                        color = Color(0xFFFFB74D),
                        fontSize = 8.5.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }

                // Room Crest pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xFF2E1C5E))
                        .border(0.6.dp, DreamLavender.copy(alpha = 0.6f), RoundedCornerShape(4.dp))
                        .padding(horizontal = 4.dp, vertical = 1.dp),
                ) {
                    Text(
                        text = "👑 Crest Lv.${room.roomLevel}",
                        color = DreamLavender,
                        fontSize = 8.5.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }

            // Activity text / Description
            val subtitle = room.activityText ?: "Casual conversation & music"
            Text(
                text = subtitle,
                color = DreamLavender.copy(alpha = 0.85f),
                fontSize = 11.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )

            // Metadata row: Category Tag + Listener Count
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = room.tag,
                    color = Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(tagBg)
                        .padding(horizontal = 7.dp, vertical = 2.dp),
                )

                Spacer(Modifier.width(8.dp))

                Icon(
                    painter = painterResource(R.drawable.ic_users),
                    contentDescription = null,
                    tint = CrystalBlue,
                    modifier = Modifier.size(12.dp),
                )
                Spacer(Modifier.width(3.dp))
                Text(
                    text = "${room.listeners} Listening",
                    color = CloudWhite.copy(alpha = 0.90f),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                )
            }
        }

        Spacer(Modifier.width(8.dp))

        // RIGHT: Crest Trophy/Emblem + Glowing Join Action Pill
        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            // Type/Rank Crest Graphic (Royal, PK, Music, or Game)
            Image(
                painter = painterResource(crestDrawable),
                contentDescription = null,
                modifier = Modifier.size(34.dp),
                contentScale = ContentScale.Fit,
            )

            // Glowing Join Action Pill (with headphones icon + "Join")
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        if (room.live) AuroraGradients.sunsetFantasy
                        else Brush.linearGradient(listOf(Color(0xFF382F58), Color(0xFF282042)))
                    )
                    .border(
                        0.8.dp,
                        if (room.live) Color.White.copy(alpha = 0.5f) else Color.Transparent,
                        RoundedCornerShape(16.dp),
                    )
                    .padding(horizontal = 14.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    painter = painterResource(R.drawable.vb_vr_ic_headphones),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(13.dp),
                )
                Spacer(Modifier.width(4.dp))
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
//  8. Voice Gift Pack Offers Bottom Sheet
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
private fun RecentEmptyRoomsState(onExploreClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 36.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Image(
            painter = painterResource(R.drawable.ap_voice_emblem_mic),
            contentDescription = null,
            modifier = Modifier.size(46.dp),
        )
        Spacer(Modifier.height(10.dp))
        Text(
            text = "No Recent Voice Visits",
            color = CloudWhite,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = "You haven't visited any voice rooms yet. Join a lounge to start listening!",
            color = DreamLavender,
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(14.dp))
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(AuroraGradients.sunsetFantasy)
                .bounceClick(onClick = onExploreClick)
                .padding(horizontal = 16.dp, vertical = 8.dp),
        ) {
            Text(
                text = "Explore Live Rooms ›",
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

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
            tint = DreamLavender.copy(alpha = 0.6f),
            modifier = Modifier.size(46.dp),
        )
        Spacer(Modifier.height(10.dp))
        Text(
            text = "No Followed Rooms Yet",
            color = CloudWhite,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = "Follow active room hosts to see them live here.",
            color = DreamLavender,
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(14.dp))
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(AuroraGradients.sunsetFantasy)
                .bounceClick(onClick = onExploreClick)
                .padding(horizontal = 16.dp, vertical = 8.dp),
        ) {
            Text(
                text = "Explore All Rooms ›",
                color = Color.White,
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
            tint = DreamLavender.copy(alpha = 0.6f),
            modifier = Modifier.size(46.dp),
        )
        Spacer(Modifier.height(10.dp))
        Text(
            text = "No voice rooms found",
            color = CloudWhite,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = "Try clearing search or switching categories",
            color = DreamLavender,
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(14.dp))
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(AuroraGradients.sunsetFantasy)
                .bounceClick(onClick = onReset)
                .padding(horizontal = 16.dp, vertical = 8.dp),
        ) {
            Text(
                text = "Reset Filters",
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}
