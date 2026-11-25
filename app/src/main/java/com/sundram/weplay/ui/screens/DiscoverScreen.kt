package com.sundram.weplay.ui.screens

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
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
import com.sundram.weplay.ui.components.navigateSafe
import com.sundram.weplay.ui.nav.Routes
import com.sundram.weplay.ui.theme.AuroraBorder
import com.sundram.weplay.ui.theme.AuroraDivider
import com.sundram.weplay.ui.theme.AuroraGold
import com.sundram.weplay.ui.theme.AuroraLavender
import com.sundram.weplay.ui.theme.AuroraOrchid
import com.sundram.weplay.ui.theme.AuroraTextPrimary
import com.sundram.weplay.ui.theme.AuroraTextSecondary
import com.sundram.weplay.ui.theme.CelestialNavy
import com.sundram.weplay.ui.theme.CloudWhite
import com.sundram.weplay.ui.theme.CrystalBlue
import com.sundram.weplay.ui.theme.DeepInk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private val DiscoverBgBottom = Color(0xFF140B33)
private val DiscoverCardBg = DeepInk.copy(alpha = 0.92f)
private val DiscoverDialogBg = Color(0xFF1A103E)
private val DiscoverMutedText = Color(0xFFA799C9)
private val DiscoverIconPill = Color(0x1FB89BFF)

private data class DiscoveryShortcut(
    val label: String,
    val iconRes: Int,
    val onClick: () -> Unit,
)

private fun FeedPost.hasEmbeddedPlayBadge(): Boolean =
    mediaResId == R.drawable.ap_discover_preview_girl && mediaUri == null

@Composable
fun DiscoverScreen(
    nav: NavHostController,
    pad: PaddingValues,
    modifier: Modifier = Modifier,
) {
    val repository = remember { LocalContentRepository() }
    var refresh by remember { mutableIntStateOf(0) }
    var state by remember { mutableStateOf<UiState<DiscoverFeed>>(UiState.Loading) }

    var selectedPost by remember { mutableStateOf<FeedPost?>(null) }
    var showNearbyDialog by remember { mutableStateOf(false) }
    var showClubsDialog by remember { mutableStateOf(false) }
    var showActivityDialog by remember { mutableStateOf(false) }
    var showCreatePostDialog by remember { mutableStateOf(false) }

    var localLikedPostIds by remember { mutableStateOf(setOf<String>()) }
    var localAddedPosts by remember { mutableStateOf(listOf<FeedPost>()) }

    LaunchedEffect(refresh) {
        state = UiState.Loading
        state = repository.discoverFeed().fold(
            onSuccess = { feed ->
                if (feed.events.isEmpty() && feed.posts.isEmpty()) {
                    UiState.Empty("No moments or updates right now.")
                } else {
                    UiState.Success(feed)
                }
            },
            onFailure = { UiState.Error(it.message ?: "Could not load Discover.", retryable = true) },
        )
    }

    val layoutDirection = LocalLayoutDirection.current
    val statusTop = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val navBottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    val cutoutPadding = WindowInsets.displayCutout.only(WindowInsetsSides.Horizontal).asPaddingValues()

    // Use Scaffold padding cleanly while falling back to system bar insets if host passes zero padding,
    // without double-padding the status bar or overlapping the bottom navigation bar.
    val safeContentInsets = PaddingValues(
        start = maxOf(
            pad.calculateStartPadding(layoutDirection),
            cutoutPadding.calculateStartPadding(layoutDirection),
        ),
        end = maxOf(
            pad.calculateEndPadding(layoutDirection),
            cutoutPadding.calculateEndPadding(layoutDirection),
        ),
        top = maxOf(pad.calculateTopPadding(), statusTop),
        bottom = maxOf(pad.calculateBottomPadding(), navBottom),
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(listOf(CelestialNavy, DiscoverBgBottom))
            ),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(safeContentInsets),
        ) {
            when (val current = state) {
                UiState.Loading -> LoadingState(Modifier.fillMaxSize())
                is UiState.Empty -> EmptyState(current.message, Modifier.fillMaxSize())
                is UiState.Error -> ErrorState(
                    message = current.message,
                    retryable = current.retryable,
                    onRetry = { refresh++ },
                    modifier = Modifier.fillMaxSize(),
                )
                is UiState.Success -> {
                    val combinedPosts = remember(current.data.posts, localAddedPosts) {
                        localAddedPosts + current.data.posts
                    }

                    DiscoverContent(
                        feed = current.data,
                        posts = combinedPosts,
                        likedPostIds = localLikedPostIds,
                        nav = nav,
                        onPostClick = { selectedPost = it },
                        onNearbyClick = { showNearbyDialog = true },
                        onClubsClick = { showClubsDialog = true },
                        onActivityTodayClick = { showActivityDialog = true },
                        onShareMomentClick = { showCreatePostDialog = true },
                    )
                }
            }
        }

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

        if (showNearbyDialog) {
            NearbyDiscoveryDialog(onDismiss = { showNearbyDialog = false })
        }

        if (showClubsDialog) {
            ClubsSpacesDialog(
                onDismiss = { showClubsDialog = false },
                onExploreFamilies = {
                    showClubsDialog = false
                    nav.navigateSafe(Routes.FAMILY)
                },
            )
        }

        if (showActivityDialog) {
            val feed = (state as? UiState.Success)?.data
            ActivityHistoryDialog(
                events = feed?.events ?: emptyList(),
                onDismiss = { showActivityDialog = false },
            )
        }

        if (showCreatePostDialog) {
            CreateMomentDialog(
                onDismiss = { showCreatePostDialog = false },
                onPostCreated = { newPost ->
                    localAddedPosts = listOf(newPost) + localAddedPosts
                    showCreatePostDialog = false
                },
            )
        }
    }
}

@Composable
private fun DiscoverContent(
    feed: DiscoverFeed,
    posts: List<FeedPost>,
    likedPostIds: Set<String>,
    nav: NavHostController,
    onPostClick: (FeedPost) -> Unit,
    onNearbyClick: () -> Unit,
    onClubsClick: () -> Unit,
    onActivityTodayClick: () -> Unit,
    onShareMomentClick: () -> Unit,
) {
    val shortcuts = remember(nav, onNearbyClick, onClubsClick) {
        listOf(
            DiscoveryShortcut(
                label = "Families",
                iconRes = R.drawable.ic_users,
                onClick = { nav.navigateSafe(Routes.FAMILY) },
            ),
            DiscoveryShortcut(
                label = "Nearby",
                iconRes = R.drawable.ic_location_pin,
                onClick = onNearbyClick,
            ),
            DiscoveryShortcut(
                label = "Clubs & Spaces",
                iconRes = R.drawable.ic_comment,
                onClick = onClubsClick,
            ),
            DiscoveryShortcut(
                label = "Live Events",
                iconRes = R.drawable.ic_calendar,
                onClick = { nav.navigateSafe(Routes.EVENT_CENTER) },
            ),
        )
    }

    val momentRows = remember(posts) { posts.chunked(2) }
    val recentEvents = remember(feed.events) { feed.events.take(2) }

    LazyColumn(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        modifier = Modifier.fillMaxSize(),
    ) {
        item(key = "header") {
            DiscoverHeader()
        }

        item(key = "quick_nav") {
            QuickNavigationRow(shortcuts = shortcuts)
        }

        item(key = "community_activity") {
            CommunityActivitySection(
                events = recentEvents,
                onTodayClick = onActivityTodayClick,
            )
        }

        item(key = "moments_header") {
            MomentsSectionHeader(onShareClick = onShareMomentClick)
        }

        items(
            items = momentRows,
            key = { row -> row.joinToString("_") { it.id.ifEmpty { "${it.who}_${it.title}" } } },
        ) { rowPosts ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                rowPosts.forEach { post ->
                    val isLiked = likedPostIds.contains(post.id)
                    MomentCard(
                        post = post,
                        likesCount = post.likes + if (isLiked) 1 else 0,
                        isLiked = isLiked,
                        onClick = { onPostClick(post) },
                        modifier = Modifier.weight(1f),
                    )
                }
                if (rowPosts.size == 1) {
                    Spacer(Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun DiscoverHeader() {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Discover",
            color = CloudWhite,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = (-0.3).sp,
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = "Explore, connect and meet new people.",
            color = AuroraTextSecondary,
            fontSize = 13.sp,
            fontWeight = FontWeight.Normal,
        )
    }
}

@Composable
private fun QuickNavigationRow(
    shortcuts: List<DiscoveryShortcut>,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        shortcuts.forEach { shortcut ->
            Column(
                modifier = Modifier
                    .weight(1f)
                    .defaultMinSize(minHeight = 76.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(DiscoverCardBg)
                    .border(1.dp, AuroraBorder, RoundedCornerShape(14.dp))
                    .clickable(onClick = shortcut.onClick)
                    .padding(horizontal = 4.dp, vertical = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(DiscoverIconPill),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        painter = painterResource(shortcut.iconRes),
                        contentDescription = shortcut.label,
                        tint = CloudWhite,
                        modifier = Modifier.size(18.dp),
                    )
                }
                Spacer(Modifier.height(6.dp))
                Text(
                    text = shortcut.label,
                    color = CloudWhite,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    lineHeight = 13.sp,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

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
            Icon(
                painter = painterResource(R.drawable.ic_bolt),
                contentDescription = null,
                tint = AuroraLavender,
                modifier = Modifier.size(16.dp),
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = "Community Activity",
                color = CloudWhite,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(Modifier.weight(1f))
            SectionActionLink(
                label = "Today",
                iconRes = R.drawable.ic_chevron_right,
                onClick = onTodayClick,
            )
        }

        Spacer(Modifier.height(10.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(DiscoverCardBg)
                .border(1.dp, AuroraBorder, RoundedCornerShape(14.dp))
                .clickable(onClick = onTodayClick)
                .padding(horizontal = 14.dp, vertical = 12.dp),
        ) {
            events.forEachIndexed { index, event ->
                ActivityRow(event = event)
                if (index < events.lastIndex) {
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 10.dp),
                        thickness = 0.8.dp,
                        color = AuroraDivider,
                    )
                }
            }
        }
    }
}

private fun activityAvatarColor(letter: String): Color = when (letter.uppercase()) {
    "A" -> Color(0xFF6B3FD0)
    "P" -> Color(0xFF2196D3)
    "D" -> Color(0xFFCC7A29)
    "R" -> Color(0xFFC44574)
    else -> Color(0xFF259A7E)
}

@Composable
private fun ActivityRow(event: FeedEvent) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(activityAvatarColor(event.avatarLetter)),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = event.avatarLetter,
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }

        Spacer(Modifier.width(10.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = event.who,
                    color = CloudWhite,
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(Modifier.width(6.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(AuroraGold.copy(alpha = 0.16f))
                        .padding(horizontal = 6.dp, vertical = 1.dp),
                ) {
                    Text(
                        text = "Lv.${event.level}",
                        color = AuroraGold,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                    )
                }
                Spacer(Modifier.weight(1f))
                Text(
                    text = event.time,
                    color = DiscoverMutedText,
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Normal,
                )
            }
            Spacer(Modifier.height(2.dp))
            Text(
                text = event.action,
                color = AuroraTextSecondary,
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun MomentsSectionHeader(onShareClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_camera),
            contentDescription = null,
            tint = AuroraLavender,
            modifier = Modifier.size(16.dp),
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = "Moments",
            color = CloudWhite,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
        )
        Spacer(Modifier.weight(1f))
        SectionActionLink(
            label = "Share",
            iconRes = R.drawable.ic_plus,
            onClick = onShareClick,
        )
    }
}

@Composable
private fun MomentCard(
    post: FeedPost,
    likesCount: Int,
    isLiked: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(DiscoverCardBg)
            .border(1.dp, AuroraBorder, RoundedCornerShape(14.dp))
            .clickable(onClick = onClick),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1.58f)
                .background(DeepInk),
        ) {
            MomentMediaImage(
                post = post,
                fallbackRes = R.drawable.ap_discover_media_fallback,
                contentDescription = post.text,
                modifier = Modifier.fillMaxSize(),
            )

            // Draw play badge only when the image does not already carry one baked in.
            if (post.isVideo && !post.hasEmbeddedPlayBadge()) {
                Box(
                    modifier = Modifier
                        .padding(start = 10.dp, bottom = 10.dp)
                        .align(Alignment.BottomStart)
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(Color(0x99000000)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_play),
                        contentDescription = "Video",
                        tint = Color.White,
                        modifier = Modifier.size(14.dp),
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 8.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = post.who,
                    color = CloudWhite,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )
                Spacer(Modifier.width(4.dp))
                Icon(
                    painter = painterResource(R.drawable.ic_heart),
                    contentDescription = null,
                    tint = if (isLiked) Color(0xFFFF5277) else DiscoverMutedText,
                    modifier = Modifier.size(11.dp),
                )
                Spacer(Modifier.width(3.dp))
                Text(
                    text = "$likesCount",
                    color = if (isLiked) Color(0xFFFF5277) else DiscoverMutedText,
                    fontSize = 11.sp,
                )
            }
            Spacer(Modifier.height(2.dp))
            Text(
                text = post.text,
                color = AuroraTextSecondary,
                fontSize = 11.5.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun SectionActionLink(
    label: String,
    iconRes: Int,
    onClick: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 6.dp, vertical = 4.dp),
    ) {
        Text(
            text = label,
            color = AuroraLavender,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
        )
        Spacer(Modifier.width(3.dp))
        Icon(
            painter = painterResource(iconRes),
            contentDescription = null,
            tint = AuroraLavender,
            modifier = Modifier.size(13.dp),
        )
    }
}

@Composable
private fun DialogCloseButton(onDismiss: () -> Unit) {
    IconButton(
        onClick = onDismiss,
        modifier = Modifier.size(36.dp),
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_close),
            contentDescription = "Close",
            tint = AuroraLavender,
            modifier = Modifier.size(16.dp),
        )
    }
}

@Composable
private fun MomentMediaImage(
    post: FeedPost,
    fallbackRes: Int,
    contentDescription: String?,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val decodedBitmap by produceState<ImageBitmap?>(initialValue = null, key1 = post.mediaUri) {
        value = post.mediaUri?.let { uriStr ->
            withContext(Dispatchers.IO) {
                runCatching {
                    val uri = Uri.parse(uriStr)
                    val opts = BitmapFactory.Options().apply { inJustDecodeBounds = true }
                    context.contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, opts) }
                    var sample = 1
                    while (opts.outWidth / sample > 1080 || opts.outHeight / sample > 1080) {
                        sample *= 2
                    }
                    val readOpts = BitmapFactory.Options().apply { inSampleSize = sample }
                    context.contentResolver.openInputStream(uri)?.use {
                        BitmapFactory.decodeStream(it, null, readOpts)?.asImageBitmap()
                    }
                }.getOrNull()
            }
        }
    }

    if (decodedBitmap != null) {
        Image(
            bitmap = decodedBitmap!!,
            contentDescription = contentDescription,
            contentScale = ContentScale.Crop,
            modifier = modifier,
        )
    } else {
        Image(
            painter = painterResource(post.mediaResId ?: fallbackRes),
            contentDescription = contentDescription,
            contentScale = ContentScale.Crop,
            modifier = modifier,
        )
    }
}

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
            shape = RoundedCornerShape(20.dp),
            color = DiscoverDialogBg,
            border = BorderStroke(1.dp, AuroraBorder),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(activityAvatarColor(post.who.take(1))),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = post.who.take(1).uppercase(),
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 15.sp,
                        )
                    }
                    Spacer(Modifier.width(10.dp))
                    Column(Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = post.who,
                                color = CloudWhite,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 15.sp,
                            )
                            Spacer(Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(AuroraGold.copy(alpha = 0.16f))
                                    .padding(horizontal = 6.dp, vertical = 1.dp),
                            ) {
                                Text(
                                    text = "Lv.${post.userLevel}",
                                    color = AuroraGold,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Medium,
                                )
                            }
                        }
                        Text(
                            text = post.timeAgo,
                            color = DiscoverMutedText,
                            fontSize = 11.5.sp,
                        )
                    }
                    DialogCloseButton(onDismiss)
                }

                Spacer(Modifier.height(14.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1.58f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(DeepInk)
                        .border(1.dp, AuroraBorder, RoundedCornerShape(14.dp)),
                    contentAlignment = Alignment.Center,
                ) {
                    MomentMediaImage(
                        post = post,
                        fallbackRes = R.drawable.ap_discover_media_fallback,
                        contentDescription = post.title,
                        modifier = Modifier.fillMaxSize(),
                    )
                    if (post.isVideo && !post.hasEmbeddedPlayBadge()) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(0x99000000)),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_play),
                                contentDescription = "Video",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp),
                            )
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))

                Text(
                    text = post.text,
                    color = AuroraTextPrimary,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                )

                Spacer(Modifier.height(14.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(DiscoverIconPill)
                            .clickable(onClick = onToggleLike)
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_heart),
                            contentDescription = "Like",
                            tint = if (isLiked) Color(0xFFFF5277) else CloudWhite,
                            modifier = Modifier.size(16.dp),
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = "$likesCount",
                            color = if (isLiked) Color(0xFFFF5277) else CloudWhite,
                            fontWeight = FontWeight.Medium,
                            fontSize = 13.sp,
                        )
                    }

                    Spacer(Modifier.width(10.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(DiscoverIconPill)
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_comment),
                            contentDescription = "Comments",
                            tint = CloudWhite,
                            modifier = Modifier.size(16.dp),
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = "${post.comments}",
                            color = CloudWhite,
                            fontWeight = FontWeight.Medium,
                            fontSize = 13.sp,
                        )
                    }

                    Spacer(Modifier.weight(1f))

                    IconButton(
                        onClick = {
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_SUBJECT, "Community Moment")
                                putExtra(
                                    Intent.EXTRA_TEXT,
                                    "Check out ${post.who}'s moment: \"${post.text}\"",
                                )
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "Share Moment"))
                        },
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(DiscoverIconPill),
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_send),
                            contentDescription = "Share",
                            tint = CloudWhite,
                            modifier = Modifier.size(16.dp),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun NearbyDiscoveryDialog(
    onDismiss: () -> Unit,
) {
    val context = LocalContext.current
    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION,
            ) == PackageManager.PERMISSION_GRANTED
        )
    }
    var permissionDenied by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasPermission = isGranted
        permissionDenied = !isGranted
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = DiscoverDialogBg,
            border = BorderStroke(1.dp, AuroraBorder),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(DiscoverIconPill),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_location_pin),
                        contentDescription = null,
                        tint = CrystalBlue,
                        modifier = Modifier.size(22.dp),
                    )
                }
                Spacer(Modifier.height(12.dp))
                Text(
                    text = "Nearby Players",
                    color = CloudWhite,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = when {
                        hasPermission -> "Approximate location is enabled. Live nearby discovery requires a connected location service."
                        permissionDenied -> "Location access was declined. Enable coarse location in device settings to search for nearby players."
                        else -> "Find players near your area for local matches and voice rooms. Location is checked only when you open Nearby."
                    },
                    color = AuroraTextSecondary,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp,
                )

                if (hasPermission) {
                    Spacer(Modifier.height(14.dp))
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(DiscoverCardBg)
                            .border(1.dp, AuroraBorder, RoundedCornerShape(12.dp))
                            .padding(12.dp),
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(CrystalBlue),
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = "Location ready",
                                color = CrystalBlue,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                            )
                        }
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = "No verified nearby players in your area right now. Global rooms and games are open.",
                            color = AuroraTextSecondary,
                            fontSize = 12.sp,
                            lineHeight = 17.sp,
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = AuroraLavender),
                        border = BorderStroke(1.dp, AuroraBorder),
                    ) {
                        Text("Close")
                    }

                    if (!hasPermission) {
                        Button(
                            onClick = {
                                permissionLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = AuroraOrchid,
                                contentColor = Color.White,
                            ),
                        ) {
                            Text(if (permissionDenied) "Try Again" else "Enable", fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ClubsSpacesDialog(
    onDismiss: () -> Unit,
    onExploreFamilies: () -> Unit,
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = DiscoverDialogBg,
            border = BorderStroke(1.dp, AuroraBorder),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(DiscoverIconPill),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_comment),
                        contentDescription = null,
                        tint = AuroraLavender,
                        modifier = Modifier.size(22.dp),
                    )
                }
                Spacer(Modifier.height(12.dp))
                Text(
                    text = "Clubs & Spaces",
                    color = CloudWhite,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Interest-based clubs are not open yet. Join or create a Family in the meantime to play and chat with a regular group.",
                    color = AuroraTextSecondary,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp,
                )

                Spacer(Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = AuroraLavender),
                        border = BorderStroke(1.dp, AuroraBorder),
                    ) {
                        Text("Close")
                    }

                    Button(
                        onClick = onExploreFamilies,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AuroraOrchid,
                            contentColor = Color.White,
                        ),
                    ) {
                        Text("Families", fontWeight = FontWeight.Medium)
                    }
                }
            }
        }
    }
}

@Composable
private fun ActivityHistoryDialog(
    events: List<FeedEvent>,
    onDismiss: () -> Unit,
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = DiscoverDialogBg,
            border = BorderStroke(1.dp, AuroraBorder),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_bolt),
                        contentDescription = null,
                        tint = AuroraLavender,
                        modifier = Modifier.size(18.dp),
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Today's Activity",
                        color = CloudWhite,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Spacer(Modifier.weight(1f))
                    DialogCloseButton(onDismiss)
                }

                Spacer(Modifier.height(12.dp))

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
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

@Composable
private fun CreateMomentDialog(
    onDismiss: () -> Unit,
    onPostCreated: (FeedPost) -> Unit,
) {
    val context = LocalContext.current
    var textInput by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    val imagePicker = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        selectedImageUri = uri
    }

    val previewBitmap by produceState<ImageBitmap?>(initialValue = null, key1 = selectedImageUri) {
        value = selectedImageUri?.let { uri ->
            withContext(Dispatchers.IO) {
                runCatching {
                    val opts = BitmapFactory.Options().apply { inSampleSize = 2 }
                    context.contentResolver.openInputStream(uri)?.use {
                        BitmapFactory.decodeStream(it, null, opts)?.asImageBitmap()
                    }
                }.getOrNull()
            }
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = DiscoverDialogBg,
            border = BorderStroke(1.dp, AuroraBorder),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        text = "Share Moment",
                        color = CloudWhite,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Spacer(Modifier.weight(1f))
                    DialogCloseButton(onDismiss)
                }

                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = textInput,
                    onValueChange = { textInput = it },
                    placeholder = { Text("Share an update or match note", color = DiscoverMutedText) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = CloudWhite,
                        unfocusedTextColor = CloudWhite,
                        focusedBorderColor = AuroraLavender,
                        unfocusedBorderColor = AuroraBorder,
                        focusedContainerColor = DeepInk,
                        unfocusedContainerColor = DeepInk,
                    ),
                )

                if (previewBitmap != null) {
                    Spacer(Modifier.height(10.dp))
                    Image(
                        bitmap = previewBitmap!!,
                        contentDescription = "Selected photo",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1.5f)
                            .clip(RoundedCornerShape(12.dp))
                            .border(1.dp, AuroraBorder, RoundedCornerShape(12.dp)),
                    )
                }

                Spacer(Modifier.height(14.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Button(
                        onClick = {
                            imagePicker.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DeepInk,
                            contentColor = AuroraLavender,
                        ),
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_camera),
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = if (selectedImageUri != null) "Change Photo" else "Add Photo",
                            fontSize = 12.sp,
                        )
                    }

                    Spacer(Modifier.weight(1f))

                    Button(
                        onClick = {
                            if (textInput.isNotBlank() || selectedImageUri != null) {
                                onPostCreated(
                                    FeedPost(
                                        who = "You",
                                        text = textInput.trim().ifEmpty { "Shared a photo" },
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
                        enabled = textInput.isNotBlank() || selectedImageUri != null,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AuroraOrchid,
                            contentColor = Color.White,
                        ),
                    ) {
                        Text("Post", fontWeight = FontWeight.Medium)
                    }
                }
            }
        }
    }
}
