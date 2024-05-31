package fi.espoo.vekkuli.controllers

import fi.espoo.vekkuli.config.MessageUtil
import fi.espoo.vekkuli.domain.*
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.inTransactionUnchecked
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
class BoatSpaceApplicationController {
    @Autowired
    lateinit var jdbi: Jdbi

    @Autowired
    lateinit var messageUtil: MessageUtil

    @GetMapping("/venepaikkahakemus")
    fun boatSpaceApplication(model: Model): String {
        val boatTypes = listOf("Rowboat", "OutboardMotor", "InboardMotor", "Sailboat", "JetSki")
        model.addAttribute("boatTypes", boatTypes)
        model.addAttribute("locations", jdbi.inTransactionUnchecked { it.getLocations() })
        return "boat-space-application"
    }

    @GetMapping("/partial/venepaikkatoiveet")
    fun addHarborOption(@RequestParam locationId: List<String>, model: Model): String {
        model.addAttribute("harbors", locationId)
        model.addAttribute("locations", jdbi.inTransactionUnchecked { it.getLocations() })
        return "fragments/harbor-options :: harborOptions"
    }

    @PostMapping("/venepaikkahakemus")
    fun submitBoatSpaceApplication(
        @RequestParam name: String,
        @RequestParam email: String,
        @RequestParam phone: String,
        @RequestParam boatType: String,
        @RequestParam boatName: String,
        @RequestParam registrationCode: String,
        @RequestParam lengthInMeters: Int,
        @RequestParam widthInMeters: Int,
        @RequestParam weightInKg: Int,
        @RequestParam locationId: List<String>,
    ): String {
        jdbi.inTransactionUnchecked { tx ->
            tx.insertBoatSpaceApplication(
                AddBoatSpaceApplication(
                    type = BoatSpaceType.Slip,
                    boatType = BoatType.valueOf(boatType),
                    amenity = BoatSpaceAmenity.RearBuoy,
                    boatWidthCm = widthInMeters,
                    boatLengthCm = lengthInMeters,
                    boatWeightKg = weightInKg,
                    boatRegistrationCode = registrationCode,
                    information = "Hakija: $name, $email, $phone",
                    // TODO use real user identified when authentication is enabled
                    citizenId = 1,
                    locationWishes = locationId.mapIndexed { index, id ->
                        AddLocationWish(locationId = id.toInt(), priority = index)
                    }
                )
            )
        }
        return "application-received"
    }
}
