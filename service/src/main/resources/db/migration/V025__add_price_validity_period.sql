-- Add a validity period to each price row and prevent two rows with the same
-- name from claiming overlapping periods. end_date NULL = valid to infinity.

ALTER TABLE price ADD COLUMN start_date date;
UPDATE price SET start_date = DATE '2020-01-01' WHERE start_date IS NULL;
ALTER TABLE price ALTER COLUMN start_date SET NOT NULL;
ALTER TABLE price ALTER COLUMN start_date SET DEFAULT CURRENT_DATE;

ALTER TABLE price ADD COLUMN end_date date;

CREATE EXTENSION IF NOT EXISTS btree_gist;  -- already enabled by V007; idempotent

ALTER TABLE price
    ADD CONSTRAINT price_no_overlapping_validity
    EXCLUDE USING GIST (
        name WITH =,
        daterange(start_date, end_date, '[]') WITH &&
    );
