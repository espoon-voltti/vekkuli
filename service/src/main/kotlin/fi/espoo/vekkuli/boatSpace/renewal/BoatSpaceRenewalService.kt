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
import fi.espoo.vekkuli.views.employee.SendInvoiceModel
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.util.*

@Service
class BoatSpaceRenewalService(
    private val boatReservationService: BoatReservationService,
    private val reserverService: ReserverService,
    private val boatSpaceRenewalRepository: BoatSpaceRenewalRepository,
    private val invoiceService: BoatSpaceInvoiceService,
    private val boatService: BoatService,
    private val messageUtil: MessageUtil,
    private val boatSpaceReservationRepo: BoatSpaceReservationRepository,
    private val trailerRepo: TrailerRepository,
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
            dueDate = invoiceData.dueDate,
            costCenter = "?",
            invoiceType = "?",
            priceWithTax = intToDecimal(reservation.priceCents),
            discountedPriceWithTax = intToDecimal(reservation.discountedPriceCents),
            description = invoiceData.description,
            contactPerson = "",
            orgId = invoiceData.orgId ?: "",
            function = getDefaultFunction(reservation.type),
            discountPercentage = reservation.discountPercentage ?: 0
        )
    }

    fun getDefaultFunction(boatSpaceType: BoatSpaceType): String =
        when (boatSpaceType) {
            BoatSpaceType.Slip -> "T1270"
            BoatSpaceType.Winter -> "T1271"
            BoatSpaceType.Storage -> "T1276"
            BoatSpaceType.Trailer -> "T1270"
        }

    @Transactional
    fun activateRenewalAndSendInvoice(
        renewedReservationId: Int,
        reserverId: UUID?,
        originalReservationId: Int?,
        input: InvoiceInput
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

        boatReservationService.setReservationStatusToInvoiced(renewedReservationId)

        boatReservationService.markReservationEnded(originalReservationId)

        invoiceService.createAndSendInvoice(invoiceData, reserverId, renewedReservationId)
            ?: throw InternalError("Failed to send invoice")
        boatReservationService.sendReservationEmailAndInsertMemoIfSwitch(renewedReservationId)
    }

    fun buildBoatSpaceRenewalViewParams(
        citizenId: UUID,
        renewedReservation: ReservationWithDependencies,
        formInput: ModifyReservationInput,
    ): BoatSpaceRenewViewParams {
        val citizen = reserverService.getCitizen(citizenId)
        if (citizen == null || renewedReservation.reserverId != citizenId) {
            throw UnauthorizedException()
        }

        var input =
            formInput.copy(
                email = formInput.email ?: citizen.email,
                phone = formInput.phone ?: citizen.phone,
                storageType =
                    renewedReservation.storageType ?: StorageType.None,
            )
        if (renewedReservation.trailerId != null) {
            val trailer = trailerRepo.getTrailer(renewedReservation.trailerId) ?: throw BadRequest("Trailer not found")
            input =
                input.copy(
                    trailerLength = intToDecimal(trailer.lengthCm),
                    trailerWidth = intToDecimal(trailer.widthCm),
                    trailerRegistrationNumber = trailer.registrationCode,
                )
        }

        val usedBoatId = formInput.boatId ?: renewedReservation.boatId // use boat id from reservation if it exists
        if (usedBoatId != null && usedBoatId != 0) {
            val boat = boatService.getBoat(usedBoatId)

            if (boat != null) {
                input =
                    input.copy(
                        boatId = boat.id,
                        depth = intToDecimal(boat.depthCm),
                        boatName = boat.name,
                        weight = boat.weightKg,
                        width = intToDecimal(boat.widthCm),
                        length = intToDecimal(boat.lengthCm),
                        otherIdentification = boat.otherIdentification,
                        extraInformation = boat.extraInformation,
                        ownership = boat.ownership,
                        boatType = boat.type,
                        boatRegistrationNumber = boat.registrationCode,
                        noRegistrationNumber = boat.registrationCode.isNullOrEmpty()
                    )
            }
        } else {
            input = input.copy(boatId = 0)
        }

        val reserverId = renewedReservation.reserverId

        val boats =
            reserverId.let {
                boatService
                    .getBoatsForReserver(reserverId)
                    .map { boat -> boat.updateBoatDisplayName(messageUtil) }
            } ?: emptyList()

        val renewedReservationForApplicationForm = buildReservationForApplicationForm(renewedReservation)
        return BoatSpaceRenewViewParams(
            renewedReservationForApplicationForm,
            boats,
            citizen,
            input,
            UserType.CITIZEN,
        )
    }

    private fun buildReservationForApplicationForm(reservationWithDependencies: ReservationWithDependencies) =
        RenewalReservationForApplicationForm(
            reservationWithDependencies.id,
            reservationWithDependencies.reserverId,
            reservationWithDependencies.boatId,
            reservationWithDependencies.lengthCm,
            reservationWithDependencies.widthCm,
            reservationWithDependencies.amenity,
            reservationWithDependencies.type,
            reservationWithDependencies.place,
            reservationWithDependencies.locationName,
            reservationWithDependencies.validity,
            reservationWithDependencies.startDate,
            reservationWithDependencies.endDate,
            reservationWithDependencies.priceCents,
            reservationWithDependencies.vatCents,
            reservationWithDependencies.netPriceCents,
            reservationWithDependencies.created,
            reservationWithDependencies.excludedBoatTypes,
            reservationWithDependencies.section,
            reservationWithDependencies.storageType,
            reservationWithDependencies.creationType,
            reservationWithDependencies.originalReservationId.toString(),
        )

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
