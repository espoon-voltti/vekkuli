package fi.espoo.vekkuli.service

import fi.espoo.vekkuli.common.getAppUser
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.inTransactionUnchecked
import org.springframework.stereotype.Service
import java.util.*

@Service
class UserService(
    private val jdbi: Jdbi,
) {
    fun isAppUser(userId: UUID): Boolean =
        userId.let {
            jdbi.inTransactionUnchecked { tx ->
                tx.getAppUser(userId) != null
            }
        }
}
