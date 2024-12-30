package fi.espoo.vekkuli.boatSpace.citizenBoatSpaceReservation

import fi.espoo.vekkuli.domain.BoatType
import fi.espoo.vekkuli.domain.OwnershipStatus
import fi.espoo.vekkuli.domain.StorageType
import java.math.BigDecimal
import java.util.*

data class FillReservationInformationInput(
    val citizen: Citizen,
    val organization: Organization? = null,
    val boat: Boat,
    val certifyInformation: Boolean = false,
    val agreeToRules: Boolean = false,
    val storageType: StorageType? = null,
    val trailer: Trailer? = null,
) {
    data class Citizen(
        val email: String,
        val phone: String,
    )

    data class Organization(
        val id: UUID? = null,
        val name: String,
        val businessId: String,
        val municipalityCode: String,
        val phone: String,
        val email: String,
        val address: String? = null,
        val postalCode: String? = null,
        val city: String? = null,
    )

    data class Boat(
        val id: Int?,
        val name: String,
        val type: BoatType,
        val width: BigDecimal,
        val length: BigDecimal,
        val depth: BigDecimal,
        val weight: Int,
        val registrationNumber: String,
        val hasNoRegistrationNumber: Boolean = false,
        val otherIdentification: String,
        val extraInformation: String? = null,
        val ownership: OwnershipStatus,
    )

    data class Trailer(
        val id: Int?,
        val registrationNumber: String,
        val width: BigDecimal,
        val length: BigDecimal,
    )
}

fun FillReservationInformationInput.toReservationInformation() =
    ReservationInformation(
        citizen =
            ReservationInformation.Citizen(
                email = citizen.email,
                phone = citizen.phone,
            ),
        organization =
            if (organization == null) {
                null
            } else {
                ReservationInformation.Organization(
                    id = organization.id,
                    name = organization.name,
                    businessId = organization.businessId,
                    municipalityCode = organization.municipalityCode,
                    phone = organization.phone,
                    email = organization.email,
                    address = organization.address,
                    postalCode = organization.postalCode,
                    city = organization.city,
                )
            },
        boat =
            ReservationInformation.Boat(
                id = boat.id,
                name = boat.name,
                type = boat.type,
                width = boat.width,
                length = boat.length,
                depth = boat.depth,
                weight = boat.weight,
                registrationNumber = boat.registrationNumber,
                hasNoRegistrationNumber = boat.hasNoRegistrationNumber,
                otherIdentification = boat.otherIdentification,
                extraInformation = boat.extraInformation,
                ownership = boat.ownership,
            ),
        certifyInformation = certifyInformation,
        agreeToRules = agreeToRules,
        storageType = storageType,
        trailer =
            if (trailer == null) {
                null
            } else {
                ReservationInformation.Trailer(
                    id = trailer.id,
                    registrationNumber = trailer.registrationNumber,
                    width = trailer.width,
                    length = trailer.length,
                )
            },
    )
