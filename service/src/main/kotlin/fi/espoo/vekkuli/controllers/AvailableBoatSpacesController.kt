// SPDX-FileCopyrightText: 2023-2024 City of Espoo
//
// SPDX-License-Identifier: LGPL-2.1-or-later
package fi.espoo.vekkuli.controllers

import fi.espoo.vekkuli.config.getAuthenticatedUser
import fi.espoo.vekkuli.domain.*
import fi.espoo.vekkuli.utils.cmToM
import fi.espoo.vekkuli.utils.mToCm
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import jakarta.validation.constraints.AssertTrue
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.inTransactionUnchecked
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

data class BoatFilter(
    val width: Double?,
    val length: Double?,
    val type: BoatType?
)

data class ReservationInput(
    @field:NotNull(message = "Varaus ID tarvitaan")
    private val reservationId: Int?,
    @field:NotNull(message = "Venetyyppi tarvitaan")
    val boatType: BoatType?,
    @field:NotNull(message = "Veneen leveys tarvitaan")
    val width: Double?,
    @field:NotNull(message = "Veneen pituus tarvitaan")
    val length: Double?,
    @field:NotNull(message = "{validation.age.NotNull}")
    val depth: Double?,
    @field:NotNull(message = "{validation.age.NotNull}")
    val weight: Int?,
    val noRegistrationNumber: Boolean?,
    val boatRegistrationNumber: String?,
    val boatName: String?,
    @field:NotNull(message = "{validation.age.NotNull}")
    val otherIdentification: String?,
    val extraInformation: String?,
    @field:NotNull(message = "{validation.age.NotNull}")
    val ownerShip: OwnershipStatus?,
    @field:NotBlank(message = "{validation.age.NotNull}")
    val email: String?,
    @field:NotBlank(message = "{validation.age.NotNull}")
    val phone: String?,
    val certifyInformation: Boolean?,
    val agreeToRules: Boolean?,
) {
    @AssertTrue
    private fun isOk(): Boolean {
        val res = noRegistrationNumber == true || !boatRegistrationNumber.isNullOrBlank()
        return res
    }

    companion object {
        fun emptyInput(): ReservationInput {
            return ReservationInput(
                reservationId = null,
                boatType = BoatType.InboardMotor,
                width = 1.2,
                length = 3.0,
                depth = 0.5,
                weight = 100,
                noRegistrationNumber = false,
                boatRegistrationNumber = "A12345",
                boatName = "Mun vene",
                otherIdentification = "Tää on kiva",
                extraInformation = "joo",
                ownerShip = OwnershipStatus.Owner,
                email = "foo@bar.com",
                phone = "1234567890",
                agreeToRules = false,
                certifyInformation = false,
            )
        }
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
                it.getUnreservedBoatSpaceOptions(width?.mToCm(), length?.mToCm(), amenities, boatSpaceType, harbor?.map { it.toInt() })
            }

        model.addAttribute("harbors", harbors)
        model.addAttribute("boat", BoatFilter(width, length, boatType))
        return "boat-space-options"
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
        request: HttpServletRequest,
        model: Model
    ): String {
        val citizen = getCitizen(request) ?: return "redirect:/"
        val boatSpace =
            jdbi.inTransactionUnchecked {
                it.getUnreservedBoatSpace(spaceId)
            }

        if (boatSpace == null) {
            return "redirect:/"
        }

        val reservation =
            jdbi.inTransactionUnchecked {
                it.insertBoatSpaceReservation(
                    citizen.id,
                    boatSpace.id,
                    LocalDate.now(),
                    LocalDate.now().plusYears(1),
                    ReservationStatus.Info
                )
            }

        val baseUrl = getBaseUrl()

        // Construct the redirect URL
        val redirectUrl = "$baseUrl/kuntalainen/venepaikka/varaus/${reservation.id}"
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

        model.addAttribute("boatTypes", listOf("Unknown", "Rowboat", "OutboardMotor", "InboardMotor", "Sailboat", "JetSki"))
        model.addAttribute("ownershipOptions", listOf("Owner", "User", "CoOwner", "FutureOwner"))
        model.addAttribute("input", input)
        model.addAttribute("boatSpace", boatSpaceFront)
        model.addAttribute(
            "user",
            mockedUser
        )
        return "boat-space-reservation-application"
    }

    @RequestMapping("/venepaikka/varaus/{reservationId}")
    fun boatSpaceApplication(
        @PathVariable reservationId: Int,
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

        return renderBoatSpaceReservationApplication(reservation, user, model, ReservationInput.emptyInput())
    }

    private fun getCitizen(request: HttpServletRequest): Citizen? {
        val authenticatedUser = request.getAuthenticatedUser()
        val citizen = authenticatedUser?.let { jdbi.inTransactionUnchecked { tx -> tx.getCitizen(it.id) } }
        return citizen
    }
}

@ResponseStatus(HttpStatus.UNAUTHORIZED)
internal class UnauthorizedException : RuntimeException()
