ALTER TABLE boat_space_reservation
ALTER COLUMN citizen_id DROP NOT NULL;

ALTER TABLE boat_space_reservation
ADD COLUMN employee_id uuid;
