CREATE TABLE trailer (
    id Serial PRIMARY KEY,
    reserver_id uuid NOT NULL,
    registration_code text,
    width_cm INT NOT NULL,
    length_cm INT NOT NULL,

    FOREIGN KEY (reserver_id) REFERENCES reserver(id)
);

ALTER TABLE boat_space_reservation
    ADD COLUMN trailer_id INT;

ALTER TABLE boat_space_reservation
    ADD CONSTRAINT fk_trailer_id
        FOREIGN KEY (trailer_id) REFERENCES trailer(id);
