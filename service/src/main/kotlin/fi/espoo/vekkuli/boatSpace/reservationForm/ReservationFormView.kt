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
    private fun formBase(
        citizenInformation: String,
        reserverInformation: String,
        boatInformation: String,
        boatSpaceTypeInformation: String? = null,
        reservationInformation: String? = null,
    ): String {
        val reservationInformationSection =
            if (reservationInformation != null) {
                """
                <div class='form-section'>
                    $reservationInformation
                </div>
                """.trimIndent()
            } else {
                ""
            }
        // language=HTML
        return """
            <div class='form-section'>
                $citizenInformation
            </div>
              <div class='form-section' id='reserver-boat-information'>
                 <div class='form-section'>
                    $reserverInformation
                </div> 
                
                <div class='form-section'>
                    $boatInformation
                </div>
                ${boatSpaceTypeInformation ?: ""}
                
                $reservationInformationSection
                
              
            </div>
            
            """.trimIndent()
    }

    fun trailerForm(
        reservation: ReservationForApplicationForm,
        boats: List<Boat>,
        citizen: CitizenWithDetails?,
        organizations: List<Organization>,
        input: ReservationInput,
        userType: UserType,
        municipalities: List<Municipality>,
        isNewCustomer: Boolean = true,
    ): String {
        val citizenContainer =
            citizenContainer.render(
                userType,
                reservation.id,
                input,
                citizen,
                municipalities
            )
        val slipHolder =
            slipHolder.render(
                organizations,
                input.isOrganization ?: false,
                input.organizationId,
                userType,
                reservation.id,
                municipalities
            )
        val boatForm =
            boatForm.render(
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
            )
        val trailerContentContainer =
            """
             <div class='form-section pb-none' x-data="{ storageType: '${StorageType.Trailer.name}', reservationValidity: '${input.reservationValidity}' }">
                <div class='form-section'>
                   ${ storageTypeContainer.trailerContainer(
                input.trailerRegistrationNumber,
                input.trailerWidth,
                input.trailerLength,
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
        // language=HTML
        val trailerContent = formBase(citizenContainer, slipHolder, boatForm, trailerContentContainer)

        return boatSpaceForm.render(
            reservation,
            userType,
            titleText = t("boatApplication.title.reservation.trailer"),
            formContent = trailerContent,
            reserverPriceInfo = input.reserverPriceInfo,
        )
    }

    fun storageForm(
        reservation: ReservationForApplicationForm,
        boats: List<Boat>,
        citizen: CitizenWithDetails?,
        organizations: List<Organization>,
        input: ReservationInput,
        userType: UserType,
        municipalities: List<Municipality>,
        isNewCustomer: Boolean = true,
    ): String {
        val citizenContainer =
            citizenContainer.render(
                userType,
                reservation.id,
                input,
                citizen,
                municipalities
            )
        val slipHolder =
            slipHolder.render(
                organizations,
                input.isOrganization ?: false,
                input.organizationId,
                userType,
                reservation.id,
                municipalities
            )
        val boatForm =
            boatForm.render(
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
            )
        val storageTypeContainer =
            if (reservation.amenity == BoatSpaceAmenity.Trailer) {
                storageTypeContainer.trailerContainer(
                    input.trailerRegistrationNumber,
                    input.trailerWidth,
                    input.trailerLength,
                )
            } else {
                storageTypeContainer.buckStorageTypeRadioButtons(input.storageType)
            }
        // language=HTML
        val storageContent =
            """
            <div class='form-section pb-none' x-data="{ storageType: '${StorageType.Trailer.name}', reservationValidity: '${input.reservationValidity}' }">
                <div class='form-section'>
                    $storageTypeContainer
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

        val storageContentForm =
            formBase(
                citizenContainer,
                slipHolder,
                boatForm,
                storageContent
            )
        return boatSpaceForm.render(
            reservation,
            userType,
            titleText = t("boatApplication.title.reservation.storage"),
            formContent = storageContentForm,
            reserverPriceInfo = input.reserverPriceInfo,
        )
    }

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
        val citizenContainer =
            citizenContainer.render(userType, reservation.id, input, citizen, municipalities)
        val slipHolder =
            slipHolder.render(
                organizations,
                input.isOrganization ?: false,
                input.organizationId,
                userType,
                reservation.id,
                municipalities
            )
        val boatForm =
            boatForm.render(
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
            )
        // language=HTML
        val storageContent =
            """
            <div class='form-section pb-none' x-data="{ storageType: '${StorageType.Trailer.name}', reservationValidity: '${input.reservationValidity}' }">
                <div class='form-section'>
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

        val storageContentForm =
            formBase(
                citizenContainer,
                slipHolder,
                boatForm,
                storageContent
            )
        return boatSpaceForm.render(
            reservation,
            userType,
            titleText = t("boatApplication.title.reservation.winter"),
            formContent = storageContentForm,
            reserverPriceInfo = input.reserverPriceInfo,
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
        val citizenContainer =
            citizenContainer.render(userType, reservation.id, input, citizen, municipalities)
        val slipHolder =
            slipHolder.render(
                organizations,
                input.isOrganization ?: false,
                input.organizationId,
                userType,
                reservation.id,
                municipalities
            )
        val boatForm =
            boatForm.render(
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
            )
        // language=HTML
        val slipContent =
            """
            <div x-data="{ reservationValidity: '${input.reservationValidity}' }">

                ${if (userType == UserType.EMPLOYEE) {
                """<div class='form-section'>
                  ${reservationValidityContainer.render(input.reservationValidity)}
                </div>"""
            } else {
                ""
            }}
            
                <div class='form-section'>
                  ${reservationInformation.render(reservation)}
                </div>
                
            </div>
            """.trimIndent()

        val slipContentForm =
            formBase(
                citizenContainer,
                slipHolder,
                boatForm,
                slipContent
            )

        return boatSpaceForm.render(
            reservation,
            userType,
            titleText = t("boatApplication.title.reservation.slip"),
            formContent = slipContentForm,
            reserverPriceInfo = input.reserverPriceInfo,
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
