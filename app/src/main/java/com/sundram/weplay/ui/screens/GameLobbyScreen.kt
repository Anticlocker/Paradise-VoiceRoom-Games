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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.sundram.weplay.data.UserProfile
import com.sundram.weplay.data.getAvatarById
import com.sundram.weplay.ui.components.AnimatedAvatar
import com.sundram.weplay.ui.components.bounceClick
import com.sundram.weplay.ui.components.navigateSafe
import com.sundram.weplay.ui.nav.Routes
import com.sundram.weplay.ui.theme.VyboraBg
import com.sundram.weplay.ui.theme.VyboraBorder
import com.sundram.weplay.ui.theme.VyboraGold
import com.sundram.weplay.ui.theme.VyboraLavender
import com.sundram.weplay.ui.theme.VyboraPurple
import com.sundram.weplay.ui.theme.VyboraSurface
import com.sundram.weplay.ui.theme.VyboraTextPrimary
import com.sundram.weplay.ui.theme.VyboraTextSecondary
import kotlinx.coroutines.launch

// ─────────────────────────────────────────────────────────────────────────────
//  1. Distinctive Branded Header (Greeting + Avatar + Wallet + Actions)
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun VyboraHomeHeader(nav: NavHostController, profile: UserProfile) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // User Profile Avatar with subtle glow ring and spring touch feedback
        AnimatedAvatar(
            avatar = getAvatarById(profile.avatarId),
            size = 48.dp,
            animate = true,
            onClick = { nav.navigateSafe(Routes.ME) },
        )

        Spacer(Modifier.width(10.dp))

        // Personalized Welcome Greeting
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Welcome back 👋",
                color = VyboraTextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
            )
            Text(
                text = profile.name,
                color = VyboraTextPrimary,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }

        Spacer(Modifier.width(8.dp))

        // Compact Coin Balance Pill: [ 🪙 80 + ]
        Row(
            modifier = Modifier
                .height(34.dp)
                .shadow(1.dp, RoundedCornerShape(17.dp))
                .clip(RoundedCornerShape(17.dp))
                .background(VyboraSurface)
                .border(1.dp, VyboraBorder, RoundedCornerShape(17.dp))
                .bounceClick { nav.navigateSafe(Routes.TOPUP) }
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                painter = painterResource(R.drawable.vb_coin),
                contentDescription = "Coins",
                modifier = Modifier.size(19.dp),
            )
            Spacer(Modifier.width(5.dp))
            Text(
                text = "${profile.coins}",
                color = VyboraTextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.width(6.dp))
            Image(
                painter = painterResource(R.drawable.vb_plus),
                contentDescription = "Add coins",
                modifier = Modifier.size(14.dp),
            )
        }

        Spacer(Modifier.width(8.dp))

        // Notifications Shortcut Button
        Box(
            modifier = Modifier
                .size(34.dp)
                .shadow(1.dp, CircleShape)
                .clip(CircleShape)
                .background(VyboraSurface)
                .border(1.dp, VyboraBorder, CircleShape)
                .bounceClick { nav.navigateSafe(Routes.NOTIFICATIONS) },
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_bell),
                contentDescription = "Notifications",
                tint = VyboraTextPrimary,
                modifier = Modifier.size(17.dp),
            )
        }

        Spacer(Modifier.width(6.dp))

        // Events Shortcut Button
        Box(
            modifier = Modifier
                .size(34.dp)
                .shadow(1.dp, CircleShape)
                .clip(CircleShape)
                .background(VyboraSurface)
                .border(1.dp, VyboraBorder, CircleShape)
                .bounceClick { nav.navigateSafe(Routes.DISCOVER) },
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(R.drawable.vb_shortcut_events),
                contentDescription = "Events",
                modifier = Modifier.size(24.dp),
                contentScale = ContentScale.Fit,
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  2. Redesigned Quick Shortcuts (Modern Action Cards Row)
// ─────────────────────────────────────────────────────────────────────────────
private data class VyboraShortcutItem(
    val title: String,
    val subtitle: String,
    val iconRes: Int,
    val tintBg: Color,
    val route: String,
)

@Composable
private fun VyboraShortcutsRow(nav: NavHostController) {
    val shortcuts = listOf(
        VyboraShortcutItem("Ranks", "Leaderboard", R.drawable.vb_shortcut_ranking, Color(0xFFF3E8FF), Routes.RANKING),
        VyboraShortcutItem("Tasks", "Missions", R.drawable.vb_shortcut_tasks, Color(0xFFE0F2FE), Routes.TASKS),
        VyboraShortcutItem("Friends", "Social Hub", R.drawable.vb_shortcut_friends, Color(0xFFDCFCE7), Routes.MSG),
        VyboraShortcutItem("Lounge", "VIP Club", R.drawable.vb_shortcut_jungle_king, Color(0xFFFEF3C7), Routes.ROOM),
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        shortcuts.forEach { item ->
            Column(
                modifier = Modifier
                    .weight(1f)
                    .shadow(2.dp, RoundedCornerShape(16.dp))
                    .clip(RoundedCornerShape(16.dp))
                    .background(VyboraSurface)
                    .border(1.dp, VyboraBorder, RoundedCornerShape(16.dp))
                    .bounceClick { nav.navigateSafe(item.route) }
                    .padding(vertical = 10.dp, horizontal = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(item.tintBg),
                    contentAlignment = Alignment.Center,
                ) {
                    Image(
                        painter = painterResource(item.iconRes),
                        contentDescription = item.title,
                        modifier = Modifier.size(28.dp),
                        contentScale = ContentScale.Fit,
                    )
                }
                Spacer(Modifier.height(6.dp))
                Text(
                    text = item.title,
                    color = VyboraTextPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = item.subtitle,
                    color = VyboraTextSecondary,
                    fontSize = 9.sp,
                    maxLines = 1,
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  3. Game Rooms Access Bar
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun VyboraGameRoomsBanner(nav: NavHostController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.horizontalGradient(
                    listOf(
                        Color(0xFFEDE5FF),
                        Color(0xFFE0FAFB),
                    )
                )
            )
            .border(1.dp, VyboraBorder, RoundedCornerShape(16.dp))
            .bounceClick { nav.navigateSafe(Routes.GAME_ROOMS) }
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painter = painterResource(R.drawable.vb_room_door),
            contentDescription = null,
            modifier = Modifier.size(24.dp),
        )
        Spacer(Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Multiplayer Game Rooms",
                color = VyboraTextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "Join custom matches, lobbies & voice parties",
                color = VyboraTextSecondary,
                fontSize = 11.sp,
            )
        }
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(VyboraPurple)
                .padding(horizontal = 10.dp, vertical = 4.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "Browse ›",
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  4. Section A — Hero Featured Game Card (Original Anti-Aliased Composition)
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun HeroFeaturedGameCard(
    onPlayClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(148.dp)
            .shadow(4.dp, RoundedCornerShape(22.dp))
            .clip(RoundedCornerShape(22.dp))
            .background(
                Brush.linearGradient(
                    listOf(
                        Color(0xFF5B22D4),
                        Color(0xFF7A42F4),
                        Color(0xFF27D2DC),
                    )
                )
            )
            // Anti-aliasing stroke along smooth rounded corner curve
            .border(1.dp, Color.White.copy(alpha = 0.25f), RoundedCornerShape(22.dp))
            .bounceClick(scaleDown = 0.98f, onClick = onPlayClick),
    ) {
        // Decorative radial highlight
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(160.dp)
                .background(Color.White.copy(alpha = 0.08f), CircleShape),
        )

        // Left Content: Tag + Title + Subtitle + Action Button
        Column(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 18.dp, top = 14.dp, bottom = 14.dp)
                .fillMaxWidth(0.58f),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            // Featured Tag
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(VyboraGold)
                    .padding(horizontal = 8.dp, vertical = 2.dp),
            ) {
                Text(
                    text = "⚡ FEATURED GAME",
                    color = Color(0xFF4A3200),
                    fontSize = 9.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 0.5.sp,
                )
            }

            Column {
                Text(
                    text = "Petrol Pump Simulator",
                    color = Color.White,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.ExtraBold,
                    lineHeight = 21.sp,
                )
                Spacer(Modifier.height(3.dp))
                Text(
                    text = "Refuel, manage & upgrade your speed station",
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = 11.sp,
                    lineHeight = 14.sp,
                    maxLines = 2,
                )
            }

            // Play Action Pill
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color.White)
                    .padding(horizontal = 12.dp, vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "▶ Play Now",
                    color = VyboraPurple,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                )
            }
        }

        // Right Illustration with pristine proportions and safe transparent margins
        Image(
            painter = painterResource(R.drawable.vb_character_petrol_pump),
            contentDescription = "Petrol Pump Simulator",
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .size(136.dp)
                .padding(end = 6.dp, bottom = 4.dp),
            contentScale = ContentScale.Fit,
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  5. Section B — Popular Games (Balanced 2-Card Row with Smooth Rounded Edges)
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun PopularGamesRow(
    onSpyClick: () -> Unit,
    onPaintClick: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        // Card 1: Who's the Spy
        Box(
            modifier = Modifier
                .weight(1f)
                .height(132.dp)
                .shadow(3.dp, RoundedCornerShape(20.dp))
                .clip(RoundedCornerShape(20.dp))
                .background(
                    Brush.linearGradient(
                        listOf(
                            Color(0xFF0277BD),
                            Color(0xFF00ACC1),
                        )
                    )
                )
                // Smooth anti-aliased edge outline
                .border(1.dp, Color.White.copy(alpha = 0.22f), RoundedCornerShape(20.dp))
                .bounceClick(scaleDown = 0.97f, onClick = onSpyClick),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color.White.copy(alpha = 0.25f))
                        .padding(horizontal = 6.dp, vertical = 2.dp),
                ) {
                    Text("🔍 Mystery", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                }

                Column(modifier = Modifier.fillMaxWidth(0.62f)) {
                    Text(
                        text = "Who's the Spy",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 17.sp,
                    )
                    Text(
                        text = "Pass & play",
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 10.sp,
                    )
                }
            }

            Image(
                painter = painterResource(R.drawable.vb_character_whos_the_spy),
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(76.dp)
                    .padding(end = 4.dp, bottom = 4.dp),
                contentScale = ContentScale.Fit,
            )
        }

        // Card 2: Paint to Hide
        Box(
            modifier = Modifier
                .weight(1f)
                .height(132.dp)
                .shadow(3.dp, RoundedCornerShape(20.dp))
                .clip(RoundedCornerShape(20.dp))
                .background(
                    Brush.linearGradient(
                        listOf(
                            Color(0xFFE65100),
                            Color(0xFFFF8F00),
                        )
                    )
                )
                // Smooth anti-aliased edge outline
                .border(1.dp, Color.White.copy(alpha = 0.22f), RoundedCornerShape(20.dp))
                .bounceClick(scaleDown = 0.97f, onClick = onPaintClick),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(VyboraGold)
                        .padding(horizontal = 6.dp, vertical = 2.dp),
                ) {
                    Text("🎨 New", color = Color(0xFF4A3200), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                }

                Column(modifier = Modifier.fillMaxWidth(0.62f)) {
                    Text(
                        text = "Paint to Hide",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 17.sp,
                    )
                    Text(
                        text = "Graffiti party",
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 10.sp,
                    )
                }
            }

            Image(
                painter = painterResource(R.drawable.vb_character_paint_to_hide),
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(76.dp)
                    .padding(end = 4.dp, bottom = 4.dp),
                contentScale = ContentScale.Fit,
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  6. Section C — Explore More Games (Smooth 2-Column Grid Card)
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun VyboraGameGridCard(
    title: String,
    genre: String,
    accentColor: Color,
    illustrationRes: Int,
    badgeText: String? = null,
    isSceneCover: Boolean = false,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(112.dp)
            .shadow(2.dp, RoundedCornerShape(18.dp))
            .clip(RoundedCornerShape(18.dp))
            .background(VyboraSurface)
            .border(1.dp, VyboraBorder, RoundedCornerShape(18.dp))
            .bounceClick(scaleDown = 0.97f, onClick = onClick)
            .padding(12.dp),
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .fillMaxWidth(0.58f),
            verticalArrangement = Arrangement.Center,
        ) {
            if (badgeText != null) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(accentColor.copy(alpha = 0.15f))
                        .padding(horizontal = 5.dp, vertical = 2.dp),
                ) {
                    Text(badgeText, color = accentColor, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.height(4.dp))
            }

            Text(
                text = title,
                color = VyboraTextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )

            Spacer(Modifier.height(2.dp))

            Text(
                text = genre,
                color = VyboraTextSecondary,
                fontSize = 10.sp,
                maxLines = 1,
            )
        }

        if (isSceneCover) {
            // Rounded thumbnail container for square scene covers (e.g. Billiards)
            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .size(66.dp)
                    .shadow(1.dp, RoundedCornerShape(14.dp))
                    .clip(RoundedCornerShape(14.dp))
                    .border(1.dp, VyboraBorder, RoundedCornerShape(14.dp)),
            ) {
                Image(
                    painter = painterResource(illustrationRes),
                    contentDescription = title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                )
            }
        } else {
            // Transparent character cutout
            Image(
                painter = painterResource(illustrationRes),
                contentDescription = title,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .size(72.dp),
                contentScale = ContentScale.Fit,
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  7. Dedicated Game Search Bar Component (Phase 3 Requirement)
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun GameSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onClose: () -> Unit,
) {
    val focusManager = LocalFocusManager.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 2.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .height(40.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(VyboraSurface)
                .border(1.dp, VyboraBorder, RoundedCornerShape(20.dp))
                .padding(horizontal = 12.dp),
            contentAlignment = Alignment.CenterStart,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(R.drawable.ic_search),
                    contentDescription = null,
                    tint = VyboraTextSecondary,
                    modifier = Modifier.size(16.dp),
                )
                Spacer(Modifier.width(8.dp))
                BasicTextField(
                    value = query,
                    onValueChange = onQueryChange,
                    singleLine = true,
                    textStyle = TextStyle(color = VyboraTextPrimary, fontSize = 13.sp),
                    cursorBrush = SolidColor(VyboraPurple),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
                    modifier = Modifier.weight(1f),
                    decorationBox = { inner ->
                        if (query.isEmpty()) {
                            Text("Search games (e.g. Spy, Ludo, Cafe)...", color = VyboraTextSecondary, fontSize = 13.sp)
                        }
                        inner()
                    },
                )
                if (query.isNotEmpty()) {
                    Icon(
                        painter = painterResource(R.drawable.ic_close),
                        contentDescription = "Clear search",
                        tint = VyboraTextSecondary,
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
            color = VyboraPurple,
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
//  8. Game Definition Model for Home Screen Filtering
// ─────────────────────────────────────────────────────────────────────────────
private data class VyboraGameItem(
    val id: String,
    val title: String,
    val genre: String,
    val accentColor: Color,
    val illustrationRes: Int,
    val badgeText: String? = null,
    val isSceneCover: Boolean = false,
    val route: String? = null,
)

private val allHomeGames = listOf(
    VyboraGameItem("1", "Petrol Pump Simulator", "Speed Station Simulator", Color(0xFF8258EE), R.drawable.vb_character_petrol_pump, "Featured"),
    VyboraGameItem("2", "Who's the Spy", "Mystery Deduction", Color(0xFF00ACC1), R.drawable.vb_character_whos_the_spy, "Party", route = Routes.SPY),
    VyboraGameItem("3", "Paint to Hide", "Graffiti Prop Party", Color(0xFFFF8F00), R.drawable.vb_character_paint_to_hide, "New"),
    VyboraGameItem("4", "Trickster's Cafe", "Card Bluffing", Color(0xFF8D6E63), R.drawable.vb_character_tricksters_cafe, "Hot"),
    VyboraGameItem("5", "Hide And Seek", "Prop Hunt", Color(0xFF00796B), R.drawable.vb_character_hide_and_seek, "Party"),
    VyboraGameItem("6", "Space Werewolf", "Sci-Fi Impostor", Color(0xFF5E35B1), R.drawable.vb_character_space_werewolf),
    VyboraGameItem("7", "Oh My Card", "Card Matching", Color(0xFF2E7D32), R.drawable.vb_character_oh_my_card),
    VyboraGameItem("8", "Ludo Classic", "Royal Board Game", Color(0xFFC62828), R.drawable.vb_character_royal_dice, "Live", route = Routes.LUDO),
    VyboraGameItem("9", "Billiards Lounge", "8-Ball Pool", Color(0xFF0288D1), R.drawable.vb_room_cover_billiards_lounge, isSceneCover = true),
)

// ─────────────────────────────────────────────────────────────────────────────
//  9. Explore Feed Model & Item (Preserved for social discovery)
// ─────────────────────────────────────────────────────────────────────────────
data class ExploreUser(
    val id: String,
    val name: String,
    val gender: String, // "F" or "M"
    val avatarId: String,
    val level: Int,
    val isOnline: Boolean,
    val distance: String,
    val statusText: String,
    val isGameRoom: Boolean = false,
    val bio: String,
    val isFollowed: Boolean = false,
)

private val exploreUsers = listOf(
    ExploreUser("1", "Madiha", "F", "avatar_aurora_explorer", 28, true, "< 194km", "In Voice Room", false, "Vybora explorer seeking great conversations!"),
    ExploreUser("2", "L4ZYK!D!!", "M", "avatar_cyber_gamer", 35, true, "< 213km", "Playing Ludo", false, "Always up for a quick casual Ludo match!"),
    ExploreUser("3", "Scooby", "F", "avatar_mystic_fox", 19, true, "< 110km", "In Voice Room", false, "Music lover & trivia enthusiast 🎵"),
    ExploreUser("4", "Siddhi", "F", "avatar_lunar_artist", 42, false, "", "Offline", false, "Artist & midnight listener in celestial lounges"),
    ExploreUser("5", "K O H L I", "M", "avatar_diamond_crown", 48, true, "", "Playing Spy", false, "Master of Who's the Spy. Can you guess?"),
    ExploreUser("6", "T!wariji", "M", "avatar_neon_musician", 16, true, "", "In Voice Room", false, "DJ in the house! Playing chill lo-fi beats"),
    ExploreUser("7", "K!RAN", "F", "avatar_star_captain", 33, false, "", "Offline", false, "Silence is the best answer. Good vibes only."),
    ExploreUser("8", "Aanya", "F", "avatar_royal_dreamer", 29, true, "< 85km", "In Voice Room", false, "Royal lounge regular & friendly host"),
    ExploreUser("9", "Rohan", "M", "avatar_celestial_wanderer", 21, true, "< 140km", "Playing Ludo", false, "Rolling sixes and taking over the board!"),
    ExploreUser("10", "Meera", "F", "avatar_crystal_guardian", 25, true, "< 60km", "In Voice Room", false, "Spreading peace and positivity in voice rooms ✨"),
    ExploreUser("11", "Simran", "F", "avatar_cosmic_bunny", 14, false, "", "Offline", false, "New to VYBORA! Say hi 👋"),
    ExploreUser("12", "Aryan", "M", "avatar_galaxy_dj", 37, true, "< 95km", "Playing Spy", false, "Catching the imposter every single round 🕵️"),
)

@Composable
private fun ExploreFeedItem(
    user: ExploreUser,
    onClick: () -> Unit = {},
    onMessageClick: () -> Unit = {},
) {
    var isFollowed by rememberSaveable { mutableStateOf(user.isFollowed) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(1.dp, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .background(VyboraSurface)
            .border(1.dp, VyboraBorder, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Animated 2.5D Character Avatar with Shimmer Glow Frame
        AnimatedAvatar(
            avatar = getAvatarById(user.avatarId),
            size = 46.dp,
            animate = true,
            onClick = onClick,
        )

        Spacer(Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = user.name,
                    color = VyboraTextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = if (user.gender == "F") "♀" else "♂",
                    color = if (user.gender == "F") Color(0xFFE91E63) else Color(0xFF1E88E5),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(Modifier.width(6.dp))

                // Level Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(VyboraGold)
                        .padding(horizontal = 4.dp, vertical = 1.dp),
                ) {
                    Text(
                        text = "Lv.${user.level}",
                        color = Color.Black,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.ExtraBold,
                    )
                }

                // Verified Online Green Dot
                if (user.isOnline) {
                    Spacer(Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF00E676)),
                    )
                }

                if (user.distance.isNotEmpty()) {
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = user.distance,
                        color = VyboraTextSecondary,
                        fontSize = 10.sp,
                    )
                }
            }

            Spacer(Modifier.height(3.dp))

            Text(
                text = user.bio,
                color = VyboraTextSecondary,
                fontSize = 11.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )

            Spacer(Modifier.height(4.dp))

            // Status Chip
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(if (user.isOnline) Color(0xFFE8F5E9) else Color(0xFFF5F5F5))
                    .padding(horizontal = 6.dp, vertical = 2.dp),
            ) {
                Text(
                    text = user.statusText,
                    color = if (user.isOnline) Color(0xFF2E7D32) else VyboraTextSecondary,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }

        Spacer(Modifier.width(8.dp))

        // Action Buttons Row: Follow + Message
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            // Follow Button
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isFollowed) VyboraBg else VyboraPurple)
                    .border(1.dp, if (isFollowed) VyboraBorder else Color.Transparent, RoundedCornerShape(10.dp))
                    .clickable { isFollowed = !isFollowed }
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = if (isFollowed) "Following" else "Follow",
                    color = if (isFollowed) VyboraTextSecondary else Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                )
            }

            // Message Button
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(VyboraBg)
                    .border(1.dp, VyboraBorder, CircleShape)
                    .clickable(onClick = onMessageClick),
                contentAlignment = Alignment.Center,
            ) {
                Text("💬", fontSize = 12.sp)
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  10. Gaming Gift Pack Modal Bottom Sheet (Phase 7 Requirement)
// ─────────────────────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeGiftPackSheet(
    onDismiss: () -> Unit,
    onBrowseGifts: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = VyboraSurface,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Image(
                painter = painterResource(R.drawable.vb_gift_pack),
                contentDescription = null,
                modifier = Modifier.size(68.dp),
            )

            Spacer(Modifier.height(10.dp))

            Text(
                text = "Gaming Hub Gift Pack",
                color = VyboraTextPrimary,
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold,
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = "Unlock game bonuses, dice skins, spy badges & coin perks",
                color = VyboraTextSecondary,
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
            )

            Spacer(Modifier.height(16.dp))

            val perks = listOf(
                "🎲 Exclusive Royal Gold Dice Skin for Ludo",
                "🕵️ 3x Free Spy Clue Hints & Magnifier Frame",
                "⚡ 20% Faster XP Gain in Multiplayer Rooms",
                "🪙 200 Welcome Bonus Game Chips",
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(VyboraBg)
                    .border(1.dp, VyboraBorder, RoundedCornerShape(14.dp))
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                perks.forEach { perk ->
                    Text(text = perk, color = VyboraTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Medium)
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
                    .background(VyboraPurple)
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

            Spacer(Modifier.height(8.dp))

            Text(
                text = "Browse Full Gifts Collection ›",
                color = VyboraPurple,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .clickable(onClick = onBrowseGifts)
                    .padding(vertical = 6.dp),
            )

            Spacer(Modifier.height(16.dp))
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  11. Complete Games Home Screen (VYBORA Redesign + Audit Polished)
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun GamesHomeScreen(
    nav: NavHostController,
    pad: PaddingValues,
    profile: UserProfile,
) {
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val showBackToTop by remember {
        derivedStateOf { listState.firstVisibleItemIndex > 3 }
    }
    val snackbarHostState = remember { SnackbarHostState() }

    var isSearchActive by rememberSaveable { mutableStateOf(false) }
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var showGiftPackSheet by rememberSaveable { mutableStateOf(false) }

    fun comingSoon(name: String) {
        scope.launch {
            snackbarHostState.showSnackbar("Coming soon – $name is currently in development [DEMO]")
        }
    }

    // Filtered games when search is active
    val searchResults = remember(searchQuery) {
        if (searchQuery.isBlank()) {
            emptyList()
        } else {
            allHomeGames.filter { game ->
                game.title.contains(searchQuery, ignoreCase = true) ||
                        game.genre.contains(searchQuery, ignoreCase = true) ||
                        (game.badgeText?.contains(searchQuery, ignoreCase = true) == true)
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(VyboraBg),
    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding(),
            contentPadding = PaddingValues(
                start = 14.dp,
                end = 14.dp,
                top = 8.dp,
                bottom = pad.calculateBottomPadding() + 24.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            // ── Section 1: Distinctive Home Header ──
            item { VyboraHomeHeader(nav = nav, profile = profile) }

            // ── Section 2: Quick Action Tiles (Ranks, Tasks, Friends, VIP) ──
            item { VyboraShortcutsRow(nav = nav) }

            // ── Section 3: Game Rooms Access Banner ──
            item { VyboraGameRoomsBanner(nav = nav) }

            // ── Section 4: Games Discovery Header + Search Toggle ──
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 2.dp, vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Gaming Hub",
                        color = VyboraTextPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                    )

                    Spacer(Modifier.weight(1f))

                    // In-page Game Search Toggle Button
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .shadow(1.dp, CircleShape)
                            .clip(CircleShape)
                            .background(VyboraSurface)
                            .border(1.dp, VyboraBorder, CircleShape)
                            .bounceClick {
                                isSearchActive = !isSearchActive
                                if (!isSearchActive) searchQuery = ""
                            },
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_search),
                            contentDescription = "Search games",
                            tint = if (isSearchActive) VyboraPurple else VyboraTextPrimary,
                            modifier = Modifier.size(16.dp),
                        )
                    }

                    Spacer(Modifier.width(10.dp))

                    Text(
                        text = "All Games ›",
                        color = VyboraPurple,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .bounceClick { nav.navigateSafe(Routes.GAME_ROOMS) }
                            .padding(vertical = 4.dp),
                    )
                }
            }

            // In-page Search Bar (if search active)
            if (isSearchActive) {
                item {
                    GameSearchBar(
                        query = searchQuery,
                        onQueryChange = { searchQuery = it },
                        onClose = {
                            isSearchActive = false
                            searchQuery = ""
                        },
                    )
                }
            }

            // ── Search Results or Default Sections ──
            if (isSearchActive && searchQuery.isNotBlank()) {
                if (searchResults.isEmpty()) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Text(
                                text = "No games found matching \"$searchQuery\"",
                                color = VyboraTextSecondary,
                                fontSize = 14.sp,
                            )
                            Spacer(Modifier.height(10.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(VyboraLavender)
                                    .bounceClick { searchQuery = "" }
                                    .padding(horizontal = 14.dp, vertical = 6.dp),
                            ) {
                                Text("Clear search", color = VyboraPurple, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                } else {
                    // Responsive 2-column grid of search results
                    items(searchResults.chunked(2)) { pair ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            Box(Modifier.weight(1f)) {
                                val game = pair[0]
                                VyboraGameGridCard(
                                    title = game.title,
                                    genre = game.genre,
                                    accentColor = game.accentColor,
                                    illustrationRes = game.illustrationRes,
                                    badgeText = game.badgeText,
                                    isSceneCover = game.isSceneCover,
                                    onClick = {
                                        if (game.route != null) nav.navigateSafe(game.route)
                                        else comingSoon(game.title)
                                    },
                                )
                            }
                            if (pair.size > 1) {
                                Box(Modifier.weight(1f)) {
                                    val game = pair[1]
                                    VyboraGameGridCard(
                                        title = game.title,
                                        genre = game.genre,
                                        accentColor = game.accentColor,
                                        illustrationRes = game.illustrationRes,
                                        badgeText = game.badgeText,
                                        isSceneCover = game.isSceneCover,
                                        onClick = {
                                            if (game.route != null) nav.navigateSafe(game.route)
                                            else comingSoon(game.title)
                                        },
                                    )
                                }
                            } else {
                                Spacer(Modifier.weight(1f))
                            }
                        }
                    }
                }
            } else {
                // ── Section 5: Section A — Prominent Hero Featured Game ──
                item {
                    HeroFeaturedGameCard(
                        onPlayClick = { comingSoon("Petrol Pump Simulator") }
                    )
                }

                // ── Section 6: Section B — Popular Games Row (Who's the Spy & Paint to Hide) ──
                item {
                    PopularGamesRow(
                        onSpyClick = { nav.navigateSafe(Routes.SPY) },
                        onPaintClick = { comingSoon("Paint to Hide") }
                    )
                }

                // ── Section 7: Section C — Explore More Games (2-Column Grid) ──
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        // Row 1: Trickster's Cafe & Hide and Seek
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            Box(Modifier.weight(1f)) {
                                VyboraGameGridCard(
                                    title = "Trickster's Cafe",
                                    genre = "Card Bluffing",
                                    accentColor = Color(0xFF8D6E63),
                                    illustrationRes = R.drawable.vb_character_tricksters_cafe,
                                    badgeText = "Hot",
                                    onClick = { comingSoon("Trickster's Cafe") },
                                )
                            }
                            Box(Modifier.weight(1f)) {
                                VyboraGameGridCard(
                                    title = "Hide And Seek",
                                    genre = "Prop Hunt",
                                    accentColor = Color(0xFF00796B),
                                    illustrationRes = R.drawable.vb_character_hide_and_seek,
                                    badgeText = "Party",
                                    onClick = { comingSoon("Hide And Seek") },
                                )
                            }
                        }

                        // Row 2: Space Werewolf & Oh My Card
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            Box(Modifier.weight(1f)) {
                                VyboraGameGridCard(
                                    title = "Space Werewolf",
                                    genre = "Sci-Fi Impostor",
                                    accentColor = Color(0xFF5E35B1),
                                    illustrationRes = R.drawable.vb_character_space_werewolf,
                                    onClick = { comingSoon("Space Werewolf") },
                                )
                            }
                            Box(Modifier.weight(1f)) {
                                VyboraGameGridCard(
                                    title = "Oh My Card",
                                    genre = "Card Matching",
                                    accentColor = Color(0xFF2E7D32),
                                    illustrationRes = R.drawable.vb_character_oh_my_card,
                                    onClick = { comingSoon("Oh My Card") },
                                )
                            }
                        }

                        // Row 3: Ludo & Billiards
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            Box(Modifier.weight(1f)) {
                                VyboraGameGridCard(
                                    title = "Ludo Classic",
                                    genre = "Royal Board Game",
                                    accentColor = Color(0xFFC62828),
                                    illustrationRes = R.drawable.vb_character_royal_dice,
                                    badgeText = "Live",
                                    onClick = { nav.navigateSafe(Routes.LUDO) },
                                )
                            }
                            Box(Modifier.weight(1f)) {
                                VyboraGameGridCard(
                                    title = "Billiards Lounge",
                                    genre = "8-Ball Pool",
                                    accentColor = Color(0xFF0288D1),
                                    illustrationRes = R.drawable.vb_room_cover_billiards_lounge,
                                    isSceneCover = true,
                                    onClick = { comingSoon("Billiards Lounge") },
                                )
                            }
                        }
                    }
                }

                // ── Section 8: Community Players ──
                item {
                    Spacer(Modifier.height(6.dp))
                    Column(modifier = Modifier.padding(horizontal = 2.dp)) {
                        Text(
                            text = "Community Players",
                            color = VyboraTextPrimary,
                            fontSize = 19.sp,
                            fontWeight = FontWeight.ExtraBold,
                        )
                        Text(
                            text = "Meet people who share your vibe",
                            color = VyboraTextSecondary,
                            fontSize = 12.sp,
                        )
                    }
                }

                items(exploreUsers, key = { it.id }) { user ->
                    ExploreFeedItem(
                        user = user,
                        onClick = { nav.navigateSafe(Routes.ROOM) },
                        onMessageClick = { nav.navigateSafe(Routes.chatRoute(user.id)) },
                    )
                }
            }
        }

        // ── Floating Gift Pack Widget (Bottom-Right, opens ModalBottomSheet) ──
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 14.dp, bottom = pad.calculateBottomPadding() + 8.dp)
                .shadow(6.dp, RoundedCornerShape(18.dp))
                .clip(RoundedCornerShape(18.dp))
                .background(VyboraSurface)
                .border(1.5.dp, VyboraGold, RoundedCornerShape(18.dp))
                .bounceClick { showGiftPackSheet = true }
                .padding(horizontal = 8.dp, vertical = 6.dp),
            contentAlignment = Alignment.Center,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Image(
                    painter = painterResource(R.drawable.vb_gift_pack),
                    contentDescription = "Gift Pack",
                    modifier = Modifier.size(28.dp),
                )
                Text(
                    text = "Gift Pack",
                    color = Color(0xFFE65100),
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }

        // ── Gift Pack Offers Modal Bottom Sheet ──
        if (showGiftPackSheet) {
            com.sundram.weplay.ui.components.GiftPackOffersSheet(
                userId = profile.id,
                onDismiss = { showGiftPackSheet = false },
                onOpenTopUp = {
                    showGiftPackSheet = false
                    nav.navigateSafe(Routes.TOPUP)
                },
            )
        }

        // ── Back to Top Pill (when scrolled) ──
        AnimatedVisibility(
            visible = showBackToTop,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 14.dp, bottom = pad.calculateBottomPadding() + 10.dp),
        ) {
            Box(
                modifier = Modifier
                    .shadow(3.dp, CircleShape)
                    .clip(CircleShape)
                    .background(VyboraPurple)
                    .bounceClick { scope.launch { listState.animateScrollToItem(0) } }
                    .padding(horizontal = 12.dp, vertical = 7.dp),
                contentAlignment = Alignment.Center,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("↑", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.width(4.dp))
                    Text("Top", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // ── Snackbar Host ──
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = pad.calculateBottomPadding() + 70.dp),
        )
    }
}

// ── Backwards compatibility alias for MainActivity ───────────────────────────
@Composable
fun GameLobbyScreen(
    nav: NavHostController,
    pad: PaddingValues,
    profile: UserProfile,
) = GamesHomeScreen(nav, pad, profile)
