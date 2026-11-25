package com.sundram.weplay.voiceroom.theme

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource

/**
 * Centralized theme backdrop renderer shared by BOTH LiveVoiceRoomScreen
 * and isolated RoomThemePreviewScreen.
 *
 * Rules enforced:
 * - Default Classic Lounge uses calm slate-purple backdrop with clean depth,
 *   no heavy palace artwork, and no continuous decorative particle animations.
 * - Unlocked non-classic themes render background + subtle low-alpha overlay
 *   (no double-rendered opaque arches or unreadable glare behind seats/chat).
 */
@Composable
fun RoomThemeRenderer(
    themeId: String,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.(RoomThemeSpec, RoomThemeResources, RoomThemePalette) -> Unit,
) {
    val safeThemeId = if (RoomThemeCatalog.isKnownTheme(themeId)) themeId else CLASSIC_LOUNGE_ID
    val spec = RoomThemeCatalog.findById(safeThemeId)
    val resources = RoomThemeResourceRegistry.resourcesFor(safeThemeId)
    val palette = RoomThemeResourceRegistry.paletteFor(safeThemeId)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(palette.backgroundTop, palette.backgroundBottom)
                )
            ),
    ) {
        Crossfade(
            targetState = safeThemeId,
            animationSpec = tween(durationMillis = 280),
            label = "room_theme_backdrop",
        ) { activeId ->
            val activeSpec = RoomThemeCatalog.findById(activeId)
            val activeRes = RoomThemeResourceRegistry.resourcesFor(activeId)

            Box(modifier = Modifier.fillMaxSize()) {
                Image(
                    painter = painterResource(activeRes.backgroundRes),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                )

                // Classic Lounge stays understated without extra decorative overlay layers.
                if (!activeSpec.isClassicDefault) {
                    Image(
                        painter = painterResource(activeRes.overlayRes),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxSize()
                            .alpha(0.24f),
                        contentScale = ContentScale.Crop,
                    )
                }

                // Subtle contrast vignette so header, seats, and chat stay crisp in all themes.
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colorStops = arrayOf(
                                    0.0f to Color(0x55120F24),
                                    0.42f to Color(0x1E120F24),
                                    0.72f to Color(0x45120F24),
                                    1.0f to Color(0x88120F24),
                                )
                            )
                        )
                )
            }
        }

        content(spec, resources, palette)
    }
}
