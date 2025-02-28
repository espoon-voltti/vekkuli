package fi.espoo.vekkuli.boatSpace.boatSpaceSwitch

import fi.espoo.vekkuli.common.NotFound
import fi.espoo.vekkuli.domain.*
import fi.espoo.vekkuli.service.BoatReservationService
import org.springframework.stereotype.Service

data class SwitchSourceResponse(
    val id: Int,
    val spaceType: BoatSpaceType,
    val spaceWidthCm: Int,
    val spaceLengthCm: Int,
    val trailerWidthCm: Int?,
    val trailerLengthCm: Int?,
    val boatWidthCm: Int?,
    val boatLengthCm: Int?,
    val boatType: BoatType?,
)

@Service
class SwitchSourceResponseAssembler(
    private val spaceReservationService: BoatReservationService,
) {
    fun toSwitchSourceResponse(reservation: BoatSpaceReservation): SwitchSourceResponse {
        val boatSpaceDetails = getBoatSpace(reservation.id)

        return SwitchSourceResponse(
            id = reservation.id,
            spaceType = boatSpaceDetails.type,
            spaceWidthCm = boatSpaceDetails.boatSpaceWidthCm,
            spaceLengthCm = boatSpaceDetails.boatSpaceLengthCm,
            trailerWidthCm = boatSpaceDetails.trailer?.widthCm,
            trailerLengthCm = boatSpaceDetails.trailer?.lengthCm,
            boatWidthCm = boatSpaceDetails.boat?.widthCm,
            boatLengthCm = boatSpaceDetails.boat?.lengthCm,
            boatType = boatSpaceDetails.boat?.type
        )
    }

    private fun getBoatSpace(reservationId: Int): BoatSpaceReservationDetails =
        spaceReservationService.getBoatSpaceReservation(reservationId) ?: throw NotFound()
}
