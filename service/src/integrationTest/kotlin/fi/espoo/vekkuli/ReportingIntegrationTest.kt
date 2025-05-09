package fi.espoo.vekkuli

import fi.espoo.vekkuli.boatSpace.terminateReservation.ReservationTerminationReason
import fi.espoo.vekkuli.domain.BoatSpaceAmenity
import fi.espoo.vekkuli.domain.BoatSpaceType
import fi.espoo.vekkuli.domain.BoatType
import fi.espoo.vekkuli.domain.CreationType
import fi.espoo.vekkuli.domain.OwnershipStatus
import fi.espoo.vekkuli.domain.ReservationStatus
import fi.espoo.vekkuli.domain.ReservationWarning
import fi.espoo.vekkuli.domain.ReservationWarningType
import fi.espoo.vekkuli.service.*
import org.jdbi.v3.core.kotlin.inTransactionUnchecked
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.LocalDate
import java.util.UUID
import kotlin.test.assertEquals

@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
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

        val resId = 3131

        insertDevBoatSpaceReservation(
            DevBoatSpaceReservation(
                id = resId,
                reserverId = citizenIdLeo,
                boatSpaceId = boatSpaceId,
                startDate = today,
                endDate = today.plusMonths(12),
                boatId = boatId,
                status = ReservationStatus.Confirmed
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

        // Update created date to today because it is automatically set to real time
        jdbi.inTransactionUnchecked { tx ->
            tx
                .createUpdate("UPDATE payment SET created = :created::date")
                .bind("created", today)
                .bind("id", resId)
                .execute()
        }

        val stickerReportRows = getStickerReport(jdbi, today)
        assertEquals(true, stickerReportRows.size > 0)
        assertEquals(today.atStartOfDay(), stickerReportRows[0].paid)
        val row = stickerReportRows.find { it.harbor == "Haukilahti" && it.place == "A 001" }
        assertEquals("Testi Venho", row?.boatName)
        assertEquals("leo@noreplytest.fi", row?.email)
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

        val reportRows = getBoatSpaceReport(jdbi, today.atStartOfDay())
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

        val resId = 3131

        insertDevBoatSpaceReservation(
            DevBoatSpaceReservation(
                id = resId,
                reserverId = citizenIdLeo,
                boatSpaceId = reservedBoatSpaceId,
                startDate = today,
                endDate = today.plusMonths(12),
                boatId = boatId,
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

        val freeRows = getFreeBoatSpaceReport(jdbi, today.atStartOfDay())
        assertTrue(freeRows.any { it.harbor == "Haukilahti" && it.place == "A 001" })
        assertTrue(freeRows.none { it.harbor == "Haukilahti" && it.place == "A 002" })

        val reservedRows = getReservedBoatSpaceReport(jdbi, today.atStartOfDay())
        assertTrue(reservedRows.none { it.harbor == "Haukilahti" && it.place == "A 001" })
        assertTrue(reservedRows.any { it.harbor == "Haukilahti" && it.place == "A 002" })

        val terminatedRows = getTerminatedBoatSpaceReport(jdbi, today.atStartOfDay())
        assertEquals(1, terminatedRows.size)
        assertEquals("A 003", terminatedRows[0].place)
        assertEquals("RuleViolation", terminatedRows[0].terminationReason.toString())
        assertEquals(today.plusMonths(1).atStartOfDay(), terminatedRows[0].terminationTimestamp)
        assertEquals("leo@noreplytest.fi", terminatedRows[0].email)

        val reservationWarnings = getWarningsBoatSpaceReport(jdbi, today.atStartOfDay())
        assertEquals(1, reservationWarnings.size)
        assertEquals(2, reservationWarnings[0].warnings.size)
        assertEquals(terminatedId, reservationWarnings[0].boatSpaceReportRow.reservationId)
        assertEquals("leo@noreplytest.fi", reservationWarnings[0].boatSpaceReportRow.email)
    }
}
