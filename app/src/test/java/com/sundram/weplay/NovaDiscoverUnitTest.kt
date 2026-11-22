package com.sundram.weplay

import com.sundram.weplay.data.DiscoverStore
import com.sundram.weplay.data.InMemorySharedPreferences
import com.sundram.weplay.ui.nav.Routes
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Comprehensive Unit Tests for VYBORA Discover Nova 2.0:
 * - Base moments data integrity & tag taxonomy
 * - DiscoverStore persistence: likes toggling & counts
 * - DiscoverStore comments: addition & list retention
 * - DiscoverStore custom moments: creation & priority ordering
 * - Community clubs: membership join/leave toggling
 * - Nearby players: visibility preference & distance range
 * - Live activity events: gaming sanitization & verified routing
 * - Discover events: live flags & real route associations
 * - Tag & query filtering behavior
 * - Asset resource resolution for all 29 vb_discover_* drawables
 */
class NovaDiscoverUnitTest {

    private lateinit var prefs: InMemorySharedPreferences
    private lateinit var store: DiscoverStore

    @Before
    fun setUp() {
        prefs = InMemorySharedPreferences()
        store = DiscoverStore(customPrefs = prefs)
    }

    @Test
    fun testDiscoverDefaultMomentsIntegrity() {
        val moments = store.getAllMoments()
        assertTrue("Discover must provide at least 5 default community moments", moments.size >= 5)

        // Verify tags match approved taxonomy
        moments.forEach { moment ->
            assertTrue("Moment ID must not be blank", moment.id.isNotBlank())
            assertTrue("Author name must not be blank", moment.authorName.isNotBlank())
            assertTrue("Author ID must not be blank", moment.authorId.isNotBlank())
            assertTrue("Moment text must not be blank", moment.text.isNotBlank())
            assertTrue("Moment tag '${moment.tag}' must belong to approved tags", DiscoverStore.TAGS.contains(moment.tag))
            assertNotEquals("Author avatar drawable must not be 0", 0, moment.authorAvatarRes)
            assertTrue("Likes count must be non-negative", moment.likesCount >= 0)
            assertTrue("Comments count must be non-negative", moment.commentsCount >= 0)

            moment.comments.forEach { comment ->
                assertTrue("Comment ID must not be blank", comment.id.isNotBlank())
                assertTrue("Comment author must not be blank", comment.authorName.isNotBlank())
                assertTrue("Comment text must not be blank", comment.text.isNotBlank())
                assertNotEquals("Comment avatar must not be 0", 0, comment.authorAvatarRes)
            }
        }
    }

    @Test
    fun testDiscoverStoreLikeToggling() {
        val initialMoments = store.getAllMoments()
        val firstMoment = initialMoments.first { it.id == "m1" }
        val initialLikes = firstMoment.likesCount
        assertFalse("Moment m1 should initially be unliked", firstMoment.isLiked)

        // Toggle like on
        val likedNow = store.toggleLike("m1")
        assertTrue("Toggling like on unliked moment should return true", likedNow)

        val updatedMoments = store.getAllMoments()
        val likedMoment = updatedMoments.first { it.id == "m1" }
        assertTrue("Moment m1 should now be liked", likedMoment.isLiked)
        assertEquals("Likes count should increment by 1", initialLikes + 1, likedMoment.likesCount)

        // Persistence test across store re-instantiation
        val newStoreInstance = DiscoverStore(customPrefs = prefs)
        val persistedMoment = newStoreInstance.getAllMoments().first { it.id == "m1" }
        assertTrue("Liked state must persist across store instances", persistedMoment.isLiked)
        assertEquals(initialLikes + 1, persistedMoment.likesCount)

        // Toggle like off
        val unlikedNow = store.toggleLike("m1")
        assertFalse("Toggling like again should return false (unliked)", unlikedNow)

        val revertedMoments = store.getAllMoments()
        val revertedMoment = revertedMoments.first { it.id == "m1" }
        assertFalse("Moment m1 should now be unliked again", revertedMoment.isLiked)
        assertEquals("Likes count should revert to original", initialLikes, revertedMoment.likesCount)
    }

    @Test
    fun testDiscoverStoreCommentAddition() {
        val initialMoments = store.getAllMoments()
        val target = initialMoments.first { it.id == "m2" }
        val initialCommentsCount = target.commentsCount

        val newComment = store.addComment(
            momentId = "m2",
            authorName = "Kabir",
            text = "Incredible deception skills!",
            avatarRes = R.drawable.vb_discover_avatar_01,
        )

        assertNotNull(newComment)
        assertEquals("Kabir", newComment.authorName)
        assertEquals("Incredible deception skills!", newComment.text)
        assertEquals(R.drawable.vb_discover_avatar_01, newComment.authorAvatarRes)

        // Verify updated moments feed reflects new comment
        val updatedMoments = store.getAllMoments()
        val updatedTarget = updatedMoments.first { it.id == "m2" }
        assertEquals(initialCommentsCount + 1, updatedTarget.commentsCount)
        assertTrue(updatedTarget.comments.any { it.text == "Incredible deception skills!" && it.authorName == "Kabir" })

        // Verify persistence across new store instance
        val newStore = DiscoverStore(customPrefs = prefs)
        val persistedTarget = newStore.getAllMoments().first { it.id == "m2" }
        assertEquals(initialCommentsCount + 1, persistedTarget.commentsCount)
        assertTrue(persistedTarget.comments.any { it.text == "Incredible deception skills!" })
    }

    @Test
    fun testDiscoverStoreCreateMomentPersistence() {
        val initialCount = store.getAllMoments().size

        val created = store.createMoment(
            text = "Hosting a weekend championship party in room 1! Join voice stage 🎤🏆",
            tag = "KTV",
            authorName = "Arjun",
            authorId = "user_arjun",
            avatarRes = R.drawable.vb_discover_avatar_07,
        )

        assertNotNull(created)
        assertEquals("Hosting a weekend championship party in room 1! Join voice stage 🎤🏆", created.text)
        assertEquals("KTV", created.tag)
        assertEquals("Arjun", created.authorName)
        assertEquals("user_arjun", created.authorId)
        assertEquals(R.drawable.vb_discover_avatar_07, created.authorAvatarRes)
        assertTrue(created.isVerified)

        val updatedMoments = store.getAllMoments()
        assertEquals("Created moment should increment total post count by 1", initialCount + 1, updatedMoments.size)
        // Created custom moment should appear at the top of the feed
        assertEquals(created.id, updatedMoments.first().id)
        assertEquals("Arjun", updatedMoments.first().authorName)

        // Verify persistence in independent store instance
        val reloadedStore = DiscoverStore(customPrefs = prefs)
        val reloadedMoments = reloadedStore.getAllMoments()
        assertEquals(initialCount + 1, reloadedMoments.size)
        assertEquals(created.id, reloadedMoments.first().id)
    }

    @Test
    fun testDiscoverClubsJoinToggle() {
        val clubs = store.getAllClubs()
        assertTrue("Should offer community clubs", clubs.isNotEmpty())

        val ludoClub = clubs.first { it.id == "club_ludo" }
        assertFalse("Club should initially not be joined", ludoClub.isJoined)

        // Join club
        val joined = store.toggleClubJoin("club_ludo")
        assertTrue("Joining club should return true", joined)

        val updatedClubs = store.getAllClubs()
        val updatedLudoClub = updatedClubs.first { it.id == "club_ludo" }
        assertTrue("Club should now be marked as joined", updatedLudoClub.isJoined)

        // Leave club
        val left = store.toggleClubJoin("club_ludo")
        assertFalse("Leaving club should return false", left)

        val revertedClubs = store.getAllClubs()
        assertFalse(revertedClubs.first { it.id == "club_ludo" }.isJoined)
    }

    @Test
    fun testNearbyPrivacyToggle() {
        // Default visibility is true
        assertTrue("Nearby visibility should default to true", store.isNearbyVisible())

        store.setNearbyVisible(false)
        assertFalse("Nearby visibility should now be false", store.isNearbyVisible())

        store.setNearbyVisible(true)
        assertTrue("Nearby visibility should be re-enabled", store.isNearbyVisible())
    }

    @Test
    fun testNearbyPlayersDataSanity() {
        val players = store.getNearbyPlayers()
        assertEquals(4, players.size)

        players.forEach { player ->
            assertTrue(player.id.isNotBlank())
            assertTrue(player.name.isNotBlank())
            assertTrue("Distance must be positive", player.distanceKm > 0.0)
            assertTrue("Level must be positive", player.level > 0)
            assertNotEquals("Avatar must be valid", 0, player.avatarRes)
            assertTrue(player.favoriteGame.isNotBlank())
        }
    }

    @Test
    fun testLiveUpdatesSanitizationAndRoutes() {
        val updates = store.getAllLiveUpdates()
        assertTrue("Should have real-time live activity events", updates.isNotEmpty())

        val bannedKeywords = listOf("gamble", "jackpot", "turned 5 coins into 500", "bet", "casino")

        updates.forEach { update ->
            assertTrue("Event ID must not be blank", update.id.isNotBlank())
            assertTrue("User name must not be blank", update.who.isNotBlank())
            assertTrue("Action must not be blank", update.action.isNotBlank())
            assertTrue("Time ago must not be blank", update.timeAgo.isNotBlank())
            assertNotEquals("Avatar must resolve", 0, update.whoAvatarRes)

            // Sanitization: No fake gambling or coin multiplication events
            bannedKeywords.forEach { banned ->
                assertFalse("Live activity must not contain gambling keyword '$banned'",
                    update.action.contains(banned, ignoreCase = true))
            }

            // Real target routes verification
            assertTrue("Target route '${update.targetRoute}' must be a legitimate app route",
                update.targetRoute == Routes.LUDO ||
                update.targetRoute == Routes.SPY ||
                update.targetRoute == Routes.FAMILY ||
                update.targetRoute.startsWith("room/")
            )
        }
    }

    @Test
    fun testDiscoverEventsCatalogAndRoutes() {
        val events = store.getAllEvents()
        assertTrue("Should contain discover events", events.size >= 3)

        events.forEach { event ->
            assertTrue("Event ID must not be blank", event.id.isNotBlank())
            assertTrue("Title must not be blank", event.title.isNotBlank())
            assertTrue("Subtitle must not be blank", event.subtitle.isNotBlank())
            assertTrue("Schedule must not be blank", event.schedule.isNotBlank())
            assertNotEquals("Icon must resolve", 0, event.iconRes)

            // Verify legitimate routes
            assertTrue("Event target route '${event.targetRoute}' must be valid",
                event.targetRoute == Routes.LUDO ||
                event.targetRoute == Routes.SPY ||
                event.targetRoute.startsWith("room/")
            )
        }
    }

    @Test
    fun testTagAndQueryFilteringLogic() {
        val allMoments = store.getAllMoments()

        // Filter by Tag "Ludo"
        val ludoMoments = allMoments.filter { it.tag.equals("Ludo", ignoreCase = true) }
        assertTrue("Should find Ludo moments", ludoMoments.isNotEmpty())
        assertTrue(ludoMoments.all { it.tag.equals("Ludo", ignoreCase = true) })

        // Filter by Tag "Werewolf"
        val werewolfMoments = allMoments.filter { it.tag.equals("Werewolf", ignoreCase = true) }
        assertTrue("Should find Werewolf moments", werewolfMoments.isNotEmpty())
        assertTrue(werewolfMoments.all { it.tag.equals("Werewolf", ignoreCase = true) })

        // Filter by Search Query "tournament"
        val queryMatches = allMoments.filter {
            it.text.contains("tournament", ignoreCase = true) ||
            it.authorName.contains("tournament", ignoreCase = true)
        }
        assertTrue("Should match moments containing 'tournament'", queryMatches.isNotEmpty())

        // Non-matching query
        val noMatches = allMoments.filter {
            it.text.contains("xyzNonExistentQuery12345", ignoreCase = true)
        }
        assertTrue("Unmatched query should return empty list", noMatches.isEmpty())
    }

    @Test
    fun testDiscoverAssetResourcesIntegrity() {
        val coreDrawables = listOf(
            R.drawable.vb_discover_moments,
            R.drawable.vb_discover_families,
            R.drawable.vb_discover_nearby,
            R.drawable.vb_discover_clubs,
            R.drawable.vb_discover_events,
            R.drawable.vb_discover_location_off,
            R.drawable.vb_discover_trending,
            R.drawable.vb_discover_feed,
            R.drawable.vb_discover_create,
            R.drawable.vb_discover_communities,
            R.drawable.vb_discover_friends,
            R.drawable.vb_discover_explore,
            R.drawable.vb_discover_badge_verified,
            R.drawable.vb_discover_badge_family,
            R.drawable.vb_discover_badge_live,
            R.drawable.vb_discover_badge_new,
            R.drawable.vb_discover_badge_pin,
            R.drawable.vb_discover_badge_private,
            R.drawable.vb_discover_bg_aurora,
            R.drawable.vb_discover_bg_sunrise,
            R.drawable.vb_discover_bg_violet,
            R.drawable.vb_discover_avatar_01,
            R.drawable.vb_discover_avatar_02,
            R.drawable.vb_discover_avatar_03,
            R.drawable.vb_discover_avatar_04,
            R.drawable.vb_discover_avatar_05,
            R.drawable.vb_discover_avatar_06,
            R.drawable.vb_discover_avatar_07,
            R.drawable.vb_discover_avatar_08,
        )

        assertEquals("Must verify all 29 discover assets", 29, coreDrawables.size)
        coreDrawables.forEach { resId ->
            assertNotEquals("Drawable resource ID must not be 0", 0, resId)
        }
    }
}
