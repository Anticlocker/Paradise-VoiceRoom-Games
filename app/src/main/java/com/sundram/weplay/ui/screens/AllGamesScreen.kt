package com.sundram.weplay.ui.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.sundram.weplay.R
import com.sundram.weplay.ui.components.AuroraEmptyStateCard
import com.sundram.weplay.ui.components.AuroraScreenCanvas
import com.sundram.weplay.ui.components.bounceClick
import com.sundram.weplay.ui.components.navigateSafe
import com.sundram.weplay.ui.nav.Routes
import com.sundram.weplay.ui.theme.AuroraBorder
import com.sundram.weplay.ui.theme.AuroraElevated
import com.sundram.weplay.ui.theme.AuroraGradients
import com.sundram.weplay.ui.theme.AuroraSuccess
import com.sundram.weplay.ui.theme.AuroraSurface
import com.sundram.weplay.ui.theme.CloudWhite
import com.sundram.weplay.ui.theme.CrystalBlue
import com.sundram.weplay.ui.theme.DeepInk
import com.sundram.weplay.ui.theme.DreamLavender
import com.sundram.weplay.ui.theme.MysticPurple
import com.sundram.weplay.ui.theme.RoyalGold
import com.sundram.weplay.ui.theme.RoyalViolet

data class CatalogGameEntry(
    val id: String,
    val title: String,
    val subtitle: String,
    val category: String,
    val playersLabel: String,
    val illustrationRes: Int,
    val isPlayableNow: Boolean,
    val route: String? = null,
    val badge: String? = null,
    val accentColor: Color,
    val isSceneCover: Boolean = false,
)

private val AllCatalogGames = listOf(
    CatalogGameEntry(
        id = "spy",
        title = "Who's the Spy",
        subtitle = "Social deduction & pass-and-play deception",
        category = "Party",
        playersLabel = "4-8 Players",
        illustrationRes = R.drawable.vb_character_whos_the_spy,
        isPlayableNow = true,
        route = Routes.SPY,
        badge = "Playable",
        accentColor = Color(0xFF00ACC1),
    ),
    CatalogGameEntry(
        id = "ludo",
        title = "Ludo Classic",
        subtitle = "Royal dice board game with 4-corner strategy",
        category = "Board & Card",
        playersLabel = "2-4 Players",
        illustrationRes = R.drawable.vb_character_royal_dice,
        isPlayableNow = true,
        route = Routes.LUDO,
        badge = "Playable",
        accentColor = Color(0xFFD84315),
    ),
    CatalogGameEntry(
        id = "petrol",
        title = "Petrol Pump Simulator",
        subtitle = "Manage pumps, speed service, and station upgrades",
        category = "Casual",
        playersLabel = "1-4 Players",
        illustrationRes = R.drawable.vb_character_petrol_pump,
        isPlayableNow = false,
        badge = "Preview",
        accentColor = MysticPurple,
    ),
    CatalogGameEntry(
        id = "paint",
        title = "Paint to Hide",
        subtitle = "Graffiti arena blend-and-hide stealth party",
        category = "Party",
        playersLabel = "4-10 Players",
        illustrationRes = R.drawable.vb_character_paint_to_hide,
        isPlayableNow = false,
        badge = "Soon",
        accentColor = Color(0xFFEF6C00),
    ),
    CatalogGameEntry(
        id = "cafe",
        title = "Trickster's Cafe",
        subtitle = "Fast table bluffing and high-stakes trick rounds",
        category = "Board & Card",
        playersLabel = "2-6 Players",
        illustrationRes = R.drawable.vb_character_tricksters_cafe,
        isPlayableNow = false,
        badge = "Soon",
        accentColor = Color(0xFF8D6E63),
    ),
    CatalogGameEntry(
        id = "hide",
        title = "Hide And Seek",
        subtitle = "Prop hunt disguises across enchanted maps",
        category = "Party",
        playersLabel = "4-12 Players",
        illustrationRes = R.drawable.vb_character_hide_and_seek,
        isPlayableNow = false,
        badge = "Soon",
        accentColor = Color(0xFF00796B),
    ),
    CatalogGameEntry(
        id = "werewolf",
        title = "Space Werewolf",
        subtitle = "Sci-fi crewmate duties vs covert impostor sabotage",
        category = "Party",
        playersLabel = "6-10 Players",
        illustrationRes = R.drawable.vb_character_space_werewolf,
        isPlayableNow = false,
        badge = "Soon",
        accentColor = Color(0xFF5E35B1),
    ),
    CatalogGameEntry(
        id = "card",
        title = "Oh My Card",
        subtitle = "Color-and-symbol tabletop speed card race",
        category = "Board & Card",
        playersLabel = "2-4 Players",
        illustrationRes = R.drawable.vb_character_oh_my_card,
        isPlayableNow = false,
        badge = "Soon",
        accentColor = Color(0xFF2E7D32),
    ),
    CatalogGameEntry(
        id = "billiards",
        title = "Billiards Lounge",
        subtitle = "Classic 8-ball cue table challenge",
        category = "Casual",
        playersLabel = "1v1",
        illustrationRes = R.drawable.vb_room_cover_billiards_lounge,
        isPlayableNow = false,
        badge = "Soon",
        accentColor = Color(0xFF0288D1),
        isSceneCover = true,
    ),
)

private val GameCatalogFilters = listOf("All", "Playable Now", "Party", "Board & Card", "Casual")

/**
 * Dedicated Game Catalog screen separated cleanly from Live Game Room lobbies.
 */
@Composable
fun AllGamesScreen(nav: NavHostController) {
    var selectedFilter by remember { mutableStateOf("All") }
    var searchQuery by remember { mutableStateOf("") }
    var previewGame by remember { mutableStateOf<CatalogGameEntry?>(null) }

    val filteredGames = remember(selectedFilter, searchQuery) {
        AllCatalogGames.filter { game ->
            val matchesFilter = when (selectedFilter) {
                "All" -> true
                "Playable Now" -> game.isPlayableNow
                else -> game.category == selectedFilter
            }
            val q = searchQuery.trim()
            val matchesQuery = q.isEmpty() ||
                game.title.contains(q, ignoreCase = true) ||
                game.subtitle.contains(q, ignoreCase = true) ||
                game.category.contains(q, ignoreCase = true)
            matchesFilter && matchesQuery
        }
    }

    AuroraScreenCanvas(
        title = "All Games",
        subtitle = "${filteredGames.size} games in catalog",
        onBack = { nav.popBackStack() },
        rightContent = {
            Row(
                modifier = Modifier
                    .defaultMinSize(minHeight = 40.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(AuroraElevated)
                    .border(1.dp, CrystalBlue.copy(alpha = 0.45f), RoundedCornerShape(14.dp))
                    .bounceClick { nav.navigateSafe(Routes.GAME_ROOMS) }
                    .padding(horizontal = 11.dp, vertical = 7.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Live Lobbies ›",
                    color = CrystalBlue,
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        },
        scrollable = false,
    ) {
        // Search Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(AuroraSurface)
                .border(1.dp, AuroraBorder, RoundedCornerShape(14.dp))
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_search),
                contentDescription = "Search games",
                tint = DreamLavender,
                modifier = Modifier.size(18.dp),
            )
            Spacer(Modifier.width(8.dp))
            Box(Modifier.weight(1f)) {
                if (searchQuery.isEmpty()) {
                    Text(
                        text = "Search games by title or mode…",
                        color = DreamLavender.copy(alpha = 0.65f),
                        fontSize = 13.sp,
                    )
                }
                BasicTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    singleLine = true,
                    textStyle = TextStyle(color = CloudWhite, fontSize = 13.5.sp),
                    cursorBrush = SolidColor(CrystalBlue),
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            if (searchQuery.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .clickable { searchQuery = "" },
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_close),
                        contentDescription = "Clear search",
                        tint = DreamLavender,
                        modifier = Modifier.size(14.dp),
                    )
                }
            }
        }

        Spacer(Modifier.height(10.dp))

        // Filter Chips
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            items(GameCatalogFilters, key = { it }) { filter ->
                val selected = filter == selectedFilter
                Box(
                    modifier = Modifier
                        .defaultMinSize(minHeight = 36.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            if (selected) Brush.horizontalGradient(listOf(RoyalViolet, MysticPurple))
                            else Brush.linearGradient(listOf(AuroraSurface, AuroraElevated))
                        )
                        .border(
                            1.dp,
                            if (selected) CrystalBlue.copy(alpha = 0.6f) else AuroraBorder,
                            RoundedCornerShape(16.dp),
                        )
                        .bounceClick { selectedFilter = filter }
                        .padding(horizontal = 14.dp, vertical = 7.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = filter,
                        color = if (selected) CloudWhite else DreamLavender,
                        fontSize = 12.sp,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                    )
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(bottom = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            if (filteredGames.isEmpty()) {
                item(span = { GridItemSpan(2) }) {
                    AuroraEmptyStateCard(
                        title = "No matching games",
                        message = "Try another game title or reset the active category filter.",
                        emoji = "🎮",
                        actionLabel = "Show All Games",
                        onAction = {
                            searchQuery = ""
                            selectedFilter = "All"
                        },
                    )
                }
            } else {
                items(filteredGames, key = { it.id }) { game ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .bounceClick {
                                val target = game.route
                                if (game.isPlayableNow && target != null) {
                                    nav.navigateSafe(target)
                                } else {
                                    previewGame = game
                                }
                            },
                        shape = RoundedCornerShape(18.dp),
                        color = AuroraSurface.copy(alpha = 0.92f),
                        border = BorderStroke(
                            1.dp,
                            if (game.isPlayableNow) CrystalBlue.copy(alpha = 0.45f) else AuroraBorder,
                        ),
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(92.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(
                                        Brush.linearGradient(
                                            listOf(
                                                game.accentColor.copy(alpha = 0.35f),
                                                DeepInk,
                                            )
                                        )
                                    ),
                            ) {
                                Image(
                                    painter = painterResource(game.illustrationRes),
                                    contentDescription = game.title,
                                    contentScale = if (game.isSceneCover) ContentScale.Crop else ContentScale.Fit,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(if (game.isSceneCover) 0.dp else 6.dp),
                                )
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopStart)
                                        .padding(6.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(
                                            if (game.isPlayableNow) AuroraSuccess.copy(alpha = 0.9f)
                                            else Color(0xCC1E1542)
                                        )
                                        .border(
                                            0.8.dp,
                                            if (game.isPlayableNow) CloudWhite.copy(alpha = 0.35f)
                                            else RoyalGold.copy(alpha = 0.45f),
                                            RoundedCornerShape(8.dp),
                                        )
                                        .padding(horizontal = 7.dp, vertical = 2.dp),
                                ) {
                                    Text(
                                        text = if (game.isPlayableNow) "PLAYABLE" else "SOON",
                                        color = if (game.isPlayableNow) DeepInk else RoyalGold,
                                        fontSize = 9.5.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                    )
                                }
                            }

                            Spacer(Modifier.height(10.dp))

                            Text(
                                text = game.title,
                                color = CloudWhite,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                            Spacer(Modifier.height(2.dp))
                            Text(
                                text = game.subtitle,
                                color = DreamLavender.copy(alpha = 0.8f),
                                fontSize = 11.sp,
                                lineHeight = 14.sp,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                            )
                            Spacer(Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    text = game.playersLabel,
                                    color = CrystalBlue,
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.SemiBold,
                                )
                                Text(
                                    text = if (game.isPlayableNow) "Play ›" else "Info ›",
                                    color = if (game.isPlayableNow) RoyalGold else DreamLavender,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    previewGame?.let { selected ->
        AlertDialog(
            onDismissRequest = { previewGame = null },
            containerColor = DeepInk,
            titleContentColor = CloudWhite,
            textContentColor = DreamLavender,
            title = {
                Text(selected.title, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = selected.subtitle,
                        color = CloudWhite,
                        fontSize = 13.5.sp,
                    )
                    Text(
                        text = "${selected.category} · ${selected.playersLabel}",
                        color = CrystalBlue,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(
                        text = "Multiplayer matchmaking for this title is queued for a future update. Currently playable locally: Who's the Spy and Ludo Classic.",
                        color = DreamLavender.copy(alpha = 0.85f),
                        fontSize = 12.sp,
                        lineHeight = 17.sp,
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { previewGame = null },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = RoyalViolet,
                        contentColor = CloudWhite,
                    ),
                ) {
                    Text("Got it", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        previewGame = null
                        nav.navigateSafe(Routes.GAME_ROOMS)
                    },
                ) {
                    Text("Browse Live Lobbies", color = CrystalBlue)
                }
            },
        )
    }
}
