CREATE TABLE trailer_space
(
    id     Serial PRIMARY KEY,
    width  int NOT NULL,
    length int NOT NULL,
    registration_number text NOT NULL
);

ALTER TABLE boat_space_application
    ADD trailer_space_width int,
    ADD trailer_space_length int,
    ADD trailer_space_registration_code text;

ALTER TYPE BoatSpaceType ADD VALUE 'Trailer';

