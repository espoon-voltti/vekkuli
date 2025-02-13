package fi.espoo.vekkuli.boatSpace.boatSpaceList

import fi.espoo.vekkuli.domain.*
import fi.espoo.vekkuli.utils.addTestId
import fi.espoo.vekkuli.views.BaseView
import org.springframework.stereotype.Service
import java.util.*

@Service
class BoatSpaceList : BaseView() {
    fun t(key: String): String = messageUtil.getMessage(key)

    fun render(boatSpaces: List<BoatSpaceListRow>): String {
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
                }>${result.placeNumber}</a>
                    </td>
                    <td>${t("employee.boatSpaceReservations.types.${result.type}")}</td>
                    <td>${t("boatSpaces.amenityOption.${result.amenity}")}</td>
                   
                    <td>${result.widthCm}</td>
                    <td>${result.lengthCm}</td>
                    <td>${result.priceInEuro}</td>
                    <td>${if (result.active) t("boatSpacesList.text.active") else t("boatSpacesList.text.inactive")}</td>
                    <td>${if (result.reserved) t("boatSpacesList.text.reserved") else t("boatSpacesList.text.notReserved")}</td>
                    <td>${result.validity ?: '-'}</td>
                </tr>
                """.trimIndent()
            }

        // language=HTML
        return """
            <section class="section">
               
                <div class="container" >
                    <form id="reservation-filter-form"
                          hx-get="/virkailija/venepaikat/varaukset"
                          hx-target="#table-body"
                          hx-select="#table-body"
                          hx-trigger="change, keyup delay:500ms"
                          hx-swap="outerHTML"
                          hx-push-url="true"
                          hx-indicator="#loader, .loaded-content"
                    >
                        <div class="employee-filter-container">                        
                            <div class="filter-group">
                                <h1 class="label">${t("boatSpaceReservation.title.harbor")}</h1>
                               
                            </div>
                            <div class="filter-group">
                                <h1 class="label">${t("boatSpaceReservation.title.expiration")}</h1>
                                
                            </div>
                            <div class="filter-group">
                                <h1 class="label">${t("boatSpaceReservation.title.paymentState")}</h1>
                                
                            </div>                            
                            <div class="filter-group">
                                <h1 class="label">${t("employee.boatReservations.title.reservationValidity")}</h1>
                               
                            </div>                            
                        </div>
                        
                        <div class="employee-filter-container">
                            <div class="filter-group">
                              <h1 class="label">${t("boatSpaceReservation.title.type")}</h1>
                              <div class="tag-container">
                              </div>
                            </div>
                        </div>


                        <div class="reservation-list form-section block">
                        <div class='table-container'>
                            <table class="table is-hoverable">
                                <thead>
                                <tr class="table-borderless">
                                    <th></th>
                                    <th class="nowrap">
                                        ${t("boatSpaceList.title.harbor")}
                                    </th>
                                    <th class="nowrap">
                                    
                                        ${t("boatSpaceList.title.place")}
                                    </th>

                                    <th class="nowrap">
                                        ${ t("boatSpaceList.title.type")}
                                    </th>
                                    <th class="nowrap">
                                        ${ t("boatSpaceList.title.amenity")}
                                    </th>
                                    
                                                                   
                                    <th class="nowrap">
                                        ${ t("boatSpaceList.title.widthInMeters")}
                                    </th>
                                    <th><span class="reservation-table-header">
                                        ${t("boatSpaceList.title.lengthInMeters")}
                                    </span></th>
                                    <th class="nowrap">
                                        ${ t("boatSpaceList.title.price")}
                                    </th>
                                    <th class="nowrap">
                                        ${ t("boatSpaceList.title.state")}
                                    </th> 
                                    <th class="nowrap">
                                        ${ t("boatSpaceList.title.reservation")}
                                    </th> 
                                    <th class="nowrap">
                                        ${ t("boatSpaceList.title.reservationStatus")}
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
                            <div id="loader" class="htmx-indicator"> ${icons.spinner} <div>
                        </div>
                    </form>
                </div>
            </section>
            """.trimIndent()
    }
}
