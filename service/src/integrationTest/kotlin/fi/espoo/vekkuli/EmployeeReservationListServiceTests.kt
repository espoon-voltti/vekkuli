package fi.espoo.vekkuli

import fi.espoo.vekkuli.boatSpace.employeeReservationList.EmployeeReservationListService
import fi.espoo.vekkuli.boatSpace.invoice.BoatSpaceInvoiceService
import fi.espoo.vekkuli.boatSpace.terminateReservation.TerminateReservationService
import fi.espoo.vekkuli.domain.*
import fi.espoo.vekkuli.service.*
import fi.espoo.vekkuli.utils.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.util.*
import kotlin.collections.listOf
import kotlin.test.assertContains

@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class EmployeeReservationListServiceTests : IntegrationTestBase() {


    @Autowired
    private lateinit var terminateReservationService: TerminateReservationService


    @Autowired
    private lateinit var boatReservationService: BoatReservationService

    @Autowired
    lateinit var employeeReservationListService: EmployeeReservationListService


    @Autowired lateinit var invoiceService: BoatSpaceInvoiceService

    @BeforeEach
    override fun resetDatabase() {
        deleteAllReservations(jdbi)
    }

    @Test
    fun `should filter by section`() {
        mockTimeProvider(timeProvider, startOfSlipReservationPeriod)
        val spaceInSectionB = 1
        val spaceInSectionD = 64
        val spaceInSectionE = 85
        testUtils.createReservationInConfirmedState(
            CreateReservationParams(
                timeProvider,
                this.citizenIdLeo,
                spaceInSectionB,
                1
            )
        )
        testUtils.createReservationInConfirmedState(
            CreateReservationParams(
                timeProvider,
                this.citizenIdLeo,
                spaceInSectionD,
                2
            )
        )
        testUtils.createReservationInConfirmedState(
            CreateReservationParams(
                timeProvider,
                this.citizenIdLeo,
                spaceInSectionE,
                3
            )
        )

        val reservationsBySection =
            employeeReservationListService.getBoatSpaceReservations(
                BoatSpaceReservationFilter(
                    sectionFilter = listOf("B", "D")
                )
            )
        val sections = reservationsBySection.items.map { it.section }
        assertEquals(2, reservationsBySection.totalRows, "reservations are filtered correctly")
        assertContains(sections, "B")
        assertContains(sections, "D")
    }

    @Test
    fun `should sort reservations correctly`() {
        mockTimeProvider(timeProvider, startOfSlipReservationPeriod)
        testUtils.createReservationInConfirmedState(
            CreateReservationParams(
                timeProvider,
                this.citizenIdLeo,
                1,
                1
            )
        )
        testUtils.createReservationInConfirmedState(
            CreateReservationParams(
                timeProvider,
                UUID.fromString("62d90eed-4ea3-4446-8023-8dad9c01dd34"),
                177,
                2
            )
        )
        testUtils.createReservationInInvoiceState(timeProvider, boatReservationService, invoiceService, citizenIdOlivia, 2, 3)

        val reservations =
            employeeReservationListService.getBoatSpaceReservations(
                BoatSpaceReservationFilter(
                    sortBy = BoatSpaceReservationFilterColumn.PLACE,
                    ascending = true,
                )
            )

        assertEquals(3, reservations.totalRows, "reservations are filtered correctly")
        assertEquals(listOf(1, 2, 177), reservations.items.map { it.boatSpaceId }, "reservations are sorted by place and amenity")
    }

    @Test
    fun `should return boat space related to reservation`() {
        mockTimeProvider(timeProvider, startOfSlipReservationPeriod)
        val boatSpaceId = 1
        val newReservation =
            boatReservationService.insertBoatSpaceReservation(
                this.citizenIdLeo,
                this.citizenIdLeo,
                boatSpaceId,
                CreationType.New,
                startDate = timeProvider.getCurrentDate(),
                endDate = timeProvider.getCurrentDate(),
                validity = ReservationValidity.FixedTerm,
            )
        val boatSpace = boatReservationService.getBoatSpaceRelatedToReservation(newReservation.id)
        assertEquals(boatSpaceId, boatSpace?.id, "Correct boat space is fetched")
    }

    @Test
    fun `should return expired and cancelled reservations`() {
        mockTimeProvider(timeProvider, startOfSlipReservationPeriod)
        val reservationExpired =
            testUtils.createReservationInConfirmedState(
                CreateReservationParams(
                    timeProvider,
                    this.citizenIdLeo,
                    1,
                    1,
                )
            )

        val reservationTerminated =
            testUtils.createReservationInConfirmedState(
                CreateReservationParams(
                    timeProvider,
                    this.citizenIdLeo,
                    2,
                    2
                )
            )

        val noExpiredReservations = boatReservationService.getExpiredBoatSpaceReservationsForReserver(this.citizenIdLeo)
        assertEquals(0, noExpiredReservations.size)

        boatReservationService.markReservationEnded(reservationExpired.id)
        terminateReservationService.terminateBoatSpaceReservationAsOwner(
            reservationTerminated.id,
            this.citizenIdLeo
        )

        val expiredReservations = boatReservationService.getExpiredBoatSpaceReservationsForReserver(this.citizenIdLeo)
        assertEquals(2, expiredReservations.size)
        assertEquals(
            ReservationStatus.Confirmed,
            expiredReservations.find { it.id == reservationExpired.id }?.status,
            "Reservation is still in Confirmed state"
        )
        assertEquals(
            timeProvider.getCurrentDate().minusDays(1),
            expiredReservations
                .find {
                    it.id == reservationExpired.id
                }?.endDate,
            "End date is set to yesterday"
        )

        assertEquals(
            ReservationStatus.Cancelled,
            expiredReservations
                .find {
                    it.id == reservationTerminated.id
                }?.status,
            "Reservation is marked as Cancelled"
        )
        assertEquals(
            timeProvider.getCurrentDate(),
            expiredReservations
                .find {
                    it.id == reservationTerminated.id
                }?.endDate,
            "End date is set to now"
        )
    }

    @Test
    fun `should return reservations within time`() {
        mockTimeProvider(timeProvider, startOfSlipReservationPeriod)
        val reservation1 =
            testUtils.createReservationInConfirmedState(
                CreateReservationParams(
                    timeProvider,
                    this.citizenIdLeo,
                    1,
                    1,
                    startDate = timeProvider.getCurrentDate().minusDays(10),
                    endDate = timeProvider.getCurrentDate().plusDays(10),
                )
            )
        val reservation2 =
            testUtils.createReservationInConfirmedState(
                CreateReservationParams(
                    timeProvider,
                    this.citizenIdLeo,
                    2,
                    2,
                    startDate = timeProvider.getCurrentDate().minusDays(5),
                    endDate = timeProvider.getCurrentDate().plusDays(5),
                )
            )
        val reservation3 =
            testUtils.createReservationInConfirmedState(
                CreateReservationParams(
                    timeProvider,
                    this.citizenIdLeo,
                    3,
                    3,
                    startDate = timeProvider.getCurrentDate().minusDays(5),
                    endDate = timeProvider.getCurrentDate().plusDays(10),
                )
            )
        var params =
            BoatSpaceReservationFilter(
                reservationValidFrom = timeProvider.getCurrentDate().minusDays(6),
                dateFilter = true,
            )
        var reservations = employeeReservationListService.getBoatSpaceReservations(params).items
        assertEquals(2, reservations.size, "Reservations that are valid within dates is returned")
        assertEquals(reservation2.id, reservations[0].id, "Reservation that is valid from date is returned")
        assertEquals(reservation3.id, reservations[1].id, "Reservation that is valid from date is returned")

        params =
            BoatSpaceReservationFilter(
                reservationValidFrom = timeProvider.getCurrentDate().minusDays(6),
                reservationValidUntil = timeProvider.getCurrentDate().plusDays(6),
                dateFilter = true,
            )
        reservations = employeeReservationListService.getBoatSpaceReservations(params).items
        assertEquals(1, reservations.size, "Reservation that is valid within dates is returned")
        assertEquals(reservation2.id, reservations[0].id, "Reservation that is valid within dates is returned")
    }

}
