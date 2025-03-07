package fi.espoo.vekkuli.boatSpace.boatSpaceList

import fi.espoo.vekkuli.config.audit
import fi.espoo.vekkuli.config.ensureEmployeeId
import fi.espoo.vekkuli.config.getAuthenticatedUser
import fi.espoo.vekkuli.domain.*
import fi.espoo.vekkuli.service.*
import fi.espoo.vekkuli.utils.decimalToInt
import fi.espoo.vekkuli.utils.formatDecimal
import fi.espoo.vekkuli.utils.formatInt
import fi.espoo.vekkuli.utils.intToDecimal
import fi.espoo.vekkuli.views.employee.EmployeeLayout
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.servlet.http.HttpServletRequest
import org.jdbi.v3.core.Jdbi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import java.util.*

data class BoatSpaceListRow(
    val id: Int,
    val isActive: Boolean,
    val type: BoatSpaceType,
    val place: String,
    val amenity: BoatSpaceAmenity,
    private val widthCm: Int,
    private val lengthCm: Int,
    val description: String,
    val excludedBoatTypes: List<BoatType>? = null,
    val locationName: String?,
    val locationAddress: String?,
    private val priceCents: Int,
    val priceClass: String? = null,
    val reserverName: String? = null,
    val reserverId: UUID?,
    val reserverType: ReserverType?,
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
    private lateinit var priceService: PriceService

    @Autowired
    private lateinit var paymentService: PaymentService

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
        @ModelAttribute params: BoatSpaceListParams,
        model: Model
    ): ResponseEntity<String> {
        request.getAuthenticatedUser()?.let {
            logger.audit(it, "EMPLOYEE_BOAT_SPACE_SEARCH")
        }

        request.ensureEmployeeId()
        val harbors = reservationService.getHarbors()
        val boatSpaceTypes = BoatSpaceType.entries.toList()
        val boatSpaces =
            boatSpaceService.getBoatSpacesFiltered(params)
        val sections = boatSpaceService.getSections()
        val priceClasses = priceService.getPriceClasses()
        return ResponseEntity.ok(
            layout.render(
                true,
                request.requestURI,
                boatSpaceList.render(boatSpaces, params, harbors, priceClasses, boatSpaceTypes, actualAmenities, sections, params.edit)
            )
        )
    }

    @PostMapping("/selaa/muokkaa")
    @ResponseBody
    fun boatSpaceEdit(
        request: HttpServletRequest,
        @ModelAttribute params: BoatSpaceListEditParams,
        model: Model
    ) {
        request.getAuthenticatedUser()?.let {
            logger.audit(it, "EMPLOYEE_BOAT_SPACE_EDIT")
        }

        request.ensureEmployeeId()
        boatSpaceService.editBoatSpaces(
            params.boatSpaceIds,
            EditBoatSpaceParams(
                params.harbor,
                params.boatSpaceType,
                if (params.section.isNullOrEmpty()) null else params.section,
                params.placeNumber,
                params.boatSpaceAmenity,
                decimalToInt(params.width),
                decimalToInt(params.length),
                params.payment,
                params.boatSpaceState == BoatSpaceState.Active
            )
        )
    }
}
