package com.sundram.weplay.voiceroom

import kotlinx.coroutines.flow.StateFlow

/**
 * Transport abstraction for a voice room.
 *
 * Two implementations exist:
 *  - [LiveKitVoiceRoomEngine]: real audio + chat against a self-hosted LiveKit SFU.
 *  - [DemoVoiceRoomEngine]: offline simulation used until a server is configured,
 *    so the room screen is exerciseable without any network.
 *
 * Implementations are owned by [VoiceRoomViewModel] and must be stopped with
 * [stop] before the view model is cleared.
 */
interface VoiceRoomEngine {

    val state: StateFlow<VoiceRoomState>

    /**
     * Connect and keep the room snapshot in sync.
     *
     * @param roomId LiveKit room name, e.g. "r1".
     * @param identity stable per-user id, used as the LiveKit participant identity.
     * @param displayName name other participants see.
     */
    suspend fun start(roomId: String, identity: String, displayName: String)

    /** Leave the room and release every resource held by the engine. */
    suspend fun stop()

    /** Publish (or stop publishing) local microphone audio. */
    suspend fun setMicrophone(enabled: Boolean)

    /** Send a text chat line to everyone in the room. */
    suspend fun sendChat(text: String)

    /** Take a seat on the stage (0 for Host, 1..8 for regular mic seats, 9 for Partner). */
    suspend fun takeSeat(seat: Int)

    /** Give up the current seat and become a listener. */
    suspend fun leaveSeat()

    /** Send an in-room gift event broadcast. */
    suspend fun sendGift(giftName: String, recipientName: String, count: Int, giftIconRes: Int? = null) {}

    /** Toggle favorited state of the room. */
    suspend fun toggleFavorite() {}

    /** Toggle room background theme (Aurora vs Celestial). */
    suspend fun toggleTheme() {}

    /** Host action: lock or unlock a specific stage seat. */
    suspend fun toggleLockSeat(seat: Int) {}

    /** Host action: remove a participant from a stage seat. */
    suspend fun kickSeat(seat: Int) {}

    /** Toggle following a room participant. */
    suspend fun toggleFollowMember(identity: String) {}

    /** Toggle speaker vs earpiece audio routing. */
    suspend fun toggleEarpieceMode() {}

    /** Toggle master audio output mute. */
    suspend fun toggleOutputMute() {}

    /** Host action: enable or disable the optional partner seat (beside host). */
    suspend fun togglePartnerSeat(enabled: Boolean) {}

    /** Host action: lock or unlock the partner seat. */
    suspend fun toggleLockPartnerSeat() {}

    /** Host action: request an attachment bond between two regular occupied seats (1..8). */
    suspend fun requestSeatBond(firstSeat: Int, secondSeat: Int) {}

    /** Participant action: respond to incoming bond consent request (Accept/Decline). */
    suspend fun respondSeatBondConsent(bondId: String, accept: Boolean) {}

    /** Host or Participant action: detach an existing seat bond. */
    suspend fun detachSeatBond(bondId: String) {}
}
