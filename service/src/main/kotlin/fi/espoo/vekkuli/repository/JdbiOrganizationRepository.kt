package fi.espoo.vekkuli.repository

import fi.espoo.vekkuli.domain.CitizenWithDetails
import fi.espoo.vekkuli.domain.Organization
import fi.espoo.vekkuli.utils.DbUtil.Companion.updateTable
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.mapTo
import org.jdbi.v3.core.kotlin.withHandleUnchecked
import org.springframework.stereotype.Repository
import java.util.*

@Repository
class JdbiOrganizationRepository(
    private val jdbi: Jdbi
) : OrganizationRepository {
    override fun getCitizenOrganizations(citizenId: UUID): List<Organization> =
        jdbi.withHandleUnchecked { handle ->
            handle
                .createQuery(
                    """
                    SELECT o.business_id, o.billing_name, o.billing_street_address, o.billing_postal_code, o.billing_post_office, r.*, m.name as municipality_name
                    FROM organization_member om
                    JOIN organization o on om.organization_id = o.id
                    JOIN reserver r on om.organization_id = r.id
                    JOIN municipality m ON r.municipality_code = m.code
                    WHERE om.member_id = :citizenId
                    """.trimIndent()
                ).bind("citizenId", citizenId)
                .mapTo<Organization>()
                .toList()
        }

    override fun getOrganizationMembers(organizationId: UUID): List<CitizenWithDetails> =
        jdbi.withHandleUnchecked { handle ->
            handle
                .createQuery(
                    """
                    SELECT c.national_id, c.first_name, c.last_name, r.*, m.name as municipality_name 
                    FROM organization_member om
                    JOIN citizen c on om.member_id = c.id
                    JOIN reserver r on om.member_id = r.id
                    JOIN municipality m ON r.municipality_code = m.code
                    WHERE om.organization_id = :organizationId
                    """.trimIndent()
                ).bind("organizationId", organizationId)
                .mapTo<CitizenWithDetails>()
                .toList()
        }

    override fun addCitizenToOrganization(
        organizationId: UUID,
        citizenId: UUID
    ): Unit =
        jdbi.withHandleUnchecked { handle ->
            handle
                .createUpdate(
                    """
                    INSERT INTO organization_member (organization_id, member_id)
                    VALUES (:organizationId, :citizenId)
                    ON CONFLICT DO NOTHING
                    """.trimIndent()
                ).bind("organizationId", organizationId)
                .bind("citizenId", citizenId)
                .execute()
        }

    override fun removeCitizenFromOrganization(
        organizationId: UUID,
        citizenId: UUID
    ): Unit =
        jdbi.withHandleUnchecked { handle ->
            handle
                .createUpdate(
                    """
                    DELETE FROM organization_member
                    WHERE organization_id = :organizationId AND member_id = :citizenId
                    """.trimIndent()
                ).bind("organizationId", organizationId)
                .bind("citizenId", citizenId)
                .execute()
        }

    override fun insertOrganization(
        businessId: String,
        billingName: String,
        billingStreetAddress: String,
        billingPostalCode: String,
        billingPostOffice: String,
        name: String,
        phone: String,
        email: String,
        streetAddress: String,
        streetAddressSv: String,
        postalCode: String,
        postOffice: String,
        postOfficeSv: String,
        municipalityCode: Int
    ): Organization =
        jdbi.withHandleUnchecked { handle ->
            val id = UUID.randomUUID()
            handle
                .createUpdate(
                    """
                    INSERT INTO reserver (id, type, name, email, phone, municipality_code, street_address, street_address_sv, postal_code, post_office, post_office_sv)
                    VALUES (:id, 'Organization', :name, :email, :phone, :municipalityCode, :streetAddress, :streetAddressSv, :postalCode, :postOffice, :postOfficeSv)
                    """.trimIndent()
                ).bind("id", id)
                .bind("name", name)
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
                    INSERT INTO organization (id, business_id, billing_name, billing_street_address, billing_postal_code, billing_post_office)
                    VALUES (:id, :businessId, :billingName, :billingStreetAddress, :billingPostalCode, :billingPostOffice)
                    """.trimIndent()
                ).bind("id", id)
                .bind("businessId", businessId)
                .bind("billingName", billingName)
                .bind("billingStreetAddress", billingStreetAddress)
                .bind("billingPostalCode", billingPostalCode)
                .bind("billingPostOffice", billingPostOffice)
                .execute()

            getOrganizationById(id)!!
        }

    override fun updateOrganization(params: UpdateOrganizationParams) {
        val orgParams = mutableMapOf<String, Any?>()

        if (params.businessId != null) {
            orgParams["business_id"] = params.businessId
        }
        if (params.billingName != null) {
            orgParams["billing_name"] = params.billingName
        }
        if (params.billingStreetAddress != null) {
            orgParams["billing_street_address"] = params.billingStreetAddress
        }
        if (params.billingPostalCode != null) {
            orgParams["billing_postal_code"] = params.billingPostalCode
        }
        if (params.billingPostOffice != null) {
            orgParams["billing_post_office"] = params.billingPostOffice
        }
        if (orgParams.isNotEmpty()) {
            jdbi.withHandleUnchecked { updateTable(it, "organization", params.id, orgParams) }
        }

        val reserverParams = mutableMapOf<String, Any?>()

        if (params.name != null) {
            reserverParams["name"] = params.name
        }
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

    override fun getOrganizationById(id: UUID): Organization? =
        jdbi.withHandleUnchecked { handle ->
            handle
                .createQuery(
                    """
                    SELECT o.business_id, o.billing_name, o.billing_street_address, o.billing_postal_code, o.billing_post_office, r.*, m.name as municipality_name
                    FROM organization o
                    JOIN reserver r ON r.id = o.id
                    JOIN municipality m ON r.municipality_code = m.code
                    WHERE o.id = :id
                    """.trimIndent()
                ).bind("id", id)
                .mapTo<Organization>()
                .firstOrNull()
        }

    override fun getOrganizationsByBusinessId(businessId: String): List<Organization> =
        jdbi.withHandleUnchecked { handle ->
            handle
                .createQuery(
                    """
                    SELECT o.business_id, o.billing_name, o.billing_street_address, o.billing_postal_code, o.billing_post_office, r.*, m.name as municipality_name
                    FROM organization o
                    JOIN reserver r ON r.id = o.id
                    JOIN municipality m ON r.municipality_code = m.code
                    WHERE o.business_id = :businessId
                    """.trimIndent()
                ).bind("businessId", businessId)
                .mapTo<Organization>()
                .list()
        }
}
