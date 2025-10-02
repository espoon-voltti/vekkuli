package fi.espoo.vekkuli.repository

import fi.espoo.vekkuli.domain.TrailerRow
import fi.espoo.vekkuli.domain.TrailerWithWarnings
import java.util.*

interface TrailerRepository {
    fun getTrailersForReserver(reserverId: UUID): List<TrailerRow>

    fun getTrailer(trailerId: Int): TrailerWithWarnings?

    fun updateTrailer(trailerWithWarnings: TrailerWithWarnings): TrailerRow

    fun insertTrailer(
        reserverId: UUID,
        registrationCode: String,
        widthCm: Int,
        lengthCm: Int,
    ): TrailerRow

    fun insertTrailerAndAddToReservation(
        reservationId: Int,
        reserverId: UUID,
        registrationCode: String,
        widthCm: Int,
        lengthCm: Int,
    ): TrailerRow

    fun deleteTrailer(trailerId: Int): Boolean
}
