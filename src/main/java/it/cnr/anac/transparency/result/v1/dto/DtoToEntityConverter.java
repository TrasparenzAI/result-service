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
package it.cnr.anac.transparency.result.v1.dto;

import org.springframework.stereotype.Component;

import com.google.common.base.Verify;

import it.cnr.anac.transparency.result.models.Result;
import it.cnr.anac.transparency.result.repositories.ResultRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.val;

/**
 * Classe di utilità per convertire un DTO nella corrispondente Entity.
 *
 */
@RequiredArgsConstructor
@Component
public class DtoToEntityConverter {

  private final ResultMapper mapper;
  private final ResultRepository repo;

  /**
   * Crea una nuova Entity Result a partire dai dati del DTO.
   */
  public Result createEntity(ResultCreateDto resultDto) {
    Result result = new Result();
    mapper.update(result, resultDto);
    return result;
  }

  /**
   * Aggiorna l'entity riferita dal DTO con i dati passati.
   */
  public Result updateEntity(ResultUpdateDto resultDto) {
    Verify.verifyNotNull(resultDto);
    val result = repo.findById(resultDto.getId())
        .orElseThrow(() -> new EntityNotFoundException(
            String.format("Result con id = %d non trovato", resultDto.getId())));
    mapper.update(result, resultDto);
    return result;
  }
  
}