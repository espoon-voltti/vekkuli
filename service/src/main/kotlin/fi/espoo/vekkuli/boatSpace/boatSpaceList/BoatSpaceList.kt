package fi.espoo.vekkuli.boatSpace.boatSpaceList

import fi.espoo.vekkuli.boatSpace.boatSpaceList.components.ConfirmDeleteModal
import fi.espoo.vekkuli.boatSpace.boatSpaceList.components.CreateBoatSpaceModal
import fi.espoo.vekkuli.boatSpace.boatSpaceList.components.EditModal
import fi.espoo.vekkuli.boatSpace.boatSpaceList.partials.BoatSpaceListRowsPartial
import fi.espoo.vekkuli.domain.*
import fi.espoo.vekkuli.repository.PaginatedResult
import fi.espoo.vekkuli.utils.addTestId
import fi.espoo.vekkuli.views.BaseView
import fi.espoo.vekkuli.views.employee.components.ExpandingSelectionFilter
import fi.espoo.vekkuli.views.employee.components.ListFilters
import org.springframework.stereotype.Service
import java.math.BigDecimal

data class BoatSpaceListParams(
    val sortBy: BoatSpaceFilterColumn = BoatSpaceFilterColumn.PLACE,
    val ascending: Boolean = true,
    val amenity: List<BoatSpaceAmenity> = emptyList(),
    val harbor: List<Int> = emptyList(),
    val boatSpaceType: List<BoatSpaceType> = emptyList(),
    val boatSpaceState: List<BoatSpaceState> = emptyList(),
    val sectionFilter: List<String> = emptyList(),
    val edit: List<String> = emptyList(),
    val paginationStart: Int = 0,
    val paginationEnd: Int = 50
)

data class BoatSpaceListEditParams(
    val boatSpaceIds: List<Int> = emptyList(),
    val section: String? = null,
    val placeNumber: Int? = null,
    val harbor: Int? = null,
    val boatSpaceType: BoatSpaceType?,
    val boatSpaceAmenity: BoatSpaceAmenity?,
    val width: BigDecimal?,
    val length: BigDecimal?,
    val payment: Int?,
    val boatSpaceState: BoatSpaceState?
)

data class BoatSpaceListAddParams(
    val sectionCreation: String,
    val placeNumberCreation: Int,
    val harborCreation: Int,
    val boatSpaceTypeCreation: BoatSpaceType,
    val boatSpaceAmenityCreation: BoatSpaceAmenity,
    val widthCreation: BigDecimal,
    val lengthCreation: BigDecimal,
    val paymentCreation: Int,
    val boatSpaceStateCreation: BoatSpaceState
)

@Service
class BoatSpaceList(
    private val expandingSelectionFilter: ExpandingSelectionFilter,
    private val filters: ListFilters,
    private val editModal: EditModal,
    private val createModal: CreateBoatSpaceModal,
    private val deleteModal: ConfirmDeleteModal,
    private val boatSpaceListRowsPartial: BoatSpaceListRowsPartial
) : BaseView() {
    // language=HTML
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
        boatSpaces: PaginatedResult<BoatSpaceListRow>,
        searchParams: BoatSpaceListParams,
        harbors: List<Location>,
        paymentClasses: List<Price>,
        boatSpaceTypes: List<BoatSpaceType>,
        amenities: List<BoatSpaceAmenity>,
        sections: List<String>,
        editList: List<String>,
        paginationSize: Int = 25,
    ): String {
        val paginationStartFrom = boatSpaces.end
        val paginationEndTo = boatSpaces.end + paginationSize

        val sectionFilter =
            expandingSelectionFilter.render(
                searchParams.sectionFilter,
                "selectedSections",
                sections.joinToString("\n") { expandingSelectionFilter.sectionCheckbox(it) }
            )

        // language=HTML
        val reservationRows =
            boatSpaceListRowsPartial.render(boatSpaces)

        val selectAllToggle =
            """
            <label class="checkbox" x-data="{
                selectAll: false,
                getCurrentlyVisibleBoatSpaceIds() {
                    return this.getBoatSpacesIdsFromTable(this.${'$'}el.closest('table'));
                },
            }">
                <input
                    id="select-all-toggle"
                    ${addTestId("select-all-toggle")}
                    type="checkbox"
                    x-model="selectAll"
                    @change="editBoatSpaceIds = selectAll ? getCurrentlyVisibleBoatSpaceIds() : []"
                    x-effect="selectAll = getCurrentlyVisibleBoatSpaceIds().every(id => editBoatSpaceIds.includes(id));"
                    hx-on:change="event.stopPropagation()"
                />
            </label>
            """.trimIndent()

        // language=HTML
        return """
<section class="section" x-data="{openCreateModal: false, openDeleteModal: false}">
   <div class="container block heading" >
        <h2 id="reservations-header">${t("boatSpaceReservation.title")}</h2>
        <span>                      
            <a ${addTestId("create-boat-space")} @click="openCreateModal = true">
                <span class="icon is-small">
                    ${icons.plus}
                </span>
                ${t("boatSpaceList.button.createBoatSpace")}
            </a>
        </span>
    </div>
    <div class="container" x-data="{
        sortColumn: '${searchParams.sortBy}',
        sortDirection: '${searchParams.ascending}',
        openEditModal: false,
        editBoatSpaceIds: [],
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
        },
        pruneFilteredBoatSpacesFromSelection() {
            const visibleBoatIds = this.getCurrentlyVisibleBoatSpaceIds();
            this.editBoatSpaceIds = this.editBoatSpaceIds.filter(id => visibleBoatIds.includes(id));
        },
        getCurrentlyVisibleBoatSpaceIds() {
            return this.getBoatSpacesIdsFromTable(this.${'$'}el.querySelector('table'));
        },
        getBoatSpacesIdsFromTable(table) {
            const checkboxes = table.querySelectorAll('tbody input[type=checkbox][name=spaceId]');
            return Array.from(checkboxes).map(e => e.value);
        }
    }">
        <form id="boat-space-filter-form"
              hx-get="/virkailija/venepaikat/selaa"
              hx-target="#table-body"
              hx-select="#table-body"
              hx-trigger="change from:#boat-space-filter-container, change from:#boat-space-table-header" 
              hx-params="not spaceId"
              hx-swap="outerHTML"
              hx-push-url="true"
              hx-indicator="#loader, .loaded-content"
              @htmx:after-request="pruneFilteredBoatSpacesFromSelection()"
        >
             <input type="hidden" name="sortBy" id="sortColumn" value="${searchParams.sortBy}" >
             <input type="hidden" name="ascending" id="sortDirection" value="${searchParams.ascending}">
            
            <div id='boat-space-filter-container'>
                <div class="employee-filter-container">                        
                    <div class="filter-group">
                        <h1 class="label" >${t(
            "boatSpaceReservation.title.harbor"
        )}</h1>
                        <div class="tag-container">
                        ${filters.harborFilters(
            harbors,
            searchParams.harbor
        )}
                        </div>
                    </div>
                </div>            
                 <div class="employee-filter-container">
                    <div class="filter-group">
                        <h1 class="label">${t(
            "boatSpaceReservation.title.type"
        )}</h1>
                        <div class="tag-container">
                            ${filters.boatSpaceTypeFilters(
            boatSpaceTypes,
            searchParams.boatSpaceType
        )}
                        </div>
                    </div>
                    
                    <div class="filter-group">
                      <h1 class="label">${t(
            "boatSpaceReservation.title.amenity"
        )}</h1>
                      <div class="tag-container">
                        ${filters.amenityFilters(
            amenities,
            searchParams.amenity
        )}
                      </div>
                    </div>
                </div>
                
                 <div class="employee-filter-container">
                    <div class="filter-group">
                      <h1 class="label">${t(
            "boatSpaceReservation.title.state"
        )}</h1>
                      <div class="tag-container">
                        ${filters.boatSpaceStateFilter(
            searchParams.boatSpaceState
        )}
                      </div>
                    </div>
                </div>
                <div class="employee-filter-container">
                    <button ${addTestId(
            "open-edit-modal"
        )} :disabled='editBoatSpaceIds.length <= 0' class='is-link' type='button' @click="openEditModal = true" >Muokkaa</button>    
                </div>
            </div>
            <div class='reservation-list form-section block'>
                <div class='table-container'>
                    <table class="table is-hoverable">
                    
                        <thead id='boat-space-table-header'>
                            <tr class="table-borderless">
                                <th>$selectAllToggle</th>
                                <th class="nowrap" >
                                ${sortButton(
            BoatSpaceFilterColumn.PLACE.name,
            t("boatSpaceList.title.harbor")
        )}
                                </th>
                                <th class="nowrap">
                                     ${sortButton(
            BoatSpaceFilterColumn.PLACE.name,
            t("boatSpaceList.title.place")
        )}
                                </th>
    
                                <th class="nowrap">
                                ${sortButton(
            BoatSpaceFilterColumn.PLACE_TYPE.name,
            t("boatSpaceList.title.type")
        )}
                                </th>
                                <th class="nowrap">
                                ${sortButton(
            BoatSpaceFilterColumn.AMENITY.name,
            t("boatSpaceList.title.amenity")
        )}
                                </th>
                                
                                                               
                                <th class="nowrap">
                                ${sortButton(
            BoatSpaceFilterColumn.PLACE_WIDTH.name,
            t("boatSpaceList.title.widthInMeters")
        )}
                                </th>
                                <th><span class="reservation-table-header">
                                ${sortButton(
            BoatSpaceFilterColumn.PLACE_LENGTH.name,
            t("boatSpaceList.title.lengthInMeters")
        )}
                                </span></th>
                                <th class="nowrap">
                                ${sortButton(
            BoatSpaceFilterColumn.PRICE.name,
            t("boatSpaceList.title.priceClass")
        )}
                                </th>   
                                <th class="nowrap">
                                ${sortButton(
            BoatSpaceFilterColumn.PRICE.name,
            t("boatSpaceList.title.price")
        )}
                                </th>
                                <th class="nowrap">
                                ${t(
            "boatSpaceList.title.state"
        )}
                                </th> 
                                <th class="nowrap">
                                ${sortButton(
            BoatSpaceFilterColumn.RESERVER.name,
            t("boatSpaceList.title.reserver")
        )}
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
                                <th></th>
                            </tr>
                        </thead>
                        <tbody id="table-body" class="loaded-content">
                        $reservationRows
                        </tbody>
                    </table>
                </div>
            <div id="boat-space-load-more-container" hx-swap-oob="true" class="has-text-centered" >
                                <button 
                                    class="button is-primary is-fullwidth"
                                    hx-get="/virkailija/venepaikat/selaa/rivit"
                                    hx-trigger="click"
                                    hx-target="#table-body"
                                    hx-select="tr"
                                    hx-swap="beforeend"
                                    hx-indicator="#loader"
                                    hx-include="#boat-space-filter-form"
                                    hx-push-url="false"
                                    hx-vals='{"paginationStart": $paginationStartFrom, "paginationEnd": $paginationEndTo}'
                                    x-data="{ 
                                        paginationStart: $paginationStartFrom,
                                        paginationEnd: $paginationEndTo,
                                        paginationSize: $paginationSize,
                                        paginationTotalRows: ${boatSpaces.totalRows},
                                        paginationResultsLeft: ${boatSpaces.totalRows - paginationStartFrom},
                                        hasMore: ${boatSpaces.totalRows - paginationStartFrom > 0}
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
                        <div id="loader" class="htmx-indicator is-centered is-vcentered"> ${icons.spinner} </div>
                            
                        </div>
        </form>
        ${editModal.render(harbors, paymentClasses)}
        ${createModal.render(harbors, paymentClasses)}
        ${deleteModal.render()}
    </div>
</section>
            """.trimIndent()
    }
}
