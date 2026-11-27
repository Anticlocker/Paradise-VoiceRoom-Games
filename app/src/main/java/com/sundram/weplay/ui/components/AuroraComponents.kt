package com.sundram.weplay.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.sundram.weplay.R
import com.sundram.weplay.ui.theme.AuroraBorder
import com.sundram.weplay.ui.theme.AuroraElevated
import com.sundram.weplay.ui.theme.AuroraError
import com.sundram.weplay.ui.theme.AuroraSurface
import com.sundram.weplay.ui.theme.CelestialNavy
import com.sundram.weplay.ui.theme.CloudWhite
import com.sundram.weplay.ui.theme.CrystalBlue
import com.sundram.weplay.ui.theme.DeepInk
import com.sundram.weplay.ui.theme.DreamLavender
import com.sundram.weplay.ui.theme.MysticPurple
import com.sundram.weplay.ui.theme.RoyalGold
import com.sundram.weplay.ui.theme.RoyalViolet

/**
 * Shared dark Aurora Paradise 3.0 scaffold for secondary/detail screens with system-bar insets
 * and accessible 48dp minimum touch targets on navigation controls.
 */
@Composable
fun AuroraDetailScaffold(
    nav: NavHostController,
    title: String,
    subtitle: String = "",
    rightContent: (@Composable () -> Unit)? = null,
    bottomBar: (@Composable () -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    AuroraScreenCanvas(
        title = title,
        subtitle = subtitle,
        onBack = { nav.popBackStack() },
        rightContent = rightContent,
        bottomBar = bottomBar,
        scrollable = true,
        content = content,
    )
}

@Composable
fun AuroraScreenCanvas(
    title: String,
    subtitle: String = "",
    onBack: (() -> Unit)? = null,
    rightContent: (@Composable () -> Unit)? = null,
    bottomBar: (@Composable () -> Unit)? = null,
    scrollable: Boolean = false,
    content: @Composable ColumnScope.() -> Unit,
) {
    val bgBrush = Brush.verticalGradient(
        listOf(
            Color(0xFF0E082D),
            CelestialNavy,
            Color(0xFF1A0F45),
        )
    )
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(bgBrush)
            .statusBarsPadding()
            .then(if (bottomBar == null) Modifier.navigationBarsPadding() else Modifier),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (onBack != null) {
                RoundIconButton(
                    painter = painterResource(R.drawable.ic_back),
                    onClick = onBack,
                    contentDescription = "Back",
                    size = 44.dp,
                    iconSize = 20.dp,
                    tint = CloudWhite,
                    bg = AuroraSurface.copy(alpha = 0.85f),
                    modifier = Modifier.defaultMinSize(minWidth = 48.dp, minHeight = 48.dp),
                )
                Spacer(Modifier.width(10.dp))
            }
            Column(Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = CloudWhite,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                if (subtitle.isNotBlank()) {
                    Text(
                        text = subtitle,
                        color = DreamLavender.copy(alpha = 0.82f),
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
            if (rightContent != null) {
                Spacer(Modifier.width(8.dp))
                rightContent()
            }
        }

        if (scrollable) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            ) {
                item {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        content()
                    }
                    Spacer(Modifier.height(20.dp))
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
            ) {
                content()
            }
        }

        if (bottomBar != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding(),
            ) {
                bottomBar()
            }
        }
    }
}

@Composable
fun AuroraGlassCard(
    modifier: Modifier = Modifier,
    borderColor: Color = AuroraBorder,
    containerColor: Color = AuroraSurface.copy(alpha = 0.9f),
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (onClick != null) {
                    Modifier.bounceClick(onClick = onClick)
                } else {
                    Modifier
                }
            ),
        shape = RoundedCornerShape(18.dp),
        color = containerColor,
        border = BorderStroke(1.dp, borderColor),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            content = content,
        )
    }
}

@Composable
fun AuroraSectionTitle(
    title: String,
    subtitle: String = "",
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f)) {
            Text(
                text = title,
                color = CloudWhite,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (subtitle.isNotBlank()) {
                Text(
                    text = subtitle,
                    color = DreamLavender.copy(alpha = 0.78f),
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
        if (!actionLabel.isNullOrBlank() && onAction != null) {
            Box(
                modifier = Modifier
                    .defaultMinSize(minWidth = 48.dp, minHeight = 40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .clickable(onClick = onAction)
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = actionLabel,
                    color = CrystalBlue,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
}

@Composable
fun AuroraEmptyStateCard(
    title: String,
    message: String,
    emoji: String = "🌙",
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    AuroraGlassCard(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(AuroraElevated)
                    .border(1.dp, AuroraBorder, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Text(emoji, fontSize = 24.sp)
            }
            Spacer(Modifier.height(12.dp))
            Text(
                text = title,
                color = CloudWhite,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = message,
                color = DreamLavender.copy(alpha = 0.85f),
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
                lineHeight = 18.sp,
            )
            if (!actionLabel.isNullOrBlank() && onAction != null) {
                Spacer(Modifier.height(14.dp))
                Button(
                    onClick = onAction,
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MysticPurple,
                        contentColor = CloudWhite,
                    ),
                    modifier = Modifier.defaultMinSize(minHeight = 44.dp),
                ) {
                    Text(actionLabel, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun AuroraLoadingCard(
    label: String = "Loading celestial data…",
    modifier: Modifier = Modifier,
) {
    AuroraGlassCard(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            CircularProgressIndicator(
                color = CrystalBlue,
                strokeWidth = 2.5.dp,
                modifier = Modifier.size(20.dp),
            )
            Spacer(Modifier.width(12.dp))
            Text(
                text = label,
                color = DreamLavender,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
            )
        }
    }
}

@Composable
fun AuroraErrorCard(
    title: String = "Unable to load content",
    message: String,
    onRetry: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    AuroraGlassCard(
        modifier = modifier,
        borderColor = AuroraError.copy(alpha = 0.45f),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(title, color = CloudWhite, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(4.dp))
            Text(
                message,
                color = DreamLavender.copy(alpha = 0.85f),
                fontSize = 12.5.sp,
                textAlign = TextAlign.Center,
            )
            if (onRetry != null) {
                Spacer(Modifier.height(12.dp))
                OutlinedButton(
                    onClick = onRetry,
                    border = BorderStroke(1.dp, CrystalBlue.copy(alpha = 0.6f)),
                    shape = RoundedCornerShape(12.dp),
                ) {
                    Text("Retry", color = CrystalBlue, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun AuroraConfirmDialog(
    title: String,
    body: String,
    confirmText: String = "Confirm",
    dismissText: String = "Cancel",
    destructive: Boolean = false,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DeepInk,
        titleContentColor = CloudWhite,
        textContentColor = DreamLavender,
        title = {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 17.sp)
        },
        text = {
            Text(body, fontSize = 13.5.sp, lineHeight = 19.sp)
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (destructive) AuroraError else RoyalViolet,
                    contentColor = CloudWhite,
                ),
                shape = RoundedCornerShape(12.dp),
            ) {
                Text(confirmText, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(dismissText, color = DreamLavender)
            }
        },
    )
}

@Composable
fun AuroraPreviewNoticePill(
    label: String = "Local preview · Demo simulation",
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(AuroraElevated.copy(alpha = 0.8f))
            .border(1.dp, RoyalGold.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
            .padding(horizontal = 10.dp, vertical = 5.dp),
    ) {
        Text(
            text = label,
            color = RoyalGold,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
        )
    }
}
