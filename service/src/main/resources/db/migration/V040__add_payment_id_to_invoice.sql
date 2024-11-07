ALTER TABLE invoice ADD COLUMN payment_id UUID REFERENCES payment(id);
ALTER TABLE invoice DROP COLUMN payment_date;
ALTER TABLE payment ADD COLUMN paid timestamp;
