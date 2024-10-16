package fi.espoo.vekkuli.views.components.modal

class ModalBuilder {
    private val modalStateId = "isOpen"
    private var title: String? = null
    private var content: String? = null
    private val buttons: MutableList<ModalButtonParam> = mutableListOf()

    fun setTitle(title: String) =
        apply {
            this.title = title
        }

    fun setContent(content: String) =
        apply {
            this.content = content
        }

    fun addButton(init: ModalButtonBuilder.() -> Unit) =
        apply {
            val builder = ModalButtonBuilder()
            builder.init()
            buttons.add(builder.build())
        }

    fun build(): String {
        // language=HTML
        return """
            <div 
                class="modal" 
                x-data="{ $modalStateId: true }" 
                x-show="$modalStateId" 
                x-on:htmx:after-on-load="$modalStateId = false"
                x-effect="
                    if (!$modalStateId) {
                        ${'$'}el.remove()
                    }
                ">
                <div class="modal-underlay" @click="$modalStateId = false;"></div>
                <div class="modal-content">
                    ${if (!title.isNullOrEmpty()) """<h3>$title</h3>""" else ""}
                    $content
                    ${if (buttons.isNotEmpty()) buildButtons(buttons) else ""}
                </div>
            </div>
            """.trimIndent()
    }

    private fun buildButtons(buttons: List<ModalButtonParam>): String {
        // language=HTML
        return """
                                    <div class="buttons">
                                        ${
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
        }
                                    </div>
            """.trimIndent()
    }

    private fun getButtonType(type: ModalButtonType): String {
        return when (type) {
            ModalButtonType.Button -> "type=\"button\""
            ModalButtonType.Cancel -> "type=\"button\" x-on:click=\"$modalStateId = false\""
            ModalButtonType.Submit -> "type=\"submit\""
        }
    }

    class ModalButtonBuilder {
        var text: String = ""
        var type: ModalButtonType = ModalButtonType.Button
        var style: ModalButtonStyle = ModalButtonStyle.Default
        val attributes: MutableMap<String, String> = mutableMapOf()

        fun setTargetForm(formId: String) = apply { attributes["form"] = formId }

        fun setText(text: String) = apply { this.text = text }

        fun setType(type: ModalButtonType) = apply { this.type = type }

        fun setStyle(style: ModalButtonStyle) = apply { this.style = style }

        fun addAttribute(
            key: String,
            value: String
        ) = apply { attributes[key] = value }

        fun build(): ModalButtonParam {
            return ModalButtonParam(text, type, style, attributes)
        }
    }
}
