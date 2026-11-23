package com.sundram.weplay

import com.sundram.weplay.data.AppNotification
import com.sundram.weplay.data.NotificationKind
import com.sundram.weplay.ui.nav.Routes
import com.sundram.weplay.ui.screens.relativeTime
import com.sundram.weplay.ui.theme.AuroraChatsTokens
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * Verification test suite for Aurora Paradise 3.0 — Chats Hybrid Redesign:
 * - Asset verification for all 54 ap_chats_hybrid_*.webp drawables in drawable-nodpi.
 * - Theme token color integrity against COLOR_TOKENS.json specification.
 * - Navigation routing verification for chats and conversations.
 * - Helper logic and notification mapping verification.
 */
class ChatsHybridUnitTest {

    @Test
    fun testAll54ChatsHybridAssetsExistInResources() {
        val hybridDrawables = listOf(
            R.drawable.ap_chats_hybrid_bg_alerts,
            R.drawable.ap_chats_hybrid_bg_conversation,
            R.drawable.ap_chats_hybrid_bg_inbox,
            R.drawable.ap_chats_hybrid_bg_people,
            R.drawable.ap_chats_hybrid_empty_no_alerts,
            R.drawable.ap_chats_hybrid_empty_no_chats,
            R.drawable.ap_chats_hybrid_empty_no_people,
            R.drawable.ap_chats_hybrid_empty_no_results,
            R.drawable.ap_chats_hybrid_frame_amethyst,
            R.drawable.ap_chats_hybrid_frame_crystal,
            R.drawable.ap_chats_hybrid_frame_rose,
            R.drawable.ap_chats_hybrid_frame_royal,
            R.drawable.ap_chats_hybrid_fx_aurora_ribbon,
            R.drawable.ap_chats_hybrid_fx_separator,
            R.drawable.ap_chats_hybrid_fx_stars,
            R.drawable.ap_chats_hybrid_header_palace,
            R.drawable.ap_chats_hybrid_ic_alerts,
            R.drawable.ap_chats_hybrid_ic_alert_dot,
            R.drawable.ap_chats_hybrid_ic_attach,
            R.drawable.ap_chats_hybrid_ic_back,
            R.drawable.ap_chats_hybrid_ic_bell,
            R.drawable.ap_chats_hybrid_ic_block,
            R.drawable.ap_chats_hybrid_ic_camera,
            R.drawable.ap_chats_hybrid_ic_chat,
            R.drawable.ap_chats_hybrid_ic_check,
            R.drawable.ap_chats_hybrid_ic_clear,
            R.drawable.ap_chats_hybrid_ic_compose,
            R.drawable.ap_chats_hybrid_ic_copy,
            R.drawable.ap_chats_hybrid_ic_delete,
            R.drawable.ap_chats_hybrid_ic_emoji,
            R.drawable.ap_chats_hybrid_ic_friend_accept,
            R.drawable.ap_chats_hybrid_ic_friend_add,
            R.drawable.ap_chats_hybrid_ic_friend_decline,
            R.drawable.ap_chats_hybrid_ic_gift,
            R.drawable.ap_chats_hybrid_ic_incoming,
            R.drawable.ap_chats_hybrid_ic_mic,
            R.drawable.ap_chats_hybrid_ic_mute,
            R.drawable.ap_chats_hybrid_ic_outgoing,
            R.drawable.ap_chats_hybrid_ic_people,
            R.drawable.ap_chats_hybrid_ic_photo,
            R.drawable.ap_chats_hybrid_ic_pin,
            R.drawable.ap_chats_hybrid_ic_read,
            R.drawable.ap_chats_hybrid_ic_reply,
            R.drawable.ap_chats_hybrid_ic_report,
            R.drawable.ap_chats_hybrid_ic_room_invite,
            R.drawable.ap_chats_hybrid_ic_search,
            R.drawable.ap_chats_hybrid_ic_send,
            R.drawable.ap_chats_hybrid_ic_settings,
            R.drawable.ap_chats_hybrid_ic_smile,
            R.drawable.ap_chats_hybrid_ic_unlock,
            R.drawable.ap_chats_hybrid_ic_unread,
            R.drawable.ap_chats_hybrid_ic_vip,
            R.drawable.ap_chats_hybrid_ic_voice_note,
            R.drawable.ap_chats_hybrid_ic_wallpaper,
        )

        assertEquals("Total hybrid assets must be exactly 54", 54, hybridDrawables.size)

        hybridDrawables.forEach { resId ->
            assertTrue("Resource ID $resId must be valid non-zero integer", resId != 0)
        }
    }

    @Test
    fun testAll54AssetFilesPresentOnDisk() {
        val resDir = File("src/main/res/drawable-nodpi")
        assertTrue("drawable-nodpi directory must exist", resDir.exists())

        val hybridFiles = resDir.listFiles { _, name ->
            name.startsWith("ap_chats_hybrid_") && name.endsWith(".webp")
        } ?: emptyArray()

        assertEquals("Exactly 54 WebP asset files must exist on disk in drawable-nodpi", 54, hybridFiles.size)

        hybridFiles.forEach { file ->
            assertTrue("File ${file.name} must be non-empty", file.length() > 0)
        }
    }

    @Test
    fun testColorTokensMatchColorTokensJson() {
        // Base = #13092F
        assertEquals(0xFF13092FUL, AuroraChatsTokens.BgBase.value shr 32)
        // Header = #1A0C46
        assertEquals(0xFF1A0C46UL, AuroraChatsTokens.Header.value shr 32)
        // Surface = #241448
        assertEquals(0xFF241448UL, AuroraChatsTokens.Surface.value shr 32)
        // SurfaceElevated = #31205F
        assertEquals(0xFF31205FUL, AuroraChatsTokens.SurfaceElevated.value shr 32)
        // TextPrimary = #FFF8FF
        assertEquals(0xFFFFF8FFUL, AuroraChatsTokens.TextPrimary.value shr 32)
        // TextSecondary = #CFC2EF
        assertEquals(0xFFCFC2EFUL, AuroraChatsTokens.TextSecondary.value shr 32)
        // GlowPurple = #CA74FF
        assertEquals(0xFFCA74FFUL, AuroraChatsTokens.GlowPurple.value shr 32)
        // CrystalCyan = #54CFFF
        assertEquals(0xFF54CFFFUL, AuroraChatsTokens.CrystalCyan.value shr 32)
        // ActiveCyan = #29CBE8
        assertEquals(0xFF29CBE8UL, AuroraChatsTokens.ActiveCyan.value shr 32)
        // RoyalGold = #FFD16F
        assertEquals(0xFFFFD16FUL, AuroraChatsTokens.RoyalGold.value shr 32)
        // RoseAlert = #FF639F
        assertEquals(0xFFFF639FUL, AuroraChatsTokens.RoseAlert.value shr 32)
        // OnlineGreen = #3DDCA8
        assertEquals(0xFF3DDCA8UL, AuroraChatsTokens.OnlineGreen.value shr 32)
        // IncomingBubble = #302457
        assertEquals(0xFF302457UL, AuroraChatsTokens.IncomingBubble.value shr 32)
        // OutgoingBubble = #6238C6
        assertEquals(0xFF6238C6UL, AuroraChatsTokens.OutgoingBubble.value shr 32)
        // SecondaryOutgoing = #285B9F
        assertEquals(0xFF285B9FUL, AuroraChatsTokens.SecondaryOutgoing.value shr 32)
    }

    @Test
    fun testNavigationRoutesPreserved() {
        assertEquals("tab_msg", Routes.MSG)
        assertEquals("chat/{userId}", Routes.CHAT)
        assertEquals("chat/user42", Routes.chatRoute("user42"))
        assertEquals("user/{userId}", Routes.USER)
        assertEquals("user/priya", Routes.userRoute("priya"))
        assertTrue("Routes.tabs must contain MSG tab", Routes.tabs.contains(Routes.MSG))
    }

    @Test
    fun testRelativeTimeFormatting() {
        val now = System.currentTimeMillis()
        assertEquals("now", relativeTime(now - 10_000L))
        assertEquals("5m", relativeTime(now - 300_000L))
        assertEquals("2h", relativeTime(now - 7_200_000L))
        assertEquals("3d", relativeTime(now - 259_200_000L))
        assertEquals("", relativeTime(0L))
    }

    @Test
    fun testNotificationItemKinds() {
        val notifReq = AppNotification(
            id = "n1",
            kind = NotificationKind.FRIEND_REQUEST,
            text = "Raj sent you a friend request.",
            userId = "raj",
            ts = System.currentTimeMillis(),
        )
        assertEquals(NotificationKind.FRIEND_REQUEST, notifReq.kind)
        assertEquals("raj", notifReq.userId)

        val notifRoom = AppNotification(
            id = "n2",
            kind = NotificationKind.ROOM_INVITE,
            text = "Join Tea Time Talks!",
            roomId = "room_1",
            ts = System.currentTimeMillis(),
        )
        assertEquals(NotificationKind.ROOM_INVITE, notifRoom.kind)
        assertEquals("room_1", notifRoom.roomId)
    }
}
