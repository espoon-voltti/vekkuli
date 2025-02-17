package fi.espoo.vekkuli.boatSpace.boatSpaceList

import fi.espoo.vekkuli.domain.*
import fi.espoo.vekkuli.utils.addTestId
import fi.espoo.vekkuli.views.BaseView
import org.springframework.stereotype.Service

data class BoatSpaceListParams(
    val sortBy: BoatSpaceFilterColumn = BoatSpaceFilterColumn.PLACE,
    val ascending: Boolean = false,
)

@Service
class BoatSpaceList : BaseView() {
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
        searchParams: BoatSpaceListParams
    ): String {
        fun getBoatSpacePage(boatSpaceId: Int) {
        }
        // language=HTML
        val reservationRows =
            boatSpaces.joinToString("\n") { result ->

                """
                <tr class="boat-space-item"
                    id="boat-space-${result.id}"
                    hx-trigger="click"
                    hx-get=${getBoatSpacePage(result.id)}
                    hx-push-url="true"
                    hx-target=".section"
                    hx-select=".section">
                    <td></td>
                    <td>${result.locationName}</td>
                    <td>
                        <a href=${getBoatSpacePage(result.id)} ${
                    addTestId(
                        "place"
                    )
                }>${result.place}</a>
                    </td>
                    <td>${t("employee.boatSpaceReservations.types.${result.type}")}</td>
                    <td>${t("boatSpaces.amenityOption.${result.amenity}")}</td>
                   
                    <td>${result.widthInMeter}</td>
                    <td>${result.lengthInMeter}</td>
                    <td>${result.priceInEuro}</td>
                    <td> <span id='status-ball' class=${if (result.active) "active" else "inactive"}></span></td>
                    <td>${result.reserverName ?: '-'}</td>
                </tr>
                """.trimIndent()
            }

        // language=HTML
        return """
            <section class="section">
               
                <div class="container" x-data="{
                    sortColumn: '${searchParams.sortBy}',
                    sortDirection: '${searchParams.ascending}',
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
                }">
                    <form id="reservation-filter-form"
                          hx-get="/virkailija/venepaikat/selaa"
                          hx-target="#table-body"
                          hx-select="#table-body"
                          hx-trigger="change, keyup delay:500ms"
                          hx-swap="outerHTML"
                          hx-push-url="true"
                          hx-indicator="#loader, .loaded-content"
                    >
                         <input type="hidden" name="sortBy" id="sortColumn" value="${searchParams.sortBy}" >
                         <input type="hidden" name="ascending" id="sortDirection" value="${searchParams.ascending}">
                        
                        
                       


                        <div class="reservation-list form-section block">
                        <div class='table-container'>
                            <table class="table is-hoverable">
                                <thead>
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
                                    ${sortButton(BoatSpaceFilterColumn.ACTIVE.name, t("boatSpaceList.title.state"))}
                                    </th> 
                                    <th class="nowrap">
                                    ${sortButton(BoatSpaceFilterColumn.RESERVER.name, t("boatSpaceList.title.reserver"))}
                                    </th> 
                                 
                                </tr>
                                
                                <tr>
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
                                    <th></th>
                                    <th></th>
                                </tr>
                                </thead>
                                <tbody id="table-body" class="loaded-content">
                                $reservationRows
                                </tbody>
                            </table>
                            </div>
                            <div id="loader" class="htmx-indicator is-centered is-vcentered"> ${icons.spinner} <div>
                        </div>
                </div>
            </section>
            """.trimIndent()
    }
}
