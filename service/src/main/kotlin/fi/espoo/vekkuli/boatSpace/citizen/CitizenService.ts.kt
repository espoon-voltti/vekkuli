package fi.espoo.vekkuli.boatSpace.citizen

import fi.espoo.vekkuli.repository.BoatRepository
import fi.espoo.vekkuli.service.BoatReservationService
import fi.espoo.vekkuli.service.CitizenAccessControl
import fi.espoo.vekkuli.service.ReserverService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
open class CitizenService(
    private val boatRepository: BoatRepository,
    private val citizenAccessControl: CitizenAccessControl,
    private val boatReservationService: BoatReservationService,
    private val reserverService: ReserverService,
) {
    @Transactional
    open fun updateTrailer(trailer: UpdateTrailerInformationInput) {
        val (citizenId) = citizenAccessControl.requireCitizen()
        boatReservationService.updateTrailer(
            citizenId,
            trailer.id,
            trailer.registrationNumber,
            trailer.width,
            trailer.length
        )
    }

    @Transactional
    open fun updateBoat(boat: UpdateBoatInformationInput) {
        val (citizenId) = citizenAccessControl.requireCitizen()
        boatRepository.updateBoat(boat.toBoatInput(citizenId))
    }

    @Transactional
    open fun updateCitizen(citizen: UpdateCitizenInformationInput) {
        val (citizenId) = citizenAccessControl.requireCitizen()
        reserverService.updateCitizen(citizen.toCitizenUpdateInput(citizenId))
    }
}
