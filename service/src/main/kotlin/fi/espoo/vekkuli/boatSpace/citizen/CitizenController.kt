package fi.espoo.vekkuli.boatSpace.citizen

import fi.espoo.vekkuli.boatSpace.citizenBoatSpaceReservation.ReservationResponse
import fi.espoo.vekkuli.boatSpace.citizenBoatSpaceReservation.ReservationResponseMapper
import fi.espoo.vekkuli.boatSpace.citizenBoatSpaceReservation.ReservationService
import fi.espoo.vekkuli.common.Forbidden
import fi.espoo.vekkuli.config.audit
import fi.espoo.vekkuli.config.ensureCitizenId
import fi.espoo.vekkuli.config.getAuthenticatedUser
import fi.espoo.vekkuli.controllers.Utils.Companion.getCitizen
import fi.espoo.vekkuli.service.BoatService
import fi.espoo.vekkuli.service.OrganizationService
import fi.espoo.vekkuli.service.PermissionService
import fi.espoo.vekkuli.service.ReserverService
import jakarta.servlet.http.HttpServletRequest
import mu.KotlinLogging
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("/api/citizen")
class CitizenController(
    private val reserverService: ReserverService,
    private val boatService: BoatService,
    private val citizenService: CitizenService,
    private val organizationService: OrganizationService,
    private val reservationService: ReservationService,
    private val reservationResponseMapper: ReservationResponseMapper,
    private val permissionService: PermissionService,
) {
    private val logger = KotlinLogging.logger {}

    @GetMapping("/public/current")
    fun getCurrentCitizen(request: HttpServletRequest): CurrentCitizenResponse {
        request.getAuthenticatedUser()?.let {
            logger.audit(
                it,
                "GET_CURRENT_CITIZEN"
            )
        }
        val citizen = getCitizen(request, reserverService)
        return citizen.toCurrentCitizenResponse()
    }

    @GetMapping("/current/boats")
    fun getBoats(request: HttpServletRequest): List<CitizenBoatResponse> {
        request.getAuthenticatedUser()?.let {
            logger.audit(
                it,
                "GET_CURRENT_BOATS"
            )
        }
        val citizenId = request.ensureCitizenId()
        val boats = boatService.getBoatsForReserver(citizenId)
        return boats.toCitizenBoatListResponse()
    }

    @GetMapping("/current/organization-boats/{orgId}")
    fun getOrganizationBoats(
        @PathVariable orgId: UUID,
        request: HttpServletRequest
    ): List<CitizenBoatResponse> {
        request.getAuthenticatedUser()?.let {
            logger.audit(
                it,
                "GET_CURRENT_ORGANIZATION_BOATS",
                mapOf("targetId" to orgId.toString())
            )
        }
        val userId = request.getAuthenticatedUser()?.id
        if (userId == null || !permissionService.hasAccessToOrganization(userId, orgId)) throw Forbidden()
        val boats = boatService.getBoatsForReserver(orgId)
        return boats.toCitizenBoatListResponse()
    }

    @GetMapping("/current/organizations")
    fun getOrganizations(request: HttpServletRequest): List<CitizenOrganizationResponse> {
        request.getAuthenticatedUser()?.let {
            logger.audit(
                it,
                "GET_CURRENT_ORGANIZATIONS"
            )
        }
        val citizenId = request.ensureCitizenId()
        val organizations = organizationService.getCitizenOrganizations(citizenId)
        return organizations.toCitizenOrganizationListResponse()
    }

    @GetMapping("/current/active-reservations")
    fun getActiveReservations(request: HttpServletRequest): List<ReservationResponse> {
        request.getAuthenticatedUser()?.let {
            logger.audit(
                it,
                "GET_CURRENT_ACTIVE_RESERVATIONS"
            )
        }
        val reservations = reservationService.getActiveReservationsForCurrentCitizen()
        return reservations.map { reservationResponseMapper.toReservationResponse(it) }
    }

    @GetMapping("/current/expired-reservations")
    fun getExpiredReservations(request: HttpServletRequest): List<ReservationResponse> {
        request.getAuthenticatedUser()?.let {
            logger.audit(
                it,
                "GET_CURRENT_EXPIRED_RESERVATIONS"
            )
        }
        val reservations = reservationService.getExpiredReservationsForCurrentCitizen()
        return reservations.map { reservationResponseMapper.toReservationResponse(it) }
    }

    @GetMapping("/current/organization-active-reservations/{orgId}")
    fun getOrganizationActiveReservations(
        @PathVariable orgId: UUID,
        request: HttpServletRequest
    ): List<ReservationResponse> {
        request.getAuthenticatedUser()?.let {
            logger.audit(
                it,
                "GET_CURRENT_ORGANIZATION_ACTIVE_RESERVATIONS",
                mapOf("targetId" to orgId.toString())
            )
        }
        val userId = request.getAuthenticatedUser()?.id
        if (userId == null || !permissionService.hasAccessToOrganization(userId, orgId)) throw Forbidden()
        val reservations = reservationService.getActiveReservationsForOrganization(orgId)
        return reservations.map { reservationResponseMapper.toReservationResponse(it) }
    }

    @GetMapping("/current/organization-expired-reservations/{orgId}")
    fun getOrganizationExpiredReservations(
        @PathVariable orgId: UUID,
        request: HttpServletRequest
    ): List<ReservationResponse> {
        request.getAuthenticatedUser()?.let {
            logger.audit(
                it,
                "GET_CURRENT_ORGANIZATION_EXPIRED_RESERVATIONS",
                mapOf("targetId" to orgId.toString())
            )
        }
        val userId = request.getAuthenticatedUser()?.id
        if (userId == null || !permissionService.hasAccessToOrganization(userId, orgId)) throw Forbidden()
        val reservations = reservationService.getExpiredReservationsForOrganization(orgId)
        return reservations.map { reservationResponseMapper.toReservationResponse(it) }
    }

    @PostMapping("/current/update-information")
    fun postUpdateCitizenInformation(
        @RequestBody input: UpdateCitizenInformationInput,
        request: HttpServletRequest,
    ) {
        request.getAuthenticatedUser()?.let {
            logger.audit(
                it,
                "UPDATE_CITIZEN_INFORMATION",
            )
        }
        citizenService.updateCitizen(input)
    }

    @PostMapping("/current/update-trailer")
    fun postUpdateCitizenInformation(
        request: HttpServletRequest,
        @RequestBody input: UpdateTrailerInformationInput,
    ) {
        request.getAuthenticatedUser()?.let {
            logger.audit(
                it,
                "UPDATE_TRAILER",
            )
        }
        citizenService.updateTrailer(input)
    }

    @PostMapping("/current/update-boat")
    fun postUpdateCitizenInformation(
        @RequestBody input: UpdateBoatInformationInput,
        request: HttpServletRequest,
    ) {
        request.getAuthenticatedUser()?.let {
            logger.audit(
                it,
                "UPDATE_BOAT",
            )
        }
        citizenService.updateBoat(input)
    }
}
