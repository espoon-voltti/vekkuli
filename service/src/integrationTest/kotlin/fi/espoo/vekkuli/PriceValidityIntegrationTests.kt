package fi.espoo.vekkuli

import fi.espoo.vekkuli.domain.BoatSpaceAmenity
import fi.espoo.vekkuli.domain.BoatSpaceType
import fi.espoo.vekkuli.domain.BoatType
import fi.espoo.vekkuli.domain.OwnershipStatus
import fi.espoo.vekkuli.domain.ReservationStatus
import fi.espoo.vekkuli.repository.BoatSpaceReservationRepository
import fi.espoo.vekkuli.service.BoatService
import fi.espoo.vekkuli.service.PaytrailMock
import fi.espoo.vekkuli.service.PriceService
import fi.espoo.vekkuli.utils.mockTimeProvider
import org.jdbi.v3.core.kotlin.mapTo
import org.jdbi.v3.core.kotlin.withHandleUnchecked
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.LocalDate
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

/**
 * Regression coverage for date-aware price resolution (the V026 `current_price(as_of)`
 * function).
 *
 * A price class (`name`) can have several validity periods. A boat space points at
 * one specific price row via `boat_space.price_id`, but its effective price must
 * follow whichever period is valid on the application's current date — without
 * repointing `price_id`. "Today" is supplied by the app's `TimeProvider`, never the
 * database clock, so these tests drive it entirely with `mockTimeProvider` and fixed
 * calendar dates.
 */
class PriceValidityIntegrationTests : IntegrationTestBase() {
    @Autowired
    private lateinit var boatService: BoatService

    @Autowired
    private lateinit var boatSpaceReservationRepository: BoatSpaceReservationRepository

    @Autowired
    private lateinit var priceService: PriceService

    // Two adjacent, non-overlapping periods for the same class. Fixed calendar dates,
    // independent of the DB clock; the cutover is the 2026-08-01 -> 2026-08-02 boundary.
    private val oldStart = LocalDate.of(2020, 1, 1)
    private val oldEnd = LocalDate.of(2026, 8, 1)
    private val newStart = LocalDate.of(2026, 8, 2)

    @BeforeEach
    override fun resetDatabase() {
        PaytrailMock.reset()
        deleteAllReservations(jdbi)
        deleteAllBoatSpaces(jdbi)
        deleteAllBoats(jdbi)
        // Boat spaces (which reference price) are gone above, so the test price
        // rows can be removed without violating the FK.
        jdbi.withHandleUnchecked { it.execute("DELETE FROM price WHERE name = 'DATED_TEST'") }
    }

    @Test
    fun `boat space pointing at an expired price row resolves to the currently valid price`() {
        // Freeze the app clock inside the NEW period.
        mockTimeProvider(timeProvider, newStart.atTime(12, 0))

        val oldPriceId = insertPrice("DATED_TEST", priceCents = 10_000, startDate = oldStart, endDate = oldEnd)
        insertPrice("DATED_TEST", priceCents = 20_000, startDate = newStart, endDate = null)

        // The boat space still points at the OLD (now-expired) row, exactly as prod does after a cutover.
        val reservationId = insertSpaceAndReservationPointingAt(oldPriceId)

        val details = boatSpaceReservationRepository.getBoatSpaceReservationDetails(reservationId)

        assertNotNull(details)
        // Not 10_000 (the row price_id points at) but the period valid today.
        assertEquals(20_000, details.priceCents)
    }

    @Test
    fun `getPriceClasses returns one currently valid row per class, not expired periods`() {
        mockTimeProvider(timeProvider, newStart.atTime(12, 0))

        insertPrice("DATED_TEST", priceCents = 10_000, startDate = oldStart, endDate = oldEnd)
        insertPrice("DATED_TEST", priceCents = 20_000, startDate = newStart, endDate = null)

        val dated = priceService.getPriceClasses().filter { it.name == "DATED_TEST" }

        assertEquals(1, dated.size)
        assertEquals(20_000, dated.single().priceCents)
    }

    @Test
    fun `resolved price follows the app clock across a price period boundary`() {
        val oldPriceId = insertPrice("DATED_TEST", priceCents = 10_000, startDate = oldStart, endDate = oldEnd)
        insertPrice("DATED_TEST", priceCents = 20_000, startDate = newStart, endDate = null)

        val reservationId = insertSpaceAndReservationPointingAt(oldPriceId)

        // On the last day of the old period the app resolves the old amount...
        mockTimeProvider(timeProvider, oldEnd.atTime(12, 0))
        assertEquals(10_000, boatSpaceReservationRepository.getBoatSpaceReservationDetails(reservationId)?.priceCents)
        assertEquals(10_000, priceService.getPriceClasses().single { it.name == "DATED_TEST" }.priceCents)

        // ...and the very next day, with no database change, it flips to the new amount.
        mockTimeProvider(timeProvider, newStart.atTime(12, 0))
        assertEquals(20_000, boatSpaceReservationRepository.getBoatSpaceReservationDetails(reservationId)?.priceCents)
        assertEquals(20_000, priceService.getPriceClasses().single { it.name == "DATED_TEST" }.priceCents)
    }

    /** Creates a boat space pointing at [priceId] plus a confirmed reservation, and returns the reservation id. */
    private fun insertSpaceAndReservationPointingAt(priceId: Int): Int {
        val boatSpaceId = 987654
        insertDevBoatSpace(
            DevBoatSpace(
                id = boatSpaceId,
                type = BoatSpaceType.Slip,
                locationId = 1,
                priceId = priceId,
                section = "Z",
                placeNumber = 1,
                amenity = BoatSpaceAmenity.None,
                widthCm = 100,
                lengthCm = 200,
                description = "Price validity test space"
            )
        )

        val reservationId = 987655
        insertDevBoatSpaceReservation(
            DevBoatSpaceReservation(
                id = reservationId,
                reserverId = citizenIdMikko,
                boatSpaceId = boatSpaceId,
                boatId = insertBoat(citizenIdMikko),
                startDate = LocalDate.of(2026, 1, 1),
                endDate = LocalDate.of(2026, 12, 31),
                status = ReservationStatus.Confirmed
            )
        )
        return reservationId
    }

    private fun insertBoat(reserverId: UUID): Int =
        boatService
            .insertBoat(
                reserverId,
                "registrationCode",
                "TestBoat",
                150,
                150,
                150,
                150,
                BoatType.Sailboat,
                "",
                "",
                OwnershipStatus.Owner
            ).id

    /** Inserts a price row with an explicit validity period; [endDate] null means "valid to infinity". */
    private fun insertPrice(
        name: String,
        priceCents: Int,
        startDate: LocalDate,
        endDate: LocalDate?,
    ): Int =
        jdbi.withHandleUnchecked { handle ->
            handle
                .createQuery(
                    """
                    INSERT INTO price (name, price_cents, vat_cents, net_price_cents, start_date, end_date)
                    VALUES (:name, :priceCents, 0, :priceCents, :startDate::date, :endDate::date)
                    RETURNING id
                    """.trimIndent()
                ).bind("name", name)
                .bind("priceCents", priceCents)
                .bind("startDate", startDate)
                .bind("endDate", endDate)
                .mapTo<Int>()
                .one()
        }
}
