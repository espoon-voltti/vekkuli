ALTER TABLE price
    ADD COLUMN vat_cents integer NOT NULL DEFAULT 0,
    ADD COLUMN net_price_cents integer NOT NULL DEFAULT 0;

UPDATE price SET net_price_cents = 17823, vat_cents = 4545, price_cents = 22367 WHERE name = 'ML1';
UPDATE price SET net_price_cents = 21290, vat_cents = 5429, price_cents = 26719 WHERE name = 'ML2';
UPDATE price SET net_price_cents = 27500, vat_cents = 7012, price_cents = 34512 WHERE name = 'ML3';
UPDATE price SET net_price_cents = 33306, vat_cents = 8493, price_cents = 41800 WHERE name = 'ML4';
UPDATE price SET net_price_cents = 36048, vat_cents = 9193, price_cents = 45241 WHERE name = 'ML5';
UPDATE price SET net_price_cents = 43548, vat_cents = 11105, price_cents = 54000 WHERE name = 'ML6';
