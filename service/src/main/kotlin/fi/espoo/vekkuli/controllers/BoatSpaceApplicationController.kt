// SPDX-FileCopyrightText: 2023-2024 City of Espoo
//
// SPDX-License-Identifier: LGPL-2.1-or-later

package fi.espoo.vekkuli.controllers

import fi.espoo.vekkuli.domain.*
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.inTransactionUnchecked
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import java.util.UUID

private data class Boat(
    val type: BoatType,
    val widthInMeters: Double,
    val lengthInMeters: Double,
    val depthInMeters: Double,
    val weightInKg: Int
)

private data class BoatSpace(
    val type: BoatSpaceType,
    val section: String,
    val placeNumber: Int,
    val amenity: BoatSpaceAmenity,
    val widthInMeters: Double,
    val lengthInMeters: Double,
    val description: String,
    val harbor: String,
    val price: Double
)

private data class User(
    val name: String,
    val ssn: String,
    val address: String,
)

data class BoatSpaceReservationRequest(
    val amenity: BoatSpaceAmenity,
    val boatWidthInMeters: Double,
    val boatLengthInMeters: Double,
    val harbor: String,
    val section: String,
    val boatSpaceWidthInMeters: Double,
    val boatSpaceLengthInMeters: Double,
    val boatType: BoatType,
    val boatWeightInKg: Int,
    val boatDepthInMeters: Double
)

@Controller
@RequestMapping("/kuntalainen")
class BoatSpaceApplicationController {
    @Autowired
    lateinit var jdbi: Jdbi

    @GetMapping("/venepaikka")
    fun boatSpaceApplication(
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
        model: Model
    ): String {
        val boatTypes = listOf("Rowboat", "OutboardMotor", "InboardMotor", "Sailboat", "JetSki")
        val boatSpaceReservationRequest =
            BoatSpaceReservationRequest(
                amenity = BoatSpaceAmenity.Buoy,
                boatWidthInMeters = 2.0,
                boatLengthInMeters = 5.0,
                harbor = "Soukka",
                section = "B",
                boatSpaceWidthInMeters = 2.5,
                boatSpaceLengthInMeters = 10.0,
                boatType = BoatType.Sailboat,
                boatWeightInKg = 1500,
                boatDepthInMeters = 1.5
            )
        val boatSpace =
            BoatSpace(
                BoatSpaceType.Slip,
                boatSpaceReservationRequest.section,
                1,
                boatSpaceReservationRequest.amenity,
                boatSpaceReservationRequest.boatSpaceWidthInMeters,
                boatSpaceReservationRequest.boatSpaceLengthInMeters,
                "Description",
                boatSpaceReservationRequest.harbor,
                250.0
            )
        model.addAttribute("boatSpace", boatSpace)
        val boat =
            Boat(
                boatSpaceReservationRequest.boatType,
                boatSpaceReservationRequest.boatSpaceWidthInMeters,
                boatSpaceReservationRequest.boatSpaceLengthInMeters,
                boatSpaceReservationRequest.boatDepthInMeters,
                boatSpaceReservationRequest.boatWeightInKg,
            )
        model.addAttribute("boat", boat)
        model.addAttribute("user", User("Esko Eukkola", "081285-182", "Maalarinkatu 5, 20700, Turku"))

        return "boat-space-reservation-application"
    }

    @GetMapping("/partial/venepaikkatoiveet")
    fun addHarborOption(
        @RequestParam locationId: List<String>,
        model: Model
    ): String {
        model.addAttribute("harbors", locationId)
        model.addAttribute("locations", jdbi.inTransactionUnchecked { it.getLocations() })
        return "fragments/harbor-options :: harborOptions"
    }

    @PostMapping("/venepaikkahakemus")
    fun submitBoatSpaceApplication(
        @RequestParam amenity: BoatSpaceAmenity,
        @RequestParam name: String,
        @RequestParam email: String,
        @RequestParam phone: String,
        @RequestParam boatSpaceType: String,
        @RequestParam boatType: String,
        @RequestParam boatName: String,
        @RequestParam boatRegistrationCode: String,
        @RequestParam boatLengthInMeters: Float,
        @RequestParam boatWidthInMeters: Float,
        @RequestParam weightInKg: Int,
        @RequestParam locationId: List<String>,
        @RequestParam extraInformation: String,
        @RequestParam(required = false) trailerRegistrationCode: String?,
        @RequestParam(required = false) trailerLengthInMeters: Float?,
        @RequestParam(required = false) trailerWidthInMeters: Float?,
    ): String {
        jdbi.inTransactionUnchecked { tx ->
            tx.insertBoatSpaceApplication(
                AddBoatSpaceApplication(
                    type = BoatSpaceType.valueOf(boatSpaceType),
                    boatType = BoatType.valueOf(boatType),
                    amenity = amenity,
                    boatWidthCm = boatWidthInMeters.mToCm(),
                    boatLengthCm = boatLengthInMeters.mToCm(),
                    boatWeightKg = weightInKg,
                    boatRegistrationCode = boatRegistrationCode,
                    information = extraInformation,
                    // TODO use real user identified when authentication is enabled
                    citizenId = UUID.fromString("62d90eed-4ea3-4446-8023-8dad9c01dd34"),
                    trailerRegistrationCode = trailerRegistrationCode,
                    trailerWidthCm = trailerWidthInMeters?.mToCm(),
                    trailerLengthCm = trailerLengthInMeters?.mToCm(),
                    locationWishes =
                        locationId.mapIndexed
                            { index, id ->
                                AddLocationWish(locationId = id.toInt(), priority = index)
                            },
                )
            )
        }
        return "application-received"
    }
}
