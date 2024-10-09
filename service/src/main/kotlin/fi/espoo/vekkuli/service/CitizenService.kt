package fi.espoo.vekkuli.service

import fi.espoo.vekkuli.domain.*
import fi.espoo.vekkuli.repository.ReserverRepository
import fi.espoo.vekkuli.repository.SentMessageRepository
import fi.espoo.vekkuli.repository.UpdateCitizenParams
import org.springframework.stereotype.Service
import java.util.*

@Service
class CitizenService(
    private val reserverRepository: ReserverRepository,
    private val sentMessagesRepository: SentMessageRepository,
) {
    fun getCitizen(id: UUID): CitizenWithDetails? = reserverRepository.getCitizenById(id)

    fun getCitizens(nameSearch: String?): List<CitizenWithDetails> = reserverRepository.searchCitizens(nameSearch)

    fun updateCitizen(params: UpdateCitizenParams): CitizenWithDetails? {
        reserverRepository.updateCitizen(params)
        return reserverRepository.getCitizenById(params.id)
    }

    fun getCitizenBySsn(ssn: String): CitizenWithDetails? = reserverRepository.getCitizenByNationalId(ssn)

    fun insertCitizen(
        phone: String,
        email: String,
        nationalId: String,
        firstName: String,
        lastName: String,
        address: String,
        postalCode: String,
        municipalityCode: Int,
        dataProtection: Boolean,
    ): CitizenWithDetails =
        reserverRepository.insertCitizen(
            nationalId,
            firstName,
            lastName,
            phone,
            email,
            address,
            address,
            postalCode,
            "",
            "",
            municipalityCode,
            dataProtection,
        )

    fun getMessages(citizenId: UUID): List<SentMessage> = sentMessagesRepository.getMessagesSentToUser(citizenId)

    fun getMunicipalities(): List<Municipality> = reserverRepository.getMunicipalities()

    fun upsertCitizenUserFromAd(adUser: CitizenAdUser): CitizenWithDetails = reserverRepository.upsertCitizenUserFromAd(adUser)
}
