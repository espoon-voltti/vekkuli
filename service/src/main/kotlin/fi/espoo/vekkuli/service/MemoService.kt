package fi.espoo.vekkuli.service

import fi.espoo.vekkuli.domain.ReservationType
import fi.espoo.vekkuli.domain.ReserverMemoWithDetails
import fi.espoo.vekkuli.repository.MemoRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class MemoService(
    private val memoRepository: MemoRepository
) {
    fun getMemos(
        reserverId: UUID,
        category: ReservationType
    ): List<ReserverMemoWithDetails> = memoRepository.getMemos(reserverId, category)

    fun getMemo(id: Int): ReserverMemoWithDetails? = memoRepository.getMemo(id)

    fun updateMemo(
        id: Int,
        updatedBy: UUID,
        content: String
    ): ReserverMemoWithDetails? {
        memoRepository.updateMemo(id, updatedBy, content)
        return memoRepository.getMemo(id)
    }

    fun insertMemo(
        reserverId: UUID,
        userId: UUID,
        category: ReservationType,
        content: String
    ): ReserverMemoWithDetails? {
        val memo = memoRepository.insertMemo(reserverId, userId, category, content)
        return memoRepository.getMemo(memo.id)
    }

    fun removeMemo(id: Int): Unit = memoRepository.removeMemo(id)
}
