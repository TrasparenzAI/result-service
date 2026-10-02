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

import it.cnr.anac.transparency.result.models.AtMap;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * DTO con le informazioni di una ATMap, senza il contenuto del file, per non appesantire
 * le risposte degli endpoint che restituiscono liste.
 */
@ToString
@Data
public class AtMapShowDto {

  private Long id;

  private Long idIpa;

  private String codiceIpa;

  private String workflowId;

  private String url;

  private boolean found;

  private Integer status;

  private AtMap.AtMapFormat format;

  private AtMap.AtMapDiscovery discoveredBy;

  private Boolean valid;

  private String errorMessage;

  private LocalDateTime createdAt;

  private LocalDateTime updatedAt;

}
