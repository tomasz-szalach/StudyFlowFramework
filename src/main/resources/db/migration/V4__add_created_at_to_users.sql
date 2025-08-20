-- V4: add created_at to users, fill existing rows, enforce NOT NULL + default
BEGIN;

ALTER TABLE users
    ADD COLUMN IF NOT EXISTS created_at timestamp without time zone;

UPDATE users
SET created_at = CURRENT_TIMESTAMP
WHERE created_at IS NULL;

ALTER TABLE users
    ALTER COLUMN created_at SET NOT NULL,
ALTER COLUMN created_at SET DEFAULT CURRENT_TIMESTAMP;

COMMIT;
