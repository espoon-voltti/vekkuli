-- noinspection SqlWithoutWhereForFile

INSERT INTO location (id, name, address)
VALUES (1, 'Haukilahti', 'Mellstenintie 6, 02170 Espoo'),
       (2, 'Kivenlahti', 'Marinsatamantie 5, 02320 Espoo'),
       (3, 'Laajalahti', 'Ruukinrannantie 29, 02600 Espoo'),
       (4, 'Otsolahti', 'Sateenkaari 9, 02100 Espoo'),
       (5, 'Soukka', 'Soukanlahdentie 15, 02360 Espoo'),
       (6, 'Suomenoja', 'Hylkeenpyytäjäntie 9, 02270 Espoo'),
       (7, 'Svinö', 'Skatantie 36, 02380 Espoo'),
       (8, 'Ämmäsmäki', 'Ämmäsmäentie 4, 02820 Espoo')
ON CONFLICT (id) DO UPDATE SET
    name = EXCLUDED.name,
    address = EXCLUDED.address;

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
