package fi.espoo.vekkuli

import fi.espoo.vekkuli.domain.*
import fi.espoo.vekkuli.repository.TrailerRepository
import fi.espoo.vekkuli.service.*
import fi.espoo.vekkuli.utils.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.math.BigDecimal

@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class BoatReservationServiceTests : IntegrationTestBase() {
    @Autowired
    lateinit var reservationService: BoatReservationService

    @Autowired
    lateinit var trailerRepository: TrailerRepository

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
        assertEquals(8, harbors.size, "Correct number of harbors are fetched")
        assertEquals("Mellstenintie 6, 02170 Espoo", harbors[0].address, "Correct address for first harbor")
        assertEquals("Haukilahti", harbors[0].name, "Correct name for first harbor")
    }

    @Test
    fun `should be able to update trailer`() {
        val reservation =
            testUtils.createReservationInConfirmedState(
                CreateReservationParams(
                    timeProvider,
                    citizenId = citizenIdOlivia,
                )
            )

        val trailer =
            trailerRepository
                .insertTrailerAndAddToReservation(reservation.id, citizenIdOlivia, "XYZ-789", 200, 400)

        val updatedReservation = reservationService.getBoatSpaceReservation(reservation.id)

        assertEquals(trailer.id, updatedReservation?.trailerWithWarnings?.id, "Reservation has a trailer attached")
        assertEquals(trailer.reserverId, updatedReservation?.trailerWithWarnings?.reserverId, "Trailer belongs to the reserver")
        assertEquals(trailer.widthCm, updatedReservation?.trailerWithWarnings?.widthCm, "Trailer width has been set")
        assertEquals(trailer.lengthCm, updatedReservation?.trailerWithWarnings?.lengthCm, "Trailer length has been set")

        reservationService
            .updateTrailerAndAddWarnings(
                citizenIdOlivia,
                trailer.id,
                trailerRegistrationCode = "ABC-123",
                trailerWidth = BigDecimal(2.5),
                trailerLength = BigDecimal(15.0)
            )

        val updatedTrailer = reservationService.getTrailer(trailer.id)
        assertEquals("ABC-123", updatedTrailer?.registrationCode, "Trailer registration number is updated")
        assertEquals(250, updatedTrailer?.widthCm, "Trailer width is updated")
        assertEquals(1500, updatedTrailer?.lengthCm, "Trailer length is updated")
        assertEquals(1, updatedTrailer?.warnings?.size, "There is one warning for the trailer")
        assertEquals(ReservationWarningType.TrailerLength, updatedTrailer?.warnings?.first(), "Warning is for trailer length")
    }

    @Test
    fun `should update for employee`() {
        val reservation =
            testUtils.createReservationInConfirmedState(
                CreateReservationParams(
                    timeProvider,
                    citizenId = citizenIdOlivia,
                    boatSpaceId = boatSpaceIdForTrailer
                )
            )

        // Should add a trailer for the reserver
        val trailer =
            reservationService.createOrUpdateTrailerForReservationEmployee(
                reservation.id,
                userId,
                reserverId = reservation.reserverId,
                trailerRegistrationCode = "ABC-123",
                trailerWidth = BigDecimal(2.5),
                trailerLength = BigDecimal(15.0)
            )
        val fetchedTrailer = reservationService.getTrailer(trailer.id)

        // Created trailer and fetched reservation should be the same
        assertEquals(trailer, fetchedTrailer, "Created trailer and fetched trailer are the same")

        assertEquals("ABC-123", fetchedTrailer?.registrationCode, "Trailer registration number is updated")
        assertEquals(250, fetchedTrailer?.widthCm, "Trailer width is updated")
        assertEquals(1500, fetchedTrailer?.lengthCm, "Trailer length is updated")
        assertEquals(0, fetchedTrailer?.warnings?.size, "Employee updates should not add warnings")

        // Should update the trailer for the reserver
        val updatedTrailer =
            reservationService.createOrUpdateTrailerForReservationEmployee(
                reservation.id,
                userId,
                reserverId = reservation.reserverId,
                trailerRegistrationCode = "XYZ-789",
                trailerWidth = BigDecimal(3.0),
                trailerLength = BigDecimal(6.0)
            )
        val fetchedUpdatedTrailer = reservationService.getTrailer(updatedTrailer.id)

        // Updated trailer and fetched reservation should be the same
        assertEquals(updatedTrailer, fetchedUpdatedTrailer, "Updated trailer and fetched trailer are the same")

        assertEquals(trailer.id, updatedTrailer?.id, "Trailer is updated, not created")
        assertEquals("XYZ-789", fetchedUpdatedTrailer?.registrationCode, "Trailer registration number is updated")
        assertEquals(300, fetchedUpdatedTrailer?.widthCm, "Trailer width is updated")
        assertEquals(600, fetchedUpdatedTrailer?.lengthCm, "Trailer length is updated")
        assertEquals(0, fetchedUpdatedTrailer?.warnings?.size, "There are no warnings")
    }
}
