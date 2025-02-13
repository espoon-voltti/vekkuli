-- noinspection SqlWithoutWhereForFile

INSERT INTO email_template (id, subject, body)
VALUES
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
venepaikat@espoo.fi');


INSERT INTO email_template (id, subject, body)
VALUES
    ('storage_place_expired_to_employee', 'Säilytyspaikan {{name}} vuokrasopimus on päättynyt, asiakas: {{reserverName}}', E'Säilytyspaikan {{name}} vuokrasopimus on päättynyt {{endDate}}, asiakas ei ole maksanut kausimaksua uudelle kaudelle eräpäivään mennessä.\n\nAsiakas:\n{{reserverName}}
\nSähköposti: {{reserverEmail}}');