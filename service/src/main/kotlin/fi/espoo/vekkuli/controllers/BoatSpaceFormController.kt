package fi.espoo.vekkuli.controllers

import fi.espoo.vekkuli.common.AppUser
import fi.espoo.vekkuli.common.getAppUser
import fi.espoo.vekkuli.config.BoatSpaceConfig
import fi.espoo.vekkuli.config.BoatSpaceConfig.doesBoatFit
import fi.espoo.vekkuli.config.Dimensions
import fi.espoo.vekkuli.config.MessageUtil
import fi.espoo.vekkuli.config.getAuthenticatedUser
import fi.espoo.vekkuli.controllers.Utils.Companion.getCitizen
import fi.espoo.vekkuli.controllers.Utils.Companion.getServiceUrl
import fi.espoo.vekkuli.controllers.Utils.Companion.redirectUrl
import fi.espoo.vekkuli.domain.*
import fi.espoo.vekkuli.service.BoatReservationService
import fi.espoo.vekkuli.service.BoatService
import fi.espoo.vekkuli.service.CitizenService
import fi.espoo.vekkuli.service.ReserveBoatSpaceInput
import fi.espoo.vekkuli.utils.cmToM
import fi.espoo.vekkuli.utils.mToCm
import fi.espoo.vekkuli.views.citizen.BoatSpaceForm
import fi.espoo.vekkuli.views.citizen.Layout
import fi.espoo.vekkuli.views.employee.EmployeeLayout
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.*
import jakarta.validation.constraints.*
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.inTransactionUnchecked
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*
import java.net.URI
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Month
import kotlin.reflect.KClass

@Controller
class BoatSpaceFormController {
    @Autowired
    private lateinit var employeeLayout: EmployeeLayout

    @Autowired
    private lateinit var boatSpaceForm: BoatSpaceForm

    @Autowired
    lateinit var layout: Layout

    @Autowired
    lateinit var jdbi: Jdbi

    @Autowired
    lateinit var messageUtil: MessageUtil

    @Autowired
    lateinit var reservationService: BoatReservationService

    @Autowired
    lateinit var boatService: BoatService

    @Autowired
    lateinit var citizenService: CitizenService

    @RequestMapping("/{userType:kuntalainen|virkailija}/venepaikka/varaus/{reservationId}")
    @ResponseBody
    fun boatSpaceFormPage(
        @PathVariable userType: String,
        @PathVariable reservationId: Int,
        @RequestParam boatType: BoatType?,
        @RequestParam boatId: Int?,
        @RequestParam width: Double?,
        @RequestParam length: Double?,
        request: HttpServletRequest,
        response: HttpServletResponse,
        model: Model,
    ): ResponseEntity<String> {
        val isEmployee = userType == "virkailija"
        if (isEmployee) {
            val employee = getEmployee(request)
            if (employee == null) {
                return ResponseEntity(HttpStatus.FORBIDDEN)
            }

            val reservation = reservationService.getReservationWithoutCitizen(reservationId)

            if (reservation == null) {
                val headers = org.springframework.http.HttpHeaders()
                headers.location = URI(getServiceUrl("/virkailija/venepaikat"))
                return ResponseEntity(headers, HttpStatus.FOUND)
            }

            if (reservation.employeeId != employee.id) {
                throw UnauthorizedException()
            }

            var input = ReservationInput.initializeInput(boatType, width, length, null)

            val usedBoatId = boatId ?: reservation.boatId // use boat id from reservation if it exists
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

            return ResponseEntity.ok(renderBoatSpaceReservationApplication(reservation, null, input, request))
        }

        val user = getCitizen(request, citizenService)
        val reservation =
            reservationService.getReservationWithCitizen(reservationId)

        if (reservation == null) {
            val headers = org.springframework.http.HttpHeaders()
            headers.location = URI(getServiceUrl("/kuntalainen/venepaikat"))
            return ResponseEntity(headers, HttpStatus.FOUND)
        }

        if (user == null || reservation.citizenId != user.id) {
            throw UnauthorizedException()
        }

        var input = ReservationInput.initializeInput(boatType, width, length, user)
        val usedBoatId = boatId ?: reservation.boatId // use boat id from reservation if it exists
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

        return ResponseEntity.ok(renderBoatSpaceReservationApplication(reservation, user, input, request))
    }

    @DeleteMapping("/{userType:kuntalainen|virkailija}/venepaikka/varaus/{reservationId}")
    fun removeBoatSpaceReservation(
        @PathVariable userType: String,
        @PathVariable reservationId: Int,
        request: HttpServletRequest,
    ): ResponseEntity<Void> {
        val citizen = getCitizen(request, citizenService) ?: return ResponseEntity.noContent().build()
        reservationService.removeBoatSpaceReservation(reservationId, citizen.id)
        return ResponseEntity.noContent().build()
    }

    @PostMapping("/{userType:kuntalainen|virkailija}/venepaikka/varaus/{reservationId}/validate")
    @ResponseBody
    fun validateForm(
        @PathVariable userType: String,
        @PathVariable reservationId: Int,
        @Valid @ModelAttribute("input") input: ReservationInput,
        bindingResult: BindingResult,
        request: HttpServletRequest,
        model: Model,
    ): String {
        val citizen = getCitizen(request, citizenService) ?: return redirectUrl("/")
        val reservation = reservationService.getReservationWithCitizen(reservationId)
        if (reservation == null) return redirectUrl("/")

        return renderBoatSpaceReservationApplication(reservation, citizen, input, request)
    }

    @GetMapping("/{userType:kuntalainen|virkailija}/venepaikka/varaus/{reservationId}/vahvistus")
    fun confirmBoatSpaceReservation(
        @PathVariable userType: String,
        @PathVariable reservationId: Int,
        model: Model,
        request: HttpServletRequest,
    ): String {
        val citizen = getCitizen(request, citizenService) ?: return redirectUrl("/")
        val reservation = reservationService.getBoatSpaceReservation(reservationId, citizen.id)
        if (reservation == null) return redirectUrl("/")
        model.addAttribute("reservation", reservation)
        return "boat-space-reservation-confirmation"
    }

    @PostMapping("/{userType:kuntalainen|virkailija}/venepaikka/varaus/{reservationId}")
    fun reserveBoatSpace(
        @PathVariable userType: String,
        @PathVariable reservationId: Int,
        @Valid @ModelAttribute("input") input: ReservationInput,
        bindingResult: BindingResult,
        request: HttpServletRequest,
    ): ResponseEntity<String> {
        fun badRequest(body: String): ResponseEntity<String> {
            return ResponseEntity.badRequest().body(body)
        }

        fun redirectUrl(url: String): ResponseEntity<String> {
            return ResponseEntity.status(HttpStatus.FOUND)
                .header("Location", url)
                .body("")
        }

        val citizen =
            getCitizen(request, citizenService)
                ?: return ResponseEntity.status(HttpStatus.FOUND)
                    .header("Location", "/")
                    .build()

        if (bindingResult.hasErrors()) {
            val reservation = reservationService.getReservationWithCitizen(reservationId)
            if (reservation == null) {
                return redirectUrl("/")
            }
            return badRequest("Invalid input")
        }

        reservationService.reserveBoatSpace(
            citizen,
            ReserveBoatSpaceInput(
                reservationId = reservationId,
                boatId = input.boatId,
                boatType = input.boatType!!,
                width = input.width ?: 0.0,
                length = input.length ?: 0.0,
                depth = input.depth ?: 0.0,
                weight = input.weight,
                boatRegistrationNumber = input.boatRegistrationNumber ?: "",
                boatName = input.boatName ?: "",
                otherIdentification = input.otherIdentification ?: "",
                extraInformation = input.extraInformation ?: "",
                ownerShip = input.ownership!!,
                email = input.email!!,
                phone = input.phone!!,
            )
        )

        // redirect to payments page with reservation id and slip type
        return redirectUrl("/kuntalainen/maksut/maksa?id=$reservationId&type=${PaymentType.BoatSpaceReservation}")
    }

    @GetMapping("/{userType:kuntalainen|virkailija}/venepaikka/varaa/{spaceId}")
    fun reserveBoatSpace(
        @PathVariable userType: String,
        @PathVariable spaceId: Int,
        @RequestParam boatType: BoatType?,
        @RequestParam width: Double?,
        @RequestParam length: Double?,
        request: HttpServletRequest,
        model: Model,
    ): ResponseEntity<String> {
        val isEmployee = userType == "virkailija"
        val userId =
            if (isEmployee) {
                getEmployee(request)?.id
            } else {
                getCitizen(request, citizenService)?.id
            }

        if (userId == null) {
            return ResponseEntity(HttpStatus.FORBIDDEN)
        }

        val existingReservation =
            if (isEmployee) {
                reservationService.getReservationForEmployee(userId)
            } else {
                reservationService.getReservationForCitizen(userId)
            }

        val reservationId =
            if (existingReservation != null) {
                existingReservation.id
            } else {
                val today = LocalDate.now()
                val endOfYear = LocalDate.of(today.getYear(), Month.DECEMBER, 31)
                if (isEmployee) {
                    reservationService.insertBoatSpaceReservationAsEmployee(userId, spaceId, today, endOfYear).id
                } else {
                    reservationService
                        .insertBoatSpaceReservation(
                            userId,
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
        headers.location = URI(getServiceUrl("/$userType/venepaikka/varaus/$reservationId?$queryString"))
        return ResponseEntity(headers, HttpStatus.FOUND)
    }

    fun renderBoatSpaceReservationApplication(
        reservation: ReservationWithDependencies,
        citizen: Citizen?,
        input: ReservationInput,
        request: HttpServletRequest
    ): String {
        val boats =
            if (citizen == null) {
                emptyList()
            } else {
                boatService
                    .getBoatsForCitizen(citizen.id)
                    .map { boat ->
                        boat.updateBoatDisplayName(messageUtil)
                    }
            }

        val showBoatSizeWarning =
            showBoatSizeWarning(
                input.width?.mToCm(),
                input.length?.mToCm(),
                reservation.amenity,
                reservation.widthCm,
                reservation.lengthCm
            )

        return employeeLayout.render(
            true,
            request.requestURI,
            (
                boatSpaceForm.boatSpaceForm(
                    reservation,
                    boats,
                    citizen,
                    input,
                    showBoatSizeWarning,
                    getReservationTimeInSeconds(reservation.created)
                )
            )
        )
    }

    private fun showBoatSizeWarning(
        widthInCm: Int?,
        lengthInCm: Int?,
        boatSpaceAmenity: BoatSpaceAmenity,
        spaceWidthInCm: Int,
        spaceLengthInCm: Int,
    ): Boolean {
        val boatDimensions = Dimensions(widthInCm ?: 0, lengthInCm ?: 0)
        val spaceDimensions = Dimensions(spaceWidthInCm, spaceLengthInCm)
        return !doesBoatFit(spaceDimensions, boatSpaceAmenity, boatDimensions)
    }

    fun getEmployee(request: HttpServletRequest): AppUser? {
        val authenticatedUser = request.getAuthenticatedUser() ?: return null

        return authenticatedUser.let {
            jdbi.inTransactionUnchecked { tx ->
                tx.getAppUser(authenticatedUser.id)
            }
        }
    }
}

fun getReservationTimeInSeconds(reservationCreated: LocalDateTime): Long {
    val reservationTimePassed = Duration.between(reservationCreated, LocalDateTime.now()).toSeconds()
    return (BoatSpaceConfig.SESSION_TIME_IN_SECONDS - reservationTimePassed)
}

@ResponseStatus(HttpStatus.UNAUTHORIZED)
internal class UnauthorizedException : RuntimeException()

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [BoatRegistrationValidator::class])
annotation class ValidBoatRegistration(
    val message: String = "{validation.required}",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = [],
)

class BoatRegistrationValidator : ConstraintValidator<ValidBoatRegistration, ReservationInput> {
    override fun isValid(
        value: ReservationInput,
        context: ConstraintValidatorContext,
    ): Boolean {
        var isValid = true

        // If registration number is selected, it must be filled
        if (value.noRegistrationNumber != true && value.boatRegistrationNumber.isNullOrBlank()) {
            context.disableDefaultConstraintViolation()
            context
                .buildConstraintViolationWithTemplate(context.defaultConstraintMessageTemplate)
                .addPropertyNode("boatRegistrationNumber")
                .addConstraintViolation()
            isValid = false
        }

        // If no registration number is selected, other identification field must be filled
        if (value.noRegistrationNumber == true && value.otherIdentification.isNullOrBlank()) {
            context.disableDefaultConstraintViolation()
            context
                .buildConstraintViolationWithTemplate(context.defaultConstraintMessageTemplate)
                .addPropertyNode("otherIdentification")
                .addConstraintViolation()
            isValid = false
        }
        return isValid
    }
}

@ValidBoatRegistration
data class ReservationInput(
    @field:NotNull(message = "{validation.required}")
    private val reservationId: Int?,
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
) {
    companion object {
        fun initializeInput(
            boatType: BoatType?,
            width: Double?,
            length: Double?,
            citizen: Citizen?
        ): ReservationInput =
            ReservationInput(
                reservationId = null,
                boatId = null,
                boatType = boatType,
                width = width,
                length = length,
                depth = null,
                weight = null,
                noRegistrationNumber = false,
                boatRegistrationNumber = null,
                boatName = null,
                otherIdentification = null,
                extraInformation = null,
                ownership = OwnershipStatus.Owner,
                email = citizen?.email,
                phone = citizen?.phone,
                agreeToRules = false,
                certifyInformation = false,
            )
    }
}
