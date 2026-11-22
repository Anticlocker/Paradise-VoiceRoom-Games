package com.sundram.weplay.data

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.NumberFormat
import java.util.Locale

/**
 * Repository responsible for Profile Frame catalog, user entitlements,
 * equipment state, and category-filtered Leaderboard data feeds.
 */
class FrameRepository(
    private val context: Context? = null,
) {
    private val prefs: SharedPreferences? =
        context?.getSharedPreferences("weplay_frames_prefs", Context.MODE_PRIVATE)

    // Master Catalog of Frames across all 6 tiers
    val catalog: List<FrameDefinition> = listOf(
        FrameDefinition(
            id = "frame_common_aurora",
            name = "Aurora Breeze",
            tier = FrameTier.COMMON,
            description = "Clean light cyan starter ring with soft dual-tone border.",
            unlockSource = FrameUnlockSource.DEFAULT,
            minLevel = 1,
            priceCoins = 0L,
            durationDays = 0,
            isShopPurchasable = false,
            isEquippableByDefault = true,
        ),
        FrameDefinition(
            id = "frame_rare_cyan_pulse",
            name = "Cyan Pulse",
            tier = FrameTier.RARE,
            description = "2200ms breathing cyan aura with radial ambient glow.",
            unlockSource = FrameUnlockSource.ACCOUNT_LEVEL,
            minLevel = 5,
            priceCoins = 0L,
            durationDays = 0,
            isShopPurchasable = false,
        ),
        FrameDefinition(
            id = "frame_rare_azure_crest",
            name = "Azure Crest",
            tier = FrameTier.RARE,
            description = "Sky blue pulsing ring with bright starlight accents.",
            unlockSource = FrameUnlockSource.SHOP_COINS,
            minLevel = 1,
            priceCoins = 600L,
            durationDays = 30,
            isShopPurchasable = true,
        ),
        FrameDefinition(
            id = "frame_epic_violet_orbit",
            name = "Violet Orbit",
            tier = FrameTier.EPIC,
            description = "4200ms clockwise rotating particles (8 orbit dots) on royal violet ring.",
            unlockSource = FrameUnlockSource.ACHIEVEMENT,
            minLevel = 10,
            priceCoins = 0L,
            durationDays = 0,
            isShopPurchasable = false,
        ),
        FrameDefinition(
            id = "frame_epic_amethyst_ring",
            name = "Amethyst Spark",
            tier = FrameTier.EPIC,
            description = "Rotating lilac particle stream with electric violet aura.",
            unlockSource = FrameUnlockSource.SHOP_COINS,
            minLevel = 1,
            priceCoins = 1800L,
            durationDays = 30,
            isShopPurchasable = true,
        ),
        FrameDefinition(
            id = "frame_legendary_golden_sun",
            name = "Solar Monarch",
            tier = FrameTier.LEGENDARY,
            description = "5600ms counter-rotating dual orbital bands with 12 sparkling stars.",
            unlockSource = FrameUnlockSource.VIP_BENEFIT,
            minLevel = 15,
            priceCoins = 0L,
            durationDays = 0,
            isShopPurchasable = false,
        ),
        FrameDefinition(
            id = "frame_legendary_amber_crown",
            name = "Amber Nova",
            tier = FrameTier.LEGENDARY,
            description = "Dual counter-rotating golden starbands for prestigious leaders.",
            unlockSource = FrameUnlockSource.SHOP_COINS,
            minLevel = 1,
            priceCoins = 4500L,
            durationDays = 30,
            isShopPurchasable = true,
        ),
        FrameDefinition(
            id = "frame_mythic_radiant_crown",
            name = "Radiant Sovereign",
            tier = FrameTier.MYTHIC,
            description = "6400ms slow floating gold crown with 3 orbiting jewels.",
            unlockSource = FrameUnlockSource.FAMILY_REWARDS,
            minLevel = 20,
            priceCoins = 0L,
            durationDays = 0,
            isShopPurchasable = false,
        ),
        FrameDefinition(
            id = "frame_mythic_rose_jewel",
            name = "Rose Empress",
            tier = FrameTier.MYTHIC,
            description = "Imperial rose gold floating tiara and ruby star orbiting jewels.",
            unlockSource = FrameUnlockSource.SHOP_COINS,
            minLevel = 1,
            priceCoins = 9000L,
            durationDays = 30,
            isShopPurchasable = true,
        ),
        FrameDefinition(
            id = "frame_celestial_aurora_stream",
            name = "Celestial Aurora",
            tier = FrameTier.CELESTIAL,
            description = "8000ms sweep aurora gradient, radiant rays, and celestial gems.",
            unlockSource = FrameUnlockSource.EVENT,
            minLevel = 25,
            priceCoins = 0L,
            durationDays = 0,
            isShopPurchasable = false,
        ),
        FrameDefinition(
            id = "frame_celestial_nebula_god",
            name = "Nebula Paragon",
            tier = FrameTier.CELESTIAL,
            description = "Full-spectrum rainbow cosmic aurora with orbiting stardust gems.",
            unlockSource = FrameUnlockSource.SHOP_COINS,
            minLevel = 1,
            priceCoins = 18000L,
            durationDays = 30,
            isShopPurchasable = true,
        ),
    )

    private val _ownedFrameIds = MutableStateFlow<Set<String>>(loadOwnedFrames())
    val ownedFrameIds: StateFlow<Set<String>> = _ownedFrameIds.asStateFlow()

    private val _equippedFrameId = MutableStateFlow<String?>(loadEquippedFrame())
    val equippedFrameId: StateFlow<String?> = _equippedFrameId.asStateFlow()

    private fun loadOwnedFrames(): Set<String> {
        val stored = prefs?.getStringSet("owned_frames", null)
        val defaultOwned = mutableSetOf("frame_common_aurora")
        if (stored != null) {
            defaultOwned.addAll(stored)
        }
        return defaultOwned
    }

    private fun loadEquippedFrame(): String? {
        return prefs?.getString("equipped_frame_id", "frame_common_aurora") ?: "frame_common_aurora"
    }

    private fun saveOwnedFrames(frames: Set<String>) {
        prefs?.edit()?.putStringSet("owned_frames", frames)?.apply()
        _ownedFrameIds.value = frames
    }

    private fun saveEquippedFrame(frameId: String?) {
        prefs?.edit()?.putString("equipped_frame_id", frameId)?.apply()
        _equippedFrameId.value = frameId
    }

    fun getFrameById(frameId: String?): FrameDefinition? {
        if (frameId.isNullOrBlank()) return null
        return catalog.firstOrNull { it.id.equals(frameId, ignoreCase = true) }
    }

    /**
     * Determines whether the user owns or is eligible to equip a frame
     * based on level, purchase, or defaults.
     */
    fun isFrameUnlocked(frame: FrameDefinition, userLevel: Int): Boolean {
        if (frame.isEquippableByDefault) return true
        if (_ownedFrameIds.value.contains(frame.id)) return true
        if (frame.unlockSource == FrameUnlockSource.ACCOUNT_LEVEL && userLevel >= frame.minLevel) return true
        return false
    }

    /**
     * Equips a frame if the user has unlocked it.
     */
    fun equipFrame(
        frameId: String,
        userLevel: Int,
        profile: UserProfile? = null,
        onProfileUpdate: ((UserProfile) -> Unit)? = null,
    ): Boolean {
        val frame = getFrameById(frameId) ?: return false
        if (!isFrameUnlocked(frame, userLevel)) return false

        saveEquippedFrame(frame.id)
        if (profile != null && onProfileUpdate != null) {
            val updated = profile.copy(frame = frame.id)
            onProfileUpdate(updated)
        }
        return true
    }

    /**
     * Unequips the currently active frame.
     */
    fun unequipFrame(
        profile: UserProfile? = null,
        onProfileUpdate: ((UserProfile) -> Unit)? = null,
    ) {
        saveEquippedFrame(null)
        if (profile != null && onProfileUpdate != null) {
            val updated = profile.copy(frame = null)
            onProfileUpdate(updated)
        }
    }

    /**
     * Purchases a shop frame using coins. Deducts coins, adds to owned set,
     * auto-equips, and updates Profile.
     */
    fun purchaseFrame(
        frameId: String,
        currentCoins: Long,
        userLevel: Int,
        profile: UserProfile? = null,
        onCoinsDeducted: ((Long) -> Unit)? = null,
        onProfileUpdate: ((UserProfile) -> Unit)? = null,
    ): Result<FrameDefinition> {
        val frame = getFrameById(frameId) ?: return Result.failure(IllegalArgumentException("Frame not found"))
        if (!frame.isShopPurchasable) {
            return Result.failure(IllegalStateException("Frame is not available for purchase"))
        }
        if (isFrameUnlocked(frame, userLevel)) {
            return Result.failure(IllegalStateException("You already own this frame"))
        }
        if (currentCoins < frame.priceCoins) {
            return Result.failure(IllegalStateException("Insufficient coins (requires ${frame.priceCoins})"))
        }

        // Deduct coins
        val newCoinBalance = currentCoins - frame.priceCoins
        onCoinsDeducted?.invoke(newCoinBalance)

        // Add to owned frames
        val updatedOwned = _ownedFrameIds.value.toMutableSet()
        updatedOwned.add(frame.id)
        saveOwnedFrames(updatedOwned)

        // Auto-equip
        saveEquippedFrame(frame.id)

        if (profile != null && onProfileUpdate != null) {
            val updated = profile.copy(coins = newCoinBalance, frame = frame.id)
            onProfileUpdate(updated)
        }

        return Result.success(frame)
    }

    /**
     * Formats integer/long scores with comma grouping (e.g. 834,027).
     */
    fun formatScore(score: Long): String =
        NumberFormat.getNumberInstance(Locale.US).format(score)

    /**
     * Returns the ranking entries and current user summary for any category and period.
     */
    fun getLeaderboard(
        category: LeaderboardCategory,
        period: LeaderboardPeriod,
        currentUser: UserProfile? = null,
    ): Pair<List<RankingEntry>, CurrentUserRank> {
        val multiplier = when (period) {
            LeaderboardPeriod.TODAY -> 1.0
            LeaderboardPeriod.YESTERDAY -> 0.92
            LeaderboardPeriod.WEEKLY -> 4.8
            LeaderboardPeriod.TOTAL -> 14.5
            LeaderboardPeriod.CELEBRITY -> 18.0
            LeaderboardPeriod.ANNUAL -> 52.0
            LeaderboardPeriod.GLOBAL -> 65.0
        }

        val entries: List<RankingEntry> = when (category) {
            LeaderboardCategory.POPULARITY -> listOf(
                RankingEntry(1, "u_01", "AuraQueen", null, (834027 * multiplier).toLong(), formatScore((834027 * multiplier).toLong()), true, 28, "frame_celestial_aurora_stream"),
                RankingEntry(2, "u_02", "Kush Boy", null, (667288 * multiplier).toLong(), formatScore((667288 * multiplier).toLong()), true, 22, "frame_legendary_golden_sun"),
                RankingEntry(3, "u_03", "Nexus Prime", null, (588424 * multiplier).toLong(), formatScore((588424 * multiplier).toLong()), false, 19, "frame_epic_violet_orbit"),
                RankingEntry(4, "u_04", "HiTension", null, (416822 * multiplier).toLong(), formatScore((416822 * multiplier).toLong()), true, 16, "frame_rare_cyan_pulse"),
                RankingEntry(5, "u_05", "Zulfiqar", null, (377365 * multiplier).toLong(), formatScore((377365 * multiplier).toLong()), false, 14, "frame_rare_azure_crest"),
                RankingEntry(6, "u_06", "Ghan", null, (373752 * multiplier).toLong(), formatScore((373752 * multiplier).toLong()), true, 13, "frame_epic_amethyst_ring"),
                RankingEntry(7, "u_07", "Mr. Santy", null, (364403 * multiplier).toLong(), formatScore((364403 * multiplier).toLong()), false, 12, "frame_common_aurora"),
                RankingEntry(8, "u_08", "Jazzy Beat", null, (339240 * multiplier).toLong(), formatScore((339240 * multiplier).toLong()), false, 11, "frame_common_aurora"),
                RankingEntry(9, "u_09", "Solace Wind", null, (303194 * multiplier).toLong(), formatScore((303194 * multiplier).toLong()), true, 10, "frame_epic_violet_orbit"),
                RankingEntry(10, "u_10", "Cosmic Ray", null, (288410 * multiplier).toLong(), formatScore((288410 * multiplier).toLong()), false, 9, "frame_rare_cyan_pulse"),
            )
            LeaderboardCategory.VIP -> listOf(
                RankingEntry(1, "v_01", "Lord Valerius", null, (1250000 * multiplier).toLong(), formatScore((1250000 * multiplier).toLong()), true, 30, "frame_celestial_nebula_god", subtitle = "VIP 10 Sovereign"),
                RankingEntry(2, "v_02", "Empress Lyra", null, (980000 * multiplier).toLong(), formatScore((980000 * multiplier).toLong()), true, 26, "frame_mythic_rose_jewel", subtitle = "VIP 9 Archon"),
                RankingEntry(3, "v_03", "GoldBaron", null, (740000 * multiplier).toLong(), formatScore((740000 * multiplier).toLong()), true, 24, "frame_legendary_amber_crown", subtitle = "VIP 8 Duke"),
                RankingEntry(4, "v_04", "DiamondKnight", null, (560000 * multiplier).toLong(), formatScore((560000 * multiplier).toLong()), true, 20, "frame_legendary_golden_sun", subtitle = "VIP 7 Marquis"),
                RankingEntry(5, "v_05", "SterlingStar", null, (420000 * multiplier).toLong(), formatScore((420000 * multiplier).toLong()), false, 18, "frame_epic_violet_orbit", subtitle = "VIP 6 Earl"),
                RankingEntry(6, "v_06", "VelvetRose", null, (310000 * multiplier).toLong(), formatScore((310000 * multiplier).toLong()), false, 15, "frame_epic_amethyst_ring", subtitle = "VIP 5 Baron"),
                RankingEntry(7, "v_07", "ApexCrown", null, (245000 * multiplier).toLong(), formatScore((245000 * multiplier).toLong()), true, 13, "frame_rare_azure_crest", subtitle = "VIP 4 Knight"),
                RankingEntry(8, "v_08", "SilkenShadow", null, (190000 * multiplier).toLong(), formatScore((190000 * multiplier).toLong()), false, 11, "frame_rare_cyan_pulse", subtitle = "VIP 3 Noble"),
            )
            LeaderboardCategory.COUPLE -> listOf(
                RankingEntry(1, "cp_01", "Romeo & Juliet", null, (920400 * multiplier).toLong(), formatScore((920400 * multiplier).toLong()), true, 25, "frame_celestial_aurora_stream", subtitle = "Eternal Soulmates (365d)", partnerName = "Juliet Star"),
                RankingEntry(2, "cp_02", "Leo & Maya", null, (780150 * multiplier).toLong(), formatScore((780150 * multiplier).toLong()), true, 22, "frame_mythic_radiant_crown", subtitle = "Golden Devotion (280d)", partnerName = "Maya Sun"),
                RankingEntry(3, "cp_03", "Kai & Chloe", null, (640220 * multiplier).toLong(), formatScore((640220 * multiplier).toLong()), false, 19, "frame_legendary_golden_sun", subtitle = "Twin Flames (194d)", partnerName = "Chloe Moon"),
                RankingEntry(4, "cp_04", "Ash & Serena", null, (490000 * multiplier).toLong(), formatScore((490000 * multiplier).toLong()), true, 17, "frame_epic_violet_orbit", subtitle = "Sweet Harmony (120d)", partnerName = "Serena Song"),
                RankingEntry(5, "cp_05", "Jack & Rose", null, (360500 * multiplier).toLong(), formatScore((360500 * multiplier).toLong()), false, 14, "frame_rare_cyan_pulse", subtitle = "True Companions (88d)", partnerName = "Rose Dew"),
                RankingEntry(6, "cp_06", "Noah & Emma", null, (280000 * multiplier).toLong(), formatScore((280000 * multiplier).toLong()), false, 12, "frame_common_aurora", subtitle = "First Promise (42d)", partnerName = "Emma Grace"),
            )
            LeaderboardCategory.ROOM -> listOf(
                RankingEntry(1, "rm_01", "Aurora Celestial Beats", null, (1450000 * multiplier).toLong(), formatScore((1450000 * multiplier).toLong()), true, 29, "frame_celestial_nebula_god", subtitle = "Host: DJ Hyperion • 980 listening"),
                RankingEntry(2, "rm_02", "Midnight Chill Cafe", null, (1120000 * multiplier).toLong(), formatScore((1120000 * multiplier).toLong()), true, 25, "frame_mythic_rose_jewel", subtitle = "Host: Luna Melody • 740 listening"),
                RankingEntry(3, "rm_03", "K-Pop & Anime Party", null, (890000 * multiplier).toLong(), formatScore((890000 * multiplier).toLong()), false, 21, "frame_legendary_golden_sun", subtitle = "Host: Ren Tanaka • 560 listening"),
                RankingEntry(4, "rm_04", "Acoustic Lounge Live", null, (640000 * multiplier).toLong(), formatScore((640000 * multiplier).toLong()), true, 18, "frame_epic_violet_orbit", subtitle = "Host: Oliver Strings • 410 listening"),
                RankingEntry(5, "rm_05", "Late Night Confessions", null, (480000 * multiplier).toLong(), formatScore((480000 * multiplier).toLong()), false, 15, "frame_rare_azure_crest", subtitle = "Host: Elena Whisper • 320 listening"),
                RankingEntry(6, "rm_06", "Gaming Squad Talks", null, (350000 * multiplier).toLong(), formatScore((350000 * multiplier).toLong()), false, 12, "frame_common_aurora", subtitle = "Host: Titan Bro • 210 listening"),
            )
            LeaderboardCategory.BFF -> listOf(
                RankingEntry(1, "bff_01", "Alex & Sam", null, (650000 * multiplier).toLong(), formatScore((650000 * multiplier).toLong()), true, 22, "frame_legendary_golden_sun", subtitle = "Besties Forever (480d)"),
                RankingEntry(2, "bff_02", "Mia & Lily", null, (520000 * multiplier).toLong(), formatScore((520000 * multiplier).toLong()), true, 20, "frame_epic_violet_orbit", subtitle = "Kindred Spirits (310d)"),
                RankingEntry(3, "bff_03", "David & Lucas", null, (410000 * multiplier).toLong(), formatScore((410000 * multiplier).toLong()), false, 18, "frame_epic_amethyst_ring", subtitle = "Dynamic Duo (220d)"),
                RankingEntry(4, "bff_04", "Hannah & Zoe", null, (310000 * multiplier).toLong(), formatScore((310000 * multiplier).toLong()), false, 15, "frame_rare_cyan_pulse", subtitle = "Secret Keepers (150d)"),
                RankingEntry(5, "bff_05", "Ryan & Chris", null, (210000 * multiplier).toLong(), formatScore((210000 * multiplier).toLong()), false, 13, "frame_common_aurora", subtitle = "Game Buddies (90d)"),
            )
            LeaderboardCategory.FAMILY -> listOf(
                RankingEntry(1, "fam_01", "Solaris Dynasty", null, (48200 * multiplier).toLong(), formatScore((48200 * multiplier).toLong()), true, 10, "frame_celestial_aurora_stream", subtitle = "50/50 Members • Patriarch: Cyrus King", tag = "SOLR"),
                RankingEntry(2, "fam_02", "Moonlight Echo", null, (39500 * multiplier).toLong(), formatScore((39500 * multiplier).toLong()), true, 8, "frame_mythic_radiant_crown", subtitle = "45/50 Members • Matriarch: Selena Frost", tag = "MOON"),
                RankingEntry(3, "fam_03", "Aurora Knights", null, (26800 * multiplier).toLong(), formatScore((26800 * multiplier).toLong()), true, 3, "frame_legendary_golden_sun", subtitle = "18/30 Members • Leader: Aria Nova", tag = "AURK"),
                RankingEntry(4, "fam_04", "Cyber Phoenix", null, (23400 * multiplier).toLong(), formatScore((23400 * multiplier).toLong()), false, 4, "frame_epic_violet_orbit", subtitle = "28/35 Members • Leader: Neo Walker", tag = "CYBR"),
                RankingEntry(5, "fam_05", "Crystal Guardians", null, (18200 * multiplier).toLong(), formatScore((18200 * multiplier).toLong()), false, 2, "frame_rare_cyan_pulse", subtitle = "20/25 Members • Leader: Tara Stone", tag = "CRYS"),
            )
        }

        // Current user mock / actual calculation
        val userRank: Int? = if (currentUser != null && currentUser.level >= 2) 14 else null
        val userScore: Long = if (userRank != null) (142500 * multiplier).toLong() else 0L

        val currentUserSummary = CurrentUserRank(
            rank = userRank,
            score = userScore,
            scoreFormatted = formatScore(userScore),
            percentile = if (userRank != null) "Top 8%" else null,
            category = category,
            ctaLabel = category.ctaLabel,
            ctaRoute = category.ctaRoute,
        )

        return Pair(entries, currentUserSummary)
    }

    /**
     * Official explanation and rules sheet for each category.
     */
    fun getRankingRules(category: LeaderboardCategory): RankingRuleInfo = when (category) {
        LeaderboardCategory.POPULARITY -> RankingRuleInfo(
            category = category,
            title = "Popularity Ranking Rules",
            summary = "Celebrates members with the highest received charm gifts across the social platform.",
            calculationRules = listOf(
                "Each gift received grants charm points equal to coin value.",
                "Today leaderboard recalculates every 15 minutes; resets at 00:00 UTC.",
                "Weekly leaderboard tallies points from Monday 00:00 UTC to Sunday 23:59 UTC.",
                "Total leaderboard aggregates all-time verified charm score.",
            ),
            resetSchedule = "Daily at 00:00 UTC / Weekly on Sunday 23:59 UTC",
            antiCheatNote = "Self-gifting and unauthorized bot accounts are screened by server moderation; fraudulent transactions are revoked.",
        )
        LeaderboardCategory.VIP -> RankingRuleInfo(
            category = category,
            title = "VIP Leaderboard Rules",
            summary = "Recognizes elite supporters based on their lifetime and periodic VIP contribution points.",
            calculationRules = listOf(
                "VIP points are earned through coin top-ups and verified platform activities.",
                "VIP tier unlocks exclusive frame cosmetics and badge crests.",
                "Celebrity period highlights continuous VIP tier retention.",
            ),
            resetSchedule = "VIP points are permanent; period tallies reset weekly.",
            antiCheatNote = "Chargebacks or refund requests automatically revoke earned VIP points and cosmetic perks.",
        )
        LeaderboardCategory.COUPLE -> RankingRuleInfo(
            category = category,
            title = "Couple Intimacy Ranking Rules",
            summary = "Ranks bonded partners by their shared intimacy and social synergy.",
            calculationRules = listOf(
                "Intimacy increases by voice room co-presence (+5/min), private messaging, and mutual gifting.",
                "Annual rankings honor pairs with persistent, unbroken partner bonds.",
                "Dissolving a bond resets unshared intimacy records.",
            ),
            resetSchedule = "Today resets daily at 00:00 UTC; Annual resets December 31st.",
            antiCheatNote = "Co-presence is capped at 180 minutes per day to prevent idle abuse.",
        )
        LeaderboardCategory.ROOM -> RankingRuleInfo(
            category = category,
            title = "Room Heat Ranking Rules",
            summary = "Features the most active voice rooms and live audio lounges.",
            calculationRules = listOf(
                "Heat points = active listeners × 2 + mic speaker time + in-room gifts.",
                "Rooms with higher engagement and audio interaction earn bonus multipliers.",
                "Host earns recognition and weekly badge crests.",
            ),
            resetSchedule = "Calculated in real-time; daily snapshot taken at 00:00 UTC.",
            antiCheatNote = "Audio silence and automated ghost listeners are excluded from heat calculation.",
        )
        LeaderboardCategory.BFF -> RankingRuleInfo(
            category = category,
            title = "BFF Friendship Ranking Rules",
            summary = "Highlights the tightest friendships on the platform.",
            calculationRules = listOf(
                "Friendship bond increases through chat exchanges, party gaming, and collaborative quests.",
                "Milestones unlock special dual profile frame perks.",
            ),
            resetSchedule = "Weekly tallies reset Sunday at 23:59 UTC.",
            antiCheatNote = "Automated spam messaging is filtered by conversation health algorithms.",
        )
        LeaderboardCategory.FAMILY -> RankingRuleInfo(
            category = category,
            title = "Family Activeness Ranking Rules",
            summary = "Ranks guilds and dynasties by weekly family quest completion and treasury donations.",
            calculationRules = listOf(
                "Members earn family activeness through daily quests and room participation.",
                "Top 3 families receive prestigious celestial/mythic crests for all members.",
                "Weekly treasury dividends are distributed to active contributors.",
            ),
            resetSchedule = "Weekly resets Sunday at 23:59 UTC.",
            antiCheatNote = "Ineligible or banned members do not contribute to guild activeness.",
        )
    }
}
