CREATE COLLATION fi_fi_icu (provider = icu, locale = 'fi-FI', deterministic = true);

ALTER TABLE reserver
    ALTER COLUMN name TYPE text COLLATE fi_fi_icu,
    ALTER COLUMN street_address TYPE text COLLATE fi_fi_icu,
    ALTER COLUMN post_office TYPE text COLLATE fi_fi_icu;

ALTER TABLE location
    ALTER COLUMN name TYPE text COLLATE fi_fi_icu;

ALTER TABLE municipality
    ALTER COLUMN name TYPE text COLLATE fi_fi_icu;

ALTER TABLE organization
    ALTER COLUMN billing_name TYPE text COLLATE fi_fi_icu,
    ALTER COLUMN billing_street_address TYPE text COLLATE fi_fi_icu,
    ALTER COLUMN billing_post_office TYPE text COLLATE fi_fi_icu;
