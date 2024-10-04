package fi.espoo.vekkuli.repository

import fi.espoo.vekkuli.domain.*
import fi.espoo.vekkuli.utils.DbUtil.Companion.buildNameSearchClause
import fi.espoo.vekkuli.utils.DbUtil.Companion.updateTable
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.mapTo
import org.jdbi.v3.core.kotlin.withHandleUnchecked
import org.springframework.stereotype.Repository
import java.util.*

data class UpdateCitizenParams(
    val id: UUID,
    val nationalId: String? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val phone: String? = null,
    val email: String? = null,
    val streetAddress: String? = null,
    val postalCode: String? = null,
    val streetAddressSv: String? = null,
    val postOffice: String? = null,
    val postOfficeSv: String? = null,
    val municipalityCode: Int? = null,
)

@Repository
class JdbiReserverRepository(
    private val jdbi: Jdbi
) : ReserverRepository {
    override fun getReserverById(id: UUID): ReserverWithDetails =
        jdbi.withHandleUnchecked { handle ->
            handle
                .createQuery(
                    """
                    SELECT *, m.name as municipality_name
                    FROM reserver r
                    JOIN municipality m ON r.municipality_code = m.code
                    WHERE r.id = :id
                    """.trimIndent()
                ).bind("id", id)
                .mapTo<ReserverWithDetails>()
                .one()
        }

    override fun getCitizenById(id: UUID): CitizenWithDetails? =
        jdbi.withHandleUnchecked { handle ->
            handle
                .createQuery(
                    """    
                    SELECT c.first_name, c.last_name, c.national_id, r.*, m.name as municipality_name
                    FROM citizen c
                    JOIN reserver r ON r.id = c.id
                    JOIN municipality m ON r.municipality_code = m.code
                    WHERE c.id = :id
                    """.trimIndent()
                ).bind("id", id)
                .mapTo<CitizenWithDetails>()
                .firstOrNull()
        }

    override fun getCitizenByNationalId(nationalId: String): CitizenWithDetails? =
        jdbi.withHandleUnchecked { handle ->
            handle
                .createQuery(
                    """    
                    SELECT c.first_name, c.last_name, c.national_id, r.*, m.name as municipality_name
                    FROM citizen c
                    JOIN reserver r ON r.id = c.id
                    JOIN municipality m ON r.municipality_code = m.code
                    WHERE c.national_id = :nationalId
                    """.trimIndent()
                ).bind("nationalId", nationalId)
                .mapTo<CitizenWithDetails>()
                .firstOrNull()
        }

    override fun searchCitizens(nameSearch: String?): List<CitizenWithDetails> =
        jdbi.withHandleUnchecked { handle ->
            val nameSearchClause =
                buildNameSearchClause(nameSearch)
            var query =
                handle.createQuery(
                    """
                    SELECT c.first_name, c.last_name, c.national_id, r.*, m.name as municipality_name 
                    FROM citizen c
                    JOIN reserver r ON r.id = c.id
                    JOIN municipality m ON r.municipality_code = m.code
                    WHERE $nameSearchClause
                    """.trimIndent()
                )
            if (!nameSearch.isNullOrEmpty()) {
                query.bind("nameSearch", nameSearch.trim())
            }
            query.mapTo<CitizenWithDetails>().toList()
        }

    override fun getOrganizationById(id: UUID): Organization? =
        jdbi.withHandleUnchecked { handle ->
            handle
                .createQuery(
                    """
                    SELECT o.*, r.*, m.name as municipality_name
                    FROM organization o
                    JOIN reserver r ON r.id = o.id
                    JOIN municipality m ON r.municipality_code = m.code
                    WHERE o.id = :id
                    """.trimIndent()
                ).bind("id", id)
                .mapTo<Organization>()
                .firstOrNull()
        }

    override fun updateCitizen(params: UpdateCitizenParams) {
        val citizenParams = mutableMapOf<String, Any?>()

        if (params.nationalId != null) {
            citizenParams["national_id"] = params.nationalId
        }
        if (params.firstName != null) {
            citizenParams["first_name"] = params.firstName
        }
        if (params.lastName != null) {
            citizenParams["last_name"] = params.lastName
        }
        if (citizenParams.isNotEmpty()) {
            jdbi.withHandleUnchecked { updateTable(it, "citizen", params.id, citizenParams) }
        }

        val reserverParams = mutableMapOf<String, Any?>()

        if (params.phone != null) {
            reserverParams["phone"] = params.phone
        }
        if (params.email != null) {
            reserverParams["email"] = params.email
        }
        if (params.streetAddress != null) {
            reserverParams["street_address"] = params.streetAddress
        }
        if (params.streetAddressSv != null) {
            reserverParams["street_address_sv"] = params.streetAddressSv
        }
        if (params.postalCode != null) {
            reserverParams["postal_code"] = params.postalCode
        }
        if (params.postOffice != null) {
            reserverParams["post_office"] = params.postOffice
        }
        if (params.postOfficeSv != null) {
            reserverParams["post_office_sv"] = params.postOfficeSv
        }
        if (params.municipalityCode != null) {
            reserverParams["municipality_code"] = params.municipalityCode
        }

        if (reserverParams.isNotEmpty()) {
            jdbi.withHandleUnchecked { updateTable(it, "reserver", params.id, reserverParams) }
        }
    }

    override fun insertCitizen(
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
    ): CitizenWithDetails =
        jdbi.withHandleUnchecked { handle ->
            val id = UUID.randomUUID()
            handle
                .createUpdate(
                    """
                    INSERT INTO reserver (id, type, email, phone, municipality_code, street_address, street_address_sv, postal_code, post_office, post_office_sv)
                    VALUES (:id, 'Citizen', :email, :phone, :municipalityCode, :streetAddress, :streetAddressSv, :postalCode, :postOffice, :postOfficeSv)
                    """.trimIndent()
                ).bind("id", id)
                .bind("email", email)
                .bind("phone", phone)
                .bind("municipalityCode", municipalityCode)
                .bind("streetAddress", streetAddress)
                .bind("streetAddressSv", streetAddressSv)
                .bind("postalCode", postalCode)
                .bind("postOffice", postOffice)
                .bind("postOfficeSv", postOfficeSv)
                .execute()

            handle
                .createUpdate(
                    """
                    INSERT INTO citizen (id, national_id, first_name, last_name)
                    VALUES (:id, :nationalId, :firstName, :lastName)
                    """.trimIndent()
                ).bind("id", id)
                .bind("nationalId", nationalId)
                .bind("firstName", firstName)
                .bind("lastName", lastName)
                .execute()

            getCitizenByNationalId(nationalId)!!
        }

    override fun upsertCitizenUserFromAd(adUser: CitizenAdUser): CitizenWithDetails {
        val existingCitizen = getCitizenByNationalId(adUser.nationalId)

        return if (existingCitizen != null) {
            updateCitizen(
                UpdateCitizenParams(
                    id = existingCitizen.id,
                    nationalId = adUser.nationalId,
                    firstName = adUser.firstName,
                    lastName = adUser.lastName,
                    phone = adUser.phone,
                    email = adUser.email,
                    streetAddress = adUser.address.fi ?: "",
                    streetAddressSv = adUser.address.sv ?: "",
                    postalCode = adUser.postalCode ?: "",
                    postOffice = adUser.postOffice.fi ?: "",
                    postOfficeSv = adUser.postOffice.sv ?: "",
                )
            )
            getCitizenByNationalId(adUser.nationalId)!!
        } else {
            insertCitizen(
                nationalId = adUser.nationalId,
                firstName = adUser.firstName,
                lastName = adUser.lastName,
                phone = adUser.phone ?: "",
                email = adUser.email ?: "",
                streetAddress = adUser.address.fi ?: "",
                streetAddressSv = adUser.address.sv ?: "",
                postalCode = adUser.postalCode ?: "",
                postOffice = adUser.postOffice.fi ?: "",
                postOfficeSv = adUser.postOffice.sv ?: "",
                municipalityCode = adUser.municipalityCode
            )
        }
    }
}
