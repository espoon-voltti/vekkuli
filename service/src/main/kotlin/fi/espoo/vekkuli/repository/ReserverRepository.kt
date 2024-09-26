package fi.espoo.vekkuli.repository

import fi.espoo.vekkuli.domain.*
import java.util.*

interface ReserverRepository {
    fun getReserverById(id: UUID): ReserverWithDetails

    fun getCitizenById(id: UUID): CitizenWithDetails?

    fun searchCitizens(nameSearch: String?): List<CitizenWithDetails>

    fun getCitizenByNationalId(nationalId: String): CitizenWithDetails?

    fun getOrganizationById(id: UUID): Organization?

    fun getOrganizationByBusinessId(businessId: String): Organization?

    fun insertCitizen(
        nationalId: String,
        firstName: String,
        lastName: String,
        phone: String,
        email: String,
        streetAddress: String,
        streetAddressSv: String,
        postalCode: String,
        postOffice: String,
        postOfficeSv: String,
        municipalityCode: Int
    ): CitizenWithDetails

    fun updateCitizen(params: UpdateCitizenParams): Unit

    fun insertOrganization(
        businessId: String,
        name: String,
        phone: String,
        email: String,
        streetAddress: String,
        streetAddressSv: String,
        postalCode: String,
        postOffice: String,
        postOfficeSv: String,
        municipalityCode: Int
    ): Organization

    fun upsertCitizenUserFromAd(adUser: CitizenAdUser): CitizenWithDetails
}
