package fi.espoo.vekkuli.views.components.modal

enum class ModalButtonType {
    Button,
    Cancel,
    Submit,
}

enum class OpenModalButtonType {
    Button,
    Link
}

data class ModalButtonParam(
    val text: String,
    val type: ModalButtonType = ModalButtonType.Button,
    val style: ModalButtonStyle = ModalButtonStyle.Default,
    val attributes: Map<String, String> = emptyMap()
)
