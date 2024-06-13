// SPDX-FileCopyrightText: 2023-2024 City of Espoo
//
// SPDX-License-Identifier: LGPL-2.1-or-later
package fi.espoo.vekkuli.controllers

import fi.espoo.vekkuli.domain.BoatSpaceAmenity
import fi.espoo.vekkuli.domain.getBoatSpaceGroups
import jakarta.validation.constraints.Min
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.inTransactionUnchecked
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequestMapping("/kuntalainen")
class AvailableBoatSpacesController {
    @Autowired
    lateinit var jdbi: Jdbi

    @RequestMapping("/venepaikat")
    fun availableBoatSpaces(model: Model): String {
        model.addAttribute(
            "amenities",
            listOf(
                BoatSpaceAmenity.None.toString(),
                BoatSpaceAmenity.Buoy.toString(),
                BoatSpaceAmenity.RearBuoy.toString(),
                BoatSpaceAmenity.Beam.toString(),
                BoatSpaceAmenity.WalkBeam.toString()
            )
        )
        return "available-boat-spaces"
    }

    @RequestMapping("/partial/vapaat-paikat")
    fun freeSpaces(
        @RequestParam @Min(0) width: Float?,
        @RequestParam @Min(0) length: Float?,
        @RequestParam amenity: BoatSpaceAmenity?,
        model: Model
    ): String {
        val results =
            jdbi.inTransactionUnchecked {
                it.getBoatSpaceGroups(width.mToCm(), length.mToCm(), amenity)
            }
        println(results)
        model.addAttribute("results", results)
        return "boat-space-groups"
    }
}

fun Float?.mToCm(): Int? = if (this == null) null else (this * 100F).toInt()
