package fi.espoo.vekkuli.views.citizen

import org.springframework.stereotype.Service

@Service
class Home {
    fun render(): String {
        // language=HTML
        return """
            <section class="section">
                <div class="container">
                    <h1 class="title">Varaukset</h1>
                    <div><a href="/kuntalainen/venepaikat">Hae venepaikkaa</a></div>
                </div>
            </section>
            """.trimIndent()
    }
}
