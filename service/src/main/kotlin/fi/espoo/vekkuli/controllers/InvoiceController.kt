package fi.espoo.vekkuli.controllers

import fi.espoo.vekkuli.service.BoatReservationService
import fi.espoo.vekkuli.service.BoatSpaceInvoiceService
import fi.espoo.vekkuli.views.employee.EmployeeLayout
import fi.espoo.vekkuli.views.employee.InvoicePreview
import fi.espoo.vekkuli.views.employee.InvoiceRow
import fi.espoo.vekkuli.views.employee.SendInvoiceModel
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import java.time.LocalDate

@Controller
class InvoiceController(
    private val employeeLayout: EmployeeLayout,
    private val sendInvoiceView: InvoicePreview,
    private val reservationService: BoatReservationService,
    private val invoiceService: BoatSpaceInvoiceService,
) {
    @RequestMapping("/virkailija/venepaikka/varaus/{reservationId}/lasku")
    @ResponseBody
    fun invoiceView(
        @PathVariable reservationId: Int,
        request: HttpServletRequest,
    ): ResponseEntity<String> {
        val reservation = reservationService.getReservationWithReserver(reservationId)
        if (reservation == null || reservation.reserverId == null) {
            throw IllegalArgumentException("Reservation not found")
        }
        val invoiceData = invoiceService.createInvoiceData(reservationId, reservation.reserverId)
        if (invoiceData == null) {
            throw IllegalArgumentException("Failed to create invoice data")
        }

        // TODO: get the actual data
        val model =
            SendInvoiceModel(
                reservationId = reservationId,
                reserverName = "${invoiceData.firstnames} ${invoiceData.lastname}",
                reserverSsn = invoiceData.ssn,
                reserverAddress = "${invoiceData.street} ${invoiceData.postalCode} ${invoiceData.post}",
                product = reservation.locationName,
                functionInformation = "?",
                billingPeriodStart = "",
                billingPeriodEnd = "",
                boatingSeasonStart = LocalDate.of(2025, 5, 1),
                boatingSeasonEnd = LocalDate.of(2025, 9, 30),
                invoiceNumber = "",
                dueDate = LocalDate.of(2025, 12, 31),
                costCenter = "?",
                invoiceType = "?",
                invoiceRows =
                    listOf(
                        InvoiceRow(
                            description = invoiceData.description,
                            customer = "${invoiceData.lastname} ${invoiceData.firstnames}",
                            priceWithoutVat = reservation.priceWithoutAlvInEuro.toString(),
                            vat = reservation.alvPriceInEuro.toString(),
                            priceWithVat = reservation.priceInEuro.toString(),
                            organization = "Merellinen ulkoilu",
                            paymentDate = LocalDate.of(2025, 1, 1)
                        )
                    )
            )
        val content = sendInvoiceView.render(model)
        val page = employeeLayout.render(true, request.requestURI, content)
        return ResponseEntity.ok(page)
    }

    @PostMapping("/virkailija/venepaikka/varaus/{reservationId}/lasku")
    fun sendInvoice(
        @PathVariable reservationId: Int,
    ): ResponseEntity<String> {
        // send the invoice, update reservation status
        val reservation = reservationService.getReservationWithReserver(reservationId)
        if (reservation?.reserverId == null) {
            throw IllegalArgumentException("Reservation not found")
        }
        val invoiceData =
            invoiceService.createInvoiceData(reservationId, reservation.reserverId)
                ?: throw InternalError("Failed to create invoice batch")

        invoiceService.createAndSendInvoice(invoiceData, reservation.reserverId, reservationId)
            ?: throw InternalError("Failed to send invoice")

        reservationService.setReservationStatusToInvoiced(reservationId)

        return ResponseEntity
            .status(HttpStatus.FOUND)
            .header("Location", "/virkailija/venepaikat/varaukset")
            .body("")
    }
}
