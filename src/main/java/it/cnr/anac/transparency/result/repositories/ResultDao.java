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

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;

import it.cnr.anac.transparency.result.models.QResult;
import it.cnr.anac.transparency.result.models.Result;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;

/**
 * DAO per le ricerche sui risultati di validazione.
 *
 */
@RequiredArgsConstructor
@Component
public class ResultDao {

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

  public Page<Result> findAll(
      Optional<Long> idIpa,
      Optional<String> codiceCategoria, Optional<String> codiceFiscaleEnte,
      Optional<String> codiceIpa, Optional<String> denominazioneEnte, 
      Optional<Boolean> isLeaf,
      Optional<Integer> status, Optional<String> workflowId,
      Optional<LocalDate> createdAfter,
      Pageable pageable) {
    QResult result = QResult.result;
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
    if (workflowId.isPresent()) {
      builder.and(result.workflowId.equalsIgnoreCase(workflowId.get()));
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
    return repo.findAll(builder.getValue(), pageable);
  }

}