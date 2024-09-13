package fi.espoo.vekkuli.repository

import fi.espoo.vekkuli.service.EmailTemplate
import fi.espoo.vekkuli.service.EmailTemplateRepository
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.mapTo
import org.jdbi.v3.core.kotlin.withHandleUnchecked
import org.springframework.stereotype.Repository

@Repository
class JdbiEmailTemplateRepository(
    private val jdbi: Jdbi
) : EmailTemplateRepository {
    override fun getTemplate(templateId: String): EmailTemplate? =
        jdbi.withHandleUnchecked { handle ->
            handle
                .createQuery("SELECT * FROM email_template WHERE id = :id")
                .bind("id", templateId)
                .mapTo<EmailTemplate>()
                .findFirst()
                .orElse(null)
        }
}
