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
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
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
import com.sundram.weplay.data.FrameTier
import com.sundram.weplay.data.UserProfile
import com.sundram.weplay.data.getAvatarById
import com.sundram.weplay.ui.components.AnimatedAvatar
import com.sundram.weplay.ui.components.AvatarWithFrame
import com.sundram.weplay.ui.components.bounceClick
import com.sundram.weplay.ui.components.navigateSafe
import com.sundram.weplay.ui.nav.Routes
import com.sundram.weplay.ui.theme.AuroraBackground
import com.sundram.weplay.ui.theme.AuroraBorder
import com.sundram.weplay.ui.theme.AuroraCoral
import com.sundram.weplay.ui.theme.AuroraCyan
import com.sundram.weplay.ui.theme.AuroraGamesBackground
import com.sundram.weplay.ui.theme.AuroraGold
import com.sundram.weplay.ui.theme.AuroraGradients
import com.sundram.weplay.ui.theme.AuroraLavender
import com.sundram.weplay.ui.theme.AuroraOrchid
import com.sundram.weplay.ui.theme.AuroraRose
import com.sundram.weplay.ui.theme.AuroraSurface
import com.sundram.weplay.ui.theme.AuroraTextPrimary
import com.sundram.weplay.ui.theme.AuroraTextSecondary
import kotlinx.coroutines.launch
import java.text.NumberFormat

// ─────────────────────────────────────────────────────────────────────────────
//  1. Distinctive Aurora Paradise Header with Premium Coin Wallet & Star Icon
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun AuroraHomeHeader(nav: NavHostController, profile: UserProfile) {
    val formattedCoins = remember(profile.coins) {
        NumberFormat.getIntegerInstance().format(profile.coins)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // User Profile Avatar with subtle glow ring and spring touch feedback
        AnimatedAvatar(
            avatar = getAvatarById(profile.avatarId),
            size = 46.dp,
            animate = true,
            onClick = { nav.navigateSafe(Routes.ME) },
        )

        Spacer(Modifier.width(10.dp))

        // Personalized Welcome Greeting
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Welcome back 👋",
                color = AuroraTextSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
            )
            Text(
                text = profile.name,
                color = AuroraTextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }

        Spacer(Modifier.width(6.dp))

        // B6: Premium Coin Wallet Pill [ 🪙 1,250 + ]
        Row(
            modifier = Modifier
                .height(34.dp)
                .shadow(1.dp, RoundedCornerShape(17.dp))
                .clip(RoundedCornerShape(17.dp))
                .background(Color(0xFFFFFDF8)) // Champagne / Pearl white
                .border(1.dp, AuroraBorder, RoundedCornerShape(17.dp))
                .padding(horizontal = 7.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Tap coin balance -> Wallet
            Row(
                modifier = Modifier
                    .bounceClick { nav.navigateSafe(Routes.WALLET_SETTINGS) },
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_gold_coin_3d),
                    contentDescription = "Coins",
                    modifier = Modifier.size(20.dp),
                )
                Spacer(Modifier.width(5.dp))
                Text(
                    text = formattedCoins,
                    color = AuroraTextPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold,
                    maxLines = 1,
                )
            }

            Spacer(Modifier.width(6.dp))

            // Tap Plus -> Top Up
            Box(
                modifier = Modifier
                    .size(18.dp)
                    .clip(CircleShape)
                    .background(AuroraGradients.sunsetFantasy)
                    .bounceClick { nav.navigateSafe(Routes.TOPUP) },
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "+",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }

        Spacer(Modifier.width(8.dp))

        // B2: Upper-Right Star Icon -> Event Center
        Box(
            modifier = Modifier
                .size(34.dp)
                .shadow(1.dp, CircleShape)
                .clip(CircleShape)
                .background(AuroraSurface)
                .border(1.dp, AuroraBorder, CircleShape)
                .bounceClick { nav.navigateSafe(Routes.EVENT_CENTER) },
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(R.drawable.ic_star_event),
                contentDescription = "Event Center",
                modifier = Modifier.size(18.dp),
            )
        }

        Spacer(Modifier.width(6.dp))

        // Notifications Shortcut Button
        Box(
            modifier = Modifier
                .size(34.dp)
                .shadow(1.dp, CircleShape)
                .clip(CircleShape)
                .background(AuroraSurface)
                .border(1.dp, AuroraBorder, CircleShape)
                .bounceClick { nav.navigateSafe(Routes.NOTIFICATIONS) },
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_bell),
                contentDescription = "Notifications",
                tint = AuroraTextPrimary,
                modifier = Modifier.size(17.dp),
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  2. B1: Rebalanced 3-Action Quick Shortcuts (VIP Lounge removed from header)
// ─────────────────────────────────────────────────────────────────────────────
private data class AuroraShortcutItem(
    val title: String,
    val subtitle: String,
    val iconRes: Int,
    val tintBg: Color,
    val route: String,
)

@Composable
private fun AuroraShortcutsRow(nav: NavHostController) {
    // Exactly 3 balanced primary shortcuts (Ranks, Tasks, Friends)
    val shortcuts = listOf(
        AuroraShortcutItem("Ranks", "Leaderboard", R.drawable.ap_ic_trophy, Color(0xFFF0ECFF), Routes.RANKING),
        AuroraShortcutItem("Tasks", "Missions", R.drawable.ap_ic_shield, Color(0xFFE2F8FA), Routes.TASKS),
        AuroraShortcutItem("Friends", "Social Hub", R.drawable.ap_ic_chat, Color(0xFFFFF0EC), Routes.MSG),
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        shortcuts.forEach { item ->
            Column(
                modifier = Modifier
                    .weight(1f)
                    .shadow(2.dp, RoundedCornerShape(18.dp))
                    .clip(RoundedCornerShape(18.dp))
                    .background(AuroraSurface)
                    .border(1.dp, AuroraBorder, RoundedCornerShape(18.dp))
                    .bounceClick { nav.navigateSafe(item.route) }
                    .padding(vertical = 10.dp, horizontal = 6.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
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
                    color = AuroraTextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = item.subtitle,
                    color = AuroraTextSecondary,
                    fontSize = 10.sp,
                    maxLines = 1,
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  3. B4: Compact Multiple Game Rooms Action Bar
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun AuroraGameRoomsBanner(nav: NavHostController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .shadow(1.dp, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.horizontalGradient(
                    listOf(
                        Color(0xFFFFECE5),
                        Color(0xFFF0ECFF),
                    )
                )
            )
            .border(1.dp, AuroraBorder, RoundedCornerShape(16.dp))
            .bounceClick { nav.navigateSafe(Routes.GAME_ROOMS) }
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painter = painterResource(R.drawable.vb_room_door),
            contentDescription = null,
            modifier = Modifier.size(22.dp),
        )
        Spacer(Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Multiplayer Game Rooms",
                color = AuroraTextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "Custom matches, lobbies & voice parties",
                color = AuroraTextSecondary,
                fontSize = 10.sp,
            )
        }
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(11.dp))
                .background(AuroraGradients.sunsetFantasy)
                .padding(horizontal = 11.dp, vertical = 4.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "Browse ›",
                color = Color.White,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  4. Section A — Hero Featured Game Card (Sunset Fantasy + Aurora Cyan)
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun HeroFeaturedGameCard(
    onPlayClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(152.dp)
            .shadow(4.dp, RoundedCornerShape(22.dp))
            .clip(RoundedCornerShape(22.dp))
            .background(
                Brush.linearGradient(
                    listOf(
                        Color(0xFFFF7A68),
                        Color(0xFFA17BEB),
                        Color(0xFF42CFD8),
                    )
                )
            )
            .border(1.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(22.dp))
            .bounceClick(scaleDown = 0.98f, onClick = onPlayClick),
    ) {
        // Decorative radial highlight
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(160.dp)
                .background(Color.White.copy(alpha = 0.10f), CircleShape),
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
                    .background(AuroraGold)
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
                    color = Color.White.copy(alpha = 0.90f),
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
                    color = AuroraOrchid,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                )
            }
        }

        // Right Illustration
        Image(
            painter = painterResource(R.drawable.vb_character_petrol_pump),
            contentDescription = "Petrol Pump Simulator",
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .size(138.dp)
                .padding(end = 6.dp, bottom = 4.dp),
            contentScale = ContentScale.Fit,
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  5. Section B — Popular Games (Dreamy Sky & Sunset Fantasy Cards)
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
                .height(134.dp)
                .shadow(3.dp, RoundedCornerShape(20.dp))
                .clip(RoundedCornerShape(20.dp))
                .background(
                    Brush.linearGradient(
                        listOf(
                            Color(0xFF26B2C8),
                            Color(0xFF6E74E5),
                        )
                    )
                )
                .border(1.dp, Color.White.copy(alpha = 0.25f), RoundedCornerShape(20.dp))
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
                        color = Color.White.copy(alpha = 0.90f),
                        fontSize = 10.sp,
                    )
                }
            }

            Image(
                painter = painterResource(R.drawable.vb_character_whos_the_spy),
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(78.dp)
                    .padding(end = 4.dp, bottom = 4.dp),
                contentScale = ContentScale.Fit,
            )
        }

        // Card 2: Paint to Hide
        Box(
            modifier = Modifier
                .weight(1f)
                .height(134.dp)
                .shadow(3.dp, RoundedCornerShape(20.dp))
                .clip(RoundedCornerShape(20.dp))
                .background(
                    Brush.linearGradient(
                        listOf(
                            Color(0xFFFF7A68),
                            Color(0xFFF477AC),
                        )
                    )
                )
                .border(1.dp, Color.White.copy(alpha = 0.25f), RoundedCornerShape(20.dp))
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
                        .background(AuroraGold)
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
                        color = Color.White.copy(alpha = 0.90f),
                        fontSize = 10.sp,
                    )
                }
            }

            Image(
                painter = painterResource(R.drawable.vb_character_paint_to_hide),
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(78.dp)
                    .padding(end = 4.dp, bottom = 4.dp),
                contentScale = ContentScale.Fit,
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  6. Section C — Explore More Games (Aurora Ivory Card Grid)
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun AuroraGameGridCard(
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
            .background(AuroraSurface)
            .border(1.dp, AuroraBorder, RoundedCornerShape(18.dp))
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
                color = AuroraTextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )

            Spacer(Modifier.height(2.dp))

            Text(
                text = genre,
                color = AuroraTextSecondary,
                fontSize = 10.sp,
                maxLines = 1,
            )
        }

        if (isSceneCover) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .size(66.dp)
                    .shadow(1.dp, RoundedCornerShape(14.dp))
                    .clip(RoundedCornerShape(14.dp))
                    .border(1.dp, AuroraBorder, RoundedCornerShape(14.dp)),
            ) {
                Image(
                    painter = painterResource(illustrationRes),
                    contentDescription = title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                )
            }
        } else {
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
//  7. Dedicated Game Search Bar Component
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
                            Text("Search games (e.g. Spy, Ludo, Cafe)...", color = AuroraTextSecondary, fontSize = 13.sp)
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
//  8. Game Definition Model
// ─────────────────────────────────────────────────────────────────────────────
private data class AuroraGameItem(
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
    AuroraGameItem("1", "Petrol Pump Simulator", "Speed Station Simulator", Color(0xFF8258EE), R.drawable.vb_character_petrol_pump, "Featured"),
    AuroraGameItem("2", "Who's the Spy", "Mystery Deduction", Color(0xFF00ACC1), R.drawable.vb_character_whos_the_spy, "Party", route = Routes.SPY),
    AuroraGameItem("3", "Paint to Hide", "Graffiti Prop Party", Color(0xFFFF8F00), R.drawable.vb_character_paint_to_hide, "New"),
    AuroraGameItem("4", "Trickster's Cafe", "Card Bluffing", Color(0xFF8D6E63), R.drawable.vb_character_tricksters_cafe, "Hot"),
    AuroraGameItem("5", "Hide And Seek", "Prop Hunt", Color(0xFF00796B), R.drawable.vb_character_hide_and_seek, "Party"),
    AuroraGameItem("6", "Space Werewolf", "Sci-Fi Impostor", Color(0xFF5E35B1), R.drawable.vb_character_space_werewolf),
    AuroraGameItem("7", "Oh My Card", "Card Matching", Color(0xFF2E7D32), R.drawable.vb_character_oh_my_card),
    AuroraGameItem("8", "Ludo Classic", "Royal Board Game", Color(0xFFC62828), R.drawable.vb_character_royal_dice, "Live", route = Routes.LUDO),
    AuroraGameItem("9", "Billiards Lounge", "8-Ball Pool", Color(0xFF0288D1), R.drawable.vb_room_cover_billiards_lounge, isSceneCover = true),
)

// ─────────────────────────────────────────────────────────────────────────────
//  9. B5: Redesigned Community Players Model & Card with Equipped Frames
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
    val frameTier: FrameTier = FrameTier.COMMON,
)

private val exploreUsers = listOf(
    ExploreUser("1", "Madiha", "F", "avatar_aurora_explorer", 28, true, "< 194km", "In Voice Room", false, "Aurora explorer seeking great conversations!", frameTier = FrameTier.EPIC),
    ExploreUser("2", "L4ZYK!D!!", "M", "avatar_cyber_gamer", 35, true, "< 213km", "Playing Ludo", false, "Always up for a quick casual Ludo match!", frameTier = FrameTier.LEGENDARY),
    ExploreUser("3", "Scooby", "F", "avatar_mystic_fox", 19, true, "< 110km", "In Voice Room", false, "Music lover & trivia enthusiast 🎵", frameTier = FrameTier.RARE),
    ExploreUser("4", "Siddhi", "F", "avatar_lunar_artist", 42, false, "", "Offline", false, "Artist & midnight listener in celestial lounges", frameTier = FrameTier.MYTHIC),
    ExploreUser("5", "K O H L I", "M", "avatar_diamond_crown", 48, true, "", "Playing Spy", false, "Master of Who's the Spy. Can you guess?", frameTier = FrameTier.CELESTIAL),
    ExploreUser("6", "T!wariji", "M", "avatar_neon_musician", 16, true, "", "In Voice Room", false, "DJ in the house! Playing chill lo-fi beats", frameTier = FrameTier.COMMON),
    ExploreUser("7", "K!RAN", "F", "avatar_star_captain", 33, false, "", "Offline", false, "Silence is the best answer. Good vibes only.", frameTier = FrameTier.COMMON),
    ExploreUser("8", "Aanya", "F", "avatar_royal_dreamer", 29, true, "< 85km", "In Voice Room", false, "Royal lounge regular & friendly host", frameTier = FrameTier.EPIC),
    ExploreUser("9", "Rohan", "M", "avatar_celestial_wanderer", 21, true, "< 140km", "Playing Ludo", false, "Rolling sixes and taking over the board!", frameTier = FrameTier.RARE),
    ExploreUser("10", "Meera", "F", "avatar_crystal_guardian", 25, true, "< 60km", "In Voice Room", false, "Spreading peace and positivity in voice rooms ✨", frameTier = FrameTier.LEGENDARY),
    ExploreUser("11", "Simran", "F", "avatar_cosmic_bunny", 14, false, "", "Offline", false, "New to the community! Say hi 👋", frameTier = FrameTier.COMMON),
    ExploreUser("12", "Aryan", "M", "avatar_galaxy_dj", 37, true, "< 95km", "Playing Spy", false, "Catching the imposter every single round 🕵️", frameTier = FrameTier.MYTHIC),
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
            .shadow(1.dp, RoundedCornerShape(18.dp))
            .clip(RoundedCornerShape(18.dp))
            .background(AuroraSurface)
            .border(1.dp, AuroraBorder, RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Avatar with Equipped Animated Profile Frame from FrameTier
        Box(
            modifier = Modifier.bounceClick(onClick = onClick),
            contentAlignment = Alignment.Center,
        ) {
            AvatarWithFrame(
                name = user.name,
                photoPath = null,
                size = 52.dp,
                frameTier = user.frameTier,
                animated = user.isOnline,
                showCrown = user.level >= 40,
            )
        }

        Spacer(Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = user.name,
                    color = AuroraTextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false),
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = if (user.gender == "F") "♀" else "♂",
                    color = if (user.gender == "F") AuroraRose else AuroraCyan,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(Modifier.width(6.dp))

                // Level Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(AuroraGold)
                        .padding(horizontal = 4.dp, vertical = 1.dp),
                ) {
                    Text(
                        text = "Lv.${user.level}",
                        color = Color.Black,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.ExtraBold,
                    )
                }

                // Verified Online Green Dot ONLY when confirmed online
                if (user.isOnline) {
                    Spacer(Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF00E676)),
                    )
                }
            }

            Spacer(Modifier.height(3.dp))

            Text(
                text = user.bio,
                color = AuroraTextSecondary,
                fontSize = 11.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )

            Spacer(Modifier.height(4.dp))

            // Status Chip
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(if (user.isOnline) Color(0xFFE8F5E9) else AuroraBackground)
                    .border(0.5.dp, if (user.isOnline) Color(0xFFC8E6C9) else AuroraBorder, RoundedCornerShape(6.dp))
                    .padding(horizontal = 6.dp, vertical = 2.dp),
            ) {
                Text(
                    text = user.statusText,
                    color = if (user.isOnline) Color(0xFF2E7D32) else AuroraTextSecondary,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }

        Spacer(Modifier.width(8.dp))

        // Interaction Actions: Follow / Chat
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            // Follow Button
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isFollowed) AuroraBackground else AuroraOrchid)
                    .border(1.dp, if (isFollowed) AuroraBorder else Color.Transparent, RoundedCornerShape(12.dp))
                    .clickable { isFollowed = !isFollowed }
                    .padding(horizontal = 11.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = if (isFollowed) "Following" else "Follow",
                    color = if (isFollowed) AuroraTextSecondary else Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                )
            }

            // Message Button
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(AuroraBackground)
                    .border(1.dp, AuroraBorder, CircleShape)
                    .clickable(onClick = onMessageClick),
                contentAlignment = Alignment.Center,
            ) {
                Text("💬", fontSize = 12.sp)
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  10. Games Home Screen 3.0
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

    AuroraGamesBackground(
        modifier = Modifier.fillMaxSize(),
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
            // ── Section 1: Distinctive Home Header with Wallet & Star Event Icon ──
            item { AuroraHomeHeader(nav = nav, profile = profile) }

            // ── Section 2: Rebalanced 3-Action Shortcuts (Ranks, Tasks, Friends) ──
            item { AuroraShortcutsRow(nav = nav) }

            // ── Section 3: B4: Compact Game Rooms Banner ──
            item { AuroraGameRoomsBanner(nav = nav) }

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
                        color = AuroraTextPrimary,
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
                            .background(AuroraSurface)
                            .border(1.dp, AuroraBorder, CircleShape)
                            .bounceClick {
                                isSearchActive = !isSearchActive
                                if (!isSearchActive) searchQuery = ""
                            },
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_search),
                            contentDescription = "Search games",
                            tint = if (isSearchActive) AuroraOrchid else AuroraTextPrimary,
                            modifier = Modifier.size(16.dp),
                        )
                    }

                    Spacer(Modifier.width(10.dp))

                    Text(
                        text = "All Games ›",
                        color = AuroraOrchid,
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
                                color = AuroraTextSecondary,
                                fontSize = 14.sp,
                            )
                            Spacer(Modifier.height(10.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(AuroraLavender)
                                    .bounceClick { searchQuery = "" }
                                    .padding(horizontal = 14.dp, vertical = 6.dp),
                            ) {
                                Text("Clear search", color = AuroraOrchid, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                } else {
                    items(searchResults.chunked(2)) { pair ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            Box(Modifier.weight(1f)) {
                                val game = pair[0]
                                AuroraGameGridCard(
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
                                    AuroraGameGridCard(
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
                // ── Section 5: Section A — Hero Featured Game ──
                item {
                    HeroFeaturedGameCard(
                        onPlayClick = { comingSoon("Petrol Pump Simulator") }
                    )
                }

                // ── Section 6: Section B — Popular Games Row ──
                item {
                    PopularGamesRow(
                        onSpyClick = { nav.navigateSafe(Routes.SPY) },
                        onPaintClick = { comingSoon("Paint to Hide") }
                    )
                }

                // ── Section 7: Section C — Explore More Games ──
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            Box(Modifier.weight(1f)) {
                                AuroraGameGridCard(
                                    title = "Trickster's Cafe",
                                    genre = "Card Bluffing",
                                    accentColor = Color(0xFF8D6E63),
                                    illustrationRes = R.drawable.vb_character_tricksters_cafe,
                                    badgeText = "Hot",
                                    onClick = { comingSoon("Trickster's Cafe") },
                                )
                            }
                            Box(Modifier.weight(1f)) {
                                AuroraGameGridCard(
                                    title = "Hide And Seek",
                                    genre = "Prop Hunt",
                                    accentColor = Color(0xFF00796B),
                                    illustrationRes = R.drawable.vb_character_hide_and_seek,
                                    badgeText = "Party",
                                    onClick = { comingSoon("Hide And Seek") },
                                )
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            Box(Modifier.weight(1f)) {
                                AuroraGameGridCard(
                                    title = "Space Werewolf",
                                    genre = "Sci-Fi Impostor",
                                    accentColor = Color(0xFF5E35B1),
                                    illustrationRes = R.drawable.vb_character_space_werewolf,
                                    onClick = { comingSoon("Space Werewolf") },
                                )
                            }
                            Box(Modifier.weight(1f)) {
                                AuroraGameGridCard(
                                    title = "Oh My Card",
                                    genre = "Card Matching",
                                    accentColor = Color(0xFF2E7D32),
                                    illustrationRes = R.drawable.vb_character_oh_my_card,
                                    onClick = { comingSoon("Oh My Card") },
                                )
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            Box(Modifier.weight(1f)) {
                                AuroraGameGridCard(
                                    title = "Ludo Classic",
                                    genre = "Royal Board Game",
                                    accentColor = Color(0xFFC62828),
                                    illustrationRes = R.drawable.vb_character_royal_dice,
                                    badgeText = "Live",
                                    onClick = { nav.navigateSafe(Routes.LUDO) },
                                )
                            }
                            Box(Modifier.weight(1f)) {
                                AuroraGameGridCard(
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

                // ── Section 8: B5: Community Players with Animated Frames & Real Profiles ──
                item {
                    Spacer(Modifier.height(6.dp))
                    Column(modifier = Modifier.padding(horizontal = 2.dp)) {
                        Text(
                            text = "Community Players",
                            color = AuroraTextPrimary,
                            fontSize = 19.sp,
                            fontWeight = FontWeight.ExtraBold,
                        )
                        Text(
                            text = "Meet people who share your vibe",
                            color = AuroraTextSecondary,
                            fontSize = 12.sp,
                        )
                    }
                }

                if (exploreUsers.isEmpty()) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Text(
                                text = "No players nearby right now",
                                color = AuroraTextSecondary,
                                fontSize = 14.sp,
                            )
                        }
                    }
                } else {
                    items(exploreUsers, key = { it.id }) { user ->
                        ExploreFeedItem(
                            user = user,
                            onClick = { nav.navigateSafe(Routes.userDetailRoute(user.id)) },
                            onMessageClick = { nav.navigateSafe(Routes.chatRoute(user.id)) },
                        )
                    }
                }
            }
        }

        // ── B7: Floating 3D Gift Pack Widget (Bottom-Right, opens Celebration Sheet) ──
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 14.dp, bottom = pad.calculateBottomPadding() + 8.dp)
                .shadow(4.dp, RoundedCornerShape(18.dp))
                .clip(RoundedCornerShape(18.dp))
                .background(AuroraSurface)
                .border(1.5.dp, AuroraGold, RoundedCornerShape(18.dp))
                .bounceClick { showGiftPackSheet = true }
                .padding(horizontal = 8.dp, vertical = 6.dp),
            contentAlignment = Alignment.Center,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Image(
                    painter = painterResource(R.drawable.ic_gift_box_3d),
                    contentDescription = "3D Gift Pack",
                    modifier = Modifier.size(30.dp),
                )
                Text(
                    text = "Gift Pack",
                    color = Color(0xFFE65100),
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }

        // ── 3D Gift Pack Offers Modal Bottom Sheet ──
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
                    .background(AuroraGradients.sunsetFantasy)
                    .bounceClick { scope.launch { listState.animateScrollToItem(0) } }
                    .padding(horizontal = 14.dp, vertical = 7.dp),
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
