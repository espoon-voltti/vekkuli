package fi.espoo.vekkuli.tests

import fi.espoo.vekkuli.common.Forbidden
import fi.espoo.vekkuli.common.Unauthorized
import fi.espoo.vekkuli.domain.CitizenWithDetails
import fi.espoo.vekkuli.service.CitizenContextProvider
import fi.espoo.vekkuli.service.ContextCitizenAccessControl
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito
import org.mockito.kotlin.mock
import java.util.*
import kotlin.test.assertEquals

class ContextCitizenAccessControlTest {
    @Test
    fun `should throw Unauthorized when citizen is not logged in`() {
        val citizenContextProvider: CitizenContextProvider = mock()
        val citizenContext = ContextCitizenAccessControl(citizenContextProvider)
        Mockito.`when`(citizenContextProvider.getCurrentCitizen()).thenReturn(null)

        assertThrows<Unauthorized> {
            citizenContext.requireCitizen()
        }
    }

    @Test
    fun `should return citizen when one is logged in`() {
        val loggedInCitizen = mock<CitizenWithDetails>()
        val citizenContextProvider: CitizenContextProvider = mock()
        val citizenContext = ContextCitizenAccessControl(citizenContextProvider)
        Mockito.`when`(citizenContextProvider.getCurrentCitizen()).thenReturn(loggedInCitizen)

        val citizen = citizenContext.requireCitizen()

        assertEquals(loggedInCitizen, citizen)
    }

    @Test
    fun `should throw Forbidden when citizen id does not match`() {
        val loggedInCitizenId = UUID.fromString("509edb00-5549-11ef-a1c7-776e76028a49")
        val loggedInCitizen = mock<CitizenWithDetails>()
        val citizenContextProvider: CitizenContextProvider = mock()
        val citizenContext = ContextCitizenAccessControl(citizenContextProvider)
        Mockito.`when`(citizenContextProvider.getCurrentCitizen()).thenReturn(loggedInCitizen)
        Mockito.`when`(loggedInCitizen.id).thenReturn(loggedInCitizenId)

        assertThrows<Forbidden> {
            citizenContext.requireCitizenId(UUID.fromString("f5d377ea-5547-11ef-a1c7-7f2b94cf9afd"))
        }
    }

    @Test
    fun `should not throw when citizen id does match`() {
        val loggedInCitizenId = UUID.fromString("509edb00-5549-11ef-a1c7-776e76028a49")
        val loggedInCitizen = mock<CitizenWithDetails>()
        val citizenContextProvider: CitizenContextProvider = mock()
        val citizenContext = ContextCitizenAccessControl(citizenContextProvider)
        Mockito.`when`(citizenContextProvider.getCurrentCitizen()).thenReturn(loggedInCitizen)
        Mockito.`when`(loggedInCitizen.id).thenReturn(loggedInCitizenId)

        assertDoesNotThrow {
            citizenContext.requireCitizenId(loggedInCitizenId)
        }
    }
}
