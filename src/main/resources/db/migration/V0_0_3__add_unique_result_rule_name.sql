DROP INDEX IF EXISTS id_ipa_workflow_id_unique_key;
ALTER TABLE results DROP CONSTRAINT IF EXISTS results_un;
CREATE UNIQUE INDEX id_ipa_workflow_id_rule_name_unique_key ON results (id_ipa, workflow_id, rule_name);