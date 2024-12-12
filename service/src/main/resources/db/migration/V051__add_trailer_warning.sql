ALTER TABLE reservation_warning DROP CONSTRAINT reservation_warning_pkey;
ALTER TABLE reservation_warning ALTER COLUMN boat_id DROP NOT NULL;
ALTER TABLE reservation_warning ADD COLUMN trailer_id INT NULL;
ALTER TABLE reservation_warning
    ADD CONSTRAINT reservation_warning_trailer_id_fkey FOREIGN KEY (trailer_id) REFERENCES trailer (id);

