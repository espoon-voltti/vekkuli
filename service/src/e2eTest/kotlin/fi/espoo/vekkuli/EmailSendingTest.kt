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
        invoiceAddress: String,
        sendAndAssertSendCount: Boolean? = null
    ) = assertIndefiniteReservationEmail(
        emailAddress,
        "Espoon kaupungin laituripaikkavaraus",
        "Lasku lähetetään osoitteeseen $invoiceAddress",
        sendAndAssertSendCount
    )

    protected fun assertEmailIsSentOfEmployeesIndefiniteSlipReservationWithoutPayment(
        emailAddress: String? = null,
        sendAndAssertSendCount: Boolean? = null
    ) = assertIndefiniteReservationEmail(
        emailAddress,
        "Vahvistus Espoon kaupungin laituripaikkavarauksesta",
        "Sinulle on varattu Espoon kaupungin laituripaikka",
        sendAndAssertSendCount
    )

    protected fun assertEmailIsSentOfEmployeesFixedTermSlipReservationWithInvoice(
        emailAddress: String? = null,
        endDate: String? = null,
        sendAndAssertSendCount: Boolean? = null
    ) = assertFixedTermReservationEmail(
        emailAddress,
        "Espoon kaupungin laituripaikkavaraus",
        "Lasku lähetetään osoitteeseen",
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
        "Sinulle on varattu Espoon kaupungin laituripaikka",
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
        "Lasku lähetetään osoitteeseen",
        sendAndAssertSendCount
    )

    protected fun assertEmailIsSentOfEmployeesIndefiniteStorageSpaceReservationWithoutPayment(
        emailAddress: String? = null,
        sendAndAssertSendCount: Boolean? = null
    ) = assertIndefiniteReservationEmail(
        emailAddress,
        "Vahvistus Espoon kaupungin säilytyspaikkavarauksesta",
        "Sinulle on varattu Espoon kaupungin säilytyspaikka",
        sendAndAssertSendCount
    )

    protected fun assertEmailIsSentOfEmployeesFixedTermStorageReservationWithInvoice(
        emailAddress: String? = null,
        endDate: String? = null,
        sendAndAssertSendCount: Boolean? = null
    ) = assertFixedTermReservationEmail(
        emailAddress,
        "Espoon kaupungin säilytyspaikkavaraus",
        "Lasku lähetetään osoitteeseen",
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
        "Sinulle on varattu Espoon kaupungin säilytyspaikka",
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
        "Lasku lähetetään osoitteeseen",
        sendAndAssertSendCount
    )

    protected fun assertEmailIsSentOfEmployeeIndefiniteTrailerReservationWithoutPayment(
        emailAddress: String? = null,
        sendAndAssertSendCount: Boolean? = null
    ) = assertIndefiniteReservationEmail(
        emailAddress,
        "Vahvistus Espoon kaupungin traileripaikkavarauksesta",
        "Sinulle on varattu Espoon kaupungin traileripaikka",
        sendAndAssertSendCount
    )

    protected fun assertEmailIsSentOfEmployeeFixedTermTrailerReservationWithInvoice(
        emailAddress: String? = null,
        endDate: String? = null,
        sendAndAssertSendCount: Boolean? = null
    ) = assertFixedTermReservationEmail(
        emailAddress,
        "Espoon kaupungin traileripaikkavaraus",
        "Lasku lähetetään osoitteeseen",
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
        "Sinulle on varattu Espoon kaupungin traileripaikka",
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
        "Lasku lähetetään osoitteeseen",
        sendAndAssertSendCount
    )

    protected fun assertEmailIsSentOfEmployeeIndefiniteWinterSpaceReservationWithoutPayment(
        emailAddress: String? = null,
        sendAndAssertSendCount: Boolean? = null
    ) = assertIndefiniteReservationEmail(
        emailAddress,
        "Vahvistus Espoon kaupungin talvipaikkavarauksesta",
        "Sinulle on varattu Espoon kaupungin talvipaikka",
        sendAndAssertSendCount
    )

    protected fun assertEmailIsSentOfEmployeeFixedTermWinterSpaceReservationWithInvoice(
        emailAddress: String? = null,
        endDate: String? = null,
        sendAndAssertSendCount: Boolean? = null
    ) = assertFixedTermReservationEmail(
        emailAddress,
        "Espoon kaupungin talvipaikkavaraus",
        "Lasku lähetetään osoitteeseen",
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
        "Sinulle on varattu Espoon kaupungin talvipaikka",
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
        "Lasku lähetetään osoitteeseen",
        sendAndAssertSendCount
    )

    protected fun assertEmailIsSentOfEmployeeSlipRenewalWithoutPayment(
        emailAddress: String? = null,
        sendAndAssertSendCount: Boolean? = null
    ) = assertIndefiniteReservationEmail(
        emailAddress,
        "Espoon kaupungin laituripaikkavarauksen jatkaminen",
        "Varaamasi Espoon kaupungin laituripaikka on jatkettu tulevalle kaudelle.",
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
        "Lasku lähetetään osoitteeseen",
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
        "Lasku lähetetään osoitteeseen",
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
        "Lasku lähetetään osoitteeseen",
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
        emailSubject: String,
        validity: String,
        contentSnippet: String,
        sendAndAssertSendCount: Boolean? = null,
    ) {
        val send = sendAndAssertSendCount ?: true
        val recipientAddress = emailAddress ?: "test@example.com"
        if (send) {
            messageService.sendScheduledEmails()
            assertEquals(1, SendEmailServiceMock.emails.size)
            val email = SendEmailServiceMock.emails[0]
            assertEquals(email.recipientAddress, recipientAddress)
            assertEquals(email.subject, emailSubject)
            assertTrue(email.body.contains(validity))
            assertTrue(email.body.contains(contentSnippet))
        } else {
            assertTrue(
                SendEmailServiceMock.emails.any { email ->
                    email.recipientAddress == recipientAddress &&
                        email.subject == emailSubject &&
                        email.body.contains(validity) &&
                        email.body.contains(contentSnippet)
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
            SendEmailServiceMock.emails.sortedWith(
                compareBy(
                    { email -> if (email.recipientAddress == "venepaikat@espoo.fi") 1 else 0 },
                    { email -> recipientAddresses.indexOfFirst { email.recipientAddress == it } }
                )
            )

        val citizenEmailSubject = "Vahvistus Espoon kaupungin venepaikan irtisanomisesta"

        recipientAddresses.forEachIndexed { i, recipientAddress ->
            val emailToCitizen = sortedEmails[i]
            assertTrue(
                emailToCitizen.recipientAddress == recipientAddress &&
                    emailToCitizen.subject == citizenEmailSubject &&
                    emailToCitizen.body.contains("$placeType $terminatedSpace on irtisanottu.") &&
                    emailToCitizen.body.contains("Irtisanoja: $terminator") &&
                    emailToCitizen.body.contains("Paikan vuokraaja: $reserverName")
            )
        }

        val emailToEmployee = sortedEmails.last()
        val employeeEmailSubject = "Espoon kaupungin $placeType $terminatedSpace irtisanottu, asiakas: $reserverName"

        assertTrue(
            emailToEmployee.recipientAddress == "venepaikat@espoo.fi" &&
                emailToEmployee.subject == employeeEmailSubject &&
                emailToEmployee.body.contains("$placeType $terminatedSpace on irtisanottu") &&
                emailToEmployee.body.contains("Paikan vuokraaja: $reserverName") &&
                emailToEmployee.body.contains("Irtisanoja:\nNimi: $terminator")
        )
    }
}
