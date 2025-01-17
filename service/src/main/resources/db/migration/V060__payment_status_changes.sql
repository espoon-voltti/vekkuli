CREATE TYPE payment_type AS ENUM ('OnlinePayment', 'Invoice', 'Other');

ALTER TABLE payment ADD COLUMN payment_type payment_type;

UPDATE payment SET payment_type = 'Other';

ALTER TABLE payment ALTER COLUMN payment_type SET NOT NULL;

ALTER TYPE paymentstatus ADD VALUE 'Refunded';