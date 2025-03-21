package fi.espoo.vekkuli.views.components.modal

import fi.espoo.vekkuli.controllers.Utils
import org.springframework.stereotype.Service

@Service
class Modal {
    fun createModalBuilder(): ModalBuilder = ModalBuilder()

    fun createOpenModalBuilder(): OpenModalButtonBuilder = OpenModalButtonBuilder()

    class OpenModalButtonBuilder {
        var text: String = ""
        var path: String = ""
        var type: OpenModalButtonType = OpenModalButtonType.Button
        var style: ModalButtonStyle = ModalButtonStyle.Default
        val attributes: MutableMap<String, String> = mutableMapOf()

        fun setText(text: String) = apply { this.text = text }

        fun setPath(path: String) = apply { this.path = path }

        fun setType(type: OpenModalButtonType) = apply { this.type = type }

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

        fun build(): String {
            val additionalAttributes =
                attributes.entries.joinToString(" ") { (key, value) ->
                    "$key=\"$value\""
                }
            return when (type) {
                OpenModalButtonType.Button -> buildButton()
                OpenModalButtonType.Link -> buildLink()
            }
        }

        fun buildButton(): String {
            val additionalAttributes =
                attributes.entries.joinToString(" ") { (key, value) ->
                    "$key=\"$value\""
                }
            // language=HTML
            return """
                <button
                    class="${style.cssClass}"
                    hx-get="$path"
                    hx-target="#modal-container"
                    hx-swap="innerHTML"
                    $additionalAttributes>
                    $text
                </button>
                """.trimIndent()
        }

        fun buildLink(): String {
            val additionalAttributes =
                attributes.entries.joinToString(" ") { (key, value) ->
                    "$key=\"$value\""
                }

            // language=HTML
            return """
                <a
                    class="${style.cssClass}"
                    hx-get="$path"
                    hx-target="#modal-container"
                    hx-swap="innerHTML"
                    $additionalAttributes>
                    $text
                </a>
                """.trimIndent()
        }
    }
}
