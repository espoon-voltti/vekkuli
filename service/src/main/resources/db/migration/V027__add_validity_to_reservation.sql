CREATE TYPE Validity AS ENUM('FixedTerm', 'Indefinite');

ALTER TABLE boat_space_reservation
  ADD COLUMN validity Validity NOT NULL DEFAULT 'FixedTerm';
