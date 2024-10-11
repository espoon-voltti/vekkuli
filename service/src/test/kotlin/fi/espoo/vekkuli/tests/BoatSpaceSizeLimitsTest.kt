package fi.espoo.vekkuli.tests

import fi.espoo.vekkuli.config.BoatSpaceConfig
import fi.espoo.vekkuli.domain.BoatSpaceAmenity
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import kotlin.test.assertEquals

class BoatSpaceSizeLimitsTest {
    @ParameterizedTest
    @CsvSource(
        "Beam, 200, 200, 100, 160, 100, 330"
    )
    fun `given boat space size, should get correct limits for a boat size`(
        amenity: BoatSpaceAmenity,
        boatSpaceWidth: Int,
        boatSpaceLength: Int,
        expectedMinWidth: Int,
        expectedMaxWidth: Int,
        expectedMinLength: Int,
        expectedMaxLength: Int,
    ) {
        val (minWidth, maxWidth) = BoatSpaceConfig.getWidthLimitsForBoat(boatSpaceWidth, amenity)
        val (minLength, maxLength) = BoatSpaceConfig.getLengthLimitsForBoat(boatSpaceLength, amenity)
        assertEquals(expectedMinWidth, minWidth)
        assertEquals(expectedMaxWidth, maxWidth)
        assertEquals(expectedMinLength, minLength)
        assertEquals(expectedMaxLength, maxLength)
    }

    @ParameterizedTest
    @CsvSource(
        "Beam, 200, 200, 240, 300, 70, 300"
    )
    fun `given boat size, should get limits for boat space size`(
        amenity: BoatSpaceAmenity,
        boatWidth: Int,
        boatLength: Int,
        expectedMinWidth: Int,
        expectedMaxWidth: Int,
        expectedMinLength: Int,
        expectedMaxLength: Int,
    ) {
        val (minWidth, maxWidth) = BoatSpaceConfig.getWidthLimitsForBoatSpace(boatWidth, amenity)
        val (minLength, maxLength) = BoatSpaceConfig.getLengthLimitsForBoatSpace(boatLength, amenity)
        assertEquals(expectedMinWidth, minWidth)
        assertEquals(expectedMaxWidth, maxWidth)
        assertEquals(expectedMinLength, minLength)
        assertEquals(expectedMaxLength, maxLength)
    }
}
