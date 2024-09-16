package fi.espoo.vekkuli

import fi.espoo.vekkuli.domain.CitizenWithDetails
import fi.espoo.vekkuli.domain.MemoCategory
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
class CitizenServiceIntegrationTests : IntegrationTestBase() {
    @Autowired
    lateinit var citizenService: CitizenService

    @Test
    fun `should get correct reservation with citizen`() {
        val citizen = citizenService.getCitizen(citizenId)
        assertEquals(citizenId, citizen?.id, "Citizen is correctly fetched")
        assertEquals("Leo Korhonen", citizen?.fullName, "Citizen's first name is correctly fetched")
    }

    @Test
    fun `should update citizen email and phone number`() {
        val updatedPhoneNumber = "123456789"
        val updatedEmail = "new@email.com"
        val updatedCitizen =
            citizenService.updateCitizen(id = citizenId, phone = updatedPhoneNumber, email = updatedEmail)
        val citizen = citizenService.getCitizen(citizenId)
        assertNotNull(updatedCitizen)
        assertEquals(updatedPhoneNumber, citizen?.phone, "Citizen's phone is correctly updated")
        assertEquals(updatedEmail, citizen?.email, "Citizen's email is correctly updated")
        assertEquals(updatedCitizen, citizen, "Updated citizen is correctly returned")
    }

    @Test
    fun `should update all citizen fields`() {
        val newCitizen =
            CitizenWithDetails(
                id = citizenId,
                firstName = "Testi",
                lastName = "Testeri",
                phone = "123456789",
                email = "new@email.com",
                address = "New address",
                postalCode = "12345",
                municipalityCode = 49,
                nationalId = "123456-789A",
                municipalityName = "Espoo"
            )
        val updatedCitizen =
            citizenService.updateCitizen(
                id = citizenId,
                phone = newCitizen.phone,
                email = newCitizen.email,
                firstName = newCitizen.firstName,
                lastName = newCitizen.lastName,
                address = newCitizen.address,
                postalCode = newCitizen.postalCode,
                municipalityCode = newCitizen.municipalityCode,
                nationalId = newCitizen.nationalId
            )
        val citizen = citizenService.getCitizen(citizenId)
        assertNotNull(updatedCitizen)
        assertEquals(newCitizen.municipalityName, citizen?.municipalityName, "Citizen's municipality is correctly updated")
        assertEquals(newCitizen, citizen, "Citizen is correctly updated")
        assertEquals(updatedCitizen, citizen, "Updated citizen is correctly returned")
    }

    @Test
    fun `should update citizen multiple times`() {
        val newCitizen1 =
            CitizenWithDetails(
                id = citizenId,
                firstName = "Testi",
                lastName = "Testeri",
                phone = "123456789",
                email = "new@email.com",
                address = "New address",
                postalCode = "12345",
                municipalityCode = 49,
                nationalId = "123456-789A",
                municipalityName = "Espoo"
            )
        citizenService.updateCitizen(
            id = citizenId,
            phone = newCitizen1.phone,
            email = newCitizen1.email,
            firstName = newCitizen1.firstName,
            lastName = newCitizen1.lastName,
            address = newCitizen1.address,
            postalCode = newCitizen1.postalCode,
            municipalityCode = newCitizen1.municipalityCode,
            nationalId = newCitizen1.nationalId
        )

        val newCitizen2 =
            newCitizen1.copy(
                municipalityCode = 91,
                municipalityName = "Helsinki"
            )
        val updatedCitizen2 =
            citizenService.updateCitizen(
                id = citizenId,
                phone = newCitizen2.phone,
                email = newCitizen2.email,
                firstName = newCitizen2.firstName,
                lastName = newCitizen2.lastName,
                address = newCitizen2.address,
                postalCode = newCitizen2.postalCode,
                municipalityCode = newCitizen2.municipalityCode,
                nationalId = newCitizen2.nationalId
            )
        val citizen = citizenService.getCitizen(citizenId)
        assertNotNull(updatedCitizen2)
        assertEquals(newCitizen2.municipalityName, citizen?.municipalityName, "Citizen's municipality is correctly updated")
        assertEquals(updatedCitizen2.municipalityName, citizen?.municipalityName, "Updated citizen is correctly returned")
    }

    @Test
    fun `should store and load notes`() {
        val memo = citizenService.insertMemo(citizenId, userId, MemoCategory.Marine, "Test note")
        assertNotNull(memo)
        val memos = citizenService.getMemos(citizenId, MemoCategory.Marine)
        val found = memos.find { it.id == memo.id }
        assertNotNull(found)
        assertEquals(memo, found)
    }

    @Test
    fun `should delete a note`() {
        val memo = citizenService.insertMemo(citizenId, userId, MemoCategory.Marine, "Test note")
        assertNotNull(memo)
        citizenService.removeMemo(memo.id)
        assertNull(citizenService.getMemo(memo.id))
    }

    @Test
    fun `should update a note`() {
        val memo = citizenService.insertMemo(citizenId, userId, MemoCategory.Marine, "Test note")
        assertNotNull(memo)
        citizenService.updateMemo(memo.id, userId, "Updated note")
        val updated = citizenService.getMemo(memo.id)
        assertNotNull(updated)
        assertEquals("Updated note", updated.content)
    }

    @Test
    fun `should get all valid municipalities`() {
        val municipalities = citizenService.getMunicipalities()
        assertTrue(municipalities.isNotEmpty())
        assertEquals("Espoo", municipalities.first { it.code == 49 }.name)
    }
}
