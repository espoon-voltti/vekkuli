package fi.espoo.vekkuli.boatSpace.citizenBoatSpaceReservation

import fi.espoo.vekkuli.common.NotFound
import fi.espoo.vekkuli.domain.*
import fi.espoo.vekkuli.service.BoatReservationService
import fi.espoo.vekkuli.service.BoatSpaceRepository
import fi.espoo.vekkuli.utils.intToDecimal
import org.springframework.stereotype.Service
import java.math.BigDecimal

enum class CanReserveResultStatus {
    CanReserve,
    CanNotReserve,
    NotAvailable,
    CanReserveOnlyForOrganization,
}

data class SwitchableOrganizationReservation(
    val organizationName: String?,
    val reservations: List<BoatSpaceReservationDetails>
)

data class CanReserveResult(
    val status: CanReserveResultStatus,
    val switchableReservations: List<BoatSpaceReservationDetails>,
    val switchableOrganization: List<SwitchableOrganizationReservation>
)

data class CanReserveResponse(
    val status: CanReserveResultStatus,
    val switchableReservations: List<SwitchableReservation>,
    val switchableOrganizationReservations: List<SwitchableOrganizationReservation>
) {
    data class SwitchableOrganizationReservation(
        val organizationName: String?,
        val reservations: List<SwitchableReservation>
    )

    data class SwitchableReservation(
        val id: Int,
        val boatSpace: BoatSpace,
        val totalPrice: String,
        val vatValue: String,
    )

    data class BoatSpace(
        val id: Int,
        val type: BoatSpaceType,
        val placeNumber: Int,
        val section: String,
        val locationName: String?,
        val width: BigDecimal,
        val length: BigDecimal,
        val amenity: BoatSpaceAmenity
    )
}

@Service
class CanReserveResponseMapper(
    private val spaceReservationService: BoatReservationService,
    private val boatSpaceRepository: BoatSpaceRepository
) {
    fun toCanReserveResponse(canReserveResult: CanReserveResult): CanReserveResponse =
        CanReserveResponse(
            status = canReserveResult.status,
            switchableReservations =
                canReserveResult.switchableReservations.map {
                    val boatSpace = getBoatSpace(it.boatSpaceId)
                    CanReserveResponse.SwitchableReservation(
                        id = it.id,
                        boatSpace = formatBoatSpace(boatSpace),
                        totalPrice = it.priceInEuro,
                        vatValue = it.vatPriceInEuro
                    )
                },
            switchableOrganizationReservations =
                canReserveResult.switchableOrganization.map {
                    CanReserveResponse.SwitchableOrganizationReservation(
                        organizationName = it.organizationName,
                        reservations =
                            it.reservations.map { reservation ->
                                val boatSpace = getBoatSpace(reservation.boatSpaceId)
                                CanReserveResponse.SwitchableReservation(
                                    id = reservation.id,
                                    boatSpace = formatBoatSpace(boatSpace),
                                    totalPrice = reservation.priceInEuro,
                                    vatValue = reservation.vatPriceInEuro
                                )
                            }
                    )
                }
        )

    private fun getBoatSpace(boatSpaceId: Int): BoatSpace =
        boatSpaceRepository.getBoatSpace(boatSpaceId)
            ?: throw NotFound("Boat space not found")

    private fun formatBoatSpace(boatSpace: BoatSpace): CanReserveResponse.BoatSpace =
        CanReserveResponse.BoatSpace(
            id = boatSpace.id,
            type = boatSpace.type,
            section = boatSpace.section,
            placeNumber = boatSpace.placeNumber,
            locationName = boatSpace.locationName,
            width = intToDecimal(boatSpace.widthCm),
            length = intToDecimal(boatSpace.lengthCm),
            amenity = boatSpace.amenity
        )
}
