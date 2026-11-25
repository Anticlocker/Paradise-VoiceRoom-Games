package com.sundram.weplay.voiceroom.theme

import androidx.compose.ui.graphics.Color
import com.sundram.weplay.R

/**
 * Canonical 10-theme starter catalog matching app/src/main/assets/room_theme_catalog.json
 * and the 50 imported drawable-nodpi theme assets.
 *
 * All resource bindings are compile-time R.drawable references to avoid runtime reflection.
 */
object RoomThemeCatalog {

    val DEFAULT_THEME_ID = CLASSIC_LOUNGE_ID

    val entries: List<RoomThemeSpec> = listOf(
        RoomThemeSpec(
            id = "classic_lounge",
            name = "Classic Lounge",
            requiredCrestLevel = 1,
            unlockType = ThemeUnlockType.FREE,
            defaultEquipped = true,
            priceCoins = null,
            accentHex = "#40345E",
            artworkStatus = "procedural_starter",
            backgroundAsset = "backgrounds/classic_lounge.webp",
            thumbnailAsset = "thumbnails/classic_lounge.webp",
            seatFrameAsset = "seat_frames/classic_lounge.png",
            hostFrameAsset = "host_frames/classic_lounge.png",
            overlayAsset = "transparent_overlays/classic_lounge.png",
        ),
        RoomThemeSpec(
            id = "ocean_lounge",
            name = "Ocean Lounge",
            requiredCrestLevel = 3,
            unlockType = ThemeUnlockType.CREST,
            defaultEquipped = false,
            priceCoins = null,
            accentHex = "#1599AC",
            artworkStatus = "procedural_starter",
            backgroundAsset = "backgrounds/ocean_lounge.webp",
            thumbnailAsset = "thumbnails/ocean_lounge.webp",
            seatFrameAsset = "seat_frames/ocean_lounge.png",
            hostFrameAsset = "host_frames/ocean_lounge.png",
            overlayAsset = "transparent_overlays/ocean_lounge.png",
        ),
        RoomThemeSpec(
            id = "velvet_lounge",
            name = "Velvet Lounge",
            requiredCrestLevel = 5,
            unlockType = ThemeUnlockType.CREST,
            defaultEquipped = false,
            priceCoins = null,
            accentHex = "#B16C96",
            artworkStatus = "procedural_starter",
            backgroundAsset = "backgrounds/velvet_lounge.webp",
            thumbnailAsset = "thumbnails/velvet_lounge.webp",
            seatFrameAsset = "seat_frames/velvet_lounge.png",
            hostFrameAsset = "host_frames/velvet_lounge.png",
            overlayAsset = "transparent_overlays/velvet_lounge.png",
        ),
        RoomThemeSpec(
            id = "royal_lounge",
            name = "Royal Lounge",
            requiredCrestLevel = 7,
            unlockType = ThemeUnlockType.CREST,
            defaultEquipped = false,
            priceCoins = null,
            accentHex = "#C6A259",
            artworkStatus = "procedural_starter",
            backgroundAsset = "backgrounds/royal_lounge.webp",
            thumbnailAsset = "thumbnails/royal_lounge.webp",
            seatFrameAsset = "seat_frames/royal_lounge.png",
            hostFrameAsset = "host_frames/royal_lounge.png",
            overlayAsset = "transparent_overlays/royal_lounge.png",
        ),
        RoomThemeSpec(
            id = "aurora_palace",
            name = "Aurora Palace",
            requiredCrestLevel = 9,
            unlockType = ThemeUnlockType.CREST,
            defaultEquipped = false,
            priceCoins = null,
            accentHex = "#9250C8",
            artworkStatus = "procedural_starter",
            backgroundAsset = "backgrounds/aurora_palace.webp",
            thumbnailAsset = "thumbnails/aurora_palace.webp",
            seatFrameAsset = "seat_frames/aurora_palace.png",
            hostFrameAsset = "host_frames/aurora_palace.png",
            overlayAsset = "transparent_overlays/aurora_palace.png",
        ),
        RoomThemeSpec(
            id = "celestial_kingdom",
            name = "Celestial Kingdom",
            requiredCrestLevel = 11,
            unlockType = ThemeUnlockType.CREST,
            defaultEquipped = false,
            priceCoins = null,
            accentHex = "#747FE7",
            artworkStatus = "procedural_starter",
            backgroundAsset = "backgrounds/celestial_kingdom.webp",
            thumbnailAsset = "thumbnails/celestial_kingdom.webp",
            seatFrameAsset = "seat_frames/celestial_kingdom.png",
            hostFrameAsset = "host_frames/celestial_kingdom.png",
            overlayAsset = "transparent_overlays/celestial_kingdom.png",
        ),
        RoomThemeSpec(
            id = "imperial_realm",
            name = "Imperial Realm",
            requiredCrestLevel = 16,
            unlockType = ThemeUnlockType.CREST,
            defaultEquipped = false,
            priceCoins = null,
            accentHex = "#A98946",
            artworkStatus = "procedural_starter",
            backgroundAsset = "backgrounds/imperial_realm.webp",
            thumbnailAsset = "thumbnails/imperial_realm.webp",
            seatFrameAsset = "seat_frames/imperial_realm.png",
            hostFrameAsset = "host_frames/imperial_realm.png",
            overlayAsset = "transparent_overlays/imperial_realm.png",
        ),
        RoomThemeSpec(
            id = "eternal_galaxy",
            name = "Eternal Galaxy",
            requiredCrestLevel = 21,
            unlockType = ThemeUnlockType.CREST,
            defaultEquipped = false,
            priceCoins = null,
            accentHex = "#5656D8",
            artworkStatus = "procedural_starter",
            backgroundAsset = "backgrounds/eternal_galaxy.webp",
            thumbnailAsset = "thumbnails/eternal_galaxy.webp",
            seatFrameAsset = "seat_frames/eternal_galaxy.png",
            hostFrameAsset = "host_frames/eternal_galaxy.png",
            overlayAsset = "transparent_overlays/eternal_galaxy.png",
        ),
        RoomThemeSpec(
            id = "sakura_garden",
            name = "Sakura Garden",
            requiredCrestLevel = null,
            unlockType = ThemeUnlockType.STORE,
            defaultEquipped = false,
            priceCoins = null,
            accentHex = "#F09AB9",
            artworkStatus = "procedural_starter",
            backgroundAsset = "backgrounds/sakura_garden.webp",
            thumbnailAsset = "thumbnails/sakura_garden.webp",
            seatFrameAsset = "seat_frames/sakura_garden.png",
            hostFrameAsset = "host_frames/sakura_garden.png",
            overlayAsset = "transparent_overlays/sakura_garden.png",
        ),
        RoomThemeSpec(
            id = "rose_palace",
            name = "Rose Palace",
            requiredCrestLevel = null,
            unlockType = ThemeUnlockType.STORE,
            defaultEquipped = false,
            priceCoins = null,
            accentHex = "#D6A287",
            artworkStatus = "procedural_starter",
            backgroundAsset = "backgrounds/rose_palace.webp",
            thumbnailAsset = "thumbnails/rose_palace.webp",
            seatFrameAsset = "seat_frames/rose_palace.png",
            hostFrameAsset = "host_frames/rose_palace.png",
            overlayAsset = "transparent_overlays/rose_palace.png",
        ),
    )

    private val byId: Map<String, RoomThemeSpec> = entries.associateBy { it.id }

    fun findById(themeId: String?): RoomThemeSpec =
        byId[themeId] ?: byId.getValue(CLASSIC_LOUNGE_ID)

    fun isKnownTheme(themeId: String?): Boolean =
        themeId != null && byId.containsKey(themeId)

    /**
     * Parses raw theme_catalog.json content in pure Kotlin (no org.json dependency so pure JVM tests run cleanly).
     */
    fun parseCatalogJson(rawJson: String): List<RoomThemeSpec> {
        val objectBlocks = Regex("\\{([^{}]*)\\}").findAll(rawJson)
        val fieldRegex = Regex("\"([^\"]+)\"\\s*:\\s*(\"([^\"]*)\"|null|true|false|-?\\d+)")
        return objectBlocks.map { block ->
            val fields = mutableMapOf<String, String?>()
            fieldRegex.findAll(block.groupValues[1]).forEach { m ->
                val key = m.groupValues[1]
                val rawVal = m.groupValues[2]
                fields[key] = when {
                    rawVal == "null" -> null
                    rawVal.startsWith("\"") -> m.groupValues[3]
                    else -> rawVal
                }
            }
            RoomThemeSpec(
                id = fields.getValue("id") ?: "",
                name = fields.getValue("name") ?: "",
                requiredCrestLevel = fields["requiredCrestLevel"]?.toIntOrNull(),
                unlockType = ThemeUnlockType.fromWire(fields["unlockType"]),
                defaultEquipped = fields["defaultEquipped"]?.toBooleanStrictOrNull() ?: false,
                priceCoins = fields["priceCoins"]?.toIntOrNull(),
                accentHex = fields["accent"] ?: "#40345E",
                artworkStatus = fields["artworkStatus"] ?: "procedural_starter",
                backgroundAsset = fields["background"] ?: "",
                thumbnailAsset = fields["thumbnail"] ?: "",
                seatFrameAsset = fields["seatFrame"] ?: "",
                hostFrameAsset = fields["hostFrame"] ?: "",
                overlayAsset = fields["overlay"] ?: "",
            )
        }.toList()
    }
}

object RoomThemeResourceRegistry {

    private val resourceMap: Map<String, RoomThemeResources> = mapOf(
        "classic_lounge" to RoomThemeResources(
            backgroundRes = R.drawable.room_bg_classic_lounge,
            thumbnailRes = R.drawable.room_thumb_classic_lounge,
            seatFrameRes = R.drawable.room_seat_classic_lounge,
            hostFrameRes = R.drawable.room_host_classic_lounge,
            overlayRes = R.drawable.room_overlay_classic_lounge,
        ),
        "ocean_lounge" to RoomThemeResources(
            backgroundRes = R.drawable.room_bg_ocean_lounge,
            thumbnailRes = R.drawable.room_thumb_ocean_lounge,
            seatFrameRes = R.drawable.room_seat_ocean_lounge,
            hostFrameRes = R.drawable.room_host_ocean_lounge,
            overlayRes = R.drawable.room_overlay_ocean_lounge,
        ),
        "velvet_lounge" to RoomThemeResources(
            backgroundRes = R.drawable.room_bg_velvet_lounge,
            thumbnailRes = R.drawable.room_thumb_velvet_lounge,
            seatFrameRes = R.drawable.room_seat_velvet_lounge,
            hostFrameRes = R.drawable.room_host_velvet_lounge,
            overlayRes = R.drawable.room_overlay_velvet_lounge,
        ),
        "royal_lounge" to RoomThemeResources(
            backgroundRes = R.drawable.room_bg_royal_lounge,
            thumbnailRes = R.drawable.room_thumb_royal_lounge,
            seatFrameRes = R.drawable.room_seat_royal_lounge,
            hostFrameRes = R.drawable.room_host_royal_lounge,
            overlayRes = R.drawable.room_overlay_royal_lounge,
        ),
        "aurora_palace" to RoomThemeResources(
            backgroundRes = R.drawable.room_bg_aurora_palace,
            thumbnailRes = R.drawable.room_thumb_aurora_palace,
            seatFrameRes = R.drawable.room_seat_aurora_palace,
            hostFrameRes = R.drawable.room_host_aurora_palace,
            overlayRes = R.drawable.room_overlay_aurora_palace,
        ),
        "celestial_kingdom" to RoomThemeResources(
            backgroundRes = R.drawable.room_bg_celestial_kingdom,
            thumbnailRes = R.drawable.room_thumb_celestial_kingdom,
            seatFrameRes = R.drawable.room_seat_celestial_kingdom,
            hostFrameRes = R.drawable.room_host_celestial_kingdom,
            overlayRes = R.drawable.room_overlay_celestial_kingdom,
        ),
        "imperial_realm" to RoomThemeResources(
            backgroundRes = R.drawable.room_bg_imperial_realm,
            thumbnailRes = R.drawable.room_thumb_imperial_realm,
            seatFrameRes = R.drawable.room_seat_imperial_realm,
            hostFrameRes = R.drawable.room_host_imperial_realm,
            overlayRes = R.drawable.room_overlay_imperial_realm,
        ),
        "eternal_galaxy" to RoomThemeResources(
            backgroundRes = R.drawable.room_bg_eternal_galaxy,
            thumbnailRes = R.drawable.room_thumb_eternal_galaxy,
            seatFrameRes = R.drawable.room_seat_eternal_galaxy,
            hostFrameRes = R.drawable.room_host_eternal_galaxy,
            overlayRes = R.drawable.room_overlay_eternal_galaxy,
        ),
        "sakura_garden" to RoomThemeResources(
            backgroundRes = R.drawable.room_bg_sakura_garden,
            thumbnailRes = R.drawable.room_thumb_sakura_garden,
            seatFrameRes = R.drawable.room_seat_sakura_garden,
            hostFrameRes = R.drawable.room_host_sakura_garden,
            overlayRes = R.drawable.room_overlay_sakura_garden,
        ),
        "rose_palace" to RoomThemeResources(
            backgroundRes = R.drawable.room_bg_rose_palace,
            thumbnailRes = R.drawable.room_thumb_rose_palace,
            seatFrameRes = R.drawable.room_seat_rose_palace,
            hostFrameRes = R.drawable.room_host_rose_palace,
            overlayRes = R.drawable.room_overlay_rose_palace,
        ),
    )

    fun resourcesFor(themeId: String?): RoomThemeResources =
        resourceMap[themeId] ?: resourceMap.getValue(CLASSIC_LOUNGE_ID)

    fun allRegisteredThemeIds(): Set<String> = resourceMap.keys

    fun paletteFor(themeId: String?): RoomThemePalette {
        val spec = RoomThemeCatalog.findById(themeId)
        return if (spec.isClassicDefault) {
            RoomThemePalette(
                backgroundTop = Color(0xFF19172D),
                backgroundBottom = Color(0xFF211B38),
                headerSurface = Color(0xCC241E3A),
                cardSurface = Color(0xB829233D),
                seatSurface = Color(0xFF29233D),
                seatBorder = Color(0xFF473E63),
                primaryText = Color(0xFFF7F4FF),
                secondaryText = Color(0xFFB8B0CC),
                accent = Color(0xFF9B82E9),
                activeMic = Color(0xFF42D6A0),
                mutedMic = Color(0xFFEF5578),
                bondLine = Color(0xFF9B82E9),
                usesRestrainedStyle = true,
            )
        } else {
            val accent = spec.accentColor
            RoomThemePalette(
                backgroundTop = Color(0xFF17142D),
                backgroundBottom = Color(0xFF1D1736),
                headerSurface = Color(0xB3221A40),
                cardSurface = Color(0xAA251C45),
                seatSurface = Color(0xBB271D48),
                seatBorder = accent.copy(alpha = 0.65f),
                primaryText = Color(0xFFF7F4FF),
                secondaryText = Color(0xFFD2CAE6),
                accent = accent,
                activeMic = Color(0xFF42D6A0),
                mutedMic = Color(0xFFEF5578),
                bondLine = accent,
                usesRestrainedStyle = false,
            )
        }
    }
}
