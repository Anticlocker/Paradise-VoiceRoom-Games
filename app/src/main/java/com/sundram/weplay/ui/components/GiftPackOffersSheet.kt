package com.sundram.weplay.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sundram.weplay.R
import com.sundram.weplay.data.ALL_GIFT_PACK_OFFERS
import com.sundram.weplay.data.FeaturedOfferState
import com.sundram.weplay.data.GiftPackOffer
import com.sundram.weplay.data.GiftPackRotationManager
import com.sundram.weplay.ui.theme.AuroraBackground
import com.sundram.weplay.ui.theme.AuroraBorder
import com.sundram.weplay.ui.theme.AuroraCoral
import com.sundram.weplay.ui.theme.AuroraCyan
import com.sundram.weplay.ui.theme.AuroraGold
import com.sundram.weplay.ui.theme.AuroraGradients
import com.sundram.weplay.ui.theme.AuroraLavender
import com.sundram.weplay.ui.theme.AuroraOrchid
import com.sundram.weplay.ui.theme.AuroraRose
import com.sundram.weplay.ui.theme.AuroraSurface
import com.sundram.weplay.ui.theme.AuroraTextPrimary
import com.sundram.weplay.ui.theme.AuroraTextSecondary
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GiftPackOffersSheet(
    userId: String,
    onDismiss: () -> Unit,
    onOpenTopUp: () -> Unit,
) {
    val context = LocalContext.current
    val rotationManager = remember(userId) { GiftPackRotationManager(context, userId) }
    var currentTime by remember { mutableLongStateOf(System.currentTimeMillis()) }

    // Live countdown ticker
    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            currentTime = System.currentTimeMillis()
        }
    }

    val featuredState = remember(currentTime) { rotationManager.getFeaturedOffer(currentTime) }
    var selectedTab by remember { mutableStateOf(0) } // 0: Featured 48h, 1: All 10 Packs
    var showPreviewConfirmation by remember { mutableStateOf(false) }

    // ── Celebration Opening Animation Values ──
    val boxScale = remember { Animatable(0.82f) }
    val lidOffset = remember { Animatable(0f) }
    val lidRotation = remember { Animatable(0f) }
    val particlesProgress = remember { Animatable(0f) }
    val contentAlpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        // Step 1: Box scales up slightly with bounce
        boxScale.animateTo(1.08f, tween(260, easing = FastOutSlowInEasing))
        boxScale.animateTo(1.0f, tween(160, easing = FastOutSlowInEasing))

        // Step 2: Lid opens & sparkles emerge
        lidOffset.animateTo(-16f, tween(220, easing = FastOutSlowInEasing))
        lidRotation.animateTo(-14f, tween(220, easing = FastOutSlowInEasing))
        particlesProgress.animateTo(1f, tween(400, easing = FastOutSlowInEasing))

        // Step 3: Offer content smoothly fades in
        contentAlpha.animateTo(1f, tween(300, easing = LinearEasing))
    }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = AuroraSurface,
        shape = RoundedCornerShape(topStart = 26.dp, topEnd = 26.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // ── Top Header with Dismiss Button ──
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Promotional Gift Pack",
                    color = AuroraTextPrimary,
                    fontSize = 19.sp,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.weight(1f),
                )
                // Dismiss '✕' Button
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(AuroraBackground)
                        .border(1.dp, AuroraBorder, CircleShape)
                        .clickable(onClick = onDismiss),
                    contentAlignment = Alignment.Center,
                ) {
                    Text("✕", color = AuroraTextSecondary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(Modifier.height(8.dp))

            // ── Celebration 3D Gift Box Animation Container ──
            Box(
                modifier = Modifier
                    .size(110.dp)
                    .scale(boxScale.value),
                contentAlignment = Alignment.Center,
            ) {
                // Golden Radiant Aura behind box
                Canvas(modifier = Modifier.size(110.dp)) {
                    val progress = particlesProgress.value
                    if (progress > 0f) {
                        drawCircle(
                            brush = Brush.radialGradient(
                                listOf(
                                    Color(0x66F6C66E),
                                    Color(0x22FF9C86),
                                    Color.Transparent,
                                )
                            ),
                            radius = size.minDimension / 2f * (0.8f + progress * 0.4f),
                            alpha = (1f - progress * 0.5f),
                        )
                        // Burst particles
                        val particleCount = 8
                        for (i in 0 until particleCount) {
                            val angle = (i * (360f / particleCount) + 20f) * (Math.PI / 180.0)
                            val dist = (size.minDimension / 2.3f) * progress
                            val px = center.x + (cos(angle) * dist).toFloat()
                            val py = center.y + (sin(angle) * dist).toFloat()
                            drawCircle(
                                color = if (i % 2 == 0) AuroraGold else AuroraCoral,
                                radius = 3.5f * (1f - progress * 0.3f),
                                center = Offset(px, py),
                                alpha = (1f - progress * 0.4f),
                            )
                        }
                    }
                }

                // Main 3D Gift Box Body with Ambient Glow & Reward Pedestal
                Box(
                    modifier = Modifier.size(110.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    // Ambient glow layer
                    Image(
                        painter = painterResource(R.drawable.ap3r_fx_gift_ambient_glow),
                        contentDescription = null,
                        modifier = Modifier.size(110.dp),
                        contentScale = androidx.compose.ui.layout.ContentScale.Fit,
                    )
                    // Pedestal layer
                    Image(
                        painter = painterResource(R.drawable.ap3r_fx_reward_pedestal),
                        contentDescription = null,
                        modifier = Modifier
                            .size(96.dp)
                            .align(Alignment.BottomCenter)
                            .offset(y = 12.dp),
                        contentScale = androidx.compose.ui.layout.ContentScale.Fit,
                    )
                    // Animated 3D Gift Chest
                    Image(
                        painter = painterResource(R.drawable.vb_gift_pack),
                        contentDescription = "3D Gift Box",
                        modifier = Modifier
                            .size(80.dp)
                            .offset(y = (lidOffset.value * 0.3f).dp)
                            .rotate(lidRotation.value * 0.2f),
                    )
                }
            }

            // ── Animated Content Container ──
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .alpha(contentAlpha.value),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                // 48-Hour Rotating Timer Banner
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color(0xFFFFECE5), Color(0xFFF0ECFF))
                            )
                        )
                        .border(1.dp, AuroraBorder, RoundedCornerShape(14.dp))
                        .padding(horizontal = 14.dp, vertical = 7.dp),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("⏳", fontSize = 13.sp)
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = "Rotating 48h Offer:",
                                color = AuroraTextPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                        Text(
                            text = featuredState.formattedTimeRemaining,
                            color = AuroraRose,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.ExtraBold,
                        )
                    }
                }

                Spacer(Modifier.height(10.dp))

                // Toggle Tabs: [ Featured Offer ] | [ Browse 10 Packs ]
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(AuroraBackground)
                        .border(1.dp, AuroraBorder, RoundedCornerShape(14.dp))
                        .padding(3.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(11.dp))
                            .background(if (selectedTab == 0) AuroraSurface else Color.Transparent)
                            .clickable { selectedTab = 0 }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "⭐ Active Offer",
                            color = if (selectedTab == 0) AuroraOrchid else AuroraTextSecondary,
                            fontSize = 12.sp,
                            fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Medium,
                        )
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(11.dp))
                            .background(if (selectedTab == 1) AuroraSurface else Color.Transparent)
                            .clickable { selectedTab = 1 }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "🎁 All Packs (${ALL_GIFT_PACK_OFFERS.size})",
                            color = if (selectedTab == 1) AuroraOrchid else AuroraTextSecondary,
                            fontSize = 12.sp,
                            fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Medium,
                        )
                    }
                }

                Spacer(Modifier.height(10.dp))

                // Tab Content
                if (selectedTab == 0) {
                    FeaturedPackCard(
                        featuredState = featuredState,
                        onClaimPreview = { showPreviewConfirmation = true },
                        onOpenTopUp = onOpenTopUp,
                    )
                } else {
                    AllPacksList(
                        offers = ALL_GIFT_PACK_OFFERS,
                        featuredId = featuredState.offer.id,
                        onSelectPack = { showPreviewConfirmation = true },
                    )
                }

                // Billing / Preview Notice
                if (showPreviewConfirmation) {
                    Spacer(Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFFFF9E6))
                            .border(1.dp, Color(0xFFFFD54F), RoundedCornerShape(12.dp))
                            .padding(10.dp),
                    ) {
                        Column {
                            Text(
                                text = "⚡ Billing Verification Notice",
                                color = Color(0xFFE65100),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                            )
                            Spacer(Modifier.height(2.dp))
                            Text(
                                text = "Real Google Play store billing is not connected. Package contents and ₹ pricing are demonstrated for UI preview without charging real money.",
                                color = Color(0xFF5D4037),
                                fontSize = 10.sp,
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = "Dismiss",
                                color = AuroraOrchid,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.clickable { showPreviewConfirmation = false },
                            )
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun FeaturedPackCard(
    featuredState: FeaturedOfferState,
    onClaimPreview: () -> Unit,
    onOpenTopUp: () -> Unit,
) {
    val offer = featuredState.offer

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(18.dp))
            .clip(RoundedCornerShape(18.dp))
            .background(AuroraSurface)
            .border(1.5.dp, AuroraGold, RoundedCornerShape(18.dp))
            .padding(14.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                offer.badge?.let { badge ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(AuroraGold)
                            .padding(horizontal = 6.dp, vertical = 2.dp),
                    ) {
                        Text(badge, color = Color.Black, fontSize = 9.sp, fontWeight = FontWeight.ExtraBold)
                    }
                    Spacer(Modifier.height(4.dp))
                }
                Text(
                    text = offer.title,
                    color = AuroraTextPrimary,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = offer.description,
                    color = AuroraTextSecondary,
                    fontSize = 11.sp,
                )
            }

            // Price Pill
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "₹${offer.demoPriceRupees}",
                    color = AuroraCoral,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                )
                Text(
                    text = "Configured Price",
                    color = AuroraTextSecondary,
                    fontSize = 9.sp,
                )
            }
        }

        Spacer(Modifier.height(12.dp))
        Text(
            text = "Package Items & Perks:",
            color = AuroraTextPrimary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(6.dp))

        // Included Items Grid
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            offer.items.forEach { item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(AuroraBackground)
                        .border(0.5.dp, AuroraBorder, RoundedCornerShape(10.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Image(
                        painter = painterResource(item.iconRes),
                        contentDescription = item.name,
                        modifier = Modifier.size(24.dp),
                    )
                    Spacer(Modifier.width(10.dp))
                    Text(
                        text = "${item.name} x${item.count}",
                        color = AuroraTextPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.weight(1f),
                    )
                    item.durationText?.let { dur ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(AuroraLavender)
                                .padding(horizontal = 6.dp, vertical = 2.dp),
                        ) {
                            Text(dur, color = AuroraOrchid, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(14.dp))

        // Action Buttons Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            // Purchase / Claim Preview Button (Sunset Fantasy)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(AuroraGradients.sunsetFantasy)
                    .clickable(onClick = onClaimPreview),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "Claim Offer (₹${offer.demoPriceRupees})",
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                )
            }

            // Top Up Shortcut
            Box(
                modifier = Modifier
                    .height(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(AuroraSurface)
                    .border(1.dp, AuroraBorder, RoundedCornerShape(12.dp))
                    .clickable(onClick = onOpenTopUp)
                    .padding(horizontal = 14.dp),
                contentAlignment = Alignment.Center,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(R.drawable.vb_coin),
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                    )
                    Spacer(Modifier.width(6.dp))
                    Text("Wallet", color = AuroraTextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun AllPacksList(
    offers: List<GiftPackOffer>,
    featuredId: String,
    onSelectPack: (GiftPackOffer) -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(offers, key = { it.id }) { pack ->
            val isFeatured = pack.id == featuredId

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(if (isFeatured) Color(0xFFF3E5F5) else AuroraSurface)
                    .border(
                        1.dp,
                        if (isFeatured) AuroraOrchid else AuroraBorder,
                        RoundedCornerShape(14.dp)
                    )
                    .clickable { onSelectPack(pack) }
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(AuroraBackground),
                    contentAlignment = Alignment.Center,
                ) {
                    Image(
                        painter = painterResource(pack.items.firstOrNull()?.iconRes ?: R.drawable.ic_gift_box_3d),
                        contentDescription = pack.title,
                        modifier = Modifier.size(24.dp),
                    )
                }

                Spacer(Modifier.width(10.dp))

                Column(Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = pack.title,
                            color = AuroraTextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                        )
                        if (isFeatured) {
                            Spacer(Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(AuroraOrchid)
                                    .padding(horizontal = 4.dp, vertical = 1.dp),
                            ) {
                                Text("48H ACTIVE", color = Color.White, fontSize = 7.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                    Text(
                        text = pack.items.joinToString(", ") { "${it.name} x${it.count}" },
                        color = AuroraTextSecondary,
                        fontSize = 10.sp,
                        maxLines = 1,
                    )
                }

                Spacer(Modifier.width(8.dp))

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(AuroraBackground)
                        .border(1.dp, AuroraBorder, RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 5.dp),
                ) {
                    Text(
                        text = "₹${pack.demoPriceRupees}",
                        color = AuroraOrchid,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    }
}
