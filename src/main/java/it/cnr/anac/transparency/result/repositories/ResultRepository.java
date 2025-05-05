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

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import it.cnr.anac.transparency.result.models.Result;
import jakarta.transaction.Transactional;

public interface ResultRepository extends JpaRepository<Result,Long>, QuerydslPredicateExecutor<Result> {

  @Transactional
  public long deleteByWorkflowId(String workflowId);

  @Query("SELECT r FROM Result r "
      + "WHERE r.workflowId = ?1 AND "
      + "((r.storageData.objectBucket IS NOT NULL AND r.storageData.objectBucket <> '') "
      + "OR (r.storageData.screenshotBucket IS NOT NULL AND r.storageData.screenshotBucket <> ''))")
  public List<Result> findByWorkflowIdAndStorageDataNotEmpty(String workflowId);

}