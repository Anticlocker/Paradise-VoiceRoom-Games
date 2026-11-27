package com.sundram.weplay.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.sundram.weplay.ui.components.AuroraDetailScaffold
import com.sundram.weplay.ui.components.AuroraGlassCard
import com.sundram.weplay.ui.components.AuroraPreviewNoticePill
import com.sundram.weplay.ui.nav.Routes
import com.sundram.weplay.ui.nav.switchTab
import com.sundram.weplay.ui.theme.AuroraTurquoise
import com.sundram.weplay.ui.theme.CloudWhite
import com.sundram.weplay.ui.theme.CrystalBlue
import com.sundram.weplay.ui.theme.DeepInk
import com.sundram.weplay.ui.theme.DreamLavender
import com.sundram.weplay.ui.theme.MysticPurple
import com.sundram.weplay.ui.theme.RoyalGold

private data class GuideStep(
    val number: String,
    val title: String,
    val body: String,
    val actionLabel: String,
    val color: Color,
    val onAction: (NavHostController) -> Unit,
)

private val guideSteps = listOf(
    GuideStep(
        number = "01",
        title = "Play party games",
        body = "Open Ludo Classic or Who's the Spy right away from the Games tab, or browse live lobbies.",
        actionLabel = "Explore Games",
        color = CrystalBlue,
        onAction = { switchTab(it, Routes.HUB) },
    ),
    GuideStep(
        number = "02",
        title = "Join live voice rooms",
        body = "Pick an unlocked seat, try 10 celestial room themes, send animated gifts, and raise room crest levels.",
        actionLabel = "Open Voice Rooms",
        color = MysticPurple,
        onAction = { switchTab(it, Routes.ROOM) },
    ),
    GuideStep(
        number = "03",
        title = "Safety & local controls",
        body = "Mute, block, or report players anytime from rooms, profiles, and Settings > Privacy & Safety.",
        actionLabel = "Safety Settings",
        color = AuroraTurquoise,
        onAction = { it.navigate(Routes.PRIVACY_SETTINGS) },
    ),
    GuideStep(
        number = "04",
        title = "Local preview mode",
        body = "Coins, VIP items, rankings, and demo accounts stay on this device until connected to your server.",
        actionLabel = "Open Settings",
        color = RoyalGold,
        onAction = { it.navigate(Routes.SETTINGS) },
    ),
)

@Composable
fun OnboardingScreen(nav: NavHostController) {
    AuroraDetailScaffold(
        nav = nav,
        title = "Paradise Guide",
        subtitle = "Welcome tour & safety overview",
    ) {
        AuroraPreviewNoticePill("Paradise - Voice Room & Games · Local Preview")
        Spacer(Modifier.height(14.dp))

        guideSteps.forEach { step ->
            AuroraGlassCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top,
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(11.dp))
                            .background(step.color.copy(alpha = 0.18f)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            step.number,
                            color = step.color,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.ExtraBold,
                        )
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            step.title,
                            color = CloudWhite,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            step.body,
                            color = DreamLavender,
                            fontSize = 12.5.sp,
                            lineHeight = 18.sp,
                        )
                        Spacer(Modifier.height(10.dp))
                        Row(horizontalArrangement = Arrangement.Start) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(step.color)
                                    .clickable { step.onAction(nav) }
                                    .padding(horizontal = 12.dp, vertical = 7.dp),
                            ) {
                                Text(
                                    step.actionLabel,
                                    color = DeepInk,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                )
                            }
                        }
                    }
                }
            }
            Spacer(Modifier.height(12.dp))
        }
    }
}
