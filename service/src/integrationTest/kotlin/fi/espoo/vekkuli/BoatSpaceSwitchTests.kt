package fi.espoo.vekkuli

import fi.espoo.vekkuli.boatSpace.boatSpaceSwitch.BoatSpaceSwitchService
import fi.espoo.vekkuli.boatSpace.boatSpaceSwitch.SwitchPolicyService
import fi.espoo.vekkuli.boatSpace.citizenBoatSpaceReservation.CanReserveResultStatus
import fi.espoo.vekkuli.boatSpace.citizenBoatSpaceReservation.FillReservationInformationInput
import fi.espoo.vekkuli.boatSpace.citizenBoatSpaceReservation.ReservationService
import fi.espoo.vekkuli.boatSpace.citizenBoatSpaceReservation.toReservationInformation
import fi.espoo.vekkuli.boatSpace.seasonalService.SeasonalService
import fi.espoo.vekkuli.common.Forbidden
import fi.espoo.vekkuli.config.BoatSpaceConfig
import fi.espoo.vekkuli.config.validateReservationIsActive
import fi.espoo.vekkuli.domain.*
import fi.espoo.vekkuli.repository.PaymentRepository
import fi.espoo.vekkuli.repository.ReserverRepository
import fi.espoo.vekkuli.service.*
import fi.espoo.vekkuli.utils.*
import org.junit.jupiter.api.Assertions
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
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.math.BigDecimal
import java.time.LocalDate
import java.util.UUID
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class BoatSpaceSwitchTests : IntegrationTestBase() {
    @Autowired
    private lateinit var paymentService: PaymentService

    @MockitoBean
    private lateinit var citizenContextProvider: CitizenContextProvider

    @MockitoBean
    private lateinit var seasonalService: SeasonalService

    @Autowired
    private lateinit var boatSpaceSwitchService: BoatSpaceSwitchService

    @Autowired
    lateinit var boatReservationService: BoatReservationService

    @Autowired
    lateinit var reservationService: ReservationService

    @MockitoBean
    lateinit var organizationService: OrganizationService

    @Autowired
    private lateinit var reserverRepository: ReserverRepository

    @Autowired
    private lateinit var paymentRepository: PaymentRepository

    @MockitoBean
    private lateinit var switchPolicyService: SwitchPolicyService

    @BeforeEach
    override fun resetDatabase() {
        createAndSeedDatabase(jdbi)
        deleteAllReservations(jdbi)
    }

    @BeforeEach
    fun mockSeasonalService() {
        // We live in the starting period of reservation switch for espoo citizen
        mockTimeProvider(timeProvider, startOfSlipSwitchPeriodForEspooCitizen)
        Mockito.`when`(seasonalService.isReservationSwitchPeriodActive(any(), any())).thenReturn(true)
    }

    @BeforeEach
    fun mockLoggedInUser() {
        val loggedInCitizen =
            CitizenWithDetails(
                id = citizenIdMikko,
                email = "test@example.com",
                phone = "123456789",
                municipalityCode = 123,
                municipalityName = "Espoo",
                streetAddress = "Test Street 1",
                streetAddressSv = "Testgatan 1",
                postOffice = "Helsinki",
                postOfficeSv = "Helsingfors",
                postalCode = "00100",
                espooRulesApplied = true,
                discountPercentage = 10,
                nationalId = "123456-789A",
                firstName = "Mikko",
                lastName = "Testinen",
                dataProtection = false
            )

        // Let's not tes
        Mockito.`when`(citizenContextProvider.getCurrentCitizen()).thenReturn(loggedInCitizen)
    }

    @BeforeEach
    fun mockAllowSwitching() {
        Mockito
            .`when`(
                switchPolicyService.citizenCanSwitchToReservation(
                    any(),
                    any(),
                    any()
                )
            ).thenReturn(
                ReservationResult.Success(
                    ReservationResultSuccess(
                        startOfSlipSwitchPeriodForEspooCitizen.toLocalDate(),
                        startOfSlipSwitchPeriodForEspooCitizen.toLocalDate(),
                        ReservationValidity.Indefinite
                    )
                )
            )
    }

    @Test
    fun `should create a switch reservation for citizen retaining information from the original reservation`() {
        val originalReservation = createTestReservationForEspooCitizen(startDate = timeProvider.getCurrentDate().minusDays(60))
        val newBoatSpaceId = 2
        val switchedReservationId =
            boatSpaceSwitchService
                .startReservation(
                    newBoatSpaceId,
                    originalReservation.id
                ).id
        val switchedReservation = boatReservationService.getBoatSpaceReservation(switchedReservationId)

        assertNotNull(switchedReservation, "Switch reservation should exist")

        // These fields should be changed
        assertNotEquals(originalReservation.id, switchedReservation.id, "Switch reservation ID is not the same as original")
        assertEquals(originalReservation.id, switchedReservation.originalReservationId, "Original reservation ID should match")
        assertEquals(ReservationStatus.Info, switchedReservation.status, "Status should be info")
        assertEquals(newBoatSpaceId, switchedReservation.boatSpaceId, "Boat space id should be changed")
        assertEquals(CreationType.Switch, switchedReservation.creationType, "Creation type should be switch")
        assertNotEquals(originalReservation.startDate, switchedReservation.startDate, "Start date should not be the same")
        assertEquals(timeProvider.getCurrentDate(), switchedReservation.startDate, "Start date of new reservation should be current date")

        // These fields should be the same
        assertEquals(originalReservation.endDate, switchedReservation.endDate, "End date should be the same")
        assertEquals(originalReservation.reserverId, switchedReservation.reserverId, "Reserver should be the same")
        assertEquals(originalReservation.validity, switchedReservation.validity, "Validity should be the same")
        assertEquals(originalReservation.reserverType, switchedReservation.reserverType, "ReserverType should be the same")
        assertEquals(originalReservation.boat?.id, switchedReservation.boat?.id, "Boat should be the same")
    }

    @Test
    fun `should not be able to start creating a switch reservation if active existing unfinished switch reservation exists`() {
        val originalReservation = createTestReservationForEspooCitizen()
        val firstSwitch =
            boatSpaceSwitchService.startReservation(
                2,
                originalReservation.id
            )
        assertNotNull(firstSwitch, "Should be able create first switch reservation")

        val switchException =
            assertThrows<Forbidden> {
                boatSpaceSwitchService.startReservation(2, originalReservation.id)
            }

        assertEquals("Citizen can not have multiple reservations started", switchException.message)
    }

    @Test
    fun `should be able to create a switch reservation if existing switch reservation has expired`() {
        val originalReservation = createTestReservationForEspooCitizen()
        val firstSwitch =
            boatSpaceSwitchService.startReservation(
                2,
                originalReservation.id
            )
        assertNotNull(firstSwitch, "Should be able create first switch reservation")

        mockTimeProvider(
            timeProvider,
            startOfSlipSwitchPeriodForEspooCitizen.plusSeconds((BoatSpaceConfig.SESSION_TIME_IN_SECONDS + 1).toLong())
        )
        val secondSwitch =
            boatSpaceSwitchService.startReservation(
                2,
                originalReservation.id
            )

        assertNotNull(secondSwitch, "Should be able create second switch reservation after session time has passed")
    }

    @Test
    fun `should update switch reservation details and set status to Confirmed when reservation costs the same`() {
        val originalReservation = createTestReservationForEspooCitizen()
        val switchReservation =
            boatSpaceSwitchService.startReservation(
                2,
                originalReservation.id
            )
        val switchInput = createTestSwitchReservationFormFillInput()

        reservationService.fillReservationInformation(switchReservation.id, switchInput.toReservationInformation())

        val updatedSwitchedReservation = boatReservationService.getBoatSpaceReservation(switchReservation.id)
        assertNotNull(updatedSwitchedReservation, "Updated switch reservation should exist")

        val paymentId = updatedSwitchedReservation.paymentId
        assertNotNull(paymentId, "Payment id should be set")

        val payment = paymentService.getPayment(paymentId)
        assertNotNull(payment, "Payment should exist")

        // Should update fields based on input data
        assertNotEquals(originalReservation.boat?.id, updatedSwitchedReservation.boat?.id, "Boat ID should not be the same as original")
        assertEquals(switchInput.boat.id, updatedSwitchedReservation.boat?.id, "Boat ID should be the same as input")
        assertNotEquals(originalReservation.email, updatedSwitchedReservation.email, "User email should not be the same as original")
        assertEquals(switchInput.citizen.email, updatedSwitchedReservation.email, "User email should be the same as input")
        assertNotEquals(originalReservation.phone, updatedSwitchedReservation.phone, "User phone should not be the same as original")
        assertEquals(switchInput.citizen.phone, updatedSwitchedReservation.phone, "User phone should be the same as input")

        // Should be in Confirmed state when the price is the same
        val revisedPrice =
            boatSpaceSwitchService.getRevisedPrice(
                updatedSwitchedReservation.originalReservationId,
                updatedSwitchedReservation.priceCents
            )
        assertEquals(0, revisedPrice, "The revised price should be 0")
        assertEquals(
            originalReservation.priceCents,
            updatedSwitchedReservation.priceCents,
            "The price of the reservation should be the same"
        )
        assertEquals(ReservationStatus.Confirmed, updatedSwitchedReservation.status, "Status should be set to Confirmed")
    }

    @Test
    fun `should update switch reservation details and set status to Payment when new reservation costs is greater`() {
        val originalReservation = createTestReservationForEspooCitizen()
        val expensiveSpaceId = createExpensiveTestBoatSpace()
        val switchReservation =
            boatSpaceSwitchService.startReservation(
                expensiveSpaceId,
                originalReservation.id
            )
        val switchInput = createTestSwitchReservationFormFillInput()

        reservationService.fillReservationInformation(switchReservation.id, switchInput.toReservationInformation())

        val updatedSwitchedReservation = boatReservationService.getReservationWithDependencies(switchReservation.id)

        assertNotNull(updatedSwitchedReservation, "Updated switch reservation should exist")

        assertEquals(switchInput.boat.id, updatedSwitchedReservation.boatId, "Boat ID should be updated")
        assertEquals(switchInput.citizen.email, updatedSwitchedReservation.email, "User email should be updated")
        assertEquals(switchInput.citizen.phone, updatedSwitchedReservation.phone, "User phone should be updated")

        // Should be in payment state after sending the switch request, will redirect to payment page
        assertEquals(ReservationStatus.Payment, updatedSwitchedReservation.status, "Status should be set to Payment")
    }

    @Test
    fun `should set old reservation as expired after switch reservation is directly confirmed`() {
        val originalReservation = createTestReservationForEspooCitizen()
        val switchReservation =
            boatSpaceSwitchService.startReservation(
                2,
                originalReservation.id
            )
        val switchInput = createTestSwitchReservationFormFillInput()

        reservationService.fillReservationInformation(switchReservation.id, switchInput.toReservationInformation())

        val updatedOriginalReservation = boatReservationService.getBoatSpaceReservation(originalReservation.id)
        val updatedSwitchedReservation = boatReservationService.getBoatSpaceReservation(switchReservation.id)

        assertNotNull(updatedOriginalReservation)
        assertNotNull(updatedSwitchedReservation)

        assertTrue(
            validateReservationIsActive(updatedSwitchedReservation, timeProvider.getCurrentDateTime()),
            "Switch reservation should be active"
        )
        assertTrue(
            !validateReservationIsActive(updatedOriginalReservation, timeProvider.getCurrentDateTime()),
            "Original reservation should not be active"
        )
    }

    @Test
    fun `should set the switched reservation to confirmed state if price is zero and add a payment entry`() {
        val originalReservation = createTestReservationForEspooCitizen()
        val expensiveSpaceId = createExpensiveTestBoatSpace(989898)

        reserverRepository.updateDiscount(citizenIdMikko, 100)

        val switchReservation =
            boatSpaceSwitchService.startReservation(
                expensiveSpaceId,
                originalReservation.id
            )
        val switchInput = createTestSwitchReservationFormFillInput()

        reservationService.fillReservationInformation(switchReservation.id, switchInput.toReservationInformation())

        val updatedSwitchedReservation = boatReservationService.getReservationWithDependencies(switchReservation.id)

        assertNotNull(updatedSwitchedReservation, "Updated switch reservation should exist")

        assertEquals(ReservationStatus.Confirmed, updatedSwitchedReservation.status, "Status should be confirmed")
        val payment = paymentRepository.getPaymentForReservation(updatedSwitchedReservation.id)
        Assertions.assertNotNull(payment, "Should create payment")
        assertEquals(PaymentStatus.Success, payment!!.status)
        assertEquals(0, payment.totalCents)
    }

    @Test
    fun `should return CanReserveOnlyForOrganization if reservation is not reservable but citizen can reserve for organization`() {
        // Can not reserve a new space
        Mockito
            .`when`(
                seasonalService.canReserveANewSpace(eq(citizenIdLeo), any())
            ).thenReturn(
                ReservationResult.Failure(
                    errorCode = ReservationResultErrorCode.MaxReservations
                )
            )

        val organizationId = UUID.randomUUID()

        val organizationList =
            listOf(
                Organization(
                    id = organizationId,
                    businessId = "1234567-8",
                    billingName = "Test",
                    billingStreetAddress = "Test",
                    billingPostalCode = "12345",
                    billingPostOffice = "Test",
                    name = "Test",
                    phone = "123456",
                    email = "test@email.com",
                    streetAddress = "Test",
                    streetAddressSv = "Test",
                    postalCode = "12345",
                    postOffice = "Test",
                    postOfficeSv = "Test",
                    municipalityCode = 123,
                    discountPercentage = 0,
                    espooRulesApplied = false,
                    municipalityName = "Espoo"
                )
            )

        Mockito
            .`when`(
                seasonalService.canReserveANewSpace(eq(organizationId), any())
            ).thenReturn(
                ReservationResult.Success(
                    ReservationResultSuccess(
                        startOfSlipReservationPeriod.toLocalDate(),
                        startOfSlipReservationPeriod.toLocalDate(),
                        ReservationValidity.Indefinite
                    )
                )
            )

        Mockito.`when`(organizationService.getCitizenOrganizations(citizenIdLeo)).thenReturn(
            organizationList
        )

        val result = reservationService.checkReservationAvailability(citizenIdLeo, boatSpaceIdForSlip2)
        assertEquals(CanReserveResultStatus.CanReserveOnlyForOrganization, result.status)
    }

    @Test
    fun `should return CanNotReserve if reservation is not reservable and citizen can not reserve for their organization`() {
        // Can not reserve a new space
        Mockito
            .`when`(
                seasonalService.canReserveANewSpace(eq(citizenIdLeo), any())
            ).thenReturn(
                ReservationResult.Failure(
                    errorCode = ReservationResultErrorCode.MaxReservations
                )
            )

        val organizationId = UUID.randomUUID()
        val secondOrganizationId = UUID.randomUUID()

        val organizationList =
            listOf(
                Organization(
                    id = organizationId,
                    businessId = "1234567-8",
                    billingName = "Test",
                    billingStreetAddress = "Test",
                    billingPostalCode = "12345",
                    billingPostOffice = "Test",
                    name = "Test",
                    phone = "123456",
                    email = "test@email.com",
                    streetAddress = "Test",
                    streetAddressSv = "Test",
                    postalCode = "12345",
                    postOffice = "Test",
                    postOfficeSv = "Test",
                    municipalityCode = 123,
                    discountPercentage = 0,
                    espooRulesApplied = false,
                    municipalityName = "Espoo"
                ),
                Organization(
                    id = secondOrganizationId,
                    businessId = "1234567-8",
                    billingName = "Test",
                    billingStreetAddress = "Test",
                    billingPostalCode = "12345",
                    billingPostOffice = "Test",
                    name = "Test",
                    phone = "123456",
                    email = "test@email.com",
                    streetAddress = "Test",
                    streetAddressSv = "Test",
                    postalCode = "12345",
                    postOffice = "Test",
                    postOfficeSv = "Test",
                    municipalityCode = 123,
                    discountPercentage = 0,
                    espooRulesApplied = false,
                    municipalityName = "Espoo"
                )
            )

        Mockito
            .`when`(
                seasonalService.canReserveANewSpace(eq(organizationId), any())
            ).thenReturn(
                ReservationResult.Failure(
                    errorCode = ReservationResultErrorCode.MaxReservations
                )
            )

        Mockito
            .`when`(
                seasonalService.canReserveANewSpace(eq(secondOrganizationId), any())
            ).thenReturn(
                ReservationResult.Failure(
                    errorCode = ReservationResultErrorCode.MaxReservations
                )
            )

        Mockito.`when`(organizationService.getCitizenOrganizations(citizenIdLeo)).thenReturn(
            organizationList
        )

        val result = reservationService.checkReservationAvailability(citizenIdLeo, boatSpaceIdForSlip2)
        assertEquals(CanReserveResultStatus.CanNotReserve, result.status)
    }

    private fun createTestReservationForEspooCitizen(
        state: ReservationStatus = ReservationStatus.Confirmed,
        spaceId: Int = 1,
        startDate: LocalDate = timeProvider.getCurrentDate(),
    ): BoatSpaceReservationDetails {
        if (ReservationStatus.Confirmed == state) {
            return testUtils.createReservationInConfirmedState(
                CreateReservationParams(
                    timeProvider,
                    citizenIdMikko,
                    spaceId,
                    validity = ReservationValidity.Indefinite,
                    startDate = startDate,
                    endDate = timeProvider.getCurrentDate().plusDays(30),
                )
            )
        }
        throw RuntimeException("Not implemented")
    }

    private fun createTestSwitchReservationFormFillInput() =
        FillReservationInformationInput(
            citizen =
                FillReservationInformationInput.Citizen(
                    phone = (1000000..9999999).random().toString(),
                    email = "${UUID.randomUUID()}@some-email.com",
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
            certifyInformation = true,
            agreeToRules = true,
        )

    private fun createExpensiveTestBoatSpace(id: Int = 999999): Int {
        val currentlyMostExpensiveSpacePriceId = 6
        val spaceId = id

        insertDevBoatSpace(
            DevBoatSpace(
                id = spaceId,
                priceId = currentlyMostExpensiveSpacePriceId,
                widthCm = 300,
                lengthCm = 400,
                type = BoatSpaceType.Slip,
                locationId = 1,
                description = "Test space",
                section = "T",
                placeNumber = 999,
                amenity = BoatSpaceAmenity.None
            )
        )

        return spaceId
    }
}
