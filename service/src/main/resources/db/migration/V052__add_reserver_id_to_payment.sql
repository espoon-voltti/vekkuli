ALTER TABLE payment ADD COLUMN reserver_id uuid NOT NULL DEFAULT uuid_nil();

UPDATE payment SET reserver_id = citizen_id;

ALTER TABLE payment ALTER COLUMN reserver_id DROP DEFAULT;

ALTER TABLE payment ADD CONSTRAINT payment_reserver_id_fkey
    FOREIGN KEY (reserver_id) REFERENCES reserver(id);

CREATE INDEX idx_payment_reserver_id ON payment(reserver_id);

ALTER TABLE payment DROP CONSTRAINT payment_citizen_id_fkey;

DROP INDEX idx_payment_citizen_id;

ALTER TABLE payment DROP COLUMN citizen_id;

-- First add the new column
ALTER TABLE invoice ADD COLUMN reserver_id uuid NOT NULL DEFAULT uuid_nil();

-- Copy the data from citizen_id to reserver_id
UPDATE invoice SET reserver_id = citizen_id;

-- Remove the DEFAULT after data is copied
ALTER TABLE invoice ALTER COLUMN reserver_id DROP DEFAULT;

-- Add foreign key constraint to reference the reserver table
ALTER TABLE invoice ADD CONSTRAINT invoice_reserver_id_fkey
    FOREIGN KEY (reserver_id) REFERENCES reserver(id);

-- Add index for the new column
CREATE INDEX idx_invoice_reserver_id ON invoice(reserver_id);

-- Drop the foreign key constraint for the old column
ALTER TABLE invoice DROP CONSTRAINT invoice_citizen_id_fkey;

-- Finally drop the citizen_id column
ALTER TABLE invoice DROP COLUMN citizen_id;
