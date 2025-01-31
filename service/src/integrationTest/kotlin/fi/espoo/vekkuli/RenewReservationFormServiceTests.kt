package fi.espoo.vekkuli

import fi.espoo.vekkuli.boatSpace.renewal.BoatSpaceRenewalService
import fi.espoo.vekkuli.boatSpace.renewal.RenewalPolicyService
import fi.espoo.vekkuli.domain.*
import fi.espoo.vekkuli.service.*
import fi.espoo.vekkuli.utils.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.LocalDateTime
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull

@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class RenewReservationFormServiceTests : IntegrationTestBase() {
    @BeforeEach
    override fun resetDatabase() {
        deleteAllReservations(jdbi)
        mockTimeProvider(timeProvider, startOfSlipRenewPeriod)
    }

    @Autowired
    lateinit var reservationService: BoatReservationService

    @Autowired
    lateinit var boatSpaceRenewalService: BoatSpaceRenewalService

    @Autowired
    lateinit var renewalPolicyService: RenewalPolicyService

    @Test
    fun `should create a renewal reservation for employee if not exist or fetch if already created`() {
        val reservation =
            testUtils.createReservationInConfirmedState(
                CreateReservationParams(
                    timeProvider,
                    citizenIdLeo,
                    1,
                    validity = ReservationValidity.Indefinite,
                    startDate = startOfSlipRenewPeriod.minusYears(1).toLocalDate(),
                    endDate = startOfSlipRenewPeriod.plusDays(1).toLocalDate()
                )
            )
        var createdRenewal = boatSpaceRenewalService.getOrCreateRenewalReservationForEmployee(userId, reservation.id)
        assertNotEquals(reservation.id, createdRenewal.id, "Renewal reservation ID is not the same as original")
        assertEquals(reservation.id, createdRenewal.originalReservationId, "Original reservation ID should match")
        assertEquals(ReservationStatus.Info, createdRenewal.status, "Status should be renewal")
        assertEquals(CreationType.Renewal, createdRenewal.creationType, "Status should be renewal")

        var newReservation = boatSpaceRenewalService.getOrCreateRenewalReservationForEmployee(userId, reservation.id)
        assertEquals(createdRenewal.id, newReservation.id, "Should fetch existing renewal reservation")
        assertEquals(ReservationStatus.Info, newReservation.status, "Status should be renewal")
        assertEquals(CreationType.Renewal, newReservation.creationType, "Status should be renewal")
    }

    @Test
    fun `should create a renewal reservation for employee on behalf of organization if not exist or fetch if already`() {
        val reservation =
            testUtils.createReservationInConfirmedState(
                CreateReservationParams(
                    timeProvider,
                    citizenIdOlivia,
                    1,
                    reserverId = organizationId,
                    validity = ReservationValidity.Indefinite,
                    startDate = startOfSlipRenewPeriod.minusYears(1).toLocalDate(),
                    endDate = startOfSlipRenewPeriod.plusDays(1).toLocalDate()
                )
            )
        var createdRenewal = boatSpaceRenewalService.getOrCreateRenewalReservationForEmployee(userId, reservation.id)
        assertNotEquals(reservation.id, createdRenewal.id, "Renewal reservation ID is not the same as original")
        assertEquals(reservation.id, createdRenewal.originalReservationId, "Original reservation ID should match")
        assertEquals(ReservationStatus.Info, createdRenewal.status, "Status should be renewal")
        assertEquals(CreationType.Renewal, createdRenewal.creationType, "Status should be renewal")

        var newReservation = boatSpaceRenewalService.getOrCreateRenewalReservationForEmployee(userId, reservation.id)
        assertEquals(createdRenewal.id, newReservation.id, "Should fetch existing renewal reservation")
        assertEquals(ReservationStatus.Info, newReservation.status, "Status should be renewal")
        assertEquals(CreationType.Renewal, newReservation.creationType, "Status should be renewal")
    }

    @Test
    fun `should create a renewal reservation for employee if no renewal reservation exists`() {
        val reservation =
            testUtils.createReservationInConfirmedState(
                CreateReservationParams(
                    timeProvider,
                    citizenIdLeo,
                    1,
                    validity = ReservationValidity.Indefinite,
                    startDate = startOfSlipRenewPeriod.minusYears(1).toLocalDate(),
                    endDate = startOfSlipRenewPeriod.plusDays(1).toLocalDate()
                )
            )
        val secondReservation =
            testUtils.createReservationInConfirmedState(
                CreateReservationParams(
                    timeProvider,
                    citizenIdOlivia,
                    2,
                    validity = ReservationValidity.Indefinite,
                    startDate = startOfSlipRenewPeriod.minusYears(1).toLocalDate(),
                    endDate = startOfSlipRenewPeriod.plusDays(1).toLocalDate()
                )
            )
        val firstRenewal = boatSpaceRenewalService.getOrCreateRenewalReservationForEmployee(userId, reservation.id)
        assertNotNull(firstRenewal.id, "Renewal reservation ID is not the same as original")

        val secondRenewal = boatSpaceRenewalService.getOrCreateRenewalReservationForEmployee(userId, secondReservation.id)
        assertNotNull(secondRenewal.id, "Renewal reservation ID is not the same as original")
        assertEquals(secondReservation.id, secondRenewal.originalReservationId, "Original reservation ID should match")
        assertEquals(ReservationStatus.Info, secondRenewal.status, "Status should be renewal")
        assertEquals(CreationType.Renewal, secondRenewal.creationType, "Status should be renewal")

        var newReservation = boatSpaceRenewalService.getOrCreateRenewalReservationForEmployee(userId, reservation.id)
        assertEquals(firstRenewal.id, newReservation.id, "Should fetch existing renewal reservation")
        assertEquals(ReservationStatus.Info, newReservation.status, "Status should be renewal")
        assertEquals(CreationType.Renewal, newReservation.creationType, "Status should be renewal")
    }

    @Test
    fun `should generate invoice model for reservation`() {
        val reservation =
            testUtils.createReservationInRenewState(
                CreateReservationParams(
                    timeProvider,
                    citizenIdLeo,
                    1,
                    validity = ReservationValidity.Indefinite,
                )
            )

        val invoiceModel = boatSpaceRenewalService.getSendInvoiceModel(reservation.id)

        assertNotNull(invoiceModel, "Invoice model should be generated")
        assertEquals(reservation.id, invoiceModel.reservationId, "Reservation ID should match")
    }

    @Test
    fun `should be able to renew expiring reservation`() {
        mockTimeProvider(timeProvider, LocalDateTime.of(2024, 4, 30, 12, 0, 0))
        val reserver = this.citizenIdLeo
        val originalReservation =
            testUtils.createReservationInConfirmedState(
                CreateReservationParams(
                    timeProvider,
                    reserver,
                    1,
                    1,
                    validity = ReservationValidity.Indefinite,
                    endDate = endDateWithinMonthOfSlipRenewWindow
                )
            )

        assertNotNull(originalReservation, "Reservation should exist")
        assertEquals(
            false,
            renewalPolicyService.citizenCanRenewReservation(originalReservation.id, reserver).success,
            "Reservation can not be renewed"
        )
        assertNotNull(originalReservation.endDate, "Reservation has end date")

        val reservationExpiringAndSeasonOpenTime = LocalDateTime.of(2025, 1, 8, 12, 0, 0)
        mockTimeProvider(timeProvider, reservationExpiringAndSeasonOpenTime)
        assertEquals(
            true,
            renewalPolicyService.citizenCanRenewReservation(originalReservation.id, reserver).success,
            "Reservation can be renewed"
        )
    }
}
