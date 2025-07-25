package fi.espoo.vekkuli.boatSpace.employeeReservationList

import fi.espoo.vekkuli.boatSpace.employeeReservationList.components.*
import fi.espoo.vekkuli.domain.*
import fi.espoo.vekkuli.domain.BoatSpaceReservationFilterColumn.*
import fi.espoo.vekkuli.utils.addTestId
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
    private val dateFilter: DateFilter,
    private val amenityFilter: AmenityFilter,
    private val sectionFilter: SectionFilter,
    private val sendMessageView: SendMessageView
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

        val selectAllToggle =
            """
            <label class="checkbox" >
                <input
                    id="select-all-toggle"
                    ${addTestId("select-all-toggle")}
                    type="checkbox"
                    name='selectAll'
                    x-model="selectAll"
                    @change="reservationIds = selectAll ? getCurrentlyVisibleReservationIds() : []"
                    x-effect="
                        reservationIds; // access property to ensures Alpine tracks this reactive property
                                          // even when table is empty
                        selectAll = getCurrentlyVisibleReservationIds().length > 0 &&  
                            getCurrentlyVisibleReservationIds().every(id => reservationIds.includes(id));
                    "
                    hx-on:change="event.stopPropagation()"
                />
            </label>
            """.trimIndent()

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
                        reservationIds: [],
                        selectAll: false,
                        updateSort(column) {
                            if (this.sortColumn === column) {
                                this.sortDirection = this.sortDirection === 'true' ? 'false' : 'true';
                            } else {
                                this.sortColumn = column;
                                this.sortDirection = 'true';
                            }
                            document.getElementById('sortColumn').value = this.sortColumn;
                            document.getElementById('sortDirection').value = this.sortDirection;
                            document.getElementById('reservation-table-header').dispatchEvent(new Event('change'));
                        },
                        pruneFilteredReservationsFromSelection() {
                            const visibleIds = this.getCurrentlyVisibleReservationIds();
                            this.reservationIds = this.reservationIds.filter(id => visibleIds.includes(id));
                        },
                        getCurrentlyVisibleReservationIds() {
                            const table = this.${'$'}refs.tableBody;
                            const checkboxes = table.querySelectorAll('tbody input[type=checkbox][name=spaceId]');
                            return Array.from(checkboxes).map(e => e.value);
                        }
                    }"
                    > 
                    <form id="reservation-filter-form"
                          hx-get="/virkailija/venepaikat/varaukset"
                          hx-target="#table-body"
                          hx-select="#table-body, #modal"
                          hx-select-oob='#send-mass-message, #employee-reservation-list-warnings-filter, #reservation-list-load-more-container'
                          hx-trigger="change from:#reservation-filter-container delay:500ms, input change from:#reservation-table-header delay:1ms, keyup delay:500ms" 
                          hx-swap="outerHTML"
                          hx-history="false"
                          hx-push-url="true"
                          hx-indicator="#loader, .loaded-content"
                          @htmx:after-settle.window="pruneFilteredReservationsFromSelection()">
                          
                        <input type="hidden" name="sortBy" id="sortColumn" value="${params.sortBy}" >
                        <input type="hidden" name="ascending" id="sortDirection" value="${params.ascending}">
                   
                    <div id='reservation-filter-container'>
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
                            <div 
                                id="employee-reservation-list-warnings-filter" 
                                hx-swap-oob="true">
                                ${warningFilter.render(params.warningFilter == true, reservations.totalWarnings)}
                            </div>
                            <div>${exceptionsFilter.render(params.exceptionsFilter == true)}</div>
                            <div>${dateFilter.render(params.exceptionsFilter == true)}</div>
                        </div>
                    </div>  
                  
                        <div class="employee-filter-container" id="send-mass-message" hx-swap-oob="true">
                            ${sendMessageView.renderSendMassMessageLink(reservations.totalRows)}
                        </div>
                        <div class="reservation-list form-section block">
                            <div class='table-container'>
                                <table class="table is-hoverable">
                                    <thead id='reservation-table-header'>
                                        <tr class="table-borderless">
                                            <th>$selectAllToggle</th>
                                            <th>${sortButton(WARNING_CREATED.toString(), "")}</th>
                                            <th class="nowrap">
                                                ${sortButton(PLACE.toString(), t("boatSpaceReservation.title.harbor"))}
                                            </th>
                                            <th class="nowrap">
                                                ${sortButton(PLACE.toString(), t("boatSpaceReservation.title.place"))}
                                            </th>
            
                                            <th class="nowrap">
                                                ${sortButton(PLACE_TYPE.toString(), t("employee.boatSpaceReservations.table.title.type"))}
                                            </th>
                                            <th class="nowrap">
                                                ${sortButton(AMENITY.toString(), t("employee.boatReservations.title.amenity"))}
                                            </th>
                                            <th class="nowrap">
                                                ${sortButton(CUSTOMER.toString(), t("boatSpaceReservation.title.subject"))}
                                            </th>
                                            <th class="nowrap">
                                                ${sortButton(PHONE.toString(), t("boatSpaceReservation.title.phoneNumber"))}
                                            </th>                                    
                                            <th class="nowrap">
                                                ${sortButton(EMAIL.toString(), t("employee.boatSpaceReservations.table.title.email"))}
                                            </th>                                    
                                            <th class="nowrap">
                                                ${sortButton(HOME_TOWN.toString(), t("boatSpaceReservation.title.homeTown"))}
                                            </th>
                                            <th><span class="reservation-table-header">
                                                ${t("boatSpaceReservation.title.paymentState")}
                                            </span></th>
                                            <th class="nowrap">
                                                ${sortButton(START_DATE.toString(), t("boatSpaceReservation.title.startDate"))}
                                            </th>
                                            <th class="nowrap">
                                                ${sortButton(END_DATE.toString(), t("boatSpaceReservation.title.endDate"))}
                                            </th>
            
                                        </tr>      
                                        <tr>
                                            <th></th>
                                            <th></th>
                                            <th></th>
                                            <th>${sectionFilter.render(params.sectionFilter, sections)}</th>
                                            <th></th>
                                            <th>${amenityFilter.render(params.amenity, amenities)}</th>
                                            <th>${textSearchFilter.render("nameSearch", htmlEscape(params.nameSearch ?: ""))}</th>
                                            <th>${textSearchFilter.render("phoneSearch", htmlEscape(params.phoneSearch ?: ""))}</th>
                                            <th>${textSearchFilter.render("emailSearch", htmlEscape(params.emailSearch ?: ""))}</th>
                                            <th></th>
                                            <th></th>
                                            <th></th>
                                            <th></th>
                                        </tr>
                                        </thead>
                                        <tbody id="table-body" class="loaded-content" x-ref="tableBody">
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
                                        hasMore: ${reservations.totalRows - paginationStartFrom > 0}
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

    // language=HTML
    private fun sortButton(
        column: String,
        text: String
    ) = """
        <a href="#" @click.prevent="updateSort('$column')">
        ${if (text.isNotEmpty()) """<span class="reservation-table-header">$text</span>""" else "" }
        <span class="reservation-table-icon">
                <span x-show="sortColumn != '$column'">${icons.sort("")}</span>
                <span x-show="sortColumn == '$column' && sortDirection=='true'">${icons.sort("asc")}</span>
                <span x-show="sortColumn == '$column' && sortDirection=='false'">${icons.sort("desc")}</span>
            </span>
        """.trimIndent()
}
