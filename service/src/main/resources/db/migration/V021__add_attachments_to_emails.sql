CREATE TABLE attachment (
    key TEXT PRIMARY KEY,
    message_id UUID NOT NULL
        REFERENCES sent_message(id)
            ON DELETE CASCADE,
    created TIMESTAMP DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_attachments_message_id
    ON attachment (message_id);

