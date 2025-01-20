package fi.espoo.vekkuli.boatSpace.reservationForm

import fi.espoo.vekkuli.boatSpace.reservationForm.components.BoatForm
import fi.espoo.vekkuli.boatSpace.reservationForm.components.CitizenContainerForEmployee
import fi.espoo.vekkuli.boatSpace.reservationForm.components.CitizensSearchContent
import fi.espoo.vekkuli.common.BadRequest
import fi.espoo.vekkuli.common.Forbidden
import fi.espoo.vekkuli.common.Unauthorized
import fi.espoo.vekkuli.config.*
import fi.espoo.vekkuli.config.BoatSpaceConfig.doesBoatFit
import fi.espoo.vekkuli.controllers.*
import fi.espoo.vekkuli.controllers.Routes.Companion.USERTYPE
import fi.espoo.vekkuli.controllers.Utils.Companion.badRequest
import fi.espoo.vekkuli.controllers.Utils.Companion.getCitizen
import fi.espoo.vekkuli.controllers.Utils.Companion.getServiceUrl
import fi.espoo.vekkuli.controllers.Utils.Companion.redirectUrl
import fi.espoo.vekkuli.domain.*
import fi.espoo.vekkuli.service.*
import fi.espoo.vekkuli.utils.decimalToInt
import fi.espoo.vekkuli.views.Warnings
import fi.espoo.vekkuli.views.citizen.Layout
import fi.espoo.vekkuli.views.common.CommonComponents
import fi.espoo.vekkuli.views.employee.EmployeeLayout
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.*
import mu.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*
import java.math.BigDecimal
import java.net.URI
import java.util.*

@Controller
class ReservationFormController(
    private val reservationService: ReservationFormService,
    private val reservationFormView: ReservationFormView,
    private val employeeLayout: EmployeeLayout,
    private val reserverService: ReserverService,
    private val messageUtil: MessageUtil,
    private val warnings: Warnings,
    private val layout: Layout,
    private val commonComponents: CommonComponents,
    private val boatForm: BoatForm,
    private val citizensSearchContent: CitizensSearchContent,
    private val citizenContainerForEmployee: CitizenContainerForEmployee
) {
    private val logger = KotlinLogging.logger {}

    @RequestMapping("/kuntalainen/venepaikka/varaus/{reservationId}")
    @ResponseBody
    fun boatSpaceApplicationFormForCitizen(
        @PathVariable reservationId: Int,
        @ModelAttribute formInput: ReservationInput,
        request: HttpServletRequest,
        response: HttpServletResponse,
    ): ResponseEntity<String> {
        request.getAuthenticatedUser()?.let {
            logger.audit(it, "GET_BOAT_SPACE_APPLICATION_FORM", mapOf("targetId" to reservationId.toString()))
        }

        val citizenId = request.ensureCitizenId()

        try {
            val page = reservationService.getBoatSpaceFormForCitizen(citizenId, reservationId, formInput, request.requestURI)
            return ResponseEntity.ok(page)
        } catch (e: BadRequest) {
            val headers = org.springframework.http.HttpHeaders()
            headers.location = URI(getServiceUrl("/kuntalainen/venepaikat"))
            return ResponseEntity(headers, HttpStatus.FOUND)
        } catch (e: UnauthorizedException) {
            return badRequest("Unauthorized")
        }
    }

    @RequestMapping("/virkailija/venepaikka/varaus/{reservationId}")
    @ResponseBody
    fun boatSpaceApplicationFormForEmployee(
        @PathVariable reservationId: Int,
        @ModelAttribute formInput: ReservationInput,
        request: HttpServletRequest,
        response: HttpServletResponse,
    ): ResponseEntity<String> {
        request.getAuthenticatedUser()?.let {
            logger.audit(it, "GET_BOAT_SPACE_APPLICATION_FORM", mapOf("targetId" to reservationId.toString()))
        }
        try {
            val page = reservationService.getBoatSpaceFormForEmployee(reservationId, formInput, request.requestURI)
            return ResponseEntity.ok(page)
        } catch (e: BadRequest) {
            val headers = org.springframework.http.HttpHeaders()
            headers.location = URI(getServiceUrl("/virkailija/venepaikat"))
            return ResponseEntity(headers, HttpStatus.FOUND)
        } catch (e: UnauthorizedException) {
            return badRequest("Unauthorized")
        } catch (e: Exception) {
            // TODO: should we respond with error page or redirect to some other page?
            val errorPage = reservationFormView.errorPage(e.message ?: "Unspecified error", 2)
            return ResponseEntity.ok(employeeLayout.render(true, request.requestURI, errorPage))
        }
    }

    @DeleteMapping("/kuntalainen/venepaikka/varaus/{reservationId}")
    fun removeBoatSpaceReservationAsCitizen(
        @PathVariable reservationId: Int,
        request: HttpServletRequest,
    ): ResponseEntity<Void> {
        request.getAuthenticatedUser()?.let {
            logger.audit(it, "REMOVE_BOAT_SPACE_RESERVATION", mapOf("targetId" to reservationId.toString()))
        }
        val citizenId = request.ensureCitizenId()
        reservationService.removeBoatSpaceReservation(reservationId, citizenId)
        return ResponseEntity.noContent().build()
    }

    @DeleteMapping("/virkailija/venepaikka/varaus/{reservationId}")
    fun removeBoatSpaceReservationAsEmployee(
        @PathVariable reservationId: Int,
        request: HttpServletRequest,
    ): ResponseEntity<Void> {
        request.getAuthenticatedUser()?.let {
            logger.audit(it, "REMOVE_BOAT_SPACE_RESERVATION", mapOf("targetId" to reservationId.toString()))
        }
        val user = request.getAuthenticatedUser() ?: throw Unauthorized()
        reservationService.removeBoatSpaceReservation(reservationId, user.id)
        return ResponseEntity.noContent().build()
    }

    @GetMapping("/$USERTYPE/venepaikka/varaus/{reservationId}/kuntalainen/hae")
    @ResponseBody
    fun searchCitizens(
        request: HttpServletRequest,
        @RequestParam nameParameter: String,
        @PathVariable usertype: String,
        @PathVariable reservationId: Int
    ): String {
        request.getAuthenticatedUser()?.let {
            logger.audit(it, "SEARCH_CITIZENS", mapOf("targetId" to reservationId.toString(), "nameParameter" to nameParameter))
        }
        reserverService.getCitizens(nameParameter).let {
            return citizenContainerForEmployee.reservationFormCitizenSearchContent(it, reservationId)
        }
    }

    @GetMapping("/$USERTYPE/venepaikka/varaus/kuntalainen")
    @ResponseBody
    fun searchCitizen(
        @RequestParam citizenIdOption: UUID,
        @PathVariable usertype: String,
        request: HttpServletRequest
    ): String {
        val citizen = reserverService.getCitizen(citizenIdOption)
        request.getAuthenticatedUser()?.let {
            logger.audit(it, "SEARCH_CITIZENS_RESULTS", mapOf("targetId" to citizenIdOption.toString()))
        }
        return if (citizen != null) {
            commonComponents.citizenDetails(citizen, reserverService.getMunicipalities())
        } else {
            ""
        }
    }

    @GetMapping("/kuntalainen/venepaikka/varaus/{reservationId}/boat-form")
    @ResponseBody
    fun boatFormCitizen(
        @PathVariable reservationId: Int,
        @RequestParam boatId: Int?,
        @RequestParam isOrganization: Boolean?,
        @RequestParam organizationId: UUID?,
        @RequestParam citizenId: UUID?,
        @RequestParam width: BigDecimal?,
        @RequestParam length: BigDecimal?,
        @RequestParam type: BoatType?,
        request: HttpServletRequest,
    ): ResponseEntity<String> {
        request.getAuthenticatedUser()?.let {
            logger.audit(it, "BOAT_FORM_CITIZEN", mapOf("targetId" to reservationId.toString()))
        }
        val citizen = getCitizen(request, reserverService)

        if (citizen == null) return ResponseEntity.badRequest().build()

        val boatFormParams =
            reservationService.buildBoatFormParams(
                reservationId,
                UserType.CITIZEN,
                citizen,
                isOrganization,
                organizationId,
                boatId,
                type,
                width,
                length
            )

        return ResponseEntity.ok(boatForm.render(boatFormParams))
    }

    @GetMapping("/virkailija/venepaikka/varaus/{reservationId}/boat-form")
    @ResponseBody
    fun boatFormEmployee(
        @PathVariable reservationId: Int,
        @RequestParam boatId: Int?,
        @RequestParam isOrganization: Boolean?,
        @RequestParam organizationId: UUID?,
        @RequestParam citizenId: UUID?,
        @RequestParam width: BigDecimal?,
        @RequestParam length: BigDecimal?,
        @RequestParam type: BoatType?,
        request: HttpServletRequest,
    ): ResponseEntity<String> {
        request.getAuthenticatedUser()?.let {
            logger.audit(it, "BOAT_FORM_EMPLOYEE", mapOf("targetId" to reservationId.toString(), "citizenId" to citizenId.toString()))
        }
        if (citizenId == null) return ResponseEntity.badRequest().build()

        val citizen = reserverService.getCitizen(citizenId) ?: return ResponseEntity.badRequest().build()
        val boatFormParams =
            reservationService.buildBoatFormParams(
                reservationId,
                UserType.EMPLOYEE,
                citizen,
                isOrganization,
                organizationId,
                boatId,
                type,
                width,
                length
            )
        return ResponseEntity.ok(boatForm.render(boatFormParams))
    }

    @PostMapping("/kuntalainen/venepaikka/varaus/{reservationId}")
    fun reserveBoatSpaceForCitizen(
        @PathVariable reservationId: Int,
        @Valid @ModelAttribute("input") input: ReservationInput,
        bindingResult: BindingResult,
        request: HttpServletRequest,
    ): ResponseEntity<String> {
        request.getAuthenticatedUser()?.let {
            logger.audit(it, "RESERVE_BOAT_SPACE_CITIZEN", mapOf("targetId" to reservationId.toString()))
        }

        val citizenId = request.ensureCitizenId()

        if (bindingResult.hasErrors()) {
            logger.error { "Backend validation errors: ${bindingResult.allErrors}" }
            return badRequest("Invalid input")
        }
        try {
            reservationService.createOrUpdateReserverAndReservationForCitizen(reservationId, citizenId, input)
        } catch (e: Forbidden) {
            return ResponseEntity.ok(
                renderCitizenErrorPage(
                    getCitizen(request, reserverService),
                    request,
                    messageUtil.getMessage("errorCode.split.${e.errorCode}")
                )
            )
        }
        // redirect to payments page with reservation id and slip type
        return redirectUrl("/kuntalainen/maksut/maksa?id=$reservationId&type=BoatSpaceReservation")
    }

    @PostMapping("/virkailija/venepaikka/varaus/{reservationId}")
    fun reserveBoatSpaceForEmployee(
        @PathVariable reservationId: Int,
        @Valid @ModelAttribute("input") input: ReservationInput,
        bindingResult: BindingResult,
        request: HttpServletRequest,
    ): ResponseEntity<String> {
        request.getAuthenticatedUser()?.let {
            logger.audit(it, "RESERVE_BOAT_SPACE_EMPLOYEE", mapOf("targetId" to reservationId.toString()))
        }
        request.ensureEmployeeId()

        if (bindingResult.hasErrors()) {
            logger.error { "Backend validation errors: ${bindingResult.allErrors}" }
            val reservation = reservationService.getReservationForApplicationForm(reservationId)
            if (reservation == null) {
                return redirectUrl("/")
            }
            return badRequest("Invalid input")
        }
        reservationService.updateReserverAndReservationForEmployee(reservationId, input)

        return redirectUrl("/virkailija/venepaikka/varaus/$reservationId/lasku")
    }

    @GetMapping("/venepaikka/varaus/{reservationId}/boat-type-warning")
    fun boatTypeWarning(
        @PathVariable reservationId: Int,
        @RequestParam boatType: BoatType,
        request: HttpServletRequest,
    ): ResponseEntity<String> {
        request.getAuthenticatedUser()?.let {
            logger.audit(
                it,
                "BOAT_TYPE_WARNING",
                mapOf(
                    "targetId" to reservationId.toString()
                )
            )
        }
        val reservation = reservationService.getReservationForApplicationForm(reservationId)
        val excludedBoatTypes = reservation?.excludedBoatTypes
        if (excludedBoatTypes != null && excludedBoatTypes.contains(boatType)) {
            return ResponseEntity.ok(warnings.boatTypeWarning())
        }
        return ResponseEntity.ok("")
    }

    @GetMapping("/venepaikka/varaus/{reservationId}/boat-size-warning")
    fun boatSizeWarning(
        @PathVariable reservationId: Int,
        @RequestParam width: BigDecimal?,
        @RequestParam length: BigDecimal?,
        request: HttpServletRequest,
    ): ResponseEntity<String> {
        request.getAuthenticatedUser()?.let {
            logger.audit(
                it,
                "BOAT_SIZE_WARNING",
                mapOf(
                    "targetId" to reservationId.toString()
                )
            )
        }
        val reservation = reservationService.getReservationForApplicationForm(reservationId)

        if (reservation == null) {
            return ResponseEntity.badRequest().build()
        }

        val showBoatSizeWarning =
            showBoatSizeWarning(
                decimalToInt(width),
                decimalToInt(length),
                reservation.amenity,
                reservation.widthCm,
                reservation.lengthCm
            )
        if (showBoatSizeWarning) {
            return ResponseEntity.ok(warnings.boatSizeWarning())
        }
        return ResponseEntity.ok("")
    }

    private fun showBoatSizeWarning(
        widthInCm: Int?,
        lengthInCm: Int?,
        boatSpaceAmenity: BoatSpaceAmenity,
        spaceWidthInCm: Int,
        spaceLengthInCm: Int,
    ): Boolean {
        val boatDimensions = Dimensions(widthInCm, lengthInCm)
        val spaceDimensions = Dimensions(spaceWidthInCm, spaceLengthInCm)
        return !doesBoatFit(spaceDimensions, boatSpaceAmenity, boatDimensions)
    }

    @GetMapping("/venepaikka/varaus/{reservationId}/boat-weight-warning")
    fun boatWeight(
        @PathVariable reservationId: Int,
        @RequestParam weight: Int?,
        request: HttpServletRequest,
    ): ResponseEntity<String> {
        request.getAuthenticatedUser()?.let {
            logger.audit(
                it,
                "BOAT_WEIGHT_WARNING",
                mapOf(
                    "targetId" to reservationId.toString()
                )
            )
        }
        if (weight != null && weight > BoatSpaceConfig.BOAT_WEIGHT_THRESHOLD_KG) {
            return ResponseEntity.ok(warnings.boatWeightWarning())
        }
        return ResponseEntity.ok("")
    }

    // initial reservation in info state
    @GetMapping("/kuntalainen/venepaikka/varaa/{spaceId}")
    fun reserveBoatSpaceByCitizen(
        @PathVariable spaceId: Int,
        @RequestParam boatType: BoatType?,
        @RequestParam width: BigDecimal?,
        @RequestParam length: BigDecimal?,
        request: HttpServletRequest,
        model: Model,
    ): ResponseEntity<String> {
        request.getAuthenticatedUser()?.let {
            logger.audit(it, "INITIAL_BOAT_SPACE_RESERVATION", mapOf("targetId" to spaceId.toString()))
        }
        val citizen = getCitizen(request, reserverService)
        if (citizen?.id == null) {
            return ResponseEntity(HttpStatus.FORBIDDEN)
        }
        try {
            val reservationId = reservationService.getOrCreateReservationForCitizen(citizen.id, spaceId)
            val queryString = createQueryParamsForBoatInformation(boatType, width, length)

            val headers = org.springframework.http.HttpHeaders()
            headers.location = URI(getServiceUrl("/kuntalainen/venepaikka/varaus/$reservationId?$queryString"))
            return ResponseEntity(headers, HttpStatus.FOUND)
        } catch (e: Forbidden) {
            return ResponseEntity.ok(
                renderCitizenErrorPage(
                    citizen,
                    request,
                    messageUtil.getMessage("errorCode.split.${e.errorCode}")
                )
            )
        }
    }

    // initial reservation in info state
    @GetMapping("/virkailija/venepaikka/varaa/{spaceId}")
    fun reserveBoatSpaceByEmployee(
        @PathVariable spaceId: Int,
        @RequestParam boatType: BoatType?,
        @RequestParam width: BigDecimal?,
        @RequestParam length: BigDecimal?,
        request: HttpServletRequest,
        model: Model,
    ): ResponseEntity<String> {
        request.getAuthenticatedUser()?.let {
            logger.audit(it, "INITIAL_BOAT_SPACE_RESERVATION", mapOf("targetId" to spaceId.toString()))
        }
        val employeeId = request.ensureEmployeeId()

        val reservationId =
            reservationService.getOrCreateReservationIdForEmployee(employeeId, spaceId)

        val queryString = createQueryParamsForBoatInformation(boatType, width, length)

        val headers = org.springframework.http.HttpHeaders()
        headers.location = URI(getServiceUrl("/virkailija/venepaikka/varaus/$reservationId?$queryString"))
        return ResponseEntity(headers, HttpStatus.FOUND)
    }

    private fun createQueryParamsForBoatInformation(
        boatType: BoatType?,
        width: BigDecimal?,
        length: BigDecimal?,
    ): String {
        val queryParams = mutableListOf<String>()
        boatType?.let { queryParams.add("boatType=${it.name}") }
        width?.let { queryParams.add("width=$it") }
        length?.let { queryParams.add("length=$it") }

        val queryString = queryParams.joinToString("&")
        return queryString
    }

    fun renderCitizenErrorPage(
        citizen: CitizenWithDetails?,
        request: HttpServletRequest,
        error: String
    ): String =
        layout.render(
            true,
            citizen?.fullName,
            request.requestURI,
            reservationFormView.errorPage(error, 2)
        )
}
