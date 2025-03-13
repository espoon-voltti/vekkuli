package fi.espoo.vekkuli.boatSpace.boatSpaceSwitch

import fi.espoo.vekkuli.common.NotFound
import fi.espoo.vekkuli.domain.*
import fi.espoo.vekkuli.service.BoatReservationService
import fi.espoo.vekkuli.utils.intToDecimal
import org.springframework.stereotype.Service
import java.math.BigDecimal

data class SwitchSourceResponse(
    val id: Int,
    val spaceType: BoatSpaceType,
    val width: BigDecimal?,
    val length: BigDecimal?,
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
            width = getSwitchSourceDimensions(boatSpaceDetails).first,
            length = getSwitchSourceDimensions(boatSpaceDetails).second,
            boatType = boatSpaceDetails.boat?.type
        )
    }

    private fun getBoatSpace(reservationId: Int): BoatSpaceReservationDetails =
        spaceReservationService.getBoatSpaceReservation(reservationId) ?: throw NotFound()

    private fun getSwitchSourceDimensions(boatSpaceDetails: BoatSpaceReservationDetails): Pair<BigDecimal?, BigDecimal?> =
        when (boatSpaceDetails.type) {
            BoatSpaceType.Slip ->
                Pair(
                    intToDecimal(boatSpaceDetails.boat?.widthCm),
                    intToDecimal(boatSpaceDetails.boat?.lengthCm)
                )
            BoatSpaceType.Winter ->
                Pair(
                    boatSpaceDetails.boatSpaceWidthInM,
                    boatSpaceDetails.boatSpaceLengthInM
                )
            BoatSpaceType.Storage ->
                Pair(
                    boatSpaceDetails.boatSpaceWidthInM,
                    boatSpaceDetails.boatSpaceLengthInM
                )
            BoatSpaceType.Trailer ->
                Pair(
                    intToDecimal(boatSpaceDetails.trailer?.widthCm),
                    intToDecimal(boatSpaceDetails.trailer?.lengthCm)
                )
        }
}
