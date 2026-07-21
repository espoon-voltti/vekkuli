-- Install the 2026-08-02 boat space price list as a dated validity period.
--
-- Replaces the (never-deployed) R__prices repeatable migration: instead of
-- overwriting amounts in place, we keep the current prices as a closed historical
-- period and add the new prices as a new period, so price history is preserved.
--
-- Order matters: the current open rows must be closed BEFORE the new open-ended
-- rows are inserted, otherwise a name would momentarily have two rows extending to
-- infinity and the price_no_overlapping_validity constraint would reject them.

-- 1. Close the current (open) price period the day before the new prices start.
UPDATE price SET end_date = DATE '2026-08-01' WHERE end_date IS NULL;

-- 2. The existing price rows were loaded with explicit ids, so the id sequence
--    may still be behind max(id); advance it before inserting so serial ids don't
--    collide with existing rows. No-op on an empty table (dev/test, where prices
--    are seeded separately): sets the sequence so the next id is 1.
SELECT setval(
    pg_get_serial_sequence('price', 'id'),
    COALESCE((SELECT max(id) FROM price), 1),
    (SELECT max(id) FROM price) IS NOT NULL
);

-- 3. Install the new prices, valid from 2026-08-02 onward (end_date NULL = infinity).
--    Amounts in cents; vat_cents = 25.5% ALV; net_price_cents + vat_cents = price_cents.
INSERT INTO price (name, net_price_cents, vat_cents, price_cents, start_date, end_date) VALUES
    -- Summer berths (laituripaikat)
    ('ML1',            19594,  4996, 24590, DATE '2026-08-02', NULL),
    ('ML2',            23386,  5964, 29350, DATE '2026-08-02', NULL),
    ('ML3',            30239,  7711, 37950, DATE '2026-08-02', NULL),
    ('ML4',            36614,  9336, 45950, DATE '2026-08-02', NULL),
    ('ML5',            39681, 10119, 49800, DATE '2026-08-02', NULL),
    ('ML6',            47904, 12216, 60120, DATE '2026-08-02', NULL),
    -- Buoy berth (poijupaikka)
    ('POIJU',          37530,  9570, 47100, DATE '2026-08-02', NULL),
    -- Talvipaikka (winter)
    ('TSML1',           7100,  1810,  8910, DATE '2026-08-02', NULL),
    ('TSML2',          10554,  2691, 13245, DATE '2026-08-02', NULL),
    ('TSML3',          14724,  3755, 18479, DATE '2026-08-02', NULL),
    ('TSML4',          21737,  5543, 27280, DATE '2026-08-02', NULL),
    ('TSML5',          32595,  8312, 40907, DATE '2026-08-02', NULL),
    ('TSML6',          40897, 10429, 51326, DATE '2026-08-02', NULL),
    ('TSML7',          48120, 12271, 60391, DATE '2026-08-02', NULL),
    ('TSML8',          52064, 13276, 65340, DATE '2026-08-02', NULL),
    -- Ämmäsmäki pukkipaikat (buck storage)
    ('SPML1',           5320,  1357,  6677, DATE '2026-08-02', NULL),
    ('SPML2',           7625,  1944,  9569, DATE '2026-08-02', NULL),
    ('SPML3',          11175,  2850, 14025, DATE '2026-08-02', NULL),
    ('SPML4',          16408,  4184, 20592, DATE '2026-08-02', NULL),
    ('SPML5',          25927,  6611, 32538, DATE '2026-08-02', NULL),
    -- Ämmäsmäki traileripaikat (trailer storage)
    ('SPML1T',          6205,  1582,  7787, DATE '2026-08-02', NULL),
    ('SPML2T',          9405,  2398, 11803, DATE '2026-08-02', NULL),
    ('SPML3T',         13480,  3437, 16917, DATE '2026-08-02', NULL),
    ('SPML4T',         18757,  4783, 23540, DATE '2026-08-02', NULL),
    -- Traileripaikka (trailer space)
    ('TRAILERIPAIKKA', 22447,  5724, 28171, DATE '2026-08-02', NULL);
