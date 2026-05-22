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
          hx-include="#attachment-list input[name='attachmentId']"
          @htmx:after-request="
            const status = event.detail.xhr ? event.detail.xhr.status : 0;
            const sizeBox = document.getElementById('attachment-size-error');
            const genericBox = document.getElementById('error-box');
            sizeBox.hidden = true;
            sizeBox.textContent = '';
            if (event.detail.successful) {
              genericBox.hidden = true;
            } else if (status === 422) {
              genericBox.hidden = true;
              sizeBox.textContent = event.detail.xhr.responseText;
              sizeBox.hidden = false;
            } else {
              genericBox.hidden = false;
            }
            document.getElementById('attachment-input').value = null;
          "
          hx-indicator="#upload-indicator"
        >
          <input
            id="attachment-input"
            type="file"
            name="file"
            accept="image/png, image/jpeg, image/jpg, application/pdf"
          >
          <div id="attachment-size-error" hidden role="alert" class="is-centered is-vcentered is-error-text"></div>
          <div id="error-box" hidden role="alert" class="is-centered is-vcentered is-error-text">Liitteen lisäämisessä tapahtui virhe.</div>
        </form>
        <ul
          id="attachment-list"
          @htmx:after-request="
            if (event.detail.successful) {
              const sizeBox = document.getElementById('attachment-size-error');
              sizeBox.textContent = '';
              sizeBox.hidden = true;
            }
          "
        >
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

    //language=HTML
    fun renderSizeLimitError(
        currentBytes: Long,
        attemptedBytes: Long,
        limitBytes: Long,
    ): String {
        val combinedMb = formatMb(currentBytes + attemptedBytes)
        val limitMb = formatMb(limitBytes)
        return """
            Liitteiden yhteenlaskettu koko ylittäisi sallitun rajan ($combinedMb MB / $limitMb MB).
            Poista liite tai valitse pienempi tiedosto.
            """.trimIndent()
    }

    private fun formatMb(bytes: Long): String {
        val mb = bytes.toDouble() / 1_000_000.0
        return String.format(messageUtil.localeFI, "%.1f", mb)
    }
}
