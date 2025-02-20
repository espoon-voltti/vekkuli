package fi.espoo.vekkuli.views.employee.components

import fi.espoo.vekkuli.domain.*
import fi.espoo.vekkuli.utils.*
import fi.espoo.vekkuli.views.BaseView
import fi.espoo.vekkuli.views.employee.SanitizeInput
import fi.espoo.vekkuli.views.employee.SubTab
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.util.*

@Component
class ReserverDetailsMemoContainer : BaseView() {
    @Autowired
    lateinit var reserverDetailsTabs: ReserverDetailsTabs

    fun formatDate(d: LocalDateTime): String = d.format(fullDateTimeFormat)

    fun memoContent(
        @SanitizeInput memo: ReserverMemoWithDetails,
        edit: Boolean,
    ): String {
        val createdBy =
            if (memo.createdBy !== null) {
                """
                <span class="memo-label">${memo.createdBy}</span>
                """
            } else {
                ""
            }

        val buttons =
            if (edit) {
                ""
            } else {
                """
                <a id="edit-memo-button"
                   hx-get="${reserverDetailsTabs.getTabUrl("${memo.reserverId}/muistiinpanot/muokkaa/${memo.id}")}"
                   hx-trigger="click"
                   hx-target="#memo-${memo.id}"
                   hx-swap="outerHTML">
                    <span class="icon ml-m">
                        ${icons.edit}
                    </span>
                </a>
                <a id="delete-memo-button"
                   hx-delete="${reserverDetailsTabs.getTabUrl("${memo.reserverId}/muistiinpanot/${memo.id}")}"
                   hx-trigger="click"
                   hx-target="#tab-content"
                   hx-swap="outerHTML"
                   hx-confirm="${t("citizenDetails.removeMemoConfirm")}">
                    <span class="icon ml-m">
                        ${icons.remove}
                    </span>
                </a>
                """.trimIndent()
            }

        val header =
            """
            <div>
                <span class="memo-label">${formatDate(memo.createdAt)}</span>
                $createdBy
                $buttons
            </div>
            """.trimIndent()

        val updated =
            if (memo.updatedBy !== null && memo.updatedAt !== null) {
                """
                <div class="memo-updated-by">
                    Muokattu
                    <span>${formatDate(memo.updatedAt)}</span>
                    <span>${memo.updatedBy}</span>
                </div>
                """.trimIndent()
            } else {
                ""
            }

        val content =
            if (edit) {
                // language=HTML
                """
                <form hx-patch="${reserverDetailsTabs.getTabUrl("${memo.reserverId}/muistiinpanot/${memo.id}")}"
                      hx-target="#memo-${memo.id}"
                      hx-swap="outerHTML">
                    <div class="control memo-edit-area">
                        <textarea id="edit-memo-content" 
                                  class="textarea" 
                                  rows="1" 
                                  class="memo-content-input" 
                                  name="content">${memo.content}</textarea>
                        <div class="memo-edit-buttons">
                            <button id="save-edit-button" type="submit">
                                <span class="icon ml-m" 
                                      style='stroke: green;'>
                                    ${icons.check}
                                </span>
                            </button>
                            <span id="cancel-edit-button" class="icon ml-m"
                                  hx-get="${reserverDetailsTabs.getTabUrl("${memo.reserverId}/muistiinpanot/${memo.id}")}"
                                  hx-trigger="click"
                                  hx-target="#memo-${memo.id}"
                                  hx-swap="outerHTML">
                                ${icons.xMark}
                            </span>
                        </div>
                    </div>
                </form>
                """.trimIndent()
            } else {
                // language=HTML
                """
                <div class="memo-content">
                    ${memo.content}
                </div>
                """
            }

        // language=HTML
        return """
            <div class="block memo" id="memo-${memo.id}">
               $header
               $updated
               $content
            </div>
            """.trimIndent()
    }

    fun newMemoContent(
        citizenId: UUID,
        edit: Boolean,
    ): String {
        if (edit) {
            // language=HTML
            return """
                <div id="new-memo" class="block">
                    <form hx-post="${reserverDetailsTabs.getTabUrl("$citizenId/muistiinpanot")}"
                        hx-target="#tab-content"
                        hx-swap="outerHTML">
                        <div class="memo-edit-area">
                            <div class="control">
                                <textarea id="new-memo-content" 
                                          class="textarea" 
                                          rows="1" 
                                          class="memo-content-input" 
                                          name="content"></textarea>
                            </div>
                            <div class="memo-edit-buttons">
                                <button id="new-memo-save-button" type="submit">
                                    <span class="icon ml-m" style='stroke: green;'>${icons.check}</span>
                                </button>
                                <a id="new-memo-cancel-button" 
                                   hx-get="${reserverDetailsTabs.getTabUrl("$citizenId/muistiinpanot/lisaa_peruuta")}" 
                                    hx-trigger="click" 
                                    hx-target="#new-memo" 
                                    hx-swap="outerHTML" >
                                    <span class="icon ml-m">${icons.xMark}</span>
                                </a>
                            </div> 
                        </div>
                    </form>
                </div>
                """.trimIndent()
        }
        // language=HTML
        return """
            <div id="new-memo" class="block">
                <a 
                    id="add-new-memo"
                    hx-get="${reserverDetailsTabs.getTabUrl("$citizenId/muistiinpanot/lisaa")}"
                    hx-trigger="click"
                    hx-target="#new-memo"
                    hx-swap="outerHTML">
                    <span class="icon mr-s">${icons.plus}</span>
                    <span>${t("citizenDetails.newMemo")}</span>   
                </a>
            </div>
            """.trimIndent()
    }

    fun tabContent(
        reserver: ReserverWithDetails,
        @SanitizeInput memos: List<ReserverMemoWithDetails>,
    ): String {
        val memoHtml =
            memos.joinToString("\n") {
                memoContent(it, false)
            }

        val result =
            // language=HTML
            """
            <div id="tab-content" class="container block">
                ${reserverDetailsTabs.renderTabNavi(reserver, SubTab.Memos)}
                ${newMemoContent(reserver.id, false)}
                $memoHtml
            <div>
            """.trimIndent()

        return result
    }
}
