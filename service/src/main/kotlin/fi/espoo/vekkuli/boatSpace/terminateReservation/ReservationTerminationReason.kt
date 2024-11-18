package fi.espoo.vekkuli.boatSpace.terminateReservation

enum class ReservationTerminationReason {
    UserRequest,
    InvalidOwner,
    RuleViolation,
    PaymentViolation,
    Other,
}

enum class ReservationTerminationReasonOptions {
    InvalidOwner,
    RuleViolation,
    PaymentViolation,
    Other
}
