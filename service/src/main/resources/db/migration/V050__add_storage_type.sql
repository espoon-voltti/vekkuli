CREATE TYPE StorageType AS ENUM ('None', 'Trailer', 'Buck', 'BuckWithTent');

ALTER TABLE boat_space_reservation ADD COLUMN storage_type StorageType DEFAULT 'None';
