package fi.espoo.vekkuli.boatSpace.reservationForm

import fi.espoo.vekkuli.config.BoatSpaceConfig
import fi.espoo.vekkuli.domain.BoatSpaceAmenity
import fi.espoo.vekkuli.domain.BoatType
import fi.espoo.vekkuli.domain.ReservationStatus
import fi.espoo.vekkuli.domain.ReservationValidity
import fi.espoo.vekkuli.utils.TimeProvider
import fi.espoo.vekkuli.utils.centsToEuro
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.mapTo
import org.jdbi.v3.core.kotlin.withHandleUnchecked
import org.springframework.stereotype.Repository
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

data class ReservationForApplicationForm(
    val id: Int,
    val reserverId: UUID,
    val boatId: Int,
    val lengthCm: Int,
    val widthCm: Int,
    val amenity: BoatSpaceAmenity,
    val type: String,
    val place: String,
    val locationName: String?,
    val validity: ReservationValidity,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val priceCents: Int,
    val vatCents: Int,
    val netPriceCents: Int,
    val created: LocalDateTime,
    val status: ReservationStatus,
    val excludedBoatTypes: List<BoatType>? = null,
) {
    val priceInEuro: String
        get() = priceCents.centsToEuro()
    val vatPriceInEuro: String
        get() = vatCents.centsToEuro()
    val priceWithoutVatInEuro: String
        get() = netPriceCents.centsToEuro()
}

@Repository
class ReservationRepository(
    private val jdbi: Jdbi,
    private val timeProvider: TimeProvider
) {
    fun getReservationForApplicationForm(id: Int): ReservationForApplicationForm? =
        jdbi.withHandleUnchecked { handle ->
            val query =
                handle.createQuery(
                    """
                    SELECT 
                        bsr.id, 
                        bsr.reserver_id, 
                        bs.id as boat_id,
                        bs.length_cm,
                        bs.width_cm,
                        bs.amenity,
                        bs.type,
                        CONCAT(bs.section, ' ', TO_CHAR(bs.place_number, 'FM000')) as place,
                        location.name as location_name,
                        bsr.validity,
                        bsr.start_date,
                        bsr.end_date,
                        bsr.created,
                        price.price_cents, 
                        price.vat_cents, 
                        price.net_price_cents
                    FROM boat_space_reservation bsr
                    JOIN boat_space bs ON bsr.boat_space_id = bs.id
                    JOIN location ON bsr.location_id = location.id
                    JOIN price ON bsr.price_id = price.id
                    WHERE bsr.id = :id
                      AND bsr.status IN ('Info', 'Payment')
                      AND bsr.created > :currentTime - make_interval(secs => :sessionTimeInSeconds)
                    """.trimIndent()
                )
            query.bind("id", id)
            query.bind("sessionTimeInSeconds", BoatSpaceConfig.SESSION_TIME_IN_SECONDS)
            query.bind("currentTime", timeProvider.getCurrentDateTime())
            query.mapTo<ReservationForApplicationForm>().findOne().orElse(null)
        }
}
