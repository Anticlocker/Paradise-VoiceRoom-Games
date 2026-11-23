package com.sundram.weplay.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import com.sundram.weplay.R
import com.sundram.weplay.core.EmptyState
import com.sundram.weplay.core.ErrorState
import com.sundram.weplay.core.LoadingState
import com.sundram.weplay.core.UiState
import com.sundram.weplay.platform.content.DiscoverFeed
import com.sundram.weplay.platform.content.FeedEvent
import com.sundram.weplay.platform.content.FeedPost
import com.sundram.weplay.platform.content.LocalContentRepository
import com.sundram.weplay.ui.components.bounceClick
import com.sundram.weplay.ui.nav.Routes

// ─────────────────────────────────────────────────────────────────────────────
// AURORA PARADISE 3.0 DISCOVER COLOR TOKENS (from COLOR_TOKENS.json)
// ─────────────────────────────────────────────────────────────────────────────
private val DiscoverBgDark = Color(0xFF170B40)
private val DiscoverBgLow = Color(0xFF130830)
private val DiscoverSurface = Color(0xFF29164F)
private val DiscoverSurfaceElevated = Color(0xFF342164)
private val DiscoverBorder = Color(0x339872CF)
private val DiscoverBorderGlow = Color(0x40C8B2F4)
private val DiscoverLavender = Color(0xFFC8B2F4)
private val DiscoverCyan = Color(0xFF54CFFF)
private val DiscoverGold = Color(0xFFFFD36B)
private val DiscoverTextPrimary = Color(0xFFFFF8FF)
private val DiscoverTextSecondary = Color(0xFFCFC2E9)
private val DiscoverTextMuted = Color(0xFF9E8DBE)

private data class DiscoveryShortcut(
    val label: String,
    val iconRes: Int,
    val description: String,
    val onClick: () -> Unit,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiscoverScreen(
    nav: NavHostController,
    pad: PaddingValues,
    modifier: Modifier = Modifier,
) {
    val repository = remember { LocalContentRepository() }
    var refresh by remember { mutableStateOf(0) }
    var state by remember { mutableStateOf<UiState<DiscoverFeed>>(UiState.Loading) }

    // Interactive sheet / dialog states
    var selectedPost by remember { mutableStateOf<FeedPost?>(null) }
    var showNearbySheet by remember { mutableStateOf(false) }
    var showClubsSheet by remember { mutableStateOf(false) }
    var showActivitySheet by remember { mutableStateOf(false) }
    var showCreatePostSheet by remember { mutableStateOf(false) }
    var showAllMomentsSheet by remember { mutableStateOf(false) }

    // Dynamic local likes tracking
    var localLikedPostIds by remember { mutableStateOf(setOf<String>()) }
    var localAddedPosts by remember { mutableStateOf(listOf<FeedPost>()) }

    LaunchedEffect(refresh) {
        state = UiState.Loading
        state = repository.discoverFeed().fold(
            onSuccess = { feed ->
                if (feed.events.isEmpty() && feed.posts.isEmpty() && feed.trending.isEmpty()) {
                    UiState.Empty("No moments or updates are available right now.")
                } else {
                    UiState.Success(feed)
                }
            },
            onFailure = { UiState.Error(it.message ?: "Could not load discover feed.", retryable = true) },
        )
    }

    // Atmospheric screen background
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(DiscoverBgDark, DiscoverBgLow)
                )
            ),
    ) {
        // Decorative midnight star backdrop
        Image(
            painter = painterResource(R.drawable.ap_discover_body_midnight),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
            alpha = 0.65f,
        )

        // Moonlit palace header vignette at top
        Image(
            painter = painterResource(R.drawable.ap_discover_header_palace),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            alignment = Alignment.TopEnd,
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp),
            alpha = 0.90f,
        )

        // Soft gradient overlay to ensure perfect readability of header text
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .background(
                    Brush.verticalGradient(
                        0.0f to Color.Transparent,
                        0.75f to DiscoverBgDark.copy(alpha = 0.5f),
                        1.0f to DiscoverBgDark,
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding(),
        ) {
            when (val current = state) {
                UiState.Loading -> LoadingState(Modifier.weight(1f))
                is UiState.Empty -> EmptyState(current.message, Modifier.weight(1f))
                is UiState.Error -> ErrorState(
                    message = current.message,
                    retryable = current.retryable,
                    onRetry = { refresh++ },
                    modifier = Modifier.weight(1f),
                )
                is UiState.Success -> {
                    val combinedPosts = remember(current.data.posts, localAddedPosts) {
                        localAddedPosts + current.data.posts
                    }

                    DiscoverContent(
                        feed = current.data,
                        posts = combinedPosts,
                        nav = nav,
                        pad = pad,
                        likedPostIds = localLikedPostIds,
                        onPostClick = { selectedPost = it },
                        onMoreMomentsClick = { showAllMomentsSheet = true },
                        onNearbyClick = { showNearbySheet = true },
                        onClubsClick = { showClubsSheet = true },
                        onActivityTodayClick = { showActivitySheet = true },
                        onCommunityMomentsClick = { showCreatePostSheet = true },
                    )
                }
            }
        }

        // ─────────────────────────────────────────────────────────────────────
        // INTERACTIVE DIALOGS & MODAL SHEETS
        // ─────────────────────────────────────────────────────────────────────

        // 1. Moment Detail Viewer Sheet
        selectedPost?.let { post ->
            val isLiked = localLikedPostIds.contains(post.id)
            val currentLikes = post.likes + if (isLiked) 1 else 0

            MomentDetailDialog(
                post = post,
                isLiked = isLiked,
                likesCount = currentLikes,
                onDismiss = { selectedPost = null },
                onToggleLike = {
                    localLikedPostIds = if (isLiked) {
                        localLikedPostIds - post.id
                    } else {
                        localLikedPostIds + post.id
                    }
                },
            )
        }

        // 2. Nearby Players Discovery Sheet
        if (showNearbySheet) {
            NearbyDiscoveryDialog(
                onDismiss = { showNearbySheet = false },
            )
        }

        // 3. Clubs & Spaces Honest Dialog
        if (showClubsSheet) {
            ClubsSpacesDialog(
                onDismiss = { showClubsSheet = false },
                onExploreFamilies = {
                    showClubsSheet = false
                    nav.navigate(Routes.FAMILY)
                },
            )
        }

        // 4. Community Activity Full History Sheet
        if (showActivitySheet) {
            val feed = (state as? UiState.Success)?.data
            ActivityHistoryDialog(
                events = feed?.events ?: emptyList(),
                onDismiss = { showActivitySheet = false },
            )
        }

        // 5. Create Moment / Post Composer Sheet
        if (showCreatePostSheet) {
            CreateMomentDialog(
                onDismiss = { showCreatePostSheet = false },
                onPostCreated = { newPost ->
                    localAddedPosts = listOf(newPost) + localAddedPosts
                    showCreatePostSheet = false
                },
            )
        }

        // 6. All Community Moments Gallery Sheet
        if (showAllMomentsSheet) {
            val allPosts = (state as? UiState.Success)?.data?.let { localAddedPosts + it.posts } ?: emptyList()
            AllMomentsDialog(
                posts = allPosts,
                onPostClick = {
                    showAllMomentsSheet = false
                    selectedPost = it
                },
                onDismiss = { showAllMomentsSheet = false },
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// MAIN SCROLLABLE DISCOVER CONTENT HIERARCHY
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun DiscoverContent(
    feed: DiscoverFeed,
    posts: List<FeedPost>,
    nav: NavHostController,
    pad: PaddingValues,
    likedPostIds: Set<String>,
    onPostClick: (FeedPost) -> Unit,
    onMoreMomentsClick: () -> Unit,
    onNearbyClick: () -> Unit,
    onClubsClick: () -> Unit,
    onActivityTodayClick: () -> Unit,
    onCommunityMomentsClick: () -> Unit,
) {
    val events = feed.events

    LazyColumn(
        contentPadding = PaddingValues(
            start = 16.dp,
            end = 16.dp,
            top = 4.dp,
            bottom = pad.calculateBottomPadding() + 24.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxSize(),
    ) {
        // Item 1: Moonlit Palace Header Title & Subtitle
        item(key = "header_section") {
            DiscoverHeroHeader()
        }

        // Item 2: Community Moments Horizontal Strip
        item(key = "community_moments_section") {
            CommunityMomentsSection(
                posts = posts,
                onPostClick = onPostClick,
                onMoreClick = onMoreMomentsClick,
            )
        }

        // Item 3: Four Quick-Access Shortcut Tiles
        item(key = "shortcuts_section") {
            DiscoveryShortcutsGrid(
                shortcuts = listOf(
                    DiscoveryShortcut(
                        label = "Families",
                        iconRes = R.drawable.ap_discover_ic_families,
                        description = "Join and explore clan families",
                        onClick = { nav.navigate(Routes.FAMILY) },
                    ),
                    DiscoveryShortcut(
                        label = "Nearby",
                        iconRes = R.drawable.ap_discover_ic_nearby,
                        description = "Find players near you",
                        onClick = onNearbyClick,
                    ),
                    DiscoveryShortcut(
                        label = "Clubs & Spaces",
                        iconRes = R.drawable.ap_discover_ic_clubs,
                        description = "Voice and interest clubs",
                        onClick = onClubsClick,
                    ),
                    DiscoveryShortcut(
                        label = "Live Events",
                        iconRes = R.drawable.ap_discover_ic_events,
                        description = "Tournaments and shows",
                        onClick = { nav.navigate(Routes.EVENT_CENTER) },
                    ),
                )
            )
        }

        // Item 4: Compact Community Activity Grouped Card Panel
        item(key = "community_activity_section") {
            CommunityActivitySection(
                events = events,
                onTodayClick = onActivityTodayClick,
            )
        }

        // Item 5: Larger Moments Feed / Media Preview Cards
        item(key = "moments_feed_preview_section") {
            MomentsPreviewSection(
                posts = posts,
                likedPostIds = likedPostIds,
                onPostClick = onPostClick,
                onCommunityClick = onCommunityMomentsClick,
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// COMPONENT 1: HERO HEADER
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun DiscoverHeroHeader() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp, bottom = 4.dp),
    ) {
        Text(
            text = "Discover",
            color = DiscoverTextPrimary,
            fontSize = 28.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = (-0.5).sp,
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = "Explore community moments and live updates",
            color = DiscoverLavender,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// COMPONENT 2: COMMUNITY MOMENTS HORIZONTAL MEDIA STRIP
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun CommunityMomentsSection(
    posts: List<FeedPost>,
    onPostClick: (FeedPost) -> Unit,
    onMoreClick: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Community Moments",
                color = DiscoverTextPrimary,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.weight(1f))
            Text(
                text = "More >",
                color = DiscoverLavender,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable(onClick = onMoreClick)
                    .padding(horizontal = 4.dp, vertical = 2.dp),
            )
        }

        Spacer(Modifier.height(10.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(horizontal = 0.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            items(posts.take(6), key = { it.id.ifEmpty { "${it.who}_${it.title}" } }) { post ->
                CommunityMomentCard(
                    post = post,
                    onClick = { onPostClick(post) },
                )
            }
        }
    }
}

@Composable
private fun CommunityMomentCard(
    post: FeedPost,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .width(100.dp)
            .height(126.dp)
            .shadow(4.dp, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .background(DiscoverSurface)
            .border(1.dp, DiscoverBorderGlow, RoundedCornerShape(16.dp))
            .bounceClick(onClick = onClick),
    ) {
        // Thumbnail Image or Artistic Card
        val res = post.mediaResId ?: R.drawable.ap_discover_media_fallback
        Image(
            painter = painterResource(res),
            contentDescription = post.title.ifEmpty { "Community Moment" },
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
        )

        // Gradient overlay for contrast
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        0.5f to Color.Transparent,
                        1.0f to Color(0xCC11082C),
                    )
                )
        )

        // Subtle decorative highlight border
        Box(
            modifier = Modifier
                .fillMaxSize()
                .border(
                    width = 1.dp,
                    brush = Brush.verticalGradient(
                        listOf(Color(0x40FFFFFF), Color.Transparent, Color(0x209872CF))
                    ),
                    shape = RoundedCornerShape(16.dp),
                )
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// COMPONENT 3: FOUR QUICK-ACCESS SHORTCUT TILES
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun DiscoveryShortcutsGrid(
    shortcuts: List<DiscoveryShortcut>,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        shortcuts.forEach { shortcut ->
            Column(
                modifier = Modifier
                    .weight(1f)
                    .height(96.dp)
                    .shadow(3.dp, RoundedCornerShape(18.dp))
                    .clip(RoundedCornerShape(18.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                DiscoverSurfaceElevated,
                                DiscoverSurface,
                            )
                        )
                    )
                    .border(1.dp, DiscoverBorder, RoundedCornerShape(18.dp))
                    .bounceClick(onClick = shortcut.onClick)
                    .padding(vertical = 10.dp, horizontal = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Image(
                    painter = painterResource(shortcut.iconRes),
                    contentDescription = shortcut.label,
                    modifier = Modifier.size(38.dp),
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = shortcut.label,
                    color = DiscoverTextPrimary,
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// COMPONENT 4: COMPACT COMMUNITY ACTIVITY (2–3 ROWS)
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun CommunityActivitySection(
    events: List<FeedEvent>,
    onTodayClick: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                painter = painterResource(R.drawable.ap_discover_ic_activity),
                contentDescription = null,
                modifier = Modifier.size(18.dp),
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = "Community activity",
                color = DiscoverTextPrimary,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.weight(1f))
            Text(
                text = "Today >",
                color = DiscoverLavender,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable(onClick = onTodayClick)
                    .padding(horizontal = 4.dp, vertical = 2.dp),
            )
        }

        Spacer(Modifier.height(10.dp))

        // Grouped Translucent Glass Card
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(4.dp, RoundedCornerShape(20.dp))
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFF26144D))
                .border(1.dp, DiscoverBorder, RoundedCornerShape(20.dp))
                .padding(horizontal = 14.dp, vertical = 14.dp),
        ) {
            val displayEvents = events.take(2)
            displayEvents.forEachIndexed { index, event ->
                ActivityRow(event = event)
                if (index < displayEvents.lastIndex) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp)
                            .height(0.8.dp)
                            .background(Color(0x1FC8B2F4)),
                    )
                }
            }
        }
    }
}

@Composable
private fun ActivityRow(event: FeedEvent) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Initial Avatar circle with themed background
        val avatarBg = if (event.avatarLetter == "A") Color(0xFF7435D9) else Color(0xFF1EA7E1)
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(avatarBg),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = event.avatarLetter,
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
            )
        }

        Spacer(Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = event.who,
                    color = DiscoverTextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(Modifier.width(8.dp))
                // Level Badge Pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0x33FFD36B))
                        .border(0.8.dp, Color(0x88FFD36B), RoundedCornerShape(8.dp))
                        .padding(horizontal = 6.dp, vertical = 1.dp),
                ) {
                    Text(
                        text = "Lv.${event.level}",
                        color = DiscoverGold,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
                Spacer(Modifier.weight(1f))
                Text(
                    text = event.time,
                    color = DiscoverTextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                )
            }
            Spacer(Modifier.height(3.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = event.action + if (event.iconEmoji != null) " ${event.iconEmoji}" else "",
                    color = DiscoverTextSecondary,
                    fontSize = 12.5.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// COMPONENT 5: MOMENTS FEED PREVIEW (2 LARGE CARDS)
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun MomentsPreviewSection(
    posts: List<FeedPost>,
    likedPostIds: Set<String>,
    onPostClick: (FeedPost) -> Unit,
    onCommunityClick: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                painter = painterResource(R.drawable.ap_discover_ic_moments),
                contentDescription = null,
                modifier = Modifier.size(18.dp),
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = "Moments",
                color = DiscoverTextPrimary,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.weight(1f))
            Text(
                text = "Community >",
                color = DiscoverLavender,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable(onClick = onCommunityClick)
                    .padding(horizontal = 4.dp, vertical = 2.dp),
            )
        }

        Spacer(Modifier.height(10.dp))

        // Two large side-by-side cards exactly as in approved reference
        val previewLeft = posts.getOrNull(0) ?: FeedPost(
            who = "Sneha",
            text = "Acoustic vibes on loop! 🎧",
            likes = 24,
            comments = 8,
            id = "left_prev",
            mediaResId = R.drawable.ap_discover_media_fallback,
            isVideo = true,
        )
        val previewRight = posts.getOrNull(1) ?: FeedPost(
            who = "Ankit",
            text = "Twilight moon over the palace 🌙",
            likes = 42,
            comments = 5,
            id = "right_prev",
            mediaResId = R.drawable.ap_discover_header_palace,
            isVideo = false,
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            // Left Card (Video Indicator)
            LargeMomentCard(
                post = previewLeft,
                modifier = Modifier.weight(1f),
                isVideo = previewLeft.isVideo,
                onClick = { onPostClick(previewLeft) },
            )

            // Right Card (Sunset / Landscape visual)
            LargeMomentCard(
                post = previewRight,
                modifier = Modifier.weight(1f),
                isVideo = previewRight.isVideo,
                onClick = { onPostClick(previewRight) },
            )
        }
    }
}

@Composable
private fun LargeMomentCard(
    post: FeedPost,
    isVideo: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Box(
        modifier = modifier
            .height(150.dp)
            .shadow(4.dp, RoundedCornerShape(18.dp))
            .clip(RoundedCornerShape(18.dp))
            .background(DiscoverSurface)
            .border(1.dp, DiscoverBorder, RoundedCornerShape(18.dp))
            .bounceClick(onClick = onClick),
    ) {
        val res = post.mediaResId ?: R.drawable.ap_discover_media_fallback
        Image(
            painter = painterResource(res),
            contentDescription = post.text,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
        )

        // Gradient shadow at bottom
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        0.5f to Color.Transparent,
                        1.0f to Color(0x9911082C),
                    )
                )
        )

        // Video Play Button Indicator in bottom left
        if (isVideo) {
            Box(
                modifier = Modifier
                    .padding(start = 12.dp, bottom = 12.dp)
                    .align(Alignment.BottomStart)
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(Color(0x80000000)),
                contentAlignment = Alignment.Center,
            ) {
                Canvas(modifier = Modifier.size(12.dp)) {
                    val path = Path().apply {
                        moveTo(size.width * 0.25f, size.height * 0.15f)
                        lineTo(size.width * 0.85f, size.height * 0.5f)
                        lineTo(size.width * 0.25f, size.height * 0.85f)
                        close()
                    }
                    drawPath(path, color = Color.White)
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// DIALOG 1: INTERACTIVE MOMENT VIEWER
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun MomentDetailDialog(
    post: FeedPost,
    isLiked: Boolean,
    likesCount: Int,
    onDismiss: () -> Unit,
    onToggleLike: () -> Unit,
) {
    val context = LocalContext.current

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = DiscoverBgDark,
            border = androidx.compose.foundation.BorderStroke(1.dp, DiscoverBorderGlow),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
            ) {
                // Header: Author Avatar, Name, Level, Close
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF7435D9)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = post.who.take(1).uppercase(),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                        )
                    }
                    Spacer(Modifier.width(10.dp))
                    Column(Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = post.who,
                                color = DiscoverTextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                            )
                            Spacer(Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(0x33FFD36B))
                                    .padding(horizontal = 6.dp, vertical = 1.dp),
                            ) {
                                Text(
                                    text = "Lv.${post.userLevel}",
                                    color = DiscoverGold,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                )
                            }
                        }
                        Text(
                            text = post.timeAgo,
                            color = DiscoverTextSecondary,
                            fontSize = 11.sp,
                        )
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Text("✕", color = DiscoverLavender, fontSize = 16.sp)
                    }
                }

                Spacer(Modifier.height(14.dp))

                // Post Visual Media
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(DiscoverSurface)
                        .border(1.dp, DiscoverBorder, RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center,
                ) {
                    val res = post.mediaResId ?: R.drawable.ap_discover_media_fallback
                    Image(
                        painter = painterResource(res),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                    )
                    if (post.isVideo) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(Color(0x80000000)),
                            contentAlignment = Alignment.Center,
                        ) {
                            Canvas(modifier = Modifier.size(16.dp)) {
                                val path = Path().apply {
                                    moveTo(size.width * 0.25f, size.height * 0.15f)
                                    lineTo(size.width * 0.85f, size.height * 0.5f)
                                    lineTo(size.width * 0.25f, size.height * 0.85f)
                                    close()
                                }
                                drawPath(path, color = Color.White)
                            }
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))

                // Post Caption
                Text(
                    text = post.text,
                    color = DiscoverTextPrimary,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                )

                Spacer(Modifier.height(16.dp))

                // Action Bar: Like, Comment, Share
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    // Like button
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0x22FFFFFF))
                            .clickable(onClick = onToggleLike)
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                    ) {
                        Image(
                            painter = painterResource(R.drawable.ap_discover_ic_heart),
                            contentDescription = "Like",
                            modifier = Modifier.size(18.dp),
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = "$likesCount",
                            color = if (isLiked) Color(0xFFFF5277) else DiscoverTextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                        )
                    }

                    Spacer(Modifier.width(12.dp))

                    // Comment button
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0x22FFFFFF))
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                    ) {
                        Image(
                            painter = painterResource(R.drawable.ap_discover_ic_comment),
                            contentDescription = "Comment",
                            modifier = Modifier.size(18.dp),
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = "${post.comments}",
                            color = DiscoverTextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                        )
                    }

                    Spacer(Modifier.weight(1f))

                    // Share button
                    IconButton(
                        onClick = {
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_SUBJECT, "Aurora Paradise Moment")
                                putExtra(
                                    Intent.EXTRA_TEXT,
                                    "Check out ${post.who}'s moment on Aurora: \"${post.text}\"",
                                )
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "Share Moment"))
                        },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0x22FFFFFF)),
                    ) {
                        Image(
                            painter = painterResource(R.drawable.ap_discover_ic_share),
                            contentDescription = "Share",
                            modifier = Modifier.size(18.dp),
                        )
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// DIALOG 2: NEARBY DISCOVERY SHEET
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun NearbyDiscoveryDialog(
    onDismiss: () -> Unit,
) {
    var permissionRequested by remember { mutableStateOf(false) }
    var hasPermission by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasPermission = isGranted
        permissionRequested = true
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = DiscoverBgDark,
            border = androidx.compose.foundation.BorderStroke(1.dp, DiscoverBorderGlow),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Image(
                    painter = painterResource(R.drawable.ap_discover_ic_location),
                    contentDescription = null,
                    modifier = Modifier.size(54.dp),
                )
                Spacer(Modifier.height(14.dp))
                Text(
                    text = "Nearby Players",
                    color = DiscoverTextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = if (hasPermission) {
                        "Scanning for active Aurora gamers in your local radius..."
                    } else {
                        "Discover gamers near your area for local multiplayer parties, voice hangouts, and matches. Location is only used while exploring nearby."
                    },
                    color = DiscoverTextSecondary,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp,
                )

                Spacer(Modifier.height(18.dp))

                if (hasPermission) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(DiscoverSurface)
                            .padding(12.dp),
                    ) {
                        Text(
                            text = "🟢 3 Players nearby",
                            color = DiscoverCyan,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                        )
                        Spacer(Modifier.height(6.dp))
                        Text("• Ankit (1.2 km away) — Playing Ludo", color = DiscoverTextPrimary, fontSize = 12.sp)
                        Text("• Priya (2.5 km away) — In Voice Lounge", color = DiscoverTextPrimary, fontSize = 12.sp)
                        Text("• Raj (3.1 km away) — Online", color = DiscoverTextPrimary, fontSize = 12.sp)
                    }
                }

                Spacer(Modifier.height(18.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = DiscoverLavender,
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, DiscoverBorder),
                    ) {
                        Text("Close")
                    }

                    if (!hasPermission) {
                        Button(
                            onClick = {
                                permissionLauncher.launch(android.Manifest.permission.ACCESS_COARSE_LOCATION)
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF7435D9),
                                contentColor = Color.White,
                            ),
                        ) {
                            Text("Enable", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// DIALOG 3: CLUBS & SPACES HONEST DIALOG
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun ClubsSpacesDialog(
    onDismiss: () -> Unit,
    onExploreFamilies: () -> Unit,
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = DiscoverBgDark,
            border = androidx.compose.foundation.BorderStroke(1.dp, DiscoverBorderGlow),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Image(
                    painter = painterResource(R.drawable.ap_discover_ic_clubs),
                    contentDescription = null,
                    modifier = Modifier.size(54.dp),
                )
                Spacer(Modifier.height(14.dp))
                Text(
                    text = "Clubs & Spaces",
                    color = DiscoverTextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Special interest community clubs and topic spaces are launching in the upcoming Aurora Paradise release! In the meantime, join Family Clubs to chat, play, and level up together.",
                    color = DiscoverTextSecondary,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp,
                )

                Spacer(Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = DiscoverLavender,
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, DiscoverBorder),
                    ) {
                        Text("Dismiss")
                    }

                    Button(
                        onClick = onExploreFamilies,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF7435D9),
                            contentColor = Color.White,
                        ),
                    ) {
                        Text("Explore Families", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// DIALOG 4: COMMUNITY ACTIVITY FULL HISTORY
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun ActivityHistoryDialog(
    events: List<FeedEvent>,
    onDismiss: () -> Unit,
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = DiscoverBgDark,
            border = androidx.compose.foundation.BorderStroke(1.dp, DiscoverBorderGlow),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Image(
                        painter = painterResource(R.drawable.ap_discover_ic_activity),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Today's Activity",
                        color = DiscoverTextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(Modifier.weight(1f))
                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Text("✕", color = DiscoverLavender, fontSize = 16.sp)
                    }
                }

                Spacer(Modifier.height(14.dp))

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 340.dp),
                ) {
                    items(events, key = { "${it.who}_${it.time}_${it.action}" }) { event ->
                        ActivityRow(event = event)
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// DIALOG 5: CREATE MOMENT / POST COMPOSER
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun CreateMomentDialog(
    onDismiss: () -> Unit,
    onPostCreated: (FeedPost) -> Unit,
) {
    var textInput by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    val imagePicker = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        selectedImageUri = uri
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = DiscoverBgDark,
            border = androidx.compose.foundation.BorderStroke(1.dp, DiscoverBorderGlow),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        text = "Create Moment",
                        color = DiscoverTextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(Modifier.weight(1f))
                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Text("✕", color = DiscoverLavender, fontSize = 16.sp)
                    }
                }

                Spacer(Modifier.height(14.dp))

                OutlinedTextField(
                    value = textInput,
                    onValueChange = { textInput = it },
                    placeholder = { Text("What's happening in your game world?", color = DiscoverTextMuted) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = DiscoverTextPrimary,
                        unfocusedTextColor = DiscoverTextPrimary,
                        focusedBorderColor = DiscoverLavender,
                        unfocusedBorderColor = DiscoverBorder,
                        focusedContainerColor = DiscoverSurface,
                        unfocusedContainerColor = DiscoverSurface,
                    ),
                )

                Spacer(Modifier.height(14.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Button(
                        onClick = {
                            imagePicker.launch(
                                androidx.activity.result.PickVisualMediaRequest(
                                    ActivityResultContracts.PickVisualMedia.ImageOnly
                                )
                            )
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DiscoverSurfaceElevated,
                            contentColor = DiscoverLavender,
                        ),
                    ) {
                        Image(
                            painter = painterResource(R.drawable.ap_discover_ic_gallery),
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = if (selectedImageUri != null) "Photo Selected" else "Add Photo",
                            fontSize = 12.sp,
                        )
                    }

                    Spacer(Modifier.weight(1f))

                    Button(
                        onClick = {
                            if (textInput.isNotBlank()) {
                                onPostCreated(
                                    FeedPost(
                                        who = "You",
                                        text = textInput.trim(),
                                        likes = 0,
                                        comments = 0,
                                        id = "custom_${System.currentTimeMillis()}",
                                        mediaResId = R.drawable.ap_discover_media_fallback,
                                        mediaUri = selectedImageUri?.toString(),
                                        timeAgo = "Just now",
                                    )
                                )
                            }
                        },
                        enabled = textInput.isNotBlank(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF7435D9),
                            contentColor = Color.White,
                        ),
                    ) {
                        Text("Post", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// DIALOG 6: ALL COMMUNITY MOMENTS GALLERY
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun AllMomentsDialog(
    posts: List<FeedPost>,
    onPostClick: (FeedPost) -> Unit,
    onDismiss: () -> Unit,
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = DiscoverBgDark,
            border = androidx.compose.foundation.BorderStroke(1.dp, DiscoverBorderGlow),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        text = "Community Moments",
                        color = DiscoverTextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(Modifier.weight(1f))
                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Text("✕", color = DiscoverLavender, fontSize = 16.sp)
                    }
                }

                Spacer(Modifier.height(14.dp))

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 380.dp),
                ) {
                    items(posts, key = { it.id.ifEmpty { "${it.who}_${it.title}" } }) { post ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(DiscoverSurface)
                                .border(1.dp, DiscoverBorder, RoundedCornerShape(14.dp))
                                .clickable { onPostClick(post) }
                                .padding(10.dp),
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color(0xFF342164)),
                            ) {
                                val res = post.mediaResId ?: R.drawable.ap_discover_media_fallback
                                Image(
                                    painter = painterResource(res),
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize(),
                                )
                            }
                            Spacer(Modifier.width(10.dp))
                            Column(Modifier.weight(1f)) {
                                Text(
                                    text = post.who,
                                    color = DiscoverTextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                )
                                Text(
                                    text = post.text,
                                    color = DiscoverTextSecondary,
                                    fontSize = 12.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            }
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = "❤️ ${post.likes}",
                                color = DiscoverLavender,
                                fontSize = 11.sp,
                            )
                        }
                    }
                }
            }
        }
    }
}
