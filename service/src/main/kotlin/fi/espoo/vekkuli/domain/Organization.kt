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
    // Fields for Organization
    val businessId: String,
    val billingName: String,
    val billingStreetAddress: String,
    val billingPostalCode: String,
    val billingPostOffice: String,
)
