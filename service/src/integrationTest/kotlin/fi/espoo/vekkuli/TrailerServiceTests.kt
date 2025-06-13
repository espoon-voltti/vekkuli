package fi.espoo.vekkuli

import fi.espoo.vekkuli.boatSpace.citizenTrailer.UpdateTrailerInput
import fi.espoo.vekkuli.domain.*
import fi.espoo.vekkuli.repository.BoatSpaceReservationRepository
import fi.espoo.vekkuli.repository.TrailerRepository
import fi.espoo.vekkuli.service.BoatReservationService
import fi.espoo.vekkuli.service.PaytrailMock
import fi.espoo.vekkuli.utils.decimalToInt
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.math.BigDecimal

@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class TrailerServiceTests : IntegrationTestBase() {
    @Autowired
    private lateinit var boatReservationService: BoatReservationService

    @Autowired
    lateinit var boatSpaceReservationRepository: BoatSpaceReservationRepository

    @Autowired
    lateinit var trailerRepository: TrailerRepository

    @BeforeEach
    override fun resetDatabase() {
        PaytrailMock.reset()
        deleteAllReservations(jdbi)
    }

    @Test
    fun `citizen should be able to change storage type to trailer`() {
        val reservationId =
            testUtils
                .createReservationInConfirmedState(
                    CreateReservationParams(
                        timeProvider,
                        this.citizenIdLeo,
                        boatSpaceIdForWinter
                    )
                ).id
        val trailer =
            UpdateTrailerInput(
                registrationNumber = "ABC-123",
                width = BigDecimal(2.5),
                length = BigDecimal(5.0)
            )

        boatReservationService.updateStorageTypeAndTrailer(
            reservationId,
            storageType = StorageType.Trailer,
            trailerInput = trailer
        )

        val reservation = boatSpaceReservationRepository.getReservationWithDependencies(reservationId)
        val trailerInformation =
            reservation!!.trailerId?.let { trailerRepository.getTrailer(it) }
        assertNotNull(trailerInformation, "Trailer should have been created and added to the reservation")
        assertEquals(StorageType.Trailer, reservation.storageType, "Storage type should have been updated to Trailer")
        assertEquals(decimalToInt(trailer.length), trailerInformation?.lengthCm, "Trailer length should have been updated")
        assertEquals(decimalToInt(trailer.width), trailerInformation?.widthCm, "Trailer width should have been updated")
        assertEquals(
            trailer.registrationNumber,
            trailerInformation?.registrationCode,
            "Trailer registration number have been updated"
        )
    }

    @Test
    fun `if user tries to update storage type to Trailer should throw error if no trailer information is give`() {
        val reservationId =
            testUtils
                .createReservationInConfirmedState(
                    CreateReservationParams(
                        timeProvider,
                        this.citizenIdLeo,
                        boatSpaceIdForWinter
                    )
                ).id

        assertThrows<IllegalArgumentException> {
            boatReservationService.updateStorageTypeAndTrailer(
                reservationId,
                storageType = StorageType.Trailer,
                trailerInput = null
            )
        }
    }

    @Test
    fun `citizen should be able to change storage type to buck`() {
        val reservationId =
            testUtils
                .createReservationInConfirmedState(
                    CreateReservationParams(
                        timeProvider,
                        this.citizenIdLeo,
                        boatSpaceIdForWinter
                    )
                ).id
        boatReservationService.updateStorageTypeAndTrailer(
            reservationId,
            StorageType.Trailer,
            trailerInput = UpdateTrailerInput("ABC123", BigDecimal(1), BigDecimal(1),)
        )
        val originalReservation = boatSpaceReservationRepository.getReservationWithDependencies(reservationId)

        assertEquals(StorageType.Trailer, originalReservation?.storageType, "Reservation should originally be trailer storage type")

        boatReservationService.updateStorageTypeAndTrailer(
            reservationId,
            StorageType.Buck
        )
        val updatedReservation = boatSpaceReservationRepository.getReservationWithDependencies(reservationId)
        val trailer = originalReservation?.trailerId?.let { trailerRepository.getTrailer(it) }
        assertEquals(StorageType.Buck, updatedReservation?.storageType, "Storage type should have been updated to Buck")
        assertEquals(null, updatedReservation?.trailerId, "Trailer should have been removed from the reservation")
        assertNull(trailer, "Trailer should have been deleted")
    }

    @Test
    fun `trying to update trailer for Storage with different amenity storage type, throws an exception`() {
        val reservationId =
            testUtils
                .createReservationInConfirmedState(
                    CreateReservationParams(
                        timeProvider,
                        this.citizenIdLeo,
                        boatSpaceIdForBuckStorage
                    )
                ).id

        val expection =
            assertThrows<IllegalArgumentException> {
                boatReservationService.updateStorageTypeAndTrailer(
                    reservationId,
                    StorageType.Trailer,
                    trailerInput = UpdateTrailerInput("ABC123", BigDecimal(1), BigDecimal(1),)
                )
            }
        assertEquals("Can't update trailer if amenity does not match", expection.message, "Should throw specific message")
    }

    @Test
    fun `trying to update trailer for Split type space, throws an exception`() {
        val reservationId =
            testUtils
                .createReservationInConfirmedState(
                    CreateReservationParams(
                        timeProvider,
                        this.citizenIdLeo,
                        boatSpaceIdForSlip
                    )
                ).id

        val expection =
            assertThrows<IllegalArgumentException> {
                boatReservationService.updateStorageTypeAndTrailer(
                    reservationId,
                    StorageType.Trailer,
                    trailerInput = UpdateTrailerInput("ABC123", BigDecimal(1), BigDecimal(1),)
                )
            }
        assertEquals("Can't update trailer if amenity does not match", expection.message, "Should throw specific message")
    }
}
