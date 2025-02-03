package fi.espoo.vekkuli.boatSpace.citizen

import fi.espoo.vekkuli.domain.CitizenWithDetails

data class CitizenOrganizationContactDetail(
    val name: String,
    val email: String,
    val phone: String,
)

typealias CitizenOrganizationContactDetailsResponse = List<CitizenOrganizationContactDetail>

fun List<CitizenWithDetails>.toCitizenOrganizationContactDetailsResponse() = map { it.toCitizenOrganizationContactDetailResponse() }

fun CitizenWithDetails.toCitizenOrganizationContactDetailResponse() =
    CitizenOrganizationContactDetail(
        name = listOf<String>(firstName, lastName).filter { it.isNotEmpty() }.joinToString(" "),
        email = email,
        phone = phone,
    )
