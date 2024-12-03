CREATE SEQUENCE IF NOT EXISTS invoice_number_seq START WITH 100000;

ALTER TABLE invoice
    ADD COLUMN invoice_number INTEGER UNIQUE DEFAULT nextval('invoice_number_seq');

WITH numbered_invoices AS (
    SELECT id, ROW_NUMBER() OVER () + 99999 as new_number
    FROM invoice
    WHERE invoice_number IS NULL
)
UPDATE invoice i
SET invoice_number = ni.new_number
FROM numbered_invoices ni
WHERE i.id = ni.id;

ALTER TABLE invoice
    ALTER COLUMN invoice_number SET NOT NULL;
