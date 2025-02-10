-- noinspection SqlWithoutWhereForFile

INSERT INTO location (id, name, address)
VALUES (1, 'Haukilahti', 'Satamatie 1, Espoo'),
       (2, 'Kivenlahti', 'Kivenlahdentie 10, Espoo'),
       (3, 'Laajalahti', 'Laajalahdentie 5, Espoo'),
       (4, 'Otsolahti', 'Otsolahdentie 7, Espoo'),
       (5, 'Soukka', 'Soukantie 3, Espoo'),
       (6, 'Suomenoja', 'Suomenojantie 15, Espoo'),
       (7, 'Svinö', 'Svinöntie 8, Espoo')
ON CONFLICT (id) DO NOTHING;

DELETE FROM harbor_restriction;

INSERT INTO harbor_restriction(location_id, excluded_boat_type)
VALUES
    (1, 'JetSki'),
    (2, 'JetSki'),
    (3, 'JetSki'),
    (4, 'JetSki'),
    (5, 'JetSki'),
    (7, 'JetSki'),
    (4, 'Sailboat'),
    (7, 'Sailboat');
