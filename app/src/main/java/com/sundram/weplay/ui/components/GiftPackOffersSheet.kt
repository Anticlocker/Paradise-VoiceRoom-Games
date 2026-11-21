package com.sundram.weplay.ui.components

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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
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
import com.sundram.weplay.ui.theme.VyboraBg
import com.sundram.weplay.ui.theme.VyboraBorder
import com.sundram.weplay.ui.theme.VyboraGold
import com.sundram.weplay.ui.theme.VyboraLavender
import com.sundram.weplay.ui.theme.VyboraPurple
import com.sundram.weplay.ui.theme.VyboraSurface
import com.sundram.weplay.ui.theme.VyboraTextPrimary
import com.sundram.weplay.ui.theme.VyboraTextSecondary
import kotlinx.coroutines.delay

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
    var selectedTab by remember { mutableStateOf(0) } // 0: Featured, 1: All 10 Packs
    var selectedPackForDetail by remember { mutableStateOf<GiftPackOffer?>(null) }
    var showPreviewConfirmation by remember { mutableStateOf(false) }

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
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Sheet Header with Gift Box Artwork
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Image(
                    painter = painterResource(R.drawable.vb_gift_pack),
                    contentDescription = null,
                    modifier = Modifier.size(44.dp),
                )
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(
                        text = "Gift Pack Offers",
                        color = VyboraTextPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                    )
                    Text(
                        text = "Curated bonus packages & room flairs",
                        color = VyboraTextSecondary,
                        fontSize = 12.sp,
                    )
                }
                // Dismiss 'X' Button
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(VyboraBg)
                        .clickable(onClick = onDismiss),
                    contentAlignment = Alignment.Center,
                ) {
                    Text("✕", color = VyboraTextSecondary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(Modifier.height(14.dp))

            // 48-Hour Rotating Timer Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(Color(0xFFEDE7F6), Color(0xFFE0F7FA))
                        )
                    )
                    .border(1.dp, VyboraBorder, RoundedCornerShape(12.dp))
                    .padding(horizontal = 14.dp, vertical = 8.dp),
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
                            text = "Featured 48h Offer:",
                            color = VyboraPurple,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                    Text(
                        text = featuredState.formattedTimeRemaining,
                        color = Color(0xFFD81B60),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.ExtraBold,
                    )
                }
            }

            Spacer(Modifier.height(14.dp))

            // Toggle Tabs: [ Featured Offer ] | [ Browse 10 Packs ]
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(VyboraBg)
                    .padding(3.dp),
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (selectedTab == 0) VyboraSurface else Color.Transparent)
                        .clickable { selectedTab = 0 }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "⭐ Your Featured Pack",
                        color = if (selectedTab == 0) VyboraPurple else VyboraTextSecondary,
                        fontSize = 12.sp,
                        fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Medium,
                    )
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (selectedTab == 1) VyboraSurface else Color.Transparent)
                        .clickable { selectedTab = 1 }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "🎁 All 10 Packs (${ALL_GIFT_PACK_OFFERS.size})",
                        color = if (selectedTab == 1) VyboraPurple else VyboraTextSecondary,
                        fontSize = 12.sp,
                        fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Medium,
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            // Tab Content
            if (selectedTab == 0) {
                // Featured Offer Card
                FeaturedPackView(
                    featuredState = featuredState,
                    onPreviewClaim = { showPreviewConfirmation = true },
                    onOpenTopUp = onOpenTopUp,
                )
            } else {
                // All 10 Packs Catalog
                AllPacksCatalogView(
                    offers = ALL_GIFT_PACK_OFFERS,
                    featuredId = featuredState.offer.id,
                    onSelectPack = { pack ->
                        selectedPackForDetail = pack
                        showPreviewConfirmation = true
                    },
                )
            }

            // Preview Info Modal / Toast simulation
            if (showPreviewConfirmation) {
                Spacer(Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFFFF9C4))
                        .border(1.dp, Color(0xFFFBC02D), RoundedCornerShape(12.dp))
                        .padding(12.dp),
                ) {
                    Column {
                        Text(
                            text = "ℹ️ Store Preview Mode",
                            color = Color(0xFFF57F17),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                        )
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = "Real Google Play store billing is not connected. Package contents and ₹ pricing are demonstrated for UI preview without charging real money.",
                            color = Color(0xFF5D4037),
                            fontSize = 11.sp,
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = "Dismiss Notice",
                            color = VyboraPurple,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .clickable { showPreviewConfirmation = false }
                                .padding(2.dp),
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun FeaturedPackView(
    featuredState: FeaturedOfferState,
    onPreviewClaim: () -> Unit,
    onOpenTopUp: () -> Unit,
) {
    val offer = featuredState.offer

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(18.dp))
            .clip(RoundedCornerShape(18.dp))
            .background(VyboraBg)
            .border(1.5.dp, VyboraGold, RoundedCornerShape(18.dp))
            .padding(16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column {
                offer.badge?.let { badge ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(VyboraGold)
                            .padding(horizontal = 6.dp, vertical = 2.dp),
                    ) {
                        Text(badge, color = Color.Black, fontSize = 9.sp, fontWeight = FontWeight.ExtraBold)
                    }
                    Spacer(Modifier.height(4.dp))
                }
                Text(
                    text = offer.title,
                    color = VyboraTextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = offer.description,
                    color = VyboraTextSecondary,
                    fontSize = 12.sp,
                )
            }

            // Price Tag
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "₹${offer.demoPriceRupees}",
                    color = Color(0xFFD81B60),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                )
                Text(
                    text = "[Demo Price]",
                    color = VyboraTextSecondary,
                    fontSize = 10.sp,
                )
            }
        }

        Spacer(Modifier.height(14.dp))
        Text(
            text = "Guaranteed Package Contents:",
            color = VyboraTextPrimary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(8.dp))

        // Included Items Grid / List
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            offer.items.forEach { item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(VyboraSurface)
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
                        color = VyboraTextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.weight(1f),
                    )
                    item.durationText?.let { dur ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(VyboraLavender)
                                .padding(horizontal = 6.dp, vertical = 2.dp),
                        ) {
                            Text(dur, color = VyboraPurple, fontSize = 10.sp, fontWeight = FontWeight.Bold)
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
            // Claim / Preview Button
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(42.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        Brush.horizontalGradient(listOf(Color(0xFFEC407A), Color(0xFF8E24AA)))
                    )
                    .clickable(onClick = onPreviewClaim),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "Claim Preview (${offer.title})",
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                )
            }

            // Add Coins (Top Up) Shortcut
            Box(
                modifier = Modifier
                    .height(42.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(VyboraSurface)
                    .border(1.dp, VyboraBorder, RoundedCornerShape(12.dp))
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
                    Text("Top Up", color = VyboraTextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun AllPacksCatalogView(
    offers: List<GiftPackOffer>,
    featuredId: String,
    onSelectPack: (GiftPackOffer) -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .height(320.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(offers, key = { it.id }) { pack ->
            val isFeatured = pack.id == featuredId

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(if (isFeatured) Color(0xFFF3E5F5) else VyboraBg)
                    .border(
                        1.dp,
                        if (isFeatured) VyboraPurple else VyboraBorder,
                        RoundedCornerShape(14.dp)
                    )
                    .clickable { onSelectPack(pack) }
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Pack Icon
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(VyboraSurface),
                    contentAlignment = Alignment.Center,
                ) {
                    Image(
                        painter = painterResource(pack.items.firstOrNull()?.iconRes ?: R.drawable.vb_gift_pack),
                        contentDescription = pack.title,
                        modifier = Modifier.size(24.dp),
                    )
                }

                Spacer(Modifier.width(10.dp))

                Column(Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = pack.title,
                            color = VyboraTextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                        )
                        if (isFeatured) {
                            Spacer(Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(VyboraPurple)
                                    .padding(horizontal = 4.dp, vertical = 1.dp),
                            ) {
                                Text("ACTIVE 48H", color = Color.White, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                    Text(
                        text = pack.items.joinToString(", ") { "${it.name} x${it.count}" },
                        color = VyboraTextSecondary,
                        fontSize = 11.sp,
                        maxLines = 1,
                    )
                }

                Spacer(Modifier.width(8.dp))

                // Price Pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(VyboraSurface)
                        .border(1.dp, VyboraBorder, RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                ) {
                    Text(
                        text = "₹${pack.demoPriceRupees}",
                        color = VyboraPurple,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    }
}
