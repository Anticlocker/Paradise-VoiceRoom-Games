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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.sundram.weplay.R
import com.sundram.weplay.data.Demo
import com.sundram.weplay.data.RoomCard
import com.sundram.weplay.ui.components.bounceClick
import com.sundram.weplay.ui.nav.Routes
import com.sundram.weplay.ui.theme.CyanBrand

private val BgPearl = Color(0xFFF8F7FF)
private val TextDark = Color(0xFF211B38)
private val TextSecondary = Color(0xFF6C628A)
private val BorderLavender = Color(0xFFECE9FE)
private val CardWhite = Color(0xFFFFFFFF)
private val PurpleBrand = Color(0xFF8058E9)
private val ChipInactiveBg = Color(0xFFF0EDFF)

private val FilterCategories = listOf("All", "Games", "Deduction", "Casual", "KTV", "PK")

/**
 * Redesigned Game Rooms screen with white/pearl theme, readable text contrast,
 * hero banner ("Find your next squad"), filter chips, expandable search bar,
 * and high-contrast room cards with cover illustrations.
 */
@Composable
fun GameRoomsScreen(nav: NavHostController) {
    var selectedCategory by remember { mutableStateOf("All") }
    var searchQuery by remember { mutableStateOf("") }
    var isSearchExpanded by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    val filteredRooms = remember(selectedCategory, searchQuery) {
        val query = searchQuery.trim().lowercase()
        Demo.rooms.filter { room ->
            val matchesCategory = if (selectedCategory == "All") {
                true
            } else {
                room.tag.equals(selectedCategory, ignoreCase = true)
            }
            val matchesSearch = if (query.isEmpty()) {
                true
            } else {
                room.title.lowercase().contains(query) ||
                    room.host.lowercase().contains(query) ||
                    room.tag.lowercase().contains(query)
            }
            matchesCategory && matchesSearch
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgPearl)
            .statusBarsPadding()
            .navigationBarsPadding(),
    ) {
        // ── 1. Top Bar ──
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Back Button
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
                    painter = painterResource(R.drawable.ic_back),
                    contentDescription = "Back",
                    tint = TextDark,
                    modifier = Modifier.size(20.dp),
                )
            }

            Spacer(Modifier.width(12.dp))

            // Title & Count Badge
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f),
            ) {
                Text(
                    text = "Game Rooms",
                    color = TextDark,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(BorderLavender)
                        .padding(horizontal = 8.dp, vertical = 2.dp),
                ) {
                    Text(
                        text = "${filteredRooms.size}",
                        color = PurpleBrand,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }

            // Search Toggle Button
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(if (isSearchExpanded) BorderLavender else CardWhite)
                    .border(1.dp, BorderLavender, CircleShape)
                    .bounceClick {
                        isSearchExpanded = !isSearchExpanded
                        if (!isSearchExpanded) searchQuery = ""
                    },
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_search),
                    contentDescription = "Search",
                    tint = if (isSearchExpanded) PurpleBrand else TextDark,
                    modifier = Modifier.size(18.dp),
                )
            }

            Spacer(Modifier.width(8.dp))

            // Create Room (+) Button
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.horizontalGradient(listOf(PurpleBrand, CyanBrand))
                    )
                    .bounceClick { nav.navigate(Routes.CREATE_ROOM) },
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_plus),
                    contentDescription = "Create Game Room",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp),
                )
            }
        }

        // ── 2. Expandable Search Bar ──
        AnimatedVisibility(
            visible = isSearchExpanded,
            enter = fadeIn(),
            exit = fadeOut(),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(CardWhite)
                    .border(1.dp, PurpleBrand.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                    .padding(horizontal = 12.dp, vertical = 10.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(R.drawable.ic_search),
                        contentDescription = null,
                        tint = PurpleBrand,
                        modifier = Modifier.size(18.dp),
                    )
                    Spacer(Modifier.width(8.dp))
                    BasicTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        textStyle = TextStyle(color = TextDark, fontSize = 14.sp),
                        cursorBrush = SolidColor(PurpleBrand),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
                        decorationBox = { innerTextField ->
                            if (searchQuery.isEmpty()) {
                                Text("Search room name, host or tag...", color = TextSecondary, fontSize = 13.sp)
                            }
                            innerTextField()
                        },
                    )
                    if (searchQuery.isNotEmpty()) {
                        Icon(
                            painter = painterResource(R.drawable.ic_close),
                            contentDescription = "Clear search",
                            tint = TextSecondary,
                            modifier = Modifier
                                .size(16.dp)
                                .clickable { searchQuery = "" },
                        )
                    }
                }
            }
        }

        // ── 3. Scrollable Content ──
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 24.dp),
        ) {
            // Hero Banner ("Find your next squad")
            item(key = "hero_banner") {
                GameRoomsHeroBanner(
                    onCreateRoom = { nav.navigate(Routes.CREATE_ROOM) },
                )
            }

            // Filter Chips
            item(key = "filter_chips") {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(FilterCategories, key = { it }) { cat ->
                        val selected = cat == selectedCategory
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(
                                    if (selected) {
                                        Brush.horizontalGradient(listOf(PurpleBrand, Color(0xFF6B3CE0)))
                                    } else {
                                        SolidColor(ChipInactiveBg)
                                    }
                                )
                                .border(
                                    width = if (selected) 0.dp else 1.dp,
                                    color = if (selected) Color.Transparent else BorderLavender,
                                    shape = RoundedCornerShape(16.dp),
                                )
                                .clickable { selectedCategory = cat }
                                .padding(horizontal = 14.dp, vertical = 7.dp),
                        ) {
                            Text(
                                text = cat,
                                color = if (selected) Color.White else TextDark,
                                fontSize = 13.sp,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.SemiBold,
                            )
                        }
                    }
                }
            }

            // Empty State
            if (filteredRooms.isEmpty()) {
                item(key = "empty_state") {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 40.dp, bottom = 40.dp, start = 24.dp, end = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Image(
                            painter = painterResource(R.drawable.vb_rs_bg_empty_rooms),
                            contentDescription = "No rooms",
                            modifier = Modifier.size(160.dp),
                            contentScale = ContentScale.Fit,
                        )
                        Spacer(Modifier.height(16.dp))
                        Text(
                            text = "No Game Rooms Found",
                            color = TextDark,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = if (searchQuery.isNotEmpty()) {
                                "No matches for \"$searchQuery\" in $selectedCategory."
                            } else {
                                "Be the first to create a game room in this category!"
                            },
                            color = TextSecondary,
                            fontSize = 13.sp,
                        )
                        Spacer(Modifier.height(16.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(
                                    Brush.horizontalGradient(listOf(PurpleBrand, CyanBrand))
                                )
                                .bounceClick { nav.navigate(Routes.CREATE_ROOM) }
                                .padding(horizontal = 20.dp, vertical = 10.dp),
                        ) {
                            Text(
                                text = "Create a Room",
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                    }
                }
            } else {
                // Room Cards
                items(filteredRooms, key = { it.id }) { room ->
                    Box(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                    ) {
                        GameRoomItem(room = room) {
                            nav.navigate(Routes.roomRoute(room.id))
                        }
                    }
                }
            }
        }
    }
}

/**
 * Top Hero Banner ("Find your next squad") using the original `vb_rs_bg_game_rooms` asset.
 */
@Composable
private fun GameRoomsHeroBanner(onCreateRoom: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .height(132.dp)
            .clip(RoundedCornerShape(20.dp))
            .shadow(4.dp, RoundedCornerShape(20.dp)),
    ) {
        // Artwork Background
        Image(
            painter = painterResource(R.drawable.vb_rs_bg_game_rooms),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
        )

        // Gradient overlay for contrast
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xEE1C1438),
                            Color(0xAA1C1438),
                            Color(0x331C1438),
                        )
                    )
                ),
        )

        // Banner content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(R.drawable.vb_rs_host_crown),
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = "FEATURED SQUADS",
                    color = Color(0xFFFFD54F),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.sp,
                )
            }
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Find Your Next Squad",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = "Jump into real-time voice & games with friends",
                color = Color(0xFFD4CCEC),
                fontSize = 12.sp,
            )
            Spacer(Modifier.height(10.dp))

            // Quick Create Action
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(CyanBrand)
                    .bounceClick(onClick = onCreateRoom)
                    .padding(horizontal = 12.dp, vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_plus),
                    contentDescription = null,
                    tint = Color(0xFF06202B),
                    modifier = Modifier.size(14.dp),
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = "Create Room",
                    color = Color(0xFF06202B),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

/**
 * High-contrast, clean room card adhering to the White/Lavender/Cyan VYBORA identity.
 */
@Composable
private fun GameRoomItem(
    room: RoomCard,
    onClick: () -> Unit,
) {
    // Pick appropriate cover illustration based on room tag
    val coverDrawable = when (room.tag.lowercase()) {
        "games" -> R.drawable.vb_rs_cover_games
        "ktv", "music" -> R.drawable.vb_rs_cover_music
        "pk" -> R.drawable.vb_rs_cover_pk
        else -> R.drawable.vb_rs_cover_friends
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(CardWhite)
            .border(1.dp, BorderLavender, RoundedCornerShape(18.dp))
            .shadow(2.dp, RoundedCornerShape(18.dp), ambientColor = Color(0x10000000))
            .clickable(onClick = onClick)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Room Cover Artwork
        Box(
            modifier = Modifier
                .size(62.dp)
                .clip(RoundedCornerShape(14.dp)),
        ) {
            Image(
                painter = painterResource(coverDrawable),
                contentDescription = room.title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
            )
        }

        Spacer(Modifier.width(12.dp))

        // Room Info
        Column(Modifier.weight(1f)) {
            Text(
                text = room.title,
                color = TextDark,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(Modifier.height(4.dp))

            // Tag & Verified Host
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Category Chip
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(ChipInactiveBg)
                        .padding(horizontal = 6.dp, vertical = 2.dp),
                ) {
                    Text(
                        text = room.tag,
                        color = PurpleBrand,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }

                Spacer(Modifier.width(8.dp))

                // Host Crown + Name
                Image(
                    painter = painterResource(R.drawable.vb_rs_host_crown),
                    contentDescription = null,
                    modifier = Modifier.size(12.dp),
                )
                Spacer(Modifier.width(3.dp))
                Text(
                    text = room.host,
                    color = TextSecondary,
                    fontSize = 11.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            Spacer(Modifier.height(4.dp))

            // Capacity indicator
            Text(
                text = "${room.members}/8 on stage • ${room.listeners} listening",
                color = TextSecondary,
                fontSize = 11.sp,
            )
        }

        Spacer(Modifier.width(10.dp))

        // Join Action Button
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(14.dp))
                .background(
                    Brush.horizontalGradient(
                        listOf(CyanBrand, Color(0xFF26B2C8))
                    )
                )
                .bounceClick(onClick = onClick)
                .padding(horizontal = 14.dp, vertical = 8.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "JOIN",
                color = Color(0xFF04212D),
                fontSize = 12.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 0.5.sp,
            )
        }
    }
}
