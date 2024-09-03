package fi.espoo.vekkuli

import fi.espoo.vekkuli.utils.createAndSeedDatabase
import org.jdbi.v3.core.Jdbi
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
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

    @BeforeAll
    fun beforeAllSuper() {
        createAndSeedDatabase(jdbi)
    }

    @BeforeEach
    fun resetDatabase() {
        // Override this method in subclasses to reset the database before each test
    }
}
