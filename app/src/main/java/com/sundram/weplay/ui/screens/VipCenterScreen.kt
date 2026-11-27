package com.sundram.weplay.ui.screens

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.sundram.weplay.R
import com.sundram.weplay.data.ProfileStore
import com.sundram.weplay.ui.components.AuroraGlassCard
import com.sundram.weplay.ui.components.AuroraPreviewNoticePill
import com.sundram.weplay.ui.components.AuroraScreenCanvas
import com.sundram.weplay.ui.components.navigateSafe
import com.sundram.weplay.ui.nav.Routes
import com.sundram.weplay.ui.theme.AuroraBorder
import com.sundram.weplay.ui.theme.AuroraElevated
import com.sundram.weplay.ui.theme.AuroraSurface
import com.sundram.weplay.ui.theme.CloudWhite
import com.sundram.weplay.ui.theme.CrystalBlue
import com.sundram.weplay.ui.theme.DreamLavender
import com.sundram.weplay.ui.theme.MysticPurple
import com.sundram.weplay.ui.theme.RoyalGold
import com.sundram.weplay.ui.theme.RoyalViolet
import com.sundram.weplay.ui.theme.SunsetRose

private data class VipTierSpec(
    val level: Int,
    val title: String,
    val subtitle: String,
    val accent: Color,
    val badgeRes: Int,
    val perks: List<String>,
)

private val VipTiers = listOf(
    VipTierSpec(
        level = 1,
        title = "Starlight Noble",
        subtitle = "Entry Sovereign",
        accent = CrystalBlue,
        badgeRes = R.drawable.ap_me_vip,
        perks = listOf(
            "Starlight Noble profile crest",
            "Priority seat request queue in rooms",
            "Exclusive greeting bubble color",
        ),
    ),
    VipTierSpec(
        level = 2,
        title = "Moonlit Knight",
        subtitle = "Silver Aura",
        accent = DreamLavender,
        badgeRes = R.drawable.ap_me_vip,
        perks = listOf(
            "All Noble benefits included",
            "Animated entrance notice in public voice rooms",
            "20% extra daily free gift cap",
        ),
    ),
    VipTierSpec(
        level = 3,
        title = "Aurora Sovereign",
        subtitle = "Gold Tier",
        accent = RoyalGold,
        badgeRes = R.drawable.ap_me_vip,
        perks = listOf(
            "Celestial gold mic ring across all stages",
            "Unlocked Royal Pavilion theme eligibility",
            "Dedicated VIP badge next to chat messages",
        ),
    ),
    VipTierSpec(
        level = 4,
        title = "Celestial Monarch",
        subtitle = "Top Tier",
        accent = SunsetRose,
        badgeRes = R.drawable.ap_me_vip,
        perks = listOf(
            "All Aurora Sovereign benefits",
            "Custom room entrance spotlight banner",
            "Exclusive Monarch frame in Cosmetics Shop",
        ),
    ),
)

/**
 * Dedicated VIP Center screen replacing the old alias to Top Up.
 */
@Composable
fun VipCenterScreen(nav: NavHostController) {
    val context = LocalContext.current
    val coins = remember { ProfileStore(context).coinBalance() }
    var selectedTierIdx by remember { mutableIntStateOf(2) }
    val activeTier = VipTiers[selectedTierIdx]

    AuroraScreenCanvas(
        title = "VIP Center",
        subtitle = "Sovereign privileges & stage perks",
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
                Text(
                    text = "$coins coins",
                    color = RoyalGold,
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        },
        scrollable = true,
    ) {
        // Hero Card
        Surface(
            shape = RoundedCornerShape(22.dp),
            color = AuroraSurface,
            border = BorderStroke(1.dp, activeTier.accent.copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color(0xFF3A1F7A),
                                AuroraSurface,
                            )
                        )
                    )
                    .padding(18.dp),
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Image(
                        painter = painterResource(activeTier.badgeRes),
                        contentDescription = activeTier.title,
                        modifier = Modifier.size(64.dp),
                    )
                    Spacer(Modifier.height(10.dp))
                    Text(
                        text = "VIP ${activeTier.level} · ${activeTier.title}",
                        color = activeTier.accent,
                        fontSize = 19.sp,
                        fontWeight = FontWeight.ExtraBold,
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = activeTier.subtitle,
                        color = DreamLavender,
                        fontSize = 12.5.sp,
                    )
                    Spacer(Modifier.height(10.dp))
                    AuroraPreviewNoticePill("Preview privileges · No live billing active")
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        Text(
            text = "Compare Tiers",
            color = CloudWhite,
            fontSize = 15.5.sp,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(10.dp))

        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(VipTiers.indices.toList(), key = { it }) { idx ->
                val tier = VipTiers[idx]
                val selected = idx == selectedTierIdx
                Box(
                    modifier = Modifier
                        .defaultMinSize(minHeight = 40.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (selected) RoyalViolet else AuroraSurface)
                        .border(
                            1.dp,
                            if (selected) tier.accent else AuroraBorder,
                            RoundedCornerShape(14.dp),
                        )
                        .clickable { selectedTierIdx = idx }
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "VIP ${tier.level}",
                        color = if (selected) CloudWhite else DreamLavender,
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }

        Spacer(Modifier.height(14.dp))

        AuroraGlassCard {
            Text(
                text = "${activeTier.title} Privileges",
                color = CloudWhite,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.height(10.dp))
            activeTier.perks.forEach { perk ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 5.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(activeTier.accent),
                    )
                    Spacer(Modifier.width(10.dp))
                    Text(
                        text = perk,
                        color = DreamLavender.copy(alpha = 0.92f),
                        fontSize = 13.sp,
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            OutlinedButton(
                onClick = { nav.navigateSafe(Routes.SHOP) },
                border = BorderStroke(1.dp, CrystalBlue.copy(alpha = 0.55f)),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .weight(1f)
                    .defaultMinSize(minHeight = 48.dp),
            ) {
                Text("Cosmetics Shop", color = CrystalBlue, fontWeight = FontWeight.Bold)
            }
            Button(
                onClick = { nav.navigateSafe(Routes.TOPUP) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MysticPurple,
                    contentColor = CloudWhite,
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .weight(1f)
                    .defaultMinSize(minHeight = 48.dp),
            ) {
                Text("Top Up Coins", fontWeight = FontWeight.Bold)
            }
        }
    }
}
