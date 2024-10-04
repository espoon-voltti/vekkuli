ALTER TABLE boat_space_reservation
    ADD COLUMN acting_citizen_id UUID DEFAULT NULL,
    ADD FOREIGN KEY (acting_citizen_id) REFERENCES citizen(id);
