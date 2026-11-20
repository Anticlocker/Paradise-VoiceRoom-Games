package com.sundram.weplay.data

import android.content.Context
import androidx.annotation.DrawableRes
import com.sundram.weplay.R
import java.util.concurrent.TimeUnit

/**
 * An item included in a Gift Pack offer.
 */
data class GiftPackItem(
    val name: String,
    val count: Int,
    @DrawableRes val iconRes: Int,
    val isEffectOrPreview: Boolean = false,
    val durationText: String? = null,
)

/**
 * Definition of a Gift Pack offer.
 */
data class GiftPackOffer(
    val id: String,
    val title: String,
    val demoPriceRupees: Int,
    val description: String,
    val items: List<GiftPackItem>,
    val badge: String? = null,
    val isDemoOnly: Boolean = true,
)

/**
 * 10 Configurable Starter Offer Definitions for VYBORA.
 */
val ALL_GIFT_PACK_OFFERS: List<GiftPackOffer> = listOf(
    GiftPackOffer(
        id = "GP01",
        title = "Mini Spark",
        demoPriceRupees = 9,
        description = "Instant room greeting starter kit",
        items = listOf(
            GiftPackItem("Roses", 3, R.drawable.vb_gift_rose),
            GiftPackItem("Sparkle Gift", 1, R.drawable.vb_gift_star, isEffectOrPreview = true),
        ),
        badge = "STARTER",
    ),
    GiftPackOffer(
        id = "GP02",
        title = "Sweet Hearts",
        demoPriceRupees = 15,
        description = "Spread love in voice lounges",
        items = listOf(
            GiftPackItem("Hearts", 5, R.drawable.vb_gift_heart),
            GiftPackItem("Love Badge Preview", 1, R.drawable.vb_badge_silver, isEffectOrPreview = true, durationText = "3 Days"),
        ),
        badge = "POPULAR",
    ),
    GiftPackOffer(
        id = "GP03",
        title = "Lucky Petals",
        demoPriceRupees = 19,
        description = "Fragrant gifts & ambient entrance shimmer",
        items = listOf(
            GiftPackItem("Roses", 6, R.drawable.vb_gift_rose),
            GiftPackItem("Gift Effect", 2, R.drawable.vb_gift_star, isEffectOrPreview = true),
        ),
        badge = "VALUE",
    ),
    GiftPackOffer(
        id = "GP04",
        title = "Friendship Box",
        demoPriceRupees = 25,
        description = "Perfect gesture for your favorite speakers",
        items = listOf(
            GiftPackItem("Friendship Gifts", 4, R.drawable.vb_gift_heart),
            GiftPackItem("Profile Glow Effect", 1, R.drawable.vb_badge_gold, isEffectOrPreview = true, durationText = "7 Days"),
        ),
        badge = "SOCIAL",
    ),
    GiftPackOffer(
        id = "GP05",
        title = "Starry Surprise",
        demoPriceRupees = 29,
        description = "Dazzling night-sky gifts for stage moments",
        items = listOf(
            GiftPackItem("Stars", 5, R.drawable.vb_badge_gold),
            GiftPackItem("Sparkle Gifts", 2, R.drawable.vb_gift_star, isEffectOrPreview = true),
        ),
        badge = "DAZZLE",
    ),
    GiftPackOffer(
        id = "GP06",
        title = "Moonlight Treat",
        demoPriceRupees = 39,
        description = "Celestial tokens & heartfelt affections",
        items = listOf(
            GiftPackItem("Moon Gifts", 3, R.drawable.vb_badge_diamond),
            GiftPackItem("Hearts", 3, R.drawable.vb_gift_heart),
        ),
        badge = "NIGHT",
    ),
    GiftPackOffer(
        id = "GP07",
        title = "Royal Mini",
        demoPriceRupees = 49,
        description = "Regal crowns fit for the room host",
        items = listOf(
            GiftPackItem("Royal Crowns", 2, R.drawable.vb_gift_crown),
            GiftPackItem("Roses", 4, R.drawable.vb_gift_rose),
        ),
        badge = "ROYAL",
    ),
    GiftPackOffer(
        id = "GP08",
        title = "Crystal Joy",
        demoPriceRupees = 59,
        description = "Sparkling luxury crystal tokens",
        items = listOf(
            GiftPackItem("Crystal Castles", 3, R.drawable.vb_gift_diamond),
            GiftPackItem("Stars", 2, R.drawable.vb_badge_diamond),
        ),
        badge = "LUXURY",
    ),
    GiftPackOffer(
        id = "GP09",
        title = "Festival Bundle",
        demoPriceRupees = 79,
        description = "Celebration items with audio wave flairs",
        items = listOf(
            GiftPackItem("Celebration Gifts", 5, R.drawable.vb_gift_star),
            GiftPackItem("Premium Stage Effect", 2, R.drawable.vb_gift_crown, isEffectOrPreview = true, durationText = "14 Days"),
        ),
        badge = "FESTIVAL",
    ),
    GiftPackOffer(
        id = "GP10",
        title = "Aurora Special",
        demoPriceRupees = 99,
        description = "The ultimate Aurora Lounge deluxe bundle",
        items = listOf(
            GiftPackItem("Supercar & Pegasus", 4, R.drawable.vb_gift_crown),
            GiftPackItem("Aurora Frame Preview", 1, R.drawable.vb_vr_frame_vip, isEffectOrPreview = true, durationText = "30 Days"),
        ),
        badge = "AURORA VIP",
    ),
)

/**
 * State representing the currently active 48-hour featured offer.
 */
data class FeaturedOfferState(
    val offer: GiftPackOffer,
    val startTimeMillis: Long,
    val endTimeMillis: Long,
    val remainingMillis: Long,
    val formattedTimeRemaining: String,
    val cycleIndex: Int,
)

/**
 * Manager responsible for rotating exactly 1 offer per 48-hour cycle per user.
 */
class GiftPackRotationManager(context: Context, private val userId: String) {

    private val prefs = context.getSharedPreferences("weplay_gift_packs", Context.MODE_PRIVATE)

    companion object {
        const val CYCLE_DURATION_MILLIS = 48L * 60L * 60L * 1000L // 48 Hours
    }

    /**
     * Get or calculate the active featured offer for the current 48-hour cycle.
     */
    fun getFeaturedOffer(currentTimeMillis: Long = System.currentTimeMillis()): FeaturedOfferState {
        val userPrefix = "u_${userId}_"
        var startTime = prefs.getLong("${userPrefix}start_time", 0L)
        var endTime = prefs.getLong("${userPrefix}end_time", 0L)
        var offerId = prefs.getString("${userPrefix}offer_id", null)
        var cycleIndex = prefs.getInt("${userPrefix}cycle_index", 0)

        // Check if cycle expired or not initialized
        if (startTime == 0L || currentTimeMillis >= endTime || offerId == null) {
            startTime = currentTimeMillis
            endTime = currentTimeMillis + CYCLE_DURATION_MILLIS
            cycleIndex += 1

            // Deterministic rotation across the 10 offers
            val offerIndex = (cycleIndex - 1) % ALL_GIFT_PACK_OFFERS.size
            offerId = ALL_GIFT_PACK_OFFERS[offerIndex].id

            prefs.edit()
                .putLong("${userPrefix}start_time", startTime)
                .putLong("${userPrefix}end_time", endTime)
                .putString("${userPrefix}offer_id", offerId)
                .putInt("${userPrefix}cycle_index", cycleIndex)
                .putBoolean("${userPrefix}shown_auto", false)
                .apply()
        }

        val offer = ALL_GIFT_PACK_OFFERS.firstOrNull { it.id == offerId } ?: ALL_GIFT_PACK_OFFERS[0]
        val remaining = (endTime - currentTimeMillis).coerceAtLeast(0L)
        val formatted = formatRemainingTime(remaining)

        return FeaturedOfferState(
            offer = offer,
            startTimeMillis = startTime,
            endTimeMillis = endTime,
            remainingMillis = remaining,
            formattedTimeRemaining = formatted,
            cycleIndex = cycleIndex,
        )
    }

    /**
     * Checks if the automatic promotional popup has already been shown for this 48h cycle.
     */
    fun shouldShowAutoPopup(): Boolean {
        val userPrefix = "u_${userId}_"
        return !prefs.getBoolean("${userPrefix}shown_auto", false)
    }

    /**
     * Marks the automatic popup as shown for the current cycle.
     */
    fun markAutoPopupShown() {
        val userPrefix = "u_${userId}_"
        prefs.edit().putBoolean("${userPrefix}shown_auto", true).apply()
    }

    private fun formatRemainingTime(millis: Long): String {
        val hours = TimeUnit.MILLISECONDS.toHours(millis)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(millis) % 60
        val seconds = TimeUnit.MILLISECONDS.toSeconds(millis) % 60
        return "%02dh %02dm %02ds".format(hours, minutes, seconds)
    }
}
