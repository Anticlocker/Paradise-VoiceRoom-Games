package com.sundram.weplay.ui.screens

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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.navigation.NavHostController
import com.sundram.weplay.R
import com.sundram.weplay.data.CoinPack
import com.sundram.weplay.data.DemoCoinPacks
import com.sundram.weplay.data.TopUpRecord
import com.sundram.weplay.data.TopUpStore
import com.sundram.weplay.data.UserProfile
import com.sundram.weplay.ui.components.RoundIconButton
import com.sundram.weplay.ui.theme.VyboraBg
import com.sundram.weplay.ui.theme.VyboraBorder
import com.sundram.weplay.ui.theme.VyboraGold
import com.sundram.weplay.ui.theme.VyboraLavender
import com.sundram.weplay.ui.theme.VyboraPurple
import com.sundram.weplay.ui.theme.VyboraSurface
import com.sundram.weplay.ui.theme.VyboraTextPrimary
import com.sundram.weplay.ui.theme.VyboraTextSecondary
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val stampFormat = SimpleDateFormat("d MMM · h:mm a", Locale.US)

private enum class CheckoutState {
    IDLE,
    PENDING,
    SUCCESS,
    FAILED
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopUpScreen(
    nav: NavHostController,
    profile: UserProfile,
    onSave: (UserProfile) -> Unit,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val store = remember { TopUpStore(context) }
    var history by remember { mutableStateOf(store.history(profile.id)) }

    var selectedPack by remember { mutableStateOf<CoinPack?>(null) }
    var checkoutState by remember { mutableStateOf(CheckoutState.IDLE) }
    var lastPurchasedRecord by remember { mutableStateOf<TopUpRecord?>(null) }

    var showHistorySheet by remember { mutableStateOf(false) }
    var showHelpDialog by remember { mutableStateOf(false) }

    // Hardware Back Navigation Interception
    BackHandler {
        when {
            showHistorySheet -> showHistorySheet = false
            showHelpDialog -> showHelpDialog = false
            selectedPack != null -> selectedPack = null
            else -> nav.popBackStack()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(VyboraBg)
            .statusBarsPadding()
            .navigationBarsPadding(),
    ) {
        // ── Top Back Bar with Title, Balance & Actions ──
        TopUpHeader(
            coins = profile.coins,
            onBack = { nav.popBackStack() },
            onHistoryClick = { showHistorySheet = true },
            onHelpClick = { showHelpDialog = true },
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
        ) {
            // ── Live Wallet Balance Card ──
            WalletBalanceCard(coins = profile.coins)

            Spacer(Modifier.height(18.dp))

            // ── Section Title: Choose a Pack ──
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = "Select Coin Package",
                    color = VyboraTextPrimary,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = "8 Available",
                    color = VyboraTextSecondary,
                    fontSize = 12.sp,
                )
            }

            Spacer(Modifier.height(10.dp))

            // ── 8 Packages Grid (2 Columns) ──
            DemoCoinPacks.chunked(2).forEach { rowPacks ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    rowPacks.forEach { pack ->
                        val isSelected = selectedPack?.id == pack.id
                        Box(Modifier.weight(1f)) {
                            CoinPackCard(
                                pack = pack,
                                isSelected = isSelected,
                                onClick = { selectedPack = pack },
                            )
                        }
                    }
                    if (rowPacks.size == 1) {
                        Spacer(Modifier.weight(1f))
                    }
                }
                Spacer(Modifier.height(10.dp))
            }

            Spacer(Modifier.height(14.dp))

            // ── Store Preview & Payment Safety Notice ──
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFFEDE7F6))
                    .border(1.dp, VyboraBorder, RoundedCornerShape(14.dp))
                    .padding(14.dp),
            ) {
                Row(verticalAlignment = Alignment.Top) {
                    Text("🛡️", fontSize = 16.sp)
                    Spacer(Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Google Play Billing Notice",
                            color = VyboraPurple,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                        )
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = "This application operates in development preview mode. Packs and pricing are demonstrative; simulated purchases credit test coins locally with zero real money charged.",
                            color = VyboraTextSecondary,
                            fontSize = 11.sp,
                            lineHeight = 16.sp,
                        )
                    }
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }

    // ── Purchase Confirmation / Checkout Dialog ──
    val packToBuy = selectedPack
    if (packToBuy != null && checkoutState == CheckoutState.IDLE) {
        AlertDialog(
            onDismissRequest = { selectedPack = null },
            containerColor = VyboraSurface,
            title = {
                Text(
                    text = "Top Up Confirmation",
                    color = VyboraTextPrimary,
                    fontWeight = FontWeight.Bold,
                )
            },
            text = {
                Column {
                    Text(
                        text = "${packToBuy.title} Package",
                        color = VyboraPurple,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "%,d Coins".format(packToBuy.coins),
                        color = VyboraTextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                    )
                    if (packToBuy.bonus > 0) {
                        Text(
                            text = "+ %,d Bonus Coins".format(packToBuy.bonus),
                            color = Color(0xFF2E7D32),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                    Spacer(Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text("Price:", color = VyboraTextSecondary, fontSize = 13.sp)
                        Text(
                            text = "₹%,d".format(packToBuy.price),
                            color = Color(0xFFD81B60),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Demo checkout. Tapping Pay will simulate test fulfillment locally.",
                        color = VyboraTextSecondary,
                        fontSize = 11.sp,
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        checkoutState = CheckoutState.PENDING
                        scope.launch {
                            delay(1200) // Simulate gateway roundtrip
                            val newBalance = profile.coins + packToBuy.total
                            val record = TopUpRecord(
                                packId = packToBuy.id,
                                coins = packToBuy.total,
                                price = packToBuy.price,
                                at = System.currentTimeMillis(),
                                title = packToBuy.title,
                            )
                            store.add(profile.id, record)
                            history = store.history(profile.id)
                            onSave(profile.copy(coins = newBalance))
                            lastPurchasedRecord = record
                            checkoutState = CheckoutState.SUCCESS
                        }
                    }
                ) {
                    Text("Pay ₹${packToBuy.price} [Demo]", color = VyboraPurple, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedPack = null }) {
                    Text("Cancel", color = VyboraTextSecondary)
                }
            },
        )
    }

    // ── Checkout Pending Indicator ──
    if (checkoutState == CheckoutState.PENDING) {
        AlertDialog(
            onDismissRequest = {},
            containerColor = VyboraSurface,
            title = { Text("Processing Payment...", color = VyboraTextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    CircularProgressIndicator(color = VyboraPurple, modifier = Modifier.size(36.dp))
                    Spacer(Modifier.width(16.dp))
                    Text("Verifying demo transaction...", color = VyboraTextSecondary, fontSize = 13.sp)
                }
            },
            confirmButton = {},
        )
    }

    // ── Purchase Success Dialog ──
    if (checkoutState == CheckoutState.SUCCESS && lastPurchasedRecord != null) {
        AlertDialog(
            onDismissRequest = {
                checkoutState = CheckoutState.IDLE
                selectedPack = null
            },
            containerColor = VyboraSurface,
            title = {
                Text(
                    text = "🎉 Top Up Successful!",
                    color = VyboraTextPrimary,
                    fontWeight = FontWeight.Bold,
                )
            },
            text = {
                Column {
                    Text(
                        text = "+%,d Coins Added".format(lastPurchasedRecord!!.coins),
                        color = Color(0xFF2E7D32),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "New Coin Balance: %,d".format(profile.coins),
                        color = VyboraTextPrimary,
                        fontSize = 14.sp,
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Your coins are now available for games, voice lounges, and sending gifts.",
                        color = VyboraTextSecondary,
                        fontSize = 12.sp,
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        checkoutState = CheckoutState.IDLE
                        selectedPack = null
                    }
                ) {
                    Text("Done", color = VyboraPurple, fontWeight = FontWeight.Bold)
                }
            },
        )
    }

    // ── Transaction History Bottom Sheet ──
    if (showHistorySheet) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(
            onDismissRequest = { showHistorySheet = false },
            sheetState = sheetState,
            containerColor = VyboraSurface,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = "Transaction History",
                        color = VyboraTextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = "✕",
                        color = VyboraTextSecondary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .clickable { showHistorySheet = false }
                            .padding(4.dp),
                    )
                }
                Spacer(Modifier.height(12.dp))

                if (history.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "No top-up transactions yet.\nYour purchase history will appear here.",
                            color = VyboraTextSecondary,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center,
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        items(history) { record ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(VyboraBg)
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Image(
                                    painter = painterResource(R.drawable.vb_coin),
                                    contentDescription = null,
                                    modifier = Modifier.size(24.dp),
                                )
                                Spacer(Modifier.width(10.dp))
                                Column(Modifier.weight(1f)) {
                                    Text(
                                        text = "+%,d Coins".format(record.coins),
                                        color = VyboraTextPrimary,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                    )
                                    Text(
                                        text = stampFormat.format(Date(record.at)),
                                        color = VyboraTextSecondary,
                                        fontSize = 11.sp,
                                    )
                                }
                                Text(
                                    text = "₹%,d".format(record.price),
                                    color = VyboraPurple,
                                    fontSize = 14.sp,
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

    // ── Purchase Help & Refund FAQ Dialog ──
    if (showHelpDialog) {
        AlertDialog(
            onDismissRequest = { showHelpDialog = false },
            containerColor = VyboraSurface,
            title = { Text("Top Up & Wallet FAQ", color = VyboraTextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "1. What are Coins used for?",
                        color = VyboraTextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = "Coins are used for sending gifts on voice room stages, unlocking cosmetic badges, and participating in room mini-games.",
                        color = VyboraTextSecondary,
                        fontSize = 12.sp,
                    )
                    Text(
                        text = "2. How fast are coins credited?",
                        color = VyboraTextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = "In production, coins are credited immediately after payment verification from Google Play.",
                        color = VyboraTextSecondary,
                        fontSize = 12.sp,
                    )
                    Text(
                        text = "3. Refund Policy",
                        color = VyboraTextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = "Purchases follow standard Google Play refund guidelines within 48 hours of purchase.",
                        color = VyboraTextSecondary,
                        fontSize = 12.sp,
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showHelpDialog = false }) {
                    Text("Got It", color = VyboraPurple, fontWeight = FontWeight.Bold)
                }
            },
        )
    }
}

@Composable
private fun TopUpHeader(
    coins: Long,
    onBack: () -> Unit,
    onHistoryClick: () -> Unit,
    onHelpClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Back Button
        RoundIconButton(
            painter = painterResource(R.drawable.ic_back),
            onClick = onBack,
            contentDescription = "Back",
            size = 36.dp,
            iconSize = 20.dp,
            tint = VyboraTextPrimary,
        )

        Spacer(Modifier.width(10.dp))

        // Title
        Text(
            text = "Top Up",
            color = VyboraTextPrimary,
            fontSize = 20.sp,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier.weight(1f),
        )

        // Help '?' Button
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(VyboraSurface)
                .border(1.dp, VyboraBorder, CircleShape)
                .clickable(onClick = onHelpClick),
            contentAlignment = Alignment.Center,
        ) {
            Text("?", color = VyboraTextSecondary, fontSize = 15.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(Modifier.width(8.dp))

        // History Button
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(VyboraSurface)
                .border(1.dp, VyboraBorder, CircleShape)
                .clickable(onClick = onHistoryClick),
            contentAlignment = Alignment.Center,
        ) {
            Text("📜", fontSize = 14.sp)
        }
    }
}

@Composable
private fun WalletBalanceCard(coins: Long) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(20.dp))
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.horizontalGradient(
                    listOf(Color(0xFF8E24AA), Color(0xFF5E35B1))
                )
            )
            .padding(20.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column {
                Text(
                    text = "CURRENT COIN BALANCE",
                    color = Color(0xFFD1C4E9),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp,
                )
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(R.drawable.vb_coin),
                        contentDescription = null,
                        modifier = Modifier.size(28.dp),
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "%,d".format(coins),
                        color = Color.White,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                    )
                }
                Spacer(Modifier.height(2.dp))
                Text(
                    text = "Ready for voice rooms, mini-games & gifts",
                    color = Color(0xFFEDE7F6),
                    fontSize = 11.sp,
                )
            }

            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center,
            ) {
                Image(
                    painter = painterResource(R.drawable.vb_gift_pack),
                    contentDescription = null,
                    modifier = Modifier.size(34.dp),
                )
            }
        }
    }
}

@Composable
private fun CoinPackCard(
    pack: CoinPack,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(if (isSelected) 3.dp else 1.dp, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .background(VyboraSurface)
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) VyboraPurple else VyboraBorder,
                shape = RoundedCornerShape(16.dp),
            )
            .clickable(onClick = onClick)
            .padding(12.dp),
    ) {
        // Tag Badge (if any)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            pack.tag?.let { tag ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(if (tag == "HOT" || tag == "BEST VALUE") Color(0xFFFF5722) else VyboraGold)
                        .padding(horizontal = 5.dp, vertical = 2.dp),
                ) {
                    Text(tag, color = Color.White, fontSize = 8.sp, fontWeight = FontWeight.ExtraBold)
                }
            } ?: Spacer(Modifier.width(1.dp))

            Text(
                text = pack.title,
                color = VyboraTextSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }

        Spacer(Modifier.height(6.dp))

        // Coin Icon + Count
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(R.drawable.vb_coin),
                contentDescription = null,
                modifier = Modifier.size(22.dp),
            )
            Spacer(Modifier.width(6.dp))
            Text(
                text = "%,d".format(pack.coins),
                color = VyboraTextPrimary,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
            )
        }

        // Bonus Pill
        Spacer(Modifier.height(4.dp))
        if (pack.bonus > 0) {
            Text(
                text = "+%,d Bonus".format(pack.bonus),
                color = Color(0xFF2E7D32),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
            )
        } else {
            Text(
                text = "Base pack",
                color = VyboraTextSecondary,
                fontSize = 11.sp,
            )
        }

        Spacer(Modifier.height(10.dp))

        // Price Button Pill
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(32.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(if (isSelected) VyboraPurple else VyboraBg)
                .border(1.dp, if (isSelected) VyboraPurple else VyboraBorder, RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "₹%,d".format(pack.price),
                color = if (isSelected) Color.White else VyboraPurple,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}
