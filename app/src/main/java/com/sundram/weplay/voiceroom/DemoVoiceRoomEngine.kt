package com.sundram.weplay.voiceroom

import com.sundram.weplay.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/**
 * Offline stand-in for [LiveKitVoiceRoomEngine].
 *
 * Used when `livekit.url` / `livekit.token.url` are not present in
 * `local.properties`, so the room screen — seats, speaking ring, mic toggle, chat
 * feed — can be built and reviewed before the VPS is ready. It never opens a
 * socket; everything it shows is generated locally and clearly labelled as a
 * simulation in the header.
 */
class DemoVoiceRoomEngine(
    private val scope: CoroutineScope,
) : VoiceRoomEngine {

    private val _state = MutableStateFlow(
        VoiceRoomState(
            transportLabel = "Aurora Lounge · Offline Preview",
            connection = RoomConnection.SIMULATED,
        ),
    )
    override val state: StateFlow<VoiceRoomState> = _state

    private val members = mutableListOf<RoomMemberVo>()
    private val chat = ArrayDeque<RoomMessage>()
    private var messageSeq = 0L
    private var scriptIndex = 0
    private var tickJob: Job? = null
    private var selfName = "You"

    override suspend fun start(roomId: String, identity: String, displayName: String) {
        members.clear()
        chat.clear()
        scriptIndex = 0
        selfName = displayName.ifBlank { "You" }
        members += GHOSTS.map { it.copy() }
        members += RoomMemberVo(
            identity = identity.ifBlank { "self" },
            name = selfName,
            seat = null,
            micOn = false,
            speaking = false,
            isSelf = true,
            joinedAtMs = System.currentTimeMillis(),
            role = "listener",
            frameRes = R.drawable.vb_vr_frame_normal,
            level = 5,
        )
        appendMessage(systemMessage("✨ Welcome to Aurora Lounge 2.0! Seats, mic and audio are running in local preview mode."))
        appendMessage(scriptMessage(GHOSTS[0].name, "Hey everyone! Welcome to the lounge 🎙️ grab a seat to chat!"))
        _state.update {
            it.copy(
                roomId = roomId,
                connection = RoomConnection.SIMULATED,
                members = members.toList(),
                error = null,
            )
        }
        tickJob?.cancel()
        tickJob = scope.launch { runScript() }
    }

    override suspend fun stop() {
        tickJob?.cancel()
        tickJob = null
        _state.update { it.copy(connection = RoomConnection.DISCONNECTED) }
    }

    override suspend fun setMicrophone(enabled: Boolean) {
        members.replaceAll { if (it.isSelf) it.copy(micOn = enabled, speaking = false) else it }
        publish()
    }

    override suspend fun sendChat(text: String) {
        val body = text.trim()
        if (body.isEmpty()) return
        val self = members.firstOrNull { it.isSelf } ?: return
        chat.addLast(RoomMessage(nextId(), self.name, body, isSelf = true))
        trimChat()
        val reply = REPLIES.getOrElse(scriptIndex) { REPLIES.last() }
        scriptIndex++
        val responder = members.filter { !it.isSelf }.randomOrNull()
        publish()
        scope.launch {
            delay(1200)
            if (responder != null) chat.addLast(RoomMessage(nextId(), responder.name, reply, isSelf = false))
            trimChat()
            publish()
        }
    }

    override suspend fun takeSeat(seat: Int) {
        if (seat !in 0 until VOICE_ROOM_SEAT_COUNT) return
        val currentLocked = _state.value.lockedSeats
        if (seat in currentLocked) {
            chat.addLast(systemMessage("Seat ${seat + 1} is locked by the host."))
            publish()
            return
        }
        val self = members.firstOrNull { it.isSelf } ?: return
        val takenByOther = members.any { !it.isSelf && it.seat == seat }
        if (takenByOther) {
            chat.addLast(systemMessage("Seat ${seat + 1} is already taken."))
            publish()
            return
        }
        members.replaceAll { if (it.isSelf) it.copy(seat = seat, role = if (seat == 0) "host" else "speaker", frameRes = if (seat == 0) R.drawable.vb_vr_frame_host else R.drawable.vb_vr_frame_speaker) else it }
        chat.addLast(systemMessage("${self.name} took Seat ${seat + 1}"))
        publish()
    }

    override suspend fun leaveSeat() {
        val self = members.firstOrNull { it.isSelf } ?: return
        val prevSeat = self.seat
        members.replaceAll { if (it.isSelf) it.copy(seat = null, speaking = false, role = "listener", frameRes = R.drawable.vb_vr_frame_normal) else it }
        if (prevSeat != null) {
            chat.addLast(systemMessage("${self.name} moved to the listener lounge."))
        }
        publish()
    }

    override suspend fun sendGift(giftName: String, recipientName: String, count: Int, giftIconRes: Int?) {
        val giftMsg = RoomMessage(
            id = nextId(),
            author = selfName,
            body = "sent $giftName x$count to $recipientName! 🎉",
            isSelf = true,
            system = false,
            isGift = true,
            giftIconRes = giftIconRes,
        )
        appendMessage(giftMsg)
    }

    override suspend fun toggleFavorite() {
        _state.update { it.copy(isFavorited = !it.isFavorited) }
    }

    override suspend fun toggleTheme() {
        _state.update {
            it.copy(selectedTheme = if (it.selectedTheme == "aurora") "celestial" else "aurora")
        }
    }

    override suspend fun toggleLockSeat(seat: Int) {
        _state.update { curr ->
            val updated = if (seat in curr.lockedSeats) curr.lockedSeats - seat else curr.lockedSeats + seat
            curr.copy(lockedSeats = updated)
        }
    }

    override suspend fun kickSeat(seat: Int) {
        val member = members.firstOrNull { it.seat == seat } ?: return
        members.replaceAll { if (it.identity == member.identity) it.copy(seat = null, speaking = false) else it }
        chat.addLast(systemMessage("${member.name} was moved from Seat ${seat + 1}."))
        publish()
    }

    override suspend fun toggleFollowMember(identity: String) {
        members.replaceAll { if (it.identity == identity) it.copy(isFollowed = !it.isFollowed) else it }
        publish()
    }

    override suspend fun toggleEarpieceMode() {
        _state.update { it.copy(isEarpieceMode = !it.isEarpieceMode) }
    }

    override suspend fun toggleOutputMute() {
        _state.update { it.copy(isAudioMuted = !it.isAudioMuted) }
    }

    /** Cycles speaking highlights so the ring animation is visible without a server. */
    private suspend fun CoroutineScope.runScript() {
        var step = 0
        while (isActive) {
            delay(2600)
            step++
            val talkers = members.filter { !it.isSelf && it.hasSeat && it.micOn }
            if (talkers.isNotEmpty()) {
                val active = talkers[step % talkers.size]
                members.replaceAll {
                    if (it.isSelf) {
                        it.copy(speaking = it.micOn && step % 7 == 0)
                    } else {
                        it.copy(speaking = it.identity == active.identity)
                    }
                }
            }
            SCRIPT_LINES.getOrNull(scriptIndex)?.let { (who, line) ->
                val author = members.firstOrNull { !it.isSelf && it.name == who }?.name ?: who
                chat.addLast(RoomMessage(nextId(), author, line, isSelf = false))
                trimChat()
                scriptIndex++
            }
            publish()
        }
    }

    private fun publish() {
        val selfMicOn = members.firstOrNull { it.isSelf }?.micOn == true
        _state.update {
            it.copy(
                members = members.toList(),
                messages = chat.toList(),
                selfMicOn = selfMicOn,
            )
        }
    }

    private fun trimChat() {
        while (chat.size > MAX_CHAT_HISTORY) chat.removeFirst()
    }

    private fun systemMessage(body: String) =
        RoomMessage(nextId(), "System", body, isSelf = false, system = true)

    private fun scriptMessage(author: String, body: String) =
        RoomMessage(nextId(), author, body, isSelf = false)

    private fun appendMessage(message: RoomMessage) {
        chat.addLast(message)
        trimChat()
        publish()
    }

    private fun nextId(): Long = ++messageSeq

    private companion object {
        const val MAX_CHAT_HISTORY = 200

        val GHOSTS = listOf(
            RoomMemberVo(
                identity = "ghost-host",
                name = "Neha",
                seat = 0,
                micOn = true,
                speaking = true,
                isSelf = false,
                joinedAtMs = 0L,
                role = "host",
                avatarRes = R.drawable.vb_vr_avatar_demo_01,
                frameRes = R.drawable.vb_vr_frame_host,
                level = 18,
                isFollowed = true,
            ),
            RoomMemberVo(
                identity = "ghost-arjun",
                name = "Arjun",
                seat = 1,
                micOn = true,
                speaking = false,
                isSelf = false,
                joinedAtMs = 0L,
                role = "moderator",
                avatarRes = R.drawable.vb_vr_avatar_demo_02,
                frameRes = R.drawable.vb_vr_frame_moderator,
                level = 14,
            ),
            RoomMemberVo(
                identity = "ghost-tara",
                name = "Tara",
                seat = 2,
                micOn = false,
                speaking = false,
                isSelf = false,
                joinedAtMs = 0L,
                role = "vip",
                avatarRes = R.drawable.vb_vr_avatar_demo_03,
                frameRes = R.drawable.vb_vr_frame_vip,
                level = 12,
            ),
            RoomMemberVo(
                identity = "ghost-vikram",
                name = "Vikram",
                seat = 4,
                micOn = true,
                speaking = false,
                isSelf = false,
                joinedAtMs = 0L,
                role = "speaker",
                avatarRes = R.drawable.vb_vr_avatar_demo_04,
                frameRes = R.drawable.vb_vr_frame_speaker,
                level = 9,
            ),
        )

        val SCRIPT_LINES = listOf(
            "Arjun" to "Next track is acoustic guitar jam, who wants to queue up next? 🎸",
            "Neha" to "Welcome to tonight's Aurora Lounge session everyone! Drop some hearts! ✨",
            "Vikram" to "Sent 3x roses to the host, amazing song! 🌹",
            "Tara" to "Muted for a sec grabbing headphones, be right back!",
        )

        val REPLIES = listOf(
            "Love the vibe in here! 💜",
            "Such a great acoustic set!",
            "Who wants to do a quick PK battle round?",
        )
    }
}
