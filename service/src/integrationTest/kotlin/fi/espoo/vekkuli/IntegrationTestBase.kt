package fi.espoo.vekkuli

import fi.espoo.vekkuli.domain.BoatSpaceAmenity
import fi.espoo.vekkuli.domain.BoatSpaceType
import fi.espoo.vekkuli.utils.TimeProvider
import fi.espoo.vekkuli.utils.createAndSeedDatabase
import fi.espoo.vekkuli.utils.mockTimeProvider
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.bindKotlin
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.ActiveProfiles
import java.util.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT
)
@ActiveProfiles("test")
abstract class IntegrationTestBase {
    @Autowired
    protected lateinit var jdbi: Jdbi

    val citizenIdLeo: UUID = UUID.fromString("f5d377ea-5547-11ef-a1c7-7f2b94cf9afd")
    val citizenIdOlivia: UUID = UUID.fromString("509edb00-5549-11ef-a1c7-776e76028a49")
    val citizenIdMikko: UUID = UUID.fromString("62d90eed-4ea3-4446-8023-8dad9c01dd34")
    val organizationId: UUID = UUID.fromString("8b220a43-86a0-4054-96f6-d29a5aba17e7")

    val userId: UUID = UUID.fromString("94833b54-132b-4ab8-b841-60df45809b3e")

    @MockBean
    lateinit var timeProvider: TimeProvider

    @BeforeEach
    fun setUp() {
        mockTimeProvider(timeProvider)
    }

    @BeforeAll
    fun beforeAllSuper() {
        createAndSeedDatabase(jdbi)
    }

    @BeforeEach
    fun resetDatabase() {
        // Override this method in subclasses to reset the database before each test
    }

    data class DevBoatSpace(
        val id: Int,
        val type: BoatSpaceType,
        val locationId: Int,
        val priceId: Int,
        val section: String,
        val placeNumber: Int,
        val amenity: BoatSpaceAmenity,
        val widthCm: Int,
        val lengthCm: Int,
        val description: String,
    )

    fun insertDevBoatSpace(boatSpace: DevBoatSpace) {
        jdbi.inTransaction<Unit, Exception> { handle ->
            handle.createUpdate(
                """
                INSERT INTO boat_space (
                    id, type, location_id, price_id, section, place_number, amenity, width_cm, length_cm, description
                ) VALUES (
                    :id, :type, :locationId, :priceId, :section, :placeNumber, :amenity, :widthCm, :lengthCm, :description
                )
                """
            ).bindKotlin(boatSpace)
                .execute()
        }
    }
}
