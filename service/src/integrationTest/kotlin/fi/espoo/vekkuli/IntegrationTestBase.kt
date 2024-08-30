package fi.espoo.vekkuli

import fi.espoo.vekkuli.service.BoatReservationService
import fi.espoo.vekkuli.utils.createAndSeedDatabase
import org.jdbi.v3.core.Jdbi
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT
)
@ActiveProfiles("test")
abstract class IntegrationTestBase {
    @Autowired
    protected lateinit var jdbi: Jdbi

    @Autowired
    lateinit var reservationService: BoatReservationService

    @BeforeAll
    fun beforeAllSuper() {
        createAndSeedDatabase(jdbi)
    }

    @BeforeEach
    fun resetDatabase() {
        reservationService.deleteAllReservations()
    }
}
