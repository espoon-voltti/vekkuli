package fi.espoo.vekkuli.controllers

import fi.espoo.vekkuli.config.BoatSpaceConfig
import fi.espoo.vekkuli.config.MessageUtil
import fi.espoo.vekkuli.controllers.Utils.Companion.getCitizen
import fi.espoo.vekkuli.controllers.Utils.Companion.redirectUrl
import fi.espoo.vekkuli.domain.*
import fi.espoo.vekkuli.utils.cmToM
import fi.espoo.vekkuli.utils.mToCm
import jakarta.servlet.http.HttpServletRequest
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
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.reflect.KClass

@Controller
@RequestMapping("/kuntalainen")
class BoatSpaceFormController {
    @Autowired
    lateinit var jdbi: Jdbi

    @Autowired
    lateinit var messageUtil: MessageUtil

    @RequestMapping("/venepaikka/varaus/{reservationId}")
    fun boatSpaceFormPage(
        @PathVariable reservationId: Int,
        @RequestParam boatType: BoatType?,
        @RequestParam boatId: Int?,
        @RequestParam width: Double?,
        @RequestParam length: Double?,
        request: HttpServletRequest,
        model: Model
    ): String {
        val user = getCitizen(request, jdbi)
        val reservation =
            jdbi.inTransactionUnchecked {
                it.getReservationWithCitizen(reservationId)
            }

        if (reservation == null) return redirectUrl("/")

        if (user == null || reservation.citizenId != user.id) {
            throw UnauthorizedException()
        }

        var input = ReservationInput.initializeInput(boatType, width, length)

        if (boatId != null && boatId != 0) {
            val boat = jdbi.inTransactionUnchecked { it.getBoat(boatId) }
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
                        ownerShip = boat.ownership,
                        boatType = boat.type,
                        boatRegistrationNumber = boat.registrationCode,
                    )
            }
        } else {
            input = input.copy(boatId = 0)
        }

        return renderBoatSpaceReservationApplication(reservation, user, model, input)
    }

    @DeleteMapping("/venepaikka/varaus/{reservationId}")
    fun removeBoatSpaceReservation(
        @PathVariable reservationId: Int,
        request: HttpServletRequest
    ): ResponseEntity<Void> {
        val citizen = getCitizen(request, jdbi) ?: return ResponseEntity.noContent().build()
        jdbi.inTransactionUnchecked { it.removeBoatSpaceReservation(reservationId, citizen.id) }
        return ResponseEntity.noContent().build()
    }

    @PostMapping("/venepaikka/varaus/{reservationId}/validate")
    fun validateForm(
        @PathVariable reservationId: Int,
        @Valid @ModelAttribute("input") input: ReservationInput,
        bindingResult: BindingResult,
        request: HttpServletRequest,
        model: Model,
    ): String {
        val citizen = getCitizen(request, jdbi) ?: return redirectUrl("/")
        val reservation =
            jdbi.inTransactionUnchecked {
                it.getReservationWithCitizen(reservationId)
            }

        if (reservation == null) return redirectUrl("/")

        return renderBoatSpaceReservationApplication(reservation, citizen, model, input)
    }

    @PostMapping("/venepaikka/varaus/{reservationId}")
    fun reserveBoatSpace(
        @PathVariable reservationId: Int,
        @Valid @ModelAttribute("input") input: ReservationInput,
        bindingResult: BindingResult,
        request: HttpServletRequest,
        model: Model,
    ): String {
        val citizen = getCitizen(request, jdbi) ?: return redirectUrl("/")

        if (bindingResult.hasErrors()) {
            val reservation =
                jdbi.inTransactionUnchecked {
                    it.getReservationWithCitizen(reservationId)
                }

            if (reservation == null) return redirectUrl("/")

            return renderBoatSpaceReservationApplication(reservation, citizen, model, input)
        }

        val boat =
            if (input.boatId == 0 || input.boatId == null) {
                jdbi.inTransactionUnchecked {
                    it.insertBoat(
                        citizen.id,
                        input.boatRegistrationNumber ?: "",
                        input.boatName!!,
                        input.width!!.mToCm(),
                        input.length!!.mToCm(),
                        input.depth!!.mToCm(),
                        input.weight!!,
                        input.boatType!!,
                        input.otherIdentification ?: "",
                        input.extraInformation ?: "",
                        input.ownerShip!!
                    )
                }
            } else {
                jdbi.inTransactionUnchecked {
                    it.updateBoat(
                        Boat(
                            id = input.boatId,
                            citizenId = citizen.id,
                            registrationCode = input.boatRegistrationNumber!!,
                            name = input.boatName!!,
                            widthCm = input.width!!.mToCm(),
                            lengthCm = input.length!!.mToCm(),
                            depthCm = input.depth!!.mToCm(),
                            weightKg = input.weight!!,
                            type = input.boatType!!,
                            otherIdentification = input.otherIdentification ?: "",
                            extraInformation = input.extraInformation ?: "",
                            ownership = input.ownerShip!!
                        )
                    )
                }
            }

        jdbi.inTransactionUnchecked { it.updateCitizen(citizen.id, input.phone!!, input.email!!) }
        jdbi.inTransactionUnchecked { it.updateBoatSpaceReservation(reservationId, boat.id) }
        return "payment"
    }

    @GetMapping("/venepaikka/varaa/{spaceId}")
    fun reserveBoatSpace(
        @PathVariable spaceId: Int,
        @RequestParam boatType: BoatType?,
        @RequestParam width: Double?,
        @RequestParam length: Double?,
        request: HttpServletRequest,
        model: Model
    ): String {
        val citizen = getCitizen(request, jdbi) ?: return redirectUrl("/")

        val existingReservation = jdbi.inTransactionUnchecked { it.getReservationForCitizen(citizen.id) }

        val reservationId =
            if (existingReservation != null) {
                existingReservation.id
            } else {
                jdbi
                    .inTransactionUnchecked {
                        it.insertBoatSpaceReservation(
                            citizen.id,
                            spaceId,
                            LocalDate.now(),
                            LocalDate.now().plusYears(1),
                            ReservationStatus.Info
                        )
                    }.id
            }

        val queryParams = mutableListOf<String>()
        boatType?.let { queryParams.add("boatType=${it.name}") }
        width?.let { queryParams.add("width=$it") }
        length?.let { queryParams.add("length=$it") }

        val queryString = queryParams.joinToString("&")

        return redirectUrl("/kuntalainen/venepaikka/varaus/$reservationId?$queryString")
    }

    fun renderBoatSpaceReservationApplication(
        reservation: ReservationWithDependencies,
        user: Citizen,
        model: Model,
        input: ReservationInput
    ): String {
        val boats =
            jdbi.inTransactionUnchecked {
                it.getBoatsForCitizen(user.id)
            }.map { boat ->
                boat.updateBoatDisplayName(messageUtil)
            }
        // Todo: do not calculate alv here
        val calculatedAlv = reservation.price * 0.1
        val boatSpaceFront =
            object {
                val type = reservation.type
                val section = reservation.section
                val placeNumber = reservation.placeNumber
                val amenity = reservation.amenity
                val widthInMeters = reservation.widthCm.cmToM()
                val lengthInMeters = reservation.lengthCm.cmToM()
                val description: String = reservation.description
                val harbor = reservation.locationName
                val priceTotal = reservation.price
                val priceAlv = calculatedAlv
                val priceWithoutAlv = (reservation.price * 1.0) - calculatedAlv
            }

        model.addAttribute(
            "reservationTimeInSeconds",
            getReservationTimeInSeconds(reservation.created)
        )
        model.addAttribute("boatTypes", listOf("Rowboat", "OutboardMotor", "InboardMotor", "Sailboat", "JetSki"))
        model.addAttribute("ownershipOptions", listOf("Owner", "User", "CoOwner", "FutureOwner"))
        model.addAttribute("input", input)
        model.addAttribute("boatSpace", boatSpaceFront)
        model.addAttribute("boats", boats)
        model.addAttribute("user", user)

        model.addAttribute(
            "showSizeWarning",
            showBoatSizeWarning(
                input.width,
                input.length,
                reservation.amenity,
                reservation.widthCm.cmToM(),
                reservation.lengthCm.cmToM()
            )
        )

        return "boat-space-form"
    }

    private fun showBoatSizeWarning(
        width: Double?,
        length: Double?,
        boatSpaceAmenity: BoatSpaceAmenity,
        spaceWidth: Double,
        spaceLength: Double
    ): Boolean {
        if (boatSpaceAmenity != BoatSpaceAmenity.Buoy && length != null && length > 15.0) {
            return true
        }

        when (boatSpaceAmenity) {
            BoatSpaceAmenity.Buoy, BoatSpaceAmenity.Beam -> {
                val widthTooLarge = width != null && width + 0.4 > spaceWidth
                val lengthTooLarge = length != null && length > spaceLength + 1.0
                return widthTooLarge || lengthTooLarge
            }

            BoatSpaceAmenity.RearBuoy -> {
                val widthTooLarge = width != null && width + 0.5 > spaceWidth
                val lengthTooLarge = length != null && length > spaceLength - 3.0
                return widthTooLarge || lengthTooLarge
            }

            BoatSpaceAmenity.WalkBeam -> {
                val widthTooLarge = width != null && width + 0.75 > spaceWidth
                val lengthTooLarge = length != null && length > spaceLength + 1.0
                return widthTooLarge || lengthTooLarge
            }

            else -> {
                return false
            }
        }
    }

    private fun getReservationTimeInSeconds(reservationCreated: LocalDateTime): Long {
        val reservationTimePassed = Duration.between(reservationCreated, LocalDateTime.now()).toSeconds()
        return (BoatSpaceConfig.sessionTimeInSeconds - reservationTimePassed)
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
    val payload: Array<KClass<out Payload>> = []
)

class BoatRegistrationValidator : ConstraintValidator<ValidBoatRegistration, ReservationInput> {
    override fun isValid(
        value: ReservationInput,
        context: ConstraintValidatorContext
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
    val ownerShip: OwnershipStatus?,
    @field:NotBlank(message = "{validation.required}")
    @field:Email(message = "{validation.email}")
    val email: String?,
    @field:NotBlank(message = "{validation.required}")
    val phone: String?,
    @field:AssertTrue(message = "{validation.certifyInformation}")
    val certifyInformation: Boolean?,
    @field:AssertTrue(message = "{validation.agreeToRules}")
    val agreeToRules: Boolean?
) {
    companion object {
        fun initializeInput(
            boatType: BoatType?,
            width: Double?,
            length: Double?
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
                ownerShip = OwnershipStatus.Owner,
                email = null,
                phone = null,
                agreeToRules = false,
                certifyInformation = false,
            )
    }
}
