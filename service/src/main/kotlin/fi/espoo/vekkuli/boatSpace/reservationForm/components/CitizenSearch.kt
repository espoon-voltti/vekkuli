package fi.espoo.vekkuli.boatSpace.reservationForm.components

import fi.espoo.vekkuli.views.BaseView
import org.springframework.stereotype.Component

@Component
class CitizenSearch : BaseView() {
    fun render(searchUrl: String): String { // language=HTML
        return """
            <div id="citizen-results-container" class="container" >
                <div class="field" id="customer-search-container">
                    <label class="label">${t("boatApplication.select.citizen")}</label>
                    <div class="control width-is-half">
                        <p class="control has-icons-left has-icons-right">
                            <input x-model="citizenFullName" id="customer-search" 
                                placeholder="${t("boatApplication.placeholder.searchCitizens")}"
                                name="nameParameter" class="input search-input" type="text" 
                                hx-get="$searchUrl" hx-trigger="keyup changed delay:500ms" 
                                hx-target="#citizen-results">
                            <span class="icon is-small is-left">
                                ${icons.search}
                            </span>
                            <span id="citizen-empty-input" x-show="citizenFullName != ''" class="icon is-small is-right is-clickable p-s" @click="citizenFullName = ''; citizenId = ''">
                                ${icons.xMark}
                            </span>
                        </p>
                               
                        <!-- Where the results will be displayed -->                    
                        <div id="citizen-results" class="select is-multiple" ></div>                   
                    </div>
                    <input id="citizenId" name="citizenId" x-model.fill="citizenId" data-required hidden />
                    <div id="citizenId-error-container">
                        <span id="citizenId-error" class="help is-danger" style="display: none" x-show="citizenId == ''">
                            ${t("validation.required")}
                        </span>
                    </div>
                </div>
                
            </div>
            """.trimIndent()
    }
}
