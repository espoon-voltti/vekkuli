package fi.espoo.vekkuli.tests

import fi.espoo.vekkuli.config.BoatSpaceConfig
import fi.espoo.vekkuli.domain.BoatSpaceAmenity
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import kotlin.test.assertEquals

class BoatSpaceSizeLimitsTest {
    @ParameterizedTest
    @CsvSource(
        "Buoy, 200, 300, null, null, null, null",
        "RearBuoy, 200, 600, null, 150, null, 300",
        "Beam, 200, 300, 100, 160, 200, 430",
        "WalkBeam, 200, 300, 100, 125, 150, 430"
    )
    fun `given boat space size, should get correct limits for a boat size`(
        amenity: BoatSpaceAmenity,
        boatSpaceWidth: Int,
        boatSpaceLength: Int,
        expectedMinWidth: String?,
        expectedMaxWidth: String?,
        expectedMinLength: String?,
        expectedMaxLength: String?,
    ) {
        val (minWidth, maxWidth) = BoatSpaceConfig.getWidthLimitsForBoat(boatSpaceWidth, amenity)
        val (minLength, maxLength) = BoatSpaceConfig.getLengthLimitsForBoat(boatSpaceLength, amenity)

        val expectedMinWidthOrNull = expectedMinWidth?.toIntOrNull()
        val expectedMaxWidthOrNull = expectedMaxWidth?.toIntOrNull()
        val expectedMinLengthOrNull = expectedMinLength?.toIntOrNull()
        val expectedMaxLengthOrNull = expectedMaxLength?.toIntOrNull()

        assertEquals(expectedMinWidthOrNull, minWidth)
        assertEquals(expectedMaxWidthOrNull, maxWidth)
        assertEquals(expectedMinLengthOrNull, minLength)
        assertEquals(expectedMaxLengthOrNull, maxLength)
    }

    @ParameterizedTest
    @CsvSource(
        "Buoy, 200, 300, 0, 2147483647, 0, 2147483647",
        "RearBuoy, 200, 600, 250, 2147483647, 900, 2147483647",
        "Beam, 200, 200, 240, 300, 70, 300",
        "WalkBeam, 200, 300, 275, 300, 170, 450"
    )
    fun `given boat size, should get limits for boat space size`(
        amenity: BoatSpaceAmenity,
        boatWidth: Int,
        boatLength: Int,
        expectedMinWidth: String?,
        expectedMaxWidth: String?,
        expectedMinLength: String?,
        expectedMaxLength: String?,
    ) {
        val (minWidth, maxWidth) = BoatSpaceConfig.getWidthLimitsForBoatSpace(boatWidth, amenity)
        val (minLength, maxLength) = BoatSpaceConfig.getLengthLimitsForBoatSpace(boatLength, amenity)

        val expectedMinWidthOrNull = expectedMinWidth?.toIntOrNull()
        val expectedMaxWidthOrNull = expectedMaxWidth?.toIntOrNull()
        val expectedMinLengthOrNull = expectedMinLength?.toIntOrNull()
        val expectedMaxLengthOrNull = expectedMaxLength?.toIntOrNull()

        assertEquals(expectedMinWidthOrNull, minWidth)
        assertEquals(expectedMaxWidthOrNull, maxWidth)
        assertEquals(expectedMinLengthOrNull, minLength)
        assertEquals(expectedMaxLengthOrNull, maxLength)
    }
}
