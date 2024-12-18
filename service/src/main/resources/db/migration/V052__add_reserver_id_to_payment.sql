-- Replace the citizen_id column with the reserver_id column in the payment and invoice tables.

ALTER TABLE payment ADD COLUMN reserver_id uuid NOT NULL DEFAULT uuid_nil();

UPDATE payment SET reserver_id = citizen_id;

ALTER TABLE payment ALTER COLUMN reserver_id DROP DEFAULT;

ALTER TABLE payment ADD CONSTRAINT payment_reserver_id_fkey
    FOREIGN KEY (reserver_id) REFERENCES reserver(id);

CREATE INDEX idx_payment_reserver_id ON payment(reserver_id);

ALTER TABLE payment DROP CONSTRAINT payment_citizen_id_fkey;

DROP INDEX idx_payment_citizen_id;

ALTER TABLE payment DROP COLUMN citizen_id;

ALTER TABLE invoice ADD COLUMN reserver_id uuid NOT NULL DEFAULT uuid_nil();

UPDATE invoice SET reserver_id = citizen_id;

ALTER TABLE invoice ALTER COLUMN reserver_id DROP DEFAULT;

ALTER TABLE invoice ADD CONSTRAINT invoice_reserver_id_fkey
    FOREIGN KEY (reserver_id) REFERENCES reserver(id);

CREATE INDEX idx_invoice_reserver_id ON invoice(reserver_id);

ALTER TABLE invoice DROP CONSTRAINT invoice_citizen_id_fkey;

ALTER TABLE invoice DROP COLUMN citizen_id;
