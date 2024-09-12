package fi.espoo.vekkuli.service

import fi.espoo.vekkuli.domain.*
import org.springframework.stereotype.Service
import java.util.*

interface CitizenRepository {
    fun getCitizen(id: UUID): Citizen?

    fun updateCitizen(
        id: UUID,
        phone: String,
        email: String,
    ): Citizen

    fun getMemo(id: Int): CitizenMemoWithDetails?

    fun getMemos(
        citizenId: UUID,
        category: MemoCategory
    ): List<CitizenMemoWithDetails>

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
    private val citizenRepository: CitizenRepository,
    private val sentMessagesRepository: SentMessageRepository,
) {
    fun getCitizen(id: UUID): Citizen? = citizenRepository.getCitizen(id)

    fun getMessages(citizenId: UUID): List<SentMessage> = sentMessagesRepository.getMessagesSentToUser(citizenId)

    fun getMemos(
        citizenId: UUID,
        category: MemoCategory
    ): List<CitizenMemoWithDetails> = citizenRepository.getMemos(citizenId, category)

    fun getMemo(id: Int): CitizenMemoWithDetails? = citizenRepository.getMemo(id)

    fun updateMemo(
        id: Int,
        updatedBy: UUID,
        content: String
    ): CitizenMemoWithDetails? {
        citizenRepository.updateMemo(id, updatedBy, content)
        return citizenRepository.getMemo(id)
    }

    fun insertMemo(
        citizenId: UUID,
        userId: UUID,
        category: MemoCategory,
        content: String
    ): CitizenMemoWithDetails? {
        val memo = citizenRepository.insertMemo(citizenId, userId, category, content)
        return citizenRepository.getMemo(memo.id)
    }

    fun removeMemo(id: Int): Unit = citizenRepository.removeMemo(id)
}
