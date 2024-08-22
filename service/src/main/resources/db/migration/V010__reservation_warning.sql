CREATE TABLE reservation_warning (
    -- The reservation that this warning is related to
    reservation_id INT NOT NULL,

    -- Key specifies the type of the warning (can be used also for i18n)
    key TEXT NOT NULL,

    -- When was this warning created. Normally this is the time when the reservation
    -- is made or edited.
    created TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),

    -- When was this warning acknowledged (or NULL if not acknowledged yet)
    ack_at TIMESTAMP WITH TIME ZONE DEFAULT NULL,

    -- Who acknowledged this warning (or NULL if not acknowledged yet)
    ack_by UUID DEFAULT NULL,

    -- The note that the user can write when acknowledging the warning
    note TEXT NOT NULL DEFAULT '',

    -- Each reservation has only one warning of each type
    PRIMARY KEY (reservation_id, key),

    FOREIGN KEY (reservation_id) REFERENCES boat_space_reservation(id),
    FOREIGN KEY (ack_by) REFERENCES app_user(id)
);
