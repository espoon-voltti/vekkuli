package fi.espoo.vekkuli.boatSpace.citizenBoat

import fi.espoo.vekkuli.common.Unauthorized
import fi.espoo.vekkuli.service.*
import fi.espoo.vekkuli.service.BoatService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CitizenBoatService(
    private val boatService: BoatService,
    private val citizenAccessControl: CitizenAccessControl,
    private val permissionService: PermissionService
) {
    @Transactional
    fun updateBoat(
        boatId: Int,
        input: UpdateBoatInput
    ) {
        val (citizenId) = citizenAccessControl.requireCitizen()
        if (!permissionService.canEditBoat(citizenId, boatId)) throw Unauthorized()
        boatService.updateBoatAsCitizen(input.toBoatInput(boatId, citizenId))
    }
}
