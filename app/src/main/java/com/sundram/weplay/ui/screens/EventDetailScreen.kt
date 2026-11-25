package com.sundram.weplay.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.material3.Icon
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.sundram.weplay.R
import com.sundram.weplay.data.EventItem
import com.sundram.weplay.data.EventRepository
import com.sundram.weplay.data.EventReward
import com.sundram.weplay.data.EventStatus
import com.sundram.weplay.data.EventTask
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
import com.sundram.weplay.ui.theme.AuroraSurface
import com.sundram.weplay.ui.theme.AuroraTextPrimary
import com.sundram.weplay.ui.theme.AuroraTextSecondary
import com.sundram.weplay.ui.theme.FestivalEventBackground
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun EventDetailScreen(
    eventId: String,
    nav: NavHostController,
    onBack: () -> Unit = { nav.popBackStack() },
) {
    val context = LocalContext.current
    val repository = remember { EventRepository(context) }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var eventState by remember(eventId) { mutableStateOf(repository.getEventById(eventId)) }
    var currentTime by remember { mutableLongStateOf(System.currentTimeMillis()) }

    // Live countdown ticker
    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            currentTime = System.currentTimeMillis()
        }
    }

    val event = eventState

    FestivalEventBackground(
        modifier = Modifier.fillMaxSize(),
    ) {
        if (event == null) {
            // Error / Not found state
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Text("Event not found", color = AuroraTextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(AuroraOrchid)
                        .clickable(onClick = onBack)
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                ) {
                    Text("Return to Events", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 36.dp),
            ) {
                // ── Hero Banner Header with Cover ──
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(260.dp),
                    ) {
                        Image(
                            painter = painterResource(event.coverRes),
                            contentDescription = event.title,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop,
                        )

                        // Subtle gradient overlay for readability
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        listOf(
                                            Color(0x99000000),
                                            Color.Transparent,
                                            Color(0xDD17132E),
                                        )
                                    )
                                ),
                        )

                        // Top Action Bar: Back + Share
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .statusBarsPadding()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .shadow(2.dp, CircleShape)
                                    .clip(CircleShape)
                                    .background(Color(0x66000000))
                                    .border(1.dp, Color.White.copy(alpha = 0.3f), CircleShape)
                                    .bounceClick(onClick = onBack),
                                contentAlignment = Alignment.Center,
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_back),
                                    contentDescription = "Back",
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp),
                                )
                            }

                            Spacer(Modifier.weight(1f))

                            val clipboardManager = LocalClipboardManager.current
                            // Share Action Button
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .shadow(2.dp, CircleShape)
                                    .clip(CircleShape)
                                    .background(Color(0x66000000))
                                    .border(1.dp, Color.White.copy(alpha = 0.3f), CircleShape)
                                    .bounceClick {
                                        clipboardManager.setText(AnnotatedString("${event.title} — ${event.subtitle}"))
                                        scope.launch {
                                            snackbarHostState.showSnackbar("Event invite copied to clipboard")
                                        }
                                    },
                                contentAlignment = Alignment.Center,
                            ) {
                                Icon(
                                    painter = painterResource(id = com.sundram.weplay.R.drawable.ic_send),
                                    contentDescription = "Share event",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp),
                                )
                            }
                        }

                        // Bottom Title & Countdown over Hero
                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                val statusBg = when (event.status) {
                                    EventStatus.LIVE -> Color(0xFF00E676)
                                    EventStatus.UPCOMING -> AuroraCyan
                                    EventStatus.ENDED -> Color.Gray
                                }
                                Box(
                                    modifier = Modifier
                                        .size(7.dp)
                                        .clip(CircleShape)
                                        .background(statusBg),
                                )
                                Spacer(Modifier.width(6.dp))
                                Text(
                                    text = event.formatRemainingTime(currentTime),
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                )

                                if (event.badgeText != null) {
                                    Spacer(Modifier.width(8.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(AuroraCoral)
                                            .padding(horizontal = 6.dp, vertical = 2.dp),
                                    ) {
                                        Text(event.badgeText, color = Color.White, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                            Spacer(Modifier.height(4.dp))

                            Text(
                                text = event.title,
                                color = Color.White,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.ExtraBold,
                            )

                            Text(
                                text = "${event.startDate} – ${event.endDate}",
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 11.sp,
                            )
                        }
                    }
                }

                // ── Eligibility & Overview Card ──
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                            .shadow(2.dp, RoundedCornerShape(18.dp))
                            .clip(RoundedCornerShape(18.dp))
                            .background(AuroraSurface)
                            .border(1.dp, AuroraBorder, RoundedCornerShape(18.dp))
                            .padding(14.dp),
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Eligibility:", color = AuroraTextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            Spacer(Modifier.width(6.dp))
                            Text(event.eligibility, color = AuroraOrchid, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        Spacer(Modifier.height(8.dp))

                        Text(
                            text = event.description,
                            color = AuroraTextPrimary,
                            fontSize = 13.sp,
                            lineHeight = 18.sp,
                        )

                        if (event.leaderboardId != null) {
                            Spacer(Modifier.height(10.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(AuroraBackground)
                                    .border(1.dp, AuroraBorder, RoundedCornerShape(12.dp))
                                    .clickable { nav.navigateSafe(Routes.RANKING) }
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        painter = painterResource(id = com.sundram.weplay.R.drawable.ic_crown),
                                        contentDescription = null,
                                        tint = AuroraOrchid,
                                        modifier = Modifier.size(15.dp),
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    Text("View Live Event Leaderboard", color = AuroraOrchid, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    Spacer(Modifier.weight(1f))
                                    Icon(
                                        painter = painterResource(id = com.sundram.weplay.R.drawable.ic_chevron_right),
                                        contentDescription = null,
                                        tint = AuroraOrchid,
                                        modifier = Modifier.size(16.dp),
                                    )
                                }
                            }
                        }
                    }
                }

                // ── Rewards Catalog Section ──
                if (event.rewards.isNotEmpty()) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 6.dp),
                        ) {
                            Text("Reward Catalog", color = AuroraTextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                            ) {
                                event.rewards.forEach { reward ->
                                    Column(
                                        modifier = Modifier
                                            .weight(1f)
                                            .shadow(1.dp, RoundedCornerShape(16.dp))
                                            .clip(RoundedCornerShape(16.dp))
                                            .background(AuroraSurface)
                                            .border(1.dp, if (reward.isRare) AuroraGold else AuroraBorder, RoundedCornerShape(16.dp))
                                            .padding(10.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                    ) {
                                        Text(reward.iconEmoji, fontSize = 24.sp)
                                        Spacer(Modifier.height(4.dp))
                                        Text(
                                            text = reward.title,
                                            color = AuroraTextPrimary,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            maxLines = 1,
                                        )
                                        Text(
                                            text = reward.valueText,
                                            color = if (reward.isRare) AuroraGold else AuroraTextSecondary,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Medium,
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // ── Tasks & Milestones Section ──
                if (event.tasks.isNotEmpty()) {
                    item {
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = "Event Milestones & Tasks",
                            color = AuroraTextPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 16.dp),
                        )
                    }

                    items(event.tasks, key = { it.id }) { task ->
                        TaskRowCard(
                            task = task,
                            onClaim = {
                                val success = repository.claimTaskReward(event.id, task.id)
                                if (success) {
                                    eventState = repository.getEventById(event.id)
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Claimed +${task.rewardCoins} coins & +${task.rewardXp} XP!")
                                    }
                                } else {
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Reward already claimed!")
                                    }
                                }
                            },
                        )
                    }
                }

                // ── Official Rules Card ──
                if (event.rules.isNotEmpty()) {
                    item {
                        Spacer(Modifier.height(10.dp))
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                                .shadow(1.dp, RoundedCornerShape(16.dp))
                                .clip(RoundedCornerShape(16.dp))
                                .background(AuroraSurface)
                                .border(1.dp, AuroraBorder, RoundedCornerShape(16.dp))
                                .padding(14.dp),
                        ) {
                            Text("Official Guidelines & Rules", color = AuroraTextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.height(8.dp))
                            event.rules.forEachIndexed { idx, rule ->
                                Text(
                                    text = "${idx + 1}. $rule",
                                    color = AuroraTextSecondary,
                                    fontSize = 11.sp,
                                    lineHeight = 16.sp,
                                    modifier = Modifier.padding(vertical = 2.dp),
                                )
                            }
                        }
                    }
                }
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp),
        )
    }
}

@Composable
private fun TaskRowCard(
    task: EventTask,
    onClaim: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .shadow(1.dp, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .background(AuroraSurface)
            .border(1.dp, AuroraBorder, RoundedCornerShape(16.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(task.title, color = AuroraTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Text(task.description, color = AuroraTextSecondary, fontSize = 11.sp)

            Spacer(Modifier.height(6.dp))

            // Progress bar
            val progressFraction = (task.currentCount.toFloat() / task.targetCount.toFloat()).coerceIn(0f, 1f)
            Row(verticalAlignment = Alignment.CenterVertically) {
                LinearProgressIndicator(
                    progress = { progressFraction },
                    modifier = Modifier
                        .weight(1f)
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = AuroraOrchid,
                    trackColor = AuroraLavender.copy(alpha = 0.5f),
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "${task.currentCount}/${task.targetCount}",
                    color = AuroraTextSecondary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }

        Spacer(Modifier.width(12.dp))

        // Claim or In-Progress Button
        if (task.isClaimed) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFE8F5E9))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
            ) {
                Text("Claimed", color = Color(0xFF2E7D32), fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        } else if (task.isCompleted) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(AuroraGradients.sunsetFantasy)
                    .bounceClick(onClick = onClaim)
                    .padding(horizontal = 14.dp, vertical = 7.dp),
            ) {
                Text("Claim", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        } else {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(AuroraBackground)
                    .border(1.dp, AuroraBorder, RoundedCornerShape(12.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp),
            ) {
                Text("+${task.rewardCoins} Coins", color = AuroraOrchid, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
