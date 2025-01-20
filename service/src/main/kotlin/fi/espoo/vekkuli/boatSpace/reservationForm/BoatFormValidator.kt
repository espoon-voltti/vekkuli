package fi.espoo.vekkuli.boatSpace.reservationForm

import fi.espoo.vekkuli.boatSpace.reservationForm.components.BusinessIdInput
import fi.espoo.vekkuli.config.BoatSpaceConfig
import fi.espoo.vekkuli.config.MessageUtil
import fi.espoo.vekkuli.controllers.Routes.Companion.USERTYPE
import fi.espoo.vekkuli.controllers.Utils.Companion.getCitizen
import fi.espoo.vekkuli.controllers.Utils.Companion.redirectUrl
import fi.espoo.vekkuli.domain.*
import fi.espoo.vekkuli.service.*
import fi.espoo.vekkuli.utils.FINNISH_NATIONAL_ID_REGEX
import fi.espoo.vekkuli.views.citizen.Layout
import fi.espoo.vekkuli.views.citizen.ReservationConfirmation
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.*
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import java.math.BigDecimal
import java.time.Duration
import java.time.LocalDateTime
import kotlin.reflect.KClass

@Controller
class BoatFormValidator(
    private val layout: Layout,
    private val messageUtil: MessageUtil,
    private val reservationService: BoatReservationService,
    private val reserverService: ReserverService,
    private val organizationService: OrganizationService,
    private val reservationConfirmation: ReservationConfirmation,
    private val businessIdInput: BusinessIdInput
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
        val citizen = getCitizen(request, reserverService) ?: return redirectUrl("/")
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
        val isValid = ssn?.matches(FINNISH_NATIONAL_ID_REGEX.toRegex()) ?: false
        val isUnique = ssn?.let { reserverService.getCitizenBySsn(ssn) == null } ?: false

        return if (isValid && isUnique) {
            ResponseEntity.ok(mapOf("isValid" to true))
        } else {
            val message =
                if (!isValid) {
                    messageUtil.getMessage("validation.nationalId")
                } else {
                    messageUtil.getMessage("validation.uniqueSsn")
                }
            ResponseEntity.ok(mapOf("isValid" to false, "message" to message))
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
            val warning = businessIdInput.warning(organizations ?: listOf(), value ?: "")
            return ResponseEntity.ok(
                mapOf(
                    "isValid" to false,
                    "message" to warning
                )
            )
        }
        return ResponseEntity.ok(mapOf("isValid" to true, "message" to ""))
    }

    @PostMapping("/info/businessid")
    fun businessIdInfo(
        @RequestParam orgBusinessId: String,
    ): ResponseEntity<String> {
        val organizations = organizationService.getOrganizationsByBusinessId(orgBusinessId)
        val showBusinessIdInfo = organizations.isNotEmpty()
        if (showBusinessIdInfo) {
            val info = businessIdInput.infoBox(organizations ?: listOf(), orgBusinessId ?: "")
            return ResponseEntity.ok(
                info
            )
        }
        return ResponseEntity.ok("")
    }
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

class BoatRegistrationValidator : ConstraintValidator<ValidBoatRegistration, BoatRegistrationBaseInput> {
    override fun isValid(
        value: BoatRegistrationBaseInput,
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

fun getReservationTimeInSeconds(
    reservationCreated: LocalDateTime,
    currentDate: LocalDateTime
): Long {
    val reservationTimePassed = Duration.between(reservationCreated, currentDate).toSeconds()
    return (BoatSpaceConfig.SESSION_TIME_IN_SECONDS - reservationTimePassed)
}

interface BoatRegistrationBaseInput {
    val boatId: Int?
    val boatType: BoatType?
    val width: BigDecimal?
    val length: BigDecimal?
    val depth: BigDecimal?
    val weight: Int?
    val noRegistrationNumber: Boolean?
    val boatRegistrationNumber: String?
    val otherIdentification: String?
    val boatName: String?
    val extraInformation: String?
    val ownership: OwnershipStatus?
    val email: String?
    val phone: String?
    val certifyInformation: Boolean?
    val agreeToRules: Boolean?
    val orgPhone: String?
    val orgEmail: String?
    val storageType: StorageType?
    val trailerRegistrationNumber: String?
    val trailerWidth: BigDecimal?
    val trailerLength: BigDecimal?
    val reservationValidity: ReservationValidity?
}
