package com.sundram.weplay.ui.theme

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing

/**
 * Vybora / WePlay motion token system.
 *
 * Duration constants (milliseconds):
 *   Fast      = 120 ms  – micro-interactions (ripple reveal, icon swap)
 *   Mid       = 250 ms  – most UI transitions (tab switch, expand)
 *   Slow      = 400 ms  – modal entrance, page-level transitions
 *   Cinematic = 600 ms  – gift animations, hero entrance
 *
 * Easing:
 *   EaseOut = FastOutSlowInEasing – natural deceleration (default for most)
 *   EaseIn  = LinearOutSlowInEasing – acceleration (use for exits)
 *
 * Usage:
 *   animateFloatAsState(tween(Motion.Mid, easing = Motion.EaseOut))
 */
object Motion {
    const val Fast      = 120
    const val Mid       = 250
    const val Slow      = 400
    const val Cinematic = 600

    val EaseOut = FastOutSlowInEasing
    val EaseIn  = LinearOutSlowInEasing
}

