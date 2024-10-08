package fi.espoo.vekkuli

import fi.espoo.vekkuli.config.EmailEnv
import fi.espoo.vekkuli.config.MessageUtil
import fi.espoo.vekkuli.domain.CreatePaymentParams
import fi.espoo.vekkuli.domain.Recipient
import fi.espoo.vekkuli.repository.BoatRepository
import fi.espoo.vekkuli.repository.BoatSpaceReservationRepository
import fi.espoo.vekkuli.repository.ReserverRepository
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
}
