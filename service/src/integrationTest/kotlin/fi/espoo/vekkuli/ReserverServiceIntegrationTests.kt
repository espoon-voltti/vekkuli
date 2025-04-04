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
import kotlin.test.assertNotNull

@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class ReserverServiceIntegrationTests : IntegrationTestBase() {
    @Autowired
    lateinit var reserverService: ReserverService

    @Test
    fun `should get correct citizen`() {
        val citizen = reserverService.getCitizen(this.citizenIdLeo)
        assertNotNull(citizen, "Citizen is not null")
        assertEquals(this.citizenIdLeo, citizen.id, "Citizen is correctly fetched")
    }

    @Test
    fun `should get citizen by full name`() {
        val citizens = reserverService.getCitizens("Virtanen Olivia")
        assertEquals(1, citizens.size, "Should find a citizen")
        assertEquals(citizenIdOlivia, citizens[0].id, "Citizen is correctly fetched")
    }

    @Test
    fun `should get citizen by last name`() {
        val citizens = reserverService.getCitizens("Virtanen")
        assertEquals(2, citizens.size, "Should find two citizens")
        assertNotNull(citizens.first { it.id == citizenIdOlivia }, "Citizen is correctly fetched")
    }

    @Test
    fun `should update citizen email and phone number`() {
        val updatedPhoneNumber = "123456789"
        val updatedEmail = "new@email.com"
        val updatedCitizen =
            reserverService.updateCitizen(UpdateCitizenParams(id = this.citizenIdLeo, phone = updatedPhoneNumber, email = updatedEmail))
        val citizen = reserverService.getCitizen(this.citizenIdLeo)
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
                espooRulesApplied = false,
                discountPercentage = 0,
                dataProtection = false,
                exceptionNotes = null
            )
        val updatedCitizen =
            reserverService.updateCitizen(
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
                    postOfficeSv = newCitizen.postOfficeSv,
                )
            )
        val citizen = reserverService.getCitizen(this.citizenIdLeo)
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
                espooRulesApplied = false,
                discountPercentage = 0,
                dataProtection = false,
                exceptionNotes = "Test notes"
            )
        reserverService.updateCitizen(
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
            reserverService.updateCitizen(
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

        val citizen = reserverService.getCitizen(this.citizenIdLeo)
        assertNotNull(updatedCitizen2)
        assertEquals(newCitizen2.municipalityName, citizen?.municipalityName, "Citizen's municipality is correctly updated")
        assertEquals(updatedCitizen2.municipalityName, citizen?.municipalityName, "Updated citizen is correctly returned")
    }

    @Test
    fun `should get all valid municipalities`() {
        val municipalities = reserverService.getMunicipalities()
        assertTrue(municipalities.isNotEmpty())
        assertEquals("Espoo", municipalities.first { it.code == 49 }.name)
    }

    @Test
    fun `should update citizen exceptions`() {
        val exceptionNotesTest = "Test exception"
        val updatedCitizen =
            reserverService.updateExceptions(
                this.citizenIdLeo,
                rulesApplied = true,
                exceptionNotes = exceptionNotesTest,
                discountPercentage = 10
            )

        val citizen = reserverService.getCitizen(this.citizenIdLeo)
        assertNotNull(updatedCitizen)
        assertEquals(true, citizen?.espooRulesApplied, "Citizen's Espoo rules are correctly updated")
        assertEquals(exceptionNotesTest, citizen?.exceptionNotes, "Citizen's exception notes are correctly updated")
        assertEquals(10, citizen?.discountPercentage, "Citizen's discount percentage is correctly updated")
    }
}
