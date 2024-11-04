ALTER TABLE boat_space_reservation
    ADD COLUMN renewed_from_id INT REFERENCES boat_space_reservation(id);
