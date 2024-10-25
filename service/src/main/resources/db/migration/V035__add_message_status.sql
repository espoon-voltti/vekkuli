ALTER TYPE MessageStatus ADD VALUE 'Processing';

CREATE TYPE ReservationType AS ENUM ('Marine', 'Spaces', 'GroupExercise' );
ALTER TABLE sent_message ADD COLUMN retry_count INT DEFAULT 0 NOT NULL;
CREATE TABLE processed_message (
    reservation_type ReservationType,
    reservation_id INT NOT NULL,
    -- message_type is the type of message that was sent -- e.g. 'renew', 'newReservation', 'cancelReservation'
    message_type VARCHAR(255) NOT NULL,
    recipient_email VARCHAR(255) NOT NULL,
    PRIMARY KEY (reservation_type, reservation_id, message_type, recipient_email)
);

