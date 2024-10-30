INSERT INTO email_template (id, subject, body)
VALUES
    ('marine_reservation_termination_employee_notice', 'Venepaikka on irtisanottu', E'Hei!,\n\n{{terminator}} on irtisanonut {{time}} venepaikan {{location}} {{place}}.');

ALTER TABLE sent_message ALTER recipient_id DROP NOT NULL;
