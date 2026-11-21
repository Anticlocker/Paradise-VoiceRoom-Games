package com.sundram.weplay

import com.sundram.weplay.data.Chat
import com.sundram.weplay.data.ChatMessage
import com.sundram.weplay.data.DirectoryUser
import com.sundram.weplay.data.MsgType
import com.sundram.weplay.ui.nav.Routes
import com.sundram.weplay.ui.screens.novaAvatarRes
import com.sundram.weplay.ui.screens.relativeTime
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit tests for VYBORA Nova Chats 2.0:
 * - Deterministic mascot avatar fallback mapping
 * - Routes and bottom navigation tabs integrity
 * - Conversation filtering and search logic
 * - Metadata badge verification (Official / Family / Moderator)
 * - Message formatting and relative timestamps
 */
class NovaChatsUnitTest {

    @Test
    fun testNovaAvatarDeterministicMapping() {
        val id1 = "user_alpha"
        val id2 = "user_beta"

        val res1First = novaAvatarRes(id1)
        val res1Second = novaAvatarRes(id1)

        // Must be stable (never rerandomizes across calls)
        assertEquals("Avatar mapping must be deterministic for the same ID", res1First, res1Second)
        assertNotEquals("Drawable resource ID must not be zero", 0, res1First)

        // Multiple distinct IDs should map correctly into the 12 mascot pack
        val sampleIds = (1..50).map { "user_sample_$it" }
        val mappedRes = sampleIds.map { novaAvatarRes(it) }.toSet()
        assertTrue("Mapping should utilize multiple avatars across distinct users", mappedRes.size > 1)
    }

    @Test
    fun testBottomNavTabsAndRouteIntegrity() {
        // Tab integrity: Games, Voice Room, Chats, Discover, Me
        assertEquals(5, Routes.tabs.size)
        assertTrue(Routes.tabs.contains(Routes.HUB))
        assertTrue(Routes.tabs.contains(Routes.ROOM))
        assertTrue(Routes.tabs.contains(Routes.MSG))
        assertTrue(Routes.tabs.contains(Routes.DISCOVER))
        assertTrue(Routes.tabs.contains(Routes.ME))

        // Stable chat routes
        assertEquals("chat/user42", Routes.chatRoute("user42"))
        assertEquals("user/user42", Routes.userRoute("user42"))
        assertEquals("profile/detail/user42", Routes.userDetailRoute("user42"))
    }

    @Test
    fun testChatFilterLogic() {
        val now = System.currentTimeMillis()
        val chats = listOf(
            Chat(userId = "priya", unread = 2, messages = listOf(ChatMessage("m1", false, "Hey there", now))),
            Chat(userId = "raj", unread = 0, messages = listOf(ChatMessage("m2", false, "Game soon", now))),
            Chat(userId = "ankit", unread = 1, messages = listOf(ChatMessage("m3", false, "Match?", now))),
        )

        // Unread filter
        val unreadChats = chats.filter { it.unread > 0 }
        assertEquals(2, unreadChats.size)
        assertTrue(unreadChats.all { it.unread > 0 })
        assertFalse(unreadChats.any { it.userId == "raj" })

        // Search query filter
        val searchResults = chats.filter { chat ->
            chat.userId.contains("pri", ignoreCase = true) ||
                chat.displayText.contains("pri", ignoreCase = true)
        }
        assertEquals(1, searchResults.size)
        assertEquals("priya", searchResults[0].userId)
    }

    @Test
    fun testOfficialAndFamilyMetadataIntegrity() {
        val regularUser = DirectoryUser(id = "user1", name = "User One")
        assertFalse("Regular user must not have official flag", regularUser.isOfficial)
        assertFalse("Regular user must not have family flag", regularUser.isFamily)
        assertFalse("Regular user must not have moderator flag", regularUser.isModerator)

        val officialUser = DirectoryUser(id = "vybora_official", name = "VYBORA Official", isOfficial = true)
        assertTrue("Official user must reflect genuine isOfficial metadata", officialUser.isOfficial)

        val familyUser = DirectoryUser(id = "family_member", name = "Family Member", isFamily = true)
        assertTrue("Family member must reflect genuine isFamily metadata", familyUser.isFamily)
    }

    @Test
    fun testRelativeTimeFormatting() {
        assertEquals("", relativeTime(0L))

        val now = System.currentTimeMillis()
        assertEquals("now", relativeTime(now))

        val tenMinutesAgo = now - 10 * 60_000L
        assertEquals("10m", relativeTime(tenMinutesAgo))

        val threeHoursAgo = now - 3 * 3_600_000L
        assertEquals("3h", relativeTime(threeHoursAgo))

        val twoDaysAgo = now - 2 * 86_400_000L
        assertEquals("2d", relativeTime(twoDaysAgo))
    }

    @Test
    fun testChatDisplayTextVariations() {
        val now = System.currentTimeMillis()

        val textChat = Chat("u1", messages = listOf(ChatMessage("1", false, "Hello", now, type = MsgType.TEXT)))
        assertEquals("Hello", textChat.displayText)

        val giftChat = Chat("u2", messages = listOf(ChatMessage("2", false, "", now, type = MsgType.GIFT, giftId = "rose")))
        assertEquals("Gift", giftChat.displayText)

        val photoChat = Chat("u3", messages = listOf(ChatMessage("3", false, "", now, type = MsgType.IMAGE, mediaPath = "/path/to/img.webp")))
        assertEquals("Photo", photoChat.displayText)

        val voiceChat = Chat("u4", messages = listOf(ChatMessage("4", false, "", now, type = MsgType.VOICE, mediaPath = "/path/to/voice.m4a")))
        assertEquals("Voice note", voiceChat.displayText)

        val recalledChat = Chat("u5", messages = listOf(ChatMessage("5", false, "Old text", now, recalled = true)))
        assertEquals("Message recalled", recalledChat.displayText)

        val emptyChat = Chat("u6", messages = emptyList())
        assertEquals("Say hello", emptyChat.displayText)

        val roomInviteChat = Chat("u7", messages = listOf(ChatMessage("7", false, "", now, type = MsgType.ROOM_INVITE, roomTitle = "Tea Time Talks")))
        assertEquals("Voice room: Tea Time Talks", roomInviteChat.displayText)

        val gameInviteChat = Chat("u8", messages = listOf(ChatMessage("8", false, "", now, type = MsgType.GAME_INVITE, gameTitle = "Ludo Classic")))
        assertEquals("Invited to play Ludo Classic", gameInviteChat.displayText)

        val richGiftChat = Chat("u9", messages = listOf(ChatMessage("9", false, "Sent Rose x5", now, type = MsgType.GIFT, giftId = "rose", giftCost = 10L, giftQuantity = 5)))
        assertEquals("Sent Rose x5", richGiftChat.displayText)
    }

    @Test
    fun testGroupChatModelAndFiltering() {
        val now = System.currentTimeMillis()
        val individualChat = Chat(userId = "priya", isGroup = false, messages = listOf(ChatMessage("1", false, "Hello", now)))
        val groupChat = Chat(userId = "group_12345", isGroup = true, messages = listOf(ChatMessage("2", false, "Hey squad", now)))

        val allChats = listOf(individualChat, groupChat)

        // All filter
        assertEquals(2, allChats.size)

        // Groups filter
        val groupFiltered = allChats.filter { it.isGroup }
        assertEquals(1, groupFiltered.size)
        assertEquals("group_12345", groupFiltered[0].userId)
        assertTrue(groupFiltered[0].isGroup)

        // Direct friends only filter
        val directChats = allChats.filter { !it.isGroup }
        assertEquals(1, directChats.size)
        assertEquals("priya", directChats[0].userId)
    }

    @Test
    fun testEmojiReactionsAndReplyQuoteIntegrity() {
        val quote = com.sundram.weplay.data.ReplyQuote(
            id = "msg_orig",
            senderName = "Priya",
            text = "Are you joining the voice lounge tonight?",
            type = MsgType.TEXT,
        )

        val reactionsMap = mapOf(
            "❤️" to listOf("user1", "user2"),
            "🔥" to listOf("user1"),
        )

        val msg = ChatMessage(
            id = "msg_reply",
            fromMe = true,
            text = "Yes, absolutely!",
            ts = System.currentTimeMillis(),
            replyTo = quote,
            reactions = reactionsMap,
        )

        assertNotNull(msg.replyTo)
        assertEquals("Priya", msg.replyTo?.senderName)
        assertEquals("Are you joining the voice lounge tonight?", msg.replyTo?.text)

        assertEquals(2, msg.reactions.size)
        assertEquals(2, msg.reactions["❤️"]?.size)
        assertTrue(msg.reactions["❤️"]?.contains("user1") == true)
        assertTrue(msg.reactions["❤️"]?.contains("user2") == true)
        assertEquals(1, msg.reactions["🔥"]?.size)
    }

    @Test
    fun testNovaAvatarResBoundaryConditions() {
        // Empty string
        val resEmpty = novaAvatarRes("")
        assertNotEquals(0, resEmpty)

        // Strings with various hash codes
        val testStrings = listOf(
            "a", "b", "c", "123", "!@#$%",
            "polygenelubricants", // Known Java string whose hashCode is Integer.MIN_VALUE
            "abcdefghijklmnopqrstuvwxyz",
            "test_user_id_with_long_characters_and_symbols_99999",
        )
        for (str in testStrings) {
            val res = novaAvatarRes(str)
            assertNotEquals("Avatar resource must never be 0 for input '$str'", 0, res)
        }
    }

    @Test
    fun testChatRouteIntegrity() {
        assertEquals("chat/priya", Routes.chatRoute("priya"))
        assertEquals("chat/group_desi_gamers", Routes.chatRoute("group_desi_gamers"))
        assertEquals("chat/user123", Routes.chatRoute("user123"))
    }
}
