package fi.espoo.vekkuli

import fi.espoo.vekkuli.boatSpace.terminateReservation.ReservationTerminationReason
import fi.espoo.vekkuli.domain.BoatSpaceAmenity
import fi.espoo.vekkuli.domain.BoatSpaceType
import fi.espoo.vekkuli.domain.BoatType
import fi.espoo.vekkuli.domain.CreatePaymentParams
import fi.espoo.vekkuli.domain.CreationType
import fi.espoo.vekkuli.domain.OwnershipStatus
import fi.espoo.vekkuli.domain.Payment
import fi.espoo.vekkuli.domain.PaymentStatus
import fi.espoo.vekkuli.domain.PaymentType
import fi.espoo.vekkuli.domain.ReservationStatus
import fi.espoo.vekkuli.domain.ReservationValidity
import fi.espoo.vekkuli.domain.StorageType
import fi.espoo.vekkuli.repository.JdbiPaymentRepository
import fi.espoo.vekkuli.service.PaytrailMock
import fi.espoo.vekkuli.service.SendEmailServiceMock
import fi.espoo.vekkuli.utils.TimeProvider
import fi.espoo.vekkuli.utils.createAndSeedDatabase
import fi.espoo.vekkuli.utils.mockTimeProvider
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.bindKotlin
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT
)
@ActiveProfiles("test")
abstract class IntegrationTestBase {
    @Autowired
    private lateinit var jdbiPaymentRepository: JdbiPaymentRepository

    @Autowired
    protected lateinit var jdbi: Jdbi

    @Autowired
    protected lateinit var testUtils: TestUtils

    final val citizenIdLeo: UUID = UUID.fromString("f5d377ea-5547-11ef-a1c7-7f2b94cf9afd")
    final val citizenIdOlivia: UUID = UUID.fromString("509edb00-5549-11ef-a1c7-776e76028a49")
    final val citizenIdMikko: UUID = UUID.fromString("62d90eed-4ea3-4446-8023-8dad9c01dd34")
    final val organizationId: UUID = UUID.fromString("8b220a43-86a0-4054-96f6-d29a5aba17e7")
    final val citizenIdJorma: UUID = UUID.fromString("82722a75-793a-4cbe-a3d9-a3043f2f5731")
    final val citizenIdMarko: UUID = UUID.fromString("1128bd21-fbbc-4e9a-8658-dc2044a64a58")
    final val espooCitizenWithoutReservationsId: UUID = citizenIdMikko
    final val nonEspooCitizenWithoutReservationsId: UUID = citizenIdMarko

    val userId: UUID = UUID.fromString("94833b54-132b-4ab8-b841-60df45809b3e")

    val boatSpaceIdForSlip = 1
    val boatSpaceIdForSlip2 = 2
    val boatSpaceIdForSlip3 = 3
    val boatSpaceIdForStorage = 4
    val boatSpaceIdForStorage1 = 5
    val boatSpaceIdForStorage2 = 6
    val boatSpaceIdForWinter = 8
    val boatSpaceIdForWinter2 = 9
    val boatSpaceIdForTrailer = 2203

    @MockBean
    lateinit var timeProvider: TimeProvider

    @BeforeEach
    fun setUp() {
        mockTimeProvider(timeProvider)
    }

    @BeforeAll
    fun beforeAllSuper() {
        createAndSeedDatabase(jdbi)
    }

    @BeforeEach
    fun resetDatabase() {
        // Override this method in subclasses to reset the database before each test
        PaytrailMock.reset()
        SendEmailServiceMock.resetEmails()
    }

    data class DevBoatSpace(
        val id: Int,
        val type: BoatSpaceType,
        val locationId: Int,
        val priceId: Int,
        val section: String,
        val placeNumber: Int,
        val amenity: BoatSpaceAmenity,
        val widthCm: Int,
        val lengthCm: Int,
        val description: String,
    )

    fun insertDevBoatSpace(boatSpace: DevBoatSpace) {
        jdbi.inTransaction<Unit, Exception> { handle ->
            handle
                .createUpdate(
                    """
                INSERT INTO boat_space (
                    id, type, location_id, price_id, section, place_number, amenity, width_cm, length_cm
                ) VALUES (
                    :id, :type, :locationId, :priceId, :section, :placeNumber, :amenity, :widthCm, :lengthCm
                )
                """
                ).bindKotlin(boatSpace)
                .execute()
        }
    }

    data class DevBoat(
        val id: Int,
        val registrationCode: String?,
        val reserverId: UUID,
        val name: String?,
        val widthCm: Int,
        val lengthCm: Int,
        val depthCm: Int,
        val weightKg: Int,
        val type: BoatType,
        val otherIdentification: String?,
        val extraInformation: String?,
        val ownership: OwnershipStatus,
        val deletedAt: java.time.LocalDateTime? = null
    )

    fun insertDevBoat(boat: DevBoat) {
        jdbi.inTransaction<Unit, Exception> { handle ->
            handle
                .createUpdate(
                    """
                    INSERT INTO boat (
                        id, registration_code, reserver_id, name, width_cm, length_cm, depth_cm, 
                        weight_kg, type, other_identification, extra_information, ownership, deleted_at
                    ) VALUES (
                        :id, :registrationCode, :reserverId, :name, :widthCm, :lengthCm, :depthCm, 
                        :weightKg, :type, :otherIdentification, :extraInformation, :ownership, :deletedAt
                    )
                    """.trimIndent()
                ).bindKotlin(boat)
                .execute()
        }
    }

    data class DevBoatSpaceReservation(
        val id: Int? = null,
        val reserverId: UUID? = null,
        val boatSpaceId: Int,
        val startDate: LocalDate,
        val endDate: LocalDate,
        val created: LocalDateTime = LocalDateTime.now(),
        val updated: LocalDateTime = LocalDateTime.now(),
        val status: ReservationStatus = ReservationStatus.Info,
        val boatId: Int? = null,
        val employeeId: UUID? = null,
        val actingCitizenId: UUID? = null,
        val validity: ReservationValidity = ReservationValidity.Indefinite,
        val originalReservationId: Int? = null,
        val terminationReason: ReservationTerminationReason? = null,
        val terminationComment: String? = null,
        val terminationTimestamp: LocalDateTime? = null,
        val trailerId: Int? = null,
        val storageType: StorageType = StorageType.None,
        val creationType: CreationType? = CreationType.New
    )

    fun insertDevBoatSpaceReservation(reservation: DevBoatSpaceReservation) {
        jdbi.inTransaction<Unit, Exception> { handle ->
            handle
                .createUpdate(
                    """
                    INSERT INTO boat_space_reservation (
                        id, reserver_id, boat_space_id, start_date, end_date, created, updated, 
                        status, boat_id, employee_id, acting_citizen_id, validity, original_reservation_id, 
                        termination_reason, termination_comment, termination_timestamp, trailer_id, storage_type, creation_type
                    ) VALUES (
                        :id, :reserverId, :boatSpaceId, :startDate, :endDate, :created, :updated, 
                        :status, :boatId, :employeeId, :actingCitizenId, :validity, :originalReservationId, 
                        :terminationReason, :terminationComment, :terminationTimestamp, :trailerId, :storageType, :creationType
                    )
                    """.trimIndent()
                ).bindKotlin(reservation)
                .execute()
        }
    }

    data class DevPayment(
        val reserverId: UUID,
        val paymentType: PaymentType = PaymentType.OnlinePayment,
        val status: PaymentStatus = PaymentStatus.Success,
        val reference: String = "",
        val totalCents: Int = 100,
        val vatPercentage: Double = 25.5,
        val productCode: String = "TestProduct1",
        val reservationId: Int,
        val paid: LocalDateTime? = LocalDateTime.now()
    )

    fun insertDevPayment(payment: DevPayment): Payment =
        jdbiPaymentRepository.insertPayment(
            CreatePaymentParams(
                reserverId = payment.reserverId,
                reference = payment.reference,
                totalCents = payment.totalCents,
                vatPercentage = payment.vatPercentage,
                productCode = payment.productCode,
                paymentType = payment.paymentType,
                status = payment.status,
                paid = payment.paid
            ),
            payment.reservationId
        )
}
