DELETE FROM reservation_period;

ALTER TABLE reservation_period
    DROP COLUMN id,
    ADD COLUMN is_espoo_citizen BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN operation ReservationOperation NOT NULL DEFAULT 'New',
    ADD COLUMN boat_space_type BoatSpaceType NOT NULL DEFAULT 'Slip';

INSERT INTO reservation_period(is_espoo_citizen, boat_space_type, operation, start_month, start_day, end_month, end_day)
VALUES
    -- For Espoo citizens
    (true, 'Slip', 'New', 3, 1, 9, 30),
    (true, 'Slip', 'Renew', 1, 7, 1, 31),
    (true, 'Slip', 'Change', 1, 7, 1, 31),
    (true, 'Slip', 'Change', 3, 1, 9, 30),
    (true, 'Trailer', 'New', 5, 1, 12, 31),
    (true, 'Trailer', 'Renew', 4, 1, 4, 30),
    (true, 'Trailer', 'Change',  4, 1, 12, 31),
    (true, 'Winter', 'New', 9, 1, 12, 31),
    (true, 'Winter', 'Renew',  8, 1, 8, 31),
    (true, 'Winter', 'Change',  8, 1, 12, 31),
    (true, 'Storage', 'New', 9, 1, 31, 7),
    (true, 'Storage', 'Renew',  8, 1, 8, 31),
    (true, 'Storage', 'Change',  8, 1, 7, 31),

    -- For non-Espoo citizens
    (false, 'Slip', 'New', 4, 1, 9, 30),
    (false, 'Slip', 'Change', 4, 1, 9, 30),
    (false, 'Trailer', 'New', 5, 1, 12, 31),
    (false, 'Trailer', 'Change',  5, 1, 12, 31),
    (false, 'Winter', 'New', 9, 15, 12, 31),
    (false, 'Winter', 'Change',  9, 15, 12, 31),
    (false, 'Storage', 'New', 9, 15, 7, 31),
    (false, 'Storage', 'Renew', 9, 1, 9, 15),
    (false, 'Storage', 'Change',  9, 1, 7, 31);
