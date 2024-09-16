package fi.espoo.vekkuli.domain

import java.util.*

data class Citizen(
    val id: UUID,
    val nationalId: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val phone: String,
    val address: String?,
    val postalCode: String?,
    val municipalityCode: Int,
)

data class CitizenWithDetails(
    val id: UUID,
    val nationalId: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val phone: String,
    val address: String?,
    val postalCode: String?,
    val municipalityCode: Int,
    val municipalityName: String,
    val fullName: String = "$firstName $lastName"
)

data class Municipality(
    val code: Int,
    val name: String
)
