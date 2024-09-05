CREATE TYPE MemoCategory AS ENUM ('Marine', 'Room', 'Groups');

CREATE TABLE citizen_memo (
    -- Unique identifier for the memo
    id Serial PRIMARY KEY,

    -- When was this memo added
    created TIMESTAMP NOT NULL DEFAULT now(),

    -- When was this message updated
    updated TIMESTAMP DEFAULT NULL,

    -- Category of the memo entry
    category MemoCategory NOT NULL,

    -- The user that created the memo
    user_id UUID DEFAULT NULL,

    -- Citizen user that the memo is about
    citizen_id UUID NOT NULL,

    --Content of the message
    content TEXT NOT NULL,

    FOREIGN KEY (user_id) REFERENCES app_user(id),
    FOREIGN KEY (citizen_id) REFERENCES citizen(id)
);
