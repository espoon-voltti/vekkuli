package fi.espoo.vekkuli.repository

import fi.espoo.vekkuli.domain.*
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.mapTo
import org.jdbi.v3.core.kotlin.withHandleUnchecked
import org.springframework.stereotype.Repository
import java.util.*

@Repository
class PriceRepository(
    private val jdbi: Jdbi
) {
    fun getPriceClasses(): List<Price> =
        jdbi.withHandleUnchecked { handle ->
            handle
                .createQuery(
                    """
                    SELECT id, name, price_cents, vat_cents, net_price_cents FROM price
                    """.trimIndent()
                ).mapTo<Price>()
                .list()
        }
}
