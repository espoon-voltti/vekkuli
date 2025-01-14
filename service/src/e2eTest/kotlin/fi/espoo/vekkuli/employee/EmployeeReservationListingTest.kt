package fi.espoo.vekkuli.employee

import com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat
import fi.espoo.vekkuli.PlaywrightTest
import fi.espoo.vekkuli.domain.BoatSpaceAmenity
import fi.espoo.vekkuli.domain.ReservationValidity
import fi.espoo.vekkuli.pages.*
import org.junit.jupiter.api.Test
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("test")
class EmployeeReservationListingTest : PlaywrightTest() {
    @Test
    fun `Employee can filter boat spaces`() {
        val listingPage = reservationListPage()
        page.waitForCondition { listingPage.reservations.count() == 5 }
        listingPage.boatSpaceTypeFilter("Winter").click()
        page.waitForCondition { listingPage.reservations.count() == 1 }
    }

    @Test
    fun `Employee can filter by reserver phone number`() {
        val listingPage = reservationListPage()
        page.waitForCondition { listingPage.reservations.count() == 5 }
        listingPage.searchInput("phoneSearch").fill("04056")
        listingPage.searchInput("phoneSearch").blur()
        page.waitForCondition { listingPage.reservations.count() == 1 }
        assertThat(listingPage.getByDataTestId("reserver-name").first()).containsText("Korhonen Leo")
    }

    @Test
    fun `Employee can filter by reservation validity`() {
        val listingPage = reservationListPage()
        page.waitForCondition { listingPage.reservations.count() == 5 }
        listingPage.reservationValidityFilter(ReservationValidity.FixedTerm.toString()).click()
        page.waitForCondition { listingPage.reservations.count() == 1 }
        assertThat(listingPage.getByDataTestId("place").first()).containsText("B 003")
    }

    @Test
    fun `Employee can filter by reserver exceptions`() {
        val listingPage = reservationListPage()
        page.waitForCondition { listingPage.reservations.count() == 5 }
        listingPage.exceptionsFilter.click()
        page.waitForCondition { listingPage.getByDataTestId("reserver-name").count() == 1 }
        assertThat(listingPage.getByDataTestId("reserver-name").first()).containsText("Pulkkinen Jorma")
    }

    @Test
    fun `Employee can filter by amenity`() {
        val listingPage = reservationListPage()
        page.waitForCondition { listingPage.reservations.count() == 5 }
        listingPage.expandingSelectionFilter("amenity").click()
        listingPage.amenityFilter(BoatSpaceAmenity.Trailer.name).click()
        page.waitForCondition { listingPage.reservations.count() == 1 }
        assertThat(listingPage.getByDataTestId("place").first()).containsText("B 015")
        listingPage.amenityFilter(BoatSpaceAmenity.Beam.name).click()
        page.waitForCondition { listingPage.reservations.count() == 5 }
    }

    private fun reservationListPage(): ReservationListPage {
        val employeeHome = EmployeeHomePage(page)
        employeeHome.employeeLogin()

        val listingPage = ReservationListPage(page)
        listingPage.navigateTo()
        return listingPage
    }
}
