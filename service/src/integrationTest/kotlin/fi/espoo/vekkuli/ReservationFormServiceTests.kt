package fi.espoo.vekkuli

import fi.espoo.vekkuli.boatSpace.renewal.BoatSpaceRenewalService
import fi.espoo.vekkuli.boatSpace.reservationForm.ReservationFormService
import fi.espoo.vekkuli.boatSpace.reservationForm.ReservationInput
import fi.espoo.vekkuli.boatSpace.seasonalService.SeasonalService
import fi.espoo.vekkuli.common.Forbidden
import fi.espoo.vekkuli.controllers.UserType
import fi.espoo.vekkuli.domain.*
import fi.espoo.vekkuli.repository.PaymentRepository
import fi.espoo.vekkuli.repository.ReserverRepository
import fi.espoo.vekkuli.repository.TrailerRepository
import fi.espoo.vekkuli.service.*
import fi.espoo.vekkuli.utils.decimalToInt
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
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
import kotlin.test.Test

@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class ReservationFormServiceTests : IntegrationTestBase() {
    @Autowired
    private lateinit var boatSpaceRenewalService: BoatSpaceRenewalService

    @Autowired
    private lateinit var organizationService: OrganizationService

    @Autowired
    private lateinit var boatReservationService: BoatReservationService

    @MockBean
    lateinit var seasonalService: SeasonalService

    private lateinit var reservationInput: ReservationInput

    @Autowired
    private lateinit var reserverRepository: ReserverRepository

    @Autowired
    private lateinit var paymentRepository: PaymentRepository

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
                isOrganization = false,
                trailerRegistrationNumber = null,
                trailerWidth = null,
                trailerLength = null,
                storageType = StorageType.None
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
            .`when`(seasonalService.canReserveANewSpace(any(), eq(BoatSpaceType.Slip)))
            .thenReturn(
                ReservationResult.Success(
                    ReservationResultSuccess(
                        LocalDate.now(),
                        LocalDate.now().plusDays(30),
                        ReservationValidity.Indefinite,
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
            .`when`(seasonalService.canReserveANewSpace(citizenIdOlivia, BoatSpaceType.Winter))
            .thenReturn(ReservationResult.Failure(ReservationResultErrorCode.NotPossible))

        val exception =
            assertThrows<Forbidden> {
                reservationService.getOrCreateReservationForCitizen(citizenIdOlivia, 10)
            }

        assertEquals("Citizen can not reserve slip", exception.message)
    }

    @Test
    fun `should create or update a reservation for citizen`() {
        val madeReservation = testUtils.createReservationInInfoState(citizenIdOlivia)

        Mockito
            .`when`(seasonalService.canReserveANewSpace(citizenIdOlivia, BoatSpaceType.Slip))
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

    @Test
    fun `should add trailer and update storage type and trailer of reservation`() {
        Mockito
            .`when`(seasonalService.canReserveANewSpace(any(), eq(BoatSpaceType.Winter)))
            .thenReturn(
                ReservationResult.Success(
                    ReservationResultSuccess(
                        LocalDate.now(),
                        LocalDate.now().plusDays(30),
                        ReservationValidity.Indefinite
                    )
                )
            )
        // Boat space is winter storage
        val madeReservation = testUtils.createReservationInInfoState(citizenIdOlivia, boatSpaceId = 7)
        val input =
            reservationInput.copy(
                storageType = StorageType.Trailer,
                trailerRegistrationNumber = "ABC123",
                trailerLength = BigDecimal(1.0),
                trailerWidth = BigDecimal(1.0)
            )

        reservationService.createOrUpdateReserverAndReservationForCitizen(madeReservation.id, citizenIdOlivia, input)
        val reservation = boatReservationService.getBoatSpaceReservation(madeReservation.id)

        assertEquals(StorageType.Trailer, reservation?.storageType, "Storage type should be Trailer")
        assertEquals(decimalToInt(input.trailerLength), reservation?.trailer?.lengthCm, "Trailer length should be the same as in the input")
        assertEquals(decimalToInt(input.trailerWidth), reservation?.trailer?.widthCm, "Trailer width should be the same as in the input")
        assertEquals(
            input.trailerRegistrationNumber,
            reservation?.trailer?.registrationCode,
            "Trailer registration number should be the same as in the input"
        )
    }

    @Test
    fun `should be able to update reservation when the creation type is renew`() {
        val madeReservation = testUtils.createReservationInConfirmedState(CreateReservationParams(timeProvider, citizenIdOlivia))

        Mockito
            .`when`(seasonalService.canRenewAReservation(madeReservation.id))
            .thenReturn(
                ReservationResult.Success(
                    ReservationResultSuccess(
                        startDate = LocalDate.now(),
                        endDate = LocalDate.now().plusDays(30),
                        reservationValidity = ReservationValidity.Indefinite
                    )
                )
            )
        val renewedReservation = boatSpaceRenewalService.createRenewalReservation(madeReservation.id, citizenIdOlivia, UserType.CITIZEN)

        assertNotNull(renewedReservation)
        reservationService.createOrUpdateReserverAndReservationForCitizen(renewedReservation!!.id, citizenIdOlivia, reservationInput)
        val reservation = boatReservationService.getReservationWithReserver(renewedReservation.id)
        assertNotNull(reservation, "Should create reservation")
        assertEquals(reservation?.reserverId, citizenIdOlivia, "Should update reserver")
        assertEquals(madeReservation.id, reservation!!.originalReservationId, "Original reservation ID should match")
        assertEquals(ReservationStatus.Payment, reservation.status, "Status should be renewal")
        assertEquals(CreationType.Renewal, reservation.creationType, "Status should be renewal")
    }

    @Test
    fun `should set the new reservation to confirmed state if price is zero and add a payment entry`() {
        val madeReservation = testUtils.createReservationInInfoState(citizenIdOlivia)
        reserverRepository.updateDiscount(citizenIdOlivia, 100)

        Mockito
            .`when`(seasonalService.canReserveANewSpace(citizenIdOlivia, BoatSpaceType.Slip))
            .thenReturn(
                ReservationResult.Success(
                    ReservationResultSuccess(
                        startDate = LocalDate.now(),
                        endDate = LocalDate.now().plusDays(30),
                        reservationValidity = ReservationValidity.FixedTerm
                    )
                )
            )

        reservationService.createOrUpdateReserverAndReservationForCitizen(
            madeReservation.id,
            citizenIdOlivia,
            reservationInput
        )
        val reservation = boatReservationService.getReservationWithDependencies(madeReservation.id)
        assertEquals(reservation?.status, ReservationStatus.Confirmed, "Status should be confirmed")
        val payment = paymentRepository.getPaymentForReservation(madeReservation.id)
        assertNotNull(payment, "Should create payment")
        assertEquals(PaymentStatus.Success, payment!!.status)
        assertEquals(0, payment.totalCents)
    }

    @Test
    fun `should set the renewed reservation to confirmed state if price is zero and add a payment entry`() {
        val madeReservation =
            testUtils.createReservationInConfirmedState(CreateReservationParams(timeProvider, citizenIdOlivia))

        Mockito
            .`when`(seasonalService.canRenewAReservation(madeReservation.id))
            .thenReturn(
                ReservationResult.Success(
                    ReservationResultSuccess(
                        startDate = LocalDate.now(),
                        endDate = LocalDate.now().plusDays(30),
                        reservationValidity = ReservationValidity.Indefinite
                    )
                )
            )
        val renewedReservation =
            boatSpaceRenewalService.createRenewalReservation(madeReservation.id, citizenIdOlivia, UserType.CITIZEN)

        assertNotNull(renewedReservation)
        reserverRepository.updateDiscount(citizenIdOlivia, 100)

        reservationService.createOrUpdateReserverAndReservationForCitizen(
            renewedReservation!!.id,
            citizenIdOlivia,
            reservationInput
        )

        val reservation = boatReservationService.getReservationWithDependencies(renewedReservation.id)
        assertEquals(reservation?.status, ReservationStatus.Confirmed, "Status should be confirmed")
        val payment = paymentRepository.getPaymentForReservation(reservation!!.id)
        assertNotNull(payment, "Should create payment")
        assertEquals(PaymentStatus.Success, payment!!.status)
        assertEquals(0, payment.totalCents)

        assertEquals(madeReservation.id, renewedReservation.originalReservationId, "Original reservation ID should match")
        assertEquals(CreationType.Renewal, renewedReservation.creationType, "Status should be renewal")
    }
}
