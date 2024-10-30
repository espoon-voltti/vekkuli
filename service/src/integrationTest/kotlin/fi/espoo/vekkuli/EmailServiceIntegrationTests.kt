package fi.espoo.vekkuli

import fi.espoo.vekkuli.domain.*
import fi.espoo.vekkuli.service.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.times
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.verify
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.util.*

@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class EmailTemplateServiceIntegrationTests : IntegrationTestBase() {
    @Autowired lateinit var reservationService: BoatReservationService

    @MockBean lateinit var emailServiceMock: TemplateEmailService

    @Test
    fun `send single email on confirmation`() {
        val madeReservation =
            createReservationInPaymentState(
                timeProvider,
                reservationService,
                this.citizenIdLeo
            )

        val payment =
            reservationService.addPaymentToReservation(
                madeReservation.id,
                CreatePaymentParams(
                    citizenId = this.citizenIdLeo,
                    reference = "1",
                    totalCents = 1,
                    vatPercentage = 24.0,
                    productCode = "1",
                )
            )

        reservationService.handlePaymentResult(mapOf("checkout-stamp" to payment.id.toString()), true)
        verify(emailServiceMock).sendEmail(
            eq("varausvahvistus"),
            eq(null),
            any(),
            eq(
                Recipient(
                    this.citizenIdLeo,
                    "leo@noreplytest.fi"
                )
            ),
            any(),
        )
    }

    @Test
    fun `send organization email on confirmation`() {
        val madeReservation =
            createReservationInPaymentState(
                timeProvider,
                reservationService,
                organizationId,
                this.citizenIdLeo
            )

        val payment =
            reservationService.addPaymentToReservation(
                madeReservation.id,
                CreatePaymentParams(
                    citizenId = this.citizenIdLeo,
                    reference = "1",
                    totalCents = 1,
                    vatPercentage = 24.0,
                    productCode = "1",
                )
            )

        reservationService.handlePaymentResult(mapOf("checkout-stamp" to payment.id.toString()), true)
        verify(emailServiceMock).sendBatchEmail(
            eq("reservation_organization_confirmation"),
            eq(null),
            any(),
            eq(
                listOf(
                    Recipient(organizationId, "eps@noreplytest.fi"),
                    Recipient(
                        citizenIdOlivia,
                        "olivia@noreplytest.fi"
                    )
                )
            ),
            any()
        )
    }

    @Test
    fun `should send correct template email on invoice`() {
        val madeReservation =
            createReservationInInfoState(
                timeProvider,
                reservationService,
                this.citizenIdLeo
            )
        reservationService.reserveBoatSpace(
            this.citizenIdLeo,
            ReserveBoatSpaceInput(
                reservationId = madeReservation.id,
                boatId = 1,
                boatType = BoatType.OutboardMotor,
                width = 1.0,
                length = 1.0,
                depth = 1.0,
                weight = 1,
                boatRegistrationNumber = "OYK342",
                boatName = "Boat",
                otherIdentification = "Other identification",
                extraInformation = "Extra information",
                ownerShip = OwnershipStatus.Owner,
                email = "leo@noreplytest.fi",
                phone = "123456789"
            ),
            ReservationStatus.Invoiced,
            ReservationValidity.FixedTerm,
            timeProvider.getCurrentDate(),
            timeProvider.getCurrentDate()
        )
        verify(emailServiceMock).sendEmail(
            eq("reservation_confirmation_invoice"),
            eq(null),
            any(),
            eq(
                Recipient(
                    this.citizenIdLeo,
                    "leo@noreplytest.fi"
                )
            ),
            any()
        )
    }
}

@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class EmailServiceIntegrationTests : IntegrationTestBase() {
    @Autowired lateinit var reservationService: BoatReservationService

    @MockBean lateinit var messageServiceMock: MessageService

    @Test
    fun `should send single email on confirmation`() {
        val madeReservation =
            createReservationInPaymentState(
                timeProvider,
                reservationService,
                this.citizenIdLeo,
                this.citizenIdLeo
            )
        val payment =
            reservationService.addPaymentToReservation(
                madeReservation.id,
                CreatePaymentParams(
                    citizenId = this.citizenIdLeo,
                    reference = "1",
                    totalCents = 1,
                    vatPercentage = 24.0,
                    productCode = "1",
                )
            )

        reservationService.handlePaymentResult(mapOf("checkout-stamp" to payment.id.toString()), true)
        verify(messageServiceMock).sendEmails(
            eq(null),
            any(),
            eq(listOf(Recipient(this.citizenIdLeo, "leo@noreplytest.fi"))),
            any(),
            any()
        )
    }

    @Test
    fun `should send multiple emails on confirmation`() {
        val madeReservation =
            createReservationInPaymentState(
                timeProvider,
                reservationService,
                organizationId,
                this.citizenIdLeo
            )
        val payment =
            reservationService.addPaymentToReservation(
                madeReservation.id,
                CreatePaymentParams(
                    citizenId = this.citizenIdLeo,
                    reference = "1",
                    totalCents = 1,
                    vatPercentage = 24.0,
                    productCode = "1",
                )
            )

        reservationService.handlePaymentResult(mapOf("checkout-stamp" to payment.id.toString()), true)
        verify(messageServiceMock).sendEmails(
            eq(null),
            any(),
            eq(listOf(Recipient(organizationId, "eps@noreplytest.fi"), Recipient(citizenIdOlivia, "olivia@noreplytest.fi"))),
            any(),
            any(),
        )
    }

    @Test
    fun `should send email on invoice`() {
        val madeReservation =
            createReservationInInfoState(
                timeProvider,
                reservationService,
                this.citizenIdLeo
            )
        reservationService.reserveBoatSpace(
            this.citizenIdLeo,
            ReserveBoatSpaceInput(
                reservationId = madeReservation.id,
                boatId = 1,
                boatType = BoatType.OutboardMotor,
                width = 1.0,
                length = 1.0,
                depth = 1.0,
                weight = 1,
                boatRegistrationNumber = "1",
                boatName = "1",
                otherIdentification = "1",
                extraInformation = "1",
                ownerShip = OwnershipStatus.Owner,
                email = "leo@noreplytest.fi",
                phone = "123456789"
            ),
            ReservationStatus.Invoiced,
            ReservationValidity.FixedTerm,
            timeProvider.getCurrentDate(),
            timeProvider.getCurrentDate()
        )
        verify(messageServiceMock).sendEmails(
            eq(null),
            any(),
            eq(listOf(Recipient(this.citizenIdLeo, "leo@noreplytest.fi"))),
            any(),
            any()
        )
    }
}
