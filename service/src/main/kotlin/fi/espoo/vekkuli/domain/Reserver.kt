package fi.espoo.vekkuli.domain

import java.time.LocalDateTime
import java.util.UUID

enum class ReserverType {
    Citizen,
    Organization,
}

data class Reserver(
    val id: UUID,
    val type: ReserverType,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
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
    val type: ReserverType,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
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
