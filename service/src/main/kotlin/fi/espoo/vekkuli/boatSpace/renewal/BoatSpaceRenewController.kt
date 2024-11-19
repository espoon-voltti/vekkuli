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

@Controller
class BoatSpaceRenewController(
    private val boatSpaceRenewForm: BoatSpaceRenewFormView,
    private val layout: Layout,
    private val employeeLayout: EmployeeLayout,
    private val reservationService: BoatReservationService,
    private val citizenService: CitizenService,
    private val boatSpaceRenewalService: BoatSpaceRenewalService,
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
        headers.location = URI(getServiceUrl("/kuntalainen/venepaikka/jatka/${renewalReservation.id}?oldReservationId=$reservationId"))

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
        val t = request.getAuthenticatedUser()?.id
        val userId = request.getAuthenticatedUser()?.id ?: throw UnauthorizedException()

        val renewalReservation = boatSpaceRenewalService.getOrCreateRenewalReservationForEmployee(userId, reservationId)

        val headers = org.springframework.http.HttpHeaders()
        headers.location = URI(getServiceUrl("/virkailija/venepaikka/jatka/${renewalReservation.id}/lasku?oldReservationId=$reservationId"))

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
        @RequestParam oldReservationId: Int?,
        request: HttpServletRequest,
    ): ResponseEntity<String> {
        if (oldReservationId == null) {
            return ResponseEntity.badRequest().body("Old reservation id is required")
        }
        try {
            boatSpaceRenewalService.activateRenewalAndSendInvoice(renewedReservationId, oldReservationId)
        } catch (e: Exception) {
            val errorPage = boatSpaceRenewForm.invoiceErrorPage()
            return ResponseEntity.ok(employeeLayout.render(true, request.requestURI, errorPage))
        }
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
            boatSpaceRenewalService.buildBoatSpaceRenewalViewParams(citizenId, renewedReservation, formInput)

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
        @RequestParam oldReservationId: Int,
        request: HttpServletRequest,
    ): ResponseEntity<String> {
        // TODO: get the actual data
        val invoiceModel = boatSpaceRenewalService.getSendInvoiceModel(reservationId)
        val content = boatSpaceRenewForm.renewInvoicePreview(invoiceModel, oldReservationId)
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
    override val noRegistrationNumber: Boolean?,
    override val boatRegistrationNumber: String?,
    val boatName: String?,
    override val otherIdentification: String?,
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
) : BoatRegistrationInput
