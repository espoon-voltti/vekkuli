CREATE INDEX idx_boat_space_reservation_reserver_id ON boat_space_reservation (reserver_id);

CREATE INDEX idx_payment_reservation_id ON payment (reservation_id);

CREATE INDEX idx_invoice_reservation_id ON invoice (reservation_id);

CREATE INDEX idx_reservation_warning_reservation_id ON reservation_warning (reservation_id);