package fi.espoo.vekkuli.boatSpace.employeeReservationList.components

import fi.espoo.vekkuli.domain.Attachment
import fi.espoo.vekkuli.views.BaseView
import fi.espoo.vekkuli.views.employee.SanitizeInput
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class AttachmentView : BaseView() {
    //language=HTML
    fun render() =
        //language=HTML
        """
        <form
          hx-post="/virkailija/viestit/add-attachment"
          hx-encoding="multipart/form-data"
          hx-target="#attachment-list"  
          hx-swap="beforeend"          
          hx-trigger="input changed"
          @htmx:after-request="document.getElementById('attachment-input').value=null"  
        >
          <input
            id="attachment-input"
            type="file"
            name="file"
          >
        </form>
        <ul id="attachment-list"></ul>
        
        """.trimIndent()

    // language=HTML
    fun renderAttachmentList(attachments: List<Attachment>): String =
        """
            <ul id="attachment-list">
        ${attachments.joinToString(separator = "") { attachment ->
            renderAttachmentListItem(attachment.id, attachment.name)
        }}
            </ul>
        """.trimIndent()

    fun renderAttachmentListItemWithDelete(
        id: UUID,
        @SanitizeInput name: String
    ): String {
        //language=HTML
        val deleteSection = """<span class="icon" hx-post='/virkailija/delete-attachment/$id'>${icons.cross}</span>"""
        return renderAttachmentListItem(id, name, deleteSection)
    }

    fun renderAttachmentListItem(
        id: UUID,
        name: String,
        children: String = "",
    ): String {
        //language=HTML
        return """    
            <li class="attachment-view">
                <a href="/virkailija/attachments/$id/content" target="_blank" rel="noopener">
                    <span class="icon">${icons.file}</span>
                    <span>$name</span>
                </a>
                <input hidden name="attachmentId" value=$id />
                $children
            </li>
            """.trimIndent()
    }
}
