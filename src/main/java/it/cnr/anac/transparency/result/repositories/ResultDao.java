/*
 * Copyright (C) 2025 Consiglio Nazionale delle Ricerche
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
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import it.cnr.anac.transparency.result.config.RuleCategoryProperties;
import it.cnr.anac.transparency.result.models.*;
import it.cnr.anac.transparency.result.v1.dto.CategoryValueDto;
import it.cnr.anac.transparency.result.v1.dto.RuleDto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * DAO per le ricerche sui risultati di validazione.
 *
 */
@RequiredArgsConstructor
@Component
public class ResultDao {

    public final static String RESULTS_CACHE_NAME = "results";
    public final static String RESULTS_GROUPED_BY_CACHE_NAME = "resultsGroupedBy";

    private final ResultRepository repo;
    private final RuleCategoryProperties ruleCategoryProperties;

    @PersistenceContext
    private EntityManager entityManager;

    public Optional<Result> lastResult() {
        QResult result = QResult.result;
        JPAQuery<Result> query = new JPAQuery<Result>(entityManager);
        return Optional.ofNullable(
                query.from(result)
                        .orderBy(result.id.desc()).limit(1)
                        .select(result)
                        .fetchFirst());
    }

    public Optional<Result> lastResultForCodiceIpa(String codiceIpa) {
        QResult result = QResult.result;
        QWorkflow workflow = QWorkflow.workflow;
        JPAQuery<Result> query = new JPAQuery<Result>(entityManager);
        return Optional.ofNullable(
                query.from(result)
                        .join(workflow).on(result.workflowId.eq(workflow.workflowId))
                        .where(result.company.codiceIpa.eq(codiceIpa).and(workflow.status.eq(Workflow.WorkflowStatus.COMPLETED)))
                        .orderBy(result.id.desc()).limit(1)
                        .select(result)
                        .fetchFirst());
    }

    public List<CategoryValueDto> countResultsAndGroupByCategoriesWidthWorkflowIdAndStatus(
            String workflowId,
            List<Integer> status
    ) {
        return ruleCategoryProperties
                .getRules()
                .stream()
                .map(ruleDto ->  new CategoryValueDto(
                        ruleDto,
                        countResults(
                                workflowId,
                                status,
                                ruleDto.getMin(),
                                ruleDto.getMax()
                        )
                ))
                .collect(Collectors.toList());
    }

    private Integer countResults(String workflowId, List<Integer> status, Integer min, Integer max) {
        QResult result = QResult.result;
        JPAQuery<Company> queryTotal = new JPAQuery<Company>(entityManager)
                .select(result.company)
                .from(result)
                .where(result.workflowId.eq(workflowId)
                        .and(result.status.in(status)))
                .groupBy(result.company)
                .having(result.count().goe(min)
                        .and(result.count().loe(max)));
        return queryTotal.fetch().size();
    }

    public Page<Result> findCompaniesByWorkflowAndStatusWithOccurencesBetween(
            String workflowId, List<Integer> status,
            Integer minNumberOfRules, Integer maxNumberOfRules,
            String denominazioneEnte, String codiceFiscaleEnte,
            String codiceIpa, String codiceCategoria,
            Pageable pageable
    ) {
        QResult result = QResult.result;
        JPAQuery<Company> query = new JPAQuery<Company>(entityManager)
                .select(result.company)
                .from(result)
                .where(result.workflowId.eq(workflowId)
                        .and(result.status.in(status)))
                .groupBy(result.company)
                .having(result.count().goe(minNumberOfRules)
                        .and(result.count().loe(maxNumberOfRules)));
        if (denominazioneEnte != null) query.where(result.company.denominazioneEnte.containsIgnoreCase(denominazioneEnte));
        if (codiceFiscaleEnte != null) query.where(result.company.codiceFiscaleEnte.containsIgnoreCase(codiceFiscaleEnte));
        if (codiceIpa != null) query.where(result.company.codiceIpa.eq(codiceIpa));
        if (codiceCategoria != null) query.where(result.company.codiceCategoria.eq(codiceCategoria));

        query.offset(pageable.getOffset());
        query.limit(pageable.getPageSize());

        List<Result> content = query.fetch().stream().map(Result::new).collect(Collectors.toList());
        // Per contare il totale dei gruppi, bisogna usare il campo per cui si raggruppa
        // in una lista e contarne la dimensione.
        JPAQuery<Company> queryTotal = new JPAQuery<Company>(entityManager)
                .select(result.company)
                .from(result)
                .where(result.workflowId.eq(workflowId)
                        .and(result.status.in(status)))
                .groupBy(result.company)
                .having(result.count().goe(minNumberOfRules)
                        .and(result.count().loe(maxNumberOfRules)));
        // Esegui la query completa senza paginazione
        if (denominazioneEnte != null) queryTotal.where(result.company.denominazioneEnte.containsIgnoreCase(denominazioneEnte));
        if (codiceFiscaleEnte != null) queryTotal.where(result.company.codiceFiscaleEnte.containsIgnoreCase(codiceFiscaleEnte));
        if (codiceIpa != null) queryTotal.where(result.company.codiceIpa.eq(codiceIpa));
        if (codiceCategoria != null) queryTotal.where(result.company.codiceCategoria.eq(codiceCategoria));
        long total = queryTotal.fetch().size();

        return new PageImpl<>(content, pageable, total);
    }

    @Cacheable(RESULTS_GROUPED_BY_CACHE_NAME)
    public List<ResultCount> countAndGroupByWorkflowIdAndStatusWithCache(String ruleName, List<String> workflowId) {
        return countAndGroupByWorkflowIdAndStatus(ruleName, workflowId);
    }

    public List<ResultCount> countAndGroupByWorkflowIdAndStatus(String ruleName, List<String> workflowId) {
        QResult result = QResult.result;
        JPAQuery<Result> query = new JPAQuery<Result>(entityManager);
        if (Optional.ofNullable(ruleName).filter(string -> !string.isEmpty()).isPresent()) {
            query = query.where(new BooleanBuilder(result.ruleName.eq(ruleName)));
        }
        if (Optional.ofNullable(workflowId).filter(strings -> !strings.isEmpty()).isPresent()) {
            query = query.where(new BooleanBuilder(result.workflowId.in(workflowId)));
        }
        return query
                .from(result)
                .where(result.workflowId.isNotNull().and(result.status.isNotNull()))
                .groupBy(result.workflowId, result.status)
                .select(Projections.constructor(
                        ResultCount.class,
                        result.workflowId,
                        result.status,
                        result.count()
                ))
                .fetch();
    }

    private BooleanBuilder findConditions(QResult result, Optional<Long> idIpa,
                                          Optional<String> codiceCategoria, Optional<String> codiceFiscaleEnte,
                                          Optional<String> codiceIpa, Optional<String> denominazioneEnte,
                                          Optional<String> ruleName,
                                          Optional<Boolean> isLeaf,
                                          Optional<Integer> status, Optional<String> workflowId,
                                          Optional<LocalDate> createdAfter) {
        BooleanBuilder builder = new BooleanBuilder(result.id.isNotNull());
        if (idIpa.isPresent()) {
            builder.and(result.company.idIpa.eq(idIpa.get()));
        }
        if (codiceCategoria.isPresent()) {
            builder.and(result.company.codiceCategoria.equalsIgnoreCase(codiceCategoria.get()));
        }
        if (codiceFiscaleEnte.isPresent()) {
            builder.and(result.company.codiceFiscaleEnte.equalsIgnoreCase(codiceFiscaleEnte.get()));
        }
        if (codiceIpa.isPresent()) {
            builder.and(result.company.codiceIpa.equalsIgnoreCase(codiceIpa.get()));
        }
        if (denominazioneEnte.isPresent()) {
            builder.and(result.company.denominazioneEnte.containsIgnoreCase(denominazioneEnte.get()));
        }
        if (ruleName.isPresent()) {
            builder.and(result.ruleName.eq(ruleName.get()));
        }
        if (workflowId.isPresent()) {
            builder.and(result.workflowId.eq(workflowId.get()));
        }
        if (isLeaf.isPresent()) {
            builder.and(result.isLeaf.eq(isLeaf.get()));
        }
        if (status.isPresent()) {
            builder.and(result.status.eq(status.get()));
        }
        if (createdAfter.isPresent()) {
            builder.and(result.createdAt.after(createdAfter.get().atStartOfDay()));
        }
        return builder;
    }

    @Cacheable(RESULTS_CACHE_NAME)
    public Page<Result> findWithCache(
            Optional<Long> idIpa,
            Optional<String> codiceCategoria, Optional<String> codiceFiscaleEnte,
            Optional<String> codiceIpa, Optional<String> denominazioneEnte,
            Optional<String> ruleName,
            Optional<Boolean> isLeaf,
            Optional<Integer> status, Optional<String> workflowId,
            Optional<LocalDate> createdAfter,
            Pageable pageable) {
        return find(idIpa, codiceCategoria, codiceFiscaleEnte, codiceIpa, denominazioneEnte,
                ruleName, isLeaf, status, workflowId, createdAfter, pageable);
    }

    public Page<Result> find(
            Optional<Long> idIpa,
            Optional<String> codiceCategoria, Optional<String> codiceFiscaleEnte,
            Optional<String> codiceIpa, Optional<String> denominazioneEnte,
            Optional<String> ruleName,
            Optional<Boolean> isLeaf,
            Optional<Integer> status, Optional<String> workflowId,
            Optional<LocalDate> createdAfter,
            Pageable pageable) {
        QResult result = QResult.result;
        BooleanBuilder conditions =
                findConditions(result,
                        idIpa, codiceCategoria, codiceFiscaleEnte, codiceIpa, denominazioneEnte,
                        ruleName, isLeaf, status, workflowId, createdAfter);
        return repo.findAll(conditions.getValue(), pageable);
    }

    public List<Result> find(Optional<Long> idIpa,
                             Optional<String> codiceCategoria, Optional<String> codiceFiscaleEnte,
                             Optional<String> codiceIpa, Optional<String> denominazioneEnte,
                             Optional<String> ruleName,
                             Optional<Boolean> isLeaf,
                             Optional<Integer> status, Optional<String> workflowId,
                             Optional<LocalDate> createdAfter, Sort sort) {
        QResult result = QResult.result;
        BooleanBuilder conditions =
                findConditions(result,
                        idIpa, codiceCategoria, codiceFiscaleEnte, codiceIpa, denominazioneEnte, ruleName,
                        isLeaf, status, workflowId, createdAfter);
        return StreamSupport.stream(repo.findAll(conditions.getValue(), sort).spliterator(), false)
                .collect(Collectors.toList());
    }

    public List<StorageData> storageDataByWorkflowId(String workflowId) {
        return repo.findByWorkflowIdAndStorageDataNotEmpty(workflowId)
                .stream().map(r -> r.getStorageData()).collect(Collectors.toList());
    }
}