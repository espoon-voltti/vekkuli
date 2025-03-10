ALTER TABLE reservation_warning
    ADD COLUMN invoice_number integer
        REFERENCES invoice(invoice_number)
            ON DELETE CASCADE;