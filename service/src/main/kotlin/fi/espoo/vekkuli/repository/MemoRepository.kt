package fi.espoo.vekkuli.repository

import fi.espoo.vekkuli.domain.ReservationType
import fi.espoo.vekkuli.domain.ReserverMemo
import fi.espoo.vekkuli.domain.ReserverMemoWithDetails
import java.util.*

interface MemoRepository {
    fun getMemo(id: Int): ReserverMemoWithDetails?

    fun getMemos(
        reserverId: UUID,
        category: ReservationType
    ): List<ReserverMemoWithDetails>

    fun removeMemo(id: Int): Unit

    fun insertMemo(
        reserverId: UUID,
        userId: UUID?,
        content: String,
        category: ReservationType
    ): ReserverMemo

    fun updateMemo(
        id: Int,
        updatedBy: UUID,
        content: String
    ): ReserverMemo
}
