package com.sundram.weplay.ui.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.sundram.weplay.R
import com.sundram.weplay.data.CheckInDayVo
import com.sundram.weplay.data.CheckInStatus
import com.sundram.weplay.data.STARTER_ACHIEVEMENTS
import com.sundram.weplay.data.STARTER_DAILY_MISSIONS
import com.sundram.weplay.data.STARTER_NEW_PLAYER_MISSIONS
import com.sundram.weplay.data.STARTER_WEEKLY_CHALLENGES
import com.sundram.weplay.data.TaskCategory
import com.sundram.weplay.data.TaskItemVo
import com.sundram.weplay.data.TaskStatus
import com.sundram.weplay.data.TaskStore
import com.sundram.weplay.data.UserProfile
import com.sundram.weplay.data.getAvatarById
import com.sundram.weplay.ui.components.AnimatedAvatar
import com.sundram.weplay.ui.components.RoundIconButton
import com.sundram.weplay.ui.components.navigateSafe
import com.sundram.weplay.ui.theme.AuroraBorder
import com.sundram.weplay.ui.theme.AuroraElevated
import com.sundram.weplay.ui.theme.AuroraSurface
import com.sundram.weplay.ui.theme.CelestialNavy
import com.sundram.weplay.ui.theme.CloudWhite
import com.sundram.weplay.ui.theme.CrystalBlue
import com.sundram.weplay.ui.theme.DreamLavender
import com.sundram.weplay.ui.theme.MysticPurple
import com.sundram.weplay.ui.theme.RoyalGold

private val VyboraBg = CelestialNavy
private val VyboraBorder = AuroraBorder
private val VyboraGold = RoyalGold
private val VyboraLavender = AuroraElevated
private val VyboraPurple = MysticPurple
private val VyboraSurface = AuroraSurface
private val VyboraTextPrimary = CloudWhite
private val VyboraTextSecondary = DreamLavender

@Composable
fun TaskCenterScreen(
    nav: NavHostController,
    profile: UserProfile,
    onSaveProfile: (UserProfile) -> Unit,
) {
    val context = LocalContext.current
    val taskStore = remember(profile.id) { TaskStore(context, profile.id) }

    var checkInDays by remember { mutableStateOf(taskStore.getCheckInDays()) }
    var streakDays by remember { mutableStateOf(taskStore.getStreakDays()) }
    var isTodayClaimed by remember { mutableStateOf(taskStore.isTodayCheckedIn()) }

    var selectedCategory by remember { mutableStateOf(TaskCategory.DAILY) }
    var rewardCelebrationText by remember { mutableStateOf<String?>(null) }

    // Re-fetch missions
    val dailyList = remember(rewardCelebrationText, selectedCategory) { taskStore.getMissions(STARTER_DAILY_MISSIONS) }
    val weeklyList = remember(rewardCelebrationText, selectedCategory) { taskStore.getMissions(STARTER_WEEKLY_CHALLENGES) }
    val newPlayerList = remember(rewardCelebrationText, selectedCategory) { taskStore.getMissions(STARTER_NEW_PLAYER_MISSIONS) }
    val achievementList = remember(rewardCelebrationText, selectedCategory) { taskStore.getMissions(STARTER_ACHIEVEMENTS) }

    val activeList = when (selectedCategory) {
        TaskCategory.DAILY -> dailyList
        TaskCategory.WEEKLY -> weeklyList
        TaskCategory.NEW_PLAYER -> newPlayerList
        TaskCategory.ACHIEVEMENT, TaskCategory.COMMUNITY -> achievementList
    }

    // Hardware back navigation
    BackHandler {
        nav.popBackStack()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(VyboraBg)
            .statusBarsPadding()
            .navigationBarsPadding(),
    ) {
        // ── Top Header Bar ──
        TaskCenterHeader(onBack = { nav.popBackStack() })

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            // ── 1. User Level & XP Progression Banner ──
            item {
                UserProfileProgressCard(
                    profile = profile,
                    streakDays = streakDays,
                    dailyTasksCompleted = dailyList.count { it.status == TaskStatus.CLAIMED },
                    dailyTasksTotal = dailyList.size,
                )
            }

            // ── 2. 7-Day Daily Check-In Section ──
            item {
                DailyCheckInSection(
                    checkInDays = checkInDays,
                    isTodayClaimed = isTodayClaimed,
                    onClaimClick = {
                        val success = taskStore.claimDailyCheckIn { coins, xp ->
                            val updatedProfile = profile.copy(coins = profile.coins + coins)
                            onSaveProfile(updatedProfile)
                            rewardCelebrationText = "Claimed Daily Check-In: +$coins Coins and +$xp XP"
                            checkInDays = taskStore.getCheckInDays()
                            streakDays = taskStore.getStreakDays()
                            isTodayClaimed = taskStore.isTodayCheckedIn()
                        }
                        if (!success) {
                            Toast.makeText(context, "Already checked in today.", Toast.LENGTH_SHORT).show()
                        }
                    },
                )
            }

            // ── 3. Category Filter Tabs ──
            item {
                TaskCategoryTabs(
                    selectedCategory = selectedCategory,
                    onSelect = { selectedCategory = it },
                )
            }

            // ── 4. Task Mission Cards ──
            items(activeList, key = { it.definition.id }) { itemVo ->
                TaskMissionCard(
                    item = itemVo,
                    onGoClick = {
                        nav.navigateSafe(itemVo.definition.actionRoute)
                    },
                    onClaimClick = {
                        val success = taskStore.claimTaskReward(itemVo.definition.id) { coins, xp ->
                            val updatedProfile = profile.copy(coins = profile.coins + coins)
                            onSaveProfile(updatedProfile)
                            rewardCelebrationText = "${itemVo.definition.title} completed — +$coins Coins and +$xp XP"
                        }
                        if (!success) {
                            Toast.makeText(context, "Reward already claimed.", Toast.LENGTH_SHORT).show()
                        }
                    },
                )
            }

            item {
                Spacer(Modifier.height(24.dp))
            }
        }
    }

    // ── Reward Celebration Overlay / Dialog ──
    rewardCelebrationText?.let { celebrationMsg ->
        AlertDialog(
            onDismissRequest = { rewardCelebrationText = null },
            containerColor = VyboraSurface,
            title = {
                Text(
                    text = "Reward Claimed",
                    color = VyboraTextPrimary,
                    fontWeight = FontWeight.Bold,
                )
            },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(
                        painter = painterResource(R.drawable.vb_gift_pack),
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                    )
                    Spacer(Modifier.height(10.dp))
                    Text(
                        text = celebrationMsg,
                        color = VyboraPurple,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center,
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = "Your level and coin balance have been updated.",
                        color = VyboraTextSecondary,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { rewardCelebrationText = null }) {
                    Text("Done", color = VyboraPurple, fontWeight = FontWeight.Bold)
                }
            },
        )
    }
}

@Composable
private fun TaskCenterHeader(onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RoundIconButton(
            painter = painterResource(R.drawable.ic_back),
            onClick = onBack,
            contentDescription = "Back",
            size = 36.dp,
            iconSize = 20.dp,
            tint = VyboraTextPrimary,
        )
        Spacer(Modifier.width(10.dp))
        Column {
            Text(
                text = "Your Missions",
                color = VyboraTextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
            )
            Text(
                text = "Play, connect, and level up",
                color = VyboraTextSecondary,
                fontSize = 12.sp,
            )
        }
    }
}

@Composable
private fun UserProfileProgressCard(
    profile: UserProfile,
    streakDays: Int,
    dailyTasksCompleted: Int,
    dailyTasksTotal: Int,
) {
    val avatar = getAvatarById(profile.avatarId)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(3.dp, RoundedCornerShape(18.dp))
            .clip(RoundedCornerShape(18.dp))
            .background(
                Brush.horizontalGradient(
                    listOf(Color(0xFF6A1B9A), Color(0xFF4A148C))
                )
            )
            .padding(16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // User Animated Character Avatar
            AnimatedAvatar(
                avatar = avatar,
                size = 56.dp,
                animate = true,
            )

            Spacer(Modifier.width(12.dp))

            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = profile.name,
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(VyboraGold)
                            .padding(horizontal = 6.dp, vertical = 2.dp),
                    ) {
                        Text(
                            text = "Lv.${profile.level}",
                            color = Color.Black,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.ExtraBold,
                        )
                    }
                }

                Spacer(Modifier.height(4.dp))

                // XP Progress Bar
                LinearProgressIndicator(
                    progress = { 0.65f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = Color(0xFF00E5FF),
                    trackColor = Color.White.copy(alpha = 0.25f),
                )

                Spacer(Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = "XP: 650 / 1,000",
                        color = Color(0xFFE1BEE7),
                        fontSize = 10.sp,
                    )
                    Text(
                        text = "Daily: $dailyTasksCompleted/$dailyTasksTotal done",
                        color = Color(0xFFE1BEE7),
                        fontSize = 10.sp,
                    )
                }
            }

            Spacer(Modifier.width(12.dp))

            // Streak Pill
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    painter = painterResource(id = com.sundram.weplay.R.drawable.ic_star),
                    contentDescription = null,
                    tint = Color(0xFFFFA000),
                    modifier = Modifier.size(22.dp),
                )
                Text(
                    text = "${streakDays}d Streak",
                    color = VyboraGold,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                )
            }
        }
    }
}

@Composable
private fun DailyCheckInSection(
    checkInDays: List<CheckInDayVo>,
    isTodayClaimed: Boolean,
    onClaimClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(1.dp, RoundedCornerShape(18.dp))
            .clip(RoundedCornerShape(18.dp))
            .background(VyboraSurface)
            .border(1.dp, VyboraBorder, RoundedCornerShape(18.dp))
            .padding(14.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column {
                Text(
                    text = "7-Day Daily Check-In",
                    color = VyboraTextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = "Check in daily to claim coins & Royal Day 7 chest",
                    color = VyboraTextSecondary,
                    fontSize = 11.sp,
                )
            }

            // Big Claim Button
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (!isTodayClaimed) VyboraPurple else Color(0xFF2D2152))
                    .clickable(enabled = !isTodayClaimed, onClick = onClaimClick)
                    .padding(horizontal = 12.dp, vertical = 7.dp),
            ) {
                Text(
                    text = if (!isTodayClaimed) "Check In" else "Claimed",
                    color = if (!isTodayClaimed) Color.White else DreamLavender.copy(alpha = 0.72f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        // 7 Days Horizontal Deck
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(checkInDays) { dayVo ->
                DailyCheckInDayCard(dayVo = dayVo)
            }
        }
    }
}

@Composable
private fun DailyCheckInDayCard(dayVo: CheckInDayVo) {
    val isClaimed = dayVo.status == CheckInStatus.CLAIMED
    val isAvailable = dayVo.status == CheckInStatus.AVAILABLE

    Column(
        modifier = Modifier
            .width(52.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(
                when {
                    isClaimed -> Color(0xFF2A1D54)
                    isAvailable -> Color(0xFF3E2A12)
                    else -> VyboraBg
                }
            )
            .border(
                1.dp,
                when {
                    isAvailable -> VyboraGold
                    isClaimed -> VyboraPurple.copy(alpha = 0.5f)
                    else -> VyboraBorder
                },
                RoundedCornerShape(12.dp)
            )
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Day ${dayVo.dayNumber}",
            color = if (isAvailable) RoyalGold else VyboraTextSecondary,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
        )

        Spacer(Modifier.height(4.dp))

        Icon(
            painter = painterResource(
                id = when {
                    dayVo.isRoyalBonus -> com.sundram.weplay.R.drawable.ic_crown
                    isClaimed -> com.sundram.weplay.R.drawable.ic_check
                    else -> com.sundram.weplay.R.drawable.ic_coin
                },
            ),
            contentDescription = null,
            tint = when {
                dayVo.isRoyalBonus -> Color(0xFFFFD54F)
                isClaimed -> Color(0xFF66BB6A)
                else -> Color(0xFFFFB300)
            },
            modifier = Modifier.size(if (dayVo.isRoyalBonus) 16.dp else 15.dp),
        )

        Spacer(Modifier.height(4.dp))

        Text(
            text = "+${dayVo.coinReward}",
            color = if (isAvailable) RoyalGold else VyboraTextPrimary,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun TaskCategoryTabs(
    selectedCategory: TaskCategory,
    onSelect: (TaskCategory) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(VyboraSurface)
            .border(1.dp, VyboraBorder, RoundedCornerShape(12.dp))
            .padding(3.dp),
    ) {
        listOf(
            Pair(TaskCategory.DAILY, "Daily"),
            Pair(TaskCategory.WEEKLY, "Weekly"),
            Pair(TaskCategory.NEW_PLAYER, "Newbie"),
            Pair(TaskCategory.ACHIEVEMENT, "Badges"),
        ).forEach { (cat, label) ->
            val isSelected = selectedCategory == cat
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isSelected) VyboraPurple else Color.Transparent)
                    .clickable { onSelect(cat) }
                    .padding(vertical = 7.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = label,
                    color = if (isSelected) Color.White else VyboraTextSecondary,
                    fontSize = 12.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                )
            }
        }
    }
}

@Composable
private fun TaskMissionCard(
    item: TaskItemVo,
    onGoClick: () -> Unit,
    onClaimClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(1.dp, RoundedCornerShape(14.dp))
            .clip(RoundedCornerShape(14.dp))
            .background(VyboraSurface)
            .border(1.dp, VyboraBorder, RoundedCornerShape(14.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Emoji Icon Container
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(VyboraBg),
            contentAlignment = Alignment.Center,
        ) {
            Text(item.definition.iconEmoji, fontSize = 20.sp)
        }

        Spacer(Modifier.width(10.dp))

        Column(Modifier.weight(1f)) {
            Text(
                text = item.definition.title,
                color = VyboraTextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = item.definition.description,
                color = VyboraTextSecondary,
                fontSize = 11.sp,
            )
            Spacer(Modifier.height(6.dp))

            // Progress bar and rewards
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                LinearProgressIndicator(
                    progress = { item.progressFraction },
                    modifier = Modifier
                        .width(90.dp)
                        .height(5.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = VyboraPurple,
                    trackColor = VyboraBorder,
                )
                Text(
                    text = "${item.currentCount}/${item.definition.targetCount}",
                    color = VyboraTextSecondary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                )
                Row {
                    Text(
                        text = "+${item.definition.xpReward} XP",
                        color = Color(0xFF00B0FF),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = "+${item.definition.coinReward} Coins",
                        color = Color(0xFFFFA000),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }

        Spacer(Modifier.width(10.dp))

        // Action Button
        when (item.status) {
            TaskStatus.IN_PROGRESS -> {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(VyboraLavender)
                        .clickable(onClick = onGoClick)
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = item.definition.actionLabel,
                        color = CrystalBlue,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
            TaskStatus.CLAIMABLE -> {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            Brush.horizontalGradient(listOf(Color(0xFF00E676), Color(0xFF00C853)))
                        )
                        .clickable(onClick = onClaimClick)
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "Claim",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
            TaskStatus.CLAIMED -> {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(VyboraBg)
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "Claimed",
                        color = VyboraTextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                    )
                }
            }
        }
    }
}
