package fi.espoo.vekkuli.employee

import com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat
import fi.espoo.vekkuli.PlaywrightTest
import fi.espoo.vekkuli.domain.BoatSpaceAmenity
import fi.espoo.vekkuli.domain.BoatSpaceState
import fi.espoo.vekkuli.pages.employee.BoatSpaceListPage
import fi.espoo.vekkuli.pages.employee.EmployeeHomePage
import fi.espoo.vekkuli.shared.CitizenIds
import fi.espoo.vekkuli.shared.OrganizationIds
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.springframework.test.context.ActiveProfiles
import kotlin.test.assertEquals

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
        try {
            val listingPage = boatSpaceListPage()
            listingPage.checkBox(1).click()
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
        } catch (e: AssertionError) {
            handleError(e)
        }
    }

    @Test
    fun `should be able to filter boat spaces`() {
        val listingPage = boatSpaceListPage()
        page.waitForCondition { listingPage.listItems.count() == 5 }

        // Filter by boat space type
        listingPage.boatSpaceTypeFilter("Winter").click()
        page.waitForCondition { listingPage.listItems.count() == 1 }

        // Reset and filter by amenity
        page.waitForCondition { listingPage.listItems.count() == 5 }
        listingPage.expandingSelectionFilter("amenity").click()
        listingPage.amenityFilter(BoatSpaceAmenity.Trailer.name).click()
        page.waitForCondition { listingPage.listItems.count() == 1 }
        assertThat(listingPage.getByDataTestId("place").first()).containsText("B 015")

        // Reset and filter by boat space state

        listingPage.boatStateFilter("Inactive").click()
        page.waitForCondition { listingPage.listItems.count() == 4 }
    }

    @Test
    fun `should be able to load more boat spaces`() {
        try {
            val listingPage = boatSpaceListPage()
            assertThat(listingPage.listItems).hasCount(50)

            listingPage.showMoreButton().click()
            assertThat(listingPage.listItems).hasCount(75)
            listingPage.showMoreButton().click()
            assertThat(listingPage.listItems).hasCount(100)
        } catch (e: AssertionError) {
            handleError(e)
        }
    }

    @Test
    fun `should be able to add and delete a boat space`() {
        try {
            val listingPage = boatSpaceListPage()

            // Add a new boat space
            listingPage.addBoatSpaceButton.click()
            val createModal = listingPage.createModal
            val harborParam = "8"

            createModal.fillForm(
                "1.5",
                "3.5",
                harbor = harborParam,
                section = "A",
                placeNumber = "111",
                boatSpaceType = "Storage",
                boatSpaceAmenity = "RearBuoy",
                payment = "2",
                boatSpaceState = BoatSpaceState.Inactive
            )
            createModal.submitButton.click()
            assertThat(createModal.successModal).isVisible()

            listingPage.harborFilter(harborParam).click()
            assertThat(listingPage.getBoatSpaceRowByIndex(0)).containsText("A 111")
            assertThat(listingPage.listItems).hasCount(2)
            listingPage.checkBox(listingPage.getBoatSpaceRowByIndex(0)).click()
            listingPage.editModalButton.click()
            // Remove the boat space

            val editModal = listingPage.editModalPage
            editModal.deleteButton.click()
            editModal.confirmButton.click()
            assertThat(editModal.deletionSuccessModal).isVisible()
            assertThat(listingPage.getBoatSpaceRowByIndex(0)).not().containsText("A 111")
            assertThat(listingPage.listItems).hasCount(1)
        } catch (e: AssertionError) {
            handleError(e)
        }
    }

    @Test
    fun `Employee can toggle selection of all visible boat spaces`() {
        val listingPage = boatSpaceListPage()
        val selectAllToggle = listingPage.selectAllToggle
        val testBoatSpaceId = 31

        listingPage.boatStateFilter("Inactive").click()
        page.waitForCondition { listingPage.listItems.count() == 4 }
        // click should select all when no rows are selected
        selectAllToggle.click()
        assertThat(selectAllToggle).isChecked()
        assertSelectedBoatSpaceCount(4)

        // click should deselect all when all rows are selected
        selectAllToggle.click()
        assertThat(selectAllToggle).not().isChecked()
        assertSelectedBoatSpaceCount(0)

        // click should select all when only some rows are manually selected
        listingPage.checkBox(testBoatSpaceId).click()
        selectAllToggle.click()
        assertThat(selectAllToggle).isChecked()
        assertSelectedBoatSpaceCount(4)

        // deselecting manually some rows should mark the element as unchecked
        listingPage.checkBox(testBoatSpaceId).click()
        assertThat(selectAllToggle).not().isChecked()
        assertSelectedBoatSpaceCount(3)

        // selecting manually all rows should mark the element as checked
        listingPage.checkBox(testBoatSpaceId).click()
        assertThat(selectAllToggle).isChecked()
        assertSelectedBoatSpaceCount(4)
    }

    @Test
    fun `should remove boat spaces from selection if they are no longer visible due to the filter`() {
        val listingPage = boatSpaceListPage()
        val activeBoatSpaceId = 1
        val inactiveBoatSpaceId = 31

        listingPage.checkBox(activeBoatSpaceId).click()

        listingPage.boatStateFilter("Inactive").click()
        page.waitForCondition { listingPage.listItems.count() == 4 }

        listingPage.checkBox(inactiveBoatSpaceId).click()

        assertSelectedBoatSpaceCount(1)
    }

    private fun boatSpaceListPage(): BoatSpaceListPage {
        val employeeHome = EmployeeHomePage(page)
        employeeHome.employeeLogin()

        val listingPage = BoatSpaceListPage(page)
        listingPage.navigateTo()
        return listingPage
    }

    private fun assertSelectedBoatSpaceCount(expectedCount: Int) {
        val listingPage = BoatSpaceListPage(page)

        val selectedRowCount = listingPage.listItems.locator("input[type=checkbox][name=spaceId]:checked").count()
        assertEquals(expectedCount, selectedRowCount, "selected row count: $selectedRowCount vs $expectedCount")

        if (expectedCount > 0) {
            val modalPage = listingPage.editModalPage
            listingPage.editModalButton.click()
            assertThat(modalPage.boatSpaceCount).containsText("Muokataan $expectedCount paikkaa")
            modalPage.cancelButton.click()
        }
    }
}
