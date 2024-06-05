-- SPDX-FileCopyrightText: 2023-2024 City of Espoo
--
-- SPDX-License-Identifier: LGPL-2.1-or-later

-- Populate the price table with 5 different prices
INSERT INTO price (name, price)
VALUES ('ML1', 100.00),
       ('ML2', 150.00),
       ('ML3', 200.00),
       ('ML4', 80.00),
       ('ML5', 250.00);

-- Populate the location table with 7 different locations with made-up Finnish addresses
INSERT INTO location (name, address)
VALUES ('Haukilahti', 'Satamatie 1, Espoo'),
       ('Kivenlahti', 'Kivenlahdentie 10, Espoo'),
       ('Laajalahti', 'Laajalahdentie 5, Espoo'),
       ('Otsolahti', 'Otsolahdentie 7, Espoo'),
       ('Soukka', 'Soukantie 3, Espoo'),
       ('Suomenoja', 'Suomenojantie 15, Espoo'),
       ('Svinö', 'Svinöntie 8, Espoo');

INSERT INTO citizen (id, name, phone, email)
VALUES ('62d90eed-4ea3-4446-8023-8dad9c01dd34', 'Mikko Virtanen', '0401122334', 'mikko.virtanen@noreplytest.fi');

INSERT INTO boat_space_application (citizen_id, created_at, type, boat_type, amenity, boat_width_cm, boat_length_cm,
                                    boat_weight_kg, boat_registration_code, information)
VALUES ('62d90eed-4ea3-4446-8023-8dad9c01dd34', now(), 'Slip', 'JetSki', 'Buoy', 180, 400, 200, 'A255345', 'Mieluiten laiturin kärjestä');

INSERT INTO boat_space_application (citizen_id, created_at, type, boat_type, amenity, boat_width_cm, boat_length_cm,
                                    boat_weight_kg, boat_registration_code, trailer_space_length, trailer_space_width, trailer_space_registration_code, information)
VALUES ('62d90eed-4ea3-4446-8023-8dad9c01dd34', now(), 'Trailer', 'JetSki', 'Buoy', 180, 400, 200, 'A255345', 200, 500, 'OYJ123', '');

INSERT INTO boat_space_application_location_wish (boat_space_application_id, location_id, priority)
VALUES (1, 1, 1),
       (1, 2, 2);

-- Populate the boat_space table with 3-7 sections per location and 30-100 boat spaces per section
DO
$$
    DECLARE
        loc_id        INT;
        price_id      INT;
        boat_type     BoatSpaceType;
        boat_amenity  BoatAmenity;
        section       CHAR(1);
        section_count INT;
        place_num     INT;
        space_count   INT;
    BEGIN
        FOR loc_id IN 1..7
            LOOP
                section_count := 3 + FLOOR(RANDOM() * 5); -- 3-7 sections
                FOR section_num IN 1..section_count
                    LOOP
                        section := CHR(65 + section_num - 1); -- Section A, B, C, etc.
                        space_count := 30 + FLOOR(RANDOM() * 71); -- 30-100 boat spaces
                        FOR place_num IN 1..space_count
                            LOOP
                                -- Randomly assign price, type, and amenity
                                price_id := (SELECT id FROM price ORDER BY RANDOM() LIMIT 1);
                                boat_type := (ARRAY ['Storage', 'Slip', 'Trailer'])[FLOOR(RANDOM() * 3 + 1)];
                                boat_amenity :=
                                        (ARRAY ['None', 'Buoy', 'RearBuoy', 'Beam', 'WalkBeam'])[FLOOR(RANDOM() * 5 + 1)];

                                INSERT INTO boat_space (type, location_id, price_id, section, place_number, amenity,
                                                        width_cm, length_cm, description)
                                VALUES (boat_type, loc_id, price_id, section, place_num, boat_amenity,
                                        300 + FLOOR(RANDOM() * 200), 600 + FLOOR(RANDOM() * 400), 'Test description');
                            END LOOP;
                    END LOOP;
            END LOOP;
    END
$$;
