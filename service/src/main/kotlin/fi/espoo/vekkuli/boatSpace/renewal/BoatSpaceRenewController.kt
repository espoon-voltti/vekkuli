package fi.espoo.vekkuli.boatSpace.renewal

import fi.espoo.vekkuli.boatSpace.reservationForm.BoatRegistrationBaseInput
import fi.espoo.vekkuli.boatSpace.reservationForm.ValidBoatRegistration
import fi.espoo.vekkuli.common.Conflict
import fi.espoo.vekkuli.config.audit
import fi.espoo.vekkuli.config.ensureCitizenId
import fi.espoo.vekkuli.config.ensureEmployeeId
import fi.espoo.vekkuli.config.getAuthenticatedUser
import fi.espoo.vekkuli.controllers.*
import fi.espoo.vekkuli.controllers.Utils.Companion.badRequest
import fi.espoo.vekkuli.controllers.Utils.Companion.getServiceUrl
import fi.espoo.vekkuli.controllers.Utils.Companion.redirectUrl
import fi.espoo.vekkuli.domain.*
import fi.espoo.vekkuli.service.BoatReservationService
import fi.espoo.vekkuli.views.citizen.Layout
import fi.espoo.vekkuli.views.employee.EmployeeLayout
import fi.espoo.vekkuli.views.employee.InvoicePreview
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.*
import jakarta.validation.constraints.*
import mu.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*
import java.math.BigDecimal
import java.net.URI
import java.util.*

@Controller
class BoatSpaceRenewController(
    private val boatSpaceRenewForm: BoatSpaceRenewFormView,
    private val layout: Layout,
    private val employeeLayout: EmployeeLayout,
    private val boatSpaceRenewalService: BoatSpaceRenewalService,
    private val invoicePreview: InvoicePreview,
    private val boatReservationService: BoatReservationService,
) {
    private val logger = KotlinLogging.logger {}

    @RequestMapping("/kuntalainen/venepaikka/jatka/{originalReservationId}")
    @ResponseBody
    fun boatSpaceRenewPage(
        @PathVariable originalReservationId: Int,
        @ModelAttribute formInput: ModifyReservationInput,
        request: HttpServletRequest,
        response: HttpServletResponse,
    ): ResponseEntity<String> {
        val citizenId = request.ensureCitizenId()
        try {
            val renewedReservation =
                boatSpaceRenewalService.getOrCreateRenewalReservationForCitizen(citizenId, originalReservationId)
            val boatSpaceRenewForm = boatSpaceRenewalService.buildRenewForm(citizenId, renewedReservation, formInput)
            val page =
                layout.render(
                    true,
                    renewedReservation.name,
                    request.requestURI,
                    boatSpaceRenewForm
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
        try {
            val renewedReservation =
                boatSpaceRenewalService.getOrCreateRenewalReservationForEmployee(userId, originalReservationId)

            val invoiceModel = boatSpaceRenewalService.getSendInvoiceModel(renewedReservation.id)
            if (renewedReservation.reserverId == null || renewedReservation.originalReservationId == null) {
                return badRequest("Invalid renewal reservation")
            }
            val content =
                invoicePreview.render(
                    invoiceModel,
                    submitUrl = "/virkailija/venepaikka/jatka/${renewedReservation.originalReservationId}/lasku",
                    backUrl = getBackUrl(renewedReservation.reserverType, renewedReservation.reserverId),
                    deleteUrl = "/virkailija/venepaikka/jatka/${renewedReservation.id}/lasku",
                    invoiceModel.orgId.isNotEmpty()
                )
            val page = employeeLayout.render(true, request.requestURI, content)
            return ResponseEntity.ok(page)
        } catch (e: Conflict) {
            return redirectUrl("/virkailija/venepaikat/varaukset")
        } catch (e: Exception) {
            return badRequest("Unable to renew reservation")
        }
    }

    @PostMapping("/kuntalainen/venepaikka/jatka/{originalReservationId}")
    fun renewBoatSpace(
        @PathVariable originalReservationId: Int,
        @Valid @ModelAttribute("input") input: ModifyReservationInput,
        bindingResult: BindingResult,
        request: HttpServletRequest,
    ): ResponseEntity<String> {
        request.getAuthenticatedUser()?.let {
            logger.audit(it, "RENEW_BOAT_SPACE")
        }
        val citizenId = request.ensureCitizenId()

        if (bindingResult.hasErrors()) {
            return badRequest("Invalid input")
        }

        try {
            val renewedReservation = boatSpaceRenewalService.getRenewalReservationForCitizen(citizenId, originalReservationId)
            boatSpaceRenewalService.updateRenewReservation(citizenId, input, renewedReservation.id)

            // redirect to payments page with reservation id and slip type
            return redirectUrl("/kuntalainen/maksut/maksa?id=${renewedReservation.id}&type=BoatSpaceReservation")
        } catch (e: Exception) {
            return badRequest(e.message ?: "Failed to renew boat space")
        }
    }

    @PostMapping("/virkailija/venepaikka/jatka/{originalReservationId}/lasku")
    fun sendInvoiceAndTerminateOldReservation(
        @PathVariable originalReservationId: Int,
        request: HttpServletRequest,
    ): ResponseEntity<String> {
        request.getAuthenticatedUser()?.let {
            logger.audit(it, "RENEW_BOAT_SPACE_INVOICE")
        }
        val employeeId = request.ensureEmployeeId()
        try {
            val renewedReservation = boatSpaceRenewalService.getRenewalReservationForEmployee(employeeId, originalReservationId)
            boatSpaceRenewalService.activateRenewalAndSendInvoice(
                renewedReservation.id,
                renewedReservation.reserverId,
                renewedReservation.originalReservationId,
            )
            return redirectUrl(getBackUrl(renewedReservation.reserverType, renewedReservation.reserverId))
        } catch (e: Exception) {
            // TODO: should we respond with error page or redirect to some other page?
            val errorPage = boatSpaceRenewForm.invoiceErrorPage()
            return ResponseEntity.ok(employeeLayout.render(true, request.requestURI, errorPage))
        }
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

    @DeleteMapping("/virkailija/venepaikka/jatka/{renewedReservationId}/lasku")
    fun cancelRenewal(
        @PathVariable renewedReservationId: Int,
        request: HttpServletRequest,
    ): ResponseEntity<String> {
        request.getAuthenticatedUser()?.let {
            logger.audit(it, "RENEW_BOAT_SPACE_CANCEL")
        }
        val employeeId = request.ensureEmployeeId()
        boatSpaceRenewalService.cancelRenewalReservation(renewedReservationId, employeeId)
        return ResponseEntity.noContent().build()
    }

    @DeleteMapping("/kuntalainen/venepaikka/jatka/{renewedReservationId}")
    fun cancelRenewalCitizen(
        @PathVariable renewedReservationId: Int,
        request: HttpServletRequest,
    ): ResponseEntity<String> {
        request.getAuthenticatedUser()?.let {
            logger.audit(it, "RENEW_BOAT_SPACE_CANCEL")
        }
        val citizenId = request.ensureCitizenId()
        boatSpaceRenewalService.cancelRenewalReservation(renewedReservationId, citizenId)
        return redirectUrl("/kuntalainen/omat-tiedot")
    }
}

@ValidBoatRegistration
data class ModifyReservationInput(
    @field:NotNull(message = "{validation.required}")
    private val originalReservationId: Int?,
    override val boatId: Int?,
    @field:NotNull(message = "{validation.required}")
    override val boatType: BoatType?,
    @field:NotNull(message = "{validation.required}")
    @field:Positive(message = "{validation.positiveNumber}")
    override val width: BigDecimal?,
    @field:NotNull(message = "{validation.required}")
    @field:Positive(message = "{validation.positiveNumber}")
    override val length: BigDecimal?,
    @field:NotNull(message = "{validation.required}")
    @field:Positive(message = "{validation.positiveNumber}")
    override val depth: BigDecimal?,
    @field:NotNull(message = "{validation.required}")
    @field:Positive(message = "{validation.positiveNumber}")
    override val weight: Int?,
    override val noRegistrationNumber: Boolean?,
    override val boatRegistrationNumber: String?,
    override val boatName: String?,
    override val otherIdentification: String?,
    override val extraInformation: String?,
    @field:NotNull(message = "{validation.required}")
    override val ownership: OwnershipStatus?,
    @field:NotBlank(message = "{validation.required}")
    @field:Email(message = "{validation.email}")
    override val email: String?,
    @field:NotBlank(message = "{validation.required}")
    override val phone: String?,
    @field:AssertTrue(message = "{validation.certifyInformation}")
    override val certifyInformation: Boolean?,
    @field:AssertTrue(message = "{validation.agreeToRules}")
    override val agreeToRules: Boolean?,
    override val orgPhone: String? = null,
    override val orgEmail: String? = null,
    override val storageType: StorageType = StorageType.None,
    override val trailerRegistrationNumber: String? = null,
    override val trailerWidth: BigDecimal? = null,
    override val trailerLength: BigDecimal? = null,
    override val reservationValidity: ReservationValidity = ReservationValidity.Indefinite
) : BoatRegistrationBaseInput
