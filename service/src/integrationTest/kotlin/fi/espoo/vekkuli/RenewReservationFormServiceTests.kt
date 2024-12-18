package fi.espoo.vekkuli

import fi.espoo.vekkuli.asyncJob.AsyncJob
import fi.espoo.vekkuli.asyncJob.IAsyncJobRunner
import fi.espoo.vekkuli.boatSpace.renewal.BoatSpaceRenewalService
import fi.espoo.vekkuli.boatSpace.renewal.RenewalReservationInput
import fi.espoo.vekkuli.boatSpace.seasonalService.SeasonalService
import fi.espoo.vekkuli.domain.*
import fi.espoo.vekkuli.service.*
import fi.espoo.vekkuli.utils.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito
import org.mockito.kotlin.any
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.math.BigDecimal
import java.time.LocalDate
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
    private lateinit var paymentService: PaymentService

    @Autowired
    private lateinit var citizenService: CitizenService

    @Autowired
    lateinit var reservationService: BoatReservationService

    @Autowired
    lateinit var boatSpaceRenewalService: BoatSpaceRenewalService

    @Autowired
    lateinit var seasonalService: SeasonalService

    @MockBean
    lateinit var asyncJobRunner: IAsyncJobRunner<AsyncJob>

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
        assertEquals(reservation.id, createdRenewal.renewedFromId, "Original reservation ID should match")
        assertEquals(ReservationStatus.Renewal, createdRenewal.status, "Status should be renewal")

        var newReservation = boatSpaceRenewalService.getOrCreateRenewalReservationForEmployee(userId, reservation.id)
        assertEquals(createdRenewal.id, newReservation.id, "Should fetch existing renewal reservation")
        assertEquals(ReservationStatus.Renewal, newReservation.status, "Status should be renewal")
    }

    @Test
    fun `should create a renewal reservation for citizen if not exist or fetch if already created`() {
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
        var createdRenewal = boatSpaceRenewalService.getOrCreateRenewalReservationForCitizen(citizenIdLeo, reservation.id)
        assertNotEquals(reservation.id, createdRenewal.id, "Renewal reservation ID is not the same as original")
        assertEquals(reservation.id, createdRenewal.renewedFromId, "Original reservation ID should match")
        assertEquals(ReservationStatus.Renewal, createdRenewal.status, "Status should be renewal")

        var newReservation = boatSpaceRenewalService.getOrCreateRenewalReservationForCitizen(citizenIdLeo, reservation.id)
        assertEquals(createdRenewal.id, newReservation.id, "Should fetch existing renewal reservation")
        assertEquals(ReservationStatus.Renewal, newReservation.status, "Status should be renewal")
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
        assertEquals(secondReservation.id, secondRenewal.renewedFromId, "Original reservation ID should match")
        assertEquals(ReservationStatus.Renewal, secondRenewal.status, "Status should be renewal")

        var newReservation = boatSpaceRenewalService.getOrCreateRenewalReservationForEmployee(userId, reservation.id)
        assertEquals(firstRenewal.id, newReservation.id, "Should fetch existing renewal reservation")
        assertEquals(ReservationStatus.Renewal, newReservation.status, "Status should be renewal")
    }

    @Test
    fun `should create a renewal reservation for citizen if no renewal reservation exists`() {
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
        val firstRenewal = boatSpaceRenewalService.getOrCreateRenewalReservationForCitizen(citizenIdLeo, reservation.id)
        assertNotNull(firstRenewal.id, "Renewal reservation ID is not the same as original")

        val secondRenewal = boatSpaceRenewalService.getOrCreateRenewalReservationForCitizen(citizenIdOlivia, secondReservation.id)
        assertNotNull(secondRenewal.id, "Renewal reservation ID is not the same as original")
        assertEquals(secondReservation.id, secondRenewal.renewedFromId, "Original reservation ID should match")
        assertEquals(ReservationStatus.Renewal, secondRenewal.status, "Status should be renewal")

        var newReservation = boatSpaceRenewalService.getOrCreateRenewalReservationForCitizen(citizenIdLeo, reservation.id)
        assertEquals(firstRenewal.id, newReservation.id, "Should fetch existing renewal reservation")
        assertEquals(ReservationStatus.Renewal, newReservation.status, "Status should be renewal")
    }

    @Test
    fun `should update renew reservation details based on input`() {
        val reservation =
            testUtils.createReservationInRenewState(
                CreateReservationParams(
                    timeProvider,
                    citizenIdLeo,
                    1,
                    validity = ReservationValidity.Indefinite,
                    startDate = startOfSlipRenewPeriod.minusYears(1).toLocalDate(),
                    endDate = startOfSlipRenewPeriod.plusDays(1).toLocalDate()
                )
            )

        val renewalInput =
            RenewalReservationInput(
                boatId = 2,
                boatType = BoatType.Sailboat,
                width = BigDecimal(3.0),
                length = BigDecimal(4.0),
                depth = BigDecimal(1.0),
                weight = 100,
                boatRegistrationNumber = "12345",
                boatName = "TestBoat",
                otherIdentification = "OtherID",
                extraInformation = "ExtraInfo",
                ownership = OwnershipStatus.Owner,
                email = "citizen@example.com",
                phone = "1234567890",
                agreeToRules = true,
                certifyInformation = true,
                noRegistrationNumber = false,
                originalReservationId = 4
            )

        boatSpaceRenewalService.updateRenewReservation(citizenIdLeo, renewalInput, reservation.id)
        val updatedReservation = reservationService.getReservationWithReserver(reservation.id)

        assertEquals(renewalInput.boatId, updatedReservation?.boatId, "Boat ID should be updated")
        assertEquals(renewalInput.email, updatedReservation?.email, "User email should be updated")
        assertEquals(renewalInput.phone, updatedReservation?.phone, "User phone should be updated")

        // Should be in payment state after sending the renewal request, will redirect to payment page
        assertEquals(ReservationStatus.Payment, updatedReservation?.status, "Status should be set to Payment")
    }

    @Test
    fun `should send invoice, set renewal to invoice state and set old reservation as expired`() {
        val oldReservation =
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
        val renewalReservation = boatSpaceRenewalService.getOrCreateRenewalReservationForCitizen(citizenIdLeo, oldReservation.id)

        boatSpaceRenewalService.activateRenewalAndSendInvoice(
            renewalReservation.id,
            renewalReservation.reserverId,
            renewalReservation.renewedFromId
        )

        val updatedOldReservation = reservationService.getBoatSpaceReservation(oldReservation.id)
        val updatedRenewalReservation = reservationService.getBoatSpaceReservation(renewalReservation.id)
        val invoice = paymentService.getInvoiceForReservation(renewalReservation.id)
        assertNotNull(invoice, "Invoice should exist")

        val payment = paymentService.getPayment(invoice!!.paymentId)
        assertNotNull(payment, "Payment should exist")

        assertNotNull(updatedRenewalReservation, "Renewal reservation should exist")
        assertEquals(ReservationStatus.Invoiced, updatedRenewalReservation?.status, "Renewal reservation should be invoiced")

        assertNotNull(updatedOldReservation, "Old reservation should exist")
        assertEquals(
            timeProvider.getCurrentDate().minusDays(1),
            updatedOldReservation?.endDate,
            "Old reservation should be marked as ended"
        )
    }

    @Test
    fun `should rollback if sending invoice fails`() {
        Mockito.`when`(asyncJobRunner.plan(any())).thenThrow(RuntimeException("Invoice sending failed"))

        val oldReservation =
            testUtils.createReservationInConfirmedState(
                CreateReservationParams(
                    timeProvider,
                    citizenIdLeo,
                    1,
                    startDate = LocalDate.of(2024, 4, 1),
                    endDate = startOfSlipRenewPeriod.plusDays(1).toLocalDate(),
                    validity = ReservationValidity.Indefinite,
                )
            )
        val renewalReservation = boatSpaceRenewalService.getOrCreateRenewalReservationForCitizen(citizenIdLeo, oldReservation.id)
        assertThrows<RuntimeException> {
            boatSpaceRenewalService.activateRenewalAndSendInvoice(
                renewalReservation.id,
                renewalReservation.reserverId,
                renewalReservation.renewedFromId
            )
        }

        assertEquals(ReservationStatus.Renewal, renewalReservation.status, "Renewal reservation should be rolled back")
        assertEquals(ReservationStatus.Confirmed, oldReservation.status, "Old reservation should be rolled back")
        assertEquals(
            startOfSlipRenewPeriod.plusDays(1).toLocalDate(),
            oldReservation.endDate,
            "Old reservation should not be marked as ended"
        )
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
    fun `should prefill renew application with customer information`() {
        val reservation =
            testUtils.createReservationInConfirmedState(
                CreateReservationParams(
                    timeProvider,
                    citizenIdLeo,
                    1,
                    endDate = startOfSlipRenewPeriod.plusDays(1).toLocalDate(),
                    validity = ReservationValidity.Indefinite
                )
            )
        val renewalInput =
            RenewalReservationInput(
                boatId = 0,
                boatType = BoatType.Sailboat,
                width = BigDecimal(3.0),
                length = BigDecimal(4.0),
                depth = BigDecimal(1.0),
                weight = 100,
                boatRegistrationNumber = "12345",
                boatName = "TestBoat",
                otherIdentification = "OtherID",
                extraInformation = "ExtraInfo",
                ownership = OwnershipStatus.Owner,
                email = "citizen@example.com",
                phone = "1234567890",
                agreeToRules = true,
                certifyInformation = true,
                noRegistrationNumber = false,
                originalReservationId = 4
            )

        val citizen = citizenService.getCitizen(citizenIdLeo)
        val renewedReservation = boatSpaceRenewalService.getOrCreateRenewalReservationForCitizen(citizenIdLeo, reservation.id)

        val viewParams = boatSpaceRenewalService.buildBoatSpaceRenewalViewParams(citizenIdLeo, renewedReservation, renewalInput)

        assertEquals(citizenIdLeo, viewParams.citizen?.id, "Citizen ID should match")
        assertEquals(citizen?.email, viewParams.input.email, "Email should have been updated")
    }

    @Test
    fun `should prefill renew application with boat information`() {
        val reservation =
            testUtils.createReservationInConfirmedState(
                CreateReservationParams(
                    timeProvider,
                    citizenIdLeo,
                    1,
                    endDate = startOfSlipRenewPeriod.plusDays(1).toLocalDate(),
                    validity = ReservationValidity.Indefinite
                )
            )
        val renewalInput =
            RenewalReservationInput(
                boatId = 0,
                boatType = BoatType.Sailboat,
                width = BigDecimal(3.0),
                length = BigDecimal(4.0),
                depth = BigDecimal(1.0),
                weight = 100,
                boatRegistrationNumber = "12345",
                boatName = "TestBoat",
                otherIdentification = "OtherID",
                extraInformation = "ExtraInfo",
                ownership = OwnershipStatus.Owner,
                email = "citizen@example.com",
                phone = "1234567890",
                agreeToRules = true,
                certifyInformation = true,
                noRegistrationNumber = false,
                originalReservationId = 4,
                storageType = StorageType.None
            )

        val renewedReservation = boatSpaceRenewalService.getOrCreateRenewalReservationForCitizen(citizenIdLeo, reservation.id)

        val viewParams = boatSpaceRenewalService.buildBoatSpaceRenewalViewParams(citizenIdLeo, renewedReservation, renewalInput)

        assertEquals(renewalInput.boatName, viewParams.input.boatName, "Boat name should have been updated")
        assertEquals(renewalInput.boatId, viewParams.input.boatId, "Boat ID should have been updated")
        assertEquals(renewalInput.boatType, viewParams.input.boatType, "Boat type should have been updated")
        assertEquals(renewalInput.extraInformation, viewParams.input.extraInformation, "Extra information should have been updated")
        assertEquals(renewalInput.weight, viewParams.input.weight, "Weight should have been updated")
        assertEquals(renewalInput.width, viewParams.input.width, "Width should have been updated")
        assertEquals(renewalInput.length, viewParams.input.length, "Length should have been updated")
        assertEquals(renewalInput.depth, viewParams.input.depth, "Depth should have been updated")
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
                    endDate = endDateWithinMonthOfSlipRenewWindow
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

        val reservationExpiringAndSeasonOpenTime = LocalDateTime.of(2025, 1, 8, 12, 0, 0)
        mockTimeProvider(timeProvider, reservationExpiringAndSeasonOpenTime)
        reservation =
            reservationService
                .getBoatSpaceReservationsForCitizen(this.citizenIdLeo)
                .firstOrNull {
                    it.id == madeReservation.id
                }
        assertEquals(reservation?.canRenew, true, "Reservation can be renewed")
    }

    @Test
    fun `should renew winter reservations`() {
        mockTimeProvider(timeProvider, startOfWinterSpaceRenewPeriod)
        val madeReservation =
            testUtils.createReservationInConfirmedState(
                CreateReservationParams(
                    timeProvider,
                    citizenIdLeo,
                    8,
                    1,
                    validity = ReservationValidity.Indefinite,
                    endDate = endDateWithinMonthOfWinterRenewWindow
                )
            )

        val renewalReservation = boatSpaceRenewalService.getOrCreateRenewalReservationForCitizen(citizenIdLeo, madeReservation.id)
        val invalidInput =
            RenewalReservationInput(
                boatId = 2,
                boatType = BoatType.Sailboat,
                width = BigDecimal(3.0),
                length = BigDecimal(4.0),
                depth = BigDecimal(1.0),
                weight = 100,
                boatRegistrationNumber = "12345",
                boatName = "TestBoat",
                otherIdentification = "OtherID",
                extraInformation = "ExtraInfo",
                ownership = OwnershipStatus.Owner,
                email = "test@email.com",
                phone = "1234567890",
                agreeToRules = true,
                certifyInformation = true,
                noRegistrationNumber = false,
                originalReservationId = 4,
            )
        val storageException =
            assertThrows<IllegalArgumentException> {
                boatSpaceRenewalService.updateRenewReservation(
                    citizenIdLeo,
                    invalidInput,
                    renewalReservation.id
                )
            }
        assertEquals("Storage type has to be given.", storageException.message)

        val trailerExpection =
            assertThrows<IllegalArgumentException> {
                boatSpaceRenewalService.updateRenewReservation(
                    citizenIdLeo,
                    invalidInput.copy(storageType = StorageType.Trailer),
                    renewalReservation.id
                )
            }

        assertEquals("Trailer information can not be empty.", trailerExpection.message)
        val validInput =
            invalidInput.copy(
                storageType = StorageType.Trailer,
                trailerRegistrationNumber = "12345",
                trailerWidth = BigDecimal(3.0),
                trailerLength = BigDecimal(4.0)
            )
        boatSpaceRenewalService.updateRenewReservation(citizenIdLeo, validInput, renewalReservation.id)
        val updatedReservation = reservationService.getBoatSpaceReservation(renewalReservation.id)
        assertEquals(validInput.trailerRegistrationNumber, updatedReservation?.trailer?.registrationCode)
        assertEquals(validInput.trailerWidth?.mToCm(), updatedReservation?.trailer?.widthCm)
        assertEquals(validInput.trailerLength?.mToCm(), updatedReservation?.trailer?.lengthCm)
    }
}
