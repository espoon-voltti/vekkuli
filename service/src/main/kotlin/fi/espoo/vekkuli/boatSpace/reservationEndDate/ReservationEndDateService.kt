package fi.espoo.vekkuli.boatSpace.reservationEndDate

import fi.espoo.vekkuli.common.Conflict
import fi.espoo.vekkuli.common.NotFound
import fi.espoo.vekkuli.common.Unauthorized
import fi.espoo.vekkuli.domain.ReservationStatus
import fi.espoo.vekkuli.domain.ReservationWithDependencies
import fi.espoo.vekkuli.repository.BoatSpaceReservationRepository
import fi.espoo.vekkuli.repository.OverlappingReservation
import fi.espoo.vekkuli.service.MemoService
import fi.espoo.vekkuli.service.PermissionService
import fi.espoo.vekkuli.utils.TimeProvider
import fi.espoo.vekkuli.utils.fullDateFormat
import fi.espoo.vekkuli.utils.reservationToText
import org.jdbi.v3.core.statement.UnableToExecuteStatementException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.util.UUID

sealed interface EndDateError {
    data object Missing : EndDateError

    data object BeforeStartDate : EndDateError

    data class OverlapsAnotherReservation(
        val conflict: OverlappingReservation
    ) : EndDateError
}

sealed interface EndDateWarning {
    data object FreesThePlaceEarlier : EndDateWarning

    data object EndsInThePast : EndDateWarning

    data class PendingReservationOnSameSpace(
        val pending: OverlappingReservation
    ) : EndDateWarning
}

data class EndDateValidationResult(
    val error: EndDateError?,
    val warnings: List<EndDateWarning>
) {
    val isValid: Boolean get() = error == null
}

@Service
class ReservationEndDateService(
    private val boatSpaceReservationRepository: BoatSpaceReservationRepository,
    private val permissionService: PermissionService,
    private val memoService: MemoService,
    private val timeProvider: TimeProvider
) {
    fun getEditableReservation(reservationId: Int): ReservationWithDependencies {
        val reservation =
            boatSpaceReservationRepository.getReservationWithDependencies(reservationId)
                ?: throw NotFound("Reservation not found")
        if (reservation.status !in reservedStatuses) {
            throw Conflict("Reservation end date can not be edited in status ${reservation.status}")
        }
        return reservation
    }

    fun validate(
        reservation: ReservationWithDependencies,
        newEndDate: LocalDate
    ): EndDateValidationResult {
        if (newEndDate.isBefore(reservation.startDate)) {
            return EndDateValidationResult(EndDateError.BeforeStartDate, emptyList())
        }

        val overlapping =
            boatSpaceReservationRepository.getReservationsOverlappingDateRange(
                reservation.boatSpaceId,
                reservation.id,
                reservation.startDate,
                newEndDate
            )

        val blocking = overlapping.firstOrNull { it.status in reservedStatuses }
        if (blocking != null) {
            return EndDateValidationResult(EndDateError.OverlapsAnotherReservation(blocking), emptyList())
        }

        val warnings =
            buildList {
                if (newEndDate.isBefore(reservation.endDate)) add(EndDateWarning.FreesThePlaceEarlier)
                if (newEndDate.isBefore(timeProvider.getCurrentDate())) add(EndDateWarning.EndsInThePast)
                overlapping.forEach { add(EndDateWarning.PendingReservationOnSameSpace(it)) }
            }

        return EndDateValidationResult(null, warnings)
    }

    @Transactional
    fun updateEndDate(
        reservationId: Int,
        newEndDate: LocalDate,
        employeeId: UUID
    ) {
        if (!permissionService.canUpdateReservationEndDate(employeeId, reservationId)) {
            throw Unauthorized()
        }

        val reservation = getEditableReservation(reservationId)
        val validation = validate(reservation, newEndDate)
        if (validation.error != null) {
            throw Conflict("New end date is not valid: ${validation.error}")
        }

        val updated =
            try {
                boatSpaceReservationRepository.updateReservationEndDate(reservationId, newEndDate)
            } catch (e: UnableToExecuteStatementException) {
                throw Conflict("Boat space is reserved for the new date range", cause = e)
            }
                ?: throw NotFound("Reservation not found")

        val reserverId = reservation.reserverId ?: return
        memoService.insertMemo(
            reserverId,
            employeeId,
            "Varauksen ${reservationToText(reservation)} loppupäivä muutettu: " +
                "${reservation.endDate.format(fullDateFormat)} -> ${updated.endDate.format(fullDateFormat)}"
        )
    }

    companion object {
        private val reservedStatuses = setOf(ReservationStatus.Confirmed, ReservationStatus.Invoiced)
    }
}
