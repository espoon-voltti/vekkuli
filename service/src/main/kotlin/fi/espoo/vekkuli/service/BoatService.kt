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

    fun getBoat(boatId: Int): Boat? = boatRepository.getBoat(boatId)

    fun updateBoat(
        boat: Boat,
        checkReservationWarnings: Boolean = true
    ): Boat {
        val result = boatRepository.updateBoat(boat)

        if (checkReservationWarnings) {
            setReservationWarnings(result)
        }

        return result
    }

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

    fun deleteBoat(boatId: Int): Boolean = boatRepository.deleteBoat(boatId)

    private fun setReservationWarnings(boat: Boat) {
        val reservations = boatReservationService.getBoatSpaceReservationsForReserver(boat.reserverId)
        val reservation = reservations.find { it.boat?.id == boat.id }
        if (reservation != null) {
            val boatSpace =
                boatReservationService.getBoatSpaceRelatedToReservation(reservation.id)
                    ?: throw IllegalArgumentException("Reservation not found")

            boatReservationService.addReservationWarnings(
                reservation.id,
                boat.id,
                reservation.boatSpaceWidthCm,
                reservation.boatSpaceLengthCm,
                reservation.amenity,
                boat.widthCm,
                boat.lengthCm,
                boat.ownership,
                boat.weightKg,
                boat.type,
                boatSpace.excludedBoatTypes ?: listOf(),
            )
        }
    }
}
