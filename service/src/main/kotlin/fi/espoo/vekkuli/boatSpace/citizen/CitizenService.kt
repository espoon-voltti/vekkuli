package fi.espoo.vekkuli.boatSpace.citizen

import fi.espoo.vekkuli.service.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
open class CitizenService(
    private val citizenAccessControl: CitizenAccessControl,
    private val reserverService: ReserverService,
) {
    @Transactional
    open fun updateCitizen(citizen: UpdateCitizenInformationInput) {
        val (citizenId) = citizenAccessControl.requireCitizen()
        reserverService.updateCitizen(citizen.toCitizenUpdateInput(citizenId))
    }
}
