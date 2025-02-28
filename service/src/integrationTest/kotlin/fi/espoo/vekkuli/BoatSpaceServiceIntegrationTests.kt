package fi.espoo.vekkuli

import fi.espoo.vekkuli.boatSpace.boatSpaceList.BoatSpaceListParams
import fi.espoo.vekkuli.config.BoatSpaceConfig
import fi.espoo.vekkuli.domain.*
import fi.espoo.vekkuli.service.BoatSpaceService
import fi.espoo.vekkuli.utils.mockTimeProvider
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.math.BigDecimal
import java.time.LocalDateTime

@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class BoatSpaceServiceIntegrationTests : IntegrationTestBase() {
    @Autowired
    lateinit var boatSpaceService: BoatSpaceService

    @Test
    fun `should not return a reserved boat space if reservation ends today`() {
        mockTimeProvider(timeProvider, LocalDateTime.of(2024, 4, 30, 12, 0, 0))

        val boatSpaces =
            boatSpaceService
                .getUnreservedBoatSpaceOptions(
                    width = BigDecimal(3.5),
                    length = BigDecimal(5.0),
                    boatSpaceType = BoatSpaceType.Slip
                ).first
                .map { it.boatSpaces }
                .flatten()

        assertTrue(boatSpaces.any { it.id == 83 }, "Boat space 83 is available")

        // Now reserve
        val reserver = this.citizenIdLeo
        testUtils.createReservationInConfirmedState(
            CreateReservationParams(
                timeProvider,
                reserver,
                83,
                1,
                validity = ReservationValidity.Indefinite,
                startDate = timeProvider.getCurrentDate(),
                endDate = timeProvider.getCurrentDate()
            )
        )
        val boatSpacesAfterReservation =
            boatSpaceService
                .getUnreservedBoatSpaceOptions(
                    width = BigDecimal(3.5),
                    length = BigDecimal(5.0),
                    boatSpaceType = BoatSpaceType.Slip
                ).first
                .map { it.boatSpaces }
                .flatten()

        assertTrue(boatSpacesAfterReservation.none { it.id == 83 }, "Boat space 83 is not available")
    }

    @Test
    fun `should fetch boat spaces if there are no filters`() {
        val boatSpaces =
            boatSpaceService.getUnreservedBoatSpaceOptions()
        assertEquals(0, boatSpaces.second, "No boat spaces are fetched")
    }

    @Test
    fun `should fetch filtered boat spaces`() {
        mockTimeProvider(timeProvider, LocalDateTime.of(2024, 4, 30, 12, 0, 0))

        val filteredBoatWidth = 350
        val filteredBoatLength = 500
        val boatSpaces =
            boatSpaceService.getUnreservedBoatSpaceOptions(
                BoatType.Sailboat,
                BigDecimal(filteredBoatWidth / 100.0),
                BigDecimal(filteredBoatLength / 100.0),
                listOf(BoatSpaceAmenity.Beam),
                BoatSpaceType.Slip
            )
        assertEquals(7, boatSpaces.second, "Correct number of boat spaces are fetched")

        assertTrue(
            boatSpaces.first.all {
                it.boatSpaces.all { bs ->
                    bs.amenity == BoatSpaceAmenity.Beam
                }
            },
            "Only boat spaces with correct amenity are fetched"
        )
        assertTrue(
            boatSpaces.first.all {
                it.boatSpaces.all { bs ->
                    bs.widthCm >= (filteredBoatWidth + BoatSpaceConfig.BEAM_MAX_WIDTH_ADJUSTMENT_CM) &&
                        bs.lengthCm >= (filteredBoatLength - BoatSpaceConfig.BEAM_MIN_LENGTH_ADJUSTMENT_CM)
                }
            },
            "Only boat spaces that are big enough are fetched"
        )
    }

    @Test
    fun `should return empty list if boat width and length is not given`() {
        val boatSpaces =
            boatSpaceService.getUnreservedBoatSpaceOptions(
                BoatType.Sailboat,
                null,
                null,
                listOf(BoatSpaceAmenity.Beam),
                BoatSpaceType.Slip
            )
        assertEquals(boatSpaces.second, 0, "No boat spaces are fetched")
    }

    @ParameterizedTest
    @CsvSource(
        "Slip, Beam, 10, 10, 0",
        "Slip, Beam, 3, 5, 30",
        "Trailer, None, 10, 10, 0",
        "Trailer, None, 1, 3, 28",
        "Trailer, Trailer, 1, 1, 0",
        "Trailer, Buck, 1, 1, 0",
        "Winter, None, 10, 10, 0",
        "Winter, None, 2.75, 5.5, 25",
        "Winter, None, 2.5, 4.5, 28",
        "Winter, Trailer, 1, 1, 0",
        "Winter, Buck, 1, 1, 0",
        "Storage, Buck, 10, 10, 0",
        "Storage, Buck, 1, 1, 11",
        "Storage, Trailer, 10, 10, 0",
        "Storage, Trailer, 1, 1, 10",
        "Storage, None, 1, 1, 0",
    )
    fun `should fetch spaces boat spaces with expected filters`(
        spaceType: BoatSpaceType,
        amenity: BoatSpaceAmenity,
        width: BigDecimal,
        length: BigDecimal,
        expectedResults: Int
    ) {
        val expectedResultBoatSpaces =
            boatSpaceService.getUnreservedBoatSpaceOptions(
                BoatType.OutboardMotor,
                width,
                length,
                listOf(amenity),
                spaceType
            )

        assertEquals(
            expectedResults,
            expectedResultBoatSpaces.second,
            "Correct number of boat spaces are fetched for: $spaceType: $amenity with filters ${width}x$length"
        )

        assertTrue(
            expectedResultBoatSpaces.first.all {
                it.boatSpaces.all { bs ->
                    bs.amenity == amenity
                }
            },
            "Only boat spaces with correct amenity are fetched"
        )
    }

    @Test
    fun `should get sections`() {
        val sections = boatSpaceService.getSections()

        // the sections are set up in the seed data (seed.sql)
        assertEquals(14, sections.size, "Correct number of sections are fetched")
    }

    @Test
    fun `should get all boat spaces`() {
        val params = BoatSpaceListParams()
        val boatSpaces = boatSpaceService.getBoatSpacesFiltered(params)

        // the boat spaces are set up in the seed data (seed.sql)
        assertEquals(2434, boatSpaces.size, "No boat spaces are fetched")
    }

    @Test
    fun `should get boat spaces filtered`() {
        val params =
            BoatSpaceListParams(
                sortBy = BoatSpaceFilterColumn.AMENITY,
                boatSpaceState = listOf(BoatSpaceState.Active),
                harbor = listOf(1),
                boatSpaceType = listOf(BoatSpaceType.Slip),
                amenity = listOf(BoatSpaceAmenity.Beam),
                sectionFilter = listOf("B")
            )
        val boatSpaces = boatSpaceService.getBoatSpacesFiltered(params)

        // the boat spaces are set up in the seed data (seed.sql)
        assertEquals(17, boatSpaces.size, "Correct number of boat spaces are fetched")
    }
}
