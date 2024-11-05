package fi.espoo.vekkuli.service

import fi.espoo.vekkuli.domain.ReserverType
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class PermissionService(
    private val reservationService: BoatReservationService,
    private val userService: UserService,
    private val organizationService: OrganizationService
) {
    fun canTerminateBoatSpaceReservation(
        terminatorId: UUID,
        reservationId: Int
    ): Boolean {
        val reservation = reservationService.getReservationWithDependencies(reservationId)
        return when {
            reservation?.reserverId == null -> false
            reservation.reserverId == terminatorId -> true
            userService.isAppUser(terminatorId) -> true
            reservation.reserverType == ReserverType.Organization -> {
                terminatorId in organizationService.getOrganizationMembers(reservation.reserverId).map { it.id }
            }
            else -> false
        }
    }
}
