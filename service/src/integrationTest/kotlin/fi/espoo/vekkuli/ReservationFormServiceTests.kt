package fi.espoo.vekkuli

import fi.espoo.vekkuli.boatSpace.reservationForm.ReservationFormService
import fi.espoo.vekkuli.boatSpace.reservationForm.ReservationInput
import fi.espoo.vekkuli.boatSpace.seasonalService.SeasonalService
import fi.espoo.vekkuli.common.Forbidden
import fi.espoo.vekkuli.service.*
import fi.espoo.vekkuli.domain.BoatType
import fi.espoo.vekkuli.domain.OwnershipStatus
import fi.espoo.vekkuli.domain.ReservationValidity
import fi.espoo.vekkuli.service.BoatReservationService
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
import kotlin.test.assertNotNull

@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class ReservationFormServiceTests : IntegrationTestBase() {
    @Autowired
    private lateinit var organizationService: OrganizationService

    @Autowired
    private lateinit var boatReservationService: BoatReservationService

    @MockBean
    lateinit var seasonalService: SeasonalService

    private lateinit var reservationInput: ReservationInput

    @BeforeEach
    override fun resetDatabase() {
        deleteAllReservations(jdbi)
        reservationInput =
            ReservationInput(
                reservationId = 1,
                boatId = 1,
                boatType = BoatType.Sailboat,
                width = BigDecimal("2.5"),
                length = BigDecimal("5.0"),
                depth = BigDecimal("1.0"),
                weight = 1500,
                boatName = "Test Boat",
                extraInformation = "Extra info",
                noRegistrationNumber = false,
                boatRegistrationNumber = "REG123",
                otherIdentification = "ID123",
                ownership = OwnershipStatus.Owner,
                firstName = "John",
                lastName = "Doe",
                ssn = "123456-789",
                address = "Street 123",
                postalCode = "00100",
                postalOffice = "Espoo",
                city = "Espoo",
                municipalityCode = 49,
                citizenId = citizenIdOlivia,
                email = "john.doe@example.com",
                phone = "+358401234567",
                certifyInformation = true,
                agreeToRules = true,
                isOrganization = false
            )
    }

    @Autowired
    lateinit var reservationService: ReservationFormService

    @Test
    fun `should create a reservation for employee if not exist or fetch if already created`() {
        val createdReservationId = reservationService.getOrCreateReservationIdForEmployee(userId, spaceId = 3)
        assertNotNull(createdReservationId, "Should create reservation")

        val secondCreatedReservationId = reservationService.getOrCreateReservationIdForEmployee(userId, spaceId = 3)

        assertEquals(createdReservationId, secondCreatedReservationId, "Should fetch existing renewal reservation")
    }

    @Test
    fun `should create a reservation for citizen if not exist or fetch if already created`() {
        Mockito
            .`when`(seasonalService.canReserveANewSlip(any()))
            .thenReturn(
                ReservationResult.Success(
                    ReservationResultSuccess(
                        LocalDate.now(),
                        LocalDate.now().plusDays(30),
                        ReservationValidity.Indefinite
                    )
                )
            )

        val createdReservationId = reservationService.getOrCreateReservationForCitizen(citizenIdOlivia, spaceId = 3)
        assertNotNull(createdReservationId, "Should create reservation")

        val secondCreatedReservationId = reservationService.getOrCreateReservationForCitizen(citizenIdOlivia, spaceId = 3)

        assertEquals(createdReservationId, secondCreatedReservationId, "Should fetch existing renewal reservation")
    }

    @Test
    fun `should fail if citizen does not have permission to reserve`() {
        Mockito
            .`when`(seasonalService.canReserveANewSlip(citizenIdOlivia))
            .thenReturn(ReservationResult.Failure(ReservationResultErrorCode.NotPossible))

        val exception =
            assertThrows<Forbidden> {
                reservationService.getOrCreateReservationForCitizen(citizenIdOlivia, 10)
            }

        assertEquals("Citizen can not reserve slip", exception.message)
    }

    @Test
    fun `should create or update a reservation for citizen`() {
        val madeReservation = testUtils.createReservationInInfoState(timeProvider, boatReservationService, citizenIdOlivia)

        Mockito
            .`when`(seasonalService.canReserveANewSlip(citizenIdOlivia))
            .thenReturn(
                ReservationResult.Success(
                    ReservationResultSuccess(
                        startDate = LocalDate.now(),
                        endDate = LocalDate.now().plusDays(30),
                        reservationValidity = ReservationValidity.FixedTerm
                    )
                )
            )

        reservationService.createOrUpdateReserverAndReservationForCitizen(madeReservation.id, citizenIdOlivia, reservationInput)
        val reservation = boatReservationService.getReservationWithReserver(madeReservation.id)
        assertNotNull(reservation, "Should create reservation")
    }

    @Test
    fun `should add or update organization`() {
        var organizations = organizationService.getCitizenOrganizations(citizenIdLeo)
        assertEquals(organizations.size, 0, "Should not have any organizations")

        val createdOrgId =
            reservationService.addOrUpdateOrganization(
                citizenIdLeo,
                reservationInput.copy(
                    isOrganization = true,
                    orgName = "Test Organization",
                    orgBusinessId = "1234567-8",
                    orgEmail = "org@example.com",
                )
            )
        organizations = organizationService.getCitizenOrganizations(citizenIdLeo)
        assertNotNull(createdOrgId, "Should create organization")
        assertEquals(organizations.size, 1, "Should have one organization")
        assertEquals(organizations[0].email, "org@example.com", "Should add organization email")

        val orgEmail = "new.email@example.com"
        val secondCreatedOrgId =
            reservationService.addOrUpdateOrganization(
                citizenIdLeo,
                reservationInput.copy(
                    organizationId = createdOrgId,
                    isOrganization = true,
                    orgEmail = orgEmail
                )
            )
        organizations = organizationService.getCitizenOrganizations(citizenIdLeo)
        assertEquals(organizations.size, 1, "Should have one organization")
        assertEquals(secondCreatedOrgId, createdOrgId, "Should fetch existing organization")
        assertEquals(organizations[0].email, orgEmail, "Should update organization email")
    }
}
