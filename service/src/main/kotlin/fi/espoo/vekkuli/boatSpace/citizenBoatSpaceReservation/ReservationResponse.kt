package fi.espoo.vekkuli.boatSpace.citizenBoatSpaceReservation

import fi.espoo.vekkuli.boatSpace.boatSpaceSwitch.BoatSpaceSwitchService
import fi.espoo.vekkuli.boatSpace.seasonalService.SeasonalService
import fi.espoo.vekkuli.common.NotFound
import fi.espoo.vekkuli.domain.*
import fi.espoo.vekkuli.service.*
import fi.espoo.vekkuli.utils.formatInt
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
    val revisedPrice: String,
    val storageType: StorageType?,
    val trailer: Trailer?,
    val creationType: CreationType,
    val canRenew: Boolean,
    val canSwitch: Boolean,
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
    private val organizationService: OrganizationService,
    private val boatSpaceSwitchService: BoatSpaceSwitchService,
    private val seasonalService: SeasonalService,
    private val permissionService: PermissionService
) {
    fun toReservationResponse(reservation: BoatSpaceReservation): ReservationResponse =
        reservationResponse(
            reservation.id,
            reservation.actingCitizenId,
            reservation.reserverId,
            reservation.status,
            reservation.created,
            reservation.startDate,
            reservation.validity,
            reservation.endDate,
            reservation.paymentDate,
        )

    fun toReservationResponse(reservation: ReservationWithDependencies): ReservationResponse =
        reservationResponse(
            reservation.id,
            reservation.actingCitizenId,
            reservation.reserverId,
            reservation.status,
            reservation.created,
            reservation.startDate,
            reservation.validity,
            reservation.endDate,
            null
        )

    private fun reservationResponse(
        reservationId: Int,
        actingCitizenId: UUID?,
        reserverId: UUID?,
        status: ReservationStatus,
        created: LocalDateTime,
        startDate: LocalDate,
        validity: ReservationValidity,
        endDate: LocalDate,
        paymentDate: LocalDate?
    ): ReservationResponse {
        val reservationWithDependencies =
            spaceReservationService.getReservationWithDependencies(reservationId) ?: throw NotFound()
        val citizen =
            if (reservationWithDependencies.reserverType ==
                ReserverType.Citizen
            ) {
                getCitizen(actingCitizenId, reserverId)
            } else {
                null
            }
        val organization =
            if (reservationWithDependencies.reserverType ==
                ReserverType.Organization
            ) {
                getOrganization(reservationWithDependencies)
            } else {
                null
            }
        val boat = getBoat(reservationWithDependencies)
        val boatSpace = getBoatSpace(reservationWithDependencies)
        val trailer = getTrailer(reservationWithDependencies)
        val revisedPrice = getRevisedPrice(reservationWithDependencies)

        return ReservationResponse(
            id = reservationId,
            reserverType = reservationWithDependencies.reserverType ?: ReserverType.Citizen,
            citizen = formatCitizen(citizen),
            organization = formatOrganization(organization),
            boat = formatBoat(boat),
            boatSpace = formatBoatSpace(boatSpace),
            status = status,
            created = created,
            startDate = startDate,
            validity = validity,
            endDate = endDate,
            totalPrice = reservationWithDependencies.priceInEuro,
            vatValue = reservationWithDependencies.vatPriceInEuro,
            netPrice = reservationWithDependencies.priceWithoutVatInEuro,
            revisedPrice = revisedPrice,
            storageType = reservationWithDependencies.storageType,
            trailer = formatTrailer(trailer),
            paymentDate = paymentDate,
            creationType = reservationWithDependencies.creationType,
            canRenew = seasonalService.canRenewAReservation(reservationId).success,
            canSwitch = seasonalService.canSwitchReservation(reserverId, boatSpace.type, reservationId).success,
        )
    }

    private fun getCitizen(
        actingCitizenId: UUID?,
        reserverId: UUID?
    ): CitizenWithDetails {
        val citizenId = actingCitizenId ?: reserverId

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
            birthDate = citizen.birthdayAsDate
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

    private fun getBoatSpace(reservation: ReservationWithDependencies): BoatSpace =
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

    // This can be a function to get the revised price of a reservation, eg. for switch or because of discounts
    private fun getRevisedPrice(reservation: ReservationWithDependencies): String =
        when (reservation.creationType) {
            CreationType.Switch -> {
                formatInt(boatSpaceSwitchService.getRevisedPrice(reservation))
            }

            else -> {
                reservation.priceInEuro
            }
        }
}
