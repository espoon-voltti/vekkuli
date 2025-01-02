ALTER TABLE boat_space_reservation
    ADD CONSTRAINT check$start_before_end CHECK ( end_date >= start_date );