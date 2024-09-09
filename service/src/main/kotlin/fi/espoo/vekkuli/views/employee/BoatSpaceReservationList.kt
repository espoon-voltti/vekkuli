package fi.espoo.vekkuli.views.employee

import fi.espoo.vekkuli.config.MessageUtil
import fi.espoo.vekkuli.domain.*
import fi.espoo.vekkuli.views.Icons
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.format.DateTimeFormatter

@Service
class BoatSpaceReservationList {
    @Autowired
    lateinit var messageUtil: MessageUtil

    @Autowired
    lateinit var icons: Icons

    fun t(key: String): String {
        return messageUtil.getMessage(key)
    }

    fun render(
        harbors: List<Location>,
        amenities: List<BoatSpaceAmenity>,
        reservations: List<BoatSpaceReservationItem>,
        params: BoatSpaceReservationFilter
    ): String {
        val harborFilters =
            harbors.joinToString("\n") { harbor ->
                """
                <label class="filter-button">
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

        val amenityFilters =
            amenities.joinToString("\n") { amenity ->
                """
                <label class="filter-button">
                    <input type="checkbox" name="amenity" value="${amenity.name}" class="is-hidden" ${if (params.hasAmenity(
                        amenity
                    )
                ) {
                    "checked"
                } else {
                    ""
                }}>
                    <span class="icon is-small">
                        ${icons.check}
                    </span>
                    <span>${t("boatSpaces.amenityOption.$amenity")}</span>
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

        val nameSearchInput =
            """
            <p class="control has-icons-left has-icons-right">
                <input class="input search-input" type="text" name="nameSearch" 
                    aria-label="${t("boatSpaces.searchButton")}"/>
                <span class="icon is-small is-left">${icons.search}</span>
            </p>
            """.trimIndent()

        // Reservation list
        val reservationRows =
            reservations.joinToString("\n") { result ->
                val startDateFormatted = result.startDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                val endDateFormatted = result.endDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                """
                <tr class="reservation-item"
                    id="boat-space-${result.boatSpaceId}"
                    hx-trigger="click"
                    hx-get="/virkailija/kayttaja/${result.citizenId}"
                    hx-push-url="true"
                    hx-target=".section"
                    hx-select=".section">
                    <td>${result.locationName}</td>
                    <td>
                        <span>${result.place}</span>
                        ${if (result.hasWarning("BoatWidth") || result.hasWarning("BoatLength")) icons.warningExclamation(false) else ""}
                    </td>
                    <td>${t("boatSpaces.type${result.type}Option")}</td>
                    <td><a href="/virkailija/kayttaja/${result.citizenId}">${result.firstName} ${result.lastName}</a></td>
                    <td>${result.homeTown}</td>
                    <td></td>
                    <td>$startDateFormatted</td>
                    <td>$endDateFormatted</td>
                    <td class="has-text-centered">
                        <div class="is-flex is-align-items-center is-justify-content-center">
                            <p>${t("boatApplication.ownershipOption.${result.boatOwnership}")}</p>
                            ${if (result.hasWarning(
                        "BoatFutureOwner"
                    ) || result.hasWarning("BoatCoOwner")
                ) {
                    icons.warningExclamation(false)
                } else {
                    ""
                }}
                        </div>
                    </td>
                </tr>
                """.trimIndent()
            }

        // language=HTML
        return """
            <section class="section">
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
                    >
                        <input type="text" name="sortBy" id="sortColumn" value="${params.sortBy}" style="visibility: hidden">
                        <input type="text" name="ascending" id="sortDirection" value="${params.ascending}" style="visibility: hidden">

                        <div class="block reservation-list-header">
                            <div class="field">
                                <p class="control has-icons-left has-icons-right">
                                    <input class="input search-input" type="text" name="search" 
                                        aria-label="${t("boatSpaces.searchButton")}"/>
                                    <span class="icon is-small is-left">${icons.search}</span>
                                    <span class="icon is-small is-right">${icons.cross}</span>
                                </p>
                            </div>
                            <a class="add-reservation" href="/virkailija/venepaikat/varaukset/luo">
                                <span class="icon is-large">${icons.plusRound}</span>
                                <span class="label">Luo varaus</span>
                            </a>
                        </div>

                        <div class="block">
                            <h1 class="label">${t("boatSpaceReservation.title.harbor")}</h1>
                            <div class="tag-container">
                                $harborFilters
                            </div>
                        </div>

                        <div class="block">
                            <h1 class="label">${t("boatSpaceReservation.title.amenity")}</h1>
                            <div class="tag-container">
                                $amenityFilters
                            </div>
                        </div>
                        <div class="block">
                            <h1 class="label">${t("boatSpaceReservation.title.payment")}</h1>
                            <div class="tag-container">
                                $paymentFilters
                            </div>
                        </div>
                        <div class="block">
                        </div>

                        <div class="reservation-list block">
                            <table class="table is-hoverable">
                                <thead>
                                <tr>
                                    <td>
                                        <a href="#" @click.prevent="updateSort('PLACE')">
                                            <span>${t("boatSpaceReservation.title.harbor")}</span>
                                            ${icons.sort(params.getSortForColumn("PLACE"))}
                                        </a>
                                    </td>
                                    <th>
                                        <a href="#" @click.prevent="updateSort('PLACE')">
                                            <span>${t("boatSpaceReservation.title.place")}</span>
                                            ${icons.sort(params.getSortForColumn("PLACE"))}
                                        </a>
                                    </th>
                                    <th>
                                        <a href="#" @click.prevent="updateSort('PLACE_TYPE')">
                                            <span>${t("boatSpaceReservation.title.type")}</span>
                                            ${icons.sort(params.getSortForColumn("PLACE_TYPE"))}
                                        </a>
                                    </th>
                                    <th>
                                        <a href="#" @click.prevent="updateSort('CUSTOMER')">
                                            <span>${t("boatSpaceReservation.title.subject")}</span>
                                            ${icons.sort(params.getSortForColumn("CUSTOMER"))}
                                        </a>
                                    </th>
                                    <th>
                                        <a href="#" @click.prevent="updateSort('HOME_TOWN')">
                                            <span>${t("boatSpaceReservation.title.homeTown")}</span>
                                            ${icons.sort(params.getSortForColumn("HOME_TOWN"))}
                                        </a>
                                    </th>
                                    <th><span>${t("boatSpaceReservation.title.payment")}</span></th>
                                    <th>
                                        <a href="#" @click.prevent="updateSort('START_DATE')">
                                            <span>${t("boatSpaceReservation.title.startDate")}</span>
                                            ${icons.sort(params.getSortForColumn("START_DATE"))}
                                        </a>
                                    </th>
                                    <th>
                                        <a href="#" @click.prevent="updateSort('END_DATE')">
                                            <span>${t("boatSpaceReservation.title.endDate")}</span>
                                            ${icons.sort(params.getSortForColumn("END_DATE"))}
                                        </a>
                                    </th>
                                    <th><span>${t("boatSpaceReservation.title.ownership")}</span></th>
                                </tr>
                                
                                <tr>
                                    <th>
                                    </th>
                                    <th>
                                    </th>
                                    <th>
                                    </th>
                                    <th>
                                        $nameSearchInput
                                    </th>
                                    <th>
                                    </th>
                                    <th>
                                    </th>
                                    <th>
                                    </th>
                                    <th>
                                    </th>
                                    <th>
                                    
                                    
                                    </th>
                                </tr>
                                </thead>
                                <tbody id="table-body">
                                $reservationRows
                                </tbody>
                            </table>
                        </div>
                    </form>
                </div>
            </section>
            """.trimIndent()
    }
}
