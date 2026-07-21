-- Canonical boat space price amounts (repeatable migration).
--
-- Edit the amounts below; Flyway re-applies them on the next deploy whenever this
-- file's checksum changes. Only existing rows are UPDATEd, matched by `name` (the
-- price table's natural key), so ids stay stable and boat_space.price_id
-- assignments are untouched. A name with no matching row is simply skipped, which
-- is expected in dev/test/CI: this migration runs at startup against an empty
-- price table, and the test seed (seed.sql) populates prices independently.
--
-- Categories: TSML1-8 = Talvipaikka (winter), SPML1-5 = Ämmäsmäki pukkipaikat
-- (buck storage), SPML1T-4T = Ämmäsmäki traileripaikat (trailer storage),
-- TRAILERIPAIKKA = trailer space. Amounts are in cents; vat_cents = 25.5% ALV and
-- net_price_cents + vat_cents = price_cents for every row.

UPDATE price AS p
SET net_price_cents = v.net_price_cents,
    vat_cents       = v.vat_cents,
    price_cents     = v.price_cents
FROM (
    VALUES
         -- Summer berths (laituripaikat)
         ('ML1',            19594,  4996, 24590),
         ('ML2',            23386,  5964, 29350),
         ('ML3',            30239,  7711, 37950),
         ('ML4',            36614,  9336, 45950),
         ('ML5',            39681, 10119, 49800),
         ('ML6',            47904, 12216, 60120),
         -- Buoy berth (poijupaikka)
         ('POIJU',          37530,  9570, 47100),
         -- Talvipaikka (winter)
        ('TSML1',           7100,  1810,  8910),
        ('TSML2',          10554,  2691, 13245),
        ('TSML3',          14724,  3755, 18479),
        ('TSML4',          21737,  5543, 27280),
        ('TSML5',          32595,  8312, 40907),
        ('TSML6',          40897, 10429, 51326),
        ('TSML7',          48120, 12271, 60391),
        ('TSML8',          52064, 13276, 65340),
        -- Ämmäsmäki pukkipaikat (buck storage)
        ('SPML1',           5320,  1357,  6677),
        ('SPML2',           7625,  1944,  9569),
        ('SPML3',          11175,  2850, 14025),
        ('SPML4',          16408,  4184, 20592),
        ('SPML5',          25927,  6611, 32538),
        -- Ämmäsmäki traileripaikat (trailer storage)
        ('SPML1T',          6205,  1582,  7787),
        ('SPML2T',          9405,  2398, 11803),
        ('SPML3T',         13480,  3437, 16917),
        ('SPML4T',         18757,  4783, 23540),
        -- Traileripaikka (trailer space)
        ('TRAILERIPAIKKA', 22447,  5724, 28171)
) AS v(name, net_price_cents, vat_cents, price_cents)
WHERE p.name = v.name;
