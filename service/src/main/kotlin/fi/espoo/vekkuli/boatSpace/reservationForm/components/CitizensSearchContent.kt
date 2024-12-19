package fi.espoo.vekkuli.boatSpace.reservationForm.components

import fi.espoo.vekkuli.domain.CitizenWithDetails
import fi.espoo.vekkuli.views.BaseView
import org.springframework.stereotype.Component

// language=HTML
@Component
class CitizensSearchContent : BaseView() {
    fun searchContentList(citizens: List<CitizenWithDetails>): String {
        // language=HTML
        return (
            """
            ${
                citizens.withIndex().joinToString("\n") { (index, citizen) ->
                    """
                    <option id="option-$index" role="option" value="${citizen.id}" 
                        data-fullname="${citizen.fullName}">
                        <p>${citizen.fullName}
                        <span class='is-small'>${citizen.birthday}</span></p>
                    </option>
                    """.trimIndent()
                }
            }

            """.trimIndent()
        )
    }
}
