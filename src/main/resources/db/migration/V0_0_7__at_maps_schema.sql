CREATE TABLE IF NOT EXISTS at_maps (
    id BIGSERIAL PRIMARY KEY,

    id_ipa BIGINT,
    codice_ipa TEXT,
    denominazione_ente TEXT,
    codice_fiscale_ente TEXT,
    tipologia TEXT,
    codice_categoria TEXT,
    codice_natura TEXT,
    acronimo TEXT,
    sito_istituzionale TEXT,
    sorgente TEXT,

    workflow_id TEXT,
    url TEXT,
    found BOOLEAN NOT NULL DEFAULT FALSE,
    status INTEGER,
    content TEXT,
    format TEXT,
    discovered_by TEXT,
    valid BOOLEAN,
    error_message TEXT,

    created_at TIMESTAMP WITHOUT TIME ZONE,
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    version INT DEFAULT 0,

    CONSTRAINT at_maps_workflow_id_codice_ipa_key UNIQUE (workflow_id, codice_ipa));

CREATE INDEX workflow_id_at_maps_key ON at_maps(workflow_id);
CREATE INDEX codice_ipa_at_maps_key ON at_maps(codice_ipa);
CREATE INDEX codice_ipa_lower_at_maps_key ON at_maps(lower(codice_ipa));
