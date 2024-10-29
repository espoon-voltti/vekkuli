package fi.espoo.vekkuli

import fi.espoo.vekkuli.domain.ReservationType
import fi.espoo.vekkuli.service.MemoService
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
class MemoServiceIntegrationTests : IntegrationTestBase() {
    @Autowired
    lateinit var memoService: MemoService

    @Test
    fun `should store and load notes`() {
        val memo = memoService.insertMemo(this.citizenIdLeo, userId, ReservationType.Marine, "Test note")
        assertNotNull(memo)
        val memos = memoService.getMemos(this.citizenIdLeo, ReservationType.Marine)
        val found = memos.find { it.id == memo.id }
        assertNotNull(found)
        assertEquals(memo, found)
    }

    @Test
    fun `should delete a note`() {
        val memo = memoService.insertMemo(this.citizenIdLeo, userId, ReservationType.Marine, "Test note")
        assertNotNull(memo)
        memoService.removeMemo(memo.id)
        assertNull(memoService.getMemo(memo.id))
    }

    @Test
    fun `should update a note`() {
        val memo = memoService.insertMemo(this.citizenIdLeo, userId, ReservationType.Marine, "Test note")
        assertNotNull(memo)
        memoService.updateMemo(memo.id, userId, "Updated note")
        val updated = memoService.getMemo(memo.id)
        assertNotNull(updated)
        assertEquals("Updated note", updated.content)
    }
}
