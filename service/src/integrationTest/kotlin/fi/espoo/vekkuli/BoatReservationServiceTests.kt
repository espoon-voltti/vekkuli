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
class BoatReservationServiceTests : IntegrationTestBase() {
    @Autowired
    lateinit var reservationService: BoatReservationService

    @BeforeEach
    override fun resetDatabase() {
        deleteAllReservations(jdbi)
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
    fun `should fetch all harbors`() {
        val harbors = reservationService.getHarbors()
        Assertions.assertEquals(8, harbors.size, "Correct number of harbors are fetched")
        Assertions.assertEquals("Mellstenintie 6, 02170 Espoo", harbors[0].address, "Correct address for first harbor")
        Assertions.assertEquals("Haukilahti", harbors[0].name, "Correct name for first harbor")
    }

}
