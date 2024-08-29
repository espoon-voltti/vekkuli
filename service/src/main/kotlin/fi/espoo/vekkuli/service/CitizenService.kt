package fi.espoo.vekkuli.service

import fi.espoo.vekkuli.domain.Citizen
import org.springframework.stereotype.Service
import java.util.*

@Service
class CitizenService(
    private val citizenRepository: CitizenRepository
) {
    fun getCitizen(id: UUID): Citizen? = citizenRepository.getCitizen(id)
}
