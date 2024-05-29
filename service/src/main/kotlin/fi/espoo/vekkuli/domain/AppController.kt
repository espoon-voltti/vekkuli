// SPDX-FileCopyrightText: 2023-2024 City of Espoo
//
// SPDX-License-Identifier: LGPL-2.1-or-later

package fi.espoo.vekkuli.domain

import fi.espoo.vekkuli.config.MessageUtil
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.constraints.Min
import kotlinx.html.*
import kotlinx.html.stream.appendHTML
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.inTransactionUnchecked
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam


import org.springframework.web.bind.annotation.RestController

const val TEXT_HTML_UTF8 = "${MediaType.TEXT_HTML_VALUE};charset=UTF-8"

fun Int.cmToM(): Float = this / 100F

fun Float.mToCm(): Int = (this * 100F).toInt()

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
        @RequestParam @Min(0) width: Float?,
        @RequestParam @Min(0) length: Float?,
        @RequestParam locationId: Int?,
        @RequestParam amenity: BoatSpaceAmenity?,
        @RequestParam boatSpaceType: BoatSpaceType?,
    ): String {
        val locations = jdbi.inTransactionUnchecked { tx ->
            tx.getLocations()
        }

        return layout("Venepaikat") {
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
                                            attributes["for"] = "width"
                                            +"${messageUtil.getMessage("boatSpaces.widthLabel")}: "
                                        }
                                        div("control") {
                                            consumer.numberInput("width", width)
                                        }
                                    }
                                    div("field") {
                                        label("label") {
                                            attributes["for"] = "length"
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
                                            attributes["for"] = "amenity"
                                            +messageUtil.getMessage("boatSpaces.amenityLabel")
                                        }
                                        div("select") {
                                            select {
                                                name = "amenity"
                                                id = "amenity"
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
                                            attributes["for"] = "locationId"
                                            +messageUtil.getMessage("boatSpaces.harborHeader")
                                        }
                                        div("select") {
                                            select {
                                                name = "locationId"
                                                id = "locationId"
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

                                    div("field") {
                                        label("label") {
                                            attributes["for"] = "boatSpaceType"
                                            +messageUtil.getMessage("boatSpaces.typeHeader")
                                        }
                                        div("select") {
                                            select {
                                                name = "boatSpaceType"
                                                id = "boatSpaceType"
                                                option {
                                                    value = ""
                                                    if (boatSpaceType == null) {
                                                        attributes["selected"] = "selected"
                                                    }
                                                    +messageUtil.getMessage("boatSpaces.noneOption")
                                                }
                                                option {
                                                    value = "Slip"
                                                    if (boatSpaceType == BoatSpaceType.Slip) {
                                                        attributes["selected"] = "selected"
                                                    }
                                                    +messageUtil.getMessage("boatSpaces.typeSlipOption")
                                                }
                                                option {
                                                    value = "Storage"
                                                    if (boatSpaceType == BoatSpaceType.Storage) {
                                                        attributes["selected"] = "selected"
                                                    }
                                                    +messageUtil.getMessage("boatSpaces.typeStorageOption")
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
                        consumer.boatSpaces(page, pageSize, width, length, amenity, locationId, page, boatSpaceType)
                    }
                }
            }
        }
    }

    private fun TagConsumer<*>.numberInput(id: String, value: Float? = null) {
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
        @RequestParam @Min(1) page: Int = 1,
        @RequestParam pageSize: Int = 25,
        @RequestParam @Min(0) width: Float?,
        @RequestParam @Min(0) length: Float?,
        @RequestParam locationId: Int?,
        @RequestParam amenity: BoatSpaceAmenity?,
        @RequestParam boatSpaceType: BoatSpaceType?,
        response: HttpServletResponse
    ): String {
        val qs = createQueryString(page, pageSize, width, length, locationId, amenity)
        response.setHeader(
            "HX-Push-Url",
            qs
        )
        return buildString {
            appendHTML().boatSpaces(
                page,
                pageSize,
                width,
                length,
                amenity,
                locationId,
                page,
                boatSpaceType
            )
        }
    }

    private fun TagConsumer<*>.boatSpaces(
        page: Int,
        pageSize: Int,
        width: Float?,
        length: Float?,
        amenity: BoatSpaceAmenity?,
        locationId: Int?,
        currentPage: Int,
        boatSpaceType: BoatSpaceType?
    ) {
        val boatSlips = jdbi.inTransactionUnchecked { tx ->
            tx.getBoatSpaces(
                BoatSpaceFilter(
                    page = page,
                    pageSize = pageSize,
                    minWidth = width?.mToCm()?.plus(WIDTH_MIN_TOLERANCE),
                    maxWidth = width?.mToCm()?.plus(WIDTH_MAX_TOLERANCE),
                    minLength = length?.mToCm(),
                    maxLength = null,
                    amenity = amenity,
                    locationId = locationId,
                    boatSpaceType = boatSpaceType,
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
        width: Float?,
        length: Float?,
        locationId: Int?,
        amenity: BoatSpaceAmenity?
    ): String {
        val queryString = StringBuilder("?")
        queryString.append("page=$page&pageSize=$pageSize")
        width?.let { queryString.append("&width=${it}") }
        length?.let { queryString.append("&length=${it}") }
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
                    th { +messageUtil.getMessage("boatSpaces.typeHeader") }
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
                        td {
                            +messageUtil.getMessage(
                                "boatSpaces.type${slip.type}Option"
                            )
                        }
                        td { +slip.section }
                        td { +slip.placeNumber.toString() }
                        td {
                            +messageUtil.getMessage(
                                "boatSpaces.${
                                    slip.amenity.toString().replaceFirstChar(Char::lowercase)
                                }"
                            )
                        }
                        td { +String.format("%.2f", slip.widthCm.cmToM()) }
                        td { +String.format("%.2f", slip.lengthCm.cmToM()) }
                        td { +slip.description }
                    }
                }
            }
        }
    }
}
