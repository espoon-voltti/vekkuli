package fi.espoo.vekkuli.service

import fi.espoo.vekkuli.boatSpace.reservationForm.UnauthorizedException
import fi.espoo.vekkuli.boatSpace.seasonalService.SeasonalService
import fi.espoo.vekkuli.common.Unauthorized
import fi.espoo.vekkuli.config.*
import fi.espoo.vekkuli.config.BoatSpaceConfig.BOAT_WEIGHT_THRESHOLD_KG
import fi.espoo.vekkuli.config.BoatSpaceConfig.isLengthOk
import fi.espoo.vekkuli.config.BoatSpaceConfig.isWidthOk
import fi.espoo.vekkuli.domain.*
import fi.espoo.vekkuli.repository.*
import fi.espoo.vekkuli.repository.filter.SortDirection
import fi.espoo.vekkuli.repository.filter.boatspacereservation.*
import fi.espoo.vekkuli.repository.filter.boatspacereservation.LocationExpr
import fi.espoo.vekkuli.utils.*
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

    fun setReservationWarningAcknowledged(
        reservationId: Int,
        boatIdOrTrailerId: Int,
        key: String,
    ): Unit
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
    private val organizationService: OrganizationService,
    private val timeProvider: TimeProvider,
    private val memoService: MemoService,
    private val permissionService: PermissionService,
    private val seasonalService: SeasonalService,
    private val trailerRepository: TrailerRepository
) {
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

        if (reservation.renewedFromId != null) {
            markReservationEnded(reservation.renewedFromId)
        }

        if (payment.status != PaymentStatus.Created) return PaymentProcessResult.HandledAlready(reservation)

        handleReservationPaymentResult(stamp, paymentSuccess)

        if (paymentSuccess) {
            sendConfirmationEmail(reservation, payment)
        }
        return PaymentProcessResult.Success(reservation)
    }

    private fun sendConfirmationEmail(
        reservation: BoatSpaceReservationDetails,
        payment: Payment,
    ) {
        if (reservation.reserverType == ReserverType.Organization) {
            val members = organizationService.getOrganizationMembers(reservation.reserverId)
            val organisationMembers = members.map { Recipient(it.id, it.email) }
            val organizationInfo = Recipient(reservation.reserverId, reservation.email)
            emailService
                .sendBatchEmail(
                    "reservation_organization_confirmation",
                    null,
                    emailEnv.senderAddress,
                    listOf(organizationInfo) + organisationMembers,
                    mapOf(
                        "organizationName" to reservation.name,
                        "name" to "${reservation.locationName} ${reservation.place}",
                        "width" to reservation.boatSpaceWidthInM,
                        "length" to reservation.boatSpaceLengthInM,
                        "amenity" to messageUtil.getMessage("boatSpaces.amenityOption.${reservation.amenity}"),
                        "endDate" to reservation.endDate
                    )
                )
        } else {
            emailService.sendEmail(
                "varausvahvistus",
                null,
                emailEnv.senderAddress,
                Recipient(payment.reserverId, reservation.email),
                mapOf(
                    "name" to " ${reservation.locationName} ${reservation.place}",
                    "width" to reservation.boatSpaceWidthInM,
                    "length" to reservation.boatSpaceLengthInM,
                    "amenity" to messageUtil.getMessage("boatSpaces.amenityOption.${reservation.amenity}"),
                    "endDate" to reservation.endDate
                )
            )
        }
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
        boatSpaceReservationRepo.getBoatSpaceReservation(reservationId)

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

    fun getUnfinishedReservationForCitizen(id: UUID): ReservationWithDependencies? =
        boatSpaceReservationRepo.getUnfinishedReservationForCitizen(id)

    fun getUnfinishedReservationForEmployee(id: UUID): ReservationWithDependencies? =
        boatSpaceReservationRepo.getUnfinishedReservationForEmployee(id)

    fun insertBoatSpaceReservation(
        reserverId: UUID,
        actingUserId: UUID?,
        boatSpaceId: Int,
        startDate: LocalDate,
        endDate: LocalDate,
    ): BoatSpaceReservation =
        boatSpaceReservationRepo.insertBoatSpaceReservation(
            reserverId,
            actingUserId,
            boatSpaceId,
            startDate,
            endDate,
        )

    fun insertBoatSpaceReservationAsEmployee(
        employeeId: UUID,
        boatSpaceId: Int,
        startDate: LocalDate,
        endDate: LocalDate,
    ): BoatSpaceReservation =
        boatSpaceReservationRepo.insertBoatSpaceReservationAsEmployee(
            employeeId,
            boatSpaceId,
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
                            PaymentFilter.PAID -> listOf(ReservationStatus.Confirmed, ReservationStatus.Cancelled)
                            PaymentFilter.UNPAID -> listOf(ReservationStatus.Invoiced)
                        }
                    }.ifEmpty { listOf(ReservationStatus.Confirmed, ReservationStatus.Invoiced, ReservationStatus.Cancelled) }
            )
        )

        filters.add(EndDateNotPassedExpr(timeProvider.getCurrentDate()))

        if (params.warningFilter == true) {
            filters.add(HasWarningExpr())
        }

        if (!params.nameSearch.isNullOrBlank()) {
            filters.add(NameSearchExpr(params.nameSearch))
        }

        if (params.harbor.isNotEmpty()) {
            filters.add(LocationExpr(params.harbor))
        }

        if (params.amenity.isNotEmpty()) {
            filters.add(AmenityExpr(params.amenity))
        }
        if (params.sectionFilter.isNotEmpty()) {
            filters.add(SectionExpr(params.sectionFilter))
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

    fun getBoatSpaceReservationsForCitizen(
        citizenId: UUID,
        spaceType: BoatSpaceType? = null
    ): List<BoatSpaceReservationDetails> =
        seasonalService.addPeriodInformationToReservation(
            citizenId,
            boatSpaceReservationRepo.getBoatSpaceReservationsForCitizen(
                citizenId,
                spaceType,
            )
        )

    fun getExpiredBoatSpaceReservationsForCitizen(citizenId: UUID): List<BoatSpaceReservationDetails> =
        boatSpaceReservationRepo.getExpiredBoatSpaceReservationsForCitizen(citizenId)

    fun getExpiringIndefiniteBoatSpaceReservations(): List<BoatSpaceReservationDetails> =
        boatSpaceReservationRepo.getExpiringBoatSpaceReservations(ReservationValidity.Indefinite)

    fun getExpiringFixedTermBoatSpaceReservations(): List<BoatSpaceReservationDetails> =
        boatSpaceReservationRepo.getExpiringBoatSpaceReservations(ReservationValidity.FixedTerm)

    fun acknowledgeWarning(
        reservationId: Int,
        userId: UUID,
        boatOrTrailerId: Int,
        key: String,
        infoText: String,
    ) {
        reservationWarningRepo.setReservationWarningAcknowledged(reservationId, boatOrTrailerId, key)
        val reservation = getReservationWithDependencies(reservationId)
        if (reservation?.reserverId == null) {
            throw IllegalArgumentException("No reservation or reservation has no reserver")
        }
        memoService.insertMemo(reservation.reserverId, userId, ReservationType.Marine, infoText)
    }

    fun acknowledgeWarningForTrailer(
        trailerId: Int,
        userId: UUID,
        key: String,
        infoText: String
    ) {
        val reservationsWithTrailer = getReservationsForTrailer(trailerId)
        reservationsWithTrailer.forEach {
            acknowledgeWarning(it.id, userId, trailerId, key, infoText)
        }
    }

    fun markInvoicePaid(
        reservationId: Int,
        paymentDate: LocalDateTime
    ) {
        boatSpaceReservationRepo.updateReservationInvoicePaid(reservationId)
        val reservation = boatSpaceReservationRepo.getBoatSpaceReservation(reservationId)
        if (reservation?.paymentId == null) {
            throw IllegalArgumentException("Reservation has no payment")
        }
        paymentService.updatePayment(reservation.paymentId, true, paymentDate)
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
                widthCm = trailerWidth.mToCm(),
                lengthCm = trailerLength.mToCm(),
                reserverId = oldTrailer.reserverId
            )

        val result = trailerRepository.updateTrailer(updatedTrailer)
        addTrailerWarningsToReservations(trailerId, updatedTrailer.widthCm, updatedTrailer.lengthCm)
        return result
    }
}
