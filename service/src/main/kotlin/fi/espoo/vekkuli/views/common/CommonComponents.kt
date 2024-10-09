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
