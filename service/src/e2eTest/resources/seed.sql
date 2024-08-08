-- SPDX-FileCopyrightText: 2023-2024 City of Espoo
--
-- SPDX-License-Identifier: LGPL-2.1-or-later

-- Populate the price table with 5 different prices
INSERT INTO price (name, price)
VALUES ('ML1', 100.00),
       ('ML2', 150.00),
       ('ML3', 200.00),
       ('ML4', 80.00),
       ('ML5', 250.00),
       ('ML6', 290.00);

-- Populate the location table with 7 different locations with made-up Finnish addresses
INSERT INTO location (name, address)
VALUES ('Haukilahti', 'Satamatie 1, Espoo'),
       ('Kivenlahti', 'Kivenlahdentie 10, Espoo'),
       ('Laajalahti', 'Laajalahdentie 5, Espoo'),
       ('Otsolahti', 'Otsolahdentie 7, Espoo'),
       ('Soukka', 'Soukantie 3, Espoo'),
       ('Suomenoja', 'Suomenojantie 15, Espoo'),
       ('Svinö', 'Svinöntie 8, Espoo');

INSERT INTO harbor_restriction (location_id, excluded_boat_type) VALUES (1, 'JetSki');
INSERT INTO harbor_restriction (location_id, excluded_boat_type) VALUES (4, 'Sailboat');
INSERT INTO harbor_restriction (location_id, excluded_boat_type) VALUES (7, 'JetSki');
INSERT INTO harbor_restriction (location_id, excluded_boat_type) VALUES (7, 'Sailboat');

INSERT INTO citizen (id, updated, national_id, first_name, last_name, phone, email)
VALUES ('62d90eed-4ea3-4446-8023-8dad9c01dd34', now(), '010106A957V', 'Mikko', 'Virtanen', '0401122334', 'mikko.virtanen@noreplytest.fi');

INSERT INTO boat_space_application (citizen_id, created_at, type, boat_type, amenity, boat_width_cm, boat_length_cm,
                                    boat_weight_kg, boat_registration_code, information)
VALUES ('62d90eed-4ea3-4446-8023-8dad9c01dd34', now(), 'Slip', 'JetSki', 'Buoy', 180, 400, 200, 'A255345', 'Mieluiten laiturin kärjestä');

INSERT INTO boat_space_application (citizen_id, created_at, type, boat_type, amenity, boat_width_cm, boat_length_cm,
                                    boat_weight_kg, boat_registration_code, trailer_space_length, trailer_space_width, trailer_space_registration_code, information)
VALUES ('62d90eed-4ea3-4446-8023-8dad9c01dd34', now(), 'Trailer', 'JetSki', 'Buoy', 180, 400, 200, 'A255345', 200, 500, 'OYJ123', '');

INSERT INTO boat_space_application_location_wish (boat_space_application_id, location_id, priority)
VALUES (1, 1, 1),
       (1, 2, 2);
