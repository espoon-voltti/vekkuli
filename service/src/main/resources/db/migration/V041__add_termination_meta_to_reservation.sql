CREATE TYPE ReservationTerminationReason AS ENUM ('UserRequest', 'InvalidOwner', 'RuleViolation', 'PaymentViolation', 'Other');

ALTER TABLE boat_space_reservation
    ADD COLUMN termination_reason ReservationTerminationReason,
    ADD COLUMN termination_comment text;
