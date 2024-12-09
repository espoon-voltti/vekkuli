package fi.espoo.vekkuli.boatSpace.reservationForm

import fi.espoo.vekkuli.config.BoatSpaceConfig
import fi.espoo.vekkuli.domain.*
import fi.espoo.vekkuli.utils.TimeProvider
import fi.espoo.vekkuli.utils.centsToEuro
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.mapTo
import org.jdbi.v3.core.kotlin.withHandleUnchecked
import org.springframework.stereotype.Repository
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

open class ReservationForApplicationForm(
    val id: Int,
    val reserverId: UUID?,
    val boatId: Int?,
    val lengthCm: Int,
    val widthCm: Int,
    val amenity: BoatSpaceAmenity,
    val boatSpaceType: BoatSpaceType,
    val place: String,
    val locationName: String?,
    val validity: ReservationValidity?,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val priceCents: Int,
    val vatCents: Int,
    val netPriceCents: Int,
    val created: LocalDateTime,
    val excludedBoatTypes: List<BoatType>?,
    val section: String,
    val placeNumber: String,
) {
    val priceInEuro: String
        get() = priceCents.centsToEuro()
    val vatPriceInEuro: String
        get() = vatCents.centsToEuro()
    val priceWithoutVatInEuro: String
        get() = netPriceCents.centsToEuro()
}

@Repository
class ReservationFormRepository(
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
                        bsr.boat_id,
                        bs.length_cm,
                        bs.width_cm,
                        bs.amenity,
                        bs.section, 
                        bs.place_number,
                        bs.type as boat_space_type,
                        CONCAT(bs.section, ' ', TO_CHAR(bs.place_number, 'FM000')) as place,
                        location.name as location_name,
                        bsr.validity,
                        bsr.start_date,
                        bsr.end_date,
                        bsr.created,
                        price.price_cents, 
                        price.vat_cents, 
                        price.net_price_cents,
                        ARRAY_AGG(harbor_restriction.excluded_boat_type) as excluded_boat_types
                    FROM boat_space_reservation bsr
                    JOIN boat_space bs ON bsr.boat_space_id = bs.id
                    JOIN location ON bs.location_id = location.id
                    JOIN price ON bs.price_id = price.id
                    LEFT JOIN harbor_restriction ON harbor_restriction.location_id = bs.location_id
                    WHERE bsr.id = :id
                      AND bsr.status IN ('Info', 'Payment')
                      AND bsr.created > :currentTime - make_interval(secs => :sessionTimeInSeconds)
                    GROUP BY bsr.id, bs.id, location.name, price.price_cents, price.vat_cents, price.net_price_cents
                    """.trimIndent()
                )
            query.bind("id", id)
            query.bind("sessionTimeInSeconds", BoatSpaceConfig.SESSION_TIME_IN_SECONDS)
            query.bind("currentTime", timeProvider.getCurrentDateTime())
            query.mapTo<ReservationForApplicationForm>().findOne().orElse(null)
        }
}
