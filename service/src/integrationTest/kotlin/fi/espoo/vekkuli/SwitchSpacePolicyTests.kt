package fi.espoo.vekkuli

import fi.espoo.vekkuli.boatSpace.boatSpaceSwitch.BoatSpaceSwitchService
import fi.espoo.vekkuli.boatSpace.boatSpaceSwitch.SwitchPolicyService
import fi.espoo.vekkuli.domain.BoatSpaceType
import fi.espoo.vekkuli.domain.ReservationOperation
import fi.espoo.vekkuli.service.ReservationResult
import fi.espoo.vekkuli.service.ReservationResultSuccess
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class SwitchSpacePolicyTests : IntegrationTestBase() {
    @Autowired
    lateinit var boatSpaceSwitchService: BoatSpaceSwitchService

    @Autowired
    lateinit var switchPolicyService: SwitchPolicyService

    @BeforeEach
    override fun resetDatabase() {
        deleteAllReservations(jdbi)
        testUtils.moveTimeToReservationPeriodStart(BoatSpaceType.Slip, ReservationOperation.Change)
    }

    @Test
    fun `should check whether boat space can be switched`() {
        val originalSpaceId = boatSpaceIdForSlip
        val targetSpaceId = boatSpaceIdForSlip2
        val reserver = espooCitizenWithoutReservationsId

        val originalReservation =
            testUtils.createReservationInConfirmedState(
                CreateReservationParams(
                    timeProvider,
                    reserver,
                    originalSpaceId
                )
            )
        val canSwitch = switchPolicyService.citizenCanSwitchToReservation(originalReservation.id, reserver, targetSpaceId)
        assertEquals(true, canSwitch.success, "Boat space can be switched")
        assertEquals(
            ReservationResult.Success(
                ReservationResultSuccess(originalReservation.startDate, originalReservation.endDate, originalReservation.validity)
            ),
            canSwitch,
            "ReservationResultSuccess is returned"
        )
    }

    @Test
    fun `should fail if space type is not the same`() {
        val originalSpaceId = boatSpaceIdForWinter
        val targetSpaceId = boatSpaceIdForSlip
        testUtils.moveTimeToReservationPeriodStart(BoatSpaceType.Winter, ReservationOperation.Change)
        // create a second reservation for the same boat space
        val originalReservation =
            testUtils.createReservationInConfirmedState(
                CreateReservationParams(
                    timeProvider,
                    espooCitizenWithoutReservationsId,
                    originalSpaceId
                )
            )
        val canSwitch = switchPolicyService.citizenCanSwitchToReservation(originalReservation.id, citizenIdLeo, targetSpaceId)
        assertEquals(false, canSwitch.success, "Boat space cannot be switched")
    }

    @Test
    fun `should not be able to create a switch reservation if boat space is already reserved`() {
        val originalSpaceId = boatSpaceIdForSlip
        val targetSpaceId = boatSpaceIdForSlip2
        val reserver = espooCitizenWithoutReservationsId

        testUtils.createReservationInConfirmedState(
            CreateReservationParams(
                timeProvider,
                nonEspooCitizenWithoutReservationsId,
                targetSpaceId
            )
        )
        val originalReservation =
            testUtils.createReservationInConfirmedState(
                CreateReservationParams(
                    timeProvider,
                    reserver,
                    originalSpaceId
                )
            )

        assertEquals(
            false,
            switchPolicyService.citizenCanSwitchToReservation(originalReservation.id, reserver, targetSpaceId).success,
            "Citizen can not switch reservation if the space is already reserved"
        )
    }

    @Test
    fun `allow switching winter reservations`() {
        val originalSpaceId = boatSpaceIdForWinter
        val targetSpaceId = boatSpaceIdForWinter2
        val reserver = espooCitizenWithoutReservationsId

        testUtils.moveTimeToReservationPeriodStart(BoatSpaceType.Winter, ReservationOperation.Change)
        val originalReservation =
            testUtils.createReservationInConfirmedState(
                CreateReservationParams(
                    timeProvider,
                    reserver,
                    originalSpaceId
                )
            )
        assertEquals(
            true,
            switchPolicyService.citizenCanSwitchToReservation(originalReservation.id, reserver, targetSpaceId).success,
            "Allow switching winter reservations to another"
        )
    }

    @Test
    fun `should not be able switch slip reservation with winter reservation`() {
        val originalSpaceId = boatSpaceIdForSlip
        val targetSpaceId = boatSpaceIdForWinter2
        val reserver = espooCitizenWithoutReservationsId

        val originalReservation =
            testUtils.createReservationInConfirmedState(
                CreateReservationParams(
                    timeProvider,
                    reserver,
                    originalSpaceId
                )
            )

        testUtils.moveTimeToReservationPeriodStart(BoatSpaceType.Winter, ReservationOperation.Change)
        assertEquals(
            false,
            switchPolicyService.citizenCanSwitchToReservation(originalReservation.id, reserver, targetSpaceId).success,
            "Citizen can not switch reservation"
        )
    }
}
