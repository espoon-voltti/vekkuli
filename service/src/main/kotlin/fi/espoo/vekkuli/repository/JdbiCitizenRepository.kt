package fi.espoo.vekkuli.repository

import fi.espoo.vekkuli.domain.*
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.mapTo
import org.jdbi.v3.core.kotlin.withHandleUnchecked
import org.springframework.stereotype.Repository
import java.util.*

@Repository
class JdbiCitizenRepository(
    private val jdbi: Jdbi
) : CitizenRepository {
    override fun getMunicipalities(): List<Municipality> =
        jdbi.withHandleUnchecked { handle ->
            handle
                .createQuery(
                    """
                    SELECT * FROM municipality
                    """.trimIndent()
                ).mapTo<Municipality>()
                .toList()
        }
}
