package fi.espoo.vekkuli

import fi.espoo.vekkuli.domain.CitizenWithDetails
import fi.espoo.vekkuli.repository.UpdateCitizenParams
import fi.espoo.vekkuli.service.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.util.UUID
import kotlin.test.assertNotNull

@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class CitizenServiceIntegrationTests : IntegrationTestBase() {
    @Autowired
    lateinit var citizenService: CitizenService

    private val oliviaCitizenId = UUID.fromString("509edb00-5549-11ef-a1c7-776e76028a49")

    @Test
    fun `should get correct citizen`() {
        val citizen = citizenService.getCitizen(this.citizenIdLeo)
        assertNotNull(citizen, "Citizen is not null")
        assertEquals(this.citizenIdLeo, citizen.id, "Citizen is correctly fetched")
    }

    @Test
    fun `should get citizen by full name`() {
        val citizens = citizenService.getCitizens("Olivia Virtanen")
        assertEquals(1, citizens.size, "Should find a citizen")
        assertEquals(oliviaCitizenId, citizens[0].id, "Citizen is correctly fetched")
    }

    @Test
    fun `should get citizen by first name`() {
        val citizens = citizenService.getCitizens("olivia")
        assertEquals(1, citizens.size, "Should find a citizen")
        assertEquals(oliviaCitizenId, citizens[0].id, "Citizen is correctly fetched")
    }

    @Test
    fun `should get citizen by last name`() {
        val citizens = citizenService.getCitizens("Virtanen")
        assertEquals(2, citizens.size, "Should find two citizens")
        assertNotNull(citizens.first { it.id == oliviaCitizenId }, "Citizen is correctly fetched")
    }

    @Test
    fun `should update citizen email and phone number`() {
        val updatedPhoneNumber = "123456789"
        val updatedEmail = "new@email.com"
        val updatedCitizen =
            citizenService.updateCitizen(UpdateCitizenParams(id = this.citizenIdLeo, phone = updatedPhoneNumber, email = updatedEmail))
        val citizen = citizenService.getCitizen(this.citizenIdLeo)
        assertNotNull(updatedCitizen)
        assertEquals(updatedPhoneNumber, citizen?.phone, "Citizen's phone is correctly updated")
        assertEquals(updatedEmail, citizen?.email, "Citizen's email is correctly updated")
        assertEquals(updatedCitizen, citizen, "Updated citizen is correctly returned")
    }

    @Test
    fun `should update all citizen fields`() {
        val newCitizen =
            CitizenWithDetails(
                id = this.citizenIdLeo,
                firstName = "Testi",
                lastName = "Testeri",
                phone = "123456789",
                email = "new@email.com",
                streetAddress = "New address",
                postalCode = "12345",
                municipalityCode = 49,
                nationalId = "123456-789A",
                municipalityName = "Espoo",
                postOffice = "Espoo",
                postOfficeSv = "Esbo",
                streetAddressSv = "",
            )
        val updatedCitizen =
            citizenService.updateCitizen(
                UpdateCitizenParams(
                    id = this.citizenIdLeo,
                    phone = newCitizen.phone,
                    email = newCitizen.email,
                    firstName = newCitizen.firstName,
                    lastName = newCitizen.lastName,
                    streetAddress = newCitizen.streetAddress,
                    postalCode = newCitizen.postalCode,
                    municipalityCode = newCitizen.municipalityCode,
                    nationalId = newCitizen.nationalId,
                    streetAddressSv = newCitizen.streetAddressSv,
                    postOffice = newCitizen.postOffice,
                    postOfficeSv = newCitizen.postOfficeSv
                )
            )
        val citizen = citizenService.getCitizen(this.citizenIdLeo)
        assertNotNull(updatedCitizen)
        assertEquals(newCitizen.municipalityName, citizen?.municipalityName, "Citizen's municipality is correctly updated")
        assertEquals(newCitizen, citizen, "Citizen is correctly updated")
        assertEquals(updatedCitizen, citizen, "Updated citizen is correctly returned")
    }

    @Test
    fun `should update citizen multiple times`() {
        val newCitizen1 =
            CitizenWithDetails(
                id = this.citizenIdLeo,
                firstName = "Testi",
                lastName = "Testeri",
                phone = "123456789",
                email = "new@email.com",
                streetAddress = "New address",
                postalCode = "12345",
                municipalityCode = 49,
                nationalId = "123456-789A",
                municipalityName = "Espoo",
                postOffice = "Espoo",
                postOfficeSv = "Esbo",
                streetAddressSv = "",
            )
        citizenService.updateCitizen(
            UpdateCitizenParams(
                id = this.citizenIdLeo,
                phone = newCitizen1.phone,
                email = newCitizen1.email,
                firstName = newCitizen1.firstName,
                lastName = newCitizen1.lastName,
                streetAddress = newCitizen1.streetAddress,
                postalCode = newCitizen1.postalCode,
                municipalityCode = newCitizen1.municipalityCode,
                nationalId = newCitizen1.nationalId,
                streetAddressSv = newCitizen1.streetAddressSv,
                postOffice = newCitizen1.postOffice,
                postOfficeSv = newCitizen1.postOfficeSv
            )
        )

        val newCitizen2 =
            newCitizen1.copy(
                municipalityCode = 91,
                municipalityName = "Helsinki"
            )
        val updatedCitizen2 =
            citizenService.updateCitizen(
                UpdateCitizenParams(
                    id = this.citizenIdLeo,
                    phone = newCitizen2.phone,
                    email = newCitizen2.email,
                    firstName = newCitizen2.firstName,
                    lastName = newCitizen2.lastName,
                    streetAddress = newCitizen2.streetAddress,
                    postalCode = newCitizen2.postalCode,
                    municipalityCode = newCitizen2.municipalityCode,
                    nationalId = newCitizen2.nationalId,
                    streetAddressSv = newCitizen2.streetAddressSv,
                    postOffice = newCitizen2.postOffice,
                    postOfficeSv = newCitizen2.postOfficeSv
                )
            )

        val citizen = citizenService.getCitizen(this.citizenIdLeo)
        assertNotNull(updatedCitizen2)
        assertEquals(newCitizen2.municipalityName, citizen?.municipalityName, "Citizen's municipality is correctly updated")
        assertEquals(updatedCitizen2.municipalityName, citizen?.municipalityName, "Updated citizen is correctly returned")
    }

    @Test
    fun `should get all valid municipalities`() {
        val municipalities = citizenService.getMunicipalities()
        assertTrue(municipalities.isNotEmpty())
        assertEquals("Espoo", municipalities.first { it.code == 49 }.name)
    }
}
