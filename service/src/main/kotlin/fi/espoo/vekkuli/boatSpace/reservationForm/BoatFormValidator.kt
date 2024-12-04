package fi.espoo.vekkuli.boatSpace.reservationForm

import fi.espoo.vekkuli.config.BoatSpaceConfig
import fi.espoo.vekkuli.config.MessageUtil
import fi.espoo.vekkuli.controllers.Routes.Companion.USERTYPE
import fi.espoo.vekkuli.controllers.Utils.Companion.getCitizen
import fi.espoo.vekkuli.controllers.Utils.Companion.redirectUrl
import fi.espoo.vekkuli.domain.*
import fi.espoo.vekkuli.service.*
import fi.espoo.vekkuli.views.Warnings
import fi.espoo.vekkuli.views.citizen.Layout
import fi.espoo.vekkuli.views.citizen.ReservationConfirmation
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.*
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import java.time.Duration
import java.time.LocalDateTime
import kotlin.reflect.KClass

@Controller
class BoatFormValidator(
    private val layout: Layout,
    private val messageUtil: MessageUtil,
    private val reservationService: BoatReservationService,
    private val citizenService: CitizenService,
    private val organizationService: OrganizationService,
    private val reservationConfirmation: ReservationConfirmation,
    private val warnings: Warnings,
) {
    // TODO: move this to somewhere else
    @GetMapping("/$USERTYPE/venepaikka/varaus/{reservationId}/vahvistus")
    @ResponseBody
    fun confirmBoatSpaceReservation(
        @PathVariable usertype: String,
        @PathVariable reservationId: Int,
        model: Model,
        request: HttpServletRequest,
    ): ResponseEntity<String> {
        val citizen = getCitizen(request, citizenService) ?: return redirectUrl("/")
        val reservation = reservationService.getBoatSpaceReservation(reservationId)
        if (reservation == null) return redirectUrl("/")

        return ResponseEntity.ok(
            layout.render(
                true,
                citizen.fullName,
                request.requestURI,
                reservationConfirmation.render(reservation)
            )
        )
    }

    @PostMapping("/validate/ssn")
    fun validateSSN(
        @RequestBody request: Map<String, String>
    ): ResponseEntity<Map<String, Any>> {
        val ssn = request["value"]
        val isValid = ssn?.let { citizenService.getCitizenBySsn(ssn) == null } ?: false

        return if (isValid) {
            ResponseEntity.ok(mapOf("isValid" to true))
        } else {
            ResponseEntity.ok(mapOf("isValid" to false, "message" to messageUtil.getMessage("validation.uniqueSsn")))
        }
    }

    @PostMapping("/validate/businessid")
    fun businessIdWarning(
        @RequestBody request: Map<String, String>
    ): ResponseEntity<Map<String, Any>> {
        val value = request["value"]
        val organizations = value?.let { organizationService.getOrganizationsByBusinessId(value) }
        val showBusinessIdWarning = !organizations.isNullOrEmpty()
        if (showBusinessIdWarning) {
            val warning = warnings.businessId(organizations ?: listOf(), value ?: "")
            return ResponseEntity.ok(
                mapOf(
                    "isValid" to false,
                    "message" to warning
                )
            )
        }
        return ResponseEntity.ok(mapOf("isValid" to true, "message" to ""))
    }
}

fun getReservationTimeInSeconds(
    reservationCreated: LocalDateTime,
    currentDate: LocalDateTime
): Long {
    val reservationTimePassed = Duration.between(reservationCreated, currentDate).toSeconds()
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

class BoatRegistrationValidator : ConstraintValidator<ValidBoatRegistration, BoatRegistrationInput> {
    override fun isValid(
        value: BoatRegistrationInput,
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

interface BoatRegistrationInput {
    val noRegistrationNumber: Boolean?
    val boatRegistrationNumber: String?
    val otherIdentification: String?
}
