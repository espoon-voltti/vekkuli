package fi.espoo.vekkuli.service

import fi.espoo.vekkuli.domain.Boat
import fi.espoo.vekkuli.domain.BoatType
import fi.espoo.vekkuli.domain.OwnershipStatus
import fi.espoo.vekkuli.repository.BoatRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class BoatService(
    private val boatRepository: BoatRepository
) {
    fun getBoatsForReserver(reserverId: UUID): List<Boat> = boatRepository.getBoatsForReserver(reserverId)

    fun getBoat(boatId: Int): Boat? = boatRepository.getBoat(boatId)

    fun updateBoat(boat: Boat): Boat = boatRepository.updateBoat(boat)

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
}
