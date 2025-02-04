package fi.espoo.vekkuli

import fi.espoo.vekkuli.domain.*
import fi.espoo.vekkuli.repository.BoatSpaceReservationRepository
import fi.espoo.vekkuli.service.BoatService
import fi.espoo.vekkuli.service.PaytrailMock
import fi.espoo.vekkuli.utils.mockTimeProvider
import fi.espoo.vekkuli.utils.startOfSlipReservationPeriod
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.Duration
import java.util.UUID
import kotlin.test.assertEquals

@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class BoatSpaceReservationRepositoryTests : IntegrationTestBase() {
    @Autowired
    private lateinit var boatService: BoatService

    @Autowired
    private lateinit var boatSpaceReservationRepository: BoatSpaceReservationRepository

    @BeforeEach
    override fun resetDatabase() {
        PaytrailMock.reset()
        deleteAllReservations(jdbi)
        deleteAllBoatSpaces(jdbi)
        deleteAllBoats(jdbi)
    }

    @Test
    fun `Payment before reservation time expiry results in confirmed reservation`() {
        val currentTime = startOfSlipReservationPeriod
        val today = currentTime.toLocalDate()
        mockTimeProvider(timeProvider, currentTime)

        val boatSpaceId = insertBoatSpace()

        val reservationId = 3456
        insertDevBoatSpaceReservation(
            DevBoatSpaceReservation(
                id = reservationId,
                reserverId = citizenIdMikko,
                boatSpaceId = boatSpaceId,
                boatId = insertBoat(citizenIdMikko),
                created = currentTime,
                startDate = today,
                endDate = today.plusMonths(12),
                status = ReservationStatus.Payment
            )
        )

        val payment =
            insertDevPayment(
                DevPayment(
                    reserverId = citizenIdMikko,
                    reservationId = reservationId,
                    status = PaymentStatus.Created
                )
            )

        val beforeReservationTimerExpires = currentTime.plus(Duration.ofMinutes(15))
        mockTimeProvider(timeProvider, beforeReservationTimerExpires)

        val updateResult = boatSpaceReservationRepository.updateBoatSpaceReservationOnPaymentSuccess(payment.id)
        val updatedReservation = boatSpaceReservationRepository.getBoatSpaceReservationDetails(reservationId)

        assertEquals(reservationId, updateResult)
        assertEquals(ReservationStatus.Confirmed, updatedReservation?.status)
    }

    @Test
    fun `Payment after reservation time expiry results in confirmed reservation if space is still available`() {
        val currentTime = startOfSlipReservationPeriod
        val today = currentTime.toLocalDate()
        mockTimeProvider(timeProvider, currentTime)

        val boatSpaceId = insertBoatSpace()

        val reservationId = 3456
        insertDevBoatSpaceReservation(
            DevBoatSpaceReservation(
                id = reservationId,
                reserverId = citizenIdMikko,
                boatSpaceId = boatSpaceId,
                boatId = insertBoat(citizenIdMikko),
                created = currentTime,
                startDate = today,
                endDate = today.plusMonths(12),
                status = ReservationStatus.Payment
            )
        )

        val payment =
            insertDevPayment(
                DevPayment(
                    reserverId = citizenIdMikko,
                    reservationId = reservationId,
                    status = PaymentStatus.Created
                )
            )

        val reservationTimerExpired = currentTime.plus(Duration.ofMinutes(25))
        mockTimeProvider(timeProvider, reservationTimerExpired)

        val updateResult = boatSpaceReservationRepository.updateBoatSpaceReservationOnPaymentSuccess(payment.id)
        val updatedReservation = boatSpaceReservationRepository.getBoatSpaceReservationDetails(reservationId)

        assertEquals(reservationId, updateResult)
        assertEquals(ReservationStatus.Confirmed, updatedReservation?.status)
    }

    @Test
    fun `Payment after reservation time expiry fails if space is not available`() {
        val currentTime = startOfSlipReservationPeriod
        val today = currentTime.toLocalDate()
        mockTimeProvider(timeProvider, currentTime)

        val boatSpaceId = insertBoatSpace()

        val reservationId = 3456
        insertDevBoatSpaceReservation(
            DevBoatSpaceReservation(
                id = reservationId,
                reserverId = citizenIdMikko,
                boatSpaceId = boatSpaceId,
                boatId = insertBoat(citizenIdMikko),
                created = currentTime,
                startDate = today,
                endDate = today.plusMonths(12),
                status = ReservationStatus.Payment
            )
        )

        val newerReservationId = 3457
        insertDevBoatSpaceReservation(
            DevBoatSpaceReservation(
                id = newerReservationId,
                reserverId = citizenIdLeo,
                boatSpaceId = boatSpaceId,
                boatId = insertBoat(citizenIdLeo),
                created = currentTime.plusMinutes(1),
                startDate = today,
                endDate = today.plusMonths(12),
                status = ReservationStatus.Info
            )
        )

        val payment =
            insertDevPayment(
                DevPayment(
                    reserverId = citizenIdMikko,
                    reservationId = reservationId,
                    status = PaymentStatus.Created
                )
            )

        val reservationTimerExpired = currentTime.plus(Duration.ofMinutes(25))
        mockTimeProvider(timeProvider, reservationTimerExpired)

        val updateResult = boatSpaceReservationRepository.updateBoatSpaceReservationOnPaymentSuccess(payment.id)
        val updatedReservation = boatSpaceReservationRepository.getBoatSpaceReservationDetails(reservationId)

        assertEquals(null, updateResult)
        assertEquals(ReservationStatus.Payment, updatedReservation?.status)
    }

    private fun insertBoat(reserverId: UUID): Int =
        boatService
            .insertBoat(
                reserverId,
                "registrationCode",
                "TestBoat",
                150,
                150,
                150,
                150,
                BoatType.Sailboat,
                "",
                "",
                OwnershipStatus.Owner
            ).id

    private fun insertBoatSpace(): Int {
        val boatSpaceId = 1234
        insertDevBoatSpace(
            DevBoatSpace(
                id = boatSpaceId,
                type = BoatSpaceType.Slip,
                locationId = 1,
                priceId = 1,
                section = "A",
                placeNumber = 1,
                amenity = BoatSpaceAmenity.None,
                widthCm = 100,
                lengthCm = 200,
                description = "Test boat space"
            )
        )

        return boatSpaceId
    }
}
