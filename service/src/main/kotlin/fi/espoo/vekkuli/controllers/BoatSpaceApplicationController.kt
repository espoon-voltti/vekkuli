// SPDX-FileCopyrightText: 2023-2024 City of Espoo
//
// SPDX-License-Identifier: LGPL-2.1-or-later

package fi.espoo.vekkuli.controllers

import fi.espoo.vekkuli.config.MessageUtil
import fi.espoo.vekkuli.domain.*
import kotlinx.html.*
import kotlinx.html.stream.appendHTML
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.inTransactionUnchecked
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class BoatSpaceApplicationController {
    @Autowired
    lateinit var jdbi: Jdbi

    @Autowired
    lateinit var messageUtil: MessageUtil

    @GetMapping("/venepaikkahakemus", produces = [TEXT_HTML_UTF8])
    fun boatSpaceApplication(): String {
        val boatTypes =
            listOf(
                "Rowboat",
                "OutboardMotor",
                "InboardMotor",
                "Sailboat",
                "JetSki"
            )
        return layout("Hae venepaikkaa") {
            section("section") {
                div("container") {
                    h1("title") { +"Hae venepaikkaa" }
                    form {
                        action = "/venepaikkahakemus"
                        method = FormMethod.post
                        h2("subtitle") { +"Henkilötiedot" }
                        textField("Nimi", "name")
                        textField("Sähköposti", "email", InputType.email, ".+@.+")
                        textField("Puhelinnumero", "phone", InputType.tel)
                        h2("subtitle") { +"Veneen tiedot" }

                        div("field") {
                            label("label") {
                                +"Venetyyppi"
                            }
                            div("select") {
                                select {
                                    name = "boatType"
                                    id = "boatType"
                                    required = true
                                    option {
                                        value = ""
                                        +"Valitse venetyyppi"
                                    }
                                    boatTypes.forEach {
                                        option {
                                            value = it
                                            +it
                                        }
                                    }
                                }
                            }
                        }
                        textField("Veneen nimi", "boatName")
                        textField("Rekisteritunnus", "registrationCode")
                        numberField("Pituus (cm)", "lengthInMeters")
                        numberField("Leveys (cm)", "widthInMeters")
                        numberField("Paino (kg)", "weightInKg")
                        h2("subtitle") { +"Satamatoiveet" }
                        consumer.harbourOptions(listOf())
                        br()
                        radioButtonGroup(
                            listOf(
                                RadioButton("Traileripaikka", "trailer"),
                                RadioButton(
                                    "Laituripaikka",
                                    "slip"
                                )
                            ),
                            "boatSpaceType",
                            "Venetyypit",
                        )
                        br()
                        button {
                            classes = setOf("button", "is-primary")
                            type = ButtonType.submit
                            +"Lähetä hakemus"
                        }
                    }
                }
            }
        }
    }

    data class RadioButton(val label: String, val value: String)

    private fun FlowContent.radioButtonGroup(
        radioButtonTitles: List<RadioButton>,
        name: String,
        label: String
    ) {
        div {
            p("label") {
                +label
            }
            radioButtonTitles.forEach { rb ->
                div {
                    input {
                        this.name = name
                        id = rb.value
                        type = InputType.radio
                        value = rb.value
                    }
                    label {
                        attributes["for"] = rb.value
                        +rb.label
                    }
                }
            }
        }
    }

    @GetMapping("/partial/venepaikkatoiveet", produces = [TEXT_HTML_UTF8])
    fun addHarborOption(
        @RequestParam locationId: List<String>
    ): String {
        return buildString {
            appendHTML().harbourOptions(locationId)
        }
    }

    private fun TagConsumer<*>.harbourOptions(harbors: List<String>) {
        val locations =
            jdbi.inTransactionUnchecked { tx ->
                tx.getLocations()
            }
        div {
            id = "harborOptions"
            harbors.forEachIndexed { index, harborValue ->
                div("field") {
                    id = "harborOption$index"
                    label("label") {
                        attributes["for"] = "locationId"
                        +"Satamatoive"
                    }
                    div("control") {
                        div("select") {
                            select {
                                name = "locationId"
                                id = "locationId$index"
                                classes = setOf("locationId")
                                required = true
                                option {
                                    value = ""
                                    +messageUtil.getMessage("boatSpaces.noneOption")
                                }
                                locations.forEach { location ->
                                    option {
                                        value = location.id
                                        selected = location.id == harborValue

                                        +location.name
                                    }
                                }
                            }
                        }
                        button {
                            classes = setOf("delete")
                            attributes["hx-get"] = "data:text/html,"
                            attributes["hx-target"] = "#harborOption$index"
                            attributes["hx-swap"] = "outerHTML"
                            attributes["style"] = "margin: 10px"
                        }
                    }
                }
            }
            div("field") {
                label("label") {
                    attributes["for"] = "locationId"
                    +"Satamatoive"
                }
                div("control") {
                    div("select") {
                        select {
                            name = "locationId"
                            id = "locationId"
                            classes = setOf("locationId")
                            required = true
                            attributes["hx-validate"] = "true"
                            option {
                                value = "none"
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
            br()
            div("field") {
                div("control") {
                    button {
                        classes = setOf("button")
                        attributes["hx-get"] = "/partial/venepaikkatoiveet"
                        attributes["hx-target"] = "#harborOptions"
                        attributes["hx-swap"] = "outerHTML"
                        attributes["hx-include"] = ".locationId"
                        +"Lisää satamatoive"
                    }
                }
            }
        }
    }

    @PostMapping("/venepaikkahakemus")
    fun submitBoatSpaceApplication(
        @RequestParam name: String,
        @RequestParam email: String,
        @RequestParam phone: String,
        @RequestParam boatType: String,
        @RequestParam boatName: String,
        @RequestParam registrationCode: String,
        @RequestParam lengthInMeters: Int,
        @RequestParam widthInMeters: Int,
        @RequestParam weightInKg: Int,
        @RequestParam locationId: List<String>
    ): String {
        val application =
            jdbi.inTransactionUnchecked { tx ->
                tx.insertBoatSpaceApplication(
                    AddBoatSpaceApplication(
                        type = BoatSpaceType.Slip,
                        boatType = BoatType.valueOf(boatType),
                        amenity = BoatSpaceAmenity.RearBuoy,
                        boatWidthCm = widthInMeters,
                        boatLengthCm = lengthInMeters,
                        boatWeightKg = weightInKg,
                        boatRegistrationCode = registrationCode,
                        information = "Hakija: $name, $email, $phone",
                        // TODO use real user identified when authentication is enabled
                        citizenId = 1,
                        locationWishes =
                            locationId.mapIndexed { index, id ->
                                AddLocationWish(
                                    locationId = id.toInt(),
                                    priority = index,
                                )
                            }
                    )
                )
            }
        return "Hakemus vastaanotettu"
    }
}
