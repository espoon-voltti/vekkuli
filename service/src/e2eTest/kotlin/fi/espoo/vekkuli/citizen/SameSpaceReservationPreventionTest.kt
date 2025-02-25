package fi.espoo.vekkuli.citizen

import com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat
import fi.espoo.vekkuli.PlaywrightTest
import fi.espoo.vekkuli.pages.citizen.BoatSpaceFormPage
import fi.espoo.vekkuli.pages.citizen.CitizenHomePage
import fi.espoo.vekkuli.pages.citizen.ReserveBoatSpacePage
import org.junit.jupiter.api.Test
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("test")
class SameSpaceReservationPreventionTest : PlaywrightTest() {
    @Test
    fun `Should not be allowed to reserve space that is being reserved by other citizen from reserve modal`() {
        browser.newContext().use { oliviaContext ->
            val leoPage = page
            val oliviaPage = oliviaContext.newPage()

            val leoReservationPage = ReserveBoatSpacePage(leoPage)
            val leoReserveModal = leoReservationPage.getReserveModal()
            val leoBoatSpaceFormPage = BoatSpaceFormPage(leoPage)
            val oliviaReservationPage = ReserveBoatSpacePage(oliviaPage)
            val oliviaReserveModal = oliviaReservationPage.getReserveModal()
            val oliviaBoatSpaceFormPage = BoatSpaceFormPage(oliviaPage)

            CitizenHomePage(oliviaPage).loginAsOliviaVirtanen()
            oliviaReservationPage.navigateToPage()
            oliviaReservationPage.startReservingBoatSpaceB314()

            CitizenHomePage(leoPage).loginAsLeoKorhonen()
            leoReservationPage.navigateToPage()
            leoReservationPage.startReservingBoatSpaceB314()

            leoReserveModal.reserveANewSpace.click()
            oliviaReserveModal.reserveANewSpace.click()

            assertThat(leoBoatSpaceFormPage.header).isVisible()
            assertThat(oliviaBoatSpaceFormPage.header).not().isVisible()
            assertThat(oliviaReservationPage.header).isVisible()
        }
    }

    @Test
    fun `Should not be allowed to switch space that is being reserved by other citizen from reserve modal`() {
        browser.newContext().use { oliviaContext ->
            val leoPage = page
            val oliviaPage = oliviaContext.newPage()

            val leoReservationPage = ReserveBoatSpacePage(leoPage)
            val leoReserveModal = leoReservationPage.getReserveModal()
            val leoBoatSpaceFormPage = BoatSpaceFormPage(leoPage)
            val oliviaReservationPage = ReserveBoatSpacePage(oliviaPage)
            val oliviaReserveModal = oliviaReservationPage.getReserveModal()
            val oliviaBoatSpaceFormPage = BoatSpaceFormPage(oliviaPage)

            CitizenHomePage(oliviaPage).loginAsOliviaVirtanen()
            oliviaReservationPage.navigateToPage()
            oliviaReservationPage.startReservingBoatSpaceB314()

            CitizenHomePage(leoPage).loginAsLeoKorhonen()
            leoReservationPage.navigateToPage()
            leoReservationPage.startReservingBoatSpaceB314()

            assertThat(leoReserveModal.firstSwitchReservationButton).isVisible()
            assertThat(oliviaReserveModal.firstSwitchReservationButton).isVisible()

            leoReserveModal.firstSwitchReservationButton.click()
            oliviaReserveModal.firstSwitchReservationButton.click()

            assertThat(leoBoatSpaceFormPage.header).isVisible()
            assertThat(oliviaBoatSpaceFormPage.header).not().isVisible()
            assertThat(oliviaReservationPage.header).isVisible()
        }
    }
}
