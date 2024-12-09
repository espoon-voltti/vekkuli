package fi.espoo.vekkuli.repository

import fi.espoo.vekkuli.domain.Trailer
import java.util.*

interface TrailerRepository {
    fun getTrailersForReserver(reserverId: UUID): List<Trailer>

    fun getTrailer(trailerId: Int): Trailer?

    fun updateTrailer(trailer: Trailer): Trailer

    fun insertTrailer(
        reserverId: UUID,
        registrationCode: String,
        widthCm: Int,
        lengthCm: Int,
    ): Trailer

    fun insertTrailerAndAddToReservation(
        reservationId: Int,
        reserverId: UUID,
        registrationCode: String,
        widthCm: Int,
        lengthCm: Int,
    ): Trailer

    fun deleteTrailer(trailerId: Int): Boolean
}
