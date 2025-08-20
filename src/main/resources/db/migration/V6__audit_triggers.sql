-- V6: Audyt zmian – wspólna tabela + triggery na users, task_lists, tasks

CREATE SCHEMA IF NOT EXISTS audit;

CREATE TABLE IF NOT EXISTS audit.change_log (
                                                id          bigserial PRIMARY KEY,
                                                table_name  text        NOT NULL,
                                                op          char(1)     NOT NULL,          -- 'I' / 'U' / 'D'
    changed_at  timestamp   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    actor       text        NOT NULL DEFAULT current_user,  -- nazwa roli DB; opcjonalnie można wstrzykiwać id użytkownika aplikacji
    row_pk      text,
    old_data    jsonb,
    new_data    jsonb
    );

-- Funkcja generyczna; nazwa kolumny PK przekazywana w argumencie wyzwalacza
CREATE OR REPLACE FUNCTION audit.log_with_pk() RETURNS trigger AS $$
DECLARE
pk_col text := TG_ARGV[0];
    pk_val text;
BEGIN
    IF TG_OP = 'INSERT' THEN
        pk_val := (to_jsonb(NEW)->>pk_col);
INSERT INTO audit.change_log(table_name, op, row_pk, new_data)
VALUES (TG_TABLE_NAME, 'I', pk_val, to_jsonb(NEW));
RETURN NEW;

ELSIF TG_OP = 'UPDATE' THEN
        pk_val := (to_jsonb(NEW)->>pk_col);
INSERT INTO audit.change_log(table_name, op, row_pk, old_data, new_data)
VALUES (TG_TABLE_NAME, 'U', pk_val, to_jsonb(OLD), to_jsonb(NEW));
RETURN NEW;

ELSIF TG_OP = 'DELETE' THEN
        pk_val := (to_jsonb(OLD)->>pk_col);
INSERT INTO audit.change_log(table_name, op, row_pk, old_data)
VALUES (TG_TABLE_NAME, 'D', pk_val, to_jsonb(OLD));
RETURN OLD;
END IF;

RETURN NULL;
END;
$$ LANGUAGE plpgsql;

-- Triggery

-- users (PK: user_id)
DROP TRIGGER IF EXISTS trg_audit_users ON users;
CREATE TRIGGER trg_audit_users
    AFTER INSERT OR UPDATE OR DELETE ON users
    FOR EACH ROW EXECUTE FUNCTION audit.log_with_pk('user_id');

-- task_lists (PK: task_list_id)
DROP TRIGGER IF EXISTS trg_audit_task_lists ON task_lists;
CREATE TRIGGER trg_audit_task_lists
    AFTER INSERT OR UPDATE OR DELETE ON task_lists
    FOR EACH ROW EXECUTE FUNCTION audit.log_with_pk('task_list_id');

-- tasks (PK: task_id)
DROP TRIGGER IF EXISTS trg_audit_tasks ON tasks;
CREATE TRIGGER trg_audit_tasks
    AFTER INSERT OR UPDATE OR DELETE ON tasks
    FOR EACH ROW EXECUTE FUNCTION audit.log_with_pk('task_id');
