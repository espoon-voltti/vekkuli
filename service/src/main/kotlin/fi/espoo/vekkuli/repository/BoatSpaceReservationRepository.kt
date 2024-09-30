package fi.espoo.vekkuli.repository

import fi.espoo.vekkuli.domain.*
import java.time.LocalDate
import java.util.*

interface BoatSpaceReservationRepository {
    fun getBoatSpaceReservationIdForPayment(id: UUID): Int

    fun getBoatSpaceReservationWithPaymentId(id: UUID): BoatSpaceReservationDetails?

    fun updateBoatSpaceReservationOnPaymentSuccess(paymentId: UUID): Int?

    fun getReservationForCitizen(id: UUID): ReservationWithDependencies?

    fun getReservationForEmployee(id: UUID): ReservationWithDependencies?

    fun getReservationWithCitizen(id: Int): ReservationWithDependencies?

    fun getReservationWithoutCitizen(id: Int): ReservationWithDependencies?

    fun removeBoatSpaceReservation(
        id: Int,
        reserverId: UUID,
    ): Unit

    fun getBoatSpaceReservationsForCitizen(reserverId: UUID): List<BoatSpaceReservationDetails>

    fun getBoatSpaceReservation(
        reservationId: Int,
        reserverId: UUID,
    ): BoatSpaceReservationDetails?

    fun getBoatSpaceReservations(params: BoatSpaceReservationFilter): List<BoatSpaceReservationItem>

    fun getBoatSpaceRelatedToReservation(reservationId: Int): BoatSpace?

    fun insertBoatSpaceReservation(
        reserverId: UUID,
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

    fun updateReservationWithPayment(
        reservationId: Int,
        paymentId: UUID,
        reserverId: UUID,
    ): BoatSpaceReservation

    fun updateReservationInvoicePaid(reservationId: Int): BoatSpaceReservation
}
