package fi.espoo.vekkuli.boatSpace.citizenBoatSpaceReservation

import fi.espoo.vekkuli.boatSpace.citizen.toCitizenBoatListResponse
import fi.espoo.vekkuli.boatSpace.citizen.toCitizenOrganizationListResponse
import fi.espoo.vekkuli.boatSpace.citizenTrailer.UpdateStorageTypeInput
import fi.espoo.vekkuli.common.NotFound
import fi.espoo.vekkuli.config.audit
import fi.espoo.vekkuli.config.ensureCitizenId
import fi.espoo.vekkuli.config.getAuthenticatedUser
import fi.espoo.vekkuli.service.*
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.servlet.http.HttpServletRequest
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/citizen")
class ReservationController(
    private val reservationService: ReservationService,
    private val reservationResponseMapper: ReservationResponseMapper,
    private val reserverService: ReserverService,
    private val canReserveResponseMapper: CanReserveResponseMapper,
    private val boatService: BoatService,
    private val organizationService: OrganizationService,
    private val boatSpaceRepository: BoatSpaceRepository
) {
    private val logger = KotlinLogging.logger {}

    @GetMapping("/unfinished-reservation")
    fun getUnfinishedReservation(request: HttpServletRequest): UnfinishedReservationResponse {
        request.getAuthenticatedUser()?.let {
            logger.audit(
                it,
                "GET_UNFINISHED_RESERVATION"
            )
        }
        val citizenId = request.ensureCitizenId()

        val reservation = reservationService.getUnfinishedReservationForCurrentCitizen() ?: throw NotFound()
        val boatSpace = boatSpaceRepository.getBoatSpace(reservation.boatSpaceId) ?: throw NotFound()

        val organizations =
            organizationService.getOrganizationsForReservation(
                citizenId,
                reservation,
                boatSpace.type
            )
        val reserverId = if (reservation.reserverId !== null) reservation.reserverId else citizenId
        val boats = boatService.getBoatsForReserver(reserverId)
        val boatsByOrganization = boatService.getBoatsForReserversOrganizations(citizenId)
        val reservationResponse = reservationResponseMapper.toReservationResponse(reservation)
        val revisedPrice = reservationResponse.reservationInfo
        return UnfinishedReservationResponse(
            reservationResponse,
            boats.toCitizenBoatListResponse(),
            reserverService.getMunicipalities().toMunicipalityListResponse(),
            organizations.toCitizenOrganizationListResponse(),
            boatsByOrganization,
            reservationResponseMapper.toOrganizationReservationInfos(
                revisedPrice.revisedPriceInCents,
                organizations,
                boatSpace.type,
                reservation
            ),
        )
    }

    @GetMapping("/unfinished-reservation-expiration")
    fun getUnfinishedReservationExpiration(request: HttpServletRequest): Int {
        request.getAuthenticatedUser()?.let {
            logger.audit(
                it,
                "GET_UNFINISHED_RESERVATION_EXPIRATION",
            )
        }
        return reservationService.getUnfinishedReservationExpirationForCurrentCitizen()?.value ?: throw NotFound()
    }

    @PostMapping("/reserve/{spaceId}")
    fun postStartReservation(
        @PathVariable spaceId: Int,
        request: HttpServletRequest
    ): ReservationResponse {
        request.getAuthenticatedUser()?.let {
            logger.audit(
                it,
                "START_RESERVATION",
                mapOf(
                    "targetId" to spaceId.toString()
                )
            )
        }
        val reservation = reservationService.startReservation(spaceId)
        return reservationResponseMapper.toReservationResponse(reservation)
    }

    @GetMapping("/can-reserve/{spaceId}")
    fun canReserveSpace(
        @PathVariable spaceId: Int,
        request: HttpServletRequest
    ): CanReserveResponse {
        request.getAuthenticatedUser()?.let {
            logger.audit(
                it,
                "CAN_RESERVE_SPACE",
                mapOf("targetId" to spaceId.toString())
            )
        }
        val canReserve = reservationService.checkReservationAvailabilityForCurrentCitizen(spaceId)
        return canReserveResponseMapper.toCanReserveResponse(canReserve)
    }

    @GetMapping("/reservation/{reservationId}")
    fun getReservation(
        @PathVariable reservationId: Int,
        request: HttpServletRequest
    ): ReservationResponse {
        request.getAuthenticatedUser()?.let {
            logger.audit(
                it,
                "GET_RESERVATION",
                mapOf("targetId" to reservationId.toString())
            )
        }
        val reservation = reservationService.getReservation(reservationId)
        return reservationResponseMapper.toReservationResponse(reservation)
    }

    @PostMapping("/reservation/{reservationId}/fill")
    fun postFillReservationInformation(
        @PathVariable reservationId: Int,
        @RequestBody input: FillReservationInformationInput,
        request: HttpServletRequest
    ): ReservationResponse {
        request.getAuthenticatedUser()?.let {
            logger.audit(
                it,
                "FILL_RESERVATION_INFORMATION",
                mapOf("targetId" to reservationId.toString())
            )
        }
        val information = input.toReservationInformation()
        return reservationResponseMapper.toReservationResponse(
            reservationService.fillReservationInformation(reservationId, information)
        )
    }

    @DeleteMapping("/reservation/{reservationId}/cancel")
    fun deleteReservation(
        @PathVariable reservationId: Int,
        request: HttpServletRequest
    ) {
        request.getAuthenticatedUser()?.let {
            logger.audit(
                it,
                "CANCEL_RESERVATION",
                mapOf("targetId" to reservationId.toString())
            )
        }
        reservationService.cancelUnfinishedReservation(reservationId)
    }

    @PatchMapping("/reservation/{reservationId}/cancel-payment")
    fun cancelPayment(
        @PathVariable reservationId: Int,
        request: HttpServletRequest
    ) {
        request.getAuthenticatedUser()?.let {
            logger.audit(
                it,
                "CANCEL_PAYMENT",
                mapOf("targetId" to reservationId.toString())
            )
        }
        reservationService.cancelUnfinishedReservationPaymentState(reservationId)
    }

    @PostMapping("/reservation/{reservationId}/payment-information")
    suspend fun getPaymentInformation(
        @PathVariable reservationId: Int,
        request: HttpServletRequest
    ): PaymentInformationResponse {
        request.getAuthenticatedUser()?.let {
            logger.audit(
                it,
                "GET_PAYMENT_INFORMATION",
                mapOf("targetId" to reservationId.toString())
            )
        }
        return reservationService.getPaymentInformation(reservationId).toPaymentInformationResponse()
    }

    @GetMapping("/municipalities")
    fun getMunicipalities(request: HttpServletRequest): List<MunicipalityResponse> {
        request.getAuthenticatedUser()?.let {
            logger.audit(
                it,
                "GET_MUNICIPALITIES",
            )
        }
        return reserverService.getMunicipalities().toMunicipalityListResponse()
    }

    @PostMapping("/reservation/{reservationId}/terminate")
    fun terminateReservation(
        @PathVariable reservationId: Int,
        request: HttpServletRequest
    ) {
        request.getAuthenticatedUser()?.let {
            logger.audit(
                it,
                "TERMINATE_RESERVATION",
                mapOf("targetId" to reservationId.toString())
            )
        }
        reservationService.terminateReservation(reservationId)
    }

    @PatchMapping("/reservation/{reservationId}/update-storage-type")
    fun patchTrailer(
        @PathVariable reservationId: Int,
        @RequestBody input: UpdateStorageTypeInput,
        request: HttpServletRequest,
    ) {
        request.getAuthenticatedUser()?.let {
            logger.audit(
                it,
                "UPDATE_STORAGE_TYPE",
                mapOf(
                    "targetId" to reservationId.toString()
                ),
            )
        }
        reservationService.updateStorageTypeAndTrailer(reservationId, input)
    }
}
