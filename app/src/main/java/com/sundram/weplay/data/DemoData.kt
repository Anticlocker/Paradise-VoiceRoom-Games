package com.sundram.weplay.data

import androidx.annotation.DrawableRes
import com.sundram.weplay.R

data class GameCard(
    val id: String,
    val title: String,
    val desc: String,
    val tag: String? = null,
    val playable: Boolean = false,
)

data class RoomCard(
    val id: String,
    val title: String,
    val host: String,
    val members: Int,
    val tag: String,
    val live: Boolean = true,
    @DrawableRes val coverRes: Int? = null,
    val badges: List<Int> = emptyList(),
    val activityText: String? = null,
)

data class ChatItem(
    val id: String,
    val name: String,
    val lastMsg: String,
    val time: String,
    val unread: Int = 0,
)

data class StageGiftItem(
    val id: String,
    val name: String,
    val emoji: String,
    val tier: Int, // 1=small, 2=premium, 3=luxury
    val coinCost: Int,
)

data class NotificationItem(
    val id: String,
    val type: String, // "friend_request", "family_invite", "room_invite", "ranking_reward"
    val title: String,
    val body: String,
    val timestamp: String,
    val read: Boolean = false,
    val destinationRoute: String? = null,
)

data class FamilyModel(
    val id: String,
    val name: String,
    val leaderId: String,
    val leaderName: String,
    val members: Int,
    val level: Int,
    val xp: Int,
    val maxXp: Int,
    val tag: String,
)

data class LeaderboardEntry(
    val rank: Int,
    val userId: String,
    val name: String,
    val score: Long,
    val tier: String, // "wealth", "charm", "game"
)

object Demo {
    val games = listOf(
        GameCard("ludo", "Ludo", "Board · vs bots", "HOT", playable = true),
        GameCard("spy", "Who's the Spy", "Deduction · pass & play", "NEW", playable = true),
        GameCard("draw", "Draw & Guess", "Party · drawing"),
        GameCard("bingo", "Bingo", "Luck · numbers"),
        GameCard("sing", "Mic Grab", "Voice · music"),
        GameCard("hum", "Guess the Hum", "Music · guess"),
        GameCard("squid", "Squid Run", "Action · survival"),
        GameCard("sheep", "Sheep Merge", "Puzzle · merge"),
    )

    val rooms = listOf(
        RoomCard("r1", "Tea Time Talks", "Priya", 7, "Friends", coverRes = R.drawable.vb_room_cover_moonlight, badges = listOf(R.drawable.vb_room_badge_gold, R.drawable.vb_room_badge_sapphire), activityText = "Chit-chat & Tea"),
        RoomCard("r2", "Werewolf Night", "Raj", 10, "PK", coverRes = R.drawable.vb_room_cover_werewolf_night, badges = listOf(R.drawable.vb_room_badge_ruby, R.drawable.vb_room_badge_shield), activityText = "Social Deduction"),
        RoomCard("r3", "Music & Chill", "Ankit", 4, "Music", coverRes = R.drawable.vb_room_cover_karaoke_stage, badges = listOf(R.drawable.vb_room_badge_music, R.drawable.vb_room_badge_star), activityText = "Live Acoustic"),
        RoomCard("r4", "Desi Gamers", "Sam", 6, "Game", coverRes = R.drawable.vb_room_cover_beast_wars, badges = listOf(R.drawable.vb_room_badge_flame, R.drawable.vb_room_badge_diamond), activityText = "Ranked Match"),
        RoomCard("r5", "Late Night Vibes", "Sneha", 2, "Chats", coverRes = R.drawable.vb_room_cover_beach_party, badges = listOf(R.drawable.vb_room_badge_emerald, R.drawable.vb_room_badge_gold), activityText = "Chillout Lounge"),
        RoomCard("r6", "Sing-Along Stage", "Kavya", 9, "Music", coverRes = R.drawable.vb_room_cover_pop_star, badges = listOf(R.drawable.vb_room_badge_royal, R.drawable.vb_room_badge_music), activityText = "Karaoke Contest"),
        RoomCard("r7", "Royal High Stakes", "Vikram", 14, "Game", coverRes = R.drawable.vb_room_cover_royal_casino, badges = listOf(R.drawable.vb_room_badge_royal, R.drawable.vb_room_badge_gold), activityText = "VIP Cards"),
        RoomCard("r8", "8-Ball Billiards Lounge", "Arjun", 8, "Game", coverRes = R.drawable.vb_room_cover_billiards_lounge, badges = listOf(R.drawable.vb_room_badge_diamond, R.drawable.vb_room_badge_ice), activityText = "Tournament"),
        RoomCard("r9", "Crown VIP Cinema", "Natasha", 18, "Video", coverRes = R.drawable.vb_room_cover_royal_crown, badges = listOf(R.drawable.vb_room_badge_royal, R.drawable.vb_room_badge_ruby, R.drawable.vb_room_badge_sapphire), activityText = "Movie Night"),
    )

    val chats = listOf(
        ChatItem("c1", "Ankit", "match tomorrow?", "2m", 2),
        ChatItem("c2", "Tea Time Talks", "Priya: listen to this", "12m", 1),
        ChatItem("c3", "Sneha", "you there?", "1h"),
        ChatItem("c4", "Werewolf Night", "game starts soon", "3h"),
        ChatItem("c5", "Family: Desi Kings", "complete the task!", "5h", 3),
    )

    val gifts = listOf(
        StageGiftItem("g1", "Rose", "🌹", 1, 10),
        StageGiftItem("g2", "Candy", "🍬", 1, 15),
        StageGiftItem("g3", "Star", "⭐", 1, 25),
        StageGiftItem("g4", "Heart", "❤️", 1, 50),
        StageGiftItem("g5", "Diamond", "💎", 2, 199),
        StageGiftItem("g6", "Crown", "👑", 2, 299),
        StageGiftItem("g7", "Trophy", "🏆", 2, 499),
        StageGiftItem("g8", "Rocket", "🚀", 2, 599),
        StageGiftItem("g9", "Galaxy", "🌌", 3, 1999),
        StageGiftItem("g10", "Phoenix", "🦅", 3, 4999),
        StageGiftItem("g11", "Dragon", "🐉", 3, 9999),
        StageGiftItem("g12", "Universe", "✨", 3, 19999),
    )

    val notifications = listOf(
        NotificationItem("n1", "friend_request", "New Friend Request", "Ankit wants to be your friend", "2m"),
        NotificationItem("n2", "room_invite", "Room Invitation", "Priya invited you to Tea Time Talks", "15m", destinationRoute = "room/r1"),
        NotificationItem("n3", "ranking_reward", "Ranking Reward", "You reached Top 100 this week!", "1h", read = true),
        NotificationItem("n4", "family_invite", "Family Invitation", "Join 'Desi Kings' family?", "3h"),
    )

    val families = listOf(
        FamilyModel("f1", "Desi Kings", "u1", "Raj", 24, 8, 3200, 5000, "Gaming"),
        FamilyModel("f2", "Night Owls", "u2", "Priya", 15, 5, 1800, 3000, "Casual"),
        FamilyModel("f3", "Voice Squad", "u3", "Ankit", 31, 12, 7500, 10000, "Voice"),
    )

    val leaderboard = listOf(
        LeaderboardEntry(1, "u_top1", "ZeynepK", 128500, "wealth"),
        LeaderboardEntry(2, "u_top2", "RajKing", 98300, "wealth"),
        LeaderboardEntry(3, "u_top3", "AnkitPro", 76200, "wealth"),
        LeaderboardEntry(4, "u_top4", "PriyaStar", 54100, "wealth"),
        LeaderboardEntry(5, "u_top5", "SamVoice", 43800, "wealth"),
    )
}
