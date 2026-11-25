package com.sundram.weplay.voiceroom

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Holds at most ONE active live voice room engine instance across screen navigation so:
 * - Minimizing a voice room preserves the active audio/chat session.
 * - Returning to the minimized room reuses the existing engine without creating a duplicate connection.
 * - Joining another room or explicitly leaving properly stops and releases the previous engine.
 */
object VoiceRoomSessionHolder {

    data class MinimizedRoomSummary(
        val roomId: String,
        val title: String,
        val participantCount: Int,
        val selfMicOn: Boolean,
    )

    private var activeRoomId: String? = null
    private var activeTitle: String = "Classic Lounge"
    private var activeEngine: VoiceRoomEngine? = null
    private var startedForIdentity: String? = null
    private var sessionScope: CoroutineScope? = null

    private val _minimizedSummary = MutableStateFlow<MinimizedRoomSummary?>(null)
    val minimizedSummary: StateFlow<MinimizedRoomSummary?> = _minimizedSummary.asStateFlow()

    @Synchronized
    fun acquireEngine(
        appContext: Context,
        roomId: String,
        roomTitle: String,
        identity: String,
        displayName: String,
    ): VoiceRoomEngine {
        val current = activeEngine
        if (current != null && activeRoomId == roomId && startedForIdentity == identity) {
            activeTitle = roomTitle
            _minimizedSummary.value = null
            return current
        }

        // Switching to a different room: release old session cleanly first.
        val oldEngine = current
        val oldScope = sessionScope
        val newScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
        sessionScope = newScope

        if (oldEngine != null) {
            (oldScope ?: CoroutineScope(SupervisorJob() + Dispatchers.Default)).launch {
                oldEngine.stop()
            }
        }

        val created = VoiceRoomEngineFactory.create(appContext.applicationContext, newScope)
        activeRoomId = roomId
        activeTitle = roomTitle
        activeEngine = created
        startedForIdentity = identity
        _minimizedSummary.value = null

        newScope.launch {
            created.start(roomId, identity, displayName)
        }
        return created
    }

    @Synchronized
    fun minimizeCurrentRoom() {
        val engine = activeEngine ?: return
        val rid = activeRoomId ?: return
        val snapshot = engine.state.value
        _minimizedSummary.value = MinimizedRoomSummary(
            roomId = rid,
            title = activeTitle,
            participantCount = snapshot.members.size.coerceAtLeast(1),
            selfMicOn = snapshot.selfMicOn,
        )
    }

    @Synchronized
    fun leaveAndRelease() {
        val engineToStop = activeEngine
        val scopeToStop = sessionScope
        activeEngine = null
        activeRoomId = null
        startedForIdentity = null
        sessionScope = null
        _minimizedSummary.value = null

        if (engineToStop != null) {
            (scopeToStop ?: CoroutineScope(SupervisorJob() + Dispatchers.Default)).launch {
                engineToStop.stop()
            }
        }
    }

    fun isSameActiveRoom(roomId: String): Boolean =
        activeEngine != null && activeRoomId == roomId
}
