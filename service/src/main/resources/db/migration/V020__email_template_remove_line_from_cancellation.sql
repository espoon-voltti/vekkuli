-- Remove the line about contacting via email if the place has not been terminated by the citizen ---
UPDATE email_template
SET subject = 'Vahvistus Espoon kaupungin venepaikan irtisanomisesta',
    body= E'Hei!,

Espoon kaupungin {{placeTypeFi}} {{name}} on irtisanottu.

Irtisanoja: {{terminatorName}}

Paikan vuokraaja: {{reserverName}}

Paikan tulee olla tyhjä ja siivottu seuraavaa vuokralaista varten.

Jos irtisanoit laituri- tai traileripaikan, tulee se tyhjentää välittömästi.
Talvi- ja Ämmäsmäen säilytyspaikan voit pitää vielä kuluvan kauden loppuun asti.

Jos irtisanoit Ämmäsmäen säilytyspaikan:
Ämmäsmäen kulkulätkä tulee palauttaa Suomenojan satamaan (Hylkeenpyytäjäntie 9).

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
Vinter- och Käringbacken-förvaringsplats kan användas till slutet av pågående säsong.

Om du har sagt upp en förvaringsplats i Käringbacken:
Elektroniska nyckeln måste returneras till Finno hamn (Hylkeenpyytäjäntie 9).

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

Best regards,

Maritime Outdoor Services
venepaikat@espoo.fi'
WHERE id = 'reservation_termination_by_citizen';
