CREATE TYPE reserver_type AS ENUM ('citizen', 'organization');

CREATE TABLE reserver (
    id UUID PRIMARY KEY,
    type reserver_type NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    updated_at TIMESTAMP,
    name TEXT NOT NULL DEFAULT '',
    email TEXT NOT NULL DEFAULT '',
    phone TEXT NOT NULL DEFAULT '',
    municipality_code INT NOT NULL DEFAULT 1,
    street_address TEXT NOT NULL DEFAULT '',
    street_address_sv TEXT NOT NULL DEFAULT '',
    postal_code TEXT NOT NULL DEFAULT '',
    post_office TEXT NOT NULL DEFAULT '',
    post_office_sv TEXT NOT NULL DEFAULT ''
);

CREATE OR REPLACE FUNCTION check_reserver_type()
    RETURNS TRIGGER AS $$
DECLARE
    reserver_id_value UUID;
BEGIN
    EXECUTE 'SELECT ($1).' || TG_ARGV[1] INTO reserver_id_value USING NEW;
    IF NOT EXISTS (
        SELECT 1 FROM reserver
        WHERE id = reserver_id_value
          AND type = CAST(TG_ARGV[0] AS reserver_type)
    ) THEN
        RAISE EXCEPTION '% must refer to a reserver with type = %', TG_ARGV[1], TG_ARGV[0];
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Trigger for checking organization type
CREATE TRIGGER trigger_check_citizen_type
    BEFORE INSERT OR UPDATE ON citizen
    FOR EACH ROW
EXECUTE FUNCTION check_reserver_type('citizen', 'id');

-- copy data from old citizen table to a new reserver table
INSERT INTO reserver (
    id,
    type,
    created_at,
    updated_at,
    name,
    email ,
    phone,
    municipality_code,
    street_address,
    street_address_sv,
    postal_code,
    post_office,
    post_office_sv
)
SELECT id, 'citizen' AS type, CAST(created as TIMESTAMP) as created_at, CAST(updated as TIMESTAMP) as updated_at,
       CONCAT(first_name, ' ', last_name) as name,
       email, phone,
       municipality_code,
       COALESCE(address, '') as street_address,
       COALESCE(address_sv, '') as street_address_sv,
       postal_code,
       COALESCE(post_office, ''),
       COALESCE(post_office_sv, '')
FROM citizen;

ALTER TABLE citizen
    ADD CONSTRAINT fk_citizen_reserver_id
        FOREIGN KEY (id)
            REFERENCES reserver (id),
    DROP COLUMN updated,
    DROP COLUMN created,
    DROP COLUMN email,
    DROP COLUMN phone,
    DROP COLUMN municipality_code,
    DROP COLUMN address,
    DROP COLUMN address_sv,
    DROP COLUMN postal_code,
    DROP COLUMN post_office,
    DROP COLUMN post_office_sv;

CREATE TABLE organization (
    id UUID PRIMARY KEY REFERENCES reserver(id),
    business_id TEXT NOT NULL DEFAULT '',

    CONSTRAINT fk_organization_reserver_id
        FOREIGN KEY (id) REFERENCES reserver(id)
);

CREATE TRIGGER trigger_check_organization_type
    BEFORE INSERT OR UPDATE ON organization
    FOR EACH ROW
EXECUTE FUNCTION check_reserver_type('organization', 'id');

CREATE TABLE organization_member (
    organization_id UUID NOT NULL REFERENCES reserver(id),
    member_id UUID NOT NULL REFERENCES reserver(id),

    PRIMARY KEY (organization_id, member_id),

    -- Foreign key constraint for organization_id referencing reserver.id
    CONSTRAINT fk_organization_member_organization_id
        FOREIGN KEY (organization_id)
            REFERENCES reserver(id),

    -- Foreign key constraint for member_id referencing reserver.id
    CONSTRAINT fk_organization_member_member_id
        FOREIGN KEY (member_id)
            REFERENCES reserver(id)

);

CREATE TRIGGER trigger_check_organization_member_citizen_type
    BEFORE INSERT OR UPDATE ON organization_member
    FOR EACH ROW
EXECUTE FUNCTION check_reserver_type('citizen', 'member_id');

CREATE TRIGGER trigger_check_organization_member_organization_type
    BEFORE INSERT OR UPDATE ON organization_member
    FOR EACH ROW
EXECUTE FUNCTION check_reserver_type('organization', 'organization_id');

CREATE OR REPLACE FUNCTION update_reserver_name()
    RETURNS TRIGGER AS $$
BEGIN
    UPDATE reserver
    SET name = NEW.first_name || ' ' || NEW.last_name
    WHERE id = NEW.id;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER update_reserver_from_citizen
    AFTER INSERT OR UPDATE OF first_name, last_name
    ON citizen
    FOR EACH ROW
EXECUTE FUNCTION update_reserver_name();
