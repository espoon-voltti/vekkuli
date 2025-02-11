
UPDATE email_template SET subject = 'Vahvistus Espoon kaupungin {{placeTypeFi}}varauksesta',
                          body = E'Hyvä asiakas,

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
venepaikat@espoo.fi'
WHERE id = 'reservation_created_by_citizen';

UPDATE email_template SET subject = 'Espoon kaupungin {{placeTypeFi}}varaus',
                          body = E'Hyvä asiakas,

Sinulle on varattu Espoon kaupungin {{placeTypeFi}} {{name}}.

Sinulle on lähetetty lasku osoitteeseen {{invoiceAddress}}. Vahvistaaksesi varauksen, maksa paikka eräpäivään mennessä. Maksamaton paikka irtisanoutuu ja se vapautuu muiden varattavaksi. Maksun saavuttua tilillemme lähetämme sähköpostilla lisätietoa laiturin portin avaimesta sekä kausitarran postitse.

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

En faktura har skickats till adressen {{invoiceAddress}}. För att bekräfta bokningen, betala platsen innan förfallodatumet. Om betalningen uteblir annulleras bokningen och platsen blir tillgänglig för andra. När betalningen har mottagits på vårt konto skickar vi ytterligare information om bryggportens nyckel via e-post samt säsongsetiketten per post.

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

An invoice has been sent to {{invoiceAddress}}. To confirm your booking, please pay for the spot before the due date. If unpaid, the booking will be canceled and made available for others. Once payment is received, we will send additional information about the dock gate key via email and the season sticker by mail.

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
venepaikat@espoo.fi'
WHERE id = 'reservation_created_by_employee';

UPDATE email_template SET subject = 'Vahvistus Espoon kaupungin {{placeTypeFi}}varauksesta',
                          body = E'Hyvä asiakas,

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
venepaikat@espoo.fi'
WHERE id = 'reservation_created_by_employee_confirmed';

UPDATE email_template SET subject = 'Espoon kaupungin {{placeTypeFi}}varauksen jatkaminen',
                          body = E'Hyvä asiakas,

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
venepaikat@espoo.fi'
WHERE id = 'reservation_renewed_by_citizen';

UPDATE email_template SET subject = 'Espoon kaupungin {{placeTypeFi}}varauksen jatkaminen',
                          body = E'Hyvä asiakas,

Varaamasi Espoon kaupungin {{placeTypeFi}} on jatkettu uudelle kaudelle.

Sinulle on lähetetty lasku osoitteeseen {{invoiceAddress}}. Vahvistaaksesi varauksen, maksa paikka eräpäivään mennessä. Maksamaton paikka irtisanoutuu ja se vapautuu muiden varattavaksi.

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

En faktura har skickats till adressen {{invoiceAddress}}. För att bekräfta bokningen, vänligen betala platsen innan förfallodatumet. En obetald plats sägs upp och blir tillgänglig för andra.

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

An invoice has been sent to {{invoiceAddress}}. To confirm your reservation, please pay for the spot before the due date. An unpaid spot will be canceled and made available for others.

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
venepaikat@espoo.fi'
WHERE id = 'reservation_renewed_by_employee';

UPDATE email_template SET subject = 'Espoon kaupungin {{placeTypeFi}}varauksen jatkaminen',
                          body = E'Hyvä asiakas,

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
venepaikat@espoo.fi'
WHERE id = 'reservation_renewed_by_employee_confirmed';

UPDATE email_template SET subject='Vahvistus Espoon kaupungin {{placeTypeFi}}varauksen vaihdosta',
                          body = E'Hyvä asiakas,

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
venepaikat@espoo.fi'
WHERE id = 'reservation_switched_by_citizen';

UPDATE email_template SET subject = 'Vahvistus Espoon kaupungin venepaikan irtisanomisesta',
                          body = E'Hei!,

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
venepaikat@espoo.fi'
WHERE id = 'reservation_termination_by_citizen';

UPDATE email_template SET subject = 'Espoon kaupungin {{placeTypeFi}} {{name}} irtisanottu, asiakas: {{reserverName}}',
                          body = E'Hei!,\n\nEspoon kaupungin {{placeTypeFi}} {{name}} on irtisanottu {{time}}\n\nPaikan vuokraaja: {{reserverName}}\nSähköposti: {{reserverEmail}}\n\nIrtisanoja:\nNimi: {{terminatorName}}\nSähköposti: {{terminatorEmail}}\nPuhelinnumero: {{terminatorPhone}}'
WHERE id = 'reservation_termination_by_citizen_to_employee';

UPDATE email_template SET id = 'fixed_term_reservation_expiring',
                          subject = 'Espoon kaupungin {{placeTypeFi}}varauksesi on päättymässä',
                          body = E'Hyvä asiakas,

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
venepaikat@espoo.fi'
WHERE id = 'boat_reservation_expiry_reminder';

UPDATE email_template SET subject = 'Varmista Espoon kaupungin {{placeTypeFi}}si jatko ensi kaudelle nyt',
                          body = E'Hyvä asiakas,

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
venepaikat@espoo.fi'
WHERE id = 'boat_reservation_renew_reminder';
