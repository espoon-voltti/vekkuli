package fi.espoo.vekkuli

import fi.espoo.vekkuli.boatSpace.renewal.RenewalPolicyService
import fi.espoo.vekkuli.config.BoatSpaceConfig.getSlipEndDate
import fi.espoo.vekkuli.config.BoatSpaceConfig.getTrailerEndDate
import fi.espoo.vekkuli.config.BoatSpaceConfig.getWinterEndDate
import fi.espoo.vekkuli.domain.BoatSpaceType
import fi.espoo.vekkuli.domain.ReservationOperation
import fi.espoo.vekkuli.domain.ReservationValidity
import fi.espoo.vekkuli.service.BoatReservationService
import fi.espoo.vekkuli.service.ReservationResult
import fi.espoo.vekkuli.utils.mockTimeProvider
import fi.espoo.vekkuli.utils.startOfSlipRenewPeriod
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
class RenewReservationPolicyTests : IntegrationTestBase() {
    @BeforeEach
    override fun resetDatabase() {
        deleteAllReservations(jdbi)
        mockTimeProvider(timeProvider, startOfSlipRenewPeriod)
    }

    @Autowired
    lateinit var reservationService: BoatReservationService

    @Autowired
    lateinit var renewalPolicyService: RenewalPolicyService

    @Test
    fun `should be able to renew expiring indefinite slip reservation as Espoo citizen within renewal time limits`() {
        val reserverId = espooCitizenWithoutReservationsId

        // Start at the start of reservation period
        testUtils.moveTimeToReservationPeriodStart(BoatSpaceType.Slip, ReservationOperation.New)
        val reservation = testUtils.createReservationInConfirmedState(
            CreateReservationParams(
                timeProvider,
                reserverId,
                validity = ReservationValidity.Indefinite,
                endDate = getSlipEndDate(timeProvider.getCurrentDate(), ReservationValidity.Indefinite)
            )
        )

        // Move time before the start of the renewal period
        testUtils.moveTimeToReservationPeriodStart(BoatSpaceType.Slip, ReservationOperation.Renew, addDays = -1)

        assertEquals(
            false,
            renewalPolicyService.citizenCanRenewReservation(reservation.id, reserverId).success,
            "Reservation can't be renewed at the start of the season"
        )

        // Move to the start of renewal period
        testUtils.moveTimeToReservationPeriodStart(BoatSpaceType.Slip, ReservationOperation.Renew)
        assertEquals(
            true,
            renewalPolicyService.citizenCanRenewReservation(reservation.id, reserverId).success,
            "Reservation can be renewed at the start of the season"
        )

        // Move to the end of renewal period
        testUtils.moveTimeToReservationPeriodEnd(BoatSpaceType.Slip, ReservationOperation.Renew, addDays = 1)
        assertEquals(
            false,
            renewalPolicyService.citizenCanRenewReservation(reservation.id, reserverId).success,
            "Reservation can't be renewed after the renewal period"
        )

    }

    @Test
    fun `should not be able to renew expiring indefinite term slip reservation as non-Espoo citizen`() {
        val reserverId = nonEspooCitizenWithoutReservationsId
        // Start at the start of reservation period
        testUtils.moveTimeToReservationPeriodStart(BoatSpaceType.Slip, ReservationOperation.New, isEspooCitizen = false)

        val reservation = testUtils.createReservationInConfirmedState(
            CreateReservationParams(
                timeProvider,
                reserverId,
                validity = ReservationValidity.Indefinite,
                endDate = getSlipEndDate(timeProvider.getCurrentDate(), ReservationValidity.Indefinite)
            )
        )

        // Move to the start of renewal period (for espoo citizen)
        testUtils.moveTimeToReservationPeriodStart(BoatSpaceType.Slip, ReservationOperation.Renew)

        assertEquals(false, renewalPolicyService.citizenCanRenewReservation(reservation.id, reserverId).success, "Reservation can't be renewed")
    }

    @Test
    fun `should not be able to renew expiring fixed term slip reservation as Espoo citizen`() {
        val reserverId = espooCitizenWithoutReservationsId
        // Start at the start of reservation period
        testUtils.moveTimeToReservationPeriodStart(BoatSpaceType.Slip, ReservationOperation.New)

        val reservation = testUtils.createReservationInConfirmedState(
            CreateReservationParams(
                timeProvider,
                reserverId,
                validity = ReservationValidity.FixedTerm,
                endDate = getSlipEndDate(timeProvider.getCurrentDate(), ReservationValidity.FixedTerm)
            )
        )

        // Move to the start of renewal period
        testUtils.moveTimeToReservationPeriodStart(BoatSpaceType.Slip, ReservationOperation.Renew)

        assertEquals(false, renewalPolicyService.citizenCanRenewReservation(reservation.id, reserverId).success, "Reservation can't be renewed")
    }

    @Test
    fun `should be able to renew expiring indefinite slip reservation as Employee at any point in time`() {
        // Start at the start of reservation period
        testUtils.moveTimeToReservationPeriodStart(BoatSpaceType.Slip, ReservationOperation.New)

        val reserverId = espooCitizenWithoutReservationsId
        val validity = ReservationValidity.Indefinite
        val endDate = getSlipEndDate(timeProvider.getCurrentDate(), validity)

        val reservation = testUtils.createReservationInConfirmedState(
            CreateReservationParams(
                timeProvider,
                reserverId,
                validity = validity,
                endDate = endDate
            )
        )

        // Move time before the start of the renewal period
        testUtils.moveTimeToReservationPeriodStart(BoatSpaceType.Slip, ReservationOperation.Renew, addDays = -1)

        val expectedRenewalEndDate = getSlipEndDate(timeProvider.getCurrentDate().plusYears(1), validity)
        val beforePeriodResult = renewalPolicyService.employeeCanRenewReservation(reservation.id)

        assertEquals(
            true,
            beforePeriodResult.success,
            "Employee can renew reservation before the start of the season"
        )

        assertEquals(
            timeProvider.getCurrentDate(),
            (beforePeriodResult as? ReservationResult.Success)?.data?.startDate,
            "Renewed reservation should start from the current date"
        )

        assertEquals(
            expectedRenewalEndDate,
            (beforePeriodResult as? ReservationResult.Success)?.data?.endDate,
            "Renewed reservation should end at the end of the next season"
        )

        // Move to the start of renewal period
        testUtils.moveTimeToReservationPeriodStart(BoatSpaceType.Slip, ReservationOperation.Renew)
        assertEquals(
            true,
            renewalPolicyService.employeeCanRenewReservation(reservation.id).success,
            "Employee can renew reservation at the start of the season"
        )

        // Move after the end of renewal period
        testUtils.moveTimeToReservationPeriodEnd(BoatSpaceType.Slip, ReservationOperation.Renew, addDays = 1)
        assertEquals(
            false,
            renewalPolicyService.employeeCanRenewReservation(reservation.id).success,
            "Even Employee can't renew reservation after the reservation has expired"
        )
    }

    @Test
    fun `should be able to renew expiring indefinite winter reservation as Espoo citizen within renewal time limits`() {
        val reserverId = espooCitizenWithoutReservationsId
        val endDate = getWinterEndDate(timeProvider.getCurrentDate())
        val boatSpaceType = BoatSpaceType.Winter

        // Start at the start of reservation period
        testUtils.moveTimeToReservationPeriodStart(boatSpaceType, ReservationOperation.New)
        val reservation = testUtils.createReservationInConfirmedState(
            CreateReservationParams(
                timeProvider,
                reserverId,
                boatSpaceId = boatSpaceIdForWinter,
                validity = ReservationValidity.Indefinite,
                endDate = endDate
            )
        )

        // Move time before the start of the renewal period
        testUtils.moveTimeToReservationPeriodStart(boatSpaceType, ReservationOperation.Renew, addDays = -1)

        assertEquals(
            false,
            renewalPolicyService.citizenCanRenewReservation(reservation.id, reserverId).success,
            "Reservation can't be renewed at the start of the season"
        )

        // Move to the start of renewal period
        testUtils.moveTimeToReservationPeriodStart(boatSpaceType, ReservationOperation.Renew)
        assertEquals(
            true,
            renewalPolicyService.citizenCanRenewReservation(reservation.id, reserverId).success,
            "Reservation can be renewed at the start of the season"
        )

        // Move to the end of renewal period
        testUtils.moveTimeToReservationPeriodEnd(boatSpaceType, ReservationOperation.Renew, addDays = 1)
        assertEquals(
            false,
            renewalPolicyService.citizenCanRenewReservation(reservation.id, reserverId).success,
            "Reservation can't be renewed after the renewal period"
        )

    }

    @Test
    fun `should be able to renew expiring indefinite storage reservation as Espoo citizen within renewal time limits`() {
        val reserverId = espooCitizenWithoutReservationsId
        val endDate = getWinterEndDate(timeProvider.getCurrentDate())
        val boatSpaceType = BoatSpaceType.Storage

        // Start at the start of reservation period
        testUtils.moveTimeToReservationPeriodStart(boatSpaceType, ReservationOperation.New)
        val reservation = testUtils.createReservationInConfirmedState(
            CreateReservationParams(
                timeProvider,
                reserverId,
                boatSpaceId = boatSpaceIdForStorage,
                validity = ReservationValidity.Indefinite,
                endDate = endDate
            )
        )

        // Move time before the start of the renewal period
        testUtils.moveTimeToReservationPeriodStart(boatSpaceType, ReservationOperation.Renew, addDays = -1)

        assertEquals(
            false,
            renewalPolicyService.citizenCanRenewReservation(reservation.id, reserverId).success,
            "Reservation can't be renewed at the start of the season"
        )

        // Move to the start of renewal period
        testUtils.moveTimeToReservationPeriodStart(boatSpaceType, ReservationOperation.Renew)
        assertEquals(
            true,
            renewalPolicyService.citizenCanRenewReservation(reservation.id, reserverId).success,
            "Reservation can be renewed at the start of the season"
        )

        // Move to the end of renewal period
        testUtils.moveTimeToReservationPeriodEnd(boatSpaceType, ReservationOperation.Renew, addDays = 1)
        assertEquals(
            false,
            renewalPolicyService.citizenCanRenewReservation(reservation.id, reserverId).success,
            "Reservation can't be renewed after the renewal period"
        )

    }

    @Test
    fun `should be able to renew expiring indefinite trailer reservation as Espoo citizen within renewal time limits`() {
        val reserverId = espooCitizenWithoutReservationsId
        val validity = ReservationValidity.Indefinite
        val endDate = getTrailerEndDate(timeProvider.getCurrentDate(),validity)
        val boatSpaceType = BoatSpaceType.Trailer

        // Start at the start of reservation period
        testUtils.moveTimeToReservationPeriodStart(boatSpaceType, ReservationOperation.New)
        val reservation = testUtils.createReservationInConfirmedState(
            CreateReservationParams(
                timeProvider,
                reserverId,
                boatSpaceId = boatSpaceIdForTrailer,
                validity = validity,
                endDate = endDate
            )
        )

        // Move time before the start of the renewal period
        testUtils.moveTimeToReservationPeriodStart(boatSpaceType, ReservationOperation.Renew, addDays = -1)

        assertEquals(
            false,
            renewalPolicyService.citizenCanRenewReservation(reservation.id, reserverId).success,
            "Reservation can't be renewed at the start of the season"
        )

        // Move to the start of renewal period
        testUtils.moveTimeToReservationPeriodStart(boatSpaceType, ReservationOperation.Renew)
        assertEquals(
            true,
            renewalPolicyService.citizenCanRenewReservation(reservation.id, reserverId).success,
            "Reservation can be renewed at the start of the season"
        )

        // Move to the end of renewal period
        testUtils.moveTimeToReservationPeriodEnd(boatSpaceType, ReservationOperation.Renew, addDays = 1)
        assertEquals(
            false,
            renewalPolicyService.citizenCanRenewReservation(reservation.id, reserverId).success,
            "Reservation can't be renewed after the renewal period"
        )

    }
}

