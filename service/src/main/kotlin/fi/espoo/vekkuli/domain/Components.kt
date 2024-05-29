// SPDX-FileCopyrightText: 2023-2024 City of Espoo
//
// SPDX-License-Identifier: LGPL-2.1-or-later

package fi.espoo.vekkuli.domain

import kotlinx.html.*

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
