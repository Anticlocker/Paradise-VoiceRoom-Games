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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.sundram.weplay.R
import com.sundram.weplay.data.EventCategory
import com.sundram.weplay.data.EventItem
import com.sundram.weplay.data.EventRepository
import com.sundram.weplay.data.EventStatus
import com.sundram.weplay.ui.components.bounceClick
import com.sundram.weplay.ui.components.navigateSafe
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
import com.sundram.weplay.ui.theme.FestivalEventBackground

private val CategoryFilterList = listOf("All") + EventCategory.values().map { it.displayName }

@Composable
fun EventCenterScreen(
    nav: NavHostController,
    onBack: () -> Unit = { nav.popBackStack() },
) {
    val context = LocalContext.current
    val repository = remember { EventRepository(context) }
    var selectedCategory by remember { mutableStateOf("All") }

    val allEvents = remember { repository.getAllEvents() }
    val filteredEvents = remember(selectedCategory) {
        if (selectedCategory == "All") {
            allEvents
        } else {
            allEvents.filter { it.category.displayName == selectedCategory }
        }
    }

    FestivalEventBackground(
        modifier = Modifier.fillMaxSize(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding(),
        ) {
            // ── Top Header: Back Button + Centered Title ──
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .shadow(1.dp, CircleShape)
                        .clip(CircleShape)
                        .background(AuroraSurface)
                        .border(1.dp, AuroraBorder, CircleShape)
                        .bounceClick(onClick = onBack),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_back),
                        contentDescription = "Back",
                        tint = AuroraTextPrimary,
                        modifier = Modifier.size(18.dp),
                    )
                }

                Text(
                    text = "Event Center",
                    color = AuroraTextPrimary,
                    fontSize = 19.sp,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f),
                )

                // Placeholder for symmetry
                Spacer(Modifier.width(38.dp))
            }

            // ── Category Filter Pills Row ──
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                items(CategoryFilterList, key = { it }) { catName ->
                    val isSelected = catName == selectedCategory
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                if (isSelected) AuroraGradients.sunsetFantasy
                                else Brush.linearGradient(listOf(AuroraSurface, AuroraSurface))
                            )
                            .border(
                                1.dp,
                                if (isSelected) Color.Transparent else AuroraBorder,
                                RoundedCornerShape(16.dp)
                            )
                            .bounceClick { selectedCategory = catName }
                            .padding(horizontal = 14.dp, vertical = 7.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = catName,
                            color = if (isSelected) Color.White else AuroraTextSecondary,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        )
                    }
                }
            }

            Spacer(Modifier.height(4.dp))

            // ── Two-Column Grid of Illustrated Event Cards ──
            LazyColumn(
                contentPadding = PaddingValues(start = 14.dp, end = 14.dp, top = 8.dp, bottom = 32.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.fillMaxSize(),
            ) {
                if (filteredEvents.isEmpty()) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 48.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Text(
                                text = "No events in this category yet",
                                color = AuroraTextSecondary,
                                fontSize = 14.sp,
                            )
                        }
                    }
                } else {
                    items(filteredEvents.chunked(2)) { pair ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            Box(modifier = Modifier.weight(1f)) {
                                EventCard(
                                    event = pair[0],
                                    onClick = { nav.navigateSafe(Routes.eventDetailRoute(pair[0].id)) },
                                )
                            }
                            if (pair.size > 1) {
                                Box(modifier = Modifier.weight(1f)) {
                                    EventCard(
                                        event = pair[1],
                                        onClick = { nav.navigateSafe(Routes.eventDetailRoute(pair[1].id)) },
                                    )
                                }
                            } else {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Two-Column Illustrated Event Card inspired by reference layout
 * with 16dp rounded corners, cover thumbnail, category ribbon & countdown.
 */
@Composable
private fun EventCard(
    event: EventItem,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(18.dp))
            .clip(RoundedCornerShape(18.dp))
            .background(AuroraSurface)
            .border(1.dp, AuroraBorder, RoundedCornerShape(18.dp))
            .bounceClick(scaleDown = 0.97f, onClick = onClick)
            .padding(bottom = 10.dp),
    ) {
        // Upper Cover Banner with rounded top & badges
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1.35f)
                .clip(RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp))
                .background(AuroraBackground),
        ) {
            Image(
                painter = painterResource(event.coverRes),
                contentDescription = event.title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
            )

            // Category ribbon badge (Top-Left)
            if (!event.badgeText.isNullOrBlank()) {
                val badgeBg = when (event.status) {
                    EventStatus.LIVE -> AuroraCoral
                    EventStatus.UPCOMING -> AuroraCyan
                    EventStatus.ENDED -> AuroraTextSecondary
                }
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(badgeBg)
                        .padding(horizontal = 6.dp, vertical = 2.dp),
                ) {
                    Text(
                        text = event.badgeText,
                        color = Color.White,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.ExtraBold,
                    )
                }
            }

            // Live Countdown Pill (Bottom-Left overlay)
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(6.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xCC17132E))
                    .padding(horizontal = 6.dp, vertical = 2.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (event.status == EventStatus.LIVE) {
                        Box(
                            modifier = Modifier
                                .size(5.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF00E676)),
                        )
                        Spacer(Modifier.width(3.dp))
                    }
                    Text(
                        text = event.formatRemainingTime(),
                        color = Color.White,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        // Title and Subtitle below Card
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp),
        ) {
            Text(
                text = event.title,
                color = AuroraTextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )

            Spacer(Modifier.height(2.dp))

            Text(
                text = event.subtitle,
                color = AuroraTextSecondary,
                fontSize = 10.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}
