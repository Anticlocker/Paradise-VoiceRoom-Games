package com.sundram.weplay.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * Vybora / WePlay design token typography scale.
 *
 * Names follow the design system spec:
 *   Display  → 32 sp / ExtraBold  – hero text, big numbers
 *   Headline → 22 sp / Bold       – section headers
 *   Title    → 18 sp / SemiBold   – card titles, dialog headers
 *   Body     → 14 sp / Normal     – default body copy
 *   Label    → 12 sp / Medium     – chips, captions, nav labels
 *   Caption  → 11 sp / Normal     – timestamps, secondary metadata
 */
object VyboraTypography {
    val Display = TextStyle(fontSize = 32.sp, fontWeight = FontWeight.ExtraBold, lineHeight = 38.sp, letterSpacing = (-0.5).sp)
    val Headline = TextStyle(fontSize = 22.sp, fontWeight = FontWeight.Bold, lineHeight = 28.sp, letterSpacing = (-0.2).sp)
    val Title = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.SemiBold, lineHeight = 24.sp)
    val Body = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Normal, lineHeight = 20.sp)
    val Label = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Medium, lineHeight = 16.sp)
    val Caption = TextStyle(fontSize = 11.sp, fontWeight = FontWeight.Normal, lineHeight = 15.sp, letterSpacing = 0.1.sp)
}

/**
 * Material 3 Typography wired to the Vybora scale.
 * Passed to MaterialTheme(typography = WePlayTypography) in WePlayTheme.
 */
val WePlayTypography = Typography(
    displayLarge  = VyboraTypography.Display.copy(fontSize = 57.sp),
    displayMedium = VyboraTypography.Display.copy(fontSize = 45.sp),
    displaySmall  = VyboraTypography.Display,
    headlineLarge  = VyboraTypography.Headline.copy(fontSize = 26.sp),
    headlineMedium = VyboraTypography.Headline,
    headlineSmall  = VyboraTypography.Headline.copy(fontSize = 20.sp),
    titleLarge  = VyboraTypography.Title.copy(fontSize = 20.sp),
    titleMedium = VyboraTypography.Title,
    titleSmall  = VyboraTypography.Title.copy(fontSize = 16.sp),
    bodyLarge   = VyboraTypography.Body.copy(fontSize = 16.sp),
    bodyMedium  = VyboraTypography.Body,
    bodySmall   = VyboraTypography.Body.copy(fontSize = 12.sp),
    labelLarge  = VyboraTypography.Label.copy(fontSize = 14.sp),
    labelMedium = VyboraTypography.Label,
    labelSmall  = VyboraTypography.Caption,
)

