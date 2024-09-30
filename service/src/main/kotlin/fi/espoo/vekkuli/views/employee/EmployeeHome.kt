package fi.espoo.vekkuli.views.citizen

import fi.espoo.vekkuli.config.MessageUtil
import org.springframework.stereotype.Service

@Service
class EmployeeHome(
    private val messageUtil: MessageUtil,
) {
    fun t(key: String): String = messageUtil.getMessage(key)

    fun render(
        isAuthenticatedEmployee: Boolean,
        userName: String
    ): String {
        // language=HTML
        val content =
            if (isAuthenticatedEmployee) {
                """
                <div class="block" th:if="$isAuthenticatedEmployee">
                  <p>Käyttäjä: <strong th:text="$userName"></strong></p>
                  <div><a href="/virkailija/venepaikat/varaukset">Venepaikat</a></div>
                  <br />
                </div>
                """.trimIndent()
            } else {
                ""
            }
        return """
            <section class="section">
              <div class="container">
                <h1 class="title">Varaukset</h1>
                <h2 class="subtitle">Virkailijan kirjautuminen</h2>
                $content
              </div>
            </section>
            """.trimIndent()
    }
}
