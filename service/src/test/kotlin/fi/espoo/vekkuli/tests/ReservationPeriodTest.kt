package fi.espoo.vekkuli.tests

import fi.espoo.vekkuli.domain.HasExistingReservationsTypes
import fi.espoo.vekkuli.domain.ReservationConditions
import fi.espoo.vekkuli.domain.ReservationResultErrorCode
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import kotlin.test.assertEquals

class ReservationPeriodTest {
    private val year = 2024

    private val espooCitizen =
        ReservationConditions(
            true,
            HasExistingReservationsTypes.No,
            LocalDateTime.of(year, 1, 1, 0, 0),
        )
    private val otherCitizen =
        ReservationConditions(
            false,
            HasExistingReservationsTypes.No,
            LocalDateTime.of(year, 1, 1, 0, 0)
        )

    @Test
    fun `should allow new slip for Espoo citizen within correct period`() {
        assertNull(espooCitizen.copy(currentDate = LocalDateTime.of(year, 3, 1, 0, 0)).canReserveSlip())
        assertNull(espooCitizen.copy(currentDate = LocalDateTime.of(year, 9, 30, 23, 59)).canReserveSlip())
    }

    @Test
    fun `should disallow new slip for Espoo citizen out of correct period`() {
        assertEquals(
            espooCitizen.copy(currentDate = LocalDateTime.of(year, 2, 29, 23, 59)).canReserveSlip(),
            ReservationResultErrorCode.NotWithinPeriod
        )
        assertEquals(
            espooCitizen.copy(currentDate = LocalDateTime.of(year, 10, 1, 0, 0)).canReserveSlip(),
            ReservationResultErrorCode.NotWithinPeriod
        )
    }

    @Test
    fun `should allow new slip for non Espoo citizen within correct period`() {
        assertNull(otherCitizen.copy(currentDate = LocalDateTime.of(year, 4, 1, 0, 0)).canReserveSlip())
        assertNull(otherCitizen.copy(currentDate = LocalDateTime.of(year, 9, 30, 23, 59)).canReserveSlip())
    }

    @Test
    fun `should disallow new slip for non Espoo citizen out of correct period`() {
        assertEquals(
            otherCitizen.copy(currentDate = LocalDateTime.of(year, 3, 31, 23, 59)).canReserveSlip(),
            ReservationResultErrorCode.NotWithinPeriod
        )
        assertEquals(
            otherCitizen.copy(currentDate = LocalDateTime.of(year, 10, 1, 0, 0)).canReserveSlip(),
            ReservationResultErrorCode.NotWithinPeriod
        )
    }

    @Test
    fun `should not allow second slip for Espoo citizen if out of period`() {
        assertEquals(
            espooCitizen
                .copy(
                    hasExistingReservationsTypes = HasExistingReservationsTypes.FixedTerm,
                    currentDate = LocalDateTime.of(year, 3, 31, 23, 59)
                ).canReserveSlip(),
            ReservationResultErrorCode.NotWithinPeriod
        )
        assertEquals(
            espooCitizen
                .copy(
                    hasExistingReservationsTypes = HasExistingReservationsTypes.FixedTerm,
                    currentDate = LocalDateTime.of(year, 10, 1, 0, 0)
                ).canReserveSlip(),
            ReservationResultErrorCode.NotWithinPeriod
        )
    }

    @Test
    fun `should allow second slip for Espoo citizen if first one is fixed term`() {
        assertNull(
            espooCitizen
                .copy(
                    hasExistingReservationsTypes = HasExistingReservationsTypes.FixedTerm,
                    currentDate = LocalDateTime.of(year, 4, 1, 0, 0)
                ).canReserveSlip()
        )
    }

    @Test
    fun `can reserve second place if first is indefinite`() {
        assertNull(
            espooCitizen
                .copy(
                    hasExistingReservationsTypes = HasExistingReservationsTypes.Indefinite,
                    currentDate = LocalDateTime.of(year, 4, 1, 0, 0)
                ).canReserveSlip()
        )
    }

    @Test
    fun `cannot add third place`() {
        assertEquals(
            espooCitizen
                .copy(
                    hasExistingReservationsTypes = HasExistingReservationsTypes.Both,
                    currentDate = LocalDateTime.of(2021, 4, 1, 0, 0)
                ).canReserveSlip(),
            ReservationResultErrorCode.AlreadyHasReservation
        )
    }

    @Test
    fun `should allow renewing indefinite slip for Espoo citizen within correct period`() {
        assertNull(
            espooCitizen
                .copy(
                    hasExistingReservationsTypes = HasExistingReservationsTypes.Indefinite,
                    currentDate = LocalDateTime.of(year, 1, 1, 0, 0)
                ).canRenewSlip()
        )
        assertNull(
            espooCitizen
                .copy(
                    hasExistingReservationsTypes = HasExistingReservationsTypes.Indefinite,
                    currentDate = LocalDateTime.of(year, 1, 31, 23, 59)
                ).canRenewSlip()
        )
    }

    @Test
    fun `should not allow renewing fixed term slip for Espoo citizen within correct period`() {
        assertEquals(
            espooCitizen
                .copy(
                    hasExistingReservationsTypes = HasExistingReservationsTypes.FixedTerm,
                    currentDate = LocalDateTime.of(year, 1, 1, 0, 0)
                ).canRenewSlip(),
            ReservationResultErrorCode.NotIndefinite
        )
        assertEquals(
            espooCitizen
                .copy(
                    hasExistingReservationsTypes = HasExistingReservationsTypes.FixedTerm,
                    currentDate = LocalDateTime.of(year, 1, 31, 23, 59)
                ).canRenewSlip(),
            ReservationResultErrorCode.NotIndefinite
        )
    }

    @Test
    fun `should not allow renewing indefinite slip for non Espoo citizen within correct period`() {
        assertEquals(
            otherCitizen
                .copy(
                    hasExistingReservationsTypes = HasExistingReservationsTypes.Indefinite,
                    currentDate = LocalDateTime.of(year, 1, 1, 0, 0)
                ).canRenewSlip(),
            ReservationResultErrorCode.NotEspooCitizen
        )
        assertEquals(
            otherCitizen
                .copy(
                    hasExistingReservationsTypes = HasExistingReservationsTypes.Indefinite,
                    currentDate = LocalDateTime.of(year, 1, 31, 23, 59)
                ).canRenewSlip(),
            ReservationResultErrorCode.NotEspooCitizen
        )
    }

    @Test
    fun `should allow changing slip for Espoo citizen within renewal and change period`() {
        assertNull(
            espooCitizen
                .copy(
                    hasExistingReservationsTypes = HasExistingReservationsTypes.Indefinite,
                    currentDate = LocalDateTime.of(year, 1, 30, 0, 0)
                ).canChangeSlip()
        )
    }

    @Test
    fun `should allow changing slip for Espoo citizen within change period`() {
        assertNull(
            espooCitizen
                .copy(
                    hasExistingReservationsTypes = HasExistingReservationsTypes.Indefinite,
                    currentDate = LocalDateTime.of(year, 3, 25, 0, 0)
                ).canChangeSlip()
        )
    }

    @Test
    fun `should disallow changing slip for Espoo citizen within correct period if there is no slip`() {
        assertEquals(
            espooCitizen
                .copy(
                    hasExistingReservationsTypes = HasExistingReservationsTypes.No,
                    currentDate = LocalDateTime.of(year, 2, 1, 0, 0)
                ).canChangeSlip(),
            ReservationResultErrorCode.NoReservations
        )
    }
}
