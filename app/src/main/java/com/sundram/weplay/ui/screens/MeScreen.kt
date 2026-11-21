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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.graphics.SolidColor
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
import com.sundram.weplay.data.DemoBackpack
import com.sundram.weplay.data.FeaturedOfferState
import com.sundram.weplay.data.GiftPackOffer
import com.sundram.weplay.data.GiftPackRotationManager
import com.sundram.weplay.data.UserProfile
import com.sundram.weplay.data.ageFromDob
import com.sundram.weplay.ui.components.UserAvatar
import com.sundram.weplay.ui.components.bounceClick
import com.sundram.weplay.ui.nav.Routes

// ─────────────────────────────────────────────────────────────────────────────
//  Color Palette — PROJECT ME NOVA 2.0 (Light White / Lavender / Cyan / Gold)
// ─────────────────────────────────────────────────────────────────────────────
private val PageBg          = Color(0xFFFAF9FF)
private val CardBg          = Color(0xFFFFFFFF)
private val CardBorder      = Color(0xFFECE8FA)
private val TextInk         = Color(0xFF29263E)
private val TextSub         = Color(0xFF7F7B93)
private val BrandCyan       = Color(0xFF35CCD6)
private val BrandViolet     = Color(0xFF7656D9)
private val BrandGold       = Color(0xFFF5BD6C)
private val SoftLavender    = Color(0xFFEFE8FF)
private val DangerRed       = Color(0xFFFF4D4F)

/**
 * Project Me Page 2.0: Complete native Redesign of Screen 5.
 *
 * Implements a light white/lavender/cyan/gold theme with:
 * - Aurora hero banner, verified identity, copyable user ID, XP progress
 * - Social metrics strip (Friends, Following, Followers)
 * - Compact wallet & stats chips (Coins, Level, Badges)
 * - 4 Quick-Access Tiles: VIP Center, Shop, Avatar Studio, My Space
 * - Illustrated Badge Showcase (`me_nova_badge_*`) & Gift Showcase
 * - 48h Featured Gift Pack Offer Bar
 * - Grouped action cards (Activity & Social, Wallet & Collection, Account & Safety)
 * - Native sheets for VIP, Shop, Avatar Studio, My Space, Stats, Visitors, Language, Parental Controls
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MeScreen(
    nav: NavHostController,
    pad: PaddingValues,
    profile: UserProfile,
    onLogout: () -> Unit,
    onSaveProfile: (UserProfile) -> Unit = {},
) {
    val context = LocalContext.current
    var showLogoutDialog by rememberSaveable { mutableStateOf(false) }

    // Bottom Sheet States
    var showVipSheet by rememberSaveable { mutableStateOf(false) }
    var showShopSheet by rememberSaveable { mutableStateOf(false) }
    var showAvatarStudioSheet by rememberSaveable { mutableStateOf(false) }
    var showMySpaceSheet by rememberSaveable { mutableStateOf(false) }
    var showStatsSheet by rememberSaveable { mutableStateOf(false) }
    var showVisitorsSheet by rememberSaveable { mutableStateOf(false) }
    var showContributionsSheet by rememberSaveable { mutableStateOf(false) }
    var showLanguageSheet by rememberSaveable { mutableStateOf(false) }
    var showParentalControlsSheet by rememberSaveable { mutableStateOf(false) }
    var showGiftPackSheet by rememberSaveable { mutableStateOf(false) }
    var selectedBadgeForDetail by remember { mutableStateOf<BadgeItemData?>(null) }

    // 48h Featured Offer State
    val giftPackManager = remember(profile.id) { GiftPackRotationManager(context, profile.id) }
    val featuredOfferState = remember(profile.id) { giftPackManager.getFeaturedOffer() }

    // Avatar ring frame from backpack
    val ringColor = DemoBackpack.firstOrNull { it.id == profile.frame }?.frame?.let { Color(it) }

    // Back handling for nested sheets and dialogs
    BackHandler {
        when {
            showLogoutDialog -> showLogoutDialog = false
            selectedBadgeForDetail != null -> selectedBadgeForDetail = null
            showVipSheet -> showVipSheet = false
            showShopSheet -> showShopSheet = false
            showAvatarStudioSheet -> showAvatarStudioSheet = false
            showMySpaceSheet -> showMySpaceSheet = false
            showStatsSheet -> showStatsSheet = false
            showVisitorsSheet -> showVisitorsSheet = false
            showContributionsSheet -> showContributionsSheet = false
            showLanguageSheet -> showLanguageSheet = false
            showParentalControlsSheet -> showParentalControlsSheet = false
            showGiftPackSheet -> showGiftPackSheet = false
            else -> nav.popBackStack()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PageBg)
            .statusBarsPadding()
            .padding(bottom = pad.calculateBottomPadding())
            .verticalScroll(rememberScrollState()),
    ) {
        // ── 1. Profile Hero Section ──
        ProfileHeroSection(
            profile = profile,
            ringColor = ringColor,
            onAvatarClick = { showAvatarStudioSheet = true },
            onCopyId = {
                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                clipboard.setPrimaryClip(ClipData.newPlainText("User ID", profile.id))
                Toast.makeText(context, "ID ${profile.id} copied to clipboard", Toast.LENGTH_SHORT).show()
            },
            onShareProfile = {
                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, "Connect with ${profile.name} on the app! Profile ID: ${profile.id}")
                }
                context.startActivity(Intent.createChooser(shareIntent, "Share Profile"))
            },
            onEditProfile = { nav.navigate(Routes.PROFILE_EDIT) },
        )

        Spacer(Modifier.height(8.dp))

        // ── 2. Social Metrics Strip (Friends, Following, Followers) ──
        SocialMetricsStrip(
            onFriendsClick = { nav.navigate(Routes.MSG) },
            onFollowingClick = { nav.navigate(Routes.DISCOVER) },
            onFollowersClick = { nav.navigate(Routes.RANKING) },
        )

        Spacer(Modifier.height(10.dp))

        // ── 3. Wallet, Level & Badges Quick Bar ──
        WalletMetricsBar(
            coins = profile.coins,
            level = profile.level,
            onCoinsClick = { nav.navigate(Routes.TOPUP) },
            onLevelClick = { showStatsSheet = true },
            onBadgesClick = { nav.navigate(Routes.BACKPACK) },
        )

        Spacer(Modifier.height(14.dp))

        // ── 4. Four Core Quick-Access Tiles (VIP, Shop, Avatar Studio, My Space) ──
        QuickTilesGrid(
            onVipClick = { showVipSheet = true },
            onShopClick = { showShopSheet = true },
            onAvatarStudioClick = { showAvatarStudioSheet = true },
            onMySpaceClick = { showMySpaceSheet = true },
        )

        Spacer(Modifier.height(14.dp))

        // ── 5. Illustrated Badge Showcase ──
        BadgeShowcaseSection(
            onSeeAll = { nav.navigate(Routes.BACKPACK) },
            onBadgeClick = { badge -> selectedBadgeForDetail = badge },
        )

        Spacer(Modifier.height(14.dp))

        // ── 6. Gift Showcase ──
        GiftShowcaseSection(
            onViewGifts = { nav.navigate(Routes.GIFTS) },
        )

        Spacer(Modifier.height(14.dp))

        // ── 7. 48-Hour Featured Gift Pack Offer Bar ──
        FeaturedGiftPackBanner(
            offerTitle = featuredOfferState.offer.title,
            timeRemaining = featuredOfferState.formattedTimeRemaining,
            onOpenOffer = { showGiftPackSheet = true },
        )

        Spacer(Modifier.height(16.dp))

        // ── 8. Grouped Actions: Activity & Social ──
        ActionGroupCard(title = "Activity & Social") {
            ActionRowItem(
                iconRes = R.drawable.me_nova_ic_moments,
                title = "Moments",
                subtitle = "Your public updates and posts",
                onClick = { nav.navigate(Routes.DISCOVER) },
            )
            ActionDivider()
            ActionRowItem(
                iconRes = R.drawable.me_nova_ic_stats,
                title = "Game & Voice Stats",
                subtitle = "Wins, hours and level history",
                onClick = { showStatsSheet = true },
            )
            ActionDivider()
            ActionRowItem(
                iconRes = R.drawable.me_nova_ic_visitors,
                title = "Visitors",
                subtitle = "Recent guests and privacy",
                onClick = { showVisitorsSheet = true },
            )
            ActionDivider()
            ActionRowItem(
                iconRes = R.drawable.me_nova_ic_invite,
                title = "Invite Friends",
                subtitle = "Share your invite link",
                onClick = {
                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, "Join me to play and voice chat! Download the app: https://weplay.app/invite/${profile.id}")
                    }
                    context.startActivity(Intent.createChooser(shareIntent, "Invite Friends"))
                },
            )
            ActionDivider()
            ActionRowItem(
                iconRes = R.drawable.me_nova_ic_contributions,
                title = "Contributions",
                subtitle = "Room and guild support points",
                onClick = { showContributionsSheet = true },
            )
            ActionDivider()
            ActionRowItem(
                iconRes = R.drawable.me_nova_ic_family,
                title = "Family Hub",
                subtitle = profile.family ?: "Join or create a social family",
                onClick = { nav.navigate(Routes.FAMILY) },
            )
        }

        Spacer(Modifier.height(14.dp))

        // ── 9. Grouped Actions: Wallet & Collection ──
        ActionGroupCard(title = "Wallet & Collection") {
            ActionRowItem(
                iconRes = R.drawable.me_nova_ic_topup,
                title = "Top Up Coins",
                subtitle = "Add balance to your wallet",
                trailingText = "%,d".format(profile.coins),
                onClick = { nav.navigate(Routes.TOPUP) },
            )
            ActionDivider()
            ActionRowItem(
                iconRes = R.drawable.me_nova_ic_gifts,
                title = "My Gifts",
                subtitle = "Received and sent gift collection",
                onClick = { nav.navigate(Routes.GIFTS) },
            )
            ActionDivider()
            ActionRowItem(
                iconRes = R.drawable.me_nova_ic_backpack,
                title = "Backpack",
                subtitle = "Frames, bubbles, badges and cosmetics",
                onClick = { nav.navigate(Routes.BACKPACK) },
            )
        }

        Spacer(Modifier.height(14.dp))

        // ── 10. Grouped Actions: Account & Safety ──
        ActionGroupCard(title = "Account & Safety") {
            ActionRowItem(
                iconRes = R.drawable.me_nova_ic_edit_profile,
                title = "Edit Profile",
                subtitle = "Name, avatar, bio and birthday",
                onClick = { nav.navigate(Routes.PROFILE_EDIT) },
            )
            ActionDivider()
            ActionRowItem(
                iconRes = R.drawable.me_nova_ic_language,
                title = "Language",
                subtitle = "Change application language",
                trailingText = "English",
                onClick = { showLanguageSheet = true },
            )
            ActionDivider()
            ActionRowItem(
                iconRes = R.drawable.me_nova_ic_parental_controls,
                title = "Parental Control Mode",
                subtitle = "Safe mode and usage limits",
                onClick = { showParentalControlsSheet = true },
            )
            ActionDivider()
            ActionRowItem(
                iconRes = R.drawable.me_nova_ic_help,
                title = "Help Center",
                subtitle = "FAQs and support tickets",
                onClick = { nav.navigate(Routes.SUPPORT_SETTINGS) },
            )
            ActionDivider()
            ActionRowItem(
                iconRes = R.drawable.me_nova_ic_security,
                title = "Security Center",
                subtitle = "Password, login sessions and verification",
                onClick = { nav.navigate(Routes.ACCOUNT_SETTINGS) },
            )
            ActionDivider()
            ActionRowItem(
                iconRes = R.drawable.me_nova_ic_settings,
                title = "Settings",
                subtitle = "Notifications, audio and privacy",
                onClick = { nav.navigate(Routes.SETTINGS) },
            )
            ActionDivider()
            ActionRowItem(
                iconRes = R.drawable.me_nova_ic_about,
                title = "About",
                subtitle = "Version, legal terms and licenses",
                onClick = { nav.navigate(Routes.ABOUT) },
            )
            ActionDivider()
            ActionRowItem(
                iconRes = R.drawable.me_nova_ic_logout,
                title = "Log Out",
                subtitle = "Sign out from this device",
                tint = DangerRed,
                onClick = { showLogoutDialog = true },
            )
        }

        Spacer(Modifier.height(20.dp))

        // Footer info
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "Social Gaming Hub • Version 2.4.0 (Build 240)",
                color = TextSub.copy(alpha = 0.7f),
                fontSize = 11.sp,
            )
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  Interactive Modals & Bottom Sheets
    // ─────────────────────────────────────────────────────────────────────────

    // 1. Log Out Dialog
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            containerColor = CardBg,
            shape = RoundedCornerShape(20.dp),
            title = {
                Text("Log Out?", color = TextInk, fontWeight = FontWeight.Bold)
            },
            text = {
                Text("You will return to the welcome screen. Your saved preferences will be preserved on this device.", color = TextSub, fontSize = 13.sp)
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutDialog = false
                        onLogout()
                    }
                ) {
                    Text("Log Out", color = DangerRed, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancel", color = TextSub)
                }
            },
        )
    }

    // 2. VIP Center Bottom Sheet
    if (showVipSheet) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(
            onDismissRequest = { showVipSheet = false },
            sheetState = sheetState,
            containerColor = CardBg,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        ) {
            VipCenterBottomSheet(
                level = profile.level,
                onDismiss = { showVipSheet = false },
            )
        }
    }

    // 3. Shop Bottom Sheet
    if (showShopSheet) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(
            onDismissRequest = { showShopSheet = false },
            sheetState = sheetState,
            containerColor = CardBg,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        ) {
            ShopBottomSheet(
                coins = profile.coins,
                onTopUpClick = {
                    showShopSheet = false
                    nav.navigate(Routes.TOPUP)
                },
                onDismiss = { showShopSheet = false },
            )
        }
    }

    // 4. Avatar Studio Bottom Sheet
    if (showAvatarStudioSheet) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(
            onDismissRequest = { showAvatarStudioSheet = false },
            sheetState = sheetState,
            containerColor = CardBg,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        ) {
            AvatarStudioBottomSheet(
                profile = profile,
                onSelectPreset = { presetRes ->
                    // In local preview, save preset selection
                    Toast.makeText(context, "Avatar preset selected! [DEMO]", Toast.LENGTH_SHORT).show()
                },
                onEditDetails = {
                    showAvatarStudioSheet = false
                    nav.navigate(Routes.PROFILE_EDIT)
                },
                onDismiss = { showAvatarStudioSheet = false },
            )
        }
    }

    // 5. My Space Bottom Sheet
    if (showMySpaceSheet) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(
            onDismissRequest = { showMySpaceSheet = false },
            sheetState = sheetState,
            containerColor = CardBg,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        ) {
            MySpaceBottomSheet(
                profile = profile,
                onDismiss = { showMySpaceSheet = false },
            )
        }
    }

    // 6. Game & Voice Stats Bottom Sheet
    if (showStatsSheet) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(
            onDismissRequest = { showStatsSheet = false },
            sheetState = sheetState,
            containerColor = CardBg,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        ) {
            StatsBottomSheet(
                profile = profile,
                onDismiss = { showStatsSheet = false },
            )
        }
    }

    // 7. Profile Visitors Bottom Sheet
    if (showVisitorsSheet) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(
            onDismissRequest = { showVisitorsSheet = false },
            sheetState = sheetState,
            containerColor = CardBg,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        ) {
            VisitorsBottomSheet(
                onDismiss = { showVisitorsSheet = false },
            )
        }
    }

    // 8. Contributions Bottom Sheet
    if (showContributionsSheet) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(
            onDismissRequest = { showContributionsSheet = false },
            sheetState = sheetState,
            containerColor = CardBg,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        ) {
            ContributionsBottomSheet(
                onDismiss = { showContributionsSheet = false },
            )
        }
    }

    // 9. Language Selection Bottom Sheet
    if (showLanguageSheet) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(
            onDismissRequest = { showLanguageSheet = false },
            sheetState = sheetState,
            containerColor = CardBg,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        ) {
            LanguageBottomSheet(
                onDismiss = { showLanguageSheet = false },
            )
        }
    }

    // 10. Parental Controls Bottom Sheet
    if (showParentalControlsSheet) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(
            onDismissRequest = { showParentalControlsSheet = false },
            sheetState = sheetState,
            containerColor = CardBg,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        ) {
            ParentalControlsBottomSheet(
                onDismiss = { showParentalControlsSheet = false },
            )
        }
    }

    // 11. Gift Pack Offer Bottom Sheet
    if (showGiftPackSheet) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(
            onDismissRequest = { showGiftPackSheet = false },
            sheetState = sheetState,
            containerColor = CardBg,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        ) {
            GiftPackOfferBottomSheet(
                offer = featuredOfferState.offer,
                timeRemaining = featuredOfferState.formattedTimeRemaining,
                onClaim = {
                    Toast.makeText(context, "Starter Offer Claimed! [DEMO]", Toast.LENGTH_SHORT).show()
                    showGiftPackSheet = false
                },
                onDismiss = { showGiftPackSheet = false },
            )
        }
    }

    // 12. Badge Details Dialog
    selectedBadgeForDetail?.let { badge ->
        AlertDialog(
            onDismissRequest = { selectedBadgeForDetail = null },
            containerColor = CardBg,
            shape = RoundedCornerShape(20.dp),
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(badge.iconRes),
                        contentDescription = badge.name,
                        modifier = Modifier.size(36.dp),
                    )
                    Spacer(Modifier.width(10.dp))
                    Text(badge.name, color = TextInk, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                }
            },
            text = {
                Column {
                    Text(badge.description, color = TextSub, fontSize = 13.sp)
                    Spacer(Modifier.height(10.dp))
                    Text("Requirement: ${badge.requirement}", color = BrandViolet, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = if (badge.isEarned) "Status: Earned & Active" else "Status: Locked",
                        color = if (badge.isEarned) Color(0xFF00C853) else TextSub,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { selectedBadgeForDetail = null }) {
                    Text("Close", color = BrandViolet, fontWeight = FontWeight.Bold)
                }
            },
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  1. Profile Hero Section
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun ProfileHeroSection(
    profile: UserProfile,
    ringColor: Color?,
    onAvatarClick: () -> Unit,
    onCopyId: () -> Unit,
    onShareProfile: () -> Unit,
    onEditProfile: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFFE8E5F8), Color(0xFFF6F3FF), PageBg)
                )
            ),
    ) {
        // Decorative background header artwork
        Image(
            painter = painterResource(R.drawable.me_nova_bg_aurora),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp),
            contentScale = ContentScale.Crop,
            alpha = 0.35f,
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 12.dp),
        ) {
            // Top Action Row (Share + Edit)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Share Icon
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.85f))
                        .border(1.dp, CardBorder, CircleShape)
                        .bounceClick(onClick = onShareProfile),
                    contentAlignment = Alignment.Center,
                ) {
                    Image(
                        painter = painterResource(R.drawable.vb_vr_ic_share),
                        contentDescription = "Share Profile",
                        modifier = Modifier.size(16.dp),
                    )
                }

                Spacer(Modifier.width(8.dp))

                // Edit Profile Icon
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.85f))
                        .border(1.dp, CardBorder, CircleShape)
                        .bounceClick(onClick = onEditProfile),
                    contentAlignment = Alignment.Center,
                ) {
                    Image(
                        painter = painterResource(R.drawable.me_nova_ic_edit_profile),
                        contentDescription = "Edit Profile",
                        modifier = Modifier.size(18.dp),
                    )
                }
            }

            Spacer(Modifier.height(4.dp))

            // Profile Info Row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
            ) {
                // Avatar with Frame & Camera icon
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .bounceClick(onClick = onAvatarClick),
                    contentAlignment = Alignment.Center,
                ) {
                    UserAvatar(
                        name = profile.name,
                        photoPath = profile.photoPath,
                        size = 72.dp,
                        ring = ringColor,
                    )
                    // Camera / Studio badge
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(BrandViolet)
                            .border(1.5.dp, Color.White, CircleShape),
                        contentAlignment = Alignment.Center,
                    ) {
                        Image(
                            painter = painterResource(R.drawable.me_nova_ic_camera),
                            contentDescription = "Change photo",
                            modifier = Modifier.size(12.dp),
                        )
                    }
                }

                Spacer(Modifier.width(14.dp))

                // Name, Verified, Copyable ID, Age
                Column(Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = profile.name,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextInk,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        // Verified badge (Shown only when verified)
                        if (profile.phoneVerified || profile.emailVerified) {
                            Spacer(Modifier.width(6.dp))
                            Image(
                                painter = painterResource(R.drawable.me_nova_ic_verified),
                                contentDescription = "Verified Account",
                                modifier = Modifier.size(18.dp),
                            )
                        }
                    }

                    Spacer(Modifier.height(4.dp))

                    // Copyable ID Pill
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White.copy(alpha = 0.8f))
                            .border(1.dp, CardBorder, RoundedCornerShape(8.dp))
                            .clickable(onClick = onCopyId)
                            .padding(horizontal = 8.dp, vertical = 2.dp),
                    ) {
                        Text(
                            text = "ID: ${profile.id}",
                            fontSize = 11.sp,
                            color = TextSub,
                            fontWeight = FontWeight.Medium,
                        )
                        Spacer(Modifier.width(5.dp))
                        Image(
                            painter = painterResource(R.drawable.me_nova_ic_copy),
                            contentDescription = "Copy ID",
                            modifier = Modifier.size(11.dp),
                        )
                    }

                    Spacer(Modifier.height(6.dp))

                    // Online & Age Tag
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF00E676)),
                        )
                        Spacer(Modifier.width(5.dp))
                        Text("Online", fontSize = 11.sp, color = TextSub, fontWeight = FontWeight.Medium)
                        Spacer(Modifier.width(8.dp))
                        Text("•", fontSize = 10.sp, color = TextSub)
                        Spacer(Modifier.width(8.dp))
                        Text("Age ${ageFromDob(profile.dobMillis)}", fontSize = 11.sp, color = TextSub)
                        if (profile.gender.isNotBlank() && profile.gender != "Not specified") {
                            Spacer(Modifier.width(8.dp))
                            Text("•", fontSize = 10.sp, color = TextSub)
                            Spacer(Modifier.width(8.dp))
                            Text(profile.gender, fontSize = 11.sp, color = TextSub)
                        }
                    }
                }
            }

            Spacer(Modifier.height(14.dp))

            // Level & XP Progress Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White.copy(alpha = 0.85f))
                    .border(1.dp, CardBorder, RoundedCornerShape(12.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Lv.${profile.level}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = BrandViolet,
                )
                Spacer(Modifier.width(10.dp))
                LinearProgressIndicator(
                    progress = { 0.65f },
                    modifier = Modifier
                        .weight(1f)
                        .height(7.dp)
                        .clip(RoundedCornerShape(3.5.dp)),
                    color = BrandViolet,
                    trackColor = SoftLavender,
                )
                Spacer(Modifier.width(10.dp))
                Text(
                    text = "650 / 1000 XP",
                    fontSize = 10.sp,
                    color = TextSub,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  2. Social Metrics Strip
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun SocialMetricsStrip(
    onFriendsClick: () -> Unit,
    onFollowingClick: () -> Unit,
    onFollowersClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(CardBg)
            .border(1.dp, CardBorder, RoundedCornerShape(16.dp))
            .shadow(1.dp, RoundedCornerShape(16.dp), ambientColor = Color(0x08000000))
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        SocialMetricItem(count = "24", label = "Friends", iconRes = R.drawable.me_nova_ic_friends, onClick = onFriendsClick)
        Box(Modifier.width(1.dp).height(28.dp).background(CardBorder))
        SocialMetricItem(count = "142", label = "Following", iconRes = R.drawable.me_nova_ic_following, onClick = onFollowingClick)
        Box(Modifier.width(1.dp).height(28.dp).background(CardBorder))
        SocialMetricItem(count = "380", label = "Followers", iconRes = R.drawable.me_nova_ic_followers, onClick = onFollowersClick)
    }
}

@Composable
private fun SocialMetricItem(
    count: String,
    label: String,
    iconRes: Int,
    onClick: () -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .bounceClick(scaleDown = 0.95f, onClick = onClick)
            .padding(horizontal = 16.dp),
    ) {
        Text(text = count, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextInk)
        Spacer(Modifier.height(2.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(iconRes),
                contentDescription = null,
                modifier = Modifier.size(12.dp),
            )
            Spacer(Modifier.width(4.dp))
            Text(text = label, fontSize = 11.sp, color = TextSub)
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  3. Wallet, Level & Badges Quick Bar
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun WalletMetricsBar(
    coins: Long,
    level: Int,
    onCoinsClick: () -> Unit,
    onLevelClick: () -> Unit,
    onBadgesClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(CardBg)
            .border(1.dp, CardBorder, RoundedCornerShape(16.dp))
            .padding(vertical = 12.dp, horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        // Coins
        Row(
            modifier = Modifier
                .weight(1f)
                .bounceClick(scaleDown = 0.96f, onClick = onCoinsClick),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFFFFF8E7)),
                contentAlignment = Alignment.Center,
            ) {
                Image(
                    painter = painterResource(R.drawable.me_nova_ic_wallet),
                    contentDescription = "Wallet Coins",
                    modifier = Modifier.size(20.dp),
                )
            }
            Spacer(Modifier.width(8.dp))
            Column {
                Text(text = "%,d".format(coins), color = TextInk, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(text = "Coins • Top up", color = BrandGold, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
            }
        }

        Box(Modifier.width(1.dp).height(28.dp).background(CardBorder))

        // Level
        Row(
            modifier = Modifier
                .weight(1f)
                .bounceClick(scaleDown = 0.96f, onClick = onLevelClick)
                .padding(start = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(SoftLavender),
                contentAlignment = Alignment.Center,
            ) {
                Image(
                    painter = painterResource(R.drawable.me_nova_ic_level),
                    contentDescription = "User Level",
                    modifier = Modifier.size(20.dp),
                )
            }
            Spacer(Modifier.width(8.dp))
            Column {
                Text(text = "Level $level", color = TextInk, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(text = "Stats & XP", color = BrandViolet, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
            }
        }

        Box(Modifier.width(1.dp).height(28.dp).background(CardBorder))

        // Badges
        Row(
            modifier = Modifier
                .weight(1f)
                .bounceClick(scaleDown = 0.96f, onClick = onBadgesClick)
                .padding(start = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFFE0F7FA)),
                contentAlignment = Alignment.Center,
            ) {
                Image(
                    painter = painterResource(R.drawable.me_nova_ic_badges),
                    contentDescription = "Badges",
                    modifier = Modifier.size(20.dp),
                )
            }
            Spacer(Modifier.width(8.dp))
            Column {
                Text(text = "6 Badges", color = TextInk, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(text = "Backpack", color = BrandCyan, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  4. Four Quick-Access Tiles (VIP, Shop, Avatar Studio, My Space)
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun QuickTilesGrid(
    onVipClick: () -> Unit,
    onShopClick: () -> Unit,
    onAvatarStudioClick: () -> Unit,
    onMySpaceClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        QuickTileCard(
            modifier = Modifier.weight(1f),
            title = "VIP Center",
            subtitle = "Exclusive perks",
            iconRes = R.drawable.me_nova_ic_vip,
            badge = "PREMIUM",
            badgeColor = BrandGold,
            onClick = onVipClick,
        )
        QuickTileCard(
            modifier = Modifier.weight(1f),
            title = "Shop",
            subtitle = "Frames & themes",
            iconRes = R.drawable.me_nova_ic_shop,
            onClick = onShopClick,
        )
        QuickTileCard(
            modifier = Modifier.weight(1f),
            title = "Avatar Studio",
            subtitle = "Custom styles",
            iconRes = R.drawable.me_nova_ic_avatar_studio,
            badge = "HOT",
            badgeColor = BrandViolet,
            onClick = onAvatarStudioClick,
        )
        QuickTileCard(
            modifier = Modifier.weight(1f),
            title = "My Space",
            subtitle = "Room & decor",
            iconRes = R.drawable.me_nova_ic_my_space,
            onClick = onMySpaceClick,
        )
    }
}

@Composable
private fun QuickTileCard(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String,
    iconRes: Int,
    badge: String? = null,
    badgeColor: Color = BrandViolet,
    onClick: () -> Unit,
) {
    Box(
        modifier = modifier
            .height(96.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(CardBg)
            .border(1.dp, CardBorder, RoundedCornerShape(16.dp))
            .bounceClick(scaleDown = 0.95f, onClick = onClick)
            .padding(8.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Image(
                painter = painterResource(iconRes),
                contentDescription = title,
                modifier = Modifier.size(32.dp),
            )
            Spacer(Modifier.height(5.dp))
            Text(
                text = title,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = TextInk,
                maxLines = 1,
            )
            Text(
                text = subtitle,
                fontSize = 9.sp,
                color = TextSub,
                maxLines = 1,
            )
        }

        if (badge != null) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .clip(RoundedCornerShape(4.dp))
                    .background(badgeColor)
                    .padding(horizontal = 4.dp, vertical = 1.dp),
            ) {
                Text(
                    text = badge,
                    color = Color.White,
                    fontSize = 7.sp,
                    fontWeight = FontWeight.ExtraBold,
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  5. Illustrated Badge Showcase
// ─────────────────────────────────────────────────────────────────────────────
data class BadgeItemData(
    val name: String,
    val iconRes: Int,
    val description: String,
    val requirement: String,
    val isEarned: Boolean,
)

private val ShowcaseBadgesList = listOf(
    BadgeItemData("Newcomer", R.drawable.me_nova_badge_newcomer, "Awarded for joining the community", "Complete sign-up", isEarned = true),
    BadgeItemData("Ludo Gamer", R.drawable.me_nova_badge_gamer, "Mastered casual tabletop games", "Win 5 board game matches", isEarned = true),
    BadgeItemData("Star Host", R.drawable.me_nova_badge_host, "Hosted engaging live voice lounges", "Host 2 hours on stage", isEarned = true),
    BadgeItemData("VIP Member", R.drawable.me_nova_badge_vip, "Supporter of the platform", "Unlock VIP Membership", isEarned = false),
    BadgeItemData("Collector", R.drawable.me_nova_badge_collector, "Amassed rare gifts & cosmetics", "Collect 10 unique gift items", isEarned = true),
    BadgeItemData("Family Guild", R.drawable.me_nova_badge_family, "Active family guild contributor", "Reach 1,000 family points", isEarned = false),
)

@Composable
private fun BadgeShowcaseSection(
    onSeeAll: () -> Unit,
    onBadgeClick: (BadgeItemData) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Badge Showcase",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = TextInk,
            )
            Text(
                text = "See all ›",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = BrandViolet,
                modifier = Modifier.clickable(onClick = onSeeAll),
            )
        }

        Spacer(Modifier.height(8.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            items(ShowcaseBadgesList, key = { it.name }) { badge ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .width(76.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(CardBg)
                        .border(1.dp, CardBorder, RoundedCornerShape(14.dp))
                        .bounceClick(scaleDown = 0.94f) { onBadgeClick(badge) }
                        .padding(8.dp),
                ) {
                    Image(
                        painter = painterResource(badge.iconRes),
                        contentDescription = badge.name,
                        modifier = Modifier.size(42.dp),
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = badge.name,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (badge.isEarned) TextInk else TextSub.copy(alpha = 0.6f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  6. Gift Showcase Section
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun GiftShowcaseSection(
    onViewGifts: () -> Unit,
) {
    val sampleGifts = listOf(
        Triple("Rose", R.drawable.vb_gift_rose, 18),
        Triple("Star", R.drawable.vb_gift_star, 9),
        Triple("Diamond", R.drawable.vb_gift_diamond, 4),
        Triple("Crown", R.drawable.vb_gift_crown, 1),
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Gift Showcase",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = TextInk,
            )
            Text(
                text = "View collection ›",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = BrandViolet,
                modifier = Modifier.clickable(onClick = onViewGifts),
            )
        }

        Spacer(Modifier.height(8.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            items(sampleGifts, key = { it.first }) { (name, iconRes, count) ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(CardBg)
                        .border(1.dp, CardBorder, RoundedCornerShape(12.dp))
                        .bounceClick(scaleDown = 0.95f, onClick = onViewGifts)
                        .padding(horizontal = 10.dp, vertical = 7.dp),
                ) {
                    Image(
                        painter = painterResource(iconRes),
                        contentDescription = name,
                        modifier = Modifier.size(24.dp),
                    )
                    Spacer(Modifier.width(8.dp))
                    Column {
                        Text(text = name, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextInk)
                        Text(text = "×$count", fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFFFF4081))
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  7. 48-Hour Featured Gift Pack Offer Banner
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun FeaturedGiftPackBanner(
    offerTitle: String,
    timeRemaining: String,
    onOpenOffer: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.horizontalGradient(
                    listOf(Color(0xFF7656D9), Color(0xFF35CCD6))
                )
            )
            .bounceClick(scaleDown = 0.98f, onClick = onOpenOffer)
            .padding(14.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.25f)),
                contentAlignment = Alignment.Center,
            ) {
                Image(
                    painter = painterResource(R.drawable.me_nova_ic_gift_pack),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                )
            }

            Spacer(Modifier.width(12.dp))

            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "48H FEATURED OFFER",
                        color = Color(0xFFFFD54F),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 0.5.sp,
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = "⏳ $timeRemaining",
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 10.sp,
                    )
                }
                Spacer(Modifier.height(2.dp))
                Text(
                    text = offerTitle,
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White)
                    .padding(horizontal = 12.dp, vertical = 6.dp),
            ) {
                Text(
                    text = "View",
                    color = BrandViolet,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  8. Grouped Action Card & Action Row Item
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun ActionGroupCard(
    title: String,
    content: @Composable () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
    ) {
        Text(
            text = title,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = TextInk,
            modifier = Modifier.padding(start = 4.dp, bottom = 6.dp),
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(CardBg)
                .border(1.dp, CardBorder, RoundedCornerShape(18.dp))
                .padding(vertical = 4.dp),
        ) {
            content()
        }
    }
}

@Composable
private fun ActionRowItem(
    iconRes: Int,
    title: String,
    subtitle: String,
    trailingText: String? = null,
    tint: Color = TextInk,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(SoftLavender.copy(alpha = 0.6f)),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(iconRes),
                contentDescription = title,
                modifier = Modifier.size(20.dp),
            )
        }

        Spacer(Modifier.width(12.dp))

        Column(Modifier.weight(1f)) {
            Text(
                text = title,
                color = tint,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(Modifier.height(1.dp))
            Text(
                text = subtitle,
                color = TextSub,
                fontSize = 11.sp,
            )
        }

        if (trailingText != null) {
            Text(
                text = trailingText,
                color = BrandViolet,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.width(6.dp))
        }

        Image(
            painter = painterResource(R.drawable.me_nova_ic_chevron),
            contentDescription = null,
            modifier = Modifier.size(14.dp),
        )
    }
}

@Composable
private fun ActionDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(horizontal = 14.dp),
        thickness = 0.8.dp,
        color = CardBorder.copy(alpha = 0.6f),
    )
}

// ─────────────────────────────────────────────────────────────────────────────
//  9. Interactive Sheets Implementation
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun VipCenterBottomSheet(
    level: Int,
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
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(R.drawable.me_nova_ic_vip),
                    contentDescription = null,
                    modifier = Modifier.size(28.dp),
                )
                Spacer(Modifier.width(8.dp))
                Text("VIP Center", color = TextInk, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
            TextButton(onClick = onDismiss) { Text("Close", color = TextSub) }
        }

        Spacer(Modifier.height(8.dp))
        Text(
            text = "Unlock exclusive privileges, golden room badges, animated stage entrances and priority support.",
            color = TextSub,
            fontSize = 12.sp,
        )

        Spacer(Modifier.height(16.dp))

        // VIP Tier Cards
        val tiers = listOf(
            Triple("Silver VIP", "Lv.1 - 5", "5% coin discount • Silver Frame"),
            Triple("Gold VIP", "Lv.6 - 15", "10% coin discount • Golden Crown Frame"),
            Triple("Platinum VIP", "Lv.16 - 25", "20% coin bonus • Stage Entry Splash"),
            Triple("Diamond VIP", "Lv.26+", "Custom animated frame • Personal VIP Concierge"),
        )
        tiers.forEach { (name, req, perk) ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFFAF7EE))
                    .border(1.dp, BrandGold.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                    .padding(12.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Column {
                        Text(name, color = TextInk, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text(perk, color = TextSub, fontSize = 11.sp)
                    }
                    Text(req, color = BrandGold, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }
            }
        }

        Spacer(Modifier.height(16.dp))
        Text(
            text = "• VIP purchases require Google Play Billing verification [DEMO MODE]",
            color = TextSub,
            fontSize = 11.sp,
        )
        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun ShopBottomSheet(
    coins: Long,
    onTopUpClick: () -> Unit,
    onDismiss: () -> Unit,
) {
    var selectedCategory by remember { mutableStateOf("Frames") }
    val categories = listOf("Frames", "Bubbles", "Themes", "Effects")

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
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(R.drawable.me_nova_ic_shop),
                    contentDescription = null,
                    modifier = Modifier.size(28.dp),
                )
                Spacer(Modifier.width(8.dp))
                Text("Cosmetics Shop", color = TextInk, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
            TextButton(onClick = onDismiss) { Text("Close", color = TextSub) }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("Balance: %,d coins".format(coins), color = BrandGold, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Text(
                "Top Up Coins ›",
                color = BrandViolet,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                modifier = Modifier.clickable(onClick = onTopUpClick),
            )
        }

        Spacer(Modifier.height(12.dp))

        // Category Filter
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            categories.forEach { cat ->
                val active = cat == selectedCategory
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (active) BrandViolet else SoftLavender)
                        .clickable { selectedCategory = cat }
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                ) {
                    Text(cat, color = if (active) Color.White else TextInk, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // Sample Catalog Items
        val items = listOf(
            Triple("Aurora Neon Frame", 200, R.drawable.me_nova_ic_frame),
            Triple("Golden Crown Frame", 500, R.drawable.me_nova_ic_crown),
            Triple("Celestial Night Theme", 350, R.drawable.me_nova_bg_pearl),
            Triple("Diamond Sparkle Entry", 800, R.drawable.me_nova_ic_diamond),
        )
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            items(items) { (name, price, icon) ->
                Column(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(PageBg)
                        .border(1.dp, CardBorder, RoundedCornerShape(12.dp))
                        .padding(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Image(
                        painter = painterResource(icon),
                        contentDescription = name,
                        modifier = Modifier.size(36.dp),
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(name, color = TextInk, fontSize = 11.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                    Text("$price coins", color = BrandGold, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun AvatarStudioBottomSheet(
    profile: UserProfile,
    onSelectPreset: (Int) -> Unit,
    onEditDetails: () -> Unit,
    onDismiss: () -> Unit,
) {
    val presetAvatars = listOf(
        R.drawable.me_nova_avatar_01,
        R.drawable.me_nova_avatar_02,
        R.drawable.me_nova_avatar_03,
        R.drawable.me_nova_avatar_04,
        R.drawable.me_nova_avatar_05,
        R.drawable.me_nova_avatar_06,
        R.drawable.me_nova_avatar_07,
        R.drawable.me_nova_avatar_08,
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("Avatar Studio", color = TextInk, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            TextButton(onClick = onDismiss) { Text("Close", color = TextSub) }
        }

        Spacer(Modifier.height(6.dp))
        Text(
            text = "Select from original illustrated vector presets or upload a custom photo in Edit Profile.",
            color = TextSub,
            fontSize = 12.sp,
            textAlign = TextAlign.Start,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(Modifier.height(16.dp))

        // 8 Preset Avatars Grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            items(presetAvatars) { avatarRes ->
                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(PageBg)
                        .border(1.5.dp, SoftLavender, RoundedCornerShape(16.dp))
                        .bounceClick { onSelectPreset(avatarRes) },
                    contentAlignment = Alignment.Center,
                ) {
                    Image(
                        painter = painterResource(avatarRes),
                        contentDescription = "Avatar Preset",
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop,
                    )
                }
            }
        }

        Spacer(Modifier.height(14.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
                .clip(RoundedCornerShape(22.dp))
                .background(BrandViolet)
                .bounceClick(onClick = onEditDetails),
            contentAlignment = Alignment.Center,
        ) {
            Text("Upload Custom Photo in Edit Profile", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun MySpaceBottomSheet(
    profile: UserProfile,
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
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(R.drawable.me_nova_ic_my_space),
                    contentDescription = null,
                    modifier = Modifier.size(28.dp),
                )
                Spacer(Modifier.width(8.dp))
                Text("My Space", color = TextInk, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
            TextButton(onClick = onDismiss) { Text("Close", color = TextSub) }
        }

        Spacer(Modifier.height(6.dp))
        Text(
            text = "Personal room profile, space decorations, and visitor highlights.",
            color = TextSub,
            fontSize = 12.sp,
        )

        Spacer(Modifier.height(16.dp))

        // Space preview card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
                .clip(RoundedCornerShape(16.dp)),
        ) {
            Image(
                painter = painterResource(R.drawable.me_nova_bg_pearl),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0x33000000))
                    .padding(14.dp),
                verticalArrangement = Arrangement.Bottom,
            ) {
                Text(text = "${profile.name}'s Cozy Lounge", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Text(text = "Theme: Pearl Aurora • Status: Open to Friends", color = Color.White.copy(alpha = 0.85f), fontSize = 11.sp)
            }
        }

        Spacer(Modifier.height(20.dp))
    }
}

@Composable
private fun StatsBottomSheet(
    profile: UserProfile,
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
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(R.drawable.me_nova_ic_stats),
                    contentDescription = null,
                    modifier = Modifier.size(28.dp),
                )
                Spacer(Modifier.width(8.dp))
                Text("Game & Voice Stats", color = TextInk, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
            TextButton(onClick = onDismiss) { Text("Close", color = TextSub) }
        }

        Spacer(Modifier.height(12.dp))

        val stats = listOf(
            Triple("Matches Played", "48", "Ludo & Spy games"),
            Triple("Victory Count", "31", "64.5% win rate"),
            Triple("Voice Lounge Time", "14.5 hrs", "Host & listener"),
            Triple("Login Streak", "7 Days", "Active weekly streak"),
        )
        stats.forEach { (label, value, sub) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(PageBg)
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column {
                    Text(label, color = TextInk, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Text(sub, color = TextSub, fontSize = 11.sp)
                }
                Text(value, color = BrandViolet, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
            }
        }

        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun VisitorsBottomSheet(
    onDismiss: () -> Unit,
) {
    var allowVisitors by remember { mutableStateOf(true) }

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
            Text("Profile Visitors", color = TextInk, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            TextButton(onClick = onDismiss) { Text("Close", color = TextSub) }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(Modifier.weight(1f)) {
                Text("Record Profile Visits", color = TextInk, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                Text("Allow others to see when you view their profiles", color = TextSub, fontSize = 11.sp)
            }
            Switch(
                checked = allowVisitors,
                onCheckedChange = { allowVisitors = it },
                colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = BrandViolet),
            )
        }

        Spacer(Modifier.height(14.dp))

        Text("Recent Visitors (Today)", color = TextSub, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))

        val visitors = listOf(
            Pair("Arjun", "15m ago"),
            Pair("Tara", "2h ago"),
            Pair("Vikram", "Yesterday"),
        )
        visitors.forEach { (name, time) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(SoftLavender),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(name.take(1), color = BrandViolet, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                    Spacer(Modifier.width(10.dp))
                    Text(name, color = TextInk, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }
                Text(time, color = TextSub, fontSize = 11.sp)
            }
        }

        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun ContributionsBottomSheet(
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
            Text("Contributions", color = TextInk, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            TextButton(onClick = onDismiss) { Text("Close", color = TextSub) }
        }

        Spacer(Modifier.height(8.dp))
        Text("Your community and family guild support milestones.", color = TextSub, fontSize = 12.sp)

        Spacer(Modifier.height(14.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(PageBg)
                .padding(14.dp),
        ) {
            Column {
                Text("Total Guild Score: 1,420 pts", color = TextInk, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(Modifier.height(4.dp))
                Text("• Gifting contributions: 850 pts", color = TextSub, fontSize = 12.sp)
                Text("• Room hosting contributions: 570 pts", color = TextSub, fontSize = 12.sp)
            }
        }

        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun LanguageBottomSheet(
    onDismiss: () -> Unit,
) {
    var selectedLanguage by remember { mutableStateOf("English") }
    val languages = listOf("English", "Hindi", "Español", "Français", "Deutsch", "日本語")

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
            Text("Select Language", color = TextInk, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            TextButton(onClick = onDismiss) { Text("Close", color = TextSub) }
        }

        Spacer(Modifier.height(10.dp))

        languages.forEach { lang ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { selectedLanguage = lang }
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(lang, color = TextInk, fontSize = 14.sp, fontWeight = if (lang == selectedLanguage) FontWeight.Bold else FontWeight.Normal)
                if (lang == selectedLanguage) {
                    Image(
                        painter = painterResource(R.drawable.me_nova_ic_verified),
                        contentDescription = "Selected",
                        modifier = Modifier.size(18.dp),
                    )
                }
            }
            HorizontalDivider(color = CardBorder.copy(alpha = 0.5f))
        }

        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun ParentalControlsBottomSheet(
    onDismiss: () -> Unit,
) {
    var safeModeEnabled by remember { mutableStateOf(false) }

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
            Text("Parental Control Mode", color = TextInk, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            TextButton(onClick = onDismiss) { Text("Close", color = TextSub) }
        }

        Spacer(Modifier.height(6.dp))
        Text(
            text = "Restrict voice chat with strangers, filter mature content, and set daily screen time limits.",
            color = TextSub,
            fontSize = 12.sp,
        )

        Spacer(Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(Modifier.weight(1f)) {
                Text("Enable Restricted Safe Mode", color = TextInk, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Text("Requires 4-digit guardian PIN to disable", color = TextSub, fontSize = 11.sp)
            }
            Switch(
                checked = safeModeEnabled,
                onCheckedChange = { safeModeEnabled = it },
                colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = BrandViolet),
            )
        }

        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun GiftPackOfferBottomSheet(
    offer: com.sundram.weplay.data.GiftPackOffer,
    timeRemaining: String,
    onClaim: () -> Unit,
    onDismiss: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("48-Hour Starter Offer", color = TextInk, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            TextButton(onClick = onDismiss) { Text("Close", color = TextSub) }
        }

        Spacer(Modifier.height(8.dp))

        Text(
            text = "Limited promotional pack • Expires in $timeRemaining",
            color = BrandGold,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
        )

        Spacer(Modifier.height(14.dp))

        Text(offer.title, color = TextInk, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
        Spacer(Modifier.height(4.dp))
        Text(offer.description, color = TextSub, fontSize = 13.sp)

        Spacer(Modifier.height(16.dp))

        // Items included in pack
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
        ) {
            offer.items.forEach { item ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(horizontal = 12.dp),
                ) {
                    Image(
                        painter = painterResource(item.iconRes),
                        contentDescription = item.name,
                        modifier = Modifier.size(44.dp),
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(item.name, color = TextInk, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Text("×${item.count}", color = BrandViolet, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold)
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(
                    Brush.horizontalGradient(listOf(BrandViolet, BrandCyan))
                )
                .bounceClick(onClick = onClaim),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "Claim Offer (₹${offer.demoPriceRupees}) [DEMO]",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
            )
        }

        Spacer(Modifier.height(16.dp))
    }
}
