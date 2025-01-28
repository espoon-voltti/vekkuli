package fi.espoo.vekkuli.citizen

import com.microsoft.playwright.Page
import com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat
import fi.espoo.vekkuli.PlaywrightTest
import fi.espoo.vekkuli.pages.employee.CitizenDetailsPage
import fi.espoo.vekkuli.pages.employee.EmployeeHomePage
import fi.espoo.vekkuli.pages.employee.ReservationListPage
import fi.espoo.vekkuli.service.SendEmailServiceMock
import org.junit.jupiter.api.BeforeEach

open class ReserveTest : PlaywrightTest() {
    @BeforeEach
    fun setUp() {
        SendEmailServiceMock.resetEmails()
    }

    protected fun setDiscountForReserver(
        page: Page,
        reserverName: String,
        discount: Int,
        doLogin: Boolean = true
    ) {
        val listingPage = reservationListPage(doLogin)
        listingPage
            .getByDataTestId("reserver-name")
            .getByText(reserverName)
            .first()
            .click()
        val citizenDetails = CitizenDetailsPage(page)
        citizenDetails.exceptionsNavi.click()
        val discount0 = page.getByTestId("reserver_discount_0")
        assertThat(discount0).isChecked()
        assertThat(citizenDetails.getByDataTestId("exceptions-tab-attention")).hasClass("attention")
        page.getByTestId("reserver_discount_$discount").check()
    }

    private fun reservationListPage(doLogin: Boolean = true): ReservationListPage {
        val employeeHome = EmployeeHomePage(page)
        if (doLogin) {
            employeeHome.employeeLogin()
        }

        val listingPage = ReservationListPage(page)
        listingPage.navigateTo()
        return listingPage
    }
}
