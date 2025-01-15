package fi.espoo.vekkuli

import fi.espoo.vekkuli.boatSpace.boatSpaceSwitch.BoatSpaceSwitchService
import fi.espoo.vekkuli.boatSpace.citizenBoatSpaceReservation.CanReserveResultStatus
import fi.espoo.vekkuli.boatSpace.citizenBoatSpaceReservation.FillReservationInformationInput
import fi.espoo.vekkuli.boatSpace.citizenBoatSpaceReservation.ReservationService
import fi.espoo.vekkuli.boatSpace.seasonalService.SeasonalService
import fi.espoo.vekkuli.common.BadRequest
import fi.espoo.vekkuli.domain.*
import fi.espoo.vekkuli.service.*
import fi.espoo.vekkuli.utils.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
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
import java.time.LocalDateTime
import kotlin.test.assertNotEquals

@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class BoatSpaceSwitchTests : IntegrationTestBase() {
    @BeforeEach
    override fun resetDatabase() {
        deleteAllReservations(jdbi)

        // pass through the return values
        Mockito
            .`when`(
                seasonalService.addPeriodInformationToReservation(
                    any(),
                    any(),
                )
            ).thenAnswer {
                it.getArgument(1)
            }
    }

    @Autowired
    private lateinit var paymentService: PaymentService

    @MockBean
    private lateinit var seasonalService: SeasonalService

    @Autowired
    private lateinit var boatSpaceSwitchService: BoatSpaceSwitchService

    @Autowired
    lateinit var boatReservationService: BoatReservationService

    @Autowired
    lateinit var reservationService: ReservationService

    @MockBean
    lateinit var organizationService: OrganizationService

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
                    any(),
                    eq(reservation.type),
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
    fun `should update switch reservation details and set status to confirmed when reservation costs the same`() {
        val reservation =
            testUtils.createReservationInConfirmedState(
                CreateReservationParams(
                    timeProvider,
                    citizenIdLeo,
                    1,
                    validity = ReservationValidity.Indefinite,
                    startDate = startOfSlipSwitchPeriodForOthers.minusYears(1).toLocalDate(),
                    endDate = startOfSlipSwitchPeriodForOthers.plusDays(10).toLocalDate()
                )
            )
        mockToAllowSwitchingWithReservationVariables(reservation)
        val switchReservation =
            boatSpaceSwitchService.getOrCreateSwitchReservationForCitizen(citizenIdLeo, reservation.id, 2)

        val switchInput =
            FillReservationInformationInput(
                citizen =
                    FillReservationInformationInput.Citizen(
                        phone = "1234567890",
                        email = "test@gmail.com",
                    ),
                boat =
                    FillReservationInformationInput.Boat(
                        id = 2,
                        type = BoatType.Sailboat,
                        width = BigDecimal(3.0),
                        length = BigDecimal(4.0),
                        depth = BigDecimal(1.0),
                        weight = 100,
                        registrationNumber = "12345",
                        name = "TestBoat",
                        otherIdentification = "OtherID",
                        extraInformation = "ExtraInfo",
                        ownership = OwnershipStatus.Owner,
                    ),
                organization = null,
                certifyInformation = true,
                agreeToRules = true,
                storageType = null,
                trailer = null
            )
        boatSpaceSwitchService.processSwitchInformation(citizenIdLeo, switchInput, switchReservation.id)
        val updatedReservation = boatReservationService.getReservationWithDependencies(switchReservation.id)

        assertEquals(switchInput.boat.id, updatedReservation?.boatId, "Boat ID should be updated")
        assertEquals(switchInput.citizen.email, updatedReservation?.email, "User email should be updated")
        assertEquals(switchInput.citizen.phone, updatedReservation?.phone, "User phone should be updated")

        // Should be in payment state after sending the switch request, will redirect to payment page
        assertEquals(ReservationStatus.Confirmed, updatedReservation?.status, "Status should be set to Confirmed")
    }

    @Test
    fun `should update switch reservation details when price is greater`() {
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
            boatSpaceSwitchService.getOrCreateSwitchReservationForCitizen(citizenIdLeo, reservation.id, 50)

        val switchInput =
            FillReservationInformationInput(
                citizen =
                    FillReservationInformationInput.Citizen(
                        phone = "1234567890",
                        email = "test@gmail.com",
                    ),
                boat =
                    FillReservationInformationInput.Boat(
                        id = 2,
                        type = BoatType.Sailboat,
                        width = BigDecimal(3.0),
                        length = BigDecimal(4.0),
                        depth = BigDecimal(1.0),
                        weight = 100,
                        registrationNumber = "12345",
                        name = "TestBoat",
                        otherIdentification = "OtherID",
                        extraInformation = "ExtraInfo",
                        ownership = OwnershipStatus.Owner,
                    ),
                organization = null,
                certifyInformation = true,
                agreeToRules = true,
                storageType = null,
                trailer = null
            )
        boatSpaceSwitchService.processSwitchInformation(citizenIdLeo, switchInput, switchReservation.id)
        val updatedReservation = boatReservationService.getReservationWithReserver(switchReservation.id)

        assertEquals(switchInput.boat.id, updatedReservation?.boatId, "Boat ID should be updated")
        assertEquals(switchInput.citizen.email, updatedReservation?.email, "User email should be updated")
        assertEquals(switchInput.citizen.phone, updatedReservation?.phone, "User phone should be updated")

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

        // Price difference is 0,because the boat spaces are the same size
        assertEquals(0, boatSpaceSwitchService.getPriceDifference(oldReservation.id), "Price difference should be 0")
        val switchReservation =
            boatSpaceSwitchService.getOrCreateSwitchReservationForCitizen(citizenIdLeo, oldReservation.id, 2)
        val switchInput =
            FillReservationInformationInput(
                citizen =
                    FillReservationInformationInput.Citizen(
                        phone = "1234567890",
                        email = "test@gmail.com",
                    ),
                boat =
                    FillReservationInformationInput.Boat(
                        id = 2,
                        type = BoatType.Sailboat,
                        width = BigDecimal(3.0),
                        length = BigDecimal(4.0),
                        depth = BigDecimal(1.0),
                        weight = 100,
                        registrationNumber = "12345",
                        name = "TestBoat",
                        otherIdentification = "OtherID",
                        extraInformation = "ExtraInfo",
                        ownership = OwnershipStatus.Owner,
                    ),
                organization = null,
                certifyInformation = true,
                agreeToRules = true,
                storageType = null,
                trailer = null
            )
        boatSpaceSwitchService.processSwitchInformation(
            citizenIdLeo,
            switchInput,
            switchReservation.id,
        )

        val updatedOldReservation = boatReservationService.getBoatSpaceReservation(oldReservation.id)
        val updatedSwitchReservation = boatReservationService.getBoatSpaceReservation(switchReservation.id)
        assertNotNull(updatedSwitchReservation?.paymentId)
        val payment = paymentService.getPayment(updatedSwitchReservation?.paymentId!!)
        assertNotNull(payment, "Payment should exist")

        assertEquals(updatedSwitchReservation.paymentDate, timeProvider.getCurrentDate(), "Payment date should match")
        assertNotNull(updatedSwitchReservation, "Switch reservation should exist")
        assertEquals(
            ReservationStatus.Confirmed,
            updatedSwitchReservation.status,
            "Switch reservation should be confirmed"
        )

        assertNotNull(updatedOldReservation, "Old reservation should exist")
        assertEquals(
            timeProvider.getCurrentDate().minusDays(1),
            updatedOldReservation?.endDate,
            "Old reservation should be marked as ended"
        )
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
            boatSpaceSwitchService.getOrCreateSwitchReservationForCitizen(
                citizenIdLeo,
                madeReservation.id,
                boatSpaceIdForWinter2
            )

        mockToAllowSwitchingWithReservationVariables(madeReservation)
        val validInput =
            FillReservationInformationInput(
                citizen =
                    FillReservationInformationInput.Citizen(
                        phone = "1234567890",
                        email = "test@gmail.com",
                    ),
                boat =
                    FillReservationInformationInput.Boat(
                        id = 2,
                        type = BoatType.Sailboat,
                        width = BigDecimal(3.0),
                        length = BigDecimal(4.0),
                        depth = BigDecimal(1.0),
                        weight = 100,
                        registrationNumber = "12345",
                        name = "TestBoat",
                        otherIdentification = "OtherID",
                        extraInformation = "ExtraInfo",
                        ownership = OwnershipStatus.Owner,
                    ),
                organization = null,
                certifyInformation = true,
                agreeToRules = true,
                storageType = StorageType.Trailer,
                trailer =
                    FillReservationInformationInput.Trailer(
                        id = 1,
                        registrationNumber = "12345",
                        width = BigDecimal(3.0),
                        length = BigDecimal(4.0),
                    )
            )

        boatSpaceSwitchService.processSwitchInformation(citizenIdLeo, validInput, switchReservation.id)
        val updatedReservation = boatReservationService.getBoatSpaceReservation(switchReservation.id)
        assertEquals(validInput.trailer?.registrationNumber, updatedReservation?.trailer?.registrationCode)
        assertEquals(decimalToInt(validInput.trailer?.width), updatedReservation?.trailer?.widthCm)
        assertEquals(decimalToInt(validInput.trailer?.length), updatedReservation?.trailer?.lengthCm)
        assertEquals(boatSpaceIdForWinter2, updatedReservation?.boatSpaceId, "Boat space id should be changed")
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
                boatSpaceSwitchService.getOrCreateSwitchReservationForCitizen(
                    citizenIdLeo,
                    madeReservation.id,
                    boatSpaceIdForWinter
                )
            }
        assertEquals("Boat space type does not match", boatSpaceTypeExpection.message)
    }

    /*

     * 1. Can reserve space
     * 2. Can not reserve space
     * 3. Can reserve space and switch
     * 4. Can not reserve space but can switch
     * 5. Can not reserve space and can not switch
     * 6. Can not reserve a space for reserver, but for organization
     * */
    @Test
    fun `should return CanReserve status when space is reservable`() {
        Mockito
            .`when`(
                seasonalService.canReserveANewSpace(any(), any())
            ).thenReturn(
                ReservationResult.Success(
                    ReservationResultSuccess(
                        startOfSlipReservationPeriod.toLocalDate(),
                        startOfSlipReservationPeriod.toLocalDate(),
                        ReservationValidity.Indefinite
                    )
                )
            )
        val result = reservationService.checkReservationAvailability(citizenIdLeo, boatSpaceIdForSlip3)
        assertEquals(CanReserveResultStatus.CanReserve, result.status)
    }

    @Test
    fun `should return CanNotReserve status when no space is reservable`() {
        Mockito.`when`(seasonalService.canReserveANewSpace(any(), any())).thenReturn(
            ReservationResult.Failure(
                errorCode = ReservationResultErrorCode.NotPossible
            )
        )
        val result = reservationService.checkReservationAvailability(citizenIdLeo, boatSpaceIdForSlip3)
        assertEquals(CanReserveResultStatus.CanNotReserve, result.status)
    }

    @Test
    fun `should return CanReserve status and switchable spaces when space is reservable and can switch`() {
        val slipReservation =
            testUtils.createReservationInConfirmedState(
                CreateReservationParams(
                    timeProvider = timeProvider,
                    citizenId = citizenIdLeo,
                    boatSpaceId = boatSpaceIdForSlip,
                    boatId = 2,
                    validity = ReservationValidity.Indefinite,
                    endDate = startOfSlipReservationPeriod.plusDays(10).toLocalDate()
                )
            )

        Mockito
            .`when`(
                seasonalService.canReserveANewSpace(any(), any())
            ).thenReturn(
                ReservationResult.Success(
                    ReservationResultSuccess(
                        startOfSlipReservationPeriod.toLocalDate(),
                        startOfSlipReservationPeriod.toLocalDate(),
                        ReservationValidity.Indefinite
                    )
                )
            )

        Mockito
            .`when`(
                seasonalService.canSwitchReservation(
                    any(),
                    any(),
                    any()
                )
            ).thenReturn(
                ReservationResult.Success(
                    ReservationResultSuccess(
                        startOfSlipReservationPeriod.toLocalDate(),
                        startOfSlipReservationPeriod.toLocalDate(),
                        ReservationValidity.Indefinite
                    )
                )
            )

        val result = reservationService.checkReservationAvailability(citizenIdLeo, boatSpaceIdForSlip2)
        assertEquals(CanReserveResultStatus.CanReserve, result.status)
    }

    @Test
    fun `should return CanNotReserve status and switchable spaces when space is not reservable and can switch`() {
        val slipReservation =
            testUtils.createReservationInConfirmedState(
                CreateReservationParams(
                    timeProvider = timeProvider,
                    citizenId = citizenIdLeo,
                    boatSpaceId = boatSpaceIdForSlip,
                    boatId = 2,
                    validity = ReservationValidity.Indefinite,
                    endDate = startOfSlipReservationPeriod.plusDays(10).toLocalDate()
                )
            )

        // Can not reserve a new space
        Mockito
            .`when`(
                seasonalService.canReserveANewSpace(any(), any())
            ).thenReturn(
                ReservationResult.Failure(
                    errorCode = ReservationResultErrorCode.MaxReservations
                )
            )

        //
        Mockito
            .`when`(
                seasonalService.canSwitchReservation(
                    any(),
                    any(),
                    any()
                )
            ).thenReturn(
                ReservationResult.Success(
                    ReservationResultSuccess(
                        startOfSlipReservationPeriod.toLocalDate(),
                        startOfSlipReservationPeriod.toLocalDate(),
                        ReservationValidity.Indefinite
                    )
                )
            )

        val result = reservationService.checkReservationAvailability(citizenIdLeo, boatSpaceIdForSlip2)
        assertEquals(CanReserveResultStatus.CanNotReserve, result.status)
        assertEquals(listOf(slipReservation.id), result.switchableReservations.map { it.id })
    }

    @Test
    fun `should return CanReserveOnlyForOrganization if reservation is not reservable but citizen belongs to organization`() {
        // Can not reserve a new space
        Mockito
            .`when`(
                seasonalService.canReserveANewSpace(any(), any())
            ).thenReturn(
                ReservationResult.Failure(
                    errorCode = ReservationResultErrorCode.MaxReservations
                )
            )

        Mockito.`when`(organizationService.getCitizenOrganizations(citizenIdLeo)).thenReturn(
            listOf(Mockito.mock(Organization::class.java))
        )

        val result = reservationService.checkReservationAvailability(citizenIdLeo, boatSpaceIdForSlip2)
        assertEquals(CanReserveResultStatus.CanReserveOnlyForOrganization, result.status)
    }
}
