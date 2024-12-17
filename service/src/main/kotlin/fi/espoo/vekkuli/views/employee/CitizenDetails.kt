package fi.espoo.vekkuli.views.employee

import fi.espoo.vekkuli.FormComponents
import fi.espoo.vekkuli.controllers.CitizenUserController
import fi.espoo.vekkuli.controllers.UserType
import fi.espoo.vekkuli.domain.*
import fi.espoo.vekkuli.views.BaseView
import fi.espoo.vekkuli.views.Icons
import fi.espoo.vekkuli.views.citizen.details.reservation.ReservationList
import fi.espoo.vekkuli.views.common.CommonComponents
import fi.espoo.vekkuli.views.components.WarningBox
import fi.espoo.vekkuli.views.employee.components.ReserverDetailsReservationsContainer
import org.springframework.stereotype.Service
import java.util.*

enum class SubTab {
    Reservations,
    Payments,
    Messages,
    Memos,
}

@Service
class CitizenDetails(
    private val formComponents: FormComponents,
    private val reservationListBuilder: ReservationList,
    private val icons: Icons,
    private val commonComponents: CommonComponents,
    private val warningBox: WarningBox,
    private val reserverDetailsReservationsContainer: ReserverDetailsReservationsContainer,
) : BaseView() {
    fun citizenPage(
        @SanitizeInput citizen: CitizenWithDetails,
        @SanitizeInput boatSpaceReservations: List<BoatSpaceReservationDetails>,
        @SanitizeInput boats: List<CitizenUserController.BoatUpdateForm>,
        userType: UserType,
        @SanitizeInput errors: MutableMap<String, String>? = mutableMapOf(),
    ): String {
        // language=HTML
        fun customerInfo(): String {
            val firstNameValue =
                formComponents.field(
                    "boatSpaceReservation.title.firstName",
                    "firstNameField",
                    citizen.firstName,
                )
            val lastNameValue =
                formComponents.field(
                    "boatSpaceReservation.title.lastName",
                    "lastNameField",
                    citizen.lastName,
                )
            val nationalIdValue =
                formComponents.field(
                    "boatSpaceReservation.title.nationalId",
                    "nationalIdField",
                    citizen.nationalId,
                )
            val addressValue = formComponents.field("boatSpaceReservation.title.address", "addressField", citizen.streetAddress)
            val postalCodeValue =
                formComponents.field("boatSpaceReservation.title.postalCode", "postalCodeField", citizen.postalCode)
            val cityValue = formComponents.field("boatSpaceReservation.title.city", "cityField", citizen.postOffice)
            val municipalityValue =
                formComponents.field(
                    "boatSpaceReservation.title.municipality",
                    "municipalityCodeField",
                    citizen.municipalityName
                )
            val phoneNumberValue =
                formComponents.field("boatSpaceReservation.title.phoneNumber", "phoneNumberField", citizen.phone)
            val emailValue = formComponents.field("boatSpaceReservation.title.email", "emailField", citizen.email)
            val editUrl =
                if (userType == UserType.EMPLOYEE) {
                    "/virkailija/kayttaja/${citizen.id}/muokkaa"
                } else {
                    "/kuntalainen/kayttaja/muokkaa"
                }
            return (
                """
                <div class="container block" id="citizen-information">
                    <div class="columns">
                        <div class="column is-narrow">
                            <h3 class="header">${t("boatSpaceReservation.title.customerInformation")}</h3>
                        </div>
                        <div class="column">
                            <div>
                                <a class="is-link is-icon-link" 
                                    id="edit-customer"
                                    hx-get="$editUrl"
                                    hx-target="#citizen-information"
                                    hx-swap="innerHTML">
                                    <span class="icon">
                                        ${icons.edit}
                                    </span>
                                    <span>${t("boatSpaceReservation.button.editCustomerDetails")}</span>
                                </a>
                            </div>
                            <!-- Placeholder for additional actions, if needed -->
                        </div>
                    </div>
                    ${
                    commonComponents.getCitizenFields(
                        firstNameValue,
                        lastNameValue,
                        nationalIdValue,
                        addressValue,
                        postalCodeValue,
                        cityValue,
                        municipalityValue,
                        phoneNumberValue,
                        emailValue
                    )
                }
            </div> 
            """
            )
        }

        val backUrl =
            if (userType == UserType.EMPLOYEE) {
                "/virkailija/venepaikat/varaukset"
            } else {
                "/"
            }
        val result =
            // language=HTML
            """
            <section class="section" id="reserver-details">
                <div class="container block">
                    ${commonComponents.goBackButton(backUrl)} 
                    <h2>${citizen.firstName + " " + citizen.lastName}</h2>
                </div>
                ${customerInfo()}
                ${reserverDetailsReservationsContainer.render(citizen.id, boatSpaceReservations, boats, userType)}
            </section>
            """.trimIndent()

        return result
    }
}
