package fi.espoo.vekkuli.boatSpace.invoice

import fi.espoo.vekkuli.config.audit
import fi.espoo.vekkuli.config.ensureEmployeeId
import fi.espoo.vekkuli.config.getAuthenticatedUser
import fi.espoo.vekkuli.domain.ReservationWithDependencies
import fi.espoo.vekkuli.domain.ReserverType
import fi.espoo.vekkuli.repository.BoatSpaceReservationRepository
import fi.espoo.vekkuli.service.BoatReservationService
import fi.espoo.vekkuli.utils.decimalToInt
import fi.espoo.vekkuli.views.BaseView
import fi.espoo.vekkuli.views.employee.EmployeeLayout
import fi.espoo.vekkuli.views.employee.InvoicePreview
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import java.math.BigDecimal
import java.util.*

@Controller
class InvoiceController(
    private val employeeLayout: EmployeeLayout,
    private val invoicePreview: InvoicePreview,
    private val reservationService: BoatReservationService,
    private val boatReservationService: BoatReservationService,
    private val invoiceService: BoatSpaceInvoiceService,
    private val boatSpaceReservationRepo: BoatSpaceReservationRepository
) : BaseView() {
    private val logger = KotlinLogging.logger {}

    @RequestMapping("/virkailija/venepaikka/varaus/{reservationId}/lasku")
    @ResponseBody
    fun invoiceView(
        @PathVariable reservationId: Int,
        request: HttpServletRequest,
    ): ResponseEntity<String> {
        request.getAuthenticatedUser()?.let {
            logger.audit(it, "INVOICE_VIEW", mapOf("targetId" to reservationId.toString()))
        }
        val reservation = reservationService.getReservationWithReserver(reservationId)
        if (reservation?.reserverId == null) {
            throw IllegalArgumentException("Reservation not found")
        }

        val invoiceData = invoiceService.createInvoiceData(reservationId, reservation.reserverId)

        if (invoiceData == null) {
            throw IllegalArgumentException("Failed to create invoice data")
        }

        val model = invoicePreview.buildInvoiceModel(reservation, invoiceData)
        val content =
            invoicePreview.render(
                model,
                submitUrl = "/virkailija/venepaikka/varaus/${model.reservationId}/lasku",
                backUrl = "/virkailija/venepaikat/varaukset",
                deleteUrl = "/virkailija/venepaikka/varaus/$reservationId/lasku",
                isOrganization = reservation.reserverType == ReserverType.Organization
            )
        val page = employeeLayout.render(true, request.requestURI, content)
        return ResponseEntity.ok(page)
    }

    data class InvoiceInput(
        val priceWithTax: BigDecimal,
        val description: String,
        val function: String,
        val contactPerson: String? = "",
        val markAsPaid: Boolean = false
    )

    @PostMapping("/virkailija/venepaikka/varaus/{reservationId}/lasku")
    fun sendInvoice(
        @PathVariable reservationId: Int,
        @ModelAttribute("input") input: InvoiceInput,
        request: HttpServletRequest,
    ): ResponseEntity<String> {
        request.getAuthenticatedUser()?.let {
            logger.audit(it, "SEND_INVOICE", mapOf("targetId" to reservationId.toString()))
        }
        // send the invoice, update reservation status
        val reservation = reservationService.getReservationWithReserver(reservationId)

        if (reservation?.reserverId == null) {
            throw IllegalArgumentException("Reservation not found")
        }
        try {
            handleInvoiceSending(reservation, input)
        } catch (e: Exception) {
            val content = invoicePreview.invoiceErrorPage()
            return ResponseEntity.ok(employeeLayout.render(true, request.requestURI, content))
        }

        return ResponseEntity
            .status(HttpStatus.FOUND)
            .header("Location", getBackUrl(reservation.reserverType, reservation.reserverId))
            .body("")
    }

    fun getBackUrl(
        reserverType: ReserverType?,
        reserverId: UUID?
    ): String =
        if (reserverType == ReserverType.Citizen) {
            "/virkailija/kayttaja/$reserverId"
        } else {
            "/virkailija/yhteiso/$reserverId"
        }

    private fun handleInvoiceSending(
        reservation: ReservationWithDependencies,
        input: InvoiceInput
    ) {
        if (reservation.reserverId == null) {
            throw IllegalArgumentException("Reservation not found")
        }
        val priceWithVat = input.priceWithTax
        val priceWithVatInCents = decimalToInt(priceWithVat)
        val invoiceData =
            invoiceService.createInvoiceData(
                reservation.id,
                reservation.reserverId,
                priceWithVatInCents,
                input.description,
                input.function,
                input.contactPerson
            )
                ?: throw InternalError("Failed to create invoice batch")

        val invoice =
            invoiceService.createAndSendInvoice(
                invoiceData,
                reservation.reserverId,
                reservation.id,
                markAsPaidAndSkipSending = input.markAsPaid
            )

        if (invoice == null) {
            throw InternalError("Failed to create invoice")
        }
        if (!input.markAsPaid) {
            reservationService.setReservationStatusToInvoiced(reservation.id)
        }
        boatReservationService.sendReservationEmailAndInsertMemoIfSwitch(reservation.id)
    }

    @DeleteMapping("/virkailija/venepaikka/varaus/{reservationId}/lasku")
    fun cancelRenewal(
        @PathVariable reservationId: Int,
        request: HttpServletRequest,
    ): ResponseEntity<String> {
        request.getAuthenticatedUser()?.let {
            logger.audit(it, "INVOICE_CANCEL", mapOf("targetId" to reservationId.toString()))
        }
        val employeeId = request.ensureEmployeeId()
        boatSpaceReservationRepo.removeBoatSpaceReservation(reservationId)
        return ResponseEntity.noContent().build()
    }
}
