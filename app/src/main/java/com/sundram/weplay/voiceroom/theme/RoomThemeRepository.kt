package com.sundram.weplay.voiceroom.theme

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject

/**
 * Pure domain logic + Android preference store for room themes, entitlements,
 * created rooms, room announcements, and moderation settings.
 *
 * Designed so pure JVM unit tests can test all ownership, equip, price-null,
 * permission, and crest-unlock rules using InMemoryRoomThemeStorage without Android mocks.
 */
interface RoomThemeStorage {
    fun getEquippedThemeId(roomId: String): String
    fun setEquippedThemeId(roomId: String, themeId: String)
    fun getRoomUnlockedThemes(roomId: String): Set<String>
    fun grantRoomUnlockedTheme(roomId: String, themeId: String)
    fun getUserOwnedThemes(userId: String): Set<String>
    fun grantUserOwnedTheme(userId: String, themeId: String)
    fun getAnnouncement(roomId: String): String?
    fun setAnnouncement(roomId: String, text: String)
    fun getCreatedRooms(): List<CreatedRoomRecord>
    fun saveCreatedRoom(record: CreatedRoomRecord)
}

class InMemoryRoomThemeStorage : RoomThemeStorage {
    private val equipped = mutableMapOf<String, String>()
    private val roomUnlocks = mutableMapOf<String, MutableSet<String>>()
    private val userOwned = mutableMapOf<String, MutableSet<String>>()
    private val announcements = mutableMapOf<String, String>()
    private val createdRooms = mutableListOf<CreatedRoomRecord>()

    override fun getEquippedThemeId(roomId: String): String =
        equipped[roomId] ?: CLASSIC_LOUNGE_ID

    override fun setEquippedThemeId(roomId: String, themeId: String) {
        equipped[roomId] = themeId
    }

    override fun getRoomUnlockedThemes(roomId: String): Set<String> =
        (roomUnlocks[roomId] ?: emptySet()) + CLASSIC_LOUNGE_ID

    override fun grantRoomUnlockedTheme(roomId: String, themeId: String) {
        roomUnlocks.getOrPut(roomId) { mutableSetOf(CLASSIC_LOUNGE_ID) }.add(themeId)
    }

    override fun getUserOwnedThemes(userId: String): Set<String> =
        (userOwned[userId] ?: emptySet()) + CLASSIC_LOUNGE_ID

    override fun grantUserOwnedTheme(userId: String, themeId: String) {
        userOwned.getOrPut(userId) { mutableSetOf(CLASSIC_LOUNGE_ID) }.add(themeId)
    }

    override fun getAnnouncement(roomId: String): String? = announcements[roomId]

    override fun setAnnouncement(roomId: String, text: String) {
        announcements[roomId] = text
    }

    override fun getCreatedRooms(): List<CreatedRoomRecord> = createdRooms.toList()

    override fun saveCreatedRoom(record: CreatedRoomRecord) {
        createdRooms.removeAll { it.id == record.id }
        createdRooms.add(0, record)
    }
}

class SharedPrefsRoomThemeStorage(context: Context) : RoomThemeStorage {
    private val prefs = context.applicationContext.getSharedPreferences("weplay_room_themes", Context.MODE_PRIVATE)

    override fun getEquippedThemeId(roomId: String): String {
        val raw = prefs.getString("equipped_$roomId", null)
        return if (RoomThemeCatalog.isKnownTheme(raw)) raw!! else CLASSIC_LOUNGE_ID
    }

    override fun setEquippedThemeId(roomId: String, themeId: String) {
        prefs.edit().putString("equipped_$roomId", themeId).apply()
    }

    override fun getRoomUnlockedThemes(roomId: String): Set<String> {
        val raw = prefs.getStringSet("room_ent_$roomId", emptySet()) ?: emptySet()
        return raw + CLASSIC_LOUNGE_ID
    }

    override fun grantRoomUnlockedTheme(roomId: String, themeId: String) {
        val next = getRoomUnlockedThemes(roomId).toMutableSet()
        next.add(themeId)
        prefs.edit().putStringSet("room_ent_$roomId", next).apply()
    }

    override fun getUserOwnedThemes(userId: String): Set<String> {
        val raw = prefs.getStringSet("user_ent_$userId", emptySet()) ?: emptySet()
        return raw + CLASSIC_LOUNGE_ID
    }

    override fun grantUserOwnedTheme(userId: String, themeId: String) {
        val next = getUserOwnedThemes(userId).toMutableSet()
        next.add(themeId)
        prefs.edit().putStringSet("user_ent_$userId", next).apply()
    }

    override fun getAnnouncement(roomId: String): String? =
        prefs.getString("announcement_$roomId", null)

    override fun setAnnouncement(roomId: String, text: String) {
        prefs.edit().putString("announcement_$roomId", text.trim()).apply()
    }

    override fun getCreatedRooms(): List<CreatedRoomRecord> {
        val raw = prefs.getString("created_rooms_json", null) ?: return emptyList()
        return runCatching {
            val arr = JSONArray(raw)
            buildList {
                for (i in 0 until arr.length()) {
                    val o = arr.getJSONObject(i)
                    add(
                        CreatedRoomRecord(
                            id = o.getString("id"),
                            title = o.getString("title"),
                            tag = o.optString("tag", "Friends"),
                            modeTitle = o.optString("modeTitle", "Normal"),
                            hostName = o.optString("hostName", "Host"),
                            announcement = o.optString("announcement", ""),
                            isPrivate = o.optBoolean("isPrivate", false),
                            hasPasscode = o.optBoolean("hasPasscode", false),
                            themeId = o.optString("themeId", CLASSIC_LOUNGE_ID)
                                .takeIf { RoomThemeCatalog.isKnownTheme(it) } ?: CLASSIC_LOUNGE_ID,
                            createdAtMillis = o.optLong("createdAtMillis", 0L),
                        )
                    )
                }
            }
        }.getOrDefault(emptyList())
    }

    override fun saveCreatedRoom(record: CreatedRoomRecord) {
        val list = getCreatedRooms().filterNot { it.id == record.id }.toMutableList()
        list.add(0, record)
        val arr = JSONArray()
        list.take(25).forEach { r ->
            arr.put(
                JSONObject().apply {
                    put("id", r.id)
                    put("title", r.title)
                    put("tag", r.tag)
                    put("modeTitle", r.modeTitle)
                    put("hostName", r.hostName)
                    put("announcement", r.announcement)
                    put("isPrivate", r.isPrivate)
                    put("hasPasscode", r.hasPasscode)
                    put("themeId", r.themeId)
                    put("createdAtMillis", r.createdAtMillis)
                }
            )
        }
        prefs.edit().putString("created_rooms_json", arr.toString()).apply()
    }
}

class RoomThemeRepository(
    private val storage: RoomThemeStorage,
    private val catalogProvider: () -> List<RoomThemeSpec> = { RoomThemeCatalog.entries },
) {
    private val _revision = MutableStateFlow(0)
    val revision: StateFlow<Int> = _revision.asStateFlow()

    fun getEquippedThemeId(roomId: String): String = storage.getEquippedThemeId(roomId)

    fun buildCatalogForRoom(
        roomId: String,
        userId: String,
        crestLevel: Int,
    ): List<RoomThemeCatalogItem> {
        val equippedId = storage.getEquippedThemeId(roomId)
        val roomUnlocked = storage.getRoomUnlockedThemes(roomId)
        val userOwned = storage.getUserOwnedThemes(userId)

        return catalogProvider().map { spec ->
            val unlockedByCrest = spec.unlockType == ThemeUnlockType.CREST &&
                spec.requiredCrestLevel != null &&
                crestLevel >= spec.requiredCrestLevel
            val owned = spec.unlockType == ThemeUnlockType.FREE ||
                roomUnlocked.contains(spec.id) ||
                userOwned.contains(spec.id) ||
                unlockedByCrest
            val lockReason = when {
                owned -> null
                spec.unlockType == ThemeUnlockType.CREST && spec.requiredCrestLevel != null ->
                    "Unlocks at Room Crest Lv.${spec.requiredCrestLevel}"
                spec.unlockType == ThemeUnlockType.STORE && spec.priceCoins == null ->
                    "Purchase currently unavailable (price not configured)"
                spec.unlockType == ThemeUnlockType.STORE ->
                    "Store exclusive · ${spec.priceCoins} Coins"
                else -> "Locked"
            }

            RoomThemeCatalogItem(
                spec = spec,
                resources = RoomThemeResourceRegistry.resourcesFor(spec.id),
                isOwned = owned,
                isUnlockedByCrest = unlockedByCrest,
                isEquipped = spec.id == equippedId,
                lockReason = lockReason,
            )
        }
    }

    fun filterByTab(items: List<RoomThemeCatalogItem>, tab: RoomThemeTab): List<RoomThemeCatalogItem> =
        when (tab) {
            RoomThemeTab.ALL -> items
            RoomThemeTab.MY_THEMES -> items.filter { it.canEquip }
            RoomThemeTab.CREST_REWARDS -> items.filter { it.spec.unlockType == ThemeUnlockType.CREST }
            RoomThemeTab.STORE_EXCLUSIVES -> items.filter { it.spec.unlockType == ThemeUnlockType.STORE }
        }

    fun equipTheme(
        roomId: String,
        themeId: String,
        userId: String,
        crestLevel: Int,
        isHostOrAuthorized: Boolean,
    ): ThemeActionResult {
        if (!isHostOrAuthorized) {
            return ThemeActionResult.Unauthorized("Only the room host or a moderator can change the room theme.")
        }
        if (!RoomThemeCatalog.isKnownTheme(themeId)) {
            return ThemeActionResult.Unavailable("Unknown theme: $themeId")
        }
        val items = buildCatalogForRoom(roomId, userId, crestLevel)
        val target = items.firstOrNull { it.spec.id == themeId }
            ?: return ThemeActionResult.Unavailable("Theme not found.")

        if (!target.canEquip) {
            return when (target.spec.unlockType) {
                ThemeUnlockType.CREST -> ThemeActionResult.Locked(
                    reason = "Requires Room Crest Lv.${target.spec.requiredCrestLevel ?: 1} (current Lv.$crestLevel).",
                    requiredCrestLevel = target.spec.requiredCrestLevel,
                )
                ThemeUnlockType.STORE -> if (target.spec.priceCoins == null) {
                    ThemeActionResult.Unavailable("Purchase currently unavailable (price not configured).")
                } else {
                    ThemeActionResult.Locked("Purchase in Room Themes Store for ${target.spec.priceCoins} Coins.")
                }
                ThemeUnlockType.FREE -> ThemeActionResult.Locked("Locked.")
            }
        }

        storage.setEquippedThemeId(roomId, themeId)
        _revision.value += 1
        return ThemeActionResult.Success("${target.spec.name} applied to room.", themeId)
    }

    /**
     * Enforces strict pricing validation:
     * - NULL priceCoins MUST NOT be treated as 0 coins and MUST return Unavailable.
     * - Prevents duplicate purchases of an already-owned theme.
     */
    fun purchaseStoreTheme(
        themeId: String,
        userId: String,
        currentCoinBalance: Long,
        onDeductCoins: (Int) -> Boolean,
    ): ThemeActionResult {
        val spec = catalogProvider().firstOrNull { it.id == themeId }
            ?: return ThemeActionResult.Unavailable("Theme not found.")

        if (spec.unlockType != ThemeUnlockType.STORE) {
            return ThemeActionResult.Unavailable("${spec.name} is not a store-purchasable theme.")
        }
        if (storage.getUserOwnedThemes(userId).contains(spec.id)) {
            return ThemeActionResult.Success("Already owned: ${spec.name}.", spec.id)
        }
        val price = spec.priceCoins
        if (price == null || price <= 0) {
            return ThemeActionResult.Unavailable("Purchase currently unavailable (price not configured).")
        }
        if (currentCoinBalance < price) {
            return ThemeActionResult.InsufficientCoins(requiredCoins = price, currentBalance = currentCoinBalance)
        }
        val debited = onDeductCoins(price)
        if (!debited) {
            return ThemeActionResult.Unavailable("Wallet update declined.")
        }
        storage.grantUserOwnedTheme(userId, spec.id)
        _revision.value += 1
        return ThemeActionResult.Success("Unlocked ${spec.name}!", spec.id)
    }

    fun syncUnlockedThemesForCrest(roomId: String, crestLevel: Int): List<String> {
        val current = storage.getRoomUnlockedThemes(roomId)
        val granted = mutableListOf<String>()
        catalogProvider().forEach { spec ->
            val req = spec.requiredCrestLevel
            if (spec.unlockType == ThemeUnlockType.CREST && req != null && crestLevel >= req && !current.contains(spec.id)) {
                storage.grantRoomUnlockedTheme(roomId, spec.id)
                granted += spec.id
            }
        }
        if (granted.isNotEmpty()) _revision.value += 1
        return granted
    }

    fun getAnnouncement(roomId: String, fallback: String): String =
        storage.getAnnouncement(roomId)?.takeIf { it.isNotBlank() } ?: fallback

    fun updateAnnouncement(roomId: String, text: String, isAuthorized: Boolean): Boolean {
        if (!isAuthorized) return false
        storage.setAnnouncement(roomId, text)
        _revision.value += 1
        return true
    }

    fun registerCreatedRoom(record: CreatedRoomRecord) {
        storage.saveCreatedRoom(record)
        storage.setEquippedThemeId(record.id, record.themeId)
        if (record.announcement.isNotBlank()) {
            storage.setAnnouncement(record.id, record.announcement)
        }
        _revision.value += 1
    }

    fun getCreatedRooms(): List<CreatedRoomRecord> = storage.getCreatedRooms()
}
