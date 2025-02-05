ALTER TABLE boat_space_reservation
    ADD COLUMN start_datetime TIMESTAMP WITHOUT TIME ZONE DEFAULT now(),
    ADD COLUMN end_datetime TIMESTAMP WITHOUT TIME ZONE DEFAULT now();

UPDATE boat_space_reservation
SET start_datetime = start_date::TIMESTAMP,
    end_datetime = end_date::timestamp + interval '1 day' - interval '1 second';

ALTER TABLE boat_space_reservation DROP COLUMN start_date;
ALTER TABLE boat_space_reservation DROP COLUMN end_date;

ALTER TABLE boat_space_reservation RENAME COLUMN start_datetime TO start_date;
ALTER TABLE boat_space_reservation RENAME COLUMN end_datetime TO end_date;

-- If there are rows where end_date is before start_date, set end_date to start_date
UPDATE boat_space_reservation
    SET end_date = start_date::timestamp + interval '1 day' - interval '1 second'
    WHERE end_date < start_date;

CREATE EXTENSION IF NOT EXISTS btree_gist;

ALTER TABLE boat_space_reservation
    ADD CONSTRAINT check$start_before_end
        CHECK (end_date >= start_date),

    ADD CONSTRAINT check$no_overlapping_confirmed_reservations
        EXCLUDE USING GIST (
        boat_space_id WITH =,  -- Ensures uniqueness per boat space
        tsrange(start_date, end_date, '[)') WITH &&
        )
        WHERE (status = 'Confirmed');
