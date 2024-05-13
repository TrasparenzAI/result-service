CREATE INDEX workflow_id_results_key ON results(workflow_id);
CREATE INDEX codice_ipa_results_key ON results(codice_ipa);
CREATE INDEX codice_ipa_lower_results_key ON results(lower(codice_ipa));
CREATE INDEX denominazione_ente_lower_results_key ON results(lower(denominazione_ente));
CREATE INDEX codice_fiscale_ente_lower_results_key ON results(lower(codice_fiscale_ente));
CREATE INDEX codice_categoria_lower_results_key ON results(lower(codice_categoria));
CREATE INDEX status_results_key ON results(status);