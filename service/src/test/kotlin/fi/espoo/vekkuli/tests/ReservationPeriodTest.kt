package fi.espoo.vekkuli.tests

import fi.espoo.vekkuli.domain.ExistingReservations
import fi.espoo.vekkuli.domain.ReservationConditions
import fi.espoo.vekkuli.domain.ReservationResult
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import kotlin.test.assertEquals

class ReservationPeriodTest {
    private val year = 2024

    private val espooCitizen =
        ReservationConditions(
            true,
            ExistingReservations.No,
            year,
            LocalDateTime.of(year, 1, 1, 0, 0),
        )
    private val otherCitizen =
        ReservationConditions(
            false,
            ExistingReservations.No,
            year,
            LocalDateTime.of(year, 1, 1, 0, 0)
        )

    @Test
    fun `should allow new slip for Espoo citizen within correct period`() {
        assertNotNull(espooCitizen.copy(currentDate = LocalDateTime.of(year, 3, 1, 0, 0)).canReserveSlip())
        assertNotNull(espooCitizen.copy(currentDate = LocalDateTime.of(year, 9, 30, 23, 59)).canReserveSlip())
    }

    @Test
    fun `should disallow new slip for Espoo citizen out of correct period`() {
        assertNull(espooCitizen.copy(currentDate = LocalDateTime.of(year, 2, 29, 23, 59)).canReserveSlip())
        assertNull(espooCitizen.copy(currentDate = LocalDateTime.of(year, 10, 1, 0, 0)).canReserveSlip())
    }

    @Test
    fun `should allow new slip for non Espoo citizen within correct period`() {
        assertNotNull(otherCitizen.copy(currentDate = LocalDateTime.of(year, 4, 1, 0, 0)).canReserveSlip())
        assertNotNull(otherCitizen.copy(currentDate = LocalDateTime.of(year, 9, 30, 23, 59)).canReserveSlip())
    }

    @Test
    fun `should disallow new slip for non Espoo citizen out of correct period`() {
        assertNull(otherCitizen.copy(currentDate = LocalDateTime.of(year, 3, 31, 23, 59)).canReserveSlip())
        assertNull(otherCitizen.copy(currentDate = LocalDateTime.of(year, 10, 1, 0, 0)).canReserveSlip())
    }

    @Test
    fun `should now allow second slip for Espoo citizen if out of period`() {
        assertNull(
            espooCitizen
                .copy(
                    existingReservations = ExistingReservations.FixedTerm,
                    currentDate = LocalDateTime.of(year, 3, 31, 23, 59)
                ).canReserveSlip()
        )
        assertNull(
            espooCitizen
                .copy(
                    existingReservations = ExistingReservations.FixedTerm,
                    currentDate = LocalDateTime.of(year, 10, 1, 0, 0)
                ).canReserveSlip()
        )
    }

    @Test
    fun `should allow second slip for Espoo citizen if first one is fixed term`() {
        assertNotNull(
            espooCitizen
                .copy(
                    existingReservations = ExistingReservations.FixedTerm,
                    currentDate = LocalDateTime.of(year, 4, 1, 0, 0)
                ).canReserveSlip()
        )
    }

    @Test
    fun `can reserve second place if first is indefinite`() {
        assertEquals(
            espooCitizen
                .copy(
                    existingReservations = ExistingReservations.Indefinite,
                    currentDate = LocalDateTime.of(year, 4, 1, 0, 0)
                ).canReserveSlip(),
            ReservationResult(
                LocalDateTime.of(year, 4, 1, 0, 0).toLocalDate(),
                LocalDateTime.of(year + 1, 1, 31, 0, 0).toLocalDate(),
                ExistingReservations.Indefinite
            )
        )
    }

    @Test
    fun `cannot add third place`() {
        assertNull(
            espooCitizen
                .copy(
                    existingReservations = ExistingReservations.Both,
                    currentDate = LocalDateTime.of(2021, 4, 1, 0, 0)
                ).canReserveSlip()
        )
    }

    @Test
    fun `should allow renewing indefinite slip for Espoo citizen within correct period`() {
        assertNotNull(
            espooCitizen
                .copy(
                    existingReservations = ExistingReservations.Indefinite,
                    currentDate = LocalDateTime.of(year, 1, 1, 0, 0)
                ).canRenewSlip()
        )
        assertNotNull(
            espooCitizen
                .copy(
                    existingReservations = ExistingReservations.Indefinite,
                    currentDate = LocalDateTime.of(year, 1, 31, 23, 59)
                ).canRenewSlip()
        )
    }

    @Test
    fun `should not allow renewing fixed term slip for Espoo citizen within correct period`() {
        assertNull(
            espooCitizen
                .copy(
                    existingReservations = ExistingReservations.FixedTerm,
                    currentDate = LocalDateTime.of(year, 1, 1, 0, 0)
                ).canRenewSlip()
        )
        assertNull(
            espooCitizen
                .copy(
                    existingReservations = ExistingReservations.FixedTerm,
                    currentDate = LocalDateTime.of(year, 1, 31, 23, 59)
                ).canRenewSlip()
        )
    }

    @Test
    fun `should now allow renewing indefinite slip for non Espoo citizen within correct period`() {
        assertNull(
            otherCitizen
                .copy(
                    existingReservations = ExistingReservations.Indefinite,
                    currentDate = LocalDateTime.of(year, 1, 1, 0, 0)
                ).canRenewSlip()
        )
        assertNull(
            otherCitizen
                .copy(
                    existingReservations = ExistingReservations.Indefinite,
                    currentDate = LocalDateTime.of(year, 1, 31, 23, 59)
                ).canRenewSlip()
        )
    }

    @Test
    fun `should allow changing slip for Espoo citizen within renewal and change period`() {
        assertNotNull(
            espooCitizen
                .copy(
                    existingReservations = ExistingReservations.Indefinite,
                    currentDate = LocalDateTime.of(year, 1, 30, 0, 0)
                ).canChangeSlip()
        )
    }

    @Test
    fun `should allow changing slip for Espoo citizen within change period`() {
        assertNotNull(
            espooCitizen
                .copy(
                    existingReservations = ExistingReservations.Indefinite,
                    currentDate = LocalDateTime.of(year, 3, 25, 0, 0)
                ).canChangeSlip()
        )
    }

    @Test
    fun `should disallow changing slip for Espoo citizen within correct period if there is no slip`() {
        assertNull(
            espooCitizen
                .copy(
                    existingReservations = ExistingReservations.No,
                    currentDate = LocalDateTime.of(year, 2, 1, 0, 0)
                ).canChangeSlip()
        )
    }
}
