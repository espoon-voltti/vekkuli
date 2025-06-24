UPDATE email_template
SET subject = 'Vahvistus Espoon kaupungin {{placeTypeFi}}varauksesta',
    body    = E'Hyvä asiakas,

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

Jos varasit laituripaikan, saat myöhemmin postissa kausitarran ja avainkoodin laiturin portin avaimen teettämistä varten (Otsolahden F-laiturille ei ole porttia). Kausitarra tulee kiinnittää näkyvälle paikalle veneeseen tai jos varasit talvi- tai säilytyspaikan, suojatelttaan, traileriin tai pukkiin.

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

Om du har bokat en bryggplats får du senare per post ett säsongsklistermärke för din båt och en nyckelkod för att tillverka en nyckel till bryggans port (obs! Ingen port till F-bryggan i Björnviken). Klistermärket ska fästas synligt på båten eller, om du har bokat en vinter- eller förvaringsplats, på skyddstältet, trailern eller bocken.

Om du har bokat en förvaringsplats i Käringbacken:
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

If you reserved a dock space, you will later receive a season sticker and a key code for making a key for the dock gate by mail (Otsolahti F-dock has no gate). The season sticker must be placed visibly on the boat, or if you reserved a winter or storage space, on the protective tent, trailer or stand.

If you reserved a storage space in Ämmäsmäki:
The pickup of the access badge must be arranged in advance by calling 050 3209 681 on weekdays between 9-13. The badge is picked up from the Suomenoja harbor (Hylkeenpyytäjäntie 9) by showing the payment receipt.

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

Jos varasit laituripaikan, saat myöhemmin postissa kausitarran ja avainkoodin laiturin portin avaimen teettämistä varten (Otsolahden F-laiturille ei ole porttia). Kausitarra tulee kiinnittää näkyvälle paikalle veneeseen tai jos varasit talvi- tai säilytyspaikan, suojatelttaan, traileriin tai pukkiin.

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

Om du har bokat en bryggplats får du senare per post ett säsongsklistermärke för din båt och en nyckelkod för att tillverka en nyckel till bryggans port (obs! Ingen port till F-bryggan i Björnviken). Klistermärket ska fästas synligt på båten eller, om du har bokat en vinter- eller förvaringsplats, på skyddstältet, trailern eller bocken.

Om du har bokat en förvaringsplats i Käringbacken:
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

If you reserved a dock space, you will receive a season sticker and a key code by mail later for making a key for the dock gate (Otsolahti F-dock has no gate). The season sticker must be placed visibly on the boat, or if you reserved a winter or storage space, on the protective tent, trailer or stand.

If you reserved a storage space in Ämmäsmäki:
The pickup of the access badge must be arranged in advance by calling 050 3209 681 on weekdays between 9-13. The badge is picked up from the Suomenoja harbor (Hylkeenpyytäjäntie 9) by showing the payment receipt.

Manage your reservations, boats and personal details easily at https://varaukset.espoo.fi/kuntalainen/omat-tiedot.

Terms and conditions for boat harbors and additional information can be found at https://www.espoo.fi/en/sports-and-nature/boating.

Best regards
Maritime Outdoor Services
venepaikat@espoo.fi'
WHERE id = 'reservation_created_by_employee';


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

Jos varasit laituripaikan, saat myöhemmin postissa kausitarran ja avainkoodin laiturin portin avaimen teettämistä varten (Otsolahden F-laiturille ei ole porttia). Kausitarra tulee kiinnittää näkyvälle paikalle veneeseen tai jos varasit talvi- tai säilytyspaikan, suojatelttaan, traileriin tai pukkiin.

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

Om du har bokat en bryggplats får du senare per post ett säsongsklistermärke för din båt och en nyckelkod för att tillverka en nyckel till bryggans port (obs! Ingen port till F-bryggan i Björnviken). Klistermärket ska fästas synligt på båten eller, om du har bokat en vinter- eller förvaringsplats, på skyddstältet, trailern eller bocken.

Om du har bokat en förvaringsplats i Käringbacken:
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

If you reserved a dock space, you will receive a season sticker and a key code by mail later for making a key for the dock gate (Otsolahti F-dock has no gate). The season sticker must be placed visibly on the boat, or if you reserved a winter or storage space, on the protective tent, trailer or stand.

If you reserved a storage space in Ämmäsmäki:
The pickup of the access badge must be arranged in advance by calling 050 3209 681 on weekdays between 9-13. The badge is picked up from the Suomenoja harbor (Hylkeenpyytäjäntie 9) by showing the payment receipt.

Manage your reservations, boats and personal details easily at https://varaukset.espoo.fi/kuntalainen/omat-tiedot.

Terms and conditions for boat harbors and additional information can be found at https://www.espoo.fi/en/sports-and-nature/boating.

Best regards
Maritime Outdoor Services
venepaikat@espoo.fi'
WHERE id = 'reservation_created_by_employee_confirmed';
