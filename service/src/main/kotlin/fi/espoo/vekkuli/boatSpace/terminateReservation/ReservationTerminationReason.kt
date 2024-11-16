package fi.espoo.vekkuli.boatSpace.terminateReservation

enum class ReservationTerminationReason(
    val translationKey: String
) {
    UserRequest("boatSpaceReservation.terminateReason.userRequest"),
    InvalidOwner("boatSpaceReservation.terminateReason.invalidOwner"),
    RuleViolation("boatSpaceReservation.terminateReason.ruleViolation"),
    PaymentViolation("boatSpaceReservation.terminateReason.paymentViolation"),
    Other("boatSpaceReservation.terminateReason.other")
}

enum class ReservationTerminationReasonOptions(
    val translationKey: String
) {
    InvalidOwner("boatSpaceReservation.terminateReason.invalidOwner"),
    RuleViolation("boatSpaceReservation.terminateReason.ruleViolation"),
    PaymentViolation("boatSpaceReservation.terminateReason.paymentViolation"),
    Other("boatSpaceReservation.terminateReason.other")
}
