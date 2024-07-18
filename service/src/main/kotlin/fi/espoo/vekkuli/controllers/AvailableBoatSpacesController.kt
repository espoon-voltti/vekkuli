// SPDX-FileCopyrightText: 2023-2024 City of Espoo
//
// SPDX-License-Identifier: LGPL-2.1-or-later
package fi.espoo.vekkuli.controllers

import fi.espoo.vekkuli.config.BoatSpaceConfig
import fi.espoo.vekkuli.config.getAuthenticatedUser
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
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.reflect.KClass

data class BoatFilter(
    val width: Double?,
    val length: Double?,
    val type: BoatType?
)

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
        if (value.noRegistrationNumber != true && value.boatRegistrationNumber.isNullOrBlank()) {
            context.disableDefaultConstraintViolation()
            context
                .buildConstraintViolationWithTemplate(context.defaultConstraintMessageTemplate)
                .addPropertyNode("boatRegistrationNumber")
                .addConstraintViolation()
            return false
        }
        return true
    }
}

@ValidBoatRegistration
data class ReservationInput(
    @field:NotNull(message = "{validation.required}")
    private val reservationId: Int?,
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
    @field:NotNull(message = "{validation.required}")
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
    val agreeToRules: Boolean?,
) {
    companion object {
        fun initializeInput(
            boatType: BoatType?,
            width: Double?,
            length: Double?
        ): ReservationInput =
            ReservationInput(
                reservationId = null,
                boatType = boatType ?: BoatType.OutboardMotor,
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

@Controller
@RequestMapping("/kuntalainen")
class AvailableBoatSpacesController {
    @Autowired
    lateinit var jdbi: Jdbi

    @RequestMapping("/venepaikat")
    fun availableBoatSpaces(model: Model): String {
        val locations =
            jdbi.inTransactionUnchecked { tx ->
                tx.getLocations()
            }
        model.addAttribute(
            "amenities",
            BoatSpaceAmenity.entries.map { it.toString() }
        )
        model.addAttribute(
            "boatTypes",
            BoatType.entries.map { it.toString() }
        )
        model.addAttribute("locations", locations)

        return "available-boat-spaces"
    }

    @RequestMapping("/partial/vapaat-paikat")
    fun freeSpaces(
        @RequestParam(required = false) boatType: BoatType?,
        @RequestParam @Min(0) width: Double?,
        @RequestParam @Min(0) length: Double?,
        @RequestParam amenities: List<BoatSpaceAmenity>?,
        @RequestParam boatSpaceType: BoatSpaceType?,
        @RequestParam harbor: List<String>?,
        model: Model
    ): String {
        val harbors =
            jdbi.inTransactionUnchecked {
                it.getUnreservedBoatSpaceOptions(
                    BoatSpaceFilter(
                        boatType,
                        width?.mToCm(),
                        length?.mToCm(),
                        amenities,
                        boatSpaceType,
                        harbor?.map { it.toInt() }
                    )
                )
            }

        model.addAttribute("harbors", harbors)
        model.addAttribute("boat", BoatFilter(width, length, boatType))
        return "boat-space-options"
    }

    private fun getReservationTimeInSeconds(reservationCreated: LocalDateTime): Long {
        val reservationTimePassed = Duration.between(reservationCreated, LocalDateTime.now()).toSeconds()
        return (BoatSpaceConfig.sessionTimeInSeconds - reservationTimePassed)
    }

    @RequestMapping("/venepaikka/varaus/{reservationId}")
    fun boatSpaceApplication(
        @PathVariable reservationId: Int,
        @RequestParam boatType: BoatType?,
        @RequestParam width: Double?,
        @RequestParam length: Double?,
        request: HttpServletRequest,
        model: Model
    ): String {
        val user = getCitizen(request)
        val reservation =
            jdbi.inTransactionUnchecked {
                it.getReservationWithCitizen(reservationId)
            }

        if (reservation == null) return "redirect:/"

        if (user == null || reservation.citizenId != user.id) {
            throw UnauthorizedException()
        }
        return renderBoatSpaceReservationApplication(reservation, user, model, ReservationInput.initializeInput(boatType, width, length))
    }

    @PostMapping("/venepaikka/varaus/{reservationId}")
    fun reserveBoatSpace(
        @PathVariable reservationId: Int,
        @Valid @ModelAttribute("input") input: ReservationInput,
        bindingResult: BindingResult,
        request: HttpServletRequest,
        model: Model
    ): String {
        val citizen = getCitizen(request) ?: return "redirect:/"

        if (bindingResult.hasErrors()) {
            val reservation =
                jdbi.inTransactionUnchecked {
                    it.getReservationWithCitizen(reservationId)
                }

            if (reservation == null) return "redirect:/"

            return renderBoatSpaceReservationApplication(reservation, citizen, model, input)
        }

        val boat =
            jdbi.inTransactionUnchecked {
                it.insertBoat(
                    citizen.id,
                    input.boatRegistrationNumber!!,
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

        jdbi.inTransactionUnchecked { it.updateCitizen(citizen.id, input.phone!!, input.email!!) }
        jdbi.inTransactionUnchecked { it.updateBoatSpaceReservation(reservationId, boat.id) }
        return "payment"
    }

    @PostMapping("/venepaikka/varaus")
    fun reserveBoatSpace(
        @RequestParam spaceId: Int,
        @RequestParam boatType: BoatType?,
        @RequestParam width: Double?,
        @RequestParam length: Double?,
        request: HttpServletRequest,
        model: Model
    ): String {
        val citizen = getCitizen(request) ?: return "redirect:/"

        val reservation =
            jdbi.inTransactionUnchecked {
                it.insertBoatSpaceReservation(
                    citizen.id,
                    spaceId,
                    LocalDate.now(),
                    LocalDate.now().plusYears(1),
                    ReservationStatus.Info
                )
            }

        val baseUrl = getBaseUrl()

        val queryParams = mutableListOf<String>()
        boatType?.let { queryParams.add("boatType=${it.name}") }
        width?.let { queryParams.add("width=$it") }
        length?.let { queryParams.add("length=$it") }

        // Join the query parameters with '&'
        val queryString = queryParams.joinToString("&")

        // Construct the redirect URL
        val redirectUrl = "$baseUrl/kuntalainen/venepaikka/varaus/${reservation.id}?$queryString"
        return "redirect:$redirectUrl"
    }

    fun getBaseUrl(): String {
        val env = System.getenv("VOLTTI_ENV")
        val runningInDocker = System.getenv("E2E_ENV") == "docker"
        when (env) {
            "production" -> return "https://varaukset.espoo.fi"
            "staging" -> return "https://staging.varaukset.espoo.fi"
            else -> {
                if (runningInDocker) {
                    return "http://api-gateway:3000"
                } else {
                    return "http://localhost:3000"
                }
            }
        }
    }

    fun renderBoatSpaceReservationApplication(
        reservation: ReservationWithDependencies,
        user: Citizen,
        model: Model,
        input: ReservationInput
    ): String {
        val mockedUser =
            // Todo: fetch real data here
            user.copy(
                address = "Miestentie 2 A 23",
                postalCode = "02150",
                municipality = "Espoo"
            )

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
        model.addAttribute(
            "user",
            mockedUser
        )
        return "boat-space-reservation-application"
    }

    private fun getCitizen(request: HttpServletRequest): Citizen? {
        val authenticatedUser = request.getAuthenticatedUser()
        val citizen = authenticatedUser?.let { jdbi.inTransactionUnchecked { tx -> tx.getCitizen(it.id) } }
        return citizen
    }
}

@ResponseStatus(HttpStatus.UNAUTHORIZED)
internal class UnauthorizedException : RuntimeException()
