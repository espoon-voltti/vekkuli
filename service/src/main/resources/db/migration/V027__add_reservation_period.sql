CREATE TYPE ReservationOperation AS ENUM ('New', 'Renew', 'Change');

ALTER TYPE BoatSpaceType ADD VALUE 'Winter';

CREATE TABLE reservation_period (
    id TEXT PRIMARY KEY,
    start_month SMALLINT NOT NULL DEFAULT 1,
    start_day SMALLINT NOT NULL DEFAULT 1,
    end_month SMALLINT NOT NULL DEFAULT 12,
    end_day SMALLINT NOT NULL DEFAULT 31
);
