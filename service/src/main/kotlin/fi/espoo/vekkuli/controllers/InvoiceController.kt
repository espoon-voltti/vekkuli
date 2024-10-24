package fi.espoo.vekkuli.controllers

import fi.espoo.vekkuli.service.BoatReservationService
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
    private val reservationService: BoatReservationService,
) {
    @RequestMapping("/virkailija/venepaikka/varaus/{reservationId}/lasku")
    @ResponseBody
    fun invoiceView(
        @PathVariable reservationId: Int,
        request: HttpServletRequest,
    ): ResponseEntity<String> {
        val reservation = reservationService.getReservationWithReserver(reservationId)
        if (reservation == null) {
            throw IllegalArgumentException("Reservation not found")
        }

        val model =
            SendInvoiceModel(
                reserverName = reservation.name ?: "",
                reserverSsn = "",
                reserverAddress = "Testikatu 1",
                product = reservation.locationName,
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
                            description = "Venepaikka, ${reservation.locationName} ${reservation.section} ${reservation.placeNumber}, 2025",
                            customer = reservation.name ?: "",
                            priceWithoutVat = reservation.priceWithoutAlvInEuro.toString(),
                            vat = reservation.alvPriceInEuro.toString(),
                            priceWithVat = reservation.priceInEuro.toString(),
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
