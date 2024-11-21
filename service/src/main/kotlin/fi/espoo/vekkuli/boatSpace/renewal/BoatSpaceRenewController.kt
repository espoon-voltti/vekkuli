package fi.espoo.vekkuli.boatSpace.renewal

import fi.espoo.vekkuli.config.ensureCitizenId
import fi.espoo.vekkuli.config.ensureEmployeeId
import fi.espoo.vekkuli.controllers.*
import fi.espoo.vekkuli.controllers.Utils.Companion.badRequest
import fi.espoo.vekkuli.controllers.Utils.Companion.getServiceUrl
import fi.espoo.vekkuli.controllers.Utils.Companion.redirectUrl
import fi.espoo.vekkuli.domain.*
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
    private val boatSpaceRenewalService: BoatSpaceRenewalService,
) {
    @RequestMapping("/kuntalainen/venepaikka/jatka/{originalReservationId}")
    @ResponseBody
    fun boatSpaceRenewPage(
        @PathVariable originalReservationId: Int,
        @ModelAttribute formInput: RenewalReservationInput,
        request: HttpServletRequest,
        response: HttpServletResponse,
    ): ResponseEntity<String> {
        val citizenId = request.ensureCitizenId()
        try {
            val renewedReservation = boatSpaceRenewalService.getOrCreateRenewalReservationForCitizen(citizenId, originalReservationId)
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
        } catch (e: IllegalStateException) {
            val headers = org.springframework.http.HttpHeaders()
            headers.location = URI(getServiceUrl("/${UserType.CITIZEN.path}venepaikat"))
            return ResponseEntity(headers, HttpStatus.FOUND)
        } catch (e: Exception) {
            return ResponseEntity.badRequest().body("Failed to renew boat space")
        }
    }

    @RequestMapping("/virkailija/venepaikka/jatka/{originalReservationId}/lasku")
    @ResponseBody
    fun invoiceView(
        @PathVariable originalReservationId: Int,
        request: HttpServletRequest,
    ): ResponseEntity<String> {
        val userId = request.ensureEmployeeId()
        val renewedReservation = boatSpaceRenewalService.getOrCreateRenewalReservationForEmployee(userId, originalReservationId)

        val invoiceModel = boatSpaceRenewalService.getSendInvoiceModel(renewedReservation.id)

        val content = boatSpaceRenewForm.renewInvoicePreview(invoiceModel, renewedReservation.reserverId)
        val page = employeeLayout.render(true, request.requestURI, content)
        return ResponseEntity.ok(page)
    }

    @PostMapping("/kuntalainen/venepaikka/jatka/{originalReservationId}")
    fun renewBoatSpace(
        @PathVariable originalReservationId: Int,
        @Valid @ModelAttribute("input") input: RenewalReservationInput,
        bindingResult: BindingResult,
        request: HttpServletRequest,
    ): ResponseEntity<String> {
        val citizenId = request.ensureCitizenId()

        if (bindingResult.hasErrors()) {
            return badRequest("Invalid input")
        }

        try {
            val renewedReservation = boatSpaceRenewalService.getRenewalReservationForCitizen(citizenId, originalReservationId)
            boatSpaceRenewalService.updateRenewReservation(citizenId, input, renewedReservation.id)

            // redirect to payments page with reservation id and slip type
            return redirectUrl("/kuntalainen/maksut/maksa?id=${renewedReservation.id}&type=${PaymentType.BoatSpaceReservation}")
        } catch (e: Exception) {
            return badRequest(e.message ?: "Failed to renew boat space")
        }
    }

    @PostMapping("/virkailija/venepaikka/jatka/{originalReservationId}/lasku")
    fun sendInvoiceAndTerminateOldReservation(
        @PathVariable originalReservationId: Int,
        request: HttpServletRequest,
    ): ResponseEntity<String> {
        val employeeId = request.ensureEmployeeId()
        try {
            val renewedReservation = boatSpaceRenewalService.getRenewalReservationForEmployee(employeeId, originalReservationId)
            boatSpaceRenewalService.activateRenewalAndSendInvoice(
                renewedReservation.id,
                renewedReservation.reserverId,
                renewedReservation.renewedFromId
            )
        } catch (e: Exception) {
            // TODO: should we respond with error page or redirect to some other page?
            val errorPage = boatSpaceRenewForm.invoiceErrorPage()
            return ResponseEntity.ok(employeeLayout.render(true, request.requestURI, errorPage))
        }
        return ResponseEntity
            .status(HttpStatus.FOUND)
            .header("Location", "/virkailija/venepaikat/varaukset")
            .body("")
    }

    @DeleteMapping("/virkailija/venepaikka/jatka/{renewedReservationId}/lasku")
    fun cancelRenewal(
        @PathVariable renewedReservationId: Int,
        request: HttpServletRequest,
    ): ResponseEntity<String> {
        val employeeId = request.ensureEmployeeId()
        boatSpaceRenewalService.cancelRenewalReservation(renewedReservationId, employeeId)
        return ResponseEntity.noContent().build()
    }

    @DeleteMapping("/kuntalainen/venepaikka/jatka/{renewedReservationId}")
    fun cancelRenewalCitizen(
        @PathVariable renewedReservationId: Int,
        request: HttpServletRequest,
    ): ResponseEntity<String> {
        val citizenId = request.ensureCitizenId()
        boatSpaceRenewalService.cancelRenewalReservation(renewedReservationId, citizenId)
        return redirectUrl("/kuntalainen/omat-tiedot")
    }
}

@ValidBoatRegistration
data class RenewalReservationInput(
    @field:NotNull(message = "{validation.required}")
    private val originalReservationId: Int?,
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
