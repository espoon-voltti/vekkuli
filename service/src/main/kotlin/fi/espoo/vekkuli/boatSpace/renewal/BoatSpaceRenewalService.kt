package fi.espoo.vekkuli.boatSpace.renewal

import fi.espoo.vekkuli.boatSpace.invoice.BoatSpaceInvoiceService
import fi.espoo.vekkuli.boatSpace.invoice.InvoiceController.InvoiceInput
import fi.espoo.vekkuli.boatSpace.reservationForm.UnauthorizedException
import fi.espoo.vekkuli.common.BadRequest
import fi.espoo.vekkuli.common.Conflict
import fi.espoo.vekkuli.common.Forbidden
import fi.espoo.vekkuli.config.MessageUtil
import fi.espoo.vekkuli.controllers.UserType
import fi.espoo.vekkuli.domain.*
import fi.espoo.vekkuli.repository.*
import fi.espoo.vekkuli.service.*
import fi.espoo.vekkuli.utils.decimalToInt
import fi.espoo.vekkuli.utils.intToDecimal
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class BoatSpaceRenewalService(
    private val boatReservationService: BoatReservationService,
    private val boatSpaceRenewalRepository: BoatSpaceRenewalRepository,
    private val invoiceService: BoatSpaceInvoiceService,
    private val boatSpaceReservationRepo: BoatSpaceReservationRepository,
    private val renewalPolicy: RenewalPolicyService,
    private val citizenAccessControl: ContextCitizenAccessControl
) {
    @Transactional
    fun startReservation(reservationId: Int): ReservationWithDependencies {
        val (citizenId) = citizenAccessControl.requireCitizen()

        if (boatReservationService.getUnfinishedReservationForCitizen(citizenId) != null) {
            throw Forbidden("Citizen can not have multiple reservations started")
        }

        if (!renewalPolicy.citizenCanRenewReservation(reservationId, citizenId).success) {
            throw Forbidden("Citizen can not renew reservation")
        }

        return createRenewalReservation(reservationId, citizenId, UserType.CITIZEN)
            ?: throw BadRequest("Reservation not found")
    }

    fun getOrCreateRenewalReservationForEmployee(
        userId: UUID,
        originalReservationId: Int,
    ): ReservationWithDependencies {
        val renewal = boatSpaceRenewalRepository.getRenewalReservationForEmployee(userId, originalReservationId)
        if (renewal != null) return renewal

        val renewalReservation =
            createRenewalReservation(originalReservationId, userId, UserType.EMPLOYEE)
                ?: throw IllegalStateException("Reservation not found")
        return renewalReservation
    }

    fun getRenewalReservationForEmployee(
        userId: UUID,
        reservationId: Int,
    ): ReservationWithDependencies =
        boatSpaceRenewalRepository.getRenewalReservationForEmployee(userId, reservationId)
            ?: throw BadRequest("Reservation not found")

    fun cancelRenewalReservation(
        renewalReservationId: Int,
        citizenId: UUID
    ) {
        boatReservationService.removeBoatSpaceReservation(renewalReservationId, citizenId)
    }

    @Transactional
    fun activateRenewalAndSendInvoice(
        renewedReservationId: Int,
        reserverId: UUID?,
        originalReservationId: Int?,
        input: InvoiceInput,
        employeeId: UUID
    ) {
        if (reserverId == null || originalReservationId == null) {
            throw IllegalArgumentException("Reservation not found")
        }
        val priceWithVatInCents = decimalToInt(input.priceWithTax)
        val invoiceData =
            invoiceService.createInvoiceData(
                renewedReservationId,
                reserverId,
                priceWithVatInCents,
                input.description,
                input.function,
                input.contactPerson
            )
                ?: throw InternalError("Failed to create invoice batch")

        invoiceService.createAndSendInvoice(
            invoiceData,
            reserverId,
            renewedReservationId,
            employeeId,
            markAsPaidAndSkipSending = input.markAsPaid
        )
            ?: throw InternalError("Failed to send invoice")

        boatReservationService.markReservationEnded(originalReservationId)

        // Set correct reservation status: Confirmed if marked as paid, otherwise Invoiced
        if (input.markAsPaid) {
            boatReservationService.setReservationStatusToConfirmed(renewedReservationId)
        } else {
            boatReservationService.setReservationStatusToInvoiced(renewedReservationId)
        }

        boatReservationService.sendReservationEmailAndInsertMemoIfSwitch(renewedReservationId)
    }

    fun createRenewalReservation(
        originalReservationId: Int,
        userId: UUID,
        userType: UserType
    ): ReservationWithDependencies? {
        val reservation =
            boatSpaceReservationRepo.getBoatSpaceReservationDetails(originalReservationId)
                ?: throw BadRequest("Reservation to renew not found")
        if (userType == UserType.EMPLOYEE && !renewalPolicy.employeeCanRenewReservation(reservation.id).success) {
            throw Conflict("Reservation cannot be renewed")
        } else if (userType != UserType.EMPLOYEE && !renewalPolicy.citizenCanRenewReservation(reservation.id, userId).success) {
            throw Conflict("Reservation cannot be renewed")
        }
        val newId = boatSpaceRenewalRepository.createRenewalRow(originalReservationId, userType, userId)
        return boatSpaceReservationRepo.getReservationWithReserverInInfoPaymentRenewalStateWithinSessionTime(newId)
    }
}
