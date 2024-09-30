ALTER TABLE boat RENAME COLUMN citizen_id TO reserver_id;
ALTER TABLE boat DROP CONSTRAINT boat_citizen_id_fkey;
ALTER TABLE boat ADD CONSTRAINT boat_reserver_id_fkey FOREIGN KEY (reserver_id) REFERENCES reserver (id);

ALTER TABLE citizen_memo RENAME COLUMN citizen_id TO reserver_id;
ALTER TABLE citizen_memo DROP CONSTRAINT citizen_memo_citizen_id_fkey;
ALTER TABLE citizen_memo ADD CONSTRAINT citizen_memo_reserver_id_fkey FOREIGN KEY (reserver_id) REFERENCES reserver (id);

ALTER TABLE boat_space_reservation RENAME COLUMN citizen_id TO reserver_id;
ALTER TABLE boat_space_reservation DROP CONSTRAINT boat_space_reservation_citizen_id_fkey;
ALTER TABLE boat_space_reservation ADD CONSTRAINT boat_space_reservation_reserver_id_fkey FOREIGN KEY (reserver_id) REFERENCES reserver (id);

ALTER TABLE sent_message DROP CONSTRAINT sent_message_recipient_id_fkey;
ALTER TABLE sent_message ADD CONSTRAINT sent_message_recipient_id_fkey FOREIGN KEY (recipient_id) REFERENCES reserver (id);

-- Drop unused tables
DROP TABLE boat_space_application_location_wish;
DROP TABLE boat_space_application;
