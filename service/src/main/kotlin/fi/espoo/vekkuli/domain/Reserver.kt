package fi.espoo.vekkuli.domain

import java.util.UUID

enum class ReserverType {
    Citizen,
    Organization,
}

data class Reserver(
    val id: UUID,
    val type: ReserverType,
    val email: String,
    val phone: String,
    val municipalityCode: Int,
    val streetAddress: String,
    val streetAddressSv: String,
    val postOffice: String,
    val postOfficeSv: String,
    val postalCode: String,
)

data class ReserverWithDetails(
    val id: UUID,
    val name: String,
    val type: ReserverType,
    val email: String,
    val phone: String,
    val municipalityCode: Int,
    val municipalityName: String,
    val streetAddress: String,
    val streetAddressSv: String,
    val postOffice: String,
    val postOfficeSv: String,
    val postalCode: String,
)
