/* V3__extend_tasks_table.sql
   ──────────────────────────
   Zmiana TEXT + dwie nowe kolumny.
   Brak partycjonowania – tylko rozszerzamy schemat.
*/

ALTER TABLE tasks
ALTER COLUMN description TYPE text,
    ADD COLUMN IF NOT EXISTS start_date  date        DEFAULT CURRENT_DATE,
    ADD COLUMN IF NOT EXISTS completed_at timestamp;
