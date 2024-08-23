ALTER TABLE price
    ALTER COLUMN price TYPE int USING price::int;

ALTER TABLE price
    RENAME COLUMN price TO price_cents;
