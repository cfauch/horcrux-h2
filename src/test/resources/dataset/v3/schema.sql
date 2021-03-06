-- apply before all other scripts.
CREATE TABLE IF NOT EXISTS horcrux_versions (
    number INT NOT NULL,
    script VARCHAR(64),
    active BOOLEAN
);

-- set to true the current version of the database
MERGE INTO horcrux_versions (number, script, active) KEY(number) VALUES(3, NULL, true);

-- do other tables and constraints creation
CREATE TABLE IF NOT EXISTS horcrux_users (
    id UUID PRIMARY KEY,
    name VARCHAR(64),
    profile VARCHAR(32),
    email VARCHAR(32)
);
