-- noinspection SqlWithoutWhereForFile

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
