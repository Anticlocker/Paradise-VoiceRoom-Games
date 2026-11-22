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
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.offset
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
import androidx.compose.runtime.mutableFloatStateOf
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sundram.weplay.R
import java.text.NumberFormat

// ─────────────────────────────────────────────────────────────────────────────
// AURORA PARADISE 3.0 DESIGN SYSTEM TOKENS
// Visual Target: Royal-purple celestial fantasy, glowing lavender, crystal blue,
// dimensional golden ornaments, and floating violet dock.
// ─────────────────────────────────────────────────────────────────────────────

val CelestialNavy  = Color(0xFF11134A) // Dark royal navy night sky & deep surface
val RoyalViolet    = Color(0xFF5423B7) // Primary celestial royal violet
val MysticPurple   = Color(0xFF7435D9) // Glowing mystic purple
val DreamLavender  = Color(0xFFB89BFF) // Soft dream lavender atmospheric sheen
val CrystalBlue    = Color(0xFF54CFFF) // Neon crystal cyan/blue
val SunsetRose     = Color(0xFFFF78C4) // Sunset pink/rose accent
val RoyalGold      = Color(0xFFFFD36B) // Metallic royal celebration gold
val SoftGold       = Color(0xFFFFF0B6) // Shimmer soft highlight gold
val CloudWhite     = Color(0xFFF8F1FF) // High-contrast pearl/cloud white text & icons
val DeepInk        = Color(0xFF211448) // Translucent dock, card surfaces & dark containers

// 2.0 / Legacy Aliases for Seamless Backward Compatibility
val CelestialViolet  = MysticPurple
val DeepMidnight     = CelestialNavy
val AuroraTurquoise  = CrystalBlue
val SoftPeach        = SunsetRose
val ChampagneGold    = RoyalGold
val PearlWhite       = CloudWhite

val AuroraBackground    = CelestialNavy
val AuroraSurface       = DeepInk
val AuroraElevated      = Color(0xFF2C195E)
val AuroraTextPrimary   = CloudWhite
val AuroraTextSecondary = DreamLavender.copy(alpha = 0.85f)
val AuroraLavender      = DreamLavender
val AuroraOrchid        = MysticPurple
val AuroraCoral         = SunsetRose
val AuroraGold          = RoyalGold
val AuroraMidnight      = CelestialNavy
val AuroraCyan          = CrystalBlue
val AuroraRose          = SunsetRose
val AuroraDivider       = DreamLavender.copy(alpha = 0.18f)
val AuroraBorder        = DreamLavender.copy(alpha = 0.28f)
val AuroraGlowCyan      = CrystalBlue
val AuroraDarkRoomBg    = CelestialNavy
val AuroraSuccess       = Color(0xFF3ECF6E)
val AuroraWarning       = Color(0xFFFF9800)
val AuroraError         = Color(0xFFFF4D4F)

// ─────────────────────────────────────────────────────────────────────────────
// AURORA PARADISE 3.0 GRADIENTS
// ─────────────────────────────────────────────────────────────────────────────

object AuroraGradients {
    // Primary Royal Twilight (Celestial Navy -> Royal Violet -> Mystic Purple)
    val royalTwilight = Brush.verticalGradient(
        listOf(CelestialNavy, RoyalViolet, MysticPurple)
    )

    // Sunset Fantasy Action (Sunset Rose -> Mystic Purple)
    val sunsetFantasy = Brush.horizontalGradient(
        listOf(SunsetRose, MysticPurple)
    )

    // Crystal Blue Aura (Crystal Blue -> Dream Lavender)
    val crystalBlue = Brush.horizontalGradient(
        listOf(CrystalBlue, DreamLavender)
    )

    // Backward-compatible dreamySky alias
    val dreamySky = crystalBlue

    // Aurora Celestial Ribbon (Royal Violet -> Crystal Blue -> Dream Lavender)
    val celestialAurora = Brush.linearGradient(
        listOf(RoyalViolet, CrystalBlue, DreamLavender)
    )

    // Royal Gold Metallic VIP (Royal Gold -> Soft Gold)
    val royalGold = Brush.horizontalGradient(
        listOf(RoyalGold, SoftGold)
    )

    // Backward-compatible aliases
    val champagneGold = royalGold
    val paradiseGold = royalGold

    // Translucent Celestial Card Sheen
    val celestialCard = Brush.verticalGradient(
        listOf(DeepInk.copy(alpha = 0.85f), CelestialNavy.copy(alpha = 0.92f))
    )

    // Card Surface subtle sheen
    val cardSurface = Brush.verticalGradient(
        listOf(Color(0xFF2A1B5A), Color(0xFF1D1445))
    )

    // Floating Translucent Violet Dock
    val floatingDock = Brush.verticalGradient(
        listOf(DeepInk.copy(alpha = 0.94f), CelestialNavy.copy(alpha = 0.98f))
    )

    // Hero Banner Gradient
    val heroBanner = Brush.horizontalGradient(
        listOf(RoyalViolet, MysticPurple)
    )

    // Voice Room Spotlight Card
    val spotlightCard = Brush.horizontalGradient(
        listOf(CelestialNavy, Color(0xFF2E1C5E))
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
// AURORA PARADISE 3.0 BACKGROUND ENGINE
// Distinct fantasy environments with original WebP backdrops, atmospheric radial
// glows, soft twinkling stars and high-contrast scrims.
// ─────────────────────────────────────────────────────────────────────────────

/**
 * 1. AuroraGamesBackground:
 * Celestial blue-purple fantasy sky with illuminated WebP palace backdrop,
 * subtle twinkling stars, lavender ambient lighting, and high contrast scrim.
 */
@Composable
fun AuroraGamesBackground(
    modifier: Modifier = Modifier,
    animated: Boolean = true,
    content: @Composable BoxScope.() -> Unit,
) {
    val transition = rememberInfiniteTransition(label = "gamesBgAnim3")
    val starAlpha by if (animated) {
        transition.animateFloat(
            initialValue = 0.35f,
            targetValue = 0.95f,
            animationSpec = infiniteRepeatable(
                animation = tween(2400, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse,
            ),
            label = "starTwinkle3",
        )
    } else {
        remember { mutableFloatStateOf(0.7f) }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(CelestialNavy),
    ) {
        // High-resolution celestial WebP background
        Image(
            painter = painterResource(R.drawable.aurora3_bg_games),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
        )

        // Contrast Scrim + Soft Radial Vignette
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            CelestialNavy.copy(alpha = 0.55f),
                            Color.Transparent,
                            CelestialNavy.copy(alpha = 0.75f),
                            CelestialNavy.copy(alpha = 0.92f),
                        )
                    )
                )
        )

        // Procedural Atmospheric Floating Stars & Radial Glow
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            // Soft Dream Lavender Glow at top-right
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        DreamLavender.copy(alpha = 0.22f),
                        Color.Transparent,
                    ),
                    center = Offset(width * 0.82f, height * 0.12f),
                    radius = width * 0.60f,
                ),
            )

            // Soft Sunset Rose Glow at top-left
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        SunsetRose.copy(alpha = 0.18f),
                        Color.Transparent,
                    ),
                    center = Offset(width * 0.15f, height * 0.08f),
                    radius = width * 0.55f,
                ),
            )

            // Twinkling Star Sparks
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
                drawLine(
                    color = CrystalBlue.copy(alpha = starAlpha * 0.7f),
                    start = Offset(pos.x - r * 2.2f, pos.y),
                    end = Offset(pos.x + r * 2.2f, pos.y),
                    strokeWidth = 1.2f,
                    cap = StrokeCap.Round,
                )
                drawLine(
                    color = CrystalBlue.copy(alpha = starAlpha * 0.7f),
                    start = Offset(pos.x, pos.y - r * 2.2f),
                    end = Offset(pos.x, pos.y + r * 2.2f),
                    strokeWidth = 1.2f,
                    cap = StrokeCap.Round,
                )
                drawCircle(
                    color = RoyalGold.copy(alpha = starAlpha * 0.85f),
                    radius = r * 0.8f,
                    center = pos,
                )
            }
        }

        content()
    }
}

/**
 * 2. AuroraVoiceRoomBackground / AuroraDiscoverBackground:
 * Royal violet atmosphere with sapphire aurora ribbons, soft cloud layers,
 * and illuminated room banners.
 */
@Composable
fun AuroraDiscoverBackground(
    modifier: Modifier = Modifier,
    animated: Boolean = true,
    content: @Composable BoxScope.() -> Unit,
) {
    val transition = rememberInfiniteTransition(label = "discoverBgAnim3")
    val pulse by if (animated) {
        transition.animateFloat(
            initialValue = 0.4f,
            targetValue = 0.9f,
            animationSpec = infiniteRepeatable(
                animation = tween(2800, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse,
            ),
            label = "discoverPulse3",
        )
    } else {
        remember { mutableFloatStateOf(0.7f) }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(CelestialNavy),
    ) {
        Image(
            painter = painterResource(R.drawable.aurora3_bg_discover),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            CelestialNavy.copy(alpha = 0.45f),
                            Color.Transparent,
                            CelestialNavy.copy(alpha = 0.80f),
                        )
                    )
                )
        )

        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Star accent specks
            val specks = listOf(
                Offset(w * 0.15f, h * 0.04f),
                Offset(w * 0.85f, h * 0.06f),
                Offset(w * 0.72f, h * 0.14f),
                Offset(w * 0.25f, h * 0.18f),
            )
            for (p in specks) {
                drawCircle(
                    color = CrystalBlue.copy(alpha = pulse * 0.75f),
                    radius = 2.4f,
                    center = p,
                )
            }
        }

        content()
    }
}

@Composable
fun AuroraVoiceRoomBackground(
    modifier: Modifier = Modifier,
    animated: Boolean = true,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(CelestialNavy),
    ) {
        Image(
            painter = painterResource(R.drawable.aurora3_bg_voice_room),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            CelestialNavy.copy(alpha = 0.50f),
                            Color.Transparent,
                            CelestialNavy.copy(alpha = 0.85f),
                        )
                    )
                )
        )
        content()
    }
}

/**
 * 3. AuroraProfileBackground:
 * Royal profile environment with royal-gold accents, equipped avatar frame glow,
 * and high-contrast settings list surfaces.
 */
@Composable
fun AuroraProfileBackground(
    modifier: Modifier = Modifier,
    animated: Boolean = true,
    content: @Composable BoxScope.() -> Unit,
) {
    val transition = rememberInfiniteTransition(label = "profileBgAnim3")
    val shimmer by if (animated) {
        transition.animateFloat(
            initialValue = 0.5f,
            targetValue = 0.95f,
            animationSpec = infiniteRepeatable(
                animation = tween(3200, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse,
            ),
            label = "profileShimmer3",
        )
    } else {
        remember { mutableFloatStateOf(0.75f) }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(CelestialNavy),
    ) {
        Image(
            painter = painterResource(R.drawable.aurora3_bg_profile),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            CelestialNavy.copy(alpha = 0.40f),
                            CelestialNavy.copy(alpha = 0.75f),
                            CelestialNavy.copy(alpha = 0.95f),
                        )
                    )
                )
        )

        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            // Atmospheric golden radial glow near header
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        RoyalGold.copy(alpha = 0.20f * shimmer),
                        Color.Transparent,
                    ),
                    center = Offset(w * 0.50f, 90.dp.toPx()),
                    radius = 200.dp.toPx(),
                ),
            )
        }

        content()
    }
}

/**
 * 4. CelestialVoiceRoomBackground:
 * Midnight celestial background with gold host-seat accents, crystal speaking indicators,
 * subtle animated stars, and PK battle stage.
 */
@Composable
fun CelestialVoiceRoomBackground(
    modifier: Modifier = Modifier,
    animated: Boolean = true,
    content: @Composable BoxScope.() -> Unit,
) {
    val transition = rememberInfiniteTransition(label = "celestialRoomAnim3")
    val auroraWave by if (animated) {
        transition.animateFloat(
            initialValue = 0.30f,
            targetValue = 0.75f,
            animationSpec = infiniteRepeatable(
                animation = tween(3500, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse,
            ),
            label = "auroraWave3",
        )
    } else {
        remember { mutableFloatStateOf(0.55f) }
    }

    val starBreath by if (animated) {
        transition.animateFloat(
            initialValue = 0.4f,
            targetValue = 1.0f,
            animationSpec = infiniteRepeatable(
                animation = tween(2200, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse,
            ),
            label = "starBreath3",
        )
    } else {
        remember { mutableFloatStateOf(0.7f) }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(CelestialNavy),
    ) {
        Image(
            painter = painterResource(R.drawable.aurora3_bg_voice_room),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            CelestialNavy.copy(alpha = 0.55f),
                            Color.Transparent,
                            CelestialNavy.copy(alpha = 0.88f),
                        )
                    )
                )
        )

        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Deep Celestial Violet Aurora Wave
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        MysticPurple.copy(alpha = 0.48f * auroraWave),
                        RoyalViolet.copy(alpha = 0.25f),
                        Color.Transparent,
                    ),
                    center = Offset(w * 0.5f, h * 0.22f),
                    radius = w * 0.85f,
                ),
            )

            // Neon Crystal Blue Aurora Ribbon Sweep
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        CrystalBlue.copy(alpha = 0.32f * auroraWave),
                        CrystalBlue.copy(alpha = 0.08f),
                        Color.Transparent,
                    ),
                    center = Offset(w * 0.85f, h * 0.15f),
                    radius = w * 0.60f,
                ),
            )

            // Shimmering Constellation Stars
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
            )

            for ((pos, rad) in celestialStars) {
                if (rad >= 2.5f) {
                    drawLine(
                        color = CrystalBlue.copy(alpha = starBreath * 0.7f),
                        start = Offset(pos.x - rad * 2.5f, pos.y),
                        end = Offset(pos.x + rad * 2.5f, pos.y),
                        strokeWidth = 1.0f,
                        cap = StrokeCap.Round,
                    )
                    drawLine(
                        color = CrystalBlue.copy(alpha = starBreath * 0.7f),
                        start = Offset(pos.x, pos.y - rad * 2.5f),
                        end = Offset(pos.x, pos.y + rad * 2.5f),
                        strokeWidth = 1.0f,
                        cap = StrokeCap.Round,
                    )
                }
                drawCircle(
                    color = CloudWhite.copy(alpha = starBreath * 0.9f),
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
 * Deep purple fantasy environment with celebration star particles, gold category ornaments,
 * and glowing reward cards.
 */
@Composable
fun FestivalEventBackground(
    modifier: Modifier = Modifier,
    animated: Boolean = true,
    content: @Composable BoxScope.() -> Unit,
) {
    val transition = rememberInfiniteTransition(label = "festivalAnim3")
    val sparkAlpha by if (animated) {
        transition.animateFloat(
            initialValue = 0.35f,
            targetValue = 0.95f,
            animationSpec = infiniteRepeatable(
                animation = tween(2000, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse,
            ),
            label = "festivalSpark3",
        )
    } else {
        remember { mutableFloatStateOf(0.75f) }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(CelestialNavy),
    ) {
        Image(
            painter = painterResource(R.drawable.aurora3_bg_event),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            CelestialNavy.copy(alpha = 0.50f),
                            Color.Transparent,
                            CelestialNavy.copy(alpha = 0.88f),
                        )
                    )
                )
        )

        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Festival celebratory gold & rose radiant washes
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        RoyalGold.copy(alpha = 0.24f),
                        Color.Transparent,
                    ),
                    center = Offset(w * 0.20f, h * 0.08f),
                    radius = w * 0.65f,
                ),
            )

            // Confetti / Gold Star Particles
            val sparkles = listOf(
                Offset(w * 0.14f, h * 0.05f) to RoyalGold,
                Offset(w * 0.32f, h * 0.10f) to MysticPurple,
                Offset(w * 0.76f, h * 0.06f) to CrystalBlue,
                Offset(w * 0.88f, h * 0.15f) to RoyalGold,
                Offset(w * 0.08f, h * 0.20f) to SunsetRose,
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

/**
 * 6. AuroraChatBackground:
 * Soft dreamy lavender environment with high contrast message bubbles.
 */
@Composable
fun AuroraChatBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(CelestialNavy),
    ) {
        Image(
            painter = painterResource(R.drawable.aurora3_bg_chat),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(CelestialNavy.copy(alpha = 0.75f))
        )
        content()
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// REUSABLE PREMIUM AURORA PARADISE 3.0 COMPONENTS
// ─────────────────────────────────────────────────────────────────────────────

/**
 * CelestialHeader:
 * Framed user avatar with level badge, gold currency pill (aurora3_ic_coin),
 * crystal diamond currency pill (aurora3_ic_diamond), and Event Center / Star shortcut.
 */
@Composable
fun CelestialHeader(
    avatarRes: Int,
    userName: String,
    userLevel: Int,
    coins: Long,
    crystals: Long = 850L,
    onAvatarClick: () -> Unit,
    onCoinsClick: () -> Unit,
    onCrystalsClick: () -> Unit,
    onEventsClick: () -> Unit,
    onNotificationsClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    val formattedCoins = remember(coins) {
        NumberFormat.getIntegerInstance().format(coins)
    }
    val formattedCrystals = remember(crystals) {
        NumberFormat.getIntegerInstance().format(crystals)
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // 1. Framed Avatar with Level Badge
        Box(
            modifier = Modifier
                .size(50.dp)
                .clickable(onClick = onAvatarClick),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(avatarRes),
                contentDescription = userName,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop,
            )
            // Golden celestial frame overlay
            Image(
                painter = painterResource(R.drawable.aurora3_frame_gold),
                contentDescription = null,
                modifier = Modifier.size(50.dp),
            )
            // Level badge at bottom
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .offset(y = 2.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(AuroraGradients.royalGold)
                    .border(0.8.dp, CloudWhite, RoundedCornerShape(8.dp))
                    .padding(horizontal = 4.dp, vertical = 0.5.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "Lv.$userLevel",
                    color = DeepInk,
                    fontSize = 8.5.sp,
                    fontWeight = FontWeight.Black,
                )
            }
        }

        Spacer(Modifier.width(10.dp))

        // 2. Welcome Greeting & Name
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Welcome to Aurora",
                color = DreamLavender,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
            )
            Text(
                text = userName,
                color = CloudWhite,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }

        Spacer(Modifier.width(6.dp))

        // 3. Gold Currency Pill
        CrystalCurrencyPill(
            iconRes = R.drawable.aurora3_ic_coin,
            amount = formattedCoins,
            gradient = Brush.horizontalGradient(listOf(Color(0xFF38230D), Color(0xFF2A1608))),
            borderColor = RoyalGold,
            onClick = onCoinsClick,
        )

        Spacer(Modifier.width(6.dp))

        // 4. Crystal Diamond Currency Pill
        CrystalCurrencyPill(
            iconRes = R.drawable.aurora3_ic_diamond,
            amount = formattedCrystals,
            gradient = Brush.horizontalGradient(listOf(Color(0xFF0F2646), Color(0xFF141F3C))),
            borderColor = CrystalBlue,
            onClick = onCrystalsClick,
        )

        Spacer(Modifier.width(8.dp))

        // 5. Event Center Star Shortcut
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        listOf(RoyalViolet, DeepInk)
                    )
                )
                .border(1.2.dp, RoyalGold, CircleShape)
                .clickable(onClick = onEventsClick),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(R.drawable.aurora3_ic_nav_star),
                contentDescription = "Event Center",
                modifier = Modifier.size(18.dp),
            )
        }
    }
}

/**
 * CrystalCurrencyPill:
 * Dimensional coin or crystal counter pill with metallic border and "+" top-up button.
 */
@Composable
fun CrystalCurrencyPill(
    iconRes: Int,
    amount: String,
    gradient: Brush,
    borderColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .height(30.dp)
            .clip(RoundedCornerShape(15.dp))
            .background(gradient)
            .border(1.dp, borderColor.copy(alpha = 0.7f), RoundedCornerShape(15.dp))
            .clickable(onClick = onClick)
            .padding(start = 5.dp, end = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painter = painterResource(iconRes),
            contentDescription = null,
            modifier = Modifier.size(18.dp),
            contentScale = ContentScale.Fit,
        )
        Spacer(Modifier.width(4.dp))
        Text(
            text = amount,
            color = CloudWhite,
            fontSize = 11.5.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
        )
        Spacer(Modifier.width(4.dp))
        Box(
            modifier = Modifier
                .size(16.dp)
                .clip(CircleShape)
                .background(borderColor),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "+",
                color = DeepInk,
                fontSize = 10.sp,
                fontWeight = FontWeight.Black,
            )
        }
    }
}

/**
 * RoyalAvatarFrame:
 * Avatar with animated glowing frame, customizable frame color (gold, cyan, pink, violet),
 * and level badge.
 */
@Composable
fun RoyalAvatarFrame(
    avatarRes: Int,
    size: Dp = 56.dp,
    frameRes: Int = R.drawable.aurora3_frame_gold,
    level: Int? = null,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(size)
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painterResource(avatarRes),
            contentDescription = null,
            modifier = Modifier
                .size(size * 0.76f)
                .clip(CircleShape),
            contentScale = ContentScale.Crop,
        )
        Image(
            painter = painterResource(frameRes),
            contentDescription = null,
            modifier = Modifier.size(size),
        )
        if (level != null) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .offset(y = 3.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(AuroraGradients.royalGold)
                    .padding(horizontal = 4.dp, vertical = 0.5.dp),
            ) {
                Text(
                    text = "Lv.$level",
                    color = DeepInk,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Black,
                )
            }
        }
    }
}

/**
 * FantasyGameCard:
 * Rich illustrated game card with 3D artwork, glowing badge, title, player count,
 * and dimensional "PLAY" button.
 */
@Composable
fun FantasyGameCard(
    title: String,
    playersCount: String,
    artworkRes: Int,
    tag: String,
    onPlayClick: () -> Unit,
    modifier: Modifier = Modifier,
    tagColor: Color = SunsetRose,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1.0f,
        animationSpec = tween(120),
        label = "GameCardScale",
    )

    Box(
        modifier = modifier
            .scale(scale)
            .fillMaxWidth()
            .height(130.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(AuroraGradients.celestialCard)
            .border(1.2.dp, DreamLavender.copy(alpha = 0.35f), RoundedCornerShape(20.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onPlayClick,
            ),
    ) {
        // Decorative Artwork on right
        Image(
            painter = painterResource(artworkRes),
            contentDescription = title,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .size(115.dp)
                .padding(end = 8.dp),
            contentScale = ContentScale.Fit,
        )

        // Left Content Column
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Column {
                // Category Tag
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(tagColor.copy(alpha = 0.25f))
                        .border(0.8.dp, tagColor.copy(alpha = 0.7f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 7.dp, vertical = 2.dp),
                ) {
                    Text(
                        text = tag,
                        color = tagColor,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }

                Spacer(Modifier.height(6.dp))

                Text(
                    text = title,
                    color = CloudWhite,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )

                Text(
                    text = "👥 $playersCount playing",
                    color = DreamLavender,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                )
            }

            // Dimensional Play Button
            Box(
                modifier = Modifier
                    .height(30.dp)
                    .clip(RoundedCornerShape(15.dp))
                    .background(AuroraGradients.sunsetFantasy)
                    .border(1.dp, CloudWhite.copy(alpha = 0.5f), RoundedCornerShape(15.dp))
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "PLAY ›",
                    color = CloudWhite,
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Black,
                )
            }
        }
    }
}

/**
 * PremiumShortcut:
 * Illustrated circular / rounded shortcut tile with glowing border and label.
 */
@Composable
fun PremiumShortcut(
    title: String,
    subtitle: String,
    iconRes: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    badgeText: String? = null,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(DeepInk.copy(alpha = 0.85f))
            .border(1.dp, DreamLavender.copy(alpha = 0.35f), RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp, horizontal = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(contentAlignment = Alignment.Center) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(MysticPurple.copy(alpha = 0.45f), Color.Transparent)
                        )
                    )
                    .border(1.2.dp, CrystalBlue.copy(alpha = 0.6f), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Image(
                    painter = painterResource(iconRes),
                    contentDescription = title,
                    modifier = Modifier.size(26.dp),
                    contentScale = ContentScale.Fit,
                )
            }

            if (!badgeText.isNullOrBlank()) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = 6.dp, y = (-2).dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(SunsetRose)
                        .padding(horizontal = 4.dp, vertical = 1.dp),
                ) {
                    Text(
                        text = badgeText,
                        color = CloudWhite,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Black,
                    )
                }
            }
        }

        Spacer(Modifier.height(6.dp))
        Text(
            text = title,
            color = CloudWhite,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = subtitle,
            color = DreamLavender,
            fontSize = 9.5.sp,
            maxLines = 1,
        )
    }
}

/**
 * GlowingSectionHeader:
 * Section title with sparkling star icon, subtitle, and "See All" action pill.
 */
@Composable
fun GlowingSectionHeader(
    title: String,
    subtitle: String? = null,
    actionLabel: String? = null,
    onActionClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(R.drawable.aurora3_ic_nav_star),
                contentDescription = null,
                modifier = Modifier.size(16.dp),
            )
            Spacer(Modifier.width(6.dp))
            Column {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = CloudWhite,
                )
                if (!subtitle.isNullOrBlank()) {
                    Text(
                        text = subtitle,
                        fontSize = 11.sp,
                        color = DreamLavender,
                    )
                }
            }
        }

        if (!actionLabel.isNullOrBlank() && onActionClick != null) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(DeepInk)
                    .border(1.dp, DreamLavender.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                    .clickable(onClick = onActionClick)
                    .padding(horizontal = 10.dp, vertical = 4.dp),
            ) {
                Text(
                    text = actionLabel,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = CrystalBlue,
                )
            }
        }
    }
}

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
    textColor: Color = CloudWhite,
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
            .background(if (enabled) brush else Brush.linearGradient(listOf(Color(0xFF3B2E5C), Color(0xFF2C1E4E))))
            .border(1.dp, CloudWhite.copy(alpha = 0.35f), RoundedCornerShape(height / 2f))
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
 * AuroraCard: Reusable translucent celestial card container.
 */
@Composable
fun AuroraCard(
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape = RoundedCornerShape(20.dp),
    backgroundColor: Color = DeepInk.copy(alpha = 0.85f),
    borderColor: Color = DreamLavender.copy(alpha = 0.30f),
    elevation: Dp = 0.dp,
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
 * AuroraSectionHeader: Standard header across screens.
 */
@Composable
fun AuroraSectionHeader(
    title: String,
    subtitle: String? = null,
    actionLabel: String? = null,
    onActionClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    GlowingSectionHeader(
        title = title,
        subtitle = subtitle,
        actionLabel = actionLabel,
        onActionClick = onActionClick,
        modifier = modifier,
    )
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
    val bg = if (selected) AuroraGradients.sunsetFantasy else Brush.linearGradient(listOf(DeepInk, DeepInk))
    val textCol = if (selected) CloudWhite else DreamLavender
    val borderCol = if (selected) SunsetRose else DreamLavender.copy(alpha = 0.35f)

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

/**
 * AuroraIconTile: Reusable tile for shortcuts.
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
                    .background(DeepInk)
                    .border(1.dp, DreamLavender.copy(alpha = 0.4f), RoundedCornerShape(18.dp)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = painterResource(R.drawable.aurora3_ic_nav_star),
                    contentDescription = label,
                    tint = Color.Unspecified,
                    modifier = Modifier.size(size * 0.65f),
                )
            }

            if (!badgeText.isNullOrBlank()) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 2.dp, end = 2.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(SunsetRose)
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
            color = CloudWhite,
            maxLines = 1,
        )
    }
}

/**
 * PremiumGiftPack:
 * 3D-style treasure box presentation with countdown timer, reward crystals,
 * celebration particles, and purchase CTA button.
 */
@Composable
fun PremiumGiftPack(
    countdownText: String,
    offerTitle: String,
    rewardAmount: String,
    priceText: String,
    onBuyClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(
                Brush.horizontalGradient(
                    listOf(
                        Color(0xFF381466),
                        Color(0xFF221248),
                    )
                )
            )
            .border(1.2.dp, RoyalGold.copy(alpha = 0.65f), RoundedCornerShape(22.dp))
            .padding(14.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // 3D Treasure Box Icon
            Image(
                painter = painterResource(R.drawable.aurora3_gift_royal),
                contentDescription = "Gift Box",
                modifier = Modifier.size(64.dp),
                contentScale = ContentScale.Fit,
            )

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                // Countdown Badge
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "⏳ $countdownText",
                        color = RoyalGold,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                    )
                }
                Spacer(Modifier.height(2.dp))
                Text(
                    text = offerTitle,
                    color = CloudWhite,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                )
                Text(
                    text = "Includes $rewardAmount Crystals + VIP Frame",
                    color = DreamLavender,
                    fontSize = 11.sp,
                )
            }

            Spacer(Modifier.width(8.dp))

            // Purchase Button
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(AuroraGradients.royalGold)
                    .clickable(onClick = onBuyClick)
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = priceText,
                    color = DeepInk,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                )
            }
        }
    }
}

/**
 * AuroraNavigationDock:
 * Floating translucent violet navigation dock for the 5 main tabs:
 * Games, Voice Room, Chats, Discover, Me.
 */
@Composable
fun AuroraNavigationDock(
    currentRoute: String?,
    onTabSelected: (String) -> Unit,
    msgBadgeCount: Int = 0,
    modifier: Modifier = Modifier,
) {
    data class DockTab(
        val route: String,
        val label: String,
        val iconRes: Int,
    )

    val tabs = listOf(
        DockTab("tab_hub", "Games", R.drawable.aurora3_ic_nav_games),
        DockTab("tab_room", "Voice", R.drawable.aurora3_ic_nav_rooms),
        DockTab("tab_msg", "Chats", R.drawable.aurora3_ic_nav_chats),
        DockTab("tab_discover", "Discover", R.drawable.aurora3_ic_nav_discover),
        DockTab("tab_me", "Me", R.drawable.aurora3_ic_nav_me),
    )

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = Color.Transparent,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 6.dp)
                .clip(RoundedCornerShape(26.dp))
                .background(AuroraGradients.floatingDock)
                .border(1.2.dp, DreamLavender.copy(alpha = 0.35f), RoundedCornerShape(26.dp))
                .padding(vertical = 6.dp, horizontal = 4.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                tabs.forEach { tab ->
                    val isSelected = currentRoute == tab.route
                    val activeColor = if (tab.route == "tab_hub") RoyalGold else CrystalBlue

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                            ) { onTabSelected(tab.route) },
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            // Active glowing halo
                            if (isSelected) {
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(CircleShape)
                                        .background(activeColor.copy(alpha = 0.22f)),
                                )
                            }
                            Icon(
                                painter = painterResource(tab.iconRes),
                                contentDescription = tab.label,
                                tint = if (isSelected) activeColor else DreamLavender.copy(alpha = 0.70f),
                                modifier = Modifier.size(24.dp),
                            )

                            // Unread Badge for Chats
                            if (tab.route == "tab_msg" && msgBadgeCount > 0) {
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .offset(x = 8.dp, y = (-4).dp)
                                        .size(16.dp)
                                        .clip(CircleShape)
                                        .background(SunsetRose),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Text(
                                        text = "$msgBadgeCount",
                                        color = CloudWhite,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                    )
                                }
                            }
                        }

                        Spacer(Modifier.height(2.dp))

                        Text(
                            text = tab.label,
                            color = if (isSelected) CloudWhite else DreamLavender.copy(alpha = 0.65f),
                            fontSize = 10.5.sp,
                            fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
                        )
                    }
                }
            }
        }
    }
}
