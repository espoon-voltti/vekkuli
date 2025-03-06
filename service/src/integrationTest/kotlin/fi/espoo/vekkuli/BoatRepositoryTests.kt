package fi.espoo.vekkuli

import fi.espoo.vekkuli.domain.BoatType
import fi.espoo.vekkuli.domain.OwnershipStatus
import fi.espoo.vekkuli.repository.BoatRepository
import fi.espoo.vekkuli.service.BoatService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.util.UUID

@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class BoatRepositoryTests : IntegrationTestBase() {
    @Autowired
    private lateinit var boatService: BoatService

    @Autowired
    private lateinit var boatRepository: BoatRepository

    @Test
    fun `should fetch boat count for reserver`() {
        val expectedBoatCount = 5

        for (i in 0 until expectedBoatCount) {
            insertBoat(citizenIdMarko)
        }

        val count = boatRepository.getBoatCountForReserver(citizenIdMarko)
        assertEquals(expectedBoatCount, count, "Boat count for reserver should be equal to expected count")
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
}
