package fi.espoo.vekkuli

import fi.espoo.vekkuli.boatSpace.boatSpaceSwitch.SwitchPolicyService
import fi.espoo.vekkuli.boatSpace.citizenBoatSpaceReservation.CanReserveResultStatus
import fi.espoo.vekkuli.boatSpace.citizenBoatSpaceReservation.ReservationService
import fi.espoo.vekkuli.boatSpace.seasonalService.SeasonalService
import fi.espoo.vekkuli.domain.*
import fi.espoo.vekkuli.service.*
import fi.espoo.vekkuli.utils.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito
import org.mockito.kotlin.any
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class ReservationAvailabilityTests : IntegrationTestBase() {
    @Autowired
    private lateinit var paymentService: PaymentService

    @MockBean
    private lateinit var citizenContextProvider: CitizenContextProvider

    @MockBean
    private lateinit var seasonalService: SeasonalService

    @Autowired
    lateinit var reservationService: ReservationService

    @MockBean
    lateinit var organizationService: OrganizationService

    @MockBean
    private lateinit var switchPolicyService: SwitchPolicyService

    @BeforeEach
    override fun resetDatabase() {
        deleteAllReservations(jdbi)
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
                lastName = "Testinen"
            )

        // Let's not tes
        Mockito.`when`(citizenContextProvider.getCurrentCitizen()).thenReturn(loggedInCitizen)
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
                switchPolicyService.citizenCanSwitchToReservation(
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
                switchPolicyService.citizenCanSwitchToReservation(
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
