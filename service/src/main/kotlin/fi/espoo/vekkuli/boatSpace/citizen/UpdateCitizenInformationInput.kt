package fi.espoo.vekkuli.boatSpace.citizen

import fi.espoo.vekkuli.repository.UpdateCitizenParams
import java.util.UUID

data class UpdateCitizenInformationInput(
    val email: String,
    val phone: String,
)

fun UpdateCitizenInformationInput.toCitizenUpdateInput(citizenId: UUID): UpdateCitizenParams =
    UpdateCitizenParams(
        id = citizenId,
        phone = phone,
        email = email,
    )
