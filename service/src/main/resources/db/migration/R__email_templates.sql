UPDATE email_template
SET subject = 'Vahvistus Espoon kaupungin {{placeTypeFi}}varauksesta',
    body    = E'Hyvä asiakas,

Vesikulkuneuvolle varaamasi {{placeTypeFi}} on maksettu ja varaus on vahvistettu.{{citizenReserverFi}}

Vuokralainen:
{{reserverName}}

Paikan tiedot:
{{harborAddressFi}}
Paikan nimi: {{name}}
Paikan leveys: {{width}}
Paikan pituus: {{length}}
Paikan varuste/säilytystapa: {{amenityFi}}

Varauksesi on voimassa {{endDateFi}}.

Saat myöhemmin postissa kausitarran ja varausvahvistuksen, jossa avainkoodi laiturin portin avaimen teettämistä varten (Otsolahden F-laiturille ei ole porttia).

Jos varasit säilytyspaikan Ämmäsmäeltä:
Ämmäsmäen kulkulätkän noudosta tulee sopia ennakkoon soittamalla numeroon p. 040 634 3463 arkisin kello 8-14. Kulkulätkä noudetaan Suomenojan satamasta (Hylkeenpyytäjäntie 9) maksukuittia näyttämällä.

Hallinnoi varauksiasi, vesikulkuneuvojasi ja omia tietojasi helposti osoitteessa https://varaukset.espoo.fi/kuntalainen/omat-tiedot.

Venesatamia koskevat sopimusehdot ja säännöt sekä muuta infoa löydät osoitteesta https://www.espoo.fi/fi/liikunta-ja-luonto/veneily.

Terveisin
Merelliset ulkoilupalvelut
venepaikat@espoo.fi

**************************************************

Bästa kund,

Den plats du har bokat för din vattenfarkost är betald och bokningen har bekräftad.{{citizenReserverSv}}

Hyresgäst:
{{reserverName}}

Platsinformation:
{{harborAddressSv}}
Platsens namn: {{name}}
Platsens bredd: {{width}}
Platsens längd: {{length}}
Platsens utrustning/förvaringssätt: {{amenitySv}}

Din bokning är {{endDateSv}}.

Du får senare per post en bokningsbekräftelse och ett säsongsklistermärke. I bokningsbekräftelsen finns en nyckelkod som du behöver för att tillverka en portnyckel till bryggan (brygga F i Björnviken har ingen port).

Om du har bokat en förvaringsplats i Käringbacken:
Upphämtning av passernyckeln till Käringbacken, ring 040 634 3463 vardagar kl. 8-14. Passernyckeln hämtas från Finno hamn (Säljägarvägen 9) mot uppvisande av betalningskvitto.

Hantera dina bokningar, båtar och personuppgifter enkelt på https://varaukset.espoo.fi/kuntalainen/omat-tiedot.

Regler och villkor för båthamnar samt annan information hittar du på https://www.espoo.fi/sv/idrott-motion-och-natur/batliv.

Vänliga hälsningar
Marina friluftstjänster
venepaikat@espoo.fi

**************************************************

Dear customer,

The place you reserved for your watercraft has been paid, and the reservation has been confirmed.{{citizenReserverEn}}

Tenant:
{{reserverName}}

Location details:
{{harborAddressEn}}
Name: {{name}}
Width: {{width}}
Length: {{length}}
Amenities/storage type: {{amenityEn}}

Your reservation is valid {{endDateEn}}.

You will later receive by mail a reservation confirmation and a season sticker, which includes the key code for having a key made for the pier gate (note: there is no gate at Otsolahti’s Fpier).

If you reserved a storage place in Ämmäsmäki:
The pickup of the access badge must be arranged in advance by calling 04040 634 3463 on weekdays between 8-14. The badge is picked up from the Suomenoja harbor (Hylkeenpyytäjäntie 9) by showing the payment receipt.

Manage your reservations, boats and personal details easily at https://varaukset.espoo.fi/kuntalainen/omat-tiedot.

Terms and conditions for boat harbors and additional information can be found at https://www.espoo.fi/en/sports-and-nature/boating.

Best regards
Maritime Outdoor Services
venepaikat@espoo.fi'
WHERE id = 'reservation_created_by_citizen';

UPDATE email_template
SET subject = 'Espoon kaupungin {{placeTypeFi}}varaus',
    body    = E'Hyvä asiakas,

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

Jos varasit laituripaikan, saat myöhemmin postissa kausitarran ja satamakartan, jossa avainkoodi laiturin portin avaimen teettämistä varten (Otsolahden F-laiturille ei ole porttia). Kausitarra tulee kiinnittää näkyvälle paikalle vesikulkuneuvoon tai jos varasit talvi- tai säilytyspaikan, suojatelttaan, traileriin tai pukkiin.

Jos varasit säilytyspaikan Ämmäsmäeltä:
Ämmäsmäen kulkulätkän noudosta tulee sopia ennakkoon soittamalla numeroon p. 046 877 3668 (Toimela) tai p. 044 566 8690 (Pohjala) arkisin kello 8-14. Kulkulätkä noudetaan Suomenojan satamasta (Hylkeenpyytäjäntie 9) maksukuittia näyttämällä.

Hallinnoi varauksiasi, vesikulkuneuvojasi ja omia tietojasi helposti osoitteessa https://varaukset.espoo.fi/kuntalainen/omat-tiedot.

Venesatamia koskevat sopimusehdot ja säännöt sekä muuta infoa löydät osoitteesta https://www.espoo.fi/fi/liikunta-ja-luonto/veneily.

Terveisin
Merelliset ulkoilupalvelut
venepaikat@espoo.fi

**************************************************

Bästa kund,

Vi har bokat en {{placeTypeSv}} {{name}} från Esbo stad till dig.

Fakturan skickas till {{invoiceAddressSv}}. För att bekräfta bokningen, betala platsen senast på förfallodatumet. Om betalningen uteblir annulleras bokningen och platsen blir tillgänglig för andra. När betalningen har mottagits på vårt konto skickar vi information om nyckeln till bryggans port samt klistermärket för din båt per post.

Hyresgäst:
{{reserverName}}

Platsinformation:
{{harborAddressSv}}
Platsens namn: {{name}}
Platsens bredd: {{width}}
Platsens längd: {{length}}
Platsens utrustning/förvaringssätt: {{amenitySv}}

Bokningen gäller {{endDateSv}}.

Om du har bokat en båtplats får du senare per post ett sänsongklistermärke för din båt och en hamnkarta med nyckelkodsuppgifter för att tillverka en nyckel till bryggans port (obs! Ingen port till F-bryggan i Björnviken). Klistermärket ska fästas synligt på båten eller, om du har bokat en vinter- eller förvaringsplats, på skyddstältet, trailern eller bocken.

Om du har bokat en förvaringsplats i Käringbacken:
För att få tillgång till en elektronisk portnyckel ska man i förväg komma överrens om upphämtning av nyckeln på telefonnummer 046 877 3668 (Toimela) eller 044 566 8690 (Pohjala) vardagar mellan 8-14. Upphämtning av nyckeln från Finno hamn (Hylkeenpyytäjäntie 9) får du genoma att visa kvittot på den betalda platsen.

Hantera dina bokningar, båtar och personuppgifter enkelt på https://varaukset.espoo.fi/kuntalainen/omat-tiedot.

Regler och villkor för båthamnar samt annan information hittar du på https://www.espoo.fi/sv/idrott-motion-och-natur/batliv.

Vänliga hälsningar
Marina friluftstjänster
venepaikat@espoo.fi

**************************************************

Dear customer,

You have reserved a {{placeTypeEn}} {{name}} from the City of Espoo.

The invoice will be sent to {{invoiceAddressEn}}. To confirm your booking, please pay for the place before the due date. If unpaid, the booking will be canceled and made available for others. Once payment is received, we will send additional information about the dock gate key via email and the season sticker by mail.

Tenant:
{{reserverName}}

Location details:
{{harborAddressEn}}
Name: {{name}}
Width: {{width}}
Length: {{length}}
Amenities/storage type: {{amenityEn}}

Your reservation is valid {{endDateEn}}.

If you reserved a dock place, you will receive a season sticker and a harbor map by mail later, containing the key code needed for making a key for the dock gate (Otsolahti F-dock has no gate). The season sticker must be placed visibly on the boat, or if you reserved a winter or storage place, on the protective tent, trailer or stand.

If you reserved a storage place in Ämmäsmäki:
The collection of the access tag for Ämmämäki must be arranged in advance by calling tel. 040 634 3463 on weekdays between 8:00 and 14:00. The access tag is collected from the Suomenoja harbor (Hylkeenpyytäjäntie 9) upon presentation of the payment receipt.

Manage your reservations, boats and personal details easily at https://varaukset.espoo.fi/kuntalainen/omat-tiedot.

Terms and conditions for boat harbors and additional information can be found at https://www.espoo.fi/en/sports-and-nature/boating.

Best regards
Maritime Outdoor Services
venepaikat@espoo.fi'
WHERE id = 'reservation_created_by_employee';

UPDATE email_template
SET subject = 'Espoon kaupungin {{placeTypeFi}}varauksen jatkaminen',
    body= E'Hyvä asiakas,

Vesikulkuneuvolle varaamasi {{placeTypeFi}} on maksettu ja varaus on vahvistettu uudelle kaudelle.{{citizenReserverFi}}

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

Hallinnoi varauksiasi, vesikulkuneuvojasi ja omia tietojasi helposti osoitteessa https://varaukset.espoo.fi/kuntalainen/omat-tiedot.

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

The place you reserved for your watercraft has been paid, and the reservation has been confirmed for the new season.{{citizenReserverEn}}

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
venepaikat@espoo.fi'
WHERE id = 'reservation_renewed_by_citizen';

UPDATE email_template
SET subject = 'Espoon kaupungin {{placeTypeFi}}varauksen jatkaminen',
    body= E'Hyvä asiakas,

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

Hallinnoi varauksiasi, vesikulkuneuvojasi ja omia tietojasi helposti osoitteessa https://varaukset.espoo.fi/kuntalainen/omat-tiedot.

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

The invoice will be sent to {{invoiceAddressEn}}. To confirm your reservation, please pay for the place before the due date. An unpaid place will be canceled and made available for others.

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
venepaikat@espoo.fi'
WHERE id = 'reservation_renewed_by_employee';

UPDATE email_template
SET subject = 'Espoon kaupungin {{placeTypeFi}}varauksen jatkaminen',
    body= E'Hyvä asiakas,

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

Hallinnoi varauksiasi, vesikulkuneuvojasi ja omia tietojasi helposti osoitteessa https://varaukset.espoo.fi/kuntalainen/omat-tiedot.

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
venepaikat@espoo.fi'
WHERE id = 'reservation_renewed_by_employee_confirmed';

UPDATE email_template
SET subject = 'Vahvistus Espoon kaupungin {{placeTypeFi}}varauksen vaihdosta',
    body= E'Hyvä asiakas,

Olet vaihtanut Espoon kaupungilta vuokraamaasi {{placeTypeFi}}a.

HUOM! Vanha paikka päättyy heti vaihdon yhteydessä ja vesikulkuneuvo tulee siirtää pois vanhalta paikalta, sillä vanha paikkasi on vapautunut seuraavalle vuokrattavaksi. Venepaikkasi vuokrakausi säilyy ennallaan.

Saat myöhemmin postissa kausitarran ja varausvahvistuksen, jossa avainkoodi laiturin portin avaimen teettämistä varten (Otsolahden F-laiturille ei ole porttia).{{citizenReserverFi}}

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

Hallinnoi varauksiasi, vesikulkuneuvojasi ja omia tietojasi helposti osoitteessa https://varaukset.espoo.fi/kuntalainen/omat-tiedot.

Venesatamia koskevat sopimusehdot ja säännöt sekä muuta infoa löydät osoitteesta https://www.espoo.fi/fi/liikunta-ja-luonto/veneily.

Terveisin
Merelliset ulkoilupalvelut
venepaikat@espoo.fi

**************************************************

Bästa kund,

Du har bytt din hyrda {{placeTypeSv}} i Esbo stad.

OBS! Den gamla platsen upphör genast i samband med bytet och vattenfarkosten ska flyttas från den gamla platsen, eftersom den gamla platsen har blivit ledig att hyras ut till nästa kund. Hyresperioden för din båtplats förblir oförändrad.

Du får senare per post en bokningsbekräftelse och ett säsongsklistermärke. I bokningsbekräftelsen finns en nyckelkod som du behöver för att tillverka en portnyckel till bryggan (brygga F i Björnviken har ingen port).{{citizenReserverSv}}

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

You have changed your rented place in the City of Espoo.

NOTICE! The previous place ends immediately upon the change, and the watercraft must be removed from the old place, as it has been released for rental to the next user. Your rental period remains unchanged.

You will later receive by mail a reservation confirmation and a season sticker, which includes the key code for having a key made for the pier gate (note: there is no gate at Otsolahti’s F pier).

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
venepaikat@espoo.fi'
WHERE id = 'reservation_switched_by_citizen';

UPDATE email_template
SET subject = 'Vahvistus Espoon kaupungin venepaikan irtisanomisesta',
    body= E'Hei,

Espoon kaupungin {{placeTypeFi}} {{name}} on irtisanottu.

Irtisanoja: {{terminatorName}}

Paikan vuokraaja: {{reserverName}}

Paikan tulee olla tyhjä ja siivottu seuraavaa vuokralaista varten.

Oikeus paikkaan on irtisanomisen myötä päättynyt ja paikka tulee tyhjentää välittömästi.

Jos irtisanoit Ämmäsmäen säilytyspaikan:
Ämmäsmäen kulkulätkä tulee palauttaa Suomenojan satamaan (Hylkeenpyytäjäntie 9).

Terveisin,

Merelliset ulkoilupalvelut
venepaikat@espoo.fi

**************************************************

Hej,

{{placeTypeSv}} {{name}} i Esbo stad har sagts upp.

Uppsägare: {{terminatorName}}

Hyresgäst: {{reserverName}}

Platsen måste vara tom och städad för nästa hyresgäst.

Rätten till platsen upphör i och med uppsägningen och platsen ska tömmas omedelbart.

Om du har sagt upp en förvaringsplats i Käringbacken:
Elektroniska nyckeln måste returneras till Finno hamn (Hylkeenpyytäjäntie 9).

Vänliga hälsningar,

Marina friluftstjänster
venepaikat@espoo.fi

**************************************************

Hello,

The {{placeTypeEn}} {{name}} in the City of Espoo has been terminated.

Terminator: {{terminatorName}}

Tenant: {{reserverName}}

The place must be empty and cleaned for the next tenant.

Upon termination, the right to the place has ceased, and the premises must be vacated without delay.

If you have terminated a storage place in Ämmäsmäki:
The access badge must be returned to the Finno harbor (Hylkeenpyytäjäntie 9).

Best regards,

Maritime Outdoor Services
venepaikat@espoo.fi'
WHERE id = 'reservation_termination_by_citizen';

UPDATE email_template
SET subject = 'Espoon kaupungin {{placeTypeFi}} {{name}} irtisanottu, asiakas: {{reserverName}}',
    body= E'Hei,

Espoon kaupungin {{placeTypeFi}} {{name}} on irtisanottu {{time}}

Paikan vuokraaja: {{reserverName}}
Sähköposti: {{reserverEmail}}

Irtisanoja:
Nimi: {{terminatorName}}
Sähköposti: {{terminatorEmail}}
Puhelinnumero: {{terminatorPhone}}'
WHERE id = 'reservation_termination_by_citizen_to_employee';

UPDATE email_template
SET subject = 'Vahvistus Espoon kaupungin {{placeTypeFi}}varauksesta',
    body= E'Hyvä asiakas,

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

Jos varasit laituripaikan, saat myöhemmin postissa kausitarran ja satamakartan, jossa avainkoodi laiturin portin avaimen teettämistä varten (Otsolahden F-laiturille ei ole porttia). Kausitarra tulee kiinnittää näkyvälle paikalle vesikulkuneuvoon tai jos varasit talvi- tai säilytyspaikan, suojatelttaan, traileriin tai pukkiin.

Jos varasit säilytyspaikan Ämmäsmäeltä:
Ämmäsmäen kulkulätkän noudosta tulee sopia ennakkoon soittamalla numeroon  p. 046 877 3668 (Toimela) tai p. 044 566 8690 (Pohjala) arkisin kello 8-14. Kulkulätkä noudetaan Suomenojan satamasta (Hylkeenpyytäjäntie 9) maksukuittia näyttämällä.

Hallinnoi varauksiasi, vesikulkuneuvojasi ja omia tietojasi helposti osoitteessa https://varaukset.espoo.fi/kuntalainen/omat-tiedot.

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

Du får senare per post en bokningsbekräftelse och ett säsongsklistermärke. I bokningsbekräftelsen finns en nyckelkod som du behöver för att tillverka en portnyckel till bryggan (brygga F i Björnviken har ingen port). Säsongsklistermärket ska fästas på ett synligt ställe på vattenfarkosten eller på vinter-, trailer- eller förvaringsplatsen på skyddstält, trailer eller bock.

Om du har bokat en förvaringsplats i Käringbacken:
Upphämtning av passernyckeln till Käringbacken, ring 040 634 3463 vardagar kl. 8–14. Passernyckeln hämtas från Finno hamn (Säljägarvägen 9) mot uppvisande av betalningskvitto.

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

If you reserved a dock place, you will receive a season sticker and a harbor map by mail later, containing the key code needed for making a key for the dock gate (Otsolahti F-dock has no gate). The season sticker must be placed visibly on the boat, or if you reserved a winter or storage place, on the protective tent, trailer or stand.

If you reserved a storage place in Ämmäsmäki:
The pickup of the access badge must be arranged in advance by calling 046 877 3668 (Toimela) or 044 566 8690 (Pohjala) on weekdays between 8-14. The badge is picked up from the Suomenoja harbor (Hylkeenpyytäjäntie 9) by showing the payment receipt.

Manage your reservations, boats and personal details easily at https://varaukset.espoo.fi/kuntalainen/omat-tiedot.

Terms and conditions for boat harbors and additional information can be found at https://www.espoo.fi/en/sports-and-nature/boating.

Best regards
Maritime Outdoor Services
venepaikat@espoo.fi'
WHERE id = 'reservation_created_by_employee_confirmed';

UPDATE email_template
SET subject = 'Espoon kaupungin {{placeTypeFi}}varauksesi on päättymässä',
    body= E'Hyvä asiakas,

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

Om du har hyrt en förvaringsplats i Käringbacken måste elektroniska returneras till Finno hamn (Hylkeenpyytäjäntie 9).

Du kan kontrollera bokningsperioder och göra en ny bokning på https://varaukset.espoo.fi.

Vänliga hälsningar
Marina friluftstjänster
venepaikat@espoo.fi

**************************************************

Dear customer,

Your reservation for {{placeTypeEn}} {{name}} in the City of Espoo is coming to an end on {{endDate}}.

Tenant: {{reserverName}}

Once the rental period ends, the place must be emptied and cleaned.

If you rented a storage place in Ämmäsmäki, the access badge must be returned to the Finno harbor (Hylkeenpyytäjäntie 9).

You can check reservation periods and make a new reservation at https://varaukset.espoo.fi.

Best regards
Maritime Outdoor Services
venepaikat@espoo.fi'
WHERE id = 'fixed_term_reservation_expiring';

UPDATE email_template
SET subject = 'Varmista Espoon kaupungin {{placeTypeFi}}si jatko ensi kaudelle nyt',
    body= E'Hyvä asiakas,

On aika jatkaa {{placeTypeFi}}si varausta ensi kaudelle.

Varmistat paikkasi varauksen maksamalla kausimaksun.
Teet sen helposti osoitteessa https://varaukset.espoo.fi/kuntalainen/omat-tiedot. Kausimaksun voit maksaa oman varauksen kautta. Maksamiseen tarvitset verkkopankkitunnukset.

Jos kausimaksua ei ole maksettu {{endDate}} mennessä, paikka irtisanoutuu ja vapautuu.

Määräajat paikan jatkamiselle:
Laituripaikka: 11.–31.1.
Traileripaikka: 8.–30.4.
Talvipaikka: 23.8.–14.9.
Säilytyspaikka Ämmäsmäellä: 23.8.–14.9.

Jatkettava paikka: {{placeTypeFi}} {{name}}

Paikan vuokraaja: {{reserverName}}

Paikan tiedot:
{{harborAddressFi}}
Paikan nimi: {{name}}
Paikan leveys: {{width}}
Paikan pituus: {{length}}
Paikan varuste/säilytystapa: {{amenityFi}}

Huomioi, mikäli kotikuntasi ei enää ole Espoo, et välttämättä ole oikeutettu jatkamaan paikkaasi.

Jos kausimaksun maksaminen ei onnistu, ota yhteyttä sähköpostilla venepaikat@espoo.fi tai puhelimitse 09 81658984. Puhelinajat löytyvät verkkosivuiltamme.

Voit myös halutessasi vaihtaa nykyisen paikkasi toiseen paikkaan omien tietojen kautta. Paikan vaihto on mahdollista vasta kausimaksun maksamisen jälkeen.

Venesatamia koskevat sopimusehdot ja säännöt sekä muuta infoa löydät osoitteesta https://www.espoo.fi/fi/liikunta-ja-luonto/veneily.

Terveisin
Merelliset ulkoilupalvelut
venepaikat@espoo.fi

**************************************************

Bästa kund,

Det är dags att förnya bokning för din {{placeTypeSv}} inför nästa säsong.

Säkra din plats genom att betala säsongsavgiften.
Du gör det enkelt på https://varaukset.espoo.fi/kuntalainen/omat-tiedot. Du kan förnya din plats via din bokning. För betalning krävs bankkoder.

Om säsongsavgiften inte har betalats senast {{endDate}}, sägs platsen upp och blir tillgänglig för andra.

Tider för förnyelse av plats:
Båtplats: 11.–31.1.
Trailerplats: 8.–30.4.
Vinterplats: 23.8.–14.9.
Förvaringsplats i Käringbacken: 23.8.–14.9.

Plats att förnya: {{placeTypeSv}} {{name}}

Hyresgäst: {{reserverName}}

Platsinformation:
{{harborAddressSv}}
Platsens namn: {{name}}
Platsens bredd: {{width}}
Platsens längd: {{length}}
Platsens utrustning/förvaringssätt: {{amenitySv}}

Observera att om din hemkommun inte längre är Esbo kan du eventuellt inte förnya din plats.

Om säsongsavgiften inte kan betalas, kontakta oss via e-post på venepaikat@espoo.fi eller per telefon 09 81658984. Telefontider finns på vår webbplats.

Du kan också byta din nuvarande plats till en annan via dina egna sidor. Du kan byta plats först efter att du betalat din nuvarande plats.

Regler och villkor för båthamnar samt annan information hittar du på https://www.espoo.fi/sv/idrott-motion-och-natur/batliv.

Vänliga hälsningar
Marina friluftstjänster
venepaikat@espoo.fi

**************************************************

Dear customer,

It is time to renew the reservation for your {{placeTypeEn}} for the next season.

Secure your place by paying the seasonal fee.
You can do this easily at https://varaukset.espoo.fi/kuntalainen/omat-tiedot. You will find the seasonal payment button under your reservation details. Online banking credentials are required for payment.

If the seasonal fee is not paid by {{endDate}}, your place will be canceled and made available for others.

Deadlines for renewing a place:
Dock place: 11.–31.1.
Trailer place: 8.–30.4.
Winter storage: 23.8.–14.9.
Storage place in Ämmäsmäki: 23.8.–14.9.

Place to be renewed: {{placeTypeEn}} {{name}}

Tenant: {{reserverName}}

Location details:
{{harborAddressEn}}
Name: {{name}}
Width: {{width}}
Length: {{length}}
Amenities/storage type: {{amenityEn}}

Please note that if your home municipality is no longer Espoo, you may not be eligible to renew your place.

If you are unable to make the payment, please contact us via email at venepaikat@espoo.fi or by phone at 09 81658984. Phone hours can be found on our website.

You may change your current place to another place through your personal account. A change of place is only possible after the seasonal fee has been paid.

Terms and conditions for boat harbors and additional information can be found at https://www.espoo.fi/en/sports-and-nature/boating.

Best regards
Maritime Outdoor Services
venepaikat@espoo.fi'
WHERE id = 'boat_reservation_renew_reminder';

UPDATE email_template
SET subject = 'Ilmoitus sopimuksen irtisanomisesta',
    body= E'Hyvä asiakas,

Venepaikka: {{harbor}} {{place}} on irtisanottu virkailijan toimesta.

Irtisanominen astuu voimaan *xx.xx.xxxx*.
Irtisanomisen syy: *xxxxxx*

Pyydämme teitä ystävällisesti siirtämään vesikulkuneuvonne pois nykyiseltä paikaltaan *xx.xx.xxxx* mennessä.

Mikäli teillä on kysyttävää, ota yhteyttä sähköpostilla {{employeeEmail}} tai puhelimitse 09 81658984 ma ja ke klo 12.30-15 ja to 9-11.

Terveisin
Merellinen ulkoilu
{{employeeEmail}}'
WHERE id = 'marine_employee_reservation_termination_custom_message';

UPDATE email_template
SET subject = 'Espoon kaupungin venepaikan vuokrasopimus on päättynyt',
    body= E'Hyvä asiakas,

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

Once the rental period ends, the place must be emptied and cleaned.

You can check reservation periods and make a new reservation at https://varaukset.espoo.fi.

Best regards
Maritime Outdoor Services
venepaikat@espoo.fi'
WHERE id = 'expired_reservation';

UPDATE email_template
SET subject = 'Säilytyspaikan {{name}} vuokrasopimus on päättynyt, asiakas: {{reserverName}}',
    body= E'Säilytyspaikan {{name}} vuokrasopimus on päättynyt {{endDate}}, asiakas ei ole maksanut kausimaksua uudelle kaudelle eräpäivään mennessä.

Asiakas:
{{reserverName}}

Sähköposti: {{reserverEmail}}'
WHERE id = 'storage_place_expired_to_employee';
