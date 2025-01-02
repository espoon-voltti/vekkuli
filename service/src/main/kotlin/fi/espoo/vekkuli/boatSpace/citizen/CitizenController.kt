package fi.espoo.vekkuli.boatSpace.citizen

import fi.espoo.vekkuli.boatSpace.citizenBoatSpaceReservation.ReservationResponse
import fi.espoo.vekkuli.boatSpace.citizenBoatSpaceReservation.ReservationResponseMapper
import fi.espoo.vekkuli.boatSpace.citizenBoatSpaceReservation.ReservationService
import fi.espoo.vekkuli.config.ensureCitizenId
import fi.espoo.vekkuli.controllers.Utils.Companion.getCitizen
import fi.espoo.vekkuli.repository.UpdateCitizenParams
import fi.espoo.vekkuli.service.BoatService
import fi.espoo.vekkuli.service.OrganizationService
import fi.espoo.vekkuli.service.ReserverService
import jakarta.servlet.http.HttpServletRequest
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/citizen")
class CitizenController(
    private val reserverService: ReserverService,
    private val boatService: BoatService,
    private val organizationService: OrganizationService,
    private val reservationService: ReservationService,
    private val reservationResponseMapper: ReservationResponseMapper,
) {
    @GetMapping("/public/current")
    fun getCurrentCitizen(request: HttpServletRequest): CurrentCitizenResponse {
        val citizen = getCitizen(request, reserverService)
        return citizen.toCurrentCitizenResponse()
    }

    @GetMapping("/current/boats")
    fun getBoats(request: HttpServletRequest): List<CitizenBoatResponse> {
        val citizenId = request.ensureCitizenId()
        val boats = boatService.getBoatsForReserver(citizenId)
        return boats.toCitizenBoatListResponse()
    }

    @GetMapping("/current/organizations")
    fun getOrganizations(request: HttpServletRequest): List<CitizenOrganizationResponse> {
        val citizenId = request.ensureCitizenId()
        val organizations = organizationService.getCitizenOrganizations(citizenId)
        return organizations.toCitizenOrganizationListResponse()
    }

    @GetMapping("/current/active-reservations")
    fun getActiveReservations(request: HttpServletRequest): List<ReservationResponse> {
        val reservations = reservationService.getActiveReservationsForCurrentCitizen()
        return reservations.map { reservationResponseMapper.toReservationResponse(it) }
    }

    @GetMapping("/current/expired-reservations")
    fun getExpiredReservations(request: HttpServletRequest): List<ReservationResponse> {
        val reservations = reservationService.getExpiredReservationsForCurrentCitizen()
        return reservations.map { reservationResponseMapper.toReservationResponse(it) }
    }

    @PostMapping("/current/update-information")
    fun postUpdateCitizenInformation(
        request: HttpServletRequest,
        @RequestBody input: UpdateCitizenInformationInput,
    ) {
        val citizenId = request.ensureCitizenId()
        val params =
            UpdateCitizenParams(
                id = citizenId,
                phone = input.phone,
                email = input.email,
            )
        reserverService.updateCitizen(params)
    }

    @PostMapping("/current/update-trailer")
    fun postUpdateCitizenInformation(
        @RequestBody input: UpdateTrailerInformationInput,
    ) {
        reservationService.updateTrailer(
            input
        )
    }
}
