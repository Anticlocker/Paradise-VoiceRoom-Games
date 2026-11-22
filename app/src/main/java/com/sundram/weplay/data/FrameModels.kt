package com.sundram.weplay.data

import com.sundram.weplay.R
import com.sundram.weplay.ui.nav.Routes

/**
 * 6 cosmetic frame tiers defined for the profile frame engine.
 * Animation timings conform to docs/FRAME_ANIMATION_SPEC.md:
 * - COMMON: static (0ms)
 * - RARE: 2200ms alpha pulse (0.65f..1.0f)
 * - EPIC: 4200ms clockwise rotating particles (8 orbit dots)
 * - LEGENDARY: 5600ms counter-rotating dual orbital bands with 12 sparkling stars
 * - MYTHIC: 6400ms floating crown + 3 orbiting jewels
 * - CELESTIAL: 8000ms sweep aurora gradient + orbiting gems
 */
enum class FrameTier(
    val displayName: String,
    val durationMs: Int,
    val primaryColor: Long,
    val secondaryColor: Long,
    val accentColor: Long,
    val particleCount: Int,
    val ringDrawableRes: Int,
    val podiumDrawableRes: Int,
    val crestDrawableRes: Int,
    val shopPreviewDrawableRes: Int,
) {
    COMMON(
        displayName = "Common",
        durationMs = 0,
        primaryColor = 0xFF7DDDE4,
        secondaryColor = 0xFFB8F6F5,
        accentColor = 0xFFFFFFFF,
        particleCount = 0,
        ringDrawableRes = R.drawable.lb_common_ring,
        podiumDrawableRes = R.drawable.lb_common_podium,
        crestDrawableRes = R.drawable.lb_common_crest,
        shopPreviewDrawableRes = R.drawable.lb_common_shop_preview,
    ),
    RARE(
        displayName = "Rare",
        durationMs = 2200,
        primaryColor = 0xFF3DBBEB,
        secondaryColor = 0xFFA4E9FF,
        accentColor = 0xFFFFFFFF,
        particleCount = 0,
        ringDrawableRes = R.drawable.lb_rare_ring,
        podiumDrawableRes = R.drawable.lb_rare_podium,
        crestDrawableRes = R.drawable.lb_rare_crest,
        shopPreviewDrawableRes = R.drawable.lb_rare_shop_preview,
    ),
    EPIC(
        displayName = "Epic",
        durationMs = 4200,
        primaryColor = 0xFF9E72F5,
        secondaryColor = 0xFFE6C6FF,
        accentColor = 0xFFFFFFFF,
        particleCount = 8,
        ringDrawableRes = R.drawable.lb_epic_ring,
        podiumDrawableRes = R.drawable.lb_epic_podium,
        crestDrawableRes = R.drawable.lb_epic_crest,
        shopPreviewDrawableRes = R.drawable.lb_epic_shop_preview,
    ),
    LEGENDARY(
        displayName = "Legendary",
        durationMs = 5600,
        primaryColor = 0xFFEAB34C,
        secondaryColor = 0xFFFFF1AE,
        accentColor = 0xFFFFFFFF,
        particleCount = 12,
        ringDrawableRes = R.drawable.lb_legendary_ring,
        podiumDrawableRes = R.drawable.lb_legendary_podium,
        crestDrawableRes = R.drawable.lb_legendary_crest,
        shopPreviewDrawableRes = R.drawable.lb_legendary_shop_preview,
    ),
    MYTHIC(
        displayName = "Mythic",
        durationMs = 6400,
        primaryColor = 0xFFF18BC8,
        secondaryColor = 0xFFFFE1A6,
        accentColor = 0xFFFFFFFF,
        particleCount = 3,
        ringDrawableRes = R.drawable.lb_mythic_ring,
        podiumDrawableRes = R.drawable.lb_mythic_podium,
        crestDrawableRes = R.drawable.lb_mythic_crest,
        shopPreviewDrawableRes = R.drawable.lb_mythic_shop_preview,
    ),
    CELESTIAL(
        displayName = "Celestial",
        durationMs = 8000,
        primaryColor = 0xFF52CFC7,
        secondaryColor = 0xFFB59CFF,
        accentColor = 0xFFFFFFFF,
        particleCount = 6,
        ringDrawableRes = R.drawable.lb_celestial_ring,
        podiumDrawableRes = R.drawable.lb_celestial_podium,
        crestDrawableRes = R.drawable.lb_celestial_crest,
        shopPreviewDrawableRes = R.drawable.lb_celestial_shop_preview,
    );

    companion object {
        fun fromId(tierId: String?): FrameTier =
            values().firstOrNull { it.name.equals(tierId, ignoreCase = true) } ?: COMMON
    }
}

enum class FrameState {
    LOCKED,
    AVAILABLE,
    OWNED,
    EQUIPPED,
    EXPIRED,
}

enum class FrameUnlockSource(val label: String) {
    DEFAULT("Starter"),
    ACCOUNT_LEVEL("Level Unlock"),
    ACHIEVEMENT("Achievement"),
    VIP_BENEFIT("VIP Privilege"),
    SHOP_COINS("Coins Shop"),
    FAMILY_REWARDS("Family Rewards"),
    EVENT("Special Event"),
}

data class FrameDefinition(
    val id: String,
    val name: String,
    val tier: FrameTier,
    val description: String,
    val unlockSource: FrameUnlockSource,
    val minLevel: Int = 1,
    val priceCoins: Long = 0L,
    val durationDays: Int = 0, // 0 = permanent
    val isShopPurchasable: Boolean = false,
    val isEquippableByDefault: Boolean = false,
) {
    val ringDrawableRes: Int get() = tier.ringDrawableRes
    val podiumDrawableRes: Int get() = tier.podiumDrawableRes
    val crestDrawableRes: Int get() = tier.crestDrawableRes
    val shopPreviewDrawableRes: Int get() = tier.shopPreviewDrawableRes
}

data class UserFrameEntitlement(
    val userId: String,
    val frameId: String,
    val source: FrameUnlockSource,
    val grantedAt: Long = System.currentTimeMillis(),
    val expiresAt: Long? = null,
    val equippedAt: Long? = null,
) {
    val isExpired: Boolean get() = expiresAt != null && System.currentTimeMillis() > expiresAt
}

enum class LeaderboardCategory(
    val id: String,
    val title: String,
    val subtitle: String,
    val bannerDrawableRes: Int,
    val scoreUnit: String,
    val supportedPeriods: List<LeaderboardPeriod>,
    val ctaLabel: String,
    val ctaRoute: String,
    val iconGlyph: String,
    val primaryColor: Long,
    val bannerBgColor: Long,
) {
    POPULARITY(
        id = "popularity",
        title = "Popularity",
        subtitle = "Most admired stars",
        bannerDrawableRes = R.drawable.lb_category_popularity_banner,
        scoreUnit = "Charm",
        supportedPeriods = listOf(
            LeaderboardPeriod.TODAY,
            LeaderboardPeriod.YESTERDAY,
            LeaderboardPeriod.WEEKLY,
            LeaderboardPeriod.TOTAL,
        ),
        ctaLabel = "Send Gifts",
        ctaRoute = Routes.HUB,
        iconGlyph = "💖",
        primaryColor = 0xFFF3A3C7,
        bannerBgColor = 0xFFFFDBEA,
    ),
    VIP(
        id = "vip",
        title = "VIP",
        subtitle = "Prestigious members",
        bannerDrawableRes = R.drawable.lb_category_vip_banner,
        scoreUnit = "VIP Pts",
        supportedPeriods = listOf(
            LeaderboardPeriod.TODAY,
            LeaderboardPeriod.WEEKLY,
            LeaderboardPeriod.TOTAL,
            LeaderboardPeriod.CELEBRITY,
        ),
        ctaLabel = "VIP Center",
        ctaRoute = Routes.TOPUP,
        iconGlyph = "👑",
        primaryColor = 0xFFDDA94F,
        bannerBgColor = 0xFFFFF0C4,
    ),
    COUPLE(
        id = "couple",
        title = "Couple",
        subtitle = "Closest bond partners",
        bannerDrawableRes = R.drawable.lb_category_couple_banner,
        scoreUnit = "Intimacy",
        supportedPeriods = listOf(
            LeaderboardPeriod.TODAY,
            LeaderboardPeriod.WEEKLY,
            LeaderboardPeriod.TOTAL,
            LeaderboardPeriod.ANNUAL,
        ),
        ctaLabel = "Propose Bond",
        ctaRoute = Routes.ROOM,
        iconGlyph = "💍",
        primaryColor = 0xFFC77CDB,
        bannerBgColor = 0xFFF9DAFF,
    ),
    ROOM(
        id = "room",
        title = "Room",
        subtitle = "Hottest voice lounges",
        bannerDrawableRes = R.drawable.lb_category_room_banner,
        scoreUnit = "Heat",
        supportedPeriods = listOf(
            LeaderboardPeriod.TODAY,
            LeaderboardPeriod.YESTERDAY,
            LeaderboardPeriod.WEEKLY,
            LeaderboardPeriod.TOTAL,
        ),
        ctaLabel = "Enter Lounge",
        ctaRoute = Routes.ROOM,
        iconGlyph = "🎙️",
        primaryColor = 0xFF61B8E8,
        bannerBgColor = 0xFFD9F1FF,
    ),
    BFF(
        id = "bff",
        title = "BFF",
        subtitle = "Inseparable friends",
        bannerDrawableRes = R.drawable.lb_category_bff_banner,
        scoreUnit = "Bond",
        supportedPeriods = listOf(
            LeaderboardPeriod.TODAY,
            LeaderboardPeriod.WEEKLY,
            LeaderboardPeriod.TOTAL,
        ),
        ctaLabel = "Chat Friends",
        ctaRoute = Routes.MSG,
        iconGlyph = "🤝",
        primaryColor = 0xFF9875E4,
        bannerBgColor = 0xFFE8DDFF,
    ),
    FAMILY(
        id = "family",
        title = "Family",
        subtitle = "Top active dynasties",
        bannerDrawableRes = R.drawable.lb_category_family_banner,
        scoreUnit = "Activeness",
        supportedPeriods = listOf(
            LeaderboardPeriod.WEEKLY,
            LeaderboardPeriod.TOTAL,
            LeaderboardPeriod.GLOBAL,
        ),
        ctaLabel = "Family Hub",
        ctaRoute = Routes.FAMILY,
        iconGlyph = "🛡️",
        primaryColor = 0xFF44BCA4,
        bannerBgColor = 0xFFD8FFF2,
    );

    companion object {
        fun fromId(catId: String?): LeaderboardCategory =
            values().firstOrNull { it.id.equals(catId, ignoreCase = true) || it.name.equals(catId, ignoreCase = true) }
                ?: POPULARITY
    }
}

enum class LeaderboardPeriod(val id: String, val displayName: String) {
    TODAY("today", "Today"),
    YESTERDAY("yesterday", "Yesterday"),
    WEEKLY("weekly", "Weekly"),
    TOTAL("total", "Total"),
    CELEBRITY("celebrity", "Celebrity"),
    ANNUAL("annual", "Annual"),
    GLOBAL("global", "Global");

    companion object {
        fun fromId(periodId: String?): LeaderboardPeriod =
            values().firstOrNull { it.id.equals(periodId, ignoreCase = true) || it.name.equals(periodId, ignoreCase = true) }
                ?: TODAY
    }
}

data class RankingEntry(
    val rank: Int,
    val id: String,
    val name: String,
    val avatarPath: String? = null,
    val score: Long,
    val scoreFormatted: String,
    val verified: Boolean = false,
    val level: Int = 1,
    val frameId: String? = null,
    val subtitle: String? = null,
    val partnerName: String? = null,
    val partnerAvatar: String? = null,
    val partnerFrameId: String? = null,
    val memberCount: Int? = null,
    val tag: String? = null,
)

data class CurrentUserRank(
    val rank: Int?, // null if unranked
    val score: Long,
    val scoreFormatted: String,
    val percentile: String? = null,
    val category: LeaderboardCategory,
    val ctaLabel: String,
    val ctaRoute: String,
)

data class RankingRuleInfo(
    val category: LeaderboardCategory,
    val title: String,
    val summary: String,
    val calculationRules: List<String>,
    val resetSchedule: String,
    val antiCheatNote: String,
)
