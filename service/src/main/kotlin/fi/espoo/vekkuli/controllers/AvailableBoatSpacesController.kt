// SPDX-FileCopyrightText: 2023-2024 City of Espoo
//
// SPDX-License-Identifier: LGPL-2.1-or-later
package fi.espoo.vekkuli.controllers

import fi.espoo.vekkuli.config.getAuthenticatedUser
import fi.espoo.vekkuli.domain.*
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
        @RequestParam @Min(0) width: Float?,
        @RequestParam @Min(0) length: Float?,
        @RequestParam amenities: List<BoatSpaceAmenity>?,
        @RequestParam boatSpaceType: BoatSpaceType?,
        @RequestParam harbor: List<String>?,
        model: Model
    ): String {
        val harbors =
            jdbi.inTransactionUnchecked {
                it.getUnreservedBoatSpaceOptions(width.mToCm(), length.mToCm(), amenities, boatSpaceType, harbor?.map { it.toInt() })
            }

        model.addAttribute("harbors", harbors)
        return "boat-space-options"
    }

    @PostMapping("/venepaikka/varaus")
    fun reserveBoatSpace(
        @RequestParam id: Int,
        request: HttpServletRequest,
        model: Model
    ): String {
        val authenticatedUser = request.getAuthenticatedUser()
        val citizen = authenticatedUser?.let { jdbi.inTransactionUnchecked { tx -> tx.getCitizen(it.id) } }
        if (citizen == null) {
            return "redirect:/"
        }
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
        return "redirect:$baseUrl/kuntalainen/venepaikka/varaus/${reservation.id}"
    }

    @RequestMapping("/venepaikka/varaus/{reservationId}")
    fun boatSpaceApplication(
        @PathVariable reservationId: Int,
//        @RequestParam amenity: BoatSpaceAmenity,
//        @RequestParam boatWidthInMeters: Float,
//        @RequestParam boatLengthInMeters: Float,
//        @RequestParam harbor: String,
//        @RequestParam section: String,
//        @RequestParam boatSpaceWidthInMeters: Double,
//        @RequestParam boatSpaceLengthInMeters: Double,
//        @RequestParam boatType: BoatType,
//        @RequestParam boatWeightInKg: Int,
//        @RequestParam boatDepthInMeters: Double,
        request: HttpServletRequest,
        response: HttpServletResponse,
        model: Model
    ): String {
        val authenticatedUser = request.getAuthenticatedUser()
        val user = authenticatedUser?.let { jdbi.inTransactionUnchecked { tx -> tx.getCitizen(it.id) } }
        val reservation =
            jdbi.inTransactionUnchecked {
                it.getReservationWithCitizen(reservationId)
            }

        if (user == null || reservation == null || reservation.citizenId != user.id) {
            throw UnauthorizedException()
        }

        val boatTypes = listOf("Rowboat", "OutboardMotor", "InboardMotor", "Sailboat", "JetSki")
        model.addAttribute("boatTypes", boatTypes)
        val boatSpaceReservationRequest =
            object {
                val amenity = BoatSpaceAmenity.Buoy
                val boatWidthInMeters = 2.0
                val boatLengthInMeters = 5.0
                val harbor = "Soukka"
                val section = "B"
                val boatSpaceWidthInMeters = 2.5
                val boatSpaceLengthInMeters = 10.0
                val boatType = BoatType.Sailboat
                val boatWeightInKg = 1500
                val boatDepthInMeters = 1.5
            }
        val boatSpace =
            object {
                val type = BoatSpaceType.Slip
                val section = boatSpaceReservationRequest.section
                val placeNumber = 1
                val amenity = boatSpaceReservationRequest.amenity
                val widthInMeters = boatSpaceReservationRequest.boatSpaceWidthInMeters
                val lengthInMeters = boatSpaceReservationRequest.boatSpaceLengthInMeters
                val description = "Description"
                val harbor = boatSpaceReservationRequest.harbor
                val priceWithoutAlv = 250.0
                val priceAlv = 25
                val priceTotal = 275.0
            }
        model.addAttribute("boatSpace", boatSpace)
        val boat =
            object {
                val type = boatSpaceReservationRequest.boatType
                val widthInMeters = boatSpaceReservationRequest.boatSpaceWidthInMeters
                val lengthInMeters = boatSpaceReservationRequest.boatSpaceLengthInMeters
                val depthInMeters = boatSpaceReservationRequest.boatDepthInMeters
                val weightInKg = boatSpaceReservationRequest.boatWeightInKg
            }
        model.addAttribute("boat", boat)
        model.addAttribute(
            "user",
            object {
                val name = "${user.firstName} ${user.lastName}"
                val ssn = user.nationalId
                val address = "Miestentie 2 A 23"
                val postalCode = "02150"
                val municipal = "Espoo"
            }
        )

        return "boat-space-reservation-application"
    }
}

fun Float?.mToCm(): Int? = if (this == null) null else (this * 100F).toInt()

@ResponseStatus(HttpStatus.UNAUTHORIZED)
internal class UnauthorizedException : RuntimeException()
