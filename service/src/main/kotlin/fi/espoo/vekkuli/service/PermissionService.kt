package fi.espoo.vekkuli.service

import fi.espoo.vekkuli.domain.ReservationStatus
import fi.espoo.vekkuli.domain.ReserverType
import fi.espoo.vekkuli.repository.JdbiBoatSpaceReservationRepository
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class PermissionService(
    private val userService: UserService,
    private val organizationService: OrganizationService,
    private val boatSpaceReservationRepo: JdbiBoatSpaceReservationRepository
) {
    fun canTerminateBoatSpaceReservation(
        terminatorId: UUID,
        reservationId: Int
    ): Boolean {
        val reservation = boatSpaceReservationRepo.getReservationWithDependencies(reservationId)
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

    fun canTerminateBoatSpaceReservationForOtherUser(
        terminatorId: UUID,
        reservationId: Int
    ): Boolean =
        when {
            userService.isAppUser(terminatorId) -> true
            else -> false
        }

    fun canDeleteBoatSpaceReservation(
        deleterId: UUID,
        reservationId: Int
    ): Boolean {
        val reservation = boatSpaceReservationRepo.getReservationWithDependencies(reservationId)
        return when {
            reservation?.status !in setOf(ReservationStatus.Payment, ReservationStatus.Info) -> false
            userService.isAppUser(deleterId) -> true
            reservation?.reserverId == null -> false
            reservation.reserverId == deleterId -> true
            reservation.reserverType == ReserverType.Organization -> {
                deleterId in organizationService.getOrganizationMembers(reservation.reserverId).map { it.id }
            }
            else -> false
        }
    }
}
