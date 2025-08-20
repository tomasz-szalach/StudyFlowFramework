-- user roles
INSERT INTO user_roles (role_id, role_code, role_name) VALUES
    (1, 'USER',  'Użytkownik standardowy')
    ON CONFLICT (role_id) DO NOTHING;

INSERT INTO user_roles (role_id, role_code, role_name) VALUES
    (2, 'ADMIN', 'Administrator')
    ON CONFLICT (role_id) DO NOTHING;

-- task statuses
INSERT INTO task_statuses (status_id, status_code, status_name) VALUES
                                                                    (1, 'TODO',        'Do zrobienia'),
                                                                    (2, 'IN_PROGRESS', 'W trakcie'),
                                                                    (3, 'DONE',        'Zakończone')
    ON CONFLICT (status_id) DO NOTHING;

-- priorities
INSERT INTO priorities (priority_id, priority_code, priority_name) VALUES
                                                                       (1, 'LOW',    'Niski'),
                                                                       (2, 'MEDIUM', 'Średni'),
                                                                       (3, 'HIGH',   'Wysoki')
    ON CONFLICT (priority_id) DO NOTHING;
