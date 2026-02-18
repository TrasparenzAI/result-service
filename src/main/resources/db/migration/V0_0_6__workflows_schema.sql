CREATE TABLE IF NOT EXISTS workflows (
    id BIGSERIAL PRIMARY KEY,
    codice_ipa TEXT UNIQUE,
    status TEXT,
    workflow_id TEXT,
    create_time INT,
    update_time INT,
    start_time INT,
    end_time INT,

    created_at TIMESTAMP WITHOUT TIME ZONE,
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    version INT DEFAULT 0);
