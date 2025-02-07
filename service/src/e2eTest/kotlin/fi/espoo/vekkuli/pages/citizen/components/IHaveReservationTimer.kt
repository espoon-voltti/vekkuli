package fi.espoo.vekkuli.pages.citizen.components

import com.microsoft.playwright.Locator
import fi.espoo.vekkuli.pages.BasePage

interface IHaveReservationTimer<T> : IGetByTestId<T> where T : BasePage, T : IHaveReservationTimer<T> {
    class ReservationTimerSection(
        val root: Locator
    )

    fun getReservationTimerSection() = ReservationTimerSection(getByDataTestId("reservation-timer"))
}
