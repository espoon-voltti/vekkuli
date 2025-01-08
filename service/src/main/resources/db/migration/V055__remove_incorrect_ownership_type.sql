ALTER TYPE OwnershipStatus RENAME TO OwnershipStatus_old;
CREATE TYPE OwnershipStatus AS ENUM ('Owner', 'User', 'CoOwner', 'FutureOwner');

-- Update invalid values to a default value
UPDATE boat
SET ownership = 'Owner' -- Replace with your desired default
WHERE ownership::text NOT IN ('Owner', 'User', 'CoOwner', 'FutureOwner');


-- Update the column type
ALTER TABLE boat
    ALTER COLUMN ownership
        SET DATA TYPE OwnershipStatus
        USING ownership::text::OwnershipStatus;

DROP TYPE OwnershipStatus_old;
