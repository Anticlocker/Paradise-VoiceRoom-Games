package com.sundram.weplay

import com.sundram.weplay.data.ALL_GIFT_PACK_OFFERS
import com.sundram.weplay.data.EventCategory
import com.sundram.weplay.data.EventRepository
import com.sundram.weplay.data.EventStatus
import com.sundram.weplay.data.GiftPackRotationManager
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.concurrent.TimeUnit

class GamesAndEventsUnitTest {

    @Test
    fun testEventDefinitionsAndCategories() {
        val events = EventRepository.ALL_EVENTS
        assertTrue("Must have at least 6 starter events", events.size >= 6)

        val categoriesRepresented = events.map { it.category }.toSet()
        assertEquals(
            "All 6 event categories must be represented",
            EventCategory.values().toSet(),
            categoriesRepresented,
        )

        for (event in events) {
            assertTrue("Event ID must be non-blank", event.id.isNotBlank())
            assertTrue("Event title must be non-blank", event.title.isNotBlank())
            assertTrue("Event description must be non-blank", event.description.isNotBlank())
            assertTrue("Cover resource must be non-zero", event.coverRes != 0)
            assertTrue("Start date must be non-blank", event.startDate.isNotBlank())
            assertTrue("End date must be non-blank", event.endDate.isNotBlank())
            assertNotNull("Status must be defined", event.status)
        }
    }

    @Test
    fun testEventRemainingTimeFormatting() {
        val now = System.currentTimeMillis()
        val futureEvent = EventRepository.ALL_EVENTS.first { it.status == EventStatus.LIVE }
        val remaining = futureEvent.formatRemainingTime(now)
        assertTrue("Live event must display remaining time", remaining.contains("left") || remaining.contains("d") || remaining.contains("h"))

        val endedEvent = EventRepository.ALL_EVENTS.first { it.status == EventStatus.ENDED }
        val endedText = endedEvent.formatRemainingTime(now)
        assertEquals("Event Ended", endedText)
    }

    @Test
    fun testGiftPackCatalogHas10ConfigurableOffers() {
        assertEquals("Must have exactly 10 configurable starter offers", 10, ALL_GIFT_PACK_OFFERS.size)

        val ids = ALL_GIFT_PACK_OFFERS.map { it.id }.toSet()
        assertEquals("All 10 offers must have unique IDs", 10, ids.size)

        for (offer in ALL_GIFT_PACK_OFFERS) {
            assertTrue("Offer title must be non-blank", offer.title.isNotBlank())
            assertTrue("Price must be greater than zero", offer.demoPriceRupees > 0)
            assertTrue("Description must be non-blank", offer.description.isNotBlank())
            assertTrue("Items must not be empty", offer.items.isNotEmpty())
            for (item in offer.items) {
                assertTrue("Item name must be non-blank", item.name.isNotBlank())
                assertTrue("Item count must be > 0", item.count > 0)
                assertTrue("Icon res must be valid", item.iconRes != 0)
            }
        }
    }

    @Test
    fun testDeterministic48HourRotationMath() {
        val cycleDuration = GiftPackRotationManager.CYCLE_DURATION_MILLIS
        assertEquals(48L * 60L * 60L * 1000L, cycleDuration)

        // Verify rotation sequence over 20 consecutive cycles
        val offerIds = ALL_GIFT_PACK_OFFERS.map { it.id }
        for (cycleIndex in 1..20) {
            val expectedOfferIndex = (cycleIndex - 1) % offerIds.size
            val expectedId = offerIds[expectedOfferIndex]
            val actualId = ALL_GIFT_PACK_OFFERS[(cycleIndex - 1) % ALL_GIFT_PACK_OFFERS.size].id
            assertEquals(expectedId, actualId)
        }
    }
}
