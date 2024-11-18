CREATE OR REPLACE FUNCTION update_reserver_name()
    RETURNS TRIGGER AS $$
BEGIN
    UPDATE reserver
    SET name = NEW.last_name || ' ' || NEW.first_name
    WHERE id = NEW.id;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;