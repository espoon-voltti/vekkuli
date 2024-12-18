package fi.espoo.vekkuli

import fi.espoo.vekkuli.service.OrganizationService
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
class OrganizationControllerService1IntegrationTests : IntegrationTestBase() {
    @Autowired
    lateinit var organizationService: OrganizationService

    private val pursiseuraId = UUID.fromString("8b220a43-86a0-4054-96f6-d29a5aba17e7")

    @BeforeEach
    override fun resetDatabase() {
        deleteAllOrganizationMembers(jdbi)
    }

    @Test
    fun `should add a member`() {
        organizationService.addCitizenToOrganization(pursiseuraId, citizenIdOlivia)
        organizationService.getOrganizationMembers(pursiseuraId).let { members ->
            assertEquals(1, members.size, "Should have one member")
            assertEquals(citizenIdOlivia, members[0].id, "Correct member is added")
        }
    }

    @Test
    fun `should remove a member`() {
        organizationService.addCitizenToOrganization(pursiseuraId, citizenIdOlivia)
        organizationService.addCitizenToOrganization(pursiseuraId, citizenIdLeo)
        organizationService.removeCitizenFromOrganization(pursiseuraId, citizenIdOlivia)
        organizationService.getOrganizationMembers(pursiseuraId).let { members ->
            assertEquals(1, members.size, "Should have one member")
            assertEquals(citizenIdLeo, members[0].id, "Correct member is left")
        }
    }

    @Test
    fun `should list citizens organizations`() {
        val org =
            organizationService.insertOrganization(
                "123456-7",
                "",
                "",
                "",
                "",
                "Hauska ry",
                "12345678",
                "hauska@noreplytest.fi",
                "Testikatu 1",
                "Testikatu 1",
                "12345",
                "Espoo",
                "Espoo",
                49,
            )
        organizationService.addCitizenToOrganization(org.id, citizenIdOlivia)
        organizationService.addCitizenToOrganization(pursiseuraId, citizenIdOlivia)
        organizationService.getCitizenOrganizations(citizenIdOlivia).let { orgs ->
            assertEquals(2, orgs.size, "Should have two organizations")
            orgs.map { it.name }.sorted().let { names ->
                assertEquals("Espoon Pursiseura", names[0], "Correct organization is listed")
                assertEquals("Hauska ry", names[1], "Correct organization is listed")
            }
        }
    }
}
