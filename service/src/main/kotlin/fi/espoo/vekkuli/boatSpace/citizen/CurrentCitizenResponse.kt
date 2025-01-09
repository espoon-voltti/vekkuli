package fi.espoo.vekkuli.boatSpace.citizen

import fi.espoo.vekkuli.domain.CitizenWithDetails
import java.util.*

data class CurrentCitizenResponse(
    val loggedIn: Boolean,
    val user: User?,
    val apiVersion: String,
) {
    data class User(
        val details: UserDetails,
    )

    data class UserDetails(
        val id: UUID,
        val firstName: String,
        val lastName: String,
        val email: String,
        val phone: String,
        val municipalityName: String,
        val streetAddress: String,
        val streetAddressSv: String,
        val postOffice: String,
        val postOfficeSv: String,
        val postalCode: String,
        val birthday: String,
    )
}

fun CitizenWithDetails?.toCurrentCitizenResponse() =
    if (this == null) {
        CurrentCitizenResponse(
            loggedIn = false,
            user = null,
            apiVersion = "1.0",
        )
    } else {
        CurrentCitizenResponse(
            loggedIn = true,
            user =
                CurrentCitizenResponse.User(
                    details =
                        CurrentCitizenResponse.UserDetails(
                            id = id,
                            firstName = firstName,
                            lastName = lastName,
                            email = email,
                            phone = phone,
                            municipalityName = municipalityName,
                            streetAddress = streetAddress,
                            streetAddressSv = streetAddressSv,
                            postOffice = postOffice,
                            postOfficeSv = postOfficeSv,
                            postalCode = postalCode,
                            birthday = birthday,
                        )
                ),
            apiVersion = "1.0"
        )
    }
