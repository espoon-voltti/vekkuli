package fi.espoo.vekkuli

import fi.espoo.vekkuli.service.SendEmailServiceMock
import kotlin.test.assertEquals
import kotlin.test.assertTrue

open class EmailSendingTest : PlaywrightTest() {
    protected fun assertZeroEmailsSent() {
        messageService.sendScheduledEmails()
        assertEquals(0, SendEmailServiceMock.emails.size)
    }

    // New Reservations
    protected fun assertEmailIsSentOfCitizensIndefiniteSlipReservation(
        emailAddress: String? = null,
        sendAndAssertSendCount: Boolean? = null
    ) = assertIndefiniteReservationEmail(
        emailAddress,
        "Vahvistus Espoon kaupungin laituripaikkavarauksesta",
        "Veneelle varaamasi laituripaikka on maksettu ja varaus on vahvistettu.",
        sendAndAssertSendCount
    )

    protected fun assertEmailIsSentOfEmployeesIndefiniteSlipReservationWithInvoice(
        emailAddress: String? = null,
        sendAndAssertSendCount: Boolean? = null
    ) = assertIndefiniteReservationEmail(
        emailAddress,
        "Espoon kaupungin laituripaikkavaraus",
        "Sinulle on lähetetty lasku osoitteeseen",
        sendAndAssertSendCount
    )

    protected fun assertEmailIsSentOfEmployeesIndefiniteSlipReservationWithoutPayment(
        emailAddress: String? = null,
        sendAndAssertSendCount: Boolean? = null
    ) = assertIndefiniteReservationEmail(
        emailAddress,
        "Vahvistus Espoon kaupungin laituripaikkavarauksesta",
        "Sinulle on varattu espoon kaupungin laituripaikka",
        sendAndAssertSendCount
    )

    protected fun assertEmailIsSentOfEmployeesFixedTermSlipReservationWithInvoice(
        emailAddress: String? = null,
        endDate: String? = null,
        sendAndAssertSendCount: Boolean? = null
    ) = assertFixedTermReservationEmail(
        emailAddress,
        "Espoon kaupungin laituripaikkavaraus",
        "Sinulle on lähetetty lasku osoitteeseen",
        endDate,
        sendAndAssertSendCount
    )

    protected fun assertEmailIsSentOfEmployeesFixedTermSlipReservationWithoutPayment(
        emailAddress: String? = null,
        endDate: String? = null,
        sendAndAssertSendCount: Boolean? = null
    ) = assertFixedTermReservationEmail(
        emailAddress,
        "Vahvistus Espoon kaupungin laituripaikkavarauksesta",
        "Sinulle on varattu espoon kaupungin laituripaikka",
        endDate,
        sendAndAssertSendCount
    )

    protected fun assertEmailIsSentOfCitizensFixedTermSlipReservation(
        emailAddress: String? = null,
        endDate: String? = null,
        sendAndAssertSendCount: Boolean? = null
    ) = assertFixedTermReservationEmail(
        emailAddress,
        "Vahvistus Espoon kaupungin laituripaikkavarauksesta",
        "Veneelle varaamasi laituripaikka on maksettu ja varaus on vahvistettu.",
        endDate,
        sendAndAssertSendCount
    )

    protected fun assertEmailIsSentOfCitizensStorageSpaceReservation(
        emailAddress: String? = null,
        sendAndAssertSendCount: Boolean? = null
    ) = assertIndefiniteReservationEmail(
        emailAddress,
        "Vahvistus Espoon kaupungin säilytyspaikkavarauksesta",
        "Veneelle varaamasi säilytyspaikka on maksettu ja varaus on vahvistettu.",
        sendAndAssertSendCount
    )

    protected fun assertEmailIsSentOfEmployeesIndefiniteStorageSpaceReservationWithInvoice(
        emailAddress: String? = null,
        sendAndAssertSendCount: Boolean? = null
    ) = assertIndefiniteReservationEmail(
        emailAddress,
        "Espoon kaupungin säilytyspaikkavaraus",
        "Sinulle on lähetetty lasku osoitteeseen",
        sendAndAssertSendCount
    )

    protected fun assertEmailIsSentOfEmployeesIndefiniteStorageSpaceReservationWithoutPayment(
        emailAddress: String? = null,
        sendAndAssertSendCount: Boolean? = null
    ) = assertIndefiniteReservationEmail(
        emailAddress,
        "Vahvistus Espoon kaupungin säilytyspaikkavarauksesta",
        "Sinulle on varattu espoon kaupungin säilytyspaikka",
        sendAndAssertSendCount
    )

    protected fun assertEmailIsSentOfEmployeesFixedTermStorageReservationWithInvoice(
        emailAddress: String? = null,
        endDate: String? = null,
        sendAndAssertSendCount: Boolean? = null
    ) = assertFixedTermReservationEmail(
        emailAddress,
        "Espoon kaupungin säilytyspaikkavaraus",
        "Sinulle on lähetetty lasku osoitteeseen",
        endDate,
        sendAndAssertSendCount
    )

    protected fun assertEmailIsSentOfEmployeesFixedTermStorageReservationWithoutPayment(
        emailAddress: String? = null,
        endDate: String? = null,
        sendAndAssertSendCount: Boolean? = null
    ) = assertFixedTermReservationEmail(
        emailAddress,
        "Vahvistus Espoon kaupungin säilytyspaikkavarauksesta",
        "Sinulle on varattu espoon kaupungin säilytyspaikka",
        endDate,
        sendAndAssertSendCount
    )

    protected fun assertEmailIsSentOfCitizensIndefiniteTrailerReservation(
        emailAddress: String? = null,
        sendAndAssertSendCount: Boolean? = null
    ) = assertIndefiniteReservationEmail(
        emailAddress,
        "Vahvistus Espoon kaupungin traileripaikkavarauksesta",
        "Veneelle varaamasi traileripaikka on maksettu ja varaus on vahvistettu.",
        sendAndAssertSendCount
    )

    protected fun assertEmailIsSentOfEmployeeIndefiniteTrailerReservationWithInvoice(
        emailAddress: String? = null,
        sendAndAssertSendCount: Boolean? = null
    ) = assertIndefiniteReservationEmail(
        emailAddress,
        "Espoon kaupungin traileripaikkavaraus",
        "Sinulle on lähetetty lasku osoitteeseen",
        sendAndAssertSendCount
    )

    protected fun assertEmailIsSentOfEmployeeIndefiniteTrailerReservationWithoutPayment(
        emailAddress: String? = null,
        sendAndAssertSendCount: Boolean? = null
    ) = assertIndefiniteReservationEmail(
        emailAddress,
        "Vahvistus Espoon kaupungin traileripaikkavarauksesta",
        "Sinulle on varattu espoon kaupungin traileripaikka",
        sendAndAssertSendCount
    )

    protected fun assertEmailIsSentOfEmployeeFixedTermTrailerReservationWithInvoice(
        emailAddress: String? = null,
        endDate: String? = null,
        sendAndAssertSendCount: Boolean? = null
    ) = assertFixedTermReservationEmail(
        emailAddress,
        "Espoon kaupungin traileripaikkavaraus",
        "Sinulle on lähetetty lasku osoitteeseen",
        endDate,
        sendAndAssertSendCount
    )

    protected fun assertEmailIsSentOfEmployeeFixedTermTrailerReservationWithoutPayment(
        emailAddress: String? = null,
        endDate: String? = null,
        sendAndAssertSendCount: Boolean? = null
    ) = assertFixedTermReservationEmail(
        emailAddress,
        "Vahvistus Espoon kaupungin traileripaikkavarauksesta",
        "Sinulle on varattu espoon kaupungin traileripaikka",
        endDate,
        sendAndAssertSendCount
    )

    protected fun assertEmailIsSentOfCitizensFixedTermTrailerReservation(
        emailAddress: String? = null,
        endDate: String? = null,
        sendAndAssertSendCount: Boolean? = null
    ) = assertFixedTermReservationEmail(
        emailAddress,
        "Vahvistus Espoon kaupungin traileripaikkavarauksesta",
        "Veneelle varaamasi traileripaikka on maksettu ja varaus on vahvistettu.",
        endDate,
        sendAndAssertSendCount
    )

    protected fun assertEmailIsSentOfCitizensWinterSpaceReservation(
        emailAddress: String? = null,
        sendAndAssertSendCount: Boolean? = null
    ) = assertIndefiniteReservationEmail(
        emailAddress,
        "Vahvistus Espoon kaupungin talvipaikkavarauksesta",
        "Veneelle varaamasi talvipaikka on maksettu ja varaus on vahvistettu.",
        sendAndAssertSendCount
    )

    protected fun assertEmailIsSentOfEmployeeIndefiniteWinterSpaceReservationWithInvoice(
        emailAddress: String? = null,
        sendAndAssertSendCount: Boolean? = null
    ) = assertIndefiniteReservationEmail(
        emailAddress,
        "Espoon kaupungin talvipaikkavaraus",
        "Sinulle on lähetetty lasku osoitteeseen",
        sendAndAssertSendCount
    )

    protected fun assertEmailIsSentOfEmployeeIndefiniteWinterSpaceReservationWithoutPayment(
        emailAddress: String? = null,
        sendAndAssertSendCount: Boolean? = null
    ) = assertIndefiniteReservationEmail(
        emailAddress,
        "Vahvistus Espoon kaupungin talvipaikkavarauksesta",
        "Sinulle on varattu espoon kaupungin talvipaikka",
        sendAndAssertSendCount
    )

    protected fun assertEmailIsSentOfEmployeeFixedTermWinterSpaceReservationWithInvoice(
        emailAddress: String? = null,
        endDate: String? = null,
        sendAndAssertSendCount: Boolean? = null
    ) = assertFixedTermReservationEmail(
        emailAddress,
        "Espoon kaupungin talvipaikkavaraus",
        "Sinulle on lähetetty lasku osoitteeseen",
        endDate,
        sendAndAssertSendCount
    )

    protected fun assertEmailIsSentOfEmployeeFixedTermWinterSpaceReservationWithoutPayment(
        emailAddress: String? = null,
        endDate: String? = null,
        sendAndAssertSendCount: Boolean? = null
    ) = assertFixedTermReservationEmail(
        emailAddress,
        "Vahvistus Espoon kaupungin talvipaikkavarauksesta",
        "Sinulle on varattu espoon kaupungin talvipaikka",
        endDate,
        sendAndAssertSendCount
    )

    // Renewals
    protected fun assertEmailIsSentOfCitizensSlipRenewal(
        emailAddress: String? = null,
        sendAndAssertSendCount: Boolean? = null
    ) = assertIndefiniteReservationEmail(
        emailAddress,
        "Espoon kaupungin laituripaikkavarauksen jatkaminen",
        "Veneelle varaamasi laituripaikka on maksettu ja varaus on vahvistettu uudelle kaudelle.",
        sendAndAssertSendCount
    )

    protected fun assertEmailIsSentOfEmployeeSlipRenewalWithInvoice(
        emailAddress: String? = null,
        sendAndAssertSendCount: Boolean? = null
    ) = assertIndefiniteReservationEmail(
        emailAddress,
        "Espoon kaupungin laituripaikkavarauksen jatkaminen",
        "Sinulle on lähetetty lasku osoitteeseen",
        sendAndAssertSendCount
    )

    protected fun assertEmailIsSentOfEmployeeSlipRenewalWithoutPayment(
        emailAddress: String? = null,
        sendAndAssertSendCount: Boolean? = null
    ) = assertIndefiniteReservationEmail(
        emailAddress,
        "Espoon kaupungin laituripaikkavarauksen jatkaminen",
        "Varaamasi Espoon kaupungin laituripaikka on jatkettu uudelle kaudelle.",
        sendAndAssertSendCount
    )

    protected fun assertEmailIsSentOfCitizensStorageSpaceRenewal(
        emailAddress: String? = null,
        sendAndAssertSendCount: Boolean? = null
    ) = assertIndefiniteReservationEmail(
        emailAddress,
        "Espoon kaupungin säilytyspaikkavarauksen jatkaminen",
        "Veneelle varaamasi säilytyspaikka on maksettu ja varaus on vahvistettu uudelle kaudelle.",
        sendAndAssertSendCount
    )

    protected fun assertEmailIsSentOfEmployeeStorageSpaceRenewalWithInvoice(
        emailAddress: String? = null,
        sendAndAssertSendCount: Boolean? = null
    ) = assertIndefiniteReservationEmail(
        emailAddress,
        "Espoon kaupungin säilytyspaikkavarauksen jatkaminen",
        "Sinulle on lähetetty lasku osoitteeseen",
        sendAndAssertSendCount
    )

    protected fun assertEmailIsSentOfEmployeeStorageSpaceRenewalWithoutPayment(
        emailAddress: String? = null,
        sendAndAssertSendCount: Boolean? = null
    ) = assertIndefiniteReservationEmail(
        emailAddress,
        "Espoon kaupungin säilytyspaikkavarauksen jatkaminen",
        "Varaamasi Espoon kaupungin säilytyspaikka on jatkettu uudelle kaudelle.",
        sendAndAssertSendCount
    )

    protected fun assertEmailIsSentOfCitizensWinterSpaceRenewal(
        emailAddress: String? = null,
        sendAndAssertSendCount: Boolean? = null
    ) = assertIndefiniteReservationEmail(
        emailAddress,
        "Espoon kaupungin talvipaikkavarauksen jatkaminen",
        "Veneelle varaamasi talvipaikka on maksettu ja varaus on vahvistettu uudelle kaudelle.",
        sendAndAssertSendCount
    )

    protected fun assertEmailIsSentOfEmployeeWinterSpaceRenewalWithInvoice(
        emailAddress: String? = null,
        sendAndAssertSendCount: Boolean? = null
    ) = assertIndefiniteReservationEmail(
        emailAddress,
        "Espoon kaupungin talvipaikkavarauksen jatkaminen",
        "Sinulle on lähetetty lasku osoitteeseen",
        sendAndAssertSendCount
    )

    protected fun assertEmailIsSentOfEmployeeWinterSpaceRenewalWithoutPayment(
        emailAddress: String? = null,
        sendAndAssertSendCount: Boolean? = null
    ) = assertIndefiniteReservationEmail(
        emailAddress,
        "Espoon kaupungin talvipaikkavarauksen jatkaminen",
        "Varaamasi Espoon kaupungin talvipaikka on jatkettu uudelle kaudelle.",
        sendAndAssertSendCount
    )

    protected fun assertEmailIsSentOfCitizensTrailerRenewal(
        emailAddress: String? = null,
        sendAndAssertSendCount: Boolean? = null
    ) = assertIndefiniteReservationEmail(
        emailAddress,
        "Espoon kaupungin traileripaikkavarauksen jatkaminen",
        "Veneelle varaamasi traileripaikka on maksettu ja varaus on vahvistettu uudelle kaudelle.",
        sendAndAssertSendCount
    )

    protected fun assertEmailIsSentOfEmployeeTrailerRenewalWithInvoice(
        emailAddress: String? = null,
        sendAndAssertSendCount: Boolean? = null
    ) = assertIndefiniteReservationEmail(
        emailAddress,
        "Espoon kaupungin traileripaikkavarauksen jatkaminen",
        "Sinulle on lähetetty lasku osoitteeseen",
        sendAndAssertSendCount
    )

    protected fun assertEmailIsSentOfEmployeeTrailerRenewalWithoutPayment(
        emailAddress: String? = null,
        sendAndAssertSendCount: Boolean? = null
    ) = assertIndefiniteReservationEmail(
        emailAddress,
        "Espoon kaupungin traileripaikkavarauksen jatkaminen",
        "Varaamasi Espoon kaupungin traileripaikka on jatkettu uudelle kaudelle.",
        sendAndAssertSendCount
    )

    // Switches
    protected fun assertEmailIsSentOfCitizensIndefiniteSlipSwitch(
        emailAddress: String? = null,
        sendAndAssertSendCount: Boolean? = null
    ) = assertIndefiniteReservationEmail(
        emailAddress,
        "Vahvistus Espoon kaupungin laituripaikkavarauksen vaihdosta",
        "Olet vaihtanut Espoon kaupungilta vuokraamaasi laituripaikkaa",
        sendAndAssertSendCount
    )

    protected fun assertEmailIsSentOfCitizensFixedTermSlipSwitch(
        emailAddress: String? = null,
        endDate: String? = null,
        sendAndAssertSendCount: Boolean? = null
    ) = assertFixedTermReservationEmail(
        emailAddress,
        "Vahvistus Espoon kaupungin laituripaikkavarauksen vaihdosta",
        "Olet vaihtanut Espoon kaupungilta vuokraamaasi laituripaikkaa",
        endDate,
        sendAndAssertSendCount
    )

    protected fun assertEmailIsSentOfCitizensStorageSpaceSwitch(
        emailAddress: String? = null,
        sendAndAssertSendCount: Boolean? = null
    ) = assertIndefiniteReservationEmail(
        emailAddress,
        "Vahvistus Espoon kaupungin säilytyspaikkavarauksen vaihdosta",
        "Olet vaihtanut Espoon kaupungilta vuokraamaasi säilytyspaikkaa",
        sendAndAssertSendCount
    )

    protected fun assertEmailIsSentOfCitizensWinterSpaceSwitch(
        emailAddress: String? = null,
        sendAndAssertSendCount: Boolean? = null
    ) = assertIndefiniteReservationEmail(
        emailAddress,
        "Vahvistus Espoon kaupungin talvipaikkavarauksen vaihdosta",
        "Olet vaihtanut Espoon kaupungilta vuokraamaasi talvipaikkaa",
        sendAndAssertSendCount
    )

    protected fun assertEmailIsSentOfCitizensIndefiniteTrailerSwitch(
        emailAddress: String? = null,
        sendAndAssertSendCount: Boolean? = null
    ) = assertIndefiniteReservationEmail(
        emailAddress,
        "Vahvistus Espoon kaupungin traileripaikkavarauksen vaihdosta",
        "Olet vaihtanut Espoon kaupungilta vuokraamaasi traileripaikkaa",
        sendAndAssertSendCount
    )

    protected fun assertEmailIsSentOfCitizensFixedTermTrailerSwitch(
        emailAddress: String? = null,
        endDate: String? = null,
        sendAndAssertSendCount: Boolean? = null
    ) = assertFixedTermReservationEmail(
        emailAddress,
        "Vahvistus Espoon kaupungin traileripaikkavarauksen vaihdosta",
        "Olet vaihtanut Espoon kaupungilta vuokraamaasi traileripaikkaa",
        endDate,
        sendAndAssertSendCount
    )

    private fun assertIndefiniteReservationEmail(
        emailAddress: String? = null,
        emailSubject: String,
        contentSnippet: String,
        sendAndAssertSendCount: Boolean? = null
    ) = assertOnlyOneConfirmationEmailIsSent(
        emailAddress,
        emailSubject,
        "Varauksesi on voimassa toistaiseksi ja kausimaksu maksetaan vuosittain.",
        contentSnippet,
        sendAndAssertSendCount
    )

    private fun assertFixedTermReservationEmail(
        emailAddress: String? = null,
        emailSubject: String,
        contentSnippet: String,
        endDate: String?,
        sendAndAssertSendCount: Boolean? = null
    ) = assertOnlyOneConfirmationEmailIsSent(
        emailAddress,
        emailSubject,
        "Varauksesi on voimassa ${endDate ?: "31.12.2024"} asti.",
        contentSnippet,
        sendAndAssertSendCount
    )

    private fun assertOnlyOneConfirmationEmailIsSent(
        emailAddress: String? = null,
        emailSubject: String = "foo",
        validity: String = "foo",
        contentSnippet: String,
        sendAndAssertSendCount: Boolean? = null,
    ) {
        val send = sendAndAssertSendCount ?: true
        val recipientAddress = emailAddress ?: "test@example.com"
        if (send) {
            messageService.sendScheduledEmails()
            assertEquals(1, SendEmailServiceMock.emails.size)
            val email = SendEmailServiceMock.emails[0]
            assertTrue(email.contains("$recipientAddress with subject $emailSubject"))
            assertTrue(email.contains(validity))
            assertTrue(email.contains(contentSnippet))
        } else {
            assertTrue(
                SendEmailServiceMock.emails.any {
                    val email = it.toString()
                    email.contains("$recipientAddress with subject $emailSubject") &&
                        email.contains(validity) &&
                        email.contains(contentSnippet)
                }
            )
        }
    }

    fun assertTerminationEmailIsSentToCitizenAndEmployee(
        placeType: String,
        terminatedSpace: String,
        terminator: String,
        reserverName: String,
        recipientAddresses: List<String> = listOf("test@example.com")
    ) {
        messageService.sendScheduledEmails()
        val expectedNroOfEmails = recipientAddresses.size + 1
        assertEquals(expectedNroOfEmails, SendEmailServiceMock.emails.size)

        val sortedEmails =
            SendEmailServiceMock.emails.sortedBy { email ->
                when {
                    email.contains("to venepaikat@espoo.fi") -> 1
                    else -> 0
                }
            }

        val citizenEmailSubject = "Vahvistus Espoon kaupungin venepaikan irtisanomisesta"

        recipientAddresses.forEachIndexed { i, recipientAddress ->
            val emailToCitizen = sortedEmails[i]
            assertTrue(
                emailToCitizen.contains("$recipientAddress with subject $citizenEmailSubject") &&
                    emailToCitizen.contains("$placeType $terminatedSpace on irtisanottu.") &&
                    emailToCitizen.contains("Irtisanoaja: $terminator") &&
                    emailToCitizen.contains("Paikan vuokraaja: $reserverName")
            )
        }

        val emailToEmployee = sortedEmails.last()
        val employeeEmailSubject = "$placeType $terminatedSpace irtisanottu, asiakas: $reserverName"

        assertTrue(
            emailToEmployee.contains("venepaikat@espoo.fi with subject $employeeEmailSubject") &&
                emailToEmployee.contains("$placeType $terminatedSpace on irtisanottu") &&
                emailToEmployee.contains("Paikan vuokraaja: $reserverName") &&
                emailToEmployee.contains("Irtisanoaja:\nNimi: $terminator")
        )
    }
}
