ALTER TABLE citizen
    ADD COLUMN address text NOT NULL DEFAULT '',
    ADD COLUMN postal_code text NOT NULL DEFAULT '',
    ADD COLUMN municipality text NOT NULL DEFAULT '';

