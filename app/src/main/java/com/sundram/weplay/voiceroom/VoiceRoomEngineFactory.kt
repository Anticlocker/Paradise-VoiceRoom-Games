package com.sundram.weplay.voiceroom

import android.content.Context
import kotlinx.coroutines.CoroutineScope

/**
 * Factory for creating [VoiceRoomEngine] instances.
 *
 * Selects [LiveKitVoiceRoomEngine] when LiveKit server settings are configured,
 * otherwise returns an offline [DemoVoiceRoomEngine].
 */
object VoiceRoomEngineFactory {

    fun create(appContext: Context, scope: CoroutineScope): VoiceRoomEngine {
        return if (VoiceRoomServer.isConfigured) {
            LiveKitVoiceRoomEngine(appContext.applicationContext, scope)
        } else {
            DemoVoiceRoomEngine(scope)
        }
    }
}
