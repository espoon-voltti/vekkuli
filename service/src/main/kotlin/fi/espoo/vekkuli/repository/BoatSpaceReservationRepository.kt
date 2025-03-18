package fi.espoo.vekkuli.repository

import fi.espoo.vekkuli.domain.*
import java.time.LocalDate
import java.util.*

data class UpdateReservationParams(
    val reservationId: Int,
    val boatId: Int,
    val reserverId: UUID,
    val reservationStatus: ReservationStatus,
    val validity: ReservationValidity,
    val startDate: LocalDate,
    val endDate: LocalDate
)

interface BoatSpaceReservationRepository {
    fun getBoatSpaceReservationIdForPayment(id: UUID): Int

    fun getBoatSpaceReservationWithPaymentId(id: UUID): BoatSpaceReservationDetails?

    fun updateBoatSpaceReservationOnPaymentSuccess(paymentId: UUID): Int?

    fun getUnfinishedReservationForCitizen(id: UUID): ReservationWithDependencies?

    fun getUnfinishedReservationForEmployee(id: UUID): ReservationWithDependencies?

    fun getReservationForRenewal(id: Int): ReservationWithDependencies?

    fun getReservationWithReserverInInfoPaymentRenewalStateWithinSessionTime(id: Int): ReservationWithDependencies?

    fun getReservationReserverEmail(reservationId: Int): Recipient?

    fun getReservationWithDependencies(id: Int): ReservationWithDependencies?

    fun getReservationWithoutReserver(id: Int): ReservationWithDependencies?

    fun removeBoatSpaceReservation(id: Int): Unit

    fun getBoatSpaceReservationsForReserver(
        reserverId: UUID,
        spaceType: BoatSpaceType?
    ): List<BoatSpaceReservationDetails>

    fun getBoatSpaceReservationDetails(reservationId: Int): BoatSpaceReservationDetails?

    fun getBoatSpaceRelatedToReservation(reservationId: Int): BoatSpace?

    fun insertBoatSpaceReservation(
        reserverId: UUID,
        actingCitizenId: UUID?,
        boatSpaceId: Int,
        creationType: CreationType,
        startDate: LocalDate,
        endDate: LocalDate,
        validity: ReservationValidity,
    ): BoatSpaceReservation

    fun insertBoatSpaceReservationAsEmployee(
        employeeId: UUID,
        boatSpaceId: Int,
        creationType: CreationType,
        startDate: LocalDate,
        endDate: LocalDate,
    ): BoatSpaceReservation

    fun updateBoatInBoatSpaceReservation(
        reservationId: Int,
        boatId: Int,
        reserverId: UUID,
        reservationStatus: ReservationStatus,
        validity: ReservationValidity,
        startDate: LocalDate,
        endDate: LocalDate,
    ): BoatSpaceReservation

    fun updateTrailerInBoatSpaceReservation(
        reservationId: Int,
        trailerId: Int
    ): BoatSpaceReservation

    fun setReservationStatusToInvoiced(reservationId: Int): BoatSpaceReservation

    fun updateReservationInvoicePaid(reservationId: Int): BoatSpaceReservation?

    fun getExpiredBoatSpaceReservationsForReserver(reserverId: UUID): List<BoatSpaceReservationDetails>

    fun updateReservationStatus(
        reservationId: Int,
        status: ReservationStatus
    ): BoatSpaceReservation?

    fun getExpiringBoatSpaceReservations(validity: ReservationValidity): List<BoatSpaceReservationDetails>

    fun getExpiredBoatSpaceReservations(): List<BoatSpaceReservationDetails>

    fun setReservationAsExpired(reservationId: Int)

    fun getHarbors(): List<Location>

    fun updateStorageType(
        reservationId: Int,
        storageType: StorageType
    ): BoatSpaceReservation

    fun getReservationsForBoat(boatId: Int): List<BoatSpaceReservationDetails>

    fun getActiveReservationsForBoat(boatId: Int): List<BoatSpaceReservationDetails>

    fun getReservationsForTrailer(trailerId: Int): List<BoatSpaceReservationDetails>

    fun setReservationStatusToInfo(reservationId: Int): BoatSpaceReservation

    fun updateReservationValidity(
        reservationId: Int,
        newValidity: ReservationValidity,
        endDate: LocalDate
    )
}
