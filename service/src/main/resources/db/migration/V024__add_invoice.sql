CREATE TABLE invoice (
    id uuid PRIMARY KEY DEFAULT uuid_generate_v1mc(),
    due_date DATE NOT NULL,
    reference TEXT NOT NULL,
    payment_date DATE,
    reservation_id INT NOT NULL,
    citizen_id uuid NOT NULL,

    FOREIGN KEY (citizen_id) REFERENCES citizen(id),
    FOREIGN KEY (reservation_id) REFERENCES boat_space_reservation(id)
);

ALTER TABLE payment
    ADD COLUMN reservation_id INT,
    ADD FOREIGN KEY (reservation_id) REFERENCES boat_space_reservation(id);

/* Copy the payment ids to reservation table */
UPDATE payment
SET reservation_id = boat_space_reservation.id
FROM boat_space_reservation
WHERE payment.id = boat_space_reservation.payment_id;


ALTER TABLE boat_space_reservation DROP COLUMN payment_id;
