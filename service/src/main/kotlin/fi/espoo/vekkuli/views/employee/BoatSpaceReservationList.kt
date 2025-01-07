package fi.espoo.vekkuli.views.employee

import fi.espoo.vekkuli.controllers.UserType
import fi.espoo.vekkuli.domain.*
import fi.espoo.vekkuli.utils.addTestId
import fi.espoo.vekkuli.utils.formatAsShortYearDate
import fi.espoo.vekkuli.views.BaseView
import org.springframework.stereotype.Service
import java.util.*

@Service
class BoatSpaceReservationList : BaseView() {
    fun t(key: String): String = messageUtil.getMessage(key)

    fun render(
        harbors: List<Location>,
        boatSpaceTypes: List<BoatSpaceType>,
        amenities: List<BoatSpaceAmenity>,
        reservations: List<BoatSpaceReservationItem>,
        params: BoatSpaceReservationFilter,
        userType: UserType
    ): String {
        val harborFilters =
            harbors.joinToString("\n") { harbor ->
                """
                <label class="filter-button" ${addTestId(
                    "filter-harbor-$harbor.name"
                )}>
                    <input type="checkbox" name="harbor" value="${harbor.id}" class="is-hidden" ${if (params.hasHarbor(
                        harbor.id
                    )
                ) {
                    "checked"
                } else {
                    ""
                }}>
                    <span class="icon is-small">
                        ${icons.check}
                    </span>
                    <span>${harbor.name}</span>
                </label>
                """.trimIndent()
            }

        val boatSpaceTypeFilters =
            boatSpaceTypes.joinToString("\n") { boatSpaceType ->
                """
                <label class="filter-button" ${addTestId(
                    "filter-type-$boatSpaceType"
                )}>
                    <input type="checkbox" name="boatSpaceType" value="$boatSpaceType" class="is-hidden" ${if (params.hasBoatSpaceType(
                        boatSpaceType
                    )
                ) {
                    "checked"
                } else {
                    ""
                }}>
                    <span class="icon is-small">
                        ${icons.check}
                    </span>
                    <span>${t("employee.boatSpaceReservations.types.$boatSpaceType")}</span>
                </label>
                """.trimIndent()
            }

        val reservationExpirationFilter =
            ReservationExpiration.entries.joinToString("\n") { state ->
                """
                <label class="filter-button">
                    <input type="radio" name="expiration" value="${state.name}" class="is-hidden" ${if (params.expiration == state) {
                    "checked"
                } else {
                    ""
                }}>
                    <span class="icon is-small">
                        ${icons.check}
                    </span>
                    <span>${t("boatSpaces.expirationOption.$state")}</span>
                </label>
                """.trimIndent()
            }

        val paymentOptions = listOf(PaymentFilter.PAID, PaymentFilter.UNPAID)
        val paymentFilters =
            paymentOptions.joinToString("\n") { paymentOption ->
                """
                <label class="filter-button">
                    <input type="checkbox" name="payment" value="$paymentOption" class="is-hidden" ${if (params.hasPayment(paymentOption)) {
                    "checked"
                } else {
                    ""
                }}>
                    <span class="icon is-small">
                        ${icons.check}
                    </span>
                    <span>${t("boatSpaceReservation.paymentOption.${paymentOption.toString().lowercase()}")}</span>
                </label>
                """.trimIndent()
            }

        val reservationValidityFilters =
            ReservationValidity.entries.joinToString("\n") { validity ->
                """
                <label class="filter-button" ${addTestId(
                    "filter-reservation-validity-$validity"
                )}>
                    <input type="checkbox" name="validity" value="$validity" class="is-hidden" ${if (params.hasValidity(
                        validity
                    )
                ) {
                    "checked"
                } else {
                    ""
                }}>
                    <span class="icon is-small">
                        ${icons.check}
                    </span>
                    <span>${t("employee.boatReservations.validity.$validity")}</span>
                </label>
                """.trimIndent()
            }

        val reservationsWithWarningsCount =
            reservations.fold(0) { acc, reservation -> acc + if (reservation.hasAnyWarnings()) 1 else 0 }
        val warningFilterCheckbox =
            """
            <label class="checkbox">
                <input type="checkbox" name="warningFilter" ${if (params.warningFilter == true) {
                "checked"
            } else {
                ""
            }}>
                <span>${t("boatSpaceReservation.showReservationsWithWarnings", listOf(reservationsWithWarningsCount.toString()))}</span>
               
            </label>
             <span class="ml-s">${icons.warningExclamation(false)}</span>
            """.trimIndent()

        fun textSearchInput(
            name: String,
            inputVal: String?
        ) = """
            <p class="control has-icons-left">
                <input class="input search-input" type="text" name="$name" ${addTestId("search-input-$name")} 
                aria-label="${t("boatSpaces.searchButton")}" value="${inputVal ?: ""}"/>                
                <span class="icon is-small is-left">${icons.search}</span>                
            </p>
            """.trimIndent()

        val sections = listOf("A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "O")

        fun sectionCheckbox(section: String) =
            """
            <label class="checkbox dropdown-item" style="margin-bottom:4px;">
                <input type="checkbox" name="sectionFilter" value="$section" x-model="selectedSections" >
                <span>$section</span>
            </label>
            """.trimIndent()

        fun amenityCheckbox(amenity: String) =
            """
            <label class="checkbox dropdown-item" style="margin-bottom:4px;" ${addTestId(
                "filter-amenity-$amenity"
            )}>
                <input type="checkbox" name="amenity" value="$amenity" x-model="amenity" >
                <span>${t("boatSpaces.amenityOption.$amenity")}</span>
            </label>
            """.trimIndent()

        fun getReserverPageUrl(
            reserverId: UUID,
            reserverType: ReserverType
        ) = """"/virkailija/${if (reserverType == ReserverType.Citizen) "kayttaja" else "yhteiso"}/$reserverId""""

        fun expandingSelectionFilter(
            filter: List<String>,
            modelName: String,
            content: String
        ) = """
                                    <div x-data="{ open: false, $modelName: [${filter.joinToString(
            ","
        ) { "'$it'" }}] }" @click.outside="open = false">
                                        <div class="dropdown $modelName" :class="{ 'is-active': open }" ${addTestId(
            "filter-selection-$modelName"
        )}>        
                                            <div class="dropdown-trigger">                    
                                                <a aria-haspopup="true" aria-controls="dropdown-menu-$modelName" @click="open = !open">                    
                                                    <div class="input search-input has-icons-left has-icons-right">                    
                                                        <span class="icon is-small is-left">${icons.filter}</span>
                                                        <span class="filter-tag" x-show="$modelName.length > 0" x-text="$modelName.length" style="margin-left:auto"></span>
                                                    </div>
                                                </a>
                                            </div>
                                            <div class="dropdown-menu filter-dropdown-menu" id="dropdown-menu-$modelName" role="menu">
                                                <div>$content</div>
                                            </div>
                                        </div>
                                    </div>
            """.trimIndent()

        val sectionFilter =
            expandingSelectionFilter(params.sectionFilter, "selectedSections", sections.joinToString("\n") { sectionCheckbox(it) })

        val amenityFilter =
            expandingSelectionFilter(
                params.amenity.map { t ->
                    t.name
                },
                "amenity",
                amenities.joinToString("\n") { amenityCheckbox(it.name) }
            )

        fun getWarningIcon(hasWarnings: Boolean) =
            if (hasWarnings) {
                "<div ${addTestId("warning-icon")}>${icons.warningExclamation(false)}</div>"
            } else {
                ""
            }

        // language=HTML
        val reservationRows =
            reservations.joinToString("\n") { result ->
                val endDateFormatted = formatAsShortYearDate(result.endDate)
                val paymentDateFormatted = formatAsShortYearDate(result.paymentDate)
                val endDateText =
                    if (result.status == ReservationStatus.Cancelled) {
                        """<span class="has-text-danger">${t("reservations.text.terminated")} $endDateFormatted</span>"""
                    } else if (result.validity == ReservationValidity.FixedTerm) {
                        endDateFormatted
                    } else {
                        ""
                    }
                """
                <tr class="reservation-item"
                    id="boat-space-${result.boatSpaceId}"
                    hx-trigger="click"
                    hx-get=${getReserverPageUrl(result.reserverId, result.reserverType)}
                    hx-push-url="true"
                    hx-target=".section"
                    hx-select=".section">
                    <td>${getWarningIcon(result.hasAnyWarnings())}</td>
                    <td>${result.locationName}</td>
                    <td>
                        <span ${addTestId(
                    "place"
                )}>${result.place}</span>
                    </td>
                    <td>${t("employee.boatSpaceReservations.types.${result.type}")}</td>
                    <td>${t("boatSpaces.amenityOption.${result.getBoatSpaceAmenity()}")}</td>
                    <td ${addTestId(
                    "reserver-name"
                )}><a href=${getReserverPageUrl(result.reserverId, result.reserverType)}>${result.name}</a></td>
                    <td>${result.phone}</td>
                    <td>${result.email}</td>
                    <td>${result.municipalityName}</td>
                    <td>$paymentDateFormatted</td>
                    <td ${addTestId(
                    "reservation-end-date"
                )}>$endDateText</td>
                </tr>
                """.trimIndent()
            }

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
                }">
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
                                $harborFilters
                                </div>
                            </div>
                            <div class="filter-group">
                                <h1 class="label">${t("boatSpaceReservation.title.expiration")}</h1>
                                <div class="tag-container">
                                    $reservationExpirationFilter
                                </div>
                            </div>
                            <div class="filter-group">
                                <h1 class="label">${t("boatSpaceReservation.title.paymentState")}</h1>
                                <div class="tag-container">
                                    $paymentFilters
                                </div>
                            </div>                            
                            <div class="filter-group">
                                <h1 class="label">${t("employee.boatReservations.title.reservationValidity")}</h1>
                                <div class="tag-container">
                                    $reservationValidityFilters
                                </div>
                            </div>                            
                        </div>
                        
                        <div class="employee-filter-container">
                            <div class="filter-group">
                              <h1 class="label">${t("boatSpaceReservation.title.type")}</h1>
                              <div class="tag-container">
                                $boatSpaceTypeFilters
                              </div>
                            </div>
                        </div>

                        <div class="employee-warning-filter">
                            $warningFilterCheckbox
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
                                        ${sortButton("END_DATE", t("boatSpaceReservation.title.endDate"))}
                                    </th>

                                </tr>
                                
                                <tr>
                                    <th></th>
                                    <th></th>
                                    <th>$sectionFilter</th>
                                    <th></th>
                                    <th>$amenityFilter</th>
                                    <th>${textSearchInput("nameSearch", params.nameSearch)}</th>
                                    <th>${textSearchInput("phoneSearch", params.phoneSearch)}</th>
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
                            <div id="loader" class="htmx-indicator"> ${icons.spinner} <div>
                        </div>
                    </form>
                </div>
            </section>
            """.trimIndent()
    }
}
