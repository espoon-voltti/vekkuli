// SPDX-FileCopyrightText: 2023-2024 City of Espoo
//
// SPDX-License-Identifier: LGPL-2.1-or-later
package fi.espoo.vekkuli.controllers

import fi.espoo.vekkuli.config.getAuthenticatedUser
import fi.espoo.vekkuli.domain.*
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.constraints.Min
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.inTransactionUnchecked
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import java.time.LocalDate

@Controller
@RequestMapping("/kuntalainen")
class AvailableBoatSpacesController {
    @Autowired
    lateinit var jdbi: Jdbi

    @RequestMapping("/venepaikat")
    fun availableBoatSpaces(model: Model): String {
        model.addAttribute(
            "amenities",
            BoatSpaceAmenity.entries.map { it.toString() }
        )
        model.addAttribute(
            "boatTypes",
            BoatType.entries.map { it.toString() }
        )
        return "available-boat-spaces"
    }

    @RequestMapping("/partial/vapaat-paikat")
    fun freeSpaces(
        @RequestParam @Min(0) width: Float?,
        @RequestParam @Min(0) length: Float?,
        @RequestParam amenities: List<BoatSpaceAmenity>?,
        @RequestParam boatSpaceType: BoatSpaceType?,
        model: Model
    ): String {
        val harbors =
            jdbi.inTransactionUnchecked {
                it.getUnreservedBoatSpaceOptions(width.mToCm(), length.mToCm(), amenities, boatSpaceType)
            }
        model.addAttribute("harbors", harbors)
        model.addAttribute("harbors", harbors)
        return "boat-space-groups"
    }

    @PostMapping("/venepaikka/varaus")
    fun reserveBoatSpace(
        @RequestParam width: Int,
        @RequestParam length: Int,
        @RequestParam amenity: BoatSpaceAmenity,
        @RequestParam boatSpaceType: BoatSpaceType,
        @RequestParam section: String,
        request: HttpServletRequest,
        model: Model
    ): String {
        val authenticatedUser = request.getAuthenticatedUser()
        val citizen = authenticatedUser?.let { jdbi.inTransactionUnchecked { tx -> tx.getCitizen(it.id) } }
        if (citizen == null) {
            return "redirect:/"
        }
        println(citizen)
        val boatSpace =
            jdbi.inTransactionUnchecked {
                it.getUnreservedBoatSpace(
                    width,
                    length,
                    amenity,
                    boatSpaceType,
                    section
                )
            }
        if (boatSpace == null) {
            return "redirect:/"
        }
        println(boatSpace)
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
        println(reservation)
        val env = System.getenv("VOLTTI_ENV")
        val baseUrl = if (env == "staging") "https://staging.vekkuli.espoon-voltti.fi" else "http://localhost:3000"
        return "redirect:$baseUrl/kuntalainen/venepaikka/varaus/${boatSpace.id}"
    }

    @RequestMapping("/venepaikka/varaus/{boatSpaceId}")
    fun reserVationForm(
        @PathVariable boatSpaceId: Int,
        model: Model
    ): String {
        model.addAttribute("boatSpaceId", boatSpaceId)
        return "boat-space-application"
    }
}

fun Float?.mToCm(): Int? = if (this == null) null else (this * 100F).toInt()
