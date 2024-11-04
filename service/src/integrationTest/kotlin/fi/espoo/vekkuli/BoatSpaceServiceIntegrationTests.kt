package fi.espoo.vekkuli

import fi.espoo.vekkuli.config.BoatSpaceConfig
import fi.espoo.vekkuli.domain.BoatSpaceAmenity
import fi.espoo.vekkuli.domain.BoatSpaceType
import fi.espoo.vekkuli.domain.BoatType
import fi.espoo.vekkuli.service.BoatSpaceFilter
import fi.espoo.vekkuli.service.BoatSpaceService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class BoatSpaceServiceIntegrationTests : IntegrationTestBase() {
    @Autowired
    lateinit var boatSpaceService: BoatSpaceService

    @Test
    fun `should fetch boat spaces if there are no filters`() {
        val boatSpaces =
            boatSpaceService.getUnreservedBoatSpaceOptions(
                BoatSpaceFilter()
            )
        assertEquals(0, boatSpaces.second, "No boat spaces are fetched")
    }

    @Test
    fun `should fetch filtered boat spaces`() {
        val filteredBoatWidth = 350
        val filteredBoatLength = 500
        val boatSpaces =
            boatSpaceService.getUnreservedBoatSpaceOptions(
                BoatSpaceFilter(
                    BoatType.Sailboat,
                    filteredBoatWidth,
                    filteredBoatLength,
                    listOf(BoatSpaceAmenity.Beam),
                    BoatSpaceType.Slip
                )
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
                BoatSpaceFilter(
                    BoatType.Sailboat,
                    null,
                    null,
                    listOf(BoatSpaceAmenity.Beam),
                    BoatSpaceType.Slip
                )
            )
        assertEquals(boatSpaces.second, 0, "No boat spaces are fetched")
    }
}
