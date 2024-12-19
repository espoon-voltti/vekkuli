package fi.espoo.vekkuli.boatSpace.reservationForm

import fi.espoo.vekkuli.FormComponents
import fi.espoo.vekkuli.boatSpace.reservationForm.components.*
import fi.espoo.vekkuli.controllers.UserType
import fi.espoo.vekkuli.domain.*
import fi.espoo.vekkuli.views.BaseView
import fi.espoo.vekkuli.views.citizen.StepIndicator
import org.springframework.stereotype.Service
import java.math.BigDecimal

data class BoatFormInput(
    val id: Int,
    val boatName: String,
    val boatType: BoatType,
    val width: BigDecimal?,
    val length: BigDecimal?,
    val depth: BigDecimal?,
    val weight: Int?,
    val boatRegistrationNumber: String,
    val otherIdentification: String,
    val extraInformation: String,
    val ownership: OwnershipStatus,
    val noRegistrationNumber: Boolean,
) {
    companion object {
        fun empty(): BoatFormInput =
            BoatFormInput(
                id = 0,
                boatName = "",
                boatType = BoatType.OutboardMotor,
                width = null,
                length = null,
                depth = null,
                weight = null,
                boatRegistrationNumber = "",
                otherIdentification = "",
                extraInformation = "",
                ownership = OwnershipStatus.Owner,
                noRegistrationNumber = false,
            )
    }
}

data class BoatFormParams(
    val userType: UserType,
    val citizen: CitizenWithDetails?,
    val boats: List<Boat>,
    val reservationId: Int,
    val input: BoatFormInput,
)

@Service
class ReservationFormView(
    private val stepIndicator: StepIndicator,
    private val citizenContainer: CitizenContainer,
    private val slipHolder: SlipHolder,
    private val boatForm: BoatForm,
    private val boatSpaceForm: BoatSpaceForm,
    private val reservationInformation: ReservationInformation,
    private val formComponents: FormComponents,
    private val storageTypeContainer: StorageTypeContainer,
    private val reservationValidityContainer: ReservationValidityContainer,
) : BaseView() {
    fun winterStorageForm(
        reservation: ReservationForApplicationForm,
        boats: List<Boat>,
        citizen: CitizenWithDetails?,
        organizations: List<Organization>,
        input: ReservationInput,
        userType: UserType,
        municipalities: List<Municipality>,
        isNewCustomer: Boolean = true,
    ): String {
        // language=HTML
        val storageContent =
            """
             <div class='form-section'>
                ${citizenContainer.render(userType, reservation.id, input, citizen, municipalities)}  
            </div>
            
             <div class='form-section'>
                ${slipHolder.render(
                organizations,
                input.isOrganization ?: false,
                input.organizationId,
                userType,
                reservation.id,
                municipalities
            )}
            </div> 
            
            <div class='form-section'>
                    ${boatForm.render(
                BoatFormParams(
                    userType,
                    citizen,
                    boats,
                    reservation.id,
                    BoatFormInput(
                        id = input.boatId ?: 0,
                        boatName = input.boatName ?: "",
                        boatType = input.boatType ?: BoatType.OutboardMotor,
                        width = null,
                        length = null,
                        depth = input.depth,
                        weight = input.weight,
                        boatRegistrationNumber = input.boatRegistrationNumber ?: "",
                        otherIdentification = input.otherIdentification ?: "",
                        extraInformation = input.extraInformation ?: "",
                        ownership = input.ownership ?: OwnershipStatus.Owner,
                        noRegistrationNumber = input.noRegistrationNumber ?: false,
                    )
                )
            )}
            </div>
            <div class='form-section pb-none' x-data="{ storageType: '${StorageType.Trailer.name}', reservationValidity: '${input.reservationValidity}' }">
                <div class='form-section mb-none'>
                    ${storageTypeContainer.render(
                input.trailerRegistrationNumber,
                input.trailerWidth,
                input.trailerLength,
                input.storageType
            )}
                </div>
                
                ${if (userType == UserType.EMPLOYEE) {
                """<div class='form-section'>
                  ${reservationValidityContainer.render(input.reservationValidity)}
                 </div>"""
            } else {
                ""
            }}
            
                 <div class='form-section'>
                     ${reservationInformation.reservationInformationWithStorageType(reservation)}
                </div>
            </div>
            """.trimIndent()
        return boatSpaceForm.render(
            reservation,
            userType,
            titleText = t("boatApplication.title.reservation.winter"),
            formContent = storageContent,
        )
    }

    fun slipForm(
        reservation: ReservationForApplicationForm,
        boats: List<Boat>,
        citizen: CitizenWithDetails?,
        organizations: List<Organization>,
        input: ReservationInput,
        userType: UserType,
        municipalities: List<Municipality>,
        isNewCustomer: Boolean = true,
    ): String {
        // language=HTML
        val slipContent =
            """
             <div class='form-section'>
                ${citizenContainer.render(userType, reservation.id, input, citizen, municipalities)}  
            </div>
            
             <div class='form-section'>
                ${slipHolder.render(
                organizations,
                input.isOrganization ?: false,
                input.organizationId,
                userType,
                reservation.id,
                municipalities
            )}
            </div> 
            
            <div class='form-section'>
                ${boatForm.render(
                BoatFormParams(
                    userType,
                    citizen,
                    boats,
                    reservation.id,
                    BoatFormInput(
                        id = input.boatId ?: 0,
                        boatName = input.boatName ?: "",
                        boatType = input.boatType ?: BoatType.OutboardMotor,
                        width = input.width,
                        length = input.length,
                        depth = input.depth,
                        weight = input.weight,
                        boatRegistrationNumber = input.boatRegistrationNumber ?: "",
                        otherIdentification = input.otherIdentification ?: "",
                        extraInformation = input.extraInformation ?: "",
                        ownership = input.ownership ?: OwnershipStatus.Owner,
                        noRegistrationNumber = input.noRegistrationNumber ?: false,
                    )
                )
            )}
            </div>
            
            ${if (userType == UserType.EMPLOYEE) {
                """<div class='form-section' x-data="{ reservationValidity: '${input.reservationValidity}' }">
              ${reservationValidityContainer.render(input.reservationValidity)}
            </div>"""
            } else {
                ""
            }}

            <div class='form-section'>
              ${reservationInformation.render(reservation)}
            </div>
            """.trimIndent()

        return boatSpaceForm.render(
            reservation,
            userType,
            titleText = t("boatApplication.title.reservation.slip"),
            formContent = slipContent,
        )
    }

    // language=HTML
    fun errorPage(
        errorText: String,
        step: Int
    ): String =
        """
        <section class="section" id="error-page-container">
         ${stepIndicator.render(step)}
            <div class="container">
                <div class='column is-half'>
                    <h3 >${t("boatApplication.title.errorPage")}</h3>
                    <p >$errorText</p>
                </div>
            </div>
        </section>
        """.trimIndent()
}
