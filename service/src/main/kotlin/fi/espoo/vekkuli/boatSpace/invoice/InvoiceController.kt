package fi.espoo.vekkuli.boatSpace.invoice

import fi.espoo.vekkuli.config.audit
import fi.espoo.vekkuli.config.ensureEmployeeId
import fi.espoo.vekkuli.config.getAuthenticatedUser
import fi.espoo.vekkuli.domain.BoatSpaceType
import fi.espoo.vekkuli.domain.ReservationWithDependencies
import fi.espoo.vekkuli.domain.ReserverType
import fi.espoo.vekkuli.repository.BoatSpaceReservationRepository
import fi.espoo.vekkuli.service.BoatReservationService
import fi.espoo.vekkuli.utils.TimeProvider
import fi.espoo.vekkuli.utils.decimalToInt
import fi.espoo.vekkuli.utils.formatAsFullDate
import fi.espoo.vekkuli.utils.intToDecimal
import fi.espoo.vekkuli.views.BaseView
import fi.espoo.vekkuli.views.employee.EmployeeLayout
import fi.espoo.vekkuli.views.employee.InvoicePreview
import fi.espoo.vekkuli.views.employee.SendInvoiceModel
import jakarta.servlet.http.HttpServletRequest
import mu.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import java.math.BigDecimal
import java.time.LocalDate

@Controller
class InvoiceController(
    private val employeeLayout: EmployeeLayout,
    private val invoicePreview: InvoicePreview,
    private val reservationService: BoatReservationService,
    private val invoiceService: BoatSpaceInvoiceService,
    private val timeProvider: TimeProvider,
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
        val isOrganization = reservation.reserverType == ReserverType.Organization

        val model = buildInvoiceModel(reservation, invoiceData, reservationId, isOrganization)
        val content =
            invoicePreview.render(
                model,
                submitUrl = "/virkailija/venepaikka/varaus/${model.reservationId}/lasku",
                backUrl = "/virkailija/venepaikat/varaukset",
                deleteUrl = "/virkailija/venepaikka/varaus/$reservationId/lasku",
                isOrganization
            )
        val page = employeeLayout.render(true, request.requestURI, content)
        return ResponseEntity.ok(page)
    }

    private fun buildInvoiceModel(
        reservation: ReservationWithDependencies,
        invoiceData: InvoiceData,
        reservationId: Int,
        isOrganization: Boolean
    ): SendInvoiceModel {
        val reserverName =
            if (isOrganization) {
                invoiceData.orgName ?: ""
            } else {
                "${invoiceData.firstnames} ${invoiceData.lastname}"
            }

        val model =
            SendInvoiceModel(
                reservationId = reservationId,
                reserverName = reserverName,
                reserverSsn = invoiceData.ssn ?: "",
                reserverAddress = "${invoiceData.street}, ${invoiceData.postalCode}, ${invoiceData.post}",
                product = reservation.locationName,
                function = getDefaultFunction(reservation.type),
                billingPeriodStart = formatAsFullDate(reservation.startDate),
                billingPeriodEnd = formatAsFullDate(reservation.endDate),
                boatingSeasonStart = LocalDate.of(2025, 5, 1),
                boatingSeasonEnd = LocalDate.of(2025, 9, 30),
                invoiceNumber = "",
                dueDate = invoiceData.dueDate,
                costCenter = "",
                invoiceType = "",
                priceWithTax = intToDecimal(reservation.priceCents),
                discountedPriceWithTax = intToDecimal(reservation.discountedPriceCents),
                description = invoiceData.description,
                contactPerson = invoiceData.orgRepresentative ?: "",
                orgId = invoiceData.orgId ?: "",
                discountPercentage = reservation.discountPercentage ?: 0
            )
        return model
    }

    fun getDefaultFunction(boatSpaceType: BoatSpaceType): String =
        when (boatSpaceType) {
            BoatSpaceType.Slip -> "T1270"
            BoatSpaceType.Winter -> "T1271"
            BoatSpaceType.Storage -> "T1276"
            BoatSpaceType.Trailer -> "T1270"
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
            .header("Location", "/virkailija/venepaikat/varaukset")
            .body("")
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
