-- Date-aware price resolution.
--
-- boat_space.price_id points at one specific price row, but a price class (name)
-- can now have several rows across different validity periods (see V025). This
-- view resolves, for the row a boat space points at, the row of the same class
-- (name) that is valid today. Read queries join `current_price` instead of the
-- raw `price` table so a boat space's effective price follows the validity period
-- automatically, without repointing boat_space.price_id.
--
-- `id` is the pointed-at (base) row's id so existing `... price_id = price.id`
-- join conditions keep working unchanged; the amount columns come from the row
-- valid for CURRENT_DATE. Exactly one row per base id (each class has one row
-- valid on any given day, enforced by price_no_overlapping_validity).
--
-- The view deliberately exposes the SAME columns as the `price` table (no
-- start_date/end_date): the validity dates are only used inside the resolution
-- join, and exposing them would make an unqualified `end_date` reference in a
-- joining query ambiguous with boat_space_reservation.end_date.

CREATE VIEW current_price AS
SELECT base.id             AS id,
       base.name           AS name,
       cur.price_cents     AS price_cents,
       cur.vat_cents       AS vat_cents,
       cur.net_price_cents AS net_price_cents
FROM price base
JOIN price cur ON cur.name = base.name
              AND CURRENT_DATE <@ daterange(cur.start_date, cur.end_date, '[]');
