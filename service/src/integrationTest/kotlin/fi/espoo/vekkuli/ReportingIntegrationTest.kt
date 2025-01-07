package fi.espoo.vekkuli

import fi.espoo.vekkuli.domain.*
import fi.espoo.vekkuli.service.BoatReservationService
import fi.espoo.vekkuli.service.getRawReport
import fi.espoo.vekkuli.service.getStickerReport
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

    @Test
    fun `raw report`() {
        val boatSpaceId = Random(42).nextInt(1000, 9999)

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

        val today = LocalDate.of(2024, 10, 1)
        reservationService.insertBoatSpaceReservation(
            citizenIdLeo,
            citizenIdLeo,
            boatSpaceId,
            CreationType.New,
            today,
            today.plusMonths(12)
        )

        val rawReportRows = getRawReport(jdbi)
        assertEquals(true, rawReportRows.size > 0)
        val row = rawReportRows.find { it.boatSpaceId == boatSpaceId.toString() }
        assertEquals("Test boat space", row?.description)
        assertEquals(today.plusMonths(12).toString(), row?.endDate)
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

        val stickerReportRows = getStickerReport(jdbi, today)
        assertEquals(true, stickerReportRows.size > 0)
        val row = stickerReportRows.find { it.place == "A 001" }
        assertEquals("Testi Venho", row?.boatName)
    }
}
