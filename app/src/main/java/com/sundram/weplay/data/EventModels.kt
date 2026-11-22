package com.sundram.weplay.data

import android.content.Context
import androidx.annotation.DrawableRes
import com.sundram.weplay.R
import java.util.concurrent.TimeUnit

enum class EventCategory(val displayName: String) {
    BONUS_REWARDS("Bonus & Rewards"),
    FEATURED("Featured Events"),
    FAMILY("Family Events"),
    PK_COMPETITIONS("PK Competitions"),
    SEASONAL("Seasonal & Limited"),
    COMPLETED("Completed Events"),
}

enum class EventStatus {
    LIVE,
    UPCOMING,
    ENDED,
}

data class EventReward(
    val title: String,
    val iconEmoji: String,
    val valueText: String,
    val isRare: Boolean = false,
)

data class EventTask(
    val id: String,
    val title: String,
    val description: String,
    val targetCount: Int,
    val currentCount: Int,
    val rewardCoins: Long,
    val rewardXp: Int,
    val isClaimed: Boolean = false,
) {
    val isCompleted: Boolean
        get() = currentCount >= targetCount
}

data class EventItem(
    val id: String,
    val title: String,
    val subtitle: String,
    val description: String,
    val category: EventCategory,
    @DrawableRes val coverRes: Int,
    val badgeText: String? = null,
    val startDate: String,
    val endDate: String,
    val startTimestamp: Long,
    val endTimestamp: Long,
    val status: EventStatus,
    val eligibility: String = "All Registered Adventurers",
    val rewards: List<EventReward> = emptyList(),
    val tasks: List<EventTask> = emptyList(),
    val rules: List<String> = emptyList(),
    val leaderboardId: String? = null,
    val participantCount: Int = 0,
) {
    fun formatRemainingTime(currentTime: Long = System.currentTimeMillis()): String {
        if (status == EventStatus.ENDED || currentTime >= endTimestamp) {
            return "Event Ended"
        }
        val target = if (currentTime < startTimestamp) startTimestamp else endTimestamp
        val diff = (target - currentTime).coerceAtLeast(0L)
        val days = TimeUnit.MILLISECONDS.toDays(diff)
        val hours = TimeUnit.MILLISECONDS.toHours(diff) % 24
        val minutes = TimeUnit.MILLISECONDS.toMinutes(diff) % 60
        return if (days > 0) {
            "${days}d ${hours}h left"
        } else {
            "%02dh %02dm left".format(hours, minutes)
        }
    }
}

/**
 * Event Store managing persistence of completed tasks & claimed rewards.
 */
class EventRepository(context: Context) {
    private val prefs = context.getSharedPreferences("weplay_events_store", Context.MODE_PRIVATE)

    companion object {
        private val now = System.currentTimeMillis()
        private val dayMillis = 24L * 60L * 60L * 1000L

        val ALL_EVENTS: List<EventItem> = listOf(
            // ── 1. Bonus & Rewards ──
            EventItem(
                id = "evt_welcome",
                title = "Welcome Adventure",
                subtitle = "New player onboarding perks",
                description = "Begin your journey across our social lounge and games hub with exclusive welcome bonuses, XP boosts, and starter badges.",
                category = EventCategory.BONUS_REWARDS,
                coverRes = R.drawable.vb_room_cover_moonlight,
                badgeText = "NEW PLAYER",
                startDate = "Nov 15",
                endDate = "Dec 15",
                startTimestamp = now - (5 * dayMillis),
                endTimestamp = now + (25 * dayMillis),
                status = EventStatus.LIVE,
                eligibility = "All adventurers level 1 to 10",
                participantCount = 14200,
                rewards = listOf(
                    EventReward("200 Welcome Coins", "🪙", "+200"),
                    EventReward("Starter Starlight Frame", "🖼️", "7 Days", isRare = true),
                    EventReward("3x Rose Stage Gifts", "🌹", "x3"),
                ),
                tasks = listOf(
                    EventTask("t_01", "Complete 1 Multiplayer Game", "Play a round of Ludo or Who's the Spy", 1, 1, 100, 50),
                    EventTask("t_02", "Join an Active Voice Room", "Visit a live social room for 3 mins", 1, 1, 50, 30),
                    EventTask("t_03", "Connect with 2 Players", "Follow 2 community members", 2, 1, 50, 20),
                ),
                rules = listOf(
                    "Each account is eligible for welcome rewards once.",
                    "Tasks must be completed while logged in with a verified account.",
                    "Rewards are credited to your game wallet immediately.",
                ),
            ),
            EventItem(
                id = "evt_first_topup",
                title = "First Top-Up Bonus",
                subtitle = "100% Coin Matching Gift",
                description = "Get double value on your initial coin top-up plus an exclusive Gold Crown entrance avatar flair.",
                category = EventCategory.BONUS_REWARDS,
                coverRes = R.drawable.vb_room_cover_pop_star,
                badgeText = "100% MATCH",
                startDate = "Nov 01",
                endDate = "Dec 31",
                startTimestamp = now - (20 * dayMillis),
                endTimestamp = now + (40 * dayMillis),
                status = EventStatus.LIVE,
                eligibility = "Accounts making their first top-up transaction",
                participantCount = 8940,
                rewards = listOf(
                    EventReward("Double Coins Match", "🪙", "2x Value"),
                    EventReward("VIP Gold Crown Entrance", "👑", "14 Days", isRare = true),
                    EventReward("Stage Diamond Shower", "💎", "Special"),
                ),
                tasks = listOf(
                    EventTask("t_topup_1", "Inspect Coin Catalog", "Browse top-up packages in the store", 1, 1, 20, 10),
                    EventTask("t_topup_2", "Complete 1st Top-Up", "Purchase any verified coin pack", 1, 0, 500, 200),
                ),
                rules = listOf(
                    "Available on any package tier on your first purchase.",
                    "Bonus coins are credited alongside base pack balance upon receipt validation.",
                ),
            ),

            // ── 2. Featured Events ──
            EventItem(
                id = "evt_starlight",
                title = "Starlight Festival",
                subtitle = "Celestial Celebration Week",
                description = "Celebrate the starry night in Aurora Lounge. Send star gifts, light up the celestial stages, and unlock limited-edition constellation banners.",
                category = EventCategory.FEATURED,
                coverRes = R.drawable.vb_room_cover_moonlight,
                badgeText = "HOT",
                startDate = "Nov 20",
                endDate = "Nov 27",
                startTimestamp = now - (2 * dayMillis),
                endTimestamp = now + (5 * dayMillis),
                status = EventStatus.LIVE,
                eligibility = "Open to all verified users",
                participantCount = 23150,
                rewards = listOf(
                    EventReward("Constellation Nameplate", "✨", "Exclusive", isRare = true),
                    EventReward("Aurora Sound Wave Effect", "🎵", "14 Days"),
                    EventReward("1,000 Event Points", "🌟", "+1000"),
                ),
                tasks = listOf(
                    EventTask("t_star_1", "Spend 10 mins in Voice Rooms", "Listen to public stages", 10, 8, 80, 40),
                    EventTask("t_star_2", "Send 5 Star Gifts", "Support stage performers", 5, 2, 150, 80),
                    EventTask("t_star_3", "Share Starlight Festival", "Invite a friend to the event", 1, 0, 50, 30),
                ),
                rules = listOf(
                    "Star points are tracked across all public lounges during festival hours.",
                    "Leaderboard rankings update every hour based on verified gift activity.",
                ),
                leaderboardId = "lb_starlight",
            ),

            // ── 3. Family Events ──
            EventItem(
                id = "evt_family_fest",
                title = "Family Festival",
                subtitle = "Clan Unity & Treasury Drive",
                description = "Rally your Family members! Complete cooperative quests, donate to the treasury, and climb the global Family Rankings for prestige chests.",
                category = EventCategory.FAMILY,
                coverRes = R.drawable.vb_room_cover_pop_star,
                badgeText = "CO-OP",
                startDate = "Nov 18",
                endDate = "Nov 25",
                startTimestamp = now - (4 * dayMillis),
                endTimestamp = now + (3 * dayMillis),
                status = EventStatus.LIVE,
                eligibility = "Members of an active registered Family",
                participantCount = 6120,
                rewards = listOf(
                    EventReward("Grand Family Shield", "🛡️", "Clan Trophy", isRare = true),
                    EventReward("10,000 Treasury Coins", "🪙", "+10,000"),
                    EventReward("Family Hall Banner Flair", "🏛️", "Level Boost"),
                ),
                tasks = listOf(
                    EventTask("t_fam_1", "Donate to Family Treasury", "Contribute at least 50 coins", 1, 1, 100, 50),
                    EventTask("t_fam_2", "Gather 4 Members in Voice Hall", "Host a family voice session", 1, 0, 200, 100),
                ),
                rules = listOf(
                    "Points are combined for all members belonging to the same Family.",
                    "Only verified treasury donations count toward the Clan leaderboard.",
                ),
                leaderboardId = "lb_family_rankings",
            ),

            // ── 4. PK Competitions ──
            EventItem(
                id = "evt_royal_arena",
                title = "Royal Arena Battle",
                subtitle = "1v1 & 4v4 Showdown Series",
                description = "Step onto the battle stage. Challenge rival hosts in 1v1 duels or lead your squad in 4v4 Team PK clashes for ultimate glory and crown rewards.",
                category = EventCategory.PK_COMPETITIONS,
                coverRes = R.drawable.vb_room_cover_billiards_lounge,
                badgeText = "PK TOURNAMENT",
                startDate = "Nov 21",
                endDate = "Nov 28",
                startTimestamp = now - (1 * dayMillis),
                endTimestamp = now + (6 * dayMillis),
                status = EventStatus.LIVE,
                eligibility = "Participants in eligible PK-enabled rooms",
                participantCount = 11840,
                rewards = listOf(
                    EventReward("Royal Arena Champion Belt", "🥊", "Champion", isRare = true),
                    EventReward("Blue & Rose Battle Frames", "⚔️", "Animated", isRare = true),
                    EventReward("500 Bonus PK Score", "🔥", "+500"),
                ),
                tasks = listOf(
                    EventTask("t_pk_1", "Participate in 1 Single PK", "Compete in a 1v1 battle", 1, 1, 120, 60),
                    EventTask("t_pk_2", "Win 1 Battle Duel", "Achieve victory in any PK format", 1, 0, 250, 150),
                    EventTask("t_pk_3", "Send 1 PK Support Gift", "Contribute battle points to a team", 1, 1, 80, 40),
                ),
                rules = listOf(
                    "Battle scores are calculated server-side based on verified gift delivery.",
                    "Ties and cancellations follow documented fair-play tournament guidelines.",
                ),
                leaderboardId = "lb_pk_championship",
            ),
            EventItem(
                id = "evt_pk_championship",
                title = "PK Championship",
                subtitle = "Global Host Duels",
                description = "The premier seasonal PK tournament featuring the top 32 ranked room hosts battling live for the Golden Microphones.",
                category = EventCategory.PK_COMPETITIONS,
                coverRes = R.drawable.vb_character_space_werewolf,
                badgeText = "UPCOMING",
                startDate = "Dec 01",
                endDate = "Dec 07",
                startTimestamp = now + (9 * dayMillis),
                endTimestamp = now + (15 * dayMillis),
                status = EventStatus.UPCOMING,
                eligibility = "Top 32 ranked hosts in Season 3",
                participantCount = 32,
                rewards = listOf(
                    EventReward("Golden Microphone Trophy", "🎙️", "1st Place", isRare = true),
                    EventReward("50,000 Coin Prize Pool", "🪙", "Grand Prize", isRare = true),
                ),
                tasks = emptyList(),
                rules = listOf(
                    "Single elimination tournament format across 5 rounds.",
                    "Qualifying matches will be streamed live in featured tournament lounges.",
                ),
            ),

            // ── 5. Seasonal & Limited ──
            EventItem(
                id = "evt_winter_aurora",
                title = "Winter Aurora Gala",
                subtitle = "Frost & Starlight Limited Event",
                description = "Snowflakes, ice castles, and cozy midnight acoustic sessions. Collect festive snowflakes to craft limited-edition winter badges.",
                category = EventCategory.SEASONAL,
                coverRes = R.drawable.vb_room_cover_moonlight,
                badgeText = "LIMITED",
                startDate = "Dec 10",
                endDate = "Dec 25",
                startTimestamp = now + (18 * dayMillis),
                endTimestamp = now + (33 * dayMillis),
                status = EventStatus.UPCOMING,
                eligibility = "All adventurers",
                participantCount = 0,
                rewards = listOf(
                    EventReward("Frostbite Animated Frame", "❄️", "Limited", isRare = true),
                    EventReward("Cozy Fireplace Audio Wave", "🔥", "Audio Skin"),
                ),
                tasks = emptyList(),
                rules = listOf(
                    "Limited-time winter cosmetics will not return until next year.",
                ),
            ),

            // ── 6. Completed Events ──
            EventItem(
                id = "evt_autumn_carnival",
                title = "Autumn Carnival",
                subtitle = "Harvest Party & Games Blitz",
                description = "The autumn games festival has concluded. Thank you to the 45,000+ players who joined Ludo and Spy party tournaments!",
                category = EventCategory.COMPLETED,
                coverRes = R.drawable.vb_character_royal_dice,
                badgeText = "ENDED",
                startDate = "Oct 15",
                endDate = "Nov 10",
                startTimestamp = now - (38 * dayMillis),
                endTimestamp = now - (12 * dayMillis),
                status = EventStatus.ENDED,
                eligibility = "Event concluded",
                participantCount = 45200,
                rewards = listOf(
                    EventReward("Autumn Leaf Badge", "🍁", "Distributed"),
                ),
                tasks = emptyList(),
                rules = listOf(
                    "All final prizes and trophies were credited to winners on Nov 11.",
                ),
            ),
        )
    }

    fun getAllEvents(): List<EventItem> = ALL_EVENTS

    fun getEventById(id: String): EventItem? {
        val base = ALL_EVENTS.firstOrNull { it.id == id } ?: return null
        // Overlay local task claim states
        val updatedTasks = base.tasks.map { task ->
            val isClaimed = prefs.getBoolean("claimed_${base.id}_${task.id}", false)
            task.copy(isClaimed = isClaimed)
        }
        return base.copy(tasks = updatedTasks)
    }

    fun isTaskClaimed(eventId: String, taskId: String): Boolean {
        return prefs.getBoolean("claimed_${eventId}_${taskId}", false)
    }

    /**
     * Server-authoritative idempotency claim pattern.
     * Prevents double-claiming rewards.
     */
    fun claimTaskReward(eventId: String, taskId: String): Boolean {
        val key = "claimed_${eventId}_${taskId}"
        if (prefs.getBoolean(key, false)) {
            return false // Already claimed
        }
        prefs.edit().putBoolean(key, true).apply()
        return true
    }
}
