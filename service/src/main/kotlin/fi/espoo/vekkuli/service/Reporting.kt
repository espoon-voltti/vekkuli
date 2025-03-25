package fi.espoo.vekkuli.service

import fi.espoo.vekkuli.boatSpace.terminateReservation.ReservationTerminationReason
import fi.espoo.vekkuli.config.ReservationWarningType
import fi.espoo.vekkuli.domain.BoatSpaceAmenity
import fi.espoo.vekkuli.domain.BoatSpaceType
import fi.espoo.vekkuli.domain.BoatType
import fi.espoo.vekkuli.domain.CreationType
import fi.espoo.vekkuli.domain.OwnershipStatus
import fi.espoo.vekkuli.domain.PaymentHistory
import fi.espoo.vekkuli.domain.PaymentType
import fi.espoo.vekkuli.domain.ReservationStatus
import fi.espoo.vekkuli.domain.ReservationWarning
import fi.espoo.vekkuli.utils.amenityToText
import fi.espoo.vekkuli.utils.boatSpaceTypeToText
import fi.espoo.vekkuli.utils.boatTypeToText
import fi.espoo.vekkuli.utils.ownershipStatusToText
import fi.espoo.vekkuli.utils.placeTypeToText
import fi.espoo.vekkuli.utils.reservationCreationTypeToText
import fi.espoo.vekkuli.utils.terminationReasonToText
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.inTransactionUnchecked
import org.jdbi.v3.core.kotlin.mapTo
import org.jdbi.v3.core.kotlin.withHandleUnchecked
import org.unbescape.csv.CsvEscape
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.collections.List

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
    val creationType: CreationType?
)

fun getStickerReport(
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
                    bsr.created
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

fun stickerReportToCsv(reportRows: List<StickerReportRow>): String {
    val csvHeader =
        listOf(
            "varaaja",
            "kotiosoite",
            "postinumero",
            "postitoimipaikka",
            "satama",
            "paikka",
            "paikan tyyppi",
            "paikan leveys",
            "paikan pituus",
            "paikan varuste",
            "veneen nimi",
            "veneen tyyppi",
            "veneen leveys",
            "veneen pituus",
            "veneen paino",
            "veneen rekisterinumero",
            "muu tunniste",
            "omistussuhde",
            "maksuluokka",
            "tyyppi",
            "maksupäivä",
            "hinta",
            "varauksen alkupvm",
            "varauksen loppupvm"
        ).joinToString(CSV_FIELD_SEPARATOR, postfix = CSV_RECORD_SEPARATOR)

    val csvContent = StringBuilder()
    csvContent.append(csvHeader)

    for (report in reportRows) {
        csvContent
            .append(sanitizeCsvCellData(report.name))
            .append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(report.streetAddress))
            .append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(report.postalCode))
            .append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(report.postOffice))
            .append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(report.harbor))
            .append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(report.place))
            .append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(placeTypeToText(report.placeType)))
            .append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(intToDecimal(report.placeWidthCm)))
            .append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(intToDecimal(report.placeLengthCm)))
            .append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(amenityToText(report.amenity)))
            .append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(report.boatName))
            .append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(boatTypeToText(report.boatType)))
            .append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(intToDecimal(report.boatWidthCm)))
            .append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(intToDecimal(report.boatLengthCm)))
            .append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(report.boatWeightKg))
            .append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(report.registrationCode))
            .append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(report.otherIdentification))
            .append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(ownershipStatusToText(report.ownership)))
            .append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(report.productCode))
            .append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(reservationCreationTypeToText(report.creationType)))
            .append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(localDateTimeToText(report.paid)))
            .append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(intToDecimal(report.totalCents)))
            .append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(localDateToText(report.startDate)))
            .append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(localDateToText(report.endDate)))
            .append(CSV_RECORD_SEPARATOR)
    }

    return csvContent.toString()
}

data class BoatSpaceReportRow(
    val reservationId: Int?,
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
)

data class BoatSpaceReportRowWithWarnings(
    val boatSpaceReportRow: BoatSpaceReportRow,
    val warnings: List<ReservationWarning>
)

fun getFreeBoatSpaceReport(
    jdbi: Jdbi,
    reportDate: LocalDateTime
): List<BoatSpaceReportRow> =
    getBoatSpaceReport(jdbi, reportDate).filter {
        it.startDate == null ||
            !(it.reservationStatus == ReservationStatus.Confirmed || it.reservationStatus == ReservationStatus.Invoiced) ||
            (it.terminationTimestamp != null && it.terminationTimestamp.isBefore(reportDate))
    }

fun getReservedBoatSpaceReport(
    jdbi: Jdbi,
    reportDate: LocalDateTime
): List<BoatSpaceReportRow> =
    getBoatSpaceReport(jdbi, reportDate).filter {
        it.startDate != null &&
            (it.reservationStatus == ReservationStatus.Confirmed || it.reservationStatus == ReservationStatus.Invoiced)
    }

fun getTerminatedBoatSpaceReport(
    jdbi: Jdbi,
    reportDate: LocalDateTime
): List<BoatSpaceReportRow> = getBoatSpaceReport(jdbi, reportDate).filter { it.terminationTimestamp != null }

fun getWarningsBoatSpaceReport(
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
                        'key' AS key,
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
        getBoatSpaceReport(jdbi, reportDate, reservationsWithWarningsIds)
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

fun getBoatSpaceReport(
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
                        bsr.status AS reservation_status
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

fun boatSpaceReportToCsv(reportRows: List<BoatSpaceReportRow>): String {
    val csvHeader =
        listOf(
            "satama",
            "laituri",
            "paikka",
            "paikan leveys",
            "paikan pituus",
            "veneen leveys",
            "veneen pituus",
            "paikan varuste",
            "paikan tyyppi",
            "varaaja",
            "kotikunta",
            "veneen rekisterinumero",
            "hinta",
            "maksuluokka",
            "tyyppi",
            "irtisanomisaika",
            "irtisanomissyy",
            "alkupvm",
            "loppupvm",
            "maksupäivä"
        ).joinToString(CSV_FIELD_SEPARATOR, postfix = CSV_RECORD_SEPARATOR)

    val csvContent = StringBuilder()
    csvContent.append(csvHeader)

    for (report in reportRows) {
        csvContent
            .append(sanitizeCsvCellData(report.harbor))
            .append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(report.pier))
            .append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(report.place))
            .append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(intToDecimal(report.placeWidthCm)))
            .append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(intToDecimal(report.placeLengthCm)))
            .append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(intToDecimal(report.boatWidthCm)))
            .append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(intToDecimal(report.boatLengthCm)))
            .append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(amenityToText(report.amenity)))
            .append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(boatSpaceTypeToText(report.boatSpaceType)))
            .append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(report.name))
            .append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(report.municipality))
            .append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(report.registrationCode))
            .append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(intToDecimal(report.totalCents)))
            .append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(report.productCode))
            .append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(reservationCreationTypeToText(report.creationType)))
            .append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(localDateTimeToText(report.terminationTimestamp)))
            .append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(terminationReasonToText(report.terminationReason)))
            .append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(localDateToText(report.startDate)))
            .append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(localDateToText(report.endDate)))
            .append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(localDateTimeToText(report.paid)))
            .append(CSV_RECORD_SEPARATOR)
    }

    return csvContent.toString()
}

fun freeBoatSpaceReportToCsv(reportRows: List<BoatSpaceReportRow>): String {
    val csvHeader =
        listOf(
            "satama",
            "laituri",
            "paikka",
            "paikan leveys",
            "paikan pituus",
            "varuste",
            "tyyppi",
            "maksuluokka",
            "maksupäivä"
        ).joinToString(CSV_FIELD_SEPARATOR, postfix = CSV_RECORD_SEPARATOR)

    val csvContent = StringBuilder()
    csvContent.append(csvHeader)

    for (report in reportRows) {
        csvContent
            .append(sanitizeCsvCellData(report.harbor))
            .append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(report.pier))
            .append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(report.place))
            .append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(intToDecimal(report.placeWidthCm)))
            .append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(intToDecimal(report.placeLengthCm)))
            .append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(amenityToText(report.amenity)))
            .append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(boatSpaceTypeToText(report.boatSpaceType)))
            .append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(report.productCode))
            .append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(localDateTimeToText(report.paid)))
            .append(CSV_RECORD_SEPARATOR)
    }

    return csvContent.toString()
}

fun terminatedBoatSpaceReportToCsv(reportRows: List<BoatSpaceReportRow>): String {
    val csvHeader =
        listOf(
            "satama",
            "laituri",
            "paikka",
            "paikan leveys",
            "paikan pituus",
            "veneen leveys",
            "veneen pituus",
            "paikan varuste",
            "paikan tyyppi",
            "varaaja",
            "kotikunta",
            "veneen rekisterinumero",
            "hinta",
            "maksuluokka",
            "tyyppi",
            "irtisanomisaika",
            "irtisanomissyy",
            "alkupvm",
            "loppupvm",
            "maksupäivä"
        ).joinToString(CSV_FIELD_SEPARATOR, postfix = CSV_RECORD_SEPARATOR)

    val csvContent = StringBuilder()
    csvContent.append(csvHeader)

    for (report in reportRows) {
        csvContent
            .append(sanitizeCsvCellData(report.harbor))
            .append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(report.pier))
            .append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(report.place))
            .append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(intToDecimal(report.placeWidthCm)))
            .append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(intToDecimal(report.placeLengthCm)))
            .append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(intToDecimal(report.boatWidthCm)))
            .append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(intToDecimal(report.boatLengthCm)))
            .append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(amenityToText(report.amenity)))
            .append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(boatSpaceTypeToText(report.boatSpaceType)))
            .append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(report.name))
            .append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(report.municipality))
            .append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(report.registrationCode))
            .append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(intToDecimal(report.totalCents)))
            .append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(report.productCode))
            .append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(reservationCreationTypeToText(report.creationType)))
            .append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(localDateTimeToText(report.terminationTimestamp)))
            .append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(terminationReasonToText(report.terminationReason)))
            .append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(localDateToText(report.startDate)))
            .append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(localDateToText(report.endDate)))
            .append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(localDateTimeToText(report.paid)))
            .append(CSV_RECORD_SEPARATOR)
    }

    return csvContent.toString()
}

fun warningsBoatSpaceReportToCsv(reportRows: List<BoatSpaceReportRowWithWarnings>): String {
    val csvHeader =
        listOf(
            "satama",
            "laituri",
            "paikka",
            "paikan leveys",
            "paikan pituus",
            "veneen leveys",
            "veneen pituus",
            "paikan varuste",
            "paikan tyyppi",
            "varaaja",
            "kotikunta",
            "veneen rekisterinumero",
            "hinta",
            "maksuluokka",
            "tyyppi",
            "irtisanomisaika",
            "irtisanomissyy",
            "alkupvm",
            "loppupvm",
            "maksupäivä",
            "varoitukset"
        ).joinToString(CSV_FIELD_SEPARATOR, postfix = CSV_RECORD_SEPARATOR)

    val csvContent = StringBuilder()
    csvContent.append(csvHeader)

    for (reservationWithWarnings in reportRows) {
        val reservation = reservationWithWarnings.boatSpaceReportRow
        csvContent
            .append(sanitizeCsvCellData(reservation.harbor))
            .append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(reservation.pier))
            .append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(reservation.place))
            .append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(intToDecimal(reservation.placeWidthCm)))
            .append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(intToDecimal(reservation.placeLengthCm)))
            .append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(intToDecimal(reservation.boatWidthCm)))
            .append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(intToDecimal(reservation.boatLengthCm)))
            .append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(amenityToText(reservation.amenity)))
            .append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(boatSpaceTypeToText(reservation.boatSpaceType)))
            .append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(reservation.name))
            .append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(reservation.municipality))
            .append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(reservation.registrationCode))
            .append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(intToDecimal(reservation.totalCents)))
            .append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(reservation.productCode))
            .append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(reservationCreationTypeToText(reservation.creationType)))
            .append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(localDateTimeToText(reservation.terminationTimestamp)))
            .append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(terminationReasonToText(reservation.terminationReason)))
            .append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(localDateToText(reservation.startDate)))
            .append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(localDateToText(reservation.endDate)))
            .append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(localDateTimeToText(reservation.paid)))
            .append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(warningsToText(reservationWithWarnings.warnings)))
            .append(CSV_RECORD_SEPARATOR)
    }

    return csvContent.toString()
}

fun warningsToText(warnings: List<ReservationWarning>): String = warnings.joinToString(separator = ", ") { warningToText(it) }

fun warningToText(warning: ReservationWarning): String =
    "${reservationWarningTypeToText(
        ReservationWarningType.valueOf(warning.key)
    )}${if (!warning.infoText.isNullOrEmpty()) ": ${warning.infoText}" else ""}"

fun reservationWarningTypeToText(reservationWarningType: ReservationWarningType): String =
    when (reservationWarningType) {
        ReservationWarningType.GeneralReservationWarning -> "Yleinen"
        ReservationWarningType.BoatType -> "Venetyyppi"
        ReservationWarningType.InvoicePayment -> "Lasku"
        ReservationWarningType.TrailerWidth -> "Trailerin leveys"
        ReservationWarningType.TrailerLength -> "Trailerin pituus"
        ReservationWarningType.BoatCoOwner -> "Kanssaomistaja"
        ReservationWarningType.BoatLength -> "Veneen pituus"
        ReservationWarningType.BoatWidth -> "Veneen leveys"
        ReservationWarningType.BoatWeight -> "Veneen paino"
        ReservationWarningType.BoatFutureOwner -> "Tuleva omistaja"
    }

fun getReference(p: PaymentHistory): String? =
    if (p.paymentDetails.paymentType == PaymentType.Invoice) {
        p.paymentDetails.invoiceReference
    } else {
        p.paymentDetails.priceInfo
    }

val localDateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

fun localDateTimeToText(theDate: LocalDateTime?): String = theDate?.format(localDateTimeFormatter) ?: ""

fun localDateToText(theDate: LocalDate?): String = theDate?.toString() ?: ""

fun intToDecimal(nr: String?): String =
    nr?.let {
        val centsInt = it.toIntOrNull() ?: 0
        (centsInt / 100.0).toString().replace(".", ",")
    } ?: ""

const val CSV_FIELD_SEPARATOR = ";"
const val CSV_RECORD_SEPARATOR = "\r\n"

fun sanitizeCsvCellData(cellData: String?): String = CsvEscape.escapeCsv(escapeCsvInjection(cellData ?: ""))

/**
 * Escapes dangerous characters in strings to prevent CSV injection.
 * Prepends dangerous characters with a single quote.
 */
private fun escapeCsvInjection(value: String): String =
    if (value.startsWith("=") ||
        value.startsWith("+") ||
        value.startsWith("-") ||
        value.startsWith("@") ||
        value.startsWith("|") ||
        value.startsWith("\\")
    ) {
        "'$value"
    } else {
        value
    }
