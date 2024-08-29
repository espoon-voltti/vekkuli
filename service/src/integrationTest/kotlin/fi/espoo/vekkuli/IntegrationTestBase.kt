package fi.espoo.vekkuli

import fi.espoo.vekkuli.utils.createAndSeedDatabase
import org.jdbi.v3.core.Jdbi
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT
)
abstract class IntegrationTestBase {
    @Autowired
    protected lateinit var jdbi: Jdbi

    @BeforeAll
    fun beforeAllSuper() {
        createAndSeedDatabase(jdbi)
    }
}
