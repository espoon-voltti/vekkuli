DELETE FROM reservation_warning;
ALTER TABLE reservation_warning
  DROP COLUMN ack_at,
  DROP COLUMN ack_by,
  DROP COLUMN note,
  ADD COLUMN boat_id INT DEFAULT NULL,
  ADD CONSTRAINT fk_boat_id FOREIGN KEY (boat_id) REFERENCES boat(id),
  DROP CONSTRAINT reservation_warning_pkey,
  ADD PRIMARY KEY (reservation_id, key, boat_id);
