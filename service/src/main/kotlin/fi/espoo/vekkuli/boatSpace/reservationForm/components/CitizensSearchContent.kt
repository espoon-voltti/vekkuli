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
                    <li id="option-$index" role="option" class="citizen-search-option" tabindex="-1"
                        data-citizen-id="${citizen.id}" data-fullname="${citizen.fullName}">
                        <p>${citizen.fullName}
                        <span class='is-small'>${citizen.birthday}</span></p>
                    </li>
                    """.trimIndent()
                }
            }

            """.trimIndent()
        )
    }
}
