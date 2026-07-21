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

CREATE VIEW current_price AS
    SELECT 
        base.id             AS id,
        base.name           AS name,
        cur.price_cents     AS price_cents,
        cur.vat_cents       AS vat_cents,
        cur.net_price_cents AS net_price_cents
    FROM price base
    JOIN price cur ON cur.name = base.name
        AND CURRENT_DATE <@ daterange(cur.start_date, cur.end_date, '[]');