package fi.espoo.vekkuli.service

import fi.espoo.vekkuli.config.BoatSpaceConfig.BOAT_WEIGHT_THRESHOLD_KG
import fi.espoo.vekkuli.config.BoatSpaceConfig.isLengthOk
import fi.espoo.vekkuli.config.BoatSpaceConfig.isWidthOk
import fi.espoo.vekkuli.config.Dimensions
import fi.espoo.vekkuli.config.EmailEnv
import fi.espoo.vekkuli.config.MessageUtil
import fi.espoo.vekkuli.config.ReservationWarningType
import fi.espoo.vekkuli.domain.*
import fi.espoo.vekkuli.repository.BoatRepository
import fi.espoo.vekkuli.repository.BoatSpaceReservationRepository
import fi.espoo.vekkuli.repository.ReserverRepository
import fi.espoo.vekkuli.repository.UpdateCitizenParams
import fi.espoo.vekkuli.utils.mToCm
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.util.*

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
) {
    fun handlePaymentResult(
        params: Map<String, String>,
        success: Boolean
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

        handleReservationPaymentResult(stamp, success)

        if (!success) {
            return PaymentProcessResult.Success(reservation)
        }

        emailService.sendEmail(
            "varausvahvistus",
            null,
            emailEnv.senderAddress,
            payment.citizenId,
            reservation.email,
            mapOf(
                "name" to " ${reservation.locationName} ${reservation.place}",
                "width" to reservation.boatSpaceWidthInM,
                "length" to reservation.boatSpaceLengthInM,
                "amenity" to messageUtil.getMessage("boatSpaces.amenityOption.${reservation.amenity}"),
                "endDate" to reservation.endDate
            )
        )

        return PaymentProcessResult.Success(reservation)
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
        boatSpaceReservationRepo.updateReservationWithPayment(reservationId, payment.id, params.citizenId)
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

    fun getReservationWithCitizen(id: Int): ReservationWithDependencies? = boatSpaceReservationRepo.getReservationWithCitizen(id)

    fun getReservationWithoutCitizen(id: Int): ReservationWithDependencies? = boatSpaceReservationRepo.getReservationWithoutCitizen(id)

    fun removeBoatSpaceReservation(
        id: Int,
        citizenId: UUID,
    ): Unit = boatSpaceReservationRepo.removeBoatSpaceReservation(id, citizenId)

    fun getBoatSpaceReservation(
        reservationId: Int,
        citizenId: UUID,
    ): BoatSpaceReservationDetails? = boatSpaceReservationRepo.getBoatSpaceReservation(reservationId, citizenId)

    fun getBoatSpaceRelatedToReservation(reservationId: Int): BoatSpace? =
        boatSpaceReservationRepo.getBoatSpaceRelatedToReservation(reservationId)

    fun updateBoatInBoatSpaceReservation(
        reservationId: Int,
        boatId: Int,
        citizenId: UUID,
        status: ReservationStatus
    ): BoatSpaceReservation = boatSpaceReservationRepo.updateBoatInBoatSpaceReservation(reservationId, boatId, citizenId, status)

    @Transactional
    fun reserveBoatSpace(
        citizenId: UUID,
        input: ReserveBoatSpaceInput,
        reservationStatus: ReservationStatus
    ) {
        val boatSpace =
            getBoatSpaceRelatedToReservation(input.reservationId)
                ?: throw IllegalArgumentException("Reservation not found")
        val boat =
            if (input.boatId == 0 || input.boatId == null) {
                boatRepository.insertBoat(
                    citizenId,
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
                        reserverId = citizenId,
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
            UpdateCitizenParams(id = citizenId, phone = input.phone ?: "", email = input.email ?: "")
        )
        boatSpaceReservationRepo.updateBoatInBoatSpaceReservation(input.reservationId, boat.id, citizenId, reservationStatus)
    }

    fun getReservationForCitizen(id: UUID): ReservationWithDependencies? = boatSpaceReservationRepo.getReservationForCitizen(id)

    fun getReservationForEmployee(id: UUID): ReservationWithDependencies? = boatSpaceReservationRepo.getReservationForEmployee(id)

    fun insertBoatSpaceReservation(
        reserverId: UUID,
        boatSpaceId: Int,
        startDate: LocalDate,
        endDate: LocalDate,
    ): BoatSpaceReservation =
        boatSpaceReservationRepo.insertBoatSpaceReservation(
            reserverId,
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
        boatSpaceReservationRepo.getBoatSpaceReservationsForCitizen(citizenId)

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
}
