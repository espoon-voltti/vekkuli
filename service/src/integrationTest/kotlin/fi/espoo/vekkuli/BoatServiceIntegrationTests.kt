package fi.espoo.vekkuli

import fi.espoo.vekkuli.domain.Boat
import fi.espoo.vekkuli.domain.BoatType
import fi.espoo.vekkuli.domain.OwnershipStatus
import fi.espoo.vekkuli.service.BoatReservationService
import fi.espoo.vekkuli.service.BoatService
import fi.espoo.vekkuli.utils.mockTimeProvider
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

    @Autowired
    lateinit var reservationService: BoatReservationService

    private fun insertNewBoat(
        citizenId: UUID = this.citizenIdLeo,
        name: String = "TestBoat"
    ): Boat =
        boatService.insertBoat(
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

    @Test
    fun `should update boat`() {
        val boat = insertNewBoat()
        boatService.updateBoatAsEmployee(
            boat.copy(name = "UpdatedTestBoat")
        )
        val updatedBoat = boatService.getBoat(boat.id)
        assertEquals("UpdatedTestBoat", updatedBoat?.name, "Boat is updated")
    }

    @Test
    fun `should get boats for citizen`() {
        insertNewBoat(this.citizenIdLeo, "TestBoat1",)
        insertNewBoat(this.citizenIdLeo, "TestBoat2",)
        boatService.getBoatsForReserver(this.citizenIdLeo)
        assertEquals(2, boatService.getBoatsForReserver(this.citizenIdLeo).size, "Correct number of boats are fetched")
    }

    @Test
    fun `should delete boat`() {
        val addedBoat = insertNewBoat()
        val boatDeleted = boatService.deleteBoat(addedBoat.id)
        val boat = boatService.getBoat(addedBoat.id)
        assertEquals(true, boatDeleted, "Boat is deleted according to return value")
        assertEquals(timeProvider.getCurrentDateTime(), boat?.deletedAt, "Boat is deleted at current time")
    }

    @Test
    fun `should not delete a boat that is linked to a reservation`() {
        val newBoat = insertNewBoat()
        testUtils.createReservationInConfirmedState(
            CreateReservationParams(
                timeProvider,
                this.citizenIdLeo,
                1,
                newBoat.id
            )
        )
        val boatDeleted = boatService.deleteBoat(newBoat.id)
        val boat = boatService.getBoat(newBoat.id)
        assertEquals(false, boatDeleted, "Boat is not deleted according to return value")
        assertEquals(newBoat, boat, "Boat is not deleted")
    }

    @Test
    fun `should delete a boat that is linked to an expired reservation`() {
        val newBoat = insertNewBoat()
        testUtils.createReservationInConfirmedState(
            CreateReservationParams(
                timeProvider,
                this.citizenIdLeo,
                1,
                newBoat.id
            )
        )
        // go forth a year, the previous reservation has now expired
        mockTimeProvider(
            timeProvider,
            timeProvider.getCurrentDateTime().plusYears(1),
        )
        val boatDeleted = boatService.deleteBoat(newBoat.id)
        val boat = boatService.getBoat(newBoat.id)
        assertEquals(true, boatDeleted, "Boat is deleted according to return value")
        assertEquals(timeProvider.getCurrentDateTime(), boat?.deletedAt, "Boat is deleted at current time")
    }

    @Test
    fun `should get active boats for citizen`() {
        insertNewBoat(this.citizenIdLeo, "TestBoat1",)
        insertNewBoat(this.citizenIdLeo, "TestBoat2",)
        val deleteBoat = insertNewBoat(this.citizenIdLeo, "TestBoat3",)
        boatService.deleteBoat(deleteBoat.id)
        boatService.getBoatsForReserver(this.citizenIdLeo)
        assertEquals(2, boatService.getBoatsForReserver(this.citizenIdLeo).size, "Correct number of boats are fetched")
    }
}
