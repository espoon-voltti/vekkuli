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
    fun getMemos(reserverId: UUID): List<ReserverMemoWithDetails> = memoRepository.getMemos(reserverId, ReservationType.Marine)

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
        userId: UUID?,
        content: String
    ): ReserverMemoWithDetails? {
        val memo = memoRepository.insertMemo(reserverId, userId, content, ReservationType.Marine)
        return memoRepository.getMemo(memo.id)
    }

    fun insertSystemMemo(
        reserverId: UUID,
        content: String
    ): ReserverMemoWithDetails? = insertMemo(reserverId, null, content)

    fun removeMemo(id: Int): Unit = memoRepository.removeMemo(id)
}
