package fi.espoo.vekkuli.repository

import fi.espoo.vekkuli.domain.Boat
import fi.espoo.vekkuli.domain.BoatType
import fi.espoo.vekkuli.domain.OwnershipStatus
import java.util.*

interface BoatRepository {
    fun getBoatsForReserver(reserverId: UUID): List<Boat>

    fun getBoatsForOrganizations(organizationIds: List<UUID>): Map<String, List<Boat>>

    fun getBoat(boatId: Int): Boat?

    fun updateBoat(boat: Boat): Boat

    fun insertBoat(
        reserverId: UUID,
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
