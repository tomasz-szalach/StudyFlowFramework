-- V5: drobne poprawki etykiet i indeksów

BEGIN;

-- 1) priorytety – dodaj kolumnę PL, jeśli brak
ALTER TABLE priorities
    ADD COLUMN IF NOT EXISTS name_pl varchar(64);

-- ustaw etykietę dla priority_id = 2
UPDATE priorities
SET name_pl = 'Średni'
WHERE priority_id = 2
  AND (name_pl IS NULL OR name_pl <> 'Średni');

-- 2) unikalne członkostwa w zadaniach
CREATE UNIQUE INDEX IF NOT EXISTS ux_task_members_task_user
    ON task_members(task_id, user_id);

COMMIT;
