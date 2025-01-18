package fi.espoo.vekkuli.boatSpace.boatSpaceSwitch

import fi.espoo.vekkuli.boatSpace.citizenBoatSpaceReservation.FillReservationInformationInput
import fi.espoo.vekkuli.boatSpace.invoice.BoatSpaceInvoiceService
import fi.espoo.vekkuli.boatSpace.renewal.ModifyReservationInput
import fi.espoo.vekkuli.boatSpace.renewal.RenewalReservationForApplicationForm
import fi.espoo.vekkuli.boatSpace.reservationForm.ReservationFormService
import fi.espoo.vekkuli.boatSpace.reservationForm.UnauthorizedException
import fi.espoo.vekkuli.boatSpace.seasonalService.SeasonalService
import fi.espoo.vekkuli.common.BadRequest
import fi.espoo.vekkuli.common.Conflict
import fi.espoo.vekkuli.common.NotFound
import fi.espoo.vekkuli.controllers.UserType
import fi.espoo.vekkuli.domain.*
import fi.espoo.vekkuli.repository.*
import fi.espoo.vekkuli.service.*
import fi.espoo.vekkuli.utils.intToDecimal
import fi.espoo.vekkuli.views.employee.SendInvoiceModel
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.util.*

data class BoatSpaceSwitchViewParams(
    val reservation: RenewalReservationForApplicationForm,
    val boats: List<Boat>,
    val citizen: CitizenWithDetails? = null,
    val input: ModifyReservationInput,
    val userType: UserType,
)

@Service
class BoatSpaceSwitchService(
    private val boatSpaceReservationRepo: BoatSpaceReservationRepository,
    private val organizationService: OrganizationService,
    private val seasonalService: SeasonalService,
    private val reserverService: ReserverService,
    private val boatReservationService: BoatReservationService,
    private val boatSpaceSwitchRepository: BoatSpaceSwitchRepository,
    private val reservationService: ReservationFormService,
    private val invoiceService: BoatSpaceInvoiceService,
    private val boatSpaceRepository: BoatSpaceRepository,
) {
    fun getOrCreateSwitchReservationForCitizen(
        reserverId: UUID,
        originalReservationId: Int,
        boatSpaceId: Int
    ): ReservationWithDependencies {
        val reserver = reserverService.getReserverById(reserverId) ?: throw IllegalArgumentException("Reserver not found")
        val original = boatSpaceSwitchRepository.getSwitchReservationForCitizen(reserverId, originalReservationId)
        if (original != null) return original

        val originalReservation =
            createSwitchReservation(
                originalReservationId,
                reserverId,
                UserType.CITIZEN,
                reserver.isEspooCitizen(),
                boatSpaceId
            )
                ?: throw IllegalStateException("Reservation not found")
        return originalReservation
    }

    fun getSwitchReservationForCitizen(
        userId: UUID,
        reservationId: Int,
    ): ReservationWithDependencies =
        boatSpaceSwitchRepository
            .getSwitchReservationForCitizen(userId, reservationId)
            ?: throw BadRequest("Reservation not found")

    fun getSwitchReservationForEmployee(
        userId: UUID,
        reservationId: Int,
    ): ReservationWithDependencies =
        boatSpaceSwitchRepository.getSwitchReservationForEmployee(userId, reservationId)
            ?: throw BadRequest("Reservation not found")

    fun cancelSwitchReservation(
        originalReservationId: Int,
        citizenId: UUID
    ) {
        boatReservationService.removeBoatSpaceReservation(originalReservationId, citizenId)
    }

    fun updateSwitchReservation(
        citizenId: UUID,
        input: FillReservationInformationInput,
        reservationId: Int
    ): ReservationWithDependencies {
        val reservation =
            boatReservationService.getReservationWithReserver(reservationId)
                ?: throw NotFound("Reservation not found")

        if (reservation.reserverId == null) {
            throw UnauthorizedException()
        }
        updateReserver(reservation.reserverType, reservation.reserverId, input)

        reservationService.processBoatSpaceReservation(
            reserverId = reservation.reserverId,
            reservationService.buildReserveBoatSpaceInput(reservationId, input),
            ReservationStatus.Payment,
            reservation.validity,
            reservation.startDate,
            reservation.endDate
        )
        return reservation
    }

    fun updateReserver(
        reserverType: ReserverType?,
        reserverId: UUID,
        input: FillReservationInformationInput,
    ) {
        if (reserverType == ReserverType.Organization) {
            organizationService.updateOrganization(
                UpdateOrganizationParams(
                    id = reserverId,
                    phone = input.organization?.phone,
                    email = input.organization?.email
                )
            )
        } else {
            reserverService.updateCitizen(
                UpdateCitizenParams(
                    id = reserverId,
                    phone = input.citizen.phone,
                    email = input.citizen.email,
                )
            )
        }
    }

    fun getSendInvoiceModel(reservationId: Int): SendInvoiceModel {
        val reservation = boatReservationService.getReservationWithReserver(reservationId)
        if (reservation?.reserverId == null) {
            throw IllegalArgumentException("Reservation or reserver not found")
        }

        val invoiceData = invoiceService.createInvoiceData(reservationId, reservation.reserverId)
        if (invoiceData == null) {
            throw IllegalArgumentException("Failed to create invoice data")
        }

        // TODO: get the actual data
        return SendInvoiceModel(
            reservationId = reservationId,
            reserverName = "${invoiceData.firstnames} ${invoiceData.lastname}",
            reserverSsn = invoiceData.ssn ?: "",
            reserverAddress = "${invoiceData.street} ${invoiceData.postalCode} ${invoiceData.post}",
            product = reservation.locationName,
            billingPeriodStart = "",
            billingPeriodEnd = "",
            boatingSeasonStart = LocalDate.of(2025, 5, 1),
            boatingSeasonEnd = LocalDate.of(2025, 9, 30),
            invoiceNumber = "",
            dueDate = LocalDate.of(2025, 12, 31),
            costCenter = "?",
            invoiceType = "?",
            priceWithTax = intToDecimal(reservation.priceCents),
            description = invoiceData.description,
            contactPerson = "",
            orgId = invoiceData.orgId ?: "",
            function = getDefaultFunction(reservation.type),
        )
    }

    fun getDefaultFunction(boatSpaceType: BoatSpaceType): String =
        when (boatSpaceType) {
            BoatSpaceType.Slip -> "T1270"
            BoatSpaceType.Winter -> "T1271"
            BoatSpaceType.Storage -> "T1276"
            BoatSpaceType.Trailer -> "T1270"
        }

    fun getPriceDifference(reservationId: Int): Int? {
        val reservation =
            boatReservationService.getBoatSpaceReservation(reservationId)
                ?: throw BadRequest("Reservation not found")
        if (reservation.originalReservationId == null) {
            return null
        }
        val originalReservationPrice =
            boatReservationService
                .getBoatSpaceReservation(reservation.originalReservationId)
                ?.priceCents

        val priceDifference = calculatePriceDifference(originalReservationPrice ?: reservation.priceCents, reservation.priceCents)
        return priceDifference
    }

    fun calculatePriceDifference(
        originalPriceCents: Int,
        newPriceCents: Int
    ): Int = newPriceCents - originalPriceCents

    @Transactional
    fun endOriginalReservationAndCreateInvoice(
        switchedReservationId: Int,
        reserverId: UUID?,
        originalReservationId: Int?,
        originalPriceCents: Int,
        newPriceCents: Int
    ) {
        if (reserverId == null || originalReservationId == null) {
            throw IllegalArgumentException("Reservation not found")
        }

        // Set the original reservation to ended
        boatReservationService.markReservationEnded(originalReservationId)

        // Send invoice for new reservation
        val priceInCents = calculatePriceDifference(originalPriceCents, newPriceCents)
        if (priceInCents >= 0) {
            val invoiceData =
                invoiceService.createInvoiceData(switchedReservationId, reserverId, priceInCents)
                    ?: throw InternalError("Failed to create invoice batch")
            boatReservationService.setReservationStatusToInvoiced(switchedReservationId)
            invoiceService.createAndSendInvoice(invoiceData, reserverId, switchedReservationId)
                ?: throw InternalError("Failed to send invoice")
        }
    }

    fun createSwitchReservation(
        originalReservationId: Int,
        userId: UUID,
        userType: UserType,
        isEspooCitizen: Boolean,
        boatSpaceId: Int
    ): ReservationWithDependencies? {
        val originalReservation =
            boatSpaceReservationRepo.getBoatSpaceReservation(originalReservationId)
                ?: throw BadRequest("Reservation to switch not found")

        if (seasonalService.isBoatSpaceReserved(boatSpaceId)) {
            throw BadRequest("Boat space is already reserved")
        }
        val boatSpace =
            boatSpaceRepository.getBoatSpace(boatSpaceId)
                ?: throw BadRequest("Boat space not found")
        if (originalReservation.type !== boatSpace.type) {
            throw BadRequest("Boat space type does not match")
        }

        val reservationResult =
            seasonalService
                .canSwitchReservation(
                    originalReservation.type,
                    originalReservation.startDate,
                    originalReservation.endDate,
                    originalReservation.validity,
                    isEspooCitizen
                )
        if (!reservationResult.success
        ) {
            throw Conflict("Reservation cannot be renewed")
        }
        val reservationResultSuccessData = (reservationResult as ReservationResult.Success).data

        val newId =
            boatSpaceSwitchRepository.createSwitchRow(
                originalReservationId,
                userType,
                userId,
                boatSpaceId,
                reservationResultSuccessData.endDate,
                reservationResultSuccessData.reservationValidity
            )
        return boatSpaceReservationRepo.getReservationWithReserverInInfoPaymentRenewalStateWithinSessionTime(newId)
    }
}
