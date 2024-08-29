package fi.espoo.vekkuli

import fi.espoo.vekkuli.domain.BoatSpaceOption
import fi.espoo.vekkuli.domain.Harbor
import org.hamcrest.Matchers.*
import org.jdbi.v3.core.Jdbi
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.transaction.annotation.Transactional

@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class IntegrationTest {
    @Autowired
    private lateinit var restTemplate: TestRestTemplate

    @Autowired
    private lateinit var jdbi: Jdbi

    @Autowired
    private lateinit var mockMvc: MockMvc

    fun setup() {
        createAndSeedDatabase(jdbi)
    }

    @Test
    @Transactional
    fun filterBoatSpaces() {
        setup()
        // Perform GET request
        val response = restTemplate.getForEntity("/kuntalainen/partial/vapaat-paikat", String::class.java)
        response.statusCode.value().also { assertEquals(200, it) }
    }

    @Test
    fun `should return filtered unreserved boat spaces`() {
        mockMvc.get("/kuntalainen/partial/vapaat-paikat") {
            param("boatType", "OutboardMotor")
            param("width", "4.0")
            param("length", "8.0")
            param("harbor", "7")
        }
            .andExpect {
                status { isOk() }
                model { attributeExists("harbors") }
                model().attribute(
                    "harbors",
                    hasItem<Harbor>(
                        allOf(
                            hasProperty(
                                "boatSpaces",
                                hasItems<BoatSpaceOption>(
                                    hasProperty("id", `is`(2397)),
                                    hasProperty("id", `is`(2399)),
                                    hasProperty("id", `is`(2402))
                                )
                            )
                        )
                    )
                )
            }
    }
}
