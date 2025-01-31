package fi.espoo.vekkuli.boatSpace.boatSpaceSwitch

import fi.espoo.vekkuli.common.BadRequest
import fi.espoo.vekkuli.common.Forbidden
import fi.espoo.vekkuli.domain.*
import fi.espoo.vekkuli.service.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class BoatSpaceSwitchService(
    private val boatReservationService: BoatReservationService,
    private val boatSpaceSwitchRepository: BoatSpaceSwitchRepository,
    private val citizenAccessControl: ContextCitizenAccessControl,
    private val switchPolicyService: SwitchPolicyService,
) {
    @Transactional
    fun startReservation(
        spaceId: Int,
        reservationId: Int
    ): BoatSpaceReservation {
        val (citizenId) = citizenAccessControl.requireCitizen()

        if (citizenHasExistingUnfinishedReservation(citizenId)) {
            throw Forbidden("Citizen can not have multiple reservations started")
        }

        if (!switchPolicyService.citizenCanSwitchToReservation(reservationId, citizenId, spaceId).success) {
            throw Forbidden("Citizen can not switch reservation")
        }

        return boatSpaceSwitchRepository.copyReservationToSwitchReservation(
            reservationId,
            citizenId,
            spaceId
        )
    }

    fun isSwitchedReservation(reservation: BoatSpaceReservationDetails): Boolean = reservation.creationType == CreationType.Switch

    // Returns the total payable amount of the new reservation. Can be negative
    fun getRevisedPrice(reservation: ReservationWithDependencies): Int {
        if (reservation.originalReservationId == null) {
            throw BadRequest("Original reservation not found")
        }
        return getRevisedPrice(reservation.priceCents, reservation.originalReservationId)
    }

    fun getRevisedPrice(reservation: BoatSpaceReservationDetails): Int {
        if (reservation.originalReservationId == null) {
            throw BadRequest("Original reservation not found")
        }
        return getRevisedPrice(reservation.priceCents, reservation.originalReservationId)
    }

    private fun getRevisedPrice(
        newReservationPriceCents: Int,
        originalReservationId: Int
    ): Int {
        val originalReservation =
            boatReservationService
                .getBoatSpaceReservation(originalReservationId) ?: throw BadRequest("Original reservation not found")

        return newReservationPriceCents - originalReservation.priceCents
    }

    private fun citizenHasExistingUnfinishedReservation(citizenId: UUID): Boolean =
        boatReservationService.getUnfinishedReservationForCitizen(citizenId) != null
}
