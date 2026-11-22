package com.sundram.weplay.ui.screens

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import com.sundram.weplay.ui.components.InitialsAvatar
import com.sundram.weplay.ui.components.SectionHeader
import com.sundram.weplay.ui.components.bounceClick
import com.sundram.weplay.ui.nav.Routes
import com.sundram.weplay.ui.theme.AuroraBorder
import com.sundram.weplay.ui.theme.AuroraDiscoverBackground
import com.sundram.weplay.ui.theme.AuroraGradients
import com.sundram.weplay.ui.theme.AuroraSurface
import com.sundram.weplay.ui.theme.AuroraTextPrimary
import com.sundram.weplay.ui.theme.AuroraTextSecondary
import com.sundram.weplay.ui.theme.AuroraTurquoise
import com.sundram.weplay.ui.theme.CelestialViolet
import com.sundram.weplay.ui.theme.ChampagneGold
import com.sundram.weplay.ui.theme.DeepMidnight
import com.sundram.weplay.ui.theme.DreamLavender
import com.sundram.weplay.ui.theme.PearlWhite
import com.sundram.weplay.ui.theme.SoftPeach
import com.sundram.weplay.ui.theme.ChipBg
import com.sundram.weplay.ui.theme.CyanDeep
import com.sundram.weplay.ui.theme.OrangeAccent
import com.sundram.weplay.ui.theme.PageWhite
import com.sundram.weplay.ui.theme.PinkGift
import com.sundram.weplay.ui.theme.Stroke
import com.sundram.weplay.ui.theme.TagBlue
import com.sundram.weplay.ui.theme.TagBlueBg
import com.sundram.weplay.ui.theme.TagGreen
import com.sundram.weplay.ui.theme.TagGreenBg
import com.sundram.weplay.ui.theme.TagOrange
import com.sundram.weplay.ui.theme.TagOrangeBg
import com.sundram.weplay.ui.theme.TextBody
import com.sundram.weplay.ui.theme.TextMain
import com.sundram.weplay.ui.theme.TextMuted

private data class ExploreCard(
    val label: String,
    val icon: Int,
    val brush: Brush,
    val route: String? = null,
)

private val exploreCards = listOf(
    ExploreCard("Games", R.drawable.ic_nav_games, AuroraGradients.sunsetFantasy, Routes.GAMES),
    ExploreCard("Rooms", R.drawable.ic_nav_room, AuroraGradients.dreamySky, Routes.ROOMS),
    ExploreCard("Nearby", R.drawable.ic_users, Brush.linearGradient(listOf(ChampagneGold, SoftPeach)), null),
)

@Composable
fun DiscoverScreen(nav: NavHostController, pad: PaddingValues) {
    val repository = remember { LocalContentRepository() }
    var refresh by remember { mutableStateOf(0) }
    var state by remember { mutableStateOf<UiState<DiscoverFeed>>(UiState.Loading) }

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

    AuroraDiscoverBackground(
        modifier = Modifier.fillMaxSize(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(bottom = pad.calculateBottomPadding()),
        ) {
            when (val current = state) {
                UiState.Loading -> LoadingState(Modifier.weight(1f))
                is UiState.Empty -> EmptyState(current.message, Modifier.weight(1f))
                is UiState.Error -> ErrorState(current.message, current.retryable, { refresh++ }, Modifier.weight(1f))
                is UiState.Success -> DiscoverContent(current.data, nav)
            }
        }
    }
}

@Composable
private fun DiscoverContent(feed: DiscoverFeed, nav: NavHostController) {
    val events = feed.events
    val posts = feed.posts
    val trending = feed.trending

    LazyColumn(
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize(),
    ) {
        item {
            Column(modifier = Modifier.padding(top = 4.dp, bottom = 4.dp)) {
                Text(
                    text = "Discover",
                    color = DeepMidnight,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.ExtraBold,
                )
                Text(
                    text = "Explore community moments and live updates",
                    color = AuroraTextSecondary,
                    fontSize = 12.sp,
                )
            }
            Spacer(Modifier.height(10.dp))
            MomentsCard()
            Spacer(Modifier.height(12.dp))
            ExploreRow(nav = nav)
            Spacer(Modifier.height(14.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(trending, key = { it }) { tag ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(DreamLavender.copy(alpha = 0.25f))
                            .border(1.dp, DreamLavender.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                    ) {
                        Text(
                            text = "#$tag",
                            color = CelestialViolet,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }
            Spacer(Modifier.height(14.dp))
            SectionHeader("Live updates", "Today")
            Spacer(Modifier.height(4.dp))
        }

        items(events, key = { "${it.who}_${it.action}_${it.time}" }) { event ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(1.dp, RoundedCornerShape(16.dp))
                    .clip(RoundedCornerShape(16.dp))
                    .background(AuroraSurface)
                    .border(1.dp, AuroraBorder, RoundedCornerShape(16.dp))
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                InitialsAvatar(event.who, size = 40.dp)
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(event.who, color = DeepMidnight, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Text(event.action, color = AuroraTextSecondary, fontSize = 11.sp)
                }
                Text(event.time, color = AuroraTextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Medium)
            }
        }

        item {
            Spacer(Modifier.height(8.dp))
            SectionHeader("Moments", "Community")
            Spacer(Modifier.height(4.dp))
        }

        items(posts, key = { "${it.who}_${it.text.hashCode()}" }) { post ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(2.dp, RoundedCornerShape(18.dp))
                    .clip(RoundedCornerShape(18.dp))
                    .background(AuroraSurface)
                    .border(1.dp, AuroraBorder, RoundedCornerShape(18.dp))
                    .padding(14.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    InitialsAvatar(post.who, size = 34.dp)
                    Spacer(Modifier.width(10.dp))
                    Column {
                        Text(post.who, color = DeepMidnight, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Text("Active Now", color = AuroraTurquoise, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
                Spacer(Modifier.height(10.dp))
                Text(
                    text = post.text,
                    color = DeepMidnight.copy(alpha = 0.90f),
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                )
                Spacer(Modifier.height(12.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Reaction(R.drawable.ic_heart, "${post.likes}", Color(0xFFF477AC))
                    Spacer(Modifier.width(20.dp))
                    Reaction(R.drawable.ic_comment, "${post.comments}", AuroraTurquoise)
                }
            }
        }
    }
}

@Composable
private fun MomentsCard() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(18.dp))
            .clip(RoundedCornerShape(18.dp))
            .background(AuroraSurface)
            .border(1.dp, AuroraBorder, RoundedCornerShape(18.dp))
            .bounceClick { }
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(AuroraGradients.sunsetFantasy),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_star),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(18.dp),
            )
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Community Moments",
                color = DeepMidnight,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "Share memories & explore daily highlights",
                color = AuroraTextSecondary,
                fontSize = 11.sp,
            )
        }
        Icon(
            painter = painterResource(R.drawable.ic_chevron_right),
            contentDescription = null,
            tint = AuroraTextSecondary,
            modifier = Modifier.size(18.dp),
        )
    }
}

@Composable
private fun ExploreRow(nav: NavHostController) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        exploreCards.forEach { card ->
            Column(
                modifier = Modifier
                    .weight(1f)
                    .shadow(1.dp, RoundedCornerShape(18.dp))
                    .clip(RoundedCornerShape(18.dp))
                    .background(AuroraSurface)
                    .border(1.dp, AuroraBorder, RoundedCornerShape(18.dp))
                    .bounceClick {
                        if (card.route != null) {
                            nav.navigate(card.route)
                        }
                    }
                    .padding(vertical = 14.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(card.brush),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        painter = painterResource(card.icon),
                        contentDescription = card.label,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp),
                    )
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    text = card.label,
                    color = DeepMidnight,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

@Composable
private fun Reaction(icon: Int, count: String, tint: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            painter = painterResource(icon),
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(14.dp),
        )
        Spacer(Modifier.width(5.dp))
        Text(count, color = AuroraTextSecondary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
    }
}
