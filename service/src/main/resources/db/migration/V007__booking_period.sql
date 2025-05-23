CREATE TABLE booking_period (
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    start_time TIME WITHOUT TIME ZONE NOT NULL DEFAULT '09:00:00',
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

INSERT INTO booking_period (start_date, end_date, start_time, is_espoo_citizen, operation, boat_space_type) VALUES
    ('2024-01-07', '2024-01-31', '09:00:00', true, 'Renew', 'Slip'),
    ('2024-01-07', '2024-09-30', '09:00:00', true, 'Change', 'Slip'),
    ('2024-03-03', '2024-09-30', '09:00:00', true, 'New', 'Slip'),
    ('2024-04-01', '2024-09-30', '09:00:00', true, 'SecondNew', 'Slip'),
    ('2024-04-01', '2024-04-30', '09:00:00', true, 'Renew', 'Trailer'),
    ('2024-05-01', '2024-12-31', '09:00:00', true, 'New', 'Trailer'),
    ('2024-04-01', '2024-12-31', '09:00:00', true, 'Change', 'Trailer'),
    ('2024-08-15', '2024-09-14', '09:00:00', true, 'Renew', 'Winter'),
    ('2024-08-15', '2024-12-31', '09:00:00', true, 'Change', 'Winter'),
    ('2024-09-15', '2024-12-31', '09:00:00', true, 'New', 'Winter'),
    ('2024-09-15', '2024-12-31', '09:00:00', true, 'SecondNew', 'Winter'),
    ('2024-08-15', '2024-09-14', '09:00:00', true, 'Renew', 'Storage'),
    ('2023-08-15', '2024-08-14', '09:00:00', true, 'Change', 'Storage'),
    ('2023-09-15', '2024-09-14', '09:00:00', true, 'New', 'Storage'),
    ('2023-09-15', '2024-09-14', '09:00:00', true, 'SecondNew', 'Storage'),
    ('2024-04-01', '2024-09-30', '09:00:00', false, 'New', 'Slip'),
    ('2024-04-01', '2024-09-30', '09:00:00', false, 'Change', 'Slip'),
    ('2024-05-01', '2024-12-31', '09:00:00', false, 'New', 'Trailer'),
    ('2024-05-01', '2024-12-31', '09:00:00', false, 'Change', 'Trailer'),
    ('2024-08-15', '2024-09-14', '09:00:00', false, 'Renew', 'Storage'),
    ('2023-08-15', '2024-08-14', '09:00:00', false, 'Change', 'Storage'),
    ('2023-09-15', '2024-09-14', '09:00:00', false, 'New', 'Storage'),
    ('2023-09-15', '2024-09-14', '09:00:00', false, 'SecondNew', 'Storage'),

    ('2025-01-07', '2025-01-31', '09:00:00', true, 'Renew', 'Slip'),
    ('2025-01-07', '2025-09-30', '09:00:00', true, 'Change', 'Slip'),
    ('2025-03-03', '2025-09-30', '09:00:00', true, 'New', 'Slip'),
    ('2025-04-01', '2025-09-30', '09:00:00', true, 'SecondNew', 'Slip'),
    ('2025-04-01', '2025-04-30', '09:00:00', true, 'Renew', 'Trailer'),
    ('2025-05-01', '2025-12-31', '09:00:00', true, 'New', 'Trailer'),
    ('2025-04-01', '2025-12-31', '09:00:00', true, 'Change', 'Trailer'),
    ('2025-08-15', '2025-09-14', '09:00:00', true, 'Renew', 'Winter'),
    ('2025-08-15', '2025-12-31', '09:00:00', true, 'Change', 'Winter'),
    ('2025-09-15', '2025-12-31', '09:00:00', true, 'New', 'Winter'),
    ('2025-09-15', '2025-12-31', '09:00:00', true, 'SecondNew', 'Winter'),
    ('2025-08-15', '2025-09-14', '09:00:00', true, 'Renew', 'Storage'),
    ('2024-08-15', '2025-08-14', '09:00:00', true, 'Change', 'Storage'),
    ('2024-09-15', '2025-09-14', '09:00:00', true, 'New', 'Storage'),
    ('2024-09-15', '2025-09-14', '09:00:00', true, 'SecondNew', 'Storage'),
    ('2025-04-01', '2025-09-30', '09:00:00', false, 'New', 'Slip'),
    ('2025-04-01', '2025-09-30', '09:00:00', false, 'Change', 'Slip'),
    ('2025-05-01', '2025-12-31', '09:00:00', false, 'New', 'Trailer'),
    ('2025-05-01', '2025-12-31', '09:00:00', false, 'Change', 'Trailer'),
    ('2025-08-15', '2025-09-14', '09:00:00', false, 'Renew', 'Storage'),
    ('2024-08-15', '2025-08-14', '09:00:00', false, 'Change', 'Storage'),
    ('2024-09-15', '2025-09-14', '09:00:00', false, 'New', 'Storage'),
    ('2024-09-15', '2025-09-14', '09:00:00', false, 'SecondNew', 'Storage'),

    ('2026-01-06', '2026-01-31', '09:00:00', true, 'Renew', 'Slip'),
    ('2026-01-06', '2026-09-30', '09:00:00', true, 'Change', 'Slip'),
    ('2026-03-02', '2026-09-30', '09:00:00', true, 'New', 'Slip'),
    ('2026-04-01', '2026-09-30', '09:00:00', true, 'SecondNew', 'Slip'),
    ('2026-04-01', '2026-04-30', '09:00:00', true, 'Renew', 'Trailer'),
    ('2026-05-01', '2026-12-31', '09:00:00', true, 'New', 'Trailer'),
    ('2026-04-01', '2026-12-31', '09:00:00', true, 'Change', 'Trailer'),
    ('2026-08-17', '2026-09-14', '09:00:00', true, 'Renew', 'Winter'),
    ('2026-08-17', '2026-12-31', '09:00:00', true, 'Change', 'Winter'),
    ('2026-09-15', '2026-12-31', '09:00:00', true, 'New', 'Winter'),
    ('2026-09-15', '2026-12-31', '09:00:00', true, 'SecondNew', 'Winter'),
    ('2026-08-17', '2026-09-14', '09:00:00', true, 'Renew', 'Storage'),
    ('2025-08-17', '2026-08-14', '09:00:00', true, 'Change', 'Storage'),
    ('2025-09-15', '2026-09-14', '09:00:00', true, 'New', 'Storage'),
    ('2025-09-15', '2026-09-14', '09:00:00', true, 'SecondNew', 'Storage'),
    ('2026-04-01', '2026-09-30', '09:00:00', false, 'New', 'Slip'),
    ('2026-04-01', '2026-09-30', '09:00:00', false, 'Change', 'Slip'),
    ('2026-05-01', '2026-12-31', '09:00:00', false, 'New', 'Trailer'),
    ('2026-05-01', '2026-12-31', '09:00:00', false, 'Change', 'Trailer'),
    ('2026-08-17', '2026-09-14', '09:00:00', false, 'Renew', 'Storage'),
    ('2025-08-17', '2026-08-14', '09:00:00', false, 'Change', 'Storage'),
    ('2025-09-15', '2026-09-14', '09:00:00', false, 'New', 'Storage'),
    ('2025-09-15', '2026-09-14', '09:00:00', false, 'SecondNew', 'Storage'),

    ('2027-01-05', '2027-01-31', '09:00:00', true, 'Renew', 'Slip'),
    ('2027-01-05', '2027-09-30', '09:00:00', true, 'Change', 'Slip'),
    ('2027-03-01', '2027-09-30', '09:00:00', true, 'New', 'Slip'),
    ('2027-04-01', '2027-09-30', '09:00:00', true, 'SecondNew', 'Slip'),
    ('2027-04-01', '2027-04-30', '09:00:00', true, 'Renew', 'Trailer'),
    ('2027-05-03', '2027-12-31', '09:00:00', true, 'New', 'Trailer'),
    ('2027-04-01', '2027-12-31', '09:00:00', true, 'Change', 'Trailer'),
    ('2027-08-16', '2027-09-14', '09:00:00', true, 'Renew', 'Winter'),
    ('2027-08-16', '2027-12-31', '09:00:00', true, 'Change', 'Winter'),
    ('2027-09-15', '2027-12-31', '09:00:00', true, 'New', 'Winter'),
    ('2027-09-15', '2027-12-31', '09:00:00', true, 'SecondNew', 'Winter'),
    ('2027-08-16', '2027-09-14', '09:00:00', true, 'Renew', 'Storage'),
    ('2026-08-16', '2027-08-14', '09:00:00', true, 'Change', 'Storage'),
    ('2026-09-15', '2027-09-14', '09:00:00', true, 'New', 'Storage'),
    ('2026-09-15', '2027-09-14', '09:00:00', true, 'SecondNew', 'Storage'),
    ('2027-04-01', '2027-09-30', '09:00:00', false, 'New', 'Slip'),
    ('2027-04-01', '2027-09-30', '09:00:00', false, 'Change', 'Slip'),
    ('2027-05-03', '2027-12-31', '09:00:00', false, 'New', 'Trailer'),
    ('2027-05-03', '2027-12-31', '09:00:00', false, 'Change', 'Trailer'),
    ('2027-08-16', '2027-09-14', '09:00:00', false, 'Renew', 'Storage'),
    ('2026-08-16', '2027-08-14', '09:00:00', false, 'Change', 'Storage'),
    ('2026-09-15', '2027-09-14', '09:00:00', false, 'New', 'Storage'),
    ('2026-09-15', '2027-09-14', '09:00:00', false, 'SecondNew', 'Storage');

    