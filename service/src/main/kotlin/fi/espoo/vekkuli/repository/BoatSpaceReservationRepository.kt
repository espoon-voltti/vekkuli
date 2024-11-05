package fi.espoo.vekkuli.repository

import fi.espoo.vekkuli.controllers.UserType
import fi.espoo.vekkuli.domain.*
import fi.espoo.vekkuli.repository.filter.boatspacereservation.BoatSpaceReservationSortBy
import fi.espoo.vekkuli.utils.SqlExpr
import java.time.LocalDate
import java.util.*

interface BoatSpaceReservationRepository {
    fun getBoatSpaceReservationIdForPayment(id: UUID): Int

    fun getBoatSpaceReservationWithPaymentId(id: UUID): BoatSpaceReservationDetails?

    fun updateBoatSpaceReservationOnPaymentSuccess(paymentId: UUID): Int?

    fun getUnfinishedReservationForCitizen(id: UUID): ReservationWithDependencies?

    fun getUnfinishedReservationForEmployee(id: UUID): ReservationWithDependencies?

    fun getRenewalReservationForCitizen(id: UUID): ReservationWithDependencies?

    fun getRenewalReservationForEmployee(id: UUID): ReservationWithDependencies?

    fun getReservationForRenewal(id: Int): ReservationWithDependencies?

    fun getReservationWithReserver(id: Int): ReservationWithDependencies?

    fun getReservationWithDependencies(id: Int): ReservationWithDependencies?

    fun getReservationWithoutReserver(id: Int): ReservationWithDependencies?

    fun removeBoatSpaceReservation(
        id: Int,
        reserverId: UUID,
    ): Unit

    fun getBoatSpaceReservationsForCitizen(
        reserverId: UUID,
        spaceType: BoatSpaceType
    ): List<BoatSpaceReservationDetails>

    fun getBoatSpaceReservation(reservationId: Int): BoatSpaceReservationDetails?

    fun getBoatSpaceReservations(
        filter: SqlExpr,
        sortBy: BoatSpaceReservationSortBy? = null
    ): List<BoatSpaceReservationItem>

    fun getBoatSpaceRelatedToReservation(reservationId: Int): BoatSpace?

    fun createRenewalRow(
        reservationId: Int,
        userType: UserType,
        userId: UUID
    ): Int

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

    fun setReservationStatusToPayment(reservationId: Int): BoatSpaceReservation

    fun setReservationStatusToInvoiced(reservationId: Int): BoatSpaceReservation

    fun updateReservationInvoicePaid(reservationId: Int): BoatSpaceReservation

    fun terminateBoatSpaceReservation(reservationId: Int): BoatSpaceReservation

    fun getReservationPeriods(): List<ReservationPeriod>

    fun getExpiredBoatSpaceReservationsForCitizen(reserverId: UUID): List<BoatSpaceReservationDetails>

    fun getExpiringBoatSpaceReservations(validity: ReservationValidity): List<BoatSpaceReservationDetails>
}
