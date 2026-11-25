package com.sundram.weplay.voiceroom

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * Owns one voice-room session.
 *
 * Chooses the transport from configuration: a real [LiveKitVoiceRoomEngine] when
 * [VoiceRoomServer] has a url and token endpoint, otherwise [DemoVoiceRoomEngine]
 * so the screen still works on a machine with no server.
 */
class VoiceRoomViewModel(
    appContext: Context,
    private val roomId: String,
    private val identity: String,
    private val displayName: String,
    private val roomTitle: String = "Classic Lounge",
) : ViewModel() {

    private val context = appContext.applicationContext
    private var explicitLeft = false

    private val engine: VoiceRoomEngine = VoiceRoomSessionHolder.acquireEngine(
        appContext = context,
        roomId = roomId,
        roomTitle = roomTitle,
        identity = identity,
        displayName = displayName,
    )

    val state: StateFlow<VoiceRoomState> = engine.state

    /** True once the user has granted RECORD_AUDIO; drives the first-run prompt. */
    val microphoneGranted: Boolean
        get() = context.checkSelfPermission(Manifest.permission.RECORD_AUDIO) ==
            PackageManager.PERMISSION_GRANTED

    /** LiveKit only unblocks mic publishing after the runtime permission exists. */
    val usesLiveServer: Boolean get() = VoiceRoomServer.isConfigured

    /**
     * Turns the microphone on, asking for the permission first when needed.
     *
     * @param requestPermission invoked when RECORD_AUDIO has not been granted yet;
     * the screen forwards the result through [onMicrophonePermissionResult].
     */
    fun toggleMicrophone(requestPermission: () -> Unit) {
        val target = !state.value.selfMicOn
        if (target && usesLiveServer && !microphoneGranted) {
            requestPermission()
            return
        }
        viewModelScope.launch { engine.setMicrophone(target) }
    }

    fun onMicrophonePermissionResult(granted: Boolean) {
        if (!granted) return
        viewModelScope.launch { engine.setMicrophone(true) }
    }

    fun sendMessage(text: String) {
        val body = text.trim()
        if (body.isEmpty()) return
        viewModelScope.launch { engine.sendChat(body) }
    }

    fun takeSeat(seat: Int) {
        viewModelScope.launch { engine.takeSeat(seat) }
    }

    fun leaveSeat() {
        viewModelScope.launch { engine.leaveSeat() }
    }

    fun sendGift(giftName: String, recipientName: String, count: Int, giftIconRes: Int? = null) {
        viewModelScope.launch { engine.sendGift(giftName, recipientName, count, giftIconRes) }
    }

    fun toggleFavorite() {
        viewModelScope.launch { engine.toggleFavorite() }
    }

    fun toggleTheme() {
        viewModelScope.launch { engine.toggleTheme() }
    }

    fun toggleLockSeat(seat: Int) {
        viewModelScope.launch { engine.toggleLockSeat(seat) }
    }

    fun kickSeat(seat: Int) {
        viewModelScope.launch { engine.kickSeat(seat) }
    }

    fun toggleFollowMember(identity: String) {
        viewModelScope.launch { engine.toggleFollowMember(identity) }
    }

    fun toggleEarpieceMode() {
        viewModelScope.launch { engine.toggleEarpieceMode() }
    }

    fun toggleOutputMute() {
        viewModelScope.launch { engine.toggleOutputMute() }
    }

    fun togglePartnerSeat(enabled: Boolean) {
        viewModelScope.launch { engine.togglePartnerSeat(enabled) }
    }

    fun toggleLockPartnerSeat() {
        viewModelScope.launch { engine.toggleLockPartnerSeat() }
    }

    fun requestSeatBond(firstSeat: Int, secondSeat: Int) {
        viewModelScope.launch { engine.requestSeatBond(firstSeat, secondSeat) }
    }

    fun respondSeatBondConsent(bondId: String, accept: Boolean) {
        viewModelScope.launch { engine.respondSeatBondConsent(bondId, accept) }
    }

    fun detachSeatBond(bondId: String) {
        viewModelScope.launch { engine.detachSeatBond(bondId) }
    }

    fun moveMemberSeat(identity: String, targetSeat: Int) {
        viewModelScope.launch { engine.moveMemberSeat(identity, targetSeat) }
    }

    fun toggleModerator(identity: String) {
        viewModelScope.launch { engine.toggleModerator(identity) }
    }

    fun inviteToSeat(identity: String, seat: Int) {
        viewModelScope.launch { engine.inviteToSeat(identity, seat) }
    }

    fun setLocalMuteMember(identity: String, muted: Boolean) {
        viewModelScope.launch { engine.setLocalMuteMember(identity, muted) }
    }

    fun moderatorMuteSeat(seat: Int, muted: Boolean) {
        viewModelScope.launch { engine.moderatorMuteSeat(seat, muted) }
    }

    fun blockMember(identity: String) {
        viewModelScope.launch { engine.blockMember(identity) }
    }

    fun setAnnouncement(text: String) {
        viewModelScope.launch { engine.setAnnouncement(text) }
    }

    fun setRoomThemeId(themeId: String) {
        viewModelScope.launch { engine.setRoomThemeId(themeId) }
    }

    fun setFavorited(favorited: Boolean) {
        viewModelScope.launch { engine.setFavorited(favorited) }
    }

    fun setSelfProfileVisuals(photoPath: String?, avatarRes: Int?, frameRes: Int?) {
        viewModelScope.launch { engine.setSelfProfileVisuals(photoPath, avatarRes, frameRes) }
    }

    /** Minimizes the active session so returning keeps the single connection alive. */
    fun minimizeRoom() {
        VoiceRoomSessionHolder.minimizeCurrentRoom()
    }

    /** Retries a failed connection without leaving the screen. */
    fun reconnect() {
        viewModelScope.launch {
            engine.stop()
            engine.start(roomId, identity, displayName)
        }
    }

    /** Explicitly disconnects the room engine and clears any minimized bar. */
    fun disconnect() {
        explicitLeft = true
        VoiceRoomSessionHolder.leaveAndRelease()
    }

    override fun onCleared() {
        if (explicitLeft) {
            VoiceRoomSessionHolder.leaveAndRelease()
        }
    }

    companion object {
        fun factory(
            context: Context,
            roomId: String,
            identity: String,
            displayName: String,
            roomTitle: String = "Classic Lounge",
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: androidx.lifecycle.viewmodel.CreationExtras,
            ): T = VoiceRoomViewModel(
                appContext = context.applicationContext,
                roomId = roomId,
                identity = identity,
                displayName = displayName,
                roomTitle = roomTitle,
            ) as T
        }
    }
}
