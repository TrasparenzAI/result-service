package it.cnr.anac.transparency.result.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import it.cnr.anac.transparency.result.models.Result;
import jakarta.transaction.Transactional;

public interface ResultRepository extends JpaRepository<Result,Long>, QuerydslPredicateExecutor<Result> {

  @Transactional
  public long deleteByWorkflowId(String workflowId);
}