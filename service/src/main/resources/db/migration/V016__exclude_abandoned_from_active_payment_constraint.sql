DROP INDEX IF EXISTS unique_active_payment_per_reservation;

CREATE UNIQUE INDEX unique_active_payment_per_reservation
    ON payment (reservation_id)
    WHERE status NOT IN ('Failed', 'Abandoned');
