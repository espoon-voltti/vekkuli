package fi.espoo.vekkuli

import fi.espoo.vekkuli.domain.BoatSpaceAmenity
import fi.espoo.vekkuli.domain.BoatSpaceType
import fi.espoo.vekkuli.domain.BoatType
import fi.espoo.vekkuli.domain.OwnershipStatus
import fi.espoo.vekkuli.service.*
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.LocalDate
import kotlin.random.Random
import kotlin.test.assertEquals

@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ReportingIntegrationTest : IntegrationTestBase() {
    @Autowired
    lateinit var reservationService: BoatReservationService

    override fun resetDatabase() {
        deleteAllReservations(jdbi)
        deleteAllBoats(jdbi)
        deleteAllBoatSpaces(jdbi)
    }

    @Test
    fun `sticker report`() {
        val boatSpaceId = 4242

        insertDevBoatSpace(
            DevBoatSpace(
                id = boatSpaceId,
                type = BoatSpaceType.Slip,
                locationId = 1,
                priceId = 1,
                section = "A",
                placeNumber = 1,
                amenity = BoatSpaceAmenity.Beam,
                widthCm = 100,
                lengthCm = 200,
                description = "Test boat space"
            )
        )

        val today = LocalDate.of(2024, 10, 1)

        val boatId = 123321
        insertDevBoat(
            DevBoat(
                id = boatId,
                registrationCode = "U12345",
                reserverId = citizenIdLeo,
                name = "Testi Venho",
                widthCm = 100,
                lengthCm = 200,
                depthCm = 50,
                weightKg = 1000,
                type = BoatType.OutboardMotor,
                otherIdentification = "123456",
                extraInformation = "Test boat",
                ownership = OwnershipStatus.Owner
            )
        )

        val resId = 3131

        insertDevBoatSpaceReservation(
            DevBoatSpaceReservation(
                id = resId,
                reserverId = citizenIdLeo,
                boatSpaceId = boatSpaceId,
                startDate = today,
                endDate = today.plusMonths(12),
                boatId = boatId,
            )
        )

        val stickerReportRows = getStickerReport(jdbi, today.atStartOfDay())
        assertEquals(true, stickerReportRows.size > 0)
        val row = stickerReportRows.find { it.place == "A 001" }
        assertEquals("Testi Venho", row?.boatName)
    }

    @Test
    fun `boat place report (all)`() {
        val boatSpaceId = 4242

        insertDevBoatSpace(
            DevBoatSpace(
                id = boatSpaceId,
                type = BoatSpaceType.Slip,
                locationId = 1,
                priceId = 1,
                section = "A",
                placeNumber = 1,
                amenity = BoatSpaceAmenity.Beam,
                widthCm = 100,
                lengthCm = 200,
                description = "Test boat space"
            )
        )

        val today = LocalDate.of(2024, 10, 1)

        val boatId = 123321
        insertDevBoat(
            DevBoat(
                id = boatId,
                registrationCode = "U12345",
                reserverId = citizenIdLeo,
                name = "Testi Venho",
                widthCm = 100,
                lengthCm = 200,
                depthCm = 50,
                weightKg = 1000,
                type = BoatType.OutboardMotor,
                otherIdentification = "123456",
                extraInformation = "Test boat",
                ownership = OwnershipStatus.Owner
            )
        )

        val resId = 3131

        insertDevBoatSpaceReservation(
            DevBoatSpaceReservation(
                id = resId,
                reserverId = citizenIdLeo,
                boatSpaceId = boatSpaceId,
                startDate = today,
                endDate = today.plusMonths(12),
                boatId = boatId,
            )
        )

        val reportRows = getBoatSpaceReport(jdbi, today.atStartOfDay())
        assertEquals(true, reportRows.size > 0)
        val row = reportRows.find { it.place == "A 001" }
        assertEquals("Korhonen Leo", row?.name)
    }

    @Test
    fun `boat place report (free and reserved)`() {
        val freeBoatSpaceId = 4242
        val reservedBoatSpaceId = 4243

        insertDevBoatSpace(
            DevBoatSpace(
                id = freeBoatSpaceId,
                type = BoatSpaceType.Slip,
                locationId = 1,
                priceId = 1,
                section = "A",
                placeNumber = 1,
                amenity = BoatSpaceAmenity.Beam,
                widthCm = 100,
                lengthCm = 200,
                description = "Test free boat space"
            )
        )

        insertDevBoatSpace(
            DevBoatSpace(
                id = reservedBoatSpaceId,
                type = BoatSpaceType.Slip,
                locationId = 1,
                priceId = 1,
                section = "A",
                placeNumber = 2,
                amenity = BoatSpaceAmenity.Buoy,
                widthCm = 100,
                lengthCm = 200,
                description = "Test reserved boat space"
            )
        )

        val today = LocalDate.of(2024, 10, 1)

        val boatId = 123321
        insertDevBoat(
            DevBoat(
                id = boatId,
                registrationCode = "U12345",
                reserverId = citizenIdLeo,
                name = "Testi Venho",
                widthCm = 100,
                lengthCm = 200,
                depthCm = 50,
                weightKg = 1000,
                type = BoatType.OutboardMotor,
                otherIdentification = "123456",
                extraInformation = "Test boat",
                ownership = OwnershipStatus.Owner
            )
        )

        val resId = 3131

        insertDevBoatSpaceReservation(
            DevBoatSpaceReservation(
                id = resId,
                reserverId = citizenIdLeo,
                boatSpaceId = reservedBoatSpaceId,
                startDate = today,
                endDate = today.plusMonths(12),
                boatId = boatId,
            )
        )

        val freeRows = getFreeBoatSpaceReport(jdbi, today.atStartOfDay())
        freeRows.find { it.place == "A 001" }
        assertTrue(freeRows.none { it.place == "A 002" })

        val reservedRows = getReservedBoatSpaceReport(jdbi, today.atStartOfDay())
        assertTrue(reservedRows.any { it.place == "A 002" })
        assertTrue(reservedRows.none { it.place == "A 001" })
    }
}
