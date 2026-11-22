package com.sundram.weplay.data

import android.content.Context
import com.sundram.weplay.ui.nav.Routes

enum class TaskCategory {
    DAILY,
    WEEKLY,
    NEW_PLAYER,
    COMMUNITY,
    ACHIEVEMENT,
}

enum class TaskStatus {
    IN_PROGRESS,
    CLAIMABLE,
    CLAIMED,
}

enum class CheckInStatus {
    LOCKED,
    AVAILABLE,
    CLAIMED,
}

data class TaskDefinition(
    val id: String,
    val title: String,
    val description: String,
    val category: TaskCategory,
    val iconEmoji: String,
    val xpReward: Int,
    val coinReward: Long,
    val targetCount: Int,
    val actionRoute: String,
    val actionLabel: String = "Go",
)

data class TaskItemVo(
    val definition: TaskDefinition,
    val currentCount: Int,
    val status: TaskStatus,
) {
    val progressFraction: Float
        get() = (currentCount.toFloat() / definition.targetCount.toFloat()).coerceIn(0f, 1f)
}

data class CheckInDayVo(
    val dayNumber: Int,
    val coinReward: Long,
    val xpReward: Int,
    val isRoyalBonus: Boolean = false,
    val status: CheckInStatus = CheckInStatus.LOCKED,
)

/**
 * 8 Starter Daily Missions.
 */
val STARTER_DAILY_MISSIONS: List<TaskDefinition> = listOf(
    TaskDefinition("d_01", "Open Games Screen", "Visit the games lobby today", TaskCategory.DAILY, "🎮", 20, 20, 1, Routes.HUB, "Go"),
    TaskDefinition("d_02", "Complete a Practice Game", "Play a round of Ludo or Who's the Spy", TaskCategory.DAILY, "🎲", 50, 50, 1, Routes.LUDO, "Play"),
    TaskDefinition("d_03", "Visit Voice Lounge", "Explore live public voice rooms", TaskCategory.DAILY, "🎙️", 30, 30, 1, Routes.ROOM, "Visit"),
    TaskDefinition("d_04", "Join Aurora Voice Stage", "Take a seat or listen in a voice lounge", TaskCategory.DAILY, "✨", 40, 40, 1, Routes.ROOM, "Join"),
    TaskDefinition("d_05", "Customize Your Profile", "Update your bio or avatar selection", TaskCategory.DAILY, "🎨", 30, 30, 1, Routes.PROFILE_EDIT, "Edit"),
    TaskDefinition("d_06", "Meet Community Players", "Browse and connect with other players", TaskCategory.DAILY, "👥", 25, 25, 1, Routes.HUB, "View"),
    TaskDefinition("d_07", "Preview a Gift Pack", "Inspect the 48-hour rotating bonus offers", TaskCategory.DAILY, "🎁", 35, 35, 1, Routes.HUB, "Check"),
    TaskDefinition("d_08", "Check Today's Rankings", "See the top players on the leaderboard", TaskCategory.DAILY, "🏆", 20, 20, 1, Routes.RANKING, "View"),
)

/**
 * 5 Starter Weekly Challenges.
 */
val STARTER_WEEKLY_CHALLENGES: List<TaskDefinition> = listOf(
    TaskDefinition("w_01", "Play 5 Practice Games", "Sharpen your gaming skills", TaskCategory.WEEKLY, "⚔️", 150, 200, 5, Routes.LUDO, "Play"),
    TaskDefinition("w_02", "3-Day Check-In Streak", "Check in on 3 separate days this week", TaskCategory.WEEKLY, "📅", 120, 150, 3, Routes.TASKS, "Check In"),
    TaskDefinition("w_03", "Complete 10 Daily Tasks", "Stay active across the social hub", TaskCategory.WEEKLY, "🌟", 200, 250, 10, Routes.TASKS, "Tasks"),
    TaskDefinition("w_04", "Send Room Greetings", "Post messages in voice room chats", TaskCategory.WEEKLY, "💬", 100, 100, 5, Routes.ROOM, "Visit"),
    TaskDefinition("w_05", "Follow 2 Community Friends", "Expand your gaming network", TaskCategory.WEEKLY, "🤝", 100, 100, 2, Routes.HUB, "Explore"),
)

/**
 * 6 New Player Onboarding Missions.
 */
val STARTER_NEW_PLAYER_MISSIONS: List<TaskDefinition> = listOf(
    TaskDefinition("np_01", "Complete Your Profile", "Set your nickname and greeting bio", TaskCategory.NEW_PLAYER, "📝", 50, 50, 1, Routes.PROFILE_EDIT, "Edit"),
    TaskDefinition("np_02", "Select an Animated Avatar", "Pick your favorite 2.5D character", TaskCategory.NEW_PLAYER, "🦊", 60, 60, 1, Routes.PROFILE_EDIT, "Pick"),
    TaskDefinition("np_03", "Explore Games Lobby", "Discover games and multiplayer rooms", TaskCategory.NEW_PLAYER, "🕹️", 30, 30, 1, Routes.HUB, "Explore"),
    TaskDefinition("np_04", "Visit Aurora Lounge", "Check out the celestial voice lounge", TaskCategory.NEW_PLAYER, "🌌", 40, 40, 1, Routes.ROOM, "Explore"),
    TaskDefinition("np_05", "Inspect Top Up Preview", "View the coin packages catalog", TaskCategory.NEW_PLAYER, "🪙", 30, 30, 1, Routes.TOPUP, "View"),
    TaskDefinition("np_06", "Read Community Guidelines", "Learn about safe & respectful play", TaskCategory.NEW_PLAYER, "📖", 30, 30, 1, Routes.SUPPORT_SETTINGS, "Read"),
)

/**
 * 7 Permanent Achievements.
 */
val STARTER_ACHIEVEMENTS: List<TaskDefinition> = listOf(
    TaskDefinition("ach_01", "First Victory", "Play and finish your first game", TaskCategory.ACHIEVEMENT, "🥇", 100, 100, 1, Routes.LUDO, "Play"),
    TaskDefinition("ach_02", "Social Butterfly", "Connect with 5 players in the community", TaskCategory.ACHIEVEMENT, "🦋", 150, 150, 5, Routes.HUB, "Explore"),
    TaskDefinition("ach_03", "Room Explorer", "Join 3 different voice rooms", TaskCategory.ACHIEVEMENT, "🚀", 120, 120, 3, Routes.ROOM, "Explore"),
    TaskDefinition("ach_04", "Gifting Novice", "Send your first in-room gift", TaskCategory.ACHIEVEMENT, "💝", 100, 100, 1, Routes.ROOM, "Visit"),
    TaskDefinition("ach_05", "7-Day Streak Master", "Complete a full 7-day check-in streak", TaskCategory.ACHIEVEMENT, "🔥", 300, 500, 7, Routes.TASKS, "Check In"),
    TaskDefinition("ach_06", "Level 5 Achiever", "Reach level 5 in VYBORA", TaskCategory.ACHIEVEMENT, "👑", 250, 300, 5, Routes.TASKS, "View"),
    TaskDefinition("ach_07", "Community Star", "Receive 10 messages from friends", TaskCategory.ACHIEVEMENT, "⭐", 200, 200, 10, Routes.MSG, "Chats"),
)

val ALL_TASKS: List<TaskDefinition> = STARTER_DAILY_MISSIONS + STARTER_WEEKLY_CHALLENGES + STARTER_NEW_PLAYER_MISSIONS + STARTER_ACHIEVEMENTS

/**
 * Task Engine Repository that manages progression, check-in streaks and rewards.
 */
class TaskStore(context: Context, private val userId: String) {

    private val prefs = context.getSharedPreferences("weplay_tasks_${userId}", Context.MODE_PRIVATE)

    fun getStreakDays(): Int = prefs.getInt("streak_days", 1)

    fun isTodayCheckedIn(): Boolean {
        val lastClaimTime = prefs.getLong("last_checkin_time", 0L)
        val now = System.currentTimeMillis()
        // Simple 24h day boundary check for demo mode
        return (now - lastClaimTime) < (24L * 60L * 60L * 1000L)
    }

    fun getCheckInDays(): List<CheckInDayVo> {
        val streak = getStreakDays()
        val todayChecked = isTodayCheckedIn()

        val dailyRewards = listOf(
            Pair(20L, 20),
            Pair(30L, 30),
            Pair(50L, 50),
            Pair(70L, 60),
            Pair(100L, 80),
            Pair(150L, 100),
            Pair(300L, 250), // Day 7 Royal Chest
        )

        return (1..7).map { day ->
            val reward = dailyRewards[day - 1]
            val status = when {
                day < streak || (day == streak && todayChecked) -> CheckInStatus.CLAIMED
                day == streak && !todayChecked -> CheckInStatus.AVAILABLE
                else -> CheckInStatus.LOCKED
            }
            CheckInDayVo(
                dayNumber = day,
                coinReward = reward.first,
                xpReward = reward.second,
                isRoyalBonus = day == 7,
                status = status,
            )
        }
    }

    fun claimDailyCheckIn(onRewardGranted: (coins: Long, xp: Int) -> Unit): Boolean {
        if (isTodayCheckedIn()) return false
        val currentStreak = getStreakDays()
        val nextStreak = if (currentStreak >= 7) 1 else currentStreak + 1

        val checkInDays = getCheckInDays()
        val todayVo = checkInDays.firstOrNull { it.status == CheckInStatus.AVAILABLE } ?: return false

        prefs.edit()
            .putLong("last_checkin_time", System.currentTimeMillis())
            .putInt("streak_days", nextStreak)
            .apply()

        onRewardGranted(todayVo.coinReward, todayVo.xpReward)
        return true
    }

    fun getTaskCount(taskId: String): Int = prefs.getInt("task_${taskId}_count", 0)

    fun isTaskClaimed(taskId: String): Boolean = prefs.getBoolean("task_${taskId}_claimed", false)

    fun incrementTask(taskId: String, by: Int = 1) {
        val current = getTaskCount(taskId)
        prefs.edit().putInt("task_${taskId}_count", current + by).apply()
    }

    fun claimTaskReward(taskId: String, onRewardGranted: (coins: Long, xp: Int) -> Unit): Boolean {
        if (isTaskClaimed(taskId)) return false

        val def = (STARTER_DAILY_MISSIONS + STARTER_WEEKLY_CHALLENGES + STARTER_NEW_PLAYER_MISSIONS + STARTER_ACHIEVEMENTS)
            .firstOrNull { it.id == taskId } ?: return false

        val count = getTaskCount(taskId)
        if (count < def.targetCount) return false

        prefs.edit().putBoolean("task_${taskId}_claimed", true).apply()
        onRewardGranted(def.coinReward, def.xpReward)
        return true
    }

    fun getMissions(definitions: List<TaskDefinition>): List<TaskItemVo> {
        return definitions.map { def ->
            val count = getTaskCount(def.id)
            val claimed = isTaskClaimed(def.id)
            val status = when {
                claimed -> TaskStatus.CLAIMED
                count >= def.targetCount -> TaskStatus.CLAIMABLE
                else -> TaskStatus.IN_PROGRESS
            }
            TaskItemVo(def, count, status)
        }
    }
}
