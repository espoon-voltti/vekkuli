package fi.espoo.vekkuli.tests

import fi.espoo.vekkuli.domain.CurrentPlace
import fi.espoo.vekkuli.domain.ReservationPeriod
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class ReservationPeriodTest {
    private val year = 2024

    private val espooCitizen =
        ReservationPeriod(
            LocalDateTime.of(year, 1, 1, 0, 0),
            year,
            true,
            CurrentPlace.No
        )
    private val otherCitizen =
        ReservationPeriod(
            LocalDateTime.of(year, 1, 1, 0, 0),
            year,
            false,
            CurrentPlace.No
        )

    @Test
    fun `should allow new slip for Espoo citizen within correct period`() {
        assertNotNull(espooCitizen.copy(currentTime = LocalDateTime.of(year, 3, 1, 0, 0)).canReserveSlip())
        assertNotNull(espooCitizen.copy(currentTime = LocalDateTime.of(year, 9, 30, 23, 59)).canReserveSlip())
    }

    @Test
    fun `should disallow new slip for Espoo citizen out of correct period`() {
        assertNull(espooCitizen.copy(currentTime = LocalDateTime.of(year, 2, 29, 23, 59)).canReserveSlip())
        assertNull(espooCitizen.copy(currentTime = LocalDateTime.of(year, 10, 1, 0, 0)).canReserveSlip())
    }

    @Test
    fun `should allow new slip for non Espoo citizen within correct period`() {
        assertNotNull(otherCitizen.copy(currentTime = LocalDateTime.of(year, 4, 1, 0, 0)).canReserveSlip())
        assertNotNull(otherCitizen.copy(currentTime = LocalDateTime.of(year, 9, 30, 23, 59)).canReserveSlip())
    }

    @Test
    fun `should disallow new slip for non Espoo citizen out of correct period`() {
        assertNull(otherCitizen.copy(currentTime = LocalDateTime.of(year, 3, 31, 23, 59)).canReserveSlip())
        assertNull(otherCitizen.copy(currentTime = LocalDateTime.of(year, 10, 1, 0, 0)).canReserveSlip())
    }

    @Test
    fun `should now allow second slip for Espoo citizen if out of period`() {
        assertNull(
            espooCitizen.copy(currentPlace = CurrentPlace.FixedTerm, currentTime = LocalDateTime.of(year, 3, 31, 23, 59)).canReserveSlip()
        )
        assertNull(
            espooCitizen.copy(currentPlace = CurrentPlace.FixedTerm, currentTime = LocalDateTime.of(year, 10, 1, 0, 0)).canReserveSlip()
        )
    }

    @Test
    fun `should allow second slip for Espoo citizen if first one is fixed term`() {
        assertNotNull(
            espooCitizen.copy(currentPlace = CurrentPlace.FixedTerm, currentTime = LocalDateTime.of(year, 4, 1, 0, 0)).canReserveSlip()
        )
    }

    @Test
    fun `cannot add second place if first is indefinite`() {
        assertNull(
            espooCitizen.copy(currentPlace = CurrentPlace.Indefinite, currentTime = LocalDateTime.of(year, 4, 1, 0, 0)).canReserveSlip()
        )
    }

    @Test
    fun `cannot add third place`() {
        assertNull(
            espooCitizen.copy(currentPlace = CurrentPlace.Both, currentTime = LocalDateTime.of(2021, 4, 1, 0, 0)).canReserveSlip()
        )
    }

    @Test
    fun `should allow renewing indefinite slip for Espoo citizen within correct period`() {
        assertNotNull(
            espooCitizen.copy(currentPlace = CurrentPlace.Indefinite, currentTime = LocalDateTime.of(year, 1, 1, 0, 0)).canRenewSlip()
        )
        assertNotNull(
            espooCitizen.copy(currentPlace = CurrentPlace.Indefinite, currentTime = LocalDateTime.of(year, 1, 31, 23, 59)).canRenewSlip()
        )
    }

    @Test
    fun `should not allow renewing fixed term slip for Espoo citizen within correct period`() {
        assertNull(
            espooCitizen.copy(currentPlace = CurrentPlace.FixedTerm, currentTime = LocalDateTime.of(year, 1, 1, 0, 0)).canRenewSlip()
        )
        assertNull(
            espooCitizen.copy(currentPlace = CurrentPlace.FixedTerm, currentTime = LocalDateTime.of(year, 1, 31, 23, 59)).canRenewSlip()
        )
    }

    @Test
    fun `should now allow renewing indefinite slip for non Espoo citizen within correct period`() {
        assertNull(
            otherCitizen.copy(currentPlace = CurrentPlace.Indefinite, currentTime = LocalDateTime.of(year, 1, 1, 0, 0)).canRenewSlip()
        )
        assertNull(
            otherCitizen.copy(currentPlace = CurrentPlace.Indefinite, currentTime = LocalDateTime.of(year, 1, 31, 23, 59)).canRenewSlip()
        )
    }

    @Test
    fun `should allow changing slip for Espoo citizen within correct period`() {
        assertNotNull(
            espooCitizen.copy(currentPlace = CurrentPlace.Indefinite, currentTime = LocalDateTime.of(year, 2, 1, 0, 0)).canChangeSlip()
        )
        assertNotNull(
            espooCitizen.copy(currentPlace = CurrentPlace.FixedTerm, currentTime = LocalDateTime.of(year, 9, 30, 23, 59)).canChangeSlip()
        )
    }

    @Test
    fun `should disallow changing slip for Espoo citizen within correct period if there is no slip`() {
        assertNull(
            espooCitizen.copy(currentPlace = CurrentPlace.No, currentTime = LocalDateTime.of(year, 2, 1, 0, 0)).canChangeSlip()
        )
    }
}
