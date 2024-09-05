package fi.espoo.vekkuli.views.employee

import fi.espoo.vekkuli.config.MessageUtil
import fi.espoo.vekkuli.domain.BoatSpaceAmenity
import fi.espoo.vekkuli.domain.BoatSpaceReservationFilter
import fi.espoo.vekkuli.domain.BoatSpaceReservationItem
import fi.espoo.vekkuli.domain.Location
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
        // Render the harbors as buttons
        val harborButtons =
            harbors.joinToString("\n") { harbor ->
                """
                <a class="button is-rounded is-primary ${if (params.hasHarbor(harbor.id)) "" else "is-outlined"}"
                   id="harbor-${harbor.id}"
                   href="/virkailija/venepaikat/varaukset${params.toggleHarbor(harbor.id)}">
                    <span class="icon is-small" ${if (params.hasHarbor(harbor.id)) "" else "style='display:none;'"} >
                        ${icons.check}
                    </span>
                    <span>${harbor.name}</span>
                </a>
                """.trimIndent()
            }

        // Render the amenities as buttons
        val amenityButtons =
            amenities.joinToString("\n") { amenity ->
                """
                <a class="button is-rounded is-primary ${if (params.hasAmenity(amenity)) "" else "is-outlined"}"
                   id="amenity-${amenity.name.lowercase()}"
                   href="/virkailija/venepaikat/varaukset${params.toggleAmenity(amenity.name)}">
                    <span class="icon is-small" ${if (params.hasAmenity(amenity)) "" else "style='display:none;'"} style="stroke: white">
                        ${icons.check}
                    </span>
                    <span>${t("boatSpaces.amenityOption.$amenity")}</span>
                </a>
                """.trimIndent()
            }

        // Render the reservations list
        val reservationsRows =
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
                        ${if (result.hasWarning(
                        "BoatWidth"
                    ) || result.hasWarning("BoatLength")
                ) {
                    icons.warningExclamation(false)
                } else {
                    ""
                }}
                    </td>
                    <td>${t("boatSpaces.type${result.type}Option")}</td>
                    <td><a href="/virkailija/kayttaja/${result.citizenId}">
                        <span>${result.firstName}</span><br /><span>${result.lastName}</span>
                        </a></td>
                    <td>${result.homeTown}</td>
                    <td>${result.boatRegistrationCode ?: ""}</td>
                    <td><!-- TODO payment date and warning --></td>
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

        //language=HTML
        return """
            <section class="section">
                <div class="container">
                    <div class="block reservation-list-header">
                        <div class="field">
                            <p class="control has-icons-left has-icons-right">
                                <input class="input search-input" type="text" aria-label="${t("boatSpaces.searchButton")}"/>
                                <span class="icon is-small is-left">
                                    ${icons.search}
                                </span>
                                <span class="icon is-small is-right">
                                    ${icons.cross}
                                </span>
                            </p>
                        </div>

                        <a class="add-reservation" href="/virkailija/venepaikat/varaukset/luo">
                           <span class="icon is-large">
                               ${icons.plusRound}
                           </span>
                            <span class="label">Luo varaus</span>
                        </a>
                    </div>
                    <div class="block">
                        <h1 class="label">${t("boatSpaceReservation.title.harbor")}</h1>
                        <div class="tag-container">
                            $harborButtons
                        </div>
                    </div>
                    <div class="block">
                        <h1 class="label">${t("boatSpaceReservation.title.amenity")}</h1>
                        <div class="tag-container">
                            $amenityButtons
                        </div>
                    </div>

                    <div class="reservation-list block">
                        <table class="table is-hoverable">
                            <thead>
                            <tr>
                                <th>
                                    <a href="/virkailija/venepaikat/varaukset${params.toggleSort("PLACE")}">
                                        <span>${t("boatSpaceReservation.title.harbor")}</span>
                                        ${icons.sort(params.getSortForColumn("PLACE"))}
                                    </a>
                                </th>
                                <th>
                                    <a href="/virkailija/venepaikat/varaukset${params.toggleSort("PLACE")}">
                                        <span>${t("boatSpaceReservation.title.place")}</span>
                                        ${icons.sort(params.getSortForColumn("PLACE"))}
                                    </a>
                                </th>
                                <th>
                                    <a href="/virkailija/venepaikat/varaukset${params.toggleSort("PLACE_TYPE")}">
                                        <span>${t("boatSpaceReservation.title.type")}</span>
                                        ${icons.sort(params.getSortForColumn("PLACE_TYPE"))}
                                    </a>
                                </th>
                                <th>
                                    <a href="/virkailija/venepaikat/varaukset${params.toggleSort("CUSTOMER")}">
                                        <span>${t("boatSpaceReservation.title.subject")}</span>
                                        ${icons.sort(params.getSortForColumn("CUSTOMER"))}
                                    </a>
                                </th>
                                <th>
                                    <a href="/virkailija/venepaikat/varaukset${params.toggleSort("HOME_TOWN")}">
                                        <span>${t("boatSpaceReservation.title.homeTown")}</span>
                                        ${icons.sort(params.getSortForColumn("HOME_TOWN"))}
                                    </a>
                                </th>
                                <th>
                                    <a href="/virkailija/venepaikat/varaukset${params.toggleSort("BOAT")}">
                                        <span>${t("boatSpaceReservation.title.boat")}</span>
                                        ${icons.sort(params.getSortForColumn("BOAT"))}
                                    </a>
                                </th>
                                <th><span>${t("boatSpaceReservation.title.payment")}</span></th>
                                <th>
                                    <a href="/virkailija/venepaikat/varaukset${params.toggleSort("START_DATE")}">
                                        <span>${t("boatSpaceReservation.title.startDate")}</span>
                                        ${icons.sort(params.getSortForColumn("START_DATE"))}
                                    </a>
                                </th>
                                <th>
                                    <a href="/virkailija/venepaikat/varaukset${params.toggleSort("END_DATE")}">
                                        <span>${t("boatSpaceReservation.title.endDate")}</span>
                                        ${icons.sort(params.getSortForColumn("END_DATE"))}
                                    </a>
                                </th>
                                <th><span>${t("boatSpaceReservation.title.ownership")}</span></th>
                            </tr>
                            </thead>
                            <tbody>
                            $reservationsRows
                            </tbody>
                        </table>
                    </div>
                </div>
            </section>
            """.trimIndent()
    }
}
