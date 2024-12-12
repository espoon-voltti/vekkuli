package fi.espoo.vekkuli.boatSpace.renewal

import fi.espoo.vekkuli.boatSpace.reservationForm.BoatFormInput
import fi.espoo.vekkuli.boatSpace.reservationForm.BoatFormParams
import fi.espoo.vekkuli.boatSpace.reservationForm.components.*
import fi.espoo.vekkuli.controllers.UserType
import fi.espoo.vekkuli.domain.*
import fi.espoo.vekkuli.views.BaseView
import fi.espoo.vekkuli.views.employee.InvoiceRow
import fi.espoo.vekkuli.views.employee.SendInvoiceModel
import org.springframework.stereotype.Service
import java.util.*

data class BoatSpaceRenewViewParams(
    val reservation: RenewalReservationForApplicationForm,
    val boats: List<Boat>,
    val citizen: CitizenWithDetails? = null,
    val input: RenewalReservationInput,
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
        ReservationUrls(
            submitUrl = "/${userType.path}/venepaikka/jatka/${reservation.renewdFromReservationId}",
            deleteUrl = "/${userType.path}/venepaikka/jatka/${reservation.id}",
            urlToReturnTo = "/kuntalainen/omat-tiedot"
        )
    )

    fun getCitizenPageUrl(
        userType: UserType,
        citizenId: UUID?
    ) = "/${userType.path}/" +
        if (userType == UserType.CITIZEN) "omat-tiedot" else "kayttaja/$citizenId"

    fun renewInvoicePreview(
        model: SendInvoiceModel,
        reserverId: UUID,
        originalReservationId: Int
    ): String {
        // language=HTML
        fun invoiceLine(
            name: String,
            value: String
        ) = """
            <div class="block">
                <span class="invoice-line">$name:</span><span>$value</span>
            </div>
            """.trimIndent()

        fun invoiceRows(rows: List<InvoiceRow>): String =
            rows.joinToString { row ->
                """
                <tr>
                    <td>${row.description}</td>
                    <td>${row.customer}</td>
                    <td>${row.priceWithoutVat}</td>
                    <td>${row.vat}</td>
                    <td>${row.priceWithVat}</td>
                    <td>${row.organization}</td>
                    <td>${row.paymentDate}</td>
                </tr>
                """.trimIndent()
            }

        // language=HTML
        return """
            <section class="section">
            
            <div class="container">
            
                <h2 class="title pb-l" id="invoice-preview-header">Laskuluonnos</h2>
                
                <h3 class="subtitle">Varaajan tiedot</h3>
                ${invoiceLine("Varaaja", model.reserverName)}
                ${invoiceLine("Varaajan henkilötunnus", model.reserverSsn)}
                ${invoiceLine("Varaajan osoite", model.reserverAddress)}
                
                <hr/>
                
                <h3 class="subtitle">Laskun tiedot</h3>
                ${invoiceLine("Tuote", model.product)}
                ${invoiceLine("Toimintotieto", model.functionInformation)}
                ${invoiceLine("Laskutuskausi", "${model.billingPeriodStart} - ${model.billingPeriodEnd}")}
                ${invoiceLine("Veneilykausi", "${model.boatingSeasonStart} - ${model.boatingSeasonEnd}")}
                ${invoiceLine("Laskun numero", model.invoiceNumber)}
                ${invoiceLine("Laskun eräpäivä", model.dueDate.toString())}
                ${invoiceLine("Kustannuspaikka", model.costCenter)}
                ${invoiceLine("Laskulaji", model.invoiceType)}
                ${invoiceLine("Hintaryhmä", "100%")}
                
                <hr/>
                
                <h3 class="subtitle">Laskurivi</h3>
                
                <table class="table">
                    <thead>
                        <td>Selite</td>
                        <td>Asiakas</td>
                        <td>Hinta ilman alv</td>
                        <td>Alv 24 %</td>
                        <td>Verollinen hinta </td>
                        <td>Organisaatio</td>
                        <td>Maksupäivä</td>
                    </thead>
                    <tbody>
                        ${invoiceRows(model.invoiceRows)}
                    </tbody>
                </table>
                
                <div class="field block">
                    <div class="control">
                        <button id="cancel"
                            class="button is-secondary"
                            hx-delete="/virkailija/venepaikka/jatka/${model.reservationId}/lasku"
                            hx-target="body"
                            hx-on-htmx-after-request="window.location = '${getCitizenPageUrl(UserType.EMPLOYEE, reserverId)}';"
                            type="button">
                            ${t("cancel")}
                        </button>
                        <button id="submit"
                            class="button is-primary"
                            type="submit"
                            hx-post="/virkailija/venepaikka/jatka/$originalReservationId/lasku"
                            hx-target="body">
                            Lähetä lasku
                        </button>
                    </div>
                </div> 
            </div>
            </section>

            """.trimIndent()
    }

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
