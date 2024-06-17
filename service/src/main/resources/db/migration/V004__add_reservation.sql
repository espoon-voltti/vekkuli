/*
 * SPDX-FileCopyrightText: 2023-2024 City of Espoo
 *
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

CREATE TYPE ReservationStatus AS ENUM ('Info', 'Payment', 'Confirmed', 'Cancelled');

CREATE TABLE boat_space_reservation (
    id serial PRIMARY KEY,
    citizen_id uuid NOT NULL,
    boat_space_id SERIAL NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    created TIMESTAMP NOT NULL DEFAULT now(),
    updated TIMESTAMP NOT NULL DEFAULT now(),
    status ReservationStatus NOT NULL DEFAULT 'Info',
    FOREIGN KEY (citizen_id) REFERENCES citizen(id),
    FOREIGN KEY (boat_space_id) REFERENCES boat_space(id)
);
CREATE INDEX idx_boat_space_reservation_citizen_id ON boat_space_reservation(citizen_id);
CREATE INDEX idx_boat_space_reservation_boat_space_id ON boat_space_reservation(boat_space_id);
CREATE INDEX idx_boat_space_reservation_start_date ON boat_space_reservation(start_date);
CREATE INDEX idx_boat_space_reservation_end_date ON boat_space_reservation(end_date);
CREATE INDEX idx_boat_space_reservation_status ON boat_space_reservation(status);

