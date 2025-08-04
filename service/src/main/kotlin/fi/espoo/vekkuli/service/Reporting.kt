package fi.espoo.vekkuli.service

import fi.espoo.vekkuli.domain.PaymentHistory
import fi.espoo.vekkuli.domain.PaymentType
import fi.espoo.vekkuli.domain.ReservationStatus
import fi.espoo.vekkuli.domain.ReservationWarning
import fi.espoo.vekkuli.domain.ReservationWarningType
import fi.espoo.vekkuli.utils.amenityToText
import fi.espoo.vekkuli.utils.boatSpaceTypeToText
import fi.espoo.vekkuli.utils.boatTypeToText
import fi.espoo.vekkuli.utils.ownershipStatusToText
import fi.espoo.vekkuli.utils.placeTypeToText
import fi.espoo.vekkuli.utils.reservationCreationTypeToText
import fi.espoo.vekkuli.utils.terminationReasonToText
import org.jdbi.v3.core.Jdbi
import org.unbescape.csv.CsvEscape
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.collections.List

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
            "varauksen loppupvm",
            "sähköposti",
            "puhno",
            "vene"
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
            .append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(report.email ?: ""))
            .append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(report.phone ?: ""))
            .append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(report.boatInfo ?: ""))
            .append(CSV_RECORD_SEPARATOR)
    }

    return csvContent.toString()
}

fun getReservedBoatSpaceReport(
    jdbi: Jdbi,
    reportDate: LocalDateTime
): List<BoatSpaceReportRow> =
    getBoatSpaceReportRows(jdbi, reportDate).filter {
        it.startDate != null &&
            (it.reservationStatus == ReservationStatus.Confirmed || it.reservationStatus == ReservationStatus.Invoiced)
    }

fun getTerminatedBoatSpaceReport(
    jdbi: Jdbi,
    reportDate: LocalDateTime
): List<BoatSpaceReportRow> = getBoatSpaceReportRows(jdbi, reportDate).filter { it.terminationTimestamp != null }

fun getAllBoatSpacesReport(
    jdbi: Jdbi,
    reportDate: LocalDateTime
): List<BoatSpaceReportRow> {
    val unorderedRows = (
        getFreeBoatSpaceReportRows(jdbi, reportDate) +
            getReservedBoatSpaceReport(jdbi, reportDate) +
            getTerminatedBoatSpaceReport(jdbi, reportDate)
    )
    val orderedRows = unorderedRows.sortedBy { it.harbor + it.pier + it.place }
    return orderedRows
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
            "maksupäivä",
            "sähköposti",
            "puhno",
            "vene"
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
            .append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(report.email ?: ""))
            .append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(report.phone ?: ""))
            .append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(report.boatInfo ?: ""))
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
            "maksuluokka"
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
            "maksupäivä",
            "sähköposti",
            "puhno",
            "vene"
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
            .append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(report.email ?: ""))
            .append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(report.phone ?: ""))
            .append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(report.boatInfo ?: ""))
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
            "varoitukset",
            "sähköposti",
            "puhno",
            "vene"
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
            .append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(reservation.email ?: ""))
            .append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(reservation.phone ?: ""))
            .append(CSV_FIELD_SEPARATOR)
            .append(sanitizeCsvCellData(reservation.boatInfo ?: ""))
            .append(CSV_RECORD_SEPARATOR)
    }

    return csvContent.toString()
}

fun warningsToText(warnings: List<ReservationWarning>): String = warnings.joinToString(separator = ", ") { warningToText(it) }

fun warningToText(warning: ReservationWarning): String =
    "${reservationWarningTypeToText(
        warning.key
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
        ReservationWarningType.BoatOwnershipChange -> "Tuleva omistaja"
        ReservationWarningType.BoatRegistrationCodeChange -> "Rekisterinumeron muutos"
        ReservationWarningType.RegistrationCodeNotUnique -> "Rekisteritunnus on jo käytössä"
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
