DROP INDEX IF EXISTS unique_active_payment_per_reservation;

CREATE UNIQUE INDEX unique_active_payment_per_reservation
    ON payment (reservation_id)
    WHERE status NOT IN ('Failed', 'Refunded');

CREATE UNIQUE INDEX unique_created_reservation
    ON payment (reservation_id)
    WHERE status = 'Created';

CREATE OR REPLACE FUNCTION prevent_created_if_success_exists()
RETURNS TRIGGER AS $$
BEGIN
    -- Prevent inserting 'Created' if a 'Success' row already exists for the same reservation_id
    IF NEW.status = 'Created' AND EXISTS (
        SELECT 1 FROM payment WHERE reservation_id = NEW.reservation_id AND status = 'Success'
    ) THEN
        RAISE EXCEPTION 'Cannot insert "Created" when a "Success" payment exists for the same reservation_id';
END IF;
RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER check_created_status
    BEFORE INSERT ON payment
    FOR EACH ROW
    EXECUTE FUNCTION prevent_created_if_success_exists();
