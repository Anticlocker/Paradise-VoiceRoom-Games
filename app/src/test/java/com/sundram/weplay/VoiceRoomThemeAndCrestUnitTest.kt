package com.sundram.weplay

import com.sundram.weplay.voiceroom.DemoVoiceRoomEngine
import com.sundram.weplay.voiceroom.HOST_SEAT_INDEX
import com.sundram.weplay.voiceroom.PARTNER_SEAT_INDEX
import com.sundram.weplay.voiceroom.REGULAR_SEAT_END
import com.sundram.weplay.voiceroom.REGULAR_SEAT_START
import com.sundram.weplay.voiceroom.VOICE_ROOM_STAGE_CAPACITY
import com.sundram.weplay.voiceroom.theme.CLASSIC_LOUNGE_ID
import com.sundram.weplay.voiceroom.theme.CreatedRoomRecord
import com.sundram.weplay.voiceroom.theme.CrestProgressionEngine
import com.sundram.weplay.voiceroom.theme.InMemoryRoomThemeStorage
import com.sundram.weplay.voiceroom.theme.RoomCrestEventType
import com.sundram.weplay.voiceroom.theme.RoomThemeCatalog
import com.sundram.weplay.voiceroom.theme.RoomThemeRepository
import com.sundram.weplay.voiceroom.theme.RoomThemeResourceRegistry
import com.sundram.weplay.voiceroom.theme.RoomThemeTab
import com.sundram.weplay.voiceroom.theme.ThemeActionResult
import com.sundram.weplay.voiceroom.theme.ThemeUnlockType
import java.io.File
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class VoiceRoomThemeAndCrestUnitTest {

    @Test
    fun testAll10ThemesAnd50DrawablesRegistered() {
        assertEquals(10, RoomThemeCatalog.entries.size)
        val registeredIds = RoomThemeResourceRegistry.allRegisteredThemeIds()
        assertEquals(10, registeredIds.size)

        for (spec in RoomThemeCatalog.entries) {
            assertTrue("Missing registry mapping for ${spec.id}", registeredIds.contains(spec.id))
            val res = RoomThemeResourceRegistry.resourcesFor(spec.id)
            assertTrue("backgroundRes missing for ${spec.id}", res.backgroundRes != 0)
            assertTrue("thumbnailRes missing for ${spec.id}", res.thumbnailRes != 0)
            assertTrue("seatFrameRes missing for ${spec.id}", res.seatFrameRes != 0)
            assertTrue("hostFrameRes missing for ${spec.id}", res.hostFrameRes != 0)
            assertTrue("overlayRes missing for ${spec.id}", res.overlayRes != 0)
        }
    }

    @Test
    fun testCatalogMatchesImportedAssetJsonExactly() {
        val candidates = listOf(
            File("app/src/main/assets/room_theme_catalog.json"),
            File("src/main/assets/room_theme_catalog.json"),
        )
        val assetJsonFile = candidates.firstOrNull { it.exists() }
        assertTrue("Expected room_theme_catalog.json in app assets", assetJsonFile != null)

        val parsed = RoomThemeCatalog.parseCatalogJson(assetJsonFile!!.readText())
        assertEquals(RoomThemeCatalog.entries, parsed)
    }

    @Test
    fun testClassicLoungeIsSoleDefaultFreeThemeAndAllOthersLockedByDefault() {
        val repo = RoomThemeRepository(InMemoryRoomThemeStorage())
        assertEquals(CLASSIC_LOUNGE_ID, repo.getEquippedThemeId("fresh_room_999"))

        val catalog = repo.buildCatalogForRoom("fresh_room_999", userId = "u1", crestLevel = 1)
        val classic = catalog.first { it.spec.id == "classic_lounge" }
        assertTrue(classic.isEquipped)
        assertTrue(classic.isOwned)
        assertTrue(classic.canEquip)
        assertEquals(ThemeUnlockType.FREE, classic.spec.unlockType)
        assertNull(classic.lockReason)

        val lockedCrest = catalog.filter { it.spec.unlockType == ThemeUnlockType.CREST }
        assertEquals(7, lockedCrest.size)
        lockedCrest.forEach { item ->
            assertFalse("${item.spec.id} must be locked at Crest Lv.1", item.canEquip)
            assertTrue(item.lockReason?.contains("Crest Lv.${item.spec.requiredCrestLevel}") == true)
        }

        val storeThemes = catalog.filter { it.spec.unlockType == ThemeUnlockType.STORE }
        assertEquals(listOf("sakura_garden", "rose_palace"), storeThemes.map { it.spec.id })
        storeThemes.forEach { item ->
            assertFalse("${item.spec.id} must be locked by default", item.canEquip)
            assertNull("${item.spec.id} priceCoins must remain null until priced", item.spec.priceCoins)
            assertFalse(item.isPurchaseConfigured)
        }
    }

    @Test
    fun testFourGalleryTabsFilterAccurately() {
        val repo = RoomThemeRepository(InMemoryRoomThemeStorage())
        val items = repo.buildCatalogForRoom("r1", "u1", crestLevel = 3)
        assertEquals(10, repo.filterByTab(items, RoomThemeTab.ALL).size)
        assertEquals(listOf("classic_lounge", "ocean_lounge"), repo.filterByTab(items, RoomThemeTab.MY_THEMES).map { it.spec.id })
        assertEquals(7, repo.filterByTab(items, RoomThemeTab.CREST_REWARDS).size)
        assertEquals(2, repo.filterByTab(items, RoomThemeTab.STORE_EXCLUSIVES).size)
    }

    @Test
    fun testEquipAuthorizationAndLockEnforcement() {
        val repo = RoomThemeRepository(InMemoryRoomThemeStorage())

        // Unauthorized listener cannot change theme even for unlocked classic_lounge
        val unauth = repo.equipTheme("r1", "classic_lounge", "u_guest", crestLevel = 10, isHostOrAuthorized = false)
        assertTrue(unauth is ThemeActionResult.Unauthorized)

        // Locked Crest theme cannot be equipped before reaching level
        val locked = repo.equipTheme("r1", "aurora_palace", "u_host", crestLevel = 5, isHostOrAuthorized = true)
        assertTrue(locked is ThemeActionResult.Locked)
        assertEquals(CLASSIC_LOUNGE_ID, repo.getEquippedThemeId("r1"))

        // Reaching Crest Lv.9 unlocks Aurora Palace and allows explicit equip
        val unlocked = repo.equipTheme("r1", "aurora_palace", "u_host", crestLevel = 9, isHostOrAuthorized = true)
        assertTrue(unlocked is ThemeActionResult.Success)
        assertEquals("aurora_palace", repo.getEquippedThemeId("r1"))
    }

    @Test
    fun testNullStorePriceIsNeverTreatedAsZeroCoins() {
        val repo = RoomThemeRepository(InMemoryRoomThemeStorage())
        var wallet = 10_000L

        val sakuraRes = repo.purchaseStoreTheme("sakura_garden", "u1", wallet) { cost ->
            wallet -= cost
            true
        }
        assertTrue("Null priceCoins must return Unavailable, never 0-coin purchase", sakuraRes is ThemeActionResult.Unavailable)
        assertEquals(10_000L, wallet)
    }

    @Test
    fun testConfiguredStoreThemePurchaseDeductsOnceAndChecksInsufficientFunds() {
        val pricedCatalog = RoomThemeCatalog.entries.map {
            if (it.id == "sakura_garden") it.copy(priceCoins = 800) else it
        }
        val repo = RoomThemeRepository(InMemoryRoomThemeStorage()) { pricedCatalog }
        var wallet = 500L

        val poor = repo.purchaseStoreTheme("sakura_garden", "u1", wallet) { true }
        assertTrue(poor is ThemeActionResult.InsufficientCoins)
        assertEquals(500L, wallet)

        wallet = 1500L
        val bought = repo.purchaseStoreTheme("sakura_garden", "u1", wallet) { cost ->
            wallet -= cost
            true
        }
        assertTrue(bought is ThemeActionResult.Success)
        assertEquals(700L, wallet)

        // Idempotent repeat purchase does not deduct again
        val repeat = repo.purchaseStoreTheme("sakura_garden", "u1", wallet) { cost ->
            wallet -= cost
            true
        }
        assertTrue(repeat is ThemeActionResult.Success)
        assertEquals(700L, wallet)
    }

    @Test
    fun testCrestProgressionAntiFarmingAndNoAutoEquip() {
        val storage = InMemoryRoomThemeStorage()
        val repo = RoomThemeRepository(storage)
        var progress = CrestProgressionEngine.initialProgress("r_test")
        assertEquals(1, progress.level)
        assertEquals(CLASSIC_LOUNGE_ID, repo.getEquippedThemeId("r_test"))

        // Self-gifting blocked
        val selfGift = CrestProgressionEngine.applyEvent(
            current = progress,
            eventId = "ev_self_gift",
            type = RoomCrestEventType.GIFT_VALUE_RECEIVED,
            actorId = "host_1",
            hostId = "host_1",
            requestedDelta = 400,
            summary = "Self gift",
            timestampMillis = 1000L,
            seenEventIds = emptySet(),
            alreadyUnlockedIds = emptySet(),
        )
        assertFalse(selfGift.accepted)
        assertEquals(0L, selfGift.progress.xp)

        // Gain enough XP for Crest Lv.3 (300 XP) -> unlocks Ocean Lounge without auto-equipping
        val gain = CrestProgressionEngine.applyEvent(
            current = progress,
            eventId = "ev_host_1",
            type = RoomCrestEventType.HOST_SESSION_MINUTE,
            actorId = "host_1",
            hostId = "host_1",
            requestedDelta = 320,
            summary = "Hosted active room",
            timestampMillis = 2000L,
            seenEventIds = emptySet(),
            alreadyUnlockedIds = setOf(CLASSIC_LOUNGE_ID),
        )
        assertTrue(gain.accepted)
        assertTrue(gain.progress.level >= 3)
        assertTrue(gain.newlyUnlockedThemeIds.contains("ocean_lounge"))
        // Must remain Classic Lounge until host explicitly equips Ocean Lounge
        assertEquals(CLASSIC_LOUNGE_ID, repo.getEquippedThemeId("r_test"))

        // Duplicate event id ignored
        val dup = CrestProgressionEngine.applyEvent(
            current = gain.progress,
            eventId = "ev_host_1",
            type = RoomCrestEventType.HOST_SESSION_MINUTE,
            actorId = "host_1",
            hostId = "host_1",
            requestedDelta = 320,
            summary = "Duplicate replay",
            timestampMillis = 2001L,
            seenEventIds = setOf("ev_host_1"),
            alreadyUnlockedIds = setOf(CLASSIC_LOUNGE_ID, "ocean_lounge"),
        )
        assertFalse(dup.accepted)
        assertEquals(gain.progress.xp, dup.progress.xp)
    }

    @Test
    fun testCreatedRoomDefaultsToClassicLoungeAndPersistsAnnouncement() {
        val repo = RoomThemeRepository(InMemoryRoomThemeStorage())
        repo.registerCreatedRoom(
            CreatedRoomRecord(
                id = "room_custom_1",
                title = "Chill Acoustic Night",
                tag = "Music",
                modeTitle = "Music/KTV",
                hostName = "Sundram",
                announcement = "Keep chat respectful!",
                isPrivate = false,
                hasPasscode = false,
                createdAtMillis = 123456L,
            )
        )
        assertEquals(CLASSIC_LOUNGE_ID, repo.getEquippedThemeId("room_custom_1"))
        assertEquals("Keep chat respectful!", repo.getAnnouncement("room_custom_1", "fallback"))
        assertEquals(1, repo.getCreatedRooms().size)
    }

    @Test
    fun testVoiceEngineSeatModerationAndClassicDefault() = runBlocking {
        val scope = CoroutineScope(SupervisorJob() + Dispatchers.Unconfined)
        try {
            val engine = DemoVoiceRoomEngine(scope)
            engine.start("r_demo", "user_1", "Sundram")
            assertEquals(CLASSIC_LOUNGE_ID, engine.state.value.roomThemeId)
            assertEquals(VOICE_ROOM_STAGE_CAPACITY, engine.state.value.seatCount)
            assertEquals(0, HOST_SEAT_INDEX)
            assertEquals(1, REGULAR_SEAT_START)
            assertEquals(8, REGULAR_SEAT_END)
            assertEquals(9, PARTNER_SEAT_INDEX)

            engine.setSelfProfileVisuals("/tmp/avatar.jpg", 123, 456)
            assertEquals("/tmp/avatar.jpg", engine.state.value.self?.photoPath)

            engine.takeSeat(3)
            assertEquals(3, engine.state.value.selfSeat)

            engine.moveMemberSeat("user_1", 6)
            assertEquals(6, engine.state.value.selfSeat)

            engine.toggleModerator("ghost-tara")
            assertEquals("moderator", engine.state.value.members.first { it.identity == "ghost-tara" }.role)

            engine.moderatorMuteSeat(4, muted = true)
            assertFalse(engine.state.value.seatAt(4)?.micOn ?: true)

            engine.setLocalMuteMember("ghost-arjun", muted = true)
            assertTrue(engine.state.value.locallyMutedIdentities.contains("ghost-arjun"))

            engine.setAnnouncement("Friday Acoustic Showcase")
            assertEquals("Friday Acoustic Showcase", engine.state.value.announcement)
        } finally {
            scope.cancel()
        }
    }
}
