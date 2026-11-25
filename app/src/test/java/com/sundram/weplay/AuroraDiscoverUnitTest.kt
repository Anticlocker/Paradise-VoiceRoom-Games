package com.sundram.weplay

import com.sundram.weplay.platform.content.DiscoverFeed
import com.sundram.weplay.platform.content.FeedEvent
import com.sundram.weplay.platform.content.FeedPost
import com.sundram.weplay.platform.content.LocalContentRepository
import com.sundram.weplay.ui.nav.Routes
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import java.io.File

/**
 * Comprehensive verification for Aurora Paradise 3.0 Discover Page Redesign.
 * Tests:
 * 1. Asset verification: All 23 original WebP assets imported in drawable-nodpi
 * 2. Route verification: Families, Live Events, and Discover navigation routes
 * 3. Contract backward compatibility: FeedPost and FeedEvent default arguments
 * 4. Repository verification: LocalContentRepository discoverFeed returns valid data with demo tags
 */
class AuroraDiscoverUnitTest {

    @Test
    fun testAllDiscoverWebpAssetsExist() {
        val resDir = File("src/main/res/drawable-nodpi")
        val requiredAssets = listOf(
            "ap_discover_header_palace.webp",
            "ap_discover_body_midnight.webp",
            "ap_discover_ic_families.webp",
            "ap_discover_ic_nearby.webp",
            "ap_discover_ic_clubs.webp",
            "ap_discover_ic_events.webp",
            "ap_discover_ic_moments.webp",
            "ap_discover_ic_activity.webp",
            "ap_discover_ic_community_star.webp",
            "ap_discover_ic_trending.webp",
            "ap_discover_ic_new_post.webp",
            "ap_discover_ic_heart.webp",
            "ap_discover_ic_comment.webp",
            "ap_discover_ic_share.webp",
            "ap_discover_ic_chevron.webp",
            "ap_discover_ic_gallery.webp",
            "ap_discover_ic_location.webp",
            "ap_discover_ic_empty.webp",
            "ap_discover_frame_violet.webp",
            "ap_discover_frame_cyan.webp",
            "ap_discover_frame_gold.webp",
            "ap_discover_fx_sparkle.webp",
            "ap_discover_media_fallback.webp",
            "ap_discover_moment_singer.png",
            "ap_discover_moment_friends.png",
            "ap_discover_moment_mic.png",
            "ap_discover_moment_heart.png",
            "ap_discover_preview_girl.png",
            "ap_discover_preview_twilight.png",
            "ap_discover_palace_moon.png",
        )

        for (asset in requiredAssets) {
            val file = File(resDir, asset)
            assertTrue("Asset $asset must exist in $resDir", file.exists())
            assertTrue("Asset $asset must be non-empty", file.length() > 0)
        }
    }

    @Test
    fun testNavigationRoutesForDiscoverShortcuts() {
        assertNotNull("Routes.DISCOVER must exist", Routes.DISCOVER)
        assertEquals("tab_discover", Routes.DISCOVER)

        assertNotNull("Routes.FAMILY must exist for Families shortcut", Routes.FAMILY)
        assertTrue(Routes.FAMILY.isNotEmpty())

        assertNotNull("Routes.EVENT_CENTER must exist for Live Events shortcut", Routes.EVENT_CENTER)
        assertTrue(Routes.EVENT_CENTER.isNotEmpty())
    }

    @Test
    fun testFeedPostBackwardCompatibility() {
        // Test legacy 4-param constructor still works
        val legacyPost = FeedPost("Sneha", "testing legacy", 10, 5)
        assertEquals("Sneha", legacyPost.who)
        assertEquals("testing legacy", legacyPost.text)
        assertEquals(10, legacyPost.likes)
        assertEquals(5, legacyPost.comments)
        assertEquals("", legacyPost.id)
        assertNull(legacyPost.mediaResId)
        assertFalse(legacyPost.isVideo)

        // Test extended constructor with rich properties
        val richPost = FeedPost(
            who = "Ankit",
            text = "Video moment",
            likes = 42,
            comments = 7,
            id = "test_vid",
            mediaResId = R.drawable.ap_discover_media_fallback,
            isVideo = true,
            userLevel = 19,
            timeAgo = "5m",
            title = "Acoustic Session",
        )
        assertEquals("test_vid", richPost.id)
        assertTrue(richPost.isVideo)
        assertEquals(19, richPost.userLevel)
        assertEquals("5m", richPost.timeAgo)
    }

    @Test
    fun testFeedEventBackwardCompatibilityAndDemoTag() {
        // Test legacy 3-param constructor
        val legacyEvent = FeedEvent("Ankit", "beat Sundram at Ludo", "2m")
        assertEquals("Ankit", legacyEvent.who)
        assertEquals("beat Sundram at Ludo", legacyEvent.action)
        assertEquals("2m", legacyEvent.time)
        assertEquals(1, legacyEvent.level)
        assertEquals("A", legacyEvent.avatarLetter)
        assertTrue(legacyEvent.isDemo)

        // Test extended constructor
        val richEvent = FeedEvent(
            who = "Priya",
            action = "found the spy in round one",
            time = "9m",
            level = 28,
            avatarLetter = "P",
            iconEmoji = "🎲",
            isDemo = true,
        )
        assertEquals("Priya", richEvent.who)
        assertEquals(28, richEvent.level)
        assertEquals("P", richEvent.avatarLetter)
        assertEquals("🎲", richEvent.iconEmoji)
        assertTrue(richEvent.isDemo)
    }

    @Test
    fun testLocalContentRepositoryDiscoverFeed() = runBlocking {
        val repo = LocalContentRepository()
        val result = repo.discoverFeed()
        assertTrue("Discover feed should succeed", result.isSuccess)

        val feed: DiscoverFeed = result.getOrThrow()
        assertNotNull(feed)
        assertTrue("Trending topics should not be empty", feed.trending.isNotEmpty())
        assertTrue("Events should not be empty", feed.events.isNotEmpty())
        assertTrue("Posts should not be empty", feed.posts.isNotEmpty())

        // Verify event structure matches expected layout
        val firstEvent = feed.events.first()
        assertEquals("Ankit", firstEvent.who)
        assertEquals("2m", firstEvent.time)
        assertEquals(19, firstEvent.level)

        val secondEvent = feed.events[1]
        assertEquals("Priya", secondEvent.who)
        assertEquals("9m", secondEvent.time)
        assertEquals(28, secondEvent.level)

        // Verify posts contain video indicator and preview moments
        val videoPost = feed.posts.find { it.isVideo }
        assertNotNull("There should be at least one video moment", videoPost)
    }
}
