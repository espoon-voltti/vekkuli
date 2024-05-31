CREATE EXTENSION IF NOT EXISTS "uuid-ossp" WITH SCHEMA public;

CREATE TABLE location (
    id Serial PRIMARY KEY,
    name text NOT NULL,
    address text NOT NULL
);

CREATE TABLE price (
    id Serial PRIMARY KEY,
    name text NOT NULL,
    price float NOT NULL
);

CREATE TABLE citizen (
    id Serial PRIMARY KEY,
    name text NOT NULL,
    phone text NOT NULL,
    email text NOT NULL
);

CREATE TABLE app_user (
   id uuid PRIMARY KEY DEFAULT uuid_generate_v1mc(),
   created timestamp with time zone NOT NULL DEFAULT now(),
   updated timestamp with time zone DEFAULT NULL,
   external_id text NOT NULL,
   first_name text NOT NULL,
   last_name text NOT NULL,
   email text,
   system_user bool DEFAULT FALSE
);
CREATE UNIQUE INDEX uniq$users$external_id ON app_user(external_id);

INSERT INTO app_user (id, external_id, first_name, last_name, email, system_user)
VALUES ('00000000-0000-0000-0000-000000000000', 'api-gw', 'api-gw', 'system-user', NULL, TRUE);

CREATE TYPE BoatSpaceType AS ENUM ('Storage', 'Slip');

CREATE TYPE BoatAmenity AS ENUM ('None', 'Buoy', 'RearBuoy', 'Beam', 'WalkBeam');

CREATE TYPE BoatType AS ENUM ('Rowboat', 'OutboardMotor', 'InboardMotor', 'Sailboat', 'JetSki');

CREATE TABLE boat_space (
    id Serial PRIMARY KEY,
    type BoatSpaceType NOT NULL,
    location_id int NOT NULL,
    price_id int NOT NULL,
    section text NOT NULL,
    place_number int NOT NULL,
    amenity BoatAmenity NOT NULL,
    width_cm int NOT NULL,
    length_cm int NOT NULL,
    description text NOT NULL,
    CONSTRAINT fk_location_id FOREIGN KEY (location_id) REFERENCES location(id),
    CONSTRAINT fk_price_id FOREIGN KEY (price_id) REFERENCES price(id)
);

CREATE TABLE boat_space_application (
    id Serial PRIMARY KEY,
    citizen_id int NOT NULL,
    created_at timestamp NOT NULL,
    type BoatSpaceType NOT NULL,
    boat_type BoatType NOT NULL,
    amenity BoatAmenity NOT NULL,
    boat_width_cm int NOT NULL,
    boat_length_cm int NOT NULL,
    boat_weight_kg int NOT NULL,
    boat_registration_code text NOT NULL,
    information text NOT NULL,
    CONSTRAINT fk_application_citizen_id FOREIGN KEY (citizen_id) REFERENCES citizen(id)
);

CREATE TABLE boat_space_application_location_wish (
    boat_space_application_id int NOT NULL,
    location_id int NOT NULL,
    priority int NOT NULL,
    CONSTRAINT fk_location_wish_location_id FOREIGN KEY (location_id) REFERENCES location(id),
    CONSTRAINT fk_location_wish_application_id FOREIGN KEY (boat_space_application_id) REFERENCES location(id)
);
