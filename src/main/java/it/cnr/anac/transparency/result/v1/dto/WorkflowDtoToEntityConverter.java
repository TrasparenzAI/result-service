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
package it.cnr.anac.transparency.result.v1.dto;

import com.google.common.base.Verify;
import it.cnr.anac.transparency.result.models.Workflow;
import it.cnr.anac.transparency.result.repositories.WorkflowRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Classe di utilità per convertire un DTO relativo ai Workflow nella corrispondente Entity.
 *
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class WorkflowDtoToEntityConverter {

  private final WorkflowMapper mapper;
  private final WorkflowRepository repo;

  /**
   * Crea una nuova Entity Result a partire dai dati del DTO.
   */
  public Workflow createEntity(WorkflowCreateDto workflowDto) {
    Workflow workflow = new Workflow();
    mapper.update(workflow, workflowDto);
    return workflow;
  }

  /**
   * Aggiorna la entity riferita dal DTO con i dati passati.
   */
  public Workflow updateEntity(WorkflowUpdateDto workflowDto) {
    Verify.verifyNotNull(workflowDto);
    Workflow workflow = repo.findById(workflowDto.getId())
        .orElseThrow(() -> new EntityNotFoundException(
            String.format("Workflow con id = %d non trovato", workflowDto.getId())));
    mapper.update(workflow, workflowDto);
    log.info("id = {}, workflowId={}", workflowDto.getId(), workflowDto.getWorkflowId());
    return workflow;
  }
  
}