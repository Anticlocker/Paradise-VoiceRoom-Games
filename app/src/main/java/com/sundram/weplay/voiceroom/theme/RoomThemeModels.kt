package com.sundram.weplay.voiceroom.theme

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.Color

enum class ThemeUnlockType(val wireValue: String, val label: String) {
    FREE("free", "Free"),
    CREST("crest", "Crest"),
    STORE("store", "Store"),
    ;

    companion object {
        fun fromWire(value: String?): ThemeUnlockType = when (value?.lowercase()) {
            "free" -> FREE
            "crest" -> CREST
            "store" -> STORE
            else -> CREST
        }
    }
}

enum class RoomThemeTab(val title: String) {
    ALL("All Themes"),
    MY_THEMES("My Themes"),
    CREST_REWARDS("Crest Rewards"),
    STORE_EXCLUSIVES("Store Exclusives"),
}

enum class RoomCrestEventType(val label: String, val baseXp: Int) {
    HOST_SESSION_MINUTE("Host Session", 20),
    STAGE_SPEAKER_ACTIVITY("Speaker Stage Time", 15),
    GIFT_VALUE_RECEIVED("Room Gift Support", 30),
    MODERATOR_ACTIVITY("Host/Mod Activity", 10),
    MILESTONE_BONUS("Milestone Bonus", 50),
}

data class RoomThemeSpec(
    val id: String,
    val name: String,
    val requiredCrestLevel: Int?,
    val unlockType: ThemeUnlockType,
    val defaultEquipped: Boolean,
    val priceCoins: Int?,
    val accentHex: String,
    val artworkStatus: String,
    val backgroundAsset: String,
    val thumbnailAsset: String,
    val seatFrameAsset: String,
    val hostFrameAsset: String,
    val overlayAsset: String,
) {
    val isClassicDefault: Boolean get() = id == CLASSIC_LOUNGE_ID

    val accentColor: Color
        get() = parseHexColorOrDefault(accentHex, Color(0xFF40345E))
}

data class RoomThemeResources(
    @DrawableRes val backgroundRes: Int,
    @DrawableRes val thumbnailRes: Int,
    @DrawableRes val seatFrameRes: Int,
    @DrawableRes val hostFrameRes: Int,
    @DrawableRes val overlayRes: Int,
)

data class RoomThemePalette(
    val backgroundTop: Color,
    val backgroundBottom: Color,
    val headerSurface: Color,
    val cardSurface: Color,
    val seatSurface: Color,
    val seatBorder: Color,
    val primaryText: Color,
    val secondaryText: Color,
    val accent: Color,
    val activeMic: Color,
    val mutedMic: Color,
    val bondLine: Color,
    val usesRestrainedStyle: Boolean,
)

data class RoomThemeCatalogItem(
    val spec: RoomThemeSpec,
    val resources: RoomThemeResources,
    val isOwned: Boolean,
    val isUnlockedByCrest: Boolean,
    val isEquipped: Boolean,
    val lockReason: String?,
) {
    val canEquip: Boolean get() = isOwned || isUnlockedByCrest || spec.unlockType == ThemeUnlockType.FREE
    val isPurchaseConfigured: Boolean get() = spec.priceCoins != null && spec.priceCoins > 0
}

data class RoomCrestEventRecord(
    val id: String,
    val roomId: String,
    val type: RoomCrestEventType,
    val xpDelta: Int,
    val summary: String,
    val timestampMillis: Long,
    val accepted: Boolean,
    val rejectionReason: String? = null,
)

data class RoomCrestProgress(
    val roomId: String,
    val level: Int,
    val xp: Long,
    val currentLevelFloorXp: Long,
    val nextLevelXp: Long,
    val dailyXpEarned: Int,
    val totalHostMinutes: Int,
    val totalGiftsReceivedValue: Long,
    val completedMilestones: List<Int>,
    val recentEvents: List<RoomCrestEventRecord>,
) {
    val progressFraction: Float
        get() {
            val span = (nextLevelXp - currentLevelFloorXp).coerceAtLeast(1L)
            return ((xp - currentLevelFloorXp).coerceIn(0L, span)).toFloat() / span.toFloat()
        }
}

data class CreatedRoomRecord(
    val id: String,
    val title: String,
    val tag: String,
    val modeTitle: String,
    val hostName: String,
    val announcement: String,
    val isPrivate: Boolean,
    val hasPasscode: Boolean,
    val themeId: String = CLASSIC_LOUNGE_ID,
    val createdAtMillis: Long,
)

sealed interface ThemeActionResult {
    data class Success(val message: String, val themeId: String) : ThemeActionResult
    data class Locked(val reason: String, val requiredCrestLevel: Int? = null) : ThemeActionResult
    data class Unavailable(val reason: String) : ThemeActionResult
    data class InsufficientCoins(val requiredCoins: Int, val currentBalance: Long) : ThemeActionResult
    data class Unauthorized(val reason: String) : ThemeActionResult
}

const val CLASSIC_LOUNGE_ID = "classic_lounge"

fun parseHexColorOrDefault(hex: String, fallback: Color): Color {
    val cleaned = hex.trim().removePrefix("#")
    val argbLong = when (cleaned.length) {
        6 -> cleaned.toLongOrNull(16)?.or(0xFF000000L)
        8 -> cleaned.toLongOrNull(16)
        else -> null
    } ?: return fallback
    return Color(argbLong.toInt())
}
