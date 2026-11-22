package com.sundram.weplay.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.sundram.weplay.data.FrameTier
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Reusable animated profile avatar component supporting 6 cosmetic tiers.
 * Renders native Jetpack Compose Canvas effects with layered radial gradients,
 * particle orbits, star twinkles, floating crowns, and aurora sweep rings.
 *
 * When animated = false or size < 52.dp, simplifies rendering to static
 * vector-drawn rings to maintain maximum 60/120fps performance in long LazyLists.
 */
@Composable
fun AvatarWithFrame(
    name: String,
    photoPath: String?,
    size: Dp,
    modifier: Modifier = Modifier,
    frameTier: FrameTier? = FrameTier.COMMON,
    animated: Boolean = true,
    showCrown: Boolean = false,
) {
    val tier = frameTier ?: FrameTier.COMMON

    // Only run animation transitions if enabled, tier supports motion, and size is >= 52dp
    val shouldAnimate = animated && tier != FrameTier.COMMON && size >= 52.dp

    // Animation transition
    val transition = if (shouldAnimate) rememberInfiniteTransition(label = "FrameAnimation_${tier.name}") else null

    // Rare: 2200ms alpha pulse (0.65f..1.0f)
    val rareAlpha by if (shouldAnimate && tier == FrameTier.RARE) {
        transition!!.animateFloat(
            initialValue = 0.65f,
            targetValue = 1.0f,
            animationSpec = infiniteRepeatable(
                animation = tween(2200, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse,
            ),
            label = "RarePulse",
        )
    } else {
        androidx.compose.runtime.remember { androidx.compose.runtime.mutableFloatStateOf(1.0f) }
    }

    // Epic: 4200ms clockwise rotation
    val epicRotation by if (shouldAnimate && tier == FrameTier.EPIC) {
        transition!!.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(4200, easing = LinearEasing),
                repeatMode = RepeatMode.Restart,
            ),
            label = "EpicRotation",
        )
    } else {
        androidx.compose.runtime.remember { androidx.compose.runtime.mutableFloatStateOf(0f) }
    }

    // Legendary: 5600ms counter-rotating orbit & twinkle
    val legRotation by if (shouldAnimate && tier == FrameTier.LEGENDARY) {
        transition!!.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(5600, easing = LinearEasing),
                repeatMode = RepeatMode.Restart,
            ),
            label = "LegendaryRotation",
        )
    } else {
        androidx.compose.runtime.remember { androidx.compose.runtime.mutableFloatStateOf(0f) }
    }
    val legTwinkle by if (shouldAnimate && tier == FrameTier.LEGENDARY) {
        transition!!.animateFloat(
            initialValue = 0.5f,
            targetValue = 1.0f,
            animationSpec = infiniteRepeatable(
                animation = tween(1400, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse,
            ),
            label = "LegendaryTwinkle",
        )
    } else {
        androidx.compose.runtime.remember { androidx.compose.runtime.mutableFloatStateOf(1.0f) }
    }

    // Mythic: 6400ms slow crown float & 3 jewels orbit
    val mythicCrownFloat by if (shouldAnimate && tier == FrameTier.MYTHIC) {
        transition!!.animateFloat(
            initialValue = -6f,
            targetValue = -1f,
            animationSpec = infiniteRepeatable(
                animation = tween(3200, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse,
            ),
            label = "MythicFloat",
        )
    } else {
        androidx.compose.runtime.remember { androidx.compose.runtime.mutableFloatStateOf(-3f) }
    }
    val mythicRotation by if (shouldAnimate && tier == FrameTier.MYTHIC) {
        transition!!.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(6400, easing = LinearEasing),
                repeatMode = RepeatMode.Restart,
            ),
            label = "MythicRotation",
        )
    } else {
        androidx.compose.runtime.remember { androidx.compose.runtime.mutableFloatStateOf(0f) }
    }

    // Celestial: 8000ms aurora sweep & jewel orbit
    val celestialRotation by if (shouldAnimate && tier == FrameTier.CELESTIAL) {
        transition!!.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(8000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart,
            ),
            label = "CelestialRotation",
        )
    } else {
        androidx.compose.runtime.remember { androidx.compose.runtime.mutableFloatStateOf(0f) }
    }
    val celestialPulse by if (shouldAnimate && tier == FrameTier.CELESTIAL) {
        transition!!.animateFloat(
            initialValue = 0.75f,
            targetValue = 1.0f,
            animationSpec = infiniteRepeatable(
                animation = tween(2600, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse,
            ),
            label = "CelestialPulse",
        )
    } else {
        androidx.compose.runtime.remember { androidx.compose.runtime.mutableFloatStateOf(1.0f) }
    }

    // Canvas size has extra margin for orbit particles and floating crowns
    val framePadding = when {
        size < 52.dp -> 6.dp
        size < 80.dp -> 12.dp
        else -> 18.dp
    }
    val totalSize = size + (framePadding * 2)

    val primaryColor = Color(tier.primaryColor)
    val secondaryColor = Color(tier.secondaryColor)

    Box(
        modifier = modifier.size(totalSize),
        contentAlignment = Alignment.Center,
    ) {
        // Decorative Animated Canvas Frame
        Canvas(modifier = Modifier.size(totalSize)) {
            val center = Offset(this.size.width / 2f, this.size.height / 2f)
            val avatarRadius = (size.toPx() - (framePadding * 2).toPx()) / 2f + framePadding.toPx()
            val ringRadius = avatarRadius + 1.dp.toPx()

            when (tier) {
                FrameTier.COMMON -> {
                    // Static clean cyan/white dual ring
                    drawCircle(
                        color = primaryColor,
                        radius = ringRadius,
                        center = center,
                        style = Stroke(width = 3.dp.toPx()),
                    )
                    drawCircle(
                        color = secondaryColor.copy(alpha = 0.6f),
                        radius = ringRadius + 2.dp.toPx(),
                        center = center,
                        style = Stroke(width = 1.dp.toPx()),
                    )
                }

                FrameTier.RARE -> {
                    // 2200ms cyan breathing pulse glow
                    val glowAlpha = if (shouldAnimate) rareAlpha else 0.85f
                    // Outer diffuse aura
                    drawCircle(
                        color = primaryColor.copy(alpha = 0.25f * glowAlpha),
                        radius = ringRadius + 4.dp.toPx(),
                        center = center,
                        style = Stroke(width = 4.dp.toPx()),
                    )
                    // Inner bright ring
                    drawCircle(
                        color = primaryColor.copy(alpha = glowAlpha),
                        radius = ringRadius,
                        center = center,
                        style = Stroke(width = 3.dp.toPx()),
                    )
                    // Diamond top jewel
                    drawDiamondJewel(
                        center = Offset(center.x, center.y - ringRadius - 1.dp.toPx()),
                        sizePx = 8.dp.toPx(),
                        color = secondaryColor,
                    )
                }

                FrameTier.EPIC -> {
                    // Royal violet ring with 8 rotating orbital particles
                    drawCircle(
                        color = primaryColor,
                        radius = ringRadius,
                        center = center,
                        style = Stroke(width = 3.dp.toPx()),
                    )
                    drawCircle(
                        color = secondaryColor.copy(alpha = 0.5f),
                        radius = ringRadius - 2.dp.toPx(),
                        center = center,
                        style = Stroke(width = 1.dp.toPx()),
                    )

                    // 8 Orbiting particles
                    val angleOffset = if (shouldAnimate) epicRotation else 45f
                    val orbitRadius = ringRadius + 3.dp.toPx()
                    for (i in 0 until 8) {
                        val angleDeg = angleOffset + (i * 45f)
                        val angleRad = angleDeg * (PI / 180f).toFloat()
                        val px = center.x + orbitRadius * cos(angleRad)
                        val py = center.y + orbitRadius * sin(angleRad)
                        val particleRadius = if (i % 2 == 0) 2.2.dp.toPx() else 1.4.dp.toPx()
                        drawCircle(
                            color = if (i % 2 == 0) secondaryColor else primaryColor,
                            radius = particleRadius,
                            center = Offset(px, py),
                        )
                    }
                }

                FrameTier.LEGENDARY -> {
                    // Gold ring with 2 counter-rotating bands & 12 sparkling stars
                    val rot1 = if (shouldAnimate) legRotation else 30f
                    val rot2 = if (shouldAnimate) -legRotation else -30f
                    val twinkle = if (shouldAnimate) legTwinkle else 0.9f

                    // Base gold ring
                    drawCircle(
                        color = primaryColor,
                        radius = ringRadius,
                        center = center,
                        style = Stroke(width = 3.5.dp.toPx()),
                    )

                    // Outer orbit arc 1
                    rotate(rot1, center) {
                        drawArc(
                            color = secondaryColor.copy(alpha = 0.8f * twinkle),
                            startAngle = 0f,
                            sweepAngle = 70f,
                            useCenter = false,
                            topLeft = Offset(center.x - ringRadius - 3.dp.toPx(), center.y - ringRadius - 3.dp.toPx()),
                            size = Size((ringRadius + 3.dp.toPx()) * 2, (ringRadius + 3.dp.toPx()) * 2),
                            style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round),
                        )
                        drawArc(
                            color = secondaryColor.copy(alpha = 0.8f * twinkle),
                            startAngle = 180f,
                            sweepAngle = 70f,
                            useCenter = false,
                            topLeft = Offset(center.x - ringRadius - 3.dp.toPx(), center.y - ringRadius - 3.dp.toPx()),
                            size = Size((ringRadius + 3.dp.toPx()) * 2, (ringRadius + 3.dp.toPx()) * 2),
                            style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round),
                        )
                    }

                    // Counter-rotating outer stars
                    val starOrbit = ringRadius + 4.dp.toPx()
                    for (i in 0 until 12) {
                        val angleDeg = rot2 + (i * 30f)
                        val angleRad = angleDeg * (PI / 180f).toFloat()
                        val sx = center.x + starOrbit * cos(angleRad)
                        val sy = center.y + starOrbit * sin(angleRad)
                        val starSize = if (i % 3 == 0) 2.5.dp.toPx() * twinkle else 1.5.dp.toPx()
                        drawCircle(
                            color = if (i % 3 == 0) Color.White else primaryColor,
                            radius = starSize,
                            center = Offset(sx, sy),
                        )
                    }
                }

                FrameTier.MYTHIC -> {
                    // Floating crown + dual gold/rose concentric rings + 3 orbiting jewels
                    val rot = if (shouldAnimate) mythicRotation else 60f

                    // Inner & outer dual rings
                    drawCircle(
                        color = primaryColor,
                        radius = ringRadius,
                        center = center,
                        style = Stroke(width = 3.dp.toPx()),
                    )
                    drawCircle(
                        color = secondaryColor,
                        radius = ringRadius + 2.5.dp.toPx(),
                        center = center,
                        style = Stroke(width = 1.2.dp.toPx()),
                    )

                    // 3 Orbiting Jewels
                    val jewelOrbit = ringRadius + 4.5.dp.toPx()
                    for (i in 0 until 3) {
                        val angleDeg = rot + (i * 120f)
                        val angleRad = angleDeg * (PI / 180f).toFloat()
                        val jx = center.x + jewelOrbit * cos(angleRad)
                        val jy = center.y + jewelOrbit * sin(angleRad)
                        drawDiamondJewel(
                            center = Offset(jx, jy),
                            sizePx = 5.dp.toPx(),
                            color = if (i == 0) primaryColor else secondaryColor,
                        )
                    }
                }

                FrameTier.CELESTIAL -> {
                    // Aurora sweep and jewel orbit with rainbow-like teal/purple sheen
                    val sweepRot = if (shouldAnimate) celestialRotation else 90f
                    val pulse = if (shouldAnimate) celestialPulse else 0.9f

                    // Sweep gradient ring
                    rotate(sweepRot, center) {
                        drawCircle(
                            brush = Brush.sweepGradient(
                                listOf(
                                    primaryColor,
                                    secondaryColor,
                                    Color.White,
                                    primaryColor,
                                ),
                                center = center,
                            ),
                            radius = ringRadius,
                            center = center,
                            style = Stroke(width = 3.8.dp.toPx()),
                        )
                    }

                    // Outer celestial aura rays
                    drawCircle(
                        color = primaryColor.copy(alpha = 0.2f * pulse),
                        radius = ringRadius + 5.dp.toPx(),
                        center = center,
                        style = Stroke(width = 3.dp.toPx()),
                    )

                    // 6 Orbiting celestial starlight gems
                    val gemOrbit = ringRadius + 4.dp.toPx()
                    for (i in 0 until 6) {
                        val angleDeg = -sweepRot + (i * 60f)
                        val angleRad = angleDeg * (PI / 180f).toFloat()
                        val gx = center.x + gemOrbit * cos(angleRad)
                        val gy = center.y + gemOrbit * sin(angleRad)
                        drawCircle(
                            color = if (i % 2 == 0) Color.White else secondaryColor,
                            radius = if (i % 2 == 0) 2.4.dp.toPx() else 1.6.dp.toPx(),
                            center = Offset(gx, gy),
                        )
                    }
                }
            }
        }

        // Inner User Avatar Photo / Monogram
        UserAvatar(
            name = name,
            photoPath = photoPath,
            size = size,
            ring = null,
        )

        // Floating Mythic Crown on Top
        if (tier == FrameTier.MYTHIC || showCrown) {
            val crownOffset = if (shouldAnimate && tier == FrameTier.MYTHIC) mythicCrownFloat.dp else (-4).dp
            Canvas(
                modifier = Modifier
                    .size(if (size >= 80.dp) 24.dp else 16.dp)
                    .align(Alignment.TopCenter)
                    .offset(y = crownOffset),
            ) {
                drawCrownShape(
                    color = Color(0xFFFFD700),
                    accentColor = Color(0xFFF18BC8),
                )
            }
        }
    }
}

/** Draws a 4-point diamond jewel at the specified center. */
private fun DrawScope.drawDiamondJewel(center: Offset, sizePx: Float, color: Color) {
    val half = sizePx / 2f
    val path = Path().apply {
        moveTo(center.x, center.y - half)
        lineTo(center.x + half, center.y)
        lineTo(center.x, center.y + half)
        lineTo(center.x - half, center.y)
        close()
    }
    drawPath(path, color)
}

/** Draws a miniature ornate gold crown for Mythic tiers & podium champions. */
private fun DrawScope.drawCrownShape(color: Color, accentColor: Color) {
    val w = size.width
    val h = size.height
    val path = Path().apply {
        moveTo(0f, h * 0.85f)
        lineTo(w * 0.15f, h * 0.35f)
        lineTo(w * 0.40f, h * 0.65f)
        lineTo(w * 0.50f, h * 0.15f)
        lineTo(w * 0.60f, h * 0.65f)
        lineTo(w * 0.85f, h * 0.35f)
        lineTo(w, h * 0.85f)
        lineTo(w * 0.5f, h * 0.95f)
        close()
    }
    drawPath(path, color)

    // Center jewel on crown
    drawCircle(
        color = accentColor,
        radius = w * 0.08f,
        center = Offset(w * 0.50f, h * 0.55f),
    )
}
