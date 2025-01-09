package fi.espoo.vekkuli.domain

import java.util.UUID

enum class ReserverType {
    Citizen,
    Organization,
}

fun ReserverType.toPath() =
    when (this) {
        ReserverType.Citizen -> "kayttaja"
        ReserverType.Organization -> "yhteiso"
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
    val espooRulesApplied: Boolean,
)

fun reserverWithDetailsFromOrganization(organization: Organization): ReserverWithDetails =
    ReserverWithDetails(
        id = organization.id,
        name = organization.name,
        type = ReserverType.Organization,
        email = organization.email,
        phone = organization.phone,
        municipalityCode = organization.municipalityCode,
        municipalityName = organization.municipalityName,
        streetAddress = organization.streetAddress,
        streetAddressSv = organization.streetAddressSv,
        postOffice = organization.postOffice,
        postOfficeSv = organization.postOfficeSv,
        postalCode = organization.postalCode,
        espooRulesApplied = organization.espooRulesApplied
    )
