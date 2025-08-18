package fi.espoo.vekkuli.boatSpace.employeeReservationList.components

import fi.espoo.vekkuli.views.BaseView
import org.springframework.stereotype.Component

@Component
class AttachmentView : BaseView() {
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

    fun renderAttachmentListItem(
        key: String,
        name: String
    ): String {
        //language=HTML
        return """    
            <li class="attachment-view">
                <span>$name</span>
                <input hidden name="attachmentKey" value=$key />
                <span class="icon mr-s">${icons.cross}</span>
            </li>
            """.trimIndent()
    }
}
