-- V9: Widoki z ujednoliconymi nazwami do natural join

CREATE OR REPLACE VIEW vw_task_lists_natural AS
SELECT
    task_list_id           AS id,
    task_list_id,
    name,
    owner_id               AS user_id,
    deleted_at
FROM task_lists;

CREATE OR REPLACE VIEW vw_tasks_natural AS
SELECT
    task_id                AS id,
    task_id,
    task_list_id,
    reporter_id            AS user_id,
    task_name,
    description,
    start_date,
    due_date,
    completed_at,
    priority_id,
    status_id,
    deleted_at
FROM tasks;

-- Przykład użycia:
-- SELECT * FROM vw_tasks_natural NATURAL JOIN vw_task_lists_natural;
