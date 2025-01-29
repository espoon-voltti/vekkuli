package fi.espoo.vekkuli

import fi.espoo.vekkuli.repository.TrailerRepository
import fi.espoo.vekkuli.service.OrganizationService
import fi.espoo.vekkuli.service.PermissionService
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.util.*
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class PermissionServiceTests : IntegrationTestBase() {
    @Autowired
    private lateinit var organizationService: OrganizationService

    @Autowired
    private lateinit var permissionService: PermissionService

    @Autowired
    private lateinit var trailerRepository: TrailerRepository

    @Test
    fun `should allow user to edit trailer`() {
        val trailerId = insertTrailer(citizenIdMikko)
        assertTrue(permissionService.canEditTrailer(userId, trailerId))
    }

    @Test
    fun `should prevent non owners from editing trailer`() {
        val trailerId = insertTrailer(citizenIdMikko)
        assertFalse(permissionService.canEditTrailer(citizenIdOlivia, trailerId))
        assertFalse(permissionService.canEditTrailer(citizenIdOlivia, citizenIdMikko))
    }

    @Test
    fun `should allow owner to edit trailer`() {
        val trailerId = insertTrailer(citizenIdMikko)
        assertTrue(permissionService.canEditTrailer(citizenIdMikko, trailerId))
        assertTrue(permissionService.canEditTrailer(citizenIdMikko, citizenIdMikko))
    }

    @Test
    fun `should prevent non organization members from editing trailer`() {
        val orgId = insertOrganization(citizenIdMikko)
        val trailerId = insertTrailer(orgId)
        assertFalse(permissionService.canEditTrailer(citizenIdOlivia, trailerId))
        assertFalse(permissionService.canEditTrailer(citizenIdOlivia, orgId))
    }

    @Test
    fun `should allow organization members to edit trailer`() {
        val orgId = insertOrganization(citizenIdMikko)
        val trailerId = insertTrailer(orgId)
        assertTrue(permissionService.canEditTrailer(citizenIdMikko, trailerId))
        assertTrue(permissionService.canEditTrailer(citizenIdMikko, orgId))
    }

    private fun insertTrailer(reserverId: UUID): Int =
        trailerRepository
            .insertTrailer(
                reserverId,
                "registrationCode",
                1,
                2
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
