package com.sundram.weplay.data

import android.content.Context
import android.content.SharedPreferences
import com.sundram.weplay.R
import com.sundram.weplay.ui.nav.Routes

data class MomentComment(
    val id: String,
    val authorName: String,
    val authorAvatarRes: Int,
    val text: String,
    val timestamp: Long,
)

data class MomentPost(
    val id: String,
    val authorId: String,
    val authorName: String,
    val authorAvatarRes: Int,
    val text: String,
    val tag: String,
    val timestamp: Long,
    val likesCount: Int,
    val isLiked: Boolean = false,
    val commentsCount: Int,
    val comments: List<MomentComment> = emptyList(),
    val isVerified: Boolean = false,
    val isFamilyMember: Boolean = false,
    val mediaBadge: String? = null,
)

data class LiveActivityEvent(
    val id: String,
    val who: String,
    val whoAvatarRes: Int,
    val action: String,
    val tag: String,
    val timeAgo: String,
    val targetRoute: String,
)

data class CommunityClub(
    val id: String,
    val name: String,
    val category: String,
    val description: String,
    val membersCount: Int,
    val iconRes: Int,
    val badgeRes: Int = R.drawable.vb_discover_badge_verified,
    val isJoined: Boolean = false,
    val activityTag: String,
)

data class DiscoverEvent(
    val id: String,
    val title: String,
    val subtitle: String,
    val schedule: String,
    val prizePool: String? = null,
    val iconRes: Int,
    val isLive: Boolean = false,
    val targetRoute: String,
)

data class NearbyPlayer(
    val id: String,
    val name: String,
    val distanceKm: Double,
    val favoriteGame: String,
    val isOnline: Boolean,
    val avatarRes: Int,
    val level: Int,
)

class InMemorySharedPreferences : SharedPreferences {
    private val map = mutableMapOf<String, Any>()

    override fun getAll(): Map<String, *> = map.toMap()

    override fun getString(key: String?, defValue: String?): String? =
        map[key] as? String ?: defValue

    @Suppress("UNCHECKED_CAST")
    override fun getStringSet(key: String?, defValues: Set<String>?): Set<String>? =
        (map[key] as? Set<String>)?.toSet() ?: defValues

    override fun getInt(key: String?, defValue: Int): Int =
        (map[key] as? Number)?.toInt() ?: defValue

    override fun getLong(key: String?, defValue: Long): Long =
        (map[key] as? Number)?.toLong() ?: defValue

    override fun getFloat(key: String?, defValue: Float): Float =
        (map[key] as? Number)?.toFloat() ?: defValue

    override fun getBoolean(key: String?, defValue: Boolean): Boolean =
        map[key] as? Boolean ?: defValue

    override fun contains(key: String?): Boolean = map.containsKey(key)

    override fun edit(): SharedPreferences.Editor = EditorImpl()

    override fun registerOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener?) {}

    override fun unregisterOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener?) {}

    private inner class EditorImpl : SharedPreferences.Editor {
        private val temp = mutableMapOf<String, Any?>()
        private var clearFlag = false

        override fun putString(key: String?, value: String?): SharedPreferences.Editor {
            if (key != null) temp[key] = value
            return this
        }

        override fun putStringSet(key: String?, values: Set<String>?): SharedPreferences.Editor {
            if (key != null) temp[key] = values?.toSet()
            return this
        }

        override fun putInt(key: String?, value: Int): SharedPreferences.Editor {
            if (key != null) temp[key] = value
            return this
        }

        override fun putLong(key: String?, value: Long): SharedPreferences.Editor {
            if (key != null) temp[key] = value
            return this
        }

        override fun putFloat(key: String?, value: Float): SharedPreferences.Editor {
            if (key != null) temp[key] = value
            return this
        }

        override fun putBoolean(key: String?, value: Boolean): SharedPreferences.Editor {
            if (key != null) temp[key] = value
            return this
        }

        override fun remove(key: String?): SharedPreferences.Editor {
            if (key != null) temp[key] = null
            return this
        }

        override fun clear(): SharedPreferences.Editor {
            clearFlag = true
            return this
        }

        override fun commit(): Boolean {
            apply()
            return true
        }

        override fun apply() {
            if (clearFlag) {
                map.clear()
            }
            temp.forEach { (k, v) ->
                if (v == null) {
                    map.remove(k)
                } else {
                    map[k] = v
                }
            }
        }
    }
}

class DiscoverStore(
    context: Context? = null,
    customPrefs: SharedPreferences? = null,
) {
    private val prefs: SharedPreferences = customPrefs ?: run {
        try {
            context?.applicationContext?.getSharedPreferences("weplay_discover_store", Context.MODE_PRIVATE)
                ?: InMemorySharedPreferences()
        } catch (_: Throwable) {
            InMemorySharedPreferences()
        }
    }

    companion object {
        val TAGS = listOf("All", "Ludo", "Who's the Spy", "KTV", "Werewolf", "Casual Lounge", "Families")
    }

    private fun getLikedIds(): Set<String> =
        prefs.getStringSet("liked_moment_ids", emptySet()) ?: emptySet()

    private fun getJoinedClubIds(): Set<String> =
        prefs.getStringSet("joined_club_ids", emptySet()) ?: emptySet()

    fun isNearbyVisible(): Boolean =
        prefs.getBoolean("nearby_visible", true)

    fun setNearbyVisible(visible: Boolean) {
        prefs.edit().putBoolean("nearby_visible", visible).apply()
    }

    fun toggleLike(momentId: String): Boolean {
        val current = getLikedIds().toMutableSet()
        val isNowLiked = if (current.contains(momentId)) {
            current.remove(momentId)
            false
        } else {
            current.add(momentId)
            true
        }
        prefs.edit().putStringSet("liked_moment_ids", current).apply()
        return isNowLiked
    }

    fun toggleClubJoin(clubId: String): Boolean {
        val current = getJoinedClubIds().toMutableSet()
        val isJoined = if (current.contains(clubId)) {
            current.remove(clubId)
            false
        } else {
            current.add(clubId)
            true
        }
        prefs.edit().putStringSet("joined_club_ids", current).apply()
        return isJoined
    }

    fun addComment(momentId: String, authorName: String, text: String, avatarRes: Int): MomentComment {
        val comment = MomentComment(
            id = "c_${System.currentTimeMillis()}",
            authorName = authorName,
            authorAvatarRes = avatarRes,
            text = text,
            timestamp = System.currentTimeMillis(),
        )
        val raw = prefs.getString("comments_$momentId", "") ?: ""
        val entry = "${comment.id}|${comment.authorName}|$avatarRes|$text|${comment.timestamp}"
        val updated = if (raw.isBlank()) entry else "$raw\u0001$entry"
        prefs.edit().putString("comments_$momentId", updated).apply()
        return comment
    }

    private fun getCustomComments(momentId: String): List<MomentComment> {
        val raw = prefs.getString("comments_$momentId", null) ?: return emptyList()
        return raw.split("\u0001").mapNotNull { line ->
            val p = line.split("|")
            if (p.size >= 5) {
                MomentComment(
                    id = p[0],
                    authorName = p[1],
                    authorAvatarRes = p[2].toIntOrNull() ?: R.drawable.vb_discover_avatar_01,
                    text = p[3],
                    timestamp = p[4].toLongOrNull() ?: 0L,
                )
            } else null
        }
    }

    fun createMoment(text: String, tag: String, authorName: String, authorId: String, avatarRes: Int): MomentPost {
        val id = "post_${System.currentTimeMillis()}"
        val post = MomentPost(
            id = id,
            authorId = authorId,
            authorName = authorName,
            authorAvatarRes = avatarRes,
            text = text,
            tag = tag,
            timestamp = System.currentTimeMillis(),
            likesCount = 0,
            isLiked = false,
            commentsCount = 0,
            isVerified = true,
        )
        val raw = prefs.getString("custom_posts", "") ?: ""
        val entry = "$id|$authorId|$authorName|$avatarRes|${text.replace("|", "/")}|$tag|${post.timestamp}"
        val updated = if (raw.isBlank()) entry else "$entry\u0001$raw"
        prefs.edit().putString("custom_posts", updated).apply()
        return post
    }

    private fun getCustomMoments(): List<MomentPost> {
        val raw = prefs.getString("custom_posts", null) ?: return emptyList()
        val liked = getLikedIds()
        return raw.split("\u0001").mapNotNull { line ->
            val p = line.split("|")
            if (p.size >= 7) {
                val id = p[0]
                val customComments = getCustomComments(id)
                MomentPost(
                    id = id,
                    authorId = p[1],
                    authorName = p[2],
                    authorAvatarRes = p[3].toIntOrNull() ?: R.drawable.vb_discover_avatar_01,
                    text = p[4],
                    tag = p[5],
                    timestamp = p[6].toLongOrNull() ?: 0L,
                    likesCount = if (liked.contains(id)) 1 else 0,
                    isLiked = liked.contains(id),
                    commentsCount = customComments.size,
                    comments = customComments,
                    isVerified = true,
                )
            } else null
        }
    }

    fun getAllMoments(): List<MomentPost> {
        val liked = getLikedIds()
        val baseMoments = listOf(
            MomentPost(
                id = "m1",
                authorId = "sneha",
                authorName = "Sneha",
                authorAvatarRes = R.drawable.vb_discover_avatar_02,
                text = "Anyone ready for 4-player Ludo tournament tonight? Building an unstoppable squad! 🎲✨",
                tag = "Ludo",
                timestamp = System.currentTimeMillis() - 25L * 60_000,
                likesCount = 38 + if (liked.contains("m1")) 1 else 0,
                isLiked = liked.contains("m1"),
                commentsCount = 12 + getCustomComments("m1").size,
                comments = listOf(
                    MomentComment("c1_1", "Raj", R.drawable.vb_discover_avatar_03, "Count me in! What time?", System.currentTimeMillis() - 15L * 60_000),
                    MomentComment("c1_2", "Priya", R.drawable.vb_discover_avatar_04, "I'll host the voice room!", System.currentTimeMillis() - 8L * 60_000),
                ) + getCustomComments("m1"),
                isVerified = true,
                isFamilyMember = true,
                mediaBadge = "Match Squad",
            ),
            MomentPost(
                id = "m2",
                authorId = "ankit",
                authorName = "Ankit",
                authorAvatarRes = R.drawable.vb_discover_avatar_05,
                text = "I was the Spy three times in a row! Bluff level 100 survived until the last vote 🕵️‍♂️🔥",
                tag = "Who's the Spy",
                timestamp = System.currentTimeMillis() - 85L * 60_000,
                likesCount = 27 + if (liked.contains("m2")) 1 else 0,
                isLiked = liked.contains("m2"),
                commentsCount = 7 + getCustomComments("m2").size,
                comments = listOf(
                    MomentComment("c2_1", "Sam", R.drawable.vb_discover_avatar_06, "You had us all convinced lol", System.currentTimeMillis() - 40L * 60_000),
                ) + getCustomComments("m2"),
                mediaBadge = "Spy MVP",
            ),
            MomentPost(
                id = "m3",
                authorId = "priya",
                authorName = "Priya",
                authorAvatarRes = R.drawable.vb_discover_avatar_04,
                text = "Acoustic stage in Voice Lounge 1 was magical tonight. Thanks everyone for tuning in to the sing-along 🎙️🎶",
                tag = "KTV",
                timestamp = System.currentTimeMillis() - 180L * 60_000,
                likesCount = 54 + if (liked.contains("m3")) 1 else 0,
                isLiked = liked.contains("m3"),
                commentsCount = 19 + getCustomComments("m3").size,
                comments = listOf(
                    MomentComment("c3_1", "Kavya", R.drawable.vb_discover_avatar_07, "Your vocal tone was incredible!", System.currentTimeMillis() - 120L * 60_000),
                ) + getCustomComments("m3"),
                isVerified = true,
                mediaBadge = "Stage Clip",
            ),
            MomentPost(
                id = "m4",
                authorId = "sam",
                authorName = "Sam",
                authorAvatarRes = R.drawable.vb_discover_avatar_06,
                text = "Desi Gamers clan just claimed Rank 12 on the Family Leaderboard! Big shoutout to all contributors 👑🏆",
                tag = "Families",
                timestamp = System.currentTimeMillis() - 360L * 60_000,
                likesCount = 42 + if (liked.contains("m4")) 1 else 0,
                isLiked = liked.contains("m4"),
                commentsCount = 9 + getCustomComments("m4").size,
                comments = getCustomComments("m4"),
                isFamilyMember = true,
                mediaBadge = "Clan Rank Up",
            ),
            MomentPost(
                id = "m5",
                authorId = "vikram",
                authorName = "Vikram",
                authorAvatarRes = R.drawable.vb_discover_avatar_08,
                text = "Space Werewolf investigation was intense. Doctor saved me right on round 3 🐺🚀",
                tag = "Werewolf",
                timestamp = System.currentTimeMillis() - 720L * 60_000,
                likesCount = 19 + if (liked.contains("m5")) 1 else 0,
                isLiked = liked.contains("m5"),
                commentsCount = 4 + getCustomComments("m5").size,
                comments = getCustomComments("m5"),
            ),
        )
        return getCustomMoments() + baseMoments
    }

    fun getAllLiveUpdates(): List<LiveActivityEvent> = listOf(
        LiveActivityEvent("e1", "Ankit", R.drawable.vb_discover_avatar_05, "won a 4-player match in Ludo", "Ludo", "2m ago", Routes.LUDO),
        LiveActivityEvent("e2", "Priya", R.drawable.vb_discover_avatar_04, "discovered the Spy in round one — 3 votes", "Who's the Spy", "9m ago", Routes.SPY),
        LiveActivityEvent("e3", "Desi Kings", R.drawable.vb_discover_families, "climbed to Clan Rank 12 today", "Families", "45m ago", Routes.FAMILY),
        LiveActivityEvent("e4", "Sam", R.drawable.vb_discover_avatar_06, "opened a new Voice Room: Late Night Chill", "KTV", "1h ago", Routes.roomRoute("r1")),
        LiveActivityEvent("e5", "Sneha", R.drawable.vb_discover_avatar_02, "reached Level 10 in Space Werewolf", "Werewolf", "3h ago", Routes.SPY),
    )

    fun getAllClubs(): List<CommunityClub> {
        val joined = getJoinedClubIds()
        return listOf(
            CommunityClub(
                id = "club_ludo",
                name = "Ludo Masters Club",
                category = "Gaming",
                description = "Daily tournaments, coin challenges & squad rooms for true board tacticians.",
                membersCount = 1420,
                iconRes = R.drawable.vb_discover_clubs,
                badgeRes = R.drawable.vb_discover_badge_verified,
                isJoined = joined.contains("club_ludo"),
                activityTag = "Daily Matches",
            ),
            CommunityClub(
                id = "club_ktv",
                name = "Acoustic KTV Lounge",
                category = "Music",
                description = "Late night open mic, guitar chords, duet singing & party stages.",
                membersCount = 980,
                iconRes = R.drawable.vb_discover_communities,
                badgeRes = R.drawable.vb_discover_badge_live,
                isJoined = joined.contains("club_ktv"),
                activityTag = "Live Stage",
            ),
            CommunityClub(
                id = "club_spy",
                name = "Space Werewolf Society",
                category = "Gaming",
                description = "Mystery solvers, bluffs, deduction and intense voice investigations.",
                membersCount = 760,
                iconRes = R.drawable.vb_discover_explore,
                badgeRes = R.drawable.vb_discover_badge_new,
                isJoined = joined.contains("club_spy"),
                activityTag = "Active Squads",
            ),
            CommunityClub(
                id = "club_party",
                name = "Weekend Party Circle",
                category = "Social",
                description = "Casual chatting, game nights, chill vibes and welcoming new friends.",
                membersCount = 540,
                iconRes = R.drawable.vb_discover_friends,
                badgeRes = R.drawable.vb_discover_badge_family,
                isJoined = joined.contains("club_party"),
                activityTag = "Chill Lounge",
            ),
        )
    }

    fun getAllEvents(): List<DiscoverEvent> = listOf(
        DiscoverEvent(
            id = "ev_ludo_cup",
            title = "Weekend Ludo Championship",
            subtitle = "Compete against top players for coin prizes and seasonal bragging rights",
            schedule = "Starts in 2 hours",
            prizePool = "1,000 Coins",
            iconRes = R.drawable.vb_discover_events,
            isLive = true,
            targetRoute = Routes.LUDO,
        ),
        DiscoverEvent(
            id = "ev_ktv_stage",
            title = "Golden Mic KTV Night",
            subtitle = "Live singing contest with real-time voting by the voice audience",
            schedule = "Live Now · Room 1",
            prizePool = "Special Avatar Frame",
            iconRes = R.drawable.vb_discover_communities,
            isLive = true,
            targetRoute = Routes.roomRoute("r1"),
        ),
        DiscoverEvent(
            id = "ev_spy_clash",
            title = "Midnight Spy & Werewolf Clash",
            subtitle = "Ultimate deception showdown with 8-seat voice stage",
            schedule = "Tonight at 10:00 PM",
            prizePool = "Clan Trophy Badge",
            iconRes = R.drawable.vb_discover_explore,
            isLive = false,
            targetRoute = Routes.SPY,
        ),
    )

    fun getNearbyPlayers(): List<NearbyPlayer> = listOf(
        NearbyPlayer("near_1", "Aarav", 0.8, "Ludo & KTV", true, R.drawable.vb_discover_avatar_01, 8),
        NearbyPlayer("near_2", "Pooja", 1.4, "Werewolf Pro", true, R.drawable.vb_discover_avatar_03, 6),
        NearbyPlayer("near_3", "Rohan", 2.3, "Who's the Spy", false, R.drawable.vb_discover_avatar_05, 5),
        NearbyPlayer("near_4", "Meera", 3.1, "Party Lounge Host", true, R.drawable.vb_discover_avatar_07, 9),
    )
}
