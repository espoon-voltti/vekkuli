package fi.espoo.vekkuli.views.components.modal

enum class ModalButtonStyle(
    val cssClass: String
) {
    Default("button"),
    Primary("button is-primary"),
    Danger("button is-danger"),
    DangerOutline("button is-danger is-outlined")
}
