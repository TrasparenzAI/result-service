/*
 * Copyright (C) 2024 Consiglio Nazionale delle Ricerche
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
import it.cnr.anac.transparency.result.models.QResult;
import it.cnr.anac.transparency.result.models.Result;
import it.cnr.anac.transparency.result.models.ResultCount;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

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
            .groupBy(result.workflowId, result.status)
            .select(Projections.constructor(
                    ResultCount.class,
                    result.workflowId,
                    result.status,
                    result.count()
            ))
            .orderBy(result.workflowId.desc(), result.status.asc())
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
}