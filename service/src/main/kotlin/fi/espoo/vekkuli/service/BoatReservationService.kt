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
import fi.espoo.vekkuli.repository.filter.SortDirection
import fi.espoo.vekkuli.repository.filter.boatspacereservation.*
import fi.espoo.vekkuli.repository.filter.boatspacereservation.LocationExpr
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
    data class Success(
        val reservation: BoatSpaceReservationDetails
    ) : PaymentProcessResult()

    object Failure : PaymentProcessResult()

    data class HandledAlready(
        val reservation: BoatSpaceReservationDetails
    ) : PaymentProcessResult()
}

interface ReservationWarningRepository {
    fun addReservationWarnings(
        reservationId: Int,
        boatId: Int?,
        trailerId: Int?,
        keys: List<String>,
    ): Unit

    fun getWarningsForReservation(reservationId: Int): List<ReservationWarning>

    fun setReservationWarningsAcknowledged(
        reservationId: Int,
        boatIdOrTrailerId: Int,
        keys: List<String>,
    ): Unit

    fun deleteReservationWarningsForReservation(reservationId: Int)
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
    private val reserverRepository: ReserverRepository
) {
    fun t(
        key: String,
        params: List<String> = emptyList()
    ): String = messageUtil.getMessage(key, params)

    fun handlePaymentResult(
        params: Map<String, String>,
        paymentSuccess: Boolean
    ): PaymentProcessResult {
        if (!paytrail.checkSignature(params)) {
            return PaymentProcessResult.Failure
        }
        val stamp = UUID.fromString(params.get("checkout-stamp"))

        val payment = paymentService.getPayment(stamp)
        if (payment == null) return PaymentProcessResult.Failure

        val reservation = boatSpaceReservationRepo.getBoatSpaceReservationWithPaymentId(stamp)
        if (reservation == null) return PaymentProcessResult.Failure

        if (reservation.originalReservationId != null) {
            markReservationEnded(reservation.originalReservationId)
        }

        if (payment.status != PaymentStatus.Created) return PaymentProcessResult.HandledAlready(reservation)

        handleReservationPaymentResult(stamp, paymentSuccess) ?: return PaymentProcessResult.Failure
        if (paymentSuccess) sendReservationEmailAndInsertMemoIfSwitch(reservation.id)

        return PaymentProcessResult.Success(reservation)
    }

    fun handleReservationPaymentResult(
        paymentId: UUID,
        success: Boolean
    ): Int? {
        paymentService.updatePayment(paymentId, success, if (success) timeProvider.getCurrentDateTime() else null)
        if (!success) return boatSpaceReservationRepo.getBoatSpaceReservationIdForPayment(paymentId)

        val reservationId =
            boatSpaceReservationRepo.updateBoatSpaceReservationOnPaymentSuccess(
                paymentId
            )

        return reservationId
    }

    @Transactional
    fun addPaymentToReservation(
        reservationId: Int,
        params: CreatePaymentParams
    ): Payment {
        paymentService.deletePaymentInCreatedStatusForReservation(reservationId)
        return paymentService.insertPayment(params, reservationId)
    }

    fun addBoatWarningsToReservations(boat: Boat) {
        getReservationsForBoat(boat.id)
            .forEach { reservation ->
                val boatSpace =
                    getBoatSpaceRelatedToReservation(reservation.id)
                        ?: throw IllegalArgumentException("Boat space not found")

                addReservationWarnings(
                    reservation.id,
                    boat.id,
                    reservation.boatSpaceWidthCm,
                    reservation.boatSpaceLengthCm,
                    reservation.amenity,
                    boat.widthCm,
                    boat.lengthCm,
                    boat.ownership,
                    boat.weightKg,
                    boat.type,
                    boatSpace.excludedBoatTypes ?: listOf(),
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
            val warnings = mutableListOf<String>()

            if (trailerWidthCm > it.boatSpaceWidthCm) {
                warnings.add(ReservationWarningType.TrailerWidth.name)
            }

            if (trailerLengthCm > it.boatSpaceLengthCm) {
                warnings.add(ReservationWarningType.TrailerLength.name)
            }

            if (warnings.isNotEmpty()) {
                reservationWarningRepo.addReservationWarnings(it.id, null, trailerId, warnings)
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
        boatId: Int,
        boatSpaceWidthCm: Int,
        boatSpaceLengthCm: Int,
        amenity: BoatSpaceAmenity,
        boatWidthCm: Int,
        boatLengthCm: Int,
        boatOwnership: OwnershipStatus?,
        boatWeightKg: Int,
        boatType: BoatType,
        excludedBoatTypes: List<BoatType>,
    ) {
        val warnings = mutableListOf<String>()

        if (!isWidthOk(
                Dimensions(boatSpaceWidthCm, boatSpaceLengthCm),
                amenity,
                Dimensions(boatWidthCm, boatLengthCm)
            )
        ) {
            warnings.add(ReservationWarningType.BoatWidth.name)
        }

        if (!isLengthOk(
                Dimensions(boatSpaceWidthCm, boatSpaceLengthCm),
                amenity,
                Dimensions(boatWidthCm, boatLengthCm)
            )
        ) {
            warnings.add(ReservationWarningType.BoatLength.name)
        }

        if (boatOwnership == OwnershipStatus.FutureOwner) {
            warnings.add(ReservationWarningType.BoatFutureOwner.name)
        }

        if (boatOwnership == OwnershipStatus.CoOwner) {
            warnings.add(ReservationWarningType.BoatCoOwner.name)
        }

        if (boatWeightKg > BOAT_WEIGHT_THRESHOLD_KG) {
            warnings.add(ReservationWarningType.BoatWeight.name)
        }

        if (excludedBoatTypes.contains(boatType)) {
            warnings.add(ReservationWarningType.BoatType.name)
        }

        if (warnings.isNotEmpty()) {
            reservationWarningRepo.addReservationWarnings(reservationId, boatId, null, warnings)
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

    fun getBoatSpaceReservations(params: BoatSpaceReservationFilter): List<BoatSpaceReservationItem> {
        val filters: MutableList<SqlExpr> = mutableListOf()

        // Add status filters based on the payment status
        filters.add(
            StatusExpr(
                params.payment
                    .flatMap {
                        when (it) {
                            PaymentFilter.CONFIRMED -> listOf(ReservationStatus.Confirmed)
                            PaymentFilter.INVOICED -> listOf(ReservationStatus.Invoiced)
                            PaymentFilter.PAYMENT -> listOf(ReservationStatus.Payment, ReservationStatus.Info)
                            PaymentFilter.CANCELLED -> listOf(ReservationStatus.Cancelled)
                        }
                    }.ifEmpty {
                        listOf(
                            ReservationStatus.Confirmed,
                            ReservationStatus.Invoiced,
                            ReservationStatus.Payment,
                            ReservationStatus.Info,
                            ReservationStatus.Cancelled
                        )
                    }
            )
        )

        if (params.expiration == ReservationExpiration.Active) {
            filters.add(EndDateNotPassedExpr(timeProvider.getCurrentDate()))
        } else {
            filters.add(EndDatePassedExpr(timeProvider.getCurrentDate()))
        }

        if (params.warningFilter == true) {
            filters.add(HasWarningExpr())
        }

        if (params.exceptionsFilter == true) {
            filters.add(HasReserverExceptionsExpr())
        }

        if (!params.nameSearch.isNullOrBlank()) {
            filters.add(NameSearchExpr(params.nameSearch))
        }

        if (!params.phoneSearch.isNullOrBlank()) {
            filters.add(PhoneSearchExpr(params.phoneSearch))
        }

        if (params.harbor.isNotEmpty()) {
            filters.add(LocationExpr(params.harbor))
        }

        if (params.boatSpaceType.isNotEmpty()) {
            filters.add(BoatSpaceTypeExpr(params.boatSpaceType))
        }

        if (params.amenity.isNotEmpty()) {
            filters.add(AmenityExpr(params.amenity))
        }

        if (params.sectionFilter.isNotEmpty()) {
            filters.add(SectionExpr(params.sectionFilter))
        }

        if (params.validity.isNotEmpty()) {
            filters.add(ReservationValidityExpr(params.validity))
        }

        val direction = if (params.ascending) SortDirection.Ascending else SortDirection.Descending
        val sortBy =
            BoatSpaceReservationSortBy(
                listOf(params.sortBy to direction)
            )

        return boatSpaceReservationRepo.getBoatSpaceReservations(
            AndExpr(
                filters
            ),
            sortBy
        )
    }

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

    fun acknowledgeWarnings(
        reservationId: Int,
        userId: UUID,
        boatOrTrailerId: Int,
        keys: List<String>,
        infoText: String,
    ) {
        if (keys.isEmpty()) return
        reservationWarningRepo.setReservationWarningsAcknowledged(reservationId, boatOrTrailerId, keys)
        val reservation = getReservationWithDependencies(reservationId)
        if (reservation?.reserverId == null) {
            throw IllegalArgumentException("No reservation or reservation has no reserver")
        }
        memoService.insertMemo(reservation.reserverId, userId, infoText)
    }

    fun acknowledgeWarningForBoat(
        boatId: Int,
        userId: UUID,
        keys: List<String>,
        infoText: String
    ) {
        if (keys.isEmpty()) return
        getReservationsForBoat(boatId).forEach {
            acknowledgeWarnings(it.id, userId, boatId, keys, infoText)
        }
    }

    fun acknowledgeWarningForTrailer(
        trailerId: Int,
        userId: UUID,
        keys: List<String>,
        infoText: String
    ) {
        if (keys.isEmpty()) return
        val reservationsWithTrailer = getReservationsForTrailer(trailerId)
        reservationsWithTrailer.forEach {
            acknowledgeWarnings(it.id, userId, trailerId, keys, infoText)
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
        if (recipient?.id == null || recipient.email == null) {
            return null
        }
        return recipient
    }

    fun markReservationEnded(reservationId: Int) {
        boatSpaceReservationRepo.setReservationAsExpired(reservationId)
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

        val placeTypeText = t("boatSpaceReservation.email.types.${reservation.type}")

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
                        .intToDecimal(boatSpace.lengthCm),
                "amenity" to t("boatSpaces.amenityOption.${boatSpace.amenity}"),
                "endDate" to
                    t(
                        "boatSpaceReservation.email.validity.${reservation.validity}",
                        listOf(formatAsFullDate(reservation.endDate))
                    ),
                "placeType" to placeTypeText,
            )

        data class EmailSettings(
            val template: String,
            val recipients: List<String>,
            val params: Map<String, Any>
        )
        val invoiceAddress = "${reservation.streetAddress}, ${reservation.postalCode}"

        val recipients =
            if (reservation.reserverType == ReserverType.Organization) {
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
                                params =
                                    defaultParams
                                        .plus("reservationDescription" to "$placeTypeText $placeName")
                            )
                        } else {
                            EmailSettings(
                                template = "reservation_created_by_employee",
                                recipients = recipients,
                                params =
                                    defaultParams
                                        .plus("reservationDescription" to "$placeTypeText $placeName")
                                        .plus("invoiceAddress" to invoiceAddress)
                                        .plus("invoiceDueDate" to formatAsFullDate(getInvoiceDueDate(timeProvider)))
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
                                params =
                                    defaultParams
                                        .plus("reservationDescription" to "$placeTypeText $placeName")
                            )
                        } else {
                            EmailSettings(
                                template = "reservation_renewed_by_employee",
                                recipients = recipients,
                                params =
                                    defaultParams
                                        .plus("reservationDescription" to "$placeTypeText $placeName")
                                        .plus("invoiceAddress" to invoiceAddress)
                                        .plus("invoiceDueDate" to formatAsFullDate(getInvoiceDueDate(timeProvider)))
                            )
                        }
                    } else {
                        EmailSettings(
                            template = "reservation_renewed_by_citizen",
                            recipients = recipients,
                            params =
                                defaultParams
                                    .plus("reservationDescription" to "$placeTypeText $placeName")
                                    .plus("invoiceAddress" to invoiceAddress)
                                    .plus("invoiceDueDate" to formatAsFullDate(getInvoiceDueDate(timeProvider)))
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
}
