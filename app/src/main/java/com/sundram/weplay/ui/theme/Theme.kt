package com.sundram.weplay.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// Unified Aurora Paradise 3.0 dark palette so nested screens never render mixed light/dark chrome.
private val AuroraDarkPalette = WePlayPalette(
    page = AuroraBackground,
    bgSoft = Color(0xFF1A1242),
    bgSection = Color(0xFF231651),
    chipBg = AuroraElevated,
    card = AuroraSurface,
    divider = AuroraDivider,
    stroke = AuroraBorder,
    textMain = AuroraTextPrimary,
    textBody = Color(0xFFDCD0F5),
    textMuted = AuroraTextSecondary,
    overlaySoft = Color(0xCC100A2E),
    authSky = Color(0xFF170E3B),
    wallBlue = Color(0xFF1E1752),
    wallPurple = Color(0xFF2B1759),
    wallMint = Color(0xFF142A3E),
    accentInk = CrystalBlue,
)

private val LightPalette = AuroraDarkPalette

private val DarkPalette = AuroraDarkPalette

private val LightScheme = darkColorScheme(
    primary = RoyalViolet,
    onPrimary = CloudWhite,
    secondary = CrystalBlue,
    onSecondary = CelestialNavy,
    tertiary = RoyalGold,
    onTertiary = CelestialNavy,
    background = AuroraDarkPalette.page,
    onBackground = AuroraDarkPalette.textMain,
    surface = AuroraDarkPalette.card,
    onSurface = AuroraDarkPalette.textMain,
    surfaceVariant = AuroraDarkPalette.bgSection,
    onSurfaceVariant = AuroraDarkPalette.textBody,
    surfaceBright = AuroraDarkPalette.bgSoft,
    surfaceDim = AuroraDarkPalette.page,
    surfaceContainerLowest = AuroraDarkPalette.page,
    surfaceContainerLow = AuroraDarkPalette.bgSoft,
    surfaceContainer = AuroraDarkPalette.bgSection,
    surfaceContainerHigh = AuroraDarkPalette.bgSection,
    surfaceContainerHighest = AuroraDarkPalette.chipBg,
    outline = AuroraDarkPalette.stroke,
    outlineVariant = AuroraDarkPalette.divider,
    error = AuroraError,
)

private val DarkScheme = LightScheme

internal val LocalWePlayPalette = staticCompositionLocalOf { AuroraDarkPalette }

@Composable
fun WePlayTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    // Keep all screens consistently anchored to the Aurora Paradise 3.0 dark visual system.
    @Suppress("UNUSED_VARIABLE")
    val ignoredSystemTheme = darkTheme
    CompositionLocalProvider(
        LocalWePlayPalette provides AuroraDarkPalette,
        LocalAuroraColors provides AuroraColors(),
    ) {
        MaterialTheme(
            colorScheme = DarkScheme,
            typography = WePlayTypography,
            content = content,
        )
    }
}
