-- Resolve prices against an application-supplied date instead of the DB clock.
--
-- V025 exposed date-aware price resolution as the `current_price` view, which
-- compared validity periods against Postgres CURRENT_DATE. Because the database
-- runs in a different timezone than the application, the two disagree around
-- midnight and the wrong price period can be served on a cutover day. Replacing
-- the view with a function lets each query pass its own `today` (from the app's
-- TimeProvider) as a parameter. The resolution logic is otherwise identical to
-- the V025 view: follow a price row to its class `name`, then pick the period of
-- that name valid on `as_of`.

DROP VIEW IF EXISTS current_price;

CREATE FUNCTION current_price(as_of date)
    RETURNS TABLE (
        id              integer,
        name            text,
        price_cents     integer,
        vat_cents       integer,
        net_price_cents integer
    )
    LANGUAGE sql
    STABLE
AS $$
    SELECT base.id, base.name, cur.price_cents, cur.vat_cents, cur.net_price_cents
    FROM price base
    JOIN price cur ON cur.name = base.name
        AND as_of <@ daterange(cur.start_date, cur.end_date, '[]')
$$;
