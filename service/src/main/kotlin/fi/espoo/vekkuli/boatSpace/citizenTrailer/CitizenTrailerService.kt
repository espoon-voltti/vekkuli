package fi.espoo.vekkuli.boatSpace.citizenTrailer

import fi.espoo.vekkuli.common.Unauthorized
import fi.espoo.vekkuli.domain.StorageType
import fi.espoo.vekkuli.service.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

data class UpdateStorageTypeInput(
    val storageType: StorageType,
    val trailer: UpdateTrailerInput? = null
)

@Service
class CitizenTrailerService(
    private val citizenAccessControl: CitizenAccessControl,
    private val boatReservationService: BoatReservationService,
    private val permissionService: PermissionService
) {
    @Transactional
    fun updateTrailer(
        trailerId: Int,
        input: UpdateTrailerInput
    ) {
        val (citizenId) = citizenAccessControl.requireCitizen()
        if (!permissionService.canEditTrailer(citizenId, trailerId)) throw Unauthorized()
        boatReservationService.updateTrailer(
            citizenId,
            trailerId,
            input.registrationNumber,
            input.width,
            input.length
        )
    }
}
