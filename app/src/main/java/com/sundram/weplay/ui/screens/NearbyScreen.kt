package com.sundram.weplay.ui.screens

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import com.sundram.weplay.R
import com.sundram.weplay.ui.components.AuroraEmptyStateCard
import com.sundram.weplay.ui.components.AuroraGlassCard
import com.sundram.weplay.ui.components.AuroraPreviewNoticePill
import com.sundram.weplay.ui.components.AuroraScreenCanvas
import com.sundram.weplay.ui.components.AvatarWithFrame
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

@Composable
fun NearbyScreen(nav: NavHostController) {
    val context = LocalContext.current
    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION,
            ) == PackageManager.PERMISSION_GRANTED
        )
    }
    var permissionDenied by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasPermission = granted
        permissionDenied = !granted
    }

    val previewNearbyPlayers = remember {
        exploreUsers.filter { it.distance.isNotBlank() }
    }

    AuroraScreenCanvas(
        title = "Nearby Players",
        subtitle = "Approximate area discovery",
        onBack = { nav.popBackStack() },
        scrollable = true,
    ) {
        AuroraGlassCard {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(AuroraElevated)
                        .border(1.dp, CrystalBlue.copy(alpha = 0.45f), CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_location_pin),
                        contentDescription = "Approximate location",
                        tint = CrystalBlue,
                        modifier = Modifier.size(22.dp),
                    )
                }
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(
                        text = when {
                            hasPermission -> "Approximate location enabled"
                            permissionDenied -> "Location permission declined"
                            else -> "Coarse Location Privacy"
                        },
                        color = CloudWhite,
                        fontSize = 15.5.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = when {
                            hasPermission -> "Ready for regional matching once the live discovery service is connected."
                            permissionDenied -> "Enable approximate location in settings if you want to explore nearby rooms."
                            else -> "Paradise only asks for coarse city-level permission and never reads exact GPS coordinates."
                        },
                        color = DreamLavender.copy(alpha = 0.85f),
                        fontSize = 12.sp,
                        lineHeight = 17.sp,
                    )
                }
            }

            if (!hasPermission) {
                Spacer(Modifier.height(14.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(
                        onClick = {
                            permissionLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MysticPurple,
                            contentColor = CloudWhite,
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .defaultMinSize(minHeight = 44.dp),
                    ) {
                        Text(
                            text = if (permissionDenied) "Allow Location" else "Enable Nearby",
                            fontWeight = FontWeight.Bold,
                        )
                    }
                    if (permissionDenied) {
                        OutlinedButton(
                            onClick = {
                                val intent = Intent(
                                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                    Uri.fromParts("package", context.packageName, null),
                                )
                                context.startActivity(intent)
                            },
                            border = BorderStroke(1.dp, AuroraBorder),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.defaultMinSize(minHeight = 44.dp),
                        ) {
                            Text("App Settings", color = CrystalBlue)
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(14.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Directory Preview",
                color = CloudWhite,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
            )
            AuroraPreviewNoticePill("Simulated area tags")
        }

        Spacer(Modifier.height(10.dp))

        if (!hasPermission && previewNearbyPlayers.isEmpty()) {
            AuroraEmptyStateCard(
                title = "Location access needed for local radar",
                message = "In the meantime, explore global live voice rooms and multiplayer games.",
                emoji = "📍",
                actionLabel = "Explore Voice Rooms",
                onAction = { nav.navigateSafe(Routes.ROOM) },
            )
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                previewNearbyPlayers.forEach { player ->
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = AuroraSurface.copy(alpha = 0.92f),
                        border = BorderStroke(1.dp, AuroraBorder),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { nav.navigateSafe(Routes.userDetailRoute(player.id)) },
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            AvatarWithFrame(
                                name = player.name,
                                photoPath = null,
                                size = 48.dp,
                                frameTier = player.frameTier,
                                animated = false,
                            )
                            Spacer(Modifier.width(12.dp))
                            Column(Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = player.name,
                                        color = CloudWhite,
                                        fontSize = 14.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                    )
                                    Spacer(Modifier.width(6.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(RoyalGold.copy(alpha = 0.18f))
                                            .padding(horizontal = 6.dp, vertical = 2.dp),
                                    ) {
                                        Text(
                                            text = "Lv.${player.level}",
                                            color = RoyalGold,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                        )
                                    }
                                }
                                Spacer(Modifier.height(2.dp))
                                Text(
                                    text = "${player.distance} · ${player.statusText}",
                                    color = if (player.isOnline) AuroraSuccess else DreamLavender,
                                    fontSize = 12.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .defaultMinSize(minHeight = 36.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(RoyalViolet)
                                    .clickable { nav.navigateSafe(Routes.userDetailRoute(player.id)) }
                                    .padding(horizontal = 12.dp, vertical = 7.dp),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text("Profile", color = CloudWhite, fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}
