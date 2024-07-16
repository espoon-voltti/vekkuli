CREATE TABLE harbor_restriction (
    location_id int NOT NULL,
    excluded_boat_type BoatType NOT NULL,
    CONSTRAINT fk_location_id FOREIGN KEY (location_id) REFERENCES location(id)
);
