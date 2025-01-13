package fi.espoo.vekkuli.domain

import fi.espoo.vekkuli.config.DomainConstants.ESPOO_MUNICIPALITY_CODE
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
    val discountPercentage: Int
) {
    fun hasExceptions() = espooRulesApplied || discountPercentage > 0

    fun isEspooCitizen() = espooRulesApplied || municipalityCode == ESPOO_MUNICIPALITY_CODE
}
