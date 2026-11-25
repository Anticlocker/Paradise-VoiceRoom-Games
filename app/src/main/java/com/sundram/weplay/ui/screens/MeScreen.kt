package com.sundram.weplay.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.sundram.weplay.data.GiftPackOffer
import com.sundram.weplay.data.GiftPackRotationManager
import com.sundram.weplay.data.FrameTier
import com.sundram.weplay.data.UserProfile
import com.sundram.weplay.ui.components.AvatarWithFrame
import com.sundram.weplay.ui.components.PhoenixAvatarFrame
import com.sundram.weplay.ui.components.PhoenixFramePreview
import com.sundram.weplay.ui.components.bounceClick
import com.sundram.weplay.ui.nav.Routes

// ─────────────────────────────────────────────────────────────────────────────
//  Color Palette — ME PAGE AURORA PARADISE 3.0 ROYAL NIGHT EDITION
//  Approved palette matching Screenshot 1 & docs/color_tokens.json
// ─────────────────────────────────────────────────────────────────────────────
private val RoyalBackground    = Color(0xFF10052D) // Deep Midnight Background
private val RoyalPanel         = Color(0xFF251048) // Translucent Dark Royal Panel
private val RoyalPanelRaised   = Color(0xFF31135D) // Elevated Royal Surface
private val RoyalOutline       = Color(0xFFA758D8) // Lavender Glow Outline
private val RoyalTextPrimary   = Color(0xFFFFF9FF) // Pearl White Primary Text
private val RoyalTextSecondary = Color(0xFFD0BFE8) // Soft Lavender Secondary Text
private val RoyalGold          = Color(0xFFFFD36B) // Rich Champagne Gold
private val RoyalCyan          = Color(0xFF54CFFF) // Crystal Cyan
private val RoyalViolet        = Color(0xFFA057EB) // Vibrant Royal Violet
private val RoyalPink          = Color(0xFFFF78C4) // Sunset Pink Accent
private val RoyalDivider       = Color(0xFF6A338E).copy(alpha = 0.45f) // Soft Divider
private val RoyalDanger        = Color(0xFFFF5271) // Coral-red for Log Out
private val RoyalCardBg        = Color(0xFF1E0A3C).copy(alpha = 0.90f)

/**
 * MeScreen (ME Page Royal Night Edition — Aurora Paradise 3.0)
 *
 * Implements a deep royal celestial fantasy profile hub matching Screenshot 1:
 * - Moonlit celestial palace header backdrop with moonlight and architecture
 * - Actual user profile picture with equipped frame (supporting Phoenix Rebirth V2)
 * - Username, optional verified badge, copyable ID with toast, level pill & real XP progression
 * - Four glossy illustrated shortcuts (VIP Center, Shop, Avatar Studio, My Space)
 * - Social statistics strip (Friends, Following, Followers, and Top Up with NO balance number)
 * - Grouped translucent royal violet menu cards (Moments through Log Out)
 * - Non-obstructing 48-hour promotional offer card within the scrollable content
 * - Full continuous vertical scrolling with a single LazyColumn
 * - Consistent dark royal themed bottom sheets
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
    var showBadgesSheet by rememberSaveable { mutableStateOf(false) }
    var showLanguageSheet by rememberSaveable { mutableStateOf(false) }
    var showParentalControlsSheet by rememberSaveable { mutableStateOf(false) }
    var showGiftPackSheet by rememberSaveable { mutableStateOf(false) }
    var showFollowingSheet by rememberSaveable { mutableStateOf(false) }
    var showFollowersSheet by rememberSaveable { mutableStateOf(false) }

    // 48-Hour Featured Offer Manager
    val giftPackManager = remember(profile.id) { GiftPackRotationManager(context, profile.id) }
    val featuredOfferState = remember(profile.id) { giftPackManager.getFeaturedOffer() }

    // Back handling in LIFO order
    BackHandler {
        when {
            showLogoutDialog -> showLogoutDialog = false
            showVipSheet -> showVipSheet = false
            showShopSheet -> showShopSheet = false
            showAvatarStudioSheet -> showAvatarStudioSheet = false
            showMySpaceSheet -> showMySpaceSheet = false
            showStatsSheet -> showStatsSheet = false
            showVisitorsSheet -> showVisitorsSheet = false
            showContributionsSheet -> showContributionsSheet = false
            showBadgesSheet -> showBadgesSheet = false
            showLanguageSheet -> showLanguageSheet = false
            showParentalControlsSheet -> showParentalControlsSheet = false
            showGiftPackSheet -> showGiftPackSheet = false
            showFollowingSheet -> showFollowingSheet = false
            showFollowersSheet -> showFollowersSheet = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(RoyalBackground)
            .padding(bottom = pad.calculateBottomPadding()),
    ) {
        // Deep midnight atmospheric night sky background behind scrollable body
        Image(
            painter = painterResource(R.drawable.ap_me_body_night),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            alpha = 0.40f,
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 28.dp),
        ) {
            // ── 1. Royal Moonlit Palace Header ──
            item(key = "royal_header") {
                RoyalProfileHeader(
                    profile = profile,
                    onEditProfile = { nav.navigate(Routes.PROFILE_EDIT) },
                    onAvatarClick = { showAvatarStudioSheet = true },
                    onSettingsClick = { nav.navigate(Routes.SETTINGS) },
                    onCopyId = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        clipboard.setPrimaryClip(ClipData.newPlainText("User ID", profile.id))
                        Toast.makeText(context, "ID copied", Toast.LENGTH_SHORT).show()
                    },
                )
            }

            // ── 2. Four Illustrated Quick Shortcuts (VIP, Shop, Avatar Studio, My Space) ──
            item(key = "royal_shortcuts") {
                RoyalShortcutsRow(
                    onVipClick = { showVipSheet = true },
                    onShopClick = { showShopSheet = true },
                    onAvatarStudioClick = { showAvatarStudioSheet = true },
                    onMySpaceClick = { showMySpaceSheet = true },
                )
            }

            // ── 3. Social Statistics & Top Up Strip (MANDATORY: Top Up without balance number!) ──
            item(key = "royal_social_strip") {
                RoyalSocialMicroStrip(
                    onFriendsClick = { nav.navigate(Routes.MSG) },
                    onFollowingClick = { showFollowingSheet = true },
                    onFollowersClick = { showFollowersSheet = true },
                    onTopUpClick = { nav.navigate(Routes.TOPUP) },
                )
            }

            // ── 4. Group A: Activity & Social (Moments, Game & Voice Stats, Visitors) ──
            item(key = "group_activity") {
                RoyalMenuGroup(
                    items = listOf(
                        RoyalMenuItemData(
                            iconRes = R.drawable.ap_me_moments,
                            title = "Moments",
                            subtitle = "Posts & Feed",
                            onClick = { nav.navigate(Routes.DISCOVER) },
                        ),
                        RoyalMenuItemData(
                            iconRes = R.drawable.ap_me_stats,
                            title = "Game & Voice Stats",
                            subtitle = "Level ${profile.level}",
                            onClick = { showStatsSheet = true },
                        ),
                        RoyalMenuItemData(
                            iconRes = R.drawable.ap_me_visitors,
                            title = "Visitors",
                            subtitle = "Recent visits",
                            onClick = { showVisitorsSheet = true },
                        ),
                    ),
                )
            }

            // ── 5. Group B: Community & Growth (Invite, Badges, Contributions, Family) ──
            item(key = "group_growth") {
                RoyalMenuGroup(
                    items = listOf(
                        RoyalMenuItemData(
                            iconRes = R.drawable.ap_me_invite,
                            title = "Invite Friends",
                            subtitle = "Share with friends",
                            onClick = {
                                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(
                                        Intent.EXTRA_TEXT,
                                        "Join me in rooms and play games together! Use code: ${profile.id}"
                                    )
                                }
                                context.startActivity(Intent.createChooser(shareIntent, "Invite Friends"))
                            },
                        ),
                        RoyalMenuItemData(
                            iconRes = R.drawable.ap_me_badges,
                            title = "Badge Center",
                            subtitle = "6 Badges",
                            onClick = { showBadgesSheet = true },
                        ),
                        RoyalMenuItemData(
                            iconRes = R.drawable.ap_me_contributions,
                            title = "Contributions",
                            subtitle = "Guild milestones",
                            onClick = { showContributionsSheet = true },
                        ),
                        RoyalMenuItemData(
                            iconRes = R.drawable.ap_me_family,
                            title = "Family Hub",
                            subtitle = profile.family ?: "Join Guild",
                            onClick = { nav.navigate(Routes.FAMILY) },
                        ),
                    ),
                )
            }

            // ── 6. Group C: Wallet & Collections (Wallet & Top Up, My Gifts, Backpack) ──
            item(key = "group_assets") {
                RoyalMenuGroup(
                    items = listOf(
                        RoyalMenuItemData(
                            iconRes = R.drawable.ap_me_wallet,
                            title = "Wallet & Top Up",
                            subtitle = "${profile.coins} Coins",
                            onClick = { nav.navigate(Routes.TOPUP) },
                        ),
                        RoyalMenuItemData(
                            iconRes = R.drawable.ap_me_gifts,
                            title = "My Gifts",
                            subtitle = "Collection",
                            onClick = { nav.navigate(Routes.GIFTS) },
                        ),
                        RoyalMenuItemData(
                            iconRes = R.drawable.ap_me_backpack,
                            title = "Backpack",
                            subtitle = "Frames & Bubbles",
                            onClick = { nav.navigate(Routes.BACKPACK) },
                        ),
                    ),
                )
            }

            // ── 7. Non-Obstructing 48h Promotional Offer Banner (Inside Scrollable List) ──
            item(key = "promotional_offer") {
                RoyalPromotionalOfferBanner(
                    title = featuredOfferState.offer.title,
                    timeRemaining = featuredOfferState.formattedTimeRemaining,
                    onClick = { showGiftPackSheet = true },
                )
            }

            // ── 8. Group D: Account & Safety (Language, Parental, Help, Security, Settings) ──
            item(key = "group_settings") {
                RoyalMenuGroup(
                    items = listOf(
                        RoyalMenuItemData(
                            iconRes = R.drawable.ap_me_language,
                            title = "Language",
                            subtitle = "English",
                            onClick = { showLanguageSheet = true },
                        ),
                        RoyalMenuItemData(
                            iconRes = R.drawable.ap_me_parental,
                            title = "Parental Control Mode",
                            subtitle = "Safe Mode",
                            onClick = { showParentalControlsSheet = true },
                        ),
                        RoyalMenuItemData(
                            iconRes = R.drawable.ap_me_help,
                            title = "Help Center",
                            subtitle = "FAQ & Support",
                            onClick = { nav.navigate(Routes.SUPPORT_SETTINGS) },
                        ),
                        RoyalMenuItemData(
                            iconRes = R.drawable.ap_me_security,
                            title = "Security Center",
                            subtitle = "Account Safety",
                            onClick = { nav.navigate(Routes.ACCOUNT_SETTINGS) },
                        ),
                        RoyalMenuItemData(
                            iconRes = R.drawable.ap_me_settings,
                            title = "Settings",
                            subtitle = null,
                            onClick = { nav.navigate(Routes.SETTINGS) },
                        ),
                    ),
                )
            }

            // ── 9. Group E: System & Logout (About, Log Out) ──
            item(key = "group_system") {
                RoyalMenuGroup(
                    items = listOf(
                        RoyalMenuItemData(
                            iconRes = R.drawable.ap_me_about,
                            title = "About",
                            subtitle = "v3.0.0",
                            onClick = { nav.navigate(Routes.ABOUT) },
                        ),
                        RoyalMenuItemData(
                            iconRes = R.drawable.ap_me_logout,
                            title = "Log Out",
                            subtitle = null,
                            isDanger = true,
                            onClick = { showLogoutDialog = true },
                        ),
                    ),
                )
            }

            item(key = "bottom_spacer") {
                Spacer(Modifier.height(16.dp))
            }
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  Interactive Bottom Sheets & Modals (Royal Dark Themed)
    // ─────────────────────────────────────────────────────────────────────────

    // 1. VIP Center Sheet
    if (showVipSheet) {
        ModalBottomSheet(
            onDismissRequest = { showVipSheet = false },
            containerColor = RoyalBackground,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        ) {
            RoyalVipBottomSheet(onDismiss = { showVipSheet = false })
        }
    }

    // 2. Shop Sheet
    if (showShopSheet) {
        ModalBottomSheet(
            onDismissRequest = { showShopSheet = false },
            containerColor = RoyalBackground,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        ) {
            RoyalShopBottomSheet(
                onTopUp = {
                    showShopSheet = false
                    nav.navigate(Routes.TOPUP)
                },
                onOpenRoomThemesShop = {
                    showShopSheet = false
                    nav.navigate("profile/gifts/themes")
                },
                onDismiss = { showShopSheet = false },
            )
        }
    }

    // 3. Avatar Studio Sheet (with demo avatars & Phoenix Rebirth integration)
    if (showAvatarStudioSheet) {
        ModalBottomSheet(
            onDismissRequest = { showAvatarStudioSheet = false },
            containerColor = RoyalBackground,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        ) {
            RoyalAvatarStudioBottomSheet(
                currentProfile = profile,
                onSave = { updatedProfile ->
                    onSaveProfile(updatedProfile)
                    showAvatarStudioSheet = false
                    Toast.makeText(context, "Avatar updated", Toast.LENGTH_SHORT).show()
                },
                onDismiss = { showAvatarStudioSheet = false },
            )
        }
    }

    // 4. My Space Sheet
    if (showMySpaceSheet) {
        ModalBottomSheet(
            onDismissRequest = { showMySpaceSheet = false },
            containerColor = RoyalBackground,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        ) {
            RoyalMySpaceBottomSheet(onDismiss = { showMySpaceSheet = false })
        }
    }

    // 5. Stats Sheet
    if (showStatsSheet) {
        ModalBottomSheet(
            onDismissRequest = { showStatsSheet = false },
            containerColor = RoyalBackground,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        ) {
            RoyalStatsBottomSheet(profile = profile, onDismiss = { showStatsSheet = false })
        }
    }

    // 6. Visitors Sheet
    if (showVisitorsSheet) {
        ModalBottomSheet(
            onDismissRequest = { showVisitorsSheet = false },
            containerColor = RoyalBackground,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        ) {
            RoyalVisitorsBottomSheet(onDismiss = { showVisitorsSheet = false })
        }
    }

    // 7. Contributions Sheet
    if (showContributionsSheet) {
        ModalBottomSheet(
            onDismissRequest = { showContributionsSheet = false },
            containerColor = RoyalBackground,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        ) {
            RoyalContributionsBottomSheet(onDismiss = { showContributionsSheet = false })
        }
    }

    // 8. Badges Sheet
    if (showBadgesSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBadgesSheet = false },
            containerColor = RoyalBackground,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        ) {
            RoyalBadgesBottomSheet(onDismiss = { showBadgesSheet = false })
        }
    }

    // 9. Language Sheet
    if (showLanguageSheet) {
        ModalBottomSheet(
            onDismissRequest = { showLanguageSheet = false },
            containerColor = RoyalBackground,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        ) {
            RoyalLanguageBottomSheet(onDismiss = { showLanguageSheet = false })
        }
    }

    // 10. Parental Controls Sheet
    if (showParentalControlsSheet) {
        ModalBottomSheet(
            onDismissRequest = { showParentalControlsSheet = false },
            containerColor = RoyalBackground,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        ) {
            RoyalParentalControlsBottomSheet(onDismiss = { showParentalControlsSheet = false })
        }
    }

    // 11. Gift Pack Offer Sheet
    if (showGiftPackSheet) {
        ModalBottomSheet(
            onDismissRequest = { showGiftPackSheet = false },
            containerColor = RoyalBackground,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        ) {
            RoyalGiftPackOfferBottomSheet(
                offer = featuredOfferState.offer,
                timeRemaining = featuredOfferState.formattedTimeRemaining,
                onClaim = {
                    Toast.makeText(context, "Offer preview saved", Toast.LENGTH_SHORT).show()
                    showGiftPackSheet = false
                },
                onDismiss = { showGiftPackSheet = false },
            )
        }
    }

    // 12. Following List Sheet
    if (showFollowingSheet) {
        ModalBottomSheet(
            onDismissRequest = { showFollowingSheet = false },
            containerColor = RoyalBackground,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        ) {
            RoyalSocialListBottomSheet(
                title = "Following (142)",
                items = listOf("Aria Vance", "Kiran Patel", "Elena Rostova", "Marcus Brody"),
                onDismiss = { showFollowingSheet = false },
            )
        }
    }

    // 13. Followers List Sheet
    if (showFollowersSheet) {
        ModalBottomSheet(
            onDismissRequest = { showFollowersSheet = false },
            containerColor = RoyalBackground,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        ) {
            RoyalSocialListBottomSheet(
                title = "Followers (380)",
                items = listOf("Rohan Sharma", "Sofia Chen", "Zayd Malik", "Liam O'Connor"),
                onDismiss = { showFollowersSheet = false },
            )
        }
    }

    // 14. Logout Confirmation Dialog
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Log Out", color = RoyalTextPrimary, fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to log out of your account?", color = RoyalTextSecondary) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutDialog = false
                        onLogout()
                    }
                ) {
                    Text("Log Out", color = RoyalDanger, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancel", color = RoyalTextSecondary)
                }
            },
            containerColor = RoyalPanel,
            shape = RoundedCornerShape(20.dp),
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  1. Profile Header Component (Celestial Palace, ~225dp)
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun RoyalProfileHeader(
    profile: UserProfile,
    onEditProfile: () -> Unit,
    onAvatarClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onCopyId: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(230.dp),
    ) {
        // Celestial Palace Background
        Image(
            painter = painterResource(R.drawable.ap_me_header_palace),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
        )

        // Gradient overlay to guarantee high contrast for texts and stats
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            RoyalBackground.copy(alpha = 0.50f),
                            RoyalBackground,
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 20.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            // Top Action Row (Edit Profile & Settings Shortcuts)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Edit Profile Shortcut
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(RoyalPanel.copy(alpha = 0.85f))
                        .border(1.dp, RoyalOutline.copy(alpha = 0.40f), CircleShape)
                        .bounceClick(onClick = onEditProfile),
                    contentAlignment = Alignment.Center,
                ) {
                    Image(
                        painter = painterResource(R.drawable.ap_me_edit),
                        contentDescription = "Edit Profile",
                        modifier = Modifier.size(18.dp),
                    )
                }

                Spacer(Modifier.width(10.dp))

                // Settings Shortcut
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(RoyalPanel.copy(alpha = 0.85f))
                        .border(1.dp, RoyalOutline.copy(alpha = 0.40f), CircleShape)
                        .bounceClick(onClick = onSettingsClick),
                    contentAlignment = Alignment.Center,
                ) {
                    Image(
                        painter = painterResource(R.drawable.ap_me_settings),
                        contentDescription = "Settings",
                        modifier = Modifier.size(18.dp),
                    )
                }
            }

            // Main Profile Identity Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Avatar with Phoenix or Royal Frame Support
                Box(
                    modifier = Modifier
                        .size(88.dp)
                        .bounceClick(scaleDown = 0.94f, onClick = onAvatarClick),
                    contentAlignment = Alignment.Center,
                ) {
                    val isPhoenix = profile.frame.equals("phoenix", ignoreCase = true) ||
                            profile.frame.equals("PHOENIX_REBIRTH", ignoreCase = true)

                    if (isPhoenix) {
                        PhoenixAvatarFrame(
                            name = profile.name,
                            photoPath = profile.photoPath,
                            size = 88.dp,
                            animate = true,
                            contentDescription = "Phoenix Rebirth Avatar Frame",
                        )
                    } else {
                        AvatarWithFrame(
                            name = profile.name,
                            photoPath = profile.photoPath,
                            size = 82.dp,
                            frameTier = FrameTier.fromId(profile.frame),
                            animated = true,
                            showCrown = profile.level >= 20,
                        )
                    }

                    // Camera Action Badge
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .align(Alignment.BottomEnd)
                            .clip(CircleShape)
                            .background(RoyalPanel)
                            .border(1.dp, RoyalOutline, CircleShape),
                        contentAlignment = Alignment.Center,
                    ) {
                        Image(
                            painter = painterResource(R.drawable.ap_me_camera),
                            contentDescription = "Change Avatar",
                            modifier = Modifier.size(13.dp),
                        )
                    }
                }

                Spacer(Modifier.width(16.dp))

                // User Identity Info Column
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable(onClick = onEditProfile),
                ) {
                    // Display Name & Verified Badge
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(
                            text = profile.name.ifBlank { "User ${profile.id}" },
                            fontSize = 19.sp,
                            fontWeight = FontWeight.Bold,
                            color = RoyalTextPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )

                        // Verified Icon (Rendered ONLY if verified)
                        if (profile.phoneVerified || profile.emailVerified) {
                            Spacer(Modifier.width(6.dp))
                            Image(
                                painter = painterResource(R.drawable.ap_me_verified),
                                contentDescription = "Verified Account",
                                modifier = Modifier.size(17.dp),
                            )
                        }
                    }

                    Spacer(Modifier.height(4.dp))

                    // ID Row with Copy Action
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable(onClick = onCopyId)
                            .padding(vertical = 2.dp),
                    ) {
                        Text(
                            text = "ID: ${profile.id}",
                            fontSize = 12.sp,
                            color = RoyalTextSecondary,
                            fontWeight = FontWeight.Medium,
                        )
                        Spacer(Modifier.width(6.dp))
                        Image(
                            painter = painterResource(R.drawable.ap_me_copy),
                            contentDescription = "Copy ID",
                            modifier = Modifier.size(13.dp),
                        )
                    }

                    Spacer(Modifier.height(8.dp))

                    // Level & Real XP Progress (Calculated from level progression, NOT coins)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        // Ornate Level Pill
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(RoyalGold, Color(0xFFE5A82E))
                                    )
                                )
                                .padding(horizontal = 8.dp, vertical = 2.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = "Lv.${profile.level}",
                                color = RoyalBackground,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold,
                            )
                        }

                        Spacer(Modifier.width(8.dp))

                        val levelBaseXp = 1000
                        val currentXp = (profile.level * 350) % levelBaseXp
                        val xpProgress = (currentXp.toFloat() / levelBaseXp).coerceIn(0.12f, 0.95f)

                        Column(modifier = Modifier.weight(1f)) {
                            LinearProgressIndicator(
                                progress = { xpProgress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = RoyalCyan,
                                trackColor = RoyalPanelRaised,
                            )
                            Spacer(Modifier.height(2.dp))
                            Text(
                                text = "$currentXp / $levelBaseXp XP",
                                fontSize = 9.sp,
                                color = RoyalTextSecondary,
                            )
                        }
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  2. Four Illustrated Quick Shortcuts (VIP, Shop, Avatar Studio, My Space)
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun RoyalShortcutsRow(
    onVipClick: () -> Unit,
    onShopClick: () -> Unit,
    onAvatarStudioClick: () -> Unit,
    onMySpaceClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RoyalShortcutTile(
            iconRes = R.drawable.ap_me_vip,
            label = "VIP Center",
            onClick = onVipClick,
        )
        RoyalShortcutTile(
            iconRes = R.drawable.ap_me_shop,
            label = "Shop",
            onClick = onShopClick,
        )
        RoyalShortcutTile(
            iconRes = R.drawable.ap_me_avatar_studio,
            label = "Avatar Studio",
            onClick = onAvatarStudioClick,
        )
        RoyalShortcutTile(
            iconRes = R.drawable.ap_me_my_space,
            label = "My Space",
            onClick = onMySpaceClick,
        )
    }
}

@Composable
private fun RoyalShortcutTile(
    iconRes: Int,
    label: String,
    onClick: () -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .bounceClick(scaleDown = 0.94f, onClick = onClick)
            .padding(4.dp),
    ) {
        Box(
            modifier = Modifier
                .size(62.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(RoyalPanel.copy(alpha = 0.85f))
                .border(1.dp, RoyalOutline.copy(alpha = 0.35f), RoundedCornerShape(18.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(iconRes),
                contentDescription = label,
                modifier = Modifier.size(46.dp),
            )
        }
        Spacer(Modifier.height(6.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = RoyalTextPrimary,
            textAlign = TextAlign.Center,
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  3. Social Statistics & Top Up Strip (Friends, Following, Followers, Top Up)
//  MANDATORY REQUIREMENT: The fourth slot contains ONLY "Top Up" with NO 500 number!
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun RoyalSocialMicroStrip(
    onFriendsClick: () -> Unit,
    onFollowingClick: () -> Unit,
    onFollowersClick: () -> Unit,
    onTopUpClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(RoyalCardBg)
            .border(1.dp, RoyalOutline.copy(alpha = 0.32f), RoundedCornerShape(18.dp))
            .padding(vertical = 12.dp, horizontal = 14.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Friends
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable(onClick = onFriendsClick),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(text = "24", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = RoyalTextPrimary)
                Text(text = "Friends", fontSize = 11.sp, color = RoyalTextSecondary)
            }

            Box(Modifier.width(1.dp).height(24.dp).background(RoyalDivider))

            // Following
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable(onClick = onFollowingClick),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(text = "142", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = RoyalTextPrimary)
                Text(text = "Following", fontSize = 11.sp, color = RoyalTextSecondary)
            }

            Box(Modifier.width(1.dp).height(24.dp).background(RoyalDivider))

            // Followers
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable(onClick = onFollowersClick),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(text = "380", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = RoyalTextPrimary)
                Text(text = "Followers", fontSize = 11.sp, color = RoyalTextSecondary)
            }

            Box(Modifier.width(1.dp).height(24.dp).background(RoyalDivider))

            // Top Up (MANDATORY: Golden coin icon + "Top Up" text ONLY — NO 500 or any balance number!)
            Row(
                modifier = Modifier
                    .weight(1.1f)
                    .clip(RoundedCornerShape(12.dp))
                    .clickable(onClick = onTopUpClick)
                    .padding(horizontal = 4.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                Image(
                    painter = painterResource(R.drawable.ap_me_wallet),
                    contentDescription = "Top Up",
                    modifier = Modifier.size(22.dp),
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = "Top Up",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = RoyalGold,
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  4. Grouped Royal Menu Cards (Translucent dark royal panels, soft purple borders)
// ─────────────────────────────────────────────────────────────────────────────
data class RoyalMenuItemData(
    val iconRes: Int,
    val title: String,
    val subtitle: String?,
    val isDanger: Boolean = false,
    val onClick: () -> Unit,
)

@Composable
private fun RoyalMenuGroup(
    items: List<RoyalMenuItemData>,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(RoyalCardBg)
            .border(1.dp, RoyalOutline.copy(alpha = 0.28f), RoundedCornerShape(18.dp)),
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            items.forEachIndexed { index, item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .clickable(onClick = item.onClick)
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Image(
                        painter = painterResource(item.iconRes),
                        contentDescription = item.title,
                        modifier = Modifier.size(28.dp),
                    )

                    Spacer(Modifier.width(14.dp))

                    Text(
                        text = item.title,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (item.isDanger) RoyalDanger else RoyalTextPrimary,
                        modifier = Modifier.weight(1f),
                    )

                    if (item.subtitle != null) {
                        Text(
                            text = item.subtitle,
                            fontSize = 12.sp,
                            color = RoyalTextSecondary,
                        )
                        Spacer(Modifier.width(8.dp))
                    }

                    Image(
                        painter = painterResource(R.drawable.ap_me_chevron),
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                    )
                }

                if (index < items.size - 1) {
                    HorizontalDivider(
                        modifier = Modifier.padding(start = 58.dp, end = 16.dp),
                        thickness = 0.5.dp,
                        color = RoyalDivider,
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  5. Non-Obstructing 48h Promotional Offer Banner (Positioned inside list)
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun RoyalPromotionalOfferBanner(
    title: String,
    timeRemaining: String,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(
                Brush.horizontalGradient(
                    listOf(Color(0xFF5A1E8A), Color(0xFF231048), Color(0xFF144669))
                )
            )
            .border(1.dp, RoyalGold.copy(alpha = 0.55f), RoundedCornerShape(18.dp))
            .bounceClick(scaleDown = 0.98f, onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                painter = painterResource(R.drawable.ap_me_gifts),
                contentDescription = null,
                modifier = Modifier.size(34.dp),
            )

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = RoyalGold,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                )
                Text(
                    text = "Limited offer · $timeRemaining left",
                    color = RoyalTextSecondary,
                    fontSize = 11.sp,
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(RoyalGold)
                    .padding(horizontal = 12.dp, vertical = 6.dp),
            ) {
                Text(
                    text = "Claim",
                    color = RoyalBackground,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  6. Submenu Bottom Sheets (Royal Dark Fantasy Edition)
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun RoyalVipBottomSheet(onDismiss: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(RoyalBackground)
            .padding(horizontal = 20.dp, vertical = 16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("VIP Center", color = RoyalTextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            TextButton(onClick = onDismiss) { Text("Close", color = RoyalTextSecondary) }
        }

        Spacer(Modifier.height(10.dp))
        Text("Unlock royal privileges, exclusive room entrances, and custom badges.", color = RoyalTextSecondary, fontSize = 13.sp)

        Spacer(Modifier.height(16.dp))

        listOf(
            Triple("Free Tier", "Standard features & social lounge access", RoyalTextSecondary),
            Triple("Silver VIP", "Chat glow, 5% coin rebate, custom room badges", Color(0xFFC0C0D8)),
            Triple("Gold VIP", "Exclusive vehicle entrance, animated avatar frames", RoyalGold),
            Triple("Diamond Elite", "Host priority, room banner takeover, 24/7 concierge", RoyalCyan),
        ).forEach { (tier, perk, color) ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(RoyalPanel)
                    .border(1.dp, RoyalOutline.copy(alpha = 0.25f), RoundedCornerShape(14.dp))
                    .padding(12.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(color)
                    )
                    Spacer(Modifier.width(10.dp))
                    Column {
                        Text(tier, color = RoyalTextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text(perk, color = RoyalTextSecondary, fontSize = 11.sp)
                    }
                }
            }
        }

        Spacer(Modifier.height(20.dp))
    }
}

@Composable
private fun RoyalShopBottomSheet(
    onTopUp: () -> Unit,
    onOpenRoomThemesShop: () -> Unit,
    onDismiss: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(RoyalBackground)
            .padding(horizontal = 20.dp, vertical = 16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("Cosmetics Shop", color = RoyalTextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            TextButton(onClick = onDismiss) { Text("Close", color = RoyalTextSecondary) }
        }

        Spacer(Modifier.height(10.dp))
        Text("Customize your voice room persona with frames and enter effects.", color = RoyalTextSecondary, fontSize = 13.sp)

        Spacer(Modifier.height(14.dp))

        listOf(
            Triple("Cyan Halo Frame", "500 Coins", R.drawable.me_clean_frame),
            Triple("Gold Star Crown", "1,200 Coins", R.drawable.me_clean_crown),
            Triple("Royal Enter Shimmer", "2,500 Coins", R.drawable.ap_me_gifts),
        ).forEach { (title, price, icon) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(RoyalPanel)
                    .border(1.dp, RoyalOutline.copy(alpha = 0.25f), RoundedCornerShape(14.dp))
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(icon),
                        contentDescription = title,
                        modifier = Modifier.size(36.dp),
                    )
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(title, color = RoyalTextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text(price, color = RoyalGold, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(RoyalCyan)
                        .clickable(onClick = onTopUp)
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                ) {
                    Text("Acquire", color = RoyalBackground, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(Modifier.height(6.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(RoyalPanel)
                .border(1.dp, RoyalCyan.copy(alpha = 0.45f), RoundedCornerShape(14.dp))
                .clickable(onClick = onOpenRoomThemesShop)
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(R.drawable.room_thumb_sakura_garden),
                    contentDescription = "Room Themes",
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop,
                )
                Spacer(Modifier.width(12.dp))
                Column {
                    Text("Voice Room Themes", color = RoyalTextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text("Preview all 10 themes & Crest unlocks", color = RoyalTextSecondary, fontSize = 11.sp)
                }
            }
            Text("Open ›", color = RoyalCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun RoyalAvatarStudioBottomSheet(
    currentProfile: UserProfile,
    onSave: (UserProfile) -> Unit,
    onDismiss: () -> Unit,
) {
    // 6 Fictional Demo Avatars
    val demoAvatars = listOf(
        R.drawable.me_clean_demo_avatar_01,
        R.drawable.me_clean_demo_avatar_02,
        R.drawable.me_clean_demo_avatar_03,
        R.drawable.me_clean_demo_avatar_04,
        R.drawable.me_clean_demo_avatar_05,
        R.drawable.me_clean_demo_avatar_06,
    )

    var selectedIndex by remember { mutableIntStateOf(0) }
    var equipPhoenixFrame by remember {
        mutableStateOf(currentProfile.frame.equals("phoenix", ignoreCase = true))
    }
    var showMultiSizePreview by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(RoyalBackground)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("Avatar Studio", color = RoyalTextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            TextButton(onClick = onDismiss) { Text("Cancel", color = RoyalTextSecondary) }
        }

        Spacer(Modifier.height(8.dp))
        Text(
            text = "Select a style preset or equip the Phoenix Rebirth frame.",
            color = RoyalTextSecondary,
            fontSize = 12.sp,
        )

        Spacer(Modifier.height(16.dp))

        // Selected Preview
        if (equipPhoenixFrame) {
            PhoenixAvatarFrame(
                avatarResId = demoAvatars[selectedIndex],
                size = 104.dp,
                animate = true,
                contentDescription = "Equipped Phoenix Rebirth Frame",
            )
        } else {
            Box(
                modifier = Modifier
                    .size(92.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .border(2.dp, RoyalCyan, RoundedCornerShape(24.dp)),
                contentAlignment = Alignment.Center,
            ) {
                Image(
                    painter = painterResource(demoAvatars[selectedIndex]),
                    contentDescription = "Selected Avatar Preview",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        // Preset Grid Row
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
        ) {
            items(demoAvatars.size) { idx ->
                val isSelected = idx == selectedIndex
                Box(
                    modifier = Modifier
                        .padding(horizontal = 6.dp)
                        .size(54.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .border(
                            if (isSelected) 2.dp else 1.dp,
                            if (isSelected) RoyalCyan else RoyalOutline.copy(alpha = 0.3f),
                            RoundedCornerShape(14.dp),
                        )
                        .clickable { selectedIndex = idx }
                        .padding(2.dp),
                ) {
                    Image(
                        painter = painterResource(demoAvatars[idx]),
                        contentDescription = "Demo Avatar ${idx + 1}",
                        modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop,
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // Phoenix Rebirth V2 Frame Equip Toggle Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(RoyalPanel)
                .border(
                    1.dp,
                    if (equipPhoenixFrame) RoyalGold else RoyalOutline.copy(alpha = 0.35f),
                    RoundedCornerShape(14.dp),
                )
                .clickable { equipPhoenixFrame = !equipPhoenixFrame }
                .padding(horizontal = 14.dp, vertical = 10.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    PhoenixAvatarFrame(
                        avatarResId = demoAvatars[selectedIndex],
                        size = 38.dp,
                        animate = equipPhoenixFrame,
                    )
                    Spacer(Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                "Phoenix Rebirth V2",
                                color = if (equipPhoenixFrame) RoyalGold else RoyalTextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                            )
                            Spacer(Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color(0xFFFF7A00))
                                    .padding(horizontal = 5.dp, vertical = 2.dp),
                            ) {
                                Text("MYTHIC", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        Text(
                            if (equipPhoenixFrame) "Equipped & Animated" else "Tap to equip Phoenix frame",
                            color = if (equipPhoenixFrame) RoyalTextSecondary else RoyalTextSecondary.copy(alpha = 0.7f),
                            fontSize = 11.sp,
                        )
                    }
                }

                Switch(
                    checked = equipPhoenixFrame,
                    onCheckedChange = { equipPhoenixFrame = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = RoyalGold,
                        checkedTrackColor = Color(0xFFFF7A00),
                    ),
                )
            }
        }

        // Multi-Size Frame Preview (64dp, 96dp, 128dp) Toggle
        Spacer(Modifier.height(8.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showMultiSizePreview = !showMultiSizePreview }
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                if (showMultiSizePreview) "Hide size preview" else "Preview frame sizes",
                color = RoyalCyan,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }

        if (showMultiSizePreview) {
            Spacer(Modifier.height(8.dp))
            PhoenixFramePreview()
        }

        Spacer(Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(46.dp)
                .clip(RoundedCornerShape(23.dp))
                .background(RoyalCyan)
                .bounceClick {
                    val updated = currentProfile.copy(
                        avatarId = "clean_demo_${selectedIndex + 1}",
                        frame = if (equipPhoenixFrame) "phoenix" else if (currentProfile.frame.equals("phoenix", ignoreCase = true)) null else currentProfile.frame,
                    )
                    onSave(updated)
                },
            contentAlignment = Alignment.Center,
        ) {
            Text("Apply Avatar & Frame", color = RoyalBackground, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }

        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun RoyalMySpaceBottomSheet(onDismiss: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(RoyalBackground)
            .padding(horizontal = 20.dp, vertical = 16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("My Space", color = RoyalTextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            TextButton(onClick = onDismiss) { Text("Close", color = RoyalTextSecondary) }
        }

        Spacer(Modifier.height(8.dp))
        Text("Your personalized lounge space. Decorate furniture, stage showcases, and invite friends.", color = RoyalTextSecondary, fontSize = 12.sp)

        Spacer(Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(RoyalPanel)
                .border(1.dp, RoyalOutline.copy(alpha = 0.3f), RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Image(
                    painter = painterResource(R.drawable.ap_me_my_space),
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                )
                Spacer(Modifier.height(8.dp))
                Text("Personal Space Lounge", color = RoyalTextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text("Custom 3D room themes arriving in upcoming season update.", color = RoyalTextSecondary, fontSize = 11.sp)
            }
        }

        Spacer(Modifier.height(20.dp))
    }
}

@Composable
private fun RoyalStatsBottomSheet(profile: UserProfile, onDismiss: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(RoyalBackground)
            .padding(horizontal = 20.dp, vertical = 16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("Game & Voice Stats", color = RoyalTextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            TextButton(onClick = onDismiss) { Text("Close", color = RoyalTextSecondary) }
        }

        Spacer(Modifier.height(12.dp))

        listOf(
            Triple("Ludo Matches", "42 played • 28 won (66% win rate)", RoyalCyan),
            Triple("Spy Fall Rounds", "19 played • 14 won", RoyalViolet),
            Triple("Voice Lounge Hours", "18.5 hours hosted", RoyalGold),
            Triple("Total Room XP", "${profile.level * 850} lifetime XP", RoyalTextSecondary),
        ).forEach { (game, stat, color) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(RoyalPanel)
                    .border(1.dp, RoyalOutline.copy(alpha = 0.25f), RoundedCornerShape(14.dp))
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(color)
                )
                Spacer(Modifier.width(10.dp))
                Column {
                    Text(game, color = RoyalTextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text(stat, color = RoyalTextSecondary, fontSize = 11.sp)
                }
            }
        }

        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun RoyalVisitorsBottomSheet(onDismiss: () -> Unit) {
    var incognitoMode by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(RoyalBackground)
            .padding(horizontal = 20.dp, vertical = 16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("Recent Profile Visitors", color = RoyalTextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            TextButton(onClick = onDismiss) { Text("Close", color = RoyalTextSecondary) }
        }

        Spacer(Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(RoyalPanel)
                .border(1.dp, RoyalOutline.copy(alpha = 0.25f), RoundedCornerShape(14.dp))
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(Modifier.weight(1f)) {
                Text("Incognito Browsing", color = RoyalTextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Text("Don't leave footprint when visiting others", color = RoyalTextSecondary, fontSize = 11.sp)
            }
            Switch(
                checked = incognitoMode,
                onCheckedChange = { incognitoMode = it },
                colors = SwitchDefaults.colors(checkedThumbColor = RoyalGold, checkedTrackColor = RoyalViolet),
            )
        }

        Spacer(Modifier.height(14.dp))
        Text("Recent Visitors (Today)", color = RoyalTextSecondary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)

        Spacer(Modifier.height(8.dp))

        listOf(
            Pair("Maya Lin", "12 minutes ago"),
            Pair("Julian Ross", "1 hour ago"),
            Pair("Priya Sharma", "3 hours ago"),
        ).forEach { (name, time) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(RoyalPanelRaised),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(name.take(1), color = RoyalGold, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                    Spacer(Modifier.width(10.dp))
                    Text(name, color = RoyalTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                }
                Text(time, color = RoyalTextSecondary, fontSize = 11.sp)
            }
            HorizontalDivider(color = RoyalDivider)
        }

        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun RoyalContributionsBottomSheet(onDismiss: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(RoyalBackground)
            .padding(horizontal = 20.dp, vertical = 16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("Contributions", color = RoyalTextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            TextButton(onClick = onDismiss) { Text("Close", color = RoyalTextSecondary) }
        }

        Spacer(Modifier.height(8.dp))
        Text("Your community and guild support milestone score.", color = RoyalTextSecondary, fontSize = 12.sp)

        Spacer(Modifier.height(14.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(RoyalPanel)
                .border(1.dp, RoyalOutline.copy(alpha = 0.25f), RoundedCornerShape(14.dp))
                .padding(14.dp),
        ) {
            Column {
                Text("Total Guild Score: 1,420 pts", color = RoyalGold, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(Modifier.height(4.dp))
                Text("• Gifting contributions: 850 pts", color = RoyalTextSecondary, fontSize = 12.sp)
                Text("• Room hosting contributions: 570 pts", color = RoyalTextSecondary, fontSize = 12.sp)
            }
        }

        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun RoyalBadgesBottomSheet(onDismiss: () -> Unit) {
    val badges = listOf(
        Pair("Star Host", "Hosted 10+ active voice rooms"),
        Pair("VIP Elite", "Held active VIP subscription"),
        Pair("Master Collector", "Unlocked 5+ avatar frames"),
        Pair("Guild Pillar", "Contributed 500+ guild points"),
        Pair("Game Master", "Won 25+ matches in social games"),
        Pair("Nova Pioneer", "Early adopter community badge"),
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(RoyalBackground)
            .padding(horizontal = 20.dp, vertical = 16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("Badge Center", color = RoyalTextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            TextButton(onClick = onDismiss) { Text("Close", color = RoyalTextSecondary) }
        }

        Spacer(Modifier.height(8.dp))
        Text("Earn badges by participating in rooms, games, and guilds.", color = RoyalTextSecondary, fontSize = 12.sp)

        Spacer(Modifier.height(14.dp))

        badges.forEach { (name, desc) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(RoyalPanel)
                    .border(1.dp, RoyalOutline.copy(alpha = 0.25f), RoundedCornerShape(14.dp))
                    .padding(10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Image(
                    painter = painterResource(R.drawable.ap_me_badges),
                    contentDescription = name,
                    modifier = Modifier.size(30.dp),
                )
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(name, color = RoyalTextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text(desc, color = RoyalTextSecondary, fontSize = 11.sp)
                }
            }
        }

        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun RoyalLanguageBottomSheet(onDismiss: () -> Unit) {
    var selectedLanguage by remember { mutableStateOf("English") }
    val languages = listOf("English", "Hindi", "Español", "Français", "Deutsch", "日本語")

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(RoyalBackground)
            .padding(horizontal = 20.dp, vertical = 16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("Select Language", color = RoyalTextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            TextButton(onClick = onDismiss) { Text("Close", color = RoyalTextSecondary) }
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
                Text(
                    text = lang,
                    color = if (lang == selectedLanguage) RoyalGold else RoyalTextPrimary,
                    fontSize = 14.sp,
                    fontWeight = if (lang == selectedLanguage) FontWeight.Bold else FontWeight.Normal,
                )
                if (lang == selectedLanguage) {
                    Image(
                        painter = painterResource(R.drawable.ap_me_verified),
                        contentDescription = "Selected",
                        modifier = Modifier.size(18.dp),
                    )
                }
            }
            HorizontalDivider(color = RoyalDivider)
        }

        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun RoyalParentalControlsBottomSheet(onDismiss: () -> Unit) {
    var safeModeEnabled by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(RoyalBackground)
            .padding(horizontal = 20.dp, vertical = 16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("Parental Control Mode", color = RoyalTextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            TextButton(onClick = onDismiss) { Text("Close", color = RoyalTextSecondary) }
        }

        Spacer(Modifier.height(6.dp))
        Text(
            text = "Restrict voice chat with strangers, filter mature content, and set screen time alerts.",
            color = RoyalTextSecondary,
            fontSize = 12.sp,
        )

        Spacer(Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(RoyalPanel)
                .border(1.dp, RoyalOutline.copy(alpha = 0.25f), RoundedCornerShape(14.dp))
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(Modifier.weight(1f)) {
                Text("Restricted Safe Mode", color = RoyalTextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Text("Requires guardian PIN to change", color = RoyalTextSecondary, fontSize = 11.sp)
            }
            Switch(
                checked = safeModeEnabled,
                onCheckedChange = { safeModeEnabled = it },
                colors = SwitchDefaults.colors(checkedThumbColor = RoyalGold, checkedTrackColor = RoyalViolet),
            )
        }

        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun RoyalGiftPackOfferBottomSheet(
    offer: GiftPackOffer,
    timeRemaining: String,
    onClaim: () -> Unit,
    onDismiss: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(RoyalBackground)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("48-Hour Starter Offer", color = RoyalTextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            TextButton(onClick = onDismiss) { Text("Close", color = RoyalTextSecondary) }
        }

        Spacer(Modifier.height(8.dp))

        Text(
            text = "Limited promotional offer • Expires in $timeRemaining",
            color = RoyalGold,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
        )

        Spacer(Modifier.height(14.dp))

        Text(offer.title, color = RoyalTextPrimary, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
        Spacer(Modifier.height(4.dp))
        Text(offer.description, color = RoyalTextSecondary, fontSize = 13.sp)

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
                    Text(item.name, color = RoyalTextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Text("×${item.count}", color = RoyalCyan, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold)
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(RoyalGold)
                .bounceClick(onClick = onClaim),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "Preview Pack (₹${offer.demoPriceRupees})",
                color = RoyalBackground,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
            )
        }
        Spacer(Modifier.height(6.dp))
        Text(
            text = "Simulated offer preview — billing is not active",
            color = RoyalTextSecondary,
            fontSize = 11.sp,
        )

        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun RoyalSocialListBottomSheet(
    title: String,
    items: List<String>,
    onDismiss: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(RoyalBackground)
            .padding(horizontal = 20.dp, vertical = 16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(title, color = RoyalTextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            TextButton(onClick = onDismiss) { Text("Close", color = RoyalTextSecondary) }
        }

        Spacer(Modifier.height(10.dp))

        items.forEach { person ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(RoyalPanelRaised),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(person.take(1), color = RoyalGold, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
                Spacer(Modifier.width(12.dp))
                Text(person, color = RoyalTextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Medium)
            }
            HorizontalDivider(color = RoyalDivider)
        }

        Spacer(Modifier.height(16.dp))
    }
}
