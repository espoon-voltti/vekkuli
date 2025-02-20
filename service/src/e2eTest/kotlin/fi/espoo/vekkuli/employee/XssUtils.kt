package fi.espoo.vekkuli.employee

import com.microsoft.playwright.Page
import com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat
import fi.espoo.vekkuli.pages.employee.CitizenDetailsPage
import fi.espoo.vekkuli.pages.employee.OrganizationDetailsPage
import org.junit.jupiter.api.Assertions.assertFalse
import java.util.UUID

fun maliciousCode(value: String, extra: String? = null) = "\"><script>window.${value} = true;${if(extra != null) "var e = \"${extra}\";" else ""}</script>"

fun injectXSSToCitizenInformation(page: Page, citizenId: UUID): () -> Unit {
    val citizenDetails = CitizenDetailsPage(page)
    citizenDetails.navigateToUserPage(citizenId)

    val firstNameMaliciousValue = "XSS_ATTACK_FIRST_NAME"
    val lastNameMaliciousValue = "XSS_ATTACK_LAST_NAME"
    val addressMaliciousValue = "XSS_ATTACK_ADDRESS"
    val emailMaliciousValue = "XSS_ATTACK_EMAIL"
    val postalCodeMaliciousValue = "XSS_ATTACK_POSTAL_CODE"
    val cityMaliciousValue = "XSS_ATTACK_CITY"

    citizenDetails.editButton.click()

    citizenDetails.citizenFirstNameInput.fill(maliciousCode(firstNameMaliciousValue))
    citizenDetails.citizenLastNameInput.fill(maliciousCode(lastNameMaliciousValue))
    citizenDetails.citizenAddressInput.fill(maliciousCode(addressMaliciousValue))
    citizenDetails.citizenEmailInput.fill(maliciousCode(emailMaliciousValue, "e@e.e"))
    citizenDetails.citizenPostalCodeInput.fill(maliciousCode(postalCodeMaliciousValue))
    citizenDetails.citizenCityInput.fill(maliciousCode(cityMaliciousValue))

    citizenDetails.citizenEditSubmitButton.click()
    return {
        assertFalse(page.evaluate("() => window.hasOwnProperty('${firstNameMaliciousValue}')") as Boolean, "XSS script was executed on First name")
        assertFalse(page.evaluate("() => window.hasOwnProperty('${lastNameMaliciousValue}')") as Boolean, "XSS script was executed on Last name")
        assertFalse(page.evaluate("() => window.hasOwnProperty('${addressMaliciousValue}')") as Boolean, "XSS script was executed on Address")
        assertFalse(page.evaluate("() => window.hasOwnProperty('${emailMaliciousValue}')") as Boolean, "XSS script was executed on Email")
        assertFalse(page.evaluate("() => window.hasOwnProperty('${postalCodeMaliciousValue}')") as Boolean, "XSS script was executed on Postal code")
        assertFalse(page.evaluate("() => window.hasOwnProperty('${cityMaliciousValue}')") as Boolean, "XSS script was executed on Municipality code")
    }
}

fun injectXSSToOrganizationInformation(page: Page, organizationId: UUID): () -> Unit {
    val organizationDetails = OrganizationDetailsPage(page)

    val nameMaliciousValue = "XSS_ATTACK_NAME"
    val businessIdMaliciousValue = "XSS_ATTACK_LAST_NAME"
    val phoneNumberMaliciousValue = "XSS_ATTACK_PHONE_NUMBER"
    val emailMaliciousValue = "XSS_ATTACK_EMAIL"
    val addressMaliciousValue = "XSS_ATTACK_ADDRESS"
    val postalCodeMaliciousValue = "XSS_ATTACK_POSTAL_CODE"
    val cityMaliciousValue = "XSS_ATTACK_CITY"
    val billingNameMaliciousValue = "XSS_ATTACK_BILLING_NAME"
    val billingAddressMaliciousValue = "XSS_ATTACK_BILLING_ADDRESS"
    val billingPostalCodeMaliciousValue = "XSS_ATTACK_BILLING_POSTAL_CODE"
    val billingCityMaliciousValue = "XSS_ATTACK_BILLING_CITY"

    organizationDetails.navigateToPage(organizationId)

    assertThat(organizationDetails.organizationDetailsSection).isVisible()
    organizationDetails.editButton.click()

    assertThat(page.getByTestId("edit-organization-form")).isVisible()

    organizationDetails.organizationNameInput.fill(maliciousCode(nameMaliciousValue))
    organizationDetails.organizationBusinessIdInput.fill(maliciousCode(businessIdMaliciousValue))
    organizationDetails.organizationPhoneInput.fill(maliciousCode(phoneNumberMaliciousValue))
    organizationDetails.organizationEmailInput.fill(maliciousCode(emailMaliciousValue, "e@e.e"))
    organizationDetails.organizationAddressInput.fill(maliciousCode(addressMaliciousValue))
    organizationDetails.organizationPostalCodeInput.fill(maliciousCode(postalCodeMaliciousValue))
    organizationDetails.organizationPostOfficeInput.fill(maliciousCode(cityMaliciousValue))

    organizationDetails.organizationBillingNameInput.fill(maliciousCode(billingNameMaliciousValue))
    organizationDetails.organizationBillingAddressInput.fill(maliciousCode(billingAddressMaliciousValue))
    organizationDetails.organizationBillingPostalCodeInput.fill(maliciousCode(billingPostalCodeMaliciousValue))
    organizationDetails.organizationBillingPostOfficeInput.fill(maliciousCode(billingCityMaliciousValue))

    organizationDetails.organizationEditSubmitButton.click()

    return {
        assertFalse(page.evaluate("() => window.hasOwnProperty('${nameMaliciousValue}')") as Boolean, "XSS script was executed on First name")
        assertFalse(page.evaluate("() => window.hasOwnProperty('${businessIdMaliciousValue}')") as Boolean, "XSS script was executed on Last name")
        assertFalse(page.evaluate("() => window.hasOwnProperty('${phoneNumberMaliciousValue}')") as Boolean, "XSS script was executed on Phone number")
        assertFalse(page.evaluate("() => window.hasOwnProperty('${emailMaliciousValue}')") as Boolean, "XSS script was executed on Email")
        assertFalse(page.evaluate("() => window.hasOwnProperty('${addressMaliciousValue}')") as Boolean, "XSS script was executed on Address")
        assertFalse(page.evaluate("() => window.hasOwnProperty('${postalCodeMaliciousValue}')") as Boolean, "XSS script was executed on Postal code")
        assertFalse(page.evaluate("() => window.hasOwnProperty('${cityMaliciousValue}')") as Boolean, "XSS script was executed on Municipality code")
        assertFalse(page.evaluate("() => window.hasOwnProperty('${billingNameMaliciousValue}')") as Boolean, "XSS script was executed on Billing name")
        assertFalse(page.evaluate("() => window.hasOwnProperty('${billingAddressMaliciousValue}')") as Boolean, "XSS script was executed on Billing address")
        assertFalse(page.evaluate("() => window.hasOwnProperty('${billingPostalCodeMaliciousValue}')") as Boolean, "XSS script was executed on Billing postal code")
        assertFalse(page.evaluate("() => window.hasOwnProperty('${billingCityMaliciousValue}')") as Boolean, "XSS script was executed on Billing city")
    }
}
