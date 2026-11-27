package com.sundram.weplay.ui.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.size
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
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.sundram.weplay.R
import com.sundram.weplay.data.ProfileStore
import com.sundram.weplay.ui.components.AuroraEmptyStateCard
import com.sundram.weplay.ui.components.AuroraGlassCard
import com.sundram.weplay.ui.components.AuroraPreviewNoticePill
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

private data class CosmeticShopItem(
    val id: String,
    val title: String,
    val category: String,
    val description: String,
    val priceCoins: Long,
    val iconRes: Int,
)

private val CosmeticsCatalog = listOf(
    CosmeticShopItem(
        id = "shop_ent_meteor",
        title = "Aurora Meteor Entrance",
        category = "Entrances",
        description = "Displays a purple comet welcome banner when you enter voice rooms.",
        priceCoins = 220L,
        iconRes = R.drawable.ap_me_shop,
    ),
    CosmeticShopItem(
        id = "shop_ent_palace",
        title = "Royal Palace Fanfare",
        category = "Entrances",
        description = "Golden crescent crest flare upon stepping onto stage.",
        priceCoins = 380L,
        iconRes = R.drawable.ap_me_vip,
    ),
    CosmeticShopItem(
        id = "shop_bub_nebula",
        title = "Nebula Chat Bubble",
        category = "Chat Bubbles",
        description = "Iridescent lavender border for your in-room text messages.",
        priceCoins = 120L,
        iconRes = R.drawable.ap_chats_hybrid_ic_gift,
    ),
    CosmeticShopItem(
        id = "shop_mic_crystal",
        title = "Crystal Harmonic Mic Halo",
        category = "Mic Rings",
        description = "Cyan speaking pulse ripple around your stage seat avatar.",
        priceCoins = 180L,
        iconRes = R.drawable.ap_voice_emblem_mic,
    ),
    CosmeticShopItem(
        id = "shop_badge_starlight",
        title = "Starlight Wing Badge",
        category = "Badges",
        description = "Showcase winged badge displayed in My Space and room cards.",
        priceCoins = 150L,
        iconRes = R.drawable.ap_me_badges,
    ),
)

private val ShopTabs = listOf("All", "Entrances", "Chat Bubbles", "Mic Rings", "Badges")

/**
 * Dedicated Cosmetics & Effects Shop separated cleanly from Gifts and Top Up.
 */
@Composable
fun ShopScreen(nav: NavHostController) {
    val context = LocalContext.current
    val profileStore = remember { ProfileStore(context) }
    val shopPrefs = remember { context.getSharedPreferences("paradise_cosmetics_shop", Context.MODE_PRIVATE) }

    var coins by remember { mutableLongStateOf(profileStore.coinBalance()) }
    var selectedTab by remember { mutableStateOf("All") }
    var ownedIds by remember {
        mutableStateOf(shopPrefs.getStringSet("owned", emptySet()) ?: emptySet())
    }
    var equippedIds by remember {
        mutableStateOf(shopPrefs.getStringSet("equipped", emptySet()) ?: emptySet())
    }
    var feedback by remember { mutableStateOf<String?>(null) }

    val items = remember(selectedTab) {
        if (selectedTab == "All") CosmeticsCatalog
        else CosmeticsCatalog.filter { it.category == selectedTab }
    }

    AuroraScreenCanvas(
        title = "Cosmetics Shop",
        subtitle = "Entrances, chat bubbles & mic halos",
        onBack = { nav.popBackStack() },
        rightContent = {
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .background(AuroraElevated)
                    .border(1.dp, RoyalGold.copy(alpha = 0.45f), RoundedCornerShape(14.dp))
                    .clickable { nav.navigateSafe(Routes.TOPUP) }
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("$coins coins +", color = RoyalGold, fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
            }
        },
        scrollable = true,
    ) {
        // Cross-links banner for Room Themes & Avatar Frames
        AuroraGlassCard {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(Modifier.weight(1f)) {
                    Text("Looking for Room Themes?", color = CloudWhite, fontSize = 14.5.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(2.dp))
                    Text(
                        "Preview and equip 10 voice lounge themes or unlock avatar frames.",
                        color = DreamLavender.copy(alpha = 0.85f),
                        fontSize = 11.5.sp,
                    )
                }
                Spacer(Modifier.width(10.dp))
                Button(
                    onClick = { nav.navigateSafe("profile/gifts/themes") },
                    colors = ButtonDefaults.buttonColors(containerColor = MysticPurple, contentColor = CloudWhite),
                    shape = RoundedCornerShape(12.dp),
                ) {
                    Text("Themes ›", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }

        Spacer(Modifier.height(10.dp))
        AuroraPreviewNoticePill("Simulated coin cosmetics · Local profile storage")
        Spacer(Modifier.height(12.dp))

        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(ShopTabs, key = { it }) { tab ->
                val selected = tab == selectedTab
                Box(
                    modifier = Modifier
                        .defaultMinSize(minHeight = 36.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (selected) RoyalViolet else AuroraSurface)
                        .border(1.dp, if (selected) CrystalBlue.copy(alpha = 0.55f) else AuroraBorder, RoundedCornerShape(16.dp))
                        .bounceClick { selectedTab = tab }
                        .padding(horizontal = 14.dp, vertical = 7.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = tab,
                        color = if (selected) CloudWhite else DreamLavender,
                        fontSize = 12.sp,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                    )
                }
            }
        }

        feedback?.let { msg ->
            Spacer(Modifier.height(10.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(AuroraElevated)
                    .border(1.dp, CrystalBlue.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
            ) {
                Text(msg, color = CrystalBlue, fontSize = 12.sp, fontWeight = FontWeight.Medium)
            }
        }

        Spacer(Modifier.height(12.dp))

        if (items.isEmpty()) {
            AuroraEmptyStateCard(
                title = "No items in this tab",
                message = "Try switching back to All or check Avatar Frames in Gifts.",
                emoji = "🛍️",
                actionLabel = "Show All",
                onAction = { selectedTab = "All" },
            )
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items.forEach { item ->
                    val owned = item.id in ownedIds
                    val equipped = item.id in equippedIds

                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = AuroraSurface.copy(alpha = 0.92f),
                        border = BorderStroke(
                            1.dp,
                            if (equipped) AuroraSuccess.copy(alpha = 0.6f) else AuroraBorder,
                        ),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Image(
                                painter = painterResource(item.iconRes),
                                contentDescription = item.title,
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(AuroraElevated)
                                    .padding(6.dp),
                            )
                            Spacer(Modifier.width(12.dp))
                            Column(Modifier.weight(1f)) {
                                Text(
                                    text = item.title,
                                    color = CloudWhite,
                                    fontSize = 14.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                                Text(
                                    text = item.category,
                                    color = CrystalBlue,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                )
                                Spacer(Modifier.height(2.dp))
                                Text(
                                    text = item.description,
                                    color = DreamLavender.copy(alpha = 0.84f),
                                    fontSize = 11.5.sp,
                                    lineHeight = 15.sp,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            }
                            Spacer(Modifier.width(10.dp))
                            when {
                                equipped -> {
                                    OutlinedButton(
                                        onClick = {
                                            val next = equippedIds - item.id
                                            equippedIds = next
                                            shopPrefs.edit().putStringSet("equipped", next).apply()
                                            feedback = "${item.title} unequipped."
                                        },
                                        border = BorderStroke(1.dp, AuroraSuccess.copy(alpha = 0.6f)),
                                        shape = RoundedCornerShape(12.dp),
                                    ) {
                                        Text("Equipped", color = AuroraSuccess, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                                owned -> {
                                    Button(
                                        onClick = {
                                            val next = equippedIds + item.id
                                            equippedIds = next
                                            shopPrefs.edit().putStringSet("equipped", next).apply()
                                            feedback = "Equipped ${item.title}!"
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = RoyalViolet, contentColor = CloudWhite),
                                        shape = RoundedCornerShape(12.dp),
                                    ) {
                                        Text("Equip", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                                else -> {
                                    Button(
                                        onClick = {
                                            if (coins < item.priceCoins) {
                                                feedback = "Need ${item.priceCoins - coins} more coins for ${item.title}."
                                                Toast.makeText(context, "Not enough coins", Toast.LENGTH_SHORT).show()
                                            } else {
                                                val newBal = profileStore.adjustCoins(-item.priceCoins)
                                                coins = newBal
                                                val nextOwned = ownedIds + item.id
                                                val nextEq = equippedIds + item.id
                                                ownedIds = nextOwned
                                                equippedIds = nextEq
                                                shopPrefs.edit()
                                                    .putStringSet("owned", nextOwned)
                                                    .putStringSet("equipped", nextEq)
                                                    .apply()
                                                feedback = "Unlocked & equipped ${item.title}!"
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = RoyalGold,
                                            contentColor = Color(0xFF1A0F3B),
                                        ),
                                        shape = RoundedCornerShape(12.dp),
                                    ) {
                                        Text("${item.priceCoins}c", fontSize = 11.5.sp, fontWeight = FontWeight.ExtraBold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
