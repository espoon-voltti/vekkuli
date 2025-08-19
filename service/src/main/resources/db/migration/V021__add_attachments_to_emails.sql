CREATE TABLE attachment (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    key TEXT,
    name TEXT NOT NULL,
    message_id UUID
        REFERENCES sent_message(id)
            ON DELETE CASCADE,
    created TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_attachments_message_id
    ON attachment (message_id);

CREATE UNIQUE INDEX attachment_key_unique_when_message_id_null
    ON attachment (key)
    WHERE message_id IS NULL;
