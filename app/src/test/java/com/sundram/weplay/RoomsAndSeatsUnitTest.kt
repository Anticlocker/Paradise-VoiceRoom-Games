package com.sundram.weplay

import com.sundram.weplay.voiceroom.BondStatus
import com.sundram.weplay.voiceroom.ConsentStatus
import com.sundram.weplay.voiceroom.DemoVoiceRoomEngine
import com.sundram.weplay.voiceroom.HOST_SEAT_INDEX
import com.sundram.weplay.voiceroom.PARTNER_SEAT_INDEX
import com.sundram.weplay.voiceroom.REGULAR_SEAT_COUNT
import com.sundram.weplay.voiceroom.REGULAR_SEAT_END
import com.sundram.weplay.voiceroom.REGULAR_SEAT_START
import com.sundram.weplay.voiceroom.SeatBond
import com.sundram.weplay.voiceroom.VOICE_ROOM_STAGE_CAPACITY
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit tests validating stage seats (1 Host + 8 Regular + 1 Partner)
 * and the Attach Seats bonding state machine.
 */
class RoomsAndSeatsUnitTest {

    @Test
    fun testStageConstantsAndInvariants() {
        assertEquals(0, HOST_SEAT_INDEX)
        assertEquals(1, REGULAR_SEAT_START)
        assertEquals(8, REGULAR_SEAT_END)
        assertEquals(8, REGULAR_SEAT_COUNT)
        assertEquals(9, PARTNER_SEAT_INDEX)
        assertEquals(10, VOICE_ROOM_STAGE_CAPACITY)
    }

    @Test
    fun testSeatBondCanActivateValidation() {
        val validBond = SeatBond(
            bondId = "b1",
            roomId = "r1",
            firstSeat = 1,
            secondSeat = 2,
            firstUserId = "u1",
            secondUserId = "u2",
            firstConsent = ConsentStatus.ACCEPTED,
            secondConsent = ConsentStatus.ACCEPTED,
            status = BondStatus.REQUESTED,
        )
        assertTrue("Valid mutual consent should be activatable", validBond.canActivate())

        val selfPairBond = validBond.copy(secondUserId = "u1")
        assertFalse("Self pairing must not be activatable", selfPairBond.canActivate())

        val sameSeatBond = validBond.copy(secondSeat = 1)
        assertFalse("Same seat pairing must not be activatable", sameSeatBond.canActivate())

        val pendingConsentBond = validBond.copy(secondConsent = ConsentStatus.PENDING)
        assertFalse("Pending consent must not be activatable", pendingConsentBond.canActivate())
    }

    @Test
    fun testPartnerSeatToggleAndGracefulEviction() = runBlocking {
        val testScope = CoroutineScope(Dispatchers.Unconfined)
        val engine = DemoVoiceRoomEngine(testScope)
        engine.start("room_test", "user_self", "Hero")

        // 1. Partner seat is initially disabled
        assertFalse(engine.state.value.partnerEnabled)

        // 2. Attempting to take partner seat while disabled is rejected
        engine.takeSeat(PARTNER_SEAT_INDEX)
        assertNull("Self must not take partner seat when disabled", engine.state.value.self?.seat)

        // 3. Host enables partner seat
        engine.togglePartnerSeat(true)
        assertTrue(engine.state.value.partnerEnabled)

        // 4. Now self can take partner seat
        engine.takeSeat(PARTNER_SEAT_INDEX)
        val partnerMember = engine.state.value.seatAt(PARTNER_SEAT_INDEX)
        assertNotNull(partnerMember)
        assertEquals(PARTNER_SEAT_INDEX, partnerMember?.seat)
        assertTrue(partnerMember?.isPartner == true)

        // 5. Host disables partner seat -> occupant is gracefully unseated to listener lounge
        engine.togglePartnerSeat(false)
        assertFalse(engine.state.value.partnerEnabled)
        assertNull("Partner occupant must be moved to listeners when seat is disabled", engine.state.value.seatAt(PARTNER_SEAT_INDEX))
        assertNull("Self should have null seat after eviction", engine.state.value.self?.seat)

        engine.stop()
    }

    @Test
    fun testAttachSeatsRequestAndActivation() = runBlocking {
        val testScope = CoroutineScope(Dispatchers.Unconfined)
        val engine = DemoVoiceRoomEngine(testScope)
        engine.start("room_test", "user_self", "Hero")

        // In DemoVoiceRoomEngine, Ghost Arjun is at seat 1 and Ghost Tara is at seat 2
        assertNotNull(engine.state.value.seatAt(1))
        assertNotNull(engine.state.value.seatAt(2))

        // Request bond between Seat 1 and Seat 2
        engine.requestSeatBond(1, 2)

        // Wait for ghost acceptance simulation
        delay(1200)

        // Verify active bond
        assertTrue("Seat 1 should be reported in bond", engine.state.value.isSeatInBond(1))
        assertTrue("Seat 2 should be reported in bond", engine.state.value.isSeatInBond(2))
        assertFalse("Seat 3 should not be in bond", engine.state.value.isSeatInBond(3))

        val bond = engine.state.value.getBondForSeat(1)
        assertNotNull(bond)
        assertEquals(BondStatus.ACTIVE, bond?.status)
        assertEquals(1, bond?.firstSeat)
        assertEquals(2, bond?.secondSeat)

        // Detach bond
        engine.detachSeatBond(bond!!.bondId)
        assertFalse("Seat 1 should no longer be in bond", engine.state.value.isSeatInBond(1))
        assertFalse("Seat 2 should no longer be in bond", engine.state.value.isSeatInBond(2))

        engine.stop()
    }

    @Test
    fun testSeatDepartureInvalidatesBond() = runBlocking {
        val testScope = CoroutineScope(Dispatchers.Unconfined)
        val engine = DemoVoiceRoomEngine(testScope)
        engine.start("room_test", "user_self", "Hero")

        // Self takes open regular seat 3
        engine.takeSeat(3)
        assertEquals(3, engine.state.value.self?.seat)

        // Request bond between seat 1 (Ghost Arjun) and seat 3 (Self)
        engine.requestSeatBond(1, 3)
        delay(1200)

        assertTrue("Seat 3 should be in bond", engine.state.value.isSeatInBond(3))

        // Self leaves seat 3
        engine.leaveSeat()
        assertNull(engine.state.value.self?.seat)

        // Active bond must be invalidated when occupant leaves
        assertFalse("Active bond must be cleaned up when participant leaves", engine.state.value.isSeatInBond(3))
        assertFalse("Other participant's seat must also be freed from bond", engine.state.value.isSeatInBond(1))

        engine.stop()
    }
}
