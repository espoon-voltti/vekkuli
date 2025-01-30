package fi.espoo.vekkuli.service

import fi.espoo.vekkuli.domain.PaymentHistory
import fi.espoo.vekkuli.domain.PaymentType
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.inTransactionUnchecked
import org.jdbi.v3.core.kotlin.mapTo
import org.unbescape.csv.CsvEscape
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class StickerReportRow(
    val name: String?,
    val streetAddress: String?,
    val postalCode: String?,
    val postOffice: String?,
    val harbor: String?,
    val place: String?,
    val placeType: String?,
    val amenity: String?,
    val boatName: String?,
    val boatType: String?,
    val placeWidthCm: String?,
    val placeLengthCm: String?,
    val boatWidthCm: String?,
    val boatLengthCm: String?,
    val boatWeightKg: String?,
    val registrationCode: String?,
    val otherIdentification: String?,
    val ownership: String?,
    val startDate: String?,
    val endDate: String?,
    val productCode: String?,
    val totalCents: String?,
    val paid: LocalDateTime?,
)

fun getStickerReport(
    jdbi: Jdbi,
    reportDate: LocalDateTime
): List<StickerReportRow> {
    return jdbi.inTransactionUnchecked { tx ->
        tx.createQuery(
            """
            SELECT
                r.name, r.street_address, r.postal_code, r.post_office,
                l.name AS harbor, CONCAT(bs.section, ' ', TO_CHAR(bs.place_number, 'FM000')) as place,
                bs.type AS place_type, bs.amenity, b.name AS boat_name, b.type AS boat_type,
                bs.width_cm AS place_width_cm, bs.length_cm AS place_length_cm, b.width_cm AS boat_width_cm, b.length_cm AS boat_length_cm, b.weight_kg AS boat_weight_kg, b.registration_code, b.other_identification,
                b.ownership, bsr.start_date, bsr.end_date,
                price.name AS product_code,
                p.total_cents,
                p.paid
            FROM boat_space_reservation bsr
                JOIN reserver r ON r.id = bsr.reserver_id
                JOIN boat_space bs ON bs.id = bsr.boat_space_id
                JOIN location l ON l.id = bs.location_id
                JOIN payment p ON p.reservation_id = bsr.id
                LEFT JOIN boat b ON b.id = bsr.boat_id
                LEFT JOIN price ON price.id = bs.price_id
            WHERE 
                bsr.reserver_id IS NOT NULL
                AND :reportDate::date >= bsr.start_date 
                AND :reportDate::date <= bsr.end_date
                AND bsr.status = 'Confirmed'
            """.trimIndent()
        )
            .bind("reportDate", reportDate)
            .mapTo<StickerReportRow>()
            .list()
    }
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
            "maksupäivä",
            "hinta"
        ).joinToString(CSV_FIELD_SEPARATOR, postfix = CSV_RECORD_SEPARATOR)

    val csvContent = StringBuilder()
    csvContent.append(csvHeader)

    for (report in reportRows) {
        csvContent
            .append(sanitizeCsvCellData(report.name)).append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(report.streetAddress)).append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(report.postalCode)).append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(report.postOffice)).append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(report.harbor)).append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(report.place)).append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(placeTypeToText(report.placeType))).append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(intToDecimal(report.placeWidthCm))).append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(intToDecimal(report.placeLengthCm))).append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(amenityToText(report.amenity))).append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(report.boatName)).append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(boatTypeToText(report.boatType))).append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(intToDecimal(report.boatWidthCm))).append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(intToDecimal(report.boatLengthCm))).append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(report.boatWeightKg)).append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(report.registrationCode)).append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(report.otherIdentification)).append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(ownershipStatusToText(report.ownership))).append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(report.productCode)).append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(localDateTimeToText(report.paid))).append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(intToDecimal(report.totalCents))).append(CSV_FIELD_SEPARATOR)
            .append(CSV_RECORD_SEPARATOR)
    }

    return csvContent.toString()
}

data class BoatSpaceReportRow(
    val harbor: String?,
    val pier: String?,
    val place: String?,
    val placeWidthCm: String?,
    val placeLengthCm: String?,
    val boatWidthCm: String?,
    val boatLengthCm: String?,
    val amenity: String?,
    val name: String?,
    val municipality: String?,
    val registrationCode: String?,
    val totalCents: String?,
    val productCode: String?,
    val terminationTimestamp: LocalDateTime?,
    val terminationReason: String?,
    val startDate: LocalDate?,
    val endDate: LocalDate?,
    val paid: LocalDateTime?
)

fun getFreeBoatSpaceReport(
    jdbi: Jdbi,
    reportDate: LocalDateTime
): List<BoatSpaceReportRow> = getBoatSpaceReport(jdbi, reportDate).filter { it.startDate == null }

fun getReservedBoatSpaceReport(
    jdbi: Jdbi,
    reportDate: LocalDateTime
): List<BoatSpaceReportRow> = getBoatSpaceReport(jdbi, reportDate).filter { it.startDate != null }

fun getTerminatedBoatSpaceReport(
    jdbi: Jdbi,
    reportDate: LocalDateTime
): List<BoatSpaceReportRow> = getBoatSpaceReport(jdbi, reportDate).filter { it.terminationTimestamp != null }

fun getBoatSpaceReport(
    jdbi: Jdbi,
    reportDate: LocalDateTime
): List<BoatSpaceReportRow> {
    return jdbi.inTransactionUnchecked { tx ->
        tx.createQuery(
            """
            SELECT
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
                p.paid
            FROM boat_space bs
                 LEFT JOIN location l ON l.id = bs.location_id
                 LEFT JOIN boat_space_reservation bsr ON bsr.boat_space_id = bs.id
                 LEFT JOIN reserver r ON r.id = bsr.reserver_id
                 LEFT JOIN payment p ON p.reservation_id = bsr.id
                 LEFT JOIN boat b ON b.id = bsr.boat_id
                 LEFT JOIN municipality m ON m.code = r.municipality_code
                 LEFT JOIN price ON price.id = bs.price_id
            WHERE
                bsr.start_date is NULL OR
                (:reportDate::date >= bsr.start_date
                AND :reportDate::date <= bsr.end_date) 
            ORDER BY harbor, pier, place
            """.trimIndent()
        )
            .bind("reportDate", reportDate)
            .mapTo<BoatSpaceReportRow>()
            .list()
    }
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
            "varaaja",
            "kotikunta",
            "veneen rekisterinumero",
            "hinta",
            "maksuluokka",
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
            .append(sanitizeCsvCellData(report.harbor)).append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(report.pier)).append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(report.place)).append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(intToDecimal(report.placeWidthCm))).append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(intToDecimal(report.placeLengthCm))).append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(intToDecimal(report.boatWidthCm))).append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(intToDecimal(report.boatLengthCm))).append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(amenityToText(report.amenity))).append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(report.name)).append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(report.municipality)).append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(report.registrationCode)).append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(intToDecimal(report.totalCents))).append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(report.productCode)).append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(localDateTimeToText(report.terminationTimestamp))).append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(terminationReasonToText(report.terminationReason))).append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(localDateToText(report.startDate))).append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(localDateToText(report.endDate))).append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(localDateTimeToText(report.paid))).append(CSV_FIELD_SEPARATOR)
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
            "maksuluokka",
            "maksupäivä"
        ).joinToString(CSV_FIELD_SEPARATOR, postfix = CSV_RECORD_SEPARATOR)

    val csvContent = StringBuilder()
    csvContent.append(csvHeader)

    for (report in reportRows) {
        csvContent
            .append(sanitizeCsvCellData(report.harbor)).append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(report.pier)).append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(report.place)).append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(intToDecimal(report.placeWidthCm))).append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(intToDecimal(report.placeLengthCm))).append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(amenityToText(report.amenity))).append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(report.productCode)).append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(localDateTimeToText(report.paid))).append(CSV_FIELD_SEPARATOR)
            .append(CSV_RECORD_SEPARATOR)
    }

    return csvContent.toString()
}

fun reservedBoatSpaceReportToCsv(reportRows: List<BoatSpaceReportRow>): String {
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
            "varaaja",
            "kotikunta",
            "veneen rekisterinumero",
            "hinta",
            "maksuluokka",
            "maksupäivä",
            "varauksen alkupäivämäärä"
        ).joinToString(CSV_FIELD_SEPARATOR, postfix = CSV_RECORD_SEPARATOR)

    val csvContent = StringBuilder()
    csvContent.append(csvHeader)

    for (report in reportRows) {
        csvContent
            .append(sanitizeCsvCellData(report.harbor)).append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(report.pier)).append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(report.place)).append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(intToDecimal(report.placeWidthCm))).append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(intToDecimal(report.placeLengthCm))).append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(intToDecimal(report.boatWidthCm))).append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(intToDecimal(report.boatLengthCm))).append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(amenityToText(report.amenity))).append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(report.name)).append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(report.municipality)).append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(report.registrationCode)).append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(intToDecimal(report.totalCents))).append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(report.productCode)).append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(localDateTimeToText(report.paid))).append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(localDateToText(report.startDate))).append(CSV_FIELD_SEPARATOR)
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
            "paikan varuste",
            "varaaja",
            "kotikunta",
            "irtisanomisaika",
            "Irtisanomisen syy"
        ).joinToString(CSV_FIELD_SEPARATOR, postfix = CSV_RECORD_SEPARATOR)

    val csvContent = StringBuilder()
    csvContent.append(csvHeader)

    for (report in reportRows) {
        csvContent
            .append(sanitizeCsvCellData(report.harbor)).append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(report.pier)).append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(report.place)).append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(intToDecimal(report.placeWidthCm))).append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(intToDecimal(report.placeLengthCm))).append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(amenityToText(report.amenity))).append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(report.name)).append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(report.municipality)).append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(localDateTimeToText(report.terminationTimestamp))).append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(terminationReasonToText(report.terminationReason))).append(CSV_FIELD_SEPARATOR)
            .append(CSV_RECORD_SEPARATOR)
    }

    return csvContent.toString()
}

fun placeTypeToText(placeType: String?): String {
    return when (placeType) {
        "Storage" -> "Säilytys"
        "Slip" -> "Laituri"
        "Trailer" -> "Traileri"
        "Winter" -> "Talvi"
        else -> placeType ?: ""
    }
}

fun amenityToText(amenity: String?): String {
    return when (amenity) {
        "None" -> ""
        "RearBuoy" -> "Peräpoiju"
        "Beam" -> "Aisa"
        "WalkBeam" -> "Kävelyaisa"
        "Trailer" -> "Traileri"
        "Buck" -> "Pukit"
        else -> amenity ?: ""
    }
}

fun boatTypeToText(boatType: String?): String {
    return when (boatType) {
        "RowBoat" -> "Soutuvene"
        "OutboardMotor" -> "Perämoottori"
        "InboardMotor" -> "Sisämoottori"
        "Sailboat" -> "Purjevene"
        "JetSki" -> "Vesijetti"
        "Other" -> "Muu"
        else -> boatType ?: ""
    }
}

fun ownershipStatusToText(ownershipStatus: String?): String {
    return when (ownershipStatus) {
        "Owner" -> "Omistaja"
        "User" -> "Haltija"
        "CoOwner" -> "Kanssaomistaja"
        "FutureOwner" -> "Tuleva omistaja"
        else -> ownershipStatus ?: ""
    }
}

fun terminationReasonToText(terminationReason: String?): String {
    return when (terminationReason) {
        "UserRequest" -> "Toive"
        "InvalidOwner" -> "Väärä omistaja"
        "RuleViolation" -> "Sääntörikkomus"
        "PaymentViolation" -> "Maksurikkomus"
        "Other" -> "Muu"
        else -> terminationReason ?: ""
    }
}

fun boatSpaceTypeToText(boatSpaceType: String?): String {
    return when (boatSpaceType) {
        "Slip" -> "Laituri"
        "Storage" -> "Säilytys"
        "Trailer" -> "Traileri"
        "Winter" -> "Talvi"
        else -> boatSpaceType ?: ""
    }
}

fun paymentStatusToText(paymentStatus: String?): String {
    return when (paymentStatus) {
        "Created" -> "Luotu"
        "Success" -> "Maksettu"
        "Failed" -> "Keskeytynyt"
        "Refunded" -> "Hyvitetty"
        else -> paymentStatus ?: ""
    }
}

fun paymentTypeToText(paymentType: String?): String {
    return when (paymentType) {
        "OnlinePayment" -> "Verkkomaksu"
        "Invoice" -> "Lasku"
        "Other" -> "Muu"
        else -> paymentType ?: ""
    }
}

fun getReference(p: PaymentHistory): String? =
    if (p.paymentType == PaymentType.Invoice) {
        p.paymentReference
    } else {
        p.priceInfo
    }

val localDateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

fun localDateTimeToText(theDate: LocalDateTime?): String {
    return theDate?.format(localDateTimeFormatter) ?: ""
}

fun localDateToText(theDate: LocalDate?): String {
    return theDate?.toString() ?: ""
}

fun intToDecimal(nr: String?): String {
    return nr?.let {
        val centsInt = it.toIntOrNull() ?: 0
        (centsInt / 100.0).toString().replace(".", ",")
    } ?: ""
}

const val CSV_FIELD_SEPARATOR = ";"
const val CSV_RECORD_SEPARATOR = "\r\n"

fun sanitizeCsvCellData(cellData: String?): String {
    return CsvEscape.escapeCsv(escapeCsvInjection(cellData ?: ""))
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
