package fi.espoo.vekkuli.service

import fi.espoo.vekkuli.domain.*
import fi.espoo.vekkuli.repository.BoatRepository
import fi.espoo.vekkuli.repository.BoatSpaceReservationRepository
import fi.espoo.vekkuli.repository.TrailerRepository
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class PermissionService(
    private val userService: UserService,
    private val organizationService: OrganizationService,
    private val boatSpaceReservationRepo: BoatSpaceReservationRepository,
    private val trailerRepository: TrailerRepository,
    private val boatRepository: BoatRepository,
) {
    fun hasAccessToOrganization(
        userId: UUID,
        orgId: UUID
    ): Boolean =
        when {
            userService.isAppUser(userId) -> true
            else -> userId in organizationService.getOrganizationMembers(orgId).map { it.id }
        }

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

    fun canEditTrailer(
        editorId: UUID,
        trailerReserverId: UUID
    ): Boolean =
        when {
            userService.isAppUser(editorId) -> true
            editorId == trailerReserverId -> true
            else -> false
        }

    fun canEditTrailer(
        editorId: UUID,
        trailerId: Int
    ): Boolean {
        val trailer = trailerRepository.getTrailer(trailerId)
        return when {
            userService.isAppUser(editorId) -> true
            editorId == trailer?.reserverId -> true
            else -> false
        }
    }

    fun canEditBoat(
        editorId: UUID,
        boatId: Int
    ): Boolean {
        val boat = boatRepository.getBoat(boatId)
        return when {
            userService.isAppUser(editorId) -> true
            editorId == boat?.reserverId -> true
            else -> false
        }
    }

    fun canDeleteBoat(
        editorId: UUID,
        boatId: Int
    ): Boolean = canEditBoat(editorId, boatId)
}
