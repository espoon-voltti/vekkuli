package fi.espoo.vekkuli.domain

import java.util.*

data class Organization(
    // Fields from Reserver
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
    val espooRulesApplied: Boolean,
    val discountPercentage: Int,
    // Fields for Organization
    val businessId: String,
    val billingName: String,
    val billingStreetAddress: String,
    val billingPostalCode: String,
    val billingPostOffice: String,
)

fun Organization.toReserverWithDetails() =
    ReserverWithDetails(
        id = id,
        name = name,
        type = ReserverType.Organization,
        email = email,
        phone = phone,
        municipalityCode = municipalityCode,
        municipalityName = municipalityName,
        streetAddress = streetAddress,
        streetAddressSv = streetAddressSv,
        postOffice = postOffice,
        postOfficeSv = postOfficeSv,
        postalCode = postalCode,
        espooRulesApplied = espooRulesApplied,
        discountPercentage = discountPercentage
    )
