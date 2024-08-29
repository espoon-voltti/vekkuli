package fi.espoo.vekkuli

import fi.espoo.vekkuli.domain.BoatSpaceOption
import fi.espoo.vekkuli.domain.Harbor
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.model

@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class IntegrationTest : IntegrationTestBase() {
    @Autowired
    private lateinit var mockMvc: MockMvc

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
