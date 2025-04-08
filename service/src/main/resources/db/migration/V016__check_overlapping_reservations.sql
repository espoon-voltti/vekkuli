CREATE OR REPLACE FUNCTION check_reservation_date_overlap()
RETURNS trigger AS $$
BEGIN
  IF EXISTS (
    SELECT 1
    FROM boat_space_reservation
    WHERE boat_space_id = NEW.boat_space_id
      AND id != COALESCE(NEW.id, -1)
      AND status = 'Confirmed'
      AND NEW.status = 'Confirmed'
      AND start_date <= NEW.end_date
      AND NEW.start_date <= end_date
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