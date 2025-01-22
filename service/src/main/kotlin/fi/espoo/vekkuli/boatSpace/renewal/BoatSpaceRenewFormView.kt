package fi.espoo.vekkuli.boatSpace.renewal

import fi.espoo.vekkuli.boatSpace.reservationForm.BoatFormInput
import fi.espoo.vekkuli.boatSpace.reservationForm.BoatFormParams
import fi.espoo.vekkuli.boatSpace.reservationForm.components.*
import fi.espoo.vekkuli.controllers.UserType
import fi.espoo.vekkuli.domain.*
import fi.espoo.vekkuli.views.BaseView
import org.springframework.stereotype.Service

data class BoatSpaceRenewViewParams(
    val reservation: RenewalReservationForApplicationForm,
    val boats: List<Boat>,
    val citizen: CitizenWithDetails? = null,
    val input: ModifyReservationInput,
    val userType: UserType,
)

@Service
class BoatSpaceRenewFormView(
    private val boatForm: BoatForm,
    private val boatSpaceForm: BoatSpaceForm,
    private val citizenContainerForCitizen: CitizenContainerForCitizen,
    private val reservationInformation: ReservationInformation,
    private val storageTypeContainer: StorageTypeContainer
) : BaseView() {
    fun boatSpaceRenewalFormForWinterStorage(boatSpaceRenewParams: BoatSpaceRenewViewParams): String {
        val (reservation, boats, citizen, input, userType) = boatSpaceRenewParams

        // language=HTML
        val renerFormContent =
            """
            <div class='form-section'>
                ${citizenContainerForCitizen.render(
                input.email,
                input.phone,
                citizen
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

             <div class='form-section' x-data="{ storageType: '${StorageType.Trailer.name}' }">
                <div class='form-section mb-none'>
                    ${storageTypeContainer.render(
                input.trailerRegistrationNumber,
                input.trailerWidth,
                input.trailerLength,
                storageType = input.storageType
            )}
                </div>
                 <div class='form-section'>
                    ${reservationInformation.reservationInformationWithStorageType(reservation)}
                </div>
                
            </div>
            """.trimIndent()

        return boatSpaceRenewFormBase(reservation, userType, renerFormContent)
    }

    fun boatSpaceRenewFormForSlip(boatSpaceRenewParams: BoatSpaceRenewViewParams): String {
        val (reservation, boats, citizen, input, userType) = boatSpaceRenewParams
        // language=HTML
        val renerFormContent =
            """
            <div class='form-section'>
                ${citizenContainerForCitizen.render(
                input.email,
                input.phone,
                citizen
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
            
             <div class='form-section'>
                ${reservationInformation.render(reservation)}
            </div>
            """.trimIndent()

        return boatSpaceRenewFormBase(reservation, userType, renerFormContent)
    }

    private fun boatSpaceRenewFormBase(
        reservation: RenewalReservationForApplicationForm,
        userType: UserType,
        renerFormContent: String,
    ) = boatSpaceForm.render(
        reservation,
        userType,
        titleText = t("boatApplication.title.reservation.renew"),
        formContent = renerFormContent,
        urls =
            ReservationUrls(
                submitUrl = "/${userType.path}/venepaikka/jatka/${reservation.renewdFromReservationId}",
                deleteUrl = "/${userType.path}/venepaikka/jatka/${reservation.id}",
                urlToReturnTo = "/kuntalainen/omat-tiedot"
            )
    )

    fun invoiceErrorPage() =
        """
        <section class="section">
            <div class="container">
                <h2 class="title pb-l">${t("boatSpaceRenewal.title.errorPage")}</h2>
                <p>${t("boatSpaceRenewal.errorPage")}</p>
            </div>
        </section>
        """.trimIndent()
}
