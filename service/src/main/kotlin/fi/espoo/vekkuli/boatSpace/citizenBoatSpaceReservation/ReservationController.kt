package fi.espoo.vekkuli.boatSpace.citizenBoatSpaceReservation

import fi.espoo.vekkuli.common.NotFound
import fi.espoo.vekkuli.domain.BoatType
import fi.espoo.vekkuli.service.ReserverService
import fi.espoo.vekkuli.utils.decimalToInt
import jakarta.servlet.http.HttpServletRequest
import org.springframework.web.bind.annotation.*
import java.math.BigDecimal

@RestController
@RequestMapping("/api/citizen")
class ReservationController(
    private val reservationService: ReservationService,
    private val reservationResponseMapper: ReservationResponseMapper,
    private val reserverService: ReserverService,
) {
    @GetMapping("/unfinished-reservation")
    fun getUnfinishedReservation(): ReservationResponse {
        val reservation = reservationService.getUnfinishedReservationForCurrentCitizen() ?: throw NotFound()
        return reservationResponseMapper.toReservationResponse(reservation)
    }

    @GetMapping("/unfinished-reservation-expiration")
    fun getUnfinishedReservationExpiration(): Int {
        return reservationService.getUnfinishedReservationExpirationForCurrentCitizen()?.value ?: throw NotFound()
    }

    @PostMapping("/reserve/{spaceId}")
    fun postStartReservation(
        @PathVariable spaceId: Int,
    ): ReservationResponse {
        val reservation = reservationService.startReservation(spaceId)
        return reservationResponseMapper.toReservationResponse(reservation)
    }

    @GetMapping("/reservation/{reservationId}")
    fun getReservation(
        @PathVariable reservationId: Int,
    ): ReservationResponse {
        val reservation = reservationService.getReservation(reservationId)
        return reservationResponseMapper.toReservationResponse(reservation)
    }

    @PostMapping("/reservation/{reservationId}/fill")
    fun postFillReservationInformation(
        @PathVariable reservationId: Int,
        @RequestBody input: FillReservationInformationInput,
    ) {
        val information = input.toReservationInformation()
        reservationService.fillReservationInformation(reservationId, information)
    }

    @DeleteMapping("/reservation/{reservationId}/cancel")
    fun deleteReservation(
        @PathVariable reservationId: Int,
    ) {
        reservationService.cancelUnfinishedReservation(reservationId)
    }

    @PostMapping("/reservation/{reservationId}/payment-information")
    suspend fun getPaymentInformation(
        @PathVariable reservationId: Int,
    ): PaymentInformationResponse {
        return reservationService.getPaymentInformation(reservationId).toPaymentInformationResponse()
    }

    @GetMapping("/reservation/{reservationId}/validate-boat-type")
    fun validateBoatType(
        @PathVariable reservationId: Int,
        @RequestParam boatType: BoatType,
    ): Boolean {
        return reservationService.validateBoatType(reservationId, boatType)
    }

    @GetMapping("/reservation/{reservationId}/validate-boat-size")
    fun validateBoatSize(
        @PathVariable reservationId: Int,
        @RequestParam width: BigDecimal,
        @RequestParam length: BigDecimal,
    ): Boolean {
        return reservationService.validateBoatSize(reservationId, decimalToInt(width), decimalToInt(length))
    }

    @GetMapping("/reservation/{reservationId}/validate-boat-weight")
    fun validateBoatWeight(
        @PathVariable reservationId: Int,
        @RequestParam weight: Int
    ): Boolean {
        return reservationService.validateBoatWeight(reservationId, weight)
    }

    @GetMapping("/municipalities")
    fun getMunicipalities(request: HttpServletRequest): List<MunicipalityResponse> {
        return reserverService.getMunicipalities().toMunicipalityListResponse()
    }

    @PostMapping("/reservation/{reservationId}/terminate")
    fun terminateReservation(
        @PathVariable reservationId: Int,
    ) {
        reservationService.terminateReservation(reservationId)
    }
}
