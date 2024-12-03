package fi.espoo.vekkuli.boatSpace.reservationForm

import fi.espoo.vekkuli.common.BadRequest
import fi.espoo.vekkuli.common.Unauthorized
import fi.espoo.vekkuli.config.*
import fi.espoo.vekkuli.config.BoatSpaceConfig.doesBoatFit
import fi.espoo.vekkuli.controllers.*
import fi.espoo.vekkuli.controllers.Routes.Companion.USERTYPE
import fi.espoo.vekkuli.controllers.Utils.Companion.badRequest
import fi.espoo.vekkuli.controllers.Utils.Companion.getCitizen
import fi.espoo.vekkuli.controllers.Utils.Companion.getEmployee
import fi.espoo.vekkuli.controllers.Utils.Companion.getServiceUrl
import fi.espoo.vekkuli.controllers.Utils.Companion.redirectUrl
import fi.espoo.vekkuli.domain.*
import fi.espoo.vekkuli.service.*
import fi.espoo.vekkuli.utils.TimeProvider
import fi.espoo.vekkuli.utils.cmToM
import fi.espoo.vekkuli.utils.mToCm
import fi.espoo.vekkuli.views.Warnings
import fi.espoo.vekkuli.views.citizen.BoatFormInput
import fi.espoo.vekkuli.views.citizen.Layout
import fi.espoo.vekkuli.views.employee.EmployeeLayout
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.*
import jakarta.validation.constraints.*
import org.jdbi.v3.core.Jdbi
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*
import java.math.BigDecimal
import java.net.URI
import java.time.LocalDate
import java.time.Month
import java.util.*

@Controller
class ReservationController(
    private val reservationService: ReservationService,
    private val reservationFormView: ReservationFormView,
    private val employeeLayout: EmployeeLayout,
    private val citizenService: CitizenService,
    private val boatService: BoatService,
    private val messageUtil: MessageUtil,
    private val organizationService: OrganizationService,
    private val warnings: Warnings,
    private val timeProvider: TimeProvider,
    private val permissionService: PermissionService,
    private val boatReservationService: BoatReservationService,
    private val layout: Layout,
    private val jdbi: Jdbi
) {
    @RequestMapping("/kuntalainen/venepaikka/varaus/{reservationId}")
    @ResponseBody
    fun boatSpaceApplicationFormForCitizen(
        @PathVariable reservationId: Int,
        @ModelAttribute formInput: ReservationInput,
        request: HttpServletRequest,
        response: HttpServletResponse,
    ): ResponseEntity<String> {
        val citizenId = request.ensureCitizenId()

        try {
            val page = reservationService.getBoatSpaceFormForCitizen(citizenId, reservationId, formInput)
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
        val citizenId = request.ensureCitizenId()
        reservationService.removeBoatSpaceReservation(reservationId, citizenId)
        return ResponseEntity.noContent().build()
    }

    @DeleteMapping("/virkailija/venepaikka/varaus/{reservationId}")
    fun removeBoatSpaceReservationAsEmployee(
        @PathVariable reservationId: Int,
        request: HttpServletRequest,
    ): ResponseEntity<Void> {
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
        citizenService.getCitizens(nameParameter).let {
            return reservationFormView.citizensSearchForm(it, reservationId)
        }
    }

    @GetMapping("/$USERTYPE/venepaikka/varaus/kuntalainen")
    @ResponseBody
    fun searchCitizen(
        @RequestParam citizenIdOption: UUID,
        @PathVariable usertype: String,
    ): String {
        val citizen = citizenService.getCitizen(citizenIdOption)

        return if (citizen != null) {
            reservationFormView.citizenDetails(citizen, citizenService.getMunicipalities())
        } else {
            ""
        }
    }

    @GetMapping("/$USERTYPE/venepaikka/varaus/{reservationId}/boat-form")
    @ResponseBody
    fun boatForm(
        @PathVariable usertype: String,
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
        val userType = UserType.fromPath(usertype)
        val isEmployee = userType == UserType.EMPLOYEE
        val citizen =
            if (isEmployee) {
                if (citizenId == null) return ResponseEntity.badRequest().build()
                citizenService.getCitizen(citizenId)
            } else {
                getCitizen(request, citizenService)
            }
        if (citizen == null) return ResponseEntity.badRequest().build()
        val reserverId = if (isOrganization == true) organizationId else citizen.id
        val boats =
            if (reserverId != null) {
                boatService
                    .getBoatsForReserver(reserverId)
                    .map { boat ->
                        boat.updateBoatDisplayName(messageUtil)
                    }
            } else {
                emptyList()
            }

        val boat = if (boatId != null) boats.find { it.id == boatId } else null
        val input =
            if (boat !=
                null
            ) {
                BoatFormInput(
                    id = boat.id,
                    boatName = boat.name ?: "",
                    boatType = boat.type,
                    width = boat.widthCm.cmToM(),
                    length = boat.lengthCm.cmToM(),
                    depth = boat.depthCm.cmToM(),
                    weight = boat.weightKg,
                    boatRegistrationNumber = boat.registrationCode ?: "",
                    otherIdentification = boat.otherIdentification ?: "",
                    extraInformation = boat.extraInformation ?: "",
                    ownership = boat.ownership,
                    noRegistrationNumber = boat.registrationCode.isNullOrBlank(),
                )
            } else {
                BoatFormInput(
                    id = boatId ?: 0,
                    boatName = "",
                    boatType = type ?: BoatType.OutboardMotor,
                    width = width,
                    length = length,
                    depth = null,
                    weight = null,
                    boatRegistrationNumber = "",
                    otherIdentification = "",
                    extraInformation = "",
                    ownership = OwnershipStatus.Owner,
                    noRegistrationNumber = false,
                )
            }

        return ResponseEntity.ok(reservationFormView.boatForm(userType, citizen, boats, reservationId, input))
    }

    @PostMapping("/kuntalainen/venepaikka/varaus/{reservationId}")
    fun reserveBoatSpaceForCitizen(
        @PathVariable reservationId: Int,
        @Valid @ModelAttribute("input") input: ReservationInput,
        bindingResult: BindingResult,
        request: HttpServletRequest,
    ): ResponseEntity<String> {
        val citizenId = request.ensureCitizenId()

        if (bindingResult.hasErrors()) {
            return badRequest("Invalid input")
        }

        reservationService.createOrUpdateReserverAndReservationForCitizen(reservationId, citizenId, input)
        // redirect to payments page with reservation id and slip type
        return redirectUrl("/kuntalainen/maksut/maksa?id=$reservationId&type=${PaymentType.BoatSpaceReservation}")
    }

    @PostMapping("/virkailija/venepaikka/varaus/{reservationId}")
    fun reserveBoatSpaceForEmployee(
        @PathVariable reservationId: Int,
        @Valid @ModelAttribute("input") input: ReservationInput,
        bindingResult: BindingResult,
        request: HttpServletRequest,
    ): ResponseEntity<String> {
        request.ensureEmployeeId()

        if (bindingResult.hasErrors()) {
            val reservation = reservationService.getReservationForApplicationForm(reservationId)
            if (reservation == null) {
                return redirectUrl("/")
            }
            return badRequest("Invalid input")
        }
        val citizen = reservationService.createOrUpdateCitizen(input)
        if (citizen == null) {
            return ResponseEntity
                .status(HttpStatus.FOUND)
                .header("Location", "/")
                .build()
        }
        reservationService.createOrUpdateReserverAndReservationForEmployee(reservationId, citizen.id, input)

        return redirectUrl("/virkailija/venepaikka/varaus/$reservationId/lasku")
    }

    @GetMapping("/venepaikka/varaus/{reservationId}/boat-type-warning")
    fun boatTypeWarning(
        @PathVariable reservationId: Int,
        @RequestParam boatType: BoatType,
        request: HttpServletRequest,
    ): ResponseEntity<String> {
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
        val reservation = reservationService.getReservationForApplicationForm(reservationId)

        if (reservation == null) {
            return ResponseEntity.badRequest().build()
        }

        val showBoatSizeWarning =
            showBoatSizeWarning(
                width?.mToCm(),
                length?.mToCm(),
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
        if (weight != null && weight > BoatSpaceConfig.BOAT_WEIGHT_THRESHOLD_KG) {
            return ResponseEntity.ok(warnings.boatWeightWarning())
        }
        return ResponseEntity.ok("")
    }

    // initial reservation in info state
    @GetMapping("/$USERTYPE/venepaikka/varaa/{spaceId}")
    fun reserveBoatSpace(
        @PathVariable usertype: String,
        @PathVariable spaceId: Int,
        @RequestParam boatType: BoatType?,
        @RequestParam width: BigDecimal?,
        @RequestParam length: BigDecimal?,
        request: HttpServletRequest,
        model: Model,
    ): ResponseEntity<String> {
        val userType = UserType.fromPath(usertype)
        val isEmployee = userType == UserType.EMPLOYEE
        val citizen = getCitizen(request, citizenService)
        val userId =
            if (isEmployee) {
                getEmployee(request, jdbi)?.id
            } else {
                citizen?.id
            }
        if (userId == null) {
            return ResponseEntity(HttpStatus.FORBIDDEN)
        }

        // Show error page if citizen can not reserve slip
        if (!isEmployee) {
            if ((citizen == null)) {
                return ResponseEntity(HttpStatus.FORBIDDEN)
            }
            val result = permissionService.canReserveANewSlip(citizen.id)
            if (result is ReservationResult.Failure) {
                return ResponseEntity.ok(
                    renderErrorPage(
                        citizen,
                        request,
                        messageUtil.getMessage("errorCode.split.${result.errorCode}")
                    )
                )
            }
        }

        val existingReservation =
            if (isEmployee) {
                boatReservationService.getUnfinishedReservationForEmployee(userId)
            } else {
                boatReservationService.getUnfinishedReservationForCitizen(userId)
            }

        val reservationId =
            if (existingReservation != null) {
                existingReservation.id
            } else {
                val today = timeProvider.getCurrentDate()
                val endOfYear = LocalDate.of(today.year, Month.DECEMBER, 31)
                if (isEmployee) {
                    boatReservationService.insertBoatSpaceReservationAsEmployee(userId, spaceId, today, endOfYear).id
                } else {
                    boatReservationService
                        .insertBoatSpaceReservation(
                            userId,
                            citizen?.id,
                            spaceId,
                            today,
                            endOfYear,
                        ).id
                }
            }

        val queryParams = mutableListOf<String>()
        boatType?.let { queryParams.add("boatType=${it.name}") }
        width?.let { queryParams.add("width=$it") }
        length?.let { queryParams.add("length=$it") }

        val queryString = queryParams.joinToString("&")

        val headers = org.springframework.http.HttpHeaders()
        headers.location = URI(getServiceUrl("/${userType.path}/venepaikka/varaus/$reservationId?$queryString"))
        return ResponseEntity(headers, HttpStatus.FOUND)
    }

    fun renderErrorPage(
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

@ValidBoatRegistration
data class RenewalReservationInput(
    @field:NotNull(message = "{validation.required}")
    private val originalReservationId: Int?,
    val boatId: Int?,
    @field:NotNull(message = "{validation.required}")
    val boatType: BoatType?,
    @field:NotNull(message = "{validation.required}")
    @field:Positive(message = "{validation.positiveNumber}")
    val width: BigDecimal?,
    @field:NotNull(message = "{validation.required}")
    @field:Positive(message = "{validation.positiveNumber}")
    val length: BigDecimal?,
    @field:NotNull(message = "{validation.required}")
    @field:Positive(message = "{validation.positiveNumber}")
    val depth: BigDecimal?,
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
