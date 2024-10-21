package fi.espoo.vekkuli.service

import fi.espoo.vekkuli.config.BoatSpaceConfig.BOAT_WEIGHT_THRESHOLD_KG
import fi.espoo.vekkuli.config.BoatSpaceConfig.isLengthOk
import fi.espoo.vekkuli.config.BoatSpaceConfig.isWidthOk
import fi.espoo.vekkuli.config.Dimensions
import fi.espoo.vekkuli.config.DomainConstants.ESPOO_MUNICIPALITY_CODE
import fi.espoo.vekkuli.config.EmailEnv
import fi.espoo.vekkuli.config.MessageUtil
import fi.espoo.vekkuli.config.ReservationWarningType
import fi.espoo.vekkuli.domain.*
import fi.espoo.vekkuli.repository.BoatRepository
import fi.espoo.vekkuli.repository.BoatSpaceReservationRepository
import fi.espoo.vekkuli.repository.ReserverRepository
import fi.espoo.vekkuli.repository.UpdateCitizenParams
import fi.espoo.vekkuli.utils.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.MonthDay
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
        boatId: Int,
        keys: List<String>,
    ): Unit

    fun getWarningsForReservation(reservationId: Int): List<ReservationWarning>

    fun getWarningsForBoat(boatId: Int): List<ReservationWarning>

    fun setReservationWarningAcknowledged(
        reservationId: Int,
        boatId: Int,
        key: String,
    ): Unit
}

data class ReserveBoatSpaceInput(
    val reservationId: Int,
    val boatId: Int?,
    val boatType: BoatType,
    val width: Double,
    val length: Double,
    val depth: Double,
    val weight: Int?,
    val boatRegistrationNumber: String?,
    val boatName: String?,
    val otherIdentification: String?,
    val extraInformation: String?,
    val ownerShip: OwnershipStatus?,
    val email: String?,
    val phone: String?,
)

@Service
class BoatReservationService(
    private val paymentService: PaymentService,
    private val boatSpaceReservationRepo: BoatSpaceReservationRepository,
    private val reservationWarningRepo: ReservationWarningRepository,
    private val reserverRepo: ReserverRepository,
    private val boatRepository: BoatRepository,
    private val emailService: TemplateEmailService,
    private val messageUtil: MessageUtil,
    private val paytrail: PaytrailInterface,
    private val emailEnv: EmailEnv,
    private val organizationService: OrganizationService,
    private val timeProvider: TimeProvider,
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
                Recipient(payment.citizenId, reservation.email),
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
        stamp: UUID,
        success: Boolean
    ): Int? {
        paymentService.updatePayment(stamp, success)
        if (!success) return boatSpaceReservationRepo.getBoatSpaceReservationIdForPayment(stamp)

        val reservationId =
            boatSpaceReservationRepo.updateBoatSpaceReservationOnPaymentSuccess(
                stamp
            )

        return reservationId
    }

    @Transactional
    fun addPaymentToReservation(
        reservationId: Int,
        params: CreatePaymentParams
    ): Payment {
        val payment = paymentService.insertPayment(params, reservationId)
        boatSpaceReservationRepo.setReservationStatusToPayment(reservationId)
        return payment
    }

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
        excludedBoatTypes: List<BoatType>
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
            reservationWarningRepo.addReservationWarnings(reservationId, boatId, warnings)
        }
    }

    fun getReservationWithReserver(id: Int): ReservationWithDependencies? = boatSpaceReservationRepo.getReservationWithReserver(id)

    fun getReservationWithoutCitizen(id: Int): ReservationWithDependencies? = boatSpaceReservationRepo.getReservationWithoutReserver(id)

    fun removeBoatSpaceReservation(
        id: Int,
        citizenId: UUID,
    ): Unit = boatSpaceReservationRepo.removeBoatSpaceReservation(id, citizenId)

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

    @Transactional
    fun reserveBoatSpace(
        reserverId: UUID,
        input: ReserveBoatSpaceInput,
        reservationStatus: ReservationStatus,
        reservationValidity: ReservationValidity,
        startDate: LocalDate,
        endDate: LocalDate,
    ) {
        val boatSpace =
            getBoatSpaceRelatedToReservation(input.reservationId)
                ?: throw IllegalArgumentException("Reservation not found")
        val boat =
            if (input.boatId == 0 || input.boatId == null) {
                boatRepository.insertBoat(
                    reserverId,
                    input.boatRegistrationNumber ?: "",
                    input.boatName!!,
                    input.width.mToCm(),
                    input.length.mToCm(),
                    input.depth.mToCm(),
                    input.weight!!,
                    input.boatType,
                    input.otherIdentification ?: "",
                    input.extraInformation ?: "",
                    input.ownerShip!!
                )
            } else {
                boatRepository.updateBoat(
                    Boat(
                        id = input.boatId,
                        reserverId = reserverId,
                        registrationCode = input.boatRegistrationNumber ?: "",
                        name = input.boatName!!,
                        widthCm = input.width.mToCm(),
                        lengthCm = input.length.mToCm(),
                        depthCm = input.depth.mToCm(),
                        weightKg = input.weight!!,
                        type = input.boatType,
                        otherIdentification = input.otherIdentification ?: "",
                        extraInformation = input.extraInformation ?: "",
                        ownership = input.ownerShip!!
                    )
                )
            }
        addReservationWarnings(
            input.reservationId,
            boat.id,
            boatSpace.widthCm,
            boatSpace.lengthCm,
            boatSpace.amenity,
            boat.widthCm,
            boat.lengthCm,
            boat.ownership,
            boat.weightKg,
            boat.type,
            boatSpace.excludedBoatTypes ?: listOf()
        )

        reserverRepo.updateCitizen(
            UpdateCitizenParams(id = reserverId, phone = input.phone ?: "", email = input.email ?: "")
        )

        val reservation =
            boatSpaceReservationRepo.updateBoatInBoatSpaceReservation(
                input.reservationId,
                boat.id,
                reserverId,
                reservationStatus,
                reservationValidity,
                startDate,
                endDate
            )
        if (reservationStatus == ReservationStatus.Invoiced) {
            emailService.sendEmail(
                "reservation_confirmation_invoice",
                null,
                emailEnv.senderAddress,
                Recipient(reserverId, input.email!!),
                mapOf(
                    "name" to "${boatSpace.locationName} ${boatSpace.section}${boatSpace.placeNumber}",
                    "width" to boatSpace.widthCm.cmToM(),
                    "length" to boatSpace.lengthCm.cmToM(),
                    "amenity" to messageUtil.getMessage("boatSpaces.amenityOption.${boatSpace.amenity}"),
                    "endDate" to reservation.endDate,
                    // TODO: get from reservation
                    "invoiceDueDate" to dateToString(timeProvider.getCurrentDate().toLocalDate().plusDays(14))
                )
            )
        }
    }

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

    fun getBoatSpaceReservations(params: BoatSpaceReservationFilter): List<BoatSpaceReservationItem> =
        boatSpaceReservationRepo.getBoatSpaceReservations(params)

    fun getBoatSpaceReservationsForCitizen(citizenId: UUID): List<BoatSpaceReservationDetails> =
        boatSpaceReservationRepo.getBoatSpaceReservationsForCitizen(citizenId, BoatSpaceType.Slip)

    fun acknowledgeWarning(
        reservationId: Int,
        boatId: Int,
        key: String,
    ): Unit = reservationWarningRepo.setReservationWarningAcknowledged(reservationId, boatId, key)

    fun markInvoicePaid(
        reservationId: Int,
        paymentDate: LocalDate,
        info: String
    ) {
        val reservation = boatSpaceReservationRepo.updateReservationInvoicePaid(reservationId)
    }

    fun terminateBoatSpaceReservation(
        reservationId: Int,
        currentUser: CitizenWithDetails
    ) {
        // @TODO check wether user is allowed to terminate tha reservation
        boatSpaceReservationRepo.terminateBoatSpaceReservation(reservationId)
    }

    private fun getReservationPeriods(
        isEspooCitizen: Boolean,
        boatSpaceType: BoatSpaceType,
        operation: ReservationOperation
    ): List<ReservationPeriod> = boatSpaceReservationRepo.getReservationPeriods(isEspooCitizen, boatSpaceType, operation)

    fun hasActiveReservationPeriod(
        now: LocalDate,
        isEspooCitizen: Boolean,
        boatSpaceType: BoatSpaceType,
        operation: ReservationOperation
    ): Boolean {
        val periods = getReservationPeriods(isEspooCitizen, boatSpaceType, operation)
        val today = MonthDay.from(now)
        println("hasActiveReservationPeriod($now, $isEspooCitizen, $boatSpaceType, $operation)")
        println("Perids $periods")
        return periods.any {
            isMonthDayWithinRange(today, MonthDay.of(it.startMonth, it.startDay), MonthDay.of(it.endMonth, it.endDay))
        }
    }

    fun canReserveANewSlip(reserverID: UUID): ReservationResult {
        val reserver = reserverRepo.getReserverById(reserverID) ?: return ReservationResult.Failure(ReservationResultErrorCode.NoReserver)
        val reservations = boatSpaceReservationRepo.getBoatSpaceReservationsForCitizen(reserverID, BoatSpaceType.Slip)
        val hasSomePlace = reservations.isNotEmpty()
        println("hasSomePlace: $hasSomePlace, municipalityCode: ${reserver.municipalityCode}, num reservations ${reservations.size}")
        if (hasSomePlace && reserver.municipalityCode != ESPOO_MUNICIPALITY_CODE) {
            return ReservationResult.Failure(ReservationResultErrorCode.MaxReservations)
        }
        if (reservations.size >= 2) {
            // Only two reservations are allowed ever
            return return ReservationResult.Failure(ReservationResultErrorCode.MaxReservations)
        }
        val now = timeProvider.getCurrentDate().toLocalDate()

        println("\n\nNow is $now\n\n")
        val hasActivePeriod =
            hasActiveReservationPeriod(
                now,
                reserver.municipalityCode == ESPOO_MUNICIPALITY_CODE,
                BoatSpaceType.Slip,
                if (hasSomePlace) ReservationOperation.SecondNew else ReservationOperation.New
            )
        println("hasActivePeriod: $hasActivePeriod")
        if (!hasActivePeriod) {
            return ReservationResult.Failure(ReservationResultErrorCode.NotPossible)
        }
        val validity = if (hasSomePlace) ReservationValidity.FixedTerm else ReservationValidity.Indefinite
        val endDate = if (validity == ReservationValidity.Indefinite) getLastDayOfNextYearsJanuary(now.year) else getLastDayOfYear(now.year)
        return ReservationResult.Success(
            ReservationResultSuccess(
                now,
                endDate,
                validity
            )
        )
    }
}
