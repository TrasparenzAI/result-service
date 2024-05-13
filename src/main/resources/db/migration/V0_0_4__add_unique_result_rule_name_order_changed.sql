DROP INDEX id_ipa_workflow_id_rule_name_unique_key;
CREATE UNIQUE INDEX id_ipa_workflow_id_rule_name_unique_key ON results(workflow_id, rule_name, id_ipa);