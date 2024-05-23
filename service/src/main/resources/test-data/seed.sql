-- Populate the price table with 5 different prices
INSERT INTO price (name, price) VALUES
                                    ('ML1', 100.00),
                                    ('ML2', 150.00),
                                    ('ML3', 200.00),
                                    ('ML4', 80.00),
                                    ('ML5', 250.00);

-- Populate the location table with 7 different locations with made-up Finnish addresses
INSERT INTO location (name, address) VALUES
                                         ('Haukilahden satama', 'Satamatie 1, Espoo'),
                                         ('Kivenlahden satama', 'Kivenlahdentie 10, Espoo'),
                                         ('Laajalahden satama', 'Laajalahdentie 5, Espoo'),
                                         ('Otsolahden satama', 'Otsolahdentie 7, Espoo'),
                                         ('Soukan satama', 'Soukantie 3, Espoo'),
                                         ('Suomenojan satama', 'Suomenojantie 15, Espoo'),
                                         ('Svinön satama', 'Svinöntie 8, Espoo');

-- Populate the boat_space table with 3-7 sections per location and 30-100 boat spaces per section
DO $$
    DECLARE
        loc_id INT;
        price_id INT;
        boat_type BoatSpaceType;
        boat_amenity BoatAmenity;
        section CHAR(1);
        section_count INT;
        place_num INT;
        space_count INT;
    BEGIN
        FOR loc_id IN 1..7 LOOP
                section_count := 3 + FLOOR(RANDOM() * 5); -- 3-7 sections
                FOR section_num IN 1..section_count LOOP
                        section := CHR(65 + section_num - 1); -- Section A, B, C, etc.
                        space_count := 30 + FLOOR(RANDOM() * 71); -- 30-100 boat spaces
                        FOR place_num IN 1..space_count LOOP
                                -- Randomly assign price, type, and amenity
                                price_id := (SELECT id FROM price ORDER BY RANDOM() LIMIT 1);
                                boat_type := (ARRAY['Storage', 'Slip'])[FLOOR(RANDOM() * 2 + 1)];
                                boat_amenity := (ARRAY['None', 'Buoy', 'RearBuoy', 'Beam', 'WalkBeam'])[FLOOR(RANDOM() * 5 + 1)];

                                INSERT INTO boat_space (type, location_id, price_id, section, place_number, amenity, width_cm, length_cm, description)
                                VALUES (boat_type, loc_id, price_id, section, place_num, boat_amenity, 300 + FLOOR(RANDOM() * 200), 600 + FLOOR(RANDOM() * 400), 'Test description');
                            END LOOP;
                    END LOOP;
            END LOOP;
    END $$;
