package fi.espoo.vekkuli.views.employee

import fi.espoo.vekkuli.views.BaseView
import org.springframework.stereotype.Service
import java.time.LocalDate

data class SendInvoiceModel(
    val reserverName: String,
    val reserverSsn: String,
    val reserverAddress: String,
    val product: String,
    val functionInformation: String, // toimintotieto
    val billingPeriodStart: LocalDate,
    val billingPeriodEnd: LocalDate,
    val boatingSeasonStart: LocalDate,
    val boatingSeasonEnd: LocalDate,
    val invoiceNumber: String,
    val dueDate: LocalDate,
    val costCenter: String,
    val invoiceType: String,
    val invoiceRows: List<InvoiceRow>,
)

data class InvoiceRow(
    val description: String,
    val customer: String,
    val priceWithoutVat: String,
    val vat: String,
    val priceWithVat: String,
    val organization: String,
    val paymentDate: LocalDate,
)

@Service
class SendInvoice : BaseView() {
    fun render(model: SendInvoiceModel): String {
        println("")
        // language=HTML
        return """
            <section class="section">
            
            <div class="container">
                <h2 class="title pb-l">Laskuluonnos</h2>
                
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
                ${invoiceLine("Kustannuspaikka",model.costCenter)}
                ${invoiceLine("Laskulaji",model.invoiceType)}
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
            </div>
            </section>

            """.trimIndent()
    }

    fun invoiceLine(
        name: String,
        value: String
    ) = """
        <div>
            <span style="display:inline-block; width: 250px">$name:</span><span>$value</span>
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
}
