package fi.espoo.vekkuli

import fi.espoo.vekkuli.asyncJob.AsyncJob
import fi.espoo.vekkuli.asyncJob.IAsyncJobRunner
import fi.espoo.vekkuli.boatSpace.boatSpaceSwitch.BoatSpaceSwitchService
import fi.espoo.vekkuli.boatSpace.renewal.ModifyReservationInput
import fi.espoo.vekkuli.boatSpace.seasonalService.SeasonalService
import fi.espoo.vekkuli.common.BadRequest
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
import org.mockito.kotlin.eq
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

    private fun mockToAllowSwitchingWithReservationVariables(
        reservation: BoatSpaceReservationDetails,
        timeProviderDate: LocalDateTime = reservation.startDate.atTime(12, 0, 0)
    ) {
        mockCanSwitchReservationToSuccess(reservation)
        mockTimeProvider(timeProvider, timeProviderDate)
    }

    private fun mockCanSwitchReservationToSuccess(
        reservation: BoatSpaceReservationDetails,
        reservationResultSuccess: ReservationResultSuccess =
            ReservationResultSuccess(
                startDate = reservation.startDate,
                endDate = reservation.endDate,
                reservation.validity
            )
    ) {
        Mockito
            .`when`(
                seasonalService.canSwitchReservation(
                    eq(reservation.type),
                    eq(reservation.startDate),
                    eq(reservation.endDate),
                    eq(reservation.validity),
                    any()
                )
            ).thenReturn(
                ReservationResult.Success(
                    reservationResultSuccess
                )
            )
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
                    startDate = timeProvider.getCurrentDate(),
                    endDate = timeProvider.getCurrentDate().plusDays(30),
                )
            )
        mockToAllowSwitchingWithReservationVariables(reservation)
        val newBoatSpaceId = 2
        val switchedReservation =
            boatSpaceSwitchService.getOrCreateSwitchReservationForCitizen(
                citizenIdLeo,
                reservation.id,
                newBoatSpaceId
            )
        assertNotEquals(reservation.id, switchedReservation.id, "Switch reservation ID is not the same as original")
        assertEquals(reservation.id, switchedReservation.originalReservationId, "Original reservation ID should match")
        assertEquals(ReservationStatus.Info, switchedReservation.status, "Status should be info")
        assertEquals(CreationType.Switch, switchedReservation.creationType, "Creation type should be switch")
        assertEquals(newBoatSpaceId, switchedReservation.boatSpaceId, "Boat space id should be changed")
        assertEquals(reservation.endDate, switchedReservation.endDate, "End date should be the same")
        assertEquals(reservation.startDate, switchedReservation.startDate, "Start date should be the same")

        val newReservation =
            boatSpaceSwitchService.getOrCreateSwitchReservationForCitizen(citizenIdLeo, reservation.id, newBoatSpaceId)
        assertEquals(switchedReservation.id, newReservation.id, "Should fetch existing switch typed reservation")
        assertEquals(CreationType.Switch, newReservation.creationType, "Creation type should be switch")
        assertEquals(newBoatSpaceId, newReservation.boatSpaceId, "Boat space id should stay changed")
    }

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
        val newBoatSpaceId = 2
        mockToAllowSwitchingWithReservationVariables(reservation)
        val switchedReservation =
            boatSpaceSwitchService.getOrCreateSwitchReservationForEmployee(
                userId,
                reservation.id,
                citizenIdLeo,
                newBoatSpaceId
            )
        assertNotEquals(reservation.id, switchedReservation.id, "Switch reservation ID is not the same as original")
        assertEquals(reservation.id, switchedReservation.originalReservationId, "Original reservation ID should match")
        assertEquals(ReservationStatus.Info, switchedReservation.status, "Status should be info")
        assertEquals(CreationType.Switch, switchedReservation.creationType, "Creation type should be switch")
        assertEquals(newBoatSpaceId, switchedReservation.boatSpaceId, "Boat space id should be changed")

        val newReservation =
            boatSpaceSwitchService.getOrCreateSwitchReservationForEmployee(
                userId,
                reservation.id,
                citizenIdLeo,
                newBoatSpaceId
            )
        assertEquals(switchedReservation.id, newReservation.id, "Should fetch existing switch typed reservation")
        assertEquals(CreationType.Switch, newReservation.creationType, "Creation type should be switch")
        assertEquals(newBoatSpaceId, newReservation.boatSpaceId, "Boat space id should stay changed")
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
        mockToAllowSwitchingWithReservationVariables(reservation)

        val newBoatSpaceId = 2
        val createdSwitch =
            boatSpaceSwitchService.getOrCreateSwitchReservationForEmployee(
                userId,
                reservation.id,
                citizenIdOlivia,
                newBoatSpaceId
            )
        assertNotEquals(reservation.id, createdSwitch.id, "Switch reservation ID is not the same as original")
        assertEquals(reservation.id, createdSwitch.originalReservationId, "Original reservation ID should match")
        assertEquals(CreationType.Switch, createdSwitch.creationType, "Status should be switch")
        assertEquals(2, createdSwitch.boatSpaceId, "Boat space id should be same as original reservation")
        assertEquals(newBoatSpaceId, createdSwitch.boatSpaceId, "Boat space id should be changed")

        val newReservation =
            boatSpaceSwitchService.getOrCreateSwitchReservationForEmployee(
                userId,
                reservation.id,
                citizenIdOlivia,
                newBoatSpaceId
            )
        assertEquals(createdSwitch.id, newReservation.id, "Should fetch existing switch reservation")
        assertEquals(CreationType.Switch, newReservation.creationType, "Status should be switch")
        assertEquals(newBoatSpaceId, newReservation.boatSpaceId, "Boat space id should stay changed")
    }

    @Test
    fun `should be able to create multiple switches to different reservations`() {
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
        mockToAllowSwitchingWithReservationVariables(reservation)

        val firstSwitch =
            boatSpaceSwitchService.getOrCreateSwitchReservationForEmployee(userId, reservation.id, citizenIdOlivia, 2)
        assertNotNull(firstSwitch.id, "Switch reservation ID is not the same as original")

        val secondReservation =
            testUtils.createReservationInConfirmedState(
                CreateReservationParams(
                    timeProvider,
                    citizenIdOlivia,
                    2,
                    validity = ReservationValidity.Indefinite,
                    startDate = reservation.startDate,
                    endDate = reservation.endDate
                )
            )
        mockToAllowSwitchingWithReservationVariables(secondReservation)
        val secondSwitch =
            boatSpaceSwitchService.getOrCreateSwitchReservationForEmployee(
                userId,
                secondReservation.id,
                citizenIdOlivia,
                2
            )
        assertNotNull(secondSwitch.id, "Switch reservation ID is not the same as original")
        assertEquals(secondReservation.id, secondSwitch.originalReservationId, "Original reservation ID should match")
        assertEquals(CreationType.Switch, secondSwitch.creationType, "Status should be switch")

        val newReservation =
            boatSpaceSwitchService.getOrCreateSwitchReservationForEmployee(userId, reservation.id, citizenIdOlivia, 2)
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
        mockToAllowSwitchingWithReservationVariables(reservation)

        val firstSwitch = boatSpaceSwitchService.getOrCreateSwitchReservationForCitizen(citizenIdLeo, reservation.id, 2)
        assertNotNull(firstSwitch.id, "Switch reservation ID is not the same as original")

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
        mockToAllowSwitchingWithReservationVariables(secondReservation)
        val secondSwitch =
            boatSpaceSwitchService.getOrCreateSwitchReservationForCitizen(citizenIdOlivia, secondReservation.id, 3)
        assertNotNull(secondSwitch.id, "Switch reservation ID is not the same as original")
        assertEquals(secondReservation.id, secondSwitch.originalReservationId, "Original reservation ID should match")
        assertEquals(CreationType.Switch, secondSwitch.creationType, "Status should be switch")

        val newReservation =
            boatSpaceSwitchService.getOrCreateSwitchReservationForCitizen(citizenIdLeo, reservation.id, 3)
        assertEquals(firstSwitch.id, newReservation.id, "Should fetch existing switch reservation")
        assertEquals(CreationType.Switch, newReservation.creationType, "Status should be switch")
    }

    @Test
    fun `should not be able to create a switch reservation if boat space is already reserved`() {
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
        mockToAllowSwitchingWithReservationVariables(reservation)
        Mockito.`when`(seasonalService.isBoatSpaceReserved(2)).thenReturn(true)
        val switchException =
            assertThrows<BadRequest> {
                boatSpaceSwitchService.getOrCreateSwitchReservationForCitizen(citizenIdLeo, reservation.id, 2)
            }
        assertEquals("Boat space is already reserved", switchException.message)
    }

    @Test
    fun `should update switch reservation details based on input`() {
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
        mockToAllowSwitchingWithReservationVariables(reservation)

        val switchReservation =
            boatSpaceSwitchService.getOrCreateSwitchReservationForCitizen(citizenIdLeo, reservation.id, 2)

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
                originalReservationId = reservation.id
            )

        boatSpaceSwitchService.updateSwitchReservation(citizenIdLeo, switchInput, switchReservation.id)
        val updatedReservation = reservationService.getReservationWithReserver(switchReservation.id)

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
        mockToAllowSwitchingWithReservationVariables(oldReservation)

        val switchReservation =
            boatSpaceSwitchService.getOrCreateSwitchReservationForCitizen(citizenIdLeo, oldReservation.id, 2)

        boatSpaceSwitchService.endOriginalReservationAndCreateInvoice(
            switchReservation.id,
            switchReservation.reserverId,
            switchReservation.originalReservationId,
            100,
            200
        )

        val updatedOldReservation = reservationService.getBoatSpaceReservation(oldReservation.id)
        val updatedSwitchReservation = reservationService.getBoatSpaceReservation(switchReservation.id)
        val invoice = paymentService.getInvoiceForReservation(switchReservation.id)
        assertNotNull(invoice, "Invoice should exist")

        val payment = paymentService.getPayment(invoice!!.paymentId)
        assertNotNull(payment, "Payment should exist")
        assertEquals(100, payment.totalCents, "Payment amount should match")

        assertNotNull(updatedSwitchReservation, "Switch reservation should exist")
        assertEquals(
            ReservationStatus.Invoiced,
            updatedSwitchReservation?.status,
            "Switch reservation should be invoiced"
        )

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

        val originalEndDate = startOfSlipRenewPeriod.plusDays(1).toLocalDate()
        val oldReservation =
            testUtils.createReservationInConfirmedState(
                CreateReservationParams(
                    timeProvider,
                    citizenIdLeo,
                    1,
                    startDate = LocalDate.of(2024, 4, 1),
                    endDate = originalEndDate,
                    validity = ReservationValidity.Indefinite,
                )
            )
        mockToAllowSwitchingWithReservationVariables(oldReservation)
        val switchReservation =
            boatSpaceSwitchService.getOrCreateSwitchReservationForCitizen(citizenIdLeo, oldReservation.id, 2)
        assertThrows<RuntimeException> {
            boatSpaceSwitchService.endOriginalReservationAndCreateInvoice(
                switchReservation.id,
                switchReservation.reserverId,
                switchReservation.originalReservationId,
                100,
                101
            )
        }

        assertEquals(CreationType.Switch, switchReservation.creationType, "Switch reservation should be rolled back")
        assertEquals(ReservationStatus.Confirmed, oldReservation.status, "Old reservation should be rolled back")
        assertEquals(
            originalEndDate,
            oldReservation.endDate,
            "Old reservation should not be marked as ended"
        )
    }

    @Test
    fun `should give an error if switching winter reservation without storage types`() {
        val madeReservation =
            testUtils.createReservationInConfirmedState(
                CreateReservationParams(
                    timeProvider = timeProvider,
                    citizenId = citizenIdLeo,
                    boatSpaceId = 9,
                    boatId = 1,
                    validity = ReservationValidity.Indefinite,
                    endDate = endDateWithinMonthOfWinterRenewWindow
                )
            )
        mockToAllowSwitchingWithReservationVariables(madeReservation)

        val switchReservation =
            boatSpaceSwitchService.getOrCreateSwitchReservationForCitizen(citizenIdLeo, madeReservation.id, boatSpaceIdForWinter)
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
    }

    @Test
    fun `should switch winter reservations`() {
        val madeReservation =
            testUtils.createReservationInConfirmedState(
                CreateReservationParams(
                    timeProvider = timeProvider,
                    citizenId = citizenIdLeo,
                    boatSpaceId = boatSpaceIdForWinter,
                    boatId = 1,
                    validity = ReservationValidity.Indefinite,
                    endDate = endDateWithinMonthOfWinterRenewWindow
                )
            )
        mockToAllowSwitchingWithReservationVariables(madeReservation)

        val switchReservation =
            boatSpaceSwitchService.getOrCreateSwitchReservationForCitizen(citizenIdLeo, madeReservation.id, boatSpaceIdForWinter2)

        mockToAllowSwitchingWithReservationVariables(madeReservation)
        val validInput =
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
        assertEquals(boatSpaceIdForWinter2, updatedReservation?.boatSpaceId, "Boat space id should be changed")
    }

    @Test
    fun `should switch winter reservations when reserving for organization`() {
        mockTimeProvider(timeProvider, startOfWinterSpaceRenewPeriod)
        val madeReservation =
            testUtils.createReservationInConfirmedState(
                CreateReservationParams(
                    timeProvider = timeProvider,
                    citizenId = citizenIdOlivia,
                    boatSpaceId = 9,
                    boatId = 1,
                    reserverId = organizationId,
                    validity = ReservationValidity.Indefinite,
                    endDate = endDateWithinMonthOfWinterRenewWindow
                )
            )
        mockToAllowSwitchingWithReservationVariables(madeReservation)

        val switchReservation =
            boatSpaceSwitchService.getOrCreateSwitchReservationForEmployee(
                userId,
                madeReservation.id,
                citizenIdOlivia,
                8
            )
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
                email = "test@email",
                phone = "1234567890",
                agreeToRules = true,
                certifyInformation = true,
                noRegistrationNumber = false,
                originalReservationId = 4
            )
        val storageException =
            assertThrows<IllegalArgumentException> {
                boatSpaceSwitchService.updateSwitchReservation(
                    userId,
                    invalidInput,
                    switchReservation.id
                )
            }
        assertEquals("Storage type has to be given.", storageException.message)
    }

    @Test
    fun `should not be able switch slip reservation with winter reservation`() {
        val madeReservation =
            testUtils.createReservationInConfirmedState(
                CreateReservationParams(
                    timeProvider = timeProvider,
                    citizenId = citizenIdLeo,
                    boatSpaceId = boatSpaceIdForSlip,
                    boatId = 1,
                    validity = ReservationValidity.Indefinite,
                    endDate = endDateWithinMonthOfWinterRenewWindow
                )
            )
        mockToAllowSwitchingWithReservationVariables(madeReservation)
        val boatSpaceTypeExpection =
            assertThrows<BadRequest> {
                boatSpaceSwitchService.getOrCreateSwitchReservationForCitizen(citizenIdLeo, madeReservation.id, boatSpaceIdForWinter)
            }
        assertEquals("Boat space type does not match", boatSpaceTypeExpection.message)
    }

//    @Test
//    fun `should prefill switch application with customer information`() {
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
//        val switchedReservation = boatSpaceSwitchService.getOrCreateSwitchReservationForCitizen(citizenIdLeo, reservation.id)
//
//        val viewParams = boatSpaceSwitchService.buildBoatSpaceSwitchViewParams(citizenIdLeo, switchedReservation, switchInput)
//
//        assertEquals(citizenIdLeo, viewParams.citizen?.id, "Citizen ID should match")
//        assertEquals(citizen?.email, viewParams.input.email, "Email should have been updated")
//    }

//    @Test
//    fun `should prefill switch application with boat information`() {
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
//        val switchedReservation = boatSpaceSwitchService.getOrCreateSwitchReservationForCitizen(citizenIdLeo, reservation.id)
//
//        val viewParams = boatSpaceSwitchService.buildBoatSpaceSwitchViewParams(citizenIdLeo, switchedReservation, switchInput)
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
}
