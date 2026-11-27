package com.sundram.weplay.ui.screens

import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.sundram.weplay.ui.components.AuroraEmptyStateCard
import com.sundram.weplay.ui.components.AuroraGlassCard
import com.sundram.weplay.ui.components.AuroraScreenCanvas
import com.sundram.weplay.ui.components.bounceClick
import com.sundram.weplay.ui.components.navigateSafe
import com.sundram.weplay.ui.nav.Routes
import com.sundram.weplay.ui.theme.AuroraBorder
import com.sundram.weplay.ui.theme.AuroraElevated
import com.sundram.weplay.ui.theme.AuroraSuccess
import com.sundram.weplay.ui.theme.AuroraSurface
import com.sundram.weplay.ui.theme.CloudWhite
import com.sundram.weplay.ui.theme.CrystalBlue
import com.sundram.weplay.ui.theme.DreamLavender
import com.sundram.weplay.ui.theme.MysticPurple
import com.sundram.weplay.ui.theme.RoyalGold
import com.sundram.weplay.ui.theme.RoyalViolet

private data class InterestSpaceItem(
    val id: String,
    val title: String,
    val category: String,
    val summary: String,
    val emoji: String,
    val linkedRoomId: String,
    val badge: String,
)

private val SpaceCatalog = listOf(
    InterestSpaceItem(
        id = "club_melody",
        title = "Midnight Acoustic & KTV",
        category = "Music",
        summary = "Chill live vocals, acoustic guitars, and evening requests.",
        emoji = "🎤",
        linkedRoomId = "r1",
        badge = "Live Lounge",
    ),
    InterestSpaceItem(
        id = "club_deduction",
        title = "Spy & Deduction Society",
        category = "Games",
        summary = "Organize Who's the Spy rounds and sharpen clue strategy.",
        emoji = "🕵️",
        linkedRoomId = "r2",
        badge = "Daily Rounds",
    ),
    InterestSpaceItem(
        id = "club_board",
        title = "Royal Ludo Tacticians",
        category = "Games",
        summary = "Friendly dice tournaments and tabletop match nights.",
        emoji = "🎲",
        linkedRoomId = "r4",
        badge = "Board Club",
    ),
    InterestSpaceItem(
        id = "club_chill",
        title = "Aurora Starlight Hangout",
        category = "Social",
        summary = "Casual voice chats, study check-ins, and new friends.",
        emoji = "🌌",
        linkedRoomId = "r3",
        badge = "Welcoming",
    ),
)

private val SpaceCategories = listOf("All", "Music", "Games", "Social")

@Composable
fun ClubsSpacesScreen(nav: NavHostController) {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("paradise_clubs", Context.MODE_PRIVATE) }
    var selectedCategory by remember { mutableStateOf("All") }
    var joinedIds by remember {
        mutableStateOf(prefs.getStringSet("joined_clubs", emptySet()) ?: emptySet())
    }

    val visibleSpaces = remember(selectedCategory) {
        if (selectedCategory == "All") SpaceCatalog
        else SpaceCatalog.filter { it.category == selectedCategory }
    }

    fun toggleJoin(id: String) {
        val next = if (id in joinedIds) joinedIds - id else joinedIds + id
        joinedIds = next
        prefs.edit().putStringSet("joined_clubs", next).apply()
    }

    AuroraScreenCanvas(
        title = "Clubs & Spaces",
        subtitle = "Interest circles & community guilds",
        onBack = { nav.popBackStack() },
        scrollable = true,
    ) {
        // Family Hub Promotion Banner
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = AuroraSurface,
            border = BorderStroke(1.dp, RoyalGold.copy(alpha = 0.45f)),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.horizontalGradient(
                            listOf(RoyalViolet.copy(alpha = 0.85f), Color(0xFF28175A))
                        )
                    )
                    .padding(16.dp),
            ) {
                Column {
                    Text(
                        text = "Looking for a permanent guild?",
                        color = CloudWhite,
                        fontSize = 15.5.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "Join or create a Family for weekly quests, emblems, and private voice lounges.",
                        color = DreamLavender.copy(alpha = 0.9f),
                        fontSize = 12.sp,
                        lineHeight = 17.sp,
                    )
                    Spacer(Modifier.height(10.dp))
                    Button(
                        onClick = { nav.navigateSafe(Routes.FAMILY) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = RoyalGold,
                            contentColor = Color(0xFF1A0F3B),
                        ),
                        shape = RoundedCornerShape(12.dp),
                    ) {
                        Text("Open Families Hub ›", fontWeight = FontWeight.ExtraBold, fontSize = 12.sp)
                    }
                }
            }
        }

        Spacer(Modifier.height(14.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            items(SpaceCategories, key = { it }) { cat ->
                val active = cat == selectedCategory
                Box(
                    modifier = Modifier
                        .defaultMinSize(minHeight = 36.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (active) MysticPurple else AuroraSurface)
                        .border(1.dp, if (active) CrystalBlue.copy(alpha = 0.55f) else AuroraBorder, RoundedCornerShape(16.dp))
                        .bounceClick { selectedCategory = cat }
                        .padding(horizontal = 14.dp, vertical = 7.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = cat,
                        color = if (active) CloudWhite else DreamLavender,
                        fontSize = 12.sp,
                        fontWeight = if (active) FontWeight.Bold else FontWeight.Medium,
                    )
                }
            }
        }

        Spacer(Modifier.height(14.dp))

        if (visibleSpaces.isEmpty()) {
            AuroraEmptyStateCard(
                title = "No spaces in this category",
                message = "Try another interest tag or open the Families Hub.",
                emoji = "✨",
                actionLabel = "Reset Filter",
                onAction = { selectedCategory = "All" },
            )
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                visibleSpaces.forEach { space ->
                    val joined = space.id in joinedIds
                    AuroraGlassCard {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .defaultMinSize(minWidth = 46.dp, minHeight = 46.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(AuroraElevated)
                                    .border(1.dp, AuroraBorder, RoundedCornerShape(14.dp)),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(space.emoji, fontSize = 22.sp)
                            }
                            Spacer(Modifier.width(12.dp))
                            Column(Modifier.weight(1f)) {
                                Text(
                                    text = space.title,
                                    color = CloudWhite,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                                Spacer(Modifier.height(2.dp))
                                Text(
                                    text = "${space.category} · ${space.badge}",
                                    color = CrystalBlue,
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.SemiBold,
                                )
                            }
                        }
                        Spacer(Modifier.height(10.dp))
                        Text(
                            text = space.summary,
                            color = DreamLavender.copy(alpha = 0.88f),
                            fontSize = 12.5.sp,
                            lineHeight = 18.sp,
                        )
                        Spacer(Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            OutlinedButton(
                                onClick = { toggleJoin(space.id) },
                                border = BorderStroke(
                                    1.dp,
                                    if (joined) AuroraSuccess.copy(alpha = 0.65f) else AuroraBorder,
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .defaultMinSize(minHeight = 42.dp),
                            ) {
                                Text(
                                    text = if (joined) "Joined ✓" else "Join Space",
                                    color = if (joined) AuroraSuccess else CloudWhite,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                )
                            }
                            Button(
                                onClick = { nav.navigateSafe(Routes.roomRoute(space.linkedRoomId)) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = RoyalViolet,
                                    contentColor = CloudWhite,
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .defaultMinSize(minHeight = 42.dp),
                            ) {
                                Text("Enter Room ›", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}
