package fi.espoo.vekkuli.citizen

import com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat
import fi.espoo.vekkuli.PlaywrightTest
import fi.espoo.vekkuli.pages.citizen.*
import org.junit.jupiter.api.Test
import org.springframework.test.context.ActiveProfiles
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@ActiveProfiles("test")
class ReservationFormNavigationTest : PlaywrightTest() {
    @Test
    fun `opening reservation form should not trigger a full page load`() {
        val homePage = CitizenHomePage(page)
        homePage.navigateToPage()

        assertNoFullPageReload {
            homePage.openFormButton.click()
            assertThat(ReserveBoatSpacePage(page).header).isVisible()
        }
    }

    @Test
    fun `selecting boat space from search results should not trigger a full page load`() {
        CitizenHomePage(page).loginAsMikkoVirtanen()

        val reserveBoatSpacePage = ReserveBoatSpacePage(page)
        reserveBoatSpacePage.navigateToPage()

        assertNoFullPageReload {
            reserveBoatSpacePage.startReservingBoatSpaceB314()
            assertThat(BoatSpaceFormPage(page).header).isVisible()
        }
    }

    @Test
    fun `selecting boat space from reserve modal should not trigger a full page load`() {
        CitizenHomePage(page).loginAsLeoKorhonen()

        val reserveBoatSpacePage = ReserveBoatSpacePage(page)
        reserveBoatSpacePage.navigateToPage()
        reserveBoatSpacePage.startReservingBoatSpaceB314()

        val reserveModal = reserveBoatSpacePage.getReserveModal()
        assertThat(reserveModal.root).isVisible()

        assertNoFullPageReload {
            reserveModal.reserveAnotherButton.click()
            assertThat(BoatSpaceFormPage(page).header).isVisible()
        }
    }

    @Test
    fun `filling boat space reservation should not trigger a full page load`() {
        CitizenHomePage(page).loginAsMikkoVirtanen()

        val reserveBoatSpacePage = ReserveBoatSpacePage(page)
        reserveBoatSpacePage.navigateToPage()
        reserveBoatSpacePage.startReservingBoatSpaceB314()

        assertNoFullPageReload {
            BoatSpaceFormPage(page).fillFormAndSubmit()
            assertThat(PaymentPage(page).header).isVisible()
        }
    }

    @Test
    fun `selecting boat space for switching should not trigger a full page load`() {
        CitizenHomePage(page).loginAsLeoKorhonen()

        val reserveBoatSpacePage = ReserveBoatSpacePage(page)
        reserveBoatSpacePage.navigateToPage()
        reserveBoatSpacePage.startReservingBoatSpaceB314()

        val reserveModal = reserveBoatSpacePage.getReserveModal()
        assertThat(reserveModal.root).isVisible()

        assertNoFullPageReload {
            reserveModal.firstSwitchReservationButton.click()
            assertThat(BoatSpaceFormPage(page).header).isVisible()
        }
    }

    @Test
    fun `filling switch form for more expensive boat space should not trigger a full page load`() {
        CitizenHomePage(page).loginAsLeoKorhonen()

        val reserveBoatSpacePage = ReserveBoatSpacePage(page)
        reserveBoatSpacePage.navigateToPage()
        reserveBoatSpacePage.startReservingBoatSpaceB314()

        val reserveModal = reserveBoatSpacePage.getReserveModal()
        reserveModal.firstSwitchReservationButton.click()

        assertNoFullPageReload {
            SwitchSpaceFormPage(page).fillFormAndSubmit()
            assertThat(PaymentPage(page).header).isVisible()
        }
    }

    @Test
    fun `filling switch form for cheaper boat space should not trigger a full page load`() {
        CitizenHomePage(page).loginAsLeoKorhonen()

        val reserveBoatSpacePage = ReserveBoatSpacePage(page)
        reserveBoatSpacePage.navigateToPage()
        reserveBoatSpacePage.startReservingBoatSpaceB059()

        val reserveModal = reserveBoatSpacePage.getReserveModal()
        reserveModal.firstSwitchReservationButton.click()

        assertNoFullPageReload {
            SwitchSpaceFormPage(page).fillFormAndSubmit()
            assertThat(PaymentPage(page).reservationSuccessNotification).isVisible()
        }
    }

    @Test
    fun `opening boat space search should redirect to unfilled reservation`() {
        CitizenHomePage(page).loginAsMikkoVirtanen()

        val reserveBoatSpacePage = ReserveBoatSpacePage(page)
        reserveBoatSpacePage.navigateToPage()
        reserveBoatSpacePage.startReservingBoatSpaceB314()

        assertThat(BoatSpaceFormPage(page).header).isVisible()

        reserveBoatSpacePage.navigateToPage()
        assertThat(BoatSpaceFormPage(page).header).isVisible()
    }

    @Test
    fun `navigating to boat space search should redirect to unfilled reservation`() {
        CitizenHomePage(page).loginAsMikkoVirtanen()

        val reserveBoatSpacePage = ReserveBoatSpacePage(page)
        reserveBoatSpacePage.navigateToPage()
        reserveBoatSpacePage.startReservingBoatSpaceB314()

        assertThat(BoatSpaceFormPage(page).header).isVisible()

        assertNoFullPageReload {
            CitizenHomePage(page).boatSearchLink.click()
            assertThat(BoatSpaceFormPage(page).header).isVisible()
        }
    }

    @Test
    fun `opening boat space search should redirect to unpaid reservation`() {
        CitizenHomePage(page).loginAsMikkoVirtanen()

        val reserveBoatSpacePage = ReserveBoatSpacePage(page)
        reserveBoatSpacePage.navigateToPage()
        reserveBoatSpacePage.startReservingBoatSpaceB314()

        BoatSpaceFormPage(page).fillFormAndSubmit()
        assertThat(PaymentPage(page).header).isVisible()

        reserveBoatSpacePage.navigateToPage()
        assertThat(PaymentPage(page).header).isVisible()
    }

    private fun assertFullPageReload(operation: () -> Unit) {
        assertTrue(trackPageReload(operation), "navigation should happen with full reload")
    }

    private fun assertNoFullPageReload(operation: () -> Unit) {
        assertFalse(trackPageReload(operation), "navigation should happen without full reload")
    }

    private fun trackPageReload(callback: () -> Unit): Boolean {
        var pageReloaded = false
        page.onLoad { pageReloaded = true }

        callback()

        return pageReloaded
    }
}
