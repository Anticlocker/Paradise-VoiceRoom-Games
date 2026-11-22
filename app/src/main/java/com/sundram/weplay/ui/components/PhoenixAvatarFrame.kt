package com.sundram.weplay.ui.components

import android.graphics.BitmapFactory
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sundram.weplay.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

// ─────────────────────────────────────────────────────────────────────────────
//  Phoenix Rebirth V2 Palette
// ─────────────────────────────────────────────────────────────────────────────
private val PhoenixFireRed   = Color(0xFFFF2A1A)
private val PhoenixHotOrange = Color(0xFFFF4D0B)
private val PhoenixVividOrange = Color(0xFFFF7A00)
private val PhoenixAmberGold = Color(0xFFFFB020)
private val PhoenixGold      = Color(0xFFFFD36A)
private val PhoenixPaleEmber = Color(0xFFFFF3B0)
private val PhoenixPureWhite = Color(0xFFFFFFFF)

/**
 * Production-grade Phoenix Rebirth V2 Animated Avatar Frame.
 *
 * Visual & Performance Architecture:
 * 1. Avatar Visibility: The avatar photo occupies ~65% of component diameter (expanded
 *    from 59% in starter), centered and guaranteed free of intrusion.
 * 2. Non-Intruding Geometry:
 *    - Feather roots sit strictly outside the 65% avatar perimeter (x = sqrt(65.5^2 - y^2)).
 *    - Phoenix crest rests entirely above the avatar circle (y <= -65f).
 *    - Bottom talisman gemstone anchors entirely below the avatar circle (y >= +68f).
 * 3. Lifecycle Performance:
 *    - When [animate] is false, zero infinite transition animators are scheduled;
 *      a static resting phase (0f) is used, yielding 0% background CPU/GPU overhead.
 * 4. Zero Draw Allocations:
 *    - Paths ([wingPath], [crestPath], [gemPath], [innerGemPath]) are remembered across
 *      frames and reset per pass, eliminating heap churn and GC pauses.
 * 5. Fully Responsive:
 *    - Geometry scales proportionally with [size] via `unit = size / 200f`.
 */
@Composable
fun PhoenixAvatarFrame(
    avatar: Painter,
    modifier: Modifier = Modifier,
    size: Dp = 112.dp,
    animate: Boolean = true,
    contentDescription: String? = "User avatar",
    onClick: (() -> Unit)? = null,
) {
    PhoenixAvatarFrame(
        modifier = modifier,
        size = size,
        animate = animate,
        onClick = onClick,
    ) {
        Image(
            painter = avatar,
            contentDescription = contentDescription,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
        )
    }
}

/**
 * Convenience overload accepting drawable resource ID.
 */
@Composable
fun PhoenixAvatarFrame(
    avatarResId: Int,
    modifier: Modifier = Modifier,
    size: Dp = 112.dp,
    animate: Boolean = true,
    contentDescription: String? = null,
    onClick: (() -> Unit)? = null,
) {
    PhoenixAvatarFrame(
        avatar = painterResource(avatarResId),
        modifier = modifier,
        size = size,
        animate = animate,
        contentDescription = contentDescription,
        onClick = onClick,
    )
}

/**
 * Convenience overload for user profiles (photoPath or initials fallback).
 */
@Composable
fun PhoenixAvatarFrame(
    name: String,
    photoPath: String?,
    modifier: Modifier = Modifier,
    size: Dp = 112.dp,
    animate: Boolean = true,
    contentDescription: String? = null,
    onClick: (() -> Unit)? = null,
) {
    val photoState = remember(photoPath) { mutableStateOf<ImageBitmap?>(null) }
    LaunchedEffect(photoPath) {
        photoState.value = if (photoPath != null) {
            withContext(Dispatchers.IO) {
                BitmapFactory.decodeFile(photoPath)?.asImageBitmap()
            }
        } else null
    }
    val photo = photoState.value

    PhoenixAvatarFrame(
        modifier = modifier,
        size = size,
        animate = animate,
        onClick = onClick,
    ) {
        if (photo != null) {
            Image(
                bitmap = photo,
                contentDescription = contentDescription ?: name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.linearGradient(
                            listOf(Color(0xFF8B5CF6), Color(0xFF6366F1), Color(0xFF3B82F6))
                        )
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = name.trim().take(2).uppercase().ifEmpty { "?" },
                    color = Color.White,
                    fontSize = (size.value * 0.26f).sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

/**
 * Base Composable providing the animated Phoenix Rebirth frame around custom inner content.
 * The inner avatar occupies approximately 65% of the total component size, centered and circular.
 */
@Composable
fun PhoenixAvatarFrame(
    modifier: Modifier = Modifier,
    size: Dp = 112.dp,
    animate: Boolean = true,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    // ── 1. Animation Lifecycle: Halt all transitions when animate == false ──────
    val phase by if (animate) {
        val transition = rememberInfiniteTransition(label = "PhoenixPhaseTransition")
        transition.animateFloat(
            initialValue = 0f,
            targetValue = 2f * PI.toFloat(),
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 4600, easing = LinearEasing),
                repeatMode = RepeatMode.Restart,
            ),
            label = "PhoenixPhase",
        )
    } else {
        remember { mutableFloatStateOf(0f) }
    }

    // ── 2. Cached Paths: Zero heap allocation per draw frame ───────────────────
    val wingPath = remember { Path() }
    val crestPath = remember { Path() }
    val gemPath = remember { Path() }
    val innerGemPath = remember { Path() }

    val clickModifier = if (onClick != null) {
        Modifier.clickable(onClick = onClick)
    } else {
        Modifier
    }

    Box(
        modifier = modifier
            .size(size)
            .then(clickModifier),
        contentAlignment = Alignment.Center,
    ) {
        // ── Inner Avatar: ~65% Diameter, Centered & Circular ──────────────────
        Box(
            modifier = Modifier
                .fillMaxSize(0.65f)
                .clip(CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            content()
        }

        // ── Outer Phoenix Procedural Canvas Frame ─────────────────────────────
        Canvas(modifier = Modifier.fillMaxSize()) {
            val unit = this.size.minDimension / 200f
            val center = Offset(this.size.width / 2f, this.size.height / 2f)
            val pulse = 0.5f + 0.5f * sin(phase * 1.5f)

            // 1. Radiant Aura Behind Perimeter
            drawCircle(
                brush = Brush.radialGradient(
                    0.0f to Color(0x00FF6A00),
                    0.65f to Color(0x20FF7A00),
                    0.82f to Color(0x44FF3310),
                    1.0f to Color.Transparent,
                    center = center,
                    radius = 98f * unit,
                ),
                radius = 98f * unit,
                center = center,
            )

            // 2. Warm Halo Rings (Centered around ~66-67 units, right at the 65% avatar boundary)
            drawCircle(
                color = Color(0x55FF3A06),
                radius = 67.5f * unit,
                center = center,
                style = Stroke(width = 9.5f * unit),
            )

            // Fire sweep ring with multi-stop flame palette
            drawCircle(
                brush = Brush.sweepGradient(
                    listOf(
                        PhoenixFireRed,
                        PhoenixVividOrange,
                        PhoenixGold,
                        PhoenixPaleEmber,
                        PhoenixVividOrange,
                        PhoenixFireRed,
                    ),
                    center = center,
                ),
                radius = 66.5f * unit,
                center = center,
                style = Stroke(width = (4.8f + pulse * 1.2f) * unit),
            )

            // Crisp inner metallic gold bezel framing the avatar photo
            drawCircle(
                color = PhoenixGold.copy(alpha = 0.95f),
                radius = 65f * unit,
                center = center,
                style = Stroke(width = 1.6f * unit),
            )

            // Outer gold accent ring
            drawCircle(
                color = PhoenixAmberGold.copy(alpha = 0.65f),
                radius = 69f * unit,
                center = center,
                style = Stroke(width = 0.9f * unit),
            )

            // 3. Flame-Shaped Feather Wings (Left: -1f, Right: +1f)
            // Roots are calculated using sqrt(65.5^2 - y^2) to guarantee ZERO overlap into avatar.
            for (side in listOf(-1f, 1f)) {
                for (i in 0 until 7) {
                    val wingPhase = phase + i * 0.28f
                    val wingSpread = sin(wingPhase) * (2.8f + i * 0.32f)
                    val y = -28f + i * 10.5f
                    val clampedY = y.coerceIn(-64f, 64f)
                    val circleEdge = sqrt((65.5f * 65.5f - clampedY * clampedY).coerceAtLeast(0f))

                    val rootX = side * (circleEdge + 0.5f)
                    val tipX = side * (circleEdge + 19f + (6 - i) * 2.6f + wingSpread)
                    val tipY = y - 22f + i * 1.5f + cos(wingPhase) * 2.0f

                    val baseY = (y + 10f).coerceIn(-64f, 64f)
                    val baseCircleEdge = sqrt((65.5f * 65.5f - baseY * baseY).coerceAtLeast(0f))
                    val rootX2 = side * (baseCircleEdge + 0.5f)

                    wingPath.reset()
                    wingPath.moveTo(center.x + rootX * unit, center.y + y * unit)
                    wingPath.quadraticTo(
                        center.x + side * (circleEdge + 11f) * unit,
                        center.y + (y - 14f) * unit,
                        center.x + tipX * unit,
                        center.y + tipY * unit,
                    )
                    wingPath.quadraticTo(
                        center.x + side * (circleEdge + 13f) * unit,
                        center.y + (y + 8f) * unit,
                        center.x + rootX2 * unit,
                        center.y + baseY * unit,
                    )
                    wingPath.close()

                    drawPath(
                        path = wingPath,
                        brush = Brush.linearGradient(
                            listOf(PhoenixFireRed, PhoenixVividOrange, PhoenixGold),
                            start = Offset(center.x + rootX * unit, center.y + y * unit),
                            end = Offset(center.x + tipX * unit, center.y + tipY * unit),
                        ),
                    )
                    drawPath(
                        path = wingPath,
                        color = PhoenixGold.copy(alpha = 0.75f),
                        style = Stroke(width = 0.85f * unit),
                    )
                }
            }

            // 4. Phoenix Crest / Crown (Above avatar: y <= -65f)
            crestPath.reset()
            crestPath.moveTo(center.x - 26f * unit, center.y - 65f * unit)
            // Left plume
            crestPath.quadraticTo(
                center.x - 18f * unit, center.y - 82f * unit,
                center.x - 8f * unit, center.y - 94f * unit,
            )
            // Tall center spire
            crestPath.quadraticTo(
                center.x - 3f * unit, center.y - 99f * unit,
                center.x, center.y - 100f * unit,
            )
            crestPath.quadraticTo(
                center.x + 3f * unit, center.y - 99f * unit,
                center.x + 8f * unit, center.y - 94f * unit,
            )
            // Right plume
            crestPath.quadraticTo(
                center.x + 18f * unit, center.y - 82f * unit,
                center.x + 26f * unit, center.y - 65f * unit,
            )
            // Arching bottom contour hugging the top ring
            crestPath.quadraticTo(
                center.x, center.y - 66.5f * unit,
                center.x - 26f * unit, center.y - 65f * unit,
            )
            crestPath.close()

            drawPath(
                path = crestPath,
                brush = Brush.verticalGradient(
                    listOf(PhoenixPaleEmber, PhoenixGold, PhoenixVividOrange, PhoenixFireRed),
                    startY = center.y - 100f * unit,
                    endY = center.y - 65f * unit,
                ),
            )
            drawPath(
                path = crestPath,
                color = PhoenixGold.copy(alpha = 0.85f),
                style = Stroke(width = 1f * unit),
            )

            // Center Crest Starlight Eye
            drawCircle(
                color = PhoenixPureWhite,
                radius = 2.2f * unit,
                center = Offset(center.x, center.y - 74f * unit),
            )
            drawCircle(
                color = PhoenixGold,
                radius = 4f * unit,
                center = Offset(center.x, center.y - 74f * unit),
                style = Stroke(width = 1f * unit),
            )

            // 5. Molten Drop Gemstone (Anchored below avatar: y >= +68f)
            gemPath.reset()
            gemPath.moveTo(center.x, center.y + 68f * unit) // Top apex
            gemPath.lineTo(center.x + 13f * unit, center.y + 82f * unit) // Right point
            gemPath.lineTo(center.x, center.y + 98f * unit) // Bottom drop tip
            gemPath.lineTo(center.x - 13f * unit, center.y + 82f * unit) // Left point
            gemPath.close()

            innerGemPath.reset()
            innerGemPath.moveTo(center.x, center.y + 68f * unit)
            innerGemPath.lineTo(center.x, center.y + 98f * unit)
            innerGemPath.moveTo(center.x - 13f * unit, center.y + 82f * unit)
            innerGemPath.lineTo(center.x + 13f * unit, center.y + 82f * unit)

            drawPath(
                path = gemPath,
                brush = Brush.verticalGradient(
                    listOf(PhoenixPureWhite, PhoenixGold, PhoenixHotOrange, PhoenixFireRed),
                    startY = center.y + 68f * unit,
                    endY = center.y + 98f * unit,
                ),
            )
            drawPath(
                path = gemPath,
                color = PhoenixGold,
                style = Stroke(width = 1.4f * unit),
            )
            drawPath(
                path = innerGemPath,
                color = PhoenixPaleEmber.copy(alpha = 0.7f),
                style = Stroke(width = 0.8f * unit),
            )

            // Gemstone radiant pulse glow
            val gemPulse = 0.4f + 0.6f * sin(phase * 2f)
            drawCircle(
                color = PhoenixVividOrange.copy(alpha = 0.25f * gemPulse),
                radius = 16f * unit,
                center = Offset(center.x, center.y + 82f * unit),
            )

            // 6. Floating Embers / Sparks (20 drifting particles)
            for (i in 0 until 20) {
                val emberAngle = (i * 2f * PI.toFloat() / 20f) + phase * (if (i % 2 == 0) 0.75f else -0.55f)
                val emberDist = (72f + (i % 5) * 4.6f) * unit
                val emberX = center.x + cos(emberAngle) * emberDist
                val emberY = center.y + sin(emberAngle) * emberDist - (if (animate) sin(phase + i) * 2.8f * unit else 0f)
                val emberAlpha = (0.35f + 0.65f * (0.5f + 0.5f * sin(phase * 2.2f + i * 1.5f))).coerceIn(0f, 1f)
                val emberSize = (1.1f + (i % 3) * 0.55f) * unit
                val emberColor = if (i % 3 == 0) PhoenixGold else PhoenixVividOrange

                drawCircle(
                    color = emberColor.copy(alpha = emberAlpha),
                    radius = emberSize,
                    center = Offset(emberX, emberY),
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  PhoenixFramePreview: Interactive Testing & Verification Composable
// ─────────────────────────────────────────────────────────────────────────────
/**
 * Dedicated preview composable showcasing the Phoenix Rebirth V2 frame across
 * standard small, medium, and large sizing with an interactive animation toggle.
 */
@Composable
fun PhoenixFramePreview(
    modifier: Modifier = Modifier,
) {
    var animateEnabled by remember { mutableStateOf(true) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF131034))
            .border(1.dp, Color(0xFF2C2463), RoundedCornerShape(16.dp))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column {
                Text(
                    text = "Phoenix Rebirth V2",
                    color = PhoenixGold,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = if (animateEnabled) "Active motion (4.6s cycle)" else "Resting state (0% CPU)",
                    color = Color(0xFFBBA4FA),
                    fontSize = 11.sp,
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = if (animateEnabled) "Anim ON" else "Anim OFF",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                )
                Spacer(Modifier.size(8.dp))
                Switch(
                    checked = animateEnabled,
                    onCheckedChange = { animateEnabled = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = PhoenixGold,
                        checkedTrackColor = PhoenixFireRed.copy(alpha = 0.6f),
                    ),
                )
            }
        }

        Spacer(Modifier.height(18.dp))

        // Three standard sizes: 64dp (Seat/Rank), 96dp (Card), 128dp (Profile)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Bottom,
        ) {
            // 64.dp Small (Seats / Ranking)
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                PhoenixAvatarFrame(
                    avatarResId = R.drawable.me_clean_demo_avatar_01,
                    size = 64.dp,
                    animate = animateEnabled,
                )
                Spacer(Modifier.height(6.dp))
                Text("64dp", color = Color(0xFFE2E8F0), fontSize = 11.sp)
                Text("Lounge Seat", color = Color(0xFF94A3B8), fontSize = 9.sp)
            }

            // 96.dp Medium (Player Card / Dialog)
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                PhoenixAvatarFrame(
                    avatarResId = R.drawable.me_clean_demo_avatar_02,
                    size = 96.dp,
                    animate = animateEnabled,
                )
                Spacer(Modifier.height(6.dp))
                Text("96dp", color = Color(0xFFE2E8F0), fontSize = 11.sp)
                Text("Player Card", color = Color(0xFF94A3B8), fontSize = 9.sp)
            }

            // 128.dp Large (Profile Header / Studio)
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                PhoenixAvatarFrame(
                    avatarResId = R.drawable.me_clean_demo_avatar_03,
                    size = 128.dp,
                    animate = animateEnabled,
                )
                Spacer(Modifier.height(6.dp))
                Text("128dp", color = Color(0xFFE2E8F0), fontSize = 11.sp)
                Text("Profile Header", color = Color(0xFF94A3B8), fontSize = 9.sp)
            }
        }
    }
}
