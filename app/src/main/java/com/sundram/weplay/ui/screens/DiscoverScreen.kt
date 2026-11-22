package com.sundram.weplay.ui.screens

import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import android.net.Uri
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import coil.compose.AsyncImage
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import com.sundram.weplay.R
import com.sundram.weplay.data.CommunityClub
import com.sundram.weplay.data.DiscoverEvent
import com.sundram.weplay.data.DiscoverStore
import com.sundram.weplay.data.LiveActivityEvent
import com.sundram.weplay.data.MomentPost
import com.sundram.weplay.data.NearbyPlayer
import com.sundram.weplay.data.UserProfile
import com.sundram.weplay.ui.nav.Routes
import com.sundram.weplay.ui.theme.Gold
import com.sundram.weplay.ui.theme.KtvRed
import com.sundram.weplay.ui.theme.LiveRed
import com.sundram.weplay.ui.theme.MintGreen
import com.sundram.weplay.ui.theme.PinkGift
import com.sundram.weplay.ui.theme.VyboraBg
import com.sundram.weplay.ui.theme.VyboraBorder
import com.sundram.weplay.ui.theme.VyboraCyan
import com.sundram.weplay.ui.theme.VyboraCyanSoft
import com.sundram.weplay.ui.theme.VyboraGold
import com.sundram.weplay.ui.theme.VyboraLavender
import com.sundram.weplay.ui.theme.VyboraPurple
import com.sundram.weplay.ui.theme.VyboraSurface
import com.sundram.weplay.ui.theme.VyboraTextPrimary
import com.sundram.weplay.ui.theme.VyboraTextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiscoverScreen(
    nav: NavHostController,
    pad: PaddingValues,
    profile: UserProfile? = null,
) {
    val context = LocalContext.current
    val store = remember { DiscoverStore(context) }
    var version by remember { mutableIntStateOf(0) }
    fun bump() { version++ }

    var searchQuery by remember { mutableStateOf("") }
    var selectedTag by remember { mutableStateOf("All") }

    // Bottom sheets state
    var showCreateMomentSheet by remember { mutableStateOf(false) }
    var showNearbySheet by remember { mutableStateOf(false) }
    var showClubsSheet by remember { mutableStateOf(false) }
    var showEventsSheet by remember { mutableStateOf(false) }
    var activeCommentPost by remember { mutableStateOf<MomentPost?>(null) }

    // Data snapshots
    val allMoments = remember(version) { store.getAllMoments() }
    val allLiveUpdates = remember(version) { store.getAllLiveUpdates() }
    val allClubs = remember(version) { store.getAllClubs() }
    val allEvents = remember(version) { store.getAllEvents() }
    val nearbyPlayers = remember(version) { store.getNearbyPlayers() }

    // Filtered moments
    val filteredMoments = remember(allMoments, selectedTag, searchQuery) {
        allMoments.filter { post ->
            val matchTag = selectedTag == "All" || post.tag.equals(selectedTag, ignoreCase = true)
            val matchQuery = searchQuery.isBlank() ||
                post.text.contains(searchQuery, ignoreCase = true) ||
                post.authorName.contains(searchQuery, ignoreCase = true) ||
                post.tag.contains(searchQuery, ignoreCase = true)
            matchTag && matchQuery
        }
    }

    // Filtered live updates
    val filteredUpdates = remember(allLiveUpdates, selectedTag, searchQuery) {
        allLiveUpdates.filter { event ->
            val matchTag = selectedTag == "All" || event.tag.equals(selectedTag, ignoreCase = true)
            val matchQuery = searchQuery.isBlank() ||
                event.who.contains(searchQuery, ignoreCase = true) ||
                event.action.contains(searchQuery, ignoreCase = true)
            matchTag && matchQuery
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(VyboraBg)
            .statusBarsPadding()
            .padding(pad),
    ) {
        LazyColumn(
            contentPadding = PaddingValues(bottom = 90.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize(),
        ) {
            // ── Section A: Header / Personalized Welcome ──────────────────────────
            item {
                DiscoverHeaderSection(
                    userName = profile?.name ?: "Explorer",
                    searchQuery = searchQuery,
                    onSearchQueryChange = { searchQuery = it },
                    onCreateMoment = { showCreateMomentSheet = true },
                )
            }

            // ── Section B: Featured Moments Hero ──────────────────────────────────
            item {
                DiscoverMomentsHero(
                    latestPost = allMoments.firstOrNull(),
                    onCreateMoment = { showCreateMomentSheet = true },
                    onExploreMoments = { selectedTag = "All"; searchQuery = "" },
                )
            }

            // ── Section C: Community Shortcuts (4 Feature Tiles) ─────────────────
            item {
                CommunityShortcutsGrid(
                    onFamiliesClick = { nav.navigate(Routes.FAMILY) },
                    onNearbyClick = { showNearbySheet = true },
                    onClubsClick = { showClubsSheet = true },
                    onEventsClick = { showEventsSheet = true },
                )
            }

            // ── Section D: Trending Discovery Chips ──────────────────────────────
            item {
                TrendingDiscoveryBar(
                    tags = DiscoverStore.TAGS,
                    selectedTag = selectedTag,
                    onSelectTag = { selectedTag = it },
                )
            }

            // ── Section E: Social Activity / Live Updates ────────────────────────
            if (filteredUpdates.isNotEmpty()) {
                item {
                    LiveUpdatesSection(
                        events = filteredUpdates,
                        onEventClick = { targetRoute ->
                            if (targetRoute.isNotBlank()) nav.navigate(targetRoute)
                        },
                    )
                }
            }

            // ── Section F: Community Moments Header ──────────────────────────────
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        "Community Moments",
                        color = VyboraTextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f),
                    )
                    Text(
                        "${filteredMoments.size} Posts",
                        color = VyboraTextSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                    )
                }
            }

            // ── Section F: Community Moments Feed Items ──────────────────────────
            if (filteredMoments.isEmpty()) {
                item {
                    EmptyMomentsView(
                        query = searchQuery,
                        selectedTag = selectedTag,
                        onCreateMoment = { showCreateMomentSheet = true },
                    )
                }
            } else {
                itemsIndexed(filteredMoments, key = { index, post -> "${post.id}_$index" }) { _, post ->
                    MomentPostCard(
                        post = post,
                        onLikeClick = {
                            store.toggleLike(post.id)
                            bump()
                        },
                        onCommentClick = {
                            activeCommentPost = post
                        },
                        onShareClick = {
                            val clip = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clip.setPrimaryClip(ClipData.newPlainText("moment_post", "${post.authorName}: ${post.text}"))
                            Toast.makeText(context, "Moment copied to clipboard! 📋", Toast.LENGTH_SHORT).show()
                        },
                        onUserClick = {
                            nav.navigate(Routes.userRoute(post.authorId))
                        },
                    )
                }
            }
        }

        // ── Section G: Floating Gift Pack Widget removed ──────────────────────
    }

    // ── Bottom Sheets ────────────────────────────────────────────────────────

    // 1. Create Moment Sheet
    if (showCreateMomentSheet) {
        CreateMomentBottomSheet(
            currentUserName = profile?.name ?: "Me",
            currentUserId = profile?.id ?: "me",
            onDismiss = { showCreateMomentSheet = false },
            onPost = { text, tag ->
                store.createMoment(
                    text = text,
                    tag = tag,
                    authorName = profile?.name ?: "Me",
                    authorId = profile?.id ?: "me",
                    avatarRes = R.drawable.vb_discover_avatar_01,
                )
                showCreateMomentSheet = false
                bump()
                Toast.makeText(context, "Moment shared with the community! ✨", Toast.LENGTH_SHORT).show()
            },
        )
    }

    // 2. Nearby Players Sheet
    if (showNearbySheet) {
        NearbyPlayersBottomSheet(
            players = nearbyPlayers,
            isVisibleToOthers = store.isNearbyVisible(),
            onToggleVisibility = {
                store.setNearbyVisible(it)
                bump()
            },
            onDismiss = { showNearbySheet = false },
            onChatClick = { playerId ->
                showNearbySheet = false
                nav.navigate(Routes.chatRoute(playerId))
            },
            onUserClick = { playerId ->
                showNearbySheet = false
                nav.navigate(Routes.userRoute(playerId))
            },
        )
    }

    // 3. Communities & Clubs Sheet
    if (showClubsSheet) {
        CommunitiesBottomSheet(
            clubs = allClubs,
            onDismiss = { showClubsSheet = false },
            onToggleJoin = { clubId ->
                val joined = store.toggleClubJoin(clubId)
                bump()
                val msg = if (joined) "Joined squad! 🎉" else "Left squad"
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            },
        )
    }

    // 4. Events Sheet
    if (showEventsSheet) {
        EventsBottomSheet(
            events = allEvents,
            onDismiss = { showEventsSheet = false },
            onEventClick = { targetRoute ->
                showEventsSheet = false
                nav.navigate(targetRoute)
            },
        )
    }

    // 5. Comments Sheet
    activeCommentPost?.let { post ->
        CommentsBottomSheet(
            post = post,
            onDismiss = { activeCommentPost = null },
            onAddComment = { text ->
                store.addComment(
                    momentId = post.id,
                    authorName = profile?.name ?: "You",
                    text = text,
                    avatarRes = R.drawable.vb_discover_avatar_01,
                )
                bump()
            },
        )
    }

    // 6. Gift Pack Sheet removed
}

// ─── Section A: Discover Header ───────────────────────────────────────────────

@Composable
private fun DiscoverHeaderSection(
    userName: String,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onCreateMoment: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
            .background(
                Brush.verticalGradient(
                    listOf(VyboraLavender.copy(alpha = 0.5f), Color.Transparent)
                )
            )
            .padding(horizontal = 20.dp, vertical = 10.dp),
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(Modifier.weight(1f)) {
                        Text(
                            "Discover",
                            color = VyboraTextPrimary,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    Text(
                        "Find your people, stories and spaces",
                        color = VyboraTextSecondary,
                        fontSize = 12.sp,
                    )
                }

                // Create Moment Quick Button
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(VyboraSurface)
                        .border(1.dp, VyboraBorder, RoundedCornerShape(14.dp))
                        .clickable(onClick = onCreateMoment)
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(R.drawable.dp_discover_post),
                            contentDescription = "Post Moment",
                            modifier = Modifier.size(16.dp),
                            contentScale = ContentScale.Fit,
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            "Post",
                            color = VyboraPurple,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            // Search Bar
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(VyboraSurface, RoundedCornerShape(16.dp))
                    .border(1.dp, VyboraBorder, RoundedCornerShape(16.dp))
                    .padding(horizontal = 14.dp, vertical = 10.dp),
            ) {
                Image(
                    painter = painterResource(R.drawable.dp_discover_search),
                    contentDescription = "Search",
                    modifier = Modifier.size(16.dp),
                    contentScale = ContentScale.Fit,
                )
                Spacer(Modifier.width(10.dp))
                BasicTextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    singleLine = true,
                    textStyle = TextStyle(
                        color = VyboraTextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                    ),
                    cursorBrush = SolidColor(VyboraCyan),
                    modifier = Modifier.weight(1f),
                    decorationBox = { innerTextField ->
                        if (searchQuery.isEmpty()) {
                            Text(
                                "Search moments, games, rooms or tags…",
                                color = VyboraTextSecondary.copy(alpha = 0.8f),
                                fontSize = 13.sp,
                            )
                        }
                        innerTextField()
                    },
                )
                if (searchQuery.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(VyboraLavender)
                            .clickable { onSearchQueryChange("") },
                        contentAlignment = Alignment.Center,
                    ) {
                        Image(
                            painter = painterResource(R.drawable.dp_discover_close),
                            contentDescription = "Clear",
                            modifier = Modifier.size(10.dp),
                            contentScale = ContentScale.Fit,
                        )
                    }
                }
            }
        }
    }
}

// ─── Section B: Featured Moments Hero ─────────────────────────────────────────

@Composable
private fun DiscoverMomentsHero(
    latestPost: MomentPost?,
    onCreateMoment: () -> Unit,
    onExploreMoments: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .shadow(4.dp, RoundedCornerShape(20.dp), ambientColor = Color(0x108258EE), spotColor = Color(0x158258EE))
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.linearGradient(
                    listOf(VyboraSurface, VyboraLavender.copy(alpha = 0.4f))
                )
            )
            .border(1.dp, VyboraBorder, RoundedCornerShape(20.dp))
            .clickable(onClick = onExploreMoments)
            .padding(16.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Artwork
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(VyboraLavender),
                contentAlignment = Alignment.Center,
            ) {
                Image(
                    painter = painterResource(R.drawable.dp_discover_moments),
                    contentDescription = "Moments",
                    modifier = Modifier.size(54.dp),
                    contentScale = ContentScale.Fit,
                )
            }

            Spacer(Modifier.width(14.dp))

            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "Community Moments",
                        color = VyboraTextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(Modifier.width(6.dp))
                    Image(
                        painter = painterResource(R.drawable.vb_discover_badge_new),
                        contentDescription = "New",
                        modifier = Modifier.size(16.dp),
                    )
                }

                Spacer(Modifier.height(3.dp))

                Text(
                    latestPost?.let { "@${it.authorName}: \"${it.text}\"" }
                        ?: "Share your gaming highlights, voice clips & stories with friends",
                    color = VyboraTextSecondary,
                    fontSize = 12.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 16.sp,
                )

                Spacer(Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(Brush.horizontalGradient(listOf(VyboraPurple, VyboraCyan)))
                            .clickable(onClick = onCreateMoment)
                            .padding(horizontal = 12.dp, vertical = 5.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            "+ Post Moment",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }

                    Spacer(Modifier.width(10.dp))

                    Text(
                        "Explore Feed →",
                        color = VyboraPurple,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    }
}

// ─── Section C: Community Shortcuts Grid ──────────────────────────────────────

@Composable
private fun CommunityShortcutsGrid(
    onFamiliesClick: () -> Unit,
    onNearbyClick: () -> Unit,
    onClubsClick: () -> Unit,
    onEventsClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // 1. Families Tile
            ShortcutTile(
                modifier = Modifier.weight(1f),
                title = "Families",
                subtitle = "Clans & Ranks",
                badgeText = "Rank 12",
                iconRes = R.drawable.dp_discover_families,
                gradient = listOf(Color(0xFFFFF0F5), VyboraSurface),
                badgeColor = Color(0xFFFF597A),
                onClick = onFamiliesClick,
            )

            // 2. Nearby Tile
            ShortcutTile(
                modifier = Modifier.weight(1f),
                title = "Nearby",
                subtitle = "Local Players",
                badgeText = "0.8 km",
                iconRes = R.drawable.dp_discover_nearby,
                gradient = listOf(Color(0xFFEBFBF5), VyboraSurface),
                badgeColor = MintGreen,
                onClick = onNearbyClick,
            )
        }

        Spacer(Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // 3. Clubs & Communities Tile
            ShortcutTile(
                modifier = Modifier.weight(1f),
                title = "Clubs & Spaces",
                subtitle = "Gaming Squads",
                badgeText = "4 Active",
                iconRes = R.drawable.dp_discover_clubs_spaces,
                gradient = listOf(Color(0xFFF0F5FF), VyboraSurface),
                badgeColor = VyboraCyan,
                onClick = onClubsClick,
            )

            // 4. Live Events Tile
            ShortcutTile(
                modifier = Modifier.weight(1f),
                title = "Live Events",
                subtitle = "Tournaments",
                badgeText = "LIVE NOW",
                iconRes = R.drawable.dp_discover_live_events,
                gradient = listOf(Color(0xFFFFF7EB), VyboraSurface),
                badgeColor = LiveRed,
                onClick = onEventsClick,
            )
        }
    }
}

@Composable
private fun ShortcutTile(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String,
    badgeText: String,
    iconRes: Int,
    gradient: List<Color>,
    badgeColor: Color,
    onClick: () -> Unit,
) {
    Box(
        modifier = modifier
            .shadow(2.dp, RoundedCornerShape(18.dp), ambientColor = Color(0x0A000000), spotColor = Color(0x0A000000))
            .clip(RoundedCornerShape(18.dp))
            .background(Brush.verticalGradient(gradient))
            .border(1.dp, VyboraBorder, RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
            .padding(14.dp),
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(VyboraSurface)
                        .padding(4.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Image(
                        painter = painterResource(iconRes),
                        contentDescription = title,
                        modifier = Modifier.fillMaxSize(),
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(badgeColor.copy(alpha = 0.15f))
                        .padding(horizontal = 6.dp, vertical = 3.dp),
                ) {
                    Text(
                        badgeText,
                        color = badgeColor,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }

            Spacer(Modifier.height(10.dp))

            Text(
                title,
                color = VyboraTextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
            )
            Text(
                subtitle,
                color = VyboraTextSecondary,
                fontSize = 11.sp,
            )
        }
    }
}

// ─── Section D: Trending Discovery Hashtag Chips ─────────────────────────────

@Composable
private fun TrendingDiscoveryBar(
    tags: List<String>,
    selectedTag: String,
    onSelectTag: (String) -> Unit,
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                painter = painterResource(R.drawable.dp_discover_trending),
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                contentScale = ContentScale.Fit,
            )
            Spacer(Modifier.width(6.dp))
            Text(
                "Trending Topics",
                color = VyboraTextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
            )
        }

        Spacer(Modifier.height(8.dp))

        LazyRow(
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(tags) { tag ->
                val isSelected = selectedTag.equals(tag, ignoreCase = true)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            if (isSelected) Brush.horizontalGradient(listOf(VyboraPurple, VyboraCyan))
                            else SolidColor(VyboraSurface)
                        )
                        .border(
                            1.dp,
                            if (isSelected) Color.Transparent else VyboraBorder,
                            RoundedCornerShape(16.dp),
                        )
                        .clickable { onSelectTag(tag) }
                        .padding(horizontal = 14.dp, vertical = 7.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        if (tag == "All") "✦ All Topics" else "#$tag",
                        color = if (isSelected) Color.White else VyboraTextPrimary,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    )
                }
            }
        }
    }
}

// ─── Section E: Social Activity / Live Updates ────────────────────────────────

@Composable
private fun LiveUpdatesSection(
    events: List<LiveActivityEvent>,
    onEventClick: (String) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Image(
                painter = painterResource(R.drawable.dp_discover_notification),
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                contentScale = ContentScale.Fit,
            )
            Spacer(Modifier.width(6.dp))
            Text(
                "Live Activity",
                color = VyboraTextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f),
            )
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(MintGreen.copy(alpha = 0.15f))
                    .padding(horizontal = 6.dp, vertical = 2.dp),
            ) {
                Text(
                    "● Real-time",
                    color = MintGreen,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(VyboraSurface)
                .border(1.dp, VyboraBorder, RoundedCornerShape(18.dp))
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            events.take(4).forEach { event ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onEventClick(event.targetRoute) }
                        .padding(vertical = 2.dp),
                ) {
                    Image(
                        painter = painterResource(event.whoAvatarRes),
                        contentDescription = event.who,
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop,
                    )
                    Spacer(Modifier.width(10.dp))
                    Column(Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                event.who,
                                color = VyboraTextPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                            )
                            Spacer(Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(VyboraLavender)
                                    .padding(horizontal = 6.dp, vertical = 1.dp),
                            ) {
                                Text(
                                    "#${event.tag}",
                                    color = VyboraPurple,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                )
                            }
                        }
                        Text(
                            event.action,
                            color = VyboraTextSecondary,
                            fontSize = 11.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                    Text(
                        event.timeAgo,
                        color = VyboraTextSecondary,
                        fontSize = 10.sp,
                    )
                }
            }
        }
    }
}

// ─── Section F: Community Moment Post Card ────────────────────────────────────

@Composable
private fun MomentPostCard(
    post: MomentPost,
    onLikeClick: () -> Unit,
    onCommentClick: () -> Unit,
    onShareClick: () -> Unit,
    onUserClick: () -> Unit,
) {
    var menuOpen by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .shadow(2.dp, RoundedCornerShape(20.dp), ambientColor = Color(0x08000000), spotColor = Color(0x0A000000))
            .clip(RoundedCornerShape(20.dp))
            .background(VyboraSurface)
            .border(1.dp, VyboraBorder, RoundedCornerShape(20.dp))
            .padding(16.dp),
    ) {
        Column {
            // Author row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Image(
                    painter = painterResource(post.authorAvatarRes),
                    contentDescription = post.authorName,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .clickable(onClick = onUserClick),
                    contentScale = ContentScale.Crop,
                )
                Spacer(Modifier.width(10.dp))
                Column(Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            post.authorName,
                            color = VyboraTextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable(onClick = onUserClick),
                        )
                        if (post.isVerified) {
                            Spacer(Modifier.width(4.dp))
                            Image(
                                painter = painterResource(R.drawable.vb_discover_badge_verified),
                                contentDescription = "Verified",
                                modifier = Modifier.size(14.dp),
                            )
                        }
                        if (post.isFamilyMember) {
                            Spacer(Modifier.width(4.dp))
                            Image(
                                painter = painterResource(R.drawable.vb_discover_badge_family),
                                contentDescription = "Family",
                                modifier = Modifier.size(14.dp),
                            )
                        }
                    }
                    Text(
                        "${post.tag} · ${timeAgoText(post.timestamp)}",
                        color = VyboraTextSecondary,
                        fontSize = 11.sp,
                    )
                }

                // Media highlight pill
                post.mediaBadge?.let { badge ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(VyboraCyanSoft)
                            .padding(horizontal = 8.dp, vertical = 3.dp),
                    ) {
                        Text(
                            badge,
                            color = VyboraCyan,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                    Spacer(Modifier.width(6.dp))
                }

                // More Menu
                Box {
                    Icon(
                        painter = painterResource(R.drawable.vb_chat_ic_more),
                        contentDescription = "Options",
                        tint = VyboraTextSecondary,
                        modifier = Modifier
                            .size(18.dp)
                            .clickable { menuOpen = true },
                    )
                    DropdownMenu(
                        expanded = menuOpen,
                        onDismissRequest = { menuOpen = false },
                        containerColor = VyboraSurface,
                    ) {
                        DropdownMenuItem(
                            text = { Text("Share Post", color = VyboraTextPrimary) },
                            onClick = { menuOpen = false; onShareClick() },
                        )
                        DropdownMenuItem(
                            text = { Text("Report Post", color = KtvRed) },
                            onClick = { menuOpen = false },
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            // Post content
            Text(
                post.text,
                color = VyboraTextPrimary,
                fontSize = 13.sp,
                lineHeight = 19.sp,
            )

            Spacer(Modifier.height(14.dp))

            // Bottom actions (Like, Comment, Share)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Like Button
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (post.isLiked) PinkGift.copy(alpha = 0.12f) else VyboraBg)
                        .clickable(onClick = onLikeClick)
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                ) {
                    Image(
                        painter = painterResource(R.drawable.dp_discover_like),
                        contentDescription = "Like",
                        modifier = Modifier.size(16.dp),
                        contentScale = ContentScale.Fit,
                        alpha = if (post.isLiked) 1f else 0.5f,
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        "${post.likesCount}",
                        color = if (post.isLiked) PinkGift else VyboraTextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }

                Spacer(Modifier.width(10.dp))

                // Comment Button
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(VyboraBg)
                        .clickable(onClick = onCommentClick)
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                ) {
                    Image(
                        painter = painterResource(R.drawable.dp_discover_comment),
                        contentDescription = "Comment",
                        modifier = Modifier.size(16.dp),
                        contentScale = ContentScale.Fit,
                        alpha = 0.65f,
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        "${post.commentsCount}",
                        color = VyboraTextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }

                Spacer(Modifier.weight(1f))

                // Share Button
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(VyboraBg)
                        .clickable(onClick = onShareClick)
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(R.drawable.dp_discover_share),
                            contentDescription = "Share",
                            modifier = Modifier.size(14.dp),
                            contentScale = ContentScale.Fit,
                        )
                        Spacer(Modifier.width(5.dp))
                        Text(
                            "Share",
                            color = VyboraPurple,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }
        }
    }
}

// ─── Empty State ─────────────────────────────────────────────────────────────

@Composable
private fun EmptyMomentsView(
    query: String,
    selectedTag: String,
    onCreateMoment: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 40.dp, bottom = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(VyboraLavender),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(R.drawable.dp_discover_empty_feed),
                contentDescription = null,
                modifier = Modifier.size(56.dp),
                contentScale = ContentScale.Fit,
            )
        }
        Spacer(Modifier.height(14.dp))
        Text(
            if (query.isNotBlank()) "No moments found for \"$query\""
            else "No moments yet",
            color = VyboraTextPrimary,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
        )
        Spacer(Modifier.height(4.dp))
        Text(
            "Share your thoughts, match replays, or lounge vibes",
            color = VyboraTextSecondary,
            fontSize = 12.sp,
        )
        Spacer(Modifier.height(14.dp))
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(Brush.horizontalGradient(listOf(VyboraPurple, VyboraCyan)))
                .clickable(onClick = onCreateMoment)
                .padding(horizontal = 18.dp, vertical = 9.dp),
        ) {
            Text(
                "Create Moment ✦",
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

// ─── Modal 1: Create Moment Bottom Sheet ──────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreateMomentBottomSheet(
    currentUserName: String,
    currentUserId: String,
    onDismiss: () -> Unit,
    onPost: (String, String) -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var textInput by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Ludo") }
    val categories = listOf("Ludo", "Who's the Spy", "KTV", "Werewolf", "Casual Lounge")
    var selectedImages by remember { mutableStateOf<List<Uri>>(emptyList()) }
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = 5),
    ) { uris -> selectedImages = (selectedImages + uris).take(5) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = VyboraSurface,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 10.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    "Create Community Moment",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = VyboraTextPrimary,
                )
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(VyboraBg)
                        .clickable(onClick = onDismiss),
                    contentAlignment = Alignment.Center,
                ) {
                    Image(
                        painter = painterResource(R.drawable.dp_discover_close),
                        contentDescription = "Close",
                        modifier = Modifier.size(14.dp),
                        contentScale = ContentScale.Fit,
                    )
                }
            }

            Spacer(Modifier.height(14.dp))

            // Author info
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(R.drawable.vb_discover_avatar_01),
                    contentDescription = currentUserName,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape),
                )
                Spacer(Modifier.width(10.dp))
                Column {
                    Text(
                        currentUserName,
                        color = VyboraTextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        "Posting to Community Feed",
                        color = VyboraTextSecondary,
                        fontSize = 11.sp,
                    )
                }
            }

            Spacer(Modifier.height(14.dp))

            // Text input
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(VyboraBg)
                    .border(1.dp, VyboraBorder, RoundedCornerShape(16.dp))
                    .padding(14.dp),
            ) {
                BasicTextField(
                    value = textInput,
                    onValueChange = { textInput = it },
                    textStyle = TextStyle(
                        color = VyboraTextPrimary,
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                    ),
                    cursorBrush = SolidColor(VyboraCyan),
                    modifier = Modifier.fillMaxSize(),
                    decorationBox = { inner ->
                        if (textInput.isEmpty()) {
                            Text(
                                "What's happening in your gaming lounge or voice room? Share with the community…",
                                color = VyboraTextSecondary,
                                fontSize = 13.sp,
                            )
                        }
                        inner()
                    },
                )
            }

            Spacer(Modifier.height(14.dp))

            // Gallery / image picker button
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(VyboraBg)
                        .border(1.dp, VyboraBorder, RoundedCornerShape(10.dp))
                        .clickable {
                            imagePickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        }
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(R.drawable.dp_discover_gallery),
                            contentDescription = "Gallery",
                            modifier = Modifier.size(16.dp),
                            contentScale = ContentScale.Fit,
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            "Add Photos",
                            color = VyboraTextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                        )
                    }
                }
                Text(
                    text = if (selectedImages.isEmpty()) "Up to 5 images" else "${selectedImages.size}/5 selected",
                    color = VyboraTextSecondary,
                    fontSize = 11.sp,
                )
            }

            if (selectedImages.isNotEmpty()) {
                Spacer(Modifier.height(8.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(selectedImages) { uri ->
                        Box {
                            AsyncImage(
                                model = uri,
                                contentDescription = "Selected image",
                                modifier = Modifier
                                    .size(72.dp)
                                    .clip(RoundedCornerShape(12.dp)),
                                contentScale = ContentScale.Crop,
                            )
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .size(20.dp)
                                    .clip(CircleShape)
                                    .background(Color.Black.copy(alpha = 0.5f))
                                    .clickable { selectedImages = selectedImages - uri },
                                contentAlignment = Alignment.Center,
                            ) {
                                Image(
                                    painter = painterResource(R.drawable.dp_discover_close),
                                    contentDescription = "Remove",
                                    modifier = Modifier.size(10.dp),
                                    contentScale = ContentScale.Fit,
                                )
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(14.dp))

            // Tag selector
            Text(
                "Select Topic Tag",
                color = VyboraTextPrimary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.height(8.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(categories) { cat ->
                    val isSel = selectedCategory == cat
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSel) VyboraPurple else VyboraBg)
                            .clickable { selectedCategory = cat }
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                    ) {
                        Text(
                            "#$cat",
                            color = if (isSel) Color.White else VyboraTextPrimary,
                            fontSize = 11.sp,
                            fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                        )
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            // Submit Post Button
            val canSubmit = textInput.trim().isNotBlank() || selectedImages.isNotEmpty()
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        if (canSubmit) Brush.horizontalGradient(listOf(VyboraPurple, VyboraCyan))
                        else SolidColor(VyboraBorder)
                    )
                    .clickable(enabled = canSubmit) {
                        onPost(textInput.trim(), selectedCategory)
                    },
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    "Publish Moment ✦",
                    color = if (canSubmit) Color.White else VyboraTextSecondary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                )
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

// ─── Modal 2: Nearby Players Bottom Sheet ─────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NearbyPlayersBottomSheet(
    players: List<NearbyPlayer>,
    isVisibleToOthers: Boolean,
    onToggleVisibility: (Boolean) -> Unit,
    onDismiss: () -> Unit,
    onChatClick: (String) -> Unit,
    onUserClick: (String) -> Unit,
) {
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION,
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { granted ->
        hasLocationPermission = granted
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = VyboraSurface,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 10.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(R.drawable.vb_discover_nearby),
                        contentDescription = "Nearby",
                        modifier = Modifier.size(26.dp),
                    )
                    Spacer(Modifier.width(8.dp))
                    Column {
                        Text(
                            "Nearby Players",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = VyboraTextPrimary,
                        )
                        Text(
                            "Approximate distance within your city lounge",
                            fontSize = 11.sp,
                            color = VyboraTextSecondary,
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(VyboraBg)
                        .clickable(onClick = onDismiss),
                    contentAlignment = Alignment.Center,
                ) {
                    Image(
                        painter = painterResource(R.drawable.dp_discover_close),
                        contentDescription = "Close",
                        modifier = Modifier.size(14.dp),
                        contentScale = ContentScale.Fit,
                    )
                }
            }

            Spacer(Modifier.height(14.dp))

            // Privacy Switch Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(VyboraBg, RoundedCornerShape(14.dp))
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(Modifier.weight(1f)) {
                    Text(
                        "Visible in Nearby Lounge",
                        color = VyboraTextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(
                        "Allows nearby gamers to see your city profile",
                        color = VyboraTextSecondary,
                        fontSize = 10.sp,
                    )
                }
                Switch(
                    checked = isVisibleToOthers,
                    onCheckedChange = onToggleVisibility,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = VyboraCyan,
                    ),
                )
            }

            Spacer(Modifier.height(14.dp))

            if (!hasLocationPermission) {
                // Permission Request Card
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(VyboraLavender.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                        .border(1.dp, VyboraBorder, RoundedCornerShape(16.dp))
                        .padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Image(
                        painter = painterResource(R.drawable.vb_discover_location_off),
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                    )
                    Spacer(Modifier.height(10.dp))
                    Text(
                        "Location Access Required",
                        color = VyboraTextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Grant approximate location access to find gamers and voice rooms in your vicinity. We never share your exact GPS coordinates.",
                        color = VyboraTextSecondary,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 16.sp,
                    )
                    Spacer(Modifier.height(14.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Brush.horizontalGradient(listOf(VyboraPurple, VyboraCyan)))
                            .clickable {
                                permissionLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
                            }
                            .padding(horizontal = 20.dp, vertical = 10.dp),
                    ) {
                        Text(
                            "Enable Location ✦",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            } else {
                // Nearby list
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(players, key = { it.id }) { player ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(VyboraBg, RoundedCornerShape(14.dp))
                                .clickable { onUserClick(player.id) }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Image(
                                painter = painterResource(player.avatarRes),
                                contentDescription = player.name,
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop,
                            )
                            Spacer(Modifier.width(10.dp))
                            Column(Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        player.name,
                                        color = VyboraTextPrimary,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                    )
                                    Spacer(Modifier.width(6.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(MintGreen.copy(alpha = 0.15f))
                                            .padding(horizontal = 5.dp, vertical = 1.dp),
                                    ) {
                                        Text(
                                            "${player.distanceKm} km",
                                            color = MintGreen,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                        )
                                    }
                                }
                                Text(
                                    "Favorite: ${player.favoriteGame} · Lv ${player.level}",
                                    color = VyboraTextSecondary,
                                    fontSize = 11.sp,
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Brush.horizontalGradient(listOf(VyboraPurple, VyboraCyan)))
                                    .clickable { onChatClick(player.id) }
                                    .padding(horizontal = 12.dp, vertical = 6.dp),
                            ) {
                                Text(
                                    "Chat",
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                )
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

// ─── Modal 3: Communities & Clubs Bottom Sheet ────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CommunitiesBottomSheet(
    clubs: List<CommunityClub>,
    onDismiss: () -> Unit,
    onToggleJoin: (String) -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var selectedCategory by remember { mutableStateOf("All") }
    val categories = listOf("All", "Gaming", "Music", "Social")

    val filteredClubs = remember(clubs, selectedCategory) {
        if (selectedCategory == "All") clubs
        else clubs.filter { it.category.equals(selectedCategory, ignoreCase = true) }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = VyboraSurface,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 10.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(R.drawable.vb_discover_clubs),
                        contentDescription = "Clubs",
                        modifier = Modifier.size(26.dp),
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Community Clubs & Spaces",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = VyboraTextPrimary,
                    )
                }
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(VyboraBg)
                        .clickable(onClick = onDismiss),
                    contentAlignment = Alignment.Center,
                ) {
                    Image(
                        painter = painterResource(R.drawable.dp_discover_close),
                        contentDescription = "Close",
                        modifier = Modifier.size(14.dp),
                        contentScale = ContentScale.Fit,
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            // Category Bar
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(categories) { cat ->
                    val isSel = selectedCategory == cat
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSel) VyboraPurple else VyboraBg)
                            .clickable { selectedCategory = cat }
                            .padding(horizontal = 14.dp, vertical = 6.dp),
                    ) {
                        Text(
                            cat,
                            color = if (isSel) Color.White else VyboraTextPrimary,
                            fontSize = 12.sp,
                            fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                        )
                    }
                }
            }

            Spacer(Modifier.height(14.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(320.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                items(filteredClubs, key = { it.id }) { club ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(VyboraBg, RoundedCornerShape(16.dp))
                            .border(1.dp, VyboraBorder, RoundedCornerShape(16.dp))
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Image(
                            painter = painterResource(club.iconRes),
                            contentDescription = club.name,
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(VyboraSurface)
                                .padding(4.dp),
                        )
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    club.name,
                                    color = VyboraTextPrimary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                )
                                Spacer(Modifier.width(6.dp))
                                Image(
                                    painter = painterResource(club.badgeRes),
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp),
                                )
                            }
                            Text(
                                club.description,
                                color = VyboraTextSecondary,
                                fontSize = 11.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                            Text(
                                "${club.membersCount} members · ${club.activityTag}",
                                color = VyboraPurple,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                            )
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    if (club.isJoined) SolidColor(VyboraLavender)
                                    else Brush.horizontalGradient(listOf(VyboraPurple, VyboraCyan))
                                )
                                .clickable { onToggleJoin(club.id) }
                                .padding(horizontal = 12.dp, vertical = 7.dp),
                        ) {
                            Text(
                                if (club.isJoined) "Joined ✓" else "Join +",
                                color = if (club.isJoined) VyboraPurple else Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

// ─── Modal 4: Live Events Bottom Sheet ────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EventsBottomSheet(
    events: List<DiscoverEvent>,
    onDismiss: () -> Unit,
    onEventClick: (String) -> Unit,
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
                .padding(horizontal = 20.dp, vertical = 10.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(R.drawable.vb_discover_events),
                        contentDescription = "Events",
                        modifier = Modifier.size(26.dp),
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Live Events & Stages",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = VyboraTextPrimary,
                    )
                }
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(VyboraBg)
                        .clickable(onClick = onDismiss),
                    contentAlignment = Alignment.Center,
                ) {
                    Image(
                        painter = painterResource(R.drawable.dp_discover_close),
                        contentDescription = "Close",
                        modifier = Modifier.size(14.dp),
                        contentScale = ContentScale.Fit,
                    )
                }
            }

            Spacer(Modifier.height(14.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                items(events, key = { it.id }) { ev ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(VyboraBg, RoundedCornerShape(16.dp))
                            .border(1.dp, VyboraBorder, RoundedCornerShape(16.dp))
                            .clickable { onEventClick(ev.targetRoute) }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Image(
                            painter = painterResource(ev.iconRes),
                            contentDescription = ev.title,
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(VyboraSurface)
                                .padding(4.dp),
                        )
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    ev.title,
                                    color = VyboraTextPrimary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                )
                                if (ev.isLive) {
                                    Spacer(Modifier.width(6.dp))
                                    Image(
                                        painter = painterResource(R.drawable.vb_discover_badge_live),
                                        contentDescription = "Live",
                                        modifier = Modifier.size(14.dp),
                                    )
                                }
                            }
                            Text(
                                ev.subtitle,
                                color = VyboraTextSecondary,
                                fontSize = 11.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    ev.schedule,
                                    color = if (ev.isLive) LiveRed else VyboraPurple,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                )
                                ev.prizePool?.let { prize ->
                                    Spacer(Modifier.width(8.dp))
                                    Text(
                                        "Prize: $prize",
                                        color = Gold,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                    )
                                }
                            }
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(Brush.horizontalGradient(listOf(VyboraPurple, VyboraCyan)))
                                .clickable { onEventClick(ev.targetRoute) }
                                .padding(horizontal = 12.dp, vertical = 7.dp),
                        ) {
                            Text(
                                "Enter →",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

// ─── Modal 5: Comments Bottom Sheet ───────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CommentsBottomSheet(
    post: MomentPost,
    onDismiss: () -> Unit,
    onAddComment: (String) -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var commentInput by remember { mutableStateOf("") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = VyboraSurface,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 10.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    "Comments (${post.comments.size})",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = VyboraTextPrimary,
                )
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(VyboraBg)
                        .clickable(onClick = onDismiss),
                    contentAlignment = Alignment.Center,
                ) {
                    Image(
                        painter = painterResource(R.drawable.dp_discover_close),
                        contentDescription = "Close",
                        modifier = Modifier.size(14.dp),
                        contentScale = ContentScale.Fit,
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            // Comments list
            if (post.comments.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        "No comments yet. Start the conversation!",
                        color = VyboraTextSecondary,
                        fontSize = 13.sp,
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    items(post.comments, key = { it.id }) { comment ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(VyboraBg, RoundedCornerShape(12.dp))
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Image(
                                painter = painterResource(comment.authorAvatarRes),
                                contentDescription = comment.authorName,
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop,
                            )
                            Spacer(Modifier.width(10.dp))
                            Column(Modifier.weight(1f)) {
                                Text(
                                    comment.authorName,
                                    color = VyboraTextPrimary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                )
                                Text(
                                    comment.text,
                                    color = VyboraTextPrimary,
                                    fontSize = 12.sp,
                                )
                            }
                            Text(
                                timeAgoText(comment.timestamp),
                                color = VyboraTextSecondary,
                                fontSize = 10.sp,
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            // Add comment input
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(VyboraBg, RoundedCornerShape(22.dp))
                    .border(1.dp, VyboraBorder, RoundedCornerShape(22.dp))
                    .padding(horizontal = 14.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                BasicTextField(
                    value = commentInput,
                    onValueChange = { commentInput = it },
                    singleLine = true,
                    textStyle = TextStyle(color = VyboraTextPrimary, fontSize = 13.sp),
                    cursorBrush = SolidColor(VyboraCyan),
                    modifier = Modifier.weight(1f),
                    decorationBox = { inner ->
                        if (commentInput.isEmpty()) {
                            Text("Write a nice comment…", color = VyboraTextSecondary, fontSize = 13.sp)
                        }
                        inner()
                    },
                )
                val canSend = commentInput.trim().isNotBlank()
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(
                            if (canSend) VyboraPurple else Color.Transparent
                        )
                        .clickable(enabled = canSend) {
                            onAddComment(commentInput.trim())
                            commentInput = ""
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        painter = painterResource(R.drawable.vb_chat_ic_send),
                        contentDescription = "Send",
                        tint = if (canSend) Color.White else VyboraTextSecondary,
                        modifier = Modifier.size(16.dp),
                    )
                }
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

// ─── Time Ago Formatter ───────────────────────────────────────────────────────

private fun timeAgoText(ts: Long): String {
    if (ts == 0L) return "now"
    val diff = System.currentTimeMillis() - ts
    val min = diff / 60_000
    if (min < 1) return "just now"
    if (min < 60) return "${min}m ago"
    val hr = min / 60
    if (hr < 24) return "${hr}h ago"
    val day = hr / 24
    if (day < 7) return "${day}d ago"
    return "${day / 7}w ago"
}
