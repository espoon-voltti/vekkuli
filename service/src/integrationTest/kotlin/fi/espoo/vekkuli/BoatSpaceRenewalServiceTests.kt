package fi.espoo.vekkuli

import fi.espoo.vekkuli.boatSpace.renewal.BoatSpaceRenewalService
import fi.espoo.vekkuli.boatSpace.renewal.RenewalReservationInput
import fi.espoo.vekkuli.domain.BoatType
import fi.espoo.vekkuli.domain.OwnershipStatus
import fi.espoo.vekkuli.domain.ReservationStatus
import fi.espoo.vekkuli.service.BoatReservationService
import fi.espoo.vekkuli.service.CitizenService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import kotlin.test.assertNotNull

@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class BoatSpaceRenewalServiceTests : IntegrationTestBase() {
    @BeforeEach
    override fun resetDatabase() {
        deleteAllReservations(jdbi)
    }

    @Autowired
    private lateinit var citizenService: CitizenService

    @Autowired
    lateinit var reservationService: BoatReservationService

    @Autowired
    lateinit var boatSpaceRenewalService: BoatSpaceRenewalService

    @Test
    fun `should fetch existing renewal reservation for employee`() {
        val reservation =
            testUtils.createReservationInConfirmedState(
                CreateReservationParams(
                    timeProvider,
                    citizenIdLeo,
                    1,
                )
            )
        var createdRenewal = boatSpaceRenewalService.getOrCreateRenewalReservationForEmployee(userId, reservation.id)
        assertNotNull(createdRenewal, "Renewal reservation should be created")
        assertEquals(ReservationStatus.Renewal, createdRenewal.status, "Status should be renewal")

        var newReservation = boatSpaceRenewalService.getOrCreateRenewalReservationForEmployee(userId, reservation.id)
        assertEquals(createdRenewal.id, newReservation.id, "Should fetch existing renewal reservation")
        assertEquals(ReservationStatus.Renewal, newReservation.status, "Status should be renewal")
    }

    @Test
    fun `should update renewal reservation`() {
        val reservation =
            testUtils.createReservationInRenewState(
                CreateReservationParams(
                    timeProvider,
                    citizenIdLeo,
                    1,
                )
            )

        val renewalInput =
            RenewalReservationInput(
                boatId = 2,
                boatType = BoatType.Sailboat,
                width = 3.0,
                length = 4.0,
                depth = 1.0,
                weight = 100,
                boatRegistrationNumber = "12345",
                boatName = "TestBoat",
                otherIdentification = "OtherID",
                extraInformation = "ExtraInfo",
                ownership = OwnershipStatus.Owner,
                email = "citizen@example.com",
                phone = "1234567890",
                agreeToRules = true,
                certifyInformation = true,
                noRegistrationNumber = false,
                renewedReservationId = 4
            )

        boatSpaceRenewalService.updateRenewReservation(citizenIdLeo, renewalInput, reservation.id)
        val updatedReservation = reservationService.getReservationWithReserver(reservation.id)

        assertEquals(renewalInput.boatId, updatedReservation?.boatId, "Boat ID should be updated")
        assertEquals(renewalInput.email, updatedReservation?.email, "User email should be updated")
        assertEquals(ReservationStatus.Payment, updatedReservation?.status, "Status should be set to Payment")
    }

    @Test
    fun `should activate renewal and send invoice`() {
        val oldReservation =
            testUtils.createReservationInConfirmedState(
                CreateReservationParams(
                    timeProvider,
                    citizenIdLeo,
                    1,
                )
            )
        val renewalReservation = boatSpaceRenewalService.getOrCreateRenewalReservationForCitizen(citizenIdLeo, oldReservation.id)

        boatSpaceRenewalService.activateRenewalAndSendInvoice(renewalReservation.id, oldReservation.id)

        val updatedOldReservation = reservationService.getBoatSpaceReservation(oldReservation.id)
        val updatedRenewalReservation = reservationService.getBoatSpaceReservation(renewalReservation.id)

        assertNotNull(updatedRenewalReservation, "Renewal reservation should exist")
        assertEquals(ReservationStatus.Invoiced, updatedRenewalReservation?.status, "Renewal reservation should be invoiced")

        assertNotNull(updatedOldReservation, "Old reservation should exist")
        assertEquals(
            timeProvider.getCurrentDate().minusDays(1),
            updatedOldReservation?.endDate,
            "Old reservation should be marked as ended"
        )
    }

    @Test
    fun `should generate invoice model for reservation`() {
        val reservation =
            testUtils.createReservationInRenewState(
                CreateReservationParams(
                    timeProvider,
                    citizenIdLeo,
                    1,
                )
            )

        val invoiceModel = boatSpaceRenewalService.getSendInvoiceModel(reservation.id)

        assertNotNull(invoiceModel, "Invoice model should be generated")
        assertEquals(reservation.id, invoiceModel.reservationId, "Reservation ID should match")
        assertEquals("Merellinen ulkoilu", invoiceModel.invoiceRows.first().organization, "Organization should match")
    }

    @Test
    fun `should prefill renew application with customer information`() {
        val reservation =
            testUtils.createReservationInConfirmedState(
                CreateReservationParams(
                    timeProvider,
                    citizenIdLeo,
                    1,
                )
            )
        val renewalInput =
            RenewalReservationInput(
                boatId = 0,
                boatType = BoatType.Sailboat,
                width = 3.0,
                length = 4.0,
                depth = 1.0,
                weight = 100,
                boatRegistrationNumber = "12345",
                boatName = "TestBoat",
                otherIdentification = "OtherID",
                extraInformation = "ExtraInfo",
                ownership = OwnershipStatus.Owner,
                email = "citizen@example.com",
                phone = "1234567890",
                agreeToRules = true,
                certifyInformation = true,
                noRegistrationNumber = false,
                renewedReservationId = 4
            )

        val citizen = citizenService.getCitizen(citizenIdLeo)
        val renewedReservation = boatSpaceRenewalService.getOrCreateRenewalReservationForCitizen(citizenIdLeo, reservation.id)

        val viewParams = boatSpaceRenewalService.buildBoatSpaceRenewalViewParams(citizenIdLeo, renewedReservation, renewalInput)

        assertEquals(citizenIdLeo, viewParams.citizen?.id, "Citizen ID should match")
        assertEquals(citizen?.email, viewParams.input.email, "Email should have been updated")
    }

    @Test
    fun `should prefill renew application with boat information`() {
        val reservation =
            testUtils.createReservationInConfirmedState(
                CreateReservationParams(
                    timeProvider,
                    citizenIdLeo,
                    1,
                )
            )
        val renewalInput =
            RenewalReservationInput(
                boatId = 0,
                boatType = BoatType.Sailboat,
                width = 3.0,
                length = 4.0,
                depth = 1.0,
                weight = 100,
                boatRegistrationNumber = "12345",
                boatName = "TestBoat",
                otherIdentification = "OtherID",
                extraInformation = "ExtraInfo",
                ownership = OwnershipStatus.Owner,
                email = "citizen@example.com",
                phone = "1234567890",
                agreeToRules = true,
                certifyInformation = true,
                noRegistrationNumber = false,
                renewedReservationId = 4
            )

        val renewedReservation = boatSpaceRenewalService.getOrCreateRenewalReservationForCitizen(citizenIdLeo, reservation.id)

        val viewParams = boatSpaceRenewalService.buildBoatSpaceRenewalViewParams(citizenIdLeo, renewedReservation, renewalInput)

        assertEquals(renewalInput.boatName, viewParams.input.boatName, "Email should have been updated")
        assertEquals(renewalInput.boatId, viewParams.input.boatId, "Email should have been updated")
        assertEquals(renewalInput.boatType, viewParams.input.boatType, "Email should have been updated")
        assertEquals(renewalInput.extraInformation, viewParams.input.extraInformation, "Email should have been updated")
        assertEquals(renewalInput.weight, viewParams.input.weight, "Email should have been updated")
        assertEquals(renewalInput.width, viewParams.input.width, "Email should have been updated")
        assertEquals(renewalInput.length, viewParams.input.length, "Email should have been updated")
        assertEquals(renewalInput.depth, viewParams.input.depth, "Email should have been updated")
    }
}
