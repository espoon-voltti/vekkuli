package fi.espoo.vekkuli.employee

import com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat
import fi.espoo.vekkuli.PlaywrightTest
import fi.espoo.vekkuli.domain.BoatSpaceAmenity
import fi.espoo.vekkuli.domain.BoatSpaceState
import fi.espoo.vekkuli.pages.employee.BoatSpaceDetailsPage
import fi.espoo.vekkuli.pages.employee.BoatSpaceListPage
import fi.espoo.vekkuli.pages.employee.CitizenDetailsPage
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

        page.waitForCondition { listingPage.listItems.count() == 100 }

        // Filter by boat space state
        listingPage.boatStateFilter("Inactive").click()
        assertThat(listingPage.listItems).hasCount(4)
        listingPage.boatStateFilter("Active").click()

        // Filter by boat space type
        listingPage.boatSpaceTypeFilter("Storage").click()
        assertThat(listingPage.listItems).hasCount(21)

        // Reset and filter by amenity
        listingPage.amenityFilter(BoatSpaceAmenity.Trailer.name).click()
        assertThat(listingPage.listItems).hasCount(10)

        // Filter by section
        listingPage.expandingSelectionFilter.click()
        listingPage.expandingSelectionFilterValue("C").click()
        assertThat(listingPage.listItems).hasCount(2)

        // Filter by harbor
        listingPage.harborFilter("2").click()
        assertThat(listingPage.listItems).hasCount(1)
    }

    @Test
    fun `should show only free spaces`() {
        val listingPage = boatSpaceListPage()
        page.waitForCondition { listingPage.listItems.count() == 100 }
        listingPage.boatSpaceTypeFilter("Winter").click()
        assertThat(listingPage.listItems).hasCount(29)

        listingPage.showOnlyFreeSpaces.click()
        assertThat(listingPage.listItems).hasCount(28)
    }

    @Test
    fun `should be able to load more boat spaces`() {
        try {
            val startingCount = 100
            val itemsShownOnClick = 100
            val listingPage = boatSpaceListPage()
            assertThat(listingPage.listItems).hasCount(startingCount)

            listingPage.showMoreButton().click()
            assertThat(listingPage.listItems).hasCount(startingCount + itemsShownOnClick)
            listingPage.showMoreButton().click()
            assertThat(listingPage.listItems).hasCount(startingCount + itemsShownOnClick + itemsShownOnClick)
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
    fun `select all toggle should be unchecked when filters return empty list of boat spaces`() {
        val listingPage = boatSpaceListPage()
        val selectAllToggle = listingPage.selectAllToggle

        listingPage.boatStateFilter("Inactive").click()
        listingPage.boatSpaceTypeFilter("Storage").click()
        page.waitForCondition { listingPage.listItems.count() == 0 }

        assertThat(selectAllToggle).not().isChecked()
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

    @Test
    fun `should open boat space details page and show reservation history`() {
        val listingPage = boatSpaceListPage()
        val boatSpaceId = 2
        listingPage.placeColumn(boatSpaceId).click()
        val boatSpaceDetails = BoatSpaceDetailsPage(page)
        assertThat(boatSpaceDetails.reservationHistoryListContainer).isVisible()
        assertThat(boatSpaceDetails.reservationRows).hasCount(4)

        boatSpaceDetails.reserverColumn(0).click()
        val reserverPage = CitizenDetailsPage(page)
        assertThat(reserverPage.citizenDetailsSection).isVisible()
        reserverPage.backButton.click()

        assertThat(boatSpaceDetails.reservationHistoryListContainer).isVisible()
        boatSpaceDetails.goBack.click()
        assertThat(listingPage.boatSpaceRow(boatSpaceId)).isVisible()
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
