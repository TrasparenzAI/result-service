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
import it.cnr.anac.transparency.result.models.AtMap;
import it.cnr.anac.transparency.result.repositories.AtMapRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class AtMapDtoToEntityConverter {

  private final AtMapMapper mapper;
  private final AtMapRepository repo;

  /**
   * Crea una nuova Entity AtMap a partire dai dati del DTO.
   */
  public AtMap createEntity(AtMapCreateDto atMapDto) {
    AtMap atMap = new AtMap();
    mapper.update(atMap, atMapDto);
    return atMap;
  }

  /**
   * Aggiorna la entity riferita dal DTO con i dati passati.
   */
  public AtMap updateEntity(AtMapUpdateDto atMapDto) {
    Verify.verifyNotNull(atMapDto);
    AtMap atMap = repo.findById(atMapDto.getId())
        .orElseThrow(() -> new EntityNotFoundException(
            String.format("ATMap con id = %d non trovata", atMapDto.getId())));
    mapper.update(atMap, atMapDto);
    log.info("id = {}, workflowId = {}", atMapDto.getId(), atMapDto.getWorkflowId());
    return atMap;
  }

}
