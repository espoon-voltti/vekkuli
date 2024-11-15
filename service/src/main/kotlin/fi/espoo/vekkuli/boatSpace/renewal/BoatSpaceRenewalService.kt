package fi.espoo.vekkuli.boatSpace.renewal

import fi.espoo.vekkuli.common.NotFound
import fi.espoo.vekkuli.controllers.UnauthorizedException
import fi.espoo.vekkuli.domain.ReservationStatus
import fi.espoo.vekkuli.domain.ReservationValidity
import fi.espoo.vekkuli.domain.ReservationWithDependencies
import fi.espoo.vekkuli.domain.ReserverType
import fi.espoo.vekkuli.repository.*
import fi.espoo.vekkuli.service.*
import org.springframework.stereotype.Service
import java.util.*

@Service
class BoatSpaceRenewalService(
    private val organizationService: OrganizationService,
    private val reservationService: BoatReservationService,
    private val citizenService: CitizenService,
    private val boatSpaceRenewalRepository: BoatSpaceRenewalRepository,
    private val invoiceService: BoatSpaceInvoiceService,
) {
    fun createRenewalReservationForCitizen() {
//        val renewal = boatSpaceRenewalRepository.getRenewalReservationForCitizen(userId)
//
//        val renewalReservation = renewal ?: reservationService.createRenewalReservationForCitizen(reservationId, userId)
    }

    fun createRenewalReservation(
        userId: UUID,
        reservationId: Int,
    ): ReservationWithDependencies {
        val renewal = boatSpaceRenewalRepository.getRenewalReservationForCitizen(userId)

        val renewalReservation = renewal ?: reservationService.createRenewalReservationForCitizen(reservationId, userId)
        if (renewalReservation == null) throw IllegalStateException("Reservation not found")
        return renewalReservation
    }

    fun updateRenewReservation(
        citizenId: UUID,
        input: RenewalReservationInput,
        reservationId: Int
    ) {
        val reservation =
            reservationService.getReservationWithReserver(reservationId)
                ?: throw NotFound("Reservation not found")

        if (reservation.reserverId == null) {
            throw UnauthorizedException()
        }
        updateReserver(reservation.reserverType, reservation.reserverId, input)

        reservationService.reserveBoatSpace(
            reserverId = reservation.reserverId,
            ReserveBoatSpaceInput(
                reservationId = reservationId,
                boatId = input.boatId,
                boatType = input.boatType!!,
                width = input.width ?: 0.0,
                length = input.length ?: 0.0,
                depth = input.depth ?: 0.0,
                weight = input.weight,
                boatRegistrationNumber = input.boatRegistrationNumber ?: "",
                boatName = input.boatName ?: "",
                otherIdentification = input.otherIdentification ?: "",
                extraInformation = input.extraInformation ?: "",
                ownerShip = input.ownership!!,
                email = input.email!!,
                phone = input.phone!!,
            ),
            ReservationStatus.Payment,
            reservation.validity ?: ReservationValidity.FixedTerm,
            reservation.startDate,
            reservation.endDate
        )
    }

    fun updateReserver(
        reserverType: ReserverType?,
        reserverId: UUID,
        input: RenewalReservationInput,
    ) {
        if (reserverType == ReserverType.Organization) {
            organizationService.updateOrganization(
                UpdateOrganizationParams(
                    id = reserverId,
                    phone = input.orgPhone,
                    email = input.orgEmail
                )
            )
        } else {
            citizenService.updateCitizen(
                UpdateCitizenParams(
                    id = reserverId,
                    phone = input.phone,
                    email = input.email,
                )
            )
        }
    }
//    fun renewBoatSpace(
//        newReservationId: Int,
//        oldReservationId: Int
//    ) {
//        val oldReservation =
//            reservationService.getBoatSpaceReservation(oldReservationId)
//                ?: throw IllegalArgumentException("Old reservation not found")
//        reservationService.reserveBoatSpace(
//            oldReservation.reserverId,
//            ReserveBoatSpaceInput(
//                reservationId = newReservationId,
//                boatId = oldReservation.boatId,
//                boatType = oldReservation.boatType,
//                width = oldReservation.boatWidthInM ?: 0.0,
//                length = oldReservation.boatLengthInM ?: 0.0,
//                depth = oldReservation.boatDepthInM ?: 0.0,
//                weight = oldReservation.boatWeightKg,
//                boatRegistrationNumber = oldReservation.boatRegistrationCode ?: "",
//                boatName = oldReservation.boatName ?: "",
//                otherIdentification = oldReservation.boatOtherIdentification ?: "",
//                extraInformation = oldReservation.boatExtraInformation ?: "",
//                ownerShip = oldReservation.boatOwnership!!,
//                email = oldReservation.email,
//                phone = oldReservation.phone,
//            ),
//            ReservationStatus.Payment,
//            oldReservation.validity,
//            oldReservation.startDate,
//            oldReservation.endDate
//        )

    fun activateRenewalAndSendInvoice(renewedReservationId: Int) {
        val newReservation = reservationService.getReservationWithReserver(renewedReservationId)
        if (newReservation?.reserverId == null) {
            throw IllegalArgumentException("Reservation not found")
        }

        val invoiceBatch =
            invoiceService.createInvoiceBatchParameters(renewedReservationId, newReservation.reserverId)
                ?: throw InternalError("Failed to create invoice batch")

        invoiceService.createAndSendInvoice(invoiceBatch) ?: throw InternalError("Failed to send invoice")

        activateRenewalReservation(renewedReservationId)
    }

    fun activateRenewalReservation(renewedReservationId: Int) {
        // set renewed reservation status to invoiced
        reservationService.setReservationStatusToInvoiced(renewedReservationId)

        // set old reservation end date to yesterday
    }
}
