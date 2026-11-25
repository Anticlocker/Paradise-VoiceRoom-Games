package com.sundram.weplay.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Icon
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavHostController
import com.sundram.weplay.R
import com.sundram.weplay.data.Demo
import com.sundram.weplay.data.NotificationItem
import com.sundram.weplay.ui.components.TopBackBar
import com.sundram.weplay.ui.components.navigateSafe
import com.sundram.weplay.ui.theme.CyanBrand
import com.sundram.weplay.ui.theme.PageWhite
import com.sundram.weplay.ui.theme.TextMain
import com.sundram.weplay.ui.theme.TextMuted

@Composable
fun NotificationsScreen(nav: NavHostController) {
    var notifications by remember { mutableStateOf(Demo.notifications) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PageWhite)
            .statusBarsPadding(),
    ) {
        TopBackBar(
            title = "Notifications",
            onBack = { nav.popBackStack() },
            rightContent = {
                TextButton(
                    onClick = {
                        notifications = notifications.map { it.copy(read = true) }
                    },
                ) {
                    Text("Mark all read", color = CyanBrand, fontSize = 13.sp)
                }
            },
        )

        if (notifications.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_bell),
                        contentDescription = null,
                        tint = androidx.compose.ui.graphics.Color(0xFFB39DDB),
                        modifier = Modifier.size(44.dp),
                    )
                    Spacer(Modifier.height(12.dp))
                    Text(
                        "No notifications yet",
                        color = TextMain,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(
                        "We'll notify you when friends interact with you",
                        color = TextMuted,
                        fontSize = 13.sp,
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
            ) {
                items(notifications, key = { it.id }) { item ->
                    NotificationRow(
                        item = item,
                        onClick = {
                            notifications = notifications.map {
                                if (it.id == item.id) it.copy(read = true) else it
                            }
                            item.destinationRoute?.let { route ->
                                nav.navigateSafe(route)
                            }
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun NotificationRow(
    item: NotificationItem,
    onClick: () -> Unit,
) {
    val iconRes = when (item.type) {
        "friend_request" -> R.drawable.ic_users
        "room_invite" -> R.drawable.ic_mic
        "ranking_reward" -> R.drawable.ic_crown
        "family_invite" -> R.drawable.ic_verified
        else -> R.drawable.ic_comment
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(if (item.read) Color(0x08000000) else Color(0x1200CCF9))
            .clickable(onClick = onClick)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(Color(0x1A00CCF9)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = null,
                tint = androidx.compose.ui.graphics.Color.White,
                modifier = Modifier.size(20.dp),
            )
        }

        Spacer(Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = item.title,
                    color = TextMain,
                    fontSize = 14.sp,
                    fontWeight = if (item.read) FontWeight.Medium else FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                )
                Text(
                    text = item.timestamp,
                    color = TextMuted,
                    fontSize = 11.sp,
                )
            }
            Spacer(Modifier.height(3.dp))
            Text(
                text = item.body,
                color = if (item.read) TextMuted else TextMain,
                fontSize = 12.sp,
                lineHeight = 16.sp,
            )
        }

        if (!item.read) {
            Spacer(Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(CyanBrand),
            )
        }
    }
}
