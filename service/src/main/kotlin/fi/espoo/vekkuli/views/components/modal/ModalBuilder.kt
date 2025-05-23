package fi.espoo.vekkuli.views.components.modal

import fi.espoo.vekkuli.controllers.Utils
import fi.espoo.vekkuli.utils.addTestId

class ModalBuilder {
    private val modalStateId = "isOpen"
    private var title: String? = null
    private var content: String? = null
    private var reloadPageOnClose: Boolean = false
    private var reloadPageAfterPost: Boolean = false
    private var closeModalOnPost: Boolean = false
    private var centerButtons: Boolean = false
    private var isWide: Boolean = false
    private val buttons: MutableList<ModalButtonParam> = mutableListOf()
    private var form: FormBuilder.BuildResult? = null

    fun getModalStateId(): String = modalStateId

    fun setTitle(title: String) =
        apply {
            this.title = title
        }

    fun setForm(init: FormBuilder.() -> Unit) =
        apply {
            val builder = FormBuilder()
            builder.init()
            form = builder.build()
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

    fun setReloadPageAfterPost(reloadPageOnPost: Boolean) = apply { this.reloadPageAfterPost = reloadPageOnPost }

    fun setCloseModalOnPost(closeModalOnPost: Boolean) = apply { this.closeModalOnPost = closeModalOnPost }

    fun build(): String {
        val reloadPageOnClose =
            if (reloadPageOnClose) {
                ";window.location.reload()"
            } else {
                ""
            }

        val closeModalAfterPostEventTrigger =
            if (closeModalOnPost) {
                """x-on:htmx:after-on-load="$modalStateId = false"""
            } else {
                ""
            }

        val reloadPageAfterPostEventTrigger =
            if (reloadPageAfterPost) {
                """x-on:htmx:after-on-load="window.location.reload()""""
            } else {
                ""
            }

        // language=HTML
        return """
            <div 
                id="modal"
                class="modal" 
                x-data="{ $modalStateId: true }" 
                x-show="$modalStateId" 
                $closeModalAfterPostEventTrigger
                $reloadPageAfterPostEventTrigger
                x-effect="
                    if (!$modalStateId) {
                        ${'$'}el.remove()
                        $reloadPageOnClose
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
                    ${form?.startTag ?: ""}
                    ${if (!title.isNullOrEmpty()) """<h3>$title</h3>""" else ""}
                    $content
                    ${if (buttons.isNotEmpty()) buildButtons(buttons) else ""}
                    ${form?.endTag ?: ""}
                </div>
            </div>
            """.trimIndent()
    }

    private fun buildButtons(buttons: List<ModalButtonParam>): String {
        // language=HTML
        return """<div class="buttons ${if (centerButtons) "is-justify-content-center" else ""}">${
            buttons.joinToString("\n") { button ->
                """
                <button
                    class="${button.style.cssClass}"
                    ${getButtonType(button.type)}
                    ${button.attributes.toHtmlAttributes()}
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

    class FormBuilder {
        class BuildResult(
            val startTag: String,
            val endTag: String
        )

        private var testId: String? = null
        private val attributes: MutableMap<String, String> =
            mutableMapOf(
                "hx-disabled-elt" to "find button[type='submit']",
                "hx-on:htmx:before-request" to "htmx.addClass(this.querySelector('button[type=submit]'), 'is-loading')",
                "hx-on:htmx:after-request" to "htmx.removeClass(this.querySelector('button[type=submit]'), 'is-loading')"
            )

        fun setId(id: String) =
            apply {
                attributes["id"] = id
            }

        fun setTestId(testId: String) =
            apply {
                this.testId = testId
            }

        fun setAttributes(attributes: Map<String, String>) =
            apply {
                this.attributes.putAll(attributes)
            }

        fun build(): BuildResult =
            BuildResult(
                """
                <form
                    ${attributes.toHtmlAttributes()}
                    ${testId?.let { addTestId(it) } ?: ""}
                    xmlns="http://www.w3.org/1999/html">
                """.trimIndent(),
                "</form>"
            )
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

fun Map<String, String>.toHtmlAttributes(): String =
    entries.joinToString(" ") { (key, value) ->
        "$key=\"$value\""
    }
