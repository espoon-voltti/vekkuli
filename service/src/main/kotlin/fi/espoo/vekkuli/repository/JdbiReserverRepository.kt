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
    val dataProtection: Boolean? = null
)

@Repository
class JdbiReserverRepository(
    private val jdbi: Jdbi
) : ReserverRepository {
    override fun getReserverById(id: UUID): ReserverWithDetails? =
        jdbi.withHandleUnchecked { handle ->
            handle
                .createQuery(
                    """
                    SELECT 
                        r.id,
                        r.name,
                        r.type,
                        r.email,
                        r.phone,
                        r.municipality_code,
                        m.name as municipality_name,
                        r.street_address,
                        r.street_address_sv,
                        r.post_office,
                        r.post_office_sv,
                        r.postal_code,
                        r.espoo_rules_applied,
                        r.discount_percentage,
                        r.exception_notes
                    FROM reserver r
                    JOIN municipality m ON r.municipality_code = m.code
                    WHERE r.id = :id
                    """.trimIndent()
                ).bind("id", id)
                .mapTo<ReserverWithDetails>()
                .firstOrNull()
        }

    override fun getCitizenById(id: UUID): CitizenWithDetails? =
        jdbi.withHandleUnchecked { handle ->
            handle
                .createQuery(
                    """    
                    SELECT c.first_name, c.last_name, c.national_id, c.data_protection, r.*, m.name as municipality_name
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
                    SELECT c.first_name, c.last_name, c.national_id, c.data_protection, r.*, m.name as municipality_name
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
            val query =
                handle.createQuery(
                    """
                    SELECT c.first_name, c.last_name, c.national_id, c.data_protection, r.*, m.name as municipality_name 
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
        if (params.dataProtection != null) {
            citizenParams["data_protection"] = params.dataProtection
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
        municipalityCode: Int,
        dataProtection: Boolean
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
                    INSERT INTO citizen (id, national_id, first_name, last_name, data_protection)
                    VALUES (:id, :nationalId, :firstName, :lastName, :dataProtection)
                    """.trimIndent()
                ).bind("id", id)
                .bind("nationalId", nationalId)
                .bind("firstName", firstName)
                .bind("lastName", lastName)
                .bind("dataProtection", dataProtection)
                .execute()

            getCitizenByNationalId(nationalId)!!
        }

    override fun upsertCitizenUserFromAd(adUser: CitizenAdUser): CitizenWithDetails {
        val municipality = getMunicipality(adUser.municipalityCode)
        val municipalityCode = municipality?.code ?: 1
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
                    municipalityCode = municipalityCode,
                    dataProtection = adUser.dataProtection,
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
                municipalityCode = municipalityCode,
                dataProtection = adUser.dataProtection,
            )
        }
    }

    override fun getMunicipalities(): List<Municipality> =
        jdbi.withHandleUnchecked { handle ->
            handle
                .createQuery(
                    """
                    SELECT * FROM municipality
                    """.trimIndent()
                ).mapTo<Municipality>()
                .toList()
        }

    override fun getMunicipality(code: Int): Municipality? =
        jdbi.withHandleUnchecked { handle ->
            handle
                .createQuery(
                    """
                    SELECT * FROM municipality
                    WHERE code = :code
                    """.trimIndent()
                ).bind("code", code)
                .mapTo<Municipality>()
                .firstOrNull()
        }

    override fun toggleEspooRulesApplied(reserverId: UUID): ReserverWithDetails? =
        jdbi.withHandleUnchecked { handle ->
            val query =
                handle.createQuery(
                    """
                    UPDATE reserver r
                    SET espoo_rules_applied = NOT espoo_rules_applied
                    FROM municipality m 
                    WHERE r.municipality_code = m.code
                    AND r.id = :id
                    RETURNING r.*, m.name as municipality_name
                    """.trimIndent()
                )
            query.bind("id", reserverId)
            query.mapTo<ReserverWithDetails>().one()
        }

    override fun updateDiscount(
        reserverId: UUID,
        discountPercentage: Int
    ): ReserverWithDetails? =
        jdbi.withHandleUnchecked { handle ->
            val query =
                handle.createQuery(
                    """
                    UPDATE reserver r
                    SET discount_percentage = :discountPercentage
                    FROM municipality m 
                    WHERE r.municipality_code = m.code
                    AND r.id = :id
                    RETURNING r.*, m.name as municipality_name
                    """.trimIndent()
                )
            query.bind("discountPercentage", discountPercentage)
            query.bind("id", reserverId)
            query.mapTo<ReserverWithDetails>().one()
        }

    override fun updateExceptions(
        reserverId: UUID,
        rulesApplied: Boolean,
        exceptionNotes: String?,
        discountPercentage: Int
    ): ReserverWithDetails? =
        jdbi.withHandleUnchecked { handle ->
            val query =
                handle.createQuery(
                    """
                    UPDATE reserver r
                    SET espoo_rules_applied = :rulesApplied,
                        exception_notes = :exceptionNotes,
                        discount_percentage = :discountPercentage
                    FROM municipality m 
                    WHERE r.municipality_code = m.code
                    AND r.id = :id
                    RETURNING r.*, m.name as municipality_name
                    """.trimIndent()
                )
            query.bind("rulesApplied", rulesApplied)
            query.bind("exceptionNotes", exceptionNotes)
            query.bind("discountPercentage", discountPercentage)
            query.bind("id", reserverId)
            query.mapTo<ReserverWithDetails>().one()
        }
}
