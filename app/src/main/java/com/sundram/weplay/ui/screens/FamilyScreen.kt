package com.sundram.weplay.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
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
import com.sundram.weplay.data.ActivityEventType
import com.sundram.weplay.data.ChestTier
import com.sundram.weplay.data.FamilyActivityEvent
import com.sundram.weplay.data.FamilyMember
import com.sundram.weplay.data.FamilyProfile
import com.sundram.weplay.data.FamilyQuest
import com.sundram.weplay.data.FamilyRankingEntry
import com.sundram.weplay.data.FamilyRepository
import com.sundram.weplay.data.FamilyRole
import com.sundram.weplay.data.FamilyShopItem
import com.sundram.weplay.data.FamilyTier
import com.sundram.weplay.data.FrameTier
import com.sundram.weplay.data.QuestCategory
import com.sundram.weplay.data.RecruitmentPolicy
import com.sundram.weplay.data.UserProfile
import com.sundram.weplay.ui.components.AvatarWithFrame
import com.sundram.weplay.ui.components.UserAvatar
import com.sundram.weplay.ui.components.bounceClick
import com.sundram.weplay.ui.nav.Routes

// ─────────────────────────────────────────────────────────────────────────────
//  Color Palette — White + Lavender + Cyan + Soft Gold
// ─────────────────────────────────────────────────────────────────────────────
private val FamilyCanvas     = Color(0xFFFAF9FF)
private val FamilyWhite      = Color(0xFFFFFFFF)
private val FamilyInk        = Color(0xFF26233C)
private val FamilySecondary  = Color(0xFF8D8AA3)
private val FamilyLavender   = Color(0xFFEFE9FF)
private val FamilyCyan       = Color(0xFF31CAD9)
private val FamilyGold       = Color(0xFFF5BD62)
private val FamilyHairline   = Color(0xFFF0ECF9)
private val FamilyDanger     = Color(0xFFFF4D4F)

/**
 * Production-ready Family System Screen (Kotlin + Jetpack Compose)
 *
 * Implements:
 * - Non-member Discovery / Browse / Search / Create Hub
 * - Member Family Home (Hero, Economy strip, 5 Tabs: Overview, Quests, Members, Activity, Manage)
 * - Treasury Donations & Cashback in Family Coins
 * - 4 Weekly Chests (Normal, Advanced, Rare, Super)
 * - Daily Free Gacha/Reward
 * - Family Voice Room launcher (LiveKit connector)
 * - Family Chat & Rankings
 * - Full role-based permissions (Leader, Deputy, Admin, Moderator, Member)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FamilyScreen(
    nav: NavHostController,
    profile: UserProfile,
    onSaveProfile: (UserProfile) -> Unit = {},
) {
    val context = LocalContext.current
    val repository = remember { FamilyRepository.getInstance(context) }

    val currentFamily by repository.currentFamily.collectAsState()
    val members by repository.members.collectAsState()
    val quests by repository.quests.collectAsState()
    val chests by repository.chests.collectAsState()
    val activityEvents by repository.activityEvents.collectAsState()
    val shopItems by repository.shopItems.collectAsState()
    val rankings by repository.rankings.collectAsState()
    val joinRequests by repository.joinRequests.collectAsState()
    val discoverFamilies by repository.discoverFamilies.collectAsState()

    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    val tabTitles = listOf("Overview", "Quests", "Members", "Activity", "Manage")

    // Modals
    var showDonateDialog by rememberSaveable { mutableStateOf(false) }
    var showCreateDialog by rememberSaveable { mutableStateOf(false) }
    var showDailyRewardDialog by rememberSaveable { mutableStateOf(false) }
    var dailyRewardWon by remember { mutableStateOf<String?>(null) }
    var showShopSheet by rememberSaveable { mutableStateOf(false) }
    var showRankingsSheet by rememberSaveable { mutableStateOf(false) }
    var showLeaveDialog by rememberSaveable { mutableStateOf(false) }
    var selectedMemberForAction by remember { mutableStateOf<FamilyMember?>(null) }

    // Back handling in LIFO order
    BackHandler {
        when {
            showDonateDialog -> showDonateDialog = false
            showCreateDialog -> showCreateDialog = false
            showDailyRewardDialog -> showDailyRewardDialog = false
            showShopSheet -> showShopSheet = false
            showRankingsSheet -> showRankingsSheet = false
            showLeaveDialog -> showLeaveDialog = false
            selectedMemberForAction != null -> selectedMemberForAction = null
            else -> nav.popBackStack()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(FamilyCanvas),
    ) {
        if (currentFamily == null) {
            // ─────────────────────────────────────────────────────────────────
            //  NON-MEMBER DISCOVERY & BROWSE HUB
            // ─────────────────────────────────────────────────────────────────
            FamilyDiscoveryHub(
                discoverFamilies = discoverFamilies,
                onCreateClick = { showCreateDialog = true },
                onJoinClick = { targetFamily ->
                    repository.joinFamily(targetFamily, profile.name)
                    onSaveProfile(profile.copy(family = targetFamily.name, familyCode = targetFamily.tag))
                    Toast.makeText(context, "Joined ${targetFamily.name}!", Toast.LENGTH_SHORT).show()
                },
                onBack = { nav.popBackStack() },
            )
        } else {
            // ─────────────────────────────────────────────────────────────────
            //  MEMBER FAMILY HOME
            // ─────────────────────────────────────────────────────────────────
            val family = currentFamily!!
            val myRole = members.find { it.name == profile.name }?.role ?: FamilyRole.MEMBER

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 32.dp),
            ) {
                // 1. Family Hero Section
                item(key = "family_hero") {
                    FamilyHeroSection(
                        family = family,
                        onBack = { nav.popBackStack() },
                        onVoiceRoomClick = {
                            family.voiceRoomId?.let { roomId ->
                                nav.navigate(Routes.ROOM_DETAIL.replace("{roomId}", roomId))
                            } ?: Toast.makeText(context, "Family Voice Room unlocking at Level 4", Toast.LENGTH_SHORT).show()
                        },
                        onChatClick = {
                            nav.navigate(Routes.MSG)
                        },
                        onRankingClick = {
                            showRankingsSheet = true
                        },
                        onCopyId = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("Family Tag", family.tag))
                            Toast.makeText(context, "Tag [${family.tag}] copied!", Toast.LENGTH_SHORT).show()
                        },
                    )
                }

                // 2. Economy & Progression Stat Strip
                item(key = "economy_strip") {
                    FamilyEconomyStrip(
                        funds = family.funds,
                        coins = family.personalCoins,
                        weeklyActiveness = family.weeklyActiveness,
                        level = family.level,
                        onDonateClick = { showDonateDialog = true },
                        onShopClick = { showShopSheet = true },
                    )
                }

                // 3. Navigation Tab Row
                item(key = "tab_row") {
                    FamilyTabSelector(
                        selectedTab = selectedTab,
                        tabTitles = tabTitles,
                        onTabSelected = { selectedTab = it },
                    )
                }

                // 4. Tab Content
                when (selectedTab) {
                    0 -> { // Overview
                        item(key = "tab_overview") {
                            FamilyOverviewTab(
                                family = family,
                                chests = chests,
                                onClaimChest = { tier ->
                                    if (repository.claimWeeklyChest(tier)) {
                                        Toast.makeText(context, "Weekly Chest Claimed!", Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(context, "Reach ${tier.threshold} activeness to unlock", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                onDailyRewardClick = {
                                    val reward = repository.claimDailyReward()
                                    if (reward != null) {
                                        dailyRewardWon = reward.second
                                        showDailyRewardDialog = true
                                    } else {
                                        Toast.makeText(context, "Daily reward already claimed today", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                onDonateClick = { showDonateDialog = true },
                                onVoiceRoomClick = {
                                    family.voiceRoomId?.let { nav.navigate(Routes.ROOM_DETAIL.replace("{roomId}", it)) }
                                },
                                topContributors = members.sortedByDescending { it.weeklyActiveness }.take(3),
                            )
                        }
                    }
                    1 -> { // Quests
                        item(key = "tab_quests") {
                            FamilyQuestsTab(
                                quests = quests,
                                onClaimQuest = { questId ->
                                    if (repository.claimQuest(questId)) {
                                        Toast.makeText(context, "Quest reward claimed!", Toast.LENGTH_SHORT).show()
                                    }
                                },
                            )
                        }
                    }
                    2 -> { // Members
                        item(key = "tab_members") {
                            FamilyMembersTab(
                                members = members,
                                myRole = myRole,
                                onMemberClick = { member ->
                                    if (myRole.canManageMembers() && member.role != FamilyRole.LEADER) {
                                        selectedMemberForAction = member
                                    }
                                },
                                onInviteClick = {
                                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                        type = "text/plain"
                                        putExtra(
                                            Intent.EXTRA_TEXT,
                                            "Join our family [${family.tag}] ${family.name} in voice rooms and games!"
                                        )
                                    }
                                    context.startActivity(Intent.createChooser(shareIntent, "Invite to Family"))
                                },
                            )
                        }
                    }
                    3 -> { // Activity
                        item(key = "tab_activity") {
                            FamilyActivityTab(events = activityEvents)
                        }
                    }
                    4 -> { // Manage
                        item(key = "tab_manage") {
                            FamilyManageTab(
                                family = family,
                                myRole = myRole,
                                joinRequests = joinRequests,
                                onUpdateAnnouncement = { newAnn ->
                                    repository.updateAnnouncement(newAnn)
                                    Toast.makeText(context, "Announcement updated", Toast.LENGTH_SHORT).show()
                                },
                                onApproveRequest = { reqId ->
                                    repository.approveJoinRequest(reqId)
                                    Toast.makeText(context, "Member approved!", Toast.LENGTH_SHORT).show()
                                },
                                onRejectRequest = { reqId ->
                                    repository.rejectJoinRequest(reqId)
                                },
                                onLeaveClick = { showLeaveDialog = true },
                            )
                        }
                    }
                }
            }
        }

        // ─────────────────────────────────────────────────────────────────────
        //  Dialogs & Bottom Sheets
        // ─────────────────────────────────────────────────────────────────────

        // 1. Treasury Donation Dialog
        if (showDonateDialog) {
            TreasuryDonationDialog(
                userCoins = profile.coins,
                onDismiss = { showDonateDialog = false },
                onDonate = { amount ->
                    if (profile.coins >= amount) {
                        repository.donateToTreasury(amount)
                        onSaveProfile(profile.copy(coins = profile.coins - amount))
                        showDonateDialog = false
                        Toast.makeText(context, "Donated $amount coins to Treasury! Earned ${amount / 5} Family Coins", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Insufficient coins balance", Toast.LENGTH_SHORT).show()
                    }
                },
            )
        }

        // 2. Create Family Dialog
        if (showCreateDialog) {
            CreateFamilyDialog(
                onDismiss = { showCreateDialog = false },
                onCreate = { name, tag, sign, desc ->
                    if (repository.createFamily(name, tag, sign, desc, profile.name)) {
                        onSaveProfile(profile.copy(family = name, familyCode = tag))
                        showCreateDialog = false
                        Toast.makeText(context, "Family [$tag] $name created successfully!", Toast.LENGTH_SHORT).show()
                    }
                },
            )
        }

        // 3. Daily Free Reward Dialog
        if (showDailyRewardDialog && dailyRewardWon != null) {
            AlertDialog(
                onDismissRequest = { showDailyRewardDialog = false },
                containerColor = FamilyWhite,
                shape = RoundedCornerShape(24.dp),
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(R.drawable.family_ic_gacha),
                            contentDescription = null,
                            modifier = Modifier.size(32.dp),
                        )
                        Spacer(Modifier.width(10.dp))
                        Text("Daily Guild Gift!", color = FamilyInk, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    }
                },
                text = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                        Text("You opened today's free family gift box and received:", color = FamilySecondary, fontSize = 13.sp)
                        Spacer(Modifier.height(14.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(FamilyLavender)
                                .padding(horizontal = 20.dp, vertical = 12.dp),
                        ) {
                            Text(dailyRewardWon!!, color = FamilyCyan, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showDailyRewardDialog = false }) {
                        Text("Claim & Enjoy", color = FamilyCyan, fontWeight = FontWeight.Bold)
                    }
                },
            )
        }

        // 4. Family Shop Bottom Sheet
        if (showShopSheet) {
            ModalBottomSheet(
                onDismissRequest = { showShopSheet = false },
                containerColor = FamilyWhite,
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            ) {
                FamilyShopBottomSheet(
                    personalCoins = currentFamily?.personalCoins ?: 0L,
                    items = shopItems,
                    onPurchase = { item ->
                        if (repository.purchaseShopItem(item.id)) {
                            Toast.makeText(context, "Purchased ${item.title}!", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Not enough Family Coins", Toast.LENGTH_SHORT).show()
                        }
                    },
                    onEquip = { item ->
                        repository.equipShopItem(item.id)
                        Toast.makeText(context, "Equipped ${item.title}", Toast.LENGTH_SHORT).show()
                    },
                    onDismiss = { showShopSheet = false },
                )
            }
        }

        // 5. Family Rankings Bottom Sheet
        if (showRankingsSheet) {
            ModalBottomSheet(
                onDismissRequest = { showRankingsSheet = false },
                containerColor = FamilyWhite,
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            ) {
                FamilyRankingsBottomSheet(
                    rankings = rankings,
                    onDismiss = { showRankingsSheet = false },
                )
            }
        }

        // 6. Leave Family Dialog
        if (showLeaveDialog) {
            AlertDialog(
                onDismissRequest = { showLeaveDialog = false },
                containerColor = FamilyWhite,
                shape = RoundedCornerShape(20.dp),
                title = { Text("Leave Family?", color = FamilyInk, fontWeight = FontWeight.Bold) },
                text = {
                    Text(
                        "You will lose access to the family voice room, guild sign, and weekly chests.",
                        color = FamilySecondary,
                        fontSize = 13.sp,
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            repository.leaveFamily()
                            onSaveProfile(profile.copy(family = null, familyCode = null))
                            showLeaveDialog = false
                            Toast.makeText(context, "You left the family", Toast.LENGTH_SHORT).show()
                        }
                    ) {
                        Text("Leave", color = FamilyDanger, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showLeaveDialog = false }) {
                        Text("Stay", color = FamilySecondary)
                    }
                },
            )
        }

        // 7. Member Role Management Sheet
        if (selectedMemberForAction != null) {
            val member = selectedMemberForAction!!
            ModalBottomSheet(
                onDismissRequest = { selectedMemberForAction = null },
                containerColor = FamilyWhite,
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                ) {
                    Text(
                        text = "Manage ${member.name} (${member.role.title})",
                        color = FamilyInk,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                    )
                    Spacer(Modifier.height(14.dp))

                    if (member.role != FamilyRole.DEPUTY) {
                        ManageActionRow("Promote to Deputy", R.drawable.family_ic_deputy) {
                            repository.updateMemberRole(member.id, FamilyRole.DEPUTY)
                            selectedMemberForAction = null
                            Toast.makeText(context, "Promoted to Deputy", Toast.LENGTH_SHORT).show()
                        }
                    }

                    if (member.role != FamilyRole.ADMIN) {
                        ManageActionRow("Promote to Admin", R.drawable.family_ic_admin) {
                            repository.updateMemberRole(member.id, FamilyRole.ADMIN)
                            selectedMemberForAction = null
                            Toast.makeText(context, "Promoted to Admin", Toast.LENGTH_SHORT).show()
                        }
                    }

                    if (member.role != FamilyRole.MEMBER) {
                        ManageActionRow("Demote to Member", R.drawable.family_ic_member) {
                            repository.updateMemberRole(member.id, FamilyRole.MEMBER)
                            selectedMemberForAction = null
                            Toast.makeText(context, "Demoted to Member", Toast.LENGTH_SHORT).show()
                        }
                    }

                    ManageActionRow("Remove from Family", R.drawable.family_ic_kick, isDanger = true) {
                        repository.kickMember(member.id)
                        selectedMemberForAction = null
                        Toast.makeText(context, "${member.name} removed", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  Component: Family Discovery / Browse Hub (for non-members)
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun FamilyDiscoveryHub(
    discoverFamilies: List<FamilyProfile>,
    onCreateClick: () -> Unit,
    onJoinClick: (FamilyProfile) -> Unit,
    onBack: () -> Unit,
) {
    var searchQuery by remember { mutableStateOf("") }
    val filtered = remember(searchQuery, discoverFamilies) {
        if (searchQuery.isBlank()) discoverFamilies
        else discoverFamilies.filter {
            it.name.contains(searchQuery, ignoreCase = true) || it.tag.contains(searchQuery, ignoreCase = true)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding(),
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(FamilyWhite)
                    .clickable(onClick = onBack),
                contentAlignment = Alignment.Center,
            ) {
                Image(
                    painter = painterResource(R.drawable.me_clean_share),
                    contentDescription = "Back",
                    modifier = Modifier.size(16.dp),
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text("Family Hub", color = FamilyInk, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                Text("Play together, level up, and earn guild rewards", color = FamilySecondary, fontSize = 12.sp)
            }
        }

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search by family name or [TAG]...", color = FamilySecondary, fontSize = 13.sp) },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = FamilyWhite,
                unfocusedContainerColor = FamilyWhite,
                focusedBorderColor = FamilyCyan,
                unfocusedBorderColor = FamilyHairline,
            ),
        )

        // Banner Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(
                    Brush.horizontalGradient(listOf(Color(0xFF7B52E8), Color(0xFF31CAD9)))
                )
                .padding(18.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text("Create Your Own Guild", color = FamilyWhite, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Spacer(Modifier.height(4.dp))
                    Text("Gather friends, unlock exclusive voice rooms, and share weekly chests.", color = FamilyWhite.copy(alpha = 0.85f), fontSize = 12.sp)
                    Spacer(Modifier.height(12.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(FamilyWhite)
                            .bounceClick(onClick = onCreateClick)
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                    ) {
                        Text("Create Family", color = Color(0xFF7B52E8), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
                Spacer(Modifier.width(8.dp))
                Image(
                    painter = painterResource(R.drawable.family_ic_crown),
                    contentDescription = null,
                    modifier = Modifier.size(56.dp),
                )
            }
        }

        Spacer(Modifier.height(6.dp))
        Text(
            text = "Recommended Families (${filtered.size})",
            color = FamilyInk,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
        ) {
            items(filtered) { fam ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 5.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(FamilyWhite)
                        .border(1.dp, FamilyHairline, RoundedCornerShape(18.dp))
                        .padding(14.dp),
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(fam.emblemRes),
                            contentDescription = fam.name,
                            modifier = Modifier.size(48.dp),
                        )
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(fam.name, color = FamilyInk, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                Spacer(Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(FamilyLavender)
                                        .padding(horizontal = 6.dp, vertical = 1.dp),
                                ) {
                                    Text("[${fam.tag}]", color = Color(0xFF7B52E8), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            Spacer(Modifier.height(3.dp))
                            Text(fam.sign, color = FamilySecondary, fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Spacer(Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Lv.${fam.level}", color = FamilyGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Spacer(Modifier.width(8.dp))
                                Text("• ${fam.memberCount}/${fam.maxMembers} Members", color = FamilySecondary, fontSize = 11.sp)
                            }
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .background(FamilyCyan)
                                .bounceClick { onJoinClick(fam) }
                                .padding(horizontal = 14.dp, vertical = 8.dp),
                        ) {
                            Text("Join", color = FamilyWhite, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  Component: Member Family Hero Section
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun FamilyHeroSection(
    family: FamilyProfile,
    onBack: () -> Unit,
    onVoiceRoomClick: () -> Unit,
    onChatClick: () -> Unit,
    onRankingClick: () -> Unit,
    onCopyId: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFE9DEFF),
                        Color(0xFFE4F8FA),
                        FamilyCanvas,
                    )
                )
            )
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 10.dp),
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Top Navigation & Action Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Back Button
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(FamilyWhite.copy(alpha = 0.85f))
                        .clickable(onClick = onBack),
                    contentAlignment = Alignment.Center,
                ) {
                    Image(
                        painter = painterResource(R.drawable.me_clean_share),
                        contentDescription = "Back",
                        modifier = Modifier.size(16.dp),
                    )
                }

                // Action Shortcuts: Voice Room, Chat, Rankings
                Row(verticalAlignment = Alignment.CenterVertically) {
                    HeaderActionCircle(R.drawable.family_ic_voice, "Voice Room", onVoiceRoomClick)
                    Spacer(Modifier.width(8.dp))
                    HeaderActionCircle(R.drawable.family_ic_chat, "Family Chat", onChatClick)
                    Spacer(Modifier.width(8.dp))
                    HeaderActionCircle(R.drawable.family_ic_ranking, "Rankings", onRankingClick)
                }
            }

            Spacer(Modifier.height(14.dp))

            // Family Identity Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Emblem
                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(FamilyWhite)
                        .border(2.dp, FamilyWhite, RoundedCornerShape(20.dp))
                        .shadow(2.dp, RoundedCornerShape(20.dp)),
                    contentAlignment = Alignment.Center,
                ) {
                    Image(
                        painter = painterResource(family.emblemRes),
                        contentDescription = family.name,
                        modifier = Modifier.size(46.dp),
                    )
                }

                Spacer(Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    // Name & Tag
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(family.name, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = FamilyInk)
                        Spacer(Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(FamilyLavender)
                                .padding(horizontal = 6.dp, vertical = 2.dp),
                        ) {
                            Text("[${family.tag}]", color = Color(0xFF7B52E8), fontSize = 11.sp, fontWeight = FontWeight.ExtraBold)
                        }
                    }

                    Spacer(Modifier.height(4.dp))

                    // Sign Motto & Copy ID
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable(onClick = onCopyId),
                    ) {
                        Text(family.sign, fontSize = 12.sp, color = FamilySecondary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        Spacer(Modifier.width(6.dp))
                        Image(
                            painter = painterResource(R.drawable.family_ic_family_sign),
                            contentDescription = "Copy Tag",
                            modifier = Modifier.size(13.dp),
                        )
                    }

                    Spacer(Modifier.height(6.dp))

                    // Tier & Level Badge Row
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(family.tier.badgeRes),
                            contentDescription = family.tier.displayName,
                            modifier = Modifier.size(20.dp),
                        )
                        Spacer(Modifier.width(4.dp))
                        Text("Lv.${family.level}", color = FamilyGold, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.width(10.dp))
                        Text("• ${family.memberCount}/${family.maxMembers} Members", color = FamilySecondary, fontSize = 11.sp)
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            // Announcement Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(FamilyWhite.copy(alpha = 0.9f))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(R.drawable.family_ic_announcement),
                        contentDescription = "Notice",
                        modifier = Modifier.size(16.dp),
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = family.announcement,
                        color = FamilyInk,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }
}

@Composable
private fun HeaderActionCircle(iconRes: Int, desc: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(FamilyWhite.copy(alpha = 0.85f))
            .bounceClick(scaleDown = 0.92f, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painterResource(iconRes),
            contentDescription = desc,
            modifier = Modifier.size(18.dp),
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  Component: Economy & Progression Strip
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun FamilyEconomyStrip(
    funds: Long,
    coins: Long,
    weeklyActiveness: Int,
    level: Int,
    onDonateClick: () -> Unit,
    onShopClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(FamilyWhite)
            .border(1.dp, FamilyHairline, RoundedCornerShape(16.dp))
            .padding(horizontal = 14.dp, vertical = 12.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Treasury Funds
            Row(
                modifier = Modifier
                    .weight(1f)
                    .clickable(onClick = onDonateClick),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Image(
                    painter = painterResource(R.drawable.family_ic_funds),
                    contentDescription = "Family Funds",
                    modifier = Modifier.size(24.dp),
                )
                Spacer(Modifier.width(8.dp))
                Column {
                    Text("$funds", color = FamilyGold, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text("Treasury", color = FamilySecondary, fontSize = 10.sp)
                }
            }

            Box(Modifier.width(1.dp).height(26.dp).background(FamilyHairline))

            // Personal Family Coins
            Row(
                modifier = Modifier
                    .weight(1f)
                    .clickable(onClick = onShopClick)
                    .padding(start = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Image(
                    painter = painterResource(R.drawable.family_ic_coins),
                    contentDescription = "Personal Coins",
                    modifier = Modifier.size(24.dp),
                )
                Spacer(Modifier.width(8.dp))
                Column {
                    Text("$coins", color = FamilyCyan, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text("Guild Shop", color = FamilySecondary, fontSize = 10.sp)
                }
            }

            Box(Modifier.width(1.dp).height(26.dp).background(FamilyHairline))

            // Weekly Activeness
            Row(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Image(
                    painter = painterResource(R.drawable.family_ic_activeness),
                    contentDescription = "Activeness",
                    modifier = Modifier.size(24.dp),
                )
                Spacer(Modifier.width(8.dp))
                Column {
                    Text("$weeklyActiveness pts", color = FamilyInk, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text("Weekly Act.", color = FamilySecondary, fontSize = 10.sp)
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  Component: Tab Selector
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun FamilyTabSelector(
    selectedTab: Int,
    tabTitles: List<String>,
    onTabSelected: (Int) -> Unit,
) {
    ScrollableTabRow(
        selectedTabIndex = selectedTab,
        containerColor = FamilyCanvas,
        edgePadding = 16.dp,
        divider = {},
        indicator = { tabPositions ->
            TabRowDefaults.SecondaryIndicator(
                modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                height = 3.dp,
                color = FamilyCyan,
            )
        },
    ) {
        tabTitles.forEachIndexed { index, title ->
            Tab(
                selected = selectedTab == index,
                onClick = { onTabSelected(index) },
                text = {
                    Text(
                        text = title,
                        fontSize = 14.sp,
                        fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Medium,
                        color = if (selectedTab == index) FamilyInk else FamilySecondary,
                    )
                },
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  Tab 1: Overview
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun FamilyOverviewTab(
    family: FamilyProfile,
    chests: List<com.sundram.weplay.data.FamilyWeeklyChest>,
    onClaimChest: (ChestTier) -> Unit,
    onDailyRewardClick: () -> Unit,
    onDonateClick: () -> Unit,
    onVoiceRoomClick: () -> Unit,
    topContributors: List<FamilyMember>,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
    ) {
        // 1. Weekly Chests Progression Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(FamilyWhite)
                .border(1.dp, FamilyHairline, RoundedCornerShape(18.dp))
                .padding(14.dp),
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text("Weekly Family Chests", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = FamilyInk)
                    Text("${family.weeklyActiveness} pts", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = FamilyCyan)
                }

                Spacer(Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    chests.forEach { chest ->
                        val unlocked = family.weeklyActiveness >= chest.tier.threshold
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clickable(enabled = unlocked && !chest.isClaimed) { onClaimChest(chest.tier) }
                                .padding(4.dp),
                        ) {
                            Image(
                                painter = painterResource(chest.tier.iconRes),
                                contentDescription = chest.tier.title,
                                modifier = Modifier.size(36.dp),
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(chest.tier.title.split(" ").first(), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = FamilyInk)
                            Text(
                                text = if (chest.isClaimed) "Claimed" else if (unlocked) "Open!" else "${chest.tier.threshold}p",
                                fontSize = 9.sp,
                                color = if (chest.isClaimed) FamilySecondary else if (unlocked) FamilyCyan else FamilyGold,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(10.dp))

        // 2. Action Cards: Daily Free Box & Voice Room
        Row(modifier = Modifier.fillMaxWidth()) {
            // Daily Gift Box
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(FamilyWhite)
                    .border(1.dp, FamilyHairline, RoundedCornerShape(16.dp))
                    .bounceClick(onClick = onDailyRewardClick)
                    .padding(12.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(R.drawable.family_ic_gacha),
                        contentDescription = "Daily Box",
                        modifier = Modifier.size(32.dp),
                    )
                    Spacer(Modifier.width(8.dp))
                    Column {
                        Text("Daily Free Gift", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = FamilyInk)
                        Text("Open once a day", color = FamilyCyan, fontSize = 10.sp)
                    }
                }
            }

            Spacer(Modifier.width(10.dp))

            // Family Voice Lounge
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(FamilyWhite)
                    .border(1.dp, FamilyHairline, RoundedCornerShape(16.dp))
                    .bounceClick(onClick = onVoiceRoomClick)
                    .padding(12.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(R.drawable.family_ic_voice),
                        contentDescription = "Voice Lounge",
                        modifier = Modifier.size(32.dp),
                    )
                    Spacer(Modifier.width(8.dp))
                    Column {
                        Text("Family Room", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = FamilyInk)
                        Text("Private Lounge", color = Color(0xFF7B52E8), fontSize = 10.sp)
                    }
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        // 3. Top Contributors Leaderboard Preview
        Text("Weekly Top Contributors", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = FamilyInk)
        Spacer(Modifier.height(6.dp))

        topContributors.forEachIndexed { idx, member ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(FamilyWhite)
                    .border(1.dp, FamilyHairline, RoundedCornerShape(14.dp))
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(if (idx == 0) FamilyGold else if (idx == 1) Color(0xFFC0C0C0) else Color(0xFFCD7F32)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text("${idx + 1}", color = FamilyWhite, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                    Spacer(Modifier.width(10.dp))
                    Text(member.name, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = FamilyInk)
                    Spacer(Modifier.width(6.dp))
                    Image(
                        painter = painterResource(member.role.badgeRes),
                        contentDescription = member.role.title,
                        modifier = Modifier.size(14.dp),
                    )
                }

                Text("${member.weeklyActiveness} pts", color = FamilyCyan, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  Tab 2: Quests
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun FamilyQuestsTab(
    quests: List<FamilyQuest>,
    onClaimQuest: (String) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
    ) {
        quests.forEach { quest ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(FamilyWhite)
                    .border(1.dp, FamilyHairline, RoundedCornerShape(16.dp))
                    .padding(14.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(quest.iconRes),
                        contentDescription = quest.title,
                        modifier = Modifier.size(36.dp),
                    )
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(quest.title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = FamilyInk)
                        Spacer(Modifier.height(2.dp))
                        Text(quest.description, color = FamilySecondary, fontSize = 11.sp)
                        Spacer(Modifier.height(6.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            LinearProgressIndicator(
                                progress = { quest.progress },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(5.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = FamilyCyan,
                                trackColor = FamilyLavender,
                            )
                            Spacer(Modifier.width(8.dp))
                            Text("${quest.currentCount}/${quest.targetCount}", fontSize = 10.sp, color = FamilySecondary)
                        }
                    }
                    Spacer(Modifier.width(10.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (quest.isClaimed) FamilyCanvas
                                else if (quest.isCompleted) FamilyCyan
                                else FamilyLavender
                            )
                            .clickable(enabled = quest.isCompleted && !quest.isClaimed) {
                                onClaimQuest(quest.id)
                            }
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                    ) {
                        Text(
                            text = if (quest.isClaimed) "Claimed" else if (quest.isCompleted) "Claim" else "+${quest.rewardCoins}c",
                            color = if (quest.isClaimed) FamilySecondary else if (quest.isCompleted) FamilyWhite else Color(0xFF7B52E8),
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                        )
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  Tab 3: Members
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun FamilyMembersTab(
    members: List<FamilyMember>,
    myRole: FamilyRole,
    onMemberClick: (FamilyMember) -> Unit,
    onInviteClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
    ) {
        // Invite Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("Members Roster (${members.size})", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = FamilyInk)
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(FamilyLavender)
                    .bounceClick(onClick = onInviteClick)
                    .padding(horizontal = 12.dp, vertical = 6.dp),
            ) {
                Text("+ Invite Friends", color = Color(0xFF7B52E8), fontWeight = FontWeight.Bold, fontSize = 11.sp)
            }
        }

        Spacer(Modifier.height(8.dp))

        members.forEach { member ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(FamilyWhite)
                    .border(1.dp, FamilyHairline, RoundedCornerShape(16.dp))
                    .clickable { onMemberClick(member) }
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box {
                        AvatarWithFrame(
                            name = member.name,
                            photoPath = member.photoPath,
                            size = 40.dp,
                            frameTier = FrameTier.fromId(member.frame),
                            animated = false,
                        )
                        if (member.isOnline) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .align(Alignment.BottomEnd)
                                    .clip(CircleShape)
                                    .background(Color(0xFF4CAF50))
                                    .border(1.dp, FamilyWhite, CircleShape),
                            )
                        }
                    }
                    Spacer(Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(member.name, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = FamilyInk)
                            Spacer(Modifier.width(6.dp))
                            Image(
                                painter = painterResource(member.role.badgeRes),
                                contentDescription = member.role.title,
                                modifier = Modifier.size(14.dp),
                            )
                        }
                        Text("Weekly: ${member.weeklyActiveness} pts", color = FamilySecondary, fontSize = 11.sp)
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(FamilyCanvas)
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                ) {
                    Text(member.role.title, color = FamilyInk, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  Tab 4: Activity Timeline
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun FamilyActivityTab(events: List<FamilyActivityEvent>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
    ) {
        events.forEach { event ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(FamilyWhite)
                    .border(1.dp, FamilyHairline, RoundedCornerShape(14.dp))
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Image(
                    painter = painterResource(event.type.iconRes),
                    contentDescription = event.type.title,
                    modifier = Modifier.size(28.dp),
                )
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(event.actorName, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = FamilyInk)
                    Text(event.description, color = FamilySecondary, fontSize = 11.sp)
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  Tab 5: Manage (Role-Gated)
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun FamilyManageTab(
    family: FamilyProfile,
    myRole: FamilyRole,
    joinRequests: List<com.sundram.weplay.data.FamilyJoinRequest>,
    onUpdateAnnouncement: (String) -> Unit,
    onApproveRequest: (String) -> Unit,
    onRejectRequest: (String) -> Unit,
    onLeaveClick: () -> Unit,
) {
    var newAnnouncement by remember { mutableStateOf(family.announcement) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
    ) {
        if (myRole.canManageSettings()) {
            Text("Edit Announcement", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = FamilyInk)
            Spacer(Modifier.height(6.dp))
            OutlinedTextField(
                value = newAnnouncement,
                onValueChange = { newAnnouncement = it },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = FamilyWhite,
                    unfocusedContainerColor = FamilyWhite,
                    focusedBorderColor = FamilyCyan,
                    unfocusedBorderColor = FamilyHairline,
                ),
            )
            Spacer(Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .align(Alignment.End)
                    .clip(RoundedCornerShape(10.dp))
                    .background(FamilyCyan)
                    .bounceClick { onUpdateAnnouncement(newAnnouncement) }
                    .padding(horizontal = 14.dp, vertical = 6.dp),
            ) {
                Text("Save Notice", color = FamilyWhite, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }

            Spacer(Modifier.height(16.dp))

            // Pending Applications Queue
            Text("Pending Applications (${joinRequests.size})", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = FamilyInk)
            Spacer(Modifier.height(6.dp))

            if (joinRequests.isEmpty()) {
                Text("No pending join requests.", color = FamilySecondary, fontSize = 12.sp)
            } else {
                joinRequests.forEach { req ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(FamilyWhite)
                            .border(1.dp, FamilyHairline, RoundedCornerShape(12.dp))
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Column {
                            Text(req.applicantName, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = FamilyInk)
                            Text("Level ${req.applicantLevel}", color = FamilySecondary, fontSize = 11.sp)
                        }
                        Row {
                            TextButton(onClick = { onRejectRequest(req.id) }) {
                                Text("Reject", color = FamilySecondary, fontSize = 12.sp)
                            }
                            TextButton(onClick = { onApproveRequest(req.id) }) {
                                Text("Approve", color = FamilyCyan, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        // Leave Family Button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(FamilyWhite)
                .border(1.dp, FamilyHairline, RoundedCornerShape(14.dp))
                .clickable(onClick = onLeaveClick)
                .padding(14.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text("Leave Family", color = FamilyDanger, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  Component Dialogs
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun TreasuryDonationDialog(
    userCoins: Long,
    onDismiss: () -> Unit,
    onDonate: (Long) -> Unit,
) {
    var selectedAmount by remember { mutableStateOf(100L) }
    val amounts = listOf(50L, 100L, 200L, 500L)

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = FamilyWhite,
        shape = RoundedCornerShape(20.dp),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(R.drawable.family_ic_donate),
                    contentDescription = null,
                    modifier = Modifier.size(26.dp),
                )
                Spacer(Modifier.width(8.dp))
                Text("Donate to Treasury", color = FamilyInk, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
        },
        text = {
            Column {
                Text("Contribute coins to family funds for level upgrades and guild unlockables.", color = FamilySecondary, fontSize = 12.sp)
                Spacer(Modifier.height(14.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround,
                ) {
                    amounts.forEach { amt ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (selectedAmount == amt) FamilyCyan else FamilyLavender)
                                .clickable { selectedAmount = amt }
                                .padding(horizontal = 14.dp, vertical = 8.dp),
                        ) {
                            Text(
                                "$amt",
                                color = if (selectedAmount == amt) FamilyWhite else Color(0xFF7B52E8),
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                            )
                        }
                    }
                }
                Spacer(Modifier.height(10.dp))
                Text("Your Wallet: $userCoins Coins", color = FamilyGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        },
        confirmButton = {
            TextButton(onClick = { onDonate(selectedAmount) }) {
                Text("Donate $selectedAmount Coins", color = FamilyCyan, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = FamilySecondary) }
        },
    )
}

@Composable
private fun CreateFamilyDialog(
    onDismiss: () -> Unit,
    onCreate: (name: String, tag: String, sign: String, desc: String) -> Unit,
) {
    var name by remember { mutableStateOf("") }
    var tag by remember { mutableStateOf("") }
    var sign by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = FamilyWhite,
        shape = RoundedCornerShape(20.dp),
        title = { Text("Create New Family", color = FamilyInk, fontWeight = FontWeight.Bold) },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it.take(20) },
                    label = { Text("Family Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(Modifier.height(6.dp))
                OutlinedTextField(
                    value = tag,
                    onValueChange = { tag = it.uppercase().take(4) },
                    label = { Text("Tag (4 letters, e.g. NOVA)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(Modifier.height(6.dp))
                OutlinedTextField(
                    value = sign,
                    onValueChange = { sign = it.take(40) },
                    label = { Text("Motto / Sign") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        },
        confirmButton = {
            TextButton(
                enabled = name.isNotBlank() && tag.length >= 2,
                onClick = { onCreate(name.trim(), tag.trim(), sign.trim(), desc.trim()) }
            ) {
                Text("Create (500 Coins)", color = if (name.isNotBlank() && tag.length >= 2) FamilyCyan else FamilySecondary, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = FamilySecondary) }
        },
    )
}

@Composable
private fun FamilyShopBottomSheet(
    personalCoins: Long,
    items: List<FamilyShopItem>,
    onPurchase: (FamilyShopItem) -> Unit,
    onEquip: (FamilyShopItem) -> Unit,
    onDismiss: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("Family Shop", color = FamilyInk, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text("Coins: $personalCoins", color = FamilyCyan, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }

        Spacer(Modifier.height(12.dp))

        items.forEach { item ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(FamilyCanvas)
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(item.iconRes),
                        contentDescription = item.title,
                        modifier = Modifier.size(36.dp),
                    )
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(item.title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = FamilyInk)
                        Text(item.description, color = FamilySecondary, fontSize = 11.sp)
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (item.isOwned) Color(0xFF7B52E8) else FamilyCyan)
                        .clickable { if (item.isOwned) onEquip(item) else onPurchase(item) }
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                ) {
                    Text(
                        text = if (item.isEquipped) "Equipped" else if (item.isOwned) "Equip" else "${item.priceCoins} Coins",
                        color = FamilyWhite,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                    )
                }
            }
        }
    }
}

@Composable
private fun FamilyRankingsBottomSheet(
    rankings: List<FamilyRankingEntry>,
    onDismiss: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("Weekly Family Rankings", color = FamilyInk, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            TextButton(onClick = onDismiss) { Text("Close", color = FamilySecondary) }
        }

        Spacer(Modifier.height(10.dp))

        rankings.forEach { rank ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(FamilyCanvas)
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("#${rank.rank}", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = FamilyInk)
                    Spacer(Modifier.width(12.dp))
                    Image(
                        painter = painterResource(rank.emblemRes),
                        contentDescription = rank.name,
                        modifier = Modifier.size(32.dp),
                    )
                    Spacer(Modifier.width(10.dp))
                    Column {
                        Text(rank.name, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = FamilyInk)
                        Text("[${rank.tag}] • ${rank.memberCount} members", color = FamilySecondary, fontSize = 11.sp)
                    }
                }
                Text("${rank.weeklyActiveness} pts", color = FamilyCyan, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }
    }
}

@Composable
private fun ManageActionRow(
    title: String,
    iconRes: Int,
    isDanger: Boolean = false,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painter = painterResource(iconRes),
            contentDescription = title,
            modifier = Modifier.size(24.dp),
        )
        Spacer(Modifier.width(14.dp))
        Text(
            text = title,
            color = if (isDanger) FamilyDanger else FamilyInk,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
        )
    }
}
