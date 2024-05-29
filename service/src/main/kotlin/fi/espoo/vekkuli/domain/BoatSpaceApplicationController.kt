package fi.espoo.vekkuli.domain

import fi.espoo.vekkuli.config.MessageUtil
import jakarta.validation.constraints.Min
import kotlinx.html.*
import kotlinx.html.stream.appendHTML
import kotlinx.html.stream.createHTML
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
    fun BoatSpaceApplication(
    ): String {
        val boatTypes = listOf(
            "Rowboat",
            "OutboardMotor",
            "InboardMotor",
            "Sailboat",
            "JetSki"
        )
        val locations = jdbi.inTransactionUnchecked { tx ->
            tx.getLocations()
        }
        return createHTML().html {
            lang = "fi"
            attributes["class"] = "theme-light"
            head {
                title { +"Hae venepaikkaa" }
                link(
                    rel = "stylesheet",
                    href = "https://cdn.jsdelivr.net/npm/bulma@1.0.0/css/bulma.min.css"
                )
                script(src = "https://unpkg.com/htmx.org@1.9.12") {}
                meta {
                    name = "viewport"
                    content = "width=device-width, initial-scale=1"
                }
            }
            body {
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
    }

    @GetMapping("/partial/venepaikkatoiveet", produces = [TEXT_HTML_UTF8])
    fun addHarborOption(@RequestParam locationId: List<String>): String {
        return buildString {
            appendHTML().harbourOptions(locationId)
        }
    }

    private fun TagConsumer<*>.harbourOptions(harbors: List<String>) {
        val locations = jdbi.inTransactionUnchecked { tx ->
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
                                id = "locationId${index}"
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

    fun FlowContent.numberField(label: String, id: String) {
        div("field") {
            label("label") {
                attributes["for"] = "width"
                +label
            }
            div("control") {
                consumer.numberInput(id)
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

    fun FlowContent.textField(label: String, id: String, type: InputType = InputType.text, pattern: String? = null) {
        div("field") {
            label("label") {
                +label
            }
            div("control") {
                input {
                    classes = setOf("input")
                    this.type = type
                    name = id
                    required = true
                    if (pattern != null) {
                        this.pattern = pattern
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
        return "Hakemus vastaanotettu"
    }
}