package com.sundram.weplay

import com.sundram.weplay.voiceroom.DemoVoiceRoomEngine
import com.sundram.weplay.voiceroom.RoomConnection
import com.sundram.weplay.voiceroom.VOICE_ROOM_SEAT_COUNT
import com.sundram.weplay.voiceroom.VoiceRoomState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Functional unit tests for Aurora Lounge 2.0 Joined Voice Room.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class AuroraVoiceRoomUnitTest {

    @Test
    fun testRoomSeatCountConstant() {
        assertEquals(8, VOICE_ROOM_SEAT_COUNT)
    }

    @Test
    fun testDemoEngineStartAndInitialMembers() = runTest {
        val testScope = TestScope()
        val engine = DemoVoiceRoomEngine(testScope)

        engine.start("room_aurora_1", "user_test_1", "Alex")
        val state = engine.state.value

        assertEquals("room_aurora_1", state.roomId)
        assertEquals(RoomConnection.SIMULATED, state.connection)
        assertTrue("Members list should not be empty", state.members.isNotEmpty())

        // Verify self joined as listener
        val self = state.self
        assertNotNull("Self should be in members list", self)
        assertEquals("Alex", self!!.name)
        assertEquals(null, self.seat)
        assertFalse("Self microphone should initially be off", self.micOn)

        // Verify host is seated at seat 0
        val host = state.seatAt(0)
        assertNotNull("Host should occupy seat 0", host)
        assertTrue("Host should have isHost true", host!!.isHost)
        assertEquals(0, host.seat)
        assertEquals("Neha", host.name)

        engine.stop()
    }

    @Test
    fun testTakeAndLeaveSeat() = runTest {
        val testScope = TestScope()
        val engine = DemoVoiceRoomEngine(testScope)

        engine.start("room_aurora_1", "user_test_1", "Alex")

        // Take seat 3 (which is empty in demo ghosts)
        engine.takeSeat(3)
        var state = engine.state.value
        var self = state.self
        assertNotNull(self)
        assertEquals(3, self!!.seat)
        assertEquals("speaker", self.role)

        // Leave seat
        engine.leaveSeat()
        state = engine.state.value
        self = state.self
        assertNotNull(self)
        assertEquals(null, self!!.seat)
        assertEquals("listener", self.role)

        engine.stop()
    }

    @Test
    fun testLockedSeatRejection() = runTest {
        val testScope = TestScope()
        val engine = DemoVoiceRoomEngine(testScope)

        engine.start("room_aurora_1", "user_test_1", "Alex")

        // Lock seat 5
        engine.toggleLockSeat(5)
        var state = engine.state.value
        assertTrue("Seat 5 should be in lockedSeats", state.isSeatLocked(5))

        // Attempt to take locked seat 5
        engine.takeSeat(5)
        state = engine.state.value
        val self = state.self
        assertNotNull(self)
        assertTrue("Self should not have taken locked seat", self!!.seat != 5)

        engine.stop()
    }

    @Test
    fun testMicrophoneToggle() = runTest {
        val testScope = TestScope()
        val engine = DemoVoiceRoomEngine(testScope)

        engine.start("room_aurora_1", "user_test_1", "Alex")
        assertFalse(engine.state.value.selfMicOn)

        engine.setMicrophone(true)
        assertTrue(engine.state.value.selfMicOn)

        engine.setMicrophone(false)
        assertFalse(engine.state.value.selfMicOn)

        engine.stop()
    }

    @Test
    fun testSendGiftBroadcast() = runTest {
        val testScope = TestScope()
        val engine = DemoVoiceRoomEngine(testScope)

        engine.start("room_aurora_1", "user_test_1", "Alex")
        val initialMsgCount = engine.state.value.messages.size

        engine.sendGift("Royal Crown", "Neha", 5, R.drawable.vb_gift_crown)
        val state = engine.state.value

        assertTrue("Messages list should have increased", state.messages.size > initialMsgCount)
        val lastMsg = state.messages.last()
        assertTrue("Last message should be a gift", lastMsg.isGift)
        assertTrue("Gift message should mention recipient", lastMsg.body.contains("Neha"))
        assertTrue("Gift message should mention gift name", lastMsg.body.contains("Royal Crown"))

        engine.stop()
    }

    @Test
    fun testFavoriteAndThemeToggle() = runTest {
        val testScope = TestScope()
        val engine = DemoVoiceRoomEngine(testScope)

        engine.start("room_aurora_1", "user_test_1", "Alex")

        // Favorite toggle
        assertFalse(engine.state.value.isFavorited)
        engine.toggleFavorite()
        assertTrue(engine.state.value.isFavorited)
        engine.toggleFavorite()
        assertFalse(engine.state.value.isFavorited)

        // Theme toggle
        assertEquals("aurora", engine.state.value.selectedTheme)
        engine.toggleTheme()
        assertEquals("celestial", engine.state.value.selectedTheme)
        engine.toggleTheme()
        assertEquals("aurora", engine.state.value.selectedTheme)

        engine.stop()
    }

    @Test
    fun testCoreAuroraDrawableResourcesExist() {
        // Backdrops
        assertTrue(R.drawable.vb_vr_bg_aurora != 0)
        assertTrue(R.drawable.vb_vr_bg_celestial != 0)

        // Frames
        assertTrue(R.drawable.vb_vr_frame_host != 0)
        assertTrue(R.drawable.vb_vr_frame_speaker != 0)
        assertTrue(R.drawable.vb_vr_frame_moderator != 0)
        assertTrue(R.drawable.vb_vr_frame_vip != 0)
        assertTrue(R.drawable.vb_vr_frame_normal != 0)

        // Seat states
        assertTrue(R.drawable.vb_vr_seat_empty != 0)
        assertTrue(R.drawable.vb_vr_seat_locked != 0)

        // Control icons
        assertTrue(R.drawable.vb_vr_ic_back != 0)
        assertTrue(R.drawable.vb_vr_ic_mic != 0)
        assertTrue(R.drawable.vb_vr_ic_mic_off != 0)
        assertTrue(R.drawable.vb_vr_ic_gift != 0)
        assertTrue(R.drawable.vb_vr_ic_swords != 0)
        assertTrue(R.drawable.vb_vr_ic_dice != 0)
        assertTrue(R.drawable.vb_vr_ic_headphones != 0)
        assertTrue(R.drawable.vb_vr_ic_users != 0)
        assertTrue(R.drawable.vb_vr_ic_heart != 0)
        assertTrue(R.drawable.vb_vr_ic_more != 0)

        // Emblems
        assertTrue(R.drawable.vb_vr_emblem_crown != 0)
        assertTrue(R.drawable.vb_vr_chip_host != 0)
    }
}
