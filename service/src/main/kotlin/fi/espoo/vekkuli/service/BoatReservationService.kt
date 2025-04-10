package fi.espoo.vekkuli.service

import fi.espoo.vekkuli.boatSpace.reservationForm.UnauthorizedException
import fi.espoo.vekkuli.common.BadRequest
import fi.espoo.vekkuli.common.Unauthorized
import fi.espoo.vekkuli.config.*
import fi.espoo.vekkuli.config.BoatSpaceConfig.BOAT_WEIGHT_THRESHOLD_KG
import fi.espoo.vekkuli.config.BoatSpaceConfig.getInvoiceDueDate
import fi.espoo.vekkuli.config.BoatSpaceConfig.isLengthOk
import fi.espoo.vekkuli.config.BoatSpaceConfig.isWidthOk
import fi.espoo.vekkuli.domain.*
import fi.espoo.vekkuli.repository.*
import fi.espoo.vekkuli.utils.*
import fi.espoo.vekkuli.utils.decimalToInt
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

enum class ReservationResultErrorCode {
    NoReserver,
    NotPossible,
    MaxReservations,
    NotEspooCitizen,
}

data class ReservationResultSuccess(
    val startDate: LocalDate,
    val endDate: LocalDate,
    val reservationValidity: ReservationValidity
)

enum class PaymentProcessErrorCode {
    BoatSpaceNotAvailable,
    InvalidSignature,
    PaymentNotFound,
    ReservationNotFound,
}

sealed class ReservationResult(
    val success: Boolean
) {
    data class Success(
        val data: ReservationResultSuccess
    ) : ReservationResult(true)

    data class Failure(
        val errorCode: ReservationResultErrorCode
    ) : ReservationResult(false)
}

sealed class PaymentProcessResult {
    data class Paid(
        val reservation: BoatSpaceReservationDetails
    ) : PaymentProcessResult()

    data class Cancelled(
        val reservation: BoatSpaceReservationDetails
    ) : PaymentProcessResult()

    data class Failure(
        val errorCode: PaymentProcessErrorCode,
        val isPaid: Boolean,
        val reservation: BoatSpaceReservationDetails? = null
    ) : PaymentProcessResult()

    data class HandledAlready(
        val reservation: BoatSpaceReservationDetails
    ) : PaymentProcessResult()
}

interface ReservationWarningRepository {
    fun addReservationWarnings(warnings: List<ReservationWarning>): Unit

    fun getReservationWarning(warningId: UUID): ReservationWarning?

    fun getWarningsForReservation(reservationId: Int): List<ReservationWarning>

    fun getWarningsForReserver(reserverId: UUID): List<ReservationWarning>

    fun setReservationWarningsAcknowledged(
        reservationId: Int,
        boatIdOrTrailerId: Int,
        keys: List<ReservationWarningType>,
    ): Unit

    fun deleteReservationWarning(id: UUID)

    fun deleteReservationWarningsForReservation(
        reservationId: Int,
        keys: List<ReservationWarningType>? = null
    )
}

@Service
class BoatReservationService(
    private val paymentService: PaymentService,
    private val boatSpaceReservationRepo: BoatSpaceReservationRepository,
    private val reservationWarningRepo: ReservationWarningRepository,
    private val emailService: TemplateEmailService,
    private val messageUtil: MessageUtil,
    private val paytrail: PaytrailInterface,
    private val emailEnv: EmailEnv,
    private val timeProvider: TimeProvider,
    private val memoService: MemoService,
    private val permissionService: PermissionService,
    private val trailerRepository: TrailerRepository,
    private val organizationService: OrganizationService,
    private val paymentRepository: PaymentRepository,
    private val boatSpaceRepository: BoatSpaceRepository,
    private val reserverRepository: ReserverRepository,
    private val reserverService: ReserverService,
    private val messageService: MessageService,
    private val boatRepository: BoatRepository
) {
    fun handlePaytrailPaymentResult(
        params: Map<String, String>,
        isPaid: Boolean
    ): PaymentProcessResult {
        if (!paytrail.checkSignature(params)) {
            return PaymentProcessResult.Failure(PaymentProcessErrorCode.InvalidSignature, isPaid)
        }

        val paymentId = UUID.fromString(params.get("checkout-stamp"))
        return handlePaymentResult(paymentId, isPaid)
    }

    fun handlePaymentResult(
        paymentId: UUID,
        isPaid: Boolean
    ): PaymentProcessResult {
        val payment =
            paymentService.getPayment(paymentId)
                ?: return PaymentProcessResult.Failure(PaymentProcessErrorCode.PaymentNotFound, isPaid)

        val reservation =
            boatSpaceReservationRepo.getBoatSpaceReservationWithPaymentId(paymentId)
                ?: return PaymentProcessResult.Failure(PaymentProcessErrorCode.ReservationNotFound, isPaid)

        if (payment.status != PaymentStatus.Created) {
            return PaymentProcessResult.HandledAlready(reservation)
        }

        paymentService.updatePayment(payment.id, isPaid, if (isPaid) timeProvider.getCurrentDateTime() else null)

        if (!isPaid) {
            return PaymentProcessResult.Cancelled(reservation)
        }

        val boatSpaceWasAvailable =
            boatSpaceReservationRepo.updateBoatSpaceReservationOnPaymentSuccess(
                payment.id
            ) != null

        if (!boatSpaceWasAvailable) {
            return PaymentProcessResult.Failure(PaymentProcessErrorCode.BoatSpaceNotAvailable, isPaid, reservation)
        }

        if (reservation.originalReservationId != null) {
            markReservationEnded(reservation.originalReservationId)
        }

        sendReservationEmailAndInsertMemoIfSwitch(reservation.id)

        return PaymentProcessResult.Paid(reservation)
    }

    fun upsertCreatedPaymentToReservation(
        reservationId: Int,
        params: CreatePaymentParams
    ): Payment = paymentService.upsertCreatedPaymentToReservation(params, reservationId)

    fun addTransactionIdToPayment(
        paymentId: UUID,
        transactionId: String
    ) {
        paymentService.addTransactionIdToPayment(paymentId, transactionId)
    }

    fun addBoatWarningsToReservations(
        boat: Boat,
        previousBoatInfo: Boat?
    ) {
        getReservationsForBoat(boat.id)
            .forEach { reservation ->
                val boatSpace =
                    getBoatSpaceRelatedToReservation(reservation.id)
                        ?: throw IllegalArgumentException("Boat space not found")

                addReservationWarnings(
                    reservation.id,
                    reservation.boatSpaceWidthCm,
                    reservation.boatSpaceLengthCm,
                    reservation.amenity,
                    boatSpace.excludedBoatTypes ?: listOf(),
                    boat,
                    previousBoatInfo
                )
            }
    }

    fun addTrailerWarningsToReservations(
        trailerId: Int,
        trailerWidthCm: Int,
        trailerLengthCm: Int,
    ) {
        // find all active reservations that have this trailer
        val reservations = boatSpaceReservationRepo.getReservationsForTrailer(trailerId)

        reservations.forEach {
            val warnings = mutableListOf<ReservationWarningType>()

            if (trailerWidthCm > it.boatSpaceWidthCm) {
                warnings.add(ReservationWarningType.TrailerWidth)
            }

            if (trailerLengthCm > it.boatSpaceLengthCm) {
                warnings.add(ReservationWarningType.TrailerLength)
            }

            val reservationWarnings =
                warnings.map { warning ->
                    ReservationWarning(
                        UUID.randomUUID(),
                        it.id,
                        null,
                        trailerId,
                        null,
                        key = warning,
                        infoText = null
                    )
                }

            if (warnings.isNotEmpty()) {
                reservationWarningRepo.addReservationWarnings(reservationWarnings)
            }
        }
    }

    fun getReservationsForBoat(boatId: Int): List<BoatSpaceReservationDetails> = boatSpaceReservationRepo.getReservationsForBoat(boatId)

    fun getActiveReservationsForBoat(boatId: Int): List<BoatSpaceReservationDetails> =
        boatSpaceReservationRepo.getActiveReservationsForBoat(
            boatId
        )

    fun getReservationsForTrailer(trailerId: Int): List<BoatSpaceReservationDetails> =
        boatSpaceReservationRepo.getReservationsForTrailer(trailerId)

    fun addReservationWarnings(
        reservationId: Int,
        boatSpaceWidthCm: Int,
        boatSpaceLengthCm: Int,
        amenity: BoatSpaceAmenity,
        excludedBoatTypes: List<BoatType>,
        boat: Boat,
        previousBoatInfo: Boat?,
    ) {
        val warnings = mutableListOf<Pair<ReservationWarningType, String?>>()

        if (!isWidthOk(
                Dimensions(boatSpaceWidthCm, boatSpaceLengthCm),
                amenity,
                Dimensions(boat.widthCm, boat.lengthCm)
            )
        ) {
            warnings.add(Pair(ReservationWarningType.BoatWidth, null))
        }

        if (!isLengthOk(
                Dimensions(boatSpaceWidthCm, boatSpaceLengthCm),
                amenity,
                Dimensions(boat.widthCm, boat.lengthCm)
            )
        ) {
            warnings.add(Pair(ReservationWarningType.BoatLength, null))
        }

        if (boat.ownership == OwnershipStatus.FutureOwner) {
            warnings.add(Pair(ReservationWarningType.BoatFutureOwner, null))
        }

        if (boat.ownership == OwnershipStatus.CoOwner) {
            warnings.add(Pair(ReservationWarningType.BoatCoOwner, null))
        }

        if (previousBoatInfo != null) {
            if (previousBoatInfo.ownership != boat.ownership) {
                val previousOwnership =
                    messageUtil.getMessage("boatApplication.EMPLOYEE.ownershipOption.${previousBoatInfo.ownership}")
                warnings.add(
                    Pair(
                        ReservationWarningType.BoatOwnershipChange,
                        messageUtil.getMessage(
                            "reservationWarning.BoatOwnershipChange.previous",
                            listOf(previousOwnership)
                        )
                    )
                )
            }
            if (previousBoatInfo.registrationCode != boat.registrationCode) {
                warnings.add(
                    Pair(
                        ReservationWarningType.BoatRegistrationCodeChange,
                        messageUtil.getMessage(
                            "reservationWarning.BoatRegistrationCodeChange.previous",
                            listOf(previousBoatInfo.registrationCode ?: "-")
                        )
                    )
                )
            }
        }

        if (boat.weightKg > BOAT_WEIGHT_THRESHOLD_KG) {
            warnings.add(Pair(ReservationWarningType.BoatWeight, null))
        }

        if (excludedBoatTypes.contains(boat.type)) {
            warnings.add(Pair(ReservationWarningType.BoatType, null))
        }

        if (boat.registrationCode != null) {
            val boatsWithSameRegistrationCode: List<Pair<Boat, ReserverWithDetails?>> =
                getBoatAndReserverWithRegistrationCode(boat.registrationCode)
                    .filter { it.first.id != boat.id }
            if (boatsWithSameRegistrationCode.isNotEmpty()) {
                warnings.add(
                    Pair(
                        ReservationWarningType.RegistrationCodeNotUnique,
                        boatsWithSameRegistrationCode.joinToString(", ") { "${it.second?.email}/${it.first.name}" }
                    )
                )
            }
        }

        val reservationWarnings =
            warnings.map { warning ->
                ReservationWarning(
                    UUID.randomUUID(),
                    reservationId,
                    boat.id,
                    null,
                    null,
                    key = warning.first,
                    infoText = warning.second
                )
            }

        if (warnings.isNotEmpty()) {
            reservationWarningRepo.addReservationWarnings(
                reservationWarnings,
            )
        }
    }

    fun getReservationWithReserver(id: Int): ReservationWithDependencies? =
        boatSpaceReservationRepo.getReservationWithReserverInInfoPaymentRenewalStateWithinSessionTime(id)

    fun getReservationWithDependencies(id: Int): ReservationWithDependencies? = boatSpaceReservationRepo.getReservationWithDependencies(id)

    fun getReservationWithoutCitizen(id: Int): ReservationWithDependencies? = boatSpaceReservationRepo.getReservationWithoutReserver(id)

    fun removeBoatSpaceReservation(
        id: Int,
        citizenId: UUID,
    ) {
        if (!permissionService.canDeleteBoatSpaceReservation(citizenId, id)) {
            throw Unauthorized()
        }
        boatSpaceReservationRepo.removeBoatSpaceReservation(id)
    }

    fun getBoatSpaceReservation(reservationId: Int): BoatSpaceReservationDetails? =
        boatSpaceReservationRepo.getBoatSpaceReservationDetails(reservationId)

    fun getBoatSpaceRelatedToReservation(reservationId: Int): BoatSpace? =
        boatSpaceReservationRepo.getBoatSpaceRelatedToReservation(reservationId)

    fun updateBoatInBoatSpaceReservation(
        reservationId: Int,
        boatId: Int,
        reserverId: UUID,
        reservationStatus: ReservationStatus,
        validity: ReservationValidity,
        startDate: LocalDate,
        endDate: LocalDate,
    ): BoatSpaceReservation =
        boatSpaceReservationRepo.updateBoatInBoatSpaceReservation(
            reservationId,
            boatId,
            reserverId,
            reservationStatus,
            validity,
            startDate,
            endDate
        )

    fun setReservationStatusToInvoiced(reservationId: Int): BoatSpaceReservation =
        boatSpaceReservationRepo.setReservationStatusToInvoiced(reservationId)

    fun setReservationStatusToInfo(reservationId: Int): BoatSpaceReservation =
        boatSpaceReservationRepo.setReservationStatusToInfo(reservationId)

    fun getUnfinishedReservationForCitizen(id: UUID): ReservationWithDependencies? =
        boatSpaceReservationRepo.getUnfinishedReservationForCitizen(id)

    fun getUnfinishedReservationForEmployee(id: UUID): ReservationWithDependencies? =
        boatSpaceReservationRepo.getUnfinishedReservationForEmployee(id)

    fun insertBoatSpaceReservation(
        reserverId: UUID,
        actingCitizenId: UUID?,
        boatSpaceId: Int,
        creationType: CreationType,
        startDate: LocalDate,
        endDate: LocalDate,
        validity: ReservationValidity,
    ): BoatSpaceReservation =
        boatSpaceReservationRepo.insertBoatSpaceReservation(
            reserverId,
            actingCitizenId,
            boatSpaceId,
            creationType,
            startDate,
            endDate,
            validity
        )

    fun insertBoatSpaceReservationAsEmployee(
        employeeId: UUID,
        boatSpaceId: Int,
        creationType: CreationType,
        startDate: LocalDate,
        endDate: LocalDate,
    ): BoatSpaceReservation =
        boatSpaceReservationRepo.insertBoatSpaceReservationAsEmployee(
            employeeId,
            boatSpaceId,
            creationType,
            startDate,
            endDate,
        )

    fun getBoatSpaceReservationsForReserver(
        reserverId: UUID,
        spaceType: BoatSpaceType? = null
    ): List<BoatSpaceReservationDetails> =
        boatSpaceReservationRepo.getBoatSpaceReservationsForReserver(
            reserverId,
            spaceType,
        )

    fun getExpiredBoatSpaceReservationsForReserver(reserverId: UUID): List<BoatSpaceReservationDetails> =
        boatSpaceReservationRepo.getExpiredBoatSpaceReservationsForReserver(reserverId)

    fun getExpiringIndefiniteBoatSpaceReservations(): List<BoatSpaceReservationDetails> =
        boatSpaceReservationRepo.getExpiringBoatSpaceReservations(ReservationValidity.Indefinite)

    fun getExpiringFixedTermBoatSpaceReservations(): List<BoatSpaceReservationDetails> =
        boatSpaceReservationRepo.getExpiringBoatSpaceReservations(ReservationValidity.FixedTerm)

    fun getExpiredBoatSpaceReservations(): List<BoatSpaceReservationDetails> = boatSpaceReservationRepo.getExpiredBoatSpaceReservations()

    fun acknowledgeWarnings(
        reservationId: Int,
        userId: UUID,
        boatOrTrailerId: Int,
        keys: List<ReservationWarningType>,
        infoText: String,
    ) {
        if (keys.isEmpty()) return
        reservationWarningRepo.setReservationWarningsAcknowledged(reservationId, boatOrTrailerId, keys)
    }

    fun addAcknowledgementMemo(
        reservationId: Int,
        userId: UUID,
        infoText: String
    ) {
        val reservation = getReservationWithDependencies(reservationId)
        if (reservation?.reserverId == null) {
            throw IllegalArgumentException("No reservation or reservation has no reserver")
        }
        memoService.insertMemo(reservation.reserverId, userId, infoText)
    }

    fun acknowledgeWarningForBoat(
        boatId: Int,
        userId: UUID,
        keys: List<ReservationWarningType>,
        infoText: String
    ) {
        if (keys.isEmpty()) return
        val reservationsForBoat = getReservationsForBoat(boatId)

        reservationsForBoat.forEach {
            acknowledgeWarnings(it.id, userId, boatId, keys, infoText)
        }
        val (id) = reservationsForBoat.first()
        if (infoText.isNotEmpty()) {
            addAcknowledgementMemo(id, userId, infoText)
        }
    }

    fun acknowledgeWarningForTrailer(
        trailerId: Int,
        userId: UUID,
        keys: List<ReservationWarningType>,
        infoText: String
    ) {
        if (keys.isEmpty()) return
        val reservationsWithTrailer = getReservationsForTrailer(trailerId)
        reservationsWithTrailer.forEach {
            acknowledgeWarnings(it.id, userId, trailerId, keys, infoText)
        }
        val (id) = reservationsWithTrailer.first()
        if (infoText.isNotEmpty()) {
            addAcknowledgementMemo(id, userId, infoText)
        }
    }

    fun markInvoicePaid(
        reservationId: Int,
        paymentDate: LocalDateTime
    ) {
        boatSpaceReservationRepo.updateReservationInvoicePaid(reservationId)
        val reservation = boatSpaceReservationRepo.getBoatSpaceReservationDetails(reservationId)
        if (reservation?.paymentId == null) {
            throw IllegalArgumentException("Reservation has no payment")
        }
        paymentService.updatePayment(reservation.paymentId, true, paymentDate)
    }

    @Transactional
    fun updateReservationStatus(
        reservationId: Int,
        reservationStatus: ReservationStatus,
        paymentDate: LocalDateTime,
        paymentStatusText: String,
        priceInfo: String = "",
        paymentType: PaymentType = PaymentType.Invoice
    ) {
        val reservation =
            boatSpaceReservationRepo.updateReservationStatus(reservationId, reservationStatus)
                ?: throw RuntimeException("Reservation $reservationId missing")

        if (reservationStatus == ReservationStatus.Confirmed || reservationStatus == ReservationStatus.Invoiced) {
            val payment = paymentRepository.getPaymentForReservation(reservationId)
            if (payment != null) {
                paymentService.updatePayment(
                    payment.copy(
                        status = if (reservationStatus == ReservationStatus.Confirmed) PaymentStatus.Success else PaymentStatus.Created,
                        paid = paymentDate,
                        reference = paymentStatusText
                    )
                )
            } else {
                if (reservation.reserverId == null) {
                    throw RuntimeException(
                        "Cannot create payment for reservation $reservationId: reserverId is null"
                    )
                }
                val paymentParams =
                    CreatePaymentParams(
                        reserverId = reservation.reserverId,
                        reference = paymentStatusText,
                        totalCents = 0,
                        vatPercentage = 0.0,
                        productCode = "?",
                        status = if (reservationStatus == ReservationStatus.Confirmed) PaymentStatus.Success else PaymentStatus.Created,
                        paid = if (reservationStatus == ReservationStatus.Confirmed) paymentDate else null,
                        paymentType = paymentType,
                        priceInfo = priceInfo
                    )
                paymentService.insertPayment(paymentParams, reservationId)
            }
        }
    }

    fun getEmailRecipientForReservation(reservationId: Int): Recipient? {
        val recipient = boatSpaceReservationRepo.getReservationReserverEmail(reservationId)
        if (recipient?.id == null) {
            return null
        }
        return recipient
    }

    fun markReservationEnded(reservationId: Int) {
        boatSpaceReservationRepo.setReservationAsExpired(reservationId)
        preventSendingAReservationExpirationEmail(reservationId)
    }

    private fun preventSendingAReservationExpirationEmail(reservationId: Int) {
        val reservation =
            boatSpaceReservationRepo.getBoatSpaceReservationDetails(
                reservationId
            ) ?: throw IllegalArgumentException("Reservation not found")
        val recipientEmails = getRecipientEmails(reservation)
        messageService.getAndInsertUnsentEmails(
            ReservationType.Marine,
            reservationId,
            EmailType.ExpiredReservation,
            recipientEmails
        )
    }

    private fun getRecipientEmails(reservation: BoatSpaceReservationDetails) =
        if (reservation.reserverType == ReserverType.Organization) {
            organizationService
                .getOrganizationMembers(reservation.reserverId)
                .map { it.email } + listOf(reservation.email)
        } else {
            listOf(reservation.email)
        }

    fun getHarbors(): List<Location> = boatSpaceReservationRepo.getHarbors()

    fun getTrailer(id: Int): Trailer? = trailerRepository.getTrailer(id)

    fun updateTrailer(
        userId: UUID,
        trailerId: Int,
        trailerRegistrationCode: String,
        trailerWidth: BigDecimal,
        trailerLength: BigDecimal,
    ): Trailer {
        val oldTrailer = getTrailer(trailerId) ?: throw IllegalArgumentException("Trailer not found")

        if (!permissionService.canEditTrailer(userId, oldTrailer.reserverId)) {
            throw UnauthorizedException()
        }
        val updatedTrailer =
            Trailer(
                id = trailerId,
                registrationCode = trailerRegistrationCode,
                widthCm = decimalToInt(trailerWidth),
                lengthCm = decimalToInt(trailerLength),
                reserverId = oldTrailer.reserverId
            )

        val result = trailerRepository.updateTrailer(updatedTrailer)
        addTrailerWarningsToReservations(trailerId, updatedTrailer.widthCm, updatedTrailer.lengthCm)
        return result
    }

    fun sendReservationEmailAndInsertMemoIfSwitch(reservationId: Int) {
        val reservation =
            getBoatSpaceReservation(reservationId)
                ?: throw BadRequest("Reservation $reservationId not found")
        val boatSpace =
            boatSpaceRepository.getBoatSpace(reservation.boatSpaceId)
                ?: throw BadRequest("Boat space ${reservation.boatSpaceId} not found")
        val isInvoiced = reservation.paymentType == PaymentType.Invoice
        val placeName = "${reservation.locationName} ${reservation.place}"
        val reservationStatus = reservation.status
        val organizationReservation = reservation.reserverType == ReserverType.Organization

        val defaultParams =
            mapOf(
                "reserverName" to reservation.name,
                "harborName" to reservation.locationName,
                "name" to placeName,
                "width" to
                    fi.espoo.vekkuli.utils
                        .intToDecimal(boatSpace.widthCm),
                "length" to
                    fi.espoo.vekkuli.utils
                        .intToDecimal(boatSpace.lengthCm)
            ) +
                messageUtil.getLocalizedMap(
                    "placeType",
                    "boatSpaceReservation.email.types.${reservation.type}"
                ) +
                messageUtil.getLocalizedMap(
                    "amenity",
                    "boatSpaces.amenityOption.${boatSpace.amenity}"
                ) +
                messageUtil.getLocalizedMap(
                    "endDate",
                    "boatSpaceReservation.email.validity.${reservation.validity}",
                    listOf(formatAsFullDate(reservation.endDate))
                ) +
                getCitizenReserverForOrganizationLocalization(
                    organizationReservation,
                    reservation
                ) +
                getHarborAddressLocalization(reservation, boatSpace.locationAddress ?: "")

        data class EmailSettings(
            val template: String,
            val recipients: List<String>,
            val params: Map<String, Any>
        )

        val invoiceAddress = getInvoiceAddress(reservation)

        val recipients =
            if (organizationReservation) {
                organizationService
                    .getOrganizationMembers(reservation.reserverId)
                    .map { it.email } + listOf(reservation.email)
            } else {
                listOf(reservation.email)
            }

        val emailSettings =
            when (reservation.creationType) {
                CreationType.New -> {
                    if (isInvoiced) {
                        if (reservationStatus == ReservationStatus.Confirmed) {
                            EmailSettings(
                                template = "reservation_created_by_employee_confirmed",
                                recipients = recipients,
                                params = defaultParams
                            )
                        } else {
                            EmailSettings(
                                template = "reservation_created_by_employee",
                                recipients = recipients,
                                params =
                                    defaultParams
                                        .plus("invoiceDueDate" to formatAsFullDate(getInvoiceDueDate(timeProvider))) +
                                        invoiceAddress
                            )
                        }
                    } else {
                        EmailSettings(
                            template = "reservation_created_by_citizen",
                            recipients = recipients,
                            params = defaultParams
                        )
                    }
                }

                CreationType.Switch -> {
                    EmailSettings(
                        template = "reservation_switched_by_citizen",
                        recipients = recipients,
                        params = defaultParams
                    )
                }

                CreationType.Renewal -> {
                    if (isInvoiced) {
                        if (reservationStatus == ReservationStatus.Confirmed) {
                            EmailSettings(
                                template = "reservation_renewed_by_employee_confirmed",
                                recipients = recipients,
                                params = defaultParams
                            )
                        } else {
                            EmailSettings(
                                template = "reservation_renewed_by_employee",
                                recipients = recipients,
                                params =
                                    defaultParams
                                        .plus("invoiceDueDate" to formatAsFullDate(getInvoiceDueDate(timeProvider))) +
                                        invoiceAddress
                            )
                        }
                    } else {
                        EmailSettings(
                            template = "reservation_renewed_by_citizen",
                            recipients = recipients,
                            params =
                                defaultParams
                                    .plus("invoiceDueDate" to formatAsFullDate(getInvoiceDueDate(timeProvider))) +
                                    invoiceAddress
                        )
                    }
                }
            }

        emailService.sendBatchEmail(
            emailSettings.template,
            null,
            emailEnv.senderAddress,
            emailSettings.recipients.map { Recipient(reservation.reserverId, it) },
            emailSettings.params
        )

        if (reservation.creationType == CreationType.Switch) {
            documentSwitchToMemo(reservation)
        }
    }

    private fun getInvoiceAddress(reservation: BoatSpaceReservationDetails): Map<String, String> {
        val key = "invoiceAddress"

        if (reservation.reserverType == ReserverType.Organization) {
            val organisation =
                organizationService.getOrganizationById(reservation.reserverId)
                    ?: throw BadRequest("Organization with id ${reservation.reserverId} not found")
            val billingName = if (organisation.billingName.isNotEmpty()) "${organisation.billingName}/" else ""
            val invoiceAddress =
                "${billingName}${organisation.name}/${organisation.billingStreetAddress}," +
                    "${organisation.billingPostalCode},${organisation.billingPostOffice}"
            return getLocalizedParameter(key, invoiceAddress)
        } else {
            val citizen =
                reserverService.getCitizen(reservation.reserverId)
                    ?: throw BadRequest("Citizen with id ${reservation.reserverId} not found")
            return if (citizen.dataProtection) {
                messageUtil.getLocalizedMap(key, "boatSpaceReservation.email.dataProtectionAddress")
            } else {
                getLocalizedParameter(key, "${reservation.streetAddress}, ${reservation.postalCode}")
            }
        }
    }

    private fun getLocalizedParameter(
        key: String,
        value: String
    ) = messageUtil.locales.associate { locale ->
        "$key${locale.language.replaceFirstChar { it.uppercaseChar() }}" to value
    }

    private fun getHarborAddressLocalization(
        reservation: BoatSpaceReservationDetails,
        locationAddress: String
    ): Map<String, String> {
        val code =
            if (reservation.type == BoatSpaceType.Storage) {
                "boatSpaceReservation.email.storagePlaceAddress"
            } else {
                "boatSpaceReservation.email.harborAddress"
            }

        return messageUtil.getLocalizedMap("harborAddress", code, listOf(locationAddress))
    }

    private fun getCitizenReserverForOrganizationLocalization(
        organizationReservation: Boolean,
        reservation: BoatSpaceReservationDetails
    ): Map<String, String> {
        val key = "citizenReserver"
        val actingCitizen =
            if (organizationReservation && reservation.actingCitizenId != null) {
                reserverService.getCitizen(reservation.actingCitizenId)?.fullName
            } else {
                null
            }
        return if (actingCitizen != null) {
            messageUtil.getLocalizedMap(
                key,
                "boatSpaceReservation.email.reserver",
                listOf(actingCitizen)
            )
        } else {
            getLocalizedParameter(key, "")
        }
    }

    fun documentSwitchToMemo(reservation: BoatSpaceReservationDetails) {
        val userId = reservation.actingCitizenId ?: reservation.reserverId
        val reserver = reserverRepository.getCitizenById(userId)

        val originalReservation =
            getBoatSpaceReservation(reservation.originalReservationId ?: -1)
                ?: throw BadRequest("Reservation ${reservation.originalReservationId} not found")

        val placeName = "${reservation.locationName} ${reservation.place}"
        val newPlaceName = "${originalReservation.locationName} ${originalReservation.place}"

        val infoText =
            "${reserver?.firstName} ${reserver?.lastName} vaihtoi paikan. Vanha paikka: $placeName. Uusi paikka: $newPlaceName."

        memoService.insertSystemMemo(userId, infoText)
    }

    @Transactional
    fun changeReservationBoat(
        reservationId: Int,
        boatId: Int
    ): Boolean {
        val reservationForBoat = boatSpaceReservationRepo.changeReservationBoat(reservationId, boatId)
        reservationWarningRepo.deleteReservationWarningsForReservation(
            reservationId,
            ReservationWarningType.values().filter { it.category != ReservationWarningType.Category.General }
        )
        return reservationForBoat
    }

    fun getBoatAndReserverWithRegistrationCode(registrationCode: String): List<Pair<Boat, ReserverWithDetails?>> =
        boatRepository.getBoatsByRegistrationCode(registrationCode).map { boat ->
            val reserver = reserverRepository.getReserverById(boat.reserverId)
            Pair(boat, reserver)
        }
}
