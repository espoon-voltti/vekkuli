package fi.espoo.vekkuli.boatSpace.terminateReservation

enum class ReservationTerminationReason {
    UserRequest,
    InvalidOwner,
    RuleViolation,
    PaymentViolation,
    Switch,
    Other,
}

enum class ReservationTerminationReasonOptions {
    InvalidOwner,
    RuleViolation,
    PaymentViolation,
    Other
}
