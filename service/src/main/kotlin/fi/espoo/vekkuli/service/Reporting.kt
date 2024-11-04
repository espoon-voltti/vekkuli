package fi.espoo.vekkuli.service

import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.inTransactionUnchecked
import org.jdbi.v3.core.kotlin.mapTo
import org.unbescape.csv.CsvEscape

data class RawReportRow(
    // BoatSpace fields
    val boatSpaceId: String = "",
    val boatSpaceType: String = "",
    val section: String = "",
    val placeNumber: String = "",
    val amenity: String = "",
    val widthCm: Int,
    val lengthCm: Int,
    val description: String = "",
    // BoatSpaceReservation fields
    val reservationId: Int,
    val startDate: String = "",
    val endDate: String = "",
    val reservationStatus: String = "",
    // Citizen fields
    val citizenId: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val nationalId: String = "",
    // Payment fields
    val paymentId: String = "",
    val totalCents: String = "",
    val vatPercentage: String = "",
    val paymentStatus: String = "",
    val paymentCreated: String = "",
)

fun getRawReport(jdbi: Jdbi): List<RawReportRow> {
    return jdbi.inTransactionUnchecked { tx ->
        tx.createQuery(
            """
            SELECT 
                bs.id AS boat_space_id,
                bs.type AS boat_space_type,
                bs.section,
                bs.place_number,
                bs.amenity,
                bs.width_cm,
                bs.length_cm,
                bs.description,
                bsr.id AS reservation_id,
                bsr.start_date,
                bsr.end_date,
                bsr.status AS reservation_status,
                c.id AS citizen_id,
                c.first_name,
                c.last_name,
                c.national_id,
                p.id AS payment_id,
                p.total_cents,
                p.vat_percentage,
                p.status AS payment_status,
                p.created AS payment_created
            FROM 
                boat_space bs
            JOIN 
                boat_space_reservation bsr ON bs.id = bsr.boat_space_id
            JOIN 
                citizen c ON c.id = bsr.reserver_id
            LEFT JOIN 
                payment p ON p.reservation_id = bsr.id;

            """.trimIndent()
        )
            .mapTo<RawReportRow>()
            .list()
    }
}

const val CSV_FIELD_SEPARATOR = ";"
const val CSV_RECORD_SEPARATOR = "\r\n"

fun sanitizeCsvCellData(cellData: String): String {
    return CsvEscape.escapeCsv(escapeCsvInjection(cellData))
}

/**
 * Escapes dangerous characters in strings to prevent CSV injection.
 * Prepends dangerous characters with a single quote.
 */
private fun escapeCsvInjection(value: String): String {
    return if (value.startsWith("=") || value.startsWith("+") ||
        value.startsWith("-") || value.startsWith("@") ||
        value.startsWith("|") || value.startsWith("\\")
    ) {
        "'$value"
    } else {
        value
    }
}

fun rawReportToCsv(reportRows: List<RawReportRow>): String {
    val csvHeader =
        listOf(
            "id",
            "venepaikan tyyppi",
            "osasto",
            "paikan numero",
            "ominaisuudet",
            "l",
            "p",
            "varaus id",
            "varaus alkupvm",
            "varaus loppupvm",
            "varaustatus",
            "kuntalaisen id",
            "etunimi",
            "sukunimi",
            "kansalaisuus",
            "maksun id",
            "maksu sentteinä",
            "vat",
            "maksun status",
            "maksu luotu"
        ).joinToString(CSV_FIELD_SEPARATOR, postfix = CSV_RECORD_SEPARATOR)

    val csvContent = StringBuilder()
    csvContent.append(csvHeader)

    for (report in reportRows) {
        // val noObservations = sanitizeCsvCellData(report.noObservations?.joinToString(",") ?: "")

        csvContent.append(sanitizeCsvCellData(report.boatSpaceId.toString())).append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(report.boatSpaceType)).append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(report.section)).append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(report.placeNumber)).append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(report.amenity)).append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(report.widthCm.toString())).append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(report.lengthCm.toString())).append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(report.reservationId.toString())).append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(report.startDate.toString())).append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(report.endDate.toString())).append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(report.reservationStatus)).append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(report.citizenId.toString())).append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(report.firstName)).append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(report.lastName)).append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(report.nationalId)).append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(report.paymentId.toString())).append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(report.totalCents.toString())).append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(report.vatPercentage.toString())).append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(report.paymentStatus)).append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(report.paymentCreated.toString())).append(CSV_FIELD_SEPARATOR)
            .append(CSV_RECORD_SEPARATOR)
    }

    return csvContent.toString()
}
