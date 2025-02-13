package fi.espoo.vekkuli.boatSpace.boatSpaceList

import fi.espoo.vekkuli.common.getAppUser
import fi.espoo.vekkuli.config.audit
import fi.espoo.vekkuli.config.getAuthenticatedUser
import fi.espoo.vekkuli.controllers.Utils.Companion.getServiceUrl
import fi.espoo.vekkuli.domain.*
import fi.espoo.vekkuli.service.BoatReservationService
import fi.espoo.vekkuli.service.BoatSpaceService
import fi.espoo.vekkuli.utils.formatDecimal
import fi.espoo.vekkuli.utils.formatInt
import fi.espoo.vekkuli.utils.intToDecimal
import fi.espoo.vekkuli.views.employee.EmployeeLayout
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.servlet.http.HttpServletRequest
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.inTransactionUnchecked
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import java.net.URI

data class BoatSpaceListRow(
    val id: Int,
    // @TODO: should come from the DB
    val active: Boolean = true,
    val type: BoatSpaceType,
    val section: String,
    val placeNumber: Int,
    val amenity: BoatSpaceAmenity,
    val widthCm: Int,
    val lengthCm: Int,
    val description: String,
    val excludedBoatTypes: List<BoatType>? = null,
    val locationName: String?,
    val locationAddress: String?,
    val priceCents: Int,
    val priceClass: String? = null,
    val reserved: Boolean = false,
    val validity: ReservationValidity? = null
) {
    val priceInEuro: String
        get() = formatInt(priceCents)

    val widthInMeter: String
        get() = formatDecimal(intToDecimal(widthCm))
    val lengthInMeter: String
        get() = formatDecimal(intToDecimal(lengthCm))
}

@Controller
@RequestMapping("/virkailija/venepaikat")
class BoatSpaceListController {
    @Autowired
    private lateinit var boatSpaceService: BoatSpaceService

    @Autowired
    lateinit var jdbi: Jdbi

    @Autowired
    lateinit var reservationService: BoatReservationService

    @Autowired
    lateinit var boatSpaceList: BoatSpaceList

    @Autowired
    lateinit var layout: EmployeeLayout

    private val logger = KotlinLogging.logger {}

    @GetMapping("/selaa")
    @ResponseBody
    fun boatSpaceSearchPage(
        request: HttpServletRequest,
        model: Model
    ): ResponseEntity<String> {
        request.getAuthenticatedUser()?.let {
            logger.audit(it, "EMPLOYEE_RESERVATION_SEARCH")
        }
        val boatSpaces =
            boatSpaceService.getBoatSpaces()

        val authenticatedUser = request.getAuthenticatedUser()
        if (authenticatedUser == null) {
            val headers = org.springframework.http.HttpHeaders()
            headers.location = URI(getServiceUrl("/virkailija"))
            return ResponseEntity(headers, HttpStatus.FOUND)
        }

        val employee =
            authenticatedUser.let {
                jdbi.inTransactionUnchecked { tx ->
                    tx.getAppUser(authenticatedUser.id)
                }
            }
        if (employee == null) {
            val headers = org.springframework.http.HttpHeaders()
            headers.location = URI(getServiceUrl("/virkailija"))
            return ResponseEntity(headers, HttpStatus.FOUND)
        }

        return ResponseEntity.ok(
            layout.render(
                true,
                employee.fullName,
                boatSpaceList.render(boatSpaces)
            )
        )
    }
}
