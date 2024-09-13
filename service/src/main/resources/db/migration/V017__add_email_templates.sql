
CREATE TABLE email_template (
    id VARCHAR(128) PRIMARY KEY,
    subject TEXT NOT NULL,
    body TEXT NOT NULL
);

-- Insert this template already in migration, because it is already used in the code
INSERT INTO email_template (id, subject, body)
VALUES
  ('varausvahvistus', 'Varausvahvistus: uuden laituripaikan varaaminen', 'Hyvä asiakas,\n\nSinulle on varattu alla oleva venepaikka:\n\nVenepaikan nimi {{name}}\nVenepaikan leveys {{width}}\nVenepaikan pituus {{length}}\nVenepaikan varustus {{amenity}}\nVarauksen voimassaolo päättyy {{endDate}}')
