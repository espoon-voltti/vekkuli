package fi.espoo.vekkuli.boatSpace.reservationForm.components

import fi.espoo.vekkuli.boatSpace.reservationForm.ReservationInput
import fi.espoo.vekkuli.controllers.UserType
import fi.espoo.vekkuli.domain.CitizenWithDetails
import fi.espoo.vekkuli.domain.Municipality
import fi.espoo.vekkuli.views.BaseView
import org.springframework.stereotype.Component

// language=HTML
@Component
class CitizenContainer(
    private val citizenContainerForCitizen: CitizenContainerForCitizen,
    private val citizenContainerForEmployee: CitizenContainerForEmployee
) : BaseView() {
    fun render(
        userType: UserType,
        reservationId: Int,
        input: ReservationInput,
        citizen: CitizenWithDetails?,
        municipalities: List<Municipality>
    ): String {
        val citizenContainer =
            if (userType == UserType.CITIZEN) {
                citizenContainerForCitizen.render(input, citizen)
            } else {
                citizenContainerForEmployee.render(userType, reservationId, input, citizen, municipalities)
            }

        // language=HTML
        return """
            <div class='form-section'>
                <h3 class="header">${t("boatApplication.title.reserver")}</h3>
                $citizenContainer
            </div>
            """.trimIndent()
    }
}
