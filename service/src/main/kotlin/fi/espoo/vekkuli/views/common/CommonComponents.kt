package fi.espoo.vekkuli.views.common

import fi.espoo.vekkuli.config.LocaleUtil
import fi.espoo.vekkuli.config.MessageUtil
import fi.espoo.vekkuli.views.Icons
import org.springframework.stereotype.Component

@Component
class CommonComponents(
    private val messageUtil: MessageUtil,
    private val icons: Icons,
    private val localeUtil: LocaleUtil
) {
    fun t(key: String): String = messageUtil.getMessage(key)

    fun languageSelection(): String {
        val languageCode = localeUtil.getLocaleLanguageCode().uppercase()
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
    fun goBackButton(backUrl: String? = null) =
        """
        <button class="icon-text" onclick="${if (backUrl == null) "history.back()" else "location.href='$backUrl'"}">
            <span class="icon">
                <div>${icons.chevronLeft}</div>
            </span>
            <a>
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

    fun reservationInformationFields(
        harborField: String,
        placeField: String,
        boatSpaceTypeField: String,
        spaceDimensionField: String,
        amenityField: String,
        reservationTimeField: String,
        priceField: String,
    ) = // language=HTML

        """
        <div class='columns'>
            <div class='column is-one-quarter'>
                $harborField
            </div>
            <div class='column is-one-quarter'>
                $placeField
            </div>
            <div class='column is-one-quarter'>
                $boatSpaceTypeField
            </div>
            <div class='column is-one-quarter'>
              $spaceDimensionField
            </div>
         </div>
         <div class='columns'>
            <div class='column is-one-quarter'>
                $amenityField
            </div>
            <div class='column is-one-quarter'>
                $reservationTimeField
            </div>
            <div class='column is-one-quarter' >
               $priceField
            </div>
        </div>
        """.trimIndent()

    fun boatInformationFields(
        nameInput: String,
        boatType: String,
        boatWidth: String,
        boatLength: String,
        boatDepth: String,
        boatWeight: String,
        registrationNumber: String,
        otherIdentification: String,
        extraInformation: String,
        ownership: String,
    ) = // language=HTML

        """
        <div class='columns'>
            <div class='column is-one-quarter'>
                $nameInput
            </div>
            <div class='column is-one-quarter'>
                $boatType
            </div>
            <div class='column is-one-quarter'>
                $boatWidth
            </div>
            <div class='column is-one-quarter'>
              $boatLength
            </div>
         </div>
         <div class='columns'>
            <div class='column '>
                $boatDepth
            </div>
            <div class='column'>
                $boatWeight
            </div>
            <div class='column is-half' >
               $registrationNumber
            </div>
        </div>
        <div class='columns'>
            <div class='column is-one-quarter'>
                $otherIdentification
            </div>
            <div class='column is-one-quarter'>
               $extraInformation
            </div>
        </div>
         <div id="boat-size-warning" >
                    </div>
                   <div id="boat-weight-warning" ></div>
                   <div id="boat-type-warning" ></div>
        <div class='columns'>
            $ownership
        </div>
        """.trimIndent()

    fun citizenFields(
        firstNameField: String,
        lastNameField: String,
        birthdayField: String,
        municipalityField: String,
        emailInput: String,
        phoneInput: String,
        address: String,
        postalCodeField: String? = null,
        cityField: String? = null,
    ): String { // language=HTML
        val addressField =
            """
            ${
                if (postalCodeField != null || cityField != null) {
                    """<div class='column is-one-quarter' >
                       $address
                    </div>
                    <div class='column is-one-eight'>
                        $postalCodeField
                    </div>
                    <div class='column is-one-eight'>
                       $cityField
                    </div>
                    """
                } else {
                    """<div class='column is-half' >
                       $address
                    </div>
                    """
                }
            }
            """.trimIndent()

        // language=HTML
        return (
            """<div class='columns'>
                    <div class='column is-one-quarter'>
                        $firstNameField
                      </div>
                      <div class='column is-one-quarter'>
                        $lastNameField
                      </div>
                      <div class='column is-one-quarter'>
                        $birthdayField
                      </div>
                      <div class='column is-one-quarter'>
                      $municipalityField
                        
                      </div>
                 </div>
                 <div class='columns'>
                    <div class='column is-one-quarter'>
                    $phoneInput
                        
                    </div>
                    <div class='column is-one-quarter'>
                     $emailInput
                        
                    </div>
                    $addressField
                    
                </div>"""
        )
    }
}
