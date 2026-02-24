/*
 * Copyright (C) 2026 Consiglio Nazionale delle Ricerche
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as
 *     published by the Free Software Foundation, either version 3 of the
 *     License, or (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package it.cnr.anac.transparency.result.repositories;

import com.querydsl.core.BooleanBuilder;
import it.cnr.anac.transparency.result.models.QWorkflow;
import it.cnr.anac.transparency.result.models.Workflow;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Optional;

/**
 * DAO per le ricerche sui workflow.
 */
@RequiredArgsConstructor
@Component
public class WorkflowDao {

    private final WorkflowRepository repo;

    private BooleanBuilder findConditions(QWorkflow workflow, Optional<String> workflowId,
                                          Optional<String> codiceIpa,
                                          Optional<Workflow.WorkflowStatus> status,
                                          Optional<LocalDate> createdAfter) {
        BooleanBuilder builder = new BooleanBuilder(workflow.id.isNotNull());
        workflowId.ifPresent(s -> builder.and(workflow.workflowId.eq(s)));
        codiceIpa.ifPresent(s -> builder.and(workflow.codiceIpa.equalsIgnoreCase(s)));
        status.ifPresent(workflowStatus -> builder.and(workflow.status.eq(workflowStatus)));
        createdAfter.ifPresent(localDate -> builder.and(workflow.createdAt.after(localDate.atStartOfDay())));

        return builder;
    }

    public Page<Workflow> find(
            Optional<String> workflowId,
            Optional<String> codiceIpa,
            Optional<Workflow.WorkflowStatus> status,
            Optional<LocalDate> createdAfter,
            Pageable pageable) {
        QWorkflow workflow = QWorkflow.workflow;
        BooleanBuilder conditions =
                findConditions(workflow,
                        workflowId, codiceIpa, status, createdAfter);
        assert conditions.getValue() != null;
        return repo.findAll(conditions.getValue(), pageable);
    }
}
