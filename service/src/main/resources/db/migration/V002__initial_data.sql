
INSERT INTO app_user (id, external_id, first_name, last_name, email, is_system_user)
VALUES ('00000000-0000-0000-0000-000000000000', 'api-gw', 'api-gw', 'system-user', NULL, TRUE);

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

INSERT INTO harbor_restriction (location_id, excluded_boat_type) VALUES (1, 'JetSki');
INSERT INTO harbor_restriction (location_id, excluded_boat_type) VALUES (4, 'Sailboat');
INSERT INTO harbor_restriction (location_id, excluded_boat_type) VALUES (7, 'JetSki');
INSERT INTO harbor_restriction (location_id, excluded_boat_type) VALUES (7, 'Sailboat');

INSERT INTO reservation_period(is_espoo_citizen, boat_space_type, operation, start_month, start_day, end_month, end_day)
VALUES
    -- For Espoo citizens
    (true, 'Slip', 'Renew', 1, 7, 1, 31),
    (true, 'Slip', 'Change', 1, 7, 9, 30),
    (true, 'Slip', 'New', 3, 3, 9, 30),
    (true, 'Slip', 'SecondNew', 4, 1, 9, 30),
    (true, 'Trailer', 'Renew', 4, 1, 4, 30),
    (true, 'Trailer', 'New', 5, 1, 12, 31),
    (true, 'Trailer', 'Change',  4, 1, 12, 31),
    (true, 'Winter', 'Renew',  8, 15, 9, 14),
    (true, 'Winter', 'Change',  8, 15, 12, 31),
    (true, 'Winter', 'New', 9, 15, 12, 31),
    (true, 'Winter', 'SecondNew', 9, 15, 12, 31),
    (true, 'Storage', 'Renew',  8, 15, 9, 14),
    (true, 'Storage', 'Change',  8, 15, 8, 14),
    (true, 'Storage', 'New', 9, 15, 9, 14),
    (true, 'Storage', 'SecondNew', 9, 15, 9, 14),

    -- For non-Espoo citizens
    (false, 'Slip', 'New', 4, 1, 9, 30),
    (false, 'Slip', 'Change', 4, 1, 9, 30),
    (false, 'Trailer', 'New', 5, 1, 12, 31),
    (false, 'Trailer', 'Change',  5, 1, 12, 31),
    (false, 'Storage', 'Renew',  8, 15, 9, 14),
    (false, 'Storage', 'Change',  8, 15, 8, 14),
    (false, 'Storage', 'New', 9, 15, 9, 14),
    (false, 'Storage', 'SecondNew', 9, 15, 9, 14);


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

Hej kund,

Den plats du har bokat för din båt, {{placeTypeSv}}, är betald och bokningen har bekräftats.{{citizenReserverSv}}

Hyresgäst:
{{reserverName}}

Platsinformation:
{{harborAddressSv}}
Platsens namn: {{name}}
Platsens bredd: {{width}}
Platsens längd: {{length}}
Platsens utrustning/förvaringssätt: {{amenitySv}}

Din bokning är giltig till {{endDateSv}}.

Om du har bokat en bryggplats får du senare en säsongsetikett och en hamnkarta per post, där du hittar en nyckelkod för att skapa en nyckel till bryggporten (det finns ingen port vid Otsolahti F-bryggan). Säsongsetiketten måste fästas synligt på båten eller, om du har bokat en vinter- eller förvaringsplats, på skyddstältet, trailern eller stöttan.

Om du har bokat en förvaringsplats i Ämmäsmäki:
Uthämtning av tillträdesbrickan ska avtalas i förväg genom att ringa 050 3209 681 vardagar mellan 9-13. Tillträdesbrickan hämtas från Finno hamn (Hylkeenpyytäjäntie 9) genom att visa kvittot.

Hantera dina bokningar, båtar och personuppgifter enkelt på https://varaukset.espoo.fi/kuntalainen/omat-tiedot.

Regler och villkor för båthamnar samt annan information hittar du på https://www.espoo.fi/sv/idrott-motion-och-natur/batliv.

Vänliga hälsningar
Havsnära friluftstjänster
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

If you reserved a dock space, you will receive a season sticker and a harbor map by mail later, containing the key code needed for making a key for the dock gate (Otsolahti F-dock has no gate). The season sticker must be placed visibly on the boat, or if you reserved a winter or storage space, on the protective tent, trailer, or stand.

If you reserved a storage space in Ämmäsmäki:
The pickup of the access badge must be arranged in advance by calling 050 3209 681 on weekdays between 9-13. The badge is picked up from the Suomenoja harbor (Hylkeenpyytäjäntie 9) by showing the payment receipt.

Manage your reservations, boats, and personal details easily at https://varaukset.espoo.fi/kuntalainen/omat-tiedot.

Terms and conditions for boat harbors and additional information can be found at https://www.espoo.fi/en/sports-and-nature/boating.

Best regards
Maritime Outdoor Services
venepaikat@espoo.fi'),

    ('reservation_created_by_employee', 'Espoon kaupungin {{placeTypeFi}}varaus', E'Hyvä asiakas,

Sinulle on varattu Espoon kaupungin {{placeTypeFi}} {{name}}.

Lasku lähetetään osoitteeseen {{invoiceAddressFi}}. Vahvistaaksesi varauksen, maksa paikka eräpäivään mennessä. Maksamaton paikka irtisanoutuu ja se vapautuu muiden varattavaksi. Maksun saavuttua tilillemme lähetämme sähköpostilla lisätietoa laiturin portin avaimesta sekä kausitarran postitse.

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

Hej kund,

Du har reserverat en {{placeTypeSv}} {{name}} från Esbo stad.

Fakturan skickas till {{invoiceAddressSv}}. För att bekräfta bokningen, betala platsen innan förfallodatumet. Om betalningen uteblir annulleras bokningen och platsen blir tillgänglig för andra. När betalningen har mottagits på vårt konto skickar vi ytterligare information om bryggportens nyckel via e-post samt säsongsetiketten per post.

Hyresgäst:
{{reserverName}}

Platsinformation:
{{harborAddressSv}}
Platsens namn: {{name}}
Platsens bredd: {{width}}
Platsens längd: {{length}}
Platsens utrustning/förvaringssätt: {{amenitySv}}

Bokningen gäller till {{endDateSv}}.

Om du har bokat en bryggplats får du senare en säsongsetikett och en hamnkarta per post, där du hittar en nyckelkod för att skapa en nyckel till bryggporten (det finns ingen port vid Otsolahti F-bryggan). Säsongsetiketten måste fästas synligt på båten eller, om du har bokat en vinter- eller förvaringsplats, på skyddstältet, trailern eller stöttan.

Om du har bokat en förvaringsplats i Ämmäsmäki:
Uthämtning av tillträdesbrickan ska avtalas i förväg genom att ringa 050 3209 681 vardagar mellan 9-13. Tillträdesbrickan hämtas från Finno hamn (Hylkeenpyytäjäntie 9) genom att visa kvittot.

Hantera dina bokningar, båtar och personuppgifter enkelt på https://varaukset.espoo.fi/kuntalainen/omat-tiedot.

Regler och villkor för båthamnar samt annan information hittar du på https://www.espoo.fi/sv/idrott-motion-och-natur/batliv.

Vänliga hälsningar
Havsnära friluftstjänster
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

If you reserved a dock space, you will receive a season sticker and a harbor map by mail later, containing the key code needed for making a key for the dock gate (Otsolahti F-dock has no gate). The season sticker must be placed visibly on the boat, or if you reserved a winter or storage space, on the protective tent, trailer, or stand.

If you reserved a storage space in Ämmäsmäki:
The pickup of the access badge must be arranged in advance by calling 050 3209 681 on weekdays between 9-13. The badge is picked up from the Suomenoja harbor (Hylkeenpyytäjäntie 9) by showing the payment receipt.

Manage your reservations, boats, and personal details easily at https://varaukset.espoo.fi/kuntalainen/omat-tiedot.

Terms and conditions for boat harbors and additional information can be found at https://www.espoo.fi/en/sports-and-nature/boating.

Best regards
Maritime Outdoor Services
venepaikat@espoo.fi'),

    ('reservation_created_by_employee_confirmed', 'Vahvistus Espoon kaupungin {{placeTypeFi}}varauksesta', E'Hyvä asiakas,

Sinulle on varattu Espoon kaupungin {{placeTypeFi}} {{name}}. Lähetämme sähköpostilla lisätietoa laiturin portin avaimesta sekä kausitarran postitse.

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

Hej kund,

Du har reserverat en {{placeTypeSv}} {{name}} från Esbo stad. Vi skickar ytterligare information om bryggportens nyckel via e-post samt säsongsetiketten per post.

Hyresgäst:
{{reserverName}}

Platsinformation:
{{harborAddressSv}}
Platsens namn: {{name}}
Platsens bredd: {{width}}
Platsens längd: {{length}}
Platsens utrustning/förvaringssätt: {{amenitySv}}

Din bokning är giltig till {{endDateSv}}.

Om du har bokat en bryggplats får du senare en säsongsetikett och en hamnkarta per post, där du hittar en nyckelkod för att skapa en nyckel till bryggporten (det finns ingen port vid Otsolahti F-bryggan). Säsongsetiketten måste fästas synligt på båten eller, om du har bokat en vinter- eller förvaringsplats, på skyddstältet, trailern eller stöttan.

Om du har bokat en förvaringsplats i Ämmäsmäki:
Uthämtning av tillträdesbrickan ska avtalas i förväg genom att ringa 050 3209 681 vardagar mellan 9-13. Tillträdesbrickan hämtas från Finno hamn (Hylkeenpyytäjäntie 9) genom att visa kvittot.

Hantera dina bokningar, båtar och personuppgifter enkelt på https://varaukset.espoo.fi/kuntalainen/omat-tiedot.

Regler och villkor för båthamnar samt annan information hittar du på https://www.espoo.fi/sv/idrott-motion-och-natur/batliv.

Vänliga hälsningar
Havsnära friluftstjänster
venepaikat@espoo.fi

**************************************************

Dear customer,

You have reserved a {{placeTypeEn}} {{name}} from the City of Espoo. We will send additional information about the dock gate key via email and the season sticker by mail.

Tenant:
{{reserverName}}

Location details:
{{harborAddressEn}}
Name: {{name}}
Width: {{width}}
Length: {{length}}
Amenities/storage type: {{amenityEn}}

Your reservation is valid until {{endDateEn}}.

If you reserved a dock space, you will receive a season sticker and a harbor map by mail later, containing the key code needed for making a key for the dock gate (Otsolahti F-dock has no gate). The season sticker must be placed visibly on the boat, or if you reserved a winter or storage space, on the protective tent, trailer, or stand.

If you reserved a storage space in Ämmäsmäki:
The pickup of the access badge must be arranged in advance by calling 050 3209 681 on weekdays between 9-13. The badge is picked up from the Suomenoja harbor (Hylkeenpyytäjäntie 9) by showing the payment receipt.

Manage your reservations, boats, and personal details easily at https://varaukset.espoo.fi/kuntalainen/omat-tiedot.

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

Hej kund,

Din bokning av {{placeTypeSv}} har betalats och bekräftats för en ny säsong.{{citizenReserverSv}}

Vi skickar säsongsetiketten per post.

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
Havsnära friluftstjänster
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

Manage your reservations, boats, and personal details easily at https://varaukset.espoo.fi/kuntalainen/omat-tiedot.

Terms and conditions for boat harbors and additional information can be found at https://www.espoo.fi/en/sports-and-nature/boating.

Best regards
Maritime Outdoor Services
venepaikat@espoo.fi'),

    ('reservation_renewed_by_employee', 'Espoon kaupungin {{placeTypeFi}}varauksen jatkaminen', E'Hyvä asiakas,

Varaamasi Espoon kaupungin {{placeTypeFi}} on jatkettu uudelle kaudelle.

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

Hej kund,

Din bokning av {{placeTypeSv}} har förlängts för en ny säsong.

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
Havsnära friluftstjänster
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

Manage your reservations, boats, and personal details easily at https://varaukset.espoo.fi/kuntalainen/omat-tiedot.

Terms and conditions for boat harbors and additional information can be found at https://www.espoo.fi/en/sports-and-nature/boating.

Best regards
Maritime Outdoor Services
venepaikat@espoo.fi'),

    ('reservation_renewed_by_employee_confirmed', 'Espoon kaupungin {{placeTypeFi}}varauksen jatkaminen', E'Hyvä asiakas,

Varaamasi Espoon kaupungin {{placeTypeFi}} on jatkettu uudelle kaudelle.

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

Hej kund,

Din bokning av {{placeTypeSv}} har förlängts för en ny säsong.

Vi skickar en ny säsongsetikett per post.

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
Havsnära friluftstjänster
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

Manage your reservations, boats, and personal details easily at https://varaukset.espoo.fi/kuntalainen/omat-tiedot.

Terms and conditions for boat harbors and additional information can be found at https://www.espoo.fi/en/sports-and-nature/boating.

Best regards
Maritime Outdoor Services
venepaikat@espoo.fi'),

    ('reservation_switched_by_citizen', 'Vahvistus Espoon kaupungin {{placeTypeFi}}varauksen vaihdosta', E'Hyvä asiakas,

Olet vaihtanut Espoon kaupungilta vuokraamaasi {{placeTypeFi}}a.

HUOM! Vanha paikka päättyy heti vaihdon yhteydessä ja vene tulee siirtää pois vanhalta paikalta, sillä vanha paikkasi on vapautunut seuraavalle vuokrattavaksi. Venepaikkasi vuokrakausi säilyy ennallaan.

Jos vaihdoit laituripaikkaa:
Jotta saat uuden kausitarran ja avainkoodin uutta paikkaasi varten, ota yhteyttä sähköpostilla venepaikat@espoo.fi tai puhelimitse 09 81658984 ma ja ke klo 12.30-15 ja to 9-11.{{citizenReserverFi}}

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

Hej kund,

Du har bytt din hyrda {{placeTypeSv}} i Esbo stad.

OBS! Din tidigare plats avslutas omedelbart vid bytet, och båten måste flyttas bort eftersom platsen är tillgänglig för en ny hyresgäst. Hyresperioden för din båtplats förblir oförändrad.

Om du bytte bryggplats:
För att få en ny säsongsetikett och nyckelkod till din nya plats, kontakta oss via e-post på venepaikat@espoo.fi eller per telefon 09 81658984 må och ons kl. 12.30-15 och tors kl. 9-11.{{citizenReserverSv}}

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
Havsnära friluftstjänster
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

Manage your reservations, boats, and personal details easily at https://varaukset.espoo.fi/kuntalainen/omat-tiedot.

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

Hej!,

{{placeTypeSv}} {{name}} i Esbo stad har sagts upp.

Uppsägare: {{terminatorName}}

Hyresgäst: {{reserverName}}

Platsen måste vara tom och städad för nästa hyresgäst.

Om du har sagt upp en brygg- eller trailerplats måste den tömmas omedelbart.
Vinter- och Ämmäsmäki-förvaringsplatsen kan användas till slutet av innevarande säsong.

Om du har sagt upp en förvaringsplats i Ämmäsmäki:
Tillträdesbrickan måste returneras till Finno hamn (Hylkeenpyytäjäntie 9).

Om du inte har sagt upp din plats, vänligen kontakta oss via e-post på venepaikat@espoo.fi

Vänliga hälsningar,

Havsnära friluftstjänster
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

    ('reservation_termination_by_citizen_to_employee', 'Espoon kaupungin {{placeTypeFi}} {{name}} irtisanottu, asiakas: {{reserverName}}', E'Hei!,\n\nEspoon kaupungin {{placeTypeFi}} {{name}} on irtisanottu {{time}}\n\nPaikan vuokraaja: {{reserverName}}\nSähköposti: {{reserverEmail}}\n\nIrtisanoja:\nNimi: {{terminatorName}}\nSähköposti: {{terminatorEmail}}\nPuhelinnumero: {{terminatorPhone}}'),

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

Hej kund,

Hyresperioden för {{placeTypeSv}} {{name}} i Esbo stad närmar sig sitt slut {{endDate}}.

Hyresgäst: {{reserverName}}

När hyresperioden är slut måste platsen vara tömd och städad.

Om du har hyrt en förvaringsplats i Ämmäsmäki måste tillträdesbrickan returneras till Finno hamn (Hylkeenpyytäjäntie 9).

Du kan kontrollera bokningsperioder och göra en ny bokning på https://varaukset.espoo.fi.

Vänliga hälsningar
Havsnära friluftstjänster
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
Teet sen helposti osoitteessa https://varaukset.espoo.fi/kuntalainen/omat-tiedot. Löydät kausimaksupainikkeen omista tiedoista paikkavarauksen kohdalta. Maksamiseen tarvitset verkkopankkitunnukset.

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

Voit myös halutessasi vaihtaa nykyisen paikkasi toiseen paikkaan omilla profiilisivuillasi. Paikan vaihto vahvistuu maksamalla uuden paikan kausimaksun.

Venesatamia koskevat sopimusehdot ja säännöt sekä muuta infoa löydät osoitteesta https://www.espoo.fi/fi/liikunta-ja-luonto/veneily.

Terveisin
Merelliset ulkoilupalvelut
venepaikat@espoo.fi

**************************************************

Hej kund,

Det är dags att förnya din bokning för {{placeTypeSv}} inför nästa säsong.

Säkra din plats genom att betala säsongsavgiften.
Du gör det enkelt på https://varaukset.espoo.fi/kuntalainen/omat-tiedot. Du hittar säsongsbetalningsknappen under din bokning. För betalning krävs bankkoder.

Om säsongsavgiften inte har betalats senast {{endDate}}, sägs platsen upp och blir tillgänglig för andra.

Tidsfrister för förnyelse av plats:
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

Du kan också byta din nuvarande plats till en annan via din profilsida. Bytet bekräftas genom att betala säsongsavgiften för den nya platsen.

Regler och villkor för båthamnar samt annan information hittar du på https://www.espoo.fi/sv/idrott-motion-och-natur/batliv.

Vänliga hälsningar
Havsnära friluftstjänster
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

    ('marine_employee_reservation_termination_custom_message', 'Ilmoitus sopimuksen irtisanomisesta', e'Hyvä asiakas,

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

Hej kund,

Hyresavtalet för {{placeTypeSv}} {{name}} har upphört.

Hyresgäst: {{reserverName}}

När hyresperioden är slut måste platsen vara tömd och städad.

Du kan kontrollera bokningsperioder och göra en ny bokning på https://varaukset.espoo.fi.

Vänliga hälsningar
Havsnära friluftstjänster
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
