CREATE TABLE harbor_restriction (
    location_id int NOT NULL,
    denied_type BoatType NOT NULL,
    CONSTRAINT fk_location_id FOREIGN KEY (location_id) REFERENCES location(id)
);
