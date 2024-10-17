package fi.espoo.vekkuli.repository

import fi.espoo.vekkuli.domain.*
import java.time.LocalDate
import java.util.*

interface BoatSpaceReservationRepository {
    fun getBoatSpaceReservationIdForPayment(id: UUID): Int

    fun getBoatSpaceReservationWithPaymentId(id: UUID): BoatSpaceReservationDetails?

    fun updateBoatSpaceReservationOnPaymentSuccess(paymentId: UUID): Int?

    fun getUnfinishedReservationForCitizen(id: UUID): ReservationWithDependencies?

    fun getUnfinishedReservationForEmployee(id: UUID): ReservationWithDependencies?

    fun getReservationWithReserver(id: Int): ReservationWithDependencies?

    fun getReservationWithoutReserver(id: Int): ReservationWithDependencies?

    fun removeBoatSpaceReservation(
        id: Int,
        reserverId: UUID,
    ): Unit

    fun getBoatSpaceReservationsForCitizen(reserverId: UUID): List<BoatSpaceReservationDetails>

    fun getBoatSpaceReservation(reservationId: Int): BoatSpaceReservationDetails?

    fun getBoatSpaceReservations(params: BoatSpaceReservationFilter): List<BoatSpaceReservationItem>

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
        reservationStatus: ReservationStatus
    ): BoatSpaceReservation

    fun setReservationStatusToPayment(reservationId: Int): BoatSpaceReservation

    fun updateReservationInvoicePaid(reservationId: Int): BoatSpaceReservation

    fun getReservationPeriods(): List<ReservationPeriod>
}
