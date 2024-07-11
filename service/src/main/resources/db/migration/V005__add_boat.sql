CREATE TYPE OwnershipStatus AS ENUM ('Owner', 'User', 'CoOwner', 'FutureOwner', 'JetSki');

CREATE TABLE boat (
    id serial PRIMARY KEY,
    registration_code text,
    citizen_id uuid NOT NULL,
    name text,
    width_cm int NOT NULL,
    length_cm int NOT NULL,
    depth_cm int NOT NULL,
    weight_kg int NOT NULL,
    type BoatType NOT NULL,
    other_identification text,
    extra_information text,
    ownership OwnershipStatus NOT NULL,
    FOREIGN KEY (citizen_id) REFERENCES citizen(id)
  );

ALTER TABLE boat_space_reservation
    ADD COLUMN boat_id INT;

ALTER TABLE boat_space_reservation
    ADD CONSTRAINT fk_boat_id
        FOREIGN KEY (boat_id) REFERENCES boat(id);
