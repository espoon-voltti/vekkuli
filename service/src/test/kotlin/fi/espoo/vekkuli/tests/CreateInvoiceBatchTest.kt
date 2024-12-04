package fi.espoo.vekkuli.tests

import fi.espoo.vekkuli.domain.BoatSpaceType
import fi.espoo.vekkuli.service.*
import fi.espoo.vekkuli.utils.TimeProvider
import fi.espoo.vekkuli.utils.mockTimeProvider
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import java.time.LocalDate
import java.time.LocalDateTime

@SpringBootTest
class CreateInvoiceBatchTest {
    @MockBean
    lateinit var timeProvider: TimeProvider

    @Test
    fun `Create invoice batch for a citizen`() {
        val today = LocalDate.of(2025, 1, 1)
        mockTimeProvider(timeProvider, LocalDateTime.of(today.year, today.month, today.dayOfMonth, 0, 0))

        val given =
            InvoiceData(
                dueDate = today.plusDays(21),
                startDate = today,
                endDate = today.plusYears(1),
                invoiceNumber = 20021,
                ssn = "123456-7890",
                orgId = null,
                registerNumber = "1234567-8",
                lastname = "Sukunimi",
                firstnames = "Etunimi Toinennimi",
                street = "Katuosoite 123",
                post = "Postitoimipaikka",
                postalCode = "00100",
                language = "fi",
                mobilePhone = "0501234567",
                email = "test@test.com",
                priceCents = 10000,
                description = "Kuvaus",
                type = BoatSpaceType.Slip,
                orgName = null
            )

        val expected =
            InvoiceBatch(
                agreementType = 256,
                batchDate = today.toString(),
                batchNumber = given.invoiceNumber,
                currency = "EUR",
                sourcePrinted = false,
                systemId = "VKK",
                invoices =
                    listOf(
                        Invoice(
                            invoiceNumber = given.invoiceNumber,
                            useInvoiceNumber = true,
                            dueDate = given.dueDate.toString(),
                            client =
                                Client(
                                    ssn = given.ssn,
                                    lastname = given.lastname ?: "",
                                    firstnames = given.firstnames ?: "",
                                    street = given.street,
                                    post = given.post,
                                    postalCode = given.postalCode,
                                    language = "fi",
                                    mobilePhone = given.mobilePhone,
                                    email = given.email
                                ),
                            rows =
                                listOf(
                                    Row(
                                        productGroup = "2560001",
                                        productComponent = "T1270",
                                        periodStartDate = given.startDate.toString(),
                                        periodEndDate = given.endDate.toString(),
                                        unitCount = 100,
                                        amount = given.priceCents.toLong(),
                                        description = given.description,
                                        account = 329700,
                                        costCenter = "1230329",
                                    )
                                )
                        )
                    ),
            )

        val actual = createInvoiceBatch(given, timeProvider)

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected)
    }

    @Test
    fun `Create invoice batch for an organization`() {
        val today = LocalDate.of(2025, 1, 1)
        mockTimeProvider(timeProvider, LocalDateTime.of(today.year, today.month, today.dayOfMonth, 0, 0))

        val given =
            InvoiceData(
                dueDate = today.plusDays(21),
                startDate = today,
                endDate = today.plusYears(1),
                invoiceNumber = 10001,
                ssn = "123456-7890",
                orgName = "Yhdistys ry",
                orgId = "1234567-8",
                registerNumber = "1234567-8",
                street = "Katuosoite 123",
                post = "Postitoimipaikka",
                postalCode = "00100",
                language = "fi",
                mobilePhone = "0501234567",
                email = "test@test.com",
                priceCents = 10000,
                description = "Kuvaus",
                type = BoatSpaceType.Slip,
                firstnames = "Etunimi",
                lastname = "Sukunimi"
            )

        val expected =
            InvoiceBatch(
                agreementType = 256,
                batchDate = today.toString(),
                batchNumber = given.invoiceNumber,
                currency = "EUR",
                sourcePrinted = false,
                systemId = "VKK",
                invoices =
                    listOf(
                        Invoice(
                            invoiceNumber = given.invoiceNumber,
                            useInvoiceNumber = true,
                            dueDate = given.dueDate.toString(),
                            client =
                                Client(
                                    lastname = given.orgName ?: "",
                                    ytunnus = given.orgId,
                                    street = given.street,
                                    post = given.post,
                                    contactPerson = "${given.firstnames} ${given.lastname}",
                                    postalCode = given.postalCode,
                                    language = "fi",
                                    mobilePhone = given.mobilePhone,
                                    email = given.email
                                ),
                            rows =
                                listOf(
                                    Row(
                                        productGroup = "2560001",
                                        productComponent = "T1270",
                                        periodStartDate = given.startDate.toString(),
                                        periodEndDate = given.endDate.toString(),
                                        unitCount = 100,
                                        amount = given.priceCents.toLong(),
                                        description = given.description,
                                        account = 329700,
                                        costCenter = "1230329",
                                    )
                                )
                        )
                    ),
            )

        val actual = createInvoiceBatch(given, timeProvider)

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected)
    }
}
