package fi.espoo.vekkuli.boatSpace.employeeReservationList.components

import fi.espoo.vekkuli.domain.Attachment
import fi.espoo.vekkuli.utils.addTestId
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
          hx-post="/virkailija/viestit/lisaa-liite"
          hx-encoding="multipart/form-data"
          hx-target="#attachment-list"  
          hx-swap="beforeend"          
          hx-trigger="change from:#attachment-input"
          @htmx:after-request="if (event.detail.successful) {
              document.getElementById('error-box').hidden = true;
          } else {
            document.getElementById('error-box').hidden = false;
          };
            document.getElementById('attachment-input').value=null;"  
          hx-indicator="#upload-indicator"
        >
          <input
            id="attachment-input"
            type="file"
            name="file"
            accept="image/png, image/jpeg, image/jpg, application/pdf"
          >
         <div id="error-box" hidden class="is-centered is-vcentered is-error-text">Liitteen lisäämisessä tapahtui virhe.</div>
          
        </form>
        <ul id="attachment-list">
         <div id="upload-indicator" class="htmx-indicator is-centered is-vcentered"> ${icons.spinner} </div>
        </ul>
        
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
        name: String
    ): String {
        //language=HTML
        val deleteSection = """<a ${addTestId(
            "delete-attachment-$name"
        )} class="icon" hx-delete='/virkailija/viestit/poista-liite/$id' hx-target="closest li"
      hx-swap="outerHTML">${icons.cross}</a>"""
        return renderAttachmentListItem(id, name, deleteSection)
    }

    fun renderAttachmentListItem(
        id: UUID,
        @SanitizeInput name: String,
        children: String = "",
    ): String {
        //language=HTML
        return """    
            <li class="attachment-view">
                <a href="/virkailija/viestit/liite/$id" target="_blank" rel="noopener">
                    <span class="icon">${icons.file}</span>
                    <span>$name</span>
                </a>
                <input hidden name="attachmentId" value=$id />
                $children
            </li>
            """.trimIndent()
    }
}
