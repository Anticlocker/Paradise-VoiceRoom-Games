package com.sundram.weplay.ui.theme

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sundram.weplay.R
import com.sundram.weplay.ui.components.InitialsAvatar

/**
 * Aurora Paradise 3.0 — Hybrid Royal Moonlight + Crystal Twilight Color Tokens
 * From COLOR_TOKENS.json & Master Prompt specification.
 */
object AuroraChatsTokens {
    val BgBase = Color(0xFF13092F)
    val Header = Color(0xFF1A0C46)
    val Surface = Color(0xFF241448)
    val SurfaceElevated = Color(0xFF31205F)
    val TextPrimary = Color(0xFFFFF8FF)
    val TextSecondary = Color(0xFFCFC2EF)
    val Outline = Color(0xFF8954CB).copy(alpha = 0.35f)
    val OutlineBorder = Color(0xFF8954CB)
    val GlowPurple = Color(0xFFCA74FF)
    val CrystalCyan = Color(0xFF54CFFF)
    val ActiveCyan = Color(0xFF29CBE8)
    val RoyalGold = Color(0xFFFFD16F)
    val RoseAlert = Color(0xFFFF639F)
    val OnlineGreen = Color(0xFF3DDCA8)
    val IncomingBubble = Color(0xFF302457)
    val OutgoingBubble = Color(0xFF6238C6)
    val SecondaryOutgoing = Color(0xFF285B9F)
    val CardBorder = Color(0xFF8954CB).copy(alpha = 0.28f)
    val CardBorderGlow = Color(0xFFCA74FF).copy(alpha = 0.32f)
    val Divider = Color(0xFF6A338E).copy(alpha = 0.35f)

    val OutgoingBubbleGradient = Brush.horizontalGradient(
        listOf(OutgoingBubble, SecondaryOutgoing)
    )

    val ActiveTabGradient = Brush.horizontalGradient(
        listOf(ActiveCyan, CrystalCyan)
    )

    val RoyalGoldGradient = Brush.horizontalGradient(
        listOf(RoyalGold, Color(0xFFFFAE34))
    )

    val RoseGradient = Brush.horizontalGradient(
        listOf(RoseAlert, Color(0xFFD63384))
    )
}

/**
 * Full screen backdrop container with tab-specific background artwork and subtle celestial overlay.
 */
@Composable
fun AuroraChatsBackdrop(
    @DrawableRes backgroundRes: Int,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(AuroraChatsTokens.BgBase)
    ) {
        Image(
            painter = painterResource(backgroundRes),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
        )
        // Subtle dark translucent scrim for optimal contrast
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0x5510072B))
        )
        content()
    }
}

/**
 * Avatar with optional ornate fantasy frame overlay, glowing border ring, and status dot.
 */
@Composable
fun AuroraFramedAvatar(
    name: String,
    size: Dp = 48.dp,
    @DrawableRes frameRes: Int? = null,
    online: Boolean = false,
    statusText: String? = null,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
) {
    Box(
        modifier = modifier
            .size(size)
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier),
        contentAlignment = Alignment.Center,
    ) {
        // Base Avatar
        InitialsAvatar(
            name = name,
            size = if (frameRes != null) size * 0.76f else size,
            modifier = Modifier.clip(CircleShape),
        )

        // Ornate Frame if provided
        if (frameRes != null) {
            Image(
                painter = painterResource(frameRes),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxSize(),
            )
        } else {
            // Elegant subtle ring when no decorative frame is chosen
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .border(
                        1.5.dp,
                        Brush.sweepGradient(
                            listOf(
                                AuroraChatsTokens.GlowPurple,
                                AuroraChatsTokens.CrystalCyan,
                                AuroraChatsTokens.RoyalGold,
                                AuroraChatsTokens.GlowPurple,
                            )
                        ),
                        CircleShape,
                    )
            )
        }

        // Online or status dot at bottom end
        if (online) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(if (size >= 52.dp) 14.dp else 11.dp)
                    .background(AuroraChatsTokens.OnlineGreen, CircleShape)
                    .border(2.dp, AuroraChatsTokens.BgBase, CircleShape),
            )
        }
    }
}

/**
 * Styled Empty state with artwork, title and subtitle.
 */
@Composable
fun AuroraChatsEmptyState(
    @DrawableRes illustrationRes: Int,
    title: String,
    subtitle: String? = null,
    ctaText: String? = null,
    onCtaClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp, horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painter = painterResource(illustrationRes),
            contentDescription = null,
            modifier = Modifier.size(140.dp),
            contentScale = ContentScale.Fit,
        )
        Spacer(Modifier.height(14.dp))
        Text(
            text = title,
            color = AuroraChatsTokens.TextPrimary,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
        )
        if (!subtitle.isNullOrBlank()) {
            Spacer(Modifier.height(6.dp))
            Text(
                text = subtitle,
                color = AuroraChatsTokens.TextSecondary,
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                lineHeight = 16.sp,
            )
        }
        if (!ctaText.isNullOrBlank() && onCtaClick != null) {
            Spacer(Modifier.height(16.dp))
            Box(
                modifier = Modifier
                    .background(AuroraChatsTokens.ActiveTabGradient, RoundedCornerShape(16.dp))
                    .clickable(onClick = onCtaClick)
                    .padding(horizontal = 20.dp, vertical = 10.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = ctaText,
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}
