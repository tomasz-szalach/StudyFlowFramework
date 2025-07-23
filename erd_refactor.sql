BEGIN;

------------------------------------------------------------------
-- 1.  RENAME kolumn na unikalne
------------------------------------------------------------------
ALTER TABLE users       RENAME COLUMN id   TO user_id;
ALTER TABLE task_lists  RENAME COLUMN id   TO list_id;
ALTER TABLE task_lists  RENAME COLUMN name TO list_name;
ALTER TABLE tasks       RENAME COLUMN id   TO task_id;
ALTER TABLE tasks       RENAME COLUMN name TO task_name;

------------------------------------------------------------------
-- 2.  NOWE słowniki
------------------------------------------------------------------
CREATE TABLE user_roles (
  role_id     SMALLSERIAL PRIMARY KEY,
  role_code   VARCHAR(20) UNIQUE,
  role_name   VARCHAR(64)
);

INSERT INTO user_roles(role_code, role_name) VALUES
  ('USER',  'Użytkownik standardowy'),
  ('ADMIN', 'Administrator');

ALTER TABLE users
  ADD COLUMN role_id SMALLINT NOT NULL DEFAULT 1,
  ALTER COLUMN role SET DEFAULT NULL;

UPDATE users SET role_id = 2 WHERE role = 'ADMIN';
UPDATE users SET role_id = 1 WHERE role <> 'ADMIN' OR role IS NULL;
ALTER TABLE users DROP COLUMN role;
ALTER TABLE users ALTER COLUMN email SET NOT NULL;

------------------------------------------------------------------
CREATE TABLE priorities(
  priority_id    SMALLSERIAL PRIMARY KEY,
  priority_code  VARCHAR(10) UNIQUE,
  priority_name  VARCHAR(32)
);
INSERT INTO priorities(priority_code,priority_name) VALUES
 ('LOW','Niski'),('MEDIUM','Średni'),('HIGH','Wysoki');

CREATE TABLE task_statuses(
  status_id   SMALLSERIAL PRIMARY KEY,
  status_code VARCHAR(20) UNIQUE,
  status_name VARCHAR(64)
);
INSERT INTO task_statuses(status_code,status_name) VALUES
 ('todo','Do zrobienia'),
 ('in_progress','W trakcie'),
 ('completed','Zakończone'),
 ('archived','Zarchiwizowane');

------------------------------------------------------------------
-- 3.  TASKS: nowe FK + typy
------------------------------------------------------------------
-- zamień string → FK priorytetu
ALTER TABLE tasks ADD COLUMN priority_id SMALLINT;
UPDATE tasks t
SET priority_id = p.priority_id
FROM priorities p
WHERE upper(t.priority) = p.priority_code;
ALTER TABLE tasks ALTER COLUMN priority_id SET NOT NULL;
ALTER TABLE tasks DROP COLUMN priority;
ALTER TABLE tasks
  ADD CONSTRAINT fk_tasks_pri FOREIGN KEY(priority_id)
  REFERENCES priorities(priority_id);

-- status
ALTER TABLE tasks ADD COLUMN status_id SMALLINT;
UPDATE tasks t
SET status_id = s.status_id
FROM task_statuses s
WHERE lower(t.status) = s.status_code;
ALTER TABLE tasks ALTER COLUMN status_id SET NOT NULL;
ALTER TABLE tasks DROP COLUMN status;
ALTER TABLE tasks
  ADD CONSTRAINT fk_tasks_status FOREIGN KEY(status_id)
  REFERENCES task_statuses(status_id);

-- due_date → DATE
ALTER TABLE tasks ALTER COLUMN due_date TYPE DATE
  USING due_date::date;

-- nazwy FK zgodne po rename
ALTER TABLE task_lists  RENAME COLUMN user_id TO owner_id;
ALTER TABLE tasks       RENAME COLUMN user_id TO reporter_id;

------------------------------------------------------------------
-- 4.  M-N „task_members”
------------------------------------------------------------------
CREATE TABLE task_members(
  task_id     BIGINT REFERENCES tasks(task_id) ON DELETE CASCADE,
  user_id     BIGINT REFERENCES users(user_id) ON DELETE RESTRICT,
  role_in_task VARCHAR(20) DEFAULT 'owner',
  PRIMARY KEY(task_id,user_id)
);

------------------------------------------------------------------
-- 5.  Historia
------------------------------------------------------------------
CREATE TABLE task_history (LIKE tasks INCLUDING ALL);
ALTER TABLE task_history ADD COLUMN version_ts timestamptz;
ALTER TABLE task_history ADD COLUMN version_op char(1);

CREATE OR REPLACE FUNCTION f_task_versioning()
RETURNS trigger AS $$
BEGIN
  IF TG_OP IN ('UPDATE','DELETE') THEN
    INSERT INTO task_history
    SELECT OLD.*, now(), substr(TG_OP,1,1);
  END IF;
  RETURN NEW;
END
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_tasks_ver
AFTER UPDATE OR DELETE ON tasks
FOR EACH ROW EXECUTE FUNCTION f_task_versioning();

COMMIT;
