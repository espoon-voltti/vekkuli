package fi.espoo.vekkuli

import fi.espoo.vekkuli.boatSpace.employeeReservationList.EmployeeReservationListService
import fi.espoo.vekkuli.domain.*
import fi.espoo.vekkuli.domain.ReservationWarningType
import fi.espoo.vekkuli.service.*
import fi.espoo.vekkuli.utils.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.util.*
import kotlin.collections.listOf

@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class EmployeeReservationListIntegrationTests : IntegrationTestBase() {
    @Autowired
    lateinit var employeeReservationListService: EmployeeReservationListService

    @Autowired
    lateinit var reservationWarningRepository: ReservationWarningRepository

    @BeforeEach
    override fun resetDatabase() {
        deleteAllReservations(jdbi)
    }

    @Test
    fun `should sort the reservations by most recent warnings first when warning filter is chosen`() {
        mockTimeProvider(timeProvider, startOfSlipReservationPeriod)
        val reservation1 =
            testUtils.createReservationInConfirmedState(
                CreateReservationParams(
                    timeProvider,
                    this.citizenIdLeo,
                    1,
                    1
                )
            )
        val reservation2 =
            testUtils.createReservationInConfirmedState(
                CreateReservationParams(
                    timeProvider,
                    this.citizenIdOlivia,
                    2,
                    3
                )
            )

        testUtils.createReservationInConfirmedState(
            CreateReservationParams(
                timeProvider,
                this.citizenIdLeo,
                3,
                4
            )
        )

        reservationWarningRepository.addReservationWarnings(
            listOf(
                ReservationWarning(
                    UUID.randomUUID(),
                    reservation1.id,
                    1,
                    trailerId = null,
                    invoiceNumber = null,
                    key = ReservationWarningType.BoatType,
                    infoText = null,
                )
            )
        )

        reservationWarningRepository.addReservationWarnings(
            listOf(
                ReservationWarning(
                    UUID.randomUUID(),
                    reservation2.id,
                    1,
                    trailerId = null,
                    invoiceNumber = null,
                    key = ReservationWarningType.BoatType,
                    infoText = null,
                )
            )
        )

        val reservationsWithWarnings =
            employeeReservationListService.getBoatSpaceReservations(
                BoatSpaceReservationFilter(
                    sortBy = BoatSpaceReservationFilterColumn.CUSTOMER,
                    ascending = true,
                    warningFilter = true
                )
            )

        assertEquals(2, reservationsWithWarnings.totalRows, "reservations are filtered correctly")
        assertEquals(
            reservation2.id,
            reservationsWithWarnings.items.first().id,
            "reservation with most recent warning is returned first"
        )
        assertEquals(
            reservation1.id,
            reservationsWithWarnings.items[1].id,
            "reservation with older warning is returned second"
        )
    }
}
