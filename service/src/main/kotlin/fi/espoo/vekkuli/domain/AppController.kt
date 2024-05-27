// SPDX-FileCopyrightText: 2023-2024 City of Espoo
//
// SPDX-License-Identifier: LGPL-2.1-or-later

package fi.espoo.vekkuli.domain

import fi.espoo.vekkuli.config.MessageUtil
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.constraints.Min
import kotlinx.html.*
import kotlinx.html.stream.appendHTML
import kotlinx.html.stream.createHTML
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.inTransactionUnchecked
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam


import org.springframework.web.bind.annotation.RestController

const val TEXT_HTML_UTF8 = "${MediaType.TEXT_HTML_VALUE};charset=UTF-8"

@RestController
class AppController {
    @Autowired
    lateinit var jdbi: Jdbi

    @Autowired
    lateinit var messageUtil: MessageUtil

    companion object {
        const val WIDTH_MIN_TOLERANCE = 40
        const val WIDTH_MAX_TOLERANCE = 100
    }

    @GetMapping("/", produces = [TEXT_HTML_UTF8])
    fun example(
        @RequestParam @Min(1) page: Int = 1,
        @RequestParam pageSize: Int = 25,
        @RequestParam @Min(0) width: Int?,
        @RequestParam @Min(0) length: Int?,
        @RequestParam locationId: Int?,
        @RequestParam amenity: BoatSpaceAmenity?
    ): String {
        val locations = jdbi.inTransactionUnchecked { tx ->
            tx.getLocations()
        }

        return createHTML().html {
            attributes["class"] = "theme-light"
            head {
                title { +"Venepaikat" }
                link(
                    rel = "stylesheet",
                    href = "https://cdn.jsdelivr.net/npm/bulma@1.0.0/css/bulma.min.css"
                )
                script(src = "https://unpkg.com/htmx.org@1.9.2") {}
            }
            body {
                section("section") {
                    div("container") {
                        h1("title") { +messageUtil.getMessage("boatSpaces.title") }
                        div("box") {
                            form {
                                id = "form"
                                action = "/"
                                method = FormMethod.get
                                attributes["hx-trigger"] = "input delay:1s, change"
                                attributes["hx-get"] = "/partial/boat-spaces"
                                attributes["hx-target"] = "#boatSlipTableDiv"
                                attributes["hx-swap"] = "innerHTML"
                                div("columns") {
                                    div("column") {
                                        div("field") {
                                            label("label") {
                                                +"${messageUtil.getMessage("boatSpaces.widthLabel")}: "
                                            }
                                            div("control") {
                                                consumer.numberInput("width", width)
                                            }
                                        }

                                        div("field") {
                                            label("label") {
                                                +"${messageUtil.getMessage("boatSpaces.lengthLabel")}: "
                                            }
                                            div("control") {
                                                consumer.numberInput("length", length)
                                            }
                                        }
                                    }
                                    div("column") {
                                        div("field") {
                                            label("label") {
                                                +messageUtil.getMessage("boatSpaces.amenityLabel")
                                            }
                                            div("select") {
                                                select {
                                                    name = "amenity"
                                                    option {
                                                        value = ""
                                                        if (amenity == null) {
                                                            attributes["selected"] = "selected"
                                                        }
                                                        +messageUtil.getMessage("boatSpaces.noneOption")
                                                    }
                                                    option {
                                                        value = "Buoy"
                                                        if (amenity == BoatSpaceAmenity.Buoy) {
                                                            attributes["selected"] = "selected"
                                                        }
                                                        +messageUtil.getMessage("boatSpaces.buoyOption")
                                                    }
                                                    option {
                                                        value = "RearBuoy"
                                                        if (amenity == BoatSpaceAmenity.RearBuoy) {
                                                            attributes["selected"] = "selected"
                                                        }
                                                        +messageUtil.getMessage("boatSpaces.rearBuoyOption")
                                                    }
                                                    option {
                                                        value = "Beam"
                                                        if (amenity == BoatSpaceAmenity.Beam) {
                                                            attributes["selected"] = "selected"
                                                        }
                                                        +messageUtil.getMessage("boatSpaces.beamOption")
                                                    }
                                                    option {
                                                        value = "WalkBeam"
                                                        if (amenity == BoatSpaceAmenity.WalkBeam) {
                                                            attributes["selected"] = "selected"
                                                        }
                                                        +messageUtil.getMessage("boatSpaces.walkBeamOption")
                                                    }
                                                }
                                            }
                                        }
                                        div("field") {
                                            label("label") {
                                                +messageUtil.getMessage("boatSpaces.harborHeader")
                                            }
                                            div("select") {
                                                select {
                                                    name = "locationId"
                                                    option {
                                                        value = ""
                                                        +messageUtil.getMessage("boatSpaces.noneOption")
                                                    }
                                                    locations.forEach {
                                                        option {
                                                            value = it.id
                                                            +it.name
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        div {
                            id = "boatSlipTableDiv"
                            consumer.boatSpaces(page, pageSize, width, length, amenity, locationId, page)
                        }
                    }
                }
            }
        }
    }

    private fun TagConsumer<*>.numberInput(id: String, value: Int? = null) {
        input(InputType.number, name = id, classes = "input") {
            this.id = id
            style = "width: 100px"
            value?.let { this.value = it.toString() }
        }
    }

    @GetMapping(
        "/partial/boat-spaces",
    )
    fun partialBoatSlipTable(
        @RequestParam @Min(0) page: Int = 1,
        @RequestParam pageSize: Int = 25,
        @RequestParam width: Int?,
        @RequestParam length: Int?,
        @RequestParam locationId: Int?,
        @RequestParam amenity: BoatSpaceAmenity?,
        response: HttpServletResponse
    ): String {
        val qs = createQueryString(page, pageSize, width, length, locationId, amenity)
        response.setHeader(
            "HX-Push-Url",
            qs
        )
        return buildString { appendHTML().boatSpaces(page, pageSize, width, length, amenity, locationId, page) }
    }

    private fun TagConsumer<*>.boatSpaces(
        page: Int,
        pageSize: Int,
        width: Int?,
        length: Int?,
        amenity: BoatSpaceAmenity?,
        locationId: Int?,
        currentPage: Int
    ) {
        val boatSlips = jdbi.inTransactionUnchecked { tx ->
            tx.getBoatSpaces(
                BoatSpaceFilter(
                    page = page,
                    pageSize = pageSize,
                    minWidth = width?.minus(WIDTH_MIN_TOLERANCE),
                    maxWidth = width?.plus(WIDTH_MAX_TOLERANCE),
                    minLength = length,
                    maxLength = null,
                    amenity = amenity,
                    locationId = locationId,
                )
            )
        }
        if (boatSlips.isEmpty()) {
            div {
                h2 { +"Ei tuloksia" }
            }
            return
        }
        val pages = boatSlips[0].totalCount / pageSize + (if (boatSlips[0].totalCount % pageSize > 0) 1 else 0)
        println("pages: $pages")
        val nextPage = if (currentPage < pages) currentPage + 1 else null
        val prevPage = if (currentPage > 1) currentPage - 1 else null
        div {
            boatSpaceTable(boatSlips.toList())
            nav("pagination is-centered ") {
                a(classes = "pagination-previous ${if (prevPage == null) "is-disabled" else ""}") {
                    if (prevPage != null) {
                        attributes["hx-get"] = "/partial/boat-spaces"
                        attributes["hx-target"] = "#boatSlipTableDiv"
                        attributes["hx-swap"] = "innerHTML"
                        attributes["hx-include"] = "#form"
                        attributes["hx-vals"] = "js:{page: $prevPage}"
                    }
                    +"Edellinen"
                }
                span(classes = "pagination-list") {
                    +"$currentPage/$pages"
                }
                a(classes = "pagination-next ${if (nextPage == null) "is-disabled" else ""}") {
                    if (nextPage != null) {
                        attributes["hx-get"] = "/partial/boat-spaces"
                        attributes["hx-target"] = "#boatSlipTableDiv"
                        attributes["hx-swap"] = "innerHTML"
                        attributes["hx-include"] = "#form"
                        attributes["hx-vals"] = "js:{page: $nextPage}"
                    }
                    +"Seuraava"
                }
            }
        }
    }

    fun createQueryString(
        page: Int = 1,
        pageSize: Int = 25,
        width: Int?,
        length: Int?,
        locationId: Int?,
        amenity: BoatSpaceAmenity?
    ): String {
        val queryString = StringBuilder("?")
        queryString.append("page=$page&pageSize=$pageSize")
        width?.let { queryString.append("&width=$it") }
        length?.let { queryString.append("&length=$it") }
        locationId?.let { queryString.append("&locationId=$it") }
        amenity?.let { queryString.append("&amenity=$it") }
        return queryString.toString()
    }

    private fun TagConsumer<*>.boatSpaceTable(boatSlips: List<BoatSpace>) {
        table("table is-fullwidth is-striped is-hoverable ") {
            id = "boatSlipTable"
            thead {
                tr {
                    th { +messageUtil.getMessage("boatSpaces.harborHeader") }
                    th { +messageUtil.getMessage("boatSpaces.pierHeader") }
                    th { +messageUtil.getMessage("boatSpaces.placeNumberHeader") }
                    th { +messageUtil.getMessage("boatSpaces.amenityHeader") }
                    th { +messageUtil.getMessage("boatSpaces.widthHeader") }
                    th { +messageUtil.getMessage("boatSpaces.lengthHeader") }
                    th { +messageUtil.getMessage("boatSpaces.descriptionHeader") }
                }
            }
            tbody {
                boatSlips.forEach { slip ->
                    tr {
                        td { +slip.locationName }
                        td { +slip.section }
                        td { +slip.placeNumber.toString() }
                        td {
                            +messageUtil.getMessage(
                                "boatSpaces.${
                                    slip.amenity.toString().replaceFirstChar(Char::lowercase)
                                }"
                            )
                        }
                        td { +slip.widthCm.toString() }
                        td { +slip.lengthCm.toString() }
                        td { +slip.description }
                    }
                }
            }
        }
    }
}
