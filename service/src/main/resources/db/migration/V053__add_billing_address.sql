ALTER TABLE organization
    ADD COLUMN billing_name text NOT NULL DEFAULT '',
    ADD COLUMN billing_street_address text NOT NULL DEFAULT '',
    ADD COLUMN billing_postal_code text NOT NULL DEFAULT '',
    ADD COLUMN billing_post_office text NOT NULL DEFAULT '';
