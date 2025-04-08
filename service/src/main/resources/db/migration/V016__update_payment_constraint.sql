ALTER TABLE payment ADD COLUMN transaction_id text default null;

CREATE UNIQUE INDEX unique_created_reservation
    ON payment (reservation_id)
    WHERE status = 'Created';

CREATE OR REPLACE FUNCTION prevent_created_if_success_exists()
RETURNS TRIGGER AS $$
BEGIN
    -- Prevent inserting 'Created' if a conflicting row already exists for the same reservation_id
    IF NEW.status = 'Created' AND EXISTS (
        SELECT 1 FROM payment WHERE reservation_id = NEW.reservation_id AND status IN ('Success', 'Refunded')
    ) THEN
        RAISE EXCEPTION 'Cannot insert "Created" when a "Success" or "Refunded" payment exists for the same reservation_id';
END IF;
RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER check_created_status
    BEFORE INSERT ON payment
    FOR EACH ROW
    EXECUTE FUNCTION prevent_created_if_success_exists();
