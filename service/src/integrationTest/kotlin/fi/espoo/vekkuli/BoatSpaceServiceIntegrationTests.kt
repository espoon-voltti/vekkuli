package fi.espoo.vekkuli

import fi.espoo.vekkuli.boatSpace.boatSpaceList.BoatSpaceListParams
import fi.espoo.vekkuli.boatSpace.terminateReservation.TerminateReservationService
import fi.espoo.vekkuli.config.BoatSpaceConfig
import fi.espoo.vekkuli.domain.*
import fi.espoo.vekkuli.repository.BoatSpaceReservationRepository
import fi.espoo.vekkuli.service.BoatSpaceRepository
import fi.espoo.vekkuli.service.BoatSpaceService
import fi.espoo.vekkuli.service.EditBoatSpaceParams
import fi.espoo.vekkuli.utils.createAndSeedDatabase
import fi.espoo.vekkuli.utils.mockTimeProvider
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
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
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull

@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class BoatSpaceServiceIntegrationTests : IntegrationTestBase() {
    @Autowired
    private lateinit var boatSpaceReservationRepository: BoatSpaceReservationRepository

    @Autowired
    private lateinit var terminateReservationService: TerminateReservationService

    @Autowired
    lateinit var boatSpaceService: BoatSpaceService

    @Autowired
    lateinit var boatSpaceRepository: BoatSpaceRepository

    @BeforeEach
    override fun resetDatabase() {
        createAndSeedDatabase(jdbi)
    }

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
        "Winter, None, 2.5, 4.5, 27",
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
        assertEquals(2438, boatSpaces.size, "No boat spaces are fetched")
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

    @Test
    fun `boat space should not be available if it has reservation`() {
        var isBoatSpaceAvailable = boatSpaceRepository.isBoatSpaceAvailable(10)
        assertEquals(true, isBoatSpaceAvailable, "Boat space is available")
        testUtils.createReservationInConfirmedState(
            CreateReservationParams(
                timeProvider,
                citizenIdLeo,
                10,
                1,
                validity = ReservationValidity.Indefinite,
                startDate = timeProvider.getCurrentDate(),
                endDate = timeProvider.getCurrentDate().plusDays(1)
            )
        )
        isBoatSpaceAvailable = boatSpaceRepository.isBoatSpaceAvailable(10)
        assertEquals(false, isBoatSpaceAvailable, "Boat space is not available")
    }

    @Test
    fun `boat space should not be available if it is not active`() {
        val boatSpaceAvailable = boatSpaceRepository.isBoatSpaceAvailable(31)
        assertEquals(false, boatSpaceAvailable, "Boat space is not available")
    }

    @Test
    fun `reserver should be fetched only when they have an active reservation for boat space`() {
        val boatSpaceId = 83
        val reservation =
            testUtils.createReservationInConfirmedState(
                CreateReservationParams(
                    timeProvider,
                    this.citizenIdLeo,
                    boatSpaceId,
                    1,
                    validity = ReservationValidity.Indefinite,
                    startDate = timeProvider.getCurrentDate(),
                    endDate = timeProvider.getCurrentDate()
                )
            )
        val boatSpace =
            boatSpaceService
                .getBoatSpacesFiltered(
                    BoatSpaceListParams(
                        harbor = listOf(1),
                        boatSpaceType = listOf(BoatSpaceType.Slip),
                        amenity = listOf(BoatSpaceAmenity.Beam),
                        sectionFilter = listOf("D"),
                        sortBy = BoatSpaceFilterColumn.RESERVER,
                        ascending = true
                    )
                ).first { it.id == boatSpaceId }
        assertEquals(boatSpace.reserverId, reservation.reserverId, "Correct reserver is fetched")

        terminateReservationService.terminateBoatSpaceReservationAsOwner(
            reservation.id,
            this.citizenIdLeo
        )
        val boatSpaceAfterTermination =
            boatSpaceService
                .getBoatSpacesFiltered(
                    BoatSpaceListParams(
                        harbor = listOf(1),
                        boatSpaceType = listOf(BoatSpaceType.Slip),
                        amenity = listOf(BoatSpaceAmenity.Beam),
                        sectionFilter = listOf("D"),
                        sortBy = BoatSpaceFilterColumn.RESERVER,
                        ascending = true
                    )
                ).first { it.id == boatSpaceId }
        assertEquals(boatSpaceAfterTermination.reserverId, null, "Reserver is null after termination")
    }

    @Test
    fun `multiple boat spaces can be edited`() {
        val originalBoatSpace = boatSpaceRepository.getBoatSpace(1)
        val originalBoatSpace2 = boatSpaceRepository.getBoatSpace(84)
        val chosenHarbor =
            boatSpaceReservationRepository.getHarbors()[2]
        val editBoatSpaceParams =
            EditBoatSpaceParams(
                chosenHarbor.id,
                BoatSpaceType.Trailer,
                "test",
                2,
                BoatSpaceAmenity.RearBuoy,
                150,
                250,
                2,
                false
            )
        boatSpaceService.editBoatSpaces(listOf(1, 84), editBoatSpaceParams)
        val editedBoatSpace = boatSpaceRepository.getBoatSpace(1)
        val editedBoatSpace2 = boatSpaceRepository.getBoatSpace(84)

        assertNotNull(editedBoatSpace, "Edited boat space is fetched")
        // because there are multiple boat spaces to edit, the section and place numbers should be the same
        assertEquals(originalBoatSpace?.section, editedBoatSpace.section, "Section was not edited")
        assertEquals(originalBoatSpace?.placeNumber, editedBoatSpace.placeNumber, "Place number was not edited")
        assertEquals(chosenHarbor.name, editedBoatSpace.locationName, "Boat space location has been edited")
        assertEquals(editBoatSpaceParams.amenity, editedBoatSpace.amenity, "Amenity has been edited")
        assertEquals(editBoatSpaceParams.widthCm, editedBoatSpace.widthCm, "Boat space width has been edited")
        assertEquals(editBoatSpaceParams.lengthCm, editedBoatSpace.lengthCm, "Boat space length has been edited")
        assertEquals(editBoatSpaceParams.isActive, editedBoatSpace.isActive, "Boat space has been edited")

        assertNotNull(editedBoatSpace2, "Boat space is edited")
        assertEquals(originalBoatSpace2?.section, editedBoatSpace2.section, "Boat space section has been edited")
        assertEquals(originalBoatSpace2?.placeNumber, editedBoatSpace2.placeNumber, "Boat space place number has been edited")
        assertEquals(chosenHarbor.name, editedBoatSpace.locationName, "Boat space location has been edited")
        assertEquals(editBoatSpaceParams.amenity, editedBoatSpace2.amenity, "Boat space amenity has been edited")
        assertEquals(editBoatSpaceParams.widthCm, editedBoatSpace2.widthCm, "Boat space width has been edited")
        assertEquals(editBoatSpaceParams.lengthCm, editedBoatSpace2.lengthCm, "Boat space length has been edited")
        assertEquals(editBoatSpaceParams.isActive, editedBoatSpace2.isActive, "Boat space has been edited")

        assertNotEquals(editedBoatSpace.section, editedBoatSpace2.section, "Section has not been edited to be the same")
        assertNotEquals(editedBoatSpace.placeNumber, editedBoatSpace2.placeNumber, "Place number has not been edited to be the same")
    }

    @Test
    fun `boat space can be edited`() {
        val chosenHarbor =
            boatSpaceReservationRepository.getHarbors().first()
        val editBoatSpaceParams =
            EditBoatSpaceParams(
                2,
                BoatSpaceType.Slip,
                "C",
                5,
                BoatSpaceAmenity.Beam,
                100,
                200,
                1,
                true
            )
        boatSpaceService.editBoatSpaces(listOf(1), editBoatSpaceParams)
        val editedBoatSpace = boatSpaceRepository.getBoatSpace(1)

        assertNotNull(editedBoatSpace, "Boat space is edited")
        // because there is only one boat space to edit, the section and place numbers should be the edited ones
        assertEquals(editBoatSpaceParams.section, editedBoatSpace.section, "Boat space section has been edited")
        assertEquals(editBoatSpaceParams.placeNumber, editedBoatSpace.placeNumber, "Boat space place number has been edited")
        assertEquals(editBoatSpaceParams.amenity, editedBoatSpace.amenity, "Boat space amenity has been edited")
        assertEquals(editBoatSpaceParams.widthCm, editedBoatSpace.widthCm, "Boat space width has been edited")
        assertEquals(editBoatSpaceParams.lengthCm, editedBoatSpace.lengthCm, "Boat space length has been edited")
        assertEquals(editBoatSpaceParams.isActive, editedBoatSpace.isActive, "Boat space has been edited\"")
    }
}
