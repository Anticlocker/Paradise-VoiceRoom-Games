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
//  1. Distinctive Aurora Paradise 3.0 Header with Premium Coins & Diamond Crystals
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun AuroraHomeHeader(nav: NavHostController, profile: UserProfile) {
    com.sundram.weplay.ui.theme.CelestialHeader(
        avatarRes = getAvatarById(profile.avatarId).drawableRes,
        userName = profile.name,
        userLevel = profile.level,
        coins = profile.coins,
        crystals = null, // Verified wallet balance: crystals safely omitted until supported by backend wallet
        onAvatarClick = { nav.navigateSafe(Routes.ME) },
        onCoinsClick = { nav.navigateSafe(Routes.WALLET_SETTINGS) },
        onCrystalsClick = { nav.navigateSafe(Routes.TOPUP) },
        onEventsClick = { nav.navigateSafe(Routes.EVENT_CENTER) },
        onNotificationsClick = { nav.navigateSafe(Routes.NOTIFICATIONS) },
    )
}

// ─────────────────────────────────────────────────────────────────────────────
//  2. Rebalanced Quick Shortcuts with Celestial 3D Styling
// ─────────────────────────────────────────────────────────────────────────────
private data class AuroraShortcutItem(
    val title: String,
    val subtitle: String,
    val iconRes: Int,
    val route: String,
    val badge: String? = null,
)

@Composable
private fun AuroraShortcutsRow(nav: NavHostController) {
    val shortcuts = listOf(
        AuroraShortcutItem("Ranks", "Leaderboard", R.drawable.ap_ic_trophy, Routes.RANKING),
        AuroraShortcutItem("Tasks", "Daily", R.drawable.ap_ic_shield, Routes.TASKS),
        AuroraShortcutItem("Social", "Chats", R.drawable.ap_ic_chat, Routes.MSG),
        AuroraShortcutItem("Events", "Festival", R.drawable.aurora3_ic_nav_star, Routes.EVENT_CENTER, "HOT"),
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        shortcuts.forEach { item ->
            com.sundram.weplay.ui.theme.PremiumShortcut(
                title = item.title,
                subtitle = item.subtitle,
                iconRes = item.iconRes,
                badgeText = item.badge,
                onClick = { nav.navigateSafe(item.route) },
                modifier = Modifier.weight(1f),
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  3. Compact Glossy Game Rooms Banner
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun AuroraGameRoomsBanner(nav: NavHostController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .shadow(2.dp, RoundedCornerShape(18.dp))
            .clip(RoundedCornerShape(18.dp))
            .background(
                Brush.horizontalGradient(
                    listOf(
                        Color(0xFF2B165C),
                        Color(0xFF1B1242),
                    )
                )
            )
            .border(1.2.dp, com.sundram.weplay.ui.theme.DreamLavender.copy(alpha = 0.35f), RoundedCornerShape(18.dp))
            .bounceClick { nav.navigateSafe(Routes.GAME_ROOMS) }
            .padding(horizontal = 14.dp, vertical = 6.dp),
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
                color = com.sundram.weplay.ui.theme.CloudWhite,
                fontSize = 13.5.sp,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "Custom lobbies, voice parties & competitions",
                color = com.sundram.weplay.ui.theme.DreamLavender,
                fontSize = 10.sp,
            )
        }
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(AuroraGradients.sunsetFantasy)
                .border(0.8.dp, com.sundram.weplay.ui.theme.CloudWhite.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                .padding(horizontal = 12.dp, vertical = 5.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "Browse ›",
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  4. Section A — Hero Featured Game & Festival Carousel
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun HeroFeaturedGameCard(
    onPlayClick: () -> Unit,
    onFestivalClick: () -> Unit,
) {
    var activeSlide by rememberSaveable { mutableStateOf(0) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(156.dp)
            .shadow(6.dp, RoundedCornerShape(22.dp))
            .clip(RoundedCornerShape(22.dp))
            .background(
                Brush.horizontalGradient(
                    listOf(
                        Color(0xFF381568),
                        Color(0xFF5A22A8),
                        Color(0xFF24104B),
                    )
                )
            )
            .border(1.2.dp, com.sundram.weplay.ui.theme.RoyalGold.copy(alpha = 0.65f), RoundedCornerShape(22.dp))
            .bounceClick(scaleDown = 0.98f, onClick = {
                if (activeSlide == 0) onPlayClick() else onFestivalClick()
            }),
    ) {
        if (activeSlide == 0) {
            // Slide 0: Petrol Pump Simulator
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(160.dp)
                    .background(
                        Brush.radialGradient(
                            listOf(com.sundram.weplay.ui.theme.CrystalBlue.copy(alpha = 0.20f), Color.Transparent)
                        ),
                        CircleShape
                    ),
            )

            Column(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 18.dp, top = 14.dp, bottom = 14.dp)
                    .fillMaxWidth(0.58f),
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(com.sundram.weplay.ui.theme.AuroraGradients.royalGold)
                        .padding(horizontal = 8.dp, vertical = 2.dp),
                ) {
                    Text(
                        text = "⚡ FEATURED GAME",
                        color = com.sundram.weplay.ui.theme.DeepInk,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 0.5.sp,
                    )
                }

                Column {
                    Text(
                        text = "Petrol Pump Simulator",
                        color = com.sundram.weplay.ui.theme.CloudWhite,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.ExtraBold,
                        lineHeight = 21.sp,
                    )
                    Spacer(Modifier.height(3.dp))
                    Text(
                        text = "Refuel, manage & upgrade your speed station",
                        color = com.sundram.weplay.ui.theme.DreamLavender,
                        fontSize = 11.sp,
                        lineHeight = 14.sp,
                        maxLines = 2,
                    )
                }

                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(com.sundram.weplay.ui.theme.AuroraGradients.royalGold)
                        .border(0.8.dp, com.sundram.weplay.ui.theme.CloudWhite, RoundedCornerShape(14.dp))
                        .padding(horizontal = 14.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "▶ Play Now",
                        color = com.sundram.weplay.ui.theme.DeepInk,
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Black,
                    )
                }
            }

            Image(
                painter = painterResource(R.drawable.vb_character_petrol_pump),
                contentDescription = "Petrol Pump Simulator",
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(138.dp)
                    .padding(end = 6.dp, bottom = 4.dp),
                contentScale = ContentScale.Fit,
            )
        } else {
            // Slide 1: Featured Celestial Festival Banner (ap3r_banner_home_festival)
            Image(
                painter = painterResource(R.drawable.ap3r_banner_home_festival),
                contentDescription = "Festival Gala",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                com.sundram.weplay.ui.theme.CelestialNavy.copy(alpha = 0.85f),
                                Color.Transparent,
                            )
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 18.dp, top = 14.dp, bottom = 14.dp)
                    .fillMaxWidth(0.60f),
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(com.sundram.weplay.ui.theme.SunsetRose)
                        .padding(horizontal = 8.dp, vertical = 2.dp),
                ) {
                    Text(
                        text = "✨ CELESTIAL FESTIVAL",
                        color = Color.White,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 0.5.sp,
                    )
                }

                Column {
                    Text(
                        text = "Starlight & Dice Gala",
                        color = com.sundram.weplay.ui.theme.CloudWhite,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.ExtraBold,
                        lineHeight = 21.sp,
                    )
                    Spacer(Modifier.height(3.dp))
                    Text(
                        text = "Celebrate starlight week with royal dice challenges",
                        color = com.sundram.weplay.ui.theme.DreamLavender,
                        fontSize = 11.sp,
                        lineHeight = 14.sp,
                        maxLines = 2,
                    )
                }

                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(com.sundram.weplay.ui.theme.AuroraGradients.sunsetFantasy)
                        .border(0.8.dp, com.sundram.weplay.ui.theme.CloudWhite, RoundedCornerShape(14.dp))
                        .padding(horizontal = 14.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "★ Join Gala",
                        color = Color.White,
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Black,
                    )
                }
            }
        }

        // Carousel dots at bottom-center
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Box(
                modifier = Modifier
                    .width(if (activeSlide == 0) 16.dp else 6.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(if (activeSlide == 0) com.sundram.weplay.ui.theme.RoyalGold else Color.White.copy(alpha = 0.35f))
                    .clickable { activeSlide = 0 }
            )
            Box(
                modifier = Modifier
                    .width(if (activeSlide == 1) 16.dp else 6.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(if (activeSlide == 1) com.sundram.weplay.ui.theme.RoyalGold else Color.White.copy(alpha = 0.35f))
                    .clickable { activeSlide = 1 }
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  5. Section B — Popular Games (Deep Celestial Violet & Rose Cards)
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
                            Color(0xFF142252),
                            Color(0xFF2C1E62),
                        )
                    )
                )
                .border(1.dp, com.sundram.weplay.ui.theme.CrystalBlue.copy(alpha = 0.45f), RoundedCornerShape(20.dp))
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
                        .background(com.sundram.weplay.ui.theme.CrystalBlue.copy(alpha = 0.25f))
                        .border(0.8.dp, com.sundram.weplay.ui.theme.CrystalBlue, RoundedCornerShape(6.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp),
                ) {
                    Text("🔍 Mystery", color = com.sundram.weplay.ui.theme.CrystalBlue, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                }

                Column(modifier = Modifier.fillMaxWidth(0.62f)) {
                    Text(
                        text = "Who's the Spy",
                        color = com.sundram.weplay.ui.theme.CloudWhite,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 17.sp,
                    )
                    Text(
                        text = "Pass & play",
                        color = com.sundram.weplay.ui.theme.DreamLavender,
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
                            Color(0xFF45144A),
                            Color(0xFF22113F),
                        )
                    )
                )
                .border(1.dp, com.sundram.weplay.ui.theme.SunsetRose.copy(alpha = 0.45f), RoundedCornerShape(20.dp))
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
                        .background(com.sundram.weplay.ui.theme.SunsetRose.copy(alpha = 0.25f))
                        .border(0.8.dp, com.sundram.weplay.ui.theme.SunsetRose, RoundedCornerShape(6.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp),
                ) {
                    Text("🎨 New", color = com.sundram.weplay.ui.theme.SunsetRose, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                }

                Column(modifier = Modifier.fillMaxWidth(0.62f)) {
                    Text(
                        text = "Paint to Hide",
                        color = com.sundram.weplay.ui.theme.CloudWhite,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 17.sp,
                    )
                    Text(
                        text = "Graffiti party",
                        color = com.sundram.weplay.ui.theme.DreamLavender,
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
//  6. Section C — Explore More Games (Celestial Card Grid)
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
            .height(116.dp)
            .shadow(2.dp, RoundedCornerShape(18.dp))
            .clip(RoundedCornerShape(18.dp))
            .background(AuroraGradients.celestialCard)
            .border(1.dp, com.sundram.weplay.ui.theme.DreamLavender.copy(alpha = 0.30f), RoundedCornerShape(18.dp))
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
                        .background(accentColor.copy(alpha = 0.20f))
                        .border(0.6.dp, accentColor, RoundedCornerShape(4.dp))
                        .padding(horizontal = 5.dp, vertical = 2.dp),
                ) {
                    Text(badgeText, color = accentColor, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.height(4.dp))
            }

            Text(
                text = title,
                color = com.sundram.weplay.ui.theme.CloudWhite,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )

            Spacer(Modifier.height(2.dp))

            Text(
                text = genre,
                color = com.sundram.weplay.ui.theme.DreamLavender,
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
                    .border(1.dp, com.sundram.weplay.ui.theme.DreamLavender.copy(alpha = 0.35f), RoundedCornerShape(14.dp)),
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
            .shadow(2.dp, RoundedCornerShape(18.dp))
            .clip(RoundedCornerShape(18.dp))
            .background(AuroraGradients.celestialCard)
            .border(1.dp, com.sundram.weplay.ui.theme.DreamLavender.copy(alpha = 0.28f), RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
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
                    color = com.sundram.weplay.ui.theme.CloudWhite,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false),
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = if (user.gender == "F") "♀" else "♂",
                    color = if (user.gender == "F") com.sundram.weplay.ui.theme.SunsetRose else com.sundram.weplay.ui.theme.CrystalBlue,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(Modifier.width(6.dp))

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(com.sundram.weplay.ui.theme.AuroraGradients.royalGold)
                        .padding(horizontal = 4.dp, vertical = 1.dp),
                ) {
                    Text(
                        text = "Lv.${user.level}",
                        color = com.sundram.weplay.ui.theme.DeepInk,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.ExtraBold,
                    )
                }

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
                color = com.sundram.weplay.ui.theme.DreamLavender,
                fontSize = 11.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )

            Spacer(Modifier.height(4.dp))

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(if (user.isOnline) Color(0xFF133420) else com.sundram.weplay.ui.theme.DeepInk)
                    .border(0.6.dp, if (user.isOnline) Color(0xFF00E676) else com.sundram.weplay.ui.theme.DreamLavender.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
                    .padding(horizontal = 6.dp, vertical = 2.dp),
            ) {
                Text(
                    text = user.statusText,
                    color = if (user.isOnline) Color(0xFF69F0AE) else com.sundram.weplay.ui.theme.DreamLavender,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }

        Spacer(Modifier.width(8.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isFollowed) com.sundram.weplay.ui.theme.DeepInk else com.sundram.weplay.ui.theme.RoyalViolet)
                    .border(1.dp, if (isFollowed) com.sundram.weplay.ui.theme.DreamLavender.copy(alpha = 0.4f) else Color.Transparent, RoundedCornerShape(12.dp))
                    .clickable { isFollowed = !isFollowed }
                    .padding(horizontal = 11.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = if (isFollowed) "Following" else "Follow",
                    color = if (isFollowed) com.sundram.weplay.ui.theme.DreamLavender else Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                )
            }

            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(com.sundram.weplay.ui.theme.DeepInk)
                    .border(1.dp, com.sundram.weplay.ui.theme.DreamLavender.copy(alpha = 0.35f), CircleShape)
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
                // ── Section 5: Section A — Hero Featured Game & Festival ──
                item {
                    HeroFeaturedGameCard(
                        onPlayClick = { comingSoon("Petrol Pump Simulator") },
                        onFestivalClick = { nav.navigateSafe(Routes.EVENT_CENTER) },
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

                // ── Section 8: Premium 3D Gift Pack Treasure Chest ──
                item {
                    com.sundram.weplay.ui.theme.PremiumGiftPack(
                        countdownText = "47:59:12",
                        offerTitle = "Royal Starter Chest",
                        rewardAmount = "5,000",
                        priceText = "$0.99 ›",
                        onBuyClick = { showGiftPackSheet = true },
                    )
                }

                // ── Section 9: Community Players with Animated Frames & Real Profiles ──
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
