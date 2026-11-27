package com.sundram.weplay.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.sundram.weplay.data.DefaultAvatar
import com.sundram.weplay.ui.theme.AuroraSurface

@Composable
fun AnimatedAvatar(
    avatar: DefaultAvatar,
    modifier: Modifier = Modifier,
    size: Dp = 54.dp,
    animate: Boolean = true,
    onClick: (() -> Unit)? = null,
) {
    val infiniteTransition = rememberInfiniteTransition(label = "avatar_motion")

    // Gentle breathing scale (0.97f to 1.03f)
    val scale by if (animate) {
        infiniteTransition.animateFloat(
            initialValue = 0.97f,
            targetValue = 1.03f,
            animationSpec = infiniteRepeatable(
                animation = tween(2200, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse,
            ),
            label = "avatar_scale",
        )
    } else {
        androidx.compose.runtime.remember { androidx.compose.runtime.mutableFloatStateOf(1.0f) }
    }

    // Subtle ambient glow alpha (0.4f to 0.85f)
    val glowAlpha by if (animate) {
        infiniteTransition.animateFloat(
            initialValue = 0.35f,
            targetValue = 0.85f,
            animationSpec = infiniteRepeatable(
                animation = tween(1800, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse,
            ),
            label = "avatar_glow",
        )
    } else {
        androidx.compose.runtime.remember { androidx.compose.runtime.mutableFloatStateOf(0.5f) }
    }

    val clickModifier = if (onClick != null) {
        Modifier.clickable(onClick = onClick)
    } else {
        Modifier
    }

    Box(
        modifier = modifier
            .size(size)
            .scale(scale)
            .then(clickModifier),
        contentAlignment = Alignment.Center,
    ) {
        // Outer Radiant Shimmer Glow Ring
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            avatar.frameGlowColor.copy(alpha = glowAlpha),
                            avatar.accentColor.copy(alpha = glowAlpha * 0.5f),
                            Color.Transparent,
                        )
                    )
                )
        )

        // Inner Avatar Card with Gold/Cyan Border
        Box(
            modifier = Modifier
                .size(size - 4.dp)
                .shadow(2.dp, CircleShape)
                .clip(CircleShape)
                .background(AuroraSurface)
                .border(2.dp, avatar.accentColor.copy(alpha = 0.85f), CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(avatar.drawableRes),
                contentDescription = avatar.name,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(2.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop,
            )
        }
    }
}
