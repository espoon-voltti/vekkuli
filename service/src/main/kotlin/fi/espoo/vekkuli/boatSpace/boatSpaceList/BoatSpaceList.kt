package fi.espoo.vekkuli.boatSpace.boatSpaceList

import fi.espoo.vekkuli.FormComponents
import fi.espoo.vekkuli.boatSpace.boatSpaceList.components.EditModal
import fi.espoo.vekkuli.domain.*
import fi.espoo.vekkuli.utils.addTestId
import fi.espoo.vekkuli.views.BaseView
import fi.espoo.vekkuli.views.employee.components.ExpandingSelectionFilter
import fi.espoo.vekkuli.views.employee.components.ListFilters
import org.springframework.stereotype.Service
import org.springframework.web.util.HtmlUtils.htmlEscape
import java.math.BigDecimal
import java.util.UUID

data class BoatSpaceListParams(
    val sortBy: BoatSpaceFilterColumn = BoatSpaceFilterColumn.PLACE,
    val ascending: Boolean = true,
    val amenity: List<BoatSpaceAmenity> = emptyList(),
    val harbor: List<Int> = emptyList(),
    val boatSpaceType: List<BoatSpaceType> = emptyList(),
    val boatSpaceState: List<BoatSpaceState> = emptyList(),
    val sectionFilter: List<String> = emptyList(),
    val edit: List<String> = emptyList()
)

data class BoatSpaceListEditParams(
    val edit: List<Int> = emptyList(),
    val sectionEdit: String? = null,
    val placeNumberEdit: Int? = null,
    val boatSpaceTypeEdit: BoatSpaceType,
    val boatSpaceAmenityEdit: BoatSpaceAmenity,
    val widthEdit: BigDecimal,
    val lengthEdit: BigDecimal,
    val paymentEdit: Int,
    val boatSpaceStateEdit: BoatSpaceState
)

@Service
class BoatSpaceList(
    private val expandingSelectionFilter: ExpandingSelectionFilter,
    private val filters: ListFilters,
    private val formComponents: FormComponents,
    private val editModal: EditModal
) : BaseView() {
    fun t(key: String): String = messageUtil.getMessage(key)

    fun sortButton(
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

    fun render(
        boatSpaces: List<BoatSpaceListRow>,
        searchParams: BoatSpaceListParams,
        harbors: List<Location>,
        paymentClasses: List<Price>,
        boatSpaceTypes: List<BoatSpaceType>,
        amenities: List<BoatSpaceAmenity>,
        sections: List<String>,
        editList: List<String>
    ): String {
        fun getBoatSpacePage(
            reserverId: UUID?,
            reserverType: ReserverType?
        ) = if (reserverId !== null) {
            "/virkailija/${reserverType?.toPath()}/$reserverId"
        } else {
            ""
        }
        val sectionFilter =
            expandingSelectionFilter.render(
                searchParams.sectionFilter,
                "selectedSections",
                sections.joinToString("\n") { expandingSelectionFilter.sectionCheckbox(it) }
            )

        // language=HTML
        fun editCheckBox(result: BoatSpaceListRow) =
            """
             <label class="checkbox">
                <input name="edit" id="edit-boat-space-${result.id}" type="checkbox" value="${result.id}" x-model='editBoatSpaces' />
            </label>
            """.trimIndent()
        // language=HTML
        val reservationRows =
            boatSpaces.joinToString("\n") { result ->

                """
                <tr class="boat-space-item"
                    id="boat-space-${result.id}">
                    <td>${editCheckBox(result)}</td>
                    <td>${result.locationName}</td>
                    <td
                        ${
                    addTestId(
                        "place"
                    )
                }>${result.place}
                    </td>
                    <td>${t("employee.boatSpaceReservations.types.${result.type}")}</td>
                    <td>${t("boatSpaces.amenityOption.${result.amenity}")}</td>
                   
                    <td>${result.widthInMeter}</td>
                    <td>${result.lengthInMeter}</td>
                    <td>${result.priceInEuro}</td>
                    <td> <span id='status-ball' class=${if (result.isActive) "active" else "inactive"}></span></td>
                    <td> <a href=${getBoatSpacePage(
                    result.reserverId,
                    result.reserverType
                )} >${htmlEscape((result.reserverName ?: '-').toString())}</a></td>
                </tr>
                """.trimIndent()
            }

        // language=HTML
        return """
            <section class="section" >
               
                <div class="container" x-data="{
                    sortColumn: '${searchParams.sortBy}',
                    sortDirection: '${searchParams.ascending}',
                    openEditModal: false,
                    editBoatSpaces: [],
                    updateSort(column) {
                        if (this.sortColumn === column) {
                            this.sortDirection = this.sortDirection === 'true' ? 'false' : 'true';
                        } else {
                            this.sortColumn = column;
                            this.sortDirection = 'true';
                        }
                        document.getElementById('sortColumn').value = this.sortColumn;
                        document.getElementById('sortDirection').value = this.sortDirection;
                        document.getElementById('boat-space-table-header').dispatchEvent(new Event('change'));
                    }
                }">
                    <form id="boat-space-filter-form"
                          hx-get="/virkailija/venepaikat/selaa"
                          hx-target="#table-body"
                          hx-select="#table-body"
                          hx-trigger="change from:#boat-space-filter-container, change from:#boat-space-table-header" 
                          hx-swap="outerHTML"
                          hx-push-url="true"
                          hx-indicator="#loader, .loaded-content"
                    >
                         <input type="hidden" name="sortBy" id="sortColumn" value="${searchParams.sortBy}" >
                         <input type="hidden" name="ascending" id="sortDirection" value="${searchParams.ascending}">
                        
                        <div id='boat-space-filter-container'>
                            <div class="employee-filter-container">                        
                                <div class="filter-group">
                                    <h1 class="label">${t("boatSpaceReservation.title.harbor")}</h1>
                                    <div class="tag-container">
                                    ${filters.harborFilters(harbors, searchParams.harbor)}
                                    </div>
                                </div>
                            </div>            
                             <div class="employee-filter-container">
                                <div class="filter-group">
                                    <h1 class="label">${t("boatSpaceReservation.title.type")}</h1>
                                    <div class="tag-container">
                                        ${filters.boatSpaceTypeFilters(boatSpaceTypes, searchParams.boatSpaceType)}
                                    </div>
                                </div>
                                
                                <div class="filter-group">
                                  <h1 class="label">${t("boatSpaceReservation.title.amenity")}</h1>
                                  <div class="tag-container">
                                    ${filters.amenityFilters(amenities, searchParams.amenity)}
                                  </div>
                                </div>
                            </div>
                            
                             <div class="employee-filter-container">
                                <div class="filter-group">
                                  <h1 class="label">${t("boatSpaceReservation.title.state")}</h1>
                                  <div class="tag-container">
                                    ${filters.boatSpaceStateFilter(searchParams.boatSpaceState)}
                                  </div>
                                </div>
                            </div>
                            <div class="employee-filter-container">
                                <button id="open-edit-modal" :disabled='editBoatSpaces.length <= 0' class='is-link' type='button' @click="openEditModal = true" >Muokkaa</button>    
                            </div>
                        </div>
                        <div class='reservation-list form-section block'>
                            <div class='table-container'>
                                <table class="table is-hoverable">
                                
                                    <thead id='boat-space-table-header'>
                                        <tr class="table-borderless">
                                            <th></th>
                                            <th class="nowrap">
                                            ${sortButton(BoatSpaceFilterColumn.PLACE.name, t("boatSpaceList.title.harbor"))}
                                            </th>
                                            <th class="nowrap">
                                                 ${sortButton(BoatSpaceFilterColumn.PLACE.name, t("boatSpaceList.title.place"))}
                                            </th>
                
                                            <th class="nowrap">
                                            ${sortButton(BoatSpaceFilterColumn.PLACE_TYPE.name, t("boatSpaceList.title.type"))}
                                            </th>
                                            <th class="nowrap">
                                            ${sortButton(BoatSpaceFilterColumn.AMENITY.name, t("boatSpaceList.title.amenity"))}
                                            </th>
                                            
                                                                           
                                            <th class="nowrap">
                                            ${sortButton(BoatSpaceFilterColumn.PLACE_WIDTH.name, t("boatSpaceList.title.widthInMeters"))}
                                            </th>
                                            <th><span class="reservation-table-header">
                                            ${sortButton(BoatSpaceFilterColumn.PLACE_LENGTH.name, t("boatSpaceList.title.lengthInMeters"))}
                                            </span></th>
                                            <th class="nowrap">
                                            ${sortButton(BoatSpaceFilterColumn.PRICE.name, t("boatSpaceList.title.price"))}
                                            </th>
                                            <th class="nowrap">
                                            ${t("boatSpaceList.title.state")}
                                            </th> 
                                            <th class="nowrap">
                                            ${sortButton(BoatSpaceFilterColumn.RESERVER.name, t("boatSpaceList.title.reserver"))}
                                            </th> 
                                         
                                        </tr>
                                        
                                        <tr>
                                            <th></th>
                                            <th></th>
                                            <th>$sectionFilter</th>
                                            <th></th>
                                            <th></th>
                                            <th></th>
                                            <th></th>
                                            <th></th>
                                            <th></th>
                                            <th></th>
                                            <th></th>
                                            <th></th>
                                        </tr>
                                    </thead>
                                    <tbody id="table-body" class="loaded-content">
                                    $reservationRows
                                    </tbody>
                                </table>
                            </div>
                            <div id="loader" class="htmx-indicator is-centered is-vcentered"> ${icons.spinner} </div>
                        </div>
                    </form>
                    ${editModal.render(harbors, paymentClasses)}
                    
                </div>
            </section>
            """.trimIndent()
    }
}
