package com.sundram.weplay.ui.theme

import android.graphics.BitmapFactory
import androidx.compose.animation.animateColorAsState
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
import androidx.compose.foundation.layout.widthIn
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
import androidx.compose.runtime.produceState
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
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sundram.weplay.R
import com.sundram.weplay.data.FrameTier
import com.sundram.weplay.ui.components.PhoenixAvatarFrame
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
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
        // High-resolution celestial WebP background (ap3r_bg_games_palace)
        Image(
            painter = painterResource(R.drawable.ap3r_bg_games_palace),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
        )

        // Subtle sparkle particles overlay
        Image(
            painter = painterResource(R.drawable.ap3r_overlay_sparkle_particles),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(320.dp)
                .align(Alignment.TopCenter),
            contentScale = ContentScale.Crop,
            alpha = starAlpha * 0.45f,
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
    val transition = rememberInfiniteTransition(label = "voiceBgAnim3")
    val sparkleAlpha by if (animated) {
        transition.animateFloat(
            initialValue = 0.40f,
            targetValue = 0.90f,
            animationSpec = infiniteRepeatable(
                animation = tween(3000, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse,
            ),
            label = "sparkleTwinkle3",
        )
    } else {
        remember { mutableFloatStateOf(0.70f) }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(CelestialNavy),
    ) {
        // 1. Continuous deep violet celestial body background with muted stars
        Image(
            painter = painterResource(R.drawable.ap_voice_background_body),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
        )

        // 2. Celestial Palace Header Artwork at top with illuminated golden palace, crescent moon, lavender clouds
        Image(
            painter = painterResource(R.drawable.ap_voice_header_palace),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .align(Alignment.TopCenter),
            contentScale = ContentScale.Crop,
        )

        // 3. Delicate celestial sparkles overlay with gentle twinkle
        Image(
            painter = painterResource(R.drawable.ap_voice_fx_sparkles),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
                .align(Alignment.TopCenter),
            contentScale = ContentScale.Crop,
            alpha = sparkleAlpha * 0.55f,
        )

        // 4. Smooth gradient blend from header artwork down into list body
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
                .align(Alignment.TopCenter)
                .offset(y = 200.dp)
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color.Transparent,
                            CelestialNavy.copy(alpha = 0.50f),
                            CelestialNavy.copy(alpha = 0.95f),
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
            painter = painterResource(R.drawable.ap3r_bg_profile_dream),
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
            painter = painterResource(R.drawable.ap3r_bg_voice_midnight),
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
            painter = painterResource(R.drawable.ap3r_bg_event_festival),
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
 * Compact user profile cluster with actual photo/avatar, equipped frame and level badge,
 * "Welcome" greeting + truncated username, compact gold coin wallet pill with separate Top Up
 * plus button, and right-aligned notification bell with real unread badge.
 */
@Suppress("UNUSED_PARAMETER")
@Composable
fun CelestialHeader(
    avatarRes: Int,
    userName: String,
    userLevel: Int,
    coins: Long,
    crystals: Long? = null,
    onAvatarClick: () -> Unit,
    onCoinsClick: () -> Unit,
    onCrystalsClick: () -> Unit = {},
    onEventsClick: () -> Unit,
    onNotificationsClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    photoPath: String? = null,
    frameId: String? = null,
    notificationCount: Int = 0,
    onTopUpClick: () -> Unit = onCoinsClick,
) {
    val formattedCoins = remember(coins) {
        NumberFormat.getIntegerInstance().format(coins)
    }
    val userBitmap by produceState<ImageBitmap?>(initialValue = null, key1 = photoPath) {
        value = if (!photoPath.isNullOrBlank()) {
            withContext(Dispatchers.IO) {
                runCatching { BitmapFactory.decodeFile(photoPath)?.asImageBitmap() }.getOrNull()
            }
        } else {
            null
        }
    }
    val isPhoenixFrame = frameId.equals("phoenix", ignoreCase = true) ||
        frameId.equals("PHOENIX_REBIRTH", ignoreCase = true)
    val ringFrameRes = remember(frameId) {
        if (!frameId.isNullOrBlank() && !isPhoenixFrame) {
            FrameTier.fromId(frameId).ringDrawableRes
        } else {
            R.drawable.aurora3_frame_gold
        }
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 2.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // 1. User Avatar, Equipped Frame & Profile Greeting Trigger
        Row(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(24.dp))
                .clickable(onClick = onAvatarClick)
                .padding(end = 6.dp, top = 2.dp, bottom = 2.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier.size(50.dp),
                contentAlignment = Alignment.Center,
            ) {
                if (isPhoenixFrame) {
                    PhoenixAvatarFrame(
                        name = userName,
                        photoPath = photoPath,
                        size = 48.dp,
                        animate = false,
                        contentDescription = userName,
                    )
                } else {
                    if (userBitmap != null) {
                        Image(
                            bitmap = userBitmap!!,
                            contentDescription = userName,
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop,
                        )
                    } else {
                        Image(
                            painter = painterResource(avatarRes),
                            contentDescription = userName,
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop,
                        )
                    }
                    Image(
                        painter = painterResource(ringFrameRes),
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                    )
                }

                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .offset(y = 2.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(AuroraGradients.royalGold)
                        .border(0.8.dp, CloudWhite, RoundedCornerShape(8.dp))
                        .padding(horizontal = 5.dp, vertical = 0.5.dp),
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

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Welcome",
                    color = DreamLavender,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = userName,
                        color = CloudWhite,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false),
                    )
                    Spacer(Modifier.width(2.dp))
                    Icon(
                        painter = painterResource(R.drawable.ic_chevron_right),
                        contentDescription = "Open profile",
                        tint = CloudWhite.copy(alpha = 0.92f),
                        modifier = Modifier.size(14.dp),
                    )
                }
            }
        }

        Spacer(Modifier.width(6.dp))

        // 2. Compact Gold Coin Wallet Pill (coins -> Wallet, + -> Top Up)
        CrystalCurrencyPill(
            iconRes = R.drawable.ap_home_gold_coin,
            amount = formattedCoins,
            gradient = Brush.horizontalGradient(listOf(Color(0xFF281948), Color(0xFF1F123A))),
            borderColor = RoyalGold,
            onClick = onCoinsClick,
            onPlusClick = onTopUpClick,
        )

        Spacer(Modifier.width(8.dp))

        // 3. Notification Bell Action with Real Unread Badge
        val notifyAction = onNotificationsClick ?: onEventsClick
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(DeepInk.copy(alpha = 0.88f))
                .border(1.dp, DreamLavender.copy(alpha = 0.30f), CircleShape)
                .clickable(onClick = notifyAction),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(R.drawable.ap_home_notification_bell),
                contentDescription = "Notifications",
                modifier = Modifier.size(20.dp),
                contentScale = ContentScale.Fit,
            )

            if (notificationCount > 0) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = (-3).dp, y = 3.dp)
                        .widthIn(min = 14.dp)
                        .height(14.dp)
                        .clip(CircleShape)
                        .background(SunsetRose)
                        .padding(horizontal = 3.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = if (notificationCount > 99) "99+" else "$notificationCount",
                        color = Color.White,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    }
}

/**
 * CrystalCurrencyPill:
 * Compact premium gold coin wallet pill with real coin balance (opens wallet)
 * and a distinct circular "+" Top Up trigger.
 */
@Composable
fun CrystalCurrencyPill(
    iconRes: Int,
    amount: String,
    gradient: Brush,
    borderColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    onPlusClick: (() -> Unit)? = null,
) {
    val plusInteraction = remember { MutableInteractionSource() }
    val plusPressed by plusInteraction.collectIsPressedAsState()
    val plusScale by animateFloatAsState(
        targetValue = if (plusPressed) 0.90f else 1.0f,
        animationSpec = tween(durationMillis = 120),
        label = "CoinPlusScale",
    )

    Row(
        modifier = modifier
            .height(36.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(gradient)
            .border(1.dp, borderColor.copy(alpha = 0.60f), RoundedCornerShape(18.dp)),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(topStart = 18.dp, bottomStart = 18.dp))
                .clickable(onClick = onClick)
                .padding(start = 8.dp, end = 8.dp, top = 6.dp, bottom = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                painter = painterResource(iconRes),
                contentDescription = "Coins",
                modifier = Modifier.size(20.dp),
                contentScale = ContentScale.Fit,
            )
            Spacer(Modifier.width(5.dp))
            Text(
                text = amount,
                color = CloudWhite,
                fontSize = 12.5.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                softWrap = false,
            )
        }

        Box(
            modifier = Modifier
                .width(1.dp)
                .height(16.dp)
                .background(DreamLavender.copy(alpha = 0.25f)),
        )

        Box(
            modifier = Modifier
                .scale(plusScale)
                .clip(RoundedCornerShape(topEnd = 18.dp, bottomEnd = 18.dp))
                .clickable(
                    interactionSource = plusInteraction,
                    indication = null,
                    onClick = onPlusClick ?: onClick,
                )
                .padding(start = 7.dp, end = 7.dp, top = 6.dp, bottom = 6.dp),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(R.drawable.ap_home_plus),
                contentDescription = "Top up coins",
                modifier = Modifier.size(22.dp),
                contentScale = ContentScale.Fit,
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
                    text = "$playersCount playing",
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
 * Focused neon-line quick action card with controlled border glow and crisp label.
 */
@Composable
fun PremiumShortcut(
    title: String,
    subtitle: String,
    iconRes: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    badgeText: String? = null,
    accentBorderColor: Color = DreamLavender,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1.0f,
        animationSpec = tween(durationMillis = 120),
        label = "ShortcutPressScale",
    )

    Column(
        modifier = modifier
            .scale(scale)
            .height(94.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF221658).copy(alpha = 0.92f),
                        Color(0xFF1A1044).copy(alpha = 0.95f),
                    )
                )
            )
            .border(1.2.dp, accentBorderColor.copy(alpha = 0.65f), RoundedCornerShape(16.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick,
            )
            .padding(vertical = 10.dp, horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(contentAlignment = Alignment.Center) {
            Image(
                painter = painterResource(iconRes),
                contentDescription = title,
                modifier = Modifier.size(38.dp),
                contentScale = ContentScale.Fit,
            )

            if (!badgeText.isNullOrBlank()) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = 8.dp, y = (-2).dp)
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
            fontSize = 12.5.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        if (subtitle.isNotBlank()) {
            Text(
                text = subtitle,
                color = DreamLavender,
                fontSize = 10.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
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
    subtitle: String? = null,
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
            // 3D Treasure Box Presentation with Ambient Glow & Pedestal
            Box(
                modifier = Modifier.size(64.dp),
                contentAlignment = Alignment.Center,
            ) {
                Image(
                    painter = painterResource(R.drawable.ap3r_fx_gift_ambient_glow),
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    contentScale = ContentScale.Fit,
                )
                Image(
                    painter = painterResource(R.drawable.ap3r_fx_reward_pedestal),
                    contentDescription = null,
                    modifier = Modifier
                        .size(54.dp)
                        .align(Alignment.BottomCenter)
                        .offset(y = 6.dp),
                    contentScale = ContentScale.Fit,
                )
                Image(
                    painter = painterResource(R.drawable.vb_gift_pack),
                    contentDescription = "Gift Box",
                    modifier = Modifier.size(52.dp),
                    contentScale = ContentScale.Fit,
                )
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                // Countdown Badge
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Ends in $countdownText",
                        color = RoyalGold,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
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
                    text = subtitle ?: "Includes $rewardAmount Coins + VIP Frame",
                    color = DreamLavender,
                    fontSize = 11.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
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
                .border(0.8.dp, Color.White.copy(alpha = 0.10f), RoundedCornerShape(26.dp))
                .padding(vertical = 6.dp, horizontal = 4.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                tabs.forEach { tab ->
                    val isSelected = currentRoute == tab.route
                    val activeColor = if (tab.route == "tab_hub") RoyalGold else CrystalBlue
                    val pillColor by animateColorAsState(
                        targetValue = if (isSelected) activeColor.copy(alpha = 0.22f) else Color.Transparent,
                        animationSpec = tween(durationMillis = 200),
                        label = "DockTabPill_${tab.route}",
                    )

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
                            Box(
                                modifier = Modifier
                                    .width(44.dp)
                                    .height(30.dp)
                                    .clip(RoundedCornerShape(15.dp))
                                    .background(pillColor),
                            )
                            Icon(
                                painter = painterResource(tab.iconRes),
                                contentDescription = tab.label,
                                tint = if (isSelected) Color.White else DreamLavender.copy(alpha = 0.70f),
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
