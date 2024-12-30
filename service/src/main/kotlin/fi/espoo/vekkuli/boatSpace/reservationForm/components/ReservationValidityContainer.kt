package fi.espoo.vekkuli.boatSpace.reservationForm.components

import fi.espoo.vekkuli.FormComponents
import fi.espoo.vekkuli.RadioOption
import fi.espoo.vekkuli.domain.ReservationValidity
import fi.espoo.vekkuli.views.BaseView
import org.springframework.stereotype.Component

// language=HTML
@Component
class ReservationValidityContainer(
    private val formComponents: FormComponents,
) : BaseView() {
    fun render(reservationValidity: ReservationValidity? = ReservationValidity.Indefinite): String {
        val radioButtons =
            formComponents.radioButtons(
                "boatApplication.title.reservationValidity",
                "reservationValidity",
                reservationValidity?.name,
                listOf(
                    RadioOption(ReservationValidity.Indefinite.name, t("boatApplication.title.reservationValidity.indefinite")),
                    RadioOption(ReservationValidity.FixedTerm.name, t("boatApplication.title.reservationValidity.fixedTerm"))
                ),
                mapOf("x-model" to "reservationValidity"),
                isColumnLayout = false
            )

        return """<div data-testid="reservation-validity-selector" >
             $radioButtons
           </div>
            """
    }
}
