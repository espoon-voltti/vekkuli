CREATE TABLE invoice (
    due_date TIMESTAMP NOT NULL,
    reference TEXT NOT NULL,
    payment_date TIMESTAMP NOT NULL,
    reservation_id INT NOT NULL,

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
