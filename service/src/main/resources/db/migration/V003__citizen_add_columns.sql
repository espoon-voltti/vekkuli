ALTER TABLE citizen
    ADD COLUMN national_id text NOT NULL DEFAULT '',
    ADD COLUMN updated timestamp with time zone DEFAULT NULL,
    ADD COLUMN created timestamp with time zone NOT NULL DEFAULT now();
CREATE UNIQUE INDEX uniq$citizen$national_id ON citizen(national_id);
