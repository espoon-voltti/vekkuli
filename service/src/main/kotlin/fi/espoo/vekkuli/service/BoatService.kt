package fi.espoo.vekkuli.service

import fi.espoo.vekkuli.domain.Boat
import fi.espoo.vekkuli.domain.BoatType
import fi.espoo.vekkuli.domain.OwnershipStatus
import org.springframework.stereotype.Service
import java.util.*

interface BoatRepository {
    fun getBoatsForCitizen(citizenId: UUID): List<Boat>

    fun getBoat(boatId: Int): Boat?

    fun updateBoat(boat: Boat): Boat

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
    ): Boat
}

@Service
class BoatService(
    private val boatRepository: BoatRepository
) {
    fun getBoatsForCitizen(citizenId: UUID): List<Boat> = boatRepository.getBoatsForCitizen(citizenId)

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
}
