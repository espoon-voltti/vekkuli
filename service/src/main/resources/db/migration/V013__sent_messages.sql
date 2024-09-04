CREATE TYPE MessageType AS ENUM ('Email', 'SMS');

CREATE TYPE MessageStatus AS ENUM ('Queued', 'Sent', 'Failed');

CREATE TABLE sent_message (
    -- Unique identifier for the message
    id UUID PRIMARY KEY DEFAULT uuid_generate_v1mc(),

    -- Id from the provider (in our case an id returned by AWS SES)
    provider_id TEXT DEFAULT NULL,

    -- When was this message put to queue
    created TIMESTAMP NOT NULL DEFAULT now(),

    -- When was this message sent (last try)
    sent_at TIMESTAMP DEFAULT NULL,

    -- Channel used to send message
    type MessageType NOT NULL,

    -- Status of the message. Created messages go first to
    -- Queued state. After sending they are marked as Sent
    status MessageStatus NOT NULL DEFAULT 'Queued',

    -- Sender user of the message (null if automated message)
    sender_id UUID DEFAULT NULL,

    -- Recipient user of the message
    recipient_id UUID NOT NULL,

    -- Recipient address (email or phone number at the time of writing)
    recipient_address TEXT NOT NULL,

    -- Subject of the message
    subject TEXT NOT NULL,

    -- Content of the message
    body TEXT NOT NULL,

    FOREIGN KEY (sender_id) REFERENCES app_user(id),
    FOREIGN KEY (recipient_id) REFERENCES citizen(id)
);
