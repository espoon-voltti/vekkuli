package fi.espoo.vekkuli.boatSpace.citizenBoatSpaceReservation

import fi.espoo.vekkuli.common.NotFound
import fi.espoo.vekkuli.domain.*
import fi.espoo.vekkuli.service.*
import fi.espoo.vekkuli.utils.intToDecimal
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

data class ReservationResponse(
    val id: Int,
    val reserverType: ReserverType,
    val citizen: Citizen?,
    val organization: Organization?,
    val boatSpace: BoatSpace,
    val boat: Boat?,
    val status: ReservationStatus,
    val created: LocalDateTime,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val validity: ReservationValidity,
    val paymentDate: LocalDate?,
    val totalPrice: String,
    val vatValue: String,
    val netPrice: String,
    val storageType: StorageType?,
    val trailer: Trailer? = null,
    val totalPriceInCents: Int,
) {
    data class Citizen(
        val id: UUID,
        val firstName: String,
        val lastName: String,
        val email: String,
        val phone: String,
        val address: String,
        val postalCode: String,
        val postalOffice: String,
        val city: String,
        val municipalityCode: Int,
        val birthDate: LocalDate,
        val discountPercentage: Int,
    )

    data class Organization(
        val id: UUID,
        val name: String,
        val businessId: String,
        val municipalityCode: Int,
        val phone: String,
        val email: String,
        val address: String? = null,
        val postalCode: String? = null,
        val city: String? = null,
        val discountPercentage: Int,
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

    data class BoatSpace(
        val id: Int,
        val type: BoatSpaceType,
        val section: String,
        val placeNumber: Int,
        val amenity: BoatSpaceAmenity,
        val width: BigDecimal,
        val length: BigDecimal,
        val description: String,
        val excludedBoatTypes: List<BoatType>? = null,
        val locationName: String?,
    )

    data class Trailer(
        val id: Int,
        val registrationNumber: String,
        val width: BigDecimal,
        val length: BigDecimal,
    )
}

@Service
class ReservationResponseMapper(
    private val boatService: BoatService,
    private val spaceReservationService: BoatReservationService,
    private val reserverService: ReserverService,
    private val organizationService: OrganizationService
) {
    fun toReservationResponse(reservation: BoatSpaceReservation): ReservationResponse {
        val reservationWithDependencies = spaceReservationService.getReservationWithDependencies(reservation.id) ?: throw NotFound()
        val citizen = if (reservationWithDependencies.reserverType == ReserverType.Citizen) getCitizen(reservation) else null
        val organization =
            if (reservationWithDependencies.reserverType ==
                ReserverType.Organization
            ) {
                getOrganization(reservationWithDependencies)
            } else {
                null
            }
        val boat = getBoat(reservationWithDependencies)
        val boatSpace = getBoatSpace(reservation)
        val trailer = getTrailer(reservationWithDependencies)

        return ReservationResponse(
            id = reservation.id,
            reserverType = reservationWithDependencies.reserverType ?: ReserverType.Citizen,
            citizen = formatCitizen(citizen),
            organization = formatOrganization(organization),
            boat = formatBoat(boat),
            boatSpace = formatBoatSpace(boatSpace),
            status = reservation.status,
            created = reservation.created,
            startDate = reservation.startDate,
            validity = reservation.validity,
            endDate = reservation.endDate,
            totalPrice = reservationWithDependencies.priceInEuro,
            vatValue = reservationWithDependencies.vatPriceInEuro,
            netPrice = reservationWithDependencies.priceWithoutVatInEuro,
            storageType = reservationWithDependencies.storageType,
            trailer = formatTrailer(trailer),
            paymentDate = reservation.paymentDate,
            totalPriceInCents = reservationWithDependencies.priceCents
        )
    }

    private fun getCitizen(reservation: BoatSpaceReservation): CitizenWithDetails {
        val citizenId = reservation.actingCitizenId ?: reservation.reserverId

        if (citizenId == null) {
            throw NotFound()
        }

        return reserverService.getCitizen(citizenId) ?: throw NotFound()
    }

    private fun formatCitizen(citizen: CitizenWithDetails?): ReservationResponse.Citizen? {
        if (citizen == null) {
            return null
        }
        return ReservationResponse.Citizen(
            id = citizen.id,
            firstName = citizen.firstName,
            lastName = citizen.lastName,
            email = citizen.email,
            phone = citizen.phone,
            address = citizen.streetAddress,
            postalCode = citizen.postalCode,
            postalOffice = citizen.postOffice,
            city = citizen.municipalityName,
            municipalityCode = citizen.municipalityCode,
            birthDate = citizen.birthdayAsDate,
            discountPercentage = citizen.discountPercentage,
        )
    }

    private fun getOrganization(reservation: ReservationWithDependencies): Organization? {
        if (reservation.reserverType != ReserverType.Organization) {
            return null
        }

        if (reservation.reserverId == null) {
            throw NotFound()
        }

        return organizationService.getOrganizationById(reservation.reserverId) ?: throw NotFound()
    }

    private fun formatOrganization(organization: Organization?): ReservationResponse.Organization? {
        if (organization == null) {
            return null
        }

        return ReservationResponse.Organization(
            id = organization.id,
            name = organization.name,
            businessId = organization.businessId,
            municipalityCode = organization.municipalityCode,
            phone = organization.phone,
            email = organization.email,
            address = organization.streetAddress,
            postalCode = organization.postalCode,
            city = organization.postOffice,
            discountPercentage = organization.discountPercentage,
        )
    }

    private fun getBoat(reservation: ReservationWithDependencies): Boat? {
        if (reservation.boatId == null) {
            return null
        }

        return boatService.getBoat(reservation.boatId) ?: throw NotFound()
    }

    private fun formatBoat(boat: Boat?): ReservationResponse.Boat? {
        if (boat == null) {
            return null
        }

        return ReservationResponse.Boat(
            id = boat.id,
            name = boat.name ?: "",
            type = boat.type,
            width = intToDecimal(boat.widthCm),
            length = intToDecimal(boat.lengthCm),
            depth = intToDecimal(boat.depthCm),
            weight = boat.weightKg,
            registrationNumber = boat.registrationCode ?: "",
            hasNoRegistrationNumber = boat.registrationCode == null,
            otherIdentification = boat.otherIdentification ?: "",
            extraInformation = boat.extraInformation,
            ownership = boat.ownership,
        )
    }

    private fun getBoatSpace(reservation: BoatSpaceReservation): BoatSpace =
        spaceReservationService.getBoatSpaceRelatedToReservation(reservation.id) ?: throw NotFound()

    private fun formatBoatSpace(boatSpace: BoatSpace): ReservationResponse.BoatSpace =
        ReservationResponse.BoatSpace(
            id = boatSpace.id,
            type = boatSpace.type,
            section = boatSpace.section,
            placeNumber = boatSpace.placeNumber,
            amenity = boatSpace.amenity,
            width = intToDecimal(boatSpace.widthCm),
            length = intToDecimal(boatSpace.lengthCm),
            description = boatSpace.description,
            excludedBoatTypes = boatSpace.excludedBoatTypes,
            locationName = boatSpace.locationName
        )

    private fun getTrailer(reservation: ReservationWithDependencies): Trailer? {
        if (reservation.trailerId == null) {
            return null
        }
        return spaceReservationService.getTrailer(reservation.trailerId) ?: throw NotFound()
    }

    private fun formatTrailer(trailer: Trailer?): ReservationResponse.Trailer? {
        if (trailer == null) {
            return null
        }
        return ReservationResponse.Trailer(
            id = trailer.id,
            registrationNumber = trailer.registrationCode ?: "",
            width = intToDecimal(trailer.widthCm),
            length = intToDecimal(trailer.lengthCm),
        )
    }
}
