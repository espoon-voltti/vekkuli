ALTER TABLE booking_period
    ADD COLUMN id uuid DEFAULT uuid_generate_v1mc();

ALTER TABLE booking_period
    ADD CONSTRAINT booking_period_pkey PRIMARY KEY (id);