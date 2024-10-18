CREATE TYPE ReservationOperation AS ENUM ('New', 'Renew', 'Change');

ALTER TYPE BoatSpaceType ADD VALUE 'Winter';

CREATE TABLE reservation_period (
    id TEXT PRIMARY KEY,
    start_month SMALLINT NOT NULL DEFAULT 1,
    start_day SMALLINT NOT NULL DEFAULT 1,
    end_month SMALLINT NOT NULL DEFAULT 12,
    end_day SMALLINT NOT NULL DEFAULT 31
);

INSERT INTO reservation_period(id, start_month, start_day, end_month, end_day)
VALUES
    ('slip_espoo_change_renew',  1, 7, 1, 31),
    ('slip_espoo_change',  3, 1, 9, 30),
    ('slip_espoo_new',  3, 1, 9, 30),
    ('slip_other_new',   4, 1, 9, 30),
    ('slip_espoo_second',  4, 1, 9, 30),
    ('winter_espoo_renew',  8, 1, 8, 31),
    ('winter_espoo_change', 9, 1, 12, 31),
    ('winter_espoo_new',  9, 1, 12, 31),
    ('winter_other_new',  9, 15, 12, 31),
    ('winter_espoo_second',  9, 15, 12, 31),
    ('storage_espoo_change_renew',  8, 1, 8, 31),
    ('storage_change',  9, 1, 7, 31),
    ('storage_espoo_new',  9, 1, 7, 31),
    ('storage_fixed_term_renew',  9, 1, 9, 15),
    ('storage_other_new',  9, 15, 7, 31),
    ('trailer_renew',  4, 1, 4, 30),
    ('trailer_new',  5, 1, 12, 31);
