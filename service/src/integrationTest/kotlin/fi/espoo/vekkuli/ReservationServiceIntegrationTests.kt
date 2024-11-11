package fi.espoo.vekkuli

import fi.espoo.vekkuli.boatSpace.terminateReservation.TerminateBoatSpaceReservationService
import fi.espoo.vekkuli.domain.*
import fi.espoo.vekkuli.service.*
import fi.espoo.vekkuli.utils.mockTimeProvider
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*
import kotlin.test.assertContains

@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ReservationServiceIntegrationTests : IntegrationTestBase() {
    @Autowired
    private lateinit var terminateBoatSpaceReservationService: TerminateBoatSpaceReservationService
    val espooCitizenId = citizenIdOlivia
    val helsinkiCitizenId = UUID.fromString("1128bd21-fbbc-4e9a-8658-dc2044a64a58")

    @Autowired
    lateinit var reservationService: BoatReservationService

    @Autowired lateinit var terminateService: TerminateBoatSpaceReservationService

    @Autowired lateinit var invoiceService: BoatSpaceInvoiceService

    @BeforeEach
    override fun resetDatabase() {
        deleteAllReservations(jdbi)
    }

    @Test
    fun `first place should be indefinite for Espoo citizens`() {
        val result = reservationService.canReserveANewSlip(espooCitizenId)
        if (result is ReservationResult.Success) {
            assertEquals(LocalDate.of(2025, 1, 31), result.data.endDate)
            assertEquals(ReservationValidity.Indefinite, result.data.reservationValidity)
        } else {
            throw AssertionError("canReserveANewSlip failed")
        }
    }

    @Test
    fun `second place should be fixed term for Espoo citizens`() {
        val madeReservation = testUtils.createReservationInPaymentState(timeProvider, reservationService, espooCitizenId, 1)
        reservationService.reserveBoatSpace(
            espooCitizenId,
            ReserveBoatSpaceInput(
                reservationId = madeReservation.id,
                boatId = null,
                boatType = BoatType.Sailboat,
                width = 3.5,
                length = 6.5,
                depth = 3.0,
                weight = 180,
                boatRegistrationNumber = "JFK293",
                boatName = "Boat",
                otherIdentification = "1",
                extraInformation = "1",
                ownerShip = OwnershipStatus.FutureOwner,
                phone = "",
                email = ""
            ),
            ReservationStatus.Confirmed,
            ReservationValidity.Indefinite,
            timeProvider.getCurrentDate().minusWeeks(1),
            timeProvider.getCurrentDate().plusWeeks(1),
        )
        val result = reservationService.canReserveANewSlip(espooCitizenId)
        if (result is ReservationResult.Success) {
            assertEquals(LocalDate.of(2024, 12, 31), result.data.endDate)
            assertEquals(ReservationValidity.FixedTerm, result.data.reservationValidity)
        } else {
            throw AssertionError("canReserveANewSlip failed")
        }
    }

    @Test
    fun `third place should fail for Espoo citizens`() {
        val madeReservation1 = testUtils.createReservationInPaymentState(timeProvider, reservationService, espooCitizenId, 1)
        reservationService.reserveBoatSpace(
            espooCitizenId,
            ReserveBoatSpaceInput(
                reservationId = madeReservation1.id,
                boatId = null,
                boatType = BoatType.Sailboat,
                width = 3.5,
                length = 6.5,
                depth = 3.0,
                weight = 180,
                boatRegistrationNumber = "JFK293",
                boatName = "Boat",
                otherIdentification = "1",
                extraInformation = "1",
                ownerShip = OwnershipStatus.FutureOwner,
                phone = "",
                email = ""
            ),
            ReservationStatus.Confirmed,
            ReservationValidity.Indefinite,
            timeProvider.getCurrentDate().minusWeeks(1),
            timeProvider.getCurrentDate().plusWeeks(1),
        )
        val madeReservation2 = testUtils.createReservationInPaymentState(timeProvider, reservationService, espooCitizenId, 1)
        reservationService.reserveBoatSpace(
            espooCitizenId,
            ReserveBoatSpaceInput(
                reservationId = madeReservation2.id,
                boatId = null,
                boatType = BoatType.Sailboat,
                width = 3.5,
                length = 6.5,
                depth = 3.0,
                weight = 180,
                boatRegistrationNumber = "JFK293",
                boatName = "Boat",
                otherIdentification = "1",
                extraInformation = "1",
                ownerShip = OwnershipStatus.FutureOwner,
                phone = "",
                email = ""
            ),
            ReservationStatus.Confirmed,
            ReservationValidity.Indefinite,
            timeProvider.getCurrentDate().minusWeeks(1),
            timeProvider.getCurrentDate().plusWeeks(1),
        )
        val result = reservationService.canReserveANewSlip(espooCitizenId)
        if (result is ReservationResult.Failure) {
            assertEquals(ReservationResultErrorCode.MaxReservations, result.errorCode)
        } else {
            throw AssertionError("canReserveANewSlip succeeded, but it should fail")
        }
    }

    @Test
    fun `first place should be fixed term for Helsinki citizens`() {
        val result = reservationService.canReserveANewSlip(helsinkiCitizenId)
        if (result is ReservationResult.Success) {
            assertEquals(LocalDate.of(2024, 12, 31), result.data.endDate)
            assertEquals(ReservationValidity.FixedTerm, result.data.reservationValidity)
        } else {
            throw AssertionError("canReserveANewSlip failed")
        }
    }

    @Test
    fun `should not allow second place for Helsinki citizens`() {
        val madeReservation = testUtils.createReservationInPaymentState(timeProvider, reservationService, helsinkiCitizenId, 1)
        reservationService.reserveBoatSpace(
            helsinkiCitizenId,
            ReserveBoatSpaceInput(
                reservationId = madeReservation.id,
                boatId = null,
                boatType = BoatType.Sailboat,
                width = 3.5,
                length = 6.5,
                depth = 3.0,
                weight = 180,
                boatRegistrationNumber = "JFK293",
                boatName = "Boat",
                otherIdentification = "1",
                extraInformation = "1",
                ownerShip = OwnershipStatus.FutureOwner,
                phone = "",
                email = ""
            ),
            ReservationStatus.Confirmed,
            ReservationValidity.FixedTerm,
            timeProvider.getCurrentDate().minusWeeks(1),
            timeProvider.getCurrentDate().plusWeeks(1),
        )
        val result = reservationService.canReserveANewSlip(helsinkiCitizenId)
        if (result is ReservationResult.Failure) {
            assertEquals(ReservationResultErrorCode.MaxReservations, result.errorCode)
        } else {
            throw AssertionError("canReserveANewSlip succeeded, but it should fail")
        }
    }

    @Test
    fun `should get correct reservation with citizen`() {
        val madeReservation =
            reservationService.insertBoatSpaceReservation(
                this.citizenIdLeo,
                this.citizenIdLeo,
                1,
                startDate = timeProvider.getCurrentDate(),
                endDate = timeProvider.getCurrentDate(),
            )

        val result = reservationService.getReservationWithReserver(madeReservation.id)
        assertEquals(madeReservation.id, result?.id, "reservation is the same")
        assertEquals(madeReservation.reserverId, result?.reserverId, "citizen is the same")
    }

    @Test
    fun `should update boat in reservation`() {
        val madeReservation =
            reservationService.insertBoatSpaceReservation(
                this.citizenIdLeo,
                this.citizenIdLeo,
                1,
                startDate = timeProvider.getCurrentDate(),
                endDate = timeProvider.getCurrentDate(),
            )
        val boatId = 1
        val updatedReservation =
            reservationService.updateBoatInBoatSpaceReservation(
                madeReservation.id,
                boatId,
                this.citizenIdLeo,
                ReservationStatus.Payment,
                ReservationValidity.Indefinite,
                timeProvider.getCurrentDate(),
                timeProvider.getCurrentDate(),
            )
        val reservation = reservationService.getReservationWithReserver(madeReservation.id)
        assertEquals(madeReservation.id, updatedReservation.id, "reservation is the same")
        assertEquals(madeReservation.reserverId, updatedReservation.reserverId, "citizen is the same")
        assertEquals(boatId, reservation?.boatId, "boat is updated")
    }

    @Test
    fun `should get correct reservation for citizen`() {
        val madeReservation =
            reservationService.insertBoatSpaceReservation(
                this.citizenIdLeo,
                this.citizenIdLeo,
                1,
                startDate = timeProvider.getCurrentDate(),
                endDate = timeProvider.getCurrentDate(),
            )
        val reservation = reservationService.getUnfinishedReservationForCitizen(this.citizenIdLeo)
        assertEquals(madeReservation.id, reservation?.id, "reservation is the same")
    }

    @Test
    fun `should add reservation warnings on reservation with issues`() {
        val madeReservation =
            testUtils.createReservationInPaymentState(
                timeProvider,
                reservationService,
                this.citizenIdLeo,
                1
            )

        reservationService.reserveBoatSpace(
            this.citizenIdLeo,
            ReserveBoatSpaceInput(
                madeReservation.id,
                boatId = null,
                boatType = BoatType.Sailboat,
                width = 3.5,
                length = 6.5,
                depth = 3.0,
                weight = 180,
                boatRegistrationNumber = "JFK293",
                boatName = "Boat",
                otherIdentification = "1",
                extraInformation = "1",
                ownerShip = OwnershipStatus.FutureOwner,
                email = "email@email.com",
                phone = "0403849283",
            ),
            ReservationStatus.Confirmed,
            ReservationValidity.FixedTerm,
            timeProvider.getCurrentDate(),
            timeProvider.getCurrentDate()
        )
        val reservation =
            reservationService
                .getBoatSpaceReservations(
                    BoatSpaceReservationFilter(
                        sortBy = BoatSpaceFilterColumn.PLACE,
                        ascending = true,
                    )
                ).first()

        assertEquals(3, reservation.warnings.size, "Warnings should be present")
        assertEquals(
            listOf("BoatFutureOwner", "BoatLength", "BoatWidth"),
            reservation.warnings.sorted(),
            "Correct warnings should be present"
        )
    }

    @Test
    fun `should get correct reservations with filter`() {
        // Location id 1 and amenity type beam
        testUtils.createReservationInConfirmedState(
            CreateReservationParams(
                timeProvider,
                this.citizenIdLeo,
                1,
                1
            )
        )
        testUtils.createReservationInPaymentState(timeProvider, reservationService, citizenIdOlivia, 2, 3)
        testUtils.createReservationInInfoState(
            timeProvider,
            reservationService,
            this.citizenIdLeo,
            3
        )
        // Location id 2 and amenity type walk beam
        testUtils.createReservationInConfirmedState(
            CreateReservationParams(
                timeProvider,
                UUID.fromString("62d90eed-4ea3-4446-8023-8dad9c01dd34"),
                200,
                2
            )
        )
        // Location id 2 and amenity type beam
        testUtils.createReservationInConfirmedState(
            CreateReservationParams(
                timeProvider,
                UUID.fromString("62d90eed-4ea3-4446-8023-8dad9c01dd34"),
                177,
                2
            )
        )
        // Location id 4 and amenity type walk beam
        testUtils.createReservationInConfirmedState(
            CreateReservationParams(
                timeProvider,
                UUID.fromString("62d90eed-4ea3-4446-8023-8dad9c01dd34"),
                725,
                2
            )
        )

        val reservations =
            reservationService.getBoatSpaceReservations(
                BoatSpaceReservationFilter(
                    harbor = listOf(1, 2),
                    amenity = listOf(BoatSpaceAmenity.Beam, BoatSpaceAmenity.WalkBeam)
                )
            )

        assertEquals(3, reservations.size, "reservations are out filtered correctly")
        assertEquals(listOf(200, 177, 1), reservations.map { it.boatSpaceId }, "correct reservations are returned")
    }

    @Test
    fun `should filter by payment status`() {
        testUtils.createReservationInConfirmedState(CreateReservationParams(timeProvider, this.citizenIdLeo, 1, 1))
        testUtils.createReservationInConfirmedState(
            CreateReservationParams(
                timeProvider,
                this.citizenIdLeo,
                1,
                1
            )
        )
        testUtils.createReservationInInvoiceState(timeProvider, reservationService, invoiceService, citizenIdOlivia, 2, 3)

        val unfilteredReservations =
            reservationService.getBoatSpaceReservations(
                BoatSpaceReservationFilter()
            )

        val unpaidReservations =
            reservationService.getBoatSpaceReservations(
                BoatSpaceReservationFilter(
                    payment = listOf(PaymentFilter.UNPAID)
                )
            )

        val paidReservations =
            reservationService.getBoatSpaceReservations(
                BoatSpaceReservationFilter(
                    payment = listOf(PaymentFilter.PAID)
                )
            )
        assertEquals(2, unfilteredReservations.size, "reservations are filtered correctly")

        assertEquals(1, unpaidReservations.size, "reservations are filtered correctly")
        assertEquals(2, unpaidReservations.first().boatSpaceId, "correct reservation is returned")

        assertEquals(1, paidReservations.size, "reservations are filtered correctly")
        assertEquals(1, paidReservations.first().boatSpaceId, "correct reservation is returned")
    }

    @Test
    fun `should filter by name search`() {
        testUtils.createReservationInConfirmedState(
            CreateReservationParams(
                timeProvider,
                this.citizenIdLeo,
                1,
                1
            )
        )
        testUtils.createReservationInConfirmedState(CreateReservationParams(timeProvider, citizenIdMikko, 3, 2))
        testUtils.createReservationInConfirmedState(CreateReservationParams(timeProvider, citizenIdOlivia, 3, 2))

        // Create a reservation for Olivia Virtanen in payment state
        testUtils.createReservationInPaymentState(timeProvider, reservationService, citizenIdOlivia, 2, 3)

        val reservationsByFirstName =
            reservationService.getBoatSpaceReservations(
                BoatSpaceReservationFilter(
                    nameSearch = "leo"
                )
            )

        assertEquals(1, reservationsByFirstName.size, "reservations are filtered correctly")
        assertEquals("Korhonen Leo", reservationsByFirstName.first().name, "correct reservation is returned")

        val reservationsByLastName =
            reservationService.getBoatSpaceReservations(
                BoatSpaceReservationFilter(
                    nameSearch = "VIRTA"
                )
            )

        assertEquals(2, reservationsByLastName.size, "reservations are filtered correctly")
        val reservationsNames = reservationsByLastName.map { "${it.name}" }
        assertContains(reservationsNames, "Virtanen Mikko")
        assertContains(reservationsNames, "Virtanen Olivia")
    }

    @Test
    fun `should filter reservations that have warnings`() {
        testUtils.createReservationInConfirmedState(
            CreateReservationParams(
                timeProvider,
                this.citizenIdLeo,
                1,
                1
            )
        )
        testUtils.createReservationInPaymentState(timeProvider, reservationService, citizenIdOlivia, 2, 3)

        val madeReservation =
            testUtils.createReservationInPaymentState(
                timeProvider,
                reservationService,
                this.citizenIdLeo,
                3
            )

        reservationService.reserveBoatSpace(
            this.citizenIdLeo,
            ReserveBoatSpaceInput(
                madeReservation.id,
                boatId = null,
                boatType = BoatType.Sailboat,
                width = 3.5,
                length = 6.5,
                depth = 3.0,
                weight = 180,
                boatRegistrationNumber = "JFK293",
                boatName = "Boat",
                otherIdentification = "1",
                extraInformation = "1",
                ownerShip = OwnershipStatus.FutureOwner,
                email = "email@email.com",
                phone = "0403849283"
            ),
            ReservationStatus.Confirmed,
            ReservationValidity.FixedTerm,
            timeProvider.getCurrentDate(),
            timeProvider.getCurrentDate()
        )

        val reservationsWithWarnings =
            reservationService.getBoatSpaceReservations(
                BoatSpaceReservationFilter(
                    warningFilter = true
                )
            )

        assertEquals(1, reservationsWithWarnings.size, "reservations are filtered correctly")
        assertEquals(3, reservationsWithWarnings.first().boatSpaceId, "correct reservation is returned")
    }

    @Test
    fun `should filter by section`() {
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
            reservationService.getBoatSpaceReservations(
                BoatSpaceReservationFilter(
                    sectionFilter = listOf("B", "D")
                )
            )
        val sections = reservationsBySection.map { it.section }
        assertEquals(2, reservationsBySection.size, "reservations are filtered correctly")
        assertContains(sections, "B")
        assertContains(sections, "D")
    }

    @Test
    fun `should sort reservations correctly`() {
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
        testUtils.createReservationInInvoiceState(timeProvider, reservationService, invoiceService, citizenIdOlivia, 2, 3)

        val reservations =
            reservationService.getBoatSpaceReservations(
                BoatSpaceReservationFilter(
                    sortBy = BoatSpaceFilterColumn.PLACE,
                    ascending = true,
                )
            )

        assertEquals(3, reservations.size, "reservations are filtered correctly")
        assertEquals(listOf(1, 2, 177), reservations.map { it.boatSpaceId }, "reservations are sorted by place and amenity")
    }

    @Test
    fun `should return boat space related to reservation`() {
        val boatSpaceId = 1
        val newReservation =
            reservationService.insertBoatSpaceReservation(
                this.citizenIdLeo,
                this.citizenIdLeo,
                boatSpaceId,
                startDate = timeProvider.getCurrentDate(),
                endDate = timeProvider.getCurrentDate(),
            )
        val boatSpace = reservationService.getBoatSpaceRelatedToReservation(newReservation.id)
        assertEquals(boatSpaceId, boatSpace?.id, "Correct boat space is fetched")
    }

    @Test
    fun `should return expired and cancelled reservations`() {
        val reservationExpired =
            testUtils.createReservationInConfirmedState(
                CreateReservationParams(timeProvider,
                this.citizenIdLeo,
                1,
                1,
            ))
        val reservationTerminated =
            testUtils.createReservationInConfirmedState(
                CreateReservationParams( timeProvider,
                this.citizenIdLeo,
                2,
                2)
            )

        val noExpiredReservations = reservationService.getExpiredBoatSpaceReservationsForCitizen(this.citizenIdLeo)
        assertEquals(0, noExpiredReservations.size)

        reservationService.markReservationEnded(reservationExpired.id)
        terminateBoatSpaceReservationService.terminateBoatSpaceReservationAsOwner(
            reservationTerminated.id,
            this.citizenIdLeo
        )

        val expiredReservations = reservationService.getExpiredBoatSpaceReservationsForCitizen(this.citizenIdLeo)
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
    fun `should fetch all harbors`() {
        val harbors = reservationService.getHarbors()
        assertEquals(7, harbors.size, "Correct number of harbors are fetched")
        assertEquals("Satamatie 1, Espoo", harbors[0].address, "Correct number of harbors are fetched")
        assertEquals("Haukilahti", harbors[0].name, "Correct number of harbors are fetched")
    }

    @Test
    fun `should be able to renew expiring reservation`() {
        mockTimeProvider(timeProvider, LocalDateTime.of(2024, 4, 30, 12, 0, 0))
        val madeReservation =
            testUtils.createReservationInConfirmedState(
                CreateReservationParams(
                    timeProvider,
                    this.citizenIdLeo,
                    1,
                    1,
                    validity = ReservationValidity.Indefinite,
                    endDate = timeProvider.getCurrentDate().plusMonths(9)
                )
            )
        var reservation =
            reservationService
                .getBoatSpaceReservationsForCitizen(this.citizenIdLeo)
                .firstOrNull {
                    it.id == madeReservation.id
                }
        assertEquals(reservation?.canRenew, false, "Reservation can not be renewed")
        assertNotNull(reservation?.endDate, "Reservation has end date")
        val expiringDate = (reservation?.endDate?.minusDays(20) ?: LocalDate.now()).atTime(12, 0, 0)
        mockTimeProvider(timeProvider, expiringDate)
        reservation =
            reservationService
                .getBoatSpaceReservationsForCitizen(this.citizenIdLeo)
                .firstOrNull {
                    it.id == madeReservation.id
                }
        assertEquals(reservation?.canRenew, true, "Reservation can be renewed")
    }
}
