package com.sundram.weplay.data

import android.content.Context

data class CoinPack(
    val id: String,
    val coins: Long,
    val bonus: Long = 0L,
    val price: Int,
    val tag: String? = null,
    val title: String = "",
) {
    val total: Long get() = coins + bonus
}

data class TopUpRecord(
    val packId: String,
    val coins: Long,
    val price: Int,
    val at: Long,
    val title: String = "",
)

val DemoCoinPacks = listOf(
    CoinPack("coins_starter", 100, 0, 10, "STARTER", "Starter"),
    CoinPack("coins_mini", 300, 30, 29, null, "Mini"),
    CoinPack("coins_popular", 550, 60, 49, "POPULAR", "Popular"),
    CoinPack("coins_value", 1_200, 150, 99, "BEST VALUE", "Value"),
    CoinPack("coins_premium", 2_500, 400, 199, "HOT", "Premium"),
    CoinPack("coins_super", 5_500, 1_000, 399, "SUPER", "Super"),
    CoinPack("coins_royal", 12_000, 2_500, 799, "ROYAL", "Royal"),
    CoinPack("coins_mega", 30_000, 8_000, 1_999, "MEGA", "Mega"),
)

class TopUpStore(context: Context) {

    private val prefs = context.applicationContext
        .getSharedPreferences("weplay_topup", Context.MODE_PRIVATE)

    fun history(userId: String): List<TopUpRecord> =
        prefs.getString(key(userId), null)
            ?.lineSequence()
            ?.mapNotNull(::decode)
            ?.toList()
            .orEmpty()

    fun add(userId: String, record: TopUpRecord) {
        val next = (listOf(record) + history(userId)).take(20)
        prefs.edit()
            .putString(key(userId), next.joinToString("\n", transform = ::encode))
            .apply()
    }

    private fun key(userId: String): String = "history_$userId"

    private fun encode(record: TopUpRecord): String =
        "${record.packId}|${record.coins}|${record.price}|${record.at}"

    private fun decode(line: String): TopUpRecord? {
        val parts = line.split("|")
        if (parts.size != 4) return null
        return TopUpRecord(
            packId = parts[0],
            coins = parts[1].toLongOrNull() ?: return null,
            price = parts[2].toIntOrNull() ?: return null,
            at = parts[3].toLongOrNull() ?: return null,
        )
    }
}
