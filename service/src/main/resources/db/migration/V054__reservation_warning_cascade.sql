ALTER TABLE reservation_warning DROP CONSTRAINT reservation_warning_reservation_id_fkey;
ALTER TABLE reservation_warning DROP CONSTRAINT reservation_warning_trailer_id_fkey;
ALTER TABLE reservation_warning
    ADD CONSTRAINT reservation_warning_reservation_id_fkey
        FOREIGN KEY (reservation_id)
            REFERENCES boat_space_reservation (id) ON DELETE CASCADE;
ALTER TABLE reservation_warning
    ADD CONSTRAINT reservation_warning_trailer_id_fkey
        FOREIGN KEY (trailer_id)
            REFERENCES trailer (id) ON DELETE CASCADE;
