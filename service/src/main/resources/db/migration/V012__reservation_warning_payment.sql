ALTER TABLE reservation_warning
    ADD COLUMN invoice_number integer
        REFERENCES invoice(invoice_number)
            ON DELETE CASCADE,
    ADD COLUMN id UUID NOT NULL DEFAULT uuid_generate_v4();