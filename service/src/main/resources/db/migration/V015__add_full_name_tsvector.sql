ALTER TABLE citizen ADD COLUMN full_name_tsvector tsvector;

UPDATE citizen
SET full_name_tsvector = to_tsvector('simple', CONCAT_WS(' ', first_name, last_name));

CREATE OR REPLACE FUNCTION update_full_name_tsvector() RETURNS trigger AS $$
BEGIN
    NEW.full_name_tsvector := to_tsvector('simple', CONCAT_WS(' ', NEW.first_name, NEW.last_name));
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER tsvectorupdate BEFORE INSERT OR UPDATE
    ON citizen FOR EACH ROW EXECUTE FUNCTION update_full_name_tsvector();
