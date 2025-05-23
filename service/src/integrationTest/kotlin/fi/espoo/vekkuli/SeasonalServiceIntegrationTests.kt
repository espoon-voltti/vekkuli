package fi.espoo.vekkuli

import fi.espoo.vekkuli.boatSpace.employeeReservationList.EmployeeReservationListService
import fi.espoo.vekkuli.boatSpace.invoice.BoatSpaceInvoiceService
import fi.espoo.vekkuli.boatSpace.reservationForm.ReservationFormService
import fi.espoo.vekkuli.boatSpace.reservationForm.ReserveBoatSpaceInput
import fi.espoo.vekkuli.boatSpace.seasonalService.SeasonalService
import fi.espoo.vekkuli.boatSpace.terminateReservation.TerminateReservationService
import fi.espoo.vekkuli.config.BoatSpaceConfig.getSlipEndDate
import fi.espoo.vekkuli.config.BoatSpaceConfig.getWinterEndDate
import fi.espoo.vekkuli.domain.*
import fi.espoo.vekkuli.service.*
import fi.espoo.vekkuli.utils.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.math.BigDecimal
import java.time.LocalDate
import java.util.*
import kotlin.collections.listOf
import kotlin.test.assertContains

@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SeasonalServiceIntegrationTests : IntegrationTestBase() {
    @Autowired
    private lateinit var seasonalService: SeasonalService

    @Autowired
    private lateinit var terminateReservationService: TerminateReservationService
    val espooCitizenId = citizenIdOlivia
    val helsinkiCitizenId = UUID.fromString("1128bd21-fbbc-4e9a-8658-dc2044a64a58")
    val espooRulesAppliedNonEspooCitizenId = citizenIdJorma

    @Autowired
    lateinit var reservationService: BoatReservationService

    @Autowired
    lateinit var employeeReservationListService: EmployeeReservationListService

    @Autowired lateinit var formReservationService: ReservationFormService

    @Autowired lateinit var invoiceService: BoatSpaceInvoiceService

    @BeforeEach
    override fun resetDatabase() {
        deleteAllReservations(jdbi)
    }

    private fun validateIndefiniteSlipReservationFor(reserverId: UUID) {
        mockTimeProvider(timeProvider, startOfSlipReservationPeriod)
        val result = seasonalService.canReserveANewSpace(reserverId, BoatSpaceType.Slip)
        if (result is ReservationResult.Success) {
            assertEquals(getSlipEndDate(timeProvider.getCurrentDate(), ReservationValidity.Indefinite), result.data.endDate)
            assertEquals(ReservationValidity.Indefinite, result.data.reservationValidity)
        } else {
            throw AssertionError("canReserveANewSlip failed")
        }
    }

    @Test
    fun `first place should be indefinite for Espoo citizens reserving a slip`() {
        validateIndefiniteSlipReservationFor(espooCitizenId)
    }

    @Test
    fun `first place should be indefinite for non Espoo reserver having espooRulesAllowed enabled reserving a slip`() {
        validateIndefiniteSlipReservationFor(espooRulesAppliedNonEspooCitizenId)
    }

    private fun validateIndefiniteWinterSpaceFor(reserverId: UUID) {
        mockTimeProvider(timeProvider, startOfWinterReservationPeriod)
        val result = seasonalService.canReserveANewSpace(reserverId, BoatSpaceType.Winter)
        if (result is ReservationResult.Success) {
            assertEquals(getWinterEndDate(timeProvider.getCurrentDate(), ReservationValidity.Indefinite), result.data.endDate)
            assertEquals(ReservationValidity.Indefinite, result.data.reservationValidity)
        } else {
            throw AssertionError("canReserveANewWinterSpace failed")
        }
    }

    @Test
    fun `first place should be indefinite for Espoo citizens reserving a winter space`() {
        validateIndefiniteWinterSpaceFor(espooCitizenId)
    }

    @Test
    fun `first place should be indefinite for non Espoo reserver having espooRulesAllowed enabled reserving a winter space`() {
        validateIndefiniteWinterSpaceFor(espooRulesAppliedNonEspooCitizenId)
    }

    private fun validateFixedTermForSecondSlipReservationFor(reserverId: UUID) {
        mockTimeProvider(timeProvider, startOfSlipReservationPeriod)
        val madeReservation =
            testUtils.createReservationInPaymentState(timeProvider, reservationService, reserverId, 1)
        formReservationService.processBoatSpaceReservation(
            reserverId,
            ReserveBoatSpaceInput(
                reservationId = madeReservation.id,
                boatId = null,
                boatType = BoatType.Sailboat,
                width = BigDecimal(3.5),
                length = BigDecimal(6.5),
                depth = BigDecimal(3.0),
                weight = 180,
                boatRegistrationNumber = "JFK293",
                boatName = "Boat",
                otherIdentification = "1",
                extraInformation = "1",
                ownerShip = OwnershipStatus.FutureOwner,
                phone = "",
                email = ""
            ),
            ReservationStatus.Confirmed,
            ReservationValidity.Indefinite,
            timeProvider.getCurrentDate().minusWeeks(1),
            timeProvider.getCurrentDate().plusWeeks(1),
        )
        val result = seasonalService.canReserveANewSpace(reserverId, BoatSpaceType.Slip)
        if (result is ReservationResult.Success) {
            assertEquals(LocalDate.of(2025, 12, 31), result.data.endDate)
            assertEquals(ReservationValidity.FixedTerm, result.data.reservationValidity)
        } else {
            throw AssertionError("canReserveANewSlip failed")
        }
    }

    @Test
    fun `second place should be fixed term for Espoo citizens reserving a slip`() {
        validateFixedTermForSecondSlipReservationFor(espooCitizenId)
    }

    @Test
    fun `second place should be fixed term for non Espoo reserver having espooRulesAllowed enabled reserving a slip`() {
        validateFixedTermForSecondSlipReservationFor(espooRulesAppliedNonEspooCitizenId)
    }

    private fun validateIndefiniteSecondWinterReservationFor(reserverId: UUID) {
        mockTimeProvider(timeProvider, startOfWinterReservationPeriod)
        val madeReservation =
            testUtils.createReservationInPaymentState(timeProvider, reservationService, reserverId, 8)
        formReservationService.processBoatSpaceReservation(
            reserverId,
            ReserveBoatSpaceInput(
                reservationId = madeReservation.id,
                boatId = null,
                boatType = BoatType.Sailboat,
                width = BigDecimal(3.5),
                length = BigDecimal(6.5),
                depth = BigDecimal(3.0),
                weight = 180,
                boatRegistrationNumber = "JFK293",
                boatName = "Boat",
                otherIdentification = "1",
                extraInformation = "1",
                ownerShip = OwnershipStatus.FutureOwner,
                phone = "",
                email = "",
                storageType = StorageType.Buck
            ),
            ReservationStatus.Confirmed,
            ReservationValidity.Indefinite,
            timeProvider.getCurrentDate().minusWeeks(1),
            timeProvider.getCurrentDate().plusWeeks(1),
        )
        val result = seasonalService.canReserveANewSpace(reserverId, BoatSpaceType.Winter)
        if (result is ReservationResult.Success) {
            assertEquals(getWinterEndDate(timeProvider.getCurrentDate(), ReservationValidity.Indefinite), result.data.endDate)
            assertEquals(ReservationValidity.Indefinite, result.data.reservationValidity)
        } else {
            throw AssertionError("canReserveANewWinterSpace failed")
        }
    }

    @Test
    fun `second place should be indefinite for Espoo citizens reserving a winter space`() {
        validateIndefiniteSecondWinterReservationFor(espooCitizenId)
    }

    @Test
    fun `second place should be indefinite for non Espoo reserver having espooRulesAllowed enabled reserving a winter space`() {
        validateIndefiniteSecondWinterReservationFor(espooRulesAppliedNonEspooCitizenId)
    }

    private fun validateThirdSlipReservationFailsFor(reserverId: UUID) {
        mockTimeProvider(timeProvider, startOfSlipReservationPeriod)
        val madeReservation1 =
            testUtils.createReservationInPaymentState(timeProvider, reservationService, reserverId, 1)
        formReservationService.processBoatSpaceReservation(
            reserverId,
            ReserveBoatSpaceInput(
                reservationId = madeReservation1.id,
                boatId = null,
                boatType = BoatType.Sailboat,
                width = BigDecimal(3.5),
                length = BigDecimal(6.5),
                depth = BigDecimal(3.0),
                weight = 180,
                boatRegistrationNumber = "JFK293",
                boatName = "Boat",
                otherIdentification = "1",
                extraInformation = "1",
                ownerShip = OwnershipStatus.FutureOwner,
                phone = "",
                email = ""
            ),
            ReservationStatus.Confirmed,
            ReservationValidity.Indefinite,
            timeProvider.getCurrentDate().minusWeeks(1),
            timeProvider.getCurrentDate().plusWeeks(1),
        )
        val madeReservation2 =
            testUtils.createReservationInPaymentState(timeProvider, reservationService, reserverId, 2)
        formReservationService.processBoatSpaceReservation(
            reserverId,
            ReserveBoatSpaceInput(
                reservationId = madeReservation2.id,
                boatId = null,
                boatType = BoatType.Sailboat,
                width = BigDecimal(3.5),
                length = BigDecimal(6.5),
                depth = BigDecimal(3.0),
                weight = 180,
                boatRegistrationNumber = "JFK293",
                boatName = "Boat",
                otherIdentification = "1",
                extraInformation = "1",
                ownerShip = OwnershipStatus.FutureOwner,
                phone = "",
                email = ""
            ),
            ReservationStatus.Confirmed,
            ReservationValidity.Indefinite,
            timeProvider.getCurrentDate().minusWeeks(1),
            timeProvider.getCurrentDate().plusWeeks(1),
        )
        val result = seasonalService.canReserveANewSpace(reserverId, BoatSpaceType.Slip)
        if (result is ReservationResult.Failure) {
            assertEquals(ReservationResultErrorCode.MaxReservations, result.errorCode)
        } else {
            throw AssertionError("canReserveANewSlip succeeded, but it should fail")
        }
    }

    @Test
    fun `third place should fail for Espoo citizens reserving a slip`() {
        validateThirdSlipReservationFailsFor(espooCitizenId)
    }

    @Test
    fun `third place should fail for non Espoo reserver having espooRulesAllowed enabled reserving a slip`() {
        validateThirdSlipReservationFailsFor(espooRulesAppliedNonEspooCitizenId)
    }

    private fun validateThirdWinterSpaceReservationFailsFor(reserverId: UUID) {
        mockTimeProvider(timeProvider, startOfWinterReservationPeriod)
        val madeReservation1 =
            testUtils.createReservationInPaymentState(timeProvider, reservationService, reserverId, 7)
        formReservationService.processBoatSpaceReservation(
            reserverId,
            ReserveBoatSpaceInput(
                reservationId = madeReservation1.id,
                boatId = null,
                boatType = BoatType.Sailboat,
                width = BigDecimal(3.5),
                length = BigDecimal(6.5),
                depth = BigDecimal(3.0),
                weight = 180,
                boatRegistrationNumber = "JFK293",
                boatName = "Boat",
                otherIdentification = "1",
                extraInformation = "1",
                ownerShip = OwnershipStatus.FutureOwner,
                phone = "",
                email = "",
                storageType = StorageType.BuckWithTent
            ),
            ReservationStatus.Confirmed,
            ReservationValidity.Indefinite,
            timeProvider.getCurrentDate().minusWeeks(1),
            timeProvider.getCurrentDate().plusWeeks(1),
        )
        val madeReservation2 =
            testUtils.createReservationInPaymentState(timeProvider, reservationService, reserverId, 8)
        formReservationService.processBoatSpaceReservation(
            reserverId,
            ReserveBoatSpaceInput(
                reservationId = madeReservation2.id,
                boatId = null,
                boatType = BoatType.Sailboat,
                width = BigDecimal(3.5),
                length = BigDecimal(6.5),
                depth = BigDecimal(3.0),
                weight = 180,
                boatRegistrationNumber = "JFK293",
                boatName = "Boat",
                otherIdentification = "1",
                extraInformation = "1",
                ownerShip = OwnershipStatus.FutureOwner,
                phone = "",
                email = "",
                storageType = StorageType.Buck
            ),
            ReservationStatus.Confirmed,
            ReservationValidity.Indefinite,
            timeProvider.getCurrentDate().minusWeeks(1),
            timeProvider.getCurrentDate().plusWeeks(1),
        )
        val result = seasonalService.canReserveANewSpace(reserverId, BoatSpaceType.Winter)
        if (result is ReservationResult.Failure) {
            assertEquals(ReservationResultErrorCode.MaxReservations, result.errorCode)
        } else {
            throw AssertionError("canReserveANewWinterSpace succeeded, but it should fail")
        }
    }

    @Test
    fun `third place should fail for Espoo citizens reserving a winter space`() {
        validateThirdWinterSpaceReservationFailsFor(espooCitizenId)
    }

    @Test
    fun `third place should fail for non Espoo reserver having espooRulesAllowed enabled reserving a winter space`() {
        validateThirdWinterSpaceReservationFailsFor(espooCitizenId)
    }

    @Test
    fun `should allow reserving 2 storage spaces`() {
        mockTimeProvider(timeProvider, startOfStorageReservationPeriod)
        val firstStorageReservation =
            testUtils.createReservationInPaymentState(timeProvider, reservationService, espooCitizenId, 5)
        formReservationService.processBoatSpaceReservation(
            espooCitizenId,
            ReserveBoatSpaceInput(
                reservationId = firstStorageReservation.id,
                boatId = null,
                boatType = BoatType.Sailboat,
                width = BigDecimal(3.5),
                length = BigDecimal(6.5),
                depth = BigDecimal(3.0),
                weight = 180,
                boatRegistrationNumber = "JFK293",
                boatName = "Boat",
                otherIdentification = "1",
                extraInformation = "1",
                ownerShip = OwnershipStatus.Owner,
                phone = "",
                email = "",
                storageType = StorageType.Buck
            ),
            ReservationStatus.Confirmed,
            ReservationValidity.Indefinite,
            timeProvider.getCurrentDate().minusWeeks(1),
            timeProvider.getCurrentDate().plusWeeks(1),
        )

        val canReserveAfterFirstReservation = seasonalService.canReserveANewSpace(espooCitizenId, BoatSpaceType.Storage)

        assert(canReserveAfterFirstReservation.success)

        val secondStorageReservation =
            testUtils.createReservationInPaymentState(timeProvider, reservationService, espooCitizenId, 6)
        formReservationService.processBoatSpaceReservation(
            espooCitizenId,
            ReserveBoatSpaceInput(
                reservationId = secondStorageReservation.id,
                boatId = null,
                boatType = BoatType.Sailboat,
                width = BigDecimal(3.5),
                length = BigDecimal(6.5),
                depth = BigDecimal(3.0),
                weight = 180,
                boatRegistrationNumber = "JFK293",
                boatName = "Boat",
                otherIdentification = "1",
                extraInformation = "1",
                ownerShip = OwnershipStatus.Owner,
                phone = "",
                email = "",
                storageType = StorageType.Buck
            ),
            ReservationStatus.Confirmed,
            ReservationValidity.Indefinite,
            timeProvider.getCurrentDate().minusWeeks(1),
            timeProvider.getCurrentDate().plusWeeks(1),
        )
        val result = seasonalService.canReserveANewSpace(espooCitizenId, BoatSpaceType.Storage)
        if (result is ReservationResult.Failure) {
            assertEquals(ReservationResultErrorCode.MaxReservations, result.errorCode)
        } else {
            throw AssertionError("canReserveANewStorageSpace succeeded, but it should fail")
        }
    }

    @Test
    fun `first place should be fixed term for Helsinki citizens reserving a slip`() {
        mockTimeProvider(timeProvider, startOfSlipReservationPeriod)
        val result = seasonalService.canReserveANewSpace(helsinkiCitizenId, BoatSpaceType.Slip)
        if (result is ReservationResult.Success) {
            assertEquals(LocalDate.of(2025, 12, 31), result.data.endDate)
            assertEquals(ReservationValidity.FixedTerm, result.data.reservationValidity)
        } else {
            throw AssertionError("canReserveANewSlip failed")
        }
    }

    @Test
    fun `Should not allow reserving on the first day of season before 0900`() {
        mockTimeProvider(timeProvider, startOfSlipReservationPeriod.withHour(8))
        assertTrue(seasonalService.canReserveANewSpace(helsinkiCitizenId, BoatSpaceType.Slip) is ReservationResult.Failure)

        mockTimeProvider(timeProvider, startOfSlipReservationPeriod.withHour(9))
        assertTrue(seasonalService.canReserveANewSpace(helsinkiCitizenId, BoatSpaceType.Slip) is ReservationResult.Success)

        mockTimeProvider(timeProvider, startOfStorageReservationPeriod.withHour(8))
        assertTrue(seasonalService.canReserveANewSpace(helsinkiCitizenId, BoatSpaceType.Storage) is ReservationResult.Failure)

        mockTimeProvider(timeProvider, startOfStorageReservationPeriod.withHour(9))
        assertTrue(seasonalService.canReserveANewSpace(helsinkiCitizenId, BoatSpaceType.Storage) is ReservationResult.Success)

        mockTimeProvider(timeProvider, startOfWinterReservationPeriod.withHour(8))
        assertTrue(seasonalService.canReserveANewSpace(citizenIdLeo, BoatSpaceType.Winter) is ReservationResult.Failure)

        mockTimeProvider(timeProvider, startOfWinterReservationPeriod.withHour(9))
        assertTrue(seasonalService.canReserveANewSpace(citizenIdLeo, BoatSpaceType.Winter) is ReservationResult.Success)

        mockTimeProvider(timeProvider, startOfTrailerReservationPeriod.withHour(8))
        assertTrue(seasonalService.canReserveANewSpace(citizenIdLeo, BoatSpaceType.Trailer) is ReservationResult.Failure)

        mockTimeProvider(timeProvider, startOfTrailerReservationPeriod.withHour(9))
        assertTrue(seasonalService.canReserveANewSpace(citizenIdLeo, BoatSpaceType.Trailer) is ReservationResult.Success)
    }

    @Test
    fun `should fail if non Espoo citizen tries to reserve a winter space`() {
        mockTimeProvider(timeProvider, startOfWinterReservationPeriod)
        val result = seasonalService.canReserveANewSpace(helsinkiCitizenId, BoatSpaceType.Winter)
        if (result is ReservationResult.Failure) {
            assertEquals(ReservationResultErrorCode.NotEspooCitizen, result.errorCode)
        } else {
            throw AssertionError("canReserveANewWinterSpace succeeded, but it should fail")
        }
    }

    @Test
    fun `should not allow second place for Helsinki citizens reserving a slip`() {
        mockTimeProvider(timeProvider, startOfSlipReservationPeriod)
        val madeReservation = testUtils.createReservationInPaymentState(timeProvider, reservationService, helsinkiCitizenId, 1)
        formReservationService.processBoatSpaceReservation(
            helsinkiCitizenId,
            ReserveBoatSpaceInput(
                reservationId = madeReservation.id,
                boatId = null,
                boatType = BoatType.Sailboat,
                width = BigDecimal(3.5),
                length = BigDecimal(6.5),
                depth = BigDecimal(3.0),
                weight = 180,
                boatRegistrationNumber = "JFK293",
                boatName = "Boat",
                otherIdentification = "1",
                extraInformation = "1",
                ownerShip = OwnershipStatus.FutureOwner,
                phone = "",
                email = ""
            ),
            ReservationStatus.Confirmed,
            ReservationValidity.FixedTerm,
            timeProvider.getCurrentDate().minusWeeks(1),
            timeProvider.getCurrentDate().plusWeeks(1),
        )
        val result = seasonalService.canReserveANewSpace(helsinkiCitizenId, BoatSpaceType.Slip)
        if (result is ReservationResult.Failure) {
            assertEquals(ReservationResultErrorCode.MaxReservations, result.errorCode)
        } else {
            throw AssertionError("canReserveANewSlip succeeded, but it should fail")
        }
    }

    @Test
    fun `should get correct reservation with citizen`() {
        mockTimeProvider(timeProvider, startOfSlipReservationPeriod)
        val madeReservation =
            reservationService.insertBoatSpaceReservation(
                this.citizenIdLeo,
                this.citizenIdLeo,
                1,
                CreationType.New,
                startDate = timeProvider.getCurrentDate(),
                endDate = timeProvider.getCurrentDate(),
                validity = ReservationValidity.Indefinite,
            )

        val result = reservationService.getReservationWithReserver(madeReservation.id)
        assertEquals(madeReservation.id, result?.id, "reservation is the same")
        assertEquals(madeReservation.reserverId, result?.reserverId, "citizen is the same")
    }

    @Test
    fun `should update boat in reservation`() {
        mockTimeProvider(timeProvider, startOfSlipReservationPeriod)
        val madeReservation =
            reservationService.insertBoatSpaceReservation(
                this.citizenIdLeo,
                this.citizenIdLeo,
                1,
                CreationType.New,
                startDate = timeProvider.getCurrentDate(),
                endDate = timeProvider.getCurrentDate(),
                validity = ReservationValidity.FixedTerm,
            )
        val boatId = 1
        val updatedReservation =
            reservationService.updateBoatInBoatSpaceReservation(
                madeReservation.id,
                boatId,
                this.citizenIdLeo,
                ReservationStatus.Payment,
                ReservationValidity.Indefinite,
                timeProvider.getCurrentDate(),
                timeProvider.getCurrentDate(),
            )
        val reservation = reservationService.getReservationWithReserver(madeReservation.id)
        assertEquals(madeReservation.id, updatedReservation.id, "reservation is the same")
        assertEquals(madeReservation.reserverId, updatedReservation.reserverId, "citizen is the same")
        assertEquals(boatId, reservation?.boatId, "boat is updated")
        assertEquals(reservation?.validity, updatedReservation.validity, "validity is updated")
    }

    @Test
    fun `should get correct reservation for citizen`() {
        mockTimeProvider(timeProvider, startOfSlipReservationPeriod)
        val madeReservation =
            reservationService.insertBoatSpaceReservation(
                this.citizenIdLeo,
                this.citizenIdLeo,
                1,
                CreationType.New,
                startDate = timeProvider.getCurrentDate(),
                endDate = timeProvider.getCurrentDate(),
                validity = ReservationValidity.FixedTerm,
            )
        val reservation = reservationService.getUnfinishedReservationForCitizen(this.citizenIdLeo)
        assertEquals(madeReservation.id, reservation?.id, "reservation is the same")
    }

    @Test
    fun `should add reservation warnings on reservation with issues`() {
        mockTimeProvider(timeProvider, startOfSlipReservationPeriod)
        val madeReservation =
            testUtils.createReservationInPaymentState(
                timeProvider,
                reservationService,
                this.citizenIdLeo,
                1
            )

        formReservationService.processBoatSpaceReservation(
            this.citizenIdLeo,
            ReserveBoatSpaceInput(
                madeReservation.id,
                boatId = null,
                boatType = BoatType.Sailboat,
                width = BigDecimal(3.5),
                length = BigDecimal(6.5),
                depth = BigDecimal(3.0),
                weight = 180,
                boatRegistrationNumber = "JFK293",
                boatName = "Boat",
                otherIdentification = "1",
                extraInformation = "1",
                ownerShip = OwnershipStatus.FutureOwner,
                email = "email@email.com",
                phone = "0403849283",
            ),
            ReservationStatus.Confirmed,
            ReservationValidity.FixedTerm,
            timeProvider.getCurrentDate(),
            timeProvider.getCurrentDate()
        )
        val reservation =
            employeeReservationListService
                .getBoatSpaceReservations(
                    BoatSpaceReservationFilter(
                        sortBy = BoatSpaceReservationFilterColumn.PLACE,
                        ascending = true,
                    )
                ).items
                .first()

        assertEquals(4, reservation.warnings.count(), "Warnings should be present")
        assertEquals(
            listOf(
                ReservationWarningType.BoatFutureOwner.name,
                ReservationWarningType.BoatLength.name,
                ReservationWarningType.BoatWidth.name,
                ReservationWarningType.RegistrationCodeNotUnique.name
            ),
            reservation.warnings.sorted(),
            "Correct warnings should be present"
        )
    }

    @Test
    fun `should get correct reservations with filter`() {
        mockTimeProvider(timeProvider, startOfSlipReservationPeriod)
        // Location id 1 and amenity type beam
        testUtils.createReservationInConfirmedState(
            CreateReservationParams(
                timeProvider,
                this.citizenIdLeo,
                1,
                1
            )
        )
        testUtils.createReservationInPaymentState(timeProvider, reservationService, citizenIdOlivia, 2, 3)
        testUtils.createReservationInInfoState(
            this.citizenIdLeo,
            3
        )
        // Location id 2 and amenity type walk beam
        testUtils.createReservationInConfirmedState(
            CreateReservationParams(
                timeProvider,
                UUID.fromString("62d90eed-4ea3-4446-8023-8dad9c01dd34"),
                200,
                2
            )
        )
        // Location id 2 and amenity type beam
        testUtils.createReservationInConfirmedState(
            CreateReservationParams(
                timeProvider,
                UUID.fromString("62d90eed-4ea3-4446-8023-8dad9c01dd34"),
                177,
                2
            )
        )
        // Location id 4 and amenity type walk beam
        testUtils.createReservationInConfirmedState(
            CreateReservationParams(
                timeProvider,
                UUID.fromString("62d90eed-4ea3-4446-8023-8dad9c01dd34"),
                725,
                2
            )
        )

        val reservations =
            employeeReservationListService.getBoatSpaceReservations(
                BoatSpaceReservationFilter(
                    harbor = listOf(1, 2),
                    amenity = listOf(BoatSpaceAmenity.Beam, BoatSpaceAmenity.WalkBeam),
                    payment = listOf(PaymentFilter.CONFIRMED)
                )
            )

        assertEquals(3, reservations.totalRows, "reservations are out filtered correctly")
        assertEquals(listOf(1, 177, 200), reservations.items.map { it.boatSpaceId }, "correct reservations are returned")
    }

    @Test
    fun `should filter by payment status`() {
        mockTimeProvider(timeProvider, startOfSlipReservationPeriod)
        testUtils.createReservationInConfirmedState(CreateReservationParams(timeProvider, this.citizenIdLeo, 1, 1))

        testUtils.createReservationInInvoiceState(timeProvider, reservationService, invoiceService, citizenIdOlivia, 2, 3)

        val unfilteredReservations =
            employeeReservationListService.getBoatSpaceReservations(
                BoatSpaceReservationFilter()
            )

        val unpaidReservations =
            employeeReservationListService.getBoatSpaceReservations(
                BoatSpaceReservationFilter(
                    payment = listOf(PaymentFilter.PAYMENT, PaymentFilter.INVOICED)
                )
            )

        val paidReservations =
            employeeReservationListService.getBoatSpaceReservations(
                BoatSpaceReservationFilter(
                    payment = listOf(PaymentFilter.CONFIRMED)
                )
            )
        assertEquals(2, unfilteredReservations.totalRows, "reservations are filtered correctly")

        assertEquals(1, unpaidReservations.totalRows, "reservations are filtered correctly")
        assertEquals(2, unpaidReservations.items.first().boatSpaceId, "correct reservation is returned")

        assertEquals(1, paidReservations.totalRows, "reservations are filtered correctly")
        assertEquals(1, paidReservations.items.first().boatSpaceId, "correct reservation is returned")
    }

    @Test
    fun `should filter by name search`() {
        mockTimeProvider(timeProvider, startOfSlipReservationPeriod)
        testUtils.createReservationInConfirmedState(
            CreateReservationParams(
                timeProvider,
                this.citizenIdLeo,
                1,
                1
            )
        )
        testUtils.createReservationInConfirmedState(CreateReservationParams(timeProvider, citizenIdMikko, 3, 2))
        testUtils.createReservationInConfirmedState(CreateReservationParams(timeProvider, citizenIdOlivia, 4, 2))

        // Create a reservation for Olivia Virtanen in payment state
        testUtils.createReservationInPaymentState(timeProvider, reservationService, citizenIdOlivia, 2, 3)

        val reservationsByFirstName =
            employeeReservationListService.getBoatSpaceReservations(
                BoatSpaceReservationFilter(
                    nameSearch = "leo"
                )
            )

        assertEquals(1, reservationsByFirstName.totalRows, "reservations are filtered correctly")
        assertEquals("Korhonen Leo", reservationsByFirstName.items.first().name, "correct reservation is returned")

        val reservationsByLastName =
            employeeReservationListService.getBoatSpaceReservations(
                BoatSpaceReservationFilter(
                    nameSearch = "VIRTA",
                    payment = listOf(PaymentFilter.CONFIRMED)
                )
            )

        assertEquals(2, reservationsByLastName.totalRows, "reservations are filtered correctly")
        val reservationsNames = reservationsByLastName.items.map { "${it.name}" }
        assertContains(reservationsNames, "Virtanen Mikko")
        assertContains(reservationsNames, "Virtanen Olivia")
    }

    @Test
    fun `should filter reservations that have warnings`() {
        mockTimeProvider(timeProvider, startOfSlipReservationPeriod)
        testUtils.createReservationInConfirmedState(
            CreateReservationParams(
                timeProvider,
                this.citizenIdLeo,
                1,
                1
            )
        )
        testUtils.createReservationInPaymentState(timeProvider, reservationService, citizenIdOlivia, 2, 3)

        val madeReservation =
            testUtils.createReservationInPaymentState(
                timeProvider,
                reservationService,
                this.citizenIdLeo,
                3
            )

        formReservationService.processBoatSpaceReservation(
            this.citizenIdLeo,
            ReserveBoatSpaceInput(
                madeReservation.id,
                boatId = null,
                boatType = BoatType.Sailboat,
                width = BigDecimal(3.5),
                length = BigDecimal(6.5),
                depth = BigDecimal(3.0),
                weight = 180,
                boatRegistrationNumber = "JFK293",
                boatName = "Boat",
                otherIdentification = "1",
                extraInformation = "1",
                ownerShip = OwnershipStatus.FutureOwner,
                email = "email@email.com",
                phone = "0403849283"
            ),
            ReservationStatus.Confirmed,
            ReservationValidity.FixedTerm,
            timeProvider.getCurrentDate(),
            timeProvider.getCurrentDate()
        )

        val reservationsWithWarnings =
            employeeReservationListService.getBoatSpaceReservations(
                BoatSpaceReservationFilter(
                    warningFilter = true
                )
            )

        assertEquals(1, reservationsWithWarnings.totalRows, "reservations are filtered correctly")
        assertEquals(3, reservationsWithWarnings.items.first().boatSpaceId, "correct reservation is returned")
    }

    @Test
    fun `should filter by section`() {
        mockTimeProvider(timeProvider, startOfSlipReservationPeriod)
        val spaceInSectionB = 1
        val spaceInSectionD = 64
        val spaceInSectionE = 85
        testUtils.createReservationInConfirmedState(
            CreateReservationParams(
                timeProvider,
                this.citizenIdLeo,
                spaceInSectionB,
                1
            )
        )
        testUtils.createReservationInConfirmedState(
            CreateReservationParams(
                timeProvider,
                this.citizenIdLeo,
                spaceInSectionD,
                2
            )
        )
        testUtils.createReservationInConfirmedState(
            CreateReservationParams(
                timeProvider,
                this.citizenIdLeo,
                spaceInSectionE,
                3
            )
        )

        val reservationsBySection =
            employeeReservationListService.getBoatSpaceReservations(
                BoatSpaceReservationFilter(
                    sectionFilter = listOf("B", "D")
                )
            )
        val sections = reservationsBySection.items.map { it.section }
        assertEquals(2, reservationsBySection.totalRows, "reservations are filtered correctly")
        assertContains(sections, "B")
        assertContains(sections, "D")
    }

    @Test
    fun `should sort reservations correctly`() {
        mockTimeProvider(timeProvider, startOfSlipReservationPeriod)
        testUtils.createReservationInConfirmedState(
            CreateReservationParams(
                timeProvider,
                this.citizenIdLeo,
                1,
                1
            )
        )
        testUtils.createReservationInConfirmedState(
            CreateReservationParams(
                timeProvider,
                UUID.fromString("62d90eed-4ea3-4446-8023-8dad9c01dd34"),
                177,
                2
            )
        )
        testUtils.createReservationInInvoiceState(timeProvider, reservationService, invoiceService, citizenIdOlivia, 2, 3)

        val reservations =
            employeeReservationListService.getBoatSpaceReservations(
                BoatSpaceReservationFilter(
                    sortBy = BoatSpaceReservationFilterColumn.PLACE,
                    ascending = true,
                )
            )

        assertEquals(3, reservations.totalRows, "reservations are filtered correctly")
        assertEquals(listOf(1, 2, 177), reservations.items.map { it.boatSpaceId }, "reservations are sorted by place and amenity")
    }

    @Test
    fun `should return boat space related to reservation`() {
        mockTimeProvider(timeProvider, startOfSlipReservationPeriod)
        val boatSpaceId = 1
        val newReservation =
            reservationService.insertBoatSpaceReservation(
                this.citizenIdLeo,
                this.citizenIdLeo,
                boatSpaceId,
                CreationType.New,
                startDate = timeProvider.getCurrentDate(),
                endDate = timeProvider.getCurrentDate(),
                validity = ReservationValidity.FixedTerm,
            )
        val boatSpace = reservationService.getBoatSpaceRelatedToReservation(newReservation.id)
        assertEquals(boatSpaceId, boatSpace?.id, "Correct boat space is fetched")
    }

    @Test
    fun `should return expired and cancelled reservations`() {
        mockTimeProvider(timeProvider, startOfSlipReservationPeriod)
        val reservationExpired =
            testUtils.createReservationInConfirmedState(
                CreateReservationParams(
                    timeProvider,
                    this.citizenIdLeo,
                    1,
                    1,
                )
            )

        val reservationTerminated =
            testUtils.createReservationInConfirmedState(
                CreateReservationParams(
                    timeProvider,
                    this.citizenIdLeo,
                    2,
                    2
                )
            )

        val noExpiredReservations = reservationService.getExpiredBoatSpaceReservationsForReserver(this.citizenIdLeo)
        assertEquals(0, noExpiredReservations.size)

        reservationService.markReservationEnded(reservationExpired.id)
        terminateReservationService.terminateBoatSpaceReservationAsOwner(
            reservationTerminated.id,
            this.citizenIdLeo
        )

        val expiredReservations = reservationService.getExpiredBoatSpaceReservationsForReserver(this.citizenIdLeo)
        assertEquals(2, expiredReservations.size)
        assertEquals(
            ReservationStatus.Confirmed,
            expiredReservations.find { it.id == reservationExpired.id }?.status,
            "Reservation is still in Confirmed state"
        )
        assertEquals(
            timeProvider.getCurrentDate().minusDays(1),
            expiredReservations
                .find {
                    it.id == reservationExpired.id
                }?.endDate,
            "End date is set to yesterday"
        )

        assertEquals(
            ReservationStatus.Cancelled,
            expiredReservations
                .find {
                    it.id == reservationTerminated.id
                }?.status,
            "Reservation is marked as Cancelled"
        )
        assertEquals(
            timeProvider.getCurrentDate(),
            expiredReservations
                .find {
                    it.id == reservationTerminated.id
                }?.endDate,
            "End date is set to now"
        )
    }

    @Test
    fun `should return reservations within time`() {
        mockTimeProvider(timeProvider, startOfSlipReservationPeriod)
        val reservation1 =
            testUtils.createReservationInConfirmedState(
                CreateReservationParams(
                    timeProvider,
                    this.citizenIdLeo,
                    1,
                    1,
                    startDate = timeProvider.getCurrentDate().minusDays(10),
                    endDate = timeProvider.getCurrentDate().plusDays(10),
                )
            )
        val reservation2 =
            testUtils.createReservationInConfirmedState(
                CreateReservationParams(
                    timeProvider,
                    this.citizenIdLeo,
                    2,
                    2,
                    startDate = timeProvider.getCurrentDate().minusDays(5),
                    endDate = timeProvider.getCurrentDate().plusDays(5),
                )
            )
        val reservation3 =
            testUtils.createReservationInConfirmedState(
                CreateReservationParams(
                    timeProvider,
                    this.citizenIdLeo,
                    3,
                    3,
                    startDate = timeProvider.getCurrentDate().minusDays(5),
                    endDate = timeProvider.getCurrentDate().plusDays(10),
                )
            )
        var params =
            BoatSpaceReservationFilter(
                reservationValidFrom = timeProvider.getCurrentDate().minusDays(6),
                dateFilter = true,
            )
        var reservations = employeeReservationListService.getBoatSpaceReservations(params).items
        assertEquals(2, reservations.size, "Reservations that are valid within dates is returned")
        assertEquals(reservation2.id, reservations[0].id, "Reservation that is valid from date is returned")
        assertEquals(reservation3.id, reservations[1].id, "Reservation that is valid from date is returned")

        params =
            BoatSpaceReservationFilter(
                reservationValidFrom = timeProvider.getCurrentDate().minusDays(6),
                reservationValidUntil = timeProvider.getCurrentDate().plusDays(6),
                dateFilter = true,
            )
        reservations = employeeReservationListService.getBoatSpaceReservations(params).items
        assertEquals(1, reservations.size, "Reservation that is valid within dates is returned")
        assertEquals(reservation2.id, reservations[0].id, "Reservation that is valid within dates is returned")
    }

    @Test
    fun `should fetch all harbors`() {
        val harbors = reservationService.getHarbors()
        assertEquals(8, harbors.size, "Correct number of harbors are fetched")
        assertEquals("Mellstenintie 6, 02170 Espoo", harbors[0].address, "Correct address for first harbor")
        assertEquals("Haukilahti", harbors[0].name, "Correct name for first harbor")
    }
}
