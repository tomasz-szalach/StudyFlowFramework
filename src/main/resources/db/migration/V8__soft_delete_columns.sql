-- V8: Soft delete dla tasks i task_lists

ALTER TABLE tasks
    ADD COLUMN IF NOT EXISTS deleted_at timestamp;

ALTER TABLE task_lists
    ADD COLUMN IF NOT EXISTS deleted_at timestamp;

-- Częściowe indeksy do szybkich zapytań „aktywnych” rekordów
CREATE INDEX IF NOT EXISTS idx_tasks_active
    ON tasks(task_list_id, status_id)
    WHERE deleted_at IS NULL;

CREATE INDEX IF NOT EXISTS idx_task_lists_active
    ON task_lists(owner_id)
    WHERE deleted_at IS NULL;
