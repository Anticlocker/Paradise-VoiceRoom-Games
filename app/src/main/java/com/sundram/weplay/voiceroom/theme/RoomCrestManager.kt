package com.sundram.weplay.voiceroom.theme

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

/**
 * Pure Kotlin Room Crest progression and anti-farming engine.
 *
 * Intentionally separate from Personal Level, VIP rank, and Family tiers.
 * Unlocking a Crest milestone opens theme eligibility cleanly without auto-equipping
 * themes behind the host's back.
 */
object CrestProgressionEngine {

    const val DAILY_XP_CAP = 1_200
    const val MAX_CREST_LEVEL = 25

    val MILESTONE_LEVELS = listOf(1, 3, 5, 7, 9, 11, 16, 21)

    // Cumulative XP threshold required to reach each Room Crest level (1..25).
    fun xpForLevel(level: Int): Long {
        val clamped = level.coerceIn(1, MAX_CREST_LEVEL)
        if (clamped <= 1) return 0L
        // Smooth monotonic progression:
        // Lv.3 -> 300, Lv.5 -> 1000, Lv.7 -> 2100, Lv.9 -> 3600, Lv.11 -> 5500, Lv.16 -> 12000, Lv.21 -> 21000
        val steps = clamped - 1
        return (50L * steps * (steps + 1)) / 2L
    }

    fun levelForXp(xp: Long): Int {
        val safeXp = xp.coerceAtLeast(0L)
        var lvl = 1
        while (lvl < MAX_CREST_LEVEL && safeXp >= xpForLevel(lvl + 1)) {
            lvl++
        }
        return lvl
    }

    data class CrestMutationOutcome(
        val progress: RoomCrestProgress,
        val accepted: Boolean,
        val leveledUp: Boolean,
        val newlyUnlockedThemeIds: List<String>,
        val rejectionReason: String? = null,
    )

    fun initialProgress(roomId: String): RoomCrestProgress = RoomCrestProgress(
        roomId = roomId,
        level = 1,
        xp = 0L,
        currentLevelFloorXp = 0L,
        nextLevelXp = xpForLevel(2),
        dailyXpEarned = 0,
        totalHostMinutes = 0,
        totalGiftsReceivedValue = 0L,
        completedMilestones = listOf(1),
        recentEvents = emptyList(),
    )

    fun applyEvent(
        current: RoomCrestProgress,
        eventId: String,
        type: RoomCrestEventType,
        actorId: String,
        hostId: String,
        requestedDelta: Int,
        summary: String,
        timestampMillis: Long,
        seenEventIds: Set<String>,
        alreadyUnlockedIds: Set<String>,
    ): CrestMutationOutcome {
        if (eventId.isBlank() || seenEventIds.contains(eventId)) {
            return CrestMutationOutcome(
                progress = current,
                accepted = false,
                leveledUp = false,
                newlyUnlockedThemeIds = emptyList(),
                rejectionReason = "Duplicate crest event ignored.",
            )
        }
        if (type == RoomCrestEventType.GIFT_VALUE_RECEIVED && actorId.isNotBlank() && actorId == hostId) {
            val rejectedRecord = RoomCrestEventRecord(
                id = eventId,
                roomId = current.roomId,
                type = type,
                xpDelta = 0,
                summary = summary,
                timestampMillis = timestampMillis,
                accepted = false,
                rejectionReason = "Self-gifting cannot earn Room Crest XP.",
            )
            return CrestMutationOutcome(
                progress = current.copy(recentEvents = (listOf(rejectedRecord) + current.recentEvents).take(25)),
                accepted = false,
                leveledUp = false,
                newlyUnlockedThemeIds = emptyList(),
                rejectionReason = rejectedRecord.rejectionReason,
            )
        }
        val remainingCap = (DAILY_XP_CAP - current.dailyXpEarned).coerceAtLeast(0)
        if (remainingCap == 0) {
            val cappedRecord = RoomCrestEventRecord(
                id = eventId,
                roomId = current.roomId,
                type = type,
                xpDelta = 0,
                summary = summary,
                timestampMillis = timestampMillis,
                accepted = false,
                rejectionReason = "Daily Room Crest XP cap ($DAILY_XP_CAP XP) reached.",
            )
            return CrestMutationOutcome(
                progress = current.copy(recentEvents = (listOf(cappedRecord) + current.recentEvents).take(25)),
                accepted = false,
                leveledUp = false,
                newlyUnlockedThemeIds = emptyList(),
                rejectionReason = cappedRecord.rejectionReason,
            )
        }

        val awardedXp = requestedDelta.coerceAtLeast(1).coerceAtMost(remainingCap)
        val nextXp = current.xp + awardedXp
        val nextLevel = levelForXp(nextXp)
        val leveledUp = nextLevel > current.level

        val newlyUnlocked = RoomThemeCatalog.entries
            .filter { spec ->
                spec.unlockType == ThemeUnlockType.CREST &&
                    spec.requiredCrestLevel != null &&
                    nextLevel >= spec.requiredCrestLevel &&
                    !alreadyUnlockedIds.contains(spec.id)
            }
            .map { it.id }

        val floorXp = xpForLevel(nextLevel)
        val nextGoalXp = if (nextLevel >= MAX_CREST_LEVEL) xpForLevel(MAX_CREST_LEVEL) else xpForLevel(nextLevel + 1)

        val acceptedRecord = RoomCrestEventRecord(
            id = eventId,
            roomId = current.roomId,
            type = type,
            xpDelta = awardedXp,
            summary = summary,
            timestampMillis = timestampMillis,
            accepted = true,
        )

        val updated = current.copy(
            level = nextLevel,
            xp = nextXp,
            currentLevelFloorXp = floorXp,
            nextLevelXp = nextGoalXp,
            dailyXpEarned = current.dailyXpEarned + awardedXp,
            totalHostMinutes = current.totalHostMinutes + if (type == RoomCrestEventType.HOST_SESSION_MINUTE) 1 else 0,
            totalGiftsReceivedValue = current.totalGiftsReceivedValue + if (type == RoomCrestEventType.GIFT_VALUE_RECEIVED) requestedDelta.toLong() else 0L,
            completedMilestones = MILESTONE_LEVELS.filter { nextLevel >= it },
            recentEvents = (listOf(acceptedRecord) + current.recentEvents).take(25),
        )

        return CrestMutationOutcome(
            progress = updated,
            accepted = true,
            leveledUp = leveledUp,
            newlyUnlockedThemeIds = newlyUnlocked,
        )
    }

    fun upcomingCrestThemes(level: Int): List<RoomThemeSpec> =
        RoomThemeCatalog.entries
            .filter { it.unlockType == ThemeUnlockType.CREST && (it.requiredCrestLevel ?: 0) > level }
            .sortedBy { it.requiredCrestLevel ?: Int.MAX_VALUE }
}

class RoomCrestPreferencesStore(context: Context) {

    private val prefs = context.applicationContext.getSharedPreferences("weplay_room_crest", Context.MODE_PRIVATE)

    fun load(roomId: String): RoomCrestProgress {
        val raw = prefs.getString("progress_$roomId", null) ?: return CrestProgressionEngine.initialProgress(roomId)
        return runCatching {
            val obj = JSONObject(raw)
            val xp = obj.optLong("xp", 0L)
            val lvl = CrestProgressionEngine.levelForXp(xp)
            val eventsArr = obj.optJSONArray("events") ?: JSONArray()
            val events = buildList {
                for (i in 0 until eventsArr.length()) {
                    val item = eventsArr.getJSONObject(i)
                    add(
                        RoomCrestEventRecord(
                            id = item.getString("id"),
                            roomId = roomId,
                            type = runCatching { RoomCrestEventType.valueOf(item.getString("type")) }
                                .getOrDefault(RoomCrestEventType.MODERATOR_ACTIVITY),
                            xpDelta = item.optInt("xpDelta", 0),
                            summary = item.optString("summary", ""),
                            timestampMillis = item.optLong("ts", 0L),
                            accepted = item.optBoolean("accepted", true),
                            rejectionReason = item.optString("reason", "").ifBlank { null },
                        )
                    )
                }
            }
            RoomCrestProgress(
                roomId = roomId,
                level = lvl,
                xp = xp,
                currentLevelFloorXp = CrestProgressionEngine.xpForLevel(lvl),
                nextLevelXp = if (lvl >= CrestProgressionEngine.MAX_CREST_LEVEL) {
                    CrestProgressionEngine.xpForLevel(CrestProgressionEngine.MAX_CREST_LEVEL)
                } else {
                    CrestProgressionEngine.xpForLevel(lvl + 1)
                },
                dailyXpEarned = obj.optInt("dailyXp", 0),
                totalHostMinutes = obj.optInt("hostMinutes", 0),
                totalGiftsReceivedValue = obj.optLong("giftsValue", 0L),
                completedMilestones = CrestProgressionEngine.MILESTONE_LEVELS.filter { lvl >= it },
                recentEvents = events,
            )
        }.getOrElse { CrestProgressionEngine.initialProgress(roomId) }
    }

    fun save(progress: RoomCrestProgress) {
        val eventsArr = JSONArray()
        progress.recentEvents.forEach { ev ->
            eventsArr.put(
                JSONObject().apply {
                    put("id", ev.id)
                    put("type", ev.type.name)
                    put("xpDelta", ev.xpDelta)
                    put("summary", ev.summary)
                    put("ts", ev.timestampMillis)
                    put("accepted", ev.accepted)
                    if (ev.rejectionReason != null) put("reason", ev.rejectionReason)
                }
            )
        }
        val payload = JSONObject().apply {
            put("xp", progress.xp)
            put("dailyXp", progress.dailyXpEarned)
            put("hostMinutes", progress.totalHostMinutes)
            put("giftsValue", progress.totalGiftsReceivedValue)
            put("events", eventsArr)
        }
        prefs.edit().putString("progress_${progress.roomId}", payload.toString()).apply()
    }

    fun loadSeenEventIds(roomId: String): Set<String> =
        prefs.getStringSet("seen_$roomId", emptySet()) ?: emptySet()

    fun addSeenEventId(roomId: String, eventId: String) {
        val next = loadSeenEventIds(roomId).toMutableSet()
        next.add(eventId)
        // Cap locally stored dedup keys to last 120 to bound preference file size.
        val bounded = if (next.size > 120) next.toList().takeLast(120).toSet() else next
        prefs.edit().putStringSet("seen_$roomId", bounded).apply()
    }
}
