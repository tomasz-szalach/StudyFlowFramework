-- Tworzenie podstawowego schematu pod aplikację

-- 1) Role użytkowników
CREATE TABLE IF NOT EXISTS user_roles (
                                          role_id    SMALLINT PRIMARY KEY,
                                          role_code  VARCHAR(32)  NOT NULL UNIQUE,
    role_name  VARCHAR(255) NOT NULL
    );

-- 2) Statusy zadań
CREATE TABLE IF NOT EXISTS task_statuses (
                                             status_id    SMALLINT PRIMARY KEY,
                                             status_code  VARCHAR(32)  NOT NULL UNIQUE,
    status_name  VARCHAR(255) NOT NULL
    );

-- 3) Priorytety
CREATE TABLE IF NOT EXISTS priorities (
                                          priority_id    SMALLINT PRIMARY KEY,
                                          priority_code  VARCHAR(32)  NOT NULL UNIQUE,
    priority_name  VARCHAR(255) NOT NULL
    );

-- 4) Użytkownicy
CREATE TABLE IF NOT EXISTS users (
                                     user_id     BIGSERIAL PRIMARY KEY,
                                     username    VARCHAR(255) NOT NULL UNIQUE,
    email       VARCHAR(255) NOT NULL UNIQUE,
    password    VARCHAR(255) NOT NULL,
    role_id     SMALLINT     NOT NULL REFERENCES user_roles(role_id),
    first_name  VARCHAR(100),
    last_name   VARCHAR(100)
    );

-- 5) Listy zadań
CREATE TABLE IF NOT EXISTS task_lists (
                                          task_list_id BIGSERIAL PRIMARY KEY,
                                          name         VARCHAR(255) NOT NULL,
    owner_id     BIGINT REFERENCES users(user_id)
    );

-- 6) Zadania
CREATE TABLE IF NOT EXISTS tasks (
                                     task_id      BIGSERIAL PRIMARY KEY,
                                     task_name    VARCHAR(255),
    description  TEXT,
    start_date   DATE DEFAULT CURRENT_DATE,
    due_date     DATE,
    completed_at TIMESTAMP,
    reporter_id  BIGINT REFERENCES users(user_id),
    task_list_id BIGINT REFERENCES task_lists(task_list_id),
    priority_id  SMALLINT NOT NULL REFERENCES priorities(priority_id),
    status_id    SMALLINT NOT NULL REFERENCES task_statuses(status_id)
    );

-- 7) Członkowie zadania (współdzielenie)
CREATE TABLE IF NOT EXISTS task_members (
                                            task_id  BIGINT NOT NULL REFERENCES tasks(task_id) ON DELETE CASCADE,
    user_id  BIGINT NOT NULL REFERENCES users(user_id),
    PRIMARY KEY (task_id, user_id)
    );

-- Indeksy pomocnicze (opcjonalnie, ale warto)
CREATE INDEX IF NOT EXISTS idx_tasks_task_list_id ON tasks(task_list_id);
CREATE INDEX IF NOT EXISTS idx_tasks_reporter_id  ON tasks(reporter_id);
CREATE INDEX IF NOT EXISTS idx_tasks_status_id    ON tasks(status_id);
CREATE INDEX IF NOT EXISTS idx_tasks_priority_id  ON tasks(priority_id);
CREATE INDEX IF NOT EXISTS idx_tasks_due_date     ON tasks(due_date);
