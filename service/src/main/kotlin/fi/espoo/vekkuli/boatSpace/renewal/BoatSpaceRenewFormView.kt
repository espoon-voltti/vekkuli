package fi.espoo.vekkuli.boatSpace.renewal

import fi.espoo.vekkuli.controllers.UserType
import fi.espoo.vekkuli.domain.*
import fi.espoo.vekkuli.views.BaseView
import org.springframework.stereotype.Service

data class BoatSpaceRenewViewParams(
    val reservation: RenewalReservationForApplicationForm,
    val boats: List<Boat>,
    val citizen: CitizenWithDetails? = null,
    val input: ModifyReservationInput,
    val userType: UserType,
)

@Service
class BoatSpaceRenewFormView : BaseView() {
    fun invoiceErrorPage() =
        """
        <section class="section">
            <div class="container">
                <h2 class="title pb-l">${t("boatSpaceRenewal.title.errorPage")}</h2>
                <p>${t("boatSpaceRenewal.errorPage")}</p>
            </div>
        </section>
        """.trimIndent()
}
