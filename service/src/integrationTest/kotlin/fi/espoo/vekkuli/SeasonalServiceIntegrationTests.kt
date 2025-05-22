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
import org.junit.jupiter.api.Assertions
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
    lateinit var formReservationService: ReservationFormService

    @Autowired
    lateinit var reservationService: BoatReservationService

    val espooCitizenId = citizenIdOlivia
    val helsinkiCitizenId = citizenIdMarko
    val espooRulesAppliedNonEspooCitizenId = citizenIdJorma
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
}
