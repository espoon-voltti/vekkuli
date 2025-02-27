CREATE UNIQUE INDEX unique_active_payment_per_reservation
    ON payment (reservation_id)
    WHERE status <> 'Failed';
