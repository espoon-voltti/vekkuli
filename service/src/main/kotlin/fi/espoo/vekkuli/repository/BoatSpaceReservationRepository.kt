package fi.espoo.vekkuli.repository

import fi.espoo.vekkuli.domain.*
import fi.espoo.vekkuli.repository.filter.boatspacereservation.BoatSpaceReservationSortBy
import fi.espoo.vekkuli.utils.SqlExpr
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

    fun getBoatSpaceReservationsForCitizen(
        reserverId: UUID,
        spaceType: BoatSpaceType?
    ): List<BoatSpaceReservationDetails>

    fun getBoatSpaceReservation(reservationId: Int): BoatSpaceReservationDetails?

    fun getBoatSpaceReservations(
        filter: SqlExpr,
        sortBy: BoatSpaceReservationSortBy? = null
    ): List<BoatSpaceReservationItem>

    fun getBoatSpaceRelatedToReservation(reservationId: Int): BoatSpace?

    fun insertBoatSpaceReservation(
        reserverId: UUID,
        actingUserId: UUID?,
        boatSpaceId: Int,
        startDate: LocalDate,
        endDate: LocalDate,
    ): BoatSpaceReservation

    fun insertBoatSpaceReservationAsEmployee(
        employeeId: UUID,
        boatSpaceId: Int,
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

    fun getExpiredBoatSpaceReservationsForCitizen(reserverId: UUID): List<BoatSpaceReservationDetails>

    fun getExpiringBoatSpaceReservations(validity: ReservationValidity): List<BoatSpaceReservationDetails>

    fun setReservationAsExpired(reservationId: Int)

    fun getHarbors(): List<Location>

    fun updateStorageType(
        reservationId: Int,
        storageType: StorageType
    ): BoatSpaceReservation

    fun getReservationsForTrailer(trailerId: Int): List<BoatSpaceReservationDetails>
}
