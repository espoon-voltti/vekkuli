package fi.espoo.vekkuli.domain

import fi.espoo.vekkuli.config.MessageUtil
import fi.espoo.vekkuli.utils.cmToM
import java.util.*

data class Boat(
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
    val displayName: String? = null,
    val warnings: Set<String> = emptySet(),
) {
    fun updateBoatDisplayName(messageUtil: MessageUtil): Boat =
        this.copy(
            displayName =
                when {
                    !name.isNullOrBlank() -> name
                    !registrationCode.isNullOrBlank() -> registrationCode
                    else -> {
                        messageUtil.getMessage(
                            "boatApplication.boatTypeOption.$type"
                        ) + " " + widthCm.cmToM() + " x " + lengthCm.cmToM() + " m"
                    }
                }
        )

    fun hasWarning(warning: String): Boolean = warnings.contains(warning)

    fun hasAnyWarnings(): Boolean = warnings.isNotEmpty()
}
