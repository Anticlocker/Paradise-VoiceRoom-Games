package com.sundram.weplay.ui.theme

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sundram.weplay.R

// ─────────────────────────────────────────────────────────────────────────────
// AURORA PARADISE 2.0 DESIGN SYSTEM TOKENS
// Visual Reference Direction: Dreamy Sky + Sunset Fantasy + Royal Midnight
// ─────────────────────────────────────────────────────────────────────────────

val CelestialViolet  = Color(0xFF7042D9) // Core primary celestial violet
val DeepMidnight     = Color(0xFF17143D) // Immersive midnight ink & lounge canvas
val AuroraTurquoise  = Color(0xFF23D5D1) // Vibrant neon aurora glow
val DreamLavender    = Color(0xFFBBA4FA) // Atmospheric dreamy lavender
val SoftPeach        = Color(0xFFFFB9A4) // Warm morning sunset peach
val ChampagneGold    = Color(0xFFF4C76A) // Metallic VIP and celebration gold
val PearlWhite       = Color(0xFFFFF9FF) // Clean pearl-white daytime canvas

// Compatibility Aliases mapped to Aurora Paradise 2.0 palette
val AuroraBackground    = PearlWhite
val AuroraSurface       = Color(0xFFFFFFFF)
val AuroraElevated      = Color(0xFFFFFFFF)
val AuroraTextPrimary   = DeepMidnight
val AuroraTextSecondary = Color(0xFF6B6583)
val AuroraLavender      = DreamLavender
val AuroraOrchid        = CelestialViolet
val AuroraCoral         = SoftPeach
val AuroraGold          = ChampagneGold
val AuroraMidnight      = DeepMidnight
val AuroraCyan          = AuroraTurquoise
val AuroraRose          = Color(0xFFF477AC)
val AuroraDivider       = Color(0xFFF0ECF8)
val AuroraBorder        = Color(0xFFECE7F6)
val AuroraGlowCyan      = AuroraTurquoise
val AuroraDarkRoomBg    = DeepMidnight
val AuroraSuccess       = Color(0xFF3ECF6E)
val AuroraWarning       = Color(0xFFFF9800)
val AuroraError         = Color(0xFFFF4D4F)

object AuroraGradients {
    // Primary Sunset Fantasy Action (Soft Peach -> Celestial Violet)
    val sunsetFantasy = Brush.horizontalGradient(
        listOf(SoftPeach, CelestialViolet)
    )

    // Dreamy Sky Glow (Aurora Turquoise -> Dream Lavender)
    val dreamySky = Brush.horizontalGradient(
        listOf(AuroraTurquoise, DreamLavender)
    )

    // Aurora Celestial Ribbon (Celestial Violet -> Turquoise -> Lavender)
    val celestialAurora = Brush.linearGradient(
        listOf(CelestialViolet, AuroraTurquoise, DreamLavender)
    )

    // Royal Midnight Interior (Deep Midnight -> Indigo Violet)
    val royalMidnight = Brush.verticalGradient(
        listOf(DeepMidnight, Color(0xFF241A4E))
    )

    // Champagne Gold VIP (Champagne Gold -> Soft Peach)
    val champagneGold = Brush.horizontalGradient(
        listOf(ChampagneGold, SoftPeach)
    )

    // Backward-compatible paradiseGold alias
    val paradiseGold = champagneGold

    // Soft Pearl/Peach Header Backdrop
    val heroBanner = Brush.horizontalGradient(
        listOf(Color(0xFFFFF0EC), Color(0xFFF4F0FF))
    )

    // Card Surface subtle sheen
    val cardSurface = Brush.verticalGradient(
        listOf(Color(0xFFFFFFFF), Color(0xFFFFFDFE))
    )

    // Voice Room Spotlight Card
    val spotlightCard = Brush.horizontalGradient(
        listOf(DeepMidnight, Color(0xFF2E1C5E))
    )
}

@Immutable
data class AuroraColors(
    val background: Color = AuroraBackground,
    val surface: Color = AuroraSurface,
    val elevatedSurface: Color = AuroraElevated,
    val textPrimary: Color = AuroraTextPrimary,
    val textSecondary: Color = AuroraTextSecondary,
    val lavender: Color = AuroraLavender,
    val orchid: Color = AuroraOrchid,
    val coral: Color = AuroraCoral,
    val gold: Color = AuroraGold,
    val midnight: Color = AuroraMidnight,
    val cyan: Color = AuroraCyan,
    val rose: Color = AuroraRose,
    val divider: Color = AuroraDivider,
    val border: Color = AuroraBorder,
    val glowCyan: Color = AuroraGlowCyan,
    val darkRoomBackground: Color = AuroraDarkRoomBg,
)

val LocalAuroraColors = staticCompositionLocalOf { AuroraColors() }

@Composable
fun AuroraParadiseTheme(
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(
        LocalAuroraColors provides AuroraColors(),
        content = content,
    )
}

// ─────────────────────────────────────────────────────────────────────────────
// AURORA PARADISE 2.0 BACKGROUND ENGINE
// Layered gradients, radial glows, star particles, high contrast
// ─────────────────────────────────────────────────────────────────────────────

/**
 * 1. AuroraGamesBackground:
 * Pearl-white base with soft celestial violet & peach atmospheric aura,
 * subtle star twinkling / floating particle dots, light ambient sheen.
 */
@Composable
fun AuroraGamesBackground(
    modifier: Modifier = Modifier,
    animated: Boolean = true,
    content: @Composable BoxScope.() -> Unit,
) {
    val transition = rememberInfiniteTransition(label = "gamesBgAnim")
    val starAlpha by if (animated) {
        transition.animateFloat(
            initialValue = 0.35f,
            targetValue = 0.95f,
            animationSpec = infiniteRepeatable(
                animation = tween(2400, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse,
            ),
            label = "starTwinkle",
        )
    } else {
        remember { androidx.compose.runtime.mutableFloatStateOf(0.7f) }
    }

    val glowShift by if (animated) {
        transition.animateFloat(
            initialValue = -15f,
            targetValue = 15f,
            animationSpec = infiniteRepeatable(
                animation = tween(4000, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse,
            ),
            label = "glowShift",
        )
    } else {
        remember { androidx.compose.runtime.mutableFloatStateOf(0f) }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(PearlWhite),
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            // 1. Soft top-left Peach Glow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        SoftPeach.copy(alpha = 0.22f),
                        SoftPeach.copy(alpha = 0.05f),
                        Color.Transparent,
                    ),
                    center = Offset(width * 0.15f + glowShift, height * 0.08f),
                    radius = width * 0.65f,
                ),
            )

            // 2. Soft top-right Dream Lavender Glow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        DreamLavender.copy(alpha = 0.25f),
                        DreamLavender.copy(alpha = 0.06f),
                        Color.Transparent,
                    ),
                    center = Offset(width * 0.85f - glowShift, height * 0.12f),
                    radius = width * 0.70f,
                ),
            )

            // 3. Subtle Turquoise Sheen on mid-right
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        AuroraTurquoise.copy(alpha = 0.10f),
                        Color.Transparent,
                    ),
                    center = Offset(width * 0.92f, height * 0.42f),
                    radius = width * 0.45f,
                ),
            )

            // 4. Subtle Twinkling Star Sparks
            val starPositions = listOf(
                Offset(width * 0.12f, height * 0.05f) to 2.5f,
                Offset(width * 0.28f, height * 0.11f) to 3.5f,
                Offset(width * 0.74f, height * 0.07f) to 3.0f,
                Offset(width * 0.88f, height * 0.16f) to 2.0f,
                Offset(width * 0.06f, height * 0.22f) to 2.2f,
                Offset(width * 0.92f, height * 0.28f) to 3.2f,
                Offset(width * 0.82f, height * 0.45f) to 2.4f,
            )

            for ((pos, r) in starPositions) {
                // Cross spark
                drawLine(
                    color = CelestialViolet.copy(alpha = starAlpha * 0.6f),
                    start = Offset(pos.x - r * 2.2f, pos.y),
                    end = Offset(pos.x + r * 2.2f, pos.y),
                    strokeWidth = 1.2f,
                    cap = StrokeCap.Round,
                )
                drawLine(
                    color = CelestialViolet.copy(alpha = starAlpha * 0.6f),
                    start = Offset(pos.x, pos.y - r * 2.2f),
                    end = Offset(pos.x, pos.y + r * 2.2f),
                    strokeWidth = 1.2f,
                    cap = StrokeCap.Round,
                )
                drawCircle(
                    color = ChampagneGold.copy(alpha = starAlpha * 0.85f),
                    radius = r * 0.8f,
                    center = pos,
                )
            }
        }

        content()
    }
}

/**
 * 2. AuroraDiscoverBackground:
 * Pearl-white base with atmospheric turquoise and dream lavender radial sweeps near header,
 * delicate starry specks, high contrast.
 */
@Composable
fun AuroraDiscoverBackground(
    modifier: Modifier = Modifier,
    animated: Boolean = true,
    content: @Composable BoxScope.() -> Unit,
) {
    val transition = rememberInfiniteTransition(label = "discoverBgAnim")
    val pulse by if (animated) {
        transition.animateFloat(
            initialValue = 0.4f,
            targetValue = 0.9f,
            animationSpec = infiniteRepeatable(
                animation = tween(2800, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse,
            ),
            label = "discoverPulse",
        )
    } else {
        remember { androidx.compose.runtime.mutableFloatStateOf(0.7f) }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(PearlWhite),
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Top Turquoise Sweep
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        AuroraTurquoise.copy(alpha = 0.20f),
                        AuroraTurquoise.copy(alpha = 0.04f),
                        Color.Transparent,
                    ),
                    center = Offset(w * 0.20f, h * 0.08f),
                    radius = w * 0.70f,
                ),
            )

            // Top Lavender Sweep
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        DreamLavender.copy(alpha = 0.22f),
                        DreamLavender.copy(alpha = 0.05f),
                        Color.Transparent,
                    ),
                    center = Offset(w * 0.80f, h * 0.12f),
                    radius = w * 0.65f,
                ),
            )

            // Star accent specks
            val specks = listOf(
                Offset(w * 0.15f, h * 0.04f),
                Offset(w * 0.85f, h * 0.06f),
                Offset(w * 0.72f, h * 0.14f),
                Offset(w * 0.25f, h * 0.18f),
            )
            for (p in specks) {
                drawCircle(
                    color = AuroraTurquoise.copy(alpha = pulse * 0.75f),
                    radius = 2.4f,
                    center = p,
                )
            }
        }

        content()
    }
}

/**
 * 3. AuroraProfileBackground:
 * Soft peach & dream lavender luminous gradient header flowing softly down into pearl-white canvas.
 */
@Composable
fun AuroraProfileBackground(
    modifier: Modifier = Modifier,
    animated: Boolean = true,
    content: @Composable BoxScope.() -> Unit,
) {
    val transition = rememberInfiniteTransition(label = "profileBgAnim")
    val shimmer by if (animated) {
        transition.animateFloat(
            initialValue = 0.5f,
            targetValue = 0.95f,
            animationSpec = infiniteRepeatable(
                animation = tween(3200, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse,
            ),
            label = "profileShimmer",
        )
    } else {
        remember { androidx.compose.runtime.mutableFloatStateOf(0.75f) }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(PearlWhite),
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Header Sunset Aurora Wash (~240dp depth)
            val headerHeight = 240.dp.toPx()
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFFFF0EA), // Delicate morning peach
                        Color(0xFFF7F2FF), // Soft lavender tint
                        PearlWhite,
                    ),
                    startY = 0f,
                    endY = headerHeight,
                ),
                size = androidx.compose.ui.geometry.Size(w, headerHeight),
            )

            // Atmospheric peach radial glow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        SoftPeach.copy(alpha = 0.28f * shimmer),
                        Color.Transparent,
                    ),
                    center = Offset(w * 0.25f, 70.dp.toPx()),
                    radius = 180.dp.toPx(),
                ),
            )

            // Atmospheric lavender radial glow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        DreamLavender.copy(alpha = 0.32f * shimmer),
                        Color.Transparent,
                    ),
                    center = Offset(w * 0.78f, 90.dp.toPx()),
                    radius = 200.dp.toPx(),
                ),
            )
        }

        content()
    }
}

/**
 * 4. CelestialVoiceRoomBackground:
 * Deep Midnight (#17143D) base with layered celestial violet (#7042D9) aurora sweeps,
 * glowing turquoise (#23D5D1) stars, cosmic dust and radial nebulae.
 */
@Composable
fun CelestialVoiceRoomBackground(
    modifier: Modifier = Modifier,
    animated: Boolean = true,
    content: @Composable BoxScope.() -> Unit,
) {
    val transition = rememberInfiniteTransition(label = "celestialRoomAnim")
    val auroraWave by if (animated) {
        transition.animateFloat(
            initialValue = 0.30f,
            targetValue = 0.75f,
            animationSpec = infiniteRepeatable(
                animation = tween(3500, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse,
            ),
            label = "auroraWave",
        )
    } else {
        remember { androidx.compose.runtime.mutableFloatStateOf(0.55f) }
    }

    val starBreath by if (animated) {
        transition.animateFloat(
            initialValue = 0.4f,
            targetValue = 1.0f,
            animationSpec = infiniteRepeatable(
                animation = tween(2200, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse,
            ),
            label = "starBreath",
        )
    } else {
        remember { androidx.compose.runtime.mutableFloatStateOf(0.7f) }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DeepMidnight),
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // 1. Deep Celestial Violet Aurora Wave
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        CelestialViolet.copy(alpha = 0.48f * auroraWave),
                        Color(0xFF381D76).copy(alpha = 0.25f),
                        Color.Transparent,
                    ),
                    center = Offset(w * 0.5f, h * 0.22f),
                    radius = w * 0.85f,
                ),
            )

            // 2. Neon Turquoise Aurora Ribbon Sweep
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        AuroraTurquoise.copy(alpha = 0.32f * auroraWave),
                        AuroraTurquoise.copy(alpha = 0.08f),
                        Color.Transparent,
                    ),
                    center = Offset(w * 0.85f, h * 0.15f),
                    radius = w * 0.60f,
                ),
            )

            // 3. Lower Stage Subtle Violet Glow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF4C2A9E).copy(alpha = 0.30f),
                        Color.Transparent,
                    ),
                    center = Offset(w * 0.20f, h * 0.55f),
                    radius = w * 0.65f,
                ),
            )

            // 4. Shimmering Constellation Stars
            val celestialStars = listOf(
                Offset(w * 0.08f, h * 0.06f) to 2.2f,
                Offset(w * 0.22f, h * 0.12f) to 3.0f,
                Offset(w * 0.45f, h * 0.04f) to 2.0f,
                Offset(w * 0.68f, h * 0.10f) to 3.2f,
                Offset(w * 0.90f, h * 0.08f) to 2.4f,
                Offset(w * 0.14f, h * 0.25f) to 2.6f,
                Offset(w * 0.82f, h * 0.28f) to 2.0f,
                Offset(w * 0.35f, h * 0.34f) to 1.8f,
                Offset(w * 0.70f, h * 0.40f) to 2.8f,
                Offset(w * 0.92f, h * 0.48f) to 2.2f,
                Offset(w * 0.06f, h * 0.60f) to 2.0f,
                Offset(w * 0.88f, h * 0.66f) to 2.5f,
            )

            for ((pos, rad) in celestialStars) {
                // Cross Sparkle on major stars
                if (rad >= 2.5f) {
                    drawLine(
                        color = AuroraTurquoise.copy(alpha = starBreath * 0.7f),
                        start = Offset(pos.x - rad * 2.5f, pos.y),
                        end = Offset(pos.x + rad * 2.5f, pos.y),
                        strokeWidth = 1.0f,
                        cap = StrokeCap.Round,
                    )
                    drawLine(
                        color = AuroraTurquoise.copy(alpha = starBreath * 0.7f),
                        start = Offset(pos.x, pos.y - rad * 2.5f),
                        end = Offset(pos.x, pos.y + rad * 2.5f),
                        strokeWidth = 1.0f,
                        cap = StrokeCap.Round,
                    )
                }
                drawCircle(
                    color = Color.White.copy(alpha = starBreath * 0.9f),
                    radius = rad,
                    center = pos,
                )
            }
        }

        content()
    }
}

/**
 * 5. FestivalEventBackground:
 * Atmospheric mint/turquoise, peach, and lavender festival aura with celebratory particle sparkles.
 */
@Composable
fun FestivalEventBackground(
    modifier: Modifier = Modifier,
    animated: Boolean = true,
    content: @Composable BoxScope.() -> Unit,
) {
    val transition = rememberInfiniteTransition(label = "festivalAnim")
    val sparkAlpha by if (animated) {
        transition.animateFloat(
            initialValue = 0.35f,
            targetValue = 0.95f,
            animationSpec = infiniteRepeatable(
                animation = tween(2000, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse,
            ),
            label = "festivalSpark",
        )
    } else {
        remember { androidx.compose.runtime.mutableFloatStateOf(0.75f) }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(PearlWhite),
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Festival celebratory radiant washes
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        ChampagneGold.copy(alpha = 0.24f),
                        ChampagneGold.copy(alpha = 0.05f),
                        Color.Transparent,
                    ),
                    center = Offset(w * 0.20f, h * 0.08f),
                    radius = w * 0.65f,
                ),
            )

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        SoftPeach.copy(alpha = 0.22f),
                        Color.Transparent,
                    ),
                    center = Offset(w * 0.85f, h * 0.12f),
                    radius = w * 0.70f,
                ),
            )

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        AuroraTurquoise.copy(alpha = 0.15f),
                        Color.Transparent,
                    ),
                    center = Offset(w * 0.50f, h * 0.28f),
                    radius = w * 0.55f,
                ),
            )

            // Confetti / Gold Star Particles
            val sparkles = listOf(
                Offset(w * 0.14f, h * 0.05f) to ChampagneGold,
                Offset(w * 0.32f, h * 0.10f) to CelestialViolet,
                Offset(w * 0.76f, h * 0.06f) to AuroraTurquoise,
                Offset(w * 0.88f, h * 0.15f) to ChampagneGold,
                Offset(w * 0.08f, h * 0.20f) to SoftPeach,
                Offset(w * 0.92f, h * 0.26f) to AuroraTurquoise,
            )

            for ((pos, color) in sparkles) {
                drawCircle(
                    color = color.copy(alpha = sparkAlpha * 0.85f),
                    radius = 3.2f,
                    center = pos,
                )
            }
        }

        content()
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// REUSABLE PREMIUM AURORA PARADISE COMPONENTS
// ─────────────────────────────────────────────────────────────────────────────

/**
 * AuroraButton: Gradient action button with spring-like press animation
 * and minimum 48dp touch target.
 */
@Composable
fun AuroraButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    brush: Brush = AuroraGradients.sunsetFantasy,
    textColor: Color = Color.White,
    icon: Painter? = null,
    enabled: Boolean = true,
    height: Dp = 48.dp,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1.0f,
        animationSpec = tween(durationMillis = 140),
        label = "AuroraButtonScale",
    )

    Box(
        modifier = modifier
            .scale(scale)
            .height(height)
            .clip(RoundedCornerShape(height / 2f))
            .background(if (enabled) brush else Brush.linearGradient(listOf(Color(0xFFCCC8D8), Color(0xFFDDD9E8))))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                onClick = onClick,
            )
            .padding(horizontal = 20.dp),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            if (icon != null) {
                Icon(
                    painter = icon,
                    contentDescription = null,
                    tint = textColor,
                    modifier = Modifier.size(18.dp),
                )
                Spacer(Modifier.width(8.dp))
            }
            Text(
                text = text,
                color = textColor,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

/**
 * AuroraIconTile: 48dp+ rounded square tile for shortcuts with ivory base
 * and gradient outline.
 */
@Composable
fun AuroraIconTile(
    painter: Painter,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 56.dp,
    badgeText: String? = null,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.94f else 1.0f,
        animationSpec = tween(120),
        label = "AuroraIconTileScale",
    )

    Column(
        modifier = modifier
            .scale(scale)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick,
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(contentAlignment = Alignment.Center) {
            Box(
                modifier = Modifier
                    .size(size)
                    .clip(RoundedCornerShape(18.dp))
                    .background(AuroraBackground)
                    .border(1.dp, AuroraLavender.copy(alpha = 0.6f), RoundedCornerShape(18.dp))
                    .shadow(elevation = 1.dp, shape = RoundedCornerShape(18.dp)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = painter,
                    contentDescription = label,
                    tint = Color.Unspecified,
                    modifier = Modifier.size(size * 0.72f),
                )
            }

            if (!badgeText.isNullOrBlank()) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 2.dp, end = 2.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(AuroraCoral)
                        .padding(horizontal = 5.dp, vertical = 1.dp),
                ) {
                    Text(
                        text = badgeText,
                        color = Color.White,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }

        Spacer(Modifier.height(6.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = AuroraTextPrimary,
            maxLines = 1,
        )
    }
}

/**
 * AuroraCard: Reusable ivory/white card container with subtle borders
 * and soft Dreamy Sky / Sunset Fantasy elevation.
 */
@Composable
fun AuroraCard(
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape = RoundedCornerShape(20.dp),
    backgroundColor: Color = AuroraSurface,
    borderColor: Color = AuroraBorder,
    elevation: Dp = 2.dp,
    content: @Composable () -> Unit,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, borderColor, shape),
        shape = shape,
        color = backgroundColor,
        shadowElevation = elevation,
    ) {
        content()
    }
}

/**
 * AuroraSectionHeader: Standard header across all screens.
 */
@Composable
fun AuroraSectionHeader(
    title: String,
    subtitle: String? = null,
    actionLabel: String? = null,
    onActionClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column {
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = AuroraTextPrimary,
            )
            if (!subtitle.isNullOrBlank()) {
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = AuroraTextSecondary,
                )
            }
        }

        if (!actionLabel.isNullOrBlank() && onActionClick != null) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .background(AuroraBackground)
                    .border(1.dp, AuroraBorder, RoundedCornerShape(14.dp))
                    .clickable(onClick = onActionClick)
                    .padding(horizontal = 10.dp, vertical = 5.dp),
            ) {
                Text(
                    text = actionLabel,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = AuroraOrchid,
                )
            }
        }
    }
}

/**
 * AuroraChip: Filter pill chip with animated active/inactive styling.
 */
@Composable
fun AuroraChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: Painter? = null,
) {
    val bg = if (selected) AuroraGradients.sunsetFantasy else Brush.linearGradient(listOf(AuroraSurface, AuroraSurface))
    val textCol = if (selected) Color.White else AuroraTextPrimary
    val borderCol = if (selected) Color.Transparent else AuroraBorder

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(bg)
            .border(1.dp, borderCol, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (icon != null) {
            Icon(
                painter = icon,
                contentDescription = null,
                tint = textCol,
                modifier = Modifier.size(14.dp),
            )
            Spacer(Modifier.width(6.dp))
        }
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            color = textCol,
        )
    }
}
