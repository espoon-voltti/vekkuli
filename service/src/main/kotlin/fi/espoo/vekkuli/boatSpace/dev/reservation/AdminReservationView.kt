package fi.espoo.vekkuli.boatSpace.dev.reservation

import fi.espoo.vekkuli.FormComponents
import fi.espoo.vekkuli.RadioOption
import fi.espoo.vekkuli.config.MessageUtil
import fi.espoo.vekkuli.controllers.EnvType
import fi.espoo.vekkuli.controllers.Utils.Companion.getEnv
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

enum class UserColumn {
    Reserver,
    ActingUser,
    Employee;

    fun toName(): String =
        when (this) {
            Reserver -> "reserver_id"
            ActingUser -> "acting_user_id"
            Employee -> "employee_id"
        }
}

@Service
class ReservationView {
    @Autowired
    lateinit var components: FormComponents

    @Autowired
    lateinit var messageUtil: MessageUtil

    fun render(resultMessage: String? = null): String {
        if (getEnv() == EnvType.Production) {
            return ""
        }
        val radios =
            components.radioButtons(
                "admin.reservation.title",
                "user",
                UserColumn.Reserver.name,
                UserColumn.entries.map { RadioOption(it.name, messageUtil.getMessage("admin.reservation.userOption.${it.name}")) }
            )
        return """
            <section class="section" id="reservation-clear">
                <div class="container">
                   <h2>Varausten poisto</h2>
                   <form hx-post="/dev/reservations"
                         hx-target="#reservation-clear">
                      <div class="field">
                        <label class="label">Nimi</label>
                        <div class="control">
                            <input type="text" name="reserverName"/>
                        </div>
                      </div>
                      $radios
                      <button class="button is-primary">Poista varaukset</button>
                      ${if (resultMessage.isNullOrBlank()) "" else """<p class="has-text-success"">$resultMessage</p>"""}
                   </form>
                </div>
            </section>
            """.trimIndent()
    }
}
