package com.sundram.weplay.data

import android.content.Context
import kotlin.random.Random
import com.sundram.weplay.R

/**
 * Local demo messaging: users, chats, requests, notifications and blocking.
 * All state lives in SharedPreferences so it survives process restarts.
 *
 * The "other users" are simulated directory entries; incoming requests and
 * chat replies arrive in-app so the full flow can be exercised offline.
 */

data class DirectoryUser(
    val id: String,          // username, e.g. "priya"
    val name: String,        // display name
    val level: Int = 1,
    val bio: String = "Hey there, I am using WePlay.",
    val online: Boolean = false,
    val isOfficial: Boolean = false,
    val isFamily: Boolean = false,
    val isModerator: Boolean = false,
)

data class ReportEntry(
    val at: Long,
    val targetId: String,
    val reason: String,
)

data class GiftLog(
    val targetId: String,
    val giftName: String,
    val coins: Long,
    val at: Long,
)

enum class MsgType {
    TEXT,
    GIFT,
    IMAGE,
    VOICE,
    ROOM_INVITE,
    GAME_INVITE,
    PROFILE_SHARE,
    SYSTEM,
}

enum class MsgStatus {
    PENDING,
    SENT,
    DELIVERED,
    READ,
    FAILED,
}

data class ReplyQuote(
    val id: String,
    val senderName: String,
    val text: String,
    val type: MsgType = MsgType.TEXT,
)

data class ChatMessage(
    val id: String,
    val fromMe: Boolean,
    val text: String,
    val ts: Long,
    val read: Boolean = true,   // for my msgs: true = double tick
    val type: MsgType = MsgType.TEXT,
    val giftId: String? = null,      // GIFT
    val giftCost: Long = 0L,         // GIFT
    val giftQuantity: Int = 1,       // GIFT
    val mediaPath: String? = null,   // IMAGE / VOICE
    val durationMs: Long = 0L,       // VOICE
    val recalled: Boolean = false,
    val status: MsgStatus = if (read) MsgStatus.READ else MsgStatus.SENT,
    val replyTo: ReplyQuote? = null,
    val reactions: Map<String, List<String>> = emptyMap(), // emoji -> list of userIds
    val roomId: String? = null,      // ROOM_INVITE
    val roomTitle: String? = null,
    val roomCoverRes: Int? = null,
    val gameId: String? = null,      // GAME_INVITE
    val gameTitle: String? = null,
    val senderId: String? = null,    // for group messages
    val senderName: String? = null,
)

data class GroupInfo(
    val id: String,
    val name: String,
    val avatarRes: Int,
    val memberIds: List<String>,
    val ownerId: String,
    val description: String = "",
    val announcement: String = "",
    val createdAt: Long = System.currentTimeMillis(),
)

data class Chat(
    val userId: String,
    val messages: List<ChatMessage> = emptyList(),
    val lastTs: Long = 0L,
    val unread: Int = 0,
    val pinned: Boolean = false,
    val muted: Boolean = false,
    val wallpaper: Int = 0,      // 0=default; 1..3 alt backgrounds
    val draft: String = "",
    val lastReadTs: Long = 0L,
    val isGroup: Boolean = false,
    val isArchived: Boolean = false,
) {
    val lastMsg: ChatMessage? get() = messages.lastOrNull()
    val displayText: String get() {
        val lm = lastMsg ?: return "Say hello"
        return when {
            lm.recalled -> "Message recalled"
            lm.type == MsgType.GIFT -> {
                if (lm.text.isNotBlank()) lm.text
                else {
                    val total = lm.giftCost * lm.giftQuantity
                    if (total > 0) "Sent gift · $total coins" else "Gift"
                }
            }
            lm.type == MsgType.IMAGE -> "Photo"
            lm.type == MsgType.VOICE -> {
                if (lm.durationMs > 0) {
                    val sec = (lm.durationMs / 1000).coerceAtLeast(1)
                    "Voice note · 0:%02d".format(sec)
                } else "Voice note"
            }
            lm.type == MsgType.ROOM_INVITE -> "Voice room: ${lm.roomTitle ?: "Live Room"}"
            lm.type == MsgType.GAME_INVITE -> "Invited to play ${lm.gameTitle ?: "Game"}"
            lm.type == MsgType.PROFILE_SHARE -> "Shared a contact"
            lm.type == MsgType.SYSTEM -> lm.text
            else -> lm.text
        }
    }
}

enum class NotificationKind {
    FRIEND_REQUEST, FRIEND_ACCEPTED, MESSAGE, FOLLOW, GIFT, ROOM_INVITE,
}

data class AppNotification(
    val id: String,
    val kind: NotificationKind,
    val text: String,
    val ts: Long,
    val userId: String? = null,
    val read: Boolean = false,
    val roomId: String? = null,    // for ROOM_INVITE / gift room ref
)

class MessagingStore(context: Context, private val meProvider: () -> UserProfile?) {

    // balance and identity must reflect the latest saved profile, not the one from first composition
    private val me: UserProfile? get() = meProvider()

    private val prefs = context.applicationContext
        .getSharedPreferences("weplay_messaging", Context.MODE_PRIVATE)

    private fun set(key: String, values: Set<String>) =
        prefs.edit().putStringSet(key, values).apply()

    private fun get(key: String): Set<String> =
        prefs.getStringSet(key, emptySet())?.toSet() ?: emptySet()

    private fun listKey(prefix: String, id: String) = "${prefix}_$id"
    private fun ordered(key: String): List<String> =
        prefs.getString(key, null)?.split("\u0001")?.filter { it.isNotEmpty() } ?: emptyList()
    private fun saveOrdered(key: String, values: List<String>) =
        prefs.edit().putString(key, values.joinToString("\u0001")).apply()

    // ---------- users ----------

    companion object {
        val DIRECTORY = listOf(
            DirectoryUser("priya", "Priya", level = 5, bio = "Tea, KTV and late night talks.", online = true),
            DirectoryUser("raj", "Raj", level = 7, bio = "Werewolf nights all week.", online = true),
            DirectoryUser("ankit", "Ankit", level = 4, bio = "Ludo lover. DM for a match.", online = false),
            DirectoryUser("sneha", "Sneha", level = 3, bio = "Music and chill.", online = true),
            DirectoryUser("kavya", "Kavya", level = 6, bio = "Sing-along queen.", online = false),
            DirectoryUser("sam", "Sam", level = 2, bio = "Desi Gamers squad.", online = true),
            DirectoryUser("meera", "Meera", level = 8, bio = "Board games, all of them.", online = false),
            DirectoryUser("arjun", "Arjun", level = 5, bio = "Spy hunter. Catch me if you can.", online = true),
        )

        private val REPLY_POOL = listOf(
            "Haha nice", "Ok let's play", "Sure, why not!", "Wait, give me a sec",
            "That is funny", "Let us talk in the room later", "Sounds good!", "Really?",
            "Bet", "We can try tonight", "Deal", "Good one, tell me more",
        )
    }

    private val rnd = Random(System.currentTimeMillis())

    fun meId(): String = me?.id ?: "me"
    fun meName(): String = me?.name ?: "Me"

    /** All users shown in People: self first, then directory. */
    fun allUsers(): List<DirectoryUser> {
        val self = me?.let {
            DirectoryUser(it.id, it.name, level = it.level, online = true)
        }
        return listOfNotNull(self) + DIRECTORY
    }

    fun userById(id: String): DirectoryUser? = allUsers().firstOrNull { it.id == id }

    fun isMe(id: String): Boolean = id == meId()

    /**
     * Search by display name or username (case-insensitive substring).
     * Excludes self and blocked users.
     */
    fun search(query: String): List<DirectoryUser> {
        val q = query.trim().lowercase()
        if (q.isEmpty()) return emptyList()
        return allUsers().filter {
            !isMe(it.id) && !isBlocked(it.id) &&
                (it.name.lowercase().contains(q) || it.id.lowercase().contains(q))
        }
    }

    // ---------- friendships ----------

    private val friendsKey = "friends"
    private val incomingKey = "req_in"
    private val outgoingKey = "req_out"
    private val blockedKey = "blocked"

    fun friends(): List<DirectoryUser> =
        ordered(friendsKey).mapNotNull { userById(it) }

    fun isFriend(id: String): Boolean = ordered(friendsKey).contains(id)

    fun incomingRequests(): List<DirectoryUser> =
        ordered(incomingKey).mapNotNull { userById(it) }

    fun outgoingRequests(): List<DirectoryUser> =
        ordered(outgoingKey).mapNotNull { userById(it) }

    fun isBlocked(id: String): Boolean = get(blockedKey).contains(id)

    fun blockedUsers(): List<DirectoryUser> =
        get(blockedKey).mapNotNull { userById(it) }

    fun relationship(id: String): String = when {
        isMe(id) -> "self"
        isBlocked(id) -> "blocked"
        isFriend(id) -> "friend"
        ordered(incomingKey).contains(id) -> "incoming"
        ordered(outgoingKey).contains(id) -> "outgoing"
        else -> "none"
    }

    /** Send friend request; a reciprocal request is auto-accepted. */
    fun sendRequest(id: String): Boolean {
        if (isMe(id) || isBlocked(id) || isFriend(id)) return false
        val inc = ordered(incomingKey)
        if (inc.contains(id)) {           // they already asked -> accept
            acceptRequest(id)
            return true
        }
        val out = ordered(outgoingKey)
        if (!out.contains(id)) {
            saveOrdered(outgoingKey, out + id)
            pushNotification(NotificationKind.FRIEND_REQUEST, "Request sent to ${userById(id)?.name}", id)
        }
        return true
    }

    fun cancelRequest(id: String) {
        saveOrdered(outgoingKey, ordered(outgoingKey) - id)
    }

    fun acceptRequest(id: String) {
        saveOrdered(incomingKey, ordered(incomingKey) - id)
        saveOrdered(outgoingKey, ordered(outgoingKey) - id)
        val f = ordered(friendsKey)
        if (!f.contains(id)) saveOrdered(friendsKey, f + id)
        // seed a welcome chat so the conversation list has something
        if (chat(id) == null) {
            val ts = System.currentTimeMillis()
            setChat(Chat(userId = id, messages = listOf(
                ChatMessage("m0", fromMe = false, text = "Hey! Thanks for adding me.", ts = ts),
            ), lastTs = ts))
        }
        pushNotification(NotificationKind.FRIEND_ACCEPTED, "${userById(id)?.name} accepted your request", id)
    }

    fun declineRequest(id: String) {
        saveOrdered(incomingKey, ordered(incomingKey) - id)
    }

    fun removeFriend(id: String) {
        saveOrdered(friendsKey, ordered(friendsKey) - id)
    }

    // ---------- blocking / reporting ----------

    fun block(id: String) {
        set(blockedKey, get(blockedKey) + id)
        removeFriend(id)
        declineRequest(id)
        cancelRequest(id)
        dropPendingReply(id)
        // blocked users' chats no longer show as active conversations
        setChat(chat(id)?.copy(unread = 0) ?: Chat(userId = id))
        pushNotification(NotificationKind.FRIEND_REQUEST, "You blocked ${userById(id)?.name}", id)
    }

    fun unblock(id: String) {
        set(blockedKey, get(blockedKey) - id)
    }

    /** Reporting is a light-weight action in this demo: logs the report and blocks. */
    fun report(id: String, reason: String) {
        val ts = System.currentTimeMillis()
        val reports = prefs.getString("reports", null)
        val entry = "$ts|${meId()}|$id|$reason"
        prefs.edit().putString("reports", if (reports.isNullOrEmpty()) entry else "$reports\n$entry").apply()
        block(id)
    }

    fun reports(): List<ReportEntry> =
        prefs.getString("reports", null)
            ?.lineSequence()
            ?.mapNotNull(::decodeReport)
            ?.toList()
            .orEmpty()

    private fun decodeReport(line: String): ReportEntry? {
        val parts = line.split("|")
        if (parts.size < 4) return null
        return ReportEntry(
            at = parts[0].toLongOrNull() ?: return null,
            targetId = parts[2],
            reason = parts.drop(3).joinToString("|"),
        )
    }

    // ---------- chats ----------

    fun chat(id: String): Chat? {
        val json = prefs.getString(listKey("chat", id), null) ?: return null
        return decode(json, id)
    }

    private fun setChat(c: Chat) {
        prefs.edit().putString(listKey("chat", c.userId), encode(c)).apply()
    }

    fun allChats(): List<Chat> {
        flushDueReplies()
        val ids = get("chat_ids")
        return ids.mapNotNull { chat(it) }
            .filter { !it.isArchived && !isBlocked(it.userId) }
            .sortedWith(compareByDescending<Chat> { it.pinned }.thenByDescending { it.lastTs })
    }

    /** Gift messages even for blocked users, so wallet history stays intact. */
    fun giftLog(): List<GiftLog> =
        get("chat_ids").mapNotNull { chat(it) }.flatMap { c ->
            c.messages.filter { it.fromMe && it.type == MsgType.GIFT }.map {
                GiftLog(c.userId, it.text, it.giftCost * it.giftQuantity, it.ts)
            }
        }

    fun unreadTotal(): Int = allChats().sumOf { it.unread }

    fun openChat(id: String): Chat {
        flushDueReplies()
        val c = chat(id) ?: Chat(userId = id, isGroup = isGroup(id)).also { setChat(it) }
        val ids = get("chat_ids")
        if (!ids.contains(id)) set("chat_ids", ids + id)
        if (c.unread > 0 || c.lastReadTs < c.lastTs) {
            val updated = c.copy(unread = 0, lastReadTs = maxOf(c.lastReadTs, System.currentTimeMillis()))
            setChat(updated)
            return updated
        }
        return c
    }

    fun markChatRead(id: String) {
        chat(id)?.let {
            if (it.unread > 0) {
                setChat(it.copy(unread = 0, lastReadTs = System.currentTimeMillis()))
            }
        }
    }

    fun markAllChatsRead() {
        allChats().forEach { c ->
            if (c.unread > 0) {
                setChat(c.copy(unread = 0, lastReadTs = System.currentTimeMillis()))
            }
        }
    }

    fun archiveChat(id: String, archived: Boolean) {
        chat(id)?.let { setChat(it.copy(isArchived = archived)) }
    }

    fun isArchived(id: String): Boolean = chat(id)?.isArchived == true

    fun archivedChats(): List<Chat> =
        get("chat_ids").mapNotNull { chat(it) }.filter { it.isArchived && !isBlocked(it.userId) }
            .sortedByDescending { it.lastTs }

    private val pendingReplies: MutableSet<String> =
        (prefs.getStringSet("pending_replies", emptySet())?.toSet() ?: emptySet()).toMutableSet()

    private fun replyDueKey(id: String) = "reply_due_$id"

    private fun dropPendingReply(id: String) {
        if (pendingReplies.remove(id)) {
            prefs.edit()
                .remove(replyDueKey(id))
                .putStringSet("pending_replies", pendingReplies)
                .apply()
        }
    }

    private fun scheduleReply(id: String) {
        if (pendingReplies.contains(id)) return
        pendingReplies += id
        prefs.edit()
            .putStringSet("pending_replies", pendingReplies)
            .putLong(replyDueKey(id), System.currentTimeMillis() + 2500L + rnd.nextLong(1500L))
            .apply()
    }

    /** Outgoing messages start as sent (single tick) and flip to read after the reply. */
    fun sendMessage(id: String, text: String): ChatMessage? {
        val clean = text.trim()
        if (clean.isEmpty() || isBlocked(id)) return null
        flushDueReplies()
        val c = openChat(id)
        val msg = ChatMessage(
            id = "m${System.currentTimeMillis()}",
            fromMe = true,
            text = clean,
            ts = System.currentTimeMillis(),
            read = false,
            status = MsgStatus.SENT,
        )
        val updated = c.copy(
            messages = c.messages + msg,
            lastTs = msg.ts,
            lastReadTs = msg.ts,
        )
        setChat(updated)
        scheduleReply(id)
        return msg
    }

    /** Send a reply quoted message. */
    fun sendReplyMessage(chatId: String, text: String, replyTo: ReplyQuote): ChatMessage? {
        val clean = text.trim()
        if (clean.isEmpty() || isBlocked(chatId)) return null
        flushDueReplies()
        val c = openChat(chatId)
        val msg = ChatMessage(
            id = "m${System.currentTimeMillis()}",
            fromMe = true,
            text = clean,
            ts = System.currentTimeMillis(),
            read = false,
            type = MsgType.TEXT,
            replyTo = replyTo,
            status = MsgStatus.SENT,
        )
        val updated = c.copy(
            messages = c.messages + msg,
            lastTs = msg.ts,
            lastReadTs = msg.ts,
        )
        setChat(updated)
        scheduleReply(chatId)
        return msg
    }

    /** Send voice room invitation into DM or group. */
    fun sendRoomInviteMessage(chatId: String, roomId: String): ChatMessage? {
        if (isBlocked(chatId)) return null
        val room = Demo.rooms.firstOrNull { it.id == roomId } ?: Demo.rooms.first()
        flushDueReplies()
        val c = openChat(chatId)
        val msg = ChatMessage(
            id = "ri_${System.currentTimeMillis()}",
            fromMe = true,
            text = "Voice room invitation: ${room.title}",
            ts = System.currentTimeMillis(),
            read = false,
            type = MsgType.ROOM_INVITE,
            roomId = room.id,
            roomTitle = room.title,
            roomCoverRes = room.coverRes,
            status = MsgStatus.SENT,
        )
        setChat(c.copy(messages = c.messages + msg, lastTs = msg.ts))
        scheduleReply(chatId)
        pushNotification(
            NotificationKind.ROOM_INVITE,
            "You invited ${userById(chatId)?.name ?: "friend"} to ${room.title}",
            chatId,
            roomId,
        )
        return msg
    }

    /** Send game invitation into DM or group. */
    fun sendGameInviteMessage(chatId: String, gameId: String): ChatMessage? {
        if (isBlocked(chatId)) return null
        val gameTitle = if (gameId == "ludo") "Ludo Classic" else "Who's the Spy"
        flushDueReplies()
        val c = openChat(chatId)
        val msg = ChatMessage(
            id = "gi_${System.currentTimeMillis()}",
            fromMe = true,
            text = "Invited you to play $gameTitle",
            ts = System.currentTimeMillis(),
            read = false,
            type = MsgType.GAME_INVITE,
            gameId = gameId,
            gameTitle = gameTitle,
            status = MsgStatus.SENT,
        )
        setChat(c.copy(messages = c.messages + msg, lastTs = msg.ts))
        scheduleReply(chatId)
        return msg
    }

    /** Toggle emoji reaction on a message. */
    fun toggleReaction(chatId: String, messageId: String, emoji: String, userId: String = meId()) {
        val c = chat(chatId) ?: return
        val updatedMsgs = c.messages.map { m ->
            if (m.id == messageId) {
                val cur = m.reactions.toMutableMap()
                val userList = cur[emoji]?.toMutableList() ?: mutableListOf()
                if (userList.contains(userId)) {
                    userList.remove(userId)
                    if (userList.isEmpty()) cur.remove(emoji) else cur[emoji] = userList
                } else {
                    userList.add(userId)
                    cur[emoji] = userList
                }
                m.copy(reactions = cur)
            } else m
        }
        setChat(c.copy(messages = updatedMsgs))
    }

    /** Delivers simulated replies once their delay has passed. */
    fun flushDueReplies(): Boolean {
        if (pendingReplies.isEmpty()) return false
        val now = System.currentTimeMillis()
        var delivered = false
        pendingReplies.toList().forEach { id ->
            if (prefs.getLong(replyDueKey(id), 0L) > now) return@forEach
            pendingReplies.remove(id)
            val c = chat(id)
            if (c != null) {
                val marked = c.messages.map {
                    if (it.fromMe && !it.read) it.copy(read = true, status = MsgStatus.READ) else it
                }
                val replyAuthor = if (c.isGroup) {
                    val g = groupInfo(id)
                    g?.memberIds?.filter { it != meId() }?.randomOrNull(rnd) ?: "priya"
                } else id
                val replyName = userById(replyAuthor)?.name ?: replyAuthor
                val reply = ChatMessage(
                    id = "r${System.currentTimeMillis()}",
                    fromMe = false,
                    text = REPLY_POOL.random(rnd),
                    ts = System.currentTimeMillis(),
                    senderId = replyAuthor,
                    senderName = replyName,
                    status = MsgStatus.READ,
                )
                setChat(c.copy(messages = marked + reply, lastTs = reply.ts, unread = c.unread + 1))
                pushNotification(NotificationKind.MESSAGE, "$replyName: ${reply.text}", replyAuthor)
                delivered = true
            }
            prefs.edit().remove(replyDueKey(id)).apply()
        }
        prefs.edit().putStringSet("pending_replies", pendingReplies).apply()
        return delivered
    }

    /** Typing indicator — true while the other side is "typing". */
    fun isTyping(id: String): Boolean = pendingReplies.contains(id)

    /** Recall my message (replaces content; visible to both sides). 2-min window enforced by UI. */
    fun recallMessage(chatId: String, messageId: String) {
        val c = chat(chatId) ?: return
        val updated = c.messages.map {
            if (it.id == messageId && it.fromMe) it.copy(recalled = true, text = "") else it
        }
        setChat(c.copy(messages = updated))
    }

    /** Delete a message for myself only (other side unaffected, stays in their copy). */
    fun deleteMessageForMe(chatId: String, messageId: String) {
        val c = chat(chatId) ?: return
        val remaining = c.messages.filterNot { it.id == messageId }
        setChat(c.copy(messages = remaining, lastTs = remaining.lastOrNull()?.ts ?: 0L))
    }

    /** Send a gift in DM or group: debit the wallet and persist the gift bubble. */
    fun sendGift(
        chatId: String,
        giftId: String,
        onCoinsChanged: (Long) -> Unit,
    ): Boolean = sendRichGift(chatId, giftId, 1, onCoinsChanged)

    /** Send rich gift with quantity support. */
    fun sendRichGift(
        chatId: String,
        giftId: String,
        quantity: Int = 1,
        onCoinsChanged: (Long) -> Unit,
    ): Boolean {
        if (isBlocked(chatId)) return false
        val q = quantity.coerceAtLeast(1)
        val gift = DemoGifts.firstOrNull { it.id == giftId }
            ?: Demo.gifts.firstOrNull { it.id == giftId }?.let {
                GiftItem(it.id, it.name, "Stage Gift", R.drawable.ic_gift, 0xFFFF738F, it.coinCost.toLong())
            }
            ?: return false
        val unitCost = gift.cost
        val totalCost = unitCost * q
        if (totalCost <= 0L) return false
        val bal = me?.coins ?: 0L
        if (bal < totalCost) return false
        flushDueReplies()
        val c = openChat(chatId)
        val msg = ChatMessage(
            id = "g${System.currentTimeMillis()}",
            fromMe = true,
            text = "Sent ${gift.name}${if (q > 1) " x$q" else ""}",
            ts = System.currentTimeMillis(),
            read = false,
            type = MsgType.GIFT,
            giftId = gift.id,
            giftCost = unitCost,
            giftQuantity = q,
            status = MsgStatus.SENT,
        )
        setChat(c.copy(messages = c.messages + msg, lastTs = msg.ts))
        scheduleReply(chatId)
        pushNotification(
            NotificationKind.GIFT,
            "${userById(chatId)?.name ?: "Contact"} received your ${gift.name}${if (q > 1) " x$q" else ""}",
            chatId,
        )
        onCoinsChanged(bal - totalCost)
        return true
    }

    /** Send a rich message: IMAGE (compressed path) or VOICE (m4a path + duration). */
    fun sendMedia(chatId: String, type: MsgType, mediaPath: String, durationMs: Long = 0L): ChatMessage? {
        if (isBlocked(chatId)) return null
        flushDueReplies()
        val c = openChat(chatId)
        val label = if (type == MsgType.IMAGE) "Photo" else "Voice note"
        val msg = ChatMessage(
            id = "md${System.currentTimeMillis()}",
            fromMe = true,
            text = label,
            ts = System.currentTimeMillis(),
            read = false,
            type = type,
            mediaPath = mediaPath,
            durationMs = durationMs,
            status = MsgStatus.SENT,
        )
        setChat(c.copy(messages = c.messages + msg, lastTs = msg.ts))
        scheduleReply(chatId)
        return msg
    }

    fun pushRoomInvite(senderId: String, roomTitle: String, roomId: String) {
        pushNotification(NotificationKind.ROOM_INVITE,
            "${userById(senderId)?.name ?: senderId} invited you to $roomTitle", senderId, roomId)
    }

    fun pushGift(senderId: String, giftName: String) {
        pushNotification(NotificationKind.GIFT,
            "${userById(senderId)?.name ?: senderId} sent you a $giftName", senderId)
    }

    fun pushFollow(senderId: String) {
        pushNotification(NotificationKind.FOLLOW,
            "${userById(senderId)?.name ?: senderId} followed you", senderId)
    }

    fun setPinned(id: String, pinned: Boolean) {
        chat(id)?.let { setChat(it.copy(pinned = pinned)) }
    }

    fun setMuted(id: String, muted: Boolean) {
        chat(id)?.let { setChat(it.copy(muted = muted)) }
    }

    fun cycleWallpaper(id: String) {
        chat(id)?.let { setChat(it.copy(wallpaper = (it.wallpaper + 1) % 4)) }
    }

    fun setWallpaper(id: String, index: Int) {
        chat(id)?.let { setChat(it.copy(wallpaper = index % 4)) }
    }

    fun clearChat(id: String) {
        dropPendingReply(id)
        chat(id)?.let {
            setChat(it.copy(messages = emptyList(), unread = 0, lastTs = 0L, lastReadTs = 0L))
        }
    }

    fun deleteChat(id: String) {
        dropPendingReply(id)
        prefs.edit().remove(listKey("chat", id)).apply()
        set("chat_ids", get("chat_ids") - id)
    }

    fun saveDraft(id: String, draft: String) {
        chat(id)?.let { if (it.draft != draft) setChat(it.copy(draft = draft)) }
    }

    // ---------- Contact aliases & preferences ----------

    fun getAlias(userId: String): String? = prefs.getString("alias_$userId", null)
    fun setAlias(userId: String, alias: String) {
        if (alias.isBlank()) prefs.edit().remove("alias_$userId").apply()
        else prefs.edit().putString("alias_$userId", alias.trim()).apply()
    }

    fun isGameInvitesBlocked(userId: String): Boolean = prefs.getBoolean("block_game_$userId", false)
    fun setGameInvitesBlocked(userId: String, blocked: Boolean) {
        prefs.edit().putBoolean("block_game_$userId", blocked).apply()
    }

    fun isRoomInvitesBlocked(userId: String): Boolean = prefs.getBoolean("block_room_$userId", false)
    fun setRoomInvitesBlocked(userId: String, blocked: Boolean) {
        prefs.edit().putBoolean("block_room_$userId", blocked).apply()
    }

    // ---------- Voice Room Presence ----------

    fun userActiveRoom(userId: String): RoomCard? {
        return when (userId.lowercase()) {
            "priya" -> Demo.rooms.firstOrNull { it.id == "r1" }
            "raj" -> Demo.rooms.firstOrNull { it.id == "r2" }
            "ankit" -> Demo.rooms.firstOrNull { it.id == "r3" }
            "sam" -> Demo.rooms.firstOrNull { it.id == "r4" }
            "sneha" -> Demo.rooms.firstOrNull { it.id == "r5" }
            "kavya" -> Demo.rooms.firstOrNull { it.id == "r6" }
            "vikram" -> Demo.rooms.firstOrNull { it.id == "r7" }
            "arjun" -> Demo.rooms.firstOrNull { it.id == "r8" }
            else -> null
        }
    }

    fun isUserInVoiceRoom(userId: String): Boolean = userActiveRoom(userId) != null

    fun usersInVoiceRooms(): List<DirectoryUser> =
        allUsers().filter { isUserInVoiceRoom(it.id) && !isMe(it.id) && !isBlocked(it.id) }

    // ---------- Group chats ----------

    fun isGroup(id: String): Boolean = id.startsWith("group_") || get("group_ids").contains(id)

    fun groupInfo(id: String): GroupInfo? {
        val raw = prefs.getString("group_info_$id", null) ?: return null
        val parts = splitEscaped(raw)
        if (parts.size < 5) return null
        return GroupInfo(
            id = parts[0],
            name = parts[1],
            avatarRes = parts[2].toIntOrNull() ?: R.drawable.vb_chat_ic_groups,
            memberIds = parts[3].split(",").filter { it.isNotEmpty() },
            ownerId = parts[4],
            description = if (parts.size >= 6) parts[5] else "",
            announcement = if (parts.size >= 7) parts[6] else "",
            createdAt = if (parts.size >= 8) parts[7].toLongOrNull() ?: 0L else 0L,
        )
    }

    fun saveGroupInfo(info: GroupInfo) {
        val escName = info.name.replace("|", "/")
        val escDesc = info.description.replace("|", "/")
        val escAnn = info.announcement.replace("|", "/")
        val serialized = listOf(
            info.id, escName, info.avatarRes.toString(), info.memberIds.joinToString(","),
            info.ownerId, escDesc, escAnn, info.createdAt.toString(),
        ).joinToString("|")
        prefs.edit().putString("group_info_${info.id}", serialized).apply()
        val gids = get("group_ids")
        if (!gids.contains(info.id)) set("group_ids", gids + info.id)
    }

    fun createGroup(
        name: String,
        memberIds: List<String>,
        description: String = "",
        avatarRes: Int = R.drawable.vb_chat_avatar_07,
    ): GroupInfo {
        val cleanName = name.trim().ifEmpty { "New Group" }
        val id = "group_${System.currentTimeMillis()}"
        val allMembers = (listOf(meId()) + memberIds).distinct()
        val info = GroupInfo(
            id = id,
            name = cleanName,
            avatarRes = avatarRes,
            memberIds = allMembers,
            ownerId = meId(),
            description = description,
            createdAt = System.currentTimeMillis(),
        )
        saveGroupInfo(info)
        // create initial group chat entry
        val welcomeMsg = ChatMessage(
            id = "m_init",
            fromMe = true,
            text = "Welcome to $cleanName!",
            ts = System.currentTimeMillis(),
            type = MsgType.SYSTEM,
        )
        setChat(Chat(
            userId = id,
            messages = listOf(welcomeMsg),
            lastTs = welcomeMsg.ts,
            isGroup = true,
        ))
        set("chat_ids", get("chat_ids") + id)
        return info
    }

    fun allGroups(): List<GroupInfo> =
        get("group_ids").mapNotNull { groupInfo(it) }

    fun leaveGroup(groupId: String): Boolean {
        val g = groupInfo(groupId) ?: return false
        val nextMembers = g.memberIds.filterNot { it == meId() }
        if (nextMembers.isEmpty()) {
            prefs.edit().remove("group_info_$groupId").apply()
            set("group_ids", get("group_ids") - groupId)
            deleteChat(groupId)
        } else {
            saveGroupInfo(g.copy(memberIds = nextMembers))
            deleteChat(groupId)
        }
        return true
    }

    // ---------- notifications ----------

    fun notifications(): List<AppNotification> {
        val items = prefs.getString("notifs", null)?.split("\n")?.filter { it.isNotEmpty() }
            ?.mapNotNull { decodeNotification(it) } ?: emptyList()
        return items.sortedByDescending { it.ts }
    }

    fun unreadNotifications(): Int = notifications().count { !it.read }

    fun markNotificationsRead() {
        val items = notifications()
        prefs.edit().putString("notifs",
            items.joinToString("\n") { encode(it.copy(read = true)) }).apply()
    }

    fun clearNotifications() {
        prefs.edit().remove("notifs").apply()
    }

    private fun pushNotification(
        kind: NotificationKind, text: String, userId: String?, roomId: String? = null,
    ) {
        val n = AppNotification("n${System.currentTimeMillis()}_${rnd.nextInt(1000)}",
            kind, text, System.currentTimeMillis(), userId, roomId = roomId)
        val cur = notifications()
        val all = (listOf(n) + cur).take(50)
        prefs.edit().putString("notifs", all.joinToString("\n") { encode(it) }).apply()
    }

    /** One-time seed so Alerts/People/Chats are not empty on first use. */
    fun seedIfEmpty() {
        if (prefs.getBoolean("seeded", false)) return
        prefs.edit().putBoolean("seeded", true).apply()
        // demo incoming request + welcome chats
        saveOrdered(incomingKey, listOf("meera"))
        pushNotification(NotificationKind.FRIEND_REQUEST, "Meera sent you a friend request", "meera")
        val now = System.currentTimeMillis()
        val seedChats = listOf(
            Triple("priya", "you there?", now - 12L * 60_000),
            Triple("raj", "game starts soon", now - 3L * 3_600_000),
            Triple("ankit", "match tomorrow?", now - 2L * 60_000),
        )
        seedChats.forEach { (uid, msg, ts) ->
            setChat(Chat(
                userId = uid,
                messages = listOf(ChatMessage("s$ts", false, msg, ts)),
                lastTs = ts,
                unread = if (uid == "ankit") 2 else 1,
            ))
            set("chat_ids", get("chat_ids") + uid)
        }
        saveOrdered(friendsKey, listOf("priya", "raj", "ankit", "sneha"))

        // Seed initial groups
        val g1 = GroupInfo(
            id = "group_desi_gamers",
            name = "Desi Gamers Squad",
            avatarRes = R.drawable.vb_chat_avatar_07,
            memberIds = listOf("priya", "raj", "sam", meId()),
            ownerId = "raj",
            description = "Official squad for Ludo, Werewolf & Room party!",
            announcement = "Ludo tournament tonight at 9 PM!",
            createdAt = now - 86400_000L,
        )
        saveGroupInfo(g1)
        setChat(Chat(
            userId = g1.id,
            messages = listOf(
                ChatMessage("gm1", false, "Hey squad, who's up for Werewolf?", now - 45L * 60_000, senderId = "raj", senderName = "Raj"),
                ChatMessage("gm2", false, "I'm ready! Starting in 10 mins.", now - 20L * 60_000, senderId = "priya", senderName = "Priya"),
            ),
            lastTs = now - 20L * 60_000,
            unread = 2,
            isGroup = true,
        ))
        set("chat_ids", get("chat_ids") + g1.id)

        val g2 = GroupInfo(
            id = "group_night_owls",
            name = "Night Owls Lounge",
            avatarRes = R.drawable.vb_chat_avatar_04,
            memberIds = listOf("priya", "sneha", "kavya", meId()),
            ownerId = "priya",
            description = "Late night talks, chill music and acoustic KTV.",
            announcement = "Welcome to our chill lounge!",
            createdAt = now - 172800_000L,
        )
        saveGroupInfo(g2)
        setChat(Chat(
            userId = g2.id,
            messages = listOf(
                ChatMessage("gm3", false, "New acoustic playlist dropped in room 1!", now - 2L * 3600_000L, senderId = "sneha", senderName = "Sneha"),
            ),
            lastTs = now - 2L * 3600_000L,
            unread = 0,
            isGroup = true,
        ))
        set("chat_ids", get("chat_ids") + g2.id)

        pushNotification(NotificationKind.MESSAGE, "Ankit: match tomorrow?", "ankit")
        pushFollow("sneha")
        pushGift("priya", "Rose")
        pushRoomInvite("raj", "Werewolf Night", "r2")
    }

    // ---------- encoding (pipe-delimited, \-safe) ----------

    private fun encode(n: AppNotification): String {
        fun esc(s: String) = s.replace("\\", "\\\\").replace("|", "\\|").replace("\n", "\\n")
        return listOf(n.id, n.kind.name, esc(n.text), n.ts.toString(), n.userId ?: "",
            n.read.toString(), n.roomId ?: "").joinToString("|")
    }

    private fun decodeNotification(line: String): AppNotification? {
        val p = splitEscaped(line)
        if (p.size < 6) return null
        return AppNotification(
            id = p[0],
            kind = runCatching { NotificationKind.valueOf(p[1]) }.getOrDefault(NotificationKind.MESSAGE),
            text = p[2],
            ts = p[3].toLongOrNull() ?: 0,
            userId = p[4].ifEmpty { null },
            read = p[5] == "true",
            roomId = if (p.size >= 7) p[6].ifEmpty { null } else null,
        )
    }

    private fun encodeReactions(reactions: Map<String, List<String>>): String {
        if (reactions.isEmpty()) return ""
        return reactions.entries.joinToString(",") { "${it.key}:${it.value.joinToString(";")}" }
    }

    private fun decodeReactions(str: String): Map<String, List<String>> {
        if (str.isBlank()) return emptyMap()
        val out = mutableMapOf<String, List<String>>()
        str.split(",").forEach { pair ->
            val parts = pair.split(":")
            if (parts.size == 2) {
                out[parts[0]] = parts[1].split(";").filter { it.isNotEmpty() }
            }
        }
        return out
    }

    private fun encode(c: Chat): String {
        fun esc(s: String) = s.replace("\\", "\\\\").replace("|", "\\|")
            .replace(";", "\\;").replace("#", "\\#").replace("\n", "\\n")
        val msgs = c.messages.joinToString(";") { m ->
            val replyStr = m.replyTo?.let { "${it.id}^${it.senderName}^${it.text}^${it.type.name}" } ?: ""
            listOf(
                m.id, if (m.fromMe) "1" else "0", esc(m.text), m.ts.toString(),
                if (m.read) "1" else "0", m.type.name, m.giftId ?: "", m.giftCost.toString(),
                esc(m.mediaPath ?: ""), m.durationMs.toString(), if (m.recalled) "1" else "0",
                m.status.name, esc(m.roomId ?: ""), esc(m.roomTitle ?: ""), esc(m.gameId ?: ""), esc(m.gameTitle ?: ""),
                m.giftQuantity.toString(), esc(replyStr), esc(encodeReactions(m.reactions)),
                esc(m.senderId ?: ""), esc(m.senderName ?: "")
            ).joinToString("|")
        }
        return listOf(
            c.userId, msgs, c.lastTs.toString(), c.unread.toString(),
            if (c.pinned) "1" else "0", if (c.muted) "1" else "0",
            c.wallpaper.toString(), c.lastReadTs.toString(), esc(c.draft),
            if (c.isGroup) "1" else "0", if (c.isArchived) "1" else "0",
        ).joinToString("#")
    }

    private fun decode(json: String, fallbackId: String): Chat? {
        val parts = splitTop(json, '#')
        if (parts.size < 8) return null
        val userId = parts[0].ifEmpty { fallbackId }
        val msgs = if (parts[1].isEmpty()) emptyList() else {
            splitTop(parts[1], ';').mapNotNull { one ->
                val p = splitEscaped(one)
                if (p.size < 4) return@mapNotNull null
                val type = if (p.size >= 6) runCatching { MsgType.valueOf(p[5]) }.getOrDefault(MsgType.TEXT) else MsgType.TEXT
                val status = if (p.size >= 12) runCatching { MsgStatus.valueOf(p[11]) }.getOrDefault(MsgStatus.SENT) else (if (p.size >= 5 && p[4] == "1") MsgStatus.READ else MsgStatus.SENT)
                val replyStr = if (p.size >= 18) p[17] else ""
                val replyTo = if (replyStr.isNotEmpty()) {
                    val rparts = replyStr.split("^")
                    if (rparts.size >= 3) {
                        ReplyQuote(
                            id = rparts[0],
                            senderName = rparts[1],
                            text = rparts[2],
                            type = if (rparts.size >= 4) runCatching { MsgType.valueOf(rparts[3]) }.getOrDefault(MsgType.TEXT) else MsgType.TEXT
                        )
                    } else null
                } else null
                val reactions = if (p.size >= 19) decodeReactions(p[18]) else emptyMap()

                ChatMessage(
                    id = p[0], fromMe = p[1] == "1", text = p[2],
                    ts = p[3].toLongOrNull() ?: 0,
                    read = if (p.size >= 5) p[4] == "1" else true,
                    type = type,
                    giftId = if (p.size >= 7) p[6].ifEmpty { null } else null,
                    giftCost = if (p.size >= 8) p[7].toLongOrNull() ?: 0L else 0L,
                    mediaPath = if (p.size >= 9) p[8].ifEmpty { null } else null,
                    durationMs = if (p.size >= 10) p[9].toLongOrNull() ?: 0L else 0L,
                    recalled = if (p.size >= 11) p[10] == "1" else false,
                    status = status,
                    roomId = if (p.size >= 13) p[12].ifEmpty { null } else null,
                    roomTitle = if (p.size >= 14) p[13].ifEmpty { null } else null,
                    gameId = if (p.size >= 15) p[14].ifEmpty { null } else null,
                    gameTitle = if (p.size >= 16) p[15].ifEmpty { null } else null,
                    giftQuantity = if (p.size >= 17) p[16].toIntOrNull() ?: 1 else 1,
                    replyTo = replyTo,
                    reactions = reactions,
                    senderId = if (p.size >= 20) p[19].ifEmpty { null } else null,
                    senderName = if (p.size >= 21) p[20].ifEmpty { null } else null,
                )
            }
        }
        return Chat(
            userId = userId,
            messages = msgs,
            lastTs = parts[2].toLongOrNull() ?: 0,
            unread = parts[3].toIntOrNull() ?: 0,
            pinned = parts[4] == "1",
            muted = parts[5] == "1",
            wallpaper = parts[6].toIntOrNull() ?: 0,
            draft = if (parts.size >= 9) unescape(parts[8]) else "",
            lastReadTs = parts[7].toLongOrNull() ?: 0,
            isGroup = if (parts.size >= 10) parts[9] == "1" else false,
            isArchived = if (parts.size >= 11) parts[10] == "1" else false,
        )
    }

    /** Splits on a delimiter while keeping escape sequences intact. */
    private fun splitTop(s: String, delimiter: Char): List<String> {
        val out = mutableListOf<String>()
        val cur = StringBuilder()
        var i = 0
        while (i < s.length) {
            val ch = s[i]
            if (ch == '\\' && i + 1 < s.length) {
                cur.append(ch).append(s[i + 1]); i += 2
            } else if (ch == delimiter) {
                out += cur.toString(); cur.clear(); i++
            } else {
                cur.append(ch); i++
            }
        }
        out += cur.toString()
        return out
    }

    private fun unescape(s: String): String = splitEscaped(s).first()

    private fun splitEscaped(s: String): List<String> {
        val out = mutableListOf<String>()
        val cur = StringBuilder()
        var i = 0
        while (i < s.length) {
            val ch = s[i]
            if (ch == '\\' && i + 1 < s.length) {
                if (s[i + 1] == 'n') cur.append('\n') else cur.append(s[i + 1])
                i += 2
            } else if (ch == '|') {
                out += cur.toString(); cur.clear(); i++
            } else {
                cur.append(ch); i++
            }
        }
        out += cur.toString()
        return out
    }
}
