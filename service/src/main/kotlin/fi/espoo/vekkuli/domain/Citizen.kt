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
    val addressSv: String?,
    val postalCode: String?,
    val postOffice: String?,
    val postOfficeSv: String?,
    val municipalityCode: Int,
)

fun getBirthDateFromSSN(ssn: String): String {
    // Extract the date parts
    val day = ssn.substring(0, 2)
    val month = ssn.substring(2, 4)
    val year = ssn.substring(4, 6)
    val centuryMarker = ssn[6]

    // Determine the full year based on the century marker
    val fullYear =
        when (centuryMarker) {
            '+' -> "18$year" // Born in the 1800s
            '-' -> "19$year" // Born in the 1900s
            'A' -> "20$year" // Born in the 2000s
            else -> throw IllegalArgumentException("Invalid century marker in SSN")
        }

    // Return the formatted birthdate as dd.mm.yyyy
    return "$day.$month.$fullYear"
}

data class CitizenWithDetails(
    val id: UUID,
    val nationalId: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val phone: String,
    val address: String?,
    val addressSv: String?,
    val postalCode: String?,
    val postOffice: String?,
    val postOfficeSv: String?,
    val municipalityCode: Int,
    val municipalityName: String,
    val fullName: String = "$firstName $lastName"
) {
    val birthday: String
        get() = getBirthDateFromSSN(nationalId)
}

data class Municipality(
    val code: Int,
    val name: String
)
