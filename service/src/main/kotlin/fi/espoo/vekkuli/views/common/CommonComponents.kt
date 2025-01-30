package fi.espoo.vekkuli.views.common

import fi.espoo.vekkuli.FormComponents
import fi.espoo.vekkuli.config.LocaleUtil
import fi.espoo.vekkuli.config.MessageUtil
import fi.espoo.vekkuli.domain.CitizenWithDetails
import fi.espoo.vekkuli.domain.Municipality
import fi.espoo.vekkuli.views.Icons
import org.springframework.stereotype.Component

data class ReservationInformationParams(
    val harborField: String,
    val placeField: String,
    val boatSpaceTypeField: String,
    val spaceDimensionField: String,
    val amenityField: String,
    val reservationValidityField: String,
    val priceField: String,
)

@Component
class CommonComponents(
    private val messageUtil: MessageUtil,
    private val icons: Icons,
    private val localeUtil: LocaleUtil,
    private val formComponents: FormComponents,
) {
    fun t(key: String): String = messageUtil.getMessage(key)

    // language=HTML
    fun goBackButton(backUrl: String) =
        """
                        <button data-testid="go-back" class="icon-text">
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

    fun reservationInformationFields(params: ReservationInformationParams): String {
        val (harborField, placeField, boatSpaceTypeField, spaceDimensionField, amenityField, reservationValidityField, priceField) = params

        // language=HTML
        return (
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
                    $reservationValidityField
                </div>
                <div class='column is-one-quarter' >
                   $priceField
                </div>
            </div>
            """.trimIndent()
        )
    }

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
        <div class='columns pt-l'>
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

    // language=HTML
    fun citizenDetails(
        citizen: CitizenWithDetails,
        municipalities: List<Municipality>,
    ): String {
        val firstNameField =
            formComponents.field(
                "boatSpaceReservation.title.firstName",
                "firstName",
                citizen.firstName,
            )
        val lastNameField = formComponents.field("boatSpaceReservation.title.lastName", "lastName", citizen.lastName)
        val birthdayField = formComponents.field("boatSpaceReservation.title.birthday", "birthday", citizen.birthday)
        val addressInput =
            formComponents.textInput("boatSpaceReservation.title.address", "address", citizen.streetAddress)
        val postalCodeField =
            formComponents.textInput("boatSpaceReservation.title.postalCode", "postalCode", citizen.postalCode)
        val cityField =
            formComponents.textInput("boatSpaceReservation.title.city", "postalOffice", citizen.municipalityName)
        val emailInput = formComponents.textInput("boatApplication.email", "email", citizen.email, true)
        val phoneInput = formComponents.textInput("boatApplication.phone", "phone", citizen.phone, true)
        val municipalityInput =
            formComponents.select(
                "boatSpaceReservation.title.municipality",
                "municipalityCode",
                citizen.municipalityCode.toString(),
                municipalities.map { Pair(it.code.toString(), it.name) },
                required = true
            )
        return (
            """
             ${
                citizenFields(
                    firstNameField,
                    lastNameField,
                    birthdayField,
                    municipalityInput,
                    phoneInput,
                    emailInput,
                    addressInput,
                    postalCodeField,
                    cityField,
                )
            }
            """.trimIndent()
        )
    }
}
