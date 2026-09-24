package fi.espoo.vekkuli.employee

import com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat
import fi.espoo.vekkuli.PlaywrightTest
import fi.espoo.vekkuli.pages.employee.CitizenDetailsPage
import fi.espoo.vekkuli.pages.employee.EmployeeHomePage
import fi.espoo.vekkuli.shared.CitizenIds
import org.junit.jupiter.api.Test
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("test")
class EditReservationEndDateTest : PlaywrightTest() {
    // Leo's only reservation, on Haukilahti B 001, runs 01.02.2024 - 31.01.2025.
    private val leoReservationId = 1

    // Olivia's expired 2022 reservation on Haukilahti B 003. The same space is invoiced
    // for 01.02.2023 - 31.12.2023 by the reservation right after it.
    private val oliviaExpiredReservationId = 4

    @Test
    fun `employee shortens the end date of an active reservation`() {
        val citizenDetailsPage = CitizenDetailsPage(page)
        EmployeeHomePage(page).employeeLogin()
        citizenDetailsPage.navigateToUserPage(CitizenIds.leo)

        assertThat(citizenDetailsPage.reservationEndDateValue).hasText("31.01.2025")

        citizenDetailsPage.openReservationEndDateModal(leoReservationId).click()
        citizenDetailsPage.reservationEndDateInput.fill("2024-10-31")
        citizenDetailsPage.reservationEndDateInput.blur()

        assertThat(citizenDetailsPage.reservationEndDateWarning.first()).isVisible()
        assertThat(citizenDetailsPage.reservationEndDateError).not().isVisible()

        citizenDetailsPage.reservationEndDateModalConfirm.click()
        assertThat(citizenDetailsPage.reservationEndDateSuccessModal).isVisible()

        citizenDetailsPage.navigateToUserPage(CitizenIds.leo)
        assertThat(citizenDetailsPage.reservationEndDateValue).hasText("31.10.2024")
    }

    @Test
    fun `employee can not extend a reservation over another reservation on the same space`() {
        val citizenDetailsPage = CitizenDetailsPage(page)
        EmployeeHomePage(page).employeeLogin()
        citizenDetailsPage.navigateToUserPage(CitizenIds.olivia)
        citizenDetailsPage.toggleExpiredReservationsAccordion()

        citizenDetailsPage.openReservationEndDateModal(oliviaExpiredReservationId).click()
        citizenDetailsPage.reservationEndDateInput.fill("2023-06-01")
        citizenDetailsPage.reservationEndDateInput.blur()

        assertThat(citizenDetailsPage.reservationEndDateError).isVisible()
        assertThat(citizenDetailsPage.reservationEndDateModalConfirm).isDisabled()
    }

    @Test
    fun `an expired reservation can be extended when the space is still free`() {
        val citizenDetailsPage = CitizenDetailsPage(page)
        EmployeeHomePage(page).employeeLogin()
        citizenDetailsPage.navigateToUserPage(CitizenIds.olivia)
        citizenDetailsPage.toggleExpiredReservationsAccordion()

        citizenDetailsPage.openReservationEndDateModal(oliviaExpiredReservationId).click()
        citizenDetailsPage.reservationEndDateInput.fill("2023-01-31")
        citizenDetailsPage.reservationEndDateInput.blur()

        assertThat(citizenDetailsPage.reservationEndDateError).not().isVisible()
        citizenDetailsPage.reservationEndDateModalConfirm.click()
        assertThat(citizenDetailsPage.reservationEndDateSuccessModal).isVisible()
    }
}
