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
import java.time.LocalDateTime
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

/**
 * Regression coverage for date-aware price resolution (V027 `current_price` view).
 *
 * A price class (`name`) can have several validity periods. A boat space points at
 * one specific price row via `boat_space.price_id`, but its effective price must
 * follow whichever period is valid today — without repointing `price_id`.
 */
class PriceValidityIntegrationTests : IntegrationTestBase() {
    @Autowired
    private lateinit var boatService: BoatService

    @Autowired
    private lateinit var boatSpaceReservationRepository: BoatSpaceReservationRepository

    @Autowired
    private lateinit var priceService: PriceService

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
        mockTimeProvider(timeProvider, LocalDateTime.of(2026, 1, 1, 12, 0))

        // Same class 'DATED_TEST' with two adjacent periods: the old one ended
        // yesterday, the new one is valid from today. Periods are anchored to the
        // DB CURRENT_DATE the view compares against.
        val oldPriceId =
            insertPrice("DATED_TEST", priceCents = 10_000, startDate = "'2020-01-01'", endDate = "CURRENT_DATE - 1")
        insertPrice("DATED_TEST", priceCents = 20_000, startDate = "CURRENT_DATE", endDate = "NULL")

        // The boat space still points at the OLD (now-expired) row, exactly as prod does after a cutover.
        val boatSpaceId = 987654
        insertDevBoatSpace(
            DevBoatSpace(
                id = boatSpaceId,
                type = BoatSpaceType.Slip,
                locationId = 1,
                priceId = oldPriceId,
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

        val details = boatSpaceReservationRepository.getBoatSpaceReservationDetails(reservationId)

        assertNotNull(details)
        // Not 10_000 (the row price_id points at) but the period valid today.
        assertEquals(20_000, details.priceCents)
    }

    @Test
    fun `getPriceClasses returns one currently valid row per class, not expired periods`() {
        insertPrice("DATED_TEST", priceCents = 10_000, startDate = "'2020-01-01'", endDate = "CURRENT_DATE - 1")
        insertPrice("DATED_TEST", priceCents = 20_000, startDate = "CURRENT_DATE", endDate = "NULL")

        val dated = priceService.getPriceClasses().filter { it.name == "DATED_TEST" }

        assertEquals(1, dated.size)
        assertEquals(20_000, dated.single().priceCents)
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

    /** Inserts a price row; startDate/endDate are raw SQL expressions (e.g. "CURRENT_DATE", "NULL"). */
    private fun insertPrice(
        name: String,
        priceCents: Int,
        startDate: String,
        endDate: String,
    ): Int =
        jdbi.withHandleUnchecked { handle ->
            handle
                .createQuery(
                    """
                    INSERT INTO price (name, price_cents, vat_cents, net_price_cents, start_date, end_date)
                    VALUES (:name, :priceCents, 0, :priceCents, $startDate, $endDate)
                    RETURNING id
                    """.trimIndent()
                ).bind("name", name)
                .bind("priceCents", priceCents)
                .mapTo<Int>()
                .one()
        }
}
