package fi.espoo.vekkuli.views.employee

import fi.espoo.vekkuli.FormComponents
import fi.espoo.vekkuli.utils.formatAsFullDate
import fi.espoo.vekkuli.views.BaseView
import org.springframework.stereotype.Service
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
    val description: String,
    val contactPerson: String,
    val orgId: String,
    val function: String
)

@Service
class InvoicePreview(
    val formComponents: FormComponents
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
        val invoicePeriod =
            formComponents.field(
                "invoice.invoicePeriod",
                "invoicePeriod",
                "${model.billingPeriodStart} - ${model.billingPeriodEnd}",
            )
        val priceWithTax =
            formComponents.decimalInput(
                "invoice.priceWithTax",
                "priceWithTax",
                model.priceWithTax,
                compact = true,
                step = 0.01,
            )
        val description =
            formComponents.textInput(
                "invoice.description",
                "description",
                model.description,
            )

        val contactPersonInput =
            formComponents.textInput(
                "invoice.contactPerson",
                "contactPerson",
                model.contactPerson,
                compact = true
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

        // language=HTML
        return """
            <section class="section">
            
            <div class="container">
                <h2 class="title pb-l" id="invoice-preview-header">Laskuluonnos</h2>
                
                <h3 class="subtitle">Varaajan tiedot</h3>
                <form
                    hx-post="$submitUrl"
                    hx-target="body"
                >
                ${invoiceLine("Varaaja", model.reserverName)}
                ${if (!isOrganization)invoiceLine("Varaajan henkilötunnus", model.reserverSsn) else ""}
                
                ${if (isOrganization)invoiceLine("Y-tunnus", model.orgId) else ""}
                ${invoiceLine("Varaajan osoite", model.reserverAddress)}
                
                <hr/>
                
                    $contactPerson
                    
                    <h3 class="subtitle">Laskun tiedot</h3>
                    <div class="columns">
                        <div class="column">
                            $functionSelect
                        </div>
                        
                        <div class="column">
                            $dueDate
                        </div>
                        
                        <div class="column">
                            $invoicePeriod
                        </div>
                        
                        <div class="column">
                            $priceWithTax
                        </div>
                    </div>
                    
                                
                    <div class="columns">
                        <div class="column is-half">
                            $description
                        </div>
                    </div>
                    
                    <hr/>
                    
                <div class="field block">
                    <div class="control">
                        <button id="cancel"
                            class="button is-secondary"
                            hx-delete="$deleteUrl"
                            hx-target="body"
                            hx-on-htmx-after-request="window.location = '$backUrl';"
                            type="button">
                            ${t("cancel")}
                        </button>
                        <button id="submit"
                            class="button is-primary">
                            Lähetä lasku
                        </button>
                    </div>
                </div> 
                </form>
            </div>
            </section>

            """.trimIndent()
    }

    fun invoiceLine(
        name: String,
        value: String
    ) = """
        <div class="block">
            <span class="invoice-line">$name:</span><span>$value</span>
        </div>
        """.trimIndent()

    fun invoiceErrorPage() =
        """
        <section class="section">
            <div class="container">
                <h2 class="title pb-l">Laskun luonti epäonnistui</h2>
                <p>Laskun luonti epäonnistui. Yritä myöhemmin uudelleen.</p>
            </div>
        </section>
        """.trimIndent()
}
