package fi.espoo.vekkuli.boatSpace.invoice

import fi.espoo.vekkuli.domain.ReservationWithDependencies
import fi.espoo.vekkuli.service.BoatReservationService
import fi.espoo.vekkuli.utils.TimeProvider
import fi.espoo.vekkuli.utils.centToEuro
import fi.espoo.vekkuli.views.employee.EmployeeLayout
import fi.espoo.vekkuli.views.employee.InvoicePreview
import fi.espoo.vekkuli.views.employee.SendInvoiceModel
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import java.math.BigDecimal
import java.time.LocalDate

@Controller
class InvoiceController(
    private val employeeLayout: EmployeeLayout,
    private val sendInvoiceView: InvoicePreview,
    private val reservationService: BoatReservationService,
    private val invoiceService: BoatSpaceInvoiceService,
    private val timeProvider: TimeProvider,
) {
    @RequestMapping("/virkailija/venepaikka/varaus/{reservationId}/lasku")
    @ResponseBody
    fun invoiceView(
        @PathVariable reservationId: Int,
        request: HttpServletRequest,
    ): ResponseEntity<String> {
        val reservation = reservationService.getReservationWithReserver(reservationId)
        if (reservation?.reserverId == null) {
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
                reserverSsn = invoiceData.ssn ?: "",
                reserverAddress = "${invoiceData.street} ${invoiceData.postalCode} ${invoiceData.post}",
                product = reservation.locationName,
                functionInformation = "",
                billingPeriodStart = "",
                billingPeriodEnd = "",
                boatingSeasonStart = LocalDate.of(2025, 5, 1),
                boatingSeasonEnd = LocalDate.of(2025, 9, 30),
                invoiceNumber = "",
                dueDate = LocalDate.of(2025, 12, 31),
                costCenter = "",
                invoiceType = "",
                priceWithTax = reservation.priceCents.centToEuro(),
                description = "Venepaikan vuokraus"
            )
        val content = sendInvoiceView.render(model)
        val page = employeeLayout.render(true, request.requestURI, content)
        return ResponseEntity.ok(page)
    }

    data class InvoiceInput(
        val priceWithTax: BigDecimal,
        val description: String
    )

    @PostMapping("/virkailija/venepaikka/varaus/{reservationId}/lasku")
    fun sendInvoice(
        @PathVariable reservationId: Int,
        @ModelAttribute("input") input: InvoiceInput,
        request: HttpServletRequest,
    ): ResponseEntity<String> {
        // send the invoice, update reservation status
        val reservation = reservationService.getReservationWithReserver(reservationId)

        if (reservation?.reserverId == null) {
            throw IllegalArgumentException("Reservation not found")
        }
        try {
            handleInvoiceSending(reservation, input.priceWithTax, input.description)
        } catch (e: Exception) {
            val content = sendInvoiceView.invoiceErrorPage()
            return ResponseEntity.ok(employeeLayout.render(true, request.requestURI, content))
        }

        return ResponseEntity
            .status(HttpStatus.FOUND)
            .header("Location", "/virkailija/venepaikat/varaukset")
            .body("")
    }

    private fun handleInvoiceSending(
        reservation: ReservationWithDependencies,
        priceWithVat: BigDecimal,
        description: String
    ) {
        if (reservation.reserverId == null) {
            throw IllegalArgumentException("Reservation not found")
        }
        val priceWithVatInCents = priceWithVat.multiply(BigDecimal(100)).toInt()
        val invoiceData =
            invoiceService.createInvoiceData(reservation.id, reservation.reserverId, priceWithVatInCents, description)
                ?: throw InternalError("Failed to create invoice batch")

        val invoice = invoiceService.createAndSendInvoice(invoiceData, reservation.reserverId, reservation.id)

        if (invoice == null) {
            throw InternalError("Failed to create invoice")
        }

        reservationService.setReservationStatusToInvoiced(reservation.id)
    }
}
