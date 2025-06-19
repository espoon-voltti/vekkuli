package fi.espoo.vekkuli.boatSpace.boatSpaceList

import fi.espoo.vekkuli.boatSpace.boatSpaceDetails.BoatSpaceDetails
import fi.espoo.vekkuli.boatSpace.boatSpaceList.components.DeletionError
import fi.espoo.vekkuli.boatSpace.boatSpaceList.components.FailModalView
import fi.espoo.vekkuli.boatSpace.boatSpaceList.components.SuccessModalView
import fi.espoo.vekkuli.boatSpace.boatSpaceList.partials.BoatSpaceListRowsPartial
import fi.espoo.vekkuli.config.*
import fi.espoo.vekkuli.domain.*
import fi.espoo.vekkuli.service.*
import fi.espoo.vekkuli.utils.decimalToInt
import fi.espoo.vekkuli.utils.formatDecimal
import fi.espoo.vekkuli.utils.formatInt
import fi.espoo.vekkuli.utils.intToDecimal
import fi.espoo.vekkuli.views.employee.EmployeeLayout
import fi.espoo.vekkuli.views.employee.components.FilterOption
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.servlet.http.HttpServletRequest
import org.jdbi.v3.core.Jdbi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
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
    private lateinit var messageUtil: MessageUtil

    @Autowired
    private lateinit var failModalView: FailModalView

    @Autowired
    private lateinit var successModalView: SuccessModalView

    @Autowired
    private lateinit var priceService: PriceService

    @Autowired
    private lateinit var boatSpaceService: BoatSpaceService

    @Autowired
    lateinit var jdbi: Jdbi

    @Autowired
    lateinit var reservationService: BoatReservationService

    @Autowired
    lateinit var boatSpaceList: BoatSpaceList

    @Autowired
    lateinit var boatSpaceDetails: BoatSpaceDetails

    @Autowired
    lateinit var layout: EmployeeLayout

    @Autowired
    lateinit var boatSpaceListRowsPartial: BoatSpaceListRowsPartial

    private val logger = KotlinLogging.logger {}

    @GetMapping("/selaa")
    @ResponseBody
    fun boatSpaceSearchPage(
        request: HttpServletRequest,
        @ModelAttribute params: BoatSpaceListParams
    ): ResponseEntity<String> {
        request.getAuthenticatedUser()?.let {
            logger.audit(it, "EMPLOYEE_BOAT_SPACE_SEARCH")
        }

        request.ensureEmployeeId()

        return ResponseEntity.ok(
            layout.render(
                true,
                request.requestURI,
                getBoatSpaceList(params)
            )
        )
    }

    private fun getBoatSpaceList(
        params: BoatSpaceListParams,
        initialPageSize: Int = 100,
        loadMorePageSize: Int = 100,
    ): String {
        val harbors = reservationService.getHarbors()
        val boatSpaceTypes = BoatSpaceType.entries.toList()

        val boatSpaces =
            boatSpaceService.getBoatSpacesFiltered(params, params.paginationStart, initialPageSize)

        val sectionOptions = boatSpaceService.getSections().map { FilterOption(it, it) }
        val priceClasses = priceService.getPriceClasses()

        val boatWidthOptions =
            boatSpaceService
                .getBoatWidthOptions(
                    params
                ).map { FilterOption(it.toString(), formatDecimal(intToDecimal(it))) }
        val boatLengthOptions =
            boatSpaceService
                .getBoatLengthOptions(
                    params
                ).map { FilterOption(it.toString(), formatDecimal(intToDecimal(it))) }
        val bodyContent =
            boatSpaceList.render(
                boatSpaces,
                params,
                harbors,
                priceClasses,
                boatSpaceTypes,
                actualAmenities,
                sectionOptions,
                boatWidthOptions,
                boatLengthOptions,
                loadMorePageSize
            )
        return bodyContent
    }

    @PostMapping("/muokkaa")
    @ResponseBody
    fun boatSpaceEdit(
        request: HttpServletRequest,
        @ModelAttribute editParams: BoatSpaceListEditParams,
        @ModelAttribute filterParams: BoatSpaceListParams,
    ): ResponseEntity<String> {
        request.getAuthenticatedUser()?.let {
            logger.audit(it, "EMPLOYEE_BOAT_SPACE_EDIT")
        }

        request.ensureEmployeeId()
        boatSpaceService.editBoatSpaces(
            editParams.boatSpaceIds,
            EditBoatSpaceParams(
                editParams.harbor,
                editParams.boatSpaceType,
                if (editParams.section.isNullOrEmpty()) null else editParams.section,
                editParams.placeNumber,
                editParams.boatSpaceAmenity,
                decimalToInt(editParams.width),
                decimalToInt(editParams.length),
                editParams.payment,
                if (editParams.boatSpaceState == null) null else editParams.boatSpaceState == BoatSpaceState.Active
            )
        )

        return ResponseEntity.ok(
            layout.render(
                true,
                request.requestURI,
                getBoatSpaceList(filterParams)
            )
        )
    }

    @GetMapping("/selaa/rivit")
    @ResponseBody
    fun boatSpaceRows(
        request: HttpServletRequest,
        @ModelAttribute params: BoatSpaceListParams
    ): ResponseEntity<String> {
        request.getAuthenticatedUser()?.let {
            logger.audit(it, "EMPLOYEE_BOAT_SPACE_SEARCH_ROWS")
        }

        request.ensureEmployeeId()

        val boatSpaces =
            boatSpaceService.getBoatSpacesFiltered(params)

        return ResponseEntity.ok(boatSpaceListRowsPartial.render(boatSpaces))
    }

    @PostMapping("/lisaa")
    @ResponseBody
    fun boatSpaceAdd(
        request: HttpServletRequest,
        @ModelAttribute params: BoatSpaceListAddParams
    ): ResponseEntity<String> {
        request.getAuthenticatedUser()?.let {
            logger.audit(it, "EMPLOYEE_BOAT_SPACE_ADD")
        }

        request.ensureEmployeeId()
        try {
            boatSpaceService.createBoatSpace(
                CreateBoatSpaceParams(
                    params.harborCreation,
                    params.boatSpaceTypeCreation,
                    params.sectionCreation,
                    params.placeNumberCreation,
                    params.boatSpaceAmenityCreation,
                    decimalToInt(params.widthCreation),
                    decimalToInt(params.lengthCreation),
                    params.paymentCreation,
                    params.boatSpaceStateCreation == BoatSpaceState.Active
                )
            )
            return ResponseEntity.ok(
                successModalView.creationModal()
            )
        } catch (e: Exception) {
            logger.error { "Boat space creation failed: ${e.message}" }
            return ResponseEntity.ok(
                failModalView.creationModal()
            )
        }
    }

    @PostMapping("/poista")
    @ResponseBody
    fun boatSpaceDelete(
        request: HttpServletRequest,
        @RequestParam boatSpaceIds: List<Int> = emptyList(),
    ): ResponseEntity<String> {
        request.getAuthenticatedUser()?.let {
            logger.audit(
                it,
                "EMPLOYEE_BOAT_SPACE_DELETE",
                mapOf(
                    "targetIds" to boatSpaceIds.toString()
                )
            )
        }

        request.ensureEmployeeId()
        try {
            boatSpaceService.deleteBoatSpaces(boatSpaceIds)
            return ResponseEntity.ok(
                successModalView.deletionModal()
            )
        } catch (e: IllegalArgumentException) {
            logger.error { "Boat space deletion failed. Boat space has reservations. Message: ${e.message}" }
            return ResponseEntity.ok(
                failModalView.deletionModal(DeletionError.BOAT_SPACE_HAS_RESERVATIONS)
            )
        } catch (e: Exception) {
            logger.error { "Boat space deletion failed: ${e.message}" }
            return ResponseEntity.ok(
                failModalView.deletionModal()
            )
        }
    }

    @GetMapping("/{boatSpaceId}")
    @ResponseBody
    fun boatSpaceDetails(
        @PathVariable boatSpaceId: Int,
        request: HttpServletRequest,
    ): ResponseEntity<String> {
        request.getAuthenticatedUser()?.let {
            logger.audit(
                it,
                "EMPLOYEE_BOAT_SPACE_DETAILS",
                mapOf(
                    "targetId" to boatSpaceId.toString()
                )
            )
        }
        request.ensureEmployeeId()
        val boatSpace = boatSpaceService.getBoatSpace(boatSpaceId) ?: return ResponseEntity.notFound().build()

        fun padPlaceNumberWitZeros(boatSpace: BoatSpace) = (boatSpace.placeNumber).toString().padStart(3, '0')
        val boatSpaceName =
            "${messageUtil.getMessage(
                "boatSpaces.typeOption.${boatSpace.type}"
            )}: ${boatSpace.locationName} ${boatSpace.section}${padPlaceNumberWitZeros(boatSpace)}"

        val boatSpaceHistory = boatSpaceService.getBoatSpaceHistory(boatSpaceId)

        return ResponseEntity.ok(
            layout.render(
                true,
                request.requestURI,
                boatSpaceDetails.render(boatSpaceName, boatSpaceHistory)
            )
        )
    }
}
