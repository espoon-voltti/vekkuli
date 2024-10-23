package fi.espoo.vekkuli.controllers

import fi.espoo.vekkuli.views.employee.EmployeeLayout
import fi.espoo.vekkuli.views.employee.InvoiceRow
import fi.espoo.vekkuli.views.employee.SendInvoice
import fi.espoo.vekkuli.views.employee.SendInvoiceModel
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import java.time.LocalDate

@Controller
class InvoiceController(
    private val employeeLayout: EmployeeLayout,
    private val sendInvoiceView: SendInvoice,
) {
    @RequestMapping("/virkailija/venepaikka/varaus/{reservationId}/lasku")
    @ResponseBody
    fun invoiceView(
        @PathVariable reservationId: Int,
        request: HttpServletRequest,
    ): ResponseEntity<String> {
        val model =
            SendInvoiceModel(
                reserverName = "Testi",
                reserverSsn = "123456-7890",
                reserverAddress = "Testikatu 1",
                product = "Testituote",
                functionInformation = "Testitieto",
                billingPeriodStart = LocalDate.of(2021, 1, 1),
                billingPeriodEnd = LocalDate.of(2021, 12, 31),
                boatingSeasonStart = LocalDate.of(2021, 5, 1),
                boatingSeasonEnd = LocalDate.of(2021, 9, 30),
                invoiceNumber = "123456",
                dueDate = LocalDate.of(2021, 12, 31),
                costCenter = "123456",
                invoiceType = "Testilasku",
                invoiceRows =
                    listOf(
                        InvoiceRow(
                            description = "Venepaikka, Haukilahti, 2025",
                            customer = "Veikko Veneilijä",
                            priceWithoutVat = "333,06",
                            vat = "79,93",
                            priceWithVat = "413",
                            organization = "Merellinen ulkoilu",
                            paymentDate = LocalDate.of(2021, 1, 1)
                        )
                    )
            )
        val content = sendInvoiceView.render(model)
        val page = employeeLayout.render(true, request.requestURI, content)
        return ResponseEntity.ok(page)
    }
}
