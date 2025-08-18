CREATE TABLE attachment (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    key TEXT,
    name TEXT NOT NULL,
    message_id UUID
        REFERENCES sent_message(id)
            ON DELETE CASCADE,
    created TIMESTAMP DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_attachments_message_id
    ON attachment (message_id);

