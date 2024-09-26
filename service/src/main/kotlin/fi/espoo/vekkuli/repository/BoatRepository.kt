package fi.espoo.vekkuli.repository

import fi.espoo.vekkuli.domain.Boat
import fi.espoo.vekkuli.domain.BoatType
import fi.espoo.vekkuli.domain.OwnershipStatus
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

    fun deleteBoat(boatId: Int): Boolean
}
