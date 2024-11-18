package fi.espoo.vekkuli.boatSpace.renewal

import fi.espoo.vekkuli.config.ensureCitizenId
import fi.espoo.vekkuli.config.getAuthenticatedUser
import fi.espoo.vekkuli.controllers.*
import fi.espoo.vekkuli.controllers.UnauthorizedException
import fi.espoo.vekkuli.controllers.Utils.Companion.getCitizen
import fi.espoo.vekkuli.controllers.Utils.Companion.getServiceUrl
import fi.espoo.vekkuli.domain.*
import fi.espoo.vekkuli.service.*
import fi.espoo.vekkuli.views.citizen.Layout
import fi.espoo.vekkuli.views.employee.EmployeeLayout
import fi.espoo.vekkuli.views.employee.InvoicePreview
import fi.espoo.vekkuli.views.employee.InvoiceRow
import fi.espoo.vekkuli.views.employee.SendInvoiceModel
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.*
import jakarta.validation.constraints.*
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*
import java.net.URI
import java.time.LocalDate

@Controller
class BoatSpaceRenewController(
    private val boatSpaceRenewForm: BoatSpaceRenewFormView,
    private val layout: Layout,
    private val employeeLayout: EmployeeLayout,
    private val reservationService: BoatReservationService,
    private val citizenService: CitizenService,
    private val boatSpaceRenewalRepository: BoatSpaceRenewalRepository,
    private val boatSpaceRenewalService: BoatSpaceRenewalService,
    private val invoiceService: BoatSpaceInvoiceService,
    private val sendInvoiceView: InvoicePreview,
) {
    @RequestMapping("/kuntalainen/venepaikka/jatka-varausta/{reservationId}")
    @ResponseBody
    fun boatSpaceRenewForward1(
        @PathVariable reservationId: Int,
        @ModelAttribute formInput: ReservationInput,
        request: HttpServletRequest,
        response: HttpServletResponse,
    ): ResponseEntity<String> {
        val userId = getCitizen(request, citizenService)?.id ?: throw UnauthorizedException()

        val renewalReservation = boatSpaceRenewalService.getOrCreateRenewalReservationForCitizen(userId, reservationId)

        val headers = org.springframework.http.HttpHeaders()
        headers.location = URI(getServiceUrl("/kuntalainen/venepaikka/jatka/${renewalReservation.id}"))

        return ResponseEntity(headers, HttpStatus.FOUND)
    }

    @RequestMapping("/virkailija/venepaikka/jatka-varausta/{reservationId}")
    @ResponseBody
    fun boatSpaceRenewForward(
        @PathVariable reservationId: Int,
        @ModelAttribute formInput: ReservationInput,
        request: HttpServletRequest,
        response: HttpServletResponse,
    ): ResponseEntity<String> {
        val userId = request.getAuthenticatedUser()?.id ?: throw UnauthorizedException()

        val renewalReservation = boatSpaceRenewalService.getOrCreateRenewalReservationForEmployee(userId, reservationId)

        val headers = org.springframework.http.HttpHeaders()
        headers.location = URI(getServiceUrl("/virkailija/venepaikka/jatka/${renewalReservation.id}/lasku"))

        return ResponseEntity(headers, HttpStatus.FOUND)
    }

    @PostMapping("/kuntalainen/venepaikka/jatka/{renewedReservationId}")
    fun renewBoatSpace(
        @PathVariable renewedReservationId: Int,
        @Valid @ModelAttribute("input") input: RenewalReservationInput,
        bindingResult: BindingResult,
        request: HttpServletRequest,
    ): ResponseEntity<String> {
        fun badRequest(body: String): ResponseEntity<String> = ResponseEntity.badRequest().body(body)

        fun redirectUrl(url: String): ResponseEntity<String> =
            ResponseEntity
                .status(HttpStatus.FOUND)
                .header("Location", url)
                .body("")

        if (bindingResult.hasErrors()) {
            reservationService.getReservationWithReserver(renewedReservationId) ?: return redirectUrl("/")
            return badRequest("Invalid input")
        }

        // TODO: Extract code above
        val citizenId = request.ensureCitizenId()

        try {
            boatSpaceRenewalService.updateRenewReservation(citizenId, input, renewedReservationId)
        } catch (e: Exception) {
            return badRequest(e.message ?: "Failed to renew boat space")
        }

        // redirect to payments page with reservation id and slip type
        return redirectUrl("/kuntalainen/maksut/maksa?id=$renewedReservationId&type=${PaymentType.BoatSpaceReservation}")
    }

    @PostMapping("/virkailija/venepaikka/jatka/{renewedReservationId}/lasku")
    fun sendInvoiceAndTerminateOldReservation(
        @PathVariable renewedReservationId: Int,
        reservationId: Int
    ): ResponseEntity<String> {
        boatSpaceRenewalService.activateRenewalAndSendInvoice(renewedReservationId)

        return ResponseEntity
            .status(HttpStatus.FOUND)
            .header("Location", "/virkailija/venepaikat/varaukset")
            .body("")
    }

    @RequestMapping("/kuntalainen/venepaikka/jatka/{renewalId}")
    @ResponseBody
    fun boatSpaceRenewPage(
        @PathVariable renewalId: Int,
        @ModelAttribute formInput: RenewalReservationInput,
        request: HttpServletRequest,
        response: HttpServletResponse,
    ): ResponseEntity<String> {
        val citizenId = request.ensureCitizenId()

        val renewedReservation = reservationService.getReservationWithReserver(renewalId)

        if (renewedReservation == null) {
            val headers = org.springframework.http.HttpHeaders()
            headers.location = URI(getServiceUrl("/${UserType.CITIZEN.path}venepaikat"))
            return ResponseEntity(headers, HttpStatus.FOUND)
        }
        val htmlParams =
            boatSpaceRenewalService.getBoatSpaceRenewViewParams(citizenId, renewedReservation, formInput)

        val page =
            layout.render(
                true,
                renewedReservation.name,
                request.requestURI,
                boatSpaceRenewForm.boatSpaceRenewForm(
                    htmlParams
                )
            )

        return ResponseEntity.ok(page)
    }

    @RequestMapping("/virkailija/venepaikka/jatka/{reservationId}/lasku")
    @ResponseBody
    fun invoiceView(
        @PathVariable reservationId: Int,
        request: HttpServletRequest,
    ): ResponseEntity<String> {
        val reservation = reservationService.getReservationWithReserver(reservationId)
        if (reservation?.reserverId == null) {
            throw IllegalArgumentException("Reservation not found")
        }

        val invoiceBatch = invoiceService.createInvoiceBatchParameters(reservationId, reservation.reserverId)
        val invoice = invoiceBatch?.invoices?.first()
        val invoiceRow =
            invoiceBatch
                ?.invoices
                ?.first()
                ?.rows
                ?.first()
        val recipient = invoice?.recipient
        val recipientAddress = recipient?.address

        val recipientName = "${recipient?.firstName} ${recipient?.lastName}"

        // TODO: get the actual data
        val model =
            SendInvoiceModel(
                reservationId = reservationId,
                reserverName = recipientName,
                reserverSsn = recipient?.ssn ?: "",
                reserverAddress = "${recipientAddress?.street} ${recipientAddress?.postalCode} ${recipientAddress?.postOffice}",
                product = reservation.locationName,
                functionInformation = "?",
                billingPeriodStart = invoiceRow?.periodStartDate ?: "",
                billingPeriodEnd = invoiceRow?.periodEndDate ?: "",
                boatingSeasonStart = LocalDate.of(2025, 5, 1),
                boatingSeasonEnd = LocalDate.of(2025, 9, 30),
                invoiceNumber = invoice?.invoiceNumber.toString(),
                dueDate = LocalDate.of(2025, 12, 31),
                costCenter = "?",
                invoiceType = "?",
                invoiceRows =
                    listOf(
                        InvoiceRow(
                            description = invoiceRow?.description ?: "",
                            customer = recipientName,
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
}

@ValidBoatRegistration
data class RenewalReservationInput(
    @field:NotNull(message = "{validation.required}")
    private val renewedReservationId: Int?,
    val boatId: Int?,
    @field:NotNull(message = "{validation.required}")
    val boatType: BoatType?,
    @field:NotNull(message = "{validation.required}")
    @field:Positive(message = "{validation.positiveNumber}")
    val width: Double?,
    @field:NotNull(message = "{validation.required}")
    @field:Positive(message = "{validation.positiveNumber}")
    val length: Double?,
    @field:NotNull(message = "{validation.required}")
    @field:Positive(message = "{validation.positiveNumber}")
    val depth: Double?,
    @field:NotNull(message = "{validation.required}")
    @field:Positive(message = "{validation.positiveNumber}")
    val weight: Int?,
    val noRegistrationNumber: Boolean?,
    val boatRegistrationNumber: String?,
    val boatName: String?,
    val otherIdentification: String?,
    val extraInformation: String?,
    @field:NotNull(message = "{validation.required}")
    val ownership: OwnershipStatus?,
    @field:NotBlank(message = "{validation.required}")
    @field:Email(message = "{validation.email}")
    val email: String?,
    @field:NotBlank(message = "{validation.required}")
    val phone: String?,
    @field:AssertTrue(message = "{validation.certifyInformation}")
    val certifyInformation: Boolean?,
    @field:AssertTrue(message = "{validation.agreeToRules}")
    val agreeToRules: Boolean?,
    val orgPhone: String? = null,
    val orgEmail: String? = null,
)
