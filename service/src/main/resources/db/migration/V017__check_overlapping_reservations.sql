CREATE OR REPLACE FUNCTION check_reservation_date_overlap()
RETURNS trigger AS $$
BEGIN
  IF NEW.status = 'Confirmed' AND EXISTS (
    SELECT 1
    FROM boat_space_reservation b
    WHERE b.boat_space_id = NEW.boat_space_id
      AND b.status = 'Confirmed'
      AND b.id != COALESCE(NEW.id, -1)
      -- 👇 Exclude reservations from the same chain
      AND NOT (
        NEW.original_reservation_id IS NOT NULL AND NEW.original_reservation_id = b.id
      )
      AND b.start_date <= NEW.end_date
      AND NEW.start_date <= b.end_date
  ) THEN
    RAISE EXCEPTION 'Overlapping Confirmed reservation for reservation %, boat space %', NEW.id, NEW.boat_space_id;
  END IF;

RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER prevent_overlapping_reservations
  BEFORE INSERT OR UPDATE ON boat_space_reservation
    FOR EACH ROW
      EXECUTE FUNCTION check_reservation_date_overlap();