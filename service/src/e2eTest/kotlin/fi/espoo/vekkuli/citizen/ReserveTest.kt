package fi.espoo.vekkuli.citizen

import com.microsoft.playwright.Page
import com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat
import fi.espoo.vekkuli.PlaywrightTest
import fi.espoo.vekkuli.domain.PaymentStatus
import fi.espoo.vekkuli.pages.employee.CitizenDetailsPage
import fi.espoo.vekkuli.pages.employee.EmployeeHomePage
import fi.espoo.vekkuli.pages.employee.ReservationListPage
import fi.espoo.vekkuli.service.SendEmailServiceMock
import fi.espoo.vekkuli.service.paymentStatusToText
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

    protected fun assertCorrectPaymentForReserver(
        citizenName: String,
        status: PaymentStatus,
        placeName: String,
        amount: String,
        expectedPaymentReference: String
    ) {
        // verity that there's a payment row in reserver's info in employee view
        // todo: citizen and organization pages should have a common base class
        val citizenDetails = citizenPageInEmployeeView(citizenName)
        citizenDetails.paymentsNavi.click()
        assertThat(citizenDetails.paymentsTable).isVisible()
        val paymentRows = citizenDetails.paymentsTable.locator("tbody tr").all()

        val matchingRow =
            paymentRows.find { row ->
                val paymentStatus = citizenDetails.getByDataTestId("payment-status", row).textContent()
                val place = citizenDetails.getByDataTestId("place", row).textContent()
                val paymentAmount = citizenDetails.getByDataTestId("payment-amount", row).textContent()
                val paymentReference = citizenDetails.getByDataTestId("payment-reference", row).textContent()
                paymentStatus == paymentStatusToText(status.name) &&
                    place == placeName &&
                    paymentAmount == amount &&
                    paymentReference == expectedPaymentReference
            }

        assertThat(matchingRow).hasCount(1)
    }

    protected fun citizenPageInEmployeeView(
        reserverName: String,
        doLogin: Boolean = true
    ): CitizenDetailsPage {
        val listingPage = reservationListPage(doLogin)
        listingPage
            .getByDataTestId("reserver-name")
            .getByText(reserverName)
            .first()
            .click()
        return CitizenDetailsPage(page)
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
