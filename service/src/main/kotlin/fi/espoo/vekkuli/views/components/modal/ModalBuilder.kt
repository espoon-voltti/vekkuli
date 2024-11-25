package fi.espoo.vekkuli.views.components.modal

import fi.espoo.vekkuli.controllers.Utils
import fi.espoo.vekkuli.utils.addTestId

class ModalBuilder {
    private val modalStateId = "isOpen"
    private var title: String? = null
    private var content: String? = null
    private var reloadPageOnClose: Boolean = false
    private var reloadPageOnPost: Boolean = false
    private var closeModalOnPost: Boolean = false
    private var centerButtons: Boolean = false
    private var isWide: Boolean = false
    private val buttons: MutableList<ModalButtonParam> = mutableListOf()

    fun getModalStateId(): String = modalStateId

    fun setTitle(title: String) =
        apply {
            this.title = title
        }

    fun setContent(content: String) =
        apply {
            this.content = content
        }

    fun setIsWide(isWide: Boolean) =
        apply {
            apply { this.isWide = isWide }
        }

    fun addButton(init: ModalButtonBuilder.() -> Unit) =
        apply {
            val builder = ModalButtonBuilder()
            builder.init()
            buttons.add(builder.build())
        }

    fun setReloadPageOnClose(reloadPageOnClose: Boolean) =
        apply {
            this.reloadPageOnClose = reloadPageOnClose
        }

    fun setButtonsCentered(isCentered: Boolean) = apply { this.centerButtons = isCentered }

    fun setReloadPageOnPost(reloadPageOnPost: Boolean) = apply { this.reloadPageOnPost = reloadPageOnPost }

    fun setCloseModalOnPost(closeModalOnPost: Boolean) = apply { this.closeModalOnPost = closeModalOnPost }

    fun build(): String {
        // language=HTML
        return """
            <div 
                class="modal" 
                x-data="{ $modalStateId: true }" 
                x-show="$modalStateId" 
                ${if (closeModalOnPost) "x-on:htmx:after-on-load=\"$modalStateId = false\"" else ""}
                x-effect="
                    if (!$modalStateId) {
                        ${'$'}el.remove()
                        ${if (reloadPageOnClose) "window.location.reload()" else ""}
                    }
                "
                ${addTestId("modal-window")}
                >
                <div 
                    ${addTestId("modal-underlay")} 
                    class="modal-underlay"
                    @click="$modalStateId = false;"
                ></div>
                <div class="modal-content${if (isWide) " is-wide" else ""}">
                    ${if (!title.isNullOrEmpty()) """<h3>$title</h3>""" else ""}
                    $content
                    ${if (buttons.isNotEmpty()) buildButtons(buttons) else ""}
                </div>
            </div>
            """.trimIndent()
    }

    private fun buildButtons(buttons: List<ModalButtonParam>): String {
        // language=HTML
        return """<div class="buttons ${if (centerButtons) "is-justify-content-center" else ""}">${
            buttons.joinToString("\n") { button ->
                val additionalAttributes =
                    button.attributes.entries.joinToString(" ") { (key, value) ->
                        "$key=\"$value\""
                    }
                """
                <button
                    class="${button.style.cssClass}"
                    ${getButtonType(button.type)}
                    $additionalAttributes
                >
                    ${button.text}
                </button>
                """.trimIndent()
            }
        }</div>""".trimIndent()
    }

    private fun getButtonType(type: ModalButtonType): String =
        when (type) {
            ModalButtonType.Button -> "type=\"button\""
            ModalButtonType.Cancel -> "type=\"button\" x-on:click=\"$modalStateId = false\""
            ModalButtonType.Submit -> "type=\"submit\""
        }

    class ModalButtonBuilder {
        var text: String = ""
        var type: ModalButtonType = ModalButtonType.Button
        var style: ModalButtonStyle = ModalButtonStyle.Default
        val attributes: MutableMap<String, String> = mutableMapOf()

        fun setTargetForm(formId: String) = apply { addAttribute("form", formId) }

        fun setText(text: String) = apply { this.text = text }

        fun setType(type: ModalButtonType) = apply { this.type = type }

        fun setStyle(style: ModalButtonStyle) = apply { this.style = style }

        fun setTestId(testId: String) =
            apply {
                if (!Utils.isStagingOrProduction()) {
                    addAttribute("data-testid", testId)
                }
            }

        fun addAttribute(
            key: String,
            value: String
        ) = apply { attributes[key] = value }

        fun build(): ModalButtonParam = ModalButtonParam(text, type, style, attributes)
    }
}
