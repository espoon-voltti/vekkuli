package fi.espoo.vekkuli.service

import fi.espoo.vekkuli.domain.Boat
import fi.espoo.vekkuli.domain.BoatType
import fi.espoo.vekkuli.domain.OwnershipStatus
import fi.espoo.vekkuli.repository.BoatRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class BoatService(
    private val boatRepository: BoatRepository,
    private val boatReservationService: BoatReservationService
) {
    fun getBoatsForReserver(reserverId: UUID): List<Boat> = boatRepository.getBoatsForReserver(reserverId)

    fun getBoatsForReserversOrganizations(reserverId: UUID): Map<String, List<Boat>> =
        boatRepository.getBoatsForReserversOrganizations(reserverId)

    fun getBoat(boatId: Int): Boat? = boatRepository.getBoat(boatId)

    fun updateBoatAsCitizen(boat: Boat): Boat {
        val previousBoatInfo = getBoat(boat.id)
        val result = boatRepository.updateBoat(boat)
        boatReservationService.addBoatWarningsToReservations(result, previousBoatInfo)
        return result
    }

    fun updateBoatAsEmployee(boat: Boat) = boatRepository.updateBoat(boat)

    fun insertBoat(
        citizenId: UUID,
        registrationCode: String,
        name: String,
        widthCm: Int,
        lengthCm: Int,
        depthCm: Int,
        weightKg: Int,
        type: BoatType,
        otherIdentification: String,
        extraInformation: String,
        ownership: OwnershipStatus,
    ): Boat =
        boatRepository.insertBoat(
            citizenId,
            registrationCode,
            name,
            widthCm,
            lengthCm,
            depthCm,
            weightKg,
            type,
            otherIdentification,
            extraInformation,
            ownership
        )

    fun deleteBoat(boatId: Int): Boolean {
        val reservations = boatReservationService.getActiveReservationsForBoat(boatId)

        if (reservations.isNotEmpty()) {
            throw IllegalStateException("Cannot delete boat with active reservations")
        }

        return boatRepository.deleteBoat(boatId)
    }
}
