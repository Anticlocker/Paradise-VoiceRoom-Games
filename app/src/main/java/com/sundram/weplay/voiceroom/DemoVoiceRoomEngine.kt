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
 * Offline simulation for [VoiceRoomEngine].
 *
 * Implements the 10-seat Voice Lounge stage architecture:
 * - 1 Host seat (0)
 * - 8 Regular mic seats (1..8)
 * - 1 Optional Partner seat (9)
 * - Attach Seats (bonding) with mutual consent simulation
 * - Dynamic speaker cycling and chat activity
 */
class DemoVoiceRoomEngine(
    private val scope: CoroutineScope,
) : VoiceRoomEngine {

    private val _state = MutableStateFlow(
        VoiceRoomState(
            transportLabel = "Offline Preview",
            connection = RoomConnection.SIMULATED,
            roomThemeId = "classic_lounge",
        ),
    )
    override val state: StateFlow<VoiceRoomState> = _state

    private val members = mutableListOf<RoomMemberVo>()
    private val chat = ArrayDeque<RoomMessage>()
    private var messageSeq = 0L
    private var scriptIndex = 0
    private var tickJob: Job? = null
    private var selfName = "You"
    private var selfIdentity = "self"

    override suspend fun start(roomId: String, identity: String, displayName: String) {
        members.clear()
        chat.clear()
        scriptIndex = 0
        selfIdentity = identity.ifBlank { "self" }
        selfName = displayName.ifBlank { "You" }

        members += GHOSTS.map { it.copy() }
        members += RoomMemberVo(
            identity = selfIdentity,
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

        appendMessage(systemMessage("Welcome to the voice room. Grab an open mic seat to join the conversation."))
        appendMessage(scriptMessage(GHOSTS[0].name, "Hey everyone! Welcome to the room — feel free to take an open seat!"))

        _state.update {
            it.copy(
                roomId = roomId,
                connection = RoomConnection.SIMULATED,
                members = members.toList(),
                partnerEnabled = false,
                partnerLocked = false,
                activeBonds = emptyList(),
                pendingBondConsent = null,
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
            if (responder != null) {
                chat.addLast(RoomMessage(nextId(), responder.name, reply, isSelf = false))
                trimChat()
                publish()
            }
        }
    }

    override suspend fun takeSeat(seat: Int) {
        if (seat !in 0 until VOICE_ROOM_STAGE_CAPACITY) return

        // Partner seat permission check
        if (seat == PARTNER_SEAT_INDEX && !_state.value.partnerEnabled) {
            chat.addLast(systemMessage("Partner seat is currently disabled by the host."))
            publish()
            return
        }

        // Lock check
        if (_state.value.isSeatLocked(seat)) {
            val label = if (seat == HOST_SEAT_INDEX) "Host seat" else if (seat == PARTNER_SEAT_INDEX) "Partner seat" else "Seat $seat"
            chat.addLast(systemMessage("$label is locked by the host."))
            publish()
            return
        }

        val self = members.firstOrNull { it.isSelf } ?: return
        val takenByOther = members.any { !it.isSelf && it.seat == seat }
        if (takenByOther) {
            val label = if (seat == HOST_SEAT_INDEX) "Host seat" else if (seat == PARTNER_SEAT_INDEX) "Partner seat" else "Seat $seat"
            chat.addLast(systemMessage("$label is already taken."))
            publish()
            return
        }

        val role = when (seat) {
            HOST_SEAT_INDEX -> "host"
            PARTNER_SEAT_INDEX -> "partner"
            else -> "speaker"
        }
        val frame = when (seat) {
            HOST_SEAT_INDEX -> R.drawable.vb_vr_frame_host
            PARTNER_SEAT_INDEX -> R.drawable.vb_rs_frame_partner
            else -> R.drawable.vb_vr_frame_speaker
        }

        members.replaceAll {
            if (it.isSelf) it.copy(seat = seat, role = role, frameRes = frame) else it
        }

        val seatName = when (seat) {
            HOST_SEAT_INDEX -> "the Host Seat"
            PARTNER_SEAT_INDEX -> "the Partner Seat"
            else -> "Mic Seat $seat"
        }
        chat.addLast(systemMessage("${self.name} took $seatName"))
        publish()
    }

    override suspend fun leaveSeat() {
        val self = members.firstOrNull { it.isSelf } ?: return
        val prevSeat = self.seat
        members.replaceAll {
            if (it.isSelf) {
                it.copy(seat = null, speaking = false, role = "listener", frameRes = R.drawable.vb_vr_frame_normal)
            } else {
                it
            }
        }
        if (prevSeat != null) {
            // Invalidate any active bond involving this seat
            invalidateBondsForSeat(prevSeat, self.name)
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
        if (seat == PARTNER_SEAT_INDEX) {
            toggleLockPartnerSeat()
            return
        }
        _state.update { curr ->
            val updated = if (seat in curr.lockedSeats) curr.lockedSeats - seat else curr.lockedSeats + seat
            curr.copy(lockedSeats = updated)
        }
    }

    override suspend fun kickSeat(seat: Int) {
        val member = members.firstOrNull { it.seat == seat } ?: return
        members.replaceAll {
            if (it.identity == member.identity) {
                it.copy(seat = null, speaking = false, role = "listener", frameRes = R.drawable.vb_vr_frame_normal)
            } else {
                it
            }
        }
        invalidateBondsForSeat(seat, member.name)
        val seatLabel = when (seat) {
            HOST_SEAT_INDEX -> "Host seat"
            PARTNER_SEAT_INDEX -> "Partner seat"
            else -> "Seat $seat"
        }
        chat.addLast(systemMessage("${member.name} was moved from $seatLabel."))
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

    // ─────────────────────────────────────────────────────────────────────────
    //  Partner Seat Implementation
    // ─────────────────────────────────────────────────────────────────────────
    override suspend fun togglePartnerSeat(enabled: Boolean) {
        _state.update { it.copy(partnerEnabled = enabled) }
        if (!enabled) {
            // Gracefully unseat partner if occupied
            val partnerMember = members.firstOrNull { it.seat == PARTNER_SEAT_INDEX }
            if (partnerMember != null) {
                members.replaceAll {
                    if (it.identity == partnerMember.identity) {
                        it.copy(seat = null, speaking = false, role = "listener", frameRes = R.drawable.vb_vr_frame_normal)
                    } else {
                        it
                    }
                }
                chat.addLast(systemMessage("${partnerMember.name} moved to the listener lounge as Partner Seat was disabled."))
            } else {
                chat.addLast(systemMessage("Host disabled the Partner Seat."))
            }
        } else {
            chat.addLast(systemMessage("✨ Host enabled the Partner Seat beside the stage!"))
        }
        publish()
    }

    override suspend fun toggleLockPartnerSeat() {
        _state.update { it.copy(partnerLocked = !it.partnerLocked) }
        val msg = if (_state.value.partnerLocked) "Partner seat locked by host." else "Partner seat unlocked."
        chat.addLast(systemMessage(msg))
        publish()
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  Attach Seats (Bonding) Implementation
    // ─────────────────────────────────────────────────────────────────────────
    override suspend fun requestSeatBond(firstSeat: Int, secondSeat: Int) {
        if (firstSeat !in REGULAR_SEAT_START..REGULAR_SEAT_END ||
            secondSeat !in REGULAR_SEAT_START..REGULAR_SEAT_END ||
            firstSeat == secondSeat
        ) {
            chat.addLast(systemMessage("Attach Seats requires two distinct regular mic seats (1-8)."))
            publish()
            return
        }

        val occupantA = members.firstOrNull { it.seat == firstSeat }
        val occupantB = members.firstOrNull { it.seat == secondSeat }

        if (occupantA == null || occupantB == null) {
            chat.addLast(systemMessage("Both selected seats must be occupied to form a bond."))
            publish()
            return
        }

        val bondId = "bond_${System.currentTimeMillis()}"
        val isSelfA = occupantA.isSelf
        val isSelfB = occupantB.isSelf

        val initialBond = SeatBond(
            bondId = bondId,
            roomId = _state.value.roomId,
            firstSeat = firstSeat,
            secondSeat = secondSeat,
            firstUserId = occupantA.identity,
            secondUserId = occupantB.identity,
            firstConsent = if (isSelfA) ConsentStatus.ACCEPTED else ConsentStatus.PENDING,
            secondConsent = if (isSelfB) ConsentStatus.ACCEPTED else ConsentStatus.PENDING,
            status = BondStatus.REQUESTED,
        )

        chat.addLast(systemMessage("Requested an Attached Seat link between Seat $firstSeat (${occupantA.name}) and Seat $secondSeat (${occupantB.name}). Waiting for confirmation."))
        publish()

        // If self is occupant B, trigger pending consent dialog
        if (isSelfB && !isSelfA) {
            _state.update { it.copy(pendingBondConsent = initialBond) }
            return
        }

        // Ghost occupants simulate accepting after a brief delay
        scope.launch {
            delay(1000)
            val activeBond = initialBond.copy(
                firstConsent = ConsentStatus.ACCEPTED,
                secondConsent = ConsentStatus.ACCEPTED,
                status = BondStatus.ACTIVE,
            )
            _state.update { curr ->
                // Filter out any existing bond on either seat
                val cleanBonds = curr.activeBonds.filter {
                    it.firstSeat != firstSeat && it.secondSeat != firstSeat &&
                        it.firstSeat != secondSeat && it.secondSeat != secondSeat
                }
                curr.copy(activeBonds = cleanBonds + activeBond)
            }
            chat.addLast(systemMessage("Seats $firstSeat & $secondSeat are now connected."))
            publish()
        }
    }

    override suspend fun respondSeatBondConsent(bondId: String, accept: Boolean) {
        val pending = _state.value.pendingBondConsent
        if (pending == null || pending.bondId != bondId) return

        _state.update { it.copy(pendingBondConsent = null) }

        if (accept) {
            val activeBond = pending.copy(
                secondConsent = ConsentStatus.ACCEPTED,
                status = BondStatus.ACTIVE,
            )
            _state.update { curr ->
                val cleanBonds = curr.activeBonds.filter {
                    it.firstSeat != activeBond.firstSeat && it.secondSeat != activeBond.firstSeat &&
                        it.firstSeat != activeBond.secondSeat && it.secondSeat != activeBond.secondSeat
                }
                curr.copy(activeBonds = cleanBonds + activeBond)
            }
            chat.addLast(systemMessage("✨ Seat Bond accepted! Seats ${pending.firstSeat} & ${pending.secondSeat} are now bonded! 💕"))
        } else {
            chat.addLast(systemMessage("Seat Bond request between Seat ${pending.firstSeat} & Seat ${pending.secondSeat} was declined."))
        }
        publish()
    }

    override suspend fun detachSeatBond(bondId: String) {
        val target = _state.value.activeBonds.firstOrNull { it.bondId == bondId } ?: return
        _state.update { curr ->
            curr.copy(activeBonds = curr.activeBonds.filterNot { it.bondId == bondId })
        }
        chat.addLast(systemMessage("Seat bond between Seat ${target.firstSeat} and Seat ${target.secondSeat} was detached."))
        publish()
    }

    private fun invalidateBondsForSeat(seatIndex: Int, occupantName: String) {
        val bondsToInvalidate = _state.value.activeBonds.filter {
            it.firstSeat == seatIndex || it.secondSeat == seatIndex
        }
        if (bondsToInvalidate.isNotEmpty()) {
            _state.update { curr ->
                curr.copy(activeBonds = curr.activeBonds.filterNot { it in bondsToInvalidate })
            }
            chat.addLast(systemMessage("Seat bond was detached as $occupantName left the seat."))
        }
    }

    override suspend fun moveMemberSeat(identity: String, targetSeat: Int) {
        if (targetSeat !in 0 until VOICE_ROOM_STAGE_CAPACITY) return
        if (_state.value.isSeatLocked(targetSeat)) return
        if (members.any { it.seat == targetSeat }) return
        val current = members.firstOrNull { it.identity == identity } ?: return
        val oldSeat = current.seat
        members.replaceAll {
            if (it.identity == identity) {
                it.copy(seat = targetSeat, speaking = false)
            } else it
        }
        if (oldSeat != null && oldSeat != targetSeat) {
            invalidateBondsForSeat(oldSeat, current.name)
        }
        chat.addLast(systemMessage("${current.name} moved to ${if (targetSeat == PARTNER_SEAT_INDEX) "Partner seat" else "Seat $targetSeat"}."))
        publish()
    }

    override suspend fun toggleModerator(identity: String) {
        val target = members.firstOrNull { it.identity == identity } ?: return
        if (target.isHost) return
        val nextIsMod = target.role != "moderator"
        members.replaceAll {
            if (it.identity == identity) {
                it.copy(
                    role = if (nextIsMod) "moderator" else if (it.hasSeat) "speaker" else "listener",
                    frameRes = if (nextIsMod) R.drawable.vb_vr_frame_moderator else R.drawable.vb_vr_frame_speaker,
                )
            } else it
        }
        _state.update { curr ->
            val mods = if (nextIsMod) curr.moderatorIdentities + identity else curr.moderatorIdentities - identity
            curr.copy(moderatorIdentities = mods)
        }
        chat.addLast(systemMessage(if (nextIsMod) "${target.name} was assigned Moderator." else "${target.name} is no longer a Moderator."))
        publish()
    }

    override suspend fun inviteToSeat(identity: String, seat: Int) {
        if (seat !in 1..REGULAR_SEAT_END) return
        if (_state.value.isSeatLocked(seat) || members.any { it.seat == seat }) return
        val target = members.firstOrNull { it.identity == identity } ?: return
        if (target.isSelf) {
            takeSeat(seat)
        } else {
            members.replaceAll {
                if (it.identity == identity) it.copy(seat = seat, micOn = false, role = if (it.role == "listener") "speaker" else it.role)
                else it
            }
            chat.addLast(systemMessage("Invited ${target.name} to Seat $seat."))
            publish()
        }
    }

    override suspend fun setLocalMuteMember(identity: String, muted: Boolean) {
        _state.update { curr ->
            val updated = if (muted) curr.locallyMutedIdentities + identity else curr.locallyMutedIdentities - identity
            curr.copy(locallyMutedIdentities = updated)
        }
        val targetName = members.firstOrNull { it.identity == identity }?.name ?: "participant"
        chat.addLast(systemMessage(if (muted) "Muted $targetName locally for your device." else "Unmuted $targetName locally."))
        publish()
    }

    override suspend fun moderatorMuteSeat(seat: Int, muted: Boolean) {
        val occupant = members.firstOrNull { it.seat == seat } ?: return
        members.replaceAll {
            if (it.identity == occupant.identity) it.copy(micOn = !muted, speaking = false)
            else it
        }
        chat.addLast(systemMessage(if (muted) "Moderator muted ${occupant.name} on Seat $seat." else "${occupant.name} on Seat $seat was unmuted."))
        publish()
    }

    override suspend fun blockMember(identity: String) {
        val target = members.firstOrNull { it.identity == identity } ?: return
        if (target.isSelf) return
        members.removeAll { it.identity == identity }
        target.seat?.let { invalidateBondsForSeat(it, target.name) }
        _state.update { it.copy(blockedIdentities = it.blockedIdentities + identity) }
        chat.addLast(systemMessage("Blocked ${target.name} from your room session."))
        publish()
    }

    override suspend fun setAnnouncement(text: String) {
        val cleaned = text.trim()
        if (cleaned.isEmpty()) return
        _state.update { it.copy(announcement = cleaned) }
        chat.addLast(systemMessage("Room announcement updated."))
        publish()
    }

    override suspend fun setRoomThemeId(themeId: String) {
        _state.update { it.copy(roomThemeId = themeId) }
    }

    override suspend fun setFavorited(favorited: Boolean) {
        _state.update { it.copy(isFavorited = favorited) }
    }

    override suspend fun setSelfProfileVisuals(photoPath: String?, avatarRes: Int?, frameRes: Int?) {
        members.replaceAll {
            if (it.isSelf) {
                it.copy(
                    photoPath = photoPath,
                    avatarRes = avatarRes ?: it.avatarRes,
                    frameRes = frameRes ?: it.frameRes,
                )
            } else it
        }
        publish()
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
            // Host at Seat 0
            RoomMemberVo(
                identity = "ghost-host",
                name = "Neha",
                seat = HOST_SEAT_INDEX,
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
            // Regular Mic Seat 1
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
            // Regular Mic Seat 2
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
            // Regular Mic Seat 4
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
            // Regular Mic Seat 5
            RoomMemberVo(
                identity = "ghost-maya",
                name = "Maya",
                seat = 5,
                micOn = true,
                speaking = false,
                isSelf = false,
                joinedAtMs = 0L,
                role = "speaker",
                avatarRes = R.drawable.vb_vr_avatar_demo_05,
                frameRes = R.drawable.vb_vr_frame_speaker,
                level = 11,
            ),
        )

        val SCRIPT_LINES = listOf(
            "Arjun" to "Next track is acoustic guitar jam, who wants to queue up next? 🎸",
            "Neha" to "Welcome everyone joining tonight! Grab an open mic seat anytime.",
            "Vikram" to "Sent 3x roses to the host — great session tonight! 🌹",
            "Maya" to "Audio sounds super clear on speakers tonight.",
            "Tara" to "Muted for a moment while grabbing headphones, back shortly!",
        )

        val REPLIES = listOf(
            "Love the vibe in here! 💜",
            "Such a great acoustic set!",
            "Who wants to do a quick PK battle round?",
        )
    }
}
