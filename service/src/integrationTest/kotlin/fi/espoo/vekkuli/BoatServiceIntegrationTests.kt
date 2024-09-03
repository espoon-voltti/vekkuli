package fi.espoo.vekkuli

import fi.espoo.vekkuli.domain.Boat
import fi.espoo.vekkuli.domain.BoatType
import fi.espoo.vekkuli.domain.OwnershipStatus
import fi.espoo.vekkuli.service.BoatService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.util.*

@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class BoatServiceIntegrationTests : IntegrationTestBase() {
    @BeforeEach
    override fun resetDatabase() {
        deleteAllReservations(jdbi)
        deleteAllBoats(jdbi)
    }

    @Autowired
    lateinit var boatService: BoatService

    private fun insertNewBoat(
        citizenId: UUID = this.citizenId,
        name: String = "TestBoat"
    ): Boat {
        return boatService.insertBoat(
            citizenId,
            "registrationCode",
            name,
            150,
            150,
            150,
            150,
            BoatType.Sailboat,
            "",
            "",
            OwnershipStatus.Owner
        )
    }

    @Test
    fun `should update boat`() {
        val boat = insertNewBoat()
        boatService.updateBoat(
            boat.copy(name = "UpdatedTestBoat")
        )
        val updatedBoat = boatService.getBoat(boat.id)
        assertEquals("UpdatedTestBoat", updatedBoat?.name, "Boat is updated")
    }

    @Test
    fun `should get boats for citizen`() {
        insertNewBoat(citizenId, "TestBoat1",)
        insertNewBoat(citizenId, "TestBoat2",)
        boatService.getBoatsForCitizen(citizenId)
        assertEquals(2, boatService.getBoatsForCitizen(citizenId).size, "Correct number of boats are fetched")
    }
}
