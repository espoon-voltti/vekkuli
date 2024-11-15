package fi.espoo.vekkuli.boatSpace.renewal

import fi.espoo.vekkuli.config.MessageUtil
import fi.espoo.vekkuli.config.ensureCitizenId
import fi.espoo.vekkuli.config.getAuthenticatedUser
import fi.espoo.vekkuli.controllers.*
import fi.espoo.vekkuli.controllers.UnauthorizedException
import fi.espoo.vekkuli.controllers.Utils.Companion.getCitizen
import fi.espoo.vekkuli.controllers.Utils.Companion.getServiceUrl
import fi.espoo.vekkuli.domain.*
import fi.espoo.vekkuli.service.*
import fi.espoo.vekkuli.utils.TimeProvider
import fi.espoo.vekkuli.utils.cmToM
import fi.espoo.vekkuli.views.citizen.Layout
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
    private val messageUtil: MessageUtil,
    private val reservationService: BoatReservationService,
    private val boatService: BoatService,
    private val citizenService: CitizenService,
    private val organizationService: OrganizationService,
    private val timeProvider: TimeProvider,
    private val boatSpaceRenewalRepository: BoatSpaceRenewalRepository,
    private val invoiceService: BoatSpaceInvoiceService,
    private val boatSpaceRenewalService: BoatSpaceRenewalService
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

        val renewalReservation = boatSpaceRenewalService.createRenewalReservation(userId, reservationId)
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

        val renewal = boatSpaceRenewalRepository.getRenewalReservationForEmployee(userId)

        val renewalReservation =
            renewal ?: reservationService.createRenewalReservationForEmployee(reservationId, userId)
        if (renewalReservation == null) throw IllegalStateException("Reservation not found")

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
        val citizen = getCitizen(request, citizenService)

        val reservation = reservationService.getReservationWithReserver(renewalId)

        if (reservation == null) {
            val headers = org.springframework.http.HttpHeaders()
            headers.location = URI(getServiceUrl("/${UserType.CITIZEN.path}venepaikat"))
            return ResponseEntity(headers, HttpStatus.FOUND)
        }

        if (citizen == null || reservation.reserverId != citizen.id) {
            throw UnauthorizedException()
        }

        var input = formInput.copy(email = citizen?.email, phone = citizen?.phone)
        val usedBoatId = formInput.boatId ?: reservation.boatId // use boat id from reservation if it exists
        if (usedBoatId != null && usedBoatId != 0) {
            val boat = boatService.getBoat(usedBoatId)

            if (boat != null) {
                input =
                    input.copy(
                        boatId = boat.id,
                        depth = boat.depthCm.cmToM(),
                        boatName = boat.name,
                        weight = boat.weightKg,
                        width = boat.widthCm.cmToM(),
                        length = boat.lengthCm.cmToM(),
                        otherIdentification = boat.otherIdentification,
                        extraInformation = boat.extraInformation,
                        ownership = boat.ownership,
                        boatType = boat.type,
                        boatRegistrationNumber = boat.registrationCode,
                        noRegistrationNumber = boat.registrationCode.isNullOrEmpty()
                    )
            }
        } else {
            input = input.copy(boatId = 0)
        }

        val boatReserver = if (input.isOrganization == true) input.organizationId else citizen?.id

        val boats =
            boatReserver?.let {
                boatService
                    .getBoatsForReserver(boatReserver)
                    .map { boat -> boat.updateBoatDisplayName(messageUtil) }
            } ?: emptyList()

        val municipalities = citizenService.getMunicipalities()
        val bodyContent =
            boatSpaceRenewForm.boatSpaceRenewForm(
                reservation,
                boats,
                citizen,
                input,
                getReservationTimeInSeconds(reservation.created, timeProvider.getCurrentDateTime()),
                UserType.CITIZEN,
                municipalities
            )
        val page =
            layout.render(
                true,
                citizen?.fullName,
                request.requestURI,
                bodyContent
            )

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
