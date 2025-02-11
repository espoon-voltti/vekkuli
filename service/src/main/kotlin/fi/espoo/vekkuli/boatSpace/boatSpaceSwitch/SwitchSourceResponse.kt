package fi.espoo.vekkuli.boatSpace.boatSpaceSwitch

import fi.espoo.vekkuli.common.NotFound
import fi.espoo.vekkuli.domain.BoatSpace
import fi.espoo.vekkuli.domain.BoatSpaceReservation
import fi.espoo.vekkuli.domain.BoatSpaceType
import fi.espoo.vekkuli.service.BoatReservationService
import org.springframework.stereotype.Service

data class SwitchSourceResponse(
    val id: Int,
    val spaceType: BoatSpaceType
)

@Service
class SwitchSourceResponseAssembler(
    private val spaceReservationService: BoatReservationService,
) {
    fun toSwitchSourceResponse(reservation: BoatSpaceReservation): SwitchSourceResponse = assembleSwitchResponse(reservation)

    private fun assembleSwitchResponse(reservation: BoatSpaceReservation,): SwitchSourceResponse {
        val boatSpace = getBoatSpace(reservation.id)

        return SwitchSourceResponse(
            id = reservation.id,
            spaceType = boatSpace.type,
        )
    }

    private fun getBoatSpace(reservationId: Int): BoatSpace =
        spaceReservationService.getBoatSpaceRelatedToReservation(reservationId) ?: throw NotFound()
}
