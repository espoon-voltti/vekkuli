package fi.espoo.vekkuli.repository

import fi.espoo.vekkuli.domain.Citizen
import fi.espoo.vekkuli.service.CitizenRepository
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.mapTo
import org.jdbi.v3.core.kotlin.withHandleUnchecked
import org.springframework.stereotype.Repository
import java.time.LocalDate
import java.util.*

@Repository
class JdbiCitizenRepository(
    private val jdbi: Jdbi
) : CitizenRepository {
    override fun getCitizen(id: UUID): Citizen? =
        jdbi.withHandleUnchecked { handle ->
            val query =
                handle.createQuery(
                    """
                    SELECT * FROM citizen WHERE id = :id
                    """.trimIndent()
                )
            query.bind("id", id)
            val citizens = query.mapTo<Citizen>().toList()
            if (citizens.isEmpty()) null else citizens[0]
        }

    override fun updateCitizen(
        id: UUID,
        phone: String,
        email: String,
    ): Citizen =
        jdbi.withHandleUnchecked { handle ->
            val query =
                handle.createQuery(
                    """
                    UPDATE citizen
                    SET phone = :phone, email = :email, updated = :updated
                    WHERE id = :id
                    RETURNING *
                    """.trimIndent()
                )
            query.bind("id", id)
            query.bind("phone", phone)
            query.bind("email", email)
            query.bind("updated", LocalDate.now())
            query.mapTo<Citizen>().one()
        }
}
