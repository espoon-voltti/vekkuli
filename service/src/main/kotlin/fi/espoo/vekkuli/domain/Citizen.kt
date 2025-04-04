package fi.espoo.vekkuli.domain

import java.time.LocalDate
import java.util.*

data class Citizen(
    // Fields from Reserver
    val id: UUID,
    val email: String,
    val phone: String,
    val municipalityCode: Int,
    val municipalityName: String,
    val streetAddress: String,
    val streetAddressSv: String,
    val postOffice: String,
    val postOfficeSv: String,
    val postalCode: String,
    // Fields for Citizen
    val nationalId: String,
    val firstName: String,
    val lastName: String,
) {
    val birthday: String
        get() = getBirthDateFromSSN(nationalId)
}

data class CitizenWithDetails(
    // Fields from Reserver
    val id: UUID,
    val email: String,
    val phone: String,
    val municipalityCode: Int,
    val municipalityName: String,
    val streetAddress: String,
    val streetAddressSv: String,
    val postOffice: String,
    val postOfficeSv: String,
    val postalCode: String,
    val espooRulesApplied: Boolean,
    val discountPercentage: Int,
    val exceptionNotes: String?,
    // Fields for Citizen
    val nationalId: String,
    val firstName: String,
    val lastName: String,
    val dataProtection: Boolean
) {
    val birthday: String
        get() = getBirthDateFromSSN(nationalId)

    val birthdayAsDate: LocalDate
        get() = getBirthDateFromSSNAsDate(nationalId)

    val fullName: String
        get() = "$firstName $lastName"
}

data class LocalizedName(
    val sv: String?,
    val fi: String?
)

data class CitizenAdUser(
    val nationalId: String,
    val firstName: String,
    val lastName: String,
    val email: String?,
    val phone: String?,
    val address: LocalizedName,
    val postOffice: LocalizedName,
    val postalCode: String?,
    val municipalityCode: Int,
    val dataProtection: Boolean,
)

fun getBirthDateFromSSNAsDate(ssn: String): LocalDate {
    // Extract the date parts
    val day = ssn.substring(0, 2).toInt()
    val month = ssn.substring(2, 4).toInt()
    val year = ssn.substring(4, 6).toInt()
    val centuryMarker = ssn[6]

    // Determine the full year based on the century marker
    val fullYear =
        when (centuryMarker) {
            '+' -> 1800 + year // Born in the 1800s
            '-' -> 1900 + year // Born in the 1900s
            'A' -> 2000 + year // Born in the 2000s
            else -> throw IllegalArgumentException("Invalid century marker in SSN")
        }

    return LocalDate.of(fullYear, month, day)
}

fun getBirthDateFromSSN(ssn: String): String {
    val date = getBirthDateFromSSNAsDate(ssn)
    return "${date.dayOfMonth}.${date.monthValue}.${date.year}"
}

data class Municipality(
    val code: Int,
    val name: String
)

fun CitizenWithDetails.toReserverDetails() =
    ReserverWithDetails(
        id = id,
        email = email,
        phone = phone,
        municipalityCode = municipalityCode,
        municipalityName = municipalityName,
        streetAddress = streetAddress,
        streetAddressSv = streetAddressSv,
        postOffice = postOffice,
        postOfficeSv = postOfficeSv,
        postalCode = postalCode,
        name = fullName,
        type = ReserverType.Citizen,
        espooRulesApplied = espooRulesApplied,
        discountPercentage = discountPercentage
    )
