DELETE FROM reservation_period;

INSERT INTO reservation_period(is_espoo_citizen, boat_space_type, operation, start_month, start_day, end_month, end_day)
VALUES
    -- For Espoo citizens
    (true, 'Slip', 'Renew', 1, 7, 1, 31),
    (true, 'Slip', 'Change', 1, 7, 9, 30),
    (true, 'Slip', 'New', 3, 3, 9, 30),
    (true, 'Slip', 'SecondNew', 4, 1, 9, 30),
    (true, 'Trailer', 'Renew', 4, 1, 4, 30),
    (true, 'Trailer', 'New', 5, 1, 12, 31),
    (true, 'Trailer', 'Change',  4, 1, 12, 31),
    (true, 'Winter', 'Renew',  8, 15, 9, 14),
    (true, 'Winter', 'Change',  8, 15, 12, 31),
    (true, 'Winter', 'New', 9, 15, 12, 31),
    (true, 'Winter', 'SecondNew', 9, 15, 12, 31),
    (true, 'Storage', 'Renew',  8, 15, 9, 14),
    (true, 'Storage', 'Change',  8, 15, 8, 14),
    (true, 'Storage', 'New', 9, 15, 9, 14),
    (true, 'Storage', 'SecondNew', 9, 15, 9, 14),

    -- For non-Espoo citizens
    (false, 'Slip', 'New', 4, 1, 9, 30),
    (false, 'Slip', 'Change', 4, 1, 9, 30),
    (false, 'Trailer', 'New', 5, 1, 12, 31),
    (false, 'Trailer', 'Change',  5, 1, 12, 31),
    (false, 'Storage', 'Renew',  8, 15, 9, 14),
    (false, 'Storage', 'Change',  8, 15, 8, 14),
    (false, 'Storage', 'New', 9, 15, 9, 14),
    (false, 'Storage', 'SecondNew', 9, 15, 9, 14);

