package fi.espoo.vekkuli

import fi.espoo.vekkuli.boatSpace.citizenBoatSpaceReservation.ReservationInformation
import fi.espoo.vekkuli.boatSpace.citizenBoatSpaceReservation.ReservationService
import fi.espoo.vekkuli.boatSpace.seasonalService.SeasonalService
import fi.espoo.vekkuli.common.Conflict
import fi.espoo.vekkuli.common.Forbidden
import fi.espoo.vekkuli.common.Unauthorized
import fi.espoo.vekkuli.domain.*
import fi.espoo.vekkuli.repository.ReserverRepository
import fi.espoo.vekkuli.service.*
import jakarta.validation.ConstraintViolationException
import kotlinx.coroutines.runBlocking
import org.jdbi.v3.core.kotlin.mapTo
import org.jdbi.v3.core.kotlin.withHandleUnchecked
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.math.BigDecimal
import java.time.LocalDate
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class ReservationServiceTests : IntegrationTestBase() {
    @Autowired
    private lateinit var boatReservationService: BoatReservationService

    @Autowired
    private lateinit var boatService: BoatService

    @MockBean
    private lateinit var citizenContextProvider: CitizenContextProvider

    @Autowired
    private lateinit var reserverService: ReserverService

    @Autowired
    private lateinit var organizationService: OrganizationService

    @Autowired
    private lateinit var reservationService: ReservationService

    @MockBean
    private lateinit var seasonalService: SeasonalService

    @Autowired
    private lateinit var reserverRepository: ReserverRepository

    @MockitoBean
    private lateinit var messageService: MessageService

    @BeforeEach
    override fun resetDatabase() {
        PaytrailMock.reset()
        deleteAllReservations(jdbi)
        deleteAllBoatSpaces(jdbi)
        deleteAllBoats(jdbi)
        deleteAllOrganizations(jdbi)
    }

    @BeforeEach
    fun denyAllByDefault() {
        logOut()
        disallowReservation(any())
    }

    @Test
    fun `should prevent reservation for non citizens`() {
        allowReservation(any())
        val boatSpaceId = insertBoatSpace()

        assertThrows<Unauthorized> {
            reservationService.startReservation(boatSpaceId)
        }
    }

    @Test
    fun `should prevent reservation when citizen and their organizations do not have permission to reserve`() {
        val organization = testUtils.createOrganization("Test organization", organizationService)
        organizationService.addCitizenToOrganization(organization.id, citizenIdOlivia)
        loginAs(citizenIdOlivia)

        disallowReservation(eq(citizenIdOlivia))
        disallowReservation(eq(organizationId))

        val boatSpaceId = insertBoatSpace()
        val exception =
            assertThrows<Forbidden> {
                reservationService.startReservation(boatSpaceId)
            }

        assertEquals("Citizen and their organizations can not reserve slip", exception.message)
    }

    @Test
    fun `should allow reservation if the citizen does not have permission but their organization does`() {
        val organization = testUtils.createOrganization("Test organization", organizationService)
        organizationService.addCitizenToOrganization(organization.id, citizenIdOlivia)
        loginAs(citizenIdOlivia)

        disallowReservation(eq(citizenIdOlivia))
        allowReservation(eq(organization.id))

        val boatSpaceId = insertBoatSpace()

        reservationService.startReservation(boatSpaceId)
    }

    @Test
    fun `should allow reservation if the citizen does have permission but their organization does not`() {
        val organization = testUtils.createOrganization("Test organization", organizationService)
        organizationService.addCitizenToOrganization(organization.id, citizenIdOlivia)

        loginAs(citizenIdOlivia)

        allowReservation(eq(citizenIdOlivia))
        disallowReservation(eq(organization.id))

        val boatSpaceId = insertBoatSpace()

        reservationService.startReservation(boatSpaceId)
    }

    @Test
    fun `should prevent multiple unfinished reservations`() {
        loginAs(citizenIdOlivia)
        allowReservation(eq(citizenIdOlivia))
        val boatSpaceId = insertBoatSpace()

        reservationService.startReservation(boatSpaceId)
        val exception =
            assertThrows<Forbidden> {
                reservationService.startReservation(boatSpaceId)
            }

        assertEquals("Citizen can not have multiple reservations open", exception.message)
    }

    @Test
    fun `citizen should have unfinished reservation after starting`() {
        loginAs(citizenIdOlivia)
        allowReservation(eq(citizenIdOlivia))
        val boatSpaceId = insertBoatSpace()

        reservationService.startReservation(boatSpaceId)

        val reservation = reservationService.getUnfinishedReservationForCurrentCitizen()
        assertNotNull(reservation)
        assertEquals(citizenIdOlivia, reservation.reserverId)
        assertEquals(boatSpaceId, reservation.boatSpaceId)
    }

    @Test
    fun `should prevent filling reservation for non citizens`() {
        loginAs(citizenIdOlivia)
        allowReservation(eq(citizenIdOlivia))
        val boatSpaceId = insertBoatSpace()
        val information = createReservationInformation()
        val (reservationId) = reservationService.startReservation(boatSpaceId)

        logOut()
        assertThrows<Unauthorized> {
            reservationService.fillReservationInformation(reservationId, information)
        }
    }

    @Test
    fun `citizen should not be allowed to fill other people reservations`() {
        loginAs(citizenIdOlivia)
        allowReservation(eq(citizenIdOlivia))
        val boatSpaceId = insertBoatSpace()
        val information = createReservationInformation()
        val (reservationId) = reservationService.startReservation(boatSpaceId)

        loginAs(citizenIdLeo)
        assertThrows<Unauthorized> {
            reservationService.fillReservationInformation(reservationId, information)
        }
    }

    @Test
    fun `fill should validate reservation information`() {
        loginAs(citizenIdOlivia)
        allowReservation(eq(citizenIdOlivia))
        val boatSpaceId = insertBoatSpace()
        val information =
            createReservationInformation(
                citizen =
                    ReservationInformation.Citizen(
                        email = "not a valid email",
                        phone = "1234567890",
                    )
            )

        val (reservationId) = reservationService.startReservation(boatSpaceId)
        val exception =
            assertThrows<ConstraintViolationException> {
                reservationService.fillReservationInformation(reservationId, information)
            }

        assertEquals("email: {validation.email}", exception.message)
    }

    @Test
    fun `citizen must certify information when filling the reservation`() {
        loginAs(citizenIdOlivia)
        allowReservation(eq(citizenIdOlivia))
        val boatSpaceId = insertBoatSpace()
        val information =
            createReservationInformation(
                certifyInformation = false
            )

        val (reservationId) = reservationService.startReservation(boatSpaceId)
        val exception =
            assertThrows<ConstraintViolationException> {
                reservationService.fillReservationInformation(reservationId, information)
            }

        assertEquals("certifyInformation: {validation.certifyInformation}", exception.message)
    }

    @Test
    fun `citizen must agree to rules when filling the reservation`() {
        loginAs(citizenIdOlivia)
        allowReservation(eq(citizenIdOlivia))
        val boatSpaceId = insertBoatSpace()
        val information =
            createReservationInformation(
                agreeToRules = false
            )

        val (reservationId) = reservationService.startReservation(boatSpaceId)
        val exception =
            assertThrows<ConstraintViolationException> {
                reservationService.fillReservationInformation(reservationId, information)
            }

        assertEquals("agreeToRules: {validation.agreeToRules}", exception.message)
    }

    @Test
    fun `fill should create new boat`() {
        loginAs(citizenIdOlivia)
        allowReservation(eq(citizenIdOlivia))
        val boatSpaceId = insertBoatSpace()
        val information =
            createReservationInformation(
                boat =
                    ReservationInformation.Boat(
                        id = null,
                        name = "new boat",
                        type = BoatType.OutboardMotor,
                        width = BigDecimal(1),
                        length = BigDecimal(1),
                        depth = BigDecimal(1),
                        weight = 1000,
                        registrationNumber = "1234567890",
                        otherIdentification = "",
                        extraInformation = "",
                        ownership = OwnershipStatus.Owner,
                    )
            )

        val (reservationId) = reservationService.startReservation(boatSpaceId)
        reservationService.fillReservationInformation(reservationId, information)

        val reservation = boatReservationService.getReservationWithDependencies(reservationId)!!
        val boat = boatService.getBoat(reservation.boatId!!)!!
        assertEquals(information.boat.name, boat.name)
    }

    @Test
    fun `fill should update existing boat`() {
        loginAs(citizenIdOlivia)
        allowReservation(eq(citizenIdOlivia))
        val boatSpaceId = insertBoatSpace()
        val existingBoatId = insertBoat(citizenIdOlivia, "old name")
        val information =
            createReservationInformation(
                boat =
                    ReservationInformation.Boat(
                        id = existingBoatId,
                        name = "new boat",
                        type = BoatType.OutboardMotor,
                        width = BigDecimal(1),
                        length = BigDecimal(1),
                        depth = BigDecimal(1),
                        weight = 1000,
                        registrationNumber = "1234567890",
                        otherIdentification = "",
                        extraInformation = "",
                        ownership = OwnershipStatus.Owner,
                    )
            )

        val (reservationId) = reservationService.startReservation(boatSpaceId)
        reservationService.fillReservationInformation(reservationId, information)

        val boat = boatService.getBoat(existingBoatId)!!
        assertEquals(information.boat.name, boat.name)
    }

    @Test
    fun `fill should create new organization`() {
        loginAs(citizenIdOlivia)
        allowReservation(any())
        val boatSpaceId = insertBoatSpace()
        val information =
            createReservationInformation(
                organization =
                    ReservationInformation.Organization(
                        id = null,
                        name = "new organization",
                        businessId = "1234567890",
                        municipalityCode = "1",
                        phone = "1234567890",
                        email = "test@test.com",
                        address = "",
                        postalCode = "",
                        city = "",
                    )
            )

        val (reservationId) = reservationService.startReservation(boatSpaceId)
        val reservation = reservationService.fillReservationInformation(reservationId, information)

        val organization = organizationService.getOrganizationById(reservation.reserverId!!)!!
        assertEquals(information.organization!!.name, organization.name)
    }

    @Test
    fun `fill should update existing organization`() {
        loginAs(citizenIdOlivia)
        allowReservation(any())
        val boatSpaceId = insertBoatSpace()
        val existingOrganizationId = insertOrganization("old name")
        val information =
            createReservationInformation(
                organization =
                    ReservationInformation.Organization(
                        id = existingOrganizationId,
                        name = "new organization name",
                        businessId = "1234567890",
                        municipalityCode = "1",
                        phone = "1234567890",
                        email = "test@test.com",
                        address = "",
                        postalCode = "",
                        city = "",
                    )
            )

        val (reservationId) = reservationService.startReservation(boatSpaceId)
        reservationService.fillReservationInformation(reservationId, information)

        val organization = organizationService.getOrganizationById(existingOrganizationId)!!
        assertEquals(information.organization!!.name, organization.name)
    }

    @Test
    fun `fill should move reservation status to payment`() {
        loginAs(citizenIdOlivia)
        allowReservation(eq(citizenIdOlivia))
        val boatSpaceId = insertBoatSpace()
        val information = createReservationInformation()

        val (reservationId) = reservationService.startReservation(boatSpaceId)
        reservationService.fillReservationInformation(reservationId, information)

        val reservation = boatReservationService.getReservationWithDependencies(reservationId)

        assertNotNull(reservation)
        assertEquals(ReservationStatus.Payment, reservation.status)
    }

    @Test
    fun `only reservations in info state can be filled`() {
        loginAs(citizenIdOlivia)
        allowReservation(eq(citizenIdOlivia))
        val boatSpaceId = insertBoatSpace()
        val information = createReservationInformation()

        val (reservationId) = reservationService.startReservation(boatSpaceId)
        reservationService.fillReservationInformation(reservationId, information)

        val exception =
            assertThrows<Conflict> {
                reservationService.fillReservationInformation(reservationId, information)
            }

        assertEquals("Reservation is already filled", exception.message)
    }

    @Test
    fun `should prevent canceling reservation for non citizens`() {
        loginAs(citizenIdOlivia)
        allowReservation(eq(citizenIdOlivia))
        val boatSpaceId = insertBoatSpace()
        val (reservationId) = reservationService.startReservation(boatSpaceId)

        logOut()
        assertThrows<Unauthorized> {
            reservationService.cancelUnfinishedReservation(reservationId)
        }
    }

    @Test
    fun `citizen should be allowed to cancel only own reservations`() {
        loginAs(citizenIdOlivia)
        allowReservation(eq(citizenIdOlivia))
        val boatSpaceId = insertBoatSpace()
        val (reservationId) = reservationService.startReservation(boatSpaceId)

        loginAs(citizenIdLeo)
        assertThrows<Unauthorized> {
            reservationService.cancelUnfinishedReservation(reservationId)
        }
    }

    @Test
    fun `cancel should delete reservation`() {
        loginAs(citizenIdOlivia)
        allowReservation(eq(citizenIdOlivia))
        val boatSpaceId = insertBoatSpace()

        val (reservationId) = reservationService.startReservation(boatSpaceId)
        reservationService.cancelUnfinishedReservation(reservationId)

        val reservation = boatReservationService.getReservationWithDependencies(reservationId)
        assertNull(reservation)
    }

    @Test
    fun `citizen should not have unfinished reservation after cancel`() {
        loginAs(citizenIdOlivia)
        allowReservation(eq(citizenIdOlivia))
        val boatSpaceId = insertBoatSpace()

        val (reservationId) = reservationService.startReservation(boatSpaceId)
        reservationService.cancelUnfinishedReservation(reservationId)

        val reservation = reservationService.getUnfinishedReservationForCurrentCitizen()
        assertNull(reservation)
    }

    @Test
    fun `successful reservation`(): Unit =
        runBlocking {
            loginAs(citizenIdOlivia)
            allowReservation(any())
            val boatSpaceId = insertBoatSpace()
            val information = createReservationInformation()
            val (reservationId) = reservationService.startReservation(boatSpaceId)
            reservationService.fillReservationInformation(reservationId, information)
            reservationService.getPaymentInformation(reservationId)
            val paytrailPayments = PaytrailMock.paytrailPayments
            assertEquals(1, paytrailPayments.size)
            assertEquals(1, paytrailPayments.get(0).items?.size)
            val item = paytrailPayments.get(0).items?.get(0)
            assertEquals("329700-1230329-T1270-0-0-0-0-0-0-0-0-0-100", item?.productCode)
            assertEquals("Venepaikka 2025 Haukilahti A 001", item?.description)
        }

    @Test
    fun `should prevent paying reservation for non citizens`(): Unit =
        runBlocking {
            loginAs(citizenIdOlivia)
            allowReservation(eq(citizenIdOlivia))
            val boatSpaceId = insertBoatSpace()
            val information = createReservationInformation()
            val (reservationId) = reservationService.startReservation(boatSpaceId)
            reservationService.fillReservationInformation(reservationId, information)

            logOut()
            assertThrows<Unauthorized> {
                reservationService.getPaymentInformation(reservationId)
            }
        }

    @Test
    fun `citizen should be allowed to pay only own reservations`(): Unit =
        runBlocking {
            loginAs(citizenIdOlivia)
            allowReservation(eq(citizenIdOlivia))
            val boatSpaceId = insertBoatSpace()
            val information = createReservationInformation()
            val (reservationId) = reservationService.startReservation(boatSpaceId)
            reservationService.fillReservationInformation(reservationId, information)

            loginAs(citizenIdLeo)
            assertThrows<Unauthorized> {
                reservationService.getPaymentInformation(reservationId)
            }
        }

    @Test
    fun `only reservations in payment state can be paid`() =
        runBlocking {
            loginAs(citizenIdOlivia)
            allowReservation(eq(citizenIdOlivia))
            val boatSpaceId = insertBoatSpace()
            val (reservationId) = reservationService.startReservation(boatSpaceId)

            val exception =
                assertThrows<Conflict> {
                    reservationService.getPaymentInformation(reservationId)
                }

            assertEquals("Reservation is not filled", exception.message)
        }

    @Test
    fun `discount is taken into account in payment`(): Unit =
        runBlocking {
            val discountPercentage = 50
            loginAs(citizenIdOlivia)
            allowReservation(any())
            val boatSpaceId = insertBoatSpace()
            val boatSpacePrice = getBoatSpacePrice(boatSpaceId)
            assertNotNull(boatSpacePrice)
            val expectedPriceToBePayed = (boatSpacePrice - (boatSpacePrice * discountPercentage / 100))
            val information = createReservationInformation()
            reserverRepository.updateDiscount(citizenIdOlivia, discountPercentage)
            val (reservationId) = reservationService.startReservation(boatSpaceId)
            reservationService.fillReservationInformation(reservationId, information)
            reservationService.getPaymentInformation(reservationId)
            val paytrailPayments = PaytrailMock.paytrailPayments
            val amountToBePayed = paytrailPayments.get(0).amount
            assertEquals(amountToBePayed, expectedPriceToBePayed)
            assertEquals(1, paytrailPayments.size)
            assertEquals(1, paytrailPayments.get(0).items?.size)
            val item = paytrailPayments.get(0).items?.get(0)
            assertEquals("329700-1230329-T1270-0-0-0-0-0-0-0-0-0-100", item?.productCode)
            assertEquals("Venepaikka 2025 Haukilahti A 001", item?.description)
        }

    @Test
    fun `100 percent discount results in error in payment because zero payments are not allowed`(): Unit =
        runBlocking {
            val discountPercentage = 100
            loginAs(citizenIdOlivia)
            allowReservation(any())
            val boatSpaceId = insertBoatSpace()
            val information = createReservationInformation()
            reserverRepository.updateDiscount(citizenIdOlivia, discountPercentage)
            val (reservationId) = reservationService.startReservation(boatSpaceId)
            reservationService.fillReservationInformation(reservationId, information)
            val reservation = boatReservationService.getBoatSpaceReservation(reservationId)
            assertEquals(ReservationStatus.Confirmed, reservation!!.status)
            assertEquals(discountPercentage, reservation.discountPercentage)
            val exception =
                assertThrows<Conflict> {
                    reservationService.getPaymentInformation(reservationId)
                }
            assertEquals("Reservation is not filled", exception.message)
        }

    @Test
    fun `Marking a reservation as ended should also insert a processed message entry for same reservation`(): Unit =
        runBlocking {
            loginAs(citizenIdOlivia)
            allowReservation(any())
            val boatSpaceId = insertBoatSpace()
            val information = createReservationInformation()
            val (reservationId) = reservationService.startReservation(boatSpaceId)
            reservationService.fillReservationInformation(reservationId, information)
            boatReservationService.markReservationEnded(reservationId, LocalDate.now().plusDays(30).atStartOfDay())
            verify(messageService, times(1)).getAndInsertUnsentEmails(
                eq(ReservationType.Marine),
                eq(reservationId),
                eq(EmailType.ExpiredReservation),
                eq(listOf("test@test.com"))
            )
        }

    private fun insertBoat(
        citizenId: UUID,
        name: String = "TestBoat"
    ): Int =
        boatService
            .insertBoat(
                citizenId,
                "registrationCode",
                name,
                150,
                150,
                150,
                150,
                BoatType.Sailboat,
                "",
                "",
                OwnershipStatus.Owner
            ).id

    private fun insertBoatSpace(): Int {
        val boatSpaceId = 1234
        insertDevBoatSpace(
            DevBoatSpace(
                id = boatSpaceId,
                type = BoatSpaceType.Slip,
                locationId = 1,
                priceId = 1,
                section = "A",
                placeNumber = 1,
                amenity = BoatSpaceAmenity.None,
                widthCm = 100,
                lengthCm = 200,
                description = "Test boat space"
            )
        )

        return boatSpaceId
    }

    private fun insertOrganization(name: String = "TestOrganization"): UUID =
        organizationService
            .insertOrganization(
                businessId = "1234567890",
                name = name,
                phone = "1234567890",
                email = "test@test.com",
                streetAddress = "",
                streetAddressSv = "",
                postalCode = "",
                postOffice = "",
                postOfficeSv = "",
                municipalityCode = 1,
                billingName = "",
                billingStreetAddress = "",
                billingPostalCode = "",
                billingPostOffice = ""
            ).id

    private fun loginAs(citizenId: UUID) {
        Mockito.`when`(citizenContextProvider.getCurrentCitizen()).thenReturn(
            reserverService.getCitizen(citizenId)
        )
    }

    private fun logOut() {
        Mockito.`when`(citizenContextProvider.getCurrentCitizen()).thenReturn(null)
    }

    private fun allowReservation(
        citizenMatcher: UUID = any(),
        boatSpaceTypeMatcher: BoatSpaceType = any()
    ) {
        Mockito.`when`(seasonalService.canReserveANewSpace(citizenMatcher, boatSpaceTypeMatcher)).thenReturn(
            ReservationResult.Success(
                ReservationResultSuccess(
                    LocalDate.now(),
                    LocalDate.now().plusDays(30),
                    ReservationValidity.Indefinite
                )
            )
        )
    }

    private fun disallowReservation(
        citizenMatcher: UUID = any(),
        boatSpaceTypeMatcher: BoatSpaceType = any()
    ) {
        Mockito
            .`when`(seasonalService.canReserveANewSpace(citizenMatcher, boatSpaceTypeMatcher))
            .thenReturn(ReservationResult.Failure(ReservationResultErrorCode.NotPossible))
    }

    private fun createReservationInformation(
        citizen: ReservationInformation.Citizen? = null,
        organization: ReservationInformation.Organization? = null,
        boat: ReservationInformation.Boat? = null,
        certifyInformation: Boolean = true,
        agreeToRules: Boolean = true,
    ): ReservationInformation {
        val information =
            ReservationInformation(
                citizen =
                    citizen ?: ReservationInformation.Citizen(
                        email = "test@test.com",
                        phone = "1234567890",
                    ),
                organization = organization,
                boat =
                    boat ?: ReservationInformation.Boat(
                        id = null,
                        name = "test",
                        type = BoatType.OutboardMotor,
                        width = BigDecimal(1),
                        length = BigDecimal(1),
                        depth = BigDecimal(1),
                        weight = 1000,
                        registrationNumber = "1234567890",
                        otherIdentification = "",
                        extraInformation = "",
                        ownership = OwnershipStatus.Owner,
                    ),
                certifyInformation = certifyInformation,
                agreeToRules = agreeToRules,
            )
        return information
    }

    fun getBoatSpacePrice(boatSpaceId: Int): Int =
        jdbi.withHandleUnchecked { handle ->
            val query =
                handle.createQuery("SELECT p.price_cents FROM price p JOIN boat_space s ON s.price_id = p.id AND s.id = :id")
            query.bind("id", boatSpaceId)
            query.mapTo<Int>().findOne().orElse(null)
        }
}
