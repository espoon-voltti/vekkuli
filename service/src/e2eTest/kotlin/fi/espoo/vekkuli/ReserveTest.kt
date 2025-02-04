package fi.espoo.vekkuli

import com.microsoft.playwright.Page
import com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat
import fi.espoo.vekkuli.domain.PaymentStatus
import fi.espoo.vekkuli.pages.employee.CitizenDetailsPage
import fi.espoo.vekkuli.pages.employee.EmployeeHomePage
import fi.espoo.vekkuli.pages.employee.ReservationListPage
import fi.espoo.vekkuli.service.SendEmailServiceMock
import fi.espoo.vekkuli.service.paymentStatusToText
import fi.espoo.vekkuli.utils.fullDateFormat
import kotlin.test.assertEquals
import kotlin.test.assertTrue

open class ReserveTest : PlaywrightTest() {
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
        reference: String,
        paidDate: String? = timeProvider.getCurrentDate().format(fullDateFormat),
        doLogin: Boolean = true
    ) {
        // verity that there's a payment row in reserver's info in employee view
        // todo: citizen and organization pages should have a common base class
        val citizenDetails = citizenPageInEmployeeView(citizenName, doLogin)
        citizenDetails.paymentsNavi.click()
        assertThat(citizenDetails.paymentsTable).isVisible()
        val paymentRows = citizenDetails.paymentsTable.locator("tbody tr").all()

        val matchingRow =
            paymentRows.find { row ->
                val paymentStatus = citizenDetails.getByDataTestId("payment-status", row).textContent()
                val place = citizenDetails.getByDataTestId("place", row).textContent()
                val paymentAmount = citizenDetails.getByDataTestId("payment-amount", row).textContent()
                val paymentReference = citizenDetails.getByDataTestId("payment-reference", row).textContent()
                val paymentPaidDate = citizenDetails.getByDataTestId("payment-paid-date", row).textContent()
                paymentStatus == paymentStatusToText(status.name) &&
                    place == placeName &&
                    paymentAmount == amount &&
                    paidDate == paymentPaidDate &&
                    paymentReference == reference
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

    protected fun assertZeroEmailsSent() {
        messageService.sendScheduledEmails()
        assertEquals(0, SendEmailServiceMock.emails.size)
    }

    protected fun assertOnlyOneConfirmationEmailIsSent(
        emailAddress: String? = "test@example.com",
        emailSubject: String? = "Vahvistus Espoon kaupungin venepaikan varauksesta"
    ) {
        messageService.sendScheduledEmails()
        assertEquals(1, SendEmailServiceMock.emails.size)
        assertTrue(
            SendEmailServiceMock.emails
                .get(
                    0
                ).contains("$emailAddress with subject $emailSubject")
        )
    }
}
