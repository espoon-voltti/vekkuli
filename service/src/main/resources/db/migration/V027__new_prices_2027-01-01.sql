UPDATE price SET end_date = '2026-12-31'::date WHERE name = 'ML1' AND end_date IS null;
UPDATE price SET end_date = '2026-12-31'::date WHERE name = 'ML2' AND end_date IS null;
UPDATE price SET end_date = '2026-12-31'::date WHERE name = 'ML3' AND end_date IS null;
UPDATE price SET end_date = '2026-12-31'::date WHERE name = 'ML4' AND end_date IS null;
UPDATE price SET end_date = '2026-12-31'::date WHERE name = 'ML5' AND end_date IS null;
UPDATE price SET end_date = '2026-12-31'::date WHERE name = 'ML6' AND end_date IS null;
UPDATE price SET end_date = '2026-12-31'::date WHERE name = 'ML7' AND end_date IS null;
UPDATE price SET end_date = '2026-12-31'::date WHERE name = 'POIJU' AND end_date IS null;

INSERT INTO PRICE (name, net_price_cents, vat_cents, price_cents, start_date) VALUES
    ('ML1',             20574,   5246,  25820, '2027-01-01'::date),
    ('ML2',             24555,   6262,  30817, '2027-01-01'::date),
    ('ML3',             31751,   8096,  39847, '2027-01-01'::date),
    ('ML4',             38445,   9803,  48248, '2027-01-01'::date),
    ('ML5',             41665,  10625,  52290, '2027-01-01'::date),
    ('ML6',             50299,  12826,  63125, '2027-01-01'::date),
    ('POIJU',           39407,   10048,  49455, '2027-01-01'::date);
