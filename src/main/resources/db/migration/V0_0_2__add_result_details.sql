ALTER TABLE results ADD COLUMN workflow_child_id TEXT;
ALTER TABLE results ADD COLUMN error_message TEXT;
ALTER TABLE results ADD COLUMN length INTEGER;
ALTER TABLE results ADD COLUMN location TEXT;

ALTER TABLE results DROP CONSTRAINT IF EXISTS results_codice_ipa_key;
CREATE UNIQUE INDEX id_ipa_workflow_id_unique_key ON results (id_ipa, workflow_id);