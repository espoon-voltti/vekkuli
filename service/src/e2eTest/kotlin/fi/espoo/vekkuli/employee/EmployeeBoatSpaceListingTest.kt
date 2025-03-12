package fi.espoo.vekkuli.employee

import com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat
import fi.espoo.vekkuli.PlaywrightTest
import fi.espoo.vekkuli.pages.employee.BoatSpaceListPage
import fi.espoo.vekkuli.pages.employee.EmployeeHomePage
import fi.espoo.vekkuli.shared.CitizenIds
import fi.espoo.vekkuli.shared.OrganizationIds
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("test")
class EmployeeBoatSpaceListingTest : PlaywrightTest() {
    @Test
    fun `Employee can filter boat spaces`() {
        val listingPage = boatSpaceListPage()
        listingPage.boatStateFilter("Inactive").click()
        page.waitForCondition { listingPage.listItems.count() == 4 }
    }

    @Test
    fun `reservations list should shield against XSS scripts from citizen information`() {
        try {
            EmployeeHomePage(page).employeeLogin()

            val listingPage = BoatSpaceListPage(page)

            // Inject XSS scripts to citizen information from citizen details page and return assertions
            val assertions = injectXSSToCitizenInformation(page, CitizenIds.leo)

            listingPage.navigateTo()
            // For some reason the page doesn't automatically reload the updated data
            page.reload()
            page.waitForCondition { listingPage.listItems.count() >= 5 }

            assertions()
        } catch (e: AssertionError) {
            handleError(e)
        }
    }

    @Test
    fun `reservations list should shield against XSS scripts from organization information`() {
        try {
            EmployeeHomePage(page).employeeLogin()

            val listingPage = BoatSpaceListPage(page)

            // Inject XSS scripts to citizen information from citizen details page and return assertions
            val assertions = injectXSSToOrganizationInformation(page, OrganizationIds.espoonPursiseura)

            listingPage.navigateTo()
            // For some reason the page doesn't automatically reload the updated data
            page.reload()
            page.waitForCondition { listingPage.listItems.count() >= 5 }

            assertions()
        } catch (e: AssertionError) {
            handleError(e)
        }
    }

    @ParameterizedTest
    @CsvSource(
        "sortBy, XSS_ATTACK_SORT_BY",
        "ascending, XSS_ATTACK_ASCENDING",
        "harbor, XSS_ATTACK_HARBOR",
        "sectionFilter, XSS_ATTACK_SECTION_FILTER",
        "boatSpaceType, XSS_ATTACK_BOAT_SPACE_TYPE",
        "boatSpaceState, XSS_ATTACK_BOAT_SPACE_STATE",
        "validity, XSS_ATTACK_VALIDITY",
    )
    fun `reservations list should shield against XSS reflection scripts from parameters`(
        parameter: String,
        maliciousValue: String
    ) {
        try {
            EmployeeHomePage(page).employeeLogin()
            val listingPage = BoatSpaceListPage(page)

            fun maliciousCode(value: String) = "%22%3E%3Cscript%3Ewindow.$value=true;%3C/script%3E%20"

            val params =
                mapOf(
                    parameter to maliciousCode(maliciousValue),
                )
            listingPage.navigateToWithParams(params)

            assertFalse(page.evaluate("() => window.hasOwnProperty('$maliciousValue')") as Boolean, "XSS script was executed on $parameter")
        } catch (e: AssertionError) {
            handleError(e)
        }
    }

    @Test
    fun `should be able to edit boat space`() {
        val listingPage = boatSpaceListPage()
        listingPage.editButton(1).click()
        assertThat(listingPage.boatSpaceRow(1)).not().containsText("1,50")
        assertThat(listingPage.boatSpaceRow(1)).not().containsText("3,50")
        assertThat(listingPage.boatSpaceRow(1)).not().containsText("Laajalahti")
        assertThat(listingPage.boatSpaceRow(1)).not().containsText("Storage")
        assertThat(listingPage.boatSpaceRow(1)).not().containsText("RearBuoy")
        assertThat(listingPage.boatSpaceRow(1)).not().containsText("223,67")
        listingPage.editModalButton.click()
        val editModal = listingPage.editModalPage
        editModal.fillForm(
            "1.5",
            "3.5",
            harbor = "3",
            section = "C",
            placeNumber = "1",
            boatSpaceType = "Storage",
            boatSpaceAmenity = "RearBuoy",
            payment = "1"
        )
        editModal.submitButton.click()
        listingPage.boatSpaceTypeFilter("Storage").click()
        assertThat(listingPage.boatSpaceRow(1)).containsText("1,50")
        assertThat(listingPage.boatSpaceRow(1)).containsText("3,50")
        assertThat(listingPage.boatSpaceRow(1)).containsText("Laajalahti")
        assertThat(listingPage.boatSpaceRow(1)).containsText("Storage")
        assertThat(listingPage.boatSpaceRow(1)).containsText("Rear buoy")
        assertThat(listingPage.boatSpaceRow(1)).containsText("223,67")
    }

    @Test
    fun `should be able to load more boat spaces`() {
        val listingPage = boatSpaceListPage()
        assertThat(listingPage.listItems).hasCount(50)

        listingPage.showMoreButton().click()
        assertThat(listingPage.listItems).hasCount(75)
        listingPage.showMoreButton().click()
        assertThat(listingPage.listItems).hasCount(100)
    }

    private fun boatSpaceListPage(): BoatSpaceListPage {
        val employeeHome = EmployeeHomePage(page)
        employeeHome.employeeLogin()

        val listingPage = BoatSpaceListPage(page)
        listingPage.navigateTo()
        return listingPage
    }
}
