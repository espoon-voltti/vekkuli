package fi.espoo.vekkuli.common

import fi.espoo.vekkuli.domain.ReservationStatus

fun reservationStatusToText(reservationStatus: ReservationStatus): String =
    when (reservationStatus) {
        ReservationStatus.Info -> "Info"
        ReservationStatus.Payment -> "Maksettavana"
        ReservationStatus.Confirmed -> "Maksettu"
        ReservationStatus.Invoiced -> "Laskutettavana"
        ReservationStatus.Cancelled -> "Irtisanottu"
    }
