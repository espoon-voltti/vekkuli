-- Replace any manually added 2027 booking periods
DELETE FROM booking_period WHERE start_date >= '2027-01-01'::date AND start_date <= '2027-12-31'::date;

INSERT INTO booking_period (start_date, end_date, start_time, is_espoo_citizen, operation, boat_space_type) VALUES ('2027-01-11', '2027-12-15', '09:00:00', 'true', 'Change', 'Slip');
INSERT INTO booking_period (start_date, end_date, start_time, is_espoo_citizen, operation, boat_space_type) VALUES ('2027-01-11', '2027-01-31', '09:00:00', 'true', 'Renew', 'Slip');
INSERT INTO booking_period (start_date, end_date, start_time, is_espoo_citizen, operation, boat_space_type) VALUES ('2027-03-01', '2027-10-31', '09:00:00', 'true', 'New', 'Slip');
INSERT INTO booking_period (start_date, end_date, start_time, is_espoo_citizen, operation, boat_space_type) VALUES ('2027-04-01', '2027-12-15', '09:00:00', 'false', 'Change', 'Slip');
INSERT INTO booking_period (start_date, end_date, start_time, is_espoo_citizen, operation, boat_space_type) VALUES ('2027-04-01', '2027-10-31', '09:00:00', 'false', 'New', 'Slip');
INSERT INTO booking_period (start_date, end_date, start_time, is_espoo_citizen, operation, boat_space_type) VALUES ('2027-04-01', '2027-10-31', '09:00:00', 'true', 'SecondNew', 'Slip');
INSERT INTO booking_period (start_date, end_date, start_time, is_espoo_citizen, operation, boat_space_type) VALUES ('2027-04-10', '2027-12-15', '09:00:00', 'true', 'Change', 'Trailer');
INSERT INTO booking_period (start_date, end_date, start_time, is_espoo_citizen, operation, boat_space_type) VALUES ('2027-04-10', '2027-04-30', '09:00:00', 'true', 'Renew', 'Trailer');
INSERT INTO booking_period (start_date, end_date, start_time, is_espoo_citizen, operation, boat_space_type) VALUES ('2027-04-10', '2027-12-15', '09:00:00', 'false', 'Change', 'Trailer');
INSERT INTO booking_period (start_date, end_date, start_time, is_espoo_citizen, operation, boat_space_type) VALUES ('2027-05-01', '2027-12-15', '09:00:00', 'true', 'New', 'Trailer');
INSERT INTO booking_period (start_date, end_date, start_time, is_espoo_citizen, operation, boat_space_type) VALUES ('2027-05-01', '2027-12-15', '09:00:00', 'false', 'New', 'Trailer');
INSERT INTO booking_period (start_date, end_date, start_time, is_espoo_citizen, operation, boat_space_type) VALUES ('2027-08-25', '2027-09-14', '09:00:00', 'true', 'Renew', 'Storage');
INSERT INTO booking_period (start_date, end_date, start_time, is_espoo_citizen, operation, boat_space_type) VALUES ('2027-08-25', '2027-09-14', '09:00:00', 'false', 'Renew', 'Storage');
INSERT INTO booking_period (start_date, end_date, start_time, is_espoo_citizen, operation, boat_space_type) VALUES ('2027-08-25', '2027-12-15', '09:00:00', 'true', 'Change', 'Winter');
INSERT INTO booking_period (start_date, end_date, start_time, is_espoo_citizen, operation, boat_space_type) VALUES ('2027-08-25', '2027-09-14', '09:00:00', 'true', 'Renew', 'Winter');
INSERT INTO booking_period (start_date, end_date, start_time, is_espoo_citizen, operation, boat_space_type) VALUES ('2027-09-15', '2028-09-14', '09:00:00', 'true', 'Change', 'Storage');
INSERT INTO booking_period (start_date, end_date, start_time, is_espoo_citizen, operation, boat_space_type) VALUES ('2027-09-15', '2028-09-14', '09:00:00', 'false', 'Change', 'Storage');
INSERT INTO booking_period (start_date, end_date, start_time, is_espoo_citizen, operation, boat_space_type) VALUES ('2027-09-15', '2028-09-14', '09:00:00', 'true', 'New', 'Storage');
INSERT INTO booking_period (start_date, end_date, start_time, is_espoo_citizen, operation, boat_space_type) VALUES ('2027-09-15', '2028-09-14', '09:00:00', 'false', 'New', 'Storage');
INSERT INTO booking_period (start_date, end_date, start_time, is_espoo_citizen, operation, boat_space_type) VALUES ('2027-09-15', '2028-09-14', '09:00:00', 'true', 'SecondNew', 'Storage');
INSERT INTO booking_period (start_date, end_date, start_time, is_espoo_citizen, operation, boat_space_type) VALUES ('2027-09-15', '2028-09-14', '09:00:00', 'false', 'SecondNew', 'Storage');
INSERT INTO booking_period (start_date, end_date, start_time, is_espoo_citizen, operation, boat_space_type) VALUES ('2027-09-15', '2027-12-15', '09:00:00', 'true', 'New', 'Winter');
INSERT INTO booking_period (start_date, end_date, start_time, is_espoo_citizen, operation, boat_space_type) VALUES ('2027-09-15', '2027-12-15', '09:00:00', 'true', 'SecondNew', 'Winter');
