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

Jos vaihdoit laituripaikkaa saat myöhemmin postissa kausitarran ja satamakartan, jossa avainkoodi laiturin portin avaimen teettämistä varten jos tarvitset uuden avaimen (Otsolahden F-laiturille ei ole porttia). Kausitarra tulee kiinnittää näkyvälle paikalle veneeseen tai jos varasit talvi- tai säilytyspaikan, suojatelttaan, traileriin tai pukkiin. .{{citizenReserverFi}}

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


INSERT INTO boat_space (id, type, location_id, price_id, section, place_number, amenity, width_cm, length_cm, description, is_active) VALUES
    ('1', 'Slip', '1', '2', 'B', '1', 'Beam', '250', '450','none' , true),
    ('2', 'Slip', '1', '2', 'B', '3', 'Beam', '250', '450','none' , true),
    ('3', 'Slip', '1', '2', 'B', '5', 'Beam', '250', '450','none' , true),
    ('4', 'Storage', '1', '2', 'B', '7', 'Trailer', '250', '450','none' , true),
    ('5', 'Storage', '1', '2', 'B', '9', 'Trailer', '250', '450','none' , true),
    ('6', 'Storage', '1', '2', 'B', '11', 'Buck', '250', '450','none' , true),
    ('7', 'Winter', '1', '2', 'B', '13', 'None', '250', '450','none' , true),
    ('8', 'Winter', '1', '2', 'B', '15', 'None', '250', '450','none' , true),
    ('9', 'Winter', '1', '2', 'B', '17', 'None', '250', '450','none' , true),
    ('10', 'Winter', '1', '2', 'B', '19', 'None', '250', '450','none' , true),
    ('11', 'Winter', '1', '2', 'B', '21', 'None', '275', '550','none' , true),
    ('12', 'Winter', '1', '2', 'B', '23', 'None', '275', '550','none' , true),
    ('13', 'Winter', '1', '2', 'B', '25', 'None', '275', '550','none' , true),
    ('14', 'Winter', '1', '2', 'B', '27', 'None', '275', '550','none' , true),
    ('15', 'Winter', '1', '2', 'B', '29', 'None', '275', '550','none' , true),
    ('16', 'Winter', '1', '2', 'B', '31', 'None', '275', '550','none' , true),
    ('17', 'Winter', '1', '2', 'B', '33', 'None', '275', '550','none' , true),
    ('18', 'Winter', '1', '2', 'B', '35', 'None', '275', '550','none' , true),
    ('19', 'Winter', '1', '2', 'B', '37', 'None', '275', '550','none' , true),
    ('20', 'Winter', '1', '2', 'B', '39', 'None', '275', '550','none' , true),
    ('21', 'Winter', '1', '2', 'B', '41', 'None', '275', '550','none' , true),
    ('22', 'Winter', '1', '2', 'B', '43', 'None', '275', '550','none' , true),
    ('23', 'Winter', '1', '2', 'B', '45', 'None', '275', '550','none' , true),
    ('24', 'Winter', '1', '2', 'B', '47', 'None', '275', '550','none' , true),
    ('25', 'Winter', '1', '2', 'B', '49', 'None', '275', '550','none' , true),
    ('26', 'Winter', '1', '2', 'B', '51', 'None', '275', '550','none' , true),
    ('27', 'Winter', '1', '2', 'B', '53', 'None', '275', '550','none' , true),
    ('28', 'Winter', '1', '2', 'B', '55', 'None', '275', '550','none' , true),
    ('29', 'Winter', '1', '2', 'B', '57', 'None', '275', '550','none' , true),
    ('30', 'Slip', '1', '2', 'B', '59', 'Beam', '275', '550', 'none', true),
    ('31', 'Slip', '1', '2', 'B', '61', 'Beam', '275', '550', 'none', false),
    ('32', 'Slip', '1', '2', 'B', '63', 'Beam', '275', '550', 'none', false),
    ('33', 'Slip', '1', '2', 'B', '65', 'Beam', '275', '550', 'none', false),
    ('34', 'Slip', '1', '2', 'B', '67', 'Beam', '275', '550','none' , false),
    ('35', 'Slip', '1', '2', 'B', '69', 'Beam', '275', '550','none' , true),
    ('36', 'Slip', '1', '2', 'B', '71', 'Beam', '275', '550','none' , true),
    ('37', 'Slip', '1', '2', 'B', '73', 'Beam', '275', '550','none' , true),
    ('38', 'Slip', '1', '4', 'B', '231', 'WalkBeam', '350', '900','none' , true),
    ('39', 'Slip', '1', '4', 'B', '233', 'WalkBeam', '350', '900','none' , true),
    ('40', 'Slip', '1', '4', 'B', '235', 'WalkBeam', '350', '900','none' , true),
    ('41', 'Slip', '1', '4', 'B', '237', 'WalkBeam', '350', '900','none' , true),
    ('42', 'Slip', '1', '4', 'B', '239', 'WalkBeam', '350', '900','none' , true),
    ('43', 'Slip', '1', '4', 'B', '241', 'WalkBeam', '350', '900','none' , true),
    ('44', 'Slip', '1', '4', 'B', '243', 'WalkBeam', '350', '900','none' , true),
    ('45', 'Slip', '1', '4', 'B', '245', 'WalkBeam', '350', '900','none' , true),
    ('46', 'Slip', '1', '4', 'B', '247', 'WalkBeam', '350', '900','none' , true),
    ('47', 'Slip', '1', '4', 'B', '249', 'WalkBeam', '350', '900','none' , true),
    ('48', 'Slip', '1', '4', 'B', '251', 'WalkBeam', '350', '900','none' , true),
    ('49', 'Slip', '1', '4', 'B', '253', 'WalkBeam', '350', '900','none' , true),
    ('50', 'Slip', '1', '4', 'B', '255', 'WalkBeam', '350', '900','none' , true),
    ('51', 'Slip', '1', '4', 'B', '257', 'WalkBeam', '350', '900','none' , true),
    ('52', 'Slip', '1', '3', 'B', '310', 'Beam', '290', '650','none' , true),
    ('53', 'Slip', '1', '3', 'B', '311', 'Beam', '300', '650','none' , true),
    ('54', 'Slip', '1', '3', 'B', '312', 'Beam', '300', '650','none' , true),
    ('55', 'Slip', '1', '3', 'B', '313', 'Beam', '300', '650','none' , true),
    ('56', 'Slip', '1', '4', 'B', '314', 'Beam', '350', '650','none' , true),
    ('57', 'Slip', '1', '4', 'B', '315', 'Beam', '350', '650','none' , true),
    ('58', 'Slip', '1', '3', 'B', '316', 'Beam', '300', '650','none' , true),
    ('59', 'Slip', '1', '3', 'B', '317', 'Beam', '300', '650','none' , true),
    ('60', 'Slip', '1', '3', 'B', '318', 'Beam', '300', '650','none' , true),
    ('61', 'Slip', '1', '3', 'B', '319', 'Beam', '290', '650','none' , true),
    ('62', 'Slip', '1', '2', 'D', '1', 'Beam', '250', '600','none' , true),
    ('63', 'Slip', '1', '2', 'D', '2', 'Beam', '250', '700','none' , true),
    ('64', 'Slip', '1', '3', 'D', '3', 'Beam', '290', '600','none' , true),
    ('65', 'Slip', '1', '3', 'D', '4', 'Beam', '290', '700','none' , true),
    ('66', 'Slip', '1', '3', 'D', '5', 'Beam', '290', '600','none' , true),
    ('67', 'Slip', '1', '3', 'D', '6', 'Beam', '290', '700','none' , true),
    ('68', 'Slip', '1', '3', 'D', '7', 'Beam', '290', '600','none' , true),
    ('69', 'Slip', '1', '3', 'D', '8', 'Beam', '290', '700','none' , true),
    ('70', 'Slip', '1', '3', 'D', '9', 'Beam', '290', '600','none' , true),
    ('71', 'Slip', '1', '3', 'D', '10', 'Beam', '290', '600','none' , true),
    ('72', 'Slip', '1', '3', 'D', '11', 'Beam', '285', '600','none' , true),
    ('73', 'Slip', '1', '3', 'D', '12', 'Beam', '290', '700','none' , true),
    ('74', 'Slip', '1', '4', 'D', '13', 'Beam', '350', '600','none' , true),
    ('75', 'Slip', '1', '4', 'D', '14', 'Beam', '350', '700','none' , true),
    ('76', 'Slip', '1', '3', 'D', '15', 'Beam', '290', '600','none' , true),
    ('77', 'Slip', '1', '2', 'D', '16', 'Beam', '280', '700','none' , true),
    ('78', 'Slip', '1', '3', 'D', '17', 'Beam', '295', '600','none' , true),
    ('79', 'Slip', '1', '3', 'D', '18', 'Beam', '310', '700','none' , true),
    ('80', 'Slip', '1', '3', 'D', '19', 'Beam', '290', '600','none' , true),
    ('81', 'Slip', '1', '3', 'D', '20', 'Beam', '300', '700','none' , true),
    ('82', 'Slip', '1', '5', 'D', '22', 'Beam', '405', '700','none' , true),
    ('83', 'Slip', '1', '5', 'D', '23', 'Beam', '390', '600','none' , true),
    ('84', 'Slip', '1', '5', 'D', '24', 'Beam', '390', '700','none' , true),
    ('85', 'Slip', '1', '2', 'E', '1', 'WalkBeam', '280', '800','none' , true),
    ('86', 'Slip', '1', '2', 'E', '2', 'WalkBeam', '280', '800','none' , true),
    ('87', 'Slip', '1', '3', 'E', '3', 'WalkBeam', '300', '800','none' , true),
    ('88', 'Slip', '1', '3', 'E', '4', 'WalkBeam', '300', '800','none' , true),
    ('89', 'Slip', '1', '3', 'E', '5', 'WalkBeam', '300', '800','none' , true),
    ('90', 'Slip', '1', '3', 'E', '6', 'WalkBeam', '300', '800','none' , true),
    ('91', 'Slip', '1', '3', 'E', '8', 'WalkBeam', '330', '800','none' , true),
    ('92', 'Slip', '1', '3', 'E', '9', 'WalkBeam', '330', '900','none' , true),
    ('93', 'Slip', '1', '3', 'E', '10', 'WalkBeam', '330', '900','none' , true),
    ('94', 'Slip', '1', '3', 'E', '11', 'WalkBeam', '330', '900','none' , true),
    ('95', 'Slip', '1', '3', 'E', '12', 'WalkBeam', '330', '900','none' , true),
    ('96', 'Slip', '1', '3', 'E', '13', 'WalkBeam', '330', '900','none' , true),
    ('97', 'Slip', '1', '3', 'E', '14', 'WalkBeam', '330', '900','none' , true),
    ('98', 'Slip', '1', '3', 'E', '15', 'WalkBeam', '330', '900','none' , true),
    ('99', 'Slip', '1', '3', 'E', '16', 'WalkBeam', '330', '900','none' , true),
    ('100', 'Slip', '1', '3', 'E', '17', 'WalkBeam', '330', '900','none' , true),
    ('101', 'Slip', '1', '3', 'E', '18', 'WalkBeam', '330', '900','none' , true),
    ('102', 'Slip', '1', '3', 'E', '19', 'WalkBeam', '330', '900','none' , true),
    ('103', 'Slip', '1', '3', 'E', '20', 'WalkBeam', '330', '900','none' , true),
    ('104', 'Slip', '1', '4', 'E', '21', 'WalkBeam', '355', '900','none' , true),
    ('105', 'Slip', '1', '4', 'E', '22', 'WalkBeam', '355', '900','none' , true),
    ('106', 'Slip', '1', '4', 'E', '23', 'WalkBeam', '355', '900','none' , true),
    ('107', 'Slip', '1', '4', 'E', '24', 'WalkBeam', '355', '900','none' , true),
    ('108', 'Slip', '1', '2', 'G', '3', 'Beam', '280', '700','none' , true),
    ('109', 'Slip', '1', '2', 'G', '5', 'Beam', '280', '700','none' , true),
    ('110', 'Slip', '1', '2', 'G', '6', 'Beam', '280', '600','none' , true),
    ('111', 'Slip', '1', '2', 'G', '7', 'Beam', '280', '700','none' , true),
    ('112', 'Slip', '1', '3', 'G', '9', 'Beam', '300', '700','none' , true),
    ('113', 'Slip', '1', '3', 'G', '10', 'Beam', '300', '700','none' , true),
    ('114', 'Slip', '1', '3', 'G', '11', 'Beam', '300', '700','none' , true),
    ('115', 'Slip', '1', '3', 'G', '12', 'Beam', '300', '700','none' , true),
    ('116', 'Slip', '1', '3', 'G', '13', 'Beam', '300', '700','none' , true),
    ('117', 'Slip', '1', '3', 'G', '14', 'Beam', '300', '700','none' , true),
    ('118', 'Slip', '1', '3', 'G', '15', 'Beam', '300', '700','none' , true),
    ('119', 'Slip', '1', '3', 'G', '16', 'Beam', '300', '700','none' , true),
    ('120', 'Slip', '1', '3', 'G', '17', 'Beam', '300', '700','none' , true),
    ('121', 'Slip', '1', '3', 'G', '18', 'Beam', '300', '700','none' , true),
    ('122', 'Slip', '1', '3', 'G', '19', 'Beam', '300', '700','none' , true),
    ('123', 'Slip', '1', '3', 'G', '20', 'Beam', '300', '700','none' , true),
    ('124', 'Slip', '1', '3', 'G', '22', 'Beam', '300', '700','none' , true),
    ('125', 'Slip', '1', '3', 'G', '23', 'Beam', '300', '700','none' , true),
    ('126', 'Slip', '1', '3', 'G', '24', 'Beam', '300', '700','none' , true),
    ('127', 'Slip', '1', '3', 'G', '25', 'Beam', '300', '700','none' , true),
    ('128', 'Slip', '1', '3', 'G', '26', 'Beam', '300', '700','none' , true),
    ('129', 'Slip', '1', '3', 'G', '27', 'Beam', '300', '700','none' , true),
    ('130', 'Slip', '1', '3', 'G', '28', 'Beam', '300', '700','none' , true),
    ('131', 'Slip', '1', '3', 'G', '30', 'WalkBeam', '330', '900','none' , true),
    ('132', 'Slip', '1', '4', 'G', '31', 'WalkBeam', '380', '900','none' , true),
    ('133', 'Slip', '1', '4', 'G', '32', 'WalkBeam', '380', '900','none' , true),
    ('134', 'Slip', '1', '5', 'G', '33', 'WalkBeam', '430', '1000','none' , true),
    ('135', 'Slip', '1', '5', 'G', '34', 'WalkBeam', '430', '1000','none' , true),
    ('136', 'Slip', '1', '5', 'G', '35', 'WalkBeam', '430', '1000','none' , true),
    ('137', 'Slip', '1', '5', 'G', '36', 'WalkBeam', '430', '1000','none' , true),
    ('138', 'Slip', '1', '2', 'H', '1', 'Beam', '260', '750','none' , true),
    ('139', 'Slip', '1', '2', 'H', '2', 'Beam', '260', '750','none' , true),
    ('140', 'Slip', '1', '3', 'H', '3', 'Beam', '300', '750','none' , true),
    ('141', 'Slip', '1', '3', 'H', '4', 'Beam', '300', '750','none' , true),
    ('142', 'Slip', '1', '3', 'H', '5', 'Beam', '300', '750','none' , true),
    ('143', 'Slip', '1', '3', 'H', '6', 'Beam', '300', '750','none' , true),
    ('144', 'Slip', '1', '3', 'H', '7', 'Beam', '300', '750','none' , true),
    ('145', 'Slip', '1', '3', 'H', '8', 'Beam', '300', '750','none' , true),
    ('146', 'Slip', '1', '3', 'H', '9', 'Beam', '300', '750','none' , true),
    ('147', 'Slip', '1', '3', 'H', '10', 'Beam', '300', '750','none' , true),
    ('148', 'Slip', '1', '3', 'H', '11', 'Beam', '300', '750','none' , true),
    ('149', 'Slip', '1', '3', 'H', '12', 'Beam', '300', '750','none' , true),
    ('150', 'Slip', '1', '3', 'H', '13', 'Beam', '300', '750','none' , true),
    ('151', 'Slip', '1', '3', 'H', '14', 'Beam', '300', '750','none' , true),
    ('152', 'Slip', '1', '3', 'H', '15', 'Beam', '300', '750','none' , true),
    ('153', 'Slip', '1', '3', 'H', '16', 'Beam', '300', '750','none' , true),
    ('154', 'Slip', '1', '3', 'H', '17', 'Beam', '300', '750','none' , true),
    ('155', 'Slip', '1', '3', 'H', '20', 'Beam', '300', '750','none' , true),
    ('156', 'Slip', '1', '4', 'H', '21', 'Beam', '350', '750','none' , true),
    ('157', 'Slip', '1', '4', 'H', '22', 'Beam', '350', '750','none' , true),
    ('158', 'Slip', '1', '4', 'H', '23', 'Beam', '350', '750','none' , true),
    ('159', 'Slip', '1', '4', 'H', '24', 'Beam', '350', '750','none' , true),
    ('160', 'Slip', '1', '4', 'H', '25', 'Beam', '350', '750','none' , true),
    ('161', 'Slip', '1', '4', 'H', '26', 'Beam', '350', '750','none' , true),
    ('162', 'Slip', '1', '4', 'H', '27', 'Beam', '350', '750','none' , true),
    ('163', 'Slip', '1', '4', 'H', '28', 'Beam', '350', '750','none' , true),
    ('164', 'Slip', '1', '4', 'K', '1', 'Beam', '375', '750','none' , true),
    ('165', 'Slip', '1', '5', 'K', '2', 'Beam', '400', '750','none' , true),
    ('166', 'Slip', '1', '5', 'K', '3', 'Beam', '400', '750','none' , true),
    ('167', 'Slip', '1', '5', 'K', '4', 'Beam', '400', '750','none' , true),
    ('168', 'Slip', '1', '5', 'K', '5', 'Beam', '390', '750','none' , true),
    ('169', 'Slip', '2', '3', 'A', '1', 'Beam', '300', '700','none' , true),
    ('170', 'Slip', '2', '3', 'A', '2', 'Beam', '300', '700','none' , true),
    ('171', 'Slip', '2', '3', 'A', '3', 'Beam', '300', '700','none' , true),
    ('172', 'Slip', '2', '3', 'A', '4', 'Beam', '300', '700','none' , true),
    ('173', 'Slip', '2', '3', 'A', '5', 'Beam', '300', '700','none' , true),
    ('174', 'Slip', '2', '3', 'A', '6', 'Beam', '300', '700','none' , true),
    ('175', 'Slip', '2', '3', 'A', '7', 'Beam', '300', '700','none' , true),
    ('176', 'Slip', '2', '3', 'A', '8', 'Beam', '300', '700','none' , true),
    ('177', 'Slip', '2', '3', 'A', '9', 'Beam', '300', '700','none' , true),
    ('178', 'Slip', '2', '4', 'A', '10', 'WalkBeam', '360', '1000','none' , true),
    ('179', 'Slip', '2', '4', 'A', '11', 'WalkBeam', '380', '1000','none' , true),
    ('180', 'Slip', '2', '4', 'A', '12', 'WalkBeam', '380', '1000','none' , true),
    ('181', 'Slip', '2', '4', 'A', '13', 'WalkBeam', '380', '1000','none' , true),
    ('182', 'Slip', '2', '4', 'A', '14', 'WalkBeam', '380', '1000','none' , true),
    ('183', 'Slip', '2', '4', 'A', '15', 'WalkBeam', '380', '1000','none' , true),
    ('184', 'Slip', '2', '4', 'A', '16', 'WalkBeam', '380', '1000','none' , true),
    ('185', 'Slip', '2', '4', 'A', '17', 'WalkBeam', '380', '1000','none' , true),
    ('186', 'Slip', '2', '4', 'A', '18', 'WalkBeam', '380', '1000','none' , true),
    ('187', 'Slip', '2', '4', 'A', '19', 'WalkBeam', '380', '1000','none' , true),
    ('188', 'Slip', '2', '4', 'A', '20', 'WalkBeam', '380', '1000','none' , true),
    ('189', 'Slip', '2', '4', 'A', '21', 'WalkBeam', '380', '1000','none' , true),
    ('190', 'Slip', '2', '4', 'A', '22', 'WalkBeam', '380', '1000','none' , true),
    ('191', 'Slip', '2', '4', 'A', '23', 'WalkBeam', '380', '1000','none' , true),
    ('192', 'Slip', '2', '4', 'A', '24', 'WalkBeam', '380', '1000','none' , true),
    ('193', 'Slip', '2', '4', 'A', '25', 'WalkBeam', '380', '1000','none' , true),
    ('194', 'Slip', '2', '4', 'A', '26', 'WalkBeam', '380', '1000','none' , true),
    ('195', 'Slip', '2', '4', 'A', '27', 'WalkBeam', '380', '1000','none' , true),
    ('196', 'Slip', '2', '4', 'A', '28', 'WalkBeam', '380', '1200','none' , true),
    ('197', 'Slip', '2', '5', 'A', '29', 'WalkBeam', '430', '1200','none' , true),
    ('198', 'Slip', '2', '5', 'A', '30', 'WalkBeam', '430', '1200','none' , true),
    ('199', 'Slip', '2', '5', 'A', '31', 'WalkBeam', '430', '1200','none' , true),
    ('200', 'Slip', '2', '5', 'A', '32', 'WalkBeam', '430', '1200','none' , true),
    ('201', 'Slip', '2', '5', 'A', '33', 'WalkBeam', '430', '1200','none' , true),
    ('202', 'Slip', '2', '5', 'A', '34', 'WalkBeam', '430', '1200','none' , true),
    ('203', 'Slip', '2', '5', 'A', '35', 'WalkBeam', '430', '1200','none' , true),
    ('204', 'Slip', '2', '5', 'A', '36', 'WalkBeam', '430', '1200','none' , true),
    ('205', 'Slip', '2', '5', 'A', '37', 'WalkBeam', '430', '1200','none' , true),
    ('206', 'Slip', '2', '5', 'A', '38', 'WalkBeam', '430', '1200','none' , true),
    ('207', 'Slip', '2', '5', 'A', '39', 'WalkBeam', '450', '1200','none' , true),
    ('208', 'Slip', '2', '2', 'A', '40', 'Beam', '260', '600','none' , true),
    ('209', 'Slip', '2', '2', 'A', '41', 'Beam', '260', '500','none' , true),
    ('210', 'Slip', '2', '2', 'A', '42', 'Beam', '260', '500','none' , true),
    ('211', 'Slip', '2', '3', 'A', '43', 'Beam', '300', '500','none' , true),
    ('212', 'Slip', '2', '3', 'A', '44', 'Beam', '300', '500','none' , true),
    ('213', 'Slip', '2', '5', 'A', '45', 'WalkBeam', '420', '1000','none' , true),
    ('214', 'Slip', '2', '5', 'A', '46', 'WalkBeam', '430', '1000','none' , true),
    ('215', 'Slip', '2', '5', 'A', '47', 'WalkBeam', '430', '1000','none' , true),
    ('216', 'Slip', '2', '5', 'A', '48', 'WalkBeam', '430', '1000','none' , true),
    ('217', 'Slip', '2', '5', 'A', '49', 'WalkBeam', '430', '1000','none' , true),
    ('218', 'Slip', '2', '5', 'A', '50', 'WalkBeam', '390', '1000','none' , true),
    ('219', 'Slip', '2', '5', 'A', '51', 'WalkBeam', '390', '1000','none' , true),
    ('220', 'Slip', '2', '5', 'A', '52', 'WalkBeam', '390', '1000','none' , true),
    ('221', 'Slip', '2', '5', 'A', '53', 'WalkBeam', '390', '1000','none' , true),
    ('222', 'Slip', '2', '5', 'A', '54', 'WalkBeam', '430', '1000','none' , true),
    ('223', 'Slip', '2', '1', 'B', '1', 'Beam', '229', '650','none' , true),
    ('224', 'Slip', '2', '1', 'B', '2', 'Beam', '229', '650','none' , true),
    ('225', 'Slip', '2', '1', 'B', '3', 'Beam', '229', '650','none' , true),
    ('226', 'Slip', '2', '1', 'B', '4', 'Beam', '229', '650','none' , true),
    ('227', 'Slip', '2', '1', 'B', '5', 'Beam', '229', '650','none' , true),
    ('228', 'Slip', '2', '1', 'B', '6', 'Beam', '229', '650','none' , true),
    ('229', 'Slip', '2', '1', 'B', '7', 'Beam', '229', '650','none' , true),
    ('230', 'Slip', '2', '1', 'B', '8', 'Beam', '229', '650','none' , true),
    ('231', 'Slip', '2', '1', 'B', '9', 'Beam', '229', '650','none' , true),
    ('232', 'Slip', '2', '1', 'B', '10', 'Beam', '229', '650','none' , true),
    ('233', 'Slip', '2', '1', 'B', '11', 'Beam', '229', '650','none' , true),
    ('234', 'Slip', '2', '1', 'B', '12', 'Beam', '229', '650','none' , true),
    ('235', 'Slip', '2', '1', 'B', '13', 'Beam', '229', '650','none' , true),
    ('236', 'Slip', '2', '1', 'B', '14', 'Beam', '229', '650','none' , true),
    ('237', 'Slip', '2', '1', 'B', '15', 'Beam', '229', '650','none' , true),
    ('238', 'Slip', '2', '1', 'B', '16', 'Beam', '229', '650','none' , true),
    ('239', 'Slip', '2', '1', 'B', '17', 'Beam', '229', '650','none' , true),
    ('240', 'Slip', '2', '1', 'B', '18', 'Beam', '229', '650','none' , true),
    ('241', 'Slip', '2', '1', 'B', '19', 'Beam', '229', '650','none' , true),
    ('242', 'Slip', '2', '1', 'B', '20', 'Beam', '229', '650','none' , true),
    ('243', 'Slip', '2', '1', 'B', '21', 'Beam', '229', '650','none' , true),
    ('244', 'Slip', '2', '1', 'B', '22', 'Beam', '229', '650','none' , true),
    ('245', 'Slip', '2', '1', 'B', '23', 'Beam', '229', '650','none' , true),
    ('246', 'Slip', '2', '1', 'B', '24', 'Beam', '229', '650','none' , true),
    ('247', 'Slip', '2', '1', 'B', '25', 'Beam', '229', '650','none' , true),
    ('248', 'Slip', '2', '1', 'B', '26', 'Beam', '229', '650','none' , true),
    ('249', 'Slip', '2', '1', 'B', '27', 'Beam', '229', '650','none' , true),
    ('250', 'Slip', '2', '1', 'B', '28', 'Beam', '229', '650','none' , true),
    ('251', 'Slip', '2', '1', 'B', '29', 'Beam', '229', '650','none' , true),
    ('252', 'Slip', '2', '1', 'B', '30', 'Beam', '229', '650','none' , true),
    ('253', 'Slip', '2', '1', 'B', '31', 'Beam', '229', '650','none' , true),
    ('254', 'Slip', '2', '1', 'B', '32', 'Beam', '229', '650','none' , true),
    ('255', 'Slip', '2', '1', 'B', '33', 'Beam', '229', '650','none' , true),
    ('256', 'Slip', '2', '1', 'B', '34', 'Beam', '229', '650','none' , true),
    ('257', 'Slip', '2', '1', 'B', '35', 'Beam', '229', '650','none' , true),
    ('258', 'Slip', '2', '1', 'B', '36', 'Beam', '229', '650','none' , true),
    ('259', 'Slip', '2', '1', 'B', '37', 'Beam', '229', '650','none' , true),
    ('260', 'Slip', '2', '1', 'B', '38', 'Beam', '229', '650','none' , true),
    ('261', 'Slip', '2', '1', 'B', '39', 'Beam', '229', '650','none' , true),
    ('262', 'Slip', '2', '1', 'B', '40', 'Beam', '229', '650','none' , true),
    ('263', 'Slip', '2', '1', 'B', '41', 'Beam', '229', '650','none' , true),
    ('264', 'Slip', '2', '1', 'B', '42', 'Beam', '229', '650','none' , true),
    ('265', 'Slip', '2', '1', 'B', '43', 'Beam', '229', '650','none' , true),
    ('266', 'Slip', '2', '1', 'B', '44', 'Beam', '229', '650','none' , true),
    ('267', 'Slip', '2', '1', 'B', '45', 'Beam', '229', '650','none' , true),
    ('268', 'Slip', '2', '1', 'B', '46', 'Beam', '229', '650','none' , true),
    ('269', 'Slip', '2', '1', 'B', '47', 'Beam', '229', '650','none' , true),
    ('270', 'Slip', '2', '1', 'B', '48', 'Beam', '229', '650','none' , true),
    ('271', 'Slip', '2', '1', 'B', '49', 'Beam', '229', '650','none' , true),
    ('272', 'Slip', '2', '1', 'B', '50', 'Beam', '229', '650','none' , true),
    ('273', 'Slip', '2', '1', 'B', '51', 'Beam', '229', '650','none' , true),
    ('274', 'Slip', '2', '1', 'B', '52', 'Beam', '229', '650','none' , true),
    ('275', 'Slip', '2', '1', 'B', '53', 'Beam', '229', '650','none' , true),
    ('276', 'Slip', '2', '1', 'B', '54', 'Beam', '229', '650','none' , true),
    ('277', 'Slip', '2', '1', 'B', '55', 'Beam', '229', '650','none' , true),
    ('278', 'Slip', '2', '1', 'B', '56', 'Beam', '229', '650','none' , true),
    ('279', 'Slip', '2', '2', 'B', '57', 'Beam', '275', '650','none' , true),
    ('280', 'Slip', '2', '2', 'B', '58', 'Beam', '275', '650','none' , true),
    ('281', 'Slip', '2', '2', 'B', '59', 'Beam', '275', '650','none' , true),
    ('282', 'Slip', '2', '2', 'B', '60', 'Beam', '275', '650','none' , true),
    ('283', 'Slip', '2', '2', 'B', '61', 'Beam', '275', '650','none' , true),
    ('284', 'Slip', '2', '2', 'B', '62', 'Beam', '275', '650','none' , true),
    ('285', 'Slip', '2', '2', 'B', '63', 'Beam', '275', '650','none' , true),
    ('286', 'Slip', '2', '2', 'B', '64', 'Beam', '275', '650','none' , true),
    ('287', 'Slip', '2', '2', 'B', '65', 'Beam', '275', '650','none' , true),
    ('288', 'Slip', '2', '2', 'B', '66', 'Beam', '275', '650','none' , true),
    ('289', 'Slip', '2', '2', 'B', '67', 'Beam', '275', '650','none' , true),
    ('290', 'Slip', '2', '2', 'B', '68', 'Beam', '275', '650','none' , true),
    ('291', 'Slip', '2', '2', 'B', '69', 'Beam', '275', '650','none' , true),
    ('292', 'Slip', '2', '2', 'B', '70', 'Beam', '275', '650','none' , true),
    ('293', 'Slip', '2', '2', 'B', '71', 'Beam', '275', '650','none' , true),
    ('294', 'Slip', '2', '2', 'B', '72', 'Beam', '275', '650','none' , true),
    ('295', 'Slip', '2', '2', 'B', '73', 'Beam', '275', '650','none' , true),
    ('296', 'Slip', '2', '2', 'B', '74', 'Beam', '275', '650','none' , true),
    ('297', 'Slip', '2', '2', 'B', '75', 'Beam', '275', '650','none' , true),
    ('298', 'Slip', '2', '2', 'B', '76', 'Beam', '275', '650','none' , true),
    ('299', 'Slip', '2', '2', 'B', '77', 'Beam', '275', '650','none' , true),
    ('300', 'Slip', '2', '2', 'B', '78', 'Beam', '275', '650','none' , true),
    ('301', 'Slip', '2', '2', 'B', '79', 'Beam', '275', '650','none' , true),
    ('302', 'Slip', '2', '2', 'B', '80', 'Beam', '275', '650','none' , true),
    ('303', 'Slip', '2', '2', 'B', '81', 'Beam', '275', '650','none' , true),
    ('304', 'Slip', '2', '2', 'B', '82', 'Beam', '275', '650','none' , true),
    ('305', 'Slip', '2', '2', 'B', '83', 'Beam', '275', '650','none' , true),
    ('306', 'Slip', '2', '2', 'B', '84', 'Beam', '275', '650','none' , true),
    ('307', 'Slip', '2', '3', 'B', '85', 'Beam', '300', '650','none' , true),
    ('308', 'Slip', '2', '3', 'B', '86', 'Beam', '300', '700','none' , true),
    ('309', 'Slip', '2', '3', 'B', '87', 'Beam', '300', '700','none' , true),
    ('310', 'Slip', '2', '3', 'B', '88', 'Beam', '300', '700','none' , true),
    ('311', 'Slip', '2', '3', 'B', '89', 'Beam', '300', '700','none' , true),
    ('312', 'Slip', '2', '3', 'B', '90', 'Beam', '300', '700','none' , true),
    ('313', 'Slip', '2', '3', 'B', '91', 'Beam', '300', '700','none' , true),
    ('314', 'Slip', '2', '3', 'B', '92', 'Beam', '300', '700','none' , true),
    ('315', 'Slip', '2', '3', 'B', '93', 'Beam', '300', '700','none' , true),
    ('316', 'Slip', '2', '3', 'B', '94', 'Beam', '300', '700','none' , true),
    ('317', 'Slip', '2', '1', 'C', '1', 'Beam', '229', '600','none' , true),
    ('318', 'Slip', '2', '1', 'C', '2', 'Beam', '229', '600','none' , true),
    ('319', 'Slip', '2', '1', 'C', '3', 'Beam', '229', '600','none' , true),
    ('320', 'Slip', '2', '1', 'C', '4', 'Beam', '229', '600','none' , true),
    ('321', 'Slip', '2', '2', 'C', '5', 'Beam', '275', '600','none' , true),
    ('322', 'Slip', '2', '2', 'C', '6', 'Beam', '275', '600','none' , true),
    ('323', 'Slip', '2', '2', 'C', '7', 'Beam', '275', '600','none' , true),
    ('324', 'Slip', '2', '2', 'C', '8', 'Beam', '275', '600','none' , true),
    ('325', 'Slip', '2', '2', 'C', '9', 'Beam', '275', '600','none' , true),
    ('326', 'Slip', '2', '2', 'C', '10', 'Beam', '275', '600','none' , true),
    ('327', 'Slip', '2', '2', 'C', '11', 'Beam', '275', '600','none' , true),
    ('328', 'Slip', '2', '2', 'C', '12', 'Beam', '275', '600','none' , true),
    ('329', 'Slip', '2', '2', 'C', '13', 'Beam', '275', '600','none' , true),
    ('330', 'Slip', '2', '2', 'C', '14', 'Beam', '275', '600','none' , true),
    ('331', 'Slip', '2', '2', 'C', '15', 'Beam', '275', '600','none' , true),
    ('332', 'Slip', '2', '2', 'C', '16', 'Beam', '275', '600','none' , true),
    ('333', 'Slip', '2', '2', 'C', '17', 'Beam', '275', '600','none' , true),
    ('334', 'Slip', '2', '2', 'C', '18', 'Beam', '275', '600','none' , true),
    ('335', 'Slip', '2', '2', 'C', '19', 'Beam', '275', '600','none' , true),
    ('336', 'Slip', '2', '2', 'C', '20', 'Beam', '275', '600','none' , true),
    ('337', 'Slip', '2', '2', 'C', '21', 'Beam', '275', '600','none' , true),
    ('338', 'Slip', '2', '2', 'C', '22', 'Beam', '275', '600','none' , true),
    ('339', 'Slip', '2', '2', 'C', '23', 'Beam', '275', '600','none' , true),
    ('340', 'Slip', '2', '2', 'C', '24', 'Beam', '275', '600','none' , true),
    ('341', 'Slip', '2', '2', 'C', '25', 'Beam', '275', '600','none' , true),
    ('342', 'Slip', '2', '2', 'C', '26', 'Beam', '275', '600','none' , true),
    ('343', 'Slip', '2', '2', 'C', '27', 'Beam', '275', '600','none' , true),
    ('344', 'Slip', '2', '2', 'C', '28', 'Beam', '275', '600','none' , true),
    ('345', 'Slip', '2', '2', 'C', '29', 'Beam', '275', '600','none' , true),
    ('346', 'Storage', '8', '2', 'C', '30', 'Trailer', '275', '600','none' , true),
    ('347', 'Storage', '2', '2', 'C', '31', 'Trailer', '275', '600','none' , true),
    ('348', 'Storage', '2', '2', 'C', '32', 'Buck', '275', '600','none' , true),
    ('349', 'Storage', '2', '2', 'C', '33', 'Buck', '275', '600','none' , true),
    ('350', 'Storage', '2', '2', 'C', '34', 'Buck', '275', '600','none' , true),
    ('351', 'Slip', '2', '2', 'C', '35', 'Beam', '275', '600','none' , true),
    ('352', 'Slip', '2', '2', 'C', '36', 'Beam', '275', '600','none' , true),
    ('353', 'Slip', '2', '2', 'C', '37', 'Beam', '275', '600','none' , true),
    ('354', 'Slip', '2', '2', 'C', '38', 'Beam', '275', '600','none' , true),
    ('355', 'Slip', '2', '2', 'C', '39', 'Beam', '275', '600','none' , true),
    ('356', 'Slip', '2', '2', 'C', '40', 'Beam', '275', '600','none' , true),
    ('357', 'Slip', '2', '2', 'C', '41', 'Beam', '275', '600','none' , true),
    ('358', 'Slip', '2', '2', 'C', '42', 'Beam', '275', '600','none' , true),
    ('359', 'Slip', '2', '2', 'C', '43', 'Beam', '260', '600','none' , true),
    ('360', 'Slip', '2', '2', 'C', '44', 'Beam', '275', '600','none' , true),
    ('361', 'Slip', '2', '2', 'C', '45', 'Beam', '275', '600','none' , true),
    ('362', 'Slip', '2', '2', 'C', '46', 'Beam', '275', '600','none' , true),
    ('363', 'Slip', '2', '2', 'C', '47', 'Beam', '275', '600','none' , true),
    ('364', 'Slip', '2', '2', 'C', '48', 'Beam', '275', '600','none' , true),
    ('365', 'Slip', '2', '2', 'C', '49', 'Beam', '275', '600','none' , true),
    ('366', 'Slip', '2', '2', 'C', '50', 'Beam', '275', '600','none' , true),
    ('367', 'Slip', '2', '2', 'C', '51', 'Beam', '275', '600','none' , true),
    ('368', 'Slip', '2', '2', 'C', '52', 'Beam', '275', '600','none' , true),
    ('369', 'Slip', '2', '2', 'C', '53', 'Beam', '275', '600','none' , true),
    ('370', 'Slip', '2', '2', 'C', '54', 'Beam', '275', '700','none' , true),
    ('371', 'Slip', '2', '2', 'C', '55', 'Beam', '275', '700','none' , true),
    ('372', 'Slip', '2', '2', 'C', '56', 'Beam', '275', '700','none' , true),
    ('373', 'Slip', '2', '2', 'C', '57', 'Beam', '275', '700','none' , true),
    ('374', 'Slip', '2', '2', 'C', '58', 'Beam', '275', '700','none' , true),
    ('375', 'Slip', '2', '2', 'C', '59', 'Beam', '275', '700','none' , true),
    ('376', 'Slip', '2', '2', 'C', '60', 'Beam', '275', '700','none' , true),
    ('377', 'Slip', '2', '2', 'C', '61', 'Beam', '275', '700','none' , true),
    ('378', 'Slip', '2', '2', 'C', '62', 'Beam', '275', '700','none' , true),
    ('379', 'Slip', '2', '2', 'C', '63', 'Beam', '275', '700','none' , true),
    ('380', 'Slip', '2', '2', 'C', '64', 'Beam', '275', '700','none' , true),
    ('381', 'Slip', '2', '2', 'C', '65', 'Beam', '275', '700','none' , true),
    ('382', 'Slip', '2', '2', 'C', '66', 'Beam', '275', '700','none' , true),
    ('383', 'Slip', '2', '2', 'C', '67', 'Beam', '275', '700','none' , true),
    ('384', 'Slip', '2', '2', 'C', '68', 'Beam', '275', '700','none' , true),
    ('385', 'Slip', '2', '2', 'C', '69', 'Beam', '275', '700','none' , true),
    ('386', 'Slip', '2', '2', 'C', '70', 'Beam', '275', '700','none' , true),
    ('387', 'Slip', '2', '2', 'C', '71', 'Beam', '275', '700','none' , true),
    ('388', 'Slip', '2', '2', 'C', '72', 'Beam', '275', '700','none' , true),
    ('389', 'Slip', '2', '2', 'C', '73', 'Beam', '275', '700','none' , true),
    ('390', 'Slip', '2', '2', 'C', '74', 'Beam', '275', '700','none' , true),
    ('391', 'Slip', '2', '3', 'C', '75', 'Beam', '290', '700','none' , true),
    ('392', 'Slip', '2', '3', 'C', '76', 'Beam', '290', '700','none' , true),
    ('393', 'Slip', '2', '3', 'C', '77', 'Beam', '300', '700','none' , true),
    ('394', 'Slip', '2', '3', 'C', '78', 'Beam', '300', '700','none' , true),
    ('395', 'Slip', '2', '3', 'C', '79', 'Beam', '300', '700','none' , true),
    ('396', 'Slip', '2', '3', 'C', '80', 'Beam', '300', '700','none' , true),
    ('397', 'Slip', '2', '3', 'C', '81', 'Beam', '300', '700','none' , true),
    ('398', 'Slip', '2', '3', 'C', '82', 'Beam', '300', '700','none' , true),
    ('399', 'Slip', '2', '3', 'C', '83', 'Beam', '300', '700','none' , true),
    ('400', 'Slip', '2', '3', 'C', '84', 'Beam', '300', '700','none' , true),
    ('401', 'Slip', '2', '3', 'C', '85', 'Beam', '300', '700','none' , true),
    ('402', 'Slip', '2', '3', 'C', '86', 'Beam', '300', '700','none' , true),
    ('403', 'Slip', '2', '2', 'D', '1', 'Beam', '250', '600','none' , true),
    ('404', 'Slip', '2', '2', 'D', '2', 'Beam', '250', '600','none' , true),
    ('405', 'Slip', '2', '2', 'D', '3', 'Beam', '250', '600','none' , true),
    ('406', 'Slip', '2', '2', 'D', '4', 'Beam', '250', '600','none' , true),
    ('407', 'Slip', '2', '2', 'D', '5', 'Beam', '250', '600','none' , true),
    ('408', 'Slip', '2', '2', 'D', '6', 'Beam', '250', '600','none' , true),
    ('409', 'Slip', '2', '2', 'D', '7', 'Beam', '250', '600','none' , true),
    ('410', 'Slip', '2', '2', 'D', '8', 'Beam', '250', '600','none' , true),
    ('411', 'Slip', '2', '2', 'D', '9', 'Beam', '250', '600','none' , true),
    ('412', 'Slip', '2', '2', 'D', '10', 'Beam', '250', '600','none' , true),
    ('413', 'Slip', '2', '2', 'D', '11', 'Beam', '250', '600','none' , true),
    ('414', 'Slip', '2', '2', 'D', '12', 'Beam', '250', '600','none' , true),
    ('415', 'Slip', '2', '2', 'D', '13', 'Beam', '250', '600','none' , true),
    ('416', 'Slip', '2', '2', 'D', '14', 'Beam', '250', '600','none' , true),
    ('417', 'Slip', '2', '2', 'D', '15', 'Beam', '250', '600','none' , true),
    ('418', 'Slip', '2', '2', 'D', '16', 'Beam', '250', '600','none' , true),
    ('419', 'Slip', '2', '2', 'D', '17', 'Beam', '250', '600','none' , true),
    ('420', 'Slip', '2', '2', 'D', '18', 'Beam', '250', '600','none' , true),
    ('421', 'Slip', '2', '2', 'D', '19', 'Beam', '250', '600','none' , true),
    ('422', 'Slip', '2', '2', 'D', '20', 'Beam', '250', '600','none' , true),
    ('423', 'Slip', '2', '2', 'D', '21', 'Beam', '250', '600','none' , true),
    ('424', 'Slip', '2', '2', 'D', '22', 'Beam', '250', '600','none' , true),
    ('425', 'Slip', '2', '2', 'D', '23', 'Beam', '250', '600','none' , true),
    ('426', 'Slip', '2', '2', 'D', '24', 'Beam', '250', '600','none' , true),
    ('427', 'Slip', '2', '2', 'D', '25', 'Beam', '250', '600','none' , true),
    ('428', 'Slip', '2', '2', 'D', '26', 'Beam', '250', '600','none' , true),
    ('429', 'Slip', '2', '2', 'D', '27', 'Beam', '250', '600','none' , true),
    ('430', 'Slip', '2', '2', 'D', '28', 'Beam', '250', '600','none' , true),
    ('431', 'Slip', '2', '2', 'D', '29', 'Beam', '250', '600','none' , true),
    ('432', 'Slip', '2', '2', 'D', '30', 'Beam', '250', '600','none' , true),
    ('433', 'Slip', '2', '2', 'D', '31', 'Beam', '250', '600','none' , true),
    ('434', 'Slip', '2', '2', 'D', '32', 'Beam', '250', '600','none' , true),
    ('435', 'Slip', '2', '2', 'D', '33', 'Beam', '250', '600','none' , true),
    ('436', 'Slip', '2', '2', 'D', '34', 'Beam', '250', '600','none' , true),
    ('437', 'Slip', '2', '2', 'D', '35', 'Beam', '250', '600','none' , true),
    ('438', 'Slip', '2', '2', 'D', '36', 'Beam', '250', '600','none' , true),
    ('439', 'Slip', '2', '2', 'D', '37', 'Beam', '250', '600','none' , true),
    ('440', 'Slip', '2', '2', 'D', '38', 'Beam', '250', '600','none' , true),
    ('441', 'Slip', '2', '2', 'D', '39', 'Beam', '250', '600','none' , true),
    ('442', 'Slip', '2', '2', 'D', '40', 'Beam', '250', '600','none' , true),
    ('443', 'Slip', '2', '2', 'D', '41', 'Beam', '250', '600','none' , true),
    ('444', 'Slip', '2', '2', 'D', '42', 'Beam', '250', '600','none' , true),
    ('445', 'Slip', '2', '2', 'D', '43', 'Beam', '250', '600','none' , true),
    ('446', 'Slip', '2', '2', 'D', '44', 'Beam', '250', '600','none' , true),
    ('447', 'Slip', '2', '2', 'D', '45', 'Beam', '250', '600','none' , true),
    ('448', 'Slip', '2', '2', 'D', '46', 'Beam', '250', '600','none' , true),
    ('449', 'Slip', '2', '2', 'D', '47', 'Beam', '250', '600','none' , true),
    ('450', 'Slip', '2', '2', 'D', '48', 'Beam', '250', '600','none' , true),
    ('451', 'Slip', '2', '2', 'D', '49', 'Beam', '250', '600','none' , true),
    ('452', 'Slip', '2', '2', 'D', '50', 'Beam', '250', '600','none' , true),
    ('453', 'Slip', '2', '2', 'D', '51', 'Beam', '250', '600','none' , true),
    ('454', 'Slip', '2', '2', 'D', '52', 'Beam', '250', '600','none' , true),
    ('455', 'Slip', '2', '2', 'D', '53', 'Beam', '275', '600','none' , true),
    ('456', 'Slip', '2', '2', 'D', '54', 'Beam', '275', '600','none' , true),
    ('457', 'Slip', '2', '2', 'D', '55', 'Beam', '275', '600','none' , true),
    ('458', 'Slip', '2', '2', 'D', '56', 'Beam', '275', '600','none' , true),
    ('459', 'Slip', '2', '2', 'D', '57', 'Beam', '275', '600','none' , true),
    ('460', 'Slip', '2', '2', 'D', '58', 'Beam', '275', '600','none' , true),
    ('461', 'Slip', '2', '2', 'D', '59', 'Beam', '275', '600','none' , true),
    ('462', 'Slip', '2', '2', 'D', '60', 'Beam', '275', '600','none' , true),
    ('463', 'Slip', '2', '2', 'D', '61', 'Beam', '275', '600','none' , true),
    ('464', 'Slip', '2', '2', 'D', '62', 'Beam', '275', '600','none' , true),
    ('465', 'Slip', '2', '2', 'D', '63', 'Beam', '275', '600','none' , true),
    ('466', 'Slip', '2', '2', 'D', '64', 'Beam', '275', '600','none' , true),
    ('467', 'Slip', '2', '3', 'D', '65', 'Beam', '300', '600','none' , true),
    ('468', 'Slip', '2', '3', 'D', '66', 'Beam', '300', '600','none' , true),
    ('469', 'Slip', '2', '3', 'D', '67', 'Beam', '300', '600','none' , true),
    ('470', 'Slip', '2', '3', 'D', '68', 'Beam', '300', '600','none' , true),
    ('471', 'Slip', '2', '3', 'D', '69', 'Beam', '300', '600','none' , true),
    ('472', 'Slip', '2', '3', 'D', '70', 'Beam', '300', '600','none' , true),
    ('473', 'Slip', '2', '3', 'D', '71', 'Beam', '300', '600','none' , true),
    ('474', 'Slip', '2', '3', 'D', '72', 'Beam', '300', '600','none' , true),
    ('475', 'Slip', '2', '3', 'D', '73', 'Beam', '300', '600','none' , true),
    ('476', 'Slip', '2', '3', 'D', '74', 'Beam', '300', '600','none' , true),
    ('477', 'Slip', '2', '3', 'D', '75', 'Beam', '300', '600','none' , true),
    ('478', 'Slip', '2', '3', 'D', '76', 'Beam', '300', '600','none' , true),
    ('479', 'Slip', '2', '3', 'D', '77', 'Beam', '300', '600','none' , true),
    ('480', 'Slip', '2', '3', 'D', '78', 'Beam', '300', '600','none' , true),
    ('481', 'Slip', '2', '3', 'D', '79', 'Beam', '300', '600','none' , true),
    ('482', 'Slip', '2', '3', 'D', '80', 'Beam', '300', '600','none' , true),
    ('483', 'Slip', '2', '3', 'D', '81', 'Beam', '300', '600','none' , true),
    ('484', 'Slip', '2', '3', 'D', '82', 'Beam', '300', '600','none' , true),
    ('485', 'Slip', '2', '4', 'D', '83', 'Beam', '340', '600','none' , true),
    ('486', 'Slip', '2', '4', 'D', '84', 'Beam', '340', '600','none' , true),
    ('487', 'Slip', '2', '4', 'D', '85', 'Beam', '340', '600','none' , true),
    ('488', 'Slip', '2', '4', 'D', '86', 'Beam', '340', '600','none' , true),
    ('489', 'Slip', '2', '4', 'D', '87', 'Beam', '340', '600','none' , true),
    ('490', 'Slip', '2', '4', 'D', '88', 'Beam', '340', '600','none' , true),
    ('491', 'Slip', '2', '2', 'E', '1', 'Beam', '265', '600','none' , true),
    ('492', 'Slip', '2', '2', 'E', '2', 'Beam', '275', '600','none' , true),
    ('493', 'Slip', '2', '2', 'E', '3', 'Beam', '260', '600','none' , true),
    ('494', 'Slip', '2', '2', 'E', '4', 'Beam', '275', '600','none' , true),
    ('495', 'Slip', '2', '2', 'E', '5', 'Beam', '265', '600','none' , true),
    ('496', 'Slip', '2', '2', 'E', '6', 'Beam', '275', '600','none' , true),
    ('497', 'Slip', '2', '2', 'E', '7', 'Beam', '260', '600','none' , true),
    ('498', 'Slip', '2', '2', 'E', '8', 'Beam', '275', '600','none' , true),
    ('499', 'Slip', '2', '2', 'E', '9', 'Beam', '260', '600','none' , true),
    ('500', 'Slip', '2', '2', 'E', '10', 'Beam', '275', '600','none' , true),
    ('501', 'Slip', '2', '2', 'E', '11', 'Beam', '260', '600','none' , true),
    ('502', 'Slip', '2', '2', 'E', '12', 'Beam', '275', '600','none' , true),
    ('503', 'Slip', '2', '2', 'E', '13', 'Beam', '260', '600','none' , true),
    ('504', 'Slip', '2', '2', 'E', '14', 'Beam', '275', '600','none' , true),
    ('505', 'Slip', '2', '2', 'E', '15', 'Beam', '260', '600','none' , true),
    ('506', 'Slip', '2', '2', 'E', '16', 'Beam', '275', '600','none' , true),
    ('507', 'Slip', '2', '2', 'E', '17', 'Beam', '260', '600','none' , true),
    ('508', 'Slip', '2', '2', 'E', '18', 'Beam', '275', '600','none' , true),
    ('509', 'Slip', '2', '2', 'E', '19', 'Beam', '260', '600','none' , true),
    ('510', 'Slip', '2', '2', 'E', '20', 'Beam', '275', '600','none' , true),
    ('511', 'Slip', '2', '3', 'E', '21', 'Beam', '310', '550','none' , true),
    ('512', 'Slip', '2', '3', 'E', '22', 'Beam', '320', '600','none' , true),
    ('513', 'Slip', '2', '3', 'E', '23', 'Beam', '310', '600','none' , true),
    ('514', 'Slip', '2', '3', 'E', '24', 'Beam', '320', '600','none' , true),
    ('515', 'Slip', '2', '3', 'E', '25', 'Beam', '310', '600','none' , true),
    ('516', 'Slip', '2', '3', 'E', '26', 'Beam', '320', '600','none' , true),
    ('517', 'Slip', '2', '3', 'E', '27', 'Beam', '310', '600','none' , true),
    ('518', 'Slip', '2', '3', 'E', '28', 'Beam', '320', '600','none' , true),
    ('519', 'Slip', '2', '3', 'E', '30', 'Beam', '320', '600','none' , true),
    ('520', 'Slip', '2', '3', 'E', '32', 'Beam', '320', '600','none' , true),
    ('521', 'Slip', '2', '3', 'E', '34', 'Beam', '320', '600','none' , true),
    ('522', 'Slip', '2', '3', 'E', '36', 'Beam', '320', '600','none' , true),
    ('523', 'Slip', '2', '3', 'E', '38', 'Beam', '320', '600','none' , true),
    ('524', 'Slip', '2', '3', 'E', '40', 'Beam', '320', '600','none' , true),
    ('525', 'Slip', '2', '3', 'E', '42', 'Beam', '320', '600','none' , true),
    ('526', 'Slip', '2', '3', 'E', '44', 'Beam', '320', '600','none' , true),
    ('527', 'Slip', '2', '3', 'E', '46', 'Beam', '320', '600','none' , true),
    ('528', 'Slip', '2', '3', 'E', '48', 'Beam', '320', '600','none' , true),
    ('529', 'Slip', '2', '3', 'E', '50', 'Beam', '320', '600','none' , true),
    ('530', 'Slip', '2', '3', 'E', '52', 'Beam', '320', '600','none' , true),
    ('531', 'Slip', '2', '3', 'E', '54', 'Beam', '320', '600','none' , true),
    ('532', 'Slip', '2', '3', 'E', '56', 'Beam', '320', '600','none' , true),
    ('533', 'Slip', '2', '3', 'E', '58', 'Beam', '320', '600','none' , true),
    ('534', 'Slip', '2', '3', 'E', '60', 'Beam', '320', '600','none' , true),
    ('535', 'Slip', '2', '3', 'E', '62', 'Beam', '320', '600','none' , true),
    ('536', 'Slip', '2', '3', 'E', '64', 'Beam', '320', '600','none' , true),
    ('537', 'Slip', '2', '3', 'E', '66', 'Beam', '320', '600','none' , true),
    ('538', 'Slip', '2', '3', 'E', '68', 'Beam', '320', '600','none' , true),
    ('539', 'Slip', '2', '3', 'E', '70', 'Beam', '320', '600','none' , true),
    ('540', 'Slip', '2', '3', 'E', '72', 'Beam', '320', '600','none' , true),
    ('541', 'Slip', '2', '6', 'E', '74', 'WalkBeam', '470', '900','none' , true),
    ('542', 'Slip', '2', '4', 'E', '76', 'WalkBeam', '380', '900','none' , true),
    ('543', 'Slip', '2', '4', 'E', '78', 'WalkBeam', '380', '900','none' , true),
    ('544', 'Slip', '2', '4', 'E', '80', 'WalkBeam', '380', '900','none' , true),
    ('545', 'Slip', '2', '4', 'E', '82', 'WalkBeam', '380', '900','none' , true),
    ('546', 'Slip', '2', '4', 'E', '84', 'WalkBeam', '380', '900','none' , true),
    ('547', 'Slip', '2', '4', 'E', '86', 'WalkBeam', '380', '900','none' , true),
    ('548', 'Slip', '2', '4', 'E', '88', 'WalkBeam', '380', '900','none' , true),
    ('549', 'Slip', '2', '4', 'E', '90', 'WalkBeam', '380', '900','none' , true),
    ('550', 'Slip', '2', '4', 'E', '92', 'WalkBeam', '380', '900','none' , true),
    ('551', 'Slip', '2', '4', 'E', '94', 'WalkBeam', '380', '900','none' , true),
    ('552', 'Slip', '2', '4', 'E', '96', 'WalkBeam', '380', '900','none' , true),
    ('553', 'Slip', '2', '4', 'E', '98', 'WalkBeam', '380', '900','none' , true),
    ('554', 'Slip', '2', '4', 'E', '100', 'WalkBeam', '380', '900','none' , true),
    ('555', 'Slip', '2', '4', 'E', '102', 'WalkBeam', '380', '900','none' , true),
    ('556', 'Slip', '2', '4', 'E', '104', 'WalkBeam', '380', '900','none' , true),
    ('557', 'Slip', '2', '4', 'E', '106', 'WalkBeam', '380', '900','none' , true),
    ('558', 'Slip', '2', '4', 'E', '108', 'Beam', '380', '600','none' , true),
    ('559', 'Slip', '2', '2', 'E', '110', 'Beam', '260', '600','none' , true),
    ('560', 'Slip', '2', '2', 'E', '112', 'Beam', '260', '600', 'Suomenoja E115 käyttäjälle varattu', true),
    ('561', 'Slip', '2', '2', 'E', '114', 'Beam', '260', '600','none' , true),
    ('562', 'Slip', '2', '2', 'E', '116', 'Beam', '260', '600','none' , true),
    ('563', 'Slip', '2', '2', 'E', '118', 'Beam', '260', '600','none' , true),
    ('564', 'Slip', '2', '2', 'E', '120', 'Beam', '260', '600','none' , true),
    ('565', 'Slip', '2', '5', 'E', '122', 'WalkBeam', '390', '900','none' , true),
    ('566', 'Slip', '2', '5', 'E', '124', 'WalkBeam', '390', '900','none' , true),
    ('567', 'Slip', '2', '5', 'E', '126', 'WalkBeam', '390', '900','none' , true),
    ('568', 'Slip', '2', '5', 'E', '128', 'WalkBeam', '390', '900','none' , true),
    ('569', 'Slip', '2', '5', 'E', '130', 'WalkBeam', '390', '900','none' , true),
    ('570', 'Slip', '2', '5', 'E', '132', 'WalkBeam', '390', '900','none' , true),
    ('571', 'Slip', '2', '5', 'E', '134', 'WalkBeam', '390', '900','none' , true),
    ('572', 'Slip', '2', '5', 'E', '136', 'WalkBeam', '390', '900','none' , true),
    ('573', 'Slip', '2', '5', 'E', '138', 'WalkBeam', '390', '900','none' , true),
    ('574', 'Slip', '2', '5', 'E', '140', 'WalkBeam', '390', '900','none' , true),
    ('575', 'Slip', '2', '5', 'E', '142', 'WalkBeam', '390', '900','none' , true),
    ('576', 'Slip', '2', '5', 'E', '144', 'WalkBeam', '420', '900','none' , true),
    ('577', 'Slip', '3', '2', 'none', '001', 'Beam', '270', '500','none' , true),
    ('578', 'Slip', '3', '2', 'none', '002', 'Beam', '280', '500','none' , true),
    ('579', 'Slip', '3', '2', 'none', '003', 'Beam', '280', '500','none' , true),
    ('580', 'Slip', '3', '2', 'none', '004', 'Beam', '280', '500','none' , true),
    ('581', 'Slip', '3', '2', 'none', '005', 'Beam', '280', '500','none' , true),
    ('582', 'Slip', '3', '2', 'none', '006', 'Beam', '280', '500','none' , true),
    ('583', 'Slip', '3', '2', 'none', '007', 'Beam', '280', '500','none' , true),
    ('584', 'Slip', '3', '2', 'none', '008', 'Beam', '280', '500','none' , true),
    ('585', 'Slip', '3', '2', 'none', '009', 'Beam', '280', '500','none' , true),
    ('586', 'Slip', '3', '2', 'none', '010', 'Beam', '280', '500','none' , true),
    ('587', 'Slip', '3', '2', 'none', '011', 'Beam', '280', '500','none' , true),
    ('588', 'Slip', '3', '2', 'none', '012', 'Beam', '280', '500','none' , true),
    ('589', 'Slip', '3', '2', 'none', '013', 'Beam', '280', '500','none' , true),
    ('590', 'Slip', '3', '2', 'none', '014', 'Beam', '280', '500','none' , true),
    ('591', 'Slip', '3', '2', 'none', '015', 'Beam', '280', '500','none' , true),
    ('592', 'Slip', '3', '2', 'none', '016', 'Beam', '270', '500','none' , true),
    ('593', 'Slip', '3', '3', 'none', '017', 'Beam', '290', '500','none' , true),
    ('594', 'Slip', '3', '2', 'none', '018', 'Beam', '280', '500','none' , true),
    ('595', 'Slip', '3', '2', 'none', '019', 'Beam', '280', '500','none' , true),
    ('596', 'Slip', '3', '2', 'none', '020', 'Beam', '280', '500','none' , true),
    ('597', 'Slip', '3', '2', 'none', '021', 'Beam', '280', '500','none' , true),
    ('598', 'Slip', '3', '2', 'none', '022', 'Beam', '280', '500','none' , true),
    ('599', 'Slip', '3', '2', 'none', '023', 'Beam', '280', '500','none' , true),
    ('600', 'Slip', '3', '2', 'none', '024', 'Beam', '280', '500','none' , true),
    ('601', 'Slip', '3', '2', 'none', '025', 'Beam', '275', '500','none' , true),
    ('602', 'Slip', '3', '3', 'none', '026', 'Beam', '300', '500','none' , true),
    ('603', 'Slip', '3', '3', 'none', '027', 'Beam', '300', '600','none' , true),
    ('604', 'Slip', '3', '3', 'none', '028', 'Beam', '300', '600','none' , true),
    ('605', 'Slip', '3', '3', 'none', '029', 'Beam', '300', '500','none' , true),
    ('606', 'Slip', '3', '2', 'none', '030', 'Beam', '280', '500','none' , true),
    ('607', 'Slip', '3', '2', 'none', '031', 'Beam', '280', '500','none' , true),
    ('608', 'Slip', '3', '2', 'none', '032', 'Beam', '280', '500','none' , true),
    ('609', 'Slip', '3', '2', 'none', '033', 'Beam', '280', '500','none' , true),
    ('610', 'Slip', '3', '2', 'none', '034', 'Beam', '280', '500','none' , true),
    ('611', 'Slip', '3', '2', 'none', '035', 'Beam', '280', '500','none' , true),
    ('612', 'Slip', '3', '2', 'none', '036', 'Beam', '280', '500','none' , true),
    ('613', 'Slip', '3', '2', 'none', '037', 'Beam', '280', '500','none' , true),
    ('614', 'Slip', '3', '3', 'none', '038', 'Beam', '290', '500','none' , true),
    ('615', 'Slip', '3', '2', 'none', '039', 'Beam', '275', '500','none' , true),
    ('616', 'Slip', '3', '2', 'none', '040', 'Beam', '280', '500','none' , true),
    ('617', 'Slip', '3', '2', 'none', '041', 'Beam', '280', '500','none' , true),
    ('618', 'Slip', '3', '2', 'none', '042', 'Beam', '280', '500','none' , true),
    ('619', 'Slip', '3', '2', 'none', '043', 'Beam', '280', '500','none' , true),
    ('620', 'Slip', '3', '2', 'none', '044', 'Beam', '280', '500','none' , true),
    ('621', 'Slip', '3', '2', 'none', '045', 'Beam', '280', '500','none' , true),
    ('622', 'Slip', '3', '2', 'none', '046', 'Beam', '280', '500','none' , true),
    ('623', 'Slip', '3', '2', 'none', '047', 'Beam', '280', '500','none' , true),
    ('624', 'Slip', '3', '2', 'none', '048', 'Beam', '280', '500','none' , true),
    ('625', 'Slip', '3', '2', 'none', '049', 'Beam', '280', '500','none' , true),
    ('626', 'Slip', '3', '2', 'none', '050', 'Beam', '280', '500','none' , true),
    ('627', 'Slip', '3', '2', 'none', '051', 'Beam', '280', '500','none' , true),
    ('628', 'Slip', '3', '2', 'none', '052', 'Beam', '280', '500','none' , true),
    ('629', 'Slip', '3', '2', 'none', '053', 'Beam', '280', '500','none' , true),
    ('630', 'Slip', '3', '2', 'none', '054', 'Beam', '270', '500','none' , true),
    ('631', 'Slip', '3', '3', 'none', '055', 'Beam', '300', '600','none' , true),
    ('632', 'Slip', '3', '3', 'none', '056', 'Beam', '300', '600','none' , true),
    ('633', 'Slip', '3', '3', 'none', '057', 'Beam', '300', '600','none' , true),
    ('634', 'Slip', '3', '3', 'none', '058', 'Beam', '300', '600','none' , true),
    ('635', 'Slip', '3', '3', 'none', '059', 'Beam', '300', '600','none' , true),
    ('636', 'Slip', '3', '3', 'none', '060', 'Beam', '300', '600','none' , true),
    ('637', 'Slip', '3', '3', 'none', '061', 'Beam', '300', '600','none' , true),
    ('638', 'Slip', '3', '3', 'none', '062', 'Beam', '300', '600','none' , true),
    ('639', 'Slip', '3', '3', 'none', '063', 'Beam', '300', '600','none' , true),
    ('640', 'Slip', '3', '3', 'none', '064', 'Beam', '300', '600','none' , true),
    ('641', 'Slip', '3', '3', 'none', '065', 'Beam', '300', '600','none' , true),
    ('642', 'Slip', '3', '3', 'none', '066', 'Beam', '300', '600','none' , true),
    ('643', 'Slip', '3', '3', 'none', '067', 'Beam', '300', '600','none' , true),
    ('644', 'Slip', '3', '3', 'none', '068', 'Beam', '300', '600','none' , true),
    ('645', 'Slip', '3', '3', 'none', '069', 'Beam', '300', '600','none' , true),
    ('646', 'Slip', '3', '3', 'none', '070', 'Beam', '300', '600','none' , true),
    ('647', 'Slip', '3', '3', 'none', '071', 'Beam', '300', '600','none' , true),
    ('648', 'Slip', '3', '3', 'none', '072', 'Beam', '300', '600','none' , true),
    ('649', 'Slip', '3', '3', 'none', '073', 'Beam', '300', '600','none' , true),
    ('650', 'Slip', '3', '3', 'none', '074', 'Beam', '300', '600','none' , true),
    ('651', 'Slip', '3', '3', 'none', '075', 'Beam', '300', '600','none' , true),
    ('652', 'Slip', '3', '3', 'none', '076', 'Beam', '300', '600','none' , true),
    ('653', 'Slip', '3', '3', 'none', '077', 'Beam', '300', '600','none' , true),
    ('654', 'Slip', '3', '3', 'none', '078', 'Beam', '300', '600','none' , true),
    ('655', 'Slip', '3', '3', 'none', '079', 'Beam', '300', '600','none' , true),
    ('656', 'Slip', '3', '3', 'none', '080', 'Beam', '300', '600','none' , true),
    ('657', 'Slip', '3', '3', 'none', '081', 'Beam', '300', '600','none' , true),
    ('658', 'Slip', '3', '3', 'none', '082', 'Beam', '300', '600','none' , true),
    ('659', 'Slip', '3', '3', 'none', '083', 'Beam', '300', '600','none' , true),
    ('660', 'Slip', '3', '3', 'none', '084', 'Beam', '300', '600','none' , true),
    ('661', 'Slip', '3', '3', 'none', '085', 'Beam', '300', '600','none' , true),
    ('662', 'Slip', '3', '3', 'none', '086', 'Beam', '300', '600','none' , true),
    ('663', 'Slip', '3', '3', 'none', '087', 'Beam', '300', '600','none' , true),
    ('664', 'Slip', '3', '3', 'none', '088', 'Beam', '300', '600','none' , true),
    ('665', 'Slip', '3', '3', 'none', '089', 'Beam', '300', '600','none' , true),
    ('666', 'Slip', '3', '3', 'none', '090', 'Beam', '300', '600','none' , true),
    ('667', 'Slip', '3', '3', 'none', '091', 'Beam', '300', '600','none' , true),
    ('668', 'Slip', '3', '3', 'none', '092', 'Beam', '300', '600','none' , true),
    ('669', 'Slip', '3', '3', 'none', '093', 'Beam', '300', '600','none' , true),
    ('670', 'Slip', '3', '3', 'none', '094', 'Beam', '300', '600','none' , true),
    ('671', 'Slip', '3', '3', 'none', '095', 'Beam', '300', '600','none' , true),
    ('672', 'Slip', '3', '3', 'none', '096', 'Beam', '300', '600','none' , true),
    ('673', 'Slip', '3', '3', 'none', '097', 'Beam', '300', '600','none' , true),
    ('674', 'Slip', '3', '3', 'none', '098', 'Beam', '300', '600','none' , true),
    ('675', 'Slip', '3', '3', 'none', '099', 'Beam', '300', '600','none' , true),
    ('676', 'Slip', '3', '3', 'none', '100', 'Beam', '300', '600','none' , true),
    ('677', 'Slip', '3', '2', 'none', '101', 'Beam', '280', '600','none' , true),
    ('678', 'Slip', '3', '2', 'none', '102', 'Beam', '280', '600','none' , true),
    ('679', 'Slip', '3', '3', 'none', '103', 'Beam', '285', '500','none' , true),
    ('680', 'Slip', '3', '3', 'none', '104', 'Beam', '285', '500','none' , true),
    ('681', 'Slip', '3', '2', 'none', '105', 'Beam', '275', '500','none' , true),
    ('682', 'Slip', '3', '2', 'none', '106', 'Beam', '280', '500','none' , true),
    ('683', 'Slip', '3', '3', 'none', '107', 'Beam', '300', '600','none' , true),
    ('684', 'Slip', '3', '3', 'none', '108', 'Beam', '300', '600','none' , true),
    ('685', 'Slip', '3', '3', 'none', '109', 'Beam', '300', '600','none' , true),
    ('686', 'Slip', '3', '3', 'none', '110', 'Beam', '300', '600','none' , true),
    ('687', 'Slip', '3', '3', 'none', '111', 'Beam', '300', '600','none' , true),
    ('688', 'Slip', '3', '3', 'none', '112', 'Beam', '300', '600','none' , true),
    ('689', 'Slip', '3', '3', 'none', '113', 'Beam', '300', '600','none' , true),
    ('690', 'Slip', '3', '3', 'none', '114', 'Beam', '300', '600','none' , true),
    ('691', 'Slip', '3', '3', 'none', '115', 'Beam', '300', '600','none' , true),
    ('692', 'Slip', '3', '3', 'none', '116', 'Beam', '300', '600','none' , true),
    ('693', 'Slip', '3', '3', 'none', '117', 'Beam', '300', '600','none' , true),
    ('694', 'Slip', '3', '3', 'none', '118', 'Beam', '300', '600','none' , true),
    ('695', 'Slip', '3', '3', 'none', '119', 'Beam', '300', '600','none' , true),
    ('696', 'Slip', '3', '3', 'none', '120', 'Beam', '300', '600','none' , true),
    ('697', 'Slip', '3', '3', 'none', '121', 'Beam', '300', '600','none' , true),
    ('698', 'Slip', '3', '3', 'none', '122', 'Beam', '300', '600','none' , true),
    ('699', 'Slip', '3', '3', 'none', '123', 'Beam', '300', '600','none' , true),
    ('700', 'Slip', '3', '3', 'none', '124', 'Beam', '290', '600','none' , true),
    ('701', 'Slip', '3', '3', 'none', '125', 'Beam', '300', '600','none' , true),
    ('702', 'Slip', '4', '3', 'A', '1', 'Beam', '285', '600','none' , true),
    ('703', 'Slip', '4', '3', 'A', '2', 'Beam', '285', '600','none' , true),
    ('704', 'Slip', '4', '3', 'A', '3', 'Beam', '285', '600','none' , true),
    ('705', 'Slip', '4', '3', 'A', '5', 'Beam', '285', '600','none' , true),
    ('706', 'Slip', '4', '3', 'A', '7', 'Beam', '285', '600','none' , true),
    ('707', 'Slip', '4', '3', 'A', '8', 'Beam', '285', '600','none' , true),
    ('708', 'Slip', '4', '3', 'A', '10', 'Beam', '285', '600','none' , true),
    ('709', 'Slip', '4', '3', 'A', '11', 'Beam', '285', '600','none' , true),
    ('710', 'Slip', '4', '3', 'A', '12', 'Beam', '285', '600','none' , true),
    ('711', 'Slip', '4', '3', 'A', '13', 'Beam', '285', '600','none' , true),
    ('712', 'Slip', '4', '3', 'A', '14', 'Beam', '285', '600','none' , true),
    ('713', 'Slip', '4', '3', 'A', '15', 'Beam', '285', '600','none' , true),
    ('714', 'Slip', '4', '3', 'A', '17', 'Beam', '285', '600','none' , true),
    ('715', 'Slip', '4', '3', 'A', '19', 'Beam', '285', '600','none' , true),
    ('716', 'Slip', '4', '2', 'A', '28', 'Beam', '285', '600','none' , true),
    ('717', 'Slip', '4', '4', 'A', '32', 'Beam', '285', '600','none' , true),
    ('718', 'Slip', '4', '2', 'A', '49', 'Beam', '285', '600','none' , true),
    ('719', 'Slip', '4', '3', 'A', '51', 'Beam', '285', '600','none' , true),
    ('720', 'Slip', '4', '3', 'A', '53', 'Beam', '285', '600','none' , true),
    ('721', 'Slip', '4', '3', 'A', '58', 'Beam', '300', '600','none' , true),
    ('722', 'Slip', '4', '3', 'A', '60', 'Beam', '300', '600','none' , true),
    ('723', 'Slip', '4', '3', 'A', '65', 'Beam', '285', '600','none' , true),
    ('724', 'Slip', '4', '3', 'A', '67', 'Beam', '285', '600','none' , true),
    ('725', 'Slip', '4', '4', 'A', '72', 'WalkBeam', '350', '700','none' , true),
    ('726', 'Slip', '4', '4', 'A', '73', 'WalkBeam', '350', '700','none' , true),
    ('727', 'Slip', '4', '4', 'A', '74', 'WalkBeam', '350', '700','none' , true),
    ('728', 'Slip', '4', '4', 'A', '75', 'WalkBeam', '350', '700','none' , true),
    ('729', 'Slip', '4', '4', 'A', '78', 'WalkBeam', '400', '700','none' , true),
    ('730', 'Slip', '4', '4', 'A', '79', 'WalkBeam', '350', '700','none' , true),
    ('731', 'Slip', '4', '5', 'A', '91', 'WalkBeam', '400', '1000','none' , true),
    ('732', 'Slip', '4', '2', 'B', '106', 'Beam', '275', '500','none' , true),
    ('733', 'Slip', '4', '2', 'B', '109', 'Beam', '275', '500','none' , true),
    ('734', 'Slip', '4', '2', 'B', '110', 'Beam', '275', '500','none' , true),
    ('735', 'Slip', '4', '2', 'B', '111', 'Beam', '275', '500','none' , true),
    ('736', 'Slip', '4', '2', 'B', '112', 'Beam', '275', '500','none' , true),
    ('737', 'Slip', '4', '3', 'B', '113', 'Beam', '290', '500','none' , true),
    ('738', 'Slip', '4', '3', 'B', '114', 'Beam', '290', '500','none' , true),
    ('739', 'Slip', '4', '2', 'B', '115', 'Beam', '275', '550','none' , true),
    ('740', 'Slip', '4', '2', 'B', '118', 'Beam', '275', '550','none' , true),
    ('741', 'Slip', '4', '3', 'B', '119', 'Beam', '290', '550','none' , true),
    ('742', 'Slip', '4', '3', 'B', '120', 'Beam', '290', '550','none' , true),
    ('743', 'Slip', '4', '2', 'B', '121', 'Beam', '275', '550','none' , true),
    ('744', 'Slip', '4', '2', 'B', '122', 'Beam', '275', '550','none' , true),
    ('745', 'Slip', '4', '2', 'B', '124', 'Beam', '275', '550','none' , true),
    ('746', 'Slip', '4', '2', 'B', '125', 'Beam', '275', '550','none' , true),
    ('747', 'Slip', '4', '2', 'B', '126', 'Beam', '275', '550','none' , true),
    ('748', 'Slip', '4', '2', 'B', '127', 'Beam', '275', '550','none' , true),
    ('749', 'Slip', '4', '2', 'B', '128', 'Beam', '275', '550','none' , true),
    ('750', 'Slip', '4', '2', 'B', '129', 'Beam', '275', '550','none' , true),
    ('751', 'Slip', '4', '2', 'B', '130', 'Beam', '275', '550','none' , true),
    ('752', 'Slip', '4', '2', 'B', '132', 'Beam', '275', '550','none' , true),
    ('753', 'Slip', '4', '2', 'B', '134', 'Beam', '275', '550','none' , true),
    ('754', 'Slip', '4', '2', 'B', '136', 'Beam', '275', '550','none' , true),
    ('755', 'Slip', '4', '2', 'B', '137', 'Beam', '275', '550','none' , true),
    ('756', 'Slip', '4', '2', 'B', '138', 'Beam', '275', '550','none' , true),
    ('757', 'Slip', '4', '2', 'B', '139', 'Beam', '275', '550','none' , true),
    ('758', 'Slip', '4', '2', 'B', '140', 'Beam', '275', '550','none' , true),
    ('759', 'Slip', '4', '2', 'B', '141', 'Beam', '275', '550','none' , true),
    ('760', 'Slip', '4', '2', 'B', '142', 'Beam', '275', '550','none' , true),
    ('761', 'Slip', '4', '2', 'B', '143', 'Beam', '275', '550','none' , true),
    ('762', 'Slip', '4', '2', 'B', '144', 'Beam', '275', '550','none' , true),
    ('763', 'Slip', '4', '2', 'B', '146', 'Beam', '275', '550','none' , true),
    ('764', 'Slip', '4', '2', 'B', '148', 'Beam', '275', '550','none' , true),
    ('765', 'Slip', '4', '2', 'B', '154', 'Beam', '275', '550','none' , true),
    ('766', 'Slip', '4', '2', 'B', '155', 'Beam', '275', '550','none' , true),
    ('767', 'Slip', '4', '2', 'B', '158', 'Beam', '275', '550','none' , true),
    ('768', 'Slip', '4', '2', 'B', '160', 'Beam', '275', '550','none' , true),
    ('769', 'Slip', '4', '2', 'B', '161', 'Beam', '275', '550','none' , true),
    ('770', 'Slip', '4', '2', 'B', '162', 'Beam', '275', '550','none' , true),
    ('771', 'Slip', '4', '2', 'B', '165', 'Beam', '280', '550','none' , true),
    ('772', 'Slip', '4', '2', 'B', '168', 'Beam', '275', '550','none' , true),
    ('773', 'Slip', '4', '2', 'B', '170', 'Beam', '275', '550','none' , true),
    ('774', 'Slip', '4', '2', 'B', '171', 'Beam', '275', '550','none' , true),
    ('775', 'Slip', '4', '2', 'B', '173', 'Beam', '275', '550','none' , true),
    ('776', 'Slip', '4', '2', 'B', '174', 'Beam', '275', '550','none' , true),
    ('777', 'Slip', '4', '2', 'B', '175', 'Beam', '275', '550','none' , true),
    ('778', 'Slip', '4', '2', 'B', '176', 'Beam', '275', '550','none' , true),
    ('779', 'Slip', '4', '2', 'B', '177', 'Beam', '275', '550','none' , true),
    ('780', 'Slip', '4', '2', 'B', '178', 'Beam', '280', '550','none' , true),
    ('781', 'Slip', '4', '2', 'B', '179', 'Beam', '280', '700','none' , true),
    ('782', 'Slip', '4', '3', 'B', '187', 'Beam', '330', '700','none' , true),
    ('783', 'Slip', '4', '3', 'B', '188', 'Beam', '330', '700','none' , true),
    ('784', 'Slip', '4', '3', 'B', '189', 'Beam', '330', '700','none' , true),
    ('785', 'Slip', '4', '3', 'B', '190', 'Beam', '330', '700','none' , true),
    ('786', 'Slip', '4', '2', 'C', '201', 'Beam', '275', '600','none' , true),
    ('787', 'Slip', '4', '2', 'C', '202', 'Beam', '275', '600','none' , true),
    ('788', 'Slip', '4', '2', 'C', '204', 'Beam', '275', '600','none' , true),
    ('789', 'Slip', '4', '2', 'C', '205', 'Beam', '275', '600','none' , true),
    ('790', 'Slip', '4', '2', 'C', '206', 'Beam', '275', '600','none' , true),
    ('791', 'Slip', '4', '3', 'C', '207', 'Beam', '295', '600','none' , true),
    ('792', 'Slip', '4', '3', 'C', '208', 'Beam', '295', '600','none' , true),
    ('793', 'Slip', '4', '2', 'C', '209', 'Beam', '275', '600','none' , true),
    ('794', 'Slip', '4', '2', 'C', '210', 'Beam', '275', '600','none' , true),
    ('795', 'Slip', '4', '2', 'C', '211', 'Beam', '275', '600','none' , true),
    ('796', 'Slip', '4', '2', 'C', '212', 'Beam', '275', '600','none' , true),
    ('797', 'Slip', '4', '2', 'C', '213', 'Beam', '275', '600','none' , true),
    ('798', 'Slip', '4', '2', 'C', '214', 'Beam', '275', '600','none' , true),
    ('799', 'Slip', '4', '3', 'C', '215', 'Beam', '310', '600','none' , true),
    ('800', 'Slip', '4', '3', 'C', '216', 'Beam', '310', '600','none' , true),
    ('801', 'Slip', '4', '2', 'C', '217', 'Beam', '275', '600','none' , true),
    ('802', 'Slip', '4', '2', 'C', '218', 'Beam', '275', '600','none' , true),
    ('803', 'Slip', '4', '2', 'C', '219', 'Beam', '275', '600','none' , true),
    ('804', 'Slip', '4', '2', 'C', '220', 'Beam', '275', '600','none' , true),
    ('805', 'Slip', '4', '2', 'C', '221', 'Beam', '275', '600','none' , true),
    ('806', 'Slip', '4', '2', 'C', '222', 'Beam', '275', '600','none' , true),
    ('807', 'Slip', '4', '3', 'C', '223', 'Beam', '310', '600','none' , true),
    ('808', 'Slip', '4', '3', 'C', '224', 'Beam', '310', '600','none' , true),
    ('809', 'Slip', '4', '2', 'C', '225', 'Beam', '275', '600','none' , true),
    ('810', 'Slip', '4', '2', 'C', '226', 'Beam', '275', '600','none' , true),
    ('811', 'Slip', '4', '2', 'C', '227', 'Beam', '275', '600','none' , true),
    ('812', 'Slip', '4', '2', 'C', '228', 'Beam', '275', '600','none' , true),
    ('813', 'Slip', '4', '2', 'C', '229', 'Beam', '275', '600','none' , true),
    ('814', 'Slip', '4', '2', 'C', '230', 'Beam', '275', '600','none' , true),
    ('815', 'Slip', '4', '3', 'C', '231', 'Beam', '310', '600','none' , true),
    ('816', 'Slip', '4', '3', 'C', '232', 'Beam', '310', '600','none' , true),
    ('817', 'Slip', '4', '2', 'C', '233', 'Beam', '275', '600','none' , true),
    ('818', 'Slip', '4', '2', 'C', '234', 'Beam', '275', '600','none' , true),
    ('819', 'Slip', '4', '2', 'C', '235', 'Beam', '275', '600','none' , true),
    ('820', 'Slip', '4', '2', 'C', '236', 'Beam', '275', '600','none' , true),
    ('821', 'Slip', '4', '2', 'C', '237', 'Beam', '275', '600','none' , true),
    ('822', 'Slip', '4', '2', 'C', '238', 'Beam', '275', '600','none' , true),
    ('823', 'Slip', '4', '3', 'C', '239', 'Beam', '310', '600','none' , true),
    ('824', 'Slip', '4', '3', 'C', '240', 'Beam', '310', '600','none' , true),
    ('825', 'Slip', '4', '2', 'C', '241', 'Beam', '275', '600','none' , true),
    ('826', 'Slip', '4', '2', 'C', '242', 'Beam', '275', '600','none' , true),
    ('827', 'Slip', '4', '2', 'C', '243', 'Beam', '275', '600','none' , true),
    ('828', 'Slip', '4', '2', 'C', '244', 'Beam', '275', '600','none' , true),
    ('829', 'Slip', '4', '2', 'C', '245', 'Beam', '275', '600','none' , true),
    ('830', 'Slip', '4', '2', 'C', '246', 'Beam', '275', '600','none' , true),
    ('831', 'Slip', '4', '3', 'C', '247', 'Beam', '310', '600','none' , true),
    ('832', 'Slip', '4', '3', 'C', '248', 'Beam', '310', '600','none' , true),
    ('833', 'Slip', '4', '2', 'C', '249', 'Beam', '275', '600','none' , true),
    ('834', 'Slip', '4', '2', 'C', '250', 'Beam', '275', '600','none' , true),
    ('835', 'Slip', '4', '2', 'C', '251', 'Beam', '275', '600','none' , true),
    ('836', 'Slip', '4', '2', 'C', '252', 'Beam', '275', '600','none' , true),
    ('837', 'Slip', '4', '2', 'C', '253', 'Beam', '275', '600','none' , true),
    ('838', 'Slip', '4', '2', 'C', '254', 'Beam', '275', '600','none' , true),
    ('839', 'Slip', '4', '3', 'C', '255', 'Beam', '310', '600','none' , true),
    ('840', 'Slip', '4', '3', 'C', '256', 'Beam', '310', '600','none' , true),
    ('841', 'Slip', '4', '2', 'C', '257', 'Beam', '275', '600','none' , true),
    ('842', 'Slip', '4', '2', 'C', '258', 'Beam', '275', '600','none' , true),
    ('843', 'Slip', '4', '2', 'C', '259', 'Beam', '275', '600','none' , true),
    ('844', 'Slip', '4', '2', 'C', '260', 'Beam', '275', '600','none' , true),
    ('845', 'Slip', '4', '2', 'C', '261', 'Beam', '275', '600','none' , true),
    ('846', 'Slip', '4', '2', 'C', '262', 'Beam', '275', '600','none' , true),
    ('847', 'Slip', '4', '3', 'C', '263', 'Beam', '310', '600','none' , true),
    ('848', 'Slip', '4', '3', 'C', '264', 'Beam', '310', '600','none' , true),
    ('849', 'Slip', '4', '2', 'C', '265', 'Beam', '275', '600','none' , true),
    ('850', 'Slip', '4', '2', 'C', '266', 'Beam', '275', '600','none' , true),
    ('851', 'Slip', '4', '2', 'C', '267', 'Beam', '275', '600','none' , true),
    ('852', 'Slip', '4', '2', 'C', '268', 'Beam', '275', '600','none' , true),
    ('853', 'Slip', '4', '2', 'C', '269', 'Beam', '275', '600','none' , true),
    ('854', 'Slip', '4', '2', 'C', '270', 'Beam', '275', '600','none' , true),
    ('855', 'Slip', '4', '3', 'C', '271', 'Beam', '310', '600','none' , true),
    ('856', 'Slip', '4', '3', 'C', '272', 'Beam', '310', '600','none' , true),
    ('857', 'Slip', '4', '2', 'C', '273', 'Beam', '275', '600','none' , true),
    ('858', 'Slip', '4', '2', 'C', '274', 'Beam', '275', '600','none' , true),
    ('859', 'Slip', '4', '2', 'C', '275', 'Beam', '275', '600','none' , true),
    ('860', 'Slip', '4', '2', 'C', '276', 'Beam', '275', '600','none' , true),
    ('861', 'Slip', '4', '2', 'C', '277', 'Beam', '275', '600','none' , true),
    ('862', 'Slip', '4', '2', 'C', '278', 'Beam', '275', '600','none' , true),
    ('863', 'Slip', '4', '3', 'C', '279', 'Beam', '310', '600','none' , true),
    ('864', 'Slip', '4', '3', 'C', '280', 'Beam', '310', '600','none' , true),
    ('865', 'Slip', '4', '2', 'C', '281', 'Beam', '275', '600','none' , true),
    ('866', 'Slip', '4', '2', 'C', '282', 'Beam', '275', '600','none' , true),
    ('867', 'Slip', '4', '2', 'C', '283', 'Beam', '275', '600','none' , true),
    ('868', 'Slip', '4', '2', 'C', '284', 'Beam', '275', '600','none' , true),
    ('869', 'Slip', '4', '2', 'C', '285', 'Beam', '275', '600','none' , true),
    ('870', 'Slip', '4', '2', 'C', '286', 'Beam', '275', '600','none' , true),
    ('871', 'Slip', '4', '3', 'C', '287', 'Beam', '310', '600','none' , true),
    ('872', 'Slip', '4', '3', 'C', '288', 'Beam', '310', '600','none' , true),
    ('873', 'Slip', '4', '2', 'C', '289', 'Beam', '275', '600','none' , true),
    ('874', 'Slip', '4', '2', 'C', '290', 'Beam', '275', '600','none' , true),
    ('875', 'Slip', '4', '2', 'C', '291', 'Beam', '275', '600','none' , true),
    ('876', 'Slip', '4', '2', 'C', '292', 'Beam', '275', '600','none' , true),
    ('877', 'Slip', '4', '2', 'C', '293', 'Beam', '275', '600','none' , true),
    ('878', 'Slip', '4', '2', 'C', '294', 'Beam', '275', '600','none' , true),
    ('879', 'Slip', '4', '2', 'C', '295', 'Beam', '275', '600','none' , true),
    ('880', 'Slip', '4', '2', 'C', '296', 'Beam', '275', '600','none' , true),
    ('881', 'Slip', '4', '2', 'D', '301', 'Beam', '260', '550','none' , true),
    ('882', 'Slip', '4', '2', 'D', '302', 'Beam', '260', '550','none' , true),
    ('883', 'Slip', '4', '2', 'D', '303', 'Beam', '260', '550','none' , true),
    ('884', 'Slip', '4', '2', 'D', '304', 'Beam', '260', '550','none' , true),
    ('885', 'Slip', '4', '2', 'D', '305', 'Beam', '260', '550','none' , true),
    ('886', 'Slip', '4', '2', 'D', '306', 'Beam', '270', '550','none' , true),
    ('887', 'Slip', '4', '2', 'D', '307', 'Beam', '270', '550','none' , true),
    ('888', 'Slip', '4', '2', 'D', '308', 'Beam', '260', '550','none' , true),
    ('889', 'Slip', '4', '2', 'D', '309', 'Beam', '250', '550','none' , true),
    ('890', 'Slip', '4', '2', 'D', '310', 'Beam', '260', '550','none' , true),
    ('891', 'Slip', '4', '2', 'D', '311', 'Beam', '270', '550','none' , true),
    ('892', 'Slip', '4', '2', 'D', '312', 'Beam', '260', '550','none' , true),
    ('893', 'Slip', '4', '2', 'D', '313', 'Beam', '260', '550','none' , true),
    ('894', 'Slip', '4', '2', 'D', '314', 'Beam', '260', '550','none' , true),
    ('895', 'Slip', '4', '2', 'D', '315', 'Beam', '260', '550','none' , true),
    ('896', 'Slip', '4', '2', 'D', '316', 'Beam', '260', '550','none' , true),
    ('897', 'Slip', '4', '2', 'D', '317', 'Beam', '260', '550','none' , true),
    ('898', 'Slip', '4', '2', 'D', '318', 'Beam', '260', '550','none' , true),
    ('899', 'Slip', '4', '2', 'D', '319', 'Beam', '260', '550','none' , true),
    ('900', 'Slip', '4', '2', 'D', '320', 'Beam', '260', '550','none' , true),
    ('901', 'Slip', '4', '2', 'D', '321', 'Beam', '260', '550','none' , true),
    ('902', 'Slip', '4', '2', 'D', '322', 'Beam', '260', '550','none' , true),
    ('903', 'Slip', '4', '2', 'D', '323', 'Beam', '260', '550','none' , true),
    ('904', 'Slip', '4', '2', 'D', '324', 'Beam', '260', '550','none' , true),
    ('905', 'Slip', '4', '2', 'D', '325', 'Beam', '260', '550','none' , true),
    ('906', 'Slip', '4', '2', 'D', '326', 'Beam', '260', '550','none' , true),
    ('907', 'Slip', '4', '2', 'D', '327', 'Beam', '260', '550','none' , true),
    ('908', 'Slip', '4', '2', 'D', '328', 'Beam', '260', '550','none' , true),
    ('909', 'Slip', '4', '2', 'D', '329', 'Beam', '260', '550','none' , true),
    ('910', 'Slip', '4', '2', 'D', '331', 'Beam', '260', '550','none' , true),
    ('911', 'Slip', '4', '2', 'D', '332', 'Beam', '260', '550','none' , true),
    ('912', 'Slip', '4', '2', 'D', '333', 'Beam', '260', '550','none' , true),
    ('913', 'Slip', '4', '2', 'D', '334', 'Beam', '260', '550','none' , true),
    ('914', 'Slip', '4', '2', 'D', '335', 'Beam', '260', '550','none' , true),
    ('915', 'Slip', '4', '2', 'D', '336', 'Beam', '260', '550','none' , true),
    ('916', 'Slip', '4', '2', 'D', '337', 'Beam', '260', '550','none' , true),
    ('917', 'Slip', '4', '2', 'D', '338', 'Beam', '260', '550','none' , true),
    ('918', 'Slip', '4', '2', 'D', '339', 'Beam', '260', '550','none' , true),
    ('919', 'Slip', '4', '2', 'D', '340', 'Beam', '260', '550','none' , true),
    ('920', 'Slip', '4', '2', 'D', '341', 'Beam', '260', '550','none' , true),
    ('921', 'Slip', '4', '2', 'D', '342', 'Beam', '260', '550','none' , true),
    ('922', 'Slip', '4', '2', 'D', '343', 'Beam', '260', '550','none' , true),
    ('923', 'Slip', '4', '2', 'D', '344', 'Beam', '260', '550','none' , true),
    ('924', 'Slip', '4', '2', 'D', '345', 'Beam', '260', '550','none' , true),
    ('925', 'Slip', '4', '2', 'D', '346', 'Beam', '260', '550','none' , true),
    ('926', 'Slip', '4', '2', 'D', '347', 'Beam', '260', '550','none' , true),
    ('927', 'Slip', '4', '2', 'D', '348', 'Beam', '260', '550','none' , true),
    ('928', 'Slip', '4', '2', 'D', '349', 'Beam', '260', '550','none' , true),
    ('929', 'Slip', '4', '2', 'D', '350', 'Beam', '260', '550','none' , true),
    ('930', 'Slip', '4', '2', 'D', '351', 'Beam', '260', '550','none' , true),
    ('931', 'Slip', '4', '2', 'D', '352', 'Beam', '260', '550','none' , true),
    ('932', 'Slip', '4', '2', 'D', '353', 'Beam', '260', '550','none' , true),
    ('933', 'Slip', '4', '2', 'D', '354', 'Beam', '260', '550','none' , true),
    ('934', 'Slip', '4', '2', 'D', '355', 'Beam', '260', '550','none' , true),
    ('935', 'Slip', '4', '2', 'D', '356', 'Beam', '260', '550','none' , true),
    ('936', 'Slip', '4', '2', 'D', '357', 'Beam', '260', '550','none' , true),
    ('937', 'Slip', '4', '2', 'D', '358', 'Beam', '260', '550','none' , true),
    ('938', 'Slip', '4', '2', 'D', '359', 'Beam', '260', '550','none' , true),
    ('939', 'Slip', '4', '2', 'D', '360', 'Beam', '260', '550','none' , true),
    ('940', 'Slip', '4', '2', 'D', '361', 'Beam', '260', '550','none' , true),
    ('941', 'Slip', '4', '2', 'D', '362', 'Beam', '260', '550','none' , true),
    ('942', 'Slip', '4', '2', 'D', '363', 'Beam', '260', '550','none' , true),
    ('943', 'Slip', '4', '2', 'D', '364', 'Beam', '260', '550','none' , true),
    ('944', 'Slip', '4', '2', 'D', '365', 'Beam', '260', '550','none' , true),
    ('945', 'Slip', '4', '2', 'D', '366', 'Beam', '260', '550','none' , true),
    ('946', 'Slip', '4', '2', 'D', '367', 'Beam', '260', '550','none' , true),
    ('947', 'Slip', '4', '2', 'D', '368', 'Beam', '260', '550','none' , true),
    ('948', 'Slip', '4', '2', 'D', '369', 'Beam', '260', '550','none' , true),
    ('949', 'Slip', '4', '2', 'D', '370', 'Beam', '260', '550','none' , true),
    ('950', 'Slip', '4', '2', 'D', '371', 'Beam', '260', '550','none' , true),
    ('951', 'Slip', '4', '2', 'D', '372', 'Beam', '260', '550','none' , true),
    ('952', 'Slip', '4', '2', 'D', '373', 'Beam', '260', '550','none' , true),
    ('953', 'Slip', '4', '2', 'D', '374', 'Beam', '260', '550','none' , true),
    ('954', 'Slip', '4', '2', 'D', '375', 'Beam', '260', '550','none' , true),
    ('955', 'Slip', '4', '1', 'E', '401', 'Beam', '229', '550','none' , true),
    ('956', 'Slip', '4', '1', 'E', '402', 'Beam', '229', '550','none' , true),
    ('957', 'Slip', '4', '2', 'E', '403', 'Beam', '275', '550','none' , true),
    ('958', 'Slip', '4', '2', 'E', '404', 'Beam', '275', '550','none' , true),
    ('959', 'Slip', '4', '2', 'E', '405', 'Beam', '275', '550','none' , true),
    ('960', 'Slip', '4', '2', 'E', '406', 'Beam', '275', '550','none' , true),
    ('961', 'Slip', '4', '2', 'E', '407', 'Beam', '275', '550','none' , true),
    ('962', 'Slip', '4', '2', 'E', '408', 'Beam', '280', '550','none' , true),
    ('963', 'Slip', '4', '2', 'E', '409', 'Beam', '275', '550','none' , true),
    ('964', 'Slip', '4', '2', 'E', '410', 'Beam', '275', '550','none' , true),
    ('965', 'Slip', '4', '2', 'E', '411', 'Beam', '275', '550','none' , true),
    ('966', 'Slip', '4', '2', 'E', '412', 'Beam', '275', '550','none' , true),
    ('967', 'Slip', '4', '2', 'E', '413', 'Beam', '275', '550','none' , true),
    ('968', 'Slip', '4', '2', 'E', '414', 'Beam', '275', '550','none' , true),
    ('969', 'Slip', '4', '2', 'E', '415', 'Beam', '275', '550','none' , true),
    ('970', 'Slip', '4', '2', 'E', '416', 'Beam', '275', '550','none' , true),
    ('971', 'Slip', '4', '2', 'E', '417', 'Beam', '270', '550','none' , true),
    ('972', 'Slip', '4', '2', 'E', '418', 'Beam', '270', '550','none' , true),
    ('973', 'Slip', '4', '2', 'E', '419', 'Beam', '275', '550','none' , true),
    ('974', 'Slip', '4', '2', 'E', '420', 'Beam', '275', '550','none' , true),
    ('975', 'Slip', '4', '2', 'E', '421', 'Beam', '275', '550','none' , true),
    ('976', 'Slip', '4', '2', 'E', '423', 'Beam', '275', '550','none' , true),
    ('977', 'Slip', '4', '2', 'E', '424', 'Beam', '275', '550','none' , true),
    ('978', 'Slip', '4', '2', 'E', '425', 'Beam', '275', '550','none' , true),
    ('979', 'Slip', '4', '2', 'E', '426', 'Beam', '275', '550','none' , true),
    ('980', 'Slip', '4', '2', 'E', '427', 'Beam', '275', '550','none' , true),
    ('981', 'Slip', '4', '2', 'E', '428', 'Beam', '275', '550','none' , true),
    ('982', 'Slip', '4', '2', 'E', '429', 'Beam', '275', '550','none' , true),
    ('983', 'Slip', '4', '2', 'E', '431', 'Beam', '275', '550','none' , true),
    ('984', 'Slip', '4', '2', 'E', '432', 'Beam', '275', '550','none' , true),
    ('985', 'Slip', '4', '2', 'E', '433', 'Beam', '275', '550','none' , true),
    ('986', 'Slip', '4', '2', 'E', '434', 'Beam', '275', '550','none' , true),
    ('987', 'Slip', '4', '2', 'E', '435', 'Beam', '275', '550','none' , true),
    ('988', 'Slip', '4', '2', 'E', '436', 'Beam', '275', '550','none' , true),
    ('989', 'Slip', '4', '2', 'E', '437', 'Beam', '275', '550','none' , true),
    ('990', 'Slip', '4', '2', 'E', '438', 'Beam', '270', '550','none' , true),
    ('991', 'Slip', '4', '2', 'E', '439', 'Beam', '280', '550','none' , true),
    ('992', 'Slip', '4', '2', 'E', '440', 'Beam', '280', '550','none' , true),
    ('993', 'Slip', '4', '2', 'E', '441', 'Beam', '280', '550','none' , true),
    ('994', 'Slip', '4', '2', 'E', '442', 'Beam', '280', '550','none' , true),
    ('995', 'Slip', '4', '2', 'E', '443', 'Beam', '275', '550','none' , true),
    ('996', 'Slip', '4', '2', 'E', '445', 'Beam', '275', '550','none' , true),
    ('997', 'Slip', '4', '2', 'E', '447', 'Beam', '275', '550','none' , true),
    ('998', 'Slip', '4', '2', 'E', '448', 'Beam', '280', '550','none' , true),
    ('999', 'Slip', '4', '2', 'E', '449', 'Beam', '275', '550','none' , true),
    ('1000', 'Slip', '4', '2', 'E', '451', 'Beam', '275', '550','none' , true),
    ('1001', 'Slip', '4', '2', 'E', '452', 'Beam', '275', '550','none' , true),
    ('1002', 'Slip', '4', '2', 'E', '453', 'Beam', '270', '550','none' , true),
    ('1003', 'Slip', '4', '2', 'E', '454', 'Beam', '275', '550','none' , true),
    ('1004', 'Slip', '4', '2', 'E', '455', 'Beam', '275', '550','none' , true),
    ('1005', 'Slip', '4', '2', 'E', '456', 'Beam', '275', '550','none' , true),
    ('1006', 'Slip', '4', '3', 'F', '501', 'RearBuoy', '320', '1600','none' , true),
    ('1007', 'Slip', '4', '3', 'F', '503', 'RearBuoy', '320', '1600','none' , true),
    ('1008', 'Slip', '4', '3', 'F', '504', 'RearBuoy', '320', '1600','none' , true),
    ('1009', 'Slip', '4', '3', 'F', '505', 'RearBuoy', '320', '1600','none' , true),
    ('1010', 'Slip', '4', '3', 'F', '506', 'RearBuoy', '300', '1600','none' , true),
    ('1011', 'Slip', '4', '3', 'F', '507', 'RearBuoy', '300', '1600','none' , true),
    ('1012', 'Slip', '4', '3', 'F', '508', 'RearBuoy', '300', '1600','none' , true),
    ('1013', 'Slip', '4', '3', 'F', '509', 'RearBuoy', '300', '1600','none' , true),
    ('1014', 'Slip', '4', '3', 'F', '510', 'RearBuoy', '300', '1600','none' , true),
    ('1015', 'Slip', '4', '3', 'F', '511', 'RearBuoy', '300', '1600','none' , true),
    ('1016', 'Slip', '4', '3', 'F', '512', 'RearBuoy', '300', '1600','none' , true),
    ('1017', 'Slip', '4', '3', 'F', '513', 'RearBuoy', '300', '1600','none' , true),
    ('1018', 'Slip', '4', '3', 'F', '514', 'RearBuoy', '300', '1600','none' , true),
    ('1019', 'Slip', '4', '3', 'F', '515', 'RearBuoy', '300', '1600','none' , true),
    ('1020', 'Slip', '4', '3', 'F', '516', 'RearBuoy', '300', '1600','none' , true),
    ('1021', 'Slip', '4', '3', 'F', '521', 'RearBuoy', '320', '1600','none' , true),
    ('1022', 'Slip', '4', '3', 'F', '526', 'RearBuoy', '320', '1600','none' , true),
    ('1023', 'Slip', '4', '3', 'F', '527', 'RearBuoy', '320', '1600','none' , true),
    ('1024', 'Slip', '4', '3', 'F', '528', 'RearBuoy', '320', '1600','none' , true),
    ('1025', 'Slip', '4', '3', 'F', '529', 'RearBuoy', '320', '1600','none' , true),
    ('1026', 'Slip', '4', '3', 'F', '534', 'RearBuoy', '320', '1600','none' , true),
    ('1027', 'Slip', '4', '3', 'F', '535', 'RearBuoy', '320', '1600','none' , true),
    ('1028', 'Slip', '4', '3', 'F', '536', 'RearBuoy', '320', '1600','none' , true),
    ('1029', 'Slip', '4', '3', 'F', '537', 'RearBuoy', '320', '1600','none' , true),
    ('1030', 'Slip', '4', '3', 'F', '538', 'RearBuoy', '320', '1600','none' , true),
    ('1031', 'Slip', '4', '3', 'F', '540', 'RearBuoy', '320', '1600','none' , true),
    ('1032', 'Slip', '4', '3', 'F', '541', 'RearBuoy', '320', '1600','none' , true),
    ('1033', 'Slip', '4', '3', 'F', '542', 'RearBuoy', '320', '1600','none' , true),
    ('1034', 'Slip', '4', '3', 'F', '543', 'RearBuoy', '320', '1600','none' , true),
    ('1035', 'Slip', '4', '4', 'F', '544', 'RearBuoy', '340', '1600','none' , true),
    ('1036', 'Slip', '4', '3', 'F', '545', 'RearBuoy', '300', '1600','none' , true),
    ('1037', 'Slip', '4', '3', 'F', '546', 'RearBuoy', '300', '1600','none' , true),
    ('1038', 'Slip', '4', '3', 'F', '547', 'RearBuoy', '300', '1600','none' , true),
    ('1039', 'Slip', '4', '3', 'F', '548', 'RearBuoy', '300', '1600','none' , true),
    ('1040', 'Slip', '4', '3', 'F', '549', 'RearBuoy', '300', '1600','none' , true),
    ('1041', 'Slip', '4', '3', 'F', '550', 'RearBuoy', '300', '1600','none' , true),
    ('1042', 'Slip', '4', '3', 'F', '551', 'RearBuoy', '300', '1600','none' , true),
    ('1043', 'Slip', '4', '3', 'F', '553', 'RearBuoy', '300', '1600','none' , true),
    ('1044', 'Slip', '4', '3', 'F', '554', 'RearBuoy', '300', '1600','none' , true),
    ('1045', 'Slip', '4', '3', 'F', '556', 'RearBuoy', '300', '1600','none' , true),
    ('1046', 'Slip', '4', '3', 'F', '557', 'RearBuoy', '300', '1600','none' , true),
    ('1047', 'Slip', '4', '3', 'F', '558', 'RearBuoy', '300', '1600','none' , true),
    ('1048', 'Slip', '4', '3', 'F', '559', 'RearBuoy', '300', '1600','none' , true),
    ('1049', 'Slip', '4', '3', 'F', '561', 'RearBuoy', '300', '1600','none' , true),
    ('1050', 'Slip', '4', '3', 'F', '562', 'RearBuoy', '300', '1600','none' , true),
    ('1051', 'Slip', '4', '3', 'F', '563', 'RearBuoy', '300', '1600','none' , true),
    ('1052', 'Slip', '4', '3', 'F', '564', 'RearBuoy', '300', '1600','none' , true),
    ('1053', 'Slip', '4', '3', 'F', '566', 'RearBuoy', '300', '1600','none' , true),
    ('1054', 'Slip', '4', '3', 'F', '567', 'RearBuoy', '300', '1600','none' , true),
    ('1055', 'Slip', '4', '3', 'F', '571', 'RearBuoy', '300', '1600','none' , true),
    ('1056', 'Slip', '4', '3', 'F', '572', 'RearBuoy', '300', '1600','none' , true),
    ('1057', 'Slip', '4', '3', 'F', '573', 'RearBuoy', '300', '1600','none' , true),
    ('1058', 'Slip', '4', '3', 'F', '575', 'RearBuoy', '300', '1600','none' , true),
    ('1059', 'Slip', '5', '3', 'B', '1', 'Beam', '285', '500','none' , true),
    ('1060', 'Slip', '5', '3', 'B', '2', 'Beam', '290', '500','none' , true),
    ('1061', 'Slip', '5', '3', 'B', '3', 'Beam', '290', '500','none' , true),
    ('1062', 'Slip', '5', '3', 'B', '4', 'Beam', '290', '500','none' , true),
    ('1063', 'Slip', '5', '3', 'B', '5', 'Beam', '300', '500','none' , true),
    ('1064', 'Slip', '5', '3', 'B', '6', 'Beam', '290', '500','none' , true),
    ('1065', 'Slip', '5', '3', 'B', '7', 'Beam', '300', '500','none' , true),
    ('1066', 'Slip', '5', '3', 'B', '8', 'Beam', '300', '500','none' , true),
    ('1067', 'Slip', '5', '3', 'B', '9', 'Beam', '290', '500','none' , true),
    ('1068', 'Slip', '5', '3', 'B', '10', 'Beam', '300', '500','none' , true),
    ('1069', 'Slip', '5', '3', 'B', '11', 'Beam', '300', '500','none' , true),
    ('1070', 'Slip', '5', '3', 'B', '12', 'Beam', '300', '500','none' , true),
    ('1071', 'Slip', '5', '3', 'B', '13', 'Beam', '300', '500','none' , true),
    ('1072', 'Slip', '5', '3', 'B', '14', 'Beam', '300', '500','none' , true),
    ('1073', 'Slip', '5', '3', 'B', '15', 'Beam', '300', '500','none' , true),
    ('1074', 'Slip', '5', '3', 'B', '16', 'Beam', '300', '500','none' , true),
    ('1075', 'Slip', '5', '3', 'B', '17', 'Beam', '290', '500','none' , true),
    ('1076', 'Slip', '5', '3', 'B', '18', 'Beam', '300', '500','none' , true),
    ('1077', 'Slip', '5', '3', 'B', '19', 'Beam', '300', '500','none' , true),
    ('1078', 'Slip', '5', '3', 'B', '20', 'Beam', '300', '500','none' , true),
    ('1079', 'Slip', '5', '3', 'B', '21', 'Beam', '300', '500','none' , true),
    ('1080', 'Slip', '5', '3', 'B', '22', 'Beam', '300', '500','none' , true),
    ('1081', 'Slip', '5', '3', 'B', '23', 'Beam', '290', '500','none' , true),
    ('1082', 'Slip', '5', '3', 'B', '24', 'Beam', '290', '500','none' , true),
    ('1083', 'Slip', '5', '3', 'B', '25', 'Beam', '300', '500','none' , true),
    ('1084', 'Slip', '5', '3', 'B', '26', 'Beam', '300', '500','none' , true),
    ('1085', 'Slip', '5', '3', 'B', '27', 'Beam', '300', '500','none' , true),
    ('1086', 'Slip', '5', '3', 'B', '28', 'Beam', '300', '500','none' , true),
    ('1087', 'Slip', '5', '3', 'B', '29', 'Beam', '300', '500','none' , true),
    ('1088', 'Slip', '5', '3', 'B', '30', 'Beam', '300', '500','none' , true),
    ('1089', 'Slip', '5', '3', 'B', '31', 'Beam', '290', '500','none' , true),
    ('1090', 'Slip', '5', '3', 'B', '32', 'Beam', '290', '500','none' , true),
    ('1091', 'Slip', '5', '3', 'B', '33', 'Beam', '300', '500','none' , true),
    ('1092', 'Slip', '5', '3', 'B', '34', 'Beam', '300', '550','none' , true),
    ('1093', 'Slip', '5', '3', 'B', '35', 'Beam', '300', '550','none' , true),
    ('1094', 'Slip', '5', '3', 'B', '36', 'Beam', '300', '550','none' , true),
    ('1095', 'Slip', '5', '3', 'B', '37', 'Beam', '300', '550','none' , true),
    ('1096', 'Slip', '5', '3', 'B', '38', 'Beam', '300', '550','none' , true),
    ('1097', 'Slip', '5', '3', 'B', '39', 'Beam', '300', '550','none' , true),
    ('1098', 'Slip', '5', '3', 'B', '40', 'Beam', '300', '550','none' , true),
    ('1099', 'Slip', '5', '3', 'B', '41', 'Beam', '300', '550','none' , true),
    ('1100', 'Slip', '5', '3', 'B', '42', 'Beam', '300', '550','none' , true),
    ('1101', 'Slip', '5', '3', 'B', '43', 'Beam', '290', '550','none' , true),
    ('1102', 'Slip', '5', '3', 'B', '44', 'Beam', '300', '550','none' , true),
    ('1103', 'Slip', '5', '3', 'B', '45', 'Beam', '330', '550','none' , true),
    ('1104', 'Slip', '5', '3', 'B', '46', 'Beam', '330', '550','none' , true),
    ('1105', 'Slip', '5', '3', 'B', '47', 'Beam', '300', '550','none' , true),
    ('1106', 'Slip', '5', '3', 'B', '48', 'Beam', '300', '550','none' , true),
    ('1107', 'Slip', '5', '3', 'B', '49', 'Beam', '300', '550','none' , true),
    ('1108', 'Slip', '5', '3', 'B', '50', 'Beam', '300', '650','none' , true),
    ('1109', 'Slip', '5', '2', 'B', '51', 'Beam', '275', '650','none' , true),
    ('1110', 'Slip', '5', '2', 'B', '52', 'Beam', '275', '650','none' , true),
    ('1111', 'Slip', '5', '2', 'B', '53', 'Beam', '275', '650','none' , true),
    ('1112', 'Slip', '5', '2', 'B', '54', 'Beam', '275', '650','none' , true),
    ('1113', 'Slip', '5', '2', 'B', '55', 'Beam', '275', '650','none' , true),
    ('1114', 'Slip', '5', '2', 'B', '56', 'Beam', '275', '650','none' , true),
    ('1115', 'Slip', '5', '2', 'B', '57', 'Beam', '275', '650','none' , true),
    ('1116', 'Slip', '5', '2', 'B', '58', 'Beam', '275', '650','none' , true),
    ('1117', 'Slip', '5', '2', 'B', '59', 'Beam', '275', '650','none' , true),
    ('1118', 'Slip', '5', '2', 'B', '60', 'Beam', '275', '650','none' , true),
    ('1119', 'Slip', '5', '2', 'B', '61', 'Beam', '275', '650','none' , true),
    ('1120', 'Slip', '5', '2', 'B', '62', 'Beam', '275', '650','none' , true),
    ('1121', 'Slip', '5', '2', 'B', '63', 'Beam', '275', '650','none' , true),
    ('1122', 'Slip', '5', '2', 'B', '64', 'Beam', '275', '650','none' , true),
    ('1123', 'Slip', '5', '3', 'B', '65', 'Beam', '290', '650','none' , true),
    ('1124', 'Slip', '5', '3', 'B', '66', 'Beam', '290', '650','none' , true),
    ('1125', 'Slip', '5', '2', 'B', '67', 'Beam', '275', '650','none' , true),
    ('1126', 'Slip', '5', '2', 'B', '68', 'Beam', '275', '650','none' , true),
    ('1127', 'Slip', '5', '2', 'B', '69', 'Beam', '275', '650','none' , true),
    ('1128', 'Slip', '5', '2', 'B', '70', 'Beam', '275', '650','none' , true),
    ('1129', 'Slip', '5', '3', 'B', '71', 'Beam', '300', '650','none' , true),
    ('1130', 'Slip', '5', '3', 'B', '72', 'Beam', '300', '650','none' , true),
    ('1131', 'Slip', '5', '3', 'B', '73', 'Beam', '290', '650','none' , true),
    ('1132', 'Slip', '5', '3', 'B', '74', 'Beam', '285', '650','none' , true),
    ('1133', 'Slip', '5', '3', 'B', '75', 'WalkBeam', '330', '900','none' , true),
    ('1134', 'Slip', '5', '3', 'B', '76', 'WalkBeam', '330', '900','none' , true),
    ('1135', 'Slip', '5', '3', 'B', '77', 'WalkBeam', '330', '1000','none' , true),
    ('1136', 'Slip', '5', '3', 'B', '78', 'WalkBeam', '330', '1000','none' , true),
    ('1137', 'Slip', '5', '4', 'B', '79', 'WalkBeam', '380', '1000','none' , true),
    ('1138', 'Slip', '5', '4', 'B', '80', 'WalkBeam', '380', '1000','none' , true),
    ('1139', 'Slip', '5', '4', 'B', '81', 'WalkBeam', '380', '1000','none' , true),
    ('1140', 'Slip', '5', '4', 'B', '82', 'WalkBeam', '380', '1000','none' , true),
    ('1141', 'Slip', '5', '4', 'B', '83', 'WalkBeam', '380', '1000','none' , true),
    ('1142', 'Slip', '5', '4', 'B', '84', 'WalkBeam', '380', '1000','none' , true),
    ('1143', 'Slip', '5', '4', 'B', '85', 'WalkBeam', '380', '1000','none' , true),
    ('1144', 'Slip', '5', '4', 'B', '86', 'WalkBeam', '380', '1000','none' , true),
    ('1145', 'Slip', '5', '4', 'B', '87', 'WalkBeam', '380', '1000','none' , true),
    ('1146', 'Slip', '5', '4', 'B', '88', 'WalkBeam', '380', '1000','none' , true),
    ('1147', 'Slip', '5', '4', 'B', '89', 'WalkBeam', '380', '1000','none' , true),
    ('1148', 'Slip', '5', '4', 'B', '90', 'WalkBeam', '380', '1000','none' , true),
    ('1149', 'Slip', '5', '5', 'B', '91', 'WalkBeam', '420', '1000','none' , true),
    ('1150', 'Slip', '5', '5', 'B', '92', 'WalkBeam', '420', '1000','none' , true),
    ('1151', 'Slip', '5', '5', 'B', '93', 'WalkBeam', '420', '1200','none' , true),
    ('1152', 'Slip', '5', '5', 'B', '94', 'WalkBeam', '420', '1200','none' , true),
    ('1153', 'Slip', '5', '6', 'B', '95', 'WalkBeam', '440', '1200','none' , true),
    ('1154', 'Slip', '5', '6', 'B', '96', 'WalkBeam', '440', '1200','none' , true),
    ('1155', 'Slip', '5', '1', 'F', '1', 'Beam', '200', '550','none' , true),
    ('1156', 'Slip', '5', '1', 'F', '3', 'Beam', '229', '600','none' , true),
    ('1157', 'Slip', '5', '1', 'F', '5', 'Beam', '229', '600','none' , true),
    ('1158', 'Slip', '5', '1', 'F', '7', 'Beam', '229', '600','none' , true),
    ('1159', 'Slip', '5', '1', 'F', '9', 'Beam', '229', '600','none' , true),
    ('1160', 'Slip', '5', '1', 'F', '11', 'Beam', '229', '600','none' , true),
    ('1161', 'Slip', '5', '1', 'F', '13', 'Beam', '229', '600','none' , true),
    ('1162', 'Slip', '5', '1', 'F', '15', 'Beam', '229', '600','none' , true),
    ('1163', 'Slip', '5', '1', 'F', '17', 'Beam', '229', '600','none' , true),
    ('1164', 'Slip', '5', '1', 'F', '19', 'Beam', '229', '600','none' , true),
    ('1165', 'Slip', '5', '1', 'F', '21', 'Beam', '229', '600','none' , true),
    ('1166', 'Slip', '5', '1', 'F', '23', 'Beam', '229', '600','none' , true),
    ('1167', 'Slip', '5', '1', 'F', '25', 'Beam', '229', '600','none' , true),
    ('1168', 'Slip', '5', '1', 'F', '27', 'Beam', '229', '600','none' , true),
    ('1169', 'Slip', '5', '1', 'F', '29', 'Beam', '229', '600','none' , true),
    ('1170', 'Slip', '5', '1', 'F', '31', 'Beam', '229', '600','none' , true),
    ('1171', 'Slip', '5', '1', 'F', '33', 'Beam', '229', '600','none' , true),
    ('1172', 'Slip', '5', '1', 'F', '35', 'Beam', '229', '600','none' , true),
    ('1173', 'Slip', '5', '1', 'F', '37', 'Beam', '229', '600','none' , true),
    ('1174', 'Slip', '5', '1', 'F', '39', 'Beam', '229', '600','none' , true),
    ('1175', 'Slip', '5', '1', 'F', '41', 'Beam', '229', '600','none' , true),
    ('1176', 'Slip', '5', '1', 'F', '43', 'Beam', '229', '600','none' , true),
    ('1177', 'Slip', '5', '1', 'F', '45', 'Beam', '229', '600','none' , true),
    ('1178', 'Slip', '5', '1', 'F', '47', 'Beam', '229', '600','none' , true),
    ('1179', 'Slip', '5', '1', 'F', '49', 'Beam', '229', '600','none' , true),
    ('1180', 'Slip', '5', '1', 'F', '51', 'Beam', '229', '600','none' , true),
    ('1181', 'Slip', '5', '1', 'F', '53', 'Beam', '229', '600','none' , true),
    ('1182', 'Slip', '5', '1', 'F', '55', 'Beam', '229', '600','none' , true),
    ('1183', 'Slip', '5', '1', 'F', '57', 'Beam', '229', '600','none' , true),
    ('1184', 'Slip', '5', '1', 'F', '59', 'Beam', '229', '600','none' , true),
    ('1185', 'Slip', '5', '2', 'F', '61', 'Beam', '275', '600','none' , true),
    ('1186', 'Slip', '5', '2', 'F', '63', 'Beam', '275', '600','none' , true),
    ('1187', 'Slip', '5', '2', 'F', '65', 'Beam', '275', '600','none' , true),
    ('1188', 'Slip', '5', '2', 'F', '67', 'Beam', '275', '600','none' , true),
    ('1189', 'Slip', '5', '2', 'F', '69', 'Beam', '275', '600','none' , true),
    ('1190', 'Slip', '5', '2', 'F', '71', 'Beam', '275', '600','none' , true),
    ('1191', 'Slip', '5', '2', 'F', '73', 'Beam', '275', '600','none' , true),
    ('1192', 'Slip', '5', '2', 'F', '75', 'Beam', '275', '600','none' , true),
    ('1193', 'Slip', '5', '2', 'F', '77', 'Beam', '275', '600','none' , true),
    ('1194', 'Slip', '5', '2', 'F', '79', 'Beam', '275', '600','none' , true),
    ('1195', 'Slip', '5', '2', 'F', '80', 'Beam', '275', '600','none' , true),
    ('1196', 'Slip', '5', '2', 'F', '81', 'Beam', '275', '600','none' , true),
    ('1197', 'Slip', '5', '2', 'F', '82', 'Beam', '275', '600','none' , true),
    ('1198', 'Slip', '5', '2', 'F', '83', 'Beam', '275', '600','none' , true),
    ('1199', 'Slip', '5', '2', 'F', '84', 'Beam', '275', '600','none' , true),
    ('1200', 'Slip', '5', '2', 'F', '85', 'Beam', '275', '600','none' , true),
    ('1201', 'Slip', '5', '2', 'F', '86', 'Beam', '275', '600','none' , true),
    ('1202', 'Slip', '6', '4', 'A', '1', 'WalkBeam', '350', '900','none' , true),
    ('1203', 'Slip', '6', '2', 'A', '2', 'Beam', '260', '600','none' , true),
    ('1204', 'Slip', '6', '5', 'A', '3', 'WalkBeam', '420', '900','none' , true),
    ('1205', 'Slip', '6', '2', 'A', '4', 'Beam', '250', '600','none' , true),
    ('1206', 'Slip', '6', '6', 'A', '5', 'WalkBeam', '420', '1000','none' , true),
    ('1207', 'Slip', '6', '2', 'A', '6', 'Beam', '250', '600','none' , true),
    ('1208', 'Slip', '6', '5', 'A', '7', 'WalkBeam', '400', '1000','none' , true),
    ('1209', 'Slip', '6', '2', 'A', '8', 'Beam', '250', '600','none' , true),
    ('1210', 'Slip', '6', '5', 'A', '9', 'WalkBeam', '400', '1000','none' , true),
    ('1211', 'Slip', '6', '2', 'A', '10', 'Beam', '250', '600','none' , true),
    ('1212', 'Slip', '6', '5', 'A', '11', 'WalkBeam', '400', '1000','none' , true),
    ('1213', 'Slip', '6', '2', 'A', '12', 'Beam', '260', '600','none' , true),
    ('1214', 'Slip', '6', '5', 'A', '13', 'WalkBeam', '400', '1000','none' , true),
    ('1215', 'Slip', '6', '2', 'A', '14', 'Beam', '250', '600','none' , true),
    ('1216', 'Slip', '6', '5', 'A', '15', 'WalkBeam', '400', '1000','none' , true),
    ('1217', 'Slip', '6', '2', 'A', '16', 'Beam', '250', '600','none' , true),
    ('1218', 'Slip', '6', '5', 'A', '17', 'WalkBeam', '400', '1000','none' , true),
    ('1219', 'Slip', '6', '2', 'A', '18', 'Beam', '250', '600','none' , true),
    ('1220', 'Slip', '6', '5', 'A', '19', 'WalkBeam', '400', '1000','none' , true),
    ('1221', 'Slip', '6', '2', 'A', '20', 'Beam', '250', '600','none' , true),
    ('1222', 'Slip', '6', '5', 'A', '21', 'WalkBeam', '400', '1000','none' , true),
    ('1223', 'Slip', '6', '2', 'A', '22', 'Beam', '250', '600','none' , true),
    ('1224', 'Slip', '6', '5', 'A', '23', 'WalkBeam', '400', '1000','none' , true),
    ('1225', 'Slip', '6', '2', 'A', '24', 'Beam', '250', '600','none' , true),
    ('1226', 'Slip', '6', '5', 'A', '25', 'WalkBeam', '400', '1000','none' , true),
    ('1227', 'Slip', '6', '2', 'A', '26', 'Beam', '260', '600','none' , true),
    ('1228', 'Slip', '6', '5', 'A', '27', 'WalkBeam', '400', '1000','none' , true),
    ('1229', 'Slip', '6', '2', 'A', '28', 'Beam', '260', '600','none' , true),
    ('1230', 'Slip', '6', '5', 'A', '29', 'WalkBeam', '400', '1000','none' , true),
    ('1231', 'Slip', '6', '2', 'A', '30', 'Beam', '250', '600','none' , true),
    ('1232', 'Slip', '6', '5', 'A', '31', 'WalkBeam', '400', '1000','none' , true),
    ('1233', 'Slip', '6', '2', 'A', '32', 'Beam', '250', '600','none' , true),
    ('1234', 'Slip', '6', '5', 'A', '33', 'WalkBeam', '400', '1000','none' , true),
    ('1235', 'Slip', '6', '2', 'A', '34', 'Beam', '250', '600','none' , true),
    ('1236', 'Slip', '6', '5', 'A', '35', 'WalkBeam', '400', '1000','none' , true),
    ('1237', 'Slip', '6', '2', 'A', '36', 'Beam', '250', '600','none' , true),
    ('1238', 'Slip', '6', '5', 'A', '37', 'WalkBeam', '400', '1000','none' , true),
    ('1239', 'Slip', '6', '2', 'A', '38', 'Beam', '250', '600','none' , true),
    ('1240', 'Slip', '6', '5', 'A', '39', 'WalkBeam', '400', '1000','none' , true),
    ('1241', 'Slip', '6', '2', 'A', '40', 'Beam', '260', '600','none' , true),
    ('1242', 'Slip', '6', '4', 'A', '41', 'WalkBeam', '350', '1000','none' , true),
    ('1243', 'Slip', '6', '2', 'A', '42', 'Beam', '250', '600','none' , true),
    ('1244', 'Slip', '6', '4', 'A', '43', 'WalkBeam', '350', '1000','none' , true),
    ('1245', 'Slip', '6', '2', 'A', '44', 'Beam', '250', '600','none' , true),
    ('1246', 'Slip', '6', '4', 'A', '45', 'WalkBeam', '350', '1000','none' , true),
    ('1247', 'Slip', '6', '2', 'A', '46', 'Beam', '250', '600','none' , true),
    ('1248', 'Slip', '6', '4', 'A', '47', 'WalkBeam', '350', '1000','none' , true),
    ('1249', 'Slip', '6', '2', 'A', '48', 'Beam', '250', '600','none' , true),
    ('1250', 'Slip', '6', '3', 'A', '49', 'WalkBeam', '330', '1000','none' , true),
    ('1251', 'Slip', '6', '2', 'A', '50', 'Beam', '250', '600','none' , true),
    ('1252', 'Slip', '6', '3', 'A', '51', 'WalkBeam', '330', '1000','none' , true),
    ('1253', 'Slip', '6', '2', 'A', '52', 'Beam', '250', '600','none' , true),
    ('1254', 'Slip', '6', '4', 'A', '53', 'WalkBeam', '350', '1000','none' , true),
    ('1255', 'Slip', '6', '2', 'A', '54', 'Beam', '240', '600','none' , true),
    ('1256', 'Slip', '6', '4', 'A', '55', 'WalkBeam', '350', '1000','none' , true),
    ('1257', 'Slip', '6', '2', 'A', '56', 'Beam', '260', '600','none' , true),
    ('1258', 'Slip', '6', '4', 'A', '57', 'WalkBeam', '340', '1000','none' , true),
    ('1259', 'Slip', '6', '2', 'A', '58', 'Beam', '250', '600','none' , true),
    ('1260', 'Slip', '6', '4', 'A', '59', 'WalkBeam', '340', '1000','none' , true),
    ('1261', 'Slip', '6', '2', 'A', '60', 'Beam', '250', '600','none' , true),
    ('1262', 'Slip', '6', '4', 'A', '61', 'WalkBeam', '350', '1000','none' , true),
    ('1263', 'Slip', '6', '2', 'A', '62', 'Beam', '250', '600','none' , true),
    ('1264', 'Slip', '6', '3', 'A', '63', 'WalkBeam', '330', '1000','none' , true),
    ('1265', 'Slip', '6', '2', 'A', '64', 'Beam', '240', '600','none' , true),
    ('1266', 'Slip', '6', '4', 'A', '65', 'WalkBeam', '350', '1000','none' , true),
    ('1267', 'Slip', '6', '2', 'A', '66', 'Beam', '275', '600','none' , true),
    ('1268', 'Slip', '6', '4', 'A', '67', 'WalkBeam', '350', '1000','none' , true),
    ('1269', 'Slip', '6', '2', 'A', '68', 'Beam', '280', '600','none' , true),
    ('1270', 'Slip', '6', '4', 'A', '69', 'WalkBeam', '350', '1000','none' , true),
    ('1271', 'Slip', '6', '2', 'A', '70', 'Beam', '275', '600','none' , true),
    ('1272', 'Slip', '6', '4', 'A', '71', 'WalkBeam', '350', '900','none' , true),
    ('1273', 'Slip', '6', '2', 'A', '72', 'Beam', '275', '600','none' , true),
    ('1274', 'Slip', '6', '4', 'A', '73', 'WalkBeam', '350', '900','none' , true),
    ('1275', 'Slip', '6', '2', 'A', '74', 'Beam', '275', '600','none' , true),
    ('1276', 'Slip', '6', '4', 'A', '75', 'WalkBeam', '350', '900','none' , true),
    ('1277', 'Slip', '6', '2', 'A', '76', 'Beam', '280', '600','none' , true),
    ('1278', 'Slip', '6', '4', 'A', '77', 'WalkBeam', '350', '900','none' , true),
    ('1279', 'Slip', '6', '2', 'A', '78', 'Beam', '275', '600','none' , true),
    ('1280', 'Slip', '6', '3', 'A', '79', 'WalkBeam', '330', '900','none' , true),
    ('1281', 'Slip', '6', '2', 'A', '80', 'Beam', '275', '600','none' , true),
    ('1282', 'Slip', '6', '3', 'A', '81', 'WalkBeam', '330', '900','none' , true),
    ('1283', 'Slip', '6', '2', 'A', '82', 'Beam', '275', '600','none' , true),
    ('1284', 'Slip', '6', '3', 'A', '83', 'WalkBeam', '330', '900','none' , true),
    ('1285', 'Slip', '6', '2', 'A', '84', 'Beam', '275', '600','none' , true),
    ('1286', 'Slip', '6', '3', 'A', '85', 'WalkBeam', '330', '900','none' , true),
    ('1287', 'Slip', '6', '2', 'A', '86', 'Beam', '275', '600','none' , true),
    ('1288', 'Slip', '6', '3', 'A', '87', 'WalkBeam', '330', '900','none' , true),
    ('1289', 'Slip', '6', '2', 'A', '88', 'Beam', '275', '600','none' , true),
    ('1290', 'Slip', '6', '3', 'A', '89', 'WalkBeam', '330', '900','none' , true),
    ('1291', 'Slip', '6', '2', 'A', '90', 'Beam', '275', '600','none' , true),
    ('1292', 'Slip', '6', '3', 'A', '91', 'WalkBeam', '330', '900','none' , true),
    ('1293', 'Slip', '6', '2', 'A', '92', 'Beam', '275', '600','none' , true),
    ('1294', 'Slip', '6', '3', 'A', '93', 'WalkBeam', '330', '900','none' , true),
    ('1295', 'Slip', '6', '2', 'A', '94', 'Beam', '275', '600','none' , true),
    ('1296', 'Slip', '6', '3', 'A', '95', 'WalkBeam', '330', '900','none' , true),
    ('1297', 'Slip', '6', '2', 'A', '96', 'Beam', '275', '600','none' , true),
    ('1298', 'Slip', '6', '3', 'A', '97', 'WalkBeam', '330', '900','none' , true),
    ('1299', 'Slip', '6', '2', 'A', '98', 'Beam', '275', '600','none' , true),
    ('1300', 'Slip', '6', '3', 'A', '99', 'WalkBeam', '330', '900','none' , true),
    ('1301', 'Slip', '6', '2', 'A', '100', 'Beam', '275', '600','none' , true),
    ('1302', 'Slip', '6', '3', 'A', '101', 'WalkBeam', '330', '900','none' , true),
    ('1303', 'Slip', '6', '2', 'A', '102', 'Beam', '275', '600','none' , true),
    ('1304', 'Slip', '6', '3', 'A', '103', 'WalkBeam', '330', '900','none' , true),
    ('1305', 'Slip', '6', '2', 'A', '104', 'Beam', '275', '600','none' , true),
    ('1306', 'Slip', '6', '3', 'A', '105', 'WalkBeam', '330', '900','none' , true),
    ('1307', 'Slip', '6', '2', 'A', '106', 'Beam', '275', '600','none' , true),
    ('1308', 'Slip', '6', '2', 'A', '108', 'Beam', '275', '600','none' , true),
    ('1309', 'Slip', '6', '2', 'A', '110', 'Beam', '275', '600','none' , true),
    ('1310', 'Slip', '6', '2', 'A', '112', 'Beam', '275', '600','none' , true),
    ('1311', 'Slip', '6', '2', 'A', '114', 'Beam', '280', '600','none' , true),
    ('1312', 'Slip', '6', '2', 'A', '116', 'Beam', '275', '600','none' , true),
    ('1313', 'Slip', '6', '2', 'A', '118', 'Beam', '275', '600','none' , true),
    ('1314', 'Slip', '6', '2', 'A', '120', 'Beam', '280', '600','none' , true),
    ('1315', 'Slip', '6', '2', 'A', '122', 'Beam', '280', '600','none' , true),
    ('1316', 'Slip', '6', '2', 'A', '124', 'Beam', '240', '600','none' , true),
    ('1317', 'Slip', '6', '2', 'A', '126', 'Beam', '250', '600','none' , true),
    ('1318', 'Slip', '6', '2', 'A', '128', 'Beam', '250', '600','none' , true),
    ('1319', 'Slip', '6', '2', 'A', '130', 'Beam', '250', '600','none' , true),
    ('1320', 'Slip', '6', '2', 'A', '132', 'Beam', '250', '600','none' , true),
    ('1321', 'Slip', '6', '5', 'A', '133', 'WalkBeam', '400', '1000','none' , true),
    ('1322', 'Slip', '6', '2', 'A', '134', 'Beam', '250', '600','none' , true),
    ('1323', 'Slip', '6', '2', 'A', '136', 'Beam', '250', '600','none' , true),
    ('1324', 'Slip', '6', '2', 'A', '138', 'Beam', '275', '600','none' , true),
    ('1325', 'Slip', '6', '2', 'A', '140', 'Beam', '275', '600','none' , true),
    ('1326', 'Slip', '6', '2', 'A', '142', 'Beam', '280', '600','none' , true),
    ('1327', 'Slip', '6', '2', 'A', '144', 'Beam', '275', '600','none' , true),
    ('1328', 'Slip', '6', '2', 'A', '146', 'Beam', '275', '600','none' , true),
    ('1329', 'Slip', '6', '2', 'A', '148', 'Beam', '275', '600','none' , true),
    ('1330', 'Slip', '6', '4', 'A', '201', 'WalkBeam', '380', '900','none' , true),
    ('1331', 'Slip', '6', '3', 'A', '202', 'WalkBeam', '330', '900','none' , true),
    ('1332', 'Slip', '6', '4', 'A', '203', 'WalkBeam', '380', '900','none' , true),
    ('1333', 'Slip', '6', '3', 'A', '204', 'WalkBeam', '330', '900','none' , true),
    ('1334', 'Slip', '6', '4', 'A', '205', 'WalkBeam', '380', '900','none' , true),
    ('1335', 'Slip', '6', '3', 'A', '206', 'WalkBeam', '330', '900','none' , true),
    ('1336', 'Slip', '6', '4', 'A', '207', 'WalkBeam', '380', '900','none' , true),
    ('1337', 'Slip', '6', '3', 'A', '208', 'WalkBeam', '330', '900','none' , true),
    ('1338', 'Slip', '6', '4', 'A', '209', 'WalkBeam', '380', '900','none' , true),
    ('1339', 'Slip', '6', '3', 'A', '210', 'WalkBeam', '330', '900','none' , true),
    ('1340', 'Slip', '6', '4', 'A', '211', 'WalkBeam', '380', '900','none' , true),
    ('1341', 'Slip', '6', '3', 'A', '212', 'WalkBeam', '330', '900','none' , true),
    ('1342', 'Slip', '6', '4', 'A', '213', 'WalkBeam', '380', '900','none' , true),
    ('1343', 'Slip', '6', '3', 'A', '214', 'WalkBeam', '330', '900','none' , true),
    ('1344', 'Slip', '6', '4', 'A', '215', 'WalkBeam', '380', '900','none' , true),
    ('1345', 'Slip', '6', '3', 'A', '216', 'WalkBeam', '330', '900','none' , true),
    ('1346', 'Slip', '6', '4', 'A', '217', 'WalkBeam', '380', '900','none' , true),
    ('1347', 'Slip', '6', '3', 'A', '218', 'WalkBeam', '330', '900','none' , true),
    ('1348', 'Slip', '6', '3', 'A', '220', 'WalkBeam', '330', '900','none' , true),
    ('1349', 'Slip', '6', '3', 'A', '222', 'WalkBeam', '330', '900','none' , true),
    ('1350', 'Slip', '6', '3', 'A', '224', 'WalkBeam', '330', '900','none' , true),
    ('1351', 'Slip', '6', '3', 'A', '226', 'WalkBeam', '330', '900','none' , true),
    ('1352', 'Slip', '6', '3', 'A', '228', 'WalkBeam', '330', '900','none' , true),
    ('1353', 'Slip', '6', '3', 'A', '230', 'WalkBeam', '330', '900','none' , true),
    ('1354', 'Slip', '6', '3', 'A', '232', 'WalkBeam', '330', '900','none' , true),
    ('1355', 'Slip', '6', '3', 'A', '234', 'WalkBeam', '330', '900','none' , true),
    ('1356', 'Slip', '6', '3', 'A', '236', 'WalkBeam', '330', '900','none' , true),
    ('1357', 'Slip', '6', '4', 'A', '238', 'WalkBeam', '380', '900','none' , true),
    ('1358', 'Slip', '6', '4', 'A', '240', 'WalkBeam', '380', '900','none' , true),
    ('1359', 'Slip', '6', '4', 'A', '242', 'WalkBeam', '380', '900','none' , true),
    ('1360', 'Slip', '6', '4', 'A', '244', 'WalkBeam', '380', '900','none' , true),
    ('1361', 'Slip', '6', '4', 'A', '246', 'WalkBeam', '350', '900','none' , true),
    ('1362', 'Slip', '6', '4', 'A', '248', 'WalkBeam', '350', '900','none' , true),
    ('1363', 'Slip', '6', '4', 'A', '250', 'WalkBeam', '350', '900','none' , true),
    ('1364', 'Slip', '6', '4', 'A', '252', 'WalkBeam', '350', '900','none' , true),
    ('1365', 'Slip', '6', '4', 'A', '254', 'WalkBeam', '350', '900','none' , true),
    ('1366', 'Slip', '6', '4', 'A', '256', 'WalkBeam', '350', '900','none' , true),
    ('1367', 'Slip', '6', '4', 'A', '258', 'WalkBeam', '350', '900','none' , true),
    ('1368', 'Slip', '6', '4', 'A', '260', 'WalkBeam', '350', '900','none' , true),
    ('1369', 'Slip', '6', '4', 'A', '262', 'WalkBeam', '350', '900','none' , true),
    ('1370', 'Slip', '6', '4', 'A', '264', 'WalkBeam', '350', '900','none' , true),
    ('1371', 'Slip', '6', '4', 'A', '266', 'WalkBeam', '350', '900','none' , true),
    ('1372', 'Slip', '6', '4', 'A', '268', 'WalkBeam', '350', '900','none' , true),
    ('1373', 'Slip', '6', '4', 'A', '270', 'WalkBeam', '350', '900','none' , true),
    ('1374', 'Slip', '6', '4', 'A', '272', 'WalkBeam', '350', '1000','none' , true),
    ('1375', 'Slip', '6', '4', 'A', '274', 'WalkBeam', '370', '1000','none' , true),
    ('1376', 'Slip', '6', '4', 'A', '276', 'WalkBeam', '370', '1000','none' , true),
    ('1377', 'Slip', '6', '5', 'A', '278', 'WalkBeam', '400', '1000','none' , true),
    ('1378', 'Slip', '6', '5', 'A', '280', 'WalkBeam', '400', '1000','none' , true),
    ('1379', 'Slip', '6', '5', 'A', '282', 'WalkBeam', '400', '1000','none' , true),
    ('1380', 'Slip', '6', '5', 'A', '284', 'WalkBeam', '400', '1000','none' , true),
    ('1381', 'Slip', '6', '5', 'A', '286', 'WalkBeam', '400', '1000','none' , true),
    ('1382', 'Slip', '6', '5', 'A', '288', 'WalkBeam', '400', '1000','none' , true),
    ('1383', 'Slip', '6', '5', 'A', '290', 'WalkBeam', '400', '1000','none' , true),
    ('1384', 'Slip', '6', '5', 'A', '292', 'WalkBeam', '400', '1000','none' , true),
    ('1385', 'Slip', '6', '5', 'A', '294', 'WalkBeam', '400', '1000','none' , true),
    ('1386', 'Slip', '6', '5', 'A', '296', 'WalkBeam', '400', '1000','none' , true),
    ('1387', 'Slip', '6', '5', 'A', '298', 'WalkBeam', '400', '1000','none' , true),
    ('1388', 'Slip', '6', '5', 'A', '300', 'WalkBeam', '400', '1000','none' , true),
    ('1389', 'Slip', '6', '5', 'A', '302', 'WalkBeam', '400', '1000','none' , true),
    ('1390', 'Slip', '6', '5', 'A', '304', 'WalkBeam', '400', '1000','none' , true),
    ('1391', 'Slip', '6', '5', 'A', '306', 'WalkBeam', '400', '1000','none' , true),
    ('1392', 'Slip', '6', '5', 'A', '308', 'WalkBeam', '400', '1000','none' , true),
    ('1393', 'Slip', '6', '5', 'A', '310', 'WalkBeam', '400', '1000','none' , true),
    ('1394', 'Slip', '6', '5', 'A', '312', 'WalkBeam', '400', '1000','none' , true),
    ('1395', 'Slip', '6', '6', 'A', '314', 'WalkBeam', '500', '1000','none' , true),
    ('1396', 'Slip', '6', '6', 'A', '316', 'WalkBeam', '500', '1200','none' , true),
    ('1397', 'Slip', '6', '6', 'A', '318', 'WalkBeam', '500', '1200','none' , true),
    ('1398', 'Slip', '6', '6', 'A', '320', 'WalkBeam', '500', '1200','none' , true),
    ('1399', 'Slip', '6', '6', 'A', '322', 'WalkBeam', '500', '1200','none' , true),
    ('1400', 'Slip', '6', '2', 'B', '2', 'Beam', '275', '600','none' , true),
    ('1401', 'Slip', '6', '4', 'B', '3', 'Beam', '275', '600','none' , true),
    ('1402', 'Slip', '6', '2', 'B', '4', 'Beam', '275', '600','none' , true),
    ('1403', 'Slip', '6', '2', 'B', '5', 'Beam', '275', '600','none' , true),
    ('1404', 'Slip', '6', '2', 'B', '6', 'Beam', '275', '600','none' , true),
    ('1405', 'Slip', '6', '2', 'B', '7', 'Beam', '275', '600','none' , true),
    ('1406', 'Slip', '6', '2', 'B', '8', 'Beam', '275', '600','none' , true),
    ('1407', 'Slip', '6', '2', 'B', '9', 'Beam', '275', '600','none' , true),
    ('1408', 'Slip', '6', '2', 'B', '10', 'Beam', '275', '600','none' , true),
    ('1409', 'Slip', '6', '2', 'B', '11', 'Beam', '275', '600','none' , true),
    ('1410', 'Slip', '6', '1', 'B', '12', 'Beam', '275', '600','none' , true),
    ('1411', 'Slip', '6', '2', 'B', '13', 'Beam', '275', '600','none' , true),
    ('1412', 'Slip', '6', '2', 'B', '14', 'Beam', '275', '600','none' , true),
    ('1413', 'Slip', '6', '2', 'B', '15', 'Beam', '275', '600','none' , true),
    ('1414', 'Slip', '6', '2', 'B', '16', 'Beam', '275', '600','none' , true),
    ('1415', 'Slip', '6', '2', 'B', '17', 'Beam', '275', '600','none' , true),
    ('1416', 'Slip', '6', '2', 'B', '18', 'Beam', '275', '600','none' , true),
    ('1417', 'Slip', '6', '2', 'B', '19', 'Beam', '275', '600','none' , true),
    ('1418', 'Slip', '6', '2', 'B', '20', 'Beam', '275', '600','none' , true),
    ('1419', 'Slip', '6', '2', 'B', '21', 'Beam', '275', '600','none' , true),
    ('1420', 'Slip', '6', '2', 'B', '22', 'Beam', '275', '600','none' , true),
    ('1421', 'Slip', '6', '2', 'B', '23', 'Beam', '275', '600','none' , true),
    ('1422', 'Slip', '6', '2', 'B', '24', 'Beam', '275', '600','none' , true),
    ('1423', 'Slip', '6', '2', 'B', '25', 'Beam', '275', '600','none' , true),
    ('1424', 'Slip', '6', '2', 'B', '26', 'Beam', '275', '600','none' , true),
    ('1425', 'Slip', '6', '2', 'B', '27', 'Beam', '275', '600','none' , true),
    ('1426', 'Slip', '6', '2', 'B', '28', 'Beam', '275', '600','none' , true),
    ('1427', 'Slip', '6', '2', 'B', '29', 'Beam', '275', '600','none' , true),
    ('1428', 'Slip', '6', '2', 'B', '30', 'Beam', '275', '600','none' , true),
    ('1429', 'Slip', '6', '2', 'B', '31', 'Beam', '275', '600','none' , true),
    ('1430', 'Slip', '6', '2', 'B', '32', 'Beam', '275', '600','none' , true),
    ('1431', 'Slip', '6', '2', 'B', '33', 'Beam', '275', '600','none' , true),
    ('1432', 'Slip', '6', '2', 'B', '34', 'Beam', '275', '600','none' , true),
    ('1433', 'Slip', '6', '2', 'B', '35', 'Beam', '275', '600','none' , true),
    ('1434', 'Slip', '6', '2', 'B', '36', 'Beam', '275', '600','none' , true),
    ('1435', 'Slip', '6', '2', 'B', '37', 'Beam', '275', '600','none' , true),
    ('1436', 'Slip', '6', '2', 'B', '38', 'Beam', '275', '600','none' , true),
    ('1437', 'Slip', '6', '2', 'B', '39', 'Beam', '275', '600','none' , true),
    ('1438', 'Slip', '6', '2', 'B', '40', 'Beam', '275', '600','none' , true),
    ('1439', 'Slip', '6', '2', 'B', '41', 'Beam', '275', '600','none' , true),
    ('1440', 'Slip', '6', '2', 'B', '42', 'Beam', '275', '600','none' , true),
    ('1441', 'Slip', '6', '2', 'B', '43', 'Beam', '275', '600','none' , true),
    ('1442', 'Slip', '6', '2', 'B', '44', 'Beam', '275', '600','none' , true),
    ('1443', 'Slip', '6', '2', 'B', '45', 'Beam', '275', '600','none' , true),
    ('1444', 'Slip', '6', '2', 'B', '46', 'Beam', '275', '600','none' , true),
    ('1445', 'Slip', '6', '2', 'B', '47', 'Beam', '275', '600','none' , true),
    ('1446', 'Slip', '6', '2', 'B', '48', 'Beam', '275', '600','none' , true),
    ('1447', 'Slip', '6', '2', 'B', '49', 'Beam', '275', '600','none' , true),
    ('1448', 'Slip', '6', '2', 'B', '50', 'Beam', '275', '600','none' , true),
    ('1449', 'Slip', '6', '2', 'B', '51', 'Beam', '275', '600','none' , true),
    ('1450', 'Slip', '6', '2', 'B', '52', 'Beam', '275', '600','none' , true),
    ('1451', 'Slip', '6', '2', 'B', '53', 'Beam', '275', '600','none' , true),
    ('1452', 'Slip', '6', '2', 'B', '54', 'Beam', '275', '600','none' , true),
    ('1453', 'Slip', '6', '2', 'B', '55', 'Beam', '275', '600','none' , true),
    ('1454', 'Slip', '6', '2', 'B', '56', 'Beam', '275', '600','none' , true),
    ('1455', 'Slip', '6', '2', 'B', '57', 'Beam', '275', '600','none' , true),
    ('1456', 'Slip', '6', '2', 'B', '58', 'Beam', '275', '600','none' , true),
    ('1457', 'Slip', '6', '2', 'B', '59', 'Beam', '275', '600','none' , true),
    ('1458', 'Slip', '6', '2', 'B', '60', 'Beam', '275', '600','none' , true),
    ('1459', 'Slip', '6', '2', 'B', '61', 'Beam', '275', '600','none' , true),
    ('1460', 'Slip', '6', '2', 'B', '62', 'Beam', '275', '600','none' , true),
    ('1461', 'Slip', '6', '2', 'B', '63', 'Beam', '275', '600','none' , true),
    ('1462', 'Slip', '6', '2', 'B', '64', 'Beam', '275', '600','none' , true),
    ('1463', 'Slip', '6', '2', 'B', '65', 'Beam', '275', '600','none' , true),
    ('1464', 'Slip', '6', '2', 'B', '66', 'Beam', '275', '600','none' , true),
    ('1465', 'Slip', '6', '2', 'B', '67', 'Beam', '275', '600','none' , true),
    ('1466', 'Slip', '6', '2', 'B', '68', 'Beam', '275', '600','none' , true),
    ('1467', 'Slip', '6', '2', 'B', '69', 'Beam', '275', '600','none' , true),
    ('1468', 'Slip', '6', '2', 'B', '70', 'Beam', '275', '600','none' , true),
    ('1469', 'Slip', '6', '2', 'B', '71', 'Beam', '275', '600','none' , true),
    ('1470', 'Slip', '6', '2', 'B', '72', 'Beam', '275', '600','none' , true),
    ('1471', 'Slip', '6', '2', 'B', '73', 'Beam', '275', '600','none' , true),
    ('1472', 'Slip', '6', '2', 'B', '74', 'Beam', '275', '600','none' , true),
    ('1473', 'Slip', '6', '3', 'B', '75', 'Beam', '300', '600','none' , true),
    ('1474', 'Slip', '6', '3', 'B', '76', 'Beam', '300', '600','none' , true),
    ('1475', 'Slip', '6', '3', 'B', '77', 'Beam', '300', '600','none' , true),
    ('1476', 'Slip', '6', '3', 'B', '78', 'Beam', '300', '600','none' , true),
    ('1477', 'Slip', '6', '3', 'B', '79', 'Beam', '300', '600','none' , true),
    ('1478', 'Slip', '6', '3', 'B', '80', 'Beam', '300', '600','none' , true),
    ('1479', 'Slip', '6', '3', 'B', '81', 'Beam', '300', '600','none' , true),
    ('1480', 'Slip', '6', '3', 'B', '82', 'Beam', '300', '600','none' , true),
    ('1481', 'Slip', '6', '3', 'B', '83', 'Beam', '300', '600','none' , true),
    ('1482', 'Slip', '6', '3', 'B', '84', 'Beam', '300', '600','none' , true),
    ('1483', 'Slip', '6', '3', 'B', '85', 'Beam', '300', '600','none' , true),
    ('1484', 'Slip', '6', '3', 'B', '86', 'Beam', '300', '600','none' , true),
    ('1485', 'Winter', '6', '3', 'B', '87', 'None', '300', '600','none' , true),
    ('1486', 'Winter', '6', '3', 'B', '88', 'None', '300', '600','none' , true),
    ('1487', 'Winter', '6', '3', 'B', '89', 'None', '300', '600','none' , true),
    ('1488', 'Winter', '6', '3', 'B', '90', 'None', '300', '600','none' , true),
    ('1489', 'Winter', '6', '3', 'B', '91', 'None', '300', '600','none' , true),
    ('1490', 'Winter', '6', '3', 'B', '92', 'None', '300', '600','none' , true),
    ('1491', 'Slip', '6', '3', 'B', '93', 'Beam', '300', '600','none' , true),
    ('1492', 'Slip', '6', '3', 'B', '94', 'Beam', '300', '600','none' , true),
    ('1493', 'Slip', '6', '3', 'B', '95', 'Beam', '300', '600','none' , true),
    ('1494', 'Slip', '6', '3', 'B', '96', 'Beam', '300', '600','none' , true),
    ('1495', 'Slip', '6', '3', 'B', '97', 'Beam', '300', '600','none' , true),
    ('1496', 'Slip', '6', '3', 'B', '98', 'Beam', '300', '600','none' , true),
    ('1497', 'Slip', '6', '3', 'B', '99', 'Beam', '300', '600','none' , true),
    ('1498', 'Slip', '6', '3', 'B', '100', 'Beam', '300', '600','none' , true),
    ('1499', 'Slip', '6', '3', 'B', '101', 'Beam', '300', '600','none' , true),
    ('1500', 'Slip', '6', '3', 'B', '102', 'Beam', '300', '600','none' , true),
    ('1501', 'Slip', '6', '3', 'B', '103', 'Beam', '300', '600','none' , true),
    ('1502', 'Slip', '6', '3', 'B', '104', 'Beam', '300', '600','none' , true),
    ('1503', 'Slip', '6', '3', 'B', '105', 'Beam', '300', '600','none' , true),
    ('1504', 'Slip', '6', '3', 'B', '106', 'Beam', '300', '600','none' , true),
    ('1505', 'Slip', '6', '3', 'B', '107', 'Beam', '300', '600','none' , true),
    ('1506', 'Slip', '6', '3', 'B', '108', 'Beam', '300', '600','none' , true),
    ('1507', 'Slip', '6', '3', 'B', '109', 'Beam', '300', '600','none' , true),
    ('1508', 'Slip', '6', '3', 'B', '110', 'Beam', '300', '600','none' , true),
    ('1509', 'Slip', '6', '3', 'B', '111', 'Beam', '300', '600','none' , true),
    ('1510', 'Slip', '6', '3', 'B', '112', 'Beam', '300', '600','none' , true),
    ('1511', 'Slip', '6', '3', 'B', '113', 'Beam', '300', '600','none' , true),
    ('1512', 'Slip', '6', '3', 'B', '114', 'Beam', '300', '600','none' , true),
    ('1513', 'Slip', '6', '3', 'B', '115', 'Beam', '300', '600','none' , true),
    ('1514', 'Slip', '6', '3', 'B', '116', 'Beam', '300', '600','none' , true),
    ('1515', 'Slip', '6', '3', 'B', '117', 'Beam', '300', '600','none' , true),
    ('1516', 'Slip', '6', '3', 'B', '118', 'Beam', '300', '600','none' , true),
    ('1517', 'Slip', '6', '3', 'B', '119', 'Beam', '300', '600','none' , true),
    ('1518', 'Slip', '6', '3', 'B', '120', 'Beam', '300', '600','none' , true),
    ('1519', 'Slip', '6', '3', 'B', '121', 'Beam', '300', '600','none' , true),
    ('1520', 'Slip', '6', '3', 'B', '122', 'Beam', '300', '600','none' , true),
    ('1521', 'Slip', '6', '3', 'B', '123', 'Beam', '300', '600','none' , true),
    ('1522', 'Slip', '6', '3', 'B', '124', 'Beam', '300', '600','none' , true),
    ('1523', 'Slip', '6', '4', 'B', '125', 'Beam', '335', '700','none' , true),
    ('1524', 'Slip', '6', '4', 'B', '126', 'Beam', '335', '700','none' , true),
    ('1525', 'Slip', '6', '4', 'B', '127', 'Beam', '335', '700','none' , true),
    ('1526', 'Slip', '6', '4', 'B', '128', 'Beam', '335', '700','none' , true),
    ('1527', 'Slip', '6', '4', 'B', '129', 'Beam', '335', '700','none' , true),
    ('1528', 'Slip', '6', '4', 'B', '130', 'Beam', '335', '700','none' , true),
    ('1529', 'Slip', '6', '4', 'B', '131', 'Beam', '335', '700','none' , true),
    ('1530', 'Slip', '6', '4', 'B', '132', 'Beam', '335', '700','none' , true),
    ('1531', 'Slip', '6', '4', 'B', '133', 'Beam', '335', '700','none' , true),
    ('1532', 'Slip', '6', '4', 'B', '134', 'Beam', '335', '700','none' , true),
    ('1533', 'Slip', '6', '4', 'B', '135', 'Beam', '335', '700','none' , true),
    ('1534', 'Slip', '6', '2', 'C', '1', 'Beam', '275', '600','none' , true),
    ('1535', 'Slip', '6', '2', 'C', '2', 'Beam', '275', '600','none' , true),
    ('1536', 'Slip', '6', '2', 'C', '3', 'Beam', '275', '600','none' , true),
    ('1537', 'Slip', '6', '2', 'C', '4', 'Beam', '275', '600','none' , true),
    ('1538', 'Slip', '6', '2', 'C', '5', 'Beam', '275', '600','none' , true),
    ('1539', 'Slip', '6', '2', 'C', '6', 'Beam', '275', '600','none' , true),
    ('1540', 'Slip', '6', '2', 'C', '7', 'Beam', '275', '600','none' , true),
    ('1541', 'Slip', '6', '2', 'C', '8', 'Beam', '275', '600','none' , true),
    ('1542', 'Slip', '6', '2', 'C', '9', 'Beam', '275', '600','none' , true),
    ('1543', 'Slip', '6', '2', 'C', '10', 'Beam', '275', '600','none' , true),
    ('1544', 'Slip', '6', '2', 'C', '11', 'Beam', '275', '600','none' , true),
    ('1545', 'Slip', '6', '2', 'C', '12', 'Beam', '275', '600','none' , true),
    ('1546', 'Slip', '6', '2', 'C', '13', 'Beam', '275', '600','none' , true),
    ('1547', 'Slip', '6', '2', 'C', '14', 'Beam', '275', '600','none' , true),
    ('1548', 'Slip', '6', '2', 'C', '15', 'Beam', '275', '600','none' , true),
    ('1549', 'Slip', '6', '2', 'C', '16', 'Beam', '275', '600','none' , true),
    ('1550', 'Slip', '6', '2', 'C', '17', 'Beam', '275', '600','none' , true),
    ('1551', 'Slip', '6', '2', 'C', '18', 'Beam', '275', '600','none' , true),
    ('1552', 'Slip', '6', '2', 'C', '19', 'Beam', '275', '600','none' , true),
    ('1553', 'Slip', '6', '2', 'C', '20', 'Beam', '275', '600','none' , true),
    ('1554', 'Slip', '6', '2', 'C', '21', 'Beam', '275', '600','none' , true),
    ('1555', 'Slip', '6', '2', 'C', '22', 'Beam', '275', '600','none' , true),
    ('1556', 'Slip', '6', '2', 'C', '23', 'Beam', '275', '600','none' , true),
    ('1557', 'Slip', '6', '2', 'C', '24', 'Beam', '275', '600','none' , true),
    ('1558', 'Slip', '6', '2', 'C', '25', 'Beam', '275', '600','none' , true),
    ('1559', 'Slip', '6', '2', 'C', '26', 'Beam', '275', '600','none' , true),
    ('1560', 'Slip', '6', '2', 'C', '27', 'Beam', '275', '600','none' , true),
    ('1561', 'Slip', '6', '2', 'C', '28', 'Beam', '275', '600','none' , true),
    ('1562', 'Slip', '6', '2', 'C', '29', 'Beam', '275', '600','none' , true),
    ('1563', 'Slip', '6', '2', 'C', '30', 'Beam', '275', '600','none' , true),
    ('1564', 'Slip', '6', '2', 'C', '31', 'Beam', '275', '600','none' , true),
    ('1565', 'Slip', '6', '2', 'C', '32', 'Beam', '275', '600','none' , true),
    ('1566', 'Slip', '6', '2', 'C', '33', 'Beam', '275', '600','none' , true),
    ('1567', 'Slip', '6', '2', 'C', '34', 'Beam', '275', '600','none' , true),
    ('1568', 'Slip', '6', '2', 'C', '35', 'Beam', '275', '600','none' , true),
    ('1569', 'Slip', '6', '2', 'C', '36', 'Beam', '275', '600','none' , true),
    ('1570', 'Slip', '6', '2', 'C', '37', 'Beam', '275', '600','none' , true),
    ('1571', 'Slip', '6', '2', 'C', '38', 'Beam', '275', '600','none' , true),
    ('1572', 'Slip', '6', '2', 'C', '39', 'Beam', '275', '600','none' , true),
    ('1573', 'Slip', '6', '2', 'C', '40', 'Beam', '275', '600','none' , true),
    ('1574', 'Slip', '6', '2', 'C', '41', 'Beam', '275', '600','none' , true),
    ('1575', 'Slip', '6', '2', 'C', '42', 'Beam', '275', '600','none' , true),
    ('1576', 'Slip', '6', '2', 'C', '43', 'Beam', '275', '600','none' , true),
    ('1577', 'Slip', '6', '2', 'C', '44', 'Beam', '275', '600','none' , true),
    ('1578', 'Slip', '6', '2', 'C', '45', 'Beam', '275', '600','none' , true),
    ('1579', 'Slip', '6', '2', 'C', '46', 'Beam', '275', '600','none' , true),
    ('1580', 'Slip', '6', '2', 'C', '47', 'Beam', '275', '600','none' , true),
    ('1581', 'Slip', '6', '2', 'C', '48', 'Beam', '275', '600','none' , true),
    ('1582', 'Slip', '6', '2', 'C', '49', 'Beam', '275', '600','none' , true),
    ('1583', 'Slip', '6', '2', 'C', '50', 'Beam', '275', '600','none' , true),
    ('1584', 'Slip', '6', '2', 'C', '51', 'Beam', '275', '600','none' , true),
    ('1585', 'Slip', '6', '2', 'C', '52', 'Beam', '275', '600','none' , true),
    ('1586', 'Slip', '6', '2', 'C', '53', 'Beam', '275', '600','none' , true),
    ('1587', 'Slip', '6', '2', 'C', '54', 'Beam', '275', '600','none' , true),
    ('1588', 'Slip', '6', '2', 'C', '55', 'Beam', '275', '600','none' , true),
    ('1589', 'Slip', '6', '2', 'C', '56', 'Beam', '275', '600','none' , true),
    ('1590', 'Slip', '6', '2', 'C', '57', 'Beam', '275', '600','none' , true),
    ('1591', 'Slip', '6', '2', 'C', '58', 'Beam', '275', '600','none' , true),
    ('1592', 'Slip', '6', '2', 'C', '59', 'Beam', '275', '600','none' , true),
    ('1593', 'Slip', '6', '2', 'C', '60', 'Beam', '275', '600','none' , true),
    ('1594', 'Slip', '6', '2', 'C', '61', 'Beam', '275', '600','none' , true),
    ('1595', 'Slip', '6', '2', 'C', '62', 'Beam', '275', '600','none' , true),
    ('1596', 'Slip', '6', '2', 'C', '63', 'Beam', '275', '600','none' , true),
    ('1597', 'Slip', '6', '2', 'C', '64', 'Beam', '275', '600','none' , true),
    ('1598', 'Slip', '6', '2', 'C', '65', 'Beam', '275', '600','none' , true),
    ('1599', 'Slip', '6', '2', 'C', '66', 'Beam', '275', '600','none' , true),
    ('1600', 'Slip', '6', '2', 'C', '67', 'Beam', '275', '600','none' , true),
    ('1601', 'Slip', '6', '2', 'C', '68', 'Beam', '275', '600','none' , true),
    ('1602', 'Slip', '6', '2', 'C', '69', 'Beam', '275', '600','none' , true),
    ('1603', 'Slip', '6', '2', 'C', '70', 'Beam', '275', '600','none' , true),
    ('1604', 'Slip', '6', '2', 'C', '71', 'Beam', '275', '600','none' , true),
    ('1605', 'Slip', '6', '2', 'C', '72', 'Beam', '275', '600','none' , true),
    ('1606', 'Slip', '6', '2', 'C', '73', 'Beam', '275', '600','none' , true),
    ('1607', 'Slip', '6', '2', 'C', '74', 'Beam', '275', '600','none' , true),
    ('1608', 'Slip', '6', '2', 'C', '75', 'Beam', '275', '600','none' , true),
    ('1609', 'Slip', '6', '2', 'C', '76', 'Beam', '275', '600','none' , true),
    ('1610', 'Slip', '6', '2', 'C', '77', 'Beam', '275', '600','none' , true),
    ('1611', 'Slip', '6', '2', 'C', '78', 'Beam', '275', '600','none' , true),
    ('1612', 'Slip', '6', '2', 'C', '79', 'Beam', '275', '600','none' , true),
    ('1613', 'Slip', '6', '2', 'C', '80', 'Beam', '275', '600','none' , true),
    ('1614', 'Slip', '6', '2', 'C', '81', 'Beam', '275', '600','none' , true),
    ('1615', 'Slip', '6', '2', 'C', '82', 'Beam', '275', '600','none' , true),
    ('1616', 'Slip', '6', '2', 'C', '83', 'Beam', '275', '600','none' , true),
    ('1617', 'Slip', '6', '2', 'C', '84', 'Beam', '275', '600','none' , true),
    ('1618', 'Slip', '6', '2', 'C', '85', 'Beam', '275', '600','none' , true),
    ('1619', 'Slip', '6', '2', 'C', '86', 'Beam', '275', '600','none' , true),
    ('1620', 'Slip', '6', '2', 'C', '87', 'Beam', '275', '550','none' , true),
    ('1621', 'Slip', '6', '2', 'C', '88', 'Beam', '275', '600','none' , true),
    ('1622', 'Slip', '6', '2', 'C', '89', 'Beam', '275', '550','none' , true),
    ('1623', 'Slip', '6', '2', 'C', '90', 'Beam', '275', '550','none' , true),
    ('1624', 'Slip', '6', '3', 'C', '91', 'Beam', '300', '550','none' , true),
    ('1625', 'Slip', '6', '3', 'C', '92', 'Beam', '300', '550','none' , true),
    ('1626', 'Slip', '6', '3', 'C', '93', 'Beam', '300', '550','none' , true),
    ('1627', 'Slip', '6', '3', 'C', '94', 'Beam', '300', '550','none' , true),
    ('1628', 'Slip', '6', '3', 'C', '95', 'Beam', '300', '550','none' , true),
    ('1629', 'Slip', '6', '3', 'C', '96', 'Beam', '300', '550','none' , true),
    ('1630', 'Slip', '6', '3', 'C', '97', 'Beam', '300', '550','none' , true),
    ('1631', 'Slip', '6', '3', 'C', '98', 'Beam', '300', '550','none' , true),
    ('1632', 'Slip', '6', '3', 'C', '99', 'Beam', '300', '550','none' , true),
    ('1633', 'Slip', '6', '3', 'C', '100', 'Beam', '300', '550','none' , true),
    ('1634', 'Slip', '6', '3', 'C', '101', 'Beam', '300', '550','none' , true),
    ('1635', 'Slip', '6', '3', 'C', '102', 'Beam', '300', '550','none' , true),
    ('1636', 'Slip', '6', '3', 'C', '103', 'Beam', '300', '550','none' , true),
    ('1637', 'Slip', '6', '3', 'C', '104', 'Beam', '300', '550','none' , true),
    ('1638', 'Slip', '6', '3', 'C', '105', 'Beam', '300', '550','none' , true),
    ('1639', 'Slip', '6', '3', 'C', '106', 'Beam', '300', '550','none' , true),
    ('1640', 'Slip', '6', '3', 'C', '107', 'Beam', '300', '550','none' , true),
    ('1641', 'Slip', '6', '3', 'C', '108', 'Beam', '300', '550','none' , true),
    ('1642', 'Slip', '6', '3', 'C', '109', 'Beam', '300', '550','none' , true),
    ('1643', 'Slip', '6', '3', 'C', '110', 'Beam', '300', '550','none' , true),
    ('1644', 'Slip', '6', '3', 'C', '111', 'Beam', '300', '550','none' , true),
    ('1645', 'Slip', '6', '3', 'C', '112', 'Beam', '300', '550','none' , true),
    ('1646', 'Slip', '6', '3', 'C', '113', 'Beam', '300', '550','none' , true),
    ('1647', 'Slip', '6', '3', 'C', '114', 'Beam', '300', '550','none' , true),
    ('1648', 'Slip', '6', '3', 'C', '115', 'Beam', '300', '550','none' , true),
    ('1649', 'Slip', '6', '3', 'C', '116', 'Beam', '300', '550','none' , true),
    ('1650', 'Slip', '6', '3', 'C', '117', 'Beam', '300', '550','none' , true),
    ('1651', 'Slip', '6', '3', 'C', '118', 'Beam', '300', '550','none' , true),
    ('1652', 'Slip', '6', '3', 'C', '119', 'Beam', '300', '550','none' , true),
    ('1653', 'Slip', '6', '3', 'C', '120', 'Beam', '300', '550','none' , true),
    ('1654', 'Slip', '6', '3', 'C', '121', 'Beam', '300', '550','none' , true),
    ('1655', 'Slip', '6', '3', 'C', '122', 'Beam', '300', '550','none' , true),
    ('1656', 'Slip', '6', '3', 'C', '123', 'Beam', '300', '550','none' , true),
    ('1657', 'Slip', '6', '3', 'C', '124', 'Beam', '300', '550','none' , true),
    ('1658', 'Slip', '6', '3', 'C', '125', 'Beam', '300', '550','none' , true),
    ('1659', 'Slip', '6', '3', 'C', '126', 'Beam', '300', '550','none' , true),
    ('1660', 'Slip', '6', '3', 'C', '127', 'Beam', '300', '550','none' , true),
    ('1661', 'Slip', '6', '3', 'C', '128', 'Beam', '300', '550','none' , true),
    ('1662', 'Slip', '6', '3', 'C', '129', 'Beam', '300', '550','none' , true),
    ('1663', 'Slip', '6', '3', 'C', '130', 'Beam', '300', '550','none' , true),
    ('1664', 'Slip', '6', '3', 'C', '131', 'Beam', '300', '550','none' , true),
    ('1665', 'Slip', '6', '3', 'C', '132', 'Beam', '300', '550','none' , true),
    ('1666', 'Slip', '6', '3', 'C', '133', 'Beam', '330', '700','none' , true),
    ('1667', 'Slip', '6', '3', 'C', '134', 'Beam', '330', '700','none' , true),
    ('1668', 'Slip', '6', '3', 'C', '135', 'Beam', '330', '700','none' , true),
    ('1669', 'Slip', '6', '3', 'C', '136', 'Beam', '330', '700','none' , true),
    ('1670', 'Slip', '6', '1', 'D', '1', 'Beam', '229', '650','none' , true),
    ('1671', 'Slip', '6', '1', 'D', '2', 'Beam', '229', '650','none' , true),
    ('1672', 'Slip', '6', '3', 'D', '3', 'Beam', '300', '650','none' , true),
    ('1673', 'Slip', '6', '3', 'D', '4', 'Beam', '300', '650','none' , true),
    ('1674', 'Slip', '6', '3', 'D', '5', 'Beam', '300', '650','none' , true),
    ('1675', 'Slip', '6', '3', 'D', '6', 'Beam', '300', '650','none' , true),
    ('1676', 'Slip', '6', '3', 'D', '7', 'Beam', '300', '650','none' , true),
    ('1677', 'Slip', '6', '3', 'D', '8', 'Beam', '300', '650','none' , true),
    ('1678', 'Slip', '6', '3', 'D', '9', 'Beam', '300', '650','none' , true),
    ('1679', 'Slip', '6', '3', 'D', '10', 'Beam', '300', '650','none' , true),
    ('1680', 'Slip', '6', '3', 'D', '11', 'Beam', '300', '650','none' , true),
    ('1681', 'Slip', '6', '3', 'D', '12', 'Beam', '300', '650','none' , true),
    ('1682', 'Slip', '6', '3', 'D', '13', 'Beam', '300', '650','none' , true),
    ('1683', 'Slip', '6', '3', 'D', '14', 'Beam', '300', '650','none' , true),
    ('1684', 'Slip', '6', '3', 'D', '15', 'Beam', '300', '650','none' , true),
    ('1685', 'Slip', '6', '3', 'D', '16', 'Beam', '300', '650','none' , true),
    ('1686', 'Slip', '6', '3', 'D', '17', 'Beam', '300', '650','none' , true),
    ('1687', 'Slip', '6', '3', 'D', '18', 'Beam', '300', '650','none' , true),
    ('1688', 'Slip', '6', '3', 'D', '19', 'Beam', '300', '650','none' , true),
    ('1689', 'Slip', '6', '3', 'D', '20', 'Beam', '300', '650','none' , true),
    ('1690', 'Slip', '6', '3', 'D', '21', 'Beam', '300', '650','none' , true),
    ('1691', 'Slip', '6', '3', 'D', '22', 'Beam', '300', '650','none' , true),
    ('1692', 'Slip', '6', '3', 'D', '23', 'Beam', '300', '650','none' , true),
    ('1693', 'Slip', '6', '3', 'D', '24', 'Beam', '300', '650','none' , true),
    ('1694', 'Slip', '6', '3', 'D', '25', 'Beam', '300', '650','none' , true),
    ('1695', 'Slip', '6', '3', 'D', '26', 'Beam', '300', '650','none' , true),
    ('1696', 'Slip', '6', '3', 'D', '27', 'Beam', '300', '650','none' , true),
    ('1697', 'Slip', '6', '3', 'D', '28', 'Beam', '300', '650','none' , true),
    ('1698', 'Slip', '6', '3', 'D', '29', 'Beam', '300', '650','none' , true),
    ('1699', 'Slip', '6', '3', 'D', '30', 'Beam', '300', '650','none' , true),
    ('1700', 'Slip', '6', '3', 'D', '31', 'Beam', '300', '650','none' , true),
    ('1701', 'Slip', '6', '3', 'D', '32', 'Beam', '300', '650','none' , true),
    ('1702', 'Slip', '6', '3', 'D', '33', 'Beam', '300', '650','none' , true),
    ('1703', 'Slip', '6', '3', 'D', '34', 'Beam', '300', '650','none' , true),
    ('1704', 'Slip', '6', '3', 'D', '35', 'Beam', '300', '650','none' , true),
    ('1705', 'Slip', '6', '3', 'D', '36', 'Beam', '300', '650','none' , true),
    ('1706', 'Slip', '6', '3', 'D', '37', 'Beam', '300', '650','none' , true),
    ('1707', 'Slip', '6', '3', 'D', '38', 'Beam', '300', '650','none' , true),
    ('1708', 'Slip', '6', '3', 'D', '39', 'Beam', '300', '650','none' , true),
    ('1709', 'Slip', '6', '3', 'D', '40', 'Beam', '300', '650','none' , true),
    ('1710', 'Slip', '6', '3', 'D', '41', 'Beam', '300', '650','none' , true),
    ('1711', 'Slip', '6', '3', 'D', '42', 'Beam', '300', '650','none' , true),
    ('1712', 'Slip', '6', '3', 'D', '43', 'Beam', '300', '650','none' , true),
    ('1713', 'Slip', '6', '3', 'D', '44', 'Beam', '300', '650','none' , true),
    ('1714', 'Slip', '6', '3', 'D', '45', 'Beam', '300', '650','none' , true),
    ('1715', 'Slip', '6', '3', 'D', '46', 'Beam', '300', '650','none' , true),
    ('1716', 'Slip', '6', '3', 'D', '47', 'Beam', '300', '650','none' , true),
    ('1717', 'Slip', '6', '3', 'D', '48', 'Beam', '300', '650','none' , true),
    ('1718', 'Slip', '6', '3', 'D', '49', 'Beam', '300', '650','none' , true),
    ('1719', 'Slip', '6', '3', 'D', '50', 'Beam', '300', '650','none' , true),
    ('1720', 'Slip', '6', '3', 'D', '51', 'Beam', '300', '650','none' , true),
    ('1721', 'Slip', '6', '3', 'D', '52', 'Beam', '300', '650','none' , true),
    ('1722', 'Slip', '6', '3', 'D', '53', 'Beam', '300', '650','none' , true),
    ('1723', 'Slip', '6', '3', 'D', '54', 'Beam', '300', '650','none' , true),
    ('1724', 'Slip', '6', '3', 'D', '55', 'Beam', '300', '650','none' , true),
    ('1725', 'Slip', '6', '3', 'D', '56', 'Beam', '300', '650','none' , true),
    ('1726', 'Slip', '6', '3', 'D', '57', 'Beam', '300', '650','none' , true),
    ('1727', 'Slip', '6', '3', 'D', '58', 'Beam', '300', '650','none' , true),
    ('1728', 'Slip', '6', '3', 'D', '59', 'Beam', '300', '650','none' , true),
    ('1729', 'Slip', '6', '3', 'D', '60', 'Beam', '300', '650','none' , true),
    ('1730', 'Slip', '6', '3', 'D', '61', 'Beam', '300', '650','none' , true),
    ('1731', 'Slip', '6', '3', 'D', '62', 'Beam', '300', '650','none' , true),
    ('1732', 'Slip', '6', '3', 'D', '63', 'Beam', '300', '650','none' , true),
    ('1733', 'Slip', '6', '3', 'D', '64', 'Beam', '300', '650','none' , true),
    ('1734', 'Slip', '6', '3', 'D', '65', 'Beam', '300', '650','none' , true),
    ('1735', 'Slip', '6', '3', 'D', '66', 'Beam', '300', '650','none' , true),
    ('1736', 'Slip', '6', '3', 'D', '67', 'Beam', '300', '650','none' , true),
    ('1737', 'Slip', '6', '3', 'D', '68', 'Beam', '300', '650','none' , true),
    ('1738', 'Slip', '6', '3', 'D', '69', 'Beam', '300', '650','none' , true),
    ('1739', 'Slip', '6', '3', 'D', '70', 'Beam', '300', '650','none' , true),
    ('1740', 'Slip', '6', '3', 'D', '71', 'Beam', '300', '650','none' , true),
    ('1741', 'Slip', '6', '3', 'D', '72', 'Beam', '300', '650','none' , true),
    ('1742', 'Slip', '6', '3', 'D', '73', 'Beam', '300', '650','none' , true),
    ('1743', 'Slip', '6', '3', 'D', '74', 'Beam', '300', '650','none' , true),
    ('1744', 'Slip', '6', '3', 'D', '75', 'Beam', '300', '650','none' , true),
    ('1745', 'Slip', '6', '3', 'D', '76', 'Beam', '300', '650','none' , true),
    ('1746', 'Slip', '6', '3', 'D', '77', 'Beam', '300', '650','none' , true),
    ('1747', 'Slip', '6', '3', 'D', '78', 'Beam', '300', '650','none' , true),
    ('1748', 'Slip', '6', '3', 'D', '79', 'Beam', '300', '650','none' , true),
    ('1749', 'Slip', '6', '3', 'D', '80', 'Beam', '300', '650','none' , true),
    ('1750', 'Slip', '6', '3', 'D', '81', 'Beam', '300', '650','none' , true),
    ('1751', 'Slip', '6', '3', 'D', '82', 'Beam', '300', '650','none' , true),
    ('1752', 'Slip', '6', '3', 'D', '83', 'Beam', '300', '650','none' , true),
    ('1753', 'Slip', '6', '3', 'D', '84', 'Beam', '300', '650','none' , true),
    ('1754', 'Slip', '6', '3', 'D', '85', 'Beam', '300', '650','none' , true),
    ('1755', 'Slip', '6', '3', 'D', '86', 'Beam', '300', '650','none' , true),
    ('1756', 'Slip', '6', '3', 'D', '87', 'Beam', '300', '650','none' , true),
    ('1757', 'Slip', '6', '3', 'D', '88', 'Beam', '300', '650','none' , true),
    ('1758', 'Slip', '6', '3', 'D', '89', 'Beam', '300', '650','none' , true),
    ('1759', 'Slip', '6', '3', 'D', '90', 'Beam', '300', '650','none' , true),
    ('1760', 'Slip', '6', '3', 'D', '91', 'Beam', '300', '650','none' , true),
    ('1761', 'Slip', '6', '3', 'D', '92', 'Beam', '300', '650','none' , true),
    ('1762', 'Slip', '6', '3', 'D', '93', 'Beam', '300', '650','none' , true),
    ('1763', 'Slip', '6', '3', 'D', '94', 'Beam', '300', '650','none' , true),
    ('1764', 'Slip', '6', '3', 'D', '95', 'Beam', '300', '650','none' , true),
    ('1765', 'Slip', '6', '3', 'D', '96', 'Beam', '300', '650','none' , true),
    ('1766', 'Slip', '6', '3', 'D', '97', 'Beam', '300', '650','none' , true),
    ('1767', 'Slip', '6', '3', 'D', '98', 'Beam', '300', '650','none' , true),
    ('1768', 'Slip', '6', '3', 'D', '99', 'Beam', '300', '650','none' , true),
    ('1769', 'Slip', '6', '3', 'D', '100', 'Beam', '300', '650','none' , true),
    ('1770', 'Slip', '6', '3', 'D', '101', 'Beam', '300', '650','none' , true),
    ('1771', 'Slip', '6', '3', 'D', '102', 'Beam', '300', '650','none' , true),
    ('1772', 'Slip', '6', '4', 'D', '103', 'Beam', '380', '650','none' , true),
    ('1773', 'Slip', '6', '4', 'D', '104', 'Beam', '380', '650','none' , true),
    ('1774', 'Slip', '6', '2', 'D', '105', 'Beam', '275', '600','none' , true),
    ('1775', 'Slip', '6', '2', 'D', '106', 'Beam', '275', '600','none' , true),
    ('1776', 'Slip', '6', '2', 'D', '107', 'Beam', '275', '600','none' , true),
    ('1777', 'Slip', '6', '2', 'D', '108', 'Beam', '275', '600','none' , true),
    ('1778', 'Slip', '6', '2', 'D', '109', 'Beam', '275', '600','none' , true),
    ('1779', 'Slip', '6', '2', 'D', '110', 'Beam', '275', '600','none' , true),
    ('1780', 'Slip', '6', '2', 'D', '111', 'Beam', '275', '600','none' , true),
    ('1781', 'Slip', '6', '2', 'D', '112', 'Beam', '275', '600','none' , true),
    ('1782', 'Slip', '6', '2', 'D', '113', 'Beam', '275', '600','none' , true),
    ('1783', 'Slip', '6', '2', 'D', '114', 'Beam', '275', '600','none' , true),
    ('1784', 'Slip', '6', '2', 'D', '115', 'Beam', '275', '600','none' , true),
    ('1785', 'Slip', '6', '2', 'D', '116', 'Beam', '275', '600','none' , true),
    ('1786', 'Slip', '6', '2', 'D', '117', 'Beam', '275', '600','none' , true),
    ('1787', 'Slip', '6', '2', 'D', '118', 'Beam', '275', '600','none' , true),
    ('1788', 'Slip', '6', '2', 'D', '119', 'Beam', '275', '600','none' , true),
    ('1789', 'Slip', '6', '2', 'D', '120', 'Beam', '275', '600','none' , true),
    ('1790', 'Slip', '6', '2', 'D', '121', 'Beam', '275', '600','none' , true),
    ('1791', 'Slip', '6', '2', 'D', '122', 'Beam', '275', '600','none' , true),
    ('1792', 'Slip', '6', '2', 'D', '123', 'Beam', '275', '600','none' , true),
    ('1793', 'Slip', '6', '2', 'D', '124', 'Beam', '275', '600','none' , true),
    ('1794', 'Slip', '6', '2', 'D', '125', 'Beam', '275', '600','none' , true),
    ('1795', 'Slip', '6', '2', 'D', '126', 'Beam', '275', '600','none' , true),
    ('1796', 'Slip', '6', '2', 'D', '127', 'Beam', '275', '600','none' , true),
    ('1797', 'Slip', '6', '2', 'D', '128', 'Beam', '275', '600','none' , true),
    ('1798', 'Slip', '6', '2', 'D', '129', 'Beam', '275', '600','none' , true),
    ('1799', 'Slip', '6', '2', 'D', '130', 'Beam', '275', '600','none' , true),
    ('1800', 'Slip', '6', '2', 'D', '131', 'Beam', '275', '600','none' , true),
    ('1801', 'Slip', '6', '2', 'D', '132', 'Beam', '275', '600','none' , true),
    ('1802', 'Slip', '6', '2', 'D', '133', 'Beam', '275', '600','none' , true),
    ('1803', 'Slip', '6', '2', 'D', '134', 'Beam', '275', '600','none' , true),
    ('1804', 'Slip', '6', '2', 'D', '135', 'Beam', '275', '600','none' , true),
    ('1805', 'Slip', '6', '2', 'D', '136', 'Beam', '275', '600','none' , true),
    ('1806', 'Slip', '6', '1', 'E', '1', 'Beam', '200', '600','none' , true),
    ('1807', 'Slip', '6', '1', 'E', '2', 'Beam', '200', '600','none' , true),
    ('1808', 'Slip', '6', '2', 'E', '3', 'Beam', '275', '600','none' , true),
    ('1809', 'Slip', '6', '2', 'E', '4', 'Beam', '275', '600','none' , true),
    ('1810', 'Slip', '6', '2', 'E', '5', 'Beam', '275', '600','none' , true),
    ('1811', 'Slip', '6', '2', 'E', '6', 'Beam', '275', '600','none' , true),
    ('1812', 'Slip', '6', '2', 'E', '7', 'Beam', '275', '600','none' , true),
    ('1813', 'Slip', '6', '2', 'E', '8', 'Beam', '275', '600','none' , true),
    ('1814', 'Slip', '6', '2', 'E', '9', 'Beam', '275', '600','none' , true),
    ('1815', 'Slip', '6', '2', 'E', '10', 'Beam', '275', '600','none' , true),
    ('1816', 'Slip', '6', '2', 'E', '11', 'Beam', '275', '600','none' , true),
    ('1817', 'Slip', '6', '2', 'E', '12', 'Beam', '275', '600','none' , true),
    ('1818', 'Slip', '6', '2', 'E', '13', 'Beam', '275', '600','none' , true),
    ('1819', 'Slip', '6', '2', 'E', '14', 'Beam', '275', '600','none' , true),
    ('1820', 'Slip', '6', '2', 'E', '15', 'Beam', '275', '600','none' , true),
    ('1821', 'Slip', '6', '2', 'E', '16', 'Beam', '275', '600','none' , true),
    ('1822', 'Slip', '6', '2', 'E', '17', 'Beam', '275', '600','none' , true),
    ('1823', 'Slip', '6', '2', 'E', '18', 'Beam', '275', '600','none' , true),
    ('1824', 'Slip', '6', '2', 'E', '19', 'Beam', '275', '600','none' , true),
    ('1825', 'Slip', '6', '2', 'E', '20', 'Beam', '275', '600','none' , true),
    ('1826', 'Slip', '6', '2', 'E', '21', 'Beam', '275', '600','none' , true),
    ('1827', 'Slip', '6', '2', 'E', '22', 'Beam', '275', '600','none' , true),
    ('1828', 'Slip', '6', '2', 'E', '23', 'Beam', '275', '600','none' , true),
    ('1829', 'Slip', '6', '2', 'E', '24', 'Beam', '275', '600','none' , true),
    ('1830', 'Slip', '6', '2', 'E', '25', 'Beam', '275', '600','none' , true),
    ('1831', 'Slip', '6', '2', 'E', '26', 'Beam', '275', '600','none' , true),
    ('1832', 'Slip', '6', '2', 'E', '27', 'Beam', '275', '600','none' , true),
    ('1833', 'Slip', '6', '2', 'E', '28', 'Beam', '275', '600','none' , true),
    ('1834', 'Slip', '6', '2', 'E', '29', 'Beam', '275', '600','none' , true),
    ('1835', 'Slip', '6', '2', 'E', '30', 'Beam', '275', '600','none' , true),
    ('1836', 'Slip', '6', '2', 'E', '31', 'Beam', '275', '600','none' , true),
    ('1837', 'Slip', '6', '2', 'E', '32', 'Beam', '275', '600','none' , true),
    ('1838', 'Slip', '6', '2', 'E', '33', 'Beam', '275', '600','none' , true),
    ('1839', 'Slip', '6', '2', 'E', '34', 'Beam', '275', '600','none' , true),
    ('1840', 'Slip', '6', '2', 'E', '35', 'Beam', '275', '600','none' , true),
    ('1841', 'Slip', '6', '2', 'E', '36', 'Beam', '275', '600','none' , true),
    ('1842', 'Slip', '6', '2', 'E', '37', 'Beam', '275', '600','none' , true),
    ('1843', 'Slip', '6', '2', 'E', '38', 'Beam', '275', '600','none' , true),
    ('1844', 'Slip', '6', '2', 'E', '39', 'Beam', '275', '600','none' , true),
    ('1845', 'Slip', '6', '2', 'E', '40', 'Beam', '275', '600','none' , true),
    ('1846', 'Slip', '6', '2', 'E', '41', 'Beam', '275', '600','none' , true),
    ('1847', 'Slip', '6', '2', 'E', '42', 'Beam', '275', '600','none' , true),
    ('1848', 'Slip', '6', '2', 'E', '43', 'Beam', '275', '600','none' , true),
    ('1849', 'Slip', '6', '2', 'E', '44', 'Beam', '275', '600','none' , true),
    ('1850', 'Slip', '6', '2', 'E', '45', 'Beam', '275', '600','none' , true),
    ('1851', 'Slip', '6', '2', 'E', '46', 'Beam', '275', '600','none' , true),
    ('1852', 'Slip', '6', '2', 'E', '47', 'Beam', '275', '600','none' , true),
    ('1853', 'Slip', '6', '2', 'E', '48', 'Beam', '275', '600','none' , true),
    ('1854', 'Slip', '6', '2', 'E', '49', 'Beam', '275', '600','none' , true),
    ('1855', 'Slip', '6', '2', 'E', '50', 'Beam', '275', '600','none' , true),
    ('1856', 'Slip', '6', '2', 'E', '51', 'Beam', '275', '600','none' , true),
    ('1857', 'Slip', '6', '2', 'E', '52', 'Beam', '275', '600','none' , true),
    ('1858', 'Slip', '6', '2', 'E', '53', 'Beam', '275', '600','none' , true),
    ('1859', 'Slip', '6', '2', 'E', '54', 'Beam', '275', '600','none' , true),
    ('1860', 'Slip', '6', '2', 'E', '55', 'Beam', '275', '600','none' , true),
    ('1861', 'Slip', '6', '2', 'E', '56', 'Beam', '275', '600','none' , true),
    ('1862', 'Slip', '6', '2', 'E', '57', 'Beam', '275', '600','none' , true),
    ('1863', 'Slip', '6', '2', 'E', '58', 'Beam', '275', '600','none' , true),
    ('1864', 'Slip', '6', '2', 'E', '59', 'Beam', '275', '600','none' , true),
    ('1865', 'Slip', '6', '2', 'E', '60', 'Beam', '275', '600','none' , true),
    ('1866', 'Slip', '6', '2', 'E', '61', 'Beam', '275', '600','none' , true),
    ('1867', 'Slip', '6', '2', 'E', '62', 'Beam', '275', '600','none' , true),
    ('1868', 'Slip', '6', '2', 'E', '63', 'Beam', '275', '600','none' , true),
    ('1869', 'Slip', '6', '2', 'E', '64', 'Beam', '275', '600','none' , true),
    ('1870', 'Slip', '6', '2', 'E', '65', 'Beam', '275', '600','none' , true),
    ('1871', 'Slip', '6', '2', 'E', '66', 'Beam', '275', '600','none' , true),
    ('1872', 'Slip', '6', '2', 'E', '67', 'Beam', '275', '600','none' , true),
    ('1873', 'Slip', '6', '2', 'E', '68', 'Beam', '275', '600','none' , true),
    ('1874', 'Slip', '6', '2', 'E', '69', 'Beam', '275', '600','none' , true),
    ('1875', 'Slip', '6', '2', 'E', '70', 'Beam', '275', '600','none' , true),
    ('1876', 'Slip', '6', '2', 'E', '71', 'Beam', '275', '600','none' , true),
    ('1877', 'Slip', '6', '2', 'E', '72', 'Beam', '275', '600','none' , true),
    ('1878', 'Slip', '6', '2', 'E', '73', 'Beam', '275', '600','none' , true),
    ('1879', 'Slip', '6', '2', 'E', '74', 'Beam', '275', '600','none' , true),
    ('1880', 'Slip', '6', '2', 'E', '75', 'Beam', '275', '600','none' , true),
    ('1881', 'Slip', '6', '2', 'E', '76', 'Beam', '275', '600','none' , true),
    ('1882', 'Slip', '6', '2', 'E', '77', 'Beam', '275', '600','none' , true),
    ('1883', 'Slip', '6', '2', 'E', '78', 'Beam', '275', '600','none' , true),
    ('1884', 'Slip', '6', '2', 'E', '79', 'Beam', '275', '600','none' , true),
    ('1885', 'Slip', '6', '2', 'E', '80', 'Beam', '275', '600','none' , true),
    ('1886', 'Slip', '6', '2', 'E', '81', 'Beam', '275', '600','none' , true),
    ('1887', 'Slip', '6', '2', 'E', '82', 'Beam', '275', '600','none' , true),
    ('1888', 'Slip', '6', '2', 'E', '83', 'Beam', '275', '600','none' , true),
    ('1889', 'Slip', '6', '2', 'E', '84', 'Beam', '275', '600','none' , true),
    ('1890', 'Slip', '6', '2', 'E', '85', 'Beam', '275', '600','none' , true),
    ('1891', 'Slip', '6', '2', 'E', '86', 'Beam', '275', '600','none' , true),
    ('1892', 'Slip', '6', '2', 'E', '87', 'Beam', '275', '600','none' , true),
    ('1893', 'Slip', '6', '2', 'E', '88', 'Beam', '275', '600','none' , true),
    ('1894', 'Slip', '6', '2', 'E', '89', 'Beam', '275', '600','none' , true),
    ('1895', 'Slip', '6', '2', 'E', '90', 'Beam', '275', '600','none' , true),
    ('1896', 'Slip', '6', '2', 'E', '91', 'Beam', '275', '600','none' , true),
    ('1897', 'Slip', '6', '2', 'E', '92', 'Beam', '275', '600','none' , true),
    ('1898', 'Slip', '6', '2', 'E', '93', 'Beam', '275', '600','none' , true),
    ('1899', 'Slip', '6', '2', 'E', '94', 'Beam', '275', '600','none' , true),
    ('1900', 'Slip', '6', '2', 'E', '95', 'Beam', '275', '600','none' , true),
    ('1901', 'Slip', '6', '2', 'E', '96', 'Beam', '275', '600','none' , true),
    ('1902', 'Slip', '6', '2', 'E', '97', 'Beam', '275', '600','none' , true),
    ('1903', 'Slip', '6', '2', 'E', '98', 'Beam', '275', '600','none' , true),
    ('1904', 'Slip', '6', '2', 'E', '99', 'Beam', '275', '600','none' , true),
    ('1905', 'Slip', '6', '2', 'E', '100', 'Beam', '275', '600','none' , true),
    ('1906', 'Slip', '6', '2', 'E', '101', 'Beam', '275', '600','none' , true),
    ('1907', 'Slip', '6', '2', 'E', '102', 'Beam', '275', '600','none' , true),
    ('1908', 'Slip', '6', '2', 'E', '103', 'Beam', '275', '600','none' , true),
    ('1909', 'Slip', '6', '2', 'E', '104', 'Beam', '275', '600','none' , true),
    ('1910', 'Slip', '6', '2', 'E', '105', 'Beam', '275', '600','none' , true),
    ('1911', 'Slip', '6', '2', 'E', '106', 'Beam', '275', '600','none' , true),
    ('1912', 'Slip', '6', '2', 'E', '107', 'Beam', '275', '600','none' , true),
    ('1913', 'Slip', '6', '2', 'E', '108', 'Beam', '275', '600','none' , true),
    ('1914', 'Slip', '6', '2', 'E', '109', 'Beam', '275', '600','none' , true),
    ('1915', 'Slip', '6', '2', 'E', '110', 'Beam', '275', '600','none' , true),
    ('1916', 'Slip', '6', '2', 'E', '111', 'Beam', '275', '600','none' , true),
    ('1917', 'Slip', '6', '2', 'E', '112', 'Beam', '275', '600','none' , true),
    ('1918', 'Slip', '6', '2', 'E', '113', 'Beam', '275', '600','none' , true),
    ('1919', 'Slip', '6', '2', 'E', '114', 'Beam', '275', '600','none' , true),
    ('1920', 'Slip', '6', '2', 'E', '115', 'Beam', '275', '600','none' , true),
    ('1921', 'Slip', '6', '2', 'E', '116', 'Beam', '275', '600','none' , true),
    ('1922', 'Slip', '6', '2', 'E', '117', 'Beam', '275', '600','none' , true),
    ('1923', 'Slip', '6', '2', 'E', '118', 'Beam', '275', '600','none' , true),
    ('1924', 'Slip', '6', '2', 'E', '119', 'Beam', '275', '600','none' , true),
    ('1925', 'Slip', '6', '2', 'E', '120', 'Beam', '275', '600','none' , true),
    ('1926', 'Slip', '6', '2', 'E', '121', 'Beam', '275', '600','none' , true),
    ('1927', 'Slip', '6', '2', 'E', '122', 'Beam', '275', '600','none' , true),
    ('1928', 'Slip', '6', '2', 'E', '123', 'Beam', '275', '600','none' , true),
    ('1929', 'Slip', '6', '2', 'E', '124', 'Beam', '275', '600','none' , true),
    ('1930', 'Slip', '6', '2', 'E', '125', 'Beam', '275', '600','none' , true),
    ('1931', 'Slip', '6', '2', 'E', '126', 'Beam', '275', '600','none' , true),
    ('1932', 'Slip', '6', '2', 'E', '127', 'Beam', '275', '600','none' , true),
    ('1933', 'Slip', '6', '2', 'E', '128', 'Beam', '275', '600','none' , true),
    ('1934', 'Slip', '6', '2', 'E', '129', 'Beam', '275', '600','none' , true),
    ('1935', 'Slip', '6', '2', 'E', '130', 'Beam', '275', '600','none' , true),
    ('1936', 'Slip', '6', '2', 'E', '131', 'Beam', '275', '600','none' , true),
    ('1937', 'Slip', '6', '2', 'E', '132', 'Beam', '275', '600','none' , true),
    ('1938', 'Slip', '6', '2', 'E', '133', 'Beam', '275', '600','none' , true),
    ('1939', 'Slip', '6', '2', 'E', '134', 'Beam', '275', '600','none' , true),
    ('1940', 'Slip', '6', '2', 'E', '135', 'Beam', '275', '600','none' , true),
    ('1941', 'Slip', '6', '2', 'E', '136', 'Beam', '275', '600','none' , true),
    ('1942', 'Slip', '6', '3', 'E', '137', 'Beam', '300', '700','none' , true),
    ('1943', 'Slip', '6', '3', 'E', '138', 'Beam', '300', '700','none' , true),
    ('1944', 'Slip', '6', '3', 'E', '139', 'Beam', '310', '700','none' , true),
    ('1945', 'Slip', '6', '3', 'E', '140', 'Beam', '310', '700','none' , true),
    ('1946', 'Slip', '6', '3', 'E', '141', 'Beam', '310', '700','none' , true),
    ('1947', 'Slip', '6', '3', 'E', '142', 'Beam', '310', '700','none' , true),
    ('1948', 'Slip', '6', '3', 'E', '143', 'Beam', '310', '700','none' , true),
    ('1949', 'Slip', '6', '3', 'E', '144', 'Beam', '310', '700','none' , true),
    ('1950', 'Slip', '6', '3', 'E', '145', 'Beam', '310', '700','none' , true),
    ('1951', 'Slip', '6', '3', 'E', '146', 'Beam', '310', '700','none' , true),
    ('1952', 'Slip', '6', '3', 'E', '147', 'Beam', '310', '700','none' , true),
    ('1953', 'Slip', '6', '3', 'E', '148', 'Beam', '310', '700','none' , true),
    ('1954', 'Slip', '6', '3', 'E', '149', 'Beam', '330', '700','none' , true),
    ('1955', 'Slip', '6', '3', 'E', '150', 'Beam', '330', '700','none' , true),
    ('1956', 'Slip', '6', '2', 'F', '1', 'Beam', '275', '650','none' , true),
    ('1957', 'Slip', '6', '4', 'F', '2', 'RearBuoy', '350', '1700','none' , true),
    ('1958', 'Slip', '6', '2', 'F', '3', 'Beam', '275', '650','none' , true),
    ('1959', 'Slip', '6', '4', 'F', '4', 'RearBuoy', '350', '1700','none' , true),
    ('1960', 'Slip', '6', '2', 'F', '5', 'Beam', '275', '650','none' , true),
    ('1961', 'Slip', '6', '4', 'F', '6', 'RearBuoy', '350', '1700','none' , true),
    ('1962', 'Slip', '6', '2', 'F', '7', 'Beam', '280', '650','none' , true),
    ('1963', 'Slip', '6', '4', 'F', '8', 'RearBuoy', '350', '1700','none' , true),
    ('1964', 'Slip', '6', '2', 'F', '9', 'Beam', '275', '650','none' , true),
    ('1965', 'Slip', '6', '4', 'F', '10', 'RearBuoy', '350', '1700','none' , true),
    ('1966', 'Slip', '6', '2', 'F', '11', 'Beam', '275', '650','none' , true),
    ('1967', 'Slip', '6', '4', 'F', '12', 'RearBuoy', '350', '1700','none' , true),
    ('1968', 'Slip', '6', '2', 'F', '13', 'Beam', '275', '650','none' , true),
    ('1969', 'Slip', '6', '4', 'F', '14', 'RearBuoy', '350', '1700','none' , true),
    ('1970', 'Slip', '6', '2', 'F', '15', 'Beam', '275', '650','none' , true),
    ('1971', 'Slip', '6', '4', 'F', '16', 'RearBuoy', '350', '1700','none' , true),
    ('1972', 'Slip', '6', '2', 'F', '17', 'Beam', '275', '650','none' , true),
    ('1973', 'Slip', '6', '4', 'F', '18', 'RearBuoy', '350', '1700','none' , true),
    ('1974', 'Slip', '6', '2', 'F', '19', 'Beam', '275', '650','none' , true),
    ('1975', 'Slip', '6', '4', 'F', '20', 'RearBuoy', '350', '1700','none' , true),
    ('1976', 'Slip', '6', '2', 'F', '21', 'Beam', '275', '650','none' , true),
    ('1977', 'Slip', '6', '4', 'F', '22', 'RearBuoy', '350', '1700','none' , true),
    ('1978', 'Slip', '6', '2', 'F', '23', 'Beam', '260', '650','none' , true),
    ('1979', 'Slip', '6', '4', 'F', '24', 'RearBuoy', '350', '1700','none' , true),
    ('1980', 'Slip', '6', '2', 'F', '25', 'Beam', '280', '650','none' , true),
    ('1981', 'Slip', '6', '4', 'F', '26', 'RearBuoy', '350', '1700','none' , true),
    ('1982', 'Slip', '6', '2', 'F', '27', 'Beam', '275', '650','none' , true),
    ('1983', 'Slip', '6', '4', 'F', '28', 'RearBuoy', '350', '1700','none' , true),
    ('1984', 'Slip', '6', '2', 'F', '29', 'Beam', '275', '650','none' , true),
    ('1985', 'Slip', '6', '4', 'F', '30', 'RearBuoy', '350', '1700','none' , true),
    ('1986', 'Slip', '6', '2', 'F', '31', 'Beam', '275', '650','none' , true),
    ('1987', 'Slip', '6', '4', 'F', '32', 'RearBuoy', '350', '1700','none' , true),
    ('1988', 'Slip', '6', '2', 'F', '33', 'Beam', '275', '650','none' , true),
    ('1989', 'Slip', '6', '4', 'F', '34', 'RearBuoy', '350', '1700','none' , true),
    ('1990', 'Slip', '6', '2', 'F', '35', 'Beam', '280', '650','none' , true),
    ('1991', 'Slip', '6', '4', 'F', '36', 'RearBuoy', '350', '1700','none' , true),
    ('1992', 'Slip', '6', '2', 'F', '37', 'Beam', '275', '650','none' , true),
    ('1993', 'Slip', '6', '4', 'F', '38', 'RearBuoy', '350', '1700','none' , true),
    ('1994', 'Slip', '6', '2', 'F', '39', 'Beam', '275', '650','none' , true),
    ('1995', 'Slip', '6', '4', 'F', '40', 'RearBuoy', '350', '1700','none' , true),
    ('1996', 'Slip', '6', '2', 'F', '41', 'Beam', '275', '650','none' , true),
    ('1997', 'Slip', '6', '4', 'F', '42', 'RearBuoy', '350', '1700','none' , true),
    ('1998', 'Slip', '6', '2', 'F', '43', 'Beam', '275', '650','none' , true),
    ('1999', 'Slip', '6', '4', 'F', '44', 'RearBuoy', '350', '1700','none' , true),
    ('2000', 'Slip', '6', '2', 'F', '45', 'Beam', '275', '650','none' , true),
    ('2001', 'Slip', '6', '4', 'F', '46', 'RearBuoy', '350', '1700','none' , true),
    ('2002', 'Slip', '6', '2', 'F', '47', 'Beam', '260', '650','none' , true),
    ('2003', 'Slip', '6', '4', 'F', '48', 'RearBuoy', '350', '1700','none' , true),
    ('2004', 'Slip', '6', '2', 'F', '49', 'Beam', '280', '650','none' , true),
    ('2005', 'Slip', '6', '4', 'F', '50', 'RearBuoy', '350', '1700','none' , true),
    ('2006', 'Slip', '6', '3', 'F', '51', 'Beam', '300', '650','none' , true),
    ('2007', 'Slip', '6', '4', 'F', '52', 'RearBuoy', '350', '1700','none' , true),
    ('2008', 'Slip', '6', '3', 'F', '53', 'Beam', '300', '650','none' , true),
    ('2009', 'Slip', '6', '4', 'F', '54', 'RearBuoy', '350', '1700','none' , true),
    ('2010', 'Slip', '6', '3', 'F', '55', 'Beam', '300', '650','none' , true),
    ('2011', 'Slip', '6', '4', 'F', '56', 'RearBuoy', '350', '1700','none' , true),
    ('2012', 'Slip', '6', '3', 'F', '57', 'Beam', '300', '650','none' , true),
    ('2013', 'Slip', '6', '4', 'F', '58', 'RearBuoy', '350', '1700','none' , true),
    ('2014', 'Slip', '6', '3', 'F', '59', 'Beam', '300', '650','none' , true),
    ('2015', 'Slip', '6', '4', 'F', '60', 'RearBuoy', '350', '1700','none' , true),
    ('2016', 'Slip', '6', '3', 'F', '61', 'Beam', '300', '650','none' , true),
    ('2017', 'Slip', '6', '4', 'F', '62', 'RearBuoy', '350', '1700','none' , true),
    ('2018', 'Slip', '6', '3', 'F', '63', 'Beam', '300', '650','none' , true),
    ('2019', 'Slip', '6', '4', 'F', '64', 'RearBuoy', '350', '1700','none' , true),
    ('2020', 'Slip', '6', '3', 'F', '65', 'Beam', '300', '650','none' , true),
    ('2021', 'Slip', '6', '4', 'F', '66', 'RearBuoy', '350', '1700','none' , true),
    ('2022', 'Slip', '6', '3', 'F', '67', 'Beam', '300', '650','none' , true),
    ('2023', 'Slip', '6', '4', 'F', '68', 'RearBuoy', '350', '1700','none' , true),
    ('2024', 'Slip', '6', '3', 'F', '69', 'Beam', '300', '650','none' , true),
    ('2025', 'Slip', '6', '4', 'F', '70', 'RearBuoy', '350', '1700','none' , true),
    ('2026', 'Slip', '6', '3', 'F', '71', 'Beam', '300', '650','none' , true),
    ('2027', 'Slip', '6', '4', 'F', '72', 'RearBuoy', '350', '1700','none' , true),
    ('2028', 'Slip', '6', '3', 'F', '73', 'Beam', '300', '650','none' , true),
    ('2029', 'Slip', '6', '4', 'F', '74', 'RearBuoy', '350', '1700','none' , true),
    ('2030', 'Slip', '6', '3', 'F', '75', 'Beam', '300', '650','none' , true),
    ('2031', 'Slip', '6', '4', 'F', '76', 'RearBuoy', '350', '1700','none' , true),
    ('2032', 'Slip', '6', '3', 'F', '77', 'Beam', '300', '650','none' , true),
    ('2033', 'Slip', '6', '4', 'F', '78', 'RearBuoy', '350', '1700','none' , true),
    ('2034', 'Slip', '6', '3', 'F', '79', 'Beam', '300', '650','none' , true),
    ('2035', 'Slip', '6', '4', 'F', '80', 'RearBuoy', '350', '1700','none' , true),
    ('2036', 'Slip', '6', '3', 'F', '81', 'Beam', '300', '650','none' , true),
    ('2037', 'Slip', '6', '4', 'F', '82', 'RearBuoy', '350', '1700','none' , true),
    ('2038', 'Slip', '6', '3', 'F', '83', 'Beam', '300', '650','none' , true),
    ('2039', 'Slip', '6', '3', 'F', '84', 'Beam', '300', '650','none' , true),
    ('2040', 'Slip', '6', '3', 'F', '85', 'Beam', '290', '650','none' , true),
    ('2041', 'Slip', '6', '3', 'F', '86', 'Beam', '290', '650','none' , true),
    ('2042', 'Slip', '6', '3', 'F', '87', 'Beam', '310', '650','none' , true),
    ('2043', 'Slip', '6', '3', 'F', '88', 'Beam', '300', '650','none' , true),
    ('2044', 'Slip', '6', '3', 'F', '89', 'Beam', '300', '650','none' , true),
    ('2045', 'Slip', '6', '4', 'F', '90', 'Beam', '340', '650','none' , true),
    ('2046', 'Slip', '6', '4', 'F', '91', 'Beam', '350', '650','none' , true),
    ('2047', 'Slip', '6', '3', 'F', '92', 'Beam', '320', '650','none' , true),
    ('2048', 'Slip', '6', '3', 'F', '93', 'Beam', '320', '650','none' , true),
    ('2049', 'Slip', '6', '3', 'F', '94', 'Beam', '320', '650','none' , true),
    ('2050', 'Slip', '6', '3', 'F', '95', 'Beam', '320', '650','none' , true),
    ('2051', 'Slip', '6', '3', 'F', '96', 'Beam', '320', '650','none' , true),
    ('2052', 'Slip', '6', '3', 'F', '97', 'Beam', '320', '650','none' , true),
    ('2053', 'Slip', '6', '3', 'F', '98', 'Beam', '330', '650','none' , true),
    ('2054', 'Slip', '6', '3', 'F', '99', 'Beam', '330', '650','none' , true),
    ('2055', 'Slip', '6', '3', 'F', '100', 'Beam', '330', '650','none' , true),
    ('2056', 'Slip', '6', '3', 'F', '101', 'Beam', '330', '650','none' , true),
    ('2057', 'Slip', '6', '3', 'F', '102', 'Beam', '310', '650','none' , true),
    ('2058', 'Slip', '6', '3', 'F', '103', 'Beam', '320', '650','none' , true),
    ('2059', 'Slip', '6', '3', 'F', '104', 'Beam', '300', '650','none' , true),
    ('2060', 'Slip', '6', '3', 'F', '105', 'Beam', '300', '550','none' , true),
    ('2061', 'Slip', '6', '3', 'F', '106', 'Beam', '300', '650','none' , true),
    ('2062', 'Slip', '6', '3', 'F', '107', 'Beam', '300', '550','none' , true),
    ('2063', 'Slip', '6', '3', 'F', '108', 'Beam', '300', '550','none' , true),
    ('2064', 'Slip', '6', '3', 'F', '109', 'Beam', '300', '550','none' , true),
    ('2065', 'Slip', '6', '3', 'F', '110', 'Beam', '300', '550','none' , true),
    ('2066', 'Slip', '6', '3', 'F', '111', 'Beam', '300', '550','none' , true),
    ('2067', 'Slip', '6', '3', 'F', '112', 'Beam', '300', '550','none' , true),
    ('2068', 'Slip', '6', '3', 'F', '113', 'Beam', '300', '550','none' , true),
    ('2069', 'Slip', '6', '3', 'F', '114', 'Beam', '300', '550','none' , true),
    ('2070', 'Slip', '6', '3', 'F', '115', 'Beam', '300', '550','none' , true),
    ('2071', 'Slip', '6', '3', 'F', '116', 'Beam', '300', '550','none' , true),
    ('2072', 'Slip', '6', '3', 'F', '117', 'Beam', '300', '550','none' , true),
    ('2073', 'Slip', '6', '3', 'F', '118', 'Beam', '300', '550','none' , true),
    ('2074', 'Slip', '6', '3', 'F', '119', 'Beam', '300', '550','none' , true),
    ('2075', 'Slip', '6', '3', 'F', '120', 'Beam', '300', '550','none' , true),
    ('2076', 'Slip', '6', '3', 'F', '121', 'Beam', '300', '550','none' , true),
    ('2077', 'Slip', '6', '3', 'F', '122', 'Beam', '300', '550','none' , true),
    ('2078', 'Slip', '6', '3', 'F', '123', 'Beam', '300', '550','none' , true),
    ('2079', 'Slip', '6', '3', 'F', '124', 'Beam', '300', '550','none' , true),
    ('2080', 'Slip', '6', '3', 'F', '125', 'Beam', '300', '550','none' , true),
    ('2081', 'Slip', '6', '3', 'F', '126', 'Beam', '300', '550','none' , true),
    ('2082', 'Slip', '6', '3', 'F', '127', 'Beam', '300', '550','none' , true),
    ('2083', 'Slip', '6', '3', 'F', '128', 'Beam', '300', '550','none' , true),
    ('2084', 'Slip', '6', '3', 'F', '129', 'Beam', '300', '550','none' , true),
    ('2085', 'Slip', '6', '4', 'G', '1', 'RearBuoy', '350', '1700','none' , true),
    ('2086', 'Slip', '6', '4', 'G', '2', 'RearBuoy', '350', '1700','none' , true),
    ('2087', 'Slip', '6', '4', 'G', '3', 'RearBuoy', '350', '1700','none' , true),
    ('2088', 'Slip', '6', '4', 'G', '5', 'RearBuoy', '350', '1700','none' , true),
    ('2089', 'Slip', '6', '4', 'G', '7', 'RearBuoy', '350', '1700','none' , true),
    ('2090', 'Slip', '6', '4', 'G', '9', 'RearBuoy', '350', '1700','none' , true),
    ('2091', 'Slip', '6', '4', 'G', '15', 'RearBuoy', '350', '1700','none' , true),
    ('2092', 'Slip', '6', '4', 'G', '16', 'RearBuoy', '350', '1700','none' , true),
    ('2093', 'Slip', '6', '4', 'G', '17', 'RearBuoy', '350', '1700','none' , true),
    ('2094', 'Slip', '6', '4', 'G', '19', 'RearBuoy', '350', '1700','none' , true),
    ('2095', 'Slip', '6', '4', 'G', '22', 'RearBuoy', '350', '1700','none' , true),
    ('2096', 'Slip', '6', '4', 'G', '25', 'RearBuoy', '350', '1700','none' , true),
    ('2097', 'Slip', '6', '4', 'G', '26', 'RearBuoy', '350', '1700','none' , true),
    ('2098', 'Slip', '6', '4', 'G', '27', 'RearBuoy', '350', '1700','none' , true),
    ('2099', 'Slip', '6', '4', 'G', '29', 'RearBuoy', '350', '1700','none' , true),
    ('2100', 'Slip', '6', '4', 'G', '31', 'RearBuoy', '350', '1700','none' , true),
    ('2101', 'Slip', '6', '4', 'G', '34', 'RearBuoy', '350', '1700','none' , true),
    ('2102', 'Slip', '6', '5', 'G', '35', 'RearBuoy', '400', '1700','none' , true),
    ('2103', 'Slip', '6', '5', 'G', '36', 'RearBuoy', '400', '1700','none' , true),
    ('2104', 'Slip', '6', '4', 'G', '37', 'RearBuoy', '400', '1700','none' , true),
    ('2105', 'Slip', '6', '5', 'G', '38', 'RearBuoy', '400', '1700','none' , true),
    ('2106', 'Slip', '6', '5', 'G', '39', 'RearBuoy', '400', '1700','none' , true),
    ('2107', 'Slip', '6', '5', 'G', '41', 'RearBuoy', '400', '1700','none' , true),
    ('2108', 'Slip', '6', '5', 'G', '43', 'RearBuoy', '400', '1700','none' , true),
    ('2109', 'Slip', '6', '5', 'G', '44', 'RearBuoy', '400', '1700','none' , true),
    ('2110', 'Slip', '6', '5', 'G', '47', 'RearBuoy', '400', '1700','none' , true),
    ('2111', 'Slip', '6', '5', 'G', '49', 'RearBuoy', '400', '1700','none' , true),
    ('2112', 'Slip', '6', '5', 'G', '52', 'RearBuoy', '400', '1700','none' , true),
    ('2113', 'Slip', '6', '5', 'G', '53', 'RearBuoy', '400', '1700','none' , true),
    ('2114', 'Slip', '6', '5', 'G', '55', 'RearBuoy', '400', '1700','none' , true),
    ('2115', 'Slip', '6', '5', 'G', '56', 'RearBuoy', '400', '1700','none' , true),
    ('2116', 'Slip', '6', '5', 'G', '57', 'RearBuoy', '400', '1700','none' , true),
    ('2117', 'Slip', '6', '5', 'G', '58', 'RearBuoy', '400', '1700','none' , true),
    ('2118', 'Slip', '6', '5', 'G', '59', 'RearBuoy', '400', '1700','none' , true),
    ('2119', 'Slip', '6', '5', 'G', '60', 'RearBuoy', '400', '1700','none' , true),
    ('2120', 'Slip', '6', '5', 'G', '61', 'RearBuoy', '400', '1700','none' , true),
    ('2121', 'Slip', '6', '5', 'G', '62', 'RearBuoy', '400', '1700','none' , true),
    ('2122', 'Slip', '6', '5', 'G', '64', 'RearBuoy', '400', '1700','none' , true),
    ('2123', 'Slip', '6', '6', 'G', '65', 'RearBuoy', '450', '1700','none' , true),
    ('2124', 'Slip', '6', '6', 'G', '66', 'RearBuoy', '450', '1700','none' , true),
    ('2125', 'Slip', '6', '6', 'G', '68', 'RearBuoy', '500', '1700','none' , true),
    ('2126', 'Slip', '6', '6', 'G', '69', 'RearBuoy', '500', '1700','none' , true),
    ('2127', 'Slip', '6', '6', 'G', '70', 'RearBuoy', '500', '1700','none' , true),
    ('2128', 'Slip', '6', '6', 'G', '71', 'RearBuoy', '500', '1700','none' , true),
    ('2129', 'Slip', '6', '6', 'G', '73', 'RearBuoy', '500', '1700','none' , true),
    ('2130', 'Slip', '6', '6', 'G', '74', 'RearBuoy', '500', '1700','none' , true),
    ('2131', 'Slip', '6', '6', 'G', '75', 'RearBuoy', '500', '1700','none' , true),
    ('2132', 'Slip', '6', '6', 'G', '76', 'RearBuoy', '500', '1700','none' , true),
    ('2133', 'Slip', '6', '6', 'G', '77', 'RearBuoy', '500', '1700','none' , true),
    ('2134', 'Slip', '6', '6', 'G', '78', 'RearBuoy', '500', '1700','none' , true),
    ('2135', 'Slip', '6', '6', 'G', '79', 'RearBuoy', '500', '1700','none' , true),
    ('2136', 'Slip', '6', '6', 'G', '84', 'RearBuoy', '500', '1700','none' , true),
    ('2137', 'Slip', '6', '4', 'G', '86', 'RearBuoy', '350', '1700','none' , true),
    ('2138', 'Slip', '6', '4', 'G', '87', 'RearBuoy', '350', '1700','none' , true),
    ('2139', 'Slip', '6', '4', 'G', '88', 'RearBuoy', '350', '1700','none' , true),
    ('2140', 'Slip', '6', '4', 'G', '89', 'RearBuoy', '350', '1700','none' , true),
    ('2141', 'Slip', '6', '4', 'G', '90', 'RearBuoy', '350', '1700','none' , true),
    ('2142', 'Slip', '6', '4', 'G', '92', 'RearBuoy', '350', '1700','none' , true),
    ('2143', 'Slip', '6', '4', 'G', '93', 'RearBuoy', '350', '1700','none' , true),
    ('2144', 'Slip', '6', '4', 'G', '94', 'RearBuoy', '350', '1700','none' , true),
    ('2145', 'Slip', '6', '4', 'G', '95', 'RearBuoy', '350', '1700','none' , true),
    ('2146', 'Slip', '6', '4', 'G', '96', 'RearBuoy', '350', '1700','none' , true),
    ('2147', 'Slip', '6', '4', 'G', '97', 'RearBuoy', '350', '1700','none' , true),
    ('2148', 'Slip', '6', '4', 'G', '98', 'RearBuoy', '350', '1700','none' , true),
    ('2149', 'Slip', '6', '4', 'G', '99', 'RearBuoy', '350', '1700','none' , true),
    ('2150', 'Slip', '6', '4', 'G', '100', 'RearBuoy', '350', '1700','none' , true),
    ('2151', 'Slip', '6', '4', 'G', '101', 'RearBuoy', '350', '1700','none' , true),
    ('2152', 'Slip', '6', '4', 'G', '102', 'RearBuoy', '350', '1700','none' , true),
    ('2153', 'Slip', '6', '4', 'G', '103', 'RearBuoy', '350', '1700','none' , true),
    ('2154', 'Slip', '6', '4', 'G', '104', 'RearBuoy', '350', '1700','none' , true),
    ('2155', 'Slip', '6', '4', 'G', '105', 'RearBuoy', '350', '1700','none' , true),
    ('2156', 'Slip', '6', '4', 'G', '106', 'RearBuoy', '350', '1700','none' , true),
    ('2157', 'Slip', '6', '4', 'G', '107', 'RearBuoy', '350', '1700','none' , true),
    ('2158', 'Slip', '6', '4', 'G', '108', 'RearBuoy', '350', '1700','none' , true),
    ('2159', 'Slip', '6', '4', 'G', '109', 'RearBuoy', '350', '1700','none' , true),
    ('2160', 'Slip', '6', '4', 'G', '110', 'RearBuoy', '350', '1700','none' , true),
    ('2161', 'Slip', '6', '4', 'G', '111', 'RearBuoy', '350', '1700','none' , true),
    ('2162', 'Slip', '6', '4', 'G', '112', 'RearBuoy', '350', '1700','none' , true),
    ('2163', 'Slip', '6', '4', 'G', '113', 'RearBuoy', '350', '1700','none' , true),
    ('2164', 'Slip', '6', '4', 'G', '114', 'RearBuoy', '350', '1700','none' , true),
    ('2165', 'Slip', '6', '4', 'G', '115', 'RearBuoy', '350', '1700','none' , true),
    ('2166', 'Slip', '6', '4', 'G', '116', 'RearBuoy', '350', '1700','none' , true),
    ('2167', 'Slip', '6', '6', 'G', '117', 'RearBuoy', '500', '1700','none' , true),
    ('2168', 'Slip', '6', '2', 'J', '1', 'Beam', '260', '650','none' , true),
    ('2169', 'Slip', '6', '2', 'J', '3', 'Beam', '240', '650','none' , true),
    ('2170', 'Slip', '6', '2', 'J', '6', 'Beam', '250', '650','none' , true),
    ('2171', 'Slip', '6', '2', 'J', '7', 'Beam', '275', '650','none' , true),
    ('2172', 'Slip', '6', '2', 'J', '8', 'Beam', '275', '650','none' , true),
    ('2173', 'Slip', '6', '2', 'J', '10', 'Beam', '275', '650','none' , true),
    ('2174', 'Slip', '6', '2', 'J', '11', 'Beam', '275', '650','none' , true),
    ('2175', 'Slip', '6', '2', 'J', '12', 'Beam', '275', '650','none' , true),
    ('2176', 'Slip', '6', '2', 'J', '13', 'Beam', '275', '650','none' , true),
    ('2177', 'Slip', '6', '2', 'J', '14', 'Beam', '275', '650','none' , true),
    ('2178', 'Slip', '6', '2', 'J', '15', 'Beam', '275', '650','none' , true),
    ('2179', 'Slip', '6', '2', 'J', '16', 'Beam', '275', '650','none' , true),
    ('2180', 'Slip', '6', '2', 'J', '17', 'Beam', '275', '650','none' , true),
    ('2181', 'Slip', '6', '2', 'J', '18', 'Beam', '275', '650','none' , true),
    ('2182', 'Slip', '6', '2', 'J', '19', 'Beam', '275', '650','none' , true),
    ('2183', 'Slip', '6', '2', 'J', '20', 'Beam', '275', '650','none' , true),
    ('2184', 'Slip', '6', '2', 'J', '21', 'Beam', '275', '650','none' , true),
    ('2185', 'Slip', '6', '2', 'J', '22', 'Beam', '275', '650','none' , true),
    ('2186', 'Slip', '6', '2', 'J', '23', 'Beam', '275', '650','none' , true),
    ('2187', 'Slip', '6', '2', 'J', '27', 'Beam', '275', '650','none' , true),
    ('2188', 'Slip', '6', '2', 'J', '28', 'Beam', '275', '650','none' , true),
    ('2189', 'Slip', '6', '3', 'J', '29', 'WalkBeam', '330', '900','none' , true),
    ('2190', 'Slip', '6', '3', 'J', '31', 'WalkBeam', '330', '900','none' , true),
    ('2191', 'Slip', '6', '3', 'J', '33', 'WalkBeam', '330', '900','none' , true),
    ('2192', 'Slip', '6', '3', 'J', '37', 'WalkBeam', '330', '900','none' , true),
    ('2193', 'Slip', '6', '3', 'J', '41', 'WalkBeam', '330', '900','none' , true),
    ('2194', 'Slip', '6', '4', 'J', '53', 'WalkBeam', '360', '1000','none' , true),
    ('2195', 'Slip', '6', '4', 'J', '55', 'WalkBeam', '380', '1000','none' , true),
    ('2196', 'Slip', '6', '4', 'J', '57', 'WalkBeam', '380', '1000','none' , true),
    ('2197', 'Slip', '6', '4', 'J', '59', 'WalkBeam', '380', '1000','none' , true),
    ('2198', 'Slip', '6', '4', 'J', '63', 'WalkBeam', '380', '1000','none' , true),
    ('2199', 'Slip', '6', '4', 'J', '65', 'WalkBeam', '380', '1000','none' , true),
    ('2200', 'Slip', '6', '4', 'J', '67', 'WalkBeam', '380', '1000','none' , true),
    ('2201', 'Slip', '6', '4', 'J', '69', 'WalkBeam', '380', '1000','none' , true),
    ('2202', 'Slip', '6', '4', 'J', '71', 'WalkBeam', '380', '1000','none' , true),
    ('2203', 'Trailer', '6', '3', 'TRAILERI', '1', 'None', '260', '800','none' , true),
    ('2204', 'Trailer', '6', '3', 'TRAILERI', '2', 'None', '260', '800','none' , true),
    ('2205', 'Trailer', '6', '3', 'TRAILERI', '3', 'None', '260', '800','none' , true),
    ('2206', 'Trailer', '6', '3', 'TRAILERI', '4', 'None', '260', '800','none' , true),
    ('2207', 'Trailer', '6', '3', 'TRAILERI', '5', 'None', '260', '800','none' , true),
    ('2208', 'Trailer', '6', '3', 'TRAILERI', '6', 'None', '260', '800','none' , true),
    ('2209', 'Trailer', '6', '3', 'TRAILERI', '7', 'None', '260', '800','none' , true),
    ('2210', 'Trailer', '6', '3', 'TRAILERI', '8', 'None', '260', '800','none' , true),
    ('2211', 'Trailer', '6', '3', 'TRAILERI', '9', 'None', '260', '800','none' , true),
    ('2212', 'Trailer', '6', '3', 'TRAILERI', '10', 'None', '260', '800','none' , true),
    ('2213', 'Trailer', '6', '3', 'TRAILERI', '11', 'None', '260', '800','none' , true),
    ('2214', 'Trailer', '6', '3', 'TRAILERI', '12', 'None', '260', '700','none' , true),
    ('2215', 'Trailer', '6', '3', 'TRAILERI', '13', 'None', '260', '700','none' , true),
    ('2216', 'Trailer', '6', '3', 'TRAILERI', '14', 'None', '260', '700','none' , true),
    ('2217', 'Trailer', '6', '3', 'TRAILERI', '15', 'None', '260', '700','none' , true),
    ('2218', 'Trailer', '6', '3', 'TRAILERI', '16', 'None', '260', '700','none' , true),
    ('2219', 'Trailer', '6', '3', 'TRAILERI', '17', 'None', '260', '700','none' , true),
    ('2220', 'Trailer', '6', '3', 'TRAILERI', '18', 'None', '260', '700','none' , true),
    ('2221', 'Trailer', '6', '3', 'TRAILERI', '19', 'None', '260', '700','none' , true),
    ('2222', 'Trailer', '6', '3', 'TRAILERI', '20', 'None', '260', '700','none' , true),
    ('2223', 'Trailer', '6', '3', 'TRAILERI', '21', 'None', '260', '700','none' , true),
    ('2224', 'Trailer', '6', '3', 'TRAILERI', '22', 'None', '260', '700','none' , true),
    ('2225', 'Trailer', '6', '3', 'TRAILERI', '23', 'None', '260', '700','none' , true),
    ('2226', 'Trailer', '6', '3', 'TRAILERI', '24', 'None', '260', '700','none' , true),
    ('2227', 'Trailer', '6', '3', 'TRAILERI', '25', 'None', '260', '700','none' , true),
    ('2228', 'Trailer', '6', '3', 'TRAILERI', '26', 'None', '260', '700','none' , true),
    ('2229', 'Trailer', '6', '3', 'TRAILERI', '27', 'None', '260', '700','none' , true),
    ('2230', 'Trailer', '6', '3', 'TRAILERI', '28', 'None', '260', '700','none' , true),
    ('2231', 'Storage', '7', '3', 'A', '1', 'Trailer', '300', '550','none' , true),
    ('2232', 'Storage', '7', '3', 'A', '2', 'Trailer', '310', '500','none' , true),
    ('2233', 'Storage', '7', '3', 'A', '3', 'Trailer', '290', '550','none' , true),
    ('2234', 'Storage', '7', '3', 'A', '4', 'Trailer', '320', '500','none' , true),
    ('2235', 'Storage', '7', '2', 'A', '5', 'Trailer', '260', '550','none' , true),
    ('2236', 'Storage', '7', '2', 'A', '6', 'Trailer', '260', '500','none' , true),
    ('2237', 'Slip', '7', '2', 'A', '7', 'Beam', '275', '550','none' , true),
    ('2238', 'Slip', '7', '2', 'A', '8', 'Beam', '275', '500','none' , true),
    ('2239', 'Slip', '7', '2', 'A', '9', 'Beam', '270', '550','none' , true),
    ('2240', 'Slip', '7', '2', 'A', '10', 'Beam', '270', '500','none' , true),
    ('2241', 'Slip', '7', '2', 'A', '11', 'Beam', '280', '550','none' , true),
    ('2242', 'Slip', '7', '2', 'A', '12', 'Beam', '280', '500','none' , true),
    ('2243', 'Slip', '7', '2', 'A', '13', 'Beam', '260', '550','none' , true),
    ('2244', 'Slip', '7', '2', 'A', '14', 'Beam', '260', '500','none' , true),
    ('2245', 'Slip', '7', '2', 'A', '16', 'Beam', '280', '500','none' , true),
    ('2246', 'Slip', '7', '2', 'A', '18', 'Beam', '250', '500','none' , true),
    ('2247', 'Slip', '7', '2', 'A', '20', 'Beam', '280', '500','none' , true),
    ('2248', 'Slip', '7', '2', 'A', '22', 'Beam', '280', '500','none' , true),
    ('2249', 'Slip', '7', '2', 'A', '24', 'Beam', '260', '500','none' , true),
    ('2250', 'Slip', '7', '2', 'A', '26', 'Beam', '280', '500','none' , true),
    ('2251', 'Slip', '7', '2', 'A', '28', 'Beam', '260', '500','none' , true),
    ('2252', 'Slip', '7', '2', 'A', '30', 'Beam', '270', '500','none' , true),
    ('2253', 'Slip', '7', '2', 'A', '32', 'Beam', '270', '500','none' , true),
    ('2254', 'Slip', '7', '2', 'A', '34', 'Beam', '275', '500','none' , true),
    ('2255', 'Slip', '7', '2', 'A', '36', 'Beam', '270', '500','none' , true),
    ('2256', 'Slip', '7', '2', 'A', '38', 'Beam', '270', '500','none' , true),
    ('2257', 'Slip', '7', '2', 'A', '40', 'Beam', '260', '500','none' , true),
    ('2258', 'Slip', '7', '2', 'A', '42', 'Beam', '280', '500','none' , true),
    ('2259', 'Slip', '7', '2', 'A', '44', 'Beam', '270', '500','none' , true),
    ('2260', 'Slip', '7', '2', 'A', '46', 'Beam', '275', '600','none' , true),
    ('2261', 'Slip', '7', '2', 'A', '48', 'Beam', '260', '600','none' , true),
    ('2262', 'Slip', '7', '2', 'A', '50', 'Beam', '250', '600','none' , true),
    ('2263', 'Slip', '7', '2', 'A', '52', 'Beam', '250', '600','none' , true),
    ('2264', 'Slip', '7', '3', 'A', '54', 'Beam', '300', '600','none' , true),
    ('2265', 'Slip', '7', '3', 'A', '56', 'Beam', '300', '600','none' , true),
    ('2266', 'Slip', '7', '3', 'A', '58', 'Beam', '300', '600','none' , true),
    ('2267', 'Slip', '7', '3', 'A', '60', 'Beam', '300', '600','none' , true),
    ('2268', 'Slip', '7', '3', 'A', '62', 'Beam', '300', '600','none' , true),
    ('2269', 'Slip', '7', '3', 'A', '64', 'Beam', '300', '600','none' , true),
    ('2270', 'Slip', '7', '3', 'A', '66', 'Beam', '300', '600','none' , true),
    ('2271', 'Slip', '7', '1', 'B', '1', 'Beam', '220', '500','none' , true),
    ('2272', 'Slip', '7', '1', 'B', '2', 'Beam', '200', '550','none' , true),
    ('2273', 'Slip', '7', '2', 'B', '3', 'Beam', '250', '500','none' , true),
    ('2274', 'Slip', '7', '2', 'B', '4', 'Beam', '275', '500','none' , true),
    ('2275', 'Slip', '7', '2', 'B', '5', 'Beam', '275', '500','none' , true),
    ('2276', 'Slip', '7', '2', 'B', '6', 'Beam', '275', '500','none' , true),
    ('2277', 'Slip', '7', '2', 'B', '7', 'Beam', '275', '500','none' , true),
    ('2278', 'Slip', '7', '2', 'B', '8', 'Beam', '275', '500','none' , true),
    ('2279', 'Slip', '7', '2', 'B', '9', 'Beam', '275', '500','none' , true),
    ('2280', 'Slip', '7', '2', 'B', '10', 'Beam', '275', '500','none' , true),
    ('2281', 'Slip', '7', '2', 'B', '11', 'Beam', '275', '500','none' , true),
    ('2282', 'Slip', '7', '2', 'B', '12', 'Beam', '275', '500','none' , true),
    ('2283', 'Slip', '7', '2', 'B', '13', 'Beam', '275', '500','none' , true),
    ('2284', 'Slip', '7', '2', 'B', '14', 'Beam', '275', '500','none' , true),
    ('2285', 'Slip', '7', '2', 'B', '15', 'Beam', '275', '500','none' , true),
    ('2286', 'Slip', '7', '2', 'B', '16', 'Beam', '275', '500','none' , true),
    ('2287', 'Slip', '7', '2', 'B', '17', 'Beam', '275', '500','none' , true),
    ('2288', 'Slip', '7', '2', 'B', '18', 'Beam', '275', '500','none' , true),
    ('2289', 'Slip', '7', '2', 'B', '19', 'Beam', '275', '500','none' , true),
    ('2290', 'Slip', '7', '2', 'B', '20', 'Beam', '275', '550','none' , true),
    ('2291', 'Slip', '7', '2', 'B', '21', 'Beam', '275', '550','none' , true),
    ('2292', 'Slip', '7', '2', 'B', '22', 'Beam', '275', '550','none' , true),
    ('2293', 'Slip', '7', '2', 'B', '23', 'Beam', '275', '550','none' , true),
    ('2294', 'Slip', '7', '2', 'B', '24', 'Beam', '275', '550','none' , true),
    ('2295', 'Slip', '7', '2', 'B', '25', 'Beam', '275', '550','none' , true),
    ('2296', 'Slip', '7', '2', 'B', '26', 'Beam', '275', '550','none' , true),
    ('2297', 'Slip', '7', '2', 'B', '27', 'Beam', '275', '600','none' , true),
    ('2298', 'Slip', '7', '2', 'B', '28', 'Beam', '275', '550','none' , true),
    ('2299', 'Slip', '7', '2', 'B', '29', 'Beam', '275', '600','none' , true),
    ('2300', 'Slip', '7', '2', 'B', '30', 'Beam', '275', '600','none' , true),
    ('2301', 'Slip', '7', '2', 'B', '31', 'Beam', '275', '600','none' , true),
    ('2302', 'Slip', '7', '2', 'B', '32', 'Beam', '275', '600','none' , true),
    ('2303', 'Slip', '7', '2', 'B', '33', 'Beam', '275', '600','none' , true),
    ('2304', 'Slip', '7', '2', 'B', '34', 'Beam', '275', '600','none' , true),
    ('2305', 'Slip', '7', '2', 'B', '35', 'Beam', '275', '600','none' , true),
    ('2306', 'Slip', '7', '2', 'B', '36', 'Beam', '275', '600','none' , true),
    ('2307', 'Slip', '7', '2', 'B', '37', 'Beam', '275', '600','none' , true),
    ('2308', 'Slip', '7', '2', 'B', '38', 'Beam', '275', '600','none' , true),
    ('2309', 'Slip', '7', '2', 'B', '39', 'Beam', '275', '600','none' , true),
    ('2310', 'Slip', '7', '2', 'B', '40', 'Beam', '275', '600','none' , true),
    ('2311', 'Slip', '7', '2', 'B', '41', 'Beam', '275', '600','none' , true),
    ('2312', 'Slip', '7', '2', 'B', '42', 'Beam', '275', '600','none' , true),
    ('2313', 'Slip', '7', '3', 'B', '43', 'Beam', '300', '600','none' , true),
    ('2314', 'Slip', '7', '3', 'B', '44', 'Beam', '300', '600','none' , true),
    ('2315', 'Slip', '7', '3', 'B', '45', 'Beam', '300', '600','none' , true),
    ('2316', 'Slip', '7', '3', 'B', '46', 'Beam', '300', '600','none' , true),
    ('2317', 'Slip', '7', '3', 'B', '47', 'Beam', '300', '600','none' , true),
    ('2318', 'Slip', '7', '3', 'B', '48', 'Beam', '300', '600','none' , true),
    ('2319', 'Slip', '7', '3', 'B', '49', 'Beam', '300', '600','none' , true),
    ('2320', 'Slip', '7', '3', 'B', '50', 'Beam', '300', '600','none' , true),
    ('2321', 'Slip', '7', '3', 'B', '51', 'Beam', '300', '600','none' , true),
    ('2322', 'Slip', '7', '3', 'B', '52', 'Beam', '300', '600','none' , true),
    ('2323', 'Slip', '7', '3', 'B', '53', 'Beam', '300', '600','none' , true),
    ('2324', 'Slip', '7', '3', 'B', '54', 'Beam', '300', '600','none' , true),
    ('2325', 'Slip', '7', '3', 'B', '55', 'Beam', '300', '600','none' , true),
    ('2326', 'Slip', '7', '3', 'B', '56', 'Beam', '300', '600','none' , true),
    ('2327', 'Slip', '7', '3', 'B', '57', 'Beam', '300', '600','none' , true),
    ('2328', 'Slip', '7', '3', 'B', '58', 'Beam', '300', '600','none' , true),
    ('2329', 'Slip', '7', '5', 'B', '59', 'WalkBeam', '400', '1000','none' , true),
    ('2330', 'Slip', '7', '5', 'B', '60', 'WalkBeam', '400', '1000','none' , true),
    ('2331', 'Slip', '7', '5', 'B', '61', 'WalkBeam', '400', '1000','none' , true),
    ('2332', 'Slip', '7', '5', 'B', '62', 'WalkBeam', '400', '1000','none' , true),
    ('2333', 'Slip', '7', '2', 'C', '1', 'Beam', '275', '450','none' , true),
    ('2334', 'Slip', '7', '2', 'C', '2', 'Beam', '260', '550','none' , true),
    ('2335', 'Slip', '7', '2', 'C', '3', 'Beam', '275', '450','none' , true),
    ('2336', 'Slip', '7', '2', 'C', '4', 'Beam', '260', '450','none' , true),
    ('2337', 'Slip', '7', '2', 'C', '5', 'Beam', '280', '450','none' , true),
    ('2338', 'Slip', '7', '2', 'C', '6', 'Beam', '275', '450','none' , true),
    ('2339', 'Slip', '7', '3', 'C', '7', 'Beam', '300', '550','none' , true),
    ('2340', 'Slip', '7', '2', 'C', '8', 'Beam', '275', '450','none' , true),
    ('2341', 'Slip', '7', '3', 'C', '9', 'Beam', '300', '550','none' , true),
    ('2342', 'Slip', '7', '3', 'C', '10', 'Beam', '290', '450','none' , true),
    ('2343', 'Slip', '7', '3', 'C', '11', 'Beam', '290', '550','none' , true),
    ('2344', 'Slip', '7', '3', 'C', '12', 'Beam', '300', '550','none' , true),
    ('2345', 'Slip', '7', '3', 'C', '13', 'Beam', '300', '550','none' , true),
    ('2346', 'Slip', '7', '3', 'C', '14', 'Beam', '300', '550','none' , true),
    ('2347', 'Slip', '7', '3', 'C', '15', 'Beam', '300', '550','none' , true),
    ('2348', 'Slip', '7', '3', 'C', '16', 'Beam', '300', '550','none' , true),
    ('2349', 'Slip', '7', '3', 'C', '17', 'Beam', '300', '550','none' , true),
    ('2350', 'Slip', '7', '3', 'C', '18', 'Beam', '300', '550','none' , true),
    ('2351', 'Slip', '7', '3', 'C', '19', 'Beam', '300', '550','none' , true),
    ('2352', 'Slip', '7', '3', 'C', '20', 'Beam', '300', '550','none' , true),
    ('2353', 'Slip', '7', '3', 'C', '21', 'Beam', '300', '550','none' , true),
    ('2354', 'Slip', '7', '3', 'C', '22', 'Beam', '300', '550','none' , true),
    ('2355', 'Slip', '7', '3', 'C', '23', 'Beam', '290', '550','none' , true),
    ('2356', 'Slip', '7', '3', 'C', '24', 'Beam', '300', '550','none' , true),
    ('2357', 'Slip', '7', '3', 'C', '25', 'Beam', '300', '550','none' , true),
    ('2358', 'Slip', '7', '3', 'C', '26', 'Beam', '300', '550','none' , true),
    ('2359', 'Slip', '7', '3', 'C', '27', 'Beam', '300', '550','none' , true),
    ('2360', 'Slip', '7', '3', 'C', '28', 'Beam', '290', '550','none' , true),
    ('2361', 'Slip', '7', '3', 'C', '29', 'Beam', '300', '550','none' , true),
    ('2362', 'Slip', '7', '3', 'C', '30', 'Beam', '300', '550','none' , true),
    ('2363', 'Slip', '7', '3', 'C', '31', 'Beam', '300', '550','none' , true),
    ('2364', 'Slip', '7', '3', 'C', '32', 'Beam', '300', '550','none' , true),
    ('2365', 'Storage', '7', '3', 'C', '33', 'Buck', '300', '550','none' , true),
    ('2366', 'Storage', '7', '3', 'C', '34', 'Buck', '300', '550','none' , true),
    ('2367', 'Storage', '7', '3', 'C', '35', 'Buck', '300', '550','none' , true),
    ('2368', 'Storage', '7', '3', 'C', '36', 'Buck', '300', '550','none' , true),
    ('2369', 'Storage', '7', '3', 'C', '37', 'Buck', '300', '550','none' , true),
    ('2370', 'Storage', '7', '3', 'C', '38', 'Buck', '300', '550','none' , true),
    ('2371', 'Storage', '7', '3', 'C', '39', 'Buck', '300', '550','none' , true),
    ('2372', 'Slip', '7', '3', 'C', '40', 'Beam', '300', '550','none' , true),
    ('2373', 'Slip', '7', '3', 'C', '41', 'Beam', '300', '550','none' , true),
    ('2374', 'Slip', '7', '3', 'C', '42', 'Beam', '300', '550','none' , true),
    ('2375', 'Slip', '7', '3', 'C', '43', 'Beam', '290', '550','none' , true),
    ('2376', 'Slip', '7', '3', 'C', '44', 'Beam', '300', '550','none' , true),
    ('2377', 'Slip', '7', '3', 'C', '46', 'Beam', '300', '550','none' , true),
    ('2378', 'Slip', '7', '3', 'C', '48', 'Beam', '300', '550','none' , true),
    ('2379', 'Slip', '7', '2', 'D', '3', 'Beam', '275', '600','none' , true),
    ('2380', 'Slip', '7', '2', 'D', '5', 'Beam', '270', '600','none' , true),
    ('2381', 'Slip', '7', '2', 'D', '7', 'Beam', '280', '600','none' , true),
    ('2382', 'Slip', '7', '2', 'D', '9', 'Beam', '275', '600','none' , true),
    ('2383', 'Slip', '7', '2', 'D', '11', 'Beam', '270', '600','none' , true),
    ('2384', 'Slip', '7', '2', 'D', '15', 'Beam', '275', '600','none' , true),
    ('2385', 'Slip', '7', '2', 'D', '17', 'Beam', '275', '600','none' , true),
    ('2386', 'Slip', '7', '2', 'D', '19', 'Beam', '275', '600','none' , true),
    ('2387', 'Slip', '7', '2', 'D', '21', 'Beam', '270', '600','none' , true),
    ('2388', 'Slip', '7', '2', 'D', '27', 'Beam', '270', '600','none' , true),
    ('2389', 'Slip', '7', '3', 'D', '31', 'Beam', '300', '600','none' , true),
    ('2390', 'Slip', '7', '3', 'D', '33', 'Beam', '300', '600','none' , true),
    ('2391', 'Slip', '7', '3', 'D', '35', 'Beam', '300', '600','none' , true),
    ('2392', 'Slip', '7', '3', 'D', '37', 'Beam', '300', '600','none' , true),
    ('2393', 'Slip', '7', '3', 'D', '41', 'Beam', '300', '600','none' , true),
    ('2394', 'Slip', '7', '3', 'D', '45', 'Beam', '300', '600','none' , true),
    ('2395', 'Slip', '7', '3', 'D', '47', 'Beam', '300', '600','none' , true),
    ('2396', 'Slip', '7', '3', 'E', '2', 'Beam', '300', '650','none' , true),
    ('2397', 'Slip', '7', '6', 'E', '3', 'WalkBeam', '500', '1000','none' , true),
    ('2398', 'Slip', '7', '3', 'E', '4', 'Beam', '300', '650','none' , true),
    ('2399', 'Slip', '7', '6', 'E', '5', 'WalkBeam', '500', '1000','none' , true),
    ('2400', 'Slip', '7', '3', 'E', '6', 'WalkBeam', '330', '900','none' , true),
    ('2401', 'Slip', '7', '4', 'E', '8', 'WalkBeam', '380', '900','none' , true),
    ('2402', 'Slip', '7', '6', 'E', '9', 'WalkBeam', '500', '1000','none' , true),
    ('2403', 'Slip', '7', '4', 'E', '10', 'WalkBeam', '380', '900','none' , true),
    ('2404', 'Slip', '7', '6', 'E', '11', 'WalkBeam', '459', '1000','none' , true),
    ('2405', 'Slip', '7', '4', 'E', '12', 'WalkBeam', '380', '900','none' , true),
    ('2406', 'Slip', '7', '5', 'E', '13', 'WalkBeam', '420', '1000','none' , true),
    ('2407', 'Slip', '7', '4', 'E', '14', 'WalkBeam', '380', '900','none' , true),
    ('2408', 'Slip', '7', '5', 'E', '17', 'WalkBeam', '420', '1000','none' , true),
    ('2409', 'Slip', '7', '4', 'E', '18', 'WalkBeam', '380', '900','none' , true),
    ('2410', 'Slip', '7', '4', 'E', '20', 'WalkBeam', '380', '900','none' , true),
    ('2411', 'Slip', '7', '4', 'E', '21', 'WalkBeam', '380', '1000','none' , true),
    ('2412', 'Slip', '7', '4', 'E', '22', 'WalkBeam', '380', '900','none' , true),
    ('2413', 'Slip', '7', '4', 'E', '23', 'WalkBeam', '380', '900','none' , true),
    ('2414', 'Slip', '7', '4', 'E', '24', 'WalkBeam', '380', '900','none' , true),
    ('2415', 'Slip', '7', '4', 'E', '26', 'WalkBeam', '380', '900','none' , true),
    ('2416', 'Slip', '7', '4', 'E', '27', 'WalkBeam', '380', '900','none' , true),
    ('2417', 'Slip', '7', '4', 'E', '28', 'WalkBeam', '380', '900','none' , true),
    ('2418', 'Slip', '7', '4', 'E', '30', 'WalkBeam', '380', '900','none' , true),
    ('2419', 'Slip', '7', '4', 'E', '32', 'WalkBeam', '380', '900','none' , true),
    ('2420', 'Slip', '7', '4', 'E', '34', 'WalkBeam', '380', '900','none' , true),
    ('2421', 'Slip', '7', '4', 'E', '36', 'WalkBeam', '380', '900','none' , true),
    ('2422', 'Slip', '7', '5', 'E', '41', 'WalkBeam', '420', '900','none' , true),
    ('2423', 'Slip', '7', '4', 'E', '42', 'WalkBeam', '380', '900','none' , true),
    ('2424', 'Slip', '7', '4', 'E', '44', 'WalkBeam', '380', '900','none' , true),
    ('2425', 'Slip', '7', '5', 'E', '45', 'WalkBeam', '420', '900','none' , true),
    ('2426', 'Slip', '7', '4', 'E', '46', 'WalkBeam', '380', '900','none' , true),
    ('2427', 'Slip', '7', '5', 'E', '47', 'WalkBeam', '420', '900','none' , true),
    ('2428', 'Slip', '7', '4', 'E', '48', 'WalkBeam', '380', '900','none' , true),
    ('2429', 'Slip', '7', '4', 'E', '52', 'WalkBeam', '380', '900','none' , true),
    ('2430', 'Slip', '7', '4', 'E', '54', 'WalkBeam', '380', '1000','none' , true),
    ('2431', 'Slip', '7', '4', 'E', '58', 'WalkBeam', '380', '1000','none' , true),
    ('2432', 'Slip', '7', '4', 'E', '60', 'WalkBeam', '380', '1000','none' , true),
    ('2433', 'Slip', '7', '4', 'E', '62', 'WalkBeam', '380', '1000','none' , true),
    ('2434', 'Slip', '7', '4', 'E', '64', 'WalkBeam', '380', '1000','none' , true),
    ('2435', 'Slip', '7', '4', 'E', '66', 'WalkBeam', '380', '1000','none' , true),
    ('2436', 'Slip', '7', '3', 'E', '68', 'WalkBeam', '320', '1000','none' , true),
    ('2437', 'Slip', '7', '2', 'Ä', '1', 'Beam', '250', '500','none' , true),
    ('2438', 'Slip', '6', '5', 'POIJU', '1', 'Buoy', '100000', '100000', 'none', true);


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

