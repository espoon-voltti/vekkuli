package fi.espoo.vekkuli.service

import fi.espoo.vekkuli.boatSpace.terminateReservation.ReservationTerminationReason
import fi.espoo.vekkuli.domain.BoatSpaceAmenity
import fi.espoo.vekkuli.domain.BoatSpaceType
import fi.espoo.vekkuli.domain.BoatType
import fi.espoo.vekkuli.domain.CreationType
import fi.espoo.vekkuli.domain.OwnershipStatus
import fi.espoo.vekkuli.domain.ReservationStatus
import fi.espoo.vekkuli.domain.ReservationWarning
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.inTransactionUnchecked
import org.jdbi.v3.core.kotlin.mapTo
import org.jdbi.v3.core.kotlin.withHandleUnchecked
import java.time.LocalDate
import java.time.LocalDateTime

data class StickerReportRow(
    val name: String?,
    val streetAddress: String?,
    val postalCode: String?,
    val postOffice: String?,
    val harbor: String?,
    val place: String?,
    val placeType: BoatSpaceType?,
    val amenity: BoatSpaceAmenity?,
    val boatName: String?,
    val boatType: BoatType?,
    val placeWidthCm: String?,
    val placeLengthCm: String?,
    val boatWidthCm: String?,
    val boatLengthCm: String?,
    val boatWeightKg: String?,
    val registrationCode: String?,
    val otherIdentification: String?,
    val ownership: OwnershipStatus?,
    val startDate: LocalDate?,
    val endDate: LocalDate?,
    val productCode: String?,
    val totalCents: String?,
    val paid: LocalDateTime?,
    val creationType: CreationType?,
    val email: String?,
)

fun getStickerReportRows(
    jdbi: Jdbi,
    createdCutoffDate: LocalDate
): List<StickerReportRow> =
    jdbi.inTransactionUnchecked { tx ->
        tx
            .createQuery(
                """
                SELECT
                    r.name, r.street_address, r.postal_code, r.post_office,
                    l.name AS harbor, CONCAT(bs.section, ' ', TO_CHAR(bs.place_number, 'FM000')) as place,
                    bs.type AS place_type, bs.amenity, b.name AS boat_name, b.type AS boat_type,
                    bs.width_cm AS place_width_cm, bs.length_cm AS place_length_cm, b.width_cm AS boat_width_cm, b.length_cm AS boat_length_cm, b.weight_kg AS boat_weight_kg, b.registration_code, b.other_identification,
                    b.ownership, bsr.start_date, bsr.end_date,
                    price.name AS product_code,
                    p.total_cents,
                    p.paid,
                    bsr.creation_type,
                    bsr.created,
                    r.email
                FROM boat_space_reservation bsr
                    JOIN reserver r ON r.id = bsr.reserver_id
                    JOIN boat_space bs ON bs.id = bsr.boat_space_id
                    JOIN location l ON l.id = bs.location_id
                    JOIN payment p ON p.reservation_id = bsr.id
                    LEFT JOIN boat b ON b.id = bsr.boat_id
                    LEFT JOIN price ON price.id = bs.price_id
                WHERE 
                    bsr.reserver_id IS NOT NULL
                    AND :minPaymentCreated::date <= p.created::date
                    AND :minPaymentCreated::date <= bsr.end_date
                    AND bsr.status = 'Confirmed'
                    AND p.status = 'Success'
                """.trimIndent()
            ).bind("minPaymentCreated", createdCutoffDate)
            .mapTo<StickerReportRow>()
            .list()
    }

data class BoatSpaceReportRow(
    val reservationId: Int?,
    val boatSpaceId: Int,
    val harbor: String?,
    val pier: String?,
    val place: String?,
    val placeWidthCm: String?,
    val placeLengthCm: String?,
    val boatWidthCm: String?,
    val boatLengthCm: String?,
    val amenity: BoatSpaceAmenity?,
    val name: String?,
    val municipality: String?,
    val registrationCode: String?,
    val totalCents: String?,
    val productCode: String?,
    val terminationTimestamp: LocalDateTime?,
    val terminationReason: ReservationTerminationReason?,
    val startDate: LocalDate?,
    val endDate: LocalDate?,
    val paid: LocalDateTime?,
    val creationType: CreationType?,
    val boatSpaceType: BoatSpaceType?,
    val reservationStatus: ReservationStatus?,
    val email: String?,
)

data class BoatSpaceReportRowWithWarnings(
    val boatSpaceReportRow: BoatSpaceReportRow,
    val warnings: List<ReservationWarning>
)

fun getWarningsBoatSpaceReportRows(
    jdbi: Jdbi,
    reportDate: LocalDateTime
): List<BoatSpaceReportRowWithWarnings> {
    val reservationWarnings =
        jdbi.withHandleUnchecked { handle ->
            handle
                .createQuery(
                    """
                    SELECT
                        id,
                        reservation_id,
                        boat_id, 
                        trailer_id,
                        invoice_number,
                        key,
                        info_text
                    FROM reservation_warning
                    """.trimIndent()
                ).mapTo<ReservationWarning>()
                .list()
        }

    if (reservationWarnings.isEmpty()) {
        return emptyList()
    }

    val reservationsWithWarningsIds: List<Int> = reservationWarnings.map { it.reservationId }.distinct()
    val reservationsWithWarnings =
        getBoatSpaceReportRows(jdbi, reportDate, reservationsWithWarningsIds)
            .map { row ->
                BoatSpaceReportRowWithWarnings(
                    boatSpaceReportRow = row,
                    warnings =
                        reservationWarnings.filter { warning ->
                            row.reservationId == warning.reservationId
                        }
                )
            }

    return reservationsWithWarnings
}

fun getBoatSpaceReportRows(
    jdbi: Jdbi,
    reportDate: LocalDateTime,
    ids: List<Int>? = null
): List<BoatSpaceReportRow> =
    jdbi.inTransactionUnchecked { tx ->
        val query =
            tx
                .createQuery(
                    """
                    SELECT
                        bsr.id AS reservation_id,
                        bs.id AS boat_space_id,
                        l.name AS harbor,
                        bs.section AS pier,
                        CONCAT(bs.section, ' ', TO_CHAR(bs.place_number, 'FM000')) AS place,
                        bs.width_cm AS place_width_cm, bs.length_cm AS place_length_cm,
                        b.width_cm AS boat_width_cm, b.length_cm AS boat_length_cm,
                        bs.amenity,
                        r.name,
                        coalesce(m.name, '') AS municipality,
                        b.registration_code,
                        p.total_cents,
                        price.name AS product_code,
                        bsr.termination_timestamp,
                        bsr.termination_reason,
                        bsr.start_date,
                        bsr.end_date,
                        p.paid,
                        bsr.creation_type,
                        bs.type AS boat_space_type,
                        bsr.status AS reservation_status,
                        r.email
                    FROM boat_space bs
                         LEFT JOIN location l ON l.id = bs.location_id
                         LEFT JOIN boat_space_reservation bsr ON bsr.boat_space_id = bs.id
                         LEFT JOIN reserver r ON r.id = bsr.reserver_id
                         LEFT JOIN payment p ON p.reservation_id = bsr.id AND p.status = 'Success'
                         LEFT JOIN boat b ON b.id = bsr.boat_id
                         LEFT JOIN municipality m ON m.code = r.municipality_code
                         LEFT JOIN price ON price.id = bs.price_id
                    WHERE
                        (bsr.start_date is NULL OR
                        (:reportDate::date >= bsr.start_date
                        AND :reportDate::date <= bsr.end_date))
                        ${if (!ids.isNullOrEmpty()) "AND bsr.id in (<ids>)" else ""}
                    ORDER BY harbor, pier, place
                    """.trimIndent()
                ).bind("reportDate", reportDate)
        if (!ids.isNullOrEmpty()) {
            query.bindList("ids", ids)
        }

        query
            .mapTo<BoatSpaceReportRow>()
            .list()
    }

fun getFreeBoatSpaceReportRows(
    jdbi: Jdbi,
    reportDate: LocalDateTime
): List<BoatSpaceReportRow> =
    jdbi.inTransactionUnchecked { tx ->
        val query =
            tx
                .createQuery(
                    """
WITH places_with_active_reservations AS (
    SELECT bs.id
    FROM boat_space bs
         JOIN boat_space_reservation bsr ON bsr.boat_space_id = bs.id
    WHERE                    
    :reportDate::date >= bsr.start_date 
    AND :reportDate::date <= bsr.end_date
    AND (bsr.status = 'Confirmed' OR bsr.status = 'Invoiced')
)
SELECT
    bs.id AS boat_space_id,
    l.name AS harbor,
    bs.section AS pier,
    CONCAT(bs.section, ' ', TO_CHAR(bs.place_number, 'FM000')) AS place,
    bs.width_cm AS place_width_cm, bs.length_cm AS place_length_cm,
    bs.amenity,
    bs.type AS boat_space_type,
    price.name AS product_code
FROM boat_space bs
    JOIN location l ON bs.location_id = l.id
    LEFT JOIN price ON price.id = bs.price_id
WHERE
    bs.is_active AND
    NOT EXISTS (
        SELECT 1
        FROM places_with_active_reservations paw
        WHERE paw.id = bs.id
    )
ORDER BY harbor, pier, place
                    """.trimIndent()
                ).bind("reportDate", reportDate)

        query
            .mapTo<BoatSpaceReportRow>()
            .list()
    }
