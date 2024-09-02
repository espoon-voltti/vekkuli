package fi.espoo.vekkuli

import fi.espoo.vekkuli.service.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class CitizenServiceIntegrationTests : IntegrationTestBase() {
    @Autowired
    lateinit var citizenService: CitizenService

    @Test
    fun `should get correct reservation with citizen`() {
        val citizen = citizenService.getCitizen(citizenId)
        assertEquals(citizenId, citizen?.id, "Citizen is correctly fetched")
        assertEquals("Leo Korhonen", citizen?.fullName, "Citizen's first name is correctly fetched")
    }
}
