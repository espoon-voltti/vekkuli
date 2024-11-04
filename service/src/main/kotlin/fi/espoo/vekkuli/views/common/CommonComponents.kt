package fi.espoo.vekkuli.views.common

import fi.espoo.vekkuli.config.MessageUtil
import fi.espoo.vekkuli.views.Icons
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class CommonComponents {
    @Autowired
    lateinit var messageUtil: MessageUtil

    @Autowired
    lateinit var icons: Icons

    fun t(key: String): String = messageUtil.getMessage(key)

    fun languageSelection(): String {
        val languageCode = messageUtil.getLocaleLanguageCode().uppercase()
        // language=HTML
        return(
            """
            <div class="dropdown is-hoverable" id="language-selection" >
              <div class="dropdown-trigger">
                <a class="dropdown-title" aria-haspopup="true" aria-controls="dropdown-menu">
                    <span class="icon is-small">
                        ${icons.globe}
                      </span>
                      <span class='pl-xs'>$languageCode</span>
                    </a>
              </div>
              <div class="dropdown-menu" id="dropdown-menu" role="menu">
                <div class="dropdown-content">
                   <a class="dropdown-item" href="?lang=fi">${
                t(
                    "lang.finnish"
                )
            }</a>
                  <a class="dropdown-item" href="?lang=sv" >${
                t(
                    "lang.swedish"
                )
            }</a>
                  <a class="dropdown-item" href="?lang=en">${
                t(
                    "lang.english"
                )
            }</a>
                </div>
              </div>
            </div>
            """.trimIndent()
        )
    }

    // language=HTML
    fun goBackButton(backUrl: String) =
        """
                        <button class="icon-text">
                            <span class="icon">
                                <div>${icons.chevronLeft}</div>
                            </span>
                            <a href=$backUrl>
                                <span>${t("boatSpaces.goBack")}</span>
                            </a>
                        </button>
                       """

    // language=HTML
    fun getCitizenFields(
        firstNameField: String,
        lastNameField: String,
        ssnField: String,
        addressField: String,
        postalCodeField: String,
        cityField: String,
        municipalityField: String,
        phoneNumberField: String,
        emailField: String
    ): String {
        // language=HTML
        return """
            <div class="columns">
                <div class="field column is-one-quarter">
                   $firstNameField
                </div>
                <div class="field column is-one-quarter">
                    $lastNameField
                </div>
            
                <div class="field column is-one-quarter">
                    $addressField
                </div>
                 <div class="field column is-one-eight">
                    $postalCodeField
                </div>
                <div class="field column is-one-eight">
                    $cityField
                </div>
            </div>
            <div class="columns">
             
                <div class="field column is-one-quarter">
                    $municipalityField
                </div>
                <div class="field column is-one-quarter">
                    $phoneNumberField
                </div>
                 <div class="field column is-one-quarter">
                   $emailField
                </div>
                  <div class="field column is-one-quarter">
                    $ssnField
                </div>
            </div>
            """.trimIndent()
    }
}
