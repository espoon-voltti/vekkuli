package fi.espoo.vekkuli

import fi.espoo.vekkuli.boatSpace.terminateReservation.ReservationTerminationReason
import fi.espoo.vekkuli.domain.BoatSpaceAmenity
import fi.espoo.vekkuli.domain.BoatSpaceType
import fi.espoo.vekkuli.domain.BoatType
import fi.espoo.vekkuli.domain.CreationType
import fi.espoo.vekkuli.domain.OwnershipStatus
import fi.espoo.vekkuli.domain.PaymentStatus
import fi.espoo.vekkuli.domain.PaymentType
import fi.espoo.vekkuli.domain.ReservationStatus
import fi.espoo.vekkuli.domain.ReservationValidity
import fi.espoo.vekkuli.domain.ReservationWarning
import fi.espoo.vekkuli.domain.ReservationWarningType
import fi.espoo.vekkuli.service.*
import org.jdbi.v3.core.kotlin.inTransactionUnchecked
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.LocalDate
import java.util.UUID
import kotlin.test.assertEquals

@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class ReportingIntegrationTest : IntegrationTestBase() {
    @Autowired
    lateinit var reservationService: BoatReservationService

    @BeforeEach
    override fun resetDatabase() {
        deleteAllReservations(jdbi)
        deleteAllBoats(jdbi)
        deleteAllBoatSpaces(jdbi)
    }

    @Test
    fun `sticker report`() {
        val boatSpaceId = 4242

        insertDevBoatSpace(
            DevBoatSpace(
                id = boatSpaceId,
                type = BoatSpaceType.Slip,
                locationId = 1,
                priceId = 1,
                section = "A",
                placeNumber = 1,
                amenity = BoatSpaceAmenity.Beam,
                widthCm = 100,
                lengthCm = 200,
                description = "Test boat space"
            )
        )

        val today = LocalDate.of(2024, 10, 1)

        val boatId = 123321
        insertDevBoat(
            DevBoat(
                id = boatId,
                registrationCode = "U12345",
                reserverId = citizenIdLeo,
                name = "Testi Venho",
                widthCm = 100,
                lengthCm = 200,
                depthCm = 50,
                weightKg = 1000,
                type = BoatType.OutboardMotor,
                otherIdentification = "123456",
                extraInformation = "Test boat",
                ownership = OwnershipStatus.Owner
            )
        )

        val trailer =
            insertDevTrailer(
                citizenIdLeo,
                "ABC-987",
                widthCm = 123,
                lengthCm = 456,
            )

        val resId = 3131

        insertDevBoatSpaceReservation(
            DevBoatSpaceReservation(
                id = resId,
                reserverId = citizenIdLeo,
                boatSpaceId = boatSpaceId,
                startDate = today,
                endDate = today.plusMonths(12),
                boatId = boatId,
                status = ReservationStatus.Confirmed,
                trailerId = trailer.id
            )
        )

        val terminatedBoatSpaceId = 4243

        insertDevBoatSpace(
            DevBoatSpace(
                id = terminatedBoatSpaceId,
                type = BoatSpaceType.Slip,
                locationId = 1,
                priceId = 1,
                section = "A",
                placeNumber = 2,
                amenity = BoatSpaceAmenity.Beam,
                widthCm = 100,
                lengthCm = 200,
                description = "Test boat space"
            )
        )

        val terminatedResId = 3132

        insertDevBoatSpaceReservation(
            DevBoatSpaceReservation(
                id = terminatedResId,
                reserverId = citizenIdLeo,
                boatSpaceId = terminatedBoatSpaceId,
                startDate = today,
                endDate = today.plusMonths(12),
                boatId = boatId,
                status = ReservationStatus.Cancelled,
                terminationReason = ReservationTerminationReason.RuleViolation,
            )
        )

        val payment =
            insertDevPayment(
                DevPayment(
                    reserverId = citizenIdLeo,
                    reservationId = resId,
                    paid = today.atStartOfDay(),
                )
            )

        val terminatedPayment =
            insertDevPayment(
                DevPayment(
                    reserverId = citizenIdLeo,
                    reservationId = terminatedResId,
                    paid = today.atStartOfDay(),
                )
            )

        // Update created date to today because it is automatically set to real time
        jdbi.inTransactionUnchecked { tx ->
            tx
                .createUpdate("UPDATE payment SET created = :created::date")
                .bind("created", today)
                .bind("id", resId)
                .execute()
        }

        val stickerReportRows = getStickerReportRows(jdbi, today, today)
        assertEquals(true, stickerReportRows.size > 0)
        assertEquals(today.atStartOfDay(), stickerReportRows[0].paid)
        val row = stickerReportRows.find { it.harbor == "Haukilahti" && it.place == "A 001" }
        assertEquals("Testi Venho", row?.boatName)
        assertEquals("leo@noreplytest.fi", row?.email)
        assertEquals("ABC-987", row?.trailerRegistrationCode)
        assertEquals("123", row?.trailerWidthCm)
        assertEquals("456", row?.trailerLengthCm)

        val terminatedRow = stickerReportRows.find { it.harbor == "Haukilahti" && it.place == "A 002" }
        assertEquals("leo@noreplytest.fi", terminatedRow?.email)
    }

    @Test
    fun `billing report lists payments paid within the date range sorted by place`() {
        val haukilahti = 1
        val suomenoja = 6
        val startDate = LocalDate.of(2025, 3, 1)
        val endDate = LocalDate.of(2025, 3, 31)
        val inRange = startDate.plusDays(10).atTime(12, 0)

        fun insertPaidReservation(
            id: Int,
            type: BoatSpaceType,
            locationId: Int,
            section: String,
            placeNumber: Int,
            creationType: CreationType = CreationType.New,
            validity: ReservationValidity = ReservationValidity.Indefinite,
            reservationYear: Int = 2025,
            payment: DevPayment = DevPayment(reserverId = citizenIdLeo, reservationId = id, paid = inRange)
        ) {
            insertDevBoatSpace(
                DevBoatSpace(
                    id = id,
                    type = type,
                    locationId = locationId,
                    priceId = 1,
                    section = section,
                    placeNumber = placeNumber,
                    amenity = BoatSpaceAmenity.None,
                    widthCm = 250,
                    lengthCm = 700,
                    description = "Billing report test space"
                )
            )
            insertDevBoatSpaceReservation(
                DevBoatSpaceReservation(
                    id = id,
                    reserverId = payment.reserverId,
                    boatSpaceId = id,
                    startDate = LocalDate.of(reservationYear, 1, 1),
                    endDate = LocalDate.of(reservationYear, 12, 31),
                    status = ReservationStatus.Confirmed,
                    creationType = creationType,
                    validity = validity
                )
            )
            insertDevPayment(payment.copy(reservationId = id))
        }

        insertPaidReservation(5001, BoatSpaceType.Winter, haukilahti, "B", 2)
        insertPaidReservation(
            5002,
            BoatSpaceType.Slip,
            suomenoja,
            "A",
            3,
            creationType = CreationType.Renewal,
            validity = ReservationValidity.FixedTerm,
            payment =
                DevPayment(
                    reserverId = citizenIdOlivia,
                    reservationId = 5002,
                    paymentType = PaymentType.Invoice,
                    totalCents = 25050,
                    productCode = "ML2",
                    paid = endDate.atTime(23, 30)
                )
        )
        insertPaidReservation(5003, BoatSpaceType.Slip, haukilahti, "A", 10)
        insertPaidReservation(5004, BoatSpaceType.Slip, haukilahti, "A", 2)
        // Same place as 5004 a year earlier, renewed: sorts before 5004 by reservation type
        insertPaidReservation(5005, BoatSpaceType.Slip, haukilahti, "C", 2, creationType = CreationType.Renewal)
        jdbi.inTransactionUnchecked { tx ->
            tx.execute(
                "UPDATE boat_space_reservation SET boat_space_id = 5004, start_date = '2024-01-01', end_date = '2024-12-31' WHERE id = 5005"
            )
            tx.execute(
                """
                INSERT INTO invoice (due_date, reference, reservation_id, payment_id, reserver_id)
                SELECT '2025-04-15', 'INVOICE-REF-1', reservation_id, id, reserver_id FROM payment WHERE reservation_id = 5002
                """.trimIndent()
            )
        }

        // Not in the report: paid outside the range, refunded, or not paid at all
        insertPaidReservation(
            5101,
            BoatSpaceType.Slip,
            haukilahti,
            "D",
            1,
            payment = DevPayment(reserverId = citizenIdLeo, reservationId = 5101, paid = startDate.minusDays(1).atTime(23, 59))
        )
        insertPaidReservation(
            5102,
            BoatSpaceType.Slip,
            haukilahti,
            "D",
            2,
            payment = DevPayment(reserverId = citizenIdLeo, reservationId = 5102, paid = endDate.plusDays(1).atStartOfDay())
        )
        insertPaidReservation(
            5103,
            BoatSpaceType.Slip,
            haukilahti,
            "D",
            3,
            payment = DevPayment(reserverId = citizenIdLeo, reservationId = 5103, status = PaymentStatus.Refunded, paid = inRange)
        )
        insertPaidReservation(
            5104,
            BoatSpaceType.Slip,
            haukilahti,
            "D",
            4,
            payment =
                DevPayment(
                    reserverId = citizenIdLeo,
                    reservationId = 5104,
                    paymentType = PaymentType.Invoice,
                    status = PaymentStatus.Created,
                    paid = null
                )
        )

        val csvRows = billingReportToCsv(getBillingReportRows(jdbi, startDate, endDate)).trimEnd().split("\r\n")

        assertEquals(
            "varaaja;varauksen tyyppi;varauksen kesto;paikan tyyppi;satama;laituri;paikka;maksupäivä;summa;maksutapa;hintaluokka;viite",
            csvRows[0]
        )
        assertEquals(
            listOf(
                "Laituripaikka;Haukilahti;A;2;Jatko",
                "Laituripaikka;Haukilahti;A;2;Uusi",
                "Laituripaikka;Haukilahti;A;10;Uusi",
                "Laituripaikka;Suomenoja;A;3;Jatko",
                "Talvipaikka;Haukilahti;B;2;Uusi"
            ),
            csvRows.drop(1).map {
                val c = it.split(";")
                listOf(c[3], c[4], c[5], c[6], c[1]).joinToString(";")
            }
        )
        assertEquals(
            "\"Virtanen Olivia\";Jatko;\"Määräaikainen\";Laituripaikka;Suomenoja;A;3;\"2025-03-31 23:30:00\";\"250,5\";Lasku;ML2;\"INVOICE-REF-1\"",
            csvRows[4]
        )
    }

    @Test
    fun `boat place report (all)`() {
        val boatSpaceId = 4242

        insertDevBoatSpace(
            DevBoatSpace(
                id = boatSpaceId,
                type = BoatSpaceType.Slip,
                locationId = 1,
                priceId = 1,
                section = "A",
                placeNumber = 1,
                amenity = BoatSpaceAmenity.Beam,
                widthCm = 100,
                lengthCm = 200,
                description = "Test boat space"
            )
        )

        val today = LocalDate.of(2024, 10, 1)

        val boatId = 123321
        insertDevBoat(
            DevBoat(
                id = boatId,
                registrationCode = "U12345",
                reserverId = citizenIdLeo,
                name = "Testi Venho",
                widthCm = 100,
                lengthCm = 200,
                depthCm = 50,
                weightKg = 1000,
                type = BoatType.OutboardMotor,
                otherIdentification = "123456",
                extraInformation = "Test boat",
                ownership = OwnershipStatus.Owner
            )
        )

        val resId = 3131

        insertDevBoatSpaceReservation(
            DevBoatSpaceReservation(
                id = resId,
                reserverId = citizenIdLeo,
                boatSpaceId = boatSpaceId,
                startDate = today,
                endDate = today.plusMonths(12),
                boatId = boatId,
                creationType = CreationType.Renewal
            )
        )

        val reportRows = getBoatSpaceReportRows(jdbi, today.atStartOfDay())
        assertEquals(true, reportRows.size > 0)
        val row = reportRows.find { (it.harbor == "Haukilahti" && it.place == "A 001") }
        assertEquals("Korhonen Leo", row?.name)
        assertEquals("Renewal", row?.creationType.toString())
        assertEquals("leo@noreplytest.fi", row?.email)
    }

    @Test
    fun `boat place report (free, reserved and terminated)`() {
        val freeBoatSpaceId = 4242
        val reservedBoatSpaceId = 4243
        val terminatedBoatSpaceId = 4244

        insertDevBoatSpace(
            DevBoatSpace(
                id = freeBoatSpaceId,
                type = BoatSpaceType.Slip,
                locationId = 1,
                priceId = 1,
                section = "A",
                placeNumber = 1,
                amenity = BoatSpaceAmenity.Beam,
                widthCm = 100,
                lengthCm = 200,
                description = "Test free boat space"
            )
        )

        insertDevBoatSpace(
            DevBoatSpace(
                id = reservedBoatSpaceId,
                type = BoatSpaceType.Slip,
                locationId = 1,
                priceId = 1,
                section = "A",
                placeNumber = 2,
                amenity = BoatSpaceAmenity.Buoy,
                widthCm = 100,
                lengthCm = 200,
                description = "Test reserved boat space"
            )
        )

        insertDevBoatSpace(
            DevBoatSpace(
                id = terminatedBoatSpaceId,
                type = BoatSpaceType.Slip,
                locationId = 1,
                priceId = 1,
                section = "A",
                placeNumber = 3,
                amenity = BoatSpaceAmenity.Buoy,
                widthCm = 200,
                lengthCm = 300,
                description = "Test terminated boat space"
            )
        )

        val today = LocalDate.of(2024, 10, 1)

        val boatId = 123321
        insertDevBoat(
            DevBoat(
                id = boatId,
                registrationCode = "U12345",
                reserverId = citizenIdLeo,
                name = "Testi Venho",
                widthCm = 100,
                lengthCm = 200,
                depthCm = 50,
                weightKg = 1000,
                type = BoatType.OutboardMotor,
                otherIdentification = "123456",
                extraInformation = "Test boat",
                ownership = OwnershipStatus.Owner
            )
        )

        val trailer =
            insertDevTrailer(
                citizenIdLeo,
                "ABC-987",
                widthCm = 123,
                lengthCm = 456,
            )

        val resId = 3131

        insertDevBoatSpaceReservation(
            DevBoatSpaceReservation(
                id = resId,
                reserverId = citizenIdLeo,
                boatSpaceId = reservedBoatSpaceId,
                startDate = today,
                endDate = today.plusMonths(12),
                boatId = boatId,
                trailerId = trailer.id,
                status = ReservationStatus.Confirmed
            )
        )

        insertDevPayment(
            DevPayment(
                reserverId = citizenIdLeo,
                reservationId = resId,
                paid = today.atStartOfDay()
            )
        )

        val terminatedId = 3232

        insertDevBoatSpaceReservation(
            DevBoatSpaceReservation(
                id = terminatedId,
                reserverId = citizenIdLeo,
                boatSpaceId = terminatedBoatSpaceId,
                startDate = today,
                endDate = today.plusMonths(12),
                boatId = boatId,
                status = ReservationStatus.Cancelled,
                terminationReason = ReservationTerminationReason.RuleViolation,
                terminationTimestamp = today.plusMonths(1).atStartOfDay()
            )
        )

        insertDevReservationWarning(
            ReservationWarning(
                id = UUID.randomUUID(),
                reservationId = terminatedId,
                boatId = boatId,
                trailerId = null,
                invoiceNumber = null,
                key = ReservationWarningType.BoatWidth,
                infoText = "Boat width is too big"
            )
        )

        insertDevReservationWarning(
            ReservationWarning(
                id = UUID.randomUUID(),
                reservationId = terminatedId,
                boatId = null,
                trailerId = null,
                invoiceNumber = null,
                key = ReservationWarningType.GeneralReservationWarning,
                infoText = "Boat allowed to be in the harbor for the winter"
            )
        )

        val freeRows = getFreeBoatSpaceReportRows(jdbi, today.atStartOfDay())
        assertTrue(freeRows.any { it.harbor == "Haukilahti" && it.place == "A 001" })
        assertTrue(freeRows.none { it.harbor == "Haukilahti" && it.place == "A 002" })

        val reservedRows = getReservedBoatSpaceReport(jdbi, today.atStartOfDay())
        assertTrue(reservedRows.none { it.harbor == "Haukilahti" && it.place == "A 001" })
        assertTrue(reservedRows.any { it.harbor == "Haukilahti" && it.place == "A 002" && it.trailerRegistrationCode == "ABC-987" })

        val terminatedRows = getTerminatedBoatSpaceReport(jdbi, today.atStartOfDay())
        assertEquals(1, terminatedRows.size)
        assertEquals("A 003", terminatedRows[0].place)
        assertEquals("RuleViolation", terminatedRows[0].terminationReason.toString())
        assertEquals(today.plusMonths(1).atStartOfDay(), terminatedRows[0].terminationTimestamp)
        assertEquals("leo@noreplytest.fi", terminatedRows[0].email)

        val reservationWarnings = getWarningsBoatSpaceReportRows(jdbi, today.atStartOfDay())
        assertEquals(1, reservationWarnings.size)
        assertEquals(2, reservationWarnings[0].warnings.size)
        assertEquals(terminatedId, reservationWarnings[0].boatSpaceReportRow.reservationId)
        assertEquals("leo@noreplytest.fi", reservationWarnings[0].boatSpaceReportRow.email)
    }
}
