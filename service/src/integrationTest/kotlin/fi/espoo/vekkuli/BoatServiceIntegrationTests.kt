package fi.espoo.vekkuli

import fi.espoo.vekkuli.domain.Boat
import fi.espoo.vekkuli.domain.BoatType
import fi.espoo.vekkuli.domain.OwnershipStatus
import fi.espoo.vekkuli.service.BoatReservationService
import fi.espoo.vekkuli.service.BoatService
import fi.espoo.vekkuli.service.OrganizationService
import fi.espoo.vekkuli.utils.mockTimeProvider
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
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
    private lateinit var organizationService: OrganizationService

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

        assertThrows<IllegalStateException> {
            boatService.deleteBoat(newBoat.id)
        }

        val boat = boatService.getBoat(newBoat.id)
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
        // go forth a year and one day, the previous reservation has now expired
        mockTimeProvider(
            timeProvider,
            timeProvider.getCurrentDateTime().plusYears(1).plusDays(1),
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

    @Test
    fun `should get active boats for organizations`() {
        val orgId = insertOrganization(citizenIdMikko)
        val boatToBeDeleted = insertBoat(orgId)
        insertBoat(orgId)
        insertBoat(orgId)

        boatService.deleteBoat(boatToBeDeleted)

        val boats = boatService.getBoatsForReserversOrganizations(citizenIdMikko)
        assertEquals(1, boats.size, "Correct number of organizations are fetched")
        assertEquals(2, boats[orgId.toString()]?.size, "Correct number of boats are fetched")
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

    private fun insertOrganization(memberCitizenId: UUID): UUID {
        val result =
            organizationService
                .insertOrganization(
                    businessId = "1234567890",
                    name = "TestOrganization",
                    phone = "1234567890",
                    email = "test@test.com",
                    streetAddress = "",
                    streetAddressSv = "",
                    postalCode = "",
                    postOffice = "",
                    postOfficeSv = "",
                    municipalityCode = 1,
                    billingName = "",
                    billingStreetAddress = "",
                    billingPostalCode = "",
                    billingPostOffice = ""
                ).id

        organizationService.addCitizenToOrganization(result, memberCitizenId)

        return result
    }
}
