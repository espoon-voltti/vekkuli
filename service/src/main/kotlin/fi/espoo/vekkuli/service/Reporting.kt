package fi.espoo.vekkuli.service

import fi.espoo.vekkuli.domain.OwnershipStatus
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.inTransactionUnchecked
import org.jdbi.v3.core.kotlin.mapTo
import org.unbescape.csv.CsvEscape
import java.time.LocalDate

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
        csvContent
            .append(sanitizeCsvCellData(report.boatSpaceId.toString())).append(CSV_FIELD_SEPARATOR)
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
    val widthCm: String?,
    val lengthCm: String?,
    val weightKg: String?,
    val registrationCode: String?,
    val otherIdentification: String?,
    val ownership: OwnershipStatus?,
    val startDate: String?,
    val endDate: String?,
)

fun getStickerReport(jdbi: Jdbi, today: LocalDate): List<StickerReportRow> {
    return jdbi.inTransactionUnchecked { tx ->
        tx.createQuery(
            """
            SELECT
                r.name, r.street_address, r.postal_code, r.post_office,
                l.name AS harbor, CONCAT(bs.section, ' ', TO_CHAR(bs.place_number, 'FM000')) as place,
                bs.type AS place_type, bs.amenity, b.name AS boat_name, b.type AS boat_type,
                b.width_cm, b.length_cm, b.weight_kg, b.registration_code, b.other_identification,
                b.ownership, bsr.start_date, bsr.end_date
            FROM boat_space_reservation bsr
                JOIN reserver r ON r.id = bsr.reserver_id
                JOIN boat_space bs ON bs.id = bsr.boat_space_id
                JOIN location l ON l.id = bs.location_id
                LEFT JOIN boat b ON b.id = bsr.boat_id
            WHERE
                daterange(bsr.start_date, bsr.end_date) @> :today
            """.trimIndent()
        )
            .bind("today", today)
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
            "paikan varuste",
            "veneen nimi",
            "veneen tyyppi",
            "veneen leveys",
            "veneen pituus",
            "veneen paino",
            "veneen rekisterinumero",
            "muu tunniste",
            "omistussuhde"
        ).joinToString(CSV_FIELD_SEPARATOR, postfix = CSV_RECORD_SEPARATOR)

    val csvContent = StringBuilder()
    csvContent.append(csvHeader)

    for (report in reportRows) {

        csvContent
            .append(sanitizeCsvCellData(report.name.toString())).append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(report.streetAddress)).append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(report.postalCode)).append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(report.postOffice)).append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(report.harbor)).append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(report.place)).append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(placeTypeToText(report.placeType))).append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(amenityToText(report.amenity))).append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(report.boatName ?: "")).append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(boatTypeToText(report.boatType))).append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(report.widthCm.toString())).append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(report.lengthCm.toString())).append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(report.weightKg.toString())).append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(report.registrationCode)).append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(report.otherIdentification.toString())).append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(ownershipStatusToText(report.ownership.toString()))).append(CSV_FIELD_SEPARATOR)
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


