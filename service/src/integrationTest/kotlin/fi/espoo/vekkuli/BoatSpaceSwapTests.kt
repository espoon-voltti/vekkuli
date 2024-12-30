package fi.espoo.vekkuli

import fi.espoo.vekkuli.boatSpace.boatSpaceSwitch.BoatSpaceSwitchService
import fi.espoo.vekkuli.boatSpace.seasonalService.SeasonalService
import fi.espoo.vekkuli.common.Forbidden
import fi.espoo.vekkuli.domain.ReservationStatus
import fi.espoo.vekkuli.domain.ReservationValidity
import fi.espoo.vekkuli.service.*
import fi.espoo.vekkuli.utils.startOfSlipRenewPeriod
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
import java.util.*

@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class BoatSpaceSwapTests : IntegrationTestBase() {
    @BeforeEach
    override fun resetDatabase() {
        deleteAllReservations(jdbi)
    }

    @MockBean
    private lateinit var seasonalService: SeasonalService

    @Autowired
    private lateinit var boatSpaceSwitchService: BoatSpaceSwitchService

    @Autowired
    lateinit var boatService: BoatService

    @Autowired
    lateinit var reservationService: BoatReservationService

    @Test
    fun `should swap the reservation to a new reservation for the reserver`() {
        val reservation =
            testUtils.createReservationInConfirmedState(
                CreateReservationParams(
                    timeProvider,
                    citizenIdOlivia,
                    1,
                    validity = ReservationValidity.Indefinite,
                    startDate = timeProvider.getCurrentDate(),
                    endDate = timeProvider.getCurrentDate().plusYears(1)
                )
            )

        Mockito.`when`(seasonalService.canSwitchReservation(any(), any(), any(), any(), any())).thenReturn(
            ReservationResult.Success(
                ReservationResultSuccess(
                    reservation.startDate,
                    reservation.endDate,
                    ReservationValidity.Indefinite,
                )
            )
        )

        val newReservation =
            testUtils.createReservationInInfoState(
                citizenIdOlivia
            )
        boatSpaceSwitchService.switchBoatSpaceReservationAsEmployee(userId, citizenIdOlivia, reservation.id, newReservation.id)
        val oldReservation = reservationService.getReservationWithDependencies(reservation.id)
        assertEquals(ReservationStatus.Cancelled, oldReservation?.status, "Old reservation should be cancelled")
        assertEquals(
            ReservationStatus.Confirmed,
            reservationService.getReservationWithDependencies(newReservation.id)?.status,
            "Reservation should have been switched to new reservation"
        )
    }

    @Test
    fun `should not be able to switch reservation if reserver is not found`() {
        val reservation =
            testUtils.createReservationInConfirmedState(
                CreateReservationParams(
                    timeProvider,
                    citizenIdOlivia,
                    1,
                    validity = ReservationValidity.Indefinite,
                    startDate = startOfSlipRenewPeriod.minusYears(1).toLocalDate(),
                    endDate = startOfSlipRenewPeriod.plusDays(1).toLocalDate()
                )
            )

        val newReservation =
            testUtils.createReservationInInfoState(
                citizenIdOlivia
            )

        assertThrows<IllegalArgumentException> {
            boatSpaceSwitchService.switchBoatSpaceReservationAsCitizen(UUID.randomUUID(), reservation.id, newReservation.id)
        }
    }

    @Test
    fun `should not be able to switch reservation if reservation is not found`() {
        val newReservation =
            testUtils.createReservationInInfoState(
                citizenIdOlivia
            )

        assertThrows<IllegalArgumentException> {
            boatSpaceSwitchService.switchBoatSpaceReservationAsCitizen(citizenIdOlivia, 1, newReservation.id)
        }
    }

    @Test
    fun `should not be able to switch reservation as a citizen if season is not active`() {
        val reservation =
            testUtils.createReservationInConfirmedState(
                CreateReservationParams(
                    timeProvider,
                    citizenIdOlivia,
                    1,
                    validity = ReservationValidity.Indefinite,
                    startDate = startOfSlipRenewPeriod.minusYears(1).toLocalDate(),
                    endDate = startOfSlipRenewPeriod.plusDays(1).toLocalDate()
                )
            )

        val newReservation =
            testUtils.createReservationInInfoState(
                citizenIdOlivia
            )

        Mockito.`when`(seasonalService.canSwitchReservation(any(), any(), any(), any(), any())).thenReturn(
            ReservationResult.Failure(
                ReservationResultErrorCode.NotPossible
            )
        )
        assertThrows<Forbidden> {
            boatSpaceSwitchService.switchBoatSpaceReservationAsCitizen(citizenIdOlivia, reservation.id, newReservation.id)
        }
    }
}
