package fi.espoo.vekkuli.boatSpace.citizenBoatSpaceReservation

import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import java.net.URI

@Controller
class ReservationPaymentController(
    private val reservationPaymentService: ReservationPaymentService,
) {
    @GetMapping(ReservationPaymentConfig.SUCCESS_CALLBACK_PATH)
    fun getPaytrailCallbackSuccess(
        @RequestParam params: Map<String, String>
    ): ResponseEntity<Void> {
        reservationPaymentService.handlePaymentSuccess(params)
        return ResponseEntity(HttpStatus.NO_CONTENT)
    }

    @GetMapping(ReservationPaymentConfig.CANCEL_CALLBACK_PATH)
    fun getPaytrailCallbackCancel(
        @RequestParam params: Map<String, String>
    ): ResponseEntity<Void> {
        reservationPaymentService.handlePaymentCancel(params)
        return ResponseEntity(HttpStatus.NO_CONTENT)
    }

    @GetMapping(ReservationPaymentConfig.SUCCESS_REDIRECT_PATH)
    fun getPaytrailRedirectSuccess(
        @RequestParam params: Map<String, String>
    ): ResponseEntity<Void> {
        val result = reservationPaymentService.handlePaymentSuccess(params)
        return makeRedirectResponse(result.redirectUrl)
    }

    @GetMapping(ReservationPaymentConfig.CANCEL_REDIRECT_PATH)
    fun getPaytrailRedirectCancel(
        @RequestParam params: Map<String, String>
    ): ResponseEntity<Void> {
        val result = reservationPaymentService.handlePaymentCancel(params)
        return makeRedirectResponse(result.redirectUrl)
    }

    private fun makeRedirectResponse(location: URI): ResponseEntity<Void> {
        val headers = HttpHeaders()
        headers.location = location
        return ResponseEntity<Void>(headers, HttpStatus.FOUND)
    }
}
