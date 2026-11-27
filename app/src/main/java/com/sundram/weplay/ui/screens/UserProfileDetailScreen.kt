package com.sundram.weplay.ui.screens

import android.widget.Toast
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.sundram.weplay.R
import com.sundram.weplay.data.DemoGifts
import com.sundram.weplay.data.FrameRepository
import com.sundram.weplay.data.FrameTier
import com.sundram.weplay.data.LeaderboardCategory
import com.sundram.weplay.data.LeaderboardPeriod
import com.sundram.weplay.data.MessagingStore
import com.sundram.weplay.data.ProfileStore
import com.sundram.weplay.ui.components.AuroraConfirmDialog
import com.sundram.weplay.ui.components.AuroraDetailScaffold
import com.sundram.weplay.ui.components.AuroraEmptyStateCard
import com.sundram.weplay.ui.components.AuroraGlassCard
import com.sundram.weplay.ui.components.AvatarWithFrame
import com.sundram.weplay.ui.components.bounceClick
import com.sundram.weplay.ui.components.navigateSafe
import com.sundram.weplay.ui.nav.Routes
import com.sundram.weplay.ui.theme.AuroraBorder
import com.sundram.weplay.ui.theme.AuroraElevated
import com.sundram.weplay.ui.theme.AuroraError
import com.sundram.weplay.ui.theme.AuroraSuccess
import com.sundram.weplay.ui.theme.AuroraSurface
import com.sundram.weplay.ui.theme.CloudWhite
import com.sundram.weplay.ui.theme.CrystalBlue
import com.sundram.weplay.ui.theme.DeepInk
import com.sundram.weplay.ui.theme.DreamLavender
import com.sundram.weplay.ui.theme.MysticPurple
import com.sundram.weplay.ui.theme.RoyalGold
import com.sundram.weplay.ui.theme.RoyalViolet
import com.sundram.weplay.ui.theme.SunsetRose

private val ProfileReportReasons = listOf(
    "Harassment or abusive language",
    "Spam or promotional links",
    "Inappropriate profile content",
    "Impersonation or fake account",
    "Other",
)

private data class ResolvedUserDetail(
    val id: String,
    val name: String,
    val level: Int,
    val statusText: String,
    val bio: String,
    val online: Boolean,
    val verified: Boolean,
    val frameTier: FrameTier,
    val tagLabel: String?,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileDetailScreen(
    nav: NavHostController,
    userId: String,
) {
    val context = LocalContext.current
    val clipboard = LocalClipboardManager.current
    val profileStore = remember { ProfileStore(context) }
    val msgStore = remember { MessagingStore(context) { profileStore.load() } }

    var refreshNonce by remember { mutableIntStateOf(0) }
    var menuOpen by remember { mutableStateOf(false) }
    var showGiftSheet by remember { mutableStateOf(false) }
    var showBlockDialog by remember { mutableStateOf(false) }
    var showReportDialog by remember { mutableStateOf(false) }
    var selectedReason by remember { mutableStateOf(ProfileReportReasons.first()) }
    var giftNotice by remember { mutableStateOf<String?>(null) }
    var currentCoins by remember { mutableLongStateOf(profileStore.coinBalance()) }

    val resolved = remember(userId) {
        val trimmed = userId.trim()
        val directoryUser = msgStore.userById(trimmed)
        val communityPlayer = exploreUsers.firstOrNull { it.id == trimmed || it.name.equals(trimmed, ignoreCase = true) }
        val leaderboardRepo = FrameRepository(context)
        val rankedMatch = LeaderboardCategory.entries.firstNotNullOfOrNull { cat ->
            leaderboardRepo.getLeaderboard(cat, LeaderboardPeriod.TODAY, profileStore.load()).first
                .firstOrNull { it.id == trimmed || it.name.equals(trimmed, ignoreCase = true) }
        }

        when {
            communityPlayer != null -> ResolvedUserDetail(
                id = communityPlayer.id,
                name = communityPlayer.name,
                level = communityPlayer.level,
                statusText = communityPlayer.statusText,
                bio = communityPlayer.bio,
                online = communityPlayer.isOnline,
                verified = communityPlayer.level >= 25,
                frameTier = communityPlayer.frameTier,
                tagLabel = communityPlayer.distance.ifBlank { null },
            )
            directoryUser != null -> ResolvedUserDetail(
                id = directoryUser.id,
                name = directoryUser.name,
                level = directoryUser.level,
                statusText = if (directoryUser.online) "Online now" else "Last seen recently",
                bio = directoryUser.bio,
                online = directoryUser.online,
                verified = directoryUser.level >= 20,
                frameTier = FrameTier.EPIC,
                tagLabel = null,
            )
            rankedMatch != null -> ResolvedUserDetail(
                id = rankedMatch.id,
                name = rankedMatch.name,
                level = rankedMatch.level,
                statusText = rankedMatch.subtitle ?: "Leaderboard contender",
                bio = "Active competitor on the Paradise leaderboards (${rankedMatch.scoreFormatted} pts).",
                online = true,
                verified = rankedMatch.verified,
                frameTier = FrameTier.fromId(rankedMatch.frameId),
                tagLabel = "Rank #${rankedMatch.rank}",
            )
            trimmed.isNotBlank() -> ResolvedUserDetail(
                id = trimmed,
                name = "Player $trimmed",
                level = 1,
                statusText = "Local player directory",
                bio = "Profile details for ID $trimmed in local preview mode.",
                online = false,
                verified = false,
                frameTier = FrameTier.COMMON,
                tagLabel = null,
            )
            else -> null
        }
    }

    val rel = remember(refreshNonce, resolved?.id) {
        resolved?.let { msgStore.relationship(it.id) } ?: "none"
    }
    val isBlocked = rel == "blocked"
    val isSelf = rel == "self"

    AuroraDetailScaffold(
        nav = nav,
        title = resolved?.name ?: "Player Profile",
        subtitle = resolved?.let { "ID: ${it.id}" } ?: "Not found",
        rightContent = {
            if (resolved != null && !isSelf) {
                Box {
                    Box(
                        modifier = Modifier
                            .defaultMinSize(minWidth = 48.dp, minHeight = 48.dp)
                            .clip(CircleShape)
                            .background(AuroraSurface)
                            .border(1.dp, AuroraBorder, CircleShape)
                            .clickable { menuOpen = true },
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "•••",
                            color = CloudWhite,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                    DropdownMenu(
                        expanded = menuOpen,
                        onDismissRequest = { menuOpen = false },
                        containerColor = AuroraElevated,
                    ) {
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = if (isBlocked) "Unblock player" else "Block player",
                                    color = if (isBlocked) CrystalBlue else AuroraError,
                                )
                            },
                            onClick = {
                                menuOpen = false
                                if (isBlocked) {
                                    msgStore.unblock(resolved.id)
                                    refreshNonce++
                                    Toast.makeText(context, "${resolved.name} unblocked", Toast.LENGTH_SHORT).show()
                                } else {
                                    showBlockDialog = true
                                }
                            },
                        )
                        DropdownMenuItem(
                            text = { Text("Report player", color = AuroraError) },
                            onClick = {
                                menuOpen = false
                                showReportDialog = true
                            },
                        )
                    }
                }
            }
        },
        bottomBar = if (resolved != null && !isSelf) {
            {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xF0150D38))
                        .border(1.dp, AuroraBorder)
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    OutlinedButton(
                        onClick = {
                            giftNotice = null
                            currentCoins = profileStore.coinBalance()
                            showGiftSheet = true
                        },
                        enabled = !isBlocked,
                        border = BorderStroke(1.dp, RoyalGold.copy(alpha = 0.65f)),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .weight(1f)
                            .defaultMinSize(minHeight = 48.dp),
                    ) {
                        Text(
                            text = "Send Gift",
                            color = RoyalGold,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                    Button(
                        onClick = { nav.navigateSafe(Routes.chatRoute(resolved.id)) },
                        enabled = !isBlocked,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MysticPurple,
                            contentColor = CloudWhite,
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .weight(1f)
                            .defaultMinSize(minHeight = 48.dp),
                    ) {
                        Text(
                            text = if (isBlocked) "Blocked" else "Chat",
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }
        } else null,
    ) {
        if (resolved == null) {
            AuroraEmptyStateCard(
                title = "Player profile unavailable",
                message = "We could not find a player matching that identifier.",
                emoji = "🔍",
                actionLabel = "Go Back",
                onAction = { nav.popBackStack() },
            )
        } else {
            // Hero Banner Card
            Surface(
                shape = RoundedCornerShape(22.dp),
                color = AuroraSurface,
                border = BorderStroke(1.dp, AuroraBorder),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color(0xFF391D7D),
                                    AuroraSurface,
                                )
                            )
                        )
                        .padding(18.dp),
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        AvatarWithFrame(
                            name = resolved.name,
                            photoPath = null,
                            size = 86.dp,
                            frameTier = resolved.frameTier,
                            animated = true,
                            showCrown = resolved.level >= 25,
                        )
                        Spacer(Modifier.height(10.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = resolved.name,
                                color = CloudWhite,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.ExtraBold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                            if (resolved.verified) {
                                Spacer(Modifier.width(6.dp))
                                Icon(
                                    painter = painterResource(R.drawable.ic_check_all),
                                    contentDescription = "Verified",
                                    tint = CrystalBlue,
                                    modifier = Modifier.size(16.dp),
                                )
                            }
                        }
                        Spacer(Modifier.height(6.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            // Copyable ID pill
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(AuroraElevated)
                                    .border(1.dp, AuroraBorder, RoundedCornerShape(10.dp))
                                    .clickable {
                                        clipboard.setText(AnnotatedString(resolved.id))
                                        Toast.makeText(context, "Copied ID: ${resolved.id}", Toast.LENGTH_SHORT).show()
                                    }
                                    .padding(horizontal = 9.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    text = "ID: ${resolved.id} · Copy",
                                    color = DreamLavender,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                )
                            }
                            // Level Pill
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(RoyalGold.copy(alpha = 0.2f))
                                    .border(1.dp, RoyalGold.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                                    .padding(horizontal = 9.dp, vertical = 4.dp),
                            ) {
                                Text(
                                    text = "Lv.${resolved.level}",
                                    color = RoyalGold,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                )
                            }
                            // Status Pill
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(
                                        if (resolved.online) AuroraSuccess.copy(alpha = 0.18f)
                                        else AuroraElevated
                                    )
                                    .padding(horizontal = 9.dp, vertical = 4.dp),
                            ) {
                                Text(
                                    text = if (resolved.online) "Online" else "Offline",
                                    color = if (resolved.online) AuroraSuccess else DreamLavender,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                )
                            }
                        }

                        if (!isSelf) {
                            Spacer(Modifier.height(14.dp))
                            val (relLabel, relContainer) = when (rel) {
                                "friend" -> "Friends ✓" to AuroraElevated
                                "outgoing" -> "Request Sent (Cancel)" to AuroraElevated
                                "incoming" -> "Accept Friend Request" to RoyalViolet
                                "blocked" -> "Blocked · Tap to Unblock" to AuroraError
                                else -> "+ Add Friend" to MysticPurple
                            }
                            Button(
                                onClick = {
                                    when (rel) {
                                        "friend" -> nav.navigateSafe(Routes.chatRoute(resolved.id))
                                        "outgoing" -> {
                                            msgStore.cancelRequest(resolved.id)
                                            refreshNonce++
                                        }
                                        "incoming" -> {
                                            msgStore.acceptRequest(resolved.id)
                                            refreshNonce++
                                        }
                                        "blocked" -> {
                                            msgStore.unblock(resolved.id)
                                            refreshNonce++
                                        }
                                        else -> {
                                            msgStore.sendRequest(resolved.id)
                                            refreshNonce++
                                        }
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = relContainer,
                                    contentColor = CloudWhite,
                                ),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .defaultMinSize(minHeight = 44.dp),
                            ) {
                                Text(relLabel, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(14.dp))

            AuroraGlassCard {
                Text(
                    text = "About",
                    color = CrystalBlue,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = resolved.bio,
                    color = CloudWhite,
                    fontSize = 13.5.sp,
                    lineHeight = 19.sp,
                )
                Spacer(Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(AuroraElevated)
                            .padding(horizontal = 10.dp, vertical = 5.dp),
                    ) {
                        Text(
                            text = resolved.statusText,
                            color = DreamLavender,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Medium,
                        )
                    }
                    resolved.tagLabel?.let { tag ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(AuroraElevated)
                                .padding(horizontal = 10.dp, vertical = 5.dp),
                        ) {
                            Text(
                                text = tag,
                                color = SunsetRose,
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.SemiBold,
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            AuroraGlassCard {
                Text(
                    text = "Profile Frame & Standing",
                    color = CrystalBlue,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Column {
                        Text("Frame Tier", color = DreamLavender, fontSize = 11.5.sp)
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = resolved.frameTier.displayName,
                            color = CloudWhite,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Player Level", color = DreamLavender, fontSize = 11.5.sp)
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = "Lv.${resolved.level}",
                            color = RoyalGold,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }
        }
    }

    if (showGiftSheet && resolved != null) {
        ModalBottomSheet(
            onDismissRequest = { showGiftSheet = false },
            containerColor = DeepInk,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Send Gift to ${resolved.name}",
                        color = CloudWhite,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = "$currentCoins coins",
                        color = RoyalGold,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
                Spacer(Modifier.height(12.dp))
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.height(220.dp),
                ) {
                    items(DemoGifts, key = { it.id }) { gift ->
                        val affordable = currentCoins >= gift.cost
                        Column(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(AuroraElevated)
                                .border(
                                    1.dp,
                                    if (affordable) CrystalBlue.copy(alpha = 0.45f) else AuroraBorder,
                                    RoundedCornerShape(12.dp),
                                )
                                .bounceClick {
                                    if (!affordable) {
                                        giftNotice = "Need ${gift.cost - currentCoins} more coins for ${gift.name}."
                                    } else {
                                        var updatedCoins = currentCoins
                                        val ok = msgStore.sendGift(resolved.id, gift.id) { newBal ->
                                            updatedCoins = newBal
                                        }
                                        if (ok) {
                                            currentCoins = updatedCoins
                                            profileStore.adjustCoins(-gift.cost)
                                            currentCoins = profileStore.coinBalance()
                                            giftNotice = "Sent ${gift.name} (-${gift.cost} coins)."
                                            Toast.makeText(context, "Sent ${gift.name}!", Toast.LENGTH_SHORT).show()
                                        } else {
                                            val newBal = profileStore.adjustCoins(-gift.cost)
                                            currentCoins = newBal
                                            giftNotice = "Sent ${gift.name} (-${gift.cost} coins)."
                                            Toast.makeText(context, "Sent ${gift.name}!", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }
                                .padding(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Text(gift.name, color = CloudWhite, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, maxLines = 1)
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = "${gift.cost} coins",
                                color = if (affordable) RoyalGold else DreamLavender.copy(alpha = 0.6f),
                                fontSize = 11.sp,
                            )
                        }
                    }
                }
                giftNotice?.let { notice ->
                    Spacer(Modifier.height(8.dp))
                    Text(notice, color = CrystalBlue, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                }
                Spacer(Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    OutlinedButton(
                        onClick = {
                            showGiftSheet = false
                            nav.navigateSafe(Routes.TOPUP)
                        },
                        modifier = Modifier.weight(1f),
                        border = BorderStroke(1.dp, RoyalGold.copy(alpha = 0.5f)),
                    ) {
                        Text("Get Coins", color = RoyalGold)
                    }
                    Button(
                        onClick = { showGiftSheet = false },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = RoyalViolet, contentColor = CloudWhite),
                    ) {
                        Text("Close")
                    }
                }
                Spacer(Modifier.height(16.dp))
            }
        }
    }

    if (showBlockDialog && resolved != null) {
        AuroraConfirmDialog(
            title = "Block ${resolved.name}?",
            body = "Blocked players cannot message you or send friend requests.",
            confirmText = "Block",
            destructive = true,
            onConfirm = {
                msgStore.block(resolved.id)
                showBlockDialog = false
                refreshNonce++
                Toast.makeText(context, "${resolved.name} blocked", Toast.LENGTH_SHORT).show()
            },
            onDismiss = { showBlockDialog = false },
        )
    }

    if (showReportDialog && resolved != null) {
        AlertDialog(
            onDismissRequest = { showReportDialog = false },
            containerColor = DeepInk,
            titleContentColor = CloudWhite,
            textContentColor = DreamLavender,
            title = { Text("Report ${resolved.name}", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        "Select a reason. Reporting also blocks this player from contacting you.",
                        fontSize = 12.5.sp,
                    )
                    Spacer(Modifier.height(6.dp))
                    ProfileReportReasons.forEach { reason ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (selectedReason == reason) AuroraElevated else Color.Transparent)
                                .clickable { selectedReason = reason }
                                .padding(horizontal = 10.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = if (selectedReason == reason) "● $reason" else "○ $reason",
                                color = if (selectedReason == reason) CrystalBlue else CloudWhite,
                                fontSize = 13.sp,
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        msgStore.report(resolved.id, selectedReason)
                        showReportDialog = false
                        refreshNonce++
                        Toast.makeText(context, "Report submitted and saved locally", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AuroraError, contentColor = CloudWhite),
                ) {
                    Text("Report & Block", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showReportDialog = false }) {
                    Text("Cancel", color = DreamLavender)
                }
            },
        )
    }
}
