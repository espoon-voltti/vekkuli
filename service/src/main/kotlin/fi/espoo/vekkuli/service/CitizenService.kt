package fi.espoo.vekkuli.service

import fi.espoo.vekkuli.domain.Citizen
import fi.espoo.vekkuli.domain.CitizenMemo
import fi.espoo.vekkuli.domain.MemoCategory
import org.springframework.stereotype.Service
import java.util.*

interface CitizenRepository {
    fun getCitizen(id: UUID): Citizen?

    fun updateCitizen(
        id: UUID,
        phone: String,
        email: String,
    ): Citizen

    fun getMemos(
        citizenId: UUID,
        category: MemoCategory
    ): List<CitizenMemo>

    fun removeMemo(id: Int): Unit

    fun insertMemo(
        citizenId: UUID,
        userId: UUID,
        category: MemoCategory,
        content: String
    ): CitizenMemo

    fun updateMemo(
        id: Int,
        updatedBy: UUID,
        content: String
    ): CitizenMemo
}

@Service
class CitizenService(
    private val citizenRepository: CitizenRepository
) {
    fun getCitizen(id: UUID): Citizen? = citizenRepository.getCitizen(id)
}
