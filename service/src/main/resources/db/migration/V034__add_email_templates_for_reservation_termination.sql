INSERT INTO email_template (id, subject, body)
VALUES
    ('reservation_termination_notice_no_refund', 'Venepaikkasi on irtisanottu', E'Hei!,\n\nOlet sanonut irti venepaikkasi {{location}} {{place}}. Sinun on viipymättä poistettava veneesi paikalta.\n\nIrtisanotusta paikasta ei makseta hyvitystä.\n\nYstävällisin terveisin,\n\nMerellinen ulkoilu\nvenepaikat@espoo.fi\n\nTämä on automaattinen viesti, älä vastaa tähän viestiin.'),
    ('reservation_termination_notice_to_employee', 'Venepaikka on irtisanottu', E'Hei!,\n\n{{terminator}} on irtisanonut {{time}} venepaikan {{location}} {{place}}.');
