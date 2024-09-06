CREATE TYPE MemoCategory AS ENUM ('Marine', 'Spaces', 'GroupExercise');

CREATE TABLE citizen_memo (
    -- Unique identifier for the memo
    id Serial PRIMARY KEY,

    -- When was this memo added
    created_at TIMESTAMP NOT NULL DEFAULT now(),

    -- The user that created the memo
    created_by UUID DEFAULT NULL,

    -- When was this message updated
    updated_at TIMESTAMP DEFAULT NULL,

    -- Last user that updated the memo
    updated_by UUID DEFAULT NULL,

    -- Category of the memo entry
    category MemoCategory NOT NULL,

    -- Citizen user that the memo is about
    citizen_id UUID NOT NULL,

    --Content of the message
    content TEXT NOT NULL,

    FOREIGN KEY (created_by) REFERENCES app_user(id),
    FOREIGN KEY (updated_by) REFERENCES app_user(id),
    FOREIGN KEY (citizen_id) REFERENCES citizen(id)
);
