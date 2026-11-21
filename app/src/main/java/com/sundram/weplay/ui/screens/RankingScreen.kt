package com.sundram.weplay.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.sundram.weplay.R
import com.sundram.weplay.data.FrameDefinition
import com.sundram.weplay.data.FrameRepository
import com.sundram.weplay.data.FrameState
import com.sundram.weplay.data.FrameTier
import com.sundram.weplay.data.LeaderboardCategory
import com.sundram.weplay.data.RankingEntry
import com.sundram.weplay.data.LeaderboardPeriod
import com.sundram.weplay.data.RankingRuleInfo
import com.sundram.weplay.data.UserProfile
import com.sundram.weplay.ui.components.AvatarWithFrame
import com.sundram.weplay.ui.nav.Routes

private val PageCanvas = Color(0xFFFAF9FF)
private val SurfaceWhite = Color(0xFFFFFFFF)
private val InkDark = Color(0xFF26233C)
private val InkMuted = Color(0xFF88849E)
private val BorderSoft = Color(0xFFECE8F5)
private val PurpleBrand = Color(0xFF7B52E8)
private val CyanBrandColor = Color(0xFF31CAD9)
private val GoldAccent = Color(0xFFF5BD62)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RankingScreen(
    nav: NavHostController,
    profile: UserProfile? = null,
    onSaveProfile: ((UserProfile) -> Unit)? = null,
) {
    val context = LocalContext.current
    val repository = remember { FrameRepository(context) }

    var selectedCategory by remember { mutableStateOf(LeaderboardCategory.POPULARITY) }
    var selectedPeriod by remember { mutableStateOf(LeaderboardPeriod.TODAY) }

    val ownedFrames by repository.ownedFrameIds.collectAsState()
    val equippedFrameId by repository.equippedFrameId.collectAsState()

    var showRulesDialog by remember { mutableStateOf(false) }
    var showShopSheet by remember { mutableStateOf(false) }
    var previewFrameId by remember { mutableStateOf<String?>(null) }
    var shopFeedbackMessage by remember { mutableStateOf<String?>(null) }

    // Fetch leaderboard feed
    val (rankingEntries, userRankSummary) = remember(selectedCategory, selectedPeriod, profile) {
        repository.getLeaderboard(selectedCategory, selectedPeriod, profile)
    }

    val top3 = remember(rankingEntries) {
        val first = rankingEntries.firstOrNull { it.rank == 1 }
        val second = rankingEntries.firstOrNull { it.rank == 2 }
        val third = rankingEntries.firstOrNull { it.rank == 3 }
        Triple(first, second, third)
    }

    val listItems = remember(rankingEntries) {
        rankingEntries.filter { it.rank >= 4 }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PageCanvas)
            .statusBarsPadding(),
    ) {
        // Top App Bar
        TopLeaderboardBar(
            onBack = { nav.popBackStack() },
            onOpenRules = { showRulesDialog = true },
            onOpenShop = {
                previewFrameId = equippedFrameId ?: "frame_common_aurora"
                showShopSheet = true
            },
        )

        // Category Tabs
        CategoryTabsRow(
            selectedCategory = selectedCategory,
            onCategorySelected = { cat ->
                selectedCategory = cat
                if (!cat.supportedPeriods.contains(selectedPeriod)) {
                    selectedPeriod = cat.supportedPeriods.first()
                }
            },
        )

        // Period Filter Chips
        PeriodChipsRow(
            supportedPeriods = selectedCategory.supportedPeriods,
            selectedPeriod = selectedPeriod,
            onPeriodSelected = { selectedPeriod = it },
        )

        Spacer(Modifier.height(8.dp))

        // Main Ranking Content
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(bottom = 16.dp),
        ) {
            // Category Ornate Banner
            item {
                CategoryBannerCard(category = selectedCategory)
            }

            // Top-Three Podium
            item {
                PodiumSection(
                    first = top3.first,
                    second = top3.second,
                    third = top3.third,
                    category = selectedCategory,
                    onUserClick = { entry ->
                        nav.navigate(Routes.userDetailRoute(entry.id))
                    },
                )
                Spacer(Modifier.height(16.dp))
            }

            // Rank 4+ Section Header
            if (listItems.isNotEmpty()) {
                item {
                    Text(
                        text = "Honorable Runners",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = InkMuted,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp),
                    )
                }
            }

            // Rank 4+ List Items
            items(listItems, key = { it.id }) { entry ->
                RankRowItem(
                    entry = entry,
                    category = selectedCategory,
                    onClick = {
                        nav.navigate(Routes.userDetailRoute(entry.id))
                    },
                )
            }
        }

        // Sticky Bottom Current User Summary Bar
        StickyCurrentUserBar(
            summary = userRankSummary,
            profile = profile,
            equippedFrameId = equippedFrameId,
            onCtaClick = {
                if (userRankSummary.ctaRoute.isNotBlank()) {
                    nav.navigate(userRankSummary.ctaRoute)
                }
            },
        )
    }

    // Rules & Scoring Dialog
    if (showRulesDialog) {
        val rules = remember(selectedCategory) { repository.getRankingRules(selectedCategory) }
        RankingRulesDialog(
            rules = rules,
            onDismiss = { showRulesDialog = false },
        )
    }

    // Frame Cosmetics & Shop Sheet
    if (showShopSheet) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(
            onDismissRequest = { showShopSheet = false },
            sheetState = sheetState,
            containerColor = SurfaceWhite,
        ) {
            FrameShopSheetContent(
                profile = profile,
                repository = repository,
                catalog = repository.catalog,
                ownedFrames = ownedFrames,
                equippedFrameId = equippedFrameId,
                previewFrameId = previewFrameId,
                onSelectPreview = { previewFrameId = it },
                onEquip = { frameId ->
                    val success = repository.equipFrame(
                        frameId = frameId,
                        userLevel = profile?.level ?: 1,
                        profile = profile,
                        onProfileUpdate = onSaveProfile,
                    )
                    if (success) {
                        shopFeedbackMessage = "Equipped frame successfully!"
                    }
                },
                onUnequip = {
                    repository.unequipFrame(profile, onSaveProfile)
                    shopFeedbackMessage = "Unequipped frame."
                },
                onPurchase = { frameId ->
                    val userCoins = profile?.coins ?: 500L
                    val res = repository.purchaseFrame(
                        frameId = frameId,
                        currentCoins = userCoins,
                        userLevel = profile?.level ?: 1,
                        profile = profile,
                        onCoinsDeducted = { newBal ->
                            // Coins deducted handled in repository
                        },
                        onProfileUpdate = onSaveProfile,
                    )
                    shopFeedbackMessage = if (res.isSuccess) {
                        "Unlocked and equipped ${res.getOrNull()?.name}!"
                    } else {
                        res.exceptionOrNull()?.message ?: "Purchase failed"
                    }
                },
                feedbackMessage = shopFeedbackMessage,
            )
        }
    }
}

// ---------------------------------------------------------------------------
// SUB-COMPONENTS
// ---------------------------------------------------------------------------

@Composable
private fun TopLeaderboardBar(
    onBack: () -> Unit,
    onOpenRules: () -> Unit,
    onOpenShop: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Back Button
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(SurfaceWhite)
                .border(1.dp, BorderSoft, CircleShape)
                .clickable(onClick = onBack),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_back),
                contentDescription = "Back",
                tint = InkDark,
                modifier = Modifier.size(20.dp),
            )
        }

        Spacer(Modifier.width(12.dp))

        // Screen Title
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Leaderboard",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = InkDark,
            )
            Text(
                text = "Top Champions & Star Crests",
                fontSize = 12.sp,
                color = InkMuted,
            )
        }

        // Frame Wardrobe / Shop Shortcut
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color(0xFFF4EFFE))
                .border(1.dp, Color(0xFFDDD2F9), CircleShape)
                .clickable(onClick = onOpenShop),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(R.drawable.lb_legendary_crest),
                contentDescription = "Frame Wardrobe",
                tint = Color.Unspecified,
                modifier = Modifier.size(24.dp),
            )
        }

        Spacer(Modifier.width(8.dp))

        // Help / Rules Dialog Button
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(SurfaceWhite)
                .border(1.dp, BorderSoft, CircleShape)
                .clickable(onClick = onOpenRules),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "?",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = PurpleBrand,
            )
        }
    }
}

@Composable
private fun CategoryTabsRow(
    selectedCategory: LeaderboardCategory,
    onCategorySelected: (LeaderboardCategory) -> Unit,
) {
    val scrollState = rememberScrollState()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(scrollState)
            .padding(horizontal = 14.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        LeaderboardCategory.values().forEach { category ->
            val isSelected = category == selectedCategory
            val bgBrush = if (isSelected) {
                Brush.horizontalGradient(
                    listOf(
                        Color(category.primaryColor),
                        PurpleBrand,
                    ),
                )
            } else {
                Brush.linearGradient(listOf(SurfaceWhite, SurfaceWhite))
            }

            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(bgBrush)
                    .border(
                        1.dp,
                        if (isSelected) Color.Transparent else BorderSoft,
                        RoundedCornerShape(20.dp),
                    )
                    .clickable { onCategorySelected(category) }
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = category.iconGlyph,
                    fontSize = 14.sp,
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = category.title,
                    fontSize = 14.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = if (isSelected) Color.White else InkDark,
                )
            }
        }
    }
}

@Composable
private fun PeriodChipsRow(
    supportedPeriods: List<LeaderboardPeriod>,
    selectedPeriod: LeaderboardPeriod,
    onPeriodSelected: (LeaderboardPeriod) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        supportedPeriods.forEach { period ->
            val isSelected = period == selectedPeriod
            val animBg by animateColorAsState(
                targetValue = if (isSelected) Color(0xFFEEE8FC) else SurfaceWhite,
                animationSpec = tween(200),
                label = "PeriodBg",
            )
            val animTextColor by animateColorAsState(
                targetValue = if (isSelected) PurpleBrand else InkMuted,
                animationSpec = tween(200),
                label = "PeriodText",
            )

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(animBg)
                    .border(
                        1.dp,
                        if (isSelected) PurpleBrand.copy(alpha = 0.4f) else BorderSoft,
                        RoundedCornerShape(16.dp),
                    )
                    .clickable { onPeriodSelected(period) }
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = period.displayName,
                    fontSize = 12.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    color = animTextColor,
                )
            }
        }
    }
}

@Composable
private fun CategoryBannerCard(category: LeaderboardCategory) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .height(72.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(
                Brush.horizontalGradient(
                    listOf(
                        Color(category.bannerBgColor),
                        Color(0xFFFAF9FF),
                    ),
                ),
            )
            .border(1.dp, Color(category.primaryColor).copy(alpha = 0.3f), RoundedCornerShape(18.dp))
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.CenterStart,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column {
                Text(
                    text = "${category.title} Ranking",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = InkDark,
                )
                Text(
                    text = category.subtitle,
                    fontSize = 12.sp,
                    color = InkMuted,
                )
            }
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.8f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = painterResource(category.bannerDrawableRes),
                    contentDescription = null,
                    tint = Color.Unspecified,
                    modifier = Modifier.size(36.dp),
                )
            }
        }
    }
}

@Composable
private fun PodiumSection(
    first: RankingEntry?,
    second: RankingEntry?,
    third: RankingEntry?,
    category: LeaderboardCategory,
    onUserClick: (RankingEntry) -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.Bottom,
        ) {
            // Rank 2 (Left)
            PodiumPedestal(
                entry = second,
                rank = 2,
                pedestalHeight = 135.dp,
                avatarSize = 68.dp,
                scoreUnit = category.scoreUnit,
                modifier = Modifier.weight(1f),
                onClick = onUserClick,
            )

            // Rank 1 (Center Prominent)
            PodiumPedestal(
                entry = first,
                rank = 1,
                pedestalHeight = 165.dp,
                avatarSize = 88.dp,
                scoreUnit = category.scoreUnit,
                isChampion = true,
                modifier = Modifier.weight(1.15f),
                onClick = onUserClick,
            )

            // Rank 3 (Right)
            PodiumPedestal(
                entry = third,
                rank = 3,
                pedestalHeight = 120.dp,
                avatarSize = 64.dp,
                scoreUnit = category.scoreUnit,
                modifier = Modifier.weight(1f),
                onClick = onUserClick,
            )
        }
    }
}

@Composable
private fun PodiumPedestal(
    entry: RankingEntry?,
    rank: Int,
    pedestalHeight: androidx.compose.ui.unit.Dp,
    avatarSize: androidx.compose.ui.unit.Dp,
    scoreUnit: String,
    modifier: Modifier = Modifier,
    isChampion: Boolean = false,
    onClick: (RankingEntry) -> Unit,
) {
    val tierColor = when (rank) {
        1 -> GoldAccent
        2 -> Color(0xFF8FB3D9)
        else -> Color(0xFFC99E75)
    }

    val pedestalGradient = when (rank) {
        1 -> listOf(Color(0xFFFFF7E3), Color(0xFFFFECC0))
        2 -> listOf(Color(0xFFF3F7FC), Color(0xFFE2ECFA))
        else -> listOf(Color(0xFFFAF3EC), Color(0xFFF1E4D6))
    }

    val frameTier = when (rank) {
        1 -> FrameTier.fromId(entry?.frameId) ?: FrameTier.CELESTIAL
        2 -> FrameTier.fromId(entry?.frameId) ?: FrameTier.LEGENDARY
        else -> FrameTier.fromId(entry?.frameId) ?: FrameTier.EPIC
    }

    Column(
        modifier = modifier
            .padding(horizontal = 4.dp)
            .clickable(enabled = entry != null) {
                entry?.let { onClick(it) }
            },
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (entry != null) {
            // Crown badge for top 3
            Box(
                modifier = Modifier
                    .size(if (isChampion) 28.dp else 22.dp)
                    .clip(CircleShape)
                    .background(tierColor),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = rank.toString(),
                    fontSize = if (isChampion) 14.sp else 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                )
            }

            Spacer(Modifier.height(2.dp))

            // Framed Avatar
            AvatarWithFrame(
                name = entry.name,
                photoPath = entry.avatarPath,
                size = avatarSize,
                frameTier = frameTier,
                animated = true,
                showCrown = isChampion,
            )

            Spacer(Modifier.height(4.dp))

            // User Name
            Text(
                text = entry.name,
                fontSize = if (isChampion) 14.sp else 12.sp,
                fontWeight = FontWeight.Bold,
                color = InkDark,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
            )

            // Score with Unit
            Text(
                text = "${entry.scoreFormatted} $scoreUnit",
                fontSize = if (isChampion) 12.sp else 10.sp,
                fontWeight = FontWeight.Medium,
                color = PurpleBrand,
                maxLines = 1,
            )
        } else {
            // Empty placeholder slot
            Box(
                modifier = Modifier
                    .size(avatarSize)
                    .clip(CircleShape)
                    .background(Color(0xFFEDE9F5)),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = rank.toString(),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = InkMuted,
                )
            }
            Text("Vacant", fontSize = 11.sp, color = InkMuted)
        }

        Spacer(Modifier.height(6.dp))

        // Pedestal base block
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(pedestalHeight)
                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                .background(Brush.verticalGradient(pedestalGradient))
                .border(
                    1.dp,
                    tierColor.copy(alpha = 0.5f),
                    RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                ),
            contentAlignment = Alignment.TopCenter,
        ) {
            Column(
                modifier = Modifier.padding(top = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "NO.$rank",
                    fontSize = if (isChampion) 18.sp else 14.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = tierColor,
                )
                if (isChampion) {
                    Text(
                        text = "CHAMPION",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldAccent,
                        letterSpacing = 1.sp,
                    )
                }
            }
        }
    }
}

@Composable
private fun RankRowItem(
    entry: RankingEntry,
    category: LeaderboardCategory,
    onClick: () -> Unit,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = SurfaceWhite,
        shadowElevation = 1.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSoft),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Rank Number
            Box(
                modifier = Modifier.width(32.dp),
                contentAlignment = Alignment.CenterStart,
            ) {
                Text(
                    text = "#${entry.rank}",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (entry.rank <= 10) PurpleBrand else InkMuted,
                )
            }

            // Framed Avatar (Static / lightweight animated for list items)
            AvatarWithFrame(
                name = entry.name,
                photoPath = entry.avatarPath,
                size = 46.dp,
                frameTier = FrameTier.fromId(entry.frameId),
                animated = false,
            )

            Spacer(Modifier.width(10.dp))

            // User Info
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = entry.name,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = InkDark,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    if (entry.verified) {
                        Spacer(Modifier.width(4.dp))
                        Icon(
                            painter = painterResource(R.drawable.ic_check_all),
                            contentDescription = "Verified",
                            tint = CyanBrandColor,
                            modifier = Modifier.size(14.dp),
                        )
                    }
                }
                // Optional subtitle / level
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0xFFEDE9F8))
                            .padding(horizontal = 4.dp, vertical = 1.dp),
                    ) {
                        Text(
                            text = "Lv.${entry.level}",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = PurpleBrand,
                        )
                    }
                    if (!entry.subtitle.isNullOrBlank()) {
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = entry.subtitle,
                            fontSize = 11.sp,
                            color = InkMuted,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
            }

            // Score Column
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = entry.scoreFormatted,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = InkDark,
                )
                Text(
                    text = category.scoreUnit,
                    fontSize = 11.sp,
                    color = InkMuted,
                )
            }
        }
    }
}

@Composable
private fun StickyCurrentUserBar(
    summary: com.sundram.weplay.data.CurrentUserRank,
    profile: UserProfile?,
    equippedFrameId: String?,
    onCtaClick: () -> Unit,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding(),
        color = SurfaceWhite,
        shadowElevation = 8.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSoft),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // User Rank Badge
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFF3EEFC)),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = summary.rank?.let { "#$it" } ?: "--",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = PurpleBrand,
                )
            }

            Spacer(Modifier.width(10.dp))

            // User Avatar with current frame
            AvatarWithFrame(
                name = profile?.name ?: "You",
                photoPath = profile?.photoPath,
                size = 42.dp,
                frameTier = FrameTier.fromId(equippedFrameId),
                animated = true,
            )

            Spacer(Modifier.width(10.dp))

            // User Info & Score
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = profile?.name ?: "You",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = InkDark,
                    maxLines = 1,
                )
                Text(
                    text = "${summary.scoreFormatted} ${summary.category.scoreUnit}" +
                        (summary.percentile?.let { " • $it" } ?: ""),
                    fontSize = 11.sp,
                    color = InkMuted,
                )
            }

            // Category Call-to-Action Button
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(CyanBrandColor, PurpleBrand),
                        ),
                    )
                    .clickable(onClick = onCtaClick)
                    .padding(horizontal = 14.dp, vertical = 9.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = summary.ctaLabel,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                )
            }
        }
    }
}

@Composable
private fun RankingRulesDialog(
    rules: RankingRuleInfo,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(rules.category.iconGlyph, fontSize = 20.sp)
                Spacer(Modifier.width(8.dp))
                Text(rules.title, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = InkDark)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(rules.summary, fontSize = 13.sp, color = InkDark)
                HorizontalDivider(color = BorderSoft)
                Text("Calculation Basis:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PurpleBrand)
                rules.calculationRules.forEach { rule ->
                    Text("• $rule", fontSize = 12.sp, color = InkMuted)
                }
                Spacer(Modifier.height(4.dp))
                Text("Reset Schedule: ${rules.resetSchedule}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = InkDark)
                Text("Integrity: ${rules.antiCheatNote}", fontSize = 11.sp, color = InkMuted)
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Got It", color = PurpleBrand, fontWeight = FontWeight.Bold)
            }
        },
    )
}

@Composable
private fun FrameShopSheetContent(
    profile: UserProfile?,
    repository: FrameRepository,
    catalog: List<FrameDefinition>,
    ownedFrames: Set<String>,
    equippedFrameId: String?,
    previewFrameId: String?,
    onSelectPreview: (String) -> Unit,
    onEquip: (String) -> Unit,
    onUnequip: () -> Unit,
    onPurchase: (String) -> Unit,
    feedbackMessage: String?,
) {
    val activePreviewFrame = catalog.firstOrNull { it.id == previewFrameId } ?: catalog.first()
    val isOwned = repository.isFrameUnlocked(activePreviewFrame, profile?.level ?: 1)
    val isEquipped = equippedFrameId == activePreviewFrame.id
    val userCoins = profile?.coins ?: 500L

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp)
            .navigationBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Avatar Frame Wardrobe",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = InkDark,
        )
        Text(
            text = "Personalize your profile glow across rooms, chats, and rankings",
            fontSize = 12.sp,
            color = InkMuted,
            textAlign = TextAlign.Center,
        )

        Spacer(Modifier.height(16.dp))

        // Large Preview on User's Avatar
        Box(
            modifier = Modifier
                .size(130.dp)
                .clip(CircleShape)
                .background(Color(0xFFF6F3FB)),
            contentAlignment = Alignment.Center,
        ) {
            AvatarWithFrame(
                name = profile?.name ?: "User",
                photoPath = profile?.photoPath,
                size = 88.dp,
                frameTier = activePreviewFrame.tier,
                animated = true,
                showCrown = activePreviewFrame.tier == FrameTier.MYTHIC,
            )
        }

        Spacer(Modifier.height(6.dp))

        // Frame Name & Tier Chip
        Text(
            text = activePreviewFrame.name,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = InkDark,
        )
        Row(
            modifier = Modifier.padding(top = 2.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(activePreviewFrame.tier.primaryColor).copy(alpha = 0.2f))
                    .padding(horizontal = 8.dp, vertical = 2.dp),
            ) {
                Text(
                    text = activePreviewFrame.tier.displayName,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(activePreviewFrame.tier.primaryColor),
                )
            }
            Text(
                text = activePreviewFrame.unlockSource.label,
                fontSize = 11.sp,
                color = InkMuted,
            )
        }

        Text(
            text = activePreviewFrame.description,
            fontSize = 12.sp,
            color = InkMuted,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 4.dp, bottom = 12.dp),
        )

        // Feedback toast/message if any
        if (!feedbackMessage.isNullOrBlank()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFEBF8FA))
                    .padding(8.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(feedbackMessage, fontSize = 12.sp, color = CyanBrandColor, fontWeight = FontWeight.SemiBold)
            }
        }

        // Action Buttons (Equip / Unequip / Purchase)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            when {
                isEquipped -> {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .clip(RoundedCornerShape(22.dp))
                            .background(Color(0xFFEDE9F5))
                            .clickable(onClick = onUnequip),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text("Unequip Frame", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = InkDark)
                    }
                }
                isOwned -> {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .clip(RoundedCornerShape(22.dp))
                            .background(
                                Brush.horizontalGradient(listOf(CyanBrandColor, PurpleBrand)),
                            )
                            .clickable { onEquip(activePreviewFrame.id) },
                        contentAlignment = Alignment.Center,
                    ) {
                        Text("Equip Frame", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
                activePreviewFrame.isShopPurchasable -> {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .clip(RoundedCornerShape(22.dp))
                            .background(
                                Brush.horizontalGradient(listOf(GoldAccent, PurpleBrand)),
                            )
                            .clickable { onPurchase(activePreviewFrame.id) },
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "Unlock for 🪙 ${activePreviewFrame.priceCoins} (Balance: $userCoins)",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                        )
                    }
                }
                else -> {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .clip(RoundedCornerShape(22.dp))
                            .background(Color(0xFFF0EDF7)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "Locked (Requires Level ${activePreviewFrame.minLevel})",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = InkMuted,
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(14.dp))

        // Grid of All Frames in Catalog
        Text(
            text = "Select to Preview",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = InkMuted,
            modifier = Modifier.align(Alignment.Start),
        )

        Spacer(Modifier.height(8.dp))

        val scrollState = rememberScrollState()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(scrollState),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            catalog.forEach { frame ->
                val isSelected = frame.id == previewFrameId
                val unlocked = repository.isFrameUnlocked(frame, profile?.level ?: 1)
                val equipped = frame.id == equippedFrameId

                Column(
                    modifier = Modifier
                        .width(76.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (isSelected) Color(0xFFF1EBFC) else SurfaceWhite)
                        .border(
                            1.5.dp,
                            if (isSelected) PurpleBrand else BorderSoft,
                            RoundedCornerShape(14.dp),
                        )
                        .clickable { onSelectPreview(frame.id) }
                        .padding(6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Box(
                        modifier = Modifier.size(52.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        AvatarWithFrame(
                            name = "P",
                            photoPath = null,
                            size = 36.dp,
                            frameTier = frame.tier,
                            animated = false,
                        )
                    }
                    Text(
                        text = frame.name,
                        fontSize = 10.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = InkDark,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = when {
                            equipped -> "Active"
                            unlocked -> "Owned"
                            frame.isShopPurchasable -> "🪙 ${frame.priceCoins}"
                            else -> "Lv.${frame.minLevel}"
                        },
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (equipped) PurpleBrand else InkMuted,
                    )
                }
            }
        }

        Spacer(Modifier.height(12.dp))
    }
}
