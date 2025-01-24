package fi.espoo.vekkuli.boatSpace.citizen

import fi.espoo.vekkuli.domain.Organization
import java.util.*

data class CitizenOrganizationResponse(
    val id: UUID,
    val name: String,
    val email: String,
    val phone: String,
    val municipalityCode: Int,
    val municipalityName: String,
    val streetAddress: String,
    val streetAddressSv: String,
    val postOffice: String,
    val postOfficeSv: String,
    val postalCode: String,
    val businessId: String,
    val discountPercentage: Int,
)

fun List<Organization>.toCitizenOrganizationListResponse() = map { it.toCitizenOrganizationResponse() }

fun Organization.toCitizenOrganizationResponse() =
    CitizenOrganizationResponse(
        id = id,
        name = name,
        email = email,
        phone = phone,
        municipalityCode = municipalityCode,
        municipalityName = municipalityName,
        streetAddress = streetAddress,
        streetAddressSv = streetAddressSv,
        postOffice = postOffice,
        postOfficeSv = postOfficeSv,
        postalCode = postalCode,
        businessId = businessId,
        discountPercentage = discountPercentage,
    )
