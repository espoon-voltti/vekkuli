package fi.espoo.vekkuli.repository

import fi.espoo.vekkuli.domain.MemoCategory
import fi.espoo.vekkuli.domain.ReserverMemo
import fi.espoo.vekkuli.domain.ReserverMemoWithDetails
import java.util.*

interface MemoRepository {
    fun getMemo(id: Int): ReserverMemoWithDetails?

    fun getMemos(
        reserverId: UUID,
        category: MemoCategory
    ): List<ReserverMemoWithDetails>

    fun removeMemo(id: Int): Unit

    fun insertMemo(
        reserverId: UUID,
        userId: UUID,
        category: MemoCategory,
        content: String
    ): ReserverMemo

    fun updateMemo(
        id: Int,
        updatedBy: UUID,
        content: String
    ): ReserverMemo
}
