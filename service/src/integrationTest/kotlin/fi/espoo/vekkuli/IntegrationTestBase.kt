package fi.espoo.vekkuli

import fi.espoo.vekkuli.utils.TimeProvider
import fi.espoo.vekkuli.utils.createAndSeedDatabase
import org.jdbi.v3.core.Jdbi
import org.junit.jupiter.api.*
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDateTime
import java.util.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT
)
@ActiveProfiles("test")
abstract class IntegrationTestBase {
    @Autowired
    protected lateinit var jdbi: Jdbi

    val citizenId: UUID = UUID.fromString("f5d377ea-5547-11ef-a1c7-7f2b94cf9afd")
    val organizationId: UUID = UUID.fromString("8b220a43-86a0-4054-96f6-d29a5aba17e7")

    val userId: UUID = UUID.fromString("94833b54-132b-4ab8-b841-60df45809b3e")

    @MockBean
    lateinit var timeProvider: TimeProvider

    @BeforeEach
    fun setUp() {
        fun mockDateTime(date: LocalDateTime = LocalDateTime.of(2024, 4, 1, 22, 22, 22)) {
            Mockito.`when`(timeProvider.getCurrentDate()).thenReturn(date)
        }
        mockDateTime()
    }

    @BeforeAll
    fun beforeAllSuper() {
        createAndSeedDatabase(jdbi)
    }

    @BeforeEach
    fun resetDatabase() {
        // Override this method in subclasses to reset the database before each test
    }
}
