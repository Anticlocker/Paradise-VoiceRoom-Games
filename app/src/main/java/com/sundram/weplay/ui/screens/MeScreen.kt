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
import androidx.compose.material3.rememberModalBottomSheetState
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
import com.sundram.weplay.data.DemoBackpack
import com.sundram.weplay.data.FeaturedOfferState
import com.sundram.weplay.data.GiftPackOffer
import com.sundram.weplay.data.GiftPackRotationManager
import com.sundram.weplay.data.FrameTier
import com.sundram.weplay.data.UserProfile
import com.sundram.weplay.ui.components.AvatarWithFrame
import com.sundram.weplay.ui.components.PhoenixAvatarFrame
import com.sundram.weplay.ui.components.PhoenixFramePreview
import com.sundram.weplay.ui.components.UserAvatar
import com.sundram.weplay.ui.components.bounceClick
import com.sundram.weplay.ui.nav.Routes
import com.sundram.weplay.ui.theme.AuroraBorder
import com.sundram.weplay.ui.theme.AuroraError
import com.sundram.weplay.ui.theme.AuroraProfileBackground
import com.sundram.weplay.ui.theme.AuroraTextSecondary
import com.sundram.weplay.ui.theme.AuroraTurquoise
import com.sundram.weplay.ui.theme.ChampagneGold
import com.sundram.weplay.ui.theme.DeepMidnight
import com.sundram.weplay.ui.theme.DreamLavender
import com.sundram.weplay.ui.theme.PearlWhite
import com.sundram.weplay.ui.theme.SoftPeach

// ─────────────────────────────────────────────────────────────────────────────
//  Color Palette — ME PAGE AURORA PARADISE 2.0 EDITION
//  Bright, warm, eye-comfortable, consistent with Games & Voice Room
// ─────────────────────────────────────────────────────────────────────────────
private val CleanCanvas      = PearlWhite
private val CleanWhite       = Color(0xFFFFFFFF)
private val CleanInk         = DeepMidnight
private val CleanSecondary   = AuroraTextSecondary
private val CleanLavender    = DreamLavender.copy(alpha = 0.35f)
private val CleanCyan        = AuroraTurquoise
private val CleanPeach       = SoftPeach
private val CleanGold        = ChampagneGold
private val CleanHairline    = AuroraBorder
private val CleanDanger      = AuroraError

/**
 * MeScreen (ME Page 3.0 Clean Premium Edition)
 *
 * Implements a clean, bright, eye-comfortable social profile hub:
 * - Warm cream/peach to lavender gradient profile header (~190dp)
 * - 84dp rounded-square actual avatar with camera action
 * - Bold username, verified badge, copyable ID with toast, level pill & XP bar
 * - Four illustrated quick shortcut icons (VIP Center, Shop, Avatar Studio, My Space)
 * - Social micro-strip (Friends, Following, Followers, Wallet)
 * - Elegant grouped white menu rows with subtle hairline dividers
 * - Floating 48h promotional gift pack banner
 * - Fully interactive bottom sheets for all submenus
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

    AuroraProfileBackground(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = pad.calculateBottomPadding()),
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 80.dp),
        ) {
            // ── 1. Compact Warm Peach Header (~190dp) ──
            item(key = "clean_header") {
                CleanProfileHeader(
                    profile = profile,
                    onEditProfile = { nav.navigate(Routes.PROFILE_EDIT) },
                    onAvatarClick = { showAvatarStudioSheet = true },
                    onShareProfile = {
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(
                                Intent.EXTRA_TEXT,
                                "Connect with me on the app! Profile ID: ${profile.id}"
                            )
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "Share Profile"))
                    },
                    onCopyId = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        clipboard.setPrimaryClip(ClipData.newPlainText("User ID", profile.id))
                        Toast.makeText(context, "ID copied to clipboard!", Toast.LENGTH_SHORT).show()
                    },
                )
            }

            // ── 2. Four Clean Quick-Access Shortcuts ──
            item(key = "clean_shortcuts") {
                CleanShortcutsRow(
                    onVipClick = { showVipSheet = true },
                    onShopClick = { showShopSheet = true },
                    onAvatarStudioClick = { showAvatarStudioSheet = true },
                    onMySpaceClick = { showMySpaceSheet = true },
                )
            }

            // ── 3. Social & Wallet Micro-Strip ──
            item(key = "clean_social_strip") {
                CleanSocialMicroStrip(
                    coins = profile.coins,
                    onFriendsClick = { nav.navigate(Routes.MSG) },
                    onFollowingClick = { showFollowingSheet = true },
                    onFollowersClick = { showFollowersSheet = true },
                    onWalletClick = { nav.navigate(Routes.TOPUP) },
                )
            }

            // ── 4. Group 1: Activity & Social (Moments, Stats, Visitors) ──
            item(key = "group_activity") {
                CleanMenuGroup(
                    items = listOf(
                        CleanMenuItemData(
                            iconRes = R.drawable.me_clean_moments,
                            title = "Moments",
                            subtitle = "Posts & Feed",
                            onClick = { nav.navigate(Routes.DISCOVER) },
                        ),
                        CleanMenuItemData(
                            iconRes = R.drawable.me_clean_stats,
                            title = "Game & Voice Stats",
                            subtitle = "Level ${profile.level}",
                            onClick = { showStatsSheet = true },
                        ),
                        CleanMenuItemData(
                            iconRes = R.drawable.me_clean_visitors,
                            title = "Visitors",
                            subtitle = "Recent visits",
                            onClick = { showVisitorsSheet = true },
                        ),
                    ),
                )
            }

            // ── 5. Group 2: Growth & Community (Invite, Badges, Contributions, Family) ──
            item(key = "group_growth") {
                CleanMenuGroup(
                    items = listOf(
                        CleanMenuItemData(
                            iconRes = R.drawable.me_clean_invite,
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
                        CleanMenuItemData(
                            iconRes = R.drawable.me_clean_badges,
                            title = "Badge Center",
                            subtitle = "6 Badges",
                            onClick = { showBadgesSheet = true },
                        ),
                        CleanMenuItemData(
                            iconRes = R.drawable.me_clean_contributions,
                            title = "Contributions",
                            subtitle = "Guild milestones",
                            onClick = { showContributionsSheet = true },
                        ),
                        CleanMenuItemData(
                            iconRes = R.drawable.me_clean_family,
                            title = "Family Hub",
                            subtitle = profile.family ?: "Join Guild",
                            onClick = { nav.navigate(Routes.FAMILY) },
                        ),
                    ),
                )
            }

            // ── 6. Group 3: Assets & Collection (Wallet/Top Up, My Gifts, Backpack) ──
            item(key = "group_assets") {
                CleanMenuGroup(
                    items = listOf(
                        CleanMenuItemData(
                            iconRes = R.drawable.me_clean_wallet,
                            title = "Wallet & Top Up",
                            subtitle = "${profile.coins} Coins",
                            onClick = { nav.navigate(Routes.TOPUP) },
                        ),
                        CleanMenuItemData(
                            iconRes = R.drawable.me_clean_gifts,
                            title = "My Gifts",
                            subtitle = "Collection",
                            onClick = { nav.navigate(Routes.GIFTS) },
                        ),
                        CleanMenuItemData(
                            iconRes = R.drawable.me_clean_backpack,
                            title = "Backpack",
                            subtitle = "Frames & Bubbles",
                            onClick = { nav.navigate(Routes.BACKPACK) },
                        ),
                    ),
                )
            }

            // ── 7. Group 4: Settings & Trust (Language, Parental, Help, Security, Settings) ──
            item(key = "group_settings") {
                CleanMenuGroup(
                    items = listOf(
                        CleanMenuItemData(
                            iconRes = R.drawable.me_clean_language,
                            title = "Language",
                            subtitle = "English",
                            onClick = { showLanguageSheet = true },
                        ),
                        CleanMenuItemData(
                            iconRes = R.drawable.me_clean_parental,
                            title = "Parental Control Mode",
                            subtitle = "Safe Mode",
                            onClick = { showParentalControlsSheet = true },
                        ),
                        CleanMenuItemData(
                            iconRes = R.drawable.me_clean_help,
                            title = "Help Center",
                            subtitle = "FAQ & Support",
                            onClick = { nav.navigate(Routes.SUPPORT_SETTINGS) },
                        ),
                        CleanMenuItemData(
                            iconRes = R.drawable.me_clean_security,
                            title = "Security Center",
                            subtitle = "Account Safety",
                            onClick = { nav.navigate(Routes.ACCOUNT_SETTINGS) },
                        ),
                        CleanMenuItemData(
                            iconRes = R.drawable.me_clean_settings,
                            title = "Settings",
                            subtitle = null,
                            onClick = { nav.navigate(Routes.SETTINGS) },
                        ),
                    ),
                )
            }

            // ── 8. Group 5: System & Logout (About, Log Out) ──
            item(key = "group_system") {
                CleanMenuGroup(
                    items = listOf(
                        CleanMenuItemData(
                            iconRes = R.drawable.me_clean_about,
                            title = "About",
                            subtitle = "v2.5.0",
                            onClick = { nav.navigate(Routes.ABOUT) },
                        ),
                        CleanMenuItemData(
                            iconRes = R.drawable.me_clean_logout,
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

        // ── Floating 48h Promotional Gift Pack Banner ──
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
        ) {
            CleanFloatingOfferPill(
                title = featuredOfferState.offer.title,
                timeRemaining = featuredOfferState.formattedTimeRemaining,
                onClick = { showGiftPackSheet = true },
            )
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  Interactive Bottom Sheets & Modals
    // ─────────────────────────────────────────────────────────────────────────

    // 1. VIP Center Sheet
    if (showVipSheet) {
        ModalBottomSheet(
            onDismissRequest = { showVipSheet = false },
            containerColor = CleanWhite,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        ) {
            CleanVipBottomSheet(onDismiss = { showVipSheet = false })
        }
    }

    // 2. Shop Sheet
    if (showShopSheet) {
        ModalBottomSheet(
            onDismissRequest = { showShopSheet = false },
            containerColor = CleanWhite,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        ) {
            CleanShopBottomSheet(
                onTopUp = {
                    showShopSheet = false
                    nav.navigate(Routes.TOPUP)
                },
                onDismiss = { showShopSheet = false },
            )
        }
    }

    // 3. Avatar Studio Sheet (with 6 demo avatars and frame customization)
    if (showAvatarStudioSheet) {
        ModalBottomSheet(
            onDismissRequest = { showAvatarStudioSheet = false },
            containerColor = CleanWhite,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        ) {
            CleanAvatarStudioBottomSheet(
                currentProfile = profile,
                onSave = { updatedProfile ->
                    onSaveProfile(updatedProfile)
                    showAvatarStudioSheet = false
                    Toast.makeText(context, "Avatar updated successfully!", Toast.LENGTH_SHORT).show()
                },
                onDismiss = { showAvatarStudioSheet = false },
            )
        }
    }

    // 4. My Space Sheet
    if (showMySpaceSheet) {
        ModalBottomSheet(
            onDismissRequest = { showMySpaceSheet = false },
            containerColor = CleanWhite,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        ) {
            CleanMySpaceBottomSheet(onDismiss = { showMySpaceSheet = false })
        }
    }

    // 5. Stats Sheet
    if (showStatsSheet) {
        ModalBottomSheet(
            onDismissRequest = { showStatsSheet = false },
            containerColor = CleanWhite,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        ) {
            CleanStatsBottomSheet(profile = profile, onDismiss = { showStatsSheet = false })
        }
    }

    // 6. Visitors Sheet
    if (showVisitorsSheet) {
        ModalBottomSheet(
            onDismissRequest = { showVisitorsSheet = false },
            containerColor = CleanWhite,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        ) {
            CleanVisitorsBottomSheet(onDismiss = { showVisitorsSheet = false })
        }
    }

    // 7. Contributions Sheet
    if (showContributionsSheet) {
        ModalBottomSheet(
            onDismissRequest = { showContributionsSheet = false },
            containerColor = CleanWhite,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        ) {
            CleanContributionsBottomSheet(onDismiss = { showContributionsSheet = false })
        }
    }

    // 8. Badge Center Sheet
    if (showBadgesSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBadgesSheet = false },
            containerColor = CleanWhite,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        ) {
            CleanBadgesBottomSheet(onDismiss = { showBadgesSheet = false })
        }
    }

    // 9. Language Sheet
    if (showLanguageSheet) {
        ModalBottomSheet(
            onDismissRequest = { showLanguageSheet = false },
            containerColor = CleanWhite,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        ) {
            CleanLanguageBottomSheet(onDismiss = { showLanguageSheet = false })
        }
    }

    // 10. Parental Controls Sheet
    if (showParentalControlsSheet) {
        ModalBottomSheet(
            onDismissRequest = { showParentalControlsSheet = false },
            containerColor = CleanWhite,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        ) {
            CleanParentalControlsBottomSheet(onDismiss = { showParentalControlsSheet = false })
        }
    }

    // 11. Gift Pack Offer Sheet
    if (showGiftPackSheet) {
        ModalBottomSheet(
            onDismissRequest = { showGiftPackSheet = false },
            containerColor = CleanWhite,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        ) {
            CleanGiftPackOfferBottomSheet(
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

    // 12. Following List Sheet
    if (showFollowingSheet) {
        ModalBottomSheet(
            onDismissRequest = { showFollowingSheet = false },
            containerColor = CleanWhite,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        ) {
            CleanSocialListBottomSheet(
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
            containerColor = CleanWhite,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        ) {
            CleanSocialListBottomSheet(
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
            title = { Text("Log Out", color = CleanInk, fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to log out of your account?", color = CleanSecondary) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutDialog = false
                        onLogout()
                    }
                ) {
                    Text("Log Out", color = CleanDanger, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancel", color = CleanSecondary)
                }
            },
            containerColor = CleanWhite,
            shape = RoundedCornerShape(20.dp),
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  1. Profile Header Component (Warm peach-to-lavender, ~190dp)
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun CleanProfileHeader(
    profile: UserProfile,
    onEditProfile: () -> Unit,
    onAvatarClick: () -> Unit,
    onShareProfile: () -> Unit,
    onCopyId: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        CleanPeach.copy(alpha = 0.85f),
                        CleanLavender.copy(alpha = 0.50f),
                        CleanCanvas,
                    )
                )
            )
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 12.dp),
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Top Action Row (Share & Edit Profile)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Share Shortcut
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(CleanWhite.copy(alpha = 0.85f))
                        .bounceClick(onClick = onShareProfile),
                    contentAlignment = Alignment.Center,
                ) {
                    Image(
                        painter = painterResource(R.drawable.me_clean_share),
                        contentDescription = "Share Profile",
                        modifier = Modifier.size(18.dp),
                    )
                }

                Spacer(Modifier.width(10.dp))

                // Edit Profile Shortcut
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(CleanWhite.copy(alpha = 0.85f))
                        .bounceClick(onClick = onEditProfile),
                    contentAlignment = Alignment.Center,
                ) {
                    Image(
                        painter = painterResource(R.drawable.me_clean_profile),
                        contentDescription = "Edit Profile",
                        modifier = Modifier.size(18.dp),
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            // Main Profile Identity Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // 84dp Rounded-Square Avatar with Phoenix Rebirth Support
                Box(
                    modifier = Modifier
                        .size(84.dp)
                        .bounceClick(scaleDown = 0.94f, onClick = onAvatarClick),
                    contentAlignment = Alignment.Center,
                ) {
                    val isPhoenix = profile.frame.equals("phoenix", ignoreCase = true) ||
                            profile.frame.equals("PHOENIX_REBIRTH", ignoreCase = true)

                    if (isPhoenix) {
                        PhoenixAvatarFrame(
                            name = profile.name,
                            photoPath = profile.photoPath,
                            size = 84.dp,
                            animate = true,
                            contentDescription = "Phoenix Rebirth Avatar Frame",
                        )
                    } else {
                        AvatarWithFrame(
                            name = profile.name,
                            photoPath = profile.photoPath,
                            size = 80.dp,
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
                            .background(CleanWhite)
                            .border(1.dp, CleanHairline, CircleShape),
                        contentAlignment = Alignment.Center,
                    ) {
                        Image(
                            painter = painterResource(R.drawable.me_clean_camera),
                            contentDescription = "Change Avatar",
                            modifier = Modifier.size(14.dp),
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
                            color = CleanInk,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )

                        // Verified Icon (Rendered ONLY if verified)
                        if (profile.phoneVerified || profile.emailVerified) {
                            Spacer(Modifier.width(6.dp))
                            Image(
                                painter = painterResource(R.drawable.me_clean_verify),
                                contentDescription = "Verified Account",
                                modifier = Modifier.size(16.dp),
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
                            color = CleanSecondary,
                            fontWeight = FontWeight.Medium,
                        )
                        Spacer(Modifier.width(6.dp))
                        Image(
                            painter = painterResource(R.drawable.me_clean_share),
                            contentDescription = "Copy ID",
                            modifier = Modifier.size(12.dp),
                        )
                    }

                    Spacer(Modifier.height(8.dp))

                    // Level & XP Progress
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        // Level Pill
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(CleanGold)
                                .padding(horizontal = 7.dp, vertical = 2.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = "Lv.${profile.level}",
                                color = CleanWhite,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                            )
                        }

                        Spacer(Modifier.width(8.dp))

                        val xpProgress = ((profile.coins % 1000) / 1000f).coerceIn(0f, 1f)
                        Column(modifier = Modifier.weight(1f)) {
                            LinearProgressIndicator(
                                progress = { xpProgress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(5.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = CleanCyan,
                                trackColor = CleanWhite.copy(alpha = 0.7f),
                            )
                            Spacer(Modifier.height(2.dp))
                            Text(
                                text = "${profile.coins % 1000} / 1000 XP",
                                fontSize = 9.sp,
                                color = CleanSecondary,
                            )
                        }
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  2. Four Clean Quick-Access Shortcuts (VIP, Shop, Avatar Studio, My Space)
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun CleanShortcutsRow(
    onVipClick: () -> Unit,
    onShopClick: () -> Unit,
    onAvatarStudioClick: () -> Unit,
    onMySpaceClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CleanShortcutTile(
            iconRes = R.drawable.me_clean_vip,
            label = "VIP Center",
            onClick = onVipClick,
        )
        CleanShortcutTile(
            iconRes = R.drawable.me_clean_shop,
            label = "Shop",
            onClick = onShopClick,
        )
        CleanShortcutTile(
            iconRes = R.drawable.me_clean_avatar,
            label = "Avatar Studio",
            onClick = onAvatarStudioClick,
        )
        CleanShortcutTile(
            iconRes = R.drawable.me_clean_home,
            label = "My Space",
            onClick = onMySpaceClick,
        )
    }
}

@Composable
private fun CleanShortcutTile(
    iconRes: Int,
    label: String,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .bounceClick(scaleDown = 0.92f, onClick = onClick)
            .padding(horizontal = 6.dp, vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(54.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(CleanWhite)
                .border(1.dp, CleanHairline, RoundedCornerShape(16.dp))
                .shadow(1.dp, RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(iconRes),
                contentDescription = label,
                modifier = Modifier.size(34.dp),
            )
        }
        Spacer(Modifier.height(6.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = CleanInk,
            textAlign = TextAlign.Center,
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  3. Social Micro-Strip (Friends, Following, Followers, Wallet)
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun CleanSocialMicroStrip(
    coins: Long,
    onFriendsClick: () -> Unit,
    onFollowingClick: () -> Unit,
    onFollowersClick: () -> Unit,
    onWalletClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(CleanWhite)
            .border(1.dp, CleanHairline, RoundedCornerShape(16.dp))
            .padding(vertical = 10.dp, horizontal = 12.dp),
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
                Text(text = "24", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = CleanInk)
                Text(text = "Friends", fontSize = 11.sp, color = CleanSecondary)
            }

            Box(Modifier.width(1.dp).height(24.dp).background(CleanHairline))

            // Following
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable(onClick = onFollowingClick),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(text = "142", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = CleanInk)
                Text(text = "Following", fontSize = 11.sp, color = CleanSecondary)
            }

            Box(Modifier.width(1.dp).height(24.dp).background(CleanHairline))

            // Followers
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable(onClick = onFollowersClick),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(text = "380", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = CleanInk)
                Text(text = "Followers", fontSize = 11.sp, color = CleanSecondary)
            }

            Box(Modifier.width(1.dp).height(24.dp).background(CleanHairline))

            // Wallet (Coins)
            Row(
                modifier = Modifier
                    .weight(1.2f)
                    .clickable(onClick = onWalletClick)
                    .padding(start = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                Image(
                    painter = painterResource(R.drawable.me_clean_wallet),
                    contentDescription = "Wallet Coins",
                    modifier = Modifier.size(20.dp),
                )
                Spacer(Modifier.width(6.dp))
                Column {
                    Text(text = "$coins", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = CleanGold)
                    Text(text = "Top Up", fontSize = 10.sp, color = CleanSecondary)
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  4. Clean Grouped Menu Cards (Generous 58-64dp height, hairline dividers)
// ─────────────────────────────────────────────────────────────────────────────
data class CleanMenuItemData(
    val iconRes: Int,
    val title: String,
    val subtitle: String?,
    val isDanger: Boolean = false,
    val onClick: () -> Unit,
)

@Composable
private fun CleanMenuGroup(
    items: List<CleanMenuItemData>,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(CleanWhite)
            .border(1.dp, CleanHairline, RoundedCornerShape(16.dp)),
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
                        modifier = Modifier.size(26.dp),
                    )

                    Spacer(Modifier.width(14.dp))

                    Text(
                        text = item.title,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (item.isDanger) CleanDanger else CleanInk,
                        modifier = Modifier.weight(1f),
                    )

                    if (item.subtitle != null) {
                        Text(
                            text = item.subtitle,
                            fontSize = 12.sp,
                            color = CleanSecondary,
                        )
                        Spacer(Modifier.width(8.dp))
                    }

                    Image(
                        painter = painterResource(R.drawable.me_clean_share),
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                    )
                }

                if (index < items.size - 1) {
                    HorizontalDivider(
                        modifier = Modifier.padding(start = 56.dp, end = 16.dp),
                        thickness = 0.5.dp,
                        color = CleanHairline,
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  5. Floating 48h Promotional Offer Pill
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun CleanFloatingOfferPill(
    title: String,
    timeRemaining: String,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(
                Brush.horizontalGradient(
                    listOf(Color(0xFF7B52E8), Color(0xFF31CAD9))
                )
            )
            .bounceClick(scaleDown = 0.98f, onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 10.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                painter = painterResource(R.drawable.me_clean_giftpack),
                contentDescription = null,
                modifier = Modifier.size(28.dp),
            )

            Spacer(Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = CleanWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                )
                Text(
                    text = "Limited 48h offer • $timeRemaining left",
                    color = CleanWhite.copy(alpha = 0.85f),
                    fontSize = 11.sp,
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(CleanWhite)
                    .padding(horizontal = 10.dp, vertical = 4.dp),
            ) {
                Text(
                    text = "View",
                    color = Color(0xFF7B52E8),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  6. Submenu Bottom Sheets
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun CleanVipBottomSheet(onDismiss: () -> Unit) {
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
            Text("VIP Center", color = CleanInk, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            TextButton(onClick = onDismiss) { Text("Close", color = CleanSecondary) }
        }

        Spacer(Modifier.height(10.dp))
        Text("Unlock royal privileges, exclusive room entrances, and custom badges.", color = CleanSecondary, fontSize = 13.sp)

        Spacer(Modifier.height(16.dp))

        listOf(
            Triple("Free Tier", "Standard features & social lounge access", CleanSecondary),
            Triple("Silver VIP", "Chat glow, 5% coin rebate, custom room badges", Color(0xFF9E9E9E)),
            Triple("Gold VIP", "Exclusive vehicle entrance, animated avatar frames", CleanGold),
            Triple("Diamond Elite", "Host priority, room banner takeover, 24/7 concierge", CleanCyan),
        ).forEach { (tier, perk, color) ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(CleanCanvas)
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
                        Text(tier, color = CleanInk, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text(perk, color = CleanSecondary, fontSize = 11.sp)
                    }
                }
            }
        }

        Spacer(Modifier.height(20.dp))
    }
}

@Composable
private fun CleanShopBottomSheet(
    onTopUp: () -> Unit,
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
            Text("Cosmetics Shop", color = CleanInk, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            TextButton(onClick = onDismiss) { Text("Close", color = CleanSecondary) }
        }

        Spacer(Modifier.height(10.dp))
        Text("Customize your voice room persona with frames and enter effects.", color = CleanSecondary, fontSize = 13.sp)

        Spacer(Modifier.height(14.dp))

        listOf(
            Triple("Cyan Halo Frame", "500 Coins", R.drawable.me_clean_frame),
            Triple("Gold Star Crown", "1,200 Coins", R.drawable.me_clean_crown),
            Triple("Royal Enter Shimmer", "2,500 Coins", R.drawable.me_clean_giftpack),
        ).forEach { (title, price, icon) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(CleanCanvas)
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
                        Text(title, color = CleanInk, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text(price, color = CleanGold, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(CleanCyan)
                        .clickable(onClick = onTopUp)
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                ) {
                    Text("Acquire", color = CleanWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun CleanAvatarStudioBottomSheet(
    currentProfile: UserProfile,
    onSave: (UserProfile) -> Unit,
    onDismiss: () -> Unit,
) {
    // 6 Fictional Demo Avatars from me_clean_demo_avatar_01..06
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
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("Avatar Studio", color = CleanInk, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            TextButton(onClick = onDismiss) { Text("Cancel", color = CleanSecondary) }
        }

        Spacer(Modifier.height(8.dp))
        Text(
            text = "Select a demo style preset or equip an original avatar.",
            color = CleanSecondary,
            fontSize = 12.sp,
        )

        Spacer(Modifier.height(16.dp))

        // Selected Preview (shows Phoenix Rebirth frame when equipPhoenixFrame is enabled)
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
                    .border(2.dp, CleanCyan, RoundedCornerShape(24.dp)),
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
                            if (isSelected) CleanCyan else CleanHairline,
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
                .background(if (equipPhoenixFrame) Color(0xFF231B45) else CleanCanvas)
                .border(
                    1.dp,
                    if (equipPhoenixFrame) Color(0xFFFFD36A) else CleanHairline,
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
                                color = if (equipPhoenixFrame) Color(0xFFFFD36A) else CleanInk,
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
                            color = if (equipPhoenixFrame) Color(0xFFBBA4FA) else CleanSecondary,
                            fontSize = 11.sp,
                        )
                    }
                }

                Switch(
                    checked = equipPhoenixFrame,
                    onCheckedChange = { equipPhoenixFrame = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color(0xFFFFD36A),
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
                if (showMultiSizePreview) "Hide Multi-Size Preview ▲" else "View Multi-Size Frame Preview (64/96/128dp) ▼",
                color = CleanCyan,
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
                .background(CleanCyan)
                .bounceClick {
                    // Update user profile avatar ID and equipped frame
                    val updated = currentProfile.copy(
                        avatarId = "clean_demo_${selectedIndex + 1}",
                        frame = if (equipPhoenixFrame) "phoenix" else if (currentProfile.frame.equals("phoenix", ignoreCase = true)) null else currentProfile.frame,
                    )
                    onSave(updated)
                },
            contentAlignment = Alignment.Center,
        ) {
            Text("Apply Avatar & Frame", color = CleanWhite, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }

        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun CleanMySpaceBottomSheet(onDismiss: () -> Unit) {
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
            Text("My Space", color = CleanInk, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            TextButton(onClick = onDismiss) { Text("Close", color = CleanSecondary) }
        }

        Spacer(Modifier.height(8.dp))
        Text("Your personalized lounge space. Decorate furniture, stage showcases, and invite friends.", color = CleanSecondary, fontSize = 12.sp)

        Spacer(Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(CleanLavender.copy(alpha = 0.5f)),
            contentAlignment = Alignment.Center,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Image(
                    painter = painterResource(R.drawable.me_clean_home),
                    contentDescription = null,
                    modifier = Modifier.size(44.dp),
                )
                Spacer(Modifier.height(8.dp))
                Text("Personal Space Lounge", color = CleanInk, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Text("Custom 3D room themes arriving in upcoming season update.", color = CleanSecondary, fontSize = 11.sp)
            }
        }

        Spacer(Modifier.height(20.dp))
    }
}

@Composable
private fun CleanStatsBottomSheet(profile: UserProfile, onDismiss: () -> Unit) {
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
            Text("Game & Voice Stats", color = CleanInk, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            TextButton(onClick = onDismiss) { Text("Close", color = CleanSecondary) }
        }

        Spacer(Modifier.height(12.dp))

        listOf(
            Triple("Ludo Matches", "42 played • 28 won (66% win rate)", CleanCyan),
            Triple("Spy Fall Rounds", "19 played • 14 won", Color(0xFF7B52E8)),
            Triple("Voice Lounge Hours", "18.5 hours hosted", CleanGold),
            Triple("Total Room XP", "${profile.coins * 2} lifetime XP", CleanSecondary),
        ).forEach { (game, stat, color) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(CleanCanvas)
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
                    Text(game, color = CleanInk, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text(stat, color = CleanSecondary, fontSize = 11.sp)
                }
            }
        }

        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun CleanVisitorsBottomSheet(onDismiss: () -> Unit) {
    var incognitoMode by remember { mutableStateOf(false) }

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
            Text("Recent Profile Visitors", color = CleanInk, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            TextButton(onClick = onDismiss) { Text("Close", color = CleanSecondary) }
        }

        Spacer(Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(CleanCanvas)
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(Modifier.weight(1f)) {
                Text("Incognito Browsing", color = CleanInk, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Text("Don't leave footprint when visiting others", color = CleanSecondary, fontSize = 11.sp)
            }
            Switch(
                checked = incognitoMode,
                onCheckedChange = { incognitoMode = it },
                colors = SwitchDefaults.colors(checkedThumbColor = CleanWhite, checkedTrackColor = CleanCyan),
            )
        }

        Spacer(Modifier.height(14.dp))
        Text("Recent Visitors (Today)", color = CleanSecondary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)

        Spacer(Modifier.height(8.dp))

        listOf(
            Pair("Maya Lin", "12 minutes ago"),
            Pair("Julian Ross", "1 hour ago"),
            Pair("Priya Sharma", "3 hours ago"),
        ).forEach { (name, time) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(CleanLavender),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(name.take(1), color = CleanInk, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                    Spacer(Modifier.width(10.dp))
                    Text(name, color = CleanInk, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                }
                Text(time, color = CleanSecondary, fontSize = 11.sp)
            }
            HorizontalDivider(color = CleanHairline)
        }

        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun CleanContributionsBottomSheet(onDismiss: () -> Unit) {
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
            Text("Contributions", color = CleanInk, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            TextButton(onClick = onDismiss) { Text("Close", color = CleanSecondary) }
        }

        Spacer(Modifier.height(8.dp))
        Text("Your community and guild support milestone score.", color = CleanSecondary, fontSize = 12.sp)

        Spacer(Modifier.height(14.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(CleanCanvas)
                .padding(14.dp),
        ) {
            Column {
                Text("Total Guild Score: 1,420 pts", color = CleanInk, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(Modifier.height(4.dp))
                Text("• Gifting contributions: 850 pts", color = CleanSecondary, fontSize = 12.sp)
                Text("• Room hosting contributions: 570 pts", color = CleanSecondary, fontSize = 12.sp)
            }
        }

        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun CleanBadgesBottomSheet(onDismiss: () -> Unit) {
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
            .padding(horizontal = 20.dp, vertical = 16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("Badge Center", color = CleanInk, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            TextButton(onClick = onDismiss) { Text("Close", color = CleanSecondary) }
        }

        Spacer(Modifier.height(8.dp))
        Text("Earn badges by participating in rooms, games, and guilds.", color = CleanSecondary, fontSize = 12.sp)

        Spacer(Modifier.height(14.dp))

        badges.forEach { (name, desc) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(CleanCanvas)
                    .padding(10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Image(
                    painter = painterResource(R.drawable.me_clean_crown),
                    contentDescription = name,
                    modifier = Modifier.size(28.dp),
                )
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(name, color = CleanInk, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text(desc, color = CleanSecondary, fontSize = 11.sp)
                }
            }
        }

        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun CleanLanguageBottomSheet(onDismiss: () -> Unit) {
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
            Text("Select Language", color = CleanInk, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            TextButton(onClick = onDismiss) { Text("Close", color = CleanSecondary) }
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
                Text(lang, color = CleanInk, fontSize = 14.sp, fontWeight = if (lang == selectedLanguage) FontWeight.Bold else FontWeight.Normal)
                if (lang == selectedLanguage) {
                    Image(
                        painter = painterResource(R.drawable.me_clean_verify),
                        contentDescription = "Selected",
                        modifier = Modifier.size(18.dp),
                    )
                }
            }
            HorizontalDivider(color = CleanHairline)
        }

        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun CleanParentalControlsBottomSheet(onDismiss: () -> Unit) {
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
            Text("Parental Control Mode", color = CleanInk, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            TextButton(onClick = onDismiss) { Text("Close", color = CleanSecondary) }
        }

        Spacer(Modifier.height(6.dp))
        Text(
            text = "Restrict voice chat with strangers, filter mature content, and set screen time alerts.",
            color = CleanSecondary,
            fontSize = 12.sp,
        )

        Spacer(Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(Modifier.weight(1f)) {
                Text("Restricted Safe Mode", color = CleanInk, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Text("Requires guardian PIN to change", color = CleanSecondary, fontSize = 11.sp)
            }
            Switch(
                checked = safeModeEnabled,
                onCheckedChange = { safeModeEnabled = it },
                colors = SwitchDefaults.colors(checkedThumbColor = CleanWhite, checkedTrackColor = CleanCyan),
            )
        }

        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun CleanGiftPackOfferBottomSheet(
    offer: GiftPackOffer,
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
            Text("48-Hour Starter Offer", color = CleanInk, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            TextButton(onClick = onDismiss) { Text("Close", color = CleanSecondary) }
        }

        Spacer(Modifier.height(8.dp))

        Text(
            text = "Limited promotional offer • Expires in $timeRemaining",
            color = CleanGold,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
        )

        Spacer(Modifier.height(14.dp))

        Text(offer.title, color = CleanInk, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
        Spacer(Modifier.height(4.dp))
        Text(offer.description, color = CleanSecondary, fontSize = 13.sp)

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
                    Text(item.name, color = CleanInk, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Text("×${item.count}", color = CleanCyan, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold)
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(CleanCyan)
                .bounceClick(onClick = onClaim),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "Claim Offer (₹${offer.demoPriceRupees}) [DEMO]",
                color = CleanWhite,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
            )
        }

        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun CleanSocialListBottomSheet(
    title: String,
    items: List<String>,
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
            Text(title, color = CleanInk, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            TextButton(onClick = onDismiss) { Text("Close", color = CleanSecondary) }
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
                        .background(CleanPeach),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(person.take(1), color = CleanInk, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
                Spacer(Modifier.width(12.dp))
                Text(person, color = CleanInk, fontSize = 14.sp, fontWeight = FontWeight.Medium)
            }
            HorizontalDivider(color = CleanHairline)
        }

        Spacer(Modifier.height(16.dp))
    }
}
