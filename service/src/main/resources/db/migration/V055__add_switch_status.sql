/* Rename renewedFromId to originalReservationId*/
ALTER TABLE boat_space_reservation RENAME COLUMN renewed_from_id TO original_reservation_id;

/* Create enum for ReservationOrigin that can have two values; switch and renewal*/
CREATE TYPE reservation_creation_type AS ENUM ('Switch', 'Renewal', 'New');
/* Add column for checking whether reservation is switched or renewed with enum type*/
ALTER TABLE boat_space_reservation ADD COLUMN creation_type reservation_creation_type DEFAULT 'New';
