package fi.espoo.vekkuli

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
}
