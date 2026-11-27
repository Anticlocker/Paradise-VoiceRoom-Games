package com.sundram.weplay.ui.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.sundram.weplay.data.ProfileStore
import com.sundram.weplay.data.UserPrefs
import com.sundram.weplay.ui.components.AuroraGlassCard
import com.sundram.weplay.ui.components.AuroraScreenCanvas
import com.sundram.weplay.ui.components.navigateSafe
import com.sundram.weplay.ui.nav.Routes
import com.sundram.weplay.ui.theme.AuroraBorder
import com.sundram.weplay.ui.theme.AuroraElevated
import com.sundram.weplay.ui.theme.CloudWhite
import com.sundram.weplay.ui.theme.CrystalBlue
import com.sundram.weplay.ui.theme.DeepInk
import com.sundram.weplay.ui.theme.DreamLavender
import com.sundram.weplay.ui.theme.MysticPurple
import com.sundram.weplay.ui.theme.RoyalGold
import com.sundram.weplay.ui.theme.RoyalViolet

private val WallThemes = listOf("Aurora Nightfall", "Velvet Cosmos", "Gold Starlight")

@Composable
fun MySpaceScreen(nav: NavHostController) {
    val context = LocalContext.current
    val profile = remember { ProfileStore(context).load() }
    val userPrefs = remember { UserPrefs(context) }
    val spacePrefs = remember { context.getSharedPreferences("paradise_my_space", Context.MODE_PRIVATE) }

    var spaceMood by remember {
        mutableStateOf(spacePrefs.getString("mood", "Available for voice chats & Spy rounds!") ?: "")
    }
    var selectedWallTheme by remember {
        mutableStateOf(spacePrefs.getString("wall_theme", WallThemes.first()) ?: WallThemes.first())
    }
    val recentRooms = remember { userPrefs.getRecentVisitedRooms() }

    AuroraScreenCanvas(
        title = "My Space",
        subtitle = "Personal lounge showcase & status",
        onBack = { nav.popBackStack() },
        scrollable = true,
    ) {
        AuroraGlassCard {
            Text("Showcase Card", color = CrystalBlue, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(6.dp))
            Text(
                text = profile?.name ?: "Player",
                color = CloudWhite,
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
            )
            Text(
                text = "Lv.${profile?.level ?: 1} · ID: ${profile?.id ?: "player"}",
                color = RoyalGold,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(Modifier.height(10.dp))
            Text("Custom Status Note", color = DreamLavender, fontSize = 12.sp)
            Spacer(Modifier.height(6.dp))
            OutlinedTextField(
                value = spaceMood,
                onValueChange = { if (it.length <= 80) spaceMood = it },
                singleLine = false,
                maxLines = 2,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = CloudWhite,
                    unfocusedTextColor = CloudWhite,
                    focusedBorderColor = CrystalBlue,
                    unfocusedBorderColor = AuroraBorder,
                    focusedContainerColor = DeepInk,
                    unfocusedContainerColor = DeepInk,
                ),
            )
            Spacer(Modifier.height(10.dp))
            Button(
                onClick = {
                    spacePrefs.edit().putString("mood", spaceMood.trim()).apply()
                    Toast.makeText(context, "My Space status updated", Toast.LENGTH_SHORT).show()
                },
                colors = ButtonDefaults.buttonColors(containerColor = MysticPurple, contentColor = CloudWhite),
                shape = RoundedCornerShape(12.dp),
            ) {
                Text("Save Status", fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }

        Spacer(Modifier.height(12.dp))

        AuroraGlassCard {
            Text("Profile Space Atmosphere", color = CrystalBlue, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                WallThemes.forEach { themeName ->
                    val selected = themeName == selectedWallTheme
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (selected) RoyalViolet else AuroraElevated)
                            .border(1.dp, if (selected) CrystalBlue else AuroraBorder, RoundedCornerShape(12.dp))
                            .clickable {
                                selectedWallTheme = themeName
                                spacePrefs.edit().putString("wall_theme", themeName).apply()
                            }
                            .padding(vertical = 10.dp, horizontal = 8.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = themeName,
                            color = if (selected) CloudWhite else DreamLavender,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        AuroraGlassCard {
            Text("Recent Voice Lounge Footprints", color = CrystalBlue, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            if (recentRooms.isEmpty()) {
                Text(
                    text = "No recent room visits recorded on this device yet.",
                    color = DreamLavender.copy(alpha = 0.8f),
                    fontSize = 12.5.sp,
                )
            } else {
                recentRooms.take(4).forEach { (roomId, _) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(AuroraElevated)
                            .clickable { nav.navigateSafe(Routes.roomRoute(roomId)) }
                            .padding(horizontal = 12.dp, vertical = 9.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text("Room #$roomId", color = CloudWhite, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        Text("Revisit ›", color = CrystalBlue, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(Modifier.height(14.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            OutlinedButton(
                onClick = { nav.navigateSafe(Routes.BACKPACK) },
                border = BorderStroke(1.dp, AuroraBorder),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .weight(1f)
                    .defaultMinSize(minHeight = 46.dp),
            ) {
                Text("Backpack", color = DreamLavender, fontWeight = FontWeight.Bold)
            }
            Button(
                onClick = { nav.navigateSafe(Routes.PROFILE_EDIT) },
                colors = ButtonDefaults.buttonColors(containerColor = RoyalViolet, contentColor = CloudWhite),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .weight(1f)
                    .defaultMinSize(minHeight = 46.dp),
            ) {
                Text("Edit Profile", fontWeight = FontWeight.Bold)
            }
        }
    }
}
