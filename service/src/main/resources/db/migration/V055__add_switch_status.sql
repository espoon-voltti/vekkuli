/* Rename renewedFromId to originalReservationId*/
ALTER TABLE boat_space_reservation RENAME COLUMN renewed_from_id TO original_reservation_id;

/* Create enum for ReservationOrigin that can have two values; switch and renewal*/
CREATE TYPE reservation_creation_type AS ENUM ('Switch', 'Renewal', 'New');
/* Add column for checking whether reservation is switched or renewed with enum type*/
ALTER TABLE boat_space_reservation ADD COLUMN creation_type reservation_creation_type DEFAULT 'New';


ALTER TYPE ReservationStatus RENAME TO ReservationStatus_old;

CREATE TYPE ReservationStatus AS ENUM ('Info', 'Payment', 'Confirmed', 'Cancelled', 'Invoiced');

ALTER TABLE boat_space_reservation ALTER COLUMN status DROP DEFAULT;

UPDATE boat_space_reservation
SET creation_type='Renewal', status = 'Info'
WHERE status = 'Renewal';

ALTER TABLE boat_space_reservation
    ALTER COLUMN status SET DATA TYPE TEXT USING status::TEXT;

ALTER TABLE boat_space_reservation
    ALTER COLUMN status SET DATA TYPE ReservationStatus USING status::ReservationStatus;

ALTER TABLE boat_space_reservation ALTER COLUMN status SET DEFAULT 'Info';

DROP TYPE ReservationStatus_old;
