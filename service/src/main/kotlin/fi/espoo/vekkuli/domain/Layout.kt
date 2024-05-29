package fi.espoo.vekkuli.domain

import kotlinx.html.*
import kotlinx.html.stream.createHTML

fun layout(title: String, block: HtmlBlockTag.() -> Unit): String {
    return createHTML().html {
        lang = "fi"
        attributes["class"] = "theme-light"
        head {
            title { +title }
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
            block()
        }
    }
}