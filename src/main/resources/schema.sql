CREATE TABLE IF NOT EXISTS clients (
    client_id INTEGER PRIMARY KEY,
    client_type TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS policies (
    policy_id INTEGER PRIMARY KEY,
    client_type TEXT NOT NULL,
    max_requests INTEGER NOT NULL,
    time_window INTEGER NOT NULL
);