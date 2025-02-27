CREATE TABLE booking_period (
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    is_espoo_citizen BOOLEAN NOT NULL DEFAULT FALSE,
    operation reservationoperation NOT NULL DEFAULT 'New'::reservationoperation,
    boat_space_type boatspacetype NOT NULL DEFAULT 'Slip'::boatspacetype,
    created TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE EXTENSION IF NOT EXISTS btree_gist;

ALTER TABLE booking_period
    ADD CONSTRAINT booking_period_no_overlap
    EXCLUDE USING GIST (
    daterange(start_date, end_date, '[]') WITH &&,
    is_espoo_citizen WITH =,
    operation WITH =,
    boat_space_type WITH =
);