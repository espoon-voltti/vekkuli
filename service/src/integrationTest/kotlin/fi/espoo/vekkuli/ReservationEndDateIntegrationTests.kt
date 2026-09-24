package fi.espoo.vekkuli

import fi.espoo.vekkuli.boatSpace.reservationEndDate.EndDateError
import fi.espoo.vekkuli.boatSpace.reservationEndDate.EndDateWarning
import fi.espoo.vekkuli.boatSpace.reservationEndDate.ReservationEndDateService
import fi.espoo.vekkuli.common.Conflict
import fi.espoo.vekkuli.domain.ReservationStatus
import fi.espoo.vekkuli.service.BoatReservationService
import fi.espoo.vekkuli.service.MemoService
import fi.espoo.vekkuli.utils.mockTimeProvider
import fi.espoo.vekkuli.utils.startOfSlipReservationPeriod
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.LocalDate
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlin.test.fail

@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class ReservationEndDateIntegrationTests : IntegrationTestBase() {
    @BeforeEach
    override fun resetDatabase() {
        deleteAllReservations(jdbi)
    }

    @Autowired
    lateinit var endDateService: ReservationEndDateService

    @Autowired
    lateinit var reservationService: BoatReservationService

    @Autowired
    lateinit var memoService: MemoService

    private val editedReservationId = 1001
    private val otherReservationId = 1002

    // The edited reservation runs for the 2024 season on the slip space.
    private val startDate: LocalDate = LocalDate.of(2024, 2, 1)
    private val endDate: LocalDate = LocalDate.of(2024, 12, 31)

    private fun insertEditedReservation(status: ReservationStatus = ReservationStatus.Confirmed) =
        insertDevBoatSpaceReservation(
            DevBoatSpaceReservation(
                id = editedReservationId,
                reserverId = citizenIdOlivia,
                boatSpaceId = boatSpaceIdForSlip,
                startDate = startDate,
                endDate = endDate,
                status = status
            )
        )

    private fun insertOtherReservation(
        status: ReservationStatus,
        start: LocalDate,
        end: LocalDate,
        boatSpaceId: Int = boatSpaceIdForSlip
    ) = insertDevBoatSpaceReservation(
        DevBoatSpaceReservation(
            id = otherReservationId,
            reserverId = citizenIdLeo,
            boatSpaceId = boatSpaceId,
            startDate = start,
            endDate = end,
            status = status
        )
    )

    private fun validate(newEndDate: LocalDate) =
        endDateService.validate(endDateService.getEditableReservation(editedReservationId), newEndDate)

    @Test
    fun `end date can be extended when nobody else has reserved the space`() {
        insertEditedReservation()

        val result = validate(LocalDate.of(2025, 12, 31))

        assertEquals(null, result.error, "Extending into a free period is allowed")
        assertTrue(result.warnings.isEmpty(), "Extending into a free period needs no warning")
    }

    @Test
    fun `extending over the next confirmed reservation is rejected`() {
        insertEditedReservation()
        insertOtherReservation(ReservationStatus.Confirmed, LocalDate.of(2025, 2, 1), LocalDate.of(2025, 12, 31))

        val result = validate(LocalDate.of(2025, 6, 1))

        val error = assertIs<EndDateError.OverlapsAnotherReservation>(result.error)
        assertEquals(otherReservationId, error.conflict.id)
    }

    @Test
    fun `extending to the first day of the next reservation is rejected, the day before is allowed`() {
        insertEditedReservation()
        insertOtherReservation(ReservationStatus.Confirmed, LocalDate.of(2025, 2, 1), LocalDate.of(2025, 12, 31))

        assertIs<EndDateError.OverlapsAnotherReservation>(
            validate(LocalDate.of(2025, 2, 1)).error,
            "A reservation starting on the new end date still occupies the space that day"
        )
        assertEquals(null, validate(LocalDate.of(2025, 1, 31)).error)
    }

    @Test
    fun `an invoiced reservation blocks extending although the database trigger ignores it`() {
        insertEditedReservation()
        insertOtherReservation(ReservationStatus.Invoiced, LocalDate.of(2025, 2, 1), LocalDate.of(2025, 12, 31))

        assertIs<EndDateError.OverlapsAnotherReservation>(validate(LocalDate.of(2025, 6, 1)).error)
    }

    @Test
    fun `a terminated reservation does not block extending`() {
        insertEditedReservation()
        insertOtherReservation(ReservationStatus.Cancelled, LocalDate.of(2025, 2, 1), LocalDate.of(2025, 12, 31))

        assertEquals(null, validate(LocalDate.of(2025, 6, 1)).error)
    }

    @Test
    fun `a reservation on another boat space does not block extending`() {
        insertEditedReservation()
        insertOtherReservation(
            ReservationStatus.Confirmed,
            LocalDate.of(2025, 2, 1),
            LocalDate.of(2025, 12, 31),
            boatSpaceId = boatSpaceIdForSlip2
        )

        assertEquals(null, validate(LocalDate.of(2025, 6, 1)).error)
    }

    @Test
    fun `an end date before the start date is rejected`() {
        insertEditedReservation()

        assertIs<EndDateError.BeforeStartDate>(validate(startDate.minusDays(1)).error)
    }

    @Test
    fun `shortening warns that the place is freed, a past date warns that the reservation ends immediately`() {
        insertEditedReservation()
        mockTimeProvider(timeProvider, startOfSlipReservationPeriod)

        val result = validate(LocalDate.of(2024, 6, 1))

        assertEquals(null, result.error, "Shortening is allowed")
        assertTrue(result.warnings.any { it is EndDateWarning.FreesThePlaceEarlier })
        assertTrue(
            result.warnings.any { it is EndDateWarning.EndsInThePast },
            "The mocked clock is past the new end date, so the reservation expires at once"
        )
    }

    @Test
    fun `a reservation waiting for payment warns without blocking`() {
        insertEditedReservation()
        insertOtherReservation(ReservationStatus.Payment, LocalDate.of(2025, 2, 1), LocalDate.of(2025, 12, 31))

        val result = validate(LocalDate.of(2025, 6, 1))

        assertEquals(null, result.error, "A half-finished reservation does not own the space yet")
        assertTrue(result.warnings.any { it is EndDateWarning.PendingReservationOnSameSpace })
    }

    @Test
    fun `updating the end date stores it and writes a memo`() {
        insertEditedReservation()
        val newEndDate = LocalDate.of(2025, 12, 31)

        endDateService.updateEndDate(editedReservationId, newEndDate, userId)

        assertEquals(newEndDate, reservationService.getBoatSpaceReservation(editedReservationId)?.endDate)
        val memo = memoService.getMemos(citizenIdOlivia).firstOrNull()
        assertNotNull(memo, "The change is recorded as a memo")
        assertTrue(memo.content.contains("31.12.2025"), "The memo names the new end date, was: ${memo.content}")
    }

    @Test
    fun `updating to an occupied date range fails as a conflict instead of a database error`() {
        insertEditedReservation()
        insertOtherReservation(ReservationStatus.Confirmed, LocalDate.of(2025, 2, 1), LocalDate.of(2025, 12, 31))

        try {
            endDateService.updateEndDate(editedReservationId, LocalDate.of(2025, 6, 1), userId)
            fail("Expected the update to be rejected")
        } catch (e: Conflict) {
            assertEquals(endDate, reservationService.getBoatSpaceReservation(editedReservationId)?.endDate)
        }
    }

    @Test
    fun `a terminated reservation can not be edited`() {
        insertEditedReservation(status = ReservationStatus.Cancelled)

        try {
            endDateService.getEditableReservation(editedReservationId)
            fail("Expected editing a terminated reservation to be rejected")
        } catch (e: Conflict) {
            assertTrue(e.message.contains("Cancelled"))
        }
    }
}
