// SPDX-FileCopyrightText: 2023-2024 City of Espoo
//
// SPDX-License-Identifier: LGPL-2.1-or-later
package fi.espoo.vekkuli.controllers

import fi.espoo.vekkuli.config.getAuthenticatedUser
import fi.espoo.vekkuli.domain.*
import fi.espoo.vekkuli.utils.cmToM
import fi.espoo.vekkuli.utils.mToCm
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.constraints.Min
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.inTransactionUnchecked
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

data class BoatFilter(
    val width: Double?,
    val length: Double?,
    val type: BoatType?
)

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
        @RequestParam boatType: BoatType,
        @RequestParam width: Double,
        @RequestParam length: Double,
        @RequestParam depth: Double,
        @RequestParam weight: Int,
        @RequestParam boatRegistrationNumber: String,
        @RequestParam boatName: String,
        @RequestParam otherIdentification: String,
        @RequestParam extraInformation: String,
        @RequestParam ownerShip: OwnershipStatus,
        @RequestParam email: String,
        @RequestParam phone: String,
        request: HttpServletRequest,
        model: Model
    ): String {
        val citizen = getCitizen(request) ?: return "redirect:/"
        val boat =
            jdbi.inTransactionUnchecked {
                it.insertBoat(
                    citizen.id,
                    boatRegistrationNumber,
                    boatName,
                    width.mToCm(),
                    length.mToCm(),
                    depth.mToCm(),
                    weight,
                    boatType,
                    otherIdentification,
                    extraInformation,
                    ownerShip
                )
            }
        jdbi.inTransactionUnchecked { it.updatetBoatSpaceReservation(reservationId, boat.id) }
        return "payment"
    }

    @PostMapping("/venepaikka/varaus")
    fun reserveBoatSpace(
        @RequestParam id: Int,
        @RequestParam(required = false) boatType: BoatType?,
        @RequestParam(required = false) width: Double?,
        @RequestParam(required = false) length: Double?,
        request: HttpServletRequest,
        model: Model
    ): String {
        val citizen = getCitizen(request) ?: return "redirect:/"
        val boatSpace =
            jdbi.inTransactionUnchecked {
                it.getUnreservedBoatSpace(id)
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
        val env = System.getenv("VOLTTI_ENV")
        val baseUrl = if (env == "staging") "https://staging.varaukset.espoo.fi" else "http://localhost:3000"
        // Construct the query parameters
        val queryParams = mutableListOf<String>()
        boatType?.let { queryParams.add("boatType=${it.name}") }
        width?.let { queryParams.add("width=$it") }
        length?.let { queryParams.add("length=$it") }

        // Join the query parameters with '&'
        val queryString = queryParams.joinToString("&")

        // Construct the redirect URL
        val redirectUrl = "$baseUrl/kuntalainen/venepaikka/varaus/${reservation.id}"
        val fullUrl = if (queryString.isNotEmpty()) "$redirectUrl?$queryString" else redirectUrl

        return "redirect:$fullUrl"
    }

    @RequestMapping("/venepaikka/varaus/{reservationId}")
    fun boatSpaceApplication(
        @PathVariable reservationId: Int,
        @RequestParam(required = false) boatType: BoatType?,
        @RequestParam(required = false) width: Double?,
        @RequestParam(required = false) length: Double?,
        request: HttpServletRequest,
        response: HttpServletResponse,
        model: Model
    ): String {
        val user = getCitizen(request)
        val reservation =
            jdbi.inTransactionUnchecked {
                it.getReservationWithCitizen(reservationId)
            }

        if (reservation == null) return "redirect:/"
        val boat = BoatFilter(width, length, boatType)

        if (user == null || reservation == null || reservation.citizenId != user.id) {
            throw UnauthorizedException()
        }
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

                val priceWithoutAlv = (reservation.price * 1.0) - calculatedAlv
                val priceAlv = calculatedAlv
                val priceTotal = reservation.price
            }

        val boatTypes = listOf("Rowboat", "OutboardMotor", "InboardMotor", "Sailboat", "JetSki")
        model.addAttribute("boatTypes", boatTypes)
        model.addAttribute("boatSpace", boatSpaceFront)
        model.addAttribute("boat", boat)
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
