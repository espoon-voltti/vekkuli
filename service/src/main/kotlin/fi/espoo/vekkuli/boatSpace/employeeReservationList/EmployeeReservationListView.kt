package fi.espoo.vekkuli.boatSpace.employeeReservationList

import fi.espoo.vekkuli.boatSpace.employeeReservationList.components.AmenityFilter
import fi.espoo.vekkuli.boatSpace.employeeReservationList.components.ExceptionsFilter
import fi.espoo.vekkuli.boatSpace.employeeReservationList.components.SectionFilter
import fi.espoo.vekkuli.boatSpace.employeeReservationList.components.TextSearchFilter
import fi.espoo.vekkuli.boatSpace.employeeReservationList.components.WarningFilter
import fi.espoo.vekkuli.domain.*
import fi.espoo.vekkuli.views.BaseView
import fi.espoo.vekkuli.views.employee.components.ListFilters
import org.springframework.stereotype.Service
import org.springframework.web.util.HtmlUtils.htmlEscape

@Service
class EmployeeReservationListView(
    private val filters: ListFilters,
    private val textSearchFilter: TextSearchFilter,
    private val reservationListRowsPartial: ReservationListRowsPartial,
    private val warningFilter: WarningFilter,
    private val exceptionsFilter: ExceptionsFilter,
    private val amenityFilter: AmenityFilter,
    private val sectionFilter: SectionFilter
) : BaseView() {
    fun render(
        harbors: List<Location>,
        boatSpaceTypes: List<BoatSpaceType>,
        amenities: List<BoatSpaceAmenity>,
        reservations: PaginatedReservationsResult<BoatSpaceReservationItem>,
        params: BoatSpaceReservationFilter,
        sections: List<String>,
        paginationSize: Int = 25,
    ): String {
        val paginationStartFrom = reservations.end
        val paginationEndTo = reservations.end + paginationSize
        // language=HTML
        return """
                        <section class="section">
                            <div class="container block heading">
                                <h2 id="reservations-header">${t("boatSpaceReservation.title")}</h2>
                                <span>                      
                                    <a id="create-reservation" href="/virkailija/venepaikat">
                                        <span class="icon is-small">
                                            ${icons.plus}
                                        </span>
                                        ${t("boatSpaceReservation.createReservation")}
                                    </a>
                                </span>
                            </div>
                            <div class="container" x-data="{
                                    sortColumn: '${params.sortBy}',
                                    sortDirection: '${params.ascending}',
                                    updateSort(column) {
                                        if (this.sortColumn === column) {
                                            this.sortDirection = this.sortDirection === 'true' ? 'false' : 'true';
                                        } else {
                                            this.sortColumn = column;
                                            this.sortDirection = 'true';
                                        }
                                        document.getElementById('sortColumn').value = this.sortColumn;
                                        document.getElementById('sortDirection').value = this.sortDirection;
                                        document.getElementById('reservation-filter-form').dispatchEvent(new Event('change'));
                                    }
                                }"
                                > 
                                <form id="reservation-filter-form"
                                      hx-get="/virkailija/venepaikat/varaukset"
                                      hx-target="#table-body"
                                      hx-select="#table-body"
                                      hx-trigger="change, keyup delay:500ms"
                                      hx-swap="outerHTML"
                                      hx-push-url="true"
                                      hx-indicator="#loader, .loaded-content"
                                >
                                    <input type="hidden" name="sortBy" id="sortColumn" value="${params.sortBy}" >
                                    <input type="hidden" name="ascending" id="sortDirection" value="${params.ascending}">

                                    <div class="employee-filter-container">                        
                                        <div class="filter-group">
                                            <h1 class="label">${t("boatSpaceReservation.title.harbor")}</h1>
                                            <div class="tag-container">
                                              ${filters.harborFilters(harbors, params.harbor)}
                                            </div>
                                        </div>
                                        <div class="filter-group">
                                            <h1 class="label">${t("boatSpaceReservation.title.expiration")}</h1>
                                            <div class="tag-container">
                                                ${filters.reservationExpirationFilter(params.expiration)}
                                            </div>
                                        </div>
                                        <div class="filter-group">
                                            <h1 class="label">${t("boatSpaceReservation.title.paymentState")}</h1>
                                            <div class="tag-container">
                                                ${filters.paymentFilters(params.payment)}
                                            </div>
                                        </div>                            
                                        <div class="filter-group">
                                            <h1 class="label">${t("employee.boatReservations.title.reservationValidity")}</h1>
                                            <div class="tag-container">
                                                ${filters.reservationValidityFilters(params.validity)}
                                            </div>
                                        </div>                            
                                    </div>
                                    
                                    <div class="employee-filter-container">
                                        <div class="filter-group">
                                          <h1 class="label">${t("boatSpaceReservation.title.type")}</h1>
                                          <div class="tag-container">
                                            ${filters.boatSpaceTypeFilters(boatSpaceTypes, params.boatSpaceType)}
                                          </div>
                                        </div>
                                    </div>

                                    <div class="employee-warning-filter">
                                        <div id="employee-reservation-list-warnings-filter" hx-swap-oob="true">${warningFilter.render(
            params.warningFilter == true,
            reservations.totalWarnings
        )}</div>
                                        <div>${exceptionsFilter.render(params.exceptionsFilter == true)}</div>
                                    </div>

                                    <div class="reservation-list form-section block">
                                        <div class='table-container'>
                                            <table class="table is-hoverable">
                                                <thead>
                                                    <tr class="table-borderless">
                                                        <th></th>
                                                        <th class="nowrap">
                                                            ${sortButton("PLACE", t("boatSpaceReservation.title.harbor"))}
                                                        </th>
                                                        <th class="nowrap">
                                                            ${sortButton("PLACE", t("boatSpaceReservation.title.place"))}
                                                        </th>
                        
                                                        <th class="nowrap">
                                                            ${sortButton("PLACE_TYPE", t("employee.boatSpaceReservations.table.title.type"))}
                                                        </th>
                                                        <th class="nowrap">
                                                            ${sortButton("AMENITY", t("employee.boatReservations.title.amenity"))}
                                                        </th>
                                                        <th class="nowrap">
                                                            ${sortButton("CUSTOMER", t("boatSpaceReservation.title.subject"))}
                                                        </th>
                                                        <th class="nowrap">
                                                            ${sortButton("PHONE", t("boatSpaceReservation.title.phoneNumber"))}
                                                        </th>                                    
                                                        <th class="nowrap">
                                                            ${sortButton("EMAIL", t("employee.boatSpaceReservations.table.title.email"))}
                                                        </th>                                    
                                                        <th class="nowrap">
                                                            ${sortButton("HOME_TOWN", t("boatSpaceReservation.title.homeTown"))}
                                                        </th>
                                                        <th><span class="reservation-table-header">
                                                            ${t("boatSpaceReservation.title.paymentState")}
                                                        </span></th>
                                                        <th class="nowrap">
                                                            ${sortButton("START_DATE", t("boatSpaceReservation.title.startDate"))}
                                                        </th>
                                                        <th class="nowrap">
                                                            ${sortButton("END_DATE", t("boatSpaceReservation.title.endDate"))}
                                                        </th>
                        
                                                    </tr>      
                                                    <tr>
                                                        <th></th>
                                                        <th></th>
                                                        <th>${sectionFilter.render(params.sectionFilter, sections)}</th>
                                                        <th></th>
                                                        <th>${amenityFilter.render(params.amenity, amenities)}</th>
                                                        <th>${textSearchFilter.render("nameSearch", htmlEscape(params.nameSearch ?: ""))}</th>
                                                        <th>${textSearchFilter.render("phoneSearch", htmlEscape(params.phoneSearch ?: ""))}</th>
                                                        <th></th>
                                                        <th></th>
                                                        <th></th>
                                                        <th></th>
                                                        <th></th>
                                                    </tr>
                                                    </thead>
                                                    <tbody id="table-body" class="loaded-content">
                                                    ${reservationListRowsPartial.render(reservations)}
                                                    </tbody>
                                            </table>
                                        </div>
                                        <div id="reservation-list-load-more-container" hx-swap-oob="true" class="has-text-centered" >
                                            <button 
                                                class="button is-primary is-fullwidth"
                                                hx-get="/virkailija/venepaikat/varaukset/rivit"
                                                hx-trigger="click"
                                                hx-target="#table-body"
                                                hx-select="tr"
                                                hx-swap="beforeend"
                                                hx-indicator="#loader"
                                                hx-include="#reservation-filter-form"
                                                hx-push-url="false"
                                                hx-vals='{"paginationStart": $paginationStartFrom, "paginationEnd": $paginationEndTo}'
                                                x-data="{ 
                                                    paginationStart: $paginationStartFrom,
                                                    paginationEnd: $paginationEndTo,
                                                    paginationSize: $paginationSize,
                                                    paginationTotalRows: ${reservations.totalRows},
                                                    paginationResultsLeft: ${reservations.totalRows - paginationStartFrom},
                                                    hasMore: ${reservations.totalRows > paginationEndTo}
                                                }" x-show="hasMore"
                                                @htmx:after-request="
                                                    paginationStart = paginationEnd;
                                                    paginationEnd = paginationEnd + paginationSize;
                                                    paginationResultsLeft = paginationTotalRows - paginationStart;
                                                    hasMore = paginationResultsLeft > 0;
                                                    ${'$'}el.setAttribute('hx-vals', JSON.stringify({ paginationStart: paginationStart, paginationEnd: paginationEnd }));
                                                "
                                            >
                                                ${t("showMore")} (<span x-text="paginationResultsLeft"></span>)
                                            </button>
                                        </div>
                                        <div id="loader" class="htmx-indicator has-text-centered">${icons.spinner}</div>
                                    </div>
                                </form>
                            </div>
                        </section>
            """.trimIndent()
    }

    private fun sortButton(
        column: String,
        text: String
    ) = """
        <a href="#" @click.prevent="updateSort('$column')">
            <span class="reservation-table-header">
                $text
            </span>
            <span class="reservation-table-icon">
                <span x-show="sortColumn != '$column'">${icons.sort("")}</span>
                <span x-show="sortColumn == '$column' && sortDirection=='true'">${icons.sort("asc")}</span>
                <span x-show="sortColumn == '$column' && sortDirection=='false'">${icons.sort("desc")}</span>
            </span>
        """.trimIndent()
}
