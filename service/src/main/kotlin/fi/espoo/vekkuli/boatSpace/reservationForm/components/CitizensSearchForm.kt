package fi.espoo.vekkuli.boatSpace.reservationForm.components

import fi.espoo.vekkuli.domain.CitizenWithDetails
import fi.espoo.vekkuli.views.BaseView
import org.springframework.stereotype.Component

// language=HTML
@Component
class CitizensSearchForm : BaseView() {
    fun render(
        citizens: List<CitizenWithDetails>,
        reservationId: Int
    ): String {
        // language=HTML
        val listSize = if (citizens.size > 5) 5 else citizens.size
        return (
            """
            <select 
                x-show="citizenFullName != ''" 
                multiple 
                size="$listSize" 
                name='citizenIdOption' 
                hx-get="/virkailija/venepaikka/varaus/$reservationId"  
                hx-include="#form"
                hx-trigger="change" 
                hx-select="#form-inputs"
                hx-target="#form-inputs" @change="updateFullName">
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
            </select>

            """.trimIndent()
        )
    }
}
