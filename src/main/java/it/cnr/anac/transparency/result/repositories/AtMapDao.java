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
import com.querydsl.jpa.impl.JPAQuery;
import it.cnr.anac.transparency.result.models.AtMap;
import it.cnr.anac.transparency.result.models.QAtMap;
import it.cnr.anac.transparency.result.models.QWorkflow;
import it.cnr.anac.transparency.result.models.Workflow;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Optional;

@RequiredArgsConstructor
@Component
public class AtMapDao {

    private final AtMapRepository repo;

    @PersistenceContext
    private EntityManager entityManager;

    private BooleanBuilder findConditions(QAtMap atMap, Optional<String> workflowId,
                                          Optional<String> codiceIpa,
                                          Optional<Boolean> found,
                                          Optional<LocalDate> createdAfter) {
        BooleanBuilder builder = new BooleanBuilder(atMap.id.isNotNull());
        workflowId.ifPresent(s -> builder.and(atMap.workflowId.eq(s)));
        codiceIpa.ifPresent(s -> builder.and(atMap.company.codiceIpa.equalsIgnoreCase(s)));
        found.ifPresent(b -> builder.and(atMap.found.eq(b)));
        createdAfter.ifPresent(localDate -> builder.and(atMap.createdAt.after(localDate.atStartOfDay())));

        return builder;
    }

    public Page<AtMap> find(
            Optional<String> workflowId,
            Optional<String> codiceIpa,
            Optional<Boolean> found,
            Optional<LocalDate> createdAfter,
            Pageable pageable) {
        QAtMap atMap = QAtMap.atMap;
        BooleanBuilder conditions =
                findConditions(atMap, workflowId, codiceIpa, found, createdAfter);
        assert conditions.getValue() != null;
        return repo.findAll(conditions.getValue(), pageable);
    }

    /**
     * La ATMap individuata più recentemente per la PA passata, considerando solo le scansioni
     * portate a termine correttamente.
     */
    public Optional<AtMap> lastAtMapForCodiceIpa(String codiceIpa) {
        QAtMap atMap = QAtMap.atMap;
        QWorkflow workflow = QWorkflow.workflow;
        JPAQuery<AtMap> query = new JPAQuery<AtMap>(entityManager);
        return Optional.ofNullable(
                query.from(atMap)
                        .join(workflow).on(atMap.workflowId.eq(workflow.workflowId))
                        .where(atMap.company.codiceIpa.equalsIgnoreCase(codiceIpa)
                                .and(workflow.status.eq(Workflow.WorkflowStatus.COMPLETED)))
                        .orderBy(atMap.id.desc()).limit(1)
                        .select(atMap)
                        .fetchFirst());
    }
}
