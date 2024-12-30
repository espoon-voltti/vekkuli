package fi.espoo.vekkuli

import fi.espoo.vekkuli.asyncJob.AsyncJob
import fi.espoo.vekkuli.asyncJob.IAsyncJobRunner
import fi.espoo.vekkuli.boatSpace.boatSpaceSwitch.BoatSpaceSwitchService
import fi.espoo.vekkuli.boatSpace.renewal.ModifyReservationInput
import fi.espoo.vekkuli.boatSpace.seasonalService.SeasonalService
import fi.espoo.vekkuli.common.Forbidden
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
import java.util.*
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull

@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class BoatSpaceSwitchTests : IntegrationTestBase() {
    @BeforeEach
    override fun resetDatabase() {
        deleteAllReservations(jdbi)
    }

    @Autowired
    private lateinit var reserverService: ReserverService

    @MockBean
    lateinit var asyncJobRunner: IAsyncJobRunner<AsyncJob>

    @Autowired
    private lateinit var paymentService: PaymentService

    @MockBean
    private lateinit var seasonalService: SeasonalService

    @Autowired
    private lateinit var boatSpaceSwitchService: BoatSpaceSwitchService

    @Autowired
    lateinit var boatService: BoatService

    @Autowired
    lateinit var reservationService: BoatReservationService

//    @Test
//    fun `should swap the reservation to a new reservation for the reserver`() {
//        val reservation =
//            testUtils.createReservationInConfirmedState(
//                CreateReservationParams(
//                    timeProvider,
//                    citizenIdOlivia,
//                    1,
//                    validity = ReservationValidity.Indefinite,
//                    startDate = timeProvider.getCurrentDate(),
//                    endDate = timeProvider.getCurrentDate().plusYears(1)
//                )
//            )
//
//        Mockito.`when`(seasonalService.canSwitchReservation(any(), any(), any(), any(), any())).thenReturn(
//            ReservationResult.Success(
//                ReservationResultSuccess(
//                    reservation.startDate,
//                    reservation.endDate,
//                    ReservationValidity.Indefinite,
//                )
//            )
//        )
//
//        val newReservation =
//            testUtils.createReservationInInfoState(
//                citizenIdOlivia
//            )
//        boatSpaceSwitchService.(userId, citizenIdOlivia, reservation.id, newReservation.id)
//        val oldReservation = reservationService.getReservationWithDependencies(reservation.id)
//        assertEquals(timeProvider.getCurrentDate().minusDays(1), oldReservation?.endDate, "Old reservation should be cancelled")
//        assertEquals(
//            ReservationStatus.Confirmed,
//            reservationService.getReservationWithDependencies(newReservation.id)?.status,
//            "Reservation should have been switched to new reservation"
//        )
//    }

    @Test
    fun `should create a switch reservation for employee if not exist or fetch if already created`() {
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
        var createdSwitch = boatSpaceSwitchService.getOrCreateSwitchReservationForEmployee(userId, reservation.id, citizenIdLeo)
        assertNotEquals(reservation.id, createdSwitch.id, "Switch reservation ID is not the same as original")
        assertEquals(reservation.id, createdSwitch.originalReservationId, "Original reservation ID should match")
        assertEquals(CreationType.Switch, createdSwitch.creationType, "Status should be switch")

        var newReservation = boatSpaceSwitchService.getOrCreateSwitchReservationForEmployee(userId, reservation.id, citizenIdLeo)
        assertEquals(createdSwitch.id, newReservation.id, "Should fetch existing switch reservation")
        assertEquals(CreationType.Switch, newReservation.creationType, "Status should be switch")
    }

    @Test
    fun `should create a switch reservation for employee on behalf of organization if not exist or fetch if already`() {
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
        var createdSwitch = boatSpaceSwitchService.getOrCreateSwitchReservationForEmployee(userId, reservation.id, citizenIdOlivia)
        assertNotEquals(reservation.id, createdSwitch.id, "Switch reservation ID is not the same as original")
        assertEquals(reservation.id, createdSwitch.originalReservationId, "Original reservation ID should match")
        assertEquals(CreationType.Switch, createdSwitch.creationType, "Status should be switch")

        var newReservation = boatSpaceSwitchService.getOrCreateSwitchReservationForEmployee(userId, reservation.id, citizenIdOlivia)
        assertEquals(createdSwitch.id, newReservation.id, "Should fetch existing switch reservation")
        assertEquals(CreationType.Switch, newReservation.creationType, "Status should be switch")
    }

    @Test
    fun `should create a switch reservation for citizen if not exist or fetch if already created`() {
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
        var createdSwitch = boatSpaceSwitchService.getOrCreateSwitchReservationForCitizen(citizenIdLeo, reservation.id)
        assertNotEquals(reservation.id, createdSwitch.id, "Switch reservation ID is not the same as original")
        assertEquals(reservation.id, createdSwitch.originalReservationId, "Original reservation ID should match")
        assertEquals(CreationType.Switch, createdSwitch.creationType, "Status should be switch")

        var newReservation = boatSpaceSwitchService.getOrCreateSwitchReservationForCitizen(citizenIdLeo, reservation.id)
        assertEquals(createdSwitch.id, newReservation.id, "Should fetch existing switch reservation")
        assertEquals(CreationType.Switch, newReservation.creationType, "Status should be switch")
    }

    @Test
    fun `should create a switch reservation for employee if no switch reservation exists`() {
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
        val firstSwitch = boatSpaceSwitchService.getOrCreateSwitchReservationForEmployee(userId, reservation.id, citizenIdOlivia)
        assertNotNull(firstSwitch.id, "Switch reservation ID is not the same as original")

        val secondSwitch = boatSpaceSwitchService.getOrCreateSwitchReservationForEmployee(userId, secondReservation.id, citizenIdOlivia)
        assertNotNull(secondSwitch.id, "Switch reservation ID is not the same as original")
        assertEquals(secondReservation.id, secondSwitch.originalReservationId, "Original reservation ID should match")
        assertEquals(CreationType.Switch, secondSwitch.creationType, "Status should be switch")

        var newReservation = boatSpaceSwitchService.getOrCreateSwitchReservationForEmployee(userId, reservation.id, citizenIdOlivia)
        assertEquals(firstSwitch.id, newReservation.id, "Should fetch existing switch reservation")
        assertEquals(CreationType.Switch, newReservation.creationType, "Status should be switch")
    }

    @Test
    fun `should create a switch reservation for citizen if no switch reservation exists`() {
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
        val firstSwitch = boatSpaceSwitchService.getOrCreateSwitchReservationForCitizen(citizenIdLeo, reservation.id)
        assertNotNull(firstSwitch.id, "Switch reservation ID is not the same as original")

        val secondSwitch = boatSpaceSwitchService.getOrCreateSwitchReservationForCitizen(citizenIdOlivia, secondReservation.id)
        assertNotNull(secondSwitch.id, "Switch reservation ID is not the same as original")
        assertEquals(secondReservation.id, secondSwitch.originalReservationId, "Original reservation ID should match")
        assertEquals(CreationType.Switch, secondSwitch.creationType, "Status should be switch")

        var newReservation = boatSpaceSwitchService.getOrCreateSwitchReservationForCitizen(citizenIdLeo, reservation.id)
        assertEquals(firstSwitch.id, newReservation.id, "Should fetch existing switch reservation")
        assertEquals(CreationType.Switch, newReservation.creationType, "Status should be switch")
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

        val switchInput =
            ModifyReservationInput(
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

        boatSpaceSwitchService.updateSwitchReservation(citizenIdLeo, switchInput, reservation.id)
        val updatedReservation = reservationService.getReservationWithReserver(reservation.id)

        assertEquals(switchInput.boatId, updatedReservation?.boatId, "Boat ID should be updated")
        assertEquals(switchInput.email, updatedReservation?.email, "User email should be updated")
        assertEquals(switchInput.phone, updatedReservation?.phone, "User phone should be updated")

        // Should be in payment state after sending the switch request, will redirect to payment page
        assertEquals(ReservationStatus.Payment, updatedReservation?.status, "Status should be set to Payment")
    }

    @Test
    fun `should send invoice, set switch to invoice state and set old reservation as expired`() {
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
        val switchReservation = boatSpaceSwitchService.getOrCreateSwitchReservationForCitizen(citizenIdLeo, oldReservation.id)

        boatSpaceSwitchService.activateSwitchAndSendInvoice(
            switchReservation.id,
            switchReservation.reserverId,
            switchReservation.originalReservationId
        )

        val updatedOldReservation = reservationService.getBoatSpaceReservation(oldReservation.id)
        val updatedSwitchReservation = reservationService.getBoatSpaceReservation(switchReservation.id)
        val invoice = paymentService.getInvoiceForReservation(switchReservation.id)
        assertNotNull(invoice, "Invoice should exist")

        val payment = paymentService.getPayment(invoice!!.paymentId)
        assertNotNull(payment, "Payment should exist")

        assertNotNull(updatedSwitchReservation, "Switch reservation should exist")
        assertEquals(ReservationStatus.Invoiced, updatedSwitchReservation?.status, "Switch reservation should be invoiced")

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
        val switchReservation = boatSpaceSwitchService.getOrCreateSwitchReservationForCitizen(citizenIdLeo, oldReservation.id)
        assertThrows<RuntimeException> {
            boatSpaceSwitchService.activateSwitchAndSendInvoice(
                switchReservation.id,
                switchReservation.reserverId,
                switchReservation.originalReservationId
            )
        }

        assertEquals(CreationType.Switch, switchReservation.creationType, "Switch reservation should be rolled back")
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

        val invoiceModel = boatSpaceSwitchService.getSendInvoiceModel(reservation.id)

        assertNotNull(invoiceModel, "Invoice model should be generated")
        assertEquals(reservation.id, invoiceModel.reservationId, "Reservation ID should match")
    }

//    @Test
//    fun `should prefill renew application with customer information`() {
//        val reservation =
//            testUtils.createReservationInConfirmedState(
//                CreateReservationParams(
//                    timeProvider,
//                    citizenIdLeo,
//                    1,
//                    endDate = startOfSlipRenewPeriod.plusDays(1).toLocalDate(),
//                    validity = ReservationValidity.Indefinite
//                )
//            )
//        val switchInput =
//            ModifyReservationInput(
//                boatId = 0,
//                boatType = BoatType.Sailboat,
//                width = BigDecimal(3.0),
//                length = BigDecimal(4.0),
//                depth = BigDecimal(1.0),
//                weight = 100,
//                boatRegistrationNumber = "12345",
//                boatName = "TestBoat",
//                otherIdentification = "OtherID",
//                extraInformation = "ExtraInfo",
//                ownership = OwnershipStatus.Owner,
//                email = "citizen@example.com",
//                phone = "1234567890",
//                agreeToRules = true,
//                certifyInformation = true,
//                noRegistrationNumber = false,
//                originalReservationId = 4
//            )
//
//        val citizen = reserverService.getCitizen(citizenIdLeo)
//        val renewedReservation = boatSpaceSwitchService.getOrCreateSwitchReservationForCitizen(citizenIdLeo, reservation.id)
//
//        val viewParams = boatSpaceSwitchService.buildBoatSpaceSwitchViewParams(citizenIdLeo, renewedReservation, switchInput)
//
//        assertEquals(citizenIdLeo, viewParams.citizen?.id, "Citizen ID should match")
//        assertEquals(citizen?.email, viewParams.input.email, "Email should have been updated")
//    }

//    @Test
//    fun `should prefill renew application with boat information`() {
//        val reservation =
//            testUtils.createReservationInConfirmedState(
//                CreateReservationParams(
//                    timeProvider,
//                    citizenIdLeo,
//                    1,
//                    endDate = startOfSlipRenewPeriod.plusDays(1).toLocalDate(),
//                    validity = ReservationValidity.Indefinite
//                )
//            )
//        val switchInput =
//            ModifyReservationInput(
//                boatId = 0,
//                boatType = BoatType.Sailboat,
//                width = BigDecimal(3.0),
//                length = BigDecimal(4.0),
//                depth = BigDecimal(1.0),
//                weight = 100,
//                boatRegistrationNumber = "12345",
//                boatName = "TestBoat",
//                otherIdentification = "OtherID",
//                extraInformation = "ExtraInfo",
//                ownership = OwnershipStatus.Owner,
//                email = "citizen@example.com",
//                phone = "1234567890",
//                agreeToRules = true,
//                certifyInformation = true,
//                noRegistrationNumber = false,
//                originalReservationId = 4,
//                storageType = StorageType.None
//            )
//
//        val renewedReservation = boatSpaceSwitchService.getOrCreateSwitchReservationForCitizen(citizenIdLeo, reservation.id)
//
//        val viewParams = boatSpaceSwitchService.buildBoatSpaceSwitchViewParams(citizenIdLeo, renewedReservation, switchInput)
//
//        assertEquals(switchInput.boatName, viewParams.input.boatName, "Boat name should have been updated")
//        assertEquals(switchInput.boatId, viewParams.input.boatId, "Boat ID should have been updated")
//        assertEquals(switchInput.boatType, viewParams.input.boatType, "Boat type should have been updated")
//        assertEquals(switchInput.extraInformation, viewParams.input.extraInformation, "Extra information should have been updated")
//        assertEquals(switchInput.weight, viewParams.input.weight, "Weight should have been updated")
//        assertEquals(switchInput.width, viewParams.input.width, "Width should have been updated")
//        assertEquals(switchInput.length, viewParams.input.length, "Length should have been updated")
//        assertEquals(switchInput.depth, viewParams.input.depth, "Depth should have been updated")
//    }

    @Test
    fun `should be able to switch expiring reservation`() {
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

        assertEquals(reservation?.canRenew, false, "Reservation can not be switched")
        assertNotNull(reservation?.endDate, "Reservation has end date")

        val reservationExpiringAndSeasonOpenTime = LocalDateTime.of(2025, 1, 8, 12, 0, 0)
        mockTimeProvider(timeProvider, reservationExpiringAndSeasonOpenTime)
        reservation =
            reservationService
                .getBoatSpaceReservationsForCitizen(this.citizenIdLeo)
                .firstOrNull {
                    it.id == madeReservation.id
                }
        assertEquals(reservation?.canRenew, true, "Reservation can be switched")
    }

    @Test
    fun `should switch winter reservations`() {
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

        val switchReservation = boatSpaceSwitchService.getOrCreateSwitchReservationForCitizen(citizenIdLeo, madeReservation.id)
        val invalidInput =
            ModifyReservationInput(
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
                boatSpaceSwitchService.updateSwitchReservation(
                    citizenIdLeo,
                    invalidInput,
                    switchReservation.id
                )
            }
        assertEquals("Storage type has to be given.", storageException.message)

        val trailerExpection =
            assertThrows<IllegalArgumentException> {
                boatSpaceSwitchService.updateSwitchReservation(
                    citizenIdLeo,
                    invalidInput.copy(storageType = StorageType.Trailer),
                    switchReservation.id
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
        boatSpaceSwitchService.updateSwitchReservation(citizenIdLeo, validInput, switchReservation.id)
        val updatedReservation = reservationService.getBoatSpaceReservation(switchReservation.id)
        assertEquals(validInput.trailerRegistrationNumber, updatedReservation?.trailer?.registrationCode)
        assertEquals(decimalToInt(validInput.trailerWidth), updatedReservation?.trailer?.widthCm)
        assertEquals(decimalToInt(validInput.trailerLength), updatedReservation?.trailer?.lengthCm)
    }

    @Test
    fun `should not be able to switch reservation if reserver is not found`() {
        val reservation =
            testUtils.createReservationInConfirmedState(
                CreateReservationParams(
                    timeProvider,
                    citizenIdOlivia,
                    1,
                    validity = ReservationValidity.Indefinite,
                    startDate = startOfSlipRenewPeriod.minusYears(1).toLocalDate(),
                    endDate = startOfSlipRenewPeriod.plusDays(1).toLocalDate()
                )
            )

        val newReservation =
            testUtils.createReservationInInfoState(
                citizenIdOlivia
            )

        assertThrows<IllegalArgumentException> {
            boatSpaceSwitchService.switchBoatSpaceReservationAsCitizen(UUID.randomUUID(), reservation.id, newReservation.id)
        }
    }

    @Test
    fun `should not be able to switch reservation if reservation is not found`() {
        val newReservation =
            testUtils.createReservationInInfoState(
                citizenIdOlivia
            )

        assertThrows<IllegalArgumentException> {
            boatSpaceSwitchService.switchBoatSpaceReservationAsCitizen(citizenIdOlivia, 1, newReservation.id)
        }
    }

    @Test
    fun `should not be able to switch reservation as a citizen if season is not active`() {
        val reservation =
            testUtils.createReservationInConfirmedState(
                CreateReservationParams(
                    timeProvider,
                    citizenIdOlivia,
                    1,
                    validity = ReservationValidity.Indefinite,
                    startDate = startOfSlipRenewPeriod.minusYears(1).toLocalDate(),
                    endDate = startOfSlipRenewPeriod.plusDays(1).toLocalDate()
                )
            )

        val newReservation =
            testUtils.createReservationInInfoState(
                citizenIdOlivia
            )

        Mockito.`when`(seasonalService.canSwitchReservation(any(), any(), any(), any(), any())).thenReturn(
            ReservationResult.Failure(
                ReservationResultErrorCode.NotPossible
            )
        )
        assertThrows<Forbidden> {
            boatSpaceSwitchService.switchBoatSpaceReservationAsCitizen(citizenIdOlivia, reservation.id, newReservation.id)
        }
    }
}
