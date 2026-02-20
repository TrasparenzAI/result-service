CREATE TABLE IF NOT EXISTS workflows (
    id BIGSERIAL PRIMARY KEY,
    codice_ipa TEXT,
    status TEXT,
    workflow_id TEXT,
    create_time BIGINT,
    update_time BIGINT,
    start_time BIGINT,
    end_time BIGINT,

    created_at TIMESTAMP WITHOUT TIME ZONE,
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    version INT DEFAULT 0);

INSERT INTO workflows
    (workflow_id, create_time, update_time, start_time, end_time, created_at, updated_at, codice_ipa, status, version)
(SELECT
    workflow_id,
    date_part('epoch', min(created_at))::bigint * 1000 as create_time,
    date_part('epoch', max(updated_at))::bigint * 1000 as end_time,
    date_part('epoch', min(created_at))::bigint * 1000 as start_time,
    date_part('epoch', max(updated_at))::bigint * 1000 as end_time,
    min(created_at) created_at,
    max(updated_at) updated_at,
    CASE
        WHEN COUNT(DISTINCT codice_ipa) = 1 THEN MAX(codice_ipa)
        ELSE NULL
    END AS codice_ipa,
'COMPLETED' status,
0 version
from results
group by workflow_id);
