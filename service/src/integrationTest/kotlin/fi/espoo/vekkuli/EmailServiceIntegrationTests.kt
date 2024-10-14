package fi.espoo.vekkuli

import fi.espoo.vekkuli.config.EmailEnv
import fi.espoo.vekkuli.config.MessageUtil
import fi.espoo.vekkuli.domain.*
import fi.espoo.vekkuli.repository.BoatRepository
import fi.espoo.vekkuli.repository.BoatSpaceReservationRepository
import fi.espoo.vekkuli.repository.ReserverRepository
import fi.espoo.vekkuli.repository.SentMessageRepository
import fi.espoo.vekkuli.service.*
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.mock
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.verify
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.util.*

@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class EmailServiceIntegrationTests : IntegrationTestBase() {
    @Autowired lateinit var paymentService: PaymentService

    @Autowired lateinit var boatSpaceReservationRepo: BoatSpaceReservationRepository

    @Autowired lateinit var reservationWarningRepo: ReservationWarningRepository

    @Autowired lateinit var reserverRepo: ReserverRepository

    @Autowired lateinit var boatRepository: BoatRepository

    @Autowired lateinit var messageUtil: MessageUtil

    @Autowired lateinit var paytrail: PaytrailInterface

    @Autowired lateinit var emailEnv: EmailEnv

    @Autowired lateinit var organizationService: OrganizationService

    @Autowired lateinit var templateRepo: EmailTemplateRepository

    @Autowired lateinit var messageRepository: SentMessageRepository

    @BeforeAll
    @Test
    fun `send single email on confirmation`() {
        val emailServiceMock = mock<TemplateEmailService>()
        val reservationService =
            BoatReservationService(
                paymentService,
                boatSpaceReservationRepo,
                reservationWarningRepo,
                reserverRepo,
                boatRepository,
                emailServiceMock,
                messageUtil,
                paytrail,
                emailEnv,
                organizationService
            )
        val madeReservation = createReservationInPaymentState(reservationService, citizenId)

        val payment =
            reservationService.addPaymentToReservation(
                madeReservation.id,
                CreatePaymentParams(
                    citizenId = citizenId,
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
                    citizenId,
                    "leo@noreplytest.fi"
                )
            ),
            any()
        )
    }

    @Test
    fun `send organization email on confirmation`() {
        val emailServiceMock = mock<TemplateEmailService>()
        val reservationService =
            BoatReservationService(
                paymentService,
                boatSpaceReservationRepo,
                reservationWarningRepo,
                reserverRepo,
                boatRepository,
                emailServiceMock,
                messageUtil,
                paytrail,
                emailEnv,
                organizationService
            )
        val madeReservation = createReservationInPaymentState(reservationService, organizationId, citizenId)

        val payment =
            reservationService.addPaymentToReservation(
                madeReservation.id,
                CreatePaymentParams(
                    citizenId = citizenId,
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
                        UUID.fromString("509edb00-5549-11ef-a1c7-776e76028a49"),
                        "olivia@noreplytest.fi"
                    )
                )
            ),
            any()
        )
    }

    @Test
    fun `should send single email on confirmation`() {
        val sendEmailInterfaceMock = mock<SendEmailInterface>()

        val messageServiceMock = MessageService(messageRepository, sendEmailInterfaceMock)
        val reservationService =
            BoatReservationService(
                paymentService,
                boatSpaceReservationRepo,
                reservationWarningRepo,
                reserverRepo,
                boatRepository,
                TemplateEmailService(messageServiceMock, templateRepo),
                messageUtil,
                paytrail,
                emailEnv,
                organizationService
            )
        val madeReservation = createReservationInPaymentState(reservationService, citizenId, citizenId)
        val payment =
            reservationService.addPaymentToReservation(
                madeReservation.id,
                CreatePaymentParams(
                    citizenId = citizenId,
                    reference = "1",
                    totalCents = 1,
                    vatPercentage = 24.0,
                    productCode = "1",
                )
            )

        reservationService.handlePaymentResult(mapOf("checkout-stamp" to payment.id.toString()), true)
        verify(sendEmailInterfaceMock).sendMultipleEmails(
            any(),
            eq(listOf("leo@noreplytest.fi")),
            any(),
            any()
        )
    }

    @Test
    fun `should send multiple emails on confirmation`() {
        val sendEmailInterfaceMock = mock<SendEmailInterface>()

        val messageServiceMock = MessageService(messageRepository, sendEmailInterfaceMock)
        val reservationService =
            BoatReservationService(
                paymentService,
                boatSpaceReservationRepo,
                reservationWarningRepo,
                reserverRepo,
                boatRepository,
                TemplateEmailService(messageServiceMock, templateRepo),
                messageUtil,
                paytrail,
                emailEnv,
                organizationService
            )
        val madeReservation = createReservationInPaymentState(reservationService, organizationId, citizenId)
        val payment =
            reservationService.addPaymentToReservation(
                madeReservation.id,
                CreatePaymentParams(
                    citizenId = citizenId,
                    reference = "1",
                    totalCents = 1,
                    vatPercentage = 24.0,
                    productCode = "1",
                )
            )

        reservationService.handlePaymentResult(mapOf("checkout-stamp" to payment.id.toString()), true)
        verify(sendEmailInterfaceMock).sendMultipleEmails(
            any(),
            eq(listOf("eps@noreplytest.fi", "olivia@noreplytest.fi")),
            any(),
            any()
        )
    }

    @Test
    fun `should send correct template email on invoice`() {
        val emailServiceMock = mock<TemplateEmailService>()
        val reservationService =
            BoatReservationService(
                paymentService,
                boatSpaceReservationRepo,
                reservationWarningRepo,
                reserverRepo,
                boatRepository,
                emailServiceMock,
                messageUtil,
                paytrail,
                emailEnv,
                organizationService
            )

        val madeReservation = createReservationInInfoState(reservationService, citizenId)
        reservationService.reserveBoatSpace(
            citizenId,
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
            ReservationStatus.Invoiced
        )

        verify(emailServiceMock).sendEmail(
            eq("reservation_confirmation_invoice"),
            eq(null),
            any(),
            eq(
                Recipient(
                    citizenId,
                    "leo@noreplytest.fi"
                )
            ),
            any()
        )
    }

    @Test
    fun `should send email on invoice`() {
        val sendEmailInterfaceMock = mock<SendEmailInterface>()

        val messageServiceMock = MessageService(messageRepository, sendEmailInterfaceMock)
        val reservationService =
            BoatReservationService(
                paymentService,
                boatSpaceReservationRepo,
                reservationWarningRepo,
                reserverRepo,
                boatRepository,
                TemplateEmailService(messageServiceMock, templateRepo),
                messageUtil,
                paytrail,
                emailEnv,
                organizationService
            )

        val madeReservation = createReservationInInfoState(reservationService, citizenId)
        reservationService.reserveBoatSpace(
            citizenId,
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
            ReservationStatus.Invoiced
        )
        verify(sendEmailInterfaceMock).sendMultipleEmails(
            any(),
            eq(listOf("leo@noreplytest.fi")),
            any(),
            any()
        )
    }
}
