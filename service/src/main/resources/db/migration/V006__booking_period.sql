CREATE TABLE booking_period (
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    is_espoo_citizen BOOLEAN NOT NULL DEFAULT FALSE,
    operation reservationoperation NOT NULL DEFAULT 'New'::reservationoperation,
    boat_space_type boatspacetype NOT NULL DEFAULT 'Slip'::boatspacetype,
    created TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);