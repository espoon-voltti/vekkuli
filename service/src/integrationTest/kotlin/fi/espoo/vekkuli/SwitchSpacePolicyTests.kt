package fi.espoo.vekkuli

import fi.espoo.vekkuli.boatSpace.boatSpaceSwitch.BoatSpaceSwitchService
import fi.espoo.vekkuli.boatSpace.boatSpaceSwitch.SwitchPolicyService
import fi.espoo.vekkuli.config.BoatSpaceConfig.getSlipEndDate
import fi.espoo.vekkuli.config.BoatSpaceConfig.getWinterEndDate
import fi.espoo.vekkuli.domain.BoatSpaceType
import fi.espoo.vekkuli.domain.ReservationOperation
import fi.espoo.vekkuli.domain.ReservationValidity
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
                ReservationResultSuccess(
                    originalReservation.startDate.toLocalDate(),
                    originalReservation.endDate.toLocalDate(),
                    originalReservation.validity
                )
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

    @Test
    fun `should be able to switch slip reservation to slip space as Espoo citizen within switch time limits`() {
        val reserverId = espooCitizenWithoutReservationsId
        val validity = ReservationValidity.Indefinite
        val boatSpaceType = BoatSpaceType.Slip
        val targetSpaceId = boatSpaceIdForSlip2

        // Start at the start of reservation period
        testUtils.moveTimeToReservationPeriodStart(boatSpaceType, ReservationOperation.New)
        val endDate = getSlipEndDate(timeProvider.getCurrentDate(), validity)

        val reservation =
            testUtils.createReservationInConfirmedState(
                CreateReservationParams(
                    timeProvider,
                    reserverId,
                    boatSpaceId = boatSpaceIdForSlip,
                    validity = validity,
                    endDate = endDate
                )
            )

        // Move time before the start of the switch period
        testUtils.moveTimeToReservationPeriodStart(boatSpaceType, ReservationOperation.Change, addDays = -1)

        assertEquals(
            false,
            switchPolicyService.citizenCanSwitchToReservation(reservation.id, reserverId, targetSpaceId).success,
            "Reservation can't be switched before the start of the season"
        )

        // Move to the start of switch period
        testUtils.moveTimeToReservationPeriodStart(boatSpaceType, ReservationOperation.Change)
        assertEquals(
            true,
            switchPolicyService.citizenCanSwitchToReservation(reservation.id, reserverId, targetSpaceId).success,
            "Reservation can be switched at the start of the season"
        )

        // Move to the end of switch period
        testUtils.moveTimeToReservationPeriodEnd(boatSpaceType, ReservationOperation.Change)
        assertEquals(
            true,
            switchPolicyService.citizenCanSwitchToReservation(reservation.id, reserverId, targetSpaceId).success,
            "Reservation can be switched at the end of the season"
        )

        // Move time after the end of switch period
        testUtils.moveTimeToReservationPeriodEnd(boatSpaceType, ReservationOperation.Change, addDays = 1)
        assertEquals(
            false,
            switchPolicyService.citizenCanSwitchToReservation(reservation.id, reserverId, targetSpaceId).success,
            "Reservation can't be switched after the switch period"
        )
    }

    @Test
    fun `should be able to switch winter reservation to winter space as Espoo citizen within switch time limits`() {
        val reserverId = espooCitizenWithoutReservationsId
        val validity = ReservationValidity.Indefinite
        val boatSpaceType = BoatSpaceType.Winter
        val originalSpaceId = boatSpaceIdForWinter
        val targetSpaceId = boatSpaceIdForWinter2

        // Start at the start of reservation period
        testUtils.moveTimeToReservationPeriodStart(boatSpaceType, ReservationOperation.New)

        val endDate = getWinterEndDate(timeProvider.getCurrentDate())

        val reservation =
            testUtils.createReservationInConfirmedState(
                CreateReservationParams(
                    timeProvider,
                    reserverId,
                    boatSpaceId = originalSpaceId,
                    validity = validity,
                    endDate = endDate
                )
            )

        // Move time before the start of the switch period
        testUtils.moveTimeToReservationPeriodStart(boatSpaceType, ReservationOperation.Change, addDays = -1)

        assertEquals(
            false,
            switchPolicyService.citizenCanSwitchToReservation(reservation.id, reserverId, targetSpaceId).success,
            "Reservation can't be switched before the start of the season"
        )

        // Move to the start of switch period
        testUtils.moveTimeToReservationPeriodStart(boatSpaceType, ReservationOperation.Change)
        assertEquals(
            true,
            switchPolicyService.citizenCanSwitchToReservation(reservation.id, reserverId, targetSpaceId).success,
            "Reservation can be switched at the start of the season"
        )

        // Move to the end of switch period
        testUtils.moveTimeToReservationPeriodEnd(boatSpaceType, ReservationOperation.Change)
        assertEquals(
            true,
            switchPolicyService.citizenCanSwitchToReservation(reservation.id, reserverId, targetSpaceId).success,
            "Reservation can be switched at the end of the season"
        )

        // Move time after the end of switch period
        testUtils.moveTimeToReservationPeriodEnd(boatSpaceType, ReservationOperation.Change, addDays = 1)
        assertEquals(
            false,
            switchPolicyService.citizenCanSwitchToReservation(reservation.id, reserverId, targetSpaceId).success,
            "Reservation can't be switched after the switch period"
        )
    }

    @Test
    fun `should check whether slip reservation is switchable in the change period`() {
        val reserverId = espooCitizenWithoutReservationsId
        val validity = ReservationValidity.Indefinite
        val boatSpaceType = BoatSpaceType.Slip
        val originalSpaceId = boatSpaceIdForSlip

        // Start at the start of reservation period
        testUtils.moveTimeToReservationPeriodStart(boatSpaceType, ReservationOperation.New)

        val endDate = getSlipEndDate(timeProvider.getCurrentDate(), validity)

        val reservation =
            testUtils.createReservationInConfirmedState(
                CreateReservationParams(
                    timeProvider,
                    reserverId,
                    boatSpaceId = originalSpaceId,
                    validity = validity,
                    endDate = endDate
                )
            )

        // Move time before the start of the switch period
        testUtils.moveTimeToReservationPeriodStart(boatSpaceType, ReservationOperation.Change, addDays = -1)

        assertEquals(
            false,
            switchPolicyService.citizenCanSwitchReservation(reservation.id, reserverId).success,
            "Reservation can't be switched before the start of the season"
        )

        // Move to the start of switch period
        testUtils.moveTimeToReservationPeriodStart(boatSpaceType, ReservationOperation.Change)
        assertEquals(
            true,
            switchPolicyService.citizenCanSwitchReservation(reservation.id, reserverId).success,
            "Reservation can be switched at the start of the season"
        )

        // Move to the end of switch period
        testUtils.moveTimeToReservationPeriodEnd(boatSpaceType, ReservationOperation.Change)
        assertEquals(
            true,
            switchPolicyService.citizenCanSwitchReservation(reservation.id, reserverId).success,
            "Reservation can be switched at the end of the season"
        )

        // Move time after the end of switch period
        testUtils.moveTimeToReservationPeriodEnd(boatSpaceType, ReservationOperation.Change, addDays = 1)
        assertEquals(
            false,
            switchPolicyService.citizenCanSwitchReservation(reservation.id, reserverId).success,
            "Reservation can't be switched after the switch period"
        )
    }

    @Test
    fun `should check whether reservation is still active when it can be changed`() {
        val reserverId = espooCitizenWithoutReservationsId
        val validity = ReservationValidity.Indefinite
        val boatSpaceType = BoatSpaceType.Slip
        val originalSpaceId = boatSpaceIdForSlip

        // Start at the start of reservation period
        testUtils.moveTimeToReservationPeriodStart(boatSpaceType, ReservationOperation.New)

        val endDate = getSlipEndDate(timeProvider.getCurrentDate(), validity)

        val reservation =
            testUtils.createReservationInConfirmedState(
                CreateReservationParams(
                    timeProvider,
                    reserverId,
                    boatSpaceId = originalSpaceId,
                    validity = validity,
                    endDate = endDate
                )
            )

        // Move to the start of switch period
        testUtils.moveTimeToReservationPeriodStart(boatSpaceType, ReservationOperation.Change)
        assertEquals(
            true,
            switchPolicyService.citizenCanSwitchReservation(reservation.id, reserverId).success,
            "Reservation can be switched at the start of the current season"
        )

        // Move to the start of switch period of next year
        testUtils.moveTimeToReservationPeriodStart(boatSpaceType, ReservationOperation.Change, addDays = 365)
        assertEquals(
            false,
            switchPolicyService.citizenCanSwitchReservation(reservation.id, reserverId).success,
            "Expired reservation can't be switched"
        )
    }
}
