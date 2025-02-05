package fi.espoo.vekkuli.views.employee

import fi.espoo.vekkuli.FormComponents
import fi.espoo.vekkuli.utils.addTestId
import fi.espoo.vekkuli.utils.formatAsFullDate
import fi.espoo.vekkuli.views.BaseView
import fi.espoo.vekkuli.views.employee.components.MarkAsPaidConfirmModal
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.time.LocalDate

data class SendInvoiceModel(
    val reservationId: Int,
    val reserverName: String,
    val reserverSsn: String,
    val reserverAddress: String,
    val product: String,
    val billingPeriodStart: String,
    val billingPeriodEnd: String,
    val boatingSeasonStart: LocalDate,
    val boatingSeasonEnd: LocalDate,
    val invoiceNumber: String,
    val dueDate: LocalDate,
    val costCenter: String,
    val invoiceType: String,
    val priceWithTax: BigDecimal,
    val discountedPriceWithTax: BigDecimal,
    val description: String,
    val contactPerson: String,
    val orgId: String,
    val function: String,
    val discountPercentage: Int
) {
    val hasDiscount: Boolean
        get() = discountPercentage > 0
}

@Component
class InvoicePreview(
    val formComponents: FormComponents,
    private val markAsPaidConfirmModal: MarkAsPaidConfirmModal
) : BaseView() {
    fun render(
        model: SendInvoiceModel,
        submitUrl: String,
        backUrl: String,
        deleteUrl: String,
        isOrganization: Boolean
    ): String {
        val functionSelect =
            formComponents.select(
                "invoice.function",
                "function",
                model.function,
                listOf(
                    Pair("T1270", "Venepaikka T1270",),
                    Pair("T1271", "Talvisäilytys T1271",),
                    Pair("T1276", "Varastopaikka T1276",),
                )
            )
        val dueDate =
            formComponents.field(
                "invoice.dueDate",
                "dueDate",
                formatAsFullDate(model.dueDate)
            )
        val priceWithTax =
            formComponents.decimalInput(
                "invoice.priceWithTax",
                "priceWithTax",
                model.discountedPriceWithTax,
                compact = true,
                step = 0.01,
            )
        val description =
            formComponents.textInput(
                "invoice.description",
                "description",
                model.description,
                attributes = """maxlength="70"""",
            )

        val contactPersonInput =
            formComponents.textInput(
                "invoice.contactPerson",
                "contactPerson",
                model.contactPerson,
                compact = true,
                attributes = """maxlength="35"""",
            )

        val contactPerson =
            if (isOrganization) {
                """
                $contactPersonInput
                <hr>
                """.trimIndent()
            } else {
                ""
            }

        val discountInfo =
            if (model.hasDiscount) {
                """
                <div class="discount-info" ${addTestId("invoice-discount-info")}>
                <span>${t("invoice.discountInfo", listOf(model.discountPercentage.toString(), model.priceWithTax.toString()))}</span>
                </div>    
                """.trimIndent()
            } else {
                ""
            }

        // language=HTML
        return """
            <section class="section" x-data="{ confirmModalOpen: false, markAsPaidInputValue: false }">
                <div class="container">
                    <h2 class="title pb-l" id="invoice-preview-header">${t("invoice.title.previewHeader")}</h2>

                    <h3 class="subtitle">${t("invoice.subtitle.bookerDetails")}</h3>
                    <form
                        hx-post="$submitUrl"
                        hx-target="body"
                        hx-push-url="true"
                    >
                    ${invoiceLine(t("invoice.label.booker"), model.reserverName,"reserverName")}
                    ${if (!isOrganization) invoiceLine(t("invoice.label.bookerSsn"), model.reserverSsn,"reserverSsn") else ""}
                    
                    ${if (isOrganization) invoiceLine(t("invoice.label.companyId"), model.orgId,"orgId") else ""}
                    ${invoiceLine(t("invoice.label.bookerAddress"), model.reserverAddress,"reserverAddress")}
                    
                    <hr/>
                    
                    $contactPerson
                    
                    <h3 class="subtitle">${t("invoice.subtitle.invoiceDetails")}</h3>
                    <div class="columns">
                        <div class="column">
                            $functionSelect
                        </div>
                        
                        <div class="column">
                            $dueDate
                        </div>
                        
                        <div class="column">
                            $priceWithTax
                            $discountInfo
                        </div>
                    </div>
                                
                    <div class="columns">
                        <div class="column is-half">
                            $description
                        </div>
                    </div>
                    
                    <input type="hidden" name="markAsPaid" id="markAsPaidInput" x-model="markAsPaidInputValue">

                    <hr/>
                    
                    <div class="field block">
                        <div class="control">
                            <button id="cancel"
                                class="button is-secondary"
                                hx-delete="$deleteUrl"
                                hx-target="body"
                                hx-params="none"
                                hx-on-htmx-after-request="window.location = '$backUrl';"
                                type="button">
                                ${t("invoice.button.cancel")}
                            </button>
                              <button id="mark-as-paid"
                                class="button is-primary"
                                type='button'
                                x-on:click="confirmModalOpen = true">
                                ${t("invoice.button.reserveWithoutInvoice")}
                            </button>
                            <button id="submit"
                                class="button is-primary" type='submit'>
                                ${t("invoice.button.sendInvoice")}
                            </button>
                          
                        </div>
                    </div> 
                    ${markAsPaidConfirmModal.render()}
                    </form>
                </div>
            </section>
            """.trimIndent()
    }

    // language=HTML
    fun invoiceLine(
        name: String,
        value: String,
        id: String
    ) = """
        <div class="block">
            <span class="invoice-line">$name:</span><span id="$id" >$value</span>
        </div>
        """.trimIndent()

    // language=HTML
    fun invoiceErrorPage() =
        """
        <section class="section">
            <div class="container">
                <h2 class="title pb-l">${t("invoice.title.errorPage")}</h2>
                <p>${t("invoice.text.errorPage")}</p>
            </div>
        </section>
        """.trimIndent()
}
