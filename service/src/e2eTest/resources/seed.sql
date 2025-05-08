-- SPDX-FileCopyrightText: 2023-2024 City of Espoo
--
-- SPDX-License-Identifier: LGPL-2.1-or-later

-- Populate the price table with 5 different prices
INSERT INTO municipality (code, name)
VALUES
    (1, 'Kotikuntaa vailla olevat'),
    (4, 'Alahärmä'),
    (5, 'Alajärvi'),
    (6, 'Alastaro'),
    (9, 'Alavieska'),
    (10, 'Alavus'),
    (754, 'Anjalankoski'),
    (15, 'Artjärvi'),
    (16, 'Asikkala'),
    (17, 'Askainen'),
    (18, 'Askola'),
    (19, 'Aura'),
    (35, 'Brandö'),
    (40, 'Dragsfjärd'),
    (43, 'Eckerö'),
    (44, 'Elimäki'),
    (45, 'Eno'),
    (46, 'Enonkoski'),
    (47, 'Enontekiö'),
    (49, 'Espoo'),
    (50, 'Eura'),
    (51, 'Eurajoki'),
    (52, 'Evijärvi'),
    (60, 'Finström'),
    (61, 'Forssa'),
    (62, 'Föglö'),
    (65, 'Geta'),
    (69, 'Haapajärvi'),
    (71, 'Haapavesi'),
    (72, 'Hailuoto'),
    (73, 'Halikko'),
    (74, 'Halsua'),
    (75, 'Hamina'),
    (76, 'Hammarland'),
    (77, 'Hankasalmi'),
    (78, 'Hanko'),
    (79, 'Harjavalta'),
    (81, 'Hartola'),
    (82, 'Hattula'),
    (83, 'Hauho'),
    (84, 'Haukipudas'),
    (85, 'Haukivuori'),
    (86, 'Hausjärvi'),
    (111, 'Heinola'),
    (90, 'Heinävesi'),
    (91, 'Helsinki'),
    (95, 'Himanka'),
    (97, 'Hirvensalmi'),
    (98, 'Hollola'),
    (99, 'Honkajoki'),
    (101, 'Houtskari'),
    (102, 'Huittinen'),
    (103, 'Humppila'),
    (105, 'Hyrynsalmi'),
    (106, 'Hyvinkää'),
    (283, 'Hämeenkoski'),
    (108, 'Hämeenkyrö'),
    (109, 'Hämeenlinna'),
    (139, 'Ii'),
    (140, 'Iisalmi'),
    (142, 'Iitti'),
    (143, 'Ikaalinen'),
    (145, 'Ilmajoki'),
    (146, 'Ilomantsi'),
    (153, 'Imatra'),
    (148, 'Inari'),
    (150, 'Iniö'),
    (149, 'Inkoo'),
    (151, 'Isojoki'),
    (152, 'Isokyrö'),
    (163, 'Jaala'),
    (164, 'Jalasjärvi'),
    (165, 'Janakkala'),
    (167, 'Joensuu'),
    (169, 'Jokioinen'),
    (170, 'Jomala'),
    (171, 'Joroinen'),
    (172, 'Joutsa'),
    (173, 'Joutseno'),
    (174, 'Juankoski'),
    (175, 'Jurva'),
    (176, 'Juuka'),
    (177, 'Juupajoki'),
    (178, 'Juva'),
    (179, 'Jyväskylä'),
    (180, 'Jyväskylän mlk'),
    (181, 'Jämijärvi'),
    (182, 'Jämsä'),
    (183, 'Jämsänkoski'),
    (184, 'Jäppilä'),
    (186, 'Järvenpää'),
    (202, 'Kaarina'),
    (204, 'Kaavi'),
    (205, 'Kajaani'),
    (208, 'Kalajoki'),
    (210, 'Kalvola'),
    (211, 'Kangasala'),
    (212, 'Kangaslampi'),
    (213, 'Kangasniemi'),
    (214, 'Kankaanpää'),
    (216, 'Kannonkoski'),
    (217, 'Kannus'),
    (218, 'Karijoki'),
    (219, 'Karinainen'),
    (220, 'Karjaa'),
    (223, 'Karjalohja'),
    (224, 'Karkkila'),
    (226, 'Karstula'),
    (227, 'Karttula'),
    (230, 'Karvia'),
    (231, 'Kaskinen'),
    (232, 'Kauhajoki'),
    (233, 'Kauhava'),
    (235, 'Kauniainen'),
    (236, 'Kaustinen'),
    (239, 'Keitele'),
    (240, 'Kemi'),
    (320, 'Kemijärvi'),
    (241, 'Keminmaa'),
    (243, 'Kemiö'),
    (244, 'Kempele'),
    (245, 'Kerava'),
    (246, 'Kerimäki'),
    (247, 'Kestilä'),
    (248, 'Kesälahti'),
    (249, 'Keuruu'),
    (250, 'Kihniö'),
    (251, 'Kiihtelysvaara'),
    (252, 'Kiikala'),
    (254, 'Kiikoinen'),
    (255, 'Kiiminki'),
    (256, 'Kinnula'),
    (257, 'Kirkkonummi'),
    (259, 'Kisko'),
    (260, 'Kitee'),
    (261, 'Kittilä'),
    (262, 'Kiukainen'),
    (263, 'Kiuruvesi'),
    (265, 'Kivijärvi'),
    (266, 'Kodisjoki'),
    (271, 'Kokemäki'),
    (272, 'Kokkola'),
    (273, 'Kolari'),
    (275, 'Konnevesi'),
    (276, 'Kontiolahti'),
    (277, 'Korpilahti'),
    (279, 'Korppoo'),
    (280, 'Korsnäs'),
    (281, 'Kortesjärvi'),
    (284, 'Koski Tl'),
    (285, 'Kotka'),
    (286, 'Kouvola'),
    (287, 'Kristiinankaupunki'),
    (288, 'Kruunupyy'),
    (289, 'Kuhmalahti'),
    (290, 'Kuhmo'),
    (291, 'Kuhmoinen'),
    (292, 'Kuivaniemi'),
    (293, 'Kullaa'),
    (295, 'Kumlinge'),
    (297, 'Kuopio'),
    (300, 'Kuortane'),
    (301, 'Kurikka'),
    (303, 'Kuru'),
    (304, 'Kustavi'),
    (305, 'Kuusamo'),
    (306, 'Kuusankoski'),
    (308, 'Kuusjoki'),
    (310, 'Kylmäkoski'),
    (312, 'Kyyjärvi'),
    (315, 'Kälviä'),
    (316, 'Kärkölä'),
    (317, 'Kärsämäki'),
    (318, 'Kökar'),
    (319, 'Köyliö'),
    (398, 'Lahti'),
    (399, 'Laihia'),
    (400, 'Laitila'),
    (401, 'Lammi'),
    (407, 'Lapinjärvi'),
    (402, 'Lapinlahti'),
    (403, 'Lappajärvi'),
    (405, 'Lappeenranta'),
    (406, 'Lappi'),
    (408, 'Lapua'),
    (410, 'Laukaa'),
    (413, 'Lavia'),
    (414, 'Lehtimäki'),
    (415, 'Leivonmäki'),
    (416, 'Lemi'),
    (417, 'Lemland'),
    (418, 'Lempäälä'),
    (419, 'Lemu'),
    (420, 'Leppävirta'),
    (421, 'Lestijärvi'),
    (422, 'Lieksa'),
    (423, 'Lieto'),
    (424, 'Liljendal'),
    (425, 'Liminka'),
    (426, 'Liperi'),
    (444, 'Lohja'),
    (429, 'Lohtaja'),
    (430, 'Loimaa'),
    (431, 'Loimaan kunta'),
    (433, 'Loppi'),
    (434, 'Loviisa'),
    (435, 'Luhanka'),
    (436, 'Lumijoki'),
    (438, 'Lumparland'),
    (439, 'Luopioinen'),
    (440, 'Luoto'),
    (441, 'Luumäki'),
    (442, 'Luvia'),
    (443, 'Längelmäki'),
    (475, 'Maalahti'),
    (476, 'Maaninka'),
    (478, 'Maarianhamina'),
    (479, 'Maksamaa'),
    (480, 'Marttila'),
    (481, 'Masku'),
    (482, 'Mellilä'),
    (483, 'Merijärvi'),
    (484, 'Merikarvia'),
    (485, 'Merimasku'),
    (489, 'Miehikkälä'),
    (490, 'Mietoinen'),
    (491, 'Mikkeli'),
    (493, 'Mouhijärvi'),
    (494, 'Muhos'),
    (495, 'Multia'),
    (498, 'Muonio'),
    (499, 'Mustasaari'),
    (500, 'Muurame'),
    (501, 'Muurla'),
    (503, 'Mynämäki'),
    (504, 'Myrskylä'),
    (505, 'Mäntsälä'),
    (506, 'Mänttä'),
    (507, 'Mäntyharju'),
    (529, 'Naantali'),
    (531, 'Nakkila'),
    (532, 'Nastola'),
    (533, 'Nauvo'),
    (534, 'Nilsiä'),
    (535, 'Nivala'),
    (536, 'Nokia'),
    (537, 'Noormarkku'),
    (538, 'Nousiainen'),
    (540, 'Nummi-Pusula'),
    (541, 'Nurmes'),
    (543, 'Nurmijärvi'),
    (544, 'Nurmo'),
    (545, 'Närpiö'),
    (559, 'Oravainen'),
    (560, 'Orimattila'),
    (561, 'Oripää'),
    (562, 'Orivesi'),
    (563, 'Oulainen'),
    (564, 'Oulu'),
    (567, 'Oulunsalo'),
    (309, 'Outokumpu'),
    (576, 'Padasjoki'),
    (577, 'Paimio'),
    (578, 'Paltamo'),
    (573, 'Parainen'),
    (580, 'Parikkala'),
    (581, 'Parkano'),
    (582, 'Pattijoki'),
    (599, 'Pedersöre'),
    (583, 'Pelkosenniemi'),
    (854, 'Pello'),
    (584, 'Perho'),
    (585, 'Pernaja'),
    (586, 'Perniö'),
    (587, 'Pertteli'),
    (588, 'Pertunmaa'),
    (589, 'Peräseinäjoki'),
    (592, 'Petäjävesi'),
    (594, 'Pieksämäen mlk'),
    (593, 'Pieksämäki'),
    (595, 'Pielavesi'),
    (598, 'Pietarsaari'),
    (601, 'Pihtipudas'),
    (602, 'Piikkiö'),
    (603, 'Piippola'),
    (604, 'Pirkkala'),
    (606, 'Pohja'),
    (607, 'Polvijärvi'),
    (608, 'Pomarkku'),
    (609, 'Pori'),
    (611, 'Pornainen'),
    (638, 'Porvoo'),
    (614, 'Posio'),
    (615, 'Pudasjärvi'),
    (616, 'Pukkila'),
    (617, 'Pulkkila'),
    (618, 'Punkaharju'),
    (619, 'Punkalaidun'),
    (620, 'Puolanka'),
    (623, 'Puumala'),
    (624, 'Pyhtää'),
    (625, 'Pyhäjoki'),
    (626, 'Pyhäjärvi'),
    (630, 'Pyhäntä'),
    (631, 'Pyhäranta'),
    (632, 'Pyhäselkä'),
    (633, 'Pylkönmäki'),
    (635, 'Pälkäne'),
    (636, 'Pöytyä'),
    (678, 'Raahe'),
    (680, 'Raisio'),
    (681, 'Rantasalmi'),
    (682, 'Rantsila'),
    (683, 'Ranua'),
    (684, 'Rauma'),
    (686, 'Rautalampi'),
    (687, 'Rautavaara'),
    (689, 'Rautjärvi'),
    (691, 'Reisjärvi'),
    (692, 'Renko'),
    (694, 'Riihimäki'),
    (696, 'Ristiina'),
    (697, 'Ristijärvi'),
    (699, 'Rovaniemen mlk'),
    (698, 'Rovaniemi'),
    (700, 'Ruokolahti'),
    (701, 'Ruotsinpyhtää'),
    (702, 'Ruovesi'),
    (704, 'Rusko'),
    (708, 'Ruukki'),
    (705, 'Rymättylä'),
    (707, 'Rääkkylä'),
    (728, 'Saari'),
    (729, 'Saarijärvi'),
    (730, 'Sahalahti'),
    (732, 'Salla'),
    (734, 'Salo'),
    (736, 'Saltvik'),
    (737, 'Sammatti'),
    (738, 'Sauvo'),
    (739, 'Savitaipale'),
    (740, 'Savonlinna'),
    (741, 'Savonranta'),
    (742, 'Savukoski'),
    (743, 'Seinäjoki'),
    (746, 'Sievi'),
    (747, 'Siikainen'),
    (748, 'Siikajoki'),
    (749, 'Siilinjärvi'),
    (751, 'Simo'),
    (753, 'Sipoo'),
    (755, 'Siuntio'),
    (758, 'Sodankylä'),
    (759, 'Soini'),
    (761, 'Somero'),
    (762, 'Sonkajärvi'),
    (765, 'Sotkamo'),
    (766, 'Sottunga'),
    (768, 'Sulkava'),
    (770, 'Sumiainen'),
    (771, 'Sund'),
    (772, 'Suodenniemi'),
    (774, 'Suolahti'),
    (775, 'Suomenniemi'),
    (776, 'Suomusjärvi'),
    (777, 'Suomussalmi'),
    (778, 'Suonenjoki'),
    (781, 'Sysmä'),
    (783, 'Säkylä'),
    (784, 'Särkisalo'),
    (831, 'Taipalsaari'),
    (832, 'Taivalkoski'),
    (833, 'Taivassalo'),
    (834, 'Tammela'),
    (835, 'Tammisaari'),
    (837, 'Tampere'),
    (838, 'Tarvasjoki'),
    (844, 'Tervo'),
    (845, 'Tervola'),
    (846, 'Teuva'),
    (848, 'Tohmajärvi'),
    (849, 'Toholampi'),
    (864, 'Toijala'),
    (850, 'Toivakka'),
    (851, 'Tornio'),
    (853, 'Turku'),
    (855, 'Tuulos'),
    (856, 'Tuupovaara'),
    (857, 'Tuusniemi'),
    (858, 'Tuusula'),
    (859, 'Tyrnävä'),
    (863, 'Töysä'),
    (885, 'Ullava'),
    (886, 'Ulvila'),
    (887, 'Urjala'),
    (889, 'Utajärvi'),
    (890, 'Utsjoki'),
    (891, 'Uukuniemi'),
    (892, 'Uurainen'),
    (893, 'Uusikaarlepyy'),
    (895, 'Uusikaupunki'),
    (785, 'Vaala'),
    (905, 'Vaasa'),
    (906, 'Vahto'),
    (908, 'Valkeakoski'),
    (909, 'Valkeala'),
    (911, 'Valtimo'),
    (912, 'Vammala'),
    (913, 'Vampula'),
    (92, 'Vantaa'),
    (915, 'Varkaus'),
    (916, 'Varpaisjärvi'),
    (917, 'Vehkalahti'),
    (918, 'Vehmaa'),
    (919, 'Vehmersalmi'),
    (920, 'Velkua'),
    (921, 'Vesanto'),
    (922, 'Vesilahti'),
    (924, 'Veteli'),
    (925, 'Vieremä'),
    (926, 'Vihanti'),
    (927, 'Vihti'),
    (928, 'Viiala'),
    (931, 'Viitasaari'),
    (932, 'Viljakkala'),
    (933, 'Vilppula'),
    (934, 'Vimpeli'),
    (935, 'Virolahti'),
    (936, 'Virrat'),
    (937, 'Virtasalmi'),
    (940, 'Vuolijoki'),
    (942, 'Vähäkyrö'),
    (943, 'Värtsilä'),
    (923, 'Västanfjärd'),
    (941, 'Vårdö'),
    (944, 'Vöyri'),
    (972, 'Yli-Ii'),
    (971, 'Ylihärmä'),
    (973, 'Ylikiiminki'),
    (975, 'Ylistaro'),
    (976, 'Ylitornio'),
    (977, 'Ylivieska'),
    (978, 'Ylämaa'),
    (979, 'Yläne'),
    (980, 'Ylöjärvi'),
    (981, 'Ypäjä'),
    (988, 'Äetsä'),
    (989, 'Ähtäri'),
    (992, 'Äänekoski')
ON CONFLICT DO NOTHING;

INSERT INTO price (name, net_price_cents, vat_cents, price_cents)
VALUES ('ML1', 17823, 4545, 22367),
       ('ML2', 21290, 5429, 26719),
       ('ML3', 27500, 7012, 34512),
       ('ML4', 33306, 8493, 41800),
       ('ML5', 36048, 9193, 45241),
       ('ML6', 43548, 11105, 54000);




-- Populate the location table with 8 different locations
INSERT INTO location (id, name, address)
VALUES (1, 'Haukilahti', 'Mellstenintie 6, 02170 Espoo'),
       (2, 'Kivenlahti', 'Marinsatamantie 5, 02320 Espoo'),
       (3, 'Laajalahti', 'Ruukinrannantie 29, 02600 Espoo'),
       (4, 'Otsolahti', 'Sateenkaari 9, 02100 Espoo'),
       (5, 'Soukka', 'Soukanlahdentie 15, 02360 Espoo'),
       (6, 'Suomenoja', 'Hylkeenpyytäjäntie 9, 02270 Espoo'),
       (7, 'Svinö', 'Skatantie 36, 02380 Espoo'),
       (8, 'Ämmäsmäki', 'Ämmäsmäentie 4, 02820 Espoo')
ON CONFLICT DO NOTHING;


DELETE FROM harbor_restriction;
INSERT INTO harbor_restriction (location_id, excluded_boat_type) VALUES (1, 'JetSki');
INSERT INTO harbor_restriction (location_id, excluded_boat_type) VALUES (4, 'Sailboat');
INSERT INTO harbor_restriction (location_id, excluded_boat_type) VALUES (7, 'JetSki');
INSERT INTO harbor_restriction (location_id, excluded_boat_type) VALUES (7, 'Sailboat');

DELETE FROM booking_period;

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


INSERT INTO app_user (id, external_id, first_name, last_name, email)
VALUES
    ('94833b54-132b-4ab8-b841-60df45809b3e', 'ad:001', 'Ville', 'Virkailija', 'ville@noreplytest.fi');

INSERT INTO reserver (id, type, name, updated_at, phone, email, street_address, postal_code, municipality_code, post_office, espoo_rules_applied)
VALUES
    ('62d90eed-4ea3-4446-8023-8dad9c01dd34', 'Citizen', 'Mikko virtanen', now(), '0401122334', 'mikko.virtanen@noreplytest.fi', 'Katu 1', '00100', 49, 'Espoo', false),
    ('f5d377ea-5547-11ef-a1c7-7f2b94cf9afd', 'Citizen', 'Leo Korhonen', now(), '0405623462', 'leo@noreplytest.fi', '', '' , 49, 'Espoo', false),
    ('509edb00-5549-11ef-a1c7-776e76028a49', 'Citizen', 'Olivia Virtanen', now(), '04083677348', 'olivia@noreplytest.fi', '', '' , 49, 'Espoo', false),
    ('1128bd21-fbbc-4e9a-8658-dc2044a64a58', 'Citizen', 'Marko Kuusinen', now(), '04583464312', 'marko@noreplytest.fi', '', '' , 91, 'Helsinki', false),
    ('82722a75-793a-4cbe-a3d9-a3043f2f5731', 'Citizen', 'Jorma Pulkkinen', now(), '0503528873', 'jorma@noreplytest.fi', '', '' , 398, 'Lahti', true),
    ('8b220a43-86a0-4054-96f6-d29a5aba17e7', 'Organization', 'Espoon Pursiseura', now(), '0448101969', 'eps@noreplytest.fi', 'Nuottatie 19', '02230', 49, 'Espoo', false),
    ('6a7b1b37-ace5-4992-878b-1fa0cd52e4e7', 'Citizen', 'Turvald Kieltoinen', now(), '050123123', 'turvald@kieltoinen.fi', '', '' , 49, 'Espoo', true)

ON CONFLICT (id) DO NOTHING;

INSERT INTO email_template (id, subject, body)
VALUES
    ('reservation_created_by_citizen', 'Vahvistus Espoon kaupungin {{placeTypeFi}}varauksesta', E'Hyvä asiakas,

Veneelle varaamasi {{placeTypeFi}} on maksettu ja varaus on vahvistettu.{{citizenReserverFi}}

Vuokralainen:
{{reserverName}}

Paikan tiedot:
{{harborAddressFi}}
Paikan nimi: {{name}}
Paikan leveys: {{width}}
Paikan pituus: {{length}}
Paikan varuste/säilytystapa: {{amenityFi}}

Varauksesi on voimassa {{endDateFi}}.

Jos varasit laituripaikan, saat myöhemmin postissa kausitarran ja satamakartan, jossa avainkoodi laiturin portin avaimen teettämistä varten (Otsolahden F-laiturille ei ole porttia). Kausitarra tulee kiinnittää näkyvälle paikalle veneeseen tai jos varasit talvi- tai säilytyspaikan, suojatelttaan, traileriin tai pukkiin.

Jos varasit säilytyspaikan Ämmäsmäeltä:
Ämmäsmäen kulkulätkän noudosta tulee sopia ennakkoon soittamalla numeroon 050 3209 681 arkisin kello 9-13. Kulkulätkä noudetaan Suomenojan satamasta (Hylkeenpyytäjäntie 9) maksukuittia näyttämällä.

Hallinnoi varauksiasi, veneitäsi ja omia tietojasi helposti osoitteessa https://varaukset.espoo.fi/kuntalainen/omat-tiedot.

Venesatamia koskevat sopimusehdot ja säännöt sekä muuta infoa löydät osoitteesta https://www.espoo.fi/fi/liikunta-ja-luonto/veneily.

Terveisin
Merelliset ulkoilupalvelut
venepaikat@espoo.fi

**************************************************

Bästa kund,

Den plats du har bokat för din båt, {{placeTypeSv}}, är betald och bokningen har bekräftats.{{citizenReserverSv}}

Hyresgäst:
{{reserverName}}

Platsinformation:
{{harborAddressSv}}
Platsens namn: {{name}}
Platsens bredd: {{width}}
Platsens längd: {{length}}
Platsens utrustning/förvaringssätt: {{amenitySv}}

Din bokning är i kraft till {{endDateSv}}.

Om du har bokat en bryggplats får du senare per post ett sänsongklistermärke för din båt och en hamnkarta med nyckelkodsuppgifter för att tillverka en nyckel till bryggans port (obs! Ingen port till F-bryggan i Björnviken). Klistermärket ska fästas synligt på båten eller, om du har bokat en vinter- eller förvaringsplats, på skyddstältet, trailern eller bocken.

Om du har bokat en förvaringsplats i Ämmäsmäki:
För att få tillgång till en elektronisk portnyckel ska man i förväg komma överrens om upphämtning av nyckeln på telefonnummer 050 3209 681 vardagar mellan 9-13. Upphämtning av nyckeln från Finno hamn (Hylkeenpyytäjäntie 9) får du genoma att visa kvittot på den betalda platsen.

Hantera dina bokningar, båtar och personuppgifter enkelt på https://varaukset.espoo.fi/kuntalainen/omat-tiedot.

Regler och villkor för båthamnar samt annan information hittar du på https://www.espoo.fi/sv/idrott-motion-och-natur/batliv.

Vänliga hälsningar
Marina friluftstjänster
venepaikat@espoo.fi

**************************************************

Dear customer,

The {{placeTypeEn}} you reserved for your boat has been paid, and the reservation is confirmed.{{citizenReserverEn}}

Tenant:
{{reserverName}}

Location details:
{{harborAddressEn}}
Name: {{name}}
Width: {{width}}
Length: {{length}}
Amenities/storage type: {{amenityEn}}

Your reservation is valid until {{endDateEn}}.

If you reserved a dock space, you will receive a season sticker and a harbor map by mail later, containing the key code needed for making a key for the dock gate (Otsolahti F-dock has no gate). The season sticker must be placed visibly on the boat, or if you reserved a winter or storage space, on the protective tent, trailer or stand.

If you reserved a storage space in Ämmäsmäki:
The pickup of the access badge must be arranged in advance by calling 050 3209 681 on weekdays between 9-13. The badge is picked up from the Suomenoja harbor (Hylkeenpyytäjäntie 9) by showing the payment receipt.

Manage your reservations, boats and personal details easily at https://varaukset.espoo.fi/kuntalainen/omat-tiedot.

Terms and conditions for boat harbors and additional information can be found at https://www.espoo.fi/en/sports-and-nature/boating.

Best regards
Maritime Outdoor Services
venepaikat@espoo.fi'),

('reservation_created_by_employee', 'Espoon kaupungin {{placeTypeFi}}varaus', E'Hyvä asiakas,

Sinulle on varattu Espoon kaupungin {{placeTypeFi}} {{name}}.

Lasku lähetetään osoitteeseen {{invoiceAddressFi}}. Vahvistaaksesi varauksen, maksa paikka eräpäivään mennessä. Maksamaton paikka irtisanoutuu ja se vapautuu muiden varattavaksi. Maksun saavuttua tilillemme lähetämme lisätietoa laiturin portin avaimesta sekä kausitarran postitse.

Vuokralainen:
{{reserverName}}

Paikan tiedot:
{{harborAddressFi}}
Paikan nimi: {{name}}
Paikan leveys: {{width}}
Paikan pituus: {{length}}
Paikan varuste/säilytystapa: {{amenityFi}}

Varauksesi on voimassa {{endDateFi}}.

Jos varasit laituripaikan, saat myöhemmin postissa kausitarran ja satamakartan, jossa avainkoodi laiturin portin avaimen teettämistä varten (Otsolahden F-laiturille ei ole porttia). Kausitarra tulee kiinnittää näkyvälle paikalle veneeseen tai jos varasit talvi- tai säilytyspaikan, suojatelttaan, traileriin tai pukkiin.

Jos varasit säilytyspaikan Ämmäsmäeltä:
Ämmäsmäen kulkulätkän noudosta tulee sopia ennakkoon soittamalla numeroon 050 3209 681 arkisin kello 9-13. Kulkulätkä noudetaan Suomenojan satamasta (Hylkeenpyytäjäntie 9) maksukuittia näyttämällä.

Hallinnoi varauksiasi, veneitäsi ja omia tietojasi helposti osoitteessa https://varaukset.espoo.fi/kuntalainen/omat-tiedot.

Venesatamia koskevat sopimusehdot ja säännöt sekä muuta infoa löydät osoitteesta https://www.espoo.fi/fi/liikunta-ja-luonto/veneily.

Terveisin
Merelliset ulkoilupalvelut
venepaikat@espoo.fi

**************************************************

Bästa kund,

Vi har bokat en  {{placeTypeSv}} {{name}} från Esbo stad till dig.

Fakturan skickas till {{invoiceAddressSv}}. För att bekräfta bokningen, betala platsen senast på förfallodatumet. Om betalningen uteblir annulleras bokningen och platsen blir tillgänglig för andra. När betalningen har mottagits på vårt konto skickar vi information om nyckeln till bryggans port samt klistermärket för din båt per post.

Hyresgäst:
{{reserverName}}

Platsinformation:
{{harborAddressSv}}
Platsens namn: {{name}}
Platsens bredd: {{width}}
Platsens längd: {{length}}
Platsens utrustning/förvaringssätt: {{amenitySv}}

Bokningen gäller till {{endDateSv}}.

Om du har bokat en bryggplats får du senare per post ett sänsongklistermärke för din båt och en hamnkarta med nyckelkodsuppgifter för att tillverka en nyckel till bryggans port (obs! Ingen port till F-bryggan i Björnviken). Klistermärket ska fästas synligt på båten eller, om du har bokat en vinter- eller förvaringsplats, på skyddstältet, trailern eller bocken.

Om du har bokat en förvaringsplats i Ämmäsmäki:
För att få tillgång till en elektronisk portnyckel ska man i förväg komma överrens om upphämtning av nyckeln på telefonnummer 050 3209 681 vardagar mellan 9-13. Upphämtning av nyckeln från Finno hamn (Hylkeenpyytäjäntie 9) får du genoma att visa kvittot på den betalda platsen.

Hantera dina bokningar, båtar och personuppgifter enkelt på https://varaukset.espoo.fi/kuntalainen/omat-tiedot.

Regler och villkor för båthamnar samt annan information hittar du på https://www.espoo.fi/sv/idrott-motion-och-natur/batliv.

Vänliga hälsningar
Marina friluftstjänster
venepaikat@espoo.fi

**************************************************

Dear customer,

You have reserved a {{placeTypeEn}} {{name}} from the City of Espoo.

The invoice will be sent to {{invoiceAddressEn}}. To confirm your booking, please pay for the spot before the due date. If unpaid, the booking will be canceled and made available for others. Once payment is received, we will send additional information about the dock gate key via email and the season sticker by mail.

Tenant:
{{reserverName}}

Location details:
{{harborAddressEn}}
Name: {{name}}
Width: {{width}}
Length: {{length}}
Amenities/storage type: {{amenityEn}}

Your reservation is valid until {{endDateEn}}.

If you reserved a dock space, you will receive a season sticker and a harbor map by mail later, containing the key code needed for making a key for the dock gate (Otsolahti F-dock has no gate). The season sticker must be placed visibly on the boat, or if you reserved a winter or storage space, on the protective tent, trailer or stand.

If you reserved a storage space in Ämmäsmäki:
The pickup of the access badge must be arranged in advance by calling 050 3209 681 on weekdays between 9-13. The badge is picked up from the Suomenoja harbor (Hylkeenpyytäjäntie 9) by showing the payment receipt.

Manage your reservations, boats and personal details easily at https://varaukset.espoo.fi/kuntalainen/omat-tiedot.

Terms and conditions for boat harbors and additional information can be found at https://www.espoo.fi/en/sports-and-nature/boating.

Best regards
Maritime Outdoor Services
venepaikat@espoo.fi'),

('reservation_created_by_employee_confirmed', 'Vahvistus Espoon kaupungin {{placeTypeFi}}varauksesta', E'Hyvä asiakas,

Sinulle on varattu Espoon kaupungin {{placeTypeFi}} {{name}}.

Vuokralainen:
{{reserverName}}

Paikan tiedot:
{{harborAddressFi}}
Paikan nimi: {{name}}
Paikan leveys: {{width}}
Paikan pituus: {{length}}
Paikan varuste/säilytystapa: {{amenityFi}}

Varauksesi on voimassa {{endDateFi}}.

Jos varasit laituripaikan, saat myöhemmin postissa kausitarran ja satamakartan, jossa avainkoodi laiturin portin avaimen teettämistä varten (Otsolahden F-laiturille ei ole porttia). Kausitarra tulee kiinnittää näkyvälle paikalle veneeseen tai jos varasit talvi- tai säilytyspaikan, suojatelttaan, traileriin tai pukkiin.

Jos varasit säilytyspaikan Ämmäsmäeltä:
Ämmäsmäen kulkulätkän noudosta tulee sopia ennakkoon soittamalla numeroon 050 3209 681 arkisin kello 9-13. Kulkulätkä noudetaan Suomenojan satamasta (Hylkeenpyytäjäntie 9) maksukuittia näyttämällä.

Hallinnoi varauksiasi, veneitäsi ja omia tietojasi helposti osoitteessa https://varaukset.espoo.fi/kuntalainen/omat-tiedot.

Venesatamia koskevat sopimusehdot ja säännöt sekä muuta infoa löydät osoitteesta https://www.espoo.fi/fi/liikunta-ja-luonto/veneily.

Terveisin
Merelliset ulkoilupalvelut
venepaikat@espoo.fi

**************************************************

Bästa kund,

Du har reserverat en {{placeTypeSv}} {{name}} från Esbo stad.

Hyresgäst:
{{reserverName}}

Platsinformation:
{{harborAddressSv}}
Platsens namn: {{name}}
Platsens bredd: {{width}}
Platsens längd: {{length}}
Platsens utrustning/förvaringssätt: {{amenitySv}}

Din bokning är giltig {{endDateSv}}.

Om du har bokat en bryggplats får du senare per post ett sänsongklistermärke för din båt och en hamnkarta med nyckelkodsuppgifter för att tillverka en nyckel till bryggans port (obs! Ingen port till F-bryggan i Björnviken). Klistermärket ska fästas synligt på båten eller, om du har bokat en vinter- eller förvaringsplats, på skyddstältet, trailern eller bocken.

Om du har bokat en förvaringsplats i Ämmäsmäki:
För att få tillgång till en elektronisk portnyckel ska man i förväg komma överrens om upphämtning av nyckeln på telefonnummer 050 3209 681 vardagar mellan 9-13. Upphämtning av nyckeln från Finno hamn (Hylkeenpyytäjäntie 9) får du genoma att visa kvittot på den betalda platsen.

Hantera dina bokningar, båtar och personuppgifter enkelt på https://varaukset.espoo.fi/kuntalainen/omat-tiedot.

Regler och villkor för båthamnar samt annan information hittar du på https://www.espoo.fi/sv/idrott-motion-och-natur/batliv.

Vänliga hälsningar
Marina friluftstjänster
venepaikat@espoo.fi

**************************************************

Dear customer,

You have reserved a {{placeTypeEn}} {{name}} from the City of Espoo.

Tenant:
{{reserverName}}

Location details:
{{harborAddressEn}}
Name: {{name}}
Width: {{width}}
Length: {{length}}
Amenities/storage type: {{amenityEn}}

Your reservation is valid until {{endDateEn}}.

If you reserved a dock space, you will receive a season sticker and a harbor map by mail later, containing the key code needed for making a key for the dock gate (Otsolahti F-dock has no gate). The season sticker must be placed visibly on the boat, or if you reserved a winter or storage space, on the protective tent, trailer or stand.

If you reserved a storage space in Ämmäsmäki:
The pickup of the access badge must be arranged in advance by calling 050 3209 681 on weekdays between 9-13. The badge is picked up from the Suomenoja harbor (Hylkeenpyytäjäntie 9) by showing the payment receipt.

Manage your reservations, boats and personal details easily at https://varaukset.espoo.fi/kuntalainen/omat-tiedot.

Terms and conditions for boat harbors and additional information can be found at https://www.espoo.fi/en/sports-and-nature/boating.

Best regards
Maritime Outdoor Services
venepaikat@espoo.fi'),

('reservation_renewed_by_citizen', 'Espoon kaupungin {{placeTypeFi}}varauksen jatkaminen', E'Hyvä asiakas,

Veneelle varaamasi {{placeTypeFi}} on maksettu ja varaus on vahvistettu uudelle kaudelle.{{citizenReserverFi}}

Lähetämme uuden kausitarran postitse.

Vuokralainen:
{{reserverName}}

Paikan tiedot:
{{harborAddressFi}}
Paikan nimi: {{name}}
Paikan leveys: {{width}}
Paikan pituus: {{length}}
Paikan varuste/säilytystapa: {{amenityFi}}

Varauksesi on voimassa {{endDateFi}}.

Hallinnoi varauksiasi, veneitäsi ja omia tietojasi helposti osoitteessa https://varaukset.espoo.fi/kuntalainen/omat-tiedot.

Venesatamia koskevat sopimusehdot ja säännöt sekä muuta infoa löydät osoitteesta https://www.espoo.fi/fi/liikunta-ja-luonto/veneily.

Terveisin
Merelliset ulkoilupalvelut
venepaikat@espoo.fi

**************************************************

Bästa kund,

Din bokning av {{placeTypeSv}} har betalats och bekräftats för en ny säsong.{{citizenReserverSv}}

Vi skickar säsongsklistermärket till din båt per post.

Hyresgäst:
{{reserverName}}

Platsinformation:
{{harborAddressSv}}
Platsens namn: {{name}}
Platsens bredd: {{width}}
Platsens längd: {{length}}
Platsens utrustning/förvaringssätt: {{amenitySv}}

Din bokning är giltig till {{endDateSv}}.

Hantera dina bokningar, båtar och personuppgifter enkelt på https://varaukset.espoo.fi/kuntalainen/omat-tiedot.

Regler och villkor för båthamnar samt annan information hittar du på https://www.espoo.fi/sv/idrott-motion-och-natur/batliv.

Vänliga hälsningar
Marina friluftstjänster
venepaikat@espoo.fi

**************************************************

Dear customer,

The {{placeTypeEn}} you reserved for your boat has been paid and the reservation is confirmed for the new season.{{citizenReserverEn}}

We will send the new season sticker by mail.

Tenant:
{{reserverName}}

Location details:
{{harborAddressEn}}
Name: {{name}}
Width: {{width}}
Length: {{length}}
Amenities/storage type: {{amenityEn}}

Your reservation is valid until {{endDateEn}}.

Manage your reservations, boats and personal details easily at https://varaukset.espoo.fi/kuntalainen/omat-tiedot.

Terms and conditions for boat harbors and additional information can be found at https://www.espoo.fi/en/sports-and-nature/boating.

Best regards
Maritime Outdoor Services
venepaikat@espoo.fi'),

('reservation_renewed_by_employee', 'Espoon kaupungin {{placeTypeFi}}varauksen jatkaminen', E'Hyvä asiakas,

Varaamasi Espoon kaupungin {{placeTypeFi}} on jatkettu tulevalle kaudelle.

Lasku lähetetään osoitteeseen {{invoiceAddressFi}}. Vahvistaaksesi varauksen, maksa paikka eräpäivään mennessä. Maksamaton paikka irtisanoutuu ja se vapautuu muiden varattavaksi.

Vuokralainen:
{{reserverName}}

Paikan tiedot:
{{harborAddressFi}}
Paikan nimi: {{name}}
Paikan leveys: {{width}}
Paikan pituus: {{length}}
Paikan varuste/säilytystapa: {{amenityFi}}

Varauksesi on voimassa {{endDateFi}}.

Hallinnoi varauksiasi, veneitäsi ja omia tietojasi helposti osoitteessa https://varaukset.espoo.fi/kuntalainen/omat-tiedot.

Venesatamia koskevat sopimusehdot ja säännöt sekä muuta infoa löydät osoitteesta https://www.espoo.fi/fi/liikunta-ja-luonto/veneily.

Terveisin
Merelliset ulkoilupalvelut
venepaikat@espoo.fi

**************************************************

Bästa kund,

Din bokning av {{placeTypeSv}} har förlängts för inkommande säsong.

Fakturan skickas till {{invoiceAddressSv}}. För att bekräfta bokningen, vänligen betala platsen innan förfallodatumet. En obetald plats sägs upp och blir tillgänglig för andra.

Hyresgäst:
{{reserverName}}

Platsinformation:
{{harborAddressSv}}
Platsens namn: {{name}}
Platsens bredd: {{width}}
Platsens längd: {{length}}
Platsens utrustning/förvaringssätt: {{amenitySv}}

Din bokning är giltig till {{endDateSv}}.

Hantera dina bokningar, båtar och personuppgifter enkelt på https://varaukset.espoo.fi/kuntalainen/omat-tiedot.

Regler och villkor för båthamnar samt annan information hittar du på https://www.espoo.fi/sv/idrott-motion-och-natur/batliv.

Vänliga hälsningar
Marina friluftstjänster
venepaikat@espoo.fi

**************************************************

Dear customer,

Your reservation for {{placeTypeEn}} has been extended for a new season.

The invoice will be sent to {{invoiceAddressEn}}. To confirm your reservation, please pay for the spot before the due date. An unpaid spot will be canceled and made available for others.

Tenant:
{{reserverName}}

Location details:
{{harborAddressEn}}
Name: {{name}}
Width: {{width}}
Length: {{length}}
Amenities/storage type: {{amenityEn}}

Your reservation is valid until {{endDateEn}}.

Manage your reservations, boats and personal details easily at https://varaukset.espoo.fi/kuntalainen/omat-tiedot.

Terms and conditions for boat harbors and additional information can be found at https://www.espoo.fi/en/sports-and-nature/boating.

Best regards
Maritime Outdoor Services
venepaikat@espoo.fi'),

('reservation_renewed_by_employee_confirmed', 'Espoon kaupungin {{placeTypeFi}}varauksen jatkaminen', E'Hyvä asiakas,

Varaamasi Espoon kaupungin {{placeTypeFi}} on jatkettu tulevalle kaudelle.

Lähetämme uuden kausitarran postitse.

Vuokralainen:
{{reserverName}}

Paikan tiedot:
{{harborAddressFi}}
Paikan nimi: {{name}}
Paikan leveys: {{width}}
Paikan pituus: {{length}}
Paikan varuste/säilytystapa: {{amenityFi}}

Varauksesi on voimassa {{endDateFi}}.

Hallinnoi varauksiasi, veneitäsi ja omia tietojasi helposti osoitteessa https://varaukset.espoo.fi/kuntalainen/omat-tiedot.

Venesatamia koskevat sopimusehdot ja säännöt sekä muuta infoa löydät osoitteesta https://www.espoo.fi/fi/liikunta-ja-luonto/veneily.

Terveisin
Merelliset ulkoilupalvelut
venepaikat@espoo.fi

**************************************************

Bästa kund,

Din bokning av {{placeTypeSv}} har förlängts för inkommande säsong.

Vi skickar säsongsklistermärket till din båt per post.

Hyresgäst:
{{reserverName}}

Platsinformation:
{{harborAddressSv}}
Platsens namn: {{name}}
Platsens bredd: {{width}}
Platsens längd: {{length}}
Platsens utrustning/förvaringssätt: {{amenitySv}}

Din bokning är giltig till {{endDateSv}}.

Hantera dina bokningar, båtar och personuppgifter enkelt på https://varaukset.espoo.fi/kuntalainen/omat-tiedot.

Regler och villkor för båthamnar samt annan information hittar du på https://www.espoo.fi/sv/idrott-motion-och-natur/batliv.

Vänliga hälsningar
Marina friluftstjänster
venepaikat@espoo.fi

**************************************************

Dear customer,

Your reservation for {{placeTypeEn}} has been extended for a new season.

We will send a new season sticker by mail.

Tenant:
{{reserverName}}

Location details:
{{harborAddressEn}}
Name: {{name}}
Width: {{width}}
Length: {{length}}
Amenities/storage type: {{amenityEn}}

Your reservation is valid until {{endDateEn}}.

Manage your reservations, boats and personal details easily at https://varaukset.espoo.fi/kuntalainen/omat-tiedot.

Terms and conditions for boat harbors and additional information can be found at https://www.espoo.fi/en/sports-and-nature/boating.

Best regards
Maritime Outdoor Services
venepaikat@espoo.fi'),

('reservation_switched_by_citizen', 'Vahvistus Espoon kaupungin {{placeTypeFi}}varauksen vaihdosta', E'Hyvä asiakas,

Olet vaihtanut Espoon kaupungilta vuokraamaasi {{placeTypeFi}}a.

HUOM! Vanha paikka päättyy heti vaihdon yhteydessä ja vene tulee siirtää pois vanhalta paikalta, sillä vanha paikkasi on vapautunut seuraavalle vuokrattavaksi. Venepaikkasi vuokrakausi säilyy ennallaan.

Jos vaihdoit laituripaikkaa saat myöhemmin postissa kausitarran ja satamakartan, jossa avainkoodi laiturin portin avaimen teettämistä varten jos tarvitset uuden avaimen (Otsolahden F-laiturille ei ole porttia). Kausitarra tulee kiinnittää näkyvälle paikalle veneeseen tai jos varasit talvi- tai säilytyspaikan, suojatelttaan, traileriin tai pukkiin.{{citizenReserverFi}}

Uusi paikka:

Vuokralainen:
{{reserverName}}

Paikan tiedot:
{{harborAddressFi}}
Paikan nimi: {{name}}
Paikan leveys: {{width}}
Paikan pituus: {{length}}
Paikan varuste/säilytystapa: {{amenityFi}}

Varauksesi on voimassa {{endDateFi}}.

Hallinnoi varauksiasi, veneitäsi ja omia tietojasi helposti osoitteessa https://varaukset.espoo.fi/kuntalainen/omat-tiedot.

Venesatamia koskevat sopimusehdot ja säännöt sekä muuta infoa löydät osoitteesta https://www.espoo.fi/fi/liikunta-ja-luonto/veneily.

Terveisin
Merelliset ulkoilupalvelut
venepaikat@espoo.fi

**************************************************

Bästa kund,

Du har bytt din hyrda {{placeTypeSv}} i Esbo stad.

OBS! Din tidigare plats avslutas omedelbart vid bytet, och båten måste flyttas bort eftersom platsen är tillgänglig för en ny hyresgäst. Hyresperioden för din båtplats förblir oförändrad.

Om du bytte bryggplats får du per post ett nytt säsongsklistermärke till din båt samt nyckelkod till bryggans port ifall du behöver en ny nyckel. {{citizenReserverSv}}

Ny plats:

Hyresgäst:
{{reserverName}}

Platsinformation:
{{harborAddressSv}}
Platsens namn: {{name}}
Platsens bredd: {{width}}
Platsens längd: {{length}}
Platsens utrustning/förvaringssätt: {{amenitySv}}

Din bokning är giltig till {{endDateSv}}.

Hantera dina bokningar, båtar och personuppgifter enkelt på https://varaukset.espoo.fi/kuntalainen/omat-tiedot.

Regler och villkor för båthamnar samt annan information hittar du på https://www.espoo.fi/sv/idrott-motion-och-natur/batliv.

Vänliga hälsningar
Marina friluftstjänster
venepaikat@espoo.fi

**************************************************

Dear customer,

You have changed your rented {{placeTypeEn}} in the City of Espoo.

NOTICE! Your previous spot ends immediately upon the switch, and the boat must be moved from the old location as it is now available for a new tenant. Your rental period remains unchanged.

If you switched dock space:
To receive a new season sticker and key code for your new spot, please contact us via email at venepaikat@espoo.fi or by phone at 09 81658984 on Mon and Wed 12:30-15 and Thu 9-11.{{citizenReserverEn}}

New location:

Tenant:
{{reserverName}}

Location details:
{{harborAddressEn}}
Name: {{name}}
Width: {{width}}
Length: {{length}}
Amenities/storage type: {{amenityEn}}

Your reservation is valid until {{endDateEn}}.

Manage your reservations, boats and personal details easily at https://varaukset.espoo.fi/kuntalainen/omat-tiedot.

Terms and conditions for boat harbors and additional information can be found at https://www.espoo.fi/en/sports-and-nature/boating.

Best regards
Maritime Outdoor Services
venepaikat@espoo.fi'),

('reservation_termination_by_citizen', 'Vahvistus Espoon kaupungin venepaikan irtisanomisesta', E'Hei!,

Espoon kaupungin {{placeTypeFi}} {{name}} on irtisanottu.

Irtisanoja: {{terminatorName}}

Paikan vuokraaja: {{reserverName}}

Paikan tulee olla tyhjä ja siivottu seuraavaa vuokralaista varten.

Jos irtisanoit laituri- tai traileripaikan, tulee se tyhjentää välittömästi.
Talvi- ja Ämmäsmäen säilytyspaikan voit pitää vielä kuluvan kauden loppuun asti.

Jos irtisanoit Ämmäsmäen säilytyspaikan:
Ämmäsmäen kulkulätkä tulee palauttaa Suomenojan satamaan (Hylkeenpyytäjäntie 9).

Mikäli et ole irtisanonut paikkaasi, ota yhteyttä sähköpostilla venepaikat@espoo.fi

Terveisin,

Merelliset ulkoilupalvelut
venepaikat@espoo.fi

**************************************************

Hej!

{{placeTypeSv}} {{name}} i Esbo stad har sagts upp.

Uppsägare: {{terminatorName}}

Hyresgäst: {{reserverName}}

Platsen måste vara tom och städad för nästa hyresgäst.

Om du har sagt upp en brygg- eller trailerplats måste den tömmas omedelbart.
Vinter- och Ämmäsmäki-förvaringsplats kan användas till slutet av pågående säsong.

Om du har sagt upp en förvaringsplats i Ämmäsmäki:
Elektroniska nyckeln måste returneras till Finno hamn (Hylkeenpyytäjäntie 9).

Om du inte har sagt upp din plats, vänligen kontakta oss via e-post på venepaikat@espoo.fi

Vänliga hälsningar,

Marina friluftstjänster
venepaikat@espoo.fi

**************************************************

Hello!,

The {{placeTypeEn}} {{name}} in the City of Espoo has been terminated.

Terminator: {{terminatorName}}

Tenant: {{reserverName}}

The space must be empty and cleaned for the next tenant.

If you have terminated a dock or trailer spot, it must be cleared immediately.
Winter and Ämmäsmäki storage spaces can be used until the end of the current season.

If you have terminated a storage space in Ämmäsmäki:
The access badge must be returned to the Finno harbor (Hylkeenpyytäjäntie 9).

If you have not terminated your place, please contact us via email at venepaikat@espoo.fi

Best regards,

Maritime Outdoor Services
venepaikat@espoo.fi'),

('reservation_termination_by_citizen_to_employee', 'Espoon kaupungin {{placeTypeFi}} {{name}} irtisanottu, asiakas: {{reserverName}}', E'Hei!

Espoon kaupungin {{placeTypeFi}} {{name}} on irtisanottu {{time}}

Paikan vuokraaja: {{reserverName}}
Sähköposti: {{reserverEmail}}

Irtisanoja:
Nimi: {{terminatorName}}
Sähköposti: {{terminatorEmail}}
Puhelinnumero: {{terminatorPhone}}'),

('fixed_term_reservation_expiring', 'Espoon kaupungin {{placeTypeFi}}varauksesi on päättymässä', E'Hyvä asiakas,

Espoon kaupungin {{placeTypeFi}} {{name}} varausaika on päättymässä {{endDate}}.

Paikan vuokraaja: {{reserverName}}

Vuokrasopimuksen päätyttyä on paikan oltava tyhjennetty ja siivottu.

Ämmäsmäen säilytyspaikan varauksen päätyttyä kulkulätkä tulee palauttaa Suomenojan satamaan (Hylkeenpyytäjäntie 9).

Voit tarkistaa paikkojen varausajat ja tehdä uuden varauksen osoitteessa https://varaukset.espoo.fi.

Terveisin
Merelliset ulkoilupalvelut
venepaikat@espoo.fi

**************************************************

Bästa kund,

Hyresperioden för {{placeTypeSv}} {{name}} i Esbo stad närmar sig sitt slut {{endDate}}.

Hyresgäst: {{reserverName}}

När hyresperioden är slut måste platsen vara tömd och städad.

Om du har hyrt en förvaringsplats i Ämmäsmäki måste elektroniska returneras till Finno hamn (Hylkeenpyytäjäntie 9).

Du kan kontrollera bokningsperioder och göra en ny bokning på https://varaukset.espoo.fi.

Vänliga hälsningar
Marina friluftstjänster
venepaikat@espoo.fi

**************************************************

Dear customer,

Your reservation for {{placeTypeEn}} {{name}} in the City of Espoo is coming to an end on {{endDate}}.

Tenant: {{reserverName}}

Once the rental period ends, the space must be emptied and cleaned.

If you rented a storage space in Ämmäsmäki, the access badge must be returned to the Finno harbor (Hylkeenpyytäjäntie 9).

You can check reservation periods and make a new reservation at https://varaukset.espoo.fi.

Best regards
Maritime Outdoor Services
venepaikat@espoo.fi'),

    ('boat_reservation_renew_reminder', 'Varmista Espoon kaupungin {{placeTypeFi}}si jatko ensi kaudelle nyt', E'Hyvä asiakas,

On aika jatkaa {{placeTypeFi}}si varausta ensi kaudelle.

Varmistat paikkasi varauksen maksamalla kausimaksun.
Teet sen helposti osoitteessa https://varaukset.espoo.fi/kuntalainen/omat-tiedot. Kausimaksun voit maksaa oman varauksen kautta. Maksamiseen tarvitset verkkopankkitunnukset.

Jos kausimaksua ei ole maksettu {{endDate}} mennessä, paikka irtisanoutuu ja vapautuu.

Määräajat paikan jatkamiselle:
Laituripaikka: 7.–31.1.
Traileripaikka: 1.–30.4.
Talvipaikka: 15.8.–14.9.
Säilytyspaikka Ämmäsmäellä: 15.8.–14.9.

Jatkettava paikka:

Paikan vuokraaja: {{reserverName}}

Paikan tiedot:
{{harborAddressFi}}
Paikan nimi: {{name}}
Paikan leveys: {{width}}
Paikan pituus: {{length}}
Paikan varuste/säilytystapa: {{amenityFi}}

Huomioi, mikäli kotikuntasi ei enää ole Espoo, et välttämättä ole oikeutettu jatkamaan paikkaasi.

Jos kausimaksun maksaminen ei onnistu, ota yhteyttä sähköpostilla venepaikat@espoo.fi tai puhelimitse 09 81658984. Puhelinajat löytyvät verkkosivuiltamme.

Voit myös halutessasi vaihtaa nykyisen paikkasi toiseen paikkaan omien tietojen kautta. Paikan vaihto vahvistuu kun maksat uuden paikan kausimaksun.

Venesatamia koskevat sopimusehdot ja säännöt sekä muuta infoa löydät osoitteesta https://www.espoo.fi/fi/liikunta-ja-luonto/veneily.

Terveisin
Merelliset ulkoilupalvelut
venepaikat@espoo.fi

**************************************************

Bästa kund,

Det är dags att förnya din bokning för {{placeTypeSv}} inför nästa säsong.

Säkra din plats genom att betala säsongsavgiften.
Du gör det enkelt på https://varaukset.espoo.fi/kuntalainen/omat-tiedot. Du kan förnya din plats via din bokning. För betalning krävs bankkoder.

Om säsongsavgiften inte har betalats senast {{endDate}}, sägs platsen upp och blir tillgänglig för andra.

Tider för förnyelse av plats:
Bryggplats: 7.–31.1.
Trailerplats: 1.–30.4.
Vinterplats: 15.8.–14.9.
Förvaringsplats i Ämmäsmäki: 15.8.–14.9.

Plats att förnya:

Hyresgäst: {{reserverName}}

Platsinformation:
{{harborAddressSv}}
Platsens namn: {{name}}
Platsens bredd: {{width}}
Platsens längd: {{length}}
Platsens utrustning/förvaringssätt: {{amenitySv}}

Observera att om din hemkommun inte längre är Esbo kan du eventuellt inte förnya din plats.

Om säsongsavgiften inte kan betalas, kontakta oss via e-post på venepaikat@espoo.fi eller per telefon 09 81658984. Telefontider finns på vår webbplats.

Du kan också byta din nuvarande plats till en annan via dina egna sidor. Bytet bekräftas genom att betala säsongsavgiften för den nya platsen.

Regler och villkor för båthamnar samt annan information hittar du på https://www.espoo.fi/sv/idrott-motion-och-natur/batliv.

Vänliga hälsningar
Marina friluftstjänster
venepaikat@espoo.fi

**************************************************

Dear customer,

It is time to renew your reservation for {{placeTypeEn}} for the next season.

Secure your spot by paying the seasonal fee.
You can do this easily at https://varaukset.espoo.fi/kuntalainen/omat-tiedot. You will find the seasonal payment button under your reservation details. Online banking credentials are required for payment.

If the seasonal fee is not paid by {{endDate}}, your spot will be canceled and made available for others.

Deadlines for renewing a spot:
Dock space: 7.–31.1.
Trailer space: 1.–30.4.
Winter storage: 15.8.–14.9.
Storage space in Ämmäsmäki: 15.8.–14.9.

Spot to be renewed:

Tenant: {{reserverName}}

Location details:
{{harborAddressEn}}
Name: {{name}}
Width: {{width}}
Length: {{length}}
Amenities/storage type: {{amenityEn}}

Please note that if your home municipality is no longer Espoo, you may not be eligible to renew your spot.

If you are unable to make the payment, please contact us via email at venepaikat@espoo.fi or by phone at 09 81658984. Phone hours can be found on our website.

You may also choose to switch your current spot to another through your profile page. The switch is confirmed by paying the seasonal fee for the new spot.

Terms and conditions for boat harbors and additional information can be found at https://www.espoo.fi/en/sports-and-nature/boating.

Best regards
Maritime Outdoor Services
venepaikat@espoo.fi'),

    ('marine_employee_reservation_termination_custom_message', 'Ilmoitus sopimuksen irtisanomisesta', E'Hyvä asiakas,

Venepaikka: {{harbor}} {{place}} on irtisanottu virkailijan toimesta.

Irtisanominen astuu voimaan *xx.xx.xxxx*.
Irtisanomisen syy: *xxxxxx*

Pyydämme teitä ystävällisesti siirtämään veneenne pois nykyiseltä paikaltaan *xx.xx.xxxx* mennessä.

Mikäli teillä on kysyttävää, ota yhteyttä sähköpostilla {{employeeEmail}} tai puhelimitse 09 81658984 ma ja ke klo 12.30-15 ja to 9-11.

Terveisin
Merellinen ulkoilu
{{employeeEmail}}'),
    ('expired_reservation', 'Espoon kaupungin venepaikan vuokrasopimus on päättynyt', E'Hyvä asiakas,

Espoon kaupungin {{placeTypeFi}} {{name}} vuokrasopimus on päättynyt.

Paikan vuokraaja: {{reserverName}}

Vuokrasopimuksen päätyttyä on paikan oltava tyhjennetty ja siivottu.

Voit tarkistaa paikkojen varausajat ja tehdä uuden varauksen osoitteessa https://varaukset.espoo.fi.

Terveisin
Merelliset ulkoilupalvelut
venepaikat@espoo.fi

**************************************************

Bästa kund,

Hyresavtalet för {{placeTypeSv}} {{name}} har upphört.

Hyresgäst: {{reserverName}}

När hyresperioden är slut måste platsen vara tömd och städad.

Du kan kontrollera bokningsperioder och göra en ny bokning på https://varaukset.espoo.fi.

Vänliga hälsningar
Marina friluftstjänster
venepaikat@espoo.fi

**************************************************

Dear customer,

The rental agreement for the {{placeTypeEn}} {{name}} has ended.

Tenant: {{reserverName}}

Once the rental period ends, the space must be emptied and cleaned.

You can check reservation periods and make a new reservation at https://varaukset.espoo.fi.

Best regards
Maritime Outdoor Services
venepaikat@espoo.fi'),
('storage_place_expired_to_employee', 'Säilytyspaikan {{name}} vuokrasopimus on päättynyt, asiakas: {{reserverName}}', E'Säilytyspaikan {{name}} vuokrasopimus on päättynyt {{endDate}}, asiakas ei ole maksanut kausimaksua uudelle kaudelle eräpäivään mennessä.\n\nAsiakas:\n{{reserverName}}
\nSähköposti: {{reserverEmail}}')
ON CONFLICT (id) DO NOTHING;

INSERT INTO citizen (id, national_id, first_name, last_name)
VALUES
  ('62d90eed-4ea3-4446-8023-8dad9c01dd34', '010106A957V', 'Mikko', 'Virtanen'),
  ('f5d377ea-5547-11ef-a1c7-7f2b94cf9afd', '150499-911U', 'Leo', 'Korhonen'),
  ('509edb00-5549-11ef-a1c7-776e76028a49', '031298-988S', 'Olivia', 'Virtanen'),
  ('1128bd21-fbbc-4e9a-8658-dc2044a64a58', '290991-993F', 'Marko', 'Kuusinen'),
  ('82722a75-793a-4cbe-a3d9-a3043f2f5731', '111275-180K', 'Jorma', 'Pulkkinen')
ON CONFLICT (id) DO NOTHING;

INSERT INTO citizen (id, national_id, first_name, last_name, data_protection)
VALUES
    ('6a7b1b37-ace5-4992-878b-1fa0cd52e4e7', '250695-7378', 'Turvald', 'Kieltoinen', true)
ON CONFLICT (id) DO NOTHING;

INSERT INTO organization (id, business_id, billing_name, billing_street_address, billing_postal_code, billing_post_office)
VALUES
  ('8b220a43-86a0-4054-96f6-d29a5aba17e7', '1015253-4', 'Espoon Pursiseura laskutus', 'Laskutustie 19', '02130', 'Espoo')
ON CONFLICT (id) DO NOTHING;

INSERT INTO organization_member (organization_id, member_id)
VALUES
  ('8b220a43-86a0-4054-96f6-d29a5aba17e7', '509edb00-5549-11ef-a1c7-776e76028a49');

INSERT INTO citizen_memo (created_by, category, reserver_id, content)
VALUES
  ('94833b54-132b-4ab8-b841-60df45809b3e', 'Marine', 'f5d377ea-5547-11ef-a1c7-7f2b94cf9afd', 'Kiva viesti');

INSERT INTO sent_message (provider_id, created, sent_at, type, status, sender_id, sender_address, recipient_id, recipient_address, subject, body)
VALUES
    ('1000', '2024-09-01 13:01:20', '2024-09-01 13:01:21', 'Email', 'Sent', '94833b54-132b-4ab8-b841-60df45809b3e', 'ville@noreplytest.fi', 'f5d377ea-5547-11ef-a1c7-7f2b94cf9afd', 'leo@gmail.com', 'Käyttöveden katko', 'Haukilahden satamassa on käyttöveden katko 2.9.2024 klo 12-14. Pahoittelemme häiriötä.'),
    ('1000', '2024-09-01 13:01:20', '2024-09-01 13:01:21', 'Email', 'Sent', '94833b54-132b-4ab8-b841-60df45809b3e', 'ville@noreplytest.fi', '8b220a43-86a0-4054-96f6-d29a5aba17e7', 'eps@noreplytest.fi', 'Käyttöveden katko', 'Haukilahden satamassa on käyttöveden katko 2.9.2024 klo 12-14. Pahoittelemme häiriötä.');


INSERT INTO boat (registration_code, reserver_id, name, width_cm, length_cm, depth_cm, weight_kg, type, other_identification, extra_information, ownership, deleted_at)
VALUES
    ('A1234', 'f5d377ea-5547-11ef-a1c7-7f2b94cf9afd', 'Leon vene', 120, 400, 20, 180, 'OutboardMotor', 'Terhi 400', '', 'Owner', null),
    ('B4321', '509edb00-5549-11ef-a1c7-776e76028a49', 'Olivian vene', 160, 800, 50, 420, 'Sailboat', 'Swan', '', 'Owner', null),
    ('C1234', 'f5d377ea-5547-11ef-a1c7-7f2b94cf9afd', 'Leon toinen liian iso vene', 1200, 4000, 20, 180, 'OutboardMotor', 'Ismo 400', '', 'FutureOwner', null),
    ('D1234', 'f5d377ea-5547-11ef-a1c7-7f2b94cf9afd', 'Leon poistettu vene', 120, 400, 20, 180, 'OutboardMotor', 'Delmo 600', '', 'Owner', '2024-04-01T00:00:00'),
    ('W9876', '8b220a43-86a0-4054-96f6-d29a5aba17e7', 'Espoon lohi', 120, 400, 20, 180, 'OutboardMotor', 'Buster mini', '', 'Owner', null),
    ('W9876', '8b220a43-86a0-4054-96f6-d29a5aba17e7', 'Espoon kuha', 120, 400, 20, 180, 'OutboardMotor', 'Buster mini', '', 'Owner', null),
    ('W9876', '82722a75-793a-4cbe-a3d9-a3043f2f5731', 'Ruutuässä', 200, 300, 120, 3000, 'Sailboat', 'Swan 45', '', 'Owner', null);


INSERT INTO boat_space (type, location_id, price_id, section, place_number, amenity, width_cm, length_cm, is_active) VALUES
    ('Slip', '1', '2', 'B', '1', 'Beam', '250', '450', true),
    ('Slip', '1', '2', 'B', '3', 'Beam', '250', '450', true),
    ('Slip', '1', '2', 'B', '5', 'Beam', '250', '450', true),
    ('Storage', '1', '2', 'B', '7', 'Trailer', '250', '450', true),
    ('Storage', '1', '2', 'B', '9', 'Trailer', '250', '450', true),
    ('Storage', '1', '2', 'B', '11', 'Buck', '250', '450', true),
    ('Winter', '3', '2', 'B', '13', 'None', '250', '450', true),
    ('Winter', '3', '2', 'B', '15', 'None', '250', '450', true),
    ('Winter', '3', '2', 'B', '17', 'None', '250', '450', true),
    ('Winter', '3', '2', 'B', '19', 'None', '250', '450', true),
    ('Winter', '3', '2', 'B', '21', 'None', '275', '550', true),
    ('Winter', '3', '2', 'B', '23', 'None', '275', '550', true),
    ('Winter', '3', '2', 'B', '25', 'None', '275', '550', true),
    ('Winter', '3', '2', 'B', '27', 'None', '275', '550', true),
    ('Winter', '3', '2', 'B', '29', 'None', '275', '550', true),
    ('Winter', '3', '2', 'B', '31', 'None', '275', '550', true),
    ('Winter', '3', '2', 'B', '33', 'None', '275', '550', true),
    ('Winter', '3', '2', 'B', '35', 'None', '275', '550', true),
    ('Winter', '3', '2', 'B', '37', 'None', '275', '550', true),
    ('Winter', '3', '2', 'B', '39', 'None', '275', '550', true),
    ('Winter', '3', '2', 'B', '41', 'None', '275', '550', true),
    ('Winter', '3', '2', 'B', '43', 'None', '275', '550', true),
    ('Winter', '3', '2', 'B', '45', 'None', '275', '550', true),
    ('Winter', '3', '2', 'B', '47', 'None', '275', '550', true),
    ('Winter', '3', '2', 'B', '49', 'None', '275', '550', true),
    ('Winter', '3', '2', 'B', '51', 'None', '275', '550', true),
    ('Winter', '3', '2', 'B', '53', 'None', '275', '550', true),
    ('Winter', '3', '2', 'B', '55', 'None', '275', '550', true),
    ('Winter', '3', '2', 'B', '57', 'None', '275', '550', true),
    ('Slip', '1', '2', 'B', '59', 'Beam', '275', '550', true),
    ('Slip', '1', '2', 'B', '61', 'Beam', '275', '550', false),
    ('Slip', '1', '2', 'B', '63', 'Beam', '275', '550', false),
    ('Slip', '1', '2', 'B', '65', 'Beam', '275', '550', false),
    ('Slip', '1', '2', 'B', '67', 'Beam', '275', '550', false),
    ('Slip', '1', '2', 'B', '69', 'Beam', '275', '550', true),
    ('Slip', '1', '2', 'B', '71', 'Beam', '275', '550', true),
    ('Slip', '1', '2', 'B', '73', 'Beam', '275', '550', true),
    ('Slip', '1', '4', 'B', '231', 'WalkBeam', '350', '900', true),
    ('Slip', '1', '4', 'B', '233', 'WalkBeam', '350', '900', true),
    ('Slip', '1', '4', 'B', '235', 'WalkBeam', '350', '900', true),
    ('Slip', '1', '4', 'B', '237', 'WalkBeam', '350', '900', true),
    ('Slip', '1', '4', 'B', '239', 'WalkBeam', '350', '900', true),
    ('Slip', '1', '4', 'B', '241', 'WalkBeam', '350', '900', true),
    ('Slip', '1', '4', 'B', '243', 'WalkBeam', '350', '900', true),
    ('Slip', '1', '4', 'B', '245', 'WalkBeam', '350', '900', true),
    ('Slip', '1', '4', 'B', '247', 'WalkBeam', '350', '900', true),
    ('Slip', '1', '4', 'B', '249', 'WalkBeam', '350', '900', true),
    ('Slip', '1', '4', 'B', '251', 'WalkBeam', '350', '900', true),
    ('Slip', '1', '4', 'B', '253', 'WalkBeam', '350', '900', true),
    ('Slip', '1', '4', 'B', '255', 'WalkBeam', '350', '900', true),
    ('Slip', '1', '4', 'B', '257', 'WalkBeam', '350', '900', true),
    ('Slip', '1', '3', 'B', '310', 'Beam', '290', '650', true),
    ('Slip', '1', '3', 'B', '311', 'Beam', '300', '650', true),
    ('Slip', '1', '3', 'B', '312', 'Beam', '300', '650', true),
    ('Slip', '1', '3', 'B', '313', 'Beam', '300', '650', true),
    ('Slip', '1', '4', 'B', '314', 'Beam', '350', '650', true),
    ('Slip', '1', '4', 'B', '315', 'Beam', '350', '650', true),
    ('Slip', '1', '3', 'B', '316', 'Beam', '300', '650', true),
    ('Slip', '1', '3', 'B', '317', 'Beam', '300', '650', true),
    ('Slip', '1', '3', 'B', '318', 'Beam', '300', '650', true),
    ('Slip', '1', '3', 'B', '319', 'Beam', '290', '650', true),
    ('Slip', '1', '2', 'D', '1', 'Beam', '250', '600', true),
    ('Slip', '1', '2', 'D', '2', 'Beam', '250', '700', true),
    ('Slip', '1', '3', 'D', '3', 'Beam', '290', '600', true),
    ('Slip', '1', '3', 'D', '4', 'Beam', '290', '700', true),
    ('Slip', '1', '3', 'D', '5', 'Beam', '290', '600', true),
    ('Slip', '1', '3', 'D', '6', 'Beam', '290', '700', true),
    ('Slip', '1', '3', 'D', '7', 'Beam', '290', '600', true),
    ('Slip', '1', '3', 'D', '8', 'Beam', '290', '700', true),
    ('Slip', '1', '3', 'D', '9', 'Beam', '290', '600', true),
    ('Slip', '1', '3', 'D', '10', 'Beam', '290', '600', true),
    ('Slip', '1', '3', 'D', '11', 'Beam', '285', '600', true),
    ('Slip', '1', '3', 'D', '12', 'Beam', '290', '700', true),
    ('Slip', '1', '4', 'D', '13', 'Beam', '350', '600', true),
    ('Slip', '1', '4', 'D', '14', 'Beam', '350', '700', true),
    ('Slip', '1', '3', 'D', '15', 'Beam', '290', '600', true),
    ('Slip', '1', '2', 'D', '16', 'Beam', '280', '700', true),
    ('Slip', '1', '3', 'D', '17', 'Beam', '295', '600', true),
    ('Slip', '1', '3', 'D', '18', 'Beam', '310', '700', true),
    ('Slip', '1', '3', 'D', '19', 'Beam', '290', '600', true),
    ('Slip', '1', '3', 'D', '20', 'Beam', '300', '700', true),
    ('Slip', '1', '5', 'D', '22', 'Beam', '405', '700', true),
    ('Slip', '1', '5', 'D', '23', 'Beam', '390', '600', true),
    ('Slip', '1', '5', 'D', '24', 'Beam', '390', '700', true),
    ('Slip', '1', '2', 'E', '1', 'WalkBeam', '280', '800', true),
    ('Slip', '1', '2', 'E', '2', 'WalkBeam', '280', '800', true),
    ('Slip', '1', '3', 'E', '3', 'WalkBeam', '300', '800', true),
    ('Slip', '1', '3', 'E', '4', 'WalkBeam', '300', '800', true),
    ('Slip', '1', '3', 'E', '5', 'WalkBeam', '300', '800', true),
    ('Slip', '1', '3', 'E', '6', 'WalkBeam', '300', '800', true),
    ('Slip', '1', '3', 'E', '8', 'WalkBeam', '330', '800', true),
    ('Slip', '1', '3', 'E', '9', 'WalkBeam', '330', '900', true),
    ('Slip', '1', '3', 'E', '10', 'WalkBeam', '330', '900', true),
    ('Slip', '1', '3', 'E', '11', 'WalkBeam', '330', '900', true),
    ('Slip', '1', '3', 'E', '12', 'WalkBeam', '330', '900', true),
    ('Slip', '1', '3', 'E', '13', 'WalkBeam', '330', '900', true),
    ('Slip', '1', '3', 'E', '14', 'WalkBeam', '330', '900', true),
    ('Slip', '1', '3', 'E', '15', 'WalkBeam', '330', '900', true),
    ('Slip', '1', '3', 'E', '16', 'WalkBeam', '330', '900', true),
    ('Slip', '1', '3', 'E', '17', 'WalkBeam', '330', '900', true),
    ('Slip', '1', '3', 'E', '18', 'WalkBeam', '330', '900', true),
    ('Slip', '1', '3', 'E', '19', 'WalkBeam', '330', '900', true),
    ('Slip', '1', '3', 'E', '20', 'WalkBeam', '330', '900', true),
    ('Slip', '1', '4', 'E', '21', 'WalkBeam', '355', '900', true),
    ('Slip', '1', '4', 'E', '22', 'WalkBeam', '355', '900', true),
    ('Slip', '1', '4', 'E', '23', 'WalkBeam', '355', '900', true),
    ('Slip', '1', '4', 'E', '24', 'WalkBeam', '355', '900', true),
    ('Slip', '1', '2', 'G', '3', 'Beam', '280', '700', true),
    ('Slip', '1', '2', 'G', '5', 'Beam', '280', '700', true),
    ('Slip', '1', '2', 'G', '6', 'Beam', '280', '600', true),
    ('Slip', '1', '2', 'G', '7', 'Beam', '280', '700', true),
    ('Slip', '1', '3', 'G', '9', 'Beam', '300', '700', true),
    ('Slip', '1', '3', 'G', '10', 'Beam', '300', '700', true),
    ('Slip', '1', '3', 'G', '11', 'Beam', '300', '700', true),
    ('Slip', '1', '3', 'G', '12', 'Beam', '300', '700', true),
    ('Slip', '1', '3', 'G', '13', 'Beam', '300', '700', true),
    ('Slip', '1', '3', 'G', '14', 'Beam', '300', '700', true),
    ('Slip', '1', '3', 'G', '15', 'Beam', '300', '700', true),
    ('Slip', '1', '3', 'G', '16', 'Beam', '300', '700', true),
    ('Slip', '1', '3', 'G', '17', 'Beam', '300', '700', true),
    ('Slip', '1', '3', 'G', '18', 'Beam', '300', '700', true),
    ('Slip', '1', '3', 'G', '19', 'Beam', '300', '700', true),
    ('Slip', '1', '3', 'G', '20', 'Beam', '300', '700', true),
    ('Slip', '1', '3', 'G', '22', 'Beam', '300', '700', true),
    ('Slip', '1', '3', 'G', '23', 'Beam', '300', '700', true),
    ('Slip', '1', '3', 'G', '24', 'Beam', '300', '700', true),
    ('Slip', '1', '3', 'G', '25', 'Beam', '300', '700', true),
    ('Slip', '1', '3', 'G', '26', 'Beam', '300', '700', true),
    ('Slip', '1', '3', 'G', '27', 'Beam', '300', '700', true),
    ('Slip', '1', '3', 'G', '28', 'Beam', '300', '700', true),
    ('Slip', '1', '3', 'G', '30', 'WalkBeam', '330', '900', true),
    ('Slip', '1', '4', 'G', '31', 'WalkBeam', '380', '900', true),
    ('Slip', '1', '4', 'G', '32', 'WalkBeam', '380', '900', true),
    ('Slip', '1', '5', 'G', '33', 'WalkBeam', '430', '1000', true),
    ('Slip', '1', '5', 'G', '34', 'WalkBeam', '430', '1000', true),
    ('Slip', '1', '5', 'G', '35', 'WalkBeam', '430', '1000', true),
    ('Slip', '1', '5', 'G', '36', 'WalkBeam', '430', '1000', true),
    ('Slip', '1', '2', 'H', '1', 'Beam', '260', '750', true),
    ('Slip', '1', '2', 'H', '2', 'Beam', '260', '750', true),
    ('Slip', '1', '3', 'H', '3', 'Beam', '300', '750', true),
    ('Slip', '1', '3', 'H', '4', 'Beam', '300', '750', true),
    ('Slip', '1', '3', 'H', '5', 'Beam', '300', '750', true),
    ('Slip', '1', '3', 'H', '6', 'Beam', '300', '750', true),
    ('Slip', '1', '3', 'H', '7', 'Beam', '300', '750', true),
    ('Slip', '1', '3', 'H', '8', 'Beam', '300', '750', true),
    ('Slip', '1', '3', 'H', '9', 'Beam', '300', '750', true),
    ('Slip', '1', '3', 'H', '10', 'Beam', '300', '750', true),
    ('Slip', '1', '3', 'H', '11', 'Beam', '300', '750', true),
    ('Slip', '1', '3', 'H', '12', 'Beam', '300', '750', true),
    ('Slip', '1', '3', 'H', '13', 'Beam', '300', '750', true),
    ('Slip', '1', '3', 'H', '14', 'Beam', '300', '750', true),
    ('Slip', '1', '3', 'H', '15', 'Beam', '300', '750', true),
    ('Slip', '1', '3', 'H', '16', 'Beam', '300', '750', true),
    ('Slip', '1', '3', 'H', '17', 'Beam', '300', '750', true),
    ('Slip', '1', '3', 'H', '20', 'Beam', '300', '750', true),
    ('Slip', '1', '4', 'H', '21', 'Beam', '350', '750', true),
    ('Slip', '1', '4', 'H', '22', 'Beam', '350', '750', true),
    ('Slip', '1', '4', 'H', '23', 'Beam', '350', '750', true),
    ('Slip', '1', '4', 'H', '24', 'Beam', '350', '750', true),
    ('Slip', '1', '4', 'H', '25', 'Beam', '350', '750', true),
    ('Slip', '1', '4', 'H', '26', 'Beam', '350', '750', true),
    ('Slip', '1', '4', 'H', '27', 'Beam', '350', '750', true),
    ('Slip', '1', '4', 'H', '28', 'Beam', '350', '750', true),
    ('Slip', '1', '4', 'K', '1', 'Beam', '375', '750', true),
    ('Slip', '1', '5', 'K', '2', 'Beam', '400', '750', true),
    ('Slip', '1', '5', 'K', '3', 'Beam', '400', '750', true),
    ('Slip', '1', '5', 'K', '4', 'Beam', '400', '750', true),
    ('Slip', '1', '5', 'K', '5', 'Beam', '390', '750', true),
    ('Slip', '2', '3', 'A', '1', 'Beam', '300', '700', true),
    ('Slip', '2', '3', 'A', '2', 'Beam', '300', '700', true),
    ('Slip', '2', '3', 'A', '3', 'Beam', '300', '700', true),
    ('Slip', '2', '3', 'A', '4', 'Beam', '300', '700', true),
    ('Slip', '2', '3', 'A', '5', 'Beam', '300', '700', true),
    ('Slip', '2', '3', 'A', '6', 'Beam', '300', '700', true),
    ('Slip', '2', '3', 'A', '7', 'Beam', '300', '700', true),
    ('Slip', '2', '3', 'A', '8', 'Beam', '300', '700', true),
    ('Slip', '2', '3', 'A', '9', 'Beam', '300', '700', true),
    ('Slip', '2', '4', 'A', '10', 'WalkBeam', '360', '1000', true),
    ('Slip', '2', '4', 'A', '11', 'WalkBeam', '380', '1000', true),
    ('Slip', '2', '4', 'A', '12', 'WalkBeam', '380', '1000', true),
    ('Slip', '2', '4', 'A', '13', 'WalkBeam', '380', '1000', true),
    ('Slip', '2', '4', 'A', '14', 'WalkBeam', '380', '1000', true),
    ('Slip', '2', '4', 'A', '15', 'WalkBeam', '380', '1000', true),
    ('Slip', '2', '4', 'A', '16', 'WalkBeam', '380', '1000', true),
    ('Slip', '2', '4', 'A', '17', 'WalkBeam', '380', '1000', true),
    ('Slip', '2', '4', 'A', '18', 'WalkBeam', '380', '1000', true),
    ('Slip', '2', '4', 'A', '19', 'WalkBeam', '380', '1000', true),
    ('Slip', '2', '4', 'A', '20', 'WalkBeam', '380', '1000', true),
    ('Slip', '2', '4', 'A', '21', 'WalkBeam', '380', '1000', true),
    ('Slip', '2', '4', 'A', '22', 'WalkBeam', '380', '1000', true),
    ('Slip', '2', '4', 'A', '23', 'WalkBeam', '380', '1000', true),
    ('Slip', '2', '4', 'A', '24', 'WalkBeam', '380', '1000', true),
    ('Slip', '2', '4', 'A', '25', 'WalkBeam', '380', '1000', true),
    ('Slip', '2', '4', 'A', '26', 'WalkBeam', '380', '1000', true),
    ('Slip', '2', '4', 'A', '27', 'WalkBeam', '380', '1000', true),
    ('Slip', '2', '4', 'A', '28', 'WalkBeam', '380', '1200', true),
    ('Slip', '2', '5', 'A', '29', 'WalkBeam', '430', '1200', true),
    ('Slip', '2', '5', 'A', '30', 'WalkBeam', '430', '1200', true),
    ('Slip', '2', '5', 'A', '31', 'WalkBeam', '430', '1200', true),
    ('Slip', '2', '5', 'A', '32', 'WalkBeam', '430', '1200', true),
    ('Slip', '2', '5', 'A', '33', 'WalkBeam', '430', '1200', true),
    ('Slip', '2', '5', 'A', '34', 'WalkBeam', '430', '1200', true),
    ('Slip', '2', '5', 'A', '35', 'WalkBeam', '430', '1200', true),
    ('Slip', '2', '5', 'A', '36', 'WalkBeam', '430', '1200', true),
    ('Slip', '2', '5', 'A', '37', 'WalkBeam', '430', '1200', true),
    ('Slip', '2', '5', 'A', '38', 'WalkBeam', '430', '1200', true),
    ('Slip', '2', '5', 'A', '39', 'WalkBeam', '450', '1200', true),
    ('Slip', '2', '2', 'A', '40', 'Beam', '260', '600', true),
    ('Slip', '2', '2', 'A', '41', 'Beam', '260', '500', true),
    ('Slip', '2', '2', 'A', '42', 'Beam', '260', '500', true),
    ('Slip', '2', '3', 'A', '43', 'Beam', '300', '500', true),
    ('Slip', '2', '3', 'A', '44', 'Beam', '300', '500', true),
    ('Slip', '2', '5', 'A', '45', 'WalkBeam', '420', '1000', true),
    ('Slip', '2', '5', 'A', '46', 'WalkBeam', '430', '1000', true),
    ('Slip', '2', '5', 'A', '47', 'WalkBeam', '430', '1000', true),
    ('Slip', '2', '5', 'A', '48', 'WalkBeam', '430', '1000', true),
    ('Slip', '2', '5', 'A', '49', 'WalkBeam', '430', '1000', true),
    ('Slip', '2', '5', 'A', '50', 'WalkBeam', '390', '1000', true),
    ('Slip', '2', '5', 'A', '51', 'WalkBeam', '390', '1000', true),
    ('Slip', '2', '5', 'A', '52', 'WalkBeam', '390', '1000', true),
    ('Slip', '2', '5', 'A', '53', 'WalkBeam', '390', '1000', true),
    ('Slip', '2', '5', 'A', '54', 'WalkBeam', '430', '1000', true),
    ('Slip', '2', '1', 'B', '1', 'Beam', '229', '650', true),
    ('Slip', '2', '1', 'B', '2', 'Beam', '229', '650', true),
    ('Slip', '2', '1', 'B', '3', 'Beam', '229', '650', true),
    ('Slip', '2', '1', 'B', '4', 'Beam', '229', '650', true),
    ('Slip', '2', '1', 'B', '5', 'Beam', '229', '650', true),
    ('Slip', '2', '1', 'B', '6', 'Beam', '229', '650', true),
    ('Slip', '2', '1', 'B', '7', 'Beam', '229', '650', true),
    ('Slip', '2', '1', 'B', '8', 'Beam', '229', '650', true),
    ('Slip', '2', '1', 'B', '9', 'Beam', '229', '650', true),
    ('Slip', '2', '1', 'B', '10', 'Beam', '229', '650', true),
    ('Slip', '2', '1', 'B', '11', 'Beam', '229', '650', true),
    ('Slip', '2', '1', 'B', '12', 'Beam', '229', '650', true),
    ('Slip', '2', '1', 'B', '13', 'Beam', '229', '650', true),
    ('Slip', '2', '1', 'B', '14', 'Beam', '229', '650', true),
    ('Slip', '2', '1', 'B', '15', 'Beam', '229', '650', true),
    ('Slip', '2', '1', 'B', '16', 'Beam', '229', '650', true),
    ('Slip', '2', '1', 'B', '17', 'Beam', '229', '650', true),
    ('Slip', '2', '1', 'B', '18', 'Beam', '229', '650', true),
    ('Slip', '2', '1', 'B', '19', 'Beam', '229', '650', true),
    ('Slip', '2', '1', 'B', '20', 'Beam', '229', '650', true),
    ('Slip', '2', '1', 'B', '21', 'Beam', '229', '650', true),
    ('Slip', '2', '1', 'B', '22', 'Beam', '229', '650', true),
    ('Slip', '2', '1', 'B', '23', 'Beam', '229', '650', true),
    ('Slip', '2', '1', 'B', '24', 'Beam', '229', '650', true),
    ('Slip', '2', '1', 'B', '25', 'Beam', '229', '650', true),
    ('Slip', '2', '1', 'B', '26', 'Beam', '229', '650', true),
    ('Slip', '2', '1', 'B', '27', 'Beam', '229', '650', true),
    ('Slip', '2', '1', 'B', '28', 'Beam', '229', '650', true),
    ('Slip', '2', '1', 'B', '29', 'Beam', '229', '650', true),
    ('Slip', '2', '1', 'B', '30', 'Beam', '229', '650', true),
    ('Slip', '2', '1', 'B', '31', 'Beam', '229', '650', true),
    ('Slip', '2', '1', 'B', '32', 'Beam', '229', '650', true),
    ('Slip', '2', '1', 'B', '33', 'Beam', '229', '650', true),
    ('Slip', '2', '1', 'B', '34', 'Beam', '229', '650', true),
    ('Slip', '2', '1', 'B', '35', 'Beam', '229', '650', true),
    ('Slip', '2', '1', 'B', '36', 'Beam', '229', '650', true),
    ('Slip', '2', '1', 'B', '37', 'Beam', '229', '650', true),
    ('Slip', '2', '1', 'B', '38', 'Beam', '229', '650', true),
    ('Slip', '2', '1', 'B', '39', 'Beam', '229', '650', true),
    ('Slip', '2', '1', 'B', '40', 'Beam', '229', '650', true),
    ('Slip', '2', '1', 'B', '41', 'Beam', '229', '650', true),
    ('Slip', '2', '1', 'B', '42', 'Beam', '229', '650', true),
    ('Slip', '2', '1', 'B', '43', 'Beam', '229', '650', true),
    ('Slip', '2', '1', 'B', '44', 'Beam', '229', '650', true),
    ('Slip', '2', '1', 'B', '45', 'Beam', '229', '650', true),
    ('Slip', '2', '1', 'B', '46', 'Beam', '229', '650', true),
    ('Slip', '2', '1', 'B', '47', 'Beam', '229', '650', true),
    ('Slip', '2', '1', 'B', '48', 'Beam', '229', '650', true),
    ('Slip', '2', '1', 'B', '49', 'Beam', '229', '650', true),
    ('Slip', '2', '1', 'B', '50', 'Beam', '229', '650', true),
    ('Slip', '2', '1', 'B', '51', 'Beam', '229', '650', true),
    ('Slip', '2', '1', 'B', '52', 'Beam', '229', '650', true),
    ('Slip', '2', '1', 'B', '53', 'Beam', '229', '650', true),
    ('Slip', '2', '1', 'B', '54', 'Beam', '229', '650', true),
    ('Slip', '2', '1', 'B', '55', 'Beam', '229', '650', true),
    ('Slip', '2', '1', 'B', '56', 'Beam', '229', '650', true),
    ('Slip', '2', '2', 'B', '57', 'Beam', '275', '650', true),
    ('Slip', '2', '2', 'B', '58', 'Beam', '275', '650', true),
    ('Slip', '2', '2', 'B', '59', 'Beam', '275', '650', true),
    ('Slip', '2', '2', 'B', '60', 'Beam', '275', '650', true),
    ('Slip', '2', '2', 'B', '61', 'Beam', '275', '650', true),
    ('Slip', '2', '2', 'B', '62', 'Beam', '275', '650', true),
    ('Slip', '2', '2', 'B', '63', 'Beam', '275', '650', true),
    ('Slip', '2', '2', 'B', '64', 'Beam', '275', '650', true),
    ('Slip', '2', '2', 'B', '65', 'Beam', '275', '650', true),
    ('Slip', '2', '2', 'B', '66', 'Beam', '275', '650', true),
    ('Slip', '2', '2', 'B', '67', 'Beam', '275', '650', true),
    ('Slip', '2', '2', 'B', '68', 'Beam', '275', '650', true),
    ('Slip', '2', '2', 'B', '69', 'Beam', '275', '650', true),
    ('Slip', '2', '2', 'B', '70', 'Beam', '275', '650', true),
    ('Slip', '2', '2', 'B', '71', 'Beam', '275', '650', true),
    ('Slip', '2', '2', 'B', '72', 'Beam', '275', '650', true),
    ('Slip', '2', '2', 'B', '73', 'Beam', '275', '650', true),
    ('Slip', '2', '2', 'B', '74', 'Beam', '275', '650', true),
    ('Slip', '2', '2', 'B', '75', 'Beam', '275', '650', true),
    ('Slip', '2', '2', 'B', '76', 'Beam', '275', '650', true),
    ('Slip', '2', '2', 'B', '77', 'Beam', '275', '650', true),
    ('Slip', '2', '2', 'B', '78', 'Beam', '275', '650', true),
    ('Slip', '2', '2', 'B', '79', 'Beam', '275', '650', true),
    ('Slip', '2', '2', 'B', '80', 'Beam', '275', '650', true),
    ('Slip', '2', '2', 'B', '81', 'Beam', '275', '650', true),
    ('Slip', '2', '2', 'B', '82', 'Beam', '275', '650', true),
    ('Slip', '2', '2', 'B', '83', 'Beam', '275', '650', true),
    ('Slip', '2', '2', 'B', '84', 'Beam', '275', '650', true),
    ('Slip', '2', '3', 'B', '85', 'Beam', '300', '650', true),
    ('Slip', '2', '3', 'B', '86', 'Beam', '300', '700', true),
    ('Slip', '2', '3', 'B', '87', 'Beam', '300', '700', true),
    ('Slip', '2', '3', 'B', '88', 'Beam', '300', '700', true),
    ('Slip', '2', '3', 'B', '89', 'Beam', '300', '700', true),
    ('Slip', '2', '3', 'B', '90', 'Beam', '300', '700', true),
    ('Slip', '2', '3', 'B', '91', 'Beam', '300', '700', true),
    ('Slip', '2', '3', 'B', '92', 'Beam', '300', '700', true),
    ('Slip', '2', '3', 'B', '93', 'Beam', '300', '700', true),
    ('Slip', '2', '3', 'B', '94', 'Beam', '300', '700', true),
    ('Slip', '2', '1', 'C', '1', 'Beam', '229', '600', true),
    ('Slip', '2', '1', 'C', '2', 'Beam', '229', '600', true),
    ('Slip', '2', '1', 'C', '3', 'Beam', '229', '600', true),
    ('Slip', '2', '1', 'C', '4', 'Beam', '229', '600', true),
    ('Slip', '2', '2', 'C', '5', 'Beam', '275', '600', true),
    ('Slip', '2', '2', 'C', '6', 'Beam', '275', '600', true),
    ('Slip', '2', '2', 'C', '7', 'Beam', '275', '600', true),
    ('Slip', '2', '2', 'C', '8', 'Beam', '275', '600', true),
    ('Slip', '2', '2', 'C', '9', 'Beam', '275', '600', true),
    ('Slip', '2', '2', 'C', '10', 'Beam', '275', '600', true),
    ('Slip', '2', '2', 'C', '11', 'Beam', '275', '600', true),
    ('Slip', '2', '2', 'C', '12', 'Beam', '275', '600', true),
    ('Slip', '2', '2', 'C', '13', 'Beam', '275', '600', true),
    ('Slip', '2', '2', 'C', '14', 'Beam', '275', '600', true),
    ('Slip', '2', '2', 'C', '15', 'Beam', '275', '600', true),
    ('Slip', '2', '2', 'C', '16', 'Beam', '275', '600', true),
    ('Slip', '2', '2', 'C', '17', 'Beam', '275', '600', true),
    ('Slip', '2', '2', 'C', '18', 'Beam', '275', '600', true),
    ('Slip', '2', '2', 'C', '19', 'Beam', '275', '600', true),
    ('Slip', '2', '2', 'C', '20', 'Beam', '275', '600', true),
    ('Slip', '2', '2', 'C', '21', 'Beam', '275', '600', true),
    ('Slip', '2', '2', 'C', '22', 'Beam', '275', '600', true),
    ('Slip', '2', '2', 'C', '23', 'Beam', '275', '600', true),
    ('Slip', '2', '2', 'C', '24', 'Beam', '275', '600', true),
    ('Slip', '2', '2', 'C', '25', 'Beam', '275', '600', true),
    ('Slip', '2', '2', 'C', '26', 'Beam', '275', '600', true),
    ('Slip', '2', '2', 'C', '27', 'Beam', '275', '600', true),
    ('Slip', '2', '2', 'C', '28', 'Beam', '275', '600', true),
    ('Slip', '2', '2', 'C', '29', 'Beam', '275', '600', true),
    ('Storage', '8', '2', 'C', '30', 'Trailer', '275', '600', true),
    ('Storage', '2', '2', 'C', '31', 'Trailer', '275', '600', true),
    ('Storage', '2', '2', 'C', '32', 'Buck', '275', '600', true),
    ('Storage', '2', '2', 'C', '33', 'Buck', '275', '600', true),
    ('Storage', '2', '2', 'C', '34', 'Buck', '275', '600', true),
    ('Slip', '2', '2', 'C', '35', 'Beam', '275', '600', true),
    ('Slip', '2', '2', 'C', '36', 'Beam', '275', '600', true),
    ('Slip', '2', '2', 'C', '37', 'Beam', '275', '600', true),
    ('Slip', '2', '2', 'C', '38', 'Beam', '275', '600', true),
    ('Slip', '2', '2', 'C', '39', 'Beam', '275', '600', true),
    ('Slip', '2', '2', 'C', '40', 'Beam', '275', '600', true),
    ('Slip', '2', '2', 'C', '41', 'Beam', '275', '600', true),
    ('Slip', '2', '2', 'C', '42', 'Beam', '275', '600', true),
    ('Slip', '2', '2', 'C', '43', 'Beam', '260', '600', true),
    ('Slip', '2', '2', 'C', '44', 'Beam', '275', '600', true),
    ('Slip', '2', '2', 'C', '45', 'Beam', '275', '600', true),
    ('Slip', '2', '2', 'C', '46', 'Beam', '275', '600', true),
    ('Slip', '2', '2', 'C', '47', 'Beam', '275', '600', true),
    ('Slip', '2', '2', 'C', '48', 'Beam', '275', '600', true),
    ('Slip', '2', '2', 'C', '49', 'Beam', '275', '600', true),
    ('Slip', '2', '2', 'C', '50', 'Beam', '275', '600', true),
    ('Slip', '2', '2', 'C', '51', 'Beam', '275', '600', true),
    ('Slip', '2', '2', 'C', '52', 'Beam', '275', '600', true),
    ('Slip', '2', '2', 'C', '53', 'Beam', '275', '600', true),
    ('Slip', '2', '2', 'C', '54', 'Beam', '275', '700', true),
    ('Slip', '2', '2', 'C', '55', 'Beam', '275', '700', true),
    ('Slip', '2', '2', 'C', '56', 'Beam', '275', '700', true),
    ('Slip', '2', '2', 'C', '57', 'Beam', '275', '700', true),
    ('Slip', '2', '2', 'C', '58', 'Beam', '275', '700', true),
    ('Slip', '2', '2', 'C', '59', 'Beam', '275', '700', true),
    ('Slip', '2', '2', 'C', '60', 'Beam', '275', '700', true),
    ('Slip', '2', '2', 'C', '61', 'Beam', '275', '700', true),
    ('Slip', '2', '2', 'C', '62', 'Beam', '275', '700', true),
    ('Slip', '2', '2', 'C', '63', 'Beam', '275', '700', true),
    ('Slip', '2', '2', 'C', '64', 'Beam', '275', '700', true),
    ('Slip', '2', '2', 'C', '65', 'Beam', '275', '700', true),
    ('Slip', '2', '2', 'C', '66', 'Beam', '275', '700', true),
    ('Slip', '2', '2', 'C', '67', 'Beam', '275', '700', true),
    ('Slip', '2', '2', 'C', '68', 'Beam', '275', '700', true),
    ('Slip', '2', '2', 'C', '69', 'Beam', '275', '700', true),
    ('Slip', '2', '2', 'C', '70', 'Beam', '275', '700', true),
    ('Slip', '2', '2', 'C', '71', 'Beam', '275', '700', true),
    ('Slip', '2', '2', 'C', '72', 'Beam', '275', '700', true),
    ('Slip', '2', '2', 'C', '73', 'Beam', '275', '700', true),
    ('Slip', '2', '2', 'C', '74', 'Beam', '275', '700', true),
    ('Slip', '2', '3', 'C', '75', 'Beam', '290', '700', true),
    ('Slip', '2', '3', 'C', '76', 'Beam', '290', '700', true),
    ('Slip', '2', '3', 'C', '77', 'Beam', '300', '700', true),
    ('Slip', '2', '3', 'C', '78', 'Beam', '300', '700', true),
    ('Slip', '2', '3', 'C', '79', 'Beam', '300', '700', true),
    ('Slip', '2', '3', 'C', '80', 'Beam', '300', '700', true),
    ('Slip', '2', '3', 'C', '81', 'Beam', '300', '700', true),
    ('Slip', '2', '3', 'C', '82', 'Beam', '300', '700', true),
    ('Slip', '2', '3', 'C', '83', 'Beam', '300', '700', true),
    ('Slip', '2', '3', 'C', '84', 'Beam', '300', '700', true),
    ('Slip', '2', '3', 'C', '85', 'Beam', '300', '700', true),
    ('Slip', '2', '3', 'C', '86', 'Beam', '300', '700', true),
    ('Slip', '2', '2', 'D', '1', 'Beam', '250', '600', true),
    ('Slip', '2', '2', 'D', '2', 'Beam', '250', '600', true),
    ('Slip', '2', '2', 'D', '3', 'Beam', '250', '600', true),
    ('Slip', '2', '2', 'D', '4', 'Beam', '250', '600', true),
    ('Slip', '2', '2', 'D', '5', 'Beam', '250', '600', true),
    ('Slip', '2', '2', 'D', '6', 'Beam', '250', '600', true),
    ('Slip', '2', '2', 'D', '7', 'Beam', '250', '600', true),
    ('Slip', '2', '2', 'D', '8', 'Beam', '250', '600', true),
    ('Slip', '2', '2', 'D', '9', 'Beam', '250', '600', true),
    ('Slip', '2', '2', 'D', '10', 'Beam', '250', '600', true),
    ('Slip', '2', '2', 'D', '11', 'Beam', '250', '600', true),
    ('Slip', '2', '2', 'D', '12', 'Beam', '250', '600', true),
    ('Slip', '2', '2', 'D', '13', 'Beam', '250', '600', true),
    ('Slip', '2', '2', 'D', '14', 'Beam', '250', '600', true),
    ('Slip', '2', '2', 'D', '15', 'Beam', '250', '600', true),
    ('Slip', '2', '2', 'D', '16', 'Beam', '250', '600', true),
    ('Slip', '2', '2', 'D', '17', 'Beam', '250', '600', true),
    ('Slip', '2', '2', 'D', '18', 'Beam', '250', '600', true),
    ('Slip', '2', '2', 'D', '19', 'Beam', '250', '600', true),
    ('Slip', '2', '2', 'D', '20', 'Beam', '250', '600', true),
    ('Slip', '2', '2', 'D', '21', 'Beam', '250', '600', true),
    ('Slip', '2', '2', 'D', '22', 'Beam', '250', '600', true),
    ('Slip', '2', '2', 'D', '23', 'Beam', '250', '600', true),
    ('Slip', '2', '2', 'D', '24', 'Beam', '250', '600', true),
    ('Slip', '2', '2', 'D', '25', 'Beam', '250', '600', true),
    ('Slip', '2', '2', 'D', '26', 'Beam', '250', '600', true),
    ('Slip', '2', '2', 'D', '27', 'Beam', '250', '600', true),
    ('Slip', '2', '2', 'D', '28', 'Beam', '250', '600', true),
    ('Slip', '2', '2', 'D', '29', 'Beam', '250', '600', true),
    ('Slip', '2', '2', 'D', '30', 'Beam', '250', '600', true),
    ('Slip', '2', '2', 'D', '31', 'Beam', '250', '600', true),
    ('Slip', '2', '2', 'D', '32', 'Beam', '250', '600', true),
    ('Slip', '2', '2', 'D', '33', 'Beam', '250', '600', true),
    ('Slip', '2', '2', 'D', '34', 'Beam', '250', '600', true),
    ('Slip', '2', '2', 'D', '35', 'Beam', '250', '600', true),
    ('Slip', '2', '2', 'D', '36', 'Beam', '250', '600', true),
    ('Slip', '2', '2', 'D', '37', 'Beam', '250', '600', true),
    ('Slip', '2', '2', 'D', '38', 'Beam', '250', '600', true),
    ('Slip', '2', '2', 'D', '39', 'Beam', '250', '600', true),
    ('Slip', '2', '2', 'D', '40', 'Beam', '250', '600', true),
    ('Slip', '2', '2', 'D', '41', 'Beam', '250', '600', true),
    ('Slip', '2', '2', 'D', '42', 'Beam', '250', '600', true),
    ('Slip', '2', '2', 'D', '43', 'Beam', '250', '600', true),
    ('Slip', '2', '2', 'D', '44', 'Beam', '250', '600', true),
    ('Slip', '2', '2', 'D', '45', 'Beam', '250', '600', true),
    ('Slip', '2', '2', 'D', '46', 'Beam', '250', '600', true),
    ('Slip', '2', '2', 'D', '47', 'Beam', '250', '600', true),
    ('Slip', '2', '2', 'D', '48', 'Beam', '250', '600', true),
    ('Slip', '2', '2', 'D', '49', 'Beam', '250', '600', true),
    ('Slip', '2', '2', 'D', '50', 'Beam', '250', '600', true),
    ('Slip', '2', '2', 'D', '51', 'Beam', '250', '600', true),
    ('Slip', '2', '2', 'D', '52', 'Beam', '250', '600', true),
    ('Slip', '2', '2', 'D', '53', 'Beam', '275', '600', true),
    ('Slip', '2', '2', 'D', '54', 'Beam', '275', '600', true),
    ('Slip', '2', '2', 'D', '55', 'Beam', '275', '600', true),
    ('Slip', '2', '2', 'D', '56', 'Beam', '275', '600', true),
    ('Slip', '2', '2', 'D', '57', 'Beam', '275', '600', true),
    ('Slip', '2', '2', 'D', '58', 'Beam', '275', '600', true),
    ('Slip', '2', '2', 'D', '59', 'Beam', '275', '600', true),
    ('Slip', '2', '2', 'D', '60', 'Beam', '275', '600', true),
    ('Slip', '2', '2', 'D', '61', 'Beam', '275', '600', true),
    ('Slip', '2', '2', 'D', '62', 'Beam', '275', '600', true),
    ('Slip', '2', '2', 'D', '63', 'Beam', '275', '600', true),
    ('Slip', '2', '2', 'D', '64', 'Beam', '275', '600', true),
    ('Slip', '2', '3', 'D', '65', 'Beam', '300', '600', true),
    ('Slip', '2', '3', 'D', '66', 'Beam', '300', '600', true),
    ('Slip', '2', '3', 'D', '67', 'Beam', '300', '600', true),
    ('Slip', '2', '3', 'D', '68', 'Beam', '300', '600', true),
    ('Slip', '2', '3', 'D', '69', 'Beam', '300', '600', true),
    ('Slip', '2', '3', 'D', '70', 'Beam', '300', '600', true),
    ('Slip', '2', '3', 'D', '71', 'Beam', '300', '600', true),
    ('Slip', '2', '3', 'D', '72', 'Beam', '300', '600', true),
    ('Slip', '2', '3', 'D', '73', 'Beam', '300', '600', true),
    ('Slip', '2', '3', 'D', '74', 'Beam', '300', '600', true),
    ('Slip', '2', '3', 'D', '75', 'Beam', '300', '600', true),
    ('Slip', '2', '3', 'D', '76', 'Beam', '300', '600', true),
    ('Slip', '2', '3', 'D', '77', 'Beam', '300', '600', true),
    ('Slip', '2', '3', 'D', '78', 'Beam', '300', '600', true),
    ('Slip', '2', '3', 'D', '79', 'Beam', '300', '600', true),
    ('Slip', '2', '3', 'D', '80', 'Beam', '300', '600', true),
    ('Slip', '2', '3', 'D', '81', 'Beam', '300', '600', true),
    ('Slip', '2', '3', 'D', '82', 'Beam', '300', '600', true),
    ('Slip', '2', '4', 'D', '83', 'Beam', '340', '600', true),
    ('Slip', '2', '4', 'D', '84', 'Beam', '340', '600', true),
    ('Slip', '2', '4', 'D', '85', 'Beam', '340', '600', true),
    ('Slip', '2', '4', 'D', '86', 'Beam', '340', '600', true),
    ('Slip', '2', '4', 'D', '87', 'Beam', '340', '600', true),
    ('Slip', '2', '4', 'D', '88', 'Beam', '340', '600', true),
    ('Slip', '2', '2', 'E', '1', 'Beam', '265', '600', true),
    ('Slip', '2', '2', 'E', '2', 'Beam', '275', '600', true),
    ('Slip', '2', '2', 'E', '3', 'Beam', '260', '600', true),
    ('Slip', '2', '2', 'E', '4', 'Beam', '275', '600', true),
    ('Slip', '2', '2', 'E', '5', 'Beam', '265', '600', true),
    ('Slip', '2', '2', 'E', '6', 'Beam', '275', '600', true),
    ('Slip', '2', '2', 'E', '7', 'Beam', '260', '600', true),
    ('Slip', '2', '2', 'E', '8', 'Beam', '275', '600', true),
    ('Slip', '2', '2', 'E', '9', 'Beam', '260', '600', true),
    ('Slip', '2', '2', 'E', '10', 'Beam', '275', '600', true),
    ('Slip', '2', '2', 'E', '11', 'Beam', '260', '600', true),
    ('Slip', '2', '2', 'E', '12', 'Beam', '275', '600', true),
    ('Slip', '2', '2', 'E', '13', 'Beam', '260', '600', true),
    ('Slip', '2', '2', 'E', '14', 'Beam', '275', '600', true),
    ('Slip', '2', '2', 'E', '15', 'Beam', '260', '600', true),
    ('Slip', '2', '2', 'E', '16', 'Beam', '275', '600', true),
    ('Slip', '2', '2', 'E', '17', 'Beam', '260', '600', true),
    ('Slip', '2', '2', 'E', '18', 'Beam', '275', '600', true),
    ('Slip', '2', '2', 'E', '19', 'Beam', '260', '600', true),
    ('Slip', '2', '2', 'E', '20', 'Beam', '275', '600', true),
    ('Slip', '2', '3', 'E', '21', 'Beam', '310', '550', true),
    ('Slip', '2', '3', 'E', '22', 'Beam', '320', '600', true),
    ('Slip', '2', '3', 'E', '23', 'Beam', '310', '600', true),
    ('Slip', '2', '3', 'E', '24', 'Beam', '320', '600', true),
    ('Slip', '2', '3', 'E', '25', 'Beam', '310', '600', true),
    ('Slip', '2', '3', 'E', '26', 'Beam', '320', '600', true),
    ('Slip', '2', '3', 'E', '27', 'Beam', '310', '600', true),
    ('Slip', '2', '3', 'E', '28', 'Beam', '320', '600', true),
    ('Slip', '2', '3', 'E', '30', 'Beam', '320', '600', true),
    ('Slip', '2', '3', 'E', '32', 'Beam', '320', '600', true),
    ('Slip', '2', '3', 'E', '34', 'Beam', '320', '600', true),
    ('Slip', '2', '3', 'E', '36', 'Beam', '320', '600', true),
    ('Slip', '2', '3', 'E', '38', 'Beam', '320', '600', true),
    ('Slip', '2', '3', 'E', '40', 'Beam', '320', '600', true),
    ('Slip', '2', '3', 'E', '42', 'Beam', '320', '600', true),
    ('Slip', '2', '3', 'E', '44', 'Beam', '320', '600', true),
    ('Slip', '2', '3', 'E', '46', 'Beam', '320', '600', true),
    ('Slip', '2', '3', 'E', '48', 'Beam', '320', '600', true),
    ('Slip', '2', '3', 'E', '50', 'Beam', '320', '600', true),
    ('Slip', '2', '3', 'E', '52', 'Beam', '320', '600', true),
    ('Slip', '2', '3', 'E', '54', 'Beam', '320', '600', true),
    ('Slip', '2', '3', 'E', '56', 'Beam', '320', '600', true),
    ('Slip', '2', '3', 'E', '58', 'Beam', '320', '600', true),
    ('Slip', '2', '3', 'E', '60', 'Beam', '320', '600', true),
    ('Slip', '2', '3', 'E', '62', 'Beam', '320', '600', true),
    ('Slip', '2', '3', 'E', '64', 'Beam', '320', '600', true),
    ('Slip', '2', '3', 'E', '66', 'Beam', '320', '600', true),
    ('Slip', '2', '3', 'E', '68', 'Beam', '320', '600', true),
    ('Slip', '2', '3', 'E', '70', 'Beam', '320', '600', true),
    ('Slip', '2', '3', 'E', '72', 'Beam', '320', '600', true),
    ('Slip', '2', '6', 'E', '74', 'WalkBeam', '470', '900', true),
    ('Slip', '2', '4', 'E', '76', 'WalkBeam', '380', '900', true),
    ('Slip', '2', '4', 'E', '78', 'WalkBeam', '380', '900', true),
    ('Slip', '2', '4', 'E', '80', 'WalkBeam', '380', '900', true),
    ('Slip', '2', '4', 'E', '82', 'WalkBeam', '380', '900', true),
    ('Slip', '2', '4', 'E', '84', 'WalkBeam', '380', '900', true),
    ('Slip', '2', '4', 'E', '86', 'WalkBeam', '380', '900', true),
    ('Slip', '2', '4', 'E', '88', 'WalkBeam', '380', '900', true),
    ('Slip', '2', '4', 'E', '90', 'WalkBeam', '380', '900', true),
    ('Slip', '2', '4', 'E', '92', 'WalkBeam', '380', '900', true),
    ('Slip', '2', '4', 'E', '94', 'WalkBeam', '380', '900', true),
    ('Slip', '2', '4', 'E', '96', 'WalkBeam', '380', '900', true),
    ('Slip', '2', '4', 'E', '98', 'WalkBeam', '380', '900', true),
    ('Slip', '2', '4', 'E', '100', 'WalkBeam', '380', '900', true),
    ('Slip', '2', '4', 'E', '102', 'WalkBeam', '380', '900', true),
    ('Slip', '2', '4', 'E', '104', 'WalkBeam', '380', '900', true),
    ('Slip', '2', '4', 'E', '106', 'WalkBeam', '380', '900', true),
    ('Slip', '2', '4', 'E', '108', 'Beam', '380', '600', true),
    ('Slip', '2', '2', 'E', '110', 'Beam', '260', '600', true),
    ('Slip', '2', '2', 'E', '112', 'Beam', '260', '600', true),
    ('Slip', '2', '2', 'E', '114', 'Beam', '260', '600', true),
    ('Slip', '2', '2', 'E', '116', 'Beam', '260', '600', true),
    ('Slip', '2', '2', 'E', '118', 'Beam', '260', '600', true),
    ('Slip', '2', '2', 'E', '120', 'Beam', '260', '600', true),
    ('Slip', '2', '5', 'E', '122', 'WalkBeam', '390', '900', true),
    ('Slip', '2', '5', 'E', '124', 'WalkBeam', '390', '900', true),
    ('Slip', '2', '5', 'E', '126', 'WalkBeam', '390', '900', true),
    ('Slip', '2', '5', 'E', '128', 'WalkBeam', '390', '900', true),
    ('Slip', '2', '5', 'E', '130', 'WalkBeam', '390', '900', true),
    ('Slip', '2', '5', 'E', '132', 'WalkBeam', '390', '900', true),
    ('Slip', '2', '5', 'E', '134', 'WalkBeam', '390', '900', true),
    ('Slip', '2', '5', 'E', '136', 'WalkBeam', '390', '900', true),
    ('Slip', '2', '5', 'E', '138', 'WalkBeam', '390', '900', true),
    ('Slip', '2', '5', 'E', '140', 'WalkBeam', '390', '900', true),
    ('Slip', '2', '5', 'E', '142', 'WalkBeam', '390', '900', true),
    ('Slip', '2', '5', 'E', '144', 'WalkBeam', '420', '900', true),
    ('Slip', '3', '2', 'none', '001', 'Beam', '270', '500', true),
    ('Slip', '3', '2', 'none', '002', 'Beam', '280', '500', true),
    ('Slip', '3', '2', 'none', '003', 'Beam', '280', '500', true),
    ('Slip', '3', '2', 'none', '004', 'Beam', '280', '500', true),
    ('Slip', '3', '2', 'none', '005', 'Beam', '280', '500', true),
    ('Slip', '3', '2', 'none', '006', 'Beam', '280', '500', true),
    ('Slip', '3', '2', 'none', '007', 'Beam', '280', '500', true),
    ('Slip', '3', '2', 'none', '008', 'Beam', '280', '500', true),
    ('Slip', '3', '2', 'none', '009', 'Beam', '280', '500', true),
    ('Slip', '3', '2', 'none', '010', 'Beam', '280', '500', true),
    ('Slip', '3', '2', 'none', '011', 'Beam', '280', '500', true),
    ('Slip', '3', '2', 'none', '012', 'Beam', '280', '500', true),
    ('Slip', '3', '2', 'none', '013', 'Beam', '280', '500', true),
    ('Slip', '3', '2', 'none', '014', 'Beam', '280', '500', true),
    ('Slip', '3', '2', 'none', '015', 'Beam', '280', '500', true),
    ('Slip', '3', '2', 'none', '016', 'Beam', '270', '500', true),
    ('Slip', '3', '3', 'none', '017', 'Beam', '290', '500', true),
    ('Slip', '3', '2', 'none', '018', 'Beam', '280', '500', true),
    ('Slip', '3', '2', 'none', '019', 'Beam', '280', '500', true),
    ('Slip', '3', '2', 'none', '020', 'Beam', '280', '500', true),
    ('Slip', '3', '2', 'none', '021', 'Beam', '280', '500', true),
    ('Slip', '3', '2', 'none', '022', 'Beam', '280', '500', true),
    ('Slip', '3', '2', 'none', '023', 'Beam', '280', '500', true),
    ('Slip', '3', '2', 'none', '024', 'Beam', '280', '500', true),
    ('Slip', '3', '2', 'none', '025', 'Beam', '275', '500', true),
    ('Slip', '3', '3', 'none', '026', 'Beam', '300', '500', true),
    ('Slip', '3', '3', 'none', '027', 'Beam', '300', '600', true),
    ('Slip', '3', '3', 'none', '028', 'Beam', '300', '600', true),
    ('Slip', '3', '3', 'none', '029', 'Beam', '300', '500', true),
    ('Slip', '3', '2', 'none', '030', 'Beam', '280', '500', true),
    ('Slip', '3', '2', 'none', '031', 'Beam', '280', '500', true),
    ('Slip', '3', '2', 'none', '032', 'Beam', '280', '500', true),
    ('Slip', '3', '2', 'none', '033', 'Beam', '280', '500', true),
    ('Slip', '3', '2', 'none', '034', 'Beam', '280', '500', true),
    ('Slip', '3', '2', 'none', '035', 'Beam', '280', '500', true),
    ('Slip', '3', '2', 'none', '036', 'Beam', '280', '500', true),
    ('Slip', '3', '2', 'none', '037', 'Beam', '280', '500', true),
    ('Slip', '3', '3', 'none', '038', 'Beam', '290', '500', true),
    ('Slip', '3', '2', 'none', '039', 'Beam', '275', '500', true),
    ('Slip', '3', '2', 'none', '040', 'Beam', '280', '500', true),
    ('Slip', '3', '2', 'none', '041', 'Beam', '280', '500', true),
    ('Slip', '3', '2', 'none', '042', 'Beam', '280', '500', true),
    ('Slip', '3', '2', 'none', '043', 'Beam', '280', '500', true),
    ('Slip', '3', '2', 'none', '044', 'Beam', '280', '500', true),
    ('Slip', '3', '2', 'none', '045', 'Beam', '280', '500', true),
    ('Slip', '3', '2', 'none', '046', 'Beam', '280', '500', true),
    ('Slip', '3', '2', 'none', '047', 'Beam', '280', '500', true),
    ('Slip', '3', '2', 'none', '048', 'Beam', '280', '500', true),
    ('Slip', '3', '2', 'none', '049', 'Beam', '280', '500', true),
    ('Slip', '3', '2', 'none', '050', 'Beam', '280', '500', true),
    ('Slip', '3', '2', 'none', '051', 'Beam', '280', '500', true),
    ('Slip', '3', '2', 'none', '052', 'Beam', '280', '500', true),
    ('Slip', '3', '2', 'none', '053', 'Beam', '280', '500', true),
    ('Slip', '3', '2', 'none', '054', 'Beam', '270', '500', true),
    ('Slip', '3', '3', 'none', '055', 'Beam', '300', '600', true),
    ('Slip', '3', '3', 'none', '056', 'Beam', '300', '600', true),
    ('Slip', '3', '3', 'none', '057', 'Beam', '300', '600', true),
    ('Slip', '3', '3', 'none', '058', 'Beam', '300', '600', true),
    ('Slip', '3', '3', 'none', '059', 'Beam', '300', '600', true),
    ('Slip', '3', '3', 'none', '060', 'Beam', '300', '600', true),
    ('Slip', '3', '3', 'none', '061', 'Beam', '300', '600', true),
    ('Slip', '3', '3', 'none', '062', 'Beam', '300', '600', true),
    ('Slip', '3', '3', 'none', '063', 'Beam', '300', '600', true),
    ('Slip', '3', '3', 'none', '064', 'Beam', '300', '600', true),
    ('Slip', '3', '3', 'none', '065', 'Beam', '300', '600', true),
    ('Slip', '3', '3', 'none', '066', 'Beam', '300', '600', true),
    ('Slip', '3', '3', 'none', '067', 'Beam', '300', '600', true),
    ('Slip', '3', '3', 'none', '068', 'Beam', '300', '600', true),
    ('Slip', '3', '3', 'none', '069', 'Beam', '300', '600', true),
    ('Slip', '3', '3', 'none', '070', 'Beam', '300', '600', true),
    ('Slip', '3', '3', 'none', '071', 'Beam', '300', '600', true),
    ('Slip', '3', '3', 'none', '072', 'Beam', '300', '600', true),
    ('Slip', '3', '3', 'none', '073', 'Beam', '300', '600', true),
    ('Slip', '3', '3', 'none', '074', 'Beam', '300', '600', true),
    ('Slip', '3', '3', 'none', '075', 'Beam', '300', '600', true),
    ('Slip', '3', '3', 'none', '076', 'Beam', '300', '600', true),
    ('Slip', '3', '3', 'none', '077', 'Beam', '300', '600', true),
    ('Slip', '3', '3', 'none', '078', 'Beam', '300', '600', true),
    ('Slip', '3', '3', 'none', '079', 'Beam', '300', '600', true),
    ('Slip', '3', '3', 'none', '080', 'Beam', '300', '600', true),
    ('Slip', '3', '3', 'none', '081', 'Beam', '300', '600', true),
    ('Slip', '3', '3', 'none', '082', 'Beam', '300', '600', true),
    ('Slip', '3', '3', 'none', '083', 'Beam', '300', '600', true),
    ('Slip', '3', '3', 'none', '084', 'Beam', '300', '600', true),
    ('Slip', '3', '3', 'none', '085', 'Beam', '300', '600', true),
    ('Slip', '3', '3', 'none', '086', 'Beam', '300', '600', true),
    ('Slip', '3', '3', 'none', '087', 'Beam', '300', '600', true),
    ('Slip', '3', '3', 'none', '088', 'Beam', '300', '600', true),
    ('Slip', '3', '3', 'none', '089', 'Beam', '300', '600', true),
    ('Slip', '3', '3', 'none', '090', 'Beam', '300', '600', true),
    ('Slip', '3', '3', 'none', '091', 'Beam', '300', '600', true),
    ('Slip', '3', '3', 'none', '092', 'Beam', '300', '600', true),
    ('Slip', '3', '3', 'none', '093', 'Beam', '300', '600', true),
    ('Slip', '3', '3', 'none', '094', 'Beam', '300', '600', true),
    ('Slip', '3', '3', 'none', '095', 'Beam', '300', '600', true),
    ('Slip', '3', '3', 'none', '096', 'Beam', '300', '600', true),
    ('Slip', '3', '3', 'none', '097', 'Beam', '300', '600', true),
    ('Slip', '3', '3', 'none', '098', 'Beam', '300', '600', true),
    ('Slip', '3', '3', 'none', '099', 'Beam', '300', '600', true),
    ('Slip', '3', '3', 'none', '100', 'Beam', '300', '600', true),
    ('Slip', '3', '2', 'none', '101', 'Beam', '280', '600', true),
    ('Slip', '3', '2', 'none', '102', 'Beam', '280', '600', true),
    ('Slip', '3', '3', 'none', '103', 'Beam', '285', '500', true),
    ('Slip', '3', '3', 'none', '104', 'Beam', '285', '500', true),
    ('Slip', '3', '2', 'none', '105', 'Beam', '275', '500', true),
    ('Slip', '3', '2', 'none', '106', 'Beam', '280', '500', true),
    ('Slip', '3', '3', 'none', '107', 'Beam', '300', '600', true),
    ('Slip', '3', '3', 'none', '108', 'Beam', '300', '600', true),
    ('Slip', '3', '3', 'none', '109', 'Beam', '300', '600', true),
    ('Slip', '3', '3', 'none', '110', 'Beam', '300', '600', true),
    ('Slip', '3', '3', 'none', '111', 'Beam', '300', '600', true),
    ('Slip', '3', '3', 'none', '112', 'Beam', '300', '600', true),
    ('Slip', '3', '3', 'none', '113', 'Beam', '300', '600', true),
    ('Slip', '3', '3', 'none', '114', 'Beam', '300', '600', true),
    ('Slip', '3', '3', 'none', '115', 'Beam', '300', '600', true),
    ('Slip', '3', '3', 'none', '116', 'Beam', '300', '600', true),
    ('Slip', '3', '3', 'none', '117', 'Beam', '300', '600', true),
    ('Slip', '3', '3', 'none', '118', 'Beam', '300', '600', true),
    ('Slip', '3', '3', 'none', '119', 'Beam', '300', '600', true),
    ('Slip', '3', '3', 'none', '120', 'Beam', '300', '600', true),
    ('Slip', '3', '3', 'none', '121', 'Beam', '300', '600', true),
    ('Slip', '3', '3', 'none', '122', 'Beam', '300', '600', true),
    ('Slip', '3', '3', 'none', '123', 'Beam', '300', '600', true),
    ('Slip', '3', '3', 'none', '124', 'Beam', '290', '600', true),
    ('Slip', '3', '3', 'none', '125', 'Beam', '300', '600', true),
    ('Slip', '4', '3', 'A', '1', 'Beam', '285', '600', true),
    ('Slip', '4', '3', 'A', '2', 'Beam', '285', '600', true),
    ('Slip', '4', '3', 'A', '3', 'Beam', '285', '600', true),
    ('Slip', '4', '3', 'A', '5', 'Beam', '285', '600', true),
    ('Slip', '4', '3', 'A', '7', 'Beam', '285', '600', true),
    ('Slip', '4', '3', 'A', '8', 'Beam', '285', '600', true),
    ('Slip', '4', '3', 'A', '10', 'Beam', '285', '600', true),
    ('Slip', '4', '3', 'A', '11', 'Beam', '285', '600', true),
    ('Slip', '4', '3', 'A', '12', 'Beam', '285', '600', true),
    ('Slip', '4', '3', 'A', '13', 'Beam', '285', '600', true),
    ('Slip', '4', '3', 'A', '14', 'Beam', '285', '600', true),
    ('Slip', '4', '3', 'A', '15', 'Beam', '285', '600', true),
    ('Slip', '4', '3', 'A', '17', 'Beam', '285', '600', true),
    ('Slip', '4', '3', 'A', '19', 'Beam', '285', '600', true),
    ('Slip', '4', '2', 'A', '28', 'Beam', '285', '600', true),
    ('Slip', '4', '4', 'A', '32', 'Beam', '285', '600', true),
    ('Slip', '4', '2', 'A', '49', 'Beam', '285', '600', true),
    ('Slip', '4', '3', 'A', '51', 'Beam', '285', '600', true),
    ('Slip', '4', '3', 'A', '53', 'Beam', '285', '600', true),
    ('Slip', '4', '3', 'A', '58', 'Beam', '300', '600', true),
    ('Slip', '4', '3', 'A', '60', 'Beam', '300', '600', true),
    ('Slip', '4', '3', 'A', '65', 'Beam', '285', '600', true),
    ('Slip', '4', '3', 'A', '67', 'Beam', '285', '600', true),
    ('Slip', '4', '4', 'A', '72', 'WalkBeam', '350', '700', true),
    ('Slip', '4', '4', 'A', '73', 'WalkBeam', '350', '700', true),
    ('Slip', '4', '4', 'A', '74', 'WalkBeam', '350', '700', true),
    ('Slip', '4', '4', 'A', '75', 'WalkBeam', '350', '700', true),
    ('Slip', '4', '4', 'A', '78', 'WalkBeam', '400', '700', true),
    ('Slip', '4', '4', 'A', '79', 'WalkBeam', '350', '700', true),
    ('Slip', '4', '5', 'A', '91', 'WalkBeam', '400', '1000', true),
    ('Slip', '4', '2', 'B', '106', 'Beam', '275', '500', true),
    ('Slip', '4', '2', 'B', '109', 'Beam', '275', '500', true),
    ('Slip', '4', '2', 'B', '110', 'Beam', '275', '500', true),
    ('Slip', '4', '2', 'B', '111', 'Beam', '275', '500', true),
    ('Slip', '4', '2', 'B', '112', 'Beam', '275', '500', true),
    ('Slip', '4', '3', 'B', '113', 'Beam', '290', '500', true),
    ('Slip', '4', '3', 'B', '114', 'Beam', '290', '500', true),
    ('Slip', '4', '2', 'B', '115', 'Beam', '275', '550', true),
    ('Slip', '4', '2', 'B', '118', 'Beam', '275', '550', true),
    ('Slip', '4', '3', 'B', '119', 'Beam', '290', '550', true),
    ('Slip', '4', '3', 'B', '120', 'Beam', '290', '550', true),
    ('Slip', '4', '2', 'B', '121', 'Beam', '275', '550', true),
    ('Slip', '4', '2', 'B', '122', 'Beam', '275', '550', true),
    ('Slip', '4', '2', 'B', '124', 'Beam', '275', '550', true),
    ('Slip', '4', '2', 'B', '125', 'Beam', '275', '550', true),
    ('Slip', '4', '2', 'B', '126', 'Beam', '275', '550', true),
    ('Slip', '4', '2', 'B', '127', 'Beam', '275', '550', true),
    ('Slip', '4', '2', 'B', '128', 'Beam', '275', '550', true),
    ('Slip', '4', '2', 'B', '129', 'Beam', '275', '550', true),
    ('Slip', '4', '2', 'B', '130', 'Beam', '275', '550', true),
    ('Slip', '4', '2', 'B', '132', 'Beam', '275', '550', true),
    ('Slip', '4', '2', 'B', '134', 'Beam', '275', '550', true),
    ('Slip', '4', '2', 'B', '136', 'Beam', '275', '550', true),
    ('Slip', '4', '2', 'B', '137', 'Beam', '275', '550', true),
    ('Slip', '4', '2', 'B', '138', 'Beam', '275', '550', true),
    ('Slip', '4', '2', 'B', '139', 'Beam', '275', '550', true),
    ('Slip', '4', '2', 'B', '140', 'Beam', '275', '550', true),
    ('Slip', '4', '2', 'B', '141', 'Beam', '275', '550', true),
    ('Slip', '4', '2', 'B', '142', 'Beam', '275', '550', true),
    ('Slip', '4', '2', 'B', '143', 'Beam', '275', '550', true),
    ('Slip', '4', '2', 'B', '144', 'Beam', '275', '550', true),
    ('Slip', '4', '2', 'B', '146', 'Beam', '275', '550', true),
    ('Slip', '4', '2', 'B', '148', 'Beam', '275', '550', true),
    ('Slip', '4', '2', 'B', '154', 'Beam', '275', '550', true),
    ('Slip', '4', '2', 'B', '155', 'Beam', '275', '550', true),
    ('Slip', '4', '2', 'B', '158', 'Beam', '275', '550', true),
    ('Slip', '4', '2', 'B', '160', 'Beam', '275', '550', true),
    ('Slip', '4', '2', 'B', '161', 'Beam', '275', '550', true),
    ('Slip', '4', '2', 'B', '162', 'Beam', '275', '550', true),
    ('Slip', '4', '2', 'B', '165', 'Beam', '280', '550', true),
    ('Slip', '4', '2', 'B', '168', 'Beam', '275', '550', true),
    ('Slip', '4', '2', 'B', '170', 'Beam', '275', '550', true),
    ('Slip', '4', '2', 'B', '171', 'Beam', '275', '550', true),
    ('Slip', '4', '2', 'B', '173', 'Beam', '275', '550', true),
    ('Slip', '4', '2', 'B', '174', 'Beam', '275', '550', true),
    ('Slip', '4', '2', 'B', '175', 'Beam', '275', '550', true),
    ('Slip', '4', '2', 'B', '176', 'Beam', '275', '550', true),
    ('Slip', '4', '2', 'B', '177', 'Beam', '275', '550', true),
    ('Slip', '4', '2', 'B', '178', 'Beam', '280', '550', true),
    ('Slip', '4', '2', 'B', '179', 'Beam', '280', '700', true),
    ('Slip', '4', '3', 'B', '187', 'Beam', '330', '700', true),
    ('Slip', '4', '3', 'B', '188', 'Beam', '330', '700', true),
    ('Slip', '4', '3', 'B', '189', 'Beam', '330', '700', true),
    ('Slip', '4', '3', 'B', '190', 'Beam', '330', '700', true),
    ('Slip', '4', '2', 'C', '201', 'Beam', '275', '600', true),
    ('Slip', '4', '2', 'C', '202', 'Beam', '275', '600', true),
    ('Slip', '4', '2', 'C', '204', 'Beam', '275', '600', true),
    ('Slip', '4', '2', 'C', '205', 'Beam', '275', '600', true),
    ('Slip', '4', '2', 'C', '206', 'Beam', '275', '600', true),
    ('Slip', '4', '3', 'C', '207', 'Beam', '295', '600', true),
    ('Slip', '4', '3', 'C', '208', 'Beam', '295', '600', true),
    ('Slip', '4', '2', 'C', '209', 'Beam', '275', '600', true),
    ('Slip', '4', '2', 'C', '210', 'Beam', '275', '600', true),
    ('Slip', '4', '2', 'C', '211', 'Beam', '275', '600', true),
    ('Slip', '4', '2', 'C', '212', 'Beam', '275', '600', true),
    ('Slip', '4', '2', 'C', '213', 'Beam', '275', '600', true),
    ('Slip', '4', '2', 'C', '214', 'Beam', '275', '600', true),
    ('Slip', '4', '3', 'C', '215', 'Beam', '310', '600', true),
    ('Slip', '4', '3', 'C', '216', 'Beam', '310', '600', true),
    ('Slip', '4', '2', 'C', '217', 'Beam', '275', '600', true),
    ('Slip', '4', '2', 'C', '218', 'Beam', '275', '600', true),
    ('Slip', '4', '2', 'C', '219', 'Beam', '275', '600', true),
    ('Slip', '4', '2', 'C', '220', 'Beam', '275', '600', true),
    ('Slip', '4', '2', 'C', '221', 'Beam', '275', '600', true),
    ('Slip', '4', '2', 'C', '222', 'Beam', '275', '600', true),
    ('Slip', '4', '3', 'C', '223', 'Beam', '310', '600', true),
    ('Slip', '4', '3', 'C', '224', 'Beam', '310', '600', true),
    ('Slip', '4', '2', 'C', '225', 'Beam', '275', '600', true),
    ('Slip', '4', '2', 'C', '226', 'Beam', '275', '600', true),
    ('Slip', '4', '2', 'C', '227', 'Beam', '275', '600', true),
    ('Slip', '4', '2', 'C', '228', 'Beam', '275', '600', true),
    ('Slip', '4', '2', 'C', '229', 'Beam', '275', '600', true),
    ('Slip', '4', '2', 'C', '230', 'Beam', '275', '600', true),
    ('Slip', '4', '3', 'C', '231', 'Beam', '310', '600', true),
    ('Slip', '4', '3', 'C', '232', 'Beam', '310', '600', true),
    ('Slip', '4', '2', 'C', '233', 'Beam', '275', '600', true),
    ('Slip', '4', '2', 'C', '234', 'Beam', '275', '600', true),
    ('Slip', '4', '2', 'C', '235', 'Beam', '275', '600', true),
    ('Slip', '4', '2', 'C', '236', 'Beam', '275', '600', true),
    ('Slip', '4', '2', 'C', '237', 'Beam', '275', '600', true),
    ('Slip', '4', '2', 'C', '238', 'Beam', '275', '600', true),
    ('Slip', '4', '3', 'C', '239', 'Beam', '310', '600', true),
    ('Slip', '4', '3', 'C', '240', 'Beam', '310', '600', true),
    ('Slip', '4', '2', 'C', '241', 'Beam', '275', '600', true),
    ('Slip', '4', '2', 'C', '242', 'Beam', '275', '600', true),
    ('Slip', '4', '2', 'C', '243', 'Beam', '275', '600', true),
    ('Slip', '4', '2', 'C', '244', 'Beam', '275', '600', true),
    ('Slip', '4', '2', 'C', '245', 'Beam', '275', '600', true),
    ('Slip', '4', '2', 'C', '246', 'Beam', '275', '600', true),
    ('Slip', '4', '3', 'C', '247', 'Beam', '310', '600', true),
    ('Slip', '4', '3', 'C', '248', 'Beam', '310', '600', true),
    ('Slip', '4', '2', 'C', '249', 'Beam', '275', '600', true),
    ('Slip', '4', '2', 'C', '250', 'Beam', '275', '600', true),
    ('Slip', '4', '2', 'C', '251', 'Beam', '275', '600', true),
    ('Slip', '4', '2', 'C', '252', 'Beam', '275', '600', true),
    ('Slip', '4', '2', 'C', '253', 'Beam', '275', '600', true),
    ('Slip', '4', '2', 'C', '254', 'Beam', '275', '600', true),
    ('Slip', '4', '3', 'C', '255', 'Beam', '310', '600', true),
    ('Slip', '4', '3', 'C', '256', 'Beam', '310', '600', true),
    ('Slip', '4', '2', 'C', '257', 'Beam', '275', '600', true),
    ('Slip', '4', '2', 'C', '258', 'Beam', '275', '600', true),
    ('Slip', '4', '2', 'C', '259', 'Beam', '275', '600', true),
    ('Slip', '4', '2', 'C', '260', 'Beam', '275', '600', true),
    ('Slip', '4', '2', 'C', '261', 'Beam', '275', '600', true),
    ('Slip', '4', '2', 'C', '262', 'Beam', '275', '600', true),
    ('Slip', '4', '3', 'C', '263', 'Beam', '310', '600', true),
    ('Slip', '4', '3', 'C', '264', 'Beam', '310', '600', true),
    ('Slip', '4', '2', 'C', '265', 'Beam', '275', '600', true),
    ('Slip', '4', '2', 'C', '266', 'Beam', '275', '600', true),
    ('Slip', '4', '2', 'C', '267', 'Beam', '275', '600', true),
    ('Slip', '4', '2', 'C', '268', 'Beam', '275', '600', true),
    ('Slip', '4', '2', 'C', '269', 'Beam', '275', '600', true),
    ('Slip', '4', '2', 'C', '270', 'Beam', '275', '600', true),
    ('Slip', '4', '3', 'C', '271', 'Beam', '310', '600', true),
    ('Slip', '4', '3', 'C', '272', 'Beam', '310', '600', true),
    ('Slip', '4', '2', 'C', '273', 'Beam', '275', '600', true),
    ('Slip', '4', '2', 'C', '274', 'Beam', '275', '600', true),
    ('Slip', '4', '2', 'C', '275', 'Beam', '275', '600', true),
    ('Slip', '4', '2', 'C', '276', 'Beam', '275', '600', true),
    ('Slip', '4', '2', 'C', '277', 'Beam', '275', '600', true),
    ('Slip', '4', '2', 'C', '278', 'Beam', '275', '600', true),
    ('Slip', '4', '3', 'C', '279', 'Beam', '310', '600', true),
    ('Slip', '4', '3', 'C', '280', 'Beam', '310', '600', true),
    ('Slip', '4', '2', 'C', '281', 'Beam', '275', '600', true),
    ('Slip', '4', '2', 'C', '282', 'Beam', '275', '600', true),
    ('Slip', '4', '2', 'C', '283', 'Beam', '275', '600', true),
    ('Slip', '4', '2', 'C', '284', 'Beam', '275', '600', true),
    ('Slip', '4', '2', 'C', '285', 'Beam', '275', '600', true),
    ('Slip', '4', '2', 'C', '286', 'Beam', '275', '600', true),
    ('Slip', '4', '3', 'C', '287', 'Beam', '310', '600', true),
    ('Slip', '4', '3', 'C', '288', 'Beam', '310', '600', true),
    ('Slip', '4', '2', 'C', '289', 'Beam', '275', '600', true),
    ('Slip', '4', '2', 'C', '290', 'Beam', '275', '600', true),
    ('Slip', '4', '2', 'C', '291', 'Beam', '275', '600', true),
    ('Slip', '4', '2', 'C', '292', 'Beam', '275', '600', true),
    ('Slip', '4', '2', 'C', '293', 'Beam', '275', '600', true),
    ('Slip', '4', '2', 'C', '294', 'Beam', '275', '600', true),
    ('Slip', '4', '2', 'C', '295', 'Beam', '275', '600', true),
    ('Slip', '4', '2', 'C', '296', 'Beam', '275', '600', true),
    ('Slip', '4', '2', 'D', '301', 'Beam', '260', '550', true),
    ('Slip', '4', '2', 'D', '302', 'Beam', '260', '550', true),
    ('Slip', '4', '2', 'D', '303', 'Beam', '260', '550', true),
    ('Slip', '4', '2', 'D', '304', 'Beam', '260', '550', true),
    ('Slip', '4', '2', 'D', '305', 'Beam', '260', '550', true),
    ('Slip', '4', '2', 'D', '306', 'Beam', '270', '550', true),
    ('Slip', '4', '2', 'D', '307', 'Beam', '270', '550', true),
    ('Slip', '4', '2', 'D', '308', 'Beam', '260', '550', true),
    ('Slip', '4', '2', 'D', '309', 'Beam', '250', '550', true),
    ('Slip', '4', '2', 'D', '310', 'Beam', '260', '550', true),
    ('Slip', '4', '2', 'D', '311', 'Beam', '270', '550', true),
    ('Slip', '4', '2', 'D', '312', 'Beam', '260', '550', true),
    ('Slip', '4', '2', 'D', '313', 'Beam', '260', '550', true),
    ('Slip', '4', '2', 'D', '314', 'Beam', '260', '550', true),
    ('Slip', '4', '2', 'D', '315', 'Beam', '260', '550', true),
    ('Slip', '4', '2', 'D', '316', 'Beam', '260', '550', true),
    ('Slip', '4', '2', 'D', '317', 'Beam', '260', '550', true),
    ('Slip', '4', '2', 'D', '318', 'Beam', '260', '550', true),
    ('Slip', '4', '2', 'D', '319', 'Beam', '260', '550', true),
    ('Slip', '4', '2', 'D', '320', 'Beam', '260', '550', true),
    ('Slip', '4', '2', 'D', '321', 'Beam', '260', '550', true),
    ('Slip', '4', '2', 'D', '322', 'Beam', '260', '550', true),
    ('Slip', '4', '2', 'D', '323', 'Beam', '260', '550', true),
    ('Slip', '4', '2', 'D', '324', 'Beam', '260', '550', true),
    ('Slip', '4', '2', 'D', '325', 'Beam', '260', '550', true),
    ('Slip', '4', '2', 'D', '326', 'Beam', '260', '550', true),
    ('Slip', '4', '2', 'D', '327', 'Beam', '260', '550', true),
    ('Slip', '4', '2', 'D', '328', 'Beam', '260', '550', true),
    ('Slip', '4', '2', 'D', '329', 'Beam', '260', '550', true),
    ('Slip', '4', '2', 'D', '331', 'Beam', '260', '550', true),
    ('Slip', '4', '2', 'D', '332', 'Beam', '260', '550', true),
    ('Slip', '4', '2', 'D', '333', 'Beam', '260', '550', true),
    ('Slip', '4', '2', 'D', '334', 'Beam', '260', '550', true),
    ('Slip', '4', '2', 'D', '335', 'Beam', '260', '550', true),
    ('Slip', '4', '2', 'D', '336', 'Beam', '260', '550', true),
    ('Slip', '4', '2', 'D', '337', 'Beam', '260', '550', true),
    ('Slip', '4', '2', 'D', '338', 'Beam', '260', '550', true),
    ('Slip', '4', '2', 'D', '339', 'Beam', '260', '550', true),
    ('Slip', '4', '2', 'D', '340', 'Beam', '260', '550', true),
    ('Slip', '4', '2', 'D', '341', 'Beam', '260', '550', true),
    ('Slip', '4', '2', 'D', '342', 'Beam', '260', '550', true),
    ('Slip', '4', '2', 'D', '343', 'Beam', '260', '550', true),
    ('Slip', '4', '2', 'D', '344', 'Beam', '260', '550', true),
    ('Slip', '4', '2', 'D', '345', 'Beam', '260', '550', true),
    ('Slip', '4', '2', 'D', '346', 'Beam', '260', '550', true),
    ('Slip', '4', '2', 'D', '347', 'Beam', '260', '550', true),
    ('Slip', '4', '2', 'D', '348', 'Beam', '260', '550', true),
    ('Slip', '4', '2', 'D', '349', 'Beam', '260', '550', true),
    ('Slip', '4', '2', 'D', '350', 'Beam', '260', '550', true),
    ('Slip', '4', '2', 'D', '351', 'Beam', '260', '550', true),
    ('Slip', '4', '2', 'D', '352', 'Beam', '260', '550', true),
    ('Slip', '4', '2', 'D', '353', 'Beam', '260', '550', true),
    ('Slip', '4', '2', 'D', '354', 'Beam', '260', '550', true),
    ('Slip', '4', '2', 'D', '355', 'Beam', '260', '550', true),
    ('Slip', '4', '2', 'D', '356', 'Beam', '260', '550', true),
    ('Slip', '4', '2', 'D', '357', 'Beam', '260', '550', true),
    ('Slip', '4', '2', 'D', '358', 'Beam', '260', '550', true),
    ('Slip', '4', '2', 'D', '359', 'Beam', '260', '550', true),
    ('Slip', '4', '2', 'D', '360', 'Beam', '260', '550', true),
    ('Slip', '4', '2', 'D', '361', 'Beam', '260', '550', true),
    ('Slip', '4', '2', 'D', '362', 'Beam', '260', '550', true),
    ('Slip', '4', '2', 'D', '363', 'Beam', '260', '550', true),
    ('Slip', '4', '2', 'D', '364', 'Beam', '260', '550', true),
    ('Slip', '4', '2', 'D', '365', 'Beam', '260', '550', true),
    ('Slip', '4', '2', 'D', '366', 'Beam', '260', '550', true),
    ('Slip', '4', '2', 'D', '367', 'Beam', '260', '550', true),
    ('Slip', '4', '2', 'D', '368', 'Beam', '260', '550', true),
    ('Slip', '4', '2', 'D', '369', 'Beam', '260', '550', true),
    ('Slip', '4', '2', 'D', '370', 'Beam', '260', '550', true),
    ('Slip', '4', '2', 'D', '371', 'Beam', '260', '550', true),
    ('Slip', '4', '2', 'D', '372', 'Beam', '260', '550', true),
    ('Slip', '4', '2', 'D', '373', 'Beam', '260', '550', true),
    ('Slip', '4', '2', 'D', '374', 'Beam', '260', '550', true),
    ('Slip', '4', '2', 'D', '375', 'Beam', '260', '550', true),
    ('Slip', '4', '1', 'E', '401', 'Beam', '229', '550', true),
    ('Slip', '4', '1', 'E', '402', 'Beam', '229', '550', true),
    ('Slip', '4', '2', 'E', '403', 'Beam', '275', '550', true),
    ('Slip', '4', '2', 'E', '404', 'Beam', '275', '550', true),
    ('Slip', '4', '2', 'E', '405', 'Beam', '275', '550', true),
    ('Slip', '4', '2', 'E', '406', 'Beam', '275', '550', true),
    ('Slip', '4', '2', 'E', '407', 'Beam', '275', '550', true),
    ('Slip', '4', '2', 'E', '408', 'Beam', '280', '550', true),
    ('Slip', '4', '2', 'E', '409', 'Beam', '275', '550', true),
    ('Slip', '4', '2', 'E', '410', 'Beam', '275', '550', true),
    ('Slip', '4', '2', 'E', '411', 'Beam', '275', '550', true),
    ('Slip', '4', '2', 'E', '412', 'Beam', '275', '550', true),
    ('Slip', '4', '2', 'E', '413', 'Beam', '275', '550', true),
    ('Slip', '4', '2', 'E', '414', 'Beam', '275', '550', true),
    ('Slip', '4', '2', 'E', '415', 'Beam', '275', '550', true),
    ('Slip', '4', '2', 'E', '416', 'Beam', '275', '550', true),
    ('Slip', '4', '2', 'E', '417', 'Beam', '270', '550', true),
    ('Slip', '4', '2', 'E', '418', 'Beam', '270', '550', true),
    ('Slip', '4', '2', 'E', '419', 'Beam', '275', '550', true),
    ('Slip', '4', '2', 'E', '420', 'Beam', '275', '550', true),
    ('Slip', '4', '2', 'E', '421', 'Beam', '275', '550', true),
    ('Slip', '4', '2', 'E', '423', 'Beam', '275', '550', true),
    ('Slip', '4', '2', 'E', '424', 'Beam', '275', '550', true),
    ('Slip', '4', '2', 'E', '425', 'Beam', '275', '550', true),
    ('Slip', '4', '2', 'E', '426', 'Beam', '275', '550', true),
    ('Slip', '4', '2', 'E', '427', 'Beam', '275', '550', true),
    ('Slip', '4', '2', 'E', '428', 'Beam', '275', '550', true),
    ('Slip', '4', '2', 'E', '429', 'Beam', '275', '550', true),
    ('Slip', '4', '2', 'E', '431', 'Beam', '275', '550', true),
    ('Slip', '4', '2', 'E', '432', 'Beam', '275', '550', true),
    ('Slip', '4', '2', 'E', '433', 'Beam', '275', '550', true),
    ('Slip', '4', '2', 'E', '434', 'Beam', '275', '550', true),
    ('Slip', '4', '2', 'E', '435', 'Beam', '275', '550', true),
    ('Slip', '4', '2', 'E', '436', 'Beam', '275', '550', true),
    ('Slip', '4', '2', 'E', '437', 'Beam', '275', '550', true),
    ('Slip', '4', '2', 'E', '438', 'Beam', '270', '550', true),
    ('Slip', '4', '2', 'E', '439', 'Beam', '280', '550', true),
    ('Slip', '4', '2', 'E', '440', 'Beam', '280', '550', true),
    ('Slip', '4', '2', 'E', '441', 'Beam', '280', '550', true),
    ('Slip', '4', '2', 'E', '442', 'Beam', '280', '550', true),
    ('Slip', '4', '2', 'E', '443', 'Beam', '275', '550', true),
    ('Slip', '4', '2', 'E', '445', 'Beam', '275', '550', true),
    ('Slip', '4', '2', 'E', '447', 'Beam', '275', '550', true),
    ('Slip', '4', '2', 'E', '448', 'Beam', '280', '550', true),
    ('Slip', '4', '2', 'E', '449', 'Beam', '275', '550', true),
    ('Slip', '4', '2', 'E', '451', 'Beam', '275', '550', true),
    ('Slip', '4', '2', 'E', '452', 'Beam', '275', '550', true),
    ('Slip', '4', '2', 'E', '453', 'Beam', '270', '550', true),
    ('Slip', '4', '2', 'E', '454', 'Beam', '275', '550', true),
    ('Slip', '4', '2', 'E', '455', 'Beam', '275', '550', true),
    ('Slip', '4', '2', 'E', '456', 'Beam', '275', '550', true),
    ('Slip', '4', '3', 'F', '501', 'RearBuoy', '320', '1600', true),
    ('Slip', '4', '3', 'F', '503', 'RearBuoy', '320', '1600', true),
    ('Slip', '4', '3', 'F', '504', 'RearBuoy', '320', '1600', true),
    ('Slip', '4', '3', 'F', '505', 'RearBuoy', '320', '1600', true),
    ('Slip', '4', '3', 'F', '506', 'RearBuoy', '300', '1600', true),
    ('Slip', '4', '3', 'F', '507', 'RearBuoy', '300', '1600', true),
    ('Slip', '4', '3', 'F', '508', 'RearBuoy', '300', '1600', true),
    ('Slip', '4', '3', 'F', '509', 'RearBuoy', '300', '1600', true),
    ('Slip', '4', '3', 'F', '510', 'RearBuoy', '300', '1600', true),
    ('Slip', '4', '3', 'F', '511', 'RearBuoy', '300', '1600', true),
    ('Slip', '4', '3', 'F', '512', 'RearBuoy', '300', '1600', true),
    ('Slip', '4', '3', 'F', '513', 'RearBuoy', '300', '1600', true),
    ('Slip', '4', '3', 'F', '514', 'RearBuoy', '300', '1600', true),
    ('Slip', '4', '3', 'F', '515', 'RearBuoy', '300', '1600', true),
    ('Slip', '4', '3', 'F', '516', 'RearBuoy', '300', '1600', true),
    ('Slip', '4', '3', 'F', '521', 'RearBuoy', '320', '1600', true),
    ('Slip', '4', '3', 'F', '526', 'RearBuoy', '320', '1600', true),
    ('Slip', '4', '3', 'F', '527', 'RearBuoy', '320', '1600', true),
    ('Slip', '4', '3', 'F', '528', 'RearBuoy', '320', '1600', true),
    ('Slip', '4', '3', 'F', '529', 'RearBuoy', '320', '1600', true),
    ('Slip', '4', '3', 'F', '534', 'RearBuoy', '320', '1600', true),
    ('Slip', '4', '3', 'F', '535', 'RearBuoy', '320', '1600', true),
    ('Slip', '4', '3', 'F', '536', 'RearBuoy', '320', '1600', true),
    ('Slip', '4', '3', 'F', '537', 'RearBuoy', '320', '1600', true),
    ('Slip', '4', '3', 'F', '538', 'RearBuoy', '320', '1600', true),
    ('Slip', '4', '3', 'F', '540', 'RearBuoy', '320', '1600', true),
    ('Slip', '4', '3', 'F', '541', 'RearBuoy', '320', '1600', true),
    ('Slip', '4', '3', 'F', '542', 'RearBuoy', '320', '1600', true),
    ('Slip', '4', '3', 'F', '543', 'RearBuoy', '320', '1600', true),
    ('Slip', '4', '4', 'F', '544', 'RearBuoy', '340', '1600', true),
    ('Slip', '4', '3', 'F', '545', 'RearBuoy', '300', '1600', true),
    ('Slip', '4', '3', 'F', '546', 'RearBuoy', '300', '1600', true),
    ('Slip', '4', '3', 'F', '547', 'RearBuoy', '300', '1600', true),
    ('Slip', '4', '3', 'F', '548', 'RearBuoy', '300', '1600', true),
    ('Slip', '4', '3', 'F', '549', 'RearBuoy', '300', '1600', true),
    ('Slip', '4', '3', 'F', '550', 'RearBuoy', '300', '1600', true),
    ('Slip', '4', '3', 'F', '551', 'RearBuoy', '300', '1600', true),
    ('Slip', '4', '3', 'F', '553', 'RearBuoy', '300', '1600', true),
    ('Slip', '4', '3', 'F', '554', 'RearBuoy', '300', '1600', true),
    ('Slip', '4', '3', 'F', '556', 'RearBuoy', '300', '1600', true),
    ('Slip', '4', '3', 'F', '557', 'RearBuoy', '300', '1600', true),
    ('Slip', '4', '3', 'F', '558', 'RearBuoy', '300', '1600', true),
    ('Slip', '4', '3', 'F', '559', 'RearBuoy', '300', '1600', true),
    ('Slip', '4', '3', 'F', '561', 'RearBuoy', '300', '1600', true),
    ('Slip', '4', '3', 'F', '562', 'RearBuoy', '300', '1600', true),
    ('Slip', '4', '3', 'F', '563', 'RearBuoy', '300', '1600', true),
    ('Slip', '4', '3', 'F', '564', 'RearBuoy', '300', '1600', true),
    ('Slip', '4', '3', 'F', '566', 'RearBuoy', '300', '1600', true),
    ('Slip', '4', '3', 'F', '567', 'RearBuoy', '300', '1600', true),
    ('Slip', '4', '3', 'F', '571', 'RearBuoy', '300', '1600', true),
    ('Slip', '4', '3', 'F', '572', 'RearBuoy', '300', '1600', true),
    ('Slip', '4', '3', 'F', '573', 'RearBuoy', '300', '1600', true),
    ('Slip', '4', '3', 'F', '575', 'RearBuoy', '300', '1600', true),
    ('Slip', '5', '3', 'B', '1', 'Beam', '285', '500', true),
    ('Slip', '5', '3', 'B', '2', 'Beam', '290', '500', true),
    ('Slip', '5', '3', 'B', '3', 'Beam', '290', '500', true),
    ('Slip', '5', '3', 'B', '4', 'Beam', '290', '500', true),
    ('Slip', '5', '3', 'B', '5', 'Beam', '300', '500', true),
    ('Slip', '5', '3', 'B', '6', 'Beam', '290', '500', true),
    ('Slip', '5', '3', 'B', '7', 'Beam', '300', '500', true),
    ('Slip', '5', '3', 'B', '8', 'Beam', '300', '500', true),
    ('Slip', '5', '3', 'B', '9', 'Beam', '290', '500', true),
    ('Slip', '5', '3', 'B', '10', 'Beam', '300', '500', true),
    ('Slip', '5', '3', 'B', '11', 'Beam', '300', '500', true),
    ('Slip', '5', '3', 'B', '12', 'Beam', '300', '500', true),
    ('Slip', '5', '3', 'B', '13', 'Beam', '300', '500', true),
    ('Slip', '5', '3', 'B', '14', 'Beam', '300', '500', true),
    ('Slip', '5', '3', 'B', '15', 'Beam', '300', '500', true),
    ('Slip', '5', '3', 'B', '16', 'Beam', '300', '500', true),
    ('Slip', '5', '3', 'B', '17', 'Beam', '290', '500', true),
    ('Slip', '5', '3', 'B', '18', 'Beam', '300', '500', true),
    ('Slip', '5', '3', 'B', '19', 'Beam', '300', '500', true),
    ('Slip', '5', '3', 'B', '20', 'Beam', '300', '500', true),
    ('Slip', '5', '3', 'B', '21', 'Beam', '300', '500', true),
    ('Slip', '5', '3', 'B', '22', 'Beam', '300', '500', true),
    ('Slip', '5', '3', 'B', '23', 'Beam', '290', '500', true),
    ('Slip', '5', '3', 'B', '24', 'Beam', '290', '500', true),
    ('Slip', '5', '3', 'B', '25', 'Beam', '300', '500', true),
    ('Slip', '5', '3', 'B', '26', 'Beam', '300', '500', true),
    ('Slip', '5', '3', 'B', '27', 'Beam', '300', '500', true),
    ('Slip', '5', '3', 'B', '28', 'Beam', '300', '500', true),
    ('Slip', '5', '3', 'B', '29', 'Beam', '300', '500', true),
    ('Slip', '5', '3', 'B', '30', 'Beam', '300', '500', true),
    ('Slip', '5', '3', 'B', '31', 'Beam', '290', '500', true),
    ('Slip', '5', '3', 'B', '32', 'Beam', '290', '500', true),
    ('Slip', '5', '3', 'B', '33', 'Beam', '300', '500', true),
    ('Slip', '5', '3', 'B', '34', 'Beam', '300', '550', true),
    ('Slip', '5', '3', 'B', '35', 'Beam', '300', '550', true),
    ('Slip', '5', '3', 'B', '36', 'Beam', '300', '550', true),
    ('Slip', '5', '3', 'B', '37', 'Beam', '300', '550', true),
    ('Slip', '5', '3', 'B', '38', 'Beam', '300', '550', true),
    ('Slip', '5', '3', 'B', '39', 'Beam', '300', '550', true),
    ('Slip', '5', '3', 'B', '40', 'Beam', '300', '550', true),
    ('Slip', '5', '3', 'B', '41', 'Beam', '300', '550', true),
    ('Slip', '5', '3', 'B', '42', 'Beam', '300', '550', true),
    ('Slip', '5', '3', 'B', '43', 'Beam', '290', '550', true),
    ('Slip', '5', '3', 'B', '44', 'Beam', '300', '550', true),
    ('Slip', '5', '3', 'B', '45', 'Beam', '330', '550', true),
    ('Slip', '5', '3', 'B', '46', 'Beam', '330', '550', true),
    ('Slip', '5', '3', 'B', '47', 'Beam', '300', '550', true),
    ('Slip', '5', '3', 'B', '48', 'Beam', '300', '550', true),
    ('Slip', '5', '3', 'B', '49', 'Beam', '300', '550', true),
    ('Slip', '5', '3', 'B', '50', 'Beam', '300', '650', true),
    ('Slip', '5', '2', 'B', '51', 'Beam', '275', '650', true),
    ('Slip', '5', '2', 'B', '52', 'Beam', '275', '650', true),
    ('Slip', '5', '2', 'B', '53', 'Beam', '275', '650', true),
    ('Slip', '5', '2', 'B', '54', 'Beam', '275', '650', true),
    ('Slip', '5', '2', 'B', '55', 'Beam', '275', '650', true),
    ('Slip', '5', '2', 'B', '56', 'Beam', '275', '650', true),
    ('Slip', '5', '2', 'B', '57', 'Beam', '275', '650', true),
    ('Slip', '5', '2', 'B', '58', 'Beam', '275', '650', true),
    ('Slip', '5', '2', 'B', '59', 'Beam', '275', '650', true),
    ('Slip', '5', '2', 'B', '60', 'Beam', '275', '650', true),
    ('Slip', '5', '2', 'B', '61', 'Beam', '275', '650', true),
    ('Slip', '5', '2', 'B', '62', 'Beam', '275', '650', true),
    ('Slip', '5', '2', 'B', '63', 'Beam', '275', '650', true),
    ('Slip', '5', '2', 'B', '64', 'Beam', '275', '650', true),
    ('Slip', '5', '3', 'B', '65', 'Beam', '290', '650', true),
    ('Slip', '5', '3', 'B', '66', 'Beam', '290', '650', true),
    ('Slip', '5', '2', 'B', '67', 'Beam', '275', '650', true),
    ('Slip', '5', '2', 'B', '68', 'Beam', '275', '650', true),
    ('Slip', '5', '2', 'B', '69', 'Beam', '275', '650', true),
    ('Slip', '5', '2', 'B', '70', 'Beam', '275', '650', true),
    ('Slip', '5', '3', 'B', '71', 'Beam', '300', '650', true),
    ('Slip', '5', '3', 'B', '72', 'Beam', '300', '650', true),
    ('Slip', '5', '3', 'B', '73', 'Beam', '290', '650', true),
    ('Slip', '5', '3', 'B', '74', 'Beam', '285', '650', true),
    ('Slip', '5', '3', 'B', '75', 'WalkBeam', '330', '900', true),
    ('Slip', '5', '3', 'B', '76', 'WalkBeam', '330', '900', true),
    ('Slip', '5', '3', 'B', '77', 'WalkBeam', '330', '1000', true),
    ('Slip', '5', '3', 'B', '78', 'WalkBeam', '330', '1000', true),
    ('Slip', '5', '4', 'B', '79', 'WalkBeam', '380', '1000', true),
    ('Slip', '5', '4', 'B', '80', 'WalkBeam', '380', '1000', true),
    ('Slip', '5', '4', 'B', '81', 'WalkBeam', '380', '1000', true),
    ('Slip', '5', '4', 'B', '82', 'WalkBeam', '380', '1000', true),
    ('Slip', '5', '4', 'B', '83', 'WalkBeam', '380', '1000', true),
    ('Slip', '5', '4', 'B', '84', 'WalkBeam', '380', '1000', true),
    ('Slip', '5', '4', 'B', '85', 'WalkBeam', '380', '1000', true),
    ('Slip', '5', '4', 'B', '86', 'WalkBeam', '380', '1000', true),
    ('Slip', '5', '4', 'B', '87', 'WalkBeam', '380', '1000', true),
    ('Slip', '5', '4', 'B', '88', 'WalkBeam', '380', '1000', true),
    ('Slip', '5', '4', 'B', '89', 'WalkBeam', '380', '1000', true),
    ('Slip', '5', '4', 'B', '90', 'WalkBeam', '380', '1000', true),
    ('Slip', '5', '5', 'B', '91', 'WalkBeam', '420', '1000', true),
    ('Slip', '5', '5', 'B', '92', 'WalkBeam', '420', '1000', true),
    ('Slip', '5', '5', 'B', '93', 'WalkBeam', '420', '1200', true),
    ('Slip', '5', '5', 'B', '94', 'WalkBeam', '420', '1200', true),
    ('Slip', '5', '6', 'B', '95', 'WalkBeam', '440', '1200', true),
    ('Slip', '5', '6', 'B', '96', 'WalkBeam', '440', '1200', true),
    ('Slip', '5', '1', 'F', '1', 'Beam', '200', '550', true),
    ('Slip', '5', '1', 'F', '3', 'Beam', '229', '600', true),
    ('Slip', '5', '1', 'F', '5', 'Beam', '229', '600', true),
    ('Slip', '5', '1', 'F', '7', 'Beam', '229', '600', true),
    ('Slip', '5', '1', 'F', '9', 'Beam', '229', '600', true),
    ('Slip', '5', '1', 'F', '11', 'Beam', '229', '600', true),
    ('Slip', '5', '1', 'F', '13', 'Beam', '229', '600', true),
    ('Slip', '5', '1', 'F', '15', 'Beam', '229', '600', true),
    ('Slip', '5', '1', 'F', '17', 'Beam', '229', '600', true),
    ('Slip', '5', '1', 'F', '19', 'Beam', '229', '600', true),
    ('Slip', '5', '1', 'F', '21', 'Beam', '229', '600', true),
    ('Slip', '5', '1', 'F', '23', 'Beam', '229', '600', true),
    ('Slip', '5', '1', 'F', '25', 'Beam', '229', '600', true),
    ('Slip', '5', '1', 'F', '27', 'Beam', '229', '600', true),
    ('Slip', '5', '1', 'F', '29', 'Beam', '229', '600', true),
    ('Slip', '5', '1', 'F', '31', 'Beam', '229', '600', true),
    ('Slip', '5', '1', 'F', '33', 'Beam', '229', '600', true),
    ('Slip', '5', '1', 'F', '35', 'Beam', '229', '600', true),
    ('Slip', '5', '1', 'F', '37', 'Beam', '229', '600', true),
    ('Slip', '5', '1', 'F', '39', 'Beam', '229', '600', true),
    ('Slip', '5', '1', 'F', '41', 'Beam', '229', '600', true),
    ('Slip', '5', '1', 'F', '43', 'Beam', '229', '600', true),
    ('Slip', '5', '1', 'F', '45', 'Beam', '229', '600', true),
    ('Slip', '5', '1', 'F', '47', 'Beam', '229', '600', true),
    ('Slip', '5', '1', 'F', '49', 'Beam', '229', '600', true),
    ('Slip', '5', '1', 'F', '51', 'Beam', '229', '600', true),
    ('Slip', '5', '1', 'F', '53', 'Beam', '229', '600', true),
    ('Slip', '5', '1', 'F', '55', 'Beam', '229', '600', true),
    ('Slip', '5', '1', 'F', '57', 'Beam', '229', '600', true),
    ('Slip', '5', '1', 'F', '59', 'Beam', '229', '600', true),
    ('Slip', '5', '2', 'F', '61', 'Beam', '275', '600', true),
    ('Slip', '5', '2', 'F', '63', 'Beam', '275', '600', true),
    ('Slip', '5', '2', 'F', '65', 'Beam', '275', '600', true),
    ('Slip', '5', '2', 'F', '67', 'Beam', '275', '600', true),
    ('Slip', '5', '2', 'F', '69', 'Beam', '275', '600', true),
    ('Slip', '5', '2', 'F', '71', 'Beam', '275', '600', true),
    ('Slip', '5', '2', 'F', '73', 'Beam', '275', '600', true),
    ('Slip', '5', '2', 'F', '75', 'Beam', '275', '600', true),
    ('Slip', '5', '2', 'F', '77', 'Beam', '275', '600', true),
    ('Slip', '5', '2', 'F', '79', 'Beam', '275', '600', true),
    ('Slip', '5', '2', 'F', '80', 'Beam', '275', '600', true),
    ('Slip', '5', '2', 'F', '81', 'Beam', '275', '600', true),
    ('Slip', '5', '2', 'F', '82', 'Beam', '275', '600', true),
    ('Slip', '5', '2', 'F', '83', 'Beam', '275', '600', true),
    ('Slip', '5', '2', 'F', '84', 'Beam', '275', '600', true),
    ('Slip', '5', '2', 'F', '85', 'Beam', '275', '600', true),
    ('Slip', '5', '2', 'F', '86', 'Beam', '275', '600', true),
    ('Slip', '6', '4', 'A', '1', 'WalkBeam', '350', '900', true),
    ('Slip', '6', '2', 'A', '2', 'Beam', '260', '600', true),
    ('Slip', '6', '5', 'A', '3', 'WalkBeam', '420', '900', true),
    ('Slip', '6', '2', 'A', '4', 'Beam', '250', '600', true),
    ('Slip', '6', '6', 'A', '5', 'WalkBeam', '420', '1000', true),
    ('Slip', '6', '2', 'A', '6', 'Beam', '250', '600', true),
    ('Slip', '6', '5', 'A', '7', 'WalkBeam', '400', '1000', true),
    ('Slip', '6', '2', 'A', '8', 'Beam', '250', '600', true),
    ('Slip', '6', '5', 'A', '9', 'WalkBeam', '400', '1000', true),
    ('Slip', '6', '2', 'A', '10', 'Beam', '250', '600', true),
    ('Slip', '6', '5', 'A', '11', 'WalkBeam', '400', '1000', true),
    ('Slip', '6', '2', 'A', '12', 'Beam', '260', '600', true),
    ('Slip', '6', '5', 'A', '13', 'WalkBeam', '400', '1000', true),
    ('Slip', '6', '2', 'A', '14', 'Beam', '250', '600', true),
    ('Slip', '6', '5', 'A', '15', 'WalkBeam', '400', '1000', true),
    ('Slip', '6', '2', 'A', '16', 'Beam', '250', '600', true),
    ('Slip', '6', '5', 'A', '17', 'WalkBeam', '400', '1000', true),
    ('Slip', '6', '2', 'A', '18', 'Beam', '250', '600', true),
    ('Slip', '6', '5', 'A', '19', 'WalkBeam', '400', '1000', true),
    ('Slip', '6', '2', 'A', '20', 'Beam', '250', '600', true),
    ('Slip', '6', '5', 'A', '21', 'WalkBeam', '400', '1000', true),
    ('Slip', '6', '2', 'A', '22', 'Beam', '250', '600', true),
    ('Slip', '6', '5', 'A', '23', 'WalkBeam', '400', '1000', true),
    ('Slip', '6', '2', 'A', '24', 'Beam', '250', '600', true),
    ('Slip', '6', '5', 'A', '25', 'WalkBeam', '400', '1000', true),
    ('Slip', '6', '2', 'A', '26', 'Beam', '260', '600', true),
    ('Slip', '6', '5', 'A', '27', 'WalkBeam', '400', '1000', true),
    ('Slip', '6', '2', 'A', '28', 'Beam', '260', '600', true),
    ('Slip', '6', '5', 'A', '29', 'WalkBeam', '400', '1000', true),
    ('Slip', '6', '2', 'A', '30', 'Beam', '250', '600', true),
    ('Slip', '6', '5', 'A', '31', 'WalkBeam', '400', '1000', true),
    ('Slip', '6', '2', 'A', '32', 'Beam', '250', '600', true),
    ('Slip', '6', '5', 'A', '33', 'WalkBeam', '400', '1000', true),
    ('Slip', '6', '2', 'A', '34', 'Beam', '250', '600', true),
    ('Slip', '6', '5', 'A', '35', 'WalkBeam', '400', '1000', true),
    ('Slip', '6', '2', 'A', '36', 'Beam', '250', '600', true),
    ('Slip', '6', '5', 'A', '37', 'WalkBeam', '400', '1000', true),
    ('Slip', '6', '2', 'A', '38', 'Beam', '250', '600', true),
    ('Slip', '6', '5', 'A', '39', 'WalkBeam', '400', '1000', true),
    ('Slip', '6', '2', 'A', '40', 'Beam', '260', '600', true),
    ('Slip', '6', '4', 'A', '41', 'WalkBeam', '350', '1000', true),
    ('Slip', '6', '2', 'A', '42', 'Beam', '250', '600', true),
    ('Slip', '6', '4', 'A', '43', 'WalkBeam', '350', '1000', true),
    ('Slip', '6', '2', 'A', '44', 'Beam', '250', '600', true),
    ('Slip', '6', '4', 'A', '45', 'WalkBeam', '350', '1000', true),
    ('Slip', '6', '2', 'A', '46', 'Beam', '250', '600', true),
    ('Slip', '6', '4', 'A', '47', 'WalkBeam', '350', '1000', true),
    ('Slip', '6', '2', 'A', '48', 'Beam', '250', '600', true),
    ('Slip', '6', '3', 'A', '49', 'WalkBeam', '330', '1000', true),
    ('Slip', '6', '2', 'A', '50', 'Beam', '250', '600', true),
    ('Slip', '6', '3', 'A', '51', 'WalkBeam', '330', '1000', true),
    ('Slip', '6', '2', 'A', '52', 'Beam', '250', '600', true),
    ('Slip', '6', '4', 'A', '53', 'WalkBeam', '350', '1000', true),
    ('Slip', '6', '2', 'A', '54', 'Beam', '240', '600', true),
    ('Slip', '6', '4', 'A', '55', 'WalkBeam', '350', '1000', true),
    ('Slip', '6', '2', 'A', '56', 'Beam', '260', '600', true),
    ('Slip', '6', '4', 'A', '57', 'WalkBeam', '340', '1000', true),
    ('Slip', '6', '2', 'A', '58', 'Beam', '250', '600', true),
    ('Slip', '6', '4', 'A', '59', 'WalkBeam', '340', '1000', true),
    ('Slip', '6', '2', 'A', '60', 'Beam', '250', '600', true),
    ('Slip', '6', '4', 'A', '61', 'WalkBeam', '350', '1000', true),
    ('Slip', '6', '2', 'A', '62', 'Beam', '250', '600', true),
    ('Slip', '6', '3', 'A', '63', 'WalkBeam', '330', '1000', true),
    ('Slip', '6', '2', 'A', '64', 'Beam', '240', '600', true),
    ('Slip', '6', '4', 'A', '65', 'WalkBeam', '350', '1000', true),
    ('Slip', '6', '2', 'A', '66', 'Beam', '275', '600', true),
    ('Slip', '6', '4', 'A', '67', 'WalkBeam', '350', '1000', true),
    ('Slip', '6', '2', 'A', '68', 'Beam', '280', '600', true),
    ('Slip', '6', '4', 'A', '69', 'WalkBeam', '350', '1000', true),
    ('Slip', '6', '2', 'A', '70', 'Beam', '275', '600', true),
    ('Slip', '6', '4', 'A', '71', 'WalkBeam', '350', '900', true),
    ('Slip', '6', '2', 'A', '72', 'Beam', '275', '600', true),
    ('Slip', '6', '4', 'A', '73', 'WalkBeam', '350', '900', true),
    ('Slip', '6', '2', 'A', '74', 'Beam', '275', '600', true),
    ('Slip', '6', '4', 'A', '75', 'WalkBeam', '350', '900', true),
    ('Slip', '6', '2', 'A', '76', 'Beam', '280', '600', true),
    ('Slip', '6', '4', 'A', '77', 'WalkBeam', '350', '900', true),
    ('Slip', '6', '2', 'A', '78', 'Beam', '275', '600', true),
    ('Slip', '6', '3', 'A', '79', 'WalkBeam', '330', '900', true),
    ('Slip', '6', '2', 'A', '80', 'Beam', '275', '600', true),
    ('Slip', '6', '3', 'A', '81', 'WalkBeam', '330', '900', true),
    ('Slip', '6', '2', 'A', '82', 'Beam', '275', '600', true),
    ('Slip', '6', '3', 'A', '83', 'WalkBeam', '330', '900', true),
    ('Slip', '6', '2', 'A', '84', 'Beam', '275', '600', true),
    ('Slip', '6', '3', 'A', '85', 'WalkBeam', '330', '900', true),
    ('Slip', '6', '2', 'A', '86', 'Beam', '275', '600', true),
    ('Slip', '6', '3', 'A', '87', 'WalkBeam', '330', '900', true),
    ('Slip', '6', '2', 'A', '88', 'Beam', '275', '600', true),
    ('Slip', '6', '3', 'A', '89', 'WalkBeam', '330', '900', true),
    ('Slip', '6', '2', 'A', '90', 'Beam', '275', '600', true),
    ('Slip', '6', '3', 'A', '91', 'WalkBeam', '330', '900', true),
    ('Slip', '6', '2', 'A', '92', 'Beam', '275', '600', true),
    ('Slip', '6', '3', 'A', '93', 'WalkBeam', '330', '900', true),
    ('Slip', '6', '2', 'A', '94', 'Beam', '275', '600', true),
    ('Slip', '6', '3', 'A', '95', 'WalkBeam', '330', '900', true),
    ('Slip', '6', '2', 'A', '96', 'Beam', '275', '600', true),
    ('Slip', '6', '3', 'A', '97', 'WalkBeam', '330', '900', true),
    ('Slip', '6', '2', 'A', '98', 'Beam', '275', '600', true),
    ('Slip', '6', '3', 'A', '99', 'WalkBeam', '330', '900', true),
    ('Slip', '6', '2', 'A', '100', 'Beam', '275', '600', true),
    ('Slip', '6', '3', 'A', '101', 'WalkBeam', '330', '900', true),
    ('Slip', '6', '2', 'A', '102', 'Beam', '275', '600', true),
    ('Slip', '6', '3', 'A', '103', 'WalkBeam', '330', '900', true),
    ('Slip', '6', '2', 'A', '104', 'Beam', '275', '600', true),
    ('Slip', '6', '3', 'A', '105', 'WalkBeam', '330', '900', true),
    ('Slip', '6', '2', 'A', '106', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'A', '108', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'A', '110', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'A', '112', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'A', '114', 'Beam', '280', '600', true),
    ('Slip', '6', '2', 'A', '116', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'A', '118', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'A', '120', 'Beam', '280', '600', true),
    ('Slip', '6', '2', 'A', '122', 'Beam', '280', '600', true),
    ('Slip', '6', '2', 'A', '124', 'Beam', '240', '600', true),
    ('Slip', '6', '2', 'A', '126', 'Beam', '250', '600', true),
    ('Slip', '6', '2', 'A', '128', 'Beam', '250', '600', true),
    ('Slip', '6', '2', 'A', '130', 'Beam', '250', '600', true),
    ('Slip', '6', '2', 'A', '132', 'Beam', '250', '600', true),
    ('Slip', '6', '5', 'A', '133', 'WalkBeam', '400', '1000', true),
    ('Slip', '6', '2', 'A', '134', 'Beam', '250', '600', true),
    ('Slip', '6', '2', 'A', '136', 'Beam', '250', '600', true),
    ('Slip', '6', '2', 'A', '138', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'A', '140', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'A', '142', 'Beam', '280', '600', true),
    ('Slip', '6', '2', 'A', '144', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'A', '146', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'A', '148', 'Beam', '275', '600', true),
    ('Slip', '6', '4', 'A', '201', 'WalkBeam', '380', '900', true),
    ('Slip', '6', '3', 'A', '202', 'WalkBeam', '330', '900', true),
    ('Slip', '6', '4', 'A', '203', 'WalkBeam', '380', '900', true),
    ('Slip', '6', '3', 'A', '204', 'WalkBeam', '330', '900', true),
    ('Slip', '6', '4', 'A', '205', 'WalkBeam', '380', '900', true),
    ('Slip', '6', '3', 'A', '206', 'WalkBeam', '330', '900', true),
    ('Slip', '6', '4', 'A', '207', 'WalkBeam', '380', '900', true),
    ('Slip', '6', '3', 'A', '208', 'WalkBeam', '330', '900', true),
    ('Slip', '6', '4', 'A', '209', 'WalkBeam', '380', '900', true),
    ('Slip', '6', '3', 'A', '210', 'WalkBeam', '330', '900', true),
    ('Slip', '6', '4', 'A', '211', 'WalkBeam', '380', '900', true),
    ('Slip', '6', '3', 'A', '212', 'WalkBeam', '330', '900', true),
    ('Slip', '6', '4', 'A', '213', 'WalkBeam', '380', '900', true),
    ('Slip', '6', '3', 'A', '214', 'WalkBeam', '330', '900', true),
    ('Slip', '6', '4', 'A', '215', 'WalkBeam', '380', '900', true),
    ('Slip', '6', '3', 'A', '216', 'WalkBeam', '330', '900', true),
    ('Slip', '6', '4', 'A', '217', 'WalkBeam', '380', '900', true),
    ('Slip', '6', '3', 'A', '218', 'WalkBeam', '330', '900', true),
    ('Slip', '6', '3', 'A', '220', 'WalkBeam', '330', '900', true),
    ('Slip', '6', '3', 'A', '222', 'WalkBeam', '330', '900', true),
    ('Slip', '6', '3', 'A', '224', 'WalkBeam', '330', '900', true),
    ('Slip', '6', '3', 'A', '226', 'WalkBeam', '330', '900', true),
    ('Slip', '6', '3', 'A', '228', 'WalkBeam', '330', '900', true),
    ('Slip', '6', '3', 'A', '230', 'WalkBeam', '330', '900', true),
    ('Slip', '6', '3', 'A', '232', 'WalkBeam', '330', '900', true),
    ('Slip', '6', '3', 'A', '234', 'WalkBeam', '330', '900', true),
    ('Slip', '6', '3', 'A', '236', 'WalkBeam', '330', '900', true),
    ('Slip', '6', '4', 'A', '238', 'WalkBeam', '380', '900', true),
    ('Slip', '6', '4', 'A', '240', 'WalkBeam', '380', '900', true),
    ('Slip', '6', '4', 'A', '242', 'WalkBeam', '380', '900', true),
    ('Slip', '6', '4', 'A', '244', 'WalkBeam', '380', '900', true),
    ('Slip', '6', '4', 'A', '246', 'WalkBeam', '350', '900', true),
    ('Slip', '6', '4', 'A', '248', 'WalkBeam', '350', '900', true),
    ('Slip', '6', '4', 'A', '250', 'WalkBeam', '350', '900', true),
    ('Slip', '6', '4', 'A', '252', 'WalkBeam', '350', '900', true),
    ('Slip', '6', '4', 'A', '254', 'WalkBeam', '350', '900', true),
    ('Slip', '6', '4', 'A', '256', 'WalkBeam', '350', '900', true),
    ('Slip', '6', '4', 'A', '258', 'WalkBeam', '350', '900', true),
    ('Slip', '6', '4', 'A', '260', 'WalkBeam', '350', '900', true),
    ('Slip', '6', '4', 'A', '262', 'WalkBeam', '350', '900', true),
    ('Slip', '6', '4', 'A', '264', 'WalkBeam', '350', '900', true),
    ('Slip', '6', '4', 'A', '266', 'WalkBeam', '350', '900', true),
    ('Slip', '6', '4', 'A', '268', 'WalkBeam', '350', '900', true),
    ('Slip', '6', '4', 'A', '270', 'WalkBeam', '350', '900', true),
    ('Slip', '6', '4', 'A', '272', 'WalkBeam', '350', '1000', true),
    ('Slip', '6', '4', 'A', '274', 'WalkBeam', '370', '1000', true),
    ('Slip', '6', '4', 'A', '276', 'WalkBeam', '370', '1000', true),
    ('Slip', '6', '5', 'A', '278', 'WalkBeam', '400', '1000', true),
    ('Slip', '6', '5', 'A', '280', 'WalkBeam', '400', '1000', true),
    ('Slip', '6', '5', 'A', '282', 'WalkBeam', '400', '1000', true),
    ('Slip', '6', '5', 'A', '284', 'WalkBeam', '400', '1000', true),
    ('Slip', '6', '5', 'A', '286', 'WalkBeam', '400', '1000', true),
    ('Slip', '6', '5', 'A', '288', 'WalkBeam', '400', '1000', true),
    ('Slip', '6', '5', 'A', '290', 'WalkBeam', '400', '1000', true),
    ('Slip', '6', '5', 'A', '292', 'WalkBeam', '400', '1000', true),
    ('Slip', '6', '5', 'A', '294', 'WalkBeam', '400', '1000', true),
    ('Slip', '6', '5', 'A', '296', 'WalkBeam', '400', '1000', true),
    ('Slip', '6', '5', 'A', '298', 'WalkBeam', '400', '1000', true),
    ('Slip', '6', '5', 'A', '300', 'WalkBeam', '400', '1000', true),
    ('Slip', '6', '5', 'A', '302', 'WalkBeam', '400', '1000', true),
    ('Slip', '6', '5', 'A', '304', 'WalkBeam', '400', '1000', true),
    ('Slip', '6', '5', 'A', '306', 'WalkBeam', '400', '1000', true),
    ('Slip', '6', '5', 'A', '308', 'WalkBeam', '400', '1000', true),
    ('Slip', '6', '5', 'A', '310', 'WalkBeam', '400', '1000', true),
    ('Slip', '6', '5', 'A', '312', 'WalkBeam', '400', '1000', true),
    ('Slip', '6', '6', 'A', '314', 'WalkBeam', '500', '1000', true),
    ('Slip', '6', '6', 'A', '316', 'WalkBeam', '500', '1200', true),
    ('Slip', '6', '6', 'A', '318', 'WalkBeam', '500', '1200', true),
    ('Slip', '6', '6', 'A', '320', 'WalkBeam', '500', '1200', true),
    ('Slip', '6', '6', 'A', '322', 'WalkBeam', '500', '1200', true),
    ('Slip', '6', '2', 'B', '2', 'Beam', '275', '600', true),
    ('Slip', '6', '4', 'B', '3', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'B', '4', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'B', '5', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'B', '6', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'B', '7', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'B', '8', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'B', '9', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'B', '10', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'B', '11', 'Beam', '275', '600', true),
    ('Slip', '6', '1', 'B', '12', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'B', '13', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'B', '14', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'B', '15', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'B', '16', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'B', '17', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'B', '18', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'B', '19', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'B', '20', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'B', '21', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'B', '22', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'B', '23', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'B', '24', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'B', '25', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'B', '26', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'B', '27', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'B', '28', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'B', '29', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'B', '30', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'B', '31', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'B', '32', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'B', '33', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'B', '34', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'B', '35', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'B', '36', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'B', '37', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'B', '38', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'B', '39', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'B', '40', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'B', '41', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'B', '42', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'B', '43', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'B', '44', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'B', '45', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'B', '46', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'B', '47', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'B', '48', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'B', '49', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'B', '50', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'B', '51', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'B', '52', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'B', '53', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'B', '54', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'B', '55', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'B', '56', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'B', '57', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'B', '58', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'B', '59', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'B', '60', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'B', '61', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'B', '62', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'B', '63', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'B', '64', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'B', '65', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'B', '66', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'B', '67', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'B', '68', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'B', '69', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'B', '70', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'B', '71', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'B', '72', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'B', '73', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'B', '74', 'Beam', '275', '600', true),
    ('Slip', '6', '3', 'B', '75', 'Beam', '300', '600', true),
    ('Slip', '6', '3', 'B', '76', 'Beam', '300', '600', true),
    ('Slip', '6', '3', 'B', '77', 'Beam', '300', '600', true),
    ('Slip', '6', '3', 'B', '78', 'Beam', '300', '600', true),
    ('Slip', '6', '3', 'B', '79', 'Beam', '300', '600', true),
    ('Slip', '6', '3', 'B', '80', 'Beam', '300', '600', true),
    ('Slip', '6', '3', 'B', '81', 'Beam', '300', '600', true),
    ('Slip', '6', '3', 'B', '82', 'Beam', '300', '600', true),
    ('Slip', '6', '3', 'B', '83', 'Beam', '300', '600', true),
    ('Slip', '6', '3', 'B', '84', 'Beam', '300', '600', true),
    ('Slip', '6', '3', 'B', '85', 'Beam', '300', '600', true),
    ('Slip', '6', '3', 'B', '86', 'Beam', '300', '600', true),
    ('Winter', '6', '3', 'B', '87', 'None', '300', '600', true),
    ('Winter', '6', '3', 'B', '88', 'None', '300', '600', true),
    ('Winter', '6', '3', 'B', '89', 'None', '300', '600', true),
    ('Winter', '6', '3', 'B', '90', 'None', '300', '600', true),
    ('Winter', '6', '3', 'B', '91', 'None', '300', '600', true),
    ('Winter', '6', '3', 'B', '92', 'None', '300', '600', true),
    ('Slip', '6', '3', 'B', '93', 'Beam', '300', '600', true),
    ('Slip', '6', '3', 'B', '94', 'Beam', '300', '600', true),
    ('Slip', '6', '3', 'B', '95', 'Beam', '300', '600', true),
    ('Slip', '6', '3', 'B', '96', 'Beam', '300', '600', true),
    ('Slip', '6', '3', 'B', '97', 'Beam', '300', '600', true),
    ('Slip', '6', '3', 'B', '98', 'Beam', '300', '600', true),
    ('Slip', '6', '3', 'B', '99', 'Beam', '300', '600', true),
    ('Slip', '6', '3', 'B', '100', 'Beam', '300', '600', true),
    ('Slip', '6', '3', 'B', '101', 'Beam', '300', '600', true),
    ('Slip', '6', '3', 'B', '102', 'Beam', '300', '600', true),
    ('Slip', '6', '3', 'B', '103', 'Beam', '300', '600', true),
    ('Slip', '6', '3', 'B', '104', 'Beam', '300', '600', true),
    ('Slip', '6', '3', 'B', '105', 'Beam', '300', '600', true),
    ('Slip', '6', '3', 'B', '106', 'Beam', '300', '600', true),
    ('Slip', '6', '3', 'B', '107', 'Beam', '300', '600', true),
    ('Slip', '6', '3', 'B', '108', 'Beam', '300', '600', true),
    ('Slip', '6', '3', 'B', '109', 'Beam', '300', '600', true),
    ('Slip', '6', '3', 'B', '110', 'Beam', '300', '600', true),
    ('Slip', '6', '3', 'B', '111', 'Beam', '300', '600', true),
    ('Slip', '6', '3', 'B', '112', 'Beam', '300', '600', true),
    ('Slip', '6', '3', 'B', '113', 'Beam', '300', '600', true),
    ('Slip', '6', '3', 'B', '114', 'Beam', '300', '600', true),
    ('Slip', '6', '3', 'B', '115', 'Beam', '300', '600', true),
    ('Slip', '6', '3', 'B', '116', 'Beam', '300', '600', true),
    ('Slip', '6', '3', 'B', '117', 'Beam', '300', '600', true),
    ('Slip', '6', '3', 'B', '118', 'Beam', '300', '600', true),
    ('Slip', '6', '3', 'B', '119', 'Beam', '300', '600', true),
    ('Slip', '6', '3', 'B', '120', 'Beam', '300', '600', true),
    ('Slip', '6', '3', 'B', '121', 'Beam', '300', '600', true),
    ('Slip', '6', '3', 'B', '122', 'Beam', '300', '600', true),
    ('Slip', '6', '3', 'B', '123', 'Beam', '300', '600', true),
    ('Slip', '6', '3', 'B', '124', 'Beam', '300', '600', true),
    ('Slip', '6', '4', 'B', '125', 'Beam', '335', '700', true),
    ('Slip', '6', '4', 'B', '126', 'Beam', '335', '700', true),
    ('Slip', '6', '4', 'B', '127', 'Beam', '335', '700', true),
    ('Slip', '6', '4', 'B', '128', 'Beam', '335', '700', true),
    ('Slip', '6', '4', 'B', '129', 'Beam', '335', '700', true),
    ('Slip', '6', '4', 'B', '130', 'Beam', '335', '700', true),
    ('Slip', '6', '4', 'B', '131', 'Beam', '335', '700', true),
    ('Slip', '6', '4', 'B', '132', 'Beam', '335', '700', true),
    ('Slip', '6', '4', 'B', '133', 'Beam', '335', '700', true),
    ('Slip', '6', '4', 'B', '134', 'Beam', '335', '700', true),
    ('Slip', '6', '4', 'B', '135', 'Beam', '335', '700', true),
    ('Slip', '6', '2', 'C', '1', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'C', '2', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'C', '3', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'C', '4', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'C', '5', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'C', '6', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'C', '7', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'C', '8', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'C', '9', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'C', '10', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'C', '11', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'C', '12', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'C', '13', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'C', '14', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'C', '15', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'C', '16', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'C', '17', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'C', '18', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'C', '19', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'C', '20', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'C', '21', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'C', '22', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'C', '23', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'C', '24', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'C', '25', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'C', '26', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'C', '27', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'C', '28', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'C', '29', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'C', '30', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'C', '31', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'C', '32', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'C', '33', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'C', '34', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'C', '35', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'C', '36', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'C', '37', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'C', '38', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'C', '39', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'C', '40', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'C', '41', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'C', '42', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'C', '43', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'C', '44', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'C', '45', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'C', '46', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'C', '47', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'C', '48', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'C', '49', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'C', '50', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'C', '51', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'C', '52', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'C', '53', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'C', '54', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'C', '55', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'C', '56', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'C', '57', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'C', '58', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'C', '59', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'C', '60', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'C', '61', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'C', '62', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'C', '63', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'C', '64', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'C', '65', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'C', '66', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'C', '67', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'C', '68', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'C', '69', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'C', '70', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'C', '71', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'C', '72', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'C', '73', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'C', '74', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'C', '75', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'C', '76', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'C', '77', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'C', '78', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'C', '79', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'C', '80', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'C', '81', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'C', '82', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'C', '83', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'C', '84', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'C', '85', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'C', '86', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'C', '87', 'Beam', '275', '550', true),
    ('Slip', '6', '2', 'C', '88', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'C', '89', 'Beam', '275', '550', true),
    ('Slip', '6', '2', 'C', '90', 'Beam', '275', '550', true),
    ('Slip', '6', '3', 'C', '91', 'Beam', '300', '550', true),
    ('Slip', '6', '3', 'C', '92', 'Beam', '300', '550', true),
    ('Slip', '6', '3', 'C', '93', 'Beam', '300', '550', true),
    ('Slip', '6', '3', 'C', '94', 'Beam', '300', '550', true),
    ('Slip', '6', '3', 'C', '95', 'Beam', '300', '550', true),
    ('Slip', '6', '3', 'C', '96', 'Beam', '300', '550', true),
    ('Slip', '6', '3', 'C', '97', 'Beam', '300', '550', true),
    ('Slip', '6', '3', 'C', '98', 'Beam', '300', '550', true),
    ('Slip', '6', '3', 'C', '99', 'Beam', '300', '550', true),
    ('Slip', '6', '3', 'C', '100', 'Beam', '300', '550', true),
    ('Slip', '6', '3', 'C', '101', 'Beam', '300', '550', true),
    ('Slip', '6', '3', 'C', '102', 'Beam', '300', '550', true),
    ('Slip', '6', '3', 'C', '103', 'Beam', '300', '550', true),
    ('Slip', '6', '3', 'C', '104', 'Beam', '300', '550', true),
    ('Slip', '6', '3', 'C', '105', 'Beam', '300', '550', true),
    ('Slip', '6', '3', 'C', '106', 'Beam', '300', '550', true),
    ('Slip', '6', '3', 'C', '107', 'Beam', '300', '550', true),
    ('Slip', '6', '3', 'C', '108', 'Beam', '300', '550', true),
    ('Slip', '6', '3', 'C', '109', 'Beam', '300', '550', true),
    ('Slip', '6', '3', 'C', '110', 'Beam', '300', '550', true),
    ('Slip', '6', '3', 'C', '111', 'Beam', '300', '550', true),
    ('Slip', '6', '3', 'C', '112', 'Beam', '300', '550', true),
    ('Slip', '6', '3', 'C', '113', 'Beam', '300', '550', true),
    ('Slip', '6', '3', 'C', '114', 'Beam', '300', '550', true),
    ('Slip', '6', '3', 'C', '115', 'Beam', '300', '550', true),
    ('Slip', '6', '3', 'C', '116', 'Beam', '300', '550', true),
    ('Slip', '6', '3', 'C', '117', 'Beam', '300', '550', true),
    ('Slip', '6', '3', 'C', '118', 'Beam', '300', '550', true),
    ('Slip', '6', '3', 'C', '119', 'Beam', '300', '550', true),
    ('Slip', '6', '3', 'C', '120', 'Beam', '300', '550', true),
    ('Slip', '6', '3', 'C', '121', 'Beam', '300', '550', true),
    ('Slip', '6', '3', 'C', '122', 'Beam', '300', '550', true),
    ('Slip', '6', '3', 'C', '123', 'Beam', '300', '550', true),
    ('Slip', '6', '3', 'C', '124', 'Beam', '300', '550', true),
    ('Slip', '6', '3', 'C', '125', 'Beam', '300', '550', true),
    ('Slip', '6', '3', 'C', '126', 'Beam', '300', '550', true),
    ('Slip', '6', '3', 'C', '127', 'Beam', '300', '550', true),
    ('Slip', '6', '3', 'C', '128', 'Beam', '300', '550', true),
    ('Slip', '6', '3', 'C', '129', 'Beam', '300', '550', true),
    ('Slip', '6', '3', 'C', '130', 'Beam', '300', '550', true),
    ('Slip', '6', '3', 'C', '131', 'Beam', '300', '550', true),
    ('Slip', '6', '3', 'C', '132', 'Beam', '300', '550', true),
    ('Slip', '6', '3', 'C', '133', 'Beam', '330', '700', true),
    ('Slip', '6', '3', 'C', '134', 'Beam', '330', '700', true),
    ('Slip', '6', '3', 'C', '135', 'Beam', '330', '700', true),
    ('Slip', '6', '3', 'C', '136', 'Beam', '330', '700', true),
    ('Slip', '6', '1', 'D', '1', 'Beam', '229', '650', true),
    ('Slip', '6', '1', 'D', '2', 'Beam', '229', '650', true),
    ('Slip', '6', '3', 'D', '3', 'Beam', '300', '650', true),
    ('Slip', '6', '3', 'D', '4', 'Beam', '300', '650', true),
    ('Slip', '6', '3', 'D', '5', 'Beam', '300', '650', true),
    ('Slip', '6', '3', 'D', '6', 'Beam', '300', '650', true),
    ('Slip', '6', '3', 'D', '7', 'Beam', '300', '650', true),
    ('Slip', '6', '3', 'D', '8', 'Beam', '300', '650', true),
    ('Slip', '6', '3', 'D', '9', 'Beam', '300', '650', true),
    ('Slip', '6', '3', 'D', '10', 'Beam', '300', '650', true),
    ('Slip', '6', '3', 'D', '11', 'Beam', '300', '650', true),
    ('Slip', '6', '3', 'D', '12', 'Beam', '300', '650', true),
    ('Slip', '6', '3', 'D', '13', 'Beam', '300', '650', true),
    ('Slip', '6', '3', 'D', '14', 'Beam', '300', '650', true),
    ('Slip', '6', '3', 'D', '15', 'Beam', '300', '650', true),
    ('Slip', '6', '3', 'D', '16', 'Beam', '300', '650', true),
    ('Slip', '6', '3', 'D', '17', 'Beam', '300', '650', true),
    ('Slip', '6', '3', 'D', '18', 'Beam', '300', '650', true),
    ('Slip', '6', '3', 'D', '19', 'Beam', '300', '650', true),
    ('Slip', '6', '3', 'D', '20', 'Beam', '300', '650', true),
    ('Slip', '6', '3', 'D', '21', 'Beam', '300', '650', true),
    ('Slip', '6', '3', 'D', '22', 'Beam', '300', '650', true),
    ('Slip', '6', '3', 'D', '23', 'Beam', '300', '650', true),
    ('Slip', '6', '3', 'D', '24', 'Beam', '300', '650', true),
    ('Slip', '6', '3', 'D', '25', 'Beam', '300', '650', true),
    ('Slip', '6', '3', 'D', '26', 'Beam', '300', '650', true),
    ('Slip', '6', '3', 'D', '27', 'Beam', '300', '650', true),
    ('Slip', '6', '3', 'D', '28', 'Beam', '300', '650', true),
    ('Slip', '6', '3', 'D', '29', 'Beam', '300', '650', true),
    ('Slip', '6', '3', 'D', '30', 'Beam', '300', '650', true),
    ('Slip', '6', '3', 'D', '31', 'Beam', '300', '650', true),
    ('Slip', '6', '3', 'D', '32', 'Beam', '300', '650', true),
    ('Slip', '6', '3', 'D', '33', 'Beam', '300', '650', true),
    ('Slip', '6', '3', 'D', '34', 'Beam', '300', '650', true),
    ('Slip', '6', '3', 'D', '35', 'Beam', '300', '650', true),
    ('Slip', '6', '3', 'D', '36', 'Beam', '300', '650', true),
    ('Slip', '6', '3', 'D', '37', 'Beam', '300', '650', true),
    ('Slip', '6', '3', 'D', '38', 'Beam', '300', '650', true),
    ('Slip', '6', '3', 'D', '39', 'Beam', '300', '650', true),
    ('Slip', '6', '3', 'D', '40', 'Beam', '300', '650', true),
    ('Slip', '6', '3', 'D', '41', 'Beam', '300', '650', true),
    ('Slip', '6', '3', 'D', '42', 'Beam', '300', '650', true),
    ('Slip', '6', '3', 'D', '43', 'Beam', '300', '650', true),
    ('Slip', '6', '3', 'D', '44', 'Beam', '300', '650', true),
    ('Slip', '6', '3', 'D', '45', 'Beam', '300', '650', true),
    ('Slip', '6', '3', 'D', '46', 'Beam', '300', '650', true),
    ('Slip', '6', '3', 'D', '47', 'Beam', '300', '650', true),
    ('Slip', '6', '3', 'D', '48', 'Beam', '300', '650', true),
    ('Slip', '6', '3', 'D', '49', 'Beam', '300', '650', true),
    ('Slip', '6', '3', 'D', '50', 'Beam', '300', '650', true),
    ('Slip', '6', '3', 'D', '51', 'Beam', '300', '650', true),
    ('Slip', '6', '3', 'D', '52', 'Beam', '300', '650', true),
    ('Slip', '6', '3', 'D', '53', 'Beam', '300', '650', true),
    ('Slip', '6', '3', 'D', '54', 'Beam', '300', '650', true),
    ('Slip', '6', '3', 'D', '55', 'Beam', '300', '650', true),
    ('Slip', '6', '3', 'D', '56', 'Beam', '300', '650', true),
    ('Slip', '6', '3', 'D', '57', 'Beam', '300', '650', true),
    ('Slip', '6', '3', 'D', '58', 'Beam', '300', '650', true),
    ('Slip', '6', '3', 'D', '59', 'Beam', '300', '650', true),
    ('Slip', '6', '3', 'D', '60', 'Beam', '300', '650', true),
    ('Slip', '6', '3', 'D', '61', 'Beam', '300', '650', true),
    ('Slip', '6', '3', 'D', '62', 'Beam', '300', '650', true),
    ('Slip', '6', '3', 'D', '63', 'Beam', '300', '650', true),
    ('Slip', '6', '3', 'D', '64', 'Beam', '300', '650', true),
    ('Slip', '6', '3', 'D', '65', 'Beam', '300', '650', true),
    ('Slip', '6', '3', 'D', '66', 'Beam', '300', '650', true),
    ('Slip', '6', '3', 'D', '67', 'Beam', '300', '650', true),
    ('Slip', '6', '3', 'D', '68', 'Beam', '300', '650', true),
    ('Slip', '6', '3', 'D', '69', 'Beam', '300', '650', true),
    ('Slip', '6', '3', 'D', '70', 'Beam', '300', '650', true),
    ('Slip', '6', '3', 'D', '71', 'Beam', '300', '650', true),
    ('Slip', '6', '3', 'D', '72', 'Beam', '300', '650', true),
    ('Slip', '6', '3', 'D', '73', 'Beam', '300', '650', true),
    ('Slip', '6', '3', 'D', '74', 'Beam', '300', '650', true),
    ('Slip', '6', '3', 'D', '75', 'Beam', '300', '650', true),
    ('Slip', '6', '3', 'D', '76', 'Beam', '300', '650', true),
    ('Slip', '6', '3', 'D', '77', 'Beam', '300', '650', true),
    ('Slip', '6', '3', 'D', '78', 'Beam', '300', '650', true),
    ('Slip', '6', '3', 'D', '79', 'Beam', '300', '650', true),
    ('Slip', '6', '3', 'D', '80', 'Beam', '300', '650', true),
    ('Slip', '6', '3', 'D', '81', 'Beam', '300', '650', true),
    ('Slip', '6', '3', 'D', '82', 'Beam', '300', '650', true),
    ('Slip', '6', '3', 'D', '83', 'Beam', '300', '650', true),
    ('Slip', '6', '3', 'D', '84', 'Beam', '300', '650', true),
    ('Slip', '6', '3', 'D', '85', 'Beam', '300', '650', true),
    ('Slip', '6', '3', 'D', '86', 'Beam', '300', '650', true),
    ('Slip', '6', '3', 'D', '87', 'Beam', '300', '650', true),
    ('Slip', '6', '3', 'D', '88', 'Beam', '300', '650', true),
    ('Slip', '6', '3', 'D', '89', 'Beam', '300', '650', true),
    ('Slip', '6', '3', 'D', '90', 'Beam', '300', '650', true),
    ('Slip', '6', '3', 'D', '91', 'Beam', '300', '650', true),
    ('Slip', '6', '3', 'D', '92', 'Beam', '300', '650', true),
    ('Slip', '6', '3', 'D', '93', 'Beam', '300', '650', true),
    ('Slip', '6', '3', 'D', '94', 'Beam', '300', '650', true),
    ('Slip', '6', '3', 'D', '95', 'Beam', '300', '650', true),
    ('Slip', '6', '3', 'D', '96', 'Beam', '300', '650', true),
    ('Slip', '6', '3', 'D', '97', 'Beam', '300', '650', true),
    ('Slip', '6', '3', 'D', '98', 'Beam', '300', '650', true),
    ('Slip', '6', '3', 'D', '99', 'Beam', '300', '650', true),
    ('Slip', '6', '3', 'D', '100', 'Beam', '300', '650', true),
    ('Slip', '6', '3', 'D', '101', 'Beam', '300', '650', true),
    ('Slip', '6', '3', 'D', '102', 'Beam', '300', '650', true),
    ('Slip', '6', '4', 'D', '103', 'Beam', '380', '650', true),
    ('Slip', '6', '4', 'D', '104', 'Beam', '380', '650', true),
    ('Slip', '6', '2', 'D', '105', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'D', '106', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'D', '107', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'D', '108', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'D', '109', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'D', '110', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'D', '111', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'D', '112', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'D', '113', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'D', '114', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'D', '115', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'D', '116', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'D', '117', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'D', '118', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'D', '119', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'D', '120', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'D', '121', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'D', '122', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'D', '123', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'D', '124', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'D', '125', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'D', '126', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'D', '127', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'D', '128', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'D', '129', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'D', '130', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'D', '131', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'D', '132', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'D', '133', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'D', '134', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'D', '135', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'D', '136', 'Beam', '275', '600', true),
    ('Slip', '6', '1', 'E', '1', 'Beam', '200', '600', true),
    ('Slip', '6', '1', 'E', '2', 'Beam', '200', '600', true),
    ('Slip', '6', '2', 'E', '3', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'E', '4', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'E', '5', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'E', '6', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'E', '7', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'E', '8', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'E', '9', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'E', '10', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'E', '11', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'E', '12', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'E', '13', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'E', '14', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'E', '15', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'E', '16', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'E', '17', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'E', '18', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'E', '19', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'E', '20', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'E', '21', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'E', '22', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'E', '23', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'E', '24', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'E', '25', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'E', '26', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'E', '27', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'E', '28', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'E', '29', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'E', '30', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'E', '31', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'E', '32', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'E', '33', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'E', '34', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'E', '35', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'E', '36', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'E', '37', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'E', '38', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'E', '39', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'E', '40', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'E', '41', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'E', '42', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'E', '43', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'E', '44', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'E', '45', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'E', '46', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'E', '47', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'E', '48', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'E', '49', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'E', '50', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'E', '51', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'E', '52', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'E', '53', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'E', '54', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'E', '55', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'E', '56', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'E', '57', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'E', '58', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'E', '59', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'E', '60', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'E', '61', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'E', '62', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'E', '63', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'E', '64', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'E', '65', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'E', '66', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'E', '67', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'E', '68', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'E', '69', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'E', '70', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'E', '71', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'E', '72', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'E', '73', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'E', '74', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'E', '75', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'E', '76', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'E', '77', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'E', '78', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'E', '79', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'E', '80', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'E', '81', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'E', '82', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'E', '83', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'E', '84', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'E', '85', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'E', '86', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'E', '87', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'E', '88', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'E', '89', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'E', '90', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'E', '91', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'E', '92', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'E', '93', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'E', '94', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'E', '95', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'E', '96', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'E', '97', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'E', '98', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'E', '99', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'E', '100', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'E', '101', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'E', '102', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'E', '103', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'E', '104', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'E', '105', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'E', '106', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'E', '107', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'E', '108', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'E', '109', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'E', '110', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'E', '111', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'E', '112', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'E', '113', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'E', '114', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'E', '115', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'E', '116', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'E', '117', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'E', '118', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'E', '119', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'E', '120', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'E', '121', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'E', '122', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'E', '123', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'E', '124', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'E', '125', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'E', '126', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'E', '127', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'E', '128', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'E', '129', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'E', '130', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'E', '131', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'E', '132', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'E', '133', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'E', '134', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'E', '135', 'Beam', '275', '600', true),
    ('Slip', '6', '2', 'E', '136', 'Beam', '275', '600', true),
    ('Slip', '6', '3', 'E', '137', 'Beam', '300', '700', true),
    ('Slip', '6', '3', 'E', '138', 'Beam', '300', '700', true),
    ('Slip', '6', '3', 'E', '139', 'Beam', '310', '700', true),
    ('Slip', '6', '3', 'E', '140', 'Beam', '310', '700', true),
    ('Slip', '6', '3', 'E', '141', 'Beam', '310', '700', true),
    ('Slip', '6', '3', 'E', '142', 'Beam', '310', '700', true),
    ('Slip', '6', '3', 'E', '143', 'Beam', '310', '700', true),
    ('Slip', '6', '3', 'E', '144', 'Beam', '310', '700', true),
    ('Slip', '6', '3', 'E', '145', 'Beam', '310', '700', true),
    ('Slip', '6', '3', 'E', '146', 'Beam', '310', '700', true),
    ('Slip', '6', '3', 'E', '147', 'Beam', '310', '700', true),
    ('Slip', '6', '3', 'E', '148', 'Beam', '310', '700', true),
    ('Slip', '6', '3', 'E', '149', 'Beam', '330', '700', true),
    ('Slip', '6', '3', 'E', '150', 'Beam', '330', '700', true),
    ('Slip', '6', '2', 'F', '1', 'Beam', '275', '650', true),
    ('Slip', '6', '4', 'F', '2', 'RearBuoy', '350', '1700', true),
    ('Slip', '6', '2', 'F', '3', 'Beam', '275', '650', true),
    ('Slip', '6', '4', 'F', '4', 'RearBuoy', '350', '1700', true),
    ('Slip', '6', '2', 'F', '5', 'Beam', '275', '650', true),
    ('Slip', '6', '4', 'F', '6', 'RearBuoy', '350', '1700', true),
    ('Slip', '6', '2', 'F', '7', 'Beam', '280', '650', true),
    ('Slip', '6', '4', 'F', '8', 'RearBuoy', '350', '1700', true),
    ('Slip', '6', '2', 'F', '9', 'Beam', '275', '650', true),
    ('Slip', '6', '4', 'F', '10', 'RearBuoy', '350', '1700', true),
    ('Slip', '6', '2', 'F', '11', 'Beam', '275', '650', true),
    ('Slip', '6', '4', 'F', '12', 'RearBuoy', '350', '1700', true),
    ('Slip', '6', '2', 'F', '13', 'Beam', '275', '650', true),
    ('Slip', '6', '4', 'F', '14', 'RearBuoy', '350', '1700', true),
    ('Slip', '6', '2', 'F', '15', 'Beam', '275', '650', true),
    ('Slip', '6', '4', 'F', '16', 'RearBuoy', '350', '1700', true),
    ('Slip', '6', '2', 'F', '17', 'Beam', '275', '650', true),
    ('Slip', '6', '4', 'F', '18', 'RearBuoy', '350', '1700', true),
    ('Slip', '6', '2', 'F', '19', 'Beam', '275', '650', true),
    ('Slip', '6', '4', 'F', '20', 'RearBuoy', '350', '1700', true),
    ('Slip', '6', '2', 'F', '21', 'Beam', '275', '650', true),
    ('Slip', '6', '4', 'F', '22', 'RearBuoy', '350', '1700', true),
    ('Slip', '6', '2', 'F', '23', 'Beam', '260', '650', true),
    ('Slip', '6', '4', 'F', '24', 'RearBuoy', '350', '1700', true),
    ('Slip', '6', '2', 'F', '25', 'Beam', '280', '650', true),
    ('Slip', '6', '4', 'F', '26', 'RearBuoy', '350', '1700', true),
    ('Slip', '6', '2', 'F', '27', 'Beam', '275', '650', true),
    ('Slip', '6', '4', 'F', '28', 'RearBuoy', '350', '1700', true),
    ('Slip', '6', '2', 'F', '29', 'Beam', '275', '650', true),
    ('Slip', '6', '4', 'F', '30', 'RearBuoy', '350', '1700', true),
    ('Slip', '6', '2', 'F', '31', 'Beam', '275', '650', true),
    ('Slip', '6', '4', 'F', '32', 'RearBuoy', '350', '1700', true),
    ('Slip', '6', '2', 'F', '33', 'Beam', '275', '650', true),
    ('Slip', '6', '4', 'F', '34', 'RearBuoy', '350', '1700', true),
    ('Slip', '6', '2', 'F', '35', 'Beam', '280', '650', true),
    ('Slip', '6', '4', 'F', '36', 'RearBuoy', '350', '1700', true),
    ('Slip', '6', '2', 'F', '37', 'Beam', '275', '650', true),
    ('Slip', '6', '4', 'F', '38', 'RearBuoy', '350', '1700', true),
    ('Slip', '6', '2', 'F', '39', 'Beam', '275', '650', true),
    ('Slip', '6', '4', 'F', '40', 'RearBuoy', '350', '1700', true),
    ('Slip', '6', '2', 'F', '41', 'Beam', '275', '650', true),
    ('Slip', '6', '4', 'F', '42', 'RearBuoy', '350', '1700', true),
    ('Slip', '6', '2', 'F', '43', 'Beam', '275', '650', true),
    ('Slip', '6', '4', 'F', '44', 'RearBuoy', '350', '1700', true),
    ('Slip', '6', '2', 'F', '45', 'Beam', '275', '650', true),
    ('Slip', '6', '4', 'F', '46', 'RearBuoy', '350', '1700', true),
    ('Slip', '6', '2', 'F', '47', 'Beam', '260', '650', true),
    ('Slip', '6', '4', 'F', '48', 'RearBuoy', '350', '1700', true),
    ('Slip', '6', '2', 'F', '49', 'Beam', '280', '650', true),
    ('Slip', '6', '4', 'F', '50', 'RearBuoy', '350', '1700', true),
    ('Slip', '6', '3', 'F', '51', 'Beam', '300', '650', true),
    ('Slip', '6', '4', 'F', '52', 'RearBuoy', '350', '1700', true),
    ('Slip', '6', '3', 'F', '53', 'Beam', '300', '650', true),
    ('Slip', '6', '4', 'F', '54', 'RearBuoy', '350', '1700', true),
    ('Slip', '6', '3', 'F', '55', 'Beam', '300', '650', true),
    ('Slip', '6', '4', 'F', '56', 'RearBuoy', '350', '1700', true),
    ('Slip', '6', '3', 'F', '57', 'Beam', '300', '650', true),
    ('Slip', '6', '4', 'F', '58', 'RearBuoy', '350', '1700', true),
    ('Slip', '6', '3', 'F', '59', 'Beam', '300', '650', true),
    ('Slip', '6', '4', 'F', '60', 'RearBuoy', '350', '1700', true),
    ('Slip', '6', '3', 'F', '61', 'Beam', '300', '650', true),
    ('Slip', '6', '4', 'F', '62', 'RearBuoy', '350', '1700', true),
    ('Slip', '6', '3', 'F', '63', 'Beam', '300', '650', true),
    ('Slip', '6', '4', 'F', '64', 'RearBuoy', '350', '1700', true),
    ('Slip', '6', '3', 'F', '65', 'Beam', '300', '650', true),
    ('Slip', '6', '4', 'F', '66', 'RearBuoy', '350', '1700', true),
    ('Slip', '6', '3', 'F', '67', 'Beam', '300', '650', true),
    ('Slip', '6', '4', 'F', '68', 'RearBuoy', '350', '1700', true),
    ('Slip', '6', '3', 'F', '69', 'Beam', '300', '650', true),
    ('Slip', '6', '4', 'F', '70', 'RearBuoy', '350', '1700', true),
    ('Slip', '6', '3', 'F', '71', 'Beam', '300', '650', true),
    ('Slip', '6', '4', 'F', '72', 'RearBuoy', '350', '1700', true),
    ('Slip', '6', '3', 'F', '73', 'Beam', '300', '650', true),
    ('Slip', '6', '4', 'F', '74', 'RearBuoy', '350', '1700', true),
    ('Slip', '6', '3', 'F', '75', 'Beam', '300', '650', true),
    ('Slip', '6', '4', 'F', '76', 'RearBuoy', '350', '1700', true),
    ('Slip', '6', '3', 'F', '77', 'Beam', '300', '650', true),
    ('Slip', '6', '4', 'F', '78', 'RearBuoy', '350', '1700', true),
    ('Slip', '6', '3', 'F', '79', 'Beam', '300', '650', true),
    ('Slip', '6', '4', 'F', '80', 'RearBuoy', '350', '1700', true),
    ('Slip', '6', '3', 'F', '81', 'Beam', '300', '650', true),
    ('Slip', '6', '4', 'F', '82', 'RearBuoy', '350', '1700', true),
    ('Slip', '6', '3', 'F', '83', 'Beam', '300', '650', true),
    ('Slip', '6', '3', 'F', '84', 'Beam', '300', '650', true),
    ('Slip', '6', '3', 'F', '85', 'Beam', '290', '650', true),
    ('Slip', '6', '3', 'F', '86', 'Beam', '290', '650', true),
    ('Slip', '6', '3', 'F', '87', 'Beam', '310', '650', true),
    ('Slip', '6', '3', 'F', '88', 'Beam', '300', '650', true),
    ('Slip', '6', '3', 'F', '89', 'Beam', '300', '650', true),
    ('Slip', '6', '4', 'F', '90', 'Beam', '340', '650', true),
    ('Slip', '6', '4', 'F', '91', 'Beam', '350', '650', true),
    ('Slip', '6', '3', 'F', '92', 'Beam', '320', '650', true),
    ('Slip', '6', '3', 'F', '93', 'Beam', '320', '650', true),
    ('Slip', '6', '3', 'F', '94', 'Beam', '320', '650', true),
    ('Slip', '6', '3', 'F', '95', 'Beam', '320', '650', true),
    ('Slip', '6', '3', 'F', '96', 'Beam', '320', '650', true),
    ('Slip', '6', '3', 'F', '97', 'Beam', '320', '650', true),
    ('Slip', '6', '3', 'F', '98', 'Beam', '330', '650', true),
    ('Slip', '6', '3', 'F', '99', 'Beam', '330', '650', true),
    ('Slip', '6', '3', 'F', '100', 'Beam', '330', '650', true),
    ('Slip', '6', '3', 'F', '101', 'Beam', '330', '650', true),
    ('Slip', '6', '3', 'F', '102', 'Beam', '310', '650', true),
    ('Slip', '6', '3', 'F', '103', 'Beam', '320', '650', true),
    ('Slip', '6', '3', 'F', '104', 'Beam', '300', '650', true),
    ('Slip', '6', '3', 'F', '105', 'Beam', '300', '550', true),
    ('Slip', '6', '3', 'F', '106', 'Beam', '300', '650', true),
    ('Slip', '6', '3', 'F', '107', 'Beam', '300', '550', true),
    ('Slip', '6', '3', 'F', '108', 'Beam', '300', '550', true),
    ('Slip', '6', '3', 'F', '109', 'Beam', '300', '550', true),
    ('Slip', '6', '3', 'F', '110', 'Beam', '300', '550', true),
    ('Slip', '6', '3', 'F', '111', 'Beam', '300', '550', true),
    ('Slip', '6', '3', 'F', '112', 'Beam', '300', '550', true),
    ('Slip', '6', '3', 'F', '113', 'Beam', '300', '550', true),
    ('Slip', '6', '3', 'F', '114', 'Beam', '300', '550', true),
    ('Slip', '6', '3', 'F', '115', 'Beam', '300', '550', true),
    ('Slip', '6', '3', 'F', '116', 'Beam', '300', '550', true),
    ('Slip', '6', '3', 'F', '117', 'Beam', '300', '550', true),
    ('Slip', '6', '3', 'F', '118', 'Beam', '300', '550', true),
    ('Slip', '6', '3', 'F', '119', 'Beam', '300', '550', true),
    ('Slip', '6', '3', 'F', '120', 'Beam', '300', '550', true),
    ('Slip', '6', '3', 'F', '121', 'Beam', '300', '550', true),
    ('Slip', '6', '3', 'F', '122', 'Beam', '300', '550', true),
    ('Slip', '6', '3', 'F', '123', 'Beam', '300', '550', true),
    ('Slip', '6', '3', 'F', '124', 'Beam', '300', '550', true),
    ('Slip', '6', '3', 'F', '125', 'Beam', '300', '550', true),
    ('Slip', '6', '3', 'F', '126', 'Beam', '300', '550', true),
    ('Slip', '6', '3', 'F', '127', 'Beam', '300', '550', true),
    ('Slip', '6', '3', 'F', '128', 'Beam', '300', '550', true),
    ('Slip', '6', '3', 'F', '129', 'Beam', '300', '550', true),
    ('Slip', '6', '4', 'G', '1', 'RearBuoy', '350', '1700', true),
    ('Slip', '6', '4', 'G', '2', 'RearBuoy', '350', '1700', true),
    ('Slip', '6', '4', 'G', '3', 'RearBuoy', '350', '1700', true),
    ('Slip', '6', '4', 'G', '5', 'RearBuoy', '350', '1700', true),
    ('Slip', '6', '4', 'G', '7', 'RearBuoy', '350', '1700', true),
    ('Slip', '6', '4', 'G', '9', 'RearBuoy', '350', '1700', true),
    ('Slip', '6', '4', 'G', '15', 'RearBuoy', '350', '1700', true),
    ('Slip', '6', '4', 'G', '16', 'RearBuoy', '350', '1700', true),
    ('Slip', '6', '4', 'G', '17', 'RearBuoy', '350', '1700', true),
    ('Slip', '6', '4', 'G', '19', 'RearBuoy', '350', '1700', true),
    ('Slip', '6', '4', 'G', '22', 'RearBuoy', '350', '1700', true),
    ('Slip', '6', '4', 'G', '25', 'RearBuoy', '350', '1700', true),
    ('Slip', '6', '4', 'G', '26', 'RearBuoy', '350', '1700', true),
    ('Slip', '6', '4', 'G', '27', 'RearBuoy', '350', '1700', true),
    ('Slip', '6', '4', 'G', '29', 'RearBuoy', '350', '1700', true),
    ('Slip', '6', '4', 'G', '31', 'RearBuoy', '350', '1700', true),
    ('Slip', '6', '4', 'G', '34', 'RearBuoy', '350', '1700', true),
    ('Slip', '6', '5', 'G', '35', 'RearBuoy', '400', '1700', true),
    ('Slip', '6', '5', 'G', '36', 'RearBuoy', '400', '1700', true),
    ('Slip', '6', '4', 'G', '37', 'RearBuoy', '400', '1700', true),
    ('Slip', '6', '5', 'G', '38', 'RearBuoy', '400', '1700', true),
    ('Slip', '6', '5', 'G', '39', 'RearBuoy', '400', '1700', true),
    ('Slip', '6', '5', 'G', '41', 'RearBuoy', '400', '1700', true),
    ('Slip', '6', '5', 'G', '43', 'RearBuoy', '400', '1700', true),
    ('Slip', '6', '5', 'G', '44', 'RearBuoy', '400', '1700', true),
    ('Slip', '6', '5', 'G', '47', 'RearBuoy', '400', '1700', true),
    ('Slip', '6', '5', 'G', '49', 'RearBuoy', '400', '1700', true),
    ('Slip', '6', '5', 'G', '52', 'RearBuoy', '400', '1700', true),
    ('Slip', '6', '5', 'G', '53', 'RearBuoy', '400', '1700', true),
    ('Slip', '6', '5', 'G', '55', 'RearBuoy', '400', '1700', true),
    ('Slip', '6', '5', 'G', '56', 'RearBuoy', '400', '1700', true),
    ('Slip', '6', '5', 'G', '57', 'RearBuoy', '400', '1700', true),
    ('Slip', '6', '5', 'G', '58', 'RearBuoy', '400', '1700', true),
    ('Slip', '6', '5', 'G', '59', 'RearBuoy', '400', '1700', true),
    ('Slip', '6', '5', 'G', '60', 'RearBuoy', '400', '1700', true),
    ('Slip', '6', '5', 'G', '61', 'RearBuoy', '400', '1700', true),
    ('Slip', '6', '5', 'G', '62', 'RearBuoy', '400', '1700', true),
    ('Slip', '6', '5', 'G', '64', 'RearBuoy', '400', '1700', true),
    ('Slip', '6', '6', 'G', '65', 'RearBuoy', '450', '1700', true),
    ('Slip', '6', '6', 'G', '66', 'RearBuoy', '450', '1700', true),
    ('Slip', '6', '6', 'G', '68', 'RearBuoy', '500', '1700', true),
    ('Slip', '6', '6', 'G', '69', 'RearBuoy', '500', '1700', true),
    ('Slip', '6', '6', 'G', '70', 'RearBuoy', '500', '1700', true),
    ('Slip', '6', '6', 'G', '71', 'RearBuoy', '500', '1700', true),
    ('Slip', '6', '6', 'G', '73', 'RearBuoy', '500', '1700', true),
    ('Slip', '6', '6', 'G', '74', 'RearBuoy', '500', '1700', true),
    ('Slip', '6', '6', 'G', '75', 'RearBuoy', '500', '1700', true),
    ('Slip', '6', '6', 'G', '76', 'RearBuoy', '500', '1700', true),
    ('Slip', '6', '6', 'G', '77', 'RearBuoy', '500', '1700', true),
    ('Slip', '6', '6', 'G', '78', 'RearBuoy', '500', '1700', true),
    ('Slip', '6', '6', 'G', '79', 'RearBuoy', '500', '1700', true),
    ('Slip', '6', '6', 'G', '84', 'RearBuoy', '500', '1700', true),
    ('Slip', '6', '4', 'G', '86', 'RearBuoy', '350', '1700', true),
    ('Slip', '6', '4', 'G', '87', 'RearBuoy', '350', '1700', true),
    ('Slip', '6', '4', 'G', '88', 'RearBuoy', '350', '1700', true),
    ('Slip', '6', '4', 'G', '89', 'RearBuoy', '350', '1700', true),
    ('Slip', '6', '4', 'G', '90', 'RearBuoy', '350', '1700', true),
    ('Slip', '6', '4', 'G', '92', 'RearBuoy', '350', '1700', true),
    ('Slip', '6', '4', 'G', '93', 'RearBuoy', '350', '1700', true),
    ('Slip', '6', '4', 'G', '94', 'RearBuoy', '350', '1700', true),
    ('Slip', '6', '4', 'G', '95', 'RearBuoy', '350', '1700', true),
    ('Slip', '6', '4', 'G', '96', 'RearBuoy', '350', '1700', true),
    ('Slip', '6', '4', 'G', '97', 'RearBuoy', '350', '1700', true),
    ('Slip', '6', '4', 'G', '98', 'RearBuoy', '350', '1700', true),
    ('Slip', '6', '4', 'G', '99', 'RearBuoy', '350', '1700', true),
    ('Slip', '6', '4', 'G', '100', 'RearBuoy', '350', '1700', true),
    ('Slip', '6', '4', 'G', '101', 'RearBuoy', '350', '1700', true),
    ('Slip', '6', '4', 'G', '102', 'RearBuoy', '350', '1700', true),
    ('Slip', '6', '4', 'G', '103', 'RearBuoy', '350', '1700', true),
    ('Slip', '6', '4', 'G', '104', 'RearBuoy', '350', '1700', true),
    ('Slip', '6', '4', 'G', '105', 'RearBuoy', '350', '1700', true),
    ('Slip', '6', '4', 'G', '106', 'RearBuoy', '350', '1700', true),
    ('Slip', '6', '4', 'G', '107', 'RearBuoy', '350', '1700', true),
    ('Slip', '6', '4', 'G', '108', 'RearBuoy', '350', '1700', true),
    ('Slip', '6', '4', 'G', '109', 'RearBuoy', '350', '1700', true),
    ('Slip', '6', '4', 'G', '110', 'RearBuoy', '350', '1700', true),
    ('Slip', '6', '4', 'G', '111', 'RearBuoy', '350', '1700', true),
    ('Slip', '6', '4', 'G', '112', 'RearBuoy', '350', '1700', true),
    ('Slip', '6', '4', 'G', '113', 'RearBuoy', '350', '1700', true),
    ('Slip', '6', '4', 'G', '114', 'RearBuoy', '350', '1700', true),
    ('Slip', '6', '4', 'G', '115', 'RearBuoy', '350', '1700', true),
    ('Slip', '6', '4', 'G', '116', 'RearBuoy', '350', '1700', true),
    ('Slip', '6', '6', 'G', '117', 'RearBuoy', '500', '1700', true),
    ('Slip', '6', '2', 'J', '1', 'Beam', '260', '650', true),
    ('Slip', '6', '2', 'J', '3', 'Beam', '240', '650', true),
    ('Slip', '6', '2', 'J', '6', 'Beam', '250', '650', true),
    ('Slip', '6', '2', 'J', '7', 'Beam', '275', '650', true),
    ('Slip', '6', '2', 'J', '8', 'Beam', '275', '650', true),
    ('Slip', '6', '2', 'J', '10', 'Beam', '275', '650', true),
    ('Slip', '6', '2', 'J', '11', 'Beam', '275', '650', true),
    ('Slip', '6', '2', 'J', '12', 'Beam', '275', '650', true),
    ('Slip', '6', '2', 'J', '13', 'Beam', '275', '650', true),
    ('Slip', '6', '2', 'J', '14', 'Beam', '275', '650', true),
    ('Slip', '6', '2', 'J', '15', 'Beam', '275', '650', true),
    ('Slip', '6', '2', 'J', '16', 'Beam', '275', '650', true),
    ('Slip', '6', '2', 'J', '17', 'Beam', '275', '650', true),
    ('Slip', '6', '2', 'J', '18', 'Beam', '275', '650', true),
    ('Slip', '6', '2', 'J', '19', 'Beam', '275', '650', true),
    ('Slip', '6', '2', 'J', '20', 'Beam', '275', '650', true),
    ('Slip', '6', '2', 'J', '21', 'Beam', '275', '650', true),
    ('Slip', '6', '2', 'J', '22', 'Beam', '275', '650', true),
    ('Slip', '6', '2', 'J', '23', 'Beam', '275', '650', true),
    ('Slip', '6', '2', 'J', '27', 'Beam', '275', '650', true),
    ('Slip', '6', '2', 'J', '28', 'Beam', '275', '650', true),
    ('Slip', '6', '3', 'J', '29', 'WalkBeam', '330', '900', true),
    ('Slip', '6', '3', 'J', '31', 'WalkBeam', '330', '900', true),
    ('Slip', '6', '3', 'J', '33', 'WalkBeam', '330', '900', true),
    ('Slip', '6', '3', 'J', '37', 'WalkBeam', '330', '900', true),
    ('Slip', '6', '3', 'J', '41', 'WalkBeam', '330', '900', true),
    ('Slip', '6', '4', 'J', '53', 'WalkBeam', '360', '1000', true),
    ('Slip', '6', '4', 'J', '55', 'WalkBeam', '380', '1000', true),
    ('Slip', '6', '4', 'J', '57', 'WalkBeam', '380', '1000', true),
    ('Slip', '6', '4', 'J', '59', 'WalkBeam', '380', '1000', true),
    ('Slip', '6', '4', 'J', '63', 'WalkBeam', '380', '1000', true),
    ('Slip', '6', '4', 'J', '65', 'WalkBeam', '380', '1000', true),
    ('Slip', '6', '4', 'J', '67', 'WalkBeam', '380', '1000', true),
    ('Slip', '6', '4', 'J', '69', 'WalkBeam', '380', '1000', true),
    ('Slip', '6', '4', 'J', '71', 'WalkBeam', '380', '1000', true),
    ('Trailer', '6', '3', 'TRAILERI', '1', 'None', '260', '800', true),
    ('Trailer', '6', '3', 'TRAILERI', '2', 'None', '260', '800', true),
    ('Trailer', '6', '3', 'TRAILERI', '3', 'None', '260', '800', true),
    ('Trailer', '6', '3', 'TRAILERI', '4', 'None', '260', '800', true),
    ('Trailer', '6', '3', 'TRAILERI', '5', 'None', '260', '800', true),
    ('Trailer', '6', '3', 'TRAILERI', '6', 'None', '260', '800', true),
    ('Trailer', '6', '3', 'TRAILERI', '7', 'None', '260', '800', true),
    ('Trailer', '6', '3', 'TRAILERI', '8', 'None', '260', '800', true),
    ('Trailer', '6', '3', 'TRAILERI', '9', 'None', '260', '800', true),
    ('Trailer', '6', '3', 'TRAILERI', '10', 'None', '260', '800', true),
    ('Trailer', '6', '3', 'TRAILERI', '11', 'None', '260', '800', true),
    ('Trailer', '6', '3', 'TRAILERI', '12', 'None', '260', '700', true),
    ('Trailer', '6', '3', 'TRAILERI', '13', 'None', '260', '700', true),
    ('Trailer', '6', '3', 'TRAILERI', '14', 'None', '260', '700', true),
    ('Trailer', '6', '3', 'TRAILERI', '15', 'None', '260', '700', true),
    ('Trailer', '6', '3', 'TRAILERI', '16', 'None', '260', '700', true),
    ('Trailer', '6', '3', 'TRAILERI', '17', 'None', '260', '700', true),
    ('Trailer', '6', '3', 'TRAILERI', '18', 'None', '260', '700', true),
    ('Trailer', '6', '3', 'TRAILERI', '19', 'None', '260', '700', true),
    ('Trailer', '6', '3', 'TRAILERI', '20', 'None', '260', '700', true),
    ('Trailer', '6', '3', 'TRAILERI', '21', 'None', '260', '700', true),
    ('Trailer', '6', '3', 'TRAILERI', '22', 'None', '260', '700', true),
    ('Trailer', '6', '3', 'TRAILERI', '23', 'None', '260', '700', true),
    ('Trailer', '6', '3', 'TRAILERI', '24', 'None', '260', '700', true),
    ('Trailer', '6', '3', 'TRAILERI', '25', 'None', '260', '700', true),
    ('Trailer', '6', '3', 'TRAILERI', '26', 'None', '260', '700', true),
    ('Trailer', '6', '3', 'TRAILERI', '27', 'None', '260', '700', true),
    ('Trailer', '6', '3', 'TRAILERI', '28', 'None', '260', '700', true),
    ('Storage', '7', '3', 'A', '1', 'Trailer', '300', '550', true),
    ('Storage', '7', '3', 'A', '2', 'Trailer', '310', '500', true),
    ('Storage', '7', '3', 'A', '3', 'Trailer', '290', '550', true),
    ('Storage', '7', '3', 'A', '4', 'Trailer', '320', '500', true),
    ('Storage', '7', '2', 'A', '5', 'Trailer', '260', '550', true),
    ('Storage', '7', '2', 'A', '6', 'Trailer', '260', '500', true),
    ('Slip', '7', '2', 'A', '7', 'Beam', '275', '550', true),
    ('Slip', '7', '2', 'A', '8', 'Beam', '275', '500', true),
    ('Slip', '7', '2', 'A', '9', 'Beam', '270', '550', true),
    ('Slip', '7', '2', 'A', '10', 'Beam', '270', '500', true),
    ('Slip', '7', '2', 'A', '11', 'Beam', '280', '550', true),
    ('Slip', '7', '2', 'A', '12', 'Beam', '280', '500', true),
    ('Slip', '7', '2', 'A', '13', 'Beam', '260', '550', true),
    ('Slip', '7', '2', 'A', '14', 'Beam', '260', '500', true),
    ('Slip', '7', '2', 'A', '16', 'Beam', '280', '500', true),
    ('Slip', '7', '2', 'A', '18', 'Beam', '250', '500', true),
    ('Slip', '7', '2', 'A', '20', 'Beam', '280', '500', true),
    ('Slip', '7', '2', 'A', '22', 'Beam', '280', '500', true),
    ('Slip', '7', '2', 'A', '24', 'Beam', '260', '500', true),
    ('Slip', '7', '2', 'A', '26', 'Beam', '280', '500', true),
    ('Slip', '7', '2', 'A', '28', 'Beam', '260', '500', true),
    ('Slip', '7', '2', 'A', '30', 'Beam', '270', '500', true),
    ('Slip', '7', '2', 'A', '32', 'Beam', '270', '500', true),
    ('Slip', '7', '2', 'A', '34', 'Beam', '275', '500', true),
    ('Slip', '7', '2', 'A', '36', 'Beam', '270', '500', true),
    ('Slip', '7', '2', 'A', '38', 'Beam', '270', '500', true),
    ('Slip', '7', '2', 'A', '40', 'Beam', '260', '500', true),
    ('Slip', '7', '2', 'A', '42', 'Beam', '280', '500', true),
    ('Slip', '7', '2', 'A', '44', 'Beam', '270', '500', true),
    ('Slip', '7', '2', 'A', '46', 'Beam', '275', '600', true),
    ('Slip', '7', '2', 'A', '48', 'Beam', '260', '600', true),
    ('Slip', '7', '2', 'A', '50', 'Beam', '250', '600', true),
    ('Slip', '7', '2', 'A', '52', 'Beam', '250', '600', true),
    ('Slip', '7', '3', 'A', '54', 'Beam', '300', '600', true),
    ('Slip', '7', '3', 'A', '56', 'Beam', '300', '600', true),
    ('Slip', '7', '3', 'A', '58', 'Beam', '300', '600', true),
    ('Slip', '7', '3', 'A', '60', 'Beam', '300', '600', true),
    ('Slip', '7', '3', 'A', '62', 'Beam', '300', '600', true),
    ('Slip', '7', '3', 'A', '64', 'Beam', '300', '600', true),
    ('Slip', '7', '3', 'A', '66', 'Beam', '300', '600', true),
    ('Slip', '7', '1', 'B', '1', 'Beam', '220', '500', true),
    ('Slip', '7', '1', 'B', '2', 'Beam', '200', '550', true),
    ('Slip', '7', '2', 'B', '3', 'Beam', '250', '500', true),
    ('Slip', '7', '2', 'B', '4', 'Beam', '275', '500', true),
    ('Slip', '7', '2', 'B', '5', 'Beam', '275', '500', true),
    ('Slip', '7', '2', 'B', '6', 'Beam', '275', '500', true),
    ('Slip', '7', '2', 'B', '7', 'Beam', '275', '500', true),
    ('Slip', '7', '2', 'B', '8', 'Beam', '275', '500', true),
    ('Slip', '7', '2', 'B', '9', 'Beam', '275', '500', true),
    ('Slip', '7', '2', 'B', '10', 'Beam', '275', '500', true),
    ('Slip', '7', '2', 'B', '11', 'Beam', '275', '500', true),
    ('Slip', '7', '2', 'B', '12', 'Beam', '275', '500', true),
    ('Slip', '7', '2', 'B', '13', 'Beam', '275', '500', true),
    ('Slip', '7', '2', 'B', '14', 'Beam', '275', '500', true),
    ('Slip', '7', '2', 'B', '15', 'Beam', '275', '500', true),
    ('Slip', '7', '2', 'B', '16', 'Beam', '275', '500', true),
    ('Slip', '7', '2', 'B', '17', 'Beam', '275', '500', true),
    ('Slip', '7', '2', 'B', '18', 'Beam', '275', '500', true),
    ('Slip', '7', '2', 'B', '19', 'Beam', '275', '500', true),
    ('Slip', '7', '2', 'B', '20', 'Beam', '275', '550', true),
    ('Slip', '7', '2', 'B', '21', 'Beam', '275', '550', true),
    ('Slip', '7', '2', 'B', '22', 'Beam', '275', '550', true),
    ('Slip', '7', '2', 'B', '23', 'Beam', '275', '550', true),
    ('Slip', '7', '2', 'B', '24', 'Beam', '275', '550', true),
    ('Slip', '7', '2', 'B', '25', 'Beam', '275', '550', true),
    ('Slip', '7', '2', 'B', '26', 'Beam', '275', '550', true),
    ('Slip', '7', '2', 'B', '27', 'Beam', '275', '600', true),
    ('Slip', '7', '2', 'B', '28', 'Beam', '275', '550', true),
    ('Slip', '7', '2', 'B', '29', 'Beam', '275', '600', true),
    ('Slip', '7', '2', 'B', '30', 'Beam', '275', '600', true),
    ('Slip', '7', '2', 'B', '31', 'Beam', '275', '600', true),
    ('Slip', '7', '2', 'B', '32', 'Beam', '275', '600', true),
    ('Slip', '7', '2', 'B', '33', 'Beam', '275', '600', true),
    ('Slip', '7', '2', 'B', '34', 'Beam', '275', '600', true),
    ('Slip', '7', '2', 'B', '35', 'Beam', '275', '600', true),
    ('Slip', '7', '2', 'B', '36', 'Beam', '275', '600', true),
    ('Slip', '7', '2', 'B', '37', 'Beam', '275', '600', true),
    ('Slip', '7', '2', 'B', '38', 'Beam', '275', '600', true),
    ('Slip', '7', '2', 'B', '39', 'Beam', '275', '600', true),
    ('Slip', '7', '2', 'B', '40', 'Beam', '275', '600', true),
    ('Slip', '7', '2', 'B', '41', 'Beam', '275', '600', true),
    ('Slip', '7', '2', 'B', '42', 'Beam', '275', '600', true),
    ('Slip', '7', '3', 'B', '43', 'Beam', '300', '600', true),
    ('Slip', '7', '3', 'B', '44', 'Beam', '300', '600', true),
    ('Slip', '7', '3', 'B', '45', 'Beam', '300', '600', true),
    ('Slip', '7', '3', 'B', '46', 'Beam', '300', '600', true),
    ('Slip', '7', '3', 'B', '47', 'Beam', '300', '600', true),
    ('Slip', '7', '3', 'B', '48', 'Beam', '300', '600', true),
    ('Slip', '7', '3', 'B', '49', 'Beam', '300', '600', true),
    ('Slip', '7', '3', 'B', '50', 'Beam', '300', '600', true),
    ('Slip', '7', '3', 'B', '51', 'Beam', '300', '600', true),
    ('Slip', '7', '3', 'B', '52', 'Beam', '300', '600', true),
    ('Slip', '7', '3', 'B', '53', 'Beam', '300', '600', true),
    ('Slip', '7', '3', 'B', '54', 'Beam', '300', '600', true),
    ('Slip', '7', '3', 'B', '55', 'Beam', '300', '600', true),
    ('Slip', '7', '3', 'B', '56', 'Beam', '300', '600', true),
    ('Slip', '7', '3', 'B', '57', 'Beam', '300', '600', true),
    ('Slip', '7', '3', 'B', '58', 'Beam', '300', '600', true),
    ('Slip', '7', '5', 'B', '59', 'WalkBeam', '400', '1000', true),
    ('Slip', '7', '5', 'B', '60', 'WalkBeam', '400', '1000', true),
    ('Slip', '7', '5', 'B', '61', 'WalkBeam', '400', '1000', true),
    ('Slip', '7', '5', 'B', '62', 'WalkBeam', '400', '1000', true),
    ('Slip', '7', '2', 'C', '1', 'Beam', '275', '450', true),
    ('Slip', '7', '2', 'C', '2', 'Beam', '260', '550', true),
    ('Slip', '7', '2', 'C', '3', 'Beam', '275', '450', true),
    ('Slip', '7', '2', 'C', '4', 'Beam', '260', '450', true),
    ('Slip', '7', '2', 'C', '5', 'Beam', '280', '450', true),
    ('Slip', '7', '2', 'C', '6', 'Beam', '275', '450', true),
    ('Slip', '7', '3', 'C', '7', 'Beam', '300', '550', true),
    ('Slip', '7', '2', 'C', '8', 'Beam', '275', '450', true),
    ('Slip', '7', '3', 'C', '9', 'Beam', '300', '550', true),
    ('Slip', '7', '3', 'C', '10', 'Beam', '290', '450', true),
    ('Slip', '7', '3', 'C', '11', 'Beam', '290', '550', true),
    ('Slip', '7', '3', 'C', '12', 'Beam', '300', '550', true),
    ('Slip', '7', '3', 'C', '13', 'Beam', '300', '550', true),
    ('Slip', '7', '3', 'C', '14', 'Beam', '300', '550', true),
    ('Slip', '7', '3', 'C', '15', 'Beam', '300', '550', true),
    ('Slip', '7', '3', 'C', '16', 'Beam', '300', '550', true),
    ('Slip', '7', '3', 'C', '17', 'Beam', '300', '550', true),
    ('Slip', '7', '3', 'C', '18', 'Beam', '300', '550', true),
    ('Slip', '7', '3', 'C', '19', 'Beam', '300', '550', true),
    ('Slip', '7', '3', 'C', '20', 'Beam', '300', '550', true),
    ('Slip', '7', '3', 'C', '21', 'Beam', '300', '550', true),
    ('Slip', '7', '3', 'C', '22', 'Beam', '300', '550', true),
    ('Slip', '7', '3', 'C', '23', 'Beam', '290', '550', true),
    ('Slip', '7', '3', 'C', '24', 'Beam', '300', '550', true),
    ('Slip', '7', '3', 'C', '25', 'Beam', '300', '550', true),
    ('Slip', '7', '3', 'C', '26', 'Beam', '300', '550', true),
    ('Slip', '7', '3', 'C', '27', 'Beam', '300', '550', true),
    ('Slip', '7', '3', 'C', '28', 'Beam', '290', '550', true),
    ('Slip', '7', '3', 'C', '29', 'Beam', '300', '550', true),
    ('Slip', '7', '3', 'C', '30', 'Beam', '300', '550', true),
    ('Slip', '7', '3', 'C', '31', 'Beam', '300', '550', true),
    ('Slip', '7', '3', 'C', '32', 'Beam', '300', '550', true),
    ('Storage', '7', '3', 'C', '33', 'Buck', '300', '550', true),
    ('Storage', '7', '3', 'C', '34', 'Buck', '300', '550', true),
    ('Storage', '7', '3', 'C', '35', 'Buck', '300', '550', true),
    ('Storage', '7', '3', 'C', '36', 'Buck', '300', '550', true),
    ('Storage', '7', '3', 'C', '37', 'Buck', '300', '550', true),
    ('Storage', '7', '3', 'C', '38', 'Buck', '300', '550', true),
    ('Storage', '7', '3', 'C', '39', 'Buck', '300', '550', true),
    ('Slip', '7', '3', 'C', '40', 'Beam', '300', '550', true),
    ('Slip', '7', '3', 'C', '41', 'Beam', '300', '550', true),
    ('Slip', '7', '3', 'C', '42', 'Beam', '300', '550', true),
    ('Slip', '7', '3', 'C', '43', 'Beam', '290', '550', true),
    ('Slip', '7', '3', 'C', '44', 'Beam', '300', '550', true),
    ('Slip', '7', '3', 'C', '46', 'Beam', '300', '550', true),
    ('Slip', '7', '3', 'C', '48', 'Beam', '300', '550', true),
    ('Slip', '7', '2', 'D', '3', 'Beam', '275', '600', true),
    ('Slip', '7', '2', 'D', '5', 'Beam', '270', '600', true),
    ('Slip', '7', '2', 'D', '7', 'Beam', '280', '600', true),
    ('Slip', '7', '2', 'D', '9', 'Beam', '275', '600', true),
    ('Slip', '7', '2', 'D', '11', 'Beam', '270', '600', true),
    ('Slip', '7', '2', 'D', '15', 'Beam', '275', '600', true),
    ('Slip', '7', '2', 'D', '17', 'Beam', '275', '600', true),
    ('Slip', '7', '2', 'D', '19', 'Beam', '275', '600', true),
    ('Slip', '7', '2', 'D', '21', 'Beam', '270', '600', true),
    ('Slip', '7', '2', 'D', '27', 'Beam', '270', '600', true),
    ('Slip', '7', '3', 'D', '31', 'Beam', '300', '600', true),
    ('Slip', '7', '3', 'D', '33', 'Beam', '300', '600', true),
    ('Slip', '7', '3', 'D', '35', 'Beam', '300', '600', true),
    ('Slip', '7', '3', 'D', '37', 'Beam', '300', '600', true),
    ('Slip', '7', '3', 'D', '41', 'Beam', '300', '600', true),
    ('Slip', '7', '3', 'D', '45', 'Beam', '300', '600', true),
    ('Slip', '7', '3', 'D', '47', 'Beam', '300', '600', true),
    ('Slip', '7', '3', 'E', '2', 'Beam', '300', '650', true),
    ('Slip', '7', '6', 'E', '3', 'WalkBeam', '500', '1000', true),
    ('Slip', '7', '3', 'E', '4', 'Beam', '300', '650', true),
    ('Slip', '7', '6', 'E', '5', 'WalkBeam', '500', '1000', true),
    ('Slip', '7', '3', 'E', '6', 'WalkBeam', '330', '900', true),
    ('Slip', '7', '4', 'E', '8', 'WalkBeam', '380', '900', true),
    ('Slip', '7', '6', 'E', '9', 'WalkBeam', '500', '1000', true),
    ('Slip', '7', '4', 'E', '10', 'WalkBeam', '380', '900', true),
    ('Slip', '7', '6', 'E', '11', 'WalkBeam', '459', '1000', true),
    ('Slip', '7', '4', 'E', '12', 'WalkBeam', '380', '900', true),
    ('Slip', '7', '5', 'E', '13', 'WalkBeam', '420', '1000', true),
    ('Slip', '7', '4', 'E', '14', 'WalkBeam', '380', '900', true),
    ('Slip', '7', '5', 'E', '17', 'WalkBeam', '420', '1000', true),
    ('Slip', '7', '4', 'E', '18', 'WalkBeam', '380', '900', true),
    ('Slip', '7', '4', 'E', '20', 'WalkBeam', '380', '900', true),
    ('Slip', '7', '4', 'E', '21', 'WalkBeam', '380', '1000', true),
    ('Slip', '7', '4', 'E', '22', 'WalkBeam', '380', '900', true),
    ('Slip', '7', '4', 'E', '23', 'WalkBeam', '380', '900', true),
    ('Slip', '7', '4', 'E', '24', 'WalkBeam', '380', '900', true),
    ('Slip', '7', '4', 'E', '26', 'WalkBeam', '380', '900', true),
    ('Slip', '7', '4', 'E', '27', 'WalkBeam', '380', '900', true),
    ('Slip', '7', '4', 'E', '28', 'WalkBeam', '380', '900', true),
    ('Slip', '7', '4', 'E', '30', 'WalkBeam', '380', '900', true),
    ('Slip', '7', '4', 'E', '32', 'WalkBeam', '380', '900', true),
    ('Slip', '7', '4', 'E', '34', 'WalkBeam', '380', '900', true),
    ('Slip', '7', '4', 'E', '36', 'WalkBeam', '380', '900', true),
    ('Slip', '7', '5', 'E', '41', 'WalkBeam', '420', '900', true),
    ('Slip', '7', '4', 'E', '42', 'WalkBeam', '380', '900', true),
    ('Slip', '7', '4', 'E', '44', 'WalkBeam', '380', '900', true),
    ('Slip', '7', '5', 'E', '45', 'WalkBeam', '420', '900', true),
    ('Slip', '7', '4', 'E', '46', 'WalkBeam', '380', '900', true),
    ('Slip', '7', '5', 'E', '47', 'WalkBeam', '420', '900', true),
    ('Slip', '7', '4', 'E', '48', 'WalkBeam', '380', '900', true),
    ('Slip', '7', '4', 'E', '52', 'WalkBeam', '380', '900', true),
    ('Slip', '7', '4', 'E', '54', 'WalkBeam', '380', '1000', true),
    ('Slip', '7', '4', 'E', '58', 'WalkBeam', '380', '1000', true),
    ('Slip', '7', '4', 'E', '60', 'WalkBeam', '380', '1000', true),
    ('Slip', '7', '4', 'E', '62', 'WalkBeam', '380', '1000', true),
    ('Slip', '7', '4', 'E', '64', 'WalkBeam', '380', '1000', true),
    ('Slip', '7', '4', 'E', '66', 'WalkBeam', '380', '1000', true),
    ('Slip', '7', '3', 'E', '68', 'WalkBeam', '320', '1000', true),
    ('Slip', '7', '2', 'Ä', '1', 'Beam', '250', '500', true),
    ('Slip', '6', '5', 'POIJU', '1', 'Buoy', '100000', '100000', true);


INSERT INTO trailer (registration_code, reserver_id, width_cm, length_cm)
VALUES ('ABC123', '509edb00-5549-11ef-a1c7-776e76028a49', 200, 300);

/* System current date for tests 2024-04-01 */
INSERT INTO boat_space_reservation (reserver_id, boat_space_id, created, start_date, end_date,  status, boat_id, validity, trailer_id, storage_type)
VALUES ('f5d377ea-5547-11ef-a1c7-7f2b94cf9afd', 1,'2024-02-01T00:00:00','2024-02-01', '2025-01-31', 'Confirmed', 1, 'Indefinite', null, null),
       ('509edb00-5549-11ef-a1c7-776e76028a49', 2, '2024-02-01T00:00:00', '2024-02-01', '2024-12-31', 'Confirmed', 2, 'FixedTerm', null, null),
       ('509edb00-5549-11ef-a1c7-776e76028a49', 2, '2023-02-01T00:00:00', '2023-02-01', '2023-12-31', 'Invoiced', 2, 'FixedTerm', null, null),
       ('509edb00-5549-11ef-a1c7-776e76028a49', 2, '2022-02-01T00:00:00', '2022-02-01', '2022-12-31', 'Confirmed', 2, 'FixedTerm', null, null),
       ('509edb00-5549-11ef-a1c7-776e76028a49', 2, '2021-02-01T00:00:00', '2021-02-01', '2021-12-31', 'Payment', 2, 'FixedTerm', null, null),
       ('509edb00-5549-11ef-a1c7-776e76028a49', 8, '2024-02-01T00:00:00', '2024-02-01', '2025-08-31', 'Confirmed', 2, 'Indefinite', 1, 'Trailer'),
       ('8b220a43-86a0-4054-96f6-d29a5aba17e7', 3, '2024-02-01T00:00:00', '2024-02-01', '2025-01-31', 'Confirmed', 5, 'Indefinite', null, null),
       ('82722a75-793a-4cbe-a3d9-a3043f2f5731', 1185, '2024-02-01T00:00:00', '2024-02-01', '2025-01-31', 'Confirmed', 7, 'Indefinite', null, null);

-- Set the default staging system date to 2024-04-01
INSERT INTO variable (id, value)
VALUES ('current_system_staging_datetime', '2024-04-01T00:00:00');

