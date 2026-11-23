package com.sundram.weplay.platform.content

import com.sundram.weplay.data.GameCard
import com.sundram.weplay.data.RoomCard

data class FeedPost(
    val who: String,
    val text: String,
    val likes: Int,
    val comments: Int,
    val id: String = "",
    val mediaResId: Int? = null,
    val mediaUri: String? = null,
    val isVideo: Boolean = false,
    val userLevel: Int = 1,
    val timeAgo: String = "Just now",
    val title: String = "",
    val tag: String = "",
    val isLiked: Boolean = false,
)

data class FeedEvent(
    val who: String,
    val action: String,
    val time: String,
    val level: Int = 1,
    val avatarLetter: String = who.take(1).uppercase(),
    val iconEmoji: String? = null,
    val isDemo: Boolean = true,
)

data class HomeFeed(
    val games: List<GameCard>,
    val rooms: List<RoomCard>,
)

data class DiscoverFeed(
    val trending: List<String>,
    val events: List<FeedEvent>,
    val posts: List<FeedPost>,
)

interface ContentRepository {
    suspend fun homeFeed(): Result<HomeFeed>
    suspend fun discoverFeed(): Result<DiscoverFeed>
}
