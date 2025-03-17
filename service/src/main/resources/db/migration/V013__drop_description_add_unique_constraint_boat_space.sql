ALTER TABLE boat_space DROP COLUMN description;

/* Add constrain that boat space can have only unique locationId, place number and section combinations. */
ALTER TABLE boat_space ADD CONSTRAINT boat_space_unique_location_place_section UNIQUE (type, location_id, place_number, section);
